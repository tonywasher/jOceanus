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

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificate;
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
    }
    /**
     * KeyStore KeyPair.
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
        private final GordianCertificate[] theChain;

        /**
         * Constructor.
         * @param pKeyPair the keyPair.
         * @param pChain the matching certificateChain
         * @param pDate the creation date
         */
        GordianCoreKeyStorePair(final GordianKeyPair pKeyPair,
                                final GordianCoreCertificate[] pChain,
                                final TethysDate pDate) {
            super(pDate);
            theKeyPair = pKeyPair;
            theChain = Arrays.copyOf(pChain, pChain.length);
        }

        @Override
        public GordianKeyPair getKeyPair() {
            return theKeyPair;
        }

        @Override
        public GordianCertificate[] getCertificateChain() {
            return Arrays.copyOf(theChain, theChain.length);
        }
    }

    /**
     * KeyStore KeyEntry.
     * @param <T> the key type
     */
    public static class GordianCoreKeyStoreKey<T extends GordianKeySpec>
            extends GordianCoreKeyStoreEntry
            implements GordianKeyStoreKey {
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
