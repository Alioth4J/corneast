# Corneast

20 cores CPU, 16GB Memory, 5 Redis Instances  
2025-03-26  
(It seems that CPU becomes the bottleneck.)  

| Label          | # Samples | Average | Median | 90% Line | 95% Line | 99% Line | Min | Max  | Error % | Throughput   | Received KB/sec | Sent KB/sec |
|----------------|-----------|---------|--------|----------|----------|----------|-----|------|---------|--------------|-----------------|-------------|
| HTTP Request   | 100000    | 241     | 270    | 325      | 338      | 379      | 1   | 1171 | 0.000%  | 3523.60817   | 712.29          | 726.06      |
| TOTAL          | 100000    | 241     | 270    | 325      | 338      | 379      | 1   | 1171 | 0.000%  | 3523.60817   | 712.29          | 726.06      |

---
20 cores CPU, 16GB Memory, 3 Redis Instances  
2025-03-26  

| Label         | # Samples | Average | Median | 90% Line | 95% Line | 99% Line | Min | Max | Error %   | Throughput  | Received KB/sec | Sent KB/sec |
|---------------|----------:|--------:|-------:|---------:|---------:|---------:|----:|----:|----------:|------------:|----------------:|------------:|
| HTTP Request  | 100,000   | 847     | 649    | 1,492    | 1,769    | 1,860    | 51  | 3,981 | 0.000%    | 3,037.85163 | 614.09          | 625.96      |
| TOTAL         | 100,000   | 847     | 649    | 1,492    | 1,769    | 1,860    | 51  | 3,981 | 0.000%    | 3,037.85163 | 614.09          | 625.96      |

---
20 cores CPU, 16GB Memory  
2025-03-21  

| Label         | # Samples | Average | Median | 90% Line | 95% Line | 99% Line | Min | Max   | Error % | Throughput   | Received KB/sec | Sent KB/sec |
|---------------|-----------|---------|--------|----------|----------|----------|-----|-------|---------|--------------|-----------------|-------------|
| HTTP Request  | 100000    | 6210    | 4941   | 7127     | 10133    | 54115    | 67  | 76329 | 0.065%  | 1039.93344   | 211.80          | 214.14      |
| TOTAL         | 100000    | 6210    | 4941   | 7127     | 10133    | 54115    | 67  | 76329 | 0.065%  | 1039.93344   | 211.80          | 214.14      |