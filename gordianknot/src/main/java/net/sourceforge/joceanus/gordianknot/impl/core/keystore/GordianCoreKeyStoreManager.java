/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.keystore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bouncycastle.asn1.x500.X500Name;

import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyPairUse;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreManager;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStorePair;
import net.sourceforge.joceanus.oceanus.OceanusException;

/**
 * KeyStore Manager implementation.
 */
public class GordianCoreKeyStoreManager
        implements GordianKeyStoreManager {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The keyStore.
     */
    private final GordianCoreKeyStore theKeyStore;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pKeyStore the keyStore
     */
    GordianCoreKeyStoreManager(final GordianCoreFactory pFactory,
                               final GordianCoreKeyStore pKeyStore) {
        theFactory = pFactory;
        theKeyStore = pKeyStore;
    }

    @Override
    public GordianCoreKeyStore getKeyStore() {
        return theKeyStore;
    }

    @Override
    public GordianKeyStoreSet createKeySet(final GordianKeySetSpec pKeySetSpec,
                                           final String pAlias,
                                           final char[] pPassword) throws OceanusException {
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        final GordianKeySet myKeySet = myFactory.generateKeySet(pKeySetSpec);
        theKeyStore.setKeySet(pAlias, myKeySet, pPassword);
        return (GordianKeyStoreSet) theKeyStore.getEntry(pAlias, pPassword);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K extends GordianKeySpec> GordianKeyStoreKey<K> createKey(final K pKeySpec,
                                                                      final String pAlias,
                                                                      final char[] pPassword) throws OceanusException {
        /* Access the relevant keyGenerator */
        final GordianKeyGenerator<K> myGenerator = pKeySpec instanceof GordianMacSpec
                ? theFactory.getMacFactory().getKeyGenerator(pKeySpec)
                : theFactory.getCipherFactory().getKeyGenerator(pKeySpec);

        /* Generate, store and return key */
        final GordianKey<K> myKey = myGenerator.generateKey();
        theKeyStore.setKey(pAlias, myKey, pPassword);
        return (GordianKeyStoreKey<K>) theKeyStore.getEntry(pAlias, pPassword);
    }

    @Override
    public GordianCoreKeyStorePair createRootKeyPair(final GordianKeyPairSpec pKeySpec,
                                                     final X500Name pSubject,
                                                     final String pAlias,
                                                     final char[] pPassword) throws OceanusException {
        /* Check that the keySpec can provide a signature */
        if (theFactory.getKeyPairFactory().getSignatureFactory().defaultForKeyPair(pKeySpec) == null) {
            throw new GordianDataException("Root keyPair must be capable of signing");
        }

        /* Create the new keyPair */
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pKeySpec);
        final GordianCoreKeyPair myKeyPair = (GordianCoreKeyPair) myGenerator.generateKeyPair();

        /* Create the certificate */
        final GordianCoreCertificate myCert = new GordianCoreCertificate(theFactory, myKeyPair, pSubject);
        final List<GordianCertificate> myChain = Collections.singletonList(myCert);

        /* Record into keyStore */
        theKeyStore.setKeyPair(pAlias, myKeyPair, pPassword, myChain);
        return (GordianCoreKeyStorePair) theKeyStore.getEntry(pAlias, pPassword);
    }


    @Override
    public GordianCoreKeyStorePair createKeyPair(final GordianKeyPairSpec pKeySpec,
                                                 final X500Name pSubject,
                                                 final GordianKeyPairUsage pUsage,
                                                 final GordianKeyStorePair pSigner,
                                                 final String pAlias,
                                                 final char[] pPassword) throws OceanusException {
        /* Create the new keyPair */
        checkKeyPairUsage(pKeySpec, pUsage);
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pKeySpec);
        final GordianKeyPair myKeyPair = myGenerator.generateKeyPair();

        /* Create the certificate */
        final GordianCoreCertificate myCert = new GordianCoreCertificate(theFactory, pSigner, myKeyPair, pSubject, pUsage);

        /* Create the new chain */
        final List<GordianCertificate> myParentChain = pSigner.getCertificateChain();
        final List<GordianCertificate> myChain = new ArrayList<>(myParentChain);
        myChain.add(0, myCert);

        /* Record into keyStore */
        theKeyStore.setKeyPair(pAlias, myKeyPair, pPassword, myChain);
        return (GordianCoreKeyStorePair) theKeyStore.getEntry(pAlias, pPassword);
    }

    @Override
    public GordianCoreKeyStorePair createAlternate(final GordianKeyStorePair pKeyPair,
                                                   final GordianKeyPairUsage pUsage,
                                                   final GordianKeyStorePair pSigner,
                                                   final String pAlias,
                                                   final char[] pPassword) throws OceanusException {
        /* Access the keyPair and subject */
        final GordianCoreKeyPair myKeyPair = (GordianCoreKeyPair) pKeyPair.getKeyPair();
        final X500Name mySubject = pKeyPair.getCertificateChain().get(0).getSubject().getName();
        checkKeyPairUsage(myKeyPair.getKeyPairSpec(), pUsage);

        /* Create the certificate */
        final GordianCoreCertificate myCert = new GordianCoreCertificate(theFactory, pSigner, myKeyPair, mySubject, pUsage);

        /* Create the new chain */
        final List<GordianCertificate> myParentChain = pSigner.getCertificateChain();
        final List<GordianCertificate> myChain = new ArrayList<>(myParentChain);
        myChain.add(0, myCert);

        /* Record into keyStore */
        theKeyStore.setKeyPair(pAlias, myKeyPair, pPassword, myChain);
        return (GordianCoreKeyStorePair) theKeyStore.getEntry(pAlias, pPassword);
    }

    /**
     * Sign keyPair.
     * @param pKeyPair the keyPair
     * @param pSubject the name of the entity
     * @param pUsage   the key usage
     * @param pSigner the signer
     * @return the certificate chain
     * @throws OceanusException on error
     */
    List<GordianCertificate> signKeyPair(final GordianKeyPair pKeyPair,
                                         final X500Name pSubject,
                                         final GordianKeyPairUsage pUsage,
                                         final GordianKeyStorePair pSigner) throws OceanusException {
        /* Create the certificate */
        final GordianCoreCertificate myCert = new GordianCoreCertificate(theFactory, pSigner, pKeyPair, pSubject, pUsage);

        /* Create the new chain */
        final List<GordianCertificate> myParentChain = pSigner.getCertificateChain();
        final List<GordianCertificate> myChain = new ArrayList<>(myParentChain);
        myChain.add(0, myCert);
        return myChain;
    }

    /**
     * Check Usage for keyPairSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pUsage   the key usage
     * @throws OceanusException on error
     */
    private void checkKeyPairUsage(final GordianKeyPairSpec pKeyPairSpec,
                                   final GordianKeyPairUsage pUsage) throws OceanusException {
        /* Determine the requirements */
        final boolean needsSign = pUsage.hasUse(GordianKeyPairUse.CERTIFICATE)
                || pUsage.hasUse(GordianKeyPairUse.SIGNATURE);
        final boolean needsEnc = pUsage.hasUse(GordianKeyPairUse.KEYENCRYPT)
                || pUsage.hasUse(GordianKeyPairUse.DATAENCRYPT);
        final boolean needsAgree = pUsage.hasUse(GordianKeyPairUse.AGREEMENT);

        /* Validate keyPairSpec against requirements */
        final GordianKeyPairFactory myKPFactory = theFactory.getKeyPairFactory();
        final boolean bFail = (needsSign && myKPFactory.getSignatureFactory().defaultForKeyPair(pKeyPairSpec) == null)
                || (needsEnc && myKPFactory.getEncryptorFactory().defaultForKeyPair(pKeyPairSpec) == null)
                || (needsAgree && myKPFactory.getAgreementFactory().defaultForKeyPair(pKeyPairSpec) == null);

        /* Handle failure */
        if (bFail) {
            throw new GordianLogicException("Unsupported Usage for keyPair");
        }
    }
}
