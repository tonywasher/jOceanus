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
package net.sourceforge.joceanus.jgordianknot.impl.core.keystore;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificateId;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
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
     * KeyStore Certificate.
     */
    static class GordianKeyStoreCertificateKey {
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
        GordianKeyStoreCertificateKey(final GordianCertificate pCertificate) {
            theIssuer = pCertificate.getIssuer();
            theSubject = pCertificate.getSubject();
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
     */
    static class GordianKeyStoreCertificateElement
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
        GordianKeyStoreCertificateElement(final GordianCertificate pCertificate) {
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

        /**
         * Obtain the corresponding keyStoreEntry.
         * @param pKeyStore the keyStore
         * @return the keyStore certificate entry
         */
        GordianCoreKeyStoreCertificate buildEntry(final GordianCoreKeyStore pKeyStore) {
            final GordianCoreCertificate myCert = pKeyStore.getCertificate(theKey);
            return new GordianCoreKeyStoreCertificate(myCert, getCreationDate());
        }
    }

    /**
     * KeyStore KeyPair Element.
     */
    static class GordianKeyStorePairElement
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
        private final GordianKeyStoreCertificateKey[] theChain;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pKeyPair the keyPair
         * @param pPassword the securing password.
         * @param pChain the certificate chain.
         * @throws OceanusException on error
         */
        GordianKeyStorePairElement(final GordianFactory pFactory,
                                   final GordianKeyPair pKeyPair,
                                   final char[] pPassword,
                                   final GordianCertificate[] pChain) throws OceanusException {
            /* Create a securing hash */
            final GordianKeySetFactory myFactory = pFactory.getKeySetFactory();
            final GordianKeySetHash myHash = myFactory.generateKeySetHash(pPassword);
            final GordianKeySet myKeySet = myHash.getKeySet();

            /* Store details */
            theSecuredKey = myKeySet.securePrivateKey(pKeyPair);
            theSecuringHash = new GordianKeyStoreHashElement(myHash);

            /* Create the chain */
            theChain = new GordianKeyStoreCertificateKey[pChain.length];
            for (int i = 0; i < pChain.length; i++) {
                theChain[i] = new GordianKeyStoreCertificateKey(pChain[i]);
            }
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
                                   final GordianKeyStoreCertificateKey[] pChain,
                                   final TethysDate pDate) {
            /* Store details */
            super(pDate);
            theSecuredKey = pSecuredKey;
            theSecuringHash = new GordianKeyStoreHashElement(pSecuringHash, pDate);
            theChain = Arrays.copyOf(pChain, pChain.length);
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

        @Override
        public GordianKeyStoreCertificateKey getCertificateKey() {
            return theChain[0];
        }

        /**
         * Obtain the certificateChain.
         * @return the certificateChain
         */
        GordianKeyStoreCertificateKey[] getCertificateChain() {
            return theChain;
        }

        /**
         * Obtain the corresponding keyStoreEntry.
         * @param pKeyStore the keyStore
         * @param pPassword the password
         * @return the keyStore certificate entry
         * @throws OceanusException on error
         */
        GordianCoreKeyStorePair buildEntry(final GordianCoreKeyStore pKeyStore,
                                           final char[] pPassword) throws OceanusException {
            /* Create the chain */
            final GordianCoreCertificate[] myChain = new GordianCoreCertificate[theChain.length];
            for (int i = 0; i < theChain.length; i++) {
                myChain[i] = pKeyStore.getCertificate(theChain[i]);
            }

            /* Resolve securing hash */
            final GordianKeyStoreHash myHash = theSecuringHash.buildEntry(pKeyStore, pPassword);

            /* derive the keyPair */
            final GordianKeySet myKeySet = myHash.getKeySetHash().getKeySet();
            final GordianKeyPair myPair = myKeySet.deriveKeyPair(myChain[0].getX509KeySpec(), theSecuredKey);

            /* Create the entry */
            return new GordianCoreKeyStorePair(myPair, myChain, getCreationDate());
        }
    }

    /**
     * KeyStore hash Element.
     */
    static class GordianKeyStoreHashElement
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
    }

    /**
     * KeyStore key Element.
     * @param <T> the key type
     */
    static class GordianKeyStoreKeyElement<T extends GordianKeySpec>
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
         * @param pKey the key
         * @param pPassword the securing password.
         * @throws OceanusException on error
         */
        GordianKeyStoreKeyElement(final GordianFactory pFactory,
                                  final GordianKey<T> pKey,
                                  final char[] pPassword) throws OceanusException {
            /* Create a securing hash */
            final GordianKeySetFactory myFactory = pFactory.getKeySetFactory();
            final GordianKeySetHash myHash = myFactory.generateKeySetHash(pPassword);
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
    }

    /**
     * KeyStore keySet Element.
     */
    static class GordianKeyStoreSetElement
            extends GordianCoreKeyStoreEntry {
        /**
         * The keyMap.
         */
        private final Map<GordianSymKeyType, byte[]> theKeyMap;

        /**
         * The securing hash.
         */
        private final GordianKeyStoreHashElement theSecuringHash;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pKeySet the keySet
         * @param pPassword the securing password.
         * @throws OceanusException on error
         */
        GordianKeyStoreSetElement(final GordianFactory pFactory,
                                  final GordianKeySet pKeySet,
                                  final char[] pPassword) throws OceanusException {
            /* Access the KeySet */
            final GordianCoreKeySet mySrcKeySet = (GordianCoreKeySet) pKeySet;

            /* Create a securing hash */
            final GordianKeySetFactory myFactory = pFactory.getKeySetFactory();
            final GordianKeySetHash myHash = myFactory.generateKeySetHash(pPassword);
            final GordianKeySet myKeySet = myHash.getKeySet();
            theSecuringHash = new GordianKeyStoreHashElement(myHash);

            /* Create the map */
            theKeyMap = new EnumMap<>(GordianSymKeyType.class);

            /* For each key in the map */
            for (Map.Entry<GordianSymKeySpec, GordianKey<GordianSymKeySpec>> myEntry : mySrcKeySet.getSymKeyMap().entrySet()) {
                final GordianSymKeyType myKeyType = myEntry.getKey().getSymKeyType();
                final byte[] mySecuredKey = myKeySet.secureKey(myEntry.getValue());
                theKeyMap.put(myKeyType, mySecuredKey);
            }
       }

        /**
         * Constructor.
         * @param pKeyMap the keyMap
         * @param pSecuringHash the securing hash.
         * @param pDate the creation date
         */
        GordianKeyStoreSetElement(final Map<GordianSymKeyType, byte[]> pKeyMap,
                                  final byte[] pSecuringHash,
                                  final TethysDate pDate) {
            /* Store details */
            super(pDate);
            theSecuringHash = new GordianKeyStoreHashElement(pSecuringHash, pDate);

            /* Create the map */
            theKeyMap = new EnumMap<>(pKeyMap);
        }

        /**
         * Obtain the keyType.
         * @return the keyType
         */
        Map<GordianSymKeyType, byte[]> getKeyMap() {
            return theKeyMap;
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

            /* Create a new keySet */
            final GordianKeySetFactory myFactory = pKeyStore.getFactory().getKeySetFactory();
            final GordianKeySet myKeySet = myFactory.createKeySet();

            /* Loop through the keys */
            for (Map.Entry<GordianSymKeyType, byte[]> myEntry : theKeyMap.entrySet()) {
                /* Derive and declare the key */
                final GordianSymKeySpec mySpec = new GordianSymKeySpec(myEntry.getKey(), GordianLength.LEN_128);
                final GordianKey<GordianSymKeySpec> myKey = mySecuringKeySet.deriveKey(myEntry.getValue(), mySpec);
                myKeySet.declareSymKey(myKey);
            }

            /* build the new entry */
            return new GordianCoreKeyStoreSet(myKeySet, getCreationDate());
        }
    }
}
