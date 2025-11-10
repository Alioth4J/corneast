## corneast-deploy
### Components to Be Deployed
- Redis for storage
- Redis for idempotence

### Notice
#### SELinux
SELinux may cause docker startup failure.  

Disable SELinux temporarily:  
```bash
sudo setenforce 0
```

#### docker run -v
In this example we only use `-v` to mount config files, we don't mount log files to avoid permission issues.  

You can go into containers to inspect log files.  

### Redis For Storage
#### Create Docker Network
```bash
sudo docker network create corneast-redis-network
```

#### Clusters
Users can deploy any number of clusters according to their business, and custom the composition of a cluster as well.  

Here is an example of deploying **one** cluster. You may repeat this operation several times to deploy other clusters. Note that you need to use your own arguments in the following bash statements.  

> Please look into the config file of Redis sentinel (sentinel-*.conf), you have to change the real master ip in it. If using docker instance name, it won't work. You can get the master ip with the following command:  
> ```bash
> sudo docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <container_name_or_id>
> ```

```bash
# deploy master
sudo docker run -d \
  --name corneast-redis-master-1 \
  --network corneast-redis-network \
  -p 7001:6379 \
  -v /var/home/alioth4j/code/corneast/corneast-deploy/1/master-1.conf:/usr/local/etc/redis/redis.conf \
  redis:latest \
  redis-server /usr/local/etc/redis/redis.conf

# deploy slave
sudo docker run -d \
  --name corneast-redis-slave-1 \
  --network corneast-redis-network \
  -p 7002:6379 \
  -v /var/home/alioth4j/code/corneast/corneast-deploy/1/slave-1.conf:/usr/local/etc/redis/redis.conf \
  redis:latest \
  redis-server /usr/local/etc/redis/redis.conf

# deploy sentinel
sudo docker run -d \
  --name corneast-redis-sentinel-1 \
  --network corneast-redis-network \
  -p 27001:26379 \
  -v /var/home/alioth4j/code/corneast/corneast-deploy/1/sentinel-1.conf:/usr/local/etc/redis/sentinel.conf \
  redis:latest \
  redis-sentinel /usr/local/etc/redis/sentinel.conf
```

```bash
# deploy master
sudo docker run -d \
  --name corneast-redis-master-2 \
  --network corneast-redis-network \
  -p 7003:6379 \
  -v /var/home/alioth4j/code/corneast/corneast-deploy/2/master-2.conf:/usr/local/etc/redis/redis.conf \
  redis:latest \
  redis-server /usr/local/etc/redis/redis.conf

# deploy slave
sudo docker run -d \
  --name corneast-redis-slave-2 \
  --network corneast-redis-network \
  -p 7004:6379 \
  -v /var/home/alioth4j/code/corneast/corneast-deploy/2/slave-2.conf:/usr/local/etc/redis/redis.conf \
  redis:latest \
  redis-server /usr/local/etc/redis/redis.conf

# deploy sentinel
sudo docker run -d \
  --name corneast-redis-sentinel-2 \
  --network corneast-redis-network \
  -p 27002:26379 \
  -v /var/home/alioth4j/code/corneast/corneast-deploy/2/sentinel-2.conf:/usr/local/etc/redis/sentinel.conf \
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

### Start/Stop/Remove Commands
```bash
sudo docker start corneast-redis-{master,slave,sentinel}-{1,2}
sudo docker start corneast-idempotent-{6000,6001,6002}

sudo docker stop corneast-redis-{master,slave,sentinel}-{1,2}
sudo docker stop corneast-idempotent-{6000,6001,6002}

sudo docker rm corneast-redis-{master,slave,sentinel}-{1,2}
sudo docker rm corneast-idempotent-{6000,6001,6002}
```
