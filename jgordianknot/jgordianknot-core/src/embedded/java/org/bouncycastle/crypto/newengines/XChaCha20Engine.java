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
package org.bouncycastle.crypto.newengines;

import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.util.Pack;

/**
 * Modelled on XSalsa20Engine.
 */
public class XChaCha20Engine extends ChaChaEngine {

    public String getAlgorithmName()
    {
        return "XChaCha20";
    }

    protected int getNonceSize()
    {
        return 24;
    }

    /**
     * XChaCha key generation: process 256 bit input key and 128 bits of the input nonce
     * using a core ChaCha20 function without input addition to produce 256 bit working key
     * and use that with the remaining 64 bits of nonce to initialize a standard ChaCha20 engine state.
     */
    protected void setKey(byte[] keyBytes, byte[] ivBytes)
    {
        if (keyBytes == null)
        {
            throw new IllegalArgumentException(getAlgorithmName() + " doesn't support re-init with null key");
        }

        if (keyBytes.length != 32)
        {
            throw new IllegalArgumentException(getAlgorithmName() + " requires a 256 bit key");
        }

        // Set key for HChaCha20
        super.setKey(keyBytes, ivBytes);

        // Pack next 64 bits of IV into engine state instead of counter
        Pack.littleEndianToInt(ivBytes, 8, engineState, 12, 2);

        // Process engine state to generate ChaCha20 key
        int[] hChaCha20Out = new int[engineState.length];
        chachaCore(20, engineState, hChaCha20Out);

        // Set new key, removing addition in last round of chachaCore
        engineState[4] = hChaCha20Out[0] - engineState[0];
        engineState[5] = hChaCha20Out[1] - engineState[1];
        engineState[6] = hChaCha20Out[2] - engineState[2];
        engineState[7] = hChaCha20Out[3] - engineState[3];

        engineState[8] = hChaCha20Out[12] - engineState[12];
        engineState[9] = hChaCha20Out[13] - engineState[13];
        engineState[10] = hChaCha20Out[14] - engineState[14];
        engineState[11] = hChaCha20Out[15] - engineState[15];

        // Last 64 bits of input IV
        Pack.littleEndianToInt(ivBytes, 16, engineState, 14, 2);
    }
}
