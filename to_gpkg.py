import subprocess

def to_gpkg(k):
    for i in range(2 ** k):
        command = f'mkdir -p gpkg/{k}/{i}'
        print(command)
        subprocess.run(command, shell=True)
        for j in range(2 ** k):
            command = f'ogr2ogr gpkg/{k}/{i}/{j}.gpkg polygons/{k}/{i}/{j}.geojson'
            print(command)
            subprocess.run(command, shell=True)

for k in range(5 + 1):
    to_gpkg(k)
