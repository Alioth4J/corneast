package com.alioth4j.corneast_core.config;

import java.util.ArrayList;
import java.util.List;

public class RedisSentinelConfigProperties {

    private String master;

    private List<String> nodes = new ArrayList<>();


    public RedisSentinelConfigProperties() {
    }

    public RedisSentinelConfigProperties(String master, List<String> nodes) {
        this.master = master;
        this.nodes = nodes;
    }


    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

}
