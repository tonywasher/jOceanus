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
package net.sourceforge.joceanus.prometheus.data;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataType;
import net.sourceforge.joceanus.prometheus.PrometheusDataException;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.OceanusDataConverter;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * FieldGenerator.
 */
public class PrometheusFieldGenerator {
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
    private final TethysUIDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param pKeySet the keySet
     */
    public PrometheusFieldGenerator(final TethysUIDataFormatter pFormatter,
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
        final MetisDataType myDataType = PrometheusEncryptor.getDataTypeForValue(pValue);
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
     * Decrypt bytes.
     * @param pBytes the bytes to decrypt.
     * @param pClazz the class of the value
     * @return the encryptedPair.
     * @throws OceanusException on error
     */
    public PrometheusEncryptedPair decryptValue(final byte[] pBytes,
                                                final Class<?> pClazz) throws OceanusException {
        /* Access the encryptor */
        final MetisDataType myDataType = PrometheusEncryptor.getDataTypeForClass(pClazz);
        final PrometheusDataEncryptor myEncryptor = ENCRYPTORS.get(myDataType);

        /* Decrypt the data */
        final byte[] myDecrypted = theKeySet.decryptBytes(pBytes);
        final Object myValue = myEncryptor.parseValue(theFormatter, myDecrypted);
        return new PrometheusEncryptedPair(theKeySet, myValue, pBytes);
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
            return pFormatter.getDateFormatter().toBytes((OceanusDate) pValue);
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
            return OceanusDataConverter.shortToByteArray((short) pValue);
        }

        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
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
        public byte[] convertValue(final TethysUIDataFormatter pFormatter,
                                   final Object pValue) {
            return OceanusDataConverter.integerToByteArray((int) pValue);
        }

        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
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
        public byte[] convertValue(final TethysUIDataFormatter pFormatter,
                                   final Object pValue) {
            return OceanusDataConverter.longToByteArray((long) pValue);
        }

        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
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
        public byte[] convertValue(final TethysUIDataFormatter pFormatter,
                                   final Object pValue) {
            return OceanusDataConverter.stringToByteArray(pValue.toString());
        }

        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
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
        public byte[] convertValue(final TethysUIDataFormatter pFormatter,
                                   final Object pValue) {
            return OceanusDataConverter.stringToByteArray((String) pValue);
        }

        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
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
        public byte[] convertValue(final TethysUIDataFormatter pFormatter,
                                   final Object pValue) throws OceanusException {
            return OceanusDataConverter.charsToByteArray((char[]) pValue);
        }

        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
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
        public byte[] convertValue(final TethysUIDataFormatter pFormatter,
                                   final Object pValue) {
            return ((OceanusDecimal) pValue).toBytes();
        }
    }

    /**
     * MoneyEncryptor.
     */
    private static class PrometheusMoneyEncryptor
            extends PrometheusDecimalEncryptor {
        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return new OceanusMoney(pBytes);
        }
    }

    /**
     * PriceEncryptor.
     */
    private static final class PrometheusPriceEncryptor
            extends PrometheusMoneyEncryptor {
        @Override
        public Object parseValue(final TethysUIDataFormatter pFormatter,
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
        public Object parseValue(final TethysUIDataFormatter pFormatter,
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
        public Object parseValue(final TethysUIDataFormatter pFormatter,
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
        public Object parseValue(final TethysUIDataFormatter pFormatter,
                                 final byte[] pBytes) throws OceanusException {
            return new OceanusRate(pBytes);
        }
    }
}