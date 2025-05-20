package com.alioth4j.corneast_core.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RedisSentinelConfigProperties {

    private String master;

    private List<String> nodes = new ArrayList<>();

}
