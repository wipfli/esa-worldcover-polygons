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

    String[] sourceNameParts = sourceName.split("-");

    String category = "";
    switch (sourceNameParts[0]) {
      case "10":
        category = "Tree cover";
        break;
      case "20":
        category = "Shrubland";
        break;
      case "30":
        category = "Grassland";
        break;
      case "40":
        category = "Cropland";
        break;
      case "50":
        category = "Built-up";
        break;
      case "60":
        category = "Bare / sparse vegetation";
        break;
      case "70":
        category = "Snow and ice";
        break;
      case "80":
        category = "Permanent water bodies";
        break;
      case "90":
        category = "Herbaceous wetland";
        break;
      case "95":
        category = "Mangroves";
        break;
      case "100":
        category = "Moss and lichen";
        break;
      case "110":
        category = "Land";
        break;
    }

    Integer minZoom = 0;
    Integer maxZoom = 3;

    switch (sourceNameParts[1]) {
      case "0":
        minZoom = 0;
        maxZoom = 3;
        break;
      case "1":
        minZoom = 4;
        maxZoom = 4;
        break;
      case "2":
        minZoom = 5;
        maxZoom = 5;
        break;
      case "3":
        minZoom = 6;
        maxZoom = 6;
        break;
      case "4":
        minZoom = 7;
        maxZoom = 7;
        break;
      case "5":
        minZoom = 8;
        maxZoom = 8;
        break;
      case "6":
        minZoom = 9;
        maxZoom = 9;
        break;
      case "7":
        minZoom = 10;
        maxZoom = 10;
        break;
    }

    features.polygon("landcover")
            .setMinZoom(minZoom)
            .setMaxZoom(maxZoom)
            .setAttr("category", category)
            .setPixelTolerance(pixelTolerance);
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