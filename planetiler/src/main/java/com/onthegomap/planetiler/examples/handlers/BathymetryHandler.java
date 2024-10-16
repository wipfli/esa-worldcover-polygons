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

public class BathymetryHandler implements ForwardingProfile.FeatureProcessor, ForwardingProfile.LayerPostProcesser {
  public static final String LAYER = "bathymetry";

  public BathymetryHandler(Translations translations, PlanetilerConfig config, Stats stats) {
  }

  @Override
  public void processFeature(SourceFeature sourceFeature, FeatureCollector features) {
    if (!sourceFeature.getSource().contains("bathy")) {
      return;
    }

    int depth = ((Number) sourceFeature.getTag("depth")).intValue();

    // skip depth 0 since it's already included in globallandcover layer (class water)
    if (depth == 0) {
      return;
    }

    features.polygon(LAYER)
        .setBufferPixels(4)
        .setPixelTolerance(0.5)
        .setSortKey(depth)
        .setAttr("min_depth", depth);
  }

  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
    return FeatureMerge.mergeNearbyPolygons(items, 1, 1, 0.2, 0.2);
  }

  @Override
  public String name() {
    return LAYER;
  }
}
