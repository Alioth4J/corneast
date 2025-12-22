/*
 * Corneast
 * Copyright (C) 2025 Alioth Null
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alioth4j.corneast_client.send;

import com.alioth4j.corneast_client.config.CorneastConfig;
import com.alioth4j.corneast_client.exception.RequestBuildException;
import com.alioth4j.corneast_client.request.CorneastRequest;
import com.alioth4j.corneast_common.operation.CorneastOperation;
import com.alioth4j.corneast_common.proto.RequestProto;
import com.alioth4j.corneast_common.proto.ResponseProto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CorneastBioClientTests {

    @Test
    void testSend() {
        RequestProto.RequestDTO registerReqDTO = new CorneastRequest(CorneastOperation.REGISTER, "", "key-register", 1000).instance;
        CorneastConfig config = new CorneastConfig();
        config.setHost("127.0.0.1");
        config.setPort(8088);
        CorneastBioClient corneastBioClient = CorneastBioClient.of(config);
        ResponseProto.ResponseDTO responseDTO = null;
        try {
            responseDTO = corneastBioClient.send(registerReqDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(CorneastOperation.REGISTER, responseDTO.getType());
        Assertions.assertEquals("", responseDTO.getId());
        Assertions.assertEquals("key-register", responseDTO.getRegisterRespDTO().getKey());
        Assertions.assertEquals(true, responseDTO.getRegisterRespDTO().getSuccess());
    }

    @Test
    void testUnknownTypeWithClientAPI() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            RequestProto.RequestDTO requestDTO = new CorneastRequest("abcdefg", "", "key-unknown").instance;
            CorneastConfig config = new CorneastConfig();
            config.setHost("127.0.0.1");
            config.setPort(8088);
            CorneastBioClient corneastBioClient = CorneastBioClient.of(config);
            ResponseProto.ResponseDTO responseDTO = null;
            try {
                responseDTO = corneastBioClient.send(requestDTO);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Assertions.assertEquals(CorneastOperation.UNKNOWN, responseDTO.getType());
            Assertions.assertEquals("", responseDTO.getId());
        });
    }

    @Test
    void testUnknownTypeWithProtobufAPI() {
        RequestProto.RequestDTO requestDTO = RequestProto.RequestDTO.newBuilder()
                .setType("abcdefg")
                .setId("")
                .build();
        CorneastConfig config = new CorneastConfig();
        config.setHost("127.0.0.1");
        config.setPort(8088);
        CorneastBioClient corneastBioClient = CorneastBioClient.of(config);
        ResponseProto.ResponseDTO responseDTO = null;
        try {
            responseDTO = corneastBioClient.send(requestDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(CorneastOperation.UNKNOWN, responseDTO.getType());
        Assertions.assertEquals("", responseDTO.getId());
    }

}
