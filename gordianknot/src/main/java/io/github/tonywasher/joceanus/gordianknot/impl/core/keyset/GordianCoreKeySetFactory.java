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
package io.github.tonywasher.joceanus.gordianknot.impl.core.keyset;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keyset.GordianKeySet;
import io.github.tonywasher.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory.GordianKeySetGenerate;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;

import java.util.function.Predicate;

/**
 * GordianKnot Core KeySet Factory.
 */
public class GordianCoreKeySetFactory
        implements GordianBaseKeySetFactory, GordianKeySetGenerate {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * Constructor.
     *
     * @param pFactory the factory.
     * @throws GordianException on error
     */
    public GordianCoreKeySetFactory(final GordianBaseFactory pFactory) throws GordianException {
        theFactory = pFactory;
    }

    /**
     * Obtain the factory.
     *
     * @return the factory
     */
    public GordianBaseFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianCoreKeySet createKeySet(final GordianKeySetSpec pSpec) throws GordianException {
        /* Check Spec */
        checkKeySetSpec(pSpec);

        /* Generate an empty keySet */
        return new GordianCoreKeySet(getFactory(), pSpec);
    }

    @Override
    public GordianCoreKeySet generateKeySet(final GordianKeySetSpec pSpec) throws GordianException {
        /* Check Spec */
        checkKeySetSpec(pSpec);

        /* Generate a random keySet */
        final GordianCoreKeySet myKeySet = new GordianCoreKeySet(theFactory, pSpec);
        myKeySet.buildFromRandom();
        return myKeySet;
    }

    @Override
    public GordianKeySet generateKeySet(final byte[] pSeed) throws GordianException {
        final GordianCoreKeySet myKeySet = generateKeySet(new GordianKeySetSpec());
        myKeySet.buildFromSecret(pSeed);
        return myKeySet;
    }

    @Override
    public Predicate<GordianKeySetSpec> supportedKeySetSpecs() {
        return GordianCoreKeySetFactory::validKeySetSpec;
    }

    /**
     * Check the keySetSpec.
     *
     * @param pSpec the keySetSpec
     * @throws GordianException on error
     */
    public void checkKeySetSpec(final GordianKeySetSpec pSpec) throws GordianException {
        /* Check validity of KeySet */
        if (!supportedKeySetSpecs().test(pSpec)) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pSpec));
        }
    }

    /**
     * check valid keySetSpec.
     *
     * @param pSpec the keySetSpec
     * @return true/false
     */
    public static boolean validKeySetSpec(final GordianKeySetSpec pSpec) {
        /* Check for invalid spec */
        if (pSpec == null || !pSpec.isValid()) {
            return false;
        }

        /* Check on length */
        switch (pSpec.getKeyLength()) {
            case LEN_128:
            case LEN_192:
            case LEN_256:
                return true;
            default:
                return false;
        }
    }
}
