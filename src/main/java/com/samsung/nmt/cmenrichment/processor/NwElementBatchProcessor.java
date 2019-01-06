package com.samsung.nmt.cmenrichment.processor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.samsung.nmt.cmenrichment.client.Action;
import com.samsung.nmt.cmenrichment.constants.AppProperties;
import com.samsung.nmt.cmenrichment.dto.BatchProcessedData;
import com.samsung.nmt.cmenrichment.dto.HistoryData;
import com.samsung.nmt.cmenrichment.dto.MatricHistory;
import com.samsung.nmt.cmenrichment.dto.MergedData;
import com.samsung.nmt.cmenrichment.dto.NwEMS;
import com.samsung.nmt.cmenrichment.dto.NwElement;
import com.samsung.nmt.cmenrichment.dto.NwPropMetadata;
import com.samsung.nmt.cmenrichment.dto.SinglePROCDData;
import com.samsung.nmt.cmenrichment.exceptions.EMSDetailsNotFound;
import com.samsung.nmt.cmenrichment.exceptions.IllegalActionDataException;
import com.samsung.nmt.cmenrichment.qualifiers.NwEMSQ;
import com.samsung.nmt.cmenrichment.qualifiers.NwElementPropMetadataQ;
import com.samsung.nmt.cmenrichment.repo.MetadataRepo;
import com.samsung.nmt.cmenrichment.repo.NwEMSRepo;
import com.samsung.nmt.cmenrichment.repo.NwElementRepo;
import com.samsung.nmt.cmenrichment.service.JsonMergerService;
import com.samsung.nmt.cmenrichment.service.NwElementService;
import com.samsung.nmt.cmenrichment.utils.DateTimeUtil;
import com.samsung.nmt.cmenrichment.utils.StringInClauseCreator;
import com.samsung.platform.domain.kafka.Event;
import com.samsung.platform.domain.kafka.Identifier;
import com.samsung.platform.domain.kafka.RequestHeader;

@Component
public class NwElementBatchProcessor implements BatchProcessor<NwElement> {

    private static final Logger logger = LoggerFactory.getLogger(NwElementBatchProcessor.class);

    @Autowired
    @NwElementPropMetadataQ
    private MetadataRepo<NwPropMetadata> nwElementPropRepo;

    @Autowired
    private NwElementRepo nwElementRepo;

    @Autowired
    private JsonMergerService jsonMergerService;

    @Autowired
    private NwElementService nwElementService;

    @Autowired
    @NwEMSQ
    private NwEMSRepo<String> nwEMSRepo;

    @Autowired
    private AppProperties appProperties;

    @Override
    public BatchProcessedData<NwElement> process(RequestHeader requestHeader,
            List<Event> events) {

        //fetch element names and prepare in clause
        StringInClauseCreator neNamesInClause = new StringInClauseCreator();
        events.forEach((event) -> {
            Identifier identifier = event.getIdentifier();
            if (identifier.getName().equals(appProperties.getNwElementIdentifier())) {
                neNamesInClause.add(identifier.getValue());
            }
        });

        BatchProcessDataBuilder<NwElement> batchProcessDataBuilder = new BatchProcessDataBuilder<>(
                neNamesInClause.length());

        //check if events has any nw element data
        if (neNamesInClause.length() > 0) {
            //get map of element name and nwElement object from database using in clause
            Map<String, NwElement> elementNameMap = null;
            elementNameMap = nwElementRepo.getAllByNames(neNamesInClause.toString());

            Integer emsId = nwEMSRepo.getId(requestHeader.getEmsID());
            if (emsId == null) {
                throw new EMSDetailsNotFound("Ems Id null for ems : " + requestHeader.getEmsID());
            }
            NwEMS nwEMS = new NwEMS(emsId, requestHeader.getEmsID());
            nwEMSRepo.updateStatusToActive(emsId);

            for (Event event : events) {
                Identifier identifier = event.getIdentifier();
                if (identifier.getName().equals(appProperties.getNwElementIdentifier())) {
                    Action action = Action.getAction(identifier.getAction());
                    switch (action) {

                    case UPDATE: {
                        SinglePROCDData<NwElement> singlePROCDData = update(event,
                                elementNameMap);
                        batchProcessDataBuilder.addToUpdateActionData(singlePROCDData);
                        break;
                    }

                    case ADD: {
                        NwElement nwElement = add(event, nwEMS, elementNameMap);
                        batchProcessDataBuilder.addToAddActionData(nwElement);
                        break;
                    }

                    case DELETE: {
                        NwElement nwElement = delete(event, elementNameMap);
                        batchProcessDataBuilder.addToDeleteActionData(nwElement);
                        break;
                    }

                    }
                }
            }

        }
        logger.info("Nw Elements Processed : " + batchProcessDataBuilder.countToString());
        return batchProcessDataBuilder.build();
    }

    private NwElement add(Event event, NwEMS nwEMS, Map<String, NwElement> neNameMap) {

        //check nw element present in db or not
        NwElement dbNwElement = neNameMap.get(event.getIdentifier().getValue());
        if (dbNwElement != null && dbNwElement.getProperties() != null) {
            throw new IllegalActionDataException(
                    "Network element is already present in DB for nw element name : "
                            + event.getIdentifier().getValue());
        }

        ObjectNode collectorProperties = event.getProperties().getAdditionalFields();
        NwElement nwElement = nwElementService.createNwElementFromCollectorAddData(event, collectorProperties, nwEMS);

        //add property metadata id in db
        collectorProperties.fields().forEachRemaining((jsonEntry) -> {
            String key = jsonEntry.getKey();
            nwElementPropRepo.addIfAbsentAndGetId(new NwPropMetadata(nwElement.getTypeId(), key));
        });

        if (logger.isDebugEnabled()) {
            logger.debug(log(Action.ADD, nwElement));
        }

        return nwElement;
    }

    private SinglePROCDData<NwElement> update(Event event,
            Map<String, NwElement> neNameMap) {

        //get element from db map
        NwElement dbNwElement = neNameMap.get(event.getIdentifier().getValue());
        if (dbNwElement == null) {
            throw new IllegalActionDataException("Network element is not present in db for nw element name: "
                    + event.getIdentifier().getValue());
        }

        //merge db properties and collector properties
        MergedData mergedData = jsonMergerService.merge(dbNwElement.getProperties(),
                appProperties.getNwElementColumnAttrs(),
                appProperties.getNwelementJcbKeys(), event.getAdditionalObjects());

        NwElement updatedNwElement = nwElementService.createNwElementFromCollectorUpdateData(mergedData, dbNwElement);

        //set curr time in last modified timestamp in db nw element for history
        dbNwElement.setLastModifiedTimeStamp(updatedNwElement.getLastModifiedTimeStamp());

        Integer typeId = dbNwElement.getTypeId();

        //create metadata history matric data
        List<MatricHistory> updatedAttrsIds = mergedData.getUpdatedAttrs().stream().map((updatedAttr) -> {

            //get prop metadata id from db(here newly added attributes will get inserted otherwise fetched from cache)
            int propMetadataId = nwElementPropRepo.addIfAbsentAndGetId(new NwPropMetadata(typeId, updatedAttr));
            return new MatricHistory(0, propMetadataId);

        }).collect(Collectors.toList());

        //create history object
        HistoryData<NwElement> historyData = new HistoryData<NwElement>(dbNwElement, updatedAttrsIds);

        //create complete processed data
        SinglePROCDData<NwElement> singlePROCDData = new SinglePROCDData<>(
                updatedNwElement, historyData, mergedData.isOnlyPropUpdated());

        if (logger.isDebugEnabled()) {
            logger.debug(log(Action.UPDATE, updatedNwElement));
        }

        return singlePROCDData;
    }

    private NwElement delete(Event event, Map<String, NwElement> neNameMap) {
        //get data from db
        NwElement dbData = neNameMap.get(event.getIdentifier().getValue());

        if (dbData == null) {
            throw new IllegalActionDataException(
                    "data not found in db, can not process delete action : " + event.getIdentifier().getValue());
        }
        //set current time in last modified timestamp in db nw element for history
        dbData.setLastModifiedTimeStamp(DateTimeUtil.getInstance().currTime());

        if (logger.isDebugEnabled()) {
            logger.debug(log(Action.DELETE, dbData));
        }

        return dbData;

    }

    private String log(Action action, NwElement nwElement) {
        StringBuilder stringBuilder = new StringBuilder("Action : ");
        stringBuilder.append(action);
        stringBuilder.append(", Msg: metadata/json processed");
        stringBuilder.append(", Nw Element :[");
        stringBuilder.append(", ElementId :");
        stringBuilder.append(nwElement.getElementId());
        stringBuilder.append(", Name :");
        stringBuilder.append(nwElement.getName());
        stringBuilder.append("]");
        return stringBuilder.toString();

    }

}
