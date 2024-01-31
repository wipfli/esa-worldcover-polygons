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

    // skip depth 0 since it's already included in globallandcover layer (class water)
    if (depth == 0) {
      return;
    }

    features.polygon(LAYER)
        .setBufferPixels(4)
        .setPixelTolerance(0.5)
        .setMinZoom(0)
        .setMaxZoom(Math.max(config.maxzoom(), 7))
        .setSortKey(depth)
        .setAttr("min_depth", depth);
  }

  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
    return FeatureMerge.mergeNearbyPolygons(items, 1, 1, 0.1, 0.1);
  }

  @Override
  public String name() {
    return LAYER;
  }
}
