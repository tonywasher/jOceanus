/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.cipher;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters.GordianAEADCipherParameters;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters.GordianKeyCipherParameters;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters.GordianNonceParameters;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters.GordianPBECipherParameters;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPBESpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPBESpec.GordianPBEArgon2Spec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPBESpec.GordianPBEDigestAndCountSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPBESpec.GordianPBESCryptSpec;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.gordianknot.impl.core.key.GordianCoreKeyGenerator;
import net.sourceforge.joceanus.tethys.OceanusException;

/**
 * Core Cipher parameters implementation.
 * @param <T> the key type
 */
public class GordianCoreCipherParameters<T extends GordianKeySpec> {
    /**
     * The PBESaltLength.
     */
    private static final int PBESALTLEN = GordianLength.LEN_256.getByteLength();

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The cipherSpec.
     */
    private final GordianCipherSpec<T> theSpec;

    /**
     * The secureRandom.
     */
    private final GordianRandomSource theRandom;

    /**
     * The KeyGenerator.
     */
    private GordianCoreKeyGenerator<T> theGenerator;

    /**
     * Key.
     */
    private GordianKey<T> theKey;

    /**
     * InitialisationVector.
     */
    private byte[] theInitVector;

    /**
     * InitialAEAD.
     */
    private byte[] theInitialAEAD;

    /**
     * pbeSalt.
     */
    private byte[] thePBESalt;

    /**
     * PBESpec.
     */
    private GordianPBESpec thePBESpec;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pCipherSpec the CipherSpec
     */
    GordianCoreCipherParameters(final GordianCoreFactory pFactory,
                                final GordianCipherSpec<T> pCipherSpec) {
        theFactory = pFactory;
        theSpec = pCipherSpec;
        theRandom = theFactory.getRandomSource();
    }

    /**
     * Obtain the key.
     * @return the key
     */
    public GordianKey<T> getKey() {
        return theKey;
    }

    /**
     * Obtain the initVector.
     * @return the initVector
     */
    byte[] getInitVector() {
        return theInitVector;
    }

    /**
     * Obtain the initialAEAD.
     * @return the initialAEAD
     */
    byte[] getInitialAEAD() {
        return theInitialAEAD;
    }

    /**
     * Obtain the pbeSalt.
     * @return the pbeSalt
     */
    byte[] getPBESalt() {
        return thePBESalt;
    }

    /**
     * Obtain the pbeSpec.
     * @return the pbeSpec
     */
    GordianPBESpec getPBESpec() {
        return thePBESpec;
    }


    /**
     * Process cipherParameters.
     * @param pParams the cipher parameters
     * @throws OceanusException on error
     */
    void processParameters(final GordianCipherParameters pParams) throws OceanusException {
        /* If the cipher parameters are PBE */
        if (pParams instanceof GordianPBECipherParameters) {
            /* Process separately */
            processPBEParameters((GordianPBECipherParameters) pParams);

            /* else standard parameters */
        } else {
            /* Show that we are not PBE */
            thePBESpec = null;
            thePBESalt = null;

            /* Access the key details */
            theKey = obtainKeyFromParameters(pParams);
            theInitVector = obtainNonceFromParameters(pParams, false);
            theInitialAEAD = obtainInitialAEADFromParameters(pParams);
        }
    }

    /**
     * Process cipherParameters.
     * @param pParams the cipher parameters
     * @throws OceanusException on error
     */
    private void processPBEParameters(final GordianPBECipherParameters pParams) throws OceanusException {
        /* Check that the PBE parameters are supported */
        final GordianPBESpec myPBESpec = pParams.getPBESpec();
        final GordianPBECipherSpec<T> myPBECipherSpec = new GordianPBECipherSpec<>(myPBESpec, theSpec);
        final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
        if (!myCipherFactory.supportedPBECipherSpecs().test(myPBECipherSpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(myPBECipherSpec));
        }

        /* Access PBE details */
        thePBESpec = myPBESpec;
        thePBESalt = obtainNonceFromParameters(pParams, true);

        /* Switch on the PBE type */
        CipherParameters myParams;
        switch (thePBESpec.getPBEType()) {
            case PBKDF2:
                myParams = derivePBKDF2Parameters(pParams.getPassword());
                break;
            case PKCS12:
                myParams = derivePKCS12Parameters(pParams.getPassword());
                break;
            case SCRYPT:
                myParams = deriveSCRYPTParameters(pParams.getPassword());
                break;
            case ARGON2:
            default:
                myParams = deriveArgon2Parameters(pParams.getPassword());
                break;
        }

        /* Store details */
        theInitialAEAD = null;
        theInitVector = null;
        if (myParams instanceof ParametersWithIV) {
            final ParametersWithIV myIVParams = (ParametersWithIV) myParams;
            theInitVector = myIVParams.getIV();
            myParams = myIVParams.getParameters();
        }
        theKey = buildKeyFromBytes(((KeyParameter) myParams).getKey());
    }

    /**
     * Build a key from bytes.
     * @param pKeyBytes the bytes to use
     * @return the key
     * @throws OceanusException on error
     */
    GordianKey<T> buildKeyFromBytes(final byte[] pKeyBytes) throws OceanusException {
        /* Create generator if needed */
        if (theGenerator == null) {
            final GordianCipherFactory myFactory = theFactory.getCipherFactory();
            theGenerator = (GordianCoreKeyGenerator<T>) myFactory.getKeyGenerator(theSpec.getKeyType());
        }

        /* Create the key */
        return theGenerator.buildKeyFromBytes(pKeyBytes);
    }

    /**
     * Obtain Key from CipherParameters.
     * @param pParams parameters
     * @return the key
     */
    @SuppressWarnings("unchecked")
    private GordianKey<T> obtainKeyFromParameters(final GordianCipherParameters pParams) {
        /* If we have specified IV */
        if (pParams instanceof GordianKeyCipherParameters) {
            /* Access the parameters */
            final GordianKeyCipherParameters<?> myParams = (GordianKeyCipherParameters<?>) pParams;
            return (GordianKey<T>) myParams.getKey();
        }

        /* No key */
        return null;
    }

    /**
     * Obtain Nonce from CipherParameters.
     * @param pParams parameters
     * @param pPBESalt is this a PBESalt
     * @return the nonce
     */
    private byte[] obtainNonceFromParameters(final GordianCipherParameters pParams,
                                             final boolean pPBESalt) {
        /* Default IV is null */
        byte[] myIV = null;

        /* If we have specified IV */
        if (pParams instanceof GordianNonceParameters) {
            /* Access the parameters */
            final GordianNonceParameters myParams = (GordianNonceParameters) pParams;

            /* If we have an explicit Nonce */
            if (!myParams.randomNonce()) {
                /* access the nonce */
                myIV = Arrays.clone(myParams.getNonce());

                /* Else if we actually need a nonce */
            } else if (pPBESalt || theSpec.needsIV()) {
                /* Create a random IV */
                final int myLen = pPBESalt ? PBESALTLEN : theSpec.getIVLength();
                myIV = new byte[myLen];
                theRandom.getRandom().nextBytes(myIV);
            }
        }

        /* return the IV */
        return myIV;
    }

    /**
     * Obtain initialAEAD from CipherParameters.
     * @param pParams parameters
     * @return the initialAEAD
     */
    private static byte[] obtainInitialAEADFromParameters(final GordianCipherParameters pParams) {
        /* Default initialAEAD is null */
        byte[] myInitial = null;

        /* If we have specified IV */
        if (pParams instanceof GordianAEADCipherParameters) {
            /* Access the parameters */
            final GordianAEADCipherParameters<?> myParams = (GordianAEADCipherParameters<?>) pParams;
            myInitial = Arrays.clone(myParams.getInitialAEAD());
        }

        /* return initialAEAD */
        return myInitial;
    }

    /**
     * derive PBKDF2 key and IV.
     * @param pPassword the password
     * @return the parameters
     */
    private CipherParameters derivePBKDF2Parameters(final char[] pPassword) {
        /* Protect password bytes */
        byte[] myPassword = null;
        try {
            /* Create the digest */
            final GordianPBEDigestAndCountSpec mySpec = (GordianPBEDigestAndCountSpec) thePBESpec;
            final Digest myDigest = new SHA512Digest();

            /* Create the generator and initialise it */
            final PKCS5S2ParametersGenerator myGenerator = new PKCS5S2ParametersGenerator(myDigest);
            myPassword = PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(pPassword);
            myGenerator.init(myPassword, thePBESalt, mySpec.getIterationCount());

            /* Generate the parameters */
            final int myKeyLen = theSpec.getKeyType().getKeyLength().getLength();
            final int myIVLen =  theSpec.needsIV() ? Byte.SIZE * theSpec.getIVLength() : 0;
            return myIVLen == 0
                   ? myGenerator.generateDerivedParameters(myKeyLen)
                   : myGenerator.generateDerivedParameters(myKeyLen, myIVLen);
        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    /**
     * derive PKCS12 key and IV.
     * @param pPassword the password
     * @return the parameters
     */
    private CipherParameters derivePKCS12Parameters(final char[] pPassword) {
        /* Protect password bytes */
        byte[] myPassword = null;
        try {
            /* Create the digest */
            final GordianPBEDigestAndCountSpec mySpec = (GordianPBEDigestAndCountSpec) thePBESpec;
            final Digest myDigest = new SHA512Digest();

            /* Create the generator and initialise it */
            final PKCS12ParametersGenerator myGenerator = new PKCS12ParametersGenerator(myDigest);
            myPassword = PBEParametersGenerator.PKCS12PasswordToBytes(pPassword);
            myGenerator.init(myPassword, thePBESalt, mySpec.getIterationCount());

            /* Generate the parameters */
            final int myKeyLen = theSpec.getKeyType().getKeyLength().getLength();
            final int myIVLen =  theSpec.needsIV() ? Byte.SIZE * theSpec.getIVLength() : 0;
            return myIVLen == 0
                   ? myGenerator.generateDerivedParameters(myKeyLen)
                   : myGenerator.generateDerivedParameters(myKeyLen, myIVLen);
        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    /**
     * derive SCRYPT key and IV.
     * @param pPassword the password
     * @return the parameters
     */
    private CipherParameters deriveSCRYPTParameters(final char[] pPassword) {
        /* Protect password bytes */
        byte[] myPassword = null;
        try {
            /* Access the password as bytes */
            final GordianPBESCryptSpec mySpec = (GordianPBESCryptSpec) thePBESpec;
            myPassword = PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(pPassword);

            /* Generate the bytes */
            final int myKeyLen = theSpec.getKeyType().getKeyLength().getByteLength();
            final int myIVLen = theSpec.needsIV() ? theSpec.getIVLength() : 0;
            final int myBufLen = myIVLen + myKeyLen;
            final byte[] myBuffer = SCrypt.generate(myPassword, thePBESalt, mySpec.getCost(),
                    mySpec.getBlockSize(), mySpec.getParallel(), myBufLen);

            /* Convert to parameters */
            final KeyParameter myKeyParm = new KeyParameter(myBuffer, 0, myKeyLen);
            return myIVLen == 0
                   ? myKeyParm
                   : new ParametersWithIV(myKeyParm, myBuffer, myKeyLen, myIVLen);
        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    /**
     * derive Argon2 key and IV.
     * @param pPassword the password
     * @return the parameters
     */
    private CipherParameters deriveArgon2Parameters(final char[] pPassword)  {
        /* Create the parameters */
        final GordianPBEArgon2Spec mySpec = (GordianPBEArgon2Spec) thePBESpec;
        final Argon2Parameters.Builder myBuilder = new Argon2Parameters.Builder();
        myBuilder.withSalt(thePBESalt);
        myBuilder.withIterations(mySpec.getIterationCount());
        myBuilder.withParallelism(mySpec.getLanes());
        myBuilder.withMemoryAsKB(mySpec.getMemory());
        final Argon2Parameters myParams = myBuilder.build();

        /* Generate the bytes */
        final Argon2BytesGenerator myGenerator = new Argon2BytesGenerator();
        myGenerator.init(myParams);
        final int myKeyLen = theSpec.getKeyType().getKeyLength().getByteLength();
        final int myIVLen = theSpec.needsIV() ? theSpec.getIVLength() : 0;
        final int myBufLen = myIVLen + myKeyLen;
        final byte[] myBuffer = new byte[myBufLen];
        myGenerator.generateBytes(pPassword, myBuffer);

        /* Convert to parameters */
        final KeyParameter myKeyParm = new KeyParameter(myBuffer, 0, myKeyLen);
        return myIVLen == 0
               ? myKeyParm
               : new ParametersWithIV(myKeyParm, myBuffer, myKeyLen, myIVLen);
    }
}
