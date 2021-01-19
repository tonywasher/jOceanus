/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.jca;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianKeyPairEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianMcElieceEncryptionType;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianSM2EncryptionSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianSM2EncryptionSpec.GordianSM2EncryptionType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.encrypt.GordianCoreEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaEncryptor.JcaBlockEncryptor;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaEncryptor.JcaHybridEncryptor;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Jca Encryptor Factory.
 */
public class JcaEncryptorFactory
        extends GordianCoreEncryptorFactory {
    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    JcaEncryptorFactory(final JcaFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
    }

    @Override
    public JcaFactory getFactory() {
        return (JcaFactory) super.getFactory();
    }

    @Override
    public GordianKeyPairEncryptor createKeyPairEncryptor(final GordianEncryptorSpec pEncryptorSpec) throws OceanusException {
        /* Check validity of encryptor */
        checkEncryptorSpec(pEncryptorSpec);

        /* Create the encryptor */
        return getJcaEncryptor(pEncryptorSpec);
    }

    /**
     * Create the Jca Encryptor.
     * @param pEncryptorSpec the encryptorSpec
     * @return the Encryptor
     * @throws OceanusException on error
     */
    private GordianKeyPairEncryptor getJcaEncryptor(final GordianEncryptorSpec pEncryptorSpec) throws OceanusException {
        switch (pEncryptorSpec.getKeyPairType()) {
            case RSA:
            case ELGAMAL:
                return new JcaBlockEncryptor(getFactory(), pEncryptorSpec);
            case SM2:
                return new JcaHybridEncryptor(getFactory(), pEncryptorSpec);
            case MCELIECE:
                return GordianMcElieceEncryptionType.STANDARD.equals(pEncryptorSpec.getMcElieceType())
                       ? new JcaBlockEncryptor(getFactory(), pEncryptorSpec)
                       : new JcaHybridEncryptor(getFactory(), pEncryptorSpec);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pEncryptorSpec.getKeyPairType()));
        }
    }

    /**
     * Create the BouncyCastle KeyFactory via JCA.
     * @param pAlgorithm the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyFactory
     * @throws OceanusException on error
     */
    static Cipher getJavaEncryptor(final String pAlgorithm,
                                   final boolean postQuantum) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a Cipher for the algorithm */
            return Cipher.getInstance(pAlgorithm, postQuantum
                                                  ? JcaFactory.BCPQPROV
                                                  : JcaFactory.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create Cipher", e);
        }
    }

    @Override
    protected boolean validEncryptorSpec(final GordianEncryptorSpec pSpec) {
        /* validate the encryptorSpec */
        if (!super.validEncryptorSpec(pSpec)) {
            return false;
        }

        /* Switch on KeyType */
        switch (pSpec.getKeyPairType()) {
            case RSA:
            case ELGAMAL:
            case MCELIECE:
                return true;
            case SM2:
                final GordianSM2EncryptionSpec mySpec = pSpec.getSM2EncryptionSpec();
                return mySpec != null && mySpec.getEncryptionType() == GordianSM2EncryptionType.C1C2C3;
            default:
                return false;
        }
    }
}
