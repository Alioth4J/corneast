# Corneast
Distributed Token Middleware  

## Wrk Test Result
Single server  
```bash
$ ~/wrk/wrk -t10 -c100 -d1m -s ./wrk.lua http://127.0.0.1:8080/corneast/reduce
Running 1m test @ http://127.0.0.1:8080/corneast/reduce
  10 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     3.55ms    4.90ms 328.34ms   96.17%
    Req/Sec     3.19k   531.13     3.91k    92.15%
  1905131 requests in 1.00m, 378.26MB read
Requests/sec:  31738.98
Transfer/sec:      6.30MB
```
