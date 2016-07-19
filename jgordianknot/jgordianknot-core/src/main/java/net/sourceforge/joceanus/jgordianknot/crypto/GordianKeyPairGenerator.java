/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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

import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot interface for KeyPair Generators.
 */
public abstract class GordianKeyPairGenerator {
    /**
     * The Key Type.
     */
    private final GordianAsymKeyType theKeyType;

    /**
     * The Security Factory.
     */
    private final GordianFactory theFactory;

    /**
     * The Random Generator.
     */
    private final SecureRandom theRandom;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeyType the keyType
     */
    protected GordianKeyPairGenerator(final GordianFactory pFactory,
                                      final GordianAsymKeyType pKeyType) {
        /* Store parameters */
        theKeyType = pKeyType;
        theFactory = pFactory;

        /* Cache some values */
        theRandom = pFactory.getRandom();
    }

    /**
     * Obtain keyType.
     * @return the keyType
     */
    public GordianAsymKeyType getKeyType() {
        return theKeyType;
    }

    /**
     * Obtain random generator.
     * @return the generator
     */
    protected SecureRandom getRandom() {
        return theRandom;
    }

    /**
     * Obtain factory.
     * @return the factory
     */
    protected GordianFactory getFactory() {
        return theFactory;
    }

    /**
     * Generate a new KeyPair.
     * @return the new KeyPair
     */
    public abstract GordianKeyPair generateKeyPair();

    /**
     * Obtain PKCS8EncodedKeySpec.
     * @param pPrivateKey the privateKey
     * @return the EncodedKeySpec
     * @throws OceanusException on error
     */
    protected abstract PKCS8EncodedKeySpec getPKCS8Encoding(final GordianPrivateKey pPrivateKey) throws OceanusException;

    /**
     * Create the private key from the PKCS8 encoding.
     * @param pEncodedKey the encoded private key
     * @return the private key
     * @throws OceanusException on error
     */
    protected abstract GordianPrivateKey derivePrivateKey(final PKCS8EncodedKeySpec pEncodedKey) throws OceanusException;

    /**
     * Extract the X509 encoding for the public key.
     * @param pPublicKey the public key
     * @return the X509 encoding
     * @throws OceanusException on error
     */
    public abstract X509EncodedKeySpec getX509Encoding(final GordianPublicKey pPublicKey) throws OceanusException;

    /**
     * Create the public key from the X509 encoding.
     * @param pEncodedKey the encoded public key
     * @return the public key
     * @throws OceanusException on error
     */
    public abstract GordianPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException;
}
