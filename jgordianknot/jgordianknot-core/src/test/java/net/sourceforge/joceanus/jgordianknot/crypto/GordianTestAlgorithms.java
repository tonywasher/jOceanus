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
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.Provider;
import java.security.Security;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianTestSuite.SecurityManagerCreator;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Security Test suite - Algorithms.
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
    public GordianTestAlgorithms(final SecurityManagerCreator pCreator) {
        theCreator = pCreator;
    }

    /**
     * Check the supported algorithms.
     * @throws OceanusException on error
     */
    protected void checkAlgorithms() throws OceanusException {
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
        String myTestName = pType.toString() + "-" + (pRestricted
                                                                  ? "Restricted"
                                                                  : "Unlimited");
        System.out.println(myTestName);

        /* Create new Security Generator */
        GordianParameters myParams = new GordianParameters(pRestricted);
        myParams.setFactoryType(pType);
        GordianHashManager myManager = theCreator.newSecureManager(myParams);
        GordianFactory myFactory = myManager.getSecurityFactory();

        /* Access predicates */
        Predicate<GordianDigestSpec> myDigestPredicate = myFactory.supportedDigestSpecs();
        Predicate<GordianMacSpec> myMacPredicate = myFactory.supportedMacSpecs();
        Predicate<GordianSymKeySpec> mySymKeyPredicate = myFactory.supportedSymKeySpecs();
        Predicate<GordianStreamKeyType> myStreamKeyPredicate = myFactory.supportedStreamKeyTypes();

        /* Loop through the digests */
        System.out.println(" Digests");
        for (GordianDigestSpec mySpec : GordianDigestSpec.listAll()) {
            /* If the digest is supported */
            if (myDigestPredicate.test(mySpec)) {
                /* Create and profile the digest */
                GordianDigest myDigest = myFactory.createDigest(mySpec);
                profileDigest(myDigest);
            }
        }

        /* Loop through the macs */
        System.out.println(" Macs");
        for (GordianMacSpec mySpec : GordianMacSpec.listAll()) {
            /* If the mac is supported */
            if (myMacPredicate.test(mySpec)) {
                /* Create the mac */
                GordianMac myMac = myFactory.createMac(mySpec);
                GordianKeyGenerator<GordianMacSpec> myGenerator = myFactory.getKeyGenerator(mySpec);
                GordianKey<GordianMacSpec> myKey = myGenerator.generateKey();
                profileMac(myFactory, myMac, myKey);
            }
        }

        /* Create instance of each symmetric keySpec */
        System.out.println(" SymKeys");
        for (GordianSymKeySpec mySpec : GordianSymKeySpec.listAll()) {
            if (mySymKeyPredicate.test(mySpec)) {
                GordianKeyGenerator<GordianSymKeySpec> mySymGenerator = myFactory.getKeyGenerator(mySpec);
                GordianKey<GordianSymKeySpec> mySymKey = mySymGenerator.generateKey();
                profileSymKey(myFactory, mySymKey);
                checkCipherModes(myFactory, mySymKey);
                checkWrapCipher(myFactory, mySymKey);
            }
        }

        /* Create instance of each stream key */
        System.out.println(" StreamKeys");
        for (GordianStreamKeyType myType : GordianStreamKeyType.values()) {
            if (myStreamKeyPredicate.test(myType)) {
                GordianKeyGenerator<GordianStreamKeyType> myStreamGenerator = myFactory.getKeyGenerator(myType);
                GordianKey<GordianStreamKeyType> myStreamKey = myStreamGenerator.generateKey();
                checkCipher(myFactory, myStreamKey);
            }
        }
    }

    /**
     * Profile digest.
     * @param pDigest the digest to profile
     */
    private void profileDigest(final GordianDigest pDigest) {
        byte[] myBytes = "MacInput".getBytes();
        long myStart = System.nanoTime();
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
    private void profileMac(final GordianFactory pFactory,
                            final GordianMac pMac,
                            final GordianKey<GordianMacSpec> pKey) throws OceanusException {
        /* Define the input */
        byte[] myBytes = "MacInput".getBytes();
        boolean isInconsistent = false;

        /* Access the two macs */
        GordianMacSpec mySpec = pMac.getMacSpec();
        GordianMacType myType = mySpec.getMacType();
        boolean twoMacs = GordianMacType.GMAC.equals(myType);
        boolean needsReInit = myType.needsReInitialisation() || myType.needsReset();
        GordianMac myMac1 = pMac;
        GordianMac myMac2 = twoMacs
                                    ? pFactory.createMac(mySpec)
                                    : pMac;

        /* Start loop */
        long myStart = System.nanoTime();
        for (int i = 0; i < 500; i++) {
            /* Use first mac */
            myMac1.initMac(pKey);
            myMac1.update(myBytes);
            byte[] myFirst = myMac1.finish();

            /* If we need to reInitialise */
            if (needsReInit) {
                myMac2.initMac(pKey, myMac1.getInitVector());
            }

            /* Use second mac */
            myMac2.update(myBytes);
            byte[] mySecond = myMac2.finish();
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
        byte[] myTestData = getTestData();
        BiPredicate<GordianSymCipherSpec, Boolean> myCipherPredicate = pFactory.supportedSymCipherSpecs();

        /* Create the Spec */
        GordianSymCipherSpec mySpec = new GordianSymCipherSpec(pKey.getKeyType(), pMode, pPadding);
        if (myCipherPredicate.test(mySpec, false)) {
            System.out.println("   " + mySpec.toString());
            GordianCipher<GordianSymKeySpec> myCipher = pFactory.createSymKeyCipher(mySpec);
            myCipher.initCipher(pKey);
            if (!pMode.hasPadding()
                || !GordianPadding.NONE.equals(pPadding)) {
                byte[] myIV = myCipher.getInitVector();
                byte[] myEncrypted = myCipher.finish(myTestData);
                myCipher.initCipher(pKey, myIV, false);
                byte[] myResult = myCipher.finish(myEncrypted);
                if (!Arrays.areEqual(myTestData, myResult)) {
                    System.out.println("Failed to encrypt/decrypt");
                }
            }
        }
    }

    /**
     * Check AAD cipher mode.
     * @param pFactory the factory
     * @param pKey the key
     * @param pMode the mode
     * @param pPadding the padding
     * @throws OceanusException on error
     */
    private void checkAADCipher(final GordianFactory pFactory,
                                final GordianKey<GordianSymKeySpec> pKey,
                                final GordianCipherMode pMode) throws OceanusException {
        /* Access Data */
        byte[] myTestData = getTestData();
        byte[] myAADData = getAADData();
        BiPredicate<GordianSymCipherSpec, Boolean> myCipherPredicate = pFactory.supportedSymCipherSpecs();

        /* Create the Spec */
        GordianSymCipherSpec mySpec = new GordianSymCipherSpec(pKey.getKeyType(), pMode, GordianPadding.NONE);
        if (myCipherPredicate.test(mySpec, true)) {
            System.out.println("   " + mySpec.toString());
            GordianAADCipher myCipher = pFactory.createAADCipher(mySpec);
            myCipher.initCipher(pKey);
            byte[] myIV = myCipher.getInitVector();
            myCipher.updateAAD(myAADData);
            byte[] myEncrypted = myCipher.finish(myTestData);
            myCipher.initCipher(pKey, myIV, false);
            myCipher.updateAAD(myAADData);
            byte[] myResult = myCipher.finish(myEncrypted);
            if (!Arrays.areEqual(myTestData, myResult)) {
                System.out.println("Failed to encrypt/decrypt");
            }
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
        byte[] myTestData = getTestData();

        /* Create the Cipher */
        System.out.println("  " + pKey.getKeyType().toString());
        GordianCipher<GordianStreamKeyType> myCipher = pFactory.createStreamKeyCipher(GordianStreamCipherSpec.stream(pKey.getKeyType()));
        myCipher.initCipher(pKey);
        byte[] myIV = myCipher.getInitVector();
        byte[] myEncrypted = myCipher.finish(myTestData);
        myCipher.initCipher(pKey, myIV, false);
        byte[] myResult = myCipher.finish(myEncrypted);
        if (!Arrays.areEqual(myTestData, myResult)) {
            System.out.println("Failed to encrypt/decrypt");
        }
    }

    /**
     * Check wrap cipher.
     * @param pFactory the factory
     * @param pKey the key
     * @throws OceanusException on error
     */
    private void checkWrapCipher(final GordianFactory pFactory,
                                 final GordianKey<GordianSymKeySpec> pKey) throws OceanusException {
        /* Access Data */
        byte[] myTestData = getTestData();

        /* Create the Cipher */
        System.out.println("   Wrap" + pKey.getKeyType().toString());
        GordianWrapCipher myCipher = pFactory.createWrapCipher(pKey.getKeyType());
        byte[] myWrapped = myCipher.secureBytes(pKey, myTestData);
        byte[] myResult = myCipher.deriveBytes(pKey, myWrapped);
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
    private void profileSymKey(final GordianFactory pFactory,
                               final GordianKey<GordianSymKeySpec> pKey) throws OceanusException {
        final GordianSymKeySpec myKeyType = pKey.getKeyType();
        final int myLen = myKeyType.getBlockLength().getByteLength();
        byte[] myBytes = new byte[myLen];
        GordianSymCipherSpec mySpec = new GordianSymCipherSpec(myKeyType, GordianCipherMode.ECB, GordianPadding.NONE);
        GordianCipher<GordianSymKeySpec> myCipher = pFactory.createSymKeyCipher(mySpec);
        long myStart = System.nanoTime();
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

    /**
     * List the supported algorithms.
     */
    protected static void listAlgorithms() {
        Set<String> ciphers = new HashSet<String>();
        Set<String> secretKeyFactories = new HashSet<String>();
        Set<String> keyFactories = new HashSet<String>();
        Set<String> keyAgreements = new HashSet<String>();
        Set<String> keyGenerators = new HashSet<String>();
        Set<String> keyPairGenerators = new HashSet<String>();
        Set<String> messageDigests = new HashSet<String>();
        Set<String> macs = new HashSet<String>();
        Set<String> signatures = new HashSet<String>();
        Set<String> randoms = new HashSet<String>();
        Set<String> remaining = new HashSet<String>();

        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new BouncyCastlePQCProvider());

        for (Provider myProvider : Security.getProviders()) {
            if (!"BC".equals(myProvider.getName())
                && !"BCPQC".equals(myProvider.getName())) {
                continue;
            }
            Iterator<Object> it = myProvider.keySet().iterator();
            while (it.hasNext()) {
                String entry = (String) it.next();
                if (entry.startsWith("Alg.Alias.")) {
                    entry = entry.substring("Alg.Alias.".length());
                }
                if (entry.contains(".OID.")
                    || entry.contains(".1.")) {
                    continue;
                }
                if (entry.startsWith("Cipher.")) {
                    ciphers.add(entry.substring("Cipher.".length()));
                } else if (entry.startsWith("SecretKeyFactory.")) {
                    secretKeyFactories.add(entry.substring("SecretKeyFactory.".length()));
                } else if (entry.startsWith("KeyFactory.")) {
                    keyFactories.add(entry.substring("KeyFactory.".length()));
                } else if (entry.startsWith("KeyAgreement.")) {
                    keyAgreements.add(entry.substring("KeyAgreement.".length()));
                } else if (entry.startsWith("KeyGenerator.")) {
                    keyGenerators.add(entry.substring("KeyGenerator.".length()));
                } else if (entry.startsWith("KeyPairGenerator.")) {
                    keyPairGenerators.add(entry.substring("KeyPairGenerator.".length()));
                } else if (entry.startsWith("MessageDigest.")) {
                    messageDigests.add(entry.substring("MessageDigest.".length()));
                } else if (entry.startsWith("Mac.")) {
                    macs.add(entry.substring("Mac.".length()));
                } else if (entry.startsWith("Signature.")) {
                    signatures.add(entry.substring("Signature.".length()));
                } else if (entry.startsWith("SecureRandom.")) {
                    randoms.add(entry.substring("SecureRandom.".length()));
                } else {
                    remaining.add(entry);
                }
            }
        }

        printSet("Ciphers", ciphers);
        printSet("SecretKeyFactories", secretKeyFactories);
        printSet("KeyFactories", keyFactories);
        printSet("KeyAgreements", keyAgreements);
        printSet("KeyGenerators", keyGenerators);
        printSet("KeyPairGenerators", keyPairGenerators);
        printSet("MessageDigests", messageDigests);
        printSet("Macs", macs);
        printSet("Signatures", signatures);
        printSet("Randoms", randoms);
        printSet("Remaining", remaining);
    }

    /**
     * Print out a set of algorithms.
     * @param setName the name of the set
     * @param algorithms the set of algorithms
     */
    private static void printSet(final String setName,
                                 final Set<String> algorithms) {
        System.out.println(setName
                           + ":");
        if (algorithms.isEmpty()) {
            System.out.println("            None available.");
        } else {
            Iterator<String> it = algorithms.iterator();
            while (it.hasNext()) {
                String name = it.next();
                System.out.println("            "
                                   + name);
            }
        }
    }
}
