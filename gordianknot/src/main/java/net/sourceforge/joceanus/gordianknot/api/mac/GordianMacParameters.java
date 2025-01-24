/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.gordianknot.api.mac;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;

/**
 * Mac Parameters.
 */
public final class GordianMacParameters {
    /**
     * The Key.
     */
    private GordianKey<GordianMacSpec> theKey;

    /**
     * The Nonce.
     */
    private byte[] theNonce;

    /**
     * Random Nonce requested?
     */
    private boolean randomNonce;

    /**
     * Personalisation.
     */
    private byte[] thePersonal;

    /**
     * Output length.
     */
    private long theOutLen;

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
     * Constructor.
     */
    private GordianMacParameters() {
    }

    /**
     * Generate keyOnly Parameters.
     * @param pKey the key
     * @return the macParameters
     */
    public static GordianMacParameters key(final GordianKey<GordianMacSpec> pKey) {
        final GordianMacParameters myParams = new GordianMacParameters();
        myParams.theKey = pKey;
        return myParams;
    }

    /**
     * Obtain keyAndNonce Parameters.
     * @param pKey the key
     * @param pNonce the nonce
     * @return the macParameters
     */
    public static GordianMacParameters keyAndNonce(final GordianKey<GordianMacSpec> pKey,
                                                   final byte[] pNonce) {
        final GordianMacParameters myParams = new GordianMacParameters();
        myParams.theKey = pKey;
        myParams.theNonce = Arrays.clone(pNonce);
        return myParams;
    }

    /**
     * Obtain keyAndRandomNonce Parameters.
     * @param pKey the key
     * @return the macParameters
     */
    public static GordianMacParameters keyWithRandomNonce(final GordianKey<GordianMacSpec> pKey) {
        final GordianMacParameters myParams = new GordianMacParameters();
        myParams.theKey = pKey;
        myParams.randomNonce = true;
        return myParams;
    }

    /**
     * Obtain the key.
     * @return the key
     */
    public GordianKey<GordianMacSpec> getKey() {
        return theKey;
    }

    /**
     * Obtain the Nonce.
     * @return the nonce
     */
    public byte[] getNonce() {
        return Arrays.clone(theNonce);
    }

    /**
     * Is the nonce randomly generated?
     * @return true/false
     */
    public boolean randomNonce() {
        return randomNonce;
    }

    /**
     * Obtain the Personalisation.
     * @return the personalisation
     */
    public byte[] getPersonal() {
        return Arrays.clone(thePersonal);
    }

    /**
     * Obtain the Output length.
     * @return the outLength
     */
    public long getOutputLength() {
        return theOutLen;
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
    public static class GordianMacParametersBuilder {
        /**
         * The Key.
         */
        private GordianKey<GordianMacSpec> theKey;

        /**
         * The Nonce.
         */
        private byte[] theNonce;

        /**
         * Random Nonce requested?
         */
        private boolean randomNonce;

        /**
         * Personalisation.
         */
        private byte[] thePersonal;

        /**
         * Output length.
         */
        private long theOutLen;

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
        GordianMacParametersBuilder setKey(final GordianKey<GordianMacSpec> pKey) {
            theKey = pKey;
            return this;
        }

        /**
         * Set the nonce.
         * @param pNonce the nonce
         * @return the Builder
         */
        GordianMacParametersBuilder setNonce(final byte[] pNonce) {
            theNonce = Arrays.clone(pNonce);
            randomNonce = false;
            return this;
        }

        /**
         * Use random nonce.
         * @return the Builder
         */
        GordianMacParametersBuilder withRandomNonce() {
            theNonce = null;
            randomNonce = true;
            return this;
        }

        /**
         * Set the personalisation.
         * @param pPersonal the personalisation
         * @return the Builder
         */
        GordianMacParametersBuilder setPersonalisation(final byte[] pPersonal) {
            thePersonal = Arrays.clone(pPersonal);
            return this;
        }

        /**
         * Set the output length.
         * @param pOutLen the outputLen
         * @return the Builder
         */
        GordianMacParametersBuilder setOutputLength(final long pOutLen) {
            theOutLen = pOutLen;
            return this;
        }

        /**
         * Set the treeConfig.
         * @param pFanOut the fanout.
         * @param pMaxDepth the maxDepth.
         * @param pLeafLen the leafLength.
         * @return the Builder
         */
        public GordianMacParametersBuilder setTreeConfig(final int pFanOut,
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
        public GordianMacParameters build() {
            /* Create params */
            final GordianMacParameters myParams = new GordianMacParameters();

            /* Record key and Nonce */
            if (theKey != null) {
                myParams.theKey = theKey;
            }
            if (theNonce != null) {
                myParams.theNonce = theNonce;
            } else if (randomNonce) {
                myParams.randomNonce = true;
            }

            /* Record personalisation and output length */
            if (thePersonal != null) {
                myParams.thePersonal = thePersonal;
            }
            myParams.theOutLen = theOutLen;

            /* Record tree details */
            myParams.theFanOut = theFanOut;
            myParams.theMaxDepth = theMaxDepth;
            myParams.theLeafLen = theLeafLen;

            /* Return the parameters */
            return myParams;
        }
    }
}
