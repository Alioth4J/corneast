/*
 * Corneast
 * Copyright (C) 2025-2026 Alioth Null
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

package com.alioth4j.corneast.client.send;

import com.alioth4j.corneast.client.config.CorneastConfig;
import com.alioth4j.corneast.client.eureka.EurekaConsumer;
import com.alioth4j.corneast.client.request.CorneastRequest;
import com.alioth4j.corneast.common.algo.RandomSelector;
import com.alioth4j.corneast.common.algo.Selector;
import com.alioth4j.corneast.common.operation.CorneastOperation;
import com.alioth4j.corneast.common.proto.RequestProto;
import com.alioth4j.corneast.common.proto.ResponseProto;
import com.netflix.appinfo.InstanceInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class CorneastNioClientTests {

    @Test
    void testSend() {
        RequestProto.RequestDTO registerReqDTO = new CorneastRequest(CorneastOperation.REGISTER, "", "key-register", 1000).instance;

        EurekaConsumer eurekaConsumer = new EurekaConsumer();
        List<InstanceInfo> instanceInfoList = eurekaConsumer.getInstanceInfos();
        Selector<InstanceInfo> selector = new RandomSelector<>();
        InstanceInfo instanceInfo = selector.select(instanceInfoList);

        CorneastConfig config = new CorneastConfig();
        config.setHost(instanceInfo.getHostName());
        config.setPort(instanceInfo.getPort());

        CorneastNioClient corneastNioClient = CorneastNioClient.of(config);

        ResponseProto.ResponseDTO responseDTO = null;
        try {
            responseDTO = corneastNioClient.send(registerReqDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(CorneastOperation.REGISTER, responseDTO.getType());
        Assertions.assertEquals("", responseDTO.getId());
        Assertions.assertEquals("key-register", responseDTO.getRegisterRespDTO().getKey());
        Assertions.assertEquals(true, responseDTO.getRegisterRespDTO().getSuccess());
    }

}
