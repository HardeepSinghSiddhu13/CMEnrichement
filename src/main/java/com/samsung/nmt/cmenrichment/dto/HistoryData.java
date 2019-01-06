package com.samsung.nmt.cmenrichment.dto;

import java.util.List;

public class HistoryData<H> {
    private H historyData;
    private List<MatricHistory> updatedAttributes;

    public HistoryData(H historyData, List<MatricHistory> updatedAttributes) {
        super();
        this.historyData = historyData;
        this.updatedAttributes = updatedAttributes;
    }

    public H getHistoryData() {
        return historyData;
    }

    public void setHistoryData(H historyData) {
        this.historyData = historyData;
    }

    public List<MatricHistory> getUpdatedAttributes() {
        return updatedAttributes;
    }

    public void setUpdatedAttributes(List<MatricHistory> updatedAttributes) {
        this.updatedAttributes = updatedAttributes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HistoryData [historyData=");
        builder.append(historyData);
        builder.append(", updatedAttributes=");
        builder.append(updatedAttributes);
        builder.append("]");
        return builder.toString();
    }

}
