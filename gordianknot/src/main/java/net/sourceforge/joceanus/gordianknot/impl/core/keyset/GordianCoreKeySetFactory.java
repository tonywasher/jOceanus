/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.gordianknot.impl.core.keyset;

import java.util.function.Predicate;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory.GordianKeySetGenerate;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.oceanus.OceanusException;

/**
 * GordianKnot Core KeySet Factory.
 */
public class GordianCoreKeySetFactory
    implements GordianKeySetFactory, GordianKeySetGenerate {
    /**
     * KeySetOID branch.
     */
    static final ASN1ObjectIdentifier KEYSETOID = GordianASN1Util.EXTOID.branch("1");

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * Constructor.
     * @param pFactory the factory.
     * @throws OceanusException on error
     */
    public GordianCoreKeySetFactory(final GordianCoreFactory pFactory) throws OceanusException {
        theFactory = pFactory;
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    public GordianCoreFactory getFactory() {
        return theFactory;
    }

    /**
     * create an empty keySet.
     * @param pSpec the keySetSpec
     * @return the empty keySedt
     * @throws OceanusException on error
     */
    public GordianCoreKeySet createKeySet(final GordianKeySetSpec pSpec) throws OceanusException {
        /* Check Spec */
        checkKeySetSpec(pSpec);

        /* Generate an empty keySet */
        return new GordianCoreKeySet(getFactory(), pSpec);
    }

    @Override
    public GordianCoreKeySet generateKeySet(final GordianKeySetSpec pSpec) throws OceanusException {
        /* Check Spec */
        checkKeySetSpec(pSpec);

        /* Generate a random keySet */
        final GordianCoreKeySet myKeySet = new GordianCoreKeySet(theFactory, pSpec);
        myKeySet.buildFromRandom();
        return myKeySet;
    }

    @Override
    public GordianKeySet generateKeySet(final byte[] pSeed) throws OceanusException {
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
     * @param pSpec the keySetSpec
     * @throws OceanusException on error
     */
    public void checkKeySetSpec(final GordianKeySetSpec pSpec) throws OceanusException {
        /* Check validity of KeySet */
        if (!supportedKeySetSpecs().test(pSpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pSpec));
        }
    }

    /**
     * check valid keySetSpec.
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
