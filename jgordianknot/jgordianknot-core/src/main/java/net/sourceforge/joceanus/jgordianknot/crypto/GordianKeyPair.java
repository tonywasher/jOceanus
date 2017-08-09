/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

/**
 * Asymmetric KeyPair.
 */
public class GordianKeyPair {
    /**
     * Key Spec.
     */
    private final GordianAsymKeySpec theKeySpec;

    /**
     * Public Key.
     */
    private final GordianPublicKey thePublicKey;

    /**
     * Key Type.
     */
    private final GordianPrivateKey thePrivateKey;

    /**
     * Constructor.
     * @param pPublic the public key
     * @param pPrivate the private key
     */
    protected GordianKeyPair(final GordianPublicKey pPublic,
                             final GordianPrivateKey pPrivate) {
        /* Store the keys */
        thePublicKey = pPublic;
        thePrivateKey = pPrivate;
        theKeySpec = pPublic.getKeySpec();

        /* Check that the KeySpec matches the keys */
        if (pPrivate != null
            && !theKeySpec.equals(pPrivate.getKeySpec())) {
            throw new IllegalArgumentException("MisMatch on keyTypes");
        }
    }

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    public GordianAsymKeySpec getKeySpec() {
        return theKeySpec;
    }

    /**
     * Is only the public key known?
     * @return the keyType
     */
    public boolean isPublicOnly() {
        return thePrivateKey == null;
    }

    /**
     * Obtain the public key.
     * @return the public key
     */
    protected GordianPublicKey getPublicKey() {
        return thePublicKey;
    }

    /**
     * Obtain the private key.
     * @return the private key
     */
    protected GordianPrivateKey getPrivateKey() {
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
        if (!(pThat instanceof GordianKeyPair)) {
            return false;
        }

        /* Access the target field */
        final GordianKeyPair myThat = (GordianKeyPair) pThat;

        /* Check key Spec */
        if (!theKeySpec.equals(myThat.getKeySpec())) {
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
        myHash *= GordianFactory.HASH_PRIME;
        myHash += getKeySpec().hashCode();
        myHash *= GordianFactory.HASH_PRIME;
        return myHash + thePublicKey.hashCode();
    }
}
