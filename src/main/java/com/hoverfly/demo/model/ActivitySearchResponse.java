package com.hoverfly.demo.model;

import java.util.List;

public class ActivitySearchResponse {

    private String status;
    private List<ActivityEvent> events;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ActivityEvent> getEvents() {
        return events;
    }

    public void setEvents(List<ActivityEvent> events) {
        this.events = events;
    }
}
