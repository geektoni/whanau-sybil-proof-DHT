package it.unitn.disi.ds2.whanau.controls;

import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.util.SupplierUtil;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.dynamics.WireGraph;
import peersim.graph.Graph;

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
        graphGenerator = new GnpRandomGraphGenerator<Integer, DefaultEdge>(Network.size(), p, 42);
    }

    /**
     * Build the general topology of the graph.
     * @param graph An instance of {@link peersim.graph.Graph}.
     */
    public void wire(Graph graph) {

        // TODO: It must be possible to do it from social network file.
        org.jgrapht.Graph<Integer, DefaultEdge> g = generateTopology();

        // Loop over all nodes and add the links between them.
        // This way we will build the Peersim topology.
        // Moreover, we will build their neighbours array.
        Set<DefaultEdge> edgs = g.edgeSet();
        for (DefaultEdge e : edgs)
        {
            graph.setEdge(g.getEdgeSource(e), g.getEdgeTarget(e));
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

    /* ID of the linkable protocol which will be present in each node */
    private int pid;

    /** Probability of adding an edge between two nodes */
    private double p;

    /** Graph generator */
    private GnpRandomGraphGenerator<Integer, DefaultEdge> graphGenerator;

    /* Configuration parameter identifier for the linkable protocol*/
    static private String prot = "protocol";

}
