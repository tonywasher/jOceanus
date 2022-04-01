/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
 * Used for the following constructs
 * <ul>
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

            /* Record personalisation */
            if (thePersonal != null) {
                myParams.thePersonal = thePersonal;
            }

            /* Return the parameters */
            return myParams;
        }
    }
}
