package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.Number;

public class Rate extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "Rate";

	/* Local values */
	private long 	theAccountId	= -1;
	
	/* Access methods */
	public  Values     	getObj()       	{ return (Values)super.getObj(); }	
	public  Number.Rate getRate()      	{ return getObj().getRate(); }
	public  Number.Rate getBonus()     	{ return getObj().getBonus(); }
	public  Date 		getDate()   	{ return getObj().getEndDate(); }
	public  Date 		getEndDate()   	{ return getObj().getEndDate(); }
	public  Account		getAccount()	{ return getObj().getAccount(); }
	private void        setAccount(Account pAccount)   {
		getObj().setAccount(pAccount); }

	/* Linking methods */
	public Rate     getBase() { return (Rate)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_ID       = 0;
	public static final int FIELD_ACCOUNT  = 1;
	public static final int FIELD_RATE     = 2;
	public static final int FIELD_BONUS    = 3;
	public static final int FIELD_ENDDATE  = 4;
	public static final int NUMFIELDS	   = 5;

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
	public String	fieldName(int iField) {
		switch (iField) {
			case FIELD_ID:			return "ID";
			case FIELD_ACCOUNT:		return "Account";
			case FIELD_RATE:		return "Rate";
			case FIELD_BONUS:		return "Bonus";
			case FIELD_ENDDATE:		return "EndDate";
			default:		  		return super.fieldName(iField);
		}
	}

	/**
	 * Format the value of a particular field as a table row
	 * @param iField the field number
	 * @param pObj the values to use
	 * @return the formatted field
	 */
	public String formatField(int iField, histObject pObj) {
		String 	myString = "<tr><td>" + fieldName(iField) + "</td><td>";
		Values 	myObj 	 = (Values)pObj;
		switch (iField) {
			case FIELD_ID: 		
				myString += getId(); 
				break;
			case FIELD_ACCOUNT:
				if ((getAccount() == null) &&
					(theAccountId != -1))
					myString += "Id=" + theAccountId;
				else
					myString += Utils.formatAccount(getAccount()); 
				break;
			case FIELD_RATE:	
				myString += Utils.formatRate(myObj.getRate()); 
				break;
			case FIELD_BONUS:	
				myString += Utils.formatRate(myObj.getBonus()); 
				break;
			case FIELD_ENDDATE:	
				myString += Utils.formatDate(myObj.getEndDate()); 
				break;
		}
		return myString + "</td></tr>";
	}
		
	/**
	 *	Construct a copy of a Rate Period
	 * 
	 * @param pPeriod The Period to copy 
	 */
	protected Rate(List pList, Rate pPeriod) {
		/* Set standard values */
		super(pList, pPeriod.getId());
		Values myObj = new Values(pPeriod.getObj());
		setObj(myObj);

		/* Switch on the LinkStyle */
		switch (pList.getStyle()) {
			case CORE:
				pList.setNewId(this);				
				break;
			case EDIT:
				setBase(pPeriod);
				setState(DataState.CLEAN);
				break;
			case UPDATE:
				setBase(pPeriod);
				setState(pPeriod.getState());
				break;
		}
	}

	/* Standard constructor for a newly inserted rate */
	public Rate(List pList) {
		super(pList, 0);
		Values myObj = new Values();
		setObj(myObj);
		setAccount(pList.theAccount);
		setState(DataState.NEW);
	}

	/* Standard constructor */
	private Rate(List       	pList,
				 long           uId,
				 long 		   	uAccountId,
				 java.util.Date	pEndDate, 
				 String		   	pRate,
				 String		   	pBonus) throws Exception {
		/* Initialise the item */
		super(pList, uId);
		
		/* Initialise the values */
		Values myObj = new Values();
		setObj(myObj);

		/* Record the Id */
		theAccountId = uAccountId;
		
		/* Look up the Account */
		myObj.setAccount(pList.theData.getAccounts().searchFor(uAccountId));
		if (getAccount() == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Account Id");
					
		/* Record the date */
		if (pEndDate != null)
			myObj.setEndDate(new Date(pEndDate));
		
		/* Record the rate */
		Number.Rate myRate = Number.Rate.Parse(pRate);
		if (myRate == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Rate: " + pRate);
		myObj.setRate(myRate);

		/* If we have a bonus */
		if (pBonus != null) {
			/* Record the bonus */
			myRate = Number.Rate.Parse(pBonus);
			if (myRate == null) 
				throw new Exception(ExceptionClass.DATA,
									this,
									"Invalid Bonus: " + pBonus);
			myObj.setBonus(myRate);
		}
		
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
		Rate myRate = (Rate)pThat;
		
		/* Check for equality */
		if (getId() != myRate.getId()) return false;
		if (Utils.differs(getAccount(), myRate.getAccount())) 	return false;
		if (Utils.differs(getEndDate(), myRate.getEndDate())) 	return false;
		if (Utils.differs(getRate(),    myRate.getRate())) 		return false;
		if (Utils.differs(getBonus(),  	myRate.getBonus()))		return false;
		return true;
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
		Rate myThat = (Rate)pThat;

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
		Rate 	myCurr;
		Date 	myDate = getEndDate();
		List 	myList = (List)getList();
		DataSet	mySet  = myList.getData();

		/* If the date is null then we must be the last element for the account */
		if ((myDate == null) || (myDate.isNull())) {
			/* Access the next element (if any) */
			myCurr = myList.peekNext(this);
			
			/* Ignore if this item doesn't belong to the account */
			if ((myCurr != null) &&
			    (!Utils.differs(myCurr.getAccount(), getAccount())))
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
	 * 
	 * @param pRate the rate 
	 */
	public void setRate(Number.Rate pRate) {
		getObj().setRate((pRate == null) ? null : new Number.Rate(pRate));
	}

	/**
	 * Set a new bonus 
	 * 
	 * @param pBonus the rate 
	 */
	public void setBonus(Number.Rate pBonus) {
		getObj().setBonus((pBonus == null) ? null : new Number.Rate(pBonus));
	}

	/**
	 * Set a new date 
	 * 
	 * @param pDate the new date 
	 */
	public void setEndDate(Date pDate) {
		getObj().setEndDate(new Date(pDate));
	}

	/**
	 * Update Rate from a Rate extract 
	 * 
	 * @param pRate the updated item 
	 */
	public void applyChanges(DataItem pRate) {
		Rate myRate =  (Rate)pRate;

		/* Store the current detail into history */
		pushHistory();

		/* Update the rate if required */
		if (Utils.differs(getRate(), myRate.getRate())) 
			setRate(myRate.getRate());

		/* Update the bonus if required */
		if (Utils.differs(getBonus(), myRate.getBonus())) 
			setBonus(myRate.getBonus());

		/* Update the date if required */
		if (Utils.differs(getEndDate(), myRate.getEndDate())) 
			setEndDate(myRate.getEndDate());

		/* Check for changes */
		if (checkForHistory()) setState(DataState.CHANGED);
	}

	public static class List  	extends DataList<Rate> {
		/* Members */
		private Account	theAccount	= null;
		private DataSet	theData		= null;
		public 	DataSet getData()	{ return theData; }

		/** 
		 * Construct an empty CORE rate list
	 	 * @param pData the DataSet for the list
		 */
		protected List(DataSet pData) { 
			super(ListStyle.CORE, false);
			theData = pData;
		}

		/** 
		 * Construct an empty generic rate list
	 	 * @param pData the DataSet for the list
		 * @param pStyle the style of the list 
		 */
		protected List(DataSet pData, ListStyle pStyle) { 
			super(pStyle, false);
			theData = pData;
		}

		/** 
		 * Construct a generic rate list
		 * @param pList the source rate list 
		 * @param pStyle the style of the list 
		 */
		public List(List pList, ListStyle pStyle) {
			super(pList, pStyle);
			theData = pList.getData();
		}

		/** 
		 * Construct a difference rate list
		 * @param pNew the new Rate list 
		 * @param pOld the old Rate list 
		 */
		protected List(List pNew, List pOld) { 
			super(pNew, pOld);
			theData = pNew.getData();
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
			super(ListStyle.EDIT, false);
			theData = pList.getData();

			/* Local variables */
			ListIterator 	myIterator;
			Rate 			myCurr;
			Rate 			myItem;

			/* Store the account */
			theAccount = pAccount;

			/* Access the list iterator */
			myIterator = pList.listIterator(true);
			
			/* Loop through the list */
			while ((myCurr = myIterator.next()) != null) {
				/* If this item belongs to the account */
				if (!Utils.differs(myCurr.getAccount(), pAccount)) {
					/* Copy the item */
					myItem = new Rate(this, myCurr);
					myItem.addToList();
				}
			}
		}

		/** 
		 * Clone a Rate list
		 * @return the cloned list
		 */
		protected List cloneIt() { return new List(this, ListStyle.CORE); }

		/* Is this list locked */
		public boolean isLocked() { return ((theAccount != null) && (theAccount.isLocked())); }

		/**
		 * Add a new item to the core list
		 * 
		 * @param pRate item
		 * @return the newly added item
		 */
		public DataItem addNewItem(DataItem pRate) {
			Rate myRate = new Rate(this, (Rate)pRate);
			myRate.addToList();
			return myRate;
		}

		/**
		 * Add a new item to the edit list
		 *
		 * @param isCredit - ignored
		 */
		public void addNewItem(boolean        isCredit) {
			Rate myRate = new Rate(this);
			myRate.setAccount(theAccount);
			myRate.addToList();
		}

		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }

		/**
		 * Count the instances of a date
		 * 
		 * @param pDate the date
		 * @return The Item if present (or null)
		 */
		protected int countInstances(Date 		pDate,
									 Account    pAccount) {
			ListIterator 	myIterator;
			Rate 			myCurr;
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
			Rate 			myCurr;

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
		public Rate getLatestRate(Account   pAccount, 
								  Date 		pDate) {
			ListIterator 	myIterator;
			Rate    		myRate = null;
			Rate    		myCurr;
			Date 			myDate;

			/* Access the list iterator */
			myIterator = listIterator();
			
			/* Loop through the Rates */
			while ((myCurr = myIterator.next()) != null) {
				/* Skip records that do not belong to this account */
				if (Utils.differs(myCurr.getAccount(), pAccount))
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
		public void addItem(long     		uId,
							String   		pAccount,
	            			String   		pRate,
	            			java.util.Date  pDate,
				            String   pBonus) throws Exception {
			Account     	myAccount;
			Account.List 	myAccounts;
			
			/* Access the Accounts */
			myAccounts = theData.getAccounts();
			
			/* Look up the Account */
			myAccount = myAccounts.searchFor(pAccount);
			if (myAccount == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Rate on [" + 
			                        Utils.formatDate(new Date(pDate)) +
			                        "] has invalid Account [" +
			                        pAccount + "]");
				
			/* Add the rate */
			addItem(uId,
					myAccount.getId(),
					pRate,
					pDate,
					pBonus);
		}
		
		/**
		 *  Allow a rate to be added
		 */
		public void addItem(long     		uId,
							long  	 		uAccountId,
	            			String   		pRate,
	            			java.util.Date  pDate,
				            String   		pBonus) throws Exception {
			Rate     	myRate;
			
			/* Create the period */
			myRate    = new Rate(this, uId, uAccountId,
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
			myRate.addToList();
		}			
	}

	/* RateValues */
	public class Values implements histObject {
		private Number.Rate theRate      = null;
		private Number.Rate theBonus     = null;
		private Date       	theEndDate   = null;
		private Account    	theAccount   = null;

		/* Access methods */
		public Number.Rate  getRate()      { return theRate; }
		public Number.Rate  getBonus()     { return theBonus; }
		public Date       	getEndDate()   { return theEndDate; }
		public Account		getAccount()   { return theAccount; }

		public void setRate(Number.Rate pRate) {
			theRate      = pRate; }
		public void setBonus(Number.Rate pBonus) {
			theBonus     = pBonus; }
		public void setEndDate(Date pEndDate) {
			theEndDate   = pEndDate; }
		public void setAccount(Account pAccount) {
			theAccount   = pAccount; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) {
			theRate      = pValues.getRate();
			theBonus     = pValues.getBonus();
			theEndDate   = pValues.getEndDate();
			theAccount   = pValues.getAccount();
		}

		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (Utils.differs(theRate,    pValues.theRate))    return false;
			if (Utils.differs(theBonus,   pValues.theBonus))   return false;
			if (Utils.differs(theEndDate, pValues.theEndDate)) return false;
			if (Utils.differs(theAccount, pValues.theAccount)) return false;
			return true;
		}

		/* Copy values */
		public void    copyFrom(histObject pSource) {
			Values myValues = (Values)pSource;
			copyFrom(myValues);
		}
		public histObject copySelf() {
			return new Values(this);
		}
		public void    copyFrom(Values pValues) {
			theRate      = pValues.getRate();
			theBonus     = pValues.getBonus();
			theEndDate   = pValues.getEndDate();
			theAccount   = pValues.getAccount();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values 		pValues = (Values)pOriginal;	
			boolean		bResult = false;
			switch (fieldNo) {
				case FIELD_RATE:
					bResult = (Utils.differs(theRate,    pValues.theRate));
					break;
				case FIELD_BONUS:
					bResult = (Utils.differs(theBonus,   pValues.theBonus));
					break;
				case FIELD_ENDDATE:
					bResult = (Utils.differs(theEndDate, pValues.theEndDate));
					break;
				case FIELD_ACCOUNT:
					bResult = (Utils.differs(theAccount,   pValues.theAccount));
					break;
			}
			return bResult;
		}
	}
}
