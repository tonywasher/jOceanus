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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyLengths;

import java.util.ArrayList;
import java.util.List;

/**
 * SymKey specification Builder.
 */
public final class GordianCoreSymKeySpecBuilder
        implements GordianNewSymKeySpecBuilder {
    /**
     * The type.
     */
    private GordianNewSymKeyType theType;

    /**
     * The subSpec.
     */
    private GordianLength theBlockLength;

    /**
     * The keyLength.
     */
    private GordianLength theKeyLength;

    /**
     * Private constructor.
     */
    private GordianCoreSymKeySpecBuilder() {
    }

    /**
     * Obtain new instance.
     *
     * @return the new instance
     */
    public static GordianCoreSymKeySpecBuilder newInstance() {
        return new GordianCoreSymKeySpecBuilder();
    }

    @Override
    public GordianNewSymKeySpecBuilder withType(final GordianNewSymKeyType pType) {
        theType = pType;
        return this;
    }

    @Override
    public GordianNewSymKeySpecBuilder withBlockLength(final GordianLength pBlockLength) {
        theBlockLength = pBlockLength;
        return this;
    }

    @Override
    public GordianNewSymKeySpecBuilder withKeyLength(final GordianLength pKeyLength) {
        theKeyLength = pKeyLength;
        return this;
    }

    @Override
    public GordianNewSymKeySpec build() {
        /* Handle null type */
        if (theType == null) {
            throw new NullPointerException("symKeyType is null");
        }

        /* Handle defaults */
        theKeyLength = theKeyLength == null ? GordianCoreSymKeyType.defaultKeyLengthForSymKeyType(theType) : theKeyLength;
        theBlockLength = theBlockLength == null
                ? GordianCoreSymKeyType.defaultBlockLengthForSymKeyTypeAndKeyLength(theType, theKeyLength) : theBlockLength;

        /* Create spec, reset and return */
        final GordianCoreSymKeySpec mySpec = new GordianCoreSymKeySpec(theType, theBlockLength, theKeyLength);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theType = null;
        theBlockLength = null;
        theKeyLength = null;
    }

    /**
     * Obtain all possible symKeySpecs for the given keyLength.
     *
     * @param pKeyLen the keyLength
     * @return the list
     */
    public static List<GordianNewSymKeySpec> listAllPossibleSymKeySpecs(final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianNewSymKeySpec> myList = new ArrayList<>();

        /* Check that the keyLength is supported */
        if (!GordianKeyLengths.isSupportedLength(pKeyLen)) {
            return myList;
        }

        /* For each symKey type */
        for (final GordianCoreSymKeyType myType : GordianCoreSymKeyType.values()) {
            /* For each supported block length */
            for (final GordianLength myBlkLen : myType.getSupportedBlockLengths()) {
                /* Add spec if valid for blkLen and keyLen */
                if (myType.validBlockAndKeyLengths(myBlkLen, pKeyLen)) {
                    myList.add(new GordianCoreSymKeySpec(myType.getType(), myBlkLen, pKeyLen));
                }
            }
        }

        /* Return the list */
        return myList;
    }
}
