/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
     * BIKE.
     */
    BIKE,

    /**
     * NTRU.
     */
    NTRU,

    /**
     * NTRULPRIME.
     */
    NTRULPRIME,

    /**
     * SNTRUPRIME.
     */
    SNTRUPRIME,

    /**
     * Falcon.
     */
    FALCON,

    /**
     * Picnic.
     */
    PICNIC,

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
            case SPHINCSPLUS:
            case DILITHIUM:
            case FALCON:
            case XMSS:
            case EDDSA:
                return false;
            default:
                return true;
        }
    }

    /**
     * null digest for signatures?
     * @return true/false
     */
    public boolean nullDigestForSignatures() {
        switch (this) {
            case SPHINCSPLUS:
            case DILITHIUM:
            case FALCON:
            case PICNIC:
            case XMSS:
            case EDDSA:
            case LMS:
                return true;
            default:
                return false;
        }
    }

    /**
     * use subType for signatures?
     * @return true/false
     */
    public boolean subTypeForSignatures() {
        switch (this) {
            case XMSS:
                return true;
            default:
                return false;
        }
    }
}
