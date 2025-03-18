package com.alioth4j.corneast_core.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryRespDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;

    private Integer remainingTokens;

}
