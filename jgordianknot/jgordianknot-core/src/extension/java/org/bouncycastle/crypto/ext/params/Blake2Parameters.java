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
package org.bouncycastle.crypto.ext.params;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacParameters;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacParameters.GordianMacParametersBuilder;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;

/**
 * Blake2 Parameters.
 */
public class Blake2Parameters {
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
    private Long theMaxXofLen;

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
     * The nodeIndex.
     */
    private int theNodeIndex;

    /**
     * The nodeDepth.
     */
    private short theNodeDepth;

    /**
     * Constructor.
     */
    public Blake2Parameters() {
    }

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
    public Long getMaxOutputLength() {
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
     * Obtain the treeNodeDepth.
     * @return the depth
     */
    public short getTreeNodeDepth() {
        return theNodeDepth;
    }

    /**
     * Obtain the treeNodeIndex.
     * @return the index
     */
    public int getTreeNodeIndex() {
        return theNodeIndex;
    }

    /**
     * Parameter Builder.
     */
    public static class Builder {
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
        private Long theMaxXofLen;

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
         * The nodeIndex.
         */
        private int theNodeIndex;

        /**
         * The nodeDepth.
         */
        private short theNodeDepth;

        /**
         * Set the key.
         * @param pKey the key
         * @return the Builder
         */
        Builder setKey(final byte[] pKey) {
            theKey = Arrays.clone(pKey);
            return this;
        }

        /**
         * Set the salt.
         * @param pSalt the salt
         * @return the Builder
         */
        Builder setNonce(final byte[] pSalt) {
            theSalt = Arrays.clone(pSalt);
            return this;
        }

        /**
         * Set the personalisation.
         * @param pPersonal the personalisation
         * @return the Builder
         */
        Builder setPersonalisation(final byte[] pPersonal) {
            thePersonal = Arrays.clone(pPersonal);
            return this;
        }

        /**
         * Set the maximum output length.
         * @param pMaxXofLen the maximum output length
         * @return the Builder
         */
        Builder setMaxXofLen(final long pMaxXofLen) {
            theMaxXofLen = pMaxXofLen;
            return this;
        }

        /**
         * Set the treeConfig.
         * @param pFanOut the fanout.
         * @param pMaxDepth the maxDepth.
         * @param pLeafLen the leafLength.
         * @return the Builder
         */
        public Builder setTreeConfig(final int pFanOut,
                                     final int pMaxDepth,
                                     final int pLeafLen) {
            theFanOut = (short) pFanOut;
            theMaxDepth = (short) pMaxDepth;
            theLeafLen = pLeafLen;
            return this;
        }

        /**
         * Set the nodePosition.
         * @param pOffset the offset.
         * @param pDepth the depth.
         * @return the Builder
         */
        public Builder setNodePosition(final int pOffset,
                                       final int pDepth) {
            theNodeIndex = pOffset;
            theNodeDepth = (short) pDepth;
            return this;
        }

        /**
         * Build the parameters.
         * @return the parameters
         */
        public Blake2Parameters build() {
            /* Create params */
            final Blake2Parameters myParams = new Blake2Parameters();

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
            if (theMaxXofLen != null) {
                myParams.theMaxXofLen = theMaxXofLen;
            }

            /* Record tree details */
            if (theFanOut != 0) {
                myParams.theFanOut = theFanOut;
                myParams.theMaxDepth = theMaxDepth;
                myParams.theLeafLen = theLeafLen;
                myParams.theNodeIndex = theNodeIndex;
                myParams.theNodeDepth = theNodeDepth;
            }

            /* Return the parameters */
            return myParams;
        }
    }
}
