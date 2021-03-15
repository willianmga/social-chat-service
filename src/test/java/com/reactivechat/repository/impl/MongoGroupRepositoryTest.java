package com.reactivechat.repository.impl;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import static org.junit.jupiter.api.Assertions.*;

public class MongoGroupRepositoryTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoGroupRepositoryTest.class);
    
    @Test
    public void test() {
    
        System.out.println("started");
        
        Callable<String> callable = () -> {
            Thread.sleep(5000);
            System.out.println("finishedddd....");
            return "finished";
        };
        
        Mono.fromCallable(callable)
            .publishOn(Schedulers.boundedElastic())
            .doOnEach(result -> System.out.println("result is: " + result))
            //.subscribe();
            
            .subscribe(result -> System.out.println("result is: " + result));
    
        System.out.println("finished");
    
//        while (true) {
//
//        }
//
    }
    
    
    public static void main(String[] args) {
    
        LOGGER.info("started");
        
        Flux.interval(Duration.ofSeconds(1))
            .take(10)
            .subscribe(value -> LOGGER.info("Thread: {}, Value: {}", Thread.currentThread().getName(), value));
    
        LOGGER.info("finished");
        
//        while (true) {
//
//        }
//
    }
    
}