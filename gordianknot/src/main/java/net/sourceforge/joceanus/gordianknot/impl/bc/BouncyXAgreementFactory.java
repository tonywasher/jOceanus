/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianNTRUPrimeSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianNTRUPrimeSpec.GordianNTRUPrimeType;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyBIKEKeyPair.BouncyBIKEXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyCMCEKeyPair.BouncyCMCEXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyDHKeyPair.BouncyDHAnonXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyDHKeyPair.BouncyDHBasicXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyDHKeyPair.BouncyDHMQVXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyDHKeyPair.BouncyDHUnifiedXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECAnonXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECBasicXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECIESXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECMQVXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECUnifiedXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyFrodoKeyPair.BouncyFrodoXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyHQCKeyPair.BouncyHQCXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyMLKEMKeyPair.BouncyMLKEMXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyNTRUKeyPair.BouncyNTRUXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyNTRULPrimeKeyPair.BouncyNTRULPrimeXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyNewHopeKeyPair.BouncyNewHopeXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyRSAKeyPair.BouncyRSAXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncySABERKeyPair.BouncySABERXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncySM2KeyPair.BouncySM2XAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncySNTRUPrimeKeyPair.BouncySNTRUPrimeXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyXDHKeyPair.BouncyXDHAnonXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyXDHKeyPair.BouncyXDHBasicXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyXDHKeyPair.BouncyXDHUnifiedXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseData;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementFactory;

/**
 * Bouncy Agreement Factory.
 */
 public class BouncyXAgreementFactory
        extends GordianXCoreAgreementFactory {
    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    BouncyXAgreementFactory(final GordianBaseFactory pFactory) {
        super(pFactory);
    }

    @Override
    public GordianXCoreAgreementEngine createEngine(final GordianAgreementSpec pSpec) throws GordianException {
        switch (pSpec.getKeyPairSpec().getKeyPairType()) {
            case RSA:       return new BouncyRSAXAgreementEngine(this, pSpec);
            case EC:
            case GOST2012:
            case DSTU4145:
            case SM2:       return getBCECEngine(pSpec);
            case DH:        return getBCDHEngine(pSpec);
            case NEWHOPE:   return new BouncyNewHopeXAgreementEngine(this, pSpec);
            case CMCE:      return new BouncyCMCEXAgreementEngine(this, pSpec);
            case FRODO:     return new BouncyFrodoXAgreementEngine(this, pSpec);
            case SABER:     return new BouncySABERXAgreementEngine(this, pSpec);
            case MLKEM:     return new BouncyMLKEMXAgreementEngine(this, pSpec);
            case HQC:       return new BouncyHQCXAgreementEngine(this, pSpec);
            case BIKE:      return new BouncyBIKEXAgreementEngine(this, pSpec);
            case NTRU:      return new BouncyNTRUXAgreementEngine(this, pSpec);
            case NTRUPRIME:
                final GordianNTRUPrimeSpec mySpec = pSpec.getKeyPairSpec().getNTRUPrimeKeySpec();
                return mySpec.getType() == GordianNTRUPrimeType.NTRUL
                        ? new BouncyNTRULPrimeXAgreementEngine(this, pSpec)
                        : new BouncySNTRUPrimeXAgreementEngine(this, pSpec);
            case XDH:       return getBCXDHEngine(pSpec);
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
    private GordianXCoreAgreementEngine getBCECEngine(final GordianAgreementSpec pSpec) throws GordianException {
        switch (pSpec.getAgreementType()) {
            case KEM:       return new BouncyECIESXAgreementEngine(this, pSpec);
            case ANON:      return new BouncyECAnonXAgreementEngine(this, pSpec);
            case SIGNED:
            case BASIC:     return new BouncyECBasicXAgreementEngine(this, pSpec);
            case MQV:       return new BouncyECMQVXAgreementEngine(this, pSpec);
            case UNIFIED:   return new BouncyECUnifiedXAgreementEngine(this, pSpec);
            case SM2:       return new BouncySM2XAgreementEngine(this, pSpec);
            default:        throw new GordianDataException(GordianBaseData.getInvalidText(pSpec));
        }
    }

    /**
     * Create the BouncyCastle DH Agreement.
     *
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianXCoreAgreementEngine getBCDHEngine(final GordianAgreementSpec pSpec) throws GordianException {
        switch (pSpec.getAgreementType()) {
            case ANON:      return new BouncyDHAnonXAgreementEngine(this, pSpec);
            case SIGNED:
            case BASIC:     return new BouncyDHBasicXAgreementEngine(this, pSpec);
            case MQV:       return new BouncyDHMQVXAgreementEngine(this, pSpec);
            case UNIFIED:   return new BouncyDHUnifiedXAgreementEngine(this, pSpec);
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
    private GordianXCoreAgreementEngine getBCXDHEngine(final GordianAgreementSpec pSpec) throws GordianException {
        switch (pSpec.getAgreementType()) {
            case ANON:      return new BouncyXDHAnonXAgreementEngine(this, pSpec);
            case SIGNED:
            case BASIC:     return new BouncyXDHBasicXAgreementEngine(this, pSpec);
            case UNIFIED:   return new BouncyXDHUnifiedXAgreementEngine(this, pSpec);
            default:        throw new GordianDataException(GordianBaseData.getInvalidText(pSpec));
        }
    }
}
