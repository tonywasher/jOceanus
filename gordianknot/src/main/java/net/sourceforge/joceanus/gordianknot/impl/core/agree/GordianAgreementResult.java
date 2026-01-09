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
package net.sourceforge.joceanus.gordianknot.impl.core.agree;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianDataConverter;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.gordianknot.impl.core.kdf.GordianHKDFEngine;
import net.sourceforge.joceanus.gordianknot.impl.core.kdf.GordianHKDFParams;
import net.sourceforge.joceanus.gordianknot.impl.core.key.GordianCoreKeyGenerator;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySetFactory;
import org.bouncycastle.util.Arrays;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Result derivation.
 */
public class GordianAgreementResult {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

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
    GordianAgreementResult(final GordianBaseFactory pFactory) {
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
     * @throws GordianException on error
     */
    Object processSecret(final byte[] pSecret,
                         final Object pResultType) throws GordianException {
        /* If the resultType is a FactoryType */
        if (pResultType instanceof GordianFactoryType myType) {
            /* derive the factory */
            return deriveFactory(myType, pSecret);

            /* If the resultType is a KeySetSpec */
        } else if (pResultType instanceof GordianKeySetSpec mySpec) {
            /* Derive the keySet */
            return deriveKeySet(mySpec, pSecret);

            /* If the resultType is a SymCipherSpec */
        } else if (pResultType instanceof GordianSymCipherSpec mySpec) {
            /* Derive the key */
            return deriveCipher(mySpec, pSecret);

            /* If the resultType is a StreamCipherSpec */
        } else if (pResultType instanceof GordianStreamCipherSpec mySpec) {
            /* Derive the key */
            return deriveCipher(mySpec, pSecret);

            /* If the resultType is an Integer */
        } else if (pResultType instanceof Integer myLength) {
            /* Derive the key */
            return deriveBytes(pSecret, myLength);
        }

        /* Return the raw secret */
        return Arrays.clone(pSecret);
    }

    /**
     * Derive factory from the secret.
     * @param pFactoryType the factoryType
     * @param pSecret the secret
     * @return the factory
     * @throws GordianException on error
     */
    private GordianFactory deriveFactory(final GordianFactoryType pFactoryType,
                                         final byte[] pSecret) throws GordianException {
        /* Ensure that we clear out the seed */
        byte[] mySeed = null;
        try {
            /* Calculate the seed */
            mySeed = calculateDerivedSecret(pSecret, GordianDerivationId.FACTORY, GordianParameters.SEED_LEN.getByteLength());

            /* Create a new Factory using the phrase */
            final GordianParameters myParams = new GordianParameters(pFactoryType, mySeed);
            return theFactory.newFactory(myParams);

            /* Clear buffer */
        } finally {
            if (mySeed != null) {
                Arrays.fill(mySeed, (byte) 0);
            }
        }
    }

    /**
     * Derive a keySet from the secret.
     * @param pSpec the keySetSpec
     * @param pSecret the secret
     * @return the derived keySet
     * @throws GordianException on error
     */
    private GordianKeySet deriveKeySet(final GordianKeySetSpec pSpec,
                                       final byte[] pSecret) throws GordianException {
        /* Derive a shared factory */
        final GordianFactory myFactory = deriveFactory(GordianFactoryType.BC, pSecret);

        /* Ensure that we clear out the secret */
        byte[] mySecret = null;
        try {
            /* Calculate the secret */
            mySecret = calculateDerivedSecret(pSecret, GordianDerivationId.KEYSET, GordianParameters.SECRET_LEN.getByteLength());

            /* Derive the keySet */
            final GordianCoreKeySetFactory myKeySets = (GordianCoreKeySetFactory) myFactory.getKeySetFactory();
            final GordianCoreKeySet myKeySet = myKeySets.createKeySet(pSpec);
            myKeySet.buildFromSecret(mySecret);
            return myKeySet;

        } finally {
            if (mySecret != null) {
                Arrays.fill(mySecret, (byte) 0);
            }
        }
    }

    /**
     * Derive a symKeyCipher pair from the secret.
     * @param pCipherSpec the cipherSpec
     * @param pSecret the secret
     * @return the ciphers
     * @throws GordianException on error
     */
    private GordianSymCipher[] deriveCipher(final GordianSymCipherSpec pCipherSpec,
                                            final byte[] pSecret) throws GordianException {
        /* Derive a shared factory */
        final GordianFactory myFactory = deriveFactory(GordianFactoryType.BC, pSecret);

        /* Generate the key */
        final GordianKey<GordianSymKeySpec> myKey = deriveKey(myFactory, pCipherSpec.getKeyType(), pSecret);

        /* Create the cipher and initialise with key */
        final GordianCipherFactory myCiphers = myFactory.getCipherFactory();
        final GordianSymCipher myOutCipher = myCiphers.createSymKeyCipher(pCipherSpec);
        final GordianSymCipher myInCipher = myCiphers.createSymKeyCipher(pCipherSpec);

        /* If we need an IV */
        if (pCipherSpec.needsIV()) {
            /* Calculate the IV */
            final byte[] myIV = deriveIV(pSecret, pCipherSpec.getIVLength());

            /* Initialise the ciphers */
            final GordianCipherParameters myParms = GordianCipherParameters.keyAndNonce(myKey, myIV);
            myOutCipher.initForEncrypt(myParms);
            myInCipher.initForDecrypt(myParms);

            /* else no IV */
        } else {
            final GordianCipherParameters myParms = GordianCipherParameters.key(myKey);
            myOutCipher.initForEncrypt(myParms);
            myInCipher.initForDecrypt(myParms);
        }
        return new GordianSymCipher[] { myOutCipher, myInCipher };
    }

    /**
     * Derive a streamKeyCipher pair from the secret.
     * @param pCipherSpec the cipherSpec
     * @param pSecret the secret
     * @return the ciphers
     * @throws GordianException on error
     */
    private GordianStreamCipher[] deriveCipher(final GordianStreamCipherSpec pCipherSpec,
                                               final byte[] pSecret) throws GordianException {
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
            final byte[] myIV = deriveIV(pSecret, pCipherSpec.getIVLength());

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
     * @throws GordianException on error
     */
    private <T extends GordianKeySpec> GordianKey<T> deriveKey(final GordianFactory pFactory,
                                                               final T pKeyType,
                                                               final byte[] pSecret) throws GordianException {
        /* Ensure that we clear out the key */
        byte[] myKey = null;
        try {
            /* Calculate the secret */
            myKey = calculateDerivedSecret(pSecret, GordianDerivationId.KEY, pKeyType.getKeyLength().getByteLength());

            /* Derive the key */
            final GordianCipherFactory myCiphers = pFactory.getCipherFactory();
            final GordianCoreKeyGenerator<T> myGenerator = (GordianCoreKeyGenerator<T>) myCiphers.getKeyGenerator(pKeyType);
            return myGenerator.buildKeyFromBytes(myKey);

            /* Clear buffers */
        } finally {
            if (myKey != null) {
                Arrays.fill(myKey, (byte) 0);
            }
        }
    }

    /**
     * Calculate the derived IV.
     * @param pSecret the secret
     * @param pIVLen the IV length
     * @return the derived IV
     * @throws GordianException on error
     */
    private byte[] deriveIV(final byte[] pSecret,
                            final int pIVLen) throws GordianException {
        /* Calculate the secret */
        return calculateDerivedSecret(pSecret, GordianDerivationId.IV, pIVLen);
    }

    /**
     * Derive bytes.
     * @param pSecret the secret
     * @param pLength the length of bytes
     * @return the factory
     * @throws GordianException on error
     */
    private byte[] deriveBytes(final byte[] pSecret,
                               final int pLength) throws GordianException {
        /* Return the secret */
        return calculateDerivedSecret(pSecret, GordianDerivationId.BYTES, pLength);
    }

    /**
     * Calculate the derived secret.
     * @param pSecret the secret
     * @param pId the derivation Id
     * @param pResultLen the length of the result
     * @return the derived secret
     * @throws GordianException on error
     */
    byte[] calculateDerivedSecret(final byte[] pSecret,
                                  final GordianDerivationId pId,
                                  final int pResultLen) throws GordianException {
        /* Build the 64-bit seed and create the seeded random */
        final long mySeed = GordianDataConverter.byteArrayToLong(pSecret);
        final Random myRandom = new Random(mySeed);

        /* Protect against exceptions */
        final GordianHKDFParams myParams = GordianHKDFParams.extractThenExpand(pResultLen);
        try {
            /* Customise the HKDF parameters */
            final GordianDigestSpec myDigestSpec = new GordianDigestSpec(pId.getDigestType());
            final byte[] myBytes = new byte[Long.BYTES];
            myRandom.nextBytes(myBytes);
            myParams.withIKM(pSecret).withIKM(pId.getId())
                    .withSalt(theClientIV).withSalt(theServerIV)
                    .withInfo(myBytes);

            /* Derive the bytes */
            final GordianHKDFEngine myEngine = new GordianHKDFEngine(theFactory, myDigestSpec);
            return myEngine.deriveBytes(myParams);

        } finally {
            if (myParams != null) {
                myParams.clearParameters();
            }
        }
    }

    /**
     * Derived secret id.
     */
    public enum GordianDerivationId {
        /**
         * Factory.
         */
        FACTORY("Factory"),

        /**
         * KeySet.
         */
        KEYSET("KeySet"),

        /**
         * Key.
         */
        KEY("Key"),

        /**
         * IV.
         */
        IV("IV"),

        /**
         * Bytes.
         */
        BYTES("Bytes"),

        /**
         * Tags.
         */
        TAGS("Tags"),

        /**
         * Composite.
         */
        COMPOSITE("Composite");

        /**
         * The id.
         */
        private final byte[] theId;

        /**
         * Constructor.
         * @param pId the id
         */
        GordianDerivationId(final String pId) {
            theId = pId.getBytes(StandardCharsets.UTF_8);
        }

        /**
         * Obtain the id.
         * @return the id.
         */
        byte[] getId() {
            return theId;
        }

        /**
         * Obtain digest type for id.
         * @return the id
         */
        public GordianDigestType getDigestType() {
            /*
             * Assign a different digestType to each Id.
             * Note that each type must nbe available as an HMAC in JCA.
             */
            switch (this) {
                case FACTORY:
                    return GordianDigestType.SHA3;
                case KEYSET:
                    return GordianDigestType.SHA2;
                case KEY:
                    return GordianDigestType.SKEIN;
                case IV:
                    return GordianDigestType.RIPEMD;
                case BYTES:
                    return GordianDigestType.STREEBOG;
                case TAGS:
                    return GordianDigestType.WHIRLPOOL;
                case COMPOSITE:
                    return GordianDigestType.SM3;
                default:
                    throw new IllegalArgumentException("Invalid ID");
            }
        }
    }
}
