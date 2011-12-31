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

public class EncryptionControl {
	/**
	 * ControlKey
	 */
	private ControlKey	theControl	= null;

	/**
	 * Constructor
	 * @param pControl the ControlKey to use
	 */
	public EncryptionControl(ControlKey pControl) {
		/* Store value */
		theControl = pControl;
	}
	
	/**
	 * Obtain the ControlKey
	 * @return the Control Key
	 */
	public ControlKey getControlKey() { return theControl; }
	
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
	 * The generic encrypted object class 
	 */
	private abstract class EncryptedField<T> {
		/**
		 * Encrypted value
		 */
		private byte[]	theEncrypted	= null;
	
		/**
		 * Decrypted value
		 */
		private T		theDecrypted	= null;
	
		/**
		 * Constructor
		 * @param pEncrypted the encrypted value of the field
		 */
		private EncryptedField(byte[] 	pEncrypted) throws ModelException {
			/* Store the encrypted value */
			theEncrypted = pEncrypted;
			
			/* Reject if encryption is not initialised */
			if (theControl == null)
				throw new ModelException(ExceptionClass.LOGIC,
									"Encryption is not initialised");
				
			/* Decrypt the Bytes */
			byte[] myBytes = theControl.decryptBytes(theEncrypted);

			/* Set the decrypted value */
			theDecrypted = parseBytes(myBytes);
		}
	
		/**
		 * Encrypt the value
		 */
		private void encryptValue() throws ModelException {
			/* Reject if encryption is not initialised */
			if (theControl == null)
				throw new ModelException(ExceptionClass.LOGIC,
									"Encryption is not initialised");
			
			/* Obtain the bytes representation of the value */
			byte[] myBytes = getBytesForEncryption();
			
			/* Encrypt the Bytes */
			theEncrypted = theControl.encryptBytes(myBytes);
		}		

		/**
		 * Constructor
		 * @param pUnencrypted the unencrypted value of the field
		 */
		public EncryptedField(T pUnencrypted) throws ModelException {
			/* Store the value */
			theDecrypted = pUnencrypted;
			
			/* Return if we have no encryption yet */
			if (theControl == null) return;
			
			/* encrypt the value */
			encryptValue();
		}

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public EncryptedField(EncryptedField<T> pField) {
			/* Store the values */
			theDecrypted = pField.theDecrypted;
			theEncrypted = pField.theEncrypted;
		}

		/**
		 * Obtain the ControlKey
		 * @return the Control Key
		 */
		public ControlKey getControlKey() { return theControl; }
		
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
		 * @param the decrypted bytes
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
		 */
		protected void renewEncryption() throws ModelException { encryptValue(); }
		
		/**
		 * Adopt Encryption
		 * @param pField field to adopt encryption from 
		 */
		protected void adoptEncryption(EncryptedField<T> pField) throws ModelException { 
			/* If we need to renew the encryption */
			if ((pField == null) ||
				(ControlKey.differs(getControlKey(), pField.getControlKey()).isDifferent()) ||
				(Utils.differs(getValue(), pField.getValue()).isDifferent())) {
				/* encrypt the value again */
				encryptValue();
			}
				
			/* else we can simply adopt the underlying encryption */
			else {
				/* Pick up the underlying encryption */
				theEncrypted = pField.getBytes();
			}
		}
		
		/**
		 * Compare this EncryptedField to another to establish equality.
		 * @param pThat The Field to compare to
		 * @return <code>true</code> if the field is identical, <code>false</code> otherwise
		 */
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
			if (Utils.differs(theEncrypted, myThat.theEncrypted).isDifferent()) return false;
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
	public class EncryptedString extends EncryptedField<String> {
		/**
		 * Constructor
		 * @param pEncrypted the encrypted value of the field
		 */
		public EncryptedString(byte[] pEncrypted) throws ModelException { super(pEncrypted); }

		/**
		 * Constructor
		 * @param pUnencrypted the unencrypted value of the field
		 */
		public EncryptedString(String pUnencrypted) throws ModelException { super(pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public EncryptedString(EncryptedString pField) { super(pField); }

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
	public class EncryptedInteger extends EncryptedField<Integer> {
		/**
		 * Constructor
		 * @param pEncrypted the encrypted value of the field
		 */
		private EncryptedInteger(byte[] pEncrypted) throws ModelException { super(pEncrypted); }

		/**
		 * Constructor
		 * @param pUnencrypted the unencrypted value of the field
		 */
		private EncryptedInteger(Integer pUnencrypted) throws ModelException { super(pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		private EncryptedInteger(EncryptedInteger pField) { super(pField); }

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
	public class EncryptedBoolean extends EncryptedField<Boolean> {
		/**
		 * Constructor
		 * @param pEncrypted the encrypted value of the field
		 */
		private EncryptedBoolean(byte[] pEncrypted) throws ModelException { super(pEncrypted); }

		/**
		 * Constructor
		 * @param pUnencrypted the unencrypted value of the field
		 */
		private EncryptedBoolean(Boolean pUnencrypted) throws ModelException { super(pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		private EncryptedBoolean(EncryptedBoolean pField) { super(pField); }

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
	public class EncryptedDate extends EncryptedField<DateDay> {
		/**
		 * Constructor
		 * @param pEncrypted the encrypted value of the field
		 */
		private EncryptedDate(byte[] pEncrypted) throws ModelException { super(pEncrypted); }

		/**
		 * Constructor
		 * @param pUnencrypted the unencrypted value of the field
		 */
		private EncryptedDate(DateDay pUnencrypted) throws ModelException { super(pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		private EncryptedDate(EncryptedDate pField) { super(pField); }

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
	public class EncryptedCharArray extends EncryptedField<char[]> {
		/**
		 * Constructor
		 * @param pEncrypted the encrypted value of the field
		 */
		private EncryptedCharArray(byte[] pEncrypted) throws ModelException { super(pEncrypted); }
	
		/**
		 * Constructor
		 * @param pUnencrypted the unencrypted value of the field
		 */
		private EncryptedCharArray(char[] pUnencrypted) throws ModelException { super(pUnencrypted); }
	
		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		private EncryptedCharArray(EncryptedCharArray pField) { super(pField); }
	
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
	 * The encrypted Number class
	 */
	public abstract class EncryptedNumber<X extends Decimal> extends EncryptedField<X> {
		/**
		 * Constructor
		 * @param pEncrypted the encrypted value of the field
		 */
		private EncryptedNumber(byte[] pEncrypted) throws ModelException { super(pEncrypted); }

		/**
		 * Constructor
		 * @param pUnencrypted the unencrypted value of the field
		 */
		private EncryptedNumber(X pUnencrypted) throws ModelException { super(pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		private EncryptedNumber(EncryptedNumber<X> pField) { super(pField); }

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
	public class EncryptedMoney extends EncryptedNumber<Money> {
		/**
		 * Constructor
		 * @param pEncrypted the encrypted value of the field
		 */
		public EncryptedMoney(byte[] pEncrypted) throws ModelException { super(pEncrypted); }

		/**
		 * Constructor
		 * @param pUnencrypted the unencrypted value of the field
		 */
		public EncryptedMoney(Money pUnencrypted) throws ModelException { super(pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public EncryptedMoney(EncryptedMoney pField) { super(pField); }

		@Override
		protected Money parseValue(String pValue) throws ModelException { return new Money(pValue); }
	}

	/**
	 * The encrypted Units class
	 */
	public class EncryptedUnits extends EncryptedNumber<Units> {
		/**
		 * Constructor
		 * @param pEncrypted the encrypted value of the field
		 */
		public EncryptedUnits(byte[] pEncrypted) throws ModelException { super(pEncrypted); }

		/**
		 * Constructor
		 * @param pUnencrypted the unencrypted value of the field
		 */
		public EncryptedUnits(Units pUnencrypted) throws ModelException { super(pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public EncryptedUnits(EncryptedUnits pField) { super(pField); }

		@Override
		protected Units parseValue(String pValue) throws ModelException { return new Units(pValue); }
	}

	/**
	 * The encrypted Rate class
	 */
	public class EncryptedRate extends EncryptedNumber<Rate> {
		/**
		 * Constructor
		 * @param pEncrypted the encrypted value of the field
		 */
		public EncryptedRate(byte[] pEncrypted) throws ModelException { super(pEncrypted); }

		/**
		 * Constructor
		 * @param pUnencrypted the unencrypted value of the field
		 */
		public EncryptedRate(Rate pUnencrypted) throws ModelException { super(pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public EncryptedRate(EncryptedRate pField) { super(pField); }

		@Override
		protected Rate parseValue(String pValue) throws ModelException { return new Rate(pValue); }
	}

	/**
	 * The encrypted Price class
	 */
	public class EncryptedPrice extends EncryptedNumber<Price> {
		/**
		 * Constructor
		 * @param pEncrypted the encrypted value of the field
		 */
		public EncryptedPrice(byte[] pEncrypted) throws ModelException { super(pEncrypted); }

		/**
		 * Constructor
		 * @param pUnencrypted the unencrypted value of the field
		 */
		public EncryptedPrice(Price pUnencrypted) throws ModelException { super(pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public EncryptedPrice(EncryptedPrice pField) { super(pField); }

		@Override
		protected Price parseValue(String pValue) throws ModelException { return new Price(pValue); }
	}

	/**
	 * The encrypted Dilution class
	 */
	public class EncryptedDilution extends EncryptedNumber<Dilution> {
		/**
		 * Constructor
		 * @param pEncrypted the encrypted value of the field
		 */
		public EncryptedDilution(byte[] pEncrypted) throws ModelException { super(pEncrypted); }

		/**
		 * Constructor
		 * @param pUnencrypted the unencrypted value of the field
		 */
		public EncryptedDilution(Dilution pUnencrypted) throws ModelException { super(pUnencrypted); }

		/**
		 * Constructor
		 * @param pField the field to initialise from
		 */
		public EncryptedDilution(EncryptedDilution pField) { super(pField); }

		@Override
		protected Dilution parseValue(String pValue) throws ModelException { return new Dilution(pValue); }
	}
}