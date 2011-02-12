package uk.co.tolcroft.models;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.util.Arrays;

import uk.co.tolcroft.finance.data.*;
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
	 * Determine whether two {@link Rate} objects differ.
	 * 
	 * @param pCurr The current Rate 
	 * @param pNew The new Rate
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(Number.Rate pCurr, Number.Rate pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link Money} objects differ.
	 * 
	 * @param pCurr The current Money 
	 * @param pNew The new Money
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(Number.Money pCurr, Number.Money pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link Price} objects differ.
	 * 
	 * @param pCurr The current Price 
	 * @param pNew The new Price
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(Number.Price pCurr, Number.Price pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link DilutedPrice} objects differ.
	 * 
	 * @param pCurr The current DilutedPrice 
	 * @param pNew The new DilutedPrice
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(Number.DilutedPrice pCurr, Number.DilutedPrice pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link Dilution} objects differ.
	 * 
	 * @param pCurr The current Dilution
	 * @param pNew The new Dilution
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(Number.Dilution pCurr, Number.Dilution pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link Units} objects differ.
	 * 
	 * @param pCurr The current Units 
	 * @param pNew The new Units
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(Number.Units pCurr, Number.Units pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link Date} objects differ.
	 * 
	 * @param pCurr The current Date 
	 * @param pNew The new Date
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(Date pCurr, Date pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link TransactionType} objects differ.
	 * 
	 * @param pCurr The current TransType 
	 * @param pNew The new TransType
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(TransactionType pCurr, TransactionType pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link TaxType} objects differ.
	 * 
	 * @param pCurr The current TaxType 
	 * @param pNew The new TaxType
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(TaxType pCurr, TaxType pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link AccountType} objects differ.
	 * 
	 * @param pCurr The current AccountType 
	 * @param pNew The new AccountType
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(AccountType pCurr, AccountType pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link TaxRegime} objects differ.
	 * 
	 * @param pCurr The current TransRegime
	 * @param pNew The new TransRegime
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(TaxRegime pCurr, TaxRegime pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link Frequency} objects differ.
	 * 
	 * @param pCurr The current Frequency 
	 * @param pNew The new Frequency
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(Frequency pCurr, Frequency pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link account} objects differ.
	 * 
	 * @param pCurr The current Account 
	 * @param pNew The new Account
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(Account pCurr, Account pNew) {
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
	 * Format a Rate 
	 * 
	 * @param pRate the rate to format
	 * @return the formatted Rate
	 */
	public static String formatRate(Number.Rate pRate) {
		String 	myFormat;
		myFormat = (pRate != null) ? pRate.format(false)
								   : "null";
		return myFormat;
	}

	/**
	 * Format a Money 
	 * 
	 * @param pMoney the money to format
	 * @return the formatted Money
	 */
	public static String formatMoney(Number.Money pMoney) {
		String 	myFormat;
		myFormat = (pMoney != null) ? pMoney.format(false)
								    : "null";
		return myFormat;
	}

	/**
	 * Format a Price 
	 * 
	 * @param pPrice the price to format
	 * @return the formatted Price
	 */
	public static String formatPrice(Number.Price pPrice) {
		String 	myFormat;
		myFormat = (pPrice != null) ? pPrice.format(false)
								   : "null";
		return myFormat;
	}

	/**
	 * Format a DilutedPrice 
	 * 
	 * @param pPrice the price to format
	 * @return the formatted Price
	 */
	public static String formatDilutedPrice(Number.DilutedPrice pPrice) {
		String 	myFormat;
		myFormat = (pPrice != null) ? pPrice.format(false)
								    : "null";
		return myFormat;
	}

	/**
	 * Format a Dilution 
	 * 
	 * @param pDilution the dilution to format
	 * @return the formatted Dilution
	 */
	public static String formatDilution(Number.Dilution pDilution) {
		String 	myFormat;
		myFormat = (pDilution != null) ? pDilution.format(false)
								       : "null";
		return myFormat;
	}

	/**
	 * Format a Units 
	 * 
	 * @param pUnits the units to format
	 * @return the formatted Units
	 */
	public static String formatUnits(Number.Units pUnits) {
		String 	myFormat;
		myFormat = (pUnits != null) ? pUnits.format(false)
								    : "null";
		return myFormat;
	}

	/**
	 * Format a Date 
	 * 
	 * @param pDate the date to format
	 * @return the formatted Date
	 */
	public static String formatDate(Date pDate) {
		String 	myFormat;
		myFormat = (pDate != null) ? pDate.formatDate(false)
							       : "null";
		return myFormat;
	}

	/**
	 * Format a TransType 
	 * 
	 * @param pTrans the transtype to format
	 * @return the formatted transtype
	 */
	public static String formatTrans(TransactionType pTrans) {
		String 	myFormat;
		myFormat = (pTrans != null) ? pTrans.getName()
								    : "null";
		return myFormat;
	}

	/**
	 * Format a TaxType 
	 * 
	 * @param pTaxType the taxtype to format
	 * @return the formatted taxtype
	 */
	public static String formatTaxType(TaxType pTaxType) {
		String 	myFormat;
		myFormat = (pTaxType != null) ? pTaxType.getName()
								      : "null";
		return myFormat;
	}

	/**
	 * Format an Account 
	 * 
	 * @param pAccount the account to format
	 * @return the formatted account
	 */
	public static String formatAccount(Account pAccount) {
		String 	myFormat;
		myFormat = (pAccount != null) ? pAccount.getName()
							 	      : "null";
		return myFormat;
	}

	/**
	 * Format an AccountType 
	 * 
	 * @param pActType the account type to format
	 * @return the formatted account type
	 */
	public static String formatAccountType(AccountType pActType) {
		String 	myFormat;
		myFormat = (pActType != null) ? pActType.getName()
							 	      : "null";
		return myFormat;
	}

	/**
	 * Format an TaxRegime 
	 * 
	 * @param pRegime the tax regime to format
	 * @return the formatted tax regime
	 */
	public static String formatRegime(TaxRegime pRegime) {
		String 	myFormat;
		myFormat = (pRegime != null) ? pRegime.getName()
							      	 : "null";
		return myFormat;
	}

	/**
	 * Format a Frequency 
	 * 
	 * @param pFreq the frequency to format
	 * @return the formatted frequency
	 */
	public static String formatFreq(Frequency pFreq) {
		String 	myFormat;
		myFormat = (pFreq != null) ? pFreq.getName()
							       : "null";
		return myFormat;
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
			/* Initialise the hash value as the UTF-8 version of the password */
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
			
			/* Initialise the hash value as the UTF-8 version of the password */
			ByteArrayInputStream bais = new ByteArrayInputStream(pBytes);
			InputStreamReader in = new InputStreamReader(bais, SecurityControl.ENCODING);
			in.read(myArray);
			return myArray;
		}
		catch (Throwable e) {
			throw new Exception(ExceptionClass.DATA,
								"Unable to convert byte array to characters");
		}
	}
}
