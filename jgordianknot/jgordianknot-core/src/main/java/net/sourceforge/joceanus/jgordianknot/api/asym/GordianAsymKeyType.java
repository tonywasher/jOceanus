/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.asym;

/**
 * Asymmetric KeyTypes.
 */
public enum GordianAsymKeyType {
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
     * EdwardsXDH25519.
     */
    X25519,

    /**
     * EdwardsXDH448.
     */
    X448,

    /**
     * EdwardsDSA25519.
     */
    ED25519,

    /**
     * EdwardsDSA448.
     */
    ED448,

    /**
     * SPHINCS.
     */
    SPHINCS,

    /**
     * McEliece.
     */
    MCELIECE,

    /**
     * Rainbow.
     */
    RAINBOW,

    /**
     * XMSS.
     */
    XMSS,

    /**
     * XMSSMT.
     */
    XMSSMT,

    /**
     * NewHope.
     */
    NEWHOPE,

    /**
     * qTESLA.
     */
    QTESLA;

    /**
     * use random for signatures?
     * @return true/false
     */
    public boolean useRandomForSignatures() {
        switch (this) {
            case SPHINCS:
            case XMSS:
            case XMSSMT:
            case ED25519:
            case ED448:
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
            case SPHINCS:
            case XMSS:
            case XMSSMT:
            case ED25519:
            case ED448:
            case QTESLA:
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
            case SPHINCS:
            case XMSS:
            case XMSSMT:
            case QTESLA:
                return true;
            default:
                return false;
        }
    }

    /**
     * Dow we skipped derived key equality check?
     * <p>
     * Temporary fix for DH JCA bug where the derived key does not equal the original key
     * @return true/false
     */
    public boolean differentDerivedKey() {
        return this == DH;
    }
}
