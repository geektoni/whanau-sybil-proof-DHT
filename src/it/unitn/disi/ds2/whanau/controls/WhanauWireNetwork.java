package it.unitn.disi.ds2.whanau.controls;

import org.jgrapht.generate.WattsStrogatzGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.util.SupplierUtil;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.dynamics.WireGraph;
import peersim.graph.Graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Supplier;

/**
 * This class implements the methods used to create the network
 * topology. It can easily generate a random topology of given
 * size or it can build a network given a pre-defined topology
 * (like the graph of a social network.)
 */
public class WhanauWireNetwork extends WireGraph {

    /**
     * Constructor.
     * @param prefix the prefix used inside the configuration file.
     */
    public WhanauWireNetwork(String prefix)
    {
        super(prefix);
        this.pid = Configuration.getPid(prefix + "." + prot);
        p = Configuration.getDouble(prefix + ".probability",0.1);
        this.networkFilename = Configuration.getString(prefix+"."+socialNetworkFilename,"");

        graphGenerator = new WattsStrogatzGraphGenerator<Integer, DefaultEdge>(Network.size(), 30, p);
    }

    /**
     * Build the general topology of the graph.
     * @param graph An instance of {@link peersim.graph.Graph}.
     */
    public void wire(Graph graph) {
        org.jgrapht.Graph<Integer, DefaultEdge> g;
        if(this.networkFilename.equals(""))
        {
            g = generateTopology();
        }
        else
        {
            g = readTopologyFromFile();
        }


        // Loop over all nodes and add the links between them.
        // This way we will build the Peersim topology.
        // Moreover, we will build their neighbours array.
        Set<DefaultEdge> edgs = g.edgeSet();
        for (DefaultEdge e : edgs)
        {
            graph.setEdge(g.getEdgeSource(e), g.getEdgeTarget(e));
            graph.setEdge(g.getEdgeTarget(e), g.getEdgeSource(e));
        }
    }

    /**
     * Generate a Erdős–Rényi graph topology from the generator.
     * @return a generate graph
     */
    private org.jgrapht.Graph<Integer, DefaultEdge> generateTopology() {

        // Create the VertexFactory so the generator can create vertices
        Supplier<Integer> vSupplier = new Supplier<Integer>()
        {
            private int id = 0;

            @Override
            public Integer get()
            {
                return id++;
            }
        };

        org.jgrapht.Graph<Integer, DefaultEdge> g =
                new DefaultUndirectedGraph<>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);
        graphGenerator.generateGraph(g, null);
        return g;
    }

    /**
     * Generates the graph starting from the specified file
     * This allows to build the network in order to replicate real social networks
     * @return the loaded graph
     */
    private org.jgrapht.Graph<Integer, DefaultEdge> readTopologyFromFile()
    {
        org.jgrapht.Graph<Integer, DefaultEdge> g= new DefaultUndirectedGraph<Integer,DefaultEdge>(DefaultEdge.class);
        try {
            FileReader fr = new FileReader("social-graphs/" + this.networkFilename);
            Scanner scanner = new Scanner(new BufferedReader(fr));
            long nodeNumber = scanner.nextLong();
            long edgeNumber = scanner.nextInt();
            HashSet<Integer> nodes = new HashSet<>();
            for (int i = 0; i < edgeNumber; i++) {
                int src,dst;
                src = scanner.nextInt();
                dst = scanner.nextInt();
                if(!nodes.contains(src))
                {
                    g.addVertex(src);
                    nodes.add(src);
                }
                if(!nodes.contains(dst))
                {
                    g.addVertex(dst);
                    nodes.add(dst);
                }
                g.addEdge(src,dst);
            }
            fr.close();
        } catch (Exception e ) {
            e.printStackTrace();
            System.exit(-1);
        }

        return g;
    }


    /* ID of the linkable protocol which will be present in each node */
    private int pid;

    /** Probability of adding an edge between two nodes */
    private double p;

    /* File for building the network topology */
    private String networkFilename;

    /** Graph generator */
    private WattsStrogatzGraphGenerator<Integer, DefaultEdge> graphGenerator;

    /* Configuration parameter identifier for the linkable protocol*/
    static private String prot = "protocol";
    static private String socialNetworkFilename = "social-network";

}
