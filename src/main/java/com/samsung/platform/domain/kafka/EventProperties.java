package com.samsung.platform.domain.kafka;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class EventProperties {

    ObjectNode additionalFields;
    List<String> additionalFieldsArray;

    public ObjectNode getAdditionalFields() {
        return additionalFields;
    }

    public void setAdditionalFields(ObjectNode additionalFields) {
        this.additionalFields = additionalFields;
    }

    public List<String> getAdditionalFieldsArray() {
        return additionalFieldsArray;
    }

    public void setAdditionalFieldsArray(List<String> additionalFieldsArray) {
        this.additionalFieldsArray = additionalFieldsArray;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EventProperties [additionalFields=");
        builder.append(additionalFields);
        builder.append(", additionalFieldsArray=");
        builder.append(additionalFieldsArray);
        builder.append("]");
        return builder.toString();
    }

}
