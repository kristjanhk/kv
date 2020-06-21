#!/usr/bin/env bash

docker run \
-p 5432:5432 \
-v kvdb:/var/lib/postgresql/data \
--env POSTGRES_USER=username \
--env POSTGRES_PASSWORD=password \
--name kvdb \
postgres:12