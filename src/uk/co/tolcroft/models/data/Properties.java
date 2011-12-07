package uk.co.tolcroft.models.data;

import java.text.SimpleDateFormat;
import java.util.prefs.Preferences;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class Properties {
	/**
	 * Location of Registry Keys
	 */
	private final static String REGISTRYLOC		= "finance/Properties";

	/**
	 * Registry name for DataBase driver
	 */
	private final static String nameDBDriver	= "Databasedriver";

	/**
	 * Registry name for DataBase connect
	 */
	private final static String nameDBConnect	= "Databaseconn";

	/**
	 * Registry name for Base spreadsheet
	 */
	private final static String nameBaseSSheet	= "Basesheet";

	/**
	 * Registry name for Backup directory
	 */
	private final static String nameBackupDir	= "Backupdir";

	/**
	 * Registry name for Repository directory
	 */
	private final static String nameRepoDir		= "Repodir";

	/**
	 * Registry name for Backup file
	 */
	private final static String nameBackupPrefix= "Backupprefix";

	/**
	 * Registry name for Show Debug flag
	 */
	private final static String nameShowDebug	= "Showdebug";

	/**
	 * Registry name for BirthDate
	 */
	private final static String nameBirthDate	= "Birthdate";

	/**
	 * Default Database driver string
	 */
	private final static String	defDBDriver		= "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	/**
	 * Default Database connection string
	 */
	private final static String	defDBConnection	= "jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;database=Finance;integratedSecurity=true";
	
	/**
	 * Default Source old format spreadsheet
	 */
	private final static String defBaseSSheet	= "C:\\users\\Tony\\work\\Self\\tax\\Financenew1.xls";
			
	/**
	 * Default Backup directory name
	 */
	private final static String defBackupDir	= "C:\\Users\\Tony\\Backup";
			
	/**
	 * Default Repository directory name
	 */
	private final static String defRepoDir		= "C:\\Program Files\\csvn\\data\\repositories";
			
	/**
	 * Default Backup file name
	 */
	private final static String defBackupPrefix	= "FinanceBackup";
			
	/**
	 * Default Show Debug value
	 */
	private final static boolean defShowDebug	= true;
			
	/**
	 * Default BirthDate
	 */
	private final static String defBirthDate	= "16-Nov-1962";
			
	/**
	 * Data version 0
	 */
	public final static int	DATAVERSION_0 		= 0;
	
	/**
	 * Data version is at version 0
	 */
	public final static int CURRENTVERSION 	= DATAVERSION_0;

	/**
	 * Cached properties handle
	 */
	private	Preferences		theHandle 		= null; 

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
	 * Repository directory name
	 */
	private String  theRepoDir				= null;
			
	/**
	 * Backup file prefix
	 */
	private String  theBackupPrefix			= null;
			
	/**
	 * Show debug window?
	 */
	private boolean doShowDebug				= defShowDebug;
			
	/**
	 * BirthDate for tax purposes
	 */
	private Date 			theBirthDate	= null;
			
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
	public String getBackupPrefix() 		{ return theBackupPrefix; }

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
	 * Constructor 
	 */
	public Properties() throws Exception {
		/* Access preferences for this application */
		theHandle = Preferences.userRoot().node(REGISTRYLOC);

		/* load the preferences */
		loadPreferences();
	}
	
	/**
	 * Load DB Driver from preferences 
	 */
	private void loadPreferences() throws Exception {
		java.util.Date 			   myDate;
		java.text.SimpleDateFormat myFormat;
		String					   myBirthDate;
		
		/* Access the Database driver name */
		theDBDriver = theHandle.get(nameDBDriver, null);
		if (theDBDriver == null) setDBDriver(defDBDriver);
		
		/* Access the Database connection string */
		theDBConnection = theHandle.get(nameDBConnect, null);
		if (theDBConnection == null) setDBConnection(defDBConnection);
		
		/* Access the Base Spreadsheet name */
		theBaseSpreadSheet = theHandle.get(nameBaseSSheet, null);
		if (theBaseSpreadSheet == null) setBaseSpreadSheet(defBaseSSheet);
		
		/* Access the BackupDir name */
		theBackupDir = theHandle.get(nameBackupDir, null);
		if (theBackupDir == null) setBackupDir(defBackupDir);
		
		/* Access the RepositoryDir name */
		theRepoDir = theHandle.get(nameRepoDir, null);
		if (theRepoDir == null) setRepoDir(defRepoDir);
		
		/* Access the BackupFile prefix */
		theBackupPrefix = theHandle.get(nameBackupPrefix, null);
		if (theBackupPrefix == null) setBackupPrefix(defBackupPrefix);
		
		/* Access the ShowDebug flag */
		doShowDebug = theHandle.getBoolean(nameShowDebug, defShowDebug);
		
		/* Access the BirthDate */
		myBirthDate = theHandle.get(nameBirthDate, null);
		if (myBirthDate == null) { myBirthDate = defBirthDate; setBirthDate(myBirthDate); }
		
		/* protect against exceptions */
		try { 
			/* Parse the date */
			myFormat     = new SimpleDateFormat("dd-MMM-yyyy");
			myDate       = myFormat.parse(myBirthDate);
			theBirthDate = new Date(myDate);
		}
		catch (Throwable e) {
			throw new Exception(ExceptionClass.DATA,
								"Failed to parse Date: " + myBirthDate,
								e);
		}
		
		/* Flush changes */
		flushChanges();
	}
	
	/**
	 * Flush changes to the Preferences store 
	 */
	public void flushChanges() throws Exception {		
		/* Protect against exceptions */
		try {
			/* Flush the output */
			theHandle.flush();
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.PREFERENCE,
								"Failed to flush preferences to store",
								e);
		}
	}

	/**
	 * Set the DB Driver name 
	 * @param pValue the new value
	 */
	public void setDBDriver(String pValue) {
		/* Record the new value */
		theDBDriver = new String(pValue);
		
		/* Store the value into the preferences */
		theHandle.put(nameDBDriver, theDBDriver);
	}

	/**
	 * Set the DB connection name 
	 * @param pValue the new value
	 */
	public void setDBConnection(String pValue) {
		/* Record the new value */
		theDBConnection = new String(pValue);
		
		/* Store the value into the preferences */
		theHandle.put(nameDBConnect, theDBConnection);
	}

	/**
	 * Set the Base Spreadsheet name 
	 * @param pValue the new value
	 */
	public void setBaseSpreadSheet(String pValue) {
		/* Record the new value */
		theBaseSpreadSheet = new String(pValue);
		
		/* Store the value into the preferences */
		theHandle.put(nameBaseSSheet, theBaseSpreadSheet);
	}

	/**
	 * Set the Backup Directory name 
	 * @param pValue the new value
	 */
	public void setBackupDir(String pValue) {
		/* Record the new value */
		theBackupDir = new String(pValue);
		
		/* Store the value into the preferences */
		theHandle.put(nameBackupDir, theBackupDir);
	}

	/**
	 * Set the Repository Directory name 
	 * @param pValue the new value
	 */
	public void setRepoDir(String pValue) {
		/* Record the new value */
		theRepoDir = new String(pValue);
		
		/* Store the value into the preferences */
		theHandle.put(nameRepoDir, theRepoDir);
	}

	/**
	 * Set the Backup File prefix 
	 * @param pValue the new value
	 */
	public void setBackupPrefix(String pValue) {
		/* Record the new value */
		theBackupPrefix = new String(pValue);
		
		/* Store the value into the preferences */
		theHandle.put(nameBackupPrefix, theBackupPrefix);
	}

	/**
	 * Set the Show Debug flag 
	 * @param bValue the new value
	 */
	public void setDoShowDebug(boolean bValue) {
		/* Record the new value */
		doShowDebug = bValue;
		
		/* Store the value into the preferences */
		theHandle.putBoolean(nameShowDebug, doShowDebug);
	}
	
	/**
	 * Set the BirthDate 
	 * @param pValue the new value
	 */
	public void setBirthDate(Date pValue) {
		/* Record the new value */
		theBirthDate = pValue;
		
		/* Store the value into the preferences */
		setBirthDate(pValue.formatDate());
	}
	
	/**
	 * Set the BirthDate 
	 * @param pValue the new value
	 */
	private void setBirthDate(String pValue) {
		/* Store the value into the preferences */
		theHandle.put(nameBirthDate, pValue);
	}
}
