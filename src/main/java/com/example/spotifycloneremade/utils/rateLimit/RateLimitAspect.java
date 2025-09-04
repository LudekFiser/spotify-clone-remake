package com.example.spotifycloneremade.utils.rateLimit;

import com.example.spotifycloneremade.exception.TooManyAttemptsException;
import com.example.spotifycloneremade.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {

    //@Autowired
    private final RateLimitService rateLimitService;

    //@Autowired
    private final AuthService authService;

    @Around("@annotation(rateLimit)")
    public Object handleRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {

        var currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        // Create unique key
        String key = rateLimit.keyPrefix() + ":" + currentUser.getId();

        // Convert TimeUnit to Duration
        Duration duration = Duration.of(rateLimit.timeAmount(),
                convertToChronoUnit(rateLimit.timeUnit()));

        // Check rate limit
        if (!rateLimitService.isAllowed(key, rateLimit.requests(), duration)) {
            String timeText = formatTimeUnit(rateLimit.timeAmount(), rateLimit.timeUnit());
            throw new TooManyAttemptsException(
                    String.format("Too many requests. Maximum %d requests per %s allowed.",
                            rateLimit.requests(), timeText)
            );
            //throw new TooManyAttemptsException(rateLimit.requests(), rateLimit.timeAmount(), rateLimit.timeUnit());
        }

        log.info("Rate limit check passed for key: {}", key);

        // Continue with previous method
        return joinPoint.proceed();
    }

    private ChronoUnit convertToChronoUnit(TimeUnit timeUnit) {
        return switch (timeUnit) {
            case MINUTES -> ChronoUnit.MINUTES;
            case HOURS -> ChronoUnit.HOURS;
            case DAYS -> ChronoUnit.DAYS;
            case SECONDS -> ChronoUnit.SECONDS;
            default -> ChronoUnit.HOURS;
        };
    }

    private String formatTimeUnit(int amount, TimeUnit unit) {
        String unitText = switch (unit) {
            case MINUTES -> amount == 1 ? "minute" : "minutes";
            case HOURS -> amount == 1 ? "hour" : "hours";
            case DAYS -> amount == 1 ? "day" : "days";
            case SECONDS -> amount == 1 ? "second" : "seconds";
            default -> "hours";
        };
        return amount + " " + unitText;
    }
}