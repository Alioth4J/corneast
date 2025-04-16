package com.alioth4j.corneast_core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class of Redis nodes count, corresponding with application.yml
 *
 * @author Alioth Null
 */
@ConfigurationProperties(prefix = "redis-config")
public class RedisConfigProperties {

    private List<RedisNodeProperties> nodes = new ArrayList<>();


    // ----- getters and setters -----

    public List<RedisNodeProperties> getNodes() {
        return nodes;
    }

    public void setNodes(List<RedisNodeProperties> nodes) {
        this.nodes = nodes;
    }

}
