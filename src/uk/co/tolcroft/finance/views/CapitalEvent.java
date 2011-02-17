package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number.*;

public class CapitalEvent extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "CapitalEvent";

	/**
	 * The Amount Tax threshold for "small" transactions (£3000)
	 */
	private final static Money valueLimit 	= new Money(Money.convertToValue(3000));

	/**
	 * The Rate Tax threshold for "small" transactions (5%)
	 */
	private final static Rate rateLimit 	= new Rate(Rate.convertToValue(5));

	/* Members */
	private Account			theAccount		= null;
	private Date			theDate			= null;
	private TransactionType theTransType	= null;
	private String 			theDesc			= null;
	private Money			theCashTakeover	= null;
	private Money			theInvestment	= null;
	private Money			theDividends	= null;
	private Money			theTotalCost	= null;
	private Money			theDeltaCost	= null;
	private Units			theUnits		= null;
	private Price			thePrice		= null;
	private Money			theDeltaGains	= null;
	private Money			theTotalGains	= null;
	private Money			theValue		= null;
	private Money			theProfit		= null;
	
	private boolean			isReinvestment	= false;
	
	/* Access methods */
	public Account 			getAccount() 	{ return theAccount; }
	public Date 			getDate() 		{ return theDate; }
	public String 			getDesc() 		{ return theDesc; }
	public TransactionType	getTransType() 	{ return theTransType; }
	public Money 			getInvestment() { return theInvestment; }
	public Money 			getDividends()  { return theDividends; }
	public Money 			getTotalCost() 	{ return theTotalCost; }
	public Money 			getDeltaCost() 	{ return theDeltaCost; }
	public Units 			getUnits() 		{ return theUnits; }
	public Price 			getPrice() 		{ return thePrice; }
	public Money 			getDeltaGains()	{ return theDeltaGains; }
	public Money 			getTotalGains() { return theTotalGains; }
	public Money 			getValue() 		{ return theValue; }
	public Money 			getProfit() 	{ return theProfit; }
	
	/* Map the getBase function */
	public Event 			getBase()		{ return (Event)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_ID     	= 0;
	public static final int FIELD_ACCOUNT  	= 1;
	public static final int FIELD_DATE      = 2;
	public static final int FIELD_TRANTYPE  = 3;
	public static final int FIELD_DESC		= 4;
	public static final int FIELD_INVESTMNT = 5;
	public static final int FIELD_DIVIDENDS = 6;
	public static final int FIELD_TOTALCOST = 7;
	public static final int FIELD_DELTACOST = 8;
	public static final int FIELD_UNITS 	= 9;
	public static final int FIELD_PRICE 	= 10;
	public static final int FIELD_VALUE		= 11;
	public static final int FIELD_TOTALGAIN = 12;
	public static final int FIELD_DELTAGAIN = 13;
	public static final int FIELD_PROFIT	= 14;
	public static final int NUMFIELDS	    = 15;
	
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
			case FIELD_INVESTMNT: 	return "Investment";
			case FIELD_DIVIDENDS: 	return "Dividends";
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
	private CapitalEvent(List 			pList,
						 Event 			pEvent,
						 CapitalEvent	pPrevious) {
		/* Call super-constructor */
		super(pList, pEvent.getId());
		
		/* Store the account from the list */
		theAccount 		= pList.getAccount();
		
		/* Pick up sorting values from the event */
		theDate 		= pEvent.getDate();
		theTransType 	= pEvent.getTransType();
		theDesc 		= pEvent.getDesc();
		
		/* If we have a previous capital event */
		if (pPrevious != null) {
			/* Initialise totals from previous event */
			theUnits 		= new Units(pPrevious.getUnits());
			theTotalCost	= new Money(pPrevious.getTotalCost());
			theTotalGains	= new Money(pPrevious.getTotalGains());
			theInvestment	= new Money(pPrevious.getInvestment());
			theDividends	= new Money(pPrevious.getDividends());
		}
		
		/* else this is the first capital event */
		else {
			/* Initialise the totals */
			theUnits 		= new Units(0);
			theTotalCost 	= new Money(0);
			theTotalGains	= new Money(0);
			theInvestment	= new Money(0);
			theDividends	= new Money(0);
		}
		
		/* Process the event */
		processEvent(pEvent);
		
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
	
	/**
	 * Process Event
	 * @param pEvent the event
	 */
	private void processEvent(Event pEvent) {
		Money 		myAmount 		= pEvent.getAmount();
		Money 		myTaxCredit		= pEvent.getTaxCredit();
		Units 		myUnits 		= pEvent.getUnits();
		Dilution	myDilution		= pEvent.getDilution();
		boolean 	isDebit			= !Account.differs(theAccount, pEvent.getDebit());
		boolean 	isCredit		= !Account.differs(theAccount, pEvent.getCredit());

	}
	
	/**
	 * Process an event that is a stock split.
	 * This capital event relates only to the Debit Account
	 * @param pEvent the event
	 */
	private void processStockSplit(Event pEvent) {
		Units 	myUnits 	= pEvent.getUnits();

		/* Add/Subtract the units movement for the account */
		if (myUnits != null) theUnits.addUnits(myUnits);
	}
	
	/**
	 * Process an event that is a transfer into capital (also StockRightTaken and Dividend Re-investment).
	 * This capital event relates only to the Credit Account
	 * @param pEvent the event
	 */
	private void processTransferIn(Event pEvent) {
		Money	myAmount 	= pEvent.getAmount();
		Units 	myUnits 	= pEvent.getUnits();

		/* Add any new units into the account */
		if (myUnits != null) theUnits.addUnits(myUnits);
			
		/* This amount is added to the cost, so record as the delta cost */
		theDeltaCost = new Money(myAmount);

		/* Adjust the total cost of this account */
		theTotalCost.addAmount(myAmount);

		/* Adjust the total money invested into this account */
		theInvestment.addAmount(myAmount);
	}
	
	/**
	 * Process a dividend event.
	 * This capital event relates to the only to Debit account, 
	 * although this may be a re-investment in which case we will utilise
	 * the TransferIn logic for the re-investment part
	 * @param pEvent the event
	 */
	private void processDividend(Event pEvent) {
		Money	myAmount 	= pEvent.getAmount();
		Money	myTaxCredit	= pEvent.getTaxCredit();

		if (isReinvestment) {
			/* Process as a transfer in */
			processTransferIn(pEvent);
		}

		/* Add the amount plus any tax credit to dividends */
		theDividends.addAmount(myAmount);
		theDividends.addAmount(myTaxCredit);
	}
	
	/**
	 * Process an event that is a transfer from capital.
	 * This capital event relates only to the Debit Account
	 * @param pEvent the event
	 */
	private void processTransferOut(Event pEvent) {
		Money	myAmount 	= pEvent.getAmount();
		Units 	myUnits 	= pEvent.getUnits();
		Money	myReduction;

		/* Adjust the total amount invested into this account */
		theInvestment.subtractAmount(myAmount);
		
		/* Assume the the cost reduction is the full value */
		myReduction = new Money(myAmount);
		
		/* If we are reducing units in the account */
		if ((myUnits != null) && (myUnits.isNonZero())) {
			/* The reduction is the relevant fraction of the cost */
			myReduction = theTotalCost.valueAtWeight(myUnits, theUnits);
		}
		
		/* If the reduction is greater than the total cost */
		if (myReduction.getValue() > theTotalCost.getValue()) {
			/* Reduction is the total cost */
			myReduction = new Money(theTotalCost);
		}
		
		/* Adjust the total cost */
		theDeltaCost = new Money(myReduction);
		theDeltaCost.negate();
		theTotalCost.addAmount(theDeltaCost);
		
		/* Adjust the gains */
		theDeltaGains = new Money(myAmount);
		theDeltaGains.addAmount(theDeltaCost);
		theTotalGains.addAmount(theDeltaGains);
		
		/* Subtract any redeemed units from the account */
		if (myUnits != null) theUnits.subtractUnits(myUnits);
	}
	
	/**
	 * Process an event that is stock right waived.
	 * This capital event relates only to the Debit Account
	 * @param pEvent the event
	 */
	private void processStockRightWaived(Event pEvent) {
		DataSet 		myData		= ((List)getList()).getData();
		AcctPrice.List 	myPrices	= myData.getPrices();
		Money			myAmount 	= pEvent.getAmount();
		AcctPrice		myPrice;
		Money			myReduction;
		Money			myPortion;

		/* Adjust the total amount invested into this account */
		theInvestment.subtractAmount(myAmount);
		
		/* Get the appropriate price for the account */
		myPrice  = myPrices.getLatestPrice(pEvent.getDebit(),
										   pEvent.getDate());
		thePrice = myPrice.getPrice();
		
		/* Determine value of this stock at the current time */
		theValue = theUnits.valueAtPrice(thePrice);
		
		/* Calculate the portion of the value that creates a large transaction */
		myPortion = theValue.valueAtRate(rateLimit);
		
		/* If this is a large stock waiver (> both valueLimit and rateLimit of value) */
		if ((myAmount.getValue() > valueLimit.getValue()) &&
			(myAmount.getValue() > myPortion.getValue()))
		{
			/* Determine the total value of rights plus share value */
			Money myTotalValue = new Money(myAmount);
			myTotalValue.addAmount(myAmount);
			
			/* Determine the reduction as a proportion of the total value */
			myReduction = theTotalCost.valueAtWeight(myAmount, myTotalValue);						
		}
		
		/* else this is viewed as small and is taken out of the cost */
		else {
			/* Set the reduction to be the entire amount */
			myReduction = new Money(myAmount);
		}
		
		/* If the reduction is greater than the total cost */
		if (myReduction.getValue() > theTotalCost.getValue()) {
			/* Reduction is the total cost */
			myReduction = new Money(theTotalCost);
		}
		
		/* Adjust the total cost */
		theDeltaCost = new Money(myReduction);
		theDeltaCost.negate();
		theTotalCost.addAmount(theDeltaCost);
		
		/* Adjust the gains */
		theDeltaGains = new Money(myAmount);
		theDeltaGains.addAmount(theDeltaCost);
		theTotalGains.addAmount(theDeltaGains);
	}
	
	/**
	 * Process an event that is Stock DeMerger.
	 * This capital event relates to both the Credit and Debit accounts
	 * @param pEvent the event
	 */
	private void processStockDeMerger(Event pEvent) {
		Dilution	myDilution 	= pEvent.getDilution();
		Units		myUnits		= pEvent.getUnits();
		Money		myCost;

		/* Calculate the diluted value of the Debit account */
		myCost = theTotalCost.getDilutedAmount(myDilution);
		
		/* Calculate the delta to the cost */
		theDeltaCost = new Money(myCost);
		theDeltaCost.subtractAmount(theTotalCost);
		
		/* Record the new total cost */
		theTotalCost = new Money(myCost);
		
		/* Adjust the investment for the debit account */
		theInvestment.addAmount(theDeltaCost);
		
		/* The deltaCost is transferred to the credit account */
		theDeltaCost = new Money(theDeltaCost);
		theDeltaCost.negate();
		
		/* Add the deltaCost to investment and total cost of credit account */
		theInvestment.addAmount(theDeltaCost);
		theTotalCost.addAmount(theDeltaCost);
		
		/* Adjust the units for the credit account */
		theUnits.addUnits(myUnits);
	}
	
	/**
	 * Process an event that is StockTakeover.
	 * This capital event relates to both the Credit and Debit accounts
	 * In particular it makes reference to the CashTakeOver aspect of the debit account
	 * @param pEvent the event
	 */
	private void processStockTakeover(Event pEvent) {
		DataSet 		myData		= ((List)getList()).getData();
		AcctPrice.List 	myPrices	= myData.getPrices();
		Units			myUnits		= pEvent.getUnits();
		AcctPrice		myPrice;
		Money			myStockCost;
		Money			myCashCost;
		Money			myTotalCost;
		
		/* Adjust the units for the credit account */
		theUnits.addUnits(myUnits);

		/* If we have a Cash TakeOver component in the debit */
		if (theCashTakeover != null) {
			/* Get the appropriate price for the credit account */
			myPrice  = myPrices.getLatestPrice(pEvent.getCredit(),
										   	   pEvent.getDate());
			thePrice = myPrice.getPrice();
			
			/* Determine value of the stock part of the takeover */
			theValue = myUnits.valueAtPrice(thePrice);
			
			/* Calculate the total cost of the takeover */
			myTotalCost = new Money(theCashTakeover);
			myTotalCost.addAmount(theValue);
		
			/* Split the total cost of the debit account between stock and cash */
			myStockCost = theTotalCost.valueAtWeight(theValue, myTotalCost);
			myCashCost  = new Money(myTotalCost);
			myCashCost.subtractAmount(myStockCost);
			
			/* The debit cost now becomes zero */
			theDeltaCost = new Money(theTotalCost);
			theTotalCost.setZero();
			
			/* The Delta Gains is the Amount minus the CashCost */
			theDeltaGains = new Money(theCashTakeover);
			theDeltaGains.subtractAmount(myCashCost);
			theTotalGains.addAmount(theDeltaGains);

			/* The units now becomes zero */
			theUnits.setZero();

			/* The cost of the credit account is the Stock Cost */
			theDeltaCost = new Money(myStockCost);
			theTotalCost.addAmount(theDeltaCost);
			theInvestment.addAmount(theDeltaCost);
		}
		
		/* else there is no cash part to this takeover */
		else {
			/* Simply transfer the cost from the debit account */
			theTotalCost.addAmount(theTotalCost);
			theDeltaCost = new Money(theTotalCost);
			theInvestment.addAmount(theDeltaCost);
			
			/* The debit cost now becomes zero */
			theDeltaCost = new Money(theTotalCost);
			theDeltaCost.negate();
			theTotalCost.setZero();
			
			/* The units now becomes zero */
			theUnits.setZero();
		}		
	}
	
	/**
	 * Process an event that is the Cash portion of a StockTakeOver.
	 * This capital event relates only to the Debit Account
	 * @param pEvent the event
	 */
	private void processCashTakeover(Event pEvent) {
		DataSet 		myData		= ((List)getList()).getData();
		AcctPrice.List 	myPrices	= myData.getPrices();
		Money			myAmount 	= pEvent.getAmount();
		AcctPrice	 	myPrice;
		Money			myPortion;
		Money			myReduction;

		/* Adjust the total amount invested into this account */
		theInvestment.subtractAmount(myAmount);
		
		/* Get the appropriate price for the account */
		myPrice  = myPrices.getLatestPrice(pEvent.getDebit(),
										   pEvent.getDate());
		thePrice = myPrice.getPrice();
		
		/* Determine value of this stock at the current time */
		theValue = theUnits.valueAtPrice(thePrice);
		
		/* Calculate the portion of the value that creates a large transaction */
		myPortion = theValue.valueAtRate(rateLimit);
		
		/* If this is a large cash takeover portion (> both valueLimit and rateLimit of value) */
		if ((myAmount.getValue() > valueLimit.getValue()) &&
			(myAmount.getValue() > myPortion.getValue()))
		{
			/* We have to defer the allocation of cost until we know of the Stock takeover part */
			theCashTakeover = new Money(myAmount);
		}
		
		/* else this is viewed as small and is taken out of the cost */
		else {
			/* Set the reduction to be the entire amount */
			myReduction = new Money(myAmount);
		
			/* If the reduction is greater than the total cost */
			if (myReduction.getValue() > theTotalCost.getValue()) {
				/* Reduction is the total cost */
				myReduction = new Money(theTotalCost);
			}
				
			/* Adjust the total cost */
			theDeltaCost = new Money(myReduction);
			theDeltaCost.negate();
			theTotalCost.addAmount(theDeltaCost);
		
			/* Adjust the gains */
			theDeltaGains = new Money(myAmount);
			theDeltaGains.addAmount(theDeltaCost);
			theTotalGains.addAmount(theDeltaGains);
		}
	}
	
	/* The List of capital events */
	public static class List extends DataList<CapitalEvent> {
		/* Members */
		private DataSet			theData			= null;
		private Account			theAccount		= null;
		private Money			theTotalCost	= null;
		private Units			theUnits		= null;
		private Money			theTotalGains	= null;
	
		/* Access methods */
		public DataSet			getData()		{ return theData; }
		public Account 			getAccount() 	{ return theAccount; }
		public Money 			getTotalCost() 	{ return theTotalCost; }
		public Units 			getUnits() 		{ return theUnits; }
		public Money 			getTotalGains() { return theTotalGains; }

		/** 
	 	 * Construct an empty Capital event list
	 	 * @param pAccount the Account for the list
	 	 */
		protected List(DataSet pData, Account pAccount) { 
			super(ListStyle.VIEW, false);
			
			/* Store the account */
			theData			= pData;
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
		 * @param pPrevious the previous capital event
		 * @return the newly created capital event
		 */
		protected CapitalEvent addEvent(Event 			pEvent,
										CapitalEvent	pPrevious) {
			CapitalEvent myEvent;
			
			/* Create the Capital Event and add to list */
			myEvent = new CapitalEvent(this, pEvent, pPrevious);
			myEvent.addToList();
			
			/* return the new event */
			return myEvent;
		}
	}
}
