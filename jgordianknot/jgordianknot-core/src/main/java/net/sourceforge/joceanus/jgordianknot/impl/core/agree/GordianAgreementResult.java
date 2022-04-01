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
package net.sourceforge.joceanus.jgordianknot.impl.core.agree;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherParameters;
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
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.impl.core.key.GordianCoreKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySetFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Result derivation.
 */
public class GordianAgreementResult {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The client initVector.
     */
    private byte[] theClientIV;

    /**
     * The server initVector.
     */
    private byte[] theServerIV;

    /**
     * constructor.
     * @param pFactory the factory
     */
    GordianAgreementResult(final GordianCoreFactory pFactory) {
        theFactory = pFactory;
    }

    /**
     * Obtain the clientIV.
     * @return the clientIV
     */
    protected byte[] getClientIV() {
        return theClientIV;
    }

    /**
     * Set clientIV.
     * @param pClientIV the clientIV
     */
    void setClientIV(final byte[] pClientIV) {
        theClientIV = pClientIV;
    }

    /**
     * Obtain the serverIV.
     * @return the serverIV
     */
    protected byte[] getServerIV() {
        return theServerIV;
    }

    /**
     * Set serverIV.
     * @param pServerIV the serverIV
     */
    void setServerIV(final byte[] pServerIV) {
        theServerIV = pServerIV;
    }

    /**
     * Reset data.
     */
    public void reset() {
        /* Reset the client and serverIVs */
        theClientIV = null;
        theServerIV = null;
    }

    /**
     * Process the secret.
     * @param pSecret the secret
     * @param pResultType the resultType
     * @return teh result
     * @throws OceanusException on error
     */
    Object processSecret(final byte[] pSecret,
                         final Object pResultType) throws OceanusException {
        /* If the resultType is a FactoryType */
        if (pResultType instanceof GordianFactoryType) {
            /* derive the factory */
            return deriveFactory((GordianFactoryType) pResultType, pSecret);

            /* If the resultType is a KeySetSpec */
        } else if (pResultType instanceof GordianKeySetSpec) {
            /* Derive the keySet */
            return deriveKeySet((GordianKeySetSpec) pResultType, pSecret);

            /* If the resultType is a SymCipherSpec */
        } else if (pResultType instanceof GordianSymCipherSpec) {
            /* Derive the key */
            final GordianSymCipherSpec myCipherSpec = (GordianSymCipherSpec) pResultType;
            return deriveCipher(myCipherSpec, pSecret);

            /* If the resultType is a StreamCipherSpec */
        } else if (pResultType instanceof GordianStreamCipherSpec) {
            /* Derive the key */
            final GordianStreamCipherSpec myCipherSpec = (GordianStreamCipherSpec) pResultType;
            return deriveCipher(myCipherSpec, pSecret);
        }

        /* Derive the secret */
        return deriveBasicResult(pSecret);
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
            calculateDerivedSecret(GordianDigestType.BLAKE2, pSecret, myPhrase);

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
     * Calculate the derived secret.
     * @param pDigestType the digestType
     * @param pSecret the secret
     * @param pResult the result buffer
     * @throws OceanusException on error
     */
    void calculateDerivedSecret(final GordianDigestType pDigestType,
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
    private void calculateDerivedIV(final byte[] pSecret,
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
}
