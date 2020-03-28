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
package net.sourceforge.joceanus.jgordianknot.api.agree;

import net.sourceforge.joceanus.jtethys.OceanusException;

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
     *     <dt>FactoryType</dt>
     *     <dd>To agree a Factory</dd>
     *     <dt>SymCipherSpec</dt>
     *     <dd>To agree a symCipher</dd>
     *     <dt>StreamCipherSpec</dt>
     *     <dd>To agree a streamCipher</dd>
     *     <dt>KeySetSpec</dt>
     *     <dd>To agree a KeySet</dd>
     * </dl>
     * @param pResultType the resultType.
     * @throws OceanusException on error
     */
    void setResultType(Object pResultType) throws OceanusException;

    /**
     * Obtain resultType.
     * @return the resultType
     */
    Object getResultType();

    /**
     * Obtain result.
     * @return the result
     * @throws OceanusException on error
     */
    Object getResult() throws OceanusException;
}

