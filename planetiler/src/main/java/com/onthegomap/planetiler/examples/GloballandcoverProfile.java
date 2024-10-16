package com.onthegomap.planetiler.examples;

import com.onthegomap.planetiler.ForwardingProfile;
import com.onthegomap.planetiler.Planetiler;
import com.onthegomap.planetiler.config.PlanetilerConfig;
import com.onthegomap.planetiler.examples.handlers.BathymetryHandler;
import com.onthegomap.planetiler.examples.handlers.EsaHandler;
import com.onthegomap.planetiler.examples.handlers.NaturalEarthHandler;
import com.onthegomap.planetiler.stats.Stats;
import com.onthegomap.planetiler.util.Translations;
import java.util.List;
import java.util.stream.Collectors;

public class GloballandcoverProfile extends ForwardingProfile {
  public static final String ESA_WORLD_COVER_SOURCE = "esa_world_cover";
  public static final String BATHYMETRY_SOURCE = "natural_earth_bathymetry";
  public static final String NATURAL_EARTH_SOURCE = "natural_earth";
  public static final String LAYER = "globallandcover";
  public static List<String> includedClasses;


  public GloballandcoverProfile(Planetiler runner) {
    this(runner.translations(), runner.config(), runner.stats());
  }

  public GloballandcoverProfile(Translations translations, PlanetilerConfig config, Stats stats) {
    List<String> availableClasses = List.of("tree", "shrub", "grass", "crop", "urban", "barren", "ice", "water", "herbaceous", "mangroves", "moss", "land");
    List<String> excludedClasses = config.arguments().getList("exclude_classes", "Exclude certain classes", List.of());
    includedClasses = availableClasses.stream().filter(i -> !excludedClasses.contains(i))
        .collect(Collectors.toList());

    EsaHandler esa = new EsaHandler(translations, config, stats);
    registerHandler(esa);
    for (int category : EsaHandler.categories) {
      for (int k : EsaHandler.zoom) {
        String source = String.format(GloballandcoverProfile.ESA_WORLD_COVER_SOURCE + "-%d-%d", category, k);
        registerSourceHandler(source, esa::processFeature);
      }
    }

    NaturalEarthHandler naturalEarth = new NaturalEarthHandler(translations, config, stats);
    registerHandler(naturalEarth);
    registerSourceHandler(GloballandcoverProfile.NATURAL_EARTH_SOURCE, naturalEarth::processFeature);

    BathymetryHandler bathymetry = new BathymetryHandler(translations, config, stats);
    registerHandler(bathymetry);
    registerSourceHandler(GloballandcoverProfile.BATHYMETRY_SOURCE, bathymetry::processFeature);

  }

  @Override
  public String name() {
    return "Global Landcover";
  }

  @Override
  public String description() {
    return "Land cover as polygons for the whole world, the following coverages are classified: " + String.join(", ", includedClasses) + ". Additionally bathymetry is included.";

  }

  @Override
  public String attribution() {
    return "<a href=\"https://worldcover2020.esa.int\">© ESA WorldCover</a>";
  }
}