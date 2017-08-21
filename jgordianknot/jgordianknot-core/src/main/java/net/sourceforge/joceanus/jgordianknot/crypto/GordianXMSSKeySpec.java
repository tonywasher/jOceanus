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
 * XMSS key Spec.
 */
public final class GordianXMSSKeySpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The key type.
     */
    private final GordianXMSSKeyType theKeyType;

    /**
     * The digest type.
     */
    private final GordianXMSSDigestType theDigestType;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pKeyType the keyType
     * @param pDigestType the digestType
     */
    private GordianXMSSKeySpec(final GordianXMSSKeyType pKeyType,
                               final GordianXMSSDigestType pDigestType) {
        theKeyType = pKeyType;
        theDigestType = pDigestType;
    }

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    public GordianXMSSKeyType getKeyType() {
        return theKeyType;
    }

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    public GordianXMSSDigestType getDigestType() {
        return theDigestType;
    }

    /**
     * Create XMSSSpec.
     * @param pDigestType the digestType
     * @return the SignatureSpec
     */
    public static GordianXMSSKeySpec xmss(final GordianXMSSDigestType pDigestType) {
        return new GordianXMSSKeySpec(GordianXMSSKeyType.XMSS, pDigestType);
    }

    /**
     * Create XMSSSpec.
     * @param pDigestType the digestType
     * @return the SignatureSpec
     */
    public static GordianXMSSKeySpec xmssmt(final GordianXMSSDigestType pDigestType) {
        return new GordianXMSSKeySpec(GordianXMSSKeyType.XMSSMT, pDigestType);
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = theKeyType.toString();
            theName += SEP + theDigestType.toString();
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

        /* Make sure that the object is a xmssSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target xmssSpec */
        final GordianXMSSKeySpec myThat = (GordianXMSSKeySpec) pThat;

        /* Check KeyType and digestType */
        return theKeyType == myThat.getKeyType()
               && theDigestType == myThat.getDigestType();
    }

    @Override
    public int hashCode() {
        final int hashCode = theKeyType.hashCode() << TethysDataConverter.BYTE_SHIFT;
        return hashCode + theDigestType.hashCode();
    }

    /**
     * XMSS keyTypes.
     */
    public enum GordianXMSSKeyType {
        /**
         * XMSS.
         */
        XMSS,

        /**
         * XMSS-MT.
         */
        XMSSMT;

        /**
         * Is this key XMSSMT?
         * @return true/false
         */
        public boolean isXMSSMT() {
            return this == XMSSMT;
        }
    }

    /**
     * XMSS digestTypes.
     */
    public enum GordianXMSSDigestType {
        /**
         * SHA256.
         */
        SHA256,

        /**
         * SHA512.
         */
        SHA512,

        /**
         * SHAKE128.
         */
        SHAKE128,

        /**
         * SHAKE256.
         */
        SHAKE256;
    }
}
