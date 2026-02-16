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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewBlakeXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewChaCha20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewElephantKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewISAPKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewRomulusKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewSalsa20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewSkeinXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewSparkleKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewVMPCKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyLengths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * StreamKey specification Builder.
 */

public class GordianCoreStreamKeySpecBuilder
        implements GordianNewStreamKeySpecBuilder {
    /**
     * The type.
     */
    private GordianNewStreamKeyType theType;

    /**
     * The subSpec.
     */
    private GordianNewStreamKeySubType theSubType;

    /**
     * The keyLength.
     */
    private GordianLength theKeyLength;

    @Override
    public GordianNewStreamKeySpecBuilder withType(final GordianNewStreamKeyType pType) {
        theType = pType;
        return this;
    }

    @Override
    public GordianNewStreamKeySpecBuilder withSubType(final GordianNewStreamKeySubType pSubType) {
        theSubType = pSubType;
        return this;
    }

    @Override
    public GordianNewStreamKeySpecBuilder withKeyLength(final GordianLength pKeyLength) {
        theKeyLength = pKeyLength;
        return this;
    }

    @Override
    public GordianNewStreamKeySpec build() {
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
                return GordianCoreStreamKeySubType.requiredSparkleKeyLength((GordianNewSparkleKey) theSubType);
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
    public static List<GordianNewStreamKeySpec> listAllPossibleStreamKeySpecs(final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianNewStreamKeySpec> myList = new ArrayList<>();

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
    private static List<GordianNewStreamKeySpec> listStreamSubKeys(final GordianCoreStreamKeyType pKeyType,
                                                                   final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianNewStreamKeySpec> myList = new ArrayList<>();

        /* Loop through the subKeyTypes */
        for (GordianNewStreamKeySubType mySubKeyType : listStreamSubKeys(pKeyType)) {
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
    private static List<GordianNewStreamKeySubType> listStreamSubKeys(final GordianCoreStreamKeyType pKeyType) {
        /* Switch on keyType */
        switch (pKeyType.getType()) {
            case SALSA20:
                return Arrays.asList(GordianNewSalsa20Key.values());
            case CHACHA20:
                return Arrays.asList(GordianNewChaCha20Key.values());
            case VMPC:
                return Arrays.asList(GordianNewVMPCKey.values());
            case SKEINXOF:
                return Arrays.asList(GordianNewSkeinXofKey.values());
            case BLAKE2XOF:
                return Arrays.asList(GordianNewBlakeXofKey.values());
            case ELEPHANT:
                return Arrays.asList(GordianNewElephantKey.values());
            case ISAP:
                return Arrays.asList(GordianNewISAPKey.values());
            case ROMULUS:
                return Arrays.asList(GordianNewRomulusKey.values());
            case SPARKLE:
                return Arrays.asList(GordianNewSparkleKey.values());
            default:
                return Collections.emptyList();
        }
    }
}
