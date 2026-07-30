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

package io.github.tonywasher.joceanus.gordianknot.impl.jca.base;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianDestroyable;
import org.bouncycastle.util.Arrays;

import javax.crypto.SecretKey;

/**
 * Secret Key Destroyable implementation.
 */
public class JcaSecretKey
        implements SecretKey, GordianDestroyable {
    /**
     * The algorithm.
     */
    private final String theAlgorithm;

    /**
     * The key.
     */
    private final byte[] theKey;

    /**
     * Is the key destroyed?
     */
    private boolean isDestroyed;

    /**
     * Constructor.
     *
     * @param pAlgorithm the algorithm
     * @param pKeyBytes  the key bytes
     */
    JcaSecretKey(final byte[] pKeyBytes,
                 final String pAlgorithm) {
        /* Store details */
        theAlgorithm = pAlgorithm;
        theKey = Arrays.clone(pKeyBytes);
    }

    @Override
    public String getAlgorithm() {
        return theAlgorithm;
    }

    @Override
    public String getFormat() {
        return "RAW";
    }

    @Override
    public byte[] getEncoded() {
        return Arrays.clone(theKey);
    }

    @Override
    public boolean isClearable() {
        return true;
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    @Override
    public void destroy() {
        if (!isDestroyed()) {
            Arrays.fill(theKey, (byte) 0);
        }
        isDestroyed = true;
    }
}
