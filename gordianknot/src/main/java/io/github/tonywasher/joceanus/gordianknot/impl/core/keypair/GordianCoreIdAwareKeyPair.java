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

package io.github.tonywasher.joceanus.gordianknot.impl.core.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianIdAwareKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianIdAwareKeyType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseDestroyable;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianLogicException;

/**
 * Core IdAware keyPair.
 */
public interface GordianCoreIdAwareKeyPair
        extends GordianIdAwareKeyPair, GordianBaseDestroyable {
    /**
     * Obtain idAware privateKey.
     *
     * @return the private key
     */
    GordianIdAwarePublicKey getIdAwarePublicKey();

    /**
     * Obtain idAware privateKey.
     *
     * @return the private key
     */
    GordianIdAwarePrivateKey getIdAwarePrivateKey();

    /**
     * Obtain idAware privateKey.
     *
     * @param pPublic  the public key
     * @param pPrivate the private key
     * @return the new keyPair
     */
    GordianIdAwareKeyPair newKeyPair(GordianIdAwarePublicKey pPublic,
                                     GordianIdAwarePrivateKey pPrivate);

    @Override
    default GordianIdAwareUserKeyPair derivePublicOnlyUserKeyPair(final GordianIdAwareKeyType pKeyType,
                                                                  final byte[] pIdentity) throws GordianException {
        /* Reject if requested keyType is not user */
        if (pKeyType == null || !pKeyType.isUserKey()) {
            throw new GordianLogicException("Invalid keyType: " + pKeyType);
        }

        /* Check that KeyType is correct class */
        if (!getSubKeyType().getClass().isInstance(pKeyType)) {
            throw new GordianLogicException("Incorrect keyType: " + pKeyType);
        }

        /* Reject if identity is null */
        if (pIdentity == null || pIdentity.length == 0) {
            throw new GordianLogicException("Null identity");
        }

        /* derive new publicKey */
        final GordianIdAwarePublicKey myPublic = getIdAwarePublicKey().deriveUserPublicKey(pKeyType, pIdentity);
        return (GordianIdAwareUserKeyPair) newKeyPair(myPublic, null);
    }

    /**
     * IdAware PrivateKey.
     */
    interface GordianCoreIdAwareMasterKeyPair
            extends GordianCoreIdAwareKeyPair, GordianIdAwareMasterKeyPair {
        @Override
        GordianIdAwareMasterPrivateKey getIdAwarePrivateKey();

        @Override
        default GordianIdAwareUserKeyPair newUserKeyPair(final GordianIdAwareKeyType pKeyType,
                                                         final byte[] pIdentity) throws GordianException {
            /* Reject if we are not master key */
            if (getSubKeyType().isUserKey()) {
                throw new GordianLogicException("Can't create new userKeyPair from userKey");
            }

            /* Reject if we are public only */
            if (isPublicOnly()) {
                throw new GordianLogicException("Can't create new userKeyPair without privateKey");
            }

            /* Check for destroyed keyPair */
            checkForDestroyed("keyPair");

            /* Reject if requested keyType is not user */
            if (pKeyType == null || !pKeyType.isUserKey()) {
                throw new GordianLogicException("Invalid keyType: " + pKeyType);
            }

            /* Check that KeyType is correct class */
            if (!getSubKeyType().getClass().isInstance(pKeyType)) {
                throw new GordianLogicException("Incorrect keyType: " + pKeyType);
            }

            /* Reject if identity is null */
            if (pIdentity == null || pIdentity.length == 0) {
                throw new GordianLogicException("Null identity");
            }

            /* Create new userKey */
            final GordianIdAwarePrivateKey myPrivate = getIdAwarePrivateKey().newUserPrivateKey(pKeyType, pIdentity);
            final GordianIdAwarePublicKey myPublic = getIdAwarePublicKey().deriveUserPublicKey(pKeyType, pIdentity);
            return (GordianIdAwareUserKeyPair) newKeyPair(myPublic, myPrivate);
        }
    }

    /**
     * IdAware PrivateKey.
     */
    interface GordianIdAwarePrivateKey {
        /**
         * get subKeyType.
         *
         * @return the keyType
         */
        GordianIdAwareKeyType getSubKeyType();
    }

    /**
     * IdAware PrivateKey.
     */
    interface GordianIdAwareMasterPrivateKey
            extends GordianIdAwarePrivateKey {
        /**
         * Obtain a new user keyPair for identity.
         *
         * @param pKeyType  the user keyType
         * @param pIdentity the identity
         * @return the new keyPair
         */
        GordianIdAwareUserPrivateKey newUserPrivateKey(GordianIdAwareKeyType pKeyType,
                                                       byte[] pIdentity);
    }

    /**
     * IdAware PrivateKey.
     */
    interface GordianIdAwareUserPrivateKey
            extends GordianIdAwarePrivateKey {
        /**
         * Obtain identity.
         *
         * @return the identity
         */
        byte[] getIdentity();
    }

    /**
     * IdAware Public Key.
     */
    interface GordianIdAwarePublicKey {
        /**
         * get subKeyType.
         *
         * @return the keyType
         */
        GordianIdAwareKeyType getSubKeyType();

        /**
         * Obtain a new user keyPair for identity.
         *
         * @param pKeyType  the user keyType
         * @param pIdentity the identity
         * @return the new keyPair
         */
        GordianIdAwareUserPublicKey deriveUserPublicKey(GordianIdAwareKeyType pKeyType,
                                                        byte[] pIdentity);

        /**
         * Obtain the master publicKeyPair.
         *
         * @return the master keyPair
         */
        GordianIdAwareMasterKeyPair deriveMasterPublicKey();
    }

    /**
     * IdAware Master Public Key.
     */
    interface GordianIdAwareMasterPublicKey
            extends GordianIdAwarePublicKey {
    }

    /**
     * IdAware UserPublic Key.
     */
    interface GordianIdAwareUserPublicKey
            extends GordianIdAwarePublicKey {
        /**
         * Obtain identity.
         *
         * @return the identity
         */
        byte[] getIdentity();
    }
}
