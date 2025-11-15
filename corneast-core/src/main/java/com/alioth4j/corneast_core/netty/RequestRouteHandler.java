package com.alioth4j.corneast_core.netty;

import com.alioth4j.corneast_core.common.CorneastOperation;
import com.alioth4j.corneast_core.exception.CorneastHandleException;
import com.alioth4j.corneast_core.proto.RequestProto;
import com.alioth4j.corneast_core.proto.ResponseProto;
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
                channelHandlerContext.writeAndFlush(exResponseDTO);
            }
        });
    }

}
