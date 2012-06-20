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
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import net.sourceforge.JDataManager.DataConverter;
import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataObject;
import net.sourceforge.JDataManager.JDataObject.JDataDiffers;
import net.sourceforge.JDataManager.JDataObject.JDataFormat;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Decimal;
import net.sourceforge.JDecimal.Dilution;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Price;
import net.sourceforge.JDecimal.Rate;
import net.sourceforge.JDecimal.Units;

/**
 * Encrypted data types.
 * @author Tony Washer
 */
public final class EncryptedData {
    /**
     * Encrypted data conversion failure message.
     */
    private static final String MSG_BYTES_CONVERT = "Failed to convert value from bytes";

    /**
     * Encrypted data conversion failure message.
     */
    private static final String MSG_VALUE_CONVERT = "Failed to convert value to bytes";

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
        return (pField == null) ? null : pField.getValue();
    }

    /**
     * Obtain encrypted bytes for a field (which may be null).
     * @param pField the field to obtain the encrypted bytes for
     * @return the value
     */
    public static byte[] getBytes(final EncryptedField<?> pField) {
        return (pField == null) ? null : pField.getBytes();
    }

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
     * The generic encrypted object class.
     * @param <T> the field type
     */
    public abstract static class EncryptedField<T> implements JDataFormat, JDataDiffers {
        /**
         * Encryption CipherSet.
         */
        private CipherSet theCipherSet = null;

        /**
         * Encrypted value.
         */
        private byte[] theEncrypted = null;

        /**
         * Decrypted value.
         */
        private T theDecrypted = null;

        /**
         * Constructor.
         * @param pCipherSet the cipher set
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        private EncryptedField(final CipherSet pCipherSet,
                               final byte[] pEncrypted) throws JDataException {
            /* Store the cipherSet */
            theCipherSet = pCipherSet;

            /* Store the encrypted value */
            theEncrypted = Arrays.copyOf(pEncrypted, pEncrypted.length);

            /* Reject if encryption is not initialised */
            if (theCipherSet == null) {
                throw new JDataException(ExceptionClass.LOGIC, "Encryption is not initialised");
            }

            /* Decrypt the Bytes */
            byte[] myBytes = theCipherSet.decryptBytes(theEncrypted);

            /* Set the decrypted value */
            theDecrypted = parseBytes(myBytes);
        }

        /**
         * Encrypt the value.
         * @throws JDataException on error
         */
        private void encryptValue() throws JDataException {
            /* Reject if encryption is not initialised */
            if (theCipherSet == null) {
                throw new JDataException(ExceptionClass.LOGIC, "Encryption is not initialised");
            }

            /* Obtain the bytes representation of the value */
            byte[] myBytes = getBytesForEncryption();

            /* Encrypt the Bytes */
            theEncrypted = theCipherSet.encryptBytes(myBytes);
        }

        /**
         * Constructor.
         * @param pCipherSet the CipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        private EncryptedField(final CipherSet pCipherSet,
                               final T pUnencrypted) throws JDataException {
            /* Store the control */
            theCipherSet = pCipherSet;

            /* Store the value */
            theDecrypted = pUnencrypted;

            /* Return if we have no encryption yet */
            if (theCipherSet == null) {
                return;
            }

            /* encrypt the value */
            encryptValue();
        }

        /**
         * Obtain the CipherSet.
         * @return the Cipher Set
         */
        public CipherSet getCipherSet() {
            return theCipherSet;
        }

        /**
         * Obtain the encrypted value.
         * @return the encrypted value
         */
        public byte[] getBytes() {
            return theEncrypted;
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
            return JDataObject.formatField(theDecrypted);
        }

        /**
         * Parse the decrypted bytes.
         * @param pBytes the decrypted bytes
         * @return the decrypted value
         * @throws JDataException on error
         */
        protected abstract T parseBytes(byte[] pBytes) throws JDataException;

        /**
         * Obtain the bytes format to encrypt.
         * @return the bytes to encrypt
         * @throws JDataException on error
         */
        protected abstract byte[] getBytesForEncryption() throws JDataException;

        /**
         * Apply fresh encryption to value.
         * @param pCipherSet the cipherSet
         * @throws JDataException on error
         */
        protected void applyEncryption(final CipherSet pCipherSet) throws JDataException {
            /* Store the CipherSet */
            theCipherSet = pCipherSet;

            /* Encrypt the value */
            encryptValue();
        }

        /**
         * Adopt Encryption.
         * @param pCipherSet the cipherSet
         * @param pField field to adopt encryption from
         * @throws JDataException on error
         */
        protected void adoptEncryption(final CipherSet pCipherSet,
                                       final EncryptedField<?> pField) throws JDataException {
            /* Store the CipherSet */
            theCipherSet = pCipherSet;

            /* If we need to renew the encryption */
            if ((pField == null)
                    || (Difference.getDifference(pCipherSet, pField.getCipherSet()).isDifferent())
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
            int myHashCode = SecurityGenerator.HASH_PRIME * getValue().hashCode();
            if (theEncrypted != null) {
                myHashCode += theEncrypted.hashCode();
            }
            return myHashCode;
        }

        @Override
        public Difference differs(final Object pThat) {
            /* Reject if null */
            if (pThat == null) {
                return Difference.Different;
            }

            /* Reject if wrong class */
            if (getClass() != pThat.getClass()) {
                return Difference.Different;
            }

            /* Access as correct class */
            EncryptedField<?> myField = (EncryptedField<?>) pThat;

            /* Compare Unencrypted value */
            if (Difference.getDifference(getValue(), myField.getValue()).isDifferent()) {
                return Difference.Different;
            }

            /* Compare Encrypted value */
            if (!Arrays.equals(getBytes(), myField.getBytes())) {
                return Difference.Security;
            }

            /* Item is the Same */
            return Difference.Identical;
        }
    }

    /**
     * The encrypted String class.
     */
    public static final class EncryptedString extends EncryptedField<String> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedString(final CipherSet pCipherSet,
                                  final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedString(final CipherSet pCipherSet,
                                  final String pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected String parseBytes(final byte[] pBytes) throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string */
                return DataConverter.byteArrayToString(pBytes);

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the string to a byte array */
                return DataConverter.stringToByteArray(getValue());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Short class.
     */
    public static final class EncryptedShort extends EncryptedField<Short> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedShort(final CipherSet pCipherSet,
                                 final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedShort(final CipherSet pCipherSet,
                                 final Short pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Short parseBytes(final byte[] pBytes) throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a short */
                return Short.parseShort(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the short to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Integer class.
     */
    public static final class EncryptedInteger extends EncryptedField<Integer> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedInteger(final CipherSet pCipherSet,
                                   final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedInteger(final CipherSet pCipherSet,
                                   final Integer pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Integer parseBytes(final byte[] pBytes) throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                return Integer.parseInt(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the integer to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Long class.
     */
    public static final class EncryptedLong extends EncryptedField<Long> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedLong(final CipherSet pCipherSet,
                                final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedLong(final CipherSet pCipherSet,
                                final Long pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Long parseBytes(final byte[] pBytes) throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a long */
                return Long.parseLong(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the long to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Float class.
     */
    public static final class EncryptedFloat extends EncryptedField<Float> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedFloat(final CipherSet pCipherSet,
                                 final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedFloat(final CipherSet pCipherSet,
                                 final Float pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Float parseBytes(final byte[] pBytes) throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a float */
                return Float.parseFloat(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the float to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Double class.
     */
    public static final class EncryptedDouble extends EncryptedField<Double> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedDouble(final CipherSet pCipherSet,
                                  final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedDouble(final CipherSet pCipherSet,
                                  final Double pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Double parseBytes(final byte[] pBytes) throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a Double */
                return Double.parseDouble(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the double to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Boolean class.
     */
    public static final class EncryptedBoolean extends EncryptedField<Boolean> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedBoolean(final CipherSet pCipherSet,
                                   final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedBoolean(final CipherSet pCipherSet,
                                   final Boolean pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Boolean parseBytes(final byte[] pBytes) throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                return Boolean.parseBoolean(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the boolean to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Date class.
     */
    public static final class EncryptedDate extends EncryptedField<Date> {
        /**
         * The Date format.
         */
        private static final DateFormat FORMAT_US = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                                                                                   DateFormat.SHORT,
                                                                                   Locale.US);

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedDate(final CipherSet pCipherSet,
                                final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedDate(final CipherSet pCipherSet,
                                final Date pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Date parseBytes(final byte[] pBytes) throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                return FORMAT_US.parse(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the date to a string and then a byte array */
                return DataConverter.stringToByteArray(FORMAT_US.format(getValue()));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted DateDay class.
     */
    public static final class EncryptedDateDay extends EncryptedField<DateDay> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedDateDay(final CipherSet pCipherSet,
                                   final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedDateDay(final CipherSet pCipherSet,
                                   final DateDay pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected DateDay parseBytes(final byte[] pBytes) throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                return new DateDay(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_BYTES_CONVERT, e);
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the date to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted CharArray class.
     */
    public static final class EncryptedCharArray extends EncryptedField<char[]> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedCharArray(final CipherSet pCipherSet,
                                     final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedCharArray(final CipherSet pCipherSet,
                                     final char[] pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected char[] parseBytes(final byte[] pBytes) throws JDataException {
            return DataConverter.bytesToCharArray(pBytes);
        }

        @Override
        protected byte[] getBytesForEncryption() throws JDataException {
            return DataConverter.charsToByteArray(getValue());
        }
    }

    /**
     * The encrypted BigInteger class.
     */
    public static final class EncryptedBigInteger extends EncryptedField<BigInteger> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedBigInteger(final CipherSet pCipherSet,
                                      final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedBigInteger(final CipherSet pCipherSet,
                                      final BigInteger pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected BigInteger parseBytes(final byte[] pBytes) throws JDataException {
            return new BigInteger(pBytes);
        }

        @Override
        protected byte[] getBytesForEncryption() throws JDataException {
            return getValue().toByteArray();
        }
    }

    /**
     * The encrypted BigDecimal class.
     */
    public static final class EncryptedBigDecimal extends EncryptedField<BigDecimal> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedBigDecimal(final CipherSet pCipherSet,
                                      final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedBigDecimal(final CipherSet pCipherSet,
                                      final BigDecimal pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected BigDecimal parseBytes(final byte[] pBytes) throws JDataException {
            return new BigDecimal(DataConverter.byteArrayToString(pBytes));
        }

        @Override
        protected byte[] getBytesForEncryption() throws JDataException {
            return DataConverter.stringToByteArray(getValue().toString());
        }
    }

    /**
     * The encrypted Decimal class.
     * @param <X> the decimal type
     */
    public abstract static class EncryptedDecimal<X extends Decimal> extends EncryptedField<X> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        private EncryptedDecimal(final CipherSet pCipherSet,
                                 final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        private EncryptedDecimal(final CipherSet pCipherSet,
                                 final X pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected X parseBytes(final byte[] pBytes) throws JDataException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and parse it */
                return parseValue(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_BYTES_CONVERT, e);
            }
        }

        /**
         * Parse a string value to get a value.
         * @param pValue the string value
         * @return the value
         * @throws JDataException on error
         */
        protected abstract X parseValue(final String pValue) throws JDataException;

        @Override
        protected byte[] getBytesForEncryption() throws JDataException {
            /* Protect against exceptions */
            try {
                /* Format the value */
                String myValue = getValue().format(false);

                /* Convert the string to a byte array */
                return DataConverter.stringToByteArray(myValue);

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new JDataException(ExceptionClass.CRYPTO, MSG_VALUE_CONVERT, e);
            }
        }
    }

    /**
     * The encrypted Money class.
     */
    public static final class EncryptedMoney extends EncryptedDecimal<Money> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedMoney(final CipherSet pCipherSet,
                                 final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedMoney(final CipherSet pCipherSet,
                                 final Money pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Money parseValue(final String pValue) throws JDataException {
            return new Money(pValue);
        }
    }

    /**
     * The encrypted Units class.
     */
    public static final class EncryptedUnits extends EncryptedDecimal<Units> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedUnits(final CipherSet pCipherSet,
                                 final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedUnits(final CipherSet pCipherSet,
                                 final Units pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Units parseValue(final String pValue) throws JDataException {
            return new Units(pValue);
        }
    }

    /**
     * The encrypted Rate class.
     */
    public static final class EncryptedRate extends EncryptedDecimal<Rate> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedRate(final CipherSet pCipherSet,
                                final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedRate(final CipherSet pCipherSet,
                                final Rate pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Rate parseValue(final String pValue) throws JDataException {
            return new Rate(pValue);
        }
    }

    /**
     * The encrypted Price class.
     */
    public static final class EncryptedPrice extends EncryptedDecimal<Price> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedPrice(final CipherSet pCipherSet,
                                 final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedPrice(final CipherSet pCipherSet,
                                 final Price pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Price parseValue(final String pValue) throws JDataException {
            return new Price(pValue);
        }
    }

    /**
     * The encrypted Dilution class.
     */
    public static final class EncryptedDilution extends EncryptedDecimal<Dilution> {
        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedDilution(final CipherSet pCipherSet,
                                    final byte[] pEncrypted) throws JDataException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws JDataException on error
         */
        protected EncryptedDilution(final CipherSet pCipherSet,
                                    final Dilution pUnencrypted) throws JDataException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Dilution parseValue(final String pValue) throws JDataException {
            return new Dilution(pValue);
        }
    }
}
