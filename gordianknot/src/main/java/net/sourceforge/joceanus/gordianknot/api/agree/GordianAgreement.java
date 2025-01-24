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
package net.sourceforge.joceanus.gordianknot.api.agree;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;

/**
 * Key Agreement Specification.
 */
public interface GordianAgreement {
    /**
     * Obtain the agreementSpec.
     * @return the spec
     */
    GordianAgreementSpec getAgreementSpec();

    /**
     * Set the result type of the agreement.
     * <p>
     *     This can be any of
     * </p>
     * <dl>
     *     <dt>GordianFactoryType</dt>
     *     <dd>To agree a Factory</dd>
     *     <dt>GordianSymCipherSpec</dt>
     *     <dd>To agree a symCipher</dd>
     *     <dt>GordianStreamCipherSpec</dt>
     *     <dd>To agree a streamCipher</dd>
     *     <dt>GordianKeySetSpec</dt>
     *     <dd>To agree a KeySet</dd>
     *     <dt>Integer</dt>
     *     <dd>To agree a defined length byte array</dd>
     *     <dt>null</dt>
     *     <dd>To obtain the raw agreement</dd>
     * </dl>
     * @param pResultType the resultType.
     * @throws GordianException on error
     */
    void setResultType(Object pResultType) throws GordianException;

    /**
     * Obtain resultType.
     * @return the resultType
     */
    Object getResultType();

    /**
     * Obtain result.
     * @return the result
     * @throws GordianException on error
     */
    Object getResult() throws GordianException;

    /**
     * Obtain agreement status.
     * @return the agreement state
     */
    GordianAgreementStatus getStatus();

    /**
     * Reset agreement.
     */
    void reset();
}
