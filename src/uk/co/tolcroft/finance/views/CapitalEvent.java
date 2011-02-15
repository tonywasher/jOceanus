package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number.*;

public class CapitalEvent extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "CapitalEvent";

	/* Members */
	private Account			theAccount		= null;
	private Date			theDate			= null;
	private TransactionType theTransType	= null;
	private String 			theDesc			= null;
	private Money			theTotalCost	= null;
	private Money			theDeltaCost	= null;
	private Units			theUnits		= null;
	private Price			thePrice		= null;
	private Money			theDeltaGains	= null;
	private Money			theTotalGains	= null;
	private Money			theValue		= null;
	private Money			theProfit		= null;
	
	/* Access methods */
	public Account 			getAccount() 	{ return theAccount; }
	public Date 			getDate() 		{ return theDate; }
	public String 			getDesc() 		{ return theDesc; }
	public TransactionType	getTransType() 	{ return theTransType; }
	public Money 			getTotalCost() 	{ return theTotalCost; }
	public Money 			getDeltaCost() 	{ return theDeltaCost; }
	public Units 			getUnits() 		{ return theUnits; }
	public Price 			getPrice() 		{ return thePrice; }
	public Money 			getDeltaGains()	{ return theDeltaGains; }
	public Money 			getTotalGains() { return theTotalGains; }
	public Money 			getValue() 		{ return theValue; }
	public Money 			getProfit() 	{ return theProfit; }
	
	/* Field IDs */
	public static final int FIELD_ID     	= 0;
	public static final int FIELD_ACCOUNT  	= 1;
	public static final int FIELD_DATE      = 2;
	public static final int FIELD_TRANTYPE  = 3;
	public static final int FIELD_DESC		= 4;
	public static final int FIELD_TOTALCOST = 5;
	public static final int FIELD_DELTACOST = 6;
	public static final int FIELD_UNITS 	= 7;
	public static final int FIELD_PRICE 	= 8;
	public static final int FIELD_VALUE		= 9;
	public static final int FIELD_TOTALGAIN = 10;
	public static final int FIELD_DELTAGAIN = 11;
	public static final int FIELD_PROFIT	= 12;
	public static final int NUMFIELDS	    = 13;
	
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
			case FIELD_ID: 	  		return "ID";
			case FIELD_ACCOUNT: 	return "Name";
			case FIELD_DATE: 		return "Date";
			case FIELD_DESC: 		return "Description";
			case FIELD_TRANTYPE: 	return "TransType";
			case FIELD_TOTALCOST: 	return "TotalCost";
			case FIELD_DELTACOST: 	return "DeltaCost";
			case FIELD_UNITS: 		return "Units";
			case FIELD_PRICE: 		return "Price";
			case FIELD_VALUE: 	  	return "Value";
			case FIELD_TOTALGAIN: 	return "TotalGain";
			case FIELD_DELTAGAIN: 	return "DeltaGain";
			case FIELD_PROFIT: 		return "Profit";
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
		String myString = ""; 
		switch (iField) {
			case FIELD_ID: 			
				myString += getId();
				break;
			case FIELD_ACCOUNT:		
				myString += Account.format(theAccount);
				break;
			case FIELD_DATE: 		
				myString += Date.format(theDate);
				break;
			case FIELD_TRANTYPE: 	
				myString += TransactionType.format(theTransType);
				break;
			case FIELD_DESC: 	
				myString += theDesc;
				break;
			case FIELD_TOTALCOST:
				myString += Money.format(theTotalCost);
				break;
			case FIELD_DELTACOST:
				myString += Money.format(theDeltaCost);
				break;
			case FIELD_UNITS:
				myString += Units.format(theUnits);
				break;
			case FIELD_PRICE:
				myString += Price.format(thePrice);
				break;
			case FIELD_VALUE:
				myString += Money.format(theValue);
				break;
			case FIELD_TOTALGAIN:
				myString += Money.format(theTotalGains);
				break;
			case FIELD_DELTAGAIN:
				myString += Money.format(theDeltaGains);
				break;
			case FIELD_PROFIT:
				myString += Money.format(theProfit);
				break;
		}
		return myString;
	}

	/**
	 * Constructor
	 * @param pList the list to belong to
	 * @param pEvent the underlying event
	 */
	private CapitalEvent(List 	pList,
						 Event 	pEvent) {
		/* Call super-constructor */
		super(pList, pEvent.getId());
		
		/* Store the account from the list */
		theAccount 		= pList.getAccount();
		
		/* Pick up sorting values from the event */
		theDate 		= pEvent.getDate();
		theTransType 	= pEvent.getTransType();
		theDesc 		= pEvent.getDesc();
		
		/* Link to the event */
		setBase(pEvent);
		
		/* Set status */
		setState(DataState.CLEAN);
	}
	
	/**
	 * Compare this CapitalEvent to another to establish equality.
	 * 
	 * @param pThat The CapitalEvent to compare to
	 * @return <code>true</code> if the event is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a Capital Event */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a CapitalEvent */
		CapitalEvent myEvent = (CapitalEvent)pThat;
		
		/* Check for equality */
		if (getId() != myEvent.getId()) return false;
		if (Date.differs(getDate(),      			myEvent.getDate())) 		return false;
		if (Account.differs(getAccount(),    		myEvent.getAccount())) 		return false;
		if (TransactionType.differs(getTransType(),	myEvent.getTransType())) 	return false;
		if (Utils.differs(getDesc(),  				myEvent.getDesc())) 		return false;
		if (Money.differs(getTotalCost(),   		myEvent.getTotalCost())) 	return false;
		if (Money.differs(getDeltaCost(), 			myEvent.getDeltaCost())) 	return false;
		if (Units.differs(getUnits(),      			myEvent.getUnits())) 		return false;
		if (Price.differs(getPrice(),      			myEvent.getPrice())) 		return false;
		if (Money.differs(getValue(),  				myEvent.getValue()))		return false;
		if (Money.differs(getTotalGains(),  		myEvent.getTotalGains()))	return false;
		if (Money.differs(getDeltaGains(),			myEvent.getDeltaGains()))	return false;
		if (Money.differs(getProfit(),				myEvent.getProfit()))		return false;
		return getBase().equals(myEvent.getBase());
	}

	/**
	 * Compare this capital event to another to establish sort order. 
	 * @param pThat The Capital Event to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a CapitalEvent */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as a CapitalEvent */
		CapitalEvent myThat = (CapitalEvent)pThat;
		
		/* Compare the underlying events */
		return getBase().compareTo(myThat.getBase());
	}
	
	/* The List of capital events */
	public static class List extends DataList<CapitalEvent> {
		/* Members */
		private Account			theAccount		= null;
		private Money			theTotalCost	= null;
		private Units			theUnits		= null;
		private Money			theTotalGains	= null;
	
		/* Access methods */
		public Account 			getAccount() 	{ return theAccount; }
		public Money 			getTotalCost() 	{ return theTotalCost; }
		public Units 			getUnits() 		{ return theUnits; }
		public Money 			getTotalGains() { return theTotalGains; }

		/** 
	 	 * Construct an empty Capital event list
	 	 * @param pAccount the Account for the list
	 	 */
		protected List(Account pAccount) { 
			super(ListStyle.VIEW, false);
			
			/* Store the account */
			theAccount 		= pAccount;
			
			/* Initialise the totals */
			theTotalCost 	= new Money(0);
			theTotalGains 	= new Money(0);
			theUnits		= new Units(0);
		}

		/** 
	 	 * Clone a Capital Event list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return null; }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pItem the item to add
		 * @return the newly added item
		 */
		public DataItem addNewItem(DataItem pItem) { return null; }

	
		/**
		 * Add a new item to the edit list
		 * 
		 * @param isCredit - ignored
		 */
		public void addNewItem(boolean isCredit) { return; }
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }		

		/**
		 * Add additional fields to HTML String
		 * @param pBuffer the string buffer 
		 */
		public void addHTMLFields(StringBuilder pBuffer) {
			/* Start the Fields section */
			pBuffer.append("<tr><th rowspan=\"5\">Fields</th></tr>");
				
			/* Format the balances */
			pBuffer.append("<tr><td>Account</td><td>"); 
			pBuffer.append(Account.format(theAccount)); 
			pBuffer.append("</td></tr>");
			
			/* Format the totals */
			pBuffer.append("<tr><td>Total Cost</td><td>"); 
			pBuffer.append(Money.format(theTotalCost)); 
			pBuffer.append("</td></tr>"); 
			pBuffer.append("<tr><td>Total Gains</td><td>"); 
			pBuffer.append(Money.format(theTotalGains)); 
			pBuffer.append("</td></tr>"); 
			pBuffer.append("<tr><td>Units</td><td>"); 
			pBuffer.append(Units.format(theUnits)); 
			pBuffer.append("</td></tr>"); 
		}
		
		/**
		 * Add an event to the list
		 * 
		 * @param pEvent the Event to add
		 */
		protected void addEvent(Event pEvent) {
			CapitalEvent myEvent;
			
			/* Create the Capital Event and add to list */
			myEvent = new CapitalEvent(this, pEvent);
			myEvent.addToList();
		}
	}
}
