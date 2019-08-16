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

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;

/**
 * Blake2 Parameters.
 */
public class Blake2Parameters
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
        public Builder setKey(final byte[] pKey) {
            theKey = Arrays.clone(pKey);
            return this;
        }

        /**
         * Set the salt.
         * @param pSalt the salt
         * @return the Builder
         */
        public Builder setSalt(final byte[] pSalt) {
            theSalt = Arrays.clone(pSalt);
            return this;
        }

        /**
         * Set the personalisation.
         * @param pPersonal the personalisation
         * @return the Builder
         */
        public Builder setPersonalisation(final byte[] pPersonal) {
            thePersonal = Arrays.clone(pPersonal);
            return this;
        }

        /**
         * Set the maximum output length. (-1 = unlimited)
         * @param pMaxXofLen the maximum output length
         * @return the Builder
         */
        public Builder setMaxXofLen(final long pMaxXofLen) {
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
