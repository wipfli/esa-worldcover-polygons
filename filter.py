import cv2
import subprocess
import json
from osgeo import gdal




def get_ullr_from_tif(tif_path):
    # Open the GeoTIFF file
    dataset = gdal.Open(tif_path)

    if dataset is None:
        print(f"Error: Unable to open {tif_path}")
        return None

    # Get the geotransform information
    geotransform = dataset.GetGeoTransform()

    # Calculate the upper-left and lower-right coordinates
    x_min = geotransform[0]
    y_max = geotransform[3]
    x_max = x_min + (geotransform[1] * dataset.RasterXSize)
    y_min = y_max + (geotransform[5] * dataset.RasterYSize)

    # Close the dataset
    dataset = None

    return x_min, y_max, x_max, y_min


def run(sample_i, sample_j, sample_k):
    source_filename = f'samples/{sample_k}/{sample_i}/{sample_j}.tif'

    command = f'gdal_edit.py -unsetnodata {source_filename}'
    print(command)
    subprocess.run(command, shell=True)

    print('selecting value...')

    value = 10 # forest 10, water 80

    expressions = []
    expressions += [f"(A=={value})*255"]

    calc = ' + '.join(expressions)

    in_filename = source_filename
    out_filename = 'value.tif'
    command = f'gdal_calc.py -A {in_filename} --calc="{calc}" --outfile="{out_filename}" --co COMPRESS=LZW --overwrite'
    print(command)
    subprocess.run(command, shell=True)

    in_filename = out_filename
    print(f'reading {in_filename}...')
    image = cv2.imread(in_filename)

    ksize = 3
    print(f'filtering with ksize={ksize}...')
    filtered = cv2.medianBlur(image, ksize=ksize)

    out_filename = f'filtered.tif'
    print(f'writing {out_filename}...')
    cv2.imwrite(out_filename, filtered)

    sieve_threshold = 50
    in_filename = out_filename
    out_filename = 'sieved.tif'
    print(f'sieving...')
    command = f'gdal_sieve.py -st {sieve_threshold} {in_filename} {out_filename}'
    print(command)
    subprocess.run(command, shell=True)

    in_filename = 'sieved.tif'
    print(f'reading {in_filename}...')
    image = cv2.imread(in_filename)

    off_pixel_value = [0, 0, 0]

    image[0][0] = off_pixel_value
    image[0][1] = off_pixel_value
    image[1][0] = off_pixel_value

    image[0][-1] = off_pixel_value
    image[0][-2] = off_pixel_value
    image[1][-1] = off_pixel_value

    image[-1][0] = off_pixel_value
    image[-1][1] = off_pixel_value
    image[-2][0] = off_pixel_value

    image[-1][-1] = off_pixel_value
    image[-1][-2] = off_pixel_value
    image[-2][-1] = off_pixel_value

    out_filename = f'corners.tif'
    print(f'writing {out_filename}...')
    cv2.imwrite(out_filename, image)

    print('projecting...')

    in_filename = out_filename
    out_filename = 'projected.tif'
    ullr = get_ullr_from_tif(source_filename)
    command = f'gdal_translate {in_filename} {out_filename} -a_srs "EPSG:4326" -a_ullr {" ".join([str(x) for x in ullr])} -b 1'
    print(command)
    subprocess.run(command, shell=True)

    print('removing old file...')
    command = f'rm polygon.gpkg'
    print(command)
    subprocess.run(command, shell=True)

    print(f'polygonizing...')
    command = f'gdal_polygonize.py projected.tif -b 1 -f "GPKG" polygon.gpkg'
    subprocess.run(command, shell=True)

    print(f'select...')
    out_filename = f'polygon.geojson'
    command = f'ogr2ogr -f GeoJSON {out_filename} polygon.gpkg -where "\"DN\" == 255"'
    subprocess.run(command, shell=True)


    print('midpoint...')
    in_filename = out_filename
    with open(in_filename) as f:
        data = json.load(f)

    num_iterations = 3

    for _ in range(num_iterations):
        for feature in data['features']:
            for j in range(len(feature['geometry']['coordinates'])):
                ring = feature['geometry']['coordinates'][j]
                new_ring = []
                if len(ring) < 3:
                    print('error polygon ring length less than 3')
                    exit()
                for i in range(len(ring) - 1):
                    p0 = ring[i]
                    p1 = ring[i + 1]
                    new_ring.append([0.5 * (p0[0] + p1[0]), 0.5 * (p0[1] + p1[1])])
                new_ring.append(list(new_ring[0]))
                feature['geometry']['coordinates'][j] = new_ring

    command = f'mkdir -p polygons/{sample_k}/{sample_i}'
    print(command)
    subprocess.run(command, shell=True)

    out_filename = f'polygons/{sample_k}/{sample_i}/{sample_j}.geojson'

    with open(out_filename, 'w') as f:
        json.dump(data, f)

    # convert to gpkg here, then to_gpkg.py is not needed...

    command = 'rm corners.tif filtered.tif polygon.gpkg polygon.geojson projected.tif sieved.tif value.tif'
    print(command)
    subprocess.run(command, shell=True)


k_min = 6
k_max = 8

for k in range(k_min, k_max + 1):
    for i in range(2 ** k):
        for j in range(2 ** k):
            run(i, j, k)
