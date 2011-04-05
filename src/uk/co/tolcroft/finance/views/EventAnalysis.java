package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.views.Analysis.*;
import uk.co.tolcroft.finance.views.DebugManager.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.data.DataSet.LoadState;
import uk.co.tolcroft.finance.data.TransactionType.TransClass;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.DataList.ListStyle;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.Exception;

public class EventAnalysis {
	/**
	 * The Amount Tax threshold for "small" transactions (£3000)
	 */
	private final static Money valueLimit 	= new Money(Money.convertToValue(3000));

	/**
	 * The Rate Tax threshold for "small" transactions (5%)
	 */
	private final static Rate rateLimit 	= new Rate(Rate.convertToValue(5));
	
	/* Members */
	private DataSet				theData			= null;
	private DebugManager		theDebugMgr		= null;
	private Analysis			theAnalysis		= null;
	private MetaAnalysis		theMetaAnalysis	= null;
	private List				theYears		= null;
	private ActDetail			theAccount		= null;
	private Date				theDate			= null;
	private DilutionEvent.List 	theDilutions 	= null;
	private ExternalAccount		theTaxMan		= null;
	private TransDetail			theTaxPaid		= null;

	/* Access methods */
	public Analysis				getAnalysis() 					{ return theAnalysis; }
	public MetaAnalysis			getMetaAnalysis()				{ return theMetaAnalysis; }
	public List					getAnalysisYears() 				{ return theYears; }
	public AnalysisYear			getAnalysisYear(TaxYear pYear) 	{ return (theYears == null) ? null : theYears.searchFor(pYear); }
	public DilutionEvent.List 	getDilutions() 					{ return theDilutions; }
	
	/**
	 * Constructor for a dated analysis
	 * @param pDebugMgr the debug manager
	 * @param pData	the data to analyse events for
	 * @param pDate	the Date for the analysis
	 */
	public EventAnalysis(DebugManager	pDebugMgr,
			 			 DataSet		pData,
						 Date	 		pDate) throws Exception {
		DataList<Event>.ListIterator 	myIterator;
		Event.List					 	myEvents;
		Event 							myCurr;
		int   							myResult;
		DebugEntry						mySection;
		DebugEntry						myDebug;
		
		/* Store the parameters */
		theData 	= pData;
		theDate		= pDate;
		
		/* Access the debug manager */
		theDebugMgr = pDebugMgr;
		
		/* Access the top-level debug entry for this analysis  */
		mySection = theDebugMgr.getInstant();
		mySection.removeChildren();

		/* Create the analysis */
		theAnalysis = new Analysis(theData,
								   theDate);
		
		/* Create associated MetaAnalyser */
		theMetaAnalysis = new MetaAnalysis(theAnalysis);

		/* Access the TaxMan account and Tax Credit transaction */
		Account 	myTaxMan 	= theData.getAccounts().getTaxMan();
		BucketList	myBuckets	= theAnalysis.getList();
		theTaxMan 	= (ExternalAccount)myBuckets.getAccountDetail(myTaxMan);
		theTaxPaid 	= myBuckets.getTransDetail(TransClass.TAXCREDIT);
		
		/* Access the events and the iterator */
		myEvents 	= pData.getEvents();
		myIterator 	= myEvents.listIterator();
		
		/* Loop through the Events extracting relevant elements */
		while ((myCurr = myIterator.next()) != null) {
			/* Check the range */
			myResult = theDate.compareTo(myCurr.getDate());
			
			/* Handle out of range */
			if (myResult == -1) break;
			
			/* Process the event in the asset report */
			processEvent(myCurr);
		}		
		
		/* Value priced assets */
		theMetaAnalysis.valueAssets();

		/* produce totals */
		theMetaAnalysis.produceTotals();

		/* Create the debug entry for this analysis and add it as a child of the main entry  */
		myDebug = theDebugMgr.new DebugEntry("Totals");
		myDebug.addAsChildOf(mySection);
		myDebug.setObject(theAnalysis.getList());

		/* Create the debug entry for the capital events and add as a child of the main entry */
		myDebug = theMetaAnalysis.annotateCapitalDebug(theDebugMgr);
		myDebug.addAsChildOf(mySection);
		
		/* Show the section */
		mySection.showEntry();
		mySection.setChanged();
	}
	
	/**
	 * Constructor for a statement analysis
	 * @param pDebugMgr the debug manager
	 * @param pData	the data to analyse events for
	 * @param pStatement the statement to prepare
	 */
	public EventAnalysis(DebugManager	pDebugMgr,
						 DataSet		pData,
						 Statement 		pStatement)  throws Exception {
		DataList<Event>.ListIterator 	myIterator;
		Event.List					 	myEvents;
		Event 							myCurr;
		Date.Range						myRange;
		Account							myAccount;
		Statement.Line					myLine;
		Statement.List					myList;
		DebugEntry						mySection;
		DebugEntry						myDebug;
		int   							myResult;

		/* Access key points of the statement */
		myRange		= pStatement.getDateRange();
		myAccount	= pStatement.getAccount();
		myList		= pStatement.getLines();

		/* Store the parameters */
		theData 	= pData;
		theDate		= myRange.getStart();
		
		/* Access the debug manager */
		theDebugMgr = pDebugMgr;
		
		/* Access the top-level debug entry for this analysis  */
		mySection = theDebugMgr.getAccount();
		mySection.removeChildren();

		/* Create the analysis */
		theAnalysis = new Analysis(theData,
								   myAccount,
								   theDate);

		/* Access the TaxMan account and Tax Credit transaction */
		Account 	myTaxMan 	= theData.getAccounts().getTaxMan();
		BucketList	myBuckets	= theAnalysis.getList();
		theTaxMan 	= (ExternalAccount)myBuckets.getAccountDetail(myTaxMan);
		theTaxPaid 	= myBuckets.getTransDetail(TransClass.TAXCREDIT);
		theAccount  = myBuckets.getAccountDetail(myAccount);
		
		/* Access the events and the iterator */
		myEvents 	= pData.getEvents();
		myIterator 	= myEvents.listIterator();
		
		/* Loop through the Events extracting relevant elements */
		while ((myCurr = myIterator.next()) != null) {
			/* Check the range */
			myResult = myRange.compareTo(myCurr.getDate());
			
			/* If we are at or past the range break the loop */
			if (myResult != 1) break;
			
			/* Ignore items that do not relate to this account */
			if (!myCurr.relatesTo(myAccount)) continue;
			
			/* Process the event in the asset report */
			processEvent(myCurr);
		}	
		
		/* move the iterator back one */
		myIterator.previous();
		
		/* create a save point */
		theAccount.createSavePoint();

		/* Set starting balance and units for account */
		pStatement.setStartBalances(theAccount);
		
		/* Continue looping through the Events extracting relevant elements */
		while ((myCurr = myIterator.next()) != null) {
			/* Check the range */
			myResult = myRange.compareTo(myCurr.getDate());
			
			/* Handle past limit */
			if (myResult == -1) break;
			
			/* Ignore items that do not relate to this account */
			if (!myCurr.relatesTo(myAccount)) continue;
			
			/* Add a statement line to the statement */
			myLine = pStatement.new Line(myList, myCurr, myAccount);
			myLine.addToList();
		}
	
		/* Reset the statement balances */
		resetStatementBalance(pStatement);
		
		/* Create the debug entry for this analysis and add it as a child of the main entry  */
		myDebug = theDebugMgr.new DebugEntry("Totals");
		myDebug.addAsChildOf(mySection);
		myDebug.setObject(theAccount);

		/* If the account is an asset */
		if (theAccount instanceof AssetAccount) {
			/* Create the debug entry for the capital events and add as a child of the main entry */
			myDebug = theDebugMgr.new DebugEntry("CapitalEvents");
			myDebug.addAsChildOf(mySection);
			myDebug.setObject(((AssetAccount)theAccount).getCapitalEvents());
		}

		/* Update display */
		mySection.setChanged();
	}
	
 	/**
 	 * recalculate statement balance
 	 * @param pStatement the statement
 	 */
	protected void resetStatementBalance(Statement pStatement) throws Exception {
		Statement.Line     			myLine;
		Statement.List				myLines;
		Event.List					myList;
		Event						myEvent;
		
		DataList<Statement.Line>.ListIterator	myIterator;

		/* Access the iterator */
		myLines		= pStatement.getLines();
		myIterator = myLines.listIterator();
		
		/* If we don't have balances just return */
		if (theAccount instanceof ExternalAccount) return;
		
		/* Restore the SavePoint */
		theAccount.restoreSavePoint();
		
		/* Create a new Event list */
		myList = new Event.List(theData, ListStyle.VIEW);
	
		/* Loop through the lines adjusting the balance */
		while ((myLine = myIterator.next()) != null) {
			/* Skip deleted lines */
			if (myLine.isDeleted()) continue;

			/* Ignore if it is not a valid event */
			if (myLine.getPartner() == null) continue;
			if (myLine.getTransType() == null) continue;
			if (myLine.getAmount() == null) continue;
			
			/* Create an event from this line */
			myEvent = new Event(myList, myLine);

			/* Process the event */
			processEvent(myEvent);
			
			/* Update the balances */
			myLine.setBalances();
		}
	
		/* Set the ending balances */
		pStatement.setEndBalances();
	}
	
	/**
	 * Constructor for a full year set of accounts
	 * @param pDebugMgr the debug manager
	 * @param pData	the data to analyse events for
	 */
	public EventAnalysis(DebugManager	pDebugMgr,
			 			 DataSet		pData) throws Exception {
		Event           				myCurr;
		DataList<Event>.ListIterator	myIterator;
		int             				myResult	= -1;
		TaxYear         				myTax  		= null;
		Date   							myDate 		= null;
		TaxYear.List					myList;
		AnalysisYear					myYear;
		Account							myAccount;
		DebugEntry						mySection;
		DebugEntry						myDebug;
		boolean							isLoaded;

		/* Store the parameters */
		theData 	= pData;
		
		/* Access the debug manager */
		theDebugMgr = pDebugMgr;
		
		/* Create the top level debug entry for this analysis  */
		mySection = theDebugMgr.getAnalysis();
		mySection.removeChildren();

		/* Create a list of AnalysisYears */
		theYears = new List();

		/* Create the Dilution Event List */
		theDilutions = new DilutionEvent.List(theData);
		
		/* Determine whether the DataSet is fully loaded */
		isLoaded = (theData.getLoadState() == LoadState.LOADED);
		
		/* Access the tax years list */
		myList = theData.getTaxYears();
		
		/* Add the Yearly totals as a child of the main entry */
		myDebug = theYears.getDebugEntry();
		myDebug.addAsChildOf(mySection);
				
		/* Access the Event iterator */
		myIterator = theData.getEvents().listIterator();
		
		/* Loop through the Events extracting relevant elements */
		while ((myCurr = myIterator.next()) != null) {
			/* If we have a current tax year */
			if (myTax != null) {
				/* Check that this event is still in the tax year */
				myResult = myDate.compareTo(myCurr.getDate());
			}
			
			/* If we have exhausted the tax year or else this is the first tax year */
			if (myResult == -1) { 
				/* Access the relevant tax year */
				myTax  = myList.searchFor(myCurr.getDate());
				myDate = myTax.getDate();
		
				/* If we have an existing meta analysis year */
				if (theAnalysis != null) {
					/* Value priced assets */
					theMetaAnalysis.valueAssets();
				}
				
				/* Create the new Analysis */
				myYear = theYears.getNewAnalysis(myTax, theAnalysis);
				theAnalysis 	= myYear.getAnalysis();
				theMetaAnalysis = myYear.getMetaAnalysis();

				/* Access the TaxMan account and Tax Credit transaction */
				Account 	myTaxMan 	= theData.getAccounts().getTaxMan();
				BucketList	myBuckets	= theAnalysis.getList();
				theTaxMan 	= (ExternalAccount)myBuckets.getAccountDetail(myTaxMan);
				theTaxPaid 	= myBuckets.getTransDetail(TransClass.TAXCREDIT);
			}
						
			/* Touch credit account */
			myAccount = myCurr.getCredit();
			myAccount.touchAccount(myCurr);
			if ((isLoaded) && (myAccount.isChild())) {
				myAccount.getParent().touchAccount(myCurr);
			}
			
			/* Touch debit accounts */
			myAccount = myCurr.getDebit();
			myAccount.touchAccount(myCurr);
			if ((isLoaded) && (myAccount.isChild())) {
				myAccount.getParent().touchAccount(myCurr);
			}

			/* If the event has a dilution factor */
			if (myCurr.getDilution() != null) {
				/* Add to the dilution event list */
				theDilutions.addDilution(myCurr);
			}

			/* Process the event in the report set */
			processEvent(myCurr);
			myTax.setActive();
		}
		
		/* Value priced assets of the most recent set */
		if (theMetaAnalysis != null) theMetaAnalysis.valueAssets();

		/* If we have any entries */
		if (theMetaAnalysis != null) {
			/* Create the debug entry for the capital events and add as a child of the main entry */
			myDebug = theMetaAnalysis.annotateCapitalDebug(theDebugMgr);
			myDebug.addAsChildOf(mySection);
		
			/* Create the debug entry for the dilutions and add it as a child of the main entry */
			myDebug = theDebugMgr.new DebugEntry("Dilutions");
			myDebug.addAsChildOf(mySection);
			myDebug.setObject(theDilutions);
		}

		/* Update display */
		mySection.setChanged();
	}
	
	/* Public class for analysis year */
	public class AnalysisYear extends DataItem {
		/**
		 * The name of the object
		 */
		private static final String objName = "AnalysisYear";

		/* Members */
		private Analysis 		theAnalysis 	= null;
		private MetaAnalysis	theMetaAnalysis	= null;
		private TaxYear			theYear			= null;
		private DebugEntry		theListDebug	= null;
		private DebugEntry		theChargeDebug	= null;

		/* Access methods */
		public 	Date			getDate()			{ return theYear.getDate(); }
		public 	TaxYear			getTaxYear()		{ return theYear; }
		public 	Analysis		getAnalysis()		{ return theAnalysis; }
		public 	MetaAnalysis	getMetaAnalysis()	{ return theMetaAnalysis; }

		/** 
		 * Constructor for the Analysis Year
		 */
		private AnalysisYear(List 		pList,
							 TaxYear 	pYear,
							 Analysis	pPrevious) {
			/* Call super-constructor */
			super(pList, 0);
			
			/* Debug entry */
			DebugEntry myDebug;
			
			/* Store tax year */
			theYear = pYear;
			
			/* Create new analysis */
			theAnalysis = new Analysis(theData, pYear, pPrevious);
			
			/* Create associated MetaAnalyser */
			theMetaAnalysis = new MetaAnalysis(theAnalysis);

			/* Create the top level debug entry for this year and add it as a child of the list  */
			myDebug = theDebugMgr.new DebugEntry(Integer.toString(theYear.getDate().getYear()));
			myDebug.addAsFirstChildOf(pList.getDebugEntry());

			/* Create the buckets debug entry for this year and add it as a child of the year  */
			theListDebug = theDebugMgr.new DebugEntry("Totals");
			theListDebug.addAsChildOf(myDebug);
			theListDebug.setObject(theAnalysis.getList());

			/* Create the charges debug entry for this year and add it as a child of the year  */
			theChargeDebug = theDebugMgr.new DebugEntry("Charges");
			theChargeDebug.addAsChildOf(myDebug);
			theChargeDebug.setObject(theAnalysis.getCharges());
			
			/* Hide the charges */
			theChargeDebug.hideEntry();
		}
		
		/* Field IDs */
		public static final int FIELD_YEAR	  	= 0;
		public static final int NUMFIELDS	    = 1;
	
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
				case FIELD_YEAR: 		return "Year";
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
			String myString = ""; 
			switch (iField) {
				case FIELD_YEAR: 			
					myString += Date.format(theYear.getDate());
					break;
			}
			return myString;
		}

		/**
		 * Compare this Bucket to another to establish equality.
		 * 
		 * @param pThat The Bucket to compare to
		 * @return <code>true</code> if the bucket is identical, <code>false</code> otherwise
		 */
		public boolean equals(Object pThat) {
			/* Handle the trivial cases */
			if (this == pThat) return true;
			if (pThat == null) return false;
		
			/* Make sure that the object is the same class */
			if (pThat.getClass() != this.getClass()) return false;
		
			/* Access the object as an AnalysisYear Bucket */
			AnalysisYear myThat = (AnalysisYear)pThat;
		
			/* Check for equality */
			if (Date.differs(getDate(), myThat.getDate())) 	return false;
			return true;
		}

		/**
		 * Compare this Bucket to another to establish sort order.
		 * 
		 * @param pThat The Bucket to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		public int compareTo(Object pThat) {
			int result;

			/* Handle the trivial cases */
			if (this == pThat) return 0;
			if (pThat == null) return -1;
		
			/* Make sure that the object is the same class */
			if (pThat.getClass() != this.getClass()) return -1;
		
			/* Access the object as am Analysis Year */
			AnalysisYear myThat = (AnalysisYear)pThat;
		
			/* Compare the bucket order */
			result = getDate().compareTo(myThat.getDate());
			return result;
		}		

		/**
		 * Produce totals for an analysis year 
		 * @param pProperties the properties
		 */
		public void produceTotals() {
			/* If we are in valued state */
			if (theAnalysis.getState() == AnalysisState.VALUED) {
				/* call the meta analyser to produce totals */
				theMetaAnalysis.produceTotals();
				
				/* Declare a change in the debug entry */
				theListDebug.setChanged();
			}
		}

		/**
		 * Calculate tax for an analysis year 
		 * @param pProperties the properties
		 */
		public void calculateTax(Properties pProperties) {
			/* If we are not in taxed state */
			if (theAnalysis.getState() != AnalysisState.TAXED) {
				/* call the meta analyser to calculate tax */
				theMetaAnalysis.calculateTax(pProperties);
				
				/* Declare a change in the debug entry */
				theListDebug.setChanged();
				
				/* Show the charges */
				theChargeDebug.showEntry();
			}
		}
	}
	
	/* the list class */
	public class List extends DataList<AnalysisYear> {
		/* Members */
		private DebugEntry		theDebug		= null;

		/**
		 * Access the debug entry for this list 
		 * @return the debug entry 
		 */
		private DebugEntry getDebugEntry() { return theDebug; }
		
		/**
		 * Construct a top-level List
		 */
		public List() {
			/* Call super constructor */
			super(ListStyle.VIEW, false);
			
			/* Create debug entry for this list */
			theDebug = theDebugMgr.new DebugEntry("AnnualTotals");
		}

		/** 
	 	 * Clone a Bucket list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return null; }
		
		/**
		 * Add a new item to the list
		 * @param pItem the item to add
		 * @return the newly added item
		 */
		public AnalysisYear addNewItem(DataItem pItem) { return null; }
	
		/**
		 * Add a new item to the edit list
		 * @param isCredit - ignored
		 * @return the newly added item
		 */
		public AnalysisYear addNewItem(boolean isCredit) { return null; }
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return AnalysisYear.objName; }
				
		/**
		 * Add new analysis based on the previous analysis 
		 * @param pYear the tax year
		 * @param pAnalysis the previous analysis
		 * @return the bucket
		 */
		protected AnalysisYear getNewAnalysis(TaxYear 	pYear,
									      	  Analysis	pAnalysis) {
			/* Locate the bucket in the list */
			AnalysisYear myYear = new AnalysisYear(this, pYear, pAnalysis);
			myYear.addToList();
			return myYear;
		}		
		
		/**
		 * Search for tax year 
		 * @param pYear the tax year to search for
		 * @return the analysis
		 */
		public AnalysisYear searchFor(TaxYear pYear) {
			ListIterator myIterator;
			AnalysisYear myCurr;
			
			/* Access the list iterator */
			myIterator = listIterator();
			
			/* Loop through the tax parameters */
			while ((myCurr  = myIterator.next()) != null) {
				/* Break on match */
				if (myCurr.theYear.compareTo(pYear) == 0)
					break;
			}
			
			/* Return to caller */
			return myCurr;
		}		
	}
	
	/**
	 * Process an event 
	 * @param pEvent the event to process 
	 */
	private void processEvent(Event pEvent) throws Exception {
		Account			myDebit 	= pEvent.getDebit();
		Account			myCredit 	= pEvent.getCredit(); 
		
		/* If the event relates to a priced item split out the workings */
		if ((myDebit.isPriced()) || (myCredit.isPriced())) {
			/* Process as a Capital event */
			processCapitalEvent(pEvent);
		}
		
		/* Else handle the event normally */
		else {
			TransactionType 		myTrans 	= pEvent.getTransType();
			TransactionType.List 	myTranList 	= theData.getTransTypes();
			BucketList				myBuckets	= theAnalysis.getList();
			
			/* If the event is interest */
			if (myTrans.isInterest()) {
				/* If the account is tax free */
				if (myDebit.isTaxFree()) {
					/* The true transaction type is TaxFreeInterest */
					myTrans = myTranList.searchFor(TransClass.TAXFREEINTEREST);
				}
				
				/* True debit account is the parent */
				myDebit = myDebit.getParent();
			}

			/* Adjust the debit account bucket */
			ActDetail	myBucket = myBuckets.getAccountDetail(myDebit);
			myBucket.adjustForDebit(pEvent);

			/* Adjust the credit account bucket */
			myBucket = myBuckets.getAccountDetail(myCredit);
			myBucket.adjustForCredit(pEvent);
			
			/* If the event causes a tax credit */
			if (pEvent.getTaxCredit() != null) {
				/* Adjust the TaxMan account for the tax credit */
				theTaxMan.adjustForTaxCredit(pEvent);
				theTaxPaid.adjustForTaxCredit(pEvent);
			}
			
			/* Adjust the relevant transaction bucket */
			TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
			myTranBucket.adjustAmount(pEvent);
		}
	}
	
	/**
	 * Process a capital event 
	 * @param pEvent the event to process 
	 */
	void processCapitalEvent(Event pEvent) throws Exception {
		TransactionType 		myTrans 	= pEvent.getTransType();
		
		/* Switch on the transaction */
		switch (myTrans.getTranClass()) {
			/* Process a stock split */
			case STOCKSPLIT:
			case ADMINCHARGE:
				processStockSplit(pEvent);
				break;
			/* Process a stock right taken */
			case STOCKRIGHTTAKEN:
				processTransferIn(pEvent);
				break;
			/* Process a stock right taken */
			case STOCKRIGHTWAIVED:
				processStockRightWaived(pEvent);
				break;
			/* Process a stock DeMerger */	
			case STOCKDEMERGER:
				processStockDeMerger(pEvent);
				break;
			/* Process a Cash TakeOver */	
			case CASHTAKEOVER:
				processCashTakeover(pEvent);
				break;
			/* Process a Cash TakeOver */	
			case STOCKTAKEOVER:
				processStockTakeover(pEvent);
				break;
			/* Process a Taxable Gain */	
			case TAXABLEGAIN:
				processTaxableGain(pEvent);
				break;
			/* Process a dividend */
			case DIVIDEND:
				processDividend(pEvent);
				break;
			/* Process standard transfer in/out */
			case TRANSFER:
			case EXPENSE:
			case INHERITED:
			case TAXFREEINCOME:
				if (pEvent.getCredit().isPriced())
					processTransferIn(pEvent);
				else 
					processTransferOut(pEvent);
				break;
			/* Throw an Exception  */
			default:
				throw new Exception(ExceptionClass.LOGIC,
									"Unexpected transaction type: " + myTrans.getTranClass());
		}
	}

	/**
	 * Process an event that is a stock split.
	 * This capital event relates only to the Debit Account
	 * @param pEvent the event
	 */
	private void processStockSplit(Event pEvent) {
		/* Stock split has identical credit/debit and always has Units */
		Account	myAccount 	= pEvent.getCredit();
		Units	myUnits		= pEvent.getUnits();

		/* Access the Asset Account Bucket */
		BucketList 		myBuckets 	= theAnalysis.getList();
		AssetAccount	myAsset		= (AssetAccount)myBuckets.getAccountDetail(myAccount);
		
		/* Allocate a Capital event and record Current and delta units */
		CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);
		myEvent.addAttribute(CapitalEvent.capitalInitialUnits, myAsset.getUnits());
		myEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myUnits);

		/* Add/Subtract the units movement for the account */
		myAsset.getUnits().addUnits(myUnits);
		myEvent.addAttribute(CapitalEvent.capitalFinalUnits, myAsset.getUnits());
	}

	/**
	 * Process an event that is a transfer into capital (also StockRightTaken and Dividend Re-investment).
	 * This capital event relates only to the Credit Account
	 * @param pEvent the event
	 */
	private void processTransferIn(Event pEvent) {
		/* Transfer in is to the credit account and may or may not have units */
		Account			myAccount 	= pEvent.getCredit();
		Account			myDebit		= pEvent.getDebit();
		Units			myUnits		= pEvent.getUnits();
		Money			myAmount 	= pEvent.getAmount();
		TransactionType	myTrans		= pEvent.getTransType();

		/* Access the Asset Account Bucket */
		BucketList 		myBuckets 	= theAnalysis.getList();
		AssetAccount	myAsset		= (AssetAccount)myBuckets.getAccountDetail(myAccount);

		/* Allocate a Capital event and record Current and delta costs */
		CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);
		myEvent.addAttribute(CapitalEvent.capitalInitialCost, myAsset.getCost());
		myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myAmount);

		/* Adjust the cost of this account */
		myAsset.getCost().addAmount(myAmount);
		myEvent.addAttribute(CapitalEvent.capitalFinalCost, myAsset.getCost());

		/* If we have new units */
		if (myUnits != null) {
			/* Record current and delta units */
			myEvent.addAttribute(CapitalEvent.capitalInitialUnits, myAsset.getUnits());
			myEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myUnits);

			/* Add the units movement for the account */
			myAsset.getUnits().addUnits(myUnits);
			myEvent.addAttribute(CapitalEvent.capitalFinalUnits, myAsset.getUnits());
		}

		/* Record the current/delta investment */
		myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
		myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myAmount);
		
		/* Adjust the total money invested into this account */
		myAsset.getInvested().addAmount(myAmount);
		myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());
				
		/* If the event causes a tax credit */
		if (pEvent.getTaxCredit() != null) {
			/* Adjust the TaxMan account for the tax credit */
			theTaxMan.adjustForTaxCredit(pEvent);
			theTaxPaid.adjustForTaxCredit(pEvent);
		}
		
		/* Adjust the debit account bucket */
		ActDetail	myBucket = myBuckets.getAccountDetail(myDebit);
		myBucket.adjustForDebit(pEvent);

		/* Adjust the relevant transaction bucket */
		TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
		myTranBucket.adjustAmount(pEvent);
	}
	
	/**
	 * Process a dividend event.
	 * This capital event relates to the only to Debit account, 
	 * @param pEvent the event
	 */
	private void processDividend(Event pEvent) {
		/* The main account that we are interested in is the debit account */
		Account					myAccount		= pEvent.getDebit();
		Account					myCredit		= pEvent.getCredit();
		TransactionType			myTrans			= pEvent.getTransType();
		TransactionType.List 	myTranList 		= theData.getTransTypes();
		Money					myAmount 		= pEvent.getAmount();
		Money					myTaxCredit		= pEvent.getTaxCredit();
		Units					myUnits			= pEvent.getUnits();
		Account					myDebit;

		/* If the account is tax free */
		if (myAccount.isTaxFree()) {
			/* The true transaction type is TaxFreeDividend */
			myTrans = myTranList.searchFor(TransClass.TAXFREEDIVIDEND);
		}
		
		/* else if the account is a unit trust */
		else if (myAccount.isUnitTrust()) {
			/* The true transaction type is UnitTrustDividend */
			myTrans = myTranList.searchFor(TransClass.UNITTRUSTDIV);
		}
		
		/* True debit account is the parent */
		myDebit = myAccount.getParent();
	
		/* Adjust the debit account bucket */
		BucketList 	myBuckets 	= theAnalysis.getList();
		ActDetail	myBucket 	= myBuckets.getAccountDetail(myDebit);
		myBucket.adjustForDebit(pEvent);

		/* Access the Asset Account Bucket */
		AssetAccount	myAsset		= (AssetAccount)myBuckets.getAccountDetail(myAccount);

		/* Allocate a Capital event and record Current and delta costs */
		CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

		/* If this is a re-investment */
		if (myAccount.equals(myCredit)) {			
			/* This amount is added to the cost, so record as the delta cost */
			myEvent.addAttribute(CapitalEvent.capitalInitialCost, myAsset.getCost());
			myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myAmount);

			/* Adjust the cost of this account */
			myAsset.getCost().addAmount(myAmount);
			myEvent.addAttribute(CapitalEvent.capitalFinalCost, myAsset.getCost());

			/* Record the current/delta investment */
			myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
			myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myAmount);
			
			/* Adjust the total money invested into this account */
			myAsset.getInvested().addAmount(myAmount);
			myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());
					
			/* If we have new units */
			if (myUnits != null) {
				/* Record current and delta units */
				myEvent.addAttribute(CapitalEvent.capitalInitialUnits, myAsset.getUnits());
				myEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myUnits);

				/* Add the units movement for the account */
				myAsset.getUnits().addUnits(myUnits);
				myEvent.addAttribute(CapitalEvent.capitalFinalUnits, myAsset.getUnits());
			}

			/* If we have a tax credit */
			if (myTaxCredit != null) {
				/* The Tax Credit is viewed as a gain from the account */
				myEvent.addAttribute(CapitalEvent.capitalInitialDiv, myAsset.getDividend());
				myEvent.addAttribute(CapitalEvent.capitalDeltaDiv, myTaxCredit);

				/* The Tax Credit is viewed as a gain from the account */
				myAsset.getDividend().addAmount(myTaxCredit);
				myEvent.addAttribute(CapitalEvent.capitalFinalDiv, myAsset.getDividend());
			}	
		}
		
		/* else we are paying out to another account */
		else {
			/* Adjust the gains total for this asset */
			Money myDividends = new Money(myAmount);
			
			/* Any tax credit is viewed as a realised gain from the account */
			if (myTaxCredit != null)
				myDividends.addAmount(myTaxCredit);
			
			/* The Dividend is viewed as a dividend from the account */
			myEvent.addAttribute(CapitalEvent.capitalInitialDiv, myAsset.getDividend());
			myEvent.addAttribute(CapitalEvent.capitalDeltaDiv, myDividends);

			/* The Dividend is viewed as a gain from the account */
			myAsset.getDividend().addAmount(myDividends);
			myEvent.addAttribute(CapitalEvent.capitalFinalDiv, myAsset.getDividend());

			/* Adjust the credit account bucket */
			myBucket = myBuckets.getAccountDetail(myCredit);
			myBucket.adjustForCredit(pEvent);			
		}
		
		/* If the event causes a tax credit */
		if (pEvent.getTaxCredit() != null) {
			/* Adjust the TaxMan account for the tax credit */
			theTaxMan.adjustForTaxCredit(pEvent);
			theTaxPaid.adjustForTaxCredit(pEvent);
		}		

		/* Adjust the relevant transaction bucket */
		TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
		myTranBucket.adjustAmount(pEvent);
	}
	
	/**
	 * Process an event that is a transfer from capital.
	 * This capital event relates only to the Debit Account
	 * @param pEvent the event
	 */
	private void processTransferOut(Event pEvent) {
		/* Transfer out is from the debit account and may or may not have units */
		Account			myAccount 	= pEvent.getDebit();
		Account			myCredit 	= pEvent.getCredit();
		Money			myAmount 	= pEvent.getAmount();
		Units 			myUnits 	= pEvent.getUnits();
		TransactionType	myTrans		= pEvent.getTransType();
		Money			myReduction;
		Money			myDeltaCost;
		Money			myDeltaGains;
		Money			myCost;

		/* Access the Asset Account Bucket */
		BucketList 		myBuckets 	= theAnalysis.getList();
		AssetAccount	myAsset		= (AssetAccount)myBuckets.getAccountDetail(myAccount);

		/* Allocate a Capital event and record Current and delta costs */
		CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

		/* Record the current/delta investment */
		myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
		myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myAmount);

		/* Adjust the total amount invested into this account */
		myAsset.getInvested().subtractAmount(myAmount);
		myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());
		
		/* Assume the the cost reduction is the full value */
		myReduction = new Money(myAmount);
		myCost 		= myAsset.getCost();
		
		/* If we are reducing units in the account */
		if ((myUnits != null) && (myUnits.isNonZero())) {
			/* The reduction is the relevant fraction of the cost */
			myReduction = myCost.valueAtWeight(myUnits, myAsset.getUnits());
		}
		
		/* If the reduction is greater than the total cost */
		if (myReduction.getValue() > myCost.getValue()) {
			/* Reduction is the total cost */
			myReduction = new Money(myCost);
		}
		
		/* Determine the delta to the cost */
		myDeltaCost = new Money(myReduction);
		myDeltaCost.negate();
		
		/* If we have a delta to the cost */
		if (myDeltaCost.isNonZero()) {
			/* This amount is subtracted from the cost, so record as the delta cost */
			myEvent.addAttribute(CapitalEvent.capitalInitialCost, myCost);
			myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);
		
			/* Adjust the cost appropriately */
			myCost.addAmount(myDeltaCost);
			myEvent.addAttribute(CapitalEvent.capitalFinalCost, myCost);
		}
		
		/* Determine the delta to the gains */
		myDeltaGains = new Money(myAmount);
		myDeltaGains.addAmount(myDeltaCost);
		
		/* If we have a delta to the gains */
		if (myDeltaGains.isNonZero()) {
			/* This amount is subtracted from the cost, so record as the delta cost */
			myEvent.addAttribute(CapitalEvent.capitalInitialGains, myAsset.getGains());
			myEvent.addAttribute(CapitalEvent.capitalDeltaGains, myDeltaGains);
		
			/* Adjust the cost appropriately */
			myAsset.getGains().addAmount(myDeltaGains);
			myEvent.addAttribute(CapitalEvent.capitalFinalGains, myAsset.getGains());
		}
				
		/* If we have reduced units */
		if (myUnits != null) {
			/* Access units as negative value */
			Units myDeltaUnits = new Units(myUnits);
			myDeltaUnits.negate();
			
			/* Record current and delta units */
			myEvent.addAttribute(CapitalEvent.capitalInitialUnits, myAsset.getUnits());
			myEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myDeltaUnits);

			/* Add the units movement for the account */
			myAsset.getUnits().subtractUnits(myUnits);
			myEvent.addAttribute(CapitalEvent.capitalFinalUnits, myAsset.getUnits());
		}

		/* Adjust the credit account bucket */
		ActDetail myBucket = myBuckets.getAccountDetail(myCredit);
		myBucket.adjustForCredit(pEvent);
		
		/* Adjust the relevant transaction bucket */
		TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
		myTranBucket.adjustAmount(pEvent);
	}
	
	/**
	 * Process an event that is a taxable gain.
	 * This capital event relates only to the Debit Account
	 * @param pEvent the event
	 */
	private void processTaxableGain(Event pEvent) {
		/* Transfer in is from the debit account and may or may not have units */
		Account			myAccount 	= pEvent.getDebit();
		Account			myCredit 	= pEvent.getCredit();
		Money			myAmount 	= pEvent.getAmount();
		Units 			myUnits 	= pEvent.getUnits();
		TransactionType	myTrans		= pEvent.getTransType();
		Money			myReduction;
		Money			myDeltaCost;
		Money			myDeltaGains;
		Money			myCost;
		Account			myDebit;

		/* Access the Asset Account Bucket */
		BucketList 		myBuckets 	= theAnalysis.getList();
		AssetAccount	myAsset		= (AssetAccount)myBuckets.getAccountDetail(myAccount);

		/* Allocate a Capital event */
		CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

		/* Record the current/delta investment */
		myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
		myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myAmount);

		/* Adjust the total amount invested into this account */
		myAsset.getInvested().subtractAmount(myAmount);
		myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());
		
		/* Assume the the cost reduction is the full value */
		myReduction = new Money(myAmount);
		myCost 		= myAsset.getCost();
		
		/* If we are reducing units in the account */
		if ((myUnits != null) && (myUnits.isNonZero())) {
			/* The reduction is the relevant fraction of the cost */
			myReduction = myCost.valueAtWeight(myUnits, myAsset.getUnits());
		}
		
		/* If the reduction is greater than the total cost */
		if (myReduction.getValue() > myCost.getValue()) {
			/* Reduction is the total cost */
			myReduction = new Money(myCost);
		}
		
		/* Determine the delta to the cost */
		myDeltaCost = new Money(myReduction);
		myDeltaCost.negate();
		
		/* If we have a delta to the cost */
		if (myDeltaCost.isNonZero()) {
			/* This amount is subtracted from the cost, so record as the delta cost */
			myEvent.addAttribute(CapitalEvent.capitalInitialCost, myCost);
			myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);
		
			/* Adjust the cost appropriately */
			myCost.addAmount(myDeltaCost);
			myEvent.addAttribute(CapitalEvent.capitalFinalCost, myCost);
		}
		
		/* Determine the delta to the gains */
		myDeltaGains = new Money(myAmount);
		myDeltaGains.addAmount(myDeltaCost);
		
		/* If we have a delta to the gains */
		if (myDeltaGains.isNonZero()) {
			/* This amount is subtracted from the cost, so record as the delta cost */
			myEvent.addAttribute(CapitalEvent.capitalInitialGains, myAsset.getGains());
			myEvent.addAttribute(CapitalEvent.capitalDeltaGains, myDeltaGains);
		
			/* Adjust the cost appropriately */
			myAsset.getGains().addAmount(myDeltaGains);
			myEvent.addAttribute(CapitalEvent.capitalFinalGains, myAsset.getGains());
		}
				
		/* If we have reduced units */
		if (myUnits != null) {
			/* Access units as negative value */
			Units myDeltaUnits = new Units(myUnits);
			myDeltaUnits.negate();
			
			/* Record current and delta units */
			myEvent.addAttribute(CapitalEvent.capitalInitialUnits, myAsset.getUnits());
			myEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myDeltaUnits);

			/* Add the units movement for the account */
			myAsset.getUnits().subtractUnits(myUnits);
			myEvent.addAttribute(CapitalEvent.capitalFinalUnits, myAsset.getUnits());
		}

		/* True debit account is the parent */
		myDebit = myAccount.getParent();
	
		/* Adjust the debit account bucket */
		ExternalAccount myDebitBucket 	= (ExternalAccount)myBuckets.getAccountDetail(myDebit);
		myDebitBucket.adjustForTaxGainTaxCredit(pEvent);

		/* Adjust the credit account bucket */
		ActDetail myBucket = myBuckets.getAccountDetail(myCredit);
		myBucket.adjustForCredit(pEvent);
		
		/* Adjust the relevant transaction bucket */
		TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
		myTranBucket.adjustAmount(pEvent);
		myTranBucket.getAmount().subtractAmount(myReduction);
		
		/* Adjust the TaxMan account for the tax credit */
		theTaxMan.adjustForTaxCredit(pEvent);
		theTaxPaid.adjustForTaxCredit(pEvent);
	}
	
	/**
	 * Process an event that is stock right waived.
	 * This capital event relates only to the Debit Account
	 * @param pEvent the event
	 */
	private void processStockRightWaived(Event pEvent) {
		/* Stock Right Waived is from the debit account */
		Account			myAccount 	= pEvent.getDebit();
		Account			myCredit 	= pEvent.getCredit();
		AcctPrice.List 	myPrices	= theData.getPrices();
		Money			myAmount 	= pEvent.getAmount();
		TransactionType	myTrans		= pEvent.getTransType();
		AcctPrice		myActPrice;
		Price			myPrice;
		Money			myValue;
		Money			myCost;
		Money			myReduction;
		Money			myPortion;
		Money			myDeltaCost;
		Money			myDeltaGains;

		/* Access the Asset Account Bucket */
		BucketList 		myBuckets 	= theAnalysis.getList();
		AssetAccount	myAsset		= (AssetAccount)myBuckets.getAccountDetail(myAccount);

		/* Allocate a Capital event */
		CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

		/* Record the current/delta investment */
		myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
		myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myAmount);

		/* Adjust the total amount invested into this account */
		myAsset.getInvested().subtractAmount(myAmount);
		myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());
		
		/* Get the appropriate price for the account */
		myActPrice  = myPrices.getLatestPrice(myAccount,
										      pEvent.getDate());
		myPrice = myActPrice.getPrice();
		myEvent.addAttribute(CapitalEvent.capitalInitialPrice, myPrice);
		
		/* Determine value of this stock at the current time */
		myValue = myAsset.getUnits().valueAtPrice(myPrice);
		myEvent.addAttribute(CapitalEvent.capitalInitialValue, myValue);
		
		/* Access the current cost */
		myCost = myAsset.getCost();
		
		/* Calculate the portion of the value that creates a large transaction */
		myPortion = myValue.valueAtRate(rateLimit);
		
		/* If this is a large stock waiver (> both valueLimit and rateLimit of value) */
		if ((myAmount.getValue() > valueLimit.getValue()) &&
			(myAmount.getValue() > myPortion.getValue()))
		{
			/* Determine the total value of rights plus share value */
			Money myTotalValue = new Money(myAmount);
			myTotalValue.addAmount(myValue);
			
			/* Determine the reduction as a proportion of the total value */
			myReduction = myAsset.getCost().valueAtWeight(myAmount, myTotalValue);						
		}
		
		/* else this is viewed as small and is taken out of the cost */
		else {
			/* Set the reduction to be the entire amount */
			myReduction = new Money(myAmount);
		}
		
		/* If the reduction is greater than the total cost */
		if (myReduction.getValue() > myCost.getValue()) {
			/* Reduction is the total cost */
			myReduction = new Money(myCost);
		}
		
		/* Calculate the delta cost */
		myDeltaCost = new Money(myReduction);
		myDeltaCost.negate();
		
		/* Record the current/delta cost */
		myEvent.addAttribute(CapitalEvent.capitalInitialCost, myCost);
		myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);

		/* Adjust the cost */
		myCost.addAmount(myDeltaCost);
		myEvent.addAttribute(CapitalEvent.capitalFinalCost, myCost);
		
		/* Calculate the delta gains */
		myDeltaGains = new Money(myAmount);
		myDeltaGains.addAmount(myDeltaCost);

		/* Record the current/delta gains */
		myEvent.addAttribute(CapitalEvent.capitalInitialGains, myAsset.getGains());
		myEvent.addAttribute(CapitalEvent.capitalDeltaGains, myDeltaGains);

		/* Adjust the gains */
		myAsset.getGains().addAmount(myDeltaGains);
		myEvent.addAttribute(CapitalEvent.capitalFinalGains, myAsset.getGains());

		/* Adjust the credit account bucket */
		ActDetail myBucket = myBuckets.getAccountDetail(myCredit);
		myBucket.adjustForCredit(pEvent);
		
		/* Adjust the relevant transaction bucket */
		TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
		myTranBucket.adjustAmount(pEvent);
	}
	
	/**
	 * Process an event that is Stock DeMerger.
	 * This capital event relates to both the Credit and Debit accounts
	 * @param pEvent the event
	 */
	private void processStockDeMerger(Event pEvent) {
		Account		myDebit		= pEvent.getDebit();
		Account		myCredit	= pEvent.getCredit();
		Dilution	myDilution 	= pEvent.getDilution();
		Units		myUnits		= pEvent.getUnits();
		Money		myCost;
		Money		myDeltaCost;
		Money		myNewCost;

		/* Access the Debit Asset Account Bucket */
		BucketList 		myBuckets 	= theAnalysis.getList();
		AssetAccount	myAsset		= (AssetAccount)myBuckets.getAccountDetail(myDebit);

		/* Allocate a Capital event */
		CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

		/* Calculate the diluted value of the Debit account */
		myCost 		= myAsset.getCost();
		myNewCost 	= myCost.getDilutedAmount(myDilution);
		
		/* Calculate the delta to the cost */
		myDeltaCost = new Money(myNewCost);
		myDeltaCost.subtractAmount(myCost);
		
		/* Record the current/delta cost */
		myEvent.addAttribute(CapitalEvent.capitalInitialCost, myCost);
		myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);

		/* Record the new total cost */
		myCost.addAmount(myDeltaCost);
		myEvent.addAttribute(CapitalEvent.capitalFinalCost, myCost);
		
		/* Record the current/delta investment */
		myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
		myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myDeltaCost);

		/* Adjust the investment for the debit account */
		myAsset.getInvested().addAmount(myDeltaCost);
		myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());
		
		/* Access the Credit Asset Account Bucket */
		myAsset		= (AssetAccount)myBuckets.getAccountDetail(myCredit);
		
		/* Allocate a Capital event */
		myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

		/* The deltaCost is transferred to the credit account */
		myDeltaCost = new Money(myDeltaCost);
		myDeltaCost.negate();
		
		/* Record the current/delta cost */
		myEvent.addAttribute(CapitalEvent.capitalInitialCost, myAsset.getCost());
		myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);

		/* Adjust the cost */
		myAsset.getCost().addAmount(myDeltaCost);
		myEvent.addAttribute(CapitalEvent.capitalFinalCost, myAsset.getCost());

		/* Record the current/delta investment */
		myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
		myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myDeltaCost);

		/* Adjust the investment */
		myAsset.getInvested().addAmount(myDeltaCost);
		myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());
		
		/* Record the current/delta units */
		myEvent.addAttribute(CapitalEvent.capitalInitialUnits, myAsset.getUnits());
		myEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myUnits);

		/* Adjust the units for the credit account */
		myAsset.getUnits().addUnits(myUnits);
		myEvent.addAttribute(CapitalEvent.capitalFinalUnits, myAsset.getUnits());
	}
	
	/**
	 * Process an event that is the Cash portion of a StockTakeOver.
	 * This capital event relates only to the Debit Account
	 * @param pEvent the event
	 */
	private void processCashTakeover(Event pEvent) {
		Account			myDebit		= pEvent.getDebit();
		Account			myCredit	= pEvent.getCredit();
		AcctPrice.List 	myPrices	= theData.getPrices();
		Money			myAmount 	= pEvent.getAmount();
		TransactionType	myTrans		= pEvent.getTransType();
		AcctPrice	 	myActPrice;
		Price			myPrice;
		Money			myValue;
		Money			myPortion;
		Money			myReduction;
		Money			myCost;
		Money			myResidualCost;
		Money			myDeltaCost;
		Money			myDeltaGains;

		/* Access the Debit Asset Account Bucket */
		BucketList 		myBuckets 	= theAnalysis.getList();
		AssetAccount	myAsset		= (AssetAccount)myBuckets.getAccountDetail(myDebit);

		/* Allocate a Capital event */
		CapitalEvent myEvent = myAsset.getCapitalEvents().addEvent(pEvent);

		/* Record the current/delta investment */
		myEvent.addAttribute(CapitalEvent.capitalInitialInvest, myAsset.getInvested());
		myEvent.addAttribute(CapitalEvent.capitalDeltaInvest, myAmount);

		/* Adjust the total amount invested into this account */
		myAsset.getInvested().subtractAmount(myAmount);
		myEvent.addAttribute(CapitalEvent.capitalFinalInvest, myAsset.getInvested());
		
		/* Get the appropriate price for the account */
		myActPrice = myPrices.getLatestPrice(myDebit,
										     pEvent.getDate());
		myPrice    = myActPrice.getPrice();
		myEvent.addAttribute(CapitalEvent.capitalInitialPrice, myPrice);
		
		/* Determine value of this stock at the current time */
		myValue = myAsset.getUnits().valueAtPrice(myPrice);
		myEvent.addAttribute(CapitalEvent.capitalInitialValue, myValue);
		
		/* Access the current cost */
		myCost = myAsset.getCost();
		
		/* Calculate the portion of the value that creates a large transaction */
		myPortion = myValue.valueAtRate(rateLimit);
		
		/* If this is a large cash takeover portion (> both valueLimit and rateLimit of value) */
		if ((myAmount.getValue() > valueLimit.getValue()) &&
			(myAmount.getValue() > myPortion.getValue()))
		{
			/* We have to defer the allocation of cost until we know of the Stock TakeOver part */
			myEvent.addAttribute(CapitalEvent.capitalTakeoverCash, myAmount);
		}
		
		/* else this is viewed as small and is taken out of the cost */
		else {
			/* Set the reduction to be the entire amount */
			myReduction = new Money(myAmount);
		
			/* If the reduction is greater than the total cost */
			if (myReduction.getValue() > myCost.getValue()) {
				/* Reduction is the total cost */
				myReduction = new Money(myCost);
			}
				
			/* Calculate the residual cost */
			myResidualCost = new Money(myReduction);
			myResidualCost.negate();
			myResidualCost.addAmount(myCost);

			/* Record the residual cost */
			myEvent.addAttribute(CapitalEvent.capitalTakeoverCost, myResidualCost);

			/* Calculate the delta cost */
			myDeltaCost = new Money(myCost);
			myDeltaCost.negate();

			/* Record the current/delta cost */
			myEvent.addAttribute(CapitalEvent.capitalInitialCost, myCost);
			myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);

			/* Adjust the cost */
			myCost.addAmount(myDeltaCost);
			myEvent.addAttribute(CapitalEvent.capitalFinalCost, myCost);
		
			/* Calculate the gains */
			myDeltaGains = new Money(myAmount);
			myDeltaGains.addAmount(myDeltaCost);

			/* Record the current/delta cost */
			myEvent.addAttribute(CapitalEvent.capitalInitialGains, myAsset.getGains());
			myEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaGains);

			/* Adjust the gained */
			myAsset.getGained().addAmount(myDeltaGains);
			myEvent.addAttribute(CapitalEvent.capitalFinalGains, myAsset.getGains());
		}

		/* Adjust the credit account bucket */
		ActDetail myBucket = myBuckets.getAccountDetail(myCredit);
		myBucket.adjustForCredit(pEvent);
		
		/* Adjust the relevant transaction bucket */
		TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
		myTranBucket.adjustAmount(pEvent);
	}
	
	/**
	 * Process an event that is StockTakeover.
	 * This capital event relates to both the Credit and Debit accounts
	 * In particular it makes reference to the CashTakeOver aspect of the debit account
	 * @param pEvent the event
	 */
	private void processStockTakeover(Event pEvent) {
		Account			myDebit		= pEvent.getDebit();
		Account			myCredit	= pEvent.getCredit();
		AcctPrice.List 	myPrices	= theData.getPrices();
		Units			myUnits		= pEvent.getUnits();
		TransactionType	myTrans		= pEvent.getTransType();
		AcctPrice		myActPrice;
		Price			myPrice;
		Money			myValue;
		Money			myStockCost;
		Money			myCashCost;
		Money			myTotalCost;
		Money			myDeltaCost;
		Money			myDeltaGains;
		Units			myDeltaUnits;
		Money			myResidualCash = null;
		CapitalEvent	myCredEvent;
		CapitalEvent	myDebEvent;
		
		/* Access the Asset Account Buckets */
		BucketList 		myBuckets 	= theAnalysis.getList();
		AssetAccount	myDebAsset	= (AssetAccount)myBuckets.getAccountDetail(myDebit);
		AssetAccount	myCredAsset	= (AssetAccount)myBuckets.getAccountDetail(myCredit);

		/* Access the cash takeover record for the debit account if it exists */
		myDebEvent = myDebAsset.getCapitalEvents().getCashTakeOver();
		
		/* If we have had a cash takeover event */
		if (myDebEvent != null) {
			/* Access the residual cost/cash */
			myResidualCash = (Money)myDebEvent.findAttribute(CapitalEvent.capitalTakeoverCash);
		}
		
		/* Allocate new Capital events */
		myDebEvent  = myDebAsset.getCapitalEvents().addEvent(pEvent);
		myCredEvent = myCredAsset.getCapitalEvents().addEvent(pEvent);
		
		/* If we have a Cash TakeOver component */
		if (myResidualCash != null) {
			/* Get the appropriate price for the credit account */
			myActPrice  = myPrices.getLatestPrice(myCredit,
										   	      pEvent.getDate());
			myPrice = myActPrice.getPrice();
			myDebEvent.addAttribute(CapitalEvent.capitalTakeoverPrice, myPrice);
			
			/* Determine value of the stock part of the takeover */
			myValue = myUnits.valueAtPrice(myPrice);
			myDebEvent.addAttribute(CapitalEvent.capitalTakeoverValue, myValue);
			
			/* Calculate the total cost of the takeover */
			myTotalCost = new Money(myResidualCash);
			myTotalCost.addAmount(myValue);
		
			/* Split the total cost of the takeover between stock and cash */
			myStockCost = myTotalCost.valueAtWeight(myValue, myTotalCost);
			myCashCost  = new Money(myTotalCost);
			myCashCost.subtractAmount(myStockCost);
			
			/* Record the values */
			myDebEvent.addAttribute(CapitalEvent.capitalTakeoverCash, myCashCost);
			myDebEvent.addAttribute(CapitalEvent.capitalTakeoverStock, myStockCost);
			myDebEvent.addAttribute(CapitalEvent.capitalTakeoverTotal, myTotalCost);
			
			/* The Delta Gains is the Amount minus the CashCost */
			myDeltaGains = new Money(myResidualCash);
			myDeltaGains.subtractAmount(myCashCost);

			/* Record the gains */
			myDebEvent.addAttribute(CapitalEvent.capitalInitialGains, myDebAsset.getGains());
			myDebEvent.addAttribute(CapitalEvent.capitalDeltaGains, myDeltaGains);
			myDebAsset.getGained().addAmount(myDeltaGains);
			myDebEvent.addAttribute(CapitalEvent.capitalFinalGains, myDebAsset.getGains());

			/* The cost of the new stock is the stock cost */
			myCredEvent.addAttribute(CapitalEvent.capitalInitialCost, myCredAsset.getCost());
			myCredEvent.addAttribute(CapitalEvent.capitalDeltaCost, myStockCost);
			myCredAsset.getCost().addAmount(myStockCost);
			myCredEvent.addAttribute(CapitalEvent.capitalFinalCost, myCredAsset.getCost());
		}
		
		/* else there is no cash part to this takeover */
		else {
			/* The cost of the new stock is the residual debit cost */
			myDeltaCost = myDebAsset.getCost();
			myCredEvent.addAttribute(CapitalEvent.capitalInitialCost, myCredAsset.getCost());
			myCredEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);
			myCredAsset.getCost().addAmount(myDeltaCost);
			myCredEvent.addAttribute(CapitalEvent.capitalFinalCost, myCredAsset.getCost());
		}		
		
		/* Calculate the delta cost */
		myDeltaCost = new Money(myDebAsset.getCost());
		myDeltaCost.negate();

		/* Record the current/delta cost */
		myDebEvent.addAttribute(CapitalEvent.capitalInitialCost, myDebAsset.getCost());
		myDebEvent.addAttribute(CapitalEvent.capitalDeltaCost, myDeltaCost);

		/* Adjust the cost */
		myDebAsset.getCost().addAmount(myDeltaCost);
		myDebEvent.addAttribute(CapitalEvent.capitalFinalCost, myDebAsset.getCost());
	
		/* Calculate the delta units */
		myDeltaUnits = new Units(myDebAsset.getUnits());
		myDeltaUnits.negate();

		/* Record the current/delta units */
		myDebEvent.addAttribute(CapitalEvent.capitalInitialUnits, myDebAsset.getUnits());
		myDebEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myDeltaUnits);

		/* Adjust the Units */
		myDebAsset.getUnits().addUnits(myDeltaUnits);
		myDebEvent.addAttribute(CapitalEvent.capitalFinalUnits, myDebAsset.getUnits());
	
		/* Record the current/delta units */
		myCredEvent.addAttribute(CapitalEvent.capitalInitialUnits, myCredAsset.getUnits());
		myCredEvent.addAttribute(CapitalEvent.capitalDeltaUnits, myUnits);
		myCredAsset.getUnits().addUnits(myUnits);
		myCredEvent.addAttribute(CapitalEvent.capitalFinalUnits, myCredAsset.getUnits());
	
		/* Adjust the relevant transaction bucket */
		TransDetail myTranBucket = myBuckets.getTransDetail(myTrans);
		myTranBucket.adjustAmount(pEvent);
	}	
}
