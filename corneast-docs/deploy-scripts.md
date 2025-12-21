# Deploy with Scripts
Working directory: `corneast/corneast-deploy`  

## Idempotent
```bash
./deploy-idempotent.sh <N>
```

## Storage
Create network manually:  
```bash
docker network create corneast-storage-network
```
### Master-slave
```bash
./deploy-storage-clusters.sh <CLUSTER_COUNT> <SLAVE_COUNT>
```

### Sentinel
```bash
./deploy-storage-sentinels.sh <SENTINEL_COUNT>
```
