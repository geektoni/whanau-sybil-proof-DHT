package it.unitn.disi.ds2.whanau.protocols;

import it.unitn.disi.ds2.whanau.utils.Pair;
import peersim.core.Protocol;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This class will contain all the methods specified
 * by the protocol. See Section 6 of the original paper.
 */
public class WhanauProtocol implements Protocol {

    /**
     * Empty constructor method
     * @param str a random string, it can be null
     */
    public WhanauProtocol(String str) {
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

    @Override
    public Object clone() {
        return null;
    }

    /**
     * Methods used for the setup of the network
     */

    public void setup() {};

    private void sampleRecords() {};

    private void sampleRecord() {};

    private void randomWalk() {};

    private void chooseId() {};

    private void fingers() {};

    private void successors() {};

    private void successorSample() {};


    /**
     * Methods used to lookup a value.
     */

    public void lookup() {};

    private void _try() {};

    private void chooseFinger() {};

    private void query() {};


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

}
