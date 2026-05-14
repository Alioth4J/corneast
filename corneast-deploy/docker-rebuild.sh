#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"

docker ps -q -f "name=^corneast" | xargs -r docker stop

docker ps -a -q -f "name=^corneast" | xargs -r docker rm

{ docker network ls -q -f name='corneast-storage-network' | grep -q . && docker network rm corneast-storage-network; } || true
{ docker network ls -q -f name='corneast-idempotent-network' | grep -q . && docker network rm corneast-idempotent-network; } || true

# create idempotent instances
sh "${SCRIPT_DIR}/deploy-idempotent.sh" 6

# create storage instances
docker network create corneast-storage-network
sh "${SCRIPT_DIR}/deploy-storage-clusters.sh" 2 3
sh "${SCRIPT_DIR}/deploy-storage-sentinels.sh" 3
