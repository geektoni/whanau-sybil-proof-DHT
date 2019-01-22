package it.unitn.disi.ds2.whanau.controls;

import it.unitn.disi.ds2.whanau.protocols.WhanauProtocol;
import it.unitn.disi.ds2.whanau.utils.LoggerSingleton;
import it.unitn.disi.ds2.whanau.utils.Pair;
import it.unitn.disi.ds2.whanau.utils.RandomSingleton;
import peersim.config.Configuration;
import peersim.core.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Control class which implements the logic of the setup()
 * method presented in the Whanau's paper. This class enables
 * us to build the internal tables of each nodes.
 */
public class WhanauSetup implements Control {

    /**
     * Constructor
     * @param prefix
     */
    public WhanauSetup(String prefix) {
        int networkSize = Network.size();
        this.pid = Configuration.getPid(prefix + "." + prot);
        this.lid = Configuration.getPid(prefix + "." + prot_link);
        this.l = Configuration.getInt(prefix + "." + layers, 3);
        this.w = Configuration.getInt(prefix + "." + mixing_time, (int)(Math.log(networkSize)/Math.log(2)));
        this.d = Configuration.getInt(prefix + "." + database_size, 1);
        this.f = Configuration.getInt(prefix + "." + max_fingers, (int)(Math.sqrt(this.d*networkSize)));
        this.s = Configuration.getInt(prefix + "." + max_successors, (int)(Math.sqrt(this.d*networkSize)));
        this.ratioAttackEdges = Configuration.getDouble(prefix + "." + ratio_attack_edges,-1.0);
        this.cluster_attack = Configuration.getBoolean(prefix + "." + do_cluster_attack,false);
        this.t_node = Configuration.getInt(prefix+"."+target_node, 10);
        this.rng = RandomSingleton.getInstance(Configuration.getInt("random.seed", 1));

        this.logger = LoggerSingleton.getInstance(this.getClass().getSimpleName(),
                Configuration.getBoolean("enable_logging",false));

        this.logger.log("Table sizes: successors "+this.s+", fingers "+this.f+", layers "+this.l);

    }

    /**
     * Execute the setup action for all the nodes.
     * @return true if it succeded.
     */
    public boolean execute() {

        // For each node, store inside them a <key,value>.
        // The key is increasing, while the value it is just
        // a random integer.
        logger.log("Initializing the stored records of the nodes.");
        for (int i = 0; i < Network.size(); i++) {
            Pair<Integer, String> value = new Pair<Integer, String>(rng.nextInt(Integer.MAX_VALUE), getRandomIpAddress());
            WhanauProtocol node = (WhanauProtocol) Network.get(i).getProtocol(this.pid);
            node.addToStoredRecords(value);
        }

        this.target_key = ((WhanauProtocol) Network.get(this.t_node).getProtocol(this.pid)).getStored_records().firstKey();

        int totalEdges = 0;

        // Initialize the internal tables of each node
        // (ids, fingers, successors, db).
        logger.log("Initializing the internal tables (just create them).");
        for (int i = 0; i < Network.size(); i++) {
            WhanauProtocol node = (WhanauProtocol) Network.get(i).getProtocol(this.pid);
            node.setUpInternalTables(this.f, this.s, this.d, this.l, this.w);
            totalEdges += ((IdleProtocol)(Network.get(i).getProtocol(this.lid))).degree();
        }

        // Set sybil nodes
        int attackEdgesToSet=(int)(totalEdges * this.ratioAttackEdges);
        if (this.ratioAttackEdges == -1.0)
        {
            attackEdgesToSet = totalEdges / this.w;
        }
        logger.log("Setting the Sybil nodes. Percentage of attack edges: "+(double)attackEdgesToSet*100.0/totalEdges+"%");
        int counter = 0,index = 0;
        int networkSize = Network.size();
        ArrayList<Integer> ids = new ArrayList<>(networkSize);
        for (int i = 0; i < networkSize; i++) {
            ids.add(i);
        }
        ids.remove(this.t_node);
        Collections.shuffle(ids, new Random(Configuration.getInt("random.seed", 1)));
        Node currentNode;
        WhanauProtocol whanauNode;
        while(counter<attackEdgesToSet)
        {
            currentNode = Network.get(ids.get(index));
            whanauNode = (WhanauProtocol)currentNode.getProtocol(pid);
            whanauNode.setSybil(true);
            counter += ((IdleProtocol)(currentNode.getProtocol(this.lid))).degree();
            index++;
        }
        this.total_sybil_nodes = index;

        // Set up the db table
        logger.log("Set up the db table");
        for (int i=0; i< Network.size(); i++)
        {
            this.sampleRecords(Network.get(i));
        }

        // For each of the levels set up the other tables
        for (int i=0; i<l; i++)
        {
            logger.log("Set up the other tables for layer "+(i+1)+"/"+this.l);
            logger.log("Layer "+(i+1)+"/"+this.l+": Setting up IDs.");
            // Set up the ids for each layer
            for (int j=0; j< Network.size(); j++)
            {
                chooseId(Network.get(j), i);
            }

            logger.log("Layer "+(i+1)+"/"+this.l+": Setting up fingers.");
            // Set up the fingers for each layer
            for (int j=0; j< Network.size(); j++)
            {
                fingers(Network.get(j), i);
            }

            logger.log("Layer "+(i+1)+"/"+this.l+": Setting up successors.");
            // Set up the successors for each layer
            for (int j=0; j< Network.size(); j++)
            {
                successors(Network.get(j), i);
            }
        }

        logger.log("Sort fingers and successors tables");
        // For each node, sort their fingers and succ tables
        for (int j=0; j< Network.size(); j++)
        {
            WhanauProtocol node = (WhanauProtocol) Network.get(j).getProtocol(this.pid);
            for (ArrayList<Pair<Integer, Node>> list : node.getFingers())
            {
                list.sort(new Pair.FingersComparator());
            }
            for (ArrayList<Pair<Integer, String>> list : node.getSucc())
            {
                list.sort(new Pair.SuccComparator());
            }
        }

        return true;
    }

    /**
     * Set up the successors <key, value> pairs given a starting
     * node and the target layer.
     * @param node starting node
     * @param layer the layer we are considering
     */
    protected void successors(Node node, int layer)
    {
        WhanauProtocol source = (WhanauProtocol) node.getProtocol(this.pid);
        ArrayList<Pair<Integer, String>> all_elements = new ArrayList<>();
        int current_key = source.getIdOfLayer(layer);
        for (int i = 0; i < this.s; i++) {
            Node n = this.randomWalk(node, w);
            WhanauProtocol casted_node = (WhanauProtocol) n.getProtocol(this.pid);

            // Add the found values to the successor table
            for (Pair<Integer, String> p :  casted_node.successorsSample(current_key))
            {
                if (!all_elements.contains(p))
                    all_elements.add(p);
            }
        }
        source.addToSuccessors(all_elements, layer);
    }

    /**
     * Set up one layer the finger table for each node.
     * @param node the given node.
     * @param layer the target layer.
     */
    protected void fingers(Node node, int layer) {
        WhanauProtocol source = (WhanauProtocol) node.getProtocol(this.pid);
        ArrayList<Pair<Integer, Node>> fings = new ArrayList<>();
        for (int i = 0; i < f; i++) {
            Node n = randomWalk(node, w);

            WhanauProtocol node_casted = (WhanauProtocol) n.getProtocol(this.pid);
            Integer ids = node_casted.getIdOfLayer(layer);

            Pair<Integer, Node> pair = new Pair<>(ids, n);
            fings.add(pair);
        }
        source.setFingerForLayer(fings, layer);
    }

    /**
     * Choose an ID for the given node at the specified layer.
     * @param node the given node
     * @param layer the given layer
     */
    protected void chooseId(Node node, int layer)
    {
        WhanauProtocol n = (WhanauProtocol) node.getProtocol(this.pid);
        Integer id = -1;
        if (layer == 0)
        {
            if (n.isSybil() && cluster_attack)
            {
                // Clever way to choose a Sybil finger (they will precede the key)
                id = (this.target_key - rng.nextInt(this.total_sybil_nodes)-1);
                if (id < 0)
                {
                    id = Integer.MAX_VALUE+id;
                }
            } else {
                id= n.randomRecord().first;
            }
        } else {
            id= n.randomRecordFingers(layer-1).first;
        }
        n.setIdForLayer(id, layer);
    }

    /**
     * Sample records with a random walk starting from the given node.
     * @param node
     */
    protected void sampleRecords(Node node) {
        for (int i =0; i<d; i++)
        {
            Node random_node = this.randomWalk(node, this.l);
            Pair<Integer, String> random_record = ((WhanauProtocol) random_node.getProtocol(this.pid)).randomRecord();
            ((WhanauProtocol) node.getProtocol(this.pid)).addToDb(random_record);
        }
    }

    /**
     * Starting from the source Node, perform a random walk of specified length on
     * the network and return the final node.
     * @param source Starting node of the walk.
     * @param length Length of the random walk.
     * @return Final node.
     */
    protected Node randomWalk(Node source, int length)
    {
        Node target = source;
        IdleProtocol l_source = (IdleProtocol) source.getProtocol(this.lid);
        for (int i = 0; i < length; i++) {

            // If we reach a node that is sybil, we return it immediately, since
            // we are assuming that if you reach a sybil region, then you won't "come"
            // out from it.
            WhanauProtocol checkIfSybil = (WhanauProtocol) target.getProtocol(this.pid);
            if (checkIfSybil.isSybil())
                return target;

            if (l_source.degree() == 0) return target;
            int random_id = ((rng.nextInt(Integer.MAX_VALUE))%l_source.degree());
            target = l_source.getNeighbor(random_id);
            IdleProtocol tmp = (IdleProtocol) target.getProtocol(this.lid);
            l_source = tmp;
        }
        return target;
    }

    /**
     * Get a randomly generated IP address
     * @return
     */
    protected String getRandomIpAddress()
    {
        return rng.nextInt(256) + "." + rng.nextInt(256) + "."
                + rng.nextInt(256) + "." + rng.nextInt(256);
    }

    /* Possible parameters */
    static private String prot = "protocol";
    static private String prot_link = "protocol_link";
    static private String layers = "layers";
    static private String database_size = "database_size";
    static private String max_successors = "max_successors";
    static private String max_fingers = "max_fingers";
    static private String mixing_time = "mixing_time";
    static private String ratio_attack_edges = "ratio_attack_edges";
    static private String do_cluster_attack = "cluster_attack";
    static private String target_node = "target_node";

    protected int pid;
    protected int lid;

    protected RandomSingleton rng;

    protected int f;
    protected int s;
    protected int d;
    protected int l;
    protected int w;
    protected double ratioAttackEdges;

    protected int t_node;
    protected int target_key;
    protected int total_sybil_nodes;
    protected boolean cluster_attack;

    protected LoggerSingleton logger;
}
