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

package io.github.tonywasher.joceanus.gordianknot.impl.core.mac;

import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacParams;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacParamsBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacSpec;
import org.bouncycastle.util.Arrays;

/**
 * Core MacParams Builder.
 */
public final class GordianCoreMacParamsBuilder
        implements GordianMacParamsBuilder {
    /**
     * The Key.
     */
    private GordianKey<GordianMacSpec> theKey;

    /**
     * The Nonce.
     */
    private byte[] theNonce;

    /**
     * Random Nonce requested?
     */
    private boolean randomNonce;

    /**
     * Personalisation.
     */
    private byte[] thePersonal;

    /**
     * Output length.
     */
    private long theOutLen;

    /**
     * The fanOut.
     */
    private short theFanOut = 1;

    /**
     * The maxDepth.
     */
    private short theMaxDepth = 1;

    /**
     * The leafLength.
     */
    private int theLeafLen;

    /**
     * Constructor.
     */
    private GordianCoreMacParamsBuilder() {
    }

    /**
     * Create new MacParamsBuilder.
     *
     * @return the Builder
     */
    public static GordianMacParamsBuilder newInstance() {
        return new GordianCoreMacParamsBuilder();
    }

    /**
     * Generate keyOnly Parameters.
     *
     * @param pKey the key
     * @return the macParameters
     */
    public GordianMacParams key(final GordianKey<GordianMacSpec> pKey) {
        final GordianCoreMacParams myParams = new GordianCoreMacParams();
        myParams.setKey(pKey);
        return myParams;
    }

    /**
     * Obtain keyAndNonce Parameters.
     *
     * @param pKey   the key
     * @param pNonce the nonce
     * @return the macParameters
     */
    public GordianMacParams keyAndNonce(final GordianKey<GordianMacSpec> pKey,
                                        final byte[] pNonce) {
        final GordianCoreMacParams myParams = new GordianCoreMacParams();
        myParams.setKey(pKey);
        myParams.setNonce(pNonce);
        return myParams;
    }

    /**
     * Obtain keyAndRandomNonce Parameters.
     *
     * @param pKey the key
     * @return the macParameters
     */
    public GordianMacParams keyWithRandomNonce(final GordianKey<GordianMacSpec> pKey) {
        final GordianCoreMacParams myParams = new GordianCoreMacParams();
        myParams.setKey(pKey);
        myParams.setRandomNonce();
        return myParams;
    }

    @Override
    public GordianMacParamsBuilder setKey(final GordianKey<GordianMacSpec> pKey) {
        theKey = pKey;
        return this;
    }

    @Override
    public GordianMacParamsBuilder setNonce(final byte[] pNonce) {
        theNonce = Arrays.clone(pNonce);
        randomNonce = false;
        return this;
    }

    @Override
    public GordianMacParamsBuilder withRandomNonce() {
        theNonce = null;
        randomNonce = true;
        return this;
    }

    @Override
    public GordianMacParamsBuilder setPersonalisation(final byte[] pPersonal) {
        thePersonal = Arrays.clone(pPersonal);
        return this;
    }

    @Override
    public GordianMacParamsBuilder setOutputLength(final long pOutLen) {
        theOutLen = pOutLen;
        return this;
    }

    @Override
    public GordianMacParamsBuilder setTreeConfig(final int pFanOut,
                                                 final int pMaxDepth,
                                                 final int pLeafLen) {
        theFanOut = (short) pFanOut;
        theMaxDepth = (short) pMaxDepth;
        theLeafLen = pLeafLen;
        return this;
    }

    @Override
    public GordianMacParams build() {
        /* Create params */
        final GordianCoreMacParams myParams = new GordianCoreMacParams();

        /* Record key and Nonce */
        if (theKey != null) {
            myParams.setKey(theKey);
        }
        if (theNonce != null) {
            myParams.setNonce(theNonce);
        } else if (randomNonce) {
            myParams.setRandomNonce();
        }

        /* Record personalisation and output length */
        if (thePersonal != null) {
            myParams.setPersonal(thePersonal);
        }
        myParams.setOutLen(theOutLen);

        /* Record tree details */
        myParams.setTreeFanOut(theFanOut);
        myParams.setTreeMaxDepth(theMaxDepth);
        myParams.setTreeLeafLen(theLeafLen);

        /* Reset state */
        reset();

        /* Return the parameters */
        return myParams;
    }

    /**
     * Reset state.
     */
    private void reset() {
        /* Reset data */
        theKey = null;
        theNonce = null;
        thePersonal = null;

        randomNonce = false;

        theOutLen = 0;
        theFanOut = 1;
        theMaxDepth = 1;
        theLeafLen = 0;
    }
}
