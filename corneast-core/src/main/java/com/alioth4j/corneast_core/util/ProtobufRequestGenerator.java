package com.alioth4j.corneast_core.util;

import com.alioth4j.corneast_core.proto.QueryProto;
import com.alioth4j.corneast_core.proto.ReduceProto;
import com.alioth4j.corneast_core.proto.RegisterProto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ProtobufRequestGenerator implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // params
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String key = now.format(formatter);
        Long tokenCount = 1000L;

        // create dir
        File dir = new File("corneast-core", "request");
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                System.err.println("Unable to mkdirs: " + dir.getCanonicalPath());
                return;
            }
        }

        // register request
        RegisterProto.RegisterReqDTO registerReqDTO = RegisterProto.RegisterReqDTO.newBuilder()
                .setKey(key)
                .setTokenCount(tokenCount)
                .build();
        byte[] registerReqByteArray = registerReqDTO.toByteArray();
        File registerReqFile = new File(dir, "register.bin");
        try (FileOutputStream fos = new FileOutputStream(registerReqFile)) {
            fos.write(registerReqByteArray);
        } catch (IOException e) {
            handleIOException(e);
        }

        // reduce request
        ReduceProto.ReduceReqDTO reduceReqDTO = ReduceProto.ReduceReqDTO.newBuilder()
                .setKey(key)
                .build();
        byte[] reduceReqByteArray = reduceReqDTO.toByteArray();
        File reduceReqFile = new File(dir, "reduce.bin");
        try (FileOutputStream fos = new FileOutputStream(reduceReqFile)) {
            fos.write(reduceReqByteArray);
        } catch (IOException e) {
            handleIOException(e);
        }

        // query request
        QueryProto.QueryReqDTO queryReqDTO = QueryProto.QueryReqDTO.newBuilder()
                .setKey(key)
                .build();
        byte[] queryReqByteArray = queryReqDTO.toByteArray();
        File queryReqFile = new File(dir, "query.bin");
        try (FileOutputStream fos = new FileOutputStream(queryReqFile)) {
            fos.write(queryReqByteArray);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    private static void handleIOException(IOException e) {
        System.err.println("Something went wrong when writing request to the file: " + e.getMessage());
        e.printStackTrace();
    }

}
