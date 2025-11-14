package com.alioth4j.corneast_client.send;

import com.alioth4j.corneast_client.request.CorneastRequest;
import com.alioth4j.corneast_core.common.CorneastOperation;
import com.alioth4j.corneast_core.proto.RequestProto;
import com.alioth4j.corneast_core.proto.ResponseProto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CorneastSocketClientTests {

    @Test
    void testSend() throws IOException {
        RequestProto.RequestDTO registerReqDTO = new CorneastRequest(CorneastOperation.REGISTER, "", "key-register", 1000).instance;
        ResponseProto.ResponseDTO responseDTO = CorneastSocketClient.send(registerReqDTO);
        Assertions.assertEquals(CorneastOperation.REGISTER, responseDTO.getType());
        Assertions.assertEquals("", responseDTO.getId());
        Assertions.assertEquals("key-register", responseDTO.getRegisterRespDTO().getKey());
        Assertions.assertEquals(true, responseDTO.getRegisterRespDTO().getSuccess());
    }

}
