/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.test;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Test for Jca OCB support bugs.
 */
public final class GordianJcaOCBTest {
    /**
     * Note the provider.
     */
    private static final Provider BCPROV = new BouncyCastleProvider();

    /**
     * KeyLength.
     */
    private static final int KEYLEN = 256;

    /**
     * IVLength.
     */
    private static final int IVLEN = 16;

    /**
     * Create a logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(GordianJcaOCBTest.class);

    /**
     * Run Tests.
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        testOCBInit("DSTU7624-128");
        testOCBInit("GOST3412-2015");
        testG3413CTRInit("GOST3412-2015");
    }

    /**
     * Private constructor.
     */
    private GordianJcaOCBTest() {
    }

    /**
     * Test Jca OCB Init.
     * @param pAlgorithm the algorithm
     */
    private static void testOCBInit(final String pAlgorithm) {
        /* Catch Exceptions */
        try {
            /* Create the generator and generate a key */
            final KeyGenerator myGenerator = KeyGenerator.getInstance(pAlgorithm, BCPROV);

            /* Initialise the generator */
            final SecureRandom myRandom = new SecureRandom();
            myGenerator.init(KEYLEN, myRandom);
            final SecretKey myKey = myGenerator.generateKey();

            /* Create IV */
            final byte[] myIV = new byte[IVLEN];
            myRandom.nextBytes(myIV);

            /* Create a OCB Cipher */
            final Cipher myCipher = Cipher.getInstance(pAlgorithm + "/OCB/NoPadding", BCPROV);
            myCipher.init(Cipher.ENCRYPT_MODE, myKey, new IvParameterSpec(myIV));

            LOGGER.error(pAlgorithm + " OCB JCA init Bug fixed");

            /* Catch general exceptions */
        } catch (NoSuchPaddingException
                | InvalidKeyException
                | InvalidAlgorithmParameterException e) {
            LOGGER.error("Failed to create generator", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(pAlgorithm + " OCB JCA init Bug still exists", e);
        }
    }

    /**
     * Test Jca G3413CTR Init.
     * @param pAlgorithm the algorithm
     */
    private static void testG3413CTRInit(final String pAlgorithm) {
        /* Catch Exceptions */
        try {
            /* Create the generator and generate a key */
            final KeyGenerator myGenerator = KeyGenerator.getInstance(pAlgorithm, BCPROV);

            /* Initialise the generator */
            final SecureRandom myRandom = new SecureRandom();
            myGenerator.init(KEYLEN, myRandom);
            final SecretKey myKey = myGenerator.generateKey();

            /* Create IV */
            final byte[] myIV = new byte[IVLEN];
            myRandom.nextBytes(myIV);

            /* Create a OCB Cipher */
            final Cipher myCipher = Cipher.getInstance(pAlgorithm + "/CTR/NoPadding", BCPROV);
            myCipher.init(Cipher.ENCRYPT_MODE, myKey, new IvParameterSpec(myIV));

            LOGGER.error(pAlgorithm + " G3413CTR JCA init Bug fixed");

            /* Catch general exceptions */
        } catch (NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidAlgorithmParameterException e) {
            LOGGER.error("Failed to create generator", e);
        } catch (InvalidKeyException e) {
            LOGGER.error(pAlgorithm + " G3413CTR JCA init Bug still exists", e);
        }
    }
}
