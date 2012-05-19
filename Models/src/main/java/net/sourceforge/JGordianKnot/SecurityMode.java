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

public abstract class SecurityMode implements ReportDetail {
    /**
     * Report fields
     */
    protected static final ReportFields theFields = new ReportFields(SecurityMode.class.getSimpleName());

    /* Field IDs */
    public static final ReportField FIELD_RESTRICT = theFields.declareLocalField("Restricted");
    public static final ReportField FIELD_ENCODED = theFields.declareLocalField("Encoded");
    public static final ReportField FIELD_LENGTH = theFields.declareLocalField("ParsedLength");

    @Override
    public ReportFields getReportFields() {
        return theFields;
    }

    @Override
    public Object getFieldValue(ReportField pField) {
        if (pField == FIELD_ENCODED)
            return theEncoded;
        if (pField == FIELD_RESTRICT)
            return useRestricted;
        return null;
    }

    @Override
    public String getObjectSummary() {
        return theFields.getName();
    }

    /**
     * The locations (in units of 4-bit shifts)
     */
    private final static int placeVERSION = 0;
    private final static int placeFLAGS = 1;
    protected final static int placeDATA = 2;

    /**
     * The various flags
     */
    private final static short flagRESTRICT = 4;

    /**
     * Version of Mode
     */
    private short theVersion;

    /**
     * Use restricted security
     */
    private boolean useRestricted;

    /**
     * flags
     */
    private short theFlags;

    /**
     * The encoded format
     */
    private byte[] theEncoded;

    /**
     * The data offset
     */
    private int theDataOffset;

    /**
     * Get the data version
     * @return the data version
     */
    public short getVersion() {
        return theVersion;
    }

    /**
     * Should we use restricted keys
     * @return true/false
     */
    public boolean useRestricted() {
        return useRestricted;
    }

    /**
     * Obtain the Encoded format
     * @return the encoded format
     */
    public byte[] getEncoded() {
        return theEncoded;
    }

    /**
     * Set the data version
     * @param pVers the data version
     */
    public void setVersion(short pVers) {
        /* Store value */
        theVersion = pVers;
    }

    /**
     * Set the restricted flag
     * @param bRestricted do we us restricted keys
     */
    public void setRestricted(boolean bRestricted) {
        /* Store value */
        useRestricted = bRestricted;
        encodeHeader();
    }

    /**
     * Encode header
     */
    private void encodeHeader() {
        /* Build flags value */
        theFlags = (useRestricted ? flagRESTRICT : 0);
    }

    /**
     * Set the encoded value
     * @param pEncoded the encoded array
     */
    public void setEncoded(byte[] pEncoded) {
        /* Store value */
        theEncoded = pEncoded;

        /* Declare data positions */
        theDataOffset = placeFLAGS + 1;

        /* Obtain version and flags */
        theVersion = (short) getValue(placeVERSION);
        int myFlags = getValue(placeFLAGS);
        useRestricted = ((myFlags & flagRESTRICT) != 0);
    }

    /**
     * Allocate encoded value
     * @param iMaxPos the maximum data position
     */
    protected void allocateEncoded(int iMaxPos) {
        /* Encode the header */
        encodeHeader();

        /* Declare data positions */
        theDataOffset = placeFLAGS + 1;

        /* Allocate the encoded array */
        int encodeLen = 1 + ((iMaxPos + theDataOffset) / 2);
        theEncoded = new byte[encodeLen];
        Arrays.fill(theEncoded, (byte) 0);

        /* Store version and flags */
        setValue(placeVERSION, theVersion);
        setValue(placeFLAGS, theFlags);
    }

    /**
     * Obtain value of a nybble in a byte
     * @param iPos the nybble within the array
     * @return the nybble
     */
    protected short getDataValue(int iPos) {
        /* Adjust for data offset */
        return getValue(iPos + theDataOffset);
    }

    /**
     * Obtain value of a nybble in a byte
     * @param iPos the nybble within the array
     * @return the nybble
     */
    private short getValue(int iPos) {
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
    protected void setDataValue(int iPos,
                                int pValue) {
        /* Adjust for data offset */
        setValue(iPos + theDataOffset, pValue);
    }

    /**
     * Set value of a nybble in a byte array
     * @param iPos the nybble within the array
     * @param pValue the value to set
     */
    private void setValue(int iPos,
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

        /* Access the object as a SecurityMode */
        SecurityMode myMode = (SecurityMode) pThat;

        /* Determine equality */
        return Arrays.equals(getEncoded(), myMode.getEncoded());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(theEncoded);
    }

    /**
     * Are the two modes different
     * @param pFirst first mode
     * @param pSecond second mode
     * @return true/false
     */
    public static boolean isDifferent(SecurityMode pFirst,
                                      SecurityMode pSecond) {
        /* Handle nulls */
        if (pFirst == null)
            return (pSecond != null);
        if (pSecond == null)
            return true;

        /* Make sure that they are the same class */
        if (pFirst.getClass() != pSecond.getClass())
            return true;

        /* Return difference in modes */
        return Arrays.equals(pFirst.getEncoded(), pSecond.getEncoded());
    }
}
