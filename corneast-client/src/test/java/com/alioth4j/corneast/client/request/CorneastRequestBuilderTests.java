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

package com.alioth4j.corneast.client.request;

import com.alioth4j.corneast.client.exception.RequestBuildException;
import com.alioth4j.corneast.common.operation.CorneastOperation;
import com.alioth4j.corneast.common.proto.RequestProto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for CorneastRequestBuilder.
 *
 * @author Alioth Null
 */
public class CorneastRequestBuilderTests {

    @Test
    void testCorrectBuilder() {
        RequestProto.RequestDTO requestDTO = CorneastRequestBuilder.newBuilder()
                .setType(CorneastOperation.REGISTER)
                .setId("12345")
                .setKey("key-67890")
                .setTokenCount(1000)
                .build();
        assertEquals(CorneastOperation.REGISTER, requestDTO.getType());
        assertEquals("12345", requestDTO.getId());
        assertEquals("key-67890", requestDTO.getRegisterReqDTO().getKey());
        assertEquals(1000, requestDTO.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testNotSetTypeBeforeSettingKey() {
        assertThrows(RequestBuildException.class, () -> {
            CorneastRequestBuilder.newBuilder()
                    .setKey("key-67890");
        });
    }

    @Test
    void testNullType() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            CorneastRequestBuilder.newBuilder()
                    .setType(null)
                    .setId("id")
                    .setKey("key")
                    .build();
        });
    }

    @Test
    void testEmptyType() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            CorneastRequestBuilder.newBuilder()
                    .setType("")
                    .setId("id")
                    .setKey("key")
                    .build();
        });
    }

    @Test
    void testNotSetType() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            CorneastRequestBuilder.newBuilder()
                    .setId("id")
                    .setKey("key")
                    .build();
        });
    }

    @Test
    void testNullId() {
        RequestProto.RequestDTO requestDTO = CorneastRequestBuilder.newBuilder()
                .setType(CorneastOperation.REDUCE)
                .setId(null)
                .setKey("key")
                .build();
        Assertions.assertEquals(CorneastOperation.REDUCE, requestDTO.getType());
        Assertions.assertEquals("", requestDTO.getId());
        Assertions.assertEquals("key", requestDTO.getReduceReqDTO().getKey());
    }

    @Test
    void testEmptyId() {
        RequestProto.RequestDTO requestDTO = CorneastRequestBuilder.newBuilder()
                .setType(CorneastOperation.REDUCE)
                .setId("")
                .setKey("key")
                .build();
        Assertions.assertEquals(CorneastOperation.REDUCE, requestDTO.getType());
        Assertions.assertEquals("", requestDTO.getId());
        Assertions.assertEquals("key", requestDTO.getReduceReqDTO().getKey());
    }

    @Test
    void testNotSetId() {
        RequestProto.RequestDTO requestDTO = CorneastRequestBuilder.newBuilder()
                .setType(CorneastOperation.REDUCE)
                .setKey("key")
                .build();
        Assertions.assertEquals(CorneastOperation.REDUCE, requestDTO.getType());
        Assertions.assertEquals("", requestDTO.getId());
        Assertions.assertEquals("key", requestDTO.getReduceReqDTO().getKey());
    }

    @Test
    void testNullKey() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            CorneastRequestBuilder.newBuilder()
                    .setType(CorneastOperation.REDUCE)
                    .setId("id")
                    .setKey(null)
                    .build();
        });
    }

    @Test
    void testEmptyKey() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            CorneastRequestBuilder.newBuilder()
                    .setType(CorneastOperation.REDUCE)
                    .setId("id")
                    .setKey("")
                    .build();
        });
    }

    @Test
    void testNotSetKey() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            CorneastRequestBuilder.newBuilder()
                    .setType(CorneastOperation.REDUCE)
                    .setId("id")
                    .build();
        });
    }

    @Test
    void testZeroTokenCount() {
        RequestProto.RequestDTO request = CorneastRequestBuilder.newBuilder()
                .setType(CorneastOperation.REGISTER)
                .setId("id")
                .setKey("key")
                .setTokenCount(1000)
                .build();
        Assertions.assertEquals(CorneastOperation.REGISTER, request.getType());
        Assertions.assertEquals("id", request.getId());
        Assertions.assertEquals("key", request.getRegisterReqDTO().getKey());
        Assertions.assertEquals(1000, request.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testNegativeTokenCount() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            CorneastRequestBuilder.newBuilder()
                    .setType(CorneastOperation.REGISTER)
                    .setId("id")
                    .setKey("key")
                    .setTokenCount(-1)
                    .build();
        });
    }

    @Test
    void testNotSetTokenCount() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            CorneastRequestBuilder.newBuilder()
                    .setType(CorneastOperation.REGISTER)
                    .setId("id")
                    .setKey("key")
                    .build();
        });
    }

}
