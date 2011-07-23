package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.views.Statement.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.DataList.ListStyle;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.Number.*;

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

	/* Local IDs for use in loading */
	private int					theDebitId	= -1;
	private int					theCreditId	= -1;
	private int					theTransId	= -1;
	
	/* Access methods */
	public  Values         	getObj()       { return (Values)super.getObj(); }	
	public  Date      		getDate()      { return getObj().getDate(); }
	public  String          getDesc()      { return getPairValue(getObj().getDesc()); }
	public  Money     		getAmount()    { return getPairValue(getObj().getAmount()); }
	public  Account         getDebit()     { return getObj().getDebit(); }
	public  Account         getCredit()    { return getObj().getCredit(); }
	public  Units     		getUnits()     { return getPairValue(getObj().getUnits()); }
	public  TransactionType getTransType() { return getObj().getTransType(); }
	public  Money     		getTaxCredit() { return getPairValue(getObj().getTaxCredit()); }
	public  Integer	        getYears()     { return getObj().getYears(); }
	public  Dilution        getDilution()  { return getPairValue(getObj().getDilution()); }

	/* Encrypted value access */
	public  byte[]	getAmountBytes()    { return getPairBytes(getObj().getAmount()); }
	public  byte[]  getDescBytes()      { return getPairBytes(getObj().getDesc()); }
	public  byte[]	getUnitsBytes()     { return getPairBytes(getObj().getUnits()); }
	public  byte[]  getTaxCredBytes()   { return getPairBytes(getObj().getTaxCredit()); }
	public  byte[]  getDilutionBytes()  { return getPairBytes(getObj().getDilution()); }

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
	 * @param iField the field number
	 * @param pObj the values to use
	 * @return the formatted field
	 */
	public String formatField(int iField, histObject pObj) {
		String 	myString = "";
		Values 	myObj 	 = (Values)pObj;
		switch (iField) {
			case FIELD_DATE:	
				myString += Date.format(myObj.getDate()); 
				break;
			case FIELD_DESC:	
				myString += myObj.getDescValue(); 
				break;
			case FIELD_TRNTYP: 	
				if ((myObj.getTransType() == null) &&
					(theTransId != -1))
					myString += "Id=" + theTransId;
				else
					myString += TransactionType.format(myObj.getTransType());	
				break;
			case FIELD_DEBIT:
				if ((myObj.getDebit() == null) &&
					(theDebitId != -1))
					myString += "Id=" + theDebitId;
				else
					myString += Account.format(myObj.getDebit()); 
				break;
			case FIELD_CREDIT:	
				if ((myObj.getCredit() == null) &&
					(theCreditId != -1))
					myString += "Id=" + theCreditId;
				else
					myString += Account.format(myObj.getCredit()); 
				break;
			case FIELD_AMOUNT: 	
				myString += Money.format(myObj.getAmountValue());	
				break;
			case FIELD_UNITS: 	
				myString += Units.format(myObj.getUnitsValue());	
				break;
			case FIELD_TAXCREDIT:	
				myString += Money.format(myObj.getTaxCredValue()); 
				break;
			case FIELD_YEARS:	
				myString += myObj.getYears(); 
				break;
			case FIELD_DILUTION:	
				myString += Dilution.format(myObj.getDilutionValue()); 
				break;
			case FIELD_ID: 		
				myString += super.formatField(iField, pObj); 
				break;
		}
		return myString;
	}
							
	/**
	 * Construct a copy of an Event
	 * 
	 * @param pEvent The Event to copy 
	 */
	public Event(List pList, Event pEvent) {
		/* Set standard values */
		super(pList, pEvent.getId()); 
		setObj(new Values(pEvent.getObj()));
		setControlKey(pEvent.getControlKey());

		/* Switch on the ListStyle */
		switch (pList.getStyle()) {
			case EDIT:
				if (pEvent.getList().getStyle() != ListStyle.EDIT) {
					setBase(pEvent);
					pList.setNewId(this);				
					break;
				}
				/* Fall Through for duplicating item */
			case CORE:
				/* Force new id for item */
				setId(0);
				pList.setNewId(this);				
				break;
			case UPDATE:
				setBase(pEvent);
				setState(pEvent.getState());
				break;
		}
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
		Values 					myObj 	= new Values();
		Statement.Line.Values	myBase	= pLine.getObj();
		setObj(myObj);
		setControlKey(pLine.getControlKey());
		myObj.setDate(pLine.getDate());
		myObj.setDesc(new StringPair(myBase.getDesc()));
		myObj.setAmount(new MoneyPair(myBase.getAmount()));
		if (myBase.getUnits() != null)
			myObj.setUnits(new UnitsPair(myBase.getUnits()));
		myObj.setTransType(pLine.getTransType());
		if (myBase.getDilution() != null)
			myObj.setDilution(new DilutionPair(myBase.getDilution()));
		if (myBase.getTaxCredit() != null)
			myObj.setTaxCredit(new MoneyPair(myBase.getTaxCredit()));
		myObj.setYears(pLine.getYears());
		
		/* If this is a credit */
		if (pLine.isCredit()) {
			myObj.setCredit(pLine.getAccount());
			myObj.setDebit(pLine.getPartner());
		}
		
		/* else this is a debit */
		else {
			myObj.setDebit(pLine.getAccount());
			myObj.setCredit(pLine.getPartner());
		}
			
		/* Allocate the id */
		pList.setNewId(this);				
	}
	
	/**
	 * Construct a new event from an Account pattern
	 * 
	 * @param pList the list to build into
	 * @param pLine The Line to copy 
	 */
	protected Event(List   	pList,
		            Pattern pLine) throws Exception {
		/* Set standard values */
		super(pList, 0);
		Values 			myObj	= new Values();
		Pattern.Values 	myBase 	= pLine.getObj();
		setObj(myObj);
		setControlKey(pLine.getControlKey());
		myObj.setDate(pLine.getDate());
		myObj.setDesc(new StringPair(myBase.getDesc()));
		myObj.setAmount(new MoneyPair(myBase.getAmount()));
		myObj.setTransType(pLine.getTransType());
	
		/* If this is a credit */
		if (pLine.isCredit()) {
			myObj.setCredit(pLine.getAccount());
			myObj.setDebit(pLine.getPartner());
		}
		
		/* else this is a debit */
		else {
			myObj.setDebit(pLine.getAccount());
			myObj.setCredit(pLine.getPartner());
		}
			
		/* If the event needs a Tax Credit */
		if (needsTaxCredit()) {
			/* Set a new null tax credit */
			myObj.setTaxCredit(new MoneyPair(new Money(0)));
			
			/* If the event has tax years */
			if (getTransType().isTaxableGain()) {
				/* Set a new years value */
				myObj.setYears(new Integer(1));
			}
		}

		/* If the event needs dilution */
		if (needsDilution()) {
			/* Set a null dilution value */
			myObj.setDilution(new DilutionPair(new Dilution(Dilution.MAX_VALUE)));
		}
		
		/* Allocate the id */
		pList.setNewId(this);				
	}
	
	/* Standard constructor for a newly inserted event */
	public Event(List pList) {
		super(pList, 0);
		Values theObj = new Values();
		setObj(theObj);
		setControlKey(pList.getData().getControl().getControlKey());
		pList.setNewId(this);				
	}

	/* Standard constructor */
	private Event(List      		pList,
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
		DataSet	myData 	= pList.getData();
		myAccounts = myData.getAccounts();
		
		/* Create a new EventValues object */
		Values myObj = new Values();
		setObj(myObj);

		/* Store the IDs that we will look up */
		theDebitId  = uDebit;
		theCreditId = uCredit;
		theTransId	= uTransType;
		setControlKey(uControlId);
		
		/* Create the date */
		myObj.setDate(new Date(pDate));
		
		/* Look up the Debit Account */
		myAccount = myAccounts.searchFor(uDebit);
		if (myAccount == null)
			throw new Exception(ExceptionClass.DATA,
   					  			this, 
   					  			"Invalid Debit Account Id");
		myObj.setDebit(myAccount);
		
		/* Look up the Debit Account */
		myAccount = myAccounts.searchFor(uCredit);
		if (myAccount == null)
			throw new Exception(ExceptionClass.DATA,
   					  			this, 
   					  			"Invalid Credit Account Id");
		myObj.setCredit(myAccount);
		
		/* Look up the Transaction Type */
		myTransType = myData.getTransTypes().searchFor(uTransType);
		if (myTransType == null)
			throw new Exception(ExceptionClass.DATA,
   					  			this, 
   					  			"Invalid Transaction Type Id");
		myObj.setTransType(myTransType);
		
		/* Set the years */
		myObj.setYears(pYears);
		
		/* Record the encrypted values */
		myObj.setDesc(new StringPair(pDesc));
		myObj.setAmount(new MoneyPair(pAmount));
		if (pUnits != null) myObj.setUnits(new UnitsPair(pUnits));
		if (pTaxCredit != null) myObj.setTaxCredit(new MoneyPair(pTaxCredit));
		if (pDilution != null) myObj.setDilution(new DilutionPair(pDilution));

		/* Allocate the id */
		pList.setNewId(this);				
	}
	
	/* Standard constructor */
	private Event(List      		pList,
		          java.util.Date 	pDate,
		          String         	pDesc,
		          Account          	pDebit,
		          Account        	pCredit,
		          TransactionType	pTransType,
		          String     		pAmount,
		          String			pUnits,
		          String			pTaxCredit,
		          String			pDilution,
		          Integer			pYears) throws Exception {
		/* Initialise item */
		super(pList, 0);
		
		/* Create a new EventValues object */
		Values myObj = new Values();
		setObj(myObj);

		/* Record the encrypted values */
		myObj.setDesc(new StringPair(pDesc));
		myObj.setAmount(new MoneyPair(pAmount));
		if (pUnits != null) myObj.setUnits(new UnitsPair(pUnits));
		if (pTaxCredit != null) myObj.setTaxCredit(new MoneyPair(pTaxCredit));
		if (pDilution != null) myObj.setDilution(new DilutionPair(pDilution));
		myObj.setDebit(pDebit);
		myObj.setCredit(pCredit);
		myObj.setTransType(pTransType);
		myObj.setDate(new Date(pDate));
		myObj.setYears(pYears);
		
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
		return getObj().histEquals(myThat.getObj());
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
		myResult = Account.differs(pDebit, pCredit);
		
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
				myResult = !Account.differs(pDebit, pCredit.getParent());
				break;
			/* Mortgage payment must be to the owner of the mortgage */
			case MORTGAGE:
				myResult = !Account.differs(pCredit, pDebit.getParent());
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
		DataSet			mySet 		= myList.getData();
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
		else if (mySet.getDateRange().compareTo(myDate) != 0) {
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
		
		/* Check for valid priced credit account */
		if ((myCredit != null) && (myCredit.isPriced())) {
			/* If the date of this event is prior to the first price */
			if ((myCredit.getInitPrice() != null) &&
			    (getDate().compareTo(myCredit.getInitPrice().getDate()) < 0))
				addError("Event Date is prior to first priced date for Credit Account", FIELD_DATE);
		}
		
		/* Check for valid priced debit account */
		if ((myDebit != null) && (myDebit.isPriced()) &&
			(Account.differs(myCredit, myDebit))) {
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
						(Account.differs(myCredit, myDebit))) {
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
				addError("Units must positive unless this is a StockSplit/AdminCharge", FIELD_UNITS);
			}
		}
		
		/* Else check for required units */
		else {
			if (isStockSplit()) 
				addError("Stock Split requires non-zero Units", FIELD_UNITS);
			else if (isAdminCharge()) 
				addError("Admin Charge requires non-zero Units", FIELD_UNITS);
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
		
		/* If we have a dilution */
		if (myDilution != null) {
			/* If the dilution is not allowed */
			if ((!needsDilution()) && (!myTransType.isStockSplit()))
				addError("Dilution factor given where not allowed", 
						 FIELD_DILUTION);			

			/* If the dilution is out of range */
			if (myDilution.outOfRange())
				addError("Dilution factor value is outside allowed range (0-1)", 
						 FIELD_DILUTION);			
		}
		
		/* else if we are missing a required dilution factor */
		else if (needsDilution()) {
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
		else if ((myTransType != null) && (needsTaxCredit())) {
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
	public boolean needsTaxCredit() {
		boolean myResult = false;
	
		/* Handle null transtype */
		if (getTransType() == null) return myResult;
		
		/* Switch on transaction type */
		switch (getTransType().getTranClass()) {
			/* If this is a Taxable Gain/TaxedIncome we need a tax credit */
			case TAXABLEGAIN:
			case TAXEDINCOME:
				myResult = true;
				break;
			/* Check for dividend/interest */
			case DIVIDEND: 
			case INTEREST:
				myResult = (getDebit() != null) && !getDebit().isTaxFree();
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
	public boolean needsDilution() {
		boolean myResult = false;
	
		/* Handle null transtype */
		if (getTransType() == null) return myResult;
		
		/* Switch on transaction type */
		switch (getTransType().getTranClass()) {
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
		DataSet			myData	= ((List)getList()).getData(); 
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
		getObj().setDebit(pDebit);
	}
	
	/**
	 * Set a new credit account 
	 * 
	 * @param pCredit the credit account 
	 */
	public void setCredit(Account pCredit) {
		getObj().setCredit(pCredit);
	}
	
	/**
	 * Set a new transtype 
	 * 
	 * @param pTransType the transtype 
	 */
	public void setTransType(TransactionType pTransType) {
		getObj().setTransType(pTransType);
	}
	
	/**
	 * Set a new description 
	 * 
	 * @param pDesc the description 
	 */
	public void setDescription(String pDesc) throws Exception {
		if (pDesc != null) getObj().setDesc(new StringPair(pDesc));
		else 			   getObj().setDesc(null);
	}
	
	/**
	 * Set a new amount 
	 * 
	 * @param pAmount the amount 
	 */
	public void setAmount(Money pAmount) throws Exception {
		if (pAmount != null) getObj().setAmount(new MoneyPair(pAmount));
		else 				 getObj().setAmount(null);
	}
	
	/**
	 * Set a new units 
	 * 
	 * @param pUnits the units 
	 */
	public void setUnits(Units pUnits) throws Exception {
		if (pUnits != null) getObj().setUnits(new UnitsPair(pUnits));
		else 				getObj().setUnits(null);
	}
	
	/**
	 * Set a new date 
	 * 
	 * @param pDate the new date 
	 */
	public void setDate(Date pDate) {
		getObj().setDate((pDate == null) ? null : new Date(pDate));
	}

	/**
	 * Set a new tax credit amount 
	 * 
	 * @param pAmount the tax credit amount 
	 */
	public void setTaxCredit(Money pAmount) throws Exception {
		if (pAmount != null) getObj().setAmount(new MoneyPair(pAmount));
		else 				 getObj().setAmount(null);
	}
	
	/**
	 * Set a new years value 
	 * 
	 * @param pYears the years 
	 */
	public void setYears(Integer pYears) {
		getObj().setYears(pYears);
	}
	
	/**
	 * Set a new dilution value 
	 * 
	 * @param pDilution the dilution 
	 */
	public void setDilution(Dilution pDilution) throws Exception {
		if (pDilution != null) getObj().setDilution(new DilutionPair(pDilution));
		else 		   		   getObj().setDilution(null);
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
		else if (pItem instanceof Statement.Line) {
			Statement.Line myLine = (Statement.Line)pItem;
			bChanged = applyChanges(myLine);
		}
		return bChanged;
	}
	
	/**
	 * Update event from a Statement Line 
	 * @param pLine the changed line 
	 * @return whether changes have been made
	 */
	private boolean applyChanges(Statement.Line pLine) {
		Values					myObj		= getObj();
		Statement.Line.Values	myNew		= pLine.getObj();
		boolean					bChanged	= false;

		/* Store the current detail into history */
		pushHistory();
		
		/* Update the date if required */
		if (Date.differs(getDate(), pLine.getDate())) 
			setDate(pLine.getDate());
	
		/* Update the description if required */
		if (differs(myObj.getDesc(), myNew.getDesc()))
			myObj.setDesc(new StringPair(myNew.getDesc()));
		
		/* Update the amount if required */
		if (differs(myObj.getAmount(), myNew.getAmount())) 
			myObj.setAmount(new MoneyPair(myNew.getAmount()));
		
		/* Update the units if required */
		if (differs(myObj.getUnits(), myNew.getUnits())) 
			myObj.setUnits((myNew.getUnits() == null) ? null : new UnitsPair(myNew.getUnits()));
	
		/* Update the tranType if required */
		if (TransactionType.differs(getTransType(), pLine.getTransType())) 
			setTransType(pLine.getTransType());
	
		/* Update the tax credit if required */
		if (differs(myObj.getTaxCredit(), myNew.getTaxCredit())) 
			myObj.setTaxCredit((myNew.getTaxCredit() == null) ? null : new MoneyPair(myNew.getTaxCredit()));
	
		/* Update the years if required */
		if (Utils.differs(getYears(), pLine.getYears())) 
			setYears(pLine.getYears());
		
		/* Update the dilution if required */
		if (differs(myObj.getDilution(), myNew.getDilution())) 
			myObj.setDilution((myNew.getDilution() == null) ? null : new DilutionPair(myNew.getDilution()));
				
		/* If this is a credit */
		if (pLine.isCredit()) {			
			/* Update the debit if required */
			if (Account.differs(getDebit(), pLine.getPartner())) 
				setDebit(pLine.getPartner());
		} else {
			/* Update the credit if required */
			if (Account.differs(getCredit(), pLine.getPartner())) 
				setCredit(pLine.getPartner());
		}
		
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
	 * Update event from an Event extract  
	 * @param pEvent the changed event 
	 * @return whether changes have been made
	 */
	private boolean applyChanges(Event pEvent) {
		Values	myObj		= getObj();
		Values	myNew		= pEvent.getObj();
		boolean bChanged	= false;

		/* Store the current detail into history */
		pushHistory();
		
		/* Update the date if required */
		if (Date.differs(getDate(), pEvent.getDate())) 
			setDate(pEvent.getDate());
	
		/* Update the description if required */
		if (differs(myObj.getDesc(), myNew.getDesc())) 
			myObj.setDesc(myNew.getDesc());
		
		/* Update the amount if required */
		if (differs(myObj.getAmount(), myNew.getAmount())) 
			myObj.setAmount(myNew.getAmount());
		
		/* Update the units if required */
		if (differs(myObj.getUnits(), myNew.getUnits())) 
			myObj.setUnits(myNew.getUnits());
				
		/* Update the tranType if required */
		if (TransactionType.differs(getTransType(), pEvent.getTransType())) 
			setTransType(pEvent.getTransType());
	
		/* Update the debit if required */
		if (Account.differs(getDebit(), pEvent.getDebit())) 
			setDebit(pEvent.getDebit());
	
		/* Update the credit if required */
		if (Account.differs(getCredit(), pEvent.getCredit())) 
			setCredit(pEvent.getCredit());		
		
		/* Update the tax credit if required */
		if (differs(myObj.getTaxCredit(), myNew.getTaxCredit())) 
			myObj.setTaxCredit(myNew.getTaxCredit());
	
		/* Update the dilution if required */
		if (differs(myObj.getDilution(), myNew.getDilution())) 
			myObj.setDilution(myNew.getDilution());
				
		/* Update the years if required */
		if (Utils.differs(getYears(), pEvent.getYears())) 
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
	 *  List class for Events 
	 */
	public static class List extends EncryptedList<Event> {
		/** 
	 	 * Construct an empty CORE event list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(DataSet pData) { 
			super(Event.class, pData);
		}

		/** 
	 	 * Construct an empty generic event list
	 	 * @param pData the DataSet for the list
	 	 */
		public List(DataSet pData, ListStyle pStyle) { 
			super(Event.class, pData, pStyle);
		}

		/** 
	 	 * Construct a generic event list
	 	 * @param pList the source event list 
	 	 * @param pStyle the style of the list 
	 	 */
		public List(List pList, ListStyle pStyle) {
			super(Event.class, pList, pStyle);
		}

		/** 
	 	 * Construct a difference event list
	 	 * @param pNew the new Event list 
	 	 * @param pOld the old Event list 
	 	 */
		protected List(List pNew, List pOld) { 
			super(pNew, pOld);
		}
	
		/** 
	 	 * Clone an Event list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return new List(this, ListStyle.DIFFER); }
		
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
		 * @param isCredit - ignored
		 * @return the newly added item
		 */
		public Event addNewItem(boolean isCredit) {
			Event myEvent = new Event(this);
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
		public void addItem(java.util.Date	pDate,
				            String   		pDesc,
				            String   		pAmount,
				            String   		pDebit,
				            String   		pCredit,
				            String   		pUnits,
				            String   		pTransType,
				            String   		pTaxCredit,
				            String			pDilution,
				            Integer   		pYears) throws Exception {
			DataSet			myData;
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
			myEvent = new Event(this, pDate, pDesc,
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
			theDebit     = pDebit; }
		public void setCredit(Account pCredit) {
			theCredit    = pCredit; }
		public void setUnits(UnitsPair pUnits) {
			theUnits     = pUnits; }
		public void setTransType(TransactionType pTransType) {
			theTransType = pTransType; }
		public void setTaxCredit(MoneyPair pTaxCredit) {
			theTaxCredit = pTaxCredit; }
		public void setYears(Integer iYears) {
			theYears     = iYears; }
		public void setDilution(DilutionPair pDilution) {
			theDilution  = pDilution; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) {
			theDate      = pValues.getDate();
			theDesc      = pValues.getDesc();
			theAmount    = pValues.getAmount();
			theDebit     = pValues.getDebit();
			theCredit    = pValues.getCredit();
			theUnits     = pValues.getUnits();
			theTransType = pValues.getTransType();
			theTaxCredit = pValues.getTaxCredit();
			theYears     = pValues.getYears();
			theDilution  = pValues.getDilution();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (Date.differs(theDate,      				pValues.theDate))      return false;
			if (differs(theDesc, 						pValues.theDesc))      return false;
			if (differs(theAmount,    					pValues.theAmount))    return false;
			if (differs(theUnits,     					pValues.theUnits))     return false;
			if (Account.differs(theDebit,    			pValues.theDebit))     return false;
			if (Account.differs(theCredit,    			pValues.theCredit))    return false;
			if (TransactionType.differs(theTransType, 	pValues.theTransType)) return false;
			if (differs(theTaxCredit,					pValues.theTaxCredit)) return false;
			if (Utils.differs(theYears,     			pValues.theYears))	   return false;
			if (differs(theDilution,					pValues.theDilution))  return false;
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
			theDate      = pValues.getDate();
			theDesc      = pValues.getDesc();
			theAmount    = pValues.getAmount();
			theDebit     = pValues.getDebit();
			theCredit    = pValues.getCredit();
			theUnits     = pValues.getUnits();
			theTransType = pValues.getTransType();
			theTaxCredit = pValues.getTaxCredit();
			theYears     = pValues.getYears();
			theDilution  = pValues.getDilution();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean			bResult = false;
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
			}
			return bResult;
		}

		/**
		 * Ensure encryption after security change
		 */
		protected void applySecurity() throws Exception {
			/* Apply the encryption */
			theDesc.encryptPair();
			theAmount.encryptPair();
			if (theUnits     != null) theUnits.encryptPair();
			if (theTaxCredit != null) theTaxCredit.encryptPair();
			if (theDilution  != null) theDilution.encryptPair();
		}		
	}	
}
