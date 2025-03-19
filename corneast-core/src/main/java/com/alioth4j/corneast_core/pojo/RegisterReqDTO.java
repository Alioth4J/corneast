package com.alioth4j.corneast_core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RegisterReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;

    private Integer tokenCount;

}
