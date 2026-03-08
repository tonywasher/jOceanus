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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.mac;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSubSpec.GordianDigestState;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacSpec;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacType;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianSipHashType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSubSpec.GordianCoreDigestState;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestType;

import java.util.ArrayList;
import java.util.List;

/**
 * Mac Specification Builder.
 */
public final class GordianCoreMacSpecBuilder
        implements GordianMacSpecBuilder {
    /**
     * The digestSpec builder.
     */
    private final GordianDigestSpecBuilder theDigestBuilder;

    /**
     * The symKeySpec builder.
     */
    private final GordianSymKeySpecBuilder theSymKeyBuilder;

    /**
     * The type.
     */
    private GordianMacType theType;

    /**
     * The subSpec.
     */
    private GordianLength theKeyLength;

    /**
     * The subSpec.
     */
    private Object theSubSpec;

    /**
     * Constructor.
     */
    private GordianCoreMacSpecBuilder() {
        theDigestBuilder = GordianCoreDigestSpecBuilder.newInstance();
        theSymKeyBuilder = GordianCoreSymKeySpecBuilder.newInstance();
    }

    /**
     * Obtain new instance.
     *
     * @return the new instance
     */
    public static GordianCoreMacSpecBuilder newInstance() {
        return new GordianCoreMacSpecBuilder();
    }

    @Override
    public GordianMacSpecBuilder withType(final GordianMacType pType) {
        theType = pType;
        return this;
    }

    @Override
    public GordianMacSpecBuilder withKeyLength(final GordianLength pKeyLength) {
        theKeyLength = pKeyLength;
        return this;
    }

    @Override
    public GordianMacSpecBuilder withDigestSubSpec(final GordianDigestSpec pDigest) {
        theSubSpec = pDigest;
        return this;
    }

    @Override
    public GordianMacSpecBuilder withSymKeySubSpec(final GordianSymKeySpec pSymKey) {
        theSubSpec = pSymKey;
        return this;
    }

    @Override
    public GordianMacSpecBuilder withSipHashSubSpec(final GordianSipHashType pSipHash) {
        theSubSpec = pSipHash;
        return this;
    }

    @Override
    public GordianMacSpecBuilder withLengthSubSpec(final GordianLength pLength) {
        theSubSpec = pLength;
        return this;
    }

    @Override
    public GordianDigestSpecBuilder usingDigestSpecBuilder() {
        return theDigestBuilder;
    }

    @Override
    public GordianSymKeySpecBuilder usingSymKeySpecBuilder() {
        return theSymKeyBuilder;
    }

    @Override
    public GordianMacSpec build() {
        /* Handle defaults */
        theKeyLength = determineKeyLength(theKeyLength);

        /* Create spec, reset and return */
        final GordianCoreMacSpec mySpec = new GordianCoreMacSpec(theType, theKeyLength, theSubSpec);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theType = null;
        theSubSpec = null;
        theKeyLength = null;
    }

    /**
     * Determine keyLength.
     *
     * @param pKeyLength the proposed keyLength
     * @return the keyLength
     */
    private GordianLength determineKeyLength(final GordianLength pKeyLength) {
        /* Honour proposed keyLength */
        if (pKeyLength != null) {
            return pKeyLength;
        }

        /* Handle sipHashType */
        if (theSubSpec instanceof GordianSipHashType) {
            return GordianLength.LEN_128;

            /* Handle symKeySpec */
        } else if (theSubSpec instanceof GordianDigestSpec myDigest) {
            return myDigest.getDigestLength();

            /* Handle symKeySpec */
        } else if (theSubSpec instanceof GordianSymKeySpec mySym) {
            return GordianMacType.POLY1305 == theType
                    ? GordianLength.LEN_256
                    : mySym.getKeyLength();
        }

        /* Default to supplied length */
        return pKeyLength;
    }

    /**
     * List all possible macSpecs for a keyLength.
     *
     * @param pKeyLen the keyLength
     * @return the list
     */
    public static List<GordianMacSpec> listAllPossibleSpecs(final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianMacSpec> myList = new ArrayList<>();
        final GordianCoreMacSpecBuilder myBuilder = new GordianCoreMacSpecBuilder();
        final GordianCoreDigestSpecBuilder myDigestBuilder = GordianCoreDigestSpecBuilder.newInstance();

        /* For each digestSpec */
        for (final GordianDigestSpec mySpec : GordianCoreDigestSpecBuilder.listAllPossibleSpecs()) {
            /* Add the hMacSpec */
            myList.add(myBuilder.hMac(mySpec, pKeyLen));

            /* Add KMAC for digestType of SHAKE */
            if (GordianDigestType.SHAKE == mySpec.getDigestType()) {
                myList.add(myBuilder.kMac(pKeyLen, mySpec));
            }
        }

        /* For each SymKey */
        for (final GordianSymKeySpec mySymKeySpec : GordianCoreSymKeySpecBuilder.listAllPossibleSymKeySpecs(pKeyLen)) {
            /* Add gMac/cMac/cfbMac/cbcMac */
            myList.add(myBuilder.gMac(mySymKeySpec));
            myList.add(myBuilder.cMac(mySymKeySpec));
            myList.add(myBuilder.cbcMac(mySymKeySpec));
            myList.add(myBuilder.cfbMac(mySymKeySpec));

            /* Add kalynaMac for keyType of Kalyna */
            if (GordianSymKeyType.KALYNA == mySymKeySpec.getSymKeyType()) {
                myList.add(myBuilder.kalynaMac(mySymKeySpec));
            }
        }

        /* Only add poly1305 for 256bit keyLengths */
        if (GordianLength.LEN_256 == pKeyLen) {
            /* For each SymKey at 128 bits*/
            for (final GordianSymKeySpec mySymKeySpec : GordianCoreSymKeySpecBuilder.listAllPossibleSymKeySpecs(GordianLength.LEN_128)) {
                myList.add(myBuilder.poly1305Mac(mySymKeySpec));
            }

            /* Add raw poly1305 */
            myList.add(myBuilder.poly1305Mac());

            /* Add Blake3 macs */
            for (final GordianLength myLength : GordianCoreDigestType.getSupportedLengths(GordianDigestType.BLAKE3)) {
                myList.add(myBuilder.blake3Mac(myLength));
            }
        }

        /* Add kupynaMac */
        for (final GordianLength myLength : GordianCoreDigestType.getSupportedLengths(GordianDigestType.KUPYNA)) {
            myList.add(myBuilder.kupynaMac(pKeyLen, myLength));
        }

        /* Loop through states */
        for (final GordianCoreDigestState myState : GordianCoreDigestState.values()) {
            final GordianDigestState myBaseState = myState.getState();
            /* Add SkeinMacs */
            for (final GordianLength myLength : GordianCoreDigestType.getSupportedLengths(GordianDigestType.SKEIN)) {
                final GordianMacSpec mySkeinSpec = myBuilder.skeinMac(pKeyLen, myBaseState, myLength);
                if (mySkeinSpec.isValid()) {
                    myList.add(mySkeinSpec);
                }
            }
            final GordianMacSpec mySkeinSpec = myBuilder.skeinXMac(pKeyLen, myBaseState);
            if (mySkeinSpec.isValid()) {
                myList.add(mySkeinSpec);
            }

            /* Add blake2Macs */
            for (final GordianLength myLength : GordianCoreDigestType.getSupportedLengths(GordianDigestType.BLAKE2)) {
                final GordianMacSpec myBlakeSpec = myBuilder.blake2Mac(pKeyLen, myDigestBuilder.blake2(myBaseState, myLength));
                if (myBlakeSpec.isValid()) {
                    myList.add(myBlakeSpec);
                }
            }

            final GordianMacSpec myBlakeSpec = myBuilder.blake2XMac(pKeyLen, myBaseState);
            if (myBlakeSpec.isValid()) {
                myList.add(myBlakeSpec);
            }
        }

        /* Add vmpcMac */
        myList.add(myBuilder.vmpcMac(pKeyLen));

        /* Add sipHash for 128bit keys */
        if (GordianLength.LEN_128 == pKeyLen) {
            for (final GordianSipHashType myType : GordianSipHashType.values()) {
                myList.add(myBuilder.sipHash(myType));
            }
        }

        /* Add gostHash for 256bit keys */
        if (GordianLength.LEN_256 == pKeyLen) {
            myList.add(myBuilder.gostMac());
        }

        /* Add zucMac */
        if (GordianLength.LEN_128 == pKeyLen) {
            myList.add(myBuilder.zucMac(pKeyLen, GordianLength.LEN_32));
        } else if (GordianLength.LEN_256 == pKeyLen) {
            myList.add(myBuilder.zucMac(pKeyLen, GordianLength.LEN_32));
            myList.add(myBuilder.zucMac(pKeyLen, GordianLength.LEN_64));
            myList.add(myBuilder.zucMac(pKeyLen, GordianLength.LEN_128));
        }

        /* Return the list */
        return myList;
    }
}
