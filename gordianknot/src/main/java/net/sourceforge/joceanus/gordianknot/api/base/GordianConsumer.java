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
package net.sourceforge.joceanus.gordianknot.api.base;

/**
 * GordianKnot interface for Consumers such as Message Digests, Macs and Signatures.
 */
public interface GordianConsumer {
    /**
     * Update the consumer with a portion of a byte array.
     * @param pBytes the bytes to update with.
     * @param pOffset the offset of the data within the byte array
     * @param pLength the length of the data to use
     */
    void update(byte[] pBytes,
                int pOffset,
                int pLength);

    /**
     * Update the consumer with a single byte.
     * @param pByte the byte to update with.
     */
    void update(byte pByte);

    /**
     * Update the consumer with a byte array.
     * @param pBytes the bytes to update with.
     */
    default void update(final byte[] pBytes)  {
        update(pBytes, 0, pBytes == null ? 0 : pBytes.length);
    }

    /**
     * Reset the Consumer.
     */
    void reset();
}
