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
package net.sourceforge.joceanus.jgordianknot.impl.core.encrypt;

import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianMcElieceKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;

/**
 * GordianKnot base for encryptorFactory.
 */
public abstract class GordianCoreEncryptorFactory
    implements GordianEncryptorFactory {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    public GordianCoreEncryptorFactory(final GordianCoreFactory pFactory) {
        theFactory = pFactory;
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected GordianCoreFactory getFactory() {
        return theFactory;
    }

    @Override
    public Predicate<GordianEncryptorSpec> supportedEncryptors() {
        return this::validEncryptorSpec;
    }

    /**
     * Check EncryptorSpec.
     * @param pSpec the encryptorSpec
     * @return true/false
     */
    protected boolean validEncryptorSpec(final GordianEncryptorSpec pSpec) {
        /* Check that spec is supported */
        return pSpec.isSupported();
    }

    @Override
    public boolean validEncryptorSpecForKeyPair(final GordianKeyPair pKeyPair,
                                                final GordianEncryptorSpec pEncryptorSpec) {
        /* Check encryptor matches keyPair */
        if (pEncryptorSpec.getKeyType() != pKeyPair.getKeySpec().getKeyType()) {
            return false;
        }

        /* Check that the encryptorSpec is supported */
        if (!validEncryptorSpec(pEncryptorSpec)) {
            return false;
        }

        /* Disallow EC if the curve does not support encryption */
        final GordianAsymKeySpec myKeySpec = pKeyPair.getKeySpec();
        if (GordianAsymKeyType.EC.equals(myKeySpec.getKeyType())) {
            return myKeySpec.getElliptic().canEncrypt();
        }

        /* Disallow McEliece if it is the wrong style key */
        if (GordianAsymKeyType.MCELIECE.equals(myKeySpec.getKeyType())) {
            return GordianMcElieceKeySpec.checkValidEncryptionType(myKeySpec.getMcElieceSpec(), pEncryptorSpec.getMcElieceType());
        }

        /* If this is a RSA encryption */
        if (GordianAsymKeyType.RSA.equals(myKeySpec.getKeyType())) {
            /* The digest length cannot be too large wrt to the modulus */
            int myLen = pEncryptorSpec.getDigestSpec().getDigestLength().getByteLength();
            myLen = (myLen + 1) * Byte.SIZE;
            if (myKeySpec.getModulus().getLength() < (myLen << 1)) {
                return false;
            }
        }

        /* OK */
        return true;
    }
}
