import sys

def parse_line(line):
	return [int(_) for _ in line.strip().split("\t")]

def add_parsed(s):
	index = s.find(".txt")
	return s[:index]+"-parsed"+s[index:]

if len(sys.argv) != 2:
	print("Wrong usage; use 'python parse_network_file.py <filename>' instead")

filename = sys.argv[1]

mapping = {}
index = 0

with open(filename,"r") as file:
	with open(add_parsed(filename),"w") as output:
		output.write(file.readline())		#skip n,m line
		for line in file:
			src,dst = parse_line(line)

			if not src in mapping:
				mapping[src] = index
				index += 1

			if not dst in mapping:
				mapping[dst] = index
				index += 1

			output.write(str(mapping[src])+" "+str(mapping[dst])+"\n")