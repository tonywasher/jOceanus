package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.finance.data.StaticClass.AccountClass;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.StaticData;

public class AccountType extends StaticData<AccountType, AccountClass> {
	/**
	 * The name of the object
	 */
	public static final String objName 	= "AccountType";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";

	/**
	 * Return the Account class of the AccountType
	 * @return the class
	 */
	public AccountClass getAccountClass()         { return super.getStaticClass(); }
	
	/* Linking methods */
	public AccountType getBase() { return (AccountType)super.getBase(); }
	
	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	public String itemType() { return objName; }
	
	/**
	 * Construct a copy of an Account Type.
	 * @param pList	The list to associate the Account Type with
	 * @param pAcType The Account Type to copy 
	 */
	protected AccountType(List 			pList,
			              AccountType 	pAcType) { 
		super(pList, pAcType);
	}
	
	/**
	 * Construct a standard account type on load
	 * @param pList	The list to associate the Account Type with
	 * @param sName Name of Account Type
	 */
	private AccountType(List 	pList,
			            String	sName) throws Exception {
		super(pList, sName);
	}
	
	/**
	 * Construct a standard account type on load
	 * @param pList	The list to associate the Account Type with
	 * @param uClassId the class id of the new item
	 * @param pName Name of Account Type
	 * @param pDesc Description of Account Type
	 */
	private AccountType(List 	pList,
						int		uId,
			            int		uClassId,
			            String	pName,
			            String	pDesc) throws Exception {
		super(pList, uId, uClassId, pName, pDesc);
	}
	
	/**
	 * Construct a standard account type on load
	 * @param pList	The list to associate the Account Type with
	 * @param uId   ID of Account Type
	 * @param uControlId the control id of the new item
	 * @param uClassId the class id of the new item
	 * @param pName Encrypted Name of Account Type
	 * @param pDesc Encrypted Description of Account Type
	 */
	private AccountType(List 	pList,
			            int		uId,
			            int		uControlId,
			            int		uClassId,
			            byte[]	pName,
			            byte[]	pDesc) throws Exception {
		super(pList, uId, uControlId, uClassId, pName, pDesc);
	}
	
	/**
	 * Determine whether the AccountType is external
	 * 
	 * @return <code>true</code> if the account is external, <code>false</code> otherwise.
	 */
	public boolean isExternal() {
		switch (getAccountClass()) {
			case EXTERNAL:
			case OWNER:
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
		switch (getAccountClass()) {
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
		switch (getAccountClass()) {
			case HOUSE:
			case CAR:
			case SHARES:
			case LIFEBOND:
			case UNITTRUST:
			case UNITISA:
			case ENDOWMENT:
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
		switch (getAccountClass()) {
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
		switch (getAccountClass()) {
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
		switch (getAccountClass()) {
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
		switch (getAccountClass()) {
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
		switch (getAccountClass()) {
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
		switch (getAccountClass()) {
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
		switch (getAccountClass()) {
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
			case ENDOWMENT:
			case DEBTS:
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
		switch (getAccountClass()) {
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
		switch (getAccountClass()) {
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
		switch (getAccountClass()) {
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
		switch (getAccountClass()) {
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
	 * Determine whether the AccountType is Owner
	 * 
	 * @return <code>true</code> if the account is Owner, <code>false</code> otherwise.
	 */
	public boolean isOwner() {
		switch (getAccountClass()) {
			case INHERITANCE:
			case OWNER:
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
	protected boolean isCash()      { return (getAccountClass() == AccountClass.CASH); }

	/**
	 * Determine whether the AccountType is parents
	 * 
	 * @return <code>true</code> if the account is parents, <code>false</code> otherwise.
	 */
	protected boolean isInheritance()   { return (getAccountClass() == AccountClass.INHERITANCE); }

	/**
	 * Determine whether the AccountType is WriteOff
	 * 
	 * @return <code>true</code> if the account is WriteOff, <code>false</code> otherwise.
	 */
	protected boolean isWriteOff()  { return (getAccountClass() == AccountClass.WRITEOFF); }

	/**
	 * Determine whether the AccountType is market
	 * 
	 * @return <code>true</code> if the account is market, <code>false</code> otherwise.
	 */
	protected boolean isMarket()    { return (getAccountClass() == AccountClass.MARKET); }

	/**
	 * Determine whether the AccountType is TaxMan
	 * 
	 * @return <code>true</code> if the account is taxman, <code>false</code> otherwise.
	 */
	protected boolean isTaxMan()    { return (getAccountClass() == AccountClass.TAXMAN); }

	/**
	 * Determine whether the AccountType is Employer
	 * 
	 * @return <code>true</code> if the account is employer, <code>false</code> otherwise.
	 */
	protected boolean isEmployer()    { return (getAccountClass() == AccountClass.EMPLOYER); }

	/**
	 * Determine whether the AccountType is endowment
	 * 
	 * @return <code>true</code> if the account is endowment, <code>false</code> otherwise.
	 */
	public boolean isEndowment() { return (getAccountClass() == AccountClass.ENDOWMENT); }

	/**
	 * Determine whether the AccountType is deferred
	 * 
	 * @return <code>true</code> if the account is deferred, <code>false</code> otherwise.
	 */
	protected boolean isDeferred()  { return (getAccountClass() == AccountClass.DEFERRED); }

	/**
	 * Determine whether the AccountType is benefit
	 * 
	 * @return <code>true</code> if the account is benefit, <code>false</code> otherwise.
	 */
	protected boolean isBenefit()   { return (getAccountClass() == AccountClass.BENEFIT); }

	/**
	 * Determine whether the AccountType is a Share
	 *  
	 * @return <code>true</code> if the account is Share, <code>false</code> otherwise.
	 */
	public boolean isShares()   { return (getAccountClass() == AccountClass.SHARES); }

	/**
	 * Determine whether the AccountType is a LifeBond
	 * 
	 * @return <code>true</code> if the account is LifeBond, <code>false</code> otherwise.
	 */
	public boolean isLifeBond()   { return (getAccountClass() == AccountClass.LIFEBOND); }

	/**
	 * Determine whether the AccountType is internal
	 * 
	 * @return <code>true</code> if the account is internal, <code>false</code> otherwise.
	 */
	public boolean isInternal()  { return !isExternal(); }
	
	/**
	 * Represents a list of {@link AccountType} objects. 
	 */
	public static class List extends StaticList<AccountType, AccountClass> {
		protected Class<AccountClass> getEnumClass() { return AccountClass.class; }
		
	 	/** 
	 	 * Construct an empty CORE account type list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(FinanceData pData) { 
			super(AccountType.class, pData, ListStyle.CORE);
		}

		/** 
	 	 * Construct a generic account type list
	 	 * @param pList the source account type list 
	 	 * @param pStyle the style of the list 
	 	 */
		public List(List pList, ListStyle pStyle) { super(AccountType.class, pList, pStyle); }

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
		public AccountType addNewItem(DataItem<?> pItem) {
			AccountType myType = new AccountType(this, (AccountType)pItem);
			add(myType);
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
		public String itemType() { return listName; }
		
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
				 
			/* Check that this ClassId has not been previously added */
			if (searchForEnum(myActType.getStaticClassId()) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myActType,
			                        "Duplicate AccountClass");
				
			/* Add the Account Type to the list */
			add(myActType);
		}	

		/**
		 * Add an AccountType to the list
		 * @param uClassId the ClassId of the account type
		 * @param pActType the Name of the account type
		 * @param pDesc the Description of the account type
		 */ 
		public void addItem(int	   uId,
							int	   uClassId,
				            String pActType,
				            String pDesc) throws Exception {
			AccountType myActType;
				
			/* Create a new Account Type */
			myActType = new AccountType(this, uId, uClassId, pActType, pDesc);
				
			/* Check that this AccountTypeId has not been previously added */
			if (!isIdUnique(myActType.getId())) 
				throw new Exception(ExceptionClass.DATA,
	                      			myActType,
			  			            "Duplicate AccountTypeId");
				 
			/* Check that this AccountType has not been previously added */
			if (searchFor(myActType.getName()) != null) 
				throw new Exception(ExceptionClass.DATA,
	   					  			myActType,
			  			            "Duplicate Account Type");
				 
			/* Check that this ClassId has not been previously added */
			if (searchForEnum(uClassId) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myActType,
			                        "Duplicate AccountClass");
				
			/* Add the Account Type to the list */
			add(myActType);
		}	

		/**
		 * Add an AccountType to the list
		 * @param uId the Id of the account type
		 * @param uControlId the control id of the new item
		 * @param uClassId the ClassId of the account type
		 * @param pActType the encrypted Name of the account type
		 * @param pDesc the Encrypted Description of the account type
		 */ 
		public void addItem(int    uId,
							int	   uControlId,
							int	   uClassId,
				            byte[] pActType,
				            byte[] pDesc) throws Exception {
			AccountType myActType;
				
			/* Create a new Account Type */
			myActType = new AccountType(this, uId, uControlId, uClassId, pActType, pDesc);
				
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
				 
			/* Check that this ClassId has not been previously added */
			if (searchForEnum(uClassId) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myActType,
			                        "Duplicate AccountClass");
				
			/* Add the Account Type to the list */
			add(myActType);
		}
	}	
}
