/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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

import java.security.spec.X509EncodedKeySpec;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiPredicate;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation.GordianKEMSender;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.ec(GordianElliptic.SECT571K1));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.dh(GordianModulus.MOD4096));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.sphincs());
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.rainbow());
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.newHope());
    }

    /**
     * Create keyPair.
     * @param pFactory the factory
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    private void createKeyPair(final GordianFactory pFactory,
                               final GordianKeySet pKeySet,
                               final GordianAsymKeySpec pKeySpec) throws OceanusException {
        /* Create and record the pair */
        AsymPairControl myPair = new AsymPairControl(pFactory, pKeySet, pKeySpec);
        theMap.put(pKeySpec.getKeyType(), myPair);

        /* Create signatures */
        myPair.createSignatures(theSignatureSource);

        /* Check KeyExchange */
        myPair.checkKEMS();
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
        Iterator<AsymPairControl> myIterator = theMap.values().iterator();
        while (myIterator.hasNext()) {
            AsymPairControl myControl = myIterator.next();
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
        private final byte[] thePrivate;

        /**
         * Signature Map.
         */
        private final Map<GordianSignatureSpec, byte[]> theSignatures;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pKeySet the keySet
         * @param pKeySpec the Asymmetric KeyType.
         * @throws OceanusException on error
         */
        private AsymPairControl(final GordianFactory pFactory,
                                final GordianKeySet pKeySet,
                                final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Store the factory */
            theFactory = pFactory;

            /* Create new KeyPair */
            GordianKeyPairGenerator myGenerator = pFactory.getKeyPairGenerator(pKeySpec);
            thePair = myGenerator.generateKeyPair();

            /* Secure the keys */
            thePrivate = myGenerator.securePrivateKey(thePair, pKeySet);
            thePublic = myGenerator.getX509Encoding(thePair);

            /* Create the signature map */
            theSignatures = new HashMap<>();
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
            GordianKeyPairGenerator myGenerator = pFactory.getKeyPairGenerator(pStatus.getKeySpec());
            thePair = myGenerator.deriveKeyPair(pStatus.thePublic, pStatus.thePrivate, pKeySet);

            /* Don't worry about the keySpes */
            thePrivate = null;
            thePublic = null;

            /* Record the signature map */
            theSignatures = pStatus.theSignatures;

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
         * Create the signatures.
         * @param pSources the sources to sign
         * @throws OceanusException on error
         */
        private void createSignatures(final byte[][] pSources) throws OceanusException {
            /* Access the signature predicate */
            BiPredicate<GordianKeyPair, GordianSignatureSpec> mySignPredicate = theFactory.supportedSignatures();

            /* For each possible signature */
            GordianAsymKeyType myType = getKeySpec().getKeyType();
            for (GordianSignatureType mySignType : myType.getSupportedSignatures()) {
                /* For each possible digestSpec */
                for (GordianDigestSpec mySpec : GordianDigestSpec.listAll()) {
                    /* Create the corresponding signatureSpec */
                    GordianSignatureSpec mySign = new GordianSignatureSpec(myType, mySignType, mySpec);

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
            GordianSigner mySigner = theFactory.createSigner(thePair, pSignatureSpec);
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
            Iterator<GordianSignatureSpec> myIterator = theSignatures.keySet().iterator();
            while (myIterator.hasNext()) {
                GordianSignatureSpec mySpec = myIterator.next();
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
            GordianValidator myValidator = theFactory.createValidator(thePair, pSignatureSpec);
            for (byte[] mySource : pSources) {
                myValidator.update(mySource);
            }
            if (!myValidator.verify(theSignatures.get(pSignatureSpec))) {
                System.out.println("Failed to validate signature: " + pSignatureSpec);
            }
        }

        /**
         * Check KEMS.
         * @param pDigestType the digestType
         * @param pSymSafe the encrypted SymKey
         * @param pStreamSafe the encrypted StreamKey
         * @throws OceanusException on error
         */
        private void checkKEMS() throws OceanusException {
            /* Access the Exchange predicate */
            BiPredicate<GordianKeyPair, GordianDigestSpec> myXchgPredicate = theFactory.supportedKeyExchanges();
            GordianDigestSpec mySpec = theFactory.getDefaultDigest();

            /* If we can perform keyExchange */
            if (myXchgPredicate.test(thePair, mySpec)) {
                /* Perform the key exchange */
                GordianKEMSender mySender = theFactory.createKEMessage(thePair, mySpec);
                GordianKeyEncapsulation myReceiver = theFactory.parseKEMessage(thePair, mySpec, mySender.getCipherText());

                /* Check agreement */
                GordianKeySet myKeySet = mySender.deriveKeySet();
                if (!myKeySet.equals(myReceiver.deriveKeySet())) {
                    System.out.println("Failed to agree keys: " + getKeySpec());
                }
            }
        }
    }
}