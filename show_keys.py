import matplotlib.pyplot as plt
import numpy as np

MAX_INT = 2147483647
n_bins = 800	

def get_bin(x):
	return int(np.floor(x*n_bins/MAX_INT))

sybil = []
with open("stats/sybil.txt") as file:
	line = file.readline().strip()
	sybil = [int(v) for v in line.split(" ")]
sybil = [v==1 for v in sybil]
not_sybil = [not v for v in sybil]

layer_distribution = []
with open("stats/layer_distribution.txt") as file:
	for line in file:
		layer_distribution.append([int(_) for _ in line.strip().split(" ")])
layer_distribution = np.array(layer_distribution)

n_nodes = len(sybil)


bins = np.linspace(0,MAX_INT,n_bins)
x = list(range(n_nodes))
show=[layer_distribution[0][not_sybil],layer_distribution[0][sybil]]
plt.yscale("log")
plt.hist(show,n_bins,stacked=True)
plt.show()

'''
bins = [0]* n_bins
for v in layer_distribution[0]:
	bins[get_bin(v)] += 1
'''
'''
x = list(range(n_bins))
plt.hist(layer_distribution[0],n_bins)
plt.show()
'''