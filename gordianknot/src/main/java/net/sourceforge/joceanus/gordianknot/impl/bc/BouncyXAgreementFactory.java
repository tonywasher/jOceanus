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
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECIESXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyFrodoKeyPair.BouncyFrodoXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyHQCKeyPair.BouncyHQCXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyMLKEMKeyPair.BouncyMLKEMXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyNTRUKeyPair.BouncyNTRUXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyNTRULPrimeKeyPair.BouncyNTRULPrimeXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyNewHopeKeyPair.BouncyNewHopeXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyRSAKeyPair.BouncyRSAXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncySABERKeyPair.BouncySABERXAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncySNTRUPrimeKeyPair.BouncySNTRUPrimeXAgreementEngine;
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
    BouncyXAgreementFactory(final BouncyFactory pFactory) {
        super(pFactory);
    }

    @Override
    public GordianXCoreAgreementEngine createEngine(final GordianAgreementSpec pSpec) throws GordianException {
        switch (pSpec.getKeyPairSpec().getKeyPairType()) {
            case RSA:   return new BouncyRSAXAgreementEngine(this, pSpec);
            case EC:
            case GOST2012:
            case DSTU4145:
            case SM2:   return new BouncyECIESXAgreementEngine(this, pSpec);
            //    return getBCECEngine(pSpec);
            //case DH:
            //    return getBCDHEngine(pSpec);
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
            //case XDH:
            //    return getBCXDHEngine(pSpec);
            case COMPOSITE:
            default:
                return super.createEngine(pSpec);
        }
    }
 }
