package it.unitn.disi.ds2.whanau.controls;

import it.unitn.disi.ds2.whanau.protocols.WhanauProtocol;
import it.unitn.disi.ds2.whanau.utils.Pair;
import it.unitn.disi.ds2.whanau.utils.LookupResult;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

public class WhanauLookup extends WhanauSetup {

    public WhanauLookup(String prefix) {
        super(prefix);
        key = Configuration.getInt(prefix+"."+target_key,1);
        execution_cycles = Configuration.getInt(prefix + "." + exec_cycles,1);
    }

    /**
     * Execute the lookup action from a given random node
     * which is not sybil.
     * @return
     */
    public boolean execute()
    {
        ArrayList<LookupResult> collectedResults = new ArrayList<>();
        for (int i = 0; i < execution_cycles; i++) {
            // Get a random node not sybil
            Node source = this.getRandomNodeNotSybil(this.t_node);

            // Get the target node and get its key
            Node target = Network.get(this.t_node);
            WhanauProtocol target_casted = (WhanauProtocol) target.getProtocol(this.pid);
            key = target_casted.getIdOfLayer(0);

            // Lookup
            LookupResult result = this.lookup(source,key);
            collectedResults.add(result);

            System.out.println(String.valueOf(target_casted.getStored_records().get(key)));
            System.out.println(String.format("Find element: %s", result.value));
        }
        String filename = "stats/lookup_n"+this.execution_cycles+".txt";
        LookupResult.writeOnFile(collectedResults,filename);

        return false;
    }

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

    public LookupResult lookup(Node u,int key)
    {
        int triesNumber = 15, counter = 0, total_messages=0;
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
        return new LookupResult(value,counter+total_messages);
    }

    public Pair<String, Integer> _try(Node source, int key)
    {
        WhanauProtocol u = (WhanauProtocol) source.getProtocol(this.pid);
        ArrayList<Pair<Integer, Node>> fingers = u.getFingersForLayer(0);
        int j = this.f-1;
        String value = null;
        int query_count =0;
        do {
            if (fingers.get(j).first >= key)
            {
                j--;
                continue;
            }

            Pair<Node, Integer> choose_fing = this.chooseFinger(source, fingers.get(j).first, key);
            if (choose_fing.first == null || choose_fing.second == null)
                return null;
            value = this.query(choose_fing.first, choose_fing.second, key);
            query_count++;
            j = j-1;
        } while (value == null && j>=0);
        return new Pair<>(value, query_count);
    }

    private Pair<Node, Integer> chooseFinger(Node source, int id_layer_zero, Integer key)
    {
        ArrayList<ArrayList<Pair<Integer, Node>>> F = new ArrayList<>(Collections.nCopies(this.l, new ArrayList<>()));
        WhanauProtocol u = (WhanauProtocol) source.getProtocol(this.pid);
        for (int i = 0; i < this.l; i++) {
            for (Pair<Integer, Node> x : u.getFingersForLayer(i)) {
                if (x.first >= id_layer_zero && x.first <= key) {
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

    private String query(Node u, int layer, int key)
    {
        WhanauProtocol prot = (WhanauProtocol) u.getProtocol(this.pid);

        //System.out.println("Trying node "+String.valueOf(prot.getIdOfLayer(0))+" for key "+key);

        String value = prot.getValueOfKey(key, layer);

        return value;
    }

    // Key we want to find
    private Integer key;
    private int execution_cycles;

    static private String target_key = "target_key";
    static private String exec_cycles = "execution_cycles";
}

