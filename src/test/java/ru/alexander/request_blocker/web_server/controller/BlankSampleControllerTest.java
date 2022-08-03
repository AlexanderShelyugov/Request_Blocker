package ru.alexander.request_blocker.web_server.controller;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.alexander.request_blocker.blocking.LimitIPConfiguration;
import ru.alexander.request_blocker.util.IpAddressHelper;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.generate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static ru.alexander.request_blocker.util.HttpRequestsHelper.runHTTPRequests;
import static ru.alexander.request_blocker.util.IpAddressHelper.randomIPv4Address;
import static ru.alexander.request_blocker.util.IpAddressHelper.randomIPv6Address;

@SpringBootTest(
    webEnvironment = MOCK
)
@Import(LimitIPConfiguration.class)
@ActiveProfiles({"sample-controller-tests", "storage-simple"})
@AutoConfigureMockMvc
class BlankSampleControllerTest {
    private static final String URI = "/sample_ip_protected";
    private static final int REQUESTS_AT_ONCE = 100;
    private static final int TOTAL_REQUESTS = REQUESTS_AT_ONCE * 20;
    private static final int TIME_LIMIT = 60;

    @Value("${block_ip.requests.amount}")
    private int expectedSuccesses;

    @Autowired
    private MockMvc mockMvc;

    private ExecutorService pool;

    @BeforeEach
    void setUp() {
        pool = newFixedThreadPool(REQUESTS_AT_ONCE);
    }

    @Test
    @DisplayName("Context loads successfully!")
    void contextLoads() {
        assertThat(mockMvc, is(notNullValue()));
    }

    @Test
    @DisplayName("Same IP v4 requests fail after limit")
    @Timeout(value = TIME_LIMIT / 2)
    void callWithSameIPv4() throws Exception {
        val tasks = sameIpv4Tasks(TOTAL_REQUESTS);
        // Execute requests
        val results = runHTTPRequests(pool, tasks);
        assertEquals(expectedSuccesses, results.getSuccessful());
        assertEquals(TOTAL_REQUESTS - expectedSuccesses, results.getFailed());
    }

    @Test
    @DisplayName("Same IP v6 requests fail after limit")
    @Timeout(value = TIME_LIMIT / 2)
    void callWithSameIPv6() throws Exception {
        val tasks = sameIpv6Tasks(TOTAL_REQUESTS);
        // Execute requests
        val results = runHTTPRequests(pool, tasks);
        assertEquals(expectedSuccesses, results.getSuccessful());
        assertEquals(TOTAL_REQUESTS - expectedSuccesses, results.getFailed());
    }


    @Test
    @DisplayName("Unique IP requests work fine")
    @Timeout(value = TIME_LIMIT / 2)
    void callWithUniqueIPs() throws Exception {
        val tasks = uniqueIpTasks(TOTAL_REQUESTS);
        // Execute requests
        val results = runHTTPRequests(pool, tasks);
        assertEquals(TOTAL_REQUESTS, results.getSuccessful());
        assertEquals(0, results.getFailed());
    }

    public List<Callable<HttpServletResponse>> sameIpv4Tasks(int n) {
        val ip = randomIPv4Address();
        return generate(() -> new RandomIPRequestTask(mockMvc, URI, ip))
            .limit(n)
            .collect(toList());
    }

    public List<Callable<HttpServletResponse>> sameIpv6Tasks(int n) {
        val ip = randomIPv6Address();
        return generate(() -> new RandomIPRequestTask(mockMvc, URI, ip))
            .limit(n)
            .collect(toList());
    }


    public List<Callable<HttpServletResponse>> uniqueIpTasks(int n) {
        return generate(IpAddressHelper::randomIPAddress)
            .distinct()
            .limit(n)
            .map(ip -> new RandomIPRequestTask(mockMvc, URI, ip))
            .collect(toList());
    }

}