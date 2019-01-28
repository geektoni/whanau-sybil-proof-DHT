import os 
import re
import sys
import numpy as np
import matplotlib.pyplot as plt

def parse_line(line):
	return int(line.strip() )


if len(sys.argv) != 2:
	stats_dir = "stats/"
else:
	stats_dir = sys.argv[1]

print("Using dir: ",stats_dir)
os.chdir(stats_dir)

msg_stats = {}

pattern="lookup_network_(?P<network_size>[0-9]+)_l_(?P<layers>[0-9]+)_n_(?P<exec_cycles>[0-9]+)"\
		"_f_(?P<table_size>[0-9]+)_s_(?P<successors>[0-9]+)_attack_edges_perc_(?P<attack_edges>[0-9]+).txt"

for filename in os.listdir("."):
	match = re.search(pattern,filename)
	if not match == None:
		k = (match.group("network_size"),match.group("table_size"),match.group("attack_edges"))
		print(k)
		data = []
		with open(filename,"r") as file:
			for line in file:
				data.append(parse_line(line))

		msg_stats[k] = data


