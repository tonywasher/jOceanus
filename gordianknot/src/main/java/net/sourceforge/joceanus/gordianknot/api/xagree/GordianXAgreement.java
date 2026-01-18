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
package net.sourceforge.joceanus.gordianknot.api.xagree;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;

/**
 * Key Agreement Specification.
 */
public interface GordianXAgreement {
    /**
     * Obtain the agreementParameters.
     *
     * @return the parameters
     */
    GordianXAgreementParams getAgreementParams();

    /**
     * Obtain agreement status.
     *
     * @return the agreement state
     */
    GordianXAgreementStatus getStatus();

    /**
     * Obtain result.
     *
     * @return the result which may be any of
     * <table>
     *     <tr><td>GordianFactory</td><td>If a factory was agreed</td></tr>
     *     <tr><td>GordianSymCipher[2]</td><td>If a pair of symCiphers was agreed</td></tr>
     *     <tr><td>GordianStreamCipher[2]</td><td>If a pair of streamCiphers was agreed</td></tr>
     *     <tr><td>GordianKeySet</td><td>If a keySet was agreed</td></tr>
     *     <tr><td>byte[]</td><td>If a defined length byte array was agreed</td></tr>
     *     <tr><td>GordianException</td><td>If the agreement was rejected.</td></tr>
     * </table>
     * @throws GordianException on error
     */
    Object getResult() throws GordianException;

    /**
     * Obtain factory result.
     *
     * @return the result if it is available as a factory, otherwise null
     */
    GordianFactory getFactoryResult();

    /**
     * Obtain keySet result.
     *
     * @return the result if it is available as a keySet, otherwise null
     */
    GordianKeySet getKeySetResult();

    /**
     * Obtain symCipherPair result.
     *
     * @return the result if it is available as a symCipherPair, otherwise null
     */
    GordianSymCipher[] getSymCipherPairResult();

    /**
     * Obtain streamCipherPair result.
     *
     * @return the result if it is available as a streamCipherPair, otherwise null
     */
    GordianStreamCipher[] getStreamCipherPairResult();

    /**
     * Obtain byteArray result.
     *
     * @return the result if it is available as a byteArray, otherwise null
     */
    byte[] getByteArrayResult();

    /**
     * Obtain Rejection result.
     *
     * @return the result if it is available as an exception, otherwise null
     */
    GordianException getRejectionResult();

    /**
     * Update parameters.
     *
     * @param pParams the updated parameters
     * @throws GordianException on error
     */
    void updateParams(GordianXAgreementParams pParams) throws GordianException;

    /**
     * Reject the agreement with error message.
     *
     * @param pError the error
     * @throws GordianException on error
     */
    void setError(String pError) throws GordianException;

    /**
     * Is the agreement rejected?.
     *
     * @return true/false
     */
    boolean isRejected();

    /**
     * Obtain the next message.
     *
     * @return the next message (if any)
     */
    byte[] nextMessage();
}
