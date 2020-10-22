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
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry;
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
     * KeyStorePair Certificate.
     */
    static class GordianCoreKeyStorePairCertificate
            extends GordianCoreKeyStoreEntry
            implements GordianKeyStorePairCertificate {
        /**
         * The Certificate.
         */
        private final GordianCoreKeyPairCertificate theCertificate;

        /**
         * Constructor.
         * @param pCertificate the certificate
         * @param pDate the creation date
         */
        GordianCoreKeyStorePairCertificate(final GordianCoreKeyPairCertificate pCertificate,
                                           final TethysDate pDate) {
            super(pDate);
            theCertificate = pCertificate;
        }

        @Override
        public GordianCoreKeyPairCertificate getCertificate() {
            return theCertificate;
        }
    }

    /**
     * KeyStorePairSet Certificate.
     */
    static class GordianCoreKeyStorePairSetCertificate
            extends GordianCoreKeyStoreEntry
            implements GordianKeyStorePairSetCertificate {
        /**
         * The Certificate.
         */
        private final GordianCoreKeyPairSetCertificate theCertificate;

        /**
         * Constructor.
         * @param pCertificate the certificate
         * @param pDate the creation date
         */
        GordianCoreKeyStorePairSetCertificate(final GordianCoreKeyPairSetCertificate pCertificate,
                                              final TethysDate pDate) {
            super(pDate);
            theCertificate = pCertificate;
        }

        @Override
        public GordianCoreKeyPairSetCertificate getCertificate() {
            return theCertificate;
        }
    }

    /**
     * KeyStore KeyPairEntry.
     * @param <C> the certificate type
     * @param <K> the keyPairType
     */
    static class GordianKeyStorePairEntry<C extends GordianCertificate<K>, K>
            extends GordianCoreKeyStoreEntry {
        /**
         * The KeyPair.
         */
        private final K theKeyPair;

        /**
         * The CertificateChain.
         */
        private final List<C> theChain;

        /**
         * Constructor.
         * @param pKeyPair the keyPair.
         * @param pChain the matching certificateChain
         * @param pDate the creation date
         */
        GordianKeyStorePairEntry(final K pKeyPair,
                                 final List<C> pChain,
                                 final TethysDate pDate) {
            super(pDate);
            theKeyPair = pKeyPair;
            theChain = new ArrayList<>(pChain);
        }

        /**
         * Obtain the keyPair.
         * @return the keyPair
         */
        K getPair() {
            return theKeyPair;
        }

        /**
         * Obtain the certificate chain.
         * @return the chain
         */
        public List<C> getCertificateChain() {
            return theChain;
        }
    }

    /**
     * KeyStore KeyPair.
     */
    static class GordianCoreKeyStorePair
            extends GordianKeyStorePairEntry<GordianKeyPairCertificate, GordianKeyPair>
            implements GordianKeyStorePair {
        /**
         * Constructor.
         * @param pKeyPair the keyPair.
         * @param pChain the matching certificateChain
         * @param pDate the creation date
         */
        GordianCoreKeyStorePair(final GordianKeyPair pKeyPair,
                                final List<GordianKeyPairCertificate> pChain,
                                final TethysDate pDate) {
            super(pKeyPair, pChain, pDate);
        }

        @Override
        public GordianKeyPair getKeyPair() {
            return getPair();
        }
    }

    /**
     * KeyStore KeyPairSet.
     */
    static class GordianCoreKeyStorePairSet
            extends GordianKeyStorePairEntry<GordianKeyPairSetCertificate, GordianKeyPairSet>
            implements GordianKeyStorePairSet {
        /**
         * Constructor.
         * @param pKeyPairSet the keyPairSet.
         * @param pChain the matching certificateChain
         * @param pDate the creation date
         */
        GordianCoreKeyStorePairSet(final GordianKeyPairSet pKeyPairSet,
                                   final List<GordianKeyPairSetCertificate> pChain,
                                   final TethysDate pDate) {
            super(pKeyPairSet, pChain, pDate);
        }

        @Override
        public GordianKeyPairSet getKeyPairSet() {
            return getPair();
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
}
