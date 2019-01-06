package com.samsung.nmt.cmenrichment.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.samsung.nmt.cmenrichment.dto.BatchProcessedData;
import com.samsung.nmt.cmenrichment.dto.HistoryData;
import com.samsung.nmt.cmenrichment.dto.PROCDData;
import com.samsung.nmt.cmenrichment.dto.SinglePROCDData;

public class BatchProcessDataBuilder<D> {
    private boolean hasUpdateData = false;
    private boolean hasDeleteData = false;
    private boolean hasAddData = false;
    private boolean isAllColumnsUpdated = false;

    private List<D> addData;
    private List<D> deleteData;

    private List<D> allColumnsUpdatedData;
    private List<D> propertiesUpdatedData;
    private List<HistoryData<D>> historyData;

    private int addCount;
    private int updateCount;
    private int deleteCount;

    private int updateDataSize;

    public BatchProcessDataBuilder(int updateDataSize) {
        propertiesUpdatedData = new ArrayList<>(updateDataSize);
        historyData = new ArrayList<>(updateDataSize);
    }

    public BatchProcessDataBuilder() {
        propertiesUpdatedData = new ArrayList<>();
        historyData = new ArrayList<>();
    }

    private void addAllColumnUpdatedData(D d) {
        if (isAllColumnsUpdated == false) {
            allColumnsUpdatedData = new ArrayList<>(updateDataSize);
            isAllColumnsUpdated = true;
        }
        allColumnsUpdatedData.add(d);
    }

    public void addToUpdateActionData(SinglePROCDData<D> singlePROCDData) {
        if (singlePROCDData.isOnlyProperyUpdated()) {
            propertiesUpdatedData.add(singlePROCDData.getData());
        } else {
            addAllColumnUpdatedData(singlePROCDData.getData());
        }
        historyData.add(singlePROCDData.getHistoryData());
        hasUpdateData = true;
        updateCount++;
    }

    public void addToAddActionData(D d) {
        if (hasAddData == false) {
            addData = new ArrayList<>();
            hasAddData = true;
        }
        addData.add(d);
        addCount++;
    }

    public void addToDeleteActionData(D d) {
        if (hasDeleteData == false) {
            deleteData = new ArrayList<>();
            hasDeleteData = true;
        }
        deleteData.add(d);
        deleteCount++;
    }

    public BatchProcessedData<D> build() {
        BatchProcessedData<D> batchProcessedData = new BatchProcessedData<>();

        PROCDData<D> procdData = new PROCDData<>(propertiesUpdatedData, historyData,
                Optional.ofNullable(allColumnsUpdatedData));

        batchProcessedData.setHasAddData(hasAddData);
        batchProcessedData.setHasUpdateData(hasUpdateData);
        batchProcessedData.setHasDeleteData(hasDeleteData);
        batchProcessedData.setAddData(addData);
        batchProcessedData.setUpdateData(procdData);
        batchProcessedData.setDeleteData(deleteData);
        return batchProcessedData;
    }

    public String countToString() {
        StringBuilder builder = new StringBuilder("Batch data count[");
        builder.append("addCount=");
        builder.append(addCount);
        builder.append(", updateCount=");
        builder.append(updateCount);
        builder.append(", deleteCount=");
        builder.append(deleteCount);
        builder.append("]");
        return builder.toString();
    }

}
