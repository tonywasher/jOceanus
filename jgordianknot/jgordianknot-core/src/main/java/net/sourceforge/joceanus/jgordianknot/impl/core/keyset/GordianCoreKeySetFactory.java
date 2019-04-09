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

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot Core KeySet Factory.
 */
public class GordianCoreKeySetFactory
    implements GordianKeySetFactory {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * Personalisation.
     */
    private final GordianPersonalisation thePersonalisation;

    /**
     * IdManager.
     */
    private final GordianIdManager theIdManager;

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
        thePersonalisation = new GordianPersonalisation(theFactory);
        theIdManager = new GordianIdManager(theFactory, this);
        theObfuscater = new GordianCoreKnuthObfuscater(this);
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    public GordianCoreFactory getFactory() {
        return theFactory;
    }

    /**
     * Obtain the personalisation bytes.
     * @return the personalisation
     */
    public GordianPersonalisation getPersonalisation() {
        return thePersonalisation;
    }

    /**
     * Obtain the idManager.
     * @return the idManager
     */
    public GordianIdManager getIdManager() {
        return theIdManager;
    }

    @Override
    public GordianCoreKnuthObfuscater getObfuscater() {
        return theObfuscater;
    }

    @Override
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
    public GordianKeySetHash generateKeySetHash(final GordianKeySetSpec pSpec,
                                                final char[] pPassword) throws OceanusException {
        /* Check Spec */
        checkKeySetSpec(pSpec);

        /* Create the new hash */
        return GordianCoreKeySetHash.newKeySetHash(getFactory(), pSpec, pPassword);
    }

    @Override
    public GordianKeySetHash deriveKeySetHash(final byte[] pHashBytes,
                                              final char[] pPassword) throws OceanusException {
        return GordianCoreKeySetHash.resolveKeySetHash(getFactory(), pHashBytes, pPassword);
    }

    @Override
    public Predicate<GordianDigestType> supportedKeySetDigestTypes() {
        final GordianMacFactory myMacs = getFactory().getMacFactory();
        return myMacs.supportedHMacDigestTypes().and(GordianDigestType::isCombinedHashDigest);
    }

    @Override
    public Predicate<GordianSymKeyType> supportedKeySetSymKeyTypes(final GordianLength pKeyLen) {
        return t -> validKeySetSymKeyType(t, pKeyLen);
    }

    @Override
    public Predicate<GordianSymKeySpec> supportedKeySetSymKeySpecs(final GordianLength pKeyLen) {
        return s -> supportedKeySetSymKeyTypes(pKeyLen).test(s.getSymKeyType())
                && s.getBlockLength() == GordianLength.LEN_128;
    }

    @Override
    public Predicate<GordianKeySetSpec> supportedKeySetSpecs() {
        return this::validKeySetSpec;
    }

    /**
     * check valid keySet symKeyType.
     * @param pKeyType the symKeyType
     * @param pKeyLen the keyLength
     * @return true/false
     */
    private boolean validKeySetSymKeyType(final GordianSymKeyType pKeyType,
                                          final GordianLength pKeyLen) {
        final GordianCoreFactory myFactory = getFactory();
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) myFactory.getCipherFactory();
        return myCiphers.validSymKeyType(pKeyType)
                && GordianCoreCipherFactory.validStdBlockSymKeyTypeForKeyLength(pKeyType, pKeyLen);
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
    private boolean validKeySetSpec(final GordianKeySetSpec pSpec) {
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
