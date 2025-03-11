package com.alioth4j.corneast_core.controller;

import com.alioth4j.corneast_core.pojo.*;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/corneast")
public class CorneastController {

    @PostMapping("/register")
    public RegisterRespDTO register(@RequestBody RegisterReqDTO registerReqDTO) {

    }

    @PostMapping("/reduce")
    public CompletableFuture<ReduceRespDTO> reduce(@RequestBody ReduceReqDTO reduceReqDTO) {

    }

    @GetMapping("/query")
    public CompletableFuture<QueryRespDTO> query(@RequestBody QueryReqDTO queryReqDTO) {

    }

}
