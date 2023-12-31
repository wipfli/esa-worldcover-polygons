# global-forest-polygons

## Demo

https://wipfli.github.io/global-forest-polygons

<a href="https://wipfli.github.io/global-forest-polygons">
<img src="screenshot.png" width=450>
</a>

## Steps

Download the ESA Worldcover GeoTiffs with:

```bash
cd downloads/
./download.sh
./unzip-all.sh
```

Create a Virtual Raster (VRT) `mosaic.vrt` that combines all the ESA GeoTiffs into one file:

```bash
gdalbuildvrt mosaic.vrt downloads/ESA_*.tif 
```

Create tif samples:

```bash
python3 sample.py
```

Filter samples and polygonize them with:

```bash
python3 filter.py
```

To GPKG:

```bash
python3 to_gpkg.py
```

Run planetiler