package com.playwrightdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@SpringBootTest(classes = PlaywrightDemoApplication.class)
public abstract class PlaywrightDemoApplicationTests extends AbstractTestNGSpringContextTests {

    @Test
    void contextLoads() {
    }

}
