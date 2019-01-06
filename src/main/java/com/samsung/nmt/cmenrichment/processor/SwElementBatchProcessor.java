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
import com.samsung.nmt.cmenrichment.constants.SwElementConstants;
import com.samsung.nmt.cmenrichment.dto.BatchProcessedData;
import com.samsung.nmt.cmenrichment.dto.HistoryData;
import com.samsung.nmt.cmenrichment.dto.InventoryPropMetadata;
import com.samsung.nmt.cmenrichment.dto.MatricHistory;
import com.samsung.nmt.cmenrichment.dto.MergedData;
import com.samsung.nmt.cmenrichment.dto.PROCDData;
import com.samsung.nmt.cmenrichment.dto.SinglePROCDData;
import com.samsung.nmt.cmenrichment.dto.SwElement;
import com.samsung.nmt.cmenrichment.exceptions.IllegalActionDataException;
import com.samsung.nmt.cmenrichment.qualifiers.SwElementQ;
import com.samsung.nmt.cmenrichment.qualifiers.InventoryPropMetadataQ;
import com.samsung.nmt.cmenrichment.qualifiers.InventoryTypeQ;
import com.samsung.nmt.cmenrichment.qualifiers.NwElementQ;
import com.samsung.nmt.cmenrichment.repo.LocationRepo;
import com.samsung.nmt.cmenrichment.repo.MetadataRepo;
import com.samsung.nmt.cmenrichment.repo.SwElementRepoImpl;
import com.samsung.nmt.cmenrichment.service.JsonMergerService;
import com.samsung.nmt.cmenrichment.utils.IntegerInClauseCreator;
import com.samsung.platform.domain.kafka.Event;
import com.samsung.platform.domain.kafka.Identifier;
import com.samsung.platform.domain.kafka.RequestHeader;

@Component
@SwElementQ
public class SwElementBatchProcessor implements BatchProcessor<SwElement> {

    private static final Logger logger = LoggerFactory.getLogger(SwElementBatchProcessor.class);

    @Autowired
    @InventoryPropMetadataQ
    MetadataRepo<InventoryPropMetadata> inventoryPropRepo;

    @Autowired
    SwElementRepoImpl swElementRepo;

    @Autowired
    @NwElementQ
    MetadataRepo<String> nwElementRepo;

    @Autowired
    JsonMergerService jsonMergerService;

    @Autowired
    @InventoryTypeQ
    private MetadataRepo<String> inventoryTypeRepo;

    @Autowired
    private LocationRepo locationRepo;

    @Override
    public BatchProcessedData<SwElement> process(RequestHeader requestHeader,
            List<Event> events) {

        IntegerInClauseCreator elementIdInClause = new IntegerInClauseCreator();
        events.forEach((event) -> {
            Identifier identifier = event.getIdentifier();
            if (identifier.getName().equals(SwElementConstants.IDENTIFIER)) {
                Integer elementId = nwElementRepo.addIfAbsentAndGetId(identifier.getParent());
                elementIdInClause.add(elementId);
            }
        });

        Map<Integer, SwElement> swIdMap = swElementRepo.getAllByElementID(elementIdInClause.toString());

        List<SwElement> newSwElements = null;

        List<SwElement> updatedSwElements = null;
        List<HistoryData<SwElement>> updatedNwElementsHistoryData = new ArrayList<>();

        List<SwElement> deleteSwElements = null;

        BatchProcessedData<SwElement> batchProcessedData = new BatchProcessedData<>();

        for (Event event : events) {
            Identifier identifier = event.getIdentifier();
            if (identifier.getName().equals(SwElementConstants.IDENTIFIER)) {
                Action action = Action.getAction(identifier.getAction());
                switch (action) {

                case UPDATE: {
                    SinglePROCDData<SwElement> singlePROCDData = update(event, swIdMap);
                    if (batchProcessedData.isHasUpdateData() == false) {
                        updatedSwElements = new ArrayList<>(elementIdInClause.length());
                    }
                    updatedSwElements.add(singlePROCDData.getData());
                    updatedNwElementsHistoryData.add(singlePROCDData.getHistoryData());
                    batchProcessedData.setHasUpdateData(true);
                    break;
                }

                case ADD: {
                    SwElement swElement = add(event);
                    if (batchProcessedData.isHasAddData() == false) {
                        newSwElements = new ArrayList<>(elementIdInClause.length());
                    }
                    newSwElements.add(swElement);
                    batchProcessedData.setHasAddData(true);
                    break;
                }

                case DELETE: {
                    SwElement swElement = delete(event, swIdMap);
                    if (batchProcessedData.isHasDeleteData() == false) {
                        deleteSwElements = new ArrayList<>(elementIdInClause.length());
                    }
                    deleteSwElements.add(swElement);
                    batchProcessedData.setHasDeleteData(true);
                    break;
                }
                }

            }
        }

        PROCDData<SwElement> updatePROCDData = new PROCDData<>(updatedSwElements,
                updatedNwElementsHistoryData);
        batchProcessedData.setUpdateData(updatePROCDData);
        batchProcessedData.setAddData(newSwElements);
        batchProcessedData.setDeleteData(deleteSwElements);

        return batchProcessedData;
    }

    private SwElement add(Event event) {

        SwElement swElement = new SwElement();
        ObjectNode collectorProperties = event.getProperties().getAdditionalFields();
        collectorProperties.fields().forEachRemaining((jsonEntry) -> {
            String key = jsonEntry.getKey();
            inventoryPropRepo.addIfAbsentAndGetId(
                    new InventoryPropMetadata(inventoryTypeRepo.addIfAbsentAndGetId(event.getIdentifier().getName()),
                            key));
        });

        Integer elementId = nwElementRepo.addIfAbsentAndGetId(event.getIdentifier().getParent());

        swElement.setElementId(elementId);
        swElement.setProperties(collectorProperties);
        Calendar currTime = Calendar.getInstance();
        swElement.setCreatedTimeStamp(currTime);
        swElement.setModifiedTimeStamp(currTime);
        swElement.setCircleId(locationRepo.getCircleId(event.getIdentifier().getParent()));

        return swElement;
    }

    private SinglePROCDData<SwElement> update(Event event, Map<Integer, SwElement> seIdMap) {

        Integer elelementId = nwElementRepo.addIfAbsentAndGetId(event.getIdentifier().getParent());

        if (seIdMap == null)
            throw new IllegalActionDataException("Software element is not present in DB");

        SwElement dbSwElement = seIdMap.get(elelementId);

        MergedData mergedData = jsonMergerService.merge(dbSwElement.getProperties(),
                event.getAdditionalObjects());

        SwElement updatedSwElement = new SwElement();

        updatedSwElement.setProperties(mergedData.getMergedJson());
        updatedSwElement.setElementId(dbSwElement.getElementId());

        Calendar currTime = Calendar.getInstance();
        updatedSwElement.setModifiedTimeStamp(currTime);
        dbSwElement.setModifiedTimeStamp(currTime);

        List<String> updatedAttrs = mergedData.getUpdatedAttrs();

        List<MatricHistory> updatedAttrsIds = updatedAttrs.stream().map((updatedAttr) -> {
            int propMetadataId = inventoryPropRepo
                    .addIfAbsentAndGetId(new InventoryPropMetadata(
                            inventoryTypeRepo.addIfAbsentAndGetId(event.getIdentifier().getName()), updatedAttr));
            return new MatricHistory(0, propMetadataId);
        }).collect(Collectors.toList());

        HistoryData<SwElement> historyData = new HistoryData<SwElement>(dbSwElement, updatedAttrsIds);

        SinglePROCDData<SwElement> singlePROCDData = new SinglePROCDData<>(
                updatedSwElement, historyData);

        return singlePROCDData;
    }

    private SwElement delete(Event event, Map<Integer, SwElement> seIdMap) {
        Integer elelementId = nwElementRepo.addIfAbsentAndGetId(event.getIdentifier().getParent());

        if (seIdMap == null)
            throw new IllegalActionDataException("Software element is not present in DB");

        SwElement swEl = seIdMap.get(elelementId);
        swEl.setModifiedTimeStamp(Calendar.getInstance());
        return swEl;
    }

}
