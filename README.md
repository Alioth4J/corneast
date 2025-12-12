# Corneast
Corneast is a distributed token middleware.  

## Overview  
Corneast centralizes token management so that application teams can enforce resource quotas, concurrency limits, and fairness policies without duplicating stateful logic. The system relies on Redis master-slave-sentinel clusters for hot data, a dedicated Redis cluster for idempotence tracking, and auto‑discovered core nodes registered through Eureka. The Java client ships with Protobuf contracts so that any JVM service can interact with the cluster over low‑latency Netty connections.  

### Primary use cases  
- Rate limiting and distributed throttling across microservices  
- Managing limited inventory tokens safely  
- Coordinating reservations with idempotent guarantees  

## Key Capabilities  
- **Business‑agnostic core** – Token semantics live in `corneast‑core`, keeping product logic in downstream services.  
- **Ultra‑low latency path** – Netty, LMAX Disruptor, and Redisson keep the hot path lock‑free and event‑driven.  
- **Multi‑cluster storage** – Horizontal scaling through multiple Redis clusters for state plus a dedicated idempotence cluster.  
- **Pluggable clients** – Official Java client with generated Protobuf DTOs and builders to reduce manual serialization work.  

## Architecture at a Glance  
- **Core (`corneast‑core`)** – Processes REGISTER/REDUCE/RELEASE/QUERY operations, talks to Redis clusters, and exposes Netty endpoint.  
- **Eureka (`corneast‑eureka`)** – Spring Cloud Eureka server that tracks available core nodes and keeps heartbeats tuned for fast failover.  
- **Client (`corneast‑client`)** – Java library that builds/sends Protobuf requests via `CorneastRequest`/`CorneastRequestBuilder` and a socket client.  
- **Common (`corneast‑common`)** – Shared operations enum, DTO definitions, and banners.  
- **Docs & Deploy** – `corneast‑docs` hosts how‑to guides; `corneast‑deploy` contains Docker and configuration examples for Redis clusters.  

## Building
Run `./mvnw clean install -DskipTests` from the root directory of the project.  

The built jar files will be located in the `target` directory of each module.  

## Running
```java
java -jar corneast-<module>-<version>.jar
```

## Reporting Issues
Use Github Issues.  

## Contributing
Contributions to Corneast are welcome!  
