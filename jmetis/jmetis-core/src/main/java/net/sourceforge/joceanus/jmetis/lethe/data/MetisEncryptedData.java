/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2022 Tony Washer
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
import java.util.Arrays;
import java.util.Date;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jmetis.MetisLogicException;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference.MetisDataDiffers;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Encrypted data types.
 */
public final class MetisEncryptedData {
    /**
     * The Hash prime.
     */
    private static final int HASH_PRIME = 19;

    /**
     * Encrypted Money length.
     */
    public static final int MONEYLEN = 15;

    /**
     * Encrypted Units length.
     */
    public static final int UNITSLEN = 15;

    /**
     * Encrypted Rate length.
     */
    public static final int RATELEN = 10;

    /**
     * Encrypted Price length.
     */
    public static final int PRICELEN = 15;

    /**
     * Encrypted Dilution length.
     */
    public static final int DILUTELEN = 10;

    /**
     * Encrypted Ratio length.
     */
    public static final int RATIOLEN = 10;

    /**
     * Encrypted data conversion failure message.
     */
    private static final String ERROR_BYTES_CONVERT = "Failed to convert value from bytes";

    /**
     * Encryption not initialised failure message.
     */
    private static final String ERROR_INIT = "Encryption is not initialised";

    /**
     * Private constructor to avoid instantiation.
     */
    private MetisEncryptedData() {
    }

    /**
     * Obtain value for a field (which may be null).
     * @param <T> the field type
     * @param pField the field to obtain the value for
     * @return the value
     */
    public static <T> T getFieldValue(final MetisEncryptedField<T> pField) {
        return (pField == null)
                                ? null
                                : pField.getValue();
    }

    /**
     * Obtain encrypted bytes for a field (which may be null).
     * @param pField the field to obtain the encrypted bytes for
     * @return the value
     */
    public static byte[] getFieldBytes(final MetisEncryptedField<?> pField) {
        return (pField == null)
                                ? null
                                : pField.getBytes();
    }

    /**
     * The generic encrypted object class.
     * @param <T> the field type
     */
    public abstract static class MetisEncryptedField<T>
            implements MetisDataObjectFormat, MetisDataDiffers {
        /**
         * Encryption KeySet.
         */
        private GordianKeySet theKeySet;

        /**
         * Encrypted value.
         */
        private byte[] theEncrypted;

        /**
         * Decrypted value.
         */
        private T theDecrypted;

        /**
         * Data formatter.
         */
        private MetisDataFormatter theFormatter;

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        private MetisEncryptedField(final GordianKeySet pKeySet,
                                    final MetisDataFormatter pFormatter,
                                    final byte[] pEncrypted) throws OceanusException {
            /* Store the keySet and formatter */
            theKeySet = pKeySet;
            theFormatter = pFormatter;

            /* Store the encrypted value */
            theEncrypted = Arrays.copyOf(pEncrypted, pEncrypted.length);

            /* Reject if encryption is not initialised */
            if (theKeySet == null) {
                throw new MetisLogicException(ERROR_INIT);
            }

            /* Decrypt the Bytes */
            final byte[] myBytes = theKeySet.decryptBytes(theEncrypted);

            /* Set the decrypted value */
            theDecrypted = parseBytes(myBytes);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        private MetisEncryptedField(final GordianKeySet pKeySet,
                                    final MetisDataFormatter pFormatter,
                                    final T pUnencrypted) throws OceanusException {
            /* Store the keySet and formatter */
            theKeySet = pKeySet;
            theFormatter = pFormatter;

            /* Store the value */
            theDecrypted = pUnencrypted;

            /* Return if we have no encryption yet */
            if (theKeySet == null) {
                return;
            }

            /* encrypt the value */
            encryptValue();
        }

        /**
         * Obtain the formatter.
         * @return the formatter
         */
        protected MetisDataFormatter getFormatter() {
            return theFormatter;
        }

        /**
         * Encrypt the value.
         * @throws OceanusException on error
         */
        private void encryptValue() throws OceanusException {
            /* Reject if encryption is not initialised */
            if (theKeySet == null) {
                throw new MetisLogicException(ERROR_INIT);
            }

            /* Obtain the bytes representation of the value */
            final byte[] myBytes = getBytesForEncryption();

            /* Encrypt the Bytes */
            theEncrypted = theKeySet.encryptBytes(myBytes);
        }

        /**
         * Obtain the keySet.
         * @return the keySet
         */
        public GordianKeySet getKeySet() {
            return theKeySet;
        }

        /**
         * Obtain the encrypted value.
         * @return the encrypted value
         */
        public byte[] getBytes() {
            return (theEncrypted == null)
                                          ? null
                                          : Arrays.copyOf(theEncrypted, theEncrypted.length);
        }

        /**
         * Obtain the unencrypted value.
         * @return the unencrypted value
         */
        public T getValue() {
            return theDecrypted;
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            /* Format the unencrypted field */
            return toString();
        }

        @Override
        public String toString() {
            return theFormatter.formatObject(theDecrypted);
        }

        /**
         * Parse the decrypted bytes.
         * @param pBytes the decrypted bytes
         * @return the decrypted value
         * @throws OceanusException on error
         */
        protected abstract T parseBytes(byte[] pBytes) throws OceanusException;

        /**
         * Obtain the bytes format to encrypt.
         * @return the bytes to encrypt
         * @throws OceanusException on error
         */
        protected abstract byte[] getBytesForEncryption() throws OceanusException;

        /**
         * Apply fresh encryption to value.
         * @param pKeySet the keySet
         * @throws OceanusException on error
         */
        protected void applyEncryption(final GordianKeySet pKeySet) throws OceanusException {
            /* Store the keySet */
            theKeySet = pKeySet;

            /* Encrypt the value */
            encryptValue();
        }

        /**
         * Adopt Encryption.
         * @param pKeySet the keySet
         * @param pFormatter the formatter
         * @param pField field to adopt encryption from
         * @throws OceanusException on error
         */
        protected void adoptEncryption(final GordianKeySet pKeySet,
                                       final MetisDataFormatter pFormatter,
                                       final MetisEncryptedField<?> pField) throws OceanusException {
            /* Store the keySet and formatter */
            theKeySet = pKeySet;
            theFormatter = pFormatter;

            /* If we need to renew the encryption */
            if (pField == null
                || MetisDataDifference.difference(pKeySet, pField.getKeySet()).isDifferent()
                || MetisDataDifference.difference(getValue(), pField.getValue()).isDifferent()) {
                /* encrypt the value */
                encryptValue();

                /* else we can simply adopt the underlying encryption */
            } else {
                /* Pick up the underlying encryption */
                theEncrypted = pField.getBytes();
            }
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Make sure that the object is the same class */
            if (pThat.getClass() != this.getClass()) {
                return false;
            }

            /* Access the target field */
            final MetisEncryptedField<?> myThat = (MetisEncryptedField<?>) pThat;

            /* Check differences */
            if (MetisDataDifference.difference(getValue(), myThat.getValue()).isDifferent()) {
                return false;
            }

            /* Check encryption */
            return Arrays.equals(getBytes(), myThat.getBytes());
        }

        @Override
        public int hashCode() {
            /* Calculate hash allowing for field that has not been encrypted yet */
            int myHashCode = HASH_PRIME
                             * getValue().hashCode();
            if (theEncrypted != null) {
                myHashCode += Arrays.hashCode(theEncrypted);
            }
            return myHashCode;
        }

        @Override
        public MetisDataDifference differs(final Object pThat) {
            /* Reject if null */
            if (pThat == null) {
                return MetisDataDifference.DIFFERENT;
            }

            /* Reject if wrong class */
            if (!getClass().equals(pThat.getClass())) {
                return MetisDataDifference.DIFFERENT;
            }

            /* Access as correct class */
            final MetisEncryptedField<?> myField = (MetisEncryptedField<?>) pThat;

            /* Compare Unencrypted value */
            if (MetisDataDifference.difference(getValue(), myField.getValue()).isDifferent()) {
                return MetisDataDifference.DIFFERENT;
            }

            /* Compare Encrypted value */
            if (!Arrays.equals(getBytes(), myField.getBytes())) {
                return MetisDataDifference.SECURITY;
            }

            /* Item is the Same */
            return MetisDataDifference.IDENTICAL;
        }
    }

    /**
     * The encrypted String class.
     */
    public static final class MetisEncryptedString
            extends MetisEncryptedField<String> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedString(final GordianKeySet pKeySet,
                                       final MetisDataFormatter pFormatter,
                                       final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedString(final GordianKeySet pKeySet,
                                       final MetisDataFormatter pFormatter,
                                       final String pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected String parseBytes(final byte[] pBytes) {
            /* Convert the byte array to a string */
            return TethysDataConverter.byteArrayToString(pBytes);
        }

        @Override
        protected byte[] getBytesForEncryption() {
            /* Convert the string to a byte array */
            return TethysDataConverter.stringToByteArray(getValue());
        }
    }

    /**
     * The encrypted Short class.
     */
    public static final class MetisEncryptedShort
            extends MetisEncryptedField<Short> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedShort(final GordianKeySet pKeySet,
                                      final MetisDataFormatter pFormatter,
                                      final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedShort(final GordianKeySet pKeySet,
                                      final MetisDataFormatter pFormatter,
                                      final Short pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected Short parseBytes(final byte[] pBytes) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a short */
                return Short.parseShort(TethysDataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (NumberFormatException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() {
            /* Convert the short to a string and then a byte array */
            return TethysDataConverter.stringToByteArray(getValue().toString());
        }
    }

    /**
     * The encrypted Integer class.
     */
    public static final class MetisEncryptedInteger
            extends MetisEncryptedField<Integer> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedInteger(final GordianKeySet pKeySet,
                                        final MetisDataFormatter pFormatter,
                                        final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedInteger(final GordianKeySet pKeySet,
                                        final MetisDataFormatter pFormatter,
                                        final Integer pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected Integer parseBytes(final byte[] pBytes) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                return Integer.parseInt(TethysDataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (NumberFormatException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() {
            /* Convert the integer to a string and then a byte array */
            return TethysDataConverter.stringToByteArray(getValue().toString());
        }
    }

    /**
     * The encrypted Long class.
     */
    public static final class MetisEncryptedLong
            extends MetisEncryptedField<Long> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedLong(final GordianKeySet pKeySet,
                                     final MetisDataFormatter pFormatter,
                                     final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedLong(final GordianKeySet pKeySet,
                                     final MetisDataFormatter pFormatter,
                                     final Long pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected Long parseBytes(final byte[] pBytes) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a long */
                return Long.parseLong(TethysDataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (NumberFormatException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() {
            /* Convert the long to a string and then a byte array */
            return TethysDataConverter.stringToByteArray(getValue().toString());
        }
    }

    /**
     * The encrypted Float class.
     */
    public static final class MetisEncryptedFloat
            extends MetisEncryptedField<Float> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedFloat(final GordianKeySet pKeySet,
                                      final MetisDataFormatter pFormatter,
                                      final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedFloat(final GordianKeySet pKeySet,
                                      final MetisDataFormatter pFormatter,
                                      final Float pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected Float parseBytes(final byte[] pBytes) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a float */
                return Float.parseFloat(TethysDataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (NumberFormatException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() {
            /* Convert the float to a string and then a byte array */
            return TethysDataConverter.stringToByteArray(getValue().toString());
        }
    }

    /**
     * The encrypted Double class.
     */
    public static final class MetisEncryptedDouble
            extends MetisEncryptedField<Double> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedDouble(final GordianKeySet pKeySet,
                                       final MetisDataFormatter pFormatter,
                                       final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedDouble(final GordianKeySet pKeySet,
                                       final MetisDataFormatter pFormatter,
                                       final Double pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected Double parseBytes(final byte[] pBytes) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a Double */
                return Double.parseDouble(TethysDataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (NumberFormatException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() {
            /* Convert the double to a string and then a byte array */
            return TethysDataConverter.stringToByteArray(getValue().toString());
        }
    }

    /**
     * The encrypted Boolean class.
     */
    public static final class MetisEncryptedBoolean
            extends MetisEncryptedField<Boolean> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedBoolean(final GordianKeySet pKeySet,
                                        final MetisDataFormatter pFormatter,
                                        final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedBoolean(final GordianKeySet pKeySet,
                                        final MetisDataFormatter pFormatter,
                                        final Boolean pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected Boolean parseBytes(final byte[] pBytes) {
            /* Convert the byte array to a string and then an integer */
            return Boolean.parseBoolean(TethysDataConverter.byteArrayToString(pBytes));
        }

        @Override
        protected byte[] getBytesForEncryption() {
            /* Convert the boolean to a string and then a byte array */
            return TethysDataConverter.stringToByteArray(getValue().toString());
        }
    }

    /**
     * The encrypted Java Date class.
     */
    public static final class MetisEncryptedJavaDate
            extends MetisEncryptedField<Date> {
        /**
         * Date Formatter.
         */
        private TethysDateFormatter theDateFormatter;

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedJavaDate(final GordianKeySet pKeySet,
                                         final MetisDataFormatter pFormatter,
                                         final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedJavaDate(final GordianKeySet pKeySet,
                                         final MetisDataFormatter pFormatter,
                                         final Date pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        /**
         * Obtain the formatter.
         * @return the formatter
         */
        private TethysDateFormatter getDateFormatter() {
            if (theDateFormatter == null) {
                theDateFormatter = getFormatter().getDateFormatter();
            }
            return theDateFormatter;
        }

        @Override
        protected Date parseBytes(final byte[] pBytes) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a date */
                final String myInput = TethysDataConverter.byteArrayToString(pBytes);
                return getDateFormatter().parseJavaDate(myInput);

                /* Catch Exceptions */
            } catch (IllegalArgumentException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() {
            /* Convert the date to a string and then a byte array */
            final String myInput = getDateFormatter().formatJavaDate(getValue());
            return TethysDataConverter.stringToByteArray(myInput);
        }
    }

    /**
     * The encrypted TethysDate class.
     */
    public static final class MetisEncryptedDate
            extends MetisEncryptedField<TethysDate> {
        /**
         * Date Formatter.
         */
        private TethysDateFormatter theDateFormatter;

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedDate(final GordianKeySet pKeySet,
                                     final MetisDataFormatter pFormatter,
                                     final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedDate(final GordianKeySet pKeySet,
                                     final MetisDataFormatter pFormatter,
                                     final TethysDate pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        /**
         * Obtain the formatter.
         * @return the formatter
         */
        private TethysDateFormatter getDateFormatter() {
            if (theDateFormatter == null) {
                theDateFormatter = getFormatter().getDateFormatter();
            }
            return theDateFormatter;
        }

        @Override
        protected TethysDate parseBytes(final byte[] pBytes) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                final String myInput = TethysDataConverter.byteArrayToString(pBytes);
                return getDateFormatter().parseDate(myInput);

                /* Catch Exceptions */
            } catch (IllegalArgumentException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() {
            /* Convert the date to a string and then a byte array */
            final String myInput = getDateFormatter().formatDate(getValue());
            return TethysDataConverter.stringToByteArray(myInput);
        }
    }

    /**
     * The encrypted CharArray class.
     */
    public static final class MetisEncryptedCharArray
            extends MetisEncryptedField<char[]> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedCharArray(final GordianKeySet pKeySet,
                                          final MetisDataFormatter pFormatter,
                                          final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedCharArray(final GordianKeySet pKeySet,
                                          final MetisDataFormatter pFormatter,
                                          final char[] pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected char[] parseBytes(final byte[] pBytes) throws OceanusException {
            return TethysDataConverter.bytesToCharArray(pBytes);
        }

        @Override
        protected byte[] getBytesForEncryption() throws OceanusException {
            return TethysDataConverter.charsToByteArray(getValue());
        }
    }

    /**
     * The encrypted BigInteger class.
     */
    public static final class MetisEncryptedBigInteger
            extends MetisEncryptedField<BigInteger> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedBigInteger(final GordianKeySet pKeySet,
                                           final MetisDataFormatter pFormatter,
                                           final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedBigInteger(final GordianKeySet pKeySet,
                                           final MetisDataFormatter pFormatter,
                                           final BigInteger pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected BigInteger parseBytes(final byte[] pBytes) throws OceanusException {
            try {
                return new BigInteger(pBytes);
                /* Catch Exceptions */
            } catch (NumberFormatException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() {
            return getValue().toByteArray();
        }
    }

    /**
     * The encrypted BigDecimal class.
     */
    public static final class MetisEncryptedBigDecimal
            extends MetisEncryptedField<BigDecimal> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedBigDecimal(final GordianKeySet pKeySet,
                                           final MetisDataFormatter pFormatter,
                                           final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedBigDecimal(final GordianKeySet pKeySet,
                                           final MetisDataFormatter pFormatter,
                                           final BigDecimal pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected BigDecimal parseBytes(final byte[] pBytes) throws OceanusException {
            try {
                return new BigDecimal(TethysDataConverter.byteArrayToString(pBytes));
                /* Catch Exceptions */
            } catch (NumberFormatException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() {
            return TethysDataConverter.stringToByteArray(getValue().toString());
        }
    }

    /**
     * The encrypted Decimal class.
     * @param <X> the decimal type
     */
    public abstract static class MetisEncryptedDecimal<X extends TethysDecimal>
            extends MetisEncryptedField<X> {
        /**
         * Decimal Formatter.
         */
        private TethysDecimalFormatter theDecimalFormatter;

        /**
         * Decimal Parser.
         */
        private TethysDecimalParser theDecimalParser;

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        private MetisEncryptedDecimal(final GordianKeySet pKeySet,
                                      final MetisDataFormatter pFormatter,
                                      final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        private MetisEncryptedDecimal(final GordianKeySet pKeySet,
                                      final MetisDataFormatter pFormatter,
                                      final X pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        /**
         * Obtain the formatter.
         * @return the formatter
         */
        protected TethysDecimalFormatter getDecimalFormatter() {
            if (theDecimalFormatter == null) {
                theDecimalFormatter = getFormatter().getDecimalFormatter();
            }
            return theDecimalFormatter;
        }

        /**
         * Obtain the parser.
         * @return the parser
         */
        protected TethysDecimalParser getDecimalParser() {
            if (theDecimalParser == null) {
                theDecimalParser = getFormatter().getDecimalParser();
            }
            return theDecimalParser;
        }

        @Override
        protected X parseBytes(final byte[] pBytes) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and parse it */
                final String myInput = TethysDataConverter.byteArrayToString(pBytes);
                return parseValue(myInput);

                /* Catch Exceptions */
            } catch (OceanusException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        /**
         * Parse a string value to get a value.
         * @param pValue the string value
         * @return the value
         * @throws OceanusException on error
         */
        protected abstract X parseValue(String pValue) throws OceanusException;

        @Override
        protected byte[] getBytesForEncryption() {
            /* Format the value */
            final String myValue = getValue().toString();

            /* Convert the string to a byte array */
            return TethysDataConverter.stringToByteArray(myValue);
        }
    }

    /**
     * The encrypted Money class.
     */
    public static final class MetisEncryptedMoney
            extends MetisEncryptedDecimal<TethysMoney> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedMoney(final GordianKeySet pKeySet,
                                      final MetisDataFormatter pFormatter,
                                      final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedMoney(final GordianKeySet pKeySet,
                                      final MetisDataFormatter pFormatter,
                                      final TethysMoney pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected byte[] getBytesForEncryption() {
            /* Format the value */
            final String myValue = getDecimalFormatter().toCurrencyString(getValue());

            /* Convert the string to a byte array */
            return TethysDataConverter.stringToByteArray(myValue);
        }

        @Override
        protected TethysMoney parseValue(final String pValue) throws OceanusException {
            try {
                return getDecimalParser().parseMoneyValue(pValue);
            } catch (IllegalArgumentException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Units class.
     */
    public static final class MetisEncryptedUnits
            extends MetisEncryptedDecimal<TethysUnits> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedUnits(final GordianKeySet pKeySet,
                                      final MetisDataFormatter pFormatter,
                                      final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedUnits(final GordianKeySet pKeySet,
                                      final MetisDataFormatter pFormatter,
                                      final TethysUnits pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected TethysUnits parseValue(final String pValue) throws OceanusException {
            try {
                return new TethysUnits(pValue);
            } catch (IllegalArgumentException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Rate class.
     */
    public static final class MetisEncryptedRate
            extends MetisEncryptedDecimal<TethysRate> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedRate(final GordianKeySet pKeySet,
                                     final MetisDataFormatter pFormatter,
                                     final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedRate(final GordianKeySet pKeySet,
                                     final MetisDataFormatter pFormatter,
                                     final TethysRate pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected TethysRate parseValue(final String pValue) throws OceanusException {
            try {
                return new TethysRate(pValue);
                /* Catch Exceptions */
            } catch (IllegalArgumentException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Price class.
     */
    public static final class MetisEncryptedPrice
            extends MetisEncryptedDecimal<TethysPrice> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedPrice(final GordianKeySet pKeySet,
                                      final MetisDataFormatter pFormatter,
                                      final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedPrice(final GordianKeySet pKeySet,
                                      final MetisDataFormatter pFormatter,
                                      final TethysPrice pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected byte[] getBytesForEncryption() {
            /* Format the value */
            final String myValue = getDecimalFormatter().toCurrencyString(getValue());

            /* Convert the string to a byte array */
            return TethysDataConverter.stringToByteArray(myValue);
        }

        @Override
        protected TethysPrice parseValue(final String pValue) throws OceanusException {
            try {
                return getDecimalParser().parsePriceValue(pValue);
            } catch (IllegalArgumentException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Dilution class.
     */
    public static final class MetisEncryptedDilution
            extends MetisEncryptedDecimal<TethysDilution> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedDilution(final GordianKeySet pKeySet,
                                         final MetisDataFormatter pFormatter,
                                         final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedDilution(final GordianKeySet pKeySet,
                                         final MetisDataFormatter pFormatter,
                                         final TethysDilution pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected TethysDilution parseValue(final String pValue) throws OceanusException {
            try {
                return new TethysDilution(pValue);
            } catch (IllegalArgumentException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Ratio class.
     */
    public static final class MetisEncryptedRatio
            extends MetisEncryptedDecimal<TethysRatio> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedRatio(final GordianKeySet pKeySet,
                                      final MetisDataFormatter pFormatter,
                                      final byte[] pEncrypted) throws OceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws OceanusException on error
         */
        protected MetisEncryptedRatio(final GordianKeySet pKeySet,
                                      final MetisDataFormatter pFormatter,
                                      final TethysRatio pUnencrypted) throws OceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected TethysRatio parseValue(final String pValue) throws OceanusException {
            try {
                return new TethysRatio(pValue);
            } catch (IllegalArgumentException e) {
                throw new MetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }
}
