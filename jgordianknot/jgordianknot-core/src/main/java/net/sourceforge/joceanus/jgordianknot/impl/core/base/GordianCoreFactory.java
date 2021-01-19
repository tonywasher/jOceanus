/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.base;

import java.security.SecureRandom;
import java.util.function.Predicate;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Base factory.
 */
public abstract class GordianCoreFactory
    implements GordianFactory, GordianFactoryGenerator {
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
     * @throws OceanusException on error
     */
    protected GordianCoreFactory(final GordianFactoryGenerator pGenerator,
                                 final GordianParameters pParameters) throws OceanusException {
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
    }

    /**
     * Declare factories.
     * @throws OceanusException on error
     */
    protected abstract void declareFactories() throws OceanusException;

    @Override
    public GordianFactory newFactory(final GordianParameters pParameters) throws OceanusException {
        return theGenerator.newFactory(pParameters);
    }

    /**
     * Obtain the random source.
     * @return the random source
     */
    public GordianRandomSource getRandomSource() {
        return theRandom;
    }

    @Override
    public GordianFactoryType getFactoryType() {
        return theParameters.getFactoryType();
    }

    /**
     * Obtain the security phrase.
     * @return the security phrase
     */
    public byte[] getSecurityPhrase() {
        return theParameters.getSecurityPhrase();
    }

    /**
     * Is this an internal factory?
     * @return true/false
     */
    public boolean isInternal() {
        return theParameters.isInternal();
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
        if (myRandom instanceof GordianSeededRandom) {
            /* Access the seeded random */
            final GordianSeededRandom mySeeded = (GordianSeededRandom) myRandom;

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

    Predicate<GordianDigestType> supportedKeySetDigestTypes() {
        final GordianMacFactory myMacs = getMacFactory();
        return myMacs.supportedHMacDigestTypes().and(GordianDigestType::isCombinedHashDigest);
    }

    /**
     * Obtain predicate for supported keySet symKeySpecs.
     * @param pKeyLen the keyLength
     * @return the predicate
     */
    public Predicate<GordianSymKeySpec> supportedKeySetSymKeySpecs(final GordianLength pKeyLen) {
        return s -> supportedKeySetSymKeyTypes(pKeyLen).test(s.getSymKeyType())
                && s.getBlockLength() == GordianLength.LEN_128;
    }

    /**
     * Obtain predicate for keySet SymKeyTypes.
     * @param pKeyLen the keyLength
     * @return the predicate
     */
    public Predicate<GordianSymKeyType> supportedKeySetSymKeyTypes(final GordianLength pKeyLen) {
        return t -> validKeySetSymKeyType(t, pKeyLen);
    }

    /**
     * check valid keySet symKeyType.
     * @param pKeyType the symKeyType
     * @param pKeyLen the keyLength
     * @return true/false
     */
    private boolean validKeySetSymKeyType(final GordianSymKeyType pKeyType,
                                          final GordianLength pKeyLen) {
        return validSymKeyType(pKeyType)
                && validStdBlockSymKeyTypeForKeyLength(pKeyType, pKeyLen);
    }

    /**
     * Check standard block symKeyType.
     * @param pKeyType the symKeyType
     * @param pKeyLen the keyLength
     * @return true/false
     */
    public static boolean validStdBlockSymKeyTypeForKeyLength(final GordianSymKeyType pKeyType,
                                                              final GordianLength pKeyLen) {
        return validSymKeyTypeForKeyLength(pKeyType, pKeyLen)
                && pKeyType.getDefaultBlockLength().equals(GordianLength.LEN_128);
    }

    /**
     * Check SymKeyType.
     * @param pKeyType the symKeyType
     * @param pKeyLen the keyLength
     * @return true/false
     */
    public static boolean validSymKeyTypeForKeyLength(final GordianSymKeyType pKeyType,
                                                      final GordianLength pKeyLen) {
        return pKeyType.validForKeyLength(pKeyLen);
    }

    /**
     * Check SymKeyType.
     * @param pKeyType the symKeyType
     * @return true/false
     */
    public boolean validSymKeyType(final GordianSymKeyType pKeyType) {
        return pKeyType != null;
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
            myBuilder.append(pValue.toString());
        } else {
            myBuilder.append("null value");
        }

        /* Return the string */
        return myBuilder.toString();
    }
}
