package com.samsung.nmt.cmenrichment.processor;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.samsung.nmt.cmenrichment.dto.NwPropMetadata;
import com.samsung.nmt.cmenrichment.dto.NwSubElKey;
import com.samsung.nmt.cmenrichment.dto.NwSubElement;
import com.samsung.nmt.cmenrichment.dto.NwSubElementRawData;
import com.samsung.nmt.cmenrichment.dto.SinglePROCDData;
import com.samsung.nmt.cmenrichment.exceptions.IllegalActionDataException;
import com.samsung.nmt.cmenrichment.exceptions.LocationNotFound;
import com.samsung.nmt.cmenrichment.qualifiers.NwElementQ;
import com.samsung.nmt.cmenrichment.qualifiers.NwElementTypeQ;
import com.samsung.nmt.cmenrichment.qualifiers.NwSubElementPropMetadataQ;
import com.samsung.nmt.cmenrichment.repo.LocationRepo;
import com.samsung.nmt.cmenrichment.repo.MetadataRepo;
import com.samsung.nmt.cmenrichment.repo.NwSubElementRepo;
import com.samsung.nmt.cmenrichment.repo.NwSubElementTypeRepo;
import com.samsung.nmt.cmenrichment.service.JsonMergerService;
import com.samsung.nmt.cmenrichment.utils.DateTimeUtil;
import com.samsung.nmt.cmenrichment.utils.IntegerInClauseCreator;
import com.samsung.nmt.cmenrichment.utils.StringInClauseCreator;
import com.samsung.platform.domain.kafka.Event;
import com.samsung.platform.domain.kafka.Identifier;
import com.samsung.platform.domain.kafka.RequestHeader;

@Component
public class NwSubElementProcessor implements BatchProcessor<NwSubElement> {

    private static final Logger logger = LoggerFactory.getLogger(NwSubElementProcessor.class);

    @Autowired
    @NwElementQ
    private MetadataRepo<String> nwElementRepo;

    @Autowired
    @NwElementTypeQ
    private MetadataRepo<String> nwElementTypeRepo;

    @Autowired
    private NwSubElementTypeRepo nwSubElementTypeRepo;

    @Autowired
    private NwSubElementRepo nwSubElementRepo;

    @Autowired
    @NwSubElementPropMetadataQ
    private MetadataRepo<NwPropMetadata> nwSubElementPropRepo;

    @Autowired
    private LocationRepo locationRepo;

    @Autowired
    private JsonMergerService jsonMergerService;

    @Autowired
    private AppProperties appProperties;

    @Override
    public BatchProcessedData<NwSubElement> process(RequestHeader requestHeader,
            List<Event> events) {

        final List<NwSubElementRawData> nwSubElementRawDataBatch = new ArrayList<>();

        Map<NwSubElKey, NwSubElement> dbDataMap = getDBData(events, nwSubElementRawDataBatch);

        BatchProcessDataBuilder<NwSubElement> batchProcessDataBuilder = new BatchProcessDataBuilder<>();

        for (NwSubElementRawData nwSubElementRawData : nwSubElementRawDataBatch) {

            Action action = nwSubElementRawData.getAction();

            switch (action) {
            case UPDATE: {
                SinglePROCDData<NwSubElement> singlePROCDData = update(nwSubElementRawData,
                        dbDataMap);
                batchProcessDataBuilder.addToUpdateActionData(singlePROCDData);
                break;
            }

            case ADD: {
                NwSubElement nwSubElement = add(nwSubElementRawData, dbDataMap);
                batchProcessDataBuilder.addToAddActionData(nwSubElement);
                break;
            }

            case DELETE: {
                NwSubElement nwSubElement = delete(nwSubElementRawData, dbDataMap);
                batchProcessDataBuilder.addToDeleteActionData(nwSubElement);
                break;
            }

            }
        }

        logger.info("Nw Sub Elements Processed : " + batchProcessDataBuilder.countToString());
        return batchProcessDataBuilder.build();
    }

    private Map<NwSubElKey, NwSubElement> getDBData(List<Event> events,
            List<NwSubElementRawData> nwSubElementRawDataBatch) {
        IntegerInClauseCreator elementIdsInClauseCreator = new IntegerInClauseCreator();
        IntegerInClauseCreator typeIdsInClauseCreator = new IntegerInClauseCreator();
        StringInClauseCreator identifierNamesInClauseCreator = new StringInClauseCreator(
                StringInClauseCreator.ALLOW_DUPLICATE);
        IntegerInClauseCreator locationIdsInClauseCreator = new IntegerInClauseCreator();
        Calendar currTime = DateTimeUtil.getInstance().currTime();

        events.forEach((event) -> {
            Identifier identifier = event.getIdentifier();

            //check whether event belonged to network sub element or not
            String functionName = identifier.getName();
            if (appProperties.getNwElementIdentifier().equals(functionName) == false) {

                String elementName = identifier.getParent();
                String functionIdentifier = identifier.getValue();

                identifierNamesInClauseCreator.add(functionIdentifier);

                Integer elementId = nwElementRepo.addIfAbsentAndGetId(elementName);
                elementIdsInClauseCreator.add(elementId);

                //get type id of network sub element function name
                Integer nwElementTypeId = nwElementTypeRepo.addIfAbsentAndGetId(appProperties.getNwElementTypeName());
                Integer nwSubElementTypeId = nwSubElementTypeRepo.addIfAbsentAndGetId(functionName,
                        nwElementTypeId);
                typeIdsInClauseCreator.add(nwSubElementTypeId);

                Integer locationId = locationRepo.getLocationId(elementName);
                if (locationId == null) {
                    throw new LocationNotFound(
                            "Location id not found for element : " + elementName);
                }
                locationIdsInClauseCreator.add(locationId);

                ObjectNode collectorProperties = null;
                if (event.getProperties() != null) {
                    collectorProperties = event.getProperties().getAdditionalFields();
                }

                Action action = Action.getAction(identifier.getAction());

                //create raw data object
                NwSubElementRawData nwSubElementRawData = new NwSubElementRawData(elementId, nwSubElementTypeId,
                        collectorProperties,
                        action, functionIdentifier,
                        elementName, locationId, event.getAdditionalObjects(), currTime);
                nwSubElementRawDataBatch.add(nwSubElementRawData);
            }
        });

        //get map of NwSubElKey and NwSubElement
        Map<NwSubElKey, NwSubElement> dbDataMap = null;

        if (elementIdsInClauseCreator.length() > 0) {

            dbDataMap = nwSubElementRepo
                    .getBytypeIdAndElementIdAndName(elementIdsInClauseCreator.toString(),
                            typeIdsInClauseCreator.toString(), identifierNamesInClauseCreator.toString(),
                            locationIdsInClauseCreator.toString());
        }

        return dbDataMap;
    }

    public NwSubElement add(NwSubElementRawData nwSubElementRawData,
            Map<NwSubElKey, NwSubElement> dbDataMap) {

        if (dbDataMap != null && dbDataMap.containsKey(nwSubElementRawData.getNwSubElKey())) {
            throw new IllegalActionDataException(
                    "Network sub element is already present in DB for nw sub Element : "
                            + nwSubElementRawData.getNwSubElKey());
        } else {

            nwSubElementRawData.getProperties().fields().forEachRemaining((jsonEntry) -> {
                String key = jsonEntry.getKey();
                nwSubElementPropRepo.addIfAbsentAndGetId(
                        new NwPropMetadata(nwSubElementRawData.getNwSubElKey().getSubElTypeId(), key));
            });

            NwSubElement nwSubElement = new NwSubElement();
            nwSubElement.setElementId(nwSubElementRawData.getNwSubElKey().getElementId());
            nwSubElement.setTypeId(nwSubElementRawData.getNwSubElKey().getSubElTypeId());

            nwSubElement.setProperties(nwSubElementRawData.getProperties());
            nwSubElement.setCreatedTimeStamp(nwSubElementRawData.getCurrTime());
            nwSubElement.setLastModifiedTimeStamp(nwSubElementRawData.getCurrTime());
            nwSubElement.setName(nwSubElementRawData.getNwSubElKey().getName());
            nwSubElement.setLocationId(nwSubElementRawData.getLocationId());

            if (logger.isDebugEnabled()) {
                logger.debug(log(Action.ADD, nwSubElement));
            }

            return nwSubElement;
        }
    }

    public SinglePROCDData<NwSubElement> update(NwSubElementRawData nwSubElementRawData,
            Map<NwSubElKey, NwSubElement> dbDataMap) {

        NwSubElement dbData = dbDataMap.get(nwSubElementRawData.getNwSubElKey());

        if (dbData == null) {
            throw new IllegalActionDataException(
                    "Network sub element is not present in DB for nw sub Element : "
                            + nwSubElementRawData.getNwSubElKey());
        }

        MergedData mergedData = jsonMergerService.merge(dbData.getProperties(), nwSubElementRawData.getEvents());

        List<MatricHistory> updatedAttrsIds = mergedData.getUpdatedAttrs().stream().map((updatedAttr) -> {
            int propMetadataId = nwSubElementPropRepo
                    .addIfAbsentAndGetId(
                            new NwPropMetadata(nwSubElementRawData.getNwSubElKey().getSubElTypeId(), updatedAttr));
            return new MatricHistory(0, propMetadataId);
        }).collect(Collectors.toList());

        NwSubElement nwSubElement = new NwSubElement(nwSubElementRawData.getNwSubElKey().getElementId(),
                nwSubElementRawData.getNwSubElKey().getSubElTypeId(), mergedData.getMergedJson());
        nwSubElement.setSubElementId(dbData.getSubElementId());
        nwSubElement.setLastModifiedTimeStamp(nwSubElementRawData.getCurrTime());
        dbData.setLastModifiedTimeStamp(nwSubElementRawData.getCurrTime());

        HistoryData<NwSubElement> historyData = new HistoryData<NwSubElement>(dbData, updatedAttrsIds);
        SinglePROCDData<NwSubElement> singlePROCDData = new SinglePROCDData<>(
                nwSubElement, historyData);

        if (logger.isDebugEnabled()) {
            logger.debug(log(Action.UPDATE, nwSubElement));
        }

        return singlePROCDData;

    }

    public NwSubElement delete(NwSubElementRawData nwSubElementRawData,
            Map<NwSubElKey, NwSubElement> dbDataMap) {

        NwSubElement dbData = dbDataMap.get(nwSubElementRawData.getNwSubElKey());
        if (dbData == null) {
            throw new IllegalActionDataException(
                    "data not found in db, can not process delete action : " + nwSubElementRawData.getNwSubElKey());
        }
        dbData.setLastModifiedTimeStamp(nwSubElementRawData.getCurrTime());

        if (logger.isDebugEnabled()) {
            logger.debug(log(Action.UPDATE, dbData));
        }

        return dbData;

    }

    private String log(Action action, NwSubElement nwSubElement) {
        StringBuilder stringBuilder = new StringBuilder("Action : ");
        stringBuilder.append(action);
        stringBuilder.append(", Msg: metadata/json processed");
        stringBuilder.append(", Nw Sub Element :[");
        stringBuilder.append(", ElementId :");
        stringBuilder.append(nwSubElement.getElementId());
        stringBuilder.append(", TypeId :");
        stringBuilder.append(nwSubElement.getTypeId());
        stringBuilder.append(", Name :");
        stringBuilder.append(nwSubElement.getName());
        stringBuilder.append("]");
        return stringBuilder.toString();

    }

    /*private String extractFunctionName(String identName) {
        if (identName.indexOf(Constants.NW_SUBELEMENT_IDENTIFIER_SEPARATOR, 2) > 0) {
            return identName.substring(1, identName.indexOf(Constants.NW_SUBELEMENT_IDENTIFIER_SEPARATOR, 2));
        } else {
            return identName.substring(1, identName.length());
        }
    }*/

}
