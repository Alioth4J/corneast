package com.alioth4j.corneast_client.request;

import java.util.UUID;

public class CorneastIdGenerator {

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

}
