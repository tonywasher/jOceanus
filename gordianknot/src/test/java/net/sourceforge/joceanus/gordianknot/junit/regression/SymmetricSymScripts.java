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
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherMode;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianKeyedCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPBESpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymAEADCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreWrapper;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactorySymCipherSpec;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactorySymKeySpec;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactorySymPBECipherSpec;
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
 * SymKey/Cipher scripts.
 */
public class SymmetricSymScripts {
    /**
     * Private constructor.
     */
    private SymmetricSymScripts() {
    }

    /**
     * Create the symKey test suite for a factory.
     * @param pFactory the factory
     * @return the test stream or null
     */
    static Stream<DynamicNode> symKeyTests(final GordianFactory pFactory,
                                           final GordianFactory pPartner) {
        /* Create the default stream */
        Stream<DynamicNode> myTests = Stream.empty();

        /* Loop through the keyLengths */
        Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            final GordianLength myKeyLen = myIterator.next();

            /* Build tests for this keyLength */
            final Stream<DynamicNode> myTest = symKeyTests(pFactory, pPartner, myKeyLen);
            if (myTest != null) {
                myTests = Stream.concat(myTests, myTest);
            }
        }

        /* Return the tests */
        return Stream.of(DynamicContainer.dynamicContainer("symKeys", myTests));
    }

    /**
     * Create the symKey test suite for a factory.
     * @param pFactory the factory
     * @param pPartner the partner
     * @param pKeyLen the keyLength
     * @return the test stream or null
     */
    private static Stream<DynamicNode> symKeyTests(final GordianFactory pFactory,
                                                   final GordianFactory pPartner,
                                                   final GordianLength pKeyLen) {
        /* Add symKey Test */
        List<FactorySymKeySpec> myKeys = SymmetricStore.symKeyProvider(pFactory, pPartner, pKeyLen);
        if (!myKeys.isEmpty()) {
            Stream<DynamicNode> myTests = myKeys.stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), symKeyTests(x)));
            return Stream.of(DynamicContainer.dynamicContainer(pKeyLen.toString(), myTests));
        }

        /* No sym Tests */
        return null;
    }

    /**
     * Create the symKey test suite for a symKeySpec.
     * @param pKeySpec the keySpec
     * @return the test stream
     */
    private static Stream<DynamicNode> symKeyTests(final FactorySymKeySpec pKeySpec) {
        /* Add profile test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("profile", () -> profileSymKey(pKeySpec)));

        /* Add modes test */
        myTests = Stream.concat(myTests, Stream.of(DynamicContainer.dynamicContainer("checkModes",
                SymmetricStore.symCipherProvider(pKeySpec).stream().map(y -> DynamicContainer.dynamicContainer(y.toString(), symCipherTests(y)))
        )));

        /* Add externalId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("externalId", () -> SymmetricTest.checkExternalId(pKeySpec))));

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("algorithmId", () -> checkSymKeyAlgId(pKeySpec))));

        /* Add wrapCipher test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("wrapCipher", () -> checkWrapCipher(pKeySpec))));

        /* Add partner test if  the partner supports this symKeySpec */
        if (pKeySpec.getPartner() != null) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("PartnerWrap", () -> checkPartnerWrapCipher(pKeySpec))));
        }

        /* Return the tests */
        return myTests;
    }

    /**
     * Create the symKey test suite for a symCipherSpec.
     * @param pCipherSpec the cipherSpec
     * @return the test stream
     */
    private static Stream<DynamicNode> symCipherTests(final FactorySymCipherSpec pCipherSpec) {
        /* Add profile test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("cipher", () -> checkSymCipher(pCipherSpec)));

        /* Add multi test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("multi", () -> multiSymCipher(pCipherSpec))));

        /* Add externalId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("externalId", () -> SymmetricTest.checkExternalId(pCipherSpec))));

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("algorithmId", () -> checkSymCipherAlgId(pCipherSpec))));

        /* Add partner test if  the partner supports this symCipherSpec */
        if (pCipherSpec.getPartner() != null) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("Partner", () -> checkPartnerSymCipher(pCipherSpec))));
        }

        /* Add PBE tests */
        myTests = Stream.concat(myTests, Stream.of(DynamicContainer.dynamicContainer("PBE", symPBECipherTests(pCipherSpec))));

        /* Return the tests */
        return myTests;
    }

    /**
     * Create the symPBECipher test suite for a symCipherSpec.
     * @param pCipherSpec the cipherSpec
     * @return the test stream
     */
    private static Stream<DynamicNode> symPBECipherTests(final FactorySymCipherSpec pCipherSpec) {
        /* Add PBE  tests */
        Stream<DynamicNode> myTests = Stream.empty();
        for (FactorySymPBECipherSpec myPBESpec : SymmetricStore.symPBECipherProvider(pCipherSpec)) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest(myPBESpec.toString(), () -> checkSymPBECipher(myPBESpec))));
        }

        /* Return the tests */
        return myTests;
    }

    /**
     * Check symKey CipherMode.
     * @param pCipherSpec the cipherSpec
     * @throws GordianException on error
     */
    private static void checkSymCipher(final FactorySymCipherSpec pCipherSpec) throws GordianException {
        /* Split out AAD cipher */
        if (pCipherSpec.getSpec().isAAD()) {
            checkSymAADCipher(pCipherSpec);
            return;
        }

        /* Access details */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final GordianSymCipherSpec mySpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianSymKeySpec> myKey = pCipherSpec.getKey();

        /* Access Data */
        final byte[] myTestData = getSymCipherTestData(mySpec);

        /* Create the Spec */
        final GordianSymCipher myCipher = myCipherFactory.createSymKeyCipher(mySpec);
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myKey);
        myCipher.initForEncrypt(myParms);

        /* Check encryption */
        final byte[] myIV = Arrays.clone(myCipher.getInitVector());
        myParms = GordianCipherParameters.keyAndNonce(myKey, myIV);
        final byte[] myEncrypted = myCipher.finish(myTestData);
        final byte[] myEncrypted2 = myCipher.finish(myTestData);
        myCipher.initForDecrypt(myParms);
        final byte[] myResult = myCipher.finish(myEncrypted);
        myCipher.initForDecrypt(myParms);
        final byte[] myResult2 = myCipher.finish(myEncrypted2);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
        Assertions.assertArrayEquals(myResult, myResult2, "Failed to reset properly");
    }

    /**
     * Obtain testData for CipherSpec.
     * @param pSpec the cipherSpec
     */
    private static byte[] getSymCipherTestData(final GordianSymCipherSpec pSpec) {
        /* Access Data */
        final byte[] myTestData = SymmetricTest.getTestData();

        /* If we need to process in blocks but have no padding */
        if (pSpec.getCipherMode().hasPadding()
                && GordianPadding.NONE.equals(pSpec.getPadding())) {
            /* Limit test data to multiple of blocks */
            final int myBlockLen = pSpec.getBlockLength().getByteLength();
            final int myDataLen = myBlockLen * (myTestData.length / myBlockLen);
            return Arrays.copyOf(myTestData, myDataLen);
        }

        /* Else return the standard data */
        return myTestData;
    }

    /**
     * Multi-call symCipher.
     * @param pCipherSpec the cipher to profile
     */
    private static void multiSymCipher(final FactorySymCipherSpec pCipherSpec) throws GordianException {
        /* Create the cipher */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final GordianSymCipherSpec mySpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianSymCipher myCipher1 = myCipherFactory.createSymKeyCipher(mySpec);
        final GordianSymCipher myCipher2 = myCipherFactory.createSymKeyCipher(mySpec);
        final GordianKey<GordianSymKeySpec> myKey = pCipherSpec.getKey();

        /* Access Data */
        final byte[] myBytes = getSymCipherTestData(mySpec);

        /* Encrypt the data as a single block */
        myCipher1.initForEncrypt(GordianCipherParameters.keyWithRandomNonce(myKey));
        byte[] myEncrypt = new byte[myCipher1.getOutputLength(myBytes.length)];
        int myOut = myCipher1.update(myBytes, 0, myBytes.length, myEncrypt, 0);
        myOut += myCipher1.finish(myEncrypt, myOut);
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
        myOut += myCipher1.finish(mySingle, myOut);
        mySingle = Arrays.copyOf(mySingle, myOut);

        /* Check that the results are identical */
        Assertions.assertEquals(myBytes.length, myOut, "Multi-Block encrypt length failed");
        Assertions.assertArrayEquals(myBytes, mySingle, "Multi-Block encrypt failed");
    }

    /**
     * Check symKey PBE CipherMode.
     * @param pCipherSpec the cipherSpec
     * @throws GordianException on error
     */
    private static void checkSymPBECipher(final FactorySymPBECipherSpec pCipherSpec) throws GordianException {
        /* Access details */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final FactorySymCipherSpec myOwner = pCipherSpec.getOwner();
        final GordianSymCipherSpec myCipherSpec = myOwner.getSpec();
        final GordianPBESpec myPBESpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();

        /* Access Data */
        final byte[] myTestData = getSymCipherTestData(myCipherSpec);
        final char[] myPassword = "HelloThere".toCharArray();

        /* Create the Spec */
        final GordianSymCipher myCipher = myCipherFactory.createSymKeyCipher(myCipherSpec);
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
     * Check partner symKey CipherMode.
     * @param pCipherSpec the cipherSpec
     * @throws GordianException on error
     */
    private static void checkPartnerSymCipher(final FactorySymCipherSpec pCipherSpec) throws GordianException {
        /* Split out AAD cipher */
        if (pCipherSpec.getSpec().isAAD()) {
            checkPartnerSymAADCipher(pCipherSpec);
            return;
        }

        /* Access details */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final GordianFactory myPartner = pCipherSpec.getPartner();
        final GordianSymCipherSpec mySpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianCipherFactory myPartnerFactory = myPartner.getCipherFactory();
        final GordianKey<GordianSymKeySpec> myKey = pCipherSpec.getKey();
        final GordianKey<GordianSymKeySpec> myPartnerKey = pCipherSpec.getPartnerKey();

        /* Access Data */
        final byte[] myTestData = SymmetricTest.getTestData();

        /* Create the Spec */
        final GordianKeyedCipher<GordianSymKeySpec> myCipher = myCipherFactory.createSymKeyCipher(mySpec);
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myKey);
        myCipher.initForEncrypt(myParms);
        if (!mySpec.getCipherMode().hasPadding()
                || !GordianPadding.NONE.equals(mySpec.getPadding())) {
            /* Check encryption */
            final byte[] myIV = myCipher.getInitVector();
            myParms = GordianCipherParameters.keyAndNonce(myPartnerKey, myIV);
            final byte[] myEncrypted = myCipher.finish(myTestData);
            final GordianKeyedCipher<GordianSymKeySpec> myPartnerCipher = myPartnerFactory.createSymKeyCipher(mySpec);
            myPartnerCipher.initForDecrypt(myParms);
            final byte[] myResult = myPartnerCipher.finish(myEncrypted);
            Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
        }
    }

    /**
     * Check AAD cipher mode.
     * @param pCipherSpec the cipherSpec
     * @throws GordianException on error
     */
    private static void checkSymAADCipher(final FactorySymCipherSpec pCipherSpec) throws GordianException {
        /* Access details */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final GordianSymCipherSpec mySpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianSymKeySpec> myKey = pCipherSpec.getKey();

        /* Access Data */
        final byte[] myTestData = SymmetricTest.getTestData();
        final byte[] myAADData = SymmetricTest.getAADData();
        final GordianSymAEADCipher myCipher = (GordianSymAEADCipher) myCipherFactory.createSymKeyCipher(mySpec);
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myKey);
        myCipher.initForEncrypt(myParms);
        final byte[] myIV = myCipher.getInitVector();
        myCipher.updateAAD(myAADData);
        final byte[] myEncrypted = myCipher.finish(myTestData);
        byte[] myEncrypted2 = null;
        if (!mySpec.getCipherMode().needsReInitialisation()) {
            myCipher.updateAAD(myAADData);
            myEncrypted2 = myCipher.finish(myTestData);
        }
        myParms = GordianCipherParameters.aeadAndNonce(myKey, myAADData, myIV);
        myCipher.initForDecrypt(myParms);
        final byte[] myResult = myCipher.finish(myEncrypted);
        if (myEncrypted2 != null) {
            myCipher.initForDecrypt(myParms);
            final byte[] myResult2 = myCipher.finish(myEncrypted2);
            Assertions.assertArrayEquals(myResult, myResult2, "Failed to reset properly");
        }
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
    }

    /**
     * Check Partner AAD cipher mode.
     * @param pCipherSpec the cipherSpec
     * @throws GordianException on error
     */
    private static void checkPartnerSymAADCipher(final FactorySymCipherSpec pCipherSpec) throws GordianException {
        /* Access details */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final GordianFactory myPartner = pCipherSpec.getPartner();
        final GordianSymCipherSpec mySpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianCipherFactory myPartnerFactory = myPartner.getCipherFactory();
        final GordianKey<GordianSymKeySpec> myKey = pCipherSpec.getKey();
        final GordianKey<GordianSymKeySpec> myPartnerKey = pCipherSpec.getPartnerKey();

        /* Encrypt Data */
        final byte[] myTestData = SymmetricTest.getTestData();
        final byte[] myAADData = SymmetricTest.getAADData();
        final GordianSymAEADCipher myCipher = (GordianSymAEADCipher) myCipherFactory.createSymKeyCipher(mySpec);
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myKey);
        myCipher.initForEncrypt(myParms);
        final byte[] myIV = myCipher.getInitVector();
        myCipher.updateAAD(myAADData);
        final byte[] myEncrypted = myCipher.finish(myTestData);

        /* Decrypt data at partner */
        final GordianSymAEADCipher myPartnerCipher = (GordianSymAEADCipher) myPartnerFactory.createSymKeyCipher(mySpec);
        myParms = GordianCipherParameters.keyAndNonce(myPartnerKey, myIV);
        myPartnerCipher.initForDecrypt(myParms);
        myPartnerCipher.updateAAD(myAADData);
        final byte[] myResult = myPartnerCipher.finish(myEncrypted);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
    }

    /**
     * Check wrap cipher.
     * @param pKeySpec the keySpec
     * @throws GordianException on error
     */
    private static void checkWrapCipher(final FactorySymKeySpec pKeySpec) throws GordianException {
        /* Access details */
        final GordianFactory myFactory = pKeySpec.getFactory();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianSymKeySpec> mySymKey = pKeySpec.getKey();

        /* Access Data */
        final byte[] myTestData = SymmetricTest.getTestData();

        /* Check wrapping bytes */
        final GordianCoreWrapper myWrapper = (GordianCoreWrapper) myCipherFactory.createKeyWrapper(mySymKey);
        byte[] myWrapped = myWrapper.secureBytes( myTestData);
        final byte[] myResult = myWrapper.deriveBytes(myWrapped);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to wrap/unwrap bytes");
        Assertions.assertEquals(myWrapper.getDataWrapLength(myTestData.length), myWrapped.length, "Incorrect wrapped length");

        /* Check wrapping key */
        myWrapped = myWrapper.secureKey(mySymKey);
        final GordianKey<GordianSymKeySpec> myResultKey = myWrapper.deriveKey(myWrapped, mySymKey.getKeyType());
        Assertions.assertEquals(mySymKey, myResultKey, "Failed to wrap/unwrap key");
        Assertions.assertEquals(myWrapper.getKeyWrapLength(mySymKey), myWrapped.length, "Incorrect wrapped length");
    }

    /**
     * Check partner wrap cipher.
     * @param pKeySpec the keySpec
     * @throws GordianException on error
     */
    private static void checkPartnerWrapCipher(final FactorySymKeySpec pKeySpec) throws GordianException {
        /* Access details */
        final GordianFactory myFactory = pKeySpec.getFactory();
        final GordianFactory myPartner = pKeySpec.getPartner();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianCipherFactory myPartnerFactory = myPartner.getCipherFactory();
        final GordianKey<GordianSymKeySpec> mySymKey = pKeySpec.getKey();
        final GordianKey<GordianSymKeySpec> myPartnerKey = pKeySpec.getPartnerKey();

        /* Access Data */
        final byte[] myTestData = SymmetricTest.getTestData();

        /* Check wrapping bytes */
        final GordianCoreWrapper myWrapper = (GordianCoreWrapper) myCipherFactory.createKeyWrapper(mySymKey);
        byte[] myWrapped = myWrapper.secureBytes(myTestData);
        final GordianCoreWrapper myPartnerWrapper = (GordianCoreWrapper) myPartnerFactory.createKeyWrapper(myPartnerKey);
        final byte[] myResult = myPartnerWrapper.deriveBytes(myWrapped);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to wrap/unwrap bytes");
        Assertions.assertEquals(myWrapper.getDataWrapLength(myTestData.length), myWrapped.length, "Incorrect wrapped length");

        /* Check wrapping key */
        myWrapped = myWrapper.secureKey(mySymKey);
        final GordianKey<GordianSymKeySpec> myResultKey = myPartnerWrapper.deriveKey(myWrapped, mySymKey.getKeyType());
        Assertions.assertEquals(myPartnerKey, myResultKey, "Failed to wrap/unwrap key");
        Assertions.assertEquals(myWrapper.getKeyWrapLength(mySymKey), myWrapped.length, "Incorrect wrapped length");
    }

    /**
     * Profile symKey.
     * @param pKeySpec the keySpec
     * @throws GordianException on error
     */
    private static void profileSymKey(final FactorySymKeySpec pKeySpec) throws GordianException {
        /* Access details */
        final GordianFactory myFactory = pKeySpec.getFactory();
        final GordianSymKeySpec mySpec = pKeySpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianSymKeySpec> mySymKey = pKeySpec.getKey();
        final int myLen = mySpec.getBlockLength().getByteLength();

        /* Build the cipher */
        byte[] myBytes = new byte[myLen];
        final GordianSymCipherSpec myCipherSpec = new GordianSymCipherSpec(mySpec, GordianCipherMode.ECB, GordianPadding.NONE);
        final GordianSymCipher myCipher = myCipherFactory.createSymKeyCipher(myCipherSpec);

        /* Start loop */
        final long myStart = System.nanoTime();
        GordianCipherParameters myParms = GordianCipherParameters.key(mySymKey);
        for (int i = 0; i < SymmetricTest.profileRepeat; i++) {
            myCipher.initForEncrypt(myParms);
            myBytes = myCipher.finish(myBytes);
        }
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= SymmetricTest.MILLINANOS * (long) SymmetricTest.profileRepeat;
        if (SymmetricTest.fullProfiles) {
            System.out.println(mySpec + ":" + myElapsed);
        }
    }

    /**
     * Check keyAlgId.
     * @param pSpec the Spec to check
     */
    private static void checkSymKeyAlgId(final FactorySymKeySpec pSpec) {
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
     * Check cipherAlgId.
     * @param pSpec the Spec to check
     */
    private static void checkSymCipherAlgId(final FactorySymCipherSpec pSpec) {
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
