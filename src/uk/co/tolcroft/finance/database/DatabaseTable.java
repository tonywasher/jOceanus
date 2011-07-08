package uk.co.tolcroft.finance.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import uk.co.tolcroft.finance.core.Threads.statusCtl;
import uk.co.tolcroft.finance.data.DataSet;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.Number;

public abstract class DatabaseTable<T extends DataItem> {
	/**
	 * The Id column name
	 */
	protected final static String 	theIdCol   		= "ID";
	
	/**
	 * The Database control
	 */
	private Database				theDatabase		= null;

	/**
	 * The Database connection
	 */
	private Connection				theConn			= null;

	/**
	 * The table name
	 */
	private String 					theTableName	= null;
	
	/**
	 * The batch size
	 */
	private int 					theBatchSize	= Database.BATCH_SIZE;
	
	/**
	 * The list of items for this table
	 */
	private DataList<T>				theList  		= null;
	
	/**
	 * The prepared statement
	 */
	private PreparedStatement   	theStmt         = null;
		
	/**
	 * The result set
	 */
	private ResultSet           	theResults      = null;
	
	/**
	 * The update string
	 */
	private String              	theUpdates      = null;
	
	/**
	 * The update parameters
	 */
	private ParameterList          	theParms      	= null;

	/**
	 * Constructor
	 */
	protected DatabaseTable(Database 	pDatabase, 
						  	String 		pTable) {
		/* Set the table */
		theDatabase		= pDatabase;
		theConn	 		= theDatabase.getConn();
		theTableName 	= pTable;
		theParms 		= new ParameterList();
	}
	
	/**
	 * Access the table name 
	 * @return the table name
	 */
	protected String getTableName() { return theTableName; }
	
	/** 
	 *  Close the result set and statement
	 */
	protected void closeStmt() throws SQLException {
		theParms.clear();
		theUpdates = null;
		if (theResults != null) theResults.close();
		if (theStmt    != null) theStmt.close();
	}
	
	/** 
	 *  Shift to next line in result set
	 */
	private boolean next() throws SQLException {
		theParms.clear();
		return theResults.next();
	}
	
	/** 
	 *  Prepare the statement
	 *  @param pStatement the statement to prepare
	 */
	private void prepareStatement(String pStatement) throws SQLException {
		theStmt	= theConn.prepareStatement(pStatement);
	}
	
	/** 
	 *  Execute the prepared statement
	 */
	private void execute() throws SQLException {
		theStmt.executeUpdate();
		theParms.clear();
	}
	
	/** 
	 *  Query the prepared statement
	 */
	private void executeQuery() throws SQLException {
		theResults = theStmt.executeQuery();
	}
	
	/** 
	 *  Commit the update
	 */
	private void commit() throws SQLException {
		theConn.commit();
	}
	
	/**
	 * Count the number of items to be loaded
	 * @return the count of items
	 * @throws SQLException
	 */
	protected int countItems() throws SQLException {
		String myString;
		int    myCount = 0;

		myString   = "select count(*) from " + theTableName;
		prepareStatement(myString);
		executeQuery();
		
		/* Loop through the results */
		while (theResults.next()) {
			/* Get the count */
			myCount = theResults.getInt(1);
		}
		
		/* Close the Statement */
		closeStmt();
		
		/* Return the count */
		return myCount;
	}
	
	/**
	 * Get the List for the table for loading
	 * @param pData the Data set
	 * @return pList the target list for loading
	 */
	protected abstract DataList<T>  getLoadList(DataSet pData);
	
	/**
	 * Get the List for the table for updates
	 * @param pData the Data set
	 * @return pList the source extract list for updates
	 */
	protected abstract DataList<T>  getUpdateList(DataSet pData);
	
	/**
	 * get the query string for loading items from the database
	 * @return the query statement
	 */
	protected abstract String 	loadStatement();
	
	/**
	 * get the insert string for inserting items into the database
	 * @return the insert statement
	 */
	protected abstract String 	insertStatement();
	
	/**
	 * get the create string for creating the table in the database
	 * @return the insert statement
	 */
	protected abstract String 	createStatement();
	
	/**
	 * get the delete string for deleting items from the database
	 * @return the delete statement
	 */
	protected String deleteStatement() {
		return "delete from " + theTableName + 
		       " where " + theIdCol + "=?";
	}
		
	/**
	 * get the drop string for deleting the table from the database
	 * @return the delete statement
	 */
	protected String dropStatement() {
		return "if exists (select * from sys.tables where name = '" +
			   theTableName + "') drop table " + theTableName;
	}
	
	/**
	 * get the drop string for deleting the table from the database
	 * @return the delete statement
	 */
	protected String purgeStatement() {
		return "delete from " + theTableName;
	}
	
	/**
	 * Obtain the list of items
	 */
	protected DataList<T>	getList() { return theList; }
	
	/**
	 * Determine the name of the items in the list
	 * @return the name
	 */
	protected abstract String 	getItemsName();
	
	/**
	 * Load an individual item from the result set 
	 */
	protected abstract void   	loadItem()	throws Exception;
	
	/**
	 * Insert an individual item from the list
	 * @param pItem the item to insert
	 */
	protected abstract void   	insertItem(T	pItem)	throws Exception;
	
	/**
	 * Update an individual item from the list
	 * @param pItem the item to update
	 */
	protected abstract void		updateItem(T	pItem)	throws Exception;
	
	/**
	 * Load items from the list into the table
	 * @param pThread the thread control
	 * @param pData the data
	 * @return Continue <code>true/false</code>
	 */
	protected boolean loadItems(statusCtl 	pThread,
								DataSet		pData) throws Exception {
		boolean bContinue = true;
		String	myQuery;
		int		mySteps;
		int     myCount   = 0;
		
		/* Declare the new stage */
		if (!pThread.setNewStage(getItemsName())) return false;

		/* Record the list for this load */
		theList = getLoadList(pData); 
			
		/* Access reporting steps */
		mySteps = pThread.getReportingSteps();
		
		/* Protect the load */
		try {
			/* Count the Items to be loaded */
			if (!pThread.setNumSteps(countItems())) return false;
			
			/* Load the items from the table */
			myQuery = loadStatement();
			prepareStatement(myQuery);
			executeQuery();
		
			/* Loop through the results */
			while (next()) {
				/* Load the next item */
				loadItem();
				
				/* Report the progress */
				myCount++;
				if ((myCount % mySteps) == 0) 
					if (!pThread.setStepsDone(myCount)) return false;
			}
			
			/* Close the Statement */
			closeStmt();
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
					            "Failed to load " + getItemsName(),
					            e);
		}
		
		/* Return to caller */
		return bContinue;
	}
	
	/**
	 * Mark an item as committed
	 * @param pItem the item
	 */
	private void commitItem(T pItem) {
		/* Handle Deletions */
		if (pItem.getState() == DataState.DELETED)
			pItem.getBase().unLink();
	
		/* else  */
		else { 
			/* Set the item to clean and clear history */
			pItem.getBase().setState(DataState.CLEAN);
			pItem.getBase().clearHistory();
		}

		/* Mark this item as clean */
		pItem.setState(DataState.CLEAN);
	}
	
	/**
	 * Mark a batch of updates as committed
	 * @param pState the state of the items to update
	 */
	private void commitBatch(DataState pState)	throws Exception {
		DataList<T>.ListIterator	myIterator;
		T							myCurr;
		int 						iBatch = 0;
		
		/* Protect the commit */
		try {
			/* Commit the update */
			commit();
				
			/* Access the iterator */
			myIterator = theList.listIterator(true);
			
			/* Loop through the list */
			while ((myCurr = myIterator.next()) != null) {
				/* Ignore items that are not this type */
				if ((pState != DataState.NOSTATE) && 
					(myCurr.getState() != pState)) continue;
				
				/* commit the Item */
				commitItem(myCurr);
				
				/* Handle end of batch */
				if ((theBatchSize > 0) &&
					(++iBatch >= theBatchSize)) break;
			}
		}	
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								"Failed to commit " + getItemsName(),
								e);
		}
	}
	
	/**
	 * Determine the count of items that are in a particular state
	 * @param pState the particular state
	 * @return the count of items
	 */
	private int countItems(DataState pState) {
		DataList<T>.ListIterator	myIterator;
		T							myCurr;
		int 						iCount = 0;
		
		/* Access the iterator */
		myIterator = theList.listIterator(true);
		
		/* Loop through the list */
		while ((myCurr = myIterator.next()) != null) {
			/* Ignore items that are not this type */
			if (myCurr.getState() != pState) continue;

			/* Increment count */
			++iCount;
		}
		
		/* Return count */
		return iCount;
	}
	
	/**
	 * Insert new items from the list
	 * @param pThread the thread control
	 * @param pData the data
	 * @return Continue <code>true/false</code>
	 */
	protected boolean insertItems(statusCtl 	pThread,
								  DataSet		pData) throws Exception {
		DataList<T>.ListIterator	myIterator;
		T							myCurr    = null;
		int                 		iBatch    = 0;
		int     					myCount   = 0;
		int							mySteps;
		String						myInsert;
		boolean             		bContinue = true;
		
		/* Declare the new stage */
		if (!pThread.setNewStage("Inserting " + getItemsName())) return false;
		
		/* Access reporting steps */
		mySteps = pThread.getReportingSteps();
		
		/* Record the list for this update operation */
		theList = getUpdateList(pData); 
			
		/* Protect the insert */
		try {
			/* Declare the number of steps */
			if (!pThread.setNumSteps(countItems(DataState.NEW))) return false;
			
			/* Prepare the insert statement */
			myInsert = insertStatement();
			prepareStatement(myInsert);
		
			/* Access the iterator */
			myIterator = theList.listIterator(true);
			
			/* Loop through the list */
			while ((myCurr = myIterator.next()) != null) {
				/* Ignore non-new items */
				if (myCurr.getState() != DataState.NEW) continue;
				
				/* Set the fields */
				insertItem(myCurr);
			
				/* Execute the insert */
				execute();
				myCurr = null;
				
				/* If we should commit the batch */
				if ((theBatchSize > 0) &&
					(++iBatch >= theBatchSize)) {
					/* Reset the batch count */
					iBatch = 0;
					
					/* Commit the batch */
					commitBatch(DataState.NEW);
				}
				
				/* Report the progress */
				myCount++;
				if ((myCount % mySteps) == 0) 
					if (!pThread.setStepsDone(myCount)) return false;
			}
								
			/* Handle outstanding commits */
			if (iBatch > 0)	commitBatch(DataState.NEW);
									
			/* Close the Statement */
			closeStmt();
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								myCurr,
					            "Failed to insert " + getItemsName(),
					            e);
		}
		
		/* Return to caller */
		return bContinue;
	}
	
	/**
	 * Update items from the list
	 * @param pThread the thread control
	 * @return Continue <code>true/false</code>
	 */
	protected boolean updateItems(statusCtl 	pThread) throws Exception {
		DataList<T>.ListIterator	myIterator;
		T							myCurr    = null;
		int              			iBatch    = 0;
		int     					myCount   = 0;
		int							mySteps;
		boolean          			bContinue = true;
	
		/* Declare the new stage */
		if (!pThread.setNewStage("Updating " + getItemsName())) return false;
		
		/* Access reporting steps */
		mySteps = pThread.getReportingSteps();
		
		/* Protect the update */
		try {
			/* Declare the number of steps */
			if (!pThread.setNumSteps(countItems(DataState.CHANGED))) return false;
			
			/* Access the iterator */
			myIterator = theList.listIterator(true);
			
			/* Loop through the list */
			while ((myCurr = myIterator.next()) != null) {
				/* Ignore non-changed items */
				if (myCurr.getState() != DataState.CHANGED) continue;
				
				/* Update the item */
				updateItem(myCurr);
									
				/* Set Id and execute update */
				updateId(myCurr.getId());
				myCurr = null;
					
				/* Close the Statement */
				closeStmt();
				
				/* If we should commit the batch */
				if ((theBatchSize > 0) &&
					(++iBatch >= theBatchSize)) {
					/* Reset the batch count */
					iBatch = 0;
					
					/* Commit the batch */
					commitBatch(DataState.CHANGED);
				}
				
				/* Report the progress */
				myCount++;
				if ((myCount % mySteps) == 0) 
					if (!pThread.setStepsDone(myCount)) return false;
			}
								
			/* Handle outstanding commits */
			if (iBatch > 0)	commitBatch(DataState.CHANGED);
		}
	
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								myCurr,
								"Failed to update " + getItemsName(),
								e);
		}
		
		/* Return to caller */
		return bContinue;
	}
	
	/**
	 * Delete items from the list
	 * @param pThread the thread control
	 * @return Continue <code>true/false</code>
	 */
	protected boolean deleteItems(statusCtl 	pThread) throws Exception {
		DataList<T>.ListIterator	myIterator;
		T							myCurr    = null;
		int              			iBatch    = 0;
		int     					myCount   = 0;
		int							mySteps;
		String						myDelete;
		boolean          			bContinue = true;
	
		/* Declare the new stage */
		if (!pThread.setNewStage("Deleting " + getItemsName())) return false;
	
		/* Access reporting steps */
		mySteps = pThread.getReportingSteps();
		
		/* Protect the delete */
		try {
			/* Declare the number of steps */
			if (!pThread.setNumSteps(countItems(DataState.DELETED))) return false;
			
			/* Prepare the delete statement */
			myDelete = deleteStatement();
			prepareStatement(myDelete);
		
	
			/* Access the iterator */
			myIterator = theList.listIterator(true);
			
			/* Loop through the list in reverse order */
			while ((myCurr = myIterator.previous()) != null) {
				/* Ignore non-deleted items */
				if (myCurr.getState() != DataState.DELETED) continue;
				
				/* DelNew items are just discarded */
				if (myCurr.getBase().getState() == DataState.DELNEW) {
					commitItem(myCurr);
					continue;
				}
				
				/* Set the id */
				setInteger(myCurr.getId());
		
				/* Execute the delete */
				execute();
				myCurr = null;
				
				/* If we should commit the batch */
				if ((theBatchSize > 0) &&
					(++iBatch >= theBatchSize)) {
					/* Reset the batch count */
					iBatch = 0;
					
					/* Commit the batch */
					commitBatch(DataState.DELETED);
				}
				
				/* Report the progress */
				myCount++;
				if ((myCount % mySteps) == 0) 
					if (!pThread.setStepsDone(myCount)) return false;
			}
								
			/* Handle outstanding commits */
			if (iBatch > 0)	commitBatch(DataState.DELETED);
							
			/* Close the Statement */
			closeStmt();
		}
	
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								myCurr,
								"Failed to delete " + getItemsName(),
								e);
		}
		
		/* Return to caller */
		return bContinue;
	}
	
	/**
	 * Create the table
	 */
	protected void createTable() throws Exception {
		String	myCreate;
	
		/* Protect the create */
		try {
			/* Prepare the create statement */
			myCreate = createStatement();
			prepareStatement(myCreate);
			
			/* Execute the delete */
			execute();
			commit();
				
			/* Close the Statement */
			closeStmt();
		}
	
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								"Failed to create " + getItemsName(),
								e);
		}
		
		/* Return to caller */
		return;
	}

	/**
	 * Drop the table
	 */
	protected void dropTable() throws Exception {
		String	myDrop;
	
		/* Protect the drop */
		try {
			/* Prepare the drop statement */
			myDrop = dropStatement();
			prepareStatement(myDrop);
			
			/* Execute the delete */
			execute();
			commit();
				
			/* Close the Statement */
			closeStmt();
		}
	
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								"Failed to drop " + getItemsName(),
								e);
		}
		
		/* Return to caller */
		return;
	}

	/**
	 * Truncate the table
	 */
	protected void purgeTable() throws Exception {
		String	myTrunc;
	
		/* Protect the truncate */
		try {
			/* Prepare the truncate statement */
			myTrunc = purgeStatement();
			prepareStatement(myTrunc);
			
			/* Execute the delete */
			execute();
			commit();
				
			/* Close the Statement */
			closeStmt();
		}
	
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								"Failed to purge " + getItemsName(),
								e);
		}
		
		/* Return to caller */
		return;
	}
		
	/** 
	 *  Get a string field
	 *  @return the string value
	 */
	protected String getString() throws SQLException {
		/* Increment the index and set the string */
		theParms.theIndex++;
		return theResults.getString(theParms.theIndex);
	}
	
	/** 
	 *  Get a long field
	 *  @return the long value
	 */
	protected Long getLong() throws SQLException {
		/* Increment the index and set the string */
		theParms.theIndex++;
		long myValue = theResults.getLong(theParms.theIndex);
		if ((myValue == 0) && (theResults.wasNull())) return null;
		return myValue;
	}
	
	/** 
	 *  Get an integer field
	 *  @return the integer value
	 */
	protected Integer getInteger() throws SQLException {
		/* Increment the index and set the string */
		theParms.theIndex++;
		int myValue = theResults.getInt(theParms.theIndex);
		if ((myValue == 0) && (theResults.wasNull())) return null;
		return myValue;
	}
	
	/** 
	 *  Get a boolean field
	 *  @return the boolean value
	 */
	protected Boolean getBoolean() throws SQLException {
		/* Increment the index and set the string */
		theParms.theIndex++;
		boolean myValue = theResults.getBoolean(theParms.theIndex);
		if ((myValue == false) && (theResults.wasNull())) return null;
		return myValue;
	}
	
	/** 
	 *  Get a date field
	 *  @return the date value
	 */
	protected java.util.Date getDate() throws SQLException {
		/* Increment the index and set the string */
		theParms.theIndex++;
		return theResults.getDate(theParms.theIndex);
	}
	
	/** 
	 *  Get a binary field
	 *  @return the binary value
	 */
	protected byte[] getBinary() throws SQLException {
		/* Increment the index and set the string */
		theParms.theIndex++;
		return theResults.getBytes(theParms.theIndex);
	}
	
	/** 
	 *  Set a number field
	 *  @param pValue the number value
	 */
	protected void setNumber(Number pValue) throws SQLException {
		String myString = null;
		if (pValue != null) myString = pValue.format(false);
		setString(myString);
	}
	
	/** 
	 *  Set a string field
	 *  @param pValue the string value
	 */
	protected void setString(String pValue) throws SQLException {
		/* Increment the index and set the string */
		theParms.theIndex++;
		Parameter.applyString(theStmt, theParms.theIndex, pValue);
	}
	
	/** 
	 *  Set a Long field
	 *  @param pValue the long value
	 */
	protected void setLong(Long pValue) throws SQLException {
		/* Increment the index and set the string */
		theParms.theIndex++;
		Parameter.applyLong(theStmt, theParms.theIndex, pValue);
	}
	
	/** 
	 *  Set an Integer field
	 *  @param pValue the integer value
	 */
	protected void setInteger(Integer pValue) throws SQLException {
		/* Increment the index and set the string */
		theParms.theIndex++;
		Parameter.applyInteger(theStmt, theParms.theIndex, pValue);
	}
	
	/** 
	 *  Set a boolean field
	 *  @param pValue the boolean value
	 */
	protected void setBoolean(Boolean pValue) throws SQLException {
		/* Increment the index and set the string */
		theParms.theIndex++;
		Parameter.applyBoolean(theStmt, theParms.theIndex, pValue);
	}
	
	/** 
	 *  Set a date field
	 *  @param pValue the date value
	 */
	protected void setDate(Date pValue) throws SQLException {
		/* Increment the index and set the string */
		theParms.theIndex++;
		Parameter.applyDate(theStmt, theParms.theIndex, pValue);
	}
	
	/** 
	 *  Set a binary field
	 *  @param pValue the binary value
	 */
	protected void setBinary(byte[] pValue) throws SQLException {
		/* Increment the index and set the string */
		theParms.theIndex++;
		Parameter.applyBinary(theStmt, theParms.theIndex, pValue);
	}
	
	/** 
	 *  Update a number field
	 *  @param pField the field name
	 *  @param pValue the string value
	 */
	protected void updateNumber(String pField,
			               	 	Number pValue) {
		/* Format the number and apply as a string */
		String myString = null;
		if (pValue != null) myString = pValue.format(false);
		updateString(pField, myString);
	}
	
	/** 
	 *  Update a string field
	 *  @param pField the field name
	 *  @param pValue the string value
	 */
	protected void updateString(String pField,
			               	 	String pValue) {
		/* Adjust the statement */
		if (theUpdates != null) theUpdates += ", "; else theUpdates = "";
		theUpdates += pField + "=?";
		
		/* Add the parameter to the parameter list */
		theParms.addParameter(ParmType.String, pValue);
	}
	
	/** 
	 *  Update a Long field
	 *  @param pField the field name
	 *  @param pValue the long value
	 */
	protected void updateLong(String 	pField,
			               	  Long		pValue) {
		/* Adjust the statement */
		if (theUpdates != null) theUpdates += ", "; else theUpdates = "";
		theUpdates += pField + "=?";
		
		/* Add the parameter to the parameter list */
		theParms.addParameter(ParmType.Long, pValue);
	}
	
	/** 
	 *  Update an integer field
	 *  @param pField the field name
	 *  @param pValue the string value
	 */
	protected void updateInteger(String 	pField,
			               	     Integer	pValue) {
		/* Adjust the statement */
		if (theUpdates != null) theUpdates += ", "; else theUpdates = "";
		theUpdates += pField + "=?";
		
		/* Add the parameter to the parameter list */
		theParms.addParameter(ParmType.Integer, pValue);
	}
	
	/** 
	 *  Update a boolean field
	 *  @param pField the field name
	 *  @param pValue the boolean value
	 */
	protected void updateBoolean(String 	pField,
			               	 	 Boolean	pValue) {
		/* Adjust the statement */
		if (theUpdates != null) theUpdates += ", "; else theUpdates = "";
		theUpdates += pField + "=?";
		
		/* Add the parameter to the parameter list */
		theParms.addParameter(ParmType.Boolean, pValue);
	}
	
	/** 
	 *  Update a string field
	 *  @param pField the field name
	 *  @param pValue the string value
	 */
	protected void updateDate(String pField,
			               	  Date	 pValue) {
		/* Adjust the statement */
		if (theUpdates != null) theUpdates += ", "; else theUpdates = "";
		theUpdates += pField + "=?";
		
		/* Add the parameter to the parameter list */
		theParms.addParameter(ParmType.Date, pValue);
	}
	
	/** 
	 *  Update a binary field
	 *  @param pField the field name
	 *  @param pValue the binary value
	 */
	protected void updateBinary(String 	pField,
			               	 	byte[]	pValue) {
		/* Adjust the statement */
		if (theUpdates != null) theUpdates += ", "; else theUpdates = "";
		theUpdates += pField + "=?";
		
		/* Add the parameter to the parameter list */
		theParms.addParameter(ParmType.Binary, pValue); 
	}
	
	/**
	 * Execute the updates for the specified id
	 * @param uId the ID of the item
	 * @throws SQLException
	 */
	protected void updateId(int uId) throws SQLException {
		String myString;
		
		/* If we have updates */
		if (theUpdates != null) {
			/* Build the SQL statement */
			myString = "update " + theTableName + " set " +
			           theUpdates + " where " + theIdCol +
			           " = " + uId;
			
			/* Prepare the statement and apply the parameters */
			prepareStatement(myString);
			theParms.applyParameters(theStmt);
			
			/* Execute the updates and reset the parameters */
			execute();
			theParms.clear();
		}
	}
	
	/**
	 * parameter list class 
	 */
	private class ParameterList {
		/**
		 * The first parameter
		 */
		private Parameter 	theFirst 	= null;

		/**
		 * The last parameter
		 */
		private Parameter 	theLast		= null;
		
		/**
		 * The first parameter
		 */
		private int 		theIndex 	= 0;
		
		/**
		 * Add a parameter to the list
		 * @param pType the parameter type
		 * @param pObject the object
		 */
		private void addParameter(ParmType 	pType,
								  Object	pValue) {
			Parameter myParm;
			
			/* Allocate the new parameter */
			theIndex++;
			myParm = new Parameter(theIndex, pType, pValue);
			
			/* If this is the first element */
			if (theLast == null) {
				/* Record as the only element */
				theFirst = myParm;
				theLast  = myParm;
			}
			
			else {
				/* Record as last element */
				theLast.theNext = myParm;
				theLast			= myParm;
			}
		}

		/**
		 * Apply parameters
		 * @param pStmt the Statement to apply the parameter to
		 */
		private void applyParameters(PreparedStatement pStmt) throws SQLException {
			Parameter myParm;
			
			/* Loop through the parameters */
			for (myParm = theFirst;
			     myParm != null;
			     myParm  = myParm.theNext) {
				/* Apply the parameter */
				myParm.applyParm(pStmt);
			}
		}

		/**
		 * Clear parameters
		 */
		private void clear() {
			Parameter myParm;

			/* Reset details */
			while (theFirst != null) { 
				myParm 			 = theFirst.theNext;
				theFirst.theNext = null;
				theFirst 		 = myParm;
			}
			theLast  = null;
			theIndex = 0;
		}
	}
	
	/**
	 * parameter class 
	 */
	private static class Parameter {
		/**
		 * Parameter index
		 */
		private int 		theIndex = -1;
		
		/**
		 * Parameter type
		 */
		private ParmType	theType		= null;
		
		/**
		 * Parameter value
		 */
		private Object		theValue	= null;
		
		/**
		 * Next parameter
		 */
		private Parameter	theNext		= null;
		
		/**
		 * Constructor
		 * @param iIndex the index
		 * @param pType the parameter type
		 * @param pValue the object
		 */
		private Parameter(int 		iIndex,
						  ParmType 	pType,
						  Object	pValue) {
			/* Store the values */
			theIndex = iIndex;
			theType  = pType;
			theValue = pValue;
		}
		
		/** 
		 * Apply parameter
		 * @param pStmt the Statement to apply the parameter to
		 */
		private void applyParm(PreparedStatement pStmt) throws SQLException {
			
			/* Switch to apply the appropriate value */
			switch (theType) {
				case String:
					applyString(pStmt, theIndex, (String)theValue);
					break;
				case Long:
					applyLong(pStmt, theIndex, (Long)theValue);
					break;
				case Integer:
					applyInteger(pStmt, theIndex, (Integer)theValue);
					break;
				case Boolean:
					applyBoolean(pStmt, theIndex, (Boolean)theValue);
					break;
				case Date:
					applyDate(pStmt, theIndex, (Date)theValue);
					break;
				case Binary:
					applyBinary(pStmt, theIndex, (byte[])theValue);
					break;
			}
		}

		/** 
		 * Apply String
		 * @param pStmt the Statement to apply the parameter to
		 * @param iIndex the index
		 * @param pValue the value
		 */
		private static void applyString(PreparedStatement 	pStmt,
								 		int			   		pIndex,
								 		String			   	pValue) throws SQLException {
			
			/* Apply the string value */
			pStmt.setString(pIndex, pValue);
		}

		/** 
		 * Apply Long
		 * @param pStmt the Statement to apply the parameter to
		 * @param iIndex the index
		 * @param pValue the value
		 */
		private static void applyLong(PreparedStatement pStmt,
								 	  int			   	pIndex,
								 	  Long			   	pValue) throws SQLException {
			
			/* Apply the long value */
			if (pValue == null) pStmt.setNull(pIndex, Types.BIGINT);
			else pStmt.setLong(pIndex, pValue);
		}

		/** 
		 * Apply Integer
		 * @param pStmt the Statement to apply the parameter to
		 * @param iIndex the index
		 * @param pValue the value
		 */
		private static void applyInteger(PreparedStatement 	pStmt,
								 	     int				pIndex,
								 	     Integer		   	pValue) throws SQLException {
			
			/* Apply the integer value */
			if (pValue == null) pStmt.setNull(pIndex, Types.INTEGER);
			else pStmt.setInt(pIndex, pValue);
		}

		/** 
		 * Apply Boolean
		 * @param pStmt the Statement to apply the parameter to
		 * @param iIndex the index
		 * @param pValue the value
		 */
		private static void applyBoolean(PreparedStatement 	pStmt,
								 	     int				pIndex,
								 	     Boolean		   	pValue) throws SQLException {
			
			/* Apply the integer value */
			if (pValue == null) pStmt.setNull(pIndex, Types.BIT);
			else pStmt.setBoolean(pIndex, pValue);
		}
		
		/** 
		 * Apply Date
		 * @param pStmt the Statement to apply the parameter to
		 * @param iIndex the index
		 * @param pValue the value
		 */
		private static void applyDate(PreparedStatement 	pStmt,
								 	  int			   		pIndex,
								 	  Date					pValue) throws SQLException {			
			java.sql.Date myDate = null;
			
			/* Build the date as a SQL date */
			if ((pValue != null) && (pValue.getDate() != null))
				myDate = new java.sql.Date(pValue.getDate().getTime()); 
				
			/* Apply the date value */
			pStmt.setDate(pIndex, myDate);
		}

		/** 
		 * Apply Binary
		 * @param pStmt the Statement to apply the parameter to
		 * @param iIndex the index
		 * @param pValue the value
		 */
		private static void applyBinary(PreparedStatement 	pStmt,
								 		int			   		pIndex,
								 		byte[]			   	pValue) throws SQLException {
			
			/* Apply the binary value */
			pStmt.setBytes(pIndex, pValue);
		}
	}
	
	/**
	 * Update parameter enumeration
	 */
	private enum ParmType {
		/**
		 * String
		 */
		String,
		
		/**
		 * Long
		 */
		Long,
		
		/**
		 * Integer
		 */
		Integer,
		
		/**
		 * Date
		 */
		Date,
		
		/**
		 * Boolean
		 */
		Boolean,
		
		/**
		 * Binary 
		 */
		Binary;
	}
}
