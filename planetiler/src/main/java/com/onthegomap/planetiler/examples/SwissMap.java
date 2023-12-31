package com.onthegomap.planetiler.examples;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.Planetiler;
import com.onthegomap.planetiler.Profile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.config.Arguments;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.reader.osm.OsmElement;
import com.onthegomap.planetiler.reader.osm.OsmRelationInfo;
import com.onthegomap.planetiler.util.ZoomFunction;

import java.nio.file.Path;
import java.util.List;
import java.io.File;

public class SwissMap implements Profile {

  @Override
  public void processFeature(SourceFeature sourceFeature, FeatureCollector features) {

    Double pixelTolerance = 0.5;
    Integer zoom = 3;
    String sourceName = sourceFeature.getSource();
    if (sourceName.equals("0")) {
      features.polygon("forest")
        .setMinZoom(0)
        .setMaxZoom(zoom)
        .setPixelTolerance(pixelTolerance);
    }

    zoom += 1;

    if (sourceName.equals("1")) {
      features.polygon("forest")
        .setMinZoom(zoom)
        .setMaxZoom(zoom)
        .setPixelTolerance(pixelTolerance);
    }

    zoom += 1;

    if (sourceName.equals("2")) {
      features.polygon("forest")
        .setMinZoom(zoom)
        .setMaxZoom(zoom)
        .setPixelTolerance(pixelTolerance);
    }

    zoom += 1;

    if (sourceName.equals("3")) {
      features.polygon("forest")
        .setMinZoom(zoom)
        .setMaxZoom(zoom)
        .setPixelTolerance(pixelTolerance);
    }

    zoom += 1;

    if (sourceName.equals("4")) {
      features.polygon("forest")
        .setMinZoom(zoom)
        .setMaxZoom(zoom)
        .setPixelTolerance(pixelTolerance);
    }

    zoom += 1;

    if (sourceName.equals("5")) {
      features.polygon("forest")
        .setMinZoom(zoom)
        .setMaxZoom(14)
        .setPixelTolerance(pixelTolerance);
    }

    zoom += 1;

    if (sourceName.equals("6")) {
      features.polygon("forest")
        .setMinZoom(zoom)
        .setMaxZoom(14)
        .setPixelTolerance(pixelTolerance);
    }

    zoom += 1;

    if (sourceName.equals("7")) {
      features.polygon("forest")
        .setMinZoom(zoom)
        .setMaxZoom(14)
        .setPixelTolerance(pixelTolerance);
    }

    zoom += 1;

    if (sourceName.equals("8")) {
      features.polygon("forest")
        .setMinZoom(zoom)
        .setMaxZoom(14)
        .setPixelTolerance(pixelTolerance);
    }

  }

  @Override
  public List<VectorTile.Feature> postProcessLayerFeatures(String layer, int zoom,
    List<VectorTile.Feature> items) {

    if ("forest".equals(layer)) {
      try {
        double area = 4.0;
        return FeatureMerge.mergeNearbyPolygons(items, area, area, 1, 1);
      }
      catch (GeometryException e) {
        return null;
      }
    }

    return null;
  }

  @Override
  public String name() {
    return "Global Forest Polygons";
  }

  @Override
  public String description() {
    return "Global Forest Polygons";
  }

  @Override
  public String attribution() {
    return "<a href=\"https://worldcover2021.esa.int/\">ESA</a>";
  }

  public static void main(String[] args) throws Exception {
    run(Arguments.fromArgsOrConfigFile(args));
  }

  static void run(Arguments args) throws Exception {
    
    Planetiler p = Planetiler.create(args)
      .setProfile(new SwissMap())
      .overwriteOutput("pmtiles", Path.of("data", "global-forest-polygons.pmtiles"));
    
    
    p.addGeoPackageSource("EPSG:4326", "0", Path.of("../zips/0.zip"), "");
    p.addGeoPackageSource("EPSG:4326", "1", Path.of("../zips/1.zip"), "");
    p.addGeoPackageSource("EPSG:4326", "2", Path.of("../zips/2.zip"), "");
    p.addGeoPackageSource("EPSG:4326", "3", Path.of("../zips/3.zip"), "");
    p.addGeoPackageSource("EPSG:4326", "4", Path.of("../zips/4.zip"), "");
    p.addGeoPackageSource("EPSG:4326", "5", Path.of("../zips/5.zip"), "");
    p.addGeoPackageSource("EPSG:4326", "6", Path.of("../zips/6.zip"), "");
    p.addGeoPackageSource("EPSG:4326", "7", Path.of("../zips/7.zip"), "");
    p.addGeoPackageSource("EPSG:4326", "8", Path.of("../zips/8.zip"), "");

    p.run();
  }
}