package com.samsung.nmt.cmenrichment.storage;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.samsung.nmt.cmenrichment.dto.BatchProcessedData;
import com.samsung.nmt.cmenrichment.dto.HistoryData;
import com.samsung.nmt.cmenrichment.dto.HwElement;
import com.samsung.nmt.cmenrichment.dto.MatricHistory;
import com.samsung.nmt.cmenrichment.dto.PROCDData;
import com.samsung.nmt.cmenrichment.qualifiers.HwElementQ;
import com.samsung.nmt.cmenrichment.repo.HwElementRepoImpl;

@Component
@HwElementQ
public class HwElementStoreManager implements BatchStorageManager<HwElement> {

    @Autowired
    HwElementRepoImpl hwElementRepo;

    @Override
    public void cudProcessedData(BatchProcessedData<HwElement> batchProcessedData) {

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

    private void add(List<HwElement> addProcessedData) {
        try {
            hwElementRepo.addHwElementsInBatch(addProcessedData);
        } catch (DuplicateKeyException e) {

        }
    }

    private void update(PROCDData<HwElement> updateProcessedData) {
        List<HwElement> updatedNwSubElements = updateProcessedData.getPropertiesUpdatedData();
        hwElementRepo.updateAllInBatch(updatedNwSubElements);

        List<HistoryData<HwElement>> historyData = updateProcessedData.getHistoryData();
        List<Long> historyIds = hwElementRepo.addHistoryAndGetIdsInBatch(historyData);

        List<MatricHistory> matricHistories = new LinkedList<>();
        for (int i = 0; i < historyIds.size(); i++) {
            long historyId = historyIds.get(i);
            HistoryData<HwElement> history = historyData.get(i);
            List<MatricHistory> updatedAttributes = history.getUpdatedAttributes();
            for (MatricHistory matricHistory : updatedAttributes) {
                matricHistory.setHistoryId(historyId);
            }
            matricHistories.addAll(updatedAttributes);
        }
        hwElementRepo.addHistoryMatricInBatch(matricHistories);
    }

    private void delete(List<HwElement> deleteProcessedData) {
        hwElementRepo.deleteHwElementsInBatch(deleteProcessedData);
        hwElementRepo.addHistoryInBatch(deleteProcessedData);
    }

}
