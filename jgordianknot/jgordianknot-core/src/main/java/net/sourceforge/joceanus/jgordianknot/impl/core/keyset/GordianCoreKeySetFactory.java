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

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
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
    public GordianKeySet createKeySet() {
        return new GordianCoreKeySet(getFactory());
    }

    @Override
    public GordianKeySet generateKeySet() throws OceanusException {
        /* Generate a random keySet */
        final GordianCoreKeySet myKeySet = new GordianCoreKeySet(theFactory);
        myKeySet.buildFromRandom();
        return myKeySet;
    }

    @Override
    public GordianKeySetHash generateKeySetHash(final char[] pPassword) throws OceanusException {
        return GordianCoreKeySetHash.newKeySetHash(getFactory(), pPassword);
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
    public Predicate<GordianSymKeyType> supportedKeySetSymKeyTypes() {
        return this::validKeySetSymKeyType;
    }

    @Override
    public Predicate<GordianSymKeySpec> supportedKeySetSymKeySpecs() {
        return p -> supportedKeySetSymKeyTypes().test(p.getSymKeyType())
                && p.getBlockLength() == GordianLength.LEN_128;
    }

    /**
     * check valid keySet symKeyType.
     * @param pKeyType the symKeyType
     * @return true/false
     */
    private boolean validKeySetSymKeyType(final GordianSymKeyType pKeyType) {
        final GordianCoreFactory myFactory = getFactory();
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) myFactory.getCipherFactory();
        return myCiphers.validSymKeyType(pKeyType)
                && GordianCoreCipherFactory.validStdBlockSymKeyTypeForRestriction(pKeyType, myFactory.isRestricted());
    }
}
