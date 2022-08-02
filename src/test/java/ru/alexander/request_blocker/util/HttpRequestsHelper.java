package ru.alexander.request_blocker.util;

import lombok.Value;
import lombok.val;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class HttpRequestsHelper {
    public static RequestCounts runHTTPRequests(
        ExecutorService pool,
        List<? extends Callable<HttpServletResponse>> tasks
    ) throws InterruptedException {
        // Execute requests
        val results = pool.invokeAll(tasks);

        // Count results
        val successful = new AtomicInteger();
        val failed = new AtomicInteger();
        iterateOverResults(results, response -> {
            AtomicInteger counter = response.getStatus() == HttpStatus.OK.value()
                ? successful
                : failed;
            counter.incrementAndGet();
        });
        return new RequestCounts(successful.get(), failed.get());
    }

    public static void iterateOverResults(List<Future<HttpServletResponse>> results,
                                          Consumer<HttpServletResponse> action) {
        results.stream()
            .map(result -> {
                try {
                    return result.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            })
            .forEach(action);
    }

    @Value
    public static class RequestCounts {
        int successful;
        int failed;
    }

    private HttpRequestsHelper() {
    }
}
