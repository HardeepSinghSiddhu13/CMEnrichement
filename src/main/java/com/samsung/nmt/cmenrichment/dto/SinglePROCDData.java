package com.samsung.nmt.cmenrichment.dto;

public class SinglePROCDData<D> {
    D data;
    HistoryData<D> historyData;
    boolean isOnlyProperyUpdated;

    public SinglePROCDData(D data, HistoryData<D> historyData, boolean isOnlyProperyUpdated) {
        super();
        this.data = data;
        this.historyData = historyData;
        this.isOnlyProperyUpdated = isOnlyProperyUpdated;
    }

    public SinglePROCDData(D data, HistoryData<D> historyData) {
        super();
        this.data = data;
        this.historyData = historyData;
        this.isOnlyProperyUpdated = false;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    public HistoryData<D> getHistoryData() {
        return historyData;
    }

    public void setHistoryData(HistoryData<D> historyData) {
        this.historyData = historyData;
    }

    public boolean isOnlyProperyUpdated() {
        return isOnlyProperyUpdated;
    }

    public void setOnlyProperyUpdated(boolean isOnlyProperyUpdated) {
        this.isOnlyProperyUpdated = isOnlyProperyUpdated;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SinglePROCDData [data=");
        builder.append(data);
        builder.append(", historyData=");
        builder.append(historyData);
        builder.append(", isOnlyProperyUpdated=");
        builder.append(isOnlyProperyUpdated);
        builder.append("]");
        return builder.toString();
    }

}
