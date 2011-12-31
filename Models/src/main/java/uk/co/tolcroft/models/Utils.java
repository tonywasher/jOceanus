package uk.co.tolcroft.models;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Arrays;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.*;
import uk.co.tolcroft.models.security.SecurityControl;

public class Utils {
	/**
	 * Determine whether two Generic objects differ.
	 * @param pCurr The current object 
	 * @param pNew The new object
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static Difference differs(Object pCurr, Object pNew) {
		/* Handle case where current value is null */
		if  (pCurr == null) return (pNew != null) ? Difference.Different 
												  : Difference.Identical;
		
		/* Handle case where new value is null */
		if  (pNew == null) return Difference.Different;
		
		/* Handle char/byte arrays separately */
		if ((pCurr instanceof char[]) && (pNew instanceof char[]))
			return differs((char[])pCurr, (char[])pNew);
		if ((pCurr instanceof byte[]) && (pNew instanceof byte[]))
			return differs((byte[])pCurr, (byte[])pNew);
		
		/* Handle Standard cases */
		return (pCurr.equals(pNew)) ? Difference.Identical
									: Difference.Different;
	}

	/**
	 * Determine whether two String objects differ.
	 * @param pCurr The current string 
	 * @param pNew The new string
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static Difference differs(String pCurr, String pNew) {
		/* Handle case where current value is null */
		if  (pCurr == null) return (pNew != null) ? Difference.Different 
												  : Difference.Identical;
		
		/* Handle case where new value is null */
		if  (pNew == null) return Difference.Different;
		
		/* Handle Standard cases */
		return (pCurr.compareTo(pNew) != 0) ? Difference.Different
											: Difference.Identical;
	}

	/**
	 * Determine whether two Integer objects differ.
	 * @param pCurr The current integer 
	 * @param pNew The new integer
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static Difference differs(Integer pCurr, Integer pNew) {
		/* Handle case where current value is null */
		if  (pCurr == null) return (pNew != null) ? Difference.Different 
												  : Difference.Identical;
		
		/* Handle case where new value is null */
		if  (pNew == null) return Difference.Different;
		
		/* Handle Standard cases */
		return (pCurr.compareTo(pNew) != 0) ? Difference.Different
											: Difference.Identical;
	}

	/**
	 * Determine whether two char array objects differ.
	 * @param pCurr The current array 
	 * @param pNew The new array
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static Difference differs(char[] pCurr, char[] pNew) {
		/* Handle case where current value is null */
		if  (pCurr == null) return (pNew != null) ? Difference.Different 
												  : Difference.Identical;
		
		/* Handle case where new value is null */
		if  (pNew == null) return Difference.Different;
		
		/* Handle Standard cases */
		return (!Arrays.equals(pCurr, pNew)) ? Difference.Different
											 : Difference.Identical;
	}
	
	/**
	 * Determine whether two byte array objects differ. 
	 * @param pCurr The current array 
	 * @param pNew The new array
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static Difference differs(byte[] pCurr, byte[] pNew) {
		/* Handle case where current value is null */
		if  (pCurr == null) return (pNew != null) ? Difference.Different 
												  : Difference.Identical;
		
		/* Handle case where new value is null */
		if  (pNew == null) return Difference.Different;
		
		/* Handle Standard cases */
		return (!Arrays.equals(pCurr, pNew)) ? Difference.Different
											 : Difference.Identical;
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
				myLong  >>= 4;
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
	public static byte[] BytesFromHexString(String pHexString) throws ModelException {
		byte[]	myByteValue;
		char	myChar;
		int		myInt;
		int		myLen;
		
		/* Access the length of the hex string */
		myLen = pHexString.length();
		
		/* Check that it has an even length */
		if ((myLen % 2) != 0) 
			throw new ModelException(ExceptionClass.DATA,
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
				throw new ModelException(ExceptionClass.DATA,
									"Non Hexadecimal Value: " + pHexString);
			
			/* Access the second byte */
			myChar  = pHexString.charAt(i+1);
			myInt  += ((myChar >= 'a') ? 10 + (myChar - 'a') : (myChar - '0'));
				
			/* Check that the char is a valid hex digit */
			if (!Character.isDigit(myChar) && ((myChar < 'a') || (myChar > 'f'))) 
				throw new ModelException(ExceptionClass.DATA,
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
	public static long LongFromHexString(String pHexString) throws ModelException {
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
			throw new ModelException(ExceptionClass.DATA,
								"Invalid HexString Length: " + pHexString);
		
 		/* Loop through the string */
 		for (int i=0; i<myLen; i++) {
			/* Access the next character */
			myChar = pHexString.charAt(i);
 				
			/* Check that the char is a valid hex digit */
			if (!Character.isDigit(myChar) && ((myChar < 'a') || (myChar > 'f'))) 
				throw new ModelException(ExceptionClass.DATA,
							  		"Non Hexadecimal Value: " + pHexString);
				
			/* Add into the value */
			myValue <<= 4;
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
	public static byte[] charToByteArray(char[] pChars) throws ModelException {
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
			throw new ModelException(ExceptionClass.DATA,
								"Unable to convert character array to bytes");
		}
	}

	/**
	 * Convert byte array to character array
	 * @param pBytes the byte array
	 * @return the character array
	 */
	public	static char[] byteToCharArray(byte[] pBytes) throws ModelException {
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
			throw new ModelException(ExceptionClass.DATA,
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
			myValue <<= 8;
			myValue += myByte;
 		}
		
 		/* Return the value */
 		return myValue;
	}

	/**
	 * build a byte array from a long
	 * @param pValue the long value to convert
	 * @return the byte array
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
			myValue >>= 8;
 		}
		
 		/* Return the value */
 		return myBytes;
	}
	
	/**
	 * parse a long from a byte array
	 * @param pBytes the eight byte array holding the long
	 * @return the long value
	 */
	public static int IntegerFromBytes(byte[] pBytes) {
		int 	myByte;
		int		myValue = 0;

 		/* Loop through the bytes */
 		for (int i=0; i<4; i++) {
			/* Access the next byte as an unsigned integer */
			myByte = pBytes[i];
			myByte &= 255;

			/* Add in to value */
			myValue <<= 8;
			myValue += myByte;
 		}
		
 		/* Return the value */
 		return myValue;
	}

	/**
	 * build a byte array from an integer
	 * @param pValue the integer value to convert
	 * @return the byte array
	 */
	public static byte[] BytesFromInteger(int pValue) {
		byte 	myByte;
		byte[]	myBytes = new byte[4];
		int		myValue = pValue;

 		/* Loop through the bytes */
 		for (int i=4; i>0; i--) {
			/* Access the next byte as an unsigned integer */
			myByte = (byte)(myValue & 255);
			myBytes[i-1] = myByte;
			
			/* Adjust value */
			myValue >>= 8;
 		}
		
 		/* Return the value */
 		return myBytes;
	}

	/**
	 * Compare two byte arrays for sort order
	 * @param pCurr The current array 
	 * @param pNew The new array
	 */
	public static int compareTo(byte[] pCurr, byte[] pNew) {
		/* Handle trivial cases */
		if (pCurr == pNew) return 0;
		if (pCurr == null) return 1;
		if (pNew  == null) return -1;
		
		/* Wrap as Byte Buffers */
		ByteBuffer myCurr = ByteBuffer.wrap(pCurr); 
		ByteBuffer myNew  = ByteBuffer.wrap(pNew);
		
		/* Compare the two */
		return myCurr.compareTo(myNew);
	}
	
	/**
	 * Obtain character as a string
	 * @param pChar the character to be represented as a string
	 */
	public static String getCharAsString(char pChar) {
		/* Build a new string from the character */
		char[] myArray = new char[1];
		myArray[0] = pChar;
		return new String(myArray);
	}
}
