package com.hoverfly.demo.model;

import com.hoverfly.demo.enums.EventType;

import java.time.LocalDate;

public class EventStatsRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private EventType eventType;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
