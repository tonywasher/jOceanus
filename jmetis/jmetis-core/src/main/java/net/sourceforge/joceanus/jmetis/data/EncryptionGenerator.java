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
package net.sourceforge.joceanus.jmetis.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jmetis.JMetisLogicException;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedBigDecimal;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedBigInteger;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedBoolean;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedCharArray;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedDate;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedDateDay;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedDilution;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedDouble;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedField;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedFloat;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedInteger;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedLong;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedMoney;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedPrice;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedRate;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedRatio;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedShort;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedString;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedUnits;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Encrypted field generator.
 */
public class EncryptionGenerator {
    /**
     * Invalid class error text.
     */
    private static final String ERROR_CLASS = "Invalid Object Class for Encryption ";

    /**
     * The KeySet to use for generation.
     */
    private final GordianKeySet theKeySet;

    /**
     * Data formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pKeySet the KeySet
     */
    public EncryptionGenerator(final GordianKeySet pKeySet) {
        /* Use new formatter */
        this(pKeySet, new JDataFormatter());
    }

    /**
     * Constructor.
     * @param pKeySet the KeySet
     * @param pFormatter the formatter
     */
    public EncryptionGenerator(final GordianKeySet pKeySet,
                               final JDataFormatter pFormatter) {
        /* Store Parameter */
        theKeySet = pKeySet;
        theFormatter = pFormatter;
    }

    /**
     * Set Encrypted value.
     * @param pCurrent the current encrypted value
     * @param pValue the new value to encrypt
     * @return the encrypted field
     * @throws OceanusException on error
     */
    public EncryptedField<?> encryptValue(final EncryptedField<?> pCurrent,
                                          final Object pValue) throws OceanusException {
        /* If we are passed a null value just return null */
        if (pValue == null) {
            return null;
        }

        /* Access current value */
        EncryptedField<?> myCurrent = pCurrent;

        /* If we have no keySet or else a different keySet, ignore the current value */
        if ((myCurrent != null)
            && ((theKeySet == null) || (!theKeySet.equals(myCurrent.getKeySet())))) {
            myCurrent = null;
        }

        /* If the value is not changed return the current value */
        if ((myCurrent != null)
            && (Difference.isEqual(myCurrent.getValue(), pValue))) {
            return pCurrent;
        }

        /* We need a new Field so handle each case individually */
        if (String.class.isInstance(pValue)) {
            return new EncryptedString(theKeySet, theFormatter, (String) pValue);
        }
        if (Short.class.isInstance(pValue)) {
            return new EncryptedShort(theKeySet, theFormatter, (Short) pValue);
        }
        if (Integer.class.isInstance(pValue)) {
            return new EncryptedInteger(theKeySet, theFormatter, (Integer) pValue);
        }
        if (Long.class.isInstance(pValue)) {
            return new EncryptedLong(theKeySet, theFormatter, (Long) pValue);
        }
        if (Boolean.class.isInstance(pValue)) {
            return new EncryptedBoolean(theKeySet, theFormatter, (Boolean) pValue);
        }
        if (Date.class.isInstance(pValue)) {
            return new EncryptedDate(theKeySet, theFormatter, (Date) pValue);
        }
        if (char[].class.isInstance(pValue)) {
            return new EncryptedCharArray(theKeySet, theFormatter, (char[]) pValue);
        }
        if (Float.class.isInstance(pValue)) {
            return new EncryptedFloat(theKeySet, theFormatter, (Float) pValue);
        }
        if (Double.class.isInstance(pValue)) {
            return new EncryptedDouble(theKeySet, theFormatter, (Double) pValue);
        }

        /* Handle big integer classes */
        if (BigInteger.class.isInstance(pValue)) {
            return new EncryptedBigInteger(theKeySet, theFormatter, (BigInteger) pValue);
        }
        if (BigDecimal.class.isInstance(pValue)) {
            return new EncryptedBigDecimal(theKeySet, theFormatter, (BigDecimal) pValue);
        }

        /* Handle decimal instances */
        if (TethysDate.class.isInstance(pValue)) {
            return new EncryptedDateDay(theKeySet, theFormatter, (TethysDate) pValue);
        }
        if (TethysUnits.class.isInstance(pValue)) {
            return new EncryptedUnits(theKeySet, theFormatter, (TethysUnits) pValue);
        }
        if (TethysRate.class.isInstance(pValue)) {
            return new EncryptedRate(theKeySet, theFormatter, (TethysRate) pValue);
        }
        if (TethysPrice.class.isInstance(pValue)) {
            return new EncryptedPrice(theKeySet, theFormatter, (TethysPrice) pValue);
        }
        if (TethysMoney.class.isInstance(pValue)) {
            return new EncryptedMoney(theKeySet, theFormatter, (TethysMoney) pValue);
        }
        if (TethysDilution.class.isInstance(pValue)) {
            return new EncryptedDilution(theKeySet, theFormatter, (TethysDilution) pValue);
        }
        if (TethysRatio.class.isInstance(pValue)) {
            return new EncryptedRatio(theKeySet, theFormatter, (TethysRatio) pValue);
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
     * @throws OceanusException on error
     */
    public EncryptedField<?> decryptValue(final byte[] pEncrypted,
                                          final Class<?> pClass) throws OceanusException {
        /* If we are passed a null value just return null */
        if (pEncrypted == null) {
            return null;
        }

        /* We need a new Field so handle each case individually */
        if (String.class.equals(pClass)) {
            return new EncryptedString(theKeySet, theFormatter, pEncrypted);
        }
        if (Short.class.equals(pClass)) {
            return new EncryptedShort(theKeySet, theFormatter, pEncrypted);
        }
        if (Integer.class.equals(pClass)) {
            return new EncryptedInteger(theKeySet, theFormatter, pEncrypted);
        }
        if (Long.class.equals(pClass)) {
            return new EncryptedLong(theKeySet, theFormatter, pEncrypted);
        }
        if (Boolean.class.equals(pClass)) {
            return new EncryptedBoolean(theKeySet, theFormatter, pEncrypted);
        }
        if (Date.class.equals(pClass)) {
            return new EncryptedDate(theKeySet, theFormatter, pEncrypted);
        }
        if (char[].class.equals(pClass)) {
            return new EncryptedCharArray(theKeySet, theFormatter, pEncrypted);
        }
        if (Float.class.equals(pClass)) {
            return new EncryptedFloat(theKeySet, theFormatter, pEncrypted);
        }
        if (Double.class.equals(pClass)) {
            return new EncryptedDouble(theKeySet, theFormatter, pEncrypted);
        }

        /* Handle BigInteger classes */
        if (BigInteger.class.equals(pClass)) {
            return new EncryptedBigInteger(theKeySet, theFormatter, pEncrypted);
        }
        if (BigDecimal.class.equals(pClass)) {
            return new EncryptedBigDecimal(theKeySet, theFormatter, pEncrypted);
        }

        /* Handle decimal instances */
        if (TethysDate.class.equals(pClass)) {
            return new EncryptedDateDay(theKeySet, theFormatter, pEncrypted);
        }
        if (TethysMoney.class.equals(pClass)) {
            return new EncryptedMoney(theKeySet, theFormatter, pEncrypted);
        }
        if (TethysUnits.class.equals(pClass)) {
            return new EncryptedUnits(theKeySet, theFormatter, pEncrypted);
        }
        if (TethysRate.class.equals(pClass)) {
            return new EncryptedRate(theKeySet, theFormatter, pEncrypted);
        }
        if (TethysPrice.class.equals(pClass)) {
            return new EncryptedPrice(theKeySet, theFormatter, pEncrypted);
        }
        if (TethysDilution.class.equals(pClass)) {
            return new EncryptedDilution(theKeySet, theFormatter, pEncrypted);
        }
        if (TethysRatio.class.equals(pClass)) {
            return new EncryptedRatio(theKeySet, theFormatter, pEncrypted);
        }

        /* Unsupported so reject */
        throw new JMetisLogicException(ERROR_CLASS
                                       + pClass.getCanonicalName());
    }

    /**
     * Adopt Encryption.
     * @param pTarget the target field
     * @param pSource the source field
     * @throws OceanusException on error
     */
    public void adoptEncryption(final EncryptedField<?> pTarget,
                                final EncryptedField<?> pSource) throws OceanusException {
        /* Adopt the encryption */
        pTarget.adoptEncryption(theKeySet, theFormatter, pSource);
    }
}
