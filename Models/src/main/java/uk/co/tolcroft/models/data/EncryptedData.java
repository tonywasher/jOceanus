/*******************************************************************************
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
package uk.co.tolcroft.models.data;

import java.util.Arrays;

import net.sourceforge.JDataManager.DataConverter;
import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Decimal;
import net.sourceforge.JDecimal.Decimal.Dilution;
import net.sourceforge.JDecimal.Decimal.Money;
import net.sourceforge.JDecimal.Decimal.Price;
import net.sourceforge.JDecimal.Decimal.Rate;
import net.sourceforge.JDecimal.Decimal.Units;
import net.sourceforge.JGordianKnot.CipherSet;

public class EncryptedData {
    /**
     * Obtain value for a field (which may be null)
     * @param <T> the field type
     * @param pField the field to obtain the value for
     * @return the value
     */
    public static <T> T getValue(EncryptedField<T> pField) {
        return (pField == null) ? null : pField.getValue();
    }

    /**
     * Obtain encrypted bytes for a field (which may be null)
     * @param pField the field to obtain the encrypted bytes for
     * @return the value
     */
    public static byte[] getBytes(EncryptedField<?> pField) {
        return (pField == null) ? null : pField.getBytes();
    }

    /**
     * Field Generator Helper class
     */
    public static class EncryptionGenerator {
        /**
         * The CipherSet to use for generation
         */
        private final CipherSet theCipherSet;

        /**
         * Constructor
         * @param pCipherSet the CipherSet
         */
        public EncryptionGenerator(CipherSet pCipherSet) {
            /* Store Parameter */
            theCipherSet = pCipherSet;
        }

        /**
         * Set Encrypted value
         * @param pCurrent the current encrypted value
         * @param pValue the new value to encrypt
         * @return the encrypted field
         * @throws ModelException
         */
        public EncryptedField<?> encryptValue(EncryptedField<?> pCurrent,
                                              Object pValue) throws ModelException {
            /* If we are passed a null value just return null */
            if (pValue == null)
                return null;

            /* Access current value */
            EncryptedField<?> myCurrent = pCurrent;

            /* If there is a current value */
            if (myCurrent != null) {
                /* If we have no cipher or else a different cipher, ignore the current value */
                if ((theCipherSet == null) || (!theCipherSet.equals(myCurrent.getCipherSet())))
                    myCurrent = null;
            }

            /* If there is a current value */
            if (myCurrent != null) {
                /* If the value is not changed return the current value */
                if (Difference.getDifference(myCurrent.getValue(), pValue).isIdentical())
                    return pCurrent;

                /* If the value is a different class ignore the current value */
                if (pValue.getClass() != myCurrent.getValue().getClass())
                    myCurrent = null;
            }

            /* We need a new Field so handle each case individually */
            if (String.class.isInstance(pValue))
                return new EncryptedString(theCipherSet, (String) pValue);
            if (Short.class.isInstance(pValue))
                return new EncryptedShort(theCipherSet, (Short) pValue);
            if (Integer.class.isInstance(pValue))
                return new EncryptedInteger(theCipherSet, (Integer) pValue);
            if (Long.class.isInstance(pValue))
                return new EncryptedLong(theCipherSet, (Long) pValue);
            if (Boolean.class.isInstance(pValue))
                return new EncryptedBoolean(theCipherSet, (Boolean) pValue);
            if (DateDay.class.isInstance(pValue))
                return new EncryptedDate(theCipherSet, (DateDay) pValue);
            if (char[].class.isInstance(pValue))
                return new EncryptedCharArray(theCipherSet, (char[]) pValue);

            /* Handle decimal instances */
            if (Money.class.isInstance(pValue))
                return new EncryptedMoney(theCipherSet, (Money) pValue);
            if (Units.class.isInstance(pValue))
                return new EncryptedUnits(theCipherSet, (Units) pValue);
            if (Rate.class.isInstance(pValue))
                return new EncryptedRate(theCipherSet, (Rate) pValue);
            if (Price.class.isInstance(pValue))
                return new EncryptedPrice(theCipherSet, (Price) pValue);
            if (Dilution.class.isInstance(pValue))
                return new EncryptedDilution(theCipherSet, (Dilution) pValue);

            /* Unsupported so reject */
            throw new ModelException(ExceptionClass.LOGIC, "Invalid Object Class for Encryption"
                    + pValue.getClass().getCanonicalName());
        }

        /**
         * decrypt value
         * @param pEncrypted the encrypted value
         * @param pClass the class of the encrypted value
         * @return the encrypted field
         * @throws ModelException
         */
        public EncryptedField<?> decryptValue(byte[] pEncrypted,
                                              Class<?> pClass) throws ModelException {
            /* If we are passed a null value just return null */
            if (pEncrypted == null)
                return null;

            /* We need a new Field so handle each case individually */
            if (String.class == pClass)
                return new EncryptedString(theCipherSet, pEncrypted);
            if (Short.class == pClass)
                return new EncryptedShort(theCipherSet, pEncrypted);
            if (Integer.class == pClass)
                return new EncryptedInteger(theCipherSet, pEncrypted);
            if (Long.class == pClass)
                return new EncryptedLong(theCipherSet, pEncrypted);
            if (Boolean.class == pClass)
                return new EncryptedBoolean(theCipherSet, pEncrypted);
            if (DateDay.class == pClass)
                return new EncryptedDate(theCipherSet, pEncrypted);
            if (char[].class == pClass)
                return new EncryptedCharArray(theCipherSet, pEncrypted);

            /* Handle decimal instances */
            if (Money.class == pClass)
                return new EncryptedMoney(theCipherSet, pEncrypted);
            if (Units.class == pClass)
                return new EncryptedUnits(theCipherSet, pEncrypted);
            if (Rate.class == pClass)
                return new EncryptedRate(theCipherSet, pEncrypted);
            if (Price.class == pClass)
                return new EncryptedPrice(theCipherSet, pEncrypted);
            if (Dilution.class == pClass)
                return new EncryptedDilution(theCipherSet, pEncrypted);

            /* Unsupported so reject */
            throw new ModelException(ExceptionClass.LOGIC, "Invalid Object Class for Encryption"
                    + pClass.getCanonicalName());
        }

        /**
         * Adopt Encryption
         * @param pTarget the target field
         * @param pSource the source field
         * @throws ModelException
         */
        public void adoptEncryption(EncryptedField<?> pTarget,
                                    EncryptedField<?> pSource) throws ModelException {
            /* Adopt the encryption */
            pTarget.adoptEncryption(theCipherSet, pSource);
        }
    }

    /**
     * The generic encrypted object class
     * @param <T> the field type
     */
    public static abstract class EncryptedField<T> {
        /**
         * Encryption CipherSet
         */
        private CipherSet theCipherSet = null;

        /**
         * Encrypted value
         */
        private byte[] theEncrypted = null;

        /**
         * Decrypted value
         */
        private T theDecrypted = null;

        /**
         * Constructor
         * @param pCipherSet the cipher set
         * @param pEncrypted the encrypted value of the field
         * @throws ModelException
         */
        private EncryptedField(CipherSet pCipherSet,
                               byte[] pEncrypted) throws ModelException {
            /* Store the cipherSet */
            theCipherSet = pCipherSet;

            /* Store the encrypted value */
            theEncrypted = pEncrypted;

            /* Reject if encryption is not initialised */
            if (theCipherSet == null)
                throw new ModelException(ExceptionClass.LOGIC, "Encryption is not initialised");

            /* Decrypt the Bytes */
            byte[] myBytes = theCipherSet.decryptBytes(theEncrypted);

            /* Set the decrypted value */
            theDecrypted = parseBytes(myBytes);
        }

        /**
         * Encrypt the value
         * @throws ModelException
         */
        private void encryptValue() throws ModelException {
            /* Reject if encryption is not initialized */
            if (theCipherSet == null)
                throw new ModelException(ExceptionClass.LOGIC, "Encryption is not initialised");

            /* Obtain the bytes representation of the value */
            byte[] myBytes = getBytesForEncryption();

            /* Encrypt the Bytes */
            theEncrypted = theCipherSet.encryptBytes(myBytes);
        }

        /**
         * Constructor
         * @param pCipherSet the CipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException
         */
        private EncryptedField(CipherSet pCipherSet,
                               T pUnencrypted) throws ModelException {
            /* Store the control */
            theCipherSet = pCipherSet;

            /* Store the value */
            theDecrypted = pUnencrypted;

            /* Return if we have no encryption yet */
            if (theCipherSet == null)
                return;

            /* encrypt the value */
            encryptValue();
        }

        /**
         * Obtain the CipherSet
         * @return the Cipher Set
         */
        public CipherSet getCipherSet() {
            return theCipherSet;
        }

        /**
         * Obtain the encrypted value
         * @return the encrypted value
         */
        public byte[] getBytes() {
            return theEncrypted;
        }

        /**
         * Obtain the unencrypted value
         * @return the unencrypted value
         */
        public T getValue() {
            return theDecrypted;
        }

        /**
         * Parse the decrypted bytes
         * @param pBytes the decrypted bytes
         * @return the decrypted value
         * @throws ModelException
         */
        protected abstract T parseBytes(byte[] pBytes) throws ModelException;

        /**
         * Obtain the bytes format to encrypt
         * @return the bytes to encrypt
         * @throws ModelException
         */
        protected abstract byte[] getBytesForEncryption() throws ModelException;

        /**
         * Apply fresh encryption to value
         * @param pCipherSet the cipherSet
         * @throws ModelException
         */
        protected void applyEncryption(CipherSet pCipherSet) throws ModelException {
            /* Store the CipherSet */
            theCipherSet = pCipherSet;

            /* Encrypt the value */
            encryptValue();
        }

        /**
         * Adopt Encryption
         * @param pCipherSet the cipherSet
         * @param pField field to adopt encryption from
         * @throws ModelException
         */
        private void adoptEncryption(CipherSet pCipherSet,
                                     EncryptedField<?> pField) throws ModelException {
            /* Store the CipherSet */
            theCipherSet = pCipherSet;

            /* If we need to renew the encryption */
            if ((pField == null)
                    || (Difference.getDifference(pCipherSet, pField.getCipherSet()).isDifferent())
                    || (Difference.getDifference(getValue(), pField.getValue()).isDifferent())) {
                /* encrypt the value */
                encryptValue();
            }

            /* else we can simply adopt the underlying encryption */
            else {
                /* Pick up the underlying encryption */
                theEncrypted = pField.getBytes();
            }
        }

        @Override
        public boolean equals(Object pThat) {
            /* Handle the trivial cases */
            if (this == pThat)
                return true;
            if (pThat == null)
                return false;

            /* Make sure that the object is the same class */
            if (pThat.getClass() != this.getClass())
                return false;

            /* Access the target field */
            EncryptedField<?> myThat = (EncryptedField<?>) pThat;

            /* Check differences */
            if (Difference.getDifference(getValue(), myThat.getValue()).isDifferent())
                return false;

            /* Check encryption */
            return !Arrays.equals(getBytes(), myThat.getBytes());
        }

        @Override
        public int hashCode() {
            /* Calculate hash allowing for field that has not been encrypted yet */
            int myHashCode = 17 * getValue().hashCode();
            if (theEncrypted != null)
                myHashCode += theEncrypted.hashCode();
            return myHashCode;
        }

        /**
         * Compare two EncryptedFields for differences
         * @param pNew the other field
         * @return the difference
         */
        public Difference getDifference(EncryptedField<?> pNew) {
            /* Reject if null */
            if (pNew == null)
                return Difference.Different;

            /* Reject if wrong class */
            if (this.getClass() != pNew.getClass())
                return Difference.Different;

            /* Access as correct class */
            EncryptedField<?> myField = (EncryptedField<?>) pNew;

            /* Compare Unencrypted value */
            if (Difference.getDifference(getValue(), myField.getValue()).isDifferent())
                return Difference.Different;

            /* Compare Encrypted value */
            if (!Arrays.equals(getBytes(), myField.getBytes()))
                return Difference.Security;

            /* Item is the Same */
            return Difference.Identical;
        }
    }

    /**
     * The encrypted String class
     */
    public static class EncryptedString extends EncryptedField<String> {
        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws ModelException
         */
        private EncryptedString(CipherSet pCipherSet,
                                byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException
         */
        private EncryptedString(CipherSet pCipherSet,
                                String pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected String parseBytes(byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string */
                return DataConverter.byteArrayToString(pBytes);
            }

            /* Catch Exceptions */
            catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the string to a byte array */
                return DataConverter.stringToByteArray(getValue());
            }

            /* Catch Exceptions */
            catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
            }
        }
    }

    /**
     * The encrypted Short class
     */
    public static class EncryptedShort extends EncryptedField<Short> {
        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws ModelException
         */
        private EncryptedShort(CipherSet pCipherSet,
                               byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException
         */
        private EncryptedShort(CipherSet pCipherSet,
                               Short pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Short parseBytes(byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a short */
                return Short.parseShort(DataConverter.byteArrayToString(pBytes));
            }

            /* Catch Exceptions */
            catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the short to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());
            }

            /* Catch Exceptions */
            catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
            }
        }
    }

    /**
     * The encrypted Integer class
     */
    public static class EncryptedInteger extends EncryptedField<Integer> {
        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws ModelException
         */
        private EncryptedInteger(CipherSet pCipherSet,
                                 byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException
         */
        private EncryptedInteger(CipherSet pCipherSet,
                                 Integer pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Integer parseBytes(byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                return Integer.parseInt(DataConverter.byteArrayToString(pBytes));
            }

            /* Catch Exceptions */
            catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the integer to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());
            }

            /* Catch Exceptions */
            catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
            }
        }
    }

    /**
     * The encrypted Long class
     */
    public static class EncryptedLong extends EncryptedField<Long> {
        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws ModelException
         */
        private EncryptedLong(CipherSet pCipherSet,
                              byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException
         */
        private EncryptedLong(CipherSet pCipherSet,
                              Long pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Long parseBytes(byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then a long */
                return Long.parseLong(DataConverter.byteArrayToString(pBytes));
            }

            /* Catch Exceptions */
            catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the long to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());
            }

            /* Catch Exceptions */
            catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
            }
        }
    }

    /**
     * The encrypted Boolean class
     */
    public static class EncryptedBoolean extends EncryptedField<Boolean> {
        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws ModelException
         */
        private EncryptedBoolean(CipherSet pCipherSet,
                                 byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException
         */
        private EncryptedBoolean(CipherSet pCipherSet,
                                 Boolean pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Boolean parseBytes(byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                return Boolean.parseBoolean(DataConverter.byteArrayToString(pBytes));
            }

            /* Catch Exceptions */
            catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the boolean to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());
            }

            /* Catch Exceptions */
            catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
            }
        }
    }

    /**
     * The encrypted Date class
     */
    public static class EncryptedDate extends EncryptedField<DateDay> {
        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws ModelException
         */
        private EncryptedDate(CipherSet pCipherSet,
                              byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException
         */
        private EncryptedDate(CipherSet pCipherSet,
                              DateDay pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected DateDay parseBytes(byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and then an integer */
                return new DateDay(DataConverter.byteArrayToString(pBytes));
            }

            /* Catch Exceptions */
            catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the date to a string and then a byte array */
                return DataConverter.stringToByteArray(getValue().toString());
            }

            /* Catch Exceptions */
            catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
            }
        }
    }

    /**
     * The encrypted CharArray class
     */
    public static class EncryptedCharArray extends EncryptedField<char[]> {
        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws ModelException
         */
        private EncryptedCharArray(CipherSet pCipherSet,
                                   byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException
         */
        private EncryptedCharArray(CipherSet pCipherSet,
                                   char[] pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected char[] parseBytes(byte[] pBytes) throws ModelException {
            return DataConverter.bytesToCharArray(pBytes);
        }

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            return DataConverter.charsToByteArray(getValue());
        }
    }

    /**
     * The encrypted Decimal class
     * @param <X> the decimal type
     */
    public static abstract class EncryptedDecimal<X extends Decimal> extends EncryptedField<X> {
        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws ModelException
         */
        private EncryptedDecimal(CipherSet pCipherSet,
                                 byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException
         */
        private EncryptedDecimal(CipherSet pCipherSet,
                                 X pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected X parseBytes(byte[] pBytes) throws ModelException {
            /* Protect against exceptions */
            try {
                /* Convert the byte array to a string and parse it */
                return parseValue(DataConverter.byteArrayToString(pBytes));
            }

            /* Catch Exceptions */
            catch (ModelException e) {
                throw e;
            } catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value from bytes");
            }
        }

        /**
         * Parse a string value to get a value
         * @param pValue the string value
         * @return the value
         * @throws ModelException
         */
        protected abstract X parseValue(String pValue) throws ModelException;

        @Override
        protected byte[] getBytesForEncryption() throws ModelException {
            /* Protect against exceptions */
            try {
                /* Format the value */
                String myValue = getValue().format(false);

                /* Convert the string to a byte array */
                return DataConverter.stringToByteArray(myValue);
            }

            /* Catch Exceptions */
            catch (Exception e) {
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to convert value to bytes");
            }
        }
    }

    /**
     * The encrypted Money class
     */
    public static class EncryptedMoney extends EncryptedDecimal<Money> {
        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws ModelException
         */
        private EncryptedMoney(CipherSet pCipherSet,
                               byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException
         */
        private EncryptedMoney(CipherSet pCipherSet,
                               Money pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Money parseValue(String pValue) throws ModelException {
            return new Money(pValue);
        }
    }

    /**
     * The encrypted Units class
     */
    public static class EncryptedUnits extends EncryptedDecimal<Units> {
        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws ModelException
         */
        private EncryptedUnits(CipherSet pCipherSet,
                               byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException
         */
        private EncryptedUnits(CipherSet pCipherSet,
                               Units pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Units parseValue(String pValue) throws ModelException {
            return new Units(pValue);
        }
    }

    /**
     * The encrypted Rate class
     */
    public static class EncryptedRate extends EncryptedDecimal<Rate> {
        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws ModelException
         */
        private EncryptedRate(CipherSet pCipherSet,
                              byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException
         */
        private EncryptedRate(CipherSet pCipherSet,
                              Rate pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Rate parseValue(String pValue) throws ModelException {
            return new Rate(pValue);
        }
    }

    /**
     * The encrypted Price class
     */
    public static class EncryptedPrice extends EncryptedDecimal<Price> {
        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws ModelException
         */
        private EncryptedPrice(CipherSet pCipherSet,
                               byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException
         */
        private EncryptedPrice(CipherSet pCipherSet,
                               Price pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Price parseValue(String pValue) throws ModelException {
            return new Price(pValue);
        }
    }

    /**
     * The encrypted Dilution class
     */
    public static class EncryptedDilution extends EncryptedDecimal<Dilution> {
        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pEncrypted the encrypted value of the field
         * @throws ModelException
         */
        private EncryptedDilution(CipherSet pCipherSet,
                                  byte[] pEncrypted) throws ModelException {
            super(pCipherSet, pEncrypted);
        }

        /**
         * Constructor
         * @param pCipherSet the cipherSet
         * @param pUnencrypted the unencrypted value of the field
         * @throws ModelException
         */
        private EncryptedDilution(CipherSet pCipherSet,
                                  Dilution pUnencrypted) throws ModelException {
            super(pCipherSet, pUnencrypted);
        }

        @Override
        protected Dilution parseValue(String pValue) throws ModelException {
            return new Dilution(pValue);
        }
    }
}
