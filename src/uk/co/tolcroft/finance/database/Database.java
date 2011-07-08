package uk.co.tolcroft.finance.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import uk.co.tolcroft.finance.core.Threads.statusCtl;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class Database {
	/**
	 * Default batch size for updates
	 */
	protected final static int    	BATCH_SIZE 		= 50;

	/**
	 * Properties for application
	 */
	private Properties				theProperties	= null;
	
	/**
	 * Database connection
	 */
	private Connection          	theConn         = null;
	
	/**
	 * Static table access
	 */
	private TableControl		        theStatic		= null;

	/**
	 * Account Type table access
	 */
	private TableAccountType        theAccountTypes = null;

	/**
	 * Transaction Type table access
	 */
	private TableTransactionType	theTransTypes   = null;

	/**
	 * Tax Type table access
	 */
	private TableTaxType            theTaxTypes     = null;

	/**
	 * Tax Regime table access
	 */
	private TableTaxRegime          theTaxRegimes   = null;

	/**
	 * Frequency table access
	 */
	private TableFrequency          theFrequencys   = null;

	/**
	 * TaxYear table access
	 */
	private TableTaxYear          	theTaxYears     = null;

	/**
	 * Account Type table access
	 */
	private TableAccount            theAccounts     = null;

	/**
	 * Rate table access
	 */
	private TableRate               theRates        = null;

	/**
	 * Price table access
	 */
	private TablePrice              thePrices       = null;

	/**
	 * Pattern table access
	 */
	private TablePattern            thePatterns     = null;

	/**
	 * Event table access
	 */
	private TableEvent              theEvents       = null;

	/**
	 * Construct a new Database class
	 */
	public Database(Properties pProperties) throws Exception {
		/* Store the properties */
		theProperties = pProperties;
		
		/* Create the connection */
		try {
			Class.forName(theProperties.getDBDriver());	   
			theConn = DriverManager.getConnection(theProperties.getDBConnection());
			theConn.setAutoCommit(false);
		}
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								"Failed to load driver",
								e);
		}
		
		/* Create the table classes */
		theStatic		= new TableControl(this);
		theAccountTypes = new TableAccountType(this);
		theTransTypes   = new TableTransactionType(this);
		theTaxTypes     = new TableTaxType(this);
		theTaxRegimes   = new TableTaxRegime(this);
		theFrequencys   = new TableFrequency(this);
		theTaxYears     = new TableTaxYear(this);
		theAccounts     = new TableAccount(this);
		theRates        = new TableRate(this);
		thePrices       = new TablePrice(this);
		thePatterns     = new TablePattern(this);
		theEvents       = new TableEvent(this);
	}
	
	/**
	 * Access the connection 
	 * @return the connection
	 */
	protected Connection getConn() { return theConn; }
	
	/**
	 * RollBack and disconnect on termination
	 */
	protected void finalize() throws Throwable {
		try {
			if (theConn != null) close();
		} finally {
			super.finalize();
		}	
	}
	
	/**
	 * Close the connection to the database 
	 * rolling back any outstanding transaction
	 * @throws SQLException
	 */
	private void close() throws SQLException {
		/* Roll-back any outstanding transaction */
		theConn.rollback();
		
		/* Close the result set and statements */
		if (theStatic       != null) theStatic.closeStmt();
		if (theAccountTypes != null) theAccountTypes.closeStmt();
		if (theTransTypes   != null) theTransTypes.closeStmt();
		if (theTaxTypes     != null) theTaxTypes.closeStmt();
		if (theTaxRegimes   != null) theTaxRegimes.closeStmt();
		if (theFrequencys   != null) theFrequencys.closeStmt();
		if (theTaxYears     != null) theTaxYears.closeStmt();
		if (theAccounts     != null) theAccounts.closeStmt();
		if (theRates        != null) theRates.closeStmt();
		if (thePrices       != null) thePrices.closeStmt();
		if (thePatterns     != null) thePatterns.closeStmt();
		if (theEvents       != null) theEvents.closeStmt();

		/* Close the connection */
		theConn.close();
	}

	/**
	 * Load data from the database 
	 * @param pThread the thread control
	 * @return the new DataSet
	 */
	public DataSet loadDatabase(statusCtl 	pThread) throws Exception {
		boolean bContinue 	= true;
		DataSet myData		= null;
		View	myView;
		
		/* Set the number of stages */
		if (!pThread.setNumStages(13)) return null;
		
		/* Access the view */
		
		/* Create the new DataSet */
		myView = pThread.getView();
		myData = new DataSet(myView.getSecurity()); 
			
		/* Load entries from tables */
		bContinue = theStatic.loadItems(pThread, myData);
		if (bContinue) bContinue = theAccountTypes.loadItems(pThread, myData);
		if (bContinue) bContinue = theTransTypes.loadItems(pThread, myData);
		if (bContinue) bContinue = theTaxTypes.loadItems(pThread, myData);
		if (bContinue) bContinue = theTaxRegimes.loadItems(pThread, myData);
		if (bContinue) bContinue = theFrequencys.loadItems(pThread, myData);
		if (bContinue) bContinue = theTaxYears.loadItems(pThread, myData);
		if (bContinue) myData.calculateDateRange();
		if (bContinue) bContinue = theAccounts.loadItems(pThread, myData);
		if (bContinue) bContinue = theRates.loadItems(pThread, myData);
		if (bContinue) bContinue = thePrices.loadItems(pThread, myData);
		if (bContinue) bContinue = thePatterns.loadItems(pThread, myData);
		if (bContinue) myData.getAccounts().validateLoadedAccounts();
		if (bContinue) bContinue = theEvents.loadItems(pThread, myData);
		
		/* analyse the data */
		if (bContinue) bContinue = pThread.setNewStage("Refreshing data");
		
		/* Check for cancellation */
		if (!bContinue) 
			throw new Exception(ExceptionClass.LOGIC,
								"Operation Cancelled");
		
		/* Return the data */
		return (bContinue) ? myData : null;
	}
	
	/**
	 * Update data into database
	 * @param pThread the thread control
	 * @param pData the data
	 */
	public void updateItems(statusCtl 	pThread,
							DataSet 	pData) throws Exception {
		boolean bContinue = true;
		
		/* Set the number of stages */
		if (!pThread.setNumStages(36)) return;
		
		/* Insert entries into tables */
		bContinue = theStatic.insertItems(pThread, pData);
		if (bContinue) bContinue = theAccountTypes.insertItems(pThread, pData);
		if (bContinue) bContinue = theTransTypes.insertItems(pThread, pData);
		if (bContinue) bContinue = theTaxTypes.insertItems(pThread, pData);
		if (bContinue) bContinue = theTaxRegimes.insertItems(pThread, pData);
		if (bContinue) bContinue = theFrequencys.insertItems(pThread, pData);
		if (bContinue) bContinue = theTaxYears.insertItems(pThread, pData);
		if (bContinue) bContinue = theAccounts.insertItems(pThread, pData);
		if (bContinue) bContinue = theRates.insertItems(pThread, pData);
		if (bContinue) bContinue = thePrices.insertItems(pThread, pData);
		if (bContinue) bContinue = thePatterns.insertItems(pThread, pData);
		if (bContinue) bContinue = theEvents.insertItems(pThread, pData);
		
		/* Update entries in tables */
		if (bContinue) bContinue = theStatic.updateItems(pThread);
		if (bContinue) bContinue = theAccountTypes.updateItems(pThread);
		if (bContinue) bContinue = theTransTypes.updateItems(pThread);
		if (bContinue) bContinue = theTaxTypes.updateItems(pThread);
		if (bContinue) bContinue = theTaxRegimes.updateItems(pThread);
		if (bContinue) bContinue = theFrequencys.updateItems(pThread);
		if (bContinue) bContinue = theTaxYears.updateItems(pThread);
		if (bContinue) bContinue = theAccounts.updateItems(pThread);
		if (bContinue) bContinue = theRates.updateItems(pThread);
		if (bContinue) bContinue = thePrices.updateItems(pThread);
		if (bContinue) bContinue = thePatterns.updateItems(pThread);
		if (bContinue) bContinue = theEvents.updateItems(pThread);
		
		/* Delete entries from table in reverse order */
		if (bContinue) bContinue = theEvents.deleteItems(pThread);
		if (bContinue) bContinue = thePatterns.deleteItems(pThread);
		if (bContinue) bContinue = thePrices.deleteItems(pThread);
		if (bContinue) bContinue = theRates.deleteItems(pThread);
		if (bContinue) bContinue = theAccounts.deleteItems(pThread);
		if (bContinue) bContinue = theTaxYears.deleteItems(pThread);
		if (bContinue) bContinue = theFrequencys.deleteItems(pThread);
		if (bContinue) bContinue = theTaxRegimes.deleteItems(pThread);
		if (bContinue) bContinue = theTaxTypes.deleteItems(pThread);
		if (bContinue) bContinue = theTransTypes.deleteItems(pThread);
		if (bContinue) bContinue = theAccountTypes.deleteItems(pThread);
		if (bContinue) bContinue = theStatic.deleteItems(pThread);
		
		/* Check for cancellation */
		if (!bContinue) 
			throw new Exception(ExceptionClass.LOGIC,
								"Operation Cancelled");
	}
	
	/**
	 * Create tables 
	 * @param pThread the thread control
	 * @return Continue <code>true/false</code>
	 */
	public void createTables(statusCtl 	pThread) throws Exception {
		
		/* Drop any existing tables */
		dropTables(pThread);
		
		/* Set the number of stages */
		if (!pThread.setNumStages(1)) return;
		
		/* Create tables */
		theStatic.createTable();
		theAccountTypes.createTable();
		theTransTypes.createTable();
		theTaxTypes.createTable();
		theTaxRegimes.createTable();
		theFrequencys.createTable();
		theTaxYears.createTable();
		theAccounts.createTable();
		theRates.createTable();
		thePrices.createTable();
		thePatterns.createTable();
		theEvents.createTable();

		/* Return the data */
		return;
	}	

	/**
	 * Drop tables 
	 * @param pThread the thread control
	 * @return Continue <code>true/false</code>
	 */
	private void dropTables(statusCtl 	pThread) throws Exception {
		
		/* Set the number of stages */
		if (!pThread.setNumStages(1)) return;
		
		/* Drop tables */
		theEvents.dropTable();
		thePatterns.dropTable();
		thePrices.dropTable();
		theRates.dropTable();
		theAccounts.dropTable();
		theTaxYears.dropTable();
		theFrequencys.dropTable();
		theTaxRegimes.dropTable();
		theTaxTypes.dropTable();
		theTransTypes.dropTable();
		theAccountTypes.dropTable();
		theStatic.dropTable();

		/* Return the data */
		return;
	}	

	/**
	 * Purge tables 
	 * @param pThread the thread control
	 * @return Continue <code>true/false</code>
	 */
	public void purgeTables(statusCtl 	pThread) throws Exception {
		
		/* Set the number of stages */
		if (!pThread.setNumStages(1)) return;
		
		/* Purge entries from tables */
		theEvents.purgeTable();
		thePatterns.purgeTable();
		thePrices.purgeTable();
		theRates.purgeTable();
		theAccounts.purgeTable();
		theTaxYears.purgeTable();
		theFrequencys.purgeTable();
		theTaxRegimes.purgeTable();
		theTaxTypes.purgeTable();
		theTransTypes.purgeTable();
		theAccountTypes.purgeTable();
		theStatic.purgeTable();

		/* Return the data */
		return;
	}	
}
