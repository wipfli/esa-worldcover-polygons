package com.onthegomap.planetiler.examples;

import com.onthegomap.planetiler.Planetiler;
import com.onthegomap.planetiler.config.Arguments;
import com.onthegomap.planetiler.examples.handlers.EsaHandler;

import java.nio.file.Path;
import java.util.stream.IntStream;

public class GloballandcoverMain {
  public static void main(String[] args) throws Exception {
    run(Arguments.fromArgsOrConfigFile(args));
  }

  static void run(Arguments arguments) throws Exception {
    Path dataDir = Path.of("data");
    Path sourcesDir = dataDir.resolve("sources");

    int maxzoom = arguments.getInteger("maxzoom", "maximum zoom level for the map data", 11);

    if (maxzoom > 11) {
      throw new IllegalArgumentException("Max zoom must be <= 11, was " + maxzoom);
    }

    // set max zoom
    EsaHandler.zoom = IntStream.range(0, maxzoom - 2).toArray();

    Planetiler p = Planetiler.create(arguments)
        // defer creation of the profile because it depends on data from the runner
        .setProfile(GloballandcoverProfile::new)
        .addShapefileSource(GloballandcoverProfile.GLACIATED_AREAS_SOURCE,
            sourcesDir.resolve("ne_10m_glaciated_areas.zip"),
            "https://naciscdn.org/naturalearth/10m/physical/ne_10m_glaciated_areas.zip")
        .addShapefileSource(GloballandcoverProfile.BATHYMETRY_SOURCE,
            sourcesDir.resolve("ne_10m_bathymetry_all.zip"),
            "https://naciscdn.org/naturalearth/10m/physical/ne_10m_bathymetry_all.zip")
        .setOutput(dataDir.resolve("output.mbtiles"));

    for (int category : EsaHandler.categories) {
      for (int k : EsaHandler.zoom) {
        p.addGeoPackageSource("EPSG:4326",
            String.format(GloballandcoverProfile.ESA_WORLD_COVER_SOURCE + "-%d-%d", category, k),
            Path.of(String.format("../zips/%d-%d.zip", category, k)), null);
      }
    }

    p.run();
  }
}
