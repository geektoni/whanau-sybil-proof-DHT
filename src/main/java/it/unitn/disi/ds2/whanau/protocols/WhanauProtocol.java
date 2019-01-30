package it.unitn.disi.ds2.whanau.protocols;

import it.unitn.disi.ds2.whanau.utils.Pair;
import peersim.config.Configuration;
import peersim.core.*;

import java.util.*;

/**
 * Class which contains the tables and values specified
 * by the Whanau protocol. It also provides some helper
 * methods to access those values.
 */
public class WhanauProtocol implements Protocol {

    /**
     * Constructor
     * @param prefix identifier used in the configuration file
     */
    public WhanauProtocol(String prefix) {
        this.prefix = prefix;
        this.rng = new Random(Configuration.getInt("random.seed", 1));
        this.stored_records = new TreeMap<>();
        this.sybil = false;
    }

    /**
     * Set up the internal tables of the node.
     * @param f Max numbers of fingers per layer we can have.
     * @param s Max numbers of successors per layer we can have.
     * @param d Max size of the db.
     * @param l Max number of layers.
     * @param w Mixing time of the graph.
     */
    public void setUpInternalTables(int f, int s, int d, int l, int w) {
        // Set up the constraint for the various tables;
        this.mixing_time = w;
        this.max_layers = l;
        this.max_successors = s;
        this.max_fingers = f;
        this.max_db_size = d;

        this.db = new Hashtable<>();
        this.fingers = new ArrayList<>(max_layers);
        this.succ = new ArrayList<>(max_layers);
        this.ids = new ArrayList<>(max_layers);
    }

    /**
     * Add a value to the record stored by this node if not already present.
     * @param value Pair (key, value) which will be memorized.
     */
    public void addToStoredRecords(Pair<Integer, String>value) {
        if (!this.stored_records.containsKey(value.first)) {
            this.stored_records.put(value.first, value.second);
        }
    }

    /**
     * Extract a random record from the node database.
     * @return a random (key,value pair)
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
     * @param layer the given layer
     * @return a random finger from the given layer.
     */
    public Pair<Integer, Node> randomRecordFingers(int layer)
    {
        // Return null if the layer is greater than the actual size.
        assert (layer < this.fingers.size() && layer >=0);

        int k_size = this.fingers.get(layer).size();
        int random_id = rng.nextInt(Integer.MAX_VALUE)%k_size;
        return this.fingers.get(layer).get(random_id);

    }

    /**
     * Set an id for the specific layer.
     * @param id the given id.
     * @param layer the given layer.
     */
    public void setIdForLayer(int id, int layer)
    {
        assert (layer < this.ids.size() && layer >=0);

        ids.add(layer, id);
    }

    /**
     * Set the fingers for a specified layer.
     * @param fingers the fingers.
     * @param layer the layers.
     */
    public void setFingerForLayer(ArrayList<Pair<Integer, Node>> fingers, int layer)
    {
        assert (layer < this.fingers.size() && layer >=0);
        this.fingers.add(layer, fingers);
    }

    /**
     * Returns if the node is sybil
     * @return the boolean value
     */
    public boolean isSybil() {
        return sybil;
    }

    /**
     * Sets the node's behaviour (sybil, not sybil)
     * @param sybil the boolean value
     */
    public void setSybil(boolean sybil) {
        this.sybil = sybil;
    }

    /**
     * Get the id of a layer.
     * @param layer the given layer.
     * @return the ID of that layer
     */
    public Integer getIdOfLayer(int layer)
    {
        assert (layer < this.ids.size() && layer >=0);
        return ids.get(layer);
    }

    /**
     * Return a sample of successors from the stored records
     * which are greater than the key.
     * @param key the given key
     * @return an array with a sample of the successors.
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

        // It means that we reached the end of the stored_records and we
        // need to read values from the other side.
        if (counter < T)
        {
            SortedMap<Integer, String> next_values = stored_records.subMap(stored_records.firstKey(), key);
            for (Integer k : next_values.keySet())
            {
                // We took basically all the available keys
                if ( k == key) break;
                successor_sample.add(new Pair<>(k, next_values.get(k)));
            }
        }

        return successor_sample;
    }

    /**
     * Add successors to a given layer.
     * @param value the successors.
     * @param layer the given layer.
     */
    public void addToSuccessors(ArrayList<Pair<Integer, String>> value, int layer)
    {
        assert (layer < this.succ.size() && layer >=0);

        this.succ.add(layer, value);
    }

    /**
     * Return the value of the key at the given layer
     * @param key the key
     * @param layer layer
     * @return the returned value
     */
    public String getValueOfKey(Integer key, int layer)
    {
        assert (layer >=0 && layer < succ.size());
        if(!this.sybil) {
            for (Pair<Integer, String> successor : succ.get(layer)) {
                if (successor.first.equals(key))
                    return successor.second;
            }
        }
        return null;
    }

    public boolean isInFingers(Pair<Integer, Node> fing, Integer layer)
    {
        assert (layer >=0 && layer < fingers.size());
        for (Pair<Integer, Node> f : fingers.get(layer))
            if (f.first.equals(fing.first) && f.second.equals(fing.second))
                return true;
        return false;
    }

    public ArrayList<Pair<Integer, Node>> getFingersForLayer(int layer)
    {
        assert (layer >=0 && layer < fingers.size());
        return this.fingers.get(layer);
    }

    /**
     * Clone method.
     * @return a cloned instance.
     */
    @Override
    public Object clone() {
        return new WhanauProtocol(this.prefix);
    }


    public ArrayList<Integer> getIds() {
        return ids;
    }

    public ArrayList<ArrayList<Pair<Integer, Node>>> getFingers() {
        return fingers;
    }

    public ArrayList<ArrayList<Pair<Integer, String>>> getSucc() {
        return succ;
    }

    public Hashtable<Integer, String> getDb() {
        return db;
    }

    public TreeMap<Integer, String> getStored_records() {
        return stored_records;
    }

    public void addToDb(Pair<Integer, String> value)
    {
        if (!this.db.containsKey(value.first)) {
            this.db.put(value.first, value.second);
        }
    }

    /* Private variables */
    private ArrayList<Integer> ids;
    private ArrayList<ArrayList<Pair<Integer, Node>>> fingers;
    private ArrayList<ArrayList<Pair<Integer, String>>> succ;
    private Hashtable<Integer, String> db;

    private int max_db_size;
    private int mixing_time;
    private int max_fingers;
    private int max_successors;
    private int max_layers;

    private Random rng;

    private String prefix;

    private TreeMap<Integer, String> stored_records;
    private boolean sybil;

    private static int T = 1;

}
