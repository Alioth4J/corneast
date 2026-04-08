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
    void testSendRegister() {
        RequestProto.RequestDTO registerReqDTO = new CorneastRequest(CorneastOperation.REGISTER, "", "CorneastNioClient#testSendRegister", 1000).instance;

        EurekaConsumer eurekaConsumer = new EurekaConsumer();
        List<InstanceInfo> instanceInfoList = eurekaConsumer.getInstanceInfos();
        Selector<InstanceInfo> selector = new RandomSelector<>(instanceInfoList);
        InstanceInfo instanceInfo = selector.select();

        CorneastConfig config = new CorneastConfig();
        config.setHost(instanceInfo.getHostName());
        config.setPort(instanceInfo.getPort());

        ResponseProto.ResponseDTO responseDTO = null;
        try (CorneastNioClient corneastNioClient = CorneastNioClient.of(config)) {
            responseDTO = corneastNioClient.send(registerReqDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(CorneastOperation.REGISTER, responseDTO.getType());
        Assertions.assertEquals("", responseDTO.getId());
        Assertions.assertEquals("CorneastNioClient#testSendRegister", responseDTO.getRegisterRespDTO().getKey());
        Assertions.assertEquals(true, responseDTO.getRegisterRespDTO().getSuccess());
    }

    @Test
    void testSendReduce() {
        RequestProto.RequestDTO registerReqDTO = new CorneastRequest(CorneastOperation.REGISTER, "", "CorneastNioClient#testSendReduce", 1000).instance;
        RequestProto.RequestDTO reduceReqDTO = new CorneastRequest(CorneastOperation.REDUCE, "", "CorneastNioClient#testSendReduce").instance;

        EurekaConsumer eurekaConsumer = new EurekaConsumer();
        List<InstanceInfo> instanceInfoList = eurekaConsumer.getInstanceInfos();
        Selector<InstanceInfo> selector = new RandomSelector<>(instanceInfoList);
        InstanceInfo instanceInfo = selector.select();

        CorneastConfig config = new CorneastConfig();
        config.setHost(instanceInfo.getHostName());
        config.setPort(instanceInfo.getPort());

        try (CorneastNioClient corneastNioClient = CorneastNioClient.of(config)) {
            corneastNioClient.send(registerReqDTO);
            for (int i = 0; i < 100; i++) {
                ResponseProto.ResponseDTO responseDTO = corneastNioClient.send(reduceReqDTO);

                Assertions.assertEquals(CorneastOperation.REDUCE, responseDTO.getType());
                Assertions.assertEquals("", responseDTO.getId());
                Assertions.assertEquals("CorneastNioClient#testSendReduce", responseDTO.getReduceRespDTO().getKey());
                Assertions.assertEquals(true, responseDTO.getReduceRespDTO().getSuccess());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testIdempotent() {
        RequestProto.RequestDTO registerReqDTO = new CorneastRequest(CorneastOperation.REGISTER, "", "CorneastNioClient#testIdempotent", 1000).instance;
        RequestProto.RequestDTO reduceReqDTO = new CorneastRequest(CorneastOperation.REDUCE, "CorneastNioClient#testIdempotent", "CorneastNioClient#testIdempotent").instance;

        EurekaConsumer eurekaConsumer = new EurekaConsumer();
        List<InstanceInfo> instanceInfoList = eurekaConsumer.getInstanceInfos();
        Selector<InstanceInfo> selector = new RandomSelector<>(instanceInfoList);
        InstanceInfo instanceInfo = selector.select();

        CorneastConfig config = new CorneastConfig();
        config.setHost(instanceInfo.getHostName());
        config.setPort(instanceInfo.getPort());

        try (CorneastNioClient corneastNioClient = CorneastNioClient.of(config)) {
            corneastNioClient.send(registerReqDTO);
            // first
            ResponseProto.ResponseDTO responseDTO = corneastNioClient.send(reduceReqDTO);

            Assertions.assertEquals(CorneastOperation.REDUCE, responseDTO.getType());
            Assertions.assertEquals("CorneastNioClient#testIdempotent", responseDTO.getId());
            Assertions.assertEquals("CorneastNioClient#testIdempotent", responseDTO.getReduceRespDTO().getKey());
            Assertions.assertEquals(true, responseDTO.getReduceRespDTO().getSuccess());

            // second
            ResponseProto.ResponseDTO idempotentedResponseDTO = corneastNioClient.send(reduceReqDTO);

            Assertions.assertEquals(CorneastOperation.REDUCE, idempotentedResponseDTO.getType());
            Assertions.assertEquals("CorneastNioClient#testIdempotent", idempotentedResponseDTO.getId());
            Assertions.assertEquals("CorneastNioClient#testIdempotent", idempotentedResponseDTO.getReduceRespDTO().getKey());
            Assertions.assertEquals(true, idempotentedResponseDTO.getReduceRespDTO().getSuccess());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSendRelease() {
        RequestProto.RequestDTO releaseReqDTO = new CorneastRequest(CorneastOperation.RELEASE, "", "CorneastNioClient#testSendRelease").instance;

        EurekaConsumer eurekaConsumer = new EurekaConsumer();
        List<InstanceInfo> instanceInfoList = eurekaConsumer.getInstanceInfos();
        Selector<InstanceInfo> selector = new RandomSelector<>(instanceInfoList);
        InstanceInfo instanceInfo = selector.select();

        CorneastConfig config = new CorneastConfig();
        config.setHost(instanceInfo.getHostName());
        config.setPort(instanceInfo.getPort());

        try (CorneastNioClient corneastNioClient = CorneastNioClient.of(config)) {
            for (int i = 0; i < 100; i++) {
                ResponseProto.ResponseDTO responseDTO = corneastNioClient.send(releaseReqDTO);
                Assertions.assertEquals(CorneastOperation.RELEASE, responseDTO.getType());
                Assertions.assertEquals("", responseDTO.getId());
                Assertions.assertEquals("CorneastNioClient#testSendRelease", responseDTO.getReleaseRespDTO().getKey());
                Assertions.assertEquals(true, responseDTO.getReleaseRespDTO().getSuccess());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSendQuery() {
        RequestProto.RequestDTO registerReqDTO = new CorneastRequest(CorneastOperation.REGISTER, "", "CorneastNioClient#testSendQuery", 1000).instance;
        RequestProto.RequestDTO queryReqDTO = new CorneastRequest(CorneastOperation.QUERY, "", "CorneastNioClient#testSendQuery").instance;

        EurekaConsumer eurekaConsumer = new EurekaConsumer();
        List<InstanceInfo> instanceInfoList = eurekaConsumer.getInstanceInfos();
        Selector<InstanceInfo> selector = new RandomSelector<>(instanceInfoList);
        InstanceInfo instanceInfo = selector.select();

        CorneastConfig config = new CorneastConfig();
        config.setHost(instanceInfo.getHostName());
        config.setPort(instanceInfo.getPort());

        ResponseProto.ResponseDTO responseDTO = null;
        try (CorneastNioClient corneastNioClient = CorneastNioClient.of(config)) {
            corneastNioClient.send(registerReqDTO);
            responseDTO = corneastNioClient.send(queryReqDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(CorneastOperation.QUERY, responseDTO.getType());
        Assertions.assertEquals("", responseDTO.getId());
        Assertions.assertEquals("CorneastNioClient#testSendQuery", responseDTO.getQueryRespDTO().getKey());
        Assertions.assertEquals(1000L, responseDTO.getQueryRespDTO().getRemainingTokenCount());
    }

}
