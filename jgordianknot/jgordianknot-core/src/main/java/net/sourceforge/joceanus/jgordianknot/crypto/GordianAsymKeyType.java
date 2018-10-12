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
package net.sourceforge.joceanus.jgordianknot.crypto;

/**
 * Asymmetric KeyTypes.
 */
public enum GordianAsymKeyType {
    /**
     * RSA.
     */
    RSA(GordianSignatureType.PSS, GordianSignatureType.ISO9796D2, GordianSignatureType.X931),

    /**
     * EllipticCurve.
     */
    EC(GordianSignatureType.DSA, GordianSignatureType.DDSA, GordianSignatureType.NR),

    /**
     * DSA.
     */
    DSA(GordianSignatureType.DSA, GordianSignatureType.DDSA),

    /**
     * DiffieHellman.
     */
    DIFFIEHELLMAN(),

    /**
     * SM2.
     */
    SM2(GordianSignatureType.NATIVE),

    /**
     * DSTU4145.
     */
    DSTU4145(GordianSignatureType.NATIVE),

    /**
     * GOST2012.
     */
    GOST2012(GordianSignatureType.NATIVE),

    /**
     * EdwardsXDH25519.
     */
    X25519(),

    /**
     * EdwardsXDH448.
     */
    X448(),

    /**
     * EdwardsDSA25519.
     */
    ED25519(GordianSignatureType.PREHASH, GordianSignatureType.PURE, GordianSignatureType.NATIVE),

    /**
     * EdwardsDSA448.
     */
    ED448(GordianSignatureType.PREHASH, GordianSignatureType.PURE),

    /**
     * SPHINCS.
     */
    SPHINCS(GordianSignatureType.PREHASH),

    /**
     * McEliece.
     */
    MCELIECE(),

    /**
     * Rainbow.
     */
    RAINBOW(GordianSignatureType.NATIVE),

    /**
     * XMSS.
     */
    XMSS(GordianSignatureType.PREHASH, GordianSignatureType.PURE),

    /**
     * XMSSMT.
     */
    XMSSMT(GordianSignatureType.PREHASH, GordianSignatureType.PURE),

    /**
     * NewHope.
     */
    NEWHOPE(),

    /**
     * qTESLA.
     */
    QTESLA(GordianSignatureType.PURE);

    /**
     * The Supported SignatureTypes.
     */
    private final GordianSignatureType[] theSignatures;

    /**
     * Constructor.
     * @param pSignatures the supported signatures
     */
    GordianAsymKeyType(final GordianSignatureType... pSignatures) {
        theSignatures = pSignatures;
    }

    /**
     * Obtain supported signatures.
     * @return the supported signatures
     */
    public GordianSignatureType[] getSupportedSignatures() {
        return theSignatures;
    }

    /**
     * is signature available?
     * @param pSignature the signatureType
     * @return true/false
     */
    public boolean isSignatureAvailable(final GordianSignatureType pSignature) {
        for (final GordianSignatureType myType : theSignatures) {
            if (myType.equals(pSignature)) {
                return true;
            }
        }
        return false;
    }

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
}
