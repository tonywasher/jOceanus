package uk.co.tolcroft.models.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.Properties;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.views.DataControl;

public abstract class Database<T extends DataSet<T>> {
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
	 * @param pProperties the database properties
	 * @param pDataSet the set to load or use for updates
	 */
	public Database(Properties 	pProperties) throws Exception {
		/* Store the properties and DataSet */
		theProperties 	= pProperties;
		
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
		theTables.add(new TableControlKeys(this));
		theTables.add(new TableDataKeys(this));
		theTables.add(new TableControl(this));
	}
	
	/**
	 * Access the connection 
	 * @return the connection
	 */
	protected Connection 	getConn() { return theConn; }
	
	/**
	 * Add a table 
	 * @param pTable the Table to add
	 */
	protected void			addTable(DatabaseTable<?> pTable) 	{ theTables.add(pTable); }
	
	/**
	 * RollBack and disconnect on termination
	 */
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	/**
	 * Close the connection to the database 
	 * rolling back any outstanding transaction
	 */
	protected void close() {
		/* Ignore if no connection */
		if (theConn != null) return;

		/* Protect against exceptions */
		try {
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
		
		/* Discard Exceptions */
		catch (Throwable e) {}
	}

	/**
	 * Load data from the database 
	 * @param pThread the thread control
	 * @return the new DataSet
	 */
	public T loadDatabase(ThreadStatus<T> 	pThread) throws Exception {
		boolean bContinue 	= true;
		
		/* Set the number of stages */
		if (!pThread.setNumStages(1+theTables.size())) return null;
		
		/* Create an empty DataSet */
		DataControl<T> 	myView = pThread.getControl();
		T				myData = myView.getNewData();
		
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
	public void updateDatabase(ThreadStatus<T>	pThread,
							   T 				pData) throws Exception {
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
				close();
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
	public void createTables(ThreadStatus<T>	pThread) throws Exception {
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
	private void dropTables(ThreadStatus<T>	pThread) throws Exception {
		
		/* Set the number of stages */
		if (!pThread.setNumStages(1)) return;
		
		/* Create the iterator */
		ListIterator<DatabaseTable<?>> 	myIterator;
		DatabaseTable<?>				myTable;
		myIterator = theTables.listIterator(theTables.size());
		
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
	public void purgeTables(ThreadStatus<T>	pThread) throws Exception {
		
		/* Set the number of stages */
		if (!pThread.setNumStages(1)) return;
		
		/* Create the iterator */
		ListIterator<DatabaseTable<?>> 	myIterator;
		DatabaseTable<?>				myTable;
		myIterator = theTables.listIterator(theTables.size());
		
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
