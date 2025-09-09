## corneast-deploy
### Components to Be Deployed
- Redis for storage
- Redis for idempotence

### Redis For Storage
#### Create Docker Network
```bash
sudo docker network create corneast-redis-network
```

#### Clusters
Users can deploy any number of clusters according to their business, and custom the composition of a cluster as well.  

Here is an example of deploying **one** cluster. You may repeat this operation several times to deploy other clusters. Note that you need to use your own arguments in the following bash statements.  

```bash
# deploy master
sudo docker run -d \
  --name corneast-redis-master-1 \
  --network corneast-redis-network \
  -p 7001:6379 \
  -v /var/home/alioth4j/code/corneast/corneast-deploy/1/master-1.conf:/usr/local/etc/redis/redis.conf \
  -v /var/home/alioth4j/code/corneast/data:/data \
  redis:latest \
  redis-server /usr/local/etc/redis/redis.conf

# deploy slave
sudo docker run -d \
  --name corneast-redis-slave-1 \
  --network corneast-redis-network \
  -p 7002:6379 \
  -v /var/home/alioth4j/code/corneast/corneast-deploy/1/slave-1.conf:/usr/local/etc/redis/redis.conf \
  -v /var/home/alioth4j/code/corneast/data:/data \
  redis:latest \
  redis-server /usr/local/etc/redis/redis.conf

# deploy sentinel
sudo docker run -d \
  --name corneast-redis-sentinel-1 \
  --network corneast-redis-network \
  -p 27001:26379 \
  -v /var/home/alioth4j/code/corneast/corneast-deploy/1/sentinel-1.conf:/usr/local/etc/redis/sentinel.conf \
  -v /var/home/alioth4j/code/corneast/data:/data \
  redis:latest \
  redis-sentinel /usr/local/etc/redis/sentinel.conf
```

### Redis For Idempotence
#### Create Docker Network
```bash
sudo docker network create corneast-idempotent-network
```

#### Deploy Instances
Here is an example of deploy **one** instance. You may repeat the following operation several times to deploy other instances. Note that you need to use your own arguments in the following bash statements.  

```bash
sudo docker run -d \
  --name corneast-idempotent-6000 \
  --network corneast-idempotent-network \
  -p 6000:6379 \
  -v /var/home/alioth4j/code/corneast/corneast-deploy/idempotent/6000.conf:/usr/local/etc/redis/redis.conf \
  -v /var/home/alioth4j/code/corneast/data:/data \
  redis:latest \
  redis-server /usr/local/etc/redis/redis.conf
```

#### Create Cluster
```bash
sudo docker exec -it corneast-idempotent-6000 \
  redis-cli --cluster create \
  corneast-idempotent-6000:6379 \
  corneast-idempotent-6001:6379 \
  corneast-idempotent-6002:6379 \
  --cluster-replicas 0
```
