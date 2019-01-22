import matplotlib.pyplot as plt
import numpy as np

# returns a percentage; utility routine
def perc(x,tot):
	return x*100/tot

MAX_INT = 2147483647
n_bins = 400

# parse the file that tells wether node i is sybil or not: 1 -> sybil, 0 -> not sybil
sybil = []
with open("stats/sybil.txt") as file:
	line = file.readline().strip()
	sybil = [int(v) for v in line.split(" ")]
sybil = [v==1 for v in sybil]
not_sybil = [not v for v in sybil]

# parse the file with the layer ids of the node
# each line contains the info for a specific layer (0,1,...)
# for each layer, values are separated by 'blank'
layer_distribution = []
with open("stats/layer_distribution.txt") as file:
	for line in file:
		layer_distribution.append([int(_) for _ in line.strip().split(" ")])
layer_distribution = np.array(layer_distribution)

n_nodes = len(sybil)
n_layers = len(layer_distribution)
n_sybil = np.sum(sybil)
n_nonsybil = np.sum(not_sybil)

#generates one subplot per layer
colors = ["blue","red"]
figs,subplots = plt.subplots(nrows=n_layers,ncols=1)
subplots = subplots.flatten()


bins = np.linspace(0,MAX_INT,n_bins) # possible values: this contains the edge-values for each bin
for i,p in enumerate(subplots):
	show=[layer_distribution[i][not_sybil],layer_distribution[i][sybil]]	# pair of arrays that contain respectively the non-sybil node ids and the sybil ones
	p.hist(show,n_bins,color=colors,stacked=True,label=["honest","sybil"])
	p.set_title("Layer "+str(i))
	p.set_yscale("log")		# in order to have a nicer visualization on the clustering attack, axis y is in logscale

subplots[0].legend(loc = "upper right")		# add one legend (that's valid for all the possible plots)
plt.subplots_adjust(hspace=0.5)

text_info = "Total number of nodes: {}  honest %: {:.2f}  sybil %:{:.2f}".format(n_nodes,perc(n_nonsybil,n_nodes),perc(n_sybil,n_nodes))
mng = plt.get_current_fig_manager()
plt.gcf().text(0.5,0.03,text_info,horizontalalignment='center',verticalalignment='bottom',fontsize=15)
mng.window.showMaximized()
plt.show()
