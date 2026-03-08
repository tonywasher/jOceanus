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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianBlakeXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianChaCha20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianElephantKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianISAPKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianRomulusKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSalsa20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSkeinXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSparkleKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianVMPCKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyLengths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * StreamKey specification Builder.
 */

public final class GordianCoreStreamKeySpecBuilder
        implements GordianStreamKeySpecBuilder {
    /**
     * The type.
     */
    private GordianStreamKeyType theType;

    /**
     * The subSpec.
     */
    private GordianStreamKeySubType theSubType;

    /**
     * The keyLength.
     */
    private GordianLength theKeyLength;

    /**
     * Private constructor.
     */
    private GordianCoreStreamKeySpecBuilder() {
    }

    /**
     * Obtain new instance.
     *
     * @return the new instance
     */
    public static GordianCoreStreamKeySpecBuilder newInstance() {
        return new GordianCoreStreamKeySpecBuilder();
    }

    @Override
    public GordianStreamKeySpecBuilder withType(final GordianStreamKeyType pType) {
        theType = pType;
        return this;
    }

    @Override
    public GordianStreamKeySpecBuilder withSubType(final GordianStreamKeySubType pSubType) {
        theSubType = pSubType;
        return this;
    }

    @Override
    public GordianStreamKeySpecBuilder withKeyLength(final GordianLength pKeyLength) {
        theKeyLength = pKeyLength;
        return this;
    }

    @Override
    public GordianStreamKeySpec build() {
        /* Handle null type */
        if (theType == null) {
            throw new NullPointerException("streamKeyType is null");
        }

        /* Handle defaults */
        theSubType = theSubType == null ? GordianCoreStreamKeySubType.defaultSubKeyType(theType) : theSubType;
        theKeyLength = theKeyLength == null ? defaultKeyLength() : theKeyLength;

        /* Create spec, reset and return */
        final GordianCoreStreamKeySpec mySpec = new GordianCoreStreamKeySpec(theType, theSubType, theKeyLength);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theType = null;
        theSubType = null;
        theKeyLength = null;
    }

    /**
     * Default length.
     *
     * @return the default
     */
    private GordianLength defaultKeyLength() {
        /* Switch on keyType */
        switch (theType) {
            case GRAIN:
            case HC:
            case RABBIT:
            case SNOW3G:
            case SALSA20:
            case CHACHA20:
            case ZUC:
            case SOSEMANUK:
            case RC4:
            case VMPC:
            case ASCON:
            case ELEPHANT:
            case ISAP:
            case PHOTONBEETLE:
            case ROMULUS:
            case XOODYAK:
                return GordianLength.LEN_128;
            case ISAAC:
            case SKEINXOF:
            case BLAKE2XOF:
            case BLAKE3XOF:
                return GordianLength.LEN_256;
            case SPARKLE:
                return GordianCoreStreamKeySubType.requiredSparkleKeyLength((GordianSparkleKey) theSubType);
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * List all possible streamKeySpecs for the keyLength.
     *
     * @param pKeyLen the keyLength
     * @return the list
     */
    public static List<GordianStreamKeySpec> listAllPossibleStreamKeySpecs(final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianStreamKeySpec> myList = new ArrayList<>();

        /* Check that the keyLength is supported */
        if (!GordianKeyLengths.isSupportedLength(pKeyLen)) {
            return myList;
        }

        /* For each streamKey type */
        for (final GordianCoreStreamKeyType myType : GordianCoreStreamKeyType.values()) {
            /* if valid for keyLength */
            if (myType.validForKeyLength(pKeyLen)) {
                /* If we need a subType */
                if (myType.needsSubKeyType()) {
                    /* Add all valid subKeyTypes */
                    myList.addAll(listStreamSubKeys(myType, pKeyLen));

                    /* Else just add the spec */
                } else {
                    myList.add(new GordianCoreStreamKeySpec(myType.getType(), null, pKeyLen));
                }
            }
        }

        /* Return the list */
        return myList;
    }

    /**
     * List all possible subKeyTypes Specs.
     *
     * @param pKeyType the keyType
     * @param pKeyLen  the keyLength
     * @return the list
     */
    private static List<GordianStreamKeySpec> listStreamSubKeys(final GordianCoreStreamKeyType pKeyType,
                                                                final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianStreamKeySpec> myList = new ArrayList<>();

        /* Loop through the subKeyTypes */
        for (GordianStreamKeySubType mySubKeyType : listStreamSubKeys(pKeyType)) {
            /* Add valid subKeySpec */
            final GordianCoreStreamKeySpec mySpec = new GordianCoreStreamKeySpec(pKeyType.getType(), mySubKeyType, pKeyLen);
            if (mySpec.isValid()) {
                myList.add(mySpec);
            }
        }

        /* Return the list */
        return myList;
    }

    /**
     * List all possible subKeyTypes.
     *
     * @param pKeyType the keyType
     * @return the list
     */
    private static List<GordianStreamKeySubType> listStreamSubKeys(final GordianCoreStreamKeyType pKeyType) {
        /* Switch on keyType */
        switch (pKeyType.getType()) {
            case SALSA20:
                return Arrays.asList(GordianSalsa20Key.values());
            case CHACHA20:
                return Arrays.asList(GordianChaCha20Key.values());
            case VMPC:
                return Arrays.asList(GordianVMPCKey.values());
            case SKEINXOF:
                return Arrays.asList(GordianSkeinXofKey.values());
            case BLAKE2XOF:
                return Arrays.asList(GordianBlakeXofKey.values());
            case ELEPHANT:
                return Arrays.asList(GordianElephantKey.values());
            case ISAP:
                return Arrays.asList(GordianISAPKey.values());
            case ROMULUS:
                return Arrays.asList(GordianRomulusKey.values());
            case SPARKLE:
                return Arrays.asList(GordianSparkleKey.values());
            default:
                return Collections.emptyList();
        }
    }
}
