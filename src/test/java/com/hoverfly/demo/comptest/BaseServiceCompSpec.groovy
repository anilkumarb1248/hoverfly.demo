package com.hoverfly.demo.comptest

import com.hoverfly.demo.Application
import io.specto.hoverfly.junit.api.view.HoverflyInfoView
import io.specto.hoverfly.junit.core.Hoverfly
import io.specto.hoverfly.junit.core.HoverflyConfig
import io.specto.hoverfly.junit.rule.HoverflyRule
import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
                classes = Application.class )
class BaseServiceCompSpec extends Specification {

//    HoverflyConfig hoverflyConfig = HoverflyConfig.localConfigs().adminPort(8080).proxyPort(8080)
//    HoverflyConfig hoverflyConfig = HoverflyConfig.localConfigs()

    @Autowired
    TestRestTemplate restTemplate

    @LocalServerPort
    int port

    @Shared
    String CONTEXT_PATH = "/HoverFlyDemo"

    @Shared
    @ClassRule
    HoverflyRule hoverFlyRule = HoverflyRule.inSimulationMode()

    def setup(){
//        hoverFlyRule.resetJournal()

        HoverflyConfig hoverflyConfig = HoverflyConfig.localConfigs()
        hoverflyConfig.upstreamProxy(new InetSocketAddress("127.0.0.1", 8900)).logToStdOut().build()
//        Hoverfly hoverfly = new Hoverfly(localConfigs().upstreamProxy(new InetSocketAddress("127.0.0.1", 8900)).logToStdOut(), SIMULATE)

        hoverflyConfig.getProperties()
//        HoverflyInfoView hoverflyInfo = hoverfly.getHoverflyInfo();
        print("***************** " + hoverflyConfig.getProperties())
//        assertThat(hoverflyInfo.getUpstreamProxy()).isEqualTo("http://127.0.0.1:8900");
    }

    URI buildUri(String resource) {
        return buildUri(resource, null)
    }

    URI buildURI(String resource, MultiValueMap<String, String> queryParams) {
        return UriComponentsBuilder.fromUriString("http://localhost:" + port + CONTEXT_PATH + resource)
                .queryParams(queryParams)
                .build(false)
                .encode()
                .toUri()
    }

    ResponseEntity<String> callService(URI uri, HttpMethod httpMethod,
                                       HttpHeaders httpHeaders, Object body) {
        return restTemplate.exchange(uri, httpMethod, new HttpEntity(body, httpHeaders), String)
    }

    ResponseEntity<String> callService(URI uri, HttpMethod httpMethod, Object body) {
        HttpHeaders httpHeaders = new HttpHeaders()
        httpHeaders.setAccept([MediaType.APPLICATION_JSON])
        if (body != null) {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON)
        }
        return callService(uri, httpMethod, httpHeaders, body)
    }

}
