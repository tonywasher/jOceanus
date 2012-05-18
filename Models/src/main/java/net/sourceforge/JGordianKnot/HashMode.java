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

import net.sourceforge.JDataWalker.ModelException;
import net.sourceforge.JDataWalker.ModelException.ExceptionClass;
import net.sourceforge.JDataWalker.ReportFields;
import net.sourceforge.JDataWalker.ReportFields.ReportField;

public class HashMode extends SecurityMode {
    /**
     * Report fields
     */
    protected static final ReportFields theFields = new ReportFields(AsymKeyMode.class.getSimpleName(),
            SecurityMode.theFields);

    /* Field IDs */
    public static final ReportField FIELD_PRIMETYPE = theFields.declareLocalField("PrimeDigest");
    public static final ReportField FIELD_ALTTYPE = theFields.declareLocalField("AlternateDigest");
    public static final ReportField FIELD_SECRETTYPE = theFields.declareLocalField("SecretDigest");
    public static final ReportField FIELD_CIPHER = theFields.declareLocalField("CipherDigest");
    public static final ReportField FIELD_SWITCH = theFields.declareLocalField("SwitchAdjust");
    public static final ReportField FIELD_FINAL = theFields.declareLocalField("FinalAdjust");

    @Override
    public ReportFields getReportFields() {
        return theFields;
    }

    @Override
    public Object getFieldValue(ReportField pField) {
        if (pField == FIELD_PRIMETYPE)
            return thePrimeDigest;
        if (pField == FIELD_ALTTYPE)
            return theAlternateDigest;
        if (pField == FIELD_SECRETTYPE)
            return theSecretDigest;
        if (pField == FIELD_CIPHER)
            return theCipherDigest;
        if (pField == FIELD_SWITCH)
            return theSwitchAdjust;
        if (pField == FIELD_FINAL)
            return theFinalAdjust;
        return null;
    }

    @Override
    public String getObjectSummary() {
        return theFields.getName();
    }

    /**
     * The locations (in nybbles)
     */
    private final static int placePRIMETYPE = 0;
    private final static int placeALTTYPE = 1;
    private final static int placeSECRETTYPE = 2;
    private final static int placeCIPHERTYPE = 3;
    private final static int placeSWITCH = 4;
    private final static int placeFINAL = 5;

    /**
     * The various versions
     */
    private final static short versionCURRENT = 1;

    /**
     * The Prime Digest type
     */
    private final DigestType thePrimeDigest;

    /**
     * The Alternate Digest type
     */
    private final DigestType theAlternateDigest;

    /**
     * The Secret Digest type
     */
    private final DigestType theSecretDigest;

    /**
     * The Cipher Digest type
     */
    private final DigestType theCipherDigest;

    /**
     * The Switch adjustment
     */
    private final int theSwitchAdjust;

    /**
     * The Final adjustment
     */
    private final int theFinalAdjust;

    /**
     * Obtain the Prime Digest type
     * @return the digest type
     */
    public DigestType getPrimeDigest() {
        return thePrimeDigest;
    }

    /**
     * Obtain the Alternate Digest type
     * @return the digest type
     */
    public DigestType getAlternateDigest() {
        return theAlternateDigest;
    }

    /**
     * Obtain the Secret Digest type
     * @return the digest type
     */
    public DigestType getSecretDigest() {
        return theSecretDigest;
    }

    /**
     * Obtain the Cipher Digest type
     * @return the digest type
     */
    public DigestType getCipherDigest() {
        return theCipherDigest;
    }

    /**
     * Obtain the Switch Adjustment
     * @return the switch adjustment
     */
    public int getSwitchAdjust() {
        return theSwitchAdjust;
    }

    /**
     * Obtain the Final Adjustment
     * @return the final adjustment
     */
    public int getFinalAdjust() {
        return theFinalAdjust;
    }

    /**
     * Constructor at random
     * @param useRestricted use restricted keys
     * @param pRandom the random generator
     * @throws ModelException
     */
    protected HashMode(boolean useRestricted, SecureRandom pRandom) throws ModelException {
        /* Access a random set of DigestTypes */
        DigestType[] myDigest = DigestType.getRandomTypes(3, pRandom);
        DigestType[] mySetDigest = DigestType.getRandomTypes(1, pRandom);

        /* Store Digest types */
        thePrimeDigest = myDigest[0];
        theAlternateDigest = myDigest[1];
        theSecretDigest = myDigest[2];
        theCipherDigest = mySetDigest[0];

        /* Access random adjustment values */
        theSwitchAdjust = 1 + pRandom.nextInt(15);
        theFinalAdjust = 1 + pRandom.nextInt(15);

        /* Set the version */
        setVersion(versionCURRENT);

        /* Set restricted flags */
        setRestricted(useRestricted);

        /* encode the keyMode */
        encodeKeyMode();
    }

    /**
     * Constructor from encoded format
     * @param pEncoded the encoded format
     * @throws ModelException
     */
    protected HashMode(byte[] pEncoded) throws ModelException {
        /* Set the initial encoded version */
        setEncoded(pEncoded);

        /* Not allowed unless version is current */
        if (getVersion() != versionCURRENT)
            throw new ModelException(ExceptionClass.LOGIC, "Invalid mode version: " + getVersion());

        /* Store Key type and digest */
        thePrimeDigest = DigestType.fromId(getDataValue(placePRIMETYPE));
        theAlternateDigest = DigestType.fromId(getDataValue(placeALTTYPE));
        theSecretDigest = DigestType.fromId(getDataValue(placeSECRETTYPE));
        theCipherDigest = DigestType.fromId(getDataValue(placeCIPHERTYPE));

        /* Access the adjustments */
        theSwitchAdjust = getDataValue(placeSWITCH);
        theFinalAdjust = getDataValue(placeFINAL);

        /* Re-encode the key mode */
        encodeKeyMode();
    }

    /**
     * Encode the key mode
     */
    private void encodeKeyMode() {
        /* Allocate the encoded array */
        allocateEncoded(placeFINAL);

        /* Set the values */
        setDataValue(placePRIMETYPE, thePrimeDigest.getId());
        setDataValue(placeALTTYPE, theAlternateDigest.getId());
        setDataValue(placeSECRETTYPE, theSecretDigest.getId());
        setDataValue(placeCIPHERTYPE, theCipherDigest.getId());
        setDataValue(placeSWITCH, theSwitchAdjust);
        setDataValue(placeFINAL, theFinalAdjust);
    }
}
