package com.alioth4j.corneast_core.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeInitializer implements CommandLineRunner {

    @Value("${snowflake.workerId}")
    private long workerId;

    @Value("${snowflake.datacenterId}")
    private long datacenterId;

    @Override
    public void run(String... args) throws Exception {
        SnowflakeIdUtil.init(workerId, datacenterId);
    }

}
