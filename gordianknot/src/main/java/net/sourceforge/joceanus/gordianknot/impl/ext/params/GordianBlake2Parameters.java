package net.sourceforge.joceanus.gordianknot.impl.ext.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;

/**
 * Blake2 Parameters.
 */
public class GordianBlake2Parameters
        implements CipherParameters {
    /**
     * The key.
     */
    private byte[] theKey;

    /**
     * The salt.
     */
    private byte[] theSalt;

    /**
     * The personalisation.
     */
    private byte[] thePersonal;

    /**
     * The maximum xofLen.
     */
    private long theMaxXofLen;

    /**
     * The fanOut.
     */
    private short theFanOut;

    /**
     * The maxDepth.
     */
    private short theMaxDepth;

    /**
     * The leafLength.
     */
    private int theLeafLen;

    /**
     * Obtain the key.
     * @return the key
     */
    public byte[] getKey() {
        return Arrays.clone(theKey);
    }

    /**
     * Obtain the salt.
     * @return the salt
     */
    public byte[] getSalt() {
        return Arrays.clone(theSalt);
    }

    /**
     * Obtain the personalisation.
     * @return the personalisation
     */
    public byte[] getPersonalisation() {
        return Arrays.clone(thePersonal);
    }

    /**
     * Obtain the maximum output length.
     * @return the output length
     */
    public long getMaxOutputLength() {
        return theMaxXofLen;
    }

    /**
     * Obtain the treeLeafLength.
     * @return the leafLength
     */
    public int getTreeLeafLen() {
        return theLeafLen;
    }

    /**
     * Obtain the treeFanOut.
     * @return the fanOut
     */
    public short getTreeFanOut() {
        return theFanOut;
    }

    /**
     * Obtain the treeMaxDepth.
     * @return the maxDepth
     */
    public short getTreeMaxDepth() {
        return theMaxDepth;
    }

    /**
     * Parameter Builder.
     */
    public static class GordianBlake2ParametersBuilder {
        /**
         * The key.
         */
        private byte[] theKey;

        /**
         * The salt.
         */
        private byte[] theSalt;

        /**
         * The personalisation.
         */
        private byte[] thePersonal;

        /**
         * The maximum xofLen.
         */
        private long theMaxXofLen;

        /**
         * The fanOut.
         */
        private short theFanOut = 1;

        /**
         * The maxDepth.
         */
        private short theMaxDepth = 1;

        /**
         * The leafLength.
         */
        private int theLeafLen;

        /**
         * Set the key.
         * @param pKey the key
         * @return the Builder
         */
        public GordianBlake2ParametersBuilder setKey(final byte[] pKey) {
            theKey = Arrays.clone(pKey);
            return this;
        }

        /**
         * Set the salt.
         * @param pSalt the salt
         * @return the Builder
         */
        public GordianBlake2ParametersBuilder setSalt(final byte[] pSalt) {
            theSalt = Arrays.clone(pSalt);
            return this;
        }

        /**
         * Set the personalisation.
         * @param pPersonal the personalisation
         * @return the Builder
         */
        public GordianBlake2ParametersBuilder setPersonalisation(final byte[] pPersonal) {
            thePersonal = Arrays.clone(pPersonal);
            return this;
        }

        /**
         * Set the maximum output length. (-1=unlimited)
         * @param pMaxOutLen the maximum output length
         * @return the Builder
         */
        public GordianBlake2ParametersBuilder setMaxOutputLen(final long pMaxOutLen) {
            theMaxXofLen = pMaxOutLen;
            return this;
        }

        /**
         * Set the treeConfig.
         * @param pFanOut the fanOut (0=unlimited, 1-255).
         * @param pMaxDepth the maxDepth (2-255).
         * @param pLeafLen the leafLength (in bytes).
         * @return the Builder
         */
        public GordianBlake2ParametersBuilder setTreeConfig(final int pFanOut,
                                                            final int pMaxDepth,
                                                            final int pLeafLen) {
            theFanOut = (short) pFanOut;
            theMaxDepth = (short) pMaxDepth;
            theLeafLen = pLeafLen;
            return this;
        }

        /**
         * Build the parameters.
         * @return the parameters
         */
        public GordianBlake2Parameters build() {
            /* Create params */
            final GordianBlake2Parameters myParams = new GordianBlake2Parameters();

            /* Record key and Salt */
            if (theKey != null) {
                myParams.theKey = theKey;
            }
            if (theSalt != null) {
                myParams.theSalt = theSalt;
            }

            /* Record personalisation and xof length */
            if (thePersonal != null) {
                myParams.thePersonal = thePersonal;
            }
            myParams.theMaxXofLen = theMaxXofLen;

            /* Record tree details */
            myParams.theFanOut = theFanOut;
            myParams.theMaxDepth = theMaxDepth;
            myParams.theLeafLen = theLeafLen;

            /* Return the parameters */
            return myParams;
        }
    }
}
