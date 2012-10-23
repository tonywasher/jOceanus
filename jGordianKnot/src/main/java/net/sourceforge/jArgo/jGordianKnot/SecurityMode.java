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
package net.sourceforge.jArgo.jGordianKnot;

import net.sourceforge.jArgo.jDataManager.JDataFields;
import net.sourceforge.jArgo.jDataManager.JDataFields.JDataField;

/**
 * Security mode base class.
 * @author Tony Washer
 */
public abstract class SecurityMode extends NybbleArray {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(SecurityMode.class.getSimpleName(),
            NybbleArray.FIELD_DEFS);

    /**
     * Version Field ID.
     */
    public static final JDataField FIELD_VERSION = FIELD_DEFS.declareLocalField("Version");

    /**
     * Restricted Field ID.
     */
    public static final JDataField FIELD_RESTRICT = FIELD_DEFS.declareLocalField("Restricted");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_VERSION.equals(pField)) {
            return theVersion;
        }
        if (FIELD_RESTRICT.equals(pField)) {
            return useRestricted;
        }
        return super.getFieldValue(pField);
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * The Version location (in units of 4-bit shifts).
     */
    private static final int PLACE_VERSION = 0;

    /**
     * The Flags location (in units of 4-bit shifts).
     */
    private static final int PLACE_FLAGS = 1;

    /**
     * The various flags.
     */
    private static final short FLAG_RESTRICT = 4;

    /**
     * Version of Mode.
     */
    private short theVersion;

    /**
     * Use restricted security.
     */
    private boolean useRestricted;

    /**
     * flags.
     */
    private short theFlags;

    /**
     * The data offset.
     */
    private int theDataOffset;

    /**
     * Get the data version.
     * @return the data version
     */
    public short getVersion() {
        return theVersion;
    }

    /**
     * Should we use restricted keys.
     * @return true/false
     */
    public boolean useRestricted() {
        return useRestricted;
    }

    /**
     * Set the data version.
     * @param pVers the data version
     */
    public void setVersion(final short pVers) {
        /* Store value */
        theVersion = pVers;
    }

    /**
     * Set the restricted flag.
     * @param bRestricted do we use restricted keys
     */
    public void setRestricted(final boolean bRestricted) {
        /* Store value */
        useRestricted = bRestricted;
        encodeHeader();
    }

    /**
     * Encode header.
     */
    private void encodeHeader() {
        /* Build flags value */
        theFlags = (useRestricted ? FLAG_RESTRICT : 0);
    }

    @Override
    public void setEncoded(final byte[] pEncoded) {
        /* Store value */
        super.setEncoded(pEncoded);

        /* Declare data positions */
        theDataOffset = PLACE_FLAGS + 1;

        /* Obtain version and flags */
        theVersion = (short) getValue(PLACE_VERSION);
        int myFlags = getValue(PLACE_FLAGS);
        useRestricted = ((myFlags & FLAG_RESTRICT) != 0);
    }

    @Override
    protected void allocateEncoded(final int iMaxPos) {
        /* Declare data positions */
        theDataOffset = PLACE_FLAGS + 1;

        /* Allocate the encoded array */
        super.allocateEncoded(iMaxPos + theDataOffset);

        /* Encode the header */
        encodeHeader();

        /* Store version and flags */
        setValue(PLACE_VERSION, theVersion);
        setValue(PLACE_FLAGS, theFlags);
    }

    /**
     * Obtain value of a nybble in a byte.
     * @param iPos the nybble within the array
     * @return the nybble
     */
    protected short getDataValue(final int iPos) {
        /* Adjust for data offset */
        return getValue(iPos + theDataOffset);
    }

    /**
     * Set value of a nybble in a byte array.
     * @param iPos the nybble within the array
     * @param pValue the value to set
     */
    protected void setDataValue(final int iPos,
                                final int pValue) {
        /* Adjust for data offset */
        setValue(iPos + theDataOffset, pValue);
    }
}
