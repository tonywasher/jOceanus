package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.Number.*;

public class EncryptedPair {
	/* Members */
	private DataSet theData 	= null;
	private Static  theStatic	= null;
	
	/**
	 * Constructor
	 * @param pData the Data set
	 */
	protected EncryptedPair(DataSet pData) {
		theData = pData;
	}
	
	/**
	 * Access the default static 
	 * @param pControl the new control 
	 */
	private Static ensureStatic() throws Exception {
		/* If we do not yet know the static */
		if (theStatic == null) {
			/* Access the Default static */
			theStatic = theData.ensureStatic();
		}
		
		/* Return the static */
		return theStatic;
	}
	
	/**
	 * Encrypt the string
	 */
	private byte[] encryptString(String pValue) throws Exception {
		/* Access the static element */
		Static myStatic = ensureStatic();
		
		/* Encrypt the string */
		return myStatic.encryptString(pValue);
	}
	
	/**
	 * Encrypt the characters
	 */
	private byte[] encryptChars(char[] pValue) throws Exception {
		/* Access the static element */
		Static myStatic = ensureStatic();
		
		/* Encrypt the characters */
		return myStatic.encryptChars(pValue);
	}
	
	/**
	 * Decrypt the string
	 */
	private String decryptString(byte[] pValue) throws Exception {
		/* Access the static element */
		Static myStatic = ensureStatic();
		
		/* Decrypt the characters */
		return myStatic.decryptString(pValue);
	}
	
	/**
	 * Decrypt the characters
	 */
	private char[] decryptChars(byte[] pValue) throws Exception {
		/* Access the static element */
		Static myStatic = ensureStatic();
		
		/* Decrypt the characters */
		return myStatic.decryptChars(pValue);
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
		public byte[] getBytes() throws Exception { 
			/* If the value has never been encrypted */
			if ((theBytes == null) && (theValue != null)) {
				/* Encrypt the value */
				theBytes = encryptString(theValue);
			}
			
			/* Return the encrypted bytes */
			return theBytes;
		} 

		/**
		 * Constructor from a clear text value
		 * @param pValue the clear text value
		 */
		protected StringPair(String pValue) throws Exception { 
			/* Store the value */
			theValue = pValue;
			
			/* Access the static element */
			Static myStatic = theData.ensureStatic();

			/* If we have a security control */
			if (myStatic.getSecurityControl() != null) {
				/* Encrypt the value */
				theBytes = encryptString(theValue);
			}
		} 

		/**
		 * Constructor from encrypted bytes
		 * @param pBytes the encrypted value
		 */
		protected StringPair(byte[] pBytes) throws Exception { 
			/* Store the value */
			theBytes = pBytes;
			
			/* Decrypt the value */
			theValue = decryptString(theBytes);
		} 

		/**
		 * Set a new value
		 * @param pValue the new clear text value
		 */
		public void setValue(String pValue) throws Exception { 
			/* Store the value */
			theValue = pValue;

			/* Encrypt the value */
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
			
			if (Utils.differs(theValue, myThat.getValue())) return false;
			if (Utils.differs(theBytes, myThat.theBytes))   return false;
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
		public byte[] getBytes() throws Exception { 
			/* If the value has never been encrypted */
			if ((theBytes == null) && (theValue != null)) {
				/* Encrypt the value */
				theBytes = encryptChars(theValue);
			}
			
			/* Return the encrypted bytes */
			return theBytes;
		} 

		/**
		 * Constructor from a clear text value
		 * @param pValue the clear text value
		 */
		protected CharArrayPair(char[] pValue) throws Exception { 
			/* Store the value */
			theValue = pValue;
			
			/* Access the static element */
			Static myStatic = theData.ensureStatic();

			/* If we have a security control */
			if (myStatic.getSecurityControl() != null) {
				/* Encrypt the value */
				theBytes = encryptChars(theValue);
			}
		} 

		/**
		 * Constructor from encrypted bytes
		 * @param pBytes the encrypted value
		 */
		protected CharArrayPair(byte[] pBytes) throws Exception { 
			/* Store the value */
			theBytes = pBytes;
			
			/* Decrypt the value */
			theValue = decryptChars(theBytes);
		} 

		/**
		 * Set a new value
		 * @param pValue the new clear text value
		 */
		public void setValue(char[] pValue) throws Exception { 
			/* Store the value */
			theValue = pValue;

			/* Encrypt the value */
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
			
			if (Utils.differs(theValue, myThat.getValue())) return false;
			if (Utils.differs(theBytes, myThat.theBytes))   return false;
			return true;
		}
	}
	
	/* Encrypted Money */
	public class MoneyPair {
		/* Members */
		private Money	theValue = null;
		private byte[]	theBytes = null;
		
		/**
		 * Access the value
		 * @return the clear value
		 */
		public Money getValue() { return theValue; } 

		/**
		 * Access the encrypted value
		 * @return the encrypted value
		 */
		public byte[] getBytes() throws Exception { 
			/* If the value has never been encrypted */
			if ((theBytes == null) && (theValue != null)) {
				/* Format the value */
				String myValue = theValue.format(false);
				
				/* Encrypt the value */
				theBytes = encryptString(myValue);
			}
			
			/* Return the encrypted bytes */
			return theBytes;
		} 

		/**
		 * Constructor from a clear value
		 * @param pValue the clear value
		 */
		protected MoneyPair(Money pValue) throws Exception { 
			/* Store the value */
			theValue = pValue;
			
			/* We have finished if the value is null */
			if (pValue == null) return;
			
			/* Access the static element */
			Static myStatic = theData.ensureStatic();

			/* If we have a security control */
			if (myStatic.getSecurityControl() != null) {
				/* Format the value */
				String myValue = theValue.format(false);
				
				/* Encrypt the value */
				theBytes = encryptString(myValue);
			}
		} 

		/**
		 * Constructor from encrypted bytes
		 * @param pBytes the encrypted value
		 */
		protected MoneyPair(byte[] pBytes) throws Exception { 
			/* Store the value */
			theBytes = pBytes;
			
			/* We have finished if the value is null */
			if (pBytes == null) return;
			
			/* Decrypt the value */
			String myValue = decryptString(theBytes);
			
			/* Parse the value */
			theValue = Money.Parse(myValue);
		} 

		/**
		 * Set a new value
		 * @param pValue the new clear text value
		 */
		public void setValue(Money pValue) throws Exception { 
			/* Store the value */
			theValue = pValue;

			/* Format the value */
			String myValue = theValue.format(false);
			
			/* Encrypt the value */
			theBytes = encryptString(myValue);
		} 
		
		/**
		 * Compare this MoneyPair type to another to establish equality.
		 * @param pThat The Pair to compare to
		 * @return <code>true</code> if the account type is identical, <code>false</code> otherwise
		 */
		public boolean equals(Object pThat) {
			/* Handle the trivial cases */
			if (this == pThat) return true;
			if (pThat == null) return false;
			
			/* Make sure that the object is a MoneyPair */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the target Pair */
			MoneyPair myThat = (MoneyPair)pThat;
			
			if (Money.differs(theValue, myThat.getValue())) return false;
			if (Utils.differs(theBytes, myThat.theBytes))   return false;
			return true;
		}
	}
	
	/* Encrypted Units */
	public class UnitsPair {
		/* Members */
		private Units	theValue = null;
		private byte[]	theBytes = null;
		
		/**
		 * Access the value
		 * @return the clear value
		 */
		public Units getValue() { return theValue; } 

		/**
		 * Access the encrypted value
		 * @return the encrypted value
		 */
		public byte[] getBytes() throws Exception { 
			/* If the value has never been encrypted */
			if ((theBytes == null) && (theValue != null)) {
				/* Format the value */
				String myValue = theValue.format(false);
				
				/* Encrypt the value */
				theBytes = encryptString(myValue);
			}
			
			/* Return the encrypted bytes */
			return theBytes;
		} 

		/**
		 * Constructor from a clear value
		 * @param pValue the clear value
		 */
		protected UnitsPair(Units pValue) throws Exception { 
			/* Store the value */
			theValue = pValue;
			
			/* We have finished if the value is null */
			if (pValue == null) return;
			
			/* Access the static element */
			Static myStatic = theData.ensureStatic();

			/* If we have a security control */
			if (myStatic.getSecurityControl() != null) {
				/* Format the value */
				String myValue = theValue.format(false);
				
				/* Encrypt the value */
				theBytes = encryptString(myValue);
			}
		} 

		/**
		 * Constructor from encrypted bytes
		 * @param pBytes the encrypted value
		 */
		protected UnitsPair(byte[] pBytes) throws Exception { 
			/* Store the value */
			theBytes = pBytes;
			
			/* We have finished if the value is null */
			if (pBytes == null) return;
			
			/* Decrypt the value */
			String myValue = decryptString(theBytes);
			
			/* Parse the value */
			theValue = Units.Parse(myValue);
		} 

		/**
		 * Set a new value
		 * @param pValue the new clear text value
		 */
		public void setValue(Units pValue) throws Exception { 
			/* Store the value */
			theValue = pValue;

			/* Format the value */
			String myValue = theValue.format(false);
			
			/* Encrypt the value */
			theBytes = encryptString(myValue);
		} 
		
		/**
		 * Compare this MoneyPair type to another to establish equality.
		 * @param pThat The Pair to compare to
		 * @return <code>true</code> if the account type is identical, <code>false</code> otherwise
		 */
		public boolean equals(Object pThat) {
			/* Handle the trivial cases */
			if (this == pThat) return true;
			if (pThat == null) return false;
			
			/* Make sure that the object is a UnitsPair */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the target Pair */
			UnitsPair myThat = (UnitsPair)pThat;
			
			if (Units.differs(theValue, myThat.getValue())) return false;
			if (Utils.differs(theBytes, myThat.theBytes))   return false;
			return true;
		}
	}
}
