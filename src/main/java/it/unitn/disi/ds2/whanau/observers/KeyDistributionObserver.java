package it.unitn.disi.ds2.whanau.observers;

import it.unitn.disi.ds2.whanau.protocols.WhanauProtocol;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class used to collect the key distribution over all the layers for each node.
 */
public class KeyDistributionObserver implements Control {

    public KeyDistributionObserver(String prefix) {
        this.pid = Configuration.getPid(prefix + "." + prot);
        this.lid = Configuration.getPid(prefix + "." + prot_link);
    }


    @Override
    public boolean execute() {
        int size = Network.size();
        int l = Configuration.getInt(layers,-1);
        // list of lists: list i contains the level-i id for the nodes in the network in index order [Network.get(i)]
        ArrayList<ArrayList<Integer>> layer_distribution = new ArrayList<>();
        // list of integers: value i (vi) tells that Network.get(i).isSybil() is true if vi==1, otherwise vi=0
        ArrayList<Integer> sybil = new ArrayList<>();
        for (int i = 0; i < l; i++) {
            layer_distribution.add(new ArrayList<>());
        }

        //scan each node and update the corresponding lists
        for (int i = 0; i < size; i++) {
            WhanauProtocol node = (WhanauProtocol) Network.get(i).getProtocol(this.pid);
            ArrayList<Integer> ids = node.getIds();
            for(int j=0;j<l;j++) {
                layer_distribution.get(j).add(ids.get(j));
            }
            sybil.add(node.isSybil() ? 1 : 0);
        }

        // save the results on the files
        String filename_layers = "stats/layer_distribution.txt";
        String filename_sybil = "stats/sybil.txt";
        try {

            // each line contains the key of the node separated by <whitespace>
            // the first line contains the layer 0 information, the second refers to layer 1 ...
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename_layers));
            for (ArrayList<Integer> list : layer_distribution) {
                for (Integer v:list) {
                    writer.write(v+" ");
                }
                writer.write("\n");
            }
            writer.close();

            // the sybil file contains a line of 1 and 0 separated by <whitespace> telling whether node i is sybil or not
            writer = new BufferedWriter(new FileWriter(filename_sybil));
            for (Integer x : sybil) {
                writer.write(x+" ");
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Impossible to write on file '"+filename_layers+"' or '"+filename_sybil+"'");
        }

        return false;
    }


    protected int pid;
    protected int lid;
    static private String prot = "protocol";
    static private String prot_link = "protocol_link";
    static private String layers = "layers";
}
