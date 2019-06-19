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
package net.sourceforge.joceanus.jgordianknot.impl.core.base;

import java.security.SecureRandom;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
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
     *  Base our ids off bouncyCastle.
     */
    public static final ASN1ObjectIdentifier BASEOID = BCObjectIdentifiers.bc.branch("100");

    /**
     *  Create the factoryId.
     */
    private static final ASN1ObjectIdentifier FACTORYOID = BASEOID.branch("1");

    /**
     *  Create the BC factoryId.
     */
    public static final ASN1ObjectIdentifier BCFACTORYOID = FACTORYOID.branch("1");

    /**
     *  Create the JCA factoryId.
     */
    public static final ASN1ObjectIdentifier JCAFACTORYOID = FACTORYOID.branch("2");

    /**
     *  Create the NULL factoryId.
     */
    public static final ASN1ObjectIdentifier NULLFACTORYOID = FACTORYOID.branch("3");

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
    }

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
     * Obtain the number of iterations.
     * @return the number of iterations
     */
    public int getNumIterations() {
        return theParameters.getNumIterations();
    }

    /**
     * Obtain the security phrase.
     * @return the security phrase
     */
    public byte[] getSecurityPhrase() {
        return theParameters.getSecurityPhrase();
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
