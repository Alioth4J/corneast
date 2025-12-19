#!/usr/bin/env bash
# Usage: ./deploy.sh N

N=$1
BASE=6000
NETWORK=corneast-idempotent-network
DIR=/var/home/alioth4j/code/corneast/corneast-deploy/idempotent

docker network create $NETWORK >/dev/null 2>&1

for i in $(seq 0 $((N-1))); do
    PORT=$((BASE+i))
    docker run -d \
        --name corneast-idempotent-$PORT \
        --network $NETWORK \
        -p $PORT:6379 \
        -v $DIR/$PORT.conf:/usr/local/etc/redis/redis.conf \
        redis:latest redis-server /usr/local/etc/redis/redis.conf
done

HOSTS=$(for i in $(seq 0 $((N-1))); do PORT=$((BASE+i)); echo -n "corneast-idempotent-$PORT:6379 "; done)

docker exec corneast-idempotent-$BASE \
    redis-cli --cluster create $HOSTS --cluster-replicas 0 --cluster-yes

