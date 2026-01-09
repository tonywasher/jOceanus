/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.factory;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianAsyncFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianLockFactory;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.gordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreKnuthObfuscater;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianFactoryGenerator;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianIdManager;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianPersonalisation;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianSeededRandom;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianValidator;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.key.GordianKeyAlgId;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySetFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.lock.GordianCoreLockFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.random.GordianCoreRandomFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.zip.GordianCoreZipFactory;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.security.SecureRandom;

/**
 * Core factory.
 */
public abstract class GordianCoreFactory
        implements GordianBaseFactory {
    /**
     * The number of seed bytes.
     */
    private static final int SEED_SIZE = GordianLength.LEN_256.getByteLength();

    /**
     * Parameters.
     */
    private final GordianParameters theParameters;

    /**
     * Factory generator.
     */
    private final GordianFactoryGenerator theGenerator;

    /**
     * Random Source.
     */
    private final GordianRandomSource theRandom;

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
     * Embedded KeySet.
     */
    private final GordianKeySet theKeySet;

    /**
     * Digest Factory.
     */
    private final GordianDigestFactory theDigestFactory;

    /**
     * Cipher Factory.
     */
    private final GordianCipherFactory theCipherFactory;

    /**
     * Mac Factory.
     */
    private final GordianMacFactory theMacFactory;

    /**
     * Random Factory.
     */
    private final GordianRandomFactory theRandomFactory;

    /**
     * KeySet Factory.
     */
    private final GordianKeySetFactory theKeySetFactory;

    /**
     * Lock Factory.
     */
    private final GordianCoreLockFactory theLockFactory;

    /**
     * Zip Factory.
     */
    private final GordianCoreZipFactory theZipFactory;

    /**
     * The validator.
     */
    private final GordianValidator theValidator;

    /**
     * The Key AlgIds.
     */
    private GordianKeyAlgId theKeyAlgIds;

    /**
     * Async Factory.
     */
    private GordianAsyncFactory theAsyncFactory;

    /**
     * Constructor.
     * @param pGenerator the factory generator
     * @param pParameters the parameters
     * @throws GordianException on error
     */
    protected GordianCoreFactory(final GordianFactoryGenerator pGenerator,
                                 final GordianParameters pParameters) throws GordianException {
        /* Check parameters */
        if (pParameters == null || !pParameters.validate()) {
            throw new GordianDataException("Invalid Parameters");
        }

        /* Store the parameters */
        theGenerator = pGenerator;
        theParameters = pParameters;

        /* Create the random source */
        theRandom = new GordianRandomSource();

        /* Declare factories */
        theValidator = newValidator();
        theDigestFactory = newDigestFactory(this);
        theCipherFactory = newCipherFactory(this);
        theMacFactory = newMacFactory(this);
        theRandomFactory = new GordianCoreRandomFactory(this);
        theKeySetFactory = new GordianCoreKeySetFactory(this);
        theLockFactory = new GordianCoreLockFactory(this);
        theZipFactory = new GordianCoreZipFactory(this);

        /* Declare personalisation */
        thePersonalisation = new GordianPersonalisation(this);
        theIdManager = new GordianIdManager(this);
        theObfuscater = new GordianCoreKnuthObfuscater(this);
        theKeySet = createEmbeddedKeySet();
    }

    @Override
    public GordianFactory newFactory(final GordianParameters pParameters) throws GordianException {
        return theGenerator.newFactory(pParameters);
    }

    /**
     * Obtain the random source.
     * @return the random source
     */
    public GordianRandomSource getRandomSource() {
        return theRandom;
    }

    /**
     * Obtain the parameters.
     * @return the parameters
     */
    public GordianParameters getParameters() {
        return theParameters;
    }

    @Override
    public GordianFactoryType getFactoryType() {
        return theParameters.getFactoryType();
    }

    /**
     * Obtain the security seed.
     * @return the security seed
     */
    public byte[] getSecuritySeed() {
        return theParameters.getSecuritySeed();
    }

    /**
     * Obtain the keySet seed.
     * @return the keySet seed
     */
    public byte[] getKeySetSeed() {
        return theParameters.getKeySetSeed();
    }

    /**
     * Is this an internal factory?
     * @return true/false
     */
    public boolean isInternal() {
        return theParameters.isInternal();
    }

    /**
     * Is this a random factory?
     * @return true/false
     */
    public boolean isRandom() {
        return getKeySetSeed() != null;
    }

    @Override
    public GordianKeySet getEmbeddedKeySet() {
        return theKeySet;
    }

    /**
     * Create embedded keySet.
     * @return the embedded keySet
     * @throws GordianException on error
     */
    private GordianKeySet createEmbeddedKeySet() throws GordianException {
        /* Obtain the keySet seed */
        final byte[] mySeed = thePersonalisation.getKeySetVector();

        /* Derive the keySet */
        final GordianKeySetGenerate myKeySets = (GordianKeySetGenerate) getKeySetFactory();
        return myKeySets.generateKeySet(mySeed);
    }

    @Override
    public GordianPersonalisation getPersonalisation() {
        return thePersonalisation;
    }

    @Override
    public GordianIdManager getIdManager() {
        return theIdManager;
    }

    @Override
    public GordianCoreKnuthObfuscater getObfuscater() {
        return theObfuscater;
    }

    @Override
    public void reSeedRandom() {
        /* Access the random */
        final SecureRandom myRandom = theRandom.getRandom();
        if (myRandom instanceof GordianSeededRandom mySeeded) {
            /* Generate and apply the new seed */
            final byte[] mySeed = mySeeded.generateSeed(SEED_SIZE);
            mySeeded.setSeed(mySeed);
            mySeeded.reseed(null);
        }
    }

    @Override
    public GordianDigestFactory getDigestFactory() {
        return theDigestFactory;
    }

    /**
     * Create a new digest factory.
     * @param pFactory the factory
     * @return the new digest factory
     */
    public abstract GordianDigestFactory newDigestFactory(GordianBaseFactory pFactory);

    @Override
    public GordianCipherFactory getCipherFactory() {
        return theCipherFactory;
    }

    /**
     * Create a new cipher factory.
     * @param pFactory the factory
     * @return the new cipher factory
     */
    public abstract GordianCipherFactory newCipherFactory(GordianBaseFactory pFactory);

    @Override
    public GordianMacFactory getMacFactory() {
        return theMacFactory;
    }

    /**
     * Create a new MAC factory.
     * @param pFactory the factory
     * @return the new MAC factory
     */
    public abstract GordianMacFactory newMacFactory(GordianBaseFactory pFactory);

    @Override
    public GordianRandomFactory getRandomFactory() {
        return theRandomFactory;
    }

    @Override
    public GordianKeySetFactory getKeySetFactory() {
        return theKeySetFactory;
    }

    @Override
    public GordianLockFactory getLockFactory() {
        return theLockFactory;
    }

    @Override
    public GordianFactoryLock newFactoryLock(final GordianFactory pFactoryToLock,
                                             final GordianPasswordLockSpec pLockSpec,
                                             final char[] pPassword) throws GordianException {
        return theLockFactory.newFactoryLock(pFactoryToLock, pLockSpec, pPassword);
    }

    @Override
    public GordianFactoryLock newFactoryLock(final GordianPasswordLockSpec pLockSpec,
                                             final GordianFactoryType pFactoryType,
                                             final char[] pPassword) throws GordianException {
        return theLockFactory.newFactoryLock(pLockSpec, pFactoryType, pPassword);
    }

    @Override
    public GordianFactoryLock resolveFactoryLock(final byte[] pLockBytes,
                                                 final char[] pPassword) throws GordianException {
        return theLockFactory.resolveFactoryLock(pLockBytes, pPassword);
    }

    /**
     * Obtain the validator.
     * @return the validator
     */
    public GordianValidator getValidator() {
        return theValidator;
    }

    /**
     * Create a new validator.
     * @return the new validator
     */
    public GordianValidator newValidator() {
        return new GordianValidator();
    }

    @Override
    public GordianZipFactory getZipFactory() {
        return theZipFactory;
    }

    @Override
    public GordianAsyncFactory getAsyncFactory() {
        if (theAsyncFactory == null) {
            theAsyncFactory = newAsyncFactory(this);
        }
        return theAsyncFactory;
    }

    /**
     * Create a new Async factory.
     * @param pFactory the factory
     * @return the new Async factory
     */
    public abstract GordianAsyncFactory newAsyncFactory(GordianBaseFactory pFactory);

    /**
     * Obtain Identifier for keySpec.
     * @param pSpec the keySpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianKeySpec pSpec) {
        return getKeyAlgIds().getIdentifierForSpec(pSpec);
    }

    /**
     * Obtain keySpec for Identifier.
     * @param pIdentifier the identifier.
     * @return the keySpec (or null if not found)
     */
    public GordianKeySpec getKeySpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return getKeyAlgIds().getKeySpecForIdentifier(pIdentifier);
    }

    /**
     * Obtain the key algorithm Ids.
     * @return the key Algorithm Ids
     */
    private GordianKeyAlgId getKeyAlgIds() {
        if (theKeyAlgIds == null) {
            theKeyAlgIds = new GordianKeyAlgId(this);
        }
        return theKeyAlgIds;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (pThat == this) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (!(pThat instanceof GordianCoreFactory myThat)) {
            return false;
        }

        /* Check Differences */
        return theParameters.equals(myThat.theParameters);
    }

    @Override
    public int hashCode() {
        return theParameters.hashCode();
    }
}
