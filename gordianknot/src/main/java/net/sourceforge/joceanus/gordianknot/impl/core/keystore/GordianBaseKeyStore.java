/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificateId;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStore;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;

import java.util.Map;

/**
 * keyStore base.
 */
public interface GordianBaseKeyStore
    extends GordianKeyStore {
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
        GordianKeyStoreCertificateKey(final GordianCertificate pCertificate) {
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
     * Obtain the factory.
     * @return the factory
     */
    GordianBaseFactory getFactory();

    /**
     * Obtain the passwordLockSpec.
     * @return the passwordLockSpec
     */
    GordianPasswordLockSpec getPasswordLockSpec();

    /**
     * Obtain the subjectMapOfMaps.
     * @return the map
     */
    Map<GordianCertificateId, Map<GordianCertificateId, GordianCertificate>> getSubjectMapOfMaps();

    /**
     * Obtain the issuerMapofMaps.
     * @return the map
     */
    Map<String, GordianKeyStoreEntry> getAliasMap();

    /**
     * Obtain the certificate.
     * @param pKey the key of the certificate
     * @return the certificate
     */
    GordianCertificate getCertificate(GordianKeyStoreCertificateKey pKey);

    /**
     * find the alias for a keyPair(Set) entry for issuer/serial#.
     * @param pIssuer the issuer
     * @return the alias if found
     * @throws GordianException on error
     */
    String findIssuerCert(IssuerAndSerialNumber pIssuer) throws GordianException;

    /**
     * Store certificate.
     * @param pCertificate the certificate
     */
    void storeCertificate(GordianCertificate pCertificate);
}
