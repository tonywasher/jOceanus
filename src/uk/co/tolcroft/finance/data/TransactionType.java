package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.finance.data.StaticClass.TransClass;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.StaticData;

public class TransactionType extends StaticData<TransactionType, TransClass> {
	/**
	 * The name of the object
	 */
	public static final String objName = "TransactionType";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";

	/**
	 * Return the Transaction class of the Transaction Type
	 * @return the class
	 */
	public TransClass 	getTranClass()         { return super.getStaticClass(); }

	/**
	 * Hidden transaction types are always active
	 */
	public boolean		isActive()        		 { return super.isActive() || isHiddenType(); }

	/* Linking methods */
	public TransactionType 	getBase() { return (TransactionType)super.getBase(); }

	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	public String itemType() { return objName; }
	
	/**
	 * Construct a copy of a Transaction Type.
	 * 
	 * @param pList	The list to associate the Transaction Type with
	 * @param pTransType The Transaction Type to copy 
	 */
	protected TransactionType(List 				pList,
                              TransactionType   pTransType) { 
		super(pList, pTransType);
	}

	/**
	 * Construct a standard Transaction type on load
	 * @param pList	The list to associate the Transaction Type with
	 * @param sName Name of Transaction Type
	 */
	private TransactionType(List 	pList,
                      		String	sName) throws Exception {
		super(pList, sName);
	}

	/**
	 * Construct a standard transaction type on load
	 * @param pList	The list to associate the Transaction Type with
	 * @param uId   ID of Transaction Type
	 * @param isEnabled is the TransType enabled
	 * @param uOrder the sort order
	 * @param pName Name of Transaction Type
	 * @param pDesc Description of Transaction Type
	 */
	private TransactionType(List 	pList,
							int		uId,
			         		boolean	isEnabled,
			         		int		uOrder,
			         		String	pName,
			         		String	pDesc) throws Exception {
		super(pList, uId, isEnabled, uOrder, pName, pDesc);
	}
	
	/**
	 * Construct a standard Transaction type on load
	 * @param pList	The list to associate the Transaction Type with
	 * @param uId   ID of Transaction Type
	 * @param uControlId the control id of the new item
	 * @param isEnabled is the TransType enabled
	 * @param uOrder the sort order
	 * @param pName Encrypted Name of Transaction Type
	 * @param pDesc Encrypted Description of Transaction Type
	 */
	private TransactionType(List 	pList,
                       		int		uId,
                       		int		uControlId,
    			            boolean	isEnabled,
    			            int		uOrder,	
    			            byte[]	pName,
    			            byte[]	pDesc) throws Exception {
		super(pList, uId, uControlId, isEnabled, uOrder, pName, pDesc);
	}

	/**
	 * Determine whether the TransactionType is a transfer
	 * 
	 * @return <code>true</code> if the transaction is transfer, <code>false</code> otherwise.
	 */
	public boolean isTransfer()  { 
		return (getTranClass() == TransClass.TRANSFER); }
	
	/**
	 * Determine whether the TransactionType is a dividend
	 * 
	 * @return <code>true</code> if the transaction is dividend, <code>false</code> otherwise.
	 */
	public boolean isDividend()  { 
		return (getTranClass() == TransClass.DIVIDEND); }
	
	/**
	 * Determine whether the TransactionType is a interest
	 * 
	 * @return <code>true</code> if the transaction is interest, <code>false</code> otherwise.
	 */
	public boolean isInterest()  { 
		return (getTranClass() == TransClass.INTEREST); }

	/**
	 * Determine whether the TransactionType is a cash payment
	 * 
	 * @return <code>true</code> if the transaction is cash payment, <code>false</code> otherwise.
	 */
	public boolean isCashPayment()  { 
		return (getTranClass() == TransClass.CASHPAYMENT); }
	
	/**
	 * Determine whether the TransactionType is a cash recovery
	 * 
	 * @return <code>true</code> if the transaction is cash recovery, <code>false</code> otherwise.
	 */
	public boolean isCashRecovery()  { 
		return (getTranClass() == TransClass.CASHRECOVERY); }
	
	/**
	 * Determine whether the TransactionType is a write off
	 * 
	 * @return <code>true</code> if the transaction is write off, <code>false</code> otherwise.
	 */
	protected boolean isWriteOff()  { 
		return (getTranClass() == TransClass.WRITEOFF); }
	
	/**
	 * Determine whether the TransactionType is a inheritance
	 * 
	 * @return <code>true</code> if the transaction is inheritance, <code>false</code> otherwise.
	 */
	protected boolean isInherited()  { 
		return (getTranClass() == TransClass.INHERITED); }
	
	/**
	 * Determine whether the TransactionType is a tax owed
	 * 
	 * @return <code>true</code> if the transaction is tax owed, <code>false</code> otherwise.
	 */
	protected boolean isTaxOwed()  { 
		return (getTranClass() == TransClass.TAXOWED); }
	
	/**
	 * Determine whether the TransactionType is a tax refund
	 * 
	 * @return <code>true</code> if the transaction is tax refund, <code>false</code> otherwise.
	 */
	protected boolean isTaxRefund()  { 
		return (getTranClass() == TransClass.TAXREFUND); }
	
	/**
	 * Determine whether the TransactionType is a tax relief
	 * 
	 * @return <code>true</code> if the transaction is tax relief, <code>false</code> otherwise.
	 */
	protected boolean isTaxRelief()  { 
		return (getTranClass() == TransClass.TAXRELIEF); }
	
	/**
	 * Determine whether the TransactionType is a debt interest
	 * 
	 * @return <code>true</code> if the transaction is debt interest, <code>false</code> otherwise.
	 */
	protected boolean isDebtInterest()  { 
		return (getTranClass() == TransClass.DEBTINTEREST); }
	
	/**
	 * Determine whether the TransactionType is a rental income
	 * 
	 * @return <code>true</code> if the transaction is rental income, <code>false</code> otherwise.
	 */
	protected boolean isRentalIncome()  { 
		return (getTranClass() == TransClass.RENTALINCOME); }
	
	/**
	 * Determine whether the TransactionType is a benefit
	 * 
	 * @return <code>true</code> if the transaction is benefit, <code>false</code> otherwise.
	 */
	protected boolean isBenefit()  { 
		return (getTranClass() == TransClass.BENEFIT); }

	/**
	 * Determine whether the TransactionType is a taxable gain
	 * 
	 * @return <code>true</code> if the transaction is taxable gain, <code>false</code> otherwise.
	 */
	public boolean isTaxableGain()  { 
		return (getTranClass() == TransClass.TAXABLEGAIN); }

	/**
	 * Determine whether the TransactionType is a capital gain
	 * 
	 * @return <code>true</code> if the transaction is capital gain, <code>false</code> otherwise.
	 */
	public boolean isCapitalGain()  { 
		return (getTranClass() == TransClass.CAPITALGAIN); }

	/**
	 * Determine whether the TransactionType is a capital loss
	 * 
	 * @return <code>true</code> if the transaction is capital loss, <code>false</code> otherwise.
	 */
	public boolean isCapitalLoss()  { 
		return (getTranClass() == TransClass.CAPITALLOSS); }

	/**
	 * Determine whether the TransactionType is a stock split
	 * 
	 * @return <code>true</code> if the transaction is stock split, <code>false</code> otherwise.
	 */
	public boolean isStockSplit()  { 
		return (getTranClass() == TransClass.STOCKSPLIT); }

	/**
	 * Determine whether the TransactionType is an admin charge
	 * 
	 * @return <code>true</code> if the transaction is admin charge, <code>false</code> otherwise.
	 */
	public boolean isAdminCharge()  { 
		return (getTranClass() == TransClass.ADMINCHARGE); }

	/**
	 * Determine whether the TransactionType is a stock demerger
	 * 
	 * @return <code>true</code> if the transaction is stock demerger, <code>false</code> otherwise.
	 */
	public boolean isStockDemerger()  { 
		return (getTranClass() == TransClass.STOCKDEMERGER); }

	/**
	 * Determine whether the TransactionType is a stock right taken
	 * 
	 * @return <code>true</code> if the transaction is stock right taken, <code>false</code> otherwise.
	 */
	public boolean isStockRightTaken()  { 
		return (getTranClass() == TransClass.STOCKRIGHTTAKEN); }

	/**
	 * Determine whether the TransactionType is a stock right waived
	 * 
	 * @return <code>true</code> if the transaction is stock right waived, <code>false</code> otherwise.
	 */
	public boolean isStockRightWaived()  { 
		return (getTranClass() == TransClass.STOCKRIGHTWAIVED); }

	/**
	 * Determine whether the TransactionType is a cash takeover
	 * 
	 * @return <code>true</code> if the transaction is cash takeover, <code>false</code> otherwise.
	 */
	public boolean isCashTakeover()  { 
		return (getTranClass() == TransClass.CASHTAKEOVER); }

	/**
	 * Determine whether the TransactionType is a stock takeover
	 * 
	 * @return <code>true</code> if the transaction is stock takeover, <code>false</code> otherwise.
	 */
	public boolean isStockTakeover()  { 
		return (getTranClass() == TransClass.STOCKTAKEOVER); }

	/**
	 * Determine whether the TransactionType is a recovery
	 * 
	 * @return <code>true</code> if the transaction is recovery, <code>false</code> otherwise.
	 */
	public boolean isRecovered()  { 
		switch (getTranClass()) {
			case RECOVERED:
			case CASHPAYMENT:
			case CASHRECOVERY:
				return true;
			default:
				return false;
		}
	}		

	/**
	 * Determine whether the TransactionType is hidden type
	 * 
	 * @return <code>true</code> if the transaction is hidden, <code>false</code> otherwise.
	 */
	public boolean isHiddenType()  { 
		switch (getTranClass()) {
			case UNITTRUSTDIVIDEND:
			case TAXFREEDIVIDEND:
			case TAXFREEINTEREST:
			case MARKETSHRINK:
			case MARKETGROWTH:
			case TAXCREDIT:
			case CAPITALGAIN:
			case CAPITALLOSS:
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
		switch (getTranClass()) {
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
		switch (getTranClass()) {
			case TAXEDINCOME:
			case INTEREST:
			case DIVIDEND:
			case UNITTRUSTDIVIDEND:
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
		switch (getTranClass()) {
			case TAXEDINCOME:
			case TAXFREEINCOME:
			case INTEREST:
			case DIVIDEND:
			case UNITTRUSTDIVIDEND:
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
		switch (getTranClass()) {
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
	 * Determine whether the TransactionType is dilutable
	 *  
	 * @return <code>true</code> if the transaction is dilutable, <code>false</code> otherwise.
	 */
	public boolean isDilutable()   { 
		switch (getTranClass()) {
			case STOCKSPLIT:
			case STOCKDEMERGER:
			case STOCKRIGHTWAIVED:
			case STOCKRIGHTTAKEN:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Represents a list of {@link TransactionType} objects. 
	 */
	public static class List extends StaticList<List, TransactionType, TransClass> {
		protected Class<TransClass> getEnumClass() { return TransClass.class; }

		/** 
	 	 * Construct an empty CORE transaction type list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(FinanceData pData) { 
			super(List.class, TransactionType.class, pData, ListStyle.CORE);
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
		public List getEditList() 	{ return getExtractList(ListStyle.EDIT); }
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
		 * Construct a difference ControlData list
		 * @param pNew the new ControlData list 
		 * @param pOld the old ControlData list 
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
		 * Add a new item to the list
		 * @param pItem item to be added
		 * @return the newly added item
		 */
		public TransactionType addNewItem(DataItem<?> pItem) {
			TransactionType myType = new TransactionType(this, (TransactionType)pItem);
			add(myType);
			return myType;
		}
	
		/**
		 * Create a new empty element in the edit list (null-operation)
		 * @return the newly added item
		 */
		public TransactionType addNewItem() { return null; }
			
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }
		
		/**
		 * Add a TransactionType
		 * @param pTransType the Name of the transaction type
		 */ 
		public void addItem(String pTransType) throws Exception {
			TransactionType     myTransType;
			
			/* Create a new Transaction Type */
			myTransType = new TransactionType(this, pTransType);
			
			/* Check that this TransTypeId has not been previously added */
			if (!isIdUnique(myTransType.getId())) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTransType,
			  			            "Duplicate TranTypeId");
				 
			/* Check that this TransactionType has not been previously added */
			if (searchFor(pTransType) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTransType,
			                        "Duplicate Transaction Type");
				
			/* Add the Transaction Type to the list */
			add(myTransType);		
		}			

		/**
		 * Add a TransactionType to the list
		 * @param uId   ID of Transaction Type
		 * @param isEnabled is the TransType enabled
		 * @param uOrder the sort order
		 * @param pTranType the Name of the transaction type
		 * @param pDesc the Description of the transaction type
		 */ 
		public void addItem(int	   	uId,
							boolean	isEnabled,
							int		uOrder,
				            String 	pTranType,
				            String 	pDesc) throws Exception {
			TransactionType myTransType;
				
			/* Create a new Transaction Type */
			myTransType = new TransactionType(this, uId, isEnabled, uOrder, pTranType, pDesc);
				
			/* Check that this TransTypeId has not been previously added */
			if (!isIdUnique(myTransType.getId())) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTransType,
			  			            "Duplicate TranTypeId");
				 
			/* Add the Transaction Type to the list */
			add(myTransType);

			/* Validate the TransType */
			myTransType.validate();

			/* Handle validation failure */
			if (myTransType.hasErrors()) 
				throw new Exception(ExceptionClass.VALIDATE,
									myTransType,
									"Failed validation");		
		}	

		/**
		 * Add a TransactionType
		 * @param uId the Id of the transaction type
		 * @param uControlId the control id of the new item
		 * @param isEnabled is the TransType enabled
		 * @param uOrder the sort order
		 * @param pTransType the Encrypted Name of the transaction type
		 * @param pDesc the Encrypted Description of the transaction type
		 */ 
		public void addItem(int    	uId,
							int	   	uControlId,
							boolean	isEnabled,
							int		uOrder,
				            byte[] 	pTransType,
				            byte[] 	pDesc) throws Exception {
			TransactionType     myTransType;
			
			/* Create a new Transaction Type */
			myTransType = new TransactionType(this, uId, uControlId, isEnabled, uOrder, pTransType, pDesc);
			
			/* Check that this TransTypeId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
	   					  			myTransType,
			  			            "Duplicate TransTypeId");

			/* Add the Transaction Type to the list */
			add(myTransType);		

			/* Validate the TransType */
			myTransType.validate();

			/* Handle validation failure */
			if (myTransType.hasErrors()) 
				throw new Exception(ExceptionClass.VALIDATE,
									myTransType,
									"Failed validation");		
		}			
	}
}
