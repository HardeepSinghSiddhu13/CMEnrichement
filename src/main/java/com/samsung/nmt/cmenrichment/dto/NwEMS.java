package com.samsung.nmt.cmenrichment.dto;

public class NwEMS {

    private Integer emsId;
    private String name;
    private Integer statusId;
    private String ipAddress;
    private String description;

    public NwEMS(Integer emsId, String name) {
        super();
        this.emsId = emsId;
        this.name = name;
    }

    public NwEMS(Integer emsId, String name, Integer statusId, String ipAddress, String description) {
        super();
        this.emsId = emsId;
        this.name = name;
        this.statusId = statusId;
        this.ipAddress = ipAddress;
        this.description = description;
    }

    public NwEMS(String name, Integer statusId, String ipAddress, String description) {
        super();
        this.name = name;
        this.statusId = statusId;
        this.ipAddress = ipAddress;
        this.description = description;
    }

    public NwEMS() {

    }

    public Integer getEmsId() {
        return emsId;
    }

    public void setEmsId(Integer emsId) {
        this.emsId = emsId;
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NwEMS [emsId=");
        builder.append(emsId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", statusId=");
        builder.append(statusId);
        builder.append(", ipAddress=");
        builder.append(ipAddress);
        builder.append(", description=");
        builder.append(description);
        builder.append("]");
        return builder.toString();
    }

}
