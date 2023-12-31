import subprocess

def merge(k):
    input_files = []
    for i in range(2 ** k):
        for j in range(2 ** k):
            input_files.append(f'polygons/{k}/{i}/{j}.geojson')

    command = f'mkdir -p merged'
    print(command)
    subprocess.run(command, shell=True)

    merged_filename = f'merged/{k}.gpkg'
    command = f'rm {merged_filename}'
    print(command)
    subprocess.run(command, shell=True)

    command = f'ogrmerge.py -o {merged_filename} {" ".join(input_files)} -single -nln forest'
    print(command)
    subprocess.run(command, shell=True)

    command = f'zip -r merged/{k}.zip merged/{k}.gpkg'
    print(command)
    subprocess.run(command, shell=True)

for k in [6, 7, 8]:
    merge(k)
