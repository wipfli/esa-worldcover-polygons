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

public class NaturalEarthHandler implements ForwardingProfile.FeatureProcessor, ForwardingProfile.FeaturePostProcessor {
  private final PlanetilerConfig config;

  public NaturalEarthHandler(Translations translations, PlanetilerConfig config, Stats stats) {
    this.config = config;
  }

  @Override
  public void processFeature(SourceFeature sourceFeature, FeatureCollector features) {
    Object minZoomTag = sourceFeature.getTag("min_zoom");

    if (minZoomTag == null) {
      return;
    }
    int minZoom = minZoomTag instanceof Integer ? (int) minZoomTag : (int) Math.round((double) minZoomTag);

    record NaturalEarthInfo(int minZoom, String clazz) {
    }
    NaturalEarthInfo info = switch (sourceFeature.getSourceLayer()) {
      case "ne_10m_ocean" -> new NaturalEarthInfo(minZoom, "water");
      case "ne_10m_land", "ne_10m_minor_islands" -> new NaturalEarthInfo(minZoom, "land");
      case "ne_10m_glaciated_areas" -> new NaturalEarthInfo(minZoom, "ice");
      default -> null;
    };

    if (info == null) {
      return;
    }

    if (!GloballandcoverProfile.includedClasses.contains(info.clazz)) {
      return;
    }

    features.polygon(GloballandcoverProfile.LAYER)
        .setMinZoom(info.minZoom)
        .setMaxZoom(config.maxzoom())
        .setAttr("class", info.clazz);
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
