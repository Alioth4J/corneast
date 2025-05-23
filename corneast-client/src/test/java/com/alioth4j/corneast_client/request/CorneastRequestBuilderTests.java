package com.alioth4j.corneast_client.request;

import com.alioth4j.corneast_client.exception.RequestBuildException;
import com.alioth4j.corneast_core.proto.RequestProto;
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
                .setType("register")
                .setId("12345")
                .setKey("key-67890")
                .setTokenCount(1000)
                .build();
        assertEquals("register", requestDTO.getType());
        assertEquals("12345", requestDTO.getId());
        assertEquals("key-67890", requestDTO.getRegisterReqDTO().getKey());
        assertEquals(1000, requestDTO.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testNoType() {
        assertThrows(RequestBuildException.class, () -> {
            CorneastRequestBuilder.newBuilder()
                    .setKey("key-67890");
        });
    }

}
