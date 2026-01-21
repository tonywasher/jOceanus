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
package io.github.tonywasher.joceanus.gordianknot.api.keyset;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipher;

/**
 * KeySet Cipher.
 */
public interface GordianKeySetCipher
        extends GordianCipher {
    /**
     * Initialise the cipher for encryption.
     *
     * @throws GordianException on error
     */
    void initForEncrypt() throws GordianException;

    /**
     * Initialise the cipher for decryption.
     *
     * @throws GordianException on error
     */
    void initForDecrypt() throws GordianException;
}
