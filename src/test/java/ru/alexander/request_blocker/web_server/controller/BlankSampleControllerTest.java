package ru.alexander.request_blocker.web_server.controller;

import lombok.val;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.ResourceLock;
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
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.generate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static ru.alexander.request_blocker.util.HttpRequestsHelper.runHTTPRequests;

@SpringBootTest(webEnvironment = MOCK)
@Import(LimitIPConfiguration.class)
@ActiveProfiles({"sample-controller-tests", "storage-simple"})
@AutoConfigureMockMvc
class BlankSampleControllerTest {
    private static final String URI = "/sample_ip_protected";
    // We can't create too many threads because of OS limitations.
    private static final int REQUESTS_AT_ONCE = 1000;
    // We can't create too many random IP either. Especially, IPv6.
    // That may create out of memory error.
    private static final int TOTAL_REQUESTS = REQUESTS_AT_ONCE * 30;
    private static final int TIME_LIMIT_SECONDS = 15;

    private static final String RESOURCE_LOCK_NAME = "requests-thread-pool";

    @Value("${block_ip.requests.limit}")
    private int expectedSuccesses;

    @Autowired
    private MockMvc mockMvc;

    private static ExecutorService pool;

    @BeforeAll
    static void setUp() {
        pool = newFixedThreadPool(REQUESTS_AT_ONCE);
    }

    @AfterAll
    static void stop() {
        pool.shutdown();
    }

    @AfterEach
    void tearDown() {
        System.gc();
    }

    @Test
    @DisplayName("Context loads successfully!")
    void contextLoads() {
        assertThat(mockMvc, is(notNullValue()));
    }

    @IPLimitTest
    @DisplayName("Same IP v4 requests fail after limit")
    void callWithSameIPv4() throws Exception {
        val tasks = sameIpv4Tasks(TOTAL_REQUESTS);
        // Execute requests
        val results = runHTTPRequests(pool, tasks);
        assertEquals(expectedSuccesses, results.getSuccessful());
        assertEquals(TOTAL_REQUESTS - expectedSuccesses, results.getFailed());
    }

    @IPLimitTest
    @DisplayName("Same IP v6 requests fail after limit")
    void callWithSameIPv6() throws Exception {
        val tasks = sameIpv6Tasks(TOTAL_REQUESTS);
        // Execute requests
        val results = runHTTPRequests(pool, tasks);
        assertEquals(expectedSuccesses, results.getSuccessful());
        assertEquals(TOTAL_REQUESTS - expectedSuccesses, results.getFailed());
    }

    @IPLimitTest
    @DisplayName("Unique IP requests work fine")
    void callWithUniqueIPs() throws Exception {
        val tasks = uniqueRandomIPTasks(TOTAL_REQUESTS);
        // Execute requests
        val results = runHTTPRequests(pool, tasks);
        assertEquals(TOTAL_REQUESTS, results.getSuccessful());
        assertEquals(0, results.getFailed());
    }

    @IPLimitTest
    @DisplayName("Unique IPv4 requests work fine")
    void callWithUniqueIPv4s() throws Exception {
        val tasks = uniqueIPTasks(TOTAL_REQUESTS, IpAddressHelper::randomIPv4Address);
        // Execute requests
        val results = runHTTPRequests(pool, tasks);
        assertEquals(TOTAL_REQUESTS, results.getSuccessful());
        assertEquals(0, results.getFailed());
    }

    @IPLimitTest
    @DisplayName("Unique IPv6 requests work fine")
    void callWithUniqueIPv6s() throws Exception {
        val tasks = uniqueIPTasks(TOTAL_REQUESTS, IpAddressHelper::randomIPv6Address);
        // Execute requests
        val results = runHTTPRequests(pool, tasks);
        assertEquals(TOTAL_REQUESTS, results.getSuccessful());
        assertEquals(0, results.getFailed());
    }

    public List<Callable<HttpServletResponse>> sameIpv4Tasks(int n) {
        return sameIPTasks(n, IpAddressHelper::randomIPv4Address);
    }

    public List<Callable<HttpServletResponse>> sameIpv6Tasks(int n) {
        return sameIPTasks(n, IpAddressHelper::randomIPv6Address);
    }

    public List<Callable<HttpServletResponse>> uniqueRandomIPTasks(int n) {
        return uniqueIPTasks(n, IpAddressHelper::randomIPAddress);
    }

    public List<Callable<HttpServletResponse>> sameIPTasks(int n, Supplier<String> ipGenerator) {
        val ip = ipGenerator.get();
        return generate(() -> new IPRequestTask(mockMvc, URI, ip))
                   .limit(n)
                   .collect(toList());
    }

    public List<Callable<HttpServletResponse>> uniqueIPTasks(int n, Supplier<String> ipProvider) {
        return generate(ipProvider)
                   .distinct()
                   .limit(n)
                   .map(ip -> new IPRequestTask(mockMvc, URI, ip))
                   .collect(toList());
    }

    /**
     * The shortcut for the common annotations set
     */
    @Target(METHOD)
    @Retention(RUNTIME)
    @Test
    @Timeout(value = TIME_LIMIT_SECONDS)
    @ResourceLock(RESOURCE_LOCK_NAME)
    @interface IPLimitTest {
    }

}