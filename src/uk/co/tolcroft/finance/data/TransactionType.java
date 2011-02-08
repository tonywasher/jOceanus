package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class TransactionType extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "TransactionType";

	/**
	 * TransactionType Name length
	 */
	public final static int NAMELEN = 50;

	/**
	 * The class of the Transaction Type
	 */
	private TransClass   theClass = null;

	/**
	 * The sort order of the Transaction Type
	 */
	private int          theOrder = -1;
	
	/**
	 * Return the name of the Transaction Type
	 * @return the name
	 */
	public String     	getName()              { return getObj().getName(); }

	/**
	 * Return the sort order of the Transaction Type
	 * @return the order
	 */
	public int        	getOrder()             { return theOrder; }

	/**
	 * Return the Transaction class of the Transaction Type
	 * @return the class
	 */
	public TransClass 	getTranClass()         { return theClass; }

	/* Linking methods */
	public TransactionType 	getBase() { return (TransactionType)super.getBase(); }
	public Values   		getObj()  { return (Values)super.getObj(); }	

	/* Field IDs */
	public static final int FIELD_ID     	= 0;
	public static final int FIELD_NAME     	= 1;
	public static final int FIELD_ORDER     = 2;
	public static final int FIELD_CLASS     = 3;
	public static final int NUMFIELDS	    = 4;
	
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
			case FIELD_ID: 	  return "ID";
			case FIELD_NAME:  return "Name";
			case FIELD_ORDER: return "Order";
			case FIELD_CLASS: return "Class";
			default:		  return super.fieldName(iField);
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
			case FIELD_ID: 		myString += getId();  	break;
			case FIELD_NAME:	myString += getName(); 	break;
			case FIELD_ORDER: 	myString += getOrder();	break;
			case FIELD_CLASS: 	myString += theClass;	break;
		}
		return myString + "</td></tr>";
	}
	
	/**
	 * Construct a copy of a Transaction Type.
	 * 
	 * @param pList	The list to associate the Transaction Type with
	 * @param pTransType The Transaction Type to copy 
	 */
	protected TransactionType(List 				pList,
                              TransactionType   pTransType) { 
		super(pList, pTransType.getId());
		Values myObj = new Values(pTransType.getObj());
		setObj(myObj);
		theClass = pTransType.theClass;
		setBase(pTransType);
		setState(pTransType.getState());
		theOrder = pTransType.getOrder();
	}

	/**
	 * Construct a standard Transaction type on load
	 * 
	 * @param pList	The list to associate the Transaction Type with
	 * @param uId   ID of Transaction Type
	 * @param sName Name of Transaction Type
	 */
	private TransactionType(List 	pList,
                       		long    uId,
                       		String	sName) throws Exception {
		super(pList, uId);
		Values myObj = new Values();
		setObj(myObj);
		myObj.setName(sName);
		pList.setNewId(this);				
	
		/* Determine class of Transaction */
		if (sName.equals("TaxedIncome")) {
			theClass = TransClass.TAXEDINCOME;
			theOrder = 0;
		}
		else if (sName.equals("Interest")) {
			theClass = TransClass.INTEREST;
			theOrder = 1;
		}
		else if (sName.equals("Dividend")) {
			theClass = TransClass.DIVIDEND;
			theOrder = 2;
		}
		else if (sName.equals("TaxFreeIncome")) {
			theClass = TransClass.TAXFREEINCOME;
			theOrder = 3;
		}
		else if (sName.equals("Inherited")) {
			theClass = TransClass.INHERITED;
			theOrder = 4;
		}
		else if (sName.equals("DebtInterest")) {
			theClass = TransClass.DEBTINTEREST;
			theOrder = 5;
		}
		else if (sName.equals("RentalIncome")) {
			theClass = TransClass.RENTALINCOME;
			theOrder = 6;
		}
		else if (sName.equals("MarketIncome")) {
			theClass = TransClass.MKTINCOME;
			theOrder = 7;
		}
		else if (sName.equals("Benefit")) {
			theClass = TransClass.BENEFIT;
			theOrder = 8;
		}
		else if (sName.equals("TaxRefund")) {
			theClass = TransClass.TAXREFUND;
			theOrder = 9;
		}
		else if (sName.equals("Recovered")) {
			theClass = TransClass.RECOVERED;
			theOrder = 10;
		}
		else if (sName.equals("TaxRelief")) {
			theClass = TransClass.TAXRELIEF;
			theOrder = 11;
		}
		else if (sName.equals("StockDemerger")) {
			theClass = TransClass.STOCKDEMERGER;
			theOrder = 12;
		}
		else if (sName.equals("StockSplit")) {
			theClass = TransClass.STOCKSPLIT;
			theOrder = 13;
		}
		else if (sName.equals("StockRightTaken")) {
			theClass = TransClass.STOCKRIGHTTAKEN;
			theOrder = 14;
		}
		else if (sName.equals("StockRightWaived")) {
			theClass = TransClass.STOCKRIGHTWAIVED;
			theOrder = 15;
		}
		else if (sName.equals("CashTakeover")) {
			theClass = TransClass.CASHTAKEOVER;
			theOrder = 16;
		}
		else if (sName.equals("StockTakeover")) {
			theClass = TransClass.STOCKTAKEOVER;
			theOrder = 17;
		}
		else if (sName.equals("Transfer")) {
			theClass = TransClass.TRANSFER;
			theOrder = 18;
		}
		else if (sName.equals("CashRecovery")) {
			theClass = TransClass.CSHRECOVER;
			theOrder = 19;
		}
		else if (sName.equals("CashPayment")) {
			theClass = TransClass.CSHPAY;
			theOrder = 20;
		}
		else if (sName.equals("Expense")) {
			theClass = TransClass.EXPENSE;
			theOrder = 21;
		}
		else if (sName.equals("Endowment")) {
			theClass = TransClass.ENDOWMENT;
			theOrder = 22;
		}
		else if (sName.equals("Mortgage")) {
			theClass = TransClass.MORTGAGE;
			theOrder = 23;
		}
		else if (sName.equals("Insurance")) {
			theClass = TransClass.INSURANCE;
			theOrder = 24;
		}
		else if (sName.equals("ExtraTax")) {
			theClass = TransClass.EXTRATAX;
			theOrder = 25;
		}
		else if (sName.equals("WriteOff")) {
			theClass = TransClass.WRITEOFF;
			theOrder = 26;
		}
		else if (sName.equals("NatInsurance")) {
			theClass = TransClass.NATINSURANCE;
			theOrder = 27;
		}
		else if (sName.equals("TaxOwed")) {
			theClass = TransClass.TAXOWED;
			theOrder = 28;
		}
		else if (sName.equals("TaxCredit")) {
			theClass = TransClass.TAXCREDIT;
			theOrder = 29;
		}
		else if (sName.equals("MarketGrowth")) {
			theClass = TransClass.MKTGROWTH;
			theOrder = 30;
		}
		else if (sName.equals("MarketShrink")) {
			theClass = TransClass.MKTSHRINK;
			theOrder = 31;
		}
		else if (sName.equals("UnitTrustDividend")) {
			theClass = TransClass.UNITTRUSTDIV;
			theOrder = 32;
		}
		else if (sName.equals("TaxFreeInterest")) {
			theClass = TransClass.TAXFREEINTEREST;
			theOrder = 33;
		}
		else if (sName.equals("TaxFreeDividend")) {
			theClass = TransClass.TAXFREEDIVIDEND;
			theOrder = 34;
		}
		else if (sName.equals("TaxableGain")) {
			theClass = TransClass.TAXABLEGAIN;
			theOrder = 35;
		}
		else if (sName.equals("CapitalGain")) {
			theClass = TransClass.CAPITALGAIN;
			theOrder = 36;
		}
		else if (sName.equals("CapitalLoss")) {
			theClass = TransClass.CAPITALLOSS;
			theOrder = 37;
		}
		else {
			throw new Exception(ExceptionClass.DATA,
   					  			this,
                                "Invalid TransType");
		}
	}

	/**
	 * Compare this transaction type to another to establish equality.
	 * @param pThat The Transaction type to compare to
	 * @return <code>true</code> if the transaction type is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a TransactionType */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target transactionType */
		TransactionType myType = (TransactionType)pThat;

		if (getId() != myType.getId()) return false;
		return (getName().compareTo(myType.getName()) == 0);
	}

	/**
	 * Compare this transaction type to another to establish sort order. 
	 * @param pThat The Transaction type to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		long result;

		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a TransactionType */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the target transactionType */
		TransactionType myThat = (TransactionType)pThat;
		
		/* Compare on order */
		if (theOrder < myThat.theOrder) return -1;
		if (theOrder > myThat.theOrder) return  1;
		
		/* Compare on name */
		result = getName().compareTo(myThat.getName());
		if (result < 0) return -1;
		if (result > 0) return 1;
		
		/* Compare on id */
		result = (int)(getId() - myThat.getId());
		if (result == 0) return 0;
		else if (result < 0) return -1;
		else return 1;
	}

	/**
	 * Determine whether the TransactionType is a transfer
	 * 
	 * @return <code>true</code> if the transaction is transfer, <code>false</code> otherwise.
	 */
	public boolean isTransfer()  { 
		return (theClass == TransClass.TRANSFER); }
	
	/**
	 * Determine whether the TransactionType is a dividend
	 * 
	 * @return <code>true</code> if the transaction is dividend, <code>false</code> otherwise.
	 */
	public boolean isDividend()  { 
		return (theClass == TransClass.DIVIDEND); }
	
	/**
	 * Determine whether the TransactionType is a interest
	 * 
	 * @return <code>true</code> if the transaction is interest, <code>false</code> otherwise.
	 */
	public boolean isInterest()  { 
		return (theClass == TransClass.INTEREST); }

	/**
	 * Determine whether the TransactionType is a cash payment
	 * 
	 * @return <code>true</code> if the transaction is cash payment, <code>false</code> otherwise.
	 */
	public boolean isCashPayment()  { 
		return (theClass == TransClass.CSHPAY); }
	
	/**
	 * Determine whether the TransactionType is a cash recovery
	 * 
	 * @return <code>true</code> if the transaction is cash recovery, <code>false</code> otherwise.
	 */
	public boolean isCashRecovery()  { 
		return (theClass == TransClass.CSHRECOVER); }
	
	/**
	 * Determine whether the TransactionType is a write off
	 * 
	 * @return <code>true</code> if the transaction is write off, <code>false</code> otherwise.
	 */
	protected boolean isWriteOff()  { 
		return (theClass == TransClass.WRITEOFF); }
	
	/**
	 * Determine whether the TransactionType is a market income
	 * 
	 * @return <code>true</code> if the transaction is market income, <code>false</code> otherwise.
	 */
	protected boolean isMktIncome()  { 
		return (theClass == TransClass.MKTINCOME); }
	
	/**
	 * Determine whether the TransactionType is a inheritance
	 * 
	 * @return <code>true</code> if the transaction is inheritance, <code>false</code> otherwise.
	 */
	protected boolean isInherited()  { 
		return (theClass == TransClass.INHERITED); }
	
	/**
	 * Determine whether the TransactionType is a tax owed
	 * 
	 * @return <code>true</code> if the transaction is tax owed, <code>false</code> otherwise.
	 */
	protected boolean isTaxOwed()  { 
		return (theClass == TransClass.TAXOWED); }
	
	/**
	 * Determine whether the TransactionType is a tax refund
	 * 
	 * @return <code>true</code> if the transaction is tax refund, <code>false</code> otherwise.
	 */
	protected boolean isTaxRefund()  { 
		return (theClass == TransClass.TAXREFUND); }
	
	/**
	 * Determine whether the TransactionType is a tax relief
	 * 
	 * @return <code>true</code> if the transaction is tax relief, <code>false</code> otherwise.
	 */
	protected boolean isTaxRelief()  { 
		return (theClass == TransClass.TAXRELIEF); }
	
	/**
	 * Determine whether the TransactionType is a debt interest
	 * 
	 * @return <code>true</code> if the transaction is debt interest, <code>false</code> otherwise.
	 */
	protected boolean isDebtInterest()  { 
		return (theClass == TransClass.DEBTINTEREST); }
	
	/**
	 * Determine whether the TransactionType is a rental income
	 * 
	 * @return <code>true</code> if the transaction is rental income, <code>false</code> otherwise.
	 */
	protected boolean isRentalIncome()  { 
		return (theClass == TransClass.RENTALINCOME); }
	
	/**
	 * Determine whether the TransactionType is a benefit
	 * 
	 * @return <code>true</code> if the transaction is benefit, <code>false</code> otherwise.
	 */
	protected boolean isBenefit()  { 
		return (theClass == TransClass.BENEFIT); }

	/**
	 * Determine whether the TransactionType is a taxable gain
	 * 
	 * @return <code>true</code> if the transaction is taxable gain, <code>false</code> otherwise.
	 */
	public boolean isTaxableGain()  { 
		return (theClass == TransClass.TAXABLEGAIN); }

	/**
	 * Determine whether the TransactionType is a capital gain
	 * 
	 * @return <code>true</code> if the transaction is capital gain, <code>false</code> otherwise.
	 */
	public boolean isCapitalGain()  { 
		return (theClass == TransClass.CAPITALGAIN); }

	/**
	 * Determine whether the TransactionType is a capital loss
	 * 
	 * @return <code>true</code> if the transaction is capital loss, <code>false</code> otherwise.
	 */
	public boolean isCapitalLoss()  { 
		return (theClass == TransClass.CAPITALLOSS); }

	/**
	 * Determine whether the TransactionType is a stock split
	 * 
	 * @return <code>true</code> if the transaction is stock split, <code>false</code> otherwise.
	 */
	public boolean isStockSplit()  { 
		return (theClass == TransClass.STOCKSPLIT); }

	/**
	 * Determine whether the TransactionType is a stock demerger
	 * 
	 * @return <code>true</code> if the transaction is stock demerger, <code>false</code> otherwise.
	 */
	public boolean isStockDemerger()  { 
		return (theClass == TransClass.STOCKDEMERGER); }

	/**
	 * Determine whether the TransactionType is a stock right taken
	 * 
	 * @return <code>true</code> if the transaction is stock right taken, <code>false</code> otherwise.
	 */
	public boolean isStockRightTaken()  { 
		return (theClass == TransClass.STOCKRIGHTTAKEN); }

	/**
	 * Determine whether the TransactionType is a stock right waived
	 * 
	 * @return <code>true</code> if the transaction is stock right waived, <code>false</code> otherwise.
	 */
	public boolean isStockRightWaived()  { 
		return (theClass == TransClass.STOCKRIGHTWAIVED); }

	/**
	 * Determine whether the TransactionType is a cash takeover
	 * 
	 * @return <code>true</code> if the transaction is cash takeover, <code>false</code> otherwise.
	 */
	public boolean isCashTakeover()  { 
		return (theClass == TransClass.CASHTAKEOVER); }

	/**
	 * Determine whether the TransactionType is a stock takeover
	 * 
	 * @return <code>true</code> if the transaction is stock takeover, <code>false</code> otherwise.
	 */
	public boolean isStockTakeover()  { 
		return (theClass == TransClass.STOCKTAKEOVER); }

	/**
	 * Determine whether the TransactionType is a market adjustment
	 * 
	 * @return <code>true</code> if the transaction is market adjustment, <code>false</code> otherwise.
	 */
	public boolean isMarketAdjust() { 
		switch (theClass) {
			case MKTGROWTH:
			case MKTSHRINK:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Determine whether the TransactionType is a recovery
	 * 
	 * @return <code>true</code> if the transaction is recovery, <code>false</code> otherwise.
	 */
	public boolean isRecovered()  { 
		switch (theClass) {
			case RECOVERED:
			case CSHPAY:
			case CSHRECOVER:
				return true;
			default:
				return false;
		}
	}		
	
	/**
	 * Determine whether the TransactionType is hidden
	 * 
	 * @return <code>true</code> if the transaction is hidden, <code>false</code> otherwise.
	 */
	public boolean isHidden()  { 
		switch (theClass) {
			case UNITTRUSTDIV:
			case TAXFREEDIVIDEND:
			case TAXFREEINTEREST:
			case MKTSHRINK:
			case MKTGROWTH:
			case TAXCREDIT:
				return true;
			default:
				return false;
		}
	}		
	
	/**
	/**
	 * Determine whether the TransactionType is a tax credit
	 * 
	 * @return <code>true</code> if the transaction is tax credit, <code>false</code> otherwise.
	 */
	protected boolean isTaxCredit() { 
		switch (theClass) {
			case NATINSURANCE:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Determine whether the TransactionType should have a tax credit
	 * 
	 * @return <code>true</code> if the transaction should have a tax credit, <code>false</code> otherwise.
	 */
	public boolean needsTaxCredit() { 
		switch (theClass) {
			case TAXEDINCOME:
			case INTEREST:
			case DIVIDEND:
			case UNITTRUSTDIV:
			case TAXABLEGAIN:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Determine whether the TransactionType is an income
	 * 
	 * @return <code>true</code> if the transaction is income, <code>false</code> otherwise.
	 */
	protected boolean isIncome()   { 
		switch (theClass) {
			case TAXEDINCOME:
			case TAXFREEINCOME:
			case INTEREST:
			case DIVIDEND:
			case UNITTRUSTDIV:
			case RECOVERED:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Determine whether the TransactionType is an expense
	 * 
	 * @return <code>true</code> if the transaction is expense, <code>false</code> otherwise.
	 */
	protected boolean isExpense()   { 
		switch (theClass) {
			case MORTGAGE:
			case ENDOWMENT:
			case EXTRATAX:
			case INSURANCE:
			case EXPENSE:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Represents a list of {@link TransType} objects. 
	 */
	public static class List extends DataList<TransactionType> {
	 	/** 
	 	 * Construct an empty CORE transaction type list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(DataSet pData) { super(ListStyle.CORE, true); }

		/** 
	 	 * Construct a generic transtype list
	 	 * @param pList the source transtype list 
	 	 * @param pStyle the style of the list 
	 	 */
		public List(List pList, ListStyle pStyle) { super(pList, pStyle); }

		/** 
	 	 * Construct a difference transtype list
	 	 * @param pNew the new TransType list 
	 	 * @param pOld the old TransType list 
	 	 */
		protected List(List pNew, List pOld) { super(pNew, pOld); }
	
		/** 
	 	 * Clone a TransType list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return new List(this, ListStyle.CORE); }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pItem item to be added
		 * @return the newly added item
		 */
		public DataItem addNewItem(DataItem pItem) {
			TransactionType myType = new TransactionType(this, (TransactionType)pItem);
			myType.addToList();
			return myType;
		}
	
		/**
		 * Create a new empty element in the edit list (null-operation)
		 * @param isCredit - is the item a credit or debit
		 */
		public void addNewItem(boolean isCredit) {};
			
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }
		
		/**
		 * Search for a particular item by class
		 *  
		 * @param eClass The class of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		public TransactionType searchFor(TransClass eClass) {
			ListIterator 	myIterator;
			TransactionType myCurr;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				if (myCurr.theClass == eClass) break;
			}
			
			/* Return to caller */
			return myCurr;
		}
		
		/**
		 * Search for a particular item by Name
		 * 
		 * @param sName The name of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		public TransactionType searchFor(String sName) {
			ListIterator 	myIterator;
			TransactionType myCurr;
			int       		iDiff;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				iDiff = sName.compareTo(myCurr.getName());
				if (iDiff == 0) break;
			}
			
			/* Return to caller */
			return myCurr;
		}
		
		/**
		 * Add a TransactionType
		 * @param uId the Id of the transaction type
		 * @param pTransType the Name of the transaction type
		 */ 
		public void addItem(long   uId,
				            String pTransType) throws Exception {
			TransactionType     myTransType;
			
			/* Create a new Transaction Type */
			myTransType = new TransactionType(this, uId, pTransType);
			
			/* Check that this TransTypeId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
	   					  			myTransType,
			  			            "Duplicate TransTypeId");
				 
			/* Check that this TransactionType has not been previously added */
			if (searchFor(pTransType) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTransType,
			                        "Duplicate Transaction Type");
				
			/* Add the Transaction Type to the list */
			myTransType.addToList();		
		}			
	}
	
	/**
	 * Values for a transaction type 
	 */
	public class Values implements histObject {
		private String     		theName      = null;
		
		/* Access methods */
		public String      	getName()      { return theName; }
		
		public void setName(String pName) {
			theName      = pName; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) {
			theName      = pValues.getName();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (Utils.differs(theName,    pValues.theName))    return false;
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
			theName      = pValues.getName();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case FIELD_NAME:
					bResult = (Utils.differs(theName,      pValues.theName));
					break;
			}
			return bResult;
		}
	}
	
	/**
	 * Enumeration of Transaction Type Classes. 
	 */
	public enum TransClass {
		/**
		 * Taxed Salary Income
		 */
		TAXEDINCOME,

		/**
		 * Interest Income
		 */
		INTEREST,

		/**
		 * Dividend Income
		 */
		DIVIDEND,

		/**
		 * Unit Trust Dividend Income
		 */
		UNITTRUSTDIV,

		/**
		 * Taxable Gain
		 */
		TAXABLEGAIN,

		/**
		 * Capital Gain
		 */
		CAPITALGAIN,

		/**
		 * Capital Loss
		 */
		CAPITALLOSS,

		/**
		 * Tax Free Interest
		 */
		TAXFREEINTEREST,

		/**
		 * Tax Free Dividend
		 */
		TAXFREEDIVIDEND,

		/**
		 * Tax Free Income
		 */
		TAXFREEINCOME,

		/**
		 * Benefit
		 */
		BENEFIT,     

		/**
		 * Inheritance
		 */
		INHERITED,

		/**
		 * Market Growth
		 */
		MKTGROWTH,

		/**
		 * Market Shrinkage
		 */
		MKTSHRINK,

		/**
		 * Market Income
		 */
		MKTINCOME,

		/**
		 * Expense
		 */
		EXPENSE,

		/**
		 * Recovered Expense
		 */
		RECOVERED,

		/**
		 * Transfer
		 */
		TRANSFER,

		/**
		 * Stock Split
		 */
		STOCKSPLIT,

		/**
		 * Stock Demerger
		 */
		STOCKDEMERGER,

		/**
		 * Stock Rights Taken
		 */
		STOCKRIGHTTAKEN,

		/**
		 * Stock Rights Waived
		 */
		STOCKRIGHTWAIVED,

		/**
		 * CashTakeover (For the cash part of a stock and cash takeover)
		 */
		CASHTAKEOVER,

		/**
		 * Stock Takeover (for the stock part of a stock and cash takeover)
		 */
		STOCKTAKEOVER,

		/**
		 * Expense Recovered directly to Cash
		 */
		CSHRECOVER,

		/**
		 * Expense paid directly from Cash
		 */
		CSHPAY,

		/**
		 * Endowment payment
		 */
		ENDOWMENT,

		/**
		 * Mortgage charge
		 */
		MORTGAGE,

		/**
		 * Insurance payment
		 */
		INSURANCE,

		/**
		 * National Insurance
		 */
		NATINSURANCE,

		/**
		 * Tax Relief
		 */
		TAXRELIEF,    

		/**
		 * Tax Owed
		 */
		TAXOWED,      

		/**
		 * Tax Refund
		 */
		TAXREFUND,    

		/**
		 * Additional taxation
		 */
		EXTRATAX,

		/**
		 * Interest on Debts
		 */
		DEBTINTEREST, 

		/**
		 * Write Off
		 */
		WRITEOFF,

		/**
		 * Tax Credit
		 */
		TAXCREDIT,

		/**
		 * Rental Income
		 */
		RENTALINCOME;
	}	
}
