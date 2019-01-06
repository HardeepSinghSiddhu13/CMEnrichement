package com.samsung.platform.domain.kafka;

import java.util.List;

/**
 * @author inirawat
 *
 */
public class Event extends EventProperties {

    Identifier identifier;
    EventProperties properties;
    List<Event> additionalObjects;

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public EventProperties getProperties() {
        return properties;
    }

    public void setProperties(EventProperties properties) {
        this.properties = properties;
    }

    public List<Event> getAdditionalObjects() {
        return additionalObjects;
    }

    public void setAdditionalObjects(List<Event> additionalObjects) {
        this.additionalObjects = additionalObjects;
    }

}
