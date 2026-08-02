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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianIdAwareKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianLogicException;

/**
 * Core IdAware keyPair.
 *
 * @param <K> the keyType.
 */
public class GordianCoreIdAwareKeyPair<K extends GordianIdAwareKeyType>
        extends GordianCoreKeyPair
        implements GordianIdAwareKeyPair<K> {
    /**
     * Constructor.
     *
     * @param pPublic the public key
     */
    private GordianCoreIdAwareKeyPair(final GordianIdAwarePublicKey<K> pPublic) {
        this(pPublic, null);
    }

    /**
     * Constructor.
     *
     * @param pPublic  the publicKey
     * @param pPrivate the privateKey
     */
    protected GordianCoreIdAwareKeyPair(final GordianIdAwarePublicKey<K> pPublic,
                                        final GordianIdAwarePrivateKey<K> pPrivate) {
        super((GordianPublicKey) pPublic, (GordianPrivateKey) pPrivate);
    }

    @Override
    public GordianCoreIdAwareKeyPair<K> getPublicOnly() {
        return new GordianCoreIdAwareKeyPair<>(getIdAwarePublicKey());
    }

    /**
     * Obtain idAware privateKey.
     *
     * @return the private key
     */
    @SuppressWarnings("unchecked")
    public GordianIdAwarePublicKey<K> getIdAwarePublicKey() {
        return (GordianIdAwarePublicKey<K>) getPublicKey();
    }

    /**
     * Obtain idAware privateKey.
     *
     * @return the private key
     */
    @SuppressWarnings("unchecked")
    public GordianIdAwarePrivateKey<K> getIdAwarePrivateKey() {
        return (GordianIdAwarePrivateKey<K>) getPrivateKey();
    }

    @Override
    public K getSubKeyType() {
        return getIdAwarePublicKey().getSubKeyType();
    }

    @Override
    public byte[] getIdentity() {
        return getIdAwarePublicKey().getIdentity();
    }

    @Override
    public GordianIdAwareKeyPair<K> newUserKeyPair(K pKeyType,
                                                   byte[] pIdentity) throws GordianException {
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

        /* Create new userKey */
        final GordianIdAwarePrivateKey<K> myPrivate = getIdAwarePrivateKey().newUserPrivateKey(pKeyType, pIdentity);
        final GordianIdAwarePublicKey<K> myPublic = getIdAwarePublicKey().deriveUserPublicKey(pKeyType, pIdentity);
        return new GordianCoreIdAwareKeyPair<K>(myPublic, myPrivate);
    }

    @Override
    public GordianIdAwareKeyPair<K> derivePublicOnlyUserKeyPair(K pKeyType,
                                                                byte[] pIdentity) throws GordianException {
        /* Reject if requested keyType is not user */
        if (pKeyType == null || !pKeyType.isUserKey()) {
            throw new GordianLogicException("Invalid keyType: " + pKeyType);
        }

        /* derive new publicKey */
        final GordianIdAwarePublicKey<K> myPublic = getIdAwarePublicKey().deriveUserPublicKey(pKeyType, pIdentity);
        return new GordianCoreIdAwareKeyPair<K>(myPublic, null);
    }

    /**
     * Check for bouncyKeyPair.
     *
     * @param pKeyPair the keyPair to check
     * @throws GordianException on error
     */
    public static void checkKeyPair(final GordianKeyPair pKeyPair) throws GordianException {
        /* Check that it is a BouncyKeyPair */
        if (!(pKeyPair instanceof GordianCoreIdAwareKeyPair<?> myPair)) {
            /* Reject keyPair */
            throw new GordianDataException("Invalid KeyPair");
        }
        myPair.checkForDestroyedKeyPair();
    }

    /**
     * IdAware PrivateKey.
     *
     * @param <K> the keyType.
     */
    public interface GordianIdAwarePrivateKey<K extends GordianIdAwareKeyType> {
        /**
         * get subKeyType.
         *
         * @return the keyType
         */
        K getSubKeyType();

        /**
         * Obtain identity.
         *
         * @return the identity
         */
        default byte[] getIdentity() {
            return new byte[0];
        }

        /**
         * Obtain a new user keyPair for identity.
         *
         * @param pKeyType  the user keyType
         * @param pIdentity the identity
         * @return the new keyPair
         */
        default GordianIdAwarePrivateKey<K> newUserPrivateKey(K pKeyType,
                                                              byte[] pIdentity) {
            return null;
        }
    }

    /**
     * IdAware Public Key.
     *
     * @param <K> the keyType.
     */
    public interface GordianIdAwarePublicKey<K extends GordianIdAwareKeyType> {
        /**
         * get subKeyType.
         *
         * @return the keyType
         */
        K getSubKeyType();

        /**
         * Obtain identity.
         *
         * @return the identity
         */
        default byte[] getIdentity() {
            return new byte[0];
        }

        /**
         * Obtain a new user keyPair for identity.
         *
         * @param pKeyType  the user keyType
         * @param pIdentity the identity
         * @return the new keyPair
         */
        default GordianIdAwarePublicKey<K> deriveUserPublicKey(K pKeyType,
                                                               byte[] pIdentity) {
            return null;
        }
    }
}
