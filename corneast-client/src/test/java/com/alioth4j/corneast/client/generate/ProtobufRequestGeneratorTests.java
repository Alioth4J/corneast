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

package com.alioth4j.corneast.client.generate;

import com.alioth4j.corneast.client.request.CorneastRequest;
import com.alioth4j.corneast.common.operation.CorneastOperation;
import com.alioth4j.corneast.common.proto.RequestProto;
import org.junit.jupiter.api.Test;

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
 * the files will be generated in corneast-client/request.
 *
 * @author Alioth Null
 */
public class ProtobufRequestGeneratorTests {

    @Test
    public void generate() {
        // params
        String key = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        Long tokenCount = 1000L;

        // create dir
        File dir = new File(System.getProperty("user.dir"), "request");
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                System.err.println("Unable to mkdirs: " + dir.getAbsolutePath());
                return;
            }
        }

        final String suffix = ".bin";

        // register request
        RequestProto.RequestDTO registerRequestDTO = new CorneastRequest(CorneastOperation.REGISTER, "", key, tokenCount).instance;
        byte[] registerReqByteArray = registerRequestDTO.toByteArray();
        File registerReqFile = new File(dir, CorneastOperation.REGISTER + suffix);
        writeWithLengthPrefix(registerReqFile, registerReqByteArray);

        // reduce request
        RequestProto.RequestDTO reduceRequestDTO = new CorneastRequest(CorneastOperation.REDUCE, "", key).instance;
        byte[] reduceReqByteArray = reduceRequestDTO.toByteArray();
        File reduceReqFile = new File(dir, CorneastOperation.REDUCE + suffix);
        writeWithLengthPrefix(reduceReqFile, reduceReqByteArray);

        // query request
        RequestProto.RequestDTO queryRequestDTO = new CorneastRequest(CorneastOperation.QUERY, "", key).instance;
        byte[] queryReqByteArray = queryRequestDTO.toByteArray();
        File queryReqFile = new File(dir, CorneastOperation.QUERY + suffix);
        writeWithLengthPrefix(queryReqFile, queryReqByteArray);

        // release request
        RequestProto.RequestDTO releaseRequestDTO = new CorneastRequest(CorneastOperation.RELEASE, "", key).instance;
        byte[] releaseReqByteArray = releaseRequestDTO.toByteArray();
        File releaseReqFile = new File(dir, CorneastOperation.RELEASE + suffix);
        writeWithLengthPrefix(releaseReqFile, releaseReqByteArray);
    }

    private void writeWithLengthPrefix(File file, byte[] data) {
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
    private byte[] encodeVarint32(int value) {
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

    private void handleIOException(IOException e) {
        System.err.println("Something went wrong when writing request to the file: " + e.getMessage());
        e.printStackTrace();
    }

}
