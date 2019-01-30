package it.unitn.disi.ds2.whanau.controls;

import it.unitn.disi.ds2.whanau.protocols.WhanauProtocol;
import it.unitn.disi.ds2.whanau.utils.LoggerSingleton;
import it.unitn.disi.ds2.whanau.utils.Pair;
import it.unitn.disi.ds2.whanau.utils.LookupResult;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;

import java.util.ArrayList;
import java.util.Collections;

/***
 * Class used to implement the lookup() procedure of Whanau. It is also
 * used to perform the various experiments.
 */
public class WhanauLookup extends WhanauSetup {

    public WhanauLookup(String prefix) {
        super(prefix);
        execution_cycles = Configuration.getInt(prefix + "." + exec_cycles,1);

        this.logger = LoggerSingleton.getInstance(this.getClass().getSimpleName(),
                Configuration.getBoolean("enable_logging",false));
    }

    /**
     * Execute the lookup action from a given random node
     * which is not sybil. The action is done several time to collect
     * an estimate about how many messages we send in order to get the
     * key associated value.
     * @return this method returns always false.
     */
    public boolean execute()
    {
        ArrayList<LookupResult> collectedResults = new ArrayList<>();
        for (int i = 0; i < execution_cycles; i++) {

            // Get a random node not sybil
            Node source = this.getRandomNodeNotSybil(this.t_node);

            WhanauProtocol source_casted = (WhanauProtocol) source.getProtocol(this.pid);

            // Get the target node and get its key
            Node target = Network.get(this.t_node);
            WhanauProtocol target_casted = (WhanauProtocol) target.getProtocol(this.pid);
            key = target_casted.getIdOfLayer(0);

            logger.log("Cycle "+(i+1)+"/"+execution_cycles+": Searching for element ("+key+", "+
                    String.valueOf(target_casted.getStored_records().get(key))+") starting " +
                    "from node with ID "+source_casted.getIdOfLayer(0));

            // Lookup
            LookupResult result = this.lookup(source,key);
            collectedResults.add(result);

            logger.log("Cycle "+(i+1)+"/"+execution_cycles+": The element found was "+result.value);
        }

        String filename = "stats/lookup_network_"+Network.size()+"_l_"+this.l+"_n_"+this.execution_cycles+"_f_"+this.f+
                "_s_"+this.s+"_attack_edges_perc_"+String.format("%02.0f",this.ratioAttackEdges*100)+".txt";
        LookupResult.writeOnFile(collectedResults,filename);

        logger.log("**********");
        logger.log("Execution Summary:");
        double successes = 0.0;
        for (int i = 0; i < collectedResults.size(); i++) {
            if (collectedResults.get(i).value == null){
                logger.log("Cycle " + (i + 1) + "/" + execution_cycles + ": lookup failure (key not found), number of queries done " + collectedResults.get(i).messages + ".");
            } else {
                successes++;
                logger.log("Cycle " + (i + 1) + "/" + execution_cycles + ": key found, number of queries done " + collectedResults.get(i).messages + ".");
            }
        }
        logger.log("Percentage of successful lookups: "
                +String.format("%02.00f",(successes/(double)collectedResults.size())*100.0)+"%");


        return false;
    }

    /**
     * Get a random node which has to be not sybil.
     * It also avoid to get a node with a specific id.
     * @param target_node the node we want to avoid to select.
     * @return the random non-sybil node.
     */
    public Node getRandomNodeNotSybil(int target_node)
    {
        int random_guy_not_sybil;
        Node result;
        do {
            random_guy_not_sybil = rng.nextInt(Network.size());
            result = Network.get(random_guy_not_sybil);
        } while (((WhanauProtocol)result.getProtocol(this.pid)).isSybil()
                || random_guy_not_sybil == target_node);
        return result;
    }

    /**
     * Lookup for a key from a given node (it try do do that for 15 times using
     * also random walk sampling).
     * @param u the given starting node
     * @param key the given key we are searching
     * @return a LookupResult object which contains the value found and the number of queries done.
     */
    public LookupResult lookup(Node u,int key)
    {
        int triesNumber = this.f, counter = 0, total_messages=0;
        Pair<String, Integer> res = null;
        String value = null;
        Node v = u;
        do
        {
            res = _try(v,key);
            total_messages += res.second;
            value = res.first;
            v = this.randomWalk(u,this.w);
            counter++;
        } while(value == null && counter < triesNumber);
        return new LookupResult(value,total_messages);
    }

    /**
     * Given a source node, search for the given key starting from it.
     * @param source the source node
     * @param key the key we are looking for
     * @return the value associated with the key + the number of messages used.
     */
    public Pair<String, Integer> _try(Node source, int key)
    {
        WhanauProtocol u = (WhanauProtocol) source.getProtocol(this.pid);
        ArrayList<Pair<Integer, Node>> fingers = this.orderFingersCyclic(u.getFingersForLayer(0), key);
        int j = this.f-1;
        String value = null;
        int query_count =0;

        do {

            // Get only the fingers which are less than the key
            //if (fingers.get(j).first >= key)
            //{
            //    j--;
            //    continue;
            //}

            // Choose a finger from my own layers and query it in order to get
            // the value paired with the key.
            Pair<Node, Integer> choose_fing = this.chooseFinger(source, fingers.get(j).first, key);

            // If we did not find any suitable fingers, then we try again.
            if (choose_fing.first == null || choose_fing.second == null)
            {
                j--;
                continue;
            }

            value = this.query(choose_fing.first, choose_fing.second, key);

            query_count++;
            j = j-1;
        } while (value == null && j>=0);
        return new Pair<>(value, query_count);
    }


    /**
     * Choose a finger from a node such that it is the closest to the key we
     * are looking for.
     * @param source the node
     * @param id_layer_zero the id of node which
     * @param key the key we are looking for
     * @return a pair (Node, Integer) which contains the suitable fingers. It is null otherwise.
     */
    private Pair<Node, Integer> chooseFinger(Node source, int id_layer_zero, Integer key)
    {
        ArrayList<ArrayList<Pair<Integer, Node>>> F = new ArrayList<>(Collections.nCopies(this.l, new ArrayList<>()));
        WhanauProtocol u = (WhanauProtocol) source.getProtocol(this.pid);
        for (int i = 0; i < this.l; i++) {
            for (Pair<Integer, Node> x : u.getFingersForLayer(i)) {
                if (this.isBetween(id_layer_zero, x.first, key)) {
                    F.get(i).add(x);
                }
            }
        }

        // If F is completely empty then try again somewhere else
        int sum_elements=0;
        for (ArrayList<Pair<Integer, Node>> v : F)
        {
            sum_elements += v.size();
        }
        if (sum_elements == 0 ) return new Pair<>(null, null);

        // Get a random layer which is non-empty
        int random_layer = (rng.nextInt(Integer.MAX_VALUE)%this.l);
        while(F.get(random_layer).size() == 0)
        {
            random_layer = (rng.nextInt(Integer.MAX_VALUE)%this.l);
        }

        // Get a random finger from that layer
        int random_finger = ((rng.nextInt(Integer.MAX_VALUE)%F.get(random_layer).size()));
        Pair<Integer, Node> fig = F.get(random_layer).get(random_finger);

        return new Pair<>(fig.second, random_layer);

    }

    /**
     * Query a given node for a specific key on a specific layer.
     * @param u the node
     * @param layer the layer
     * @param key the key we are searching for
     * @return the value of the key, null if it was not found
     */
    private String query(Node u, int layer, int key)
    {
        WhanauProtocol prot = (WhanauProtocol) u.getProtocol(this.pid);

        //logger.log("Querying node with id "+prot.getIdOfLayer(layer)+" for key "+key);

        String value = prot.getValueOfKey(key, layer);

        return value;
    }

    private ArrayList<Pair<Integer, Node>> orderFingersCyclic(ArrayList<Pair<Integer, Node>> fingers, int key)
    {
        ArrayList<Pair<Integer, Node>> tmp = new ArrayList<Pair<Integer, Node>>(fingers.size());

        // Get the index of the first finger with a key less than mine
        int first_lower_key = this.f-1;
        while(fingers.get(first_lower_key).first >= key ) {
            first_lower_key--;
            if (first_lower_key < 0) break;
        }

        // All the element have a key less than mine
        if (first_lower_key == this.f-1)
            return fingers;

        // All the fingers are greater than my key, therefore
        // I need to invert the ordering of the elements
        if (first_lower_key == -1)
        {
            for (int i = fingers.size()-1; i >= 0 ; i--) {
                tmp.add(fingers.get(i));
            }
            return tmp;
        }

        // Populate the array on both sides
        for (int i = fingers.size()-1; i > first_lower_key ; i--) {
            tmp.add(fingers.get(i));
        }
        for (int i = first_lower_key; i >= 0; i--) {
            tmp.add(fingers.get(i));
        }

        return tmp;

    }

    private boolean isBetween(int start_key, int end_key, int target_key)
    {
        // It will return false only when 10 0 15

        // Case 1: 10 - 0 - 100
        boolean between;
        if(start_key<end_key) {
            between = target_key >= start_key && target_key <= end_key;
        } else {
            between = !(target_key >= start_key && target_key <= end_key);
        }
        return between;

        // Case 2:   100 - 0 - 10
        //return start_key >= target_key && target_key <= end_key;

    }

    // Key we want to find (it is computed dynamically from the target node)
    private Integer key;

    /* How many times we search for the key from different nodes */
    private int execution_cycles;

    static private String exec_cycles = "execution_cycles";

    private LoggerSingleton logger;
}

