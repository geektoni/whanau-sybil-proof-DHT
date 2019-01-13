package it.unitn.disi.ds2.whanau.controls;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

public class WhanauSetup implements Control {

    public WhanauSetup(String prefix) {
        this.l = Configuration.getInt(prefix + "." + layers, 1);
        this.w = Configuration.getInt(prefix + "." + mixing_time, 1);
        this.d = Configuration.getInt(prefix + "." + database_size, 1);
        this.f = Configuration.getInt(prefix + "." + max_fingers, 1);
        this.s = Configuration.getInt(prefix + "." + max_successors, 1);
    }

    public boolean execute() {

        // Set up the db table
        for (int i=0; i< Network.size(); i++)
        {
           // WhanauProtocol node = (WhanauProtocol) Network.get(i);

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

    /* Possible parameters */
    final static private String layers = "layers";
    final static private String database_size = "database_size";
    final static private String max_successors = "max_successors";
    final static private String max_fingers = "max_fingers";
    final static private String mixing_time = "mixing_time";

    private int f;
    private int s;
    private int d;
    private int l;
    private int w;
}
