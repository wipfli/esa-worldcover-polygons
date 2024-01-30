package com.onthegomap.planetiler.examples.handlers;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.VectorTile;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.examples.GloballandcoverProfile;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;
import com.onthegomap.planetiler.stats.Stats;
import com.onthegomap.planetiler.util.Translations;

import java.util.List;

public class GlaciatedAreasHandler implements ForwardingProfile.FeatureProcessor, ForwardingProfile.FeaturePostProcessor {
  private final PlanetilerConfig config;

  public GlaciatedAreasHandler(Translations translations, PlanetilerConfig config, Stats stats) {
    this.config = config;
  }

  @Override
  public void processFeature(SourceFeature sourceFeature, FeatureCollector features) {
    features.polygon(GloballandcoverProfile.LAYER)
        .setMinZoom(0)
        .setMaxZoom(config.maxzoom())
        .setAttr("class", "ice");
  }

  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
    return FeatureMerge.mergeNearbyPolygons(items, 1, 1, 0.1, 0.1);
  }

  @Override
  public String name() {
    return GloballandcoverProfile.LAYER;
  }
}
