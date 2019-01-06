package com.samsung.nmt.cmenrichment.storage;

import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.samsung.nmt.cmenrichment.client.Action;
import com.samsung.nmt.cmenrichment.dto.BatchProcessedData;
import com.samsung.nmt.cmenrichment.dto.HistoryData;
import com.samsung.nmt.cmenrichment.dto.MatricHistory;
import com.samsung.nmt.cmenrichment.dto.NwElement;
import com.samsung.nmt.cmenrichment.dto.PROCDData;
import com.samsung.nmt.cmenrichment.repo.NwElementRepo;

@Component
public class NwElementBatchStoreManager implements BatchStorageManager<NwElement> {

    private static Logger logger = LoggerFactory.getLogger(NwElementBatchStoreManager.class);

    @Autowired
    private NwElementRepo nwElementRepo;

    @Override
    @Transactional
    public void cudProcessedData(BatchProcessedData<NwElement> batchProcessedData) {

        if (batchProcessedData.isHasAddData()) {
            add(batchProcessedData.getAddData());
        }

        if (batchProcessedData.isHasUpdateData()) {
            update(batchProcessedData.getUpdateData());
        }

        if (batchProcessedData.isHasDeleteData()) {
            delete(batchProcessedData.getDeleteData());
        }
    }

    @Transactional
    private void add(List<NwElement> addProcessedData) {

        for (NwElement nwElement : addProcessedData) {
            Integer nwElementId = null;
            try {
                //add element in db
                nwElementId = nwElementRepo.addAndGetId(nwElement);
                if (logger.isDebugEnabled()) {
                    logger.debug(log(Action.ADD, nwElement, "Stored successfully using insert"));
                }
            } catch (DuplicateKeyException e) {
                /*if entry is already found then update all
                 * other property except name using element id.
                 *
                 * this can happen if element name is already inserted in sw, hw or fw processing
                */
                nwElementId = nwElementRepo.getIdFromCache(nwElement.getName());
                nwElement.setElementId(nwElementId);
                nwElementRepo.updateAllExceptNameUsingId(nwElement);

                if (logger.isDebugEnabled()) {
                    logger.debug(log(Action.ADD, nwElement, "Stored successfully using update"));
                }
            }
        }
    }

    private void update(PROCDData<NwElement> updateProcessedData) {
        //update properties
        nwElementRepo.updatePropertiesInBatch(updateProcessedData.getPropertiesUpdatedData());

        //check if updated data is present(where all columns are updated)
        if (updateProcessedData.getAllColumnsUpdatedData().isPresent()) {
            nwElementRepo.updateInBatch(updateProcessedData.getAllColumnsUpdatedData().get());
        }

        List<HistoryData<NwElement>> historyData = updateProcessedData.getHistoryData();
        //add history data in nw element history table and auto incremented key list
        List<Long> historyIds = nwElementRepo.addHistoryAndGetIdsInBatch(historyData);

        List<MatricHistory> matricHistories = new LinkedList<>();

        //iterate history ids
        for (int i = 0; i < historyIds.size(); i++) {
            long historyId = historyIds.get(i);

            //get history data using index
            HistoryData<NwElement> history = historyData.get(i);
            List<MatricHistory> updatedAttributes = history.getUpdatedAttributes();
            for (MatricHistory matricHistory : updatedAttributes) {
                //set history id for matric data
                matricHistory.setHistoryId(historyId);
            }
            //ombine all updated matric data
            matricHistories.addAll(updatedAttributes);
        }

        //add matric data in batch
        nwElementRepo.addHistoryMatricInBatch(matricHistories);
        if (logger.isDebugEnabled()) {
            logger.debug(log(Action.UPDATE, updateProcessedData.getPropertiesUpdatedData()));
        }
    }

    private void delete(List<NwElement> deleteProcessedData) {
        //delete nw elements in batch
        nwElementRepo.deleteNwElementsInBatch(deleteProcessedData);

        //add deleted history data
        nwElementRepo.addHistoryInBatch(deleteProcessedData);

        if (logger.isDebugEnabled()) {
            logger.info(log(Action.DELETE, deleteProcessedData));
        }
    }

    private String log(Action action, NwElement nwElement, String msg) {
        StringBuilder stringBuilder = new StringBuilder("Action : ");
        stringBuilder.append(action);
        stringBuilder.append(", Msg: nw sublemets stored in db");
        stringBuilder.append(msg);
        stringBuilder.append(", Nw Element :[");
        stringBuilder.append(", ElementId :");
        stringBuilder.append(nwElement.getElementId());
        stringBuilder.append(", Name :");
        stringBuilder.append(nwElement.getName());
        stringBuilder.append("]");
        return stringBuilder.toString();

    }

    private String log(Action action, List<NwElement> nwElements) {
        StringBuilder stringBuilder = new StringBuilder("Action : ");
        stringBuilder.append(action);
        stringBuilder.append(", Msg: nw stored in db");
        nwElements.forEach((nwElement) -> {
            stringBuilder.append("Nw Element :[");
            stringBuilder.append(", ElementId :");
            stringBuilder.append(nwElement.getElementId());
            stringBuilder.append(", Name :");
            stringBuilder.append(nwElement.getName());
            stringBuilder.append("], ");
        });

        return stringBuilder.toString();

    }

}
