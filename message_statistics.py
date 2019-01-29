import os 
import re
import sys
import numpy as np
import matplotlib.pyplot as plt

def parse_line(line):
	t = line.strip().split(",")
	success = t[0]=="true"
	msgs = int(t[1])
	return (success,msgs)

if len(sys.argv) != 2:
	stats_dir = "stats/varying_attack_perc/"
	print("To use a different directory for the stats file use: python message_statistics.py <path>")
else:
	stats_dir = sys.argv[1]

print("Using dir: ",stats_dir)
os.chdir(stats_dir)

msg_stats = {}
msg_outcomes = {}
pattern="lookup_network_(?P<network_size>[0-9]+)_l_(?P<layers>[0-9]+)_n_(?P<exec_cycles>[0-9]+)"\
		"_f_(?P<table_size>[0-9]+)_s_(?P<successors>[0-9]+)_attack_edges_perc_(?P<attack_edges>[0-9]+).txt"

for filename in os.listdir("."):
	match = re.search(pattern,filename)
	if not match == None:
		k = (int(match.group("network_size")),int(match.group("table_size")),int(match.group("attack_edges")),int(match.group("layers")))
		if k in msg_stats:
			print("Conflicting files on\n\tnetwork_size {} table_size {} attack_edges {} layers {}".format(k[0],k[1],k[2],k[3]))
			print("Delete unwanted files")
			exit()
		data = []
		outcome = []
		with open(filename,"r") as file:
			file.readline() #skip header
			for line in file:
				success,value = parse_line(line)
				data.append(value)
				outcome.append(success)

		msg_stats[k] = data
		msg_outcomes[k] = outcome

# 1st experiment
# x table size, y mean value of messages, 1 line for each attack edge perc
layers = 3
table_sizes = [10,50,100,500,1000,2000]
attack_edges_percs = [1,10,15]
network_sizes = [10**4,10**5]

n_graphics = len(network_sizes)

net_size = 10000
for ae in attack_edges_percs:
	line = []
	for t in table_sizes:
		k = (net_size,t,ae,layers)
		data = msg_stats[k]
		line.append(np.median(data))
	plt.plot(table_sizes,line,marker="o",label=str(ae)+"%")

plt.legend()
plt.show()

# 2nd experiment
# x attack_edges perc, y mean number of messages, 1 line per network size 
def plot_messages_wrt_aep(attack_edges_percs,network_size,table_size):
	line = []
	for ae in attack_edges_percs:
		k = (network_size,table_size,ae,layers)
		data = msg_stats[k]
		line.append(np.mean(data))
	plt.plot(attack_edges_percs,line,marker="o",label=str(network_size))


attack_edges_percs = [0,1,10,20,30,50]
network_size = 10 ** 4 
table_size = 100
plot_messages_wrt_aep(attack_edges_percs,network_size,table_size)

network_size = 10 ** 5
table_size = 316
plot_messages_wrt_aep(attack_edges_percs,network_size,table_size)
plt.legend()
plt.show()

# 3rd experiment
# x size of the net, y mean number of messages
net_table_sizes = [(100,10),(1000,32),(10000,100),(100000,316)]
net_sizes = [100,1000,10000,100000]
attack_edges = 0

line = []
for net_size,table_size in net_table_sizes:
	k = (net_size,table_size,attack_edges,layers)
	data = msg_stats[k]
	line.append(np.mean(data))
	
plt.xscale("log")
plt.plot(net_sizes,line,marker="o")
plt.show()

# 4th experiment
# x attack_edges perc, y mean messages, one line per level of layers
def plot_failures_wrt_layers(network_size,table_size,attack_edges_percs,layers):
	for l in layers:
		line = []
		for ae in attack_edges_percs:
			k = (network_size,table_size,ae,l)
			outcomes = msg_outcomes[k]
			value = float(np.sum(outcomes))/len(outcomes)
			line.append(value)
		plt.plot(attack_edges_percs,line,marker="o",label=str(l))
	plt.legend()
	plt.show()


network_size = 10000
table_size = 100
layers = [1,3,5,7]
attack_edges_percs = [0,10,20,30]

plot_failures_wrt_layers(network_size,table_size,attack_edges_percs,layers)