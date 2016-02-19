/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-core/src/main/java/net/sourceforge/joceanus/jgordianknot/crypto/GordianBadCredentialsException.java $
 * $Revision: 648 $
 * $Author: Tony $
 * $Date: 2015-11-21 15:20:03 +0000 (Sat, 21 Nov 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

/**
 * Asymmetric KeyPair.
 */
public class GordianKeyPair {
    /**
     * MGF1 Salt length.
     */
    public static final int MGF1_SALTLEN = 64;

    /**
     * Key Type.
     */
    private final GordianAsymKeyType theKeyType;

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

        /* Obtain and check keyType */
        theKeyType = pPublic.getKeyType();
        if (!theKeyType.equals(pPrivate.getKeyType())) {
            throw new IllegalArgumentException("MisMatch on keyTypes");
        }
    }

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    public GordianAsymKeyType getKeyType() {
        return theKeyType;
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
}
