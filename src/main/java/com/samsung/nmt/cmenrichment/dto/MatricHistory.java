package com.samsung.nmt.cmenrichment.dto;

public class MatricHistory {
    private long historyId;
    private int propMetadataId;

    public MatricHistory(long historyId, int propMetadataId) {
        super();
        this.historyId = historyId;
        this.propMetadataId = propMetadataId;
    }

    public long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(long historyId) {
        this.historyId = historyId;
    }

    public int getPropMetadataId() {
        return propMetadataId;
    }

    public void setPropMetadataId(int propMetadataId) {
        this.propMetadataId = propMetadataId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MatricHistory [historyId=");
        builder.append(historyId);
        builder.append(", propMetadataId=");
        builder.append(propMetadataId);
        builder.append("]");
        return builder.toString();
    }

}
