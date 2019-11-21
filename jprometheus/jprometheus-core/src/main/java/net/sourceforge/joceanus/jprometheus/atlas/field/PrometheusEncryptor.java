/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.atlas.field;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.PrometheusLogicException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Encryptor/Decryptor.
 */
public class PrometheusEncryptor {
    /**
     * Encrypted data conversion failure message.
     */
    private static final String ERROR_BYTES_CONVERT = "Failed to convert value from bytes";

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
    private final MetisDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param pKeySet the keySet
     */
    PrometheusEncryptor(final MetisDataFormatter pFormatter,
                        final GordianKeySet pKeySet) {
        theFormatter = pFormatter;
        theKeySet = pKeySet;
    }

    /**
     * Encrypt a value.
     * @param pValue the value to encrypt.
     * @param pField the field definition
     * @return the encryptedPair.
     * @throws OceanusException on error
     */
    PrometheusEncryptedPair encryptValue(final Object pValue,
                                         final MetisFieldDef pField) throws OceanusException {
        /* Access the encryptor */
        final PrometheusDataEncryptor myEncryptor = ENCRYPTORS.get(pField.getDataType());
        if (myEncryptor == null) {
            throw new PrometheusLogicException("Unsupported Data Type");
        }

        /* Encrypt the data */
        final byte[] myBytes = myEncryptor.convertValue(theFormatter, pValue);
        final byte[] myEncrypted = theKeySet.encryptBytes(myBytes);
        return new PrometheusEncryptedPair(pValue, myEncrypted);
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
            throw new PrometheusLogicException("Unsupported Data Type");
        }

        /* Decrypt the data */
        final byte[] myDecrypted = theKeySet.decryptBytes(pBytes);
        final Object myValue = myEncryptor.parseValue(theFormatter, myDecrypted);
        return new PrometheusEncryptedPair(myValue, pBytes);
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
        myMap.put(MetisDataType.DILUTION, new PrometheusDilutionEncryptor());
        myMap.put(MetisDataType.RATIO, new PrometheusRatioEncryptor());
        return myMap;
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
        byte[] convertValue(MetisDataFormatter pFormatter,
                            Object pValue) throws OceanusException;

        /**
         * Parse a value from bytes.
         * @param pFormatter the data formatter
         * @param pBytes the bytes to parse.
         * @return the parsed value.
         * @throws OceanusException on error
         */
        Object parseValue(MetisDataFormatter pFormatter,
                          byte[] pBytes) throws OceanusException;
    }

    /**
     * DateEncryptor.
     */
    private static class PrometheusDateEncryptor
        implements PrometheusDataEncryptor {

        @Override
        public byte[] convertValue(final MetisDataFormatter pFormatter,
                                   final Object pValue) {
            final String myDate = pFormatter.getDateFormatter().formatDate((TethysDate) pValue);
            return TethysDataConverter.stringToByteArray(myDate);
        }

        @Override
        public Object parseValue(final MetisDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            try {
                final String myDateString = TethysDataConverter.byteArrayToString(pBytes);
                return pFormatter.getDateFormatter().parseDate(myDateString);
            } catch (IllegalArgumentException e) {
                throw new PrometheusDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * IntegerEncryptor.
     */
    private static class PrometheusShortEncryptor
            implements PrometheusDataEncryptor {

        @Override
        public byte[] convertValue(final MetisDataFormatter pFormatter,
                                   final Object pValue) {
            return TethysDataConverter.stringToByteArray(pValue.toString());
        }

        @Override
        public Object parseValue(final MetisDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            try {
                final String myShortString = TethysDataConverter.byteArrayToString(pBytes);
                return Short.parseShort(myShortString);
            } catch (NumberFormatException e) {
                throw new PrometheusDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }
    /**
     * IntegerEncryptor.
     */
    private static class PrometheusIntegerEncryptor
            implements PrometheusDataEncryptor {

        @Override
        public byte[] convertValue(final MetisDataFormatter pFormatter,
                                   final Object pValue) {
            return TethysDataConverter.stringToByteArray(pValue.toString());
        }

        @Override
        public Object parseValue(final MetisDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            try {
                final String myIntString = TethysDataConverter.byteArrayToString(pBytes);
                return Integer.parseInt(myIntString);
            } catch (NumberFormatException e) {
                throw new PrometheusDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * LongEncryptor.
     */
    private static class PrometheusLongEncryptor
            implements PrometheusDataEncryptor {

        @Override
        public byte[] convertValue(final MetisDataFormatter pFormatter,
                                   final Object pValue) {
            return TethysDataConverter.stringToByteArray(pValue.toString());
        }

        @Override
        public Object parseValue(final MetisDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            try {
                final String myLongString = TethysDataConverter.byteArrayToString(pBytes);
                return Long.parseLong(myLongString);
            } catch (NumberFormatException e) {
                throw new PrometheusDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * BooleanEncryptor.
     */
    private static class PrometheusBooleanEncryptor
            implements PrometheusDataEncryptor {

        @Override
        public byte[] convertValue(final MetisDataFormatter pFormatter,
                                   final Object pValue) {
            return TethysDataConverter.stringToByteArray(pValue.toString());
        }

        @Override
        public Object parseValue(final MetisDataFormatter pFormatter,
                                 final byte[] pBytes) {
            final String myBoolString = TethysDataConverter.byteArrayToString(pBytes);
            return Boolean.parseBoolean(myBoolString);
        }
    }

    /**
     * StringEncryptor.
     */
    private static class PrometheusStringEncryptor
            implements PrometheusDataEncryptor {

        @Override
        public byte[] convertValue(final MetisDataFormatter pFormatter,
                                   final Object pValue) {
            return TethysDataConverter.stringToByteArray((String) pValue);
        }

        @Override
        public Object parseValue(final MetisDataFormatter pFormatter,
                                 final byte[] pBytes) {
            return TethysDataConverter.byteArrayToString(pBytes);
        }
    }

    /**
     * CharArrayEncryptor.
     */
    private static class PrometheusCharArrayEncryptor
            implements PrometheusDataEncryptor {

        @Override
        public byte[] convertValue(final MetisDataFormatter pFormatter,
                                   final Object pValue) throws OceanusException {
            return TethysDataConverter.charsToByteArray((char[]) pValue);
        }

        @Override
        public Object parseValue(final MetisDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return TethysDataConverter.bytesToCharArray(pBytes);
        }
    }

    /**
     * MoneyEncryptor.
     */
    private static class PrometheusMoneyEncryptor
            implements PrometheusDataEncryptor {

        @Override
        public byte[] convertValue(final MetisDataFormatter pFormatter,
                                   final Object pValue) {
            final String myDecString = pFormatter.getDecimalFormatter().toCurrencyString((TethysMoney) pValue);
            return TethysDataConverter.stringToByteArray(myDecString);
        }

        @Override
        public Object parseValue(final MetisDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            try {
                final String myDecString = TethysDataConverter.byteArrayToString(pBytes);
                return pFormatter.getDecimalParser().parseMoneyValue(myDecString);
            } catch (IllegalArgumentException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * PriceEncryptor.
     */
    private static class PrometheusPriceEncryptor
            extends PrometheusMoneyEncryptor {

        @Override
        public Object parseValue(final MetisDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            try {
                final String myDecString = TethysDataConverter.byteArrayToString(pBytes);
                return pFormatter.getDecimalParser().parsePriceValue(myDecString);
            } catch (IllegalArgumentException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * RatioEncryptor.
     */
    private static class PrometheusRatioEncryptor
            implements PrometheusDataEncryptor {

        @Override
        public byte[] convertValue(final MetisDataFormatter pFormatter,
                                   final Object pValue) {
            final String myDecString = pValue.toString();
            return TethysDataConverter.stringToByteArray(myDecString);
        }

        @Override
        public Object parseValue(final MetisDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            try {
                final String myDecString = TethysDataConverter.byteArrayToString(pBytes);
                return new TethysRatio(myDecString);
            } catch (IllegalArgumentException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * UnitsEncryptor.
     */
    private static class PrometheusUnitsEncryptor
            extends PrometheusRatioEncryptor {

        @Override
        public Object parseValue(final MetisDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            try {
                final String myDecString = TethysDataConverter.byteArrayToString(pBytes);
                return new TethysUnits(myDecString);
            } catch (IllegalArgumentException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * RateEncryptor.
     */
    private static class PrometheusRateEncryptor
            extends PrometheusRatioEncryptor {

        @Override
        public Object parseValue(final MetisDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            try {
                final String myDecString = TethysDataConverter.byteArrayToString(pBytes);
                return new TethysRate(myDecString);
            } catch (IllegalArgumentException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * DilutionEncryptor.
     */
    private static class PrometheusDilutionEncryptor
            extends PrometheusRatioEncryptor {

        @Override
        public Object parseValue(final MetisDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            try {
                final String myDecString = TethysDataConverter.byteArrayToString(pBytes);
                return new TethysDilution(myDecString);
            } catch (IllegalArgumentException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }
}
