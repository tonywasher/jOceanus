/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
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

import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Signature Specification.
 */
public class GordianSignatureSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * ASymKeyType.
     */
    private final GordianAsymKeyType theAsymKeyType;

    /**
     * SignatureType.
     */
    private final GordianSignatureType theSignatureType;

    /**
     * DigestSpec.
     */
    private final GordianDigestSpec theDigestSpec;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pAsymKeyType the asymKeyType
     * @param pDigestSpec the digestSpec
     */
    protected GordianSignatureSpec(final GordianAsymKeyType pAsymKeyType,
                                   final GordianDigestSpec pDigestSpec) {
        /* Store parameters */
        this(pAsymKeyType, GordianSignatureType.NATIVE, pDigestSpec);
    }

    /**
     * Constructor.
     * @param pAsymKeyType the asymKeyType
     * @param pSignatureType the signatureType
     * @param pDigestSpec the digestSpec
     */
    protected GordianSignatureSpec(final GordianAsymKeyType pAsymKeyType,
                                   final GordianSignatureType pSignatureType,
                                   final GordianDigestSpec pDigestSpec) {
        /* Store parameters */
        theAsymKeyType = pAsymKeyType;
        theSignatureType = pSignatureType;
        theDigestSpec = pDigestSpec;
    }

    /**
     * Create RSASpec.
     * @param pSignatureType the signatureType
     * @param pDigestSpec the digestSpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec rsa(final GordianSignatureType pSignatureType,
                                           final GordianDigestSpec pDigestSpec) {
        return new GordianSignatureSpec(GordianAsymKeyType.RSA, pSignatureType, pDigestSpec);
    }

    /**
     * Create ECSpec.
     * @param pSignatureType the signatureType
     * @param pDigestSpec the digestSpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec ec(final GordianSignatureType pSignatureType,
                                          final GordianDigestSpec pDigestSpec) {
        return new GordianSignatureSpec(GordianAsymKeyType.EC, pSignatureType, pDigestSpec);
    }

    /**
     * Create SPHINCSSpec.
     * @param pDigestSpec the digestSpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec sphincs(final GordianDigestSpec pDigestSpec) {
        return new GordianSignatureSpec(GordianAsymKeyType.SPHINCS, pDigestSpec);
    }

    /**
     * Create RainbowSpec.
     * @param pDigestSpec the digestSpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec rainbow(final GordianDigestSpec pDigestSpec) {
        return new GordianSignatureSpec(GordianAsymKeyType.RAINBOW, pDigestSpec);
    }

    /**
     * Obtain the AsymKeyType.
     * @return the asymKeyType.
     */
    public GordianAsymKeyType getAsymKeyType() {
        return theAsymKeyType;
    }

    /**
     * Obtain the SignatureType.
     * @return the signatureType.
     */
    public GordianSignatureType getSignatureType() {
        return theSignatureType;
    }

    /**
     * Obtain the DigestSpec.
     * @return the digestSpec.
     */
    public GordianDigestSpec getDigestSpec() {
        return theDigestSpec;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = theAsymKeyType.toString();
            if (theSignatureType != GordianSignatureType.NATIVE) {
                theName += SEP + theSignatureType.toString();
            }
            theName += SEP + theDigestSpec.toString();
        }

        /* return the name */
        return theName;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a SignatureSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target SignatureSpec */
        GordianSignatureSpec myThat = (GordianSignatureSpec) pThat;

        /* Check AsymKeyType and signatureType */
        if (theAsymKeyType != myThat.getAsymKeyType()
            || theSignatureType != myThat.getSignatureType()) {
            return false;
        }

        /* Match digestSpec */
        return theDigestSpec.equals(myThat.getDigestSpec());
    }

    @Override
    public int hashCode() {
        int hashCode = theAsymKeyType.hashCode() << TethysDataConverter.BYTE_SHIFT;
        hashCode += theSignatureType.hashCode();
        hashCode <<= TethysDataConverter.BYTE_SHIFT;
        hashCode += theDigestSpec.hashCode();
        return hashCode;
    }
}
