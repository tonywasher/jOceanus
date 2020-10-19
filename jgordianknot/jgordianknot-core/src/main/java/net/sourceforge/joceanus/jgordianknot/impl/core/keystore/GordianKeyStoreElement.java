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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificateId;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairSetCertificate;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * KeyStoreElement.
 * <p>
 * These are the entries as held in the keyStore.
 * </p>
 */
public interface GordianKeyStoreElement {
    /**
     * KeyStore Certificate Key.
     */
    class GordianKeyStoreCertificateKey {
        /**
         * The issuer Id.
         */
        private final GordianCertificateId theIssuer;

        /**
         * The certificate Id.
         */
        private final GordianCertificateId theSubject;

        /**
         * Constructor.
         * @param pCertificate the certificate.
         */
        GordianKeyStoreCertificateKey(final GordianCertificate<?> pCertificate) {
            theIssuer = pCertificate.getIssuer();
            theSubject = pCertificate.getSubject();
        }

        /**
         * Constructor.
         * @param pIssuer the issuer.
         * @param pSubject the subject.
         */
        GordianKeyStoreCertificateKey(final GordianCertificateId pIssuer,
                                      final GordianCertificateId pSubject) {
            theIssuer = pIssuer;
            theSubject = pSubject;
        }

        /**
         * Obtain the issuer.
         * @return the issuer
         */
        public GordianCertificateId getIssuer() {
            return theIssuer;
        }

        /**
         * Obtain the subject.
         * @return the subject
         */
        public GordianCertificateId getSubject() {
            return theSubject;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial case */
            if (pThat == this) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Ensure object is correct class */
            if (!(pThat instanceof GordianKeyStoreCertificateKey)) {
                return false;
            }
            final GordianKeyStoreCertificateKey myThat = (GordianKeyStoreCertificateKey) pThat;

            /* Check that the subject and issuers match */
            return theSubject.equals(myThat.getSubject())
                    && theIssuer.equals(myThat.getIssuer());
        }

        @Override
        public int hashCode() {
            return theSubject.hashCode()
                    + theIssuer.hashCode();
        }
    }

    /**
     * KeyStore Certificate.
     */
    interface GordianKeyStoreCertificateHolder {
        /**
         * Obtain the key.
         * @return the key
         */
        GordianKeyStoreCertificateKey getCertificateKey();
    }

    /**
     * KeyStore Certificate Element.
     * @param <K> the keyPair type
     */
    abstract class GordianKeyStoreCertificateElement<K>
            extends GordianCoreKeyStoreEntry
            implements GordianKeyStoreCertificateHolder {
        /**
         * The certificate Id.
         */
        private final GordianKeyStoreCertificateKey theKey;

        /**
         * Constructor.
         * @param pCertificate the certificate.
         */
        GordianKeyStoreCertificateElement(final GordianCertificate<K> pCertificate) {
            theKey = new GordianKeyStoreCertificateKey(pCertificate);
        }

        /**
         * Constructor.
         * @param pKey the key.
         * @param pDate the creation date
         */
        GordianKeyStoreCertificateElement(final GordianKeyStoreCertificateKey pKey,
                                          final TethysDate pDate) {
            super(pDate);
            theKey = pKey;
        }

        @Override
        public GordianKeyStoreCertificateKey getCertificateKey() {
            return theKey;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial case */
            if (pThat == this) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Ensure object is correct class */
            if (!(pThat instanceof GordianKeyStoreCertificateElement)) {
                return false;
            }
            final GordianKeyStoreCertificateElement<?> myThat = (GordianKeyStoreCertificateElement<?>) pThat;

            /* Check that the keys match */
            return theKey.equals(myThat.getCertificateKey())
                    && super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return theKey.hashCode()
                    + super.hashCode();
        }
    }

    /**
     * KeyStore pairCertificate Element.
     */
    class GordianKeyStorePairCertificateElement
            extends GordianKeyStoreCertificateElement<GordianKeyPair> {
        /**
         * Constructor.
         * @param pCertificate the certificate.
         */
        GordianKeyStorePairCertificateElement(final GordianKeyPairCertificate pCertificate) {
            super(pCertificate);
        }

        /**
         * Constructor.
         * @param pKey the key.
         * @param pDate the creation date
         */
        GordianKeyStorePairCertificateElement(final GordianKeyStoreCertificateKey pKey,
                                              final TethysDate pDate) {
            super(pKey, pDate);
        }

        /**
         * Obtain the corresponding keyStoreEntry.
         * @param pKeyStore the keyStore
         * @return the keyStore certificate entry
         */
        GordianCoreKeyStorePairCertificate buildEntry(final GordianCoreKeyStore pKeyStore) {
            final GordianCoreKeyPairCertificate myCert = (GordianCoreKeyPairCertificate) pKeyStore.getKeyPairCertificate(getCertificateKey());
            return new GordianCoreKeyStorePairCertificate(myCert, getCreationDate());
        }
    }

    /**
     * KeyStore pairSetCertificate Element.
     */
    class GordianKeyStorePairSetCertificateElement
            extends GordianKeyStoreCertificateElement<GordianKeyPairSet> {
        /**
         * Constructor.
         * @param pCertificate the certificate.
         */
        GordianKeyStorePairSetCertificateElement(final GordianKeyPairSetCertificate pCertificate) {
            super(pCertificate);
        }

        /**
         * Constructor.
         * @param pKey the key.
         * @param pDate the creation date
         */
        GordianKeyStorePairSetCertificateElement(final GordianKeyStoreCertificateKey pKey,
                                                 final TethysDate pDate) {
            super(pKey, pDate);
        }

        /**
         * Obtain the corresponding keyStoreEntry.
         * @param pKeyStore the keyStore
         * @return the keyStore certificate entry
         */
        GordianCoreKeyStorePairSetCertificate buildEntry(final GordianCoreKeyStore pKeyStore) {
            final GordianCoreKeyPairSetCertificate myCert = (GordianCoreKeyPairSetCertificate) pKeyStore.getKeyPairSetCertificate(getCertificateKey());
            return new GordianCoreKeyStorePairSetCertificate(myCert, getCreationDate());
        }
    }

    /**
     * KeyStore KeyPair Element.
     * @param <C> the certificate type
     * @param <K> keyPair type
     */
    abstract class GordianKeyStorePairElementBase<C extends GordianCertificate<K>, K>
            extends GordianCoreKeyStoreEntry
            implements GordianKeyStoreCertificateHolder {
        /**
         * The securedPrivateKey.
         */
        private final byte[] theSecuredKey;

        /**
         * The securing hash.
         */
        private final GordianKeyStoreHashElement theSecuringHash;

        /**
         * The certificate chain.
         */
        private final List<GordianKeyStoreCertificateKey> theChain;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the keySetHashSpec
         * @param pKeyPair the keyPair
         * @param pPassword the securing password.
         * @param pChain the certificate chain.
         * @throws OceanusException on error
         */
        GordianKeyStorePairElementBase(final GordianFactory pFactory,
                                       final GordianKeySetHashSpec pSpec,
                                       final K pKeyPair,
                                       final char[] pPassword,
                                       final List<C> pChain) throws OceanusException {
            /* Create a securing hash */
            final GordianKeySetFactory myFactory = pFactory.getKeySetFactory();
            final GordianKeySetHash myHash = myFactory.generateKeySetHash(pSpec, pPassword);
            theSecuringHash = new GordianKeyStoreHashElement(myHash);
            final GordianKeySet myKeySet = myHash.getKeySet();

            /* Secure the privateKey */
            theSecuredKey = securePrivateKey(myKeySet, pKeyPair);

            /* Create the chain */
            theChain = new ArrayList<>();
            for (C myCert : pChain) {
                theChain.add(new GordianKeyStoreCertificateKey(myCert));
            }
        }

        /**
         * Constructor.
         * @param pSecuredKey the secured privateKey
         * @param pSecuringHash the securing hash.
         * @param pChain the certificate chain.
         * @param pDate the creation date
         */
        GordianKeyStorePairElementBase(final byte[] pSecuredKey,
                                       final byte[] pSecuringHash,
                                       final List<GordianKeyStoreCertificateKey> pChain,
                                       final TethysDate pDate) {
            /* Store details */
            super(pDate);
            theSecuredKey = pSecuredKey;
            theSecuringHash = new GordianKeyStoreHashElement(pSecuringHash, pDate);
            theChain = new ArrayList<>(pChain);
        }

        /**
         * Obtain the securedKey.
         * @return the securedKey
         */
        byte[] getSecuredKey() {
            return theSecuredKey;
        }

        /**
         * Obtain the securingHash.
         * @return the securingHash
         */
        GordianKeyStoreHashElement getSecuringHash() {
            return theSecuringHash;
        }

        /**
         * Obtain the securingHashHash.
         * @return the securingHashHash
         */
        byte[] getSecuringHashHash() {
            return theSecuringHash.getHash();
        }

        @Override
        public GordianKeyStoreCertificateKey getCertificateKey() {
            return theChain.get(0);
        }

        /**
         * Obtain the certificateChain.
         * @return the certificateChain
         */
        List<GordianKeyStoreCertificateKey> getCertificateChain() {
            return theChain;
        }

        /**
         * Obtain the secured privateKey.
         * @param pKeySet the keySet
         * @param pKeyPair the keyPair
         * @return the securedPrivateKey
         * @throws OceanusException on error
         */
        byte[] securePrivateKey(final GordianKeySet pKeySet,
                                final K pKeyPair) throws OceanusException {
            return theSecuringHash.getHash();
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial case */
            if (pThat == this) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Ensure object is correct class */
            if (!(pThat instanceof GordianKeyStorePairElementBase)) {
                return false;
            }
            final GordianKeyStorePairElementBase<?, ?> myThat = (GordianKeyStorePairElementBase<?, ?>) pThat;

            /* Check that the hashes match */
            return Arrays.equals(theSecuredKey, myThat.getSecuredKey())
                    && Arrays.equals(getSecuringHashHash(), myThat.getSecuringHashHash())
                    && theChain.equals(myThat.getCertificateChain())
                    && super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(theSecuredKey)
                    + Arrays.hashCode(getSecuringHashHash())
                    + theChain.hashCode()
                    + super.hashCode();
        }
    }

    /**
     * KeyStore KeyPair Element.
     */
    class GordianKeyStorePairElement
            extends GordianKeyStorePairElementBase<GordianKeyPairCertificate, GordianKeyPair> {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the keySetHashSpec
         * @param pKeyPair the keyPair
         * @param pPassword the securing password.
         * @param pChain the certificate chain.
         * @throws OceanusException on error
         */
        GordianKeyStorePairElement(final GordianFactory pFactory,
                                   final GordianKeySetHashSpec pSpec,
                                   final GordianKeyPair pKeyPair,
                                   final char[] pPassword,
                                   final List<GordianKeyPairCertificate> pChain) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec, pKeyPair, pPassword, pChain);
        }

        /**
         * Constructor.
         * @param pSecuredKey the secured privateKey
         * @param pSecuringHash the securing hash.
         * @param pChain the certificate chain.
         * @param pDate the creation date
         */
        GordianKeyStorePairElement(final byte[] pSecuredKey,
                                   final byte[] pSecuringHash,
                                   final List<GordianKeyStoreCertificateKey> pChain,
                                   final TethysDate pDate) {
            /* Initialise underlying class */
            super(pSecuredKey, pSecuringHash, pChain, pDate);
        }

        @Override
        byte[] securePrivateKey(final GordianKeySet pKeySet,
                                final GordianKeyPair pKeyPair) throws OceanusException {
            return pKeySet.securePrivateKey(pKeyPair);
        }

        /**
         * Build the corresponding certificate chain.
         * @param pKeyStore the keyStore
         * @return the certificate chain
         */
        List<GordianKeyPairCertificate> buildChain(final GordianCoreKeyStore pKeyStore) {
            /* Create the chain */
            final List<GordianKeyStoreCertificateKey> myKeys = getCertificateChain();
            final List<GordianKeyPairCertificate> myChain = new ArrayList<>();
            for (GordianKeyStoreCertificateKey myKey : myKeys) {
                myChain.add(pKeyStore.getKeyPairCertificate(myKey));
            }
            return myChain;
        }

        /**
         * Build the corresponding keyStoreEntry.
         * @param pKeyStore the keyStore
         * @param pPassword the password
         * @return the keyStore certificate entry
         * @throws OceanusException on error
         */
        GordianCoreKeyStorePair buildEntry(final GordianCoreKeyStore pKeyStore,
                                           final char[] pPassword) throws OceanusException {
            /* Create the chain */
            final List<GordianKeyPairCertificate> myChain = buildChain(pKeyStore);

            /* Resolve securing hash */
            final GordianKeyStoreHash myHash = getSecuringHash().buildEntry(pKeyStore, pPassword);

            /* derive the keyPair */
            final GordianKeySet myKeySet = myHash.getKeySetHash().getKeySet();
            final GordianCoreKeyPairCertificate myCert = (GordianCoreKeyPairCertificate) myChain.get(0);
            final GordianKeyPair myPair = myKeySet.deriveKeyPair(myCert.getX509KeySpec(), getSecuredKey());

            /* Create the entry */
            return new GordianCoreKeyStorePair(myPair, myChain, getCreationDate());
        }
    }

    /**
     * KeyStore KeyPairSet Element.
     */
    class GordianKeyStorePairSetElement
            extends GordianKeyStorePairElementBase<GordianKeyPairSetCertificate, GordianKeyPairSet> {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the keySetHashSpec
         * @param pKeyPair the keyPair
         * @param pPassword the securing password.
         * @param pChain the certificate chain.
         * @throws OceanusException on error
         */
        GordianKeyStorePairSetElement(final GordianFactory pFactory,
                                      final GordianKeySetHashSpec pSpec,
                                      final GordianKeyPairSet pKeyPair,
                                      final char[] pPassword,
                                      final List<GordianKeyPairSetCertificate> pChain) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec, pKeyPair, pPassword, pChain);
        }

        /**
         * Constructor.
         * @param pSecuredKey the secured privateKey
         * @param pSecuringHash the securing hash.
         * @param pChain the certificate chain.
         * @param pDate the creation date
         */
        GordianKeyStorePairSetElement(final byte[] pSecuredKey,
                                      final byte[] pSecuringHash,
                                      final List<GordianKeyStoreCertificateKey> pChain,
                                      final TethysDate pDate) {
            /* Initialise underlying class */
            super(pSecuredKey, pSecuringHash, pChain, pDate);
        }

        @Override
        byte[] securePrivateKey(final GordianKeySet pKeySet,
                                final GordianKeyPairSet pKeyPairSet) throws OceanusException {
            return pKeySet.securePrivateKeySet(pKeyPairSet);
        }

        /**
         * Build the corresponding certificate chain.
         * @param pKeyStore the keyStore
         * @return the certificate chain
         */
        List<GordianKeyPairSetCertificate> buildChain(final GordianCoreKeyStore pKeyStore) {
            /* Create the chain */
            final List<GordianKeyStoreCertificateKey> myKeys = getCertificateChain();
            final List<GordianKeyPairSetCertificate> myChain = new ArrayList<>();
            for (GordianKeyStoreCertificateKey myKey : myKeys) {
                myChain.add(pKeyStore.getKeyPairSetCertificate(myKey));
            }
            return myChain;
        }

        /**
         * Build the corresponding keyStoreEntry.
         * @param pKeyStore the keyStore
         * @param pPassword the password
         * @return the keyStore certificate entry
         * @throws OceanusException on error
         */
        GordianCoreKeyStorePairSet buildEntry(final GordianCoreKeyStore pKeyStore,
                                              final char[] pPassword) throws OceanusException {
            /* Create the chain */
            final List<GordianKeyPairSetCertificate> myChain = buildChain(pKeyStore);

            /* Resolve securing hash */
            final GordianKeyStoreHash myHash = getSecuringHash().buildEntry(pKeyStore, pPassword);

            /* derive the keyPair */
            final GordianKeySet myKeySet = myHash.getKeySetHash().getKeySet();
            final GordianCoreKeyPairSetCertificate myCert = (GordianCoreKeyPairSetCertificate) myChain.get(0);
            final GordianKeyPairSet myPair = myKeySet.deriveKeyPairSet(myCert.getX509KeySpec(), getSecuredKey());

            /* Create the entry */
            return new GordianCoreKeyStorePairSet(myPair, myChain, getCreationDate());
        }
    }

    /**
     * KeyStore hash Element.
     */
    class GordianKeyStoreHashElement
            extends GordianCoreKeyStoreEntry {
        /**
         * The hash.
         */
        private final byte[] theHash;

        /**
         * Constructor.
         * @param pHash the hash.
         */
        GordianKeyStoreHashElement(final GordianKeySetHash pHash) {
            /* Store details */
            theHash = pHash.getHash();
        }

        /**
         * Constructor.
         * @param pHash the hash.
         * @param pDate the creation date
         */
        GordianKeyStoreHashElement(final byte[] pHash,
                                   final TethysDate pDate) {
            /* Store details */
            super(pDate);
            theHash = pHash;
        }

        /**
         * Obtain the hash.
         * @return the hash
         */
        byte[] getHash()  {
            return theHash;
        }

        /**
         * Obtain the corresponding keyStoreEntry.
         * @param pKeyStore the keyStore
         * @param pPassword the password
         * @return the keyStore certificate entry
         * @throws OceanusException on error
         */
        GordianCoreKeyStoreHash buildEntry(final GordianCoreKeyStore pKeyStore,
                                           final char[] pPassword) throws OceanusException {
            /* Resolve the hash */
            final GordianKeySetFactory myFactory = pKeyStore.getFactory().getKeySetFactory();
            final GordianKeySetHash myHash = myFactory.deriveKeySetHash(theHash, pPassword);
            return new GordianCoreKeyStoreHash(myHash, getCreationDate());
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial case */
            if (pThat == this) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Ensure object is correct class */
            if (!(pThat instanceof GordianKeyStoreHashElement)) {
                return false;
            }
            final GordianKeyStoreHashElement myThat = (GordianKeyStoreHashElement) pThat;

            /* Check that the hashes match */
            return Arrays.equals(theHash, myThat.getHash())
                    && super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(theHash)
                    + super.hashCode();
        }
    }

    /**
     * KeyStore key Element.
     * @param <T> the key type
     */
    class GordianKeyStoreKeyElement<T extends GordianKeySpec>
            extends GordianCoreKeyStoreEntry {
        /**
         * The keyType.
         */
        private final T theKeyType;

        /**
         * The securedKey.
         */
        private final byte[] theSecuredKey;

        /**
         * The securing hash.
         */
        private final GordianKeyStoreHashElement theSecuringHash;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the keySetHashSpec
         * @param pKey the key
         * @param pPassword the securing password.
         * @throws OceanusException on error
         */
        GordianKeyStoreKeyElement(final GordianFactory pFactory,
                                  final GordianKeySetHashSpec pSpec,
                                  final GordianKey<T> pKey,
                                  final char[] pPassword) throws OceanusException {
            /* Create a securing hash */
            final GordianKeySetFactory myFactory = pFactory.getKeySetFactory();
            final GordianKeySetHash myHash = myFactory.generateKeySetHash(pSpec, pPassword);
            final GordianKeySet myKeySet = myHash.getKeySet();

            /* Store details */
            theKeyType = pKey.getKeyType();
            theSecuredKey = myKeySet.secureKey(pKey);
            theSecuringHash = new GordianKeyStoreHashElement(myHash);
        }

        /**
         * Constructor.
         * @param pKeyType the keyType
         * @param pSecuredKey the key
         * @param pSecuringHash the securing hash.
         * @param pDate the creation date
         */
        GordianKeyStoreKeyElement(final T pKeyType,
                                  final byte[] pSecuredKey,
                                  final byte[] pSecuringHash,
                                  final TethysDate pDate) {
            /* Store details */
            super(pDate);
            theKeyType = pKeyType;
            theSecuredKey = pSecuredKey;
            theSecuringHash = new GordianKeyStoreHashElement(pSecuringHash, pDate);
        }

        /**
         * Obtain the keyType.
         * @return the keyType
         */
        T getKeyType() {
            return theKeyType;
        }

        /**
         * Obtain the securedKey.
         * @return the securedKey
         */
        byte[] getSecuredKey() {
            return theSecuredKey;
        }

        /**
         * Obtain the securingHash.
         * @return the securingHash
         */
        byte[] getSecuringHash() {
            return theSecuringHash.getHash();
        }

        /**
         * Obtain the corresponding keyStoreEntry.
         * @param pKeyStore the keyStore
         * @param pPassword the password
         * @return the keyStore certificate entry
         * @throws OceanusException on error
         */
        GordianCoreKeyStoreKey<T> buildEntry(final GordianCoreKeyStore pKeyStore,
                                             final char[] pPassword) throws OceanusException {
            /* Resolve securing hash */
            final GordianKeyStoreHash myHash = theSecuringHash.buildEntry(pKeyStore, pPassword);

            /* derive the key */
            final GordianKeySet myKeySet = myHash.getKeySetHash().getKeySet();
            final GordianKey<T> myKey = myKeySet.deriveKey(theSecuredKey, theKeyType);
            return new GordianCoreKeyStoreKey<>(myKey, getCreationDate());
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial case */
            if (pThat == this) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Ensure object is correct class */
            if (!(pThat instanceof GordianKeyStoreKeyElement)) {
                return false;
            }
            final GordianKeyStoreKeyElement<?> myThat = (GordianKeyStoreKeyElement<?>) pThat;

            /* Check that the hashes match */
            return theKeyType.equals(myThat.getKeyType())
                    && Arrays.equals(theSecuredKey, myThat.getSecuredKey())
                    && Arrays.equals(getSecuringHash(), myThat.getSecuringHash())
                    && super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return theKeyType.hashCode()
                    + Arrays.hashCode(theSecuredKey)
                    + Arrays.hashCode(getSecuringHash())
                    + super.hashCode();
        }
    }

    /**
     * KeyStore keySet Element.
     */
    class GordianKeyStoreSetElement
            extends GordianCoreKeyStoreEntry {
        /**
         * The keyMap.
         */
        private final byte[] theSecuredKeySet;

        /**
         * The securing hash.
         */
        private final GordianKeyStoreHashElement theSecuringHash;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the keySetHashSpec
         * @param pKeySet the keySet
         * @param pPassword the securing password.
         * @throws OceanusException on error
         */
        GordianKeyStoreSetElement(final GordianFactory pFactory,
                                  final GordianKeySetHashSpec pSpec,
                                  final GordianKeySet pKeySet,
                                  final char[] pPassword) throws OceanusException {
            /* Create a securing hash */
            final GordianKeySetFactory myFactory = pFactory.getKeySetFactory();
            final GordianKeySetHash myHash = myFactory.generateKeySetHash(pSpec, pPassword);
            final GordianKeySet myKeySet = myHash.getKeySet();
            theSecuringHash = new GordianKeyStoreHashElement(myHash);

            /* Secure the keySet */
            theSecuredKeySet = myKeySet.secureKeySet(pKeySet);
       }

        /**
         * Constructor.
         * @param pSecuredKeySet the securedKeySet
         * @param pSecuringHash the securing hash.
         * @param pDate the creation date
         */
        GordianKeyStoreSetElement(final byte[] pSecuredKeySet,
                                  final byte[] pSecuringHash,
                                  final TethysDate pDate) {
            /* Store details */
            super(pDate);
            theSecuredKeySet = pSecuredKeySet;
            theSecuringHash = new GordianKeyStoreHashElement(pSecuringHash, pDate);
        }

        /**
         * Obtain the keyType.
         * @return the keyType
         */
        byte[] getSecuredKeySet() {
            return theSecuredKeySet;
        }

        /**
         * Obtain the securingHash.
         * @return the securingHash
         */
        byte[] getSecuringHash() {
            return theSecuringHash.getHash();
        }

        /**
         * Obtain the corresponding keyStoreEntry.
         * @param pKeyStore the keyStore
         * @param pPassword the password
         * @return the keyStore certificate entry
         * @throws OceanusException on error
         */
        GordianCoreKeyStoreSet buildEntry(final GordianCoreKeyStore pKeyStore,
                                          final char[] pPassword) throws OceanusException {
            /* Resolve the hash */
            final GordianKeyStoreHash myHash = theSecuringHash.buildEntry(pKeyStore, pPassword);
            final GordianKeySet mySecuringKeySet = myHash.getKeySetHash().getKeySet();

            /* Derive the keySet */
            final GordianKeySet myKeySet = mySecuringKeySet.deriveKeySet(theSecuredKeySet);

            /* build the new entry */
            return new GordianCoreKeyStoreSet(myKeySet, getCreationDate());
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial case */
            if (pThat == this) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Ensure object is correct class */
            if (!(pThat instanceof GordianKeyStoreSetElement)) {
                return false;
            }
            final GordianKeyStoreSetElement myThat = (GordianKeyStoreSetElement) pThat;

            /* Check that the hashes match */
            return Arrays.equals(theSecuredKeySet, myThat.getSecuredKeySet())
                    && Arrays.equals(getSecuringHash(), myThat.getSecuringHash())
                    && super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(theSecuredKeySet)
                    + Arrays.hashCode(getSecuringHash())
                    + super.hashCode();
        }
    }
}
