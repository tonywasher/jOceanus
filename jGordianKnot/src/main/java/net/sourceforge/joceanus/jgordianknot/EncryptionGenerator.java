/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JDilution;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JPrice;
import net.sourceforge.joceanus.jdecimal.JRate;
import net.sourceforge.joceanus.jdecimal.JRatio;
import net.sourceforge.joceanus.jdecimal.JUnits;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedBigDecimal;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedBigInteger;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedBoolean;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedCharArray;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedDate;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedDateDay;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedDilution;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedDouble;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedField;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedFloat;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedInteger;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedLong;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedMoney;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedPrice;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedRate;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedRatio;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedShort;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedString;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedUnits;

/**
 * Encrypted field generator.
 */
public class EncryptionGenerator {
    /**
     * Invalid class error text.
     */
    private static final String ERROR_CLASS = "Invalid Object Class for Encryption ";

    /**
     * The CipherSet to use for generation.
     */
    private final CipherSet theCipherSet;

    /**
     * Data formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pCipherSet the CipherSet
     */
    public EncryptionGenerator(final CipherSet pCipherSet) {
        /* Use new formatter */
        this(pCipherSet, new JDataFormatter());
    }

    /**
     * Constructor.
     * @param pCipherSet the CipherSet
     * @param pFormatter the formatter
     */
    public EncryptionGenerator(final CipherSet pCipherSet,
                               final JDataFormatter pFormatter) {
        /* Store Parameter */
        theCipherSet = pCipherSet;
        theFormatter = pFormatter;
    }

    /**
     * Set Encrypted value.
     * @param pCurrent the current encrypted value
     * @param pValue the new value to encrypt
     * @return the encrypted field
     * @throws JDataException on error
     */
    public EncryptedField<?> encryptValue(final EncryptedField<?> pCurrent,
                                          final Object pValue) throws JDataException {
        /* If we are passed a null value just return null */
        if (pValue == null) {
            return null;
        }

        /* Access current value */
        EncryptedField<?> myCurrent = pCurrent;

        /* If we have no cipher or else a different cipher, ignore the current value */
        if ((myCurrent != null)
            && ((theCipherSet == null) || (!theCipherSet.equals(myCurrent.getCipherSet())))) {
            myCurrent = null;
        }

        /* If the value is not changed return the current value */
        if ((myCurrent != null)
            && (Difference.isEqual(myCurrent.getValue(), pValue))) {
            return pCurrent;
        }

        /* We need a new Field so handle each case individually */
        if (String.class.isInstance(pValue)) {
            return new EncryptedString(theCipherSet, theFormatter, (String) pValue);
        }
        if (Short.class.isInstance(pValue)) {
            return new EncryptedShort(theCipherSet, theFormatter, (Short) pValue);
        }
        if (Integer.class.isInstance(pValue)) {
            return new EncryptedInteger(theCipherSet, theFormatter, (Integer) pValue);
        }
        if (Long.class.isInstance(pValue)) {
            return new EncryptedLong(theCipherSet, theFormatter, (Long) pValue);
        }
        if (Boolean.class.isInstance(pValue)) {
            return new EncryptedBoolean(theCipherSet, theFormatter, (Boolean) pValue);
        }
        if (Date.class.isInstance(pValue)) {
            return new EncryptedDate(theCipherSet, theFormatter, (Date) pValue);
        }
        if (char[].class.isInstance(pValue)) {
            return new EncryptedCharArray(theCipherSet, theFormatter, (char[]) pValue);
        }
        if (Float.class.isInstance(pValue)) {
            return new EncryptedFloat(theCipherSet, theFormatter, (Float) pValue);
        }
        if (Double.class.isInstance(pValue)) {
            return new EncryptedDouble(theCipherSet, theFormatter, (Double) pValue);
        }

        /* Handle big integer classes */
        if (BigInteger.class.isInstance(pValue)) {
            return new EncryptedBigInteger(theCipherSet, theFormatter, (BigInteger) pValue);
        }
        if (BigDecimal.class.isInstance(pValue)) {
            return new EncryptedBigDecimal(theCipherSet, theFormatter, (BigDecimal) pValue);
        }

        /* Handle decimal instances */
        if (JDateDay.class.isInstance(pValue)) {
            return new EncryptedDateDay(theCipherSet, theFormatter, (JDateDay) pValue);
        }
        if (JUnits.class.isInstance(pValue)) {
            return new EncryptedUnits(theCipherSet, theFormatter, (JUnits) pValue);
        }
        if (JRate.class.isInstance(pValue)) {
            return new EncryptedRate(theCipherSet, theFormatter, (JRate) pValue);
        }
        if (JPrice.class.isInstance(pValue)) {
            return new EncryptedPrice(theCipherSet, theFormatter, (JPrice) pValue);
        }
        if (JMoney.class.isInstance(pValue)) {
            return new EncryptedMoney(theCipherSet, theFormatter, (JMoney) pValue);
        }
        if (JDilution.class.isInstance(pValue)) {
            return new EncryptedDilution(theCipherSet, theFormatter, (JDilution) pValue);
        }
        if (JRatio.class.isInstance(pValue)) {
            return new EncryptedRatio(theCipherSet, theFormatter, (JRatio) pValue);
        }

        /* Unsupported so reject */
        throw new JDataException(ExceptionClass.LOGIC, ERROR_CLASS
                                                       + pValue.getClass().getCanonicalName());
    }

    /**
     * decrypt value.
     * @param pEncrypted the encrypted value
     * @param pClass the class of the encrypted value
     * @return the encrypted field
     * @throws JDataException on error
     */
    public EncryptedField<?> decryptValue(final byte[] pEncrypted,
                                          final Class<?> pClass) throws JDataException {
        /* If we are passed a null value just return null */
        if (pEncrypted == null) {
            return null;
        }

        /* We need a new Field so handle each case individually */
        if (String.class == pClass) {
            return new EncryptedString(theCipherSet, theFormatter, pEncrypted);
        }
        if (Short.class == pClass) {
            return new EncryptedShort(theCipherSet, theFormatter, pEncrypted);
        }
        if (Integer.class == pClass) {
            return new EncryptedInteger(theCipherSet, theFormatter, pEncrypted);
        }
        if (Long.class == pClass) {
            return new EncryptedLong(theCipherSet, theFormatter, pEncrypted);
        }
        if (Boolean.class == pClass) {
            return new EncryptedBoolean(theCipherSet, theFormatter, pEncrypted);
        }
        if (Date.class == pClass) {
            return new EncryptedDate(theCipherSet, theFormatter, pEncrypted);
        }
        if (char[].class == pClass) {
            return new EncryptedCharArray(theCipherSet, theFormatter, pEncrypted);
        }
        if (Float.class == pClass) {
            return new EncryptedFloat(theCipherSet, theFormatter, pEncrypted);
        }
        if (Double.class == pClass) {
            return new EncryptedDouble(theCipherSet, theFormatter, pEncrypted);
        }

        /* Handle BigInteger classes */
        if (BigInteger.class == pClass) {
            return new EncryptedBigInteger(theCipherSet, theFormatter, pEncrypted);
        }
        if (BigDecimal.class == pClass) {
            return new EncryptedBigDecimal(theCipherSet, theFormatter, pEncrypted);
        }

        /* Handle decimal instances */
        if (JDateDay.class == pClass) {
            return new EncryptedDateDay(theCipherSet, theFormatter, pEncrypted);
        }
        if (JMoney.class == pClass) {
            return new EncryptedMoney(theCipherSet, theFormatter, pEncrypted);
        }
        if (JUnits.class == pClass) {
            return new EncryptedUnits(theCipherSet, theFormatter, pEncrypted);
        }
        if (JRate.class == pClass) {
            return new EncryptedRate(theCipherSet, theFormatter, pEncrypted);
        }
        if (JPrice.class == pClass) {
            return new EncryptedPrice(theCipherSet, theFormatter, pEncrypted);
        }
        if (JDilution.class == pClass) {
            return new EncryptedDilution(theCipherSet, theFormatter, pEncrypted);
        }
        if (JRatio.class == pClass) {
            return new EncryptedRatio(theCipherSet, theFormatter, pEncrypted);
        }

        /* Unsupported so reject */
        throw new JDataException(ExceptionClass.LOGIC, ERROR_CLASS
                                                       + pClass.getCanonicalName());
    }

    /**
     * Adopt Encryption.
     * @param pTarget the target field
     * @param pSource the source field
     * @throws JDataException on error
     */
    public void adoptEncryption(final EncryptedField<?> pTarget,
                                final EncryptedField<?> pSource) throws JDataException {
        /* Adopt the encryption */
        pTarget.adoptEncryption(theCipherSet, theFormatter, pSource);
    }
}
