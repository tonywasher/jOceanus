/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * XMSS KeySpec.
 */
public class GordianCoreXMSSSpec
        implements GordianNewXMSSSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The key type.
     */
    private final GordianNewXMSSKeyType theKeyType;

    /**
     * The digest type.
     */
    private final GordianNewXMSSDigestType theDigestType;

    /**
     * The height.
     */
    private final GordianCoreXMSSHeight theHeight;

    /**
     * The layers.
     */
    private final GordianNewXMSSMTLayers theLayers;

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
     *
     * @param pDigestType the digestType
     * @param pHeight     the height
     */
    GordianCoreXMSSSpec(final GordianNewXMSSDigestType pDigestType,
                        final GordianNewXMSSHeight pHeight) {
        this(GordianNewXMSSKeyType.XMSS, pDigestType, pHeight, null);
    }

    /**
     * Constructor.
     *
     * @param pDigestType the digestType
     * @param pHeight     the height
     * @param pLayers     the layers
     */
    GordianCoreXMSSSpec(final GordianNewXMSSDigestType pDigestType,
                        final GordianNewXMSSHeight pHeight,
                        final GordianNewXMSSMTLayers pLayers) {
        this(GordianNewXMSSKeyType.XMSSMT, pDigestType, pHeight, pLayers);
    }

    /**
     * Constructor.
     *
     * @param pKeyType    the keyType
     * @param pDigestType the digestType
     * @param pHeight     the height
     * @param pLayers     the layers
     */
    private GordianCoreXMSSSpec(final GordianNewXMSSKeyType pKeyType,
                                final GordianNewXMSSDigestType pDigestType,
                                final GordianNewXMSSHeight pHeight,
                                final GordianNewXMSSMTLayers pLayers) {
        theKeyType = pKeyType;
        theDigestType = pDigestType;
        theHeight = GordianCoreXMSSHeight.mapCoreHeight(pHeight);
        theLayers = pLayers;
        isValid = checkValidity();
    }

    @Override
    public GordianNewXMSSKeyType getKeyType() {
        return theKeyType;
    }

    @Override
    public GordianNewXMSSDigestType getDigestType() {
        return theDigestType;
    }

    @Override
    public GordianNewXMSSHeight getHeight() {
        return theHeight.getHeight();
    }

    /**
     * Obtian the core height.
     *
     * @return the core height
     */
    public GordianCoreXMSSHeight getCoreHeight() {
        return theHeight;
    }

    @Override
    public GordianNewXMSSMTLayers getLayers() {
        return theLayers;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * Is this key MT?
     *
     * @return true/false
     */
    public boolean isMT() {
        return theKeyType == GordianNewXMSSKeyType.XMSSMT;
    }

    /**
     * Is the keySpec high (height &gt; 15)?
     *
     * @return true/false.
     */
    public boolean isHigh() {
        return isValid && theHeight.isHigh(theKeyType);
    }

    /**
     * Check spec validity.
     *
     * @return valid true/false
     */
    protected boolean checkValidity() {
        /* Check that required elements are present */
        if (theKeyType == null || theDigestType == null || theHeight == null) {
            return false;
        }

        /* check layers are valid for keyType */
        if (isMT() == (theLayers == null)) {
            return false;
        }

        /* Check that the height is valid for the keyType */
        return theHeight.validForKeyType(theKeyType)
                && getCoreHeight().hasValidLayers(theLayers);
    }

    /**
     * Obtain the required digestSpec.
     *
     * @return the digestSpec
     */
    public GordianNewDigestSpec getDigestSpec() {
        final GordianNewDigestSpecBuilder myBuilder = GordianCoreDigestSpecBuilder.newInstance();
        switch (theDigestType) {
            case SHA256:
                return myBuilder.sha2(GordianLength.LEN_256);
            case SHA512:
                return myBuilder.sha2(GordianLength.LEN_512);
            case SHAKE128:
                return myBuilder.shake128();
            case SHAKE256:
                return myBuilder.shake256();
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theKeyType.toString()
                        + SEP + theDigestType
                        + SEP + theHeight
                        + (isMT() ? SEP + theLayers : "");
            } else {
                /* Report invalid spec */
                theName = "InvalidXMSSKeySpec: " + theKeyType + ":" + theDigestType
                        + ":" + theHeight + (isMT() ? SEP + theLayers : "");
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

        /* Check KeyType, digestType and height */
        return pThat instanceof GordianCoreXMSSSpec myThat
                && theKeyType == myThat.getKeyType()
                && theDigestType == myThat.getDigestType()
                && Objects.equals(theHeight, myThat.getCoreHeight())
                && Objects.equals(theLayers, myThat.getLayers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeyType, theDigestType, theHeight, theLayers);
    }

    /**
     * Obtain a list of all possible specs.
     *
     * @return the list
     */
    public static List<GordianNewXMSSSpec> listAllPossibleSpecs() {
        /* Create the list */
        final List<GordianNewXMSSSpec> mySpecs = new ArrayList<>();

        /* Add the specs */
        for (final GordianNewXMSSDigestType myType : GordianNewXMSSDigestType.values()) {
            mySpecs.addAll(listPossibleSpecs(myType));
        }

        /* Return the list */
        return mySpecs;
    }

    /**
     * Obtain a list of all possible specs.
     *
     * @param pDigestType the digestType
     * @return the list
     */
    public static List<GordianNewXMSSSpec> listPossibleSpecs(final GordianNewXMSSDigestType pDigestType) {
        /* Create the list */
        final List<GordianNewXMSSSpec> mySpecs = new ArrayList<>();

        /* For all heights */
        for (final GordianCoreXMSSHeight myHeight : GordianCoreXMSSHeight.values()) {
            /* Add XMSS Spec if valid */
            if (myHeight.validForKeyType(GordianNewXMSSKeyType.XMSS)) {
                mySpecs.add(new GordianCoreXMSSSpec(pDigestType, myHeight.getHeight()));
            }

            /* Add XMSSMT Specs if valid */
            if (myHeight.validForKeyType(GordianNewXMSSKeyType.XMSSMT)) {
                /* For all layers */
                for (final GordianNewXMSSMTLayers myLayers : myHeight.getValidLayers()) {
                    mySpecs.add(new GordianCoreXMSSSpec(pDigestType, myHeight.getHeight(), myLayers));
                }
            }
        }

        /* Return the list */
        return mySpecs;
    }

    /**
     * Core Height.
     */
    public static final class GordianCoreXMSSHeight {
        /**
         * The specMap.
         */
        private static final Map<GordianNewXMSSHeight, GordianCoreXMSSHeight> HEIGHTMAP = newHeightMap();

        /**
         * The heightArray.
         */
        private static final GordianCoreXMSSHeight[] VALUES = HEIGHTMAP.values().toArray(new GordianCoreXMSSHeight[0]);

        /**
         * The Height.
         */
        private final GordianNewXMSSHeight theHeight;

        /**
         * The Layers.
         */
        private final GordianNewXMSSMTLayers[] theLayers;

        /**
         * Constructor.
         *
         * @param pHeight the height
         */
        private GordianCoreXMSSHeight(final GordianNewXMSSHeight pHeight) {
            theHeight = pHeight;
            theLayers = getValidLayers();
        }

        /**
         * Obtain the spec.
         *
         * @return the spec
         */
        public GordianNewXMSSHeight getHeight() {
            return theHeight;
        }

        @Override
        public String toString() {
            return theHeight.toString();
        }

        /**
         * Is the height valid for the keyType.
         *
         * @param pKeyType the keyType
         * @return true/false
         */
        boolean validForKeyType(final GordianNewXMSSKeyType pKeyType) {
            switch (theHeight) {
                case H10:
                case H16:
                    return pKeyType == GordianNewXMSSKeyType.XMSS;
                case H40:
                case H60:
                    return pKeyType == GordianNewXMSSKeyType.XMSSMT;
                default:
                    return true;
            }
        }

        /**
         * Is the layers valid for the height.
         *
         * @param pLayers the layers
         * @return true/false
         */
        boolean hasValidLayers(final GordianNewXMSSMTLayers pLayers) {
            if (pLayers != null) {
                for (GordianNewXMSSMTLayers myLayers : theLayers) {
                    if (myLayers == pLayers) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }

        /**
         * Obtain the valid XMSSMT layers.
         *
         * @return the height
         */
        private GordianNewXMSSMTLayers[] getValidLayers() {
            switch (theHeight) {
                case H10:
                case H16:
                    return new GordianNewXMSSMTLayers[]{};
                case H20:
                    return new GordianNewXMSSMTLayers[]{GordianNewXMSSMTLayers.L2, GordianNewXMSSMTLayers.L4};
                case H40:
                    return new GordianNewXMSSMTLayers[]{GordianNewXMSSMTLayers.L2, GordianNewXMSSMTLayers.L4, GordianNewXMSSMTLayers.L8};
                case H60:
                    return new GordianNewXMSSMTLayers[]{GordianNewXMSSMTLayers.L3, GordianNewXMSSMTLayers.L6, GordianNewXMSSMTLayers.L12};
                default:
                    throw new IllegalArgumentException();
            }
        }

        /**
         * Is the parameter high?
         *
         * @param pKeyType the keyTypoe
         * @return true/false.
         */
        public boolean isHigh(final GordianNewXMSSKeyType pKeyType) {
            switch (theHeight) {
                case H16:
                case H20:
                    return pKeyType == GordianNewXMSSKeyType.XMSS;
                case H40:
                case H60:
                    return true;
                default:
                    return false;
            }
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

            /* Check subFields */
            return pThat instanceof GordianCoreXMSSHeight myThat
                    && theHeight == myThat.getHeight();
        }

        @Override
        public int hashCode() {
            return theHeight.hashCode();
        }

        /**
         * Obtain the core height.
         *
         * @param pHeight the base height
         * @return the core height
         */
        public static GordianCoreXMSSHeight mapCoreHeight(final Object pHeight) {
            return pHeight instanceof GordianNewXMSSHeight myHeight ? HEIGHTMAP.get(myHeight) : null;
        }

        /**
         * Build the type map.
         *
         * @return the type map
         */
        private static Map<GordianNewXMSSHeight, GordianCoreXMSSHeight> newHeightMap() {
            final Map<GordianNewXMSSHeight, GordianCoreXMSSHeight> myMap = new EnumMap<>(GordianNewXMSSHeight.class);
            for (GordianNewXMSSHeight myHeight : GordianNewXMSSHeight.values()) {
                myMap.put(myHeight, new GordianCoreXMSSHeight(myHeight));
            }
            return myMap;
        }

        /**
         * Obtain the values.
         *
         * @return the values
         */
        public static GordianCoreXMSSHeight[] values() {
            return VALUES;
        }
    }
}
