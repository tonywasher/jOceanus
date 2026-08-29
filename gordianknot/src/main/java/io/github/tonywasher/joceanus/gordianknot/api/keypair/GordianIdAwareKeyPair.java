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

package io.github.tonywasher.joceanus.gordianknot.api.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianIdAwareKeyType;

import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Asymmetric IdAware KeyPair.
 */
public interface GordianIdAwareKeyPair
        extends GordianKeyPair {
    /**
     * get subKeyType.
     *
     * @return the keyType
     */
    GordianIdAwareKeyType getSubKeyType();

    /**
     * Obtain a new publicOnly keyPair for identity.
     *
     * @param pKeyType  the user keyType
     * @param pIdentity the identity
     * @return the new keyPair
     * @throws GordianException on error
     */
    GordianIdAwareUserKeyPair derivePublicOnlyUserKeyPair(GordianIdAwareKeyType pKeyType,
                                                          byte[] pIdentity) throws GordianException;

    /**
     * Asymmetric IdAware Master KeyPair.
     */
    interface GordianIdAwareMasterKeyPair
            extends GordianIdAwareKeyPair {
        /**
         * Obtain a new user keyPair for identity.
         *
         * @param pKeyType  the user keyType
         * @param pIdentity the identity
         * @return the new keyPair
         * @throws GordianException on error
         */
        GordianIdAwareUserKeyPair newUserKeyPair(GordianIdAwareKeyType pKeyType,
                                                 byte[] pIdentity) throws GordianException;

        /**
         * Obtain a new user keyPair for identity and encoding.
         *
         * @param pEncoding the encoding
         * @param pKeyType  the user keyType
         * @param pIdentity the identity
         * @return the new keyPair
         * @throws GordianException on error
         */
        GordianIdAwareUserKeyPair deriveUserKeyPairFromEncoding(PKCS8EncodedKeySpec pEncoding,
                                                                GordianIdAwareKeyType pKeyType,
                                                                byte[] pIdentity) throws GordianException;
    }

    /**
     * Asymmetric StateAware KeyPair.
     */
    interface GordianIdAwareUserKeyPair
            extends GordianIdAwareKeyPair {
        /**
         * Obtain identity.
         *
         * @return the identity
         */
        byte[] getIdentity();

        /**
         * Obtain the userKeyPair encoding.
         *
         * @return the encoding
         * @throws GordianException on error
         */
        PKCS8EncodedKeySpec getPartialEncoding() throws GordianException;
    }
}
