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
package uk.co.tolcroft.finance.data;

import java.util.Date;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.*;
import uk.co.tolcroft.models.Decimal.*;
import uk.co.tolcroft.models.data.ControlKey;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.EncryptedItem;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.help.DebugDetail;

public class AcctRate extends EncryptedItem<AcctRate> {
	/**
	 * The name of the object
	 */
	public static final String objName = "Rate";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";
	
	/* Access methods */
	public  Values     	getValues()     { return (Values)super.getValues(); }	
	public  Rate 		getRate()      	{ return getValues().getRateValue(); }
	public  byte[] 		getRateBytes() 	{ return getValues().getRateBytes(); }
	public  Rate 		getBonus()     	{ return getValues().getBonusValue(); }
	public  byte[] 		getBonusBytes() { return getValues().getBonusBytes(); }
	public  DateDay 	getDate()   	{ return getValues().getEndDate(); }
	public  DateDay 	getEndDate()   	{ return getValues().getEndDate(); }
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
	 * @param pDetail the debug detail
	 * @param iField the field number
	 * @param pValues the values to use
	 * @return the formatted field
	 */
	public String formatField(DebugDetail pDetail, int iField, HistoryValues<AcctRate> pValues) {
		String 	myString = "";
		Values 	myValues = (Values)pValues;
		switch (iField) {
			case FIELD_ACCOUNT:
				if ((myValues.getAccount() == null) &&
					(myValues.getAccountId() != null))
					myString += "Id=" + myValues.getAccountId();
				else
					myString += Account.format(myValues.getAccount()); 
				myString = pDetail.addDebugLink(myValues.getAccount(), myString);
				break;
			case FIELD_RATE:	
				myString += Rate.format(myValues.getRateValue()); 
				break;
			case FIELD_BONUS:	
				myString += Rate.format(myValues.getBonusValue()); 
				break;
			case FIELD_ENDDATE:	
				myString += DateDay.format(myValues.getEndDate()); 
				break;
			default:
				myString += super.formatField(pDetail, iField, pValues);
				break;
		}
		return myString;
	}
		
	/**
	 * Get an initial set of values 
	 * @return an initial set of values 
	 */
	protected HistoryValues<AcctRate> getNewValues() { return new Values(); }
	
	/**
	 *	Construct a copy of a Rate Period
	 * 
	 * @param pPeriod The Period to copy 
	 */
	protected AcctRate(List pList, AcctRate pPeriod) {
		/* Set standard values */
		super(pList, pPeriod.getId());
		Values myValues = getValues();
		myValues.copyFrom(pPeriod.getValues());
		ListStyle myOldStyle = pPeriod.getStyle();

		/* Switch on the ListStyle */
		switch (getStyle()) {
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
			case CLONE:
				reBuildLinks(pList.getData());
			case COPY:
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

	/* Insert constructor */
	public AcctRate(List pList) {
		super(pList, 0);
		setAccount(pList.theAccount);
		pList.setNewId(this);		
	}

	/* Extract constructor */
	private AcctRate(List   pList,
					 int	uId,
				     int 	uAccountId,
				     Date	pEndDate, 
				     String	pRate,
				     String	pBonus) throws ModelException {
		/* Initialise the item */
		super(pList, uId);
		
		/* Initialise the values */
		Values myValues = getValues();

		/* Record the Id */
		myValues.setAccountId(uAccountId);
		
		/* Look up the Account */
		FinanceData myData 		= pList.getData();
		Account 	myAccount 	= myData.getAccounts().searchFor(uAccountId);
		if (myAccount == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid Account Id");
		myValues.setAccount(myAccount);
					
		/* Record the date */
		if (pEndDate != null)
			myValues.setEndDate(new DateDay(pEndDate));

		/* Set the encrypted objects */
		myValues.setRate(new RatePair(pRate));
		myValues.setBonus((pBonus == null) ? null : new RatePair(pBonus));
		
		/* Allocate the id */
		pList.setNewId(this);				
	}

	/* Encryption constructor */
	private AcctRate(List   pList,
				     int	uId,
				     int	uControlId,
				     int 	uAccountId,
				     Date	pEndDate, 
				     byte[]	pRate,
				     byte[]	pBonus) throws ModelException {
		/* Initialise the item */
		super(pList, uId);
		
		/* Initialise the values */
		Values myValues = getValues();

		/* Record the Id */
		myValues.setAccountId(uAccountId);
		
		/* Store the controlId */
		setControlKey(uControlId);
		
		/* Look up the Account */
		FinanceData myData	= pList.getData();
		Account myAccount 	= myData.getAccounts().searchFor(uAccountId);
		if (myAccount == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid Account Id");
		myValues.setAccount(myAccount);
					
		/* Record the date */
		if (pEndDate != null)
			myValues.setEndDate(new DateDay(pEndDate));

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
		return getValues().histEquals(myThat.getValues()).isIdentical();
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

		/* If the date differs */
		if (this.getEndDate() != myThat.getEndDate()) {
			/* Handle null dates */
			if (this.getEndDate() == null) return 1;
			if (myThat.getEndDate() == null) return -1;
			
			/* Compare the dates */
			iDiff = getEndDate().compareTo(myThat.getEndDate());
			if (iDiff != 0) return iDiff;
		}
		
		/* Compare the accounts */
		iDiff = getAccount().compareTo(myThat.getAccount());
		if (iDiff != 0) return iDiff;

		/* Compare the IDs */
		iDiff = (int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}

	/**
	 * Rebuild Links to partner data
	 * @param pData the DataSet
	 */
	protected void reBuildLinks(FinanceData pData) {
		/* Update the Encryption details */
		super.reBuildLinks(pData);
		
		/* Access Accounts */
		Account.List myAccounts = pData.getAccounts();
		
		/* Update to use the local copy of the Accounts */
		Values 	myValues   	= getValues();
		Account	myAct		= myValues.getAccount();
		Account	myNewAct 	= myAccounts.searchFor(myAct.getId());
		myValues.setAccount(myNewAct);
	}

	/**
	 * Validate the rate
	 */
	public void validate() {
		AcctRate 	myCurr;
		DateDay 	myDate = getEndDate();
		List 		myList = (List)getList();
		FinanceData	mySet  = myList.getData();

		/* If the date is null then we must be the last element for the account */
		if ((myDate == null) || (myDate.isNull())) {
			/* Access the next element (if any) */
			myCurr = myList.peekNext(this);
			
			/* Ignore if this item doesn't belong to the account */
			if ((myCurr != null) &&
			    (Account.differs(myCurr.getAccount(), getAccount()).isDifferent()))
				myCurr = null;

			/* If we have a later element then error */
			if (myCurr != null)
				addError("Null date is only allowed on last date", FIELD_ENDDATE);
		}

		/* If we have a date */
		else if (myDate != null) {
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
	public void setRate(Rate pRate) throws ModelException {
		if (pRate != null) 	getValues().setRate(new RatePair(pRate));
		else 				getValues().setRate(null);
	}

	/**
	 * Set a new bonus 
	 * @param pBonus the rate 
	 */
	public void setBonus(Rate pBonus) throws ModelException {
		if (pBonus != null) getValues().setBonus(new RatePair(pBonus));
		else 				getValues().setBonus(null);
	}

	/**
	 * Set a new date 
	 * 
	 * @param pDate the new date 
	 */
	public void setEndDate(DateDay pDate) {
		getValues().setEndDate(new DateDay(pDate));
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
		if (differs(myValues.getRate(), myNew.getRate()).isDifferent()) 
			myValues.setRate(myNew.getRate());

		/* Update the bonus if required */
		if (differs(myValues.getBonus(), myNew.getBonus()).isDifferent()) 
			myValues.setBonus(myNew.getBonus());

		/* Update the date if required */
		if (DateDay.differs(getEndDate(), myRate.getEndDate()).isDifferent()) 
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
	
	public static class List  	extends EncryptedList<List, AcctRate> {
		/* Members */
		private Account	theAccount	= null;

		/* Access Extra Variables correctly */
		public FinanceData 	getData() 		{ return (FinanceData) super.getData(); }
		public Account 		getAccount() 	{ return theAccount; }
		
		/** 
		 * Construct an empty CORE rate list
	 	 * @param pData the DataSet for the list
		 */
		protected List(FinanceData pData) { 
			super(List.class, AcctRate.class, pData);
		}

		/**
		 * Constructor for a cloned List
		 * @param pSource the source List
		 */
		private List(List pSource) { 
			super(pSource);
		}
		
		/**
		 * Construct an update extract for the List.
		 * @return the update Extract
		 */
		private List getExtractList(ListStyle pStyle) {
			/* Build an empty Extract List */
			List myList = new List(this);
			
			/* Obtain underlying updates */
			myList.populateList(pStyle);
			
			/* Return the list */
			return myList;
		}

		/* Obtain extract lists. */
		public List getUpdateList() { return getExtractList(ListStyle.UPDATE); }
		public List getEditList() 	{ return null; }
		public List getShallowCopy() 	{ return getExtractList(ListStyle.COPY); }
		public List getDeepCopy(DataSet<?> pDataSet)	{ 
			/* Build an empty Extract List */
			List myList = new List(this);
			myList.setData(pDataSet);
			
			/* Obtain underlying clones */
			myList.populateList(ListStyle.CLONE);
			myList.setStyle(ListStyle.CORE);
			
			/* Return the list */
			return myList;
		}

		/** 
		 * Construct a difference Rate list
		 * @param pNew the new Rate list 
		 * @param pOld the old Rate list 
		 */
		protected List getDifferences(List pOld) { 
			/* Build an empty Difference List */
			List myList = new List(this);
			
			/* Calculate the differences */
			myList.getDifferenceList(this, pOld);
			
			/* Return the list */
			return myList;
		}

		/**
		 * Construct an edit extract of a Rate list
		 * @param pAccount	 The account to extract rates for 
		 */
		public List getEditList(Account pAccount) {
			/* Build an empty List */
			List myList = new List(this);
			
			/* Make this list the correct style */
			myList.setStyle(ListStyle.EDIT);

			/* Local variables */
			ListIterator 	myIterator;
			AcctRate 			myCurr;
			AcctRate 			myItem;

			/* Store the account */
			myList.theAccount = pAccount;

			/* Access the list iterator */
			myIterator = listIterator(true);
			
			/* Loop through the list */
			while ((myCurr = myIterator.next()) != null) {
				/* Check the account */
				int myResult = pAccount.compareTo(myCurr.getAccount());
				
				/* Skip different accounts */
				if (myResult != 0) continue;
				
				/* Copy the item */
				myItem = new AcctRate(myList, myCurr);
				myList.add(myItem);
			}
			
			/* Return the List */
			return myList;
		}

		/* Is this list locked */
		public boolean isLocked() { return ((theAccount != null) && (theAccount.isLocked())); }

		/**
		 * Add additional fields to HTML String
		 * @param pDetail the debug detail
		 * @param pBuffer the string buffer 
		 */
		public void addHTMLFields(DebugDetail pDetail, StringBuilder pBuffer) {
			/* If this is an account extract */
			if (theAccount != null) {
				/* Start the Fields section */
				pBuffer.append("<tr><th rowspan=\"2\">Fields</th></tr>");

				/* Format the account */
				pBuffer.append("<tr><td>Account</td><td>"); 
				pBuffer.append(Account.format(theAccount)); 
				pBuffer.append("</td></tr>");
			}
		}
		
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
		 */
		public AcctRate addNewItem() {
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
		protected int countInstances(DateDay 	pDate,
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
		protected void markActiveItems() {
			ListIterator 	myIterator;
			AcctRate 		myCurr;

			/* Access the list iterator */
			myIterator = listIterator();
			
			/* Loop through the Rates */
			while ((myCurr = myIterator.next()) != null) {
				/* mark the account referred to */
				myCurr.getAccount().touchItem(myCurr); 
			}
		}

		/**
		 *  Obtain the most relevant rate for an Account and a Date
		 *   @param pAccount the Account for which to get the rate
		 *   @param pDate the date from which a rate is required
		 *   @return The relevant Rate record 
		 */
		public AcctRate getLatestRate(Account   pAccount, 
								  	  DateDay 	pDate) {
			ListIterator 	myIterator;
			AcctRate    	myRate = null;
			AcctRate    	myCurr;
			DateDay 		myDate;

			/* Access the list iterator */
			myIterator = listIterator();
			
			/* Loop through the Rates */
			while ((myCurr = myIterator.next()) != null) {
				/* Skip records that do not belong to this account */
				if (Account.differs(myCurr.getAccount(), pAccount).isDifferent())
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
		public void addItem(int		uId,
							String  pAccount,
	            			String  pRate,
	            			Date  	pDate,
				            String  pBonus) throws ModelException {
			Account     	myAccount;
			Account.List 	myAccounts;
			
			/* Access the Accounts */
			myAccounts = getData().getAccounts();
			
			/* Look up the Account */
			myAccount = myAccounts.searchFor(pAccount);
			if (myAccount == null) 
				throw new ModelException(ExceptionClass.DATA,
			                        "Rate on [" + 
			                        DateDay.format(new DateDay(pDate)) +
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
		 *  Load an Extract Rate
		 */
		private void addItem(int	uId,
							 int  	uAccountId,
	            			 String pRate,
	            			 Date 	pDate,
				             String pBonus) throws ModelException {
			AcctRate     	myRate;
			
			/* Create the period */
			myRate    = new AcctRate(this, uId, uAccountId,
					                 pDate, pRate, pBonus);
				
			/* Check that this RateId has not been previously added */
			if (!isIdUnique(myRate.getId())) 
				throw new ModelException(ExceptionClass.DATA,
						            myRate,
			  			            "Duplicate RateId");
			 
			/* Validate the rate */
			myRate.validate();

			/* Handle validation failure */
			if (myRate.hasErrors()) 
				throw new ModelException(ExceptionClass.VALIDATE,
									myRate,
									"Failed validation");
				
			/* Add to the list */
			add(myRate);
		}			

		/**
		 *  Load an Encrypted Rate
		 */
		public void addItem(int     uId,
							int		uControlId,
							int  	uAccountId,
	            			byte[]  pRate,
	            			Date  	pDate,
				            byte[]  pBonus) throws ModelException {
			AcctRate     	myRate;
			
			/* Create the period */
			myRate    = new AcctRate(this, uId, uControlId, uAccountId,
					                 pDate, pRate, pBonus);
				
			/* Check that this RateId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new ModelException(ExceptionClass.DATA,
						            myRate,
			  			            "Duplicate RateId");
			 
			/* Validate the rate */
			myRate.validate();

			/* Handle validation failure */
			if (myRate.hasErrors()) 
				throw new ModelException(ExceptionClass.VALIDATE,
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
		private DateDay     theEndDate   = null;
		private Account    	theAccount   = null;
		private Integer		theAccountId = null;

		/* Access methods */
		public RatePair		getRate()       { return theRate; }
		public RatePair		getBonus()      { return theBonus; }
		public DateDay      getEndDate()    { return theEndDate; }
		public Account		getAccount()    { return theAccount; }
		private Integer		getAccountId()  { return theAccountId; }
		public Rate  		getRateValue()  { return getPairValue(theRate); }
		public Rate  		getBonusValue() { return getPairValue(theBonus); }
		public byte[]  		getRateBytes()  { return getPairBytes(theRate); }
		public byte[]  		getBonusBytes() { return getPairBytes(theBonus); }

		public void setRate(RatePair pRate) {
			theRate      = pRate; }
		public void setBonus(RatePair pBonus) {
			theBonus     = pBonus; }
		public void setEndDate(DateDay pEndDate) {
			theEndDate   = pEndDate; }
		public void setAccount(Account pAccount) {
			theAccount   = pAccount; 
			theAccountId = (pAccount == null) ? null : pAccount.getId(); }
		private void setAccountId(Integer pAccountId) {
			theAccountId   = pAccountId; } 

		/* Constructor */
		public Values() {}
		public Values(Values pValues) { copyFrom(pValues); }

		/* Check whether this object is equal to that passed */
		public Difference histEquals(HistoryValues<AcctRate> pCompare) {
			/* Make sure that the object is the same class */
			if (pCompare.getClass() != this.getClass()) return Difference.Different;
			
			/* Cast correctly */
			Values myValues = (Values)pCompare;

			/* Determine underlying differences */
			Difference myDifference = super.histEquals(pCompare);
			
			/* Compare underlying values */
			myDifference = myDifference.combine(differs(theRate,  			myValues.theRate));
			myDifference = myDifference.combine(differs(theBonus, 			myValues.theBonus));
			myDifference = myDifference.combine(DateDay.differs(theEndDate, myValues.theEndDate));
			myDifference = myDifference.combine(differs(theAccount, 		myValues.theAccount));
			myDifference = myDifference.combine(Utils.differs(theAccountId, myValues.theAccountId));
			
			/* Return the differences */
			return myDifference;
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
			theAccountId = myValues.getAccountId();
		}
		public Difference	fieldChanged(int fieldNo, HistoryValues<AcctRate> pOriginal) {
			Values 		pValues = (Values)pOriginal;	
			Difference	bResult = Difference.Identical;
			switch (fieldNo) {
				case FIELD_RATE:
					bResult = (differs(theRate,    pValues.theRate));
					break;
				case FIELD_BONUS:
					bResult = (differs(theBonus,   pValues.theBonus));
					break;
				case FIELD_ENDDATE:
					bResult = (DateDay.differs(theEndDate, 	pValues.theEndDate));
					break;
				case FIELD_ACCOUNT:
					bResult = (Account.differs(theAccount,  pValues.theAccount));
					break;
				default:
					bResult = super.fieldChanged(fieldNo, pValues);
					break;
			}
			return bResult;
		}

		/**
		 * Update encryption after security change
		 */
		protected void updateSecurity() throws ModelException {
			/* Update the encryption */
			theRate	= new RatePair(theRate.getValue());
			if (theBonus != null) theBonus = new RatePair(theBonus.getValue());
		}		
		
		/**
		 * Ensure encryption after non-encrypted load
		 */
		protected void applySecurity() throws ModelException {
			/* Apply the encryption */
			theRate.encryptPair(null);
			if (theBonus != null) theBonus.encryptPair(null);
		}		
		
		/**
		 * Adopt encryption from base
		 * @param pBase the Base values
		 */
		protected void adoptSecurity(ControlKey pControl, EncryptedValues pBase) throws ModelException {
			Values myBase = (Values)pBase;

			/* Apply the encryption */
			theRate.encryptPair(myBase.getRate());
			if (theBonus != null) theBonus.encryptPair(myBase.getBonus());
		}		
	}
}
