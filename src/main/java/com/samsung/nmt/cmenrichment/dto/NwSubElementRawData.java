package com.samsung.nmt.cmenrichment.dto;

import java.util.Calendar;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.samsung.nmt.cmenrichment.client.Action;
import com.samsung.platform.domain.kafka.Event;

public class NwSubElementRawData {
    private NwSubElKey nwSubElKey;
    private ObjectNode properties;
    private Action action;
    private String elementName;
    private Integer locationId;
    private List<Event> events;
    private Calendar currTime;

    public NwSubElementRawData(Integer elementId, Integer typeId, ObjectNode properties, Action action,
            String name, String elementName, Integer locationId, List<Event> events, Calendar currTime) {
        super();

        nwSubElKey = NwSubElKey.createKey(elementId, typeId, name);
        this.properties = properties;
        this.action = action;
        this.elementName = elementName;
        this.locationId = locationId;
        this.events = events;
        this.currTime = currTime;
    }

    public ObjectNode getProperties() {
        return properties;
    }

    public Action getAction() {
        return action;
    }

    public String getElementName() {
        return elementName;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public NwSubElKey getNwSubElKey() {
        return nwSubElKey;
    }

    public List<Event> getEvents() {
        return events;
    }

    public Calendar getCurrTime() {
        return currTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NwSubElementRawData [properties=");
        builder.append(properties);
        builder.append(", action=");
        builder.append(action);
        builder.append(", elementName=");
        builder.append(elementName);
        builder.append(", locationId=");
        builder.append(locationId);
        builder.append(", nwSubElKey=");
        builder.append(nwSubElKey);
        builder.append(", currTime=");
        builder.append(currTime);
        builder.append("]");
        return builder.toString();
    }

}
