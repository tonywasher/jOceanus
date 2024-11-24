/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.ext.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;

/**
 * Blake3 Parameters.
 */
public class GordianBlake3Parameters
        implements CipherParameters {
    /**
     * The key length.
     */
    private static final int KEYLEN = 32;

    /**
     * The key.
     */
    private byte[] theKey;

    /**
     * The context.
     */
    private byte[] theContext;

    /**
     * Create a key parameter.
     * @param pContext the context
     * @return the parameter
     */
    public static GordianBlake3Parameters context(final byte[] pContext) {
        if (pContext == null) {
            throw new IllegalArgumentException("Invalid context");
        }
        final GordianBlake3Parameters myParams = new GordianBlake3Parameters();
        myParams.theContext = Arrays.clone(pContext);
        return myParams;
    }

    /**
     * Create a key parameter.
     * @param pKey the key
     * @return the parameter
     */
    public static GordianBlake3Parameters key(final byte[] pKey) {
        if (pKey == null || pKey.length != KEYLEN) {
            throw new IllegalArgumentException("Invalid keyLength");
        }
        final GordianBlake3Parameters myParams = new GordianBlake3Parameters();
        myParams.theKey = Arrays.clone(pKey);
        return myParams;
    }

    /**
     * Obtain the key.
     * @return the key
     */
    public byte[] getKey() {
        return Arrays.clone(theKey);
    }

    /**
     * Clear the key bytes.
     */
    public void clearKey() {
        Arrays.fill(theKey, (byte) 0);
    }

    /**
     * Obtain the salt.
     * @return the salt
     */
    public byte[] getContext() {
        return Arrays.clone(theContext);
    }
}
