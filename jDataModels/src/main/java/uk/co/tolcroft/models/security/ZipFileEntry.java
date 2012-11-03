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
package uk.co.tolcroft.models.security;

import java.util.Arrays;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;

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
	 * The Digest property name of a file
	 */
	protected final static String propDigest		= "Digest";
	
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
	 * The next file in the list
	 */
	private ZipFileEntry 		theNext 			= null;

	/**
	 * The property list
	 */
	private property 			theProperties		= null;

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
	 * Obtain the mode of the file 
	 * @return the mode of the file
	 */
	public ZipEntryMode	getFileMode() throws ModelException 			
		{ return new ZipEntryMode(getLongProperty(propName)); }
	
	/**
	 * Obtain data length at index
	 * @param iIndex the digest index 
	 * @return the data length at index
	 */
	public long 		getDigestLen(int iIndex) 	{ return getLongProperty(propDigest + iIndex); }
	
	/**
	 * Obtain the data digest at index 
	 * @param iIndex the digest index 
	 * @return the data digest at index
	 */
	public byte[] 		getDigest(int iIndex) 		{ return getByteProperty(propDigest + iIndex); }
	
	/**
	 * Obtain the initialisation vector at index 
	 * @param iIndex the IV index 
	 * @return the IV at index
	 */
	public byte[] 		getInitVector(int iIndex) 	{ return getByteProperty(propInitVector + iIndex); }
	
	/**
	 * Obtain the secret key at index 
	 * @param iIndex the Secret key index 
	 * @return the secret key for the file
	 */
	public byte[] 		getSecretKey(int iIndex) 	{ return getByteProperty(propSecretKey + iIndex); }
	
	/**
	 * Obtain the signature for the file 
	 * @return the signature for the file
	 */
	public byte[] 		getSignature() 			{ return getByteProperty(propSignature); }

	/**
	 * Standard constructor 
	 */
	protected ZipFileEntry() {}
	
	/**
	 * Construct encryption properties from encoded string
	 * @param pCodedString the encoded properties 
	 */
	protected ZipFileEntry(String pCodedString) throws ModelException {
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
	}
	
	/**
	 * Obtain the bytes value of the named property
	 * @param pName the name of the property
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
	 * @param pName the name of the property
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
	 * @param pName the name of the property
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
	 * Set the named property at index
	 * @param pName the name of the property
	 * @param iIndex the index of the property
	 * @param pValue the Value of the property
	 */
	protected void setProperty(String pName, int iIndex, byte[] pValue) {
		setProperty(pName + iIndex, pValue);
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
	 * @param iIndex the index of the property
	 * @param pValue the Value of the property
	 */
	protected void setProperty(String pName, int iIndex, long pValue) {
		setProperty(pName + iIndex, pValue);		
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
	private void parseEncodedProperty(String pValue) throws ModelException {
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
			throw new ModelException(ExceptionClass.DATA,
								"Missing value separator: " + pValue);

		/* Split the values and name */
		myName 	= pValue.substring(0, myLoc);
		myBytes = pValue.substring(myLoc+1);
		myLen   = myBytes.length();
		
		/* If the name is already present reject it */
		if (getProperty(myName) != null) 
			throw new ModelException(ExceptionClass.DATA,
								"Duplicate name: " + pValue);
		
		/* Locate the Long separator in the string */
		myLoc = myBytes.indexOf(theLongSeparator);
		
		/* Check that we found the long separator */
		if (myLoc == -1) 
			throw new ModelException(ExceptionClass.DATA,
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
				throw new ModelException(ExceptionClass.DATA,
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
					throw new ModelException(ExceptionClass.DATA,
										"Non Hexadecimal Bytes Value: " + pValue);
				
				/* Access the second byte */
				myChar = myBytes.charAt(i+1);
				myInt  += ((myChar >= 'a') ? 10 + (myChar - 'a') : (myChar - '0'));
					
				/* Check that the char is a valid hex digit */
				if (!Character.isDigit(myChar) && ((myChar < 'a') || (myChar > 'f'))) 
					throw new ModelException(ExceptionClass.DATA,
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
					throw new ModelException(ExceptionClass.DATA,
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
