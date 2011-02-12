package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number;

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
	private Number.Money	theTotalCost	= null;
	private Number.Money	theDeltaCost	= null;
	private Number.Units	theUnits		= null;
	private Number.Price	thePrice		= null;
	private Number.Money	theDeltaGains	= null;
	private Number.Money	theTotalGains	= null;
	private Number.Money	theValue		= null;
	private Number.Money	theProfit		= null;
	
	/* Access methods */
	public Account 			getAccount() 	{ return theAccount; }
	public Date 			getDate() 		{ return theDate; }
	public String 			getDesc() 		{ return theDesc; }
	public TransactionType	getTransType() 	{ return theTransType; }
	public Number.Money 	getTotalCost() 	{ return theTotalCost; }
	public Number.Money 	getDeltaCost() 	{ return theDeltaCost; }
	public Number.Units 	getUnits() 		{ return theUnits; }
	public Number.Price 	getPrice() 		{ return thePrice; }
	public Number.Money 	getDeltaGains()	{ return theDeltaGains; }
	public Number.Money 	getTotalGains() { return theTotalGains; }
	public Number.Money 	getValue() 		{ return theValue; }
	public Number.Money 	getProfit() 	{ return theProfit; }
	
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
		String myString = "<tr><td>" + fieldName(iField) + "</td><td>"; 
		switch (iField) {
			case FIELD_ID: 			
				myString += getId();
				break;
			case FIELD_ACCOUNT:		
				myString += Utils.formatAccount(theAccount);
				break;
			case FIELD_DATE: 		
				myString += Utils.formatDate(theDate);
				break;
			case FIELD_TRANTYPE: 	
				myString += Utils.formatTrans(theTransType);
				break;
			case FIELD_DESC: 	
				myString += theDesc;
				break;
			case FIELD_TOTALCOST:
				myString += Utils.formatMoney(theTotalCost);
				break;
			case FIELD_DELTACOST:
				myString += Utils.formatMoney(theDeltaCost);
				break;
			case FIELD_UNITS:
				myString += Utils.formatUnits(theUnits);
				break;
			case FIELD_PRICE:
				myString += Utils.formatPrice(thePrice);
				break;
			case FIELD_VALUE:
				myString += Utils.formatMoney(theValue);
				break;
			case FIELD_TOTALGAIN:
				myString += Utils.formatMoney(theTotalGains);
				break;
			case FIELD_DELTAGAIN:
				myString += Utils.formatMoney(theDeltaGains);
				break;
			case FIELD_PROFIT:
				myString += Utils.formatMoney(theProfit);
				break;
		}
		return myString + "</td></tr>";
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
		if (Utils.differs(getDate(),      	myEvent.getDate())) 		return false;
		if (Utils.differs(getAccount(),    	myEvent.getAccount())) 		return false;
		if (Utils.differs(getTransType(),  	myEvent.getTransType())) 	return false;
		if (Utils.differs(getDesc(),  		myEvent.getDesc())) 		return false;
		if (Utils.differs(getTotalCost(),   myEvent.getTotalCost())) 	return false;
		if (Utils.differs(getDeltaCost(), 	myEvent.getDeltaCost())) 	return false;
		if (Utils.differs(getUnits(),      	myEvent.getUnits())) 		return false;
		if (Utils.differs(getPrice(),      	myEvent.getPrice())) 		return false;
		if (Utils.differs(getValue(),  		myEvent.getValue()))		return false;
		if (Utils.differs(getTotalGains(),  myEvent.getTotalGains()))	return false;
		if (Utils.differs(getDeltaGains(),	myEvent.getDeltaGains()))	return false;
		if (Utils.differs(getProfit(),		myEvent.getProfit()))		return false;
		return true;
	}

	/**
	 * Compare this capital event to another to establish sort order. 
	 * @param pThat The Capital Event to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		int iDiff;
		
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a CapitalEvent */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as a CapitalEvent */
		CapitalEvent myThat = (CapitalEvent)pThat;

		/* If the dates differ */
		if (this.getDate() != myThat.getDate()) {
			/* Handle null dates */
			if (this.getDate() == null) return 1;
			if (myThat.getDate() == null) return -1;
			
			/* Compare the dates */
			iDiff = getDate().compareTo(myThat.getDate());
			if (iDiff != 0) return iDiff;
		}
		
		/* If the transaction types differ */
		if (this.getTransType() != myThat.getTransType()) {
			/* Handle nulls */
			if (this.getTransType() == null) return 1;
			if (myThat.getTransType() == null) return -1;
			
			/* Compare transaction types */
			iDiff = getTransType().compareTo(myThat.getTransType());
			if (iDiff != 0) return iDiff;
		}
		
		/* If the descriptions differ */
		if (this.getDesc() != myThat.getDesc()) {
			/* Handle null descriptions */
			if (this.getDesc() == null) return 1;
			if (myThat.getDesc() == null) return -1;
			
			/* Compare the descriptions */
			iDiff = getDesc().compareTo(myThat.getDesc());
			if (iDiff < 0) return -1;
			if (iDiff > 0) return 1;
		}
		
		/* Compare ids */
		iDiff = (int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}
	
	/* The List of capital events */
	public static class List extends DataList<CapitalEvent> {
		/* Members */
		private Account			theAccount		= null;
		private Number.Money	theTotalCost	= null;
		private Number.Units	theUnits		= null;
		private Number.Money	theTotalGains	= null;
	
		/* Access methods */
		public Account 			getAccount() 	{ return theAccount; }
		public Number.Money 	getTotalCost() 	{ return theTotalCost; }
		public Number.Units 	getUnits() 		{ return theUnits; }
		public Number.Money 	getTotalGains() { return theTotalGains; }

		/** 
	 	 * Construct an empty Capital event list
	 	 * @param pAccount the Account for the list
	 	 */
		protected List(Account pAccount) { 
			super(ListStyle.VIEW, false);
			
			/* Store the account */
			theAccount 		= pAccount;
			
			/* Initialise the totals */
			theTotalCost 	= new Number.Money(0);
			theTotalGains 	= new Number.Money(0);
			theUnits		= new Number.Units(0);
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
