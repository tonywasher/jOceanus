package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.Exception;

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
	private byte[] encryptString(java.lang.String pValue) throws Exception {
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
	private java.lang.String decryptString(byte[] pValue) throws Exception {
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
	
	/* Encrypted String pair */
	public class String {
		/* Members */
		private java.lang.String	theValue = null;
		private byte[]				theBytes = null;
		
		/**
		 * Access the value
		 * @return the clear value
		 */
		public java.lang.String getValue() { return theValue; } 

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
		protected String(java.lang.String pValue) throws Exception { 
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
		protected String(byte[] pBytes) throws Exception { 
			/* Store the value */
			theBytes = pBytes;
			
			/* Encrypt the value */
			theValue = decryptString(theBytes);
		} 

		/**
		 * Set a new value
		 * @param pValue the new clear text value
		 */
		public void setValue(java.lang.String pValue) throws Exception { 
			/* Store the value */
			theValue = pValue;

			/* Encrypt the value */
			theBytes = encryptString(theValue);
		} 
	}
	
	/* Encrypted Character array */
	public class CharArray {
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
		protected CharArray(char[] pValue) throws Exception { 
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
		protected CharArray(byte[] pBytes) throws Exception { 
			/* Store the value */
			theBytes = pBytes;
			
			/* Encrypt the value */
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
	}
}
