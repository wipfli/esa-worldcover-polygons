#!/usr/bin/env bash
set -e
./mvnw clean package
java -cp target/*-with-deps.jar com.onthegomap.planetiler.examples.GloballandcoverMain --output-layerstats --maxzoom=9 --output=data/esa-worldcover-polygons-v2.pmtiles --download --force
