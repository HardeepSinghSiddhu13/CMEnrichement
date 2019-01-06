package com.samsung.nmt.cmenrichment.dto;

import java.util.Calendar;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class NwElement {

    private Integer elementId;
    private Integer neId;
    private String name;
    private Integer statusId;
    private String vendor;
    private String version;
    private Integer collectorId;
    private Integer emsId;
    private ObjectNode properties;
    private Integer locationId;
    private Calendar createdTimeStamp;
    private Calendar lastModifiedTimeStamp;
    private Integer typeId;

    public NwElement(Integer elementId, Integer neId, String name, Integer statusId, String vendor, String version,
            Integer collectorId, Integer emsId, ObjectNode properties, Integer locationId, Calendar createdTimeStamp,
            Calendar lastModifiedTimeStamp, Integer typeId) {
        super();
        this.elementId = elementId;
        this.neId = neId;
        this.name = name;
        this.statusId = statusId;
        this.vendor = vendor;
        this.version = version;
        this.collectorId = collectorId;
        this.emsId = emsId;
        this.properties = properties;
        this.locationId = locationId;
        this.createdTimeStamp = createdTimeStamp;
        this.lastModifiedTimeStamp = lastModifiedTimeStamp;
        this.typeId = typeId;
    }

    public NwElement(Integer neId, String name, Integer statusId, String vendor, String version, Integer collectorId,
            Integer emsId, ObjectNode properties, Integer locationId, Calendar createdTimeStamp,
            Calendar lastModifiedTimeStamp, Integer typeId) {
        super();
        this.neId = neId;
        this.name = name;
        this.statusId = statusId;
        this.vendor = vendor;
        this.version = version;
        this.collectorId = collectorId;
        this.emsId = emsId;
        this.properties = properties;
        this.locationId = locationId;
        this.createdTimeStamp = createdTimeStamp;
        this.lastModifiedTimeStamp = lastModifiedTimeStamp;
        this.typeId = typeId;
    }

    public NwElement() {
    }

    public Integer getNeId() {
        return neId;
    }

    public void setNeId(Integer neId) {
        this.neId = neId;
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

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getCollectorId() {
        //return collectorId;
        return 1;
    }

    public void setCollectorId(Integer collectorId) {
        this.collectorId = collectorId;
    }

    public Integer getEmsId() {
        return emsId;
    }

    public void setEmsId(Integer emsId) {
        this.emsId = emsId;
    }

    public ObjectNode getProperties() {
        return properties;
    }

    public void setProperties(ObjectNode properties) {
        this.properties = properties;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
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

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NwElement [elementId=");
        builder.append(elementId);
        builder.append(", neId=");
        builder.append(neId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", statusId=");
        builder.append(statusId);
        builder.append(", vendor=");
        builder.append(vendor);
        builder.append(", version=");
        builder.append(version);
        builder.append(", collectorId=");
        builder.append(collectorId);
        builder.append(", emsId=");
        builder.append(emsId);
        builder.append(", properties=");
        builder.append(properties);
        builder.append(", locationId=");
        builder.append(locationId);
        builder.append(", createdTimeStamp=");
        builder.append(createdTimeStamp);
        builder.append(", lastModifiedTimeStamp=");
        builder.append(lastModifiedTimeStamp);
        builder.append(", typeId=");
        builder.append(typeId);
        builder.append("]");
        return builder.toString();
    }
}
