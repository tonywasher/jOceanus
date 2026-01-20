/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.gordianknot.impl.core.keystore;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianAsyncFactory;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.cert.GordianCoreCertificate;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStorePair;
import org.bouncycastle.asn1.x500.X500Name;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * KeyStore Manager implementation.
 */
public class GordianCoreKeyStoreManager
        implements GordianBaseKeyStoreManager {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * The keyStore.
     */
    private final GordianCoreKeyStore theKeyStore;

    /**
     * Constructor.
     *
     * @param pFactory  the factory
     * @param pKeyStore the keyStore
     */
    GordianCoreKeyStoreManager(final GordianBaseFactory pFactory,
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
                                           final char[] pPassword) throws GordianException {
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        final GordianKeySet myKeySet = myFactory.generateKeySet(pKeySetSpec);
        theKeyStore.setKeySet(pAlias, myKeySet, pPassword);
        return (GordianKeyStoreSet) theKeyStore.getEntry(pAlias, pPassword);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K extends GordianKeySpec> GordianKeyStoreKey<K> createKey(final K pKeySpec,
                                                                      final String pAlias,
                                                                      final char[] pPassword) throws GordianException {
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
                                                     final char[] pPassword) throws GordianException {
        /* Check that the keySpec can provide a signature */
        if (theFactory.getAsyncFactory().getSignatureFactory().defaultForKeyPair(pKeySpec) == null) {
            throw new GordianDataException("Root keyPair must be capable of signing");
        }

        /* Create the new keyPair */
        final GordianKeyPairFactory myFactory = theFactory.getAsyncFactory().getKeyPairFactory();
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
                                                 final char[] pPassword) throws GordianException {
        /* Create the new keyPair */
        checkKeyPairUsage(pKeySpec, pUsage);
        final GordianKeyPairFactory myFactory = theFactory.getAsyncFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pKeySpec);
        final GordianKeyPair myKeyPair = myGenerator.generateKeyPair();

        /* Create the certificate */
        final GordianCoreCertificate myCert = new GordianCoreCertificate(theFactory, pSigner, myKeyPair, pSubject, pUsage);

        /* Create the new chain */
        final List<GordianCertificate> myParentChain = pSigner.getCertificateChain();
        final List<GordianCertificate> myChain = new ArrayList<>(myParentChain);
        myChain.addFirst(myCert);

        /* Record into keyStore */
        theKeyStore.setKeyPair(pAlias, myKeyPair, pPassword, myChain);
        return (GordianCoreKeyStorePair) theKeyStore.getEntry(pAlias, pPassword);
    }

    @Override
    public GordianCoreKeyStorePair createAlternate(final GordianKeyStorePair pKeyPair,
                                                   final GordianKeyStorePair pSigner,
                                                   final String pAlias,
                                                   final char[] pPassword) throws GordianException {
        /* Access the keyPair and subject */
        final GordianCoreKeyPair myKeyPair = (GordianCoreKeyPair) pKeyPair.getKeyPair();
        final GordianCertificate myBase = pKeyPair.getCertificateChain().getFirst();
        final X500Name mySubject = myBase.getSubject().getName();
        final GordianKeyPairUsage myUsage = myBase.getUsage();

        /* Create the certificate */
        final GordianCoreCertificate myCert = new GordianCoreCertificate(theFactory, pSigner, myKeyPair, mySubject, myUsage);

        /* Create the new chain */
        final List<GordianCertificate> myParentChain = pSigner.getCertificateChain();
        final List<GordianCertificate> myChain = new ArrayList<>(myParentChain);
        myChain.addFirst(myCert);

        /* Record into keyStore */
        theKeyStore.setKeyPair(pAlias, myKeyPair, pPassword, myChain);
        return (GordianCoreKeyStorePair) theKeyStore.getEntry(pAlias, pPassword);
    }

    @Override
    public List<GordianCertificate> signKeyPair(final GordianKeyPair pKeyPair,
                                                final X500Name pSubject,
                                                final GordianKeyPairUsage pUsage,
                                                final GordianKeyStorePair pSigner) throws GordianException {
        /* Create the certificate */
        final GordianCoreCertificate myCert = new GordianCoreCertificate(theFactory, pSigner, pKeyPair, pSubject, pUsage);

        /* Create the new chain */
        final List<GordianCertificate> myParentChain = pSigner.getCertificateChain();
        final List<GordianCertificate> myChain = new ArrayList<>(myParentChain);
        myChain.addFirst(myCert);
        return myChain;
    }

    /**
     * Check Usage for keyPairSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pUsage       the key usage
     * @throws GordianException on error
     */
    private void checkKeyPairUsage(final GordianKeyPairSpec pKeyPairSpec,
                                   final GordianKeyPairUsage pUsage) throws GordianException {
        /* Determine the requirements */
        final boolean needsSign = pUsage.hasUse(GordianKeyPairUse.CERTIFICATE)
                || pUsage.hasUse(GordianKeyPairUse.SIGNATURE);
        final boolean needsEnc = pUsage.hasUse(GordianKeyPairUse.KEYENCRYPT)
                || pUsage.hasUse(GordianKeyPairUse.DATAENCRYPT);
        final boolean needsAgree = pUsage.hasUse(GordianKeyPairUse.AGREEMENT);

        /* Validate keyPairSpec against requirements */
        final GordianAsyncFactory myAsyncFactory = theFactory.getAsyncFactory();
        final boolean bFail = (needsSign && myAsyncFactory.getSignatureFactory().defaultForKeyPair(pKeyPairSpec) == null)
                || (needsEnc && myAsyncFactory.getEncryptorFactory().defaultForKeyPair(pKeyPairSpec) == null)
                || (needsAgree && myAsyncFactory.getXAgreementFactory().defaultForKeyPair(pKeyPairSpec) == null);

        /* Handle failure */
        if (bFail) {
            throw new GordianLogicException("Unsupported Usage for keyPair");
        }
    }
}
