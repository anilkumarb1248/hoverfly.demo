package com.hoverfly.demo.comptest.activitylogcontroller

import com.hoverfly.demo.comptest.BaseServiceCompSpec
import com.hoverfly.demo.enums.EventType
import com.hoverfly.demo.model.ActivitySearchRequest
import com.hoverfly.demo.model.EventStatsRequest
import io.specto.hoverfly.junit.dsl.HoverflyDsl
import io.specto.hoverfly.junit.dsl.RequestMatcherBuilder
import io.specto.hoverfly.junit.dsl.matchers.HoverflyMatchers
import org.json.JSONObject
import org.springframework.util.ResourceUtils
import spock.lang.Shared

import java.nio.file.Files
import java.time.LocalDate

class BaseActivityLogCompSpec extends BaseServiceCompSpec{

    @Shared
    String ACTIVITY_MICRO_BASE_URL = "activity.demo.com"
    // If we uses the localhost the hoverfly is not working (which is also configure in yaml file)
//    String ACTIVITY_MICRO_BASE_URL = "localhost:9091"

    @Shared
    String ACTIVITY_MICRO_SEARCH_ENDPOINT = "/activity/search"

    @Shared
    String ACTIVITY_MICRO_EVENT_STATS_ENDPOINT = "/report/event/stats"

    File jsonFile = ResourceUtils.getFile("classpath:activityLog.json")
    String jsonContent = new String(Files.readAllBytes(jsonFile.toPath()))
    JSONObject jsonObject = new JSONObject(jsonContent)

    String activitySearchSuccessResponseMock = jsonObject.getJSONObject("activitySearchSuccessResponse").toString()
    String activitySearchFailureResponseMock = jsonObject.getJSONObject("activitySearchFailureResponse").toString()

    String activityEventStatsSuccessResponseMock = jsonObject.getJSONObject("activityEventStatsSuccessResponse").toString()
    String activityEventStatsFailureResponseMock = jsonObject.getJSONObject("activityEventStatsFailureResponse").toString()


    @Shared
    ActivitySearchRequest activitySearchSuccessRequest = new ActivitySearchRequest(agentId: "testId", uuid: "testUuid", userName: "testUserName", eventType: EventType.AGENT)

    @Shared
    ActivitySearchRequest activitySearchFailureRequest = new ActivitySearchRequest(agentId: "DUMMY_AGENT", uuid: "DUMMY_UUID", userName: "DUMMY_USER", eventType: EventType.USER)

    @Shared
    EventStatsRequest activityEventStatsSuccessRequest = new EventStatsRequest(startDate: LocalDate.parse("2023-01-01"), endDate: LocalDate.parse("2023-03-03"), eventType: EventType.AGENT)

    @Shared
    EventStatsRequest activityEventStatsFailureRequest = new EventStatsRequest(startDate: LocalDate.parse("2022-01-01"), endDate: LocalDate.parse("2022-03-03"), eventType: EventType.USER)

    RequestMatcherBuilder activitySearchRequestMatchBuilder = HoverflyDsl
            .service(HoverflyMatchers.matches(ACTIVITY_MICRO_BASE_URL)).post(ACTIVITY_MICRO_SEARCH_ENDPOINT)
//            .header(HttpHeaders.ACCEPT.toString(), MediaType.APPLICATION_JSON_VALUE)
//            .header(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE)


    RequestMatcherBuilder activityEventStatsRequestMatchBuilder = HoverflyDsl
            .service(HoverflyMatchers.matches(ACTIVITY_MICRO_BASE_URL)).post(ACTIVITY_MICRO_EVENT_STATS_ENDPOINT)



    //If we configure the matchers in single dsl, hoverfly is not able to match the body. giving body is matching (body: [1st match, 2nd match]
    // If we configure only one matcher it is working fine.

//    SimulationSource activitySearchMicroSimulation = SimulationSource.dsl(
//
//            activitySearchRequestMatchBuilder.body(HoverflyMatchers.equalsToJson(HttpBodyConverter.json(activitySearchSuccessRequest)))
//                    .willReturn(ResponseCreators.success(activitySearchSuccessResponseMock, MediaType.APPLICATION_JSON_VALUE)),
//
//            activitySearchRequestMatchBuilder.body(HoverflyMatchers.equalsToJson(HttpBodyConverter.json(activitySearchFailureRequest)))
//                    .willReturn(ResponseCreators.success(activitySearchFailureResponseMock, MediaType.APPLICATION_JSON_VALUE)),
//
//    )
}
