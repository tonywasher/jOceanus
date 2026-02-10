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

package io.github.tonywasher.joceanus.gordianknot.impl.core.encrypt.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianNewEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianNewEncryptorSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianNewSM2EncryptionSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianNewSM2EncryptionType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.spec.GordianCoreDigestSpecBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Asymmetric Encryption Specification Builder.
 */
public class GordianCoreEncryptorSpecBuilder
        implements GordianNewEncryptorSpecBuilder {
    /**
     * The keyPairType.
     */
    private GordianNewKeyPairType theKeyPairType;

    /**
     * The subSpec.
     */
    private Object theSubSpec;

    @Override
    public GordianNewEncryptorSpecBuilder withKeyPairType(final GordianNewKeyPairType pType) {
        theKeyPairType = pType;
        return this;
    }

    @Override
    public GordianNewEncryptorSpecBuilder withDigestSpec(final GordianNewDigestSpec pDigestSpec) {
        theSubSpec = pDigestSpec;
        return this;
    }

    @Override
    public GordianNewEncryptorSpecBuilder withSM2EncryptionSpec(final GordianNewSM2EncryptionType pType,
                                                                final GordianNewDigestSpec pDigestSpec) {
        theSubSpec = new GordianCoreSM2EncryptionSpec(pType, pDigestSpec);
        return this;
    }

    @Override
    public GordianNewEncryptorSpecBuilder withEncryptorSpecs(final List<GordianNewEncryptorSpec> pSpecs) {
        theSubSpec = pSpecs;
        return this;
    }

    @Override
    public GordianNewEncryptorSpec build() {
        /* Build spec and return it */
        final GordianCoreEncryptorSpec mySpec = new GordianCoreEncryptorSpec(theKeyPairType, theSubSpec);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theKeyPairType = null;
        theSubSpec = null;
    }

    /**
     * List all possible encryptorSpecs for a keyPairType.
     *
     * @param pKeyPairType the keyPairType
     * @return the list
     */
    public static List<GordianNewEncryptorSpec> listAllPossibleSpecs(final GordianNewKeyPairType pKeyPairType) {
        /* Create list */
        final List<GordianNewEncryptorSpec> myEncryptors = new ArrayList<>();
        final GordianCoreEncryptorSpecBuilder myBuilder = new GordianCoreEncryptorSpecBuilder();
        final GordianCoreDigestSpecBuilder myDigestBuilder = new GordianCoreDigestSpecBuilder();

        /* Switch on keyPairType */
        switch (pKeyPairType) {
            case RSA:
                myEncryptors.add(myBuilder.rsa(myDigestBuilder.sha2(GordianLength.LEN_224)));
                myEncryptors.add(myBuilder.rsa(myDigestBuilder.sha2(GordianLength.LEN_256)));
                myEncryptors.add(myBuilder.rsa(myDigestBuilder.sha2(GordianLength.LEN_384)));
                myEncryptors.add(myBuilder.rsa(myDigestBuilder.sha2(GordianLength.LEN_512)));
                break;
            case ELGAMAL:
                myEncryptors.add(myBuilder.elGamal(myDigestBuilder.sha2(GordianLength.LEN_224)));
                myEncryptors.add(myBuilder.elGamal(myDigestBuilder.sha2(GordianLength.LEN_256)));
                myEncryptors.add(myBuilder.elGamal(myDigestBuilder.sha2(GordianLength.LEN_384)));
                myEncryptors.add(myBuilder.elGamal(myDigestBuilder.sha2(GordianLength.LEN_512)));
                break;
            case EC:
            case SM2:
            case GOST2012:
                /* Add EC-ElGamal */
                myEncryptors.add(myBuilder.ec());

                /* Loop through the encryptionSpecs */
                for (GordianNewSM2EncryptionSpec mySpec : listPossibleSM2Specs()) {
                    myEncryptors.add(new GordianCoreEncryptorSpec(pKeyPairType, mySpec));
                }
                break;
            default:
                break;
        }

        /* Return the list */
        return myEncryptors;
    }

    /**
     * Obtain a list of all possible encryptionSpecs.
     *
     * @return the list
     */
    private static List<GordianNewSM2EncryptionSpec> listPossibleSM2Specs() {
        /* Create list */
        final List<GordianNewSM2EncryptionSpec> mySpecs = new ArrayList<>();
        final GordianCoreDigestSpecBuilder myBuilder = new GordianCoreDigestSpecBuilder();

        /* Loop through the encryptionTypes */
        for (GordianNewSM2EncryptionType myType : GordianNewSM2EncryptionType.values()) {
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.sm3()));
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.sha2(GordianLength.LEN_224)));
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.sha2(GordianLength.LEN_256)));
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.sha2(GordianLength.LEN_384)));
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.sha2(GordianLength.LEN_512)));
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.blake2s(GordianLength.LEN_256)));
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.blake2b(GordianLength.LEN_512)));
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.whirlpool()));
        }

        /* Return the list */
        return mySpecs;
    }
}
