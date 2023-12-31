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

to_gpkg(6)
to_gpkg(7)
to_gpkg(8)
