/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
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
    EC(GordianSignatureType.DSA, GordianSignatureType.DDSA, GordianSignatureType.NR, GordianSignatureType.SM2),

    /**
     * ElGamal.
     */
    ELGAMAL(),

    /**
     * DiffieHellman.
     */
    DIFFIEHELLMAN(),

    /**
     * SPHINCS.
     */
    SPHINCS(GordianSignatureType.NATIVE),

    /**
     * McEliece.
     */
    MCELIECE(),

    /**
     * Rainbow.
     */
    RAINBOW(GordianSignatureType.NATIVE),

    /**
     * NewHope.
     */
    NEWHOPE();

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
        for (GordianSignatureType myType : theSignatures) {
            if (myType.equals(pSignature)) {
                return true;
            }
        }
        return false;
    }
}
