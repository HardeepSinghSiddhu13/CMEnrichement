package com.samsung.nmt.cmenrichment.dto;

import java.util.Calendar;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class NwSubElement {

    private Long subElementId;
    private Integer elementId;
    private String name;
    private Integer typeId;
    private ObjectNode properties;
    private Calendar createdTimeStamp;
    private Calendar lastModifiedTimeStamp;
    private Integer locationId;

    public NwSubElement(Integer elementId, Integer typeId, ObjectNode properties) {
        super();
        this.elementId = elementId;
        this.typeId = typeId;
        this.properties = properties;
    }

    public NwSubElement(Long subElementId, Integer elementId, String name, Integer typeId, ObjectNode properties,
            Calendar createdTimeStamp, Calendar lastModifiedTimeStamp) {
        super();
        this.subElementId = subElementId;
        this.elementId = elementId;
        this.name = name;
        this.typeId = typeId;
        this.properties = properties;
        this.createdTimeStamp = createdTimeStamp;
        this.lastModifiedTimeStamp = lastModifiedTimeStamp;
    }

    public NwSubElement(Integer elementId, String name, Integer typeId, ObjectNode properties,
            Calendar createdTimeStamp,
            Calendar lastModifiedTimeStamp) {
        super();
        this.elementId = elementId;
        this.name = name;
        this.typeId = typeId;
        this.properties = properties;
        this.createdTimeStamp = createdTimeStamp;
        this.lastModifiedTimeStamp = lastModifiedTimeStamp;
    }

    public NwSubElement() {
    }

    public Long getSubElementId() {
        return subElementId;
    }

    public void setSubElementId(Long subElementId) {
        this.subElementId = subElementId;
    }

    public Integer getElementId() {
        return elementId;
    }

    public void setElementId(Integer elementId) {
        this.elementId = elementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
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

    public Calendar getLastModifiedTimeStamp() {
        return lastModifiedTimeStamp;
    }

    public void setLastModifiedTimeStamp(Calendar lastModifiedTimeStamp) {
        this.lastModifiedTimeStamp = lastModifiedTimeStamp;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NwSubElement [subElementId=");
        builder.append(subElementId);
        builder.append(", elementId=");
        builder.append(elementId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", typeId=");
        builder.append(typeId);
        builder.append(", properties=");
        builder.append(properties);
        builder.append(", createdTimeStamp=");
        builder.append(createdTimeStamp);
        builder.append(", lastModifiedTimeStamp=");
        builder.append(lastModifiedTimeStamp);
        builder.append(", locationId=");
        builder.append(locationId);
        builder.append("]");
        return builder.toString();
    }

    public String toCompactString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NwSubElement [elementId=");
        builder.append(elementId);
        builder.append(", name=");
        builder.append(name);
        builder.append("]");
        return builder.toString();
    }

}
