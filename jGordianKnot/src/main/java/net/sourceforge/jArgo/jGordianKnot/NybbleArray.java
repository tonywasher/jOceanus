/*******************************************************************************
 * JGordianKnot: Security Suite
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JGordianKnot;

import java.util.Arrays;

import net.sourceforge.JDataManager.DataConverter;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;

/**
 * An array of bytes treated as an array of nybbles (i.e. two entries per byte).
 * @author Tony Washer
 */
public class NybbleArray implements JDataContents {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(NybbleArray.class.getSimpleName());

    /**
     * Encoded array Field ID.
     */
    public static final JDataField FIELD_ENCODED = FIELD_DEFS.declareEqualityField("Encoded");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ENCODED.equals(pField)) {
            return Arrays.copyOf(theEncoded, theEncoded.length);
        }
        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * The mask shift.
     */
    private static final int MASK_SHIFT = DataConverter.NYBBLE_SHIFT;

    /**
     * The low value mask.
     */
    private static final int MASK_LOW = DataConverter.NYBBLE_MASK;

    /**
     * The high value mask.
     */
    private static final int MASK_HIGH = MASK_LOW << MASK_SHIFT;

    /**
     * The encoded format.
     */
    private byte[] theEncoded;

    /**
     * Obtain the Encoded format.
     * @return the encoded format
     */
    protected byte[] getEncoded() {
        return theEncoded;
    }

    /**
     * Set the encoded value.
     * @param pEncoded the encoded array
     */
    protected void setEncoded(final byte[] pEncoded) {
        /* Store value */
        theEncoded = Arrays.copyOf(pEncoded, pEncoded.length);
    }

    /**
     * Allocate encoded value.
     * @param iMaxPos the maximum data position
     */
    protected void allocateEncoded(final int iMaxPos) {
        /* Allocate the encoded array */
        int encodeLen = 1 + (iMaxPos / 2);
        theEncoded = new byte[encodeLen];
        Arrays.fill(theEncoded, (byte) 0);
    }

    /**
     * Obtain value of a nybble in a byte.
     * @param iPos the nybble within the array
     * @return the nybble
     */
    protected short getValue(final int iPos) {
        /* Obtain the relevant byte from the array */
        byte myByte = theEncoded[iPos / 2];

        /* Determine whether this is high/low nybble */
        boolean bHigh = ((iPos % 2) == 0);

        /* Return the relevant nybble */
        return (short) ((bHigh) ? ((myByte >> MASK_SHIFT) & MASK_LOW) : (myByte & MASK_LOW));
    }

    /**
     * Set value of a nybble in a byte array.
     * @param iPos the nybble within the array
     * @param pValue the value to set
     */
    protected void setValue(final int iPos,
                            final int pValue) {
        /* Calculate the position in the array */
        int myPos = iPos / 2;

        /* Ensure that it is within range */
        if (myPos >= theEncoded.length) {
            throw new IndexOutOfBoundsException("Invalid data position");
        }

        /* Ensure that it is within range */
        if ((pValue > MASK_LOW) || (pValue < 0)) {
            throw new IllegalArgumentException("Invalid value - " + pValue);
        }

        /* Obtain the relevant byte from the array */
        byte myByte = theEncoded[myPos];

        /* Determine whether this is high/low nybble */
        boolean bHigh = ((iPos % 2) == 0);

        /* If this is a high nybble */
        if (bHigh) {
            /* Set value into byte */
            myByte &= MASK_LOW;
            myByte |= (pValue << MASK_SHIFT);

            /* else this is a low nybble */
        } else {
            /* Set value into byte */
            myByte &= MASK_HIGH;
            myByte |= (pValue & MASK_LOW);
        }

        /* Store value back into array */
        theEncoded[myPos] = myByte;
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

        /* Make sure that the object is the same class */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the object as a NybbleArray */
        NybbleArray myArray = (NybbleArray) pThat;

        /* Determine equality */
        return Arrays.equals(getEncoded(), myArray.getEncoded());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(theEncoded);
    }
}
