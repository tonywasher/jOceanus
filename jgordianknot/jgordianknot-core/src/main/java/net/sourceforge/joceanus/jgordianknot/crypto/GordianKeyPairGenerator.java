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
 * GordianKnot class for KeyPair Generators.
 */
public abstract class GordianKeyPairGenerator {
    /**
     * The KeySpec.
     */
    private final GordianAsymKeySpec theKeySpec;

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
     * @param pKeySpec the keySpec
     */
    protected GordianKeyPairGenerator(final GordianFactory pFactory,
                                      final GordianAsymKeySpec pKeySpec) {
        /* Store parameters */
        theKeySpec = pKeySpec;
        theFactory = pFactory;

        /* Cache some values */
        theRandom = pFactory.getRandom();
    }

    /**
     * Obtain keySpec.
     * @return the keySpec
     */
    public GordianAsymKeySpec getKeySpec() {
        return theKeySpec;
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
     * Obtain public key from pair.
     * @param pKeyPair the keyPair
     * @return the public key
     */
    protected GordianPublicKey getPublicKey(final GordianKeyPair pKeyPair) {
        return pKeyPair.getPublicKey();
    }

    /**
     * Obtain private key from pair.
     * @param pKeyPair the keyPair
     * @return the private key
     */
    protected GordianPrivateKey getPrivateKey(final GordianKeyPair pKeyPair) {
        return pKeyPair.getPrivateKey();
    }

    /**
     * Obtain PKCS8EncodedKeySpec.
     * @param pKeyPair the privateKey
     * @return the EncodedKeySpec
     * @throws OceanusException on error
     */
    protected abstract PKCS8EncodedKeySpec getPKCS8Encoding(GordianKeyPair pKeyPair) throws OceanusException;

    /**
     * Create the keyPair from the PKCS8/X509 encodings.
     * @param pPublicKey the encoded public key
     * @param pPrivateKey the encoded private key
     * @return the keyPair
     * @throws OceanusException on error
     */
    public abstract GordianKeyPair deriveKeyPair(X509EncodedKeySpec pPublicKey,
                                                 PKCS8EncodedKeySpec pPrivateKey) throws OceanusException;

    /**
     * Extract the X509 encoding for the public key.
     * @param pKeyPair the keyPair
     * @return the X509 encoding
     * @throws OceanusException on error
     */
    public abstract X509EncodedKeySpec getX509Encoding(GordianKeyPair pKeyPair) throws OceanusException;

    /**
     * Create the public-only keyPair from the X509 encoding.
     * @param pPublicKey the encoded public key
     * @return the public-only keyPair
     * @throws OceanusException on error
     */
    public abstract GordianKeyPair derivePublicOnlyKeyPair(X509EncodedKeySpec pPublicKey) throws OceanusException;
}
