package it.unitn.disi.ds2.whanau.protocols;

import peersim.core.Protocol;

/**
 * This class will contain all the methods specified
 * by the protocol. See Section 6 of the original paper.
 */
public class WhanauProtocol implements Protocol {

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

}
