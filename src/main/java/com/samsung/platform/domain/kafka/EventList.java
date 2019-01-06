package com.samsung.platform.domain.kafka;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
public class EventList implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2789591683149217648L;
    private RequestHeader requestheader;
    List<Event> eventList;

    public RequestHeader getRequestheader() {
        return requestheader;
    }

    public void setRequestheader(RequestHeader requestheader) {
        this.requestheader = requestheader;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    @Override
    public String toString() {
        return "EventList [requestheader=" + requestheader + ", eventList=" + eventList + "]";
    }

}
