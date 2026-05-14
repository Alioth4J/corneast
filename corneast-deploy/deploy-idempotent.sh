#!/usr/bin/env bash
# Usage: ./deploy-idempotent.sh N

set -euo pipefail

SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"
N=$1
BASE=6000
NETWORK=corneast-idempotent-network
IMAGE=redis:latest

docker network create $NETWORK >/dev/null 2>&1

for i in $(seq 0 $((N-1))); do
    PORT=$((BASE+i))
    docker run -d \
        --name corneast-idempotent-$PORT \
        --network $NETWORK \
        -p $PORT:6379 \
        -v "${SCRIPT_DIR}/idempotent/${PORT}.conf":/usr/local/etc/redis/redis.conf \
        $IMAGE \
        redis-server /usr/local/etc/redis/redis.conf
done

HOSTS=$(for i in $(seq 0 $((N-1))); do PORT=$((BASE+i)); echo -n "corneast-idempotent-$PORT:6379 "; done)

docker exec corneast-idempotent-$BASE \
    redis-cli --cluster create $HOSTS --cluster-replicas 1 --cluster-yes
