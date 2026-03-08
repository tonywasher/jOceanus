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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign;

import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairType;

import java.util.ArrayList;
import java.util.List;

/**
 * Signature Specification Builder.
 */
public final class GordianCoreSignatureSpecBuilder
        implements GordianSignatureSpecBuilder {
    /**
     * The digestSpec builder.
     */
    private final GordianDigestSpecBuilder theBuilder;

    /**
     * The keyPairType.
     */
    private GordianKeyPairType theKeyPairType;

    /**
     * The type.
     */
    private GordianSignatureType theSignatureType;

    /**
     * The subSpec.
     */
    private Object theSubSpec;

    /**
     * Constructor.
     */
    private GordianCoreSignatureSpecBuilder() {
        theBuilder = GordianCoreDigestSpecBuilder.newInstance();
    }

    /**
     * Obtain new instance.
     *
     * @return the new instance
     */
    public static GordianCoreSignatureSpecBuilder newInstance() {
        return new GordianCoreSignatureSpecBuilder();
    }

    @Override
    public GordianSignatureSpecBuilder withKeyPairType(final GordianKeyPairType pType) {
        theKeyPairType = pType;
        return this;
    }

    @Override
    public GordianSignatureSpecBuilder withSignatureType(final GordianSignatureType pType) {
        theSignatureType = pType;
        return this;
    }

    @Override
    public GordianSignatureSpecBuilder withDigestSpec(final GordianDigestSpec pDigest) {
        theSubSpec = pDigest;
        return this;
    }

    @Override
    public GordianSignatureSpecBuilder withSignatureSpecs(final List<GordianSignatureSpec> pSpecs) {
        theSubSpec = pSpecs;
        return this;
    }

    @Override
    public GordianDigestSpecBuilder usingDigestSpecBuilder() {
        return theBuilder;
    }

    @Override
    public GordianSignatureSpec build() {
        /* Handle defaults */
        theSignatureType = theSignatureType == null ? GordianSignatureType.NATIVE : theSignatureType;

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
    public static List<GordianSignatureSpec> listAllPossibleSpecs(final GordianKeyPairType pKeyPairType) {
        /* Access the list of possible digests */
        final GordianCoreKeyPairType myCoreType = GordianCoreKeyPairType.mapCoreType(pKeyPairType);
        final List<GordianSignatureSpec> mySignatures = new ArrayList<>();
        final List<GordianDigestSpec> myDigests = GordianCoreDigestSpecBuilder.listAllPossibleSpecs();

        /* For each supported signature */
        for (GordianCoreSignatureType mySignType : GordianCoreSignatureType.values()) {
            /* Skip if the signatureType is not valid */
            if (mySignType.isSupported(pKeyPairType)) {
                /* If we need null-digestSpec */
                if (myCoreType.useDigestForSignatures().canNotExist()) {
                    /* Add the signature */
                    mySignatures.add(new GordianCoreSignatureSpec(pKeyPairType, mySignType.getType(), null));
                }

                /* If we need digestSpec */
                if (myCoreType.useDigestForSignatures().canExist()) {
                    /* For each possible digestSpec */
                    for (GordianDigestSpec mySpec : myDigests) {
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
