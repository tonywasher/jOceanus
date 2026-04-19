/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.api.agree;

import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertificate;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;

/**
 * Key Agreement Parameters Specification.
 */
public interface GordianNewAgreeParams {
    /**
     * Obtain the agreementSpec.
     *
     * @return the spec
     */
    GordianAgreementSpec getAgreementSpec();

    /**
     * Obtain the resultType.
     *
     * @return the resultType
     */
    Object getResultType();

    /**
     * Obtain the clientCertificate.
     *
     * @return the certificate
     */
    GordianCertificate getClientCertificate();

    /**
     * Obtain the serverCertificate.
     *
     * @return the certificate
     */
    GordianCertificate getServerCertificate();

    /**
     * Obtain the signerCertificate.
     *
     * @return the certificate
     */
    GordianCertificate getSignerCertificate();

    /**
     * Obtain the signatureSpec.
     *
     * @return the signatureSpec
     */
    GordianSignatureSpec getSignatureSpec();

    /**
     * Obtain the additionalData.
     *
     * @return the additional data
     */
    byte[] getAdditionalData();
}
