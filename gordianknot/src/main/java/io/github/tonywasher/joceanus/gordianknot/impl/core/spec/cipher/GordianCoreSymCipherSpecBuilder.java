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

import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewCipherMode;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPadding;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;

import java.util.ArrayList;
import java.util.List;

public final class GordianCoreSymCipherSpecBuilder
        implements GordianNewSymCipherSpecBuilder {
    /**
     * The keySpec.
     */
    private GordianCoreSymKeySpec theKeySpec;

    /**
     * The mode.
     */
    private GordianNewCipherMode theMode;

    /**
     * The padding.
     */
    private GordianNewPadding thePadding;

    /**
     * Private constructor.
     */
    private GordianCoreSymCipherSpecBuilder() {
    }

    /**
     * Obtain new instance.
     *
     * @return the new instance
     */
    public static GordianCoreSymCipherSpecBuilder newInstance() {
        return new GordianCoreSymCipherSpecBuilder();
    }

    @Override
    public GordianNewSymCipherSpecBuilder withKeySpec(final GordianNewSymKeySpec pKeySpec) {
        theKeySpec = (GordianCoreSymKeySpec) pKeySpec;
        return this;
    }

    @Override
    public GordianNewSymCipherSpecBuilder withMode(final GordianNewCipherMode pMode) {
        theMode = pMode;
        return this;
    }

    @Override
    public GordianNewSymCipherSpecBuilder withPadding(final GordianNewPadding pPadding) {
        thePadding = pPadding;
        return this;
    }

    @Override
    public GordianNewSymCipherSpec build() {
        thePadding = thePadding == null ? GordianNewPadding.NONE : thePadding;
        final GordianNewSymCipherSpec mySpec = new GordianCoreSymCipherSpec(theKeySpec, theMode, thePadding);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theKeySpec = null;
        theMode = null;
        thePadding = null;
    }

    /**
     * List all possible symCipherSpecs for a keySpec.
     *
     * @param pSpec the keySpec
     * @return the list
     */
    public static List<GordianNewSymCipherSpec> listAllPossibleSymCipherSpecs(final GordianNewSymKeySpec pSpec) {
        /* Create the array list */
        final List<GordianNewSymCipherSpec> myList = new ArrayList<>();
        final GordianCoreSymKeySpec mySpec = (GordianCoreSymKeySpec) pSpec;

        /* Loop through the modes */
        for (GordianCoreCipherMode myMode : GordianCoreCipherMode.values()) {
            /* If the mode has padding */
            if (myMode.hasPadding()) {
                /* Loop through the paddings */
                for (GordianNewPadding myPadding : GordianNewPadding.values()) {
                    myList.add(new GordianCoreSymCipherSpec(mySpec, myMode.getMode(), myPadding));
                }

                /* else no padding */
            } else {
                myList.add(new GordianCoreSymCipherSpec(mySpec, myMode.getMode(), GordianNewPadding.NONE));
            }
        }

        /* Return the list */
        return myList;
    }
}
