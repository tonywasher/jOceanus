package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.data.Price;
import uk.co.tolcroft.models.Date;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Number;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.*;

public class DilutionEvent implements SortedList.linkObject {
    /**
	 * Storage for the List Node
	 */
    private Object			theLink		= null;

	/* Members */
	private Account			theAccount	= null;
	private Date			theDate		= null;
	private	Dilution		theDilution	= null;

	/* Access methods */
	private Account			getAccount()  	{ return theAccount; }
	private Date			getDate() 		{ return theDate; }	
	private Dilution		getDilution() 	{ return theDilution; }	

	/**
	 * Get the link node for this item
	 * @return the Link node or <code>null</code>
	 */
	public Object		getLinkNode(Object pList)	{ return theLink; }

	/**
	 * Get the link node for this item
	 * @return the Link node or <code>null</code>
	 */
	public void			setLinkNode(Object l, Object o)	{ theLink = o; }

	/**
	 * Determine whether the item is visible to standard searches
	 * @return <code>true/false</code>
	 */
	public boolean		isHidden()    	{ return false; }

	/**
	 * Compare this AnalysisYear to another to establish sort order.
	 * 
	 * @param pThat The Year to compare to
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
	 * Create a dilution event from an event
	 * @param pEvent
	 */
	private DilutionEvent(Event pEvent) {
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
	}
	
	/**
	 * Create a dilution event from details
	 * @param pAccount the account
	 * @param pDate the Date
	 * @param pDilution the dilution
	 */
	private DilutionEvent(Account 	pAccount,
						  Date    	pDate,
						  Dilution	pDilution) {
		/* Store the values */
		theAccount 	= pAccount;
		theDate		= pDate;
		theDilution = pDilution;
	}
	
	/**
	 * List of DilutionEvents
	 */
	public static class List extends SortedList<DilutionEvent> {
		/* Members */
		DataSet theData = null;
		
		/**
		 * Constructor
		 * @param pData the DataSet
		 */
		public List(DataSet pData) {
			theData = pData;
		}
		
		/**
		 * Add Dilution Event to List
		 * @param pEvent the base event
		 */
		public void addDilution(Event pEvent) {
			DilutionEvent myDilution;
			
			/* Create the dilution event */
			myDilution = new DilutionEvent(pEvent);
			
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
			myEvent = new DilutionEvent(myAccount,
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
				if (!Utils.differs(pAccount, myEvent.getAccount())) {
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
				if (!Utils.differs(pAccount, myEvent.getAccount())) {
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
			Account				myAccount;
			Account.List 		myAccounts;
			Price.List			myPrices;
			Date				myDate;
			Number.Price    	myPrice;
			Number.DilutedPrice myDilutedPrice;
			Dilution			myDilution;
			
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
				myDilutedPrice = Number.DilutedPrice.Parse(pPrice);
				if (myDilutedPrice == null) 
					throw new Exception(ExceptionClass.DATA,
										"Invalid DilutedPrice: " + pPrice);
				
				/* Obtain the undiluted price */
				myPrice = myDilutedPrice.getPrice(myDilution);
			}

			/* Else this is just a price */
			else {
				/* Obtain the the price */
				myPrice = Number.Price.Parse(pPrice);
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
