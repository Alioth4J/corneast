package com.alioth4j.corneast_core.controller;

import com.alioth4j.corneast_core.proto.QueryProto;
import com.alioth4j.corneast_core.proto.ReduceProto;
import com.alioth4j.corneast_core.proto.RegisterProto;
import com.alioth4j.corneast_core.service.QueryService;
import com.alioth4j.corneast_core.service.ReduceService;
import com.alioth4j.corneast_core.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Deprecated
@RestController
@RequestMapping("/corneast")
public class CorneastController {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private ReduceService reduceService;

    @Autowired
    private QueryService queryService;


    @PostMapping(value = "/register", consumes = "application/x-protobuf", produces = "application/x-protobuf")
    public RegisterProto.RegisterRespDTO register(@RequestBody RegisterProto.RegisterReqDTO registerReqDTO) {
        return registerService.register(registerReqDTO);
    }

    @Deprecated
    @PostMapping(value = "reduce", consumes = "application/x-protobuf", produces = "application/x-protobuf")
    public CompletableFuture<ReduceProto.ReduceRespDTO> reduce(@RequestBody ReduceProto.ReduceReqDTO reduceReqDTO) {
        return reduceService.reduce(reduceReqDTO);
    }

    @PostMapping(value = "query", consumes = "application/x-protobuf", produces = "application/x-protobuf")
    public CompletableFuture<QueryProto.QueryRespDTO> query(@RequestBody QueryProto.QueryReqDTO queryReqDTO) {
        return queryService.query(queryReqDTO);
    }

}
