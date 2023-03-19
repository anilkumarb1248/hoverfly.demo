package com.hoverfly.demo.comptest.activitylogcontroller

import com.hoverfly.demo.enums.EventType
import com.hoverfly.demo.model.EventStats
import com.hoverfly.demo.model.EventStatsRequest
import groovy.json.JsonSlurper
import io.specto.hoverfly.junit.core.SimulationSource
import io.specto.hoverfly.junit.dsl.HttpBodyConverter
import io.specto.hoverfly.junit.dsl.ResponseCreators
import io.specto.hoverfly.junit.dsl.matchers.HoverflyMatchers
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Unroll

import java.time.LocalDate

class EventStatsCompSpec extends BaseActivityLogCompSpec {

    @Shared
    String ACTIVITY_LOG_EVENT_STATS_API_URI = "/admin/activity/event/stats"

    def "activity event stats api returns success response"() {
        setup:
        hoverFly.simulate(SimulationSource.dsl(
                activityEventStatsRequestMatchBuilder.body(HoverflyMatchers.equalsToJson(HttpBodyConverter.json(activityEventStatsSuccessRequest)))
                        .willReturn(ResponseCreators.success(activityEventStatsSuccessResponseMock, MediaType.APPLICATION_JSON_VALUE))
        ))

        when:
        ResponseEntity<String> responseEntity = callService(buildURI(ACTIVITY_LOG_EVENT_STATS_API_URI, null),
                HttpMethod.POST, activityEventStatsSuccessRequest)

        then:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.body
        Map bodyMap = new JsonSlurper().parseText(responseEntity.body)
        bodyMap.size() == 3
        bodyMap["status"]["code"] == "200"
        bodyMap["status"]["message"] == "Successfully fetched the event stats"
        !bodyMap["error"]
        bodyMap["payload"]
        List<EventStats> eventStats = bodyMap["payload"]
        eventStats.size() == 2
        eventStats.get(0).event == "ADD_EVENT"
        eventStats.get(0).count == 100
        eventStats.get(1).event == "UPDATE_EVENT"
        eventStats.get(1).count == 50
    }

    def "activity event stats api returns failure response when no events found"() {
        setup:
        hoverFly.simulate(SimulationSource.dsl(
                activityEventStatsRequestMatchBuilder.body(HoverflyMatchers.equalsToJson(HttpBodyConverter.json(activityEventStatsFailureRequest)))
                        .willReturn(ResponseCreators.success(activityEventStatsFailureResponseMock, MediaType.APPLICATION_JSON_VALUE))
        ))

        when:
        ResponseEntity<String> responseEntity = callService(buildURI(ACTIVITY_LOG_EVENT_STATS_API_URI, null),
                HttpMethod.POST, activityEventStatsFailureRequest)

        then:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.body
        Map bodyMap = new JsonSlurper().parseText(responseEntity.body)
        bodyMap.size() == 3
        bodyMap["status"]["code"] == "204"
        bodyMap["status"]["message"] == "No events found for the given time period"
        !bodyMap["error"]
        !bodyMap["payload"]
    }

    @Unroll
    def "activity event stats api returns bad request response when #condition"() {
        setup:
        EventStatsRequest request = new EventStatsRequest(startDate: startDate, endDate: endDate, eventType: EventType.AGENT)

        when:
        ResponseEntity<String> responseEntity = callService(buildURI(ACTIVITY_LOG_EVENT_STATS_API_URI, null),
                HttpMethod.POST, request)

        then:
        responseEntity
        responseEntity.statusCode == HttpStatus.BAD_REQUEST
        responseEntity.body
        Map bodyMap = new JsonSlurper().parseText(responseEntity.body)
        bodyMap.size() == 3
        bodyMap["status"]["code"] == "400"
        bodyMap["status"]["message"] == "Bad Request Exception"
        bodyMap["error"]
        bodyMap["error"]["errorId"] == HttpStatus.BAD_REQUEST.value()
        bodyMap["error"]["errorMsg"] == errorMessage
        bodyMap["error"]["errorDesc"] == "Bad Request Exception"
        !bodyMap["payload"]

        where:
        condition                             | startDate                     | endDate                       | errorMessage
        "Start Date is null"                  | null                          | LocalDate.parse("2023-01-01") | "Start Date and End Date should not be null"
        "End Date is null"                    | LocalDate.parse("2023-01-01") | null                          | "Start Date and End Date should not be null"
        "Start Date & End Date is null"       | null                          | null                          | "Start Date and End Date should not be null"
        "Start Date is greater than End Date" | LocalDate.parse("2024-01-01") | LocalDate.parse("2023-01-01") | "Start Date cannot be greater than End Date"

    }

}
