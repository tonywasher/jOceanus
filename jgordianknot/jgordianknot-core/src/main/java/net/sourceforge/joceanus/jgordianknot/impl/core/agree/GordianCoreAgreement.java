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

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
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
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.key.GordianCoreKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianPublicKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetSpecASN1;
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
    static final String ERROR_INVSPEC = "Incorrect AgreementSpec";

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The agreementSpec.
     */
    private final GordianAgreementSpec theSpec;

    /**
     * The resultType.
     */
    private Object theResultType;

    /**
     * The keyDerivation function.
     */
    private DerivationFunction theKDF;

    /**
     * The initVector.
     */
    private byte[] theInitVector;

    /**
     * The agreed result.
     */
    private Object theResult;

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

    @Override
    public Object getResultType() {
        return theResultType;
    }

    @Override
    public Object getResult() {
        final Object myResult = theResult;
        theResult = null;
        return myResult;
    }

    @Override
    public void setResultType(final Object pResultType) {
        /* Handle Factory request */
        if (pResultType instanceof GordianFactoryType
            || pResultType instanceof GordianKeySetSpec
            || pResultType instanceof GordianSymCipherSpec
            || pResultType instanceof GordianStreamCipherSpec) {
            theResultType = pResultType;
        } else {
            throw new IllegalArgumentException("Invalid resultType");
        }
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
     * Create and return a new initVector.
     * @return the initVector
     * @throws OceanusException on error
     */
    byte[] newInitVector() throws OceanusException {
        /* Create a new initVector */
        theInitVector = new byte[INITLEN];
        getRandom().nextBytes(theInitVector);
        return theInitVector;
    }

    /**
     * Store initVector.
     * @param pInitVector the initVector
     */
    void storeInitVector(final byte[] pInitVector) {
        /* Store the initVector */
        theInitVector = pInitVector;
    }

    /**
     * Store secret.
     * @param pSecret the secret
     * @throws OceanusException on error
     */
    protected void storeSecret(final byte[] pSecret) throws OceanusException {
        /* If we have a kdf */
        if (theKDF != null) {
            /* Create KDF Parameters */
            final KDFParameters myParms = new KDFParameters(pSecret, new byte[0]);
            theKDF.init(myParms);

            /* Create the secret */
            final byte[] mySecret = new byte[pSecret.length];
            theKDF.generateBytes(mySecret, 0, mySecret.length);
            processSecret(mySecret);
            Arrays.fill(mySecret, (byte) 0);

        } else {
            /* Just process the secret */
            processSecret(pSecret);
        }

        /* Clear the secret */
        Arrays.fill(pSecret, (byte) 0);
        Arrays.fill(theInitVector, (byte) 0);
        theInitVector = null;
    }

    /**
     * Process the secret.
     * @param pSecret the secret
     * @throws OceanusException on error
     */
    private void processSecret(final byte[] pSecret) throws OceanusException {
        /* If the resultType is a FactoryType */
        if (theResultType instanceof GordianFactoryType) {
            /* derive the factory */
            theResult = deriveFactory((GordianFactoryType) theResultType, pSecret);

            /* If the resultType is a KeySetSpec */
        } else if (theResultType instanceof GordianKeySetSpec) {
            /* Derive the keySet */
            theResult = deriveKeySet((GordianKeySetSpec) theResultType, pSecret);

            /* If the resultType is a SymCipherSpec */
        } else if (theResultType instanceof GordianSymCipherSpec) {
            /* Derive the key */
            final GordianSymCipherSpec myCipherSpec = (GordianSymCipherSpec) theResultType;
            theResult = deriveKey(myCipherSpec.getKeyType(), pSecret);

            /* If the resultType is a StreamCipherSpec */
        } else if (theResultType instanceof GordianStreamCipherSpec) {
            /* Derive the key */
            final GordianStreamCipherSpec myCipherSpec = (GordianStreamCipherSpec) theResultType;
            theResult = deriveKey(myCipherSpec.getKeyType(), pSecret);
        }
    }

    /**
     * Derive a keySet from the secret.
     * @param pSpec the keySetSpec
     * @param pSecret the secret
     * @return the derived keySet
     * @throws OceanusException on error
     */
    private GordianKeySet deriveKeySet(final GordianKeySetSpec pSpec,
                                       final byte[] pSecret) throws OceanusException {
        final GordianCoreKeySetFactory myKeySets = (GordianCoreKeySetFactory) theFactory.getKeySetFactory();
        final GordianCoreKeySet myKeySet = myKeySets.createKeySet(pSpec);
        myKeySet.buildFromSecret(pSecret, theInitVector);
        return myKeySet;
    }

    /**
     * Derive a key from the secret.
     * @param <T> the key type
     * @param pKeyType the keyType
     * @param pSecret the secret
     * @return the key
     * @throws OceanusException on error
     */
    private <T extends GordianKeySpec> GordianKey<T> deriveKey(final T pKeyType,
                                                               final byte[] pSecret) throws OceanusException {
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        final GordianCoreKeyGenerator<T> myGenerator = (GordianCoreKeyGenerator<T>) myCiphers.getKeyGenerator(pKeyType);
        return myGenerator.generateKeyFromSecret(pSecret, theInitVector);
    }

    /**
     * Derive factory from the secret.
     * @param pFactoryType the factory type
     * @param pSecret the secret
     * @return the factory
     * @throws OceanusException on error
     */
    private GordianFactory deriveFactory(final GordianFactoryType pFactoryType,
                                         final byte[] pSecret) throws OceanusException {
        /* The phrase is built from the combination of secret and initVector */
        final int myPhraseLen = pSecret.length + theInitVector.length;
        final byte[] myPhrase = new byte[myPhraseLen];

        /* Ensure that we clear out the phrase */
        try {
            /* Build the phrase */
            System.arraycopy(pSecret, 0, myPhrase, 0, pSecret.length);
            System.arraycopy(theInitVector, 0, myPhrase, pSecret.length, theInitVector.length);

            /* Create a new Factory using the phrase */
            final GordianParameters myParms = new GordianParameters(pFactoryType);
            myParms.setSecurityPhrase(myPhrase);
            return theFactory.newFactory(myParms);
        } finally {
            Arrays.fill(myPhrase, (byte) 0);
        }
    }

    /**
     * Obtain identifier for result.
     * @return the identifier
     * @throws OceanusException on error
     */
    AlgorithmIdentifier getIdentifierForResult() throws OceanusException {
        if (theResultType instanceof GordianFactoryType) {
            final ASN1ObjectIdentifier myId = GordianFactoryType.BC == theResultType
                                                   ? GordianCoreFactory.BCFACTORYOID
                                                   : GordianCoreFactory.JCAFACTORYOID;
            return new AlgorithmIdentifier(myId);
        }
        if (theResultType instanceof GordianKeySetSpec) {
            final GordianKeySetSpecASN1 myParms = new GordianKeySetSpecASN1((GordianKeySetSpec) theResultType);
            return myParms.getAlgorithmId();
        }
        if (theResultType instanceof GordianSymCipherSpec) {
            final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
            return myCipherFactory.getIdentifierForSpec((GordianSymCipherSpec) theResultType);
        }
        if (theResultType instanceof GordianStreamCipherSpec) {
            final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
            return myCipherFactory.getIdentifierForSpec((GordianStreamCipherSpec) theResultType);
        }
        throw new GordianDataException("No resultType set");
    }

    /**
     * process result algorithmId.
     * @param pResId the result algorithmId.
     * @throws OceanusException on error
     */
    void processResultIdentifier(final AlgorithmIdentifier pResId) throws OceanusException {
        /* Look for a Factory */
        final ASN1ObjectIdentifier myAlgId = pResId.getAlgorithm();
        if (GordianCoreFactory.BCFACTORYOID.equals(myAlgId)) {
            theResultType = GordianFactoryType.BC;
            return;
        }
        if (GordianCoreFactory.JCAFACTORYOID.equals(myAlgId)) {
            theResultType = GordianFactoryType.JCA;
            return;
        }

        /* Look for a keySet Spec */
        if (GordianKeySetSpecASN1.KEYSETALGID.equals(myAlgId)) {
            theResultType = GordianKeySetSpecASN1.getInstance(pResId.getParameters()).getSpec();
            return;
        }

        /* Look for a symCipher Spec */
        final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
        Object myType = myCipherFactory.getSymSpecForIdentifier(pResId);
        if (myType != null) {
            theResultType = myType;
            return;
        }

        /* Look for a streamCipher Spec */
        myType = myCipherFactory.getStreamSpecForIdentifier(pResId);
        if (myType != null) {
            theResultType = myType;
            return;
        }
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
