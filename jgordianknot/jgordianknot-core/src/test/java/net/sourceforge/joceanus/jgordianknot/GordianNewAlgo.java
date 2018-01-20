package net.sourceforge.joceanus.jgordianknot;

import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyGroestlDigest;

public class GordianNewAlgo {

    /**
     * Main test case.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        /* Perform a simple hash */
        byte[] myBase = "The quick brown fox jumped over the lazy dog".getBytes();
        byte[] myResult = new byte[28];
        BouncyGroestlDigest myDigest = new BouncyGroestlDigest(224);
        myDigest.update(myBase, 0, myBase.length);
        myDigest.doFinal(myResult, 0);

        long myStart = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            myDigest.update(myBase, 0, myBase.length);
            myDigest.doFinal(myResult, 0);
        }
        final long myElapsed = System.nanoTime() - myStart;
        System.out.println("Time Elapsed - " + myElapsed / 1000);
    }
}
