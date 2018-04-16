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
package net.sourceforge.joceanus.jgordianknot.test.crypto;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDSAElliptic;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDSAKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDSTU4145Elliptic;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianGOSTElliptic;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation.GordianKEMSender;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianModulus;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSM2Elliptic;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSPHINCSKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianXMSSKeyType;
import net.sourceforge.joceanus.jtethys.OceanusException;

import java.security.spec.X509EncodedKeySpec;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * AsymKeys Test Control.
 */
public class GordianTestAsymmetric {
    /**
     * AsyncKey table.
     */
    private final Map<GordianAsymKeyType, AsymPairControl> theMap;

    /**
     * Signature Source.
     */
    private final byte[][] theSignatureSource;

    /**
     * Constructor.
     * @param pSignatures the signature sources
     */
    protected GordianTestAsymmetric(final byte[]... pSignatures) {
        /* Create the map */
        theMap = new EnumMap<>(GordianAsymKeyType.class);

        /* Create the signature sources */
        theSignatureSource = pSignatures;
    }

    /**
     * Create keyPairs.
     * @param pFactory the factory
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    protected void createKeyPairs(final GordianFactory pFactory,
                                  final GordianKeySet pKeySet) throws OceanusException {
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.rsa(GordianModulus.MOD2048));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.ec(GordianDSAElliptic.SECT571K1));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.dsa(GordianDSAKeyType.MOD2048_2));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.dh(GordianModulus.MOD4096));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.sm2(GordianSM2Elliptic.SM2P256V1));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.sphincs(GordianSPHINCSKeyType.SHA2));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.rainbow());
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.newHope());
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.dstu4145(GordianDSTU4145Elliptic.DSTU9));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.gost2012(GordianGOSTElliptic.CRYPTOPROA));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.xmss(GordianXMSSKeyType.SHA256));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.xmssmt(GordianXMSSKeyType.SHA256));
    }

    /**
     * Test keyPairs.
     * @param pFactory the factory
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    protected void testKeyPairs(final GordianFactory pFactory,
                                final GordianKeySet pKeySet) throws OceanusException {
        for (GordianDSAElliptic myCurve : GordianDSAElliptic.values()) {
            createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.ec(myCurve));
        }
        for (GordianSM2Elliptic myCurve : GordianSM2Elliptic.values()) {
            createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.sm2(myCurve));
        }
        for (GordianDSTU4145Elliptic myCurve : GordianDSTU4145Elliptic.values()) {
            createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.dstu4145(myCurve));
        }
        for (GordianGOSTElliptic myCurve : GordianGOSTElliptic.values()) {
            createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.gost2012(myCurve));
        }
        theMap.clear();
    }

    /**
     * Create keyPair.
     * @param pFactory the factory
     * @param pKeySet the keySet
     * @param pKeySpec the keySpec
     * @throws OceanusException on error
     */
    private void createKeyPair(final GordianFactory pFactory,
                               final GordianKeySet pKeySet,
                               final GordianAsymKeySpec pKeySpec) throws OceanusException {
        /* Create and record the pair */
        System.out.println(" Creating " + pKeySpec.toString());
        final AsymPairControl myPair = new AsymPairControl(pFactory, pKeySpec);
        theMap.put(pKeySpec.getKeyType(), myPair);

        /* Create signatures */
        myPair.createSignatures(theSignatureSource);

        /* Check KeyExchange */
        myPair.checkKEMS();

        /* Secure the privateKey */
        myPair.securePrivateKey(pKeySet);
    }

    /**
     * Create keyPairs.
     * @param pFactory the factory
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    protected void validateKeyPairs(final GordianFactory pFactory,
                                    final GordianKeySet pKeySet) throws OceanusException {
        /* For each control that has been created */
        final Iterator<AsymPairControl> myIterator = theMap.values().iterator();
        while (myIterator.hasNext()) {
            AsymPairControl myControl = myIterator.next();
            System.out.println(" Restoring " + myControl.getKeySpec().toString());
            myControl = new AsymPairControl(pFactory, pKeySet, myControl);
            myControl.validateSignatures(theSignatureSource);
        }
    }

    /**
     * AsymmetricPair Control.
     */
    private static class AsymPairControl {
        /**
         * The Factory.
         */
        private final GordianFactory theFactory;

        /**
         * The KeyPairGenerator.
         */
        private final GordianKeyPairGenerator theGenerator;

        /**
         * The KeyPair.
         */
        private final GordianKeyPair thePair;

        /**
         * The Public KeySpec.
         */
        private final X509EncodedKeySpec thePublic;

        /**
         * The Private KeySpec.
         */
        private byte[] thePrivate;

        /**
         * Signature Map.
         */
        private final Map<GordianSignatureSpec, byte[]> theSignatures;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pKeySpec the Asymmetric KeyType.
         * @throws OceanusException on error
         */
        private AsymPairControl(final GordianFactory pFactory,
                                final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Store the factory */
            theFactory = pFactory;

            /* Create new KeyPair */
            theGenerator = pFactory.getKeyPairGenerator(pKeySpec);
            thePair = theGenerator.generateKeyPair();

            /* Secure the public key */
            thePublic = theGenerator.getX509Encoding(thePair);

            /* Create the signature map */
            theSignatures = new LinkedHashMap<>();
        }

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pKeySet the keySet
         * @param pStatus the Base Status.
         * @throws OceanusException on error
         */
        private AsymPairControl(final GordianFactory pFactory,
                                final GordianKeySet pKeySet,
                                final AsymPairControl pStatus) throws OceanusException {
            /* Store the factory */
            theFactory = pFactory;

            /* Create new KeyPair */
            theGenerator = pFactory.getKeyPairGenerator(pStatus.getKeySpec());
            thePair = theGenerator.deriveKeyPair(pStatus.thePublic, pStatus.thePrivate, pKeySet);

            /* Don't worry about the keySpecs */
            thePrivate = null;
            thePublic = null;

            /* Record the signature map */
            theSignatures = pStatus.theSignatures;

            /* Check that the pairs are identical */
            if (!thePair.equals(pStatus.thePair)) {
                System.out.println("Failed to decrypt KeyPair for: " + getKeySpec());
            }
        }

        /**
         * Obtain the keySpec.
         * @return the keySpec
         */
        private GordianAsymKeySpec getKeySpec() {
            return thePair.getKeySpec();
        }

        /**
         * Secure the privateKey.
         * @param pKeySet the keySet
         * @throws OceanusException on error
         */
        private void securePrivateKey(final GordianKeySet pKeySet) throws OceanusException {
            thePrivate = theGenerator.securePrivateKey(thePair, pKeySet);
        }

        /**
         * Create the signatures.
         * @param pSources the sources to sign
         * @throws OceanusException on error
         */
        private void createSignatures(final byte[][] pSources) throws OceanusException {
            /* Access the signature predicate */
            final BiPredicate<GordianKeyPair, GordianSignatureSpec> mySignPredicate = theFactory.supportedSignatures();

            /* For each possible signature */
            final GordianAsymKeyType myType = getKeySpec().getKeyType();
            for (GordianSignatureType mySignType : myType.getSupportedSignatures()) {
                /* For each possible digestSpec */
                for (GordianDigestSpec mySpec : GordianDigestSpec.listAll()) {
                    /* Create the corresponding signatureSpec */
                    final GordianSignatureSpec mySign = new GordianSignatureSpec(myType, mySignType, mySpec);

                    /* If the signature is supported */
                    if (mySignPredicate.test(thePair, mySign)) {
                        createSignature(mySign, pSources);
                    }
                }
            }
        }

        /**
         * Create the signature.
         * @param pSignatureSpec the signatureSpec
         * @param pSources the sources to sign
         * @throws OceanusException on error
         */
        private void createSignature(final GordianSignatureSpec pSignatureSpec,
                                     final byte[][] pSources) throws OceanusException {
            System.out.println("  Signing " + pSignatureSpec.toString());
            final GordianSigner mySigner = theFactory.createSigner(thePair, pSignatureSpec);
            for (byte[] mySource : pSources) {
                mySigner.update(mySource);
            }
            theSignatures.put(pSignatureSpec, mySigner.sign());
        }

        /**
         * Validate the signatures.
         * @param pSources the sources that were signed
         * @throws OceanusException on error
         */
        private void validateSignatures(final byte[][] pSources) throws OceanusException {
            /* For each signature that has been created */
            final Iterator<GordianSignatureSpec> myIterator = theSignatures.keySet().iterator();
            while (myIterator.hasNext()) {
                final GordianSignatureSpec mySpec = myIterator.next();
                validateSignature(mySpec, pSources);
            }
        }

        /**
         * Validate the signature.
         * @param pSignatureSpec the signatureSpec
         * @param pSources the sources that were signed
         * @throws OceanusException on error
         */
        private void validateSignature(final GordianSignatureSpec pSignatureSpec,
                                       final byte[][] pSources) throws OceanusException {
            System.out.println("  Validating " + pSignatureSpec.toString());
            final GordianValidator myValidator = theFactory.createValidator(thePair, pSignatureSpec);
            for (byte[] mySource : pSources) {
                myValidator.update(mySource);
            }
            if (!myValidator.verify(theSignatures.get(pSignatureSpec))) {
                System.out.println("Failed to validate signature: " + pSignatureSpec);
            }
        }

        /**
         * Check KEMS.
         * @throws OceanusException on error
         */
        private void checkKEMS() throws OceanusException {
            /* Access the Exchange predicate */
            final BiPredicate<GordianKeyPair, GordianDigestSpec> myXchgPredicate = theFactory.supportedKeyExchanges();
            final GordianDigestSpec mySpec = GordianDigestSpec.whirlpool();

            /* If we can perform keyExchange */
            if (myXchgPredicate.test(thePair, mySpec)) {
                /* Perform the key exchange */
                System.out.println("  Checking KEMS " + mySpec.toString());
                final GordianKEMSender mySender = theFactory.createKEMessage(thePair, mySpec);
                final GordianKeyEncapsulation myReceiver = theFactory.parseKEMessage(thePair, mySpec, mySender.getCipherText());

                /* Check agreement */
                final GordianKeySet myKeySet = mySender.deriveKeySet();
                if (!myKeySet.equals(myReceiver.deriveKeySet())) {
                    System.out.println("Failed to agree keys: " + getKeySpec());
                }
            }
        }
    }
}
