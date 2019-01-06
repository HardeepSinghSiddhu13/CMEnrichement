package com.samsung.nmt.cmenrichment.dto;

import java.util.Calendar;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class HwElement {

    private Integer hwrId;
    private Integer elementId;
    private Integer circleId;
    private String unitType;
    private Integer unitId;
    private String unitSide;
    private ObjectNode properties;
    private Calendar createdTimeStamp;
    private Calendar modifiedTimeStamp;

    public HwElement(Integer hwrId, Integer elementId, Integer circleId, String unitType, Integer unitId,
            String unitSide, ObjectNode properties, Calendar createdTimeStamp, Calendar modifiedTimeStamp) {
        super();
        this.hwrId = hwrId;
        this.elementId = elementId;
        this.circleId = circleId;
        this.unitType = unitType;
        this.unitId = unitId;
        this.unitSide = unitSide;
        this.properties = properties;
        this.createdTimeStamp = createdTimeStamp;
        this.modifiedTimeStamp = modifiedTimeStamp;
    }

    public HwElement(Integer elementId, String unitType, Integer unitId, String unitSide, ObjectNode properties,
            Calendar createdTimeStamp, Calendar modifiedTimeStamp) {
        super();
        this.elementId = elementId;
        this.unitType = unitType;
        this.unitId = unitId;
        this.unitSide = unitSide;
        this.properties = properties;
        this.createdTimeStamp = createdTimeStamp;
        this.modifiedTimeStamp = modifiedTimeStamp;
    }

    public HwElement() {

    }

    public Integer getHwrId() {
        return hwrId;
    }

    public void setHwrId(Integer hwrId) {
        this.hwrId = hwrId;
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

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public String getUnitSide() {
        return unitSide;
    }

    public void setUnitSide(String unitSide) {
        this.unitSide = unitSide;
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
        builder.append("HwElement [hwrId=");
        builder.append(hwrId);
        builder.append(", elementId=");
        builder.append(elementId);
        builder.append(", circleId=");
        builder.append(circleId);
        builder.append(", unitType=");
        builder.append(unitType);
        builder.append(", unitId=");
        builder.append(unitId);
        builder.append(", unitSide=");
        builder.append(unitSide);
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
