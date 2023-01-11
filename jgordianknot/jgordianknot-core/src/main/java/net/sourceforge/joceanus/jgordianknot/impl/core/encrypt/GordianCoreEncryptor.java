/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.encrypt;

import java.security.SecureRandom;

import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Asymmetric Encryptor.
 */
public abstract class GordianCoreEncryptor
    implements GordianEncryptor {
    /**
     * The Factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The encryptorSpec.
     */
    private final GordianEncryptorSpec theSpec;

    /**
     * The KeyPair.
     */
    private GordianCoreKeyPair theKeyPair;

    /**
     * Encrypt Mode.
     */
    private GordianEncryptMode theMode;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the encryptorSpec
     */
    protected GordianCoreEncryptor(final GordianCoreFactory pFactory,
                                   final GordianEncryptorSpec pSpec) {
        theFactory = pFactory;
        theSpec = pSpec;
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    public GordianCoreFactory getFactory() {
        return theFactory;
    }

    /**
     * Obtain the randomGenerator.
     * @return the random
     */
    public SecureRandom getRandom() {
        return theFactory.getRandomSource().getRandom();
    }

    @Override
    public GordianEncryptorSpec getEncryptorSpec() {
        return theSpec;
    }

    /**
     * CheckKeyPair.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    protected void checkKeyPair(final GordianKeyPair pKeyPair) throws OceanusException {
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianEncryptorFactory myEncrypts = myFactory.getEncryptorFactory();
        if (!myEncrypts.validEncryptorSpecForKeyPair(pKeyPair, theSpec)) {
            throw new GordianDataException("Incorrect KeyPair type");
        }
    }

    /**
     * Obtain public key from pair.
     * @return the public key
     */
    protected GordianPublicKey getPublicKey() {
        return theKeyPair.getPublicKey();
    }

    /**
     * Obtain private key from pair.
     * @return the private key
     */
    protected GordianPrivateKey getPrivateKey() {
        return theKeyPair.getPrivateKey();
    }

    @Override
    public void initForEncrypt(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check that the keyPair matches */
        checkKeyPair(pKeyPair);

        /* Store details */
        theMode = GordianEncryptMode.ENCRYPT;
        theKeyPair = (GordianCoreKeyPair) pKeyPair;
    }

    @Override
    public void initForDecrypt(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check that the keyPair matches */
        checkKeyPair(pKeyPair);

        /* Check that we have the private key */
        if (pKeyPair.isPublicOnly()) {
            throw new GordianDataException("Missing privateKey");
        }

        /* Store details */
        theMode = GordianEncryptMode.DECRYPT;
        theKeyPair = (GordianCoreKeyPair) pKeyPair;
    }

    /**
     * Check that we are in the correct mode.
     * @param pMode the required mode
     * @throws OceanusException on error
     */
    protected void checkMode(final GordianEncryptMode pMode) throws OceanusException {
        if (!pMode.equals(theMode)) {
            throw new GordianDataException("Incorrect encryption Mode");
        }
    }

    /**
     * Are we encrypting?
     * @return true/false
     */
    protected boolean isEncrypting() {
        return GordianEncryptMode.ENCRYPT.equals(theMode);
    }

    /**
     * EncryptionMode.
     */
    public enum GordianEncryptMode {
        /**
         * Encrypt.
         */
        ENCRYPT,

        /**
         * Decrypt.
         */
        DECRYPT;
    }
}
