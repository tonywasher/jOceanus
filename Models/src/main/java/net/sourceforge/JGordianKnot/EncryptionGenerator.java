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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Dilution;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Price;
import net.sourceforge.JDecimal.Rate;
import net.sourceforge.JDecimal.Units;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedBigDecimal;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedBigInteger;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedBoolean;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedCharArray;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedDate;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedDateDay;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedDilution;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedDouble;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedField;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedFloat;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedInteger;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedLong;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedMoney;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedPrice;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedRate;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedShort;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedString;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedUnits;

/**
 * Encrypted field generator.
 * @author Tony Washer
 */
public class EncryptionGenerator {
    /**
     * The CipherSet to use for generation.
     */
    private final CipherSet theCipherSet;

    /**
     * Constructor.
     * @param pCipherSet the CipherSet
     */
    public EncryptionGenerator(final CipherSet pCipherSet) {
        /* Store Parameter */
        theCipherSet = pCipherSet;
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
        if ((myCurrent != null) && (Difference.isEqual(myCurrent.getValue(), pValue))) {
            return pCurrent;
        }

        /* We need a new Field so handle each case individually */
        if (String.class.isInstance(pValue)) {
            return new EncryptedString(theCipherSet, (String) pValue);
        }
        if (Short.class.isInstance(pValue)) {
            return new EncryptedShort(theCipherSet, (Short) pValue);
        }
        if (Integer.class.isInstance(pValue)) {
            return new EncryptedInteger(theCipherSet, (Integer) pValue);
        }
        if (Long.class.isInstance(pValue)) {
            return new EncryptedLong(theCipherSet, (Long) pValue);
        }
        if (Boolean.class.isInstance(pValue)) {
            return new EncryptedBoolean(theCipherSet, (Boolean) pValue);
        }
        if (Date.class.isInstance(pValue)) {
            return new EncryptedDate(theCipherSet, (Date) pValue);
        }
        if (char[].class.isInstance(pValue)) {
            return new EncryptedCharArray(theCipherSet, (char[]) pValue);
        }
        if (Float.class.isInstance(pValue)) {
            return new EncryptedFloat(theCipherSet, (Float) pValue);
        }
        if (Double.class.isInstance(pValue)) {
            return new EncryptedDouble(theCipherSet, (Double) pValue);
        }

        /* Handle big integer classes */
        if (BigInteger.class.isInstance(pValue)) {
            return new EncryptedBigInteger(theCipherSet, (BigInteger) pValue);
        }
        if (BigDecimal.class.isInstance(pValue)) {
            return new EncryptedBigDecimal(theCipherSet, (BigDecimal) pValue);
        }

        /* Handle decimal instances */
        if (DateDay.class.isInstance(pValue)) {
            return new EncryptedDateDay(theCipherSet, (DateDay) pValue);
        }
        if (Money.class.isInstance(pValue)) {
            return new EncryptedMoney(theCipherSet, (Money) pValue);
        }
        if (Units.class.isInstance(pValue)) {
            return new EncryptedUnits(theCipherSet, (Units) pValue);
        }
        if (Rate.class.isInstance(pValue)) {
            return new EncryptedRate(theCipherSet, (Rate) pValue);
        }
        if (Price.class.isInstance(pValue)) {
            return new EncryptedPrice(theCipherSet, (Price) pValue);
        }
        if (Dilution.class.isInstance(pValue)) {
            return new EncryptedDilution(theCipherSet, (Dilution) pValue);
        }

        /* Unsupported so reject */
        throw new JDataException(ExceptionClass.LOGIC, "Invalid Object Class for Encryption"
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
            return new EncryptedString(theCipherSet, pEncrypted);
        }
        if (Short.class == pClass) {
            return new EncryptedShort(theCipherSet, pEncrypted);
        }
        if (Integer.class == pClass) {
            return new EncryptedInteger(theCipherSet, pEncrypted);
        }
        if (Long.class == pClass) {
            return new EncryptedLong(theCipherSet, pEncrypted);
        }
        if (Boolean.class == pClass) {
            return new EncryptedBoolean(theCipherSet, pEncrypted);
        }
        if (Date.class == pClass) {
            return new EncryptedDate(theCipherSet, pEncrypted);
        }
        if (char[].class == pClass) {
            return new EncryptedCharArray(theCipherSet, pEncrypted);
        }
        if (Float.class == pClass) {
            return new EncryptedFloat(theCipherSet, pEncrypted);
        }
        if (Double.class == pClass) {
            return new EncryptedDouble(theCipherSet, pEncrypted);
        }

        /* Handle BigInteger classes */
        if (BigInteger.class == pClass) {
            return new EncryptedBigInteger(theCipherSet, pEncrypted);
        }
        if (BigDecimal.class == pClass) {
            return new EncryptedBigDecimal(theCipherSet, pEncrypted);
        }

        /* Handle decimal instances */
        if (DateDay.class == pClass) {
            return new EncryptedDateDay(theCipherSet, pEncrypted);
        }
        if (Money.class == pClass) {
            return new EncryptedMoney(theCipherSet, pEncrypted);
        }
        if (Units.class == pClass) {
            return new EncryptedUnits(theCipherSet, pEncrypted);
        }
        if (Rate.class == pClass) {
            return new EncryptedRate(theCipherSet, pEncrypted);
        }
        if (Price.class == pClass) {
            return new EncryptedPrice(theCipherSet, pEncrypted);
        }
        if (Dilution.class == pClass) {
            return new EncryptedDilution(theCipherSet, pEncrypted);
        }

        /* Unsupported so reject */
        throw new JDataException(ExceptionClass.LOGIC, "Invalid Object Class for Encryption"
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
        pTarget.adoptEncryption(theCipherSet, pSource);
    }
}
