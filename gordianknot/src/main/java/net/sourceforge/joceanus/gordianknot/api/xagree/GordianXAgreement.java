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
package net.sourceforge.joceanus.gordianknot.api.xagree;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;

/**
 * Key Agreement Specification.
 */
public interface GordianXAgreement {
    /**
     * Obtain the agreementSpec.
     * @return the spec
     */
    GordianAgreementSpec getAgreementSpec();

    /**
     * Obtain agreement status.
     * @return the agreement state
     */
    GordianXAgreementStatus getStatus();

    /**
     * Obtain result.
     * @return the result which may be any of
     * <table>
     *     <tr><td>GordianFactory</td><td>If a factory was agreed</td></tr>
     *     <tr><td>GordianSymCipher[2]</td><td>If a pair of symCiphers was agreed</td></tr>
     *     <tr><td>GordianStreamCipher[2]</td><td>If a pair of streamCiphers was agreed</td></tr>
     *     <tr><td>GordianKeySet</td><td>If a keySet was agreed</td></tr>
     *     <tr><td>Integer</td><td>If a defined length byte array was agreed</td></tr>
     *     <tr><td>GordianException</td><td>If the agreement was rejected by the partner</td></tr>
     * </table>
     * @throws GordianException on error
     */
    Object getResult() throws GordianException;

    /**
     * Obtain the clientCertificate.
     * @return the certificate
     */
    GordianCertificate getClientCertificate();

    /**
     * Obtain the serverCertificate.
     * @return the certificate
     */
    GordianCertificate getServerCertificate();

    /**
     * Obtain the signerCertificate.
     * @return the certificate
     */
    GordianCertificate getSignerCertificate();

    /**
     * Set server Certificate to declare privateKey.
     * @param pServer the server certificate
     * @return the agreement
     * @throws GordianException on error
     */
    GordianXAgreement forServer(GordianCertificate pServer) throws GordianException;

    /**
     * Reject the agreement with error message.
     * @param pError the error
     * @throws GordianException on error
     */
    void setError(String pError) throws GordianException;

    /**
     * Obtain the next message.
     * @return the next message (if any)
     */
    byte[] nextMessage();
}
