package com.alioth4j.corneast_core.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterRespDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Boolean success;

}
