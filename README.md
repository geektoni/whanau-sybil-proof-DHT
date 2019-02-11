# WhanauProtocol: A Sybil-proof Distribued Hash Table implemented in Peersim

## Brief Overview

Whanau  is  a  novel  routing  protocol  for  distributed hash  tables  (DHTs)  that  is  efficient  and  robust  against  
Sybil attacks. We replicated the original paper [1] providing one of the first open-source Java-based implementations of
the protocol. We performed several experiments in  order  to  assess  the  real  efficiency  of  the protocol on
large-scale systems by using the Peersim framework. We tested its resilience under the presence of clustering attacks.

A report which summarize Whanau's procedure and the results can be found [here](https://github.com/geektoni/whanau-sybil-proof-DHT/blob/master/report/report.pdf).

[1] Lesniewski-Laas, Christopher, and M. Frans Kaashoek. "Whanau: A sybil-proof distributed hash table." (2010).

## Installation

The project requires **Java 1.8** and **Maven** and it was tested on Ubuntu 16.04 and 18.04,
but it should work also on other Unix based machines (MacOS or other Linux distribution).
No testing on Windows was performed.

In order to build the project you need to follow these steps:

```bash
git clone https://github.com/geektoni/whanau-sybil-proof-DHT
mvn initialize
mvn compile
```
If you encounter any problems feel free to open an [issue](https://github.com/geektoni/whanau-sybil-proof-DHT/issues) on Github.


## Usage

The protocol can be tested by using the following commands:
```bash
cd whanau-sybil-proof-DHT
mvn exec:java -Dexec.mainClass="peersim.Simulator" -Dexec.args="whanau.cfg"
```
It will run the Peersim's simulation using the `whanau.cfg` file to set up
its internal configurations. The directory `configurations` contains several
configuration files which can be used to test the protocol on different network
conditions (varying the percentage of attack edges, the number of layers, the
table sizes, etc.).

In order to run whanau with custom configuration, you need to run the following:
```bash
mvn exec:java -Dexec.mainClass="peersim.Simulator" -Dexec.args="configurations/<filename>.cfg"
```
### Preset configurations

There are 5 configuration files ready to run:
 1. `10e4_nodes_1_layer_20perc_attack_edges.cfg`
 2. `10e4_nodes_3_layer_20perc_attack_edges.cfg`
 3. `10e4_nodes_10e3_fingers_and_succs_25perc_attack_edges_3_layers.cfg`
 4. `10e4_nodes_no_attack.cfg`
 5. `dblp-dataset_15perc_attack_edges_5_layers.cfg`

Running **(1)** and **(2)** highlights the difference that layers make in Whanau: percentage of success should pass from \~73% to \~96%. These networks have both: 10000 nodes, 100 fingers and successors per layer.

**(3)** shows that in the presence of a strong attack, having much more fingers and successors per layer (1000 instead of the standard 100) doesn't make much difference when dealing with Sybil entities.

**(4)** is just a vanilla network that embodies the ideal situation of no attacks

**(5)** loads a real social network (DBLP) instead of a surrogate one and tests the performances of the protocol with 5 layers and 15% of attack edges

**NOTES:** 
If the network should be loaded from a file make sure that:
 - the `network.size` parameter matches the number of nodes in the network file
 - the `init.buildnetwork.social_network path/to/file.txt` param is set (path relative to where the `mvn exec` command has been invoked)
 - the network has the following structure:
 	- first line : <number_of_nodes> \<blank> <number_of_edges>
 	- following lines : <id_node_1> \<blank> <id_node_2>
 - the network will be interpreted always as an *undirected graph*
 - the **id**s should be in the range [0,n-1] where n is the number of nodes

 There is an utility script `social-graphs/parse-network-file.py` that takes as input (as argument, i.e. `python social-graphs/parse-network-file.py path/to/file.txt`) a file in the same format as described in the notes above, and transformes it in a valid input file (producing the `*-parsed.txt`) remapping the id of the nodes in the contiguous space that is requested. In other words, an export of the edges of a network with **arbitrary** integer ids can be easily adapted to the wanted format with this script

## Contributors

Giovanni De Toni: [giovanni.detoni@studenti.unitn.it](mailto:giovanni.detoni@studenti.unitn.it)

Andrea Zampieri: [andrea.zampieri@studenti.unitn.it](mailto:andrea.zampieri@studenti.unitn.it)
