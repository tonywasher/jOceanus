package uk.co.tolcroft.finance.data;

import java.util.Arrays;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.Number.*;

public class EncryptedPair {
	/* Members */
	private DataSet theData 	= null;
	private Static  theStatic	= null;
	
	/**
	 * Encrypted Money length
	 */
	public final static int MONEYLEN 		= 20;

	/**
	 * Encrypted Units length
	 */
	public final static int UNITSLEN 		= 20;

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
		protected StringPair(byte[] pBytes) throws Exception { 
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
		protected CharArrayPair(char[] pValue) { 
			/* Store the value */
			theValue = pValue;
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
		public byte[] getBytes() { return theBytes;	} 

		/**
		 * Constructor from a clear value
		 * @param pValue the clear value
		 */
		public MoneyPair(Money pValue) { 
			/* Store the value */
			theValue = pValue;
		} 

		/**
		 * Constructor from a string value
		 * @param pValue the clear value
		 */
		protected MoneyPair(String pValue) throws Exception { 
			/* Store the value */
			theValue = Money.Parse(pValue);
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
			
			/* Check differences */
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
		public byte[] getBytes() { return theBytes;	} 

		/**
		 * Constructor from a clear value
		 * @param pValue the clear value
		 */
		public UnitsPair(Units pValue) { 
			/* Store the value */
			theValue = pValue;
		} 

		/**
		 * Constructor from a string value
		 * @param pValue the clear value
		 */
		protected UnitsPair(String pValue) throws Exception { 
			/* Store the value */
			theValue = Units.Parse(pValue);
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
			
			/* Check differences */
			if (Units.differs(theValue, myThat.getValue())) return false;
			if (Utils.differs(theBytes, myThat.theBytes))   return false;
			return true;
		}
	}
}
