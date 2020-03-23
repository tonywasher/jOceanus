/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.keyset;

import java.util.function.Predicate;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot Core KeySet Factory.
 */
public class GordianCoreKeySetFactory
    implements GordianKeySetFactory {
    /**
     * KeySetOID branch.
     */
    static final ASN1ObjectIdentifier KEYSETOID = GordianASN1Util.EXTOID.branch("1");

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * Obfuscater.
     */
    private final GordianCoreKnuthObfuscater theObfuscater;

    /**
     * Constructor.
     * @param pFactory the factory.
     * @throws OceanusException on error
     */
    public GordianCoreKeySetFactory(final GordianCoreFactory pFactory) throws OceanusException {
        theFactory = pFactory;
        theObfuscater = new GordianCoreKnuthObfuscater(pFactory);
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    public GordianCoreFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianCoreKnuthObfuscater getObfuscater() {
        return theObfuscater;
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
    public GordianKeySetHash generateKeySetHash(final GordianKeySetHashSpec pSpec,
                                                final char[] pPassword) throws OceanusException {
        /* Check Spec */
        checkKeySetHashSpec(pSpec);

        /* Create the new hash */
        return GordianCoreKeySetHash.newKeySetHash(getFactory(), pSpec, pPassword);
    }

    @Override
    public GordianKeySetHash deriveKeySetHash(final byte[] pHashBytes,
                                              final char[] pPassword) throws OceanusException {
        return GordianCoreKeySetHash.resolveKeySetHash(getFactory(), pHashBytes, pPassword);
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
     * Check the keySetHashSpec.
     * @param pSpec the keySetSpec
     * @throws OceanusException on error
     */
    public void checkKeySetHashSpec(final GordianKeySetHashSpec pSpec) throws OceanusException {
        /* Check validity of KeySet */
        if (!validKeySetHashSpec(pSpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pSpec));
        }
    }

    /**
     * check valid keySetSpec.
     * @param pSpec the keySetSpec
     * @return true/false
     */
    private static boolean validKeySetSpec(final GordianKeySetSpec pSpec) {
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

    /**
     * check valid keySetHashSpec.
     * @param pSpec the keySetHashSpec
     * @return true/false
     */
    private boolean validKeySetHashSpec(final GordianKeySetHashSpec pSpec) {
        /* Check for invalid spec */
        if (pSpec == null || !pSpec.isValid()) {
            return false;
        }

        /* Check on length */
        return validKeySetSpec(pSpec.getKeySetSpec());
    }
}
