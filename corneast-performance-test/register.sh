#!/bin/bash

{ for i in {0..9}; do
    echo "SET key-test-$i 5000"
done; } | redis-cli --pipe