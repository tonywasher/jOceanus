/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.impl.core.mac;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacFactory;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacParameters;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianRandomSource;
import io.github.tonywasher.joceanus.gordianknot.impl.core.key.GordianCoreKeyGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.mac.GordianCoreMacSpec;

/**
 * Core Mac parameters implementation.
 */
public class GordianCoreMacParameters {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * The macSpec.
     */
    private final GordianNewMacSpec theSpec;

    /**
     * The secureRandom.
     */
    private final GordianRandomSource theRandom;

    /**
     * The KeyGenerator.
     */
    private GordianCoreKeyGenerator<GordianNewMacSpec> theGenerator;

    /**
     * Key.
     */
    private GordianKey<GordianNewMacSpec> theKey;

    /**
     * InitialisationVector.
     */
    private byte[] theInitVector;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pMacSpec the CipherSpec
     */
    GordianCoreMacParameters(final GordianBaseFactory pFactory,
                             final GordianNewMacSpec pMacSpec) {
        theFactory = pFactory;
        theSpec = pMacSpec;
        theRandom = theFactory.getRandomSource();
    }

    /**
     * Obtain the key.
     *
     * @return the key
     */
    public GordianKey<GordianNewMacSpec> getKey() {
        return theKey;
    }

    /**
     * Obtain the initVector.
     *
     * @return the initVector
     */
    byte[] getInitVector() {
        return theInitVector;
    }

    /**
     * Process macParameters.
     *
     * @param pParams the mac parameters
     * @throws GordianException on error
     */
    void processParameters(final GordianMacParameters pParams) throws GordianException {
        /* Access the key details */
        theKey = pParams.getKey();
        theInitVector = obtainNonceFromParameters(pParams);
    }

    /**
     * Build a key from bytes.
     *
     * @param pKeyBytes the bytes to use
     * @return the key
     * @throws GordianException on error
     */
    GordianKey<GordianNewMacSpec> buildKeyFromBytes(final byte[] pKeyBytes) throws GordianException {
        /* Create generator if needed */
        if (theGenerator == null) {
            final GordianMacFactory myFactory = theFactory.getMacFactory();
            theGenerator = (GordianCoreKeyGenerator<GordianNewMacSpec>) myFactory.getKeyGenerator(theSpec);
        }

        /* Create the key */
        return theGenerator.buildKeyFromBytes(pKeyBytes);
    }

    /**
     * Obtain Nonce from MacParameters.
     *
     * @param pParams parameters
     * @return the nonce
     */
    private byte[] obtainNonceFromParameters(final GordianMacParameters pParams) {
        /* Access IV */
        byte[] myIV = pParams.getNonce();
        final int myIVLen = ((GordianCoreMacSpec) theSpec).getIVLen();

        /* If we need a random nonce */
        if (pParams.randomNonce() && myIVLen != 0) {
            /* Create a random IV */
            myIV = new byte[myIVLen];
            theRandom.getRandom().nextBytes(myIV);
        }

        /* return the IV */
        return myIV;
    }
}
