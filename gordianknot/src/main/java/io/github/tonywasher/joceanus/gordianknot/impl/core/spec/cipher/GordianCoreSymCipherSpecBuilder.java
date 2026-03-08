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

import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianCipherMode;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPadding;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpecBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Core SymCipherSpec Builder.
 */
public final class GordianCoreSymCipherSpecBuilder
        implements GordianSymCipherSpecBuilder {
    /**
     * The symKeySpec builder.
     */
    private final GordianSymKeySpecBuilder theBuilder;

    /**
     * The keySpec.
     */
    private GordianCoreSymKeySpec theKeySpec;

    /**
     * The mode.
     */
    private GordianCipherMode theMode;

    /**
     * The padding.
     */
    private GordianPadding thePadding;

    /**
     * Private constructor.
     */
    private GordianCoreSymCipherSpecBuilder() {
        theBuilder = GordianCoreSymKeySpecBuilder.newInstance();
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
    public GordianSymCipherSpecBuilder withKeySpec(final GordianSymKeySpec pKeySpec) {
        theKeySpec = (GordianCoreSymKeySpec) pKeySpec;
        return this;
    }

    @Override
    public GordianSymCipherSpecBuilder withMode(final GordianCipherMode pMode) {
        theMode = pMode;
        return this;
    }

    @Override
    public GordianSymCipherSpecBuilder withPadding(final GordianPadding pPadding) {
        thePadding = pPadding;
        return this;
    }


    @Override
    public GordianSymKeySpecBuilder usingSymKeySpecBuilder() {
        return theBuilder;
    }

    @Override
    public GordianSymCipherSpec build() {
        thePadding = thePadding == null ? GordianPadding.NONE : thePadding;
        final GordianSymCipherSpec mySpec = new GordianCoreSymCipherSpec(theKeySpec, theMode, thePadding);
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
    public static List<GordianSymCipherSpec> listAllPossibleSymCipherSpecs(final GordianSymKeySpec pSpec) {
        /* Create the array list */
        final List<GordianSymCipherSpec> myList = new ArrayList<>();
        final GordianCoreSymKeySpec mySpec = (GordianCoreSymKeySpec) pSpec;

        /* Loop through the modes */
        for (GordianCoreCipherMode myMode : GordianCoreCipherMode.values()) {
            /* If the mode has padding */
            if (myMode.hasPadding()) {
                /* Loop through the paddings */
                for (GordianPadding myPadding : GordianPadding.values()) {
                    myList.add(new GordianCoreSymCipherSpec(mySpec, myMode.getMode(), myPadding));
                }

                /* else no padding */
            } else {
                myList.add(new GordianCoreSymCipherSpec(mySpec, myMode.getMode(), GordianPadding.NONE));
            }
        }

        /* Return the list */
        return myList;
    }
}
