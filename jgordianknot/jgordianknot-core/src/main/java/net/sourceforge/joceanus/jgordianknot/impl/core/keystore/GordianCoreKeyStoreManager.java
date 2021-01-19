/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.keystore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bouncycastle.asn1.x500.X500Name;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUse;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairSet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreManager;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypairset.GordianCoreKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStorePairSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
        if (GordianSignatureSpec.defaultForKey(pKeySpec) == null) {
            throw new GordianDataException("Root keyPair must be capable of signing");
        }

        /* Create the new keyPair */
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pKeySpec);
        final GordianCoreKeyPair myKeyPair = (GordianCoreKeyPair) myGenerator.generateKeyPair();

        /* Create the certificate */
        final GordianCoreKeyPairCertificate myCert = new GordianCoreKeyPairCertificate(theFactory, myKeyPair, pSubject);
        final List<GordianKeyPairCertificate> myChain = Collections.singletonList(myCert);

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
        final GordianCoreKeyPair myKeyPair = (GordianCoreKeyPair) myGenerator.generateKeyPair();

        /* Create the certificate */
        final GordianCoreKeyPairCertificate myCert = new GordianCoreKeyPairCertificate(theFactory, (GordianCoreKeyStorePair) pSigner, myKeyPair, pSubject, pUsage);

        /* Create the new chain */
        final List<GordianKeyPairCertificate> myParentChain = pSigner.getCertificateChain();
        final List<GordianKeyPairCertificate> myChain = new ArrayList<>(myParentChain);
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
        final GordianCoreKeyPairCertificate myCert = new GordianCoreKeyPairCertificate(theFactory, (GordianCoreKeyStorePair) pSigner, myKeyPair, mySubject, pUsage);

        /* Create the new chain */
        final List<GordianKeyPairCertificate> myParentChain = pSigner.getCertificateChain();
        final List<GordianKeyPairCertificate> myChain = new ArrayList<>(myParentChain);
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
    List<GordianKeyPairCertificate> signKeyPair(final GordianKeyPair pKeyPair,
                                                final X500Name pSubject,
                                                final GordianKeyPairUsage pUsage,
                                                final GordianKeyStorePair pSigner) throws OceanusException {
        /* Create the certificate */
        final GordianCoreKeyPairCertificate myCert = new GordianCoreKeyPairCertificate(theFactory, (GordianCoreKeyStorePair) pSigner, pKeyPair, pSubject, pUsage);

        /* Create the new chain */
        final List<GordianKeyPairCertificate> myParentChain = pSigner.getCertificateChain();
        final List<GordianKeyPairCertificate> myChain = new ArrayList<>(myParentChain);
        myChain.add(0, myCert);
        return myChain;
    }

    /**
     * Check Usage for keyPairSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pUsage   the key usage
     * @throws OceanusException on error
     */
    private static void checkKeyPairUsage(final GordianKeyPairSpec pKeyPairSpec,
                                          final GordianKeyPairUsage pUsage) throws OceanusException {
        /* Determine the requirements */
        final boolean needsSign = pUsage.hasUse(GordianKeyPairUse.CERTIFICATE)
                || pUsage.hasUse(GordianKeyPairUse.SIGNATURE);
        final boolean needsEnc = pUsage.hasUse(GordianKeyPairUse.KEYENCRYPT)
                || pUsage.hasUse(GordianKeyPairUse.DATAENCRYPT);
        final boolean needsAgree = pUsage.hasUse(GordianKeyPairUse.AGREEMENT);

        /* Validate keyPairSpec against requirements */
        final boolean bFail = (needsSign && GordianSignatureSpec.defaultForKey(pKeyPairSpec) == null)
                || (needsEnc && GordianEncryptorSpec.defaultForKey(pKeyPairSpec) == null)
                || (needsAgree && GordianKeyPairAgreementSpec.defaultForKey(pKeyPairSpec) == null);

        /* Handle failure */
        if (bFail) {
            throw new GordianLogicException("Unsupported Usage for keyPair");
        }
    }

    @Override
    public GordianCoreKeyStorePairSet createRootKeyPairSet(final GordianKeyPairSetSpec pKeySetSpec,
                                                           final X500Name pSubject,
                                                           final String pAlias,
                                                           final char[] pPassword) throws OceanusException {
        /* Check that the keySetSpec can provide a signature */
        if (!pKeySetSpec.canSign()) {
            throw new GordianDataException("Root keyPairSet must be capable of signing");
        }

        /* Create the new keyPair */
        final GordianKeyPairFactory myPairFactory = theFactory.getKeyPairFactory();
        final GordianKeyPairSetFactory myFactory = myPairFactory.getKeyPairSetFactory();
        final GordianKeyPairSetGenerator myGenerator = myFactory.getKeyPairSetGenerator(pKeySetSpec);
        final GordianCoreKeyPairSet myKeyPairSet = (GordianCoreKeyPairSet) myGenerator.generateKeyPairSet();

        /* Create the certificate */
        final GordianCoreKeyPairSetCertificate myCert = new GordianCoreKeyPairSetCertificate(theFactory, myKeyPairSet, pSubject);
        final List<GordianKeyPairSetCertificate> myChain = Collections.singletonList(myCert);

        /* Record into keyStore */
        theKeyStore.setKeyPairSet(pAlias, myKeyPairSet, pPassword, myChain);
        return (GordianCoreKeyStorePairSet) theKeyStore.getEntry(pAlias, pPassword);
    }


    @Override
    public GordianCoreKeyStorePairSet createKeyPairSet(final GordianKeyPairSetSpec pKeySetSpec,
                                                       final X500Name pSubject,
                                                       final GordianKeyPairUsage pUsage,
                                                       final GordianKeyStorePairSet pSigner,
                                                       final String pAlias,
                                                       final char[] pPassword) throws OceanusException {
        /* Create the new keyPairSet */
        checkKeyPairSetUsage(pKeySetSpec, pUsage);
        final GordianKeyPairFactory myPairFactory = theFactory.getKeyPairFactory();
        final GordianKeyPairSetFactory myFactory = myPairFactory.getKeyPairSetFactory();
        final GordianKeyPairSetGenerator myGenerator = myFactory.getKeyPairSetGenerator(pKeySetSpec);
        final GordianCoreKeyPairSet myKeyPairSet = (GordianCoreKeyPairSet) myGenerator.generateKeyPairSet();

        /* Create the certificate */
        final GordianCoreKeyPairSetCertificate myCert = new GordianCoreKeyPairSetCertificate(theFactory, (GordianCoreKeyStorePairSet) pSigner, myKeyPairSet, pSubject, pUsage);

        /* Create the new chain */
        final List<GordianKeyPairSetCertificate> myParentChain = pSigner.getCertificateChain();
        final List<GordianKeyPairSetCertificate> myChain = new ArrayList<>(myParentChain);
        myChain.add(0, myCert);

        /* Record into keyStore */
        theKeyStore.setKeyPairSet(pAlias, myKeyPairSet, pPassword, myChain);
        return (GordianCoreKeyStorePairSet) theKeyStore.getEntry(pAlias, pPassword);
    }

    @Override
    public GordianCoreKeyStorePairSet createAlternate(final GordianKeyStorePairSet pKeyPairSet,
                                                      final GordianKeyPairUsage pUsage,
                                                      final GordianKeyStorePairSet pSigner,
                                                      final String pAlias,
                                                      final char[] pPassword) throws OceanusException {
        /* Access the keyPair and subject */
        final GordianCoreKeyPairSet myKeyPairSet = (GordianCoreKeyPairSet) pKeyPairSet.getKeyPairSet();
        final X500Name mySubject = pKeyPairSet.getCertificateChain().get(0).getSubject().getName();
        checkKeyPairSetUsage(myKeyPairSet.getKeyPairSetSpec(), pUsage);

        /* Create the certificate */
        final GordianCoreKeyPairSetCertificate myCert = new GordianCoreKeyPairSetCertificate(theFactory, (GordianCoreKeyStorePairSet) pSigner, myKeyPairSet, mySubject, pUsage);

        /* Create the new chain */
        final List<GordianKeyPairSetCertificate> myParentChain = pSigner.getCertificateChain();
        final List<GordianKeyPairSetCertificate> myChain = new ArrayList<>(myParentChain);
        myChain.add(0, myCert);

        /* Record into keyStore */
        theKeyStore.setKeyPairSet(pAlias, myKeyPairSet, pPassword, myChain);
        return (GordianCoreKeyStorePairSet) theKeyStore.getEntry(pAlias, pPassword);
    }

    /**
     * Sign keyPairSet.
     * @param pKeyPairSet the keyPairSet
     * @param pSubject the name of the entity
     * @param pUsage   the key usage
     * @param pSigner the signer
     * @return the certificate chain
     * @throws OceanusException on error
     */
    List<GordianKeyPairSetCertificate> signKeyPairSet(final GordianKeyPairSet pKeyPairSet,
                                                      final X500Name pSubject,
                                                      final GordianKeyPairUsage pUsage,
                                                      final GordianKeyStorePairSet pSigner) throws OceanusException {
        /* Create the certificate */
        final GordianCoreKeyPairSetCertificate myCert = new GordianCoreKeyPairSetCertificate(theFactory, (GordianCoreKeyStorePairSet) pSigner, pKeyPairSet, pSubject, pUsage);

        /* Create the new chain */
        final List<GordianKeyPairSetCertificate> myParentChain = pSigner.getCertificateChain();
        final List<GordianKeyPairSetCertificate> myChain = new ArrayList<>(myParentChain);
        myChain.add(0, myCert);
        return myChain;
    }

    /**
     * Check Usage for keyPairSetSpec.
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @param pUsage   the key usage
     * @throws OceanusException on error
     */
    private static void checkKeyPairSetUsage(final GordianKeyPairSetSpec pKeyPairSetSpec,
                                             final GordianKeyPairUsage pUsage) throws OceanusException {
        /* Determine the requirements */
        final boolean needsSign = pUsage.hasUse(GordianKeyPairUse.CERTIFICATE)
                                    || pUsage.hasUse(GordianKeyPairUse.SIGNATURE);
        final boolean needsEnc = pUsage.hasUse(GordianKeyPairUse.KEYENCRYPT)
                                    || pUsage.hasUse(GordianKeyPairUse.DATAENCRYPT);
        final boolean needsAgree = pUsage.hasUse(GordianKeyPairUse.AGREEMENT);

        /* Validate keyPairSetSpec against requirements */
        final boolean bFail = (needsSign && !pKeyPairSetSpec.canSign())
                || (needsEnc && !pKeyPairSetSpec.canEncrypt())
                || (needsAgree && !pKeyPairSetSpec.canAgree());

        /* Handle failure */
        if (bFail) {
            throw new GordianLogicException("Unsupported Usage for keyPairSet");
        }
    }
}
