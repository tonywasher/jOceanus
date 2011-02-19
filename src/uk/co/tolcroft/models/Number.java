package uk.co.tolcroft.models;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

/**
 * Represents the underlying number class.
 */	
public class Number {
	/**
	 * The number of decimals for this object.
	 */	
	private int     	theDecimals = 0;
	
	/**
	 * The value of the object multiplied by 10 to the power of {@link #theDecimals}.
	 * This allows multiplication etc to be performed on integral values rather than the inaccurate
	 * method of using doubles
	 */	
	private long    	theValue    = 0;
	
	/**
	 * The Number class of the object
	 */
	private NumberClass theClass    = NumberClass.STANDARD;
	
	/**
	 * Access the value of the object 
	 * 
	 * @return the value
	 */
	public    long  getValue()            { return theValue; }

	/**
	 * Set the value of the object
	 * 
	 * @param uValue the value of the object
	 */
	private   void  setValue(long uValue) { theValue = uValue; }
	
	/**
	 * Construct a standard number 
	 * 
	 * @param uDecimals the number of decimals for the number.
	 */
	protected Number(int uDecimals) { theDecimals = uDecimals; }	

	/**
	 * Construct a specific number 
	 * 
	 * @param uDecimals the number of decimals for the number.
	 * @param pClass the class of the number
	 */
	protected Number(int uDecimals, NumberClass pClass) {
		theDecimals = uDecimals;
		theClass    = pClass;      
	}	

	/**
	 * Determine whether we have a non-zero value 
	 * 
	 * @return <code>true</code> if the value is non-zero, <code>false</code> otherwise.
	 */
	public boolean isNonZero() { return (theValue != 0); }
	
	/**
	 * Determine whether we have a positive (or zero) value 
	 * 
	 * @return <code>true</code> if the value is non-negative, <code>false</code> otherwise.
	 */
	public boolean isPositive() { return (theValue >= 0); }	

	/**
	 * Negate the value  
	 */
	public void negate() { theValue = -theValue;	}
	
	/**
	 * Set to zero value 
	 */
	public void setZero() { theValue = 0; }
	
	/**
	 * Convert a whole number value to include decimals 
	 * @param pValue the whole number value 
	 * @param pNumDec the number of decimals for this number type
	 * @return the converted value with added zeros 
	 */
	private static long convertToValue(long pValue, int pNumDecimals) { 
		/* Build in the decimals to the value */
		while (pNumDecimals-->0) pValue *= 10;       
		
		/* return the value */
		return pValue;
	}
	
	/**
	 * Add a number to the value 
	 * 
	 * @param pValue The number to add to this one.
	 */
	private void addValue(Number pValue) {
		theValue += pValue.theValue;
	}
	
	/**
	 * Subtract a number from the value 
	 * 
	 * @param pValue The number to subtract from this one.
	 */
	private void subtractValue(Number pValue) {
		theValue -= pValue.theValue;
	}
	
	/**
	 * Parse a string to set the value 
	 * 
	 * @param pString The string to parse.
	 */
	private void Parse(String pString) throws uk.co.tolcroft.models.Exception {
		int 			myLen 		= pString.length();
		StringBuilder	myWork;
		StringBuilder	myDecimals	= null;
		int	    		myPos;
		char    		myDigit    	= '0';
		char			myChar;
		boolean 		isNegative;
		
		/* Create a working copy */
		myWork = new StringBuilder(pString);

		/* Trim leading and trailing blanks */
		while ((myWork.length() > 0) && (Character.isWhitespace(myWork.charAt(0))))
			myWork.deleteCharAt(0);
		while (((myLen = myWork.length()) > 0) && (Character.isWhitespace(myWork.charAt(myLen-1))))
			myWork.deleteCharAt(myLen-1);
		
		/* If the value is negative, strip the leading minus sign */
		isNegative = (myWork.charAt(0) == '-');
		if (isNegative) myWork = myWork.deleteCharAt(0);
		
		/* If this is a rate, remove any % from the end of the string */
		myLen = myWork.length();
		if ((theClass == NumberClass.RATE) && (myWork.charAt(myLen-1) == '%'))
			myWork.deleteCharAt(myLen-1);
		
		/* If this is money, remove any £ from the beginning of the string */
		if ((theClass == NumberClass.MONEY) && (myWork.charAt(0) == '£'))
			myWork.deleteCharAt(0);

		/* If this is money, remove any $ from the beginning of the string */
		if ((theClass == NumberClass.MONEY) && (myWork.charAt(0) == '$'))
			myWork.deleteCharAt(0);

		/* Remove any commas from the value */
		while ((myPos = myWork.indexOf(",")) != -1)	myWork.deleteCharAt(myPos);
		
		/* Trim leading and trailing blanks */
		while ((myWork.length() > 0) && (Character.isWhitespace(myWork.charAt(0))))
			myWork.deleteCharAt(0);
		while (((myLen = myWork.length()) > 0) && (Character.isWhitespace(myWork.charAt(myLen-1))))
			myWork.deleteCharAt(myLen-1);
		
		/* Locate the decimal point if present */
		myPos = myWork.indexOf(".");
		
		/* If we have a decimal point */
		if (myPos != -1) {
			/* Split into the two parts being careful of a trailing decimal point */
			if ((myPos+1) < myLen) myDecimals = new StringBuilder(myWork.substring(myPos+1));
			myWork.setLength(myPos);
		}
		
 		/* Handle leading decimal point on value */
 		if (myWork.length() == 0) myWork.append("0");
 		
			/* Loop through the characters of the integer part of the value */
 		myLen = myWork.length();
 		for (int i=0; i<myLen; i++) {
			/* Access the next character */
			myChar = myWork.charAt(i);
 				
			/* Check that the char is a valid digit */
			if (!Character.isDigit(myChar)) 
				throw new Exception(ExceptionClass.DATA,
								    "Non Decimal Numeric Value: " + pString);
				
			/* Add into the value */
			theValue *= 10;
			theValue += (myChar - '0');
 		}
 		
		/* If we have a decimal part */
		if (myDecimals != null) {
			/* Extend the decimal token to correct number of decimals */
			while ((myLen = myDecimals.length()) < theDecimals)
				myDecimals.append('0');
			
			/* If we have too many decimals */
			if (myLen > theDecimals) {
				/* Extract most significant trailing digit and truncate the value */
				myDigit = myDecimals.charAt(theDecimals);
				myDecimals.setLength(theDecimals);
			}
			
 			/* Loop through the characters of the decimal part of the value */
	 		myLen = myDecimals.length();
	 		for (int i=0; i<myLen; i++) {
				/* Access the next character */
				myChar = myDecimals.charAt(i);
	 				
				/* Check that the char is a valid hex digit */
				if (!Character.isDigit(myChar)) 
					throw new Exception(ExceptionClass.DATA,
									    "Non Decimal Numeric Value: " + pString);
					
				/* Add into the value */
				theValue *= 10;
				theValue += (myChar - '0');
	 		}
	 		
	 		/* Round value according to most significant discarded decimal digit */
			if (myDigit >= '5') theValue++;
		}

		/* else we have no decimals */
		else {
			/* Raise to appropriate factor given by number of decimals */
			for (int i=0; i < theDecimals; i++)
				theValue *= 10;
		}
		
		/* If the value is negative, negate the number */
		if (isNegative) negate();
	}
	
	/**
	 * Format a numeric decimal value 
	 * 
	 * @param bPretty <code>true</code> if the value is to be formatted with thousands separator, 
	 * <code>false</code> otherwise
	 * @return the formatted value.
	 */
	private StringBuilder format(boolean bPretty) {
		StringBuilder	myString = new StringBuilder(20);
		StringBuilder	myBuild;
		String  		myDecimals;
		String			myWhole;
		String			myPart;
		int    			myLen;
		long   			myValue = theValue;
		boolean 		isNegative;
		
		/* handle negative values */
		isNegative = (theValue < 0);
		if (isNegative) myValue = -myValue;
		
		/* Special case for zero */
		if (myValue == 0) myString.append('0');
		
		/* else need to loop through the digits */
		else {
			/* While we have digits to format */
			while (myValue > 0) {
				/* Format the digit and move to next one */
				myString.insert(0, (char)('0' + (myValue % 10)));
				myValue /= 10;
			}
		}
		
		/* Add leading zeroes */
		while ((myLen = myString.length()) < (theDecimals+1))
			myString.insert(0, '0');
		
		/* Split into whole and decimal parts */
		myWhole 	= myString.substring(0, myLen - theDecimals);
		myDecimals 	= myString.substring(myLen - theDecimals);
		
		/* If this is a pretty format */
		if (bPretty){
			/* Initialise build */
			myBuild = new StringBuilder(20);
			
			/* Loop while we need to add commas */
			while ((myLen = myWhole.length()) > 3) {
				/* Split out the next part */
				myPart 	= myWhole.substring(myLen - 3);
				myWhole	= myWhole.substring(0, myLen - 3);
			
				/* Add existing build */
				if (myBuild.length() > 0) myBuild.insert(0, ',');
				myBuild.insert(0, myPart);
			}
			
			/* If we have added some commas */
			if (myBuild.length() > 0) {
				/* Access the full string */
				myBuild.insert(0, ',');
				myBuild.insert(0, myWhole);
				myWhole = myBuild.toString();
			}
		}
		
		/* Rebuild the number */
		myString.setLength(0);
		myString.append(myWhole);
		myString.append(".");
		myString.append(myDecimals);
		if (isNegative) myString.insert(0, '-');
		
		/* Return the string */
		return myString;
	}
	
	/**
	 * Compare this Number to another to establish sort order.
	 * 
	 * @param that The Number to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Number that) {
        if (this == that) return 0;
        if (theValue < that.theValue) return -1;
        if (theValue > that.theValue) return  1;
		return 0;
	}
	/**
	 * Class of a number for formatting purposes.
	 */	
	private enum NumberClass {
		/**
		 * Standard formatting for number
		 */
		STANDARD,
		
		/**
		 * Rate formatting (% at end) 
		 */
		RATE,
		
		/**
		 * Money formatting (£ at front)
		 */
		MONEY;
	}
	
	/**
	 * Represents a Rate object. 
	 */
	public static class Rate extends Number {
		/**
		 * Rates have two decimal points 
		 */
		public final static int NUMDEC = 2;
		
		/**
		 * Access the value of the Rate 
		 * 
		 * @return the value
		 */
		public long getRate() { return getValue(); }
		
		/**
		 * Construct a new Rate from a value 
		 * 
		 * @param uRate the value
		 */
		public Rate(long uRate) { super(NUMDEC, NumberClass.RATE); super.setValue(uRate); }	

		/**
		 * Construct a new Rate by copying another rate 
		 * 
		 * @param pRate the Rate to copy
		 */
		public Rate(Rate pRate) {
			super(NUMDEC, NumberClass.RATE);
			super.setValue(pRate.getRate()); 
		}
		
		/**
		 * Construct a new Rate by parsing a string value 
		 * 
		 * @param pRate the Rate to parse
		 */
		public Rate(String pRate) throws Exception {
			super(NUMDEC, NumberClass.RATE);
			super.Parse(pRate);
		}		

		/**
		 * Format a Rate 
		 * 
		 * @param bPretty <code>true</code> if the value is to be formatted with thousands separator
		 * and with a % sign at the end, <code>false</code> otherwise
		 * @return the formatted Rate
		 */
		public String format(boolean bPretty) {
			StringBuilder myFormat;
			myFormat = super.format(bPretty);
			if (bPretty) myFormat.append('%');
			return myFormat.toString();
		}

		/**
		 * Format a Rate 
		 * 
		 * @param pRate the rate to format
		 * @return the formatted Rate
		 */
		public static String format(Rate pRate) {
			String 	myFormat;
			myFormat = (pRate != null) ? pRate.format(false)
									   : "null";
			return myFormat;
		}

		/**
		 * Create a new Rate by parsing a string value 
		 * 
		 * @param pRate the Rate to parse
		 * @return the new Rate or <code>null</code> if parsing failed
		 */
		public static Rate Parse(String pRate) {
			Rate myRate;
			try {
				myRate = new Rate(pRate);
			}
			catch (Exception e) {
				myRate = null;
			}
			return myRate;
		}

		/**
		 * Determine whether two Rate objects differ.
		 * 
		 * @param pCurr The current Rate 
		 * @param pNew The new Rate
		 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
		 */	
		public static boolean differs(Rate pCurr, Rate pNew) {
			return (((pCurr == null) && (pNew != null)) ||
					((pCurr != null) && 
					 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
		}

		/**
		 * Convert a whole number value to include decimals 
		 * @param pValue the whole number value 
		 * @return the converted value with added zeros 
		 */
		public static long convertToValue(long pValue) { 
			/* Build in the decimals to the value */
			return convertToValue(pValue, NUMDEC);       
		}		
	}
	
	/**
	 * Represents a Price object. 
	 */
	public static class Price extends Number {
		/**
		 * Prices have four decimal points 
		 */
		public final static int NUMDEC = 4;

		/**
		 * Prices are formatted in pretty mode with a width of 10 characters 
		 */
		public final static int WIDTH  = 10;
		
		/**
		 * Access the value of the Price 
		 * 
		 * @return the value
		 */
		public long getPrice() { return getValue(); }
		
		/**
		 * Construct a new Price from a value 
		 * 
		 * @param uPrice the value
		 */
		public Price(long uPrice) { super(NUMDEC, NumberClass.MONEY); super.setValue(uPrice); }	

		/**
		 * Construct a new Price by copying another price 
		 * 
		 * @param pPrice the Price to copy
		 */
		public Price(Price pPrice) {
			super(NUMDEC, NumberClass.MONEY);
			super.setValue(pPrice.getPrice()); 
		}	

		/**
		 * Construct a new Price by parsing a string value 
		 * 
		 * @param pPrice the Price to parse
		 */
		public Price(String pPrice) throws Exception {
			super(NUMDEC, NumberClass.MONEY);
			super.Parse(pPrice);
		}		
		
		/**
		 * obtain a Diluted price 
		 * 
		 * @param pDilution the dilution factor
		 * @return the calculated value
		 */
		public DilutedPrice getDilutedPrice(Dilution pDilution) {
			DilutedPrice	myTotal;
			long    		myValue  = getPrice();
			long    		myPower  = NUMDEC + Dilution.NUMDEC - DilutedPrice.NUMDEC;
			long    		myFactor = 1;
			long    		myDigit;
	
			/* Calculate division factor (less one) */
			while (myPower-->1) myFactor  *= 10;       
			
			/* Calculate new value */
			myValue  *= pDilution.getDilution();
			myValue  /= myFactor;
			
			/* Access the last digit to allow rounding and complete the calculation */
			myDigit  = myValue % 10;
			myValue /= 10;
			if (myDigit >= 5) myValue++;
			
			/* Allocate value */
			myTotal = new DilutedPrice(myValue);
		
			/* Return value */
			return myTotal;
		}

		/**
		 * Format a Price 
		 * 
		 * @param bPretty <code>true</code> if the value is to be formatted with thousands separator
		 * and with a £ sign at the beginning, <code>false</code> otherwise
		 * @return the formatted Price
		 */
		public String format(boolean bPretty) {
			StringBuilder	myFormat;
			boolean 		isNegative;
			
			/* Format the value in a standard fashion */
			myFormat = super.format(bPretty);
			
			/* If we are in pretty mode */
			if (bPretty) {
				/* If the value is zero */
				if (!isNonZero()) {
					/* Provide special display */
					myFormat.setLength(0);
					myFormat.append("£      -   ");
				}
				
				/* Else non-zero value */
				else {
					/* If the value is negative, strip the leading minus sign */
					isNegative = (myFormat.charAt(0) == '-');
					if (isNegative) myFormat.deleteCharAt(0);
					
					/* Extend the value to the desired width */
					while ((myFormat.length()) < WIDTH)
						myFormat.insert(0, ' ');
					
					/* Add the pound sign */
					myFormat.insert(0, '£');
					
					/* Add back any minus sign */
					if (isNegative) myFormat.insert(0, '-');
				}
			}
			return myFormat.toString();
		}

		/**
		 * Create a new Price by parsing a string value 
		 * 
		 * @param pPrice the Price to parse
		 * @return the new Price or <code>null</code> if parsing failed
		 */
		public static Price Parse(String pPrice) {
			Price myPrice;
			try {
				myPrice = new Price(pPrice);
			}
			catch (Exception e) {
				myPrice = null;
			}
			return myPrice;
		}
		
		/**
		 * Format a Price 
		 * 
		 * @param pPrice the price to format
		 * @return the formatted Price
		 */
		public static String format(Price pPrice) {
			String 	myFormat;
			myFormat = (pPrice != null) ? pPrice.format(false)
									   : "null";
			return myFormat;
		}

		/**
		 * Determine whether two Price objects differ.
		 * 
		 * @param pCurr The current Price 
		 * @param pNew The new Price
		 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
		 */	
		public static boolean differs(Price pCurr, Price pNew) {
			return (((pCurr == null) && (pNew != null)) ||
					((pCurr != null) && 
					 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
		}
	}
	
	/**
	 * Represents a Diluted Price object. 
	 */
	public static class DilutedPrice extends Number {
		/**
		 * DilutedPrices have six decimal points 
		 */
		public final static int NUMDEC = 6;

		/**
		 * Prices are formatted in pretty mode with a width of 12 characters 
		 */
		public final static int WIDTH  = 12;
		
		/**
		 * Access the value of the Price 
		 * 
		 * @return the value
		 */
		public long getDilutedPrice() { return getValue(); }
		
		/**
		 * Construct a new DilutedPrice from a value 
		 * 
		 * @param uPrice the value
		 */
		public DilutedPrice(long uPrice) { super(NUMDEC, NumberClass.MONEY); super.setValue(uPrice); }	

		/**
		 * Construct a new DilutedPrice by copying another price 
		 * 
		 * @param pPrice the Price to copy
		 */
		public DilutedPrice(DilutedPrice pPrice) {
			super(NUMDEC, NumberClass.MONEY);
			super.setValue(pPrice.getDilutedPrice()); 
		}	

		/**
		 * Construct a new DilutedPrice by parsing a string value 
		 * 
		 * @param pPrice the Price to parse
		 */
		public DilutedPrice(String pPrice) throws Exception {
			super(NUMDEC, NumberClass.MONEY);
			super.Parse(pPrice);
		}		
		
		/**
		 * obtain a base price 
		 * 
		 * @param pDilution the dilution factor
		 * @return the calculated value
		 */
		public Price getPrice(Dilution pDilution) {
			Price	myTotal;
			long    myValue  = getDilutedPrice();
			long    myPower  = NUMDEC + DilutedPrice.NUMDEC - Dilution.NUMDEC;
			long    myFactor = 1;
			long    myDigit;
	
			/* Calculate division factor (minus one) */
			while (myPower-->1) myFactor  *= 10;       
			
			/* Calculate new value */
			myValue  *= myFactor;
			myValue  /= pDilution.getDilution();
			
			/* Access the last digit to allow rounding and complete the calculation */
			myDigit  = myValue % 10;
			myValue /= 10;
			if (myDigit >= 5) myValue++;
			
			/* Allocate value */
			myTotal = new Price(myValue);
		
			/* Return value */
			return myTotal;
		}

		/**
		 * Format a Price 
		 * 
		 * @param bPretty <code>true</code> if the value is to be formatted with thousands separator
		 * and with a £ sign at the beginning, <code>false</code> otherwise
		 * @return the formatted Price
		 */
		public String format(boolean bPretty) {
			StringBuilder	myFormat;
			boolean 		isNegative;
			
			/* Format the value in a standard fashion */
			myFormat = super.format(bPretty);
			
			/* If we are in pretty mode */
			if (bPretty) {
				/* If the value is zero */
				if (!isNonZero()) {
					/* Provide special display */
					myFormat.setLength(0);
					myFormat.append("£      -     ");
				}
				
				/* Else non-zero value */
				else {
					/* If the value is negative, strip the leading minus sign */
					isNegative = (myFormat.charAt(0) == '-');
					if (isNegative) myFormat.deleteCharAt(0);
					
					/* Extend the value to the desired width */
					while ((myFormat.length()) < WIDTH)
						myFormat.insert(0, ' ');
					
					/* Add the pound sign */
					myFormat.insert(0, '£');
					
					/* Add back any minus sign */
					if (isNegative) myFormat.insert(0, '-');
				}
			}
			return myFormat.toString();
		}

		/**
		 * Create a new Price by parsing a string value 
		 * 
		 * @param pPrice the Price to parse
		 * @return the new Price or <code>null</code> if parsing failed
		 */
		public static DilutedPrice Parse(String pPrice) {
			DilutedPrice myPrice;
			try {
				myPrice = new DilutedPrice(pPrice);
			}
			catch (Exception e) {
				myPrice = null;
			}
			return myPrice;
		}

		/**
		 * Format a DilutedPrice 
		 * 
		 * @param pPrice the price to format
		 * @return the formatted Price
		 */
		public static String format(DilutedPrice pPrice) {
			String 	myFormat;
			myFormat = (pPrice != null) ? pPrice.format(false)
									    : "null";
			return myFormat;
		}

		/**
		 * Determine whether two DilutedPrice objects differ.
		 * 
		 * @param pCurr The current DilutedPrice 
		 * @param pNew The new DilutedPrice
		 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
		 */	
		public static boolean differs(DilutedPrice pCurr, DilutedPrice pNew) {
			return (((pCurr == null) && (pNew != null)) ||
					((pCurr != null) && 
					 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
		}
	}
	
	/**
	 * Represents a Units object. 
	 */
	public static class Units extends Number {
		/**
		 * Units have four decimal points 
		 */
		public final static int NUMDEC = 4;
		
		/**
		 * Access the value of the Units 
		 * 
		 * @return the value
		 */
		public long getUnits() { return getValue(); }
		
		/**
		 * Add units to the value 
		 * 
		 * @param pUnits The units to add to this one.
		 */
		public void    addUnits(Units pUnits)      { super.addValue(pUnits); }

		/**
		 * Subtract units from the value 
		 * 
		 * @param pUnits The units to subtract from this one.
		 */
		public void    subtractUnits(Units pUnits) { super.subtractValue(pUnits); }
		
		/**
		 * Construct a new Units from a value 
		 * 
		 * @param uUnits the value
		 */
		public Units(long uUnits) { super(NUMDEC); super.setValue(uUnits); }	

		/**
		 * Construct a new Units by copying another units 
		 * 
		 * @param pUnits the Units to copy
		 */
		public Units(Units pUnits) {
			super(NUMDEC);
			super.setValue(pUnits.getUnits()); 
		}	

		/**
		 * Construct a new Units by parsing a string value 
		 * 
		 * @param pUnits the Units to parse
		 */
		public Units(String pUnits) throws Exception {
			super(NUMDEC);
			super.Parse(pUnits);
		}		
		
		/**
		 * Format a Units 
		 * 
		 * @param bPretty <code>true</code> if the value is to be formatted with thousands separator,
		 * <code>false</code> otherwise
		 * @return the formatted Units
		 */
		public String format(boolean bPretty) {
			StringBuilder	myFormat;
			myFormat = super.format(bPretty);
			return myFormat.toString();
		}
		
		/**
		 * calculate the value of these units at a given price 
		 * 
		 * @param pPrice the per unit price
		 * @return the calculated value
		 */
		public Money valueAtPrice(Price pPrice) {
			Money   myTotal;
			long    myValue  = getUnits();
			long    myPower  = NUMDEC + Price.NUMDEC - Money.NUMDEC;
			long    myFactor = 1;
			long    myDigit;
	
			/* Calculate division factor (less one) */
			while (myPower-->1) myFactor  *= 10;       
			
			/* Calculate new value */
			myValue  *= pPrice.getPrice();
			myValue  /= myFactor;
			
			/* Access the last digit to allow rounding and complete the calculation */
			myDigit  = myValue % 10;
			myValue /= 10;
			if (myDigit >= 5) myValue++;
			
			/* Allocate value */
			myTotal = new Money(myValue);
		
			/* Return value */
			return myTotal;
		}

		/**
		 * Create a new Units by parsing a string value 
		 * 
		 * @param pUnits the Units to parse
		 * @return the new Units or <code>null</code> if parsing failed
		 */
		public static Units Parse(String pUnits) {
			Units myUnits;
			try {
				myUnits = new Units(pUnits);
			}
			catch (Exception e) {
				myUnits = null;
			}
			return myUnits;
		}

		/**
		 * Format a Units 
		 * 
		 * @param pUnits the units to format
		 * @return the formatted Units
		 */
		public static String format(Units pUnits) {
			String 	myFormat;
			myFormat = (pUnits != null) ? pUnits.format(false)
									    : "null";
			return myFormat;
		}

		/**
		 * Determine whether two Units objects differ.
		 * 
		 * @param pCurr The current Units 
		 * @param pNew The new Units
		 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
		 */	
		public static boolean differs(Units pCurr, Units pNew) {
			return (((pCurr == null) && (pNew != null)) ||
					((pCurr != null) && 
					 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
		}
	}
	
	/**
	 * Represents a Dilution object. 
	 */
	public static class Dilution extends Number {
		/**
		 * Dilutions have six decimal points 
		 */
		public final static int NUMDEC = 6;
		
		/**
		 * Define the maximum dilution value 
		 */
		public final static int MAX_VALUE = 1000000;
		
		/**
		 * Define the minimum dilution value 
		 */
		public final static int MIN_VALUE = 0;
		
		/**
		 * Access the value of the Dilution
		 * 
		 * @return the value
		 */
		public long getDilution() { return getValue(); }
		
		/**
		 * Construct a new Dilution from a value 
		 * 
		 * @param uDilution the value
		 */
		public Dilution(long uDilution) { super(NUMDEC); super.setValue(uDilution); }	

		/**
		 * Construct a new Dilution by copying another dilution 
		 * 
		 * @param pDilution the Dilution to copy
		 */
		public Dilution(Dilution pDilution) {
			super(NUMDEC);
			super.setValue(pDilution.getDilution()); 
		}	

		/**
		 * Construct a new Dilution by parsing a string value 
		 * 
		 * @param pDilution the Dilution to parse
		 */
		public Dilution(String pDilution) throws Exception {
			super(NUMDEC);
			super.Parse(pDilution);
		}		
		
		/**
		 * obtain a further dilution 
		 * 
		 * @param pDilution the dilution factor
		 * @return the calculated value
		 */
		public Dilution getFurtherDilution(Dilution pDilution) {
			Dilution	myTotal;
			long    	myValue  = getDilution();
			long    	myPower  = NUMDEC + Dilution.NUMDEC - Dilution.NUMDEC;
			long    	myFactor = 1;
			long    	myDigit;
	
			/* Calculate division factor (less one) */
			while (myPower-->1) myFactor  *= 10;       
			
			/* Calculate new value */
			myValue  *= pDilution.getDilution();
			myValue  /= myFactor;
			
			/* Access the last digit to allow rounding and complete the calculation */
			myDigit  = myValue % 10;
			myValue /= 10;
			if (myDigit >= 5) myValue++;
			
			/* Allocate value */
			myTotal = new Dilution(myValue);
		
			/* Return value */
			return myTotal;
		}

		/**
		 * Is the dilution factor outside the valid range
		 * @return true/false
		 */
		public boolean outOfRange() {
			return ((getValue() > MAX_VALUE) ||
					(getValue() < MIN_VALUE));
		}
		
		/**
		 * Format a Dilution
		 * 
		 * @param bPretty <code>true</code> if the value is to be formatted with thousands separator,
		 * <code>false</code> otherwise
		 * @return the formatted Units
		 */
		public String format(boolean bPretty) {
			StringBuilder	myFormat;
			myFormat = super.format(bPretty);
			return myFormat.toString();
		}

		/**
		 * Create a new Dilution by parsing a string value 
		 * 
		 * @param pDilutions the Dilution to parse
		 * @return the new Dilution or <code>null</code> if parsing failed
		 */
		public static Dilution Parse(String pDilution) {
			Dilution myDilution;
			try {
				myDilution = new Dilution(pDilution);
			}
			catch (Exception e) {
				myDilution = null;
			}
			return myDilution;
		}

		/**
		 * Format a Dilution 
		 * 
		 * @param pDilution the dilution to format
		 * @return the formatted Dilution
		 */
		public static String format(Dilution pDilution) {
			String 	myFormat;
			myFormat = (pDilution != null) ? pDilution.format(false)
									       : "null";
			return myFormat;
		}

		/**
		 * Determine whether two Dilution objects differ.
		 * 
		 * @param pCurr The current Dilution
		 * @param pNew The new Dilution
		 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
		 */	
		public static boolean differs(Dilution pCurr, Dilution pNew) {
			return (((pCurr == null) && (pNew != null)) ||
					((pCurr != null) && 
					 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
		}
	}
	
	/**
	 * Represents a Money object. 
	 */
	public static class Money extends Number {
		/**
		 * Money has two decimal points 
		 */
		public final static int NUMDEC = 2;

		/**
		 * Money is formatted in pretty mode with a width of 10 characters 
		 */
		public final static int WIDTH  = 10;
		
		/**
		 * Access the value of the Money 
		 * 
		 * @return the value
		 */
		public long getAmount() { return getValue(); }
		
		/**
		 * Add money to the value 
		 * 
		 * @param pAmount The amount to add to this one.
		 */
		public void    addAmount(Money pAmount)      { super.addValue(pAmount); }

		/**
		 * Subtract money from the value 
		 * 
		 * @param pAmount The amount to subtract from this one.
		 */
		public void    subtractAmount(Money pAmount) { super.subtractValue(pAmount); }
		
		/**
		 * Construct a new Money from a value 
		 * 
		 * @param uAmount the value
		 */
		public Money(long uAmount) { super(NUMDEC, NumberClass.MONEY); super.setValue(uAmount); }	

		/**
		 * Construct a new Money by copying another money 
		 * 
		 * @param pMoney the Money to copy
		 */
		public Money(Money pMoney) {
			super(NUMDEC, NumberClass.MONEY);
			super.setValue(pMoney.getAmount()); 
		}	
		
		/**
		 * Construct a new Money by parsing a string value 
		 * 
		 * @param pMoney the Money to parse
		 */
		public Money(String pMoney) throws Exception {
			super(NUMDEC, NumberClass.MONEY);
			super.Parse(pMoney);
		}		
		
		/**
		 * obtain a Diluted value 
		 * 
		 * @param pDilution the dilution factor
		 * @return the calculated value
		 */
		public Money getDilutedAmount(Dilution pDilution) {
			Money	myTotal;
			long 	myValue  = getAmount();
			long 	myPower  = NUMDEC + Dilution.NUMDEC - NUMDEC;
			long 	myFactor = 1;
			long 	myDigit;
	
			/* Calculate division factor (less one) */
			while (myPower-->1) myFactor  *= 10;       
			
			/* Calculate new value */
			myValue  *= pDilution.getDilution();
			myValue  /= myFactor;
			
			/* Access the last digit to allow rounding and complete the calculation */
			myDigit  = myValue % 10;
			myValue /= 10;
			if (myDigit >= 5) myValue++;
			
			/* Allocate value */
			myTotal = new Money(myValue);
		
			/* Return value */
			return myTotal;
		}

		/**
		 * Format a Money 
		 * 
		 * @param bPretty <code>true</code> if the value is to be formatted with thousands separator
		 * and with a £ sign at the beginning, <code>false</code> otherwise
		 * @return the formatted Money
		 */
		public String format(boolean bPretty) {
			StringBuilder	myFormat;
			boolean 		isNegative;

			/* Format the value in a standard fashion */
			myFormat = super.format(bPretty);

			/* If we are in pretty mode */
			if (bPretty) {
				/* If the value is zero */
				if (!isNonZero()) {
					/* Provide special display */
					myFormat.setLength(0);
					myFormat.append("£      -   ");
				}

				/* Else non-zero value */
				else {
					/* If the value is negative, strip the leading minus sign */
					isNegative = (myFormat.charAt(0) == '-');
					if (isNegative) myFormat = myFormat.deleteCharAt(0);

					/* Extend the value to the desired width */
					while ((myFormat.length()) < WIDTH)
						myFormat.insert(0, ' ');

					/* Add the pound sign */
					myFormat.insert(0, '£');

					/* Add back any minus sign */
					if (isNegative) myFormat.insert(0, '-');
				}
			}
			return myFormat.toString();
		}

		/**
		 * Format money 
		 * 
		 * @param pMoney the money to format
		 * @return the formatted Money
		 */
		public static String format(Money pMoney) {
			String 	myFormat;
			myFormat = (pMoney != null) ? pMoney.format(false)
									    : "null";
			return myFormat;
		}

		/**
		 * Create a new Money by parsing a string value 
		 * 
		 * @param pMoney the Money to parse
		 * @return the new Money or <code>null</code> if parsing failed
		 */
		public static Money Parse(String pMoney) {
			Money myMoney;
			try {
				myMoney = new Money(pMoney);
			}
			catch (Exception e) {
				myMoney = null;
			}
			return myMoney;
		}
		
		/**
		 * Determine whether two Money objects differ.
		 * 
		 * @param pCurr The current Money 
		 * @param pNew The new Money
		 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
		 */	
		public static boolean differs(Money pCurr, Money pNew) {
			return (((pCurr == null) && (pNew != null)) ||
					((pCurr != null) && 
					 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
		}

		/**
		 * calculate the value of this money at a given rate 
		 * 
		 * @param pRate the rate to calculate at
		 * @return the calculated value
		 */
		public Money valueAtRate(Rate pRate) {
			Money   myTotal;
			long    myValue  = getAmount();
			long    myPower  = 2 + Rate.NUMDEC;
			long    myFactor = 1;
			long    myDigit;
			
			/* Calculate division factor (less one) */
			while (myPower-->1) myFactor  *= 10;       
			
			/* Calculate new value */
			myValue  *= pRate.getRate();
			myValue  /= myFactor;
			
			/* Access the last digit */
			myDigit  = myValue % 10;
			myValue /= 10;
			if (myDigit >= 5) myValue++;
			
			/* Allocate value */
			myTotal = new Money(myValue);
		
			/* Return value */
			return myTotal;
		}
		
		/**
		 * calculate the gross value of this money at a given rate
		 * used to convert from net to gross values form interest and dividends 
		 * 
		 * @param pRate the rate to calculate at
		 * @return the calculated value
		 */
		public Money grossValueAtRate(Rate pRate) {
			Money   myTotal;
			long    myValue  = getAmount();
			long    myDigit;
			
			/* Obtain 100% as a value */
			long myFactor = Rate.convertToValue(100);
			
			/* Multiply by 100% and then by 10 */
			myValue *= myFactor;
			myValue *= 10;
			
			/* Calculate the divisor */
			myFactor -= pRate.getRate();
			
			/* Divide by the factor */
			myValue  /= myFactor;
			
			/* Access the last digit */
			myDigit  = myValue % 10;
			myValue /= 10;
			if (myDigit >= 5) myValue++;
			
			/* Allocate value */
			myTotal = new Money(myValue);
		
			/* Return value */
			return myTotal;
		}
		
		/**
		 * calculate the TaxCredit value of this money at a given rate
		 * used to convert from net to gross values form interest and dividends 
		 * 
		 * @param pRate the rate to calculate at
		 * @return the calculated value
		 */
		public Money taxCreditAtRate(Rate pRate) {
			Money   myTotal;
			long    myValue  = getAmount();
			long    myDigit;
			
			/* Obtain 100% - Rate as a value */
			long myFactor = Rate.convertToValue(100);
			myFactor -= pRate.getRate();
			
			/* Multiply by the rate and then by 10 */
			myValue *= pRate.getRate();
			myValue *= 10;
			
			/* Divide by the factor */
			myValue  /= myFactor;
			
			/* Access the last digit */
			myDigit  = myValue % 10;
			myValue /= 10;
			if (myDigit >= 5) myValue++;
			
			/* Allocate value */
			myTotal = new Money(myValue);
		
			/* Return value */
			return myTotal;
		}
		
		/**
		 * calculate the value of this money at a given proportion (i.e. weight/total) 
		 * 
		 * @param pWeight the weight of this item
		 * @param pTotal the total weight of all the items
		 * @return the calculated value
		 */
		public Money valueAtWeight(Money pWeight,
								   Money pTotal) {
			Money   myTotal;
			long    myValue  = getAmount();
			long    myDigit;
			
			/* Calculate new value */
			myValue  *= pWeight.getValue() * 10;
			myValue  /= pTotal.getValue();
			
			/* Access the last digit */
			myDigit  = myValue % 10;
			myValue /= 10;
			if (myDigit >= 5) myValue++;
			
			/* Allocate value */
			myTotal = new Money(myValue);
		
			/* Return value */
			return myTotal;
		}

		/**
		 * calculate the value of this money at a given proportion (i.e. weight/total) 
		 * 
		 * @param pWeight the weight of this item
		 * @param pTotal the total weight of all the items
		 * @return the calculated value
		 */
		public Money valueAtWeight(Units pWeight,
								   Units pTotal) {
			Money   myTotal;
			long    myValue  = getAmount();
			long    myDigit;
			
			/* Calculate new value */
			myValue  *= pWeight.getValue() * 10;
			myValue  /= pTotal.getValue();
			
			/* Access the last digit */
			myDigit  = myValue % 10;
			myValue /= 10;
			if (myDigit >= 5) myValue++;
			
			/* Allocate value */
			myTotal = new Money(myValue);
		
			/* Return value */
			return myTotal;
		}

		/**
		 * Convert a whole number value to include decimals 
		 * @param pValue the whole number value 
		 * @return the converted value with added zeros 
		 */
		public static long convertToValue(long pValue) { 
			/* Build in the decimals to the value */
			return convertToValue(pValue, NUMDEC);       
		}		
	}	
}
