package com.alioth4j.corneast_core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "idempotent")
@Data
public class IdempotentConfigProperties {

    private List<String> redisAddresses;

}
