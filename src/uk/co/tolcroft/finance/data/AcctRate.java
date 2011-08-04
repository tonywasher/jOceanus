package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.ListStyle;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.Number.*;

public class AcctRate extends EncryptedItem<AcctRate> {
	/**
	 * The name of the object
	 */
	public static final String objName = "Rate";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";

	/* Local values */
	private int 		theAccountId	= -1;
	
	/* Access methods */
	public  Values     	getValues()     { return (Values)super.getValues(); }	
	public  Rate 		getRate()      	{ return getValues().getRateValue(); }
	public  byte[] 		getRateBytes() 	{ return getValues().getRateBytes(); }
	public  Rate 		getBonus()     	{ return getValues().getBonusValue(); }
	public  byte[] 		getBonusBytes() { return getValues().getBonusBytes(); }
	public  Date 		getDate()   	{ return getValues().getEndDate(); }
	public  Date 		getEndDate()   	{ return getValues().getEndDate(); }
	public  Account		getAccount()	{ return getValues().getAccount(); }
	private void        setAccount(Account pAccount)   {
		getValues().setAccount(pAccount); }

	/* Linking methods */
	public AcctRate     getBase() { return (AcctRate)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_ACCOUNT  = EncryptedItem.NUMFIELDS;
	public static final int FIELD_RATE     = EncryptedItem.NUMFIELDS+1;
	public static final int FIELD_BONUS    = EncryptedItem.NUMFIELDS+2;
	public static final int FIELD_ENDDATE  = EncryptedItem.NUMFIELDS+3;
	public static final int NUMFIELDS	   = EncryptedItem.NUMFIELDS+4;

	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	public String itemType() { return objName; }

	/**
	 * Obtain the number of fields for an item
	 * @return the number of fields
	 */
	public int	numFields() {return NUMFIELDS; }

	/**
	 * Determine the field name for a particular field
	 * @return the field name
	 */
	public static String	fieldName(int iField) {
		switch (iField) {
			case FIELD_ACCOUNT:		return "Account";
			case FIELD_RATE:		return "Rate";
			case FIELD_BONUS:		return "Bonus";
			case FIELD_ENDDATE:		return "EndDate";
			default:		  		return EncryptedItem.fieldName(iField);
		}
	}

	/**
	 * Determine the field name in a non-static fashion 
	 */
	public String getFieldName(int iField) { return fieldName(iField); }
	
	/**
	 * Format the value of a particular field as a table row
	 * @param iField the field number
	 * @param pValues the values to use
	 * @return the formatted field
	 */
	public String formatField(int iField, HistoryValues<AcctRate> pValues) {
		String 	myString = "";
		Values 	myValues = (Values)pValues;
		switch (iField) {
			case FIELD_ACCOUNT:
				if ((getAccount() == null) &&
					(theAccountId != -1))
					myString += "Id=" + theAccountId;
				else
					myString += Account.format(getAccount()); 
				break;
			case FIELD_RATE:	
				myString += Rate.format(myValues.getRateValue()); 
				break;
			case FIELD_BONUS:	
				myString += Rate.format(myValues.getBonusValue()); 
				break;
			case FIELD_ENDDATE:	
				myString += Date.format(myValues.getEndDate()); 
				break;
			default:
				myString += super.formatField(iField, pValues);
				break;
		}
		return myString;
	}
		
	/**
	 *	Construct a copy of a Rate Period
	 * 
	 * @param pPeriod The Period to copy 
	 */
	protected AcctRate(List pList, AcctRate pPeriod) {
		/* Set standard values */
		super(pList, pPeriod.getId());
		Values myValues = new Values(pPeriod.getValues());
		setValues(myValues);
		setControlKey(pPeriod.getControlKey());		
		ListStyle myOldStyle = pPeriod.getList().getStyle();

		/* Switch on the ListStyle */
		switch (pList.getStyle()) {
			case EDIT:
				/* If this is a view creation */
				if (myOldStyle == ListStyle.CORE) {
					/* Rate is based on the original element */
					setBase(pPeriod);
					pList.setNewId(this);				
					break;
				}
				
				/* Else this is a duplication so treat as new item */
				setId(0);
				pList.setNewId(this);				
				break;
			case CORE:
				/* Reset Id if this is an insert from a view */
				if (myOldStyle == ListStyle.EDIT) setId(0);
				pList.setNewId(this);				
				break;
			case UPDATE:
				setBase(pPeriod);
				setState(pPeriod.getState());
				break;
		}
	}

	/* Standard constructor for a newly inserted rate */
	public AcctRate(List pList) {
		super(pList, 0);
		Values myValues = new Values();
		setValues(myValues);
		setControlKey(pList.getData().getControl().getControlKey());
		setAccount(pList.theAccount);
		pList.setNewId(this);		
	}

	/* Standard constructor */
	private AcctRate(List       	pList,
				     int 		   	uAccountId,
				     java.util.Date	pEndDate, 
				     String		   	pRate,
				     String		   	pBonus) throws Exception {
		/* Initialise the item */
		super(pList, 0);
		
		/* Initialise the values */
		Values myValues = new Values();
		setValues(myValues);

		/* Record the Id */
		theAccountId = uAccountId;
		
		/* Look up the Account */
		DataSet myData 	= pList.getData();
		myValues.setAccount(myData.getAccounts().searchFor(uAccountId));
		if (getAccount() == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Account Id");
					
		/* Record the date */
		if (pEndDate != null)
			myValues.setEndDate(new Date(pEndDate));

		/* Set the encrypted objects */
		myValues.setRate(new RatePair(pRate));
		myValues.setBonus((pBonus == null) ? null : new RatePair(pBonus));
		
		/* Allocate the id */
		pList.setNewId(this);				
	}

	/* Encryption constructor */
	private AcctRate(List       	pList,
				     int			uId,
				     int			uControlId,
				     int 		   	uAccountId,
				     java.util.Date	pEndDate, 
				     byte[]		   	pRate,
				     byte[]		   	pBonus) throws Exception {
		/* Initialise the item */
		super(pList, uId);
		
		/* Initialise the values */
		Values myValues = new Values();
		setValues(myValues);

		/* Record the Id */
		theAccountId = uAccountId;
		
		/* Store the controlId */
		setControlKey(uControlId);
		
		/* Look up the Account */
		DataSet myData 	= pList.getData();
		myValues.setAccount(myData.getAccounts().searchFor(uAccountId));
		if (getAccount() == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Account Id");
					
		/* Record the date */
		if (pEndDate != null)
			myValues.setEndDate(new Date(pEndDate));

		/* Set the encrypted objects */
		myValues.setRate(new RatePair(pRate));
		myValues.setBonus((pBonus == null) ? null : new RatePair(pBonus));
		
		/* Allocate the id */
		pList.setNewId(this);				
	}

	/**
	 * Compare this rate to another to establish equality.
	 * 
	 * @param pThat The Rate to compare to
	 * @return <code>true</code> if the rate is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a Rate */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a Rate */
		AcctRate myThat = (AcctRate)pThat;
		
		/* Check for equality on id */
		if (getId() != myThat.getId()) return false;
		
		/* Compare the changeable values */
		return getValues().histEquals(myThat.getValues());
	}

	/**
	 * Compare this rate to another to establish sort order. 
	 * @param pThat The Rate to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		int iDiff;
		
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a Rate */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as a Rate */
		AcctRate myThat = (AcctRate)pThat;

		/* Compare the accounts */
		iDiff = getAccount().compareTo(myThat.getAccount());
		if (iDiff != 0) return iDiff;

		/* If the date differs */
		if (this.getEndDate() != myThat.getEndDate()) {
			/* Handle null dates */
			if (this.getEndDate() == null) return 1;
			if (myThat.getEndDate() == null) return -1;
			
			/* Compare the dates */
			iDiff = getEndDate().compareTo(myThat.getEndDate());
			if (iDiff != 0) return iDiff;
		}
		
		/* Compare the IDs */
		iDiff = (int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}

	/**
	 * Validate the rate
	 */
	public void validate() {
		AcctRate 	myCurr;
		Date 		myDate = getEndDate();
		List 		myList = (List)getList();
		DataSet		mySet  = myList.getData();

		/* If the date is null then we must be the last element for the account */
		if ((myDate == null) || (myDate.isNull())) {
			/* Access the next element (if any) */
			myCurr = myList.peekNext(this);
			
			/* Ignore if this item doesn't belong to the account */
			if ((myCurr != null) &&
			    (!Account.differs(myCurr.getAccount(), getAccount())))
				myCurr = null;

			/* If we have a later element then error */
			if (myCurr != null)
				addError("Null date is only allowed on last date", FIELD_ENDDATE);
		}

		/* If we have a date */
		if (myDate != null) {
			/* The date must be unique for this account */
			if (myList.countInstances(myDate, getAccount()) > 1) {
				addError("Rate Date must be unique", FIELD_ENDDATE);
			}

			/* The date must be in-range (unless it is the last one) */
			if ((myList.peekNext(this) != null) && 
				(mySet.getDateRange().compareTo(myDate) != 0)) {
				addError("Date must be within range", FIELD_ENDDATE);
			}
		}

		/* The rate must be non-zero */
		if ((getRate() == null) || 
			(!getRate().isPositive())) {
			addError("Rate must be positive", FIELD_RATE);
		}

		/* The bonus rate must be non-zero if it exists */
		if ((getBonus() != null) &&
			((!getBonus().isNonZero()) ||
			 (!getBonus().isPositive()))) {
			addError("Bonus Rate must be non-Zero and positive", FIELD_BONUS);
		}						

		/* Set validation flag */
		if (!hasErrors()) setValidEdit();
	}

	/**
	 * Set a new rate 
	 * @param pRate the rate 
	 */
	public void setRate(Rate pRate) throws Exception {
		if (pRate != null) 	getValues().setRate(new RatePair(pRate));
		else 				getValues().setRate(null);
	}

	/**
	 * Set a new bonus 
	 * @param pBonus the rate 
	 */
	public void setBonus(Rate pBonus) throws Exception {
		if (pBonus != null) getValues().setBonus(new RatePair(pBonus));
		else 				getValues().setBonus(null);
	}

	/**
	 * Set a new date 
	 * 
	 * @param pDate the new date 
	 */
	public void setEndDate(Date pDate) {
		getValues().setEndDate(new Date(pDate));
	}

	/**
	 * Update Rate from a Rate extract  
	 * @param pRate the updated item 
	 * @return whether changes have been made
	 */
	public boolean applyChanges(DataItem<?> pRate) {
		AcctRate myRate 	=  (AcctRate)pRate;
		Values	 myValues	= getValues();
		Values	 myNew		= myRate.getValues();
		boolean  bChanged	= false;

		/* Store the current detail into history */
		pushHistory();

		/* Update the rate if required */
		if (differs(myValues.getRate(), myNew.getRate())) 
			myValues.setRate(myNew.getRate());

		/* Update the bonus if required */
		if (differs(myValues.getBonus(), myNew.getBonus())) 
			myValues.setBonus(myNew.getBonus());

		/* Update the date if required */
		if (Date.differs(getEndDate(), myRate.getEndDate())) 
			setEndDate(myRate.getEndDate());

		/* Check for changes */
		if (checkForHistory()) {
			/* Mark as changed */
			setState(DataState.CHANGED);
			bChanged = true;
		}
		
		/* Return to caller */
		return bChanged;
	}
	
	public static class List  	extends EncryptedList<AcctRate> {
		/* Members */
		private Account	theAccount	= null;

		/** 
		 * Construct an empty CORE rate list
	 	 * @param pData the DataSet for the list
		 */
		protected List(DataSet pData) { 
			super(AcctRate.class, pData);
		}

		/** 
		 * Construct an empty generic rate list
	 	 * @param pData the DataSet for the list
		 * @param pStyle the style of the list 
		 */
		protected List(DataSet pData, ListStyle pStyle) { 
			super(AcctRate.class, pData, pStyle);
		}

		/** 
		 * Construct a generic rate list
		 * @param pList the source rate list 
		 * @param pStyle the style of the list 
		 */
		public List(List pList, ListStyle pStyle) {
			super(AcctRate.class, pList, pStyle);
		}

		/** 
		 * Construct a difference rate list
		 * @param pNew the new Rate list 
		 * @param pOld the old Rate list 
		 */
		protected List(List pNew, List pOld) { 
			super(pNew, pOld);
		}

		/**
		 * Construct an edit extract of a Rate list
		 * 
		 * @param pList      The list to extract from
		 * @param pAccount	 The account to extract rates for 
		 */
		public List(List 	pList,
				  	Account pAccount) {
			/* Make this list the correct style */
			super(AcctRate.class, pList.getData(), ListStyle.EDIT);

			/* Local variables */
			ListIterator 	myIterator;
			AcctRate 			myCurr;
			AcctRate 			myItem;

			/* Store the account */
			theAccount = pAccount;

			/* Access the list iterator */
			myIterator = pList.listIterator(true);
			
			/* Loop through the list */
			while ((myCurr = myIterator.next()) != null) {
				/* If this item belongs to the account */
				if (!Account.differs(myCurr.getAccount(), pAccount)) {
					/* Copy the item */
					myItem = new AcctRate(this, myCurr);
					add(myItem);
				}
			}
		}

		/** 
		 * Clone a Rate list
		 * @return the cloned list
		 */
		protected List cloneIt() { return new List(this, ListStyle.DIFFER); }

		/* Is this list locked */
		public boolean isLocked() { return ((theAccount != null) && (theAccount.isLocked())); }

		/**
		 * Add a new item to the core list
		 * 
		 * @param pRate item
		 * @return the newly added item
		 */
		public AcctRate addNewItem(DataItem<?> pRate) {
			AcctRate myRate = new AcctRate(this, (AcctRate)pRate);
			add(myRate);
			return myRate;
		}

		/**
		 * Add a new item to the edit list
		 *
		 * @param isCredit - ignored
		 */
		public AcctRate addNewItem(boolean isCredit) {
			AcctRate myRate = new AcctRate(this);
			myRate.setAccount(theAccount);
			add(myRate);
			return myRate;
		}

		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }

		/**
		 * Count the instances of a date
		 * 
		 * @param pDate the date
		 * @return The Item if present (or null)
		 */
		protected int countInstances(Date 		pDate,
									 Account    pAccount) {
			ListIterator 	myIterator;
			AcctRate 			myCurr;
			int  			iDiff;
			int  			iCount = 0;

			/* Access the list iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				iDiff = pDate.compareTo(myCurr.getEndDate());
				if (iDiff == 0) iDiff = pAccount.compareTo(myCurr.getAccount());
				if (iDiff == 0) iCount++;
			}

			/* Return to caller */
			return iCount;
		}	

		/**
		 *  Mark active rates
		 */
		protected void markActiveRates() {
			ListIterator 	myIterator;
			AcctRate 			myCurr;

			/* Access the list iterator */
			myIterator = listIterator(true);
			
			/* Loop through the Rates */
			while ((myCurr = myIterator.next()) != null) {
				/* mark the account referred to */
				myCurr.getAccount().touchRate(); 
			}
		}

		/**
		 *  Obtain the most relevant rate for an Account and a Date
		 *   @param pAccount the Account for which to get the rate
		 *   @param pDate the date from which a rate is required
		 *   @return The relevant Rate record 
		 */
		public AcctRate getLatestRate(Account   pAccount, 
								  Date 		pDate) {
			ListIterator 	myIterator;
			AcctRate    		myRate = null;
			AcctRate    		myCurr;
			Date 			myDate;

			/* Access the list iterator */
			myIterator = listIterator();
			
			/* Loop through the Rates */
			while ((myCurr = myIterator.next()) != null) {
				/* Skip records that do not belong to this account */
				if (Account.differs(myCurr.getAccount(), pAccount))
					continue;

				/* Access the date */
				myDate = myCurr.getDate();

				/* break loop if we have the correct record */
				if ((myDate == null) ||
					(myDate.isNull()) ||
					(myDate.compareTo(pDate) >= 0)) {
					myRate = myCurr; 
					break; 
				}
			}

			/* Return the rate */
			return myRate;
		}
		
		/**
		 *  Allow a rate to be added 
		 */
		public void addItem(String   		pAccount,
	            			String   		pRate,
	            			java.util.Date  pDate,
				            String   		pBonus) throws Exception {
			Account     	myAccount;
			Account.List 	myAccounts;
			
			/* Access the Accounts */
			myAccounts = getData().getAccounts();
			
			/* Look up the Account */
			myAccount = myAccounts.searchFor(pAccount);
			if (myAccount == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Rate on [" + 
			                        Date.format(new Date(pDate)) +
			                        "] has invalid Account [" +
			                        pAccount + "]");
				
			/* Add the rate */
			addItem(myAccount.getId(),
					pRate,
					pDate,
					pBonus);
		}
		
		/**
		 *  Allow a rate to be added
		 */
		public void addItem(int  	 		uAccountId,
	            			String   		pRate,
	            			java.util.Date  pDate,
				            String   		pBonus) throws Exception {
			AcctRate     	myRate;
			
			/* Create the period */
			myRate    = new AcctRate(this, uAccountId,
					                 pDate, pRate, pBonus);
				
			/* Validate the rate */
			myRate.validate();

			/* Handle validation failure */
			if (myRate.hasErrors()) 
				throw new Exception(ExceptionClass.VALIDATE,
									myRate,
									"Failed validation");
				
			/* Add to the list */
			add(myRate);
		}			

		/**
		 *  Allow a rate to be added
		 */
		public void addItem(int     		uId,
							int				uControlId,
							int  	 		uAccountId,
	            			byte[]   		pRate,
	            			java.util.Date  pDate,
				            byte[]   		pBonus) throws Exception {
			AcctRate     	myRate;
			
			/* Create the period */
			myRate    = new AcctRate(this, uId, uControlId, uAccountId,
					                 pDate, pRate, pBonus);
				
			/* Check that this RateId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
						            myRate,
			  			            "Duplicate RateId");
			 
			/* Validate the rate */
			myRate.validate();

			/* Handle validation failure */
			if (myRate.hasErrors()) 
				throw new Exception(ExceptionClass.VALIDATE,
									myRate,
									"Failed validation");
				
			/* Add to the list */
			add(myRate);
		}			
	}

	/* RateValues */
	public class Values extends EncryptedValues {
		private RatePair	theRate      = null;
		private RatePair	theBonus     = null;
		private Date       	theEndDate   = null;
		private Account    	theAccount   = null;

		/* Access methods */
		public RatePair		getRate()       { return theRate; }
		public RatePair		getBonus()      { return theBonus; }
		public Date       	getEndDate()    { return theEndDate; }
		public Account		getAccount()    { return theAccount; }
		public Rate  		getRateValue()  { return getPairValue(theRate); }
		public Rate  		getBonusValue() { return getPairValue(theBonus); }
		public byte[]  		getRateBytes()  { return getPairBytes(theRate); }
		public byte[]  		getBonusBytes() { return getPairBytes(theBonus); }

		public void setRate(RatePair pRate) {
			theRate      = pRate; }
		public void setBonus(RatePair pBonus) {
			theBonus     = pBonus; }
		public void setEndDate(Date pEndDate) {
			theEndDate   = pEndDate; }
		public void setAccount(Account pAccount) {
			theAccount   = pAccount; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) { copyFrom(pValues); }

		/* Check whether this object is equal to that passed */
		public boolean histEquals(HistoryValues<AcctRate> pCompare) {
			Values myValues = (Values)pCompare;
			if (!super.histEquals(pCompare))					  	return false;
			if (differs(theRate,  myValues.theRate))    			return false;
			if (differs(theBonus, myValues.theBonus))   			return false;
			if (Date.differs(theEndDate, 	myValues.theEndDate)) 	return false;
			if (Account.differs(theAccount, myValues.theAccount)) 	return false;
			return true;
		}

		/* Copy values */
		public HistoryValues<AcctRate> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			Values myValues = (Values)pSource;
			super.copyFrom(myValues);
			theRate      = myValues.getRate();
			theBonus     = myValues.getBonus();
			theEndDate   = myValues.getEndDate();
			theAccount   = myValues.getAccount();
		}
		public boolean	fieldChanged(int fieldNo, HistoryValues<AcctRate> pOriginal) {
			Values 		pValues = (Values)pOriginal;	
			boolean		bResult = false;
			switch (fieldNo) {
				case FIELD_RATE:
					bResult = (differs(theRate,    pValues.theRate));
					break;
				case FIELD_BONUS:
					bResult = (differs(theBonus,   pValues.theBonus));
					break;
				case FIELD_ENDDATE:
					bResult = (Date.differs(theEndDate, pValues.theEndDate));
					break;
				case FIELD_ACCOUNT:
					bResult = (Account.differs(theAccount,   pValues.theAccount));
					break;
				default:
					bResult = super.fieldChanged(fieldNo, pValues);
					break;
			}
			return bResult;
		}

		/**
		 * Ensure encryption after security change
		 */
		protected void applySecurity() throws Exception {
			/* Apply the encryption */
			theRate.encryptPair();
			if (theBonus != null) theBonus.encryptPair();
		}		
		
		/**
		 * Adopt encryption from base
		 * @param pBase the Base values
		 */
		protected void adoptSecurity(ControlKey pControl, EncryptedValues pBase) throws Exception {
			Values myBase = (Values)pBase;

			/* Apply the encryption */
			theRate.encryptPair(myBase.getRate());
			if (theBonus != null) theBonus.encryptPair(myBase.getBonus());
		}		
	}
}
