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
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

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
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianParameters;
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
    public void setResultType(final Object pResultType) throws OceanusException {
        /* Check result Type */
        checkResultType(pResultType);
        theResultType = pResultType;
    }

    /**
     * Check the resultType is valid.
     * @param pResultType the resultType
     * @throws OceanusException on error
     */
    private void checkResultType(final Object pResultType) throws OceanusException {
        /* No need to check FactoryType or null */
        if (pResultType instanceof GordianFactoryType
            || pResultType == null) {
            return;
        }

        /* Validate a keySetSpec */
        if (pResultType instanceof GordianKeySetSpec) {
            /* Check Spec */
            final GordianCoreKeySetFactory myKeySetFactory = (GordianCoreKeySetFactory) theFactory.getKeySetFactory();
            myKeySetFactory.checkKeySetSpec((GordianKeySetSpec) pResultType);
            return;
        }

        /* Validate a symCipherSpec */
        if (pResultType instanceof GordianSymCipherSpec) {
            /* Check Spec */
            final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
            myCipherFactory.checkSymCipherSpec((GordianSymCipherSpec) pResultType);
            return;
        }

        /* Validate a streamCipherSpec */
        if (pResultType instanceof GordianStreamCipherSpec) {
            /* Check Spec */
            final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
            myCipherFactory.checkStreamCipherSpec((GordianStreamCipherSpec) pResultType);
            return;
        }

        /* Invalid resultType */
        throw new GordianLogicException("Invalid resultType");
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
     */
    byte[] newInitVector() {
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
        /* Protect against failure */
        final byte[] mySecret = new byte[pSecret.length];
        try {
            /* If we have a kdf */
            if (theKDF != null) {
                /* Create KDF Parameters */
                final KDFParameters myParms = new KDFParameters(pSecret, new byte[0]);
                theKDF.init(myParms);

                /* Create the secret */
                theKDF.generateBytes(mySecret, 0, mySecret.length);
                processSecret(mySecret);

            } else {
                /* Just process the secret */
                processSecret(pSecret);
            }

            /* Clear buffers */
        } finally {
            /* Clear the secret */
            Arrays.fill(mySecret, (byte) 0);
            Arrays.fill(pSecret, (byte) 0);
            Arrays.fill(theInitVector, (byte) 0);
            theInitVector = null;
        }
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
            theResult = deriveCipher(myCipherSpec, pSecret);

            /* If the resultType is a StreamCipherSpec */
        } else if (theResultType instanceof GordianStreamCipherSpec) {
            /* Derive the key */
            final GordianStreamCipherSpec myCipherSpec = (GordianStreamCipherSpec) theResultType;
            theResult = deriveCipher(myCipherSpec, pSecret);

            /* If the resultType is pure bytes */
        } else if (theResultType == null) {
            /* Derive the secret */
            theResult = deriveBasicResult(pSecret);
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
        /* Derive a shared factory */
        final GordianFactory myFactory = deriveFactory(GordianFactoryType.BC, pSecret);

        /* Allocate the buffers */
        final int myLen = GordianLength.LEN_256.getByteLength();
        final byte[] myBase = new byte[GordianLength.LEN_512.getByteLength()];
        final byte[] mySecret = new byte[myLen];
        final byte[] myIV = new byte[myLen];

        /* Ensure that we clear out the phrase */
        try {
            /* Calculate the secret */
            calculateDerivedSecret(GordianDigestType.SHA3, pSecret, myBase);

            /* Split into secret and IV */
            System.arraycopy(myBase, 0, mySecret, 0, myLen);
            System.arraycopy(myBase, myLen, myIV, 0, myLen);

            /* Derive the keySet */
            final GordianCoreKeySetFactory myKeySets = (GordianCoreKeySetFactory) myFactory.getKeySetFactory();
            final GordianCoreKeySet myKeySet = myKeySets.createKeySet(pSpec);
            myKeySet.buildFromSecret(mySecret, myIV);
            return myKeySet;

            /* Clear buffers */
        } finally {
            Arrays.fill(myBase, (byte) 0);
            Arrays.fill(mySecret, (byte) 0);
            Arrays.fill(myIV, (byte) 0);
        }
    }

    /**
     * Derive a symKeyCipher pair from the secret.
     * @param pCipherSpec the cipherSpec
     * @param pSecret the secret
     * @return the ciphers
     * @throws OceanusException on error
     */
    private GordianSymCipher[] deriveCipher(final GordianSymCipherSpec pCipherSpec,
                                            final byte[] pSecret) throws OceanusException {
        /* Derive a shared factory */
        final GordianFactory myFactory = deriveFactory(GordianFactoryType.BC, pSecret);

        /* Generate the key */
        final GordianKey<GordianSymKeySpec> myKey = deriveKey(myFactory, pCipherSpec.getKeyType(), pSecret);

        /* Create the cipher and initialise with key */
        final GordianCipherFactory myCiphers = myFactory.getCipherFactory();
        final GordianSymCipher myOutCipher = myCiphers.createSymKeyCipher(pCipherSpec);
        final GordianSymCipher myInCipher = myCiphers.createSymKeyCipher(pCipherSpec);
        if (pCipherSpec.needsIV()) {
            /* Calculate the IV */
            final byte[] myIV = new byte[pCipherSpec.getIVLength()];
            calculateDerivedIV(pSecret, myIV);
            myOutCipher.init(true, GordianCipherParameters.keyAndNonce(myKey, myIV));
            myInCipher.init(false, GordianCipherParameters.keyAndNonce(myKey, myIV));
        } else {
            myOutCipher.init(true, GordianCipherParameters.key(myKey));
            myInCipher.init(true, GordianCipherParameters.key(myKey));
        }
        return new GordianSymCipher[] { myOutCipher, myInCipher };
    }

    /**
     * Derive a streamKeyCipher pair from the secret.
     * @param pCipherSpec the cipherSpec
     * @param pSecret the secret
     * @return the ciphers
     * @throws OceanusException on error
     */
    private GordianStreamCipher[] deriveCipher(final GordianStreamCipherSpec pCipherSpec,
                                               final byte[] pSecret) throws OceanusException {
        /* Derive a shared factory */
        final GordianFactory myFactory = deriveFactory(GordianFactoryType.BC, pSecret);

        /* Generate the key */
        final GordianKey<GordianStreamKeySpec> myKey = deriveKey(myFactory, pCipherSpec.getKeyType(), pSecret);

        /* Create the cipher and initialise with key */
        final GordianCipherFactory myCiphers = myFactory.getCipherFactory();
        final GordianStreamCipher myOutCipher = myCiphers.createStreamKeyCipher(pCipherSpec);
        final GordianStreamCipher myInCipher = myCiphers.createStreamKeyCipher(pCipherSpec);
        if (pCipherSpec.needsIV()) {
            /* Calculate the IV */
            final byte[] myIV = new byte[pCipherSpec.getIVLength()];
            calculateDerivedIV(pSecret, myIV);
            myOutCipher.init(true, GordianCipherParameters.keyAndNonce(myKey, myIV));
            myInCipher.init(false, GordianCipherParameters.keyAndNonce(myKey, myIV));
        } else {
            myOutCipher.init(true, GordianCipherParameters.key(myKey));
            myInCipher.init(true, GordianCipherParameters.key(myKey));
        }
        return new GordianStreamCipher[] { myOutCipher, myInCipher };
    }

    /**
     * Derive a key from the secret.
     * @param <T> the key type
     * @param pFactory the factory
     * @param pKeyType the keyType
     * @param pSecret the secret
     * @return the key
     * @throws OceanusException on error
     */
    private <T extends GordianKeySpec> GordianKey<T> deriveKey(final GordianFactory pFactory,
                                                               final T pKeyType,
                                                               final byte[] pSecret) throws OceanusException {
        /* Allocate the buffers */
        final int myLen = GordianLength.LEN_256.getByteLength();
        final byte[] myBase = new byte[GordianLength.LEN_512.getByteLength()];
        final byte[] mySecret = new byte[myLen];
        final byte[] myIV = new byte[myLen];

        /* Ensure that we clear out the phrase */
        try {
            /* Calculate the secret */
            calculateDerivedSecret(GordianDigestType.SKEIN, pSecret, myBase);

            /* Split into secret and IV */
            System.arraycopy(myBase, 0, mySecret, 0, myLen);
            System.arraycopy(myBase, myLen, myIV, 0, myLen);

            /* Derive the key */
            final GordianCipherFactory myCiphers = pFactory.getCipherFactory();
            final GordianCoreKeyGenerator<T> myGenerator = (GordianCoreKeyGenerator<T>) myCiphers.getKeyGenerator(pKeyType);
            return myGenerator.generateKeyFromSecret(mySecret, myIV);

            /* Clear buffers */
        } finally {
            Arrays.fill(myBase, (byte) 0);
            Arrays.fill(mySecret, (byte) 0);
            Arrays.fill(myIV, (byte) 0);
        }
    }

    /**
     * Derive factory from the secret.
     * @param pFactoryType the factoryType
     * @param pSecret the secret
     * @return the factory
     * @throws OceanusException on error
     */
    private GordianFactory deriveFactory(final GordianFactoryType pFactoryType,
                                         final byte[] pSecret) throws OceanusException {
        /* Allocate the buffer */
        final byte[] myPhrase = new byte[GordianLength.LEN_512.getByteLength()];

        /* Ensure that we clear out the phrase */
        try {
            /* Calculate the phrase */
            calculateDerivedSecret(GordianDigestType.BLAKE, pSecret, myPhrase);

            /* Create a new Factory using the phrase */
            final GordianParameters myParams = new GordianParameters(pFactoryType);
            myParams.setSecurityPhrase(myPhrase);
            myParams.setInternal();
            return theFactory.newFactory(myParams);

            /* Clear buffer */
        } finally {
            Arrays.fill(myPhrase, (byte) 0);
        }
    }

    /**
     * Derive basic result.
     * @param pSecret the secret
     * @return the factory
     * @throws OceanusException on error
     */
    private byte[] deriveBasicResult(final byte[] pSecret) throws OceanusException {
        /* Allocate the buffer */
        final byte[] mySecret = new byte[GordianLength.LEN_512.getByteLength()];

        /* Calculate the phrase */
        calculateDerivedSecret(GordianDigestType.STREEBOG, pSecret, mySecret);

        /* Return the secret */
        return mySecret;
    }

    /**
     * Obtain identifier for result.
     * @return the identifier
     * @throws OceanusException on error
     */
    AlgorithmIdentifier getIdentifierForResult() throws OceanusException {
        if (theResultType instanceof GordianFactoryType) {
            final ASN1ObjectIdentifier myOID = theResultType == GordianFactoryType.BC
                                           ? GordianCoreFactory.BCFACTORYOID
                                           : GordianCoreFactory.JCAFACTORYOID;
            return new AlgorithmIdentifier(myOID, null);
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
        if (theResultType == null) {
            return new AlgorithmIdentifier(GordianAgreementAlgId.NULLRESULTOID, null);
        }
        throw new GordianDataException("Illegal resultType set");
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

        /* Look for a cipher Spec */
        final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
        final GordianCipherSpec<?> mySpec = myCipherFactory.getCipherSpecForIdentifier(pResId);
        if (mySpec != null) {
            theResultType = mySpec;
            return;
        }

        /* Look for a Factory */
        if (GordianAgreementAlgId.NULLRESULTOID.equals(myAlgId)) {
            theResultType = null;
            return;
        }
        throw new GordianDataException("Unrecognised resultType");
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
     * Create request.
     * @return the request message
     * @throws OceanusException on error
     */
    protected byte[] createRequest() throws OceanusException {
        return createRequest(null);
    }

    /**
     * Create request.
     * @param pBase the base message
     * @return the request message
     * @throws OceanusException on error
     */
    protected byte[] createRequest(final byte[] pBase) throws OceanusException {
        /* Create the request */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final AlgorithmIdentifier myAlgId = myFactory.getIdentifierForSpec(getAgreementSpec());
        final AlgorithmIdentifier myResId = getIdentifierForResult();
        final GordianAgreementRequestASN1 myRequest = new GordianAgreementRequestASN1(myAlgId, myResId, newInitVector(), pBase);
        return myRequest.getEncodedBytes();
    }

    /**
     * Parse the incoming request message.
     * @param pMessage the incoming request message
     * @return the base message
     * @throws OceanusException on error
     */
    protected byte[] parseRequest(final byte[] pMessage) throws OceanusException {
        /* Parse the sequence */
        try {
            /* Access the sequence */
            final GordianAgreementRequestASN1 myRequest = GordianAgreementRequestASN1.getInstance(pMessage);

            /* Access message parts */
            final AlgorithmIdentifier myAlgId = myRequest.getAgreementId();
            final AlgorithmIdentifier myResId = myRequest.getResultId();
            final byte[] myInitVector = myRequest.getInitVector();
            final byte[] myData = myRequest.getData();

            /* Check agreementSpec */
            final GordianCoreAgreementFactory myFactory = getAgreementFactory();
            final GordianAgreementSpec mySpec = myFactory.getSpecForIdentifier(myAlgId);
            if (!Objects.equals(mySpec, getAgreementSpec())) {
                throw new GordianDataException(ERROR_INVSPEC);
            }

            /* Process result identifier */
            processResultIdentifier(myResId);

            /* Store initVector */
            storeInitVector(myInitVector);

            /* Return the encoded message */
            return myData;

        } catch (IllegalArgumentException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }

    /**
     * Create response.
     * @param pBase the base message
     * @return the response message
     * @throws OceanusException on error
     */
    protected byte[] createResponse(final byte[] pBase) throws OceanusException {
        /* Create the response */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final AlgorithmIdentifier myAlgId = myFactory.getIdentifierForSpec(getAgreementSpec());
        final GordianAgreementResponseASN1 myResponse = new GordianAgreementResponseASN1(myAlgId, pBase);
        return myResponse.getEncodedBytes();
    }

    /**
     * Parse the incoming response message.
     * @param pResponse the response message
     * @return the ephemeral keySpec
     * @throws OceanusException on error
     */
    protected X509EncodedKeySpec parseResponse(final byte[] pResponse) throws OceanusException {
        /* Access the sequence */
        final GordianAgreementResponseASN1 myResponse = GordianAgreementResponseASN1.getInstance(pResponse);

        /* Access message parts */
        final AlgorithmIdentifier myAlgId = myResponse.getAgreementId();
        final byte[] myKeyBytes = myResponse.getData();

        /* Check agreementSpec */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final GordianAgreementSpec mySpec = myFactory.getSpecForIdentifier(myAlgId);
        if (!Objects.equals(mySpec, getAgreementSpec())) {
            throw new GordianDataException(ERROR_INVSPEC);
        }

        /* Return the keySpec */
        return new X509EncodedKeySpec(myKeyBytes);
    }

    /**
     * Calculate the derived secret.
     * @param pDigestType the digestType
     * @param pSecret the secret
     * @param pResult the result buffer
     * @throws OceanusException on error
     */
    protected void calculateDerivedSecret(final GordianDigestType pDigestType,
                                          final byte[] pSecret,
                                          final byte[] pResult) throws OceanusException {
        /* Check that the Result is the correct length */
        if (pResult.length != GordianLength.LEN_512.getByteLength()) {
            throw new IllegalArgumentException("Invalid buffer");
        }

        /* Access the required digest */
        final GordianDigestSpec myDigestSpec = new GordianDigestSpec(pDigestType, GordianLength.LEN_512);
        final GordianDigest myDigest = theFactory.getDigestFactory().createDigest(myDigestSpec);

        /* Update the digest appropriately */
        myDigest.update(pSecret);
        myDigest.update(theInitVector);

        /* Calculate the result */
        myDigest.finish(pResult, 0);
    }

    /**
     * Calculate the derived IV.
     * @param pSecret the secret
     * @param pResult the result buffer
     * @throws OceanusException on error
     */
    protected void calculateDerivedIV(final byte[] pSecret,
                                      final byte[] pResult) throws OceanusException {
        /* Access the required digest */
        final GordianDigestSpec myDigestSpec = new GordianDigestSpec(GordianDigestType.KUPYNA, GordianLength.LEN_512);
        final GordianDigest myDigest = theFactory.getDigestFactory().createDigest(myDigestSpec);
        final byte[] myBuffer = new byte[GordianLength.LEN_512.getByteLength()];

        /* Update the digest appropriately */
        myDigest.update(pSecret);
        myDigest.update(theInitVector);

        /* Calculate the result and copy to result */
        myDigest.finish(myBuffer, 0);
        int bytesLeft = pResult.length;
        int bytesToCopy = Math.min(myBuffer.length, bytesLeft);
        System.arraycopy(myBuffer, 0, pResult, 0, bytesToCopy);
        bytesLeft -= bytesToCopy;

        /* While we need more bytes */
        while (bytesLeft > 0) {
            /* Extend the digest */
            myDigest.update(myBuffer);
            myDigest.finish(myBuffer, 0);
            bytesToCopy = Math.min(myBuffer.length, bytesLeft);
            System.arraycopy(myBuffer, 0, pResult, 0, bytesToCopy);
            bytesLeft -= bytesToCopy;
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
