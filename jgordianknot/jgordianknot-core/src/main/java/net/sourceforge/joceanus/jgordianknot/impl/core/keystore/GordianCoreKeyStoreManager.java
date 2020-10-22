/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bouncycastle.asn1.x500.X500Name;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairSet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreManager;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
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
    public GordianKeyStorePair createRootKeyPair(final GordianKeyPairSpec pKeySpec,
                                                 final X500Name pSubject,
                                                 final String pAlias,
                                                 final char[] pPassword) throws OceanusException {
        /* Create the new keyPair */
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pKeySpec);
        final GordianCoreKeyPair myKeyPair = (GordianCoreKeyPair) myGenerator.generateKeyPair();

        /* Create the certificate */
        final GordianCoreKeyPairCertificate myCert = new GordianCoreKeyPairCertificate(theFactory, myKeyPair, pSubject);
        final List<GordianKeyPairCertificate> myChain = Collections.singletonList(myCert);

        /* Record into keyStore */
        theKeyStore.setKeyPair(pAlias, myKeyPair, pPassword, myChain);
        return (GordianKeyStorePair) theKeyStore.getEntry(pAlias, pPassword);
    }


    @Override
    public GordianKeyStorePair createKeyPair(final GordianKeyPairSpec pKeySpec,
                                             final X500Name pSubject,
                                             final GordianKeyPairUsage pUsage,
                                             final GordianKeyStorePair pSigner,
                                             final String pAlias,
                                             final char[] pPassword) throws OceanusException {
        /* Create the new keyPair */
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
        return (GordianKeyStorePair) theKeyStore.getEntry(pAlias, pPassword);
    }

    @Override
    public GordianKeyStorePair createAlternate(final GordianKeyStorePair pKeyPair,
                                               final GordianKeyPairUsage pUsage,
                                               final GordianKeyStorePair pSigner,
                                               final String pAlias,
                                               final char[] pPassword) throws OceanusException {
        /* Access the keyPair and subject */
        final GordianCoreKeyPair myKeyPair = (GordianCoreKeyPair) pKeyPair.getKeyPair();
        final X500Name mySubject = pKeyPair.getCertificateChain().get(0).getSubject().getName();

        /* Create the certificate */
        final GordianCoreKeyPairCertificate myCert = new GordianCoreKeyPairCertificate(theFactory, (GordianCoreKeyStorePair) pSigner, myKeyPair, mySubject, pUsage);

        /* Create the new chain */
        final List<GordianKeyPairCertificate> myParentChain = pSigner.getCertificateChain();
        final List<GordianKeyPairCertificate> myChain = new ArrayList<>(myParentChain);
        myChain.add(0, myCert);

        /* Record into keyStore */
        theKeyStore.setKeyPair(pAlias, myKeyPair, pPassword, myChain);
        return (GordianKeyStorePair) theKeyStore.getEntry(pAlias, pPassword);
    }

    @Override
    public GordianKeyStorePairSet createRootKeyPairSet(final GordianKeyPairSetSpec pKeySetSpec,
                                                       final X500Name pSubject,
                                                       final String pAlias,
                                                       final char[] pPassword) throws OceanusException {
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
        return (GordianKeyStorePairSet) theKeyStore.getEntry(pAlias, pPassword);
    }


    @Override
    public GordianKeyStorePairSet createKeyPairSet(final GordianKeyPairSetSpec pKeySetSpec,
                                                   final X500Name pSubject,
                                                   final GordianKeyPairUsage pUsage,
                                                   final GordianKeyStorePairSet pSigner,
                                                   final String pAlias,
                                                   final char[] pPassword) throws OceanusException {
        /* Create the new keyPair */
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
        return (GordianKeyStorePairSet) theKeyStore.getEntry(pAlias, pPassword);
    }

    @Override
    public GordianKeyStorePairSet createAlternate(final GordianKeyStorePairSet pKeyPairSet,
                                                  final GordianKeyPairUsage pUsage,
                                                  final GordianKeyStorePairSet pSigner,
                                                  final String pAlias,
                                                  final char[] pPassword) throws OceanusException {
        /* Access the keyPair and subject */
        final GordianCoreKeyPairSet myKeyPairSet = (GordianCoreKeyPairSet) pKeyPairSet.getKeyPairSet();
        final X500Name mySubject = pKeyPairSet.getCertificateChain().get(0).getSubject().getName();

        /* Create the certificate */
        final GordianCoreKeyPairSetCertificate myCert = new GordianCoreKeyPairSetCertificate(theFactory, (GordianCoreKeyStorePairSet) pSigner, myKeyPairSet, mySubject, pUsage);

        /* Create the new chain */
        final List<GordianKeyPairSetCertificate> myParentChain = pSigner.getCertificateChain();
        final List<GordianKeyPairSetCertificate> myChain = new ArrayList<>(myParentChain);
        myChain.add(0, myCert);

        /* Record into keyStore */
        theKeyStore.setKeyPairSet(pAlias, myKeyPairSet, pPassword, myChain);
        return (GordianKeyStorePairSet) theKeyStore.getEntry(pAlias, pPassword);
    }

    @Override
    public void exportEntry(final String pAlias,
                            final File pFile,
                            final char[] pPassword) throws OceanusException {
        try (FileOutputStream myStream = new FileOutputStream(pFile)) {
            exportEntry(pAlias, myStream, pPassword);
        } catch (IOException e) {
            throw new GordianIOException("Failed to write to file", e);
        }
    }

    @Override
    public void exportEntry(final String pAlias,
                            final OutputStream pStream,
                            final char[] pPassword) throws OceanusException {
        final GordianKeyStoreEntry myEntry = theKeyStore.getEntry(pAlias, pPassword);
        final GordianPEMCoder myCoder = new GordianPEMCoder(theKeyStore);
        myCoder.exportKeyStoreEntry(myEntry, pStream, pPassword);
    }

    @Override
    public GordianKeyStoreEntry importEntry(final File pFile,
                                            final char[] pPassword) throws OceanusException {
        try (FileInputStream myStream = new FileInputStream(pFile)) {
            return importEntry(myStream, pPassword);
        } catch (IOException e) {
            throw new GordianIOException("Failed to read from file", e);
        }
    }

    @Override
    public GordianKeyStoreEntry importEntry(final InputStream pStream,
                                            final char[] pPassword) throws OceanusException {
        final GordianPEMCoder myCoder = new GordianPEMCoder(theKeyStore);
        return myCoder.importKeyStoreEntry(pStream, pPassword);
    }
}
