# global-forest-polygons

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

Merge polygons of the same `k` value with:

```bash
python3 merge.py
```

Run planetiler