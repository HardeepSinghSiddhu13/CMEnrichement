package com.samsung.nmt.cmenrichment.storage;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.samsung.nmt.cmenrichment.dto.BatchProcessedData;
import com.samsung.nmt.cmenrichment.dto.HistoryData;
import com.samsung.nmt.cmenrichment.dto.MatricHistory;
import com.samsung.nmt.cmenrichment.dto.PROCDData;
import com.samsung.nmt.cmenrichment.dto.SwElement;
import com.samsung.nmt.cmenrichment.qualifiers.SwElementQ;
import com.samsung.nmt.cmenrichment.repo.NwElementRepoImpl;
import com.samsung.nmt.cmenrichment.repo.SwElementRepoImpl;

@Component
@SwElementQ
public class SwElementStoreManager implements BatchStorageManager<SwElement> {

    @Autowired
    SwElementRepoImpl swElementRepo;

    @Autowired
    NwElementRepoImpl nwElementRepo;

    @Override
    public void cudProcessedData(BatchProcessedData<SwElement> batchProcessedData) {

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

    private void add(List<SwElement> addProcessedData) {

        swElementRepo.addSwElementsInBatch(addProcessedData);

    }

    private void update(PROCDData<SwElement> updateProcessedData) {

        List<SwElement> updatedNwSubElements = updateProcessedData.getPropertiesUpdatedData();
        swElementRepo.updateAllInBatch(updatedNwSubElements);

        List<HistoryData<SwElement>> historyData = updateProcessedData.getHistoryData();
        List<Long> historyIds = swElementRepo.addHistoryAndGetIdsInBatch(historyData);

        List<MatricHistory> matricHistories = new LinkedList<>();
        for (int i = 0; i < historyIds.size(); i++) {
            long historyId = historyIds.get(i);
            HistoryData<SwElement> history = historyData.get(i);
            List<MatricHistory> updatedAttributes = history.getUpdatedAttributes();
            for (MatricHistory matricHistory : updatedAttributes) {
                matricHistory.setHistoryId(historyId);
            }
            matricHistories.addAll(updatedAttributes);
        }
        swElementRepo.addHistoryMatricInBatch(matricHistories);
    }

    private void delete(List<SwElement> deleteProcessedData) {
        swElementRepo.deleteSwElementsInBatch(deleteProcessedData);
        swElementRepo.addHistoryInBatch(deleteProcessedData);
    }

}
