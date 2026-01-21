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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamCipher;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianSymCipher;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keyset.GordianKeySet;

/**
 * Key Agreement Specification.
 */
public interface GordianAgreement {
    /**
     * Obtain the agreementParameters.
     *
     * @return the parameters
     */
    GordianAgreementParams getAgreementParams();

    /**
     * Obtain agreement status.
     *
     * @return the agreement state
     */
    GordianAgreementStatus getStatus();

    /**
     * Obtain result.
     *
     * @return the result which can be any of
     * <dl>
     *     <dt>GordianFactory</dt><dd>If a factory was agreed</dd>
     *     <dt>GordianSymCipher[2]</dt><dd>If a pair of symCiphers was agreed</dd>
     *     <dt>GordianStreamCipher[2]</dt><dd>If a pair of streamCiphers was agreed</dd>
     *     <dt>GordianKeySet</dt><dd>If a keySet was agreed</dd>
     *     <dt>byte[]</dt><dd>If a defined length byte array was agreed</dd>
     *     <dt>GordianException</dt><dd>If the agreement was rejected.</dd>
     * </dl>
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
    void updateParams(GordianAgreementParams pParams) throws GordianException;

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
