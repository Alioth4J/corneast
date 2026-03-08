/*
 * Corneast
 * Copyright (C) 2026 Alioth Null
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

package com.alioth4j.corneast.test.jmeter;

import java.io.IOException;
import java.util.List;

import com.alioth4j.corneast.common.algo.RandomSelector;
import com.alioth4j.corneast.common.algo.Selector;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import com.alioth4j.corneast.client.config.CorneastConfig;
import com.alioth4j.corneast.client.request.CorneastRequest;
import com.alioth4j.corneast.client.send.*;
import com.alioth4j.corneast.common.operation.CorneastOperation;
import com.alioth4j.corneast.common.proto.RequestProto;
import com.alioth4j.corneast.common.proto.ResponseProto;

public class CorneastJavaSampler extends AbstractJavaSamplerClient {

    private final List<RequestProto.RequestDTO> reduceReqDTOList = List.of(
            new CorneastRequest(CorneastOperation.REDUCE, "", "key-test-0").instance,
            new CorneastRequest(CorneastOperation.REDUCE, "", "key-test-1").instance,
            new CorneastRequest(CorneastOperation.REDUCE, "", "key-test-2").instance,
            new CorneastRequest(CorneastOperation.REDUCE, "", "key-test-3").instance,
            new CorneastRequest(CorneastOperation.REDUCE, "", "key-test-4").instance,
            new CorneastRequest(CorneastOperation.REDUCE, "", "key-test-5").instance,
            new CorneastRequest(CorneastOperation.REDUCE, "", "key-test-6").instance,
            new CorneastRequest(CorneastOperation.REDUCE, "", "key-test-7").instance,
            new CorneastRequest(CorneastOperation.REDUCE, "", "key-test-8").instance,
            new CorneastRequest(CorneastOperation.REDUCE, "", "key-test-9").instance);

    private final Selector<RequestProto.RequestDTO> selector = new RandomSelector<>(reduceReqDTOList);

    private final CorneastNioClient corneastNioClient;

    {
        CorneastConfig config = new CorneastConfig();
        config.setHost("127.0.0.1");
        config.setPort(8088);
        try {
            corneastNioClient = CorneastNioClient.of(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();
        result.sampleStart();

        try {
            ResponseProto.ResponseDTO responseDTO = corneastNioClient.send(selector.select());

            if (responseDTO.getReduceRespDTO().getSuccess()) {
                result.setResponseData("Success", "UTF-8");
                result.setResponseCodeOK();
                result.setSuccessful(true);
            } else {
                result.setResponseData("Fail", "UTF-8");
                result.setResponseCodeOK();
                result.setSuccessful(true);
            }
        } catch (Exception e) {
            result.setResponseMessage(e.getMessage());
            result.setResponseCode("500");
            result.setSuccessful(false);
        } finally {
            result.sampleEnd();
        }
        return result;
    }

}
