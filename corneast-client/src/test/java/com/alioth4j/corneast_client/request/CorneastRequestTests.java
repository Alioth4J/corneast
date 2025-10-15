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
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(CorneastOperation.REDUCE, null, "key");
        });
    }

    @Test
    void testCommonConstructorWithEmptyId() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(CorneastOperation.REDUCE, "", "key");
        });
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
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(CorneastOperation.REGISTER, null, "key", 1000);
        });
    }

    @Test
    void testRegisterConstructorWithEmptyId() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest(CorneastOperation.REGISTER, "", "key", 1000);
        });
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

}
