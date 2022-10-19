#!/bin/zsh

podman exec postgres psql -d postgres -U admin -f /var/sql/types.sql
podman exec postgres psql -d postgres -U admin -f /var/sql/schema.sql