#!/usr/bin/env bash
set -e
./mvnw clean package
java -cp target/*-with-deps.jar com.onthegomap.planetiler.examples.GloballandcoverMain --output-layerstats --maxzoom=5 --output=data/esa-worldcover-polygons.pmtiles --download --force

