package uk.co.tolcroft.models;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.util.Arrays;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.security.SecurityControl;

public class Utils {
	/**
	 * Determine whether two String objects differ.
	 * 
	 * @param pCurr The current string 
	 * @param pNew The new string
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(String pCurr, String pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two Integer objects differ.
	 * 
	 * @param pCurr The current integer 
	 * @param pNew The new integer
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(Integer pCurr, Integer pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two char array objects differ.
	 * 
	 * @param pCurr The current array 
	 * @param pNew The new array
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(char[] pCurr, char[] pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (!Arrays.equals(pCurr, pNew)))));
	}
	
	/**
	 * Determine whether two byte array objects differ.
	 * 
	 * @param pCurr The current array 
	 * @param pNew The new array
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(byte[] pCurr, byte[] pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (!Arrays.equals(pCurr, pNew)))));
	}
	
	/**
	 * format a byte array as a hexadecimal string
	 * @param pBytes the byte array
	 * @return the string
	 */
	public static String HexStringFromBytes(byte[] pBytes) {
		int				myInt;
		int				myDigit;
		char			myChar;
		StringBuilder	myValue;
		
		/* Allocate the string builder */
		myValue = new StringBuilder(2 * pBytes.length);
		
		/* For each byte in the value */
		for (Byte b : pBytes) {
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
		
		/* Return the string */
		return myValue.toString();
	}
	
	/**
	 * format a long as a hexadecimal string
	 * @param pValue the long value
	 * @return the string
	 */
	public static String HexStringFromLong(long pValue) {
		int				myDigit;
		char			myChar;
		long			myLong;
		StringBuilder	myValue;
		
		/* Access the long value */
		myLong = pValue;
		
		/* Allocate the string builder */
		myValue = new StringBuilder();
		
		/* handle negative values */
		boolean isNegative = (myLong < 0);
		if (isNegative) myLong = -myLong;
		
		/* Special case for zero */
		if (myLong == 0) myValue.append('0');
		
		/* else need to loop through the digits */
		else {
			/* While we have digits to format */
			while (myLong > 0) {
				/* Access the digit and move to next one */
				myDigit = (int)(myLong % 16);
				myChar  = (char)((myDigit > 9) ? ('a' + (myDigit-10)) : ('0' + myDigit));
				myValue.insert(0, myChar);
				myLong  /= 16;
			}
			
			/* Reinstate negative sign */
			if (isNegative) myValue.insert(0, '-');
		}	
		
		/* Return the string */
		return myValue.toString();
	}
	
	/**
	 * parse a byte array from a hexadecimal string
	 * @param pHexString the hex string
	 * @return the bytes
	 */
	public static byte[] BytesFromHexString(String pHexString) throws Exception {
		byte[]	myByteValue;
		char	myChar;
		int		myInt;
		int		myLen;
		
		/* Access the length of the hex string */
		myLen = pHexString.length();
		
		/* Check that it has an even length */
		if ((myLen % 2) != 0) 
			throw new Exception(ExceptionClass.DATA,
								"Invalid HexString Length: " + pHexString);
		
		/* Allocate the new bytes array */
		myByteValue = new byte[myLen / 2];
	
		/* Loop through the string */
		for (int i=0; i < myLen; i+=2) {
			/* Access the top level byte */
			myChar = pHexString.charAt(i);
			myInt  = 16 * ((myChar >= 'a') ? 10 + (myChar - 'a') : (myChar - '0'));
	
			/* Check that the char is a valid hex digit */
			if (!Character.isDigit(myChar) && ((myChar < 'a') || (myChar > 'f'))) 
				throw new Exception(ExceptionClass.DATA,
									"Non Hexadecimal Value: " + pHexString);
			
			/* Access the second byte */
			myChar  = pHexString.charAt(i+1);
			myInt  += ((myChar >= 'a') ? 10 + (myChar - 'a') : (myChar - '0'));
				
			/* Check that the char is a valid hex digit */
			if (!Character.isDigit(myChar) && ((myChar < 'a') || (myChar > 'f'))) 
				throw new Exception(ExceptionClass.DATA,
									"Non Hexadecimal Value: " + pHexString);
			
			/* Convert to byte and store */
			if (myInt > 127) myInt -= 256;
			myByteValue[i/2] = (byte)myInt;
		}
		
		/* Return the bytes */
		return myByteValue;
	}

	/**
	 * parse a long from a hexadecimal string
	 * @param pHexString the hex string
	 * @return the bytes
	 */
	public static long LongFromHexString(String pHexString) throws Exception {
		int		myLen;
		char 	myChar;
		long	myValue = 0;

		/* Access the length of the hex string */
		myLen = pHexString.length();
		
		/* handle negative values */
		boolean isNegative = ((myLen > 0) && (pHexString.charAt(0) == '-'));
		if (isNegative) { pHexString = pHexString.substring(1); myLen--; }
		
		/* Check that it has an even length */
		if ((myLen % 2) != 0) 
			throw new Exception(ExceptionClass.DATA,
								"Invalid HexString Length: " + pHexString);
		
 		/* Loop through the string */
 		for (int i=0; i<myLen; i++) {
			/* Access the next character */
			myChar = pHexString.charAt(i);
 				
			/* Check that the char is a valid hex digit */
			if (!Character.isDigit(myChar) && ((myChar < 'a') || (myChar > 'f'))) 
				throw new Exception(ExceptionClass.DATA,
							  		"Non Hexadecimal Value: " + pHexString);
				
			/* Add into the value */
			myValue *= 16;
			myValue += ((myChar >= 'a') ? 10 + (myChar - 'a') : (myChar - '0'));
 		}
		
 		/* Reinstate negative values */
 		if (isNegative) myValue = -myValue;
 		
 		/* Return the value */
 		return myValue;
	}

	/**
	 * Convert character array to byte array
	 * @param pChars the character array
	 * @return the byte array
	 */
	public static byte[] charToByteArray(char[] pChars) throws Exception {
		/* protect against exceptions */
		try {
			/* Transform the character array to a byte array */
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStreamWriter out = new OutputStreamWriter(baos, SecurityControl.ENCODING);
			for(int ch : pChars) { out.write(ch); }
			out.flush();
			return baos.toByteArray();
		}
		catch (Throwable e) {
			throw new Exception(ExceptionClass.DATA,
								"Unable to convert character array to bytes");
		}
	}

	/**
	 * Convert character array to byte array
	 * @param pChars the character array
	 * @return the byte array
	 */
	public	static char[] byteToCharArray(byte[] pBytes) throws Exception {
		/* protect against exceptions */
		try {			
			/* Allocate the character array allowing for one character per byte */
			char[] myArray = new char[pBytes.length];
			int myLen;
			
			/* Transform the byte array to a character array */
			ByteArrayInputStream bais = new ByteArrayInputStream(pBytes);
			InputStreamReader in = new InputStreamReader(bais, SecurityControl.ENCODING);
			myLen = in.read(myArray);
			
			/* Cut down the array to the actual length */
			myArray = java.util.Arrays.copyOf(myArray, myLen);
			
			/* Return the array */
			return myArray;
		}
		catch (Throwable e) {
			throw new Exception(ExceptionClass.DATA,
								"Unable to convert byte array to characters");
		}
	}
	
	/**
	 * parse a long from a byte array
	 * @param pBytes the eight byte array holding the long
	 * @return the long value
	 */
	public static long LongFromBytes(byte[] pBytes) {
		int 	myByte;
		long	myValue = 0;

 		/* Loop through the bytes */
 		for (int i=0; i<8; i++) {
			/* Access the next byte as an unsigned integer */
			myByte = pBytes[i];
			myByte &= 255;

			/* Add in to value */
			myValue *= 256;
			myValue += myByte;
 		}
		
 		/* Return the value */
 		return myValue;
	}

	/**
	 * build a byte array from a long
	 * @param pBytes the eight byte array holding the long
	 * @return the long value
	 */
	public static byte[] BytesFromLong(long pValue) {
		byte 	myByte;
		byte[]	myBytes = new byte[8];
		long	myValue = pValue;

 		/* Loop through the bytes */
 		for (int i=8; i>0; i--) {
			/* Access the next byte as an unsigned integer */
			myByte = (byte)(myValue & 255);
			myBytes[i-1] = myByte;
			
			/* Adjust value */
			myValue /= 256;
 		}
		
 		/* Return the value */
 		return myBytes;
	}
}
