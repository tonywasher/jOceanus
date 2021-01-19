/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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

import java.util.Date;

/**
 * Certificate API.
 * @param <K> keyPair type
 */
public interface GordianCertificate<K> {
    /**
     * Obtain the subject of the certificate.
     *
     * @return the subject
     */
    GordianCertificateId getSubject();

    /**
     * Obtain the issuer of the certificate.
     *
     * @return the issuer name
     */
    GordianCertificateId getIssuer();

    /**
     * Obtain the keyPair of the certificate.
     *
     * @return the keyPair
     */
    K getKeyPair();

    /**
     * Obtain the encoded representation of the certificate.
     * @return the encoded representation
     */
    byte[] getEncoded();

    /**
     * Is the certificate valid at this moment?
     *
     * @return true/false
     */
    default boolean isValidNow() {
        return isValidOnDate(new Date(System.currentTimeMillis()));
    }

    /**
     * Is the certificate valid on the specified date?
     *
     * @param pDate the date to test
     * @return true/false
     */
    boolean isValidOnDate(Date pDate);

    /**
     * Is this certificate self-signed?
     * @return true/false
     */
    boolean isSelfSigned();

    /**
     * Obtain the keyPair usage?
     *
     * @return the usage
     */
    GordianKeyPairUsage getUsage();
}
