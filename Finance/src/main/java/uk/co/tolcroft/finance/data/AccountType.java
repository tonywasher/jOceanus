package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.finance.data.StaticClass.AccountClass;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.*;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
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
			            String	sName) throws ModelException {
		super(pList, sName);
	}
	
	/**
	 * Construct a standard account type on load
	 * @param pList	The list to associate the Account Type with
	 * @param isEnabled is the account type enabled
	 * @param uOrder the sort order
	 * @param pName Name of Account Type
	 * @param pDesc Description of Account Type
	 */
	private AccountType(List 	pList,
						int		uId,
			            boolean	isEnabled,
			            int		uOrder,
			            String	pName,
			            String	pDesc) throws ModelException {
		super(pList, uId, isEnabled, uOrder, pName, pDesc);
	}
	
	/**
	 * Construct a standard account type on load
	 * @param pList	The list to associate the Account Type with
	 * @param uId   ID of Account Type
	 * @param uControlId the control id of the new item
	 * @param isEnabled is the account type enabled
	 * @param uOrder the sort order
	 * @param pName Encrypted Name of Account Type
	 * @param pDesc Encrypted Description of Account Type
	 */
	private AccountType(List 	pList,
			            int		uId,
			            int		uControlId,
			            boolean	isEnabled,
			            int		uOrder,
			            byte[]	pName,
			            byte[]	pDesc) throws ModelException {
		super(pList, uId, uControlId, isEnabled, uOrder, pName, pDesc);
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
	public static class List extends StaticList<List, AccountType, AccountClass> {
		protected Class<AccountClass> getEnumClass() { return AccountClass.class; }
		
	 	/** 
	 	 * Construct an empty CORE account type list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(FinanceData pData) { 
			super(List.class, AccountType.class, pData, ListStyle.CORE);
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
		 * Construct a difference AccountType list
		 * @param pNew the new AccountType list 
		 * @param pOld the old AccountType list 
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
		public AccountType addNewItem(DataItem<?> pItem) {
			AccountType myType = new AccountType(this, (AccountType)pItem);
			add(myType);
			return myType;
		}
	
		/**
		 * Create a new empty element in the edit list (null-operation)
		 * @return the newly added item
		 */
		public AccountType addNewItem() { return null; }
			
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }
		
		/**
		 * Add an AccountType to the list
		 * @param pActType the Name of the account type
		 */ 
		public void addItem(String pActType) throws ModelException {
			AccountType myActType;
				
			/* Create a new Account Type */
			myActType = new AccountType(this, pActType);
				
			/* Check that this AccountType has not been previously added */
			if (searchFor(pActType) != null) 
				throw new ModelException(ExceptionClass.DATA,
	   					  			myActType,
			  			            "Duplicate Account Type");
				 
			/* Check that this AccountTypeId has not been previously added */
			if (!isIdUnique(myActType.getId())) 
				throw new ModelException(ExceptionClass.DATA,
	                      			myActType,
			  			            "Duplicate AccountTypeId");
				 
			/* Add the Account Type to the list */
			add(myActType);
		}	

		/**
		 * Add an AccountType to the list
		 * @param uId the Id of the account type
		 * @param isEnabled is the account type enabled
		 * @param uOrder the sort order
		 * @param pActType the Name of the account type
		 * @param pDesc the Description of the account type
		 */ 
		public void addItem(int	  	uId,
							boolean	isEnabled,
							int		uOrder,
				            String 	pActType,
				            String 	pDesc) throws ModelException {
			AccountType myActType;
				
			/* Create a new Account Type */
			myActType = new AccountType(this, uId, isEnabled, uOrder, pActType, pDesc);
				
			/* Check that this AccountTypeId has not been previously added */
			if (!isIdUnique(myActType.getId())) 
				throw new ModelException(ExceptionClass.DATA,
	                      			myActType,
			  			            "Duplicate AccountTypeId");
				 
			/* Add the Account Type to the list */
			add(myActType);
				 
			/* Validate the AccountType */
			myActType.validate();

			/* Handle validation failure */
			if (myActType.hasErrors()) 
				throw new ModelException(ExceptionClass.VALIDATE,
									myActType,
									"Failed validation");
		}	

		/**
		 * Add an AccountType to the list
		 * @param uId the Id of the account type
		 * @param uControlId the control id of the new item
		 * @param isEnabled is the account type enabled
		 * @param uOrder the sort order
		 * @param pActType the encrypted Name of the account type
		 * @param pDesc the Encrypted Description of the account type
		 */ 
		public void addItem(int    	uId,
							int	   	uControlId,
							boolean	isEnabled,
							int		uOrder,
				            byte[] 	pActType,
				            byte[] 	pDesc) throws ModelException {
			AccountType myActType;
				
			/* Create a new Account Type */
			myActType = new AccountType(this, uId, uControlId, isEnabled, uOrder, pActType, pDesc);
				
			/* Check that this AccountTypeId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new ModelException(ExceptionClass.DATA,
	                      			myActType,
			  			            "Duplicate AccountTypeId");
				 
			/* Add the Account Type to the list */
			add(myActType);
			
			/* Validate the AccountType */
			myActType.validate();

			/* Handle validation failure */
			if (myActType.hasErrors()) 
				throw new ModelException(ExceptionClass.VALIDATE,
									myActType,
									"Failed validation");
		}
	}	
}