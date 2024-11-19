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
import java.util.List;

import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.tethys.date.TethysDate;

/**
 * Core KeyStoreEntry.
 * <p>
 * These are the entries as seen by the user.
 * </p>
 */
public class GordianCoreKeyStoreEntry
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

        /* Check that the dates match */
        return theDate.equals(myThat.getCreationDate());
    }

    @Override
    public int hashCode() {
        return theDate.hashCode();
    }

    /**
     * KeyStore Certificate.
     */
    static class GordianCoreKeyStoreCertificate
            extends GordianCoreKeyStoreEntry
            implements GordianKeyStoreCertificate {
        /**
         * The Certificate.
         */
        private final GordianCoreCertificate theCertificate;

        /**
         * Constructor.
         * @param pCertificate the certificate
         * @param pDate the creation date
         */
        GordianCoreKeyStoreCertificate(final GordianCoreCertificate pCertificate,
                                       final TethysDate pDate) {
            super(pDate);
            theCertificate = pCertificate;
        }

        @Override
        public GordianCoreCertificate getCertificate() {
            return theCertificate;
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
            if (!(pThat instanceof GordianCoreKeyStoreCertificate)) {
                return false;
            }
            final GordianCoreKeyStoreCertificate myThat = (GordianCoreKeyStoreCertificate) pThat;

            /* Check that the certificates match */
            return theCertificate.equals(myThat.getCertificate())
                    && super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return theCertificate.hashCode() ^ super.hashCode();
        }
    }

    /**
     * KeyStore KeyPairEntry.
     */
    static class GordianCoreKeyStorePair
            extends GordianCoreKeyStoreEntry
            implements GordianKeyStorePair {
        /**
         * The KeyPair.
         */
        private final GordianKeyPair theKeyPair;

        /**
         * The CertificateChain.
         */
        private final List<GordianCertificate> theChain;

        /**
         * Constructor.
         * @param pKeyPair the keyPair.
         * @param pChain the matching certificateChain
         * @param pDate the creation date
         */
        GordianCoreKeyStorePair(final GordianKeyPair pKeyPair,
                                final List<GordianCertificate> pChain,
                                final TethysDate pDate) {
            super(pDate);
            theKeyPair = pKeyPair;
            theChain = new ArrayList<>(pChain);
        }

        @Override
        public GordianKeyPair getKeyPair() {
            return theKeyPair;
        }

        @Override
        public List<GordianCertificate> getCertificateChain() {
            return theChain;
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
            if (!(pThat instanceof GordianCoreKeyStorePair)) {
                return false;
            }
            final GordianCoreKeyStorePair myThat = (GordianCoreKeyStorePair) pThat;

            /* Check that the keyPairs match */
            return theKeyPair.equals(myThat.getKeyPair())
                    && theChain.equals(myThat.getCertificateChain())
                    && super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return theKeyPair.hashCode()
                    ^ theChain.hashCode()
                    ^ super.hashCode();
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
            if (!(pThat instanceof GordianCoreKeyStoreKey)) {
                return false;
            }
            final GordianCoreKeyStoreKey<?> myThat = (GordianCoreKeyStoreKey<?>) pThat;

            /* Check that the keys match */
            return theKey.equals(myThat.getKey())
                    && super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return theKey.hashCode() ^ super.hashCode();
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

        @Override
        public GordianKeySet getKeySet() {
            return theKeySet;
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
            if (!(pThat instanceof GordianCoreKeyStoreSet)) {
                return false;
            }
            final GordianCoreKeyStoreSet myThat = (GordianCoreKeyStoreSet) pThat;

            /* Check that the certificates match */
            return theKeySet.equals(myThat.getKeySet())
                    && super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return theKeySet.hashCode() ^ super.hashCode();
        }
    }
}
