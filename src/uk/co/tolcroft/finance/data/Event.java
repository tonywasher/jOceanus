package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.Number.*;

public class Event extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "Event";

	/**
	 * Event Description length
	 */
	public final static int DESCLEN 		= 50;

	/* Local IDs for use in loading */
	private long				theDebitId	= -1;
	private long				theCreditId	= -1;
	private long				theTransId	= -1;
	
	/* Access methods */
	public  Values         	getObj()       { return (Values)super.getObj(); }	
	public  Date      		getDate()      { return getObj().getDate(); }
	public  String          getDesc()      { return getObj().getDesc(); }
	public  Money     		getAmount()    { return getObj().getAmount(); }
	public  Account         getDebit()     { return getObj().getDebit(); }
	public  Account         getCredit()    { return getObj().getCredit(); }
	public  Units     		getUnits()     { return getObj().getUnits(); }
	public  TransactionType getTransType() { return getObj().getTransType(); }
	public  Money     		getTaxCredit() { return getObj().getTaxCredit(); }
	public  Integer	        getYears()     { return getObj().getYears(); }

	/* Linking methods */
	public Event     getBase() { return (Event)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_ID        = 0;
	public static final int FIELD_DATE      = 1;
	public static final int FIELD_DESC      = 2;
	public static final int FIELD_AMOUNT    = 3;
	public static final int FIELD_DEBIT     = 4;
	public static final int FIELD_CREDIT    = 5;
	public static final int FIELD_UNITS     = 6;
	public static final int FIELD_TRNTYP    = 7;
	public static final int FIELD_TAXCREDIT = 8;
	public static final int FIELD_YEARS     = 9;
	public static final int NUMFIELDS	    = 10;
			
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
			case FIELD_DATE:		return "Date";
			case FIELD_DESC:		return "Description";
			case FIELD_AMOUNT:		return "Amount";
			case FIELD_DEBIT:		return "Debit";
			case FIELD_CREDIT:		return "Credit";
			case FIELD_UNITS:		return "Units";
			case FIELD_TAXCREDIT:	return "TaxCredit";
			case FIELD_YEARS:		return "Years";
			case FIELD_TRNTYP:		return "TransactionType";
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
			case FIELD_DATE:	
				myString += Utils.formatDate(myObj.getDate()); 
				break;
			case FIELD_DESC:	
				myString += myObj.getDesc(); 
				break;
			case FIELD_TRNTYP: 	
				if ((myObj.getTransType() == null) &&
					(theTransId != -1))
					myString += "Id=" + theDebitId;
				else
					myString += Utils.formatTrans(myObj.getTransType());	
				break;
			case FIELD_DEBIT:
				if ((myObj.getDebit() == null) &&
					(theDebitId != -1))
					myString += "Id=" + theDebitId;
				else
					myString += Utils.formatAccount(myObj.getDebit()); 
				break;
			case FIELD_CREDIT:	
				if ((myObj.getCredit() == null) &&
					(theCreditId != -1))
					myString += "Id=" + theCreditId;
				else
					myString += Utils.formatAccount(myObj.getCredit()); 
				break;
			case FIELD_AMOUNT: 	
				myString += Utils.formatMoney(myObj.getAmount());	
				break;
			case FIELD_UNITS: 	
				myString += Utils.formatUnits(myObj.getUnits());	
				break;
			case FIELD_TAXCREDIT:	
				myString += Utils.formatMoney(myObj.getTaxCredit()); 
				break;
			case FIELD_YEARS:	
				myString += myObj.getYears(); 
				break;
		}
		return myString + "</td></tr>";
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
	
		/* Switch on the ListStyle */
		switch (pList.getStyle()) {
			case CORE:
				pList.setNewId(this);				
				break;
			case EDIT:
				setBase(pEvent);
				setState(DataState.CLEAN);
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
	public Event(List           pList,
		         Statement.Line	pLine) {
	
		/* Set standard values */
		super(pList, 0);
		Values myObj = new Values();
		setObj(myObj);
		myObj.setDate(pLine.getDate());
		myObj.setDesc(pLine.getDesc());
		myObj.setAmount(pLine.getAmount());
		myObj.setUnits(pLine.getUnits());
		myObj.setTransType(pLine.getTransType());
		
		/* If the event needs a Tax Credit */
		if (getTransType().needsTaxCredit()) {
			/* Set a new null tax credit */
			myObj.setTaxCredit(new Money(0));
			
			/* If the event has tax years */
			if (getTransType().isTaxableGain()) {
				/* Set a new years value */
				myObj.setYears(new Integer(1));
			}
		}

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
			
		/* Allocate the id if adding to core */
		if (pList.getStyle() == ListStyle.CORE)
			pList.setNewId(this);				
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
		Values myObj = new Values();
		setObj(myObj);
		myObj.setDate(pLine.getDate());
		myObj.setDesc(pLine.getDesc());
		myObj.setAmount(pLine.getAmount());
		myObj.setTransType(pLine.getTransType());
	
		/* If the event needs a Tax Credit */
		if (getTransType().needsTaxCredit()) {
			/* Set a new null tax credit */
			myObj.setTaxCredit(new Money(0));
			
			/* If the event has tax years */
			if (getTransType().isTaxableGain()) {
				/* Set a new years value */
				myObj.setYears(new Integer(1));
			}
		}

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
			
		/* Allocate the id if adding to core */
		if (pList.getStyle() == ListStyle.CORE)
			pList.setNewId(this);				
	}
	
	/* Standard constructor for a newly inserted event */
	public Event(List pList) {
		super(pList, 0);
		Values theObj = new Values();
		setObj(theObj);
		setState(DataState.NEW);
	}

	/* Standard constructor */
	public Event(List      		pList,
			     long           uId, 
		         java.util.Date pDate,
		         String         sDesc,
		         long           uDebit,
		         long	        uCredit,
		         long			uTransType,
		         String     	pAmount,
		         String			pUnits,
		         String			pTaxCredit,
		         Integer		pYears) throws Exception {
		/* Initialise item */
		super(pList, uId);
		
		/* Local variables */
		TransactionType	myTransType;
		Account   		myAccount;
		Account.List	myAccounts;
		
		
		/* Access account list */
		myAccounts = pList.theData.getAccounts();
		
		/* Create a new EventValues object */
		Values myObj = new Values();
		setObj(myObj);
		myObj.setDesc(sDesc);			
		
		/* Store the IDs that we will look up */
		theDebitId  = uDebit;
		theCreditId = uCredit;
		theTransId	= uTransType;
		
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
		myTransType = pList.theData.getTransTypes().searchFor(uTransType);
		if (myTransType == null)
			throw new Exception(ExceptionClass.DATA,
   					  			this, 
   					  			"Invalid Transaction Type Id");
		myObj.setTransType(myTransType);
		
		/* Record the amount */
		Money myAmount = Money.Parse(pAmount);
		if (myAmount == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Amount: " + pAmount);
		myObj.setAmount(myAmount);
		
		/* If there is tax credit */
		if (pTaxCredit != null) {
			/* Record the relief */
			myAmount = Money.Parse(pTaxCredit);
			if (myAmount == null) 
				throw new Exception(ExceptionClass.DATA,
									this,
									"Invalid TaxCredit: " + pTaxCredit);
			myObj.setTaxCredit(myAmount);
		}

		/* Set the years */
		myObj.setYears(pYears);

		/* If there are units */
		if (pUnits != null) {
			/* Record the units */
			Units myUnits = Units.Parse(pUnits);
			if (myUnits == null) 
				throw new Exception(ExceptionClass.DATA,
									this,
									"Invalid Units: " + pUnits);
			myObj.setUnits(myUnits);
		}
		
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
		Event myEvent = (Event)pThat;
		
		/* Check for equality */
		if (getId() != myEvent.getId()) return false;
		if (Utils.differs(getDate(),       myEvent.getDate())) 		return false;
		if (Utils.differs(getDesc(),       myEvent.getDesc())) 		return false;
		if (Utils.differs(getTransType(),  myEvent.getTransType())) return false;
		if (Utils.differs(getAmount(),     myEvent.getAmount())) 	return false;
		if (Utils.differs(getCredit(), 	   myEvent.getCredit())) 	return false;
		if (Utils.differs(getDebit(),      myEvent.getDebit())) 	return false;
		if (Utils.differs(getUnits(),      myEvent.getUnits())) 	return false;
		if (Utils.differs(getTaxCredit(),  myEvent.getTaxCredit()))	return false;
		if (Utils.differs(getYears(),      myEvent.getYears()))		return false;
		return true;
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
			/* Handle nulls */
			if (this.getTransType() == null) return 1;
			if (myThat.getTransType() == null) return -1;
			
			/* Compare transaction types */
			iDiff = getTransType().compareTo(myThat.getTransType());
			if (iDiff != 0) return iDiff;
		}
		
		/* Compare ids */
		iDiff = (int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}

	/**
	 * Validate the event
	 */
	public void validate() {
		Date 	myDate = getDate();
		List 	myList = (List)getList();
		DataSet	mySet  = myList.theData;
				
		/* The date must be non-null */
		if ((myDate == null) || (myDate.isNull())) {
			addError("Null date is not allowed", FIELD_DATE);
		}
			
		/* The date must be in-range */
		else if (mySet.getDateRange().compareTo(myDate) != 0) {
			addError("Date must be within range", FIELD_DATE);
		}
			
		/* Debit must be non-null */
		if (getDebit() == null) {
			addError("Debit account must be non-null", FIELD_DEBIT);
		}
			
		/* Credit must be non-null */
		if (getCredit() == null) {
			addError("Credit account must be non-null", FIELD_CREDIT);
		}
			
		/* TransType must be non-null */
		if (getTransType() == null) {
			addError("TransType must be non-null", FIELD_TRNTYP);
		}
			
		/* The description must be non-null */
		if (getDesc() == null) {
			addError("Description must be non-null", FIELD_DESC);
		}
			
		/* The description must not be too long */
		if ((getDesc() != null) && (getDesc().length() > DESCLEN)) {
			addError("Description is too long", FIELD_DESC);
		}
			
		/* Credit/Debit cannot be the same unless this is a 
		 * dividend re-investment or interest payment */
		if ((!Utils.differs(getCredit(), getDebit())) &&
			(!isDividendReInvestment()) && (!isInterest())) {
			addError("Credit and debit accounts are identical", FIELD_DEBIT);
			addError("Credit and debit accounts are identical", FIELD_CREDIT);
		}
		
		/* Dividend re-investment must have identical Credit/Debit */
		if ((Utils.differs(getCredit(), getDebit())) &&
			(isDividendReInvestment())) {
			addError("Dividend re-investment requires identical credit and debit accounts", FIELD_DEBIT);
			addError("Dividend re-investment requires identical credit and debit accounts", FIELD_CREDIT);
		}
		
		/* Hidden Events are not allowed */
		if ((getTransType() != null) &&	(getTransType().isHidden())) {
			addError("Hidden transaction types are not allowed", FIELD_TRNTYP);
		}
		
		/* Check credit account */
		if ((getTransType() != null) &&	(getCredit() != null) &&
			(!DataSet.isValidEvent(getTransType(), getCredit().getActType(), true)))
				addError("Invalid credit account for transaction", FIELD_CREDIT);
		
		/* Check debit account */
		if ((getTransType() != null) &&	(getDebit() != null) &&
			(!DataSet.isValidEvent(getTransType(), getDebit().getActType(), false)))
				addError("Invalid debit account for transaction", FIELD_CREDIT);
		
		/* If we have units */
		if (getUnits() != null) { 
			/* If we have credit/debit accounts */
			if ((getDebit() != null) && (getCredit() != null)) {				
				/* Units are only allowed if credit or debit is priced */
				if ((!getCredit().isPriced()) && (!getDebit().isPriced())) {
					addError("Units are only allowed involving a single asset", 
							 FIELD_UNITS);
				}

				/* If both credit/debit are both priced they must be identical */
				if ((getCredit().isPriced()) && (getDebit().isPriced()) &&
					(Utils.differs(getCredit(), getDebit()))) {
					addError("Units can only refer to a single priced asset", 
							 FIELD_UNITS);
				}
			}
			
			/* Units must not be negative */
			if ((!getUnits().isNonZero()) && 
				(!getUnits().isPositive())) { 
				addError("Units must be non-Zero and positive", FIELD_UNITS);
			}
		}
		
		/* Money must not be negative */
		if ((getAmount() == null) ||
			(!getAmount().isPositive())) { 
			addError("Amount cannot be negative", 
					 FIELD_AMOUNT);
		}
		
		/* If we are a taxable gain */
		if ((getTransType() != null) && (getTransType().isTaxableGain())) {
			/* Years must be positive */
			if ((getYears() == null) || (getYears() <= 0)) {
				addError("Years must be non-zero and positive", FIELD_YEARS);
			}
			
			/* Tax Credit must be non-null and positive */
			if ((getTaxCredit() == null) || (!getTaxCredit().isPositive())) {
				addError("TaxCredit must be non-null", FIELD_TAXCREDIT);
			}
		}
		
		
		/* If we need a tax credit */
		else if ((getTransType() != null) && (needsTaxCredit())) {
			/* Tax Credit must be non-null and positive */
			if ((getTaxCredit() == null) || (!getTaxCredit().isPositive())) {
				addError("TaxCredit must be non-null", FIELD_TAXCREDIT);
			}

			/* Years must be positive */
			if (getYears() != null) {
				addError("Years must be null", FIELD_YEARS);
			}
		}
		
		/* else we should not have a tax credit */
		else if (getTransType() != null) {
			/* Tax Credit must be null */
			if (getTaxCredit() != null) {
				addError("TaxCredit must be null", FIELD_TAXCREDIT);
			}

			/* Years must be null */
			if (getYears() != null) {
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
	 * Determines whether an event is a market adjustment
	 * 
	 * @return market adjustment true/false 
	 */
	protected boolean isMarketAdjustment() {
		boolean myResult = false;
	
		/* Check for market growth */
		if ((getCredit().isPriced()) &&
			(getDebit().isMarket()) &&
			(getTransType().isMarketAdjust()))
			myResult = true;
		
		/* Check for market shrink */
		else if ((getDebit().isPriced()) &&
				 (getCredit().isMarket()) &&
				 (getTransType().isMarketAdjust()))
			myResult = true;
				
		/* Return the result */
		return myResult;
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
	
		/* Check for dividend re-investment */
		if ((getTransType() != null) &&
		    (getTransType().isInterest()))
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
				myResult = !getDebit().isTaxFree();
				break;
		}
		
		/* Return the result */
		return myResult;
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
	public void setDescription(String pDesc) {
		getObj().setDesc((pDesc == null) ? null : new String(pDesc));
	}
	
	/**
	 * Set a new amount 
	 * 
	 * @param pAmount the amount 
	 */
	public void setAmount(Money pAmount) {
		getObj().setAmount((pAmount == null) ? null : new Money(pAmount));
	}
	
	/**
	 * Set a new units 
	 * 
	 * @param pUnits the units 
	 */
	public void setUnits(Units pUnits) {
		getObj().setUnits((pUnits == null) ? null : new Units(pUnits));
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
	public void setTaxCredit(Money pAmount) {
		getObj().setTaxCredit(pAmount);
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
	 * Update event from an element 
	 * 
	 * @param pItem the changed element 
	 */
	public void applyChanges(DataItem pItem){
		if (pItem instanceof Event) {
			Event myEvent = (Event)pItem;
			applyChanges(myEvent);
		}
		else if (pItem instanceof Statement.Line) {
			Statement.Line myLine = (Statement.Line)pItem;
			applyChanges(myLine);
		}
	}
	
	/**
	 * Update event from a Statement Line 
	 * 
	 * @param pLine the changed line 
	 */
	private void applyChanges(Statement.Line pLine) {
		/* Store the current detail into history */
		pushHistory();
		
		/* Update the date if required */
		if (Utils.differs(getDate(), pLine.getDate())) 
			setDate(pLine.getDate());
	
		/* Update the description if required */
		if (Utils.differs(getDesc(), pLine.getDesc()))
			setDescription(pLine.getDesc());
		
		/* Update the amount if required */
		if (Utils.differs(getAmount(), pLine.getAmount())) 
			setAmount(pLine.getAmount());
		
		/* Update the units if required */
		if (Utils.differs(getUnits(), pLine.getUnits())) 
			setUnits(pLine.getUnits());
				
		/* If the transType has changed */
		if (Utils.differs(getTransType(), pLine.getTransType())) {
			/* Set the new transtype */
			setTransType(pLine.getTransType());
			
			/* Sort out new or deleted Tax Credit */
			if (getTransType().needsTaxCredit()) {
				if (getTaxCredit() == null)	setTaxCredit(new Money(0)); 
			} else {
				setTaxCredit(null);
			}
					
			/* Sort out new or deleted Tax Years */
			if (getTransType().isTaxableGain()) {
				if (getYears() == null)	setYears(new Integer(1)); 
			} else {
				setYears(null);
			}
		}
	
		/* If this is a credit */
		if (pLine.isCredit()) {			
			/* Update the debit if required */
			if (Utils.differs(getDebit(), pLine.getPartner())) 
				setDebit(pLine.getPartner());
		} else {
			/* Update the credit if required */
			if (Utils.differs(getCredit(), pLine.getPartner())) 
				setCredit(pLine.getPartner());
		}
		
		/* Check for changes */
		if (checkForHistory()) setState(DataState.CHANGED);
	}
	
	/**
	 * Update event from an Event extract 
	 * 
	 * @param pEvent the changed event 
	 */
	private void applyChanges(Event pEvent) {
		/* Store the current detail into history */
		pushHistory();
		
		/* Update the date if required */
		if (Utils.differs(getDate(), pEvent.getDate())) 
			setDate(pEvent.getDate());
	
		/* Update the description if required */
		if (Utils.differs(getDesc(), pEvent.getDesc())) 
			setDescription(pEvent.getDesc());
		
		/* Update the amount if required */
		if (Utils.differs(getAmount(), pEvent.getAmount())) 
			setAmount(pEvent.getAmount());
		
		/* Update the units if required */
		if (Utils.differs(getUnits(), pEvent.getUnits())) 
			setUnits(pEvent.getUnits());
				
		/* Update the tranType if required */
		if (Utils.differs(getTransType(), pEvent.getTransType())) 
			setTransType(pEvent.getTransType());
	
		/* Update the debit if required */
		if (Utils.differs(getDebit(), pEvent.getDebit())) 
			setDebit(pEvent.getDebit());
	
		/* Update the credit if required */
		if (Utils.differs(getCredit(), pEvent.getCredit())) 
			setCredit(pEvent.getCredit());		
		
		/* Update the tax credit if required */
		if (Utils.differs(getTaxCredit(), pEvent.getTaxCredit())) 
			setTaxCredit(pEvent.getTaxCredit());
	
		/* Update the years if required */
		if (Utils.differs(getYears(), pEvent.getYears())) 
			setYears(pEvent.getYears());
		
		/* Check for changes */
		if (checkForHistory()) setState(DataState.CHANGED);
	}

	/**
	 *  List class for Events 
	 */
	public static class List extends DataList<Event> {
		private DataSet	theData			= null;

		/** 
	 	 * Construct an empty CORE event list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(DataSet pData) { 
			super(ListStyle.CORE, false);
			theData = pData;
		}

		/** 
	 	 * Construct an empty generic event list
	 	 * @param pData the DataSet for the list
	 	 */
		public List(DataSet pData, ListStyle pStyle) { 
			super(pStyle, false);
			theData = pData;
		}

		/** 
	 	 * Construct a generic event list
	 	 * @param pList the source event list 
	 	 * @param pStyle the style of the list 
	 	 */
		public List(List pList, ListStyle pStyle) {
			super(pList, pStyle);
			theData = pList.theData;
		}

		/** 
	 	 * Construct a difference event list
	 	 * @param pNew the new Event list 
	 	 * @param pOld the old Event list 
	 	 */
		protected List(List pNew, List pOld) { 
			super(pNew, pOld);
			theData = pNew.theData;
		}
	
		/** 
	 	 * Clone an Event list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return new List(this, ListStyle.CORE); }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pItem the item to add
		 * @return the newly added item
		 */
		public DataItem addNewItem(DataItem pItem) {
			if (pItem instanceof Event) {
				Event myEvent = new Event(this, (Event)pItem);
				myEvent.addToList();
				return myEvent;
			}
			else if (pItem instanceof Statement.Line) {
				Event myEvent = new Event(this, (Statement.Line)pItem);
				myEvent.addToList();
				return myEvent;
			}
			else return null;
		}
	
		/**
		 * Add a new item to the edit list
		 * 
		 * @param isCredit - ignored
		 */
		public void addNewItem(boolean        isCredit) {
			Event myEvent = new Event(this);
			myEvent.addToList();
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }
		
		/**
		 *  Allow an event to be added
		 */
		public void addItem(long     		uId,
				            java.util.Date	pDate,
				            String   		pDesc,
				            String   		pAmount,
				            String   		pDebit,
				            String   		pCredit,
				            String   		pUnits,
				            String   		pTransType,
				            String   		pTaxCredit,
				            Integer   		pYears) throws Exception {
			Account.List	myAccounts;
			Account         myDebit;
			Account         myCredit;
			TransactionType	myTransType;
				
			/* Access the accounts */
			myAccounts   = theData.getAccounts();
			
			/* Look up the Transaction Type */
			myTransType = theData.getTransTypes().searchFor(pTransType);
			if (myTransType == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Event on <" + 
			                        Utils.formatDate(new Date(pDate)) +
			                        "> has invalid Transact Type <" + pTransType + ">");
			
			/* Look up the Credit Account */
			myCredit = myAccounts.searchFor(pCredit);
			if (myCredit == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Event on <" + 
			                        Utils.formatDate(new Date(pDate)) +
			                        "> has invalid Credit account <" + pCredit + ">");
			
			/* Look up the Debit Account */
			myDebit = myAccounts.searchFor(pDebit);
			if (myDebit == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Event on <" + 
			                        Utils.formatDate(new Date(pDate)) +
			                        "> has invalid Debit account <" + pDebit + ">");
			
			/* Add the event */
			addItem(uId,
				    pDate,
				    pDesc,
				    pAmount,
					myDebit.getId(),
					myCredit.getId(),
					pUnits,
					myTransType.getId(), 
					pTaxCredit, 
					pYears);
		}
			
		/**
		 *  Allow an event to be added
		 */
		public void addItem(long     		uId,
				            java.util.Date  pDate,
				            String   		pDesc,
				            String   		pAmount,
				            long     		uDebitId,
				            long     		uCreditId,
				            String   		pUnits,
				            long  	  		uTransId,
				            String   		pTaxCredit,
				            Integer    		pYears) throws Exception {
			Event	myEvent;
			
			/* Create the new Event */
			myEvent = new Event(this, uId, pDate, pDesc,
					            uDebitId, uCreditId, uTransId, 
					            pAmount, pUnits, pTaxCredit, pYears);
			
			/* Check that this EventId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
									myEvent,
			  			            "Duplicate EventId");
			 
			/* If this is not a market adjustment to a priced item */
			if (!myEvent.isMarketAdjustment()) {
				/* Validate the event */
				myEvent.validate();

				/* Handle validation failure */
				if (myEvent.hasErrors()) 
					throw new Exception(ExceptionClass.VALIDATE,
										myEvent,
										"Failed validation");
					
				/* Add the Event to the list */
				myEvent.addToList();
			}
		}		
	}
		
	/**
	 *  Values for an event 
	 */
	public class Values implements histObject {
		private Date       		theDate      = null;
		private String          theDesc      = null;
		private Money      		theAmount    = null;
		private Account         theDebit     = null;
		private Account         theCredit    = null;
		private Units      		theUnits     = null;
		private TransactionType	theTransType = null;
		private Money      		theTaxCredit = null;
		private Integer         theYears     = null;
		
		/* Access methods */
		public Date       		getDate()      { return theDate; }
		public String           getDesc()      { return theDesc; }
		public Money      		getAmount()    { return theAmount; }
		public Account          getDebit()     { return theDebit; }
		public Account          getCredit()    { return theCredit; }
		public Units      		getUnits()     { return theUnits; }
		public TransactionType	getTransType() { return theTransType; }
		public Money      		getTaxCredit() { return theTaxCredit; }
		public Integer          getYears()     { return theYears; }
		
		public void setDate(Date pDate) {
			theDate      = pDate; }
		public void setDesc(String pDesc) {
			theDesc      = pDesc; }
		public void setAmount(Money pAmount) {
			theAmount    = pAmount; }
		public void setDebit(Account pDebit) {
			theDebit     = pDebit; }
		public void setCredit(Account pCredit) {
			theCredit    = pCredit; }
		public void setUnits(Units pUnits) {
			theUnits     = pUnits; }
		public void setTransType(TransactionType pTransType) {
			theTransType = pTransType; }
		public void setTaxCredit(Money pTaxCredit) {
			theTaxCredit = pTaxCredit; }
		public void setYears(Integer iYears) {
			theYears     = iYears; }

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
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (Utils.differs(theDate,      pValues.theDate))      return false;
			if (Utils.differs(theDesc,      pValues.theDesc))      return false;
			if (Utils.differs(theAmount,    pValues.theAmount))    return false;
			if (Utils.differs(theUnits,     pValues.theUnits))     return false;
			if (Utils.differs(theDebit,     pValues.theDebit))     return false;
			if (Utils.differs(theCredit,    pValues.theCredit))    return false;
			if (Utils.differs(theTransType, pValues.theTransType)) return false;
			if (Utils.differs(theTaxCredit, pValues.theTaxCredit)) return false;
			if (Utils.differs(theYears,     pValues.theYears))	   return false;
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
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean			bResult = false;
			switch (fieldNo) {
				case FIELD_DATE:
					bResult = (Utils.differs(theDate,      pValues.theDate));
					break;
				case FIELD_DESC:
					bResult = (Utils.differs(theDesc,      pValues.theDesc));
					break;
				case FIELD_TRNTYP:
					bResult = (Utils.differs(theTransType, pValues.theTransType));
					break;
				case FIELD_AMOUNT:
					bResult = (Utils.differs(theAmount,    pValues.theAmount));
					break;
				case FIELD_DEBIT:
					bResult = (Utils.differs(theDebit,     pValues.theDebit));
					break;
				case FIELD_CREDIT:
					bResult = (Utils.differs(theCredit,    pValues.theCredit));
					break;
				case FIELD_UNITS:
					bResult = (Utils.differs(theUnits,     pValues.theUnits));
					break;
				case FIELD_TAXCREDIT:
					bResult = (Utils.differs(theTaxCredit, pValues.theTaxCredit));
					break;
				case FIELD_YEARS:
					bResult = (Utils.differs(theYears,     pValues.theYears));
					break;
			}
			return bResult;
		}
	}	
}
