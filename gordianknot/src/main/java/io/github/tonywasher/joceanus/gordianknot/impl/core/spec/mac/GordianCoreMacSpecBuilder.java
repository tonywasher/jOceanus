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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSubSpec.GordianNewDigestState;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacSpec;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacType;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewSipHashType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSubSpec.GordianCoreDigestState;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestType;

import java.util.ArrayList;
import java.util.List;

/**
 * Mac Specification Builder.
 */
public class GordianCoreMacSpecBuilder
        implements GordianNewMacSpecBuilder {
    /**
     * The digestSpec builder.
     */
    private final GordianNewDigestSpecBuilder theBuilder;

    /**
     * The type.
     */
    private GordianNewMacType theType;

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
    public GordianCoreMacSpecBuilder() {
        theBuilder = new GordianCoreDigestSpecBuilder();
    }

    @Override
    public GordianNewMacSpecBuilder withType(final GordianNewMacType pType) {
        theType = pType;
        return this;
    }

    @Override
    public GordianNewMacSpecBuilder withKeyLength(final GordianLength pKeyLength) {
        theKeyLength = pKeyLength;
        return this;
    }

    @Override
    public GordianNewMacSpecBuilder withDigestSubSpec(final GordianNewDigestSpec pDigest) {
        theSubSpec = pDigest;
        return this;
    }

    @Override
    public GordianNewMacSpecBuilder withSymKeySubSpec(final GordianNewSymKeySpec pSymKey) {
        theSubSpec = pSymKey;
        return this;
    }

    @Override
    public GordianNewMacSpecBuilder withSipHashSubSpec(final GordianNewSipHashType pSipHash) {
        theSubSpec = pSipHash;
        return this;
    }

    @Override
    public GordianNewMacSpecBuilder withLengthSubSpec(final GordianLength pLength) {
        theSubSpec = pLength;
        return this;
    }

    @Override
    public GordianNewDigestSpecBuilder usingDigestSpecBuilder() {
        return theBuilder;
    }

    @Override
    public GordianNewMacSpec build() {
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
        if (theSubSpec instanceof GordianNewSipHashType) {
            return GordianLength.LEN_128;

            /* Handle symKeySpec */
        } else if (theSubSpec instanceof GordianNewDigestSpec myDigest) {
            return myDigest.getDigestLength();

            /* Handle symKeySpec */
        } else if (theSubSpec instanceof GordianNewSymKeySpec mySym) {
            return GordianNewMacType.POLY1305 == theType
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
    public static List<GordianNewMacSpec> listAllPossibleSpecs(final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianNewMacSpec> myList = new ArrayList<>();
        final GordianCoreMacSpecBuilder myBuilder = new GordianCoreMacSpecBuilder();
        final GordianCoreDigestSpecBuilder myDigestBuilder = new GordianCoreDigestSpecBuilder();

        /* For each digestSpec */
        for (final GordianNewDigestSpec mySpec : GordianCoreDigestSpecBuilder.listAllPossibleSpecs()) {
            /* Add the hMacSpec */
            myList.add(myBuilder.hMac(mySpec, pKeyLen));

            /* Add KMAC for digestType of SHAKE */
            if (GordianNewDigestType.SHAKE == mySpec.getDigestType()) {
                myList.add(myBuilder.kMac(pKeyLen, mySpec));
            }
        }

        /* For each SymKey */
        for (final GordianNewSymKeySpec mySymKeySpec : GordianCoreSymKeySpecBuilder.listAllPossibleSymKeySpecs(pKeyLen)) {
            /* Add gMac/cMac/cfbMac/cbcMac */
            myList.add(myBuilder.gMac(mySymKeySpec));
            myList.add(myBuilder.cMac(mySymKeySpec));
            myList.add(myBuilder.cbcMac(mySymKeySpec));
            myList.add(myBuilder.cfbMac(mySymKeySpec));

            /* Add kalynaMac for keyType of Kalyna */
            if (GordianNewSymKeyType.KALYNA == mySymKeySpec.getSymKeyType()) {
                myList.add(myBuilder.kalynaMac(mySymKeySpec));
            }
        }

        /* Only add poly1305 for 256bit keyLengths */
        if (GordianLength.LEN_256 == pKeyLen) {
            /* For each SymKey at 128 bits*/
            for (final GordianNewSymKeySpec mySymKeySpec : GordianCoreSymKeySpecBuilder.listAllPossibleSymKeySpecs(GordianLength.LEN_128)) {
                myList.add(myBuilder.poly1305Mac(mySymKeySpec));
            }

            /* Add raw poly1305 */
            myList.add(myBuilder.poly1305Mac());

            /* Add Blake3 macs */
            for (final GordianLength myLength : GordianCoreDigestType.getSupportedLengths(GordianNewDigestType.BLAKE3)) {
                myList.add(myBuilder.blake3Mac(myLength));
            }
        }

        /* Add kupynaMac */
        for (final GordianLength myLength : GordianCoreDigestType.getSupportedLengths(GordianNewDigestType.KUPYNA)) {
            myList.add(myBuilder.kupynaMac(pKeyLen, myLength));
        }

        /* Loop through states */
        for (final GordianCoreDigestState myState : GordianCoreDigestState.values()) {
            final GordianNewDigestState myBaseState = myState.getState();
            /* Add SkeinMacs */
            for (final GordianLength myLength : GordianCoreDigestType.getSupportedLengths(GordianNewDigestType.SKEIN)) {
                final GordianNewMacSpec mySkeinSpec = myBuilder.skeinMac(pKeyLen, myBaseState, myLength);
                if (mySkeinSpec.isValid()) {
                    myList.add(mySkeinSpec);
                }
            }
            final GordianNewMacSpec mySkeinSpec = myBuilder.skeinXMac(pKeyLen, myBaseState);
            if (mySkeinSpec.isValid()) {
                myList.add(mySkeinSpec);
            }

            /* Add blake2Macs */
            for (final GordianLength myLength : GordianDigestType.BLAKE2.getSupportedLengths()) {
                final GordianNewMacSpec myBlakeSpec = myBuilder.blake2Mac(pKeyLen, myDigestBuilder.blake2(myBaseState, myLength));
                if (myBlakeSpec.isValid()) {
                    myList.add(myBlakeSpec);
                }
            }

            final GordianNewMacSpec myBlakeSpec = myBuilder.blake2XMac(pKeyLen, myBaseState);
            if (myBlakeSpec.isValid()) {
                myList.add(myBlakeSpec);
            }
        }

        /* Add vmpcMac */
        myList.add(myBuilder.vmpcMac(pKeyLen));

        /* Add sipHash for 128bit keys */
        if (GordianLength.LEN_128 == pKeyLen) {
            for (final GordianNewSipHashType myType : GordianNewSipHashType.values()) {
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
