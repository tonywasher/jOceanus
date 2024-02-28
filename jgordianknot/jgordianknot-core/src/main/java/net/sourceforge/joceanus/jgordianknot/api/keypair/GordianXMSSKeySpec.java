/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.keypair;

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
     * The height.
     */
    private final GordianXMSSHeight theHeight;

    /**
     * The layers.
     */
    private final GordianXMSSMTLayers theLayers;

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
     * @param pDigestType the digestType
     * @param pHeight the height
     */
    public GordianXMSSKeySpec(final GordianXMSSDigestType pDigestType,
                              final GordianXMSSHeight pHeight) {
        this(GordianXMSSKeyType.XMSS, pDigestType, pHeight, null);
    }

    /**
     * Constructor.
     * @param pDigestType the digestType
     * @param pHeight the height
     * @param pLayers the layers
     */
    public GordianXMSSKeySpec(final GordianXMSSDigestType pDigestType,
                              final GordianXMSSHeight pHeight,
                              final GordianXMSSMTLayers pLayers) {
        this(GordianXMSSKeyType.XMSSMT, pDigestType, pHeight, pLayers);
    }

    /**
     * Constructor.
     * @param pKeyType the keyType
     * @param pDigestType the digestType
     * @param pHeight the height
     * @param pLayers the layers
     */
    public GordianXMSSKeySpec(final GordianXMSSKeyType pKeyType,
                              final GordianXMSSDigestType pDigestType,
                              final GordianXMSSHeight pHeight,
                              final GordianXMSSMTLayers pLayers) {
        theKeyType = pKeyType;
        theDigestType = pDigestType;
        theHeight = pHeight;
        theLayers = pLayers;
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
     * Obtain the height.
     * @return the height
     */
    public GordianXMSSHeight getHeight() {
        return theHeight;
    }

    /**
     * Obtain the layers.
     * @return the layers
     */
    public GordianXMSSMTLayers getLayers() {
        return theLayers;
    }

    /**
     * Is the keySpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Is the keySpec high (height &gt; 15)?
     * @return true/false.
     */
    public boolean isHigh() {
        return isValid && theHeight.isHigh(theKeyType);
    }

    /**
     * Create XMSS keySpec.
     * @param pDigestType the digestType
     * @param pHeight the height
     * @return the keySpec
     */
    public static GordianXMSSKeySpec xmss(final GordianXMSSDigestType pDigestType,
                                          final GordianXMSSHeight pHeight) {
        return new GordianXMSSKeySpec(pDigestType, pHeight);
    }

    /**
     * Create XMSS keySpec.
     * @param pDigestType the digestType
     * @param pHeight the height
     * @param pLayers the layers
     * @return the keySpec
     */
    public static GordianXMSSKeySpec xmssmt(final GordianXMSSDigestType pDigestType,
                                            final GordianXMSSHeight pHeight,
                                            final GordianXMSSMTLayers pLayers) {
        return new GordianXMSSKeySpec(pDigestType, pHeight, pLayers);
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* Check that required elements are present */
        if (theKeyType == null || theDigestType == null || theHeight == null) {
            return false;
        }

        /* Check that the height is valid for the keyType */
        if (!theHeight.validForKeyType(theKeyType)) {
            return false;
        }

        /* Check layers is valid for keyType/height */
        return theKeyType == GordianXMSSKeyType.XMSS
               ? theLayers == null
               : theLayers != null && theHeight.hasValidLayers(theLayers);
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theKeyType.toString()
                            + SEP + theDigestType.toString()
                            + SEP + theHeight.toString();
                if (isMT()) {
                    theName += SEP + theLayers.toString();
                }

            }  else {
                /* Report invalid spec */
                theName = "InvalidXMSSKeySpec: " + theKeyType + ":" + theDigestType
                        + ":" + theHeight + ":" + theLayers;
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

        /* Check KeyType, digestType, height and layers */
        return theKeyType == myThat.getKeyType()
                && theDigestType == myThat.getDigestType()
                && theHeight == myThat.getHeight()
                && theLayers == myThat.getLayers();
    }

    @Override
    public int hashCode() {
        int hashCode = theKeyType.hashCode() << TethysDataConverter.BYTE_SHIFT;
        hashCode += theDigestType.hashCode();
        hashCode <<= TethysDataConverter.BYTE_SHIFT;
        hashCode += theHeight.hashCode();
        hashCode <<= TethysDataConverter.BYTE_SHIFT;
        if (theLayers != null) {
            hashCode += theLayers.hashCode();
        }
        return hashCode;
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
            mySpecs.addAll(listPossibleKeySpecs(myType));
        }

        /* Return the list */
        return mySpecs;
    }

    /**
     * Obtain a list of all possible specs.
     * @param pDigestType the digestType
     * @return the list
     */
    public static List<GordianXMSSKeySpec> listPossibleKeySpecs(final GordianXMSSDigestType pDigestType) {
        /* Create the list */
        final List<GordianXMSSKeySpec> mySpecs = new ArrayList<>();

        /* For all heights */
        for (final GordianXMSSHeight myHeight : GordianXMSSHeight.values()) {
            /* Add XMSS Spec if valid */
            if (myHeight.validForKeyType(GordianXMSSKeyType.XMSS)) {
                mySpecs.add(GordianXMSSKeySpec.xmss(pDigestType, myHeight));
            }

            /* Add XMSSMT Specs if valid */
            if (myHeight.validForKeyType(GordianXMSSKeyType.XMSSMT)) {
                /* For all heights */
                for (final GordianXMSSMTLayers myLayers : myHeight.getValidLayers()) {
                    mySpecs.add(GordianXMSSKeySpec.xmssmt(pDigestType, myHeight, myLayers));
                }
            }
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
                    return GordianDigestSpec.shake128(GordianLength.LEN_256);
                case SHAKE256:
                    return GordianDigestSpec.shake256(GordianLength.LEN_512);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    /**
     * XMSS Height.
     */
    public enum GordianXMSSHeight {
        /**
         * 10.
         */
        H10(10),

        /**
         * 16.
         */
        H16(16),

        /**
         * 20.
         */
        H20(20, new GordianXMSSMTLayers[] { GordianXMSSMTLayers.L2, GordianXMSSMTLayers.L4 }),

        /**
         * 40.
         */
        H40(40, new GordianXMSSMTLayers[] { GordianXMSSMTLayers.L2, GordianXMSSMTLayers.L4, GordianXMSSMTLayers.L8 }),

        /**
         * 12.
         */
        H60(12, new GordianXMSSMTLayers[] { GordianXMSSMTLayers.L3, GordianXMSSMTLayers.L6, GordianXMSSMTLayers.L12 });

        /**
         * The Height.
         */
        private final int theHeight;

        /**
         * The Layers.
         */
        private final GordianXMSSMTLayers[] theLayers;

        /**
         * Constructor.
         * @param pHeight the height
         */
        GordianXMSSHeight(final int pHeight) {
            this(pHeight, null);
        }

        /**
         * Constructor.
         * @param pHeight the height
         * @param pLayers the layers
         */
        GordianXMSSHeight(final int pHeight,
                          final GordianXMSSMTLayers[] pLayers) {
            theHeight = pHeight;
            theLayers = pLayers;
        }

        /**
         * Obtain the height.
         * @return the height
         */
        public int getHeight() {
            return theHeight;
        }


        /**
         * Is the height valid for the keyType.
         * @param pKeyType the keyType
         * @return true/false
         */
        boolean validForKeyType(final GordianXMSSKeyType pKeyType) {
            switch (this) {
                case H10:
                case H16:
                    return pKeyType == GordianXMSSKeyType.XMSS;
                case H40:
                case H60:
                    return pKeyType == GordianXMSSKeyType.XMSSMT;
                default:
                    return true;
            }
        }

        /**
         * Is the layers valid for the height.
         * @param pLayers the layers
         * @return true/false
         */
        boolean hasValidLayers(final GordianXMSSMTLayers pLayers) {
            if (theLayers != null) {
                for (GordianXMSSMTLayers myLayers : theLayers) {
                    if (myLayers == pLayers) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Obtain the valid XMSSMT layers.
         * @return the height
         */
        GordianXMSSMTLayers[] getValidLayers() {
            return theLayers;
        }
        /**
         * Is the parameter high?
         * @param pKeyType the keyTypoe
         * @return true/false.
         */
        public boolean isHigh(final GordianXMSSKeyType pKeyType) {
            switch (this) {
                case H16:
                case H20:
                    return pKeyType == GordianXMSSKeyType.XMSS;
                case H40:
                case H60:
                    return true;
                default:
                    return false;
            }
        }
    }

    /**
     * XMSSMT Layers.
     */
    public enum GordianXMSSMTLayers {
        /**
         * 2.
         */
        L2(2),

        /**
         * 3.
         */
        L3(3),

        /**
         * 4.
         */
        L4(4),

        /**
         * 6.
         */
        L6(6),

        /**
         * 40.
         */
        L8(8),

        /**
         * 12.
         */
        L12(12);

        /**
         * The layers.
         */
        private final int theLayers;

        /**
         * Constructor.
         * @param pLayers the layers
         */
        GordianXMSSMTLayers(final int pLayers) {
            theLayers = pLayers;
        }

        /**
         * Obtain the layers.
         * @return the layers
         */
        public int getLayers() {
            return theLayers;
        }
    }
}
