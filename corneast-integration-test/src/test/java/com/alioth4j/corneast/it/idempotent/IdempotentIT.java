/*
 * Corneast
 * Copyright (C) 2026 Alioth Null
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

package com.alioth4j.corneast.it.idempotent;

import com.alioth4j.corneast.client.config.CorneastConfig;
import com.alioth4j.corneast.client.eureka.EurekaConsumer;
import com.alioth4j.corneast.client.request.CorneastRequest;
import com.alioth4j.corneast.client.send.CorneastNioClient;
import com.alioth4j.corneast.common.algo.RandomSelector;
import com.alioth4j.corneast.common.algo.Selector;
import com.alioth4j.corneast.common.operation.CorneastOperation;
import com.alioth4j.corneast.common.proto.RequestProto;
import com.alioth4j.corneast.common.proto.ResponseProto;
import com.netflix.appinfo.InstanceInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class IdempotentIT {

    private CorneastNioClient client;
    private CorneastConfig config;

    @BeforeEach
    void setup() {
        EurekaConsumer eurekaConsumer = new EurekaConsumer();
        List<InstanceInfo> instanceInfoList = eurekaConsumer.getInstanceInfos();
        Selector<InstanceInfo> selector = new RandomSelector<>(instanceInfoList);
        InstanceInfo instanceInfo = selector.select();

        config = new CorneastConfig();
        config.setHost(instanceInfo.getHostName());
        config.setPort(instanceInfo.getPort());

        try {
            client = CorneastNioClient.of(config);
        } catch (IOException e) {
            throw new RuntimeException("Error creating client", e);
        }
    }

    @AfterEach
    void cleanup() {
        if (client != null) {
            client.close();
        }
    }


    @Test
    void testNotIdempotentedWithNullId() {
        String id = null;
        String key = "IdempotentIT#testNotIdempotentedWithNullId";
        RequestProto.RequestDTO registerReqDTO = new CorneastRequest(CorneastOperation.REGISTER, "", key, 100).instance;
        RequestProto.RequestDTO reduceReqDTO = new CorneastRequest(CorneastOperation.REDUCE, id, key).instance;
        RequestProto.RequestDTO queryReqDTO = new CorneastRequest(CorneastOperation.QUERY, "", key).instance;

        try {
            // register
            client.send(registerReqDTO);

            // first
            ResponseProto.ResponseDTO responseDTO = client.send(reduceReqDTO);
            Assertions.assertEquals(CorneastOperation.REDUCE, responseDTO.getType());
            // TODO <null> id will become <> because of protobuf
            Assertions.assertEquals("", responseDTO.getId());
            Assertions.assertEquals(key, responseDTO.getReduceRespDTO().getKey());
            Assertions.assertTrue(responseDTO.getReduceRespDTO().getSuccess());

            // second, not idempotented
            ResponseProto.ResponseDTO notIdempotentedResponseDTO = client.send(reduceReqDTO);
            Assertions.assertEquals(CorneastOperation.REDUCE, notIdempotentedResponseDTO.getType());
            Assertions.assertEquals("", notIdempotentedResponseDTO.getId());
            Assertions.assertEquals(key, notIdempotentedResponseDTO.getReduceRespDTO().getKey());
            Assertions.assertTrue(notIdempotentedResponseDTO.getReduceRespDTO().getSuccess());

            // assert redis data
            ResponseProto.ResponseDTO queryResponseDTO = client.send(queryReqDTO);
            Assertions.assertEquals(CorneastOperation.QUERY, queryResponseDTO.getType());
            Assertions.assertEquals("", queryResponseDTO.getId());
            Assertions.assertEquals(key, queryResponseDTO.getQueryRespDTO().getKey());
            Assertions.assertEquals(98, queryResponseDTO.getQueryRespDTO().getRemainingTokenCount());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testNotIdempotentedWithEmptyId() {
        String id = "";
        String key = "IdempotentIT#testNotIdempotentedWithEmptyId";
        RequestProto.RequestDTO registerReqDTO = new CorneastRequest(CorneastOperation.REGISTER, "", key, 100).instance;
        RequestProto.RequestDTO reduceReqDTO = new CorneastRequest(CorneastOperation.REDUCE, id, key).instance;
        RequestProto.RequestDTO queryReqDTO = new CorneastRequest(CorneastOperation.QUERY, "", key).instance;

        try {
            // register
            client.send(registerReqDTO);

            // first
            ResponseProto.ResponseDTO responseDTO = client.send(reduceReqDTO);
            Assertions.assertEquals(CorneastOperation.REDUCE, responseDTO.getType());
            Assertions.assertEquals(id, responseDTO.getId());
            Assertions.assertEquals(key, responseDTO.getReduceRespDTO().getKey());
            Assertions.assertTrue(responseDTO.getReduceRespDTO().getSuccess());

            // second, not idempotented
            ResponseProto.ResponseDTO notIdempotentedResponseDTO = client.send(reduceReqDTO);
            Assertions.assertEquals(CorneastOperation.REDUCE, notIdempotentedResponseDTO.getType());
            Assertions.assertEquals(id, notIdempotentedResponseDTO.getId());
            Assertions.assertEquals(key, notIdempotentedResponseDTO.getReduceRespDTO().getKey());
            Assertions.assertTrue(notIdempotentedResponseDTO.getReduceRespDTO().getSuccess());

            // assert redis data
            ResponseProto.ResponseDTO queryResponseDTO = client.send(queryReqDTO);
            Assertions.assertEquals(CorneastOperation.QUERY, queryResponseDTO.getType());
            Assertions.assertEquals(id, queryResponseDTO.getId());
            Assertions.assertEquals(key, queryResponseDTO.getQueryRespDTO().getKey());
            Assertions.assertEquals(98, queryResponseDTO.getQueryRespDTO().getRemainingTokenCount());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testIdempotentedWithSameId() {
        String id = "IdempotentIT#testIdempotentedWithSameId";
        String key = "IdempotentIT#testIdempotentedWithSameId";
        RequestProto.RequestDTO registerReqDTO = new CorneastRequest(CorneastOperation.REGISTER, "", key, 100).instance;
        RequestProto.RequestDTO reduceReqDTO = new CorneastRequest(CorneastOperation.REDUCE, id, key).instance;
        RequestProto.RequestDTO queryReqDTO = new CorneastRequest(CorneastOperation.QUERY, "", key).instance;

        try {
            client.send(registerReqDTO);

            // first
            ResponseProto.ResponseDTO responseDTO = client.send(reduceReqDTO);
            Assertions.assertEquals(CorneastOperation.REDUCE, responseDTO.getType());
            Assertions.assertEquals(id, responseDTO.getId());
            Assertions.assertEquals(key, responseDTO.getReduceRespDTO().getKey());
            Assertions.assertTrue(responseDTO.getReduceRespDTO().getSuccess());

            // second, idempotented
            ResponseProto.ResponseDTO idempotentedResponseDTO = client.send(reduceReqDTO);
            Assertions.assertEquals(CorneastOperation.REDUCE, idempotentedResponseDTO.getType());
            Assertions.assertEquals(id, idempotentedResponseDTO.getId());
            Assertions.assertEquals(key, idempotentedResponseDTO.getReduceRespDTO().getKey());
            Assertions.assertTrue(idempotentedResponseDTO.getReduceRespDTO().getSuccess());

            // assert redis data
            ResponseProto.ResponseDTO queryResponseDTO = client.send(queryReqDTO);
            Assertions.assertEquals(CorneastOperation.QUERY, queryResponseDTO.getType());
            Assertions.assertEquals("", queryResponseDTO.getId());
            Assertions.assertEquals(key, queryResponseDTO.getQueryRespDTO().getKey());
            Assertions.assertEquals(99, queryResponseDTO.getQueryRespDTO().getRemainingTokenCount());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSameResultsWhenIdempotented() {
        String id = "IdempotentIT#testSameResultsWhenIdempotented";
        String key = "IdempotentIT#testSameResultsWhenIdempotented";
        RequestProto.RequestDTO registerReqDTO = new CorneastRequest(CorneastOperation.REGISTER, "", key, 1000).instance;
        RequestProto.RequestDTO reduceReqDTO = new CorneastRequest(CorneastOperation.REDUCE, id, key).instance;

        try {
            // register
            client.send(registerReqDTO);
            // first
            ResponseProto.ResponseDTO responseDTO = client.send(reduceReqDTO);
            // second, idempotented
            ResponseProto.ResponseDTO idempotentedResponseDTO = client.send(reduceReqDTO);

            Assertions.assertEquals(responseDTO.getType(), idempotentedResponseDTO.getType());
            Assertions.assertEquals(responseDTO.getId(), idempotentedResponseDTO.getId());
            Assertions.assertEquals(responseDTO.getReduceRespDTO().getKey(), idempotentedResponseDTO.getReduceRespDTO().getKey());
            Assertions.assertTrue(responseDTO.getReduceRespDTO().getSuccess());
            Assertions.assertTrue(idempotentedResponseDTO.getReduceRespDTO().getSuccess());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testHighConcurrentIdempotented() {
        String id = "IdempotentIT#testHighConcurrentIdempotented";
        String key = "IdempotentIT#testHighConcurrentIdempotented";
        RequestProto.RequestDTO registerReqDTO = new CorneastRequest(CorneastOperation.REGISTER, "", key, 100).instance;
        RequestProto.RequestDTO reduceReqDTO = new CorneastRequest(CorneastOperation.REDUCE, id, key).instance;
        RequestProto.RequestDTO queryReqDTO = new CorneastRequest(CorneastOperation.QUERY, "", key).instance;

        try {
            // register
            client.send(registerReqDTO);

            // high concurrent reduce
            for (int i = 0; i < 100; i++) {
                ResponseProto.ResponseDTO responseDTO = client.send(reduceReqDTO);
                Assertions.assertEquals(CorneastOperation.REDUCE, responseDTO.getType());
                Assertions.assertEquals(id, responseDTO.getId());
                Assertions.assertEquals(key, responseDTO.getReduceRespDTO().getKey());
                Assertions.assertTrue(responseDTO.getReduceRespDTO().getSuccess());
            }

            // assert redis data
            ResponseProto.ResponseDTO queryResponseDTO = client.send(queryReqDTO);
            Assertions.assertEquals(CorneastOperation.QUERY, queryResponseDTO.getType());
            Assertions.assertEquals("", queryResponseDTO.getId());
            Assertions.assertEquals(key, queryResponseDTO.getQueryRespDTO().getKey());
            Assertions.assertEquals(99, queryResponseDTO.getQueryRespDTO().getRemainingTokenCount());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testRetryWithAnotherClient() {
        String id = "IdempotentIT#testRetryWithAnotherClient";
        String key = "IdempotentIT#testRetryWithAnotherClient";
        RequestProto.RequestDTO registerReqDTO = new CorneastRequest(CorneastOperation.REGISTER, "", key, 100).instance;
        RequestProto.RequestDTO reduceReqDTO = new CorneastRequest(CorneastOperation.REDUCE, id, key).instance;
        RequestProto.RequestDTO queryReqDTO = new CorneastRequest(CorneastOperation.QUERY, "", key).instance;

        try {
            // register
            client.send(registerReqDTO);

            // first reduce
            client.send(reduceReqDTO);

            // close original client and create another client
            client.close();
            client = null;
            CorneastNioClient client2 = null;
            try {
                client2 = CorneastNioClient.of(config);
            } catch (IOException e) {
                throw new RuntimeException("Error creating client2", e);
            }

            // second, idempotented
            ResponseProto.ResponseDTO responseDTO2 = client2.send(reduceReqDTO);
            Assertions.assertEquals(CorneastOperation.REDUCE, responseDTO2.getType());
            Assertions.assertEquals(id, responseDTO2.getId());
            Assertions.assertEquals(key, responseDTO2.getReduceRespDTO().getKey());
            Assertions.assertTrue(responseDTO2.getReduceRespDTO().getSuccess());

            // assert redis data
            ResponseProto.ResponseDTO queryResponseDTO = client2.send(queryReqDTO);
            Assertions.assertEquals(CorneastOperation.QUERY, queryResponseDTO.getType());
            Assertions.assertEquals("", queryResponseDTO.getId());
            Assertions.assertEquals(key, queryResponseDTO.getQueryRespDTO().getKey());
            Assertions.assertEquals(99, queryResponseDTO.getQueryRespDTO().getRemainingTokenCount());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
