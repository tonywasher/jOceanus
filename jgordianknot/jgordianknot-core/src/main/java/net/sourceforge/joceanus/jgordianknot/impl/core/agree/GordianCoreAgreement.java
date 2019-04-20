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
package net.sourceforge.joceanus.jgordianknot.impl.core.agree;

import java.security.SecureRandom;

import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.key.GordianCoreKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianPublicKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySetFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Key Agreement Specification.
 */
public abstract class GordianCoreAgreement
    implements GordianAgreement {
    /**
     * InitVectorLength.
     */
    private static final int INITLEN = 32;

    /**
     * Invalid AgreementSpec message.
     */
    protected static final String ERROR_INVSPEC = "Incorrect AgreementSpec";

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The agreementSpec.
     */
    private final GordianAgreementSpec theSpec;

    /**
     * The keyDerivation function.
     */
    private DerivationFunction theKDF;

    /**
     * The shared secret.
     */
    private byte[] theSecret;

    /**
     * The initVector.
     */
    private byte[] theInitVector;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     */
    GordianCoreAgreement(final GordianCoreFactory pFactory,
                         final GordianAgreementSpec pSpec) {
        theFactory = pFactory;
        theSpec = pSpec;
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected GordianCoreFactory getFactory() {
        return theFactory;
    }

    /**
     * Obtain the agreement factory.
     * @return the factory
     */
    protected GordianCoreAgreementFactory getAgreementFactory() {
        return (GordianCoreAgreementFactory) theFactory.getAsymmetricFactory().getAgreementFactory();
    }

    @Override
    public GordianAgreementSpec getAgreementSpec() {
        return theSpec;
    }

    /**
     * Obtain the random.
     * @return the random
     */
    protected SecureRandom getRandom() {
        return theFactory.getRandomSource().getRandom();
    }

    /**
     * CheckKeyPair.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    protected void checkKeyPair(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check that the KeyPair is valid */
        final GordianAsymFactory myAsym = theFactory.getAsymmetricFactory();
        final GordianAgreementFactory myAgrees = myAsym.getAgreementFactory();
        if (!myAgrees.validAgreementSpecForKeyPair(pKeyPair, theSpec)) {
            throw new GordianDataException("Incorrect KeyPair type");
        }
    }

    /**
     * Obtain public key from pair.
     * @param pKeyPair the keyPair
     * @return the public key
     */
    protected GordianPublicKey getPublicKey(final GordianKeyPair pKeyPair) {

        return ((GordianCoreKeyPair) pKeyPair).getPublicKey();
    }

    /**
     * Obtain private key from pair.
     * @param pKeyPair the keyPair
     * @return the private key
     * @throws OceanusException on error
     */
    protected GordianPrivateKey getPrivateKey(final GordianKeyPair pKeyPair) throws OceanusException {
        if (pKeyPair.isPublicOnly()) {
            throw new GordianDataException("missing privateKey");
        }
        return ((GordianCoreKeyPair) pKeyPair).getPrivateKey();
    }

    /**
     * Create a new initVector.
     * @return the initVector
     */
    byte[] newInitVector() {
        theInitVector = new byte[INITLEN];
        getRandom().nextBytes(theInitVector);
        return Arrays.copyOf(theInitVector, theInitVector.length);
    }

    /**
     * Store initVector.
     * @param pInitVector the initVectore
     */
    void storeInitVector(final byte[] pInitVector) {
        /* Store the details */
        theInitVector = Arrays.copyOf(pInitVector, pInitVector.length);
    }

    /**
     * Store secret.
     * @param pSecret the secret
     */
    protected void storeSecret(final byte[] pSecret) {
        /* If we have a kdf */
        if (theKDF != null) {
            /* Create KDF Parameters */
            final KDFParameters myParms = new KDFParameters(pSecret, new byte[0]);
            theKDF.init(myParms);

            /* Create the secret */
            theSecret = new byte[pSecret.length];
            theKDF.generateBytes(theSecret, 0, theSecret.length);
        } else {
            /* Just store the secret */
            theSecret = Arrays.copyOf(pSecret, pSecret.length);
        }

        /* Clear the secret */
        Arrays.fill(pSecret, (byte) 0);
    }

    @Override
    public GordianKeySet deriveKeySet(final GordianKeySetSpec pSpec) throws OceanusException {
        final GordianCoreKeySetFactory myKeySets = (GordianCoreKeySetFactory) theFactory.getKeySetFactory();
        final GordianCoreKeySet myKeySet = myKeySets.createKeySet(pSpec);
        myKeySet.buildFromSecret(theSecret, theInitVector);
        return myKeySet;
    }

    @Override
    public <T extends GordianKeySpec> GordianKey<T> deriveKey(final T pKeyType) throws OceanusException {
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        final GordianCoreKeyGenerator<T> myGenerator = (GordianCoreKeyGenerator<T>) myCiphers.getKeyGenerator(pKeyType);
        return myGenerator.generateKeyFromSecret(theSecret, theInitVector);
    }

    @Override
    public GordianKeySet deriveIndependentKeySet(final GordianKeySetSpec pSpec) throws OceanusException {
        /* The phrase is built of the first quarter of the secret and the first quarter of the initVector */
        final int myPhraseSecLen = theSecret.length >> 2;
        final int myPhraseIVLen = theInitVector.length >> 2;

        /* Build the phrase */
        final byte[] myPhrase = new byte[myPhraseSecLen + myPhraseIVLen];
        System.arraycopy(theSecret, 0, myPhrase, 0, myPhraseSecLen);
        System.arraycopy(theInitVector, 0, myPhrase,  myPhraseSecLen, myPhraseIVLen);

        /* Access shortened secret and IV */
        final byte[] mySecret = Arrays.copyOfRange(theSecret, myPhraseSecLen, theSecret.length);
        final byte[] myIV = Arrays.copyOfRange(theInitVector, myPhraseIVLen, theInitVector.length);

        /* Create a new Factory using the phrase */
        final GordianParameters myParms = new GordianParameters();
        myParms.setSecurityPhrase(myPhrase);
        final GordianCoreFactory myFactory = (GordianCoreFactory) theFactory.newFactory(myParms);

        /* Create the keySet */
        final GordianCoreKeySetFactory myKeySets = (GordianCoreKeySetFactory) myFactory.getKeySetFactory();
        final GordianCoreKeySet myKeySet = myKeySets.createKeySet(pSpec);
        myKeySet.buildFromSecret(mySecret, myIV);
        return myKeySet;
    }

    /**
     * Enable additional derivation of secret.
     */
    protected void enableDerivation() {
        /* Only enable derivation if it is not none */
        if (!GordianKDFType.NONE.equals(getAgreementSpec().getKDFType())) {
            theKDF = newDerivationFunction();
        }
    }

    /**
     * Obtain the required derivation function.
     * @return the derivation function
     */
    protected DerivationFunction newDerivationFunction() {
        switch (getAgreementSpec().getKDFType()) {
            case SHA256KDF:
                return new KDF2BytesGenerator(new SHA256Digest());
            case SHA512KDF:
                return new KDF2BytesGenerator(new SHA512Digest());
            case SHA256CKDF:
                return new ConcatenationKDFGenerator(new SHA256Digest());
            case SHA512CKDF:
                return new ConcatenationKDFGenerator(new SHA512Digest());
            case NONE:
            default:
                return new GordianNullKeyDerivation();
        }
    }

    /**
     * NullKeyDerivation.
     */
    public static final class GordianNullKeyDerivation
            implements DerivationFunction {
        /**
         * The key.
         */
        private byte[] theKey;

        @Override
        public int generateBytes(final byte[] pBuffer,
                                 final int pOffset,
                                 final int pLength) {
            /* Create the array that is to be copied */
            final byte[] myKey = Arrays.copyOf(theKey, pLength);
            Arrays.fill(theKey, (byte) 0);
            System.arraycopy(myKey, 0, pBuffer, pOffset, pLength);
            Arrays.fill(myKey, (byte) 0);
            return pLength;
        }

        @Override
        public void init(final DerivationParameters pParms) {
            final byte[] mySecret = ((KDFParameters) pParms).getSharedSecret();
            theKey = Arrays.copyOf(mySecret, mySecret.length);
        }
    }
}
