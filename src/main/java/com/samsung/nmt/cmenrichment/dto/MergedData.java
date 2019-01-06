package com.samsung.nmt.cmenrichment.dto;

import java.util.List;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MergedData {

    private boolean isOnlyPropUpdated;
    private ObjectNode mergedJson;
    private List<String> updatedAttrs;

    public MergedData() {
    }

    public MergedData(boolean isOnlyPropUpdated,
            ObjectNode mergedJson, List<String> updatedAttrs) {
        super();
        this.isOnlyPropUpdated = isOnlyPropUpdated;
        this.mergedJson = mergedJson;
        this.updatedAttrs = updatedAttrs;
    }

    public MergedData(ObjectNode mergedJson) {
        super();
        isOnlyPropUpdated = false;
        this.mergedJson = mergedJson;
    }

    public boolean isOnlyPropUpdated() {
        return isOnlyPropUpdated;
    }

    public void setOnlyPropUpdated(boolean isOnlyPropUpdated) {
        this.isOnlyPropUpdated = isOnlyPropUpdated;
    }

    public ObjectNode getMergedJson() {
        return mergedJson;
    }

    public void setMergedJson(ObjectNode mergedJson) {
        this.mergedJson = mergedJson;
    }

    public List<String> getUpdatedAttrs() {
        return updatedAttrs;
    }

    public void setUpdatedAttrs(List<String> updatedAttrs) {
        this.updatedAttrs = updatedAttrs;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MergedData [isOnlyPropUpdated=");
        builder.append(isOnlyPropUpdated);
        builder.append(", mergedJson=");
        builder.append(mergedJson);
        builder.append(", updatedAttrs=");
        builder.append(updatedAttrs);
        builder.append("]");
        return builder.toString();
    }

}
