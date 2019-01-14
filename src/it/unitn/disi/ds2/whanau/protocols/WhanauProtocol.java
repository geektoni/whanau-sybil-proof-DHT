package it.unitn.disi.ds2.whanau.protocols;

import it.unitn.disi.ds2.whanau.utils.Pair;
import peersim.core.*;

import java.util.ArrayList;
import java.util.Hashtable;

import java.util.Random;

/**
 * This class will contain all the methods specified
 * by the protocol. See Section 6 of the original paper.
 */
public class WhanauProtocol implements Protocol {

    /**
     * Empty constructor method
     *
     * @param prefix a random string, it can be null
     */
    public WhanauProtocol(String prefix) {
        this.prefix = prefix;
        this.rng = new Random();
        this.neighbors = new ArrayList<>();
        this.stored_records = new Hashtable<>();
    }

    public void setUpInternalTables(int f, int s, int d, int l, int w) {
        // Set up the constraint for the various tables;
        this.mixing_time = w;
        this.max_layers = l;
        this.max_successors = s;
        this.max_fingers = f;
        this.max_db_size = d;

        this.db = new Hashtable<Integer, String>();
        this.fingers = new ArrayList<ArrayList<Pair<Integer, WhanauProtocol>>>(max_layers);
        this.succ = new ArrayList<ArrayList<Pair<Integer, String>>>(max_layers);
        this.ids = new ArrayList<Integer>(max_layers);
    }

    /**
     * Add a value to the record stored by this node
     * (if not already present).
     *
     * @param key   hash key of the given value.
     * @param value the string value which will be recorded.
     */
    public void addToStoredRecords(Pair<Integer, String>value) {
        if (!this.stored_records.containsKey(value.first)) {
            this.stored_records.put(value.first, value.second);
        }
    }

    /**
     * Extract a random record from the node database
     * @return a random <key,value pair>
     */
    public Pair<Integer, String> randomRecord()
    {
        int k_size = this.stored_records.keySet().size();
        Integer random_key = (Integer) this.stored_records.keySet().
                toArray()[rng.nextInt(Integer.MAX_VALUE)%k_size];
        return new Pair<>(random_key, this.stored_records.get(random_key));
    }


    @Override
    public Object clone() {
        return new WhanauProtocol(this.prefix);
    }

    /* Private variables */
    ArrayList<Integer> ids;
    ArrayList<ArrayList<Pair<Integer, WhanauProtocol>>> fingers;
    ArrayList<ArrayList<Pair<Integer, String>>> succ;
    Hashtable<Integer, String> db;

    int max_db_size;
    int mixing_time;
    int max_fingers;
    int max_successors;
    int max_layers;

    Random rng;

    ArrayList<Node> neighbors;

    String prefix;

    Hashtable<Integer, String> stored_records;

}
