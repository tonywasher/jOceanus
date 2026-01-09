/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianLockFactory;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeySetLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.cert.GordianCoreCertificate;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianBaseKeyStore.GordianKeyStoreCertificateKey;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    class GordianKeyStoreCertificateElement
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
                                          final LocalDate pDate) {
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
        GordianCoreKeyStoreCertificate buildEntry(final GordianBaseKeyStore pKeyStore) {
            final GordianCoreCertificate myCert = (GordianCoreCertificate) pKeyStore.getCertificate(getCertificateKey());
            return new GordianCoreKeyStoreCertificate(myCert, getCreationDate());
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
            final GordianKeyStoreCertificateElement myThat = (GordianKeyStoreCertificateElement) pThat;

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
     * KeyStore KeyPair Element.
     */
    class GordianKeyStorePairElement
            extends GordianCoreKeyStoreEntry
            implements GordianKeyStoreCertificateHolder {
        /**
         * The securedPrivateKey.
         */
        private final byte[] theSecuredKey;

        /**
         * The securing lock.
         */
        private final GordianKeyStoreLockElement theSecuringLock;

        /**
         * The certificate chain.
         */
        private final List<GordianKeyStoreCertificateKey> theChain;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the passwordLockSpec
         * @param pKeyPair the keyPair
         * @param pPassword the securing password.
         * @param pChain the certificate chain.
         * @throws GordianException on error
         */
        GordianKeyStorePairElement(final GordianFactory pFactory,
                                   final GordianPasswordLockSpec pSpec,
                                   final GordianKeyPair pKeyPair,
                                   final char[] pPassword,
                                   final List<GordianCertificate> pChain) throws GordianException {
            /* Create a securing lock */
            final GordianLockFactory myFactory = pFactory.getLockFactory();
            final GordianKeySetLock myLock = myFactory.newKeySetLock(pSpec, pPassword);
            theSecuringLock = new GordianKeyStoreLockElement(myLock);
            final GordianKeySet myKeySet = myLock.getKeySet();

            /* Secure the privateKey */
            theSecuredKey = securePrivateKey(myKeySet, pKeyPair);

            /* Create the chain */
            theChain = new ArrayList<>();
            for (GordianCertificate myCert : pChain) {
                theChain.add(new GordianKeyStoreCertificateKey(myCert));
            }
        }

        /**
         * Constructor.
         * @param pSecuredKey the secured privateKey
         * @param pSecuringLock the securing keySetLock.
         * @param pChain the certificate chain.
         * @param pDate the creation date
         */
        GordianKeyStorePairElement(final byte[] pSecuredKey,
                                   final byte[] pSecuringLock,
                                   final List<GordianKeyStoreCertificateKey> pChain,
                                   final LocalDate pDate) {
            /* Store details */
            super(pDate);
            theSecuredKey = pSecuredKey;
            theSecuringLock = new GordianKeyStoreLockElement(pSecuringLock, pDate);
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
         * Obtain the securingLock.
         * @return the securingLock
         */
        GordianKeyStoreLockElement getSecuringLock() {
            return theSecuringLock;
        }

        /**
         * Obtain the securingLockBytes.
         * @return the securingLockBytes
         */
        byte[] getSecuringLockBytes() {
            return theSecuringLock.getLock();
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
         * Update the chain.
         * @param pChain the new chain.
         */
        void updateChain(final List<GordianCertificate> pChain) {
            theChain.clear();
            for (GordianCertificate myCert : pChain) {
                theChain.add(new GordianKeyStoreCertificateKey(myCert));
            }
        }

        /**
         * Obtain the secured privateKey.
         * @param pKeySet the keySet
         * @param pKeyPair the keyPair
         * @return the securedPrivateKey
         * @throws GordianException on error
         */
        byte[] securePrivateKey(final GordianKeySet pKeySet,
                                final GordianKeyPair pKeyPair) throws GordianException {
            return pKeySet.securePrivateKey(pKeyPair);
        }

        /**
         * Build the corresponding certificate chain.
         * @param pKeyStore the keyStore
         * @return the certificate chain
         */
        List<GordianCertificate> buildChain(final GordianBaseKeyStore pKeyStore) {
            /* Create the chain */
            final List<GordianKeyStoreCertificateKey> myKeys = getCertificateChain();
            final List<GordianCertificate> myChain = new ArrayList<>();
            for (GordianKeyStoreCertificateKey myKey : myKeys) {
                myChain.add(pKeyStore.getCertificate(myKey));
            }
            return myChain;
        }

        /**
         * Build the corresponding keyStoreEntry.
         * @param pKeyStore the keyStore
         * @param pPassword the password
         * @return the keyStorePair entry
         * @throws GordianException on error
         */
        GordianCoreKeyStorePair buildEntry(final GordianBaseKeyStore pKeyStore,
                                           final char[] pPassword) throws GordianException {
            /* Create the chain */
            final List<GordianCertificate> myChain = buildChain(pKeyStore);

            /* Resolve securing lock */
            final GordianKeySetLock myHash = getSecuringLock().buildEntry(pKeyStore, pPassword);

            /* derive the keyPair */
            final GordianKeySet myKeySet = myHash.getKeySet();
            final GordianCoreCertificate myCert = (GordianCoreCertificate) myChain.get(0);
            final GordianKeyPair myPair = myKeySet.deriveKeyPair(myCert.getX509KeySpec(), getSecuredKey());

            /* Create the entry */
            return new GordianCoreKeyStorePair(myPair, myChain, getCreationDate());
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
            if (!(pThat instanceof GordianKeyStorePairElement)) {
                return false;
            }
            final GordianKeyStorePairElement myThat = (GordianKeyStorePairElement) pThat;

            /* Check that the hashes match */
            return Arrays.equals(theSecuredKey, myThat.getSecuredKey())
                    && Arrays.equals(getSecuringLockBytes(), myThat.getSecuringLockBytes())
                    && theChain.equals(myThat.getCertificateChain())
                    && super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(theSecuredKey)
                    + Arrays.hashCode(getSecuringLockBytes())
                    + theChain.hashCode()
                    + super.hashCode();
        }
    }

    /**
     * KeyStore lock Element.
     */
    class GordianKeyStoreLockElement
            extends GordianCoreKeyStoreEntry {
        /**
         * The hash.
         */
        private final byte[] theLock;

        /**
         * Constructor.
         * @param pLock the lock.
         */
        GordianKeyStoreLockElement(final GordianKeySetLock pLock) {
            /* Store details */
            theLock = pLock.getLockBytes();
        }

        /**
         * Constructor.
         * @param pLock the lock.
         * @param pDate the creation date
         */
        GordianKeyStoreLockElement(final byte[] pLock,
                                   final LocalDate pDate) {
            /* Store details */
            super(pDate);
            theLock = pLock;
        }

        /**
         * Obtain the hash.
         * @return the hash
         */
        byte[] getLock()  {
            return theLock;
        }

        /**
         * Obtain the corresponding keyStoreEntry.
         * @param pKeyStore the keyStore
         * @param pPassword the password
         * @return the keyStore certificate entry
         * @throws GordianException on error
         */
        GordianKeySetLock buildEntry(final GordianBaseKeyStore pKeyStore,
                                     final char[] pPassword) throws GordianException {
            /* Resolve the hash */
            final GordianLockFactory myFactory = pKeyStore.getFactory().getLockFactory();
            return myFactory.resolveKeySetLock(theLock, pPassword);
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
            if (!(pThat instanceof GordianKeyStoreLockElement)) {
                return false;
            }
            final GordianKeyStoreLockElement myThat = (GordianKeyStoreLockElement) pThat;

            /* Check that the hashes match */
            return Arrays.equals(theLock, myThat.getLock())
                    && super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(theLock)
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
         * The securing lock.
         */
        private final GordianKeyStoreLockElement theSecuringLock;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the passwordLockSpec
         * @param pKey the key
         * @param pPassword the securing password.
         * @throws GordianException on error
         */
        GordianKeyStoreKeyElement(final GordianFactory pFactory,
                                  final GordianPasswordLockSpec pSpec,
                                  final GordianKey<T> pKey,
                                  final char[] pPassword) throws GordianException {
            /* Create a securing lock */
            final GordianLockFactory myFactory = pFactory.getLockFactory();
            final GordianKeySetLock myLock = myFactory.newKeySetLock(pSpec, pPassword);
            final GordianKeySet myKeySet = myLock.getKeySet();

            /* Store details */
            theKeyType = pKey.getKeyType();
            theSecuredKey = myKeySet.secureKey(pKey);
            theSecuringLock = new GordianKeyStoreLockElement(myLock);
        }

        /**
         * Constructor.
         * @param pKeyType the keyType
         * @param pSecuredKey the key
         * @param pSecuringLock the securing lock.
         * @param pDate the creation date
         */
        GordianKeyStoreKeyElement(final T pKeyType,
                                  final byte[] pSecuredKey,
                                  final byte[] pSecuringLock,
                                  final LocalDate pDate) {
            /* Store details */
            super(pDate);
            theKeyType = pKeyType;
            theSecuredKey = pSecuredKey;
            theSecuringLock = new GordianKeyStoreLockElement(pSecuringLock, pDate);
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
         * Obtain the securingLock.
         * @return the securingLock
         */
        GordianKeyStoreLockElement getSecuringLock() {
            return theSecuringLock;
        }

        /**
         * Obtain the securingLockBytes.
         * @return the securingLockBytes
         */
        byte[] getSecuringLockBytes() {
            return theSecuringLock.getLock();
        }

        /**
         * Obtain the corresponding keyStoreEntry.
         * @param pKeyStore the keyStore
         * @param pPassword the password
         * @return the keyStore certificate entry
         * @throws GordianException on error
         */
        GordianCoreKeyStoreKey<T> buildEntry(final GordianBaseKeyStore pKeyStore,
                                             final char[] pPassword) throws GordianException {
            /* Resolve securing lock */
            final GordianKeySetLock myLock = theSecuringLock.buildEntry(pKeyStore, pPassword);

            /* derive the key */
            final GordianKeySet myKeySet = myLock.getKeySet();
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
                    && Arrays.equals(getSecuringLockBytes(), myThat.getSecuringLockBytes())
                    && super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return theKeyType.hashCode()
                    + Arrays.hashCode(theSecuredKey)
                    + Arrays.hashCode(getSecuringLockBytes())
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
         * The securing lock.
         */
        private final GordianKeyStoreLockElement theSecuringLock;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the keySetHashSpec
         * @param pKeySet the keySet
         * @param pPassword the securing password.
         * @throws GordianException on error
         */
        GordianKeyStoreSetElement(final GordianFactory pFactory,
                                  final GordianPasswordLockSpec pSpec,
                                  final GordianKeySet pKeySet,
                                  final char[] pPassword) throws GordianException {
            /* Create a securing hash */
            final GordianLockFactory myFactory = pFactory.getLockFactory();
            final GordianKeySetLock myLock = myFactory.newKeySetLock(pSpec, pPassword);
            final GordianKeySet myKeySet = myLock.getKeySet();
            theSecuringLock = new GordianKeyStoreLockElement(myLock);

            /* Secure the keySet */
            theSecuredKeySet = myKeySet.secureKeySet(pKeySet);
       }

        /**
         * Constructor.
         * @param pSecuredKeySet the securedKeySet
         * @param pSecuringLock the securing lock.
         * @param pDate the creation date
         */
        GordianKeyStoreSetElement(final byte[] pSecuredKeySet,
                                  final byte[] pSecuringLock,
                                  final LocalDate pDate) {
            /* Store details */
            super(pDate);
            theSecuredKeySet = pSecuredKeySet;
            theSecuringLock = new GordianKeyStoreLockElement(pSecuringLock, pDate);
        }

        /**
         * Obtain the keyType.
         * @return the keyType
         */
        byte[] getSecuredKeySet() {
            return theSecuredKeySet;
        }

        /**
         * Obtain the securingLock.
         * @return the securingLock
         */
        GordianKeyStoreLockElement getSecuringLock() {
            return theSecuringLock;
        }

        /**
         * Obtain the securingLockBytes.
         * @return the securingLockBytes
         */
        byte[] getSecuringLockBytes() {
            return theSecuringLock.getLock();
        }

        /**
         * Obtain the corresponding keyStoreEntry.
         * @param pKeyStore the keyStore
         * @param pPassword the password
         * @return the keyStore certificate entry
         * @throws GordianException on error
         */
        GordianCoreKeyStoreSet buildEntry(final GordianBaseKeyStore pKeyStore,
                                          final char[] pPassword) throws GordianException {
            /* Resolve the lock */
            final GordianKeySetLock myLock = theSecuringLock.buildEntry(pKeyStore, pPassword);
            final GordianKeySet mySecuringKeySet = myLock.getKeySet();

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
                    && Arrays.equals(getSecuringLockBytes(), myThat.getSecuringLockBytes())
                    && super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(theSecuredKeySet)
                    + Arrays.hashCode(getSecuringLockBytes())
                    + super.hashCode();
        }
    }
}
