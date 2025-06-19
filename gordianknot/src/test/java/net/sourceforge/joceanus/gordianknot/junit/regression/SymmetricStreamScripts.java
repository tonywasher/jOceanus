/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.junit.regression;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPBESpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamAEADCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactoryStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactoryStreamKeySpec;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactoryStreamPBECipherSpec;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * StreamKey/Cipher scripts.
 */
public class SymmetricStreamScripts {
    /**
     * Private constructor.
     */
    private SymmetricStreamScripts() {
    }

    /**
     * Create the streamKey test suite for a factory.
     * @param pFactory the factory
     * @param pPartner the partner
     * @return the test stream or null
     */
    static Stream<DynamicNode> streamKeyTests(final GordianFactory pFactory,
                                              final GordianFactory pPartner) {
        /* Create the default stream */
        Stream<DynamicNode> myTests = Stream.empty();

        /* Loop through the keyLengths */
        Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            final GordianLength myKeyLen = myIterator.next();

            /* Build tests for this keyLength */
            final Stream<DynamicNode> myTest = streamKeyTests(pFactory, pPartner, myKeyLen);
            if (myTest != null) {
                myTests = Stream.concat(myTests, myTest);
            }
        }

        /* Return the tests */
        return Stream.of(DynamicContainer.dynamicContainer("streamKeys", myTests));
    }

    /**
     * Create the streamKey test suite for a factory.
     * @param pFactory the factory
     * @param pPartner the partner
     * @param pKeyLen the keyLength
     * @return the test stream or null
     */
    private static Stream<DynamicNode> streamKeyTests(final GordianFactory pFactory,
                                                      final GordianFactory pPartner,
                                                      final GordianLength pKeyLen) {
        /* Add streamKey Tests */
        List<FactoryStreamKeySpec> myKeys = SymmetricStore.streamKeyProvider(pFactory, pPartner, pKeyLen);
        if (!myKeys.isEmpty()) {
            Stream<DynamicNode> myTests = myKeys.stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), streamKeyTests(x)));
            return Stream.of(DynamicContainer.dynamicContainer(pKeyLen.toString(), myTests));
        }

        /* No stream Tests */
        return null;
    }

    /**
     * Create the streamKey test suite for a streamKeySpec.
     * @param pKeySpec the keySpec
     * @return the test stream
     */
    private static Stream<DynamicNode> streamKeyTests(final FactoryStreamKeySpec pKeySpec) {
        /* Add profile test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("profile", () -> profileStreamKey(pKeySpec)));

        /* Add cipher test */
        final FactoryStreamCipherSpec myCipherSpec = new FactoryStreamCipherSpec(pKeySpec, GordianStreamCipherSpecBuilder.stream(pKeySpec.getSpec()));
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("checkCipher", () -> checkStreamCipher(myCipherSpec))));

        /* Add multi test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("multi", () -> multiStreamCipher(myCipherSpec))));

        /* Add externalId tests */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("externalKeyId", () -> SymmetricTest.checkExternalId(pKeySpec))));
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("externalCipherId", () -> SymmetricTest.checkExternalId(myCipherSpec))));

        /* Add algorithmId tests */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("algorithmKeyId", () -> checkStreamKeyAlgId(pKeySpec))));
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("algorithmCipherId", () -> checkStreamCipherAlgId(myCipherSpec))));

        /* Add partner test if  the partner supports this streamKeySpec */
        if (pKeySpec.getPartner() != null) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("Partner", () -> checkPartnerStreamKey(pKeySpec))));
        }

        /* Add PBE tests */
        myTests = Stream.concat(myTests, Stream.of(DynamicContainer.dynamicContainer("PBE", streamPBECipherTests(myCipherSpec))));

        /* Add AAD cipher tests if required */
        if (pKeySpec.hasAADVersion()) {
            final FactoryStreamCipherSpec myAADSpec = new FactoryStreamCipherSpec(pKeySpec, GordianStreamCipherSpecBuilder.stream(pKeySpec.getSpec(), true));
            myTests = Stream.concat(myTests, Stream.of(DynamicContainer.dynamicContainer("AAD", streamAADCipherTests(myAADSpec))));
        }

        /* Return the tests */
        return myTests;
    }

    /**
     * Create the streamPBECipher test suite for a streamCipherSpec.
     * @param pCipherSpec the cipherSpec
     * @return the test stream
     */
    private static Stream<DynamicNode> streamPBECipherTests(final FactoryStreamCipherSpec pCipherSpec) {
        /* Add PBE  tests */
        Stream<DynamicNode> myTests = Stream.empty();
        for (FactoryStreamPBECipherSpec mySpec : SymmetricStore.streamPBECipherProvider(pCipherSpec)) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest(mySpec.toString(), () -> checkStreamPBECipher(mySpec))));
        }

        /* Return the tests */
        return myTests;
    }

    /**
     * Create the streamPBECipher test suite for a streamCipherSpec.
     * @param pCipherSpec the cipherSpec
     * @return the test stream
     */
    private static Stream<DynamicNode> streamAADCipherTests(final FactoryStreamCipherSpec pCipherSpec) {
        /* Build standard tests */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("checkCipher", () -> checkStreamAADCipher(pCipherSpec)));
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("externalCipherId", () -> SymmetricTest.checkExternalId(pCipherSpec))));
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("algorithmId", () -> checkStreamCipherAlgId(pCipherSpec))));

        /* Add PBE tests */
        myTests = Stream.concat(myTests, Stream.of(DynamicContainer.dynamicContainer("PBE", streamPBECipherTests(pCipherSpec))));

        /* Return the tests */
        return myTests;
    }

    /**
     * Check stream cipher.
     * @param pCipherSpec the keySpec
     * @throws GordianException on error
     */
    private static void checkStreamCipher(final FactoryStreamCipherSpec pCipherSpec) throws GordianException {
        /* If this cipher is in fact an AEAD cipher */
        /* Elephant, ISAP, PhotonBeetle and Xoodyak do not currently support AEADParameters so treat as non-AEAD for the time being */
        if (pCipherSpec.getSpec().isAEAD()) {
            /* Test as AEAD cipher */
            checkStreamAADCipher(pCipherSpec);
            return;
        }

        /* Access details */
        final FactoryStreamKeySpec myOwner = pCipherSpec.getOwner();
        final GordianFactory myFactory = myOwner.getFactory();
        final GordianStreamKeySpec myKeySpec = myOwner.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianStreamKeySpec> myStreamKey = myOwner.getKey();

        /* Access Data */
        final byte[] myTestData = SymmetricTest.getTestData();

        /* Create the Cipher */
        final GordianStreamCipherSpec myCipherSpec = GordianStreamCipherSpecBuilder.stream(myKeySpec);
        final GordianStreamCipher myCipher = myCipherFactory.createStreamKeyCipher(myCipherSpec);
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myStreamKey);
        myCipher.initForEncrypt(myParms);
        final byte[] myIV = myCipher.getInitVector();
        myParms = GordianCipherParameters.keyAndNonce(myStreamKey, myIV);
        final byte[] myEncrypted = myCipher.finish(myTestData);
        if (myCipherSpec.getKeyType().getStreamKeyType().needsReInit()) {
            myCipher.initForEncrypt(myParms);
        }
        final byte[] myEncrypted2 = myCipher.finish(myTestData);
        myCipher.initForDecrypt(myParms);
        final byte[] myResult = myCipher.finish(myEncrypted);
        myCipher.initForDecrypt(myParms);
        final byte[] myResult2 = myCipher.finish(myEncrypted2);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
        Assertions.assertArrayEquals(myResult, myResult2, "Failed to reset properly");
    }

    /**
     * Multi-call symCipher.
     * @param pCipherSpec the cipher to profile
     */
    private static void multiStreamCipher(final FactoryStreamCipherSpec pCipherSpec) throws GordianException {
        /* Create the cipher */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final GordianStreamCipherSpec mySpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianStreamCipher myCipher1 = myCipherFactory.createStreamKeyCipher(mySpec);
        final GordianStreamCipher myCipher2 = myCipherFactory.createStreamKeyCipher(mySpec);
        final GordianKey<GordianStreamKeySpec> myKey = pCipherSpec.getKey();

        /* Access the test data */
        final byte[] myBytes = SymmetricTest.getTestData();

        /* Encrypt the data as a single block */
        myCipher1.initForEncrypt(GordianCipherParameters.keyWithRandomNonce(myKey));
        byte[] myEncrypt = new byte[myCipher1.getOutputLength(myBytes.length)];
        int myOut = myCipher1.update(myBytes, 0, myBytes.length, myEncrypt, 0);
        int myXpected = myOut + myCipher1.getOutputLength(0);
        myOut += myCipher1.finish(myEncrypt, myOut);
        Assertions.assertEquals(myOut, myXpected, "Output length fails on encryption");
        myEncrypt = Arrays.copyOf(myEncrypt, myOut);

        /* Decrypt the data as partial blocks */
        myCipher2.initForDecrypt(GordianCipherParameters.keyAndNonce(myKey, myCipher1.getInitVector()));
        byte[] myMulti = new byte[myCipher2.getOutputLength(myEncrypt.length)];
        myOut = 0;
        for (int myPos = 0; myPos < myEncrypt.length; myPos += SymmetricTest.PARTIALLEN) {
            final int myLen = Math.min(SymmetricTest.PARTIALLEN, myEncrypt.length - myPos);
            myOut += myCipher2.update(myEncrypt, myPos, myLen, myMulti, myOut);
        }
        myOut += myCipher2.finish(myMulti, myOut);
        myMulti = Arrays.copyOf(myMulti, myOut);

        /* Check that the results are identical */
        Assertions.assertEquals(myBytes.length, myOut, "Multi-Block decrypt length failed");
        Assertions.assertArrayEquals(myBytes, myMulti, "Multi-Block decrypt failed");

        /* Encrypt the data as partial blocks */
        myCipher2.initForEncrypt(GordianCipherParameters.keyWithRandomNonce(myKey));
        myOut = 0;
        for (int myPos = 0; myPos < myBytes.length; myPos += SymmetricTest.PARTIALLEN) {
            final int myLen = Math.min(SymmetricTest.PARTIALLEN, myBytes.length - myPos);
            myOut += myCipher2.update(myBytes, myPos, myLen, myEncrypt, myOut);
        }
        myOut += myCipher2.finish(myEncrypt, myOut);
        myEncrypt = Arrays.copyOf(myEncrypt, myOut);

        /* Decrypt the data as single block */
        myCipher1.initForDecrypt(GordianCipherParameters.keyAndNonce(myKey, myCipher2.getInitVector()));
        byte[] mySingle = new byte[myCipher1.getOutputLength(myEncrypt.length)];
        myOut = myCipher1.update(myEncrypt, 0, myEncrypt.length, mySingle, 0);
        myXpected = myOut + myCipher1.getOutputLength(0);
        myOut += myCipher1.finish(mySingle, myOut);
        Assertions.assertEquals(myOut, myXpected, "Output length fails on decryption");
        mySingle = Arrays.copyOf(mySingle, myOut);

        /* Check that the results are identical */
        Assertions.assertEquals(myBytes.length, myOut, "Multi-Block encrypt length failed");
        Assertions.assertArrayEquals(myBytes, mySingle, "Multi-Block encrypt failed");
    }

    /**
     * Check streamKey PBE CipherMode.
     * @param pCipherSpec the cipherSpec
     * @throws GordianException on error
     */
    private static void checkStreamPBECipher(final FactoryStreamPBECipherSpec pCipherSpec) throws GordianException {
        /* Access details */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final FactoryStreamCipherSpec myOwner = pCipherSpec.getOwner();
        final GordianStreamCipherSpec myCipherSpec = myOwner.getSpec();
        final GordianPBESpec myPBESpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();

        /* Access Data */
        final byte[] myTestData = SymmetricTest.getTestData();
        final char[] myPassword = "HelloThere".toCharArray();

        /* Create the Spec */
        final GordianStreamCipher myCipher = myCipherFactory.createStreamKeyCipher(myCipherSpec);
        GordianCipherParameters myParms = GordianCipherParameters.pbe(myPBESpec, myPassword);
        myCipher.initForEncrypt(myParms);

        /* Check encryption */
        final byte[] mySalt = myCipher.getPBESalt();
        myParms = GordianCipherParameters.pbeAndNonce(myPBESpec, myPassword, mySalt);
        final byte[] myEncrypted = myCipher.finish(myTestData);
        myCipher.initForDecrypt(myParms);
        final byte[] myResult = myCipher.finish(myEncrypted);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
    }

    /**
     * Check AAD cipher mode.
     * @param pCipherSpec the cipherSpec
     * @throws GordianException on error
     */
    private static void checkStreamAADCipher(final FactoryStreamCipherSpec pCipherSpec) throws GordianException {
        /* Access details */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final GordianStreamCipherSpec mySpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianStreamKeySpec> myKey = pCipherSpec.getKey();

        /* Access Data */
        final byte[] myTestData = SymmetricTest.getTestData();
        final byte[] myAADData = SymmetricTest.getAADData();
        final GordianStreamAEADCipher myCipher = (GordianStreamAEADCipher) myCipherFactory.createStreamKeyCipher(mySpec);
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myKey);
        myCipher.initForEncrypt(myParms);
        final byte[] myIV = myCipher.getInitVector();
        myCipher.updateAAD(myAADData);
        final byte[] myEncrypted = myCipher.finish(myTestData);
        myParms = GordianCipherParameters.aeadAndNonce(myKey, myAADData, myIV);
        myCipher.initForDecrypt(myParms);
        final byte[] myResult = myCipher.finish(myEncrypted);
        myCipher.initForDecrypt(myParms);
        final byte[] myResult2 = myCipher.finish(myEncrypted);
        Assertions.assertArrayEquals(myResult, myResult2, "Failed to reset properly");
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
    }

    /**
     * Check partner streamKey.
     * @param pKeySpec the streamKey to check
     */
    private static void checkPartnerStreamKey(final FactoryStreamKeySpec pKeySpec) throws GordianException {
        /* Create the macs */
        final GordianFactory myFactory = pKeySpec.getFactory();
        final GordianFactory myPartner = pKeySpec.getPartner();
        final GordianStreamKeySpec mySpec = pKeySpec.getSpec();
        final GordianStreamCipherSpec myCipherSpec = GordianStreamCipherSpecBuilder.stream(mySpec);
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianStreamCipher myCipher = myCipherFactory.createStreamKeyCipher(myCipherSpec);
        final GordianCipherFactory myPartnerFactory = myPartner.getCipherFactory();
        final GordianStreamCipher myPartnerCipher = myPartnerFactory.createStreamKeyCipher(myCipherSpec);
        final GordianKey<GordianStreamKeySpec> myKey = pKeySpec.getKey();
        final GordianKey<GordianStreamKeySpec> myPartnerKey = pKeySpec.getPartnerKey();

        /* Create message and buffers  */
        final byte[] myBytes = SymmetricTest.getTestData();

        /* Encrypt and decrypt the message */
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myKey);
        myCipher.initForEncrypt(myParms);
        final byte[] myEncrypted = myCipher.finish(myBytes);
        final byte[] myIV = myCipher.getInitVector();
        myPartnerCipher.initForDecrypt(GordianCipherParameters.keyAndNonce(myPartnerKey, myIV));
        final byte[] myDecrypted = myPartnerCipher.finish(myEncrypted);

        /* Check that the decryption worked */
        Assertions.assertArrayEquals(myBytes, myDecrypted, "cipher misMatch");
    }

    /**
     * Profile streamKey.
     * @param pStreamKeySpec the keySpec
     * @throws GordianException on error
     */
    private static void profileStreamKey(final FactoryStreamKeySpec pStreamKeySpec) throws GordianException {
        final GordianFactory myFactory = pStreamKeySpec.getFactory();
        final GordianStreamKeySpec myKeySpec = pStreamKeySpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianStreamKeySpec> myStreamKey = pStreamKeySpec.getKey();
        byte[] myBytes = SymmetricTest.getTestData();
        final GordianStreamCipherSpec myCipherSpec = GordianStreamCipherSpecBuilder.stream(myKeySpec);
        final GordianStreamCipher myCipher = myCipherFactory.createStreamKeyCipher(myCipherSpec);
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myStreamKey);
        final long myStart = System.nanoTime();
        for (int i = 0; i < SymmetricTest.profileRepeat; i++) {
            myCipher.initForEncrypt(myParms);
            myBytes = myCipher.finish(myBytes);
        }
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= SymmetricTest.MILLINANOS * (long) SymmetricTest.profileRepeat;
        if (SymmetricTest.fullProfiles) {
            System.out.println(myKeySpec.toString() + ":" + myElapsed);
        }
    }

    /**
     * Check keyAlgId.
     * @param pSpec the Spec to check
     */
    private static void checkStreamKeyAlgId(final FactoryStreamKeySpec pSpec) {
        /* Access the factory */
        final GordianCoreFactory myFactory = (GordianCoreFactory) pSpec.getFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pSpec.getSpec());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pSpec.getSpec());

        /* Check unique mapping */
        final GordianKeySpec mySpec = myFactory.getKeySpecForIdentifier(myId);
        Assertions.assertEquals(pSpec.getSpec(), mySpec, "Invalid mapping for  " + pSpec.getSpec());
    }

    /**
     * Check streamCipherAlgId.
     * @param pSpec the Spec to check
     */
    private static void checkStreamCipherAlgId(final FactoryStreamCipherSpec pSpec) {
        /* Access the factory */
        final GordianCoreCipherFactory myFactory = (GordianCoreCipherFactory) pSpec.getFactory().getCipherFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pSpec.getSpec());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pSpec.getSpec());

        /* Check unique mapping */
        final GordianCipherSpec<?> mySpec = myFactory.getCipherSpecForIdentifier(myId);
        Assertions.assertEquals(pSpec.getSpec(), mySpec, "Invalid mapping for  " + pSpec.getSpec());
    }
}
