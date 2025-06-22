package com.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class TestConfig {

    public static String baseUrl;
    public static String browserType;
    public static Boolean tracing;
    private static Environment environment;

    @Autowired
    public void setEnvironment(Environment env) {
        environment = env;
    }

    @PostConstruct
    public void init() {
        baseUrl = environment.getProperty("baseUrl", "http://localhost:8080");
        browserType = environment.getProperty("browser", "chrome");
        tracing = Boolean.parseBoolean(environment.getProperty("tracing"));
    }
}
