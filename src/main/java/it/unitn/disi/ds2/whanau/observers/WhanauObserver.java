package it.unitn.disi.ds2.whanau.observers;

import it.unitn.disi.ds2.whanau.protocols.WhanauProtocol;
import it.unitn.disi.ds2.whanau.utils.Pair;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

import java.util.ArrayList;

/**
 * Class which collects statistics about a node in the network.
 */
public class WhanauObserver implements Control {

    public WhanauObserver(String prefix) {
        this.pid = Configuration.getPid(prefix + "." + prot);
        this.lid = Configuration.getPid(prefix + "." + prot_link);
    }

    @Override
    public boolean execute() {

        WhanauProtocol node = (WhanauProtocol) Network.get(0).getProtocol(this.pid);

        System.out.println("Node 0:");
        System.out.println("-> Stored Records:"+String.valueOf(node.getStored_records()));
        System.out.println("-> Ids of Node: "+String.valueOf(node.getIds()));
        System.out.println("-> Database: "+String.valueOf(node.getDb()));
        System.out.println("-> Successors:"+String.valueOf(node.getSucc()));
        System.out.println("-> Fingers:"+String.valueOf(node.getFingers()));


        return false;
    }

    protected int pid;
    protected int lid;
    static private String prot = "protocol";
    static private String prot_link = "protocol_link";
}
