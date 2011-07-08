package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.StaticClass;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public abstract class TableStaticData<T extends StaticData<?>> extends DatabaseTable<T> {
	/**
	 * The name of the StaticData class column
	 */
	private final static String 	theClassCol		= StaticData.fieldName(StaticData.FIELD_CLASSID);

	/**
	 * The name of the Description column
	 */
	private final static String 	theDescCol		= StaticData.fieldName(StaticData.FIELD_DESC);
			
	/**
	 * The name of the Data column
	 */
	private String 					theDataCol;
			
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableStaticData(Database 	pDatabase, 
							  String 	pTabName,
							  String	pDataCol) {
		super(pDatabase, pTabName);
		theDataCol = pDataCol;
	}
	
	/* Create statement for Static Data */
	protected String createStatement() {
		return "create table " + getTableName() + " ( " +
			   theIdCol 	+ " int NOT NULL PRIMARY KEY, " +
			   theClassCol 	+ " int NOT NULL, " +
			   theDataCol	+ " varbinary(" + StaticClass.NAMELEN + ") NOT NULL, " +
	   	   	   theDescCol	+ " varbinary(" + StaticClass.DESCLEN + ") NULL )";
	}
	
	/* Determine the item name */
	protected String getItemsName() { return getTableName(); }

	/* Load statement for Static Data */
	protected String loadStatement() {
		return "select " + theIdCol + "," + theClassCol + "," +
		   	   theDataCol + "," + theDescCol + 
		       " from " + getTableName();			
	}
		
	/* Load the Static Data */
	protected  abstract void loadTheItem(int pId, int pClassId, byte[] pRegime, byte[] pDesc) throws Exception;
	
	/* Load the static data */
	protected void loadItem() throws Exception {
		int	    						myId;
		int	    						myClassId;
		byte[]  						myType;
		byte[]  						myDesc;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId   		= getInteger();
			myClassId	= getInteger();
			myType 		= getBinary();
			myDesc 		= getBinary();
			
			/* Add into the list */
			loadTheItem(myId, myClassId, myType, myDesc);
		}

		catch (Exception e) { throw e; }
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
					            "Failed to load " + getTableName() + " item",
					            e);
		}
		
		/* Return to caller */
		return;
	}
	
	/* Insert statement for Static Data */
	protected String insertStatement() {
		return "insert into " + getTableName() + 
	       " (" + theIdCol + "," + theClassCol + "," +
    		      theDataCol + "," + theDescCol + ")" +
		       " VALUES(?,?,?,?)";
	}
		
	/* Insert a Static Data */
	protected void insertItem(T	pItem) throws Exception  {
		
		/* Protect the load */
		try {			
			/* Set the fields */
			setInteger(pItem.getId());
			setInteger(pItem.getStaticClassId());
			setBinary(pItem.getNameBytes());
			setBinary(pItem.getDescBytes());
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								pItem,
					            "Failed to insert " + getTableName() + " item",
					            e);
		}
		
		/* Return to caller */
		return;
	}

	/* Update the Static Data */
	protected void updateItem(T	pItem) throws Exception {
		StaticData<?>.Values	myBase;
		
		/* Access the base */
		myBase = (StaticData<?>.Values)pItem.getBaseObj();
			
		/* Protect the update */
		try {			
			/* Update the fields */
			if (Utils.differs(pItem.getNameBytes(),
				  		  	  myBase.getNameBytes()))
				updateBinary(theDataCol, pItem.getNameBytes());
			if (Utils.differs(pItem.getDescBytes(),
		  		  	          myBase.getDescBytes()))
				updateBinary(theDescCol, pItem.getDescBytes());
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								pItem,
					            "Failed to update " + getTableName() + " item",
					            e);
		}
			
		/* Return to caller */
		return;
	}	
}
