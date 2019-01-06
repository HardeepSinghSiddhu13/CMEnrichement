package com.samsung.nmt.cmenrichment.dto;

import java.util.Calendar;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class SwElement {

    private Integer swrId;
    private Integer elementId;
    private Integer circleId;
    private ObjectNode properties;
    private Calendar createdTimeStamp;
    private Calendar modifiedTimeStamp;

    public SwElement(Integer swrId, Integer elementId, Integer circleId, ObjectNode properties,
            Calendar createdTimeStamp, Calendar modifiedTimeStamp) {
        super();
        this.swrId = swrId;
        this.elementId = elementId;
        this.circleId = circleId;
        this.properties = properties;
        this.createdTimeStamp = createdTimeStamp;
        this.modifiedTimeStamp = modifiedTimeStamp;
    }

    public SwElement(Integer elementId, ObjectNode properties, Calendar createdTimeStamp, Calendar modifiedTimeStamp) {
        super();
        this.elementId = elementId;
        this.properties = properties;
        this.createdTimeStamp = createdTimeStamp;
        this.modifiedTimeStamp = modifiedTimeStamp;
    }

    public SwElement() {

    }

    public Integer getSwrId() {
        return swrId;
    }

    public void setSwrId(Integer swrId) {
        this.swrId = swrId;
    }

    public Integer getElementId() {
        return elementId;
    }

    public void setElementId(Integer elementId) {
        this.elementId = elementId;
    }

    public Integer getCircleId() {
        return circleId;
    }

    public void setCircleId(Integer circleId) {
        this.circleId = circleId;
    }

    public ObjectNode getProperties() {
        return properties;
    }

    public void setProperties(ObjectNode properties) {
        this.properties = properties;
    }

    public Calendar getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(Calendar createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public Calendar getModifiedTimeStamp() {
        return modifiedTimeStamp;
    }

    public void setModifiedTimeStamp(Calendar modifiedTimeStamp) {
        this.modifiedTimeStamp = modifiedTimeStamp;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SwElement [swrId=");
        builder.append(swrId);
        builder.append(", elementId=");
        builder.append(elementId);
        builder.append(", circleId=");
        builder.append(circleId);
        builder.append(", properties=");
        builder.append(properties);
        builder.append(", createdTimeStamp=");
        builder.append(createdTimeStamp);
        builder.append(", modifiedTimeStamp=");
        builder.append(modifiedTimeStamp);
        builder.append("]");
        return builder.toString();
    }

}
