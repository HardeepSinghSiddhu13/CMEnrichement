package com.samsung.nmt.cmenrichment.dto;

import java.util.List;
import java.util.Map;

public class BatchProcessedData<D> {
    boolean hasUpdateData = false;
    boolean hasDeleteData = false;
    boolean hasAddData = false;
    List<D> addData;
    PROCDData<D> updateData;
    List<D> deleteData;
    Map<String, Integer> parentIdMap;

    public BatchProcessedData() {
    }

    public boolean isHasUpdateData() {
        return hasUpdateData;
    }

    public void setHasUpdateData(boolean hasUpdateData) {
        this.hasUpdateData = hasUpdateData;
    }

    public boolean isHasDeleteData() {
        return hasDeleteData;
    }

    public void setHasDeleteData(boolean hasDeleteData) {
        this.hasDeleteData = hasDeleteData;
    }

    public boolean isHasAddData() {
        return hasAddData;
    }

    public void setHasAddData(boolean hasAddData) {
        this.hasAddData = hasAddData;
    }

    public List<D> getAddData() {
        return addData;
    }

    public void setAddData(List<D> addData) {
        this.addData = addData;
    }

    public PROCDData<D> getUpdateData() {
        return updateData;
    }

    public void setUpdateData(PROCDData<D> updateData) {
        this.updateData = updateData;
    }

    public List<D> getDeleteData() {
        return deleteData;
    }

    public void setDeleteData(List<D> deleteData) {
        this.deleteData = deleteData;
    }

    public Map<String, Integer> getParentIdMap() {
        return parentIdMap;
    }

    public void setParentIdMap(Map<String, Integer> parentIdMap) {
        this.parentIdMap = parentIdMap;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BatchProcessedData [hasUpdateData=");
        builder.append(hasUpdateData);
        builder.append(", hasDeleteData=");
        builder.append(hasDeleteData);
        builder.append(", hasAddData=");
        builder.append(hasAddData);
        builder.append(", addData=");
        builder.append(addData);
        builder.append(", updateData=");
        builder.append(updateData);
        builder.append(", deleteData=");
        builder.append(deleteData);
        builder.append(", parentIdMap=");
        builder.append(parentIdMap);
        builder.append("]");
        return builder.toString();
    }

}
