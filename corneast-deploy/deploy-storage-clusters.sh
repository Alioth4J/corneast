#!/usr/bin/env bash
# Usage: ./deploy-storage-clusters.sh CLUSTER_COUNT SLAVE_COUNT

set -euo pipefail

CLUSTER_COUNT=$1
SLAVE_COUNT=$2

NETWORK="corneast-storage-network"
BASE_DIR="/var/home/alioth4j/code/corneast/corneast-deploy"
IMAGE="redis:latest"

for ((i = 1; i <= CLUSTER_COUNT; i++)); do
    CLUSTER_ID=$(printf "%03d" "$i")
    PORT_BASE=$((7000 + i * 100))

    MASTER_NAME="corneast-storage-${CLUSTER_ID}-master"
    MASTER_CONF="${BASE_DIR}/storage-${CLUSTER_ID}/master.conf"
    MASTER_PORT=$((PORT_BASE + 0))

    docker run -d \
        --name "${MASTER_NAME}" \
        --network "${NETWORK}" \
        -p "${MASTER_PORT}:6379" \
        -v "${MASTER_CONF}:/usr/local/etc/redis/redis.conf" \
        "${IMAGE}" \
        redis-server /usr/local/etc/redis/redis.conf

    for ((s = 1; s <= SLAVE_COUNT; s++)); do
        SLAVE_NAME="corneast-storage-${CLUSTER_ID}-slave-${s}"
        SLAVE_CONF="${BASE_DIR}/storage-${CLUSTER_ID}/slave-${s}.conf"
        SLAVE_PORT=$((PORT_BASE + s))

        docker run -d \
            --name "${SLAVE_NAME}" \
            --network "${NETWORK}" \
            -p "${SLAVE_PORT}:6379" \
            -v "${SLAVE_CONF}:/usr/local/etc/redis/redis.conf" \
            "${IMAGE}" \
            redis-server /usr/local/etc/redis/redis.conf
    done
done

