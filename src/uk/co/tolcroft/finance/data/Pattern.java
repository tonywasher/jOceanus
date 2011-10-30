package uk.co.tolcroft.finance.data;

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
import uk.co.tolcroft.models.data.ValidationControl;
import uk.co.tolcroft.models.data.DataList.*;
import uk.co.tolcroft.models.help.DebugDetail;
import uk.co.tolcroft.finance.data.StaticClass.*;
import uk.co.tolcroft.finance.views.*;

public class Pattern extends EncryptedItem<Pattern> {
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
			
	/* Access methods */
	public  Values         	getValues()     { return (Values)super.getValues(); }	
	public  Date        	getDate()      	{ return getValues().getDate(); }
	public  String          getDesc()      	{ return getPairValue(getValues().getDesc()); }
	public  Money       	getAmount()    	{ return getPairValue(getValues().getAmount()); }
	public  Account         getPartner()   	{ return getValues().getPartner(); }
	public  Frequency   	getFrequency() 	{ return getValues().getFrequency(); }
	public  TransactionType getTransType() 	{ return getValues().getTransType(); }
	public  AccountType 	getActType()   	{ return getAccount().getActType(); }
	public  boolean         isCredit()     	{ return getValues().isCredit(); }
	public  Account		   	getAccount()	{ return getValues().getAccount(); }
	private void         	setAccount(Account pAccount)   {
		getValues().setAccount(pAccount); }

	/* Encrypted value access */
	public  byte[]	getAmountBytes()    { return getPairBytes(getValues().getAmount()); }
	public  byte[]  getDescBytes()      { return getPairBytes(getValues().getDesc()); }

	/* Linking methods */
	public Pattern     getBase() { return (Pattern)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_ACCOUNT  = EncryptedItem.NUMFIELDS;
	public static final int FIELD_DATE     = EncryptedItem.NUMFIELDS+1;
	public static final int FIELD_DESC     = EncryptedItem.NUMFIELDS+2;
	public static final int FIELD_PARTNER  = EncryptedItem.NUMFIELDS+3;
	public static final int FIELD_AMOUNT   = EncryptedItem.NUMFIELDS+4;
	public static final int FIELD_TRNTYP   = EncryptedItem.NUMFIELDS+5;
	public static final int FIELD_FREQ     = EncryptedItem.NUMFIELDS+6;
	public static final int FIELD_CREDIT   = EncryptedItem.NUMFIELDS+7;
	public static final int NUMFIELDS	   = EncryptedItem.NUMFIELDS+8;
	
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
			case FIELD_ACCOUNT:		return "Account";
			case FIELD_DATE:		return "Date";
			case FIELD_DESC:		return "Description";
			case FIELD_PARTNER:		return "Partner";
			case FIELD_AMOUNT:		return "Amount";
			case FIELD_TRNTYP:		return "TransactionType";
			case FIELD_FREQ:		return "Frequency";
			case FIELD_CREDIT:		return "IsCredit";
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
	public String formatField(DebugDetail pDetail, int iField, HistoryValues<Pattern> pValues) {
		String 	myString = "";
		Values 	myValues  = (Values)pValues;
		switch (iField) {
			case FIELD_ACCOUNT:	
				if ((myValues.getAccount() == null) &&
					(myValues.getAccountId() != null))
					myString += "Id=" + myValues.getAccountId();
				else
					myString += Account.format(myValues.getAccount()); 
				myString = pDetail.addDebugLink(myValues.getAccount(), myString);
				break;
			case FIELD_DATE:	
				myString += Date.format(myValues.getDate()); 
				break;
			case FIELD_DESC:	
				myString += myValues.getDescValue(); 
				break;
			case FIELD_PARTNER:	
				if ((myValues.getPartner() == null) &&
					(myValues.getPartnerId() != null))
					myString += "Id=" + myValues.getPartnerId();
				else
					myString += Account.format(myValues.getPartner()); 
				myString = pDetail.addDebugLink(myValues.getPartner(), myString);
				break;
			case FIELD_TRNTYP:	
				if ((myValues.getTransType() == null) &&
					(myValues.getTransId() != null))
					myString += "Id=" + myValues.getTransId();
				else
					myString += TransactionType.format(myValues.getTransType()); 
				myString = pDetail.addDebugLink(myValues.getTransType(), myString);
				break;
			case FIELD_AMOUNT:	
				myString += Money.format(myValues.getAmountValue()); 
				break;
			case FIELD_FREQ:	
				if ((myValues.getFrequency() == null) &&
					(myValues.getFreqId() != null))
					myString += "Id=" + myValues.getFreqId();
				else
					myString += Frequency.format(myValues.getFrequency()); 
				myString = pDetail.addDebugLink(myValues.getFrequency(), myString);
				break;
			case FIELD_CREDIT: 
				myString +=	(isCredit() ? "true" : "false");
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
	protected HistoryValues<Pattern> getNewValues() { return new Values(); }
	
	/**
	 * Construct a copy of a Pattern
	 * 
	 * @param pPattern The Pattern 
	 */
	protected Pattern(List pList, Pattern pPattern) {
		/* Set standard values */
		super(pList, pPattern.getId());
		Values myValues	= getValues();
		myValues.copyFrom(pPattern.getValues());
		ListStyle myOldStyle = pPattern.getList().getStyle();

		/* Switch on the ListStyle */
		switch (pList.getStyle()) {
			case EDIT:
				/* If this is a view creation */
				if (myOldStyle == ListStyle.CORE) {
					/* Pattern is based on the original element */
					setBase(pPattern);
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
				setBase(pPattern);
				setState(pPattern.getState());
				break;
		}
	}

	/* Is this list locked */
	public boolean isLocked() { return getAccount().isLocked(); }
	
	/* Standard constructor for a newly inserted pattern */
	public Pattern(List    	pList) {
		super(pList, 0);
		setControlKey(pList.getControlKey());
		pList.setNewId(this);		
	}

	/* Construct a new pattern from a statement line */
	public Pattern(List pList, Statement.Line pLine) {
		/* Set standard values */
		super(pList, 0);
		Values 					myValues	= getValues();
		FinanceData				myData		= pList.getData();

		myValues.copyFrom(pLine.getValues());
		pList.setNewId(this);		
		
		/* Adjust the date so that it is in the 2000 tax year */
		TaxYear.List  myYears = myData.getTaxYears();
		TaxYear       myTax   = myYears.searchFor("2000");
		Date myDate = getDate();
		while (myDate.compareTo(myTax.getDate()) > 0) myDate.adjustYear(-1);
		myTax  = myYears.peekPrevious(myTax);
		while (myDate.compareTo(myTax.getDate()) <= 0) myDate.adjustYear(1);
	}

	/* Standard constructor */
	private Pattern(List      		pList,
			        int            	uId,
			        int				uControlId,
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
		Values myValues = getValues();
		
		/* Record the IDs */
		myValues.setAccountId(uAccountId);
		myValues.setPartnerId(uPartnerId);
		myValues.setTransId(uTransId);
		myValues.setFreqId(uFreqId);
		
		/* Set control Key */
		setControlKey(uControlId);
		
		/* Access the accounts */
		FinanceData	myData 	= pList.getData();
		myAccounts = myData.getAccounts();
		
		/* Look up the Account */
		myAccount = myAccounts.searchFor(uAccountId);
		if (myAccount == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Account Id");
		myValues.setAccount(myAccount);
					
		/* Look up the Partner */
		myAccount = myAccounts.searchFor(uPartnerId);
		if (myAccount == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Partner Id");
		myValues.setPartner(myAccount);
					
		/* Look up the TransType */
		myTrans = myData.getTransTypes().searchFor(uTransId);
		if (myTrans == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid TransType Id");
		myValues.setTransType(myTrans);
					
		/* Look up the Frequency */
		myFreq = myData.getFrequencys().searchFor(uFreqId);
		if (myFreq == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Frequency Id");
		myValues.setFrequency(myFreq);

		/* Create the date */
		myValues.setDate(new Date(pDate));
		
		/* Record the encrypted values */
		myValues.setDesc(new StringPair(pDesc));
		myValues.setAmount(new MoneyPair(pAmount));
		myValues.setIsCredit(isCredit);
		
		/* Allocate the id */
		pList.setNewId(this);				
	}
			
	/* Standard constructor */
	private Pattern(List      		pList,
					int				uId,
			        Account    		pAccount,
	                java.util.Date  pDate,
	                String          pDesc,
	                String			pAmount,
	                Account			pPartner,
	                TransactionType	pTransType,
	                Frequency		pFrequency,
	                boolean         isCredit) throws Exception {
		/* Initialise item */
		super(pList, uId);
		
		/* Initialise values */
		Values myValues = getValues();

		/* Record the encrypted values */
		myValues.setDesc(new StringPair(pDesc));
		myValues.setAmount(new MoneyPair(pAmount));
		myValues.setAccount(pAccount);
		myValues.setPartner(pPartner);
		myValues.setTransType(pTransType);
		myValues.setFrequency(pFrequency);
		myValues.setDate(new Date(pDate));
		myValues.setIsCredit(isCredit);
		
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
		Pattern myThat = (Pattern)pThat;
		
		/* Check for equality on id */
		if (getId() != myThat.getId())	return false;
		
		/* Compare the changeable values */
		return getValues().histEquals(myThat.getValues());
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
	 * Rebuild Links to partner data
	 * @param pData the DataSet
	 */
	protected void reBuildLinks(FinanceData pData) {
		/* Update the Encryption details */
		super.reBuildLinks(pData);
		
		/* Access Lists */
		Account.List 			myAccounts 		= pData.getAccounts();
		Frequency.List 			myFrequencys	= pData.getFrequencys();
		TransactionType.List 	myTranTypes		= pData.getTransTypes();
		
		/* Update to use the local copy of the Accounts */
		Values 	myValues   	= getValues();
		Account	myAct		= myValues.getAccount();
		Account	myNewAct 	= myAccounts.searchFor(myAct.getId());
		myValues.setAccount(myNewAct);

		/* Update partner to use the local copy of the Accounts */
		myAct		= myValues.getPartner();
		myNewAct 	= myAccounts.searchFor(myAct.getId());
		myValues.setPartner(myNewAct);

		/* Update frequency to use the local copy */
		Frequency	myFreq		= myValues.getFrequency();
		Frequency	myNewFreq 	= myFrequencys.searchFor(myFreq.getId());
		myValues.setFrequency(myNewFreq);

		/* Update transtype to use the local copy */
		TransactionType	myTran		= myValues.getTransType();
		TransactionType	myNewTran 	= myTranTypes.searchFor(myTran.getId());
		myValues.setTransType(myNewTran);
	}

	/**
	 * Validate the pattern
	 */
	public    void validate() { validate(null); }
	protected void validate(Event.List pList) {
		Event        							myEvent;
		ValidationControl<Event>.errorElement 	myError;
		int          							iField;
		List 		 							myList = (List)getList();
		
		/* Create a new Event list */
		if (pList == null)
			pList = myList.getData().getEvents().getViewList();
		
		/* Create a new event based on this line */
		try { myEvent = new Event(pList, this); }
		catch (Exception e) {
			addError("Failed to validate", Pattern.FIELD_ACCOUNT);
			return;
		}

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
		else if (!getFrequency().getEnabled()) 
			addError("Frequency must be enabled", FIELD_FREQ);
		
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
			               Date 		pDate) throws Exception {
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
		getValues().setPartner(pPartner);
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
	 * Set a new frequency 
	 * 
	 * @param pFrequency the frequency 
	 */
	public void setFrequency(Frequency pFrequency) {
		getValues().setFrequency(pFrequency);
	}

	/**
	 * Set a new description 
	 * 
	 * @param pDesc the description 
	 */
	public void setDesc(String pDesc) throws Exception {
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
	 * Set a new date 
	 * 
	 * @param pDate the new date 
	 */
	public void setDate(Date pDate) {
		getValues().setDate((pDate == null) ? null : new Date(pDate));
	}

	/**
	 * Set a new isCredit indication 
	 * 
	 * @param isCredit
	 */
	public void setIsCredit(boolean isCredit) {
		getValues().setIsCredit(isCredit);
	}
	
	/**
	 * Update Pattern from a pattern extract  
	 * @param pPattern the pattern extract
	 * @return whether changes have been made 
	 */
	public boolean applyChanges(DataItem<?> pPattern) {
		Pattern myPattern 	= (Pattern)pPattern;
		Values	myValues	= getValues();
		Values	myNew		= myPattern.getValues();
		boolean bChanged	= false;
		
		/* Store the current detail into history */
		pushHistory();
		
		/* Update the partner if required */
		if (Account.differs(getPartner(), myPattern.getPartner()).isDifferent()) 
			setPartner(myPattern.getPartner());
	
		/* Update the transtype if required */
		if (TransactionType.differs(getTransType(), myPattern.getTransType()).isDifferent()) 
			setTransType(myPattern.getTransType());
	
		/* Update the frequency if required */
		if (Frequency.differs(getFrequency(), myPattern.getFrequency()).isDifferent()) 
			setFrequency(myPattern.getFrequency());
	
		/* Update the description if required */
		if (differs(myValues.getDesc(), myNew.getDesc()).isDifferent()) 
			myValues.setDesc(myNew.getDesc());
	
		/* Update the amount if required */
		if (differs(myValues.getAmount(), myNew.getAmount()).isDifferent()) 
			myValues.setAmount(myNew.getAmount());
		
		/* Update the date if required */
		if (Date.differs(getDate(), myPattern.getDate()).isDifferent()) 
			setDate(myPattern.getDate());
		
		/* Update the isCredit if required */
		if (isCredit() != myPattern.isCredit()) 
			setIsCredit(myPattern.isCredit());
		
		/* Check for changes */
		if (checkForHistory()) {
			/* Mark as changed */
			setState(DataState.CHANGED);
			bChanged = true;
		}
		
		/* Return to caller */
		return bChanged;
	}
	
	public static class List extends EncryptedList<List, Pattern> {
		/* Local values */
		private Account	theAccount	= null;
		
		/* Access Extra Variables correctly */
		public FinanceData 	getData() 		{ return (FinanceData) super.getData(); }
		public Account 		getAccount() 	{ return theAccount; }
		
	 	/** 
	 	 * Construct an empty CORE pattern list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(FinanceData pData) { 
			super(List.class, Pattern.class, pData);
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
		public List getEditList() 	{ return null; }
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
		 * Construct a difference ControlData list
		 * @param pNew the new ControlData list 
		 * @param pOld the old ControlData list 
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
		 * Construct an edit extract of a Pattern list
		 * @param pAccount	 The account to extract patterns for 
		 */
		public List getEditList(Account pAccount) {
			/* Build an empty Update */
			List myList = new List(this);
			
			/* Make this list the correct style */
			myList.setStyle(ListStyle.EDIT);

			/* Local variables */
			Pattern 		myCurr;
			Pattern 		myItem;
			ListIterator 	myIterator;
			
			/* Store the account */
			myList.theAccount = pAccount;
			
			/* Access the list iterator */
			myIterator = listIterator(true);
			
			/* Loop through the Prices */
			while ((myCurr = myIterator.next()) != null) {
				/* Check the account */
				int myResult = pAccount.compareTo(myCurr.getAccount());
				
				/* Handle differing accounts */
				if (myResult ==  1) continue;
				if (myResult == -1) break;
				
				/* Copy the item */
				myItem = new Pattern(myList, myCurr);
				myList.add(myItem);
			}
			
			/* Return the List */
			return myList;
		}
	
		/**
		 * Add additional fields to HTML String
		 * @param pBuffer the string buffer 
		 */
		public void addHTMLFields(StringBuilder pBuffer) {
			/* If this is an account extract */
			if (theAccount != null) {
				/* Start the Fields section */
				pBuffer.append("<tr><th rowspan=\"2\">Fields</th></tr>");

				/* Format the account */
				pBuffer.append("<tr><td>Account</td><td>"); 
				pBuffer.append(Account.format(theAccount)); 
				pBuffer.append("</td></tr>");
			}
		}
		
		/* Is this list locked */
		public boolean isLocked() { return (theAccount != null) && (theAccount.isLocked()); }
		
		/**
		 * Add a new item to the core list
		 * @param pPattern item
		 * @return the newly added item
		 */
		public Pattern addNewItem(DataItem<?> pPattern) {
			Pattern myPattern = new Pattern(this, (Pattern)pPattern);
			add(myPattern);
			return myPattern;
		}
	
		/**
		 * Add a new item to the edit list
		 * @return the newly added item
		 */
		public Pattern addNewItem() {
			Pattern myPattern = new Pattern(this);
			myPattern.setAccount(theAccount);
			add(myPattern);
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
			myEvents = getData().getEvents().getViewList();
			
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
		 * Mark Active items
		 */
		public void markActiveItems() {
			ListIterator 	myIterator;
			Pattern 		myCurr;
					
			/* Access the list iterator */
			myIterator = listIterator();
			
			/* Loop through the Prices */
			while ((myCurr = myIterator.next()) != null) {
				/* Touch the patterned account */
				myCurr.getAccount().touchItem(myCurr);			
				
				/* Touch the patterned partner */
				myCurr.getPartner().touchItem(myCurr);			
				
				/* Touch the patterned frequency */
				myCurr.getFrequency().touchItem(myCurr);			
				
				/* Touch the patterned transaction type */
				myCurr.getTransType().touchItem(myCurr);			
			}			 
		}
		
		/**
		 *  Allow a pattern to be added
		 */
		public void addItem(int				uId,
							java.util.Date  pDate,
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
			FinanceData				myData;
			
			/* Access the Lists */
			myData			= getData();
			myAccounts 		= myData.getAccounts();
			myTranTypes		= myData.getTransTypes();
			myFrequencies	= myData.getFrequencys();
			
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
			myPattern = new Pattern(this, uId, myAccount, pDate, 
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
			add(myPattern);
		}
			
		/**
		 *  Allow a pattern to be added 
		 */
		public void addItem(int     		uId,
							int				uControlId,
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
			myPattern = new Pattern(this, uId, uControlId, uAccountId, pDate, 
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
			add(myPattern);
		}			
	}
	
	/**
	 * Values for a pattern
	 */
	public class Values extends EncryptedValues {
		private Date       		theDate      = null;
		private StringPair    	theDesc      = null;
		private MoneyPair  		theAmount    = null;
		private Account			thePartner   = null;
		private Frequency		theFrequency = null;
		private TransactionType	theTransType = null;
		private Account    		theAccount   = null;
		private boolean 		isCredit     = false;
		private Integer 		theAccountId = null;
		private Integer 		thePartnerId = null;
		private Integer 		theTransId	 = null;
		private Integer 		theFreqId	 = null;
		
		/* Access methods */
		public Date       		getDate()      { return theDate; }
		public StringPair       getDesc()      { return theDesc; }
		public MoneyPair   		getAmount()    { return theAmount; }
		public Account          getPartner()   { return thePartner; }
		public Frequency  		getFrequency() { return theFrequency; }
		public TransactionType  getTransType() { return theTransType; }
		public Account			getAccount()   { return theAccount; }
		public boolean         	isCredit()     { return isCredit; }
		private Integer        	getAccountId() { return theAccountId; }
		private Integer        	getPartnerId() { return thePartnerId; }
		private Integer        	getTransId()   { return theTransId; }
		private Integer        	getFreqId()    { return theFreqId; }
		
		/* Encrypted value access */
		public  Money	getAmountValue()    { return getPairValue(getAmount()); }
		public  String  getDescValue()      { return getPairValue(getDesc()); }

		/* Encrypted bytes access */
		public  byte[]	getAmountBytes()    { return getPairBytes(getAmount()); }
		public  byte[]  getDescBytes()      { return getPairBytes(getDesc()); }

		public void setDate(Date pDate) {
			theDate      = pDate; }
		public void setDesc(StringPair pDesc) {
			theDesc      = pDesc; }
		public void setAmount(MoneyPair pAmount) {
			theAmount    = pAmount; }
		public void setPartner(Account pPartner) {
			thePartner   = pPartner; 
			thePartnerId = (pPartner == null) ? null : pPartner.getId(); }
		public void setFrequency(Frequency pFrequency) {
			theFrequency = pFrequency; 
			theFreqId = (pFrequency == null) ? null : pFrequency.getId(); }
		public void setTransType(TransactionType pTransType) {
			theTransType = pTransType; 
			theTransId = (pTransType == null) ? null : pTransType.getId(); }
		public void setAccount(Account pAccount) {
			theAccount   = pAccount; 
			theAccountId = (pAccount == null) ? null : pAccount.getId(); }
		public void setIsCredit(boolean isCredit) {
			this.isCredit = isCredit; }
		private void setAccountId(Integer pAccountId) {
			theAccountId   = pAccountId; } 
		private void setPartnerId(Integer pPartnerId) {
			thePartnerId   = pPartnerId; } 
		private void setTransId(Integer pTransId) {
			theTransId   = pTransId; } 
		private void setFreqId(Integer pFreqId) {
			theFreqId   = pFreqId; } 

		/* Constructor */
		public Values() {}
		public Values(Values pValues) 		{ copyFrom(pValues); }
		public Values(Statement.Line.Values pValues) { copyFrom(pValues); }
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(HistoryValues<Pattern> pCompare) {
			Values myValues = (Values)pCompare;
			if (!super.histEquals(pCompare))					  								return false;
			if (Date.differs(theDate,    				myValues.theDate).isDifferent())      	return false;
			if (differs(theDesc,   						myValues.theDesc).isDifferent())      	return false;
			if (differs(theAmount, 						myValues.theAmount).isDifferent())    	return false;
			if (Account.differs(thePartner, 			myValues.thePartner).isDifferent())   	return false;
			if (Frequency.differs(theFrequency, 		myValues.theFrequency).isDifferent())	return false;
			if (TransactionType.differs(theTransType,	myValues.theTransType).isDifferent()) 	return false;
			if (Account.differs(theAccount, 			myValues.theAccount).isDifferent())		return false;
			if (isCredit != myValues.isCredit) 	   							   					return false;
			if (Utils.differs(theAccountId, 			myValues.theAccountId).isDifferent())	return false;
			if (Utils.differs(thePartnerId, 			myValues.thePartnerId).isDifferent())	return false;
			if (Utils.differs(theTransId, 				myValues.theTransId).isDifferent())		return false;
			if (Utils.differs(theFreqId, 				myValues.theFreqId).isDifferent())		return false;
			return true;
		}
		
		/* Copy values */
		public HistoryValues<Pattern> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			/* Handle a Pattern Values */
			if (pSource instanceof Values) {
				Values myValues = (Values)pSource;
				super.copyFrom(myValues);
				theDate      = myValues.getDate();
				theDesc      = myValues.getDesc();
				theAmount    = myValues.getAmount();
				thePartner   = myValues.getPartner();
				theFrequency = myValues.getFrequency();
				theTransType = myValues.getTransType();
				theAccount   = myValues.getAccount();
				isCredit     = myValues.isCredit();
				theAccountId = myValues.getAccountId();
				thePartnerId = myValues.getPartnerId();
				theTransId   = myValues.getTransId();
				theFreqId    = myValues.getFreqId();
			}
			
			/* Handle a Line Values */
			else if (pSource instanceof Statement.Line.Values) {
				FinanceData				myData	 = ((List)getList()).getData();
				Statement.Line.Values 	myValues = (Statement.Line.Values)pSource;
				super.copyFrom(myValues);
				
				theDate 	 = new Date(myValues.getDate());
				theDesc 	 = new StringPair(myValues.getDesc());
				theTransType = myValues.getTransType();
				theAmount    = new MoneyPair(myValues.getAmount());
				thePartner   = myValues.getPartner();
				theFrequency = myData.getFrequencys().searchFor(FreqClass.ANNUALLY);
				theAccount   = myValues.getAccount();
				isCredit     = myValues.isCredit();
			}
		}
		
		public Difference	fieldChanged(int fieldNo, HistoryValues<Pattern> pOriginal) {
			Values 	pValues = (Values)pOriginal;
			Difference	bResult = Difference.Identical;
			switch (fieldNo) {
				case FIELD_DATE:
					bResult = (Date.differs(theDate,       			pValues.theDate));
					break;
				case FIELD_DESC:
					bResult = (differs(theDesc,						pValues.theDesc));
					break;
				case FIELD_TRNTYP:
					bResult = (TransactionType.differs(theTransType, pValues.theTransType));
					break;
				case FIELD_AMOUNT:
					bResult = (differs(theAmount,					pValues.theAmount));
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
					bResult = ((isCredit != pValues.isCredit) ? Difference.Different
															  : Difference.Identical);
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
			theDesc 	= new StringPair(theDesc.getString());
			theAmount	= new MoneyPair(theAmount.getValue());
		}		
		
		/**
		 * Ensure encryption after non-encrypted load
		 */
		protected void applySecurity() throws Exception {
			/* Apply the encryption */
			theDesc.encryptPair(null);
			theAmount.encryptPair(null);
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
		}		
	}	
}
