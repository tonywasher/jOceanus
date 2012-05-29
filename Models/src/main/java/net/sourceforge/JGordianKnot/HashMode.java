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

import java.security.SecureRandom;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JDataManager.ReportFields;
import net.sourceforge.JDataManager.ReportFields.ReportField;

/**
 * Hash Mode. Encapsulates PasswordHash options.
 * @author Tony Washer
 */
public class HashMode extends SecurityMode {
    /**
     * Report fields.
     */
    protected static final ReportFields FIELD_DEFS = new ReportFields(AsymKeyMode.class.getSimpleName(),
            SecurityMode.FIELD_DEFS);

    /**
     * Prime digest field.
     */
    public static final ReportField FIELD_PRIMETYPE = FIELD_DEFS.declareLocalField("PrimeDigest");

    /**
     * Alternate digest field.
     */
    public static final ReportField FIELD_ALTTYPE = FIELD_DEFS.declareLocalField("AlternateDigest");

    /**
     * Secret digest field.
     */
    public static final ReportField FIELD_SECRETTYPE = FIELD_DEFS.declareLocalField("SecretDigest");

    /**
     * Cipher digest field.
     */
    public static final ReportField FIELD_CIPHER = FIELD_DEFS.declareLocalField("CipherDigest");

    /**
     * Switch iteration field.
     */
    public static final ReportField FIELD_SWITCH = FIELD_DEFS.declareLocalField("SwitchAdjust");

    /**
     * Final iteration field.
     */
    public static final ReportField FIELD_FINAL = FIELD_DEFS.declareLocalField("FinalAdjust");

    @Override
    public ReportFields getReportFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final ReportField pField) {
        if (pField == FIELD_PRIMETYPE) {
            return thePrimeDigest;
        }
        if (pField == FIELD_ALTTYPE) {
            return theAlternateDigest;
        }
        if (pField == FIELD_SECRETTYPE) {
            return theSecretDigest;
        }
        if (pField == FIELD_CIPHER) {
            return theCipherDigest;
        }
        if (pField == FIELD_SWITCH) {
            return theSwitchAdjust;
        }
        if (pField == FIELD_FINAL) {
            return theFinalAdjust;
        }
        return super.getFieldValue(pField);
    }

    @Override
    public String getObjectSummary() {
        return FIELD_DEFS.getName();
    }

    /**
     * Number of digests.
     */
    private static final int NUM_DIGESTS = 3;

    /**
     * Maximum iteration.
     */
    private static final int MAX_ITERATIONS = 15;

    /**
     * The locations (in nybbles).
     */
    private static final int PLACE_PRIMETYPE = 0;

    /**
     * The locations (in nybbles).
     */
    private static final int PLACE_ALTTYPE = 1;

    /**
     * The locations (in nybbles).
     */
    private static final int PLACE_SECRETTYPE = 2;

    /**
     * The locations (in nybbles).
     */
    private static final int PLACE_CIPHERTYPE = 3;

    /**
     * The locations (in nybbles).
     */
    private static final int PLACE_SWITCH = 4;

    /**
     * The locations (in nybbles).
     */
    private static final int PLACE_FINAL = 5;

    /**
     * The various versions.
     */
    private static final short VERSION_CURRENT = 1;

    /**
     * The Prime Digest type.
     */
    private final DigestType thePrimeDigest;

    /**
     * The Alternate Digest type.
     */
    private final DigestType theAlternateDigest;

    /**
     * The Secret Digest type.
     */
    private final DigestType theSecretDigest;

    /**
     * The Cipher Digest type.
     */
    private final DigestType theCipherDigest;

    /**
     * The Switch adjustment.
     */
    private final int theSwitchAdjust;

    /**
     * The Final adjustment.
     */
    private final int theFinalAdjust;

    /**
     * Obtain the Prime Digest type.
     * @return the digest type
     */
    public DigestType getPrimeDigest() {
        return thePrimeDigest;
    }

    /**
     * Obtain the Alternate Digest type.
     * @return the digest type
     */
    public DigestType getAlternateDigest() {
        return theAlternateDigest;
    }

    /**
     * Obtain the Secret Digest type.
     * @return the digest type
     */
    public DigestType getSecretDigest() {
        return theSecretDigest;
    }

    /**
     * Obtain the Cipher Digest type.
     * @return the digest type
     */
    public DigestType getCipherDigest() {
        return theCipherDigest;
    }

    /**
     * Obtain the Switch Adjustment.
     * @return the switch adjustment
     */
    public int getSwitchAdjust() {
        return theSwitchAdjust;
    }

    /**
     * Obtain the Final Adjustment.
     * @return the final adjustment
     */
    public int getFinalAdjust() {
        return theFinalAdjust;
    }

    /**
     * Constructor at random.
     * @param useRestricted use restricted keys
     * @param pRandom the random generator
     * @throws ModelException on error
     */
    protected HashMode(final boolean useRestricted,
                       final SecureRandom pRandom) throws ModelException {
        /* Access a random set of DigestTypes */
        DigestType[] myDigest = DigestType.getRandomTypes(NUM_DIGESTS, pRandom);
        DigestType[] mySetDigest = DigestType.getRandomTypes(1, pRandom);

        /* Store Digest types */
        thePrimeDigest = myDigest[0];
        theAlternateDigest = myDigest[1];
        theSecretDigest = myDigest[2];
        theCipherDigest = mySetDigest[0];

        /* Access random adjustment values */
        theSwitchAdjust = 1 + pRandom.nextInt(MAX_ITERATIONS);
        theFinalAdjust = 1 + pRandom.nextInt(MAX_ITERATIONS);

        /* Set the version */
        setVersion(VERSION_CURRENT);

        /* Set restricted flags */
        setRestricted(useRestricted);

        /* encode the keyMode */
        encodeKeyMode();
    }

    /**
     * Constructor from encoded format.
     * @param pEncoded the encoded format
     * @throws ModelException on error
     */
    protected HashMode(final byte[] pEncoded) throws ModelException {
        /* Set the initial encoded version */
        setEncoded(pEncoded);

        /* Not allowed unless version is current */
        if (getVersion() != VERSION_CURRENT) {
            throw new ModelException(ExceptionClass.LOGIC, "Invalid mode version: " + getVersion());
        }

        /* Store Key type and digest */
        thePrimeDigest = DigestType.fromId(getDataValue(PLACE_PRIMETYPE));
        theAlternateDigest = DigestType.fromId(getDataValue(PLACE_ALTTYPE));
        theSecretDigest = DigestType.fromId(getDataValue(PLACE_SECRETTYPE));
        theCipherDigest = DigestType.fromId(getDataValue(PLACE_CIPHERTYPE));

        /* Access the adjustments */
        theSwitchAdjust = getDataValue(PLACE_SWITCH);
        theFinalAdjust = getDataValue(PLACE_FINAL);

        /* Re-encode the key mode */
        encodeKeyMode();
    }

    /**
     * Encode the key mode.
     */
    private void encodeKeyMode() {
        /* Allocate the encoded array */
        allocateEncoded(PLACE_FINAL);

        /* Set the values */
        setDataValue(PLACE_PRIMETYPE, thePrimeDigest.getId());
        setDataValue(PLACE_ALTTYPE, theAlternateDigest.getId());
        setDataValue(PLACE_SECRETTYPE, theSecretDigest.getId());
        setDataValue(PLACE_CIPHERTYPE, theCipherDigest.getId());
        setDataValue(PLACE_SWITCH, theSwitchAdjust);
        setDataValue(PLACE_FINAL, theFinalAdjust);
    }
}
