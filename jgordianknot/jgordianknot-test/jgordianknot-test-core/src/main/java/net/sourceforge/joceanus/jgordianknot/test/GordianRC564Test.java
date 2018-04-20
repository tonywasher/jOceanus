/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.RC5ParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.RC564Engine;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.EAXBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.OCBBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RC5Parameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Test for RC5-64 JCE bugs.
 */
public final class GordianRC564Test {
    /**
     * RC5-64 algorithm.
     */
    private static final String RC564_ALGO = "RC5-64";

    /**
     * Note the provider.
     */
    private static final Provider BCPROV = new BouncyCastleProvider();

    /**
     * Create a logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(GordianRC564Test.class);

    /**
     * Run Tests.
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        testRC564Init("EAX");
        testRC564Init("CCM");
        testRC564Init("GCM");
        testRC564Init();
    }

    /**
     * Private constructor.
     */
    private GordianRC564Test() {
    }

    /**
     * Test Jca RC5-64 Init.
     * @param pMode the mode
     */
    private static void testRC564Init(final String pMode) {
        /* Catch Exceptions */
        try {
            /* Create the generator and generate a key */
            final KeyGenerator myGenerator = KeyGenerator.getInstance(RC564_ALGO, BCPROV);

            /* Initialise the generator */
            final SecureRandom myRandom = new SecureRandom();
            myGenerator.init(128, myRandom);
            final SecretKey myKey = myGenerator.generateKey();

            /* Create a CBC Cipher */
            final Cipher myCipher = Cipher.getInstance(RC564_ALGO + "/" + pMode + "/NoPadding", BCPROV);

            /* Create IV */
            final byte[] myIV = new byte[16];
            myRandom.nextBytes(myIV);

            /* Create a parameterSpec */
            final RC5ParameterSpec mySpec = new RC5ParameterSpec(1, 12, 64, myIV);
            myCipher.init(Cipher.ENCRYPT_MODE, myKey, mySpec);

            LOGGER.error("RC5-64 " + pMode + " JCA init Bug fixed");

            /* Catch general exceptions */
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidAlgorithmParameterException e) {
            LOGGER.error("Failed to create generator", e);
        } catch (InvalidKeyException e) {
            LOGGER.error("RC5-64 " + pMode + " JCA init Bug still exists", e);
        }
    }

    /**
     * Test lightweight RC5-64 Init.
     */
    private static void testRC564Init() {
        /* Create the generator and generate a key */
        final CipherKeyGenerator myGenerator = new CipherKeyGenerator();
        final SecureRandom myRandom = new SecureRandom();
        final KeyGenerationParameters myParams = new KeyGenerationParameters(myRandom, 128);
        myGenerator.init(myParams);
        final byte[] myKey = myGenerator.generateKey();

        /* Create base engine */
        final RC564Engine myEngine = new RC564Engine();

        /* Create IV */
        final byte[] myIV = new byte[12];
        myRandom.nextBytes(myIV);

        /* Create a parameterSpec */
        final RC5Parameters myParms = new RC5Parameters(myKey, 12);
        final ParametersWithIV myIVParms = new ParametersWithIV(myParms, myIV);

        /* Catch Exceptions */
        try {
            /* Create cipher */
            final AEADBlockCipher myCipher = new EAXBlockCipher(myEngine);
            myCipher.init(true, myIVParms);

            LOGGER.error("RC5-64 EAX init Bug fixed");

            /* Catch general exceptions */
        } catch (IllegalArgumentException e) {
            LOGGER.error("RC5-64 EAX init Bug still exists", e);
        }

        /* Catch Exceptions */
        try {
            /* Create cipher */
            final AEADBlockCipher myCipher = new CCMBlockCipher(myEngine);
            myCipher.init(true, myIVParms);

            LOGGER.error("RC5-64 CCM init Bug fixed");

            /* Catch general exceptions */
        } catch (IllegalArgumentException e) {
            LOGGER.error("RC5-64 CCM init Bug still exists", e);
        }

        /* Catch Exceptions */
        try {
            /* Create cipher */
            final AEADBlockCipher myCipher = new GCMBlockCipher(myEngine);
            myCipher.init(true, myIVParms);

            LOGGER.error("RC5-64 GCM init Bug fixed");

            /* Catch general exceptions */
        } catch (ClassCastException e) {
            LOGGER.error("RC5-64 GCM init Bug still exists", e);
        }

        /* Catch Exceptions */
        try {
            /* Create cipher */
            final AEADBlockCipher myCipher = new OCBBlockCipher(myEngine, new RC564Engine());
            myCipher.init(true, myIVParms);

            LOGGER.error("RC5-64 OCB init Bug fixed");

            /* Catch general exceptions */
        } catch (ClassCastException e) {
            LOGGER.error("RC5-64 OCB init Bug still exists", e);
        }
    }
}
