package com.alioth4j.corneast_core.util;

import com.alioth4j.corneast_core.common.Operation;
import com.alioth4j.corneast_core.proto.RequestProto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Util class to generate binary request files:
 * register.bin, reduce.bin, query.bin, release.bin.
 *
 * If the working class is the root directory of the project,
 * the files will be generated in corneast-test/request.
 *
 * @author Alioth Null
 */
public class ProtobufRequestGenerator {

    // TODO use CorneastRequestBuilder to generate
    public static void generate() {
        // params
        String key = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        Long tokenCount = 1000L;

        // create dir
        File dir = new File("../", "corneast-test/request");
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                System.err.println("Unable to mkdirs: " + dir.getAbsolutePath());
                return;
            }
        }

        final String suffix = ".bin";

        // register request
        RequestProto.RequestDTO registerRequestDTO = RequestProto.RequestDTO.newBuilder()
                .setType(Operation.REGISTER)
                .setId("123456")
                .setRegisterReqDTO(RequestProto.RegisterReqDTO.newBuilder().setKey(key).setTokenCount(tokenCount).build())
                .build();
        byte[] registerReqByteArray = registerRequestDTO.toByteArray();
        File registerReqFile = new File(dir, Operation.REGISTER + suffix);
        writeWithLengthPrefix(registerReqFile, registerReqByteArray);

        // reduce request
        RequestProto.RequestDTO reduceRequestDTO = RequestProto.RequestDTO.newBuilder()
                .setType(Operation.REDUCE)
                .setId("123456")
                .setReduceReqDTO(RequestProto.ReduceReqDTO.newBuilder().setKey(key).build())
                .build();
        byte[] reduceReqByteArray = reduceRequestDTO.toByteArray();
        File reduceReqFile = new File(dir, Operation.REDUCE + suffix);
        writeWithLengthPrefix(reduceReqFile, reduceReqByteArray);

        // query request
        RequestProto.RequestDTO queryRequestDTO = RequestProto.RequestDTO.newBuilder()
                .setType(Operation.QUERY)
                .setId("123456")
                .setQueryReqDTO(RequestProto.QueryReqDTO.newBuilder().setKey(key))
                .build();
        byte[] queryReqByteArray = queryRequestDTO.toByteArray();
        File queryReqFile = new File(dir, Operation.QUERY + suffix);
        writeWithLengthPrefix(queryReqFile, queryReqByteArray);

        // release request
        RequestProto.RequestDTO releaseRequestDTO = RequestProto.RequestDTO.newBuilder()
                .setType(Operation.RELEASE)
                .setId("123456")
                .setReleaseReqDTO(RequestProto.ReleaseReqDTO.newBuilder().setKey(key).build())
                .build();
        byte[] releaseReqByteArray = releaseRequestDTO.toByteArray();
        File releaseReqFile = new File(dir, Operation.RELEASE + suffix);
        writeWithLengthPrefix(releaseReqFile, releaseReqByteArray);
    }

    private static void writeWithLengthPrefix(File file, byte[] data) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(encodeVarint32(data.length));
            fos.write(data);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    /**
     * Encodes an integer into a varint32 format as used by Protobuf.
     *
     * @param value int to encode
     * @return length prefix
     */
    private static byte[] encodeVarint32(int value) {
        byte[] buffer = new byte[5];
        int position = 0;
        while ((value & ~0x7F) != 0) {
            buffer[position++] = (byte)((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        buffer[position++] = (byte)(value);
        byte[] result = new byte[position];
        System.arraycopy(buffer, 0, result, 0, position);
        return result;
    }

    private static void handleIOException(IOException e) {
        System.err.println("Something went wrong when writing request to the file: " + e.getMessage());
        e.printStackTrace();
    }

}
