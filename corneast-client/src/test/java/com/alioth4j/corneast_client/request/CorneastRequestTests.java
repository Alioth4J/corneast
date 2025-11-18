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

package com.alioth4j.corneast_client.request;

import com.alioth4j.corneast_client.exception.RequestBuildException;
import com.alioth4j.corneast_core.common.CorneastOperation;
import com.alioth4j.corneast_core.proto.RequestProto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for <code>CorneastRequest</code>.
 *
 * @author Alioth Null
 */
public class CorneastRequestTests {

    @Test
    void testCommonConstructorAndInstance() {
        RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REDUCE, "id", "key").instance;
        Assertions.assertEquals(CorneastOperation.REDUCE, request.getType());
        Assertions.assertEquals("id", request.getId());
        Assertions.assertEquals("key", request.getReduceReqDTO().getKey());
    }

    @Test
    void testCommonConstructorAndGetInstanceMethod() {
        RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REDUCE, "id", "key").getInstance();
        Assertions.assertEquals(CorneastOperation.REDUCE, request.getType());
        Assertions.assertEquals("id", request.getId());
        Assertions.assertEquals("key", request.getReduceReqDTO().getKey());
    }

    @Test
    void testCommonConstructorAndGetMethod() {
        RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REDUCE, "id", "key").get();
        Assertions.assertEquals(CorneastOperation.REDUCE, request.getType());
        Assertions.assertEquals("id", request.getId());
        Assertions.assertEquals("key", request.getReduceReqDTO().getKey());
    }

    @Test
    void testCommonConstructorWithNullType() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(null, "id", "key");
        });
    }

    @Test
    void testCommonConstructorWithEmptyType() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest("", "id", "key");
        });
    }

    @Test
    void testCommonConstructorWithNullId() {
        RequestProto.RequestDTO requestDTO = new CorneastRequest(CorneastOperation.REDUCE, null, "key").instance;
        Assertions.assertEquals(CorneastOperation.REDUCE, requestDTO.getType());
        Assertions.assertEquals("", requestDTO.getId());
        Assertions.assertEquals("key", requestDTO.getReduceReqDTO().getKey());
    }

    @Test
    void testCommonConstructorWithEmptyId() {
        RequestProto.RequestDTO requestDTO = new CorneastRequest(CorneastOperation.REDUCE, "", "key").instance;
        Assertions.assertEquals(CorneastOperation.REDUCE, requestDTO.getType());
        Assertions.assertEquals("", requestDTO.getId());
        Assertions.assertEquals("key", requestDTO.getReduceReqDTO().getKey());
    }

    @Test
    void testCommonConstructorWithNullKey() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(CorneastOperation.REDUCE, "id", null);
        });
    }

    @Test
    void testCommonConstructorWithEmptyKey() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(CorneastOperation.REDUCE, "id", "");
        });
    }


    @Test
    void testRegisterConstructorAndInstance() {
        RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REGISTER, "id", "key", 1000).instance;
        Assertions.assertEquals(CorneastOperation.REGISTER, request.getType());
        Assertions.assertEquals("id", request.getId());
        Assertions.assertEquals("key", request.getRegisterReqDTO().getKey());
        Assertions.assertEquals(1000, request.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testRegisterConstructorAndGetInstanceMethod() {
        RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REGISTER, "id", "key", 1000).getInstance();
        Assertions.assertEquals(CorneastOperation.REGISTER, request.getType());
        Assertions.assertEquals("id", request.getId());
        Assertions.assertEquals("key", request.getRegisterReqDTO().getKey());
        Assertions.assertEquals(1000, request.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testRegisterConstructorAndGetMethod() {
        RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REGISTER, "id", "key", 1000).get();
        Assertions.assertEquals(CorneastOperation.REGISTER, request.getType());
        Assertions.assertEquals("id", request.getId());
        Assertions.assertEquals("key", request.getRegisterReqDTO().getKey());
        Assertions.assertEquals(1000, request.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testRegisterConstructorWithNonRegisterType() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REDUCE, "id", "key", 1000).instance;
        });
    }

    @Test
    void testRegisterConstructorWithNullType() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(null, "id", "key", 1000);
        });
    }

    @Test
    void testRegisterConstructorWithEmptyType() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest("", "id", "key", 1000);
        });
    }

    @Test
    void testRegisterConstructorWithNullId() {
        RequestProto.RequestDTO requestDTO = new CorneastRequest(CorneastOperation.REGISTER, null, "key", 1000).instance;
        Assertions.assertEquals(CorneastOperation.REGISTER, requestDTO.getType());
        Assertions.assertEquals("", requestDTO.getId());
        Assertions.assertEquals("key", requestDTO.getRegisterReqDTO().getKey());
        Assertions.assertEquals(1000, requestDTO.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testRegisterConstructorWithEmptyId() {
        RequestProto.RequestDTO requestDTO = new CorneastRequest(CorneastOperation.REGISTER, "", "key", 1000).instance;
        Assertions.assertEquals(CorneastOperation.REGISTER, requestDTO.getType());
        Assertions.assertEquals("", requestDTO.getId());
        Assertions.assertEquals("key", requestDTO.getRegisterReqDTO().getKey());
        Assertions.assertEquals(1000, requestDTO.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testRegisterConstructorWithNullKey() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(CorneastOperation.REGISTER, "id", null, 1000);
        });
    }

    @Test
    void testRegisterConstructorWithEmptyKey() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(CorneastOperation.REGISTER, "id", "", 1000);
        });
    }

    @Test
    void testRegisterConstructorWithZeroTokenCount() {
        RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REGISTER, "id", "key", 0).instance;
        Assertions.assertEquals(0, request.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testRegisterConstructorWithNegativeTokenCount() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(CorneastOperation.REGISTER, "id", "key", -1);
        });
    }

    @Test
    void testNonIdempotentConstructorAndInstance() {
        RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REDUCE, "key").instance;
        Assertions.assertEquals(CorneastOperation.REDUCE, request.getType());
        Assertions.assertEquals("", request.getId());
        Assertions.assertEquals("key", request.getReduceReqDTO().getKey());
    }

    @Test
    void testNonIdempotentConstructorAndGetInstanceMethod() {
        RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REDUCE, "key")
                .getInstance();
        Assertions.assertEquals(CorneastOperation.REDUCE, request.getType());
        Assertions.assertEquals("", request.getId());
        Assertions.assertEquals("key", request.getReduceReqDTO().getKey());
    }

    @Test
    void testNonIdempotentConstructorAndGetMethod() {
        RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REDUCE, "key")
                .get();
        Assertions.assertEquals(CorneastOperation.REDUCE, request.getType());
        Assertions.assertEquals("", request.getId());
        Assertions.assertEquals("key", request.getReduceReqDTO().getKey());
    }

    @Test
    void testNonIdempotentConstructorWithNullType() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(null, "key");
        });
    }

    @Test
    void testNonIdempotentConstructorWithEmptyType() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest("", "key");
        });
    }

    @Test
    void testNonIdempotentConstructorWithNullKey() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(CorneastOperation.REDUCE, null);
        });
    }

    @Test
    void testNonIdempotentConstructorWithEmptyKey() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(CorneastOperation.REDUCE, "");
        });
    }

    @Test
    void testNonIdempotentRegisterConstructorAndInstance() {
        RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REGISTER, "key", 1000)
                .instance;
        Assertions.assertEquals(CorneastOperation.REGISTER, request.getType());
        Assertions.assertEquals("", request.getId());
        Assertions.assertEquals("key", request.getRegisterReqDTO().getKey());
        Assertions.assertEquals(1000, request.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testNonIdempotentRegisterConstructorAndGetInstanceMethod() {
        RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REGISTER, "key", 1000)
                .getInstance();
        Assertions.assertEquals(CorneastOperation.REGISTER, request.getType());
        Assertions.assertEquals("", request.getId());
        Assertions.assertEquals("key", request.getRegisterReqDTO().getKey());
        Assertions.assertEquals(1000, request.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testNonIdempotentRegisterConstructorAndGetMethod() {
        RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REGISTER, "key", 1000)
                .get();
        Assertions.assertEquals(CorneastOperation.REGISTER, request.getType());
        Assertions.assertEquals("", request.getId());
        Assertions.assertEquals("key", request.getRegisterReqDTO().getKey());
        Assertions.assertEquals(1000, request.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testNonIdempotentRegisterConstructorWithNonRegisterType() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REDUCE, "key", 1000)
                    .instance;
        });
    }

    @Test
    void testNonIdempotentRegisterConstructorWithNullType() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(null, "key", 1000);
        });
    }

    @Test
    void testNonIdempotentRegisterConstructorWithEmptyType() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest("", "key", 1000);
        });
    }

    @Test
    void testNonIdempotentRegisterConstructorWithNullKey() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(CorneastOperation.REGISTER, null, 1000);
        });
    }

    @Test
    void testNonIdempotentRegisterConstructorWithEmptyKey() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(CorneastOperation.REGISTER, "", 1000);
        });
    }

    @Test
    void testNonIdempotentRegisterConstructorWithZeroTokenCount() {
        RequestProto.RequestDTO request = new CorneastRequest(CorneastOperation.REGISTER, "key", 0)
                .instance;
        Assertions.assertEquals(0, request.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testNonIdempotentRegisterConstructorWithNegativeTokenCount() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(CorneastOperation.REGISTER, "key", -1);
        });
    }

}
