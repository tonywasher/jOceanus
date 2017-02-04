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
    public GordianAsymKeySpec(final GordianAsymKeyType pKeyType) {
        this(pKeyType, null);
    }

    /**
     * Constructor.
     * @param pKeyType the keyType
     * @param pLength the length
     */
    public GordianAsymKeySpec(final GordianAsymKeyType pKeyType,
                              final GordianModulus pLength) {
        theKeyType = pKeyType;
        theLength = pLength;
        theCurve = null;
    }

    /**
     * Constructor.
     * @param pCurve the curve
     */
    public GordianAsymKeySpec(final GordianElliptic pCurve) {
        theKeyType = GordianAsymKeyType.EC;
        theLength = null;
        theCurve = pCurve;
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

        /* Access the target Key */
        GordianAsymKeySpec myThat = (GordianAsymKeySpec) pThat;

        /* Check KeyType */
        if (theKeyType != myThat.getKeyType()) {
            return false;
        }

        /* Match subfields */
        if (theLength != null) {
            return theLength.equals(myThat.getModulus());
        }
        if (theCurve != null) {
            return theCurve.equals(myThat.getCurve());
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = theKeyType.ordinal() << TethysDataConverter.BYTE_SHIFT;
        if (theLength != null) {
            hashCode += theLength.ordinal();
        }
        if (theCurve != null) {
            hashCode += theCurve.ordinal();
        }
        return hashCode;
    }
}
