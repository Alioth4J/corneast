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
