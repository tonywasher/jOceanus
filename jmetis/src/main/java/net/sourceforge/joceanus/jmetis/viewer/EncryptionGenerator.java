/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.viewer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import net.sourceforge.joceanus.jgordianknot.crypto.CipherSet;
import net.sourceforge.joceanus.jmetis.JMetisLogicException;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedBigDecimal;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedBigInteger;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedBoolean;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedCharArray;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedDate;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedDateDay;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedDilution;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedDouble;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedField;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedFloat;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedInteger;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedLong;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedMoney;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedPrice;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedRate;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedRatio;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedShort;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedString;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedUnits;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

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
     * @throws JOceanusException on error
     */
    public EncryptedField<?> encryptValue(final EncryptedField<?> pCurrent,
                                          final Object pValue) throws JOceanusException {
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
        throw new JMetisLogicException(ERROR_CLASS
                                       + pValue.getClass().getCanonicalName());
    }

    /**
     * decrypt value.
     * @param pEncrypted the encrypted value
     * @param pClass the class of the encrypted value
     * @return the encrypted field
     * @throws JOceanusException on error
     */
    public EncryptedField<?> decryptValue(final byte[] pEncrypted,
                                          final Class<?> pClass) throws JOceanusException {
        /* If we are passed a null value just return null */
        if (pEncrypted == null) {
            return null;
        }

        /* We need a new Field so handle each case individually */
        if (String.class.equals(pClass)) {
            return new EncryptedString(theCipherSet, theFormatter, pEncrypted);
        }
        if (Short.class.equals(pClass)) {
            return new EncryptedShort(theCipherSet, theFormatter, pEncrypted);
        }
        if (Integer.class.equals(pClass)) {
            return new EncryptedInteger(theCipherSet, theFormatter, pEncrypted);
        }
        if (Long.class.equals(pClass)) {
            return new EncryptedLong(theCipherSet, theFormatter, pEncrypted);
        }
        if (Boolean.class.equals(pClass)) {
            return new EncryptedBoolean(theCipherSet, theFormatter, pEncrypted);
        }
        if (Date.class.equals(pClass)) {
            return new EncryptedDate(theCipherSet, theFormatter, pEncrypted);
        }
        if (char[].class.equals(pClass)) {
            return new EncryptedCharArray(theCipherSet, theFormatter, pEncrypted);
        }
        if (Float.class.equals(pClass)) {
            return new EncryptedFloat(theCipherSet, theFormatter, pEncrypted);
        }
        if (Double.class.equals(pClass)) {
            return new EncryptedDouble(theCipherSet, theFormatter, pEncrypted);
        }

        /* Handle BigInteger classes */
        if (BigInteger.class.equals(pClass)) {
            return new EncryptedBigInteger(theCipherSet, theFormatter, pEncrypted);
        }
        if (BigDecimal.class.equals(pClass)) {
            return new EncryptedBigDecimal(theCipherSet, theFormatter, pEncrypted);
        }

        /* Handle decimal instances */
        if (JDateDay.class.equals(pClass)) {
            return new EncryptedDateDay(theCipherSet, theFormatter, pEncrypted);
        }
        if (JMoney.class.equals(pClass)) {
            return new EncryptedMoney(theCipherSet, theFormatter, pEncrypted);
        }
        if (JUnits.class.equals(pClass)) {
            return new EncryptedUnits(theCipherSet, theFormatter, pEncrypted);
        }
        if (JRate.class.equals(pClass)) {
            return new EncryptedRate(theCipherSet, theFormatter, pEncrypted);
        }
        if (JPrice.class.equals(pClass)) {
            return new EncryptedPrice(theCipherSet, theFormatter, pEncrypted);
        }
        if (JDilution.class.equals(pClass)) {
            return new EncryptedDilution(theCipherSet, theFormatter, pEncrypted);
        }
        if (JRatio.class.equals(pClass)) {
            return new EncryptedRatio(theCipherSet, theFormatter, pEncrypted);
        }

        /* Unsupported so reject */
        throw new JMetisLogicException(ERROR_CLASS
                                       + pClass.getCanonicalName());
    }

    /**
     * Adopt Encryption.
     * @param pTarget the target field
     * @param pSource the source field
     * @throws JOceanusException on error
     */
    public void adoptEncryption(final EncryptedField<?> pTarget,
                                final EncryptedField<?> pSource) throws JOceanusException {
        /* Adopt the encryption */
        pTarget.adoptEncryption(theCipherSet, theFormatter, pSource);
    }
}
