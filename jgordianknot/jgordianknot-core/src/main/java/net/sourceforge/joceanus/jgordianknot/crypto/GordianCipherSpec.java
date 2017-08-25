/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Cipher Specification.
 * @param <T> the keyType
 */
public abstract class GordianCipherSpec<T> {
    /**
     * KeyType.
     */
    private final T theKeyType;

    /**
     * Constructor.
     * @param pKeyType the keyType
     */
    protected GordianCipherSpec(final T pKeyType) {
        theKeyType = pKeyType;
    }

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    public T getKeyType() {
        return theKeyType;
    }

    @Override
    public String toString() {
        return theKeyType.toString();
    }

    /**
     * Obtain the cipherMode.
     * @return the mode
     */
    public abstract boolean needsIV();

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a CipherSpec */
        if (!(pThat instanceof GordianCipherSpec)) {
            return false;
        }

        /* Access the target cipherSpec */
        final GordianCipherSpec<?> myThat = (GordianCipherSpec<?>) pThat;

        /* Check KeyType */
        return theKeyType == myThat.getKeyType();
    }

    @Override
    public int hashCode() {
        return theKeyType.hashCode();
    }

    /**
     * The SymCipherSpec class.
     */
    public static class GordianSymCipherSpec
            extends GordianCipherSpec<GordianSymKeySpec> {
        /**
         * The Separator.
         */
        private static final String SEP = "-";

        /**
         * Cipher Mode.
         */
        private final GordianCipherMode theMode;

        /**
         * Cipher Padding.
         */
        private final GordianPadding thePadding;

        /**
         * The String name.
         */
        private String theName;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pMode the mode
         * @param pPadding the padding
         */
        protected GordianSymCipherSpec(final GordianSymKeySpec pKeySpec,
                                       final GordianCipherMode pMode,
                                       final GordianPadding pPadding) {
            super(pKeySpec);
            theMode = pMode;
            thePadding = pPadding;
        }

        /**
         * Create an ECB symKey cipherSpec.
         * @param pKeySpec the keySpec
         * @param pPadding the padding
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec ecb(final GordianSymKeySpec pKeySpec,
                                               final GordianPadding pPadding) {
            return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.ECB, pPadding);
        }

        /**
         * Create a CBC symKey cipherSpec.
         * @param pKeySpec the keySpec
         * @param pPadding the padding
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec cbc(final GordianSymKeySpec pKeySpec,
                                               final GordianPadding pPadding) {
            return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.CBC, pPadding);
        }

        /**
         * Create a CFB symKey cipherSpec.
         * @param pKeySpec the keySpec
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec cfb(final GordianSymKeySpec pKeySpec) {
            return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.CFB, GordianPadding.NONE);
        }

        /**
         * Create a OFB symKey cipherSpec.
         * @param pKeySpec the keySpec
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec ofb(final GordianSymKeySpec pKeySpec) {
            return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.OFB, GordianPadding.NONE);
        }

        /**
         * Create a SIC symKey cipherSpec.
         * @param pKeySpec the keySpec
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec sic(final GordianSymKeySpec pKeySpec) {
            return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.SIC, GordianPadding.NONE);
        }

        /**
         * Create a CCM symKey cipherSpec.
         * @param pKeySpec the keySpec
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec ccm(final GordianSymKeySpec pKeySpec) {
            return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.CCM, GordianPadding.NONE);
        }

        /**
         * Create a GCM symKey cipherSpec.
         * @param pKeySpec the keySpec
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec gcm(final GordianSymKeySpec pKeySpec) {
            return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.GCM, GordianPadding.NONE);
        }

        /**
         * Create an EAX symKey cipherSpec.
         * @param pKeySpec the keySpec
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec eax(final GordianSymKeySpec pKeySpec) {
            return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.EAX, GordianPadding.NONE);
        }

        /**
         * Create an OCB symKey cipherSpec.
         * @param pKeySpec the keySpec
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec ocb(final GordianSymKeySpec pKeySpec) {
            return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.OCB, GordianPadding.NONE);
        }

        /**
         * Obtain the cipherMode.
         * @return the mode
         */
        public GordianCipherMode getCipherMode() {
            return theMode;
        }

        /**
         * Obtain the padding.
         * @return the padding
         */
        public GordianPadding getPadding() {
            return thePadding;
        }

        @Override
        public boolean needsIV() {
            return theMode.needsIV();
        }

        /**
         * Obtain the blockLength.
         * @return the blockLength
         */
        public int getBlockLength() {
            return getKeyType().getBlockLength().getLength();
        }

        /**
         * Is this an AAD mode?
         * @return true/false
         */
        public boolean isAAD() {
            return theMode != null && theMode.isAAD();
        }

        /**
         * validate the cipherSpec.
         * @param isAAD should the cipherSpec be AAD?
         * @return true/false
         */
        public boolean validate(final boolean isAAD) {
            /* Reject null modes and wrong AAD modes */
            if (theMode == null
                || isAAD != theMode.isAAD()) {
                return false;
            }

            /* Determine whether we have a short block length */
            final int myLen = getKeyType().getBlockLength().getLength();
            final boolean shortBlock = myLen < GordianLength.LEN_128.getLength();
            final boolean stdBlock = myLen == GordianLength.LEN_128.getLength();

            /* Reject modes which do not allow short blocks */
            if (shortBlock && !theMode.allowShortBlock()) {
                return false;
            }

            /* Reject modes which do not allow non-standard blocks */
            if (!stdBlock && theMode.needsStdBlock()) {
                return false;
            }

            /* Reject bad padding */
            return theMode.hasPadding()
                                        ? thePadding != null
                                        : GordianPadding.NONE.equals(thePadding);
        }

        @Override
        public String toString() {
            /* If we have not yet loaded the name */
            if (theName == null) {
                /* Load the name */
                theName = super.toString();
                if (theMode != null) {
                    theName += SEP + theMode.toString();
                }
                if (thePadding != null && !GordianPadding.NONE.equals(thePadding)) {
                    theName += SEP + thePadding.toString();
                }
            }

            /* return the name */
            return theName;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Make sure that the object is a SymCipherSpec */
            if (pThat.getClass() != this.getClass()) {
                return false;
            }

            /* Access the target cipherSpec */
            final GordianSymCipherSpec myThat = (GordianSymCipherSpec) pThat;

            /* Check KeyType */
            if (!getKeyType().equals(myThat.getKeyType())) {
                return false;
            }

            /* Match subfields */
            return theMode == myThat.getCipherMode()
                   && thePadding == myThat.getPadding();
        }

        @Override
        public int hashCode() {
            int hashCode = super.hashCode() << TethysDataConverter.BYTE_SHIFT;
            if (theMode != null) {
                hashCode += theMode.ordinal() + 1;
                hashCode <<= TethysDataConverter.BYTE_SHIFT;
            }
            if (thePadding != null) {
                hashCode += thePadding.ordinal() + 1;
            }
            return hashCode;
        }
    }

    /**
     * The StreamCipherSpec class.
     */
    public static class GordianStreamCipherSpec
            extends GordianCipherSpec<GordianStreamKeyType> {
        /**
         * Constructor.
         * @param pKeyType the keyType
         */
        protected GordianStreamCipherSpec(final GordianStreamKeyType pKeyType) {
            super(pKeyType);
        }

        /**
         * Create a streamKey cipherSpec.
         * @param pKeyType the keyType
         * @return the cipherSpec
         */
        public static GordianStreamCipherSpec stream(final GordianStreamKeyType pKeyType) {
            return new GordianStreamCipherSpec(pKeyType);
        }

        @Override
        public boolean needsIV() {
            return getKeyType().getIVLength() > 0;
        }
    }
}
