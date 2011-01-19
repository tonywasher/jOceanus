package finance;

import java.util.Calendar;

import finance.finBuilder.IdManager;
import finance.finLink.histObject;
import finance.finLink.linkObject;
import finance.finObject.ExceptionClass;
import finance.finObject.LoadState;
import finance.finObject.ObjectClass;
import finance.finObject.State;
import finance.finStatic.FreqClass;
import finance.finLink.validationCtl.errorElement;
import finance.finLink.itemCtl.ListStyle;

public class finData implements finObject.htmlDumpable {
	private finData                 theData       = this;
	private finStatic               theStatic     = null;
	private finStatic.ActTypeList   theActTypes   = null;
	private finStatic.TransTypeList theTransTypes = null;
	private finStatic.TaxTypeList   theTaxTypes   = null;
	private finStatic.TaxRegimeList theTaxRegimes = null;
    private finStatic.FreqList      theFrequencys = null;
    private TaxParmList             theTaxYears   = null;
    private AccountList             theAccounts   = null;
    private RateList               	theRates      = null;
    private PatternList            	thePatterns   = null; 
    private PriceList              	thePrices     = null;
	private EventList               theEvents     = null;
    private IdManager               theIdManager  = null;
    private finObject.Range         theDateRange  = null;
    private finAnalysis				theAnalysis	  = null;
    private finAnalysis.List		theTotals	  = null;
    private finObject.LoadState		theLoadState  = LoadState.INITIAL;
    private long					theDataVers	  = finProperties.CURRENTVERSION;
    
	/* Access methods */
	public EventList               getEvents()        { return theEvents; }
	public finStatic               getStatic()        { return theStatic; }
	public AccountList             getAccounts()      { return theAccounts; }
	public finStatic.ActTypeList   getActTypes()      { return theActTypes; }
	public finStatic.TaxTypeList   getTaxTypes()      { return theTaxTypes; }
	public finStatic.TaxRegimeList getTaxRegimes()    { return theTaxRegimes; }
	public finStatic.TransTypeList getTransTypes()    { return theTransTypes; }
	public RateList                getRates()      	  { return theRates; }
	public PriceList               getPrices()        { return thePrices; }
	public PatternList             getPatterns()      { return thePatterns; }
	public TaxParmList             getTaxYears()      { return theTaxYears; }
	public finStatic.FreqList      getFrequencys()    { return theFrequencys; }
	public IdManager               getIdManager()     { return theIdManager; }
	public finObject.Range         getDateRange()     { return theDateRange; }
	public finAnalysis.List        getTotals()    	  { return theTotals; }
	public finObject.LoadState     getLoadState()  	  { return theLoadState; }
	public long				       getDataVersion()   { return theDataVers; }

	/* Set functions */
	public void setDataVersion(long pVersion) 		{ theDataVers = pVersion; }
	
	/* Standard constructor */
	public finData() {
		/* Create the empty lists */
		theIdManager  = new finBuilder.IdManager();
		buildEmptyLists();
	}
	public finData(IdManager pIdManager) {
		/* Create the empty lists */
		theIdManager  = pIdManager;
		buildEmptyLists();
	}
	
	/**
	 * Build empty lists
	 */
	private void buildEmptyLists() {
		/* Create the empty lists */
		theStatic     = new finStatic(this);
		theActTypes   = theStatic.new ActTypeList();
		theTransTypes = theStatic.new TransTypeList();
		theTaxTypes   = theStatic.new TaxTypeList();
		theTaxRegimes = theStatic.new TaxRegimeList();
		theFrequencys = theStatic.new FreqList();
		theEvents     = new EventList();
		theAccounts   = new AccountList();
		theRates      = new RateList();
		thePrices     = new PriceList();
		thePatterns   = new PatternList();
		theTaxYears   = new TaxParmList();
		theAnalysis	  = new finAnalysis(this);
	}
	
	/**
	 * Construct a difference extract between two account type lists.
	 * The difference extract will only contain items that differ between the two lists.
	 * Items that are in the new list, but not in the old list will be viewed as inserted.
	 * Items that are in the old list but not in the new list will be viewed as deleted.
	 * Items that are in both list but differ will be viewed as changed 
	 * 
	 * @param pNew The new list to extract from 
	 * @param pOld The old list to extract from 
	 */
	protected finData(finData pNew, finData pOld) {
		/* Create a new id manager */
		theIdManager = new finBuilder.IdManager();
		theStatic    = new finStatic(this);
		
		/* Build the static differences */
		theActTypes   = theStatic.new ActTypeList(pNew.getActTypes(), pOld.getActTypes());
		theTransTypes = theStatic.new TransTypeList(pNew.getTransTypes(), pOld.getTransTypes());
		theTaxTypes   = theStatic.new TaxTypeList(pNew.getTaxTypes(), pOld.getTaxTypes());
		theTaxRegimes = theStatic.new TaxRegimeList(pNew.getTaxRegimes(), pOld.getTaxRegimes());
		theFrequencys = theStatic.new FreqList(pNew.getFrequencys(), pOld.getFrequencys());
		
		/* Build the data differences */
		theTaxYears   = new TaxParmList(pNew.getTaxYears(), pOld.getTaxYears());
		theAccounts   = new AccountList(pNew.getAccounts(), pOld.getAccounts());
		theRates      = new RateList(pNew.getRates(), pOld.getRates());
		thePrices     = new PriceList(pNew.getPrices(), pOld.getPrices());
		thePatterns   = new PatternList(pNew.getPatterns(), pOld.getPatterns());
		theEvents     = new EventList(pNew.getEvents(), pOld.getEvents());
	}
	
	/**
	 * ReBase this data set against an earlier version.
	 * 
	 * @param pOld The old data to reBase against 
	 */
	protected void reBase(finData pOld) {
		/* ReBase the static items */
		theActTypes.reBase(pOld.getActTypes());
		theTransTypes.reBase(pOld.getTransTypes());
		theTaxTypes.reBase(pOld.getTaxTypes());
		theTaxRegimes.reBase(pOld.getTaxRegimes());
		theFrequencys.reBase(pOld.getFrequencys());
		
		/* ReBase the data items */
		theTaxYears.reBase(pOld.getTaxYears());
		theAccounts.reBase(pOld.getAccounts());
		theRates.reBase(pOld.getRates());
		thePrices.reBase(pOld.getPrices());
		thePatterns.reBase(pOld.getPatterns());
		theEvents.reBase(pOld.getEvents());
	}
	
	/**
	 * Compare this data-set to another to establish equality.
	 * 
	 * @param that The Data-set to compare to
	 * @return <code>true</code> if the data-sets are identical, 
	 * <code>false</code> otherwise
	 */
	protected boolean equals(finData that) {
		/* Compare static data */
		if (!theActTypes.equals(that.getActTypes())) return false;
		if (!theTransTypes.equals(that.getTransTypes())) return false;
		if (!theTaxTypes.equals(that.getTaxTypes())) return false;
		if (!theTaxRegimes.equals(that.getTaxRegimes())) return false;
		if (!theFrequencys.equals(that.getFrequencys())) return false;
		
		/* Compare dynamic data */
		if (!theTaxYears.equals(that.getTaxYears())) return false;
		if (!theAccounts.equals(that.getAccounts())) return false;
		if (!theRates.equals(that.getRates())) return false;
		if (!thePrices.equals(that.getPrices())) return false;
		if (!thePatterns.equals(that.getPatterns())) return false;
		if (!theEvents.equals(that.getEvents())) return false;
		
		/* We are identical */
		return true;
	}

	/**
	 * Provide a string representation of this object
	 * @return formatted string
	 */
	public StringBuilder toHTMLString() {
		/* Local variables */
		StringBuilder myString = new StringBuilder(10000);
		
		/* Format the individual parts */
		myString.append(theActTypes.toHTMLString());
		myString.append(theTransTypes.toHTMLString());
		myString.append(theTaxTypes.toHTMLString());
		myString.append(theTaxRegimes.toHTMLString());
		myString.append(theFrequencys.toHTMLString());
		myString.append(theTaxYears.toHTMLString());
		myString.append(theAccounts.toHTMLString());
		myString.append(theRates.toHTMLString());
		myString.append(thePrices.toHTMLString());
		myString.append(thePatterns.toHTMLString());
		myString.append(theEvents.toHTMLString());
		
		/* Return the string */
		return myString;
	}
	
	/**
	 * Determine whether a DataSet has entries
	 * @return <code>true</code> if the DataSet has entries
	 */
	public boolean hasMembers() {
		/* Local variables */
		boolean hasMembers;
		
		/* Determine whether the data is empty */
		hasMembers  = 	theActTypes.hasMembers()   ||
						theTransTypes.hasMembers() ||
						theTaxTypes.hasMembers()   ||
						theTaxRegimes.hasMembers() ||
						theFrequencys.hasMembers() ||
						theTaxYears.hasMembers()   ||
						theAccounts.hasMembers()   ||
						theRates.hasMembers()      ||
						thePrices.hasMembers()     ||
						thePatterns.hasMembers()   ||
						theEvents.hasMembers();
		
		/* Return the indication */
		return hasMembers;
	}
	
	/**
	 * Determine whether the Data-set has updates
	 * @return <code>true</code> if the Data-set has updates, <code>false</code> if not
	 */
	protected boolean hasUpdates() {
			
		/* Determine whether we have updates */
		if (theActTypes.hasUpdates()) return true;
		if (theTransTypes.hasUpdates()) return true;
		if (theTaxTypes.hasUpdates()) return true;
		if (theTaxRegimes.hasUpdates()) return true;
		if (theFrequencys.hasUpdates()) return true;
		if (theTaxYears.hasUpdates()) return true;
		if (theAccounts.hasUpdates()) return true;
		if (theRates.hasUpdates()) return true;
		if (thePrices.hasUpdates()) return true;
		if (thePatterns.hasUpdates()) return true;
		if (theEvents.hasUpdates()) return true;
			
		/* We have no updates */
		return false;
	}
	
	/**
	 * Calculate the allowed Date Range
	 */
	protected void calculateDateRange() {
		theDateRange = theTaxYears.getRange();
	}
		
	/**
	 * Analyse the data
	 */
	public void analyseData() throws finObject.Exception {
		finAnalysis.Asset myAssets = null;
						
		/* Update INITIAL Load status */
		if (theLoadState == LoadState.INITIAL)
			theLoadState = LoadState.FINAL;
		
		/* Reset the flags on the accounts and tax years*/
		theAccounts.reset();
		theTaxYears.reset();
		
		/* Create the analysis List */
		theTotals = theAnalysis.new List();

		/* Note active rates */
		theRates.markActiveRates();
		
		/* Note active prices */
		thePrices.markActivePrices();
		
		/* Note active patterns */
		thePatterns.markActivePatterns();
		
		/* Access the most recent asset report */
		if (theTotals.getLast() != null)
			myAssets = theTotals.getLast().getAssetReport();
		
		/* Note active accounts by asset */
		if (myAssets != null)
			myAssets.getBuckets().markActiveAccounts();
		
		/* Note active accounts */
		theAccounts.markActiveAccounts();
		
		/* Note that we are now fully loaded */
		theLoadState = LoadState.LOADED;
	}

	/**
	 * Determines whether an event can be valid
	 * 
	 * @param pTrans The transaction type of the event
	 * @param pType The account type of the event
	 * @param isCredit is the account a credit or a debit
	 * @return valid true/false 
	 */
	protected static boolean isValidEvent(finStatic.TransType   pTrans,
			                              finStatic.AccountType pType,
			                              boolean               isCredit) {
		boolean myResult = false;

		/* Market is always false */
		if (pType.isMarket())
			return false;
		
		/* Switch on the TransType */
		switch (pTrans.getTranClass()) {
			case TAXEDINCOME:
			case TAXFREEINCOME:
			case INTEREST:
			case DIVIDEND:
			case UNITTRUSTDIV:
			case TAXABLEGAIN:
			case DIRLOAN:
				if (!isCredit) myResult = (pType.isExternal() && !pType.isCash());
				else           myResult = !pType.isExternal();
				break;
			case NATINSURANCE:
				if (!isCredit) myResult = (pType.isExternal() && !pType.isCash());
				else           myResult = pType.isTaxMan();
				break;
			case TRANSFER:
				myResult = !pType.isExternal();
				break;
			case CSHPAY:
				isCredit = !isCredit;
			case CSHRECOVER:
				if (!isCredit) myResult = ((pType.isExternal()) && (!pType.isCash()));
				else           myResult = pType.isCash();
				break;
			case INHERITED:
				if (!isCredit) myResult = pType.isInheritance();
				else           myResult = !pType.isExternal();
				break;
			case BENEFIT:
				if (!isCredit) myResult = (pType.isExternal() && !pType.isCash());
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
			case ENDOWMENT:
				if (!isCredit) myResult = !pType.isExternal();
				else           myResult = (pType.isExternal() && !pType.isCash());
				break;
			case MORTGAGE:
				if (!isCredit) myResult = pType.isDebt();
				else           myResult = (pType.isExternal() && !pType.isCash());
				break;
			case TAXREFUND:
				isCredit = !isCredit;
			case TAXOWED:
				if (!isCredit) myResult = !pType.isExternal();
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
			case MKTINCOME:
				if (!isCredit) myResult = (pType.isExternal() && !pType.isCash());
				else           myResult = pType.isEndowment();
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
	
	/* EventList class */
	public class EventList extends finLink.itemCtl
						   implements finSwing.tableList{
		/* Linking methods */
		public Event getFirst() { return (Event)super.getFirst(); }
		public Event getLast()  { return (Event)super.getLast(); }
		public Event searchFor(long uId) {
			return (Event)super.searchFor(uId); }
		public Event extractItemAt(long uIndex) {
			return (Event)super.extractItemAt(uIndex); }
		
	 	/** 
	 	 * Construct an empty CORE event list
	 	 */
		protected EventList() { super(ListStyle.CORE, false); }

		/** 
	 	 * Construct an empty generic event list
	 	 */
		protected EventList(ListStyle pStyle) { super(pStyle, false); }

		/** 
	 	 * Construct a generic event list
	 	 * @param pList the source event list 
	 	 * @param pStyle the style of the list 
	 	 */
		protected EventList(EventList pList, ListStyle pStyle) { super(pList, pStyle); }

		/** 
	 	 * Construct a difference event list
	 	 * @param pNew the new Event list 
	 	 * @param pOld the old Event list 
	 	 */
		protected EventList(EventList pNew, EventList pOld) { super(pNew, pOld); }
	
		/** 
	 	 * Clone an Event list
	 	 * @return the cloned list
	 	 */
		protected EventList cloneIt() { return new EventList(this, ListStyle.CORE); }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pItem the item to add
		 * @return the newly added item
		 */
		public finLink.itemElement addNewItem(finLink.itemElement pItem) {
			if (pItem instanceof Event) {
				Event myEvent = new Event(this, (Event)pItem);
				myEvent.addToList();
				return myEvent;
			}
			else if (pItem instanceof finView.Statement.Line) {
				Event myEvent = new Event(this, (finView.Statement.Line)pItem);
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
		public String itemType() { return "Event"; }				
	}
		
	/* EventValues */
	public static class EventValues implements finLink.histObject {
		private finObject.Date       theDate      = null;
		private String               theDesc      = null;
		private finObject.Money      theAmount    = null;
		private Account              theDebit     = null;
		private Account              theCredit    = null;
		private finObject.Units      theUnits     = null;
		private finStatic.TransType  theTransType = null;
		private finObject.Money      theTaxCredit = null;
		private Integer            	 theYears     = null;
		
		/* Access methods */
		public finObject.Date       getDate()      { return theDate; }
		public String               getDesc()      { return theDesc; }
		public finObject.Money      getAmount()    { return theAmount; }
		public Account              getDebit()     { return theDebit; }
		public Account              getCredit()    { return theCredit; }
		public finObject.Units      getUnits()     { return theUnits; }
		public finStatic.TransType  getTransType() { return theTransType; }
		public finObject.Money      getTaxCredit() { return theTaxCredit; }
		public Integer             	getYears()     { return theYears; }
		
		public void setDate(finObject.Date pDate) {
			theDate      = pDate; }
		public void setDesc(String pDesc) {
			theDesc      = pDesc; }
		public void setAmount(finObject.Money pAmount) {
			theAmount    = pAmount; }
		public void setDebit(finData.Account pDebit) {
			theDebit     = pDebit; }
		public void setCredit(finData.Account pCredit) {
			theCredit    = pCredit; }
		public void setUnits(finObject.Units pUnits) {
			theUnits     = pUnits; }
		public void setTransType(finStatic.TransType pTransType) {
			theTransType = pTransType; }
		public void setTaxCredit(finObject.Money pTaxCredit) {
			theTaxCredit = pTaxCredit; }
		public void setYears(Integer iYears) {
			theYears     = iYears; }

		/* Constructor */
		public EventValues() {}
		public EventValues(EventValues pValues) {
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
			EventValues myValues = (EventValues)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(EventValues pValues) {
			if (finObject.differs(theDate,      pValues.theDate))      return false;
			if (finObject.differs(theDesc,      pValues.theDesc))      return false;
			if (finObject.differs(theAmount,    pValues.theAmount))    return false;
			if (finObject.differs(theUnits,     pValues.theUnits))     return false;
			if (finObject.differs(theDebit,     pValues.theDebit))     return false;
			if (finObject.differs(theCredit,    pValues.theCredit))    return false;
			if (finObject.differs(theTransType, pValues.theTransType)) return false;
			if (finObject.differs(theTaxCredit, pValues.theTaxCredit)) return false;
			if (finObject.differs(theYears,     pValues.theYears))	   return false;
			return true;
		}
		
		/* Copy values */
		public void    copyFrom(histObject pSource) {
			EventValues myValues = (EventValues)pSource;
			copyFrom(myValues);
		}
		public histObject copySelf() {
			return new EventValues(this);
		}
		public void    copyFrom(EventValues pValues) {
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
			EventValues 	pValues = (EventValues)pOriginal;
			boolean			bResult = false;
			switch (fieldNo) {
				case Event.FIELD_DATE:
					bResult = (finObject.differs(theDate,      pValues.theDate));
					break;
				case Event.FIELD_DESC:
					bResult = (finObject.differs(theDesc,      pValues.theDesc));
					break;
				case Event.FIELD_TRNTYP:
					bResult = (finObject.differs(theTransType, pValues.theTransType));
					break;
				case Event.FIELD_AMOUNT:
					bResult = (finObject.differs(theAmount,    pValues.theAmount));
					break;
				case Event.FIELD_DEBIT:
					bResult = (finObject.differs(theDebit,     pValues.theDebit));
					break;
				case Event.FIELD_CREDIT:
					bResult = (finObject.differs(theCredit,    pValues.theCredit));
					break;
				case Event.FIELD_UNITS:
					bResult = (finObject.differs(theUnits,     pValues.theUnits));
					break;
				case Event.FIELD_TAXCREDIT:
					bResult = (finObject.differs(theTaxCredit, pValues.theTaxCredit));
					break;
				case Event.FIELD_YEARS:
					bResult = (finObject.differs(theYears,     pValues.theYears));
					break;
			}
			return bResult;
		}
	}
	
	/* Event class */
	public class Event extends finLink.itemElement 
					   implements finSwing.tableElement {
		/* Local IDs for use in loading */
		private long				theDebitId	= -1;
		private long				theCreditId	= -1;
		private long				theTransId	= -1;
		
		/* Access methods */
		public  EventValues         getObj()       { return (EventValues)super.getObj(); }	
		public  finObject.Date      getDate()      { return getObj().getDate(); }
		public  String              getDesc()      { return getObj().getDesc(); }
		public  finObject.Money     getAmount()    { return getObj().getAmount(); }
		public  Account             getDebit()     { return getObj().getDebit(); }
		public  Account             getCredit()    { return getObj().getCredit(); }
		public  finObject.Units     getUnits()     { return getObj().getUnits(); }
		public  finStatic.TransType getTransType() { return getObj().getTransType(); }
		public  finObject.Money     getTaxCredit() { return getObj().getTaxCredit(); }
		public  Integer	            getYears()     { return getObj().getYears(); }

		/* Linking methods */
		public Event     getNext() { return (Event)super.getNext(); }
		public Event     getPrev() { return (Event)super.getPrev(); }
		public Event     getBase() { return (Event)super.getBase(); }
		public EventList getCtl()  { return (EventList)super.getCtl(); } 
		public finSwing.tableList getList()  { 
			return (finSwing.tableList)super.getCtl(); } 
		
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
		public String itemType() { return "Event"; }
		
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
			String 		myString = "<tr><td>" + fieldName(iField) + "</td><td>";
			EventValues myObj 	 = (EventValues)pObj;
			switch (iField) {
				case FIELD_ID: 		
					myString += getId(); 
					break;
				case FIELD_DATE:	
					myString += finObject.formatDate(myObj.getDate()); 
					break;
				case FIELD_DESC:	
					myString += myObj.getDesc(); 
					break;
				case FIELD_TRNTYP: 	
					if ((myObj.getTransType() == null) &&
						(theTransId != -1))
						myString += "Id=" + theDebitId;
					else
						myString += finObject.formatTrans(myObj.getTransType());	
					break;
				case FIELD_DEBIT:
					if ((myObj.getDebit() == null) &&
						(theDebitId != -1))
						myString += "Id=" + theDebitId;
					else
						myString += finObject.formatAccount(myObj.getDebit()); 
					break;
				case FIELD_CREDIT:	
					if ((myObj.getCredit() == null) &&
						(theCreditId != -1))
						myString += "Id=" + theCreditId;
					else
						myString += finObject.formatAccount(myObj.getCredit()); 
					break;
				case FIELD_AMOUNT: 	
					myString += finObject.formatMoney(myObj.getAmount());	
					break;
				case FIELD_UNITS: 	
					myString += finObject.formatUnits(myObj.getUnits());	
					break;
				case FIELD_TAXCREDIT:	
					myString += finObject.formatMoney(myObj.getTaxCredit()); 
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
		protected Event(EventList pList, Event pEvent) {
			/* Set standard values */
			super(pList, pEvent.getId()); 
			setObj(new EventValues(pEvent.getObj()));
		
			/* Switch on the ListStyle */
			switch (pList.getStyle()) {
				case CORE:
					theIdManager.setNewEvent(this);
					break;
				case EDIT:
					setBase(pEvent);
					setState(State.CLEAN);
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
		protected Event(EventList              pList,
			            finView.Statement.Line pLine) {
		
			/* Set standard values */
			super(pList, 0);
			EventValues myObj = new EventValues();
			setObj(myObj);
			myObj.setDate(pLine.getDate());
			myObj.setDesc(pLine.getDesc());
			myObj.setAmount(pLine.getAmount());
			myObj.setUnits(pLine.getUnits());
			myObj.setTransType(pLine.getTransType());
			
			/* If the event needs a Tax Credit */
			if (getTransType().needsTaxCredit()) {
				/* Set a new null tax credit */
				myObj.setTaxCredit(new finObject.Money(0));
				
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
				theIdManager.setNewEvent(this);
		}
		
		/**
		 * Construct a new event from an Account pattern
		 * 
		 * @param pList the list to build into
		 * @param pLine The Line to copy 
		 */
		protected Event(EventList   pList,
			            Pattern 	pLine) {
			/* Set standard values */
			super(pList, 0);
			EventValues myObj = new EventValues();
			setObj(myObj);
			myObj.setDate(pLine.getDate());
			myObj.setDesc(pLine.getDesc());
			myObj.setAmount(pLine.getAmount());
			myObj.setTransType(pLine.getTransType());
		
			/* If the event needs a Tax Credit */
			if (getTransType().needsTaxCredit()) {
				/* Set a new null tax credit */
				myObj.setTaxCredit(new finObject.Money(0));
				
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
				theIdManager.setNewEvent(this);
		}
		
		/* Standard constructor for a newly inserted event */
		public Event(EventList pList) {
			super(pList, 0);
			EventValues theObj = new EventValues();
			setObj(theObj);
			setState(State.NEW);
		}

		/* Standard constructor */
		public Event(EventList      pList,
				     long           uId, 
			         java.util.Date pDate,
			         String         sDesc,
			         long           uDebit,
			         long	        uCredit,
			         long			uTransType,
			         String     	pAmount,
			         String			pUnits,
			         String			pTaxCredit,
			         int			pYears) throws finObject.Exception {
			/* Initialise item */
			super(pList, uId);
			
			/* Local variables */
			finStatic.TransType    	myTransType;
			Account   				myAccount;
			
			/* Create a new EventValues object */
			EventValues myObj = new EventValues();
			setObj(myObj);
			myObj.setDesc(sDesc);			
			
			/* Store the IDs that we will look up */
			theDebitId  = uDebit;
			theCreditId = uCredit;
			theTransId	= uTransType;
			
			/* Create the date */
			myObj.setDate(new finObject.Date(pDate));
			
			/* Look up the Debit Account */
			myAccount = theAccounts.searchFor(uDebit);
			if (myAccount == null)
				throw new finObject.Exception(ExceptionClass.DATA,
	   					  					  ObjectClass.EVENT,
	   					  					  this, 
	   					  					  "Invalid Debit Account Id");
			myObj.setDebit(myAccount);
			
			/* Look up the Debit Account */
			myAccount = theAccounts.searchFor(uCredit);
			if (myAccount == null)
				throw new finObject.Exception(ExceptionClass.DATA,
	   					  					  ObjectClass.EVENT,
	   					  					  this, 
	   					  					  "Invalid Credit Account Id");
			myObj.setCredit(myAccount);
			
			/* Look up the Transaction Type */
			myTransType = theTransTypes.searchFor(uTransType);
			if (myTransType == null)
				throw new finObject.Exception(ExceptionClass.DATA,
	   					  					  ObjectClass.EVENT,
	   					  					  this, 
	   					  					  "Invalid Transaction Type Id");
			myObj.setTransType(myTransType);
			
			/* Record the amount */
			finObject.Money myAmount = finObject.Money.Parse(pAmount);
			if (myAmount == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.EVENT,
											  this,
											  "Invalid Amount: " + pAmount);
			myObj.setAmount(myAmount);
			
			/* If there is tax credit */
			if (pTaxCredit != null) {
				/* Record the relief */
				myAmount = finObject.Money.Parse(pTaxCredit);
				if (myAmount == null) 
					throw new finObject.Exception(ExceptionClass.DATA,
											  	  ObjectClass.EVENT,
											  	  this,
											  	  "Invalid TaxCredit: " + pTaxCredit);
				myObj.setTaxCredit(myAmount);
			}

			/* If there is years */
			if (pYears != 0) {
				/* Record the years */
				myObj.setYears(new Integer(pYears));
			}

			/* If there are units */
			if (pUnits != null) {
				/* Record the units */
				finObject.Units myUnits = finObject.Units.Parse(pUnits);
				if (myUnits == null) 
					throw new finObject.Exception(ExceptionClass.DATA,
											  	  ObjectClass.EVENT,
											  	  this,
											  	  "Invalid Units: " + pUnits);
				myObj.setUnits(myUnits);
			}
			
			/* Allocate the id */
			theIdManager.setNewEvent(this);
		}
		
		/**
		 * Compare this event to another to establish equality.
		 * 
		 * @param that The Event to compare to
		 * @return <code>true</code> if the event is identical, <code>false</code> otherwise
		 */
		public boolean equals(finLink.itemElement that) {
			Event myEvent = (Event)that;
			if (this == that) return true;
			if (getId() != myEvent.getId()) return false;
			if (finObject.differs(getDate(),       myEvent.getDate())) 		return false;
			if (finObject.differs(getDesc(),       myEvent.getDesc())) 		return false;
			if (finObject.differs(getTransType(),  myEvent.getTransType())) return false;
			if (finObject.differs(getAmount(),     myEvent.getAmount())) 	return false;
			if (finObject.differs(getCredit(), 	   myEvent.getCredit())) 	return false;
			if (finObject.differs(getDebit(),      myEvent.getDebit())) 	return false;
			if (finObject.differs(getUnits(),      myEvent.getUnits())) 	return false;
			if (finObject.differs(getTaxCredit(),  myEvent.getTaxCredit()))	return false;
			if (finObject.differs(getYears(),      myEvent.getYears()))		return false;
			return true;
		}

		/**
		 * Override for standard method
		 */
		protected int compareTo(Event that) {
			int iDiff;
			if (this == that) return 0;
			if (that == null) return -1;
			if (this.getDate() != that.getDate()) {
				if (this.getDate() == null) return 1;
				if (that.getDate() == null) return -1;
				iDiff = getDate().compareTo(that.getDate());
				if (iDiff != 0) return iDiff;
			}
			if (this.getDesc() != that.getDesc()) {
				if (this.getDesc() == null) return 1;
				if (that.getDesc() == null) return -1;
				iDiff = getDesc().compareTo(that.getDesc());
				if (iDiff < 0) return -1;
				if (iDiff > 0) return 1;
			}
			if (this.getTransType() != that.getTransType()) {
				if (this.getTransType() == null) return 1;
				if (that.getTransType() == null) return -1;
				iDiff = getTransType().compareTo(that.getTransType());
				if (iDiff != 0) return iDiff;
			}
			iDiff = (int)(getId() - that.getId());
			if (iDiff < 0) return -1;
			if (iDiff > 0) return 1;
			return 0;
		}

		public int linkCompareTo(linkObject that) {
			Event myItem = (Event)that;
			return this.compareTo(myItem);
		}
		
		/**
		 * Validate the event
		 */
		public void validate() {
			finObject.Date myDate = getDate();
					
			/* The date must be non-null */
			if ((myDate == null) || (myDate.isNull())) {
				addError("Null date is not allowed", FIELD_DATE);
			}
				
			/* The date must be in-range */
			else if (getDateRange().compareTo(myDate) != 0) {
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
				
			/* Credit/Debit cannot be the same */
			if (!finObject.differs(getCredit(), getDebit())) {
				addError("Credit and debit accounts are identical", FIELD_DEBIT);
				addError("Credit and debit accounts are identical", FIELD_CREDIT);
			}
			
			/* Market Adjustment is no longer allowed */
			if ((getTransType() != null) &&	(getTransType().isMarketAdjust())) {
				addError("Explicit market adjustment disallowed", FIELD_TRNTYP);
			}
			
			/* Check credit account */
			if ((getTransType() != null) &&	(getCredit() != null) &&
				(!finData.isValidEvent(getTransType(), getCredit().getActType(), true)))
					addError("Invalid credit account for transaction", FIELD_CREDIT);
			
			/* Check debit account */
			if ((getTransType() != null) &&	(getDebit() != null) &&
				(!finData.isValidEvent(getTransType(), getDebit().getActType(), false)))
					addError("Invalid debit account for transaction", FIELD_CREDIT);
			
			/* If we have units */
			if (getUnits() != null) { 
				/* Units are only allowed if credit or debit is priced */
				if ((getDebit() != null) && (getCredit() != null) &&
					(getCredit().isPriced() == getDebit().isPriced())) {
					addError("Units are only allowed involving a single asset", 
							 FIELD_UNITS);
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
			else if ((getTransType() != null) && (getTransType().needsTaxCredit())) {
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
		protected boolean relatesTo(Account pAccount) {
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
		protected boolean isAssetRelated() {
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
		 * Set a new debit account 
		 * 
		 * @param pDebit the debit account 
		 */
		protected void setDebit(Account pDebit) {
			getObj().setDebit(pDebit);
		}
		
		/**
		 * Set a new credit account 
		 * 
		 * @param pCredit the credit account 
		 */
		protected void setCredit(Account pCredit) {
			getObj().setCredit(pCredit);
		}
		
		/**
		 * Set a new transtype 
		 * 
		 * @param pTransType the transtype 
		 */
		protected void setTransType(finStatic.TransType pTransType) {
			getObj().setTransType(pTransType);
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
			getObj().setAmount((pAmount == null) ? null : new finObject.Money(pAmount));
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
	
		/**
		 * Set a new tax credit amount 
		 * 
		 * @param pAmount the tax credit amount 
		 */
		protected void setTaxCredit(finObject.Money pAmount) {
			getObj().setTaxCredit(pAmount);
		}
		
		/**
		 * Set a new years value 
		 * 
		 * @param pYears the years 
		 */
		protected void setYears(Integer pYears) {
			getObj().setYears(pYears);
		}
		
		/**
		 * Update event from an element 
		 * 
		 * @param pItem the changed element 
		 */
		public void applyChanges(finLink.itemElement pItem){
			if (pItem instanceof Event) {
				Event myEvent = (Event)pItem;
				applyChanges(myEvent);
			}
			else if (pItem instanceof finView.Statement.Line) {
				finView.Statement.Line myLine = (finView.Statement.Line)pItem;
				applyChanges(myLine);
			}
		}
		
		/**
		 * Update event from a Statement Line 
		 * 
		 * @param pLine the changed line 
		 */
		private void applyChanges(finView.Statement.Line pLine) {
			/* Store the current detail into history */
			pushHistory();
			
			/* Update the date if required */
			if (finObject.differs(getDate(), pLine.getDate())) 
				setDate(pLine.getDate());
		
			/* Update the description if required */
			if (finObject.differs(getDesc(), pLine.getDesc()))
				setDescription(pLine.getDesc());
			
			/* Update the amount if required */
			if (finObject.differs(getAmount(), pLine.getAmount())) 
				setAmount(pLine.getAmount());
			
			/* Update the units if required */
			if (finObject.differs(getUnits(), pLine.getUnits())) 
				setUnits(pLine.getUnits());
					
			/* If the transType has changed */
			if (finObject.differs(getTransType(), pLine.getTransType())) {
				/* Set the new transtype */
				setTransType(pLine.getTransType());
				
				/* Sort out new or deleted Tax Credit */
				if (getTransType().needsTaxCredit()) {
					if (getTaxCredit() == null)	setTaxCredit(new finObject.Money(0)); 
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
				if (finObject.differs(getDebit(), pLine.getPartner())) 
					setDebit(pLine.getPartner());
			} else {
				/* Update the credit if required */
				if (finObject.differs(getCredit(), pLine.getPartner())) 
					setCredit(pLine.getPartner());
			}
			
			/* Check for changes */
			if (checkForHistory()) setState(State.CHANGED);
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
			if (finObject.differs(getDate(), pEvent.getDate())) 
				setDate(pEvent.getDate());
		
			/* Update the description if required */
			if (finObject.differs(getDesc(), pEvent.getDesc())) 
				setDescription(pEvent.getDesc());
			
			/* Update the amount if required */
			if (finObject.differs(getAmount(), pEvent.getAmount())) 
				setAmount(pEvent.getAmount());
			
			/* Update the units if required */
			if (finObject.differs(getUnits(), pEvent.getUnits())) 
				setUnits(pEvent.getUnits());
					
			/* Update the tranType if required */
			if (finObject.differs(getTransType(), pEvent.getTransType())) 
				setTransType(pEvent.getTransType());
		
			/* Update the debit if required */
			if (finObject.differs(getDebit(), pEvent.getDebit())) 
				setDebit(pEvent.getDebit());
		
			/* Update the credit if required */
			if (finObject.differs(getCredit(), pEvent.getCredit())) 
				setCredit(pEvent.getCredit());		
			
			/* Update the tax credit if required */
			if (finObject.differs(getTaxCredit(), pEvent.getTaxCredit())) 
				setTaxCredit(pEvent.getTaxCredit());
		
			/* Update the years if required */
			if (finObject.differs(getYears(), pEvent.getYears())) 
				setYears(pEvent.getYears());
			
			/* Check for changes */
			if (checkForHistory()) setState(State.CHANGED);
		}
	}
	
	/* AccountList class */
	public class AccountList  extends finLink.itemCtl {		
		/* Linking methods */
		public Account getFirst() { return (Account)super.getFirst(); }
		public Account getLast()  { return (Account)super.getLast(); }
		public Account searchFor(long uId) {
			return (Account)super.searchFor(uId); }
		public Account extractItemAt(long uIndex) {
			return (Account)super.extractItemAt(uIndex); }
			
		/** 
	 	 * Construct an empty CORE account list
	 	 */
		protected AccountList() { super(ListStyle.CORE, true); }

		/** 
	 	 * Construct a generic account list
	 	 * @param pStyle the style of the list 
	 	 */
		protected AccountList(ListStyle pStyle) { super(pStyle, true); }

		/** 
	 	 * Construct a generic account list
	 	 * @param pList the source account list 
	 	 * @param pStyle the style of the list 
	 	 */
		protected AccountList(AccountList pList, ListStyle pStyle) { super(pList, pStyle); }

		/** 
	 	 * Construct a difference account list
	 	 * @param pNew the new Account list 
	 	 * @param pOld the old Account list 
	 	 */
		protected AccountList(AccountList pNew, AccountList pOld) { super(pNew, pOld); }
		
		/** 
	 	 * Clone an Account list
	 	 * @return the cloned list
	 	 */
		protected AccountList cloneIt() { return new AccountList(this, ListStyle.CORE); }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pAccount item
		 * @return the newly added item
		 */
		public finLink.itemElement addNewItem(finLink.itemElement pAccount) {
			Account myAccount = new Account(this, (Account)pAccount);
			myAccount.addToList();
			return myAccount;
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "Account"; }
				
		/**
		 * Reset the account flags after changes to events
		 */
		public void reset() {
			Account myCurr;
			
			/* Loop through the items */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* Clear the flags */
				myCurr.reset();
			}
		}
		
		/**
		 * Update account details after data update
		 */
		public void markActiveAccounts() throws finObject.Exception {
			Account myCurr;
					
			/* Loop through the accounts marking active accounts */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				
				/* Ignore deleted accounts */
				if (myCurr.isDeleted()) continue;
				
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
				if (theLoadState == LoadState.FINAL) {
					/* Validate the account */
					myCurr.validate();
					if (myCurr.hasErrors()) 
						throw new finObject.Exception(ExceptionClass.VALIDATE,
													  ObjectClass.ACCOUNT,
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
			Account myCurr;
			int     iDiff;
			int     iCount = 0;
			
			/* Loop through the items to find the entry */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
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
		protected Account searchFor(String sName) {
			Account myCurr;
			int     iDiff;
			
			/* Loop through the items to find the entry */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
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
			Account myCurr;
			
			/* Loop through the items to find the match */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				if (myCurr.isMarket()) break;
			}
			
			/* Return */
			return myCurr;
		}		
	}
		
	/* AccountValues */
	public static class AccountValues implements finLink.histObject {
		private String               	theName     = null;
		private String               	theDesc     = null;
		private finStatic.AccountType	theType		= null;
		private finObject.Date       	theMaturity = null;
		private finObject.Date       	theClose    = null;
		private Account			     	theParent	= null;
		
		/* Access methods */
		public String               	getName()      { return theName; }
		public String               	getDesc()      { return theDesc; }
		public finStatic.AccountType	getType()      { return theType; }
		public finObject.Date       	getMaturity()  { return theMaturity; }
		public finObject.Date       	getClose()     { return theClose; }
		public Account		        	getParent()    { return theParent; }
		
		public void setName(String pName) {
			theName      = pName; }
		public void setDesc(String pDesc) {
			theDesc      = pDesc; }
		public void setType(finStatic.AccountType pType) {
			theType      = pType; }
		public void setMaturity(finObject.Date pMaturity) {
			theMaturity  = pMaturity; }
		public void setClose(finObject.Date pClose) {
			theClose     = pClose; }
		public void setParent(Account pParent) {
			theParent    = pParent; }

		/* Constructor */
		public AccountValues() {}
		public AccountValues(AccountValues pValues) {
			theName      = pValues.getName();
			theDesc      = pValues.getDesc();
			theType      = pValues.getType();
			theMaturity  = pValues.getMaturity();
			theClose     = pValues.getClose();
			theParent    = pValues.getParent();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			AccountValues myValues = (AccountValues)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(AccountValues pValues) {
			if (finObject.differs(theName,     pValues.theName))     return false;
			if (finObject.differs(theDesc,     pValues.theDesc))     return false;
			if (finObject.differs(theType,     pValues.theType))     return false;
			if (finObject.differs(theMaturity, pValues.theMaturity)) return false;
			if (finObject.differs(theClose,    pValues.theClose))    return false;
			if (finObject.differs(theParent,   pValues.theParent))   return false;
			return true;
		}
		
		/* Copy values */
		public void    copyFrom(histObject pSource) {
			AccountValues myValues = (AccountValues)pSource;
			copyFrom(myValues);
		}
		public histObject copySelf() {
			return new AccountValues(this);
		}
		public void    copyFrom(AccountValues pValues) {
			theName      = pValues.getName();
			theDesc      = pValues.getDesc();
			theType      = pValues.getType();
			theMaturity  = pValues.getMaturity();
			theClose     = pValues.getClose();
			theParent    = pValues.getParent();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			AccountValues 	pValues = (AccountValues)pOriginal;
			boolean			bResult = false;
			switch (fieldNo) {
				case Account.FIELD_NAME:
					bResult = (finObject.differs(theName,     pValues.theName));
					break;
				case Account.FIELD_DESC:
					bResult = (finObject.differs(theDesc,     pValues.theDesc));
					break;
				case Account.FIELD_TYPE:
					bResult = (finObject.differs(theType,     pValues.theType));
					break;
				case Account.FIELD_MATURITY:
					bResult = (finObject.differs(theMaturity, pValues.theMaturity));
					break;
				case Account.FIELD_CLOSE:
					bResult = (finObject.differs(theClose,    pValues.theClose));
					break;
				case Account.FIELD_PARENT:
					bResult = (finObject.differs(theParent,   pValues.theParent));
					break;
			}
			return bResult;
		}
	}
	
	/* The Account class */
	public class Account extends finLink.itemElement {
		/* Members */
		private int                   theOrder     = -1;
		private long				  theParentId  = -1;
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
		public  AccountValues         getObj()       { return (AccountValues)super.getObj(); }	
		public  String                getName()      { return getObj().getName(); }
		public  String                getDesc()      { return getObj().getDesc(); }
		public  Account               getParent()    { return getObj().getParent(); }
		public  long               	  getParentId()  { return theParentId; }
		public  Event                 getEarliest()  { return theEarliest; }
		public  Event                 getLatest()    { return theLatest; }
		public  finStatic.AccountType getActType()   { return getObj().getType(); }
		public  int                   getOrder()     { return theOrder; }
		public  finObject.Date        getMaturity()  { return getObj().getMaturity(); }
		public  finObject.Date        getClose()     { return getObj().getClose(); }
		public  boolean               isCloseable()  { return isCloseable; }
		public  boolean               isParent()  	 { return isParent; }
		public  boolean               isClosed()     { return (getClose() != null); }
		public  boolean               isDeletable()  { 
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
		public Account     getNext() { return (Account)super.getNext(); }
		public Account     getPrev() { return (Account)super.getPrev(); }
		public Account     getBase() { return (Account)super.getBase(); }
		public AccountList getCtl()  { return (AccountList)super.getCtl(); } 
		public boolean	   isLocked(){ return isClosed(); }
		
		/* Field IDs */
		public static final int FIELD_ID       = 0;
		public static final int FIELD_NAME     = 1;
		public static final int FIELD_DESC     = 2;
		public static final int FIELD_TYPE     = 3;
		public static final int FIELD_MATURITY = 4;
		public static final int FIELD_CLOSE    = 5;
		public static final int FIELD_PARENT   = 6;
		public static final int NUMFIELDS	   = 7;
		
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "Account"; }
		
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
			String 			myString = "<tr><td>" + fieldName(iField) + "</td><td>";
			AccountValues 	myObj 	 = (AccountValues)pObj;
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
						myString += finObject.formatAccountType(getActType()); 
					break;
				case FIELD_CLOSE:	
					myString += finObject.formatDate(myObj.getClose()); 
					break;
				case FIELD_MATURITY:	
					myString += finObject.formatDate(myObj.getMaturity()); 
					break;
				case FIELD_PARENT:	
					if ((myObj.getParent() == null) &&
						(theParentId != -1))
						myString += "Id=" + theParentId;
					else
						myString += finObject.formatAccount(myObj.getParent()); 
					break;
			}
			return myString + "</td></tr>";
		}
								
		/**
		 * Construct a copy of an Account
		 * 
		 * @param pAccount The Account to copy 
		 */
		protected Account(AccountList pList, Account pAccount) {
			/* Set standard values */
			super(pList, pAccount.getId());
			AccountValues myObj = new AccountValues(pAccount.getObj());
			setObj(myObj);
			theOrder     = pAccount.getOrder();
			theEarliest  = pAccount.theEarliest;
			theLatest    = pAccount.theLatest;
			isCloseable  = pAccount.isCloseable();
				
			/* Switch on the LinkStyle */
			switch (pList.getStyle()) {
				case CORE:
					theIdManager.setNewAccount(this);
					break;
				case EDIT:
					setBase(pAccount);
					setState(State.CLEAN);
					break;
				case UPDATE:
					setBase(pAccount);
					setState(pAccount.getState());
					break;
			}
		}
		
		/* Standard constructor */
		protected Account(AccountList    pList,
				          long           uId, 
						  String         sName, 
						  long			 uAcTypeId,
						  String         pDesc,
						  java.util.Date pMaturity,
				          java.util.Date pClose,
				          long           uParentId) throws finObject.Exception {
			/* Initialise the item */
			super(pList, uId);
			
			/* Local Variable */
			finStatic.AccountType myActType;
			
			/* Initialise the object */
			AccountValues myObj      = new AccountValues();
			setObj(myObj);
			myObj.setName(sName);
			myObj.setDesc(pDesc);
			
			/* Store the IDs */
			theActTypeId = uAcTypeId;
			theParentId  = uParentId;
			
			/* Look up the Account Type */
			myActType = theActTypes.searchFor(uAcTypeId);
			if (myActType == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
			                                  ObjectClass.ACCOUNT,
			                                  this,
						                      "Invalid Account Type Id");
			myObj.setType(myActType);
			theOrder    = myActType.getOrder();

			/* Parse the maturity date if it exists */
			if (pMaturity != null) 
				myObj.setMaturity(new finObject.Date(pMaturity));
				
			/* Parse the closed date if it exists */
			if (pClose != null) 
				myObj.setClose(new finObject.Date(pClose));
					
			/* Access the account types and accounts */
			/* Allocate the id */
			theIdManager.setNewAccount(this);
		}
		
		/* Standard constructor for a newly inserted account */
		public Account(AccountList pList) {
			super(pList, 0);
			AccountValues theObj = new AccountValues();
			setObj(theObj);
			setState(State.NEW);
		}

		/**
		 * Compare this account to another to establish equality.
		 * 
		 * @param that The account to compare to
		 * @return <code>true</code> if the account is identical, <code>false</code> otherwise
		 */
		public boolean equals(finLink.itemElement that) {
			Account myAccount = (Account)that;
			if (this == that) return true;
			if (getId() != myAccount.getId()) return false;
			if (finObject.differs(getName(),    	myAccount.getName())) 		return false;
			if (finObject.differs(getDesc(),    	myAccount.getDesc())) 		return false;
			if (finObject.differs(getActType(), 	myAccount.getActType())) 	return false;
			if (finObject.differs(getClose(),   	myAccount.getClose())) 		return false;
			if (finObject.differs(getMaturity(),	myAccount.getMaturity())) 	return false;
			if (finObject.differs(getParent(),      myAccount.getParent())) 	return false;			
			return true;
		}

		/**
		 * Override for standard method
		 */
		protected int compareTo(Account that) {
			long result;
			if (this == that) return 0;
			if (that == null) return -1;
			if (theOrder < that.theOrder) return -1;
			if (theOrder > that.theOrder) return  1;
			if (getName() != that.getName()) {
				if (this.getName() == null) return  1;
				if (that.getName() == null) return -1;
				result = getName().compareTo(that.getName());
				if (result < 0) return -1;
				if (result > 0) return 1;
			}
			result = (int)(getId() - that.getId());
			if (result == 0) return 0;
			else if (result < 0) return -1;
			else return 1;
		}

		public int linkCompareTo(linkObject that) {
			Account myItem = (Account)that;
			return this.compareTo(myItem);
		}
			
		/* Account flags */
		protected boolean isPriced()    { return getActType().isPriced(); }
		protected boolean isMarket()    { return getActType().isMarket(); }
		protected boolean isExternal()  { return getActType().isExternal(); }
		protected boolean isSpecial()   { return getActType().isSpecial(); }
		protected boolean isInternal()  { return getActType().isInternal(); }
		protected boolean isInheritance() { return getActType().isInheritance(); }
		protected boolean isTaxMan()    { return getActType().isTaxMan(); }
		protected boolean isMoney()     { return getActType().isMoney(); }
		protected boolean isCash()      { return getActType().isCash(); }
		protected boolean isWriteOff()  { return getActType().isWriteOff(); }
		protected boolean isEndowment() { return getActType().isEndowment(); }
		protected boolean isDebt()      { return getActType().isDebt(); }
		protected boolean isChild()     { return getActType().isChild(); }
		protected boolean isBond()      { return getActType().isBond(); }
		protected boolean isBenefit()   { return getActType().isBenefit(); }
		
		/**
		 * Validate the account
		 */
		protected void validate() {
			boolean 				isValid;
			finStatic.AccountType 	myType = getActType();
			
			/* Name must be non-null */
			if (getName() == null) {
				addError("Name must be non-null", FIELD_NAME);
		    }
			
			/* Check that the name is unique */
			else if (getCtl().countInstances(getName()) > 1) {
				addError("Name must be unique", FIELD_NAME);
		    }
			
			/* If the account is priced then prices must exist */
			if (myType.isPriced()) {
				if (!hasPrices)
					addError("Priced account has no prices", FIELD_TYPE);
		    }
			
			/* If the account is not priced then prices cannot exist */
			else {
				if (hasPrices)
					addError("non-Priced account has prices", FIELD_TYPE);
			}
			
			/* If the account is internal but not debt then patterns cannot exist */
			if (myType.isInternal() && (!myType.isDebt())) {
				if (hasPatterns)
					addError("Internal non-Debt account has patterns", FIELD_TYPE);
		    }
			
			/* If the account is not a child then parent cannot exist */
			if (!myType.isChild()) {
				if (getParent() != null)
					addError("Non-child account has parent", FIELD_PARENT);
		    }
			
			/* else we should have a parent */
			else {
				/* If data has been fully loaded we have no parent */
				if ((theLoadState == LoadState.LOADED) && 
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
			if ((theLoadState == LoadState.LOADED) && 
				(isClosed())) {
				/* Account must be closeable */
				if (!isCloseable())
					addError("Non-closeable account is closed", FIELD_CLOSE);
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
		public finObject.Money getValue(finObject.Date pDate) {
			Event           myCurr;
			int             myResult;
			finObject.Money myAmount;
			finObject.Money myValue;
			
			/* Initialise money */
			myValue = new finObject.Money(0);
				
			/* Loop through the Events extracting relevant elements */
			for (myCurr = theEvents.getFirst();
				 myCurr != null;
				 myCurr = myCurr.getNext()) {
			
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
		protected void setDescription(String pDesc) {
			getObj().setDesc((pDesc == null) ? null : new String(pDesc));
		}
		
		/**
		 * Set a new maturity date 
		 *	 
		 * @param pDate the new date 
		 */
		protected void setMaturity(finObject.Date pDate) {
			getObj().setMaturity((pDate == null) ? null : new finObject.Date(pDate));
		}
		
		/**
		 * Set a new close date 
		 *	 
		 * @param pDate the new date 
		 */
		protected void setClose(finObject.Date pDate) {
			getObj().setClose((pDate == null) ? null : new finObject.Date(pDate));
		}
		
		/**
		 * Set a new parent 
		 *	 
		 * @param pParent the new parent 
		 */
		protected void setParent(Account pParent) {
			getObj().setParent(pParent);
		}
		
		/**
		 * Set a new account name 
		 *	 
		 * @param pName the new name 
		 */
		protected void setAccountName(String pName) {
			getObj().setName((pName == null) ? null : new String(pName));
		}
		
		/**
		 * Set a new account type
		 *	 
		 * @param pType the new type 
		 */
		protected void setActType(finStatic.AccountType pType) {
			getObj().setType(pType);
			theOrder    = pType.getOrder();
		}
		
		/**
		 * Update base account from an edited account 
		 * 
		 * @param pAccount the edited account 
		 */
		public void applyChanges(finLink.itemElement pAccount) {
			Account myAccount = (Account)pAccount;
			
			/* Store the current detail into history */
			pushHistory();
			
			/* Update the Name if required */
			if (finObject.differs(getName(), myAccount.getName()))  
				setAccountName(myAccount.getName());
				
			/* Update the description if required */
			if (finObject.differs(getDesc(), myAccount.getDesc())) 
				setDescription(myAccount.getDesc());
				
			/* Update the account type if required */
			if (finObject.differs(getActType(), myAccount.getActType())) 
				setActType(myAccount.getActType());
				
			/* Update the maturity if required */
			if (finObject.differs(getMaturity(), myAccount.getMaturity())) 
				setMaturity(myAccount.getMaturity());
			
			/* Update the close if required */
			if (finObject.differs(getClose(), myAccount.getClose())) 
				setClose(myAccount.getClose());
			
			/* Update the parent if required */
			if (finObject.differs(getParent(), myAccount.getParent())) 
				setParent(myAccount.getParent());
			
			/* Check for changes */
			if (checkForHistory()) setState(State.CHANGED);
		}
	}
	

	public class PatternList extends finLink.itemCtl
							 implements finSwing.tableList {
		/* Local values */
		private Account	theAccount		= null;
		
		/* Linking methods */
		public Pattern getFirst() { return (Pattern)super.getFirst(); }
		public Pattern getLast()  { return (Pattern)super.getLast(); }
		public Pattern searchFor(long uId) {
			return (Pattern)super.searchFor(uId); }
		public Pattern extractItemAt(long uIndex) {
			return (Pattern)super.extractItemAt(uIndex); }
			
	 	/** 
	 	 * Construct an empty CORE pattern list
	 	 */
		protected PatternList() { super(ListStyle.CORE, false); }

		/** 
	 	 * Construct an empty generic pattern list
	 	 * @param pStyle the style of the list 
	 	 */
		protected PatternList(ListStyle pStyle) { super(pStyle, false); }

		/** 
	 	 * Construct a generic pattern list
	 	 * @param pList the source pattern list 
	 	 * @param pStyle the style of the list 
	 	 */
		protected PatternList(PatternList pList, ListStyle pStyle) { super(pList, pStyle); }

		/** 
	 	 * Construct a difference pattern list
	 	 * @param pNew the new Pattern list 
	 	 * @param pOld the old Pattern list 
	 	 */
		protected PatternList(PatternList pNew, PatternList pOld) { super(pNew, pOld); }
		
		/**
		 * Construct an edit extract of a Pattern list
		 * 
		 * @param pList      The list to extract from
		 * @param pAccount	 The account to extract patterns for 
		 */
		protected PatternList(PatternList pList,
						      Account  pAccount) {
			/* Make this list the correct style */
			super(ListStyle.EDIT, false);
			
			/* Local variables */
			Pattern myCurr;
			Pattern myItem;
			
			/* Store the account */
			theAccount = pAccount;
			
			/* Loop through the list */
			for (myCurr = pList.getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* If this item belongs to the account */
				if (!finObject.differs(myCurr.getAccount(), pAccount)) {
					/* Copy the item */
					myItem = theData.new Pattern(this, myCurr);
					myItem.addToList();
				}
			}
		}
	
		/** 
	 	 * Clone a Pattern list
	 	 * @return the cloned list
	 	 */
		protected PatternList cloneIt() { return new PatternList(this, ListStyle.CORE); }
		
		/* Is this list locked */
		public boolean isLocked() { return (theAccount != null) && (theAccount.isLocked()); }
		
		/**
		 * Add a new item to the core list
		 * 
		 * @param pPattern item
		 * @return the newly added item
		 */
		public finLink.itemElement addNewItem(finLink.itemElement pPattern) {
			Pattern myPattern = new Pattern(this, (Pattern)pPattern);
			myPattern.addToList();
			return myPattern;
		}
	
		/**
		 * Add a new item to the edit list
		 * 
		 * @param isCredit - is this a credit item
		 */
		public void addNewItem(boolean        isCredit) {
			Pattern myPattern = new Pattern(this, isCredit);
			myPattern.setAccount(theAccount);
			myPattern.addToList();
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "Pattern"; }
				
		/**
		 * Validate the patterns
		 */
		public void validate() {
			Pattern     myCurr;
			EventList   myEvents;
		
			/* Clear the errors */
			clearErrors();
			
			/* Create a new Event list */
			myEvents = new EventList(ListStyle.VIEW);
			
			/* Loop through the list */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* Ignore deleted patterns */
				if (myCurr.isDeleted()) continue;
				
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
			Pattern myCurr;
					
			/* Loop through the Patterns adjusting accounts */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				
				/* Ignore deleted patterns */
				if (myCurr.isDeleted()) continue;
				
				/* Touch the patterned account */
				myCurr.getAccount().touchPattern();			
				
				/* Touch the patterned partner */
				myCurr.getPartner().touchPartner();			
			}			 
		}
	}
		
	/* PatternValues */
	public static class PatternValues implements finLink.histObject {
		private finObject.Date       theDate      = null;
		private String               theDesc      = null;
		private finObject.Money      theAmount    = null;
		private Account              thePartner   = null;
		private finStatic.Frequency  theFrequency = null;
		private finStatic.TransType  theTransType = null;
		
		/* Access methods */
		public finObject.Date       getDate()      { return theDate; }
		public String               getDesc()      { return theDesc; }
		public finObject.Money      getAmount()    { return theAmount; }
		public Account              getPartner()   { return thePartner; }
		public finStatic.Frequency  getFrequency() { return theFrequency; }
		public finStatic.TransType  getTransType() { return theTransType; }
		
		public void setDate(finObject.Date pDate) {
			theDate      = pDate; }
		public void setDesc(String pDesc) {
			theDesc      = pDesc; }
		public void setAmount(finObject.Money pAmount) {
			theAmount    = pAmount; }
		public void setPartner(finData.Account pPartner) {
			thePartner   = pPartner; }
		public void setFrequency(finStatic.Frequency pFrequency) {
			theFrequency = pFrequency; }
		public void setTransType(finStatic.TransType pTransType) {
			theTransType = pTransType; }

		/* Constructor */
		public PatternValues() {}
		public PatternValues(PatternValues pValues) {
			theDate      = pValues.getDate();
			theDesc      = pValues.getDesc();
			theAmount    = pValues.getAmount();
			thePartner   = pValues.getPartner();
			theFrequency = pValues.getFrequency();
			theTransType = pValues.getTransType();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			PatternValues myValues = (PatternValues)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(PatternValues pValues) {
			if (finObject.differs(theDate,   pValues.theDate))         return false;
			if (finObject.differs(theDesc,   pValues.theDesc))         return false;
			if (finObject.differs(theAmount, pValues.theAmount))       return false;
			if (finObject.differs(thePartner, pValues.thePartner))     return false;
			if (finObject.differs(theFrequency, pValues.theFrequency)) return false;
			if (finObject.differs(theTransType, pValues.theTransType)) return false;
			return true;
		}
		
		/* Copy values */
		public void    copyFrom(histObject pSource) {
			PatternValues myValues = (PatternValues)pSource;
			copyFrom(myValues);
		}
		public histObject copySelf() {
			return new PatternValues(this);
		}
		public void    copyFrom(PatternValues pValues) {
			theDate      = pValues.getDate();
			theDesc      = pValues.getDesc();
			theAmount    = pValues.getAmount();
			thePartner   = pValues.getPartner();
			theFrequency = pValues.getFrequency();
			theTransType = pValues.getTransType();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			PatternValues 	pValues = (PatternValues)pOriginal;
			boolean			bResult = false;
			switch (fieldNo) {
				case Pattern.FIELD_DATE:
					bResult = (finObject.differs(theDate,      pValues.theDate));
					break;
				case Pattern.FIELD_DESC:
					bResult = (finObject.differs(theDesc,      pValues.theDesc));
					break;
				case Pattern.FIELD_TRNTYP:
					bResult = (finObject.differs(theTransType, pValues.theTransType));
					break;
				case Pattern.FIELD_AMOUNT:
					bResult = (finObject.differs(theAmount,    pValues.theAmount));
					break;
				case Pattern.FIELD_PARTNER:
					bResult = (finObject.differs(thePartner,   pValues.thePartner));
					break;
				case Pattern.FIELD_FREQ:
					bResult = (finObject.differs(theFrequency, pValues.theFrequency));
					break;
			}
			return bResult;
		}
	}
	
	public class Pattern extends finLink.itemElement
						 implements finSwing.tableElement {
		/* Local values */
		private long 	theAccountId	= -1;
		private long 	thePartnerId	= -1;
		private long 	theTransId		= -1;
		private long 	theFreqId		= -1;
		private Account	theAccount		= null;
		private boolean isCredit     	= false;
				
		/* Access methods */
		public  PatternValues         getObj()       { return (PatternValues)super.getObj(); }	
		public  finObject.Date        getDate()      { return getObj().getDate(); }
		public  String                getDesc()      { return getObj().getDesc(); }
		public  finObject.Money       getAmount()    { return getObj().getAmount(); }
		public  Account               getPartner()   { return getObj().getPartner(); }
		public  finStatic.Frequency   getFrequency() { return getObj().getFrequency(); }
		public  finStatic.TransType   getTransType() { return getObj().getTransType(); }
		public  finStatic.AccountType getActType()   { return theAccount.getActType(); }
		public  boolean               isCredit()     { return isCredit; }
		public  Account		   getAccount()	  { return theAccount; }
		private void         	setAccount(Account pAccount)   {
			theAccount = pAccount; }
	
		/* Linking methods */
		public Pattern     getNext() { return (Pattern)super.getNext(); }
		public Pattern     getPrev() { return (Pattern)super.getPrev(); }
		public Pattern     getBase() { return (Pattern)super.getBase(); }
		public PatternList getCtl()  { return (PatternList)super.getCtl(); } 
		public finSwing.tableList getList()  { 
			return (finSwing.tableList)super.getCtl(); } 
		
		/* Field IDs */
		public static final int FIELD_ID       = 0;
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
		public String itemType() { return "Pattern"; }
		
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
				case FIELD_ACCOUNT:		return "Account";
				case FIELD_DATE:		return "Date";
				case FIELD_DESC:		return "Description";
				case FIELD_PARTNER:		return "Partner";
				case FIELD_AMOUNT:		return "Amount";
				case FIELD_TRNTYP:		return "TransactionType";
				case FIELD_FREQ:		return "Frequency";
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
			String 			myString = "<tr><td>" + fieldName(iField) + "</td><td>";
			PatternValues 	myObj 	 = (PatternValues)pObj;
			switch (iField) {
				case FIELD_ID: 		
					myString += getId(); 
					break;
				case FIELD_ACCOUNT:	
					if ((theAccount == null) &&
						(theAccountId != -1))
						myString += "Id=" + theAccountId;
					else
						myString += finObject.formatAccount(theAccount); 
					break;
				case FIELD_DATE:	
					myString += finObject.formatDate(myObj.getDate()); 
					break;
				case FIELD_DESC:	
					myString += myObj.getDesc(); 
					break;
				case FIELD_PARTNER:	
					if ((myObj.getPartner() == null) &&
						(thePartnerId != -1))
						myString += "Id=" + thePartnerId;
					else
						myString += finObject.formatAccount(myObj.getPartner()); 
					break;
				case FIELD_TRNTYP:	
					if ((myObj.getTransType() == null) &&
						(theTransId != -1))
						myString += "Id=" + theTransId;
					else
						myString += finObject.formatTrans(myObj.getTransType()); 
					break;
				case FIELD_AMOUNT:	
					myString += finObject.formatMoney(myObj.getAmount()); 
					break;
				case FIELD_FREQ:	
					if ((myObj.getFrequency() == null) &&
						(theFreqId != -1))
						myString += "Id=" + theFreqId;
					else
						myString += finObject.formatFreq(myObj.getFrequency()); 
					break;
				case FIELD_CREDIT: 
					myString +=	(isCredit() ? "true" : "false");
					break;
			}
			return myString + "</td></tr>";
		}
								
		/**
		 * Construct a copy of a Pattern
		 * 
		 * @param pPattern The Pattern 
		 */
		protected Pattern(PatternList pList, Pattern pPattern) {
			/* Set standard values */
			super(pList, pPattern.getId());
			PatternValues myObj   = new PatternValues(pPattern.getObj());
			setObj(myObj);
			isCredit   = pPattern.isCredit();
			theAccount = pPattern.theAccount;
		
			/* Switch on the LinkStyle */
			switch (pList.getStyle()) {
				case CORE:
					theIdManager.setNewPattern(this);
					break;
				case EDIT:
					setBase(pPattern);
					setState(State.CLEAN);
					break;
				case UPDATE:
					setBase(pPattern);
					setState(pPattern.getState());
					break;
			}
		}
	
		/* Is this list locked */
		public boolean isLocked() { return theAccount.isLocked(); }
		
		/* Standard constructor for a newly inserted pattern */
		public Pattern(PatternList    pList,
					   boolean        isCredit) {
			super(pList, 0);
			this.isCredit 		= isCredit;
			PatternValues myObj = new PatternValues();
			setObj(myObj);
			setState(State.NEW);
		}

		/* Construct a new pattern from a statement line */
		protected Pattern(PatternList pList, finView.Statement.Line pLine) {
			/* Set standard values */
			super(pList, 0);
			PatternValues myObj   = new PatternValues();
			setObj(myObj);
			myObj.setDate(new finObject.Date(pLine.getDate()));
			myObj.setDesc(pLine.getDesc());
			myObj.setTransType(pLine.getTransType());
			myObj.setAmount(pLine.getAmount());
			myObj.setPartner(pLine.getPartner());
			myObj.setFrequency(theFrequencys.searchFor(FreqClass.ANNUALLY));
			isCredit   = pLine.isCredit();
			theAccount = pLine.getAccount();
			setState(State.NEW);
			
			/* Adjust the date so that it is in the 2000 tax year */
			TaxParms       myTax  = theData.getTaxYears().searchFor("2000");
			finObject.Date myDate = getDate();
			while (myDate.compareTo(myTax.getDate()) > 0) myDate.adjustYear(-1);
			myTax = myTax.getPrev();
			while (myDate.compareTo(myTax.getDate()) <= 0) myDate.adjustYear(1);
		}
	
		/* Standard constructor */
		public Pattern(PatternList      pList,
				       long             uId,
				       long             uAccountId,
		               java.util.Date   pDate,
		               String           pDesc,
		               String			pAmount,
		               long				uPartnerId,
		               long 			uTransId,
		               long				uFreqId,
		               boolean          isCredit) throws finObject.Exception {
			/* Initialise item */
			super(pList, uId);
			
			/* Local variables */
			Account 			myAccount;
			finStatic.TransType myTrans;
			finStatic.Frequency myFreq;
			
			/* Initialise values */
			PatternValues myObj = new PatternValues();
			setObj(myObj);
			myObj.setDesc(pDesc);
			this.isCredit = isCredit;
			
			/* Record the IDs */
			theAccountId = uAccountId;
			thePartnerId = uPartnerId;
			theTransId	 = uTransId;
			theFreqId    = uFreqId;
			
			/* Look up the Account */
			theAccount = theAccounts.searchFor(uAccountId);
			if (theAccount == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.PATTERN,
											  this,
											  "Invalid Account Id");
						
			/* Look up the Partner */
			myAccount = theAccounts.searchFor(uPartnerId);
			if (myAccount == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.PATTERN,
											  this,
											  "Invalid Partner Id");
			myObj.setPartner(myAccount);
						
			/* Look up the TransType */
			myTrans = theTransTypes.searchFor(uTransId);
			if (myTrans == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.PATTERN,
											  this,
											  "Invalid TransType Id");
			myObj.setTransType(myTrans);
						
			/* Look up the Frequency */
			myFreq = theFrequencys.searchFor(uFreqId);
			if (myFreq == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.PATTERN,
											  this,
											  "Invalid Frequency Id");
			myObj.setFrequency(myFreq);

			/* Create the date */
			myObj.setDate(new finObject.Date(pDate));
			
			/* Record the amount */
			finObject.Money myAmount = finObject.Money.Parse(pAmount);
			if (myAmount == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.PATTERN,
											  this,
											  "Invalid Amount: " + pAmount);
			myObj.setAmount(myAmount);

			/* Allocate the id */
			theIdManager.setNewPattern(this);
		}
				
		/**
		 * Compare this pattern to another to establish equality.
		 * 
		 * @param that The pattern to compare to
		 * @return <code>true</code> if the pattern is identical, <code>false</code> otherwise
		 */
		public boolean equals(finLink.itemElement that) {
			Pattern myPattern = (Pattern) that;
			if (this == that) return true;
			if (getId() != myPattern.getId()) return false;
			if (finObject.differs(getDate(),       myPattern.getDate())) 		return false;
			if (finObject.differs(getDesc(),       myPattern.getDesc())) 		return false;
			if (finObject.differs(getTransType(),  myPattern.getTransType())) 	return false;
			if (finObject.differs(getAmount(),     myPattern.getAmount())) 		return false;
			if (finObject.differs(getAccount(),    myPattern.getAccount())) 	return false;
			if (finObject.differs(getPartner(),    myPattern.getPartner())) 	return false;
			if (finObject.differs(getFrequency(),  myPattern.getFrequency())) 	return false;
			if (isCredit() != myPattern.isCredit()) return false;
			return true;
		}

		/**
		 * Override for standard method
		 */
		protected int compareTo(Pattern that) {
			int iDiff;
			if (this == that) return 0;
			if (that == null) return -1;
			if (this.getDate() != that.getDate()) {
				if (this.getDate() == null) return 1;
				if (that.getDate() == null) return -1;
				iDiff = getDate().compareTo(that.getDate());
				if (iDiff != 0) return iDiff;
			}
			if (this.getDesc() != that.getDesc()) {
				if (this.getDesc() == null) return 1;
				if (that.getDesc() == null) return -1;
				iDiff = getDesc().compareTo(that.getDesc());
				if (iDiff < 0) return -1;
				if (iDiff > 0) return 1;
			}
			if (this.getTransType() != that.getTransType()) {
				if (this.getTransType() == null) return 1;
				if (that.getTransType() == null) return -1;
				iDiff = getTransType().compareTo(that.getTransType());
				if (iDiff != 0) return iDiff;
			}
			iDiff = (int)(getId() - that.getId());
			if (iDiff < 0) return -1;
			if (iDiff > 0) return 1;
			return 0;
		}

		public int linkCompareTo(linkObject that) {
			Pattern myItem = (Pattern)that;
			return this.compareTo(myItem);
		}
		
		/**
		 * Validate the pattern
		 */
		public    void validate() { validate(null); }
		protected void validate(EventList pList) {
			Event        myEvent;
			errorElement myError;
			int          iField;
		
			/* Create a new Event list */
			if (pList == null)
				pList = new EventList(ListStyle.VIEW);
			
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
		protected Event nextEvent(EventList      pEvents,
								  TaxParms       pTaxYear,
				                  finObject.Date pDate) {
			Event     		myEvent;
			TaxParms  		myBase;
			finObject.Date 	myDate;
			FreqClass		myFreq;
			int       		iAdjust;
			
			/* Access the frequency */
			myFreq = getFrequency().getFrequency();
			
			/* If this is the first request for an event */
			if (pDate.compareTo(getDate()) == 0) {
				/* If the frequency is maturity */
				if (myFreq == FreqClass.MATURITY) {
					/* Access the maturity date */
					myDate = theAccount.getMaturity();
					
					/* Obtain the relevant tax year */
					myBase = theTaxYears.searchFor(getDate());
				
					/* Ignore if no maturity or else not this year */
					if ((myDate == null)  ||
						(myDate.isNull()) ||
						(myBase == null)  ||
						(myBase.compareTo(pTaxYear) != 0)) 
						return null;
				}
				
				/* Obtain the base tax year */
				myBase = theTaxYears.searchFor("2000");
			
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
					case ENDMONTH:
						pDate.endNextMonth();
						break;
				}
				
				/* If we are beyond the end of the year we have finished */
				if (pDate.compareTo(pTaxYear.getDate()) > 0)
					return null;
				
				/* If this is a ten month repeat */
				if (myFreq == FreqClass.TENMONTHS) {					
					myDate = new finObject.Date(getDate());
					
					/* Obtain the base tax year */
					myBase = theTaxYears.searchFor("2000");
				
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
			myEvent.setDate(new finObject.Date(pDate));
			
			/* Return the new event */
			return myEvent;
		}
		
		/**
		 * Set a new partner 
		 * 
		 * @param pPartner the account 
		 */
		protected void setPartner(Account pPartner) {
			getObj().setPartner(pPartner);
		}
	
		/**
		 * Set a new transtype 
		 * 
		 * @param pTransType the transtype 
		 */
		protected void setTransType(finStatic.TransType pTransType) {
			getObj().setTransType(pTransType);
		}
	
		/**
		 * Set a new frequency 
		 * 
		 * @param pFrequency the frequency 
		 */
		protected void setFrequency(finStatic.Frequency pFrequency) {
			getObj().setFrequency(pFrequency);
		}
	
		/**
		 * Set a new description 
		 * 
		 * @param pDesc the description 
		 */
		protected void setDesc(String pDesc) {
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
		 * Set a new date 
		 * 
		 * @param pDate the new date 
		 */
		protected void setDate(finObject.Date pDate) {
			getObj().setDate((pDate == null) ? null : new finObject.Date(pDate));
		}
	
		/**
		 * Update Pattern from a pattern extract 
		 * 
		 * @param pPattern the pattern extract 
		 */
		public void applyChanges(finLink.itemElement pPattern) {
			Pattern myPattern = (Pattern)pPattern;
			
			/* Store the current detail into history */
			pushHistory();
			
			/* Update the partner if required */
			if (finObject.differs(getPartner(), myPattern.getPartner())) 
				setPartner(myPattern.getPartner());
		
			/* Update the transtype if required */
			if (finObject.differs(getTransType(), myPattern.getTransType())) 
				setTransType(myPattern.getTransType());
		
			/* Update the frequency if required */
			if (finObject.differs(getFrequency(), myPattern.getFrequency())) 
				setFrequency(myPattern.getFrequency());
		
			/* Update the description if required */
			if (finObject.differs(getDesc(), myPattern.getDesc())) 
				setDesc(myPattern.getDesc());
		
			/* Update the amount if required */
			if (finObject.differs(getAmount(), myPattern.getAmount())) 
				setAmount(myPattern.getAmount());
			
			/* Update the date if required */
			if (finObject.differs(getDate(), myPattern.getDate())) 
				setDate(myPattern.getDate());
			
			/* Check for changes */
			if (checkForHistory()) setState(State.CHANGED);
		}
	}
	
	public class RateList  	extends finLink.itemCtl 
						 	implements finSwing.tableList {
		/* Members */
		private Account	  theAccount	= null;

		/* Linking methods */
		public Rate getFirst() { return (Rate)super.getFirst(); }
		public Rate getLast()  { return (Rate)super.getLast(); }
		public Rate searchFor(long uId) {
			return (Rate)super.searchFor(uId); }
		public Rate extractItemAt(long uIndex) {
			return (Rate)super.extractItemAt(uIndex); }

	 	/** 
	 	 * Construct an empty CORE rate list
	 	 */
		protected RateList() { super(ListStyle.CORE, false); }

		/** 
	 	 * Construct an empty generic rate list
	 	 * @param pStyle the style of the list 
	 	 */
		protected RateList(ListStyle pStyle) { super(pStyle, false); }

		/** 
	 	 * Construct a generic rate list
	 	 * @param pList the source rate list 
	 	 * @param pStyle the style of the list 
	 	 */
		protected RateList(RateList pList, ListStyle pStyle) { super(pList, pStyle); }

		/** 
	 	 * Construct a difference rate list
	 	 * @param pNew the new Rate list 
	 	 * @param pOld the old Rate list 
	 	 */
		protected RateList(RateList pNew, RateList pOld) { super(pNew, pOld); }
		
		/**
		 * Construct an edit extract of a Rate list
		 * 
		 * @param pList      The list to extract from
		 * @param pAccount	 The account to extract rates for 
		 */
		protected RateList(RateList pList,
						   Account  pAccount) {
			/* Make this list the correct style */
			super(ListStyle.EDIT, false);
			
			/* Local variables */
			Rate myCurr;
			Rate myItem;
			
			/* Store the account */
			theAccount = pAccount;
			
			/* Loop through the list */
			for (myCurr = pList.getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* If this item belongs to the account */
				if (!finObject.differs(myCurr.getAccount(), pAccount)) {
					/* Copy the item */
					myItem = theData.new Rate(this, myCurr);
					myItem.addToList();
				}
			}
		}
	
		/** 
	 	 * Clone a Rate list
	 	 * @return the cloned list
	 	 */
		protected RateList cloneIt() { return new RateList(this, ListStyle.CORE); }
		
		/* Is this list locked */
		public boolean isLocked() { return ((theAccount != null) && (theAccount.isLocked())); }

		/**
		 * Add a new item to the core list
		 * 
		 * @param pRate item
		 * @return the newly added item
		 */
		public finLink.itemElement addNewItem(finLink.itemElement pRate) {
			Rate myRate = new Rate(this, (Rate)pRate);
			myRate.addToList();
			return myRate;
		}

		/**
		 * Add a new item to the edit list
		 * 
		 * @param isCredit - ignored
		 */
		public void addNewItem(boolean        isCredit) {
			Rate myRate = new Rate(this);
			myRate.setAccount(theAccount);
			myRate.addToList();
		}

		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "Rate"; }

		/**
		 * Count the instances of a date
		 * 
		 * @param pDate the date
		 * @return The Item if present (or null)
		 */
		protected int countInstances(finObject.Date pDate,
				                     Account        pAccount) {
			Rate myCurr;
			int  iDiff;
			int  iCount = 0;

			/* Loop through the items to find the entry */
			for (myCurr = getFirst();
				 myCurr != null;
				 myCurr = myCurr.getNext()) {
				iDiff = pDate.compareTo(myCurr.getEndDate());
				if (iDiff == 0) iDiff = pAccount.compareTo(myCurr.getAccount());
				if (iDiff == 0) iCount++;
			}

			/* Return to caller */
			return iCount;
		}	

		/**
		 *  Mark active rates
		 */
		protected void markActiveRates() {
			Rate myCurr;
		
			/* Loop through the Rates */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* mark the account referred to */
				myCurr.getAccount().touchRate(); 
			}
		}
		
		/**
		 *  Obtain the most relevant rate for an Account and a Date
		 *   @param pAccount the Account for which to get the rate
		 *   @param pDate the date from which a rate is required
		 *   @return The relevant Rate record 
		 */
		protected Rate getLatestRate(Account        pAccount, 
									 finObject.Date pDate) {
			Rate           myRate = null;
			Rate           myCurr;
			finObject.Date myDate;
		
			/* Loop through the Rates */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* Skip records that do not belong to this account */
				if (finObject.differs(myCurr.getAccount(), pAccount))
					continue;
				
				/* Access the date */
				myDate = myCurr.getDate();
				
				/* break loop if we have the correct record */
				if ((myDate == null) ||
					(myDate.isNull()) ||
					(myDate.compareTo(pDate) >= 0)) {
					myRate = myCurr; 
					break; 
				}
			}
				
			/* Return the rate */
			return myRate;
		}		
	}

	/* RateValues */
	public static class RateValues implements finLink.histObject {
		private finObject.Rate       theRate      = null;
		private finObject.Rate       theBonus     = null;
		private finObject.Date       theEndDate   = null;
		
		/* Access methods */
		public finObject.Rate       getRate()      { return theRate; }
		public finObject.Rate       getBonus()     { return theBonus; }
		public finObject.Date       getEndDate()   { return theEndDate; }
		
		public void setRate(finObject.Rate pRate) {
			theRate      = pRate; }
		public void setBonus(finObject.Rate pBonus) {
			theBonus     = pBonus; }
		public void setEndDate(finObject.Date pEndDate) {
			theEndDate   = pEndDate; }

		/* Constructor */
		public RateValues() {}
		public RateValues(RateValues pValues) {
			theRate      = pValues.getRate();
			theBonus     = pValues.getBonus();
			theEndDate   = pValues.getEndDate();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			RateValues myValues = (RateValues)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(RateValues pValues) {
			if (finObject.differs(theRate,    pValues.theRate))    return false;
			if (finObject.differs(theBonus,   pValues.theBonus))   return false;
			if (finObject.differs(theEndDate, pValues.theEndDate)) return false;
			return true;
		}
		
		/* Copy values */
		public void    copyFrom(histObject pSource) {
			RateValues myValues = (RateValues)pSource;
			copyFrom(myValues);
		}
		public histObject copySelf() {
			return new RateValues(this);
		}
		public void    copyFrom(RateValues pValues) {
			theRate      = pValues.getRate();
			theBonus     = pValues.getBonus();
			theEndDate   = pValues.getEndDate();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			RateValues 		pValues = (RateValues)pOriginal;
			boolean			bResult = false;
			switch (fieldNo) {
				case Rate.FIELD_RATE:
					bResult = (finObject.differs(theRate,    pValues.theRate));
					break;
				case Rate.FIELD_BONUS:
					bResult = (finObject.differs(theBonus,   pValues.theBonus));
					break;
				case Rate.FIELD_ENDDATE:
					bResult = (finObject.differs(theEndDate, pValues.theEndDate));
					break;
			}
			return bResult;
		}
	}
	
	public class Rate 	extends finLink.itemElement
						implements finSwing.tableElement {
		/* Local values */
		private long 	theAccountId	= -1;
		private Account	theAccount		= null;
		
		/* Access methods */
		public  RateValues     getObj()       { return (RateValues)super.getObj(); }	
		public  finObject.Rate getRate()      { return getObj().getRate(); }
		public  finObject.Rate getBonus()     { return getObj().getBonus(); }
		public  finObject.Date getDate()   	  { return getObj().getEndDate(); }
		public  finObject.Date getEndDate()   { return getObj().getEndDate(); }
		public  Account		   getAccount()	  { return theAccount; }
		private void         	setAccount(Account pAccount)   {
			theAccount = pAccount; }

		/* Linking methods */
		public Rate     getNext() { return (Rate)super.getNext(); }
		public Rate     getPrev() { return (Rate)super.getPrev(); }
		public Rate     getBase() { return (Rate)super.getBase(); }
		public RateList getCtl()  { return (RateList)super.getCtl(); } 
		public finSwing.tableList getList()  { 
			return (finSwing.tableList)super.getCtl(); } 

		/* Field IDs */
		public static final int FIELD_ID       = 0;
		public static final int FIELD_ACCOUNT  = 1;
		public static final int FIELD_RATE     = 2;
		public static final int FIELD_BONUS    = 3;
		public static final int FIELD_ENDDATE  = 4;
		public static final int NUMFIELDS	   = 5;

		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "Rate"; }

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
				case FIELD_ACCOUNT:		return "Account";
				case FIELD_RATE:		return "Rate";
				case FIELD_BONUS:		return "Bonus";
				case FIELD_ENDDATE:		return "EndDate";
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
			RateValues 	myObj 	 = (RateValues)pObj;
			switch (iField) {
				case FIELD_ID: 		
					myString += getId(); 
					break;
				case FIELD_ACCOUNT:
					if ((theAccount == null) &&
						(theAccountId != -1))
						myString += "Id=" + theAccountId;
					else
						myString += finObject.formatAccount(theAccount); 
					break;
				case FIELD_RATE:	
					myString += finObject.formatRate(myObj.getRate()); 
					break;
				case FIELD_BONUS:	
					myString += finObject.formatRate(myObj.getBonus()); 
					break;
				case FIELD_ENDDATE:	
					myString += finObject.formatDate(myObj.getEndDate()); 
					break;
			}
			return myString + "</td></tr>";
		}
			
		/**
		 *	Construct a copy of a Rate Period
		 * 
		 * @param pPeriod The Period to copy 
		 */
		protected Rate(RateList pList, Rate pPeriod) {
			/* Set standard values */
			super(pList, pPeriod.getId());
			RateValues myObj = new RateValues(pPeriod.getObj());
			setObj(myObj);
			theAccount = pPeriod.theAccount;

			/* Switch on the LinkStyle */
			switch (pList.getStyle()) {
				case CORE:
					theIdManager.setNewRate(this);
					break;
				case EDIT:
					setBase(pPeriod);
					setState(State.CLEAN);
					break;
				case UPDATE:
					setBase(pPeriod);
					setState(pPeriod.getState());
					break;
			}
		}

		/* Standard constructor for a newly inserted rate */
		public Rate(RateList pList) {
			super(pList, 0);
			RateValues myObj = new RateValues();
			setObj(myObj);
			setState(State.NEW);
		}

		/* Standard constructor */
		public Rate(RateList       pList,
					long           uId,
					long 		   uAccountId,
					java.util.Date pEndDate, 
					String		   pRate,
					String		   pBonus) throws finObject.Exception {
			/* Initialise the item */
			super(pList, uId);
			
			/* Initialise the values */
			RateValues myObj = new RateValues();
			setObj(myObj);

			/* Record the Id */
			theAccountId = uAccountId;
			
			/* Look up the Account */
			theAccount = theAccounts.searchFor(uAccountId);
			if (theAccount == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.RATE,
											  this,
											  "Invalid Account Id");
						
			/* Record the date */
			if (pEndDate != null)
				myObj.setEndDate(new finObject.Date(pEndDate));
			
			/* Record the rate */
			finObject.Rate myRate = finObject.Rate.Parse(pRate);
			if (myRate == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.RATE,
											  this,
											  "Invalid Rate: " + pRate);
			myObj.setRate(myRate);

			/* If we have a bonus */
			if (pBonus != null) {
				/* Record the bonus */
				myRate = finObject.Rate.Parse(pBonus);
				if (myRate == null) 
					throw new finObject.Exception(ExceptionClass.DATA,
											  	  ObjectClass.RATE,
											  	  this,
											  	  "Invalid Bonus: " + pBonus);
				myObj.setBonus(myRate);
			}
			
			/* Allocate the id */
			theIdManager.setNewRate(this);
		}

		/**
		 * Compare this rate to another to establish equality.
		 * 
		 * @param that The Rate to compare to
		 * @return <code>true</code> if the rate is identical, <code>false</code> otherwise
		 */
		public boolean equals(finLink.itemElement that) {
			Rate myRate = (Rate)that;
			if (this == that) return true;
			if (getId() != myRate.getId()) return false;
			if (finObject.differs(getAccount(), myRate.getAccount())) 	return false;
			if (finObject.differs(getEndDate(), myRate.getEndDate())) 	return false;
			if (finObject.differs(getRate(),    myRate.getRate())) 		return false;
			if (finObject.differs(getBonus(),  	myRate.getBonus()))		return false;
			return true;
		}

		/**
		 * Override for standard method
		 */
		protected int compareTo(Rate that) {
			int iDiff;
			if (this == that) return 0;
			if (that == null) return -1;
			if (this.getEndDate() != that.getEndDate()) {
				if (this.getEndDate() == null) return 1;
				if (that.getEndDate() == null) return -1;
				iDiff = getEndDate().compareTo(that.getEndDate());
				if (iDiff != 0) return iDiff;
			}
			iDiff = (int)(getId() - that.getId());
			if (iDiff < 0) return -1;
			if (iDiff > 0) return 1;
			return 0;
		}

		public int linkCompareTo(linkObject that) {
			Rate myItem = (Rate)that;
			return this.compareTo(myItem);
		}

		/**
		 * Validate the rate
		 */
		public void validate() {
			Rate           myCurr;
			finObject.Date myDate = getEndDate();

			/* If the date is null then we must be the last element */
			if ((myDate == null) || (myDate.isNull())) {
				/* Loop to the next valid element */
				for (myCurr = getNext();
					 (myCurr != null);
					 myCurr = myCurr.getNext()){
					/* Ignore deleted items */
					if (!myCurr.isDeleted()) continue;
					
					/* Ignore if this item doesn't belong to the account */
					if (!finObject.differs(myCurr.getAccount(), theAccount))
						continue;
				}

				/* If we have a later element then error */
				if (myCurr != null)
					addError("Null date is only allowed on last date", FIELD_ENDDATE);
			}

			/* If we have a date */
			if (myDate != null) {
				/* The date must be unique for this account */
				if (getCtl().countInstances(myDate, theAccount) > 1) {
					addError("Rate Date must be unique", FIELD_ENDDATE);
				}

				/* The date must be in-range (unless it is the last one) */
				if ((getNext() != null) && 
					(getDateRange().compareTo(myDate) != 0)) {
					addError("Date must be within range", FIELD_ENDDATE);
				}
			}

			/* The rate must be non-zero */
			if ((getRate() == null) || 
				(!getRate().isPositive())) {
				addError("Rate must be positive", FIELD_RATE);
			}

			/* The bonus rate must be non-zero if it exists */
			if ((getBonus() != null) &&
				((!getBonus().isNonZero()) ||
				 (!getBonus().isPositive()))) {
				addError("Bonus Rate must be non-Zero and positive", FIELD_BONUS);
			}						

			/* Set validation flag */
			if (!hasErrors()) setValidEdit();
		}

		/**
		 * Set a new rate 
		 * 
		 * @param pRate the rate 
		 */
		protected void setRate(finObject.Rate pRate) {
			getObj().setRate((pRate == null) ? null : new finObject.Rate(pRate));
		}

		/**
		 * Set a new bonus 
		 * 
		 * @param pBonus the rate 
		 */
		protected void setBonus(finObject.Rate pBonus) {
			getObj().setBonus((pBonus == null) ? null : new finObject.Rate(pBonus));
		}

		/**
		 * Set a new date 
		 * 
		 * @param pDate the new date 
		 */
		protected void setEndDate(finObject.Date pDate) {
			getObj().setEndDate(new finObject.Date(pDate));
		}

		/**
		 * Update Rate from a Rate extract 
		 * 
		 * @param pRate the updated item 
		 */
		public void applyChanges(finLink.itemElement pRate) {
			Rate myRate =  (Rate)pRate;

			/* Store the current detail into history */
			pushHistory();

			/* Update the rate if required */
			if (finObject.differs(getRate(), myRate.getRate())) 
				setRate(myRate.getRate());

			/* Update the bonus if required */
			if (finObject.differs(getBonus(), myRate.getBonus())) 
				setBonus(myRate.getBonus());

			/* Update the date if required */
			if (finObject.differs(getEndDate(), myRate.getEndDate())) 
				setEndDate(myRate.getEndDate());

			/* Check for changes */
			if (checkForHistory()) setState(State.CHANGED);
		}
	}

	
	public class PriceList  extends    finLink.itemCtl
							implements finSwing.tableList {
		/* Members */
		private Account	  theAccount	= null;

		/* Linking methods */
		public Price getFirst() { return (Price)super.getFirst(); }
		public Price getLast()  { return (Price)super.getLast(); }
		public Price searchFor(long uId) {
			return (Price)super.searchFor(uId); }
		public Price extractItemAt(long uIndex) {
			return (Price)super.extractItemAt(uIndex); }
			
	 	/** 
	 	 * Construct an empty CORE price list
	 	 */
		protected PriceList() { super(ListStyle.CORE, false); }

		/** 
	 	 * Construct an empty generic price list
	 	 * @param pStyle the style of the list 
	 	 */
		protected PriceList(ListStyle pStyle) { super(pStyle, false); }

		/** 
	 	 * Construct a generic Price list
	 	 * @param pList the source price list 
	 	 * @param pStyle the style of the list 
	 	 */
		protected PriceList(PriceList pList, ListStyle pStyle) { super(pList, pStyle); }

		/** 
	 	 * Construct a difference price list
	 	 * @param pNew the new Price list 
	 	 * @param pOld the old Price list 
	 	 */
		protected PriceList(PriceList pNew, PriceList pOld) { super(pNew, pOld); }
		
		/**
		 * Construct an edit extract of a Price list
		 * 
		 * @param pList      The list to extract from
		 * @param pAccount	 The account to extract rates for 
		 */
		protected PriceList(PriceList pList,
						    Account   pAccount) {
			/* Make this list the correct style */
			super(ListStyle.EDIT, false);
			
			/* Local variables */
			Price myCurr;
			Price myItem;
			
			/* Store the account */
			theAccount = pAccount;
			
			/* Loop through the list */
			for (myCurr = pList.getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* If this item belongs to the account */
				if (!finObject.differs(myCurr.getAccount(), pAccount)) {
					/* Copy the item */
					myItem = theData.new Price(this, myCurr);
					myItem.addToList();
				}
			}
		}
	
		/** 
	 	 * Clone a Price list
	 	 * @return the cloned list
	 	 */
		protected PriceList cloneIt() { return new PriceList(this, ListStyle.CORE); }
		
		/* Is this list locked */
		public boolean isLocked() { return ((theAccount != null) && (theAccount.isLocked())); }

		/**
		 * Add a new item to the core list
		 * 
		 * @param pPrice item
		 * @return the newly added item
		 */
		public finLink.itemElement addNewItem(finLink.itemElement pPrice) {
			Price myPrice = new Price(this, (Price)pPrice);
			myPrice.addToList();
			return myPrice;
		}
	
		/**
		 * Add a new item to the edit list
		 * 
		 * @param isCredit - ignored
		 */
		public void addNewItem(boolean        isCredit) {
			Price myPrice = new Price(this);
			myPrice.setAccount(theAccount);
			myPrice.addToList();
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "Price"; }
				
		/**
		 * Count the instances of a date
		 * 
		 * @param pDate the date
		 * @return The Item if present (or null)
		 */
		protected int countInstances(finObject.Date pDate,
				                     Account		pAccount) {
			Price    myCurr;
			int      iDiff;
			int      iCount = 0;
			
			/* Loop through the items to find the entry */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				iDiff = pDate.compareTo(myCurr.getDate());
				if (iDiff == 0) iDiff = pAccount.compareTo(myCurr.getAccount());
				if (iDiff == 0) iCount++;
			}
			
			/* return to caller */
			return iCount;
		}
			
		/**
		 *  Obtain the most relevant price for a Date
		 *  
		 *   @param pDate the date from which a price is required
		 *   @return The relevant Price record 
		 */
		protected Price getLatestPrice(Account        pAccount,
				                       finObject.Date pDate) {
			Price myPrice = null;
			Price myCurr;
		
			/* Loop through the Prices */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* Skip records that do not belong to this account */
				if (finObject.differs(myCurr.getAccount(), pAccount))
					continue;
				
				/* break loop if we have passed the date */
				if (myCurr.getDate().compareTo(pDate) > 0) break;
				
				/* Record the best case so far */
				myPrice = myCurr;
			}
		
			/* Return the price */
			return myPrice;
		}
		
		/**
		 *  Mark active prices
		 */
		protected void markActivePrices () {
			Price myCurr;
		
			/* Loop through the Prices */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* mark the account referred to */
				myCurr.getAccount().touchPrice(); 
			}
		}
		
		/**
		 * Apply changes from a Spot Price list
		 */
		public void applyChanges(finView.SpotPrices pPrices) {
			finView.SpotPrices.List 	 	myList;
			finView.SpotPrices.SpotPrice 	mySpot;
			finObject.Date					myDate;
			finObject.Price					myPoint;
			Price							myPrice;
			
			/* Access details */
			myDate = pPrices.getDate();
			myList = pPrices.getPrices();
			
			/* Loop through the spot prices */
			for (mySpot  = myList.getFirst();
				 mySpot != null;
				 mySpot  = mySpot.getNext()) {
				/* Access the price for this date if it exists */
				myPrice 	= mySpot.getBase();
				myPoint 	= mySpot.getPrice();
				
				/* If the state is not clean */
				if (mySpot.getState() != State.CLEAN) {
					/* If we have an underlying price */
					if (myPrice != null) {
						/* Apply changes to the underlying entry */
						myPrice.applyChanges(mySpot);
					}
					
					/* else if we have a new price with no underlying */
					else if (myPoint != null) {
						/* Create the new Price */
						myPrice = theData.new Price(this);
						
						/* Set the date and price */
						myPrice.setDate(new finObject.Date(myDate));
						myPrice.setPrice(new finObject.Price(myPoint));
						myPrice.setAccount(mySpot.getAccount());
						
						/* Add to the list and link backwards */
						mySpot.setBase(myPrice);
						myPrice.addToList();
					}

					/* Clear history and set as a clean item */
					mySpot.clearHistory();
					mySpot.setState(State.CLEAN);					
				}
			}
		}
	}
		
	/* PriceValues */
	public static class PriceValues implements finLink.histObject {
		private finObject.Date       theDate      = null;
		private finObject.Price      thePrice     = null;
		
		/* Access methods */
		public finObject.Date       getDate()      { return theDate; }
		public finObject.Price      getPrice()     { return thePrice; }
		
		public void setDate(finObject.Date pDate) {
			theDate      = pDate; }
		public void setPrice(finObject.Price pPrice) {
			thePrice     = pPrice; }

		/* Constructor */
		public PriceValues() {}
		public PriceValues(PriceValues pValues) {
			theDate      = pValues.getDate();
			thePrice     = pValues.getPrice();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			PriceValues myValues = (PriceValues)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(PriceValues pValues) {
			if (finObject.differs(theDate,    pValues.theDate))    return false;
			if (finObject.differs(thePrice,   pValues.thePrice))   return false;
			return true;
		}
		
		/* Copy values */
		public void    copyFrom(histObject pSource) {
			PriceValues myValues = (PriceValues)pSource;
			copyFrom(myValues);
		}
		public histObject copySelf() {
			return new PriceValues(this);
		}
		public void    copyFrom(PriceValues pValues) {
			theDate      = pValues.getDate();
			thePrice     = pValues.getPrice();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			PriceValues 	pValues = (PriceValues)pOriginal;
			boolean			bResult = false;
			switch (fieldNo) {
				case Price.FIELD_DATE:
					bResult = (finObject.differs(theDate,      pValues.theDate));
					break;
				case Price.FIELD_PRICE:
					bResult = (finObject.differs(thePrice,     pValues.thePrice));
					break;
			}
			return bResult;
		}
	}
		
	public class Price extends	  finLink.itemElement
					   implements finSwing.tableElement {
		/* Local values */
		private long 	theAccountId	= -1;
		private Account	theAccount		= null;
		
		/* Access methods */
		public  PriceValues     getObj()       { return (PriceValues)super.getObj(); }
		public  finObject.Price getPrice()     { return getObj().getPrice(); }
		public  finObject.Date  getDate()      { return getObj().getDate(); }
		public  Account         getAccount()   { return theAccount; }
		private void         	setAccount(Account pAccount)   {
			theAccount = pAccount; }
	
		/* Linking methods */
		public Price     getNext() { return (Price)super.getNext(); }
		public Price     getPrev() { return (Price)super.getPrev(); }
		public Price     getBase() { return (Price)super.getBase(); }
		public PriceList getCtl()  { return (PriceList)super.getCtl(); } 
		public finSwing.tableList getList()  { 
			return (finSwing.tableList)super.getCtl(); } 
		
		/* Field IDs */
		public static final int FIELD_ID       = 0;
		public static final int FIELD_ACCOUNT  = 1;
		public static final int FIELD_DATE     = 2;
		public static final int FIELD_PRICE    = 3;
		public static final int NUMFIELDS	   = 4;
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "Price"; }
		
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
				case FIELD_ACCOUNT:		return "Account";
				case FIELD_DATE:		return "Date";
				case FIELD_PRICE:		return "Price";
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
			String 			myString = "<tr><td>" + fieldName(iField) + "</td><td>";
			PriceValues 	myObj 	 = (PriceValues)pObj;
			switch (iField) {
				case FIELD_ID: 		
					myString += getId(); 
					break;
				case FIELD_ACCOUNT:
					if ((theAccount == null) &&
						(theAccountId != -1))
						myString += "Id=" + theAccountId;
					else
						myString += finObject.formatAccount(theAccount); 
					break;
				case FIELD_DATE:	
					myString += finObject.formatDate(getDate()); 
					break;
				case FIELD_PRICE:	
					myString += finObject.formatPrice(myObj.getPrice()); 
					break;
			}
			return myString + "</td></tr>";
		}
								
		/**
	 	* Construct a copy of a Price
	 	* 
	 	* @param pPrice The Price 
	 	*/
		protected Price(PriceList pList, Price pPrice) {
			/* Set standard values */
			super(pList, pPrice.getId());
			PriceValues myObj = new PriceValues(pPrice.getObj());
			setObj(myObj);
			theAccount = pPrice.theAccount;

			/* Switch on the LinkStyle */
			switch (pList.getStyle()) {
				case CORE:
					theIdManager.setNewPrice(this);
					break;
				case EDIT:
					setBase(pPrice);
					setState(State.CLEAN);
					break;
				case UPDATE:
					setBase(pPrice);
					setState(pPrice.getState());
					break;
			}
		}
	
		/* Standard constructor for a newly inserted price */
		private Price(PriceList pList) {
			super(pList, 0);
			PriceValues myObj = new PriceValues();
			setObj(myObj);
			setState(State.NEW);
		}
	
		/* Standard constructor */
		public Price(PriceList       pList,
				     long            uId, 
					 long 		     uAccountId,
				     java.util.Date  pDate, 
				     String 		 pPrice) throws finObject.Exception {
			/* Initialise the item */
			super(pList, uId);
			
			/* Initialise the values */
			PriceValues myObj = new PriceValues();
			setObj(myObj);
			
			/* Record the Id */
			theAccountId = uAccountId;
			
			/* Look up the Account */
			theAccount = theAccounts.searchFor(uAccountId);
			if (theAccount == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.PRICE,
											  this,
											  "Invalid Account Id");
						
			/* Record the date */
			myObj.setDate(new finObject.Date(pDate));
			
			/* Record the price */
			finObject.Price myPrice = finObject.Price.Parse(pPrice);
			if (myPrice == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.PRICE,
											  this,
											  "Invalid Price: " + pPrice);
			myObj.setPrice(myPrice);
			
			
			/* Allocate the id */
			theIdManager.setNewPrice(this);
		}
	
		/**
		 * Compare this price to another to establish equality.
		 * 
		 * @param that The Price to compare to
		 * @return <code>true</code> if the tax year is identical, <code>false</code> otherwise
		 */
		public boolean equals(finLink.itemElement that) {
			Price myPrice = (Price)that;
			if (this == that) return true;
			if (getId() != myPrice.getId()) return false;
			if (finObject.differs(getAccount(),	myPrice.getAccount())) 	return false;
			if (finObject.differs(getDate(),    myPrice.getDate())) 	return false;
			if (finObject.differs(getPrice(),   myPrice.getPrice())) 	return false;
			return true;
		}

		/**
		 * Override for standard method
		 */
		protected int compareTo(Price that) {
			int iDiff;
			if (this == that) return 0;
			if (that == null) return -1;
			if (this.getDate() != that.getDate()) {
				if (this.getDate() == null) return 1;
				if (that.getDate() == null) return -1;
				iDiff = getDate().compareTo(that.getDate());
				if (iDiff != 0) return iDiff;
			}
			iDiff =(int)(getId() - that.getId());
			if (iDiff < 0) return -1;
			if (iDiff > 0) return 1;
			return 0;
		}

		public int linkCompareTo(linkObject that) {
			Price myItem = (Price)that;
			return this.compareTo(myItem);
		}
		
		/**
		 * Validate the price
		 * 
		 */
		public void validate() {
			finObject.Date myDate = getDate();
				
			/* The date must be non-null */
			if ((myDate == null) || (myDate.isNull())) {
				addError("Null Date is not allowed", FIELD_DATE);
			}
				
			/* else date is non-null */
			else {
				/* Date must be unique for this account */
				if (getCtl().countInstances(myDate, theAccount) > 1) {
					addError("Date must be unique", FIELD_DATE);
				} 
				
				/* The date must be in-range */
				if (getDateRange().compareTo(myDate) != 0) {
					addError("Date must be within range", FIELD_DATE);
				}
			}
				
			/* The Price must be non-zero */
			if ((getPrice() == null) ||
				(!getPrice().isNonZero()) ||
				(!getPrice().isPositive())) {
				addError("Price must be non-Zero and positive", FIELD_PRICE);
			}						
			
			/* Set validation flag */
			if (!hasErrors()) setValidEdit();
		}
	
		/**
		 * Set a new price 
		 * 
		 * @param pPrice the price 
		 */
		protected void setPrice(finObject.Price pPrice) {
			getObj().setPrice((pPrice == null) ? null : new finObject.Price(pPrice));
		}
	
		/**
		 * Set a new date 
		 * 
		 * @param pDate the new date 
		 */
		protected void setDate(finObject.Date pDate) {
			getObj().setDate((pDate == null) ? null : new finObject.Date(pDate));
		}
	
		/**
		 * Update Price from an item Element 
		 * 
		 * @param pItem the price extract 
		 */
		public void applyChanges(finLink.itemElement pItem) {
			if (pItem instanceof Price) {
				Price myPrice = (Price)pItem;
				applyChanges(myPrice);
			}
			else if (pItem instanceof finView.SpotPrices.SpotPrice) {
				finView.SpotPrices.SpotPrice mySpot = (finView.SpotPrices.SpotPrice)pItem;
				applyChanges(mySpot);
			}
		}
		
		/**
		 * Update Price from a Price extract 
		 * 
		 * @param pPrice the price extract 
		 */
		public void applyChanges(Price pPrice) {
			/* Store the current detail into history */
			pushHistory();
			
			/* Update the price if required */
			if (finObject.differs(getPrice(), pPrice.getPrice())) 
				setPrice(pPrice.getPrice());
		
			/* Update the date if required */
			if (finObject.differs(getDate(), pPrice.getDate())) 
				setDate(pPrice.getDate());
			
			/* Check for changes */
			if (checkForHistory()) setState(State.CHANGED);
		}
		
		/**
		 * Update Price from a Price extract 
		 * 
		 * @param pPrice the price extract 
		 */
		public void applyChanges(finView.SpotPrices.SpotPrice pPrice) {			
			/* If we are setting a null price */
			if (pPrice == null) {
				/* We are actually deleting the price */
				setState(State.DELETED);
			}
			
			/* else we have a price to set */
			else {
				/* Store the current detail into history */
				pushHistory();
			
				/* Update the price if required */
				if (finObject.differs(getPrice(), pPrice.getPrice())) 
					setPrice(pPrice.getPrice());
		
				/* Check for changes */
				if (checkForHistory()) setState(State.CHANGED);
			}
		}
	}
	
	/* The Tax Parms List class */
	public class TaxParmList extends finLink.itemCtl {
		/* Linking methods */
		public TaxParms getFirst() { return (TaxParms)super.getFirst(); }
		public TaxParms getLast()  { return (TaxParms)super.getLast(); }
		public TaxParms searchFor(long uId) {
			return (TaxParms)super.searchFor(uId); }
		public TaxParms extractItemAt(long uIndex) {
			return (TaxParms)super.extractItemAt(uIndex); }
		
	 	/** 
	 	 * Construct an empty CORE TaxYear list
	 	 */
		protected TaxParmList() { super(ListStyle.CORE, false); }

		/** 
	 	 * Construct an empty generic TaxYear list
	 	 * @param pStyle the style of the list 
	 	 */
		protected TaxParmList(ListStyle pStyle) { super(pStyle, false); }

		/** 
	 	 * Construct a generic TaxYear list
	 	 * @param pList the source TaxYear list 
	 	 * @param pStyle the style of the list 
	 	 */
		protected TaxParmList(TaxParmList pList, ListStyle pStyle) { super(pList, pStyle); }

		/** 
	 	 * Construct a difference TaxYear list
	 	 * @param pNew the new TaxYear list 
	 	 * @param pOld the old TaxYear list 
	 	 */
		protected TaxParmList(TaxParmList pNew, TaxParmList pOld) { super(pNew, pOld); }
	
		/** 
	 	 * Clone a TaxYear list
	 	 * @return the cloned list
	 	 */
		protected TaxParmList cloneIt() { return new TaxParmList(this, ListStyle.CORE); }
		
		/**
		 * Add a new item to the core list
		 * 
		 * @param pTaxYear item
		 * @return the newly added item
		 */
		public finLink.itemElement addNewItem(finLink.itemElement pTaxYear) {
			TaxParms myParms = new TaxParms(this, (TaxParms)pTaxYear);
			myParms.addToList();
			return myParms;
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "TaxYear"; }
				
		/**
		 * Search for the tax year that encompasses this date
		 * 
		 * @param pDate Date of item
		 * @return The TaxYear if present (or null)
		 */
		public TaxParms searchFor(finObject.Date pDate) {
			TaxParms   		myCurr;
			finObject.Range	myRange;
			int        	iDiff;
			
			/* Loop through the items to find the insert point */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* Access the range for this tax year */
				myRange = myCurr.getRange();
				
				/* Determine whether the date is owned by the tax year */
				iDiff = myRange.compareTo(pDate);
				if (iDiff == 0) break;
			}
			
			/* Return to caller */
			return myCurr;
		}
		
		/**
		 * Search for a particular tax year by year string
		 * 
		 * @param pYear Date of item
		 * @return The TaxYear if present (or null)
		 */
		public TaxParms searchFor(String pYear) {
			TaxParms   myCurr;
			boolean    isMatch;
			long	   uYear;
			
			/* Access the search year */
			uYear = Long.parseLong(pYear);
			
			/* Loop through the items to find the insert point */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				isMatch = (uYear == (long)myCurr.getDate().getYear());
				if (isMatch) break;
			}
			
			/* Return to caller */
			return myCurr;
		}
		
		/**
		 * Count the instances of a date
		 * 
		 * @param pDate the date
		 * @return The Item if present (or null)
		 */
		protected int countInstances(finObject.Date pDate) {
			TaxParms myCurr;
			int      iDiff;
			int      iCount = 0;
			
			/* Loop through the items to find the entry */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				iDiff = pDate.compareTo(myCurr.getDate());
				if (iDiff == 0) iCount++;
			}
			
			/* Return to caller */
			return iCount;
		}	
		
		/**
		 * Reset the active flags after changes to events
		 */
		public void reset() {
			TaxParms myCurr;
			
			/* Loop through the items */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* Clear the flags */
				myCurr.isActive = false;
			}
		}
		
		/**
		 * Extract the date range represented by the tax years
		 * 
		 * @return the range of tax years
		 */
		public finObject.Range getRange() {
			TaxParms        myCurr;
			finObject.Date  myStart;
			finObject.Date  myEnd;
			finObject.Range myRange;
			
			/* Extract the first item */
			myCurr = getFirst();
			if (myCurr == null)	{
				/* Set null values */
				myStart = null;
				myEnd   = null;
			}
			
			/* else we have a tax year */
			else {
				/* Access start date */
				myStart =  new finObject.Date(myCurr.getDate());
			
				/* Move back to start of year */
				myStart.adjustYear(-1);
				myStart.adjustDay(1);
			
				/* Extract the last item */
				myCurr = getLast();
				myEnd  = myCurr.getDate();
			}
			
			/* Create the range */
			myRange = new finObject.Range(myStart, myEnd);
			
			/* Return the range */
			return myRange;
		}
	}

	/* TaxParmValues */
	public static class TaxParmValues implements finLink.histObject {
		private finStatic.TaxRegime theTaxRegime	 = null;
		private finObject.Money 	theAllowance     = null;
		private finObject.Money 	theRentalAllow   = null;
		private finObject.Money 	theLoAgeAllow    = null;
		private finObject.Money 	theHiAgeAllow    = null;
		private finObject.Money 	theCapitalAllow  = null;
		private finObject.Money 	theLoBand		 = null;
		private finObject.Money 	theBasicBand     = null;
		private finObject.Money 	theAgeAllowLimit = null;
		private finObject.Money 	theAddAllowLimit = null;
		private finObject.Money 	theAddIncBound	 = null;
		private finObject.Rate  	theLoTaxRate     = null;
		private finObject.Rate  	theBasicTaxRate  = null;
		private finObject.Rate  	theHiTaxRate     = null;
		private finObject.Rate  	theIntTaxRate    = null;
		private finObject.Rate  	theDivTaxRate    = null;
		private finObject.Rate  	theHiDivTaxRate  = null;
		private finObject.Rate  	theAddTaxRate 	 = null;
		private finObject.Rate  	theAddDivTaxRate = null;
		private finObject.Rate  	theCapTaxRate 	 = null;
		private finObject.Rate  	theHiCapTaxRate  = null;
		
		/* Access methods */
		public finStatic.TaxRegime 	getTaxRegime()		{ return theTaxRegime; }
		public finObject.Money 		getAllowance()		{ return theAllowance; }
		public finObject.Money		getRentalAllow()  	{ return theRentalAllow; }
		public finObject.Money		getLoBand()       	{ return theLoBand; }
		public finObject.Money 		getBasicBand()    	{ return theBasicBand; }
		public finObject.Money 		getCapitalAllow()   { return theCapitalAllow; }
		public finObject.Money 		getLoAgeAllow()     { return theLoAgeAllow; }
		public finObject.Money 		getHiAgeAllow()     { return theHiAgeAllow; }
		public finObject.Money		getAgeAllowLimit()  { return theAgeAllowLimit; }
		public finObject.Money		getAddAllowLimit()  { return theAddAllowLimit; }
		public finObject.Money		getAddIncBound()  	{ return theAddIncBound; }
		public finObject.Rate  		getLoTaxRate()    	{ return theLoTaxRate; }
		public finObject.Rate  		getBasicTaxRate()   { return theBasicTaxRate; }
		public finObject.Rate  		getHiTaxRate()    	{ return theHiTaxRate; }
		public finObject.Rate  		getIntTaxRate()   	{ return theIntTaxRate; }
		public finObject.Rate  		getDivTaxRate()   	{ return theDivTaxRate; }
		public finObject.Rate  		getHiDivTaxRate() 	{ return theHiDivTaxRate; }
		public finObject.Rate  		getAddTaxRate() 	{ return theAddTaxRate; }
		public finObject.Rate  		getAddDivTaxRate() 	{ return theAddDivTaxRate; }
		public finObject.Rate  		getCapTaxRate() 	{ return theCapTaxRate; }
		public finObject.Rate  		getHiCapTaxRate() 	{ return theHiCapTaxRate; }
		
		public void setTaxRegime(finStatic.TaxRegime pTaxRegime) {
			theTaxRegime    = pTaxRegime; }
		public void setAllowance(finObject.Money pAllowance) {
			theAllowance    = pAllowance; }
		public void setRentalAllow(finObject.Money pAllowance) {
			theRentalAllow  = pAllowance; }
		public void setLoBand(finObject.Money pLoTaxBand) {
			theLoBand       = pLoTaxBand; }
		public void setBasicBand(finObject.Money pBasicTaxBand) {
			theBasicBand    = pBasicTaxBand; }
		public void setCapitalAllow(finObject.Money pAllowance) {
			theCapitalAllow = pAllowance; }
		public void setLoAgeAllow(finObject.Money pAllowance) {
			theLoAgeAllow   = pAllowance; }
		public void setHiAgeAllow(finObject.Money pAllowance) {
			theHiAgeAllow   = pAllowance; }
		public void setAgeAllowLimit(finObject.Money pLimit) {
			theAgeAllowLimit = pLimit; }
		public void setAddAllowLimit(finObject.Money pLimit) {
			theAddAllowLimit = pLimit; }
		public void setAddIncBound(finObject.Money pBound) {
			theAddIncBound	= pBound; }
		public void setLoTaxRate(finObject.Rate pLoTaxRate) {
			theLoTaxRate    = pLoTaxRate; }
		public void setBasicTaxRate(finObject.Rate pBasicTaxRate) {
			theBasicTaxRate   = pBasicTaxRate; }
		public void setHiTaxRate(finObject.Rate pHiTaxRate) {
			theHiTaxRate    = pHiTaxRate; }
		public void setIntTaxRate(finObject.Rate pIntTaxRate) {
			theIntTaxRate   = pIntTaxRate; }
		public void setDivTaxRate(finObject.Rate pDivTaxRate) {
			theDivTaxRate   = pDivTaxRate; }
		public void setHiDivTaxRate(finObject.Rate pHiDivTaxRate) {
			theHiDivTaxRate = pHiDivTaxRate; }
		public void setAddTaxRate(finObject.Rate pAddTaxRate) {
			theAddTaxRate = pAddTaxRate; }
		public void setAddDivTaxRate(finObject.Rate pAddDivTaxRate) {
			theAddDivTaxRate = pAddDivTaxRate; }
		public void setCapTaxRate(finObject.Rate pCapTaxRate) {
			theCapTaxRate = pCapTaxRate; }
		public void setHiCapTaxRate(finObject.Rate pHiCapTaxRate) {
			theHiCapTaxRate = pHiCapTaxRate; }

		/* Constructor */
		public TaxParmValues() {}
		public TaxParmValues(TaxParmValues pValues) {
			theTaxRegime     = pValues.getTaxRegime();
			theAllowance     = pValues.getAllowance();
			theRentalAllow   = pValues.getRentalAllow();
			theLoBand        = pValues.getLoBand();
			theBasicBand     = pValues.getBasicBand();
			theCapitalAllow  = pValues.getCapitalAllow();
			theLoAgeAllow    = pValues.getLoAgeAllow();
			theHiAgeAllow    = pValues.getHiAgeAllow();
			theAgeAllowLimit = pValues.getAgeAllowLimit();
			theAddAllowLimit = pValues.getAddAllowLimit();
			theAddIncBound	 = pValues.getAddIncBound();
			theLoTaxRate     = pValues.getLoTaxRate();
			theBasicTaxRate  = pValues.getBasicTaxRate();
			theHiTaxRate     = pValues.getHiTaxRate();
			theIntTaxRate    = pValues.getIntTaxRate();
			theDivTaxRate    = pValues.getDivTaxRate();
			theHiDivTaxRate  = pValues.getHiDivTaxRate();			
			theAddTaxRate    = pValues.getAddTaxRate();			
			theAddDivTaxRate = pValues.getAddDivTaxRate();			
			theCapTaxRate    = pValues.getCapTaxRate();			
			theHiCapTaxRate  = pValues.getHiCapTaxRate();			
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			TaxParmValues myValues = (TaxParmValues)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(TaxParmValues pValues) {
			if (finObject.differs(theTaxRegime,     pValues.theTaxRegime))     return false;
			if (finObject.differs(theAllowance,     pValues.theAllowance))     return false;
			if (finObject.differs(theRentalAllow,   pValues.theRentalAllow))   return false;
			if (finObject.differs(theLoBand,        pValues.theLoBand))        return false;
			if (finObject.differs(theBasicBand,     pValues.theBasicBand))     return false;
			if (finObject.differs(theCapitalAllow,  pValues.theCapitalAllow))  return false;
			if (finObject.differs(theLoAgeAllow,    pValues.theLoAgeAllow))    return false;
			if (finObject.differs(theHiAgeAllow,    pValues.theHiAgeAllow))    return false;
			if (finObject.differs(theAgeAllowLimit, pValues.theAgeAllowLimit)) return false;
			if (finObject.differs(theAddAllowLimit, pValues.theAddAllowLimit)) return false;
			if (finObject.differs(theAddIncBound,   pValues.theAddIncBound))   return false;
			if (finObject.differs(theLoTaxRate,     pValues.theLoTaxRate))     return false;
			if (finObject.differs(theBasicTaxRate,  pValues.theBasicTaxRate))  return false;
			if (finObject.differs(theHiTaxRate,     pValues.theHiTaxRate))     return false;
			if (finObject.differs(theIntTaxRate,    pValues.theIntTaxRate))    return false;
			if (finObject.differs(theDivTaxRate,    pValues.theDivTaxRate))    return false;
			if (finObject.differs(theHiDivTaxRate,  pValues.theHiDivTaxRate))  return false;
			if (finObject.differs(theAddTaxRate,    pValues.theAddTaxRate))    return false;
			if (finObject.differs(theAddDivTaxRate, pValues.theAddDivTaxRate)) return false;
			if (finObject.differs(theCapTaxRate,    pValues.theCapTaxRate))    return false;
			if (finObject.differs(theHiCapTaxRate,  pValues.theHiCapTaxRate))  return false;
			return true;
		}
		
		/* Copy values */
		public void    copyFrom(histObject pSource) {
			TaxParmValues myValues = (TaxParmValues)pSource;
			copyFrom(myValues);
		}
		public histObject copySelf() {
			return new TaxParmValues(this);
		}
		public void    copyFrom(TaxParmValues pValues) {
			theTaxRegime     = pValues.getTaxRegime();
			theAllowance     = pValues.getAllowance();
			theRentalAllow   = pValues.getRentalAllow();
			theLoBand        = pValues.getLoBand();
			theBasicBand     = pValues.getBasicBand();
			theCapitalAllow  = pValues.getCapitalAllow();
			theLoAgeAllow    = pValues.getLoAgeAllow();
			theHiAgeAllow    = pValues.getHiAgeAllow();
			theAgeAllowLimit = pValues.getAgeAllowLimit();
			theAddAllowLimit = pValues.getAddAllowLimit();
			theAddIncBound	 = pValues.getAddIncBound();
			theLoTaxRate     = pValues.getLoTaxRate();
			theBasicTaxRate  = pValues.getBasicTaxRate();
			theHiTaxRate     = pValues.getHiTaxRate();
			theIntTaxRate    = pValues.getIntTaxRate();
			theDivTaxRate    = pValues.getDivTaxRate();
			theHiDivTaxRate  = pValues.getHiDivTaxRate();						
			theAddTaxRate    = pValues.getAddTaxRate();						
			theAddDivTaxRate = pValues.getAddDivTaxRate();						
			theCapTaxRate    = pValues.getCapTaxRate();						
			theHiCapTaxRate  = pValues.getHiCapTaxRate();						
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			TaxParmValues 	pValues = (TaxParmValues)pOriginal;
			boolean			bResult = false;
			switch (fieldNo) {
				case TaxParms.FIELD_REGIME:
					bResult = (finObject.differs(theTaxRegime,  pValues.theTaxRegime));
					break;
				case TaxParms.FIELD_ALLOW:
					bResult = (finObject.differs(theAllowance,  pValues.theAllowance));
					break;
				case TaxParms.FIELD_RENTAL:
					bResult = (finObject.differs(theRentalAllow,  pValues.theRentalAllow));
					break;
				case TaxParms.FIELD_LOBAND:
					bResult = (finObject.differs(theLoBand,       pValues.theLoBand));
					break;
				case TaxParms.FIELD_BSBAND:
					bResult = (finObject.differs(theBasicBand,    pValues.theBasicBand));
					break;
				case TaxParms.FIELD_CAPALW:
					bResult = (finObject.differs(theCapitalAllow, pValues.theCapitalAllow));
					break;
				case TaxParms.FIELD_LOAGAL:
					bResult = (finObject.differs(theLoAgeAllow, pValues.theLoAgeAllow));
					break;
				case TaxParms.FIELD_HIAGAL:
					bResult = (finObject.differs(theHiAgeAllow, pValues.theHiAgeAllow));
					break;
				case TaxParms.FIELD_AGELMT:
					bResult = (finObject.differs(theAgeAllowLimit, pValues.theAgeAllowLimit));
					break;
				case TaxParms.FIELD_ADDLMT:
					bResult = (finObject.differs(theAddAllowLimit, pValues.theAddAllowLimit));
					break;
				case TaxParms.FIELD_ADDBDY:
					bResult = (finObject.differs(theAddIncBound, pValues.theAddIncBound));
					break;
				case TaxParms.FIELD_LOTAX:
					bResult = (finObject.differs(theLoTaxRate,  pValues.theLoTaxRate));
					break;
				case TaxParms.FIELD_BASTAX:
					bResult = (finObject.differs(theBasicTaxRate, pValues.theBasicTaxRate));
					break;
				case TaxParms.FIELD_HITAX:
					bResult = (finObject.differs(theHiTaxRate,  pValues.theHiTaxRate));
					break;
				case TaxParms.FIELD_INTTAX:
					bResult = (finObject.differs(theIntTaxRate, pValues.theIntTaxRate));
					break;
				case TaxParms.FIELD_DIVTAX:
					bResult = (finObject.differs(theDivTaxRate, pValues.theDivTaxRate));
					break;
				case TaxParms.FIELD_HDVTAX:
					bResult = (finObject.differs(theHiDivTaxRate, pValues.theHiDivTaxRate));
					break;
				case TaxParms.FIELD_ADDTAX:
					bResult = (finObject.differs(theAddTaxRate, pValues.theAddTaxRate));
					break;
				case TaxParms.FIELD_ADVTAX:
					bResult = (finObject.differs(theAddDivTaxRate, pValues.theAddDivTaxRate));
					break;
				case TaxParms.FIELD_CAPTAX:
					bResult = (finObject.differs(theCapTaxRate, pValues.theCapTaxRate));
					break;
				case TaxParms.FIELD_HCPTAX:
					bResult = (finObject.differs(theHiCapTaxRate, pValues.theHiCapTaxRate));
					break;
			}
			return bResult;
		}
	}
	
	/* TaxParms class */
	public class TaxParms extends finLink.itemElement {
		/* Members */
		private finObject.Date  theDate         = null;
		private long			theRegimeId		= -1;
		private boolean			isActive		= false;

		/* Access methods */
		public  finObject.Date  getDate()         { return theDate; }	
		public  TaxParmValues   getObj()          { return (TaxParmValues)super.getObj(); }	
		public  finStatic.TaxRegime getTaxRegime(){ return getObj().getTaxRegime(); }
		public  finObject.Money getAllowance()    { return getObj().getAllowance(); }
		public  finObject.Money getRentalAllowance() { return getObj().getRentalAllow(); }
		public  finObject.Money getLoBand()       { return getObj().getLoBand(); }
		public  finObject.Money getBasicBand()    { return getObj().getBasicBand(); }
		public  finObject.Money getCapitalAllow() { return getObj().getCapitalAllow(); }
		public  finObject.Money getLoAgeAllow()   { return getObj().getLoAgeAllow(); }
		public  finObject.Money getHiAgeAllow()   { return getObj().getHiAgeAllow(); }
		public  finObject.Money getAgeAllowLimit(){ return getObj().getAgeAllowLimit(); }
		public  finObject.Money getAddAllowLimit(){ return getObj().getAddAllowLimit(); }
		public  finObject.Money getAddIncBound()  { return getObj().getAddIncBound(); }
		public  finObject.Rate  getLoTaxRate()    { return getObj().getLoTaxRate(); }
		public  finObject.Rate  getBasicTaxRate() { return getObj().getBasicTaxRate(); }
		public  finObject.Rate  getHiTaxRate()    { return getObj().getHiTaxRate(); }
		public  finObject.Rate  getIntTaxRate()   { return getObj().getIntTaxRate(); }
		public  finObject.Rate  getDivTaxRate()   { return getObj().getDivTaxRate(); }
		public  finObject.Rate  getHiDivTaxRate() { return getObj().getHiDivTaxRate(); }
		public  finObject.Rate  getAddTaxRate()   { return getObj().getAddTaxRate(); }
		public  finObject.Rate  getAddDivTaxRate(){ return getObj().getAddDivTaxRate(); }
		public  finObject.Rate  getCapTaxRate()   { return getObj().getCapTaxRate(); }
		public  finObject.Rate  getHiCapTaxRate() { return getObj().getHiCapTaxRate(); }
		public  boolean			isActive() 	  	  { return isActive; }
		public  boolean         hasLoSalaryBand() 			{ return getTaxRegime().hasLoSalaryBand(); }
		public  boolean         hasAdditionalTaxBand() 		{ return getTaxRegime().hasAdditionalTaxBand(); }
		public  boolean         hasCapitalGainsAsIncome() 	{ return getTaxRegime().hasCapitalGainsAsIncome(); }
		public  void            setDate(finObject.Date pDate) { theDate = pDate; }

		/* Linking methods */
		public TaxParms    getNext() { return (TaxParms)super.getNext(); }
		public TaxParms    getPrev() { return (TaxParms)super.getPrev(); }
		public TaxParms    getBase() { return (TaxParms)super.getBase(); }
		public TaxParmList getCtl()  { return (TaxParmList)super.getCtl(); } 
		
		/* Field IDs */
		public static final int FIELD_ID     		= 0;
		public static final int FIELD_REGIME  		= 1;
		public static final int FIELD_DATE   		= 2;
		public static final int FIELD_RENTAL 		= 3;
		public static final int FIELD_ALLOW  		= 4;
		public static final int FIELD_LOAGAL 		= 5;
		public static final int FIELD_HIAGAL 		= 6;
		public static final int FIELD_LOBAND 		= 7;
		public static final int FIELD_BSBAND 		= 8;
		public static final int FIELD_CAPALW 		= 9;
		public static final int FIELD_AGELMT 		= 10;
		public static final int FIELD_ADDLMT 		= 11;
		public static final int FIELD_ADDBDY 		= 12;
		public static final int FIELD_LOTAX  		= 13;
		public static final int FIELD_BASTAX 		= 14;
		public static final int FIELD_HITAX  		= 15;
		public static final int FIELD_INTTAX 		= 16;
		public static final int FIELD_DIVTAX 		= 17;
		public static final int FIELD_HDVTAX 		= 18;
		public static final int FIELD_ADDTAX 		= 19;
		public static final int FIELD_ADVTAX 		= 20;
		public static final int FIELD_CAPTAX 		= 21;
		public static final int FIELD_HCPTAX 		= 22;
		public static final int NUMFIELDS	    	= 23;
		
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "TaxYear"; }
		
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
				case FIELD_ID:		return "ID";
				case FIELD_REGIME:	return "Regime";
				case FIELD_DATE:	return "Date";
				case FIELD_ALLOW:	return "Allowance";
				case FIELD_LOBAND:	return "LowTaxBand";
				case FIELD_BSBAND:	return "BasicTaxBand";
				case FIELD_RENTAL:	return "RentalAllowance";
				case FIELD_CAPALW:	return "CapitalAllowance";
				case FIELD_LOTAX:	return "LowTaxRate";
				case FIELD_BASTAX:	return "BasicTaxRate";
				case FIELD_HITAX:	return "HighTaxRate";
				case FIELD_INTTAX:	return "InterestTaxRate";
				case FIELD_DIVTAX:	return "DividendTaxRate";
				case FIELD_HDVTAX:	return "HighDividendTaxRate";
				case FIELD_ADDTAX:	return "AdditionalTaxRate";
				case FIELD_ADVTAX:	return "AdditionalDivTaxRate";
				case FIELD_CAPTAX:	return "CapitalTaxRate";
				case FIELD_HCPTAX:	return "HiCapitalTaxRate";
				case FIELD_LOAGAL:	return "LowAgeAllowance";
				case FIELD_HIAGAL:	return "HighAgeAllowance";
				case FIELD_AGELMT:	return "AgeAllowanceLimit";
				case FIELD_ADDLMT:	return "AdditionalAllowanceLimit";
				case FIELD_ADDBDY:	return "AdditionalIncomeBoundary";
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
			String 			myString = "<tr><td>" + fieldName(iField) + "</td><td>";
			TaxParmValues 	myObj 	 = (TaxParmValues)pObj;
			switch (iField) {
				case FIELD_ID: 		
					myString += getId(); 
					break;
				case FIELD_REGIME:
					if ((myObj.getTaxRegime() == null) &&
						(theRegimeId != -1))
						myString += "Id=" + theRegimeId;
					else
						myString += finObject.formatRegime(myObj.getTaxRegime()); 
					break;
				case FIELD_DATE:	
					myString += finObject.formatDate(getDate()); 
					break;
				case FIELD_ALLOW:	
					myString += finObject.formatMoney(myObj.getAllowance()); 
					break;
				case FIELD_LOBAND: 	
					myString += finObject.formatMoney(myObj.getLoBand());	
					break;
				case FIELD_BSBAND:	
					myString += finObject.formatMoney(myObj.getBasicBand()); 
					break;
				case FIELD_RENTAL:	
					myString += finObject.formatMoney(myObj.getRentalAllow()); 
					break;
				case FIELD_LOTAX: 	
					myString += finObject.formatRate(myObj.getLoTaxRate());	
					break;
				case FIELD_BASTAX: 	
					myString += finObject.formatRate(myObj.getBasicTaxRate());	
					break;
				case FIELD_HITAX: 	
					myString += finObject.formatRate(myObj.getHiTaxRate());	
					break;
				case FIELD_INTTAX: 	
					myString += finObject.formatRate(myObj.getIntTaxRate());	
					break;
				case FIELD_DIVTAX: 	
					myString += finObject.formatRate(myObj.getDivTaxRate());	
					break;
				case FIELD_HDVTAX: 	
					myString += finObject.formatRate(myObj.getHiDivTaxRate());	
					break;
				case FIELD_ADDTAX: 	
					myString += finObject.formatRate(myObj.getAddTaxRate());	
					break;
				case FIELD_ADVTAX: 	
					myString += finObject.formatRate(myObj.getAddDivTaxRate());	
					break;
				case FIELD_LOAGAL: 	
					myString += finObject.formatMoney(myObj.getLoAgeAllow());	
					break;
				case FIELD_HIAGAL: 	
					myString += finObject.formatMoney(myObj.getHiAgeAllow());	
					break;
				case FIELD_AGELMT: 	
					myString += finObject.formatMoney(myObj.getAgeAllowLimit());	
					break;
				case FIELD_ADDLMT: 	
					myString += finObject.formatMoney(myObj.getAddAllowLimit());	
					break;
				case FIELD_ADDBDY: 	
					myString += finObject.formatMoney(myObj.getAddIncBound());	
					break;
				case FIELD_CAPALW: 	
					myString += finObject.formatMoney(myObj.getCapitalAllow());	
					break;
				case FIELD_CAPTAX: 	
					myString += finObject.formatRate(myObj.getCapTaxRate());	
					break;
				case FIELD_HCPTAX: 	
					myString += finObject.formatRate(myObj.getHiCapTaxRate());	
					break;
			}
			return myString + "</td></tr>";
		}
								
		/**
		 * Construct a copy of a TaxParams
		 * 
		 * @param pList The List to build into 
		 * @param pTaxParms The TaxParams to copy 
		 */
		protected TaxParms(TaxParmList pList, TaxParms pTaxParms) { 
			super(pList, pTaxParms.getId());
			theDate  = pTaxParms.getDate();
			isActive = pTaxParms.isActive();
			TaxParmValues myObj  = new TaxParmValues(pTaxParms.getObj());
			setObj(myObj);
			
			/* Switch on the ListStyle */
			switch (pList.getStyle()) {
				case CORE:
					theIdManager.setNewTaxParm(this);
					break;
				case EDIT:
					setBase(pTaxParms);
					setState(State.CLEAN);
					break;
				case UPDATE:
					setBase(pTaxParms);
					setState(pTaxParms.getState());
					break;
			}
		}
		
		/* Standard constructor */
		public TaxParms(TaxParmList     pList,
				        long            uId,
				        long			uRegimeId,
				        java.util.Date  pDate,
				        String 			pAllowance,
				        String 			pRentalAllow,
				        String 			pLoAgeAllow,
				        String 			pHiAgeAllow,
				        String			pCapAllow,
				        String 			pAgeAllowLimit,
				        String 			pAddAllowLimit,
				        String 			pLoTaxBand,
				        String 			pBasicTaxBand,
				        String 			pAddIncBound,
				        String			pLoTaxRate,
				        String			pBasicTaxRate,
				        String			pHiTaxRate,
				        String			pIntTaxRate,
				        String			pDivTaxRate,
				        String			pHiDivTaxRate,
						String			pAddTaxRate,
				        String			pAddDivTaxRate,
						String			pCapTaxRate,
						String			pHiCapTaxRate) throws finObject.Exception {
			/* Initialise item */
			super(pList, uId);
			
			/* Local variable */
			finStatic.TaxRegime myRegime;
			
			/* Initialise values */
			theDate             = new finObject.Date(pDate);
			TaxParmValues myObj = new TaxParmValues();
			setObj(myObj);

			/* Record the Id */
			theRegimeId = uRegimeId;
			
			/* Look up the Regime */
			myRegime = theTaxRegimes.searchFor(uRegimeId);
			if (myRegime == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.TAXPARAMS,
											  this,
											  "Invalid Tax Regime Id");
			myObj.setTaxRegime(myRegime);
						
			/* Record the allowances */
			finObject.Money myMoney = finObject.Money.Parse(pAllowance);
			if (myMoney == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.TAXPARAMS,
											  this,
											  "Invalid Allowance: " + pAllowance);
			myObj.setAllowance(myMoney);
			myMoney = finObject.Money.Parse(pLoTaxBand);
			if (myMoney == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.TAXPARAMS,
											  this,
											  "Invalid Low Tax Band: " + pLoTaxBand);
			myObj.setLoBand(myMoney);
			myMoney = finObject.Money.Parse(pBasicTaxBand);
			if (myMoney == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.TAXPARAMS,
											  this,
											  "Invalid Basic Tax Band: " + pBasicTaxBand);
			myObj.setBasicBand(myMoney);
			myMoney = finObject.Money.Parse(pRentalAllow);
			if (myMoney == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.TAXPARAMS,
											  this,
											  "Invalid Rental Allowance: " + pRentalAllow);
			myObj.setRentalAllow(myMoney);
			myMoney = finObject.Money.Parse(pLoAgeAllow);
			if (myMoney == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
										      ObjectClass.TAXPARAMS,
										      this,
										      "Invalid Low Age Allowance: " + pLoAgeAllow);
			myObj.setLoAgeAllow(myMoney);	
			myMoney = finObject.Money.Parse(pHiAgeAllow);
			if (myMoney == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
										      ObjectClass.TAXPARAMS,
										      this,
										      "Invalid High Age Allowance: " + pHiAgeAllow);
			myObj.setHiAgeAllow(myMoney);	
			myMoney = finObject.Money.Parse(pCapAllow);
			if (myMoney == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
										      ObjectClass.TAXPARAMS,
										      this,
										      "Invalid Capital Allowance: " + pHiAgeAllow);
			myObj.setCapitalAllow(myMoney);	
			myMoney = finObject.Money.Parse(pAgeAllowLimit);
			if (myMoney == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
										      ObjectClass.TAXPARAMS,
										      this,
										      "Invalid Age Allowance Limit: " + pAgeAllowLimit);
			myObj.setAgeAllowLimit(myMoney);	
			if (pAddAllowLimit != null) {
				myMoney = finObject.Money.Parse(pAddAllowLimit);
				if (myMoney == null) 
					throw new finObject.Exception(ExceptionClass.DATA,
											      ObjectClass.TAXPARAMS,
											      this,
											      "Invalid Additional Allowance Limit: " + pAddAllowLimit);
				myObj.setAddAllowLimit(myMoney);	
			}
			if (pAddIncBound != null) {
				myMoney = finObject.Money.Parse(pAddIncBound);
				if (myMoney == null) 
					throw new finObject.Exception(ExceptionClass.DATA,
											      ObjectClass.TAXPARAMS,
											      this,
											      "Invalid Additional Income Boundary: " + pAddIncBound);
				myObj.setAddIncBound(myMoney);	
			}

			/* Record the rates */
			finObject.Rate myRate = finObject.Rate.Parse(pLoTaxRate);
			if (myRate == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.TAXPARAMS,
											  this,
											  "Invalid Low Tax Rate: " + pLoTaxRate);
			myObj.setLoTaxRate(myRate);
			myRate = finObject.Rate.Parse(pBasicTaxRate);
			if (myRate == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.TAXPARAMS,
											  this,
											  "Invalid Basic Tax Rate: " + pBasicTaxRate);
			myObj.setBasicTaxRate(myRate);
			myRate = finObject.Rate.Parse(pHiTaxRate);
			if (myRate == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.TAXPARAMS,
											  this,
											  "Invalid High Tax Rate: " + pHiTaxRate);
			myObj.setHiTaxRate(myRate);
			myRate = finObject.Rate.Parse(pIntTaxRate);
			if (myRate == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.TAXPARAMS,
											  this,
											  "Invalid Int Tax Rate: " + pIntTaxRate);
			myObj.setIntTaxRate(myRate);
			myRate = finObject.Rate.Parse(pDivTaxRate);
			if (myRate == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.TAXPARAMS,
											  this,
											  "Invalid Div Tax Rate: " + pDivTaxRate);
			myObj.setDivTaxRate(myRate);
			myRate = finObject.Rate.Parse(pHiDivTaxRate);
			if (myRate == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  ObjectClass.TAXPARAMS,
											  this,
											  "Invalid High Div Tax Rate: " + pHiDivTaxRate);
			myObj.setHiDivTaxRate(myRate);
			if (pAddTaxRate != null) {
				myRate = finObject.Rate.Parse(pAddTaxRate);
				if (myRate == null) 
					throw new finObject.Exception(ExceptionClass.DATA,
											  	  ObjectClass.TAXPARAMS,
											  	  this,
											  	  "Invalid Additional Tax Rate: " + pAddTaxRate);
				myObj.setAddTaxRate(myRate);
			}
			if (pAddDivTaxRate != null) {
				myRate = finObject.Rate.Parse(pAddDivTaxRate);
				if (myRate == null) 
					throw new finObject.Exception(ExceptionClass.DATA,
											  	  ObjectClass.TAXPARAMS,
											  	  this,
											  	  "Invalid Additional Div Tax Rate: " + pAddDivTaxRate);
				myObj.setAddDivTaxRate(myRate);
			}
			if (pCapTaxRate != null) {
				myRate = finObject.Rate.Parse(pCapTaxRate);
				if (myRate == null) 
					throw new finObject.Exception(ExceptionClass.DATA,
											  	  ObjectClass.TAXPARAMS,
											  	  this,
											  	  "Invalid Capital Gains Tax Rate: " + pCapTaxRate);
				myObj.setCapTaxRate(myRate);
			}
			if (pHiCapTaxRate != null) {
				myRate = finObject.Rate.Parse(pHiCapTaxRate);
				if (myRate == null) 
					throw new finObject.Exception(ExceptionClass.DATA,
											  	  ObjectClass.TAXPARAMS,
											  	  this,
											  	  "Invalid High Capital Gains Tax Rate: " + pHiCapTaxRate);
				myObj.setHiCapTaxRate(myRate);
			}
			
			/* Allocate the id */
			theIdManager.setNewTaxParm(this);
		}
			
		/* Standard constructor for a newly inserted account */
		public TaxParms(TaxParmList pList) {
			super(pList, 0);
			TaxParmValues theObj = new TaxParmValues();
			setObj(theObj);
			setState(State.NEW);
		}

		/**
		 * Compare this tax year to another to establish equality.
		 * 
		 * @param that The Tax Year to compare to
		 * @return <code>true</code> if the tax year is identical, <code>false</code> otherwise
		 */
		public boolean equals(finLink.itemElement that) {
			TaxParms myYear = (TaxParms)that;
			if (this == that) return true;
			if (getId() != myYear.getId()) return false;
			if (finObject.differs(getDate(),            myYear.getDate())) 				return false;
			if (finObject.differs(getTaxRegime(),       myYear.getTaxRegime())) 		return false;
			if (finObject.differs(getAllowance(),       myYear.getAllowance())) 		return false;
			if (finObject.differs(getLoBand(),          myYear.getLoBand())) 			return false;
			if (finObject.differs(getBasicBand(),       myYear.getBasicBand())) 		return false;
			if (finObject.differs(getRentalAllowance(), myYear.getRentalAllowance())) 	return false;
			if (finObject.differs(getCapitalAllow(),    myYear.getCapitalAllow())) 		return false;
			if (finObject.differs(getLoAgeAllow(), 		myYear.getLoAgeAllow()))	 	return false;
			if (finObject.differs(getHiAgeAllow(), 		myYear.getHiAgeAllow()))	 	return false;
			if (finObject.differs(getAgeAllowLimit(), 	myYear.getAgeAllowLimit()))	 	return false;
			if (finObject.differs(getAddAllowLimit(), 	myYear.getAddAllowLimit()))	 	return false;
			if (finObject.differs(getAddIncBound(), 	myYear.getAddIncBound()))	 	return false;
			if (finObject.differs(getLoTaxRate(),       myYear.getLoTaxRate())) 		return false;
			if (finObject.differs(getBasicTaxRate(),    myYear.getBasicTaxRate())) 		return false;
			if (finObject.differs(getHiTaxRate(),       myYear.getHiTaxRate())) 		return false;
			if (finObject.differs(getIntTaxRate(),      myYear.getIntTaxRate())) 		return false;
			if (finObject.differs(getDivTaxRate(),      myYear.getDivTaxRate())) 		return false;
			if (finObject.differs(getHiDivTaxRate(),    myYear.getHiDivTaxRate()))    	return false;
			if (finObject.differs(getAddTaxRate(),      myYear.getAddTaxRate()))    	return false;
			if (finObject.differs(getAddDivTaxRate(),   myYear.getAddDivTaxRate()))    	return false;
			if (finObject.differs(getCapTaxRate(),      myYear.getCapTaxRate()))    	return false;
			if (finObject.differs(getHiCapTaxRate(),    myYear.getHiCapTaxRate()))    	return false;
			return true;
		}

		/**
		 * Override for standard method
		 */
		protected int compareTo(TaxParms that) {
			int iDiff;
			if (this == that) return 0;
			if (that == null) return -1;
			if (this.getDate() != that.getDate()) {
				if (this.theDate == null) return 1;
				if (that.theDate == null) return -1;
				iDiff = theDate.compareTo(that.theDate);
				if (iDiff != 0) return iDiff;
			}
			iDiff = (int)(getId() - that.getId());
			if (iDiff < 0) return -1;
			if (iDiff > 0) return 1;
			return 0;
		}

		public int linkCompareTo(linkObject that) {
			TaxParms myItem = (TaxParms)that;
			return this.compareTo(myItem);
		}
		
		/**
		 * Validate the taxYear
		 */
		protected void validate() {
			finObject.Date myDate = getDate();
					
			/* The date must not be null */
			if ((myDate == null) || (myDate.isNull())) 
				addError("Null date is not allowed", FIELD_DATE);
				
			/* else we have a date */
			else {
				/* The date must be unique */
				if (getCtl().countInstances(myDate) > 1) 
					addError("Date must be unique", FIELD_DATE);
				
				/* The day and month must be 5th April */
				if ((myDate.getDay() != 5) || 
					(myDate.getMonth() != Calendar.APRIL)) 
					addError("Date must be 5th April", FIELD_DATE);
				
				/* The year must be one greater than the preceding element */
				if ((getPrev() != null) &&
				    (myDate.getYear() != getPrev().getDate().getYear()+1)) 
					addError("There can be no gaps in the list", FIELD_DATE);
			}
				
			/* TaxRegime must be non-null */
			if (getTaxRegime() == null) 
				addError("TaxRegime must be non-null", FIELD_REGIME);
				
			/* The allowance must be non-null */
			if ((getAllowance() == null) || (!getAllowance().isPositive()))
				addError("Value must be positive", FIELD_ALLOW);
			
			/* The rental allowance must be non-null */
			if ((getRentalAllowance() == null) || (!getRentalAllowance().isPositive()))
				addError("Value must be positive", FIELD_RENTAL);
			
			/* The loAgeAllow must be non-null */
			if ((getLoAgeAllow() == null) || (!getLoAgeAllow().isPositive()))
				addError("Value must be positive", FIELD_LOAGAL);
			
			/* The loAgeAllow must be greater than Allowance */
			if ((getLoAgeAllow() != null) && (getAllowance() != null) &&
				(getLoAgeAllow().getValue() < getAllowance().getValue()))
				addError("Value must be greater than allowance", FIELD_LOAGAL);
			
			/* The hiAgeAllow must be non-null */
			if ((getHiAgeAllow() == null) || (!getHiAgeAllow().isPositive()))
				addError("Value must be positive", FIELD_HIAGAL);
			
			/* The hiAgeAllow must be greater than loAgeAllowance */
			if ((getHiAgeAllow() != null) && (getLoAgeAllow() != null) &&
				(getHiAgeAllow().getValue() < getLoAgeAllow().getValue()))
				addError("Value must be greater than low age allowance", FIELD_HIAGAL);
			
			/* The ageAllowLimit must be non-null */
			if ((getAgeAllowLimit() == null) || (!getAgeAllowLimit().isPositive()))
				addError("Value must be positive", FIELD_AGELMT);
			
			/* The capitalAllow must be non-null */
			if ((getCapitalAllow() == null) || (!getCapitalAllow().isPositive()))
				addError("Value must be positive", FIELD_CAPALW);
			
			/* The loBand must be non-null */
			if ((getLoBand() == null) || (!getLoBand().isPositive()))
				addError("Value must be positive", FIELD_LOBAND);
			
			/* The basicBand must be non-null */
			if ((getBasicBand() == null) || (!getBasicBand().isPositive()))
				addError("Value must be positive", FIELD_BSBAND);
			
			/* The loRate must be non-null */
			if ((getLoTaxRate() == null) || (!getLoTaxRate().isPositive()))
				addError("Value must be positive", FIELD_LOTAX);
			
			/* The basicRate must be non-null */
			if ((getBasicTaxRate() == null) || (!getBasicTaxRate().isPositive()))
				addError("Value must be positive", FIELD_BASTAX);

			/* The hiRate must be non-null */
			if ((getHiTaxRate() == null) || (!getHiTaxRate().isPositive()))
				addError("Value must be positive", FIELD_HITAX);
	
			/* The intRate must be non-null */
			if ((getIntTaxRate() == null) || (!getIntTaxRate().isPositive()))
				addError("Value must be positive", FIELD_INTTAX);
			
			/* The divRate must be non-null */
			if ((getDivTaxRate() == null) || (!getDivTaxRate().isPositive()))
				addError("Value must be positive", FIELD_DIVTAX);
			
			/* The hiDivRate must be non-null */
			if ((getHiDivTaxRate() == null) || (!getHiDivTaxRate().isPositive()))
				addError("Value must be positive", FIELD_HDVTAX);			
			
			/* If the tax regime is additional */
			if ((getTaxRegime() != null) && (getTaxRegime().hasAdditionalTaxBand())) {
				/* The addAllowLimit must be non-null */
				if ((getAddAllowLimit() == null) || (!getAddAllowLimit().isPositive()))
					addError("Value must be positive", FIELD_ADDLMT);
				
				/* The addIncBound must be non-null */
				if ((getAddIncBound() == null) || (!getAddIncBound().isPositive()))
					addError("Value must be positive", FIELD_ADDBDY);
				
				/* The addRate must be non-null */
				if ((getAddTaxRate() == null) || (!getAddTaxRate().isPositive()))
					addError("Value must be positive", FIELD_ADDTAX);
				
				/* The addDivRate must be non-null */
				if ((getAddDivTaxRate() == null) || (!getAddDivTaxRate().isPositive()))
					addError("Value must be positive", FIELD_ADVTAX);							
			}
			
			/* If the tax regime does not have capital gains as income */
			if ((getTaxRegime() != null) && (!getTaxRegime().hasCapitalGainsAsIncome())) {
				/* The capitalRate must be non-null */
				if ((getCapTaxRate() == null) || (!getCapTaxRate().isPositive()))
					addError("Value must be positive", FIELD_CAPTAX);
				
				/* The hiCapTaxRate must be positive */
				if ((getHiCapTaxRate() != null) && (!getHiCapTaxRate().isPositive()))
					addError("Value must be positive", FIELD_HCPTAX);
			}
			
			/* Set validation flag */
			if (!hasErrors()) setValidEdit();
		}			
		
		/**
		 * Extract the date range represented by the tax years
		 * 
		 * @return the range of tax years
		 */
		public finObject.Range getRange() {
			finObject.Date  myStart;
			finObject.Date  myEnd;
			finObject.Range myRange;
			
			/* Access start date */
			myStart =  new finObject.Date(getDate());
			
			/* Move back to start of year */
			myStart.adjustYear(-1);
			myStart.adjustDay(1);
			
			/* Access last date */
			myEnd  = getDate();
			
			/* Create the range */
			myRange = new finObject.Range(myStart, myEnd);
			
			/* Return the range */
			return myRange;
		}
		
		/**
		 * Set a new tax regime 
		 * 
		 * @param pTaxRegime the TaxRegime 
		 */
		protected void setTaxRegime(finStatic.TaxRegime pTaxRegime) {
			getObj().setTaxRegime(pTaxRegime);
		}
		
		/**
		 * Set a new allowance 
		 * 
		 * @param pAllowance the allowance 
		 */
		protected void setAllowance(finObject.Money pAllowance) {
			getObj().setAllowance((pAllowance == null) ? null 
													   : new finObject.Money(pAllowance));
		}
		
		/**
		 * Set a new rental allowance 
		 * 
		 * @param pAllowance the allowance 
		 */
		protected void setRentalAllowance(finObject.Money pAllowance) {
			getObj().setRentalAllow((pAllowance == null) ? null 
									  				     : new finObject.Money(pAllowance));
		}
		
		/**
		 * Set a new capital allowance 
		 * 
		 * @param pAllowance the allowance 
		 */
		protected void setCapitalAllow(finObject.Money pAllowance) {
			getObj().setCapitalAllow((pAllowance == null) ? null 
									  				      : new finObject.Money(pAllowance));
		}
		
		/**
		 * Set a new Low Tax Band 
		 * 
		 * @param pLoBand the Low Tax Band 
		 */
		protected void setLoBand(finObject.Money pLoBand) {
			getObj().setLoBand((pLoBand == null) ? null : new finObject.Money(pLoBand));
		}
		
		/**
		 * Set a new Basic Tax Band 
		 * 
		 * @param pBasicBand the Basic Tax Band 
		 */
		protected void setBasicBand(finObject.Money pBasicBand) {
			getObj().setBasicBand((pBasicBand == null) ? null : new finObject.Money(pBasicBand));
		}
		
		/**
		 * Set a new Low Age Allowance 
		 * 
		 * @param pLoAgeAllow the Low Age Allowance 
		 */
		protected void setLoAgeAllow(finObject.Money pLoAgeAllow) {
			getObj().setLoAgeAllow((pLoAgeAllow == null) ? null : new finObject.Money(pLoAgeAllow));
		}
		
		/**
		 * Set a new High Age Allowance 
		 * 
		 * @param pHiAgeAllow the High Age Allowance 
		 */
		protected void setHiAgeAllow(finObject.Money pHiAgeAllow) {
			getObj().setHiAgeAllow((pHiAgeAllow == null) ? null : new finObject.Money(pHiAgeAllow));
		}
		
		/**
		 * Set a new Age Allowance Limit
		 * 
		 * @param pAgeAllowLimit the Age Allowance Limit
		 */
		protected void setAgeAllowLimit(finObject.Money pAgeAllowLimit) {
			getObj().setAgeAllowLimit((pAgeAllowLimit == null) ? null : new finObject.Money(pAgeAllowLimit));
		}
		
		/**
		 * Set a new Additional Allowance Limit
		 * 
		 * @param pAddAllowLimit the Additional Allowance Limit
		 */
		protected void setAddAllowLimit(finObject.Money pAddAllowLimit) {
			getObj().setAddAllowLimit((pAddAllowLimit == null) ? null : new finObject.Money(pAddAllowLimit));
		}
		
		/**
		 * Set a new Additional Income Boundary
		 * 
		 * @param pAddIncBound the Additional Income Boundary
		 */
		protected void setAddIncBound(finObject.Money pAddIncBound) {
			getObj().setAddIncBound((pAddIncBound == null) ? null : new finObject.Money(pAddIncBound));
		}
		
		/**
		 * Set a new Low Tax Rate 
		 * 
		 * @param pRate the Low Tax Rate 
		 */
		protected void setLoTaxRate(finObject.Rate pRate) {
			getObj().setLoTaxRate((pRate == null) ? null : new finObject.Rate(pRate));
		}
		
		/**
		 * Set a new Basic tax rate
		 * 
		 * @param pRate the Basic tax rate 
		 */
		protected void setBasicTaxRate(finObject.Rate pRate) {
			getObj().setBasicTaxRate((pRate == null) ? null : new finObject.Rate(pRate));
		}
		
		/**
		 * Set a new high tax rate 
		 * 
		 * @param pRate the high tax rate 
		 */
		protected void setHiTaxRate(finObject.Rate pRate) {
			getObj().setHiTaxRate((pRate == null) ? null : new finObject.Rate(pRate));
		}
		
		/**
		 * Set a new Interest Tax Rate 
		 * 
		 * @param pRate the Interest Tax Rate 
		 */
		protected void setIntTaxRate(finObject.Rate pRate) {
			getObj().setIntTaxRate((pRate == null) ? null : new finObject.Rate(pRate));
		}
		
		/**
		 * Set a new Dividend tax rate
		 * 
		 * @param pRate the Dividend tax rate 
		 */
		protected void setDivTaxRate(finObject.Rate pRate) {
			getObj().setDivTaxRate((pRate == null) ? null : new finObject.Rate(pRate));
		}
		
		/**
		 * Set a new high dividend tax rate 
		 * 
		 * @param pRate the high dividend tax rate 
		 */
		protected void setHiDivTaxRate(finObject.Rate pRate) {
			getObj().setHiDivTaxRate((pRate == null) ? null : new finObject.Rate(pRate));
		}
		
		/**
		 * Set a new additional tax rate 
		 * 
		 * @param pRate the additional tax rate 
		 */
		protected void setAddTaxRate(finObject.Rate pRate) {
			getObj().setAddTaxRate((pRate == null) ? null : new finObject.Rate(pRate));
		}
		
		/**
		 * Set a new additional dividend tax rate 
		 * 
		 * @param pRate the additional dividend tax rate 
		 */
		protected void setAddDivTaxRate(finObject.Rate pRate) {
			getObj().setAddDivTaxRate((pRate == null) ? null : new finObject.Rate(pRate));
		}
		
		/**
		 * Set a new capital tax rate 
		 * 
		 * @param pRate the capital tax rate 
		 */
		protected void setCapTaxRate(finObject.Rate pRate) {
			getObj().setCapTaxRate((pRate == null) ? null : new finObject.Rate(pRate));
		}
		
		/**
		 * Set a high capital tax rate 
		 * 
		 * @param pRate the additional dividend tax rate 
		 */
		protected void setHiCapTaxRate(finObject.Rate pRate) {
			getObj().setHiCapTaxRate((pRate == null) ? null : new finObject.Rate(pRate));
		}
		
		/**
		 * Mark the tax year as active 
		 */
		protected void setActive() {
			isActive = true;
		}
		
		/**
		 * Update taxYear from a taxYear extract 
		 * 
		 * @param pTaxYear the changed taxYear 
		 */
		public void applyChanges(finLink.itemElement pTaxYear) {
			TaxParms myTaxYear = (TaxParms)pTaxYear;
			
			/* Store the current detail into history */
			pushHistory();
			
			/* Update the tax regime if required */
			if (finObject.differs(getTaxRegime(), myTaxYear.getTaxRegime()))
				setTaxRegime(myTaxYear.getTaxRegime());
		
			/* Update the allowance if required */
			if (finObject.differs(getAllowance(), myTaxYear.getAllowance()))
				setAllowance(myTaxYear.getAllowance());
		
			/* Update the rental allowance if required */
			if (finObject.differs(getRentalAllowance(), myTaxYear.getRentalAllowance()))
				setRentalAllowance(myTaxYear.getRentalAllowance());
		
			/* Update the Low band if required */
			if (finObject.differs(getLoBand(), myTaxYear.getLoBand()))
				setLoBand(myTaxYear.getLoBand());
				
			/* Update the basic band if required */
			if (finObject.differs(getBasicBand(), myTaxYear.getBasicBand()))
				setBasicBand(myTaxYear.getBasicBand());
			
			/* Update the low age allowance if required */
			if (finObject.differs(getLoAgeAllow(), myTaxYear.getLoAgeAllow()))
				setLoAgeAllow(myTaxYear.getLoAgeAllow());
			
			/* Update the high age allowance if required */
			if (finObject.differs(getHiAgeAllow(), myTaxYear.getHiAgeAllow()))
				setHiAgeAllow(myTaxYear.getHiAgeAllow());
			
			/* Update the age allowance limit if required */
			if (finObject.differs(getAgeAllowLimit(), myTaxYear.getAgeAllowLimit()))
				setAgeAllowLimit(myTaxYear.getAgeAllowLimit());
			
			/* Update the additional allowance limit if required */
			if (finObject.differs(getAddAllowLimit(), myTaxYear.getAddAllowLimit()))
				setAddAllowLimit(myTaxYear.getAddAllowLimit());
			
			/* Update the additional income boundary if required */
			if (finObject.differs(getAddIncBound(), myTaxYear.getAddIncBound()))
				setAddIncBound(myTaxYear.getAddIncBound());
			
			/* Update the Low tax rate if required */
			if (finObject.differs(getLoTaxRate(), myTaxYear.getLoTaxRate()))
				setLoTaxRate(myTaxYear.getLoTaxRate());
			
			/* Update the standard tax rate if required */
			if (finObject.differs(getBasicTaxRate(), myTaxYear.getBasicTaxRate()))
				setBasicTaxRate(myTaxYear.getBasicTaxRate());
							
			/* Update the high tax rate if required */
			if (finObject.differs(getHiTaxRate(), myTaxYear.getHiTaxRate()))
				setHiTaxRate(myTaxYear.getHiTaxRate());
							
			/* Update the interest tax rate if required */
			if (finObject.differs(getIntTaxRate(), myTaxYear.getIntTaxRate()))
				setIntTaxRate(myTaxYear.getIntTaxRate());
			
			/* Update the dividend tax rate if required */
			if (finObject.differs(getDivTaxRate(), myTaxYear.getDivTaxRate()))
				setDivTaxRate(myTaxYear.getDivTaxRate());
							
			/* Update the high dividend rate if required */
			if (finObject.differs(getHiDivTaxRate(), myTaxYear.getHiDivTaxRate()))
				setHiDivTaxRate(myTaxYear.getHiDivTaxRate());
			
			/* Update the additional rate if required */
			if (finObject.differs(getAddTaxRate(), myTaxYear.getAddTaxRate()))
				setAddTaxRate(myTaxYear.getAddTaxRate());
			
			/* Update the additional dividend rate if required */
			if (finObject.differs(getAddDivTaxRate(), myTaxYear.getAddDivTaxRate()))
				setAddDivTaxRate(myTaxYear.getAddDivTaxRate());
			
			/* Update the capital rate if required */
			if (finObject.differs(getCapTaxRate(), myTaxYear.getCapTaxRate()))
				setCapTaxRate(myTaxYear.getCapTaxRate());
			
			/* Update the high capital rate if required */
			if (finObject.differs(getHiCapTaxRate(), myTaxYear.getHiCapTaxRate()))
				setHiCapTaxRate(myTaxYear.getHiCapTaxRate());
			
			/* Check for changes */
			if (checkForHistory()) setState(State.CHANGED);
		}
	}
}