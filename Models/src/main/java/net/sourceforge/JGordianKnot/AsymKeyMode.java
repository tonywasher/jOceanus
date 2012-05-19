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

public class AsymKeyMode extends SecurityMode {
    /**
     * Report fields
     */
    protected static final ReportFields theFields = new ReportFields(AsymKeyMode.class.getSimpleName(),
            SecurityMode.theFields);

    /* Field IDs */
    public static final ReportField FIELD_CIPHER = theFields.declareLocalField("CipherDigest");
    public static final ReportField FIELD_ASYMTYPE = theFields.declareLocalField("AsymType");

    @Override
    public ReportFields getReportFields() {
        return theFields;
    }

    @Override
    public Object getFieldValue(ReportField pField) {
        if (pField == FIELD_CIPHER)
            return theCipherDigest;
        if (pField == FIELD_ASYMTYPE)
            return theAsymKeyType;
        return super.getFieldValue(pField);
    }

    @Override
    public String getObjectSummary() {
        return theFields.getName();
    }

    /**
     * The locations (in nybbles)
     */
    private final static int placeASYMTYPE = 0;
    private final static int placeDIGESTTYPE = 1;

    /**
     * The version
     */
    public static short versionCURRENT = 1;

    /**
     * The Asymmetric KeyType
     */
    private final AsymKeyType theAsymKeyType;

    /**
     * The Cipher Digest type
     */
    private final DigestType theCipherDigest;

    /**
     * Obtain the Asymmetric Key Type
     * @return the key type
     */
    public AsymKeyType getAsymKeyType() {
        return theAsymKeyType;
    }

    /**
     * Obtain the Cipher Digest type
     * @return the digest type
     */
    public DigestType getCipherDigest() {
        return theCipherDigest;
    }

    /**
     * Constructor at random
     * @param useRestricted use restricted keys
     * @param pRandom the random generator
     * @throws ModelException
     */
    protected AsymKeyMode(boolean useRestricted,
                          SecureRandom pRandom) throws ModelException {
        /* Access a random set of Key/DigestTypes */
        AsymKeyType[] myKeyType = AsymKeyType.getRandomTypes(1, pRandom);
        DigestType[] myDigest = DigestType.getRandomTypes(1, pRandom);

        /* Store Key type and digest */
        theAsymKeyType = myKeyType[0];
        theCipherDigest = myDigest[0];

        /* Set the version */
        setVersion(versionCURRENT);

        /* Set restricted flags */
        setRestricted(useRestricted);

        /* encode the keyMode */
        encodeKeyMode();
    }

    /**
     * Constructor from a partner KeyMode
     * @param useRestricted use restricted keys
     * @param pSource the key mode to initialise from
     */
    protected AsymKeyMode(boolean useRestricted,
                          AsymKeyMode pSource) {
        /* Store Key type and digest */
        theAsymKeyType = pSource.getAsymKeyType();
        theCipherDigest = pSource.getCipherDigest();

        /* Set the version */
        setVersion(versionCURRENT);

        /* Use restricted if local or source requires it */
        setRestricted(useRestricted || pSource.useRestricted());

        /* encode the keyMode */
        encodeKeyMode();
    }

    /**
     * Constructor from encoded format
     * @param pEncoded the encoded format
     * @throws ModelException
     */
    protected AsymKeyMode(byte[] pEncoded) throws ModelException {
        /* Set the initial encoded version */
        setEncoded(pEncoded);

        /* Not allowed unless version is current */
        if (getVersion() != versionCURRENT)
            throw new ModelException(ExceptionClass.LOGIC, "Invalid mode version: " + getVersion());

        /* Store Key type and digest */
        theAsymKeyType = AsymKeyType.fromId(getDataValue(placeASYMTYPE));
        theCipherDigest = DigestType.fromId(getDataValue(placeDIGESTTYPE));

        /* Re-encode the key mode */
        encodeKeyMode();
    }

    /**
     * Encode the key mode
     */
    private void encodeKeyMode() {
        /* Allocate the encoded array */
        allocateEncoded(placeDIGESTTYPE);

        /* Set the values */
        setDataValue(placeASYMTYPE, theAsymKeyType.getId());
        setDataValue(placeDIGESTTYPE, theCipherDigest.getId());
    }
}
