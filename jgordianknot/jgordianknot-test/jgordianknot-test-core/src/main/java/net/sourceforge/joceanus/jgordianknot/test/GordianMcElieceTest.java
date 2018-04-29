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
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.bouncycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.McElieceKeyGenParameterSpec;

/**
 * Test for McEliece JCE bugs.
 */
public final class GordianMcElieceTest {
    /**
     * McEliece algorithm.
     */
    private static final String MCELIECE_ALGO = "McEliece";

    /**
     * McEliece-CCA2 algorithm.
     */
    private static final String MCELIECECCA2_ALGO = "McEliece-CCA2";

    /**
     * Note the post quantum provider.
     */
    private static final Provider BCPQPROV = new BouncyCastlePQCProvider();

    /**
     * Create a logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(GordianMcElieceTest.class);

    /**
     * Run Tests.
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        testMcElieceInit();
        testMcElieceCCA2Init();
        testMcElieceKeySpec();
        testMcElieceCCA2KeySpec();
    }

    /**
     * Private constructor.
     */

    private GordianMcElieceTest() {
    }

    /**
     * Test McEliece Init.
     */
    private static void testMcElieceInit() {
        /* Catch Exceptions */
        try {
            /* Create and initialise the generator */
            final KeyPairGenerator myGenerator = KeyPairGenerator.getInstance(MCELIECE_ALGO, BCPQPROV);
            final McElieceKeyGenParameterSpec mySpec = new McElieceKeyGenParameterSpec(McElieceKeyGenParameterSpec.DEFAULT_M,
                    McElieceKeyGenParameterSpec.DEFAULT_T);
            myGenerator.initialize(mySpec, new SecureRandom());

            /*
             * Generate the keyPair. This will generate a NullPointerException since the version of
             * initialise used is a NoOp
             */
            myGenerator.generateKeyPair();
            LOGGER.error("McElieceCCA2 init Bug fixed");

            /* Catch Null Pointer Exception */
        } catch (NullPointerException e) {
            LOGGER.error("McElieceCCA2 init Bug still exists", e);

            /* Catch general exceptions */
        } catch (NoSuchAlgorithmException
                | InvalidAlgorithmParameterException e) {
            LOGGER.error("Failed to create generator", e);
        }
    }

    /**
     * Test McElieceCCA2 Init.
     */
    private static void testMcElieceCCA2Init() {
        /* Catch Exceptions */
        try {
            /* Create and initialise the generator */
            final KeyPairGenerator myGenerator = KeyPairGenerator.getInstance(MCELIECECCA2_ALGO, BCPQPROV);
            final McElieceCCA2KeyGenParameterSpec mySpec = new McElieceCCA2KeyGenParameterSpec(McElieceCCA2KeyGenParameterSpec.DEFAULT_M,
                    McElieceCCA2KeyGenParameterSpec.DEFAULT_T, McElieceCCA2KeyGenParameterSpec.SHA512);
            myGenerator.initialize(mySpec, new SecureRandom());

            /*
             * Generate the keyPair. This will generate a NullPointerException since the version of
             * initialise used is a NoOp
             */
            myGenerator.generateKeyPair();
            LOGGER.error("McElieceCCA2 init Bug fixed");

            /* Catch Null Pointer Exception */
        } catch (NullPointerException e) {
            LOGGER.error("McElieceCCA2 init Bug still exists", e);

            /* Catch general exceptions */
        } catch (NoSuchAlgorithmException
                | InvalidAlgorithmParameterException e) {
            LOGGER.error("Failed to create generator", e);
        }
    }

    /**
     * Test McEliece KeySpec.
     */
    private static void testMcElieceKeySpec() {
        /* Catch Exceptions */
        try {
            /* Create and initialise the generator */
            final KeyPairGenerator myGenerator = KeyPairGenerator.getInstance(MCELIECE_ALGO, BCPQPROV);
            myGenerator.initialize(McElieceKeyGenParameterSpec.DEFAULT_M, new SecureRandom());

            /* Generate the keyPair. */
            final KeyPair myPair = myGenerator.generateKeyPair();

            /* Create the Factory */
            final KeyFactory myFactory = KeyFactory.getInstance(MCELIECE_ALGO, BCPQPROV);

            /* Access KeySpecs. This will return NULL keys since the call has not been coded */
            final PKCS8EncodedKeySpec myPrivateSpec = myFactory.getKeySpec(myPair.getPrivate(), PKCS8EncodedKeySpec.class);
            final X509EncodedKeySpec myPublicSpec = myFactory.getKeySpec(myPair.getPublic(), X509EncodedKeySpec.class);
            if (myPrivateSpec != null
                && myPublicSpec != null) {
                LOGGER.error("McElieceCCA2 keySpec Bug fixed");
            } else {
                LOGGER.error("McElieceCCA2 keySpec Bug still exists");
            }

            /* Translate Keys. This will return NULL keys since the call has not been coded */
            final Key myPrivate = myFactory.translateKey(myPair.getPrivate());
            final Key myPublic = myFactory.translateKey(myPair.getPublic());
            if ((myPrivate != null) && (myPublic != null)) {
                LOGGER.error("McElieceCCA2 translate Bug fixed");
            } else {
                LOGGER.error("McElieceCCA2 translate Bug still exists");
            }

            /* Catch general exceptions */
        } catch (NoSuchAlgorithmException
                | InvalidKeySpecException
                | InvalidKeyException e) {
            LOGGER.error("Failed to create generator", e);
        }
    }

    /**
     * Test McElieceCCA2 KeySpec.
     */
    private static void testMcElieceCCA2KeySpec() {
        /* Catch Exceptions */
        try {
            /* Create and initialise the generator */
            final KeyPairGenerator myGenerator = KeyPairGenerator.getInstance(MCELIECECCA2_ALGO, BCPQPROV);
            myGenerator.initialize(McElieceCCA2KeyGenParameterSpec.DEFAULT_M, new SecureRandom());

            /* Generate the keyPair. */
            final KeyPair myPair = myGenerator.generateKeyPair();

            /* Create the Factory */
            final KeyFactory myFactory = KeyFactory.getInstance(MCELIECECCA2_ALGO, BCPQPROV);

            /* Access KeySpecs. This will return NULL keys since the call has not been coded */
            final PKCS8EncodedKeySpec myPrivateSpec = myFactory.getKeySpec(myPair.getPrivate(), PKCS8EncodedKeySpec.class);
            final X509EncodedKeySpec myPublicSpec = myFactory.getKeySpec(myPair.getPublic(), X509EncodedKeySpec.class);
            if ((myPrivateSpec != null) && (myPublicSpec != null)) {
                LOGGER.error("McElieceCCA2 keySpec Bug fixed");
            } else {
                LOGGER.error("McElieceCCA2 keySpec Bug still exists");
            }

            /* Translate Keys. This will return NULL keys since the call has not been coded */
            final Key myPrivate = myFactory.translateKey(myPair.getPrivate());
            final Key myPublic = myFactory.translateKey(myPair.getPublic());
            if ((myPrivate != null) && (myPublic != null)) {
                LOGGER.error("McElieceCCA2 translate Bug fixed");
            } else {
                LOGGER.error("McElieceCCA2 translate Bug still exists");
            }

            /* Catch general exceptions */
        } catch (NoSuchAlgorithmException
                | InvalidKeySpecException
                | InvalidKeyException e) {
            LOGGER.error("Failed to create generator", e);
        }
    }
}
