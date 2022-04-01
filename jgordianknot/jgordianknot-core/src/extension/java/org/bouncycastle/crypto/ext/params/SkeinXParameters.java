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

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

import org.bouncycastle.crypto.params.SkeinParameters;

/**
 * Extended Skein Parameters.
 */
public class SkeinXParameters
    extends SkeinParameters {
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
     * A builder for {@link SkeinXParameters}.
     */
    public static class Builder
            extends SkeinParameters.Builder
    {
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

        @Override
        public Builder set(int type, byte[] value) {
            return (Builder) super.set(type, value);
        }

        @Override
        public Builder setKey(byte[] key) {
            return (Builder) super.setKey(key);
        }

        @Override
        public Builder setPersonalisation(byte[] personalisation) {
            return (Builder) super.setPersonalisation(personalisation);
        }

        @Override
        public Builder setPersonalisation(Date date, String emailAddress, String distinguisher) {
            return (Builder) super.setPersonalisation(date, emailAddress, distinguisher);
        }

        @Override
        public Builder setPersonalisation(Date date, Locale dateLocale, String emailAddress, String distinguisher) {
            return (Builder) super.setPersonalisation(date, dateLocale, emailAddress, distinguisher);
        }

        @Override
        public Builder setPublicKey(byte[] publicKey) {
            return (Builder) super.setPublicKey(publicKey);
        }

        @Override
        public Builder setKeyIdentifier(byte[] keyId) {
            return (Builder) super.setKeyIdentifier(keyId);
        }

        @Override
        public Builder setNonce(byte[] nonce) {
            return (Builder) super.setNonce(nonce);
        }

        /**
         * Set the maximum output length. (-1=unlimited, 0=underlying)
         * @param pMaxOutLen the maximum output length
         * @return the Builder
         */
        public Builder setMaxOutputLen(final long pMaxOutLen) {
            theMaxXofLen = pMaxOutLen;
            return this;
        }

        /**
         * Set the treeConfig.
         * @param pFanOut the fanOut (powers of two - 1-255).
         * @param pMaxDepth the maxDepth (2-255).
         * @param pLeafLen the leafLength (powers of two times outputLength - 1-255).
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

        @Override
        public SkeinXParameters build() {
            /* Build base parameters */
            final SkeinParameters myBaseParms = super.build();
            final SkeinXParameters myParams = new SkeinXParameters();

            /* Store base details */
            final Hashtable myBaseStore = myBaseParms.getParameters();
            final Hashtable myStore = myParams.getParameters();
            final Enumeration keys = myBaseStore.keys();
            while (keys.hasMoreElements())
            {
                Integer key = (Integer)keys.nextElement();
                myStore.put(key, myBaseStore.get(key));
            }

            /* Record XofDetails */
            myParams.theMaxXofLen = theMaxXofLen;

            /* Record tree details */
            myParams.theFanOut = theFanOut;
            myParams.theMaxDepth = theMaxDepth;
            myParams.theLeafLen = theLeafLen;
            return myParams;
        }
    }
}
