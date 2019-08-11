/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.mac;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;

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
    private Long theOutLen;

    /**
     * Tree Configuration.
     */
    private GordianTreeConfiguration theTreeConfig;

    /**
     * Tree Node Location.
     */
    private GordianTreeNodeLocation theNodeLocation;

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
    public Long getOutputLength() {
        return theOutLen;
    }

    /**
     * Obtain the treeConfig.
     * @return the treeConfig
     */
    public GordianTreeConfiguration getTreeConfig() {
        return theTreeConfig;
    }

    /**
     * Obtain the treeNodeLocation.
     * @return the treeNodeLocation
     */
    public GordianTreeNodeLocation getTreeNodeLocation() {
        return theNodeLocation;
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
        private Long theOutLen;

        /**
         * Tree Configuration.
         */
        private GordianTreeConfiguration theTreeConfig;

        /**
         * Tree Node Location.
         */
        private GordianTreeNodeLocation theNodeLocation;

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
         * @param pConfig the config
         * @return the Builder
         */
        GordianMacParametersBuilder setTreeConfig(final GordianTreeConfiguration pConfig) {
            theTreeConfig = pConfig;
            return this;
        }

        /**
         * Set the treeLocation.
         * @param pLocation the location
         * @return the Builder
         */
        GordianMacParametersBuilder setTreeNodeLocation(final GordianTreeNodeLocation pLocation) {
            theNodeLocation = pLocation;
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
            if (theOutLen != null) {
                myParams.theOutLen = theOutLen;
            }

            /* Record tree details */
            if (theTreeConfig != null) {
                myParams.theTreeConfig = theTreeConfig;
            }
            if (theNodeLocation != null) {
                myParams.theNodeLocation = theNodeLocation;
            }

            /* Return the parameters */
            return myParams;
        }
    }

    /**
     * TreeHash Configuration.
     */
    public static class GordianTreeConfiguration {
        /**
         * The LeafLength.
         */
        private final int theLeafLen;

        /**
         * The FanOut.
         */
        private final int theFanOut;

        /**
         * The MaxDepth.
         */
        private final int theMaxDepth;

        /**
         * Constructor.
         * @param pLeafLen the leafLength
         * @param pFanOut the fanOut
         * @param pMaxDepth the maximumDepth
         */
        public GordianTreeConfiguration(final int pLeafLen,
                                        final int pFanOut,
                                        final int pMaxDepth) {
            theLeafLen = pLeafLen;
            theFanOut = pFanOut;
            theMaxDepth = pMaxDepth;
        }

        /**
         * Obtain the leafLen.
         * @return the leafLen
         */
        public int getLeafLen() {
            return theLeafLen;
        }

        /**
         * Obtain the fanOut.
         * @return the fanOut
         */
        public int getFanOut() {
            return theFanOut;
        }

        /**
         * Obtain the maxDepth.
         * @return the maxDepth
         */
        public int getMaxDepth() {
            return theMaxDepth;
        }
    }

    /**
     * TreeNodeLocation Configuration.
     */
    public static class GordianTreeNodeLocation {
        /**
         * The Level.
         */
        private final int theLevel;

        /**
         * The Index.
         */
        private final int theIndex;

        /**
         * Constructor.
         * @param pLevel the level
         * @param pIndex the index
         */
        public GordianTreeNodeLocation(final int pLevel,
                                       final int pIndex) {
            theLevel = pLevel;
            theIndex = pIndex;
        }

        /**
         * Obtain the level.
         * @return the level
         */
        public int getLevel() {
            return theLevel;
        }

        /**
         * Obtain the index.
         * @return the index
         */
        public int getIndex() {
            return theIndex;
        }
    }
}
