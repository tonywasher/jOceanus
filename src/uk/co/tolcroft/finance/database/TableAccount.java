package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableAccount extends DatabaseTable<Account> {
	/**
	 * The name of the Account table
	 */
	private final static String theTabName 		= Account.listName;
				
	/**
	 * The name of the Account column
	 */
	private final static String theNameCol    	= Account.fieldName(Account.FIELD_NAME);

	/**
	 * The name of the Account Type column
	 */
	private final static String theActTypCol 	= Account.fieldName(Account.FIELD_TYPE);

	/**
	 * The name of the Description column
	 */
	private final static String theDescCol   	= Account.fieldName(Account.FIELD_DESC);

	/**
	 * The name of the Maturity column
	 */
	private final static String theMatureCol 	= Account.fieldName(Account.FIELD_MATURITY);

	/**
	 * The name of the Closed column
	 */
	private final static String theCloseCol  	= Account.fieldName(Account.FIELD_CLOSE);

	/**
	 * The name of the Parent column
	 */
	private final static String theParentCol 	= Account.fieldName(Account.FIELD_PARENT);
	
	/**
	 * The name of the Alias column
	 */
	private final static String theAliasCol 	= Account.fieldName(Account.FIELD_ALIAS);
	
	/**
	 * The name of the WebSite column
	 */
	private final static String theWebSiteCol 	= Account.fieldName(Account.FIELD_WEBSITE);
	
	/**
	 * The name of the CustNo column
	 */
	private final static String theCustNoCol 	= Account.fieldName(Account.FIELD_CUSTNO);
	
	/**
	 * The name of the UserId column
	 */
	private final static String theUserIdCol 	= Account.fieldName(Account.FIELD_USERID);
	
	/**
	 * The name of the Password column
	 */
	private final static String thePasswordCol 	= Account.fieldName(Account.FIELD_PASSWORD);
	
	/**
	 * The name of the Account column
	 */
	private final static String theAcctCol 		= Account.fieldName(Account.FIELD_ACCOUNT);
	
	/**
	 * The name of the Notes column
	 */
	private final static String theNotesCol 	= Account.fieldName(Account.FIELD_NOTES);
	
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableAccount(Database 	pDatabase) {
		super(pDatabase, theTabName);
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
		
	/* Determine the item name */
	protected String getItemsName() { return theTabName; }

	/* Get the List for the table for loading */
	protected Account.List  getLoadList(DataSet pData) {
		return pData.getAccounts();
	}
	
	/* Get the List for the table for updates */
	protected Account.List  getUpdateList(DataSet pData) {
		return new Account.List(pData.getAccounts(), ListStyle.UPDATE);
	}
	
	/* Create statement for Accounts */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " int NOT NULL PRIMARY KEY, " +
			   theNameCol	+ " varbinary(" + 2*Account.NAMELEN + ") NOT NULL, " +
			   theActTypCol	+ " int NOT NULL " +
			   		"REFERENCES " + TableAccountType.idReference() + ", " +
   			   theDescCol	+ " varbinary(" + 2*Account.DESCLEN + ") NULL, " +
			   theMatureCol	+ " date NULL, " +
			   theCloseCol	+ " date NULL, " +
			   theParentCol	+ " int NULL " +
				  	"REFERENCES " + idReference() + ", " +
			   theAliasCol	+ " int NULL " +
				  	"REFERENCES " + idReference() + ", " +
			   theWebSiteCol + " varbinary(" + 2*Account.WSITELEN + ") NULL, " +
			   theCustNoCol + " varbinary(" + 2*Account.CUSTLEN + ") NULL, " +
			   theUserIdCol + " varbinary(" + 2*Account.UIDLEN + ") NULL, " +
			   thePasswordCol + " varbinary(" + 2*Account.PWDLEN + ") NULL, " +
			   theAcctCol + " varbinary(" + 2*Account.ACTLEN + ") NULL, " +
			   theNotesCol + " varbinary(" + 2*Account.NOTELEN + ") NULL )";
	}
	
	/* Load statement for Accounts */
	protected String loadStatement() {
		return "select " + theIdCol + "," + theNameCol + "," + 
		                 theActTypCol + "," + theDescCol + "," +
		                 theMatureCol + "," + theCloseCol + "," +
		                 theParentCol + "," + theAliasCol + "," +
		                 theWebSiteCol + "," + theCustNoCol + "," +
		                 theUserIdCol + "," + thePasswordCol + "," +
		                 theAcctCol + "," + theNotesCol + 
		                 " from " + getTableName();			
	}
	
	/* Load the account */
	protected void loadItem() throws Exception {
		Account.List	myList;
		int	    		myId;
		byte[]  		myName;
		int    			myActTypeId;
		Integer	   		myParentId;
		Integer    		myAliasId;
		byte[]  		myDesc;
		java.util.Date  myMaturity;
		java.util.Date  myClosed;
		byte[]     		myWebSite;
		byte[]     		myCustNo;
		byte[]     		myUserId;
		byte[]     		myPassword;
		byte[]     		myAccount;
		byte[]     		myNotes;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId        	= getInteger();
			myName   		= getBinary();
			myActTypeId 	= getInteger();
			myDesc      	= getBinary();
			myMaturity  	= getDate();
			myClosed    	= getDate();
			myParentId		= getInteger();
			myAliasId		= getInteger();
			myWebSite		= getBinary();
			myCustNo		= getBinary();
			myUserId		= getBinary();
			myPassword		= getBinary();
			myAccount		= getBinary();
			myNotes			= getBinary();
	
			/* Access the list */
			myList = (Account.List)getList();
			
			/* Add into the list */
			myList.addItem(myId, 
				           myName, 
				           myActTypeId,
				           myDesc, 
				           myMaturity,
				           myClosed,
				           myParentId,
				           myAliasId,
					       myWebSite,
					       myCustNo,
					       myUserId,
					       myPassword,
					       myAccount,
					       myNotes);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
					            "Failed to load " + theTabName + " item",
					            e);
		}
		
		/* Return to caller */
		return;
	}
	
	/* Insert statement for Accounts */
	protected String insertStatement() {
		return "insert into " + getTableName() + 
		       " (" + theIdCol + "," + theNameCol + "," +
		              theActTypCol + "," + theDescCol + "," +
		              theMatureCol + "," + theCloseCol + "," +
		              theParentCol + "," + theAliasCol + "," + 
		              theWebSiteCol + "," + theCustNoCol + "," +
		              theUserIdCol + "," + thePasswordCol + "," +
		              theAcctCol + "," + theNotesCol + ")" + 
		       " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	}
	
	/* Insert the account */
	protected void insertItem(Account	pItem) throws Exception {
		
		/* Protect the access */
		try {			
			/* Set the fields */
			setInteger(pItem.getId());
			setBinary(pItem.getNameBytes());
			setInteger(pItem.getActType().getId());
			setBinary(pItem.getDescBytes());
			setDate(pItem.getMaturity());
			setDate(pItem.getClose());
			setInteger((pItem.getParent() != null)
							? pItem.getParent().getId() : null);
			setInteger((pItem.getAlias() != null)
							? pItem.getAlias().getId() : null);
			setBinary(pItem.getWebSiteBytes());
			setBinary(pItem.getCustNoBytes());
			setBinary(pItem.getUserIdBytes());
			setBinary(pItem.getPasswordBytes());
			setBinary(pItem.getAccountBytes());
			setBinary(pItem.getNotesBytes());
		}
				
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								pItem,
					            "Failed to insert " + theTabName + " item",
					            e);
		}
		
		/* Return to caller */
		return;
	}
	
	/* Update the account */
	protected void updateItem(Account	pItem) throws Exception {
		Account.Values 	myBase;
		
		/* Access the base */
		myBase = (Account.Values)pItem.getBaseObj();
			
		/* Protect the update */
		try {			
			/* Update the fields */
			if (Utils.differs(pItem.getNameBytes(),
				  		  	  myBase.getNameBytes()))
				updateBinary(theNameCol, pItem.getNameBytes());
			if (Utils.differs(pItem.getDescBytes(),
						  	  myBase.getDescBytes())) 
				updateBinary(theDescCol, pItem.getDescBytes());
			if (Date.differs(pItem.getMaturity(),
				             myBase.getMaturity())) 
				updateDate(theMatureCol, pItem.getMaturity());
			if (Date.differs(pItem.getClose(),
						  	 myBase.getClose()))
				updateDate(theCloseCol, pItem.getClose());
			if (Account.differs(pItem.getParent(),
								myBase.getParent()))
				updateInteger(theParentCol, (pItem.getParent() != null)
												? pItem.getParent().getId() : null);
			if (Account.differs(pItem.getAlias(),
				  	  			myBase.getAlias()))
				updateInteger(theAliasCol, (pItem.getAlias() != null)
												? pItem.getAlias().getId() : null);
			if (Utils.differs(pItem.getWebSiteBytes(),
				  	  		  myBase.getWebSiteBytes()))
				updateBinary(theWebSiteCol, pItem.getWebSiteBytes());
			if (Utils.differs(pItem.getCustNoBytes(),
				  	  		  myBase.getCustNoBytes()))
				updateBinary(theCustNoCol, pItem.getCustNoBytes());
			if (Utils.differs(pItem.getUserIdBytes(),
				  	  		  myBase.getUserIdBytes()))
				updateBinary(theUserIdCol, pItem.getUserIdBytes());
			if (Utils.differs(pItem.getPasswordBytes(),
				  	  		  myBase.getPasswordBytes()))
				updateBinary(thePasswordCol, pItem.getPasswordBytes());
			if (Utils.differs(pItem.getAccountBytes(),
				  	  		  myBase.getAccountBytes()))
				updateBinary(theAcctCol, pItem.getAccountBytes());
			if (Utils.differs(pItem.getNotesBytes(),
				  	  		  myBase.getNotesBytes()))
				updateBinary(theNotesCol, pItem.getNotesBytes());
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								pItem,
					            "Failed to update " + theTabName + " item",
					            e);
		}
			
		/* Return to caller */
		return;
	}	
}
