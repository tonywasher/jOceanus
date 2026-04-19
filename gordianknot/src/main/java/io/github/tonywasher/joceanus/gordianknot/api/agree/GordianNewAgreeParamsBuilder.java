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
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertificate;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;

/**
 * Key Agreement Parameters Builder.
 */
public interface GordianNewAgreeParamsBuilder {
    /**
     * Specify agreementSpec and resultType.
     *
     * @param pSpec       the agreementSpec
     * @param pResultType the result type
     *                    <dl>
     *                        <dt>GordianFactoryType</dt><dd>To agree a Factory</dd>
     *                        <dt>GordianSymCipherSpec</dt><dd>To agree a symCipher pair</dd>
     *                        <dt>GordianStreamCipherSpec</dt><dd>To agree a streamCipher pair</dd>
     *                        <dt>GordianKeySetSpec</dt><dd>To agree a KeySet</dd>
     *                        <dt>Integer</dt><dd>To agree a defined length byte array</dd>
     *                    </dl>
     * @return the Params
     * @throws GordianException on error
     */
    GordianNewAgreeParamsBuilder withSpecAndResultType(GordianAgreementSpec pSpec,
                                                       Object pResultType) throws GordianException;

    /**
     * Based on agreementParams.
     *
     * @param pBase the base agreement parameters
     * @return the Builder
     */
    GordianNewAgreeParamsBuilder withBaseParams(GordianNewAgreeParams pBase);

    /**
     * Set client Certificate.
     *
     * @param pClient the client certificate
     * @return the builder
     * @throws GordianException on error
     */
    GordianNewAgreeParamsBuilder setClientCertificate(GordianCertificate pClient) throws GordianException;

    /**
     * Set server Certificate.
     *
     * @param pServer the server certificate
     * @return the builder
     * @throws GordianException on error
     */
    GordianNewAgreeParamsBuilder setServerCertificate(GordianCertificate pServer) throws GordianException;

    /**
     * Declare signer certificate.
     *
     * @param pSigner the certificate
     * @return the builder
     * @throws GordianException on error
     */
    GordianNewAgreeParamsBuilder setSigner(GordianCertificate pSigner) throws GordianException;

    /**
     * Declare signer certificate and specification.
     *
     * @param pSigner   the certificate
     * @param pSignSpec the signSpec
     * @return the builder
     * @throws GordianException on error
     */
    GordianNewAgreeParamsBuilder setSigner(GordianCertificate pSigner,
                                           GordianSignatureSpec pSignSpec) throws GordianException;

    /**
     * Set additional data.
     *
     * @param pData the additional data
     * @return the builder
     * @throws GordianException on error
     */
    GordianNewAgreeParamsBuilder setAdditionalData(byte[] pData) throws GordianException;

    /**
     * Build the parameters.
     *
     * @return the parameters
     * @throws GordianException on error
     */
    GordianNewAgreeParams build() throws GordianException;
}
