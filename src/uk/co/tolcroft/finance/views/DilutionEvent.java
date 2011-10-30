package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Date;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.help.DebugDetail;
import uk.co.tolcroft.models.*;

public class DilutionEvent extends DataItem<DilutionEvent> {
	/**
	 * The name of the object
	 */
	private static final String objName = "DilutionEvent";

	/* Members */
	private Account			theAccount	= null;
	private Date			theDate		= null;
	private	Dilution		theDilution	= null;

	/* Access methods */
	private Account			getAccount()  	{ return theAccount; }
	private Date			getDate() 		{ return theDate; }	
	private Dilution		getDilution() 	{ return theDilution; }	

	/* Field IDs */
	public static final int FIELD_ACCOUNT  	= 0;
	public static final int FIELD_DATE      = 1;
	public static final int FIELD_DILUTION  = 2;
	public static final int NUMFIELDS	    = 3;
	
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
			case FIELD_ACCOUNT: 	return "Name";
			case FIELD_DATE: 		return "Date";
			case FIELD_DILUTION:	return "Dilution";
			default:		  		return DataItem.fieldName(iField);
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
	public String formatField(DebugDetail pDetail, int iField, HistoryValues<DilutionEvent> pValues) {
		String myString = ""; 
		switch (iField) {
			case FIELD_ACCOUNT:		
				myString += Account.format(theAccount);
				myString = pDetail.addDebugLink(theAccount, myString);
				break;
			case FIELD_DATE: 		
				myString += Date.format(theDate);
				break;
			case FIELD_DILUTION: 	
				myString += Dilution.format(theDilution);
				break;
		}
		return myString;
	}

	/**
	 * Compare this DilutionEvent to another to establish equality.
	 * 
	 * @param pThat The DilutionEvent to compare to
	 * @return <code>true</code> if the event is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a Dilution Event */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a DilutionEvent */
		DilutionEvent myEvent = (DilutionEvent)pThat;
		
		/* Check for equality */
		if (Date.differs(getDate(),      	myEvent.getDate()).isDifferent()) 		return false;
		if (Account.differs(getAccount(),  	myEvent.getAccount()).isDifferent()) 	return false;
		if (Dilution.differs(getDilution(),	myEvent.getDilution()).isDifferent())	return false;
		return getBase().equals(myEvent.getBase());
	}

	/**
	 * Compare this DilutionEvent to another to establish sort order.
	 * 
	 * @param pThat The Event to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		int iDiff;
		
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is an AnalysisYear */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as a Dilution Event */
		DilutionEvent myThat = (DilutionEvent)pThat;
		
		/* If the dates differ */
		if (this.getDate() != myThat.getDate()) {
			/* Handle null dates */
			if (this.getDate() == null) return 1;
			if (myThat.getDate() == null) return -1;
			
			/* Compare the dates */
			iDiff = getDate().compareTo(myThat.getDate());
			if (iDiff != 0) return iDiff;
		}
		
		/* Compare the account */
		return getAccount().compareTo(myThat.getAccount());
	}
	
	/**
	 * Build History (no history)
	 */
	protected void buildHistory() {}

	/**
	 * Create a dilution event from an event
	 * @param pList the list
	 * @param pEvent the underlying event
	 */
	private DilutionEvent(List 	pList, 
						  Event pEvent) {
		/* Call super constructor */
		super(pList, pEvent.getId());
		
		/* Local variables */
		Account 		myAccount;
		TransactionType myType;
		
		/* Access the transaction type */
		myType = pEvent.getTransType();
		
		/* Switch on the transaction type */
		switch (myType.getTranClass()) {
			case STOCKSPLIT:
			case STOCKRIGHTWAIVED:
			case STOCKDEMERGER:
			default:
				myAccount = pEvent.getDebit();
				break;
			case STOCKRIGHTTAKEN:
				myAccount = pEvent.getCredit();
				break;
		}
		
		/* Store the values */
		theAccount 	= myAccount;
		theDate		= pEvent.getDate();
		theDilution = pEvent.getDilution();
		
		/* Link to the event */
		setBase(pEvent);
		
		/* Set status */
		setState(DataState.CLEAN);
	}
	
	/**
	 * Create a dilution event from details
	 * @param pAccount the account
	 * @param pDate the Date
	 * @param pDilution the dilution
	 */
	private DilutionEvent(List		pList,
						  Account 	pAccount,
						  Date    	pDate,
						  Dilution	pDilution) {
		/* Call super constructor */
		super(pList, 0);
		
		/* Store the values */
		theAccount 	= pAccount;
		theDate		= pDate;
		theDilution = pDilution;
		
		/* Set status */
		setState(DataState.CLEAN);
	}
	
	/**
	 * List of DilutionEvents
	 */
	public static class List extends DataList<List, DilutionEvent> {
		/* Members */
		FinanceData theData = null;
		
		/**
		 * Constructor
		 * @param pData the DataSet
		 */
		public List(FinanceData pData) {
			super(List.class, DilutionEvent.class, ListStyle.VIEW, false);
			theData = pData;
		}
		
		/* Obtain extract lists. */
		public List getUpdateList() { return null; }
		public List getEditList() 	{ return null; }
		public List getShallowCopy() { return null; }
		public List getDeepCopy(DataSet<?> pData) { return null; }
		public List getDifferences(List pOld) { return null; }

		/**
		 * Add a new item to the list
		 * @param pItem the item to add
		 * @return the newly added item
		 */
		public DilutionEvent addNewItem(DataItem<?> pItem) { return null; }

	
		/**
		 * Add a new item to the edit list
		 * @return the newly added item
		 */
		public DilutionEvent addNewItem() { return null; }
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }		

		/**
		 * Add Dilution Event to List
		 * @param pEvent the base event
		 */
		public void addDilution(Event pEvent) {
			DilutionEvent myDilution;
			
			/* Create the dilution event */
			myDilution = new DilutionEvent(this, pEvent);
			
			/* Add it to the list */
			add(myDilution);
		}

		/**
		 * Add Dilution Event to List
		 * @param pEvent the base event
		 */
		public void addDilution(String 			pAccount,
								java.util.Date 	pDate,
								String 			pDilution) throws Exception {
			DilutionEvent 	myEvent;
			Account			myAccount;
			Date			myDate;
			Dilution		myDilution;
			Account.List	myAccounts;
			
			/* Access account list */
			myAccounts = theData.getAccounts();
			
			/* Search for the account */
			myAccount = myAccounts.searchFor(pAccount);
			if (myAccount == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Invalid Dilution account [" + pAccount + "]");
			
			/* Create the date */
			myDate = new Date(pDate);
			
			/* Record the dilution */
			myDilution = Dilution.Parse(pDilution);
			if (myDilution == null) 
				throw new Exception(ExceptionClass.DATA,
									"Invalid Dilution: " + pDilution);
			
			/* Create the dilution event */
			myEvent = new DilutionEvent(this,
										myAccount,
									 	myDate,
									 	myDilution);
			
			/* Add it to the list */
			add(myEvent);
		}

		/**
		 * Does this account have diluted prices
		 * @param pAccount the account to test
		 * @return <code>true</code> if the account has diluted prices, <code>false</code> otherwise
		 */
		public boolean hasDilution(Account pAccount) {
			SortedList<DilutionEvent>.ListIterator myIterator;
			DilutionEvent 	myEvent;
			boolean 		myResult = false;
			
			/* Create the iterator */
			myIterator = listIterator();
			
			/* Loop through the items */
			while ((myEvent = myIterator.next()) != null) {
				/* If the event is for this account */
				if (!Account.differs(pAccount, myEvent.getAccount()).isDifferent()) {
					/* Set result and break loop */
					myResult = true;
					break;
				}
			}
			
			/* Return to caller */
			return myResult;
		}

		/**
		 * Obtain the dilution factor for the account and date
		 * @param pAccount the account to dilute
		 * @param pDate the date of the price
		 * @return the dilution factor
		 */
		public Dilution getDilutionFactor(Account pAccount, Date pDate) {
			SortedList<DilutionEvent>.ListIterator myIterator;
			DilutionEvent 	myEvent;
			Dilution 		myDilution = new Dilution(Dilution.MAX_VALUE);
			
			/* Create the iterator */
			myIterator = listIterator();
			
			/* Loop through the items */
			while ((myEvent = myIterator.next()) != null) {
				/* If the event is for this account */
				if (!Account.differs(pAccount, myEvent.getAccount()).isDifferent()) {
					/* If the dilution date is later */
					if (pDate.compareTo(myEvent.getDate()) < 0) {
						/* add in the dilution factor */
						myDilution = myDilution.getFurtherDilution(myEvent.getDilution());
					}
				}
			}
			
			/* If there is no dilution at all */
			if (myDilution.getValue() == Dilution.MAX_VALUE) 
				myDilution = null;
			
			/* Return to caller */
			return myDilution;
		}

		/**
		 * Obtain the undiluted price for the account and date
		 * @param pAccount the account to dilute
		 * @param pDate the date of the price
		 * @param pPrice the (possibly) diluted price
		 * @return the dilution factor
		 */
		public void addPrice(String 		pAccount,
							 java.util.Date pDate,
							 String			pPrice) throws Exception {
			Account			myAccount;
			Account.List 	myAccounts;
			AcctPrice.List	myPrices;
			Date			myDate;
			Price    		myPrice;
			DilutedPrice 	myDilutedPrice;
			Dilution		myDilution;
			
			/* Obtain the prices and accounts */
			myAccounts 	= theData.getAccounts();
			myPrices	= theData.getPrices();
			
			/* Search for the account */
			myAccount = myAccounts.searchFor(pAccount);
			if (myAccount == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Invalid Price account [" + pAccount + "]");
			
			/* Create the date */
			myDate = new Date(pDate);

			/* If the account has diluted prices for this date */
			if ((hasDilution(myAccount)) &&
				((myDilution = getDilutionFactor(myAccount, myDate)) != null)) {
				/* Obtain the diluted price */
				myDilutedPrice = DilutedPrice.Parse(pPrice);
				if (myDilutedPrice == null) 
					throw new Exception(ExceptionClass.DATA,
										"Invalid DilutedPrice: " + pPrice);
				
				/* Obtain the undiluted price */
				myPrice = myDilutedPrice.getPrice(myDilution);
			}

			/* Else this is just a price */
			else {
				/* Obtain the the price */
				myPrice = Price.Parse(pPrice);
				if (myPrice == null) 
					throw new Exception(ExceptionClass.DATA,
										"Invalid Price: " + pPrice);
			}
			
			/* Add the item to the list */
			myPrices.addItem(myAccount,
							 myDate,
							 myPrice);
			
			/* Return to caller */
			return;
		}
	}	
}
