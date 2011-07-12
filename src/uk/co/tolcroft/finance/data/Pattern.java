package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.DataItem.validationCtl.*;
import uk.co.tolcroft.models.EncryptedPair.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.finance.data.StaticClass.*;
import uk.co.tolcroft.finance.views.*;

public class Pattern extends DataItem {
	/**
	 * The name of the object
	 */
	public static final String objName = "Pattern";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";

	/**
	 * Pattern Description length
	 */
	public final static int DESCLEN 		= Event.DESCLEN;

	/* Local values */
	private int 	theAccountId	= -1;
	private int 	thePartnerId	= -1;
	private int 	theTransId		= -1;
	private int 	theFreqId		= -1;
			
	/* Access methods */
	public  Values         	getObj()       	{ return (Values)super.getObj(); }	
	public  Date        	getDate()      	{ return getObj().getDate(); }
	public  String          getDesc()      	{ return EncryptedPair.getPairValue(getObj().getDesc()); }
	public  Money       	getAmount()    	{ return EncryptedPair.getPairValue(getObj().getAmount()); }
	public  Account         getPartner()   	{ return getObj().getPartner(); }
	public  Frequency   	getFrequency() 	{ return getObj().getFrequency(); }
	public  TransactionType getTransType() 	{ return getObj().getTransType(); }
	public  AccountType 	getActType()   	{ return getAccount().getActType(); }
	public  boolean         isCredit()     	{ return getObj().isCredit(); }
	public  Account		   	getAccount()	{ return getObj().getAccount(); }
	private void         	setAccount(Account pAccount)   {
		getObj().setAccount(pAccount); }

	/* Encrypted value access */
	public  byte[]	getAmountBytes()    { return EncryptedPair.getPairBytes(getObj().getAmount()); }
	public  byte[]  getDescBytes()      { return EncryptedPair.getPairBytes(getObj().getDesc()); }

	/* Encrypted pair access */
	private MoneyPair	getAmountPair()    { return getObj().getAmount(); }
	private StringPair	getDescPair()      { return getObj().getDesc(); }

	/* Linking methods */
	public Pattern     getBase() { return (Pattern)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_ACCOUNT  = 1;
	public static final int FIELD_DATE     = 2;
	public static final int FIELD_DESC     = 3;
	public static final int FIELD_PARTNER  = 4;
	public static final int FIELD_AMOUNT   = 5;
	public static final int FIELD_TRNTYP   = 6;
	public static final int FIELD_FREQ     = 7;
	public static final int FIELD_CREDIT   = 8;
	public static final int NUMFIELDS	   = 9;
	
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
			case FIELD_ID:			return NAME_ID;
			case FIELD_ACCOUNT:		return "Account";
			case FIELD_DATE:		return "Date";
			case FIELD_DESC:		return "Description";
			case FIELD_PARTNER:		return "Partner";
			case FIELD_AMOUNT:		return "Amount";
			case FIELD_TRNTYP:		return "TransactionType";
			case FIELD_FREQ:		return "Frequency";
			case FIELD_CREDIT:		return "IsCredit";
			default:		  		return DataItem.fieldName(iField);
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
			case FIELD_ID: 		
				myString += getId(); 
				break;
			case FIELD_ACCOUNT:	
				if ((getAccount() == null) &&
					(theAccountId != -1))
					myString += "Id=" + theAccountId;
				else
					myString += Account.format(getAccount()); 
				break;
			case FIELD_DATE:	
				myString += Date.format(myObj.getDate()); 
				break;
			case FIELD_DESC:	
				myString += myObj.getDescValue(); 
				break;
			case FIELD_PARTNER:	
				if ((myObj.getPartner() == null) &&
					(thePartnerId != -1))
					myString += "Id=" + thePartnerId;
				else
					myString += Account.format(myObj.getPartner()); 
				break;
			case FIELD_TRNTYP:	
				if ((myObj.getTransType() == null) &&
					(theTransId != -1))
					myString += "Id=" + theTransId;
				else
					myString += TransactionType.format(myObj.getTransType()); 
				break;
			case FIELD_AMOUNT:	
				myString += Money.format(myObj.getAmountValue()); 
				break;
			case FIELD_FREQ:	
				if ((myObj.getFrequency() == null) &&
					(theFreqId != -1))
					myString += "Id=" + theFreqId;
				else
					myString += Frequency.format(myObj.getFrequency()); 
				break;
			case FIELD_CREDIT: 
				myString +=	(isCredit() ? "true" : "false");
				break;
		}
		return myString;
	}
							
	/**
	 * Construct a copy of a Pattern
	 * 
	 * @param pPattern The Pattern 
	 */
	protected Pattern(List pList, Pattern pPattern) {
		/* Set standard values */
		super(pList, pPattern.getId());
		Values myObj   = new Values(pPattern.getObj());
		setObj(myObj);
	
		/* Switch on the LinkStyle */
		switch (pList.getStyle()) {
			case CORE:
				/* Create a new id for the item */
				setId(0);
				pList.setNewId(this);				
				break;
			case EDIT:
				setBase(pPattern);
				pList.setNewId(this);		
				break;
			case UPDATE:
				setBase(pPattern);
				setState(pPattern.getState());
				break;
		}
	}

	/* Is this list locked */
	public boolean isLocked() { return getAccount().isLocked(); }
	
	/* Standard constructor for a newly inserted pattern */
	public Pattern(List    	pList,
				   boolean	isCredit) {
		super(pList, 0);
		Values myObj 	= new Values();
		setObj(myObj);
		myObj.setIsCredit(isCredit);
		pList.setNewId(this);		
	}

	/* Construct a new pattern from a statement line */
	public Pattern(List pList, Statement.Line pLine) {
		/* Set standard values */
		super(pList, 0);
		Values 					myObj   = new Values();
		Statement.Line.Values	myNew	= pLine.getObj();
		setObj(myObj);
		
		myObj.setDate(new Date(pLine.getDate()));
		myObj.setDesc(myNew.getDesc());
		myObj.setTransType(pLine.getTransType());
		myObj.setAmount(myNew.getAmount());
		myObj.setPartner(pLine.getPartner());
		myObj.setFrequency(pList.theData.getFrequencys().searchFor(FreqClass.ANNUALLY));
		myObj.setAccount(pLine.getAccount());
		myObj.setIsCredit(pLine.isCredit());
		setState(DataState.NEW);
		
		/* Adjust the date so that it is in the 2000 tax year */
		TaxYear.List  myYears = pList.theData.getTaxYears();
		TaxYear       myTax   = myYears.searchFor("2000");
		Date myDate = getDate();
		while (myDate.compareTo(myTax.getDate()) > 0) myDate.adjustYear(-1);
		myTax  = myYears.peekPrevious(myTax);
		while (myDate.compareTo(myTax.getDate()) <= 0) myDate.adjustYear(1);
	}

	/* Standard constructor */
	private Pattern(List      		pList,
			        int            	uId,
			        int        		uAccountId,
	                java.util.Date  pDate,
	                byte[]          pDesc,
	                byte[]			pAmount,
	                int				uPartnerId,
	                int 			uTransId,
	                int				uFreqId,
	                boolean         isCredit) throws Exception {
		/* Initialise item */
		super(pList, uId);
		
		/* Local variables */
		Account 		myAccount;
		Account.List	myAccounts;
		TransactionType myTrans;
		Frequency 		myFreq;
		
		/* Initialise values */
		Values myObj = new Values();
		setObj(myObj);
		
		/* Create the Encrypted pair for the values */
		DataSet 		myData 	= pList.getData();
		EncryptedPair	myPairs = myData.getEncryptedPairs();
		
		/* Record the encrypted values */
		myObj.setDesc(myPairs.new StringPair(pDesc));
		myObj.setAmount(myPairs.new MoneyPair(pAmount));
		myObj.setIsCredit(isCredit);
		
		/* Record the IDs */
		theAccountId = uAccountId;
		thePartnerId = uPartnerId;
		theTransId	 = uTransId;
		theFreqId    = uFreqId;
		
		/* Access the accounts */
		myAccounts = pList.theData.getAccounts();
		
		/* Look up the Account */
		myObj.setAccount(myAccounts.searchFor(uAccountId));
		if (getAccount() == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Account Id");
					
		/* Look up the Partner */
		myAccount = myAccounts.searchFor(uPartnerId);
		if (myAccount == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Partner Id");
		myObj.setPartner(myAccount);
					
		/* Look up the TransType */
		myTrans = pList.theData.getTransTypes().searchFor(uTransId);
		if (myTrans == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid TransType Id");
		myObj.setTransType(myTrans);
					
		/* Look up the Frequency */
		myFreq = pList.theData.getFrequencys().searchFor(uFreqId);
		if (myFreq == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Frequency Id");
		myObj.setFrequency(myFreq);

		/* Create the date */
		myObj.setDate(new Date(pDate));
		
		/* Allocate the id */
		pList.setNewId(this);				
	}
			
	/* Standard constructor */
	private Pattern(List      		pList,
			        Account    		pAccount,
	                java.util.Date  pDate,
	                String          pDesc,
	                String			pAmount,
	                Account			pPartner,
	                TransactionType	pTransType,
	                Frequency		pFrequency,
	                boolean         isCredit) throws Exception {
		/* Initialise item */
		super(pList, 0);
		
		/* Initialise values */
		Values myObj = new Values();
		setObj(myObj);

		/* Create the Encrypted pair for the values */
		DataSet 		myData 	= pList.getData();
		EncryptedPair	myPairs = myData.getEncryptedPairs();
		
		/* Record the encrypted values */
		myObj.setDesc(myPairs.new StringPair(pDesc));
		myObj.setAmount(myPairs.new MoneyPair(pAmount));
		myObj.setAccount(pAccount);
		myObj.setPartner(pPartner);
		myObj.setTransType(pTransType);
		myObj.setFrequency(pFrequency);
		myObj.setDate(new Date(pDate));
		myObj.setIsCredit(isCredit);
		
		/* Allocate the id */
		pList.setNewId(this);				
	}
			
	/**
	 * Compare this pattern to another to establish equality.
	 * 
	 * @param that The pattern to compare to
	 * @return <code>true</code> if the pattern is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a Pattern */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a Pattern */
		Pattern myPattern = (Pattern)pThat;
		
		/* Check for equality */
		if (getId() != myPattern.getId()) 										return false;
		if (Date.differs(getDate(),        			myPattern.getDate())) 		return false;
		if (EncryptedPair.differs(getDescPair(),    myPattern.getDescPair())) 	return false;
		if (TransactionType.differs(getTransType(), myPattern.getTransType())) 	return false;
		if (EncryptedPair.differs(getAmountPair(),  myPattern.getAmountPair())) return false;
		if (Account.differs(getAccount(),    		myPattern.getAccount())) 	return false;
		if (Account.differs(getPartner(),    		myPattern.getPartner())) 	return false;
		if (Frequency.differs(getFrequency(),		myPattern.getFrequency())) 	return false;
		if (isCredit() != myPattern.isCredit()) 								return false;
		return true;
	}

	/**
	 * Compare this pattern to another to establish sort order. 
	 * @param pThat The Pattern to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		int iDiff;
		
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a Pattern */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as a Pattern */
		Pattern myThat = (Pattern)pThat;

		/* Compare the accounts */
		iDiff = getAccount().compareTo(myThat.getAccount());
		if (iDiff != 0) return iDiff;

		/* If the date differs */
		if (this.getDate() != myThat.getDate()) {
			/* Handle null dates */
			if (this.getDate() == null) return 1;
			if (myThat.getDate() == null) return -1;
			
			/* Compare the dates */
			iDiff = getDate().compareTo(myThat.getDate());
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
		
		/* If the transaction types differ */
		if (this.getTransType() != myThat.getTransType()) {
			/* Handle null transaction types */
			if (this.getTransType() == null) return 1;
			if (myThat.getTransType() == null) return -1;
			
			/* Compare the transaction types */
			iDiff = getTransType().compareTo(myThat.getTransType());
			if (iDiff != 0) return iDiff;
		}
		
		/* Compare the IDs */
		iDiff = (int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}
	
	/**
	 * Validate the pattern
	 */
	public    void validate() { validate(null); }
	protected void validate(Event.List pList) {
		Event        myEvent;
		errorElement myError;
		int          iField;
		List 		 myList = (List)getList();
		
		/* Create a new Event list */
		if (pList == null)
			pList = new Event.List(myList.theData, ListStyle.VIEW);
		
		/* Create a new event based on this line */
		myEvent = new Event(pList, this);

		/* Validate it */
		myEvent.validate();
			
		/* Loop through the errors */
		for (myError = myEvent.getFirstError();
		     myError != null;
		     myError = myError.getNext()) {
			switch (myError.getField()) {
				case Event.FIELD_DATE: 
					iField = Pattern.FIELD_DATE; break;
				case Event.FIELD_DESC: 
					iField = Pattern.FIELD_DESC; break;
				case Event.FIELD_AMOUNT: 
					iField = Pattern.FIELD_AMOUNT; break;
				case Event.FIELD_TRNTYP: 
					iField = Pattern.FIELD_TRNTYP; break;
				case Event.FIELD_DEBIT: 
					iField = (isCredit())
								?  Pattern.FIELD_PARTNER
							    :  Pattern.FIELD_ACCOUNT; 
					break;
				case Event.FIELD_CREDIT: 
					iField = (isCredit())
								?  Pattern.FIELD_ACCOUNT
							    :  Pattern.FIELD_PARTNER; 
					break;
				default: iField = Pattern.FIELD_ACCOUNT;
					break;
			}	
				
			/* Add an error event to this object */
			addError(myError.getError(), iField);
		}
			
		/* Check that frequency is non-null */
		if (getFrequency() == null) 
			addError("Frequency must be non-null", Pattern.FIELD_FREQ);
		
		/* Set validation flag */
		if (!hasErrors()) setValidEdit();
	}
	
	/**
	 * Adjust date that is built from a pattern
	 * 
	 * @param pTaxYear the new tax year 
	 */
	public Event nextEvent(Event.List 	pEvents,
						   TaxYear   	pTaxYear,
			               Date 		pDate) {
		Event     		myEvent;
		TaxYear  		myBase;
		Date 			myDate;
		FreqClass		myFreq;
		int       		iAdjust;
		TaxYear.List	myList;
		
		/* Access the frequency */
		myFreq = getFrequency().getFrequency();
		
		/* Access the Tax Year list */
		myList = (TaxYear.List)pTaxYear.getList();
		
		/* If this is the first request for an event */
		if (pDate.compareTo(getDate()) == 0) {
			/* If the frequency is maturity */
			if (myFreq == FreqClass.MATURITY) {
				/* Access the maturity date */
				myDate = getAccount().getMaturity();
				
				/* Obtain the relevant tax year */
				myBase = myList.searchFor(getDate());
			
				/* Ignore if no maturity or else not this year */
				if ((myDate == null)  ||
					(myDate.isNull()) ||
					(myBase == null)  ||
					(myBase.compareTo(pTaxYear) != 0)) 
					return null;
			}
			
			/* Obtain the base tax year */
			myBase = myList.searchFor("2000");
		
			/* Calculate the difference in years */
			iAdjust = pTaxYear.getDate().getYear() 
						- myBase.getDate().getYear();
		
			/* Adjust the date to fall into the tax year */
			pDate.copyDate(getDate());
			pDate.adjustYear(iAdjust);
		}
		
		/* else this is a secondary access */
		else {
			/* switch on frequency type */
			switch (myFreq) {
				/* Annual and maturity patterns only generate single event */
				case ANNUALLY:
				case MATURITY:
					return null;
					
				/* Monthly and TenMonthly add one month */
				case MONTHLY:
				case TENMONTHS:
					pDate.adjustMonth(1);
					break;
					
				/* Quarterly add three months */
				case QUARTERLY:
					pDate.adjustMonth(3);
					break;
					
				/* HalfYearly add six months */
				case HALFYEARLY:
					pDate.adjustMonth(6);
					break;
					
				/* EndMonthly shift to end of next month */
				case ENDOFMONTH:
					pDate.endNextMonth();
					break;
			}
			
			/* If we are beyond the end of the year we have finished */
			if (pDate.compareTo(pTaxYear.getDate()) > 0)
				return null;
			
			/* If this is a ten month repeat */
			if (myFreq == FreqClass.TENMONTHS) {					
				myDate = new Date(getDate());
				
				/* Obtain the base tax year */
				myBase = myList.searchFor("2000");
			
				/* Calculate the difference in years */
				iAdjust = pTaxYear.getDate().getYear() 
							- myBase.getDate().getYear();
			
				/* Adjust the date to fall into the tax year */
				myDate.copyDate(getDate());
				myDate.adjustYear(iAdjust);
				
				/* Add 9 months to get to last date */
				myDate.adjustMonth(9);
				
				/* If we are beyond this date then we have finished */
				if (pDate.compareTo(myDate) > 0)
					return null;
			}
		}
		
		/* Build the new linked event */
		myEvent = new Event(pEvents, this);
		
		/* Set the date for this event */
		myEvent.setDate(new Date(pDate));
		
		/* Return the new event */
		return myEvent;
	}
	
	/**
	 * Set a new partner 
	 * 
	 * @param pPartner the account 
	 */
	public void setPartner(Account pPartner) {
		getObj().setPartner(pPartner);
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
	 * Set a new frequency 
	 * 
	 * @param pFrequency the frequency 
	 */
	public void setFrequency(Frequency pFrequency) {
		getObj().setFrequency(pFrequency);
	}

	/**
	 * Set a new description 
	 * 
	 * @param pDesc the description 
	 */
	public void setDesc(String pDesc) throws Exception {
		/* If we are setting a non null value */
		if (pDesc != null) {
			/* Create the Encrypted pair for the values */
			DataSet 		myData 	= ((List)getList()).getData();
			EncryptedPair	myPairs = myData.getEncryptedPairs();
			StringPair		myPair	= myPairs.new StringPair(pDesc);
		
			/* Record the value and encrypt it*/
			getObj().setDesc(myPair);
			myPair.ensureEncryption();
		}
		
		/* Else we are setting a null value */
		else getObj().setDesc(null);
	}
	
	/**
	 * Set a new amount 
	 * 
	 * @param pAmount the amount 
	 */
	public void setAmount(Money pAmount) throws Exception {
		/* If we are setting a non null value */
		if (pAmount != null) {
			/* Create the Encrypted pair for the values */
			DataSet 		myData 	= ((List)getList()).getData();
			EncryptedPair	myPairs = myData.getEncryptedPairs();
			MoneyPair		myPair	= myPairs.new MoneyPair(pAmount);
		
			/* Record the value and encrypt it*/
			getObj().setAmount(myPair);
			myPair.ensureEncryption();
		}
		
		/* Else we are setting a null value */
		else getObj().setAmount(null);
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
	 * Update Pattern from a pattern extract  
	 * @param pPattern the pattern extract
	 * @return whether changes have been made 
	 */
	public boolean applyChanges(DataItem pPattern) {
		Pattern myPattern 	= (Pattern)pPattern;
		Values	myObj		= getObj();
		Values	myNew		= myPattern.getObj();
		boolean bChanged	= false;
		
		/* Store the current detail into history */
		pushHistory();
		
		/* Update the partner if required */
		if (Account.differs(getPartner(), myPattern.getPartner())) 
			setPartner(myPattern.getPartner());
	
		/* Update the transtype if required */
		if (TransactionType.differs(getTransType(), myPattern.getTransType())) 
			setTransType(myPattern.getTransType());
	
		/* Update the frequency if required */
		if (Frequency.differs(getFrequency(), myPattern.getFrequency())) 
			setFrequency(myPattern.getFrequency());
	
		/* Update the description if required */
		if (EncryptedPair.differs(myObj.getDesc(), myNew.getDesc())) 
			myObj.setDesc(myNew.getDesc());
	
		/* Update the amount if required */
		if (EncryptedPair.differs(myObj.getAmount(), myNew.getAmount())) 
			myObj.setAmount(myNew.getAmount());
		
		/* Update the date if required */
		if (Date.differs(getDate(), myPattern.getDate())) 
			setDate(myPattern.getDate());
		
		/* Check for changes */
		if (checkForHistory()) {
			/* Marke as changed */
			setState(DataState.CHANGED);
			bChanged = true;
		}
		
		/* Return to caller */
		return bChanged;
	}

	/**
	 * Ensure encryption after spreadsheet load
	 */
	protected void ensureEncryption() throws Exception {
		Values myObj = getObj();

		/* Protect against exceptions */
		try {
			/* Ensure the encryption */
			myObj.getDesc().ensureEncryption();
			myObj.getAmount().ensureEncryption();
		}
		
		/* Catch exception */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								this,
								"Failed to complete encryption",
								e);
		}
	}
	
	public static class List extends DataList<Pattern> {
		/* Local values */
		private Account	theAccount	= null;
		private DataSet	theData		= null;
		public 	DataSet getData()	{ return theData; }
		
	 	/** 
	 	 * Construct an empty CORE pattern list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(DataSet pData) { 
			super(ListStyle.CORE, false);
			theData = pData;
		}

		/** 
	 	 * Construct an empty generic pattern list
	 	 * @param pData the DataSet for the list
	 	 * @param pStyle the style of the list 
	 	 */
		protected List(DataSet pData, ListStyle pStyle) { 
			super(pStyle, false);
			theData = pData;
		}

		/** 
	 	 * Construct a generic pattern list
	 	 * @param pList the source pattern list 
	 	 * @param pStyle the style of the list 
	 	 */
		public List(List pList, ListStyle pStyle) { 
			super(pList, pStyle);
			theData = pList.getData();
		}

		/** 
	 	 * Construct a difference pattern list
	 	 * @param pNew the new Pattern list 
	 	 * @param pOld the old Pattern list 
	 	 */
		protected List(List pNew, List pOld) { 
			super(pNew, pOld);
			theData = pNew.getData();
		}
		
		/**
		 * Construct an edit extract of a Pattern list
		 * 
		 * @param pList      The list to extract from
		 * @param pAccount	 The account to extract patterns for 
		 */
		public List(List 	pList,
					Account pAccount) {
			/* Make this list the correct style */
			super(ListStyle.EDIT, false);
			theData = pList.getData();
			
			/* Local variables */
			Pattern 		myCurr;
			Pattern 		myItem;
			ListIterator 	myIterator;
			
			/* Store the account */
			theAccount = pAccount;
			
			/* Access the list iterator */
			myIterator = pList.listIterator(true);
			
			/* Loop through the Prices */
			while ((myCurr = myIterator.next()) != null) {
				/* If this item belongs to the account */
				if (!Account.differs(myCurr.getAccount(), pAccount)) {
					/* Copy the item */
					myItem = new Pattern(this, myCurr);
					myItem.addToList();
				}
			}
		}
	
		/** 
	 	 * Clone a Pattern list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return new List(this, ListStyle.DIFFER); }
		
		/* Is this list locked */
		public boolean isLocked() { return (theAccount != null) && (theAccount.isLocked()); }
		
		/**
		 * Add a new item to the core list
		 * @param pPattern item
		 * @return the newly added item
		 */
		public Pattern addNewItem(DataItem pPattern) {
			Pattern myPattern = new Pattern(this, (Pattern)pPattern);
			myPattern.addToList();
			return myPattern;
		}
	
		/**
		 * Add a new item to the edit list
		 * @param isCredit - is this a credit item
		 * @return the newly added item
		 */
		public Pattern addNewItem(boolean isCredit) {
			Pattern myPattern = new Pattern(this, isCredit);
			myPattern.setAccount(theAccount);
			myPattern.addToList();
			return myPattern;
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }
				
		/**
		 * Validate the patterns
		 */
		public void validate() {
			Pattern     	myCurr;
			Event.List		myEvents;
			ListIterator 	myIterator;
		
			/* Clear the errors */
			clearErrors();
			
			/* Create a new Event list */
			myEvents = new Event.List(theData, ListStyle.VIEW);
			
			/* Create an iterator */
			myIterator = listIterator();
			
			/* Loop through the list */
			while ((myCurr = myIterator.next()) != null) {
				/* Validate it */
				myCurr.validate(myEvents);
			}
			
			/* find the edit state */
			findEditState();
		}
		
		/**
		 * Update account details after data update
		 */
		public void markActivePatterns() {
			ListIterator 	myIterator;
			Pattern 		myCurr;
					
			/* Access the list iterator */
			myIterator = listIterator();
			
			/* Loop through the Prices */
			while ((myCurr = myIterator.next()) != null) {
				/* Touch the patterned account */
				myCurr.getAccount().touchPattern();			
				
				/* Touch the patterned partner */
				myCurr.getPartner().touchPartner();			
			}			 
		}
		
		/**
		 *  Allow a pattern to be added
		 */
		public void addItem(java.util.Date  pDate,
				            String   		pDesc,
				            String   		pAmount,
				            String   		pAccount,
				            String 			pPartner,
				            String			pTransType,
				            String  		pFrequency,
				            boolean  		isCredit) throws Exception {
			TransactionType.List	myTranTypes;
			Frequency.List			myFrequencies;
			Account.List 			myAccounts;
			Account     			myAccount;
			Account     			myPartner;
			TransactionType    		myTransType;
			Frequency    			myFrequency;
			Pattern					myPattern;
			
			/* Access the Lists */
			myAccounts 		= theData.getAccounts();
			myTranTypes		= theData.getTransTypes();
			myFrequencies	= theData.getFrequencys();
			
			/* Look up the Account */
			myAccount = myAccounts.searchFor(pAccount);
			if (myAccount == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Pattern on [" + 
			                        Date.format(new Date(pDate)) +
			                        "] has invalid Account [" +
			                        pAccount + "]");
				
			/* Look up the Partner */
			myPartner = myAccounts.searchFor(pPartner);
			if (myPartner == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Pattern on [" + 
			                        Date.format(new Date(pDate)) +
			                        "] has invalid Partner [" +
			                        pPartner + "]");
				
			/* Look up the TransType */
			myTransType = myTranTypes.searchFor(pTransType);
			if (myTransType == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Pattern on [" + 
			                        Date.format(new Date(pDate)) +
			                        "] has invalid TransType [" +
			                        pTransType + "]");
				
			/* Look up the Frequency */
			myFrequency = myFrequencies.searchFor(pFrequency);
			if (myFrequency == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Pattern on [" + 
			                        Date.format(new Date(pDate)) +
			                        "] has invalid Frequency [" +
			                        pFrequency + "]");
				
			/* Create the new pattern */
			myPattern = new Pattern(this, myAccount, pDate, 
									pDesc, pAmount, myPartner, 
								 	myTransType, myFrequency, isCredit);
			
			/* Validate the pattern */
			myPattern.validate();

			/* Handle validation failure */
			if (myPattern.hasErrors()) 
				throw new Exception(ExceptionClass.VALIDATE,
									myPattern,
									"Failed validation");
				
			/* Add to the list */
			myPattern.addToList();
		}
			
		/**
		 *  Allow a pattern to be added 
		 */
		public void addItem(int     		uId,
				            java.util.Date  pDate,
				            byte[]   		pDesc,
				            byte[]   		pAmount,
				            int     		uAccountId,
				            int   			uPartnerId,
				            int				uTransId,
				            int   			uFreqId,
				            boolean  		isCredit) throws Exception {
			Pattern     myPattern;
			
			/* Create the new pattern */
			myPattern = new Pattern(this, uId, uAccountId, pDate, 
									pDesc, pAmount, uPartnerId, 
								 	uTransId, uFreqId, isCredit);
			
			/* Check that this PatternId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
			  			            "Duplicate PatternId <" + uId + ">");
			 
			/* Validate the pattern */
			myPattern.validate();

			/* Handle validation failure */
			if (myPattern.hasErrors()) 
				throw new Exception(ExceptionClass.VALIDATE,
									myPattern,
									"Failed validation");
				
			/* Add to the list */
			myPattern.addToList();
		}			
	}
	
	/**
	 * Values for a pattern
	 */
	public class Values implements histObject {
		private Date       		theDate      = null;
		private StringPair    	theDesc      = null;
		private MoneyPair  		theAmount    = null;
		private Account			thePartner   = null;
		private Frequency		theFrequency = null;
		private TransactionType	theTransType = null;
		private Account    		theAccount   = null;
		private boolean 		isCredit     = false;
		
		/* Access methods */
		public Date       		getDate()      { return theDate; }
		public StringPair       getDesc()      { return theDesc; }
		public MoneyPair   		getAmount()    { return theAmount; }
		public Account          getPartner()   { return thePartner; }
		public Frequency  		getFrequency() { return theFrequency; }
		public TransactionType  getTransType() { return theTransType; }
		public Account			getAccount()   { return theAccount; }
		public boolean         	isCredit()     { return isCredit; }
		
		/* Encrypted value access */
		public  Money	getAmountValue()    { return EncryptedPair.getPairValue(getAmount()); }
		public  String  getDescValue()      { return EncryptedPair.getPairValue(getDesc()); }

		/* Encrypted bytes access */
		public  byte[]	getAmountBytes()    { return EncryptedPair.getPairBytes(getAmount()); }
		public  byte[]  getDescBytes()      { return EncryptedPair.getPairBytes(getDesc()); }

		public void setDate(Date pDate) {
			theDate      = pDate; }
		public void setDesc(StringPair pDesc) {
			theDesc      = pDesc; }
		public void setAmount(MoneyPair pAmount) {
			theAmount    = pAmount; }
		public void setPartner(Account pPartner) {
			thePartner   = pPartner; }
		public void setFrequency(Frequency pFrequency) {
			theFrequency = pFrequency; }
		public void setTransType(TransactionType pTransType) {
			theTransType = pTransType; }
		public void setAccount(Account pAccount) {
			theAccount   = pAccount; }
		public void setIsCredit(boolean isCredit) {
			this.isCredit = isCredit; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) {
			theDate      = pValues.getDate();
			theDesc      = pValues.getDesc();
			theAmount    = pValues.getAmount();
			thePartner   = pValues.getPartner();
			theFrequency = pValues.getFrequency();
			theTransType = pValues.getTransType();
			theAccount   = pValues.getAccount();
			isCredit     = pValues.isCredit();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (Date.differs(theDate,    				pValues.theDate))       return false;
			if (EncryptedPair.differs(theDesc,   		pValues.theDesc))       return false;
			if (EncryptedPair.differs(theAmount, 		pValues.theAmount))     return false;
			if (Account.differs(thePartner, 			pValues.thePartner))    return false;
			if (Frequency.differs(theFrequency, 		pValues.theFrequency))	return false;
			if (TransactionType.differs(theTransType,	pValues.theTransType)) 	return false;
			if (Account.differs(theAccount, 			pValues.theAccount))	return false;
			if (isCredit != pValues.isCredit) 	   							   	return false;
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
			thePartner   = pValues.getPartner();
			theFrequency = pValues.getFrequency();
			theTransType = pValues.getTransType();
			theAccount   = pValues.getAccount();
			isCredit     = pValues.isCredit();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case FIELD_DATE:
					bResult = (Date.differs(theDate,       			pValues.theDate));
					break;
				case FIELD_DESC:
					bResult = (EncryptedPair.differs(theDesc,		pValues.theDesc));
					break;
				case FIELD_TRNTYP:
					bResult = (TransactionType.differs(theTransType, pValues.theTransType));
					break;
				case FIELD_AMOUNT:
					bResult = (EncryptedPair.differs(theAmount,		pValues.theAmount));
					break;
				case FIELD_PARTNER:
					bResult = (Account.differs(thePartner,   		pValues.thePartner));
					break;
				case FIELD_FREQ:
					bResult = (Frequency.differs(theFrequency, 		pValues.theFrequency));
					break;
				case FIELD_ACCOUNT:
					bResult = (Account.differs(theAccount,   		pValues.theAccount));
					break;
				case FIELD_CREDIT:
					bResult = (isCredit != pValues.isCredit);
					break;
			}
			return bResult;
		}
	}	
}
