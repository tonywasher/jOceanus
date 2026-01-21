/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.prometheus.data;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.convert.OceanusDataConverter;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.metis.data.MetisDataType;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.prometheus.exc.PrometheusDataException;
import net.sourceforge.joceanus.prometheus.exc.PrometheusLogicException;
import net.sourceforge.joceanus.prometheus.exc.PrometheusSecurityException;

import java.util.EnumMap;
import java.util.Map;

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
    private final OceanusDataFormatter theFormatter;

    /**
     * Constructor.
     *
     * @param pFormatter the formatter
     * @param pKeySet    the keySet
     */
    public PrometheusEncryptor(final OceanusDataFormatter pFormatter,
                               final GordianKeySet pKeySet) {
        theFormatter = pFormatter;
        theKeySet = pKeySet;
    }

    /**
     * Obtain the keySet.
     *
     * @return the keySet
     */
    public GordianKeySet getKeySet() {
        return theKeySet;
    }

    /**
     * Encrypt a value.
     *
     * @param pValue the value to encrypt.
     * @return the encryptedBytes
     * @throws OceanusException on error
     */
    public byte[] encryptValue(final Object pValue) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the encryptor */
            final MetisDataType myDataType = getDataTypeForValue(pValue);
            final PrometheusDataEncryptor myEncryptor = ENCRYPTORS.get(myDataType);

            final byte[] myBytes = myEncryptor.convertValue(theFormatter, pValue);
            return theKeySet == null ? null : theKeySet.encryptBytes(myBytes);
        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);
        }
    }

    /**
     * Encrypt a value.
     *
     * @param pCurrent the current value
     * @param pValue   the value to encrypt.
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
     *
     * @param pValue the value to encrypt.
     * @param pField the field definition
     * @return the encryptedPair.
     * @throws OceanusException on error
     */
    public PrometheusEncryptedPair encryptValue(final Object pValue,
                                                final MetisFieldDef pField) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* If we are passed a null value just return null */
            if (pValue == null) {
                return null;
            }

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
        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);
        }
    }

    /**
     * Decrypt bytes.
     *
     * @param pBytes the bytes to decrypt.
     * @param pField the field definition
     * @return the encryptedPair.
     * @throws OceanusException on error
     */
    PrometheusEncryptedPair decryptValue(final byte[] pBytes,
                                         final MetisFieldDef pField) throws OceanusException {
        /* Protect agains exceptions */
        try {
            /* Access the encryptor */
            final PrometheusDataEncryptor myEncryptor = ENCRYPTORS.get(pField.getDataType());
            if (myEncryptor == null) {
                throw new PrometheusLogicException(ERROR_DATATYPE);
            }

            /* Decrypt the data */
            final byte[] myDecrypted = theKeySet.decryptBytes(pBytes);
            final Object myValue = myEncryptor.parseValue(theFormatter, myDecrypted);
            return new PrometheusEncryptedPair(theKeySet, myValue, pBytes);
        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);
        }
    }

    /**
     * Decrypt bytes.
     *
     * @param pBytes the bytes to decrypt.
     * @param pClazz the class to decrypt to
     * @return the encryptedPair.
     * @throws OceanusException on error
     */
    PrometheusEncryptedPair decryptValue(final byte[] pBytes,
                                         final Class<?> pClazz) throws OceanusException {
        /* Protect agains exceptions */
        try {
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
        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);
        }
    }

    /**
     * Determine dataType.
     *
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
        if (pValue instanceof OceanusDate) {
            return MetisDataType.DATE;
        }
        if (pValue instanceof OceanusUnits) {
            return MetisDataType.UNITS;
        }
        if (pValue instanceof OceanusRate) {
            return MetisDataType.RATE;
        }
        if (pValue instanceof OceanusPrice) {
            return MetisDataType.PRICE;
        }
        if (pValue instanceof OceanusMoney) {
            return MetisDataType.MONEY;
        }
        if (pValue instanceof OceanusRatio) {
            return MetisDataType.RATIO;
        }

        /* Unsupported so reject */
        throw new PrometheusLogicException(ERROR_CLASS
                + pValue.getClass().getCanonicalName());
    }

    /**
     * Determine dataType.
     *
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
        if (OceanusDate.class.equals(pClazz)) {
            return MetisDataType.DATE;
        }
        if (OceanusUnits.class.equals(pClazz)) {
            return MetisDataType.UNITS;
        }
        if (OceanusRate.class.equals(pClazz)) {
            return MetisDataType.RATE;
        }
        if (OceanusPrice.class.equals(pClazz)) {
            return MetisDataType.PRICE;
        }
        if (OceanusMoney.class.equals(pClazz)) {
            return MetisDataType.MONEY;
        }
        if (OceanusRatio.class.equals(pClazz)) {
            return MetisDataType.RATIO;
        }

        /* Unsupported so reject */
        throw new PrometheusLogicException(ERROR_CLASS
                + pClazz.getCanonicalName());
    }

    /**
     * Build the encryptor map.
     *
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
     *
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
         *
         * @param pFormatter the data formatter
         * @param pValue     the value to convert.
         * @return the converted bytes.
         * @throws OceanusException on error
         */
        byte[] convertValue(OceanusDataFormatter pFormatter,
                            Object pValue) throws OceanusException;

        /**
         * Parse a value from bytes.
         *
         * @param pFormatter the data formatter
         * @param pBytes     the bytes to parse.
         * @return the parsed value.
         * @throws OceanusException on error
         */
        Object parseValue(OceanusDataFormatter pFormatter,
                          byte[] pBytes) throws OceanusException;
    }

    /**
     * DateEncryptor.
     */
    private static final class PrometheusDateEncryptor
            implements PrometheusDataEncryptor {
        @Override
        public byte[] convertValue(final OceanusDataFormatter pFormatter,
                                   final Object pValue) {
            return pFormatter.getDateFormatter().toBytes((OceanusDate) pValue);
        }

        @Override
        public Object parseValue(final OceanusDataFormatter pFormatter,
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
        public byte[] convertValue(final OceanusDataFormatter pFormatter,
                                   final Object pValue) {
            return OceanusDataConverter.shortToByteArray((short) pValue);
        }

        @Override
        public Object parseValue(final OceanusDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return OceanusDataConverter.byteArrayToShort(pBytes);
        }
    }

    /**
     * IntegerEncryptor.
     */
    private static final class PrometheusIntegerEncryptor
            implements PrometheusDataEncryptor {
        @Override
        public byte[] convertValue(final OceanusDataFormatter pFormatter,
                                   final Object pValue) {
            return OceanusDataConverter.integerToByteArray((int) pValue);
        }

        @Override
        public Object parseValue(final OceanusDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return OceanusDataConverter.byteArrayToInteger(pBytes);
        }
    }

    /**
     * LongEncryptor.
     */
    private static final class PrometheusLongEncryptor
            implements PrometheusDataEncryptor {
        @Override
        public byte[] convertValue(final OceanusDataFormatter pFormatter,
                                   final Object pValue) {
            return OceanusDataConverter.longToByteArray((long) pValue);
        }

        @Override
        public Object parseValue(final OceanusDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return OceanusDataConverter.byteArrayToLong(pBytes);
        }
    }

    /**
     * BooleanEncryptor.
     */
    private static final class PrometheusBooleanEncryptor
            implements PrometheusDataEncryptor {
        @Override
        public byte[] convertValue(final OceanusDataFormatter pFormatter,
                                   final Object pValue) {
            return OceanusDataConverter.stringToByteArray(pValue.toString());
        }

        @Override
        public Object parseValue(final OceanusDataFormatter pFormatter,
                                 final byte[] pBytes) {
            final String myBoolString = OceanusDataConverter.byteArrayToString(pBytes);
            return Boolean.parseBoolean(myBoolString);
        }
    }

    /**
     * StringEncryptor.
     */
    private static final class PrometheusStringEncryptor
            implements PrometheusDataEncryptor {
        @Override
        public byte[] convertValue(final OceanusDataFormatter pFormatter,
                                   final Object pValue) {
            return OceanusDataConverter.stringToByteArray((String) pValue);
        }

        @Override
        public Object parseValue(final OceanusDataFormatter pFormatter,
                                 final byte[] pBytes) {
            return OceanusDataConverter.byteArrayToString(pBytes);
        }
    }

    /**
     * CharArrayEncryptor.
     */
    private static final class PrometheusCharArrayEncryptor
            implements PrometheusDataEncryptor {
        @Override
        public byte[] convertValue(final OceanusDataFormatter pFormatter,
                                   final Object pValue) throws OceanusException {
            return OceanusDataConverter.charsToByteArray((char[]) pValue);
        }

        @Override
        public Object parseValue(final OceanusDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return OceanusDataConverter.bytesToCharArray(pBytes);
        }
    }

    /**
     * DecimalEncryptor.
     */
    private abstract static class PrometheusDecimalEncryptor
            implements PrometheusDataEncryptor {
        @Override
        public byte[] convertValue(final OceanusDataFormatter pFormatter,
                                   final Object pValue) {
            return ((OceanusDecimal) pValue).toBytes();
        }
    }

    /**
     * MoneyEncryptor.
     */
    private static final class PrometheusMoneyEncryptor
            extends PrometheusDecimalEncryptor {
        @Override
        public Object parseValue(final OceanusDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return new OceanusMoney(pBytes);
        }
    }

    /**
     * PriceEncryptor.
     */
    private static final class PrometheusPriceEncryptor
            extends PrometheusDecimalEncryptor {
        @Override
        public Object parseValue(final OceanusDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return new OceanusPrice(pBytes);
        }
    }

    /**
     * RatioEncryptor.
     */
    private static final class PrometheusRatioEncryptor
            extends PrometheusDecimalEncryptor {
        @Override
        public Object parseValue(final OceanusDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return new OceanusRatio(pBytes);
        }
    }

    /**
     * UnitsEncryptor.
     */
    private static final class PrometheusUnitsEncryptor
            extends PrometheusDecimalEncryptor {
        @Override
        public Object parseValue(final OceanusDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return new OceanusUnits(pBytes);
        }
    }

    /**
     * RateEncryptor.
     */
    private static final class PrometheusRateEncryptor
            extends PrometheusDecimalEncryptor {
        @Override
        public Object parseValue(final OceanusDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return new OceanusRate(pBytes);
        }
    }
}
