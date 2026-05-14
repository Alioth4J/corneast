#!/usr/bin/env bash
# Usage: ./deploy-storage-sentinels.sh SENTINEL_COUNT

set -euo pipefail

SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"
SENTINEL_COUNT=$1

NETWORK="corneast-storage-network"
IMAGE=redis:latest

BASE_PORT=27000

for ((i = 1; i <= SENTINEL_COUNT; i++)); do
    SENTINEL_ID=$(printf "%02d" "$i")
    PORT=$((BASE_PORT + i))

    CONTAINER_NAME="corneast-storage-sentinel-${SENTINEL_ID}"
    CONFIG_PATH="${SCRIPT_DIR}/storage-sentinel/${SENTINEL_ID}.conf"

    docker run -d \
        --name "${CONTAINER_NAME}" \
        --network "${NETWORK}" \
        -p "${PORT}:26379" \
        -v "${CONFIG_PATH}:/usr/local/etc/redis/sentinel.conf" \
        "${IMAGE}" \
        redis-sentinel /usr/local/etc/redis/sentinel.conf
done

