package net.sourceforge.joceanus.gordianknot.impl.ext.params;

import org.bouncycastle.crypto.params.SkeinParameters;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

/**
 * Extended Skein Parameters.
 */
public class GordianSkeinParameters
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
     * A builder for {@link GordianSkeinParameters}.
     */
    public static class Builder
            extends SkeinParameters.Builder {
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
        public Builder set(final int type,
                           final byte[] value) {
            return (Builder) super.set(type, value);
        }

        @Override
        public Builder setKey(final byte[] key) {
            return (Builder) super.setKey(key);
        }

        @Override
        public Builder setPersonalisation(final byte[] personalisation) {
            return (Builder) super.setPersonalisation(personalisation);
        }

        @Override
        public Builder setPersonalisation(final Date date,
                                          final String emailAddress,
                                          final String distinguisher) {
            return (Builder) super.setPersonalisation(date, emailAddress, distinguisher);
        }

        @Override
        public Builder setPersonalisation(final Date date,
                                          final Locale dateLocale,
                                          final String emailAddress,
                                          final String distinguisher) {
            return (Builder) super.setPersonalisation(date, dateLocale, emailAddress, distinguisher);
        }

        @Override
        public Builder setPublicKey(final byte[] publicKey) {
            return (Builder) super.setPublicKey(publicKey);
        }

        @Override
        public Builder setKeyIdentifier(final byte[] keyId) {
            return (Builder) super.setKeyIdentifier(keyId);
        }

        @Override
        public Builder setNonce(final byte[] nonce) {
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
        @SuppressWarnings("unchecked")
        public GordianSkeinParameters build() {
            /* Build base parameters */
            final SkeinParameters myBaseParms = super.build();
            final GordianSkeinParameters myParams = new GordianSkeinParameters();

            /* Store base details */
            final Hashtable<Integer, Object> myBaseStore = (Hashtable<Integer, Object>) myBaseParms.getParameters();
            final Hashtable<Integer, Object> myStore = (Hashtable<Integer, Object>) myParams.getParameters();
            final Enumeration<Integer> keys = myBaseStore.keys();
            while (keys.hasMoreElements()) {
                final Integer key = keys.nextElement();
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
