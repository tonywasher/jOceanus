package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.ui.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.core.SecureManager;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class View {
	/* Members */
	private DataSet     			theData  		= null;
	private Date.Range  			theRange 		= null;
	private MainTab					theCtl 	 		= null;
    private EventAnalysis			theAnalysis		= null;
    private DilutionEvent.List		theDilutions	= null;
    private Exception				theError		= null;
    private SecureManager			theSecurity		= null;
    private DebugManager			theDebugMgr		= null;
    
	/* Access methods */
	public DataSet 				getData() 		{ return theData; }
	public MainTab				getControl()	{ return theCtl; }
	public Date.Range			getRange()		{ return theRange; }
	public EventAnalysis    	getAnalysis()	{ return theAnalysis; }
	public DilutionEvent.List   getDilutions()	{ return theDilutions; }
	public Exception			getError()		{ return theError; }
	public SecureManager		getSecurity() 	{ return theSecurity; }
	public DebugManager			getDebugMgr() 	{ return theDebugMgr; }
	
 	/* Constructor */
	public View(MainTab pCtl) {
		/* Store access to the main window */
		theCtl	= pCtl;
		
		/* Store access to the Debug Manager */
		theDebugMgr = pCtl.getDebugMgr();
		
		/* Create a new security manager */
		theSecurity = new SecureManager(pCtl);
		
		/* Create an empty data set */
		theData = new DataSet(theSecurity);
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
			theAnalysis = theData.analyseData(theDebugMgr);
		
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
	protected void refreshWindow() {
		/* Protect against exceptions */
		try {
			/* Refresh the Control*/
			theCtl.refreshData();
		}

		/* Catch any exceptions */
		catch (Exception e) {
			theError = e;
		}	

		/* Catch any exceptions */
		catch (Throwable e) {
			/* Report the failure */
			theError = new Exception(ExceptionClass.DATA,
								     "Failed refresh window",
								     e);
		}	
	}
	
	/* TaxYear Extract Class */
	public class ViewTaxYear extends TaxYear.List {
		/* Members */
		private TaxYear			theTaxYear 	= null;
		
		/* Access methods */
		public TaxYear getTaxYear() { return theTaxYear; }
		
		/* Constructor */
		public ViewTaxYear(TaxYear pTaxYear) {
			/* Call super constructor */
			super(theData, ListStyle.EDIT);
			
			/* Create a new tax year based on the passed tax year */
			theTaxYear = new TaxYear(this, pTaxYear);
			theTaxYear.addToList();
		}
		
		/* Constructor */
		public ViewTaxYear() {
			/* Call super constructor */
			super(theData, ListStyle.EDIT);

			/* Local Variables */
			TaxYear.List 					myTaxYears;
			TaxYear    						myBase;
			DataList<TaxYear>.ListIterator 	myIterator;
			
			/* Access the existing tax years */
			myTaxYears = theData.getTaxYears();
			myIterator = myTaxYears.listIterator(true);
			
			/* Create a new tax year for the list */
			myBase = myIterator.peekLast();
			theTaxYear = new TaxYear(this, myBase);
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
			myBase.applyChanges(this);
			
			/* Update Range details */
			theData.calculateDateRange();
			
			/* analyse the data */
			analyseData();
			
			/* Refresh windows */
			refreshWindow();
		}
	}
	
	/* Account Extract Class */
	public class ViewAccount extends Account.List {
		/* Members */
		private Account			theAccount = null;
		private AcctPrice.List 	thePrices  = null;
		
		/* Access methods */
		public 	Account 		getAccount() { return theAccount; }
		
		/* Constructor */
		public ViewAccount(Account pAccount) {
			/* Call super constructor */
			super(theData.getAccounts(), ListStyle.EDIT);
			
			/* Locate the account in the list */
			theAccount = searchFor(pAccount.getName());
		}
		
		public ViewAccount(AccountType pType) {
			/* Call super constructor */
			super(theData.getAccounts(), ListStyle.EDIT);
			
			/* Create an new empty account */
			theAccount = new Account(this);
			theAccount.addToList();
			
			/* Set the type of the account */
			theAccount.setActType(pType);
			
			/* If the account is a bond */
			if (theAccount.getActType().isBond()) {
				/* Create a default maturity */
				theAccount.setMaturity(new Date());
				theAccount.getMaturity().adjustYear(1);
			}

			/* If the account is prices */
			if (theAccount.getActType().isPriced()) {
				/* Create an empty price list */
				thePrices = new AcctPrice.List(theData, ListStyle.EDIT);
				
				/* Add a default £1 price for this account on this date */
				try { thePrices.addItem(theAccount, new Date(), new Price(Price.convertToValue(1))); } catch (Throwable e) {}
				theAccount.touchPrice();
			}
		}
		
		/** 
		 * Apply changes in an Account view back into the core data
		 */
		public void applyChanges() {
			Account.List myBase;
			AcctPrice.List myPrices;
			
			/* Access base details */
			myBase     	= theData.getAccounts();
			myPrices	= theData.getPrices();
			
			/* Apply the changes */
			myBase.applyChanges(this);
			if (thePrices != null) myPrices.applyChanges(thePrices); 
				
			/* analyse the data */
			analyseData(); 
			
			/* Refresh windows */
			refreshWindow();
		}
		
		/**
		 * Override the toHTMLString method to just return the account 
		 */
		public StringBuilder toHTMLString() { return theAccount.toHTMLString(); }		
	}
	
	/* Rates Extract Class */
	public class ViewRates extends AcctRate.List {
		/* Members */
		private Account 	theAccount	= null;
		
		/* Access methods */
		public AcctRate.List getRates() { return this; }
		
		/* Constructor */
		public ViewRates(Account pAccount) {
			/* Call super constructor */
			super(theData.getRates(), pAccount);
			
			/* Store the account */
			theAccount = pAccount;
		}
				
		/** 
		 * Apply changes in a Rates view back into the core data
		 */
		public void applyChanges() {
			AcctRate.List myBase;
			
			/* Access base details */
			myBase     = theData.getRates();
			
			/* Apply the changes */
			myBase.applyChanges(this);
			
			/*
			 * Analyse and refresh are performed in the statement view
			 */
		}

		/**
		 * Add additional fields to HTML String
		 * @param pBuffer the string buffer 
		 */
		public void addHTMLFields(StringBuilder pBuffer) {
			/* Start the Fields section */
			pBuffer.append("<tr><th rowspan=\"2\">Fields</th></tr>");

			/* Format the range */
			pBuffer.append("<tr><td>Account</td><td>"); 
			pBuffer.append(Account.format(theAccount)); 
			pBuffer.append("</td></tr>");
		}
	}
	
	/* Patterns Extract Class */
	public class ViewPatterns extends Pattern.List {
		/* Members */
		private Account 	theAccount	= null;
		
		/* Access methods */
		public Pattern.List getPatterns() { return this; }
		
		/* Constructor */
		public ViewPatterns(Account pAccount) {
			/* Call super constructor */
			super(theData.getPatterns(), pAccount);
			
			/* Store the account */
			theAccount = pAccount;
		}
				
		/** 
		 * Apply changes in a patterns view back into the core data
		 */
		public void applyChanges() {
			Pattern.List myBase;
			
			/* Access base details */
			myBase     = theData.getPatterns();
			
			/* Apply the changes */
			myBase.applyChanges(this);
			
			/*
			 * Analyse and refresh are performed in the statement view
			 */
		}

		/**
		 * Add additional fields to HTML String
		 * @param pBuffer the string buffer 
		 */
		public void addHTMLFields(StringBuilder pBuffer) {
			/* Start the Fields section */
			pBuffer.append("<tr><th rowspan=\"2\">Fields</th></tr>");

			/* Format the range */
			pBuffer.append("<tr><td>Account</td><td>"); 
			pBuffer.append(Account.format(theAccount)); 
			pBuffer.append("</td></tr>");
		}
	}
	
	/* Event Extract Class */
	public class ViewEvents extends Event.List {
		/* Members */
		private Date.Range  theRange   = null;

		/* Access methods */
		public Date.Range   getRange()     { return theRange; }
	 	public Event.List 	getEvents()    { return this; }
	 	
	 	/* Constructor */
		public ViewEvents(Date.Range pRange) {
			/* Call super constructor */
			super(theData, ListStyle.EDIT);
			
			/* local variable */
			DataList<Event>.ListIterator 	myIterator;
			Event.List  					myBase;
			Event      						myCurr;
			Event      						myEvent;
			int       						myResult;
			
			/* Record range and initialise the list */
			theRange   = pRange;
			
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
				myEvent = new Event(this, myCurr);
				myEvent.addToList();
			}
		}

	 	/* Constructor */
		public ViewEvents(TaxYear pTaxYear) {
			/* Call super constructor */
			super(theData, ListStyle.EDIT);

			/* Local variables */
			DataList<Pattern>.ListIterator 	myIterator;
			Pattern.List 					myPatterns;
			Pattern     					myCurr;
			Event       					myEvent;
			Date 							myDate;
			
			/* Record range and initialise the list */
			theRange   = pTaxYear.getRange();
			
			/* Access the underlying data */
			myPatterns 	= getData().getPatterns();
			myIterator 	= myPatterns.listIterator();

			/* Loop through the Patterns */
			while ((myCurr = myIterator.next()) != null) {
				/* Access a copy of the base date */
				myDate = new Date(myCurr.getDate());
					
				/* Loop while we have an event to add */
				while ((myEvent = myCurr.nextEvent(this,
												   pTaxYear, 
												   myDate)) != null) {
					/* Add it to the extract */
					myEvent.addToList();
				}
			}
		}

		/**
		 * Add additional fields to HTML String
		 * @param pBuffer the string buffer 
		 */
		public void addHTMLFields(StringBuilder pBuffer) {
			/* Start the Fields section */
			pBuffer.append("<tr><th rowspan=\"2\">Fields</th></tr>");

			/* Format the range */
			pBuffer.append("<tr><td>Range</td><td>"); 
			pBuffer.append(Date.Range.format(theRange)); 
			pBuffer.append("</td></tr>");
		}
		
		/** 
		 * Validate an extract
		 */
		public void validate() {
			DataList<Event>.ListIterator 	myIterator;
			Event 							myCurr;

			/* Clear the errors */
			clearErrors();
			
			/* Access the underlying data */
			myIterator 	= listIterator();
			
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
			myBase.applyChanges(this);
			
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
		 * Backup file prefix
		 */
		private String  		theBackupPrefix		= null;
				
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
		 * Determine the backup file prefix
		 * @return the backup file prefix
		 */
		public String getBackupPrefix()			{ return theBackupPrefix; }

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
			theBackupPrefix    	= theProperties.getBackupPrefix();
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
		 * Set the Backup File prefix 
		 * @param pValue the new value
		 */
		public void setBackupPrefix(String pValue) {
			theBackupPrefix = new String(pValue);
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
				
			/* Update the BackupPrefix if required */
			if (Utils.differs(getBackupPrefix(), theProperties.getBackupPrefix()))  
				theProperties.setBackupPrefix(getBackupPrefix());
				
			/* Update the BirthDate if required */
			if (Date.differs(getBirthDate(), theProperties.getBirthDate()))  
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
						  (Utils.differs(getBackupPrefix(), theProperties.getBackupPrefix()))           ||  
						  (Date.differs(getBirthDate(), theProperties.getBirthDate()))				||  
						  (doShowDebug != theProperties.doShowDebug())									||  
						  (doEncryptBackups != theProperties.doEncryptBackups()));  
		}		
	}
}
