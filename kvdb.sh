#!/usr/bin/env bash

docker run \
-p 5999:5432 \
-d \
--restart always \
-v kvdb:/var/lib/postgresql/data \
--env POSTGRES_USER=kristjank \
--env POSTGRES_PASSWORD=kristjank \
--name kvdb \
postgres:12