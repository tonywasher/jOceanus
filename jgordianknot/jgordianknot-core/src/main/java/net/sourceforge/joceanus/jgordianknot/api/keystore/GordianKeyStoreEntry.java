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
package net.sourceforge.joceanus.jgordianknot.api.keystore;

import java.security.KeyStore.Entry;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * KeyStore Entry API.
 */
public interface GordianKeyStoreEntry
        extends Entry {
    /**
     * Obtain the creation date.
     * @return the creation date
     */
    TethysDate getCreationDate();

    /**
     * KeyStore Certificate API.
     */
    interface GordianKeyStoreCertificate
            extends GordianKeyStoreEntry {
        /**
         * Obtain the certificate.
         * @return the certificate
         */
        GordianCertificate getCertificate();
    }

    /**
     * KeyStore KeyPair.
     */
    interface GordianKeyStorePair
            extends GordianKeyStoreEntry {
        /**
         * Obtain the keyPair.
         * @return the keyPair
         */
        GordianKeyPair getKeyPair();

        /**
         * Obtain the certificate chain.
         * @return the certificate chain
         */
        List<GordianCertificate> getCertificateChain();
    }

    /**
     * KeyStore KeyEntry.
     * @param <T> the key type
     */
    interface GordianKeyStoreKey<T extends GordianKeySpec>
            extends GordianKeyStoreEntry {
        /**
         * Obtain the key.
         * @return the key
         */
        GordianKey<T> getKey();
    }


    /**
     * KeyStore KeySet.
     */
    interface GordianKeyStoreSet
            extends GordianKeyStoreEntry {
        /**
         * Obtain the keySet.
         * @return the keySet
         */
        GordianKeySet getKeySet();
    }
}
