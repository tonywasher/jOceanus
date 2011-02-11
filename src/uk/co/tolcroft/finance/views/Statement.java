package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.DataItem.*;
import uk.co.tolcroft.models.DataItem.validationCtl.*;
import uk.co.tolcroft.models.Number;

public class Statement {
	/* Members */
	private View      		theView      	= null;
	private Account      	theAccount      = null;
	private Date.Range      theRange        = null;
	private Number.Money    theStartBalance = null;
	private Number.Money    theEndBalance   = null;
	private Number.Units    theStartUnits   = null;
	private Number.Units    theEndUnits     = null;
	private List            theLines        = null;

	/* Access methods */
	public Account       	getAccount()      { return theAccount; }
	public Date.Range       getDateRange()    { return theRange; }
	public Number.Money     getStartBalance() { return theStartBalance; }
	public Number.Money     getEndBalance()   { return theEndBalance; }
	public Number.Units     getStartUnits()   { return theStartUnits; }
	public Number.Units     getEndUnits()     { return theEndUnits; }
	public AccountType 		getActType()      { return theAccount.getActType(); }
	public List             getLines()        { return theLines; }
	public Line extractItemAt(long uIndex) {
		return theLines.get((int)uIndex); }
 	
 	/* Constructor */
	public Statement(View		pView,
					 Account 	pAccount,
			         Date.Range pRange) {
		DataSet							myData;
		Event              				myCurr;
		Event.List         				myBase;
		Line               				myLine;
		int                				myResult;
		DataList<Event>.ListIterator	myIterator;

		/* Create a copy of the account (plus surrounding list) */
		theView	   = pView;
		theAccount = pAccount;
		theRange   = pRange;
		theLines   = new List();
		
		/* Create the list of statement lines */
		theLines        = new List();
		if (hasBalance()) theStartBalance = new Number.Money(0);
		if (hasUnits())	  theStartUnits   = new Number.Units(0);
		
		/* Access the underlying data and iterator */
		myData 		= theView.getData();
		myBase 		= myData.getEvents();
		myIterator 	= myBase.listIterator(true);

		/* Loop through the Events extracting relevant elements */
		while ((myCurr = myIterator.next()) != null) {
			/* Check the range */
			myResult = pRange.compareTo(myCurr.getDate());
			
			/* Handle past limit */
			if (myResult == -1) break;
			
			/* If this Event relates to this account */
			if (myCurr.relatesTo(theAccount)) {
				/* If we are too early for the statement */
				if (myResult == 1) {
					/* If we have a balance */
					if (hasBalance()) {
						/* If the Account is Credited */
						if (pAccount.compareTo(myCurr.getCredit()) == 0) {
							/* Adjust the start balance */
							theStartBalance.addAmount(myCurr.getAmount());
						}
						else if (pAccount.compareTo(myCurr.getDebit()) == 0) {
							/* Adjust the start balance */
							theStartBalance.subtractAmount(myCurr.getAmount());
						}
					}
					
					/* If we have units */
					else if ((hasUnits()) && 
							 (myCurr.getUnits() != null)) {
						/* If the Account is Credited */
						if (pAccount.compareTo(myCurr.getCredit()) == 0) {
							/* Adjust the start balance */
							theStartUnits.addUnits(myCurr.getUnits());
						}
						else if (pAccount.compareTo(myCurr.getDebit()) == 0) {
							/* Adjust the start balance */
							if (myCurr.getTransType().isStockTakeover())
								theStartUnits.setZero();
							else
								theStartUnits.subtractUnits(myCurr.getUnits());
						}
					}
					
					/* Re-loop */
					continue;
				}
				
				/* Add a statement line to the statement */
				myLine = new Line(theLines, myCurr, theAccount);
				myLine.addToList();
			}
		}
			 
		/* reset the balance */
		resetBalance();
	}
	
 	/* recalculate balance */
	public void resetBalance() {
		Line            			myLine;
		Number.Money    			myBalance = null;
		Number.Units				myUnits   = null;
		DataList<Line>.ListIterator	myIterator;

		/* Access the iterator */
		myIterator = theLines.listIterator();
		
		/* If we don't have balances just return */
		if (!hasBalance() && !hasUnits()) return;
		
		/* Set the starting balances */
		if (hasBalance())	myBalance = new Number.Money(theStartBalance);
		if (hasUnits())		myUnits = new Number.Units(theStartUnits);
		
		/* Loop through the lines adjusting the balance */
		while ((myLine = myIterator.next()) != null) {
			/* Skip deleted lines */
			if (myLine.isDeleted()) continue;
			
			/* Adjust the value balance if required */
			if ((hasBalance()) && 
				(myLine.getAmount() != null))
				myLine.adjustBalance(myBalance);
			
			/* Adjust the units balance if required */
			if ((hasUnits()) && 
				((myLine.getUnits() != null) || 
				 (myLine.isStockTOver)))
				myLine.adjustUnits(myUnits);
		}
			
		/* Set the Ending balances */
		if (hasUnits()) 	theEndUnits = new Number.Units(myUnits);
		if (hasBalance())	theEndBalance = new Number.Money(myBalance);
	}
	
	/* Does the statement have a money balance */
	public boolean hasBalance()   { 
		return ((!theAccount.isExternal()) && (!theAccount.isPriced()));		
	}
	
	/* Does the statement have units */
	public boolean hasUnits()   { 
		return (theAccount.isPriced());		
	}
	
	/** 
	 * Apply changes in a statement back into the underlying finance objects
	 */
	public void applyChanges() {
		Event.List  myBase;
		DataSet		myData;
		
		/* Access base details */
		myData	= theView.getData();
		myBase  = myData.getEvents();
		
		/* Apply the changes from this list */
		myBase.applyChanges(theLines);
					
		/* analyse the data */
		theView.analyseData();
		
		/* Refresh windows */
		theView.refreshWindow();
	}
	
	/* The List class */
	public class List extends DataList<Line> {
		
		/* Constructors */
		public List() { super(ListStyle.EDIT, false); }
		
		/** 
	 	 * Clone a StatementLine list (never used)
	 	 * @return <code>null</code>
	 	 */
		protected List cloneIt() { return null; }
		
		/* Is this list locked */
		public boolean isLocked() { return theAccount.isLocked(); }
		
		/**
		 * Add a new item (never used)
		 */
		public DataItem addNewItem(DataItem pElement) {
			return null;}
		
		/**
		 * Add a new item to the edit list
		 * @param isCredit - ignored
		 */
		public void addNewItem(boolean        isCredit) {
			Line myLine = new Line(this, isCredit);
			myLine.addToList();
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "StatementLine"; }
		
		/** 
		 * Validate a statement
		 */
		public void validate() {
			Line        	myCurr;
			Event.List  	myList;
			ListIterator	myIterator;
			DataSet			myData;
			
			/* Clear the errors */
			clearErrors();
			
			/* Create an event list */
			myData = theView.getData();
			myList = new Event.List(myData, ListStyle.VIEW);
			
			/* Create the iterator */
			myIterator = listIterator();
			
			/* Loop through the lines */
			while ((myCurr = myIterator.next()) != null) {
				/* Validate the line */
				myCurr.validate(myList);
			}
			
			/* Determine the Edit State */
			findEditState();
		}
	}
			
	public class Line extends DataItem  {
		private Number.Money        theBalance   = null;
		private Number.Units		theBalUnits  = null;
		private boolean             isCredit     = false;
		private boolean				isCircular	 = false;
		private boolean				isStockTOver = false;

		/* Access methods */
		public Account       		getAccount()   		{ return theAccount; }
		public Values      		 	getObj()       		{ return (Values)super.getObj(); }
		public Date        			getDate()      		{ return getObj().getDate(); }
		public String               getDesc()      		{ return getObj().getDesc(); }
		public Number.Units       	getUnits()     		{ return getObj().getUnits(); }
		public Number.Money       	getAmount()    		{ return getObj().getAmount(); }
		public Account       		getPartner()   		{ return getObj().getPartner(); }
		public Number.Dilution   	getDilution() 		{ return getObj().getDilution(); }
		public Number.Money   		getTaxCredit() 		{ return getObj().getTaxCredit(); }
		public Integer   			getYears() 			{ return getObj().getYears(); }
		public TransactionType   	getTransType() 		{ return getObj().getTransType(); }
		public Number.Money       	getBalance()   		{ return theBalance; }
		public Number.Units       	getBalanceUnits() 	{ return theBalUnits; }
		public boolean              isCredit()     		{ return isCredit; }
		public boolean              isCircular()     	{ return isCircular; }
		
		/* Linking methods */
		public Event getBase() { return (Event)super.getBase(); }

		/* Field IDs */
		public static final int FIELD_ID       	= 0;
		public static final int FIELD_DATE     	= 1;
		public static final int FIELD_DESC     	= 2;
		public static final int FIELD_AMOUNT   	= 3;
		public static final int FIELD_TRNTYP   	= 4;
		public static final int FIELD_PARTNER  	= 5;
		public static final int FIELD_ACCOUNT  	= 6;
		public static final int FIELD_UNITS    	= 7;
		public static final int FIELD_CREDIT   	= 8;
		public static final int FIELD_DILUTION 	= 9;
		public static final int FIELD_TAXCREDIT	= 10;
		public static final int FIELD_YEARS   	= 11;
		public static final int NUMFIELDS	   	= 13;
		
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "StatementLine"; }
		
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
				case FIELD_ID: 	  		return "ID";
				case FIELD_DATE: 		return "Date";
				case FIELD_DESC: 		return "Description";
				case FIELD_TRNTYP: 		return "TransactionType";
				case FIELD_PARTNER: 	return "Partner";
				case FIELD_ACCOUNT: 	return "Account";
				case FIELD_AMOUNT: 		return "Amount";
				case FIELD_UNITS: 		return "Units";
				case FIELD_CREDIT:		return "IsCredit";
				case FIELD_DILUTION:	return "Dilution";
				case FIELD_TAXCREDIT:	return "TaxCredit";
				case FIELD_YEARS:		return "Years";
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
			String 		myString = "<tr><td>" + fieldName(iField) + "</td><td>";
			Values 	myObj 	 = (Values)pObj;
			switch (iField) {
				case FIELD_ID: 		
					myString += getId(); 
					break;
				case FIELD_ACCOUNT:	
					myString += Utils.formatAccount(theAccount); 
					break;
				case FIELD_DATE:	
					myString += Utils.formatDate(myObj.getDate()); 
					break;
				case FIELD_DESC:	
					myString += myObj.getDesc(); 
					break;
				case FIELD_TRNTYP: 	
					myString += Utils.formatTrans(myObj.getTransType());	
					break;
				case FIELD_PARTNER:	
					myString += Utils.formatAccount(myObj.getPartner()); 
					break;
				case FIELD_AMOUNT: 	
					myString += Utils.formatMoney(myObj.getAmount());	
					break;
				case FIELD_UNITS: 	
					myString += Utils.formatUnits(myObj.getUnits());	
					break;
				case FIELD_CREDIT: 
					myString +=	(isCredit() ? "true" : "false");
					break;
				case FIELD_TAXCREDIT: 	
					myString += Utils.formatMoney(myObj.getAmount());	
					break;
				case FIELD_YEARS:	
					myString += myObj.getYears(); 
					break;
				case FIELD_DILUTION:	
					myString += Utils.formatDilution(myObj.getDilution()); 
					break;
			}
			return myString + "</td></tr>";
		}
								
		/* Standard constructor for a newly inserted pattern */
		public Line(List           pList, 
				    boolean        isCredit) {
			super(pList, 0);
			Values myObj = new Values();
			setObj(myObj);
			this.isCredit = isCredit;
			setState(DataState.NEW);
		}

		/* Standard constructor */
		public Line(List        pList,
				    Event   	pEvent,
					Account 	pAccount) {
			/* Make this an element */
			super(pList, 0);
			Values myObj = new Values();
			setObj(myObj);
			myObj.setDate(pEvent.getDate());
			myObj.setDesc(pEvent.getDesc());
			myObj.setAmount(pEvent.getAmount());
			myObj.setUnits(pEvent.getUnits());
			myObj.setTransType(pEvent.getTransType());
			myObj.setDilution(pEvent.getDilution());
			myObj.setTaxCredit(pEvent.getTaxCredit());
			myObj.setYears(pEvent.getYears());
			setBase(pEvent);
			setState(DataState.CLEAN);

			/* If the account is debited */
			if (pAccount.compareTo(pEvent.getDebit()) == 0) {
				myObj.setPartner(pEvent.getCredit());
				isCredit   = false;
			}
			
			/* If the Account is Credited */
			else if (pAccount.compareTo(pEvent.getCredit()) == 0) {
				myObj.setPartner(pEvent.getDebit());
				isCredit   = true;
			}
		}
					
		/**
		 * Validate the line
		 */
		public void validate() { validate(null); }
		public void validate(Event.List pList) {
			Event        myEvent;
			errorElement myError;
			int          iField;
			DataSet		 myData;
		
			/* Access DataSet */
			myData = theView.getData();
			/* Create a new Event list */
			if (pList == null)
				pList = new Event.List(myData, ListStyle.VIEW);
		
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
						iField = Line.FIELD_DATE; break;
					case Event.FIELD_DESC: 
						iField = Line.FIELD_DESC; break;
					case Event.FIELD_AMOUNT: 
						iField = Line.FIELD_AMOUNT; break;
					case Event.FIELD_TRNTYP: 
						iField = Line.FIELD_TRNTYP; break;
					case Event.FIELD_UNITS: 
						iField = Line.FIELD_UNITS; break;
					case Event.FIELD_DILUTION: 
						iField = Line.FIELD_DILUTION; break;
					case Event.FIELD_TAXCREDIT: 
						iField = Line.FIELD_TAXCREDIT; break;
					case Event.FIELD_YEARS: 
						iField = Line.FIELD_YEARS; break;
					case Event.FIELD_DEBIT: 
						iField = (isCredit())
									?  Line.FIELD_PARTNER
								    :  Line.FIELD_ACCOUNT; 
						break;
					case Event.FIELD_CREDIT: 
						iField = (isCredit())
									?  Line.FIELD_ACCOUNT
								    :  Line.FIELD_PARTNER; 
						break;
					default: iField = Line.FIELD_ACCOUNT;
						break;
				}	
					
				/* Add an error event to this object */
				addError(myError.getError(), iField);
			}
			
			/* Set validation flag */
			if (!hasErrors()) setValidEdit();
		}
		
		/**
		 * Compare the line
		 */
		public boolean equals(Object that) { return (this == that); }
		
		/**
		 *  Adjust Balance for a statement line
		 *  
		 *   @param curBalance current balance
		 */
		public void adjustBalance(Number.Money curBalance) {
			/* adjust the balance */
			if ((isCredit) || (isCircular))
				curBalance.addAmount(getAmount());
			else
				curBalance.subtractAmount(getAmount());
			   
			/* Record the balance */
			theBalance = new Number.Money(curBalance);
		}
		
		/**
		 *  Adjust Units Balance for a statement line
		 *  
		 *   @param curBalance current balance
		 */
		public void adjustUnits(Number.Units curBalance) {
			/* adjust the balance */
			if ((isCredit) || (isCircular))
				curBalance.addUnits(getUnits());
			else if (isStockTOver)
				curBalance.setZero();
			else 
				curBalance.subtractUnits(getUnits());
			   
			/* Record the balance */
			theBalUnits = new Number.Units(curBalance);
		}
		
		/**
		 * Determines whether a line is locked to updates
		 * 
		 * @return true/false 
		 */
		public boolean isLocked() {
			Account myPartner = getPartner();
			
			/* Check credit and debit accounts */
			return ((myPartner != null) &&
					((getPartner().isClosed()) ||
					 (theAccount.isClosed())));
		}
			
		/**
		 * Compare this line to another to establish sort order. 
		 * @param pThat The Line to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		public int compareTo(Object pThat) {
			int iDiff;
			
			/* Handle the trivial cases */
			if (this == pThat) return 0;
			if (pThat == null) return -1;
			
			/* Make sure that the object is Statement Line */
			if (pThat.getClass() != this.getClass()) return -1;
			
			/* Access the object as a Line */
			Line myThat = (Line)pThat;

			/* Compare the account */
			iDiff = getAccount().compareTo(myThat.getAccount());
			if (iDiff != 0) return iDiff;
			
			/* Compare the date */
			if (this.getDate() == null) return 1;
			if (myThat.getDate() == null) return -1;
			iDiff = getDate().compareTo(myThat.getDate());
			if (iDiff < 0) return -1;
			if (iDiff > 0) return 1;
			
			/* Compare the transaction type */
			if (this.getTransType() == null) return 1;
			if (myThat.getTransType() == null) return -1;
			iDiff = getTransType().compareTo(myThat.getTransType());
			if (iDiff < 0) return -1;
			if (iDiff > 0) return 1;
			
			/* Compare the description */
			if (this.getDesc() == null) return 1;
			if (myThat.getDesc() == null) return -1;
			iDiff = getDesc().compareTo(myThat.getDesc());
			if (iDiff < 0) return -1;
			if (iDiff > 0) return 1;
			
			/* Return equality */
			return 0;
		}

		/**
		 * Set a new partner 
		 * 
		 * @param pPartner the new partner 
		 */
		public void setPartner(Account pPartner) {
			getObj().setPartner(pPartner);
			isCircular = !Utils.differs(theAccount, pPartner);
		}
		
		/**
		 * Set a new transtype 
		 * 
		 * @param pTranType the transtype 
		 */
		public void setTransType(TransactionType pTranType) {
			getObj().setTransType(pTranType);
			isStockTOver = ((pTranType != null) && (pTranType.isStockTakeover()));
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
		public void setAmount(Number.Money pAmount) {
			getObj().setAmount((pAmount == null) ? null 
					                             : new Number.Money(pAmount));
		}
		
		/**
		 * Set a new units 
		 * 
		 * @param pUnits the units 
		 */
		public void setUnits(Number.Units pUnits) {
			getObj().setUnits((pUnits == null) ? null : new Number.Units(pUnits));
		}
		
		/**
		 * Set a new dilution
		 * 
		 * @param pDilution the dilution 
		 */
		public void setDilution(Number.Dilution pDilution) {
			getObj().setDilution((pDilution == null) ? null : new Number.Dilution(pDilution));
		}
		
		/**
		 * Set a new tax credit
		 * 
		 * @param pTaxCredit the tax credit 
		 */
		public void setTaxCredit(Number.Money pTaxCredit) {
			getObj().setTaxCredit((pTaxCredit == null) ? null : new Number.Money(pTaxCredit));
		}
		
		/**
		 * Set a new years 
		 * 
		 * @param pYears the years 
		 */
		public void setYears(Integer pYears) {
			getObj().setYears((pYears == null) ? null : new Integer(pYears));
		}
		
		/**
		 * Set a new date 
		 * 
		 * @param pDate the new date 
		 */
		public void setDate(Date pDate) {
			getObj().setDate((pDate == null) ? null : new Date(pDate));
		}
	}
	
	/**
	 *  Values for a line 
	 */
	public class Values implements histObject {
		private Date       		theDate      = null;
		private String          theDesc      = null;
		private Number.Money    theAmount    = null;
		private Account         thePartner   = null;
		private Number.Units    theUnits     = null;
		private Number.Dilution	theDilution  = null;
		private Number.Money	theTaxCredit = null;
		private Integer			theYears  	 = null;
		private TransactionType	theTransType = null;
		
		/* Access methods */
		public Date       		getDate()      { return theDate; }
		public String           getDesc()      { return theDesc; }
		public Number.Money     getAmount()    { return theAmount; }
		public Account          getPartner()   { return thePartner; }
		public Number.Units     getUnits()     { return theUnits; }
		public Number.Dilution  getDilution()  { return theDilution; }
		public Number.Money     getTaxCredit() { return theTaxCredit; }
		public Integer     		getYears()     { return theYears; }
		public TransactionType	getTransType() { return theTransType; }
		
		public void setDate(Date pDate) {
			theDate      = pDate; }
		public void setDesc(String pDesc) {
			theDesc      = pDesc; }
		public void setAmount(Number.Money pAmount) {
			theAmount    = pAmount; }
		public void setPartner(Account pPartner) {
			thePartner   = pPartner; }
		public void setUnits(Number.Units pUnits) {
			theUnits     = pUnits; }
		public void setDilution(Number.Dilution pDilution) {
			theDilution  = pDilution; }
		public void setTaxCredit(Number.Money pTaxCredit) {
			theTaxCredit = pTaxCredit; }
		public void setYears(Integer pYears) {
			theYears     = pYears; }
		public void setTransType(TransactionType pTransType) {
			theTransType = pTransType; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) {
			theDate      = pValues.getDate();
			theDesc      = pValues.getDesc();
			theAmount    = pValues.getAmount();
			thePartner   = pValues.getPartner();
			theUnits     = pValues.getUnits();
			theDilution  = pValues.getDilution();
			theTaxCredit = pValues.getTaxCredit();
			theYears     = pValues.getYears();
			theTransType = pValues.getTransType();
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
			if (Utils.differs(thePartner,   pValues.thePartner))   return false;
			if (Utils.differs(theDilution,  pValues.theDilution))  return false;
			if (Utils.differs(theTaxCredit, pValues.theTaxCredit)) return false;
			if (Utils.differs(theYears,     pValues.theYears))     return false;
			if (Utils.differs(theTransType, pValues.theTransType)) return false;
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
			theUnits     = pValues.getUnits();
			theDilution  = pValues.getDilution();
			theTaxCredit = pValues.getTaxCredit();
			theYears     = pValues.getYears();
			theTransType = pValues.getTransType();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values	pValues = (Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case Statement.Line.FIELD_DATE:
					bResult = (Utils.differs(theDate,      pValues.theDate));
					break;
				case Statement.Line.FIELD_DESC:
					bResult = (Utils.differs(theDesc,      pValues.theDesc));
					break;
				case Statement.Line.FIELD_AMOUNT:
					bResult = (Utils.differs(theAmount,    pValues.theAmount));
					break;
				case Statement.Line.FIELD_PARTNER:
					bResult = (Utils.differs(thePartner,   pValues.thePartner));
					break;
				case Statement.Line.FIELD_UNITS:
					bResult = (Utils.differs(theUnits,     pValues.theUnits));
					break;
				case Statement.Line.FIELD_TRNTYP:
					bResult = (Utils.differs(theTransType, pValues.theTransType));
					break;
			}
			return bResult;
		}
	}
}
