/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.keypair;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianRequired;

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
     * SPHINCSPlus.
     */
    SPHINCSPLUS,

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
     * Kyber.
     */
    KYBER,

    /**
     * Dilithium.
     */
    DILITHIUM,

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
     * Rainbow.
     */
    RAINBOW,

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
            case SPHINCSPLUS:
            case DILITHIUM:
            case FALCON:
            case XMSS:
            case EDDSA:
            case LMS:
            case RAINBOW:
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
        return this == XMSS;
    }
}
