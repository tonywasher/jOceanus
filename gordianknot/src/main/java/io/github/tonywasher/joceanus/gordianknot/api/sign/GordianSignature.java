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
package io.github.tonywasher.joceanus.gordianknot.api.sign;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;

/**
 * GordianKnot base for signature.
 */
public interface GordianSignature {
    /**
     * Obtain the signatureSpec.
     *
     * @return the Spec
     */
    GordianSignatureSpec getSignatureSpec();

    /**
     * Initialise for signature.
     *
     * @param pParams the parameters
     * @throws GordianException on error
     */
    void initForSigning(GordianSignParams pParams) throws GordianException;

    /**
     * Initialise for verify.
     *
     * @param pParams the parameters
     * @throws GordianException on error
     */
    void initForVerify(GordianSignParams pParams) throws GordianException;

    /**
     * Update the signature with a portion of a byte array.
     *
     * @param pBytes  the bytes to update with.
     * @param pOffset the offset of the data within the byte array
     * @param pLength the length of the data to use
     * @throws GordianException on error
     */
    void update(byte[] pBytes,
                int pOffset,
                int pLength) throws GordianException;

    /**
     * Update the signature with a single byte.
     *
     * @param pByte the byte to update with.
     * @throws GordianException on error
     */
    void update(byte pByte) throws GordianException;

    /**
     * Update the signature with a byte array.
     *
     * @param pBytes the bytes to update with.
     * @throws GordianException on error
     */
    default void update(final byte[] pBytes) throws GordianException {
        update(pBytes, 0, pBytes == null ? 0 : pBytes.length);
    }

    /**
     * Complete the signature operation and return the signature bytes.
     *
     * @return the signature
     * @throws GordianException on error
     */
    byte[] sign() throws GordianException;

    /**
     * Verify the signature against the supplied signature bytes.
     *
     * @param pSignature the supplied signature
     * @return the signature
     * @throws GordianException on error
     */
    boolean verify(byte[] pSignature) throws GordianException;
}
