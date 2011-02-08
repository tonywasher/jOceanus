package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.finance.views.AnalysisYear;
import uk.co.tolcroft.finance.views.AssetAnalysis;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.security.*;

public class DataSet implements htmlDumpable {
	private SecurityControl			theControl	  = null;
	private Static.List				theStatic     = null;
	private AccountType.List		theActTypes   = null;
	private TransactionType.List	theTransTypes = null;
	private TaxType.List			theTaxTypes   = null;
	private TaxRegime.List			theTaxRegimes = null;
    private Frequency.List			theFrequencys = null;
    private TaxYear.List			theTaxYears   = null;
    private Account.List			theAccounts   = null;
    private Rate.List				theRates      = null;
    private Price.List				thePrices     = null;
    private Pattern.List			thePatterns   = null; 
	private Event.List				theEvents     = null;
    private Date.Range				theDateRange  = null;
    private LoadState				theLoadState  = LoadState.INITIAL;

    /* Access methods */
	public SecurityControl		getSecurity() 		{ return theControl; }
	public Static.List 			getStatic() 		{ return theStatic; }
	public AccountType.List 	getAccountTypes() 	{ return theActTypes; }
	public TransactionType.List getTransTypes() 	{ return theTransTypes; }
	public TaxType.List 		getTaxTypes() 		{ return theTaxTypes; }
	public TaxRegime.List 		getTaxRegimes() 	{ return theTaxRegimes; }
	public Frequency.List 		getFrequencys() 	{ return theFrequencys; }
	public TaxYear.List 		getTaxYears()  		{ return theTaxYears; }
	public Account.List 		getAccounts()  		{ return theAccounts; }
	public Rate.List 			getRates()  		{ return theRates; }
	public Price.List 			getPrices()  		{ return thePrices; }
	public Pattern.List 		getPatterns()  		{ return thePatterns; }
	public Event.List 			getEvents()  		{ return theEvents; }
	public Date.Range 			getDateRange()  	{ return theDateRange; }
	public LoadState 			getLoadState()  	{ return theLoadState; }

	/**
	 *  Standard constructor
	 *  @param pControl security Control
	 */ 
	public DataSet(SecurityControl pControl) {
		/* Record the security Control */
		theControl 	  = pControl;
		
		/* Create the empty lists */
		theStatic     = new Static.List(this);
		theActTypes   = new AccountType.List(this);
		theTransTypes = new TransactionType.List(this);
		theTaxTypes   = new TaxType.List(this);
		theTaxRegimes = new TaxRegime.List(this);
		theFrequencys = new Frequency.List(this);
		theTaxYears   = new TaxYear.List(this);
		theAccounts   = new Account.List(this);
		theRates      = new Rate.List(this);
		thePrices     = new Price.List(this);
		thePatterns   = new Pattern.List(this);
		theEvents     = new Event.List(this);
	}
	
	/**
	 * Construct a difference extract between two DataSets.
	 * The difference extract will only contain items that differ between the two DataSets.
	 * Items that are in the new list, but not in the old list will be viewed as inserted.
	 * Items that are in the old list but not in the new list will be viewed as deleted.
	 * Items that are in both list but differ will be viewed as changed 
	 * 
	 * @param pNew The new list to extract from 
	 * @param pOld The old list to extract from 
	 */
	public DataSet(DataSet pNew, DataSet pOld) {
		/* Copy control from new */
		theControl 	  = pNew.getSecurity();
		
		/* Build the static differences */
		theStatic     = new Static.List(pNew.getStatic(), pOld.getStatic());
		theActTypes   = new AccountType.List(pNew.getAccountTypes(), pOld.getAccountTypes());
		theTransTypes = new TransactionType.List(pNew.getTransTypes(), pOld.getTransTypes());
		theTaxTypes   = new TaxType.List(pNew.getTaxTypes(), pOld.getTaxTypes());
		theTaxRegimes = new TaxRegime.List(pNew.getTaxRegimes(), pOld.getTaxRegimes());
		theFrequencys = new Frequency.List(pNew.getFrequencys(), pOld.getFrequencys());
		
		/* Build the data differences */
		theTaxYears   = new TaxYear.List(pNew.getTaxYears(), pOld.getTaxYears());
		theAccounts   = new Account.List(pNew.getAccounts(), pOld.getAccounts());
		theRates      = new Rate.List(pNew.getRates(), pOld.getRates());
		thePrices     = new Price.List(pNew.getPrices(), pOld.getPrices());
		thePatterns   = new Pattern.List(pNew.getPatterns(), pOld.getPatterns());
		theEvents     = new Event.List(pNew.getEvents(), pOld.getEvents());
	}
	
	/**
	 * ReBase this data set against an earlier version.
	 * 
	 * @param pOld The old data to reBase against 
	 */
	public void reBase(DataSet pOld) {
		/* ReBase the static items */
		theStatic.reBase(pOld.getStatic());
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
	 * Compare this data-set to another to establish equality.
	 * 
	 * @param pThat The Data-set to compare to
	 * @return <code>true</code> if the data-sets are identical, 
	 * <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is an Event */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a DataSet */
		DataSet myThat = (DataSet)pThat;
		
		/* Compare static data */
		if (!theStatic.equals(myThat.getStatic())) return false;
		if (!theActTypes.equals(myThat.getAccountTypes())) return false;
		if (!theTransTypes.equals(myThat.getTransTypes())) return false;
		if (!theTaxTypes.equals(myThat.getTaxTypes())) return false;
		if (!theTaxRegimes.equals(myThat.getTaxRegimes())) return false;
		if (!theFrequencys.equals(myThat.getFrequencys())) return false;
		
		/* Compare dynamic data */
		if (!theTaxYears.equals(myThat.getTaxYears())) return false;
		if (!theAccounts.equals(myThat.getAccounts())) return false;
		if (!theRates.equals(myThat.getRates())) return false;
		if (!thePrices.equals(myThat.getPrices())) return false;
		if (!thePatterns.equals(myThat.getPatterns())) return false;
		if (!theEvents.equals(myThat.getEvents())) return false;
		
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
		myString.append(theStatic.toHTMLString());
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
	public boolean isEmpty() {
		/* Local variables */
		boolean isEmpty;
		
		/* Determine whether the data is empty */
		isEmpty =	theStatic.isEmpty()     &&
					theActTypes.isEmpty()   &&
					theTransTypes.isEmpty() &&
					theTaxTypes.isEmpty()   &&
					theTaxRegimes.isEmpty() &&
					theFrequencys.isEmpty() &&
					theTaxYears.isEmpty()   &&
					theAccounts.isEmpty()   &&
					theRates.isEmpty()      &&
					thePrices.isEmpty()     &&
					thePatterns.isEmpty()   &&
					theEvents.isEmpty();
		
		/* Return the indication */
		return isEmpty;
	}
	
	/**
	 * Determine whether the Data-set has updates
	 * @return <code>true</code> if the Data-set has updates, <code>false</code> if not
	 */
	public boolean hasUpdates() {
			
		/* Determine whether we have updates */
		if (theStatic.hasUpdates()) return true;
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
	public void calculateDateRange() {
		theDateRange = theTaxYears.getRange();
	}
		
	/**
	 * Access the symmetric Key
	 */
	public SymmetricKey			getKey() 		{ 
		Static.List		myStaticList;
		Static			myStatic;
		
		DataList<Static>.ListIterator myIterator;

		/* Access the first static element */
		myStaticList = getStatic();
		myIterator	 = myStaticList.listIterator();
		myStatic	 = myIterator.next();
		
		/* Return the key */
		return (myStatic == null) ? null : myStatic.getKey();
	}
	
	/**
	 * Set the symmetric Key
	 */
	public void setKey(SymmetricKey pKey)	{ 
		Static.List		myStaticList;
		Static			myStatic;
		
		DataList<Static>.ListIterator myIterator;

		/* Access the first static element */
		myStaticList = getStatic();
		myIterator	 = myStaticList.listIterator();
		myStatic	 = myIterator.next();
		
		/* Set the key */
		if (myStatic != null) myStatic.setKey(pKey);
	}
	
	/**
	 * Analyse the data
	 * @return the analysis of the year
	 */
	public AnalysisYear.List analyseData() throws Exception {
		AssetAnalysis 		myAssets 	= null;
		AnalysisYear.List	myList		= null;
						
		/* Update INITIAL Load status */
		if (theLoadState == LoadState.INITIAL)
			theLoadState = LoadState.FINAL;
		
		/* Reset the flags on the accounts and tax years*/
		theAccounts.reset();
		theTaxYears.reset();
		
		/* Create the analysis */
		myList = new AnalysisYear.List(this);

		/* Note active rates */
		theRates.markActiveRates();
		
		/* Note active prices */
		thePrices.markActivePrices();
		
		/* Note active patterns */
		thePatterns.markActivePatterns();
		
		/* Access the most recent asset report */
		myAssets = myList.getLastAssets();
		
		/* Note active accounts by asset */
		if (myAssets != null)
			myAssets.getBuckets().markActiveAccounts();
		
		/* Note active accounts */
		theAccounts.markActiveAccounts();
		
		/* Note that we are now fully loaded */
		theLoadState = LoadState.LOADED;
		
		/* Return the analysis */
		return myList;
	}

	/**
	 * Determines whether an event can be valid
	 * 
	 * @param pTrans The transaction type of the event
	 * @param pType The account type of the event
	 * @param isCredit is the account a credit or a debit
	 * @return valid true/false 
	 */
	public static boolean isValidEvent(TransactionType  pTrans,
			                     	   AccountType		pType,
			                           boolean          isCredit) {
		boolean myResult = false;

		/* Market is always false */
		if (pType.isMarket())
			return false;
		
		/* Switch on the TransType */
		switch (pTrans.getTranClass()) {
			case TAXFREEINCOME:
				if (!isCredit) myResult = (pType.isExternal() && !pType.isCash());
				else           myResult = !pType.isExternal();
				break;
			case TAXABLEGAIN:
				if (!isCredit) myResult = pType.isLifeBond();
				else           myResult = !pType.isExternal();
				break;
			case DIVIDEND:
				if (!isCredit) myResult = pType.isDividend();
				else           myResult = !pType.isExternal();
				break;
			case STOCKDEMERGER:
			case STOCKSPLIT:
			case STOCKTAKEOVER:
				myResult = pType.isShares();
				break;
			case STOCKRIGHTWAIVED:
			case CASHTAKEOVER:
				isCredit = !isCredit;
			case STOCKRIGHTTAKEN:
				if (!isCredit) myResult = (pType.isMoney() || pType.isDeferred());
				else           myResult = pType.isShares();
				break;
			case INTEREST:
				if (!isCredit) myResult = pType.isMoney();
				else           myResult = pType.isMoney();
				break;
			case TAXEDINCOME:
				if (!isCredit) myResult = pType.isEmployer();
				else           myResult = ((pType.isMoney()) || (pType.isDeferred()));
				break;
			case NATINSURANCE:
				if (!isCredit) myResult = pType.isEmployer();
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
				if (!isCredit) myResult = pType.isEmployer();
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
	
	/**
	 * Enumeration of load states of data
	 */
	protected static enum LoadState {
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
