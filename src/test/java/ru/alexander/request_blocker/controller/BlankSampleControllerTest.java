package ru.alexander.request_blocker.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static ru.alexander.request_blocker.util.IpAddressUtils.randomIPAddress;

@WebMvcTest(BlankSampleController.class)
class BlankSampleControllerTest {
    private static final String URI = "/sample";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Context loads successfully!")
    void contextLoads() {
        assertThat(mockMvc, is(notNullValue()));
    }

    @Test
    @DisplayName("We call method from the same IP address and get error")
    void callWithSameIP() throws Exception {
        mockMvc.perform(
            get(URI)
                .with(request -> {
                    request.setRemoteAddr(randomIPAddress());
                    return request;
                })
                .accept(APPLICATION_JSON)
        );
    }
}