/*******************************************************************************
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

import net.sourceforge.JDataManager.ReportFields;
import net.sourceforge.JDataManager.ReportFields.ReportField;
import net.sourceforge.JDataManager.ReportObject.ReportDetail;

public class NybbleArray implements ReportDetail {
    /**
     * Report fields
     */
    protected static final ReportFields theFields = new ReportFields(NybbleArray.class.getSimpleName());

    /* Field IDs */
    public static final ReportField FIELD_ENCODED = theFields.declareEqualityField("Encoded");

    @Override
    public ReportFields getReportFields() {
        return theFields;
    }

    @Override
    public Object getFieldValue(ReportField pField) {
        if (pField == FIELD_ENCODED)
            return theEncoded;
        return null;
    }

    @Override
    public String getObjectSummary() {
        return theFields.getName();
    }

    /**
     * The encoded format
     */
    private byte[] theEncoded;

    /**
     * Obtain the Encoded format
     * @return the encoded format
     */
    public byte[] getEncoded() {
        return theEncoded;
    }

    /**
     * Set the encoded value
     * @param pEncoded the encoded array
     */
    public void setEncoded(byte[] pEncoded) {
        /* Store value */
        theEncoded = pEncoded;
    }

    /**
     * Allocate encoded value
     * @param iMaxPos the maximum data position
     */
    protected void allocateEncoded(int iMaxPos) {
        /* Allocate the encoded array */
        int encodeLen = 1 + (iMaxPos / 2);
        theEncoded = new byte[encodeLen];
        Arrays.fill(theEncoded, (byte) 0);
    }

    /**
     * Obtain value of a nybble in a byte
     * @param iPos the nybble within the array
     * @return the nybble
     */
    protected short getValue(int iPos) {
        /* Obtain the relevant byte from the array */
        byte myByte = theEncoded[iPos / 2];

        /* Determine whether this is high/low nybble */
        boolean bHigh = ((iPos % 2) == 0);

        /* Return the relevant nybble */
        return (short) ((bHigh) ? ((myByte >> 4) & 0xF) : (myByte & 0xF));
    }

    /**
     * Set value of a nybble in a byte array
     * @param iPos the nybble within the array
     * @param pValue the value to set
     */
    protected void setValue(int iPos,
                            int pValue) {
        /* Calculate the position in the array */
        int myPos = iPos / 2;

        /* Ensure that it is within range */
        if (myPos >= theEncoded.length)
            throw new IndexOutOfBoundsException("Invalid data position");

        /* Ensure that it is within range */
        if ((pValue > 0xF) || (pValue < 0))
            throw new IllegalArgumentException("Invalid value - " + pValue);

        /* Obtain the relevant byte from the array */
        byte myByte = theEncoded[myPos];

        /* Determine whether this is high/low nybble */
        boolean bHigh = ((iPos % 2) == 0);

        /* If this is a high nybble */
        if (bHigh) {
            /* Set value into byte */
            myByte &= 0xF;
            myByte |= (pValue << 4);
        }

        /* else this is a low nybble */
        else {
            /* Set value into byte */
            myByte &= 0xF0;
            myByte |= (pValue & 0xF);
        }

        /* Store value back into array */
        theEncoded[myPos] = myByte;
    }

    @Override
    public boolean equals(Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat)
            return true;
        if (pThat == null)
            return false;

        /* Make sure that the object is the same class */
        if (pThat.getClass() != this.getClass())
            return false;

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
