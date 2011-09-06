package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.sheets.FinanceSheet;
import uk.co.tolcroft.finance.ui.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.database.FinanceDatabase;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.data.Properties;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.sheets.SpreadSheet;
import uk.co.tolcroft.models.views.DataControl;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class View extends DataControl<FinanceData> {
	/* Members */
	private FinanceData  			theData 			= null;
	private Date.Range  			theRange 			= null;
	private MainTab					theCtl 	 			= null;
    private EventAnalysis			theAnalysis			= null;
    private DilutionEvent.List		theDilutions		= null;
    
	/* Access methods */
	public MainTab				getControl()		{ return theCtl; }
	public Date.Range			getRange()			{ return theRange; }
	public EventAnalysis    	getAnalysis()		{ return theAnalysis; }
	public DilutionEvent.List   getDilutions()		{ return theDilutions; }
	
 	/* Constructor */
	public View(MainTab pCtl) {
		/* Store access to the main window */
		theCtl	= pCtl;
		
		/* Store access to the Debug Manager */
		setDebugMgr(pCtl.getDebugMgr());

		/* Create an empty data set */
		setData(getNewData());
	}
	
	/**
	 * Obtain a new DataSet
	 * @return new DataSet
	 */
	public FinanceData getNewData() {
		return new FinanceData(getSecurity());
	}
	
	/**
	 * Obtain a Database interface
	 * @return new Database object
	 */
	public Database<FinanceData> getDatabase() throws Exception {
		return new FinanceDatabase(getProperties(),
								   getSecurity());
	}
	
	/**
	 * Obtain a Database interface
	 * @return new DataSet
	 */
	public SpreadSheet<FinanceData> getSpreadSheet() {
		return new FinanceSheet();
	}
	
	/**
	 * Update the data for a view
	 * @param pData the new data set
	 */ 
	public void setData(FinanceData pData) {
		/* Record the data */
		super.setData(pData);
		theData = pData;
		
		/* Analyse the data */
		analyseData(false);
		
		/* Refresh the windows */
		refreshWindow();
	}
	
	/**
	 * Analyse the data in the view
	 * @param bPreserve preserve any error
	 */ 
	protected boolean analyseData(boolean bPreserve) {
		/* Clear the error */
		if (!bPreserve) setError(null);
		
		/* Calculate the Data Range */
		theData.calculateDateRange();
		
		/* Access the range */
		theRange = theData.getDateRange();

		/* Protect against exceptions */
		try {
			/* Analyse the data */
			theData.analyseData(this);
			theAnalysis = theData.getAnalysis();
		
			/* Access the dilutions */
			theDilutions = theAnalysis.getDilutions();

			/* Adjust the updates debug view */
			setUpdates(theData.getUpdateSet());
		}
		
		/* Catch any exceptions */
		catch (Exception e) {
			if (!bPreserve) setError(e);
		}	

		/* Catch any exceptions */
		catch (Throwable e) {
			/* Report the failure */
			if (!bPreserve)
				setError(new Exception(ExceptionClass.DATA,
								       "Failed to analyse data",
								       e));
		}	
		
		/* Return whether there was success */
		return (getError() == null);
	}
	
	/**
	 *  refresh the window view
	 */ 
	protected void refreshWindow() {
		/* Protect against exceptions */
		try {
			/* Refresh the Control*/
			theCtl.refreshData();
		}

		/* Catch any exceptions */
		catch (Exception e) {
			setError(e);
		}	

		/* Catch any exceptions */
		catch (Throwable e) {
			/* Report the failure */
			setError(new Exception(ExceptionClass.DATA,
								   "Failed refresh window",
								   e));
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
		 * Repository directory name
		 */
		private String  		theRepoDir			= null;
				
		/**
		 * Backup file prefix
		 */
		private String  		theBackupPrefix		= null;
				
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
		 * Determine the repository directory name
		 * @return the repository directory name
		 */
		public String getRepoDir() 				{ return theRepoDir; }

		/**
		 * Determine the backup file prefix
		 * @return the backup file prefix
		 */
		public String getBackupPrefix()			{ return theBackupPrefix; }

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
			theRepoDir     		= theProperties.getRepoDir();
			theBackupPrefix    	= theProperties.getBackupPrefix();
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
		 * Set the Repository Directory name 
		 * @param pValue the new value
		 */
		public void setRepoDir(String pValue) {
			theRepoDir = new String(pValue);
		}

		/**
		 * Set the Backup File prefix 
		 * @param pValue the new value
		 */
		public void setBackupPrefix(String pValue) {
			theBackupPrefix = new String(pValue);
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
				
			/* Update the RepoDirectory if required */
			if (Utils.differs(getRepoDir(), theProperties.getRepoDir()))  
				theProperties.setRepoDir(getRepoDir());
				
			/* Update the BackupPrefix if required */
			if (Utils.differs(getBackupPrefix(), theProperties.getBackupPrefix()))  
				theProperties.setBackupPrefix(getBackupPrefix());
				
			/* Update the BirthDate if required */
			if (Date.differs(getBirthDate(), theProperties.getBirthDate()))  
				theProperties.setBirthDate(getBirthDate());
				
			/* Update the doShowDebug flag if required */
			if (doShowDebug != theProperties.doShowDebug())  
				theProperties.setDoShowDebug(doShowDebug);
				
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
						  (Utils.differs(getRepoDir(), theProperties.getRepoDir())) 				||  
						  (Utils.differs(getBackupPrefix(), theProperties.getBackupPrefix()))       ||  
						  (Date.differs(getBirthDate(), theProperties.getBirthDate()))				||  
						  (doShowDebug != theProperties.doShowDebug()));  
		}		
	}
}
