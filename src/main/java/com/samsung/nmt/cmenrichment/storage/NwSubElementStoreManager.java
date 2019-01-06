package com.samsung.nmt.cmenrichment.storage;

import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.samsung.nmt.cmenrichment.client.Action;
import com.samsung.nmt.cmenrichment.dto.BatchProcessedData;
import com.samsung.nmt.cmenrichment.dto.HistoryData;
import com.samsung.nmt.cmenrichment.dto.MatricHistory;
import com.samsung.nmt.cmenrichment.dto.NwSubElement;
import com.samsung.nmt.cmenrichment.dto.PROCDData;
import com.samsung.nmt.cmenrichment.repo.NwSubElementRepo;

@Component
public class NwSubElementStoreManager implements BatchStorageManager<NwSubElement> {

    private static final Logger logger = LoggerFactory.getLogger(NwSubElementStoreManager.class);

    @Autowired
    private NwSubElementRepo nwSubElementRepo;

    @Override
    @Transactional
    public void cudProcessedData(BatchProcessedData<NwSubElement> batchProcessedData) {

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

    private void add(List<NwSubElement> addProcessedData) {
        nwSubElementRepo.addNwElements(addProcessedData);

        if (logger.isDebugEnabled()) {
            logger.debug(log(Action.ADD, addProcessedData));
        }

    }

    private void update(PROCDData<NwSubElement> updateProcessedData) {
        List<NwSubElement> updatedNwSubElements = updateProcessedData.getPropertiesUpdatedData();
        nwSubElementRepo.updateNwElementProperties(updatedNwSubElements);

        updatedNwSubElements.clear();

        List<HistoryData<NwSubElement>> historyData = updateProcessedData.getHistoryData();
        List<Long> historyIds = nwSubElementRepo.addHistoryAndGetIds(historyData);

        List<MatricHistory> matricHistories = new LinkedList<>();
        for (int i = 0; i < historyIds.size(); i++) {
            long historyId = historyIds.get(i);
            HistoryData<NwSubElement> history = historyData.get(i);
            List<MatricHistory> updatedAttributes = history.getUpdatedAttributes();
            for (MatricHistory matricHistory : updatedAttributes) {
                matricHistory.setHistoryId(historyId);
            }
            matricHistories.addAll(updatedAttributes);
        }
        nwSubElementRepo.addHistoryMatric(matricHistories);

        if (logger.isDebugEnabled()) {
            logger.debug(log(Action.UPDATE, updatedNwSubElements));
        }
    }

    private void delete(List<NwSubElement> deleteProcessedData) {
        nwSubElementRepo.deleteNwSubElements(deleteProcessedData);
        nwSubElementRepo.addHistory(deleteProcessedData);

        if (logger.isDebugEnabled()) {
            logger.debug(log(Action.DELETE, deleteProcessedData));
        }
    }

    private String log(Action action, List<NwSubElement> nwSubElements) {
        StringBuilder stringBuilder = new StringBuilder("Action : ");
        stringBuilder.append(action);
        stringBuilder.append(", Msg: nw sublemets stored in db");
        nwSubElements.forEach((nwSubElement) -> {
            stringBuilder.append(", Nw Sub Element :[");
            stringBuilder.append(", ElementId :");
            stringBuilder.append(nwSubElement.getElementId());
            stringBuilder.append(", TypeId :");
            stringBuilder.append(nwSubElement.getTypeId());
            stringBuilder.append(", Name :");
            stringBuilder.append(nwSubElement.getName());
            stringBuilder.append("]");
        });

        return stringBuilder.toString();

    }

}
