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
import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
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
     * Field Generator Helper class.
     */
    public static class EncryptionGenerator {
        /**
         * The CipherSet to use for generation.
         */
        private final CipherSet theCipherSet;

        /**
         * Constructor.
         * @param pCipherSet the CipherSet
         */
        public EncryptionGenerator(final CipherSet pCipherSet) {
            /* Store Parameter */
            theCipherSet = pCipherSet;
        }

        /**
         * Set Encrypted value.
         * @param pCurrent the current encrypted value
         * @param pValue the new value to encrypt
         * @return the encrypted field
         * @throws ModelException on error
         */
        public EncryptedField<?> encryptValue(final EncryptedField<?> pCurrent,
                                              final Object pValue) throws ModelException {
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
            if ((myCurrent != null) && (Difference.isEqual(myCurrent.getValue(), pValue))) {
                return pCurrent;
            }

            /* We need a new Field so handle each case individually */
            if (String.class.isInstance(pValue)) {
                return new EncryptedString(theCipherSet, (String) pValue);
            }
            if (Short.class.isInstance(pValue)) {
                return new EncryptedShort(theCipherSet, (Short) pValue);
            }
            if (Integer.class.isInstance(pValue)) {
                return new EncryptedInteger(theCipherSet, (Integer) pValue);
            }
            if (Long.class.isInstance(pValue)) {
                return new EncryptedLong(theCipherSet, (Long) pValue);
            }
            if (Boolean.class.isInstance(pValue)) {
                return new EncryptedBoolean(theCipherSet, (Boolean) pValue);
            }
            if (Date.class.isInstance(pValue)) {
                return new EncryptedDate(theCipherSet, (Date) pValue);
            }
            if (char[].class.isInstance(pValue)) {
                return new EncryptedCharArray(theCipherSet, (char[]) pValue);
            }
            if (Float.class.isInstance(pValue)) {
                return new EncryptedFloat(theCipherSet, (Float) pValue);
            }
            if (Double.class.isInstance(pValue)) {
                return new EncryptedDouble(theCipherSet, (Double) pValue);
            }

            /* Handle big integer classes */
            if (BigInteger.class.isInstance(pValue)) {
                return new EncryptedBigInteger(theCipherSet, (BigInteger) pValue);
            }
            if (BigDecimal.class.isInstance(pValue)) {
                return new EncryptedBigDecimal(theCipherSet, (BigDecimal) pValue);
            }

            /* Handle decimal instances */
            if (DateDay.class.isInstance(pValue)) {
                return new EncryptedDateDay(theCipherSet, (DateDay) pValue);
            }
            if (Money.class.isInstance(pValue)) {
                return new EncryptedMoney(theCipherSet, (Money) pValue);
            }
            if (Units.class.isInstance(pValue)) {
                return new EncryptedUnits(theCipherSet, (Units) pValue);
            }
            if (Rate.class.isInstance(pValue)) {
                return new EncryptedRate(theCipherSet, (Rate) pValue);
            }
            if (Price.class.isInstance(pValue)) {
                return new EncryptedPrice(theCipherSet, (Price) pValue);
            }
            if (Dilution.class.isInstance(pValue)) {
                return new EncryptedDilution(theCipherSet, (Dilution) pValue);
            }

            /* Unsupported so reject */
            throw new ModelException(ExceptionClass.LOGIC, "Invalid Object Class for Encryption"
                    + pValue.getClass().getCanonicalName());
        }

        /**
         * decrypt value.
         * @param pEncrypted the encrypted value
         * @param pClass the class of the encrypted value
         * @return the encrypted field
         * @throws ModelException on error
         */
        public EncryptedField<?> decryptValue(final byte[] pEncrypted,
                                              final Class<?> pClass) throws ModelException {
            /* If we are passed a null value just return null */
            if (pEncrypted == null) {
                return null;
            }

            /* We need a new Field so handle each case individually */
            if (String.class == pClass) {
                return new EncryptedString(theCipherSet, pEncrypted);
            }
            if (Short.class == pClass) {
                return new EncryptedShort(theCipherSet, pEncrypted);
            }
            if (Integer.class == pClass) {
                return new EncryptedInteger(theCipherSet, pEncrypted);
            }
            if (Long.class == pClass) {
                return new EncryptedLong(theCipherSet, pEncrypted);
            }
            if (Boolean.class == pClass) {
                return new EncryptedBoolean(theCipherSet, pEncrypted);
            }
            if (Date.class == pClass) {
                return new EncryptedDate(theCipherSet, pEncrypted);
            }
            if (char[].class == pClass) {
                return new EncryptedCharArray(theCipherSet, pEncrypted);
            }
            if (Float.class == pClass) {
                return new EncryptedFloat(theCipherSet, pEncrypted);
            }
            if (Double.class == pClass) {
                return new EncryptedDouble(theCipherSet, pEncrypted);
            }

            /* Handle BigInteger classes */
            if (BigInteger.class == pClass) {
                return new EncryptedBigInteger(theCipherSet, pEncrypted);
            }
            if (BigDecimal.class == pClass) {
                return new EncryptedBigDecimal(theCipherSet, pEncrypted);
            }

            /* Handle decimal instances */
            if (DateDay.class == pClass) {
                return new EncryptedDateDay(theCipherSet, pEncrypted);
            }
            if (Money.class == pClass) {
                return new EncryptedMoney(theCipherSet, pEncrypted);
            }
            if (Units.class == pClass) {
                return new EncryptedUnits(theCipherSet, pEncrypted);
            }
            if (Rate.class == pClass) {
                return new EncryptedRate(theCipherSet, pEncrypted);
            }
            if (Price.class == pClass) {
                return new EncryptedPrice(theCipherSet, pEncrypted);
            }
            if (Dilution.class == pClass) {
                return new EncryptedDilution(theCipherSet, pEncrypted);
            }

            /* Unsupported so reject */
            throw new ModelException(ExceptionClass.LOGIC, "Invalid Object Class for Encryption"
                    + pClass.getCanonicalName());
        }

        /**
         * Adopt Encryption.
         * @param pTarget the target field
         * @param pSource the source field
         * @throws ModelException on error
         */
        public void adoptEncryption(final EncryptedField<?> pTarget,
                                    final EncryptedField<?> pSource) throws ModelException {
            /* Adopt the encryption */
            pTarget.adoptEncryption(theCipherSet, pSource);
        }
    }

    /**
     * The generic encrypted object class.
     * @param <T> the field type
     */
    public abstract static class EncryptedField<T> {
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
         * @throws ModelException on error
         */
        private EncryptedField(final CipherSet pCipherSet,
                               final byte[] pEncrypted) throws ModelException {
            /* Store the cipherSet */
            theCipherSet = pCipherSet;

            /* Store the encrypted value */
            theEncrypted = pEncrypted;

            /* Reject if encryption is not initialised */
            if (theCipherSet == null) {
                throw new ModelException(ExceptionClass.LOGIC, "Encryption is not initialised");
            }

            /* Decrypt the Bytes */
            byte[] myBytes = theCipherSet.decryptBytes(theEncrypted);

            /* Set the decrypted value */
            theDecrypted = parseBytes(myBytes);
        }

        /**
         * Encrypt the value.
         * @throws ModelException on error
         */
        private void encryptValue() throws ModelException {
            /* Reject if encryption is not initialised */
            if (theCipherSet == null) {
                throw new ModelException(ExceptionClass.LOGIC, "Encryption is not initialised");
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
         * @throws ModelException on error
         */
        private EncryptedField(final CipherSet pCipherSet,
                               final T pUnencrypted) throws ModelException {
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

        /**
         * Parse the decrypted bytes.
         * @param pBytes the decrypted bytes
         * @return the decrypted value
         * @throws ModelException on error
         */
        protected abstract T parseBytes(byte[] pBytes) throws ModelException;

        /**
         * Obtain the bytes format to encrypt.
         * @return the bytes to encrypt
         * @throws ModelException on error
         */
        protected abstract byte[] getBytesForEncryption() throws ModelException;

        /**
         * Apply fresh encryption to value.
         * @param pCipherSet the cipherSet
         * @throws ModelException on error
         */
        protected void applyEncryption(final CipherSet pCipherSet) throws ModelException {
            /* Store the CipherSet */
            theCipherSet = pCipherSet;

            /* Encrypt the value */
            encryptValue();
        }

        /**
         * Adopt Encryption.
         * @param pCipherSet the cipherSet
         * @param pField field to adopt encryption from
         * @throws ModelException on error
         */
        private void adoptEncryption(final CipherSet pCipherSet,
                                     final EncryptedField<?> pField) throws ModelException {
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

        /**
         * Compare two EncryptedFields for differences.
         * @param pNew the other field
         * @return the difference
         */
        public Difference getDifference(final EncryptedField<?> pNew) {
            /* Reject if null */
            if (pNew == null) {
                return Difference.Different;
            }

            /* Reject if wrong class */
            if (this.getClass() != pNew.getClass()) {
                return Difference.Different;
            }

            /* Access as correct class */
            EncryptedField<?> myField = (EncryptedField<?>) pNew;

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
         * @throws ModelException on error
         */
        private EncryptedString(final CipherSet pCipherSet,
                                final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedString(final CipherSet pCipherSet,
                                final String pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected String parseBytes(final byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string */
                return DataConverter.byteArrayToString(pBytes);

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the string to a byte array */
                return DataConverter.stringToByteArray(getValue());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
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
         * @throws ModelException on error
         */
        private EncryptedShort(final CipherSet pCipherSet,
                               final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedShort(final CipherSet pCipherSet,
                               final Short pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Short parseBytes(final byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a short */
                return Short.parseShort(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the short to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
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
         * @throws ModelException on error
         */
        private EncryptedInteger(final CipherSet pCipherSet,
                                 final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedInteger(final CipherSet pCipherSet,
                                 final Integer pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Integer parseBytes(final byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                return Integer.parseInt(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the integer to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
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
         * @throws ModelException on error
         */
        private EncryptedLong(final CipherSet pCipherSet,
                              final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedLong(final CipherSet pCipherSet,
                              final Long pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Long parseBytes(final byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a long */
                return Long.parseLong(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the long to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
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
         * @throws ModelException on error
         */
        private EncryptedFloat(final CipherSet pCipherSet,
                               final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedFloat(final CipherSet pCipherSet,
                               final Float pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Float parseBytes(final byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a float */
                return Float.parseFloat(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the float to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
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
         * @throws ModelException on error
         */
        private EncryptedDouble(final CipherSet pCipherSet,
                                final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedDouble(final CipherSet pCipherSet,
                                final Double pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Double parseBytes(final byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a Double */
                return Double.parseDouble(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the double to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
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
         * @throws ModelException on error
         */
        private EncryptedBoolean(final CipherSet pCipherSet,
                                 final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedBoolean(final CipherSet pCipherSet,
                                 final Boolean pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Boolean parseBytes(final byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                return Boolean.parseBoolean(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the boolean to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
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
         * @throws ModelException on error
         */
        private EncryptedDate(final CipherSet pCipherSet,
                              final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedDate(final CipherSet pCipherSet,
                              final Date pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Date parseBytes(final byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                return FORMAT_US.parse(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the date to a string and then a byte array */
                return DataConverter.stringToByteArray(FORMAT_US.format(getValue()));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
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
         * @throws ModelException on error
         */
        private EncryptedDateDay(final CipherSet pCipherSet,
                                 final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedDateDay(final CipherSet pCipherSet,
                                 final DateDay pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected DateDay parseBytes(final byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                return new DateDay(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the date to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
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
         * @throws ModelException on error
         */
        private EncryptedCharArray(final CipherSet pCipherSet,
                                   final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedCharArray(final CipherSet pCipherSet,
                                   final char[] pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected char[] parseBytes(final byte[] pBytes) throws ModelException {
            return DataConverter.bytesToCharArray(pBytes);
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
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
         * @throws ModelException on error
         */
        private EncryptedBigInteger(final CipherSet pCipherSet,
                                    final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedBigInteger(final CipherSet pCipherSet,
                                    final BigInteger pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected BigInteger parseBytes(final byte[] pBytes) throws ModelException {
            return new BigInteger(pBytes);
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
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
         * @throws ModelException on error
         */
        private EncryptedBigDecimal(final CipherSet pCipherSet,
                                    final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedBigDecimal(final CipherSet pCipherSet,
                                    final BigDecimal pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected BigDecimal parseBytes(final byte[] pBytes) throws ModelException {
            return new BigDecimal(DataConverter.byteArrayToString(pBytes));
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
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
         * @throws ModelException on error
         */
        private EncryptedDecimal(final CipherSet pCipherSet,
                                 final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedDecimal(final CipherSet pCipherSet,
                                 final X pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected X parseBytes(final byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and parse it */
                return parseValue(DataConverter.byteArrayToString(pBytes));

                /* Catch Exceptions */
            } catch (ModelException e) {
                throw e;
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        /**
         * Parse a string value to get a value.
         * @param pValue the string value
         * @return the value
         * @throws ModelException on error
         */
        protected abstract X parseValue(final String pValue) throws ModelException;

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Format the value */
                String myValue = getValue().format(false);

                /* Convert the string to a byte array */
                return DataConverter.stringToByteArray(myValue);

                /* Catch Exceptions */
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
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
         * @throws ModelException on error
         */
        private EncryptedMoney(final CipherSet pCipherSet,
                               final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedMoney(final CipherSet pCipherSet,
                               final Money pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Money parseValue(final String pValue) throws ModelException {
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
         * @throws ModelException on error
         */
        private EncryptedUnits(final CipherSet pCipherSet,
                               final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedUnits(final CipherSet pCipherSet,
                               final Units pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Units parseValue(final String pValue) throws ModelException {
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
         * @throws ModelException on error
         */
        private EncryptedRate(final CipherSet pCipherSet,
                              final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedRate(final CipherSet pCipherSet,
                              final Rate pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Rate parseValue(final String pValue) throws ModelException {
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
         * @throws ModelException on error
         */
        private EncryptedPrice(final CipherSet pCipherSet,
                               final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedPrice(final CipherSet pCipherSet,
                               final Price pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Price parseValue(final String pValue) throws ModelException {
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
         * @throws ModelException on error
         */
        private EncryptedDilution(final CipherSet pCipherSet,
                                  final byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor.
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException on error
         */
        private EncryptedDilution(final CipherSet pCipherSet,
                                  final Dilution pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Dilution parseValue(final String pValue) throws ModelException {
            return new Dilution(pValue);
        }
    }
}
