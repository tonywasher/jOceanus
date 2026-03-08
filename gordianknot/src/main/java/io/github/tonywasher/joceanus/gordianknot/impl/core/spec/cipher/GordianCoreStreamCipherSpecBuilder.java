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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySpecBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * The StreamCipherSpec Builder class.
 */
public final class GordianCoreStreamCipherSpecBuilder
        implements GordianStreamCipherSpecBuilder {
    /**
     * The streamKeySpec builder.
     */
    private final GordianStreamKeySpecBuilder theBuilder;

    /**
     * The keySpec.
     */
    private GordianCoreStreamKeySpec theKeySpec;

    /**
     * Is this an AEAD variant?
     */
    private boolean asAEAD;

    /**
     * Private constructor.
     */
    private GordianCoreStreamCipherSpecBuilder() {
        theBuilder = GordianCoreStreamKeySpecBuilder.newInstance();
    }

    /**
     * Obtain new instance.
     *
     * @return the new instance
     */
    public static GordianCoreStreamCipherSpecBuilder newInstance() {
        return new GordianCoreStreamCipherSpecBuilder();
    }

    @Override
    public GordianStreamCipherSpecBuilder withKeySpec(final GordianStreamKeySpec pSpec) {
        theKeySpec = (GordianCoreStreamKeySpec) pSpec;
        return this;
    }

    @Override
    public GordianStreamCipherSpecBuilder asAEAD() {
        asAEAD = true;
        return this;
    }


    @Override
    public GordianStreamKeySpecBuilder usingStreamKeySpecBuilder() {
        return theBuilder;
    }

    @Override
    public GordianStreamCipherSpec build() {
        /* Create spec, reset and return */
        final GordianCoreStreamCipherSpec mySpec = new GordianCoreStreamCipherSpec(theKeySpec, asAEAD);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theKeySpec = null;
        asAEAD = false;
    }

    /**
     * List all possible streamCipherSpecs for a keyLength.
     *
     * @param pKeyLen the keyLength
     * @return the list
     */
    public static List<GordianStreamCipherSpec> listAllSupportedStreamCipherSpecs(final GordianLength pKeyLen) {
        final List<GordianStreamCipherSpec> myResult = new ArrayList<>();
        for (GordianStreamKeySpec mySpec : GordianCoreStreamKeySpecBuilder.listAllPossibleStreamKeySpecs(pKeyLen)) {
            /* Add the standard cipher */
            final GordianCoreStreamKeySpec myCoreSpec = (GordianCoreStreamKeySpec) mySpec;
            myResult.add(new GordianCoreStreamCipherSpec(myCoreSpec, false));

            /* Add the AAD Cipher if supported */
            if (myCoreSpec.supportsAEAD()) {
                myResult.add(new GordianCoreStreamCipherSpec(myCoreSpec, true));
            }
        }
        return myResult;
    }
}
