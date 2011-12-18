package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.finance.views.EventAnalysis;
import uk.co.tolcroft.finance.views.MetaAnalysis;
import uk.co.tolcroft.models.DateDay;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.security.SecureManager;
import uk.co.tolcroft.models.views.DataControl;

public class FinanceData extends DataSet<FinanceData> {
	/* Members */
	private AccountType.List		theActTypes   	= null;
	private TransactionType.List	theTransTypes 	= null;
	private TaxType.List			theTaxTypes   	= null;
	private TaxRegime.List			theTaxRegimes 	= null;
    private Frequency.List			theFrequencys 	= null;
    private EventInfoType.List		theInfoTypes 	= null;
    private TaxYear.List			theTaxYears   	= null;
    private Account.List			theAccounts  	= null;
    private AcctRate.List			theRates      	= null;
    private AcctPrice.List			thePrices     	= null;
    private Pattern.List			thePatterns   	= null; 
	private Event.List				theEvents     	= null;
	private EventValue.List			theEventValues 	= null;
	private EventData.List			theEventData 	= null;
    private DateDay.Range				theDateRange  	= null;
    private EventAnalysis			theAnalysis		= null;
    private LoadState				theLoadState  	= LoadState.INITIAL;

    /* Access Methods */
    public AccountType.List 	getAccountTypes() 	{ return theActTypes; }
	public TransactionType.List getTransTypes() 	{ return theTransTypes; }
	public TaxType.List 		getTaxTypes() 		{ return theTaxTypes; }
	public TaxRegime.List 		getTaxRegimes() 	{ return theTaxRegimes; }
	public Frequency.List 		getFrequencys() 	{ return theFrequencys; }
	public EventInfoType.List 	getInfoTypes() 		{ return theInfoTypes; }
	public TaxYear.List 		getTaxYears()  		{ return theTaxYears; }
	public Account.List 		getAccounts()  		{ return theAccounts; }
	public AcctRate.List 		getRates()  		{ return theRates; }
	public AcctPrice.List 		getPrices()  		{ return thePrices; }
	public Pattern.List 		getPatterns()  		{ return thePatterns; }
	public Event.List 			getEvents()  		{ return theEvents; }
	public EventValue.List 		getEventValues()  	{ return theEventValues; }
	public EventData.List 		getEventData()  	{ return theEventData; }
	public DateDay.Range 			getDateRange()  	{ return theDateRange; }
	public EventAnalysis		getAnalysis()  		{ return theAnalysis; }
	public LoadState 			getLoadState()  	{ return theLoadState; }

	/**
	 *  Standard constructor
	 */ 
	public FinanceData(SecureManager pSecurity) {
		/* Call Super-constructor */
		super(pSecurity);
		
		/* Create the empty lists */
		theActTypes    = new AccountType.List(this);
		theTransTypes  = new TransactionType.List(this);
		theTaxTypes    = new TaxType.List(this);
		theTaxRegimes  = new TaxRegime.List(this);
		theFrequencys  = new Frequency.List(this);
		theInfoTypes   = new EventInfoType.List(this);
		theTaxYears    = new TaxYear.List(this);
		theAccounts    = new Account.List(this);
		theRates       = new AcctRate.List(this);
		thePrices      = new AcctPrice.List(this);
		thePatterns    = new Pattern.List(this);
		theEvents      = new Event.List(this);
		theEventData   = new EventData.List(this);
		theEventValues = new EventValue.List(this);
		
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
		myExtract.theActTypes   = theActTypes.getUpdateList();
		myExtract.theTransTypes = theTransTypes.getUpdateList();
		myExtract.theTaxTypes   = theTaxTypes.getUpdateList();
		myExtract.theTaxRegimes = theTaxRegimes.getUpdateList();
		myExtract.theFrequencys = theFrequencys.getUpdateList();
		myExtract.theInfoTypes  = theInfoTypes.getUpdateList();
		
		/* Build the data extract */
		myExtract.theTaxYears    = theTaxYears.getUpdateList();
		myExtract.theAccounts    = theAccounts.getUpdateList();
		myExtract.theRates       = theRates.getUpdateList();
		myExtract.thePrices      = thePrices.getUpdateList();
		myExtract.thePatterns    = thePatterns.getUpdateList();
		myExtract.theEvents      = theEvents.getUpdateList();
		myExtract.theEventData   = theEventData.getUpdateList();
		myExtract.theEventValues = theEventValues.getUpdateList();

		/* Declare the lists */
		myExtract.declareLists();
		
		/* Return the extract */
		return myExtract;
	}
	
	/**
	 * Construct a Deep Copy for a DataSet.
	 */
	public FinanceData	getDeepCopy() {
		/* Build an empty DataSet */
		FinanceData myExtract = new FinanceData(this);
		
		/* Obtain underlying updates */
		myExtract.getDeepCopy(this);
		
		/* Build the static extract */
		myExtract.theActTypes   = theActTypes.getDeepCopy(myExtract);
		myExtract.theTransTypes = theTransTypes.getDeepCopy(myExtract);
		myExtract.theTaxTypes   = theTaxTypes.getDeepCopy(myExtract);
		myExtract.theTaxRegimes = theTaxRegimes.getDeepCopy(myExtract);
		myExtract.theFrequencys = theFrequencys.getDeepCopy(myExtract);
		myExtract.theInfoTypes  = theInfoTypes.getDeepCopy(myExtract);
		
		/* Build the data extract */
		myExtract.theTaxYears    = theTaxYears.getDeepCopy(myExtract);
		myExtract.theAccounts    = theAccounts.getDeepCopy(myExtract);
		myExtract.theRates       = theRates.getDeepCopy(myExtract);
		myExtract.thePrices      = thePrices.getDeepCopy(myExtract);
		myExtract.thePatterns    = thePatterns.getDeepCopy(myExtract);
		myExtract.theEvents      = theEvents.getDeepCopy(myExtract);
		myExtract.theEventData   = theEventData.getDeepCopy(myExtract);
		myExtract.theEventValues = theEventValues.getDeepCopy(myExtract);

		/* Declare the lists */
		myExtract.declareLists();
		
		/* Return the extract */
		return myExtract;		
	}
	
	/**
	 * Construct a difference extract between two DataSets.
	 * The difference extract will only contain items that differ between the two DataSets.
	 * Items that are in this list, but not in the old list will be viewed as inserted.
	 * Items that are in the old list but not in this list will be viewed as deleted.
	 * Items that are in both lists but differ will be viewed as changed 
	 * @param pOld The DataSet to compare to 
	 */
	public FinanceData getDifferenceSet(FinanceData pOld) throws ModelException {
		/* Make sure that the DataSet if the same type */
		if (!(pOld instanceof FinanceData)) 
			throw new ModelException(ExceptionClass.LOGIC,
								"Invalid DataSet type");
		
		/* Cast correctly */
		FinanceData myOld = (FinanceData)pOld;
		
		/* Build an empty DataSet */
		FinanceData myDiffers = new FinanceData(this);
		
		/* Obtain underlying differences */
		myDiffers.getDifferenceSet(this, myOld);
				
		/* Build the static differences */
		myDiffers.theActTypes  	= theActTypes.getDifferences(myOld.getAccountTypes());
		myDiffers.theTransTypes = theTransTypes.getDifferences(myOld.getTransTypes());
		myDiffers.theTaxTypes	= theTaxTypes.getDifferences(myOld.getTaxTypes());
		myDiffers.theTaxRegimes	= theTaxRegimes.getDifferences(myOld.getTaxRegimes());
		myDiffers.theFrequencys	= theFrequencys.getDifferences(myOld.getFrequencys());
		myDiffers.theInfoTypes	= theInfoTypes.getDifferences(myOld.getInfoTypes());

		/* Build the data differences */
		myDiffers.theTaxYears  	 = theTaxYears.getDifferences(myOld.getTaxYears());
		myDiffers.theAccounts  	 = theAccounts.getDifferences(myOld.getAccounts());
		myDiffers.theRates	  	 = theRates.getDifferences(myOld.getRates());
		myDiffers.thePrices	  	 = thePrices.getDifferences(myOld.getPrices());
		myDiffers.thePatterns  	 = thePatterns.getDifferences(myOld.getPatterns());
		myDiffers.theEvents	  	 = theEvents.getDifferences(myOld.getEvents());
		myDiffers.theEventData	 = theEventData.getDifferences(myOld.getEventData());
		myDiffers.theEventValues = theEventValues.getDifferences(myOld.getEventValues());

		/* Declare the lists */
		myDiffers.declareLists();
		
		/* Return the differences */
		return myDiffers;
	}

	/**
	 * ReBase this data set against an earlier version.
	 * @param pOld The old data to reBase against 
	 */
	public void reBase(FinanceData pOld) throws ModelException {
		/* Make sure that the DataSet if the same type */
		if (!(pOld instanceof FinanceData)) 
			throw new ModelException(ExceptionClass.LOGIC,
								"Invalid DataSet type");
		
		/* Cast correctly */
		FinanceData myOld = (FinanceData)pOld;
		
		/* Call super-class */
		super.reBase(myOld);
		
		/* ReBase the static items */
		theActTypes.reBase(myOld.getAccountTypes());
		theTransTypes.reBase(myOld.getTransTypes());
		theTaxTypes.reBase(myOld.getTaxTypes());
		theTaxRegimes.reBase(myOld.getTaxRegimes());
		theFrequencys.reBase(myOld.getFrequencys());
		theInfoTypes.reBase(myOld.getInfoTypes());

		/* ReBase the data items */
		theTaxYears.reBase(myOld.getTaxYears());
		theAccounts.reBase(myOld.getAccounts());
		theRates.reBase(myOld.getRates());
		thePrices.reBase(myOld.getPrices());
		thePatterns.reBase(myOld.getPatterns());
		theEvents.reBase(myOld.getEvents());
		theEventData.reBase(myOld.getEventData());
		theEventValues.reBase(myOld.getEventValues());
	}
	
	/**
	 * Declare lists
	 */
	private void declareLists() {
		/* Declare the lists */
		addList(theActTypes);
		addList(theTransTypes);
		addList(theTaxTypes);
		addList(theTaxRegimes);
		addList(theFrequencys);
		addList(theInfoTypes);
		addList(theTaxYears);
		addList(theAccounts);
		addList(theRates);
		addList(thePrices);
		addList(thePatterns);
		addList(theEvents);		
		addList(theEventData);		
		addList(theEventValues);		
	}

	/**
	 * Calculate the allowed Date Range
	 */
	public void calculateDateRange() {
		theDateRange = theTaxYears.getRange();
		theEvents.setRange(theDateRange);
	}
	
	/**
	 * Analyse the data
	 * @param pControl the data view
	 */
	public void analyseData(DataControl<?>	pControl) throws ModelException {
		MetaAnalysis 		myMetaAnalysis;
						
		/* Update INITIAL Load status */
		if (theLoadState == LoadState.INITIAL)
			theLoadState = LoadState.FINAL;
		
		/* Reset the flags on static data (ignoring TaxTypes) */
		theActTypes.clearActive();
		theTransTypes.clearActive();
		theTaxRegimes.clearActive();
		theFrequencys.clearActive();
		
		/* Reset the flags on the accounts and tax years */
		theAccounts.clearActive();
		theTaxYears.clearActive();
		
		/* Note active items referenced by tax years */
		theTaxYears.markActiveItems();
		
		/* Create the analysis */
		theAnalysis = new EventAnalysis(pControl, this);

		/* Note active items referenced by rates */
		theRates.markActiveItems();
		
		/* Note active items referenced by prices */
		thePrices.markActiveItems();
		
		/* Mark active items referenced by patterns */
		thePatterns.markActiveItems();
		
		/* Access the most recent metaAnalysis */
		myMetaAnalysis = theAnalysis.getMetaAnalysis();
		
		/* Note active accounts by asset */
		if (myMetaAnalysis != null)
			myMetaAnalysis.markActiveAccounts();
		
		/* Note active accounts */
		theAccounts.markActiveItems();
		
		/* Note that we are now fully loaded */
		theLoadState = LoadState.LOADED;
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
