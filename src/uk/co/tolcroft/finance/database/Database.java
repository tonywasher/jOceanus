package uk.co.tolcroft.finance.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import uk.co.tolcroft.finance.core.Threads.statusCtl;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class Database {
	/**
	 * Properties for application
	 */
	private Properties				theProperties	= null;
	
	/**
	 * Database connection
	 */
	private Connection          	theConn         = null;
	
	/**
	 * List of Database tables
	 */
	private List<DatabaseTable<?>>	theTables		= null;
	
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
		
		/* Create table list and add the tables to the list */
		theTables = new ArrayList<DatabaseTable<?>>();
		theTables.add(new TableControl(this));
		theTables.add(new TableAccountType(this));
		theTables.add(new TableTransactionType(this));
		theTables.add(new TableTaxType(this));
		theTables.add(new TableTaxRegime(this));
		theTables.add(new TableFrequency(this));
		theTables.add(new TableTaxYear(this));
		theTables.add(new TableAccount(this));
		theTables.add(new TableRate(this));
		theTables.add(new TablePrice(this));
		theTables.add(new TablePattern(this));
		theTables.add(new TableEvent(this));
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
		
		/* Create the iterator */
		Iterator<DatabaseTable<?>> 	myIterator;
		DatabaseTable<?>			myTable;
		myIterator = theTables.iterator();
		
		/* Loop through the tables */
		while (myIterator.hasNext()) {
			myTable = myIterator.next();
			
			/* Close the Statement */
			myTable.closeStmt();
		}

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
		if (!pThread.setNumStages(1+theTables.size())) return null;
		
		/* Access the view */
		
		/* Create the new DataSet */
		myView = pThread.getView();
		myData = new DataSet(myView.getSecurity()); 
			
		/* Create the iterator */
		Iterator<DatabaseTable<?>> 	myIterator;
		DatabaseTable<?>			myTable;
		myIterator = theTables.iterator();
		
		/* Loop through the tables */
		while ((bContinue) &&
			   (myIterator.hasNext())) {
			myTable = myIterator.next();
			
			/* Load the items */
			bContinue = myTable.loadItems(pThread, myData);
		}
		
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
	public void updateDatabase(statusCtl 	pThread,
							   DataSet 		pData) throws Exception {
		boolean 		bContinue 	= true;
		BatchControl	myBatch		= new BatchControl();
		
		/* Set the number of stages */
		if (!pThread.setNumStages(3*theTables.size())) return;
		
		/* Create the iterator */
		Iterator<DatabaseTable<?>> 	myIterator;
		DatabaseTable<?>			myTable;
		myIterator = theTables.iterator();
		
		/* Loop through the tables */
		while ((bContinue) &&
			(myIterator.hasNext())) {
			myTable = myIterator.next();
			
			/* Load the items */
			bContinue = myTable.insertItems(pThread, pData, myBatch);
		}

		/* Create the list iterator */
		ListIterator<DatabaseTable<?>> 	myListIterator;
		myListIterator = theTables.listIterator();
		
		/* Loop through the tables */
		while ((bContinue) &&
			   (myListIterator.hasNext())) {
			myTable = myListIterator.next();
			
			/* Load the items */
			bContinue = myTable.updateItems(pThread, myBatch);
		}
		
		/* Loop through the tables in reverse order */
		while ((bContinue) &&
			   (myListIterator.hasPrevious())) {
			myTable = myListIterator.previous();
			
			/* Delete items from the table */
			bContinue = myTable.deleteItems(pThread, myBatch);
		}
		
		/* If we have active work in the batch */
		if ((bContinue) && (myBatch.isActive())) {
			/* Commit the database */
			try { theConn.commit(); }
			catch (Throwable e) {
				throw new Exception(ExceptionClass.SQLSERVER,
									"Failed to commit transction");				
			}
			
			/* Commit the batch */
			myBatch.commitItems();
		}
		
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
		
		/* Create the iterator */
		Iterator<DatabaseTable<?>> 	myIterator;
		DatabaseTable<?>			myTable;
		myIterator = theTables.iterator();
		
		/* Loop through the tables */
		while (myIterator.hasNext()) {
			myTable = myIterator.next();
			
			/* Create the table */
			myTable.createTable();
		}
	}	

	/**
	 * Drop tables 
	 * @param pThread the thread control
	 * @return Continue <code>true/false</code>
	 */
	private void dropTables(statusCtl 	pThread) throws Exception {
		
		/* Set the number of stages */
		if (!pThread.setNumStages(1)) return;
		
		/* Create the iterator */
		ListIterator<DatabaseTable<?>> 	myIterator;
		DatabaseTable<?>			myTable;
		myIterator = theTables.listIterator();
		
		/* Loop through the tables to the end of the list */
		while (myIterator.hasNext()) {
			myTable = myIterator.next();
		}
		
		/* Loop through the tables in reverse order */
		while (myIterator.hasPrevious()) {
			myTable = myIterator.previous();
			
			/* Drop the table */
			myTable.dropTable();
		}
		
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
		
		/* Create the iterator */
		ListIterator<DatabaseTable<?>> 	myIterator;
		DatabaseTable<?>			myTable;
		myIterator = theTables.listIterator();
		
		/* Loop through the tables to the end of the list */
		while (myIterator.hasNext()) {
			myTable = myIterator.next();
		}
		
		/* Loop through the tables in reverse order */
		while (myIterator.hasPrevious()) {
			myTable = myIterator.previous();
			
			/* Purge the table */
			myTable.purgeTable();
		}
		
		/* Return the data */
		return;
	}	
}
