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

import java.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Core KeyStoreEntry.
 * <p>
 * These are the entries as seen by the user.
 * </p>
 */
class GordianCoreKeyStoreEntry
        implements GordianKeyStoreEntry {
    /**
     * The Creation Date.
     */
    private final TethysDate theDate;

    /**
     * Constructor.
     */
    GordianCoreKeyStoreEntry() {

        this(new TethysDate());
    }

    /**
     * Constructor.
     * @param pDate the creation date
     */
    GordianCoreKeyStoreEntry(final TethysDate pDate) {
        theDate = pDate;
    }

    @Override
    public TethysDate getCreationDate() {
        return theDate;
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
        if (!(pThat instanceof GordianCoreKeyStoreEntry)) {
            return false;
        }
        final GordianCoreKeyStoreEntry myThat = (GordianCoreKeyStoreEntry) pThat;

        /* Check that the hashes match */
        return theDate.equals(myThat.getCreationDate());
    }

    @Override
    public int hashCode() {
        return theDate.hashCode();
    }

    /**
     * KeyStore Certificate.
     * @param <S> the signatureSpec type
     * @param <K> the keyPair type
     */
    static class GordianCoreKeyStoreCertificate<S, K>
            extends GordianCoreKeyStoreEntry
            implements GordianKeyStoreCertificate<K> {
        /**
         * The Certificate.
         */
        private final GordianCoreCertificate<S, K> theCertificate;

        /**
         * Constructor.
         * @param pCertificate the certificate
         * @param pDate the creation date
         */
        GordianCoreKeyStoreCertificate(final GordianCoreCertificate<S, K> pCertificate,
                                       final TethysDate pDate) {
            super(pDate);
            theCertificate = pCertificate;
        }

        @Override
        public GordianCoreCertificate<S, K> getCertificate() {
            return theCertificate;
        }
    }

    /**
     * KeyStorePair Certificate.
     */
    static class GordianCoreKeyStorePairCertificate
            extends GordianCoreKeyStoreCertificate<GordianSignatureSpec, GordianKeyPair>
            implements GordianKeyStorePairCertificate {
        /**
         * Constructor.
         * @param pCertificate the certificate
         * @param pDate the creation date
         */
        GordianCoreKeyStorePairCertificate(final GordianCoreKeyPairCertificate pCertificate,
                                           final TethysDate pDate) {
            super(pCertificate, pDate);
        }

        @Override
        public GordianCoreKeyPairCertificate getCertificate() {
            return (GordianCoreKeyPairCertificate) super.getCertificate();
        }
    }

    /**
     * KeyStorePairSet Certificate.
     */
    static class GordianCoreKeyStorePairSetCertificate
            extends GordianCoreKeyStoreCertificate<GordianKeyPairSetSpec, GordianKeyPairSet>
            implements GordianKeyStorePairSetCertificate {
        /**
         * Constructor.
         * @param pCertificate the certificate
         * @param pDate the creation date
         */
        GordianCoreKeyStorePairSetCertificate(final GordianCoreKeyPairSetCertificate pCertificate,
                                              final TethysDate pDate) {
            super(pCertificate, pDate);
        }

        @Override
        public GordianCoreKeyPairSetCertificate getCertificate() {
            return (GordianCoreKeyPairSetCertificate) super.getCertificate();
        }
    }

    /**
     * KeyStore Pair.
     * @param <S> the signatureSpec type
     * @param <K> the keyPair type
     */
    static class GordianCoreKeyStorePairEntry<S, K>
            extends GordianCoreKeyStoreEntry
            implements GordianKeyStorePairEntry<K> {
        /**
         * The KeyPair.
         */
        private final K theKeyPair;

        /**
         * The CertificateChain.
         */
        private final GordianCoreCertificate<S, K>[] theChain;

        /**
         * Constructor.
         * @param pKeyPair the keyPair.
         * @param pChain the matching certificateChain
         * @param pDate the creation date
         */
        GordianCoreKeyStorePairEntry(final K pKeyPair,
                                     final GordianCoreCertificate<S, K>[] pChain,
                                     final TethysDate pDate) {
            super(pDate);
            theKeyPair = pKeyPair;
            theChain = Arrays.copyOf(pChain, pChain.length);
        }

        @Override
        public K getKeyPair() {
            return theKeyPair;
        }

        @Override
        public GordianCoreCertificate<S, K>[] getCertificateChain() {
            return Arrays.copyOf(theChain, theChain.length);
        }
    }

    /**
     * KeyStore KeyPair.
     */
    static class GordianCoreKeyStorePair
            extends GordianCoreKeyStorePairEntry<GordianSignatureSpec, GordianKeyPair>
            implements GordianKeyStorePair {
        /**
         * Constructor.
         * @param pKeyPair the keyPair.
         * @param pChain the matching certificateChain
         * @param pDate the creation date
         */
        GordianCoreKeyStorePair(final GordianKeyPair pKeyPair,
                                final GordianCoreKeyPairCertificate[] pChain,
                                final TethysDate pDate) {
            super(pKeyPair, pChain, pDate);
        }

        @Override
        public GordianCoreKeyPairCertificate[] getCertificateChain() {
            return (GordianCoreKeyPairCertificate[]) super.getCertificateChain();
        }
    }

    /**
     * KeyStore KeyPairSet.
     */
    static class GordianCoreKeyStorePairSet
            extends GordianCoreKeyStorePairEntry<GordianKeyPairSetSpec, GordianKeyPairSet>
            implements GordianKeyStorePairSet {
        /**
         * Constructor.
         * @param pKeyPairSet the keyPairSet.
         * @param pChain the matching certificateChain
         * @param pDate the creation date
         */
        GordianCoreKeyStorePairSet(final GordianKeyPairSet pKeyPairSet,
                                   final GordianCoreKeyPairSetCertificate[] pChain,
                                   final TethysDate pDate) {
            super(pKeyPairSet, pChain, pDate);
        }

        @Override
        public GordianCoreKeyPairSetCertificate[] getCertificateChain() {
            return (GordianCoreKeyPairSetCertificate[]) super.getCertificateChain();
        }
    }

    /**
     * KeyStore KeyEntry.
     * @param <T> the key type
     */
    public static class GordianCoreKeyStoreKey<T extends GordianKeySpec>
            extends GordianCoreKeyStoreEntry
            implements GordianKeyStoreKey<T> {
        /**
         * The Key.
         */
        private final GordianKey<T> theKey;

        /**
         * Constructor.
         * @param pKey the key
         * @param pDate the creation date
         */
        GordianCoreKeyStoreKey(final GordianKey<T> pKey,
                               final TethysDate pDate) {
            super(pDate);
            theKey = pKey;
        }

        @Override
        public GordianKey<T> getKey() {
            return theKey;
        }
    }


    /**
     * KeyStore KeySet.
     */
    static class GordianCoreKeyStoreSet
            extends GordianCoreKeyStoreEntry
            implements GordianKeyStoreSet {
        /**
         * The KeySet.
         */
        private final GordianKeySet theKeySet;

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pDate the creation date
         */
        GordianCoreKeyStoreSet(final GordianKeySet pKeySet,
                               final TethysDate pDate) {
            super(pDate);
            theKeySet = pKeySet;
        }

        /**
         * Obtain the keySetHash.
         * @return the keySetHash
         */
        public GordianKeySet getKeySet() {
            return theKeySet;
        }
    }

    /**
     * KeyStore KeySetHash.
     */
    static class GordianCoreKeyStoreHash
            extends GordianCoreKeyStoreEntry
            implements GordianKeyStoreHash {
        /**
         * The KeySetHash.
         */
        private final GordianKeySetHash theHash;

        /**
         * Constructor.
         * @param pHash the keySetHash
         * @param pDate the creation date
         */
        GordianCoreKeyStoreHash(final GordianKeySetHash pHash,
                                final TethysDate pDate) {
            super(pDate);
            theHash = pHash;
        }

        @Override
        public GordianKeySetHash getKeySetHash() {
            return theHash;
        }
    }
}
