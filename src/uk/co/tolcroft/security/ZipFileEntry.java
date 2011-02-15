package uk.co.tolcroft.security;

import java.util.Arrays;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class ZipFileEntry {
	/**
	 * The file separator 
	 */
	private final static char 	theFileSeparator 	= ';';
	
	/**
	 * The property separator 
	 */
	private final static char	thePropSeparator 	= '/';
	
	/**
	 * The value separator 
	 */
	private final static char	theValuSeparator 	= '=';
	
	/**
	 * The value separator 
	 */
	private final static char	theLongSeparator 	= '!';
	
	
	/**
	 * The property name of a file
	 */
	protected final static String propName			= "Name";
	
	/**
	 * The RawData property name of a file
	 */
	protected final static String propRawData		= "RawData";
	
	/**
	 * The CompData property name of a file
	 */
	protected final static String propCompData		= "CompressedData";
	
	/**
	 * The EncData property name of a file
	 */
	protected final static String propEncData		= "EncryptedData";
	
	/**
	 * The Signature property name of a file
	 */
	protected final static String propSignature		= "Signature";
	
	/**
	 * The SecretKey property name of a file
	 */
	protected final static String propSecretKey		= "SecretKey";
	
	/**
	 * The InitVector property name of a file
	 */
	protected final static String propInitVector	= "InitVector";
	
	/**
	 * The SecurityKey property name of a zipfile
	 */
	protected final static String propSecurityKey	= "SecurityKey";
	
	/**
	 * The next file in the list
	 */
	private ZipFileEntry 		theNext 			= null;

	/**
	 * The property list
	 */
	private property 			theProperties		= null;

	/**
	 * Is the file encrypted
	 */
	private boolean 			isEncrypted			= false;

	/**
	 * Is the file compressed
	 */
	private boolean 			isCompressed		= false;

	/**
	 * Has the file got a security key
	 */
	private boolean 			hasSecurityKey		= false;

	/**
	 * Is the file encrypted in the zip file
	 * @return is the file encrypted?   
	 */
	public boolean 		isEncrypted() 			{ return isEncrypted; }
	
	/**
	 * Is the file compressed in the zip file 
	 * @return is the file compressed?
	 */
	public boolean 		isCompressed() 			{ return isCompressed; }
	
	/**
	 * Has the file got a security key 
	 * @return true/false?
	 */
	public boolean 		hasSecurityKey() 		{ return hasSecurityKey; }
	
	/**
	 * Obtain the next file entry 
	 * @return the next file entry
	 */
	public ZipFileEntry	getNext() 				{ return theNext; }
	
	/**
	 * Obtain the name of the file 
	 * @return the name of the file
	 */
	public String 		getFileName() 			{ return new String(getByteProperty(propName)); }
	
	/**
	 * Obtain the raw data length of the file 
	 * @return the raw data length of the file
	 */
	public long 		getRawDataLen() 		{ return getLongProperty(propRawData); }
	
	/**
	 * Obtain the compressed data length of the file 
	 * @return the compressed data length of the file
	 */
	public long 		getCompressedDataLen() 	{ return getLongProperty(propCompData); }
	
	/**
	 * Obtain the encrypted data length of the file 
	 * @return the encrypted data length of the file
	 */
	public long 		getEncryptedDataLen() 	{ return getLongProperty(propEncData); }
	
	/**
	 * Obtain the raw data digest of the file 
	 * @return the raw data digest of the file
	 */
	public byte[] 		getRawDigest() 			{ return getByteProperty(propRawData); }
	
	/**
	 * Obtain the compressed data digest of the file 
	 * @return the compressed data digest of the file
	 */
	public byte[] 		getCompressedDigest() 	{ return getByteProperty(propCompData); }
	
	/**
	 * Obtain the encrypted data digest of the file 
	 * @return the encrypted data digest of the file
	 */
	public byte[] 		getEncryptedDigest() 	{ return getByteProperty(propEncData); }
	
	/**
	 * Obtain the initialisation vector for the file 
	 * @return the initialisation vector for the file
	 */
	public byte[] 		getInitVector() 		{ return getByteProperty(propInitVector); }
	
	/**
	 * Obtain the secret key for the file 
	 * @return the secret key for the file
	 */
	public byte[] 		getSecretKey() 			{ return getByteProperty(propSecretKey); }
	
	/**
	 * Obtain the signature for the file 
	 * @return the signature for the file
	 */
	public byte[] 		getSignature() 			{ return getByteProperty(propSignature); }
	
	/**
	 * Obtain the SecurityKey of the ZipFile 
	 * @return the SecurityKey of the file
	 */
	public String 		getSecurityKey() 		{ return new String(getByteProperty(propSecurityKey)); }
	
	/**
	 * Standard constructor 
	 */
	protected ZipFileEntry() {}
	
	/**
	 * Construct encryption properties from encoded string
	 * @param pCodedString the encoded properties 
	 */
	protected ZipFileEntry(String pCodedString) throws Exception {
		StringBuilder	myString = new StringBuilder(pCodedString);
		String			myPropSep	= Character.toString(thePropSeparator);
		String			myFileSep	= Character.toString(theFileSeparator);
		int				myLoc;
		
		/* If there is a file separator in the string */
		if ((myLoc = myString.indexOf(myFileSep)) != -1) {
			/* Parse the trailing data and remove it */
			theNext = new ZipFileEntry(myString.substring(myLoc+1));
			myString.setLength(myLoc);
		}
		
		/* while we have separators in the string */
		while ((myLoc = myString.indexOf(myPropSep)) != -1) {
			/* Parse the encoded property and remove it from the buffer */
			parseEncodedProperty(myString.substring(0, myLoc));
			myString.delete(0, myLoc+1);
		}
		
		/* Parse the remaining property */
		parseEncodedProperty(myString.toString());
		
		/* Determine whether the file is encrypted */
		if (getProperty(propEncData) != null) 	isEncrypted 	= true;
		
		/* Determine whether the file is compressed */
		if (getProperty(propCompData) != null) 	isCompressed 	= true;
		
		/* Determine whether the file has a security key */
		if (getProperty(propSecurityKey) != null) 	hasSecurityKey 	= true;
	}
	
	/**s
	 * Obtain the bytes value of the named property
	 * @return the value of the property or <code>null</code> if the property does not exist
	 */
	public byte[] getByteProperty(String pName) {
		property myProperty;
		
		/* Access the property */
		myProperty  = getProperty(pName);
		
		/* Return the value */
		return (myProperty == null) ? null : myProperty.getByteValue();
	}
	
	/**
	 * Obtain the long value of the named property
	 * @return the value of the property or <code>-1</code> if the property does not exist
	 */
	private long getLongProperty(String pName) {
		property myProperty;
		
		/* Access the property */
		myProperty  = getProperty(pName);
		
		/* Return the value */
		return (myProperty == null) ? -1 : myProperty.getLongValue();
	}
	
	/**
	 * Set the next file entry
	 * @param pEntry the next entry
	 */
	protected void setNext(ZipFileEntry pEntry) { theNext = pEntry; }

	/**
	 * Obtain the named property
	 * @return the value of the property or <code>null</code> if the property does not exist
	 */
	private property getProperty(String pName) {
		property myProperty;
		
		/* Loop through the properties */
		for (myProperty  = theProperties;
			 myProperty != null;
			 myProperty  = myProperty.getNext()) {
			/* Break loop if this is the desired property */
			if (myProperty.getName().compareTo(pName) == 0) break;
		}
		
		/* Return the value */
		return myProperty;
	}
	
	/**
	 * Set the named property
	 * @param pName the name of the property
	 * @param pValue the Value of the property
	 */
	protected void setProperty(String pName, byte[] pValue) {
		property myProperty;
		
		/* Access any existing property */
		myProperty = getProperty(pName);
		
		/* If the property already exists */
		if (myProperty != null) {
			/* Set the new value */
			myProperty.setByteValue(pValue);
		}
		
		/* else this is a new property */
		else {
			/* Create the new property */
			myProperty = new property(pName, pValue, -1);
							
			/* Add it to the list */
			addToList(myProperty);
		}
	}
	
	/**
	 * Set the named property
	 * @param pName the name of the property
	 * @param pValue the Value of the property
	 */
	protected void setProperty(String pName, long pValue) {
		property myProperty;
		
		/* Access any existing property */
		myProperty = getProperty(pName);
		
		/* If the property already exists */
		if (myProperty != null) {
			/* Set the new value */
			myProperty.setLongValue(pValue);
		}
		
		/* else this is a new property */
		else {
			/* Create the new property */
			myProperty = new property(pName, null, pValue);
							
			/* Add it to the list */
			addToList(myProperty);
		}
	}
	
	/**
	 * Obtain the encoded string representing the properties
	 * @return the encoded string
	 */
	protected String getEncodedString() {
		property 		myProperty;
		StringBuilder 	myString = new StringBuilder(1000);
		StringBuilder	myValue  = new StringBuilder(200);
		char	 		myChar;
		int		 		myDigit;
		int		 		myInt;
		
		/* Loop through the list */
		for (myProperty  = theProperties;
			 myProperty != null;
			 myProperty  = myProperty.getNext()) {
			/* Build the value string */
			myValue.setLength(0);
			myValue.append(myProperty.getName());
			myValue.append(theValuSeparator);
			
			/* If we have a byte value */
			if (myProperty.getByteValue() != null) {
				/* For each byte in the value */
				for (Byte b : myProperty.getByteValue()) {
					/* Access the byte as an unsigned integer */
					myInt = (int) b;
					if (myInt<0) myInt+=256;
				
					/* Access the high digit */
					myDigit = myInt / 16;
					myChar = (char)((myDigit > 9) ? ('a' + (myDigit-10)) : ('0' + myDigit));
				
					/* Add it to the value string */
					myValue.append(myChar);
					
					/* Access the low digit */
					myDigit = myInt % 16;
					myChar = (char)((myDigit > 9) ? ('a' + (myDigit-10)) : ('0' + myDigit));
				
					/* Add it to the value string */
					myValue.append(myChar);
				}
			}
			
			/* Add the value separator */
			myValue.append(theLongSeparator);
			
			/* Add the long value if it exists */
			if (myProperty.getLongValue() != -1) {
				/* Access the value */
				long value = myProperty.getLongValue();
				
				/* handle negative values */
				boolean isNegative = (value < 0);
				if (isNegative) value = -value;
				
				/* Special case for zero */
				if (value == 0) myValue.append('0');
				
				/* else need to loop through the digits */
				else {
					/* Create a new string Builder variable */
					StringBuilder myLong = new StringBuilder();
					
					/* While we have digits to format */
					while (value > 0) {
						/* Access the digit and move to next one */
						myDigit = (int)(value % 16);
						myChar  = (char)((myDigit > 9) ? ('a' + (myDigit-10)) : ('0' + myDigit));
						myLong.insert(0, myChar);
						value  /= 16;
					}
					
					/* Reinstate negative sign and append to value */
					if (isNegative) myLong.insert(0, '-');
					myValue.append(myLong);
				}	
			}
			
			/* Add the value to the string */
			if (myString.length() > 0) myString.append(thePropSeparator);
			myString.append(myValue);
		}
		
		/* If we have further files */
		if (theNext != null) {
			/* Add the encoding of the further files */
			myString.append(theFileSeparator);
			myString.append(theNext.getEncodedString());
		}
		
		/* Return the encoded string */
		return myString.toString();
	}
	
	/**
	 * Parse the encoded string representation to obtain the property
	 * @param pProperty the encoded property
	 */
	private void parseEncodedProperty(String pValue) throws Exception {
		property	myProperty;
		String  	myName;
		String		myBytes;
		String		myLong;
		byte[]		myByteValue;
		long		myLongValue;
		char		myChar;
		int			myInt;
		int			myLen;
		int 		myLoc;
		
		/* Locate the Value separator in the string */
		myLoc = pValue.indexOf(theValuSeparator);
		
		/* Check that we found the value separator */
		if (myLoc == -1) 
			throw new Exception(ExceptionClass.DATA,
								"Missing value separator: " + pValue);

		/* Split the values and name */
		myName 	= pValue.substring(0, myLoc);
		myBytes = pValue.substring(myLoc+1);
		myLen   = myBytes.length();
		
		/* If the name is already present reject it */
		if (getProperty(myName) != null) 
			throw new Exception(ExceptionClass.DATA,
								"Duplicate name: " + pValue);
		
		/* Locate the Long separator in the string */
		myLoc = myBytes.indexOf(theLongSeparator);
		
		/* Check that we found the long separator */
		if (myLoc == -1) 
			throw new Exception(ExceptionClass.DATA,
								"Missing long separator: " + pValue);

		/* Access the separate byte and long values */
		myLong 	= (myLoc < myLen-1) ? myBytes.substring(myLoc+1) : null;
		myBytes = (myLoc > 0) ? myBytes.substring(0, myLoc) : null;
		
		/* Initialise the values */
		myByteValue = null;
		myLongValue = -1;

		/* If we have a bytes array */
		if (myBytes != null) {
			/* Access the length of the bytes section */
			myLen = myBytes.length();
			
			/* Check that it has an even length */
			if ((myLen % 2) != 0) 
				throw new Exception(ExceptionClass.DATA,
									"Invalid Bytes Encoded Value Length: " + pValue);
			
			/* Allocate the new bytes array */
			myByteValue = new byte[myLen / 2];
		
			/* Loop through the string */
			for (int i=0; i < myLen; i+=2) {
				/* Access the top level byte */
				myChar = myBytes.charAt(i);
				myInt  = 16 * ((myChar >= 'a') ? 10 + (myChar - 'a') : (myChar - '0'));
		
				/* Check that the char is a valid hex digit */
				if (!Character.isDigit(myChar) && ((myChar < 'a') || (myChar > 'f'))) 
					throw new Exception(ExceptionClass.DATA,
										"Non Hexadecimal Bytes Value: " + pValue);
				
				/* Access the second byte */
				myChar = myBytes.charAt(i+1);
				myInt  += ((myChar >= 'a') ? 10 + (myChar - 'a') : (myChar - '0'));
					
				/* Check that the char is a valid hex digit */
				if (!Character.isDigit(myChar) && ((myChar < 'a') || (myChar > 'f'))) 
					throw new Exception(ExceptionClass.DATA,
										"Non Hexadecimal Bytes Value: " + pValue);
				
				/* Convert to byte and store */
				if (myInt > 127) myInt -= 256;
				myByteValue[i/2] = (byte)myInt;
			}
		}
		
		/* If we have a long value */
		if (myLong != null) {				
 			/* Initialise values */
	 		myLen 		= myLong.length();
	 		myLongValue = 0;

	 		/* Loop through the characters of the integer part of the value */
	 		for (int i=0; i<myLen; i++) {
				/* Access the next character */
				myChar = myLong.charAt(i);
	 				
				/* Check that the char is a valid hex digit */
				if (!Character.isDigit(myChar) && ((myChar < 'a') || (myChar > 'f'))) 
					throw new Exception(ExceptionClass.DATA,
									    "Non Hexadecimal Numeric Value: " + myLong);
					
				/* Add into the value */
				myLongValue *= 16;
				myLongValue += ((myChar >= 'a') ? 10 + (myChar - 'a') : (myChar - '0'));
	 		}
		}
		
		/* Create a new property */
		myProperty = new property(myName, myByteValue, myLongValue);
		
		/* Add it to the list */
		addToList(myProperty);
	}

	/**
	 * Add a property to the list
	 * @param pProperty the property to add 
	 */
	private void addToList(property pProperty) {
		property myProperty;
		property myLast;
		String	 myName;
		
		/* Access the property name */
		myName = pProperty.getName();
		
		/* Loop through the list */
		for (myProperty  = theProperties, myLast = null;
			 myProperty != null;
			 myLast = myProperty, myProperty  = myProperty.getNext()) {
			/* Break if this property should be later than the passed property */
			if (myProperty.getName().compareTo(myName) > 0) break;
		}

		/* If we do not have an insert point */
		if (myLast == null) {
			/* Add the value to the head of the list */
			pProperty.setNext(theProperties);
			theProperties = pProperty;
		}
		
		/* Else we have an insert point */
		else {
			/* Add the value in the middle of the list */
			pProperty.setNext(myProperty);
			myLast.setNext(pProperty);
		}
	}
	
	/**
	 * compare this property set to another
	 * @param pThat the properties to compare against
	 * @return (-1,0,1) depending on order 
	 */
	 public int compareTo(ZipFileEntry pThat) {
		 property myThis;
		 property myThat;
		 int 	  iDiff;
	 
		 /* If that does not exist return 1 */
		 if (pThat == null) return 1;
		 
		 /* Loop through the list */
		 for (myThis  = theProperties, myThat = pThat.theProperties;
			  myThis != null;
			  myThis  = myThis.getNext(), myThat = myThat.getNext()) {
			 /* If we have finished "that's" list return 1 */
			 if (myThat == null) return 1;
			 
			 /* If there is a difference return it */
			 if ((iDiff = myThis.compareTo(myThat)) != 0) return iDiff;
		 }
		 
		 /* If we have not finished "that's" list return -1 */
		 if (myThat != null) return -1;

		 /* If we have another entry compare that */
		 if (theNext != null) return theNext.compareTo(pThat.theNext);
		 
		 /* If there are further files for that */
		 if (pThat.theNext != null) return -1;
		 
		 /* Return no difference */
		 return 0;
	 }
	 
	/**
	 * Inner Property class
	 */
	private class property {
		/**
		 * Name of property
		 */
		private String 		theName 		= null;
		
		/**
		 * Value of property
		 */
		private byte[]		theByteValue	= null;
		
		/**
		 * Value of property
		 */
		private long		theLongValue	= -1;
		
		/** 
		 * Link to next property
		 */
		private property	theNext			= null;
		
		/**
		 * Standard Constructor
		 * @param pName the name of the property
		 * @param pBytes the Bytes value of the property
		 * @param pLong the Long value of the property
		 */
		private property(String pName, byte[] pBytes, long pLong) {
			/* Store name and value */
			theName 		= pName;
			theByteValue 	= pBytes;
			theLongValue	= pLong;
		}
		
		/**
		 * Obtain the name of the property
		 * @return the name of the property
		 */
		private String getName() { return theName; }
		
		/**
		 * Obtain the byte value of the property
		 * @return the value of the property
		 */
		private byte[] 	getByteValue() { return theByteValue; }
					
		/**
		 * Obtain the byte value of the property
		 * @return the value of the property
		 */
		private long 	getLongValue() { return theLongValue; }
					
		/**
		 * Obtain the next property
		 * @return the next property
		 */
		private property getNext() { return theNext; }
		
		/**
		 * Set the byte value
		 * @param pValue the new value
		 */
		private void setByteValue(byte[] pValue) { 
			theByteValue = Arrays.copyOf(pValue, pValue.length); }
		
		/**
		 * Set the long value
		 * @param pValue the new value
		 */
		private void setLongValue(long pValue) { 
			theLongValue = pValue; }
		
		/**
		 * Set the next property
		 * @param pProperty the next property
		 */
		private void setNext(property pProperty) { theNext = pProperty; }

		/**
		 * compare this property to another
		 * @param pThat the property to compare against
		 * @return (-1,0,1) depending on order 
		 */
		private int compareTo(property pThat) {
			 int 	  iDiff;
		 
			 /* Handle differences in name */
			 if ((iDiff = theName.compareTo(pThat.getName())) != 0) return iDiff;
			 
			 /* Handle differences in long value */
			 if (theLongValue != pThat.getLongValue()) 
				 return (theLongValue < pThat.getLongValue()) ? -1 : 1;
			 
			 /* If we are identical return 0 */
			 if (theByteValue == pThat.getByteValue()) 	return 0;
			 if (theByteValue == null) 					return -1;
			 if (pThat.getByteValue() == null) 			return 1;

			 /* Handle non-equal arrays can't get order he-hum */
			 if (!Arrays.equals(theByteValue, pThat.getByteValue())) return 1;
			 
			 /* Return OK */
			 return 0;
		 }
	}
}
