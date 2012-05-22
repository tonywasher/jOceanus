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

import java.security.SecureRandom;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JDataManager.ReportFields;
import net.sourceforge.JDataManager.ReportFields.ReportField;

public class EncryptionMode extends NybbleArray {
    /**
     * Report fields
     */
    protected static final ReportFields theFields = new ReportFields(EncryptionMode.class.getSimpleName(),
            NybbleArray.theFields);

    /**
     * Report fields
     */
    private final ReportFields theLocalFields;

    /* Called from constructor */
    /**
     * Allocate local fields
     * @return the local fields
     */
    private static ReportFields declareFields() {
        return new ReportFields(theFields.getName(), theFields);
    }

    /* Field IDs */
    public static final String FIELD_SYMKEY = "SymKey";

    @Override
    public ReportFields getReportFields() {
        return theLocalFields;
    }

    @Override
    public Object getFieldValue(ReportField pField) {
        /* If the field is a symKey, handle specially */
        if (pField.getAnchor() == theLocalFields) {
            if (pField.getName().startsWith(FIELD_SYMKEY)) {
                /* Return the relevant SymKeyType */
                String myId = pField.getName().substring(FIELD_SYMKEY.length());
                return theKeyTypes[Integer.parseInt(myId) - 1];
            }
        }
        return super.getFieldValue(pField);
    }

    @Override
    public String getObjectSummary() {
        return theFields.getName();
    }

    /**
     * The locations (in nybbles)
     */
    private final static int placeNUMTYPES = 0;
    private final static int placeSYMKEY = 1;

    /**
     * The SymKeyTypes
     */
    private final SymKeyType[] theKeyTypes;

    /**
     * Obtain the SymKeyTypes
     * @return the SymKeyTypes
     */
    public SymKeyType[] getSymKeyTypes() {
        return theKeyTypes;
    }

    /**
     * Constructor at random
     * @param pNumEncrypts the number of keys
     * @param pRandom the random generator
     * @throws ModelException
     */
    protected EncryptionMode(int pNumEncrypts,
                             SecureRandom pRandom) throws ModelException {
        /* Declare local fields */
        theLocalFields = declareFields();

        /* Access a random set of Key/DigestTypes */
        theKeyTypes = SymKeyType.getRandomTypes(pNumEncrypts, pRandom);

        /* encode the keyMode */
        encodeKeyMode();
    }

    /**
     * Constructor from encoded format
     * @param pEncoded the encoded format
     * @throws ModelException
     */
    protected EncryptionMode(byte[] pEncoded) throws ModelException {
        /* Declare local fields */
        theLocalFields = declareFields();

        /* Set the initial encoded version */
        setEncoded(pEncoded);

        /* Obtain number of key types */
        int iNumEncrypts = getValue(placeNUMTYPES);
        if (iNumEncrypts > SymKeyType.values().length)
            throw new ModelException(ExceptionClass.LOGIC, "Invalid number of encryption steps: "
                    + iNumEncrypts);

        /* Allocate the array */
        theKeyTypes = new SymKeyType[iNumEncrypts];

        /* Loop through the key types */
        for (int i = 0; i < iNumEncrypts; i++) {
            /* Pick up the key type */
            theKeyTypes[i] = SymKeyType.fromId(getValue(placeSYMKEY + i));
        }

        /* Re-encode the key mode */
        encodeKeyMode();
    }

    /**
     * Encode the key mode
     */
    private void encodeKeyMode() {
        int iNumEncrypts = theKeyTypes.length;

        /* Allocate the encoded array */
        allocateEncoded(placeSYMKEY + iNumEncrypts - 1);

        /* Set the number of keys */
        setValue(placeNUMTYPES, iNumEncrypts);

        /* Loop through the key types */
        for (int i = 0; i < iNumEncrypts; i++) {
            /* Set the key type */
            setValue(placeSYMKEY + i, theKeyTypes[i].getId());

            /* Declare the field */
            theLocalFields.declareLocalField(FIELD_SYMKEY + (i + 1));
        }
    }
}
