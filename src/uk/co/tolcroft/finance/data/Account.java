package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.finance.data.FinanceData.LoadState;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.data.ControlKey;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.EncryptedItem;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.help.DebugDetail;

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
	public  Values      getValues()     { return (Values)super.getValues(); }	
	public  String      getName()      	{ return getPairValue(getValues().getName()); }
	public  String      getDesc()      	{ return getPairValue(getValues().getDesc()); }
	public  Account     getParent()    	{ return getValues().getParent(); }
	public  Integer		getParentId()  	{ return getValues().getParentId(); }
	public  Account     getAlias()    	{ return getValues().getAlias(); }
	public  Integer     getAliasId()  	{ return getValues().getAliasId(); }
	public  Event       getEarliest()  	{ return theEarliest; }
	public  Event       getLatest()    	{ return theLatest; }
	public  AcctPrice   getInitPrice()  { return theInitPrice; }
	public  AccountType getActType()   	{ return getValues().getType(); }
	public  int         getOrder()     	{ return getValues().getOrder(); }
	public  Date        getMaturity()  	{ return getValues().getMaturity(); }
	public  Date    	getClose()     	{ return getValues().getClose(); }
	public  char[]    	getWebSite()	{ return getPairValue(getValues().getWebSite()); }
	public  char[]    	getCustNo()		{ return getPairValue(getValues().getCustNo()); }
	public  char[]    	getUserId()		{ return getPairValue(getValues().getUserId()); }
	public  char[]    	getPassword()	{ return getPairValue(getValues().getPassword()); }
	public  char[]    	getAccount()	{ return getPairValue(getValues().getAccount()); }
	public  char[]    	getNotes()		{ return getPairValue(getValues().getNotes()); }
	public  boolean     isCloseable()  	{ return isCloseable; }
	public  boolean     isParent()  	{ return isParent; }
	public  boolean     isClosed()     	{ return (getClose() != null); }
	public  boolean     isAlias()     	{ return (getAliasId() != null); }
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
	public  byte[]	getNameBytes()      { return getPairBytes(getValues().getName()); }
	public  byte[]  getDescBytes()      { return getPairBytes(getValues().getDesc()); }
	public  byte[]  getWebSiteBytes()	{ return getPairBytes(getValues().getWebSite()); }
	public  byte[]  getCustNoBytes()	{ return getPairBytes(getValues().getCustNo()); }
	public  byte[]  getUserIdBytes()	{ return getPairBytes(getValues().getUserId()); }
	public  byte[]  getPasswordBytes()	{ return getPairBytes(getValues().getPassword()); }
	public  byte[]  getAccountBytes()	{ return getPairBytes(getValues().getAccount()); }
	public  byte[]  getNotesBytes()		{ return getPairBytes(getValues().getNotes()); }
	
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
	public static final int FIELD_EVTFIRST = EncryptedItem.NUMFIELDS+13;
	public static final int FIELD_EVTLAST  = EncryptedItem.NUMFIELDS+14;
	public static final int FIELD_INITPRC  = EncryptedItem.NUMFIELDS+15;
	public static final int FIELD_HASRATES = EncryptedItem.NUMFIELDS+16;
	public static final int FIELD_HASPRICE = EncryptedItem.NUMFIELDS+17;
	public static final int FIELD_HASPATT  = EncryptedItem.NUMFIELDS+18;
	public static final int FIELD_ISPATT   = EncryptedItem.NUMFIELDS+19;
	public static final int FIELD_ISPARENT = EncryptedItem.NUMFIELDS+20;
	public static final int FIELD_ISALIASD = EncryptedItem.NUMFIELDS+21;
	public static final int FIELD_ISCLSABL = EncryptedItem.NUMFIELDS+22;
	public static final int NUMFIELDS	   = EncryptedItem.NUMFIELDS+23;
	
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
			case FIELD_EVTFIRST:	return "FirstEvent";
			case FIELD_EVTLAST:		return "LastEvent";
			case FIELD_INITPRC:		return "InitialPrice";
			case FIELD_HASRATES:	return "HasRates";
			case FIELD_HASPRICE:	return "HasPrices";
			case FIELD_HASPATT:		return "HasPatterns";
			case FIELD_ISPATT:		return "IsPatterned";
			case FIELD_ISCLSABL:	return "IsCloseable";
			case FIELD_ISPARENT:	return "IsParent";
			case FIELD_ISALIASD:	return "IsAliased";
			default:		  		return EncryptedItem.fieldName(iField);
		}
	}
	
	/**
	 * Determine the field name in a non-static fashion 
	 */
	public String getFieldName(int iField) { return fieldName(iField); }
	
	/**
	 * Format the value of a particular field as a table row
	 * @param pDetail the debug detail
	 * @param iField the field number
	 * @param pValues the values to use
	 * @return the formatted field
	 */
	public String formatField(DebugDetail pDetail, int iField, HistoryValues<Account> pValues) {
		String	myString = "";
		Values 	myValues = (Values)pValues;
		switch (iField) {
			case FIELD_NAME:	
				myString += myValues.getNameValue(); 
				break;
			case FIELD_DESC:	
				myString += myValues.getDescValue(); 
				break;
			case FIELD_TYPE:	
				if ((myValues.getType() == null) &&
					(myValues.getActTypeId() != null))
					myString += "Id=" + myValues.getActTypeId();
				else
					myString += AccountType.format(getActType()); 
				myString = pDetail.addDebugLink(myValues.getType(), myString);
				break;
			case FIELD_CLOSE:	
				myString += Date.format(myValues.getClose()); 
				break;
			case FIELD_MATURITY:	
				myString += Date.format(myValues.getMaturity()); 
				break;
			case FIELD_PARENT:	
				if ((myValues.getParent() == null) &&
					(myValues.getParentId() != null))
					myString += "Id=" + myValues.getParentId();
				else
					myString += Account.format(myValues.getParent()); 
				myString = pDetail.addDebugLink(myValues.getParent(), myString);
				break;
			case FIELD_ALIAS:	
				if ((myValues.getAlias() == null) &&
					(myValues.getAliasId() != null))
					myString += "Id=" + myValues.getAliasId();
				else
					myString += Account.format(myValues.getAlias()); 
				myString = pDetail.addDebugLink(myValues.getAlias(), myString);
				break;
			case FIELD_WEBSITE:	
				myString += getCharArrayPairString(myValues.getWebSite()); 
				break;
			case FIELD_CUSTNO:	
				myString += getCharArrayPairString(myValues.getCustNo()); 
				break;
			case FIELD_USERID:	
				myString += getCharArrayPairString(myValues.getUserId()); 
				break;
			case FIELD_PASSWORD:	
				myString += getCharArrayPairString(myValues.getPassword()); 
				break;
			case FIELD_ACCOUNT:	
				myString += getCharArrayPairString(myValues.getAccount()); 
				break;
			case FIELD_NOTES:	
				myString += getCharArrayPairString(myValues.getNotes()); 
				break;
			case FIELD_EVTFIRST:	
				myString += null;
				if (theEarliest != null) 
					myString = pDetail.addDebugLink(theEarliest, Date.format(theEarliest.getDate())); 
				break;
			case FIELD_EVTLAST:	
				myString += null;
				if (theLatest != null) 
					myString = pDetail.addDebugLink(theLatest, Date.format(theLatest.getDate())); 
				break;
			case FIELD_INITPRC:
				myString += null;
				if (theInitPrice != null) 
					myString = pDetail.addDebugLink(theInitPrice, Price.format(theInitPrice.getPrice())); 
				break;
			case FIELD_HASPATT:	
				myString += hasPatterns ? true : false; 
				break;
			case FIELD_HASRATES:	
				myString += hasRates ? true : false; 
				break;
			case FIELD_HASPRICE:	
				myString += hasPrices ? true : false; 
				break;
			case FIELD_ISPATT:	
				myString += isPatterned ? true : false; 
				break;
			case FIELD_ISCLSABL:	
				myString += isCloseable ? true : false; 
				break;
			case FIELD_ISALIASD:	
				myString += isAliasedTo ? true : false; 
				break;
			case FIELD_ISPARENT:	
				myString += isParent ? true : false; 
				break;
			default: 		
				myString += super.formatField(pDetail, iField, pValues); 
				break;
		}
		return myString;
	}
							
	/**
	 * Get an initial set of values 
	 * @return an initial set of values 
	 */
	protected HistoryValues<Account> getNewValues() { return new Values(); }
	
	/**
	 * Copy flags 
	 * @param pItem the original item 
	 */
	protected void copyFlags(Account pItem) {
		/* Copy Main flags */
		super.copyFlags(pItem);
		
		/* Copy Remaining flags */
		theEarliest  = pItem.theEarliest;
		theLatest    = pItem.theLatest;
		theInitPrice = pItem.theInitPrice;
		isCloseable  = pItem.isCloseable();
		isAliasedTo  = pItem.isAliasedTo();
		isParent     = pItem.isParent();
		isPatterned  = pItem.isPatterned;
		hasPatterns  = pItem.hasPatterns;
		hasRates     = pItem.hasRates;
		hasPrices    = pItem.hasPrices;
	}
	
	/**
	 * Construct a copy of an Account
	 * @param pAccount The Account to copy 
	 */
	public Account(List pList, Account pAccount) {
		/* Set standard values */
		super(pList, pAccount.getId());
		Values myValues = getValues();
		myValues.copyFrom(pAccount.getValues());
		ListStyle myOldStyle = pAccount.getList().getStyle();

		/* Switch on the ListStyle */
		switch (pList.getStyle()) {
			case EDIT:
				/* If this is a view creation */
				if (myOldStyle == ListStyle.CORE) {
					/* Account is based on the original element */
					setBase(pAccount);
					copyFlags(pAccount);
					pList.setNewId(this);				
					break;
				}
				
				/* Else this is a duplication so treat as new item */
				setId(0);
				pList.setNewId(this);				
				break;
			case CLONE:
				reBuildLinks(pList, pList.getData());
			case COPY:
			case CORE:
				/* Reset Id if this is an insert from a view */
				if (myOldStyle == ListStyle.EDIT) setId(0);
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
		Values myValues	= getValues();
		
		/* Store the IDs */
		myValues.setActTypeId(uAcTypeId);
		myValues.setParentId(pParentId);
		myValues.setAliasId(pAliasId);
		
		/* Set ControlId */
		setControlKey(uControlId);
		
		/* Look up the Account Type */
		FinanceData	myData 	= pList.getData();
		myActType = myData.getAccountTypes().searchFor(uAcTypeId);
		if (myActType == null) 
			throw new Exception(ExceptionClass.DATA,
		                        this,
					            "Invalid Account Type Id");
		myValues.setType(myActType);

		/* Parse the maturity date if it exists */
		if (pMaturity != null) 
			myValues.setMaturity(new Date(pMaturity));
			
		/* Parse the closed date if it exists */
		if (pClose != null) 
			myValues.setClose(new Date(pClose));
				
		/* Record the encrypted values */
		myValues.setName(new StringPair(pName));
		myValues.setDesc((pDesc == null) ? null : new StringPair(pDesc));
		myValues.setWebSite((pWebSite == null) ? null : new CharArrayPair(pWebSite));
		myValues.setCustNo((pCustNo == null) ? null : new CharArrayPair(pCustNo));
		myValues.setUserId((pUserId == null) ? null : new CharArrayPair(pUserId));
		myValues.setPassword((pPassword == null) ? null : new CharArrayPair(pPassword));
		myValues.setAccount((pAccount == null) ? null : new CharArrayPair(pAccount));
		myValues.setNotes((pNotes == null) ? null : new CharArrayPair(pNotes));
		
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
					int				uId,
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
		super(pList, uId);
		
		/* Local Variable */
		AccountType myActType;
		
		/* Initialise the object */
		Values myValues	= getValues();

		/* Record the encrypted values */
		myValues.setName(new StringPair(sName));
		myValues.setDesc((pDesc == null) ? null : new StringPair(pDesc));
		myValues.setWebSite((pWebSite == null) ? null : new CharArrayPair(pWebSite));
		myValues.setCustNo((pCustNo == null) ? null : new CharArrayPair(pCustNo));
		myValues.setUserId((pUserId == null) ? null : new CharArrayPair(pUserId));
		myValues.setPassword((pPassword == null) ? null : new CharArrayPair(pPassword));
		myValues.setAccount((pAccount == null) ? null : new CharArrayPair(pAccount));
		myValues.setNotes((pNotes == null) ? null : new CharArrayPair(pNotes));
		
		/* Store the IDs */
		myValues.setActTypeId(uAcTypeId);
		myValues.setParentId(pParentId);
		myValues.setAliasId(pAliasId);
		
		/* Look up the Account Type */
		FinanceData	myData 	= pList.getData();
		myActType = myData.getAccountTypes().searchFor(uAcTypeId);
		if (myActType == null) 
			throw new Exception(ExceptionClass.DATA,
		                        this,
					            "Invalid Account Type Id");
		myValues.setType(myActType);

		/* Parse the maturity date if it exists */
		if (pMaturity != null) 
			myValues.setMaturity(new Date(pMaturity));
			
		/* Parse the closed date if it exists */
		if (pClose != null) 
			myValues.setClose(new Date(pClose));
				
		/* Allocate the id */
		pList.setNewId(this);				
	}
	
	/* Standard constructor for a newly inserted account */
	public Account(List pList) {
		super(pList, 0);
		setControlKey(pList.getControlKey());
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
		return getValues().histEquals(myThat.getValues()).isIdentical();
	}

	/**
	 * Compare this account to another to establish sort order. 
	 * @param pThat The Account to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		long result;

		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is an Account */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as an Account */
		Account myThat = (Account)pThat;
		
		/* If we are comparing owner with non-owner */
		if (isOwner() != myThat.isOwner()) {
			/* List owners first */
			if (isOwner()) 	return -1;
			else			return 1;
		}
			
		/* If we are comparing alias with non-alias */
		if (isAlias() != myThat.isAlias()) {
			/* List alias after non-alias */
			if (isAlias()) 	return 1;
			else			return -1;
		}
		
		/* If the order differs */
		if (getOrder() < myThat.getOrder()) return -1;
		if (getOrder() > myThat.getOrder()) return  1;
		
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
		
	/**
	 * Rebuild Links to partner data
	 * @param pData the DataSet
	 */
	protected void reBuildLinks(List pList, FinanceData pData) {
		/* Update the Encryption details */
		super.reBuildLinks(pData);
		
		/* Access Account types */
		AccountType.List myTypes = pData.getAccountTypes();
		
		/* Update to use the local copy of the AccountTypes */
		Values 		myValues   	= getValues();
		AccountType	myType		= myValues.getType();
		AccountType	myNewType 	= myTypes.searchFor(myType.getId());
		myValues.setType(myNewType);

		/* If we have a parent */
		Account			myAct	= getParent();
		if (myAct != null) {
			/* Update it */
			Account	myNewAct 	= pList.searchFor(myAct.getId());
			myValues.setParent(myNewAct);
		}
		
		/* If we have an alias */
		myAct	= getAlias();
		if (myAct != null) {
			/* Update it */
			Account	myNewAct 	= pList.searchFor(myAct.getId());
			myValues.setAlias(myNewAct);
		}
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
	public  boolean isOwner()   { return getActType().isOwner(); }
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
		FinanceData		mySet  = myList.getData();
		
		/* AccountType must be non-null */
		if (myType == null) 
			addError("AccountType must be non-null", FIELD_TYPE);
		else if (!myType.getEnabled()) 
			addError("AccountType must be enabled", FIELD_TYPE);
			
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
			if (!Account.differs(this, getAlias()).isDifferent())
				addError("Cannot alias to self", FIELD_ALIAS);

			/* Cannot alias to same type */
			else if (!AccountType.differs(myType, getAlias().getActType()).isDifferent())
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
		Event.List.ListIterator 	myIterator;
		int     						myResult;
		Money 							myAmount;
		Money 							myValue;
		List 							myList = (List)getList();
		FinanceData						mySet  = myList.getData();
		
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
	 * Clear the active account flags
	 */
	public void clearActive() {
		super.clearActive();

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
	 * Touch an account
	 */
	public void touchItem(DataItem<?> pObject) {
		/* Note that the account is Active */
		super.touchItem(pObject);
		
		/* If we are being touched by an event */
		if (pObject instanceof Event) {
			/* Access as event */
			Event myEvent = (Event)pObject;
			
			/* Note flags */
			/* Record the event */
			if (theEarliest == null) theEarliest = myEvent;
			theLatest = myEvent;
			
			/* If we have a parent, touch it */
			if (getParent() != null) 
				getParent().touchItem(pObject);
		}
		
		/* If we are being touched by a rate */
		if (pObject instanceof AcctRate) {
			/* Note flags */
			hasRates = true;
		}
		
		/* If we are being touched by a price */
		else if (pObject instanceof AcctPrice) {
			/* Note flags */
			hasPrices = true;
			if (theInitPrice == null) theInitPrice = (AcctPrice)pObject;
		}
		
		/* If we are being touched by a pattern */
		else if (pObject instanceof Pattern) {
			/* Access as pattern */
			Pattern myPattern = (Pattern)pObject;
			
			/* Note flags */
			if (differs(myPattern.getAccount(), this).isIdentical()) hasPatterns = true;
			if (differs(myPattern.getPartner(), this).isIdentical()) isPatterned = true;
		}

		/* If we are being touched by another account */
		else if (pObject instanceof Account) {
			/* Access as account */
			Account myAccount = (Account)pObject;
			
			/* Note flags */
			if (differs(myAccount.getParent(), this).isIdentical()) isParent 	= true;
			if (differs(myAccount.getAlias(), this).isIdentical())  isAliasedTo = true;
		}
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
		if (pDesc != null) getValues().setDesc(new StringPair(pDesc));
		else 			   getValues().setDesc(null);
	}
	
	/**
	 * Set a new maturity date 
	 * @param pDate the new date 
	 */
	public void setMaturity(Date pDate) {
		getValues().setMaturity((pDate == null) ? null : new Date(pDate));
	}
	
	/**
	 * Set a new close date 
	 * @param pDate the new date 
	 */
	public void setClose(Date pDate) {
		getValues().setClose((pDate == null) ? null : new Date(pDate));
	}
	
	/**
	 * Set a new parent 
	 * @param pParent the new parent 
	 */
	public void setParent(Account pParent) {
		getValues().setParent(pParent);
	}
	
	/**
	 * Set a new alias 
	 * @param pAlias the new alias 
	 */
	public void setAlias(Account pAlias) {
		getValues().setAlias(pAlias);
	}
	
	/**
	 * Set a new account name 
	 * @param pName the new name 
	 */
	public void setAccountName(String pName) throws Exception {
		if (pName != null) getValues().setName(new StringPair(pName));
		else 			   getValues().setName(null);
	}
	
	/**
	 * Set a new account type
	 * @param pType the new type 
	 */
	public void setActType(AccountType pType) {
		getValues().setType(pType);
	}
	
	/**
	 * Set a new web site
	 * @param pWebSite the new site 
	 */
	public void setWebSite(char[] pWebSite) throws Exception {
		if (pWebSite != null) getValues().setWebSite(new CharArrayPair(pWebSite));
		else 				  getValues().setWebSite(null);
	}
	
	/**
	 * Set a new customer number
	 * @param pCustNo the new number 
	 */
	public void setCustNo(char[] pCustNo) throws Exception {
		if (pCustNo != null) getValues().setCustNo(new CharArrayPair(pCustNo));
		else 				 getValues().setCustNo(null);
	}
	
	/**
	 * Set a new UserId
	 * @param pUserId the new id 
	 */
	public void setUserId(char[] pUserId) throws Exception {
		if (pUserId != null) getValues().setUserId(new CharArrayPair(pUserId));
		else 				 getValues().setUserId(null);
	}
	
	/**
	 * Set a new password
	 * @param pPassword the new password 
	 */
	public void setPassword(char[] pPassword) throws Exception {
		if (pPassword != null) getValues().setPassword(new CharArrayPair(pPassword));
		else 				   getValues().setPassword(null);
	}
	
	/**
	 * Set a new account
	 * @param pAccount the new account 
	 */
	public void setAccount(char[] pAccount) throws Exception {
		if (pAccount != null) getValues().setAccount(new CharArrayPair(pAccount));
		else 				  getValues().setAccount(null);
	}
	
	/**
	 * Set a new notes
	 * @param pNotes the new notes 
	 */
	public void setNotes(char[] pNotes) throws Exception {
		if (pNotes != null) getValues().setNotes(new CharArrayPair(pNotes));
		else 				getValues().setNotes(null);
	}
	
	/**
	 * Update base account from an edited account 
	 * @param pAccount the edited account 
	 * @return whether changes have been made
	 */
	public boolean applyChanges(DataItem<?> pAccount) {
		Account myAccount 	= (Account)pAccount;
		Values	myValues	= getValues();
		Values	myNew		= myAccount.getValues();
		boolean bChanged	= false;
		
		/* Store the current detail into history */
		pushHistory();
		
		/* Update the Name if required */
		if (differs(myValues.getName(), myNew.getName()).isDifferent())  
			myValues.setName(myNew.getName());
			
		/* Update the description if required */
		if (differs(myValues.getDesc(), myNew.getDesc()).isDifferent()) 
			myValues.setDesc(myNew.getDesc());
			
		/* Update the account type if required */
		if (AccountType.differs(getActType(), myAccount.getActType()).isDifferent()) 
			setActType(myAccount.getActType());
			
		/* Update the maturity if required */
		if (Date.differs(getMaturity(), myAccount.getMaturity()).isDifferent()) 
			setMaturity(myAccount.getMaturity());
		
		/* Update the close if required */
		if (Date.differs(getClose(), myAccount.getClose()).isDifferent()) 
			setClose(myAccount.getClose());
		
		/* Update the parent if required */
		if (Account.differs(getParent(), myAccount.getParent()).isDifferent()) 
			setParent(myAccount.getParent());
		
		/* Update the alias if required */
		if (Account.differs(getAlias(), myAccount.getAlias()).isDifferent()) 
			setAlias(myAccount.getAlias());
		
		/* Update the WebSite if required */
		if (differs(myValues.getWebSite(), myNew.getWebSite()).isDifferent()) 
			myValues.setWebSite(myNew.getWebSite());
		
		/* Update the customer number if required */
		if (differs(myValues.getCustNo(), myNew.getCustNo()).isDifferent()) 
			myValues.setCustNo(myNew.getCustNo());
		
		/* Update the UserId if required */
		if (differs(myValues.getUserId(), myNew.getUserId()).isDifferent()) 
			myValues.setUserId(myNew.getUserId());
		
		/* Update the Password if required */
		if (differs(myValues.getPassword(), myNew.getPassword()).isDifferent()) 
			myValues.setPassword(myNew.getPassword());
		
		/* Update the account if required */
		if (differs(myValues.getAccount(), myNew.getAccount()).isDifferent()) 
			myValues.setAccount(myNew.getAccount());
		
		/* Update the notes if required */
		if (differs(myValues.getNotes(), myNew.getNotes()).isDifferent()) 
			myValues.setNotes(myNew.getNotes());
		
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
	 * AccountList class
	 */
	public static class List  extends EncryptedList<List, Account> {
		/* Properties */
		private Account 	theAccount	= null;
		
		/* Access DataSet correctly */
		public FinanceData 	getData() 		{ return (FinanceData) super.getData(); }
		public Account		getAccount() 	{ return theAccount; }
		
		/** 
	 	 * Construct an empty CORE account list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(FinanceData pData) { 
			super(List.class, Account.class, pData);
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
		 * Construct a difference Account list
		 * @param pNew the new Account list 
		 * @param pOld the old Account list 
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
		 * Construct an edit extract for an Account.
		 * @return the edit Extract
		 */
		public List getEditList(Account pAccount) {
			/* Build an empty Extract List */
			List myList = new List(this);
			
			/* Set the correct style */
			myList.setStyle(ListStyle.EDIT);
			
			/* Create a new account based on the passed account */
			myList.theAccount = new Account(myList, pAccount);
			myList.add(myList.theAccount);
			
			/* Return the List */
			return myList;
		}
		
		/**
		 * Construct an edit extract for an Account.
		 * @return the edit Extract
		 */
		public List getEditList(AccountType pType) {
			/* Build an empty Extract List */
			List myList = new List(this);
			
			/* Set the correct style */
			myList.setStyle(ListStyle.EDIT);
			
			/* Create a new account */
			myList.theAccount = new Account(myList);
			myList.theAccount.setActType(pType);
			myList.add(myList.theAccount);
			
			/* Return the List */
			return myList;
		}
		
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
		 * @return the newly added item
		 */
		public Account addNewItem() { return null; }
			
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }
				
		/**
		 * Update account details after data update
		 */
		public void markActiveItems() throws Exception {
			ListIterator 	myIterator;
			Account 		myCurr;
			AccountType		myType;
					
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the accounts */
			while ((myCurr = myIterator.next()) != null) {				
				/* If we have a parent, mark the parent */
				if (myCurr.getParent() != null) {
					myCurr.getParent().touchItem(myCurr);
					if (!myCurr.isClosed())
						myCurr.getParent().setNonCloseable();
				}
				
				/* If we have an alias, mark the alias */
				if (myCurr.getAlias() != null) {
					myCurr.getAlias().touchItem(myCurr);
					if (!myCurr.isClosed())
						myCurr.getAlias().setNonCloseable();
				}
				
				/* Mark the AccountType */
				myType = myCurr.getActType();
				myType.touchItem(myCurr);
				
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
		public void addItem(int				uId,
							String   		pName,
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
									uId,
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
			AccountType	 myType;
			FinanceData	 myData = getData();
		
			/* Mark active items referenced by rates */
			myData.getRates().markActiveItems();
			
			/* Mark active items referenced by prices */
			myData.getPrices().markActiveItems();
			
			/* Mark active items referenced by patterns */
			myData.getPatterns().markActiveItems();
			
			/* Access the iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				/* If the account has a parent Id */
				if (myCurr.getParentId() != null) {
					/* Set the parent */
					myCurr.setParent(searchFor(myCurr.getParentId()));
					myCurr.getParent().touchItem(myCurr);
				}
					
				/* If the account has an alias Id */
				if (myCurr.getAliasId() != null) {
					/* Set the alias */
					myCurr.setAlias(searchFor(myCurr.getAliasId()));
					myCurr.getAlias().touchItem(myCurr);
				}

				/* Mark the AccountType */
				myType = myCurr.getActType();
				myType.touchItem(myCurr);
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
		private Integer         theOrder     	= -1;
		private Integer			theParentId  	= null;
		private Integer			theAliasId   	= null;
		private Integer		    theActTypeId 	= null;
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
		private Integer			getOrder()   	{ return theOrder; }
		private Integer			getActTypeId()  { return theActTypeId; }
		private Integer			getParentId()   { return theParentId; }
		private Integer			getAliasId()    { return theAliasId; }

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
			theType      = pType; 
			theActTypeId = (pType == null) ? null : pType.getId(); 
			theOrder     = (pType == null) ? null : pType.getOrder(); }
		private void setActTypeId(int uActTypeId) {
			theActTypeId = uActTypeId; }
		public void setMaturity(Date pMaturity) {
			theMaturity  = pMaturity; }
		public void setClose(Date pClose) {
			theClose     = pClose; }
		public void setParent(Account pParent) {
			theParent    = pParent; 
			theParentId  = (pParent == null) ? null : pParent.getId(); }
		public void setAlias(Account pAlias) {
			theAlias     = pAlias; 
			theAliasId   = (pAlias == null) ? null : pAlias.getId(); }
		private void setParentId(Integer uParentId) {
			theParentId	 = uParentId; }
		private void setAliasId(Integer uAliasId) {
			theAliasId	 = uAliasId; }
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
		public Values(Values pValues) { copyFrom(pValues); }
		
		/* Check whether this object is equal to that passed */
		public Difference histEquals(HistoryValues<Account> pCompare) {
			/* Make sure that the object is the same class */
			if (pCompare.getClass() != this.getClass()) return Difference.Different;
			
			/* Cast correctly */
			Values myValues = (Values)pCompare;

			/* Handle integer differences */
			if ((Utils.differs(theActTypeId, myValues.theActTypeId).isDifferent())	||
				(Utils.differs(theParentId,  myValues.theParentId).isDifferent())	||
				(Utils.differs(theAliasId,   myValues.theAliasId).isDifferent())	||
				(Utils.differs(theOrder,     myValues.theOrder).isDifferent()))
				return Difference.Different;
			
			/* Determine underlying differences */
			Difference myDifference = super.histEquals(pCompare);
			
			/* Compare underlying values */
			myDifference = myDifference.combine(differs(theName,				myValues.theName));
			myDifference = myDifference.combine(differs(theDesc,				myValues.theDesc));
			myDifference = myDifference.combine(AccountType.differs(theType,	myValues.theType));
			myDifference = myDifference.combine(Date.differs(theMaturity, 		myValues.theMaturity));
			myDifference = myDifference.combine(Date.differs(theClose,    		myValues.theClose));
			myDifference = myDifference.combine(Account.differs(theParent,  	myValues.theParent));
			myDifference = myDifference.combine(Account.differs(theAlias,   	myValues.theAlias));
			myDifference = myDifference.combine(differs(theWebSite,				myValues.theWebSite));
			myDifference = myDifference.combine(differs(theCustNo,				myValues.theCustNo));
			myDifference = myDifference.combine(differs(theUserId,				myValues.theUserId));
			myDifference = myDifference.combine(differs(thePassword,			myValues.thePassword));
			myDifference = myDifference.combine(differs(theAccount,				myValues.theAccount));
			myDifference = myDifference.combine(differs(theNotes,				myValues.theNotes));
			
			/* Return the differences */
			return myDifference;
		}
		
		/* Copy values */
		public HistoryValues<Account> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			Values myValues = (Values)pSource;
			super.copyFrom(myValues);
			theName       = myValues.getName();
			theDesc       = myValues.getDesc();
			theType       = myValues.getType();
			theActTypeId  = myValues.getActTypeId();
			theOrder      = myValues.getOrder();
			theMaturity   = myValues.getMaturity();
			theClose      = myValues.getClose();
			theParent     = myValues.getParent();
			theAlias      = myValues.getAlias();
			theParentId   = myValues.getParentId();
			theAliasId    = myValues.getAliasId();
			theWebSite 	  = (myValues.getWebSite() != null)		? new CharArrayPair(myValues.getWebSite()) : null;
			theCustNo 	  = (myValues.getCustNo() != null)		? new CharArrayPair(myValues.getCustNo()) : null;
			theUserId 	  = (myValues.getUserId() != null)		? new CharArrayPair(myValues.getUserId()) : null;
			thePassword	  = (myValues.getPassword() != null)	? new CharArrayPair(myValues.getPassword()) : null;
			theAccount	  = (myValues.getAccount() != null)		? new CharArrayPair(myValues.getAccount()) : null;
			theNotes	  = (myValues.getNotes() != null)		? new CharArrayPair(myValues.getNotes()) : null;
		}
		public Difference	fieldChanged(int fieldNo, HistoryValues<Account> pOriginal) {
			Values 	pValues = (Values)pOriginal;
			Difference		bResult = Difference.Identical;
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
				default:
					bResult = super.fieldChanged(fieldNo, pValues);
					break;
			}
			return bResult;
		}

		/**
		 * Update encryption after security change
		 */
		protected void updateSecurity() throws Exception {
			/* Update the encryption */
			theName = new StringPair(theName.getString());
			if (theDesc     != null) theDesc		= new StringPair(theDesc.getString());
			if (theWebSite  != null) theWebSite		= new CharArrayPair(theWebSite.getChars());
			if (theCustNo   != null) theCustNo		= new CharArrayPair(theCustNo.getChars());
			if (theUserId   != null) theUserId		= new CharArrayPair(theUserId.getChars());
			if (thePassword != null) thePassword	= new CharArrayPair(thePassword.getChars());
			if (theAccount  != null) theAccount		= new CharArrayPair(theAccount.getChars());
			if (theNotes    != null) theNotes		= new CharArrayPair(theNotes.getChars());
		}		

		/**
		 * Ensure encryption after security change
		 */
		protected void applySecurity() throws Exception {
			/* Apply the encryption */
			theName.encryptPair(null);
			if (theDesc     != null) theDesc.encryptPair(null);
			if (theWebSite  != null) theWebSite.encryptPair(null);
			if (theCustNo   != null) theCustNo.encryptPair(null);
			if (theUserId   != null) theUserId.encryptPair(null);
			if (thePassword != null) thePassword.encryptPair(null);
			if (theAccount  != null) theAccount.encryptPair(null);
			if (theNotes    != null) theNotes.encryptPair(null);
		}		

		/**
		 * Adopt encryption from base
		 * @param pBase the Base values
		 */
		protected void adoptSecurity(ControlKey pControl, EncryptedValues pBase) throws Exception {
			Values myBase = (Values)pBase;

			/* Apply the encryption */
			theName.encryptPair(myBase.getName());
			if (theDesc     != null) theDesc.encryptPair(myBase.getDesc());
			if (theWebSite  != null) theWebSite.encryptPair(myBase.getWebSite());
			if (theCustNo   != null) theCustNo.encryptPair(myBase.getCustNo());
			if (theUserId   != null) theUserId.encryptPair(myBase.getUserId());
			if (thePassword != null) thePassword.encryptPair(myBase.getPassword());
			if (theAccount  != null) theAccount.encryptPair(myBase.getAccount());
			if (theNotes    != null) theNotes.encryptPair(myBase.getNotes());
		}		
	}
}
