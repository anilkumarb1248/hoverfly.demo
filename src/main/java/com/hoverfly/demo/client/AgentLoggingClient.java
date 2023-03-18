package com.hoverfly.demo.client;

import com.hoverfly.demo.exception.ClientException;
import com.hoverfly.demo.model.ActivitySearchRequest;
import com.hoverfly.demo.model.ActivitySearchResponse;
import com.hoverfly.demo.model.EventStatsRequest;
import com.hoverfly.demo.model.EventStatsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component
public class AgentLoggingClient {

    private final RestTemplate restTemplate;

    private final String activityLoggingUrl;
    private final String eventStatsResource;

    private final String activitySearchResource;

    public AgentLoggingClient(RestTemplate restTemplate,
                              @Value("${activityLog.url}") String activityLoggingUrl,
                              @Value("${activityLog.resources.search.path}") String activitySearchResource,
                              @Value("${activityLog.resources.eventStats.path}") String eventStatsResource) {
        this.restTemplate = restTemplate;
        this.activityLoggingUrl = activityLoggingUrl;
        this.activitySearchResource = activitySearchResource;
        this.eventStatsResource = eventStatsResource;
    }

    public EventStatsResponse getEventStats(EventStatsRequest request) {
        EventStatsResponse eventStatsResponse = null;
        HttpEntity httpEntity = new HttpEntity(request, buildHeaders());
        try {
            ResponseEntity<EventStatsResponse> responseEntity = restTemplate.exchange(activityLoggingUrl + eventStatsResource,
                    HttpMethod.POST, httpEntity, EventStatsResponse.class);
            if (responseEntity != null && responseEntity.getBody() != null) {
                eventStatsResponse = responseEntity.getBody();
            }
        } catch (RestClientException e) {
            throw new ClientException(e, AgentLoggingClient.class);
        }

        return eventStatsResponse;
    }

    private MultiValueMap<String, String> buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    public ActivitySearchResponse getActivities(ActivitySearchRequest request) {
        ActivitySearchResponse activitySearchResponse = null;
        HttpEntity httpEntity = new HttpEntity(request, buildHeaders());
        try {
            ResponseEntity<ActivitySearchResponse> responseEntity = restTemplate.exchange(activityLoggingUrl + activitySearchResource,
                    HttpMethod.POST, httpEntity, ActivitySearchResponse.class);
            if (responseEntity != null && responseEntity.getBody() != null) {
                activitySearchResponse = responseEntity.getBody();
            }
        } catch (RestClientException e) {
            throw new ClientException(e, AgentLoggingClient.class);
        }
        return activitySearchResponse;
    }
}
