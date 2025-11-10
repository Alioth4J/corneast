package com.alioth4j.corneast_client.request;

import com.alioth4j.corneast_client.exception.RequestBuildException;
import com.alioth4j.corneast_core.common.CorneastOperation;
import com.alioth4j.corneast_core.proto.RequestProto;
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
