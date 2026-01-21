/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.junit.regression;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptor;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptorFactory;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianAsyncFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.core.encrypt.GordianCoreEncryptorFactory;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.AsymmetricStore.FactoryEncryptor;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.AsymmetricStore.FactoryKeyPairs;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Encrypt scripts.
 */
public final class AsymmetricEncryptScripts {
    /**
     * Private constructor.
     */
    private AsymmetricEncryptScripts() {
    }

    /**
     * Create the encryptor test suite for an encryptorSpec.
     *
     * @param pEncryptor the encryptor
     * @return the test stream or null
     */
    static Stream<DynamicNode> encryptorTests(final FactoryEncryptor pEncryptor) {
        /* Add self encrypt test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("SelfEncrypt", () -> checkSelfEncryptor(pEncryptor)));

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("checkAlgId", () -> checkEncryptorAlgId(pEncryptor))));

        /* Check that the partner supports this keySpec*/
        final GordianAsyncFactory myTgtAsym = pEncryptor.getOwner().getPartner();
        if (myTgtAsym != null) {
            /* Add partner test if the partner supports this encryptore */
            final GordianEncryptorFactory myTgtEncrypts = pEncryptor.getOwner().getPartner().getEncryptorFactory();
            if (myTgtEncrypts.validEncryptorSpecForKeyPairSpec(pEncryptor.getOwner().getKeySpec(), pEncryptor.getSpec())) {
                myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("PartnerEncrypt", () -> checkPartnerEncryptor(pEncryptor))));
            }
        }

        /* Return the test stream */
        return myTests;
    }

    /**
     * Check Self Encryption.
     *
     * @param pEncryptor the encryptor
     * @throws GordianException on error
     */
    private static void checkSelfEncryptor(final FactoryEncryptor pEncryptor) throws GordianException {
        /* Create the data to encrypt */
        final byte[] mySrc = new byte[AsymmetricTest.TESTLEN];

        /* Access the KeySpec */
        final GordianEncryptorSpec mySpec = pEncryptor.getSpec();
        final FactoryKeyPairs myPairs = pEncryptor.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();

        /* Check the encryptor */
        final GordianEncryptorFactory myEncrypts = pEncryptor.getOwner().getFactory().getEncryptorFactory();
        final GordianEncryptor mySender = myEncrypts.createEncryptor(mySpec);
        final GordianEncryptor myReceiver = myEncrypts.createEncryptor(mySpec);

        /* Handle Initialisation */
        mySender.initForEncrypt(myPair);
        myReceiver.initForDecrypt(myPair);

        /* Perform the encryption and decryption for all zeros */
        byte[] myEncrypted = mySender.encrypt(mySrc);
        byte[] myResult = myReceiver.decrypt(myEncrypted);

        /* Check that the values match */
        Assertions.assertArrayEquals(mySrc, myResult, "Failed self encryption for all zeros");

        /* Perform the encryption and decryption for all ones */
        Arrays.fill(mySrc, (byte) 0xFF);
        myEncrypted = mySender.encrypt(mySrc);
        myResult = myReceiver.decrypt(myEncrypted);

        /* Check that the values match */
        Assertions.assertArrayEquals(mySrc, myResult, "Failed self encryption for all ones");

        /* Perform the encryption and decryption for random data */
        AsymmetricTest.RANDOM.nextBytes(mySrc);
        myEncrypted = mySender.encrypt(mySrc);
        myResult = myReceiver.decrypt(myEncrypted);

        /* Check that the values match */
        Assertions.assertArrayEquals(mySrc, myResult, "Failed self encryption for random data");
    }

    /**
     * Check Partner Encryption.
     *
     * @param pEncryptor the encryptor
     * @throws GordianException on error
     */
    private static void checkPartnerEncryptor(final FactoryEncryptor pEncryptor) throws GordianException {
        /* Create the data to encrypt */
        final byte[] mySrc = new byte[AsymmetricTest.TESTLEN];

        /* Access the KeySpec */
        final GordianEncryptorSpec mySpec = pEncryptor.getSpec();
        final FactoryKeyPairs myPairs = pEncryptor.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final GordianKeyPair myPartnerSelf = myPairs.getPartnerSelfKeyPair();

        /* Check the encryptor */
        final GordianEncryptorFactory mySrcEncrypts = pEncryptor.getOwner().getFactory().getEncryptorFactory();
        final GordianEncryptorFactory myTgtEncrypts = pEncryptor.getOwner().getPartner().getEncryptorFactory();
        final GordianEncryptor mySender = mySrcEncrypts.createEncryptor(mySpec);
        final GordianEncryptor myReceiver = myTgtEncrypts.createEncryptor(mySpec);

        /* Handle Initialisation */
        mySender.initForEncrypt(myPair);
        myReceiver.initForDecrypt(myPartnerSelf);

        /* Perform the encryption and decryption on random data */
        AsymmetricTest.RANDOM.nextBytes(mySrc);
        final byte[] myEncrypted = mySender.encrypt(mySrc);
        final byte[] myResult = myReceiver.decrypt(myEncrypted);

        /* Check that the values match */
        Assertions.assertArrayEquals(mySrc, myResult, "Failed sent encryption");

        /* Create a new target encryption and decrypt at receiver */
        myReceiver.initForEncrypt(myPartnerSelf);
        mySender.initForDecrypt(myPair);

        /* Perform the encryption and decryption */
        final byte[] myEncrypted2 = myReceiver.encrypt(mySrc);
        final byte[] myResult2 = mySender.decrypt(myEncrypted2);

        /* Check that the values match */
        Assertions.assertArrayEquals(mySrc, myResult2, "Failed received encryption");
    }

    /**
     * Check encryptorAlgId.
     *
     * @param pEncryptor the encryptor to check
     */
    private static void checkEncryptorAlgId(final FactoryEncryptor pEncryptor) {
        /* Access the factory */
        final GordianCoreEncryptorFactory myFactory = (GordianCoreEncryptorFactory) pEncryptor.getOwner().getFactory().getEncryptorFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pEncryptor.getSpec());
        Assertions.assertNotNull(myId, "Unknown AlgorithmId for " + pEncryptor.getSpec());

        /* Check unique mapping */
        final GordianEncryptorSpec mySpec = myFactory.getSpecForIdentifier(myId);
        Assertions.assertEquals(pEncryptor.getSpec(), mySpec, "Invalid mapping for  " + pEncryptor.getSpec());
    }
}
