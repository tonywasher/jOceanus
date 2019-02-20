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
package net.sourceforge.joceanus.jgordianknot.junit.regression;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianGenerator;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKnuthObfuscater;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Security Test suite - Test KeySet functionality.
 */
class KeySetTest {
    /**
     * Default password.
     */
    private static final char[] DEF_PASSWORD = "SimplePassword".toCharArray();

    /**
     * The factories.
     */
    private static GordianFactory BCFULLFACTORY;
    private static GordianFactory JCAFULLFACTORY;
    private static GordianFactory BCCUTFACTORY;
    private static GordianFactory JCACUTFACTORY;

    /**
     * Initialise Factories.
     */
    @BeforeAll
    static void createSecurityFactories() throws OceanusException {
        BCFULLFACTORY = GordianGenerator.createFactory(new GordianParameters(false, GordianFactoryType.BC));
        BCCUTFACTORY = GordianGenerator.createFactory(new GordianParameters(true, GordianFactoryType.BC));
        JCAFULLFACTORY = GordianGenerator.createFactory(new GordianParameters(false, GordianFactoryType.JCA));
        JCACUTFACTORY = GordianGenerator.createFactory(new GordianParameters(true, GordianFactoryType.JCA));
    }

    /**
     * Create the keySet test suite.
     * @return the test stream
     */
    @TestFactory
    Stream<DynamicNode> keySetTests() {
        /* Create tests */
        Stream<DynamicNode> myStream = keySetTests(BCCUTFACTORY);
        myStream = Stream.concat(myStream, keySetTests(BCFULLFACTORY));
        myStream = Stream.concat(myStream, keySetTests(JCACUTFACTORY));
        return Stream.concat(myStream, keySetTests(JCAFULLFACTORY));
    }

    /**
     * Create the keySet test suite for a factory.
     * @param pFactory the factory
     * @return the test stream
     */
    private Stream<DynamicNode> keySetTests(final GordianFactory pFactory) {
        /* Return the stream */
        final String myName = pFactory.getFactoryType().toString()
                + (pFactory.isRestricted() ? "-Restricted" : "-Full");
        return Stream.of(DynamicTest.dynamicTest(myName, () -> testSecurity(BCCUTFACTORY)));
    }

    /**
     * Test keySet algorithms.
     * @param pFactory the factory
     * @throws OceanusException on error
     */
    private void testSecurity(final GordianFactory pFactory) throws OceanusException {
        /* Create new Password Hash */
        final GordianRandomFactory myRandoms = pFactory.getRandomFactory();
        GordianKeySetFactory myKeySets = pFactory.getKeySetFactory();
        final GordianKeySetHash myHash = myKeySets.generateKeySetHash(DEF_PASSWORD.clone());
        final GordianKeySet myKeySet = myHash.getKeySet();
        GordianKnuthObfuscater myKnuth = myKeySets.getObfuscater();

        /* Create new symmetric key and stream Key */
        final GordianKey<GordianSymKeySpec> mySym = myRandoms.generateRandomSymKey();
        final GordianKey<GordianStreamKeyType> myStream = myRandoms.generateRandomStreamKey();

        /* Secure the keys */
        final byte[] mySymSafe = myKeySet.secureKey(mySym);
        final byte[] myStreamSafe = myKeySet.secureKey(myStream);

        /* Encrypt short block */
        final String myTest1 = "TestString";
        byte[] myBytes = TethysDataConverter.stringToByteArray(myTest1);
        final byte[] myEncrypt1 = myKeySet.encryptBytes(myBytes);

        /* Encrypt full block */
        final String myTest2 = "TestString123456";
        myBytes = TethysDataConverter.stringToByteArray(myTest2);
        final byte[] myEncrypt2 = myKeySet.encryptBytes(myBytes);

        /* Encrypt some multi-block */
        final String myTest3 = "TestString1234567";
        myBytes = TethysDataConverter.stringToByteArray(myTest3);
        final byte[] myEncrypt3 = myKeySet.encryptBytes(myBytes);

        /* Create a data digest */
        GordianDigest myDigest = myRandoms.generateRandomDigest();
        myDigest.update(mySymSafe);
        myDigest.update(myStreamSafe);
        final byte[] myDigestBytes = myDigest.finish();

        /* Create a data MAC */
        GordianMac myMac = myRandoms.generateRandomMac();
        myMac.update(mySymSafe);
        myMac.update(myStreamSafe);
        final byte[] myMacBytes = myMac.finish();

        /* Secure the keys */
        final byte[] myMacSafe = myKeySet.secureKey(myMac.getKey());
        final byte[] myIV = myMac.getInitVector();
        final int myMacId = myKnuth.deriveExternalIdFromType(myMac.getMacSpec());

        /* Start a new session */
        final GordianParameters myParams = new GordianParameters(pFactory.isRestricted(), pFactory.getFactoryType());
        final GordianFactory myFactory = GordianGenerator.createFactory(myParams);
        final GordianDigestFactory myDigests = myFactory.getDigestFactory();
        final GordianMacFactory myMacs = myFactory.getMacFactory();
        myKeySets = myFactory.getKeySetFactory();
        final GordianKeySetHash myNewHash = myKeySets.deriveKeySetHash(myHash.getHash(), DEF_PASSWORD.clone());
        final GordianKeySet myKeySet1 = myNewHash.getKeySet();
        myKnuth = myKeySets.getObfuscater();

        /* Check the keySets are the same */
        Assertions.assertEquals(myKeySet1, myKeySet, "Failed to derive keySet");

        /* Derive the Mac */
        final GordianMacSpec myMacSpec = myKnuth.deriveTypeFromExternalId(myMacId, GordianMacSpec.class);
        final GordianKey<GordianMacSpec> myMacKey = myKeySet1.deriveKey(myMacSafe, myMacSpec);
        myMac = myMacs.createMac(myMacSpec);
        myMac.initMac(myMacKey, myIV);
        myMac.update(mySymSafe);
        myMac.update(myStreamSafe);
        final byte[] myMac1Bytes = myMac.finish();
        Assertions.assertArrayEquals(myMacBytes, myMac1Bytes, "Failed to recalculate mac");

        /* Create a message digest */
        myDigest = myDigests.createDigest(myDigest.getDigestSpec());
        myDigest.update(mySymSafe);
        myDigest.update(myStreamSafe);
        final byte[] myNewBytes = myDigest.finish();
        Assertions.assertArrayEquals(myDigestBytes, myNewBytes, "Failed to recalculate digest");

        /* Derive the keys */
        final GordianKey<GordianSymKeySpec> mySym1 = myKeySet1.deriveKey(mySymSafe, mySym.getKeyType());
        Assertions.assertEquals(mySym1, mySym,"Failed to decrypt symmetricKey");
        final GordianKey<GordianStreamKeyType> myStm1 = myKeySet1.deriveKey(myStreamSafe, myStream.getKeyType());
        Assertions.assertEquals(myStm1, myStream,"Failed to decrypt streamKey");

        /* Decrypt the bytes */
        byte[] myResult = myKeySet1.decryptBytes(myEncrypt1);
        String myAnswer = TethysDataConverter.byteArrayToString(myResult);
        Assertions.assertEquals(myAnswer, myTest1,"Failed to decrypt test1 string");
        myResult = myKeySet1.decryptBytes(myEncrypt2);
        myAnswer = TethysDataConverter.byteArrayToString(myResult);
        Assertions.assertEquals(myAnswer, myTest2,"Failed to decrypt test2 string");
        myResult = myKeySet1.decryptBytes(myEncrypt3);
        myAnswer = TethysDataConverter.byteArrayToString(myResult);
        Assertions.assertEquals(myAnswer, myTest3, "Failed to decrypt test3 string");
    }
}
