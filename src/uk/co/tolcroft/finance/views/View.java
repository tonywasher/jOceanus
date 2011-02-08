package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.ui.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class View {
	/* Members */
	private DataSet     		theData  		= null;
	private Date.Range  		theRange 		= null;
	private MainTab				theCtl 	 		= null;
    private AnalysisYear.List	theAnalysis		= null;
    private DilutionEvent.List	theDilutions	= null;
    private Exception			theError		= null;

	/* Access methods */
	public DataSet 				getData() 		{ return theData; }
	public MainTab				getControl()	{ return theCtl; }
	public Date.Range			getRange()		{ return theRange; }
	public AnalysisYear.List    getAnalyses()	{ return theAnalysis; }
	public DilutionEvent.List   getDilutions()	{ return theDilutions; }
	public Exception			getError()		{ return theError; }
	
 	/* Constructor */
	public View(MainTab pCtl) {
		theCtl	= pCtl;
		theData = new DataSet(pCtl.getSecurity());
		theData.calculateDateRange();
		analyseData();
	}
	
	/* Update the data for a view */ 
	public void setData(DataSet pData) {
		/* Record the data */
		theData = pData;
		
		/* Analyse the data */
		analyseData();
		
		/* Refresh the windows */
		refreshWindow();
	}
	
	/* initialise the view from the data */ 
	public void analyseData() {
		/* Clear the error */
		theError = null;
		
		/* Access the range */
		theRange = theData.getDateRange();

		/* Protect against exceptions */
		try {
			/* Analyse the data */
			theAnalysis  = theData.analyseData();
		
			/* Access the dilutions */
			theDilutions = theAnalysis.getDilutions();
		}
		
		/* Catch any exceptions */
		catch (Exception e) {
			theError = e;
		}	

		/* Catch any exceptions */
		catch (Throwable e) {
			/* Report the failure */
			theError = new Exception(ExceptionClass.DATA,
								     "Failed to analyse data",
								     e);
		}	
	}
	
	/* refresh the window view */ 
	public void refreshWindow() {
		/* Refresh the Control*/
		if (theCtl != null) theCtl.refreshData();
	}
	
	/* TaxYear Extract Class */
	public class ViewTaxYear {
		/* Members */
		private TaxYear.List 	theList 	= null;
		private TaxYear			theTaxYear 	= null;
		
		/* Access methods */
		public TaxYear getTaxYear() { return theTaxYear; }
		
		/* Constructor */
		public ViewTaxYear(TaxYear pTaxYear) {
			/* Create an empty list */
			theList = new TaxYear.List(theData, ListStyle.EDIT);
			
			/* Create a new tax year based on the passed tax year */
			theTaxYear = new TaxYear(theList, pTaxYear);
			theTaxYear.addToList();
		}
		
		/* Constructor */
		public ViewTaxYear() {
			TaxYear.List 					myTaxYears;
			TaxYear    						myBase;
			DataList<TaxYear>.ListIterator 	myIterator;
			
			/* Access the existing tax years */
			myTaxYears = theData.getTaxYears();
			myIterator = myTaxYears.listIterator(true);
			
			/* Create an empty list */
			theList = new TaxYear.List(theData, ListStyle.EDIT);

			/* Create a new tax year for the list */
			myBase = myIterator.peekLast();
			theTaxYear = new TaxYear(theList, myBase);
			theTaxYear.setBase(null);
			theTaxYear.setState(DataState.NEW);
			theTaxYear.setId(0);
						
			/* Adjust the year and add to list */
			theTaxYear.setDate(new Date(myBase.getDate()));
			theTaxYear.getDate().adjustYear(1);
			theTaxYear.addToList();
		}
				
		/** 
		 * Apply changes in an TaxParams view back into the core data
		 */
		public void applyChanges() {
			TaxYear.List myBase;
			
			/* Access base details */
			myBase = theData.getTaxYears();
			
			/* Apply the changes from this list */
			myBase.applyChanges(theList);
			
			/* Update Range details */
			theData.calculateDateRange();
			
			/* analyse the data */
			analyseData();
			
			/* Refresh windows */
			refreshWindow();
		}
	}
	
	/* Account Extract Class */
	public class ViewAccount {
		/* Members */
		private Account.List 	theList    = null;
		private Account			theAccount = null;
		
		/* Access methods */
		public 	Account 		getAccount() { return theAccount; }
		
		/* Constructor */
		public ViewAccount(Account pAccount) {
			Account.List myAccounts;
			
			/* Access the existing accounts */
			myAccounts = theData.getAccounts();
			
			/* Create a copy of the accounts */
			theList = new Account.List(myAccounts, ListStyle.EDIT);

			/* Locate the account in the list */
			theAccount = theList.searchFor(pAccount.getName());
		}
		
		public ViewAccount(AccountType pType) {
			Account.List myAccounts;
			
			/* Access the existing accounts */
			myAccounts = theData.getAccounts();
			
			/* Create a copy of the accounts */
			theList = new Account.List(myAccounts, ListStyle.EDIT);
			
			/* Create an new empty account */
			theAccount = new Account(theList);
			theAccount.addToList();
			
			/* Set the type of the account */
			theAccount.setActType(pType);
			
			/* If the account is a bond */
			if (theAccount.getActType().isBond()) {
				/* Create a default maturity */
				theAccount.setMaturity(new Date());
				theAccount.getMaturity().adjustYear(1);
			}
		}
		
		/** 
		 * Apply changes in an Account view back into the core data
		 */
		public void applyChanges() {
			Account.List myBase;
			
			/* Access base details */
			myBase     = theData.getAccounts();
			
			/* Apply the changes */
			myBase.applyChanges(theList);
						
			/* analyse the data */
			analyseData(); 
			
			/* Refresh windows */
			refreshWindow();
		}
	}
	
	/* Rates Extract Class */
	public class ViewRates {
		/* Members */
		private Rate.List 	theList    = null;
		
		/* Access methods */
		public Rate.List getRates() { return theList; }
		
		/* Constructor */
		public ViewRates(Account pAccount) {
			Rate.List myRates;
			
			/* Access the existing rates */
			myRates = theData.getRates();
			
			/* Create a copy of the rates */
			theList = new Rate.List(myRates, pAccount);
		}
				
		/** 
		 * Apply changes in a Rates view back into the core data
		 */
		public void applyChanges() {
			Rate.List myBase;
			
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
	public class ViewPrices {
		/* Members */
		private Price.List 	theList    = null;
		
		/* Access methods */
		public Price.List getPrices() { return theList; }
		
		/* Constructor */
		public ViewPrices(Account pAccount) {
			Price.List myPrices;
			
			/* Access the existing prices */
			myPrices = theData.getPrices();
			
			/* Create a copy of the prices */
			theList = new Price.List(myPrices, pAccount);
		}
				
		/** 
		 * Apply changes in a Prices view back into the core data
		 */
		public void applyChanges() {
			Price.List myBase;
			
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
	public class ViewPatterns {
		/* Members */
		private Pattern.List 	theList    = null;
		
		/* Access methods */
		public Pattern.List getPatterns() { return theList; }
		
		/* Constructor */
		public ViewPatterns(Account pAccount) {
			Pattern.List myPatterns;
			
			/* Access the existing patterns */
			myPatterns = theData.getPatterns();
			
			/* Create a copy of the patterns */
			theList = new Pattern.List(myPatterns, pAccount);
		}
				
		/** 
		 * Apply changes in a patterns view back into the core data
		 */
		public void applyChanges() {
			Pattern.List myBase;
			
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
	public class ViewEvents {
		/* Members */
		private Date.Range  theRange   = null;
		private Event.List 	theEvents  = null;

		/* Access methods */
		public Date.Range   getRange()     { return theRange; }
	 	public Event.List 	getEvents()    { return theEvents; }
	 	
	 	/* Constructor */
		public ViewEvents(Date.Range pRange) {
			DataList<Event>.ListIterator 	myIterator;
			Event.List  					myBase;
			Event      						myCurr;
			Event      						myEvent;
			int       						myResult;
			
			/* Record range and initialise the list */
			theRange   = pRange;
			theEvents  = new Event.List(theData, ListStyle.EDIT);
			
			/* Access the underlying data */
			myBase 		= getData().getEvents();
			myIterator 	= myBase.listIterator(true);
			
			/* Loop through the Events extracting relevant elements */
			while ((myCurr = myIterator.next()) != null) {
				/* Check the range */
				myResult = pRange.compareTo(myCurr.getDate());
				
				/* Handle out of range */
				if (myResult ==  1) continue;
				if (myResult == -1) break;
				
				/* Build the new linked event and add it to the extract */
				myEvent = new Event(theEvents, myCurr);
				myEvent.addToList();
			}
		}

	 	/* Constructor */
		public ViewEvents(TaxYear pTaxYear) {
			DataList<Pattern>.ListIterator 	myIterator;
			Pattern.List 					myPatterns;
			Pattern     					myCurr;
			Event       					myEvent;
			Date 							myDate;
			
			/* Record range and initialise the list */
			theRange   = pTaxYear.getRange();
			theEvents  = new Event.List(theData, ListStyle.EDIT);
			
			/* Access the underlying data */
			myPatterns 	= getData().getPatterns();
			myIterator 	= myPatterns.listIterator();

			/* Loop through the Patterns */
			while ((myCurr = myIterator.next()) != null) {
				/* Access a copy of the base date */
				myDate = new Date(myCurr.getDate());
					
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
			DataList<Event>.ListIterator 	myIterator;
			Event 							myCurr;

			/* Clear the errors */
			theEvents.clearErrors();
			
			/* Access the underlying data */
			myIterator 	= theEvents.listIterator();
			
			/* Loop through the lines */
			while ((myCurr = myIterator.next()) != null) {
				/* Validate it */
				myCurr.validate();
			}
		}
		
		/** 
		 * Apply changes in an EventExtract view back into the core data
		 */
		public void applyChanges() {
			Event.List myBase;
			
			/* Access base details */
			myBase = getData().getEvents();
			
			/* Apply the changes from this list */
			myBase.applyChanges(theEvents);
			
			/* analyse the data */
			analyseData();
			
			/* Refresh windows */
			refreshWindow();
		}
	}
	/**
	 * The properties view class
	 */
	public class ViewProperties {
		/**
		 * Underlying properties
		 */
		private Properties		theProperties		= null;

		/**
		 * Do we have changes
		 */
		private boolean			hasChanges			= false;

		/**
		 * Database driver string
		 */
		private String			theDBDriver			= null;

		/**
		 * Database connection string
		 */
		private String			theDBConnection		= null;
		
		/**
		 * Source old format spreadsheet
		 */
		private String  		theBaseSpreadSheet	= null;
				
		/**
		 * Backup directory name
		 */
		private String  		theBackupDir		= null;
				
		/**
		 * Backup file name
		 */
		private String  		theBackupFile		= null;
				
		/**
		 * Backups done using encryption?
		 */
		private boolean 		doEncryptBackups	= false;
				
		/**
		 * Show debug window?
		 */
		private boolean 		doShowDebug			= false;
				
		/**
		 * BirthDate for tax purposes
		 */
		private Date 			theBirthDate		= null;
				
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
		public Date getBirthDate() 				{ return theBirthDate; }

		/**
		 * Determine whether we have changes
		 * @return <code>true/false</code>
		 */
		public boolean hasChanges() 			{ return hasChanges; }

		/**
		 * Constructor
		 */
		public ViewProperties() {
			/* Access the properties */
			theProperties = theCtl.getProperties();
			
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
		public void setBirthDate(Date pValue) {
			theBirthDate = pValue;
		}		

		/**
		 * ApplyChanges to properties
		 */
		public void applyChanges() throws Exception {
			/* Update the DBDriver if required */
			if (Utils.differs(getDBDriver(), theProperties.getDBDriver()))  
				theProperties.setDBDriver(getDBDriver());
				
			/* Update the DBConnection if required */
			if (Utils.differs(getDBConnection(), theProperties.getDBConnection()))  
				theProperties.setDBConnection(getDBConnection());
				
			/* Update the BaseSpreadSheet if required */
			if (Utils.differs(getBaseSpreadSheet(), theProperties.getBaseSpreadSheet()))  
				theProperties.setBaseSpreadSheet(getBaseSpreadSheet());
				
			/* Update the BackupDirectory if required */
			if (Utils.differs(getBackupDir(), theProperties.getBackupDir()))  
				theProperties.setBackupDir(getBackupDir());
				
			/* Update the BackupFile if required */
			if (Utils.differs(getBackupFile(), theProperties.getBackupFile()))  
				theProperties.setBackupFile(getBackupFile());
				
			/* Update the BirthDate if required */
			if (Utils.differs(getBirthDate(), theProperties.getBirthDate()))  
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
			hasChanges = ((Utils.differs(getDBDriver(), theProperties.getDBDriver()))               ||  
						  (Utils.differs(getDBConnection(), theProperties.getDBConnection()))       ||  
						  (Utils.differs(getBaseSpreadSheet(), theProperties.getBaseSpreadSheet())) ||  
						  (Utils.differs(getBackupDir(), theProperties.getBackupDir())) 			||  
						  (Utils.differs(getBackupFile(), theProperties.getBackupFile()))           ||  
						  (Utils.differs(getBirthDate(), theProperties.getBirthDate()))				||  
						  (doShowDebug != theProperties.doShowDebug())									||  
						  (doEncryptBackups != theProperties.doEncryptBackups()));  
		}		
	}
}
