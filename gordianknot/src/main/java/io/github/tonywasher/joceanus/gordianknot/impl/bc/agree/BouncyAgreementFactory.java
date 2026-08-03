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
package io.github.tonywasher.joceanus.gordianknot.impl.bc.agree;

import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNTRUPrimeSpec.GordianNTRUPrimeType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreNTRUPrimeSpec;

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
    public BouncyAgreementFactory(final GordianBaseFactory pFactory) {
        super(pFactory);
    }

    @Override
    public GordianCoreAgreementEngine createEngine(final GordianAgreementSpec pSpec) throws GordianException {
        final GordianCoreAgreementSpec mySpec = (GordianCoreAgreementSpec) pSpec;
        return switch (pSpec.getKeyPairSpec().getKeyPairType()) {
            case RSA -> new BouncyRSAAgreementEngine(this, mySpec);
            case EC, GOST, DSTU, SM2 -> getBCECEngine(mySpec);
            case DH -> getBCDHEngine(mySpec);
            case NEWHOPE -> new BouncyNewHopeAgreementEngine(this, mySpec);
            case CMCE -> new BouncyCMCEAgreementEngine(this, mySpec);
            case FRODO -> new BouncyFrodoAgreementEngine(this, mySpec);
            case SABER -> new BouncySABERAgreementEngine(this, mySpec);
            case MLKEM -> new BouncyMLKEMAgreementEngine(this, mySpec);
            case HQC -> new BouncyHQCAgreementEngine(this, mySpec);
            case BIKE -> new BouncyBIKEAgreementEngine(this, mySpec);
            case NTRU -> new BouncyNTRUAgreementEngine(this, mySpec);
            case NTRUPLUS -> new BouncyNTRUPlusAgreementEngine(this, mySpec);
            case SMAUGT -> new BouncySmaugTAgreementEngine(this, mySpec);
            case NTRUPRIME -> {
                final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pSpec.getKeyPairSpec();
                final GordianCoreNTRUPrimeSpec myPrimeSpec = myKeySpec.getNTRUPrimeSpec();
                yield myPrimeSpec.getType() == GordianNTRUPrimeType.NTRUL
                        ? new BouncyNTRULPrimeAgreementEngine(this, mySpec)
                        : new BouncySNTRUPrimeAgreementEngine(this, mySpec);
            }
            case XDH -> getBCXDHEngine(mySpec);
            case SM9 -> getBCSM9Engine(mySpec);
            case COMPOSITE -> super.createEngine(pSpec);
            default -> super.createEngine(pSpec);
        };
    }

    /**
     * Create the BouncyCastle EC Agreement.
     *
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getBCECEngine(final GordianCoreAgreementSpec pSpec) throws GordianException {
        return switch (pSpec.getAgreementType()) {
            case KEM -> new BouncyECIESAgreementEngine(this, pSpec);
            case ANON -> new BouncyECAnonAgreementEngine(this, pSpec);
            case SIGNED, BASIC -> new BouncyECBasicAgreementEngine(this, pSpec);
            case MQV -> new BouncyECMQVAgreementEngine(this, pSpec);
            case UNIFIED -> new BouncyECUnifiedAgreementEngine(this, pSpec);
            case SM2 -> new BouncySM2AgreementEngine(this, pSpec);
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pSpec));
        };
    }

    /**
     * Create the BouncyCastle DH Agreement.
     *
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getBCDHEngine(final GordianCoreAgreementSpec pSpec) throws GordianException {
        return switch (pSpec.getAgreementType()) {
            case ANON -> new BouncyDHAnonAgreementEngine(this, pSpec);
            case SIGNED, BASIC -> new BouncyDHBasicAgreementEngine(this, pSpec);
            case MQV -> new BouncyDHMQVAgreementEngine(this, pSpec);
            case UNIFIED -> new BouncyDHUnifiedAgreementEngine(this, pSpec);
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pSpec));
        };
    }

    /**
     * Create the BouncyCastle XDH Agreement.
     *
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getBCXDHEngine(final GordianCoreAgreementSpec pSpec) throws GordianException {
        return switch (pSpec.getAgreementType()) {
            case ANON -> new BouncyXDHAnonAgreementEngine(this, pSpec);
            case SIGNED, BASIC -> new BouncyXDHBasicAgreementEngine(this, pSpec);
            case UNIFIED -> new BouncyXDHUnifiedAgreementEngine(this, pSpec);
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pSpec));
        };
    }

    /**
     * Create the BouncyCastle EC Agreement.
     *
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getBCSM9Engine(final GordianCoreAgreementSpec pSpec) throws GordianException {
        return switch (pSpec.getAgreementType()) {
            case KEM -> new BouncySM9KEMAgreementEngine(this, pSpec);
            case BASIC -> new BouncySM9XchgAgreementEngine(this, pSpec);
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pSpec));
        };
    }
}
