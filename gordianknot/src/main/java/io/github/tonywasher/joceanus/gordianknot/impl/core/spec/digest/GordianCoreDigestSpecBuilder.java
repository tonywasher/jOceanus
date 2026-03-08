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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSubSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSubSpec.GordianDigestState;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSubSpec.GordianCoreDigestState;

import java.util.ArrayList;
import java.util.List;

/**
 * Digest Specification Builder.
 */
public final class GordianCoreDigestSpecBuilder
        implements GordianDigestSpecBuilder {
    /**
     * The type.
     */
    private GordianDigestType theType;

    /**
     * The subSpec.
     */
    private GordianDigestSubSpec theSubSpec;

    /**
     * The length.
     */
    private GordianLength theLength;

    /**
     * The type.
     */
    private boolean asXof;

    /**
     * Private constructor.
     */
    private GordianCoreDigestSpecBuilder() {
    }

    /**
     * Obtain new instance.
     *
     * @return the new instance
     */
    public static GordianCoreDigestSpecBuilder newInstance() {
        return new GordianCoreDigestSpecBuilder();
    }

    @Override
    public GordianCoreDigestSpecBuilder withType(final GordianDigestType pType) {
        theType = pType;
        return this;
    }

    @Override
    public GordianCoreDigestSpecBuilder withState(final GordianDigestState pState) {
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
    public static List<GordianDigestSpec> listAllPossibleSpecs() {
        /* Create the array list */
        final List<GordianDigestSpec> myList = new ArrayList<>();

        /* For each digest type */
        for (final GordianCoreDigestType myType : GordianCoreDigestType.values()) {
            final GordianDigestType myBaseType = myType.getType();

            /* For each subSpecType */
            for (GordianDigestSubSpec mySubSpec : GordianCoreDigestSubSpec.getPossibleSubSpecsForType(myBaseType)) {
                /* For each length */
                for (final GordianLength myLength : myType.getSupportedLengths()) {
                    final GordianDigestSpec mySpec = new GordianCoreDigestSpec(myBaseType, mySubSpec, myLength, false);

                    /* Add if valid */
                    if (mySpec.isValid()) {
                        myList.add(mySpec);
                    }
                }

                /* If we have a possible Xof */
                if (mySubSpec instanceof GordianDigestState myState) {
                    final GordianCoreDigestState myCoreState = GordianCoreDigestState.mapCoreState(myState);
                    final GordianDigestSpec mySpec = new GordianCoreDigestSpec(myBaseType, myState, myCoreState.getLength(), Boolean.TRUE);

                    /* Add if valid */
                    if (mySpec.isValid()) {
                        myList.add(mySpec);
                    }

                    /* Else look for null Xof type */
                } else {
                    final GordianDigestSpec mySpec = new GordianCoreDigestSpec(myBaseType, null,
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
