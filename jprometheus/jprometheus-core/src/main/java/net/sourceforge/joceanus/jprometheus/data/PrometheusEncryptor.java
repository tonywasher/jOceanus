/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.data;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataType;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.PrometheusLogicException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Encryptor/Decryptor.
 */
public class PrometheusEncryptor {
    /**
     * Encrypted data conversion failure message.
     */
    private static final String ERROR_BYTES_CONVERT = "Failed to convert value from bytes";

    /**
     * Invalid class error text.
     */
    private static final String ERROR_CLASS = "Invalid Object Class for Encryption ";

    /**
     * Unsupported dataType error text.
     */
    private static final String ERROR_DATATYPE = "Unsupported Data Type";

    /**
     * The Encryptor map.
     */
    private static final Map<MetisDataType, PrometheusDataEncryptor> ENCRYPTORS = buildEncryptorMap();

    /**
     * The KeySet.
     */
    private final GordianKeySet theKeySet;

    /**
     * The Data formatter.
     */
    private final TethysUIDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param pKeySet the keySet
     */
    public PrometheusEncryptor(final TethysUIDataFormatter pFormatter,
                               final GordianKeySet pKeySet) {
        theFormatter = pFormatter;
        theKeySet = pKeySet;
    }

    /**
     * Obtain the keySet.
     * @return the keySet
     */
    public GordianKeySet getKeySet() {
        return theKeySet;
    }

    /**
     * Encrypt a value.
     * @param pValue the value to encrypt.
     * @return the encryptedBytes
     * @throws OceanusException on error
     */
    public byte[] encryptValue(final Object pValue) throws OceanusException {
        /* Access the encryptor */
        final MetisDataType myDataType = getDataTypeForValue(pValue);
        final PrometheusDataEncryptor myEncryptor = ENCRYPTORS.get(myDataType);

        final byte[] myBytes = myEncryptor.convertValue(theFormatter, pValue);
        return theKeySet == null ? null : theKeySet.encryptBytes(myBytes);
    }

    /**
     * Encrypt a value.
     * @param pCurrent the current value
     * @param pValue the value to encrypt.
     * @return the encryptedPair.
     * @throws OceanusException on error
     */
    public PrometheusEncryptedPair encryptValue(final PrometheusEncryptedPair pCurrent,
                                                final Object pValue) throws OceanusException {
        /* If we are passed a null value just return null */
        if (pValue == null) {
            return null;
        }

        /* If we have no keySet or else a different keySet, ignore the current value */
        PrometheusEncryptedPair myCurrent = pCurrent;
        if (myCurrent != null
                && (theKeySet == null || !theKeySet.equals(myCurrent.getKeySet()))) {
            myCurrent = null;
        }

        /* If the value is not changed return the current value */
        if (myCurrent != null
                && MetisDataDifference.isEqual(myCurrent.getValue(), pValue)) {
            return pCurrent;
        }

        /* Encrypt the data */
        final byte[] myEncrypted = encryptValue(pValue);
        return new PrometheusEncryptedPair(theKeySet, pValue, myEncrypted);
    }

    /**
     * Encrypt a value.
     * @param pValue the value to encrypt.
     * @param pField the field definition
     * @return the encryptedPair.
     * @throws OceanusException on error
     */
    public PrometheusEncryptedPair encryptValue(final Object pValue,
                                                final MetisFieldDef pField) throws OceanusException {
        /* Handle Context dataType */
        MetisDataType myDataType = pField.getDataType();
        if (myDataType == MetisDataType.CONTEXT) {
            myDataType = getDataTypeForValue(pValue);
        }

        /* Access the encryptor */
        final PrometheusDataEncryptor myEncryptor = ENCRYPTORS.get(myDataType);
        if (myEncryptor == null) {
            throw new PrometheusLogicException(ERROR_DATATYPE);
        }

        /* Encrypt the data */
        final byte[] myBytes = myEncryptor.convertValue(theFormatter, pValue);
        final byte[] myEncrypted = theKeySet.encryptBytes(myBytes);
        return new PrometheusEncryptedPair(theKeySet, pValue, myEncrypted);
    }

    /**
     * Decrypt bytes.
     * @param pBytes the bytes to decrypt.
     * @param pField the field definition
     * @return the encryptedPair.
     * @throws OceanusException on error
     */
    PrometheusEncryptedPair decryptValue(final byte[] pBytes,
                                         final MetisFieldDef pField) throws OceanusException {
        /* Access the encryptor */
        final PrometheusDataEncryptor myEncryptor = ENCRYPTORS.get(pField.getDataType());
        if (myEncryptor == null) {
            throw new PrometheusLogicException(ERROR_DATATYPE);
        }

        /* Decrypt the data */
        final byte[] myDecrypted = theKeySet.decryptBytes(pBytes);
        final Object myValue = myEncryptor.parseValue(theFormatter, myDecrypted);
        return new PrometheusEncryptedPair(theKeySet, myValue, pBytes);
    }

    /**
     * Decrypt bytes.
     * @param pBytes the bytes to decrypt.
     * @param pClazz the class to decrypt to
     * @return the encryptedPair.
     * @throws OceanusException on error
     */
    PrometheusEncryptedPair decryptValue(final byte[] pBytes,
                                         final Class<?> pClazz) throws OceanusException {
        /* Access the encryptor */
        final MetisDataType myDataType = getDataTypeForClass(pClazz);
        final PrometheusDataEncryptor myEncryptor = ENCRYPTORS.get(myDataType);
        if (myEncryptor == null) {
            throw new PrometheusLogicException(ERROR_DATATYPE);
        }

        /* Decrypt the data */
        final byte[] myDecrypted = theKeySet.decryptBytes(pBytes);
        final Object myValue = myEncryptor.parseValue(theFormatter, myDecrypted);
        return new PrometheusEncryptedPair(theKeySet, myValue, pBytes);
    }

    /**
     * Determine dataType.
     * @param pValue the value
     * @return the dataType
     * @throws OceanusException on error
     */
    public static MetisDataType getDataTypeForValue(final Object pValue) throws OceanusException {
        if (pValue instanceof String) {
            return MetisDataType.STRING;
        }
        if (pValue instanceof Short) {
            return MetisDataType.SHORT;
        }
        if (pValue instanceof Integer) {
            return MetisDataType.INTEGER;
        }
        if (pValue instanceof Long) {
            return MetisDataType.LONG;
        }
        if (pValue instanceof Boolean) {
            return MetisDataType.BOOLEAN;
        }
        if (pValue instanceof char[]) {
            return MetisDataType.CHARARRAY;
        }

        /* Handle decimal instances */
        if (pValue instanceof TethysDate) {
            return MetisDataType.DATE;
        }
        if (pValue instanceof TethysUnits) {
            return MetisDataType.UNITS;
        }
        if (pValue instanceof TethysRate) {
            return MetisDataType.RATE;
        }
        if (pValue instanceof TethysPrice) {
            return MetisDataType.PRICE;
        }
        if (pValue instanceof TethysMoney) {
            return MetisDataType.MONEY;
        }
        if (pValue instanceof TethysRatio) {
            return MetisDataType.RATIO;
        }

        /* Unsupported so reject */
        throw new PrometheusLogicException(ERROR_CLASS
                + pValue.getClass().getCanonicalName());
    }

    /**
     * Determine dataType.
     * @param pClazz the class
     * @return the dataType
     * @throws OceanusException on error
     */
    static MetisDataType getDataTypeForClass(final Class<?> pClazz) throws OceanusException {
        if (String.class.equals(pClazz)) {
            return MetisDataType.STRING;
        }
        if (Short.class.equals(pClazz)) {
            return MetisDataType.SHORT;
        }
        if (Integer.class.equals(pClazz)) {
            return MetisDataType.INTEGER;
        }
        if (Long.class.equals(pClazz)) {
            return MetisDataType.LONG;
        }
        if (Boolean.class.equals(pClazz)) {
            return MetisDataType.BOOLEAN;
        }
        if (char[].class.equals(pClazz)) {
            return MetisDataType.CHARARRAY;
        }

        /* Handle decimal instances */
        if (TethysDate.class.equals(pClazz)) {
            return MetisDataType.DATE;
        }
        if (TethysUnits.class.equals(pClazz)) {
            return MetisDataType.UNITS;
        }
        if (TethysRate.class.equals(pClazz)) {
            return MetisDataType.RATE;
        }
        if (TethysPrice.class.equals(pClazz)) {
            return MetisDataType.PRICE;
        }
        if (TethysMoney.class.equals(pClazz)) {
            return MetisDataType.MONEY;
        }
        if (TethysRatio.class.equals(pClazz)) {
            return MetisDataType.RATIO;
        }

        /* Unsupported so reject */
        throw new PrometheusLogicException(ERROR_CLASS
                + pClazz.getCanonicalName());
    }

    /**
     * Build the encryptor map.
     * @return the map
     */
    private static Map<MetisDataType, PrometheusDataEncryptor> buildEncryptorMap() {
        final Map<MetisDataType, PrometheusDataEncryptor> myMap = new EnumMap<>(MetisDataType.class);
        myMap.put(MetisDataType.DATE, new PrometheusDateEncryptor());
        myMap.put(MetisDataType.SHORT, new PrometheusShortEncryptor());
        myMap.put(MetisDataType.INTEGER, new PrometheusIntegerEncryptor());
        myMap.put(MetisDataType.LONG, new PrometheusLongEncryptor());
        myMap.put(MetisDataType.STRING, new PrometheusStringEncryptor());
        myMap.put(MetisDataType.CHARARRAY, new PrometheusCharArrayEncryptor());
        myMap.put(MetisDataType.BOOLEAN, new PrometheusBooleanEncryptor());
        myMap.put(MetisDataType.MONEY, new PrometheusMoneyEncryptor());
        myMap.put(MetisDataType.PRICE, new PrometheusPriceEncryptor());
        myMap.put(MetisDataType.RATE, new PrometheusRateEncryptor());
        myMap.put(MetisDataType.UNITS, new PrometheusUnitsEncryptor());
        myMap.put(MetisDataType.RATIO, new PrometheusRatioEncryptor());
        return myMap;
    }

    /**
     * Adopt Encryption.
     * @param pTarget the target field
     * @param pSource the source field
     * @throws OceanusException on error
     */
    public void adoptEncryption(final PrometheusEncryptedPair pTarget,
                                final PrometheusEncryptedPair pSource) throws OceanusException {
        /* Adopt the encryption */
        pTarget.adoptEncryption(this, pSource);
    }

    /**
     * Encryptor Base.
     */
    private interface PrometheusDataEncryptor {
        /**
         * Convert a value to bytes.
         * @param pFormatter the data formatter
         * @param pValue the value to convert.
         * @return the converted bytes.
         * @throws OceanusException on error
         */
        byte[] convertValue(TethysUIDataFormatter pFormatter,
                            Object pValue) throws OceanusException;

        /**
         * Parse a value from bytes.
         * @param pFormatter the data formatter
         * @param pBytes the bytes to parse.
         * @return the parsed value.
         * @throws OceanusException on error
         */
        Object parseValue(TethysUIDataFormatter pFormatter,
                          byte[] pBytes) throws OceanusException;
    }

    /**
     * DateEncryptor.
     */
    private static final class PrometheusDateEncryptor
        implements PrometheusDataEncryptor {
        @Override
        public byte[] convertValue(final TethysUIDataFormatter pFormatter,
                                   final Object pValue) {
            return pFormatter.getDateFormatter().toBytes((TethysDate) pValue);
        }

        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            try {
                return pFormatter.getDateFormatter().fromBytes(pBytes);
            } catch (IllegalArgumentException e) {
                throw new PrometheusDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * IntegerEncryptor.
     */
    private static final class PrometheusShortEncryptor
            implements PrometheusDataEncryptor {
        @Override
        public byte[] convertValue(final TethysUIDataFormatter pFormatter,
                                   final Object pValue) {
            return TethysDataConverter.shortToByteArray((short) pValue);
        }

        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return TethysDataConverter.byteArrayToShort(pBytes);
        }
    }

    /**
     * IntegerEncryptor.
     */
    private static final class PrometheusIntegerEncryptor
            implements PrometheusDataEncryptor {
        @Override
        public byte[] convertValue(final TethysUIDataFormatter pFormatter,
                                   final Object pValue) {
            return TethysDataConverter.integerToByteArray((int) pValue);
        }

        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return TethysDataConverter.byteArrayToInteger(pBytes);
        }
    }

    /**
     * LongEncryptor.
     */
    private static final class PrometheusLongEncryptor
            implements PrometheusDataEncryptor {
        @Override
        public byte[] convertValue(final TethysUIDataFormatter pFormatter,
                                   final Object pValue) {
            return TethysDataConverter.longToByteArray((long) pValue);
        }

        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return TethysDataConverter.byteArrayToLong(pBytes);
        }
    }

    /**
     * BooleanEncryptor.
     */
    private static final class PrometheusBooleanEncryptor
            implements PrometheusDataEncryptor {
        @Override
        public byte[] convertValue(final TethysUIDataFormatter pFormatter,
                                   final Object pValue) {
            return TethysDataConverter.stringToByteArray(pValue.toString());
        }

        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
                                 final byte[] pBytes) {
            final String myBoolString = TethysDataConverter.byteArrayToString(pBytes);
            return Boolean.parseBoolean(myBoolString);
        }
    }

    /**
     * StringEncryptor.
     */
    private static final class PrometheusStringEncryptor
            implements PrometheusDataEncryptor {
        @Override
        public byte[] convertValue(final TethysUIDataFormatter pFormatter,
                                   final Object pValue) {
            return TethysDataConverter.stringToByteArray((String) pValue);
        }

        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
                                 final byte[] pBytes) {
            return TethysDataConverter.byteArrayToString(pBytes);
        }
    }

    /**
     * CharArrayEncryptor.
     */
    private static final class PrometheusCharArrayEncryptor
            implements PrometheusDataEncryptor {
        @Override
        public byte[] convertValue(final TethysUIDataFormatter pFormatter,
                                   final Object pValue) throws OceanusException {
            return TethysDataConverter.charsToByteArray((char[]) pValue);
        }

        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return TethysDataConverter.bytesToCharArray(pBytes);
        }
    }

    /**
     * DecimalEncryptor.
     */
    private abstract static class PrometheusDecimalEncryptor
            implements PrometheusDataEncryptor {
        @Override
        public byte[] convertValue(final TethysUIDataFormatter pFormatter,
                                   final Object pValue) {
            return ((TethysDecimal) pValue).toBytes();
        }
    }

    /**
     * MoneyEncryptor.
     */
    private static final class PrometheusMoneyEncryptor
            extends PrometheusDecimalEncryptor {
        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return new TethysMoney(pBytes);
        }
    }

    /**
     * PriceEncryptor.
     */
    private static final class PrometheusPriceEncryptor
            extends PrometheusDecimalEncryptor {
        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return new TethysPrice(pBytes);
        }
    }

    /**
     * RatioEncryptor.
     */
    private static final class PrometheusRatioEncryptor
            extends PrometheusDecimalEncryptor {
        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return new TethysRatio(pBytes);
        }
    }

    /**
     * UnitsEncryptor.
     */
    private static final class PrometheusUnitsEncryptor
            extends PrometheusDecimalEncryptor {
        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return new TethysUnits(pBytes);
        }
    }

    /**
     * RateEncryptor.
     */
    private static final class PrometheusRateEncryptor
            extends PrometheusDecimalEncryptor {
        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return new TethysRate(pBytes);
        }
    }
}
