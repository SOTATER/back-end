#!/bin/bash

docker exec postgres psql -d postgres -U admin -f /var/sql/types.sql
docker exec postgres psql -d postgres -U admin -f /var/sql/schema.sql