package com.samsung.nmt.cmenrichment.processor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.samsung.nmt.cmenrichment.client.Action;
import com.samsung.nmt.cmenrichment.constants.HwElementConstants;
import com.samsung.nmt.cmenrichment.dto.BatchProcessedData;
import com.samsung.nmt.cmenrichment.dto.HistoryData;
import com.samsung.nmt.cmenrichment.dto.HwElement;
import com.samsung.nmt.cmenrichment.dto.InventoryPropMetadata;
import com.samsung.nmt.cmenrichment.dto.MatricHistory;
import com.samsung.nmt.cmenrichment.dto.MergedData;
import com.samsung.nmt.cmenrichment.dto.PROCDData;
import com.samsung.nmt.cmenrichment.dto.SinglePROCDData;
import com.samsung.nmt.cmenrichment.exceptions.IllegalActionDataException;
import com.samsung.nmt.cmenrichment.qualifiers.HwElementQ;
import com.samsung.nmt.cmenrichment.qualifiers.InventoryPropMetadataQ;
import com.samsung.nmt.cmenrichment.qualifiers.InventoryTypeQ;
import com.samsung.nmt.cmenrichment.qualifiers.NwElementQ;
import com.samsung.nmt.cmenrichment.repo.HwElementRepoImpl;
import com.samsung.nmt.cmenrichment.repo.LocationRepo;
import com.samsung.nmt.cmenrichment.repo.MetadataRepo;
import com.samsung.nmt.cmenrichment.service.JsonMergerService;
import com.samsung.nmt.cmenrichment.utils.IntegerInClauseCreator;
import com.samsung.nmt.cmenrichment.utils.StringInClauseCreator;
import com.samsung.platform.domain.kafka.Event;
import com.samsung.platform.domain.kafka.Identifier;
import com.samsung.platform.domain.kafka.RequestHeader;

@Component
@HwElementQ
public class HwElementBatchProcessor implements BatchProcessor<HwElement> {

    @Autowired
    @InventoryPropMetadataQ
    MetadataRepo<InventoryPropMetadata> inventoryPropRepo;

    @Autowired
    HwElementRepoImpl hwElementRepo;

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
    public BatchProcessedData<HwElement> process(RequestHeader requestHeader,
            List<Event> events) {

        BatchProcessedData<HwElement> batchProcessedData = new BatchProcessedData<>();
        IntegerInClauseCreator elementIdInClause = new IntegerInClauseCreator();
        IntegerInClauseCreator unitIdInClause = new IntegerInClauseCreator();
        StringInClauseCreator unitTypeInClause = new StringInClauseCreator();

        events.forEach((event) -> {
            Identifier identifier = event.getIdentifier();
            if (identifier.getName().equals(HwElementConstants.IDENTIFIER)) {
                System.out.println("Element Name - " + identifier.getParent().split("\\|")[0]);
                Integer elementId = nwElementRepo.addIfAbsentAndGetId(identifier.getParent().split("\\|")[0]);
                System.out.println("Element ID - " + elementId);
                System.out.println("Action - " + event.getIdentifier().getAction());
                elementIdInClause.add(elementId);
                unitTypeInClause.add(identifier.getParent().split("\\|")[1]);
                unitIdInClause.add(Integer.parseInt(identifier.getParent().split("\\|")[2]));
            }
        });

        if (elementIdInClause.length() > 0) {

            Map<String, HwElement> hwIdMap = hwElementRepo.getAllData(elementIdInClause.toString(),
                    unitTypeInClause.toString(), unitIdInClause.toString());

            List<HwElement> newHwElements = null;

            List<HwElement> updatedHwElements = null;
            List<HistoryData<HwElement>> updatedHwElementsHistoryData = new ArrayList<>();

            List<HwElement> deleteHwElements = null;

            for (Event event : events) {
                Identifier identifier = event.getIdentifier();
                Action action = Action.getAction(identifier.getAction());
                switch (action) {

                case UPDATE: {
                    SinglePROCDData<HwElement> singlePROCDData = update(event, hwIdMap);
                    if (batchProcessedData.isHasUpdateData() == false) {
                        updatedHwElements = new ArrayList<>(elementIdInClause.length());
                    }
                    updatedHwElements.add(singlePROCDData.getData());
                    updatedHwElementsHistoryData.add(singlePROCDData.getHistoryData());
                    batchProcessedData.setHasUpdateData(true);
                    break;
                }

                case ADD: {
                    HwElement hwElement = add(event);
                    if (batchProcessedData.isHasAddData() == false) {
                        newHwElements = new ArrayList<>(elementIdInClause.length());
                    }
                    newHwElements.add(hwElement);
                    batchProcessedData.setHasAddData(true);
                    break;
                }

                case DELETE: {
                    HwElement hwElement = delete(event, hwIdMap);
                    if (batchProcessedData.isHasDeleteData() == false) {
                        deleteHwElements = new ArrayList<>(elementIdInClause.length());
                    }
                    deleteHwElements.add(hwElement);
                    batchProcessedData.setHasDeleteData(true);
                    break;
                }

                }
            }
            ;

            PROCDData<HwElement> updatePROCDData = new PROCDData<>(updatedHwElements,
                    updatedHwElementsHistoryData);
            batchProcessedData.setUpdateData(updatePROCDData);
            batchProcessedData.setAddData(newHwElements);
            batchProcessedData.setDeleteData(deleteHwElements);
        }
        return batchProcessedData;
    }

    private HwElement add(Event event) {
        HwElement hwElement = new HwElement();
        ObjectNode collectorProperties = event.getProperties().getAdditionalFields();
        collectorProperties.fields().forEachRemaining((jsonEntry) -> {
            String key = jsonEntry.getKey();
            inventoryPropRepo.addIfAbsentAndGetId(
                    new InventoryPropMetadata(inventoryTypeRepo.addIfAbsentAndGetId(event.getIdentifier().getName()),
                            key));
        });

        Integer elementId = nwElementRepo.addIfAbsentAndGetId(event.getIdentifier().getParent().split("\\|")[0]);

        hwElement.setElementId(elementId);
        hwElement.setUnitId(collectorProperties.get(HwElementConstants.UNIT_ID.toUpperCase()).asInt());
        hwElement.setUnitSide(collectorProperties.get(HwElementConstants.UNIT_SIDE.toUpperCase()).asText());
        hwElement.setUnitType(collectorProperties.get(HwElementConstants.UNIT_TYPE.toUpperCase()).asText());
        hwElement.setProperties(collectorProperties);
        Calendar currTime = Calendar.getInstance();
        hwElement.setCreatedTimeStamp(currTime);
        hwElement.setModifiedTimeStamp(currTime);
        hwElement.setCircleId(locationRepo.getCircleId(event.getIdentifier().getParent().split("\\|")[0]));
        return hwElement;
    }

    private SinglePROCDData<HwElement> update(Event event, Map<String, HwElement> heIdMap) {

        Integer elelementId = nwElementRepo.addIfAbsentAndGetId(event.getIdentifier().getParent().split("\\|")[0]);

        if (heIdMap == null)
            throw new IllegalActionDataException("Hardware element is not present in DB");

        HwElement dbHwElement = heIdMap.get(elelementId + "|" + event.getIdentifier().getParent().split("\\|")[1] + "|"
                + event.getIdentifier().getParent().split("\\|")[2]);

        MergedData mergedData = jsonMergerService.merge(dbHwElement.getProperties(), event.getAdditionalObjects());

        HwElement updatedHwElement = new HwElement();

        updatedHwElement.setProperties(mergedData.getMergedJson());
        updatedHwElement.setElementId(dbHwElement.getElementId());
        updatedHwElement.setUnitId(dbHwElement.getUnitId());
        updatedHwElement.setUnitType(dbHwElement.getUnitType());
        Calendar currTime = Calendar.getInstance();
        updatedHwElement.setModifiedTimeStamp(currTime);
        dbHwElement.setModifiedTimeStamp(currTime);

        List<String> updatedAttrs = mergedData.getUpdatedAttrs();
        List<MatricHistory> updatedAttrsIds = updatedAttrs.stream().map((updatedAttr) -> {
            int propMetadataId = inventoryPropRepo.addIfAbsentAndGetId(new InventoryPropMetadata(
                    inventoryTypeRepo.addIfAbsentAndGetId(event.getIdentifier().getName()), updatedAttr));
            return new MatricHistory(0, propMetadataId);
        }).collect(Collectors.toList());

        HistoryData<HwElement> historyData = new HistoryData<HwElement>(dbHwElement, updatedAttrsIds);

        SinglePROCDData<HwElement> singlePROCDData = new SinglePROCDData<>(
                updatedHwElement, historyData);

        return singlePROCDData;
    }

    private HwElement delete(Event event, Map<String, HwElement> heIdMap) {
        Integer elelementId = nwElementRepo.addIfAbsentAndGetId(event.getIdentifier().getParent().split("\\|")[0]);

        if (heIdMap == null)
            throw new IllegalActionDataException("Hardware element is not present in DB");

        HwElement swEl = heIdMap.get(elelementId + "|" + event.getIdentifier().getParent().split("\\|")[1] + "|"
                + event.getIdentifier().getParent().split("\\|")[2]);
        swEl.setElementId(elelementId);
        swEl.setModifiedTimeStamp(Calendar.getInstance());
        return swEl;
    }

}
