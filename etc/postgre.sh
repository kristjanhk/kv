#!/usr/bin/env bash

docker run \
-p 5999:5432 \
-d \
--restart always \
-v postgre:/var/lib/postgresql/data \
--env POSTGRES_USER=kv \
--env POSTGRES_PASSWORD=kv \
--name kvdb \
postgres:12