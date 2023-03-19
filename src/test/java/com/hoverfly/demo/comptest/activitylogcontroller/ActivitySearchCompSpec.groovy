package com.hoverfly.demo.comptest.activitylogcontroller

import com.hoverfly.demo.model.ActivityEvent
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

class ActivitySearchCompSpec extends BaseActivityLogCompSpec{

    @Shared
    String ACTIVITY_LOG_SEARCH_API_URI = "/admin/activity/search"

    def "activity search api returns success response"(){
        setup:
//        hoverFly.simulate(activitySearchMicroSimulation)
        //If we configure the matchers in single dsl, hoverfly is not able to match the body. giving body is matching (body: [1st match, 2nd match]

        hoverFly.simulate(SimulationSource.dsl(
                activitySearchRequestMatchBuilder.body(HoverflyMatchers.equalsToJson(HttpBodyConverter.json(activitySearchSuccessRequest)))
                        .willReturn(ResponseCreators.success(activitySearchSuccessResponseMock, MediaType.APPLICATION_JSON_VALUE))
        ))

        when:
        ResponseEntity<String> responseEntity = callService(buildURI(ACTIVITY_LOG_SEARCH_API_URI, null),
                HttpMethod.POST, activitySearchSuccessRequest)

        then:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.body
        Map bodyMap = new JsonSlurper().parseText(responseEntity.body)
        bodyMap.size() == 3
        bodyMap["status"]["code"] == "200"
        bodyMap["status"]["message"] == "Successfully fetched the activities"
        !bodyMap["error"]
        bodyMap["payload"]
        bodyMap["payload"]["status"] == "success"
        bodyMap["payload"]["events"]
        List<ActivityEvent> events = bodyMap["payload"]["events"]
        events.size() == 2
        events.get(0).uuid == "Test_UUID_1"
        events.get(0).userName == "Test_User_1"
        events.get(0).event == "ADD_EVENT"
        events.get(0).eventType == "AGENT"

        events.get(1).uuid == "Test_UUID_2"
        events.get(1).userName == "Test_User_2"
        events.get(1).event == "UPDATE_EVENT"
        events.get(1).eventType == "USER"
    }

    def "activity search api returns failure response when no activities found"(){
        setup:
//        hoverFly.simulate(activitySearchMicroSimulation)
        //If we configure the matchers in single dsl, hoverfly is not able to match the body. giving body is matching (body: [1st match, 2nd match]

        hoverFly.simulate(SimulationSource.dsl(
                activitySearchRequestMatchBuilder.body(HoverflyMatchers.equalsToJson(HttpBodyConverter.json(activitySearchFailureRequest)))
                        .willReturn(ResponseCreators.success(activitySearchFailureResponseMock, MediaType.APPLICATION_JSON_VALUE))
        ))

        when:
        ResponseEntity<String> responseEntity = callService(buildURI(ACTIVITY_LOG_SEARCH_API_URI, null),
                HttpMethod.POST, activitySearchFailureRequest)

        then:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.body
        Map bodyMap = new JsonSlurper().parseText(responseEntity.body)
        bodyMap.size() == 3
        bodyMap["status"]["code"] == "204"
        bodyMap["status"]["message"] == "No activities found with given request"
        !bodyMap["error"]
        !bodyMap["payload"]

    }
}
