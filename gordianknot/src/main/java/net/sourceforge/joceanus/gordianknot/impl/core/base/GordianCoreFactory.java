/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.base;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianLockFactory;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.gordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.security.SecureRandom;

/**
 * Base factory.
 */
public abstract class GordianCoreFactory
    implements GordianFactory, GordianFactoryGenerator {
    /**  * CreateKeySet interface.
     */
    public interface GordianKeySetGenerate {
        /**
         * create and build a keySet from seed.
         * @param pSeed the base seed
         * @return the keySet
         * @throws GordianException on error
         */
        GordianKeySet generateKeySet(byte[] pSeed) throws GordianException;
    }

    /**
     *  BCFactoryOID.
     */
    public static final ASN1ObjectIdentifier BCFACTORYOID = GordianASN1Util.FACTORYOID.branch("1");

    /**
     *  JCAFactoryOID.
     */
    public static final ASN1ObjectIdentifier JCAFACTORYOID = GordianASN1Util.FACTORYOID.branch("2");

    /**
     * The prime hash.
     */
    public static final int HASH_PRIME = 47;

    /**
     * RC5 rounds.
     */
    public static final int RC5_ROUNDS = 12;

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
    private GordianKeySet theKeySet;

    /**
     * Digest Factory.
     */
    private GordianDigestFactory theDigestFactory;

    /**
     * Cipher Factory.
     */
    private GordianCipherFactory theCipherFactory;

    /**
     * Mac Factory.
     */
    private GordianMacFactory theMacFactory;

    /**
     * Random Factory.
     */
    private GordianRandomFactory theRandomFactory;

    /**
     * KeySet Factory.
     */
    private GordianKeySetFactory theKeySetFactory;

    /**
     * Lock Factory.
     */
    private GordianLockFactory theLockFactory;

    /**
     * The validator.
     */
    private GordianValidator theValidator;

    /**
     * The Key AlgIds.
     */
    private GordianKeyAlgId theKeyAlgIds;

    /**
     * The Digest AlgIds.
     */
    private GordianDigestAlgId theDigestAlgIds;

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
        declareFactories();

        /* Declare personalisation */
        thePersonalisation = new GordianPersonalisation(this);
        theIdManager = new GordianIdManager(this);
        theObfuscater = new GordianCoreKnuthObfuscater(this);
        theKeySet = createEmbeddedKeySet();
    }

    /**
     * Declare factories.
     * @throws GordianException on error
     */
    protected abstract void declareFactories() throws GordianException;

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

    /**
     * Obtain the personalisation.
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
     * Set the digest factory.
     * @param pFactory the digest factory.
     */
    protected void setDigestFactory(final GordianDigestFactory pFactory)  {
        theDigestFactory = pFactory;
     }

    @Override
    public GordianCipherFactory getCipherFactory() {
        return theCipherFactory;
    }

    /**
     * Set the cipher factory.
     * @param pFactory the cipher factory.
     */
    protected void setCipherFactory(final GordianCipherFactory pFactory) {
        theCipherFactory = pFactory;
    }

    @Override
    public GordianMacFactory getMacFactory() {
        return theMacFactory;
    }

    /**
     * Set the mac factory.
     * @param pFactory the mac factory.
     */
    protected void setMacFactory(final GordianMacFactory pFactory) {
        theMacFactory = pFactory;
    }

    @Override
    public GordianRandomFactory getRandomFactory() {
        return theRandomFactory;
    }

    /**
     * Set the random factory.
     * @param pFactory the random factory.
     */
    protected void setRandomFactory(final GordianRandomFactory pFactory) {
        theRandomFactory = pFactory;
    }

    @Override
    public GordianKeySetFactory getKeySetFactory() {
        return theKeySetFactory;
    }

    /**
     * Set the keySet factory.
     * @param pFactory the keySet factory.
     */
    protected void setKeySetFactory(final GordianKeySetFactory pFactory) {
        theKeySetFactory = pFactory;
    }

    @Override
    public GordianLockFactory getLockFactory() {
        return theLockFactory;
    }

    /**
     * Set the lock factory.
     * @param pFactory the lock factory.
     */
    protected void setLockFactory(final GordianLockFactory pFactory) {
        theLockFactory = pFactory;
    }

    /**
     * Obtain the validator.
     * @return the validator
     */
    public GordianValidator getValidator() {
        return theValidator;
    }

    /**
     * Set the validator.
     * @param pValidator the validator.
     */
    protected void setValidator(final GordianValidator pValidator) {
        theValidator = pValidator;
    }

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

    /**
     * Obtain Identifier for DigestSpec.
     * @param pSpec the digestSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianDigestSpec pSpec) {
        return getDigestAlgIds().getIdentifierForSpec(pSpec);
    }

    /**
     * Obtain DigestSpec for Identifier.
     * @param pIdentifier the identifier.
     * @return the digestSpec (or null if not found)
     */
    public GordianDigestSpec getDigestSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return getDigestAlgIds().getSpecForIdentifier(pIdentifier);
    }

    /**
     * Obtain the digest algorithm Ids.
     * @return the digest Algorithm Ids
     */
    private GordianDigestAlgId getDigestAlgIds() {
        if (theDigestAlgIds == null) {
            theDigestAlgIds = new GordianDigestAlgId(this);
        }
        return theDigestAlgIds;
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
        if (!(pThat instanceof GordianCoreFactory)) {
            return false;
        }

        /* Access the target field */
        final GordianCoreFactory myThat = (GordianCoreFactory) pThat;

        /* Check Differences */
        return theParameters.equals(myThat.theParameters);
    }

    @Override
    public int hashCode() {
        return theParameters.hashCode();
    }

    /**
     * Build Invalid text string.
     * @param pValue the parameter
     * @return the text
     */
    public static String getInvalidText(final Object pValue) {
        /* Create initial string */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append("Invalid ");

        /* Build details */
        if (pValue != null) {
            myBuilder.append(pValue.getClass().getSimpleName());
            myBuilder.append(" :- ");
            myBuilder.append(pValue);
        } else {
            myBuilder.append("null value");
        }

        /* Return the string */
        return myBuilder.toString();
    }
}
