package uk.co.tolcroft.models.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.PropertySet;
import uk.co.tolcroft.models.PropertySet.PropertyManager;
import uk.co.tolcroft.models.PropertySet.PropertySetChooser;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.views.DataControl;

public abstract class Database<T extends DataSet<T>> implements PropertySetChooser {
	/**
	 * Properties for database
	 */
	private DatabaseProperties		theProperties	= null;
	
	/**
	 * Database connection
	 */
	private Connection          	theConn         = null;
	
	/**
	 * Batch Size 
	 */
	private Integer					theBatchSize	= null;
	
	/**
	 * List of Database tables
	 */
	private List<DatabaseTable<?>>	theTables		= null;

	@Override
	public Class<? extends PropertySet> getPropertySetClass() { return DatabaseProperties.class; }
	
	/**
	 * Database Properties
	 */
	public static class DatabaseProperties extends PropertySet {
		/**
		 * Registry name for DataBase driver
		 */
		protected final static String 	nameDBDriver	= "DBDriver";

		/**
		 * Registry name for DataBase server
		 */
		protected final static String 	nameDBServer	= "DBServer";

		/**
		 * Registry name for DataBase instance
		 */
		protected final static String 	nameDBInstance	= "DBInstance";

		/**
		 * Registry name for DataBase name
		 */
		protected final static String 	nameDBName		= "DBName";

		/**
		 * Registry name for DataBase batch size
		 */
		protected final static String 	nameDBBatch		= "DBBatchSize";

		/**
		 * Display name for DataBase driver
		 */
		protected final static String 	dispDBDriver	= "Database Driver Class";

		/**
		 * Display name for DataBase server
		 */
		protected final static String 	dispDBServer	= "Server Host Machine";

		/**
		 * Display name for DataBase instance
		 */
		protected final static String 	dispDBInstance	= "Server Instance";

		/**
		 * Display name for DataBase name
		 */
		protected final static String 	dispDBName		= "Database Name";

		/**
		 * Display name for DataBase batch size
		 */
		protected final static String 	dispDBBatch		= "Batch Size";

		/**
		 * Default Database driver string
		 */
		private final static JDBCDriver	defDBDriver		= JDBCDriver.SQLServer;

		/**
		 * Default Database connection string
		 */
		private final static String		defDBServer		= "localhost";
		
		/**
		 * Default Database instance
		 */
		private final static String		defDBInstance	= "SQLEXPRESS";		

		/**
		 * Default Database name
		 */
		private final static String		defDBName		= "Finance";		

		/**
		 * Default Database batch size
		 */
		private final static Integer	defDBBatch		= 50;		

		/**
		 * Constructor
		 * @throws ModelException
		 */
		public DatabaseProperties() throws ModelException { super();	}

		@Override
		protected void defineProperties() {
			/* Define the properties */
			defineProperty(nameDBDriver, JDBCDriver.class);
			defineProperty(nameDBServer, PropertyType.String);
			defineProperty(nameDBInstance, PropertyType.String);
			defineProperty(nameDBName, PropertyType.String);
			defineProperty(nameDBBatch, PropertyType.Integer);
		}

		@Override
		protected Object getDefaultValue(String pName) {
			/* Handle default values */
			if (pName.equals(nameDBDriver)) 	return defDBDriver;
			if (pName.equals(nameDBServer))		return defDBServer;
			if (pName.equals(nameDBInstance))	return defDBInstance;
			if (pName.equals(nameDBName))		return defDBName;
			if (pName.equals(nameDBBatch))		return defDBBatch;
			return null;
		}
		
		@Override
		protected String getDisplayName(String pName) {
			/* Handle default values */
			if (pName.equals(nameDBDriver)) 	return dispDBDriver;
			if (pName.equals(nameDBServer))		return dispDBServer;
			if (pName.equals(nameDBInstance))	return dispDBInstance;
			if (pName.equals(nameDBName))		return dispDBName;
			if (pName.equals(nameDBBatch))		return dispDBBatch;
			return null;
		}
		
		/**
		 * Obtain connection string 
		 * @return the connection string 
		 */
		private String getConnectionString() {
			StringBuilder myBuilder = new StringBuilder(100);
			
			/* Access the driver */
			JDBCDriver myDriver = getEnumValue(nameDBDriver, JDBCDriver.class);
			
			/* Build the connection string */
			myBuilder.append(myDriver.getPrefix());
			myBuilder.append(getStringValue(nameDBServer));
			myBuilder.append(";instanceName=");
			myBuilder.append(getStringValue(nameDBInstance));
			myBuilder.append(";database=");
			myBuilder.append(getStringValue(nameDBName));
			myBuilder.append(";integratedSecurity=true");
			
			/* Return the string */
			return myBuilder.toString();
		}
	}
	
	/**
	 * JDBCDriver
	 */
	public enum JDBCDriver {
		SQLServer;
		
		/**
		 * Obtain driver class
		 * @return the driver class
		 */
		public String getDriver() {
			switch (this) {
				case SQLServer: return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
				default: return null;
			}
		}

		/**
		 * Obtain connection prefix
		 * @return the connection prefix
		 */
		public String getPrefix() {
			switch (this) {
				case SQLServer: return "jdbc:sqlserver://";
				default: return null;
			}
		}
	}
	
	/**
	 * Construct a new Database class
	 * @param pProperties the database properties
	 */
	public Database() throws ModelException {
		/* Create the connection */
		try {
			/* Access the database properties */
			theProperties = (DatabaseProperties)PropertyManager.getPropertySet(this);
	
			/* Access the batch size */
			theBatchSize = theProperties.getIntegerValue(DatabaseProperties.nameDBBatch);
			
			/* Load the database driver */
			Class.forName(theProperties.getEnumValue(DatabaseProperties.nameDBDriver, JDBCDriver.class).getDriver());

			/* Obtain the connection */
			theConn = DriverManager.getConnection(theProperties.getConnectionString());
			theConn.setAutoCommit(false);
		}
		catch (Throwable e) {
			throw new ModelException(ExceptionClass.SQLSERVER,
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
	protected void			addTable(DatabaseTable<?> pTable) 	{
		pTable.getDefinition().resolveReferences(theTables);
		theTables.add(pTable);
	}
	
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
	public T loadDatabase(ThreadStatus<T> 	pThread) throws ModelException {
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
			throw new ModelException(ExceptionClass.LOGIC,
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
							   T 				pData) throws ModelException {
		boolean 		bContinue 	= true;
		BatchControl	myBatch		= new BatchControl(theBatchSize);
		
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
				throw new ModelException(ExceptionClass.SQLSERVER,
									"Failed to commit transction");				
			}
			
			/* Commit the batch */
			myBatch.commitItems();
		}
		
		/* Check for cancellation */
		if (!bContinue) 
			throw new ModelException(ExceptionClass.LOGIC,
								"Operation Cancelled");
	}
	
	/**
	 * Create tables 
	 * @param pThread the thread control
	 */
	public void createTables(ThreadStatus<T>	pThread) throws ModelException {
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
	private void dropTables(ThreadStatus<T>	pThread) throws ModelException {
		
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
	 */
	public void purgeTables(ThreadStatus<T>	pThread) throws ModelException {
		
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
