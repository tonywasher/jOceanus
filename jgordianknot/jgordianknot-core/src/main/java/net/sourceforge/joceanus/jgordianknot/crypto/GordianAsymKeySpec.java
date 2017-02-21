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

import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Asymmetric Key Specification.
 */
public class GordianAsymKeySpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The AsymKeyType.
     */
    private final GordianAsymKeyType theKeyType;

    /**
     * The Modulus Length.
     */
    private final GordianModulus theLength;

    /**
     * The Curve.
     */
    private final GordianElliptic theCurve;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pKeyType the keyType
     */
    protected GordianAsymKeySpec(final GordianAsymKeyType pKeyType) {
        this(pKeyType, null);
    }

    /**
     * Constructor.
     * @param pKeyType the keyType
     * @param pLength the length
     */
    protected GordianAsymKeySpec(final GordianAsymKeyType pKeyType,
                                 final GordianModulus pLength) {
        theKeyType = pKeyType;
        theLength = pLength;
        theCurve = null;
    }

    /**
     * Constructor.
     * @param pCurve the curve
     */
    protected GordianAsymKeySpec(final GordianElliptic pCurve) {
        theKeyType = GordianAsymKeyType.EC;
        theLength = null;
        theCurve = pCurve;
    }

    /**
     * Create RSAKey.
     * @param pModulus the modulus
     * @return the KeySpec
     */
    public static GordianAsymKeySpec rsa(final GordianModulus pModulus) {
        return new GordianAsymKeySpec(GordianAsymKeyType.RSA, pModulus);
    }

    /**
     * Create ECKey.
     * @param pCurve the curve
     * @return the KeySpec
     */
    public static GordianAsymKeySpec ec(final GordianElliptic pCurve) {
        return new GordianAsymKeySpec(pCurve);
    }

    /**
     * Create DHKey.
     * @param pModulus the modulus
     * @return the KeySpec
     */
    public static GordianAsymKeySpec dh(final GordianModulus pModulus) {
        return new GordianAsymKeySpec(GordianAsymKeyType.DIFFIEHELLMAN, pModulus);
    }

    /**
     * Create SPHINCSKey.
     * @return the KeySpec
     */
    public static GordianAsymKeySpec sphincs() {
        return new GordianAsymKeySpec(GordianAsymKeyType.SPHINCS);
    }

    /**
     * Create RainbowKey.
     * @return the KeySpec
     */
    public static GordianAsymKeySpec rainbow() {
        return new GordianAsymKeySpec(GordianAsymKeyType.RAINBOW);
    }

    /**
     * Create NewHopeKey.
     * @return the KeySpec
     */
    public static GordianAsymKeySpec newHope() {
        return new GordianAsymKeySpec(GordianAsymKeyType.NEWHOPE);
    }

    /**
     * Obtain the keyType.
     * @return the keyType.
     */
    public GordianAsymKeyType getKeyType() {
        return theKeyType;
    }

    /**
     * Obtain the modulus length.
     * @return the length.
     */
    public GordianModulus getModulus() {
        return theLength;
    }

    /**
     * Obtain the curve.
     * @return the curve.
     */
    public GordianElliptic getCurve() {
        return theCurve;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = theKeyType.toString();
            if (theLength != null) {
                theName += SEP + theLength.toString();
            } else if (theCurve != null) {
                theName += SEP + theCurve.toString();
            }
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

        /* Make sure that the object is an AsymSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target KeySpec */
        GordianAsymKeySpec myThat = (GordianAsymKeySpec) pThat;

        /* Check KeyType */
        if (theKeyType != myThat.getKeyType()) {
            return false;
        }

        /* Match subfields */
        return theLength == myThat.getModulus()
               && theCurve == myThat.getCurve();
    }

    @Override
    public int hashCode() {
        int hashCode = theKeyType.ordinal() + 1 << TethysDataConverter.BYTE_SHIFT;
        if (theLength != null) {
            hashCode += theLength.ordinal() + 1;
        } else if (theCurve != null) {
            hashCode += theCurve.ordinal() + 1;
        }
        return hashCode;
    }
}
