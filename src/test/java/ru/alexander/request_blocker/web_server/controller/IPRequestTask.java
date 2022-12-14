package ru.alexander.request_blocker.web_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.Callable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RequiredArgsConstructor
class IPRequestTask implements Callable<HttpServletResponse> {
    private final MockMvc mockMvc;
    private final String url;
    private final String ip;

    @Override
    public HttpServletResponse call() throws Exception {
        return mockMvc.perform(
                get(url)
                    .with(request -> {
                        request.setRemoteAddr(ip);
                        return request;
                    })
            )
            .andReturn()
            .getResponse();
    }
}
