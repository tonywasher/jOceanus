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

package io.github.tonywasher.joceanus.gordianknot.impl.core.digest.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSubSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSubSpec.GordianNewDigestState;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.spec.GordianCoreDigestSubSpec.GordianCoreDigestState;

import java.util.ArrayList;
import java.util.List;

/**
 * Digest Specification Builder.
 */
public class GordianCoreDigestSpecBuilder
        implements GordianNewDigestSpecBuilder {
    /**
     * The type.
     */
    private GordianNewDigestType theType;

    /**
     * The subSpec.
     */
    private GordianNewDigestSubSpec theSubSpec;

    /**
     * The length.
     */
    private GordianLength theLength;

    /**
     * The type.
     */
    private boolean asXof;

    @Override
    public GordianCoreDigestSpecBuilder withType(final GordianNewDigestType pType) {
        theType = pType;
        return this;
    }

    @Override
    public GordianCoreDigestSpecBuilder withState(final GordianNewDigestState pState) {
        theSubSpec = pState;
        return this;
    }

    @Override
    public GordianCoreDigestSpecBuilder withLength(final GordianLength pLength) {
        theLength = pLength;
        return this;
    }

    @Override
    public GordianCoreDigestSpecBuilder asXof() {
        asXof = true;
        return this;
    }

    @Override
    public GordianCoreDigestSpec build() {
        /* Handle null type */
        if (theType == null) {
            throw new NullPointerException("digestType is null");
        }

        /* Handle defaults */
        theLength = theLength == null ? GordianCoreDigestType.getDefaultLength(theType) : theLength;
        theSubSpec = theSubSpec == null ? GordianCoreDigestSubSpec.getDefaultSubSpecForTypeAndLength(theType, theLength) : theSubSpec;

        /* Create subSpec, reset and return */
        final GordianCoreDigestSpec mySpec = new GordianCoreDigestSpec(theType, theSubSpec, theLength, asXof);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theType = null;
        theSubSpec = null;
        theLength = null;
        asXof = false;
    }

    /**
     * List possible digestSpecs.
     *
     * @return the possible digestSpecs
     */
    public static List<GordianNewDigestSpec> listAllPossibleSpecs() {
        /* Create the array list */
        final List<GordianNewDigestSpec> myList = new ArrayList<>();

        /* For each digest type */
        for (final GordianCoreDigestType myType : GordianCoreDigestType.values()) {
            final GordianNewDigestType myBaseType = myType.getType();

            /* For each subSpecType */
            for (GordianNewDigestSubSpec mySubSpec : GordianCoreDigestSubSpec.getPossibleSubSpecsForType(myBaseType)) {
                /* For each length */
                for (final GordianLength myLength : myType.getSupportedLengths()) {
                    final GordianNewDigestSpec mySpec = new GordianCoreDigestSpec(myBaseType, mySubSpec, myLength, false);

                    /* Add if valid */
                    if (mySpec.isValid()) {
                        myList.add(mySpec);
                    }
                }

                /* If we have a possible Xof */
                if (mySubSpec instanceof GordianNewDigestState myState) {
                    final GordianCoreDigestState myCoreState = GordianCoreDigestState.mapCoreState(myState);
                    final GordianNewDigestSpec mySpec = new GordianCoreDigestSpec(myBaseType, myState, myCoreState.getLength(), Boolean.TRUE);

                    /* Add if valid */
                    if (mySpec.isValid()) {
                        myList.add(mySpec);
                    }

                    /* Else look for null Xof type */
                } else {
                    final GordianNewDigestSpec mySpec = new GordianCoreDigestSpec(myBaseType, null,
                            GordianCoreDigestType.getDefaultLength(myBaseType), Boolean.TRUE);

                    /* Add if valid */
                    if (mySpec.isValid()) {
                        myList.add(mySpec);
                    }
                }
            }
        }

        /* Return the list */
        return myList;
    }
}
