package finance;

import finance.finLink.histObject;
import finance.finLink.linkObject;
import finance.finObject.ExceptionClass;
import finance.finObject.ObjectClass;

/**
 * Provides static object classes and descriptions for the finance package. 
 * <p> These values are not available to be edited and are never deleted.
 * Occasionally new values will be introduced as the package is enhanced.
 * 
 * Five static classes are provided together with List classes for each
 *   
 * @author 	Tony Washer
 * @version 1.0
 * 
 * @see finStatic.AccountType
 * @see finStatic.TransType
 * @see finStatic.TaxType
 * @see finStatic.TaxRegime
 * @see finStatic.Frequency
 */
public class finStatic {
	/**
	 * A link back to the {@link finData} data-set that contains this instance
	 */
	private finData           theData = null;
	
	/**
	 * A link back to the {@link finBuilder.IdManager} object that controls this instance
	 */
	private finBuilder.IdManager theMgr  = null;

	/**
	 * Access the containing data-set for this 
	 * @return the containing data-set
	 */
	protected finData         getData()      { return theData; }
 	
 	/** 
 	 * Construct a static set
 	 * @param pData the {@link finData} object that contains this set
 	 */
	public finStatic(finData pData) {
		theData    = pData;
		theMgr     = pData.getIdManager();
	}
	
	/**
	 * Represents a list of {@link AccountType} objects. 
	 */
	public class ActTypeList extends finLink.itemCtl {	
		/* Linking methods */
		public AccountType getFirst() { return (AccountType)super.getFirst(); }
		public AccountType getLast()  { return (AccountType)super.getLast(); }
		public AccountType searchFor(long uId) {
			return (AccountType)super.searchFor(uId); }
		
	 	/** 
	 	 * Construct an empty CORE account type list
	 	 */
		protected ActTypeList() { super(ListStyle.CORE, true); }

		/** 
	 	 * Construct a generic account type list
	 	 * @param pList the source account type list 
	 	 * @param pStyle the style of the list 
	 	 */
		protected ActTypeList(ActTypeList pList, ListStyle pStyle) { super(pList, pStyle); }

		/** 
	 	 * Construct a difference account type list
	 	 * @param pNew the new AccountType list 
	 	 * @param pOld the old AccountType list 
	 	 */
		protected ActTypeList(ActTypeList pNew, ActTypeList pOld) { super(pNew, pOld); }
	
		/** 
	 	 * Clone an AccountType list
	 	 * @return the cloned list
	 	 */
		protected ActTypeList cloneIt() { return new ActTypeList(this, ListStyle.CORE); }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pItem item to be added
		 * @return the newly added item
		 */
		public finLink.itemElement addNewItem(finLink.itemElement pItem) {
			AccountType myType = new AccountType(this, (AccountType)pItem);
			myType.addToList();
			return myType;
		}
	
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
		protected AccountType searchFor(String sName) {
			AccountType myCurr;
			int         iDiff;
			
			/* Loop through the items to find the entry */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				iDiff = sName.compareTo(myCurr.getName());
				if (iDiff == 0) break;
			}
			
			/* Return to caller */
			return myCurr;
		}	
	}	
		
	/**
	 * Represents an AccountType object. 
	 * @see finStatic.ActTypeList
	 */
	public class AccountType extends finLink.itemElement {
		/**
		 * The class of the Account Type
		 */
		private AccountClass    theClass = null;

		/**
		 * The name of the Account Type
		 */
		private String          theName  = null;

		/**
		 * The sort order of the Account Type
		 */
		private int             theOrder = -1;
		
		/**
		 * Return the name of the Account Type
		 * @return the name
		 */
		public String getName()              { return theName; }
		
		/**
		 * Return the sort order of the Account Type
		 * @return the order
		 */
		protected int    getOrder()             { return theOrder; }
							
		/* Linking methods */
		public AccountType getNext()   { return (AccountType)super.getNext(); }
		public AccountType getPrev()   { return (AccountType)super.getPrev(); }
		public AccountType getBase()   { return (AccountType)super.getBase(); }
		
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
		public String itemType() { return "AccountType"; }
		
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
		 * Construct a copy of an Account Type.
		 * 
		 * @param pList	The list to associate the Account Type with
		 * @param pAcType The Account Type to copy 
		 */
		protected AccountType(ActTypeList pList,
				              AccountType pAcType) { 
			super(pList, pAcType.getId());
			theName  = pAcType.getName();
			theClass = pAcType.theClass;
			setBase(pAcType);
			setState(pAcType.getState());
			theOrder = pAcType.getOrder();
		}
		
		/**
		 * Construct a standard account type on load
		 * 
		 * @param pList	The list to associate the Account Type with
		 * @param uId   ID of Account Type
		 * @param sName Name of Account Type
		 * 
		 * @throws {@link finObject.Exception} if type is not supported
		 */
		protected AccountType(ActTypeList pList,
				              long        uId, 
				              String      sName) throws finObject.Exception {
			super(pList, uId);
			theName = sName;
			theMgr.setNewActType(this);				
		
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
			else if (sName.equals("IndexedBond")) {
				theClass = AccountClass.INDEXEDBOND;
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
			else if (sName.equals("Asset")) {
				theClass = AccountClass.ASSETS;
				theOrder = 12;
			}
			else if (sName.equals("Endowment")) {
				theClass = AccountClass.ENDOWMENT;
				theOrder = 13;
			}
			else if (sName.equals("CreditCard")) {
				theClass = AccountClass.CREDITCARD;
				theOrder = 14;
			}
			else if (sName.equals("Debts")) {
				theClass = AccountClass.DEBTS;
				theOrder = 15;
			}
			else if (sName.equals("Deferred")) {
				theClass = AccountClass.DEFERRED;
				theOrder = 16;
			}
			else if (sName.equals("Cash")) {
				theClass = AccountClass.CASH;
				theOrder = 17;
			}
			else if (sName.equals("TaxMan")) {
				theClass = AccountClass.TAXMAN;
				theOrder = 18;
			}
			else if (sName.equals("Inheritance")) {
				theClass = AccountClass.INHERITANCE;
				theOrder = 19;
			}
			else if (sName.equals("WriteOff")) {
				theClass = AccountClass.WRITEOFF;
				theOrder = 20;
			}
			else if (sName.equals("Benefit")) {
				theClass = AccountClass.BENEFIT;
				theOrder = 21;
			}
			else if (sName.equals("External")) {
				theClass = AccountClass.EXTERNAL;
				theOrder = 22;
			}
			else if (sName.equals("Market")) {
				theClass = AccountClass.MARKET;
				theOrder = 23;
			}
			else {
				throw new finObject.Exception(ExceptionClass.DATA,
	  					  					  ObjectClass.ACCOUNTTYPE,
	  					  					  this,
	                                          "Invalid AccountType");
			}
		}
		
		/**
		 * Compare this account type to another to establish equality.
		 * 
		 * @param that The Account type to compare to
		 * @return <code>true</code> if the account type is identical, <code>false</code> otherwise
		 */
		public boolean equals(finLink.itemElement that) {
			AccountType myType = (AccountType)that;
			if (this == that) return true;
			if (getId() != myType.getId()) return false;
			return (theName.compareTo(myType.theName) == 0);
		}

		/**
		 * Compare this account type to another to establish sort order.
		 * 
		 * @param that The Account type to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		protected int compareTo(AccountType that) {
			long result;
			if (this == that) return 0;
			if (theOrder < that.theOrder) return -1;
			if (theOrder > that.theOrder) return  1;
			result = theName.compareTo(that.theName);
			if (result < 0) return -1;
			if (result > 0) return 1;
			result = (int)(getId() - that.getId());
			if (result == 0) return 0;
			else if (result < 0) return -1;
			else return 1;
		}

		/**
		 * Compare this account type to another to establish sort order.
		 * 
		 * @param that The Account type to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		public int linkCompareTo(linkObject that) {
			AccountType myItem = (AccountType)that;
			return this.compareTo(myItem);
		}
			
		/**
		 * Determine whether the AccountType is external
		 * 
		 * @return <code>true</code> if the account is external, <code>false</code> otherwise.
		 */
		protected boolean isExternal() {
			switch (theClass) {
				case EXTERNAL:
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
		protected boolean isSpecial() {
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
		protected boolean isPriced() {
			switch (theClass) {
				case ASSETS:
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
		 * Determine whether the AccountType is savings
		 * 
		 * @return <code>true</code> if the account is savings, <code>false</code> otherwise.
		 */
		protected boolean isMoney() {
			switch (theClass) {
				case CURRENT:
				case INSTANT:
				case NOTICE:
				case BOND:
				case CASHISA:
				case ISABOND:
				case INDEXEDBOND:
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
		protected boolean isBond() {
			switch (theClass) {
				case BOND:
				case ISABOND:
				case INDEXEDBOND:
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
		protected boolean isChild() {
			switch (theClass) {
				case CURRENT:
				case INSTANT:
				case NOTICE:
				case CASHISA:
				case BOND:
				case ISABOND:
				case INDEXEDBOND:
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
		protected boolean isReserved() {
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
		 * Determine whether the AccountType is a LifeBond
		 * 
		 * @return <code>true</code> if the account is LifeBond, <code>false</code> otherwise.
		 */
		protected boolean isLifeBond()   { return (theClass == AccountClass.LIFEBOND); }

		/**
		 * Determine whether the AccountType is internal
		 * 
		 * @return <code>true</code> if the account is internal, <code>false</code> otherwise.
		 */
		protected boolean isInternal()  { return !isExternal(); }
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
		INDEXEDBOND,

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
		 * Assets
		 */
		ASSETS,

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
	
	/**
	 * Represents a list of {@link TransType} objects. 
	 */
	public class TransTypeList extends finLink.itemCtl {
		/* Linking methods */
		public TransType getFirst() { return (TransType)super.getFirst(); }
		public TransType getLast()  { return (TransType)super.getLast(); }
		public TransType searchFor(long uId) {
			return (TransType)super.searchFor(uId); }
		
	 	/** 
	 	 * Construct an empty CORE transaction type list
	 	 */
		protected TransTypeList() { super(ListStyle.CORE, true); }

		/** 
	 	 * Construct a generic transtype list
	 	 * @param pList the source transtype list 
	 	 * @param pStyle the style of the list 
	 	 */
		protected TransTypeList(TransTypeList pList, ListStyle pStyle) { super(pList, pStyle); }

		/** 
	 	 * Construct a difference transtype list
	 	 * @param pNew the new TransType list 
	 	 * @param pOld the old TransType list 
	 	 */
		protected TransTypeList(TransTypeList pNew, TransTypeList pOld) { super(pNew, pOld); }
	
		/** 
	 	 * Clone a TransType list
	 	 * @return the cloned list
	 	 */
		protected TransTypeList cloneIt() { return new TransTypeList(this, ListStyle.CORE); }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pItem item to be added
		 * @return the newly added item
		 */
		public finLink.itemElement addNewItem(finLink.itemElement pItem) {
			TransType myType = new TransType(this, (TransType)pItem);
			myType.addToList();
			return myType;
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "TransactionType"; }
		
		/**
		 * Search for a particular item by class
		 *  
		 * @param eClass The class of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		protected TransType searchFor(TransClass eClass) {
			TransType myCurr;
			
			/* Loop through the items to find the match */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
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
		protected TransType searchFor(String sName) {
			TransType myCurr;
			int       iDiff;
			
			/* Loop through the items to find the entry */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				iDiff = sName.compareTo(myCurr.getName());
				if (iDiff == 0) break;
			}
			
			/* Return to caller */
			return myCurr;
		}	
	}
	
	/**
	 * Represents a TransactionType object. 
	 * @see finStatic.TransTypeList
	 */
	public class TransType extends finLink.itemElement {
		/**
		 * The class of the Transaction Type
		 */
		private TransClass   theClass = null;

		/**
		 * The name of the Transaction Type
		 */
		private String       theName  = null;

		/**
		 * The sort order of the Transaction Type
		 */
		private int          theOrder = -1;
		
		/**
		 * Return the name of the Transaction Type
		 * @return the name
		 */
		protected String     getName()              { return theName; }

		/**
		 * Return the sort order of the Transaction Type
		 * @return the order
		 */
		protected int        getOrder()             { return theOrder; }

		/**
		 * Return the Transaction class of the Transaction Type
		 * @return the class
		 */
		protected TransClass getTranClass()         { return theClass; }
	
		/* Linking methods */
		public TransType getNext() { return (TransType)super.getNext(); }
		public TransType getPrev() { return (TransType)super.getPrev(); }
		public TransType getBase() { return (TransType)super.getBase(); }
	
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
		public String itemType() { return "Transaction"; }
		
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
		protected TransType(TransTypeList pList,
	                        TransType     pTransType) { 
			super(pList, pTransType.getId());
			theName  = pTransType.getName();
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
		 * 
		 * @throws {@link finObject.Exception} if type is not supported
		 */
		protected TransType(TransTypeList pList,
	                        long          uId,
	                        String        sName) throws finObject.Exception {
			super(pList, uId);
			theName = sName;
			theMgr.setNewTransType(this);				
		
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
			else if (sName.equals("UnitTrustDiv")) {
				theClass = TransClass.UNITTRUSTDIV;
				theOrder = 3;
			}
			else if (sName.equals("TaxableGain")) {
				theClass = TransClass.TAXABLEGAIN;
				theOrder = 4;
			}
			else if (sName.equals("CapitalGain")) {
				theClass = TransClass.CAPITALGAIN;
				theOrder = 5;
			}
			else if (sName.equals("Benefit")) {
				theClass = TransClass.BENEFIT;
				theOrder = 6;
			}
			else if (sName.equals("TaxFreeIncome")) {
				theClass = TransClass.TAXFREEINCOME;
				theOrder = 7;
			}
			else if (sName.equals("Inherited")) {
				theClass = TransClass.INHERITED;
				theOrder = 8;
			}
			else if (sName.equals("DebtInterest")) {
				theClass = TransClass.DEBTINTEREST;
				theOrder = 9;
			}
			else if (sName.equals("RentalIncome")) {
				theClass = TransClass.RENTALINCOME;
				theOrder = 10;
			}
			else if (sName.equals("DirectorsLoan")) {
				theClass = TransClass.DIRLOAN;
				theOrder = 11;
			}
			else if (sName.equals("MarketGrowth")) {
				theClass = TransClass.MKTGROWTH;
				theOrder = 12;
			}
			else if (sName.equals("MarketShrink")) {
				theClass = TransClass.MKTSHRINK;
				theOrder = 13;
			}
			else if (sName.equals("MarketIncome")) {
				theClass = TransClass.MKTINCOME;
				theOrder = 14;
			}
			else if (sName.equals("Expense")) {
				theClass = TransClass.EXPENSE;
				theOrder = 15;
			}
			else if (sName.equals("Recovered")) {
				theClass = TransClass.RECOVERED;
				theOrder = 16;
			}
			else if (sName.equals("Endowment")) {
				theClass = TransClass.ENDOWMENT;
				theOrder = 17;
			}
			else if (sName.equals("Mortgage")) {
				theClass = TransClass.MORTGAGE;
				theOrder = 18;
			}
			else if (sName.equals("Insurance")) {
				theClass = TransClass.INSURANCE;
				theOrder = 19;
			}
			else if (sName.equals("ExtraTax")) {
				theClass = TransClass.EXTRATAX;
				theOrder = 20;
			}
			else if (sName.equals("WriteOff")) {
				theClass = TransClass.WRITEOFF;
				theOrder = 21;
			}
			else if (sName.equals("NatInsurance")) {
				theClass = TransClass.NATINSURANCE;
				theOrder = 22;
			}
			else if (sName.equals("TaxRelief")) {
				theClass = TransClass.TAXRELIEF;
				theOrder = 23;
			}
			else if (sName.equals("TaxOwed")) {
				theClass = TransClass.TAXOWED;
				theOrder = 24;
			}
			else if (sName.equals("TaxRefund")) {
				theClass = TransClass.TAXREFUND;
				theOrder = 25;
			}
			else if (sName.equals("Transfer")) {
				theClass = TransClass.TRANSFER;
				theOrder = 26;
			}
			else if (sName.equals("CashRecovery")) {
				theClass = TransClass.CSHRECOVER;
				theOrder = 27;
			}
			else if (sName.equals("CashPayment")) {
				theClass = TransClass.CSHPAY;
				theOrder = 28;
			}
			else {
				throw new finObject.Exception(ExceptionClass.DATA,
	  					  					  ObjectClass.TRANSTYPE,
	  					  					  this,
                                              "Invalid TransType");
			}
		}
	
		/**
		 * Compare this transaction type to another to establish equality.
		 * 
		 * @param that The Transaction type to compare to
		 * @return <code>true</code> if the transaction type is identical, <code>false</code> otherwise
		 */
		public boolean equals(finLink.itemElement that) {
			TransType myType = (TransType)that;
			if (this == that) return true;
			if (getId() != myType.getId()) return false;
			return (theName.compareTo(myType.theName) == 0);
		}

		/**
		 * Compare this transaction type to another to establish sort order.
		 * 
		 * @param that The Transaction type to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		protected int compareTo(TransType that) {
			long result;
			if (this == that) return 0;
			if (theOrder < that.theOrder) return -1;
			if (theOrder > that.theOrder) return  1;
			result = theName.compareTo(that.theName);
			if (result < 0) return -1;
			if (result > 0) return 1;
			result = (int)(getId() - that.getId());
			if (result == 0) return 0;
			else if (result < 0) return -1;
			else return 1;
		}

		/**
		 * Compare this transaction type to another to establish sort order.
		 * 
		 * @param that The Transaction type to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		public int linkCompareTo(linkObject that) {
			TransType myItem = (TransType)that;
			return this.compareTo(myItem);
		}
			
		/**
		 * Determine whether the TransactionType is a transfer
		 * 
		 * @return <code>true</code> if the transaction is transfer, <code>false</code> otherwise.
		 */
		protected boolean isTransfer()  { 
			return (theClass == TransClass.TRANSFER); }
		
		/**
		 * Determine whether the TransactionType is a cash payment
		 * 
		 * @return <code>true</code> if the transaction is cash payment, <code>false</code> otherwise.
		 */
		protected boolean isCashPayment()  { 
			return (theClass == TransClass.CSHPAY); }
		
		/**
		 * Determine whether the TransactionType is a cash recovery
		 * 
		 * @return <code>true</code> if the transaction is cash recovery, <code>false</code> otherwise.
		 */
		protected boolean isCashRecovery()  { 
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
		protected boolean isTaxableGain()  { 
			return (theClass == TransClass.TAXABLEGAIN); }
	
		/**
		 * Determine whether the TransactionType is a capital gain
		 * 
		 * @return <code>true</code> if the transaction is capital gain, <code>false</code> otherwise.
		 */
		protected boolean isCapitalGain()  { 
			return (theClass == TransClass.CAPITALGAIN); }
	
		/**
		 * Determine whether the TransactionType is a market adjustment
		 * 
		 * @return <code>true</code> if the transaction is market adjustment, <code>false</code> otherwise.
		 */
		protected boolean isMarketAdjust() { 
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
		protected boolean isRecovered()  { 
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
		protected boolean needsTaxCredit() { 
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
				case DIRLOAN:
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
	}

	/**
	 * Enumeration of Transaction Type Classes. 
	 */
	protected enum TransClass {
		/**
		 * Taxed Salary Income
		 */
		TAXEDINCOME,

		/**
		 * Tax Free Income
		 */
		TAXFREEINCOME,

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
		 * Directors Loan
		 */
		DIRLOAN,     

		/**
		 * Write Off
		 */
		WRITEOFF,

		/**
		 * Rental Income
		 */
		RENTALINCOME;
	}
	
	/**
	 * Represents a list of {@link TaxType} objects. 
	 */
	public class TaxTypeList extends finLink.itemCtl {
		/* Linking methods */
		public TaxType getFirst() { return (TaxType)super.getFirst(); }
		public TaxType getLast()  { return (TaxType)super.getLast(); }
		public TaxType searchFor(long uId) {
			return (TaxType)super.searchFor(uId); }
			
	 	/** 
	 	 * Construct an empty CORE tax type list
	 	 */
		protected TaxTypeList() { super(ListStyle.CORE, true); }

		/** 
	 	 * Construct a generic tax type list
	 	 * @param pList the source tax type list 
	 	 * @param pStyle the style of the list 
	 	 */
		protected TaxTypeList(TaxTypeList pList, ListStyle pStyle) { super(pList, pStyle); }

		/** 
	 	 * Construct a difference tax type list
	 	 * @param pNew the new TaxType list 
	 	 * @param pOld the old TaxType list 
	 	 */
		protected TaxTypeList(TaxTypeList pNew, TaxTypeList pOld) { super(pNew, pOld); }
		
		/** 
	 	 * Clone a TaxType list
	 	 * @return the cloned list
	 	 */
		protected TaxTypeList cloneIt() { return new TaxTypeList(this, ListStyle.CORE); }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pItem item to be added
		 * @return the newly added item
		 */
		public finLink.itemElement addNewItem(finLink.itemElement pItem) {
			TaxType myType = new TaxType(this, (TaxType)pItem);
			myType.addToList();
			return myType;
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "TaxClass"; }
		
		/**
		 * Search for a particular item by class
		 *  
		 * @param eClass The class of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		protected TaxType searchFor(TaxClass eClass) {
			TaxType myCurr;
			
			/* Loop through the items to find the match */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				if (myCurr.theClass == eClass) break;
			}
			
			/* Return */
			return myCurr;
		}
		
		/**
		 * Search for a particular item by Name
		 * 
		 * @param sName The name of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		protected TaxType searchFor(String sName) {
			TaxType myCurr;
			int     iDiff;
			
			/* Loop through the items to find the entry */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				iDiff = sName.compareTo(myCurr.getName());
				if (iDiff == 0) break;
			}
			
			/* Return to caller */
			return myCurr;
		}	
	}
		
	/**
	 * Represents a TaxType object. 
	 * @see finStatic.TaxTypeList
	 */
	public class TaxType extends finLink.itemElement {
		/**
		 * The class of the TaxType
		 */
		private TaxClass   theClass = null;

		/**
		 * The name of the Tax Type
		 */
		private String     theName  = null;

		/**
		 * The sort order of the Tax Type
		 */
		private int        theOrder = -1;
		
		/**
		 * Constant to provide separation of classes in sort order
		 */		
		public final static short CLASSDIVIDE = 100;
		
		/**
		 * Return the name of the Tax Type
		 * @return the name
		 */
		protected String getName()              { return theName; }

		/**
		 * Return the sort order of the Account Type
		 * @return the order
		 */
		protected int    getOrder()             { return theOrder; }
			
		/* Linking methods */
		public TaxType getNext() { return (TaxType)super.getNext(); }
		public TaxType getPrev() { return (TaxType)super.getPrev(); }
		
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
		public String itemType() { return "TaxClass"; }
		
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
		 * Construct a copy of a Tax Type.
		 * 
		 * @param pList	The list to associate the Tax Type with
		 * @param pTaxType The Tax Type to copy 
		 */
		protected TaxType(TaxTypeList pList, TaxType pTaxType) { 
			super(pList, pTaxType.getId());
			theName = pTaxType.getName();
			theClass = pTaxType.theClass;
			setBase(pTaxType);
			setState(pTaxType.getState());
			theOrder = pTaxType.getOrder();
		}
		
		/**
		 * Construct a standard Tax type on load
		 * 
		 * @param pList	The list to associate the Tax Type with
		 * @param uId   ID of Tax Type
		 * @param sName Name of Tax Type
		 * 
		 * @throws {@link finObject.Exception} if type is not supported
		 */
		public TaxType(TaxTypeList pList,
				       long        uId, 
				       String      sName) throws finObject.Exception {
			super(pList, uId);
			theName = sName;
			theMgr.setNewTaxType(this);				
		
			if (sName.equals("GrossSalary")) {
				theClass = TaxClass.GROSSSALARY;
				theOrder = 0;
			}
			else if (sName.equals("GrossInterest")) {
				theClass = TaxClass.GROSSINTEREST;
				theOrder = 1;
			}
			else if (sName.equals("GrossDividends")) {
				theClass = TaxClass.GROSSDIVIDEND;
				theOrder = 2;
			}
			else if (sName.equals("GrossUnitTrustDividends")) {
				theClass = TaxClass.GROSSUTDIVS;
				theOrder = 3;
			}
			else if (sName.equals("GrossRental")) {
				theClass = TaxClass.GROSSRENTAL;
				theOrder = 4;
			}
			else if (sName.equals("GrossTaxableGains")) {
				theClass = TaxClass.GROSSTAXGAINS;
				theOrder = 5;
			}
			else if (sName.equals("GrossCapitalGains")) {
				theClass = TaxClass.GROSSCAPGAINS;
				theOrder = 6;
			}
			else if (sName.equals("TaxationPaid")) {
				theClass = TaxClass.TAXPAID;
				theOrder = 7;
			}
			else if (sName.equals("TaxFree")) {
				theClass = TaxClass.TAXFREE;
				theOrder = 8;
			}
			else if (sName.equals("Market")) {
				theClass = TaxClass.MARKET;
				theOrder = 9;
			}
			else if (sName.equals("Expense")) {
				theClass = TaxClass.EXPENSE;
				theOrder = 10;
			}
			else if (sName.equals("Profit/Loss")) {
				theClass = TaxClass.PROFIT;
				theOrder = 11;
			}
			else if (sName.equals("CoreProfit/Loss")) {
				theClass = TaxClass.COREPROFIT;
				theOrder = 12;
			}
			else if (sName.equals("GrossIncome")) {
				theClass = TaxClass.GROSSINCOME;
				theOrder = 0+CLASSDIVIDE;
			}
			else if (sName.equals("OriginalAllowance")) {
				theClass = TaxClass.ORIGALLOW;
				theOrder = 1+CLASSDIVIDE;
			}
			else if (sName.equals("AdjustedAllowance")) {
				theClass = TaxClass.ADJALLOW;
				theOrder = 2+CLASSDIVIDE;
			}
			else if (sName.equals("HighTaxBand")) {
				theClass = TaxClass.HITAXBAND;
				theOrder = 3+CLASSDIVIDE;
			}
			else if (sName.equals("SalaryNilRate")) {
				theClass = TaxClass.SALARYFREE;
				theOrder = 4+CLASSDIVIDE;
			}
			else if (sName.equals("RentalNilRate")) {
				theClass = TaxClass.RENTALFREE;
				theOrder = 5+CLASSDIVIDE;
			}
			else if (sName.equals("InterestNilRate")) {
				theClass = TaxClass.INTERESTFREE;
				theOrder = 6+CLASSDIVIDE;
			}
			else if (sName.equals("CapitalNilRate")) {
				theClass = TaxClass.CAPITALFREE;
				theOrder = 7+CLASSDIVIDE;
			}
			else if (sName.equals("SalaryLowRate")) {
				theClass = TaxClass.SALARYLO;
				theOrder = 8+CLASSDIVIDE;
			}
			else if (sName.equals("RentalLowRate")) {
				theClass = TaxClass.RENTALLO;
				theOrder = 9+CLASSDIVIDE;
			}
			else if (sName.equals("InterestLowRate")) {
				theClass = TaxClass.INTERESTLO;
				theOrder = 10+CLASSDIVIDE;
			}
			else if (sName.equals("SalaryBasicRate")) {
				theClass = TaxClass.SALARYBASIC;
				theOrder = 11+CLASSDIVIDE;
			}
			else if (sName.equals("RentalBasicRate")) {
				theClass = TaxClass.RENTALBASIC;
				theOrder = 12+CLASSDIVIDE;
			}
			else if (sName.equals("InterestBasicRate")) {
				theClass = TaxClass.INTERESTBASIC;
				theOrder = 13+CLASSDIVIDE;
			}
			else if (sName.equals("DividendBasicRate")) {
				theClass = TaxClass.DIVIDENDBASIC;
				theOrder = 14+CLASSDIVIDE;
			}
			else if (sName.equals("SliceBasicRate")) {
				theClass = TaxClass.SLICEBASIC;
				theOrder = 15+CLASSDIVIDE;
			}
			else if (sName.equals("GainsBasicRate")) {
				theClass = TaxClass.GAINSBASIC;
				theOrder = 16+CLASSDIVIDE;
			}
			else if (sName.equals("CapitalBasicRate")) {
				theClass = TaxClass.CAPITALBASIC;
				theOrder = 17+CLASSDIVIDE;
			}
			else if (sName.equals("SalaryHighRate")) {
				theClass = TaxClass.SALARYHI;
				theOrder = 18+CLASSDIVIDE;
			}
			else if (sName.equals("RentalHighRate")) {
				theClass = TaxClass.RENTALHI;
				theOrder = 19+CLASSDIVIDE;
			}
			else if (sName.equals("InterestHighRate")) {
				theClass = TaxClass.INTERESTHI;
				theOrder = 20+CLASSDIVIDE;
			}
			else if (sName.equals("DividendHighRate")) {
				theClass = TaxClass.DIVIDENDHI;
				theOrder = 21+CLASSDIVIDE;
			}
			else if (sName.equals("SliceHighRate")) {
				theClass = TaxClass.SLICEHI;
				theOrder = 22+CLASSDIVIDE;
			}
			else if (sName.equals("GainsHighRate")) {
				theClass = TaxClass.SLICEHI;
				theOrder = 23+CLASSDIVIDE;
			}
			else if (sName.equals("CapitalHighRate")) {
				theClass = TaxClass.CAPITALHI;
				theOrder = 24+CLASSDIVIDE;
			}
			else if (sName.equals("SalaryAdditionalRate")) {
				theClass = TaxClass.SALARYADD;
				theOrder = 25+CLASSDIVIDE;
			}
			else if (sName.equals("RentalAdditionalRate")) {
				theClass = TaxClass.RENTALADD;
				theOrder = 26+CLASSDIVIDE;
			}
			else if (sName.equals("InterestAdditionalRate")) {
				theClass = TaxClass.INTERESTADD;
				theOrder = 27+CLASSDIVIDE;
			}
			else if (sName.equals("DividendAdditionalRate")) {
				theClass = TaxClass.DIVIDENDADD;
				theOrder = 28+CLASSDIVIDE;
			}
			else if (sName.equals("SliceAdditionalRate")) {
				theClass = TaxClass.SLICEADD;
				theOrder = 29+CLASSDIVIDE;
			}
			else if (sName.equals("GainsAdditionalRate")) {
				theClass = TaxClass.GAINSADD;
				theOrder = 20+CLASSDIVIDE;
			}
			else if (sName.equals("TaxDueSlice")) {
				theClass = TaxClass.TAXDUESLICE;
				theOrder = 21+CLASSDIVIDE;
			}
			else if (sName.equals("TaxDueSalary")) {
				theClass = TaxClass.TAXDUESALARY;
				theOrder = 0+2*CLASSDIVIDE;
			}
			else if (sName.equals("TaxDueRental")) {
				theClass = TaxClass.TAXDUERENTAL;
				theOrder = 1+2*CLASSDIVIDE;
			}
			else if (sName.equals("TaxDueInterest")) {
				theClass = TaxClass.TAXDUEINTEREST;
				theOrder = 2+2*CLASSDIVIDE;
			}
			else if (sName.equals("TaxDueDividends")) {
				theClass = TaxClass.TAXDUEDIVIDEND;
				theOrder = 3+2*CLASSDIVIDE;
			}
			else if (sName.equals("TaxDueTaxableGains")) {
				theClass = TaxClass.TAXDUETAXGAINS;
				theOrder = 4+2*CLASSDIVIDE;
			}
			else if (sName.equals("TaxDueCapitalGains")) {
				theClass = TaxClass.TAXDUECAPGAINS;
				theOrder = 5+2*CLASSDIVIDE;
			}
			else if (sName.equals("TotalTaxation")) {
				theClass = TaxClass.TOTALTAX;
				theOrder = 0+3*CLASSDIVIDE;
			}
			else if (sName.equals("TaxProfit/Loss")) {
				theClass = TaxClass.TAXPROFIT;
				theOrder = 1+3*CLASSDIVIDE;
			}
			else {
				throw new finObject.Exception(ExceptionClass.DATA,
	  					  					  ObjectClass.TAXTYPE,
	  					  					  this,
	                                          "Invalid TaxType");
			}
		}
		
		/**
		 * Compare this tax type to another to establish equality.
		 * 
		 * @param that The Tax type to compare to
		 * @return <code>true</code> if the tax type is identical, <code>false</code> otherwise
		 */
		public boolean equals(finLink.itemElement that) {
			TaxType myType = (TaxType)that;
			if (this == that) return true;
			if (getId() != myType.getId()) return false;
			return (theName.compareTo(myType.theName) == 0);
		}

		/**
		 * Compare this tax type to another to establish sort order.
		 * 
		 * @param that The Tax type to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		protected int compareTo(TaxType that) {
			long result;
			if (this == that) return 0;
			if (theOrder < that.theOrder) return -1;
			if (theOrder > that.theOrder) return  1;
			result = theName.compareTo(that.theName);
			if (result < 0) return -1;
			if (result > 0) return 1;
			result = (int)(getId() - that.getId());
			if (result == 0) return 0;
			else if (result < 0) return -1;
			else return 1;
		}

		/**
		 * Compare this tax type to another to establish sort order.
		 * 
		 * @param that The Tax type to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		public int linkCompareTo(linkObject that) {
			TaxType myItem = (TaxType)that;
			return this.compareTo(myItem);
		}
		
		/**
		 * Determine whether we should add tax credits to the total
		 * 
		 * @return <code>true</code> if we should add tax credits to the total, <code>false</code> otherwise.
		 */
		protected boolean hasTaxCredits() { 
			switch (theClass) {
				case GROSSSALARY:
				case GROSSINTEREST:
				case GROSSDIVIDEND:
				case GROSSUTDIVS:
				case GROSSTAXGAINS:
					return true;
				default:
					return false;
			}
		}
		
		/**
		 * Determine whether we this is the tax paid bucket
		 * 
		 * @return <code>true</code> if we should add tax credits to the total, <code>false</code> otherwise.
		 */
		protected boolean isTaxPaid() { 
			switch (theClass) {
				case TAXPAID:
					return true;
				default:
					return false;
			}
		}
	}

	/**
	 * Enumeration of Tax Type Classes. 
	 */
	protected enum TaxClass {
		/**
		 * Gross Salary Income
		 */
		GROSSSALARY,

		/**
		 * Gross Interest Income
		 */
		GROSSINTEREST,

		/**
		 * Gross Dividend Income
		 */
		GROSSDIVIDEND,

		/**
		 * Gross Unit Trust Dividend Income
		 */
		GROSSUTDIVS,

		/**
		 * Gross Rental Income
		 */
		GROSSRENTAL,

		/**
		 * Gross Taxable gains
		 */
		GROSSTAXGAINS,

		/**
		 * Gross Capital gains
		 */
		GROSSCAPGAINS,

		/**
		 * Total Tax Paid
		 */
		TAXPAID,

		/**
		 * Market Growth/Shrinkage
		 */
		MARKET,     

		/**
		 * Tax Free Income
		 */
		TAXFREE,

		/**
		 * Gross Expense
		 */
		EXPENSE,

		/**
		 * Profit on Year
		 */
		PROFIT,
		
		/**
		 * Profit on year after ignoring market movements and inheritance
		 */
		COREPROFIT,
		
		/**
		 * Gross Income
		 */
		GROSSINCOME,

		/**
		 * Original Allowance
		 */
		ORIGALLOW,

		/**
		 * Adjusted Allowance
		 */
		ADJALLOW,

		/**
		 * High Tax Band
		 */
		HITAXBAND,

		/**
		 * Salary at nil-rate
		 */
		SALARYFREE,

		/**
		 * Salary at low-rate
		 */
		SALARYLO,

		/**
		 * Salary at basic-rate
		 */
		SALARYBASIC,

		/**
		 * Salary at high-rate
		 */
		SALARYHI,

		/**
		 * Salary at additional-rate
		 */
		SALARYADD,

		/**
		 * Rental at nil-rate
		 */
		RENTALFREE,

		/**
		 * Rental at low-rate
		 */
		RENTALLO,

		/**
		 * Rental at basic-rate
		 */
		RENTALBASIC,

		/**
		 * Rental at high-rate
		 */
		RENTALHI,

		/**
		 * Rental at additional-rate
		 */
		RENTALADD,

		/**
		 * Interest at nil-rate
		 */
		INTERESTFREE,

		/**
		 * Interest at low-rate
		 */
		INTERESTLO,

		/**
		 * Interest at basic-rate
		 */
		INTERESTBASIC,

		/**
		 * Interest at high-rate
		 */
		INTERESTHI,

		/**
		 * Interest at additional-rate
		 */
		INTERESTADD,

		/**
		 * Dividends at basic-rate
		 */
		DIVIDENDBASIC,

		/**
		 * Dividends at high-rate
		 */
		DIVIDENDHI,

		/**
		 * Dividends at additional-rate
		 */
		DIVIDENDADD,

		/**
		 * Slice at basic-rate
		 */
		SLICEBASIC,

		/**
		 * Slice at high-rate
		 */
		SLICEHI,

		/**
		 * Slice at additional-rate
		 */
		SLICEADD,

		/**
		 * Gains at basic-rate
		 */
		GAINSBASIC,

		/**
		 * Gains at high-rate
		 */
		GAINSHI,

		/**
		 * Gains at additional-rate
		 */
		GAINSADD,

		/**
		 * Capital at nil-rate
		 */
		CAPITALFREE,

		/**
		 * Capital at basic-rate
		 */
		CAPITALBASIC,

		/**
		 * Capital at high-rate
		 */
		CAPITALHI,

		/**
		 * Total Taxation Due on Salary
		 */
		TAXDUESALARY,

		/**
		 * Total Taxation Due on Rental
		 */
		TAXDUERENTAL,

		/**
		 * Total Taxation Due on Interest
		 */
		TAXDUEINTEREST,

		/**
		 * Total Taxation Due on Dividends
		 */
		TAXDUEDIVIDEND,

		/**
		 * Total Taxation Due on Taxable Gains
		 */
		TAXDUETAXGAINS,

		/**
		 * Total Taxation Due on Slice
		 */
		TAXDUESLICE,

		/**
		 * Total Taxation Due on Capital Gains
		 */
		TAXDUECAPGAINS,

		/**
		 * Total Taxation Due
		 */
		TOTALTAX,

		/**
		 * Taxation Profit (TaxDue-TaxPaid)
		 */
		TAXPROFIT;
	}
	
	/**
	 * Represents a list of {@link TaxRegime} objects. 
	 */
	public class TaxRegimeList  extends finLink.itemCtl {
		/* Linking methods */
		public TaxRegime getFirst() { return (TaxRegime)super.getFirst(); }
		public TaxRegime getLast()  { return (TaxRegime)super.getLast(); }
		public TaxRegime searchFor(long uId) {
			return (TaxRegime)super.searchFor(uId); }
			
	 	/** 
	 	 * Construct an empty CORE tax regime list
	 	 */
		protected TaxRegimeList() { super(ListStyle.CORE, true); }

		/** 
	 	 * Construct a generic tax regime list
	 	 * @param pList the source Tax Regime list 
	 	 * @param pStyle the style of the list 
	 	 */
		protected TaxRegimeList(TaxRegimeList pList, ListStyle pStyle) { super(pList, pStyle); }

		/** 
	 	 * Construct a difference tax regime list
	 	 * @param pNew the new TaxRegime list 
	 	 * @param pOld the old TaxRegime list 
	 	 */
		protected TaxRegimeList(TaxRegimeList pNew, TaxRegimeList pOld) { super(pNew, pOld); }
		
		/** 
	 	 * Clone a TaxRegime list
	 	 * @return the cloned list
	 	 */
		protected TaxRegimeList cloneIt() { return new TaxRegimeList(this, ListStyle.CORE); }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pItem item to be added
		 * @return the newly added item
		 */
		public finLink.itemElement addNewItem(finLink.itemElement pItem) {
			TaxRegime myRegime = new TaxRegime(this, (TaxRegime)pItem);
			myRegime.addToList();
			return myRegime;
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "TaxRegime"; }
				
		/**
		 * Search for a particular item by Name
		 * 
		 * @param sName The name of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		protected TaxRegime searchFor(String sName) {
			TaxRegime   myCurr;
			int         iDiff;
			
			/* Loop through the items to find the entry */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				iDiff = sName.compareTo(myCurr.getName());
				if (iDiff == 0) break;
			}
			
			/* Return to caller */
			return myCurr;
		}	
	}
	
	/**
	 * Represents a TaxRegime object. 
	 * @see finStatic.TaxRegimeList
	 */
	public class TaxRegime extends finLink.itemElement {
		/**
		 * The class of the TaxRegime
		 */
		private TaxRegClass  theClass = null;

		/**
		 * The name of the TaxRegime
		 */
		private String     theName  = null;

		/**
		 * The sort order of the TaxRegime
		 */
		private int        theOrder = -1;
		
		/**
		 * Return the name of the TaxRegime
		 * @return the name
		 */
		protected String    getName()              { return theName; }

		/**
		 * Return the sort order of the Frequency
		 * @return the order
		 */
		protected int       getOrder()             { return theOrder; }

		/**
		 * Return the TaxRegime class of the TaxRegime
		 * @return the class
		 */
		protected TaxRegClass getRegime()         { return theClass; }
		
		/* Linking methods */
		public TaxRegime getNext() { return (TaxRegime)super.getNext(); }
		public TaxRegime getPrev() { return (TaxRegime)super.getPrev(); }
		
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
		public String itemType() { return "TaxRegime"; }
		
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
		 * Construct a copy of a TaxRegime.
		 * 
		 * @param pList	The list to associate the TaxRegime with
		 * @param pTaxRegime The TaxRegime to copy 
		 */
		protected TaxRegime(TaxRegimeList  pList,
				            TaxRegime      pTaxRegime) { 
			super(pList, pTaxRegime.getId());
			theName  = pTaxRegime.getName();
			theClass = pTaxRegime.theClass;
			setBase(pTaxRegime);
			setState(pTaxRegime.getState());
			theOrder = pTaxRegime.getOrder();
		}
		
		/**
		 * Construct a standard TaxRegime on load
		 * 
		 * @param pList	The list to associate the TaxRegime with
		 * @param uId   ID of TaxRegime
		 * @param sName Name of TaxRegime
		 * 
		 * @throws {@link finObject.Exception} if type is not supported
		 */
		public TaxRegime(TaxRegimeList pList,
				         long          uId,
				         String        sName) throws finObject.Exception {
			super(pList, uId);
			theName = sName;
			theMgr.setNewTaxRegime(this);				
		
			/* Determine class of TaxRegime */
			if (sName.equals("Archive")) {
				theClass = TaxRegClass.ARCHIVE;
				theOrder = 0;
			}
			else if (sName.equals("Standard")) {
				theClass = TaxRegClass.STANDARD;
				theOrder = 0;
			}
			else if (sName.equals("LoInterest")) {
				theClass = TaxRegClass.LOINTEREST;
				theOrder = 1;
			}
			else if (sName.equals("AdditionalBand")) {
				theClass = TaxRegClass.ADDITIONAL;
				theOrder = 2;
			}
			else {
				throw new finObject.Exception(ExceptionClass.DATA,
	  					  					  ObjectClass.TAXREGIME,
	  					  					  this,
	                                          "Invalid TaxRegime");
			}
		}
		
		/**
		 * Compare this regime to another to establish equality.
		 * 
		 * @param that The Regime to compare to
		 * @return <code>true</code> if the regime is identical, <code>false</code> otherwise
		 */
		public boolean equals(finLink.itemElement that) {
			TaxRegime myRegime = (TaxRegime)that;
			if (this == that) return true;
			if (getId() != myRegime.getId()) return false;
			return (theName.compareTo(myRegime.theName) == 0);
		}

		/**
		 * Compare this tax regime to another to establish sort order.
		 * 
		 * @param that The Tax Regime to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		protected int compareTo(TaxRegime that) {
			long result;
			if (this == that) return 0;
			if (theOrder < that.theOrder) return -1;
			if (theOrder > that.theOrder) return  1;
			result = theName.compareTo(that.theName);
			if (result < 0) return -1;
			if (result > 0) return 1;
			result = (int)(getId() - that.getId());
			if (result == 0) return 0;
			else if (result < 0) return -1;
			else return 1;
		}

		/**
		 * Compare this tax regime to another to establish sort order.
		 * 
		 * @param that The Tax Regime to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		public int linkCompareTo(linkObject that) {
			TaxRegime myItem = (TaxRegime)that;
			return this.compareTo(myItem);
		}

		/**
		 * Determine whether this tax regime supports a Lo Salary Band
		 * @return <code>true/false</code>
		 */
		public  boolean         hasLoSalaryBand() {
			switch (theClass) {
				case ARCHIVE: 	return true;
				case STANDARD: 	return true;
				default: 		return false;
			}
		}
		
		/**
		 * Determine whether this tax regime treats capital gains as standard income
		 * @return <code>true/false</code>
		 */
		public  boolean         hasCapitalGainsAsIncome() {
			switch (theClass) {
				case STANDARD: 	return true;
				default: 		return false;
			}
		}
		
		/**
		 * Determine whether this tax regime supports an additional taxation band
		 * @return <code>true/false</code>
		 */
		public  boolean         hasAdditionalTaxBand() {
			switch (theClass) {
				case ADDITIONAL: 	return true;
				default: 			return false;
			}
		}
	}
	
	/**
	 * Enumeration of TaxRegime Classes. 
	 */
	protected enum TaxRegClass {
		/**
		 * Archive tax regime
		 */
		ARCHIVE,

		/**
		 * Standard tax regime
		 */
		STANDARD,

		/**
		 * Low Interest Tax Band
		 */
		LOINTEREST,

		/**
		 * Additional tax band
		 */
		ADDITIONAL,		
	}
	
	/**
	 * Represents a list of {@link Frequency} objects. 
	 */
	public class FreqList  extends finLink.itemCtl {
		/* Linking methods */
		public Frequency getFirst() { return (Frequency)super.getFirst(); }
		public Frequency getLast()  { return (Frequency)super.getLast(); }
		public Frequency searchFor(long uId) {
			return (Frequency)super.searchFor(uId); }
			
	 	/** 
	 	 * Construct an empty CORE frequency list
	 	 */
		protected FreqList() { super(ListStyle.CORE, true); }

		/** 
	 	 * Construct a generic frequency list
	 	 * @param pList the source Frequency list 
	 	 * @param pStyle the style of the list 
	 	 */
		protected FreqList(FreqList pList, ListStyle pStyle) { super(pList, pStyle); }

		/** 
	 	 * Construct a difference frequency list
	 	 * @param pNew the new Frequency list 
	 	 * @param pOld the old Frequency list 
	 	 */
		protected FreqList(FreqList pNew, FreqList pOld) { super(pNew, pOld); }
		
		/** 
	 	 * Clone a Frequency list
	 	 * @return the cloned list
	 	 */
		protected FreqList cloneIt() { return new FreqList(this, ListStyle.CORE); }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pItem item to be added
		 * @return the newly added item
		 */
		public finLink.itemElement addNewItem(finLink.itemElement pItem) {
			Frequency myFreq = new Frequency(this, (Frequency)pItem);
			myFreq.addToList();
			return myFreq;
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "Frequency"; }
				
		/**
		 * Search for a particular item by class
		 *  
		 * @param eClass The class of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		protected Frequency searchFor(FreqClass eClass) {
			Frequency myCurr;
			
			/* Loop through the items to find the match */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
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
		protected Frequency searchFor(String sName) {
			Frequency   myCurr;
			int         iDiff;
			
			/* Loop through the items to find the entry */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				iDiff = sName.compareTo(myCurr.getName());
				if (iDiff == 0) break;
			}
			
			/* Return to caller */
			return myCurr;
		}	
	}
	
	/**
	 * Represents a Frequency object. 
	 * @see finStatic.FreqList
	 */
	public class Frequency extends finLink.itemElement {
		/**
		 * The class of the Frequency
		 */
		private FreqClass  theClass = null;

		/**
		 * The name of the Frequency
		 */
		private String     theName  = null;

		/**
		 * The sort order of the Frequency
		 */
		private int        theOrder = -1;
		
		/**
		 * Return the name of the Frequency
		 * @return the name
		 */
		protected String    getName()              { return theName; }

		/**
		 * Return the sort order of the Frequency
		 * @return the order
		 */
		protected int       getOrder()             { return theOrder; }

		/**
		 * Return the Frequency class of the Frequency
		 * @return the class
		 */
		protected FreqClass getFrequency()         { return theClass; }
		
		/* Linking methods */
		public Frequency getNext() { return (Frequency)super.getNext(); }
		public Frequency getPrev() { return (Frequency)super.getPrev(); }
		
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
		public String itemType() { return "Frequency"; }
		
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
		 * Construct a copy of a Frequency.
		 * 
		 * @param pList	The list to associate the Frequency with
		 * @param pFrequency The frequency to copy 
		 */
		protected Frequency(FreqList  pList,
				            Frequency pFrequency) { 
			super(pList, pFrequency.getId());
			theName  = pFrequency.getName();
			theClass = pFrequency.theClass;
			setBase(pFrequency);
			setState(pFrequency.getState());
			theOrder = pFrequency.getOrder();
		}
		
		/**
		 * Construct a standard Frequency on load
		 * 
		 * @param pList	The list to associate the Frequency with
		 * @param uId   ID of Frequency
		 * @param sName Name of Frequency
		 * 
		 * @throws {@link finObject.Exception} if type is not supported
		 */
		public Frequency(FreqList pList,
				         long     uId,
				         String   sName) throws finObject.Exception {
			super(pList, uId);
			theName = sName;
			theMgr.setNewFrequency(this);				
		
			/* Determine class of Frequency */
			if (sName.equals("Monthly")) {
				theClass = FreqClass.MONTHLY;
				theOrder = 0;
			}
			else if (sName.equals("EndOfMonth")) {
				theClass = FreqClass.ENDMONTH;
				theOrder = 1;
			}
			else if (sName.equals("Quarterly")) {
				theClass = FreqClass.QUARTERLY;
				theOrder = 2;
			}
			else if (sName.equals("HalfYearly")) {
				theClass = FreqClass.HALFYEARLY;
				theOrder = 3;
			}
			else if (sName.equals("Annually")) {
				theClass = FreqClass.ANNUALLY;
				theOrder = 4;
			}
			else if (sName.equals("Maturity")) {
				theClass = FreqClass.MATURITY;
				theOrder = 5;
			}
			else if (sName.equals("TenMonths")) {
				theClass = FreqClass.TENMONTHS;
				theOrder = 6;
			}
			else {
				throw new finObject.Exception(ExceptionClass.DATA,
	  					  					  ObjectClass.FREQUENCY,
	  					  					  this,
	                                          "Invalid Frequency");
			}
		}
		
		/**
		 * Compare this frequency to another to establish equality.
		 * 
		 * @param that The Frequency to compare to
		 * @return <code>true</code> if the frequency is identical, <code>false</code> otherwise
		 */
		public boolean equals(finLink.itemElement that) {
			Frequency myFreq = (Frequency)that;
			if (this == that) return true;
			if (getId() != myFreq.getId()) return false;
			return (theName.compareTo(myFreq.theName) == 0);
		}

		/**
		 * Compare this frequency to another to establish sort order.
		 * 
		 * @param that The Frequency to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		protected int compareTo(Frequency that) {
			long result;
			if (this == that) return 0;
			if (theOrder < that.theOrder) return -1;
			if (theOrder > that.theOrder) return  1;
			result = theName.compareTo(that.theName);
			if (result < 0) return -1;
			if (result > 0) return 1;
			result = (int)(getId() - that.getId());
			if (result == 0) return 0;
			else if (result < 0) return -1;
			else return 1;
		}

		/**
		 * Compare this frequency to another to establish sort order.
		 * 
		 * @param that The Frequency to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		public int linkCompareTo(linkObject that) {
			Frequency myItem = (Frequency)that;
			return this.compareTo(myItem);
		}
	}
	
	/**
	 * Enumeration of Frequency Classes. 
	 */
	protected enum FreqClass {
		/**
		 * Monthly Frequency
		 */
		MONTHLY,

		/**
		 * Monthly Frequency (at end of month)
		 */
		ENDMONTH,

		/**
		 * Quarterly Frequency
		 */
		QUARTERLY,
		
		/**
		 * Half Yearly Frequency
		 */
		HALFYEARLY,

		/**
		 * Annual Frequency
		 */
		ANNUALLY,

		/**
		 * Only on Maturity
		 */
		MATURITY,

		/**
		 * Monthly for up to ten-months
		 */
		TENMONTHS;     
	}
}
