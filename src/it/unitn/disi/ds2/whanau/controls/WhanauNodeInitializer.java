package it.unitn.disi.ds2.whanau.controls;

import it.unitn.disi.ds2.whanau.protocols.WhanauProtocol;
import it.unitn.disi.ds2.whanau.protocols.WhanauSybilProtocol;
import peersim.core.Node;
import peersim.dynamics.NodeInitializer;

import java.util.Random;

/**
 * This class will initialize all the network topology.
 */
public class WhanauNodeInitializer implements NodeInitializer {

    public WhanauNodeInitializer(String s)
    {
        this._WhanauSetup();
    }

    public void _WhanauSetup() {
        this.rng = new Random(1);
        this.malicious = 2;
    }

    /**
     * This method will do the following things:
     * - Given a global social network topology, it will run the
     *   setup() method of the given node;
     * - It will transform a part of the nodes into malicious ones.
     * @param node the given network node
     */
    @Override
    public void initialize(Node node) {
        int isMalicious = rng.nextInt()%this.malicious;
        if (isMalicious>0)
        {
           WhanauSybilProtocol mnode = (WhanauSybilProtocol)node;
           mnode.setup();
        } else {
           WhanauProtocol n = (WhanauProtocol) node;
           n.setup();
        }
    }

    /**
     * Random seed generator
     */
    private Random rng;

    /**
     * Fraction of malicious nodes
     */
    private int malicious;

}
