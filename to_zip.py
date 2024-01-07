import subprocess

def to_zip(k, value):

    command = f'mkdir -p zips'
    print(command)
    subprocess.run(command, shell=True)

    command = f'zip -r zips/{value}-{k}.zip polygons/{value}/{k}/'
    print(command)
    subprocess.run(command, shell=True)

k_min = 0
k_max = 7

for value in [10, 20, 30, 40, 50, 60, 70, 80, 90, 95, 100, 110]:
    for k in range(k_min, k_max + 1):
        to_zip(k, value)
