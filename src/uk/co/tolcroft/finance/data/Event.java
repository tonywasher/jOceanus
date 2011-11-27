package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.views.Statement.*;
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

public class Event extends EncryptedItem<Event> {
	/**
	 * The name of the object
	 */
	public static final String objName = "Event";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";

	/**
	 * Event Description length
	 */
	public final static int DESCLEN 		= 50;
	
	/**
	 * Event Info Set
	 */
	private EventInfoSet	theInfoSet		= null;
	
	/* Access methods */
	public  Values         	getValues()    { return (Values)super.getValues(); }	
	public  Date      		getDate()      { return getValues().getDate(); }
	public  String          getDesc()      { return getPairValue(getValues().getDesc()); }
	public  Money     		getAmount()    { return getPairValue(getValues().getAmount()); }
	public  Account         getDebit()     { return getValues().getDebit(); }
	public  Account         getCredit()    { return getValues().getCredit(); }
	public  Units     		getUnits()     { return getPairValue(getValues().getUnits()); }
	public  TransactionType getTransType() { return getValues().getTransType(); }
	public  Money     		getTaxCredit() { return getPairValue(getValues().getTaxCredit()); }
	public  Integer	        getYears()     { return getValues().getYears(); }
	public  Dilution        getDilution()  { return getPairValue(getValues().getDilution()); }
	protected EventInfoSet  getInfoSet()   { return theInfoSet; }

	/* Encrypted value access */
	public  byte[]	getAmountBytes()    { return getPairBytes(getValues().getAmount()); }
	public  byte[]  getDescBytes()      { return getPairBytes(getValues().getDesc()); }
	public  byte[]	getUnitsBytes()     { return getPairBytes(getValues().getUnits()); }
	public  byte[]  getTaxCredBytes()   { return getPairBytes(getValues().getTaxCredit()); }
	public  byte[]  getDilutionBytes()  { return getPairBytes(getValues().getDilution()); }

	/* Linking methods */
	public Event     getBase() { return (Event)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_DATE      = EncryptedItem.NUMFIELDS;
	public static final int FIELD_DESC      = EncryptedItem.NUMFIELDS+1;
	public static final int FIELD_AMOUNT    = EncryptedItem.NUMFIELDS+2;
	public static final int FIELD_DEBIT     = EncryptedItem.NUMFIELDS+3;
	public static final int FIELD_CREDIT    = EncryptedItem.NUMFIELDS+4;
	public static final int FIELD_UNITS     = EncryptedItem.NUMFIELDS+5;
	public static final int FIELD_TRNTYP    = EncryptedItem.NUMFIELDS+6;
	public static final int FIELD_TAXCREDIT = EncryptedItem.NUMFIELDS+7;
	public static final int FIELD_DILUTION  = EncryptedItem.NUMFIELDS+8;
	public static final int FIELD_YEARS     = EncryptedItem.NUMFIELDS+9;
	public static final int NUMFIELDS	    = EncryptedItem.NUMFIELDS+10;
			
	/* Virtual Field IDs */
	public static final int VFIELD_TAXCREDIT	= 100;
	public static final int VFIELD_NATINSURE	= 101;
	public static final int VFIELD_BENEFIT		= 102;
	public static final int VFIELD_PENSION		= 103;
	public static final int VFIELD_CASHCONSIDER	= 104;
	public static final int VFIELD_CREDITUNITS	= 105;
	public static final int VFIELD_DEBITUNITS	= 106;
	public static final int VFIELD_DILUTION		= 107;
	public static final int VFIELD_XFERDELAY	= 108;
	public static final int VFIELD_QUALIFYYEARS	= 109;
	public static final int VFIELD_CASHACCOUNT	= 110;
	
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
			case FIELD_DATE:		return "Date";
			case FIELD_DESC:		return "Description";
			case FIELD_AMOUNT:		return "Amount";
			case FIELD_DEBIT:		return "Debit";
			case FIELD_CREDIT:		return "Credit";
			case FIELD_UNITS:		return "Units";
			case FIELD_TAXCREDIT:	return "TaxCredit";
			case FIELD_YEARS:		return "Years";
			case FIELD_TRNTYP:		return "TransactionType";
			case FIELD_DILUTION:	return "Dilution";
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
	public String formatField(DebugDetail pDetail, int iField, HistoryValues<Event> pValues) {
		String 	myString = "";
		Values 	myValues = (Values)pValues;
		switch (iField) {
			case FIELD_DATE:	
				myString += Date.format(myValues.getDate()); 
				break;
			case FIELD_DESC:	
				myString += myValues.getDescValue(); 
				break;
			case FIELD_TRNTYP: 	
				if ((myValues.getTransType() == null) &&
					(myValues.getTransId() != null))
					myString += "Id=" + myValues.getTransId();
				else
					myString += TransactionType.format(myValues.getTransType());	
				myString = pDetail.addDebugLink(myValues.getTransType(), myString);
				break;
			case FIELD_DEBIT:
				if ((myValues.getDebit() == null) &&
					(myValues.getDebitId() != null))
					myString += "Id=" + myValues.getDebitId();
				else
					myString += Account.format(myValues.getDebit()); 
				myString = pDetail.addDebugLink(myValues.getDebit(), myString);
				break;
			case FIELD_CREDIT:	
				if ((myValues.getCredit() == null) &&
					(myValues.getCreditId() != null))
					myString += "Id=" + myValues.getCreditId();
				else
					myString += Account.format(myValues.getCredit()); 
				myString = pDetail.addDebugLink(myValues.getCredit(), myString);
				break;
			case FIELD_AMOUNT: 	
				myString += Money.format(myValues.getAmountValue());	
				break;
			case FIELD_UNITS: 	
				myString += Units.format(myValues.getUnitsValue());	
				break;
			case FIELD_TAXCREDIT:	
				myString += Money.format(myValues.getTaxCredValue()); 
				break;
			case FIELD_YEARS:	
				myString += myValues.getYears(); 
				break;
			case FIELD_DILUTION:	
				myString += Dilution.format(myValues.getDilutionValue()); 
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
	protected HistoryValues<Event> getNewValues() { return new Values(); }
	
	/**
	 * Construct a copy of an Event
	 * 
	 * @param pEvent The Event to copy 
	 */
	public Event(List pList, Event pEvent) {		
		/* Set standard values */
		super(pList, pEvent.getId());
		Values myValues = getValues();
		myValues.copyFrom(pEvent.getValues());
		ListStyle myOldStyle = pEvent.getList().getStyle();

		/* Switch on the ListStyle */
		switch (pList.getStyle()) {
			case EDIT:
				/* If this is a view creation */
				if (myOldStyle == ListStyle.CORE) {
					/* Event is based on the original element */
					setBase(pEvent);
					pList.setNewId(this);				
					break;
				}
				
				/* Else this is a duplication so treat as new item */
				setId(0);
				pList.setNewId(this);				
				break;
			case CLONE:
				reBuildLinks(pList.getData());
			case COPY:
			case CORE:
				/* Reset Id if this is an insert from a view */
				if (myOldStyle == ListStyle.EDIT) setId(0);
				pList.setNewId(this);				
				break;
			case UPDATE:
				setBase(pEvent);
				setState(pEvent.getState());
				break;
		}
	}
	
	/**
	 * Construct a new event from an Account pattern
	 * 
	 * @param pList the list to build into
	 * @param pLine The Line to copy 
	 */
	protected Event(List   	pList,
		            Pattern pLine) {
		/* Set standard values */
		super(pList, 0);
		Values myValues = getValues();
		myValues.copyFrom(pLine.getValues());

		/* Allocate the id */
		if (this.getClass() == Event.class)
			pList.setNewId(this);				
	}
	
	/**
	 * Construct a new event from a Statement Line
	 * 
	 * @param pLine The Line to copy 
	 */
	public Event(List   pList,
		         Line	pLine) {
	
		/* Set standard values */
		super(pList, 0);
		Values myValues = getValues();
		myValues.copyFrom(pLine.getValues());
			
		/* Allocate the id */
		pList.setNewId(this);				
	}
	
	/* Standard constructor for a newly inserted event */
	public Event(List pList) {
		super(pList, 0);
		setControlKey(pList.getControlKey());
		pList.setNewId(this);				
	}

	/* constructor for load from encrypted */
	protected Event(List      		pList,
			        int	          	uId, 
			        int				uControlId,
		            java.util.Date 	pDate,
		            byte[]        	pDesc,
		            int           	uDebit,
		            int	        	uCredit,
		            int				uTransType,
		            byte[]     		pAmount,
		            byte[]			pUnits,
		            byte[]			pTaxCredit,
		            byte[]			pDilution,
		            Integer			pYears) throws Exception {
		/* Initialise item */
		super(pList, uId);
		
		/* Local variables */
		TransactionType	myTransType;
		Account   		myAccount;
		Account.List	myAccounts;
		
		/* Access account list */
		FinanceData	myData 	= pList.getData();
		myAccounts = myData.getAccounts();
		
		/* Create a new EventValues object */
		Values myValues = getValues();
		
		/* Create a new EventInfoSet */
		theInfoSet = new EventInfoSet(this);

		/* Store the IDs that we will look up */
		myValues.setDebitId(uDebit);
		myValues.setCreditId(uCredit);
		myValues.setTransId(uTransType);
		setControlKey(uControlId);
		
		/* Create the date */
		myValues.setDate(new Date(pDate));
		
		/* Look up the Debit Account */
		myAccount = myAccounts.searchFor(uDebit);
		if (myAccount == null)
			throw new Exception(ExceptionClass.DATA,
   					  			this, 
   					  			"Invalid Debit Account Id");
		myValues.setDebit(myAccount);
		
		/* Look up the Debit Account */
		myAccount = myAccounts.searchFor(uCredit);
		if (myAccount == null)
			throw new Exception(ExceptionClass.DATA,
   					  			this, 
   					  			"Invalid Credit Account Id");
		myValues.setCredit(myAccount);
		
		/* Look up the Transaction Type */
		myTransType = myData.getTransTypes().searchFor(uTransType);
		if (myTransType == null)
			throw new Exception(ExceptionClass.DATA,
   					  			this, 
   					  			"Invalid Transaction Type Id");
		myValues.setTransType(myTransType);
		
		/* Set the years */
		myValues.setYears(pYears);
		
		/* Record the encrypted values */
		myValues.setDesc(new StringPair(pDesc));
		myValues.setAmount(new MoneyPair(pAmount));
		if (pUnits != null) myValues.setUnits(new UnitsPair(pUnits));
		if (pTaxCredit != null) myValues.setTaxCredit(new MoneyPair(pTaxCredit));
		if (pDilution != null) myValues.setDilution(new DilutionPair(pDilution));

		/* Allocate the id */
		pList.setNewId(this);				
	}
	
	/* Standard constructor */
	protected Event(List      		pList,
				    int				uId,
		            java.util.Date 	pDate,
		            String         	pDesc,
		            Account         pDebit,
		            Account        	pCredit,
		            TransactionType	pTransType,
		            String     		pAmount,
		            String			pUnits,
		            String			pTaxCredit,
		            String			pDilution,
		            Integer			pYears) throws Exception {
		/* Initialise item */
		super(pList, uId);
		
		/* Create a new EventValues object */
		Values myValues = getValues();

		/* Record the encrypted values */
		myValues.setDesc(new StringPair(pDesc));
		myValues.setAmount(new MoneyPair(pAmount));
		myValues.setDebit(pDebit);
		myValues.setCredit(pCredit);
		myValues.setTransType(pTransType);
		myValues.setDate(new Date(pDate));
		myValues.setYears(pYears);
		if (pUnits != null) myValues.setUnits(new UnitsPair(pUnits));
		if (pTaxCredit != null) myValues.setTaxCredit(new MoneyPair(pTaxCredit));
		if (pDilution != null) myValues.setDilution(new DilutionPair(pDilution));
		
		/* Allocate the id */
		pList.setNewId(this);				
	}
	
	/**
	 * Compare this event to another to establish equality.
	 * 
	 * @param pThat The Event to compare to
	 * @return <code>true</code> if the event is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is an Event */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a Event */
		Event myThat = (Event)pThat;
		
		/* Check for equality on id */
		if (getId() != myThat.getId())	return false;
		
		/* Compare the changeable values */
		return getValues().histEquals(myThat.getValues()).isIdentical();
	}

	/**
	 * Compare this event to another to establish sort order. 
	 * @param pThat The Event to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		int iDiff;
		
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is an Event */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as an Event */
		Event myThat = (Event)pThat;

		/* If the dates differ */
		if (this.getDate() != myThat.getDate()) {
			/* Handle null dates */
			if (this.getDate() == null) return 1;
			if (myThat.getDate() == null) return -1;
			
			/* Compare the dates */
			iDiff = getDate().compareTo(myThat.getDate());
			if (iDiff != 0) return iDiff;
		}
		
		/* If the transaction types differ */
		if (this.getTransType() != myThat.getTransType()) {
			/* Handle nulls */
			if (this.getTransType() == null) return 1;
			if (myThat.getTransType() == null) return -1;
			
			/* Compare transaction types */
			iDiff = getTransType().compareTo(myThat.getTransType());
			if (iDiff != 0) return iDiff;
		}
		
		/* If the descriptions differ */
		if (this.getDesc() != myThat.getDesc()) {
			/* Handle null descriptions */
			if (this.getDesc() == null) return 1;
			if (myThat.getDesc() == null) return -1;
			
			/* Compare the descriptions */
			iDiff = getDesc().compareTo(myThat.getDesc());
			if (iDiff < 0) return -1;
			if (iDiff > 0) return 1;
		}
		
		/* Compare ids */
		iDiff = (int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}

	/**
	 * Rebuild Links to partner data
	 * @param pData the DataSet
	 */
	protected void reBuildLinks(FinanceData pData) {
		/* Update the Encryption details */
		super.reBuildLinks(pData);
		
		/* Access Lists */
		Account.List 			myAccounts 		= pData.getAccounts();
		TransactionType.List 	myTranTypes		= pData.getTransTypes();
		
		/* Update credit to use the local copy of the Accounts */
		Values 	myValues   	= getValues();
		Account	myAct		= myValues.getCredit();
		Account	myNewAct 	= myAccounts.searchFor(myAct.getId());
		myValues.setCredit(myNewAct);

		/* Update debit to use the local copy of the Accounts */
		myAct		= myValues.getDebit();
		myNewAct 	= myAccounts.searchFor(myAct.getId());
		myValues.setDebit(myNewAct);

		/* Update transtype to use the local copy */
		TransactionType	myTran		= myValues.getTransType();
		TransactionType	myNewTran 	= myTranTypes.searchFor(myTran.getId());
		myValues.setTransType(myNewTran);
	}

	/**
	 * Add additional fields to HTML String
	 * @param pDetail the debug detail
	 * @param pBuffer the string buffer 
	 */
	public void addHTMLFields(DebugDetail 	pDetail, 
							  StringBuilder pBuffer) {
		/* If we have any Event Info */
		if (theInfoSet != null) {
			/* Add the detail */
			theInfoSet.addHTMLFields(pDetail, pBuffer);
		}
	}
	
	/**
	 * Determines whether an event can be valid
	 * 
	 * @param pTrans The transaction type of the event
	 * @param pType The account type of the event
	 * @param isCredit is the account a credit or a debit
	 * @return valid true/false 
	 */
	public static boolean isValidEvent(TransactionType  pTrans,
			                     	   AccountType		pType,
			                           boolean          isCredit) {
		boolean myResult = false;

		/* Market is always false */
		if (pType.isMarket())
			return false;
		
		/* Switch on the TransType */
		switch (pTrans.getTranClass()) {
			case TAXFREEINCOME:
				if (!isCredit) myResult = (pType.isExternal() && !pType.isCash());
				else           myResult = !pType.isExternal();
				break;
			case TAXABLEGAIN:
				if (!isCredit) myResult = pType.isLifeBond();
				else           myResult = pType.isMoney();
				break;
			case ADMINCHARGE:
				myResult = pType.isLifeBond();
				break;
			case DIVIDEND:
				if (!isCredit) myResult = pType.isDividend();
				else           myResult = (pType.isMoney() || pType.isCapital() || pType.isDeferred());
				break;
			case STOCKDEMERGER:
			case STOCKSPLIT:
			case STOCKTAKEOVER:
				myResult = pType.isShares();
				break;
			case STOCKRIGHTWAIVED:
			case CASHTAKEOVER:
				isCredit = !isCredit;
			case STOCKRIGHTTAKEN:
				if (!isCredit) myResult = (pType.isMoney() || pType.isDeferred());
				else           myResult = pType.isShares();
				break;
			case INTEREST:
				if (!isCredit) myResult = pType.isMoney();
				else           myResult = pType.isMoney();
				break;
			case TAXEDINCOME:
				if (!isCredit) myResult = pType.isEmployer();
				else           myResult = ((pType.isMoney()) || (pType.isDeferred()));
				break;
			case NATINSURANCE:
				if (!isCredit) myResult = pType.isEmployer();
				else           myResult = pType.isTaxMan();
				break;
			case TRANSFER:
				myResult = !pType.isExternal();
				if (isCredit) myResult &= !pType.isEndowment();
				break;
			case ENDOWMENT:
				if (!isCredit) myResult = (pType.isMoney() || pType.isDebt());
				else myResult = pType.isEndowment();
				break;
			case CASHPAYMENT:
				isCredit = !isCredit;
			case CASHRECOVERY:
				if (!isCredit) myResult = ((pType.isExternal()) && (!pType.isCash()));
				else           myResult = pType.isCash();
				break;
			case INHERITED:
				if (!isCredit) myResult = pType.isInheritance();
				else           myResult = !pType.isExternal();
				break;
			case BENEFIT:
				if (!isCredit) myResult = pType.isEmployer();
				else           myResult = pType.isBenefit();
				break;
			case RECOVERED:
				isCredit = !isCredit;
			case EXPENSE:
				if (!isCredit) myResult = !pType.isExternal();
				else           myResult = pType.isExternal();
				break;
			case EXTRATAX:
			case INSURANCE:
				if (!isCredit) myResult = (pType.isMoney() || pType.isDebt());
				else           myResult = (pType.isExternal() && !pType.isCash());
				break;
			case MORTGAGE:
				if (!isCredit) myResult = pType.isDebt();
				else           myResult = (pType.isExternal() && !pType.isCash());
				break;
			case TAXREFUND:
				isCredit = !isCredit;
			case TAXOWED:
				if (!isCredit) myResult = (pType.isMoney() || pType.isDeferred());
				else           myResult = pType.isTaxMan();
				break;
			case TAXRELIEF:
				if (!isCredit) myResult = pType.isTaxMan();
				else           myResult = pType.isDebt();
				break;
			case DEBTINTEREST:
				if (!isCredit) myResult = (pType.isExternal() && !pType.isCash());
				else           myResult = pType.isDebt();
				break;
			case WRITEOFF:
				if (!isCredit) myResult = pType.isDebt();
				else           myResult = pType.isWriteOff();
				break;
			case RENTALINCOME:
				if (!isCredit) myResult = (pType.isExternal() && !pType.isCash());
				else           myResult = pType.isDebt();
				break;
			default:
				break;
		}
		
		/* Return the result */
		return myResult;
	}
	
	/**
	 * Is an event allowed between these two accounts, used for more detailed analysis once the event is
	 * deemed valid based on the account types
	 * @param pTrans The transaction type of the event
	 * @param pDebit the debit account
	 * @param pCredit the credit account
	 * @return true/false 
	 */
	public static boolean isValidEvent(TransactionType pTrans, Account pDebit, Account pCredit) {
		boolean myResult;
		
		/* Generally we must not be recursive */
		myResult = Account.differs(pDebit, pCredit).isDifferent();
		
		/* Switch on the TransType */
		switch (pTrans.getTranClass()) {
			/* Dividend */
			case DIVIDEND:
				/* If the credit account is capital */
				if (pCredit.isCapital()) {
					/* Debit and credit accounts must be identical */
					myResult = !myResult;
				}
				break;
			/* AdminCharge/StockSplit */
			case ADMINCHARGE:
			case STOCKSPLIT:
				/* Debit and credit accounts must be identical */
				myResult = !myResult;
				break;
			/* Interest can be recursive */
			case INTEREST:
				myResult = true;
				break;
			/* Debt Interest and Rental Income must come from the owner of the debt */
			case RENTALINCOME:
			case DEBTINTEREST:
				myResult = !Account.differs(pDebit, pCredit.getParent()).isDifferent();
				break;
			/* Mortgage payment must be to the owner of the mortgage */
			case MORTGAGE:
				myResult = !Account.differs(pCredit, pDebit.getParent()).isDifferent();
				break;
		}
		
		/* Return the result */
		return myResult;
	}
	
	/**
	 * Validate the event
	 */
	public void validate() {
		List 			myList		= (List)getList();
		Date 			myDate		= getDate();
		String			myDesc		= getDesc();
		Account			myDebit		= getDebit();
		Account			myCredit	= getCredit();
		Money			myAmount	= getAmount();
		TransactionType myTransType	= getTransType();
		Units			myUnits		= getUnits();
		Money			myTaxCred	= getTaxCredit();
		Integer			myYears		= getYears();
		Dilution        myDilution	= getDilution();
				
		/* The date must be non-null */
		if ((myDate == null) || (myDate.isNull())) {
			addError("Null date is not allowed", FIELD_DATE);
		}
			
		/* The date must be in-range */
		else if (myList.getRange().compareTo(myDate) != 0) {
			addError("Date must be within range", FIELD_DATE);
		}
			
		/* Debit must be non-null */
		if (myDebit == null) {
			addError("Debit account must be non-null", FIELD_DEBIT);
		}
			
		/* Credit must be non-null */
		if (myCredit == null) {
			addError("Credit account must be non-null", FIELD_CREDIT);
		}
			
		/* TransType must be non-null */
		if (myTransType == null) {
			addError("TransType must be non-null", FIELD_TRNTYP);
		}
		else if (!myTransType.getEnabled()) 
			addError("TransType must be enabled", FIELD_TRNTYP);
			
		/* The description must be non-null */
		if (myDesc == null) {
			addError("Description must be non-null", FIELD_DESC);
		}
			
		/* The description must not be too long */
		else if (myDesc.length() > DESCLEN) {
			addError("Description is too long", FIELD_DESC);
		}
			
		/* Hidden Events are not allowed */
		if ((myTransType != null) && (myTransType.isHiddenType())) {
			addError("Hidden transaction types are not allowed", FIELD_TRNTYP);
		}
		
		/* Check credit account */
		if ((myTransType != null) &&	(myCredit != null) &&
			(!isValidEvent(myTransType, myCredit.getActType(), true)))
				addError("Invalid credit account for transaction", FIELD_CREDIT);
		
		/* Check debit account */
		if ((myTransType != null) &&	(myDebit != null) &&
			(!isValidEvent(myTransType, myDebit.getActType(), false)))
				addError("Invalid debit account for transaction", FIELD_DEBIT);
		
		/* Check valid Credit/Debit combination */
		if ((myTransType != null) && (myCredit != null) && (myDebit != null) &&
 			(!isValidEvent(myTransType, myDebit, myCredit))) {
				addError("Invalid Debit/Credit combination account for transaction", FIELD_DEBIT);
				addError("Invalid Debit/Credit combination account for transaction", FIELD_CREDIT);
		}
		
		/* Money must not be null/negative */
		if (myAmount == null) 
			addError("Amount must be non-null", 
				     FIELD_AMOUNT);
		else if (!myAmount.isPositive()) 
			addError("Amount cannot be negative", 
					 FIELD_AMOUNT);
		
		/* Money must be zero for stock split/demerger */
		if ((myAmount != null) &&
			(myAmount.isNonZero()) &&
			(myTransType != null) &&
			((myTransType.isStockDemerger()) ||
			 (myTransType.isStockSplit()) ||
			 (myTransType.isStockTakeover()))) { 
			addError("Amount must be zero for Stock Split/Demerger/Takeover", 
					 FIELD_AMOUNT);
		}
		
		/* Ignore remaining checks for Patterns */
		if (!(this instanceof Pattern)) {
			/* Check for valid priced credit account */
			if ((myCredit != null) && (myCredit.isPriced())) {
				/* If the date of this event is prior to the first price */
				if ((myCredit.getInitPrice() != null) &&
				    (getDate().compareTo(myCredit.getInitPrice().getDate()) < 0))
					addError("Event Date is prior to first priced date for Credit Account", FIELD_DATE);
			}

			/* Check for valid priced debit account */
			if ((myDebit != null) && (myDebit.isPriced()) &&
				(Account.differs(myCredit, myDebit).isDifferent())) {
				/* If the date of this event is prior to the first price */
				if ((myDebit.getInitPrice() != null) &&
					(getDate().compareTo(myDebit.getInitPrice().getDate()) < 0))
					addError("Event Date is prior to first priced date for Debit Account", FIELD_DATE);
			}
		
			/* If we have units */
			if (myUnits != null) { 
				/* If we have credit/debit accounts */
				if ((myDebit != null) && (myCredit != null)) {				
					/* Units are only allowed if credit or debit is priced */
					if ((!myCredit.isPriced()) && (!myDebit.isPriced())) {
						addError("Units are only allowed involving assets", 
							 FIELD_UNITS);
					}

					/* If both credit/debit are both priced */
					if ((myCredit.isPriced()) && (myDebit.isPriced())) {
						/* TranType must be stock split or dividend between same account */
						if ((myTransType == null) ||
							((!myTransType.isDividend()) &&
							 (!myTransType.isStockSplit()) &&
							 (!myTransType.isAdminCharge()) &&
							 (!myTransType.isStockDemerger()) &&
							 (!myTransType.isStockTakeover()))) { 
							addError("Units can only refer to a single priced asset unless " +
									 "transaction is StockSplit/AdminCharge/Demerger/Takeover or Dividend", 
									 FIELD_UNITS);
						}
						
						/* Dividend between priced requires identical credit/debit */
						if ((myTransType != null) &&
							(myTransType.isDividend()) &&
							(Account.differs(myCredit, myDebit).isDifferent())) {
							addError("Unit Dividends between assets must be between same asset", 
									 FIELD_UNITS);
						}
					}
				}
			
				/* Units must be non-zero */
				if (!myUnits.isNonZero()) { 
					addError("Units must be non-Zero", FIELD_UNITS);
				}
			
				/* Units must not be negative unless it is stock split */
				if ((!myUnits.isPositive()) &&
					((myTransType == null) ||
					((!myTransType.isStockSplit()) &&
					 (!myTransType.isAdminCharge())))) {  
					addError("Units must be positive unless this is a StockSplit/AdminCharge", FIELD_UNITS);
				}
			}
		
			/* Else check for required units */
			else {
				if (isStockSplit()) 
					addError("Stock Split requires non-zero Units", FIELD_UNITS);
				else if (isAdminCharge()) 
					addError("Admin Charge requires non-zero Units", FIELD_UNITS);
			}
		
			/* If we have a dilution */
			if (myDilution != null) {
				/* If the dilution is not allowed */
				if ((!needsDilution(myTransType)) && (!myTransType.isStockSplit()))
					addError("Dilution factor given where not allowed", 
							 FIELD_DILUTION);			

				/* If the dilution is out of range */
				if (myDilution.outOfRange())
					addError("Dilution factor value is outside allowed range (0-1)", 
							 FIELD_DILUTION);			
			}
		
			/* else if we are missing a required dilution factor */
			else if (needsDilution(myTransType)) {
				addError("Dilution factor missing where required", 
						 FIELD_DILUTION);						
			}
		
			/* If we are a taxable gain */
			if ((myTransType != null) && (myTransType.isTaxableGain())) {
				/* Years must be positive */
				if ((myYears == null) || (myYears <= 0)) {
					addError("Years must be non-zero and positive", FIELD_YEARS);
				}
			
				/* Tax Credit must be non-null and positive */
				if ((myTaxCred == null) || (!myTaxCred.isPositive())) {
					addError("TaxCredit must be non-null", FIELD_TAXCREDIT);
				}
			}
		
			/* If we need a tax credit */
			else if ((myTransType != null) && (needsTaxCredit(myTransType, 
														      myDebit))) {
				/* Tax Credit must be non-null and positive */
				if ((myTaxCred == null) || (!myTaxCred.isPositive())) {
					addError("TaxCredit must be non-null", FIELD_TAXCREDIT);
				}

				/* Years must be null */
				if (myYears != null) {
					addError("Years must be null", FIELD_YEARS);
				}
			}
		
			/* else we should not have a tax credit */
			else if (myTransType != null) {
				/* Tax Credit must be null */
				if (myTaxCred != null) {
					addError("TaxCredit must be null", FIELD_TAXCREDIT);
				}

				/* Years must be null */
				if (myYears != null) {
					addError("Years must be null", FIELD_YEARS);
				}
			}
		}
		
		/* Set validation flag */
		if (!hasErrors()) setValidEdit();
	}
	
	/**
	 * Determines whether an event relates to an account
	 * 
	 * @param pAccount The account to check relations with
	 * @return related to the account true/false 
	 */
	public boolean relatesTo(Account pAccount) {
		boolean myResult = false;
	
		/* Check credit and debit accounts */
		if (getCredit().compareTo(pAccount) == 0) myResult = true;
		else if (getDebit().compareTo(pAccount) == 0) myResult = true;
			
		/* Return the result */
		return myResult;
	}
	
	/**
	 * Determines whether an event is asset related
	 * 
	 * @return asset-related to the account true/false 
	 */
	public boolean isAssetRelated() {
		boolean myResult = false;
		
		/* Check credit and debit accounts */
		if (!getCredit().isExternal()) myResult = true;
		else if (!getDebit().isExternal()) myResult = true;
			
		/* Return the result */
		return myResult;
	}
	
	/**
	 * Determines whether a line is locked to updates
	 * 
	 * @return true/false 
	 */
	public boolean isLocked() {
		Account myCredit = getCredit();
		Account myDebit  = getDebit();
	
		/* Check credit and debit accounts */
		return (((myCredit != null) && (myCredit.isClosed())) ||
				((myDebit != null) && (myDebit.isClosed())));
	}
		
	/**
	 * Determines whether an event is a dividend re-investment
	 * 
	 * @return dividend re-investment true/false 
	 */
	public boolean isDividendReInvestment() {
		boolean myResult = false;
	
		/* Check for dividend re-investment */
		if ((getTransType() != null) &&
		    (getTransType().isDividend()) &&
		    (getCredit() != null) &&
			(getCredit().isPriced()))
			myResult = true;
				
		/* Return the result */
		return myResult;
	}

	/**
	 * Determines whether an event is an interest payment
	 * 
	 * @return interest true/false 
	 */
	public boolean isInterest() {
		boolean myResult = false;
	
		/* Check for interest */
		if ((getTransType() != null) &&
		    (getTransType().isInterest()))
			myResult = true;
				
		/* Return the result */
		return myResult;
	}

	/**
	 * Determines whether an event is a stock split
	 * 
	 * @return stock split true/false 
	 */
	public boolean isStockSplit() {
		boolean myResult = false;
	
		/* Check for stock split */
		if ((getTransType() != null) &&
		    (getTransType().isStockSplit()))
			myResult = true;
				
		/* Return the result */
		return myResult;
	}

	/**
	 * Determines whether an event is an Admin Charge
	 * 
	 * @return admin charge true/false 
	 */
	public boolean isAdminCharge() {
		boolean myResult = false;
	
		/* Check for admin charge */
		if ((getTransType() != null) &&
		    (getTransType().isAdminCharge()))
			myResult = true;
				
		/* Return the result */
		return myResult;
	}

	/**
	 * Determines whether an event needs a tax credit
	 * 
	 * @return needs tax credit true/false 
	 */
	public static boolean needsTaxCredit(TransactionType 	pTrans,
								  		 Account		  	pDebit) {
		boolean myResult = false;
	
		/* Handle null transtype */
		if (pTrans == null) return myResult;
		
		/* Switch on transaction type */
		switch (pTrans.getTranClass()) {
			/* If this is a Taxable Gain/TaxedIncome we need a tax credit */
			case TAXABLEGAIN:
			case TAXEDINCOME:
				myResult = true;
				break;
			/* Check for dividend/interest */
			case DIVIDEND: 
			case INTEREST:
				myResult = (pDebit != null) && !pDebit.isTaxFree();
				break;
		}
		
		/* Return the result */
		return myResult;
	}

	/**
	 * Determines whether an event needs a dilution factor
	 * 
	 * @return needs dilution factor true/false 
	 */
	public static boolean needsDilution(TransactionType pTrans) {
		boolean myResult = false;
	
		/* Handle null transtype */
		if (pTrans == null) return myResult;
		
		/* Switch on transaction type */
		switch (pTrans.getTranClass()) {
			/* If this is a Stock Operation we need a dilution factor */
			case STOCKDEMERGER:
			case STOCKRIGHTTAKEN:
			case STOCKRIGHTWAIVED:
				myResult = true;
				break;
		}
		
		/* Return the result */
		return myResult;
	}

	/**
	 * Calculate the tax credit for an event
	 * @return the calculated tax credit
	 */
	public Money calculateTaxCredit() {
		FinanceData		myData	= ((List)getList()).getData(); 
		TaxYear.List 	myList  = myData.getTaxYears(); 
		TaxYear 		myTax;
		Rate			myRate;
		Money			myCredit;
		
		/* Ignore unless tax credit is null/zero */
		if ((getTaxCredit() != null) && (getTaxCredit().isNonZero()))
			return getTaxCredit();
		
		/* Ignore unless transaction type is interest/dividend */
		if ((getTransType() == null) ||
			((!getTransType().isInterest()) &&
			 (!getTransType().isDividend())))
			return getTaxCredit();
		
		/* Access the relevant tax year */
		myTax  = myList.searchFor(getDate());
		
		/* Determine the tax credit rate */
		if (getTransType().isInterest())
			myRate = myTax.getIntTaxRate();
		else
			myRate = myTax.getDivTaxRate();
		
		/* Calculate the tax credit */
		myCredit = getAmount().taxCreditAtRate(myRate);
		
		/* Return the tax credit */
		return myCredit;
	}

	/**
	 * Set a new debit account 
	 * 
	 * @param pDebit the debit account 
	 */
	public void setDebit(Account pDebit) {
		getValues().setDebit(pDebit);
	}
	
	/**
	 * Set a new credit account 
	 * 
	 * @param pCredit the credit account 
	 */
	public void setCredit(Account pCredit) {
		getValues().setCredit(pCredit);
	}
	
	/**
	 * Set a new transtype 
	 * 
	 * @param pTransType the transtype 
	 */
	public void setTransType(TransactionType pTransType) {
		getValues().setTransType(pTransType);
	}
	
	/**
	 * Set a new description 
	 * 
	 * @param pDesc the description 
	 */
	public void setDescription(String pDesc) throws Exception {
		if (pDesc != null) getValues().setDesc(new StringPair(pDesc));
		else 			   getValues().setDesc(null);
	}
	
	/**
	 * Set a new amount 
	 * 
	 * @param pAmount the amount 
	 */
	public void setAmount(Money pAmount) throws Exception {
		if (pAmount != null) getValues().setAmount(new MoneyPair(pAmount));
		else 				 getValues().setAmount(null);
	}
	
	/**
	 * Set a new units 
	 * 
	 * @param pUnits the units 
	 */
	public void setUnits(Units pUnits) throws Exception {
		if (pUnits != null) getValues().setUnits(new UnitsPair(pUnits));
		else 				getValues().setUnits(null);
	}
	
	/**
	 * Set a new date 
	 * 
	 * @param pDate the new date 
	 */
	public void setDate(Date pDate) {
		getValues().setDate((pDate == null) ? null : new Date(pDate));
	}

	/**
	 * Set a new tax credit amount 
	 * 
	 * @param pAmount the tax credit amount 
	 */
	public void setTaxCredit(Money pAmount) throws Exception {
		if (pAmount != null) getValues().setTaxCredit(new MoneyPair(pAmount));
		else 				 getValues().setTaxCredit(null);
	}
	
	/**
	 * Set a new years value 
	 * 
	 * @param pYears the years 
	 */
	public void setYears(Integer pYears) {
		getValues().setYears(pYears);
	}
	
	/**
	 * Set a new dilution value 
	 * 
	 * @param pDilution the dilution 
	 */
	public void setDilution(Dilution pDilution) throws Exception {
		if (pDilution != null) getValues().setDilution(new DilutionPair(pDilution));
		else 		   		   getValues().setDilution(null);
	}
	
	/**
	 * Update event from an element  
	 * @param pItem the changed element
	 * @return whether changes have been made 
	 */
	public boolean applyChanges(DataItem<?> pItem){
		boolean bChanged = false;
		if (pItem instanceof Event) {
			Event myEvent = (Event)pItem;
			bChanged = applyChanges(myEvent);
		}
		return bChanged;
	}
	
	/**
	 * Update event from an Event extract  
	 * @param pEvent the changed event 
	 * @return whether changes have been made
	 */
	private boolean applyChanges(Event pEvent) {
		Values	myValues	= getValues();
		Values	myNew		= pEvent.getValues();
		boolean bChanged	= false;

		/* Store the current detail into history */
		pushHistory();
		
		/* Update the date if required */
		if (Date.differs(getDate(), pEvent.getDate()).isDifferent()) 
			setDate(pEvent.getDate());
	
		/* Update the description if required */
		if (differs(myValues.getDesc(), myNew.getDesc()).isDifferent()) 
			myValues.setDesc(myNew.getDesc());
		
		/* Update the amount if required */
		if (differs(myValues.getAmount(), myNew.getAmount()).isDifferent()) 
			myValues.setAmount(myNew.getAmount());
		
		/* Update the units if required */
		if (differs(myValues.getUnits(), myNew.getUnits()).isDifferent()) 
			myValues.setUnits(myNew.getUnits());
				
		/* Update the tranType if required */
		if (TransactionType.differs(getTransType(), pEvent.getTransType()).isDifferent()) 
			setTransType(pEvent.getTransType());
	
		/* Update the debit if required */
		if (Account.differs(getDebit(), pEvent.getDebit()).isDifferent()) 
			setDebit(pEvent.getDebit());
	
		/* Update the credit if required */
		if (Account.differs(getCredit(), pEvent.getCredit()).isDifferent()) 
			setCredit(pEvent.getCredit());		
		
		/* Update the tax credit if required */
		if (differs(myValues.getTaxCredit(), myNew.getTaxCredit()).isDifferent()) 
			myValues.setTaxCredit(myNew.getTaxCredit());
	
		/* Update the dilution if required */
		if (differs(myValues.getDilution(), myNew.getDilution()).isDifferent()) 
			myValues.setDilution(myNew.getDilution());
				
		/* Update the years if required */
		if (Utils.differs(getYears(), pEvent.getYears()).isDifferent()) 
			setYears(pEvent.getYears());
		
		/* Check for changes */
		if (checkForHistory()) {
			/* Mark as changed */
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
	public static String format(Event pEvent) {
		String 	myFormat;
		myFormat = (pEvent != null) ? pEvent.getDesc()
							 	    : "null";
		return myFormat;
	}

	/**
	 *  List class for Events 
	 */
	public static class List extends EncryptedList<List, Event> {
		/* Members */
		private Date.Range  theRange   = null;

		/* Access DataSet correctly */
		public 		FinanceData getData() 	{ return (FinanceData) super.getData(); }
		protected 	Date.Range 	getRange() 	{ return theRange; }
		
		/* Set the range */
		protected void setRange(Date.Range pRange) { theRange = pRange; }
		
		/** 
	 	 * Construct an empty CORE event list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(FinanceData pData) { 
			super(List.class, Event.class, pData);
		}

		/**
		 * Constructor for a cloned List
		 * @param pSource the source List
		 */
		protected List(List pSource) { 
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
		public List getUpdateList() 	{ return getExtractList(ListStyle.UPDATE); }
		public List getEditList() 		{ return getExtractList(ListStyle.EDIT); }
		public List getShallowCopy() 	{ return getExtractList(ListStyle.COPY); }
		public List getDeepCopy(DataSet<?> pDataSet)	{ 
			/* Build an empty Extract List */
			List myList = new List(this);
			myList.setData(pDataSet);
			myList.setRange(theRange);
			
			/* Obtain underlying clones */
			myList.populateList(ListStyle.CLONE);
			myList.setStyle(ListStyle.CORE);
			
			/* Return the list */
			return myList;
		}

		/** 
		 * Construct a difference Event list
		 * @param pNew the new Event list 
		 * @param pOld the old Event list 
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
	 	 *  Get an EditList for a range
	 	 */
		public List getViewList() {
			/* Build an empty List */
			List myList = new List(this);
			
			/* Make this list the correct style */
			myList.setStyle(ListStyle.VIEW);

			/* Return it */
			return myList;
		}
		
	 	/**
	 	 *  Get an EditList for a range
	 	 *  @param pRange the range
	 	 */
		public List getEditList(Date.Range pRange) {
			/* Build an empty List */
			List myList = new List(this);
			
			/* Make this list the correct style */
			myList.setStyle(ListStyle.EDIT);

			/* local variable */
			Event.List.ListIterator 	myIterator;
			Event      					myCurr;
			Event      					myEvent;
			int       					myResult;
			
			/* Record range and initialise the list */
			myList.theRange   = pRange;
			
			/* Loop through the Events extracting relevant elements */
			myIterator 	= listIterator(true);
			while ((myCurr = myIterator.next()) != null) {
				/* Check the range */
				myResult = pRange.compareTo(myCurr.getDate());
				
				/* Handle out of range */
				if (myResult ==  1) continue;
				if (myResult == -1) break;
				
				/* Build the new linked event and add it to the extract */
				myEvent = new Event(myList, myCurr);
				myList.add(myEvent);
			}
			
			/* Return the List */
			return myList;
		}

	 	/**
	 	 *  Get an EditList for a new TaxYear
	 	 *  @param pTaxYear the new TaxYear
	 	 */
		public List getEditList(TaxYear pTaxYear) throws Exception {
			/* Build an empty List */
			List myList = new List(this);
			
			/* Make this list the correct style */
			myList.setStyle(ListStyle.EDIT);

			/* Local variables */
			Pattern.List.ListIterator 	myIterator;
			Pattern.List 				myPatterns;
			Event     					myCurr;
			Pattern						myPattern;
			Event       				myEvent;
			Date 						myDate;
			
			/* Record range and initialise the list */
			myList.theRange   = pTaxYear.getRange();
			
			/* Access the underlying data */
			myPatterns 	= getData().getPatterns();
			myIterator 	= myPatterns.listIterator();

			/* Loop through the Patterns */
			while ((myCurr = myIterator.next()) != null) {
				/* Access as pattern */
				myPattern = (Pattern)myCurr;
				
				/* Access a copy of the base date */
				myDate = new Date(myCurr.getDate());
					
				/* Loop while we have an event to add */
				while ((myEvent = myPattern.nextEvent(myList,
												      pTaxYear, 
												      myDate)) != null) {
					/* Add it to the extract */
					myList.add(myEvent);
				}
			}
			
			/* Return the List */
			return myList;
		}

		/**
		 * Add additional fields to HTML String
		 * @param pDetail the debug detail
		 * @param pBuffer the string buffer 
		 */
		public void addHTMLFields(DebugDetail pDetail, StringBuilder pBuffer) {
			/* If we have a Range */
			if (theRange != null) {
				/* Start the Fields section */
				pBuffer.append("<tr><th rowspan=\"2\">Fields</th></tr>");

				/* Format the range */
				pBuffer.append("<tr><td>Range</td><td>"); 
				pBuffer.append(Date.Range.format(theRange)); 
				pBuffer.append("</td></tr>");
			}
		}
		
		/** 
		 * Validate an extract
		 */
		public void validate() {
			Event.List.ListIterator 	myIterator;
			Event 						myCurr;

			/* Clear the errors */
			clearErrors();
			
			/* Access the underlying data */
			myIterator 	= listIterator();
			
			/* Loop through the lines */
			while ((myCurr = myIterator.next()) != null) {
				/* Validate it */
				myCurr.validate();
			}
		}
		
		/**
		 * Add a new item to the list
		 * @param pItem the item to add
		 * @return the newly added item
		 */
		public Event addNewItem(DataItem<?> pItem) {
			if (pItem instanceof Event) {
				Event myEvent = new Event(this, (Event)pItem);
				add(myEvent);
				return myEvent;
			}
			else if (pItem instanceof Statement.Line) {
				Event myEvent = new Event(this, (Statement.Line)pItem);
				add(myEvent);
				return myEvent;
			}
			else return null;
		}
	
		/**
		 * Add a new item to the edit list
		 * @return the newly added item
		 */
		public Event addNewItem() {
			/* Create a new Event */
			Event myEvent = new Event(this);
			
			/* Set the Date as the start of the range */
			myEvent.setDate(theRange.getStart());
			
			/* Add to list and return */
			add(myEvent);
			return myEvent;
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }
		
		/**
		 *  Allow an event to be added
		 */ 
		public void addItem(int				uId,
							java.util.Date	pDate,
				            String   		pDesc,
				            String   		pAmount,
				            String   		pDebit,
				            String   		pCredit,
				            String   		pUnits,
				            String   		pTransType,
				            String   		pTaxCredit,
				            String			pDilution,
				            Integer   		pYears) throws Exception {
			FinanceData		myData;
			Account.List	myAccounts;
			Account         myDebit;
			Account         myCredit;
			TransactionType	myTransType;
			Event			myEvent;
				
			/* Access the accounts */
			myData		 = getData();
			myAccounts   = myData.getAccounts();
			
			/* Look up the Transaction Type */
			myTransType = myData.getTransTypes().searchFor(pTransType);
			if (myTransType == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Event on [" + 
			                        Date.format(new Date(pDate)) +
			                        "] has invalid Transact Type [" + pTransType + "]");
			
			/* Look up the Credit Account */
			myCredit = myAccounts.searchFor(pCredit);
			if (myCredit == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Event on [" + 
			                        Date.format(new Date(pDate)) +
			                        "] has invalid Credit account [" + pCredit + "]");
			
			/* Look up the Debit Account */
			myDebit = myAccounts.searchFor(pDebit);
			if (myDebit == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Event on [" + 
			                        Date.format(new Date(pDate)) +
			                        "] has invalid Debit account [" + pDebit + "]");
			
			/* Create the new Event */
			myEvent = new Event(this, uId, pDate, pDesc,
					            myDebit, myCredit, myTransType, 
					            pAmount, pUnits, pTaxCredit,
					            pDilution, pYears);
			
			/* Validate the event */
			myEvent.validate();

			/* Handle validation failure */
			if (myEvent.hasErrors()) 
				throw new Exception(ExceptionClass.VALIDATE,
									myEvent,
									"Failed validation");
					
			/* Add the Event to the list */
			add(myEvent);
		}
			
		/**
		 *  Allow an event to be added
		 */
		public void addItem(int     		uId,
							int				uControlId,
				            java.util.Date  pDate,
				            byte[]   		pDesc,
				            byte[]   		pAmount,
				            int     		uDebitId,
				            int     		uCreditId,
				            byte[]   		pUnits,
				            int  	  		uTransId,
				            byte[]   		pTaxCredit,
				            byte[]			pDilution,
				            Integer    		pYears) throws Exception {
			Event	myEvent;
			
			/* Create the new Event */
			myEvent = new Event(this, uId, uControlId, pDate, pDesc,
					            uDebitId, uCreditId, uTransId, 
					            pAmount, pUnits, pTaxCredit,
					            pDilution, pYears);
			
			/* Check that this EventId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
									myEvent,
			  			            "Duplicate EventId");
			 
			/* Validate the event */
			myEvent.validate();

			/* Handle validation failure */
			if (myEvent.hasErrors()) 
				throw new Exception(ExceptionClass.VALIDATE,
									myEvent,
									"Failed validation");
					
			/* Add the Event to the list */
			add(myEvent);
		}		
	}
		
	/**
	 *  Values for an event 
	 */
	public class Values extends EncryptedValues {
		private Date       		theDate      = null;
		private StringPair      theDesc      = null;
		private MoneyPair  		theAmount    = null;
		private Account         theDebit     = null;
		private Account         theCredit    = null;
		private UnitsPair  		theUnits     = null;
		private TransactionType	theTransType = null;
		private MoneyPair  		theTaxCredit = null;
		private Integer         theYears     = null;
		private DilutionPair	theDilution  = null;
		private Integer			theDebitId	 = null;
		private Integer			theCreditId	 = null;
		private Integer			theTransId	 = null;
		
		/* Access methods */
		public Date       		getDate()      { return theDate; }
		public StringPair       getDesc()      { return theDesc; }
		public MoneyPair  		getAmount()    { return theAmount; }
		public Account          getDebit()     { return theDebit; }
		public Account          getCredit()    { return theCredit; }
		public UnitsPair   		getUnits()     { return theUnits; }
		public TransactionType	getTransType() { return theTransType; }
		public MoneyPair  		getTaxCredit() { return theTaxCredit; }
		public Integer          getYears()     { return theYears; }
		public DilutionPair     getDilution()  { return theDilution; }
		private Integer         getDebitId()   { return theDebitId; }
		private Integer         getCreditId()  { return theCreditId; }
		private Integer         getTransId()   { return theTransId; }
		
		/* Encrypted value access */
		public  Money		getAmountValue()    { return getPairValue(getAmount()); }
		public  String  	getDescValue()      { return getPairValue(getDesc()); }
		public  Money		getTaxCredValue()   { return getPairValue(getTaxCredit()); }
		public  Units		getUnitsValue()     { return getPairValue(getUnits()); }
		public  Dilution	getDilutionValue()  { return getPairValue(getDilution()); }

		/* Encrypted bytes access */
		public  byte[]	getAmountBytes()    { return getPairBytes(getAmount()); }
		public  byte[]  getDescBytes()      { return getPairBytes(getDesc()); }
		public  byte[]	getTaxCredBytes()   { return getPairBytes(getTaxCredit()); }
		public  byte[]	getUnitsBytes()     { return getPairBytes(getUnits()); }
		public  byte[]	getDilutionBytes()  { return getPairBytes(getDilution()); }

		public void setDate(Date pDate) {
			theDate      = pDate; }
		public void setDesc(StringPair pDesc) {
			theDesc      = pDesc; }
		public void setAmount(MoneyPair pAmount) {
			theAmount    = pAmount; }
		public void setDebit(Account pDebit) {
			theDebit     = pDebit; 
			theDebitId   = (pDebit == null) ? null : pDebit.getId();
			adjustTaxCredit(); }
		public void setCredit(Account pCredit) {
			theCredit    = pCredit; 
			theCreditId  = (pCredit == null) ? null : pCredit.getId(); }
		public void setUnits(UnitsPair pUnits) {
			theUnits     = pUnits; }
		public void setTransType(TransactionType pTransType) {
			theTransType = pTransType; 
			theTransId   = (pTransType == null) ? null : pTransType.getId();
			adjustTaxCredit(); }
		public void setTaxCredit(MoneyPair pTaxCredit) {
			theTaxCredit = pTaxCredit; }
		public void setYears(Integer iYears) {
			theYears     = iYears; }
		public void setDilution(DilutionPair pDilution) {
			theDilution  = pDilution; }
		private void setDebitId(Integer pDebitId) {
			theDebitId   = pDebitId; } 
		private void setCreditId(Integer pCreditId) {
			theCreditId  = pCreditId; } 
		private void setTransId(Integer pTransId) {
			theTransId   = pTransId; } 

		/* Constructor */
		public Values() {}
		public Values(Values 			pValues) { copyFrom(pValues); }
		public Values(Line.Values 		pValues) { copyFrom(pValues); }
		public Values(Pattern.Values 	pValues) { copyFrom(pValues); }
		
		/* Check whether this object is equal to that passed */
		public Difference histEquals(HistoryValues<Event> pCompare) {
			/* Make sure that the object is the same class */
			if (pCompare.getClass() != this.getClass()) return Difference.Different;
			
			/* Cast correctly */
			Values myValues = (Values)pCompare;

			/* Handle integer values */
			if ((Utils.differs(theDebitId,	myValues.theDebitId).isDifferent())		||
				(Utils.differs(theCreditId,	myValues.theCreditId).isDifferent())	||
				(Utils.differs(theTransId,	myValues.theTransId).isDifferent()))
				return Difference.Different;

			/* Determine underlying differences */
			Difference myDifference = super.histEquals(pCompare);
			
			/* Compare underlying values */
			myDifference = myDifference.combine(Date.differs(theDate,      				myValues.theDate));
			myDifference = myDifference.combine(differs(theDesc, 						myValues.theDesc));
			myDifference = myDifference.combine(differs(theAmount,    					myValues.theAmount));
			myDifference = myDifference.combine(differs(theUnits,     					myValues.theUnits));
			myDifference = myDifference.combine(Account.differs(theDebit,    			myValues.theDebit));
			myDifference = myDifference.combine(Account.differs(theCredit,    			myValues.theCredit));
			myDifference = myDifference.combine(TransactionType.differs(theTransType, 	myValues.theTransType));
			myDifference = myDifference.combine(differs(theTaxCredit,					myValues.theTaxCredit));
			myDifference = myDifference.combine(Utils.differs(theYears,     			myValues.theYears));
			myDifference = myDifference.combine(differs(theDilution,					myValues.theDilution));
			
			/* Return differences */
			return myDifference;
		}
		
		/* Copy values */
		public HistoryValues<Event> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			/* Handle an Event Values */
			if (pSource instanceof Values) {
				Values myValues = (Values)pSource;
				super.copyFrom(myValues);
				theDate      = myValues.getDate();
				theDesc      = myValues.getDesc();
				theAmount    = myValues.getAmount();
				theDebit     = myValues.getDebit();
				theCredit    = myValues.getCredit();
				theTransType = myValues.getTransType();
				theDebitId   = myValues.getDebitId();
				theCreditId  = myValues.getCreditId();
				theTransId   = myValues.getTransId();
				theUnits     = myValues.getUnits();
				theTaxCredit = myValues.getTaxCredit();
				theYears     = myValues.getYears();
				theDilution  = myValues.getDilution();
				
				/* If we are initialising from a pattern */
				if ((pSource instanceof Pattern.Values) &&
					(!(this instanceof Pattern.Values))) {
					/* If the event needs a Tax Credit */
					if (needsTaxCredit(theTransType, theDebit)) {
						/* Set a new null tax credit */
						try { theTaxCredit = new MoneyPair(new Money(0)); }
						catch (Exception e) {}
						
						/* If the event has tax years */
						if (theTransType.isTaxableGain()) {
							/* Set a new years value */
							theYears = new Integer(1);
						}
					}

					/* If the event needs dilution */
					if (needsDilution(theTransType)) {
						/* Set a null dilution value */
						try { theDilution = new DilutionPair(new Dilution(Dilution.MAX_VALUE)); }
						catch (Exception e) {}
					}									
				}
			}
		}
		
		/**
		 * Adjust TaxCredit values on change of Debit/TransactionType
		 */
		private void adjustTaxCredit() {
			/* Ignore if we are Pattern.Values */
			if (this instanceof Pattern.Values) return;
			
			/* Determine state */
			boolean needsTaxCredit = needsTaxCredit(theTransType, theDebit);
			boolean nullTaxCredit  = (theTaxCredit == null);
			
			try {
				if ((needsTaxCredit) && (nullTaxCredit))
					theTaxCredit = new MoneyPair(new Money(0));
				else if ((!needsTaxCredit) && (!nullTaxCredit))
					theTaxCredit = null;
			} catch (Exception e) {}
		}

		public Difference	fieldChanged(int fieldNo, HistoryValues<Event> pOriginal) {
			Values 		pValues = (Values)pOriginal;
			Difference	bResult = Difference.Identical;
			switch (fieldNo) {
				case FIELD_DATE:
					bResult = (Date.differs(theDate,		      	pValues.theDate));
					break;
				case FIELD_DESC:
					bResult = (differs(theDesc,      				pValues.theDesc));
					break;
				case FIELD_TRNTYP:
					bResult = (TransactionType.differs(theTransType, pValues.theTransType));
					break;
				case FIELD_AMOUNT:
					bResult = (differs(theAmount,    				pValues.theAmount));
					break;
				case FIELD_DEBIT:
					bResult = (Account.differs(theDebit,			pValues.theDebit));
					break;
				case FIELD_CREDIT:
					bResult = (Account.differs(theCredit,			pValues.theCredit));
					break;
				case FIELD_UNITS:
					bResult = (differs(theUnits,     				pValues.theUnits));
					break;
				case FIELD_TAXCREDIT:
					bResult = (differs(theTaxCredit, 				pValues.theTaxCredit));
					break;
				case FIELD_YEARS:
					bResult = (Utils.differs(theYears,				pValues.theYears));
					break;
				case FIELD_DILUTION:
					bResult = (differs(theDilution,  				pValues.theDilution));
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
			theDesc		= new StringPair(theDesc.getString());
			theAmount 	= new MoneyPair(theAmount.getValue());
			if (theUnits     != null) theUnits 		= new UnitsPair(theUnits.getValue());
			if (theTaxCredit != null) theTaxCredit 	= new MoneyPair(theTaxCredit.getValue());
			if (theDilution  != null) theDilution	= new DilutionPair(theDilution.getValue());
		}		
		
		/**
		 * Apply encryption after non-encrypted load
		 */
		protected void applySecurity() throws Exception {
			/* Apply the encryption */
			theDesc.encryptPair(null);
			theAmount.encryptPair(null);
			if (theUnits     != null) theUnits.encryptPair(null);
			if (theTaxCredit != null) theTaxCredit.encryptPair(null);
			if (theDilution  != null) theDilution.encryptPair(null);
		}		
		
		/**
		 * Adopt encryption from base
		 * @param pBase the Base values
		 */
		protected void adoptSecurity(ControlKey pControl, EncryptedValues pBase) throws Exception {
			Values myBase = (Values)pBase;

			/* Apply the encryption */
			theDesc.encryptPair(myBase.getDesc());
			theAmount.encryptPair(myBase.getAmount());
			if (theUnits     != null) theUnits.encryptPair(myBase.getUnits());
			if (theTaxCredit != null) theTaxCredit.encryptPair(myBase.getTaxCredit());
			if (theDilution  != null) theDilution.encryptPair(myBase.getDilution());
		}				
	}	
}
