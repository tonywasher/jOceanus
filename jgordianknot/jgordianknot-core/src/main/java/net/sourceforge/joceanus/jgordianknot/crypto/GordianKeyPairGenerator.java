/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
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
     * Secure privateKey.
     * @param pKeyPair the privateKey
     * @param pKeySet the keySet to use to secure privateKey
     * @return the securedPrivateKey
     * @throws OceanusException on error
     */
    public byte[] securePrivateKey(final GordianKeyPair pKeyPair,
                                   final GordianKeySet pKeySet) throws OceanusException {
        if (pKeyPair.isPublicOnly()) {
            throw new GordianDataException("No private key");
        }
        return pKeySet.securePrivateKey(pKeyPair);
    }

    /**
     * Secure privateKey.
     * @param pKeyPair the keyPair to secure
     * @param pKey the key to use to secure privateKey
     * @return the securedPrivateKey
     * @throws OceanusException on error
     */
    public byte[] securePrivateKey(final GordianKeyPair pKeyPair,
                                   final GordianKey<GordianSymKeySpec> pKey) throws OceanusException {
        if (pKeyPair.isPublicOnly()) {
            throw new GordianDataException("No private key");
        }
        final GordianWrapCipher myCipher = theFactory.createWrapCipher(pKey.getKeyType());
        return myCipher.securePrivateKey(pKey, pKeyPair);
    }

    /**
     * Obtain PKCS8EncodedKeySpec.
     * @param pKeyPair the keyPair
     * @return the PrivateKeySpec
     * @throws OceanusException on error
     */
    public abstract PKCS8EncodedKeySpec getPKCS8Encoding(GordianKeyPair pKeyPair) throws OceanusException;

    /**
     * Derive the keyPair from the securedPKCS8/X509 encodings.
     * @param pPublicKeySpec the publicKeySpec
     * @param pSecuredPrivateKey the secured privateKey
     * @param pKeySet the keySet to use to derive privateKey
     * @return the derived keyPair
     * @throws OceanusException on error
     */
    public GordianKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKeySpec,
                                        final byte[] pSecuredPrivateKey,
                                        final GordianKeySet pKeySet) throws OceanusException {
        final PKCS8EncodedKeySpec myKeySpec = pKeySet.derivePrivateKeySpec(pSecuredPrivateKey);
        return deriveKeyPair(pPublicKeySpec, myKeySpec);
    }

    /**
     * Derive the keyPair from the PKCS8/X509 encodings.
     * @param pPublicKeySpec the publicKeySpec
     * @param pSecuredPrivateKey the secured privateKey
     * @param pKey the key to use to derive privateKey
     * @return the derived keyPair
     * @throws OceanusException on error
     */
    public GordianKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKeySpec,
                                        final byte[] pSecuredPrivateKey,
                                        final GordianKey<GordianSymKeySpec> pKey) throws OceanusException {
        final GordianWrapCipher myCipher = theFactory.createWrapCipher(pKey.getKeyType());
        final PKCS8EncodedKeySpec myKeySpec = myCipher.deriveKeySpec(pKey, pSecuredPrivateKey);
        return deriveKeyPair(pPublicKeySpec, myKeySpec);
    }

    /**
     * Create the keyPair from the PKCS8/X509 encodings.
     * @param pPublicKey the encoded public key
     * @param pPrivateKey the secured private key
     * @return the keyPair
     * @throws OceanusException on error
     */
    public abstract GordianKeyPair deriveKeyPair(X509EncodedKeySpec pPublicKey,
                                                 PKCS8EncodedKeySpec pPrivateKey) throws OceanusException;

    /**
     * Extract the X509 encoding for the public key.
     * @param pKeyPair the keyPair
     * @return the X509 publicKeySpec
     * @throws OceanusException on error
     */
    public abstract X509EncodedKeySpec getX509Encoding(GordianKeyPair pKeyPair) throws OceanusException;

    /**
     * Derive the public-only keyPair from the X509 encoding.
     * @param pPublicKeySpec the publicKeySpec
     * @return the derived public-only keyPair
     * @throws OceanusException on error
     */
    public abstract GordianKeyPair derivePublicOnlyKeyPair(X509EncodedKeySpec pPublicKeySpec) throws OceanusException;
}
