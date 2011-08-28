package uk.co.tolcroft.models.data;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Number;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.security.SecurityControl;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public abstract class EncryptedItem<T extends EncryptedItem<T>> extends DataItem<T> {
	/**
	 * The Id of the Control Key
	 */
	private int			theControlId	= -1;
	
	/**
	 * Encrypted Money length
	 */
	public final static int MONEYLEN 		= 10;

	/**
	 * Encrypted Units length
	 */
	public final static int UNITSLEN 		= 10;

	/**
	 * Encrypted Rate length
	 */
	public final static int RATELEN 		= 10;

	/**
	 * Encrypted Price length
	 */
	public final static int PRICELEN 		= 10;

	/**
	 * Encrypted Dilution length
	 */
	public final static int DILUTELEN 		= 10;

	/**
	 * Get the ControlKey for this item
	 * @return the ControlKey
	 */
	public ControlKey		getControlKey()     { return getValues().getControl(); }

	/* Linking methods */
	public EncryptedItem<?>.EncryptedValues  getValues()  { 
		return (EncryptedItem<?>.EncryptedValues)super.getCurrentValues(); }
	
	/* Field IDs */
	public static final int 	FIELD_CONTROL  	= DataItem.NUMFIELDS;
	public static final int 	NUMFIELDS	   	= FIELD_CONTROL+1; 
	public static final String 	NAME_CTLID		= "ControlId"; 

	/**
	 * Determine the field name for a particular field
	 * This method is the underlying method called when the id is unknown 
	 * @return the field name
	 */
	public static String	fieldName(int fieldId)	{
		switch (fieldId) {
			case FIELD_CONTROL:	return NAME_CTLID;
			default: 			return DataItem.fieldName(fieldId);
		}
	}
	
	/**
	 * Format the value of a particular field as a table row
	 * @param iField the field number
	 * @param pObj the values to use
	 * @return the formatted field
	 */
	public String formatField(int iField, HistoryValues<T> pObj) {
		String 			myString = "";
		EncryptedItem<?>.EncryptedValues myObj 	 = (EncryptedItem<?>.EncryptedValues)pObj;
		switch (iField) {
			case FIELD_CONTROL:
				myString += (myObj.getControl() == null) 
								? "Id=" + theControlId
								: "Id=" + myObj.getControl().getId(); 
				break;
			default:	
				myString = super.formatField(iField, pObj);
				break;
		}
		return myString;
	}
							
	/**
	 * Constructor
	 * @param pCtl the list that this item is associated with
	 * @param uId the Id of the new item (or 0 if not yet known)
	 */
	public EncryptedItem(EncryptedList<T> pList, int uId) {
		super(pList, uId);
	}

	/**
	 * Set ControlKey
	 * @param pControlKey the Control Key
	 */
	protected void setControlKey(ControlKey pControlKey) {
		EncryptedItem<?>.EncryptedValues myObj = getValues();
		if (myObj != null) myObj.setControl(pControlKey);
	}
	
	/**
	 * Set ControlKey
	 * @param uControlId the Control Id
	 */
	protected void setControlKey(int uControlId) throws Exception {
		/* Store the id */
		theControlId = uControlId;

		/* Look up the Control keys */
		DataSet<?>		myData = ((EncryptedList<?>)getList()).getData();
		ControlKey.List myKeys = myData.getControlKeys();
			
		/* Look up the ControlKey */
		ControlKey myControl = myKeys.searchFor(theControlId);
		if (myControl == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid ControlKey Id");
		
		/* Store the ControlKey */
		setControlKey(myControl);
	}
	
	/**
	 * Determine whether two ValuePair objects differ.
	 * @param pCurr The current Pair
	 * @param pNew The new Pair
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(EncryptedItem<?>.ValuePair pCurr, 
								  EncryptedItem<?>.ValuePair pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (!pCurr.equals(pNew)))));
	}
	
	/**
	 * Initialise security for all encrypted values
	 * @param pControl the new Control Key 
	 */	
	private void adoptSecurity(ControlKey pControl,
							   T		  pBase) throws Exception {
		/* Access the values */
		EncryptedItem<?>.EncryptedValues myValues = getValues();
		
		/* Set the Control Key */
		setControlKey(pControl);
		
		/* If we have the same control key */
		if ((pBase != null) &&
			(pControl.equals(pBase.getControlKey())))
		{
			/* Adopt the security */
			myValues.adoptSecurity(pControl, pBase.getValues());
		}
		
		/* else we need to initialise security */
		else {
			/* Apply key to all elements */
			myValues.applySecurity();
		}
	}
	
	/**
	 * Update security for all encrypted values
	 * @param pControl the new Control Key 
	 */	
	private void updateSecurity(ControlKey pControl) throws Exception {
		/* Access the values */
		EncryptedItem<?>.EncryptedValues myValues = getValues();

		/* Ignore call if we have the same control key */
		if (pControl.equals(getControlKey()))
			return;
		
		/* Store the current detail into history */
		pushHistory();
		
		/* Set the Control Key */
		setControlKey(pControl);
		
		/* Apply key to all elements */
		myValues.applySecurity();

		/* Check for changes */
		if (checkForHistory()) setState(DataState.CHANGED);
	}
	
	/**
	 * Encrypted DataList
	 */
	public abstract static class EncryptedList<T extends EncryptedItem<T>>  extends DataList<T> {
		private DataSet<?>	theData			= null;
		public 	DataSet<?>	getData()		{ return theData; }

		/** 
	 	 * Construct an empty CORE encrypted list
	 	 * @param the class of items in the list 
	 	 * @param pData the DataSet for the list
	 	 */
		protected EncryptedList(Class<T> pClass, DataSet<?> pData) { 
			super(pClass, ListStyle.CORE, true);
			theData = pData;
		}

		/** 
	 	 * Construct a generic encrypted list
	 	 * @param the class of items in the list 
	 	 * @param pData the DataSet for the list
	 	 * @param pStyle the style of the list 
	 	 */
		public EncryptedList(Class<T> pClass, DataSet<?> pData, ListStyle pStyle) { 
			super(pClass, pStyle, true);
			theData = pData;
		}

		/** 
	 	 * Construct a generic account list
	 	 * @param the class of items in the list 
	 	 * @param pList the source account list 
	 	 * @param pStyle the style of the list 
	 	 */
		public EncryptedList(Class<T> pClass, EncryptedList<T> pList, ListStyle pStyle) { 
			super(pClass, pList, pStyle);
			theData = pList.getData();
		}

		/** 
	 	 * Construct a difference account list
	 	 * @param pNew the new Account list 
	 	 * @param pOld the old Account list 
	 	 */
		protected EncryptedList(EncryptedList<T> pNew, EncryptedList<T> pOld) { 
			super(pNew, pOld);
			theData = pNew.getData();
		}
		
		/**
		 * Update Security for items in the list
		 * @param pControl the control key to apply
		 */
		public void updateSecurity(ControlKey pControl) throws Exception {
			ListIterator 	myIterator;
			T				myCurr;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the items */
			while ((myCurr = myIterator.next()) != null) {
				/* Ensure encryption of the item */
				myCurr.updateSecurity(pControl);
			}
			
			/* Return to caller */
			return;
		}	

		/**
		 * Adopt security from underlying list.
		 * If a match for the item is found in the underlying list, its security is adopted.
		 * If no match is found then the security is initialised.
		 * @param pControlKey the control key to initialise from
		 * @param pBase The base list to adopt from 
		 */
		protected void adoptSecurity(ControlKey 		pControl,
								     EncryptedList<?> 	pBase) throws Exception {
			/* Local variables */
			ListIterator 		myIterator;
			EncryptedItem<?>	myCurr;
			EncryptedItem<?>	myBase;
			T					mySource;
			T					myTarget;
			Class<T>			myClass	= getBaseClass();
				
			/* Create an iterator for our new list */
			myIterator = listIterator(true);
			
			/* Loop through this list */
			while ((myCurr = myIterator.next()) != null) {
				/* Locate the item in the base list */
				myBase = pBase.searchFor(myCurr.getId());
				
				/* Cast the items correctly */
				mySource = (myBase == null) ? null : myClass.cast(myBase);
				myTarget = myClass.cast(myCurr);
				
				/* Adopt/initialise the security */
				myTarget.adoptSecurity(pControl, mySource);
			}
		}
		
		/* List Iterators */
		public void setNewId(T pItem)	{ super.setNewId(pItem); }
	}
		
	/**
	 * Values for an EncryptedItem
	 */
	protected abstract class EncryptedValues implements HistoryValues<T> {
		private ControlKey	theControl   = null;
		
		/* Access methods */
		public ControlKey  	getControl()   	{ return theControl; }
		
		/* Value setting */
		public void setControl(ControlKey pControl) { 
			theControl   = pControl;
			theControlId = (pControl == null) ? -1 : pControl.getId();
		}

		/* Constructor */
		public EncryptedValues() {}
		public EncryptedValues(EncryptedValues pValues) {
			theControl = pValues.getControl();
		}
		
		public boolean histEquals(HistoryValues<T> pValues) {
			EncryptedValues myValues = (EncryptedValues)pValues;
			if (ControlKey.differs(theControl,    myValues.theControl))    return false;
			return true;
		}
		public void    copyFrom(HistoryValues<?> pValues) {
			if (pValues instanceof EncryptedItem.EncryptedValues) {
				EncryptedItem<?>.EncryptedValues myValues = (EncryptedItem<?>.EncryptedValues)pValues;
				theControl = myValues.getControl();
			}
		}
		public boolean	fieldChanged(int fieldNo, HistoryValues<T> pOriginal) {
			EncryptedValues 	pValues = (EncryptedValues)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case FIELD_CONTROL:
					bResult = (ControlKey.differs(theControl,      pValues.theControl));
					break;
			}
			return bResult;
		}

		/* Apply Security */
		protected abstract void applySecurity() throws Exception;
		protected abstract void adoptSecurity(ControlKey 		pControl,
											  EncryptedValues	pBase) throws Exception;
	}
	
	/**
	 *  The underlying Value Pair Class
	 */
	private abstract class ValuePair {
		/**
		 * The Encrypted Value
		 */
		private byte[] theBytes	= null;
		
		/**
		 * Obtain the encrypted value
		 * @return the encrypted value
		 */
		public byte[] getBytes() { return theBytes; }
		
		/**
		 * Set the encrypted bytes
		 * @param pBytes the encrypted value
		 */
		private void setBytes(byte[] pBytes) { theBytes = pBytes; }
		
		/**
		 * Set the encrypted bytes
		 * @param pBytes the encrypted bytes
		 */
		private void setEncryptedValue(byte[] pBytes) { theBytes = pBytes; } 
		
		/**
		 * Get bytes for Encryption
		 * @return the bytes to be encrypted
		 */
		protected abstract byte[] getBytesForEncryption() throws Exception;
			
		/**
		 * Set decrypted value
		 * @param pValue the decrypted byte value
		 */
		protected abstract void setDecryptedValue(byte[] pValue) throws Exception;
		
		/**
		 * Encrypt a valuePair
		 */
		public void encryptPair() throws Exception {
			ControlKey myControl = getControlKey();
			
			/* Reject if encryption is not initialised */
			if (myControl == null)
				throw new Exception(ExceptionClass.LOGIC,
									"Encryption is not initialised");
			
			/* Obtain the bytes representation of the value */
			byte[] myBytes = getBytesForEncryption();
			
			/* Encrypt the Bytes */
			myBytes = myControl.encryptBytes(myBytes);

			/* Set the encrypted value */
			setEncryptedValue(myBytes);
		}		

		/**
		 * Decrypt a valuePair
		 */
		protected void decryptPair() throws Exception {
			ControlKey myControl = getControlKey();
			
			/* Reject if encryption is not initialised */
			if (myControl == null)
				throw new Exception(ExceptionClass.LOGIC,
									"Encryption is not initialised");
			
			/* Decrypt the Bytes */
			byte[] myBytes = myControl.decryptBytes(theBytes);

			/* Set the decrypted value */
			setDecryptedValue(myBytes);
		}		
	}
	
	/* Access methods for encryption */
	public static String getPairValue(EncryptedItem<?>.StringPair pPair) {
		return (pPair == null) ? null : pPair.getString(); }
	public static char[] getPairValue(EncryptedItem<?>.CharArrayPair pPair) {
		return (pPair == null) ? null : pPair.getChars(); }
	public static Money getPairValue(EncryptedItem<?>.MoneyPair pPair) {
		return (pPair == null) ? null : pPair.getValue(); }
	public static Units getPairValue(EncryptedItem<?>.UnitsPair pPair) {
		return (pPair == null) ? null : pPair.getValue(); }
	public static Rate getPairValue(EncryptedItem<?>.RatePair pPair) {
		return (pPair == null) ? null : pPair.getValue(); }
	public static Price getPairValue(EncryptedItem<?>.PricePair pPair) {
		return (pPair == null) ? null : pPair.getValue(); }
	public static Dilution getPairValue(EncryptedItem<?>.DilutionPair pPair) {
		return (pPair == null) ? null : pPair.getValue(); }
	public static byte[] getPairBytes(EncryptedItem<?>.ValuePair pPair) {
		return (pPair == null) ? null : pPair.getBytes(); }
	public static String getCharArrayPairString(EncryptedItem<?>.CharArrayPair pPair) {
		return ((pPair == null) || (pPair.getChars() == null)) ? null : new String(pPair.getChars());
	}

	/**
	 *  The String Pair Class
	 */
	public class StringPair extends ValuePair {
		/**
		 * The Non-encrypted value
		 */
		private String theString	= null;
		
		/**
		 * Obtain the non-encrypted value
		 * @return the non-encrypted string
		 */
		public String getString() { return theString; }
		
		/**
		 * Set the Clear text value
		 * @param pString the clear text string
		 */
		protected void setString(String pString) { theString = pString; }
		
		/**
		 * Constructor from a clear value
		 * @param pValue the clear value
		 */
		public StringPair(String pValue) throws Exception { 
			/* Store the value */
			theString = pValue;
			
			/* Encrypt immediately if possible */
			if (theControlId != -1) encryptPair();
		} 

		/**
		 * Constructor from an Encrypted value
		 * @param pBytes the encrypted value
		 */
		public StringPair(byte[] pBytes) throws Exception { 
			/* Store the value */
			super.setBytes(pBytes);
			
			/* Decrypt immediately */
			decryptPair();
		} 

		/**
		 * Constructor from a String Pair
		 * @param pPair the String pair
		 */
		public StringPair(EncryptedItem<?>.StringPair pPair) { 
			/* Store the string value */
			theString = pPair.getString();
			
			/* Store the bytes */
			super.setBytes(pPair.getBytes());
		} 

		/**
		 * Get bytes for Encryption
		 * @return the bytes to be encrypted
		 */
		protected byte[] getBytesForEncryption() throws Exception {
			/* Protect against exceptions */
			try {
				/* Convert the string to a byte array */
				return theString.getBytes(SecurityControl.ENCODING);
			}
			
			/* Catch Exceptions */
			catch (Throwable e) {
				throw new Exception(ExceptionClass.CRYPTO,
									"Failed to convert value to bytes");
			}
		}

		/**
		 * Set decrypted value
		 * @param pValue the decrypted byte value
		 */
		protected void setDecryptedValue(byte[] pValue) throws Exception {
			/* Protect against exceptions */
			try {
				/* Convert the byte array to a string */
				setString(new String(pValue , SecurityControl.ENCODING));
			}
			
			/* Catch Exceptions */
			catch (Throwable e) {
				throw new Exception(ExceptionClass.CRYPTO,
									"Failed to convert value from bytes");
			}
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
			
			/* Make sure that the object is a StringPair */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the target Pair */
			EncryptedItem<?>.StringPair myThat = (EncryptedItem<?>.StringPair)pThat;
			
			/* Check differences */
			if (Utils.differs(getString(), myThat.getString())) return false;
			if (Utils.differs(getBytes(), myThat.getBytes())) return false;
			return true;
		}

		/**
		 * Encrypt a stringPair or borrow encrypted form from base
		 * @param pBase the base encrypted pair
		 */
		public void encryptPair(EncryptedItem<?>.StringPair pBase) throws Exception {
			/* If the raw format differs */
			if ((pBase == null) ||
				(Utils.differs(getString(), pBase.getString()))) {
				/* Ignore the base and encrypt the string */
				encryptPair();
			}
			
			/* else we should adopt the previous encryption */
			else {				
				/* Store the bytes */
				super.setBytes(pBase.getBytes());
			}
		}		
	}
	
	/**
	 *  The CharArray Pair Class
	 */
	public class CharArrayPair extends ValuePair {
		/**
		 * The Non-encrypted value
		 */
		private char[] theChars	= null;
		
		/**
		 * Obtain the non-encrypted value
		 * @return the non-encrypted characters
		 */
		public char[] getChars() { return theChars; }
		
		/**
		 * Constructor from a clear value
		 * @param pValue the clear value
		 */
		public CharArrayPair(char[] pValue) throws Exception { 
			/* Store the value */
			theChars = pValue;
			
			/* Encrypt immediately if possible */
			if (theControlId != -1) encryptPair();
		} 

		/**
		 * Constructor from an Encrypted value
		 * @param pBytes the encrypted value
		 */
		public CharArrayPair(byte[] pBytes) throws Exception { 
			/* Store the value */
			super.setBytes(pBytes);
			
			/* Decrypt immediately */
			decryptPair();
		} 

		/**
		 * Get bytes for Encryption
		 * @return the bytes to be encrypted
		 */
		protected byte[] getBytesForEncryption() throws Exception {
			/* Convert the string to a byte array */
			return Utils.charToByteArray(theChars);
		}

		/**
		 * Set decrypted value
		 * @param pValue the decrypted byte value
		 */
		protected void setDecryptedValue(byte[] pValue) throws Exception {
			/* Convert the byte array to a string */
			Utils.byteToCharArray(pValue);
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
			
			/* Make sure that the object is a CharArrayPair */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the target Pair */
			EncryptedItem<?>.CharArrayPair myThat = (EncryptedItem<?>.CharArrayPair)pThat;
			
			/* Check differences */
			if (Utils.differs(getChars(), myThat.getChars())) return false;
			if (Utils.differs(getBytes(), myThat.getBytes())) return false;
			return true;
		}

		/**
		 * Encrypt a charArrayPair or borrow encrypted form from base
		 * @param pBase the base encrypted pair
		 */
		public void encryptPair(CharArrayPair pBase) throws Exception {
			/* If the raw format differs */
			if ((pBase == null) || 
				(Utils.differs(getChars(), pBase.getChars()))) {
				/* Ignore the base and encrypt the string */
				encryptPair();
			}
			
			/* else we should adopt the previous encryption */
			else {				
				/* Store the bytes */
				super.setBytes(pBase.getBytes());
			}
		}		
	}
	
	/**
	 *  The Underlying Number Pair Class
	 */
	private abstract class NumberPair<X extends Number> extends StringPair {
		/**
		 * The Non-encrypted number
		 */
		private X			theValue;
		
		/**
		 * Access the Non-encrypted value
		 * @return the non-encrypted value
		 */
		public X			getValue() { return theValue; } 

		/**
		 * Constructor from a clear value
		 * @param pValue the clear value
		 */
		public NumberPair(X pValue) throws Exception { 
			/* Store the value */
			super(pValue.format(false));
			
			/* Store the value */
			theValue = pValue;
		} 

		/**
		 * Constructor from a string value
		 * @param pValue the string value
		 */
		public NumberPair(String pValue) throws Exception { 
			/* Store the value */
			super(pValue);
			
			/* Store the value */
			theValue = parseValue(pValue);
			setString(theValue.format(false));
		} 

		/**
		 * Constructor from an Encrypted value
		 * @param pBytes the encrypted value
		 */
		public NumberPair(byte[] pBytes) throws Exception { 
			/* Store the value */
			super(pBytes);
		} 

		/**
		 * Constructor from a Number Pair
		 * @param pPair the Number pair
		 */
		public NumberPair(EncryptedItem<?>.NumberPair<X> pPair) {
			super(pPair);
			
			/* Store the value */
			theValue = pPair.getValue();
		} 

		/**
		 * Set decrypted value
		 * @param pValue the decrypted byte value
		 */
		protected void setDecryptedValue(byte[] pValue) throws Exception {
			/* Convert the byte array to a string */
			super.setDecryptedValue(pValue);
			
			/* Parse the value */
			theValue = parseValue(getString());
		}
		
		/**
		 * Parse a string value to get a value
		 * @param pValue the string value
		 * @return the value
		 */
		protected abstract X parseValue(String pValue) throws Exception;
				
		/**
		 * Compare this NumberPair type to another to establish equality.
		 * @param pThat The Pair to compare to
		 * @return <code>true</code> if the account type is identical, <code>false</code> otherwise
		 */
		public boolean equals(Object pThat) {
			/* Handle the trivial cases */
			if (this == pThat) return true;
			if (pThat == null) return false;
			
			/* Make sure that the object is a NumberPair */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the target Pair */
			EncryptedItem<?>.NumberPair<?> myThat = (EncryptedItem<?>.NumberPair<?>)pThat;
			
			/* Check differences */
			if (Number.differs(getValue(), myThat.getValue())) return false;
			if (Utils.differs(getBytes(), myThat.getBytes())) return false;
			return true;
		}
	}
	
	/* Encrypted Money */
	public class MoneyPair extends NumberPair<Money> {
		/* Pass-through constructors */
		public MoneyPair(Money  pValue) throws Exception { super(pValue); }
		public MoneyPair(String pValue) throws Exception { super(pValue); }
		public MoneyPair(byte[] pValue) throws Exception { super(pValue); }

		public MoneyPair(EncryptedItem<?>.MoneyPair pValue) { super(pValue); }

		/* Parse a money value */
		protected Money parseValue(String pValue) throws Exception {
			return new Money(pValue);
		}
	}
		
	/* Encrypted Units */
	public class UnitsPair extends NumberPair<Units> {
		/* Pass-through constructors */
		public UnitsPair(Units  pValue) throws Exception { super(pValue); }
		public UnitsPair(String pValue) throws Exception { super(pValue); }
		public UnitsPair(byte[] pValue) throws Exception { super(pValue); }

		public UnitsPair(EncryptedItem<?>.UnitsPair pValue) { super(pValue); }

		/* Parse a units value */
		protected Units parseValue(String pValue) throws Exception {
			return new Units(pValue);
		}
	}
	
	/* Encrypted Rate */
	public class RatePair extends NumberPair<Rate> {
		/* Pass-through constructors */
		public RatePair(Rate   pValue) throws Exception	{ super(pValue); }
		public RatePair(String pValue) throws Exception { super(pValue); }
		public RatePair(byte[] pValue) throws Exception { super(pValue); }

		/* Parse a rate value */
		protected Rate parseValue(String pValue) throws Exception {
			return new Rate(pValue);
		}
	}
		
	/* Encrypted Price */
	public class PricePair extends NumberPair<Price> {
		/* Pass-through constructors */
		public PricePair(Price  pValue) throws Exception	{ super(pValue); }
		public PricePair(String pValue) throws Exception 	{ super(pValue); }
		public PricePair(byte[] pValue) throws Exception 	{ super(pValue); }

		public PricePair(EncryptedItem<?>.PricePair pValue) { super(pValue); }

		/* Parse a price value */
		protected Price parseValue(String pValue) throws Exception {
			return new Price(pValue);
		}
	}
	
	/* Encrypted Dilution */
	public class DilutionPair extends NumberPair<Dilution> {
		/* Pass-through constructors */
		public DilutionPair(Dilution pValue) throws Exception { super(pValue); }
		public DilutionPair(String   pValue) throws Exception { super(pValue); }
		public DilutionPair(byte[]   pValue) throws Exception { super(pValue); }

		public DilutionPair(EncryptedItem<?>.DilutionPair pValue) { super(pValue); }

		/* Parse a dilution value */
		protected Dilution parseValue(String pValue) throws Exception {
			return new Dilution(pValue);
		}
	}
}