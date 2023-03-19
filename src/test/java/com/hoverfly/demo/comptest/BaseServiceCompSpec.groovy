package com.hoverfly.demo.comptest

import com.hoverfly.demo.Application
import io.specto.hoverfly.junit.core.Hoverfly
import io.specto.hoverfly.junit.core.HoverflyConfig
import io.specto.hoverfly.junit.core.HoverflyMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Shared
import spock.lang.Specification


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                classes = Application.class )
class BaseServiceCompSpec extends Specification {

    @Shared
//    HoverflyConfig localConfig = HoverflyConfig.localConfigs().disableTlsVerification().asWebServer().proxyPort(9090)
    HoverflyConfig localConfig = HoverflyConfig.localConfigs()

    @Shared
    Hoverfly hoverFly = new Hoverfly(localConfig, HoverflyMode.SIMULATE)


    @Autowired
    TestRestTemplate restTemplate

    @LocalServerPort
    int port

    @Shared
    String CONTEXT_PATH = "/HoverFlyDemo"

    def setupSpec(){
        hoverFly.start()
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
