package uk.co.tolcroft.finance.database;

import java.sql.SQLException;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableAccount extends DatabaseTable<Account> {
	/**
	 * The name of the Account table
	 */
	private final static String theTabName 		= "Accounts";
				
	/**
	 * The name of the Account column
	 */
	private final static String theNameCol    	= "Name";

	/**
	 * The name of the Account Type column
	 */
	private final static String theActTypCol 	= "AccountType";

	/**
	 * The name of the Description column
	 */
	private final static String theDescCol   	= "Description";

	/**
	 * The name of the Maturity column
	 */
	private final static String theMatureCol 	= "Maturity";

	/**
	 * The name of the Closed column
	 */
	private final static String theCloseCol  	= "Closed";

	/**
	 * The name of the Parent column
	 */
	private final static String theParentCol 	= "Parent";
	
	/**
	 * The name of the Alias column
	 */
	private final static String theAliasCol 	= "Alias";
	
	/**
	 * The name of the Parent column
	 */
	private final static String theInitVCol 	= "InitVector";
	
	/**
	 * The name of the WebSite column
	 */
	private final static String theWebSiteCol 	= "WebSite";
	
	/**
	 * The name of the CustNo column
	 */
	private final static String theCustNoCol 	= "CustomerNo";
	
	/**
	 * The name of the UserId column
	 */
	private final static String theUserIdCol 	= "UserId";
	
	/**
	 * The name of the Password column
	 */
	private final static String thePasswordCol 	= "Password";
	
	/**
	 * The name of the Account column
	 */
	private final static String theAcctCol 		= "Account";
	
	/**
	 * The name of the Notes column
	 */
	private final static String theNotesCol 	= "Notes";
	
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
		
	/* Get the List for the table for loading */
	protected Account.List  getLoadList(DataSet pData) {
		return pData.getAccounts();
	}
	
	/* Get the List for the table for updates */
	protected Account.List  getUpdateList(DataSet pData) {
		return new Account.List(pData.getAccounts(), ListStyle.UPDATE);
	}
	
	/* Determine the item name */
	protected String getItemsName() { return theTabName; }

	/**
	 * Determine the Name of the newly loaded item
	 * @return the Name
	 */
	private String getName() throws SQLException {
		return getString();
	}

	/**
	 * Determine the Account Type of the newly loaded item
	 * @return the AccountType
	 */
	private int getAccountType() throws SQLException {
		return getInteger();
	}

	/**
	 * Determine the Description of the newly loaded item
	 * @return the Description
	 */
	private String getDescription() throws SQLException {
		return getString();
	}

	/**
	 * Determine the Maturity of the newly loaded item
	 * @return the Maturity
	 */
	private java.util.Date getMaturity() throws SQLException {
		return getDate();
	}

	/**
	 * Determine the Closed Date of the newly loaded item
	 * @return the ClosedDate
	 */
	private java.util.Date getClosed() throws SQLException {
		return getDate();
	}

	/**
	 * Determine the Parent of the newly loaded item
	 * @return the Parent
	 */
	private int getParent() throws SQLException {
		Integer myResult = getInteger();
		if (myResult == null) return -1;
		return myResult;
	}

	/**
	 * Determine the Alias of the newly loaded item
	 * @return the Alias
	 */
	private int getAlias() throws SQLException {
		Integer myResult = getInteger();
		if (myResult == null) return -1;
		return myResult;
	}

	/**
	 * Determine the InitVector of the newly loaded item
	 * @return the InitVector
	 */
	private byte[] getInitVector() throws SQLException {
		return getBinary();
	}

	/**
	 * Determine the WebSite of the newly loaded item
	 * @return the WebSite
	 */
	private byte[] getWebSite() throws SQLException {
		return getBinary();
	}

	/**
	 * Determine the CustNo of the newly loaded item
	 * @return the CustomerNo
	 */
	private byte[] getCustNo() throws SQLException {
		return getBinary();
	}

	/**
	 * Determine the UserId of the newly loaded item
	 * @return the UserId
	 */
	private byte[] getUserId() throws SQLException {
		return getBinary();
	}

	/**
	 * Determine the Password of the newly loaded item
	 * @return the Password
	 */
	private byte[] getPassword() throws SQLException {
		return getBinary();
	}

	/**
	 * Determine the Account of the newly loaded item
	 * @return the Account
	 */
	private byte[] getAccount() throws SQLException {
		return getBinary();
	}

	/**
	 * Determine the Notes of the newly loaded item
	 * @return the Notes
	 */
	private byte[] getNotes() throws SQLException {
		return getBinary();
	}

	/**
	 * Set the Name of the item to be inserted
	 * @param pAccount the name of the item
	 */
	private void setName(String pAccount) throws SQLException {
		setString(pAccount);
	}

	/**
	 * Set the AccountType of the item to be inserted
	 * @param pActType the Account type of the item
	 */
	private void setAccountType(int pActType) throws SQLException {
		setInteger(pActType);
	}

	/**
	 * Set the Description of the item to be inserted
	 * @param pDesc the description of the item
	 */
	private void setDescription(String pDesc) throws SQLException {
		setString(pDesc);
	}

	/**
	 * Set the Maturity of the item to be inserted/updated
	 * @param pMaturity the maturity of the item
	 */
	private void setMaturity(Date pMaturity) throws SQLException {
		setDate(pMaturity);
	}

	/**
	 * Set the Closed Date of the item to be inserted/updated
	 * @param pClosed the Close Date of the item
	 */
	private void setClosed(Date pClosed) throws SQLException {
		setDate(pClosed);
	}

	/**
	 * Set the Parent of the item to be inserted/updated
	 * @param pParent the id of the TaxRegime for the item
	 */
	private void setParent(int pParent) throws SQLException {
		setInteger((pParent == -1) ? null : pParent);
	}
	
	/**
	 * Set the Alias of the item to be inserted/updated
	 * @param pAlias the id of the TaxRegime for the item
	 */
	private void setAlias(int pAlias) throws SQLException {
		setInteger((pAlias == -1) ? null : pAlias);
	}
	
	/**
	 * Set the InitVector of the item to be inserted/updated
	 * @param pValue the initVector
	 */
	private void setInitVector(byte[] pValue) throws SQLException {
		setBinary(pValue);
	}

	/**
	 * Set the WebSite of the item to be inserted/updated
	 * @param pValue the webSite
	 */
	private void setWebSite(byte[] pValue) throws SQLException {
		setBinary(pValue);
	}

	/**
	 * Set the CustomerNo of the item to be inserted/updated
	 * @param pValue the custNo
	 */
	private void setCustNo(byte[] pValue) throws SQLException {
		setBinary(pValue);
	}

	/**
	 * Set the UserId of the item to be inserted/updated
	 * @param pValue the userId
	 */
	private void setUserId(byte[] pValue) throws SQLException {
		setBinary(pValue);
	}

	/**
	 * Set the Password of the item to be inserted/updated
	 * @param pValue the password
	 */
	private void setPassword(byte[] pValue) throws SQLException {
		setBinary(pValue);
	}

	/**
	 * Set the Account of the item to be inserted/updated
	 * @param pValue the account
	 */
	private void setAccount(byte[] pValue) throws SQLException {
		setBinary(pValue);
	}

	/**
	 * Set the Notes of the item to be inserted/updated
	 * @param pValue the notes
	 */
	private void setNotes(byte[] pValue) throws SQLException {
		setBinary(pValue);
	}

	/**
	 * Update the Name of the item
	 * @param pValue the new name
	 */
	private void updateName(String pValue) {
		updateString(theNameCol, pValue);
	}		

	/**
	 * Update the Description of the item
	 * @param pValue the new description
	 */
	private void updateDescription(String pValue) {
		updateString(theDescCol, pValue);
	}	

	/**
	 * Update the Maturity of the item
	 * @param pValue the new maturity
	 */
	private void updateMaturity(Date pValue) {
		updateDate(theMatureCol, pValue);
	}

	/**
	 * Update the Closed Date of the item
	 * @param pValue the new closed date
	 */
	private void updateClosed(Date pValue) {
		updateDate(theCloseCol, pValue);
	}

	/**
	 * Update the Parent of the item
	 * @param pValue the new parent
	 */
	private void updateParent(int pValue) {
		updateInteger(theParentCol, (pValue == -1) ? null : pValue);
	}
	
	/**
	 * Update the Alias of the item
	 * @param pValue the new alias
	 */
	private void updateAlias(int pValue) {
		updateInteger(theAliasCol, (pValue == -1) ? null : pValue);
	}
	
	/**
	 * Update the InitVector of the item
	 * @param pValue the new initVector
	 */
	private void updateInitVector(byte[] pValue) {
		updateBinary(theInitVCol, pValue);
	}

	/**
	 * Update the WebSite of the item
	 * @param pValue the new webSite
	 */
	private void updateWebSite(byte[] pValue) {
		updateBinary(theWebSiteCol, pValue);
	}

	/**
	 * Update the CustomerNo of the item
	 * @param pValue the new custNo
	 */
	private void updateCustNo(byte[] pValue) {
		updateBinary(theCustNoCol, pValue);
	}

	/**
	 * Update the UserId of the item
	 * @param pValue the new userId
	 */
	private void updateUserId(byte[] pValue) {
		updateBinary(theUserIdCol, pValue);
	}

	/**
	 * Update the Password of the item
	 * @param pValue the new password
	 */
	private void updatePassword(byte[] pValue) {
		updateBinary(thePasswordCol, pValue);
	}

	/**
	 * Update the Account of the item
	 * @param pValue the new Account
	 */
	private void updateAccount(byte[] pValue) {
		updateBinary(theAcctCol, pValue);
	}

	/**
	 * Update the Notes of the item
	 * @param pValue the new notes
	 */
	private void updateNotes(byte[] pValue) {
		updateBinary(theNotesCol, pValue);
	}

	/* Create statement for Accounts */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " int NOT NULL PRIMARY KEY, " +
			   theNameCol	+ " varchar(" + Account.NAMELEN + ") NOT NULL, " +
			   theActTypCol	+ " int NOT NULL " +
			   		"REFERENCES " + TableAccountType.idReference() + ", " +
   			   theDescCol	+ " varchar(" + Account.DESCLEN + ") NULL, " +
			   theMatureCol	+ " date NULL, " +
			   theCloseCol	+ " date NULL, " +
			   theParentCol	+ " int NULL " +
				  	"REFERENCES " + idReference() + ", " +
			   theAliasCol	+ " int NULL " +
				  	"REFERENCES " + idReference() + ", " +
			   theInitVCol	+ " binary(" + Account.INITVLEN + ") NULL, " +
			   theWebSiteCol + " varbinary(" + Account.WSITELEN + ") NULL, " +
			   theCustNoCol + " varbinary(" + Account.CUSTLEN + ") NULL, " +
			   theUserIdCol + " varbinary(" + Account.UIDLEN + ") NULL, " +
			   thePasswordCol + " varbinary(" + Account.PWDLEN + ") NULL, " +
			   theAcctCol + " varbinary(" + Account.ACTLEN + ") NULL, " +
			   theNotesCol + " varbinary(" + Account.NOTELEN + ") NULL )";
	}
	
	/* Load statement for Accounts */
	protected String loadStatement() {
		return "select " + theIdCol + "," + theNameCol + "," + 
		                 theActTypCol + "," + theDescCol + "," +
		                 theMatureCol + "," + theCloseCol + "," +
		                 theParentCol + "," + theAliasCol + "," +
		                 theInitVCol + "," +
		                 theWebSiteCol + "," + theCustNoCol + "," +
		                 theUserIdCol + "," + thePasswordCol + "," +
		                 theAcctCol + "," + theNotesCol + 
		                 " from " + getTableName();			
	}
	
	/* Load the account */
	protected void loadItem() throws Exception {
		Account.List	myList;
		int	    		myId;
		String  		myName;
		int    			myActTypeId;
		int 	   		myParentId;
		int	    		myAliasId;
		String  		myDesc;
		java.util.Date  myMaturity;
		java.util.Date  myClosed;
		byte[]     		myInitVector;
		byte[]     		myWebSite;
		byte[]     		myCustNo;
		byte[]     		myUserId;
		byte[]     		myPassword;
		byte[]     		myAccount;
		byte[]     		myNotes;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId        	= getID();
			myName   		= getName();
			myActTypeId 	= getAccountType();
			myDesc      	= getDescription();
			myMaturity  	= getMaturity();
			myClosed    	= getClosed();
			myParentId		= getParent();
			myAliasId		= getAlias();
			myInitVector	= getInitVector();
			myWebSite		= getWebSite();
			myCustNo		= getCustNo();
			myUserId		= getUserId();
			myPassword		= getPassword();
			myAccount		= getAccount();
			myNotes			= getNotes();
	
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
					       myInitVector,
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
		              theInitVCol + "," +
		              theWebSiteCol + "," + theCustNoCol + "," +
		              theUserIdCol + "," + thePasswordCol + "," +
		              theAcctCol + "," + theNotesCol + ")" + 
		       " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	}
	
	/* Insert the account */
	protected void insertItem(Account	pItem) throws Exception {
		
		/* Protect the access */
		try {			
			/* Set the fields */
			setID(pItem.getId());
			setName(pItem.getName());
			setAccountType(pItem.getActType().getId());
			setDescription(pItem.getDesc());
			setMaturity(pItem.getMaturity());
			setClosed(pItem.getClose());
			setParent((pItem.getParent() != null)
							? pItem.getParent().getId() : -1);
			setAlias((pItem.getAlias() != null)
							? pItem.getAlias().getId() : -1);
			setInitVector(pItem.getInitVector());
			setWebSite(pItem.getWebSite());
			setCustNo(pItem.getCustNo());
			setUserId(pItem.getUserId());
			setPassword(pItem.getPassword());
			setAccount(pItem.getAccount());
			setNotes(pItem.getNotes());
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
			if (Utils.differs(pItem.getName(),
				  		  	  myBase.getName()))
				updateName(pItem.getName());
			if (Utils.differs(pItem.getDesc(),
						  	  myBase.getDesc())) 
				updateDescription(pItem.getDesc());
			if (Utils.differs(pItem.getMaturity(),
				              myBase.getMaturity())) 
				updateMaturity(pItem.getMaturity());
			if (Utils.differs(pItem.getClose(),
						  	  myBase.getClose()))
				updateClosed(pItem.getClose());
			if (Utils.differs(pItem.getParent(),
						  	  myBase.getParent()))
				updateParent((pItem.getParent() != null)
									? pItem.getParent().getId() : -1);
			if (Utils.differs(pItem.getAlias(),
				  	  		  myBase.getAlias()))
				updateAlias((pItem.getAlias() != null)
									? pItem.getAlias().getId() : -1);
			if (Utils.differs(pItem.getInitVector(),
				  	  		  myBase.getInitVector()))
				updateInitVector(pItem.getInitVector());
			if (Utils.differs(pItem.getWebSite(),
				  	  		  myBase.getWebSite()))
				updateWebSite(pItem.getWebSite());
			if (Utils.differs(pItem.getCustNo(),
				  	  		  myBase.getCustNo()))
				updateCustNo(pItem.getCustNo());
			if (Utils.differs(pItem.getUserId(),
				  	  		  myBase.getUserId()))
				updateUserId(pItem.getUserId());
			if (Utils.differs(pItem.getPassword(),
				  	  		  myBase.getPassword()))
				updatePassword(pItem.getPassword());
			if (Utils.differs(pItem.getAccount(),
				  	  		  myBase.getAccount()))
				updateAccount(pItem.getAccount());
			if (Utils.differs(pItem.getNotes(),
				  	  		  myBase.getNotes()))
				updateNotes(pItem.getNotes());
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
