/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2020 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.lethe.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jmetis.MetisLogicException;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedBigDecimal;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedBigInteger;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedBoolean;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedCharArray;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedDate;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedDilution;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedDouble;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedFloat;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedInteger;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedJavaDate;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedLong;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedMoney;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedPrice;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedRate;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedRatio;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedShort;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedString;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedUnits;
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
public class MetisEncryptionGenerator {
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
    private final MetisDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pKeySet the KeySet
     */
    public MetisEncryptionGenerator(final GordianKeySet pKeySet) {
        /* Use new formatter */
        this(pKeySet, new MetisDataFormatter());
    }

    /**
     * Constructor.
     * @param pKeySet the KeySet
     * @param pFormatter the formatter
     */
    public MetisEncryptionGenerator(final GordianKeySet pKeySet,
                                    final MetisDataFormatter pFormatter) {
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
    public MetisEncryptedField<?> encryptValue(final MetisEncryptedField<?> pCurrent,
                                               final Object pValue) throws OceanusException {
        /* If we are passed a null value just return null */
        if (pValue == null) {
            return null;
        }

        /* Access current value */
        MetisEncryptedField<?> myCurrent = pCurrent;

        /* If we have no keySet or else a different keySet, ignore the current value */
        if ((myCurrent != null)
            && ((theKeySet == null) || (!theKeySet.equals(myCurrent.getKeySet())))) {
            myCurrent = null;
        }

        /* If the value is not changed return the current value */
        if ((myCurrent != null)
            && (MetisDataDifference.isEqual(myCurrent.getValue(), pValue))) {
            return pCurrent;
        }

        /* We need a new Field so handle each case individually */
        if (String.class.isInstance(pValue)) {
            return new MetisEncryptedString(theKeySet, theFormatter, (String) pValue);
        }
        if (Short.class.isInstance(pValue)) {
            return new MetisEncryptedShort(theKeySet, theFormatter, (Short) pValue);
        }
        if (Integer.class.isInstance(pValue)) {
            return new MetisEncryptedInteger(theKeySet, theFormatter, (Integer) pValue);
        }
        if (Long.class.isInstance(pValue)) {
            return new MetisEncryptedLong(theKeySet, theFormatter, (Long) pValue);
        }
        if (Boolean.class.isInstance(pValue)) {
            return new MetisEncryptedBoolean(theKeySet, theFormatter, (Boolean) pValue);
        }
        if (Date.class.isInstance(pValue)) {
            return new MetisEncryptedJavaDate(theKeySet, theFormatter, (Date) pValue);
        }
        if (char[].class.isInstance(pValue)) {
            return new MetisEncryptedCharArray(theKeySet, theFormatter, (char[]) pValue);
        }
        if (Float.class.isInstance(pValue)) {
            return new MetisEncryptedFloat(theKeySet, theFormatter, (Float) pValue);
        }
        if (Double.class.isInstance(pValue)) {
            return new MetisEncryptedDouble(theKeySet, theFormatter, (Double) pValue);
        }

        /* Handle big integer classes */
        if (BigInteger.class.isInstance(pValue)) {
            return new MetisEncryptedBigInteger(theKeySet, theFormatter, (BigInteger) pValue);
        }
        if (BigDecimal.class.isInstance(pValue)) {
            return new MetisEncryptedBigDecimal(theKeySet, theFormatter, (BigDecimal) pValue);
        }

        /* Handle decimal instances */
        if (TethysDate.class.isInstance(pValue)) {
            return new MetisEncryptedDate(theKeySet, theFormatter, (TethysDate) pValue);
        }
        if (TethysUnits.class.isInstance(pValue)) {
            return new MetisEncryptedUnits(theKeySet, theFormatter, (TethysUnits) pValue);
        }
        if (TethysRate.class.isInstance(pValue)) {
            return new MetisEncryptedRate(theKeySet, theFormatter, (TethysRate) pValue);
        }
        if (TethysPrice.class.isInstance(pValue)) {
            return new MetisEncryptedPrice(theKeySet, theFormatter, (TethysPrice) pValue);
        }
        if (TethysMoney.class.isInstance(pValue)) {
            return new MetisEncryptedMoney(theKeySet, theFormatter, (TethysMoney) pValue);
        }
        if (TethysDilution.class.isInstance(pValue)) {
            return new MetisEncryptedDilution(theKeySet, theFormatter, (TethysDilution) pValue);
        }
        if (TethysRatio.class.isInstance(pValue)) {
            return new MetisEncryptedRatio(theKeySet, theFormatter, (TethysRatio) pValue);
        }

        /* Unsupported so reject */
        throw new MetisLogicException(ERROR_CLASS
                                      + pValue.getClass().getCanonicalName());
    }

    /**
     * decrypt value.
     * @param pEncrypted the encrypted value
     * @param pClass the class of the encrypted value
     * @return the encrypted field
     * @throws OceanusException on error
     */
    public MetisEncryptedField<?> decryptValue(final byte[] pEncrypted,
                                               final Class<?> pClass) throws OceanusException {
        /* If we are passed a null value just return null */
        if (pEncrypted == null) {
            return null;
        }

        /* We need a new Field so handle each case individually */
        if (String.class.equals(pClass)) {
            return new MetisEncryptedString(theKeySet, theFormatter, pEncrypted);
        }
        if (Short.class.equals(pClass)) {
            return new MetisEncryptedShort(theKeySet, theFormatter, pEncrypted);
        }
        if (Integer.class.equals(pClass)) {
            return new MetisEncryptedInteger(theKeySet, theFormatter, pEncrypted);
        }
        if (Long.class.equals(pClass)) {
            return new MetisEncryptedLong(theKeySet, theFormatter, pEncrypted);
        }
        if (Boolean.class.equals(pClass)) {
            return new MetisEncryptedBoolean(theKeySet, theFormatter, pEncrypted);
        }
        if (Date.class.equals(pClass)) {
            return new MetisEncryptedJavaDate(theKeySet, theFormatter, pEncrypted);
        }
        if (char[].class.equals(pClass)) {
            return new MetisEncryptedCharArray(theKeySet, theFormatter, pEncrypted);
        }
        if (Float.class.equals(pClass)) {
            return new MetisEncryptedFloat(theKeySet, theFormatter, pEncrypted);
        }
        if (Double.class.equals(pClass)) {
            return new MetisEncryptedDouble(theKeySet, theFormatter, pEncrypted);
        }

        /* Handle BigInteger classes */
        if (BigInteger.class.equals(pClass)) {
            return new MetisEncryptedBigInteger(theKeySet, theFormatter, pEncrypted);
        }
        if (BigDecimal.class.equals(pClass)) {
            return new MetisEncryptedBigDecimal(theKeySet, theFormatter, pEncrypted);
        }

        /* Handle decimal instances */
        if (TethysDate.class.equals(pClass)) {
            return new MetisEncryptedDate(theKeySet, theFormatter, pEncrypted);
        }
        if (TethysMoney.class.equals(pClass)) {
            return new MetisEncryptedMoney(theKeySet, theFormatter, pEncrypted);
        }
        if (TethysUnits.class.equals(pClass)) {
            return new MetisEncryptedUnits(theKeySet, theFormatter, pEncrypted);
        }
        if (TethysRate.class.equals(pClass)) {
            return new MetisEncryptedRate(theKeySet, theFormatter, pEncrypted);
        }
        if (TethysPrice.class.equals(pClass)) {
            return new MetisEncryptedPrice(theKeySet, theFormatter, pEncrypted);
        }
        if (TethysDilution.class.equals(pClass)) {
            return new MetisEncryptedDilution(theKeySet, theFormatter, pEncrypted);
        }
        if (TethysRatio.class.equals(pClass)) {
            return new MetisEncryptedRatio(theKeySet, theFormatter, pEncrypted);
        }

        /* Unsupported so reject */
        throw new MetisLogicException(ERROR_CLASS
                                      + pClass.getCanonicalName());
    }

    /**
     * Adopt Encryption.
     * @param pTarget the target field
     * @param pSource the source field
     * @throws OceanusException on error
     */
    public void adoptEncryption(final MetisEncryptedField<?> pTarget,
                                final MetisEncryptedField<?> pSource) throws OceanusException {
        /* Adopt the encryption */
        pTarget.adoptEncryption(theKeySet, theFormatter, pSource);
    }
}
