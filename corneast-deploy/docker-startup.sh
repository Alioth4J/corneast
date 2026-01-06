#!/bin/bash

sudo setenforce 0

sudo docker start corneast-idempotent-600{0,1,2}
sudo docker start corneast-storage-00{1,2}-master
sudo docker start corneast-storage-00{1,2}-slave-{1,2,3}
sudo docker start corneast-storage-sentinel-0{1,2,3}

