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
package net.sourceforge.joceanus.jgordianknot.test.api;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianAADCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianWrapper;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKnuthObfuscater;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianSecurityManager;
import net.sourceforge.joceanus.jgordianknot.test.api.GordianTestSuite.SecurityManagerCreator;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Security Test suite - Test Symmetric/Stream and Digest/MAC Algorithms.
 */
public class GordianTestAlgorithms {
    /**
     * The Security Manager creator.
     */
    private final SecurityManagerCreator theCreator;

    /**
     * The TestData.
     */
    private byte[] theTestData;

    /**
     * The AADData.
     */
    private byte[] theAADData;

    /**
     * Constructor.
     * @param pCreator the Secure Manager creator
     */
    GordianTestAlgorithms(final SecurityManagerCreator pCreator) {
        theCreator = pCreator;
    }

    /**
     * Check the supported algorithms.
     * @throws OceanusException on error
     */
    void checkAlgorithms() throws OceanusException {
        checkAlgorithms(true, GordianFactoryType.BC);
        checkAlgorithms(false, GordianFactoryType.BC);
        checkAlgorithms(true, GordianFactoryType.JCA);
        checkAlgorithms(false, GordianFactoryType.JCA);
    }

    /**
     * Check the supported algorithms.
     * @param pRestricted is the factory restricted
     * @param pType the type of factory
     * @throws OceanusException on error
     */
    private void checkAlgorithms(final boolean pRestricted,
                                 final GordianFactoryType pType) throws OceanusException {
        /* Determine test name */
        final String myTestName = pType.toString() + "-" + (pRestricted
                                                            ? "Restricted"
                                                            : "Unlimited");
        System.out.println(myTestName);

        /* Create new Security Generator */
        final GordianParameters myParams = new GordianParameters(pRestricted);
        myParams.setFactoryType(pType);
        final GordianSecurityManager myManager = theCreator.newSecureManager(myParams);
        final GordianFactory myFactory = myManager.getSecurityFactory();
        final GordianCipherFactory myCiphers = myFactory.getCipherFactory();
        final GordianDigestFactory myDigests = myFactory.getDigestFactory();
        final GordianMacFactory myMacs = myFactory.getMacFactory();

        /* Access predicates */
        final Predicate<GordianDigestSpec> myDigestPredicate = myDigests.supportedDigestSpecs();
        final Predicate<GordianMacSpec> myMacPredicate = myMacs.supportedMacSpecs();
        final Predicate<GordianSymKeySpec> mySymKeyPredicate = myCiphers.supportedSymKeySpecs();
        final Predicate<GordianStreamKeyType> myStreamKeyPredicate = myCiphers.supportedStreamKeyTypes();

        /* Loop through the digests */
        System.out.println(" Digests");
        for (GordianDigestSpec mySpec : GordianDigestSpec.listAll()) {
            /* If the digest is supported */
            if (myDigestPredicate.test(mySpec)) {
                /* Create and profile the digest */
                final GordianDigest myDigest = myDigests.createDigest(mySpec);
                profileDigest(myDigest);
                checkExternalId(myFactory, mySpec, GordianDigestSpec.class);
            }
        }

        /* Loop through the macs */
        System.out.println(" Macs");
        for (GordianMacSpec mySpec : GordianMacSpec.listAll()) {
            /* If the mac is supported */
            if (myMacPredicate.test(mySpec)) {
                /* Create the mac */
                final GordianMac myMac = myMacs.createMac(mySpec);
                final GordianKeyGenerator<GordianMacSpec> myGenerator = myMacs.getKeyGenerator(mySpec);
                final GordianKey<GordianMacSpec> myKey = myGenerator.generateKey();
                profileMac(myMacs, myMac, myKey);
                checkExternalId(myFactory, mySpec, GordianMacSpec.class);
            }
        }

        /* Create instance of each symmetric keySpec */
        System.out.println(" SymKeys");
        for (GordianSymKeySpec mySpec : GordianSymKeySpec.listAll()) {
            if (mySymKeyPredicate.test(mySpec)) {
                final GordianKeyGenerator<GordianSymKeySpec> mySymGenerator = myCiphers.getKeyGenerator(mySpec);
                final GordianKey<GordianSymKeySpec> mySymKey = mySymGenerator.generateKey();
                profileSymKey(myCiphers, mySymKey);
                checkCipherModes(myFactory, mySymKey);
                checkWrapCipher(myCiphers, mySymKey);
                checkExternalId(myFactory, mySpec, GordianSymKeySpec.class);
            }
        }

        /* Create instance of each stream key */
        System.out.println(" StreamKeys");
        for (GordianStreamKeyType myType : GordianStreamKeyType.values()) {
            if (myStreamKeyPredicate.test(myType)) {
                final GordianKeyGenerator<GordianStreamKeyType> myStreamGenerator = myCiphers.getKeyGenerator(myType);
                final GordianKey<GordianStreamKeyType> myStreamKey = myStreamGenerator.generateKey();
                profileStreamKey(myCiphers, myStreamKey);
                checkCipher(myFactory, myStreamKey);
                checkExternalId(myFactory, myType, GordianStreamKeyType.class);
            }
        }
    }

    /**
     * Profile digest.
     * @param pDigest the digest to profile
     */
    private void profileDigest(final GordianDigest pDigest) {
        final byte[] myBytes = "DigestInput".getBytes();
        final long myStart = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            pDigest.update(myBytes);
            pDigest.finish();
        }
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= 100000;
        System.out.println("  " + pDigest.getDigestSpec().toString() + ":" + myElapsed);
    }

    /**
     * Profile digest.
     * @param pFactory the factory
     * @param pMac the Mac to profile
     * @param pKey the key
     * @throws OceanusException on error
     */
    private void profileMac(final GordianMacFactory pFactory,
                            final GordianMac pMac,
                            final GordianKey<GordianMacSpec> pKey) throws OceanusException {
        /* Define the input */
        final byte[] myBytes = "MacInput".getBytes();
        boolean isInconsistent = false;

        /* Access the two macs */
        final GordianMacSpec mySpec = pMac.getMacSpec();
        final GordianMacType myType = mySpec.getMacType();
        final boolean twoMacs = GordianMacType.GMAC.equals(myType);
        final boolean needsReInit = myType.needsReInitialisation();
        final GordianMac myMac1 = pMac;
        final GordianMac myMac2 = twoMacs
                                  ? pFactory.createMac(mySpec)
                                  : pMac;

        /* Start loop */
        final long myStart = System.nanoTime();
        for (int i = 0; i < 500; i++) {
            /* Use first mac */
            myMac1.initMac(pKey);
            myMac1.update(myBytes);
            final byte[] myFirst = myMac1.finish();

            /* If we need to reInitialise */
            if (needsReInit) {
                myMac2.initMac(pKey, myMac1.getInitVector());
            }

            /* Use second mac */
            myMac2.update(myBytes);
            final byte[] mySecond = myMac2.finish();
            if (!Arrays.areEqual(myFirst, mySecond)) {
                isInconsistent = true;
            }
        }

        /* Record elapsed */
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= 100000;
        System.out.println("  " + pMac.getMacSpec().toString() + ":" + myElapsed);
        if (isInconsistent) {
            System.out.println("  " + pMac.getMacSpec().toString() + " inconsistent");
        }
    }

    /**
     * Check cipher modes.
     * @param pFactory the factory
     * @param pKey the key
     * @throws OceanusException on error
     */
    private void checkCipherModes(final GordianFactory pFactory,
                                  final GordianKey<GordianSymKeySpec> pKey) throws OceanusException {
        for (GordianCipherMode myMode : GordianCipherMode.values()) {
            checkCipherPadding(pFactory, pKey, myMode);
        }
    }

    /**
     * Check cipher modes.
     * @param pFactory the factory
     * @param pKey the key
     * @param pMode the mode
     * @throws OceanusException on error
     */
    private void checkCipherPadding(final GordianFactory pFactory,
                                    final GordianKey<GordianSymKeySpec> pKey,
                                    final GordianCipherMode pMode) throws OceanusException {
        if (pMode.hasPadding()) {
            for (GordianPadding myPadding : GordianPadding.values()) {
                checkCipher(pFactory, pKey, pMode, myPadding);
            }
        } else if (pMode.isAAD()) {
            checkAADCipher(pFactory, pKey, pMode);
        } else {
            checkCipher(pFactory, pKey, pMode, GordianPadding.NONE);
        }
    }

    /**
     * Check cipher mode/padding.
     * @param pFactory the factory
     * @param pKey the key
     * @param pMode the mode
     * @param pPadding the padding
     * @throws OceanusException on error
     */
    private void checkCipher(final GordianFactory pFactory,
                             final GordianKey<GordianSymKeySpec> pKey,
                             final GordianCipherMode pMode,
                             final GordianPadding pPadding) throws OceanusException {
        /* Access Data */
        final byte[] myTestData = getTestData();
        final GordianCipherFactory myCiphers = pFactory.getCipherFactory();
        final BiPredicate<GordianSymCipherSpec, Boolean> myCipherPredicate = myCiphers.supportedSymCipherSpecs();

        /* Create the Spec */
        final GordianSymCipherSpec mySpec = new GordianSymCipherSpec(pKey.getKeyType(), pMode, pPadding);
        if (myCipherPredicate.test(mySpec, false)) {
            System.out.println("   " + mySpec.toString());
            final GordianCipher<GordianSymKeySpec> myCipher = myCiphers.createSymKeyCipher(mySpec);
            myCipher.initCipher(pKey);
            if (!pMode.hasPadding()
                    || !GordianPadding.NONE.equals(pPadding)) {
                final byte[] myIV = myCipher.getInitVector();
                final byte[] myEncrypted = myCipher.finish(myTestData);
                myCipher.initCipher(pKey, myIV, false);
                final byte[] myResult = myCipher.finish(myEncrypted);
                if (!Arrays.areEqual(myTestData, myResult)) {
                    System.out.println("Failed to encrypt/decrypt");
                }
            }
            checkExternalId(pFactory, mySpec, GordianSymCipherSpec.class);
        }
    }

    /**
     * Check AAD cipher mode.
     * @param pFactory the factory
     * @param pKey the key
     * @param pMode the mode
     * @throws OceanusException on error
     */
    private void checkAADCipher(final GordianFactory pFactory,
                                final GordianKey<GordianSymKeySpec> pKey,
                                final GordianCipherMode pMode) throws OceanusException {
        /* Access Data */
        final byte[] myTestData = getTestData();
        final byte[] myAADData = getAADData();
        final GordianCipherFactory myCiphers = pFactory.getCipherFactory();
        final BiPredicate<GordianSymCipherSpec, Boolean> myCipherPredicate = myCiphers.supportedSymCipherSpecs();

        /* Create the Spec */
        final GordianSymCipherSpec mySpec = new GordianSymCipherSpec(pKey.getKeyType(), pMode, GordianPadding.NONE);
        if (myCipherPredicate.test(mySpec, true)) {
            System.out.println("   " + mySpec.toString());
            final GordianAADCipher myCipher = myCiphers.createAADCipher(mySpec);
            myCipher.initCipher(pKey);
            final byte[] myIV = myCipher.getInitVector();
            myCipher.updateAAD(myAADData);
            final byte[] myEncrypted = myCipher.finish(myTestData);
            myCipher.initCipher(pKey, myIV, false);
            myCipher.updateAAD(myAADData);
            final byte[] myResult = myCipher.finish(myEncrypted);
            if (!Arrays.areEqual(myTestData, myResult)) {
                System.out.println("Failed to encrypt/decrypt");
            }
            checkExternalId(pFactory, mySpec, GordianSymCipherSpec.class);
        }
    }

    /**
     * Check stream cipher.
     * @param pFactory the factory
     * @param pKey the key
     * @throws OceanusException on error
     */
    private void checkCipher(final GordianFactory pFactory,
                             final GordianKey<GordianStreamKeyType> pKey) throws OceanusException {
        /* Access Data */
        final byte[] myTestData = getTestData();
        final GordianCipherFactory myCiphers = pFactory.getCipherFactory();

        /* Create the Cipher */
        System.out.println("  " + pKey.getKeyType().toString());
        final GordianStreamCipherSpec mySpec = GordianStreamCipherSpec.stream(pKey.getKeyType());
        final GordianCipher<GordianStreamKeyType> myCipher = myCiphers.createStreamKeyCipher(mySpec);
        myCipher.initCipher(pKey);
        final byte[] myIV = myCipher.getInitVector();
        final byte[] myEncrypted = myCipher.finish(myTestData);
        myCipher.initCipher(pKey, myIV, false);
        final byte[] myResult = myCipher.finish(myEncrypted);
        if (!Arrays.areEqual(myTestData, myResult)) {
            System.out.println("Failed to encrypt/decrypt");
        }
        checkExternalId(pFactory, mySpec, GordianStreamCipherSpec.class);
    }

    /**
     * Check wrap cipher.
     * @param pFactory the factory
     * @param pKey the key
     * @throws OceanusException on error
     */
    private void checkWrapCipher(final GordianCipherFactory pFactory,
                                 final GordianKey<GordianSymKeySpec> pKey) throws OceanusException {
        /* Access Data */
        final byte[] myTestData = getTestData();

        /* Create the Cipher */
        System.out.println("   Wrap" + pKey.getKeyType().toString());
        final GordianWrapper myWrapper = pFactory.createKeyWrapper(pKey.getKeyType());
        final byte[] myWrapped = myWrapper.secureBytes(pKey, myTestData);
        final byte[] myResult = myWrapper.deriveBytes(pKey, myWrapped);
        if (!Arrays.areEqual(myTestData, myResult)) {
            System.out.println("Failed to encrypt/decrypt");
        }
    }

    /**
     * Profile symKey.
     * @param pFactory the factory
     * @param pKey the symKey to profile
     * @throws OceanusException on error
     */
    private void profileSymKey(final GordianCipherFactory pFactory,
                               final GordianKey<GordianSymKeySpec> pKey) throws OceanusException {
        final GordianSymKeySpec myKeyType = pKey.getKeyType();
        final int myLen = myKeyType.getBlockLength().getByteLength();
        byte[] myBytes = new byte[myLen];
        final GordianSymCipherSpec mySpec = new GordianSymCipherSpec(myKeyType, GordianCipherMode.ECB, GordianPadding.NONE);
        final GordianCipher<GordianSymKeySpec> myCipher = pFactory.createSymKeyCipher(mySpec);
        final long myStart = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            myCipher.initCipher(pKey);
            myCipher.update(myBytes);
            myBytes = myCipher.finish();
        }
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= 100;
        System.out.println("  " + myKeyType.toString() + ":" + myElapsed);
    }

    /**
     * Profile symKey.
     * @param pFactory the factory
     * @param pKey the symKey to profile
     * @throws OceanusException on error
     */
    private void profileStreamKey(final GordianCipherFactory pFactory,
                                  final GordianKey<GordianStreamKeyType> pKey) throws OceanusException {
        final GordianStreamKeyType myKeyType = pKey.getKeyType();
        final int myLen = 128;
        byte[] myBytes = new byte[myLen];
        final GordianStreamCipherSpec mySpec = GordianStreamCipherSpec.stream(myKeyType);
        final GordianCipher<GordianStreamKeyType> myCipher = pFactory.createStreamKeyCipher(mySpec);
        final long myStart = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            myCipher.initCipher(pKey);
            myCipher.update(myBytes);
            myBytes = myCipher.finish();
        }
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= 100;
        System.out.println("  " + myKeyType.toString() + ":" + myElapsed);
    }

    /**
     * Check externalId.
     * @param <T> the type of the object
     * @param pFactory the Security Factory
     * @param pObject the object
     * @param pClazz the class of the object
     * @throws OceanusException on error
     */
    private <T> void checkExternalId(final GordianFactory pFactory,
                                     final T pObject,
                                     final Class<T> pClazz) throws OceanusException {
        final GordianKeySetFactory myKeySets = pFactory.getKeySetFactory();
        final GordianKnuthObfuscater myKnuth = myKeySets.getObfuscater();
        int myId = myKnuth.deriveExternalIdFromType(pObject);
        T myResult = myKnuth.deriveTypeFromExternalId(myId, pClazz);
        if (!pObject.equals(myResult)) {
            System.out.println("Failed to resolve externalId for " + pClazz.getSimpleName() + ": " + pObject);
        }

        myId = myKnuth.deriveExternalIdFromType(pObject, 205);
        myResult = myKnuth.deriveTypeFromExternalId(myId, 205, pClazz);
        if (!pObject.equals(myResult)) {
            System.out.println("Failed to resolve adjusted externalId for " + pClazz.getSimpleName() + ": " + pObject);
        }
    }

    /**
     * Obtain the testData.
     * @return the testData
     * @throws OceanusException on error
     */
    private byte[] getTestData() throws OceanusException {
        if (theTestData == null) {
            theTestData = TethysDataConverter.stringToByteArray("TestDataStringThatIsNotRidiculouslyShort");
        }
        return theTestData;
    }

    /**
     * Obtain the aadData.
     * @return the aadData
     * @throws OceanusException on error
     */
    private byte[] getAADData() throws OceanusException {
        if (theAADData == null) {
            theAADData = TethysDataConverter.stringToByteArray("SomeAADBytes");
        }
        return theAADData;
    }
}
