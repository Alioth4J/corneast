package com.alioth4j.corneast_core.service;

import com.alioth4j.corneast_core.pojo.RegisterReqDTO;
import com.alioth4j.corneast_core.pojo.RegisterRespDTO;

public interface RegisterService {

    RegisterRespDTO register(RegisterReqDTO registerReqDTO);

}
