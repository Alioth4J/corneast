#!/bin/bash

sudo docker stop $(sudo docker ps -q -f "name=^corneast")

sudo docker rm $(sudo docker ps -a -q -f "name=^corneast")

sudo docker network rm corneast-storage-network corneast-idempotent-network

# create idempotent instances
sudo sh deploy-idempotent.sh 3

# create storage instances
sudo docker network create corneast-storage-network
sudo sh deploy-storage-clusters.sh 2 3
sudo sh deploy-storage-sentinels.sh 3

