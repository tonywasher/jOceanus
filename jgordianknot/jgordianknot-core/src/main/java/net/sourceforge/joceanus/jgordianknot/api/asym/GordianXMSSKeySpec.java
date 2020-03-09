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
package net.sourceforge.joceanus.jgordianknot.api.asym;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * XMSS KeySpec.
 */
public class GordianXMSSKeySpec {
    /**
     * Default height for XMSS key.
     */
    public static final int DEFAULT_HEIGHT = 6;

    /**
     * Default layers for XMSS key.
     */
    public static final int DEFAULT_LAYERS = 3;

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
     * The Validity.
     */
    private final boolean isValid;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pKeyType the keyType
     * @param pDigestType the digestType
     */
    public GordianXMSSKeySpec(final GordianXMSSKeyType pKeyType,
                              final GordianXMSSDigestType pDigestType) {
        theKeyType = pKeyType;
        theDigestType = pDigestType;
        isValid = checkValidity();
    }

    /**
     * Is this key MT?
     * @return true/false
     */
    public boolean isMT() {
        return theKeyType.isMT();
    }

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    public GordianXMSSKeyType getKeyType() {
        return theKeyType;
    }

    /**
     * Obtain the digestType.
     * @return the digestType
     */
    public GordianXMSSDigestType getDigestType() {
        return theDigestType;
    }

    /**
     * Is the keySpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Create XMSS keySpec.
     * @param pDigestType the digestType
     * @return the keySpec
     */
    public static GordianXMSSKeySpec xmss(final GordianXMSSDigestType pDigestType) {
        return new GordianXMSSKeySpec(GordianXMSSKeyType.XMSS, pDigestType);
    }

    /**
     * Create XMSS keySpec.
     * @param pDigestType the digestType
     * @return the keySpec
     */
    public static GordianXMSSKeySpec xmssmt(final GordianXMSSDigestType pDigestType) {
        return new GordianXMSSKeySpec(GordianXMSSKeyType.XMSSMT, pDigestType);
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* Both elements must be present */
        return theKeyType != null && theDigestType != null;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theKeyType.toString() + SEP + theDigestType.toString();

            }  else {
                /* Report invalid spec */
                theName = "InvalidXMSSKeySpec: " + theKeyType + ":" + theDigestType;
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

        /* Make sure that the object is an XMSSSpec */
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
        return theDigestType == null
               ? hashCode
               : hashCode + theDigestType.hashCode();
    }

    /**
     * Obtain a list of all possible specs.
     * @return the list
     */
    public static List<GordianXMSSKeySpec> listPossibleKeySpecs() {
        /* Create the list */
        final List<GordianXMSSKeySpec> mySpecs = new ArrayList<>();

        /* Add the specs */
        for (final GordianXMSSDigestType myType : GordianXMSSDigestType.values()) {
            mySpecs.add(GordianXMSSKeySpec.xmss(myType));
            mySpecs.add(GordianXMSSKeySpec.xmssmt(myType));
        }

        /* Return the list */
        return mySpecs;
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
         * XMSSMT.
         */
        XMSSMT;

        /**
         * Is this key MT?
         * @return true/false
         */
        public boolean isMT() {
            return this == XMSSMT;
        }

        @Override
        public String toString() {
            return this == XMSSMT ? "XMSS^MT" : "XMSS";
        }
    }

    /**
     * XMSS digestType.
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

        /**
         * Obtain the required digestSpec.
         * @return the digestSpec
         */
        public GordianDigestSpec getDigestSpec() {
            switch (this) {
                case SHA256:
                    return GordianDigestSpec.sha2(GordianLength.LEN_256);
                case SHA512:
                    return GordianDigestSpec.sha2(GordianLength.LEN_512);
                case SHAKE128:
                    return GordianDigestSpec.shakeAlt(GordianLength.LEN_256);
                case SHAKE256:
                    return GordianDigestSpec.shake(GordianLength.LEN_512);
                default:
                    throw new IllegalStateException();
            }
        }
    }
}
