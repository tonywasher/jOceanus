package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.finance.views.EventAnalysis;
import uk.co.tolcroft.finance.views.MetaAnalysis;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.Date;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.security.SecureManager;

public class FinanceData extends DataSet<ItemType> {
	/* Members */
	private AccountType.List		theActTypes   	= null;
	private TransactionType.List	theTransTypes 	= null;
	private TaxType.List			theTaxTypes   	= null;
	private TaxRegime.List			theTaxRegimes 	= null;
    private Frequency.List			theFrequencys 	= null;
    private TaxYear.List			theTaxYears   	= null;
    private Account.List			theAccounts  	= null;
    private AcctRate.List			theRates      	= null;
    private AcctPrice.List			thePrices     	= null;
    private Pattern.List			thePatterns   	= null; 
	private Event.List				theEvents     	= null;
    private Date.Range				theDateRange  	= null;
    private LoadState				theLoadState  	= LoadState.INITIAL;

    /* Access Methods */
    public AccountType.List 	getAccountTypes() 	{ return theActTypes; }
	public TransactionType.List getTransTypes() 	{ return theTransTypes; }
	public TaxType.List 		getTaxTypes() 		{ return theTaxTypes; }
	public TaxRegime.List 		getTaxRegimes() 	{ return theTaxRegimes; }
	public Frequency.List 		getFrequencys() 	{ return theFrequencys; }
	public TaxYear.List 		getTaxYears()  		{ return theTaxYears; }
	public Account.List 		getAccounts()  		{ return theAccounts; }
	public AcctRate.List 		getRates()  		{ return theRates; }
	public AcctPrice.List 		getPrices()  		{ return thePrices; }
	public Pattern.List 		getPatterns()  		{ return thePatterns; }
	public Event.List 			getEvents()  		{ return theEvents; }
	public Date.Range 			getDateRange()  	{ return theDateRange; }
	public LoadState 			getLoadState()  	{ return theLoadState; }

	/**
	 *  Standard constructor
	 */ 
	public FinanceData(SecureManager pSecurity) {
		/* Call Super-constructor */
		super(pSecurity, ItemType.class);
		
		/* Create the empty lists */
		theActTypes    = new AccountType.List(this);
		theTransTypes  = new TransactionType.List(this);
		theTaxTypes    = new TaxType.List(this);
		theTaxRegimes  = new TaxRegime.List(this);
		theFrequencys  = new Frequency.List(this);
		theTaxYears    = new TaxYear.List(this);
		theAccounts    = new Account.List(this);
		theRates       = new AcctRate.List(this);
		thePrices      = new AcctPrice.List(this);
		thePatterns    = new Pattern.List(this);
		theEvents      = new Event.List(this);
		
		/* Declare the lists */
		declareLists();
	}
	
	/**
	 * Constructor for a cloned DataSet
	 * @param pSource the source DataSet
	 */
	private FinanceData(FinanceData pSource) { super(pSource); }
	
	/**
	 * Construct an update extract for a FinanceData Set.
	 */
	public FinanceData getUpdateSet() {
		/* Build an empty DataSet */
		FinanceData myExtract = new FinanceData(this);
		
		/* Obtain underlying updates */
		getUpdateSet(myExtract);
		
		/* Build the static extract */
		myExtract.theActTypes   = new AccountType.List(theActTypes, 		ListStyle.UPDATE);
		myExtract.theTransTypes = new TransactionType.List(theTransTypes, 	ListStyle.UPDATE);
		myExtract.theTaxTypes   = new TaxType.List(theTaxTypes, 			ListStyle.UPDATE);
		myExtract.theTaxRegimes = new TaxRegime.List(theTaxRegimes, 		ListStyle.UPDATE);
		myExtract.theFrequencys = new Frequency.List(theFrequencys, 		ListStyle.UPDATE);
		
		/* Build the data extract */
		myExtract.theTaxYears   = new TaxYear.List(theTaxYears, ListStyle.UPDATE);
		myExtract.theAccounts   = new Account.List(theAccounts, ListStyle.UPDATE);
		myExtract.theRates      = new AcctRate.List(theRates, 	ListStyle.UPDATE);
		myExtract.thePrices     = new AcctPrice.List(thePrices, ListStyle.UPDATE);
		myExtract.thePatterns   = new Pattern.List(thePatterns, ListStyle.UPDATE);
		myExtract.theEvents     = new Event.List(theEvents, 	ListStyle.UPDATE);

		/* Declare the lists */
		myExtract.declareLists();
		
		/* Return the extract */
		return myExtract;
	}
	
	/**
	 * Construct a difference extract between two DataSets.
	 * The difference extract will only contain items that differ between the two DataSets.
	 * Items that are in the new list, but not in the old list will be viewed as inserted.
	 * Items that are in the old list but not in the new list will be viewed as deleted.
	 * Items that are in both list but differ will be viewed as changed 
	 * @param pOld The DataSet to compare to 
	 */
	public FinanceData getDifferenceSet(FinanceData pOld) {
		/* Build an empty DataSet */
		FinanceData myDiffers = new FinanceData(this);
		
		/* Obtain underlying differences */
		getDifferenceSet(myDiffers, pOld);
		
		/* Build the static differences */
		myDiffers.theActTypes  	= new AccountType.List(theActTypes, 		pOld.getAccountTypes());
		myDiffers.theTransTypes = new TransactionType.List(theTransTypes, 	pOld.getTransTypes());
		myDiffers.theTaxTypes	= new TaxType.List(theTaxTypes, 			pOld.getTaxTypes());
		myDiffers.theTaxRegimes	= new TaxRegime.List(theTaxRegimes, 		pOld.getTaxRegimes());
		myDiffers.theFrequencys	= new Frequency.List(theFrequencys, 		pOld.getFrequencys());

		/* Build the data differences */
		myDiffers.theTaxYears  	= new TaxYear.List(theTaxYears, pOld.getTaxYears());
		myDiffers.theAccounts  	= new Account.List(theAccounts, pOld.getAccounts());
		myDiffers.theRates	  	= new AcctRate.List(theRates, 	pOld.getRates());
		myDiffers.thePrices	  	= new AcctPrice.List(thePrices, pOld.getPrices());
		myDiffers.thePatterns  	= new Pattern.List(thePatterns, pOld.getPatterns());
		myDiffers.theEvents	  	= new Event.List(theEvents, 	pOld.getEvents());

		/* Declare the lists */
		myDiffers.declareLists();
		
		/* Return the differences */
		return myDiffers;
	}

	/**
	 * ReBase this data set against an earlier version.
	 * @param pOld The old data to reBase against 
	 */
	public void reBase(FinanceData pOld) {
		/* Call super-class */
		super.reBase(pOld);
		
		/* ReBase the static items */
		theActTypes.reBase(pOld.getAccountTypes());
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
	 * Declare lists
	 */
	private void declareLists() {
		/* Declare the lists */
		addList(ItemType.AccountType, 	theActTypes);
		addList(ItemType.TransType, 	theTransTypes);
		addList(ItemType.TaxType, 		theTaxTypes);
		addList(ItemType.TaxRegime, 	theTaxRegimes);
		addList(ItemType.Frequency,		theFrequencys);
		addList(ItemType.TaxYear,		theTaxYears);
		addList(ItemType.Account,		theAccounts);
		addList(ItemType.Rate,			theRates);
		addList(ItemType.Price,			thePrices);
		addList(ItemType.Pattern,		thePatterns);
		addList(ItemType.Event,			theEvents);		
	}

	/**
	 * Calculate the allowed Date Range
	 */
	public void calculateDateRange() {
		theDateRange = theTaxYears.getRange();
	}
	
	/**
	 * Analyse the data
	 * @param pView the data view
	 * @return the full analysis of the data
	 */
	public EventAnalysis analyseData(View			pView) throws Exception {
		EventAnalysis		myAnalysis;
		MetaAnalysis 		myMetaAnalysis;
						
		/* Update INITIAL Load status */
		if (theLoadState == LoadState.INITIAL)
			theLoadState = LoadState.FINAL;
		
		/* Reset the flags on the accounts and tax years */
		theAccounts.reset();
		theTaxYears.reset();
		
		/* Create the analysis */
		myAnalysis = new EventAnalysis(pView, this);

		/* Note active rates */
		theRates.markActiveRates();
		
		/* Note active prices */
		thePrices.markActivePrices();
		
		/* Note active patterns */
		thePatterns.markActivePatterns();
		
		/* Access the most recent metaAnalysis */
		myMetaAnalysis = myAnalysis.getMetaAnalysis();
		
		/* Note active accounts by asset */
		if (myMetaAnalysis != null)
			myMetaAnalysis.markActiveAccounts();
		
		/* Note active accounts */
		theAccounts.markActiveAccounts();
		
		/* Note that we are now fully loaded */
		theLoadState = LoadState.LOADED;
		
		/* Return the analysis */
		return myAnalysis;
	}
	
	
	/**
	 * Obtain DataList for an item type
	 * @param pItemType the type of items
	 * @return the list of items
	 */
	public DataList<?> getDataList(ItemType pItemType) {
		/* Switch on item type */
		switch(pItemType) {
			case AccountType:	return theActTypes;
			case TransType:		return theTransTypes;
			case TaxType:		return theTaxTypes;
			case TaxRegime:		return theTaxRegimes;
			case Frequency:		return theFrequencys;
			case TaxYear:		return theTaxYears;
			case Account:		return theAccounts;
			case Rate:			return theRates;
			case Price:			return thePrices;
			case Pattern:		return thePatterns;
			case Event:			return theEvents;
			default:			return null;
		}
	}
	
	/** 
	 * Enumeration of load states of data
	 */
	public static enum LoadState {
		/**
		 * Initial loading, with parental account links and close-ability not yet done
		 */
		INITIAL,
		
		/**
		 *  Final loading with parental links and close-ability done 
		 */
		FINAL,
		
		/**
		 * Fully loaded
		 */
		LOADED;
	}	
}