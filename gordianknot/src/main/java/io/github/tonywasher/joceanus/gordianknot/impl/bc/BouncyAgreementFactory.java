/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.impl.bc;

import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianNTRUPrimeSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianNTRUPrimeSpec.GordianNTRUPrimeType;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyBIKEKeyPair.BouncyBIKEAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyCMCEKeyPair.BouncyCMCEAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyDHKeyPair.BouncyDHAnonAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyDHKeyPair.BouncyDHBasicAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyDHKeyPair.BouncyDHMQVAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyDHKeyPair.BouncyDHUnifiedAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECAnonAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECBasicAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECIESAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECMQVAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECUnifiedAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyFrodoKeyPair.BouncyFrodoAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyHQCKeyPair.BouncyHQCAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyMLKEMKeyPair.BouncyMLKEMAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyNTRUKeyPair.BouncyNTRUAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyNTRULPrimeKeyPair.BouncyNTRULPrimeAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyNewHopeKeyPair.BouncyNewHopeAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyRSAKeyPair.BouncyRSAAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncySABERKeyPair.BouncySABERAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncySM2KeyPair.BouncySM2AgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncySNTRUPrimeKeyPair.BouncySNTRUPrimeAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyXDHKeyPair.BouncyXDHAnonAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyXDHKeyPair.BouncyXDHBasicAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyXDHKeyPair.BouncyXDHUnifiedAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;

/**
 * Bouncy Agreement Factory.
 */
public class BouncyAgreementFactory
        extends GordianCoreAgreementFactory {
    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    BouncyAgreementFactory(final GordianBaseFactory pFactory) {
        super(pFactory);
    }

    @Override
    public GordianCoreAgreementEngine createEngine(final GordianAgreementSpec pSpec) throws GordianException {
        switch (pSpec.getKeyPairSpec().getKeyPairType()) {
            case RSA:
                return new BouncyRSAAgreementEngine(this, pSpec);
            case EC:
            case GOST2012:
            case DSTU4145:
            case SM2:
                return getBCECEngine(pSpec);
            case DH:
                return getBCDHEngine(pSpec);
            case NEWHOPE:
                return new BouncyNewHopeAgreementEngine(this, pSpec);
            case CMCE:
                return new BouncyCMCEAgreementEngine(this, pSpec);
            case FRODO:
                return new BouncyFrodoAgreementEngine(this, pSpec);
            case SABER:
                return new BouncySABERAgreementEngine(this, pSpec);
            case MLKEM:
                return new BouncyMLKEMAgreementEngine(this, pSpec);
            case HQC:
                return new BouncyHQCAgreementEngine(this, pSpec);
            case BIKE:
                return new BouncyBIKEAgreementEngine(this, pSpec);
            case NTRU:
                return new BouncyNTRUAgreementEngine(this, pSpec);
            case NTRUPRIME:
                final GordianNTRUPrimeSpec mySpec = pSpec.getKeyPairSpec().getNTRUPrimeKeySpec();
                return mySpec.getType() == GordianNTRUPrimeType.NTRUL
                        ? new BouncyNTRULPrimeAgreementEngine(this, pSpec)
                        : new BouncySNTRUPrimeAgreementEngine(this, pSpec);
            case XDH:
                return getBCXDHEngine(pSpec);
            case COMPOSITE:
            default:
                return super.createEngine(pSpec);
        }
    }

    /**
     * Create the BouncyCastle EC Agreement.
     *
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getBCECEngine(final GordianAgreementSpec pSpec) throws GordianException {
        switch (pSpec.getAgreementType()) {
            case KEM:
                return new BouncyECIESAgreementEngine(this, pSpec);
            case ANON:
                return new BouncyECAnonAgreementEngine(this, pSpec);
            case SIGNED:
            case BASIC:
                return new BouncyECBasicAgreementEngine(this, pSpec);
            case MQV:
                return new BouncyECMQVAgreementEngine(this, pSpec);
            case UNIFIED:
                return new BouncyECUnifiedAgreementEngine(this, pSpec);
            case SM2:
                return new BouncySM2AgreementEngine(this, pSpec);
            default:
                throw new GordianDataException(GordianBaseData.getInvalidText(pSpec));
        }
    }

    /**
     * Create the BouncyCastle DH Agreement.
     *
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getBCDHEngine(final GordianAgreementSpec pSpec) throws GordianException {
        switch (pSpec.getAgreementType()) {
            case ANON:
                return new BouncyDHAnonAgreementEngine(this, pSpec);
            case SIGNED:
            case BASIC:
                return new BouncyDHBasicAgreementEngine(this, pSpec);
            case MQV:
                return new BouncyDHMQVAgreementEngine(this, pSpec);
            case UNIFIED:
                return new BouncyDHUnifiedAgreementEngine(this, pSpec);
            default:
                throw new GordianDataException(GordianBaseData.getInvalidText(pSpec));
        }
    }

    /**
     * Create the BouncyCastle XDH Agreement.
     *
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getBCXDHEngine(final GordianAgreementSpec pSpec) throws GordianException {
        switch (pSpec.getAgreementType()) {
            case ANON:
                return new BouncyXDHAnonAgreementEngine(this, pSpec);
            case SIGNED:
            case BASIC:
                return new BouncyXDHBasicAgreementEngine(this, pSpec);
            case UNIFIED:
                return new BouncyXDHUnifiedAgreementEngine(this, pSpec);
            default:
                throw new GordianDataException(GordianBaseData.getInvalidText(pSpec));
        }
    }
}
