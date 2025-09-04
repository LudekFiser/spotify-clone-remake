package com.example.spotifycloneremade.utils.rateLimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int requests() default 10;           // Počet requestů
    int timeAmount() default 1;
    TimeUnit timeUnit() default TimeUnit.HOURS;  // MINUTES, HOURS, DAYS
    String keyPrefix() default "default"; // Prefix pro rozlišení endpointů
}
