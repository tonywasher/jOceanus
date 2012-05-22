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

import net.sourceforge.JDataManager.ReportFields;
import net.sourceforge.JDataManager.ReportFields.ReportField;

public abstract class SecurityMode extends NybbleArray {
    /**
     * Report fields
     */
    protected static final ReportFields theFields = new ReportFields(SecurityMode.class.getSimpleName(),
            NybbleArray.theFields);

    /* Field IDs */
    public static final ReportField FIELD_VERSION = theFields.declareLocalField("Version");
    public static final ReportField FIELD_RESTRICT = theFields.declareLocalField("Restricted");

    @Override
    public ReportFields getReportFields() {
        return theFields;
    }

    @Override
    public Object getFieldValue(ReportField pField) {
        if (pField == FIELD_VERSION)
            return theVersion;
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

    @Override
    public void setEncoded(byte[] pEncoded) {
        /* Store value */
        super.setEncoded(pEncoded);

        /* Declare data positions */
        theDataOffset = placeFLAGS + 1;

        /* Obtain version and flags */
        theVersion = (short) getValue(placeVERSION);
        int myFlags = getValue(placeFLAGS);
        useRestricted = ((myFlags & flagRESTRICT) != 0);
    }

    @Override
    protected void allocateEncoded(int iMaxPos) {
        /* Declare data positions */
        theDataOffset = placeFLAGS + 1;

        /* Allocate the encoded array */
        super.allocateEncoded(iMaxPos + theDataOffset);

        /* Encode the header */
        encodeHeader();

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
     * Set value of a nybble in a byte array
     * @param iPos the nybble within the array
     * @param pValue the value to set
     */
    protected void setDataValue(int iPos,
                                int pValue) {
        /* Adjust for data offset */
        setValue(iPos + theDataOffset, pValue);
    }
}
