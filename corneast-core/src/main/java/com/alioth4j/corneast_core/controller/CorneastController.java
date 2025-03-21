package com.alioth4j.corneast_core.controller;

import com.alioth4j.corneast_core.pojo.*;
import com.alioth4j.corneast_core.service.QueryService;
import com.alioth4j.corneast_core.service.ReduceService;
import com.alioth4j.corneast_core.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/corneast")
public class CorneastController {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private ReduceService reduceService;

    @Autowired
    private QueryService queryService;


    @PostMapping("/register")
    public RegisterRespDTO register(@RequestBody RegisterReqDTO registerReqDTO) {
        return registerService.register(registerReqDTO);
    }

    @PostMapping("/reduce")
    public CompletableFuture<ReduceRespDTO> reduce(@RequestBody ReduceReqDTO reduceReqDTO) {
        return reduceService.reduce(reduceReqDTO);
    }

    @PostMapping("/query")
    public CompletableFuture<QueryRespDTO> query(@RequestBody QueryReqDTO queryReqDTO) {
        return queryService.query(queryReqDTO);
    }

}
