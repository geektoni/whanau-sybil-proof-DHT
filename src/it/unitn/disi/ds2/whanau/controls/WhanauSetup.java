package it.unitn.disi.ds2.whanau.controls;

import it.unitn.disi.ds2.whanau.protocols.WhanauProtocol;
import it.unitn.disi.ds2.whanau.utils.Pair;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.IdleProtocol;
import peersim.core.Network;
import peersim.core.Node;

import java.util.Random;

public class WhanauSetup implements Control {

    public WhanauSetup(String prefix) {
        this.pid = Configuration.getPid(prefix + "." + prot);
        this.lid = Configuration.getPid(prefix + "." + prot_link);
        this.l = Configuration.getInt(prefix + "." + layers, 1);
        this.w = Configuration.getInt(prefix + "." + mixing_time, 1);
        this.d = Configuration.getInt(prefix + "." + database_size, 1);
        this.f = Configuration.getInt(prefix + "." + max_fingers, 1);
        this.s = Configuration.getInt(prefix + "." + max_successors, 1);

        this.rng = new Random();

    }

    public boolean execute() {

        // For each node, store inside them a value.
        // The key is increasing, while the value it is just
        // a random integer.
        int key=0;
        for (int i = 0; i < Network.size(); i++) {
            Pair<Integer, String> value = new Pair<Integer, String>(key, String.valueOf(rng.nextInt()));
            WhanauProtocol node = (WhanauProtocol) Network.get(i).getProtocol(this.pid);
            node.addToStoredRecords(value);
        }

        // Set up the db table
        for (int i=0; i< Network.size(); i++)
        {
            this.sampleRecords(Network.get(i));
        }

        // For each of the levels set up the other tables
        for (int i=0; i<l; i++)
        {
            for (int j=0; j< Network.size(); j++)
            {
                //WhanauProtocol node = (WhanauProtocol) Network.get(i);

            }
        }

        return true;
    }

    /**
     * Sample records with a random walk starting from the given node.
     * @param node
     */
    private void sampleRecords(Node node) {
        for (int i =0; i<d; i++)
        {
            Node random_node = this.randomWalk(node, this.l);
            Pair<Integer, String> random_record = ((WhanauProtocol) random_node.getProtocol(this.pid)).randomRecord();
            ((WhanauProtocol) node.getProtocol(this.pid)).addToStoredRecords(random_record);
        }
    }

    /**
     * Starting from the source Node, perform a random walk of length l on
     * the network.
     * @param source Starting node of the walk.
     * @param length Length of the random walk.
     * @return Target node.
     */
    private Node randomWalk(Node source, int length)
    {
        Node target = source;
        IdleProtocol l_source = (IdleProtocol) source.getProtocol(this.lid);
        for (int i = 0; i < length; i++) {
            if (l_source.degree() == 0) return target;
            int random_id = ((rng.nextInt(Integer.MAX_VALUE))%l_source.degree());
            target = l_source.getNeighbor(random_id);
            IdleProtocol tmp = (IdleProtocol) target.getProtocol(this.lid);
            l_source = tmp;
        }
        return target;
    }



    /* Possible parameters */
    static private String prot = "protocol";
    static private String prot_link = "protocol_link";
    static private String layers = "layers";
    static private String database_size = "database_size";
    static private String max_successors = "max_successors";
    static private String max_fingers = "max_fingers";
    static private String mixing_time = "mixing_time";

    private int pid;
    private int lid;

    private Random rng;

    private int f;
    private int s;
    private int d;
    private int l;
    private int w;
}
