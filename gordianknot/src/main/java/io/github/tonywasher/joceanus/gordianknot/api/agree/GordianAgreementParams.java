/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertificate;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureSpec;

/**
 * Key Agreement Parameters Specification.
 */
public interface GordianAgreementParams {
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

    /**
     * Set client Certificate.
     *
     * @param pClient the client certificate
     * @return the new agreementParams
     * @throws GordianException on error
     */
    GordianAgreementParams setClientCertificate(GordianCertificate pClient) throws GordianException;

    /**
     * Set server Certificate.
     *
     * @param pServer the server certificate
     * @return the new agreementParams
     * @throws GordianException on error
     */
    GordianAgreementParams setServerCertificate(GordianCertificate pServer) throws GordianException;

    /**
     * Declare signer certificate.
     *
     * @param pSigner the certificate
     * @return the new agreementParams
     * @throws GordianException on error
     */
    GordianAgreementParams setSigner(GordianCertificate pSigner) throws GordianException;

    /**
     * Declare signer certificate and specification.
     *
     * @param pSigner   the certificate
     * @param pSignSpec the signSpec
     * @return the new agreementParams
     * @throws GordianException on error
     */
    GordianAgreementParams setSigner(GordianCertificate pSigner,
                                     GordianSignatureSpec pSignSpec) throws GordianException;

    /**
     * Set additional data.
     *
     * @param pData the additional data
     * @return the new agreementParams
     * @throws GordianException on error
     */
    GordianAgreementParams setAdditionalData(byte[] pData) throws GordianException;
}
