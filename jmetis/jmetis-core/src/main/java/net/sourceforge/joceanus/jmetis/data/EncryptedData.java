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
import java.util.Arrays;
import java.util.Date;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jmetis.JMetisDataException;
import net.sourceforge.joceanus.jmetis.JMetisLogicException;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataDiffers;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataFormat;
import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * Encrypted data types.
 */
public final class EncryptedData {
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
     * Encrypted data conversion failure message.
     */
    private static final String ERROR_VALUE_CONVERT = "Failed to convert value to bytes";

    /**
     * Encryption not initialised failure message.
     */
    private static final String ERROR_INIT = "Encryption is not initialised";

    /**
     * Private constructor to avoid instantiation.
     */
    private EncryptedData() {
    }

    /**
     * Obtain value for a field (which may be null).
     * @param <T> the field type
     * @param pField the field to obtain the value for
     * @return the value
     */
    public static <T> T getValue(final EncryptedField<T> pField) {
        return (pField == null)
                                ? null
                                : pField.getValue();
    }

    /**
     * Obtain encrypted bytes for a field (which may be null).
     * @param pField the field to obtain the encrypted bytes for
     * @return the value
     */
    public static byte[] getBytes(final EncryptedField<?> pField) {
        return (pField == null)
                                ? null
                                : pField.getBytes();
    }

    /**
     * The generic encrypted object class.
     * @param <T> the field type
     */
    public abstract static class EncryptedField<T>
            implements JDataFormat, JDataDiffers {
        /**
         * Encryption KeySet.
         */
        private GordianKeySet theKeySet = null;

        /**
         * Encrypted value.
         */
        private byte[] theEncrypted = null;

        /**
         * Decrypted value.
         */
        private T theDecrypted = null;

        /**
         * Data formatter.
         */
        private JDataFormatter theFormatter;

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        private EncryptedField(final GordianKeySet pKeySet,
                               final JDataFormatter pFormatter,
                               final byte[] pEncrypted) throws JOceanusException {
            /* Store the keySet and formatter */
            theKeySet = pKeySet;
            theFormatter = pFormatter;

            /* Store the encrypted value */
            theEncrypted = Arrays.copyOf(pEncrypted, pEncrypted.length);

            /* Reject if encryption is not initialised */
            if (theKeySet == null) {
                throw new JMetisLogicException(ERROR_INIT);
            }

            /* Decrypt the Bytes */
            byte[] myBytes = theKeySet.decryptBytes(theEncrypted);

            /* Set the decrypted value */
            theDecrypted = parseBytes(myBytes);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        private EncryptedField(final GordianKeySet pKeySet,
                               final JDataFormatter pFormatter,
                               final T pUnencrypted) throws JOceanusException {
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
        protected JDataFormatter getFormatter() {
            return theFormatter;
        }

        /**
         * Encrypt the value.
         * @throws JOceanusException on error
         */
        private void encryptValue() throws JOceanusException {
            /* Reject if encryption is not initialised */
            if (theKeySet == null) {
                throw new JMetisLogicException(ERROR_INIT);
            }

            /* Obtain the bytes representation of the value */
            byte[] myBytes = getBytesForEncryption();

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
        public String formatObject() {
            /* Format the unencrypted field */
            return theFormatter.formatObject(theDecrypted);
        }

        @Override
        public String toString() {
            return formatObject();
        }

        /**
         * Parse the decrypted bytes.
         * @param pBytes the decrypted bytes
         * @return the decrypted value
         * @throws JOceanusException on error
         */
        protected abstract T parseBytes(byte[] pBytes) throws JOceanusException;

        /**
         * Obtain the bytes format to encrypt.
         * @return the bytes to encrypt
         * @throws JOceanusException on error
         */
        protected abstract byte[] getBytesForEncryption() throws JOceanusException;

        /**
         * Apply fresh encryption to value.
         * @param pKeySet the keySet
         * @throws JOceanusException on error
         */
        protected void applyEncryption(final GordianKeySet pKeySet) throws JOceanusException {
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
         * @throws JOceanusException on error
         */
        protected void adoptEncryption(final GordianKeySet pKeySet,
                                       final JDataFormatter pFormatter,
                                       final EncryptedField<?> pField) throws JOceanusException {
            /* Store the keySet and formatter */
            theKeySet = pKeySet;
            theFormatter = pFormatter;

            /* If we need to renew the encryption */
            if ((pField == null)
                || (Difference.getDifference(pKeySet, pField.getKeySet()).isDifferent())
                || (Difference.getDifference(getValue(), pField.getValue()).isDifferent())) {
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
            EncryptedField<?> myThat = (EncryptedField<?>) pThat;

            /* Check differences */
            if (Difference.getDifference(getValue(), myThat.getValue()).isDifferent()) {
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
        public Difference differs(final Object pThat) {
            /* Reject if null */
            if (pThat == null) {
                return Difference.DIFFERENT;
            }

            /* Reject if wrong class */
            if (!getClass().equals(pThat.getClass())) {
                return Difference.DIFFERENT;
            }

            /* Access as correct class */
            EncryptedField<?> myField = (EncryptedField<?>) pThat;

            /* Compare Unencrypted value */
            if (Difference.getDifference(getValue(), myField.getValue()).isDifferent()) {
                return Difference.DIFFERENT;
            }

            /* Compare Encrypted value */
            if (!Arrays.equals(getBytes(), myField.getBytes())) {
                return Difference.SECURITY;
            }

            /* Item is the Same */
            return Difference.IDENTICAL;
        }
    }

    /**
     * The encrypted String class.
     */
    public static final class EncryptedString
            extends EncryptedField<String> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedString(final GordianKeySet pKeySet,
                                  final JDataFormatter pFormatter,
                                  final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedString(final GordianKeySet pKeySet,
                                  final JDataFormatter pFormatter,
                                  final String pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected String parseBytes(final byte[] pBytes) throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string */
                return DataConverter.byteArrayToString(pBytes);

                /* Catch Exceptions */
            } catch (JOceanusException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the string to a byte array */
                return DataConverter.stringToByteArray(getValue());

                /* Catch Exceptions */
            } catch (JOceanusException e) {
                throw new JMetisDataException(ERROR_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Short class.
     */
    public static final class EncryptedShort
            extends EncryptedField<Short> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedShort(final GordianKeySet pKeySet,
                                 final JDataFormatter pFormatter,
                                 final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedShort(final GordianKeySet pKeySet,
                                 final JDataFormatter pFormatter,
                                 final Short pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected Short parseBytes(final byte[] pBytes) throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a short */
                return Short.parseShort(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (JOceanusException | NumberFormatException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the short to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (JOceanusException e) {
                throw new JMetisDataException(ERROR_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Integer class.
     */
    public static final class EncryptedInteger
            extends EncryptedField<Integer> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedInteger(final GordianKeySet pKeySet,
                                   final JDataFormatter pFormatter,
                                   final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedInteger(final GordianKeySet pKeySet,
                                   final JDataFormatter pFormatter,
                                   final Integer pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected Integer parseBytes(final byte[] pBytes) throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                return Integer.parseInt(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (JOceanusException | NumberFormatException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the integer to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (JOceanusException e) {
                throw new JMetisDataException(ERROR_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Long class.
     */
    public static final class EncryptedLong
            extends EncryptedField<Long> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedLong(final GordianKeySet pKeySet,
                                final JDataFormatter pFormatter,
                                final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedLong(final GordianKeySet pKeySet,
                                final JDataFormatter pFormatter,
                                final Long pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected Long parseBytes(final byte[] pBytes) throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a long */
                return Long.parseLong(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (JOceanusException | NumberFormatException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the long to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (JOceanusException e) {
                throw new JMetisDataException(ERROR_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Float class.
     */
    public static final class EncryptedFloat
            extends EncryptedField<Float> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedFloat(final GordianKeySet pKeySet,
                                 final JDataFormatter pFormatter,
                                 final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedFloat(final GordianKeySet pKeySet,
                                 final JDataFormatter pFormatter,
                                 final Float pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected Float parseBytes(final byte[] pBytes) throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a float */
                return Float.parseFloat(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (JOceanusException | NumberFormatException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the float to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (JOceanusException e) {
                throw new JMetisDataException(ERROR_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Double class.
     */
    public static final class EncryptedDouble
            extends EncryptedField<Double> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedDouble(final GordianKeySet pKeySet,
                                  final JDataFormatter pFormatter,
                                  final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedDouble(final GordianKeySet pKeySet,
                                  final JDataFormatter pFormatter,
                                  final Double pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected Double parseBytes(final byte[] pBytes) throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a Double */
                return Double.parseDouble(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (JOceanusException | NumberFormatException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the double to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (JOceanusException e) {
                throw new JMetisDataException(ERROR_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Boolean class.
     */
    public static final class EncryptedBoolean
            extends EncryptedField<Boolean> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedBoolean(final GordianKeySet pKeySet,
                                   final JDataFormatter pFormatter,
                                   final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedBoolean(final GordianKeySet pKeySet,
                                   final JDataFormatter pFormatter,
                                   final Boolean pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected Boolean parseBytes(final byte[] pBytes) throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                return Boolean.parseBoolean(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (JOceanusException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the boolean to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (JOceanusException e) {
                throw new JMetisDataException(ERROR_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Date class.
     */
    public static final class EncryptedDate
            extends EncryptedField<Date> {
        /**
         * Date Formatter.
         */
        private JDateDayFormatter theDateFormatter = null;

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedDate(final GordianKeySet pKeySet,
                                final JDataFormatter pFormatter,
                                final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedDate(final GordianKeySet pKeySet,
                                final JDataFormatter pFormatter,
                                final Date pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        /**
         * Obtain the formatter.
         * @return the formatter
         */
        private JDateDayFormatter getDateFormatter() {
            if (theDateFormatter == null) {
                theDateFormatter = getFormatter().getDateFormatter();
            }
            return theDateFormatter;
        }

        @Override
        protected Date parseBytes(final byte[] pBytes) throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a date */
                String myInput = DataConverter.byteArrayToString(pBytes);
                return getDateFormatter().parseDate(myInput);

                /* Catch Exceptions */
            } catch (JOceanusException | IllegalArgumentException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the date to a string and then a byte array */
                String myInput = getDateFormatter().formatDate(getValue());
                return DataConverter.stringToByteArray(myInput);

                /* Catch Exceptions */
            } catch (JOceanusException e) {
                throw new JMetisDataException(ERROR_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted DateDay class.
     */
    public static final class EncryptedDateDay
            extends EncryptedField<JDateDay> {
        /**
         * Date Formatter.
         */
        private JDateDayFormatter theDateFormatter = null;

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedDateDay(final GordianKeySet pKeySet,
                                   final JDataFormatter pFormatter,
                                   final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedDateDay(final GordianKeySet pKeySet,
                                   final JDataFormatter pFormatter,
                                   final JDateDay pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        /**
         * Obtain the formatter.
         * @return the formatter
         */
        private JDateDayFormatter getDateFormatter() {
            if (theDateFormatter == null) {
                theDateFormatter = getFormatter().getDateFormatter();
            }
            return theDateFormatter;
        }

        @Override
        protected JDateDay parseBytes(final byte[] pBytes) throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                String myInput = DataConverter.byteArrayToString(pBytes);
                return getDateFormatter().parseDateDay(myInput);

                /* Catch Exceptions */
            } catch (JOceanusException | IllegalArgumentException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the date to a string and then a byte array */
                String myInput = getDateFormatter().formatDateDay(getValue());
                return DataConverter.stringToByteArray(myInput);

                /* Catch Exceptions */
            } catch (JOceanusException e) {
                throw new JMetisDataException(ERROR_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted CharArray class.
     */
    public static final class EncryptedCharArray
            extends EncryptedField<char[]> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedCharArray(final GordianKeySet pKeySet,
                                     final JDataFormatter pFormatter,
                                     final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedCharArray(final GordianKeySet pKeySet,
                                     final JDataFormatter pFormatter,
                                     final char[] pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected char[] parseBytes(final byte[] pBytes) throws JOceanusException {
            return DataConverter.bytesToCharArray(pBytes);
        }

        @Override
        protected byte[] getBytesForEncryption() throws JOceanusException {
            return DataConverter.charsToByteArray(getValue());
        }
    }

    /**
     * The encrypted BigInteger class.
     */
    public static final class EncryptedBigInteger
            extends EncryptedField<BigInteger> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedBigInteger(final GordianKeySet pKeySet,
                                      final JDataFormatter pFormatter,
                                      final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedBigInteger(final GordianKeySet pKeySet,
                                      final JDataFormatter pFormatter,
                                      final BigInteger pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected BigInteger parseBytes(final byte[] pBytes) throws JOceanusException {
            try {
                return new BigInteger(pBytes);
                /* Catch Exceptions */
            } catch (NumberFormatException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JOceanusException {
            return getValue().toByteArray();
        }
    }

    /**
     * The encrypted BigDecimal class.
     */
    public static final class EncryptedBigDecimal
            extends EncryptedField<BigDecimal> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedBigDecimal(final GordianKeySet pKeySet,
                                      final JDataFormatter pFormatter,
                                      final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedBigDecimal(final GordianKeySet pKeySet,
                                      final JDataFormatter pFormatter,
                                      final BigDecimal pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected BigDecimal parseBytes(final byte[] pBytes) throws JOceanusException {
            try {
                return new BigDecimal(DataConverter.byteArrayToString(pBytes));
                /* Catch Exceptions */
            } catch (JOceanusException | NumberFormatException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JOceanusException {
            return DataConverter.stringToByteArray(getValue().toString());
        }
    }

    /**
     * The encrypted Decimal class.
     * @param <X> the decimal type
     */
    public abstract static class EncryptedDecimal<X extends JDecimal>
            extends EncryptedField<X> {
        /**
         * Decimal Formatter.
         */
        private JDecimalFormatter theDecimalFormatter = null;

        /**
         * Decimal Parser.
         */
        private JDecimalParser theDecimalParser = null;

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        private EncryptedDecimal(final GordianKeySet pKeySet,
                                 final JDataFormatter pFormatter,
                                 final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        private EncryptedDecimal(final GordianKeySet pKeySet,
                                 final JDataFormatter pFormatter,
                                 final X pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        /**
         * Obtain the formatter.
         * @return the formatter
         */
        protected JDecimalFormatter getDecimalFormatter() {
            if (theDecimalFormatter == null) {
                theDecimalFormatter = getFormatter().getDecimalFormatter();
            }
            return theDecimalFormatter;
        }

        /**
         * Obtain the parser.
         * @return the parser
         */
        protected JDecimalParser getDecimalParser() {
            if (theDecimalParser == null) {
                theDecimalParser = getFormatter().getDecimalParser();
            }
            return theDecimalParser;
        }

        @Override
        protected X parseBytes(final byte[] pBytes) throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and parse it */
                String myInput = DataConverter.byteArrayToString(pBytes);
                return parseValue(myInput);

                /* Catch Exceptions */
            } catch (JOceanusException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }

        /**
         * Parse a string value to get a value.
         * @param pValue the string value
         * @return the value
         * @throws JOceanusException on error
         */
        protected abstract X parseValue(final String pValue) throws JOceanusException;

        @Override
        protected byte[] getBytesForEncryption() throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Format the value */
                String myValue = getValue().toString();

                /* Convert the string to a byte array */
                return DataConverter.stringToByteArray(myValue);

                /* Catch Exceptions */
            } catch (JOceanusException e) {
                throw new JMetisDataException(ERROR_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Money class.
     */
    public static final class EncryptedMoney
            extends EncryptedDecimal<JMoney> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedMoney(final GordianKeySet pKeySet,
                                 final JDataFormatter pFormatter,
                                 final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedMoney(final GordianKeySet pKeySet,
                                 final JDataFormatter pFormatter,
                                 final JMoney pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected byte[] getBytesForEncryption() throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Format the value */
                String myValue = getDecimalFormatter().toCurrencyString(getValue());

                /* Convert the string to a byte array */
                return DataConverter.stringToByteArray(myValue);

                /* Catch Exceptions */
            } catch (JOceanusException e) {
                throw new JMetisDataException(ERROR_VALUE_CONVERT, e);
            }
        }

        @Override
        protected JMoney parseValue(final String pValue) throws JOceanusException {
            try {
                return getDecimalParser().parseMoneyValue(pValue);
            } catch (IllegalArgumentException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Units class.
     */
    public static final class EncryptedUnits
            extends EncryptedDecimal<JUnits> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedUnits(final GordianKeySet pKeySet,
                                 final JDataFormatter pFormatter,
                                 final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedUnits(final GordianKeySet pKeySet,
                                 final JDataFormatter pFormatter,
                                 final JUnits pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected JUnits parseValue(final String pValue) throws JOceanusException {
            try {
                return new JUnits(pValue);
            } catch (IllegalArgumentException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Rate class.
     */
    public static final class EncryptedRate
            extends EncryptedDecimal<JRate> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedRate(final GordianKeySet pKeySet,
                                final JDataFormatter pFormatter,
                                final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedRate(final GordianKeySet pKeySet,
                                final JDataFormatter pFormatter,
                                final JRate pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected JRate parseValue(final String pValue) throws JOceanusException {
            try {
                return new JRate(pValue);
                /* Catch Exceptions */
            } catch (IllegalArgumentException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Price class.
     */
    public static final class EncryptedPrice
            extends EncryptedDecimal<JPrice> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedPrice(final GordianKeySet pKeySet,
                                 final JDataFormatter pFormatter,
                                 final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedPrice(final GordianKeySet pKeySet,
                                 final JDataFormatter pFormatter,
                                 final JPrice pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected byte[] getBytesForEncryption() throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Format the value */
                String myValue = getDecimalFormatter().toCurrencyString(getValue());

                /* Convert the string to a byte array */
                return DataConverter.stringToByteArray(myValue);

                /* Catch Exceptions */
            } catch (JOceanusException e) {
                throw new JMetisDataException(ERROR_VALUE_CONVERT, e);
            }
        }

        @Override
        protected JPrice parseValue(final String pValue) throws JOceanusException {
            try {
                return getDecimalParser().parsePriceValue(pValue);
            } catch (IllegalArgumentException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Dilution class.
     */
    public static final class EncryptedDilution
            extends EncryptedDecimal<JDilution> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedDilution(final GordianKeySet pKeySet,
                                    final JDataFormatter pFormatter,
                                    final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedDilution(final GordianKeySet pKeySet,
                                    final JDataFormatter pFormatter,
                                    final JDilution pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected JDilution parseValue(final String pValue) throws JOceanusException {
            try {
                return new JDilution(pValue);
            } catch (IllegalArgumentException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Ratio class.
     */
    public static final class EncryptedRatio
            extends EncryptedDecimal<JRatio> {
        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pEncrypted the encrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedRatio(final GordianKeySet pKeySet,
                                 final JDataFormatter pFormatter,
                                 final byte[] pEncrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pEncrypted);
        }

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pFormatter the data formatter
         * @param pUnencrypted the unencrypted value of the field
         * @throws JOceanusException on error
         */
        protected EncryptedRatio(final GordianKeySet pKeySet,
                                 final JDataFormatter pFormatter,
                                 final JRatio pUnencrypted) throws JOceanusException {
            super(pKeySet, pFormatter, pUnencrypted);
        }

        @Override
        protected JRatio parseValue(final String pValue) throws JOceanusException {
            try {
                return new JRatio(pValue);
            } catch (IllegalArgumentException e) {
                throw new JMetisDataException(ERROR_BYTES_CONVERT, e);
            }
        }
    }
}
