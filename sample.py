import subprocess

def divide_range(start, end, n):
    step = (end - start) / n
    ranges = [(start + i * step, start + (i + 1) * step) for i in range(n)]
    return ranges

max_k = 8

min_lon = -180
max_lon = 180
min_lat = -85
max_lat = 85

k_0_resolution = 0.1

for k in range(6, max_k + 1): # range(max_k + 1):
    n_parts = 2 ** k
    longitude_ranges = divide_range(min_lon, max_lon, n_parts)
    latitude_ranges = divide_range(min_lat, max_lat, n_parts)
    margin_lon = 0.005 * (max_lon - min_lon) / n_parts
    margin_lat = 0.005 * (max_lat - min_lat) / n_parts
    print(f'k={k}, margin_lon={margin_lon}, margin_lat={margin_lat}')
    for i in range(n_parts):
        command = f'mkdir -p samples/{k}/{i}'
        print(command)
        subprocess.run(command, shell=True)
        for j in range(n_parts):
            (start_lon, end_lon) = longitude_ranges[i]
            (start_lat, end_lat) = latitude_ranges[j]
            # print(f'i={i}, j={j}, k={k}')
            # print(f"Lon [{start_lon}, {end_lon}]")
            # print(f"Lat [{start_lat}, {end_lat}]")
            ulx = start_lon - margin_lon
            uly = end_lat + margin_lat
            lrx = end_lon + margin_lon
            lry = start_lat - margin_lat
            command = f'gdal_translate -tr {k_0_resolution / 2**k} {k_0_resolution / 2**k} -projwin {ulx} {uly} {lrx} {lry} mosaic.vrt samples/{k}/{i}/{j}.tif -co COMPRESS=LZW'
            print(command)
            subprocess.run(command, shell=True)
