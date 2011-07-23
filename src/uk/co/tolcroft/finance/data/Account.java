package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.DataList.ListStyle;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.finance.data.DataSet.*;

public class Account extends EncryptedItem<Account> {
	/**
	 * The name of the object
	 */
	public static final String objName = "Account";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";

	/**
	 * Account Name length
	 */
	public final static int NAMELEN 		= 30;

	/**
	 * Account Description length
	 */
	public final static int DESCLEN 		= 50;

	/**
	 * Account WebSite length
	 */
	public final static int WSITELEN 		= 50;

	/**
	 * Account CustNo length
	 */
	public final static int CUSTLEN 		= 20;

	/**
	 * Account UserId length
	 */
	public final static int UIDLEN 			= 20;

	/**
	 * Account PassWord length
	 */
	public final static int PWDLEN 			= 20;

	/**
	 * Account details length
	 */
	public final static int ACTLEN 			= 20;

	/**
	 * Account Notes length
	 */
	public final static int NOTELEN 		= 500;

	/* Members */
	private int                   theOrder     = -1;
	private Integer				  theParentId  = null;
	private Integer				  theAliasId   = null;
	private int 				  theActTypeId = -1;
	private Event                 theEarliest  = null;
	private Event                 theLatest    = null;
	private AcctPrice             theInitPrice = null;
	private boolean               isCloseable  = true;
	private boolean               hasRates	   = false;
	private boolean               hasPrices	   = false;
	private boolean               hasPatterns  = false;
	private boolean               isPatterned  = false;
	private boolean               isParent	   = false;
	private boolean               isAliasedTo  = false;
		
	/* Access methods */
	public  Values      getObj()       	{ return (Values)super.getObj(); }	
	public  String      getName()      	{ return getPairValue(getObj().getName()); }
	public  String      getDesc()      	{ return getPairValue(getObj().getDesc()); }
	public  Account     getParent()    	{ return getObj().getParent(); }
	public  Integer		getParentId()  	{ return theParentId; }
	public  Account     getAlias()    	{ return getObj().getAlias(); }
	public  Integer     getAliasId()  	{ return theAliasId; }
	public  Event       getEarliest()  	{ return theEarliest; }
	public  Event       getLatest()    	{ return theLatest; }
	public  AcctPrice   getInitPrice()  { return theInitPrice; }
	public  AccountType getActType()   	{ return getObj().getType(); }
	public  int         getOrder()     	{ return theOrder; }
	public  Date        getMaturity()  	{ return getObj().getMaturity(); }
	public  Date    	getClose()     	{ return getObj().getClose(); }
	public  char[]    	getWebSite()	{ return getPairValue(getObj().getWebSite()); }
	public  char[]    	getCustNo()		{ return getPairValue(getObj().getCustNo()); }
	public  char[]    	getUserId()		{ return getPairValue(getObj().getUserId()); }
	public  char[]    	getPassword()	{ return getPairValue(getObj().getPassword()); }
	public  char[]    	getAccount()	{ return getPairValue(getObj().getAccount()); }
	public  char[]    	getNotes()		{ return getPairValue(getObj().getNotes()); }
	public  boolean     isCloseable()  	{ return isCloseable; }
	public  boolean     isParent()  	{ return isParent; }
	public  boolean     isClosed()     	{ return (getClose() != null); }
	public  boolean     isAlias()     	{ return (getAlias() != null); }
	public  boolean     isAliasedTo()  	{ return isAliasedTo; }
	public  boolean     isDeletable()  	{ 
		return ((theLatest == null) && 
				(!isDeleted()) &&
				(!isParent)    &&
				(!hasRates)    &&
				((!hasPrices) || (getState() == DataState.NEW))  &&
				(!hasPatterns) && 
				(!isAliasedTo) &&
				(!isPatterned) && 
				(!getActType().isReserved())); 
	}
		
	/* Encrypted bytes access */
	public  byte[]	getNameBytes()      { return getPairBytes(getObj().getName()); }
	public  byte[]  getDescBytes()      { return getPairBytes(getObj().getDesc()); }
	public  byte[]  getWebSiteBytes()	{ return getPairBytes(getObj().getWebSite()); }
	public  byte[]  getCustNoBytes()	{ return getPairBytes(getObj().getCustNo()); }
	public  byte[]  getUserIdBytes()	{ return getPairBytes(getObj().getUserId()); }
	public  byte[]  getPasswordBytes()	{ return getPairBytes(getObj().getPassword()); }
	public  byte[]  getAccountBytes()	{ return getPairBytes(getObj().getAccount()); }
	public  byte[]  getNotesBytes()		{ return getPairBytes(getObj().getNotes()); }
	
	/* Linking methods */
	public Account     getBase() { return (Account)super.getBase(); }
	public boolean	   isLocked(){ return isClosed(); }
	
	/* Field IDs */
	public static final int FIELD_NAME     = EncryptedItem.NUMFIELDS;
	public static final int FIELD_DESC     = EncryptedItem.NUMFIELDS+1;
	public static final int FIELD_TYPE     = EncryptedItem.NUMFIELDS+2;
	public static final int FIELD_MATURITY = EncryptedItem.NUMFIELDS+3;
	public static final int FIELD_CLOSE    = EncryptedItem.NUMFIELDS+4;
	public static final int FIELD_PARENT   = EncryptedItem.NUMFIELDS+5;
	public static final int FIELD_ALIAS    = EncryptedItem.NUMFIELDS+6;
	public static final int FIELD_WEBSITE  = EncryptedItem.NUMFIELDS+7;
	public static final int FIELD_CUSTNO   = EncryptedItem.NUMFIELDS+8;
	public static final int FIELD_USERID   = EncryptedItem.NUMFIELDS+9;
	public static final int FIELD_PASSWORD = EncryptedItem.NUMFIELDS+10;
	public static final int FIELD_ACCOUNT  = EncryptedItem.NUMFIELDS+11;
	public static final int FIELD_NOTES    = EncryptedItem.NUMFIELDS+12;
	public static final int NUMFIELDS	   = EncryptedItem.NUMFIELDS+13;
	
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
	public static String	fieldName(int iField) {
		switch (iField) {
			case FIELD_NAME:		return "Name";
			case FIELD_DESC:		return "Description";
			case FIELD_TYPE:		return "AccountType";
			case FIELD_CLOSE:		return "CloseDate";
			case FIELD_MATURITY:	return "Maturity";
			case FIELD_PARENT:		return "Parent";
			case FIELD_ALIAS:		return "Alias";
			case FIELD_WEBSITE:		return "WebSite";
			case FIELD_CUSTNO:		return "CustomerNo";
			case FIELD_USERID:		return "UserId";
			case FIELD_PASSWORD:	return "Password";
			case FIELD_ACCOUNT:		return "Account";
			case FIELD_NOTES:		return "Notes";
			default:		  		return EncryptedItem.fieldName(iField);
		}
	}
	
	/**
	 * Determine the field name in a non-static fashion 
	 */
	public String getFieldName(int iField) { return fieldName(iField); }
	
	/**
	 * Format the value of a particular field as a table row
	 * @param iField the field number
	 * @param pObj the values to use
	 * @return the formatted field
	 */
	public String formatField(int iField, histObject pObj) {
		String	myString = "";
		Values 	myObj 	 = (Values)pObj;
		switch (iField) {
			case FIELD_NAME:	
				myString += myObj.getNameValue(); 
				break;
			case FIELD_DESC:	
				myString += myObj.getDescValue(); 
				break;
			case FIELD_TYPE:	
				if ((getActType() == null) &&
					(theActTypeId != -1))
					myString += "Id=" + theActTypeId;
				else
					myString += AccountType.format(getActType()); 
				break;
			case FIELD_CLOSE:	
				myString += Date.format(myObj.getClose()); 
				break;
			case FIELD_MATURITY:	
				myString += Date.format(myObj.getMaturity()); 
				break;
			case FIELD_PARENT:	
				if ((myObj.getParent() == null) &&
					(theParentId != null))
					myString += "Id=" + theParentId;
				else
					myString += Account.format(myObj.getParent()); 
				break;
			case FIELD_ALIAS:	
				if ((myObj.getAlias() == null) &&
					(theAliasId != null))
					myString += "Id=" + theAliasId;
				else
					myString += Account.format(myObj.getAlias()); 
				break;
			case FIELD_WEBSITE:	
				myString += getCharArrayPairString(myObj.getWebSite()); 
				break;
			case FIELD_CUSTNO:	
				myString += getCharArrayPairString(myObj.getCustNo()); 
				break;
			case FIELD_USERID:	
				myString += getCharArrayPairString(myObj.getUserId()); 
				break;
			case FIELD_PASSWORD:	
				myString += getCharArrayPairString(myObj.getPassword()); 
				break;
			case FIELD_ACCOUNT:	
				myString += getCharArrayPairString(myObj.getAccount()); 
				break;
			case FIELD_NOTES:	
				myString += getCharArrayPairString(myObj.getNotes()); 
				break;
			default: 		
				myString += super.formatField(iField, pObj); 
				break;
		}
		return myString;
	}
							
	/**
	 * Construct a copy of an Account
	 * @param pAccount The Account to copy 
	 */
	public Account(List pList, Account pAccount) {
		/* Set standard values */
		super(pList, pAccount.getId());
		Values myObj = new Values(pAccount.getObj());
		setObj(myObj);
		setControlKey(pAccount.getControlKey());
		theOrder     = pAccount.getOrder();
		theEarliest  = pAccount.theEarliest;
		theLatest    = pAccount.theLatest;
		isCloseable  = pAccount.isCloseable();
		isAliasedTo  = pAccount.isAliasedTo();
		isParent     = pAccount.isParent();
		isPatterned  = pAccount.isPatterned;
		hasPatterns  = pAccount.hasPatterns;
		hasRates     = pAccount.hasRates;
		hasPrices    = pAccount.hasPrices;
			
		/* Switch on the LinkStyle */
		switch (pList.getStyle()) {
			case CORE:
				/* Create a new id for the item */
				setId(0); 
				pList.setNewId(this);
				break;
			case EDIT:
				setBase(pAccount);
				pList.setNewId(this);				
				break;
			case UPDATE:
				setBase(pAccount);
				setState(pAccount.getState());
				break;
		}
	}
	
	/**
	 * Standard constructor for account added from Database/Backup
	 * @param pList the List to add to
	 * @param uId the Account id
	 * @param pName the Encrypted Name of the account
	 * @param uAcTypeId the Account type id
	 * @param pDesc the Encrypted Description of the account
	 * @param pMaturity the Maturity date for the account
	 * @param pClose the Close date for the account
	 * @param uParentId the Parent id (or -1 if no parent)
	 * @param uAliasId the Alias id (or -1 if no parent)
	 * @param pWebSite the Encrypted WebSite of the account
	 * @param pCustNo the Encrypted CustomerId of the account
	 * @param pUserId the Encrypted UserId of the account
	 * @param pPassword the Encrypted Password of the account
	 * @param pAccount the Encrypted Account details of the account
	 * @param pNotes the Encrypted Notes for the account
	 */
	private Account(List    		pList,
			        int           	uId,
			        int				uControlId,
					byte[]			pName, 
					int				uAcTypeId,
					byte[]         	pDesc,
					java.util.Date 	pMaturity,
			        java.util.Date 	pClose,
			        Integer        	pParentId,
			        Integer        	pAliasId,
			        byte[]			pWebSite,
			        byte[]			pCustNo,
			        byte[]			pUserId,
			        byte[]			pPassword,
			        byte[]			pAccount,
			        byte[]			pNotes) throws Exception {
		/* Initialise the item */
		super(pList, uId);
		
		/* Local Variable */
		AccountType myActType;
		
		/* Initialise the object */
		Values myObj	= new Values();
		setObj(myObj);
		
		/* Store the IDs */
		theActTypeId = uAcTypeId;
		theParentId  = pParentId;
		theAliasId   = pAliasId;
		
		/* Set ControlId */
		setControlKey(uControlId);
		
		/* Look up the Account Type */
		DataSet	myData 	= pList.getData();
		myActType = myData.getAccountTypes().searchFor(uAcTypeId);
		if (myActType == null) 
			throw new Exception(ExceptionClass.DATA,
		                        this,
					            "Invalid Account Type Id");
		myObj.setType(myActType);
		theOrder    = myActType.getOrder();

		/* Parse the maturity date if it exists */
		if (pMaturity != null) 
			myObj.setMaturity(new Date(pMaturity));
			
		/* Parse the closed date if it exists */
		if (pClose != null) 
			myObj.setClose(new Date(pClose));
				
		/* Record the encrypted values */
		myObj.setName(new StringPair(pName));
		myObj.setDesc((pDesc == null) ? null : new StringPair(pDesc));
		myObj.setWebSite((pWebSite == null) ? null : new CharArrayPair(pWebSite));
		myObj.setCustNo((pCustNo == null) ? null : new CharArrayPair(pCustNo));
		myObj.setUserId((pUserId == null) ? null : new CharArrayPair(pUserId));
		myObj.setPassword((pPassword == null) ? null : new CharArrayPair(pPassword));
		myObj.setAccount((pAccount == null) ? null : new CharArrayPair(pAccount));
		myObj.setNotes((pNotes == null) ? null : new CharArrayPair(pNotes));
		
		/* Allocate the id */
		pList.setNewId(this);				
	}
	
	/**
	 * Standard constructor for account added from SpreadSheet
	 * @param pList the List to add to
	 * @param sName the Name of the account
	 * @param uAcTypeId the Account type id
	 * @param pMaturity the Maturity date for the account
	 * @param pClose the Close date for the account
	 * @param uParentId the Parent id (or -1 if no parent)
	 * @param uAliasId the Alias id (or -1 if no parent)
	 */
	private Account(List    		pList,
					String         	sName, 
					int				uAcTypeId,
					String			pDesc,
					java.util.Date 	pMaturity,
			        java.util.Date 	pClose,
			        Integer        	pParentId,
			        Integer        	pAliasId,
			        char[]			pWebSite,
			        char[]			pCustNo,
			        char[]			pUserId,
			        char[]			pPassword,
			        char[]			pAccount,
			        char[]			pNotes) throws Exception {
		/* Initialise the item */
		super(pList, 0);
		
		/* Local Variable */
		AccountType myActType;
		
		/* Initialise the object */
		Values myObj	= new Values();
		setObj(myObj);

		/* Record the encrypted values */
		myObj.setName(new StringPair(sName));
		myObj.setDesc((pDesc == null) ? null : new StringPair(pDesc));
		myObj.setWebSite((pWebSite == null) ? null : new CharArrayPair(pWebSite));
		myObj.setCustNo((pCustNo == null) ? null : new CharArrayPair(pCustNo));
		myObj.setUserId((pUserId == null) ? null : new CharArrayPair(pUserId));
		myObj.setPassword((pPassword == null) ? null : new CharArrayPair(pPassword));
		myObj.setAccount((pAccount == null) ? null : new CharArrayPair(pAccount));
		myObj.setNotes((pNotes == null) ? null : new CharArrayPair(pNotes));
		
		/* Store the IDs */
		theActTypeId = uAcTypeId;
		theParentId  = pParentId;
		theAliasId   = pAliasId;
		
		/* Look up the Account Type */
		DataSet	myData 	= pList.getData();
		myActType = myData.getAccountTypes().searchFor(uAcTypeId);
		if (myActType == null) 
			throw new Exception(ExceptionClass.DATA,
		                        this,
					            "Invalid Account Type Id");
		myObj.setType(myActType);
		theOrder    = myActType.getOrder();

		/* Parse the maturity date if it exists */
		if (pMaturity != null) 
			myObj.setMaturity(new Date(pMaturity));
			
		/* Parse the closed date if it exists */
		if (pClose != null) 
			myObj.setClose(new Date(pClose));
				
		/* Allocate the id */
		pList.setNewId(this);				
	}
	
	/* Standard constructor for a newly inserted account */
	public Account(List pList) {
		super(pList, 0);
		Values theObj = new Values();
		setObj(theObj);
		setControlKey(pList.getData().getControl().getControlKey());
		pList.setNewId(this);				
	}

	/**
	 * Compare this account to another to establish equality.
	 * 
	 * @param pThat The account to compare to
	 * @return <code>true</code> if the account is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is an Account */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as an Account */
		Account myThat = (Account)pThat;
		
		/* Check for equality */
		if (getId() != myThat.getId()) return false;
		
		/* Compare the changeable values */
		return getObj().histEquals(myThat.getObj());
	}

	/**
	 * Compare this account to another to establish sort order. 
	 * @param pThat The Account to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		long result;
		ListStyle myStyle;

		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is an Account */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as an Account */
		Account myThat = (Account)pThat;
		
		/* Access the list style */
		myStyle = getList().getStyle();
		
		/* If this is in an update view we need to do some special ordering to handle alias and parent dependencies */
		if (myStyle == ListStyle.UPDATE) {
			/* If we are comparing child with parent */
			if (isChild() != myThat.isChild()) {
				/* List children after parents */
				if (isChild()) 	return 1;
				else			return -1;
			}
			
			/* If we are comparing alias with non-alias */
			if (isAlias() != myThat.isAlias()) {
				/* List alias after non-alias */
				if (isAlias()) 	return 1;
				else			return -1;
			}
		}
		
		/* If the order differs */
		if (theOrder < myThat.theOrder) return -1;
		if (theOrder > myThat.theOrder) return  1;
		
		/* If the names differ */
		if (getName() != myThat.getName()) {
			/* Handle nulls */
			if (this.getName() == null) return  1;
			if (myThat.getName() == null) return -1;
			
			/* Compare the names */
			result = getName().compareTo(myThat.getName());
			if (result < 0) return -1;
			if (result > 0) return 1;
		}
		
		/* Compare the IDs */
		result = (int)(getId() - myThat.getId());
		if (result == 0) return 0;
		else if (result < 0) return -1;
		else return 1;
	}
		
	/* Account flags */
	public 	boolean isPriced()    { return getActType().isPriced(); }
	protected boolean isMarket()    { return getActType().isMarket(); }
	public 	boolean isExternal()  { return getActType().isExternal(); }
	protected boolean isSpecial()   { return getActType().isSpecial(); }
	protected boolean isInternal()  { return getActType().isInternal(); }
	protected boolean isInheritance() { return getActType().isInheritance(); }
	protected boolean isTaxMan()    { return getActType().isTaxMan(); }
	public  boolean isMoney()     { return getActType().isMoney(); }
	protected boolean isCash()      { return getActType().isCash(); }
	protected boolean isWriteOff()  { return getActType().isWriteOff(); }
	protected boolean isEndowment() { return getActType().isEndowment(); }
	protected boolean isOwner()   { return getActType().isOwner(); }
	public	boolean isTaxFree()   { return getActType().isTaxFree(); }
	public	boolean isUnitTrust() { return getActType().isUnitTrust(); }
	public	boolean isDebt()      { return getActType().isDebt(); }
	public 	boolean isChild()     { return getActType().isChild(); }
	public 	boolean isBond()      { return getActType().isBond(); }
	public 	boolean isBenefit()   { return getActType().isBenefit(); }
	public 	boolean isLifeBond()  { return getActType().isLifeBond(); }
	public 	boolean isCapital()   { return getActType().isCapital(); }
	public 	boolean isCapitalGains()   { return getActType().isCapitalGains(); }
	
	/**
	 * Validate the account
	 */
	public void validate() {
		boolean 		isValid;
		AccountType 	myType = getActType();
		List 			myList = (List)getList();
		DataSet			mySet  = myList.getData();
		
		/* Name must be non-null */
		if (getName() == null) {
			addError("Name must be non-null", FIELD_NAME);
	    }
		
		/* Check that the name is unique */
		else { 
			/* The description must not be too long */
			if (getName().length() > NAMELEN) {
				addError("Name is too long", FIELD_NAME);
			}
				
			if (myList.countInstances(getName()) > 1) {
				addError("Name must be unique", FIELD_NAME);
			}
	    }
		
		/* The description must not be too long */
		if ((getDesc() != null) && (getDesc().length() > DESCLEN)) {
			addError("Description is too long", FIELD_DESC);
		}
			
		/* If the account is priced */
		if (myType.isPriced()) {
			/* If this account has an alias */
			if (getAlias() != null) {
				/* Must not have prices */
				if (hasPrices)
					addError("Aliased account has prices", FIELD_TYPE);
				
				/* Alias account must have prices */
				if (!getAlias().hasPrices)
					addError("Alias account has no prices", FIELD_TYPE);
			}
			
			/* else this is a standard account */
			else {
				/* Must have prices */
				if (!hasPrices)
					addError("Priced account has no prices", FIELD_TYPE);
			}
	    }
		
		/* else the account is not priced */
		else {
			/* Prices cannot exist */
			if (hasPrices)
				addError("non-Priced account has prices", FIELD_TYPE);
		}
		
		/* If the account is not a child then parent cannot exist */
		if (!myType.isChild()) {
			if (getParent() != null)
				addError("Non-child account has parent", FIELD_PARENT);
	    }
		
		/* else we should have a parent */
		else {
			/* If data has been fully loaded we have no parent */
			if ((mySet.getLoadState() != LoadState.INITIAL) && 
				(getParent() == null)) 
				addError("Child Account must have parent", FIELD_PARENT);
				
			/* if we have a parent */
			if (getParent() != null) {
				/* check that any parent is owner */
				if (!getParent().isOwner())
					addError("Parent account must be owner", FIELD_PARENT);
			
				/* If we are open then parent must be open */
				if (!isClosed() && getParent().isClosed())
					addError("Parent account must not be closed", FIELD_PARENT);
			}
	    }
		
		/* If we have an alias */
		if (getAlias() != null) {
			/* Cannot alias to self */
			if (!Account.differs(this, getAlias()))
				addError("Cannot alias to self", FIELD_ALIAS);

			/* Cannot alias to same type */
			else if (!AccountType.differs(myType, getAlias().getActType()))
				addError("Cannot alias to same account type", FIELD_ALIAS);

			/* Must be alias type */
			if (!myType.canAlias())
				addError("This account type cannot alias", FIELD_ALIAS);

			/* Must not be aliased to */
			if (isAliasedTo)
				addError("This account is already aliased to", FIELD_ALIAS);

			/* Alias must be alias type */
			if (!getAlias().getActType().canAlias())
				addError("The alias account type is invalid", FIELD_ALIAS);

			/* Alias cannot be aliased */
			if (getAlias().isAlias())
				addError("The alias account is already aliased", FIELD_ALIAS);
	    }
		
		/* If the account has rates then it must be money-based */
		if (hasRates) {
			if (!myType.isMoney())
				addError("non-Money account has rates", FIELD_TYPE);
		} 
		
		/* If the account has a maturity rate then it must be a bond */
		if (getMaturity() != null) {
			if (!myType.isBond())
				addError("non-Bond has maturity date", FIELD_MATURITY);
		}
			
		/* Open Bond accounts must have maturity */
		if (myType.isBond()) {
			if (!isClosed() && (getMaturity() == null))
				addError("Bond must have maturity date", FIELD_MATURITY);
		}
			
		/* If data has been fully loaded and the account is closed */
		if ((mySet.getLoadState() != LoadState.INITIAL) && 
			(isClosed())) {
			/* Account must be close-able */
			if (!isCloseable())
				addError("Non-closeable account is closed", FIELD_CLOSE);
		}
			
		/* The WebSite must not be too long */
		if ((getWebSite() != null) && (getWebSite().length > WSITELEN)) {
			addError("WebSite is too long", FIELD_WEBSITE);
		}
			
		/* The CustNo must not be too long */
		if ((getCustNo() != null) && (getCustNo().length > CUSTLEN)) {
			addError("Customer No. is too long", FIELD_CUSTNO);
		}
			
		/* The UserId must not be too long */
		if ((getUserId() != null) && (getUserId().length > UIDLEN)) {
			addError("UserId is too long", FIELD_USERID);
		}
			
		/* The Password must not be too long */
		if ((getPassword() != null) && (getPassword().length > PWDLEN)) {
			addError("Password is too long", FIELD_PASSWORD);
		}
			
		/* The Account must not be too long */
		if ((getAccount() != null) && (getAccount().length > ACTLEN)) {
			addError("Account is too long", FIELD_ACCOUNT);
		}
			
		/* The Notes must not be too long */
		if ((getNotes() != null) && (getNotes().length > NOTELEN)) {
			addError("WebSite is too long", FIELD_NOTES);
		}
			
		/* Set validation flag */
		isValid = !hasErrors();
		if (isValid) setValidEdit();
	}
	
	/**
	 * Get the value of an account on a specific date 
	 * @param  pDate    The date of the valuation
	 * @return Valuation of account
	 */
	public Money getValue(Date pDate) {
		Event   						myCurr;
		Event.List						myEvents;
		DataList<Event>.ListIterator 	myIterator;
		int     						myResult;
		Money 							myAmount;
		Money 							myValue;
		List 							myList = (List)getList();
		DataSet							mySet  = myList.getData();
		
		/* Initialise money */
		myValue = new Money(0);
			
		/* Access the Events and create an iterator on the events */
		myEvents 	= mySet.getEvents();
		myIterator 	= myEvents.listIterator();
		
		/* Loop through the Events extracting relevant elements */
		while ((myCurr = myIterator.next()) != null) {
			/* Check the range */
			myResult = pDate.compareTo(myCurr.getDate());
			
			/* Handle out of range */
			if (myResult == -1) break;
				
			/* If this Event relates to this account */
			if (myCurr.relatesTo(this)) {
				/* Access the amount */
				myAmount = myCurr.getAmount();
				
				/* If this is a credit add the value */
				if (this.compareTo(myCurr.getCredit()) == 0)
					myValue.addAmount(myAmount);
			
				/* else subtract from value */
				else myValue.subtractAmount(myAmount);
			}
		}
			
		/* Return the value */
		return myValue;
	}
		
	/**
	 * Reset the account flags after changes to events
	 */
	public void reset() {
		/* Reset flags */
		isCloseable   = true;
		theEarliest   = null;
		theLatest     = null;
		theInitPrice  = null;
		hasRates      = false;
		hasPrices     = false;
		hasPatterns   = false;
		isPatterned   = false;
		isParent	  = false;
		isAliasedTo	  = false;
	}
	
	/**
	 * Touch an account with an event
	 */
	public void touchAccount(Event pEvent) {
		/* Record the event */
		if (theEarliest == null) theEarliest = pEvent;
		theLatest = pEvent;
		
		/* If we have a parent, touch it */
		if (getParent() != null) 
			getParent().touchAccount(pEvent);
	}

	/**
	 * Touch an account with a rate
	 */
	protected void touchRate() {
		/* Record the rate */
		hasRates = true;
	}
		
	/**
	 * Touch an account with a price
	 * @param pPrice the price
	 */
	public void touchPrice(AcctPrice pPrice) {
		/* Record the price */
		hasPrices = true;
		if (theInitPrice == null) theInitPrice = pPrice;
	}
		
	/**
	 * Touch an account with a pattern
	 */
	protected void touchPattern() {
		/* Record the pattern */
		hasPatterns = true;
	}
		
	/**
	 * Touch an account with a pattern
	 */
	public void touchPartner() {
		/* Record the pattern */
		isPatterned = true;
	}
		
	/**
	 * Touch an account with a parent
	 */
	public void touchParent() {
		/* Record the parent */
		isParent = true;
	}
		
	/**
	 * Touch an account with an alias
	 */
	public void touchAlias() {
		/* Record the alias */
		isAliasedTo = true;
	}
		
	/**
	 * Set non-closeable
	 */
	public void setNonCloseable() {
		/* Record the status */
		isCloseable = false;
	}
	
	/**
	 * Adjust closed date
	 */
	public void adjustClosed() {
		/* If we have a latest event that is later than the close */
		if (getClose().compareTo(theLatest.getDate()) < 0) {
			/* Record the more accurate date */
			setClose(theLatest.getDate());
		}

		/* If the maturity is null for a bond set it to close date */
		if (isBond() && getMaturity() == null) {
			/* Record a date for maturity */
			setMaturity(theLatest.getDate());
		}
	}
	
	/**
	 * Close the account
	 */
	public void closeAccount() {
		/* Close the account */
		setClose(theLatest.getDate());
	}
	
	/**
	 * Re-open the account
	 */
	public void reOpenAccount() {
		/* Reopen the account */
		setClose(null);
	}
	
	/**
	 * Set a new description 
	 * @param pDesc the description 
	 */
	public void setDescription(String pDesc) throws Exception {
		if (pDesc != null) getObj().setDesc(new StringPair(pDesc));
		else 			   getObj().setDesc(null);
	}
	
	/**
	 * Set a new maturity date 
	 * @param pDate the new date 
	 */
	public void setMaturity(Date pDate) {
		getObj().setMaturity((pDate == null) ? null : new Date(pDate));
	}
	
	/**
	 * Set a new close date 
	 * @param pDate the new date 
	 */
	public void setClose(Date pDate) {
		getObj().setClose((pDate == null) ? null : new Date(pDate));
	}
	
	/**
	 * Set a new parent 
	 * @param pParent the new parent 
	 */
	public void setParent(Account pParent) {
		getObj().setParent(pParent);
	}
	
	/**
	 * Set a new alias 
	 * @param pAlias the new alias 
	 */
	public void setAlias(Account pAlias) {
		getObj().setAlias(pAlias);
	}
	
	/**
	 * Set a new account name 
	 * @param pName the new name 
	 */
	public void setAccountName(String pName) throws Exception {
		if (pName != null) getObj().setName(new StringPair(pName));
		else 			   getObj().setName(null);
	}
	
	/**
	 * Set a new account type
	 * @param pType the new type 
	 */
	public void setActType(AccountType pType) {
		getObj().setType(pType);
		theOrder    = pType.getOrder();
	}
	
	/**
	 * Set a new web site
	 * @param pWebSite the new site 
	 */
	public void setWebSite(char[] pWebSite) throws Exception {
		if (pWebSite != null) getObj().setWebSite(new CharArrayPair(pWebSite));
		else 				  getObj().setWebSite(null);
	}
	
	/**
	 * Set a new customer number
	 * @param pCustNo the new number 
	 */
	public void setCustNo(char[] pCustNo) throws Exception {
		if (pCustNo != null) getObj().setCustNo(new CharArrayPair(pCustNo));
		else 				 getObj().setCustNo(null);
	}
	
	/**
	 * Set a new UserId
	 * @param pUserId the new id 
	 */
	public void setUserId(char[] pUserId) throws Exception {
		if (pUserId != null) getObj().setUserId(new CharArrayPair(pUserId));
		else 				 getObj().setUserId(null);
	}
	
	/**
	 * Set a new password
	 * @param pPassword the new password 
	 */
	public void setPassword(char[] pPassword) throws Exception {
		if (pPassword != null) getObj().setPassword(new CharArrayPair(pPassword));
		else 				   getObj().setPassword(null);
	}
	
	/**
	 * Set a new account
	 * @param pAccount the new account 
	 */
	public void setAccount(char[] pAccount) throws Exception {
		if (pAccount != null) getObj().setAccount(new CharArrayPair(pAccount));
		else 				  getObj().setAccount(null);
	}
	
	/**
	 * Set a new notes
	 * @param pNotes the new notes 
	 */
	public void setNotes(char[] pNotes) throws Exception {
		if (pNotes != null) getObj().setNotes(new CharArrayPair(pNotes));
		else 				getObj().setNotes(null);
	}
	
	/**
	 * Update base account from an edited account 
	 * @param pAccount the edited account 
	 * @return whether changes have been made
	 */
	public boolean applyChanges(DataItem<?> pAccount) {
		Account myAccount 	= (Account)pAccount;
		Values	myObj		= getObj();
		Values	myNew		= myAccount.getObj();
		boolean bChanged	= false;
		
		/* Store the current detail into history */
		pushHistory();
		
		/* Update the Name if required */
		if (differs(myObj.getName(), myNew.getName()))  
			myObj.setName(myNew.getName());
			
		/* Update the description if required */
		if (differs(myObj.getDesc(), myNew.getDesc())) 
			myObj.setDesc(myNew.getDesc());
			
		/* Update the account type if required */
		if (AccountType.differs(getActType(), myAccount.getActType())) 
			setActType(myAccount.getActType());
			
		/* Update the maturity if required */
		if (Date.differs(getMaturity(), myAccount.getMaturity())) 
			setMaturity(myAccount.getMaturity());
		
		/* Update the close if required */
		if (Date.differs(getClose(), myAccount.getClose())) 
			setClose(myAccount.getClose());
		
		/* Update the parent if required */
		if (Account.differs(getParent(), myAccount.getParent())) 
			setParent(myAccount.getParent());
		
		/* Update the alias if required */
		if (Account.differs(getAlias(), myAccount.getAlias())) 
			setAlias(myAccount.getAlias());
		
		/* Update the WebSite if required */
		if (differs(myObj.getWebSite(), myNew.getWebSite())) 
			myObj.setWebSite(myNew.getWebSite());
		
		/* Update the customer number if required */
		if (differs(myObj.getCustNo(), myNew.getCustNo())) 
			myObj.setCustNo(myNew.getCustNo());
		
		/* Update the UserId if required */
		if (differs(myObj.getUserId(), myNew.getUserId())) 
			myObj.setUserId(myNew.getUserId());
		
		/* Update the Password if required */
		if (differs(myObj.getPassword(), myNew.getPassword())) 
			myObj.setPassword(myNew.getPassword());
		
		/* Update the account if required */
		if (differs(myObj.getAccount(), myNew.getAccount())) 
			myObj.setAccount(myNew.getAccount());
		
		/* Update the notes if required */
		if (differs(myObj.getNotes(), myNew.getNotes())) 
			myObj.setNotes(myNew.getNotes());
		
		/* Check for changes */
		if (checkForHistory()) {
			/* Set changed status */
			setState(DataState.CHANGED);
			bChanged = true;
		}
		
		/* Return to caller */
		return bChanged;
	}

	/**
	 * Format an Account 
	 * @param pAccount the account to format
	 * @return the formatted account
	 */
	public static String format(Account pAccount) {
		String 	myFormat;
		myFormat = (pAccount != null) ? pAccount.getName()
							 	      : "null";
		return myFormat;
	}

	/**
	 * Determine whether two {@link account} objects differ.
	 * @param pCurr The current Account 
	 * @param pNew The new Account
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(Account pCurr, Account pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}
	
	/**
	 * AccountList class
	 */
	public static class List  extends EncryptedList<Account> {			
		/** 
	 	 * Construct an empty CORE account list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(DataSet pData) { 
			super(Account.class, pData);
		}

		/** 
	 	 * Construct a generic account list
	 	 * @param pData the DataSet for the list
	 	 * @param pStyle the style of the list 
	 	 */
		public List(DataSet pData, ListStyle pStyle) { 
			super(Account.class, pData, pStyle);
		}

		/** 
	 	 * Construct a generic account list
	 	 * @param pList the source account list 
	 	 * @param pStyle the style of the list 
	 	 */
		public List(List pList, ListStyle pStyle) { 
			super(Account.class, pList, pStyle);
		}

		/** 
	 	 * Construct a difference account list
	 	 * @param pNew the new Account list 
	 	 * @param pOld the old Account list 
	 	 */
		protected List(List pNew, List pOld) { 
			super(pNew, pOld);
		}
		
		/** 
	 	 * Clone an Account list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return new List(this, ListStyle.DIFFER); }
		
		/**
		 * Add a new item to the list
		 * @param pAccount item
		 * @return the newly added item
		 */
		public Account addNewItem(DataItem<?> pAccount) {
			Account myAccount = new Account(this, (Account)pAccount);
			add(myAccount);
			return myAccount;
		}
	
		/**
		 * Create a new empty element in the edit list (null-operation)
		 * @param isCredit - is the item a credit or debit
		 * @return the newly added item
		 */
		public Account addNewItem(boolean isCredit) { return null; }
			
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }
				
		/**
		 * Reset the account flags after changes to events
		 */
		public void reset() {
			ListIterator 	myIterator;
			Account 		myCurr;
			
			/* Access the iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				/* Clear the flags */
				myCurr.reset();
			}
		}
		
		/**
		 * Update account details after data update
		 */
		public void markActiveAccounts() throws Exception {
			ListIterator 	myIterator;
			Account 		myCurr;
					
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the accounts */
			while ((myCurr = myIterator.next()) != null) {				
				/* If we have a parent, mark the parent */
				if (myCurr.getParent() != null) {
					myCurr.getParent().touchParent();
					if (!myCurr.isClosed())
						myCurr.getParent().setNonCloseable();
				}
				
				/* If we have an alias, mark the alias */
				if (myCurr.getAlias() != null) {
					myCurr.getAlias().touchAlias();
					if (!myCurr.isClosed())
						myCurr.getAlias().setNonCloseable();
				}
				
				/* If we are a child and have no latest event, then we are not close-able */
				if ((myCurr.isChild()) && (myCurr.getLatest() == null)) {
					myCurr.setNonCloseable();
				}
				
				/* If we have patterns or are touched by patterns, then we are not close-able */
				if (myCurr.hasPatterns || myCurr.isPatterned) {
					myCurr.setNonCloseable();
				}
				
				/* If we have a close date and a latest event */
				if ((myCurr.getClose() != null) &&
					(myCurr.getLatest() != null)) {
					/* Check whether we need to adjust the date */
					myCurr.adjustClosed();
				}
			}	
			
			/* If we are in final loading stage */
			if (getData().getLoadState() == LoadState.FINAL) {
				/* Access a new iterator */
				myIterator = listIterator();
			
				/* Loop through the accounts */
				while ((myCurr = myIterator.next()) != null) {
					/* Validate the account */
					myCurr.validate();
					if (myCurr.hasErrors()) 
						throw new Exception(ExceptionClass.VALIDATE,
											myCurr,
											"Failed validation");
				}
			}
		}

		/**
		 * Count the instances of a string
		 * @param pName the string to check for
		 * @return The Item if present (or null)
		 */
		protected int countInstances(String pName) {
			ListIterator 	myIterator;
			Account 		myCurr;
			int     		iDiff;
			int     		iCount = 0;
			
			/* Access the iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				iDiff = pName.compareTo(myCurr.getName());
				if (iDiff == 0) iCount++;
			}
			
			/* Return to caller */
			return iCount;
		}	
		
		/**
		 * Search for a particular item by Name
		 * @param sName Name of item
		 * @return The Item if present (or null)
		 */
		public Account searchFor(String sName) {
			ListIterator 	myIterator;
			Account 		myCurr;
			int     		iDiff;
			
			/* Access the iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				iDiff = sName.compareTo(myCurr.getName());
				if (iDiff == 0) break;
			}
			
			/* Return to caller */
			return myCurr;
		}
		
		/**
		 * Get the market account from the list
		 * @return the Market account
		 */
		public Account getMarket() {
			ListIterator 	myIterator;
			Account 		myCurr;
			
			/* Access the iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				if (myCurr.isMarket()) break;
			}
			
			/* Return */
			return myCurr;
		}
		
		/**
		 * Get the TaxMan account from the list
		 * @return the TaxMan account
		 */
		public Account getTaxMan() {
			ListIterator 	myIterator;
			Account 		myCurr;
			
			/* Access the iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				if (myCurr.isTaxMan()) break;
			}
			
			/* Return */
			return myCurr;
		}
		
		/**
		 * Add an Account
		 * @param pAccount the Name of the account 
		 * @param pAcType the Name of the account type
		 * @param pMaturity the Maturity date for a bond (or null)
		 * @param pClosed the Close Date for the account (or null)
		 * @param pParent the Name of the parent account (or null)
		 * @param pAlias the Name of the alias account (or null)
		 * @throws Exception on error
		 */ 
		public void addItem(String   		pName,
				            String   		pAcType,
				            String			pDesc,
				            java.util.Date  pMaturity,
				            java.util.Date  pClosed,
				            String   		pParent,
				            String   		pAlias,
					        char[]			pWebSite,
					        char[]			pCustNo,
					        char[]			pUserId,
					        char[]			pPassword,
					        char[]			pAccount,
					        char[]			pNotes) throws Exception {
			AccountType.List 	myActTypes;
			AccountType 		myActType;
			Account       		myAccount;			
			Account		 		myParent;
			Account		 		myAlias;
			Integer				myParentId = null;
			Integer				myAliasId  = null;
				
			/* Access the account types and accounts */
			myActTypes = getData().getAccountTypes();
				
			/* Look up the Account Type */
			myActType = myActTypes.searchFor(pAcType);
			if (myActType == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Account [" + pName + 
			                        "] has invalid Account Type [" + 
			                        pAcType + "]");
			
			/* If we have a parent */
			if (pParent != null) {
				/* Look up the Parent */
				myParent = searchFor(pParent);
				if (myParent == null) 
					throw new Exception(ExceptionClass.DATA,
			                            "Account [" + pName + 
			                            "] has invalid Parent [" + 
			                            pParent + "]");
				myParentId = myParent.getId();
			}
			
			/* If we have a parent */
			if (pAlias != null) {
				/* Look up the Parent */
				myAlias = searchFor(pAlias);
				if (myAlias == null) 
					throw new Exception(ExceptionClass.DATA,
			                            "Account [" + pName + 
			                            "] has invalid Alias [" + 
			                            pAlias + "]");
				myAliasId = myAlias.getId();
			}
			
			/* Create the new account */
			myAccount = new Account(this,
					                pName, 
					                myActType.getId(),
					                pDesc,
					                pMaturity,
					                pClosed,
					                myParentId,
					                myAliasId,
							        pWebSite,
							        pCustNo,
							        pUserId,
							        pPassword,
							        pAccount,
							        pNotes);
				
			/* Check that this Account has not been previously added */
			if (searchFor(myAccount.getName()) != null) 
				throw new Exception(ExceptionClass.DATA,
						  			myAccount,
			                        "Duplicate Account");			
			
			/* Add the Account to the list */
			add(myAccount);				
		}
			
		/**
		 * Add an Account
		 * @param uId the Id of the account
		 * @param pAccount the Encrypted Name of the account 
		 * @param uAcTypeId the Id of the account type
		 * @param pDesc the Encrypted Description of the account (or null)
		 * @param pMaturity the Maturity date for a bond (or null)
		 * @param pClosed the Close Date for the account (or null)
		 * @param uParentId the Id of the parent account (or -1)
		 * @param uAliasId the Id of the alias account (or -1)
		 * @param pWebSite the Encrypted WebSite of the account
		 * @param pCustNo the Encrypted CustomerId of the account
		 * @param pUserId the Encrypted UserId of the account
		 * @param pPassword the Encrypted Password of the account
		 * @param pAccount the Encrypted Account details of the account
		 * @param pNotes the Encrypted Notes for the account
		 * @throws Exception on error
		 */ 
		public void addItem(int     		uId,
							int				uControlId,
				            byte[]   		pName,
				            int     		uAcTypeId,
				            byte[]   		pDesc,
				            java.util.Date  pMaturity,
				            java.util.Date  pClosed,
				            Integer    		pParentId,
				            Integer 		pAliasId,
					        byte[]			pWebSite,
					        byte[]			pCustNo,
					        byte[]			pUserId,
					        byte[]			pPassword,
					        byte[]			pAccount,
					        byte[]			pNotes) throws Exception {
			Account       myAccount;
				
			/* Create the new account */
			myAccount = new Account(this,
					                uId, 
					                uControlId,
					                pName, 
					                uAcTypeId,
					                pDesc,
					                pMaturity,
					                pClosed,
					                pParentId,
					                pAliasId,
							        pWebSite,
							        pCustNo,
							        pUserId,
							        pPassword,
							        pAccount,
							        pNotes);
				
			/* Check that this AccountId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
									myAccount,
			  			            "Duplicate AccountId");
				 
			/* Check that this Account has not been previously added */
			if (searchFor(myAccount.getName()) != null) 
				throw new Exception(ExceptionClass.DATA,
						  			myAccount,
			                        "Duplicate Account");
			
			/* Add the Account to the list */
			add(myAccount);				
		}
		
		/**
		 * Validate newly loaded accounts. This is deliberately deferred until after loading
		 * of the Rates/Patterns/Prices so as to validate the interrelationships
		 */
		public void validateLoadedAccounts() throws Exception {
			ListIterator myIterator;
			Account      myCurr;
			DataSet		 myData = getData();
		
			/* Mark active rates */
			myData.getRates().markActiveRates();
			
			/* Mark active prices */
			myData.getPrices().markActivePrices();
			
			/* Mark active patterns */
			myData.getPatterns().markActivePatterns();
			
			/* Access the iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				/* If the account has a parent Id */
				if (myCurr.getParentId() != null) {
					/* Set the parent */
					myCurr.setParent(searchFor(myCurr.getParentId()));
					myCurr.getParent().touchParent();
				}
					
				/* If the account has an alias Id */
				if (myCurr.getAliasId() != null) {
					/* Set the alias */
					myCurr.setAlias(searchFor(myCurr.getAliasId()));
					myCurr.getAlias().touchAlias();
				}
			}

			/* Create another iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				/* Validate the account */
				myCurr.validate();
					
				/* Handle validation failure */
				if (myCurr.hasErrors()) 
					throw new Exception(ExceptionClass.VALIDATE,
										myCurr,
										"Failed validation");				
			}
		}
	}
		
	/**
	 * Values for account
	 */
	public class Values extends EncryptedValues {
		private StringPair  	theName     	= null;
		private StringPair  	theDesc     	= null;
		private AccountType		theType			= null;
		private Date       		theMaturity 	= null;
		private Date       		theClose    	= null;
		private Account			theParent		= null;
		private Account			theAlias		= null;
		private CharArrayPair	theWebSite		= null;
		private CharArrayPair	theCustNo		= null;
		private CharArrayPair	theUserId		= null;
		private CharArrayPair	thePassword		= null;
		private CharArrayPair	theAccount		= null;
		private CharArrayPair	theNotes		= null;
		
		/* Access methods */
		public StringPair   	getName()      	{ return theName; }
		public StringPair   	getDesc()      	{ return theDesc; }
		public AccountType		getType()      	{ return theType; }
		public Date       		getMaturity()  	{ return theMaturity; }
		public Date       		getClose()     	{ return theClose; }
		public Account			getParent()    	{ return theParent; }
		public Account			getAlias()    	{ return theAlias; }
		public CharArrayPair	getWebSite()	{ return theWebSite; }
		public CharArrayPair	getCustNo()		{ return theCustNo; }
		public CharArrayPair	getUserId()		{ return theUserId; }
		public CharArrayPair	getPassword()	{ return thePassword; }
		public CharArrayPair	getAccount()	{ return theAccount; }
		public CharArrayPair	getNotes()		{ return theNotes; }

		/* Encrypted value access */
		public  String	getNameValue()      { return getPairValue(getName()); }
		public  String  getDescValue()      { return getPairValue(getDesc()); }
		public  char[]  getWebSiteValue()	{ return getPairValue(getWebSite()); }
		public  char[]  getCustNoValue()	{ return getPairValue(getCustNo()); }
		public  char[]  getUserIdValue()	{ return getPairValue(getUserId()); }
		public  char[]  getPasswordValue()	{ return getPairValue(getPassword()); }
		public  char[]  getAccountValue()	{ return getPairValue(getAccount()); }
		public  char[]  getNotesValue()		{ return getPairValue(getNotes()); }
		
		/* Encrypted bytes access */
		public  byte[]	getNameBytes()      { return getPairBytes(getName()); }
		public  byte[]  getDescBytes()      { return getPairBytes(getDesc()); }
		public  byte[]  getWebSiteBytes()	{ return getPairBytes(getWebSite()); }
		public  byte[]  getCustNoBytes()	{ return getPairBytes(getCustNo()); }
		public  byte[]  getUserIdBytes()	{ return getPairBytes(getUserId()); }
		public  byte[]  getPasswordBytes()	{ return getPairBytes(getPassword()); }
		public  byte[]  getAccountBytes()	{ return getPairBytes(getAccount()); }
		public  byte[]  getNotesBytes()		{ return getPairBytes(getNotes()); }
		
		public void setName(StringPair pName) {
			theName      = pName; }
		public void setDesc(StringPair pDesc) {
			theDesc      = pDesc; }
		public void setType(AccountType pType) {
			theType      = pType; }
		public void setMaturity(Date pMaturity) {
			theMaturity  = pMaturity; }
		public void setClose(Date pClose) {
			theClose     = pClose; }
		public void setParent(Account pParent) {
			theParent    = pParent; }
		public void setAlias(Account pAlias) {
			theAlias     = pAlias; }
		public void setWebSite(CharArrayPair pWebSite) {
			theWebSite		= pWebSite; }
		public void setCustNo(CharArrayPair pCustNo) {
			theCustNo		= pCustNo; }
		public void setUserId(CharArrayPair pUserId) {
			theUserId		= pUserId; }
		public void setPassword(CharArrayPair pPassword) {
			thePassword		= pPassword; }
		public void setAccount(CharArrayPair pAccount) {
			theAccount		= pAccount; }
		public void setNotes(CharArrayPair pNotes) {
			theUserId		= pNotes; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) {
			theName       = pValues.getName();
			theDesc       = pValues.getDesc();
			theType       = pValues.getType();
			theMaturity   = pValues.getMaturity();
			theClose      = pValues.getClose();
			theParent     = pValues.getParent();
			theAlias      = pValues.getAlias();
			theWebSite 	  = pValues.getWebSite();
			theCustNo 	  = pValues.getCustNo();
			theUserId 	  = pValues.getUserId();
			thePassword	  = pValues.getPassword();
			theAccount	  = pValues.getAccount();
			theNotes	  = pValues.getNotes();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (differs(theName,			pValues.theName))     	return false;
			if (differs(theDesc,			pValues.theDesc))     	return false;
			if (AccountType.differs(theType,pValues.theType))     	return false;
			if (Date.differs(theMaturity, 	pValues.theMaturity)) 	return false;
			if (Date.differs(theClose,    	pValues.theClose))    	return false;
			if (Account.differs(theParent,  pValues.theParent))   	return false;
			if (Account.differs(theAlias,   pValues.theAlias))   	return false;
			if (differs(theWebSite,			pValues.theWebSite)) 	return false;
			if (differs(theCustNo,			pValues.theCustNo)) 	return false;
			if (differs(theUserId,			pValues.theUserId)) 	return false;
			if (differs(thePassword,		pValues.thePassword)) 	return false;
			if (differs(theAccount,			pValues.theAccount)) 	return false;
			if (differs(theNotes,			pValues.theNotes)) 		return false;
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
			theName       = pValues.getName();
			theDesc       = pValues.getDesc();
			theType       = pValues.getType();
			theMaturity   = pValues.getMaturity();
			theClose      = pValues.getClose();
			theParent     = pValues.getParent();
			theAlias      = pValues.getAlias();
			theWebSite 	  = pValues.getWebSite();
			theCustNo 	  = pValues.getCustNo();
			theUserId 	  = pValues.getUserId();
			thePassword	  = pValues.getPassword();
			theAccount	  = pValues.getAccount();
			theNotes	  = pValues.getNotes();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean			bResult = false;
			switch (fieldNo) {
				case FIELD_NAME:
					bResult = (differs(theName,   			pValues.theName));
					break;
				case FIELD_DESC:
					bResult = (differs(theDesc,   			pValues.theDesc));
					break;
				case FIELD_TYPE:
					bResult = (AccountType.differs(theType, pValues.theType));
					break;
				case FIELD_MATURITY:
					bResult = (Date.differs(theMaturity, 	pValues.theMaturity));
					break;
				case FIELD_CLOSE:
					bResult = (Date.differs(theClose,    	pValues.theClose));
					break;
				case FIELD_PARENT:
					bResult = (Account.differs(theParent,   pValues.theParent));
					break;
				case FIELD_ALIAS:
					bResult = (Account.differs(theAlias,   	pValues.theAlias));
					break;
				case FIELD_WEBSITE:
					bResult = (differs(theWebSite,			pValues.theWebSite));
					break;
				case FIELD_CUSTNO:
					bResult = (differs(theCustNo,			pValues.theCustNo));
					break;
				case FIELD_USERID:
					bResult = (differs(theUserId,			pValues.theUserId));
					break;
				case FIELD_PASSWORD:
					bResult = (differs(thePassword,			pValues.thePassword));
					break;
				case FIELD_ACCOUNT:
					bResult = (differs(theAccount,			pValues.theAccount));
					break;
				case FIELD_NOTES:
					bResult = (differs(theNotes,			pValues.theNotes));
					break;
			}
			return bResult;
		}

		/**
		 * Ensure encryption after security change
		 */
		protected void applySecurity() throws Exception {
			/* Apply the encryption */
			theName.encryptPair();
			if (theDesc     != null) theDesc.encryptPair();
			if (theWebSite  != null) theWebSite.encryptPair();
			if (theCustNo   != null) theCustNo.encryptPair();
			if (theUserId   != null) theUserId.encryptPair();
			if (thePassword != null) thePassword.encryptPair();
			if (theAccount  != null) theAccount.encryptPair();
			if (theNotes    != null) theNotes.encryptPair();
		}		
	}
}
