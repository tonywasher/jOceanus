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

package io.github.tonywasher.joceanus.gordianknot.impl.core.sign.spec;

import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianNewSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianNewSignatureSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianNewSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.spec.GordianCoreDigestSpecBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Signature Specification Builder.
 */
public class GordianCoreSignatureSpecBuilder
        implements GordianNewSignatureSpecBuilder {
    /**
     * The digestSpec builder.
     */
    private final GordianNewDigestSpecBuilder theBuilder;

    /**
     * The keyPairType.
     */
    private GordianKeyPairType theKeyPairType;

    /**
     * The type.
     */
    private GordianNewSignatureType theSignatureType;

    /**
     * The subSpec.
     */
    private Object theSubSpec;

    /**
     * Constructor.
     */
    public GordianCoreSignatureSpecBuilder() {
        theBuilder = new GordianCoreDigestSpecBuilder();
    }

    @Override
    public GordianNewSignatureSpecBuilder withKeyPairType(final GordianKeyPairType pType) {
        theKeyPairType = pType;
        return this;
    }

    @Override
    public GordianNewSignatureSpecBuilder withSignatureType(final GordianNewSignatureType pType) {
        theSignatureType = pType;
        return this;
    }

    @Override
    public GordianNewSignatureSpecBuilder withDigestSpec(final GordianNewDigestSpec pDigest) {
        theSubSpec = pDigest;
        return this;
    }

    @Override
    public GordianNewSignatureSpecBuilder withSignatureSpecs(final List<GordianNewSignatureSpec> pSpecs) {
        theSubSpec = pSpecs;
        return this;
    }

    @Override
    public GordianNewDigestSpecBuilder usingDigestSpecBuilder() {
        return theBuilder;
    }

    @Override
    public GordianNewSignatureSpec build() {
        /* Handle defaults */
        theSignatureType = theSignatureType == null ? GordianNewSignatureType.NATIVE : theSignatureType;

        /* Create spec, reset and return */
        final GordianCoreSignatureSpec mySpec = new GordianCoreSignatureSpec(theKeyPairType, theSignatureType, theSubSpec);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theKeyPairType = null;
        theSignatureType = null;
        theSubSpec = null;
    }

    /**
     * List all possible encryptorSpecs for a keyPairType.
     *
     * @param pKeyPairType the keyPairType
     * @return the list
     */
    public static List<GordianNewSignatureSpec> listAllPossibleSpecs(final GordianKeyPairType pKeyPairType) {
        /* Access the list of possible digests */
        final List<GordianNewSignatureSpec> mySignatures = new ArrayList<>();
        final List<GordianNewDigestSpec> myDigests = GordianCoreDigestSpecBuilder.listAllPossibleSpecs();

        /* For each supported signature */
        for (GordianCoreSignatureType mySignType : GordianCoreSignatureType.values()) {
            /* Skip if the signatureType is not valid */
            if (mySignType.isSupported(pKeyPairType)) {
                /* If we need null-digestSpec */
                if (pKeyPairType.useDigestForSignatures().canNotExist()) {
                    /* Add the signature */
                    mySignatures.add(new GordianCoreSignatureSpec(pKeyPairType, mySignType.getType(), null));
                }

                /* If we need digestSpec */
                if (pKeyPairType.useDigestForSignatures().canExist()) {
                    /* For each possible digestSpec */
                    for (GordianNewDigestSpec mySpec : myDigests) {
                        /* Add the signature */
                        mySignatures.add(new GordianCoreSignatureSpec(pKeyPairType, mySignType.getType(), mySpec));
                    }
                }
            }
        }

        /* Return the list */
        return mySignatures;
    }
}
