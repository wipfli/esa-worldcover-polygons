#!/usr/bin/env bash
set -e
./mvnw clean package --file standalone.pom.xml
java -cp target/*-with-deps.jar com.onthegomap.planetiler.examples.SwissMap --maxzoom=10
docker run --rm -it -v "$(pwd)/data":/data -p 8080:8080 maptiler/tileserver-gl -p 8080

