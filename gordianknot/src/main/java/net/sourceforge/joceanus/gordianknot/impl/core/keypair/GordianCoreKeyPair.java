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
package net.sourceforge.joceanus.gordianknot.impl.core.keypair;

import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;

/**
 * KeyPair implementation.
 */
public abstract class GordianCoreKeyPair
        implements GordianKeyPair {
    /**
     * The KeySpec.
     */
    private final GordianKeyPairSpec theKeySpec;

    /**
     * The PrivateKey.
     */
    private final GordianPrivateKey thePrivateKey;

    /**
     * The PublicKey.
     */
    private final GordianPublicKey thePublicKey;

    /**
     * Constructor.
     * @param pPublic the publicKey
     * @param pPrivate the privateKey
     */
    protected GordianCoreKeyPair(final GordianPublicKey pPublic,
                                 final GordianPrivateKey pPrivate) {
        /* Store keys */
        thePublicKey = pPublic;
        thePrivateKey = pPrivate;

        /* Store and check keySpecs */
        theKeySpec = thePublicKey.getKeySpec();
        if (thePrivateKey != null
                && !theKeySpec.equals(thePrivateKey.getKeySpec())) {
            throw new IllegalArgumentException("MisMatch on keySpecs");
        }
    }

    @Override
    public GordianKeyPairSpec getKeyPairSpec() {
        return theKeySpec;
    }

    @Override
    public boolean isPublicOnly() {
        return thePrivateKey == null;
    }

    /**
     * Obtain a publicOnly version of this key.
     * @return the public key
     */
    public abstract GordianCoreKeyPair getPublicOnly();

    /**
     * Validate that the keyPair public Key matches.
     * @param pPair the key pair
     * @return matches true/false
     */
    public boolean checkMatchingPublicKey(final GordianKeyPair pPair) {
        /* Must be core and matching spec */
        if (!(pPair instanceof GordianCoreKeyPair)
                || !theKeySpec.equals(pPair.getKeyPairSpec())) {
            return false;
        }

        /* Check matching public key */
        return thePublicKey.equals(((GordianCoreKeyPair) pPair).getPublicKey());
    }

    /**
     * Obtain the public key.
     * @return the public key
     */
    public GordianPublicKey getPublicKey() {
        return thePublicKey;
    }

    /**
     * Obtain the private key.
     * @return the private key
     */
    public GordianPrivateKey getPrivateKey() {
        return thePrivateKey;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (pThat == this) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (!(pThat instanceof GordianCoreKeyPair)) {
            return false;
        }

        /* Access the target field */
        final GordianCoreKeyPair myThat = (GordianCoreKeyPair) pThat;

        /* Check key Spec */
        if (!theKeySpec.equals(myThat.getKeyPairSpec())) {
            return false;
        }

        /* Check public key */
        if (!thePublicKey.equals(myThat.getPublicKey())) {
            return false;
        }

        /* Check private key */
        return isPublicOnly()
               ? myThat.isPublicOnly()
               : thePrivateKey.equals(myThat.getPrivateKey());
    }

    @Override
    public int hashCode() {
        int myHash = isPublicOnly()
                     ? 1
                     : thePrivateKey.hashCode();
        myHash *= GordianCoreFactory.HASH_PRIME;
        myHash += getKeyPairSpec().hashCode();
        myHash *= GordianCoreFactory.HASH_PRIME;
        return myHash + thePublicKey.hashCode();
    }
}
