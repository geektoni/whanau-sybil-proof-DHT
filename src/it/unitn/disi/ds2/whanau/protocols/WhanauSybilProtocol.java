package it.unitn.disi.ds2.whanau.protocols;

import peersim.core.MaliciousProtocol;

/**
 * This class will implement a malicious protocol which will make
 * a node behave like a Sybil node.
 */
public class WhanauSybilProtocol implements MaliciousProtocol {

    /**
     * Empty constructor method
     * @param s a random string, it can be null
     */
    public WhanauSybilProtocol(String s) {
    }

    @Override
    public Object clone() {
        return null;
    }

    public void setup() {

    }
}
