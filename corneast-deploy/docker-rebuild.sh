#!/bin/bash

set -euo pipefail

SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"

docker ps -q -f "name=^corneast" | xargs -r docker stop

docker rm $(sudo docker ps -a -q -f "name=^corneast")

docker network rm corneast-storage-network corneast-idempotent-network

# create idempotent instances
sh "${SCRIPT_DIR}/deploy-idempotent.sh" 6

# create storage instances
docker network create corneast-storage-network
sh "${SCRIPT_DIR}/deploy-storage-clusters.sh" 2 3
sh "${SCRIPT_DIR}/deploy-storage-sentinels.sh" 3
