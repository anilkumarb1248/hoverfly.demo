package com.hoverfly.demo.service;

import com.hoverfly.demo.client.AgentLoggingClient;
import com.hoverfly.demo.model.EventStatsRequest;
import com.hoverfly.demo.model.EventStatsResponse;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogService {

    private final AgentLoggingClient agentLoggingClient;

    public ActivityLogService(AgentLoggingClient agentLoggingClient){
        this.agentLoggingClient = agentLoggingClient;
    }

    public EventStatsResponse getEventStats(EventStatsRequest request){
        return agentLoggingClient.getEventStats(request);
    }
}
