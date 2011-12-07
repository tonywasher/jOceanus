package uk.co.tolcroft.models.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import uk.co.tolcroft.models.Date;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.Number.Money;
import uk.co.tolcroft.models.Number.Rate;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.security.CipherSet;
import uk.co.tolcroft.models.security.SymmetricKey;

public class TableDefinition {
	/**
	 * The Column Array expansion size
	 */
	private final static int		COLUMN_BLOCKSIZE	= 5;
	
	/**
	 * The Table name
	 */
	private String					theTableName	= null;
	
	/**
	 * The Column Definitions 
	 */
	private List<ColumnDefinition>	theList			= null;

	/**
	 * The Sort List 
	 */
	private List<ColumnDefinition>	theSortList		= null;

	/**
	 * Are we sorting on a reference column 
	 */
	private boolean					sortOnReference	= false;

	/**
	 * The array list for the columns
	 */
	private ColumnDefinition[]      theColumns		= null;
	
	/**
	 * The prepared statement for the insert/update
	 */
	private PreparedStatement      	theStatement	= null;
	
	/**
	 * The result set for the load
	 */
	private ResultSet           	theResults      = null;
	
	/**
	 * Obtain the table name 
	 */
	protected String getTableName() { return theTableName; }

	/**
	 * Is the table indexed 
	 */
	protected boolean isIndexed() 	{ return theSortList.size() > 0; }

	/**
	 * Column Definitions array 
	 */
	protected ColumnDefinition[] getColumns() 	{ return theColumns; }

	/**
	 * Constructor
	 * @param pName the table name
	 */
	protected TableDefinition(String pName) {
		/* Record the name */
		theTableName = pName;
		
		/* Create the column list */
		theList = new ArrayList<ColumnDefinition>();

		/* Create the sort list */
		theSortList = new ArrayList<ColumnDefinition>();

		/* Create the initial column list */
		theColumns  = new ColumnDefinition[COLUMN_BLOCKSIZE];
		
		/* Add an Id column */
		theList.add(new IdColumn());
	}
	
	/**
	 * Add a reference column
	 * @param pId the column id
	 * @param pName the column name
	 * @param pRef the reference table
	 * @return the reference column
	 */
	public ReferenceColumn addReferenceColumn(int pId, String pName, String pRef) {
		/* Create the new reference column */
		ReferenceColumn myColumn = new ReferenceColumn(pId, pName, pRef);
		
		/* Add it to the list and return it */
		theList.add(myColumn);
		return myColumn;
	}
	public ReferenceColumn addNullReferenceColumn(int pId, String pName, String pRef) {
		ReferenceColumn myColumn = addReferenceColumn(pId, pName, pRef);
		myColumn.setNullable();
		return myColumn;
	}
	
	/**
	 * Add an integer column
	 * @param pId the column id
	 * @param pName the column name
	 * @return the integer column
	 */
	public IntegerColumn addIntegerColumn(int pId, String pName) {
		/* Create the new integer column */
		IntegerColumn myColumn = new IntegerColumn(pId, pName);
		
		/* Add it to the list and return it */
		theList.add(myColumn);
		return myColumn;
	}
	public IntegerColumn addNullIntegerColumn(int pId, String pName) {
		IntegerColumn myColumn = addIntegerColumn(pId, pName);
		myColumn.setNullable();
		return myColumn;
	}
	
	/**
	 * Add a long column
	 * @param pId the column id
	 * @param pName the column name
	 * @return the long column
	 */
	public LongColumn addLongColumn(int pId, String pName) {
		/* Create the new long column */
		LongColumn myColumn = new LongColumn(pId, pName);
		
		/* Add it to the list and return it */
		theList.add(myColumn);
		return myColumn;
	}
	public LongColumn addNullLongColumn(int pId, String pName) {
		LongColumn myColumn = addLongColumn(pId, pName);
		myColumn.setNullable();
		return myColumn;
	}
	
	/**
	 * Add a boolean column
	 * @param pId the column id
	 * @param pName the column name
	 * @return the boolean column
	 */
	public BooleanColumn addBooleanColumn(int pId, String pName) {
		/* Create the new boolean column */
		BooleanColumn myColumn = new BooleanColumn(pId, pName);
		
		/* Add it to the list and return it */
		theList.add(myColumn);
		return myColumn;
	}
	public BooleanColumn addNullBooleanColumn(int pId, String pName) {
		BooleanColumn myColumn = addBooleanColumn(pId, pName);
		myColumn.setNullable();
		return myColumn;
	}
	
	/**
	 * Add a date column
	 * @param pId the column id
	 * @param pName the column name
	 * @return the date column
	 */
	public DateColumn addDateColumn(int pId, String pName) {
		/* Create the new long column */
		DateColumn myColumn = new DateColumn(pId, pName);
		
		/* Add it to the list and return it */
		theList.add(myColumn);
		return myColumn;
	}
	public DateColumn addNullDateColumn(int pId, String pName) {
		DateColumn myColumn = addDateColumn(pId, pName);
		myColumn.setNullable();
		return myColumn;
	}
	
	/**
	 * Add a money column
	 * @param pId the column id
	 * @param pName the column name
	 * @return the money column
	 */
	public MoneyColumn addMoneyColumn(int pId, String pName) {
		/* Create the new money column */
		MoneyColumn myColumn = new MoneyColumn(pId, pName);
		
		/* Add it to the list and return it */
		theList.add(myColumn);
		return myColumn;
	}
	public MoneyColumn addNullMoneyColumn(int pId, String pName) {
		MoneyColumn myColumn = addMoneyColumn(pId, pName);
		myColumn.setNullable();
		return myColumn;
	}
	
	/**
	 * Add a rate column
	 * @param pId the column id
	 * @param pName the column name
	 * @return the rate column
	 */
	public RateColumn addRateColumn(int pId, String pName) {
		/* Create the new rate column */
		RateColumn myColumn = new RateColumn(pId, pName);
		
		/* Add it to the list and return it */
		theList.add(myColumn);
		return myColumn;
	}
	public RateColumn addNullRateColumn(int pId, String pName) {
		RateColumn myColumn = addRateColumn(pId, pName);
		myColumn.setNullable();
		return myColumn;
	}
	
	/**
	 * Add a binary column
	 * @param pId the column id
	 * @param pName the column name
	 * @param pLength the underlying (character) length 
	 * @return the binary column
	 */
	public BinaryColumn addBinaryColumn(int pId, String pName, int pLength) {
		/* Create the new binary column */
		BinaryColumn myColumn = new BinaryColumn(pId, pName, pLength);
		
		/* Add it to the list and return it */
		theList.add(myColumn);
		return myColumn;
	}
	public BinaryColumn addNullBinaryColumn(int pId, String pName, int pLength) {
		BinaryColumn myColumn = addBinaryColumn(pId, pName, pLength);
		myColumn.setNullable();
		return myColumn;
	}
	
	/**
	 * Add an encrypted column
	 * @param pId the column id
	 * @param pName the column name
	 * @param pLength the underlying (character) length 
	 * @return the binary column
	 */
	public BinaryColumn addEncryptedColumn(int pId, String pName, int pLength) {
		/* Create the new binary column */
		BinaryColumn myColumn = new BinaryColumn(pId, pName, 
												 SymmetricKey.IVSIZE 
												 	+ CipherSet.KEYIDLEN 
												 	+ SymmetricKey.getEncryptionLength(2*pLength));
		
		/* Add it to the list and return it */
		theList.add(myColumn);
		return myColumn;
	}
	public BinaryColumn addNullEncryptedColumn(int pId, String pName, int pLength) {
		BinaryColumn myColumn = addEncryptedColumn(pId, pName, pLength);
		myColumn.setNullable();
		return myColumn;
	}
	
	/**
	 * Add a string column
	 * @param pId the column id
	 * @param pName the column name
	 * @param pLength the character length 
	 * @return the binary column
	 */
	public StringColumn addStringColumn(int pId, String pName, int pLength) {
		/* Create the new string column */
		StringColumn myColumn = new StringColumn(pId, pName, pLength);
		
		/* Add it to the list and return it */
		theList.add(myColumn);
		return myColumn;
	}
	public StringColumn addNullStringColumn(int pId, String pName, int pLength) {
		StringColumn myColumn = addStringColumn(pId, pName, pLength);
		myColumn.setNullable();
		return myColumn;
	}

	/**
	 * Locate reference
	 * @param pTables the list of defined tables
	 */
	protected void resolveReferences(List<DatabaseTable<?>> pTables) {
		Iterator<ColumnDefinition>	myIterator;
		ColumnDefinition			myDef;

		/* Create the iterator */
		myIterator = theList.iterator();
		
		/* Loop through the columns */
		while (myIterator.hasNext()) {
			myDef = myIterator.next();
			myDef.locateReference(pTables);
		}
	}
	
	/**
	 * Load results
	 * @param pResults the result set
	 */
	protected void loadResults(ResultSet pResults) throws SQLException {
		Iterator<ColumnDefinition>	myIterator;
		ColumnDefinition			myDef;
		int							myIndex	= 1;

		/* Store the result set and clear values */
		theResults = pResults;
		clearValues();
		
		/* Create the iterator */
		myIterator = theList.iterator();
		
		/* Loop through the columns */
		while (myIterator.hasNext()) {
			myDef = myIterator.next();
			myDef.loadValue(myIndex++);
		}
	}
	
	/**
	 * Insert values
	 * @param pStmt the statement
	 */
	protected void insertValues(PreparedStatement pStmt) throws SQLException, Exception {
		Iterator<ColumnDefinition>	myIterator;
		ColumnDefinition			myDef;
		int							myIndex	= 1;

		/* Store the Statement */
		theStatement = pStmt;
		
		/* Create the iterator */
		myIterator = theList.iterator();
		
		/* Loop through the columns */
		while (myIterator.hasNext()) {
			myDef = myIterator.next();

			/* Reject if the value is not set */
			if (!myDef.isValueSet())
				throw new Exception(ExceptionClass.LOGIC,
									"Column " + myDef.getColumnName() + 
									" in table " + theTableName +
									" has no value for insert");
			
			myDef.storeValue(myIndex++);
		}
	}
	
	/**
	 * Update values
	 * @param pStmt the statement
	 */
	protected void updateValues(PreparedStatement pStmt) throws SQLException, Exception {
		Iterator<ColumnDefinition>	myIterator;
		ColumnDefinition			myDef;
		ColumnDefinition			myId	= null;
		int							myIndex	= 1;

		/* Store the Statement */
		theStatement = pStmt;
		
		/* Create the iterator */
		myIterator = theList.iterator();
		
		/* Loop through the columns */
		while (myIterator.hasNext()) {
			myDef = myIterator.next();

			/* If this is the Id record */
			if (myDef instanceof IdColumn) {
				/* Remember the column */
				myId = myDef;
			}
			
			/* Store value if it has been set */
			else if (myDef.isValueSet())
				myDef.storeValue(myIndex++);
		}
		
		/* Store the Id */
		myId.storeValue(myIndex);
	}
	
	/**
	 * Clear values for table
	 */
	protected void clearValues() {
		/* Loop over the non-null Column Definitions */
		for (ColumnDefinition myDef: theColumns) {
			/* Skip null columns */
			if (myDef == null) continue;
			
			/* Clear value */
			myDef.clearValue();
		}
	}
	
	/**
	 * Get Integer value for column
	 * @param pId the column id
	 */
	public Integer getIntegerValue(int pId) throws Exception {
		/* Obtain the correct id */
		ColumnDefinition myCol = getColumnForId(pId);
		
		/* Reject if this is not an integer column */
		if (!(myCol instanceof IntegerColumn))
			throw new Exception(ExceptionClass.LOGIC,
								"Column " + myCol.getColumnName() + 
								" in table " + theTableName +
								" is not Integer type");
		
		/* Return the value */
		IntegerColumn myIntCol = (IntegerColumn)myCol;
		return myIntCol.getValue();
	}
	
	/**
	 * Get Long value for column
	 * @param pId the column id
	 */
	public Long getLongValue(int pId) throws Exception {
		/* Obtain the correct id */
		ColumnDefinition myCol = getColumnForId(pId);
		
		/* Reject if this is not a long column */
		if (!(myCol instanceof IntegerColumn))
			throw new Exception(ExceptionClass.LOGIC,
								"Column " + myCol.getColumnName() + 
								" in table " + theTableName +
								" is not Long type");
		
		/* Return the value */
		LongColumn myLongCol = (LongColumn)myCol;
		return myLongCol.getValue();
	}
	
	/**
	 * Get Date value for column
	 * @param pId the column id
	 */
	public java.util.Date getDateValue(int pId) throws Exception {
		/* Obtain the correct id */
		ColumnDefinition myCol = getColumnForId(pId);
		
		/* Reject if this is not a date column */
		if (!(myCol instanceof DateColumn))
			throw new Exception(ExceptionClass.LOGIC,
								"Column " + myCol.getColumnName() + 
								" in table " + theTableName +
								" is not Date type");
		
		/* Return the value */
		DateColumn myDateCol = (DateColumn)myCol;
		return myDateCol.getValue();
	}
	
	/**
	 * Get Boolean value for column
	 * @param pId the column id
	 */
	public Boolean getBooleanValue(int pId) throws Exception {
		/* Obtain the correct id */
		ColumnDefinition myCol = getColumnForId(pId);
		
		/* Reject if this is not a boolean column */
		if (!(myCol instanceof BooleanColumn))
			throw new Exception(ExceptionClass.LOGIC,
								"Column " + myCol.getColumnName() + 
								" in table " + theTableName +
								" is not Boolean type");
		
		/* Return the value */
		BooleanColumn myBoolCol = (BooleanColumn)myCol;
		return myBoolCol.getValue();
	}
	
	/**
	 * Get String value for column
	 * @param pId the column id
	 */
	public String getStringValue(int pId) throws Exception {
		/* Obtain the correct id */
		ColumnDefinition myCol = getColumnForId(pId);
		
		/* Reject if this is not a string column */
		if (!(myCol instanceof StringColumn))
			throw new Exception(ExceptionClass.LOGIC,
								"Column " + myCol.getColumnName() + 
								" in table " + theTableName +
								" is not String type");
		
		/* Return the value */
		StringColumn myStringCol = (StringColumn)myCol;
		return myStringCol.getValue();
	}
	
	/**
	 * Get Binary value for column
	 * @param pId the column id
	 */
	public byte[] getBinaryValue(int pId) throws Exception {
		/* Obtain the correct id */
		ColumnDefinition myCol = getColumnForId(pId);
		
		/* Reject if this is not a string column */
		if (!(myCol instanceof BinaryColumn))
			throw new Exception(ExceptionClass.LOGIC,
								"Column " + myCol.getColumnName() + 
								" in table " + theTableName +
								" is not Binary type");
		
		/* Return the value */
		BinaryColumn myBinaryCol = (BinaryColumn)myCol;
		return myBinaryCol.getValue();
	}
	
	/**
	 * Set Integer value for column
	 * @param pId the column id
	 * @param pValue the value
	 */
	public void setIntegerValue(int pId, Integer pValue) throws Exception {
		/* Obtain the correct id */
		ColumnDefinition myCol = getColumnForId(pId);
		
		/* Reject if this is not an integer column */
		if (!(myCol instanceof IntegerColumn))
			throw new Exception(ExceptionClass.LOGIC,
								"Column " + myCol.getColumnName() + 
								" in table " + theTableName +
								" is not Integer type");
		
		/* Set the value */
		IntegerColumn myIntCol = (IntegerColumn)myCol;
		myIntCol.setValue(pValue);
	}
	
	/**
	 * Set Long value for column
	 * @param pId the column id
	 * @param pValue the value
	 */
	public void setLongValue(int pId, Long pValue) throws Exception {
		/* Obtain the correct id */
		ColumnDefinition myCol = getColumnForId(pId);
		
		/* Reject if this is not a long column */
		if (!(myCol instanceof LongColumn))
			throw new Exception(ExceptionClass.LOGIC,
								"Column " + myCol.getColumnName() + 
								" in table " + theTableName +
								" is not Long type");
		
		/* Set the value */
		LongColumn myLongCol = (LongColumn)myCol;
		myLongCol.setValue(pValue);
	}
	
	/**
	 * Set Boolean value for column
	 * @param pId the column id
	 * @param pValue the value
	 */
	public void setBooleanValue(int pId, Boolean pValue) throws Exception {
		/* Obtain the correct id */
		ColumnDefinition myCol = getColumnForId(pId);
		
		/* Reject if this is not a boolean column */
		if (!(myCol instanceof BooleanColumn))
			throw new Exception(ExceptionClass.LOGIC,
								"Column " + myCol.getColumnName() + 
								" in table " + theTableName +
								" is not Boolean type");
		
		/* Set the value */
		BooleanColumn myBoolCol = (BooleanColumn)myCol;
		myBoolCol.setValue(pValue);
	}
	
	/**
	 * Set Date value for column
	 * @param pId the column id
	 * @param pValue the value
	 */
	public void setDateValue(int pId, Date pValue) throws Exception {
		/* Obtain the correct id */
		ColumnDefinition myCol = getColumnForId(pId);
		
		/* Reject if this is not a Date column */
		if (!(myCol instanceof DateColumn))
			throw new Exception(ExceptionClass.LOGIC,
								"Column " + myCol.getColumnName() + 
								" in table " + theTableName +
								" is not Date type");
		
		/* Set the value */
		DateColumn myDateCol = (DateColumn)myCol;
		myDateCol.setValue(pValue);
	}
	
	/**
	 * Set String value for column
	 * @param pId the column id
	 * @param pValue the value
	 */
	public void setStringValue(int pId, String pValue) throws Exception {
		/* Obtain the correct id */
		ColumnDefinition myCol = getColumnForId(pId);
		
		/* Reject if this is not a string column */
		if (!(myCol instanceof StringColumn))
			throw new Exception(ExceptionClass.LOGIC,
								"Column " + myCol.getColumnName() + 
								" in table " + theTableName +
								" is not String type");
		
		/* Set the value */
		StringColumn myStringCol = (StringColumn)myCol;
		myStringCol.setValue(pValue);
	}
	
	/**
	 * Set Binary value for column
	 * @param pId the column id
	 * @param pValue the value
	 */
	public void setBinaryValue(int pId, byte[] pValue) throws Exception {
		/* Obtain the correct id */
		ColumnDefinition myCol = getColumnForId(pId);
		
		/* Reject if this is not a binary column */
		if (!(myCol instanceof BinaryColumn))
			throw new Exception(ExceptionClass.LOGIC,
								"Column " + myCol.getColumnName() + 
								" in table " + theTableName +
								" is not Binary type");
		
		/* Set the value */
		BinaryColumn myBinaryCol = (BinaryColumn)myCol;
		myBinaryCol.setValue(pValue);
	}
	
	/**
	 * Set Money value for column
	 * @param pId the column id
	 * @param pValue the value
	 */
	public void setMoneyValue(int pId, Money pValue) throws Exception {
		/* Obtain the correct id */
		ColumnDefinition myCol = getColumnForId(pId);
		
		/* Reject if this is not a money column */
		if (!(myCol instanceof MoneyColumn))
			throw new Exception(ExceptionClass.LOGIC,
								"Column " + myCol.getColumnName() + 
								" in table " + theTableName +
								" is not Money type");
		
		/* Set the value */
		MoneyColumn myMoneyCol = (MoneyColumn)myCol;
		myMoneyCol.setValue(pValue);
	}
	
	/**
	 * Set Rate value for column
	 * @param pId the column id
	 * @param pValue the value
	 */
	public void setRateValue(int pId, Rate pValue) throws Exception {
		/* Obtain the correct id */
		ColumnDefinition myCol = getColumnForId(pId);
		
		/* Reject if this is not a rate column */
		if (!(myCol instanceof RateColumn))
			throw new Exception(ExceptionClass.LOGIC,
								"Column " + myCol.getColumnName() + 
								" in table " + theTableName +
								" is not Rate type");
		
		/* Set the value */
		RateColumn myRateCol = (RateColumn)myCol;
		myRateCol.setValue(pValue);
	}
	
	/**
	 * Locate column for id
	 * @param pId the id of the column
	 * @return the column   
	 */
	private ColumnDefinition getColumnForId(int pId) throws Exception {
		ColumnDefinition myDef = null;

		/* If the column is in range of the array, extract its definition */
		if (pId < theColumns.length) myDef = theColumns[pId];
		
		/* Check that the id is in range and present */
		if (myDef == null)
			throw new Exception(ExceptionClass.LOGIC,
								"Invalid Column Id: " + pId + " for " + theTableName);
		
		/* Return the column definition */
		return myDef;
	}
	
	/**
	 * Build the create table string for the table 
	 * @return the SQL string 
	 */
	protected String getCreateTableString() {
		StringBuilder 				myBuilder 	= new StringBuilder(1000);
		Iterator<ColumnDefinition>	myIterator;
		ColumnDefinition			myDef;
		boolean						myFirst 	= true;
		
		/* Build the initial create */
		myBuilder.append("create table ");
		myBuilder.append(theTableName);
		myBuilder.append(" (");
		
		/* Create the iterator */
		myIterator = theList.iterator();
		
		/* Loop through the columns */
		while (myIterator.hasNext()) {
			myDef = myIterator.next();
			if (!myFirst) myBuilder.append(", ");
			myDef.buildCreateString(myBuilder);
			myFirst = false;
		}
		
		/* Close the statement and return it */
		myBuilder.append(')');
		return myBuilder.toString();
	}
	
	/**
	 * Build the create index string for the table 
	 * @return the SQL string 
	 */
	protected String getCreateIndexString() {
		StringBuilder 				myBuilder 	= new StringBuilder(1000);
		Iterator<ColumnDefinition>	myIterator;
		ColumnDefinition			myDef;
		boolean						myFirst 	= true;
		
		/* Return null if we are not indexed */
		if (!isIndexed()) return null;
		
		/* Build the initial create */
		myBuilder.append("create index idx_");
		myBuilder.append(theTableName);
		myBuilder.append(" on ");
		myBuilder.append(theTableName);
		myBuilder.append(" (");
		
		/* Create the iterator */
		myIterator = theSortList.iterator();
		
		/* Loop through the columns */
		while (myIterator.hasNext()) {
			myDef = myIterator.next();
			if (!myFirst) myBuilder.append(", ");
			myBuilder.append(myDef.getColumnName());
			if (myDef.getSortOrder() == SortOrder.DESCENDING)
				myBuilder.append(" DESC");
			myFirst = false;
		}
		
		/* Close the statement and return it */
		myBuilder.append(')');
		return myBuilder.toString();
	}
	
	/**
	 * Build the drop table string for the table 
	 * @return the SQL string 
	 */
	protected String getDropTableString() {
		StringBuilder 				myBuilder 	= new StringBuilder(1000);
		
		/* Build the initial create */
		myBuilder.append("if exists (select * from sys.tables where name = '");
		myBuilder.append(theTableName);
		myBuilder.append("') drop table ");
		myBuilder.append(theTableName);
		return myBuilder.toString();
	}
	
	/**
	 * Build the drop index string for the table 
	 * @return the SQL string 
	 */
	protected String getDropIndexString() {
		StringBuilder 				myBuilder 	= new StringBuilder(1000);
		
		/* Return null if we are not indexed */
		if (!isIndexed()) return null;
		
		/* Build the initial create */
		myBuilder.append("if exists (select * from sys.indexes where name = 'idx_");
		myBuilder.append(theTableName);
		myBuilder.append("') drop index idx_");
		myBuilder.append(theTableName);
		myBuilder.append(" on ");
		myBuilder.append(theTableName);
		return myBuilder.toString();
	}
	
	/**
	 * Build the load string for a list of columns 
	 * @return the SQL string 
	 */
	protected String getLoadString() {
		StringBuilder 				myBuilder 	= new StringBuilder(1000);
		Iterator<ColumnDefinition>	myIterator;
		ColumnDefinition			myDef;
		boolean						myFirst 	= true;
		
		/* Build the initial insert */
		myBuilder.append("select ");
		
		/* Create the iterator */
		myIterator = theList.iterator();
		
		/* Loop through the columns */
		while (myIterator.hasNext()) {
			myDef = myIterator.next();
			if (!myFirst) myBuilder.append(", ");
			if (sortOnReference) myBuilder.append("a.");
			myBuilder.append(myDef.getColumnName());
			myFirst = false;
		}
		
		/* Close the statement */
		myBuilder.append(" from ");
		myBuilder.append(theTableName);
		if (sortOnReference) myBuilder.append(" a");

		/* If we are indexed */
		if (isIndexed()) {
			/* Add Joins */
			if (sortOnReference) 
				myBuilder.append(getJoinString('a', 1));
			
			/* Add the order clause */
			myBuilder.append(" order by ");

			/* Build the order string */
			myBuilder.append(getOrderString('a', 1)); 
		}
		
		return myBuilder.toString();
	}
	
	/**
	 * Build the Join string for the list of columns
	 * @param pChar the character for this table
	 * @param iOffset the join offset 
	 * @return the SQL string 
	 */
	protected String getJoinString(char 	pChar,
								   Integer	pOffset) {
		StringBuilder 				myBuilder 	= new StringBuilder(1000);
		Iterator<ColumnDefinition>	myIterator;
		ColumnDefinition			myDef;
		
		/* Create the iterator */
		myIterator 	= theSortList.iterator();

		/* Loop through the columns */
		while (myIterator.hasNext()) {
			/* Access next column and skip if not reference column */
			myDef = myIterator.next();
			if (!(myDef instanceof ReferenceColumn)) continue;
			
			/* Add the join */
			ReferenceColumn myCol = (ReferenceColumn)myDef;
			myCol.buildJoinString(myBuilder, pChar, pOffset);
		}
		
		return myBuilder.toString();
	}
	
	/**
	 * Build the Order string for the list of columns
	 * @param pChar the character for this table
	 * @param iOffset the join offset 
	 * @return the SQL string 
	 */
	protected String getOrderString(char 	pChar,
								    Integer	pOffset) {
		StringBuilder 				myBuilder 	= new StringBuilder(1000);
		Iterator<ColumnDefinition>	myIterator;
		ColumnDefinition			myDef;
		boolean						myFirst;
		
		/* Create the iterator */
		myIterator 	= theSortList.iterator();
		myFirst		= true;

		/* Loop through the columns */
		while (myIterator.hasNext()) {
			myDef = myIterator.next();
			/* Handle secondary columns */
			if (!myFirst) myBuilder.append(", ");
			
			/* If we are using prefixes */
			if ((sortOnReference) || (pChar > 'a')) {
				/* If this is a reference column */
				if (myDef instanceof ReferenceColumn) {
					/* Handle Reference column */
					ReferenceColumn myCol = (ReferenceColumn)myDef;
					myCol.buildOrderString(myBuilder, pOffset);
				}
				else {
					/* Handle standard column with prefix */
					myBuilder.append(pChar); 
					myBuilder.append("."); 
					myBuilder.append(myDef.getColumnName());
					if (myDef.getSortOrder() == SortOrder.DESCENDING)
						myBuilder.append(" DESC");
				}
			}
			else { 
				/* Handle standard column */
				myBuilder.append(myDef.getColumnName());
				if (myDef.getSortOrder() == SortOrder.DESCENDING)
					myBuilder.append(" DESC");
			}
			
			/* Note secondary columns */
			myFirst = false;
		}

		return myBuilder.toString();
	}
	
	/**
	 * Build the insert string for a list of columns
	 * @return the SQL string 
	 */
	protected String getInsertString() {
		StringBuilder 				myBuilder 	= new StringBuilder(1000);
		StringBuilder 				myValues 	= new StringBuilder(100);
		Iterator<ColumnDefinition>	myIterator;
		ColumnDefinition			myDef;
		boolean						myFirst 	= true;
		
		/* Build the initial insert */
		myBuilder.append("insert into ");
		myBuilder.append(theTableName);
		myBuilder.append(" (");
		
		/* Create the iterator */
		myIterator = theList.iterator();
		
		/* Loop through the columns */
		while (myIterator.hasNext()) {
			myDef = myIterator.next();
			if (!myFirst) myBuilder.append(", ");
			if (!myFirst) myValues.append(", ");
			myBuilder.append(myDef.getColumnName());
			myValues.append('?');
			myFirst = false;
		}
		
		/* Close the statement and return it */
		myBuilder.append(") values(");
		myBuilder.append(myValues);
		myBuilder.append(')');
		return myBuilder.toString();
	}
	
	/**
	 * Build the update string for a list of columns
	 * @return the SQL string 
	 */
	protected String getUpdateString() throws Exception {
		StringBuilder 				myBuilder 	= new StringBuilder(1000);
		Iterator<ColumnDefinition>	myIterator;
		ColumnDefinition			myDef;
		ColumnDefinition			myId		= null;
		boolean						myFirst 	= true;
		
		/* Build the initial update */
		myBuilder.append("update ");
		myBuilder.append(theTableName);
		myBuilder.append(" set ");
		
		/* Create the iterator */
		myIterator = theList.iterator();
		
		/* Loop through the columns */
		while (myIterator.hasNext()) {
			myDef = myIterator.next();

			/* If this is the Id record */
			if (myDef instanceof IdColumn) {
				/* Reject if the value is not set */
				if (!myDef.isValueSet())
					throw new Exception(ExceptionClass.LOGIC,
										"Column " + myDef.getColumnName() + 
										" in table " + theTableName +
										" has no value for update");
				
				/* Remember the column */
				myId = myDef;
			}
			
			/* If this column is to be updated */
			else if (myDef.isValueSet()) {
				/* Add the update of this column */
				if (!myFirst) myBuilder.append(", ");
				myBuilder.append(myDef.getColumnName());
				myBuilder.append("=?");
				myFirst = false;
			}
		}

		/* If we have no values then just return null */
		if (myFirst) return null;
		
		/* Close the statement and return it */
		myBuilder.append(" where ");
		myBuilder.append(myId.getColumnName());
		myBuilder.append("=?");
		return myBuilder.toString();
	}
	
	/**
	 * Build the delete string for a table
	 * @return the SQL string 
	 */
	protected String getDeleteString() {
		StringBuilder 				myBuilder 	= new StringBuilder(1000);
		ColumnDefinition			myId		= null;
		
		/* Build the initial delete */
		myBuilder.append("delete from ");
		myBuilder.append(theTableName);
		myBuilder.append(" where ");
		
		/* Access the id definition */
		myId = theList.get(0);
		
		/* Build the rest of the command */
		myBuilder.append(myId.getColumnName());
		myBuilder.append("=?");
		return myBuilder.toString();
	}
	
	/**
	 * Build the purge string for a table
	 * @return the SQL string 
	 */
	protected String getPurgeString() {
		StringBuilder 				myBuilder 	= new StringBuilder(1000);
		
		/* Build the initial delete */
		myBuilder.append("delete from ");
		myBuilder.append(theTableName);
		return myBuilder.toString();
	}
	
	/**
	 * Build the count string for a table
	 * @return the SQL string 
	 */
	protected String getCountString() {
		StringBuilder 				myBuilder 	= new StringBuilder(1000);
		
		/* Build the initial delete */
		myBuilder.append("select count(*) from ");
		myBuilder.append(theTableName);
		return myBuilder.toString();
	}

	/**
	 * Trim the column definitions 
	 */
	protected void trimColumns() {
		int	iMaxColumn = 0;
		
		/* Loop through the columns to find the maximum active column */
		for (int i=0; i<theColumns.length; i++) {
			/* Record maximum active column */
			if (theColumns[i] != null) iMaxColumn = i;
		}
		
		/* If we need to adjust the array length */
		if (iMaxColumn < theColumns.length-1) {
			/* Trim the column array */
			theColumns = java.util.Arrays.copyOf(theColumns,
												 iMaxColumn+1,
												 ColumnDefinition[].class);
		}
	}
	
	/**
	 * The underlying column definition class
	 */
	public abstract class ColumnDefinition {
		/**
		 * Column Name
		 */
		protected String		theName		= null;
	
		/**
		 * Column Identity
		 */
		protected int			theIdentity	= -1;
	
		/**
		 * Is the column null-able
		 */
		protected boolean 		isNullable	= false;

		/**
		 * Is the value set
		 */
		private boolean 		isValueSet	= false;

		/**
		 * The value of the column
		 */
		protected Object		theValue	= null;
		
		/**
		 * The sort order of the column
		 */
		protected SortOrder		theOrder	= null;
		
		/**
		 * Obtain the column name 
		 */
		protected String 		getColumnName() 	{ return theName; }
		
		/**
		 * Obtain the column id 
		 */
		protected int 			getColumnId() 		{ return theIdentity; }
		
		/**
		 * Obtain the sort order 
		 */
		protected SortOrder		getSortOrder() 		{ return theOrder; }
		
		/**
		 * Clear value 
		 */
		private void 			clearValue() 		{ theValue = null; isValueSet = false; }
		
		/**
		 * Set value 
		 */
		private void 			setValue(Object pValue) 	{ theValue = pValue; isValueSet = true; }
		
		/**
		 * Is the value set 
		 */
		private boolean			isValueSet() 		{ return isValueSet; }
		
		/**
		 * Constructor
		 * @param pId the column id
		 * @param pName the column name
		 */
		private ColumnDefinition(int pId, String pName) {
			/* Record the identity and name */
			theIdentity	= pId;
			theName		= pName;
			
			/* While the column list size is too small */
			while (theIdentity > theColumns.length-1) {
				/* Extend the column array */
				theColumns = java.util.Arrays.copyOf(theColumns,
													 theColumns.length+COLUMN_BLOCKSIZE,
													 ColumnDefinition[].class);
			}
			
			/* Store this column into the list */
			theColumns[theIdentity] = this;
		}
		
		/**
		 * Build the creation string for this column 
		 * @param pBuilder the String builder
		 */
		protected void buildCreateString(StringBuilder pBuilder) {
			/* Add the name of the column */
			pBuilder.append(theName);
			pBuilder.append(' ');
			
			/* Add the type of the column */
			buildColumnType(pBuilder);
			
			/* Add null-able indication */
			if (!isNullable) pBuilder.append(" not");
			pBuilder.append(" null");
			
			/* build the key reference */
			buildKeyReference(pBuilder);
		}

		/** 
		 * Set null-able column 
		 */
		protected void setNullable() { isNullable = true; }
		
		/** 
		 * Set sortOrder 
		 * @param pOrder the Sort direction
		 */
		public void setSortOrder(SortOrder pOrder) {
			theOrder = pOrder;
			theSortList.add(this);
		}
		
		/**
		 * Build the column type for this column 
		 * @param pBuilder the String builder
		 */
		protected abstract void buildColumnType(StringBuilder pBuilder);

		/**
		 * Load the value for this column 
		 * @param pIndex the index of the result column
		 */
		protected abstract void loadValue(int pIndex) throws SQLException;

		/**
		 * Store the value for this column 
		 * @param pIndex the index of the statement
		 */
		protected abstract void storeValue(int pIndex) throws SQLException;

		/**
		 * Define the key reference 
		 * @param pBuilder the String builder
		 */
		protected void buildKeyReference(StringBuilder pBuilder) {}

		/**
		 * Locate reference
		 * @param pTables the list of defined tables
		 */
		protected void locateReference(List<DatabaseTable<?>> pTables) {}
	}
	
	/**
	 * The integerColumn Class
	 */
	protected class IntegerColumn extends ColumnDefinition {
		/**
		 * Constructor
		 * @param pId the column id
		 * @param pName the name of the column
		 */
		private IntegerColumn(int pId, String pName) {
			/* Record the column type and name */
			super(pId, pName);
		}
		
		/**
		 * Build the column type 
		 * @param pBuilder the String builder
		 */
		protected void buildColumnType(StringBuilder pBuilder) {
			/* Add the column type */
			pBuilder.append("int");
		}
		
		/**
		 * Set the value
		 * @param pValue the value
		 */
		private void setValue(Integer pValue) {
			super.setValue(pValue);
		}
		
		/**
		 * Get the value
		 * @return the value
		 */
		private Integer getValue() {
			return (Integer)theValue;
		}
		
		/** 
		 *  Load data from column
		 *  @param the index to load from
		 */
		protected void loadValue(int pIndex) throws SQLException {
			int myValue = theResults.getInt(pIndex);
			if ((myValue == 0) && (theResults.wasNull()))
				setValue(null);
			else
				setValue(myValue);
		}
		
		/** 
		 *  Store data to column
		 *  @param the index to store to
		 */
		protected void storeValue(int pIndex) throws SQLException {
			Integer myValue = getValue();
			if (myValue == null) theStatement.setNull(pIndex, Types.INTEGER);
			else theStatement.setInt(pIndex, myValue);
		}
	}
	
	/**
	 * The idColumn Class
	 */
	protected class IdColumn extends IntegerColumn {
		/**
		 * Constructor
		 */
		private IdColumn() {
			/* Record the column type */
			super(0, DataItem.NAME_ID);
		}
		
		/**
		 * Build the key reference 
		 * @param pBuilder the String builder
		 */
		protected void buildKeyReference(StringBuilder pBuilder) {
			/* Add the column type */
			pBuilder.append(" primary key");
		}
	}
	
	/**
	 * The referenceColumn Class
	 */
	protected class ReferenceColumn extends IntegerColumn {
		/**
		 * The name of the referenced table 
		 */
		private String theReference	= null;
		
		/**
		 * The definition of the referenced table 
		 */
		private TableDefinition theDefinition	= null;
		
		/**
		 * Constructor
		 * @param pId the column id
		 * @param pName the name of the column
		 * @param pTable the name of the referenced table
		 */
		private ReferenceColumn(int pId, String pName, String pTable) {
			/* Record the column type */
			super(pId, pName);
			theReference 	= pTable;
		}
		
		/** 
		 * Set sortOrder 
		 * @param pOrder the Sort direction
		 */
		public void setSortOrder(SortOrder pOrder) {
			super.setSortOrder(pOrder);
			sortOnReference = true;
		}
		
		/**
		 * Build the key reference 
		 * @param pBuilder the String builder
		 */
		protected void buildKeyReference(StringBuilder pBuilder) {
			/* Add the reference */
			pBuilder.append(" references ");
			pBuilder.append(theReference);
			pBuilder.append('(');
			pBuilder.append(DataItem.NAME_ID);
			pBuilder.append(')');
		}
		
		/**
		 * Locate reference
		 * @param pTables the list of defined tables
		 */
		protected void locateReference(List<DatabaseTable<?>> pTables) {
			/* Access the Iterator */
			ListIterator<DatabaseTable<?>> myIterator;
			myIterator = pTables.listIterator();
			
			/* Loop through the Tables */
			while (myIterator.hasNext()) {
				/* Access Table */
				DatabaseTable<?> myTable = myIterator.next();
				
				/* If this is the referenced table */
				if (theReference.compareTo(myTable.getTableName()) == 0) {
					/* Store the reference and break the loop */
					theDefinition = myTable.getDefinition();
					break;
				}
			}
		}
		
		/** 
		 * build Join String
		 * @param pBuilder the String Builder
		 * @param pChar the character for this table
		 * @param iOffset the join offset 
		 * @return the SQL string 
		 */
		private void buildJoinString(StringBuilder 	pBuilder,
									 char			pChar,
									 Integer 		pOffset) {
			/* Calculate join character */
			char myChar = (char)('a' + pOffset);
			
			/* Build Initial part of string */
			pBuilder.append(" join ");
			pBuilder.append(theReference);
			pBuilder.append(" ");
			pBuilder.append(myChar);
			
			/* Build the join */
			pBuilder.append(" on ");
			pBuilder.append(pChar);
			pBuilder.append(".");
			pBuilder.append(getColumnName());
			pBuilder.append(" = ");
			pBuilder.append(myChar);
			pBuilder.append(".");
			pBuilder.append(DataItem.NAME_ID);
			
			/* Increment offset */
			pOffset++;

			/* Add the join string for the underlying table */
			pBuilder.append(theDefinition.getJoinString(myChar, pOffset));
		}

		/** 
		 * build Order String
		 * @param pBuilder the String Builder
		 * @param pChar the character for this table
		 * @param iOffset the join offset 
		 * @return the SQL string 
		 */
		private void buildOrderString(StringBuilder pBuilder,
									  Integer 		pOffset) {
			Iterator<ColumnDefinition>	myIterator;
			ColumnDefinition			myDef;
			boolean						myFirst = true;
			
			/* Calculate join character */
			char myChar = (char)('a' + pOffset);
			
			/* Create the iterator */
			myIterator 	= theDefinition.theSortList.iterator();

			/* Loop through the columns */
			while (myIterator.hasNext()) {
				/* Access next column  */
				myDef = myIterator.next();
				
				/* Handle subsequent columns */
				if (!myFirst) pBuilder.append(", ");
				
				/* If this is a reference column */
				if  (myDef instanceof ReferenceColumn) {
					/* Increment offset */
					pOffset++;

					/* Determine new char */
					char myNewChar = (char) ('a' + pOffset);

					/* Add the join string for the underlying table */
					ReferenceColumn myCol = (ReferenceColumn)myDef;
					pBuilder.append(myCol.theDefinition.getOrderString(myNewChar, pOffset));
				}

				/* else standard column */
				else {
					/* Build the column name */
					pBuilder.append(myChar);
					pBuilder.append(".");
					pBuilder.append(myDef.getColumnName());
					if (myDef.getSortOrder() == SortOrder.DESCENDING)
						pBuilder.append(" DESC");
				}
				
				/* Note we have a column */
				myFirst = false;
			}
		}
	}
	
	/**
	 * The longColumn Class
	 */
	protected class LongColumn extends ColumnDefinition {
		/**
		 * Constructor
		 * @param pId the column id
		 * @param pName the name of the column
		 */
		private LongColumn(int pId, String pName) {
			/* Record the column type */
			super(pId, pName);
		}
		
		/**
		 * Build the column type 
		 * @param pBuilder the String builder
		 */
		protected void buildColumnType(StringBuilder pBuilder) {
			/* Add the column type */
			pBuilder.append("bigint");
		}
		
		/**
		 * Set the value
		 * @param pValue the value
		 */
		private void setValue(Long pValue) {
			super.setValue(pValue);
		}
		
		/**
		 * Get the value
		 * @return the value
		 */
		private Long getValue() {
			return (Long)theValue;
		}
		
		/** 
		 *  Load data from column
		 *  @param the index to load from
		 */
		protected void loadValue(int pIndex) throws SQLException {
			long myValue = theResults.getLong(pIndex);
			if ((myValue == 0) && (theResults.wasNull()))
				setValue(null);
			else
				setValue(myValue);
		}
		
		/** 
		 *  Store data to column
		 *  @param the index to store to
		 */
		protected void storeValue(int pIndex) throws SQLException {
			Long myValue = getValue();
			if (myValue == null) theStatement.setNull(pIndex, Types.BIGINT);
			else theStatement.setLong(pIndex, myValue);
		}
	}
	
	/**
	 * The dateColumn Class
	 */
	protected class DateColumn extends ColumnDefinition {
		/**
		 * Constructor
		 * @param pId the column id
		 * @param pName the name of the column
		 */
		private DateColumn(int pId, String pName) {
			/* Record the column type */
			super(pId, pName);
		}
		
		/**
		 * Build the column type 
		 * @param pBuilder the String builder
		 */
		protected void buildColumnType(StringBuilder pBuilder) {
			/* Add the column type */
			pBuilder.append("date");
		}
		
		/**
		 * Set the value
		 * @param pValue the value
		 */
		private void setValue(java.util.Date pValue) {
			super.setValue(pValue);
		}
		
		/**
		 * Set the value
		 * @param pValue the value
		 */
		private void setValue(Date pValue) {
			super.setValue((pValue == null) ? null 
										    : pValue.getDate());
		}
		
		/**
		 * Get the value
		 * @return the value
		 */
		private java.util.Date getValue() {
			return (java.util.Date)theValue;
		}
		
		/** 
		 *  Load data from column
		 *  @param the index to load from
		 */
		protected void loadValue(int pIndex) throws SQLException {
			java.util.Date myValue = theResults.getDate(pIndex);
			setValue(myValue);
		}
		
		/** 
		 *  Store data to column
		 *  @param the index to store to
		 */
		protected void storeValue(int pIndex) throws SQLException {
			java.sql.Date 	myDate	= null;
			java.util.Date 	myValue = getValue();
			
			/* Build the date as a SQL date */
			if (myValue != null) myDate = new java.sql.Date(myValue.getTime()); 
			theStatement.setDate(pIndex, myDate);
		}
	}
	
	/**
	 * The booleanColumn Class
	 */
	protected class BooleanColumn extends ColumnDefinition {
		/**
		 * Constructor
		 * @param pId the column id
		 * @param pName the name of the column
		 */
		private BooleanColumn(int pId, String pName) {
			/* Record the column type */
			super(pId, pName);
		}
		
		/**
		 * Build the column type 
		 * @param pBuilder the String builder
		 */
		protected void buildColumnType(StringBuilder pBuilder) {
			/* Add the column type */
			pBuilder.append("bit");
		}
		
		/**
		 * Set the value
		 * @param pValue the value
		 */
		private void setValue(Boolean pValue) {
			super.setValue(pValue);
		}
		
		/**
		 * Get the value
		 * @return the value
		 */
		private Boolean getValue() {
			return (Boolean)theValue;
		}
		
		/** 
		 *  Load data from column
		 *  @param the index to load from
		 */
		protected void loadValue(int pIndex) throws SQLException {
			boolean myValue = theResults.getBoolean(pIndex);
			if ((myValue == false) && (theResults.wasNull()))
				setValue(null);
			else
				setValue(myValue);
		}
		
		/** 
		 *  Store data to column
		 *  @param the index to store to
		 */
		protected void storeValue(int pIndex) throws SQLException {
			Boolean myValue = getValue();
			if (myValue == null) theStatement.setNull(pIndex, Types.BIT);
			else theStatement.setBoolean(pIndex, myValue);
		}
	}
	
	/**
	 * The stringColumn Class
	 */
	protected class StringColumn extends ColumnDefinition {
		/**
		 * The length of the column 
		 */
		private int theLength	= 0;
		
		/**
		 * Constructor
		 * @param pId the column id
		 * @param pName the name of the column
		 */
		private StringColumn(int pId, String pName, int pLength) {
			/* Record the column type */
			super(pId, pName);
			theLength	= pLength;
		}
		
		/**
		 * Build the column type 
		 * @param pBuilder the String builder
		 */
		protected void buildColumnType(StringBuilder pBuilder) {
			/* Add the column type */
			pBuilder.append("varchar(");
			pBuilder.append(theLength);
			pBuilder.append(')');
		}
		
		/**
		 * Set the value
		 * @param pValue the value
		 */
		private void setValue(String pValue) {
			super.setValue(pValue);
		}
		
		/**
		 * Get the value
		 * @return the value
		 */
		private String getValue() {
			return (String)theValue;
		}
		
		/** 
		 *  Load data from column
		 *  @param the index to load from
		 */
		protected void loadValue(int pIndex) throws SQLException {
			String myValue = theResults.getString(pIndex);
			setValue(myValue);
		}
		
		/** 
		 *  Store data to column
		 *  @param the index to store to
		 */
		protected void storeValue(int pIndex) throws SQLException {
			theStatement.setString(pIndex, getValue());
		}
	}
	
	/**
	 * The moneyColumn Class
	 */
	protected class MoneyColumn extends StringColumn {
		/**
		 * Constructor
		 * @param pId the column id
		 * @param pName the name of the column
		 */
		private MoneyColumn(int pId, String pName) {
			/* Record the column type */
			super(pId, pName, 0);
		}
		
		/**
		 * Build the column type 
		 * @param pBuilder the String builder
		 */
		protected void buildColumnType(StringBuilder pBuilder) {
			/* Add the column type */
			pBuilder.append("money");
		}

		/**
		 * Set the value
		 * @param pValue the value
		 */
		private void setValue(Money pValue) {
			String myString = null;
			if (pValue != null) myString = pValue.format(false);
			super.setValue(myString);
		}
	}
	
	/**
	 * The rateColumn Class
	 */
	protected class RateColumn extends StringColumn {
		/**
		 * Constructor
		 * @param pId the column id
		 * @param pName the name of the column
		 */
		private RateColumn(int pId, String pName) {
			/* Record the column type */
			super(pId, pName, 0);
		}
		
		/**
		 * Build the column type 
		 * @param pBuilder the String builder
		 */
		protected void buildColumnType(StringBuilder pBuilder) {
			/* Add the column type */
			pBuilder.append("decimal(4,2)");
		}

		/**
		 * Set the value
		 * @param pValue the value
		 */
		private void setValue(Rate pValue) {
			String myString = null;
			if (pValue != null) myString = pValue.format(false);
			super.setValue(myString);
		}
	}
	
	/**
	 * The binaryColumn Class
	 */
	protected class BinaryColumn extends ColumnDefinition {
		/**
		 * The length of the column 
		 */
		private int theLength	= 0;
		
		/**
		 * Constructor
		 * @param pId the column id
		 * @param pName the name of the column
		 * @param pLength the length of the column
		 */
		private BinaryColumn(int pId, String pName, int pLength) {
			/* Record the column type */
			super(pId, pName);
			theLength	= pLength;
		}
		
		/**
		 * Build the column type 
		 * @param pBuilder the String builder
		 */
		protected void buildColumnType(StringBuilder pBuilder) {
			/* Add the column type */
			pBuilder.append("varbinary(");
			pBuilder.append(theLength);
			pBuilder.append(')');
		}
		
		/**
		 * Set the value
		 * @param pValue the value
		 */
		private void setValue(byte[] pValue) {
			super.setValue(pValue);
		}
		
		/**
		 * Get the value
		 * @return the value
		 */
		private byte[] getValue() {
			return (byte[])theValue;
		}
		
		/** 
		 *  Load data from column
		 *  @param the index to load from
		 */
		protected void loadValue(int pIndex) throws SQLException {
			byte[] myValue = theResults.getBytes(pIndex);
			setValue(myValue);
		}
		
		/** 
		 *  Store data to column
		 *  @param the index to store to
		 */
		protected void storeValue(int pIndex) throws SQLException {
			theStatement.setBytes(pIndex, getValue());
		}
	}
	
	/**
	 * Sort order indication
	 */
	public enum SortOrder {
		ASCENDING,
		DESCENDING;
	}
}
