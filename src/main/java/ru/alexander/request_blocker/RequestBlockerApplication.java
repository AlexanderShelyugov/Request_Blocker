package ru.alexander.request_blocker;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.alexander.request_blocker.blocking.LimitIPConfiguration;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@Import(LimitIPConfiguration.class)
public class RequestBlockerApplication {
    public static void main(String[] args) {
        run(RequestBlockerApplication.class, args);
    }
}
