package uk.co.tolcroft.finance.data;

import java.util.Arrays;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.security.*;
import uk.co.tolcroft.finance.data.DataSet.*;

public class Account extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "Account";

	/**
	 * Account Name length
	 */
	public final static int NAMELEN 		= 50;

	/**
	 * Account Description length
	 */
	public final static int DESCLEN 		= 50;

	/**
	 * Account WebSite length
	 */
	public final static int WSITELEN 		= 100;

	/**
	 * Account CustNo length
	 */
	public final static int CUSTLEN 		= 50;

	/**
	 * Account UserId length
	 */
	public final static int UIDLEN 			= 50;

	/**
	 * Account PassWord length
	 */
	public final static int PWDLEN 			= 50;

	/**
	 * Account details length
	 */
	public final static int ACTLEN 			= 100;

	/**
	 * Account UserId length
	 */
	public final static int NOTELEN 		= 500;

	/**
	 * Account InitVector length
	 */
	public final static int INITVLEN 		= SymmetricKey.IVSIZE;

	/* Members */
	private int                   theOrder     = -1;
	private long				  theParentId  = -1;
	private long				  theAliasId   = -1;
	private long 				  theActTypeId = -1;
	private Event                 theEarliest  = null;
	private Event                 theLatest    = null;
	private boolean               isCloseable  = true;
	private boolean               hasRates	   = false;
	private boolean               hasPrices	   = false;
	private boolean               hasPatterns  = false;
	private boolean               isPatterned  = false;
	private boolean               isParent	   = false;
		
	/* Access methods */
	public  Values      getObj()       	{ return (Values)super.getObj(); }	
	public  String      getName()      	{ return getObj().getName(); }
	public  String      getDesc()      	{ return getObj().getDesc(); }
	public  Account     getParent()    	{ return getObj().getParent(); }
	public  long        getParentId()  	{ return theParentId; }
	public  Account     getAlias()    	{ return getObj().getAlias(); }
	public  long        getAliasId()  	{ return theAliasId; }
	public  Event       getEarliest()  	{ return theEarliest; }
	public  Event       getLatest()    	{ return theLatest; }
	public  AccountType getActType()   	{ return getObj().getType(); }
	public  int         getOrder()     	{ return theOrder; }
	public  Date        getMaturity()  	{ return getObj().getMaturity(); }
	public  Date    	getClose()     	{ return getObj().getClose(); }
	public  byte[]    	getInitVector()	{ return getObj().getInitVector(); }
	public  byte[]    	getWebSite()	{ return getObj().getWebSite(); }
	public  byte[]    	getCustNo()		{ return getObj().getCustNo(); }
	public  byte[]    	getUserId()		{ return getObj().getUserId(); }
	public  byte[]    	getPassword()	{ return getObj().getPassword(); }
	public  byte[]    	getAccount()	{ return getObj().getAccount(); }
	public  byte[]    	getNotes()		{ return getObj().getNotes(); }
	public  boolean     isCloseable()  	{ return isCloseable; }
	public  boolean     isParent()  	{ return isParent; }
	public  boolean     isClosed()     	{ return (getClose() != null); }
	public  boolean     isDeletable()  	{ 
		return ((theLatest == null) && 
				(!isDeleted()) &&
				(!isParent)    &&
				(!hasRates)    &&
				(!hasPrices)   &&
				(!hasPatterns) && 
				(!isPatterned) && 
				(!getActType().isReserved())); 
	}
		
	/* Linking methods */
	public Account     getBase() { return (Account)super.getBase(); }
	public boolean	   isLocked(){ return isClosed(); }
	
	/* Field IDs */
	public static final int FIELD_ID       = 0;
	public static final int FIELD_NAME     = 1;
	public static final int FIELD_DESC     = 2;
	public static final int FIELD_TYPE     = 3;
	public static final int FIELD_MATURITY = 4;
	public static final int FIELD_CLOSE    = 5;
	public static final int FIELD_PARENT   = 6;
	public static final int FIELD_ALIAS    = 7;
	public static final int FIELD_IV   	   = 8;
	public static final int FIELD_WEBSITE  = 9;
	public static final int FIELD_CUSTNO   = 10;
	public static final int FIELD_USERID   = 11;
	public static final int FIELD_PASSWORD = 12;
	public static final int FIELD_ACCOUNT  = 13;
	public static final int FIELD_NOTES    = 14;
	public static final int NUMFIELDS	   = 15;
	
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
			case FIELD_ID:			return "ID";
			case FIELD_NAME:		return "Name";
			case FIELD_DESC:		return "Description";
			case FIELD_TYPE:		return "AccountType";
			case FIELD_CLOSE:		return "CloseDate";
			case FIELD_MATURITY:	return "Maturity";
			case FIELD_PARENT:		return "Parent";
			case FIELD_ALIAS:		return "Alias";
			case FIELD_IV:			return "InitVector";
			case FIELD_WEBSITE:		return "WebSite";
			case FIELD_CUSTNO:		return "CustomerNo";
			case FIELD_USERID:		return "UserId";
			case FIELD_PASSWORD:	return "Password";
			case FIELD_ACCOUNT:		return "Account";
			case FIELD_NOTES:		return "Notes";
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
		String 	myString = "<tr><td>" + fieldName(iField) + "</td><td>";
		Values 	myObj 	 = (Values)pObj;
		switch (iField) {
			case FIELD_ID: 		
				myString += getId(); 
				break;
			case FIELD_NAME:	
				myString += myObj.getName(); 
				break;
			case FIELD_DESC:	
				myString += myObj.getDesc(); 
				break;
			case FIELD_TYPE:	
				if ((getActType() == null) &&
					(theActTypeId != -1))
					myString += "Id=" + theActTypeId;
				else
					myString += Utils.formatAccountType(getActType()); 
				break;
			case FIELD_CLOSE:	
				myString += Utils.formatDate(myObj.getClose()); 
				break;
			case FIELD_MATURITY:	
				myString += Utils.formatDate(myObj.getMaturity()); 
				break;
			case FIELD_PARENT:	
				if ((myObj.getParent() == null) &&
					(theParentId != -1))
					myString += "Id=" + theParentId;
				else
					myString += Utils.formatAccount(myObj.getParent()); 
				break;
			case FIELD_ALIAS:	
				if ((myObj.getAlias() == null) &&
					(theAliasId != -1))
					myString += "Id=" + theAliasId;
				else
					myString += Utils.formatAccount(myObj.getAlias()); 
				break;
			case FIELD_IV:	
				if (myObj.getInitVector() != null) 
					myString += Utils.HexStringFromBytes(myObj.getInitVector()); 
				break;
			case FIELD_WEBSITE:	
				if (myObj.getWebSite() != null) 
					myString += Utils.HexStringFromBytes(myObj.getWebSite()); 
				break;
			case FIELD_CUSTNO:	
				if (myObj.getCustNo() != null) 
					myString += Utils.HexStringFromBytes(myObj.getCustNo()); 
				break;
			case FIELD_USERID:	
				if (myObj.getUserId() != null) 
					myString += Utils.HexStringFromBytes(myObj.getUserId()); 
				break;
			case FIELD_PASSWORD:	
				if (myObj.getPassword() != null) 
					myString += Utils.HexStringFromBytes(myObj.getPassword()); 
				break;
			case FIELD_ACCOUNT:	
				if (myObj.getAccount() != null) 
					myString += Utils.HexStringFromBytes(myObj.getAccount()); 
				break;
			case FIELD_NOTES:	
				if (myObj.getNotes() != null) 
					myString += Utils.HexStringFromBytes(myObj.getNotes()); 
				break;
		}
		return myString + "</td></tr>";
	}
							
	/**
	 * Construct a copy of an Account
	 * 
	 * @param pAccount The Account to copy 
	 */
	public Account(List pList, Account pAccount) {
		/* Set standard values */
		super(pList, pAccount.getId());
		Values myObj = new Values(pAccount.getObj());
		setObj(myObj);
		theOrder     = pAccount.getOrder();
		theEarliest  = pAccount.theEarliest;
		theLatest    = pAccount.theLatest;
		isCloseable  = pAccount.isCloseable();
			
		/* Switch on the LinkStyle */
		switch (pList.getStyle()) {
			case CORE:
				pList.setNewId(this);				
				break;
			case EDIT:
				setBase(pAccount);
				setState(DataState.CLEAN);
				break;
			case UPDATE:
				setBase(pAccount);
				setState(pAccount.getState());
				break;
		}
	}
	
	/* Standard constructor */
	private Account(List    		pList,
			        long           	uId, 
					String         	sName, 
					long			uAcTypeId,
					String         	pDesc,
					java.util.Date 	pMaturity,
			        java.util.Date 	pClose,
			        long           	uParentId,
			        long           	uAliasId,
			        byte[]			pInitVect,
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
		myObj.setName(sName);
		myObj.setDesc(pDesc);
		myObj.setInitVector(pInitVect);
		myObj.setWebSite(pWebSite);
		myObj.setCustNo(pCustNo);
		myObj.setUserId(pUserId);
		myObj.setPassword(pPassword);
		myObj.setAccount(pAccount);
		myObj.setNotes(pNotes);
		
		/* Store the IDs */
		theActTypeId = uAcTypeId;
		theParentId  = uParentId;
		theAliasId   = uAliasId;
		
		/* Look up the Account Type */
		myActType = pList.theData.getAccountTypes().searchFor(uAcTypeId);
		if (myActType == null) 
			throw new Exception(ExceptionClass.DATA,
		                        this,
					            "Invalid Account Type Id");
		myObj.setType(myActType);
		theOrder    = (myActType.isChild() ? 100 + myActType.getOrder() :  myActType.getOrder());
		/*theOrder    = myActType.getOrder();*/

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
		setState(DataState.NEW);
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
		Account myAccount = (Account)pThat;
		
		/* Check for equality */
		if (getId() != myAccount.getId()) return false;
		if (Utils.differs(getName(),    	myAccount.getName())) 		return false;
		if (Utils.differs(getDesc(),    	myAccount.getDesc())) 		return false;
		if (Utils.differs(getActType(), 	myAccount.getActType())) 	return false;
		if (Utils.differs(getClose(),   	myAccount.getClose())) 		return false;
		if (Utils.differs(getMaturity(),	myAccount.getMaturity())) 	return false;
		if (Utils.differs(getParent(),      myAccount.getParent())) 	return false;			
		if (Utils.differs(getAlias(),       myAccount.getAlias())) 		return false;			
		if (Utils.differs(getInitVector(),	myAccount.getInitVector())) return false;
		if (Utils.differs(getWebSite(),		myAccount.getWebSite())) 	return false;
		if (Utils.differs(getCustNo(),		myAccount.getCustNo())) 	return false;
		if (Utils.differs(getUserId(),		myAccount.getUserId())) 	return false;
		if (Utils.differs(getPassword(),	myAccount.getPassword())) 	return false;
		if (Utils.differs(getAccount(),		myAccount.getAccount())) 	return false;
		if (Utils.differs(getNotes(),		myAccount.getNotes())) 		return false;
		return true;
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
	public	boolean isTaxFree()   { return getActType().isTaxFree(); }
	public	boolean isUnitTrust() { return getActType().isUnitTrust(); }
	public	boolean isDebt()      { return getActType().isDebt(); }
	public 	boolean isChild()     { return getActType().isChild(); }
	public 	boolean isBond()      { return getActType().isBond(); }
	public 	boolean isBenefit()   { return getActType().isBenefit(); }
	
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
			if ((mySet.getLoadState() == LoadState.LOADED) && 
				(getParent() == null)) 
				addError("Child Account must have parent", FIELD_PARENT);
				
			/* if we have a parent */
			if (getParent() != null) {
				/* check that any parent is external */
				if (!getParent().isExternal())
					addError("Parent account must be external", FIELD_PARENT);
			
				/* If we are open then parent must be open */
				if (!isClosed() && getParent().isClosed())
					addError("Parent account must not be closed", FIELD_PARENT);
			}
	    }
		
		/* If we have an alias */
		if (getAlias() != null) {
			/* Cannot alias to self */
			if (!Utils.differs(this, getAlias()))
				addError("Cannot alias to self", FIELD_ALIAS);

			/* Cannot alias to same type */
			else if (!Utils.differs(myType, getAlias().getActType()))
				addError("Cannot alias to same account type", FIELD_ALIAS);

			/* Must be alias type */
			if (!myType.canAlias())
				addError("This account type cannot alias", FIELD_ALIAS);

			/* Alias must be alias type */
			if (!getAlias().getActType().canAlias())
				addError("The alias account type is invalid", FIELD_ALIAS);

			/* Alias cannot be aliased */
			if (getAlias().getAlias() != null)
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
		if ((mySet.getLoadState() == LoadState.LOADED) && 
			(isClosed())) {
			/* Account must be closeable */
			if (!isCloseable())
				addError("Non-closeable account is closed", FIELD_CLOSE);
		}
			
		/* The InitVector must not be correct length */
		if ((getInitVector() != null) && (getInitVector().length != INITVLEN)) {
			addError("Initialisation Vector is incorrect length", FIELD_IV);
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
	 * 
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
		hasRates      = false;
		hasPrices     = false;
		hasPatterns   = false;
		isPatterned   = false;
		isParent	  = false;
	}
	
	/**
	 * Touch an account with an event
	 */
	public void touchAccount(Event pEvent) {
		/* Record the event */
		if (theEarliest == null) theEarliest = pEvent;
		theLatest = pEvent;
	}

	/**
	 * Touch an account with a rate
	 */
	public void touchRate() {
		/* Record the rate */
		hasRates = true;
	}
		
	/**
	 * Touch an account with a pattern
	 */
	public void touchPrice() {
		/* Record the price */
		hasPrices = true;
	}
		
	/**
	 * Touch an account with a pattern
	 */
	public void touchPattern() {
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
		/* Record the pattern */
		isParent = true;
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
	 * 
	 * @param pDesc the description 
	 */
	public void setDescription(String pDesc) {
		getObj().setDesc((pDesc == null) ? null : new String(pDesc));
	}
	
	/**
	 * Set a new maturity date 
	 *	 
	 * @param pDate the new date 
	 */
	public void setMaturity(Date pDate) {
		getObj().setMaturity((pDate == null) ? null : new Date(pDate));
	}
	
	/**
	 * Set a new close date 
	 *	 
	 * @param pDate the new date 
	 */
	public void setClose(Date pDate) {
		getObj().setClose((pDate == null) ? null : new Date(pDate));
	}
	
	/**
	 * Set a new parent 
	 *	 
	 * @param pParent the new parent 
	 */
	public void setParent(Account pParent) {
		getObj().setParent(pParent);
	}
	
	/**
	 * Set a new alias 
	 *	 
	 * @param pAlias the new alias 
	 */
	public void setAlias(Account pAlias) {
		getObj().setAlias(pAlias);
	}
	
	/**
	 * Set a new account name 
	 *	 
	 * @param pName the new name 
	 */
	public void setAccountName(String pName) {
		getObj().setName((pName == null) ? null : new String(pName));
	}
	
	/**
	 * Set a new account type
	 *	 
	 * @param pType the new type 
	 */
	public void setActType(AccountType pType) {
		getObj().setType(pType);
		theOrder    = pType.getOrder();
	}
	
	/**
	 * Set a new initialisation vector
	 *	 
	 * @param pVector the new vector 
	 */
	public void setInitVector(byte[] pVector) {
		getObj().setInitVector(pVector);
	}
	
	/**
	 * Set a new web site
	 *	 
	 * @param pWebSite the new site 
	 */
	public void setWebSite(byte[] pWebSite) {
		getObj().setWebSite(pWebSite);
	}
	
	/**
	 * Set a new customer number
	 *	 
	 * @param pCustNo the new number 
	 */
	public void setCustNo(byte[] pCustNo) {
		getObj().setCustNo(pCustNo);
	}
	
	/**
	 * Set a new UserId
	 *	 
	 * @param pUserId the new id 
	 */
	public void setUserId(byte[] pUserId) {
		getObj().setUserId(pUserId);
	}
	
	/**
	 * Set a new password
	 *	 
	 * @param pPassword the new password 
	 */
	public void setPassword(byte[] pPassword) {
		getObj().setPassword(pPassword);
	}
	
	/**
	 * Set a new account
	 *	 
	 * @param pAccount the new account 
	 */
	public void setAccount(byte[] pAccount) {
		getObj().setAccount(pAccount);
	}
	
	/**
	 * Set a new notes
	 *	 
	 * @param pNotes the new notes 
	 */
	public void setNotes(byte[] pNotes) {
		getObj().setNotes(pNotes);
	}
	
	/**
	 * Update base account from an edited account 
	 * 
	 * @param pAccount the edited account 
	 */
	public void applyChanges(DataItem pAccount) {
		Account myAccount = (Account)pAccount;
		
		/* Store the current detail into history */
		pushHistory();
		
		/* Update the Name if required */
		if (Utils.differs(getName(), myAccount.getName()))  
			setAccountName(myAccount.getName());
			
		/* Update the description if required */
		if (Utils.differs(getDesc(), myAccount.getDesc())) 
			setDescription(myAccount.getDesc());
			
		/* Update the account type if required */
		if (Utils.differs(getActType(), myAccount.getActType())) 
			setActType(myAccount.getActType());
			
		/* Update the maturity if required */
		if (Utils.differs(getMaturity(), myAccount.getMaturity())) 
			setMaturity(myAccount.getMaturity());
		
		/* Update the close if required */
		if (Utils.differs(getClose(), myAccount.getClose())) 
			setClose(myAccount.getClose());
		
		/* Update the parent if required */
		if (Utils.differs(getParent(), myAccount.getParent())) 
			setParent(myAccount.getParent());
		
		/* Update the alias if required */
		if (Utils.differs(getAlias(), myAccount.getAlias())) 
			setParent(myAccount.getAlias());
		
		/* Update the InitVector if required */
		if (Utils.differs(getInitVector(), myAccount.getInitVector())) 
			setInitVector(myAccount.getInitVector());
		
		/* Update the WebSite if required */
		if (Utils.differs(getWebSite(), myAccount.getWebSite())) 
			setWebSite(myAccount.getWebSite());
		
		/* Update the customer number if required */
		if (Utils.differs(getCustNo(), myAccount.getCustNo())) 
			setCustNo(myAccount.getCustNo());
		
		/* Update the UserId if required */
		if (Utils.differs(getUserId(), myAccount.getUserId())) 
			setUserId(myAccount.getUserId());
		
		/* Update the Password if required */
		if (Utils.differs(getPassword(), myAccount.getPassword())) 
			setPassword(myAccount.getPassword());
		
		/* Update the account if required */
		if (Utils.differs(getAccount(), myAccount.getAccount())) 
			setAccount(myAccount.getAccount());
		
		/* Update the notes if required */
		if (Utils.differs(getNotes(), myAccount.getNotes())) 
			setNotes(myAccount.getNotes());
		
		/* Check for changes */
		if (checkForHistory()) setState(DataState.CHANGED);
	}

	/**
	 * AccountList class
	 */
	public static class List  extends DataList<Account> {		
		private DataSet	theData			= null;
		public 	DataSet getData()		{ return theData; }
		
		/** 
	 	 * Construct an empty CORE account list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(DataSet pData) { 
			super(ListStyle.CORE, true);
			theData = pData;
		}

		/** 
	 	 * Construct a generic account list
	 	 * @param pData the DataSet for the list
	 	 * @param pStyle the style of the list 
	 	 */
		public List(DataSet pData, ListStyle pStyle) { 
			super(pStyle, true);
			theData = pData;
		}

		/** 
	 	 * Construct a generic account list
	 	 * @param pList the source account list 
	 	 * @param pStyle the style of the list 
	 	 */
		public List(List pList, ListStyle pStyle) { 
			super(pList, pStyle);
			theData = pList.getData();
		}

		/** 
	 	 * Construct a difference account list
	 	 * @param pNew the new Account list 
	 	 * @param pOld the old Account list 
	 	 */
		protected List(List pNew, List pOld) { 
			super(pNew, pOld);
			theData = pNew.getData();
		}
		
		/** 
	 	 * Clone an Account list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return new List(this, ListStyle.CORE); }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pAccount item
		 * @return the newly added item
		 */
		public DataItem addNewItem(DataItem pAccount) {
			Account myAccount = new Account(this, (Account)pAccount);
			myAccount.addToList();
			return myAccount;
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
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {				
				/* If we have a parent, mark the parent */
				if (myCurr.getParent() != null) {
					myCurr.getParent().touchParent();
					if (!myCurr.isClosed())
						myCurr.getParent().setNonCloseable();
				}
				
				/* If we have no latest event, then we are not closeable */
				if (myCurr.getLatest() == null) {
					myCurr.setNonCloseable();
				}
				
				/* If we have patterns or are touched by patterns, then we are not closeable */
				if (myCurr.hasPatterns || myCurr.isPatterned) {
					myCurr.setNonCloseable();
				}
				
				/* If we have a close date and a latest event */
				if ((myCurr.getClose() != null) &&
					(myCurr.getLatest() != null)) {
					/* Check whether we need to adjust the date */
					myCurr.adjustClosed();
				}
				
				/* If we are in final loading stage */
				if (theData.getLoadState() == LoadState.FINAL) {
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
		 * 
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
		 * 
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
		 * 
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
		 * Get the taxman account from the list
		 * 
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
		 * @param uId the Id of the account
		 * @param pAccount the Name of the account 
		 * @param pAcType the Name of the account type
		 * @param pDesc the Description of the account (or null)
		 * @param pMaturity the Maturity date for a bond (or null)
		 * @param pClosed the Close Date for the account (or null)
		 * @param pParent the Name of the parent account (or null)
		 * @param pAlias the Name of the alias account (or null)
		 * @throws Exception on error
		 */ 
		public void addItem(long     		uId,
				            String   		pName,
				            String   		pAcType,
				            String   		pDesc,
				            java.util.Date  pMaturity,
				            java.util.Date  pClosed,
				            String   		pParent,
				            String   		pAlias,
					        byte[]			pInitVect,
					        byte[]			pWebSite,
					        byte[]			pCustNo,
					        byte[]			pUserId,
					        byte[]			pPassword,
					        byte[]			pAccount,
					        byte[]			pNotes) throws Exception {
			AccountType.List 	myActTypes;
			AccountType 		myActType;
			Account		 		myParent;
			Account		 		myAlias;
			long				myParentId = -1;
			long				myAliasId  = -1;
				
			/* Access the account types and accounts */
			myActTypes = theData.getAccountTypes();
				
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
			
			/* Add the account */
			addItem(uId,
					pName,
					myActType.getId(),
					pDesc,
					pMaturity,
					pClosed,
					myParentId,
					myAliasId,
			        pInitVect,
			        pWebSite,
			        pCustNo,
			        pUserId,
			        pPassword,
			        pAccount,
			        pNotes);
		}
			
		/**
		 * Add an Account
		 * @param uId the Id of the account
		 * @param pAccount the Name of the account 
		 * @param uAcTypeId the Id of the account type
		 * @param pDesc the Description of the account (or null)
		 * @param pMaturity the Maturity date for a bond (or null)
		 * @param pClosed the Close Date for the account (or null)
		 * @param uParentId the Id of the parent account (or -1)
		 * @param uAliasId the Id of the alias account (or -1)
		 * @throws Exception on error
		 */ 
		public void addItem(long     		uId,
				            String   		pName,
				            long     		uAcTypeId,
				            String   		pDesc,
				            java.util.Date  pMaturity,
				            java.util.Date  pClosed,
				            long     		uParentId,
				            long     		uAliasId,
					        byte[]			pInitVect,
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
					                pName, 
					                uAcTypeId,
					                pDesc,
					                pMaturity,
					                pClosed,
					                uParentId,
					                uAliasId,
							        pInitVect,
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
			if (searchFor(pName) != null) 
				throw new Exception(ExceptionClass.DATA,
						  			myAccount,
			                        "Duplicate Account");
			
			/* Add the Account to the list */
			myAccount.addToList();				
		}
		
		/* Validate accounts */
		public void validateAccounts() throws Exception {
			ListIterator myIterator;
			Account      myCurr;
		
			/* Mark active rates */
			theData.getRates().markActiveRates();
			
			/* Mark active prices */
			theData.getPrices().markActivePrices();
			
			/* Mark active patterns */
			theData.getPatterns().markActivePatterns();
			
			/* Access the iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				/* If the account has a parent Id */
				if (myCurr.getParentId() != -1) {
					/* Set the parent */
					myCurr.setParent(searchFor(myCurr.getParentId()));
				}
					
				/* If the account has an alias Id */
				if (myCurr.getAliasId() != -1) {
					/* Set the alias */
					myCurr.setAlias(searchFor(myCurr.getAliasId()));
				}
					
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
	public class Values implements histObject {
		private String      theName     	= null;
		private String      theDesc     	= null;
		private AccountType	theType			= null;
		private Date       	theMaturity 	= null;
		private Date       	theClose    	= null;
		private Account		theParent		= null;
		private Account		theAlias		= null;
		private byte[]		theInitVector	= null;
		private byte[]		theWebSite		= null;
		private byte[]		theCustNo		= null;
		private byte[]		theUserId		= null;
		private byte[]		thePassword		= null;
		private byte[]		theAccount		= null;
		private byte[]		theNotes		= null;
		
		/* Access methods */
		public String       getName()      	{ return theName; }
		public String       getDesc()      	{ return theDesc; }
		public AccountType	getType()      	{ return theType; }
		public Date       	getMaturity()  	{ return theMaturity; }
		public Date       	getClose()     	{ return theClose; }
		public Account		getParent()    	{ return theParent; }
		public Account		getAlias()    	{ return theAlias; }
		public byte[]		getInitVector()	{ return theInitVector; }
		public byte[]		getWebSite()	{ return theWebSite; }
		public byte[]		getCustNo()		{ return theCustNo; }
		public byte[]		getUserId()		{ return theUserId; }
		public byte[]		getPassword()	{ return thePassword; }
		public byte[]		getAccount()	{ return theAccount; }
		public byte[]		getNotes()		{ return theNotes; }
		
		public void setName(String pName) {
			theName      = pName; }
		public void setDesc(String pDesc) {
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
		public void setInitVector(byte[] pInitVector) {
			theInitVector	= pInitVector; }
		public void setWebSite(byte[] pWebSite) {
			theWebSite		= pWebSite; }
		public void setCustNo(byte[] pCustNo) {
			theCustNo		= pCustNo; }
		public void setUserId(byte[] pUserId) {
			theUserId		= pUserId; }
		public void setPassword(byte[] pPassword) {
			thePassword		= pPassword; }
		public void setAccount(byte[] pAccount) {
			theAccount		= pAccount; }
		public void setNotes(byte[] pNotes) {
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
			theInitVector = pValues.getInitVector();
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
			if (Utils.differs(theName,     		pValues.theName))     	return false;
			if (Utils.differs(theDesc,     		pValues.theDesc))     	return false;
			if (Utils.differs(theType,     		pValues.theType))     	return false;
			if (Utils.differs(theMaturity, 		pValues.theMaturity)) 	return false;
			if (Utils.differs(theClose,    		pValues.theClose))    	return false;
			if (Utils.differs(theParent,   		pValues.theParent))   	return false;
			if (Utils.differs(theAlias,   		pValues.theAlias))   	return false;
			if (Utils.differs(theInitVector,	pValues.theInitVector)) return false;
			if (Utils.differs(theWebSite,		pValues.theWebSite)) 	return false;
			if (Utils.differs(theCustNo,		pValues.theCustNo)) 	return false;
			if (Utils.differs(theUserId,		pValues.theUserId)) 	return false;
			if (Utils.differs(thePassword,		pValues.thePassword)) 	return false;
			if (Utils.differs(theAccount,		pValues.theAccount)) 	return false;
			if (Utils.differs(theNotes,			pValues.theNotes)) 		return false;
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
			theInitVector = pValues.getInitVector();
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
					bResult = (Utils.differs(theName,     	pValues.theName));
					break;
				case FIELD_DESC:
					bResult = (Utils.differs(theDesc,     	pValues.theDesc));
					break;
				case FIELD_TYPE:
					bResult = (Utils.differs(theType,     	pValues.theType));
					break;
				case FIELD_MATURITY:
					bResult = (Utils.differs(theMaturity, 	pValues.theMaturity));
					break;
				case FIELD_CLOSE:
					bResult = (Utils.differs(theClose,    	pValues.theClose));
					break;
				case FIELD_PARENT:
					bResult = (Utils.differs(theParent,   	pValues.theParent));
					break;
				case FIELD_ALIAS:
					bResult = (Utils.differs(theAlias,   	pValues.theAlias));
					break;
				case FIELD_IV:
					bResult = (Utils.differs(theInitVector,	pValues.theInitVector));
					break;
				case FIELD_WEBSITE:
					bResult = (Utils.differs(theWebSite,	pValues.theWebSite));
					break;
				case FIELD_CUSTNO:
					bResult = (Utils.differs(theCustNo,		pValues.theCustNo));
					break;
				case FIELD_USERID:
					bResult = (Utils.differs(theUserId,		pValues.theUserId));
					break;
				case FIELD_PASSWORD:
					bResult = (Utils.differs(thePassword,	pValues.thePassword));
					break;
				case FIELD_ACCOUNT:
					bResult = (Utils.differs(theAccount,	pValues.theAccount));
					break;
				case FIELD_NOTES:
					bResult = (Utils.differs(theNotes,		pValues.theNotes));
					break;
			}
			return bResult;
		}
	}
	
	/**
	 * SecureValues
	 */
	public static class SecureValues {
		/* Members */
		private SymmetricKey 	theKey 		= null;
		private SecurityCipher 	theCipher 	= null;
		private Account			theMaster	= null;
		private char[]			theWebSite	= null;
		private char[]			theCustNo 	= null;
		private char[]			theUserId	= null;
		private char[]			thePassword = null;
		private char[]			theAccount	= null;
		private char[]			theNotes 	= null;
		
		/* Access methods */
		public	char[] getWebSite()		{ return theWebSite; }
		public	char[] getCustNo()		{ return theCustNo; }
		public	char[] getUserId()		{ return theUserId; }
		public	char[] getPassword()	{ return thePassword; }
		public	char[] getAccount()		{ return theAccount; }
		public	char[] getNotes()		{ return theNotes; }

		/* Constructor */
		private SecureValues(Account pAccount) {
			SecurityCipher 	myCipher;
			List 		   	myList = (List)pAccount.getList();
			DataSet			myData = myList.theData;
		
			/* Record the master account */
			theMaster = pAccount;
			
			/* protect against exceptions */
			try {
				/* If we have an initialisation vector */
				if (pAccount.getInitVector() != null) {
					/* Grab a security Cipher to decrypt the values */
					theKey   = myData.getKey();
					myCipher = theKey.initDecryption(pAccount.getInitVector());
				
					/* Access the values */
					if (pAccount.getWebSite() != null)
						theWebSite  = myCipher.decryptBytes(pAccount.getWebSite());
					if (pAccount.getCustNo() != null)
						theCustNo   = myCipher.decryptBytes(pAccount.getCustNo());
					if (pAccount.getUserId() != null)
						theUserId   = myCipher.decryptBytes(pAccount.getUserId());
					if (pAccount.getPassword() != null)
						thePassword = myCipher.decryptBytes(pAccount.getPassword());
					if (pAccount.getAccount() != null)
						theAccount  = myCipher.decryptBytes(pAccount.getAccount());
					if (pAccount.getWebSite() != null)
						theNotes    = myCipher.decryptBytes(pAccount.getNotes());
				}				
			}
			catch (Throwable e) {}
		}
		
		/**
		 * Clear arrays on garbage collection
		 */
		protected void finalize() throws Throwable {
			/* Null existing values */
			if (theWebSite  != null) Arrays.fill(theWebSite,  (char) 0);
			if (theCustNo   != null) Arrays.fill(theCustNo,   (char) 0);
			if (theUserId   != null) Arrays.fill(theUserId,   (char) 0);
			if (thePassword != null) Arrays.fill(thePassword, (char) 0);
			if (theAccount  != null) Arrays.fill(theAccount,  (char) 0);
			if (theNotes    != null) Arrays.fill(theNotes,    (char) 0);
		}
		
		/* Ensure that we have a cipher */
		private	void ensureCipher() throws Exception {
			/* If we do not have a cipher */
			if (theCipher == null) {
				/* If we have an InitVector */
				if (theMaster.getInitVector() != null) {
					/* Grab a security Cipher to encrypt the values */
					theCipher = theKey.initEncryption(theMaster.getInitVector());
				}
					
				/* Else we have to initialise the vector */
				else {
					/* Grab a security Cipher to encrypt the values */
					theCipher = theKey.initEncryption();
					
					/* record the vector */
					theMaster.setInitVector(theCipher.getInitVector());
				}
			}
		}
		
		/* Access methods */
		public	void setWebSite(char[] pWebSite) {
			byte[] myBytes;
			
			/* If we have changed the value */
			if (Utils.differs(theWebSite, pWebSite)) {
				/* Protect the operation */
				try {
					/* If we have a new value */
					if (pWebSite != null) {
						/* Ensure that we have a cipher */
						ensureCipher();
						
						/* Encrypt and update */
						myBytes = theCipher.encryptChars(pWebSite);
						theMaster.setWebSite(myBytes);
					
						/* Null existing value */
						if (theWebSite != null) Arrays.fill(theWebSite, (char) 0);
						theWebSite = Arrays.copyOf(pWebSite, pWebSite.length);
						Arrays.fill(pWebSite, (char) 0);
					}
					
					/* Else setting value to null */
					else {
						/* update */
						theMaster.setWebSite(null);
						
						/* Null existing value */
						if (theWebSite != null) Arrays.fill(theWebSite, (char) 0);
						theWebSite = null;
					}
				}
				catch (Throwable e) {}
			}
		}
		
		/* Access methods */
		public	void setCustNo(char[] pCustNo) {
			byte[] myBytes;
			
			/* If we have changed the value */
			if (Utils.differs(theCustNo, pCustNo)) {
				/* Protect the operation */
				try {
					/* If we have a new value */
					if (pCustNo != null) {
						/* Ensure that we have a cipher */
						ensureCipher();

						/* Encrypt and update */
						myBytes = theCipher.encryptChars(pCustNo);
						theMaster.setCustNo(myBytes);
					
						/* Null existing value */
						if (theCustNo != null) Arrays.fill(theCustNo, (char) 0);
						theCustNo = Arrays.copyOf(pCustNo, pCustNo.length);
						Arrays.fill(pCustNo, (char) 0);
					}
					
					/* Else setting value to null */
					else {
						/* update */
						theMaster.setCustNo(null);
						
						/* Null existing value */
						if (theCustNo != null) Arrays.fill(theCustNo, (char) 0);
						theCustNo = null;
					}
				}
				catch (Throwable e) {}
			}
		}
		
		/* Access methods */
		public	void setUserId(char[] pUserId) {
			byte[] myBytes;
			
			/* If we have changed the value */
			if (Utils.differs(theUserId, pUserId)) {
				/* Protect the operation */
				try {
					/* If we have a new value */
					if (pUserId != null) {
						/* Ensure that we have a cipher */
						ensureCipher();

						/* Encrypt and update */
						myBytes = theCipher.encryptChars(pUserId);
						theMaster.setUserId(myBytes);
					
						/* Null existing value */
						if (theUserId != null) Arrays.fill(theUserId, (char) 0);
						theUserId = Arrays.copyOf(pUserId, pUserId.length);
						Arrays.fill(pUserId, (char) 0);
					}
					
					/* Else setting value to null */
					else {
						/* update */
						theMaster.setUserId(null);
						
						/* Null existing value */
						if (theUserId != null) Arrays.fill(theUserId, (char) 0);
						theUserId = null;
					}
				}
				catch (Throwable e) {}
			}
		}
		/* Access methods */
		public	void setPassword(char[] pPassword) {
			byte[] myBytes;
			
			/* If we have changed the value */
			if (Utils.differs(thePassword, pPassword)) {
				/* Protect the operation */
				try {
					/* If we have a new value */
					if (pPassword != null) {
						/* Ensure that we have a cipher */
						ensureCipher();

						/* Encrypt and update */
						myBytes = theCipher.encryptChars(pPassword);
						theMaster.setPassword(myBytes);
					
						/* Null existing value */
						if (thePassword != null) Arrays.fill(thePassword, (char) 0);
						thePassword = Arrays.copyOf(pPassword, pPassword.length);
						Arrays.fill(pPassword, (char) 0);
					}
					
					/* Else setting value to null */
					else {
						/* update */
						theMaster.setPassword(null);
						
						/* Null existing value */
						if (thePassword != null) Arrays.fill(thePassword, (char) 0);
						thePassword = null;
					}
				}
				catch (Throwable e) {}
			}
		}
		
		/* Access methods */
		public	void setAccount(char[] pAccount) {
			byte[] myBytes;
			
			/* If we have changed the value */
			if (Utils.differs(theAccount, pAccount)) {
				/* Protect the operation */
				try {
					/* If we have a new value */
					if (pAccount != null) {
						/* Ensure that we have a cipher */
						ensureCipher();

						/* Encrypt and update */
						myBytes = theCipher.encryptChars(pAccount);
						theMaster.setAccount(myBytes);
					
						/* Null existing value */
						if (theAccount != null) Arrays.fill(theAccount, (char) 0);
						theAccount = Arrays.copyOf(pAccount, pAccount.length);
						Arrays.fill(pAccount, (char) 0);
					}
					
					/* Else setting value to null */
					else {
						/* update */
						theMaster.setAccount(null);
						
						/* Null existing value */
						if (theAccount != null) Arrays.fill(theAccount, (char) 0);
						theAccount = null;
					}
				}
				catch (Throwable e) {}
			}
		}
		
		/* Access methods */
		public	void setNotes(char[] pNotes) {
			byte[] myBytes;
			
			/* If we have changed the value */
			if (Utils.differs(theNotes, pNotes)) {
				/* Protect the operation */
				try {
					/* If we have a new value */
					if (pNotes != null) {
						/* Ensure that we have a cipher */
						ensureCipher();

						/* Encrypt and update */
						myBytes = theCipher.encryptChars(pNotes);
						theMaster.setNotes(myBytes);
					
						/* Null existing value */
						if (theNotes != null) Arrays.fill(theNotes, (char) 0);
						theWebSite = Arrays.copyOf(pNotes, pNotes.length);
						Arrays.fill(pNotes, (char) 0);
					}
					
					/* Else setting value to null */
					else {
						/* update */
						theMaster.setNotes(null);
						
						/* Null existing value */
						if (theNotes != null) Arrays.fill(theNotes, (char) 0);
						theNotes = null;
					}
				}
				catch (Throwable e) {}
			}
		}
	}
}
