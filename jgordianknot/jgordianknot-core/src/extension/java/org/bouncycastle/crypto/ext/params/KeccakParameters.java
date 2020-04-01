/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
 * Keccak Parameters.
 * <p>
 * Used for the follwoing constructs
 * <ul>
 *     <li>SHAKE
 *     <li>cSHAKE
 *     <li>KMAC
 *     <li>Kangaroo
 * </ul>
 */
public class KeccakParameters
    implements CipherParameters  {
    /**
     * The key.
     */
    private byte[] theKey;

    /**
     * The personalisation.
     */
    private byte[] thePersonal;

    /**
     * The maximum xofLen.
     */
    private long theMaxXofLen;

    /**
     * Obtain the key.
     * @return the key
     */
    public byte[] getKey() {
        return Arrays.clone(theKey);
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
     * Parameter Builder.
     */
    public static class Builder {
        /**
         * The key.
         */
        private byte[] theKey;

        /**
         * The personalisation.
         */
        private byte[] thePersonal;

        /**
         * The maximum xofLen.
         */
        private long theMaxXofLen;

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
         * Set the personalisation.
         * @param pPersonal the personalisation
         * @return the Builder
         */
        public Builder setPersonalisation(final byte[] pPersonal) {
            thePersonal = Arrays.clone(pPersonal);
            return this;
        }

        /**
         * Set the maximum output length. (-1=unlimited)
         * @param pMaxOutLen the maximum output length
         * @return the Builder
         */
        public Builder setMaxOutputLen(final long pMaxOutLen) {
            theMaxXofLen = pMaxOutLen;
            return this;
        }

        /**
         * Build the parameters.
         * @return the parameters
         */
        public KeccakParameters build() {
            /* Create params */
            final KeccakParameters myParams = new KeccakParameters();

            /* Record key */
            if (theKey != null) {
                myParams.theKey = theKey;
            }

            /* Record personalisation and xof length */
            if (thePersonal != null) {
                myParams.thePersonal = thePersonal;
            }
            myParams.theMaxXofLen = theMaxXofLen;

            /* Return the parameters */
            return myParams;
        }
    }
}
