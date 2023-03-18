package com.hoverfly.demo.comptest.activitylogcontroller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.hoverfly.demo.comptest.BaseServiceCompSpec
import com.hoverfly.demo.model.ActivitySearchRequest
import groovy.json.JsonSlurper
import io.specto.hoverfly.junit.core.SimulationSource
import io.specto.hoverfly.junit.dsl.HoverflyDsl
import io.specto.hoverfly.junit.dsl.RequestMatcherBuilder
import io.specto.hoverfly.junit.dsl.ResponseCreators
import io.specto.hoverfly.junit.dsl.matchers.HoverflyMatchers
import org.json.JSONObject
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.ResourceUtils
import spock.lang.Shared

import java.nio.file.Files

class ActivitySearchCompSpec extends BaseServiceCompSpec{

    @Shared
    String ACTIVITY_MICRO_HOST = "activity-log-host"

    @Shared
    String ACTIVITY_SEARCH_MICRO_URI = "/activity/search"

    @Shared
    String ACTIVITY_LOG_SEARCH_API_URI = "/admin/activity/search"

    File jsonFile = ResourceUtils.getFile("classpath:activityLog.json")
    String jsonContent = new String(Files.readAllBytes(jsonFile.toPath()))
    JSONObject jsonObject = new JSONObject(jsonContent)

    @Shared
    ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule())

    JSONObject activitySearchSuccessResponseJson = jsonObject.getJSONObject("activitySearchSuccessResponse")
    String activitySearchSuccessResponseString = OBJECT_MAPPER.writeValueAsString(activitySearchSuccessResponseJson)

    RequestMatcherBuilder activitySearchRequestMatchBuilder = HoverflyDsl
            .service(HoverflyMatchers.matches(ACTIVITY_MICRO_HOST))
            .post(ACTIVITY_SEARCH_MICRO_URI)

    SimulationSource activitySearchMicroSimulation = SimulationSource.dsl(
            activitySearchRequestMatchBuilder.anyBody().willReturn(ResponseCreators
                    .success(activitySearchSuccessResponseString, MediaType.APPLICATION_JSON_VALUE))
    )

    def "activity search api returns success response"(){
        setup:
        hoverFlyRule.simulate(activitySearchMicroSimulation)

        ActivitySearchRequest request = new ActivitySearchRequest(uuid: "TEST_UUID_1")

        when:
        ResponseEntity<String> responseEntity = callService(buildURI(ACTIVITY_LOG_SEARCH_API_URI, null),
                HttpMethod.POST, request)

        then:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.body
        Map bodyMap = new JsonSlurper().parseText(responseEntity.body)
        bodyMap.size() == 3
    }


}
