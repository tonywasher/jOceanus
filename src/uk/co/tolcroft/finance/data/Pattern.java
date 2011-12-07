package uk.co.tolcroft.finance.data;

import java.util.Calendar;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Date.Range;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.help.DebugDetail;
import uk.co.tolcroft.finance.data.StaticClass.*;
import uk.co.tolcroft.finance.views.*;

public class Pattern extends Event {
	/**
	 * The name of the object
	 */
	public static final String objName = "Pattern";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";			

	/**
	 * The interesting date range
	 */
	public final static Range	thePatternRange	= new Range(new Date(1999, Calendar.APRIL, 6), 
															new Date(2000, Calendar.APRIL, 5));
	
	/* Access methods */
	public  Values         	getValues()     { return (Values)super.getValues(); }	
	public  Account         getPartner()   	{ return getValues().getPartner(); }
	public  Frequency   	getFrequency() 	{ return getValues().getFrequency(); }
	public  AccountType 	getActType()   	{ return getAccount().getActType(); }
	public  boolean         isCredit()     	{ return getValues().isCredit(); }
	public  Account		   	getAccount()	{ return getValues().getAccount(); }

	/* Linking methods */
	public Pattern     getBase() { return (Pattern)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_FREQ     = Event.NUMFIELDS;
	public static final int FIELD_ISCREDIT = Event.NUMFIELDS+1;
	public static final int NUMFIELDS	   = Event.NUMFIELDS+2;
	
	/* Virtual Field IDs */
	public static final int VFIELD_ACCOUNT = NUMFIELDS;
	public static final int VFIELD_PARTNER = NUMFIELDS+1;

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
			case FIELD_FREQ:		return "Frequency";
			case FIELD_ISCREDIT:	return "IsCredit";
			case VFIELD_ACCOUNT:	return "Account";
			case VFIELD_PARTNER:	return "Partner";
			default:		  		return Event.fieldName(iField);
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
		Values 	myValues  = (Values)pValues;
		switch (iField) {
			case FIELD_FREQ:	
				if ((myValues.getFrequency() == null) &&
					(myValues.getFreqId() != null))
					myString += "Id=" + myValues.getFreqId();
				else
					myString += Frequency.format(myValues.getFrequency()); 
				myString = pDetail.addDebugLink(myValues.getFrequency(), myString);
				break;
			case FIELD_ISCREDIT: 
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
	protected HistoryValues<Event> getNewValues() { return new Values(); }
	
	/**
	 * Construct a copy of a Pattern
	 * @param pPattern The Pattern 
	 */
	protected Pattern(List pList, Pattern pPattern) {
		/* Simply initialise as Event */
		super(pList, pPattern);
		
		/* Copy the id */
		setId(pPattern.getId());
		
		/* Access source style */
		ListStyle myOldStyle = pPattern.getList().getStyle();

		/* Switch on the ListStyle */
		switch (pList.getStyle()) {
			case EDIT:
				/* If this is a view creation */
				if (myOldStyle == ListStyle.CORE) {
					/* Event is based on the original element */
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
		/* Initialise as Event */
		super(pList);
		
		/* Ensure that this is a debit from this account */
		Values myValues = getValues();
		myValues.setAccount(pList.getAccount());
	}

	/* Construct a new pattern from a statement line */
	public Pattern(List pList, Statement.Line pLine) {
		/* Set standard values */
		super(pList);
		Values 					myValues	= getValues();

		/* Initialise values */
		myValues.copyFrom(pLine.getValues());
		pList.setNewId(this);		
		
		/* Adjust the date so that it is in the correct range */
		Date myDate = new Date(getDate());
		while (myDate.compareTo(thePatternRange.getEnd())   > 0) myDate.adjustYear(-1);
		while (myDate.compareTo(thePatternRange.getStart()) < 0) myDate.adjustYear(1);
		myValues.setDate(myDate);
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
		/* Initialise item assuming account as debit and partner as credit */
		super(pList, uId, uControlId, pDate, pDesc, 
			  uAccountId, uPartnerId,
			  uTransId, pAmount, null, null, null, null);
		
		/* Local variables */
		Frequency 		myFreq;
		
		/* Initialise values */
		Values myValues = getValues();
		
		/* Record the IDs */
		myValues.setFreqId(uFreqId);
		
		/* Access the Frequencys */
		FinanceData	myData 	= pList.getData();
		myFreq = myData.getFrequencys().searchFor(uFreqId);
		if (myFreq == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Frequency Id");
		myValues.setFrequency(myFreq);

		/* Record the isCredit Flag */
		myValues.setIsCredit(isCredit);
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
		/* Initialise item assuming account as debit and partner as credit */
		super(pList, uId, pDate, pDesc, 
			  pAccount, pPartner,
			  pTransType, pAmount, null, null, null, null);
		
		/* Initialise values */
		Values myValues = getValues();

		/* Record the values */
		myValues.setFrequency(pFrequency);
		myValues.setIsCredit(isCredit);
	}
			
	/**
	 * Compare this pattern to another to establish equality.
	 * 
	 * @param pThat The pattern to compare to
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
		return getValues().histEquals(myThat.getValues()).isIdentical();
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

		/* If the date differs */
		if (this.getDate() != myThat.getDate()) {
			/* Handle null dates */
			if (this.getDate() == null) return 1;
			if (myThat.getDate() == null) return -1;
			
			/* Compare the dates */
			iDiff = getDate().compareTo(myThat.getDate());
			if (iDiff != 0) return iDiff;
		}
		
		/* Compare the accounts */
		iDiff = getAccount().compareTo(myThat.getAccount());
		if (iDiff != 0) return iDiff;

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
		/* Update the Event details */
		super.reBuildLinks(pData);
		
		/* Access Lists */
		Frequency.List 			myFrequencys	= pData.getFrequencys();
		
		/* Update frequency to use the local copy */
		Values 	myValues   	= getValues();
		Frequency	myFreq		= myValues.getFrequency();
		Frequency	myNewFreq 	= myFrequencys.searchFor(myFreq.getId());
		myValues.setFrequency(myNewFreq);
	}

	/**
	 * Validate the pattern
	 */
	public void validate() {		
		/* Check that frequency is non-null */
		if (getFrequency() == null) 
			addError("Frequency must be non-null", FIELD_FREQ);
		else if (!getFrequency().getEnabled()) 
			addError("Frequency must be enabled", FIELD_FREQ);
		
		/* Validate it */
		super.validate();
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
		FinanceData myData = pEvents.getData();
		myList = myData.getTaxYears();
		
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
			
			/* Calculate the difference in years */
			iAdjust = pTaxYear.getDate().getYear() 
						- thePatternRange.getEnd().getYear();
		
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
				
				/* Calculate the difference in years */
				iAdjust = pTaxYear.getDate().getYear() 
							- thePatternRange.getEnd().getYear();
			
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
	 * Set a new frequency 
	 * 
	 * @param pFrequency the frequency 
	 */
	public void setFrequency(Frequency pFrequency) {
		getValues().setFrequency(pFrequency);
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
		
		/* Update the isCredit if required */
		if (isCredit() != myPattern.isCredit()) 
			setIsCredit(myPattern.isCredit());
		
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
	 * Add an error for this item
	 * @param pError the error text
	 * @param iField the associated field
	 */
	protected void					addError(String pError, int iField) {
		/* Re-Map Credit/Debit field errors */
		switch (iField) {
			case FIELD_CREDIT: iField = isCredit() ? VFIELD_ACCOUNT : VFIELD_PARTNER; break;
			case FIELD_DEBIT:  iField = isCredit() ? VFIELD_PARTNER : VFIELD_ACCOUNT; break;
		}
		
		/* Call super class */
		super.addError(pError, iField);
	}
	
	public static class List extends Event.List {
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
			super(pData);
			setRange(thePatternRange);
		}

		/**
		 * Constructor for a cloned List
		 * @param pSource the source List
		 */
		private List(List pSource) { 
			super(pSource);
			setRange(thePatternRange);
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
			Event			myCurr;
			Pattern 		myItem;
			ListIterator 	myIterator;
			
			/* Store the account */
			myList.theAccount = pAccount;
			
			/* Access the list iterator */
			myIterator = listIterator(true);
			
			/* Loop through the Prices */
			while ((myCurr = myIterator.next()) != null) {
				/* Check the account */
				myItem = (Pattern)myCurr;
				int myResult = pAccount.compareTo(myItem.getAccount());
				
				/* Skip differing accounts */
				if (myResult != 0) continue;
				
				/* Copy the item */
				myItem = new Pattern(myList, myItem);
				myList.add(myItem);
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
			/* Add underlying details */
			super.addHTMLFields(pDetail, pBuffer);
			
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
			//myPattern.setAccount(theAccount);
			/* Set the Date as the start of the range */
			myPattern.setDate(getRange().getStart());
			add(myPattern);
			return myPattern;
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }
				
		/**
		 * Mark Active items
		 */
		public void markActiveItems() {
			ListIterator 	myIterator;
			Event 			myCurr;
			Pattern			myItem;
					
			/* Access the list iterator */
			myIterator = listIterator();
			
			/* Loop through the Prices */
			while ((myCurr = myIterator.next()) != null) {
				/* * Access as a pattern */
				myItem = (Pattern)myCurr;
				
				/* Touch the patterned account */
				myItem.getAccount().touchItem(myCurr);			
				
				/* Touch the patterned partner */
				myItem.getPartner().touchItem(myCurr);			
				
				/* Touch the patterned frequency */
				myItem.getFrequency().touchItem(myCurr);			
				
				/* Touch the patterned transaction type */
				myItem.getTransType().touchItem(myCurr);			
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
	public class Values extends Event.Values {
		private Frequency		theFrequency = null;
		private boolean 		isCredit     = false;
		private Integer 		theFreqId	 = null;
		
		/* Access methods */
		public Account          getPartner()   { return (isCredit) ? getDebit() : getCredit(); }
		public Frequency  		getFrequency() { return theFrequency; }
		public Account			getAccount()   { return (isCredit) ? getCredit() : getDebit(); }
		public boolean         	isCredit()     { return isCredit; }
		private Integer        	getFreqId()    { return theFreqId; }
		
		public void setPartner(Account pPartner) {
			if (isCredit) 	setDebit(pPartner); 
			else 			setCredit(pPartner); }
		public void setFrequency(Frequency pFrequency) {
			theFrequency = pFrequency; 
			theFreqId = (pFrequency == null) ? null : pFrequency.getId(); }
		public void setAccount(Account pAccount) {
			if (isCredit) 	setCredit(pAccount); 
			else 			setDebit(pAccount); }
		public void setIsCredit(boolean isCredit) {
			/* If we are changing values */
			if (isCredit != this.isCredit) {
				/* Swap credit/debit values */
				Account myTemp = getCredit();
				setCredit(getDebit());
				setDebit(myTemp);
			}
			/* Store value */
			this.isCredit = isCredit; }
		private void setFreqId(Integer pFreqId) {
			theFreqId   = pFreqId; } 

		/* Constructor */
		public Values() {}
		public Values(Values pValues) 		{ copyFrom(pValues); }
		public Values(Statement.Line.Values pValues) { copyFrom(pValues); }
		
		/* Check whether this object is equal to that passed */
		public Difference histEquals(HistoryValues<Event> pCompare) {
			/* Make sure that the object is the same class */
			if (pCompare.getClass() != this.getClass()) return Difference.Different;
			
			/* Cast correctly */
			Values myValues = (Values)pCompare;

			/* Handle integer/boolean values */
			if ((isCredit != myValues.isCredit) ||
				(Utils.differs(theFreqId,	myValues.theFreqId).isDifferent()))
				return Difference.Different;
			
			/* Determine underlying differences */
			Difference myDifference = super.histEquals(pCompare);
			
			/* Compare underlying values */
			myDifference = myDifference.combine(Frequency.differs(theFrequency, myValues.theFrequency));

			/* Return Differences */
			return myDifference;
		}
		
		/* Copy values */
		public HistoryValues<Event> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			/* Handle a Pattern Values */
			if (pSource instanceof Values) {
				Values myValues = (Values)pSource;
				super.copyFrom(myValues);
				theFrequency = myValues.getFrequency();
				isCredit     = myValues.isCredit();
				theFreqId    = myValues.getFreqId();
				if (getTaxCredit() != null)
					System.out.println("");
			}
			
			/* Handle a Line Values */
			else if (pSource instanceof Statement.Line.Values) {
				FinanceData				myData	 = ((List)getList()).getData();
				Statement.Line.Values 	myValues = (Statement.Line.Values)pSource;
				super.copyFrom(myValues);
				theFrequency = myData.getFrequencys().searchFor(FreqClass.ANNUALLY);
				isCredit     = myValues.isCredit();
			}
		}
		
		public Difference	fieldChanged(int fieldNo, HistoryValues<Event> pOriginal) {
			Values 	pValues = (Values)pOriginal;
			Difference	bResult = Difference.Identical;
			switch (fieldNo) {
				case FIELD_FREQ:
					bResult = (Frequency.differs(theFrequency, 		pValues.theFrequency));
					break;
				case FIELD_ISCREDIT:
					bResult = ((isCredit != pValues.isCredit) ? Difference.Different
															  : Difference.Identical);
					break;
				default:
					bResult = super.fieldChanged(fieldNo, pValues);
					break;
			}
			return bResult;
		}
	}	
}
