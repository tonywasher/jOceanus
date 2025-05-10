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
package net.sourceforge.joceanus.gordianknot.api.keypair;

import net.sourceforge.joceanus.gordianknot.api.base.GordianRequired;

/**
 * Asymmetric KeyPairTypes.
 */
public enum GordianKeyPairType {
    /**
     * RSA.
     */
    RSA,

    /**
     * EllipticCurve.
     */
    EC,

    /**
     * DSA.
     */
    DSA,

    /**
     * DiffieHellman.
     */
    DH,

    /**
     * ElGamal.
     */
    ELGAMAL,

    /**
     * SM2.
     */
    SM2,

    /**
     * DSTU4145.
     */
    DSTU4145,

    /**
     * GOST2012.
     */
    GOST2012,

    /**
     * EdwardsXDH.
     */
    XDH,

    /**
     * EdwardsDSA.
     */
    EDDSA,

    /**
     * XMSS.
     */
    XMSS,

    /**
     * LMS.
     */
    LMS,

    /**
     * SLHDSA.
     */
    SLHDSA,

    /**
     * CMCE.
     */
    CMCE,

    /**
     * FRODO.
     */
    FRODO,

    /**
     * SABER.
     */
    SABER,

    /**
     * MLKEM.
     */
    MLKEM,

    /**
     * MLDSA.
     */
    MLDSA,

    /**
     * HQC.
     */
    HQC,

    /**
     * BIKE.
     */
    BIKE,

    /**
     * NTRU.
     */
    NTRU,

    /**
     * NTRUPRIME.
     */
    NTRUPRIME,

    /**
     * Falcon.
     */
    FALCON,

    /**
     * Picnic.
     */
    PICNIC,

    /**
     * NewHope.
     */
    NEWHOPE,

    /**
     * Mayo.
     */
    MAYO,

    /**
     * Snova.
     */
    SNOVA,

    /**
     * Composite.
     */
    COMPOSITE;

    /**
     * use random for signatures?
     * @return true/false
     */
    public boolean useRandomForSignatures() {
        switch (this) {
            case PICNIC:
            case LMS:
            case XMSS:
            case EDDSA:
                return false;
            default:
                return true;
        }
    }

    /**
     * Do we need a digest for signatures?
     * @return ALWAYS/POSSIBLE/NEVER
     */
    public GordianRequired useDigestForSignatures() {
        switch (this) {
            case SLHDSA:
            case MLDSA:
            case FALCON:
            case MAYO:
            case SNOVA:
            case XMSS:
            case EDDSA:
            case LMS:
                return GordianRequired.NEVER;
            case PICNIC:
                return GordianRequired.POSSIBLE;
            default:
                return GordianRequired.ALWAYS;
        }
    }

    /**
     * use subType for signatures?
     * @return true/false
     */
    public boolean subTypeForSignatures() {
        switch (this) {
            case MLDSA:
            case SLHDSA:
            case FALCON:
            case MAYO:
            case SNOVA:
            case XMSS:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is the keyPair in the standard jcaProvider?
     * @return true/false
     */
    public boolean isStandardJca() {
        switch (this) {
            case RSA:
            case DSA:
            case EC:
            case ELGAMAL:
            case DH:
            case SM2:
            case GOST2012:
            case DSTU4145:
            case XDH:
            case EDDSA:
            case MLKEM:
            case MLDSA:
            case SLHDSA:
                return true;
            case BIKE:
            case FRODO:
            case SABER:
            case CMCE:
            case FALCON:
            case NTRU:
            case NTRUPRIME:
            case HQC:
            case PICNIC:
            case XMSS:
            case LMS:
            case MAYO:
            default:
                return false;
        }
    }
}
