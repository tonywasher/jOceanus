package finance;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.prefs.Preferences;
import finance.finObject.ExceptionClass;

/**
 * Provides properties relating to the installation of this application
 * @author Tony Washer
 * @version 1.0
 */
public class finProperties {
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
	 * Registry name for Backup file
	 */
	private final static String nameBackupFile	= "Backupfile";

	/**
	 * Registry name for Encrypt Backups flag
	 */
	private final static String nameEncBackups	= "Encryptbackups";

	/**
	 * Registry name for Show Debug flag
	 */
	private final static String nameShowDebug	= "Showdebug";

	/**
	 * Registry name for BirthDate
	 */
	private final static String nameBirthDate	= "Birthdate";

	/**
	 * Registry name for PublicKey
	 */
	private final static String namePublicKey	= "DataKeyA";

	/**
	 * Registry name for PrivateKey
	 */
	private final static String namePrivateKey	= "DataKeyB";

	/**
	 * Registry name for DatabaseKey
	 */
	private final static String nameDataBaseKey	= "DataKeyC";

	/**
	 * Registry name for Password Salt/Hash
	 */
	private final static String namePassword	= "DataKeyD";

	/**
	 * Default Database driver string
	 */
	private final static String	defDBDriver		= "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	/**
	 * Default Database connection string
	 */
	private final static String	defDBConnection	= "jdbc:sqlserver://TONYLAP;instanceName=SQLEXPRESS1;database=Finance;integratedSecurity=true";
	
	/**
	 * Default Source old format spreadsheet
	 */
	private final static String defBaseSSheet	= "C:\\clients\\Self\\tax\\Financenew1.xls";
			
	/**
	 * Default Backup directory name
	 */
	private final static String defBackupDir	= "C:\\clients";
			
	/**
	 * Default Backup file name
	 */
	private final static String defBackupFile	= "TestBackup.xls";
			
	/**
	 * Default Encrypt backups value
	 */
	private final static boolean defEncBackups	= true;
			
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
	 * Backup file name
	 */
	private String  theBackupFile			= null;
			
	/**
	 * Backups done using encryption?
	 */
	private boolean doEncryptBackups		= defEncBackups;
			
	/**
	 * Show debug window?
	 */
	private boolean doShowDebug				= defShowDebug;
			
	/**
	 * BirthDate for tax purposes
	 */
	private finObject.Date 	theBirthDate	= null;
			
	/**
	 * PublicKeySpec
	 */
	private byte[]		 	thePublicKey	= null;
			
	/**
	 * PrivateKeySpec
	 */
	private byte[]			thePrivateKey	= null;
			
	/**
	 * DatabaseKeySpec
	 */
	private byte[]			theDatabaseKey	= null;
			
	/**
	 * DatabasePassword
	 */
	private byte[] 			thePassword		= null;
			
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
	public finObject.Date getBirthDate() 	{ return theBirthDate; }

	/**
	 * Determine public key encrypted value
	 * @return the public key
	 */
	public byte[] getPublicKey() 			{ return thePublicKey; }

	/**
	 * Determine private key encrypted value
	 * @return the private key
	 */
	public byte[] getPrivateKey() 			{ return thePrivateKey; }

	/**
	 * Determine database key encrypted value
	 * @return the database key
	 */
	public byte[] getDatabaseKey() 			{ return theDatabaseKey; }

	/**
	 * Determine password hash
	 * @return the password salt and hash value
	 */
	public byte[] getPasswordHash() 		{ return thePassword; }

	/**
	 * Constructor 
	 */
	public finProperties() throws finObject.Exception {
		/* Access preferences for this application */
		theHandle = Preferences.userRoot().node(REGISTRYLOC);

		/* load the preferences */
		loadPreferences();
	}
	
	/**
	 * Load DB Driver from preferences 
	 */
	private void loadPreferences() throws finObject.Exception {
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
		
		/* Access the BackupFile name */
		theBackupFile = theHandle.get(nameBackupFile, null);
		if (theBackupFile == null) setBackupFile(defBackupFile);
		
		/* Access the EncryptBackups flag */
		doEncryptBackups = theHandle.getBoolean(nameEncBackups, defEncBackups);
		
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
			theBirthDate = new finObject.Date(myDate);
		}
		catch (Exception e) {
			throw new finObject.Exception(ExceptionClass.DATA,
										  "Failed to parse Date: " + myBirthDate,
										  e);
		}
	
		/* Access the Public Key */
		thePublicKey = theHandle.getByteArray(namePublicKey, null);
		if (thePublicKey != null) 
			thePublicKey = finEncryption.obscureArray(thePublicKey, false);
		
		/* Access the Private Key */
		thePrivateKey = theHandle.getByteArray(namePrivateKey, null);
		if (thePrivateKey != null) 
			thePrivateKey = finEncryption.obscureArray(thePrivateKey, false);
		
		/* Access the Database Key */
		theDatabaseKey = theHandle.getByteArray(nameDataBaseKey, null);
		if (theDatabaseKey != null) 
			theDatabaseKey = finEncryption.obscureArray(theDatabaseKey, false);
		
		/* Access the Password Hash */
		thePassword = theHandle.getByteArray(namePassword, null);
		if (thePassword != null) 
			thePassword = finEncryption.obscureArray(thePassword, false);
		
		/* Flush changes */
		flushChanges();
	}
	
	/**
	 * Flush changes to the Preferences store 
	 * @throws finObject.Exception
	 */
	public void flushChanges() throws finObject.Exception {		
		/* Protect against exceptions */
		try {
			/* Flush the output */
			theHandle.flush();
		}
		
		catch (Exception e) {
			throw new finObject.Exception(ExceptionClass.PREFERENCE,
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
	 * Set the Backup File name 
	 * @param pValue the new value
	 */
	public void setBackupFile(String pValue) {
		/* Record the new value */
		theBackupFile = new String(pValue);
		
		/* Store the value into the preferences */
		theHandle.put(nameBackupFile, theBackupFile);
	}

	/**
	 * Set the Encrypt Backup flag 
	 * @param bValue the new value
	 */
	public void setDoEncryptBackups(boolean bValue) {
		/* Record the new value */
		doEncryptBackups = bValue;
		
		/* Store the value into the preferences */
		theHandle.putBoolean(nameEncBackups, doEncryptBackups);
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
	 * @param pDate the new value
	 */
	public void setBirthDate(finObject.Date pValue) {
		/* Record the new value */
		theBirthDate = pValue;
		
		/* Store the value into the preferences */
		setBirthDate(pValue.formatDate(false));
	}
	
	/**
	 * Set the BirthDate 
	 * @param pValue the new value
	 */
	private void setBirthDate(String pValue) {
		/* Store the value into the preferences */
		theHandle.put(nameBirthDate, pValue);
	}

	/**
	 * Set the Public Key encrypted value 
	 * @param pValue the new value
	 */
	public void setPublicKey(byte[] pValue) {
		/* Record the new value */
		thePublicKey = Arrays.copyOf(pValue, pValue.length);
		
		/* Store the value into the preferences */
		theHandle.putByteArray(namePublicKey, thePublicKey);
	}

	/**
	 * Set the Private Key encrypted value 
	 * @param pValue the new value
	 */
	public void setPrivateKey(byte[] pValue) {
		/* Record the new value */
		thePrivateKey = Arrays.copyOf(pValue, pValue.length);
		
		/* Store the value into the preferences */
		theHandle.putByteArray(namePrivateKey, thePrivateKey);
	}

	/**
	 * Set the Database Key encrypted value 
	 * @param pValue the new value
	 */
	public void setDatabaseKey(byte[] pValue) {
		/* Record the new value */
		theDatabaseKey = Arrays.copyOf(pValue, pValue.length);
		
		/* Store the value into the preferences */
		theHandle.putByteArray(nameDataBaseKey, theDatabaseKey);
	}

	/**
	 * Set the Password Hash 
	 * @param pValue the new value
	 */
	public void setPasswordHash(byte[] pValue) {
		/* Record the new value */
		thePassword = Arrays.copyOf(pValue, pValue.length);
		
		/* Store the value into the preferences */
		theHandle.putByteArray(namePassword, thePassword);
	}
}
