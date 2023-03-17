package com.hoverfly.demo.model;

import java.util.List;

public class EventStatsResponse {
    private String status;
    private List<EventStats> eventStats;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<EventStats> getEventStats() {
        return eventStats;
    }

    public void setEventStats(List<EventStats> eventStats) {
        this.eventStats = eventStats;
    }
}
