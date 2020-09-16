/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementStatus;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
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
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianParameters;
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
 * @param <A> the agreement specification
 */
public abstract class GordianCoreAgreement<A>
    implements GordianAgreement<A> {
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
    private final A theSpec;

    /**
     * The keyDerivation function.
     */
    private DerivationFunction theKDF;

    /**
     * The status.
     */
    private GordianAgreementStatus theStatus;

    /**
     * The client initVector.
     */
    private byte[] theClientIV;

    /**
     * The server initVector.
     */
    private byte[] theServerIV;

    /**
     * The resultType.
     */
    private Object theResultType;

    /**
     * The agreed result.
     */
    private Object theResult;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     */
    protected GordianCoreAgreement(final GordianCoreFactory pFactory,
                                   final A pSpec) {
        theFactory = pFactory;
        theSpec = pSpec;
        theStatus = GordianAgreementStatus.CLEAN;
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
        return (GordianCoreAgreementFactory) theFactory.getKeyPairFactory().getAgreementFactory();
    }

    @Override
    public A getAgreementSpec() {
        return theSpec;
    }

    @Override
    public GordianAgreementStatus getStatus() {
        return theStatus;
    }

    /**
     * Set the status.
     * @param pStatus the status
     */
    protected void setStatus(final GordianAgreementStatus pStatus) {
        theStatus = pStatus;
    }

    @Override
    public Object getResultType() {
        return theResultType;
    }

    @Override
    public Object getResult() throws OceanusException {
        /* Must be in result available state */
        checkStatus(GordianAgreementStatus.RESULT_AVAILABLE);

        /* Obtain result to  return and reset the agreement */
        final Object myResult = theResult;
        reset();

        /* return the result */
        return myResult;
    }

    @Override
    public void setResultType(final Object pResultType) throws OceanusException {
        /* Check result Type */
        checkResultType(pResultType);
        theResultType = pResultType;
    }

    @Override
    public void reset() {
        /* Reset the result and status */
        theResult = null;
        setStatus(GordianAgreementStatus.CLEAN);

        /* Reset the client and serverIVs */
        if (theClientIV != null) {
            Arrays.fill(theClientIV, (byte) 0);
            theClientIV = null;
        }
        if (theServerIV != null) {
            Arrays.fill(theServerIV, (byte) 0);
            theServerIV = null;
        }
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
     * Check status.
     * @param pStatus the required status
     * @throws OceanusException on error
     */
    protected void checkStatus(final GordianAgreementStatus pStatus) throws OceanusException {
        /* If we are in the wrong state */
        if (theStatus != pStatus) {
            throw new GordianLogicException("Invalid State: " + theStatus);
        }
    }

    /**
     * CheckKeyPair.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    protected void checkKeyPair(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check that the KeyPair is valid */
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianAgreementFactory myAgrees = myFactory.getAgreementFactory();
        if (!myAgrees.validAgreementSpecForKeyPair(pKeyPair, (GordianKeyPairAgreementSpec) theSpec)) {
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
     * Create and return a new clientIV.
     * @return the initVector
     */
    private byte[] newClientIV() {
        /* Create a new initVector */
        theClientIV = new byte[INITLEN];
        getRandom().nextBytes(theClientIV);
        return theClientIV;
    }

    /**
     * Store client initVector.
     * @param pInitVector the initVector
     */
    protected void storeClientIV(final byte[] pInitVector) {
        /* Store the initVector */
        theClientIV = pInitVector;
    }

    /**
     * Obtain the clientIV.
     * @return the clientIV
     */
    protected byte[] getClientIV() {
        return theClientIV;
    }

    /**
     * Create a new serverIV.
     */
    void newServerIV() {
        /* Create a new initVector */
        theServerIV = new byte[INITLEN];
        getRandom().nextBytes(theServerIV);
    }

    /**
     * Store server initVector.
     * @param pInitVector the initVector
     */
    private void storeServerIV(final byte[] pInitVector) {
        /* Store the initVector */
        theServerIV = pInitVector;
    }

    /**
     * Obtain the serverIV.
     * @return the serverIV
     */
    protected byte[] getServerIV() {
        return theServerIV;
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
        }
    }

    /**
     * Process the secret.
     * @param pSecret the secret
     * @throws OceanusException on error
     */
    protected void processSecret(final byte[] pSecret) throws OceanusException {
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

        /* Set status */
        setStatus(GordianAgreementStatus.RESULT_AVAILABLE);
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
            myOutCipher.initForEncrypt(GordianCipherParameters.keyAndNonce(myKey, myIV));
            myInCipher.initForDecrypt(GordianCipherParameters.keyAndNonce(myKey, myIV));
        } else {
            myOutCipher.initForEncrypt(GordianCipherParameters.key(myKey));
            myInCipher.initForDecrypt(GordianCipherParameters.key(myKey));
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

        /* Create the ciphers */
        final GordianCipherFactory myCiphers = myFactory.getCipherFactory();
        final GordianStreamCipher myOutCipher = myCiphers.createStreamKeyCipher(pCipherSpec);
        final GordianStreamCipher myInCipher = myCiphers.createStreamKeyCipher(pCipherSpec);

        /* If we need an IV */
        if (pCipherSpec.needsIV()) {
            /* Calculate the IV */
            final byte[] myIV = new byte[pCipherSpec.getIVLength()];
            calculateDerivedIV(pSecret, myIV);

            /* Initialise the ciphers */
            final GordianCipherParameters myParms = GordianCipherParameters.keyAndNonce(myKey, myIV);
            myOutCipher.initForEncrypt(myParms);
            myInCipher.initForDecrypt(myParms);

            /* else no IV */
        } else {
            /* Initialise the ciphers */
            final GordianCipherParameters myParms = GordianCipherParameters.key(myKey);
            myOutCipher.initForEncrypt(myParms);
            myInCipher.initForDecrypt(myParms);
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
    protected AlgorithmIdentifier getIdentifierForResult() throws OceanusException {
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
    public void processResultIdentifier(final AlgorithmIdentifier pResId) throws OceanusException {
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
        final GordianKeyPairAgreementSpec mySpec = (GordianKeyPairAgreementSpec) getAgreementSpec();
        if (!GordianKDFType.NONE.equals(mySpec.getKDFType())) {
            theKDF = newDerivationFunction();
        }
    }

    /**
     * Obtain the required derivation function.
     * @return the derivation function
     */
    protected DerivationFunction newDerivationFunction() {
        final GordianKeyPairAgreementSpec mySpec = (GordianKeyPairAgreementSpec) getAgreementSpec();
        switch (mySpec.getKDFType()) {
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
     * Build clientHello message.
     * @return the clientHello message
     * @throws OceanusException on error
     */
    protected byte[] buildClientHello() throws OceanusException {
        return buildClientHello(null);
    }

    /**
     * Build clientHello message.
     * @param pEncapsulated the encapsulated message
     * @return the clientHello message
     * @throws OceanusException on error
     */
    protected byte[] buildClientHello(final byte[] pEncapsulated) throws OceanusException {
        /* Must be in clean state */
        checkStatus(GordianAgreementStatus.CLEAN);

        /* Create the clientHello */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final GordianKeyPairAgreementSpec mySpec = (GordianKeyPairAgreementSpec) getAgreementSpec();
        final AlgorithmIdentifier myAlgId = myFactory.getIdentifierForSpec(mySpec);
        final AlgorithmIdentifier myResId = getIdentifierForResult();
        final GordianAgreementClientHelloASN1 myClientHello
                = new GordianAgreementClientHelloASN1(myAlgId, myResId, newClientIV(), pEncapsulated);
        return myClientHello.getEncodedBytes();
    }

    /**
     * Parse the incoming clientHello message.
     * @param pClientHello the incoming clientHello message
     * @return the encapsulated message
     * @throws OceanusException on error
     */
    protected byte[] parseClientHello(final byte[] pClientHello) throws OceanusException {
        /* Must be in clean state */
        checkStatus(GordianAgreementStatus.CLEAN);

        /* Access the sequence */
        final GordianAgreementClientHelloASN1 myClientHello = GordianAgreementClientHelloASN1.getInstance(pClientHello);

        /* Access message parts */
        final AlgorithmIdentifier myAlgId = myClientHello.getAgreementId();
        final AlgorithmIdentifier myResId = myClientHello.getResultId();
        final byte[] myInitVector = myClientHello.getInitVector();
        final byte[] myData = myClientHello.getData();

        /* Check agreementSpec */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final GordianKeyPairAgreementSpec mySpec = myFactory.getSpecForIdentifier(myAlgId);
        if (!Objects.equals(mySpec, getAgreementSpec())) {
            throw new GordianDataException(ERROR_INVSPEC);
        }

        /* Process result identifier */
        processResultIdentifier(myResId);

        /* Store client initVector */
        storeClientIV(myInitVector);

        /* Return the encapsulated message */
        return myData;
    }

    /**
     * Build serverHello message.
     * @return the serverHello message
     * @throws OceanusException on error
     */
    protected byte[] buildServerHello() throws OceanusException {
        return buildServerHello(null, null);
    }

    /**
     * Build serverHello message.
     * @param pEncapsulated the encapsulated data
     * @param pConfirmation the confirmationTag
     * @return the serverHello message
     * @throws OceanusException on error
     */
    protected byte[] buildServerHello(final byte[] pEncapsulated,
                                      final byte[] pConfirmation) throws OceanusException {
        /* Create the serverHello */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final GordianKeyPairAgreementSpec mySpec = (GordianKeyPairAgreementSpec) getAgreementSpec();
        final AlgorithmIdentifier myAlgId = myFactory.getIdentifierForSpec(mySpec);
        final GordianAgreementServerHelloASN1 myServerHello
                = new GordianAgreementServerHelloASN1(myAlgId, theServerIV, pEncapsulated, pConfirmation);

        /* If there is a server confirmation, set status */
        if (pConfirmation != null) {
            setStatus(GordianAgreementStatus.AWAITING_CLIENTCONFIRM);
        }

        /* return the serverHello */
        return myServerHello.getEncodedBytes();
    }

    /**
     * Build serverHello message.
     * @param pEncapsulated the encapsulated data
     * @param pSignId the signatureId
     * @param pSignature the signature
     * @return the serverHello message
     * @throws OceanusException on error
     */
    protected byte[] buildServerHello(final byte[] pEncapsulated,
                                      final AlgorithmIdentifier pSignId,
                                      final byte[] pSignature) throws OceanusException {
        /* Create the serverHello */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final GordianKeyPairAgreementSpec mySpec = (GordianKeyPairAgreementSpec) getAgreementSpec();
        final AlgorithmIdentifier myAlgId = myFactory.getIdentifierForSpec(mySpec);
        final GordianAgreementServerHelloASN1 myServerHello
                = new GordianAgreementServerHelloASN1(myAlgId, theServerIV, pEncapsulated, pSignId, pSignature);

        /* return the serverHello */
        return myServerHello.getEncodedBytes();
    }

    /**
     * Parse the incoming serverHello message.
     * @param pServerHello the serverHello message
     * @return the parsed ASN1
     * @throws OceanusException on error
     */
    protected GordianAgreementServerHelloASN1 parseServerHello(final byte[] pServerHello) throws OceanusException {
        /* Must be in awaiting serverHello state */
        checkStatus(GordianAgreementStatus.AWAITING_SERVERHELLO);

        /* Access the sequence */
        final GordianAgreementServerHelloASN1 myServerHello = GordianAgreementServerHelloASN1.getInstance(pServerHello);

        /* Access message parts */
        final AlgorithmIdentifier myAlgId = myServerHello.getAgreementId();
        final byte[] myInitVector = myServerHello.getInitVector();

        /* Check agreementSpec */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final GordianKeyPairAgreementSpec mySpec = myFactory.getSpecForIdentifier(myAlgId);
        if (!Objects.equals(mySpec, getAgreementSpec())) {
            throw new GordianDataException(ERROR_INVSPEC);
        }

        /* Store server initVector */
        storeServerIV(myInitVector);

        /* Return the ASN1 */
        return myServerHello;
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
        myDigest.update(theClientIV);
        if (theServerIV != null) {
            myDigest.update(theServerIV);
        }

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
        myDigest.update(theClientIV);
        if (theServerIV != null) {
            myDigest.update(theServerIV);
        }

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
            System.arraycopy(myBuffer, 0, pResult, pResult.length - bytesLeft, bytesToCopy);
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
