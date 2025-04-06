package com.alioth4j.corneast_core.util;

import com.alioth4j.corneast_core.proto.QueryProto;
import com.alioth4j.corneast_core.proto.RequestProto;
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
        // TODO update this class

        // params
        String key = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        Long tokenCount = 1000L;

        // create dir
        File dir = new File("corneast-test", "request");
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                System.err.println("Unable to mkdirs: " + dir.getCanonicalPath());
                return;
            }
        }

        // register request
        RequestProto.RequestDTO registerRequestDTO = RequestProto.RequestDTO.newBuilder()
                .setType("register")
                .setRegisterReqDTO(RequestProto.RegisterReqDTO.newBuilder().setKey(key).setTokenCount(1000).build())
                .build();
        byte[] registerReqByteArray = registerRequestDTO.toByteArray();
        File registerReqFile = new File(dir, "register.bin");
        try (FileOutputStream fos = new FileOutputStream(registerReqFile)) {
            fos.write(registerReqByteArray);
        } catch (IOException e) {
            handleIOException(e);
        }

        // reduce request
        RequestProto.RequestDTO reduceRequestDTO = RequestProto.RequestDTO.newBuilder()
                .setType("reduce")
                .setReduceReqDTO(RequestProto.ReduceReqDTO.newBuilder().setKey(key).build())
                .build();
        byte[] reduceReqByteArray = reduceRequestDTO.toByteArray();
        File reduceReqFile = new File(dir, "reduce.bin");
        try (FileOutputStream fos = new FileOutputStream(reduceReqFile)) {
            fos.write(reduceReqByteArray);
        } catch (IOException e) {
            handleIOException(e);
        }

        // query request
        RequestProto.RequestDTO queryRequestDTO = RequestProto.RequestDTO.newBuilder()
                .setType("query")
                .setQueryReqDTO(RequestProto.QueryReqDTO.newBuilder().setKey(key))
                .build();
        byte[] queryReqByteArray = queryRequestDTO.toByteArray();
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
