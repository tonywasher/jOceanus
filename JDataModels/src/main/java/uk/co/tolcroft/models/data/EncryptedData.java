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

import uk.co.tolcroft.models.DateDay;
import uk.co.tolcroft.models.Difference;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.Decimal;
import uk.co.tolcroft.models.Decimal.Dilution;
import uk.co.tolcroft.models.Decimal.Money;
import uk.co.tolcroft.models.Decimal.Price;
import uk.co.tolcroft.models.Decimal.Rate;
import uk.co.tolcroft.models.Decimal.Units;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.security.SecurityControl;

public class EncryptedData {
	/**
	 * Obtain value for a field (which may be null)
	 * @param pField the field to obtain the value for
	 * @return the value
	 */
	public static <T> T getValue(EncryptedField<T> pField) {
		return (pField == null) ? null : pField.getValue(); }
	
	/**
	 * Obtain encrypted bytes for a field (which may be null)
	 * @param pField the field to obtain the encrypted bytes for
	 * @return the value
	 */
	public static byte[] getBytes(EncryptedField<?> pField) {
		return (pField == null) ? null : pField.getBytes(); }
	
	/**
	 * Obtain string representation of EncryptedCharArray
	 * @param pArray the charArray to obtain the string for
	 * @return the string
	 */
	public static String getStringFormat(EncryptedCharArray pArray) {
		return (pArray == null) ? null : new String(pArray.getValue()); }
	
	/**
	 * The generic encrypted object class 
	 */
	protected static abstract class EncryptedField<T> {
		/**
		 * Encryption Control
		 */
		private ControlKey	theControlKey	= null;
	
		/**
		 * Encrypted value
		 */
		private byte[]		theEncrypted	= null;
	
		/**
		 * Decrypted value
		 */
		private T			theDecrypted	= null;
	
		/**
		 * Constructor
		 */
		private EncryptedField() {}
		
		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pEncrypted the encrypted value of the field
		 */
		private EncryptedField(ControlKey	pControlKey,
							   byte[] 		pEncrypted) throws ModelException {
			/* Store the control key */
			theControlKey = pControlKey;
			
			/* Store the encrypted value */
			theEncrypted = pEncrypted;
			
			/* Reject if encryption is not initialized */
			if (theControlKey == null)
				throw new ModelException(ExceptionClass.LOGIC,
										 "Encryption is not initialised");
				
			/* Decrypt the Bytes */
			byte[] myBytes = theControlKey.decryptBytes(theEncrypted);

			/* Set the decrypted value */
			theDecrypted = parseBytes(myBytes);
		}
	
		/**
		 * Encrypt the value
		 */
		private void encryptValue() throws ModelException {
			/* Reject if encryption is not initialized */
			if (theControlKey == null)
				throw new ModelException(ExceptionClass.LOGIC,
									     "Encryption is not initialised");
			
			/* Obtain the bytes representation of the value */
			byte[] myBytes = getBytesForEncryption();
			
			/* Encrypt the Bytes */
			theEncrypted = theControlKey.encryptBytes(myBytes);
		}		

		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pUnencrypted the unencrypted value of the field
		 */
		private EncryptedField(ControlKey	pControlKey,
				   			   T 			pUnencrypted) throws ModelException {
			/* Store the control */
			theControlKey = pControlKey;
			
			/* Store the value */
			theDecrypted = pUnencrypted;
			
			/* Return if we have no encryption yet */
			if (theControlKey == null) return;
			
			/* encrypt the value */
			encryptValue();
		}

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		private EncryptedField(EncryptedField<T>	pField) {
			/* Store the control */
			theControlKey	= pField.getControlKey();
			
			/* Store the values */
			theDecrypted 	= pField.getValue();
			theEncrypted 	= pField.getBytes();
		}

		/**
		 * Obtain the ControlKey
		 * @return the Control Key
		 */
		public ControlKey getControlKey() { return theControlKey; }
		
		/**
		 * Obtain the encrypted value
		 * @return the encrypted value
		 */
		public byte[] getBytes() { return theEncrypted; }
		
		/**
		 * Obtain the unencrypted value
		 * @return the unencrypted value
		 */
		public T getValue() { return theDecrypted; }
		
		/**
		 * Set the unencrypted value
		 * @param pValue the unencrypted value
		 */
		public void setValue(T pValue) { theDecrypted = pValue; }
		
		/**
		 * Parse the decrypted bytes
		 * @param pBytes the decrypted bytes
		 * @return the decrypted value
		 */
		protected abstract T parseBytes(byte[] pBytes) throws ModelException;

		/**
		 * Obtain the bytes format to encrypt
		 * @return the bytes to encrypt
		 */
		protected abstract byte[] getBytesForEncryption() throws ModelException;

		/**
		 * Apply fresh encryption to value
		 * @param pControlKey the control Key
		 */
		protected void applyEncryption(ControlKey pControlKey) throws ModelException {
			/* Store the Control Key */
			theControlKey = pControlKey;

			/* Encrypt the value */
			encryptValue();
		}
		
		/**
		 * Adopt Encryption
		 * @param pControlKey the control Key
		 * @param pField field to adopt encryption from 
		 */
		protected void adoptEncryption(ControlKey 			pControlKey,
									   EncryptedField<T> 	pField) throws ModelException { 
			/* Store the Control Key */
			theControlKey = pControlKey;

			/* If we need to renew the encryption */
			if ((pField == null) ||
				(ControlKey.differs(pControlKey, pField.getControlKey()).isDifferent()) ||
				(Utils.differs(getValue(), pField.getValue()).isDifferent())) {
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
			if (this == pThat) return true;
			if (pThat == null) return false;
			
			/* Make sure that the object is the same class */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the target field */
			EncryptedField<?> myThat = (EncryptedField<?>)pThat;
			
			/* Check differences */
			if (Utils.differs(getValue(), myThat.getValue()).isDifferent()) return false;
			if (Utils.differs(getBytes(), myThat.getBytes()).isDifferent()) return false;
			return true;
		}

		/**
		 * Compare two EncryptedFields for differences
		 * @param pNew the other field
		 */
		protected Difference	differs(EncryptedField<?> pNew) {
			/* Reject if null */
			if (pNew == null) return Difference.Different;
			
			/* Reject if wrong class */
			if (this.getClass() != pNew.getClass()) return Difference.Different;
			
			/* Access as correct class */
			EncryptedField<?> myField = (EncryptedField<?>)pNew;
			
			/* Compare Unencrypted value */
			if (Utils.differs(getValue(), myField.getValue()).isDifferent())
				return Difference.Different;

			/* Compare Encrypted value */
			if (Utils.differs(getBytes(), myField.getBytes()).isDifferent())
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
		 * @param pControlKey the control key
		 * @param pEncrypted the encrypted value of the field
		 */
		protected EncryptedString(ControlKey	pControlKey,
				   				  byte[] 		pEncrypted) throws ModelException 	{ super(pControlKey, pEncrypted); }

		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pUnencrypted the unencrypted value of the field
		 */
		protected EncryptedString(ControlKey	pControlKey,
				   				  String 		pUnencrypted) throws ModelException { super(pControlKey, pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public	  EncryptedString(EncryptedString 	pField) 	{ super(pField); }

		@Override
		protected String parseBytes(byte[] pBytes) throws ModelException {
			/* Protect against exceptions */
			try {
				/* Convert the byte array to a string */
				return new String(pBytes, SecurityControl.ENCODING);
			}
			
			/* Catch Exceptions */
			catch (Throwable e) {
				throw new ModelException(ExceptionClass.CRYPTO,
									     "Failed to convert value from bytes");
			}
		}

		@Override
		protected byte[] getBytesForEncryption() throws ModelException {
			/* Protect against exceptions */
			try {
				/* Convert the string to a byte array */
				return getValue().getBytes(SecurityControl.ENCODING);
			}
			
			/* Catch Exceptions */
			catch (Throwable e) {
				throw new ModelException(ExceptionClass.CRYPTO,
									     "Failed to convert value to bytes");
			}
		}
	}
	
	/**
	 * The encrypted Integer class
	 */
	public static class EncryptedInteger extends EncryptedField<Integer> {
		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pEncrypted the encrypted value of the field
		 */
		protected EncryptedInteger(ControlKey	pControlKey,
	   			   				   byte[] 		pEncrypted) throws ModelException 	{ super(pControlKey, pEncrypted); }

		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pUnencrypted the unencrypted value of the field
		 */
		protected EncryptedInteger(ControlKey	pControlKey,
	   			   				   Integer		pUnencrypted) throws ModelException { super(pControlKey, pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public    EncryptedInteger(EncryptedInteger pField) 	{ super(pField); }

		@Override
		protected Integer parseBytes(byte[] pBytes) throws ModelException {
			/* Protect against exceptions */
			try {
				/* Convert the byte array to a string and then an integer */
				return Integer.parseInt(new String(pBytes, SecurityControl.ENCODING));
			}
			
			/* Catch Exceptions */
			catch (Throwable e) {
				throw new ModelException(ExceptionClass.CRYPTO,
									     "Failed to convert value from bytes");
			}
		}

		@Override
		protected byte[] getBytesForEncryption() throws ModelException {
			/* Protect against exceptions */
			try {
				/* Convert the integer to a string and then a byte array */
				return getValue().toString().getBytes(SecurityControl.ENCODING);
			}
			
			/* Catch Exceptions */
			catch (Throwable e) {
				throw new ModelException(ExceptionClass.CRYPTO,
									     "Failed to convert value to bytes");
			}
		}		
	}
		
	/**
	 * The encrypted Boolean class
	 */
	public static class EncryptedBoolean extends EncryptedField<Boolean> {
		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pEncrypted the encrypted value of the field
		 */
		protected EncryptedBoolean(ControlKey	pControlKey,
	   				 			   byte[] 		pEncrypted) throws ModelException 	{ super(pControlKey, pEncrypted); }

		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pUnencrypted the unencrypted value of the field
		 */
		protected EncryptedBoolean(ControlKey	pControlKey,
	   				 			   Boolean		pUnencrypted) throws ModelException { super(pControlKey, pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public    EncryptedBoolean(EncryptedBoolean pField) { super(pField); }

		@Override
		protected Boolean parseBytes(byte[] pBytes) throws ModelException {
			/* Protect against exceptions */
			try {
				/* Convert the byte array to a string and then an integer */
				return Boolean.parseBoolean(new String(pBytes, SecurityControl.ENCODING));
			}
			
			/* Catch Exceptions */
			catch (Throwable e) {
				throw new ModelException(ExceptionClass.CRYPTO,
										 "Failed to convert value from bytes");
			}
		}

		@Override
		protected byte[] getBytesForEncryption() throws ModelException {
			/* Protect against exceptions */
			try {
				/* Convert the boolean to a string and then a byte array */
				return getValue().toString().getBytes(SecurityControl.ENCODING);
			}
			
			/* Catch Exceptions */
			catch (Throwable e) {
				throw new ModelException(ExceptionClass.CRYPTO,
										 "Failed to convert value to bytes");
			}
		}		
	}
		
	/**
	 * The encrypted Date class
	 */
	public static class EncryptedDate extends EncryptedField<DateDay> {
		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pEncrypted the encrypted value of the field
		 */
		protected EncryptedDate(ControlKey	pControlKey,
		 			 			byte[] 		pEncrypted) throws ModelException 	{ super(pControlKey, pEncrypted); }

		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pUnencrypted the unencrypted value of the field
		 */
		protected EncryptedDate(ControlKey	pControlKey,
		 			 			DateDay 	pUnencrypted) throws ModelException { super(pControlKey, pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public    EncryptedDate(EncryptedDate 	pField) 	{ super(pField); }

		@Override
		protected DateDay parseBytes(byte[] pBytes) throws ModelException {
			/* Protect against exceptions */
			try {
				/* Convert the byte array to a string and then an integer */
				return new DateDay(new String(pBytes, SecurityControl.ENCODING));
			}
			
			/* Catch Exceptions */
			catch (Throwable e) {
				throw new ModelException(ExceptionClass.CRYPTO,
										 "Failed to convert value from bytes");
			}
		}
	
		@Override
		protected byte[] getBytesForEncryption() throws ModelException {
			/* Protect against exceptions */
			try {
				/* Convert the date to a string and then a byte array */
				return getValue().formatDate().getBytes(SecurityControl.ENCODING);
			}
			
			/* Catch Exceptions */
			catch (Throwable e) {
				throw new ModelException(ExceptionClass.CRYPTO,
										 "Failed to convert value to bytes");
			}
		}		
	}

	/**
	 * The encrypted CharArray class
	 */
	public static class EncryptedCharArray extends EncryptedField<char[]> {
		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pEncrypted the encrypted value of the field
		 */
		protected EncryptedCharArray(ControlKey	pControlKey,
		 		  					 byte[] 	pEncrypted) throws ModelException 	{ super(pControlKey, pEncrypted); }
	
		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pUnencrypted the unencrypted value of the field
		 */
		protected EncryptedCharArray(ControlKey	pControlKey,
		 		  					 char[] 	pUnencrypted) throws ModelException { super(pControlKey, pUnencrypted); }
	
		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public    EncryptedCharArray(EncryptedCharArray pField) 	{ super(pField); }
	
		@Override
		protected char[] parseBytes(byte[] pBytes) throws ModelException {
			return Utils.byteToCharArray(pBytes);
		}
	
		@Override
		protected byte[] getBytesForEncryption() throws ModelException {
			return Utils.charToByteArray(getValue());
		}		
	}

	/**
	 * The encrypted Decimal class
	 */
	public static abstract class EncryptedDecimal<X extends Decimal> extends EncryptedField<X> {
		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pEncrypted the encrypted value of the field
		 */
		private EncryptedDecimal(ControlKey	pControlKey,
				   				 byte[] 	pEncrypted) throws ModelException 	{ super(pControlKey, pEncrypted); }

		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pUnencrypted the unencrypted value of the field
		 */
		private EncryptedDecimal(ControlKey	pControlKey,
				   				 X 			pUnencrypted) throws ModelException { super(pControlKey, pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		private EncryptedDecimal(EncryptedDecimal<X> 	pField) 	{ super(pField); }

		@Override
		protected X parseBytes(byte[] pBytes) throws ModelException {
			/* Protect against exceptions */
			try {
				/* Convert the byte array to a string and parse it */
				return parseValue(new String(pBytes, SecurityControl.ENCODING));
			}
			
			/* Catch Exceptions */
			catch (ModelException e) { throw e; }
			catch (Throwable e) {
				throw new ModelException(ExceptionClass.CRYPTO,
										 "Failed to convert value from bytes");
			}
		}
		
		/**
		 * Parse a string value to get a value
		 * @param pValue the string value
		 * @return the value
		 */
		protected abstract X parseValue(String pValue) throws ModelException;
				
		@Override
		protected byte[] getBytesForEncryption() throws ModelException {
			/* Protect against exceptions */
			try {
				/* Format the value */
				String myValue = getValue().format(false);
				
				/* Convert the string to a byte array */
				return myValue.getBytes(SecurityControl.ENCODING);
			}
			
			/* Catch Exceptions */
			catch (Throwable e) {
				throw new ModelException(ExceptionClass.CRYPTO,
										 "Failed to convert value to bytes");
			}
		}
	}

	/**
	 * The encrypted Money class
	 */
	public static class EncryptedMoney extends EncryptedDecimal<Money> {
		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pEncrypted the encrypted value of the field
		 */
		public EncryptedMoney(ControlKey	pControlKey,
  				 			  byte[] 		pEncrypted) throws ModelException 	{ super(pControlKey, pEncrypted); }

		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pUnencrypted the unencrypted value of the field
		 */
		public EncryptedMoney(ControlKey 	pControlKey,
  				 			  Money 		pUnencrypted) throws ModelException { super(pControlKey, pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public EncryptedMoney(EncryptedMoney 	pField) 	{ super(pField); }

		@Override
		protected Money parseValue(String pValue) throws ModelException { return new Money(pValue); }
	}

	/**
	 * The encrypted Units class
	 */
	public static class EncryptedUnits extends EncryptedDecimal<Units> {
		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pEncrypted the encrypted value of the field
		 */
		public EncryptedUnits(ControlKey	pControlKey,
  				 			  byte[] 		pEncrypted) throws ModelException 	{ super(pControlKey, pEncrypted); }

		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pUnencrypted the unencrypted value of the field
		 */
		public EncryptedUnits(ControlKey 	pControlKey,
  				 			  Units 		pUnencrypted) throws ModelException { super(pControlKey, pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public EncryptedUnits(EncryptedUnits 	pField) 	{ super(pField); }

		@Override
		protected Units parseValue(String pValue) throws ModelException { return new Units(pValue); }
	}

	/**
	 * The encrypted Rate class
	 */
	public static class EncryptedRate extends EncryptedDecimal<Rate> {
		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pEncrypted the encrypted value of the field
		 */
		public EncryptedRate(ControlKey	pControlKey,
  				 			 byte[] 	pEncrypted) throws ModelException 	{ super(pControlKey, pEncrypted); }

		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pUnencrypted the unencrypted value of the field
		 */
		public EncryptedRate(ControlKey	pControlKey,
  				 			 Rate 		pUnencrypted) throws ModelException { super(pControlKey, pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public EncryptedRate(EncryptedRate 	pField) 	{ super(pField); }

		@Override
		protected Rate parseValue(String pValue) throws ModelException { return new Rate(pValue); }
	}

	/**
	 * The encrypted Price class
	 */
	public static class EncryptedPrice extends EncryptedDecimal<Price> {
		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pEncrypted the encrypted value of the field
		 */
		public EncryptedPrice(ControlKey 	pControlKey,
  				 			  byte[] 		pEncrypted) throws ModelException 	{ super(pControlKey, pEncrypted); }

		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pUnencrypted the unencrypted value of the field
		 */
		public EncryptedPrice(ControlKey 	pControlKey,
  				 			  Price 		pUnencrypted) throws ModelException { super(pControlKey, pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public EncryptedPrice(EncryptedPrice 	pField) 	{ super(pField); }

		@Override
		protected Price parseValue(String pValue) throws ModelException { return new Price(pValue); }
	}

	/**
	 * The encrypted Dilution class
	 */
	public static class EncryptedDilution extends EncryptedDecimal<Dilution> {
		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pEncrypted the encrypted value of the field
		 */
		public EncryptedDilution(ControlKey	pControlKey,
  				 				 byte[] 	pEncrypted) throws ModelException 	{ super(pControlKey, pEncrypted); }

		/**
		 * Constructor
		 * @param pControlKey the control key
		 * @param pUnencrypted the unencrypted value of the field
		 */
		public EncryptedDilution(ControlKey	pControlKey,
  				 				 Dilution	pUnencrypted) throws ModelException { super(pControlKey, pUnencrypted); }

		/**
		 * Constructor
		 * @param pControl the encryption control
		 * @param pField the field to initialise from
		 */
		public EncryptedDilution(EncryptedDilution 	pField) 	{ super(pField); }

		@Override
		protected Dilution parseValue(String pValue) throws ModelException { return new Dilution(pValue); }
	}
}
