package finance;

import finance.finBuilder.IdManager;
import finance.finData.Account;
import finance.finData.Pattern;
import finance.finData.PatternList;
import finance.finData.Price;
import finance.finData.AccountList;
import finance.finData.TaxParmList;
import finance.finData.Event;
import finance.finData.EventList;
import finance.finObject.EditState;
import finance.finObject.State;
import finance.finObject.Range;
import finance.finLink.histObject;
import finance.finLink.linkObject;
import finance.finLink.itemCtl.ListStyle;
import finance.finLink.validationCtl.errorElement;

public class finView {	
	/* Members */
	private finData     theData 	 = null;
	private IdManager 	theManager	 = null;
	private Range       theRange     = null;
	private finSwing	theWindow 	 = null;

	/* Access methods */
	public finData 		getData() 			{ return theData; }
	public finSwing		getWindow()			{ return theWindow; }
	public IdManager    getIdManager()      { return theManager; }
	public Range        getRange()			{ return theRange; }
	
 	/* Constructor */
	public finView(finSwing pWindow) {
		theWindow  = pWindow;
		theData    = new finData();
		theManager = theData.getIdManager();
		theData.calculateDateRange();
		initialiseData();
	}
	
	/* Update the data for a view */ 
	public void setData(finData pData) {
		theData = pData;
		initialiseData();
		refreshWindow();
	}
	
	/* initialise the view from the data */ 
	public void initialiseData() {
		/* Access the range */
		theRange = theData.getDateRange();
	}
	
	/* refresh the window view */ 
	public void refreshWindow() {
		/* Refresh the window */
		if (theWindow != null) theWindow.refreshData();
	}
	
	/* TaxParams Extract Class */
	public class TaxParmView {
		/* Members */
		private TaxParmList 		theList 	= null;
		private finData.TaxParms	theTaxYear 	= null;
		
		/* Access methods */
		public finData.TaxParms getTaxYear() { return theTaxYear; }
		
		/* Constructor */
		public TaxParmView(finData.TaxParms pTaxYear) {
			/* Create an empty list */
			theList = theData.new TaxParmList(ListStyle.EDIT);
			
			/* Create a new tax year based on the passed tax year */
			theTaxYear = theData.new TaxParms(theList, pTaxYear);
			theTaxYear.addToList();
		}
		
		/* Constructor */
		public TaxParmView() {
			finData.TaxParmList myTaxYears;
			finData.TaxParms    myBase;
			
			/* Access the existing tax years */
			myTaxYears = theData.getTaxYears();
			
			/* Create an empty list */
			theList = theData.new TaxParmList(ListStyle.EDIT);

			/* Create a new tax year for the list */
			myBase = myTaxYears.getLast();
			theTaxYear = theData.new TaxParms(theList, myBase);
			theTaxYear.setBase(null);
			theTaxYear.setState(State.NEW);
			theTaxYear.setId(0);
						
			/* Adjust the year and add to list */
			theTaxYear.setDate(new finObject.Date(myBase.getDate()));
			theTaxYear.getDate().adjustYear(1);
			theTaxYear.addToList();
		}
				
		/** 
		 * Apply changes in an TaxParams view back into the core data
		 */
		public void applyChanges() {
			finData.TaxParmList myBase;
			
			/* Access base details */
			myBase = theData.getTaxYears();
			
			/* Apply the changes from this list */
			myBase.applyChanges(theList);
			
			/* Update Range details */
			theData.calculateDateRange();
			
			/* analyse the data */
			try { theData.analyseData(); } catch (Exception e) {}
			
			/* Refresh windows */
			refreshWindow();
		}
	}
	
	/* Account Extract Class */
	public class AccountView {
		/* Members */
		private finData.AccountList theList    = null;
		private finData.Account		theAccount = null;
		
		/* Access methods */
		public finData.Account getAccount() { return theAccount; }
		
		/* Constructor */
		public AccountView(finData.Account pAccount) {
			finData.AccountList myAccounts;
			
			/* Access the existing accounts */
			myAccounts = theData.getAccounts();
			
			/* Create a copy of the accounts */
			theList = theData.new AccountList(myAccounts, ListStyle.EDIT);

			/* Locate the account in the list */
			theAccount = theList.searchFor(pAccount.getName());
		}
		
		public AccountView(finStatic.AccountType pType) {
			finData.AccountList myAccounts;
			
			/* Access the existing accounts */
			myAccounts = theData.getAccounts();
			
			/* Create a copy of the accounts */
			theList = theData.new AccountList(myAccounts, ListStyle.EDIT);
			
			/* Create an new empty account */
			theAccount = theData.new Account(theList);
			theAccount.addToList();
			
			/* Set the type of the account */
			theAccount.setActType(pType);
			
			/* If the account is a bond */
			if (theAccount.getActType().isBond()) {
				/* Create a default maturity */
				theAccount.setMaturity(new finObject.Date());
				theAccount.getMaturity().adjustYear(1);
			}
		}
		
		/** 
		 * Apply changes in an Account view back into the core data
		 */
		public void applyChanges() {
			finData.AccountList myBase;
			
			/* Access base details */
			myBase     = theData.getAccounts();
			
			/* Apply the changes */
			myBase.applyChanges(theList);
						
			/* analyse the data */
			try { theData.analyseData(); } catch (Exception e) {}
			
			/* Refresh windows */
			refreshWindow();
		}
	}
	
	/* Rates Extract Class */
	public class RatesView {
		/* Members */
		private finData.RateList 	theList    = null;
		
		/* Access methods */
		public finData.RateList getRates() { return theList; }
		
		/* Constructor */
		public RatesView(finData.Account pAccount) {
			finData.RateList myRates;
			
			/* Access the existing rates */
			myRates = theData.getRates();
			
			/* Create a copy of the rates */
			theList = theData.new RateList(myRates, pAccount);
		}
				
		/** 
		 * Apply changes in a Rates view back into the core data
		 */
		public void applyChanges() {
			finData.RateList myBase;
			
			/* Access base details */
			myBase     = theData.getRates();
			
			/* Apply the changes */
			myBase.applyChanges(theList);
			
			/*
			 * Analyse and refresh are performed in the statement view
			 */
		}
	}
	
	/* Prices Extract Class */
	public class PricesView {
		/* Members */
		private finData.PriceList 	theList    = null;
		
		/* Access methods */
		public finData.PriceList getPrices() { return theList; }
		
		/* Constructor */
		public PricesView(finData.Account pAccount) {
			finData.PriceList myPrices;
			
			/* Access the existing prices */
			myPrices = theData.getPrices();
			
			/* Create a copy of the prices */
			theList = theData.new PriceList(myPrices, pAccount);
		}
				
		/** 
		 * Apply changes in a Prices view back into the core data
		 */
		public void applyChanges() {
			finData.PriceList myBase;
			
			/* Access base details */
			myBase     = theData.getPrices();
			
			/* Apply the changes */
			myBase.applyChanges(theList);
			
			/*
			 * Analyse and refresh are performed in the statement view
			 */
		}
	}
	
	/* Patterns Extract Class */
	public class PatternsView {
		/* Members */
		private finData.PatternList 	theList    = null;
		
		/* Access methods */
		public finData.PatternList getPatterns() { return theList; }
		
		/* Constructor */
		public PatternsView(finData.Account pAccount) {
			finData.PatternList myPatterns;
			
			/* Access the existing patterns */
			myPatterns = theData.getPatterns();
			
			/* Create a copy of the patterns */
			theList = theData.new PatternList(myPatterns, pAccount);
		}
				
		/** 
		 * Apply changes in a patterns view back into the core data
		 */
		public void applyChanges() {
			finData.PatternList myBase;
			
			/* Access base details */
			myBase     = theData.getPatterns();
			
			/* Apply the changes */
			myBase.applyChanges(theList);
			
			/*
			 * Analyse and refresh are performed in the statement view
			 */
		}
	}
	
	/* Event Extract Class */
	public class Extract {
		/* Members */
		private finObject.Range   theRange   = null;
		private finData.EventList theEvents  = null;

		/* Access methods */
		public finObject.Range   getRange()     { return theRange; }
	 	public finData.EventList getEvents()    { return theEvents; }
		public finData.Event extractItemAt(long uIndex) {
			return theEvents.extractItemAt(uIndex); }
	 	
	 	/* Constructor */
		public Extract(finObject.Range pRange) {
			EventList  myBase;
			Event      myCurr;
			Event      myEvent;
			int        myResult;
			
			/* Record range and initialise the list */
			theRange   = pRange;
			theEvents  = getData().new EventList(ListStyle.EDIT);
			
			/* Access the underlying data */
			myBase = getData().getEvents();

			/* Loop through the Events extracting relevant elements */
			for (myCurr = myBase.getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				
				/* Check the range */
				myResult = pRange.compareTo(myCurr.getDate());
				
				/* Handle out of range */
				if (myResult ==  1) continue;
				if (myResult == -1) break;
				
				/* Build the new linked event and add it to the extract */
				myEvent = getData().new Event(theEvents, myCurr);
				myEvent.addToList();
			}
		}

	 	/* Constructor */
		public Extract(finData.TaxParms pTaxYear) {
			PatternList 	myPatterns;
			Pattern     	myCurr;
			Event       	myEvent;
			finObject.Date 	myDate;
			
			/* Record range and initialise the list */
			theRange   = pTaxYear.getRange();
			theEvents  = getData().new EventList(ListStyle.EDIT);
			
			/* Access the underlying data */
			myPatterns = getData().getPatterns();

			/* Loop through the Patterns */
			for (myCurr  = myPatterns.getFirst();
			     myCurr != null;
			     myCurr  = myCurr.getNext()) {
					
				/* Skip deleted patterns */
				if (myCurr.isDeleted()) continue;
					
				/* Access a copy of the base date */
				myDate = new finObject.Date(myCurr.getDate());
					
				/* Loop while we have an event to add */
				while ((myEvent = myCurr.nextEvent(theEvents,
												   pTaxYear, 
												   myDate)) != null) {
					/* Add it to the extract */
					myEvent.addToList();
				}
			}
		}

		/** 
		 * Validate an extract
		 */
		public void validate() {
			finData.Event myCurr;

			/* Clear the errors */
			theEvents.clearErrors();
			
			/* Loop through the lines */
			for (myCurr = theEvents.getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* Validate it */
				myCurr.validate();
			}
		}
		
		/** 
		 * Apply changes in an EventExtract view back into the core data
		 */
		public void applyChanges() {
			finData.EventList myBase;
			
			/* Access base details */
			myBase = getData().getEvents();
			
			/* Apply the changes from this list */
			myBase.applyChanges(theEvents);
			
			/* analyse the data */
			try { theData.analyseData(); } catch (Exception e) {}
			
			/* Refresh windows */
			refreshWindow();
		}
	}
	
	/* LineValues */
	public static class LineValues implements finLink.histObject {
		private finObject.Date       theDate      = null;
		private String               theDesc      = null;
		private finObject.Money      theAmount    = null;
		private Account              thePartner   = null;
		private finObject.Units      theUnits     = null;
		private finStatic.TransType  theTransType = null;
		
		/* Access methods */
		public finObject.Date       getDate()      { return theDate; }
		public String               getDesc()      { return theDesc; }
		public finObject.Money      getAmount()    { return theAmount; }
		public Account              getPartner()   { return thePartner; }
		public finObject.Units      getUnits()     { return theUnits; }
		public finStatic.TransType  getTransType() { return theTransType; }
		
		public void setDate(finObject.Date pDate) {
			theDate      = pDate; }
		public void setDesc(String pDesc) {
			theDesc      = pDesc; }
		public void setAmount(finObject.Money pAmount) {
			theAmount    = pAmount; }
		public void setPartner(finData.Account pPartner) {
			thePartner   = pPartner; }
		public void setUnits(finObject.Units pUnits) {
			theUnits     = pUnits; }
		public void setTransType(finStatic.TransType pTransType) {
			theTransType = pTransType; }

		/* Constructor */
		public LineValues() {}
		public LineValues(LineValues pValues) {
			theDate      = pValues.getDate();
			theDesc      = pValues.getDesc();
			theAmount    = pValues.getAmount();
			thePartner   = pValues.getPartner();
			theUnits     = pValues.getUnits();
			theTransType = pValues.getTransType();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			LineValues myValues = (LineValues)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(LineValues pValues) {
			if (finObject.differs(theDate,      pValues.theDate))      return false;
			if (finObject.differs(theDesc,      pValues.theDesc))      return false;
			if (finObject.differs(theAmount,    pValues.theAmount))    return false;
			if (finObject.differs(theUnits,     pValues.theUnits))     return false;
			if (finObject.differs(thePartner,   pValues.thePartner))   return false;
			if (finObject.differs(theTransType, pValues.theTransType)) return false;
			return true;
		}
		
		/* Copy values */
		public void    copyFrom(histObject pSource) {
			LineValues myValues = (LineValues)pSource;
			copyFrom(myValues);
		}
		public histObject copySelf() {
			return new LineValues(this);
		}
		public void    copyFrom(LineValues pValues) {
			theDate      = pValues.getDate();
			theDesc      = pValues.getDesc();
			theAmount    = pValues.getAmount();
			thePartner   = pValues.getPartner();
			theUnits     = pValues.getUnits();
			theTransType = pValues.getTransType();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			LineValues	pValues = (LineValues)pOriginal;
			boolean		bResult = false;
			switch (fieldNo) {
				case Statement.Line.FIELD_DATE:
					bResult = (finObject.differs(theDate,      pValues.theDate));
					break;
				case Statement.Line.FIELD_DESC:
					bResult = (finObject.differs(theDesc,      pValues.theDesc));
					break;
				case Statement.Line.FIELD_AMOUNT:
					bResult = (finObject.differs(theAmount,    pValues.theAmount));
					break;
				case Statement.Line.FIELD_PARTNER:
					bResult = (finObject.differs(thePartner,   pValues.thePartner));
					break;
				case Statement.Line.FIELD_UNITS:
					bResult = (finObject.differs(theUnits,     pValues.theUnits));
					break;
				case Statement.Line.FIELD_TRNTYP:
					bResult = (finObject.differs(theTransType, pValues.theTransType));
					break;
			}
			return bResult;
		}
	}
	
	/* Statement class */
	public class Statement {
		/* Members */
		private finData.Account      theAccount      = null;
		private finObject.Range      theRange        = null;
		private finObject.Money      theStartBalance = null;
		private finObject.Money      theEndBalance   = null;
		private finObject.Units      theStartUnits   = null;
		private finObject.Units      theEndUnits     = null;
		private List                 theLines        = null;
		private boolean				 isUnits		 = false;

		/* Access methods */
		public finData.Account       getAccount()      { return theAccount; }
		public finObject.Range       getDateRange()    { return theRange; }
		public finObject.Money       getStartBalance() { return theStartBalance; }
		public finObject.Money       getEndBalance()   { return theEndBalance; }
		public finObject.Units       getStartUnits()   { return theStartUnits; }
		public finObject.Units       getEndUnits()     { return theEndUnits; }
		public finStatic.AccountType getActType()      { return theAccount.getActType(); }
		public List                  getLines()        { return theLines; }
		public Line extractItemAt(long uIndex) {
			return theLines.extractItemAt(uIndex); }
	 	
	 	/* Constructor */
		public Statement(finData.Account pAccount,
				         finObject.Range pRange,
				         boolean 		 isUnits) {
			Event              myCurr;
			EventList          myBase;
			Line               myLine;
			int                myResult;

			/* Store the passed data */
			this.isUnits = isUnits;
			
			/* Create a copy of the account (plus surrounding list) */
			theAccount = pAccount;
			theRange   = pRange;
			theLines   = new List();
			
			/* If we have a units request for and non-priced account, just return */
			if ((isUnits) && (!pAccount.isPriced())) return;
			
			/* Create the list of statement lines */
			theLines        = new List();
			if (hasBalance()) theStartBalance = new finObject.Money(0);
			if (isUnits)	  theStartUnits   = new finObject.Units(0);
			
			/* Access the underlying data */
			myBase = getData().getEvents();

			/* Loop through the Events extracting relevant elements */
			for (myCurr = myBase.getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				
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
						else if ((isUnits) && (myCurr.getUnits() != null) ) {
							/* If the Account is Credited */
							if (pAccount.compareTo(myCurr.getCredit()) == 0) {
								/* Adjust the start balance */
								theStartUnits.addUnits(myCurr.getUnits());
							}
							else if (pAccount.compareTo(myCurr.getDebit()) == 0) {
								/* Adjust the start balance */
								theStartUnits.subtractUnits(myCurr.getUnits());
							}
						}
						
						/* Re-loop */
						continue;
					}
					
					/* Skip unit lines that have no units */
					if ((isUnits) && (myCurr.getUnits() == null)) continue;
					
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
			Line               myLine;
			finObject.Money    myBalance;
			finObject.Units    myUnits;

			/* If we have a units request for and non-priced account, just return */
			if ((isUnits) && (!theAccount.isPriced())) return;
			
			/* If this statement is the units statement */
			if (isUnits) {
				/* Set the starting balance */
				myUnits = new finObject.Units(theStartUnits);
				
				/* Loop through the lines adjusting the balance */
				for (myLine = theLines.getFirst();
					 myLine != null;
					 myLine = myLine.getNext()) {						
					/* Skip deleted lines or lines with no units */
					if ((myLine.isDeleted()) || (myLine.getUnits() == null))
						continue;
					
					/* Adjust the balance */
					myLine.adjustUnits(myUnits);
				}
				
				/* Set the Ending balance */
				theEndUnits = new finObject.Units(myUnits);
			}
			
			/* If this statement has a balance */
			else if (hasBalance()) {
				/* Set the starting balance */
				myBalance = new finObject.Money(theStartBalance);
				
				/* Loop through the lines adjusting the balance */
				for (myLine = theLines.getFirst();
					 myLine != null;
					 myLine = myLine.getNext()) {
					/* Skip deleted lines or lines with no amount */
					if ((myLine.isDeleted()) || (myLine.getAmount() == null))
						continue;
					
					/* Adjust the balance */
					myLine.adjustBalance(myBalance);
				}
				
				/* Set the Ending balance */
				theEndBalance = new finObject.Money(myBalance);
			}
		}
		
		/* Does the statement have a balance */
		public boolean hasBalance()   { 
			return ((!theAccount.isExternal()) && (!theAccount.isPriced()));		
		}
		
		/** 
		 * Apply changes in a statement back into the underlying finance objects
		 */
		public void applyChanges() {
			finData.EventList    myBase;
			
			/* Access base details */
			myBase     = getData().getEvents();
			
			/* Apply the changes from this list */
			myBase.applyChanges(theLines);
						
			/* analyse the data */
			try { theData.analyseData(); } catch (Exception e) {}
			
			/* Refresh windows */
			refreshWindow();
		}
		
		/* The List class */
		public class List extends finLink.itemCtl
						  implements finSwing.tableList {
			/* Linking methods */
			public Line getFirst() { return (Line)super.getFirst(); }
			public Line getLast()  { return (Line)super.getLast(); }
			public Line searchFor(long uId) {
				return (Line)super.searchFor(uId); }
			public Line extractItemAt(long uIndex) {
				return (Line)super.extractItemAt(uIndex); }
			
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
			public finLink.itemElement addNewItem(finLink.itemElement pElement) {
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
				Line        myCurr;
				EventList   myList;
				
				/* Clear the errors */
				clearErrors();
				
				/* Create an event list */
				myList = getData().new EventList(ListStyle.VIEW);
				
				/* Loop through the lines */
				for (myCurr = getFirst();
				     myCurr != null;
				     myCurr = myCurr.getNext()) {
					/* Ignore if deleted */
					if (myCurr.isDeleted()) continue;
					
					/* Validate the line */
					myCurr.validate(myList);
				}
				
				/* Determine the Edit State */
				findEditState();
			}
		}
				
		public class Line extends finLink.itemElement
						  implements finSwing.tableElement  {
			private finObject.Money          theBalance   = null;
			private finObject.Units          theBalUnits  = null;
			private boolean                  isCredit     = false;

			/* Access methods */
			public finData.Account       getAccount()   { return theAccount; }
			public LineValues      		 getObj()       { return (LineValues)super.getObj(); }
			public finObject.Date        getDate()      { return getObj().getDate(); }
			public String                getDesc()      { return getObj().getDesc(); }
			public finObject.Units       getUnits()     { return getObj().getUnits(); }
			public finObject.Money       getAmount()    { return getObj().getAmount(); }
			public finData.Account       getPartner()   { return getObj().getPartner(); }
			public finStatic.TransType   getTransType() { return getObj().getTransType(); }
			public finObject.Money       getBalance()   { return theBalance; }
			public finObject.Units       getBalanceUnits() { return theBalUnits; }
			public boolean               isCredit()     { return isCredit; }
			
			/* Linking methods */
			public Line  getNext() { return (Line)super.getNext(); }
			public Line  getPrev() { return (Line)super.getPrev(); }
			public Event getBase() { return (Event)super.getBase(); }
			public List  getCtl()  { return (List)super.getCtl(); } 
			public finSwing.tableList getList()  { 
				return (finSwing.tableList)super.getCtl(); } 

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
			public static final int NUMFIELDS	   	= 9;
			
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
				LineValues 	myObj 	 = (LineValues)pObj;
				switch (iField) {
					case FIELD_ID: 		
						myString += getId(); 
						break;
					case FIELD_ACCOUNT:	
						myString += finObject.formatAccount(theAccount); 
						break;
					case FIELD_DATE:	
						myString += finObject.formatDate(myObj.getDate()); 
						break;
					case FIELD_DESC:	
						myString += myObj.getDesc(); 
						break;
					case FIELD_TRNTYP: 	
						myString += finObject.formatTrans(myObj.getTransType());	
						break;
					case FIELD_PARTNER:	
						myString += finObject.formatAccount(myObj.getPartner()); 
						break;
					case FIELD_AMOUNT: 	
						myString += finObject.formatMoney(myObj.getAmount());	
						break;
					case FIELD_UNITS: 	
						myString += finObject.formatUnits(myObj.getUnits());	
						break;
					case FIELD_CREDIT: 
						myString +=	(isCredit() ? "true" : "false");
						break;
				}
				return myString + "</td></tr>";
			}
									
			/* Standard constructor for a newly inserted pattern */
			public Line(List           pList, 
					    boolean        isCredit) {
				super(pList, 0);
				LineValues myObj = new LineValues();
				setObj(myObj);
				this.isCredit = isCredit;
				setState(State.NEW);
			}
	
			/* Standard constructor */
			public Line(List            pList,
					    finData.Event   pEvent,
						finData.Account pAccount) {
				/* Make this an element */
				super(pList, 0);
				LineValues myObj = new LineValues();
				setObj(myObj);
				myObj.setDate(pEvent.getDate());
				myObj.setDesc(pEvent.getDesc());
				myObj.setAmount(pEvent.getAmount());
				myObj.setUnits(pEvent.getUnits());
				myObj.setTransType(pEvent.getTransType());
				setBase(pEvent);
				setState(State.CLEAN);
			
				/* If the Account is Credited */
				if (pAccount.compareTo(pEvent.getCredit()) == 0) {
					myObj.setPartner(pEvent.getDebit());
					isCredit   = true;
				}
				else if (pAccount.compareTo(pEvent.getDebit()) == 0) {
					myObj.setPartner(pEvent.getCredit());
					isCredit   = false;
				}
			}
						
			/**
			 * Validate the line
			 */
			public void validate() { validate(null); }
			public void validate(EventList pList) {
				Event        myEvent;
				errorElement myError;
				int          iField;
			
				/* Create a new Event list */
				if (pList == null)
					pList = theData.new EventList(ListStyle.VIEW);
				
				/* Create a new event based on this line */
				myEvent = theData.new Event(pList, this);

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
			public boolean equals(finLink.itemElement that) { return false; }
			
			/**
			 *  Adjust Balance for a statement line
			 *  
			 *   @param curBalance current balance
			 */
			public void adjustBalance(finObject.Money curBalance) {
				/* adjust the balance */
				if (isCredit) curBalance.addAmount(getAmount());
				else          curBalance.subtractAmount(getAmount());
				   
				/* Record the balance */
				theBalance = new finObject.Money(curBalance);
			}
			
			/**
			 *  Adjust Units Balance for a statement line
			 *  
			 *   @param curBalance current balance
			 */
			public void adjustUnits(finObject.Units curBalance) {
				/* adjust the balance */
				if (isCredit) curBalance.addUnits(getUnits());
				else          curBalance.subtractUnits(getUnits());
				   
				/* Record the balance */
				theBalUnits = new finObject.Units(curBalance);
			}
			
			/**
			 * Determines whether a line is locked to updates
			 * 
			 * @return true/false 
			 */
			public boolean isLocked() {
				finData.Account myPartner = getPartner();
				
				/* Check credit and debit accounts */
				return ((myPartner != null) &&
						((getPartner().isClosed()) ||
						 (theAccount.isClosed())));
			}
				
			/**
			 * Override for standard method
			 */
			protected int compareTo(Line that) {
				int iDiff;
				if (this == that) return 0;
				if (that == null) return -1;
				if (this.getDate() == null) return 1;
				if (that.getDate() == null) return -1;
				iDiff = getDate().compareTo(that.getDate());
				if (iDiff < 0) return -1;
				if (iDiff > 0) return 1;
				if (this.getDesc() == that.getDesc()) return 0;
				if (this.getDesc() == null) return 1;
				if (that.getDesc() == null) return -1;
				iDiff = getDesc().compareTo(that.getDesc());
				if (iDiff < 0) return -1;
				if (iDiff > 0) return 1;
				return getTransType().compareTo(that.getTransType());
			}

			public int linkCompareTo(linkObject that) {
				Line myItem = (Line)that;
				return this.compareTo(myItem);
			}
			
			/**
			 * Set a new partner 
			 * 
			 * @param pPartner the new partner 
			 */
			protected void setPartner(finData.Account pPartner) {
				getObj().setPartner(pPartner);
			}
			
			/**
			 * Set a new transtype 
			 * 
			 * @param pTranType the transtype 
			 */
			protected void setTransType(finStatic.TransType pTranType) {
				getObj().setTransType(pTranType);
			}
			
			/**
			 * Set a new description 
			 * 
			 * @param pDesc the description 
			 */
			protected void setDescription(String pDesc) {
				getObj().setDesc((pDesc == null) ? null : new String(pDesc));
			}
			
			/**
			 * Set a new amount 
			 * 
			 * @param pAmount the amount 
			 */
			protected void setAmount(finObject.Money pAmount) {
				getObj().setAmount((pAmount == null) ? null 
						                             : new finObject.Money(pAmount));
			}
			
			/**
			 * Set a new units 
			 * 
			 * @param pUnits the units 
			 */
			protected void setUnits(finObject.Units pUnits) {
				getObj().setUnits((pUnits == null) ? null : new finObject.Units(pUnits));
			}
			
			/**
			 * Set a new date 
			 * 
			 * @param pDate the new date 
			 */
			protected void setDate(finObject.Date pDate) {
				getObj().setDate((pDate == null) ? null : new finObject.Date(pDate));
			}
		}
	}
	
	/* SpotValues */
	private static class SpotValues implements finLink.histObject {
		private finObject.Price		thePrice	= null;
		
		/* Access methods */
		public finObject.Price		getPrice()     { return thePrice; }
		
		public void setPrice(finObject.Price pPrice) {
			thePrice  = pPrice; }

		/* Constructor */
		public SpotValues() {}
		public SpotValues(SpotValues pValues) {
			thePrice     = pValues.getPrice();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			SpotValues myValues = (SpotValues)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(SpotValues pValues) {
			if (finObject.differs(thePrice,     pValues.thePrice))      return false;
			return true;
		}
		
		/* Copy values */
		public void    copyFrom(histObject pSource) {
			SpotValues myValues = (SpotValues)pSource;
			copyFrom(myValues);
		}
		public histObject copySelf() {
			return new SpotValues(this);
		}
		public void    copyFrom(SpotValues pValues) {
			thePrice     = pValues.getPrice();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			SpotValues	pValues = (SpotValues)pOriginal;
			boolean		bResult = false;
			switch (fieldNo) {
				case SpotPrices.SpotPrice.FIELD_PRICE:
					bResult = (finObject.differs(thePrice,  pValues.thePrice));
					break;
			}
			return bResult;
		}
	}
	
	/* SpotPrices class */
	public class SpotPrices {
		/* Members */
		private finObject.Date      theDate         = null;
		private List				thePrices		= null;

		/* Access methods */
		public finObject.Date        getDate()    		{ return theDate; }
		public List                  getPrices()        { return thePrices; }
		public SpotPrice extractItemAt(long uIndex) {
			return thePrices.extractItemAt(uIndex); }
	 	
	 	/* Constructor */
		public SpotPrices(finObject.Date pDate) {
			Account            myCurr;
			AccountList        myBase;
			SpotPrice          myPrice;

			/* Create a copy of the date and initiate the list */
			theDate    = pDate;
			thePrices  = new List();
						
			/* Access the underlying data */
			myBase = getData().getAccounts();

			/* Loop through the Accounts building the spot prices */
			for (myCurr = myBase.getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				
				/* Ignore deleted accounts */
				if (myCurr.isDeleted()) continue;
				
				/* Ignore non-priced accounts */
				if (!myCurr.isPriced()) continue;
				
				/* Add a spot price to the prices */
				myPrice = new SpotPrice(thePrices, myCurr);
				myPrice.addToList();
			}
		}
		
		/** 
		 * Apply changes in a statement back into the underlying finance objects
		 */
		public void applyChanges() {
			finData.PriceList    myBase;
			
			/* Access base details */
			myBase     = getData().getPrices();
			
			/* Apply the changes from this list */
			myBase.applyChanges(thePrices);
			
			/* Analyse the data */
			try { theData.analyseData(); } catch (Exception e) {}
			
			/* Refresh windows */
			refreshWindow();
		}
		
		/* The List class */
		public class List extends finLink.itemCtl
						  implements finSwing.tableList {
			/* Linking methods */
			public SpotPrice getFirst() { return (SpotPrice)super.getFirst(); }
			public SpotPrice getLast()  { return (SpotPrice)super.getLast(); }
			public SpotPrice searchFor(long uId) {
				return (SpotPrice)super.searchFor(uId); }
			public SpotPrice extractItemAt(long uIndex) {
				return (SpotPrice)super.extractItemAt(uIndex); }
			
			/* Constructors */
			public List() { super(ListStyle.SPOT, false); }
			
			/** 
		 	 * Clone a SpotView list (never used)
		 	 * @return <code>null</code>
		 	 */
			protected List cloneIt() { return null; }
			
			/* Is this list locked */
			public boolean isLocked() { return false; }
			
			/**
			 * Add a new item (never used)
			 */
			public finLink.itemElement addNewItem(finLink.itemElement pElement) {
				return null;}
			
			/**
			 * Add a new item to the edit list
			 * 
			 * @param isCredit - ignored
			 */
			public void addNewItem(boolean        isCredit) {}
		
			/**
			 * Obtain the type of the item
			 * @return the type of the item
			 */
			public String itemType() { return "SpotView"; }
			
			/** 
			 * Validate a spot price list
			 */
			public void validate() {
				SpotPrice myCurr;
				
				/* Loop through the list */
				for (myCurr = getFirst();
				     myCurr != null;
				     myCurr = myCurr.getNext()) {
					/* Item is always valid */
					myCurr.setValidEdit();
				}
				
				/* Set the valid indication */
				setEditState(EditState.VALID);	
			}
		}
				
		public class SpotPrice 	extends finLink.itemElement
								implements finSwing.tableElement  {
			/* Properties */
			private finData.Account       	 theAccount   = null;
			
			/* Access methods */
			public finObject.Date        getDate()      { return theDate; }
			public finData.Account       getAccount()   { return theAccount; }
			public SpotValues      		 getObj()       { return (SpotValues)super.getObj(); }
			public finObject.Price       getPrice()     { return getObj().getPrice(); }
			
			/* Linking methods */
			public SpotPrice getNext() { return (SpotPrice)super.getNext(); }
			public SpotPrice getPrev() { return (SpotPrice)super.getPrev(); }
			public Price	 getBase() { return (Price)super.getBase(); }
			public List  	 getCtl()  { return (List)super.getCtl(); } 
			public finSwing.tableList getList()  { 
				return (finSwing.tableList)super.getCtl(); } 

			/* Field IDs */
			public static final int FIELD_ID       = 0;
			public static final int FIELD_ACCOUNT  = 1;
			public static final int FIELD_PRICE    = 2;
			public static final int NUMFIELDS	   = 3;
			
			/**
			 * Obtain the type of the item
			 * @return the type of the item
			 */
			public String itemType() { return "SpotView"; }
			
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
					case FIELD_ID: 	  	return "ID";
					case FIELD_ACCOUNT: return "Account";
					case FIELD_PRICE: 	return "Price";
					default:		  	return super.fieldName(iField);
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
				SpotValues 	myObj 	 = (SpotValues)pObj;
				switch (iField) {
					case FIELD_ID: 		
						myString += getId(); 
						break;
					case FIELD_ACCOUNT:	
						myString += theAccount.getName(); 
						break;
					case FIELD_PRICE: 	
						myString += finObject.formatPrice(myObj.getPrice());	
						break;
				}
				return myString + "</td></tr>";
			}
						
			/* Standard constructor for a new SpotPrice */
			public SpotPrice(List pList, finData.Account pAccount) {
				super(pList, 0);
		
				/* Variables */
				SpotValues myObj = new SpotValues();
				Price myCurr;
				int   iDiff;
				
				/* Store base values */
				setObj(myObj);
				theAccount = pAccount;
				
				/* Loop through the prices looking for this price */
				for (myCurr = theData.getPrices().getFirst();
					 myCurr != null;
					 myCurr  = myCurr.getNext()) {
					/* Ignore if not for this account */
					if (theAccount.compareTo(myCurr.getAccount()) != 0) continue; 
					
					/* Test the date */
					iDiff = theDate.compareTo(myCurr.getDate()); 
					
					/* Break loop if matched */
					if (iDiff == 0) break;
				
					/* Break loop if past date */
					if (iDiff < 0) { myCurr = null; break; } 
				}
				
				/* If we found an existing price for this date */
				if (myCurr != null) {
					/* Set the price if it is not deleted */
					if (!myCurr.isDeleted()) setPrice(myCurr.getPrice());
					
					/* Link to base */
					setBase(myCurr);
				}
				
				/* Set the state */
				setState(State.CLEAN);
				
				/* Note if the account is closed */
				if (theAccount.isClosed()) setInVisible();
			}
	
						
			/**
			 * Validate the line
			 */
			public void validate() { }

			/**
			 * Compare the line
			 */
			public boolean equals(finLink.itemElement that) { return false; }
			
			/**
			 * Override for standard method
			 */
			protected int compareTo(SpotPrice that) {
				if (this == that) return 0;
				if (that == null) return -1;
				if (this.getAccount() == that.getAccount()) return 0;
				if (this.getAccount() == null) return 1;
				if (that.getAccount() == null) return -1;
				return getAccount().compareTo(that.getAccount());
			}

			public int linkCompareTo(linkObject that) {
				SpotPrice myItem = (SpotPrice)that;
				return this.compareTo(myItem);
			}
			
			/* History functions */
			public void    pushHistory()       { super.pushHistory(); }
			public void    popHistory()        { super.popHistory(); }
			public boolean checkForHistory()   { return super.checkForHistory(); }
			public boolean hasHistory()        { return super.hasHistory(); }
			public void    clearHistory()      { super.clearHistory(); }
			public void    peekFurther()       { super.peekFurther(); }
			public void    peekPrevious()      { super.peekPrevious(); }
			public boolean fieldChanged(int iField) { return super.fieldChanged(iField); }
			
			/**
			 * Set a new price 
			 * 
			 * @param pPrice the new price 
			 */
			protected void setPrice(finObject.Price pPrice) {
				getObj().setPrice(pPrice);
			}			
		}
	}
	
	/**
	 * The properties view class
	 */
	public class Properties {
		/**
		 * Underlying properties
		 */
		private finProperties	theProperties	= null;

		/**
		 * Do we have changes
		 */
		private boolean			hasChanges		= false;

		/**
		 * Database driver string
		 */
		private String			theDBDriver		= null;

		/**
		 * Database connection string
		 */
		private String	theDBConnection			= null;
		
		/**
		 * Source old format spreadsheet
		 */
		private String  theBaseSpreadSheet		= null;
				
		/**
		 * Backup directory name
		 */
		private String  theBackupDir			= null;
				
		/**
		 * Backup file name
		 */
		private String  theBackupFile			= null;
				
		/**
		 * Backups done using encryption?
		 */
		private boolean doEncryptBackups		= false;
				
		/**
		 * Show debug window?
		 */
		private boolean doShowDebug				= false;
				
		/**
		 * BirthDate for tax purposes
		 */
		private finObject.Date 	theBirthDate	= null;
				
		/**
		 * Determine the DB Driver string
		 * @return the DB driver string
		 */
		public String getDBDriver() 			{ return theDBDriver; }
		
		/**
		 * Determine the DB Driver string
		 * @return the DB driver string
		 */
		public String getDBConnection()			{ return theDBConnection; }

		/**
		 * Determine the old spreadsheet name
		 * @return the old spreadsheet name
		 */
		public String getBaseSpreadSheet() 		{ return theBaseSpreadSheet; }

		/**
		 * Determine the backup directory name
		 * @return the backup directory name
		 */
		public String getBackupDir() 			{ return theBackupDir; }

		/**
		 * Determine the backup file name
		 * @return the backup file name
		 */
		public String getBackupFile() 			{ return theBackupFile; }

		/**
		 * Do we encrypt backups?
		 * @return whether backups are encrypted or not
		 */
		public boolean doEncryptBackups() 		{ return doEncryptBackups; }

		/**
		 * Do we show the Debug window?
		 * @return whether we show the debug window
		 */
		public boolean doShowDebug() 			{ return doShowDebug; }

		/**
		 * Determine birth date for tax calculations
		 * @return the birthday
		 */
		public finObject.Date getBirthDate() 	{ return theBirthDate; }

		/**
		 * Determine whether we have changes
		 * @return <code>true/false</code>
		 */
		public boolean hasChanges() 			{ return hasChanges; }

		/**
		 * Constructor
		 */
		public Properties() {
			/* Access the properties */
			theProperties = theWindow.getProperties();
			
			/* Initialise the various values */
			resetData();
		}
		
		/**
		 * Reset Data from properties
		 */
		public void resetData() {
			/* Initialise the various values */
			theDBDriver      	= theProperties.getDBDriver();
			theDBConnection  	= theProperties.getDBConnection();
			theBaseSpreadSheet	= theProperties.getBaseSpreadSheet();
			theBackupDir     	= theProperties.getBackupDir();
			theBackupFile    	= theProperties.getBackupFile();
			doEncryptBackups 	= theProperties.doEncryptBackups();
			doShowDebug      	= theProperties.doShowDebug();
			theBirthDate		= theProperties.getBirthDate();
			
			/* Note that we have no changes */
			hasChanges = false;
		}
		/**
		 * Set the DB Driver name 
		 * @param pValue the new value
		 */
		public void setDBDriver(String pValue) {
			theDBDriver = new String(pValue);
		}

		/**
		 * Set the DB connection name 
		 * @param pValue the new value
		 */
		public void setDBConnection(String pValue) {
			theDBConnection = new String(pValue);
		}

		/**
		 * Set the Base Spreadsheet name 
		 * @param pValue the new value
		 */
		public void setBaseSpreadSheet(String pValue) {
			theBaseSpreadSheet = new String(pValue);
		}

		/**
		 * Set the Backup Directory name 
		 * @param pValue the new value
		 */
		public void setBackupDir(String pValue) {
			theBackupDir = new String(pValue);
		}

		/**
		 * Set the Backup File name 
		 * @param pValue the new value
		 */
		public void setBackupFile(String pValue) {
			theBackupFile = new String(pValue);
		}

		/**
		 * Set the Encrypt Backup flag 
		 * @param bValue the new value
		 */
		public void setDoEncryptBackups(boolean bValue) {
			doEncryptBackups = bValue;
		}

		/**
		 * Set the Show Debug flag 
		 * @param bValue the new value
		 */
		public void setDoShowDebug(boolean bValue) {
			doShowDebug = bValue;
		}
		
		/**
		 * Set the BirthDate 
		 * @param pDate the new value
		 */
		public void setBirthDate(finObject.Date pValue) {
			theBirthDate = pValue;
		}		

		/**
		 * ApplyChanges to properties
		 */
		public void applyChanges() throws finObject.Exception {
			/* Update the DBDriver if required */
			if (finObject.differs(getDBDriver(), theProperties.getDBDriver()))  
				theProperties.setDBDriver(getDBDriver());
				
			/* Update the DBConnection if required */
			if (finObject.differs(getDBConnection(), theProperties.getDBConnection()))  
				theProperties.setDBConnection(getDBConnection());
				
			/* Update the BaseSpreadSheet if required */
			if (finObject.differs(getBaseSpreadSheet(), theProperties.getBaseSpreadSheet()))  
				theProperties.setBaseSpreadSheet(getBaseSpreadSheet());
				
			/* Update the BackupDirectory if required */
			if (finObject.differs(getBackupDir(), theProperties.getBackupDir()))  
				theProperties.setBackupDir(getBackupDir());
				
			/* Update the BackupFile if required */
			if (finObject.differs(getBackupFile(), theProperties.getBackupFile()))  
				theProperties.setBackupFile(getBackupFile());
				
			/* Update the BirthDate if required */
			if (finObject.differs(getBirthDate(), theProperties.getBirthDate()))  
				theProperties.setBirthDate(getBirthDate());
				
			/* Update the doShowDebug flag if required */
			if (doShowDebug != theProperties.doShowDebug())  
				theProperties.setDoShowDebug(doShowDebug);
				
			/* Update the doEncryptBackups flag if required */
			if (doEncryptBackups != theProperties.doEncryptBackups())  
				theProperties.setDoEncryptBackups(doEncryptBackups);
			/* Flush changes and clear the changes flag */
			theProperties.flushChanges();
			hasChanges   = false;
		}
		
		/**
		 * Check changes
		 */
		public void checkChanges() {
			/* Note if any field has changed */
			hasChanges = ((finObject.differs(getDBDriver(), theProperties.getDBDriver()))               ||  
						  (finObject.differs(getDBConnection(), theProperties.getDBConnection()))       ||  
						  (finObject.differs(getBaseSpreadSheet(), theProperties.getBaseSpreadSheet())) ||  
						  (finObject.differs(getBackupDir(), theProperties.getBackupDir())) 			||  
						  (finObject.differs(getBackupFile(), theProperties.getBackupFile()))           ||  
						  (finObject.differs(getBirthDate(), theProperties.getBirthDate()))				||  
						  (doShowDebug != theProperties.doShowDebug())									||  
						  (doEncryptBackups != theProperties.doEncryptBackups()));  
		}		
	}
}
