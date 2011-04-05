package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.finance.data.EncryptedPair.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class AccountType extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "AccountType";

	/**
	 * AccountType Name length
	 */
	public final static int NAMELEN = 50;

	/**
	 * The class of the Account Type
	 */
	private AccountClass	theClass = null;

	/**
	 * The sort order of the Account Type
	 */
	private int             theOrder = -1;
	
	/**
	 * Return the name of the Account Type
	 * @return the name
	 */
	public String 		getName() { 
		return getObj().getName().getValue(); }
	
	/**
	 * Return the encrypted name of the Account Type
	 * @return the encrypted name
	 */
	public byte[] 		getNameBytes() { 
		return getObj().getName().getBytes(); }
	
	/**
	 * Return the sort order of the Account Type
	 * @return the order
	 */
	public int   		getOrder()             	{ return theOrder; }
						
	/* Linking methods */
	public AccountType 	getBase()   { return (AccountType)super.getBase(); }
	public Values  	 	getObj()  	{ return (Values)super.getObj(); }	
	
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
		String myString = ""; 
		switch (iField) {
			case FIELD_ID: 		myString += getId();  	break;
			case FIELD_NAME:	myString += getName(); 	break;
			case FIELD_ORDER: 	myString += getOrder();	break;
			case FIELD_CLASS: 	myString += theClass;	break;
		}
		return myString;
	}
			
	/**
	 * Construct a copy of an Account Type.
	 * 
	 * @param pList	The list to associate the Account Type with
	 * @param pAcType The Account Type to copy 
	 */
	protected AccountType(List 			pList,
			              AccountType 	pAcType) { 
		super(pList, pAcType.getId());
		Values myObj = new Values(pAcType.getObj());
		setObj(myObj);
		theClass = pAcType.theClass;
		setBase(pAcType);
		setState(pAcType.getState());
		theOrder = pAcType.getOrder();
	}
	
	/**
	 * Construct a standard account type on load
	 * 
	 * @param pList	The list to associate the Account Type with
	 * @param sName Name of Account Type
	 */
	private AccountType(List 	pList,
			            String	sName) throws Exception {
		super(pList, 0);
		Values myObj = new Values();
		setObj(myObj);
		
		/* Create the Encrypted pair for the name */
		DataSet 		myData 	= pList.getData();
		EncryptedPair	myPairs = myData.getEncryptedPairs();
		myObj.setName(myPairs.new StringPair(sName));
	
		/* Determine class of Account */
		determineClass();

		/* Allocate the id */
		pList.setNewId(this);				
	}
	
	/**
	 * Construct a standard account type on load
	 * 
	 * @param pList	The list to associate the Account Type with
	 * @param uId   ID of Account Type
	 * @param pBytes Encrypted Name of Account Type
	 */
	private AccountType(List 	pList,
			            int		uId, 
			            byte[]	pBytes) throws Exception {
		super(pList, uId);
		Values myObj = new Values();
		setObj(myObj);
		
		/* Create the Encrypted pair for the name */
		DataSet 		myData 	= pList.getData();
		EncryptedPair	myPairs = myData.getEncryptedPairs();
		myObj.setName(myPairs.new StringPair(pBytes));
	
		/* Determine class of Account */
		determineClass();

		/* Allocate the id */
		pList.setNewId(this);				
	}
	
	/**
	 * Ensure encryption after spreadsheet load
	 */
	private void ensureEncryption() throws Exception {
		/* Protect against exceptions */
		try {
			/* Ensure the encryption */
			getObj().getName().ensureEncryption();
		}
		
		/* Catch exception */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								this,
								"Failed to complete encryption",
								e);
		}
	}
	
	/**
	 * Determine the class and order of the account type
	 */
	private void determineClass() throws Exception {
		String sName = getName();
	
		/* Determine class of Account */
		if (sName.equals("Current")) {
			theClass = AccountClass.CURRENT;
			theOrder = 0;
		}
		else if (sName.equals("Instant")) {
			theClass = AccountClass.INSTANT;
			theOrder = 1;
		}
		else if (sName.equals("Notice")) {
			theClass = AccountClass.NOTICE;
			theOrder = 2;
		}
		else if (sName.equals("Bond")) {
			theClass = AccountClass.BOND;
			theOrder = 3;
		}
		else if (sName.equals("CashISA")) {
			theClass = AccountClass.CASHISA;
			theOrder = 4;
		}
		else if (sName.equals("ISABond")) {
			theClass = AccountClass.ISABOND;
			theOrder = 5;
		}
		else if (sName.equals("TaxFreeBond")) {
			theClass = AccountClass.TAXFREEBOND;
			theOrder = 6;
		}
		else if (sName.equals("EquityBond")) {
			theClass = AccountClass.EQUITYBOND;
			theOrder = 7;
		}
		else if (sName.equals("Shares")) {
			theClass = AccountClass.SHARES;
			theOrder = 8;
		}
		else if (sName.equals("UnitTrust")) {
			theClass = AccountClass.UNITTRUST;
			theOrder = 9;
		}
		else if (sName.equals("LifeBond")) {
			theClass = AccountClass.LIFEBOND;
			theOrder = 10;
		}
		else if (sName.equals("UnitISA")) {
			theClass = AccountClass.UNITISA;
			theOrder = 11;
		}
		else if (sName.equals("Car")) {
			theClass = AccountClass.CAR;
			theOrder = 12;
		}
		else if (sName.equals("House")) {
			theClass = AccountClass.HOUSE;
			theOrder = 13;
		}
		else if (sName.equals("Endowment")) {
			theClass = AccountClass.ENDOWMENT;
			theOrder = 14;
		}
		else if (sName.equals("CreditCard")) {
			theClass = AccountClass.CREDITCARD;
			theOrder = 15;
		}
		else if (sName.equals("Debts")) {
			theClass = AccountClass.DEBTS;
			theOrder = 16;
		}
		else if (sName.equals("Deferred")) {
			theClass = AccountClass.DEFERRED;
			theOrder = 17;
		}
		else if (sName.equals("Employer")) {
			theClass = AccountClass.EMPLOYER;
			theOrder = 18;
		}
		else if (sName.equals("Cash")) {
			theClass = AccountClass.CASH;
			theOrder = 19;
		}
		else if (sName.equals("TaxMan")) {
			theClass = AccountClass.TAXMAN;
			theOrder = 20;
		}
		else if (sName.equals("Inheritance")) {
			theClass = AccountClass.INHERITANCE;
			theOrder = 21;
		}
		else if (sName.equals("WriteOff")) {
			theClass = AccountClass.WRITEOFF;
			theOrder = 22;
		}
		else if (sName.equals("Benefit")) {
			theClass = AccountClass.BENEFIT;
			theOrder = 23;
		}
		else if (sName.equals("External")) {
			theClass = AccountClass.EXTERNAL;
			theOrder = 24;
		}
		else if (sName.equals("Market")) {
			theClass = AccountClass.MARKET;
			theOrder = 25;
		}
		else {
			throw new Exception(ExceptionClass.DATA,
  					  			this,
                                "Invalid AccountType");
		}
	}
	
	/**
	 * Compare this account type to another to establish equality.
	 * @param pThat The Account type to compare to
	 * @return <code>true</code> if the account type is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is an AccountType */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target accountType */
		AccountType myThat = (AccountType)pThat;
		
		if (getId() != myThat.getId()) return false;
		return (getName().compareTo(myThat.getName()) == 0);
	}

	/**
	 * Compare this account type to another to establish sort order.
	 * 
	 * @param pThat The Account type to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		long result;

		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is an AccountType */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the target accountType */
		AccountType myThat = (AccountType)pThat;
		
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
	 * Determine whether the AccountType is external
	 * 
	 * @return <code>true</code> if the account is external, <code>false</code> otherwise.
	 */
	public boolean isExternal() {
		switch (theClass) {
			case EXTERNAL:
			case EMPLOYER:
			case INHERITANCE:
			case CASH:
			case WRITEOFF:
			case TAXMAN:
			case MARKET:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Determine whether the AccountType is special external
	 * 
	 * @return <code>true</code> if the account is special external, <code>false</code> otherwise.
	 */
	public boolean isSpecial() {
		switch (theClass) {
			case INHERITANCE:
			case CASH:
			case WRITEOFF:
			case TAXMAN:
			case MARKET:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Determine whether the AccountType is priced
	 * 
	 * @return <code>true</code> if the account is priced, <code>false</code> otherwise.
	 */
	public boolean isPriced() {
		switch (theClass) {
			case HOUSE:
			case CAR:
			case SHARES:
			case LIFEBOND:
			case UNITTRUST:
			case UNITISA:
				return true;
			default:
				return false;
		}
	}
		
	/**
	 * Determine whether the AccountType is dividend provider
	 * 
	 * @return <code>true</code> if the account is a dividend provider, <code>false</code> otherwise.
	 */
	public boolean isDividend() {
		switch (theClass) {
			case SHARES:
			case EMPLOYER:
			case UNITTRUST:
			case UNITISA:
				return true;
			default:
				return false;
		}
	}
		
	/**
	 * Determine whether the AccountType is unit dividend provider
	 * 
	 * @return <code>true</code> if the account is a unit dividend provider, <code>false</code> otherwise.
	 */
	public boolean isUnitTrust() {
		switch (theClass) {
			case UNITTRUST:
				return true;
			default:
				return false;
		}
	}
		
	/**
	 * Determine whether the AccountType is tax-free provider
	 * 
	 * @return <code>true</code> if the account is a tax free dividend provider, <code>false</code> otherwise.
	 */
	public boolean isTaxFree() {
		switch (theClass) {
			case UNITISA:
			case CASHISA:
			case ISABOND:
			case TAXFREEBOND:
				return true;
			default:
				return false;
		}
	}
		
	/**
	 * Determine whether the AccountType is savings
	 * 
	 * @return <code>true</code> if the account is savings, <code>false</code> otherwise.
	 */
	public boolean isMoney() {
		switch (theClass) {
			case CURRENT:
			case INSTANT:
			case NOTICE:
			case BOND:
			case CASHISA:
			case ISABOND:
			case TAXFREEBOND:
			case EQUITYBOND:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Determine whether the AccountType is a bond
	 * 
	 * @return <code>true</code> if the account is a bond, <code>false</code> otherwise.
	 */
	public boolean isBond() {
		switch (theClass) {
			case BOND:
			case ISABOND:
			case TAXFREEBOND:
			case EQUITYBOND:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Determine whether the AccountType is debt
	 * 
	 * @return <code>true</code> if the account is debt, <code>false</code> otherwise.
	 */
	protected boolean isDebt() {
		switch (theClass) {
			case DEBTS:
			case CREDITCARD:
			case DEFERRED:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Determine whether the AccountType is child
	 * 
	 * @return <code>true</code> if the account is child, <code>false</code> otherwise.
	 */
	public boolean isChild() {
		switch (theClass) {
			case CURRENT:
			case INSTANT:
			case NOTICE:
			case CASHISA:
			case BOND:
			case ISABOND:
			case TAXFREEBOND:
			case EQUITYBOND:
			case SHARES:
			case UNITTRUST:
			case LIFEBOND:
			case UNITISA:
			case CREDITCARD:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Determine whether the AccountType is reserved
	 * 
	 * @return <code>true</code> if the account is reserved, <code>false</code> otherwise.
	 */
	public boolean isReserved() {
		switch (theClass) {
			case DEFERRED:
			case TAXMAN:
			case CASH:
			case WRITEOFF:
			case MARKET:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Determine whether the AccountType can alias
	 * 
	 * @return <code>true</code> if the account can alias, <code>false</code> otherwise.
	 */
	public boolean canAlias() {
		switch (theClass) {
			case UNITISA:
			case UNITTRUST:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Determine whether the AccountType is subject to Capital Gains
	 * 
	 * @return <code>true</code> if the account is subject to Capital Gains, <code>false</code> otherwise.
	 */
	public boolean isCapitalGains() {
		switch (theClass) {
			case SHARES:
			case UNITTRUST:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Determine whether the AccountType is Capital
	 * 
	 * @return <code>true</code> if the account is Capital, <code>false</code> otherwise.
	 */
	public boolean isCapital() {
		switch (theClass) {
			case SHARES:
			case LIFEBOND:
			case UNITTRUST:
			case UNITISA:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Determine whether the AccountType is cash
	 * 
	 * @return <code>true</code> if the account is cash, <code>false</code> otherwise.
	 */
	protected boolean isCash()      { return (theClass == AccountClass.CASH); }

	/**
	 * Determine whether the AccountType is parents
	 * 
	 * @return <code>true</code> if the account is parents, <code>false</code> otherwise.
	 */
	protected boolean isInheritance()   { return (theClass == AccountClass.INHERITANCE); }

	/**
	 * Determine whether the AccountType is WriteOff
	 * 
	 * @return <code>true</code> if the account is writeoff, <code>false</code> otherwise.
	 */
	protected boolean isWriteOff()  { return (theClass == AccountClass.WRITEOFF); }

	/**
	 * Determine whether the AccountType is market
	 * 
	 * @return <code>true</code> if the account is market, <code>false</code> otherwise.
	 */
	protected boolean isMarket()    { return (theClass == AccountClass.MARKET); }

	/**
	 * Determine whether the AccountType is TaxMan
	 * 
	 * @return <code>true</code> if the account is taxman, <code>false</code> otherwise.
	 */
	protected boolean isTaxMan()    { return (theClass == AccountClass.TAXMAN); }

	/**
	 * Determine whether the AccountType is Employer
	 * 
	 * @return <code>true</code> if the account is employer, <code>false</code> otherwise.
	 */
	protected boolean isEmployer()    { return (theClass == AccountClass.EMPLOYER); }

	/**
	 * Determine whether the AccountType is endowment
	 * 
	 * @return <code>true</code> if the account is endowment, <code>false</code> otherwise.
	 */
	protected boolean isEndowment() { return (theClass == AccountClass.ENDOWMENT); }

	/**
	 * Determine whether the AccountType is deferred
	 * 
	 * @return <code>true</code> if the account is deferred, <code>false</code> otherwise.
	 */
	protected boolean isDeferred()  { return (theClass == AccountClass.DEFERRED); }

	/**
	 * Determine whether the AccountType is benefit
	 * 
	 * @return <code>true</code> if the account is benefit, <code>false</code> otherwise.
	 */
	protected boolean isBenefit()   { return (theClass == AccountClass.BENEFIT); }

	/**
	 * Determine whether the AccountType is a Share
	 *  
	 * @return <code>true</code> if the account is Share, <code>false</code> otherwise.
	 */
	public boolean isShares()   { return (theClass == AccountClass.SHARES); }

	/**
	 * Determine whether the AccountType is a LifeBond
	 * 
	 * @return <code>true</code> if the account is LifeBond, <code>false</code> otherwise.
	 */
	public boolean isLifeBond()   { return (theClass == AccountClass.LIFEBOND); }

	/**
	 * Determine whether the AccountType is internal
	 * 
	 * @return <code>true</code> if the account is internal, <code>false</code> otherwise.
	 */
	public boolean isInternal()  { return !isExternal(); }
	
	/**
	 * Format an AccountType 
	 * 
	 * @param pActType the account type to format
	 * @return the formatted account type
	 */
	public static String format(AccountType pActType) {
		String 	myFormat;
		myFormat = (pActType != null) ? pActType.getName()
							 	      : "null";
		return myFormat;
	}

	/**
	 * Determine whether two AccountType objects differ.
	 * 
	 * @param pCurr The current AccountType 
	 * @param pNew The new AccountType
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(AccountType pCurr, AccountType pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Represents a list of {@link AccountType} objects. 
	 */
	public static class List extends DataList<AccountType> {
		/**
		 * The DataSet that this list belongs to
		 */
		private DataSet theData = null;
		
		/**
		 * Access the owning DataSet
		 * @return the DataSet
		 */
		private DataSet getData() { return theData; }
		
	 	/** 
	 	 * Construct an empty CORE account type list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(DataSet pData) { 
			super(ListStyle.CORE, false);
			theData = pData;
		}

		/** 
	 	 * Construct a generic account type list
	 	 * @param pList the source account type list 
	 	 * @param pStyle the style of the list 
	 	 */
		public List(List pList, ListStyle pStyle) { 
			super(pList, pStyle);
			theData = pList.theData;
		}

		/** 
	 	 * Construct a difference account type list
	 	 * @param pNew the new AccountType list 
	 	 * @param pOld the old AccountType list 
	 	 */
		protected List(List pNew, List pOld) { super(pNew, pOld); }
	
		/** 
	 	 * Clone an AccountType list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return new List(this, ListStyle.CORE); }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pItem item to be added
		 * @return the newly added item
		 */
		public AccountType addNewItem(DataItem pItem) {
			AccountType myType = new AccountType(this, (AccountType)pItem);
			myType.addToList();
			return myType;
		}
	
		/**
		 * Create a new empty element in the edit list (null-operation)
		 * @param isCredit - is the item a credit or debit
		 * @return the newly added item
		 */
		public AccountType addNewItem(boolean isCredit) { return null; }
			
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "AccountType"; }
		
		/**
		 * Search for a particular item by Name
		 * 
		 * @param sName The name of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		public AccountType searchFor(String sName) {
			ListIterator 	myIterator;
			AccountType		myCurr;
			int         	iDiff;
			
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
		 * Ensure encryption of items in the list after spreadsheet load
		 */
		protected void ensureEncryption() throws Exception {
			ListIterator 	myIterator;
			AccountType		myCurr;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the items */
			while ((myCurr = myIterator.next()) != null) {
				/* Ensure encryption of the item */
				myCurr.ensureEncryption();
			}
			
			/* Return to caller */
			return;
		}	

		/**
		 * Add an AccountType to the list
		 * @param pActType the Name of the account type
		 */ 
		public void addItem(String pActType) throws Exception {
			AccountType myActType;
				
			/* Create a new Account Type */
			myActType = new AccountType(this, pActType);
				
			/* Check that this AccountType has not been previously added */
			if (searchFor(pActType) != null) 
				throw new Exception(ExceptionClass.DATA,
	   					  			myActType,
			  			            "Duplicate Account Type");
				 
			/* Add the Account Type to the list */
			myActType.addToList();
		}	

		/**
		 * Add an AccountType to the list
		 * @param uId the Id of the account type
		 * @param pActType the encrypted Name of the account type
		 */ 
		public void addItem(int    uId,
				            byte[] pActType) throws Exception {
			AccountType myActType;
				
			/* Create a new Account Type */
			myActType = new AccountType(this, uId, pActType);
				
			/* Check that this AccountTypeId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
	                      			myActType,
			  			            "Duplicate AccountTypeId");
				 
			/* Check that this AccountType has not been previously added */
			if (searchFor(myActType.getName()) != null) 
				throw new Exception(ExceptionClass.DATA,
	   					  			myActType,
			  			            "Duplicate Account Type");
				 
			/* Add the Account Type to the list */
			myActType.addToList();
		}	
	}	
		
	/**
	 * Values for an account type 
	 */
	public class Values implements histObject {
		private StringPair	theName      = null;
		
		/* Access methods */
		public StringPair  	getName()      	{ return theName; }
		public byte[]  		getNameBytes() 	{ return theName.getBytes(); }
		
		/* Value setting */
		public void setName(StringPair pName) { theName = pName; }

		/* Constructor */
		public Values() { }
		public Values(Values pValues) {
			theName      = pValues.theName;
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (EncryptedPair.differs(theName,    pValues.theName))    return false;
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
			theName      = pValues.theName;
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case FIELD_NAME:
					bResult = (EncryptedPair.differs(theName, pValues.theName));
					break;
			}
			return bResult;
		}
	}
	
	/**
	 * Enumeration of Account Type Classes. 
	 */
	private enum AccountClass {
		/**
		 * Current Banking Account
		 */
		CURRENT,

		/**
		 * Instant Access Savings Account
		 */
		INSTANT,
		
		/**
		 * Savings Account Requiring Notice for Withdrawals
		 */
		NOTICE,

		/**
		 * Fixed Rate Savings Bond
		 */
		BOND,
		
		/**
		 * Instant Access Cash ISA Account
		 */
		CASHISA,

		/**
		 * Fixed Rate Cash ISA Bond
		 */
		ISABOND,

		/**
		 * Index Linked Bond
		 */
		TAXFREEBOND,

		/**
		 * Equity Bond
		 */
		EQUITYBOND,

		/**
		 * Shares
		 */
		SHARES,

		/**
		 * Unit Trust or OEIC
		 */
		UNITTRUST,

		/**
		 * Life Bond
		 */
		LIFEBOND,

		/**
		 * Unit Trust or OEIC in ISA wrapper
		 */
		UNITISA,

		/**
		 * Car
		 */
		CAR,

		/**
		 * House
		 */
		HOUSE,

		/**
		 * Debts
		 */
		DEBTS,

		/**
		 * CreditCard
		 */
		CREDITCARD,

		/**
		 * WriteOff
		 */
		WRITEOFF,

		/**
		 * External Account
		 */
		EXTERNAL,

		/**
		 * Employer Account
		 */
		EMPLOYER,

		/**
		 * Market
		 */
		MARKET,

		/**
		 * Inland Revenue
		 */
		TAXMAN,

		/**
		 * Cash
		 */
		CASH,

		/**
		 * Inheritance
		 */
		INHERITANCE,

		/**
		 * Endowment
		 */
		ENDOWMENT,

		/**
		 * Benefit
		 */
		BENEFIT,

		/**
		 * Deferred between tax years
		 */
		DEFERRED;
	}	
}
