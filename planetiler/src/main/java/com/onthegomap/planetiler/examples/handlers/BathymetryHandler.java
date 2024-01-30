package com.onthegomap.planetiler.examples.handlers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.stats.Stats;
import com.onthegomap.planetiler.util.Translations;

import java.util.List;

public class BathymetryHandler implements ForwardingProfile.FeatureProcessor, ForwardingProfile.FeaturePostProcessor {
  public static final String LAYER = "bathymetry";
  private final PlanetilerConfig config;

  public BathymetryHandler(Translations translations, PlanetilerConfig config, Stats stats) {
    this.config = config;
  }

  @Override
  public void processFeature(SourceFeature sourceFeature, FeatureCollector features) {
    int depth = ((Number) sourceFeature.getTag("depth")).intValue();

    features.polygon(LAYER)
        .setBufferPixels(4)
        .setPixelTolerance(0.5)
        .setMinZoom(0)
        .setMaxZoom(Math.max(config.maxzoom(), 7))
        .setAttr("min_depth", depth);
  }

  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
    int area = switch (zoom) {
      case 1 -> 2;
      case 2 -> 4;
      case 3 -> 6;
      case 4 -> 8;
      default -> 10;
    };

    return FeatureMerge.mergeNearbyPolygons(items, area, area, 1, 1);
  }

  @Override
  public String name() {
    return LAYER;
  }
}
