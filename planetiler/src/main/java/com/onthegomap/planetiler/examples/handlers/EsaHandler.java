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

public class EsaHandler implements ForwardingProfile.FeatureProcessor, ForwardingProfile.LayerPostProcesser {
  /**
   * 10 Tree cover (forest)
   * 20 Shrubland (shrub)
   * 30 Grassland (grass)
   * 40 Cropland (crop)
   * 50 Built-up (urban)
   * 60 Bare / sparse vegetation (barren)
   * 70 Snow and ice (snow)
   * 90 Herbaceous wetland (wetland)
   * 95 Mangroves (mangroves)
   * 100 Moss and lichen (moss)
   */
  public static int[] categories = {10, 20, 30, 40, 50, 60, 70, 90, 95, 100};
  public static int[] zoom;

  public EsaHandler(Translations translations, PlanetilerConfig config, Stats stats) {
  }

  @Override
  public void processFeature(SourceFeature sourceFeature, FeatureCollector features) {
    // This check should not be necessary, but the esa handler gets called with NE data.
    if (!sourceFeature.getSource().contains("esa")) {
      return;
    }
    double pixelTolerance = 0.5;
    String sourceName = sourceFeature.getSource();

    String[] sourceNameParts = sourceName.split("-");

    String classification = switch (sourceNameParts[1]) {
      case "10" -> "forest";
      case "20" -> "shrub";
      case "30" -> "grass";
      case "40" -> "crop";
      case "50" -> "urban";
      case "60" -> "barren";
      case "70" -> "snow";
      case "90" -> "wetland";
      case "95" -> "mangroves";
      case "100" -> "moss";
      default -> "";
    };

    if (!GloballandcoverProfile.includedClasses.contains(classification)) {
      return;
    }

    int minZoom = switch (sourceNameParts[2]) {
      case "1" -> 3;
      case "2" -> 4;
      case "3" -> 6;
      case "4" -> 7;
      case "5" -> 8;
      case "6" -> 9;
      case "7" -> 10;
      case "8" -> 11;
      default -> 0;
    };

    int maxZoom = switch (sourceNameParts[2]) {
      case "1" -> 3;
      case "2" -> 5;
      case "3" -> 6;
      case "4" -> 7;
      case "5" -> 8;
      case "6" -> 9;
      case "7" -> 10;
      case "8" -> 11;
      default -> 2;
    };

    features.polygon(GloballandcoverProfile.LAYER)
        .setMinZoom(minZoom)
        .setMaxZoom(maxZoom)
        .setAttr("class", classification)
        .setPixelTolerance(pixelTolerance);
  }

  @Override
  public List<VectorTile.Feature> postProcess(int zoom, List<VectorTile.Feature> items) throws GeometryException {
    double area = 4.0;
    return FeatureMerge.mergeNearbyPolygons(items, area, area, 0.5, 0.5);
  }

  @Override
  public String name() {
    return GloballandcoverProfile.LAYER;
  }
}
