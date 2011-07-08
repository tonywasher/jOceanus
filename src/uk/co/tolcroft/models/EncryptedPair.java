package uk.co.tolcroft.models;

import java.util.Arrays;

import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.security.*;

public class EncryptedPair {
	/* Members */
	private SymmetricKey	theKey			= null;
	private byte[]			theInitVector	= null;
	private SecurityCipher	theEncrypt		= null;
	private SecurityCipher	theDecrypt		= null;
	
	/**
	 * Encrypted Money length
	 */
	public final static int MONEYLEN 		= 10;

	/**
	 * Encrypted Units length
	 */
	public final static int UNITSLEN 		= 10;

	/**
	 * Encrypted Rate length
	 */
	public final static int RATELEN 		= 10;

	/**
	 * Encrypted Price length
	 */
	public final static int PRICELEN 		= 10;

	/**
	 * Encrypted Dilution length
	 */
	public final static int DILUTELEN 		= 10;

	/* Access methods for encryption */
	public static String getPairValue(StringPair pPair) {
		return (pPair == null) ? null : pPair.getValue(); }
	public static byte[] getPairBytes(StringPair pPair) {
		return (pPair == null) ? null : pPair.getBytes(); }
	public static char[] getPairValue(CharArrayPair pPair) {
		return (pPair == null) ? null : pPair.getValue(); }
	public static byte[] getPairBytes(CharArrayPair pPair) {
		return (pPair == null) ? null : pPair.getBytes(); }
	public static Money getPairValue(MoneyPair pPair) {
		return (pPair == null) ? null : pPair.getValue(); }
	public static byte[] getPairBytes(MoneyPair pPair) {
		return (pPair == null) ? null : pPair.getBytes(); }
	public static Units getPairValue(UnitsPair pPair) {
		return (pPair == null) ? null : pPair.getValue(); }
	public static byte[] getPairBytes(UnitsPair pPair) {
		return (pPair == null) ? null : pPair.getBytes(); }
	public static Rate getPairValue(RatePair pPair) {
		return (pPair == null) ? null : pPair.getValue(); }
	public static byte[] getPairBytes(RatePair pPair) {
		return (pPair == null) ? null : pPair.getBytes(); }
	public static Price getPairValue(PricePair pPair) {
		return (pPair == null) ? null : pPair.getValue(); }
	public static byte[] getPairBytes(PricePair pPair) {
		return (pPair == null) ? null : pPair.getBytes(); }
	public static Dilution getPairValue(DilutionPair pPair) {
		return (pPair == null) ? null : pPair.getValue(); }
	public static byte[] getPairBytes(DilutionPair pPair) {
		return (pPair == null) ? null : pPair.getBytes(); }
	public static String getCharArrayPairString(CharArrayPair pPair) {
		return ((pPair == null) || (pPair.getValue() == null)) ? null : new String(pPair.getValue());
	}
	
	/**
	 * Constructor
	 */
	public EncryptedPair() {}
	
	/**
	 * Set encryption details
	 * @param pKey the symmetric key
	 * @param pInitVector the Initialisation Vector
	 */
	public void setEncryptionDtl(SymmetricKey 	pKey,
								 byte[]			pInitVector) {
		/* Record the details */
		theKey 			= pKey;
		theInitVector	= pInitVector;
		
		/* Reset the ciphers */
		theEncrypt 	= null;
		theDecrypt	= null;
	}
	
	/**
	 * Set encryption details
	 * @param pKey the symmetric key
	 * @return the New Initialisation Vector
	 */
	public byte[] setEncryptionDtl(SymmetricKey pKey) throws Exception {
		/* Record the details */
		theKey 		= pKey;

		/* Reset the ciphers */
		theEncrypt 	= null;
		theDecrypt	= null;

		/* Create a new encryption cipher and access the initialisation vector */
		theEncrypt = theKey.initEncryption();
		theInitVector	= theEncrypt.getInitVector();
		
		/* Return the new initialisation vector */
		return theInitVector;
	}
	/**
	 * Encrypt a string
	 * @param pValue string to encrypt
	 * @return the encrypted bytes 
	 */
	private byte[] encryptString(String pValue) throws Exception {
		byte[] myBytes = null;
		
		/* Protected against exceptions */
		try {
			/* Handle null string */
			if (pValue == null) return null;
			
			/* If the encryption cipher has been reset */
			if (theEncrypt == null) {
				/* Initialise the encryption cipher */
				theEncrypt = theKey.initEncryption(theInitVector);
			}
			
			/* Encrypt the string */
			myBytes = theEncrypt.encryptString(pValue);
		}
		
		/* Catch exceptions */
		catch (Exception e) {
			/* Reset the encryption cipher and cascade the Exception */
			theEncrypt = null;
			throw e;
		}
		
		/* Catch any exceptions */
		catch (Throwable e) {
			/* Reset the encryption cipher and report the failure */
			theEncrypt = null;
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to encrypt string",
								e);
		}	

		/* Return the encrypted bytes */
		return myBytes;
	}
	
	/**
	 * Encrypt a character array
	 * @param pValue character array to encrypt
	 * @return the encrypted bytes 
	 */
	private byte[] encryptChars(char[] pValue) throws Exception {
		byte[] myBytes = null;
		
		/* Protected against exceptions */
		try {
			/* Handle null array */
			if (pValue == null) return null;
			
			/* If the encryption cipher has been reset */
			if (theEncrypt == null) {
				/* Initialise the encryption cipher */
				theEncrypt = theKey.initEncryption(theInitVector);
			}
			
			/* Encrypt the characters */
			myBytes = theEncrypt.encryptChars(pValue);
		}
		
		/* Catch exceptions */
		catch (Exception e) {
			/* Reset the encryption cipher and cascade the Exception */
			theEncrypt = null;
			throw e;
		}
		
		/* Catch any exceptions */
		catch (Throwable e) {
			/* Reset the encryption cipher and report the failure */
			theEncrypt = null;
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to encrypt characters",
								e);
		}	

		/* Return the encrypted bytes */
		return myBytes;
	}
	
	/**
	 * Decrypt a string
	 * @param pValue bytes to decrypt
	 * @return the decrypted string
	 */
	private String decryptString(byte[] pValue) throws Exception {
		String myString = null;
		
		/* Protected against exceptions */
		try {
			/* Handle null bytes */
			if (pValue == null) return null;
			
			/* If the decryption cipher has been reset */
			if (theDecrypt == null) {
				/* Initialise the decryption cipher */
				theDecrypt = theKey.initDecryption(theInitVector);
			}
			
			/* Decrypt the string */
			myString = theDecrypt.decryptString(pValue);
		}
		
		/* Catch exceptions */
		catch (Exception e) {
			/* Reset the decryption cipher and cascade the Exception */
			theDecrypt = null;
			throw e;
		}
		
		/* Catch any exceptions */
		catch (Throwable e) {
			/* Reset the decryption cipher and report the failure */
			theDecrypt = null;
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to decrypt string",
								e);
		}	

		/* Return the decrypted string */
		return myString;
	}
	
	/**
	 * Decrypt a character array
	 * @param pValue bytes to decrypt
	 * @return the decrypted bytes 
	 */
	private char[] decryptChars(byte[] pValue) throws Exception {
		char[] myChars = null;
		
		/* Protected against exceptions */
		try {
			/* Handle null bytes */
			if (pValue == null) return null;
			
			/* If the decryption cipher has been reset */
			if (theDecrypt == null) {
				/* Initialise the decryption cipher */
				theDecrypt = theKey.initDecryption(theInitVector);
			}
			
			/* Decrypt the characters */
			myChars = theDecrypt.decryptChars(pValue);
		}
		
		/* Catch exceptions */
		catch (Exception e) {
			/* Reset the decryption cipher and cascade the Exception */
			theDecrypt = null;
			throw e;
		}
		
		/* Catch any exceptions */
		catch (Throwable e) {
			/* Reset the decryption cipher and report the failure */
			theDecrypt = null;
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to decrypt characters",
								e);
		}	

		/* Return the decrypted characters */
		return myChars;
	}
	
	/**
	 * Determine whether two StringPair objects differ.
	 * 
	 * @param pCurr The current Pair
	 * @param pNew The new Pair
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(StringPair pCurr, StringPair pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (!pCurr.equals(pNew)))));
	}
	
	/**
	 * Determine whether two CharArrayPair objects differ.
	 * 
	 * @param pCurr The current Pair
	 * @param pNew The new Pair
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(CharArrayPair pCurr, CharArrayPair pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (!pCurr.equals(pNew)))));
	}
	
	/**
	 * Determine whether two MoneyPair objects differ.
	 * 
	 * @param pCurr The current Pair
	 * @param pNew The new Pair
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(MoneyPair pCurr, MoneyPair pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (!pCurr.equals(pNew)))));
	}
	
	/**
	 * Determine whether two UnitsPair objects differ.
	 * 
	 * @param pCurr The current Pair
	 * @param pNew The new Pair
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(UnitsPair pCurr, UnitsPair pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (!pCurr.equals(pNew)))));
	}
	
	/**
	 * Determine whether two RatePair objects differ.
	 * 
	 * @param pCurr The current Pair
	 * @param pNew The new Pair
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(RatePair pCurr, RatePair pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (!pCurr.equals(pNew)))));
	}
	
	/**
	 * Determine whether two PricePair objects differ.
	 * 
	 * @param pCurr The current Pair
	 * @param pNew The new Pair
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(PricePair pCurr, PricePair pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (!pCurr.equals(pNew)))));
	}
	
	/**
	 * Determine whether two DilutionPair objects differ.
	 * 
	 * @param pCurr The current Pair
	 * @param pNew The new Pair
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(DilutionPair pCurr, DilutionPair pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (!pCurr.equals(pNew)))));
	}
	
	/* Encrypted String pair */
	public class StringPair {
		/* Members */
		private String	theValue = null;
		private byte[]	theBytes = null;
		
		/**
		 * Access the value
		 * @return the clear value
		 */
		public String getValue() { return theValue; } 

		/**
		 * Access the encrypted value
		 * @return the encrypted value
		 */
		public byte[] getBytes() { return theBytes;	} 

		/**
		 * Constructor from a clear text value
		 * @param pValue the clear text value
		 */
		public StringPair(String pValue) { 
			/* Store the value and reset bytes */
			theValue = pValue;
		} 

		/**
		 * Constructor from encrypted bytes
		 * @param pBytes the encrypted value
		 */
		public StringPair(byte[] pBytes) throws Exception {
			/* Reject if encryption is not initialised */
			if ((theKey == null) || (theInitVector == null))
				throw new Exception(ExceptionClass.LOGIC,
									"Encryption is not initialised");
			
			/* Store the value */
			theBytes = pBytes;
			
			/* Decrypt the value */
			if (theBytes != null)
				theValue = decryptString(theBytes);
		}
		
		/**
		 * Ensure encryption after spreadsheet load
		 */
		public void ensureEncryption() throws Exception { 
			/* Encrypt the value if required */
			if ((theBytes == null) && (theValue != null))
				theBytes = encryptString(theValue);
		}
		
		/**
		 * Compare this StringPair type to another to establish equality.
		 * @param pThat The Pair to compare to
		 * @return <code>true</code> if the account type is identical, <code>false</code> otherwise
		 */
		public boolean equals(Object pThat) {
			/* Handle the trivial cases */
			if (this == pThat) return true;
			if (pThat == null) return false;
			
			/* Make sure that the object is an EncryptedPair.String */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the target Pair */
			StringPair myThat = (StringPair)pThat;
			
			/* Check differences */
			if (Utils.differs(theValue, myThat.getValue())) return false;
			if (Utils.differs(theBytes, myThat.getBytes())) return false;
			return true;
		}
	}
	
	/* Encrypted Character array */
	public class CharArrayPair {
		/* Members */
		private char[]	theValue = null;
		private byte[]	theBytes = null;
		
		/**
		 * Access the value
		 * @return the clear value
		 */
		public char[] getValue() { return theValue; } 

		/**
		 * Access the encrypted value
		 * @return the encrypted value
		 */
		public byte[] getBytes() { return theBytes;	} 

		/**
		 * Constructor from a clear text value
		 * @param pValue the clear text value
		 */
		public CharArrayPair(char[] pValue) { 
			/* Store the value */
			theValue = pValue;
		} 

		/**
		 * Constructor from encrypted bytes
		 * @param pBytes the encrypted value
		 */
		public CharArrayPair(byte[] pBytes) throws Exception { 
			/* Reject if encryption is not initialised */
			if ((theKey == null) || (theInitVector == null))
				throw new Exception(ExceptionClass.LOGIC,
									"Encryption is not initialised");
			
			/* Store the value */
			theBytes = pBytes;
			
			/* Decrypt the value */
			theValue = decryptChars(theBytes);
		} 

		/**
		 * clear out the character array on termination
		 */
		protected void finalize() throws Throwable {
			if (theValue != null) Arrays.fill(theValue, (char) 0);
		}
		
		/**
		 * Ensure encryption after spreadsheet load
		 */
		public void ensureEncryption() throws Exception { 
			/* Encrypt the value if required */
			if ((theBytes == null) && (theValue != null))
				theBytes = encryptChars(theValue);
		}
		
		/**
		 * Compare this CharArrayPair type to another to establish equality.
		 * @param pThat The Pair to compare to
		 * @return <code>true</code> if the account type is identical, <code>false</code> otherwise
		 */
		public boolean equals(Object pThat) {
			/* Handle the trivial cases */
			if (this == pThat) return true;
			if (pThat == null) return false;
			
			/* Make sure that the object is a CharArrayPair */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the target Pair */
			CharArrayPair myThat = (CharArrayPair)pThat;
			
			/* Check differences */
			if (Utils.differs(theValue, myThat.getValue())) return false;
			if (Utils.differs(theBytes, myThat.getBytes())) return false;
			return true;
		}
	}
	
	/* Encrypted Money */
	public class MoneyPair extends NumberPair<Money> {
		/* Pass-through constructors */
		public MoneyPair(Money  pValue)  					{ super(pValue); }
		public MoneyPair(String pValue) throws Exception 	{ super(pValue); }
		public MoneyPair(byte[] pValue) throws Exception 	{ super(pValue); }

		/* Parse a money value */
		protected Money parseValue(String pValue) throws Exception {
			return new Money(pValue);
		}
	}
	
	/* Encrypted Units */
	public class UnitsPair extends NumberPair<Units> {
		/* Pass-through constructors */
		public UnitsPair(Units  pValue)  					{ super(pValue); }
		public UnitsPair(String pValue) throws Exception 	{ super(pValue); }
		public UnitsPair(byte[] pValue) throws Exception 	{ super(pValue); }

		/* Parse a units value */
		protected Units parseValue(String pValue) throws Exception {
			return new Units(pValue);
		}
	}
	
	/* Encrypted Rate */
	public class RatePair extends NumberPair<Rate> {
		/* Pass-through constructors */
		public RatePair(Rate   pValue)  					{ super(pValue); }
		public RatePair(String pValue) throws Exception 	{ super(pValue); }
		public RatePair(byte[] pValue) throws Exception 	{ super(pValue); }

		/* Parse a rate value */
		protected Rate parseValue(String pValue) throws Exception {
			return new Rate(pValue);
		}
	}
	
	/* Encrypted Price */
	public class PricePair extends NumberPair<Price> {
		/* Pass-through constructors */
		public PricePair(Price  pValue)  					{ super(pValue); }
		public PricePair(String pValue) throws Exception 	{ super(pValue); }
		public PricePair(byte[] pValue) throws Exception 	{ super(pValue); }

		/* Parse a price value */
		protected Price parseValue(String pValue) throws Exception {
			return new Price(pValue);
		}
	}
	
	/* Encrypted Dilution */
	public class DilutionPair extends NumberPair<Dilution> {
		/* Pass-through constructors */
		public DilutionPair(Dilution	pValue)  					{ super(pValue); }
		public DilutionPair(String 		pValue) throws Exception 	{ super(pValue); }
		public DilutionPair(byte[] 		pValue) throws Exception 	{ super(pValue); }

		/* Parse a dilution value */
		protected Dilution parseValue(String pValue) throws Exception {
			return new Dilution(pValue);
		}
	}
	
	/* Encrypted abstract class */
	private abstract class NumberPair<T extends Number> {
		/* Members */
		private T			theValue = null;
		private byte[]		theBytes = null;
		
		/**
		 * Access the value
		 * @return the clear value
		 */
		public T			getValue() { return theValue; } 

		/**
		 * Access the encrypted value
		 * @return the encrypted value
		 */
		public byte[] 		getBytes() { return theBytes;	} 

		/**
		 * Parse a string value to get a value
		 * @param pValue the string value
		 * @return the value
		 */
		protected abstract T parseValue(String pValue) throws Exception;
		
		/**
		 * Constructor from a clear value
		 * @param pValue the clear value
		 */
		public NumberPair(T pValue) { 
			/* Store the value */
			theValue = pValue;
		} 

		/**
		 * Constructor from a string value
		 * @param pValue the clear value
		 */
		public NumberPair(String pValue) throws Exception { 
			/* Store the value */
			theValue = parseValue(pValue);
		} 

		/**
		 * Constructor from encrypted bytes
		 * @param pBytes the encrypted value
		 */
		public NumberPair(byte[] pBytes) throws Exception { 
			/* Reject if encryption is not initialised */
			if ((theKey == null) || (theInitVector == null))
				throw new Exception(ExceptionClass.LOGIC,
									"Encryption is not initialised");
			
			/* Store the value */
			theBytes = pBytes;
			
			/* We have finished if the value is null */
			if (pBytes == null) return;
			
			/* Decrypt the value */
			String myValue = decryptString(theBytes);
			
			/* Parse the value */
			theValue = parseValue(myValue);
		} 

		/**
		 * Ensure encryption after spreadsheet load
		 */
		public void ensureEncryption() throws Exception { 
			/* Encrypt the value if required */
			if ((theBytes == null) && (theValue != null)) {
				/* Format the value */
				String myValue = theValue.format(false);
				theBytes = encryptString(myValue);
			}
		}
		
		/**
		 * Compare this DilutionPair type to another to establish equality.
		 * @param pThat The Pair to compare to
		 * @return <code>true</code> if the account type is identical, <code>false</code> otherwise
		 */
		public boolean equals(Object pThat) {
			/* Handle the trivial cases */
			if (this == pThat) return true;
			if (pThat == null) return false;
			
			/* Make sure that the object is a NumberPair */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the target Pair */
			NumberPair<?> myThat = (NumberPair<?>)pThat;
			
			/* Check differences */
			if (Number.differs(theValue, myThat.getValue())) return false;
			if (Utils.differs(theBytes, myThat.theBytes))   return false;
			return true;
		}
	}
}
