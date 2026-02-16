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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CorneastAioClientTests {

    @Test
    void testSend() {
        RequestProto.RequestDTO registerReqDTO = new CorneastRequest(CorneastOperation.REGISTER, "", "key-register", 1000).instance;

        EurekaConsumer eurekaConsumer = new EurekaConsumer();
        List<InstanceInfo> instanceInfoList = eurekaConsumer.getInstanceInfos();
        Selector<InstanceInfo> selector = new RandomSelector<>(instanceInfoList);
        InstanceInfo instanceInfo = selector.select();


        CorneastConfig config = new CorneastConfig();
        config.setHost(instanceInfo.getHostName());
        config.setPort(instanceInfo.getPort());

        CorneastAioClient corneastAioClient = CorneastAioClient.of(config);

        CompletableFuture<ResponseProto.ResponseDTO> responseFuture = null;
        ResponseProto.ResponseDTO responseDTO = null;
        try {
            responseFuture = corneastAioClient.send(registerReqDTO);
            responseDTO = responseFuture.get();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }

        Assertions.assertEquals(CorneastOperation.REGISTER, responseDTO.getType());
        Assertions.assertEquals("", responseDTO.getId());
        Assertions.assertEquals("key-register", responseDTO.getRegisterRespDTO().getKey());
        Assertions.assertEquals(true, responseDTO.getRegisterRespDTO().getSuccess());
    }

}
