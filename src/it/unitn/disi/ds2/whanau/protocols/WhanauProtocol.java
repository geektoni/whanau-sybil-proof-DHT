package it.unitn.disi.ds2.whanau.protocols;

import it.unitn.disi.ds2.whanau.utils.Pair;
import peersim.core.*;

import java.util.*;

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
        this.stored_records = new TreeMap<>();
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

    /**
     * Return a random finger from the specified layer.
     * @param layer
     * @return
     */
    public Pair<Integer, WhanauProtocol> randomRecordFingers(int layer)
    {
        // Return null if the layer is greater than the actual size.
        assert (layer < this.fingers.size() && layer >=0);

        int k_size = this.fingers.get(layer).size();
        int random_id = rng.nextInt(Integer.MAX_VALUE)%k_size;
        return this.fingers.get(layer).get(random_id);

    }

    public void setIdForLayer(int id, int layer)
    {
        assert (layer < this.ids.size() && layer >=0);

        ids.add(layer, id);
    }

    public void setFingerForLayer(ArrayList<Pair<Integer, WhanauProtocol>> fingers, int layer)
    {
        assert (layer < this.fingers.size() && layer >=0);
        this.fingers.add(layer, fingers);
    }

    public Integer getIdOfLayer(int layer)
    {
        assert (layer < this.ids.size() && layer >=0);
        return ids.get(layer);
    }

    /**
     * Return a sample of successors from the stored records
     * which are greater than the key.
     * @param key
     * @return
     */
    public ArrayList<Pair<Integer, String>> successorsSample(int key)
    {
        ArrayList<Pair<Integer, String>> successor_sample = new ArrayList<>();
        SortedMap<Integer, String> greater_value = stored_records.tailMap(key);
        int counter = 0;
        for (Integer k : greater_value.keySet())
        {
            if (counter >= T) break;
            successor_sample.add(new Pair<>(k, greater_value.get(k)));
            counter++;
        }
        return successor_sample;
    }

    public void addToSuccessors(ArrayList<Pair<Integer, String>> value, int layer)
    {
        assert (layer < this.succ.size() && layer >=0);

        this.succ.add(layer, value);
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

    TreeMap<Integer, String> stored_records;

    private static int T = 1;

}
