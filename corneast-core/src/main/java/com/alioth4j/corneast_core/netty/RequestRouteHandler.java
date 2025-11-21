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

package com.alioth4j.corneast_core.netty;

import com.alioth4j.corneast_common.operation.CorneastOperation;
import com.alioth4j.corneast_common.proto.RequestProto;
import com.alioth4j.corneast_common.proto.ResponseProto;
import com.alioth4j.corneast_core.strategy.RequestHandlingStrategy;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Route requests for netty.
 *
 * @author Alioth Null
 */
@Component
@ChannelHandler.Sharable
public class RequestRouteHandler extends SimpleChannelInboundHandler<RequestProto.RequestDTO> {

    private static final Logger log = LoggerFactory.getLogger(RequestRouteHandler.class);

    @Autowired
    private Map<String/* beanName */, RequestHandlingStrategy/* object of strategy */> requestHandlingStrategyMap;

    /* flyweight start */
    private final ResponseProto.ResponseDTO.Builder unknownTypeResponseBuilder = ResponseProto.ResponseDTO.newBuilder()
            .setType(CorneastOperation.UNKNOWN);

    private final ResponseProto.ResponseDTO.Builder exRegisterResponseBuilder = ResponseProto.ResponseDTO.newBuilder()
            .setType(CorneastOperation.REGISTER);

    private final ResponseProto.RegisterRespDTO.Builder exRegisterRespDTOBuilder = ResponseProto.RegisterRespDTO.newBuilder().setSuccess(false);

    private final ResponseProto.ResponseDTO.Builder exReduceResponseBuilder = ResponseProto.ResponseDTO.newBuilder()
            .setType(CorneastOperation.REDUCE);

    private final ResponseProto.ReduceRespDTO.Builder exReduceRespDTOBuilder = ResponseProto.ReduceRespDTO.newBuilder().setSuccess(false);

    private final ResponseProto.ResponseDTO.Builder exReleaseResponseBuilder = ResponseProto.ResponseDTO.newBuilder()
            .setType(CorneastOperation.RELEASE);

    private final ResponseProto.ReleaseRespDTO.Builder exReleaseRespDTOBuilder = ResponseProto.ReleaseRespDTO.newBuilder().setSuccess(false);

    private final ResponseProto.ResponseDTO.Builder exQueryResponseBuilder = ResponseProto.ResponseDTO.newBuilder()
            .setType(CorneastOperation.QUERY);

    private final ResponseProto.QueryRespDTO.Builder exQueryRespDTOBuilder = ResponseProto.QueryRespDTO.newBuilder().setRemainingTokenCount(-1);
    /* flyweight end */

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RequestProto.RequestDTO requestDTO) throws Exception {
        // choose the strategy
        String requestType = requestDTO.getType();
        RequestHandlingStrategy requestHandlingStrategy = requestHandlingStrategyMap.get(requestType);
        // request type does not exist
        if (requestHandlingStrategy == null) {
            ResponseProto.ResponseDTO unknownTypeResponseDTO = unknownTypeResponseBuilder
                    .setId(requestDTO.getId())
                    .build();
            channelHandlerContext.writeAndFlush(unknownTypeResponseDTO);
            return;
        }
        // handle
        CompletableFuture<ResponseProto.ResponseDTO> responseCompletableFuture = requestHandlingStrategy.handle(requestDTO);
        // write and flush the response
        responseCompletableFuture.whenComplete((responseDTO, t) -> {
            if (t == null) {
                channelHandlerContext.writeAndFlush(responseDTO);
            } else {
                // log
                log.error("Failed to handle request of type [{}]: {}", requestType, t.getMessage(), t);
                // send an error response
                ResponseProto.ResponseDTO exResponseDTO = null;
                switch (requestType) {
                    case CorneastOperation.REGISTER: {
                        exResponseDTO = exRegisterResponseBuilder.setId(requestDTO.getId())
                                .setRegisterRespDTO(exRegisterRespDTOBuilder.setKey(requestDTO.getRegisterReqDTO().getKey()).build())
                                .build();
                        break;
                    }
                    case CorneastOperation.REDUCE: {
                        exResponseDTO = exReduceResponseBuilder.setId(requestDTO.getId())
                                .setReduceRespDTO(exReduceRespDTOBuilder.setKey(requestDTO.getReduceReqDTO().getKey()).build())
                                .build();
                        break;
                    }
                    case CorneastOperation.RELEASE: {
                        exResponseDTO = exReleaseResponseBuilder.setId(requestDTO.getId())
                                .setReleaseRespDTO(exReleaseRespDTOBuilder.setKey(requestDTO.getReleaseReqDTO().getKey()).build())
                                .build();
                        break;
                    }
                    case CorneastOperation.QUERY: {
                        exResponseDTO = exQueryResponseBuilder.setId(requestDTO.getId())
                                .setQueryRespDTO(exQueryRespDTOBuilder.setKey(requestDTO.getQueryReqDTO().getKey()).build())
                                .build();
                        break;
                    }
                }
                ResponseProto.RegisterRespDTO test = exRegisterRespDTOBuilder.setKey("").build();
                exRegisterResponseBuilder.setRegisterRespDTO(test).setId("id").build();
                channelHandlerContext.writeAndFlush(exResponseDTO);
            }
        });
    }

}
