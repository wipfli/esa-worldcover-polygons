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

    Double pixelTolerance = 0.0; // 0.5;
    String sourceName = sourceFeature.getSource();

    String category = "";
    if (sourceName.startsWith("10-")) {
      category = "Tree cover";
    }
    if (sourceName.startsWith("20-")) {
      category = "Shrubland";
    }
    if (sourceName.startsWith("30-")) {
      category = "Grassland";
    }
    if (sourceName.startsWith("40-")) {
      category = "Cropland";
    }
    if (sourceName.startsWith("50-")) {
      category = "Built-up";
    }
    if (sourceName.startsWith("60-")) {
      category = "Bare / sparse vegetation";
    }
    if (sourceName.startsWith("70-")) {
      category = "Snow and ice";
    }
    if (sourceName.startsWith("80-")) {
      category = "Permanent water bodies";
    }
    if (sourceName.startsWith("90-")) {
      category = "Herbaceous wetland";
    }
    if (sourceName.startsWith("95-")) {
      category = "Mangroves";
    }
    if (sourceName.startsWith("100-")) {
      category = "Moss and lichen";
    }
    if (sourceName.startsWith("110-")) {
      category = "Land";
    }

    Integer zoom = 3;

    if (sourceName.endsWith("-0")) {
      features.polygon("landcover")
        .setMinZoom(0)
        .setMaxZoom(zoom)
        .setAttr("category", category)
        .setPixelTolerance(pixelTolerance);
    }

    zoom = 4;
    if (sourceName.endsWith("-1")) {
      features.polygon("landcover")
        .setMinZoom(zoom)
        .setMaxZoom(zoom)
        .setAttr("category", category)
        .setPixelTolerance(pixelTolerance);
    }

    zoom = 5;
    if (sourceName.endsWith("-2")) {
      features.polygon("landcover")
        .setMinZoom(zoom)
        .setMaxZoom(zoom)
        .setAttr("category", category)
        .setPixelTolerance(pixelTolerance);
    }

    zoom = 6;
    if (sourceName.endsWith("-3")) {
      features.polygon("landcover")
        .setMinZoom(zoom)
        .setMaxZoom(zoom)
        .setAttr("category", category)
        .setPixelTolerance(pixelTolerance);
    }

    zoom = 7;
    if (sourceName.endsWith("-4")) {
      features.polygon("landcover")
        .setMinZoom(zoom)
        .setMaxZoom(zoom)
        .setAttr("category", category)
        .setPixelTolerance(pixelTolerance);
    }

    zoom = 8;
    if (sourceName.endsWith("-5")) {
      features.polygon("landcover")
        .setMinZoom(zoom)
        .setMaxZoom(zoom)
        .setAttr("category", category)
        .setPixelTolerance(pixelTolerance);
    }

    zoom = 9;
    if (sourceName.endsWith("-6")) {
      features.polygon("landcover")
        .setMinZoom(zoom)
        .setMaxZoom(zoom)
        .setAttr("category", category)
        .setPixelTolerance(pixelTolerance);
    }

    zoom = 10;
    if (sourceName.endsWith("-7")) {
      features.polygon("landcover")
        .setMinZoom(zoom)
        .setMaxZoom(zoom)
        .setAttr("category", category)
        .setPixelTolerance(pixelTolerance);
    }

    zoom = 11;
    if (sourceName.endsWith("-8")) {
      features.polygon("landcover")
        .setMinZoom(zoom)
        .setMaxZoom(zoom)
        .setAttr("category", category)
        .setPixelTolerance(pixelTolerance);
    }
  }

  @Override
  public List<VectorTile.Feature> postProcessLayerFeatures(String layer, int zoom,
    List<VectorTile.Feature> items) {

    if ("landcover".equals(layer)) {
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
    return "ESA Worldcover Polygons";
  }

  @Override
  public String description() {
    return "ESA Worldcover Polygons";
  }

  @Override
  public String attribution() {
    return "<a href=\"https://worldcover2021.esa.int/\">©ESA Worldcover</a>";
  }

  public static void main(String[] args) throws Exception {
    run(Arguments.fromArgsOrConfigFile(args));
  }

  static void run(Arguments args) throws Exception {
    
    Planetiler p = Planetiler.create(args)
      .setProfile(new SwissMap())
      .overwriteOutput("pmtiles", Path.of("data", "esa-worldcover-polygons.pmtiles"));
    
    
    int[] categories = {10, 20, 30, 40, 50, 60, 70, 80, 90, 95, 100, 110};
    for (int category : categories) {
      for (int k = 0; k <= 7; ++k) {
        p.addGeoPackageSource("EPSG:4326", String.format("%d-%d", category, k), Path.of(String.format("../zips/%d-%d.zip", category, k)), "");
      }
    }

    p.run();
  }
}