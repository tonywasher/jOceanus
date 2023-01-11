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
package net.sourceforge.joceanus.jgordianknot.impl.bc;

import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyElGamalKeyPair.BouncyElGamalEncryptor;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECEncryptor;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyRSAKeyPair.BouncyRSAEncryptor;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySM2KeyPair.BouncySM2Encryptor;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.encrypt.GordianCompositeEncryptor;
import net.sourceforge.joceanus.jgordianknot.impl.core.encrypt.GordianCoreEncryptorFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Bouncy Encryptor Factory.
 */
public class BouncyEncryptorFactory
        extends GordianCoreEncryptorFactory {
    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    BouncyEncryptorFactory(final BouncyFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
    }

    @Override
    protected BouncyFactory getFactory() {
        return (BouncyFactory) super.getFactory();
    }

    @Override
    public GordianEncryptor createEncryptor(final GordianEncryptorSpec pEncryptorSpec) throws OceanusException {
        /* Check validity of Encryptor */
        checkEncryptorSpec(pEncryptorSpec);

        /* Create the encryptor */
        return getBCEncryptor(pEncryptorSpec);
    }

    /**
     * Create the BouncyCastle Encryptor.
     *
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws OceanusException on error
     */
    private GordianEncryptor getBCEncryptor(final GordianEncryptorSpec pSpec) throws OceanusException {
        switch (pSpec.getKeyPairType()) {
            case RSA:
                return new BouncyRSAEncryptor(getFactory(), pSpec);
            case ELGAMAL:
                return new BouncyElGamalEncryptor(getFactory(), pSpec);
            case EC:
                return new BouncyECEncryptor(getFactory(), pSpec);
            case SM2:
                return new BouncySM2Encryptor(getFactory(), pSpec);
            case COMPOSITE:
                return new GordianCompositeEncryptor(getFactory(), pSpec);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pSpec));
        }
    }
}
