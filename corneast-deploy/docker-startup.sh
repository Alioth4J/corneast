#!/usr/bin/env bash

docker start corneast-idempotent-600{0..5}
docker start corneast-storage-00{1,2}-master
docker start corneast-storage-00{1,2}-slave-{1,2,3}
docker start corneast-storage-sentinel-0{1,2,3}

