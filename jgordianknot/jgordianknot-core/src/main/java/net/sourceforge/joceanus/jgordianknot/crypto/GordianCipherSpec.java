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
        GordianCipherSpec<?> myThat = (GordianCipherSpec<?>) pThat;

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
            extends GordianCipherSpec<GordianSymKeyType> {
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
         * @param pKeyType the keyType
         * @param pMode the mode
         * @param pPadding the padding
         */
        protected GordianSymCipherSpec(final GordianSymKeyType pKeyType,
                                       final GordianCipherMode pMode,
                                       final GordianPadding pPadding) {
            super(pKeyType);
            theMode = pMode;
            thePadding = pPadding;
        }

        /**
         * Create an ECB symKey cipherSpec.
         * @param pKeyType the keyType
         * @param pPadding the padding
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec ecb(final GordianSymKeyType pKeyType,
                                               final GordianPadding pPadding) {
            return new GordianSymCipherSpec(pKeyType, GordianCipherMode.ECB, pPadding);
        }

        /**
         * Create a CBC symKey cipherSpec.
         * @param pKeyType the keyType
         * @param pPadding the padding
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec cbc(final GordianSymKeyType pKeyType,
                                               final GordianPadding pPadding) {
            return new GordianSymCipherSpec(pKeyType, GordianCipherMode.CBC, pPadding);
        }

        /**
         * Create a CFB symKey cipherSpec.
         * @param pKeyType the keyType
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec cfb(final GordianSymKeyType pKeyType) {
            return new GordianSymCipherSpec(pKeyType, GordianCipherMode.CFB, GordianPadding.NONE);
        }

        /**
         * Create a OFB symKey cipherSpec.
         * @param pKeyType the keyType
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec ofb(final GordianSymKeyType pKeyType) {
            return new GordianSymCipherSpec(pKeyType, GordianCipherMode.OFB, GordianPadding.NONE);
        }

        /**
         * Create a SIC symKey cipherSpec.
         * @param pKeyType the keyType
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec sic(final GordianSymKeyType pKeyType) {
            return new GordianSymCipherSpec(pKeyType, GordianCipherMode.SIC, GordianPadding.NONE);
        }

        /**
         * Create a CCM symKey cipherSpec.
         * @param pKeyType the keyType
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec ccm(final GordianSymKeyType pKeyType) {
            return new GordianSymCipherSpec(pKeyType, GordianCipherMode.CCM, GordianPadding.NONE);
        }

        /**
         * Create a GCM symKey cipherSpec.
         * @param pKeyType the keyType
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec gcm(final GordianSymKeyType pKeyType) {
            return new GordianSymCipherSpec(pKeyType, GordianCipherMode.GCM, GordianPadding.NONE);
        }

        /**
         * Create an EAX symKey cipherSpec.
         * @param pKeyType the keyType
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec eax(final GordianSymKeyType pKeyType) {
            return new GordianSymCipherSpec(pKeyType, GordianCipherMode.EAX, GordianPadding.NONE);
        }

        /**
         * Create an OCB symKey cipherSpec.
         * @param pKeyType the keyType
         * @return the cipherSpec
         */
        public static GordianSymCipherSpec ocb(final GordianSymKeyType pKeyType) {
            return new GordianSymCipherSpec(pKeyType, GordianCipherMode.OCB, GordianPadding.NONE);
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
            return theMode != null
                   && isAAD == isAAD()
                   && theMode.hasPadding()
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
                if (thePadding != null) {
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
            GordianSymCipherSpec myThat = (GordianSymCipherSpec) pThat;

            /* Check KeyType */
            if (getKeyType() != myThat.getKeyType()) {
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
