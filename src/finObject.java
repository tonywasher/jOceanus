package finance;

import java.util.Calendar;

/**
 * Provides basic object classes for use by the finance package. 
 * 
 * Seven object classes are provided
 * 
 *   
 * @author 	Tony Washer
 * @version 1.0
 * 
 * @see finObject.Money
 * @see finObject.Rate
 * @see finObject.Units
 * @see finObject.Price
 * @see finObject.Date
 * @see finObject.Range
 * @see finObject.Exception
 */
public class finObject {
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
	 * Determine whether two {@link finObject.Rate} objects differ.
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
	 * Determine whether two {@link finObject.Money} objects differ.
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
	 * Determine whether two {@link finObject.Price} objects differ.
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

	/**
	 * Determine whether two {@link finObject.Units} objects differ.
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

	/**
	 * Determine whether two {@link finObject.Date} objects differ.
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
	 * Determine whether two {@link finStatic.TransType} objects differ.
	 * 
	 * @param pCurr The current TransType 
	 * @param pNew The new TransType
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(finStatic.TransType pCurr, finStatic.TransType pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link finStatic.AccountType} objects differ.
	 * 
	 * @param pCurr The current AccountType 
	 * @param pNew The new AccountType
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(finStatic.AccountType pCurr, finStatic.AccountType pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link finStatic.TaxRegime} objects differ.
	 * 
	 * @param pCurr The current TransRegime
	 * @param pNew The new TransRegime
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(finStatic.TaxRegime pCurr, finStatic.TaxRegime pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link finStatic.Frequency} objects differ.
	 * 
	 * @param pCurr The current Frequency 
	 * @param pNew The new Frequency
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(finStatic.Frequency pCurr, finStatic.Frequency pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Determine whether two {@link finData.Account} objects differ.
	 * 
	 * @param pCurr The current Account 
	 * @param pNew The new Account
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(finData.Account pCurr, finData.Account pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Format a Rate 
	 * 
	 * @param pRate the rate to format
	 * @return the formatted Rate
	 */
	public static String formatRate(Rate pRate) {
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
	public static String formatMoney(Money pMoney) {
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
	public static String formatPrice(Price pPrice) {
		String 	myFormat;
		myFormat = (pPrice != null) ? pPrice.format(false)
								   : "null";
		return myFormat;
	}

	/**
	 * Format a Units 
	 * 
	 * @param pUnits the units to format
	 * @return the formatted Units
	 */
	public static String formatUnits(Units pUnits) {
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
	public static String formatTrans(finStatic.TransType pTrans) {
		String 	myFormat;
		myFormat = (pTrans != null) ? pTrans.getName()
								    : "null";
		return myFormat;
	}

	/**
	 * Format an Account 
	 * 
	 * @param pAccount the account to format
	 * @return the formatted account
	 */
	public static String formatAccount(finData.Account pAccount) {
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
	public static String formatAccountType(finStatic.AccountType pActType) {
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
	public static String formatRegime(finStatic.TaxRegime pRegime) {
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
	public static String formatFreq(finStatic.Frequency pFreq) {
		String 	myFormat;
		myFormat = (pFreq != null) ? pFreq.getName()
							       : "null";
		return myFormat;
	}

	/**
	 * Determine the precedence for a {@link finObject.EditState} value.
	 * 
	 * @param pTest The EditState 
	 * @return the precedence 
	 */	
	private static int editOrder(EditState pTest) {
		switch (pTest) {
			case ERROR: return 3;
			case DIRTY: return 2;
			case VALID: return 1;
			default:    return 0;
		}
	}

	/**
	 * Combine two EditState values into a single value, using the value of greater precedence.
	 * 
	 * @param pThis The First EditState 
	 * @param pThat The Second EditState 
	 * @return the combined EditState 
	 */	
	static EditState editCombine(EditState pThis, EditState pThat) {
		if (editOrder(pThis) > editOrder(pThat))
			return(pThis);
		else 
			return(pThat);
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
	 * Represents the underlying number class.
	 */	
	protected static class Number {
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
		 * @throws finObject.Exception if invalid number
		 */
		private void Parse(String pString) throws finObject.Exception {
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
		protected Rate(long uRate) { super(NUMDEC, NumberClass.RATE); super.setValue(uRate); }	

		/**
		 * Construct a new Rate by copying another rate 
		 * 
		 * @param pRate the Rate to copy
		 */
		protected Rate(Rate pRate) {
			super(NUMDEC, NumberClass.RATE);
			super.setValue(pRate.getRate()); 
		}
		
		/**
		 * Construct a new Rate by parsing a string value 
		 * 
		 * @param pRate the Rate to parse
		 * @throws {@link finObject.Exception} if string is not valid
		 */
		protected Rate(String pRate) throws Exception {
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
		protected Price(long uPrice) { super(NUMDEC, NumberClass.MONEY); super.setValue(uPrice); }	

		/**
		 * Construct a new Price by copying another price 
		 * 
		 * @param pPrice the Price to copy
		 */
		protected Price(Price pPrice) {
			super(NUMDEC, NumberClass.MONEY);
			super.setValue(pPrice.getPrice()); 
		}	

		/**
		 * Construct a new Price by parsing a string value 
		 * 
		 * @param pPrice the Price to parse
		 * @throws {@link finObject.Exception} if string is not valid
		 */
		protected Price(String pPrice) throws Exception {
			super(NUMDEC, NumberClass.MONEY);
			super.Parse(pPrice);
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
		protected Units(long uUnits) { super(NUMDEC); super.setValue(uUnits); }	

		/**
		 * Construct a new Units by copying another units 
		 * 
		 * @param pUnits the Units to copy
		 */
		protected Units(Units pUnits) {
			super(NUMDEC);
			super.setValue(pUnits.getUnits()); 
		}	

		/**
		 * Construct a new Units by parsing a string value 
		 * 
		 * @param pUnits the Units to parse
		 * @throws {@link finObject.Exception} if string is not valid
		 */
		protected Units(String pUnits) throws Exception {
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
		protected Money(long uAmount) { super(NUMDEC, NumberClass.MONEY); super.setValue(uAmount); }	

		/**
		 * Construct a new Money by copying another money 
		 * 
		 * @param pMoney the Money to copy
		 */
		protected Money(Money pMoney) {
			super(NUMDEC, NumberClass.MONEY);
			super.setValue(pMoney.getAmount()); 
		}	
		
		/**
		 * Construct a new Money by parsing a string value 
		 * 
		 * @param pMoney the Money to parse
		 * @throws {@link finObject.Exception} if string is not valid
		 */
		protected Money(String pMoney) throws Exception {
			super(NUMDEC, NumberClass.MONEY);
			super.Parse(pMoney);
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
		 * calculate the value of this money at a given rate 
		 * 
		 * @param pRate the rate to calculate at
		 * @return the calculated value
		 */
		public Money valueAtRate(finObject.Rate pRate) {
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
		 * calculate the value of this money at a given proportion (i.e. weight/total) 
		 * 
		 * @param pWeight the weight of this item
		 * @param pTotal the total weight of all the items
		 * @return the calculated value
		 */
		public Money valueAtWeight(finObject.Money pWeight,
								   finObject.Money pTotal) {
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
	}
	
	/**
	 * Represents a Date object. Dates in the finance package are merely dates with no associated
	 * time value 
	 */
	public static class Date {
		/**
		 * The Date object in underlying Java form
		 */
		private Calendar theDate  = null;
		
		/**
		 * The year of the date
		 */
		private int      theYear  = 0;
		
		/**
		 * The month of the date
		 */
		private int      theMonth = 0;
		
		/**
		 * The day of the date
		 */
		private int      theDay   = 0;

		/**
		 * Construct a new Date and initialise with todays date 
		 */
		public Date() {
			theDate = Calendar.getInstance();
			obtainValues();
		}

		/**
		 * Construct a new Date and initialise from a java date
		 * 
		 * @param pDate the java date to initialise from
		 */
		public Date(java.util.Date pDate) {
			if (pDate != null) {
				theDate = Calendar.getInstance();
				theDate.setTime(pDate);
				obtainValues();
			}
		}

		/**
		 * Construct a new Date and initialise from a finance date
		 * 
		 * @param pDate the finance date to initialise from
		 */
		public Date(Date pDate) {
			if ((pDate != null) && 
			    (pDate.theDate != null)) {
				theDate = Calendar.getInstance();
				theDate.setTime(pDate.theDate.getTime());
				obtainValues();
			}
		}
		
		/**
		 * Adjust the date by a number of years
		 * 
		 * @param iYear the number of years to adjust by
		 */
		public void adjustYear(int iYear) {
			if (theDate != null) { 
				theDate.add(Calendar.YEAR, iYear);
				obtainValues();
			}
		}

		/**
		 * Adjust the date by a number of months
		 * 
		 * @param iMonth the number of months to adjust by
		 */
		public void adjustMonth(int iMonth) {
			if (theDate != null) {
				theDate.add(Calendar.MONTH, iMonth);
				obtainValues();
			}
		}

		/**
		 * Adjust the date to the end of the following month
		 */
		public void endNextMonth() {
			if (theDate != null) {
				theDate.add(Calendar.DAY_OF_MONTH, 1-theDay);
				theDate.add(Calendar.MONTH, 2);
				theDate.add(Calendar.DAY_OF_MONTH, -1);
				obtainValues();
			}
		}

		/**
		 * Calculate the age that someone born on this date will be on a given date
		 * @param pDate the date for which to calculate the age
		 * @return the age on that date
		 */
		public int ageOn(Date pDate) {
			int myAge = -1;

			if ((theDate != null) && (pDate.theDate != null)) {
				/* Calculate the initial age assuming same date in year */
				myAge  = pDate.theDate.get(Calendar.YEAR);
				myAge -= theDate.get(Calendar.YEAR);
				
				/* If we are a later day in the year subtract 1 year */
				if (theDate.get(Calendar.DAY_OF_YEAR) > 
				    pDate.theDate.get(Calendar.DAY_OF_YEAR))
					myAge--;
			}
			
			/* Return to caller */
			return myAge;
		}

		/**
		 * Adjust the date by a number of days
		 * 
		 * @param iDay the number of days to adjust by
		 */
		public void adjustDay(int iDay) {
			if (theDate != null) {
				theDate.add(Calendar.DAY_OF_MONTH, iDay);
				obtainValues();
			}
		}

		/**
		 * Copy a date from another finance date
		 * 
		 * @param pDate the date to copy from
		 */
		public void copyDate(Date pDate) {
			theDate.setTime(pDate.theDate.getTime());
			obtainValues();
		}

		/**
		 * Get the year of the date
		 * 
		 * @return the year of the date
		 */
		public int     getYear()  { return theYear; }

		/**
		 * Get the month of the date
		 * 
		 * @return the month of the date
		 */
		public int     getMonth() { return theMonth; }

		/**
		 * Get the day of the date
		 * 
		 * @return the day of the date
		 */
		public int     getDay()   { return theDay; }

		/**
		 * Determine whether the date value is null
		 * 
		 * @return <code>true</code> if the date is null, <code>false</code> otherwise
		 */
		public boolean isNull()   { return (theDate == null); }

		/**
		 * Get the java date associated with this object
		 * 
		 * @return the java date
		 */
		public java.util.Date getDate() { 
			return (theDate == null) ? null 
					                 : theDate.getTime(); }
		
		/**
		 * Obtain the year,month and day values from the date
		 */
		private void obtainValues() {
			theYear  = theDate.get(Calendar.YEAR); 
			theMonth = theDate.get(Calendar.MONTH); 
			theDay   = theDate.get(Calendar.DAY_OF_MONTH); 
		}
		
		/**
		 * Format a Date 
		 * 
		 * @param bShowNULL <code>true</code> if a NULL date is to be formatted as "NULL" rather
		 * than jut being returned as the null string
		 * @return the formatted Date
		 */
		public String formatDate(boolean bShowNULL) {
			StringBuilder   myString = new StringBuilder();
			String[] 		myMonths = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
					              		 "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
			String   		myMonth;
			
			if (theDate != null) {
				myMonth = myMonths[theDate.get(Calendar.MONTH)];
				myString.append("0");
				myString.append(theDate.get(Calendar.DAY_OF_MONTH));
				if (myString.length() == 3) myString.deleteCharAt(0);
				myString.append("-");
				myString.append(myMonth);
				myString.append("-");
                myString.append(theDate.get(Calendar.YEAR));
    	        return myString.toString();
			}
			return (bShowNULL) ? "NULL" : null;
		}
		
		/**
		 * Compare this Date to another to establish sort order.
		 * 
		 * @param that The Number to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		public int compareTo(Date that) {
	        if (this == that) return 0;
	        if (that == null) return -1;
	        if (this.theDate == null) return 1;
	        if (that.theDate == null) return -1;
	        if (this.theYear < that.theYear) return -1;
	        if (this.theYear > that.theYear) return 1;
	        if (this.theMonth < that.theMonth) return -1;
	        if (this.theMonth > that.theMonth) return 1;
	        if (this.theDay < that.theDay) return -1;
	        if (this.theDay > that.theDay) return 1;
			return 0;
		}
	}
	
	/**
	 * Represents a contiguous Range of dates 
	 */
	public static class Range {
		/**
		 * The Start Date for the range
		 */
		private Date theStart = null;

		/**
		 * The End Date for the range
		 */
		private Date theEnd   = null;
		
		/**
		 * Get the start date for the range
		 * 
		 * @return the Start date
		 */
		public Date  getStart()    { return theStart; }

		/**
		 * Get the end date for the range
		 * 
		 * @return the End date
		 */
		public Date  getEnd()      { return theEnd; }
		
		/**
		 * Construct a Range from a Start Date and an End Date
		 * 
		 * @param pStart the start date
		 * @param pEnd the end date
		 */
		public Range(Date pStart, Date pEnd) {
			theStart = new finObject.Date(pStart);
			theEnd   = new finObject.Date(pEnd);
		}
		
		/**
		 * Construct a range from another range
		 * 
		 * @param pRange the range to copy from
		 */
		public Range(Range pRange) {
			theStart = new finObject.Date(pRange.theStart);
			theEnd   = new finObject.Date(pRange.theEnd);
		}
		
		/**
		 * Determine whether a Date is within the date range
		 * 
		 * @return -1, 0, 1 if early, in range or late
		 */
		protected short compareTo(Date pDate) {
			if ((theStart != null) && (theStart.compareTo(pDate) > 0)) return 1;
			if ((theEnd != null) && (theEnd.compareTo(pDate) < 0))     return -1;
			return 0;
		}
		
		/**
		 * Compare the range to another range
		 * 		
		 * @return -1, 0, 1 if early, equal or later
		 */
		public int compareTo(Range that) {
			int result;
	        if (this == that) return 0;
	        if (that == null) return -1;
			if (this.getStart() != that.getStart()) {
				if (this.getStart() == null) return 1;
				if (that.getStart() == null) return -1;
				result = theStart.compareTo(that.theStart);
				if (result != 0) return result;
			}
			if (this.getEnd() != that.getEnd()) {
				if (this.getEnd() == null) return 1;
				if (that.getEnd() == null) return -1;
				result = theEnd.compareTo(that.theEnd);
				if (result != 0) return result;
			}
	        return 0;
		}
	}
	
	/**
	 * Enumeration of states of data objects
	 */
	protected static enum State {
		/*
		 * No known state
		 */
		NOSTATE,
		
		/**
		 * New object
		 */
		NEW,
		
		/**
		 * Clean object with no changes
		 */
		CLEAN,
		
		/**
		 * Changed object with history
		 */
		CHANGED,
		
		/**
		 * Deleted Clean object
		 */
		DELETED,
		
		/**
		 * Deleted New object
		 */
		DELNEW,
		
		/**
		 * Deleted Changed object
		 */
		DELCHG,
		
		/**
		 * Recovered deleted object
		 */
		RECOVERED;
	}
	
	/**
	 * Enumeration of edit states of lists in a view
	 */
	protected static enum EditState {
		/**
		 * No changes made
		 */
		CLEAN,
		
		/**
		 *  Non-validated changes made 
		 */
		DIRTY,
		
		/**
		 * Only valid changes made
		 */
		VALID,
		
		/**
		 * Object is in error
		 */
		ERROR;
	}
	
	/**
	 * Enumeration of load states of data
	 */
	protected static enum LoadState {
		/**
		 * Initial loading, with parental account links and close-ability not yet done
		 */
		INITIAL,
		
		/**
		 *  Final loading with parental links and close-ability done 
		 */
		FINAL,
		
		/**
		 * Fully loaded
		 */
		LOADED;
	}
	
	/**
	 * Represents an HTML dumpable object
	 */
	public interface htmlDumpable {
		public StringBuilder toHTMLString();
	}
	
	/**
	 * Represents a finance exception wrapper 
	 */
	public static class Exception extends java.lang.Exception
								  implements htmlDumpable {
		/**
		 * Required serialisation field
		 */
		private static final long serialVersionUID = 3100519617218144798L;
		
		/**
		 * The class of this exception
		 */
	    private ExceptionClass theClass    = null;
	    
	    /**
	     * The class of the associated object if present
	     */
	    private ObjectClass    theObjClass = ObjectClass.NONE;
	    
	    /**
	     * The associated object
	     */
	    private htmlDumpable   theObject   = null;
	    
	    /**
	     * Get the class of the exception 
	     * @return the class
	     */
	    public ExceptionClass  getExceptionClass() { return theClass; }
	    
	    /**
	     * Get the class of the associated object  
	     * @return the class
	     */
	    public ObjectClass     getObjectClass()    { return theObjClass; }
	    
	    /**
	     * Get the associated object
	     * @return the associated object
	     */
	    public Object          getObject()         { return theObject; }
	    
		/**
		 * Create a new Exception object based on a string and class
		 * @param ec the exception class
		 * @param s the description of the exception
		 */
		public Exception(ExceptionClass ec, String s) { 
			super(s); 
			theClass = ec;
			fillInStackTrace();
		}

		/**
		 * Create a new Exception object based on a string and a known exception type
		 * @param ec the exception class
		 * @param s the description of the exception
		 * @param c	the underlying exception
		 */
		public Exception(ExceptionClass ec, String s, Throwable c) {
			super(s, c);
			theClass = ec;
		}
		
		/**
		 * Create a new Exception object based on a string and and object
		 * @param ec the exception class
		 * @param o	the associated object
		 * @param oc the associated object class
		 * @param s the description of the exception
		 */
		public Exception(ExceptionClass ec,
				         ObjectClass    oc, 
				         htmlDumpable   o,
				         String         s) {
			super(s);
			theClass    = ec;
			theObjClass = oc;
			theObject   = o;
			fillInStackTrace();
		}
		
		/**
		 * Format the exception
		 * @return the formatted string
		 */
		public StringBuilder toHTMLString() {
			StringBuilder		myString     = new StringBuilder(10000);
			StringBuilder		myDetail	 = new StringBuilder(10000);	
			StackTraceElement[] myTrace      = null;
			Object				myUnderLying = null;
			StringBuilder		myXtra	 	 = null;
			int					myNumDetail  = 4;
			
			/* Initialise the string with an item name */
			myString.append("<table border=\"1\" width=\"75%\" align=\"center\">");
			myString.append("<thead><th>Exception</th>");
			myString.append("<th>Field</th><th>Value</th></thead><tbody>");
			
			/* Add the message details */
			myDetail.append("<tr><td>Message</td><td>");
			myDetail.append(getMessage());
			myDetail.append("</td></tr>");
			myDetail.append("<tr><td>Class</td><td>");
			myDetail.append(theClass);
			myDetail.append("</td></tr>");
			myDetail.append("<tr><td>ObjectClass</td><td>");
			myDetail.append(theObjClass);
			myDetail.append("</td></tr>");
			
			/* Access the underlying cause */
			myUnderLying = getCause();
			
			/* If the underlying cause is non-null */
			if (myUnderLying != null) {
				/* If the underlying cause is another instance of us */
				if (myUnderLying instanceof finObject.Exception) {
					/* Format the underlying exception */
					myXtra = ((finObject.Exception)myUnderLying).toHTMLString();
				}
				
				/* else we need to access the message and the stack trace */
				else {
					/* Record the underlying message */
					myDetail.append("<tr><td>ExceptionType</td><td>");
					myDetail.append(myUnderLying.getClass().getName());
					myDetail.append("</td></tr>");
					myNumDetail++;
					myDetail.append("<tr><td>Message</td><td>");
					myDetail.append(getCause().getMessage());
					myDetail.append("</td></tr>");
					myNumDetail++;
					
					/* Access the stack trace */
					myTrace = getCause().getStackTrace();
				}
			}
			
			/* Else we are the original exception */
			else {
				/* Access the stack trace */
				myTrace = getStackTrace();
			}
			
			/* Add the details */
			myString.append("<tr><th rowspan=\"");
			myString.append(myNumDetail);
			myString.append("\">Detail</th></tr>");
			myString.append(myDetail);
			myString.append("</tbody></table>");
			
			/* If there is an associated object */
			if (theObject != null) {
				/* Format the object */
				myString.append("<p>");
				myString.append(theObject.toHTMLString());
			}
			
			/* If there is an underlying exception */
			if (myXtra != null) {
				/* Format the Xtra */
				myString.append("<p>");
				myString.append(myXtra);
			}
			
			/* If there is a stack trace */
			if (myTrace != null) {
				/* Add the stack trace */
				myString.append("<p><table border=\"1\" width=\"75%\" align=\"center\">");
				myString.append("<thead><th>Stack Trace</th></thead><tbody>");
				
				/* Loop through the elements */
				for (StackTraceElement st : myTrace) {
					/* Add the stack trace */
					myString.append("<tr><td>");
					myString.append(st.toString());
					myString.append("</td></tr>");
				}
				
				/* Terminate the table */
				myString.append("</tbody></table>");
			}
			
			return myString;
		}
	}
	
	public static enum ExceptionClass {
		/**
		 * Exception from SQL server
		 */
		SQLSERVER,
		
		/**
		 * Exception from SQL Server
		 */
		EXCEL,
		
		/**
		 * Exception from Encryption library
		 */
		ENCRYPT,
		
		/**
		 * Exception from Data
		 */
		DATA,
		
		/**
		 * Exception from validation
		 */
		VALIDATE,
		
		/**
		 * Exception from preferences
		 */
		PREFERENCE,
		
		/**
		 * Exception from logic
		 */
		LOGIC;
	}
	
	public static enum ObjectClass {
		/**
		 * No associated object
		 */
		NONE,
		
		/**
		 * DataSet
		 */
		DATASET,
		
		/**
		 * AccountType
		 */
		ACCOUNTTYPE,
		
		/**
		 * TransType
		 */
		TRANSTYPE,
		
		/**
		 * TaxType
		 */
		TAXTYPE,
		
		/**
		 * Tax Regime
		 */
		TAXREGIME,
		
		/**
		 * Frequency
		 */
		FREQUENCY,
		
		/**
		 * TaxYear
		 */
		TAXPARAMS,
		
		/**
		 * Account
		 */
		ACCOUNT,
		
		/**
		 * Rate
		 */
		RATE,
		
		/**
		 * Price
		 */
		PRICE,
		
		/**
		 * Pattern
		 */
		PATTERN,
		
		/**
		 * Event
		 */
		EVENT,
	}
}
