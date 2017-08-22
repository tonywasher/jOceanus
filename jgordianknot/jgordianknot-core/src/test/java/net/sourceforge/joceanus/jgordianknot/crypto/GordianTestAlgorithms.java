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
import java.util.function.Predicate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianTestSuite.SecurityManagerCreator;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Security Test suite - Algorithms.
 */
public class GordianTestAlgorithms {
    /**
     * The Security Manager creator.
     */
    private final SecurityManagerCreator theCreator;

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
                /* Create the digest */
                System.out.println("  " + mySpec.toString());
                GordianDigest myDigest = myFactory.createDigest(mySpec);
                myDigest.update("MacInput".getBytes());
                myDigest.finish();
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
                System.out.println("  " + mySpec.toString());
                myMac.initMac(myKey);
                myMac.update("MacInput".getBytes());
                myMac.finish();
            }
        }

        /* Create instance of each symmetric keySpec */
        System.out.println(" SymKeys");
        for (GordianSymKeySpec mySpec : GordianSymKeySpec.listAll()) {
            if (mySymKeyPredicate.test(mySpec)) {
                GordianKeyGenerator<GordianSymKeySpec> mySymGenerator = myFactory.getKeyGenerator(mySpec);
                GordianKey<GordianSymKeySpec> mySymKey = mySymGenerator.generateKey();
                System.out.println("  " + mySpec.toString());
                checkCipherModes(myFactory, mySymKey);
            }
        }

        /* Create instance of each stream key */
        System.out.println(" StreamKeys");
        for (GordianStreamKeyType myType : GordianStreamKeyType.values()) {
            if (myStreamKeyPredicate.test(myType)) {
                GordianKeyGenerator<GordianStreamKeyType> myStreamGenerator = myFactory.getKeyGenerator(myType);
                GordianKey<GordianStreamKeyType> myStreamKey = myStreamGenerator.generateKey();
                GordianCipher<GordianStreamKeyType> myCipher = myFactory.createStreamKeyCipher(GordianStreamCipherSpec.stream(myType));
                System.out.println("  " + myType.toString());
                myCipher.initCipher(myStreamKey);
            }
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
        } else if (!pMode.isAAD()) {
            checkCipher(pFactory, pKey, pMode, GordianPadding.NONE);
        } else {
            checkAADCipher(pFactory, pKey, pMode);
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
        GordianSymCipherSpec mySpec = new GordianSymCipherSpec(pKey.getKeyType(), pMode, pPadding);
        if (mySpec.validate(false)) {
            System.out.println("   " + mySpec.toString());
            GordianCipher<GordianSymKeySpec> myCipher = pFactory.createSymKeyCipher(mySpec);
            myCipher.initCipher(pKey);
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
        GordianSymCipherSpec mySpec = new GordianSymCipherSpec(pKey.getKeyType(), pMode, GordianPadding.NONE);
        if (mySpec.validate(true)) {
            System.out.println("   " + mySpec.toString());
            GordianAADCipher myCipher = pFactory.createAADCipher(mySpec);
            myCipher.initCipher(pKey);
        }
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
