# WhanauProtocol: A Sybil-proof Distribued Hash Table implemented in Peersim

## Brief Overview

Whanau  is  a  novel  routing  protocol  for  distributed hash  tables  (DHTs)  that  is  efficient  and  robust  against  
Sybil attacks. We replicated the original paper providing one of the first Java-based implementations of the protocol. We
performed several experiments in  order  to  assess  the  real  efficiency  of  the protocol on large-scale systems by
using the Peersim framework. We tested its resilience under the presence of clustering attacks.

## Installation

The project requires **Java 1.8** and **Maven** and it was tested on Ubuntu 16.04 and 18.04,
but it should work also on other Unix based machines (MacOS or other Linux distribution).
No testing on Windows was performed.

If you encounter any problems feel free to open an issue on Github.

In order to build the project you need to follow these steps:

```bash
git clone https://github.com/geektoni/whanau-sybil-proof-DHT
mvn initialize
mvn compile
```

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
mvn exec:java -Dexec.mainClass="peersim.Simulator" -Dexec.args="configurations/whanau_exp_1.cfg"
```

## Contributors

Giovanni De Toni:
Andrea Zampieri:
