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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.random;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyLengths;
import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomSpec;
import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * SecureRandom Specification Builder.
 */
public final class GordianCoreRandomSpecBuilder
        implements GordianNewRandomSpecBuilder {
    /**
     * The type.
     */
    private GordianNewRandomType theType;

    /**
     * The subSpec.
     */
    private Object theSubSpec;

    /**
     * The subSpec.
     */
    private boolean withResistance;

    /**
     * Constructor.
     */
    private GordianCoreRandomSpecBuilder() {
    }

    /**
     * Obtain new instance.
     *
     * @return the new instance
     */
    public static GordianCoreRandomSpecBuilder newInstance() {
        return new GordianCoreRandomSpecBuilder();
    }

    @Override
    public GordianNewRandomSpecBuilder withType(final GordianNewRandomType pType) {
        theType = pType;
        return this;
    }

    @Override
    public GordianNewRandomSpecBuilder withDigestSubSpec(final GordianNewDigestSpec pDigest) {
        theSubSpec = pDigest;
        return this;
    }

    @Override
    public GordianNewRandomSpecBuilder withSymKeySubSpec(final GordianNewSymKeySpec pSymKey) {
        theSubSpec = pSymKey;
        return this;
    }

    @Override
    public GordianNewRandomSpecBuilder withResistance() {
        withResistance = true;
        return this;
    }

    @Override
    public GordianNewRandomSpec build() {
        /* Create spec, reset and return */
        final GordianCoreRandomSpec mySpec = new GordianCoreRandomSpec(theType, theSubSpec, withResistance);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theType = null;
        theSubSpec = null;
        withResistance = false;
    }

    /**
     * List all possible randomSpecs.
     *
     * @return the list
     */
    public static List<GordianNewRandomSpec> listAllPossibleSpecs() {
        /* Create the array list */
        final List<GordianNewRandomSpec> myList = new ArrayList<>();
        final GordianCoreRandomSpecBuilder myBuilder = new GordianCoreRandomSpecBuilder();

        /* For each digestSpec */
        for (final GordianNewDigestSpec mySpec : GordianCoreDigestSpecBuilder.listAllPossibleSpecs()) {
            /* Add a hash random */
            myList.add(myBuilder.hash(mySpec));
            myList.add(myBuilder.hashResist(mySpec));

            /* Add an hMac random */
            myList.add(myBuilder.hMac(mySpec));
            myList.add(myBuilder.hMacResist(mySpec));
        }

        /* For each KeyLength */
        final Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            final GordianLength myKeyLen = myIterator.next();

            /* For each symKeySpec */
            for (final GordianNewSymKeySpec mySpec : GordianCoreSymKeySpecBuilder.listAllPossibleSymKeySpecs(myKeyLen)) {
                /* Add a CTR random */
                myList.add(myBuilder.ctr(mySpec));
                myList.add(myBuilder.ctrResist(mySpec));

                /* Add an X931 random */
                myList.add(myBuilder.x931(mySpec));
                myList.add(myBuilder.x931Resist(mySpec));
            }
        }

        /* Return the list */
        return myList;
    }
}
