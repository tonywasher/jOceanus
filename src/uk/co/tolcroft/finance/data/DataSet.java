package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.views.DebugManager.DebugEntry;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.ListStyle;
import uk.co.tolcroft.security.*;

public class DataSet implements htmlDumpable {
	private SecureManager			theSecurity   	= null;
	private ControlKey.List			theControlKeys  = null;
	private DataKey.List			theDataKeys		= null;
	private ControlData.List		theControlData 	= null;
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

    /* Access methods */
	protected SecureManager		getSecurity() 		{ return theSecurity; }
	public ControlKey.List 		getControlKeys() 	{ return theControlKeys; }
	public DataKey.List 		getDataKeys() 		{ return theDataKeys; }
	public ControlData.List 	getControlData() 	{ return theControlData; }
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
	public DataSet(SecureManager pSecurity) {
		/* Store the security manager */
		theSecurity   = pSecurity;
		
		/* Create the empty lists */
		theControlKeys = new ControlKey.List(this);
		theDataKeys    = new DataKey.List(this);
		theControlData = new ControlData.List(this);
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
	}
	
	/**
	 * Construct an update extract for a DataSet.
	 * @param pSource The underlying DataSet 
	 */
	public DataSet(DataSet pSource) {
		/* Build the static differences */
		theControlKeys  = new ControlKey.List(pSource.getControlKeys(), ListStyle.UPDATE);
		theDataKeys   	= new DataKey.List(pSource.getDataKeys(), ListStyle.UPDATE);
		theControlData 	= new ControlData.List(pSource.getControlData(), ListStyle.UPDATE);
		theActTypes   	= new AccountType.List(pSource.getAccountTypes(), ListStyle.UPDATE);
		theTransTypes 	= new TransactionType.List(pSource.getTransTypes(), ListStyle.UPDATE);
		theTaxTypes   	= new TaxType.List(pSource.getTaxTypes(), ListStyle.UPDATE);
		theTaxRegimes 	= new TaxRegime.List(pSource.getTaxRegimes(), ListStyle.UPDATE);
		theFrequencys 	= new Frequency.List(pSource.getFrequencys(), ListStyle.UPDATE);
		
		/* Build the data differences */
		theTaxYears   = new TaxYear.List(pSource.getTaxYears(), ListStyle.UPDATE);
		theAccounts   = new Account.List(pSource.getAccounts(), ListStyle.UPDATE);
		theRates      = new AcctRate.List(pSource.getRates(), ListStyle.UPDATE);
		thePrices     = new AcctPrice.List(pSource.getPrices(), ListStyle.UPDATE);
		thePatterns   = new Pattern.List(pSource.getPatterns(), ListStyle.UPDATE);
		theEvents     = new Event.List(pSource.getEvents(), ListStyle.UPDATE);
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
		/* Build the static differences */
		theControlKeys  = new ControlKey.List(pNew.getControlKeys(), pOld.getControlKeys());
		theDataKeys   	= new DataKey.List(pNew.getDataKeys(), pOld.getDataKeys());
		theControlData	= new ControlData.List(pNew.getControlData(), pOld.getControlData());
		theActTypes   	= new AccountType.List(pNew.getAccountTypes(), pOld.getAccountTypes());
		theTransTypes 	= new TransactionType.List(pNew.getTransTypes(), pOld.getTransTypes());
		theTaxTypes   	= new TaxType.List(pNew.getTaxTypes(), pOld.getTaxTypes());
		theTaxRegimes 	= new TaxRegime.List(pNew.getTaxRegimes(), pOld.getTaxRegimes());
		theFrequencys 	= new Frequency.List(pNew.getFrequencys(), pOld.getFrequencys());
		
		/* Build the data differences */
		theTaxYears   = new TaxYear.List(pNew.getTaxYears(), pOld.getTaxYears());
		theAccounts   = new Account.List(pNew.getAccounts(), pOld.getAccounts());
		theRates      = new AcctRate.List(pNew.getRates(), pOld.getRates());
		thePrices     = new AcctPrice.List(pNew.getPrices(), pOld.getPrices());
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
		theControlKeys.reBase(pOld.getControlKeys());
		theDataKeys.reBase(pOld.getDataKeys());
		theControlData.reBase(pOld.getControlData());
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
		
		/* Make sure that the object is a DataSet */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a DataSet */
		DataSet myThat = (DataSet)pThat;
		
		/* Compare static data */
		if (!theControlKeys.equals(myThat.getControlKeys())) return false;
		if (!theDataKeys.equals(myThat.getDataKeys())) return false;
		if (!theControlData.equals(myThat.getControlData())) return false;
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
		myString.append(theControlKeys.toHTMLString());
		myString.append(theDataKeys.toHTMLString());
		myString.append(theControlData.toHTMLString());
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
		isEmpty =	theControlKeys.isEmpty() &&
					theDataKeys.isEmpty()    &&
					theControlData.isEmpty() &&
					theActTypes.isEmpty()    &&
					theTransTypes.isEmpty()  &&
					theTaxTypes.isEmpty()    &&
					theTaxRegimes.isEmpty()  &&
					theFrequencys.isEmpty()  &&
					theTaxYears.isEmpty()    &&
					theAccounts.isEmpty()    &&
					theRates.isEmpty()       &&
					thePrices.isEmpty()      &&
					thePatterns.isEmpty()    &&
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
		if (theControlKeys.hasUpdates()) return true;
		if (theDataKeys.hasUpdates()) return true;
		if (theControlData.hasUpdates()) return true;
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
	 * Get the control record
	 * @return the control record 
	 */
	public ControlData getControl() {
		/* Set the control */
		return getControlData().getControl();		
	}
	
	/**
	 * Get the active control key
	 * @return the control key 
	 */
	public ControlKey getControlKey() {
		/* Access the control element from the database */
		ControlData myControl = getControl();
		ControlKey  myKey		= null;
		
		/* Access control key from control data */
		if (myControl != null) myKey = myControl.getControlKey();
		
		/* Return the key */
		return myKey;
	}
	
	/**
	 * Initialise Security from database (if present) 
	 * @param pBase the database data
	 */
	public void initialiseSecurity(DataSet pBase) throws Exception {
		/* Initialise Security */
		theControlKeys.initialiseSecurity(pBase);		

		/* Access the control key */
		ControlKey myControl = getControlKey();
		
		/* Ensure encryption of the spreadsheet load */
		theActTypes.adoptSecurity(myControl, pBase.getAccountTypes());
		theTransTypes.adoptSecurity(myControl, pBase.getTransTypes());
		theTaxTypes.adoptSecurity(myControl, pBase.getTaxTypes());
		theFrequencys.adoptSecurity(myControl, pBase.getFrequencys());
		theTaxRegimes.adoptSecurity(myControl, pBase.getTaxRegimes());
		theAccounts.adoptSecurity(myControl, pBase.getAccounts());
		theRates.adoptSecurity(myControl, pBase.getRates());
		thePrices.adoptSecurity(myControl, pBase.getPrices());
		thePatterns.adoptSecurity(myControl, pBase.getPatterns());
		theEvents.adoptSecurity(myControl, pBase.getEvents());
	}
	
	/**
	 * Renew Security 
	 */
	public void renewSecurity() throws Exception {
		/* Create a new ControlKey */
		ControlKey myKey = theControlKeys.addItem();
				
		/* Declare the New Control Key */
		getControl().setControlKey(myKey);
		
		/* Update Security */
		updateSecurity();
	}
	
	/**
	 * Update Security 
	 */
	public void updateSecurity() throws Exception {
		/* Access the control key */
		ControlKey myControl = getControlKey();
		
		/* Ensure encryption of the spreadsheet load */
		theActTypes.updateSecurity(myControl);
		theTransTypes.updateSecurity(myControl);
		theTaxTypes.updateSecurity(myControl);
		theFrequencys.updateSecurity(myControl);
		theTaxRegimes.updateSecurity(myControl);
		theAccounts.updateSecurity(myControl);
		theRates.updateSecurity(myControl);
		thePrices.updateSecurity(myControl);
		thePatterns.updateSecurity(myControl);
		theEvents.updateSecurity(myControl);
		
		/* Delete old ControlSets */
		theControlKeys.purgeOldControlKeys();
	}
	
	/**
	 * Get the Security control 
	 * @return the security control 
	 */
	public SecurityControl getSecurityControl() throws Exception {
		/* Access the active control key */
		ControlKey  myKey	= getControlKey();
		
		/* Set the control */
		return (myKey == null) ? null : myKey.getSecurityControl();		
	}
	
	/**
	 * Update data with a new password
	 * @param pSource the source of the data
	 * @return was the password changed <code>true/false</code>
	 */
	public boolean updateSecurityControl(String pSource) throws Exception {
		/* Update the security control */
		boolean isChanged = theSecurity.updateSecurityControl(getSecurityControl(), pSource);
		
		/* If we changed the password */
		if (isChanged) {
			/* Update the control details */
			getControlKey().updateSecurityControl();
		}
		
		/* Return to the caller */
		return isChanged;
	}
	
	/**
	 * Adopt Database security after clear text load
	 */
	public void adoptSecurity(DataSet pBase) throws Exception {
	}
	
	/**
	 * Analyse the data
	 * @param pDebugMgr the debug manager
	 * @return the full analysis of the data
	 */
	public EventAnalysis analyseData(DebugManager pDebugMgr) throws Exception {
		EventAnalysis		myAnalysis;
		MetaAnalysis 		myMetaAnalysis;
						
		/* Update INITIAL Load status */
		if (theLoadState == LoadState.INITIAL)
			theLoadState = LoadState.FINAL;
		
		/* Reset the flags on the accounts and tax years */
		theAccounts.reset();
		theTaxYears.reset();
		
		/* Create the analysis */
		myAnalysis = new EventAnalysis(pDebugMgr, this);

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
	 * Add Debug entries
	 * @param pDebugMgr the Debug Manager
	 * @param pDebug the debug entry to register under
	 */
	public void addDebugEntries(DebugManager 	pDebugMgr,
							    DebugEntry 		pDebug) {
		/* Remove Existing entries */
		pDebug.removeChildren();

		/* Add entries for each data list  */
		addDebugEntry(pDebugMgr, pDebug,  theControlKeys);
		addDebugEntry(pDebugMgr, pDebug,  theDataKeys);
		addDebugEntry(pDebugMgr, pDebug,  theControlData);
		addDebugEntry(pDebugMgr, pDebug,  theActTypes);
		addDebugEntry(pDebugMgr, pDebug,  theTransTypes);
		addDebugEntry(pDebugMgr, pDebug,  theTaxTypes);
		addDebugEntry(pDebugMgr, pDebug,  theTaxRegimes);
		addDebugEntry(pDebugMgr, pDebug,  theFrequencys);
		addDebugEntry(pDebugMgr, pDebug,  theTaxYears);
		addDebugEntry(pDebugMgr, pDebug,  theAccounts);
		addDebugEntry(pDebugMgr, pDebug,  theRates);
		addDebugEntry(pDebugMgr, pDebug,  thePrices);
		addDebugEntry(pDebugMgr, pDebug,  thePatterns);
		addDebugEntry(pDebugMgr, pDebug,  theEvents);
	}
	
	/**
	 * Add Debug entries
	 * @param pDebugMgr the Debug Manager
	 * @param pDebug the debug entry to register under
	 */
	private void addDebugEntry(DebugManager 	pDebugMgr,
							   DebugEntry 	pDebug,
							   DataList<?>	pData) {
		/* Create the debug entry for this datalist and add it as a child of the main entry  */
		DebugEntry myDebug = pDebugMgr.new DebugEntry(pData.itemType());
		myDebug.addAsChildOf(pDebug);
		myDebug.setObject(pData);
	}
	
	/**
	 * Obtain DataList for an item type
	 * @param pItemType the type of items
	 * @return the list of items
	 */
	public DataList<?> getDataList(ItemType pItemType) {
		/* Switch on item type */
		switch(pItemType) {
			case DATAKEY:		return theDataKeys;
			case CONTROLKEY:	return theControlKeys;
			case CONTROLDATA:	return theControlData;
			case ACCOUNTTYPE:	return theActTypes;
			case TRANSTYPE:		return theTransTypes;
			case TAXTYPE:		return theTaxTypes;
			case TAXREGIME:		return theTaxRegimes;
			case FREQUENCY:		return theFrequencys;
			case TAXYEAR:		return theTaxYears;
			case ACCOUNT:		return theAccounts;
			case RATE:			return theRates;
			case PRICE:			return thePrices;
			case PATTERN:		return thePatterns;
			case EVENT:			return theEvents;
			default:			return null;
		}
	}
	
	/** 
	 * Enumeration of DataItems
	 */
	public static enum ItemType {
		/**
		 * DataKey
		 */
		DATAKEY,
		
		/**
		 *  ControlKeys 
		 */
		CONTROLKEY,
		
		/**
		 *  ControlData 
		 */
		CONTROLDATA,
		
		/**
		 *  AccountTypes 
		 */
		ACCOUNTTYPE,
		
		/**
		 *  TrabsactionTypes 
		 */
		TRANSTYPE,
		
		/**
		 *  TaxTypes 
		 */
		TAXTYPE,
		
		/**
		 *  TaxRegimes 
		 */
		TAXREGIME,
		
		/**
		 *  Frequencies 
		 */
		FREQUENCY,
		
		/**
		 *  TaxYear 
		 */
		TAXYEAR,
		
		/**
		 *  Account 
		 */
		ACCOUNT,
		
		/**
		 *  Price 
		 */
		PRICE,
		
		/**
		 *  Rate 
		 */
		RATE,
		
		/**
		 *  Pattern 
		 */
		PATTERN,
		
		/**
		 * Event
		 */
		EVENT;
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
