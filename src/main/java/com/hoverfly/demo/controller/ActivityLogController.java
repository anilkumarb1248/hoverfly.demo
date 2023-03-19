package com.hoverfly.demo.controller;

import com.hoverfly.demo.model.ActivitySearchRequest;
import com.hoverfly.demo.model.ApiResponse;
import com.hoverfly.demo.model.ApiSingleResponse;
import com.hoverfly.demo.model.Error;
import com.hoverfly.demo.model.EventStats;
import com.hoverfly.demo.model.EventStatsRequest;
import com.hoverfly.demo.model.EventStatsResponse;
import com.hoverfly.demo.model.ActivitySearchResponse;
import com.hoverfly.demo.model.Status;
import com.hoverfly.demo.service.ActivityLogService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/activity")
public class ActivityLogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityLogController.class);

    private final ActivityLogService activityLogService;

    private static final String BAD_REQUEST_MESSAGE = "Bad Request Exception";

    public ActivityLogController(ActivityLogService activityLogService){
        this.activityLogService = activityLogService;
    }
    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiSingleResponse<ActivitySearchResponse>> getActivities(@RequestBody ActivitySearchRequest request){
        LOGGER.info("************ In activity search API");
        ApiSingleResponse<ActivitySearchResponse> apiSingleResponse = new ApiSingleResponse<>();

        try{
            ActivitySearchResponse activitySearchResponse = activityLogService.getActivities(request);
            if(activitySearchResponse != null && CollectionUtils.isNotEmpty(activitySearchResponse.getEvents())){
                apiSingleResponse.setPayload(activitySearchResponse);
                apiSingleResponse.setStatus(new Status("200", "Successfully fetched the activities"));
                LOGGER.info("Successfully returning the activities, size: {}", activitySearchResponse.getEvents().size());
                return ResponseEntity.status(HttpStatus.OK).body(apiSingleResponse);
            }else{
                String message = "No activities found with given request";
                apiSingleResponse.setStatus(new Status("204", message));
                LOGGER.info(message);
                //Note: If we use HttpStatus.NO_CONTENT, it doesn't return the body. So using OK response
                return ResponseEntity.status(HttpStatus.OK).body(apiSingleResponse);
            }
        }catch (Exception e){
            String message = "Encountered Exception";
            apiSingleResponse.setStatus(new Status("500", message));
            apiSingleResponse.setError(new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, e.getMessage()));
            LOGGER.info("{}, exception: {}", message, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiSingleResponse);
        }
    }

    @PostMapping(value = "/event/stats", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<EventStats>> getEventStats(@RequestBody EventStatsRequest request){

        ApiResponse<EventStats> apiResponse = new ApiResponse<>();
        String badRequestMessage = validateEventStatsRequest(request);

        if(badRequestMessage != null){
            apiResponse.setStatus(new Status("400", BAD_REQUEST_MESSAGE));
            apiResponse.setError(new Error(HttpStatus.BAD_REQUEST.value(), badRequestMessage, BAD_REQUEST_MESSAGE));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
        try{
            EventStatsResponse eventStatsResponse = activityLogService.getEventStats(request);
            if(eventStatsResponse != null && CollectionUtils.isNotEmpty(eventStatsResponse.getEventStats())){
                apiResponse.setPayload(eventStatsResponse.getEventStats());
                apiResponse.setStatus(new Status("200", "Successfully fetched the event stats"));
                LOGGER.info("Successfully returning the event stats, startDate: {}, endDate: {}, size:{}",
                        request.getStartDate(), request.getEndDate(), eventStatsResponse.getEventStats().size());
                return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
            }else{
                String message = "No events found for the given time period";
                apiResponse.setStatus(new Status("204", message));
                LOGGER.info(message);
                //Note: If we use HttpStatus.NO_CONTENT, it doesn't return the body. So using OK response
                return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
            }
        }catch (Exception e){
            String message = "Encountered Exception";
            apiResponse.setStatus(new Status("500", message));
            apiResponse.setError(new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, e.getMessage()));
            LOGGER.info("{}, exception: {}", message, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    private String validateEventStatsRequest(EventStatsRequest request) {
        String badRequestMessage = null;
        if(request.getStartDate() == null || request.getEndDate() == null){
            badRequestMessage = "Start Date and End Date should not be null";
        }
        if(badRequestMessage == null && request.getStartDate().isAfter(request.getEndDate())){
            badRequestMessage = "Start Date cannot be greater than End Date";
        }
        return badRequestMessage;
    }


}
