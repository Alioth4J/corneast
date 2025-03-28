package com.alioth4j.corneast_core.service;

import com.alioth4j.corneast_core.pojo.RegisterReqDTO;
import com.alioth4j.corneast_core.pojo.RegisterRespDTO;
import com.alioth4j.corneast_core.proto.RegisterProto;

public interface RegisterService {

    RegisterProto.RegisterRespDTO register(RegisterProto.RegisterReqDTO registerReqDTO);

}
