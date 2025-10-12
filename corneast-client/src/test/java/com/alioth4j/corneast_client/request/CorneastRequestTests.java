package com.alioth4j.corneast_client.request;

import com.alioth4j.corneast_client.exception.RequestBuildException;
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
        RequestProto.RequestDTO request = new CorneastRequest("reduce", "id", "key").instance;
        Assertions.assertEquals("reduce", request.getType());
        Assertions.assertEquals("id", request.getId());
        Assertions.assertEquals("key", request.getReduceReqDTO().getKey());
    }

    @Test
    void testCommonConstructorAndGetInstanceMethod() {
        RequestProto.RequestDTO request = new CorneastRequest("reduce", "id", "key").getInstance();
        Assertions.assertEquals("reduce", request.getType());
        Assertions.assertEquals("id", request.getId());
        Assertions.assertEquals("key", request.getReduceReqDTO().getKey());
    }

    @Test
    void testCommonConstructorAndGetMethod() {
        RequestProto.RequestDTO request = new CorneastRequest("reduce", "id", "key").get();
        Assertions.assertEquals("reduce", request.getType());
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
            new CorneastRequest("reduce", null, "key");
        });
    }

    @Test
    void testCommonConstructorWithEmptyId() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest("reduce", "", "key");
        });
    }

    @Test
    void testCommonConstructorWithNullKey() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest("reduce", "id", null);
        });
    }

    @Test
    void testCommonConstructorWithEmptyKey() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest("reduce", "id", "");
        });
    }


    @Test
    void testRegisterConstructorAndInstance() {
        RequestProto.RequestDTO request = new CorneastRequest("register", "id", "key", 1000).instance;
        Assertions.assertEquals("register", request.getType());
        Assertions.assertEquals("id", request.getId());
        Assertions.assertEquals("key", request.getRegisterReqDTO().getKey());
        Assertions.assertEquals(1000, request.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testRegisterConstructorAndGetInstanceMethod() {
        RequestProto.RequestDTO request = new CorneastRequest("register", "id", "key", 1000).getInstance();
        Assertions.assertEquals("register", request.getType());
        Assertions.assertEquals("id", request.getId());
        Assertions.assertEquals("key", request.getRegisterReqDTO().getKey());
        Assertions.assertEquals(1000, request.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testRegisterConstructorAndGetMethod() {
        RequestProto.RequestDTO request = new CorneastRequest("register", "id", "key", 1000).get();
        Assertions.assertEquals("register", request.getType());
        Assertions.assertEquals("id", request.getId());
        Assertions.assertEquals("key", request.getRegisterReqDTO().getKey());
        Assertions.assertEquals(1000, request.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testRegisterConstructorWithNonRegisterType() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            RequestProto.RequestDTO request = new CorneastRequest("reduce", "id", "key", 1000).instance;
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
            new CorneastRequest("register", null, "key", 1000);
        });
    }

    @Test
    void testRegisterConstructorWithEmptyId() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest("register", "", "key", 1000);
        });
    }

    @Test
    void testRegisterConstructorWithNullKey() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest("register", "id", null, 1000);
        });
    }

    @Test
    void testRegisterConstructorWithEmptyKey() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest("register", "id", "", 1000);
        });
    }

    @Test
    void testRegisterConstructorWithZeroTokenCount() {
        RequestProto.RequestDTO request = new CorneastRequest("register", "id", "key", 0).instance;
        Assertions.assertEquals(0, request.getRegisterReqDTO().getTokenCount());
    }

    @Test
    void testRegisterConstructorWithNegativeTokenCount() {
        Assertions.assertThrows(RequestBuildException.class, () -> {
            new CorneastRequest("register", "id", "key", -1);
        });
    }

}
