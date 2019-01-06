package com.samsung.nmt.cmenrichment.dto;

import java.util.List;
import java.util.Optional;

public class PROCDData<D> {
    Optional<List<D>> allColumnsUpdatedData;
    List<D> propertiesUpdatedData;
    List<HistoryData<D>> historyData;

    public PROCDData(List<D> propertiesUpdatedData, List<HistoryData<D>> historyData) {
        super();
        this.propertiesUpdatedData = propertiesUpdatedData;
        this.historyData = historyData;
        this.allColumnsUpdatedData = Optional.ofNullable(null);
    }

    public PROCDData(List<D> propertiesUpdatedData, List<HistoryData<D>> historyData,
            Optional<List<D>> allColumnsUpdatedData) {
        super();
        this.propertiesUpdatedData = propertiesUpdatedData;
        this.historyData = historyData;
        this.allColumnsUpdatedData = allColumnsUpdatedData;
        this.allColumnsUpdatedData = Optional.ofNullable(null);
    }

    public Optional<List<D>> getAllColumnsUpdatedData() {
        return allColumnsUpdatedData;
    }

    public void setAllColumnsUpdatedData(Optional<List<D>> allColumnsUpdatedData) {
        this.allColumnsUpdatedData = allColumnsUpdatedData;
    }

    public List<D> getPropertiesUpdatedData() {
        return propertiesUpdatedData;
    }

    public void setPropertiesUpdatedData(List<D> propertiesUpdatedData) {
        this.propertiesUpdatedData = propertiesUpdatedData;
    }

    public List<HistoryData<D>> getHistoryData() {
        return historyData;
    }

    public void setHistoryData(List<HistoryData<D>> historyData) {
        this.historyData = historyData;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PROCDData [allColumnsUpdatedData=");
        builder.append(allColumnsUpdatedData);
        builder.append(", propertiesUpdatedData=");
        builder.append(propertiesUpdatedData);
        builder.append(", historyData=");
        builder.append(historyData);
        builder.append("]");
        return builder.toString();
    }

}
