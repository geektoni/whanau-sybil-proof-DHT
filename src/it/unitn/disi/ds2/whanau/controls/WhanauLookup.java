package it.unitn.disi.ds2.whanau.controls;

import it.unitn.disi.ds2.whanau.protocols.WhanauProtocol;
import it.unitn.disi.ds2.whanau.utils.Pair;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;

import java.util.ArrayList;
import java.util.Collections;

public class WhanauLookup extends WhanauSetup {

    public WhanauLookup(String prefix) {
        super(prefix);
        key = Configuration.getInt(prefix+"."+target_key,1);
    }

    /**
     * Execute the lookup action from a given random node
     * which is not sybil.
     * @return
     */
    public boolean execute()
    {
        Node source = Network.get(0);

        String value = this.lookup(source, 45);

        System.out.println(String.format("Find element: %s", value));

        return false;
    }

    public String lookup(Node u, int key)
    {
        String value = null;
        Node v = u;
        int retry = 10;
        do {
            value =  _try(v, key);
            v = this.randomWalk(u, this.w);
            retry--;
        } while (value == null && retry > 0);
        return value;
    }


    public String _try(Node source, int key)
    {
        WhanauProtocol u = (WhanauProtocol) source.getProtocol(this.pid);
        ArrayList<Pair<Integer, Node>> fingers = u.getFingersForLayer(0);
        assert (fingers.size() == this.f);
        int j = this.f-1;
        String value = null;
        do {
            Pair<Node, Integer> choose_fing = this.chooseFinger(source, fingers.get(j).first, key);
            value = this.query(choose_fing.first, choose_fing.second, key);
        } while (value == null && j>=0);
        return value;
    }

    private Pair<Node, Integer> chooseFinger(Node source, int id_layer_zero, Integer key)
    {
        ArrayList<ArrayList<Pair<Integer, Node>>> F = new ArrayList<>(Collections.nCopies(this.l, new ArrayList<>()));
        WhanauProtocol u = (WhanauProtocol) source.getProtocol(this.pid);
        for (int i = 0; i < this.l; i++) {
            for (Pair<Integer, Node> x : u.getFingersForLayer(i))
                if (x.first >= id_layer_zero && x.first <= key)
                {
                    F.get(i).add(x);
                }
        }

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
        String value = prot.getValueOfKey(key, layer);
        return value;
    }

    // Key we want to find
    private Integer key;

    static private String target_key = "target_key";
}

