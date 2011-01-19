package finance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import finance.finLink.itemCtl.ListStyle;
import finance.finObject.ExceptionClass;
import finance.finObject.State;
import finance.finBuilder;
import finance.finThread.statusCtl;

public class finStorage {
		
	/* SQL Server tables */
	private static class Table {
		private final static int    BATCH_SIZE = 50;
		private final static String theIdCol   = "ID";
		
		/* Members */
		private finProperties		theProperties	= null;
		private AccountTypes        theAccountTypes = null;
		private TransTypes          theTransTypes   = null;
		private TaxTypes            theTaxTypes     = null;
		private TaxRegimes          theTaxRegimes   = null;
		private Frequencys          theFrequencys   = null;
		private TaxParams           theTaxParams    = null;
		private Accounts            theAccounts     = null;
		private Rates               theRates        = null;
		private Prices              thePrices       = null;
		private Patterns            thePatterns     = null;
		private Events              theEvents       = null;
		private Connection          theConn         = null;
		private PreparedStatement   theStmt         = null;
		private ResultSet           theResults      = null;
		private String              theUpdates      = null;

		/* Access methods */
		public AccountTypes getAccountTypes() { return theAccountTypes; }
		public TransTypes   getTransTypes()   { return theTransTypes; }
		public TaxTypes     getTaxTypes()     { return theTaxTypes; }
		public TaxRegimes   getTaxRegimes()   { return theTaxRegimes; }
		public Frequencys   getFrequencys()   { return theFrequencys; }
		public TaxParams    getTaxParams()    { return theTaxParams; }
		public Accounts     getAccounts()     { return theAccounts; }
		public Rates        getRates()        { return theRates; }
		public Prices       getPrices()       { return thePrices; }
		public Patterns     getPatterns()     { return thePatterns; }
		public Events       getEvents()       { return theEvents; }
		
		/**
		 * Construct a new SQL Tables class
		 * @throws finObject.Exception
		 */
		public Table(finProperties pProperties) throws finObject.Exception {
			/* Store the properties */
			theProperties = pProperties;
			
			/* Create the connection */
			try {
				Class.forName(theProperties.getDBDriver());	   
				theConn = DriverManager.getConnection(theProperties.getDBConnection());
				theConn.setAutoCommit(false);
			}
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.SQLSERVER,
											  "Failed to load driver",
											  e);
			}
			
			/* Create the inner classes to handle the various tables */
			theAccountTypes = this.new AccountTypes();
			theTransTypes   = this.new TransTypes();
			theTaxTypes     = this.new TaxTypes();
			theTaxRegimes   = this.new TaxRegimes();
			theFrequencys   = this.new Frequencys();
			theTaxParams    = this.new TaxParams();
			theAccounts     = this.new Accounts();
			theRates        = this.new Rates();
			thePrices       = this.new Prices();
			thePatterns     = this.new Patterns();
			theEvents       = this.new Events();
		}
		
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
		 *  Determine the batch size
		 *  @return the batch size
		 */
		public int getBatchSize() { return BATCH_SIZE; }
		
		/** 
		 *  Close the result set and statement
		 */
		public void closeStmt() throws SQLException {
			theUpdates = null;
			if (theResults != null) theResults.close();
			if (theStmt    != null) theStmt.close();
		}
		
		/** 
		 *  Shift to next line in result set
		 */
		public boolean next() throws SQLException {
			return theResults.next();
		}
		
		/** 
		 *  Execute the prepared statement
		 */
		public void execute() throws SQLException {
			theStmt.executeUpdate();
		}
		
		/** 
		 *  Query the prepared statement
		 */
		public void executeQuery() throws SQLException {
			theResults = theStmt.executeQuery();
		}
		
		/** 
		 *  Execute an update statement
		 *  @param pTable the table to execute update against
		 *  @param uId the id to execute updates against
		 */
		public void executeUpdate(String pTable,
				                  long   uId) throws SQLException {
			String myString;
			if (theUpdates != null) {
				myString = "update " + pTable + " set " +
				           theUpdates + " where " + theIdCol +
				           " = " + uId;
				theStmt = theConn.prepareStatement(myString);
				theStmt.executeUpdate();
			}
		}
		
		/** 
		 *  Add an update field
		 *  @param pField the field name
		 *  @param pValue the string value
		 */
		private void addUpdate(String pField,
				               String pValue) {
			if (theUpdates != null) theUpdates += ", ";
			theUpdates += pField + "=";
			if (pValue != null)
				theUpdates += "'" + pValue + "'";
			else
				theUpdates += "NULL";
		}
		
		/** 
		 *  Add an update field
		 *  @param pField the field name
		 *  @param pValue the long value
		 */
		private void addUpdate(String pField,
				               long   pValue) {
			if (theUpdates != null) theUpdates += ", ";
			theUpdates += pField + "=" + pValue;
		}
		
		/** 
		 *  Determine the number of records to be loaded
		 *  @param pTable the table to be queried
		 *  @return the count of items to be loaded 
		 */
		public int countTheItems(String pTable) throws SQLException {
			String myString;
			int    myCount = 0;

			myString   = "select count(*) from " + pTable;
			theStmt    = theConn.prepareStatement(myString);
			theResults = theStmt.executeQuery();
			
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
		 *  Commit the outstanding transaction
		 */
		public void commit() throws SQLException {
			theConn.commit();
		}
		
		/**
		 * Roll back the outstanding transaction
		 */
		public void rollback() throws SQLException {
			theConn.rollback();
		}
		
		/**
		 * Close the connection to the database 
		 * rolling back any outstanding transaction
		 * @throws SQLException
		 */
		public void close() throws SQLException {
			/* Roll-back any outstanding transaction */
			rollback();
			
			/* Close the result set and statements */
			closeStmt();

			/* Close the connection */
			theConn.close();
		}
		
		/**
		 *  abstract class to handle generic table access
		 */
		public abstract class TableModel {
			/**
			 * The table name
			 */
			private String theTable = null;
			
			/**
			 * Constructor
			 */
			private TableModel(String pTable) {
				/* Set the table */
				theTable = pTable;
			}
			
			/**
			 * Access the table name 
			 * @return the table name
			 */
			public String getTableName() { return theTable; }
			
			/**
			 * Count the number of items to be loaded
			 * @return the count of items
			 * @throws SQLException
			 */
			public int countItems() throws SQLException {
				return countTheItems(theTable);			
			}
			
			/**
			 * Load the items from the database
			 * @throws SQLException
			 */
			public abstract void 	loadItems() 	throws SQLException;
			
			/**
			 * Insert the items into the database
			 * @throws SQLException
			 */
			public abstract void 	insertItems() 	throws SQLException;
			
			/**
			 * delete the items from the database
			 * @throws SQLException
			 */
			public void 	deleteItems() throws SQLException {};
			
			/**
			 * Determine the ID of the newly loaded item
			 * @return the ID
			 * @throws SQLException
			 */
			public long getID() throws SQLException {
				return theResults.getLong(1);
			}			
			
			/**
			 * Set the ID of the item to be inserted/updated
			 * @param uId the ID of the item
			 * @throws SQLException
			 */
			public void setID(long uId) throws SQLException {
				theStmt.setLong(1, uId);
			}
			
			/**
			 * Execute the updates for the specified id
			 * @param uId the ID of the item
			 * @throws SQLException
			 */
			public void updateId(long uId) throws SQLException {
				executeUpdate(theTable, uId);
			}			
		}
		
		/**
		 *  Inner class to handle Account Types table access
		 */
		public class AccountTypes extends TableModel {
			/**
			 * The name of the AccountType column
			 */
			private final static String theActTypCol = "AccountType";
					
			/**
			 * Constructor
			 */
			private AccountTypes() { super("AccountTypes"); };
			
			/**
			 * Load the Account Types from the database
			 * @throws SQLException
			 */
			public void loadItems() throws SQLException {
				String myQuery = "select " + theIdCol + "," + theActTypCol + 
				                 " from " + getTableName();			
				theStmt = theConn.prepareStatement(myQuery);
				executeQuery();
			}
			
			/**
			 * Determine the Account Type of the newly loaded item
			 * @return the Account Type
			 * @throws SQLException
			 */
			public String getAccountType() throws SQLException {
				return theResults.getString(2);
			}
			
			/**
			 * Insert the Account Types into the database
			 * @throws SQLException
			 */
			public void insertItems() throws SQLException {
				String myCmd = "insert into " + getTableName() + 
				               " (" + theIdCol + "," + theActTypCol + ")" +
				               " VALUES(?,?)";
				theStmt = theConn.prepareStatement(myCmd);
			}
			
			/**
			 * Set the AccountType of the item to be inserted/updated
			 * @param pActType the Account Type of the item
			 * @throws SQLException
			 */
			public void setAccountType(String pActType) throws SQLException {
				theStmt.setString(2, pActType);
			}
		}
		
		/**
		 *  Inner class to handle Transaction Types table access
		 */
		public class TransTypes extends TableModel {
			/**
			 * The name of the TransType column
			 */
			private final static String theTrnTypCol = "TransactionType";
					
			/**
			 * Constructor
			 */
			private TransTypes() { super("TransactionTypes"); };
			
			/**
			 * Load the Transaction Types from the database
			 * @throws SQLException
			 */
			public void loadItems() throws SQLException {
				String myQuery = "select " + theIdCol + "," + theTrnTypCol + 
				                 " from " + getTableName();			
				theStmt = theConn.prepareStatement(myQuery);
				executeQuery();
			}
			
			/**
			 * Determine the Transaction Type of the newly loaded item
			 * @return the Transaction Type
			 * @throws SQLException
			 */
			public String getTransType() throws SQLException {
				return theResults.getString(2);
			}
			
			/**
			 * Insert the Transaction Types into the database
			 * @throws SQLException
			 */
			public void insertItems() throws SQLException {
				String myCmd = "insert into " + getTableName() + 
				               " (" + theIdCol + "," + theTrnTypCol + ")" +
				               " VALUES(?,?)";
				theStmt = theConn.prepareStatement(myCmd);
			}
			
			/**
			 * Set the TransactionType of the item to be inserted/updated
			 * @param pTransType the Transaction Type of the item
			 * @throws SQLException
			 */
			public void setTransType(String pTransType) throws SQLException {
				theStmt.setString(2, pTransType);
			}
		}
		
		/**
		 *  Inner class to handle Tax Types table access
		 */
		public class TaxTypes extends TableModel {
			/**
			 * The name of the TaxType column
			 */
			private final static String theTaxTypCol = "TaxType";
					
			/**
			 * Constructor
			 */
			private TaxTypes() { super("TaxTypes"); };
			
			/**
			 * Load the Tax Types from the database
			 * @throws SQLException
			 */
			public void loadItems() throws SQLException {
				String myQuery = "select " + theIdCol + "," + theTaxTypCol + 
				                 " from " + getTableName();			
				theStmt = theConn.prepareStatement(myQuery);
				executeQuery();
			}
			
			/**
			 * Determine the Tax Type of the newly loaded item
			 * @return the Tax Type
			 * @throws SQLException
			 */
			public String getTaxType() throws SQLException {
				return theResults.getString(2);
			}
			
			/**
			 * Insert the Tax Types into the database
			 * @throws SQLException
			 */
			public void insertItems() throws SQLException {
				String myCmd = "insert into " + getTableName() + 
				               " (" + theIdCol + "," + theTaxTypCol + ")" +
				               " VALUES(?,?)";
				theStmt = theConn.prepareStatement(myCmd);
			}
			
			/**
			 * Set the TaxType of the item to be inserted/updated
			 * @param pTaxType the Tax Type of the item
			 * @throws SQLException
			 */
			public void setTaxType(String pTaxType) throws SQLException {
				theStmt.setString(2, pTaxType);
			}
		}
		
		/**
		 *  Inner class to handle Tax Regimes table access
		 */
		public class TaxRegimes extends TableModel {
			/**
			 * The name of the TaxRegime column
			 */
			private final static String theTaxRegCol = "TaxRegime";
					
			/**
			 * Constructor
			 */
			private TaxRegimes() { super("TaxRegimes"); };
			
			/**
			 * Load the Tax Regimes from the database
			 * @throws SQLException
			 */
			public void loadItems() throws SQLException {
				String myQuery = "select " + theIdCol + "," + theTaxRegCol + 
				                 " from " + getTableName();			
				theStmt = theConn.prepareStatement(myQuery);
				executeQuery();
			}
			
			/**
			 * Determine the Tax Regime of the newly loaded item
			 * @return the Tax Regime
			 * @throws SQLException
			 */
			public String getTaxRegime() throws SQLException {
				return theResults.getString(2);
			}
			
			/**
			 * Insert the Tax Regimes into the database
			 * @throws SQLException
			 */
			public void insertItems() throws SQLException {
				String myCmd = "insert into " + getTableName() + 
				               " (" + theIdCol + "," + theTaxRegCol + ")" +
				               " VALUES(?,?)";
				theStmt = theConn.prepareStatement(myCmd);
			}
			
			/**
			 * Set the TaxRegime of the item to be inserted/updated
			 * @param pTaxRegime the Tax Regime of the item
			 * @throws SQLException
			 */
			public void setTaxRegime(String pTaxRegime) throws SQLException {
				theStmt.setString(2, pTaxRegime);
			}
		}
		
		/**
		 *  Inner class to handle Frequencies table access
		 */
		public class Frequencys extends TableModel {
			/**
			 * The name of the TaxType column
			 */
			private final static String theFreqCol 	= "Frequency";
					
			/**
			 * Constructor
			 */
			private Frequencys() { super("Frequencys"); };
			
			/**
			 * Load the Frequencies from the database
			 * @throws SQLException
			 */
			public void loadItems() throws SQLException {
				String myQuery = "select " + theIdCol + "," + theFreqCol + 
				                 " from " + getTableName();			
				theStmt = theConn.prepareStatement(myQuery);
				executeQuery();
			}
			
			/**
			 * Determine the Frequency of the newly loaded item
			 * @return the Frequency
			 * @throws SQLException
			 */
			public String getFrequency() throws SQLException {
				return theResults.getString(2);
			}
			
			/**
			 * Insert the Frequencies into the database
			 * @throws SQLException
			 */
			public void insertItems() throws SQLException {
				String myCmd = "insert into " + getTableName() + 
				               " (" + theIdCol + "," + theFreqCol + ")" +
				               " VALUES(?,?)";
				theStmt = theConn.prepareStatement(myCmd);
			}
			
			/**
			 * Set the Frequency of the item to be inserted/updated
			 * @param pFrequency the Frequency of the item
			 * @throws SQLException
			 */
			public void setFrequency(String pFrequency) throws SQLException {
				theStmt.setString(2, pFrequency);
			}
		}
		
		/**
		 *  Inner class to handle TaxParams table access
		 */
		public class TaxParams extends TableModel {
			/**
			 * The name of the Year column
			 */
			private final static String theYearCol   = "Year";

			/**
			 * The name of the Allowance column
			 */
			private final static String theAllwCol   = "PersonalAllowance";

			/**
			 * The name of the Rental Allowance column
			 */
			private final static String theRentCol   = "RentalAllowance";

			/**
			 * The name of the LoTaxBand column
			 */
			private final static String theLoBdCol   = "LoTaxBand";

			/**
			 * The name of the BasicTaxBand column
			 */
			private final static String theBsBdCol   = "BasicTaxBand";

			/**
			 * The name of the LoAgeAllow column
			 */
			private final static String theLoAACol   = "LoAgeAllowance";

			/**
			 * The name of the HiAgeAllow column
			 */
			private final static String theHiAACol   = "HiAgeAllowance";

			/**
			 * The name of the HiAgeAllow column
			 */
			private final static String theCpAlCol   = "CapitalAllowance";

			/**
			 * The name of the AgeAllowLimit column
			 */
			private final static String theAgLmCol   = "AgeAllowanceLimit";

			/**
			 * The name of the AddAllowLimit column
			 */
			private final static String theAdLmCol   = "AddAllowanceLimit";

			/**
			 * The name of the AddIncBoundary column
			 */
			private final static String theAdBdCol   = "AddIncomeBoundary";

			/**
			 * The name of the LoTaxRate column
			 */
			private final static String theLoTxCol   = "LoTaxRate";

			/**
			 * The name of the BasicTaxRate column
			 */
			private final static String theBsTxCol   = "BasicTaxRate";

			/**
			 * The name of the HiTaxRate column
			 */
			private final static String theHiTxCol   = "HiTaxRate";

			/**
			 * The name of the IntTaxRate column
			 */
			private final static String theInTxCol   = "IntTaxRate";

			/**
			 * The name of the DivTaxRate column
			 */
			private final static String theDvTxCol   = "DivTaxRate";

			/**
			 * The name of the HiDivTaxRate column
			 */
			private final static String theHDTxCol   = "HiDivTaxRate";

			/**
			 * The name of the AddTaxRate column
			 */
			private final static String theAdTxCol   = "AddTaxRate";

			/**
			 * The name of the AddDivTaxRate column
			 */
			private final static String theADTxCol   = "AddDivTaxRate";

			/**
			 * The name of the CapTaxRate column
			 */
			private final static String theCpTxCol   = "CapTaxRate";

			/**
			 * The name of the AddDivTaxRate column
			 */
			private final static String theHCTxCol   = "HiCapTaxRate";

			/**
			 * The name of the TaxRegime column
			 */
			private final static String theTxRgCol   = "TaxRegime";

			/**
			 * Constructor
			 */
			private TaxParams() { super("TaxParms"); };
			
			/* Load the TaxParams */
			public void loadItems() throws SQLException {
				String myQuery = "select " + theIdCol + "," + theYearCol + "," + 
	            				 theAllwCol + "," + theRentCol + "," +
	            				 theLoBdCol + "," + theBsBdCol + "," +
	            				 theLoTxCol + "," + theBsTxCol + "," + 
	            				 theLoAACol + "," + theHiAACol + "," + 
	            				 theAgLmCol + "," + theAdLmCol + "," + 
	            				 theHiTxCol + "," + theInTxCol + "," +
	            				 theDvTxCol + "," + theHDTxCol + "," +
	            				 theAdTxCol + "," + theADTxCol + "," +
	            				 theAdBdCol + "," + theCpAlCol + "," +
	            				 theCpTxCol + "," + theHCTxCol + "," +
	            				 theTxRgCol +
				                 " from " + getTableName() +			
                                 " order by " + theYearCol;			
				theStmt = theConn.prepareStatement(myQuery);
				executeQuery();
			}
			
			/**
			 * Determine the Year of the newly loaded item
			 * @return the Year
			 * @throws SQLException
			 */
			public Date getYear() throws SQLException {
				return theResults.getDate(2);
			}		

			/**
			 * Determine the Allowance of the newly loaded item
			 * @return the Allowance
			 * @throws SQLException
			 */
			public String getAllowance() throws SQLException {
				return theResults.getString(3);
			}

			/**
			 * Determine the RentalAllowance of the newly loaded item
			 * @return the Rental Allowance
			 * @throws SQLException
			 */
			public String getRentalAllow() throws SQLException {
				return theResults.getString(4);
			}

			/**
			 * Determine the LoTaxBand of the newly loaded item
			 * @return the LoTaxBand
			 * @throws SQLException
			 */
			public String getLoTaxBand() throws SQLException {
				return theResults.getString(5);
			}	

			/**
			 * Determine the BasicTaxBand of the newly loaded item
			 * @return the BasicTaxBand
			 * @throws SQLException
			 */
			public String getBasicTaxBand() throws SQLException {
				return theResults.getString(6);
			}

			/**
			 * Determine the LoAgeAllow of the newly loaded item
			 * @return the LoAgeAllow
			 * @throws SQLException
			 */
			public String getLoAgeAllow() throws SQLException {
				return theResults.getString(7);
			}

			/**
			 * Determine the HiAgeAllow of the newly loaded item
			 * @return the HiAgeAllow
			 * @throws SQLException
			 */
			public String getHiAgeAllow() throws SQLException {
				return theResults.getString(8);
			}

			/**
			 * Determine the AgeAllowLimit of the newly loaded item
			 * @return the AgeAllowLimit
			 * @throws SQLException
			 */
			public String getAgeAllowLimit() throws SQLException {
				return theResults.getString(9);
			}

			/**
			 * Determine the AddAllowLimit of the newly loaded item
			 * @return the AddAllowLimit
			 * @throws SQLException
			 */
			public String getAddAllowLimit() throws SQLException {
				return theResults.getString(10);
			}

			/**
			 * Determine the LoTaxRate of the newly loaded item
			 * @return the LoTaxRate
			 * @throws SQLException
			 */
			public String getLoTaxRate() throws SQLException {
				return theResults.getString(11);
			}

			/**
			 * Determine the BasicTaxRate of the newly loaded item
			 * @return the BasicTaxRate
			 * @throws SQLException
			 */
			public String getBasicTaxRate() throws SQLException {
				return theResults.getString(12);
			}

			/**
			 * Determine the HiTaxRate of the newly loaded item
			 * @return the HiLoTaxRate
			 * @throws SQLException
			 */
			public String getHiTaxRate() throws SQLException {
				return theResults.getString(13);
			}

			/**
			 * Determine the IntTaxRate of the newly loaded item
			 * @return the IntTaxRate
			 * @throws SQLException
			 */
			public String getIntTaxRate() throws SQLException {
				return theResults.getString(14);
			}

			/**
			 * Determine the DivTaxRate of the newly loaded item
			 * @return the DivTaxRate
			 * @throws SQLException
			 */
			public String getDivTaxRate() throws SQLException {
				return theResults.getString(15);
			}

			/**
			 * Determine the HiDivTaxRate of the newly loaded item
			 * @return the HiDivTaxRate
			 * @throws SQLException
			 */
			public String getHiDivTaxRate() throws SQLException {
				return theResults.getString(16);
			}

			/**
			 * Determine the AddTaxRate of the newly loaded item
			 * @return the AddTaxRate
			 * @throws SQLException
			 */
			public String getAddTaxRate() throws SQLException {
				return theResults.getString(17);
			}

			/**
			 * Determine the AddDivTaxRate of the newly loaded item
			 * @return the AddDivTaxRate
			 * @throws SQLException
			 */
			public String getAddDivTaxRate() throws SQLException {
				return theResults.getString(18);
			}

			/**
			 * Determine the AddIncBoundary of the newly loaded item
			 * @return the AddIncBoundary
			 * @throws SQLException
			 */
			public String getAddIncBoundary() throws SQLException {
				return theResults.getString(19);
			}

			/**
			 * Determine the CapitalAllowance of the newly loaded item
			 * @return the CapAllowance
			 * @throws SQLException
			 */
			public String getCapitalAllow() throws SQLException {
				return theResults.getString(20);
			}

			/**
			 * Determine the CapitalTAxRate of the newly loaded item
			 * @return the CapitalTaxRate
			 * @throws SQLException
			 */
			public String getCapTaxRate() throws SQLException {
				return theResults.getString(21);
			}

			/**
			 * Determine the HiCapitalTaxRate of the newly loaded item
			 * @return the HiCapTaxRate
			 * @throws SQLException
			 */
			public String getHiCapTaxRate() throws SQLException {
				return theResults.getString(22);
			}

			/**
			 * Determine the TaxRegime of the newly loaded item
			 * @return the TaxRegime
			 * @throws SQLException
			 */
			public long getTaxRegime() throws SQLException {
				return theResults.getLong(23);
			}

			/* insert the TaxParams */
			public void insertItems() throws SQLException {
				String myCmd = "insert into " + getTableName() + 
				               " (" + theIdCol + "," + theYearCol + "," +
				               theAllwCol + "," + theRentCol + "," +
				               theLoBdCol + "," + theBsBdCol + "," +
				               theLoAACol + "," + theHiAACol + "," + 
				               theAgLmCol + "," + theAdLmCol + "," + 
				               theLoTxCol + "," + theBsTxCol + "," + 
				               theHiTxCol + "," + theInTxCol + "," +
				               theDvTxCol + "," + theHDTxCol + "," +
				               theAdTxCol + "," + theADTxCol + "," +
				               theAdBdCol + "," + theCpAlCol + "," +
				               theCpTxCol + "," + theHCTxCol + "," +
				               theTxRgCol + ")" +
				               " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				theStmt = theConn.prepareStatement(myCmd);
			}
			
			/**
			 * Set the Year of the item to be inserted/updated
			 * @param pYear the Year of the item
			 * @throws SQLException
			 */
			public void setYear(finObject.Date pYear) throws SQLException {
				theStmt.setString(2, pYear.formatDate(false));
			}

			/**
			 * Set the Allowance of the item to be inserted/updated
			 * @param pAllow the Allowance of the item
			 * @throws SQLException
			 */
			public void setAllowance(finObject.Money pAllow) throws SQLException {
				theStmt.setString(3, pAllow.format(false));
			}

			/**
			 * Set the Rental Allowance of the item to be inserted/updated
			 * @param pAllow the RentalAllowance of the item
			 * @throws SQLException
			 */
			public void setRentalAllow(finObject.Money pAllow) throws SQLException {
				theStmt.setString(4, pAllow.format(false));
			}

			/**
			 * Set the LoTaxBand of the item to be inserted/updated
			 * @param pBand the LoTaxBand of the item
			 * @throws SQLException
			 */
			public void setLoTaxBand(finObject.Money pBand) throws SQLException {
				theStmt.setString(5, pBand.format(false));
			}

			/**
			 * Set the BasicTaxBand of the item to be inserted/updated
			 * @param pBand the BasicTaxBand of the item
			 * @throws SQLException
			 */
			public void setBasicTaxBand(finObject.Money pBand) throws SQLException {
				theStmt.setString(6, pBand.format(false));
			}

			/**
			 * Set the LoAgeAllow of the item to be inserted/updated
			 * @param pBand the loAgeAllow of the item
			 * @throws SQLException
			 */
			public void setLoAgeAllow(finObject.Money pBand) throws SQLException {
				theStmt.setString(7, pBand.format(false));
			}

			/**
			 * Set the HiAgeAllow of the item to be inserted/updated
			 * @param pBand the hiAgeAllow of the item
			 * @throws SQLException
			 */
			public void setHiAgeAllow(finObject.Money pBand) throws SQLException {
				theStmt.setString(8, pBand.format(false));
			}

			/**
			 * Set the AgeAllowLimit of the item to be inserted/updated
			 * @param pBand the ageAllowLimit of the item
			 * @throws SQLException
			 */
			public void setAgeAllowLimit(finObject.Money pBand) throws SQLException {
				theStmt.setString(9, pBand.format(false));
			}

			/**
			 * Set the AddAllowLimit of the item to be inserted/updated
			 * @param pBand the addAllowLimit of the item
			 * @throws SQLException
			 */
			public void setAddAllowLimit(finObject.Money pBand) throws SQLException {
				theStmt.setString(10, (pBand == null) ? null : pBand.format(false));
			}

			/**
			 * Set the LoTaxRate of the item to be inserted/updated
			 * @param pRate the LoTaxRate of the item
			 * @throws SQLException
			 */
			public void setLoTaxRate(finObject.Rate pRate) throws SQLException {
				theStmt.setString(11, pRate.format(false));
			}

			/**
			 * Set the BasicTaxRate of the item to be inserted/updated
			 * @param pRate the BasicTaxRate of the item
			 * @throws SQLException
			 */
			public void setBasicTaxRate(finObject.Rate pRate) throws SQLException {
				theStmt.setString(12, pRate.format(false));
			}

			/**
			 * Set the HiTaxRate of the item to be inserted/updated
			 * @param pRate the HiTaxRate of the item
			 * @throws SQLException
			 */
			public void setHiTaxRate(finObject.Rate pRate) throws SQLException {
				theStmt.setString(13, pRate.format(false));
			}

			/**
			 * Set the IntTaxRate of the item to be inserted/updated
			 * @param pRate the IntTaxRate of the item
			 * @throws SQLException
			 */
			public void setIntTaxRate(finObject.Rate pRate) throws SQLException {
				theStmt.setString(14, pRate.format(false));
			}

			/**
			 * Set the DivTaxRate of the item to be inserted/updated
			 * @param pRate the DivTaxRate of the item
			 * @throws SQLException
			 */
			public void setDivTaxRate(finObject.Rate pRate) throws SQLException {
				theStmt.setString(15, pRate.format(false));
			}

			/**
			 * Set the HiDivTaxRate of the item to be inserted/updated
			 * @param pRate the HiDivTaxRate of the item
			 * @throws SQLException
			 */
			public void setHiDivTaxRate(finObject.Rate pRate) throws SQLException {
				theStmt.setString(16, pRate.format(false));
			}

			/**
			 * Set the AddTaxRate of the item to be inserted/updated
			 * @param pRate the AddTaxRate of the item
			 * @throws SQLException
			 */
			public void setAddTaxRate(finObject.Rate pRate) throws SQLException {
				theStmt.setString(17, (pRate == null) ? null : pRate.format(false));
			}

			/**
			 * Set the AddDivTaxRate of the item to be inserted/updated
			 * @param pRate the AddDivTaxRate of the item
			 * @throws SQLException
			 */
			public void setAddDivTaxRate(finObject.Rate pRate) throws SQLException {
				theStmt.setString(18, (pRate == null) ? null : pRate.format(false));
			}

			/**
			 * Set the AddIncBoundary of the item to be inserted/updated
			 * @param pBand the AddIncBoundary of the item
			 * @throws SQLException
			 */
			public void setAddIncBoundary(finObject.Money pBand) throws SQLException {
				theStmt.setString(19, (pBand == null) ? null : pBand.format(false));
			}

			/**
			 * Set the CapitalAllowance of the item to be inserted/updated
			 * @param pBand the CapitalAllowance of the item
			 * @throws SQLException
			 */
			public void setCapitalAllow(finObject.Money pBand) throws SQLException {
				theStmt.setString(20, pBand.format(false));
			}

			/**
			 * Set the CapitalTaxRate of the item to be inserted/updated
			 * @param pRate the CapTaxRate of the item
			 * @throws SQLException
			 */
			public void setCapTaxRate(finObject.Rate pRate) throws SQLException {
				theStmt.setString(21, (pRate == null) ? null : pRate.format(false));
			}

			/**
			 * Set the HiCapitalTaxRate of the item to be inserted/updated
			 * @param pRate the HiCapTaxRate of the item
			 * @throws SQLException
			 */
			public void setHiCapTaxRate(finObject.Rate pRate) throws SQLException {
				theStmt.setString(22, (pRate == null) ? null : pRate.format(false));
			}

			/**
			 * Set the TaxRegime of the item to be inserted/updated
			 * @param id the id of the TaxRegime for the item
			 * @throws SQLException
			 */
			public void setTaxRegime(long id) throws SQLException {
				theStmt.setLong(23, id);
			}
			
			/* Delete the items */
			public void deleteItems() throws SQLException {
				String myCmd = "delete from " + getTableName() + 
				               " WHERE " + theIdCol + "=?";
				theStmt = theConn.prepareStatement(myCmd);
			}
			
			/**
			 * Update the allowance of the item
			 * @param pValue the new allowance
			 * @throws SQLException
			 */
			public void updateAllowance(finObject.Money pValue) {
				addUpdate(theAllwCol, pValue.format(false));
			}

			/**
			 * Update the rental allowance of the item
			 * @param pValue the new rental allowance
			 * @throws SQLException
			 */
			public void updateRentalAllow(finObject.Money pValue) {
				addUpdate(theRentCol, pValue.format(false));
			}

			/**
			 * Update the LoTaxBand of the item
			 * @param pValue the new Lo tax band
			 * @throws SQLException
			 */
			public void updateLoTaxBand(finObject.Money pValue) {
				addUpdate(theLoBdCol, pValue.format(false));
			}	

			/**
			 * Update the BasicTaxBand of the item
			 * @param pValue the new Basic tax band
			 * @throws SQLException
			 */
			public void updateBasicTaxBand(finObject.Money pValue) {
				addUpdate(theBsBdCol, pValue.format(false));
			}

			/**
			 * Update the LoAgeAllow of the item
			 * @param pValue the new Lo Age allowance
			 * @throws SQLException
			 */
			public void updateLoAgeAllow(finObject.Money pValue) {
				addUpdate(theLoAACol, pValue.format(false));
			}

			/**
			 * Update the HiAgeAllow of the item
			 * @param pValue the new Hi Age allowance
			 * @throws SQLException
			 */
			public void updateHiAgeAllow(finObject.Money pValue) {
				addUpdate(theHiAACol, pValue.format(false));
			}

			/**
			 * Update the AgeAllowLimit of the item
			 * @param pValue the new age allowance limit
			 * @throws SQLException
			 */
			public void updateAgeAllowLimit(finObject.Money pValue) {
				addUpdate(theAgLmCol, pValue.format(false));
			}

			/**
			 * Update the AddAllowLimit of the item
			 * @param pValue the new additional allowance limit
			 * @throws SQLException
			 */
			public void updateAddAllowLimit(finObject.Money pValue) {
				addUpdate(theAdLmCol, (pValue == null) ? null : pValue.format(false));
			}

			/**
			 * Update the LoTaxRate of the item
			 * @param pValue the new lo tax rate
			 * @throws SQLException
			 */
			public void updateLoTaxRate(finObject.Rate pValue) {
				addUpdate(theLoTxCol, pValue.format(false));
			}

			/**
			 * Update the BasicTaxRate of the item
			 * @param pValue the new Basic tax rate
			 * @throws SQLException
			 */
			public void updateBasicTaxRate(finObject.Rate pValue) {
				addUpdate(theBsTxCol, pValue.format(false));
			}

			/**
			 * Update the HiTaxRate of the item
			 * @param pValue the new high tax rate
			 * @throws SQLException
			 */
			public void updateHiTaxRate(finObject.Rate pValue) {
				addUpdate(theHiTxCol, pValue.format(false));
			}

			/**
			 * Update the IntTaxRate of the item
			 * @param pValue the new IntTaxRate
			 * @throws SQLException
			 */
			public void updateIntTaxRate(finObject.Rate pValue) {
				addUpdate(theInTxCol, pValue.format(false));
			}

			/**
			 * Update the DivTaxRate of the item
			 * @param pValue the new DivTaxRate
			 * @throws SQLException
			 */
			public void updateDivTaxRate(finObject.Rate pValue) {
				addUpdate(theDvTxCol, pValue.format(false));
			}

			/**
			 * Update the HiDivTaxRate of the item
			 * @param pValue the new HiDivTaxRate
			 * @throws SQLException
			 */
			public void updateHiDivTaxRate(finObject.Rate pValue) {
				addUpdate(theHDTxCol, pValue.format(false));
			}

			/**
			 * Update the AddTaxRate of the item
			 * @param pValue the new additional tax rate
			 * @throws SQLException
			 */
			public void updateAddTaxRate(finObject.Rate pValue) {
				addUpdate(theAdTxCol, (pValue == null) ? null : pValue.format(false));
			}

			/**
			 * Update the AddDivTaxRate of the item
			 * @param pValue the new additional dividend rate
			 * @throws SQLException
			 */
			public void updateAddDivTaxRate(finObject.Rate pValue) {
				addUpdate(theADTxCol, (pValue == null) ? null : pValue.format(false));
			}

			/**
			 * Update the AddIncBoundary of the item
			 * @param pValue the new additional income boundary
			 * @throws SQLException
			 */
			public void updateAddIncBoundary(finObject.Money pValue) {
				addUpdate(theADTxCol, (pValue == null) ? null : pValue.format(false));
			}

			/**
			 * Update the CapitalAllowance of the item
			 * @param pValue the new CapitalAllowance
			 * @throws SQLException
			 */
			public void updateCapitalAllow(finObject.Money pValue) {
				addUpdate(theCpAlCol, pValue.format(false));
			}

			/**
			 * Update the CapTaxRate of the item
			 * @param pValue the new CapTaxRate
			 * @throws SQLException
			 */
			public void updateCapTaxRate(finObject.Rate pValue) {
				addUpdate(theCpTxCol, (pValue == null) ? null : pValue.format(false));
			}

			/**
			 * Update the HiCapTaxRate of the item
			 * @param pValue the new HiCapTaxRate
			 * @throws SQLException
			 */
			public void updateHiCapTaxRate(finObject.Rate pValue) {
				addUpdate(theHCTxCol, (pValue == null) ? null : pValue.format(false));
			}

			/**
			 * Update the TaxRegime of the item
			 * @param uId the id of the tax regime
			 * @throws SQLException
			 */
			public void updateTaxRegime(long uId) {
				addUpdate(theTxRgCol, uId);
			}
		}
		
		/**
		 *  Inner class to handle Accounts table access
		 */
		public class Accounts extends TableModel {
			/**
			 * The name of the Account column
			 */
			private final static String theActCol    = "Account";

			/**
			 * The name of the Account Type column
			 */
			private final static String theActTypCol = "AccountType";

			/**
			 * The name of the Description column
			 */
			private final static String theDescCol   = "Description";

			/**
			 * The name of the Maturity column
			 */
			private final static String theMatureCol = "Maturity";

			/**
			 * The name of the Closed column
			 */
			private final static String theCloseCol  = "Closed";

			/**
			 * The name of the Parent column
			 */
			private final static String theParentCol = "Parent";
			
			/**
			 * Constructor
			 */
			private Accounts() { super("Accounts"); };
			
			/* Load the accounts */
			public void loadItems() throws SQLException {
				String myQuery = "select " + theIdCol + "," + theActCol + "," + 
				                 theActTypCol + "," + theDescCol + "," +
				                 theMatureCol + "," + theCloseCol + "," +
				                 theParentCol +
				                 " from " + getTableName();			
				theStmt = theConn.prepareStatement(myQuery);
				executeQuery();
			}
			
			/**
			 * Determine the Name of the newly loaded item
			 * @return the Name
			 * @throws SQLException
			 */
			public String getName() throws SQLException {
				return theResults.getString(2);
			}

			/**
			 * Determine the Account Type of the newly loaded item
			 * @return the AccountType
			 * @throws SQLException
			 */
			public long getAccountType() throws SQLException {
				return theResults.getLong(3);
			}

			/**
			 * Determine the Description of the newly loaded item
			 * @return the Description
			 * @throws SQLException
			 */
			public String getDescription() throws SQLException {
				return theResults.getString(4);
			}

			/**
			 * Determine the Maturity of the newly loaded item
			 * @return the Maturity
			 * @throws SQLException
			 */
			public Date getMaturity() throws SQLException {
				return theResults.getDate(5);
			}

			/**
			 * Determine the Closed Date of the newly loaded item
			 * @return the ClosedDate
			 * @throws SQLException
			 */
			public Date getClosed() throws SQLException {
				return theResults.getDate(6);
			}

			/**
			 * Determine the Parent of the newly loaded item
			 * @return the Parent
			 * @throws SQLException
			 */
			public long getParent() throws SQLException {
				return theResults.getLong(7);
			}

			/* Insert the items */
			public void insertItems() throws SQLException {
				String myCmd = "insert into " + getTableName() + 
				               " (" + theIdCol + "," + theActCol + "," +
				                 theActTypCol + "," + theDescCol + "," +
				                 theMatureCol + "," + theCloseCol + "," +
				                 theParentCol + ")" + 
				               " VALUES(?,?,?,?,?,?,?)";
				theStmt = theConn.prepareStatement(myCmd);
			}
			
			/**
			 * Set the Name of the item to be inserted/updated
			 * @param pAccount the name of the item
			 * @throws SQLException
			 */
			public void setName(String pAccount) throws SQLException {
				theStmt.setString(2, pAccount);
			}

			/**
			 * Set the AccountType of the item to be inserted/updated
			 * @param pActType the Account type of the item
			 * @throws SQLException
			 */
			public void setAccountType(long pActType) throws SQLException {
				theStmt.setLong(3, pActType);
			}

			/**
			 * Set the Description of the item to be inserted/updated
			 * @param pDesc the description of the item
			 * @throws SQLException
			 */
			public void setDescription(String pDesc) throws SQLException {
				theStmt.setString(4, (pDesc == null) ? null 
						                             : pDesc);
			}

			/**
			 * Set the Maturity of the item to be inserted/updated
			 * @param pMaturity the maturity of the item
			 * @throws SQLException
			 */
			public void setMaturity(finObject.Date pMaturity) throws SQLException {
				theStmt.setString(5, (pMaturity == null) ? null
						                                 : pMaturity.formatDate(false));
			}

			/**
			 * Set the Closed Date of the item to be inserted/updated
			 * @param pClosed the Close Date of the item
			 * @throws SQLException
			 */
			public void setClosed(finObject.Date pClosed) throws SQLException {
				theStmt.setString(6, (pClosed == null) ? null 
						                               : pClosed.formatDate(false));
			}

			/**
			 * Set the Parent of the item to be inserted/updated
			 * @param pParent the id of the TaxRegime for the item
			 * @throws SQLException
			 */
			public void setParent(long pParent) throws SQLException {
				theStmt.setLong(7, pParent);
			}
			
			/* Delete the accounts */
			public void deleteItems() throws SQLException {
				String myCmd = "delete from " + getTableName() + 
				               " WHERE " + theIdCol + "=?";
				close(); 
				theStmt = theConn.prepareStatement(myCmd);
			}
			
			/**
			 * Update the Name of the item
			 * @param pValue the new name
			 * @throws SQLException
			 */
			public void updateName(String pValue) {
				addUpdate(theActCol, pValue);
			}		

			/**
			 * Update the Description of the item
			 * @param pValue the new description
			 * @throws SQLException
			 */
			public void updateDescription(String pValue) {
				addUpdate(theDescCol, pValue);
			}	

			/**
			 * Update the Maturity of the item
			 * @param pValue the new maturity
			 * @throws SQLException
			 */
			public void updateMaturity(finObject.Date pValue) {
				addUpdate(theMatureCol, (pValue == null) ? null
						                                 : pValue.formatDate(true));
			}

			/**
			 * Update the Closed Date of the item
			 * @param pValue the new closed date
			 * @throws SQLException
			 */
			public void updateClosed(finObject.Date pValue) {
				addUpdate(theCloseCol, (pValue == null) ? null
						                                : pValue.formatDate(true));
			}

			/**
			 * Update the Parent of the item
			 * @param pValue the new parent
			 * @throws SQLException
			 */
			public void updateParent(long pValue) {
				addUpdate(theParentCol, pValue);
			}
		}
		
		/**
		 *  Inner class to handle Rates table access
		 */
		public class Rates extends TableModel {
			/**
			 * The name of the Account column
			 */
			private final static String theActCol    = "Account";

			/**
			 * The name of the Rate column
			 */
			private final static String theRateCol   = "Rate";

			/**
			 * The name of the Bonus column
			 */
			private final static String theBonusCol  = "Bonus";

			/**
			 * The name of the EndDate column
			 */
			private final static String theDateCol   = "EndDate";
			
			/**
			 * Constructor
			 */
			private Rates() { super("Rates"); };
			
			/* Load the rates */
			public void loadItems() throws SQLException {
				String myQuery = "select " + theIdCol + "," + theActCol + "," + 
				                 theRateCol + "," + theBonusCol + "," +
				                 theDateCol + 
				                 " from " + getTableName() + 
				                 " order by " + theActCol + "," + theDateCol;			
				theStmt = theConn.prepareStatement(myQuery);
				executeQuery();
			}
			
			/**
			 * Determine the Account of the newly loaded item
			 * @return the Account
			 * @throws SQLException
			 */
			public long getAccount() throws SQLException {
				return theResults.getLong(2);
			}

			/**
			 * Determine the Rate of the newly loaded item
			 * @return the Rate
			 * @throws SQLException
			 */
			public String getRate() throws SQLException {
				return theResults.getString(3);
			}

			/**
			 * Determine the Bonus of the newly loaded item
			 * @return the Bonus
			 * @throws SQLException
			 */
			public String getBonus() throws SQLException {
				return theResults.getString(4);
			}

			/**
			 * Determine the EndDate of the newly loaded item
			 * @return the EndDate
			 * @throws SQLException
			 */
			public Date getEndDate() throws SQLException {
				return theResults.getDate(5);
			}
			
			/* Insert the rates */
			public void insertItems() throws SQLException {
				String myCmd = "insert into " + getTableName() + 
				               " (" + theIdCol + "," + theActCol + "," +
				                 theRateCol + "," + theBonusCol + "," +
				                 theDateCol + ")" + 
				               " VALUES(?,?,?,?,?)";
				theStmt = theConn.prepareStatement(myCmd);
			}
			
			/**
			 * Set the Account of the item to be inserted/updated
			 * @param pAccount the account of the item
			 * @throws SQLException
			 */
			public void setAccount(long pAccount) throws SQLException {
				theStmt.setLong(2, pAccount);
			}
			
			/**
			 * Set the Rate of the item to be inserted/updated
			 * @param pRate the rate of the item
			 * @throws SQLException
			 */
			public void setRate(finObject.Rate pRate) throws SQLException {
				theStmt.setString(3, pRate.format(false));
			}

			/**
			 * Set the Bonus of the item to be inserted/updated
			 * @param pBonus the bonus of the item
			 * @throws SQLException
			 */
			public void setBonus(finObject.Rate pBonus) throws SQLException {
				theStmt.setString(4, (pBonus == null) ? null 
						                              : pBonus.format(false));
			}

			/**
			 * Set the EndDate of the item to be inserted/updated
			 * @param pEndDate the description of the item
			 * @throws SQLException
			 */
			public void setEndDate(finObject.Date pEndDate) throws SQLException {
				theStmt.setString(5, (pEndDate == null) ? null 
						                                : pEndDate.formatDate(true));
			}
			
			/* Delete the rates */
			public void deleteItems() throws SQLException {
				String myCmd = "delete from " + getTableName() + 
				               " WHERE " + theIdCol + "=?";
				theStmt = theConn.prepareStatement(myCmd);
			}

			/**
			 * Update the Rate of the item
			 * @param pValue the new rate
			 * @throws SQLException
			 */
			public void updateRate(finObject.Rate pValue) {
				addUpdate(theRateCol, pValue.format(false));
			}	

			/**
			 * Update the Bonus of the item
			 * @param pValue the new bonus
			 * @throws SQLException
			 */
			public void updateBonus(finObject.Rate pValue) {
				addUpdate(theBonusCol, (pValue == null) ? null
						                                : pValue.format(false));
			}

			/**
			 * Update the EndDate of the item
			 * @param pValue the new EndDate
			 * @throws SQLException
			 */
			public void updateEndDate(finObject.Date pValue) {
				addUpdate(theDateCol, (pValue == null) ? null
						                               : pValue.formatDate(true));
			}
		}
		
		/**
		 *  Inner class to handle Prices table access
		 */
		public class Prices extends TableModel {
			/**
			 * The name of the Account column
			 */
			private final static String theActCol    = "Account";

			/**
			 * The name of the Date column
			 */
			private final static String theDateCol   = "Date";

			/**
			 * The name of the Price column
			 */
			private final static String thePriceCol  = "Price";
			
			/**
			 * Constructor
			 */
			private Prices() { super("Prices"); };
			
			/* Load the prices */
			public void loadItems() throws SQLException {
				String myQuery = "select " + theIdCol + "," + theActCol + "," + 
				                 theDateCol + "," + thePriceCol + 
				                 " from " + getTableName() +			
								 " order by " + theActCol + "," + theDateCol;			
				theStmt = theConn.prepareStatement(myQuery);
				executeQuery();
			}
			
			/**
			 * Determine the Account of the newly loaded item
			 * @return the Account
			 * @throws SQLException
			 */
			public long getAccount() throws SQLException {
				return theResults.getLong(2);
			}

			/**
			 * Determine the Date of the newly loaded item
			 * @return the Date
			 * @throws SQLException
			 */
			public Date getDate() throws SQLException {
				return theResults.getDate(3);
			}

			/**
			 * Determine the Price of the newly loaded item
			 * @return the Price
			 * @throws SQLException
			 */
			public String getPrice() throws SQLException {
				return theResults.getString(4);
			}
			
			/* Insert the prices */
			public void insertItems() throws SQLException {
				String myCmd = "insert into " + getTableName() + 
				               " (" + theIdCol + "," + theActCol + "," +
				                 theDateCol + "," + thePriceCol + ")" + 
				               " VALUES(?,?,?,?)";
				theStmt = theConn.prepareStatement(myCmd);
			}
			
			/**
			 * Set the Account of the item to be inserted/updated
			 * @param pAccount the account of the item
			 * @throws SQLException
			 */
			public void setAccount(long pAccount) throws SQLException {
				theStmt.setLong(2, pAccount);
			}

			/**
			 * Set the Date of the item to be inserted/updated
			 * @param pDate the date of the item
			 * @throws SQLException
			 */
			public void setDate(finObject.Date pDate) throws SQLException {
				theStmt.setString(3, pDate.formatDate(false));
			}

			/**
			 * Set the Price of the item to be inserted/updated
			 * @param pPrice the price of the item
			 * @throws SQLException
			 */
			public void setPrice(finObject.Price pPrice) throws SQLException {
				theStmt.setString(4, pPrice.format(false));
			}
			
			/* Delete the prices */
			public void deleteItems() throws SQLException {
				String myCmd = "delete from " + getTableName() + 
				               " WHERE " + theIdCol + "=?";
				theStmt = theConn.prepareStatement(myCmd);
			}

			/**
			 * Update the Date of the item
			 * @param pValue the new date
			 * @throws SQLException
			 */
			public void updateDate(finObject.Date pValue) {
				addUpdate(theDateCol, pValue.formatDate(false));
			}

			/**
			 * Update the Price of the item
			 * @param pValue the new price
			 * @throws SQLException
			 */
			public void updatePrice(finObject.Price pValue) {
				addUpdate(thePriceCol, pValue.format(false));
			}
		}
		
		/**
		 *  Inner class to handle Patterns table access
		 */
		public class Patterns extends TableModel {
			/**
			 * The name of the Date column
			 */
			private final static String theDateCol   = "Date";

			/**
			 * The name of the Description column
			 */
			private final static String theDescCol   = "Description";

			/**
			 * The name of the Amount column
			 */
			private final static String theAmntCol   = "Amount";

			/**
			 * The name of the Account column
			 */
			private final static String theAcctCol   = "Account";

			/**
			 * The name of the Partner Account column
			 */
			private final static String thePartCol   = "Partner";

			/**
			 * The name of the TransType column
			 */
			private final static String theTrnTypCol = "TransactionType";

			/**
			 * The name of the isCredit flag column
			 */
			private final static String theIsCrtCol  = "isCredit";

			/**
			 * The name of the Frequency column
			 */
			private final static String theFreqCol   = "Frequency";
			
			/**
			 * Constructor
			 */
			private Patterns() { super("Patterns"); };
			
			/* load the patterns */
			public void loadItems() throws SQLException {
				String myQuery = "select " + theIdCol + "," + theDateCol + "," + 
				                 theDescCol + "," + theAmntCol + "," + 
				                 theAcctCol + "," + thePartCol +  "," +
				                 theTrnTypCol + "," + theIsCrtCol + "," +
				                 theFreqCol + " from " + getTableName() +			
                				 " order by " + theAcctCol + "," + theDateCol;			
				theStmt = theConn.prepareStatement(myQuery);
				executeQuery();
			}
			
			/**
			 * Determine the Date of the newly loaded item
			 * @return the Date
			 * @throws SQLException
			 */
			public Date getDate() throws SQLException {
				return theResults.getDate(2);
			}

			/**
			 * Determine the Description of the newly loaded item
			 * @return the Description
			 * @throws SQLException
			 */
			public String getDesc() throws SQLException {
				return theResults.getString(3);
			}

			/**
			 * Determine the Amount of the newly loaded item
			 * @return the Amount
			 * @throws SQLException
			 */
			public String getAmount() throws SQLException {
				return theResults.getString(4);
			}

			/**
			 * Determine the Account of the newly loaded item
			 * @return the Account
			 * @throws SQLException
			 */
			public long getAccount() throws SQLException {
				return theResults.getLong(5);
			}

			/**
			 * Determine the Partner Account of the newly loaded item
			 * @return the Partner account
			 * @throws SQLException
			 */
			public long getPartner() throws SQLException {
				return theResults.getLong(6);
			}

			/**
			 * Determine the TransType of the newly loaded item
			 * @return the TransType
			 * @throws SQLException
			 */
			public long getTransType() throws SQLException {
				return theResults.getLong(7);
			}

			/**
			 * Determine the isCredit flag of the newly loaded item
			 * @return the isCredit Flag
			 * @throws SQLException
			 */
			public boolean getIsCredit() throws SQLException {
				return theResults.getBoolean(8);
			}

			/**
			 * Determine the Frequency of the newly loaded item
			 * @return the Frequency
			 * @throws SQLException
			 */
			public long getFrequency() throws SQLException {
				return theResults.getLong(9);
			}		
			
			/* Insert the patterns */
			public void insertItems() throws SQLException {
				String myCmd = "insert into " + getTableName() + 
				               " (" + theIdCol + "," + theDateCol + "," +
				                 theDescCol + "," + theAmntCol + "," + 
				                 theAcctCol + "," + thePartCol +  "," +
				                 theTrnTypCol + "," + theIsCrtCol + "," +
				                 theFreqCol + ") VALUES(?,?,?,?,?,?,?,?,?)";
				theStmt = theConn.prepareStatement(myCmd);
			}
			
			/**
			 * Set the Date of the item to be inserted/updated
			 * @param pDate the date of the item
			 * @throws SQLException
			 */
			public void setDate(finObject.Date pDate) throws SQLException {
				theStmt.setString(2, pDate.formatDate(false));
			}

			/**
			 * Set the Description of the item to be inserted/updated
			 * @param pDesc the description of the item
			 * @throws SQLException
			 */
			public void setDesc(String pDesc) throws SQLException {
				theStmt.setString(3, pDesc);
			}

			/**
			 * Set the Amount of the item to be inserted/updated
			 * @param pAmount the amount of the item
			 * @throws SQLException
			 */
			public void setAmount(finObject.Money pAmount) throws SQLException {
				theStmt.setString(4, pAmount.format(false));
			}

			/**
			 * Set the Account of the item to be inserted/updated
			 * @param pAccount the account of the item
			 * @throws SQLException
			 */
			public void setAccount(long pAccount) throws SQLException {
				theStmt.setLong(5, pAccount);
			}
			/**
			 * Set the Partner Account of the item to be inserted/updated
			 * @param pPartner the partner account of the item
			 * @throws SQLException
			 */
			public void setPartner(long pPartner) throws SQLException {
				theStmt.setLong(6, pPartner);
			}

			/**
			 * Set the TransType of the item to be inserted/updated
			 * @param pTransType the transtype of the item
			 * @throws SQLException
			 */
			public void setTransType(long pTransType) throws SQLException {
				theStmt.setLong(7, pTransType);
			}

			/**
			 * Set the IsCredit flag of the item to be inserted/updated
			 * @param isCredit the isCredit flag of the item
			 * @throws SQLException
			 */
			public void setIsCredit(boolean isCredit) throws SQLException {
				theStmt.setBoolean(8, isCredit);
			}

			/**
			 * Set the Frequency of the item to be inserted/updated
			 * @param pFrequency the frequency of the item
			 * @throws SQLException
			 */
			public void setFrequency(long pFrequency) throws SQLException {
				theStmt.setLong(9, pFrequency);
			}
			
			/* Delete the patterns */
			public void deleteItems() throws SQLException {
				String myCmd = "delete from " + getTableName() + 
				               " WHERE " + theIdCol + "=?";
				theStmt = theConn.prepareStatement(myCmd);
			}
			
			/**
			 * Update the Date of the item
			 * @param pValue the new date
			 * @throws SQLException
			 */
			public void updateDate(finObject.Date pValue) {
				addUpdate(theDateCol, pValue.formatDate(false));
			}		

			/**
			 * Update the Description of the item
			 * @param pValue the new description
			 * @throws SQLException
			 */
			public void updateDescription(String pValue) {
				addUpdate(theDescCol, pValue);
			}

			/**
			 * Update the Amount of the item
			 * @param pValue the new amount
			 * @throws SQLException
			 */
			public void updateAmount(finObject.Money pValue) {
				addUpdate(theAmntCol, pValue.format(false));
			}	

			/**
			 * Update the Partner Account of the item
			 * @param pValue the new partner account
			 * @throws SQLException
			 */
			public void updatePartner(long pValue) {
				addUpdate(thePartCol, pValue);
			}

			/**
			 * Update the TransType of the item
			 * @param pValue the new transtype
			 * @throws SQLException
			 */
			public void updateTransType(long pValue) {
				addUpdate(theTrnTypCol, pValue);
			}

			/**
			 * Update the Frequency of the item
			 * @param pValue the new frequency
			 * @throws SQLException
			 */
			public void updateFrequency(long pValue) {
				addUpdate(theFreqCol, pValue);
			}
		}
		
		/* Inner class to handle Events table access */
		public class Events extends TableModel {
			/**
			 * The name of the Date column
			 */
			private final static String theDateCol   = "Date";

			/**
			 * The name of the Description column
			 */
			private final static String theDescCol   = "Description";

			/**
			 * The name of the Amount column
			 */
			private final static String theAmntCol   = "Amount";

			/**
			 * The name of the Credit Account column
			 */
			private final static String theCrtCol    = "Credit";

			/**
			 * The name of the Debit Account column
			 */
			private final static String theDbtCol    = "Debit";

			/**
			 * The name of the Units column
			 */
			private final static String theUnitCol   = "Units";

			/**
			 * The name of the TransType column
			 */
			private final static String theTrnTypCol = "TransactionType";

			/**
			 * The name of the Tax Credit column
			 */
			private final static String theTxCrdtCol = "TaxCredit";

			/**
			 * The name of the Years column
			 */
			private final static String theYearsCol  = "Years";

			/**
			 * Constructor
			 */
			private Events() { super("Events"); };
			
			/* Load the events */
			public void loadItems() throws SQLException {
				String myQuery = "select " + theIdCol + "," + theDateCol + "," + 
				                 theDescCol + "," + theAmntCol + "," + 
				                 theCrtCol + "," + theDbtCol +  "," +
				                 theUnitCol + "," + theTrnTypCol + "," +
				                 theTxCrdtCol + "," + theYearsCol + 
				                 " from " + getTableName() + 
				                 " order by " + theDateCol + "," + theDescCol;			
				theStmt = theConn.prepareStatement(myQuery);
				executeQuery();
			}
			
			/**
			 * Determine the Date of the newly loaded item
			 * @return the Date
			 * @throws SQLException
			 */
			public Date getDate() throws SQLException {
				return theResults.getDate(2);
			}

			/**
			 * Determine the Description of the newly loaded item
			 * @return the Description
			 * @throws SQLException
			 */
			public String getDesc() throws SQLException {
				return theResults.getString(3);
			}

			/**
			 * Determine the Amount of the newly loaded item
			 * @return the Amount
			 * @throws SQLException
			 */
			public String getAmount() throws SQLException {
				return theResults.getString(4);
			}

			/**
			 * Determine the Credit Account of the newly loaded item
			 * @return the Credit account
			 * @throws SQLException
			 */
			public long getCredit() throws SQLException {
				return theResults.getLong(5);
			}

			/**
			 * Determine the Debit Account of the newly loaded item
			 * @return the Debit account
			 * @throws SQLException
			 */
			public long getDebit() throws SQLException {
				return theResults.getLong(6);
			}

			/**
			 * Determine the Units of the newly loaded item
			 * @return the Units
			 * @throws SQLException
			 */
			public String getUnits() throws SQLException {
				return theResults.getString(7);
			}

			/**
			 * Determine the Transaction Type of the newly loaded item
			 * @return the Transaction Type
			 * @throws SQLException
			 */
			public long getTransType() throws SQLException {
				return theResults.getLong(8);
			}

			/**
			 * Determine the Tax Credit of the newly loaded item
			 * @return the Tax Credit
			 * @throws SQLException
			 */
			public String getTaxCredit() throws SQLException {
				return theResults.getString(9);
			}

			/**
			 * Determine the Tax Relief Years of the newly loaded item
			 * @return the Tax Relief Years
			 * @throws SQLException
			 */
			public int getYears() throws SQLException {
				return theResults.getInt(10);
			}

			/* insert the events */
			public void insertItems() throws SQLException {
				String myCmd = "insert into " + getTableName() + 
				               " (" + theIdCol + "," + theDateCol + "," +
				                 theDescCol + "," + theAmntCol + "," + 
				                 theCrtCol + "," + theDbtCol +  "," +
				                 theUnitCol + "," + theTrnTypCol + "," +
				                 theTxCrdtCol + "," + theYearsCol + ")" +
				               " VALUES(?,?,?,?,?,?,?,?,?,?)";
				theStmt = theConn.prepareStatement(myCmd);
			}
			
			/**
			 * Set the Date of the item to be inserted/updated
			 * @param pDate the date of the item
			 * @throws SQLException
			 */
			public void setDate(finObject.Date pDate) throws SQLException {
				theStmt.setString(2, pDate.formatDate(false));
			}

			/**
			 * Set the Description of the item to be inserted/updated
			 * @param pDesc the description of the item
			 * @throws SQLException
			 */
			public void setDesc(String pDesc) throws SQLException {
				theStmt.setString(3, pDesc);
			}

			/**
			 * Set the Amount of the item to be inserted/updated
			 * @param pAmount the amount of the item
			 * @throws SQLException
			 */
			public void setAmount(finObject.Money pAmount) throws SQLException {
				theStmt.setString(4, pAmount.format(false));
			}

			/**
			 * Set the Credit Account of the item to be inserted/updated
			 * @param pCredit the credit account of the item
			 * @throws SQLException
			 */
			public void setCredit(long pCredit) throws SQLException {
				theStmt.setLong(5, pCredit);
			}

			/**
			 * Set the Debit Account of the item to be inserted/updated
			 * @param pDebit the debit account of the item
			 * @throws SQLException
			 */
			public void setDebit(long pDebit) throws SQLException {
				theStmt.setLong(6, pDebit);
			}

			/**
			 * Set the Units of the item to be inserted/updated
			 * @param pUnits the units of the item
			 * @throws SQLException
			 */
			public void setUnits(finObject.Units pUnits) throws SQLException {
				theStmt.setString(7, (pUnits == null) ? null
						                              : pUnits.format(false));
			}

			/**
			 * Set the TransType of the item to be inserted/updated
			 * @param pTransType the transtype of the item
			 * @throws SQLException
			 */
			public void setTransType(long pTransType) throws SQLException {
				theStmt.setLong(8, pTransType);
			}
			
			/**
			 * Set the Tax Credit of the item to be inserted/updated
			 * @param pCredit the Tax Credit of the item
			 * @throws SQLException
			 */
			public void setTaxCredit(finObject.Money pCredit) throws SQLException {
				theStmt.setString(9, (pCredit == null) ? null
						                               : pCredit.format(false));
			}

			/**
			 * Set the Years of the item to be inserted/updated
			 * @param pYears the Tax Relief Years of the item
			 * @throws SQLException
			 */
			public void setYears(int pYears) throws SQLException {
				theStmt.setInt(10, pYears);
			}

			/* Delete the events */
			public void deleteItems() throws SQLException {
				String myCmd = "delete from " + getTableName() + 
				               " WHERE " + theIdCol + "=?";
				theStmt = theConn.prepareStatement(myCmd);
			}
			
			/**
			 * Update the Date of the item
			 * @param pValue the new transtype
			 * @throws SQLException
			 */
			public void updateDate(finObject.Date pValue) {
				addUpdate(theDateCol, pValue.formatDate(true));
			}		

			/**
			 * Update the Description of the item
			 * @param pValue the new description
			 * @throws SQLException
			 */
			public void updateDescription(String pValue) {
				addUpdate(theDescCol, pValue);
			}

			/**
			 * Update the Amount of the item
			 * @param pValue the new amount
			 * @throws SQLException
			 */
			public void updateAmount(finObject.Money pValue) {
				addUpdate(theAmntCol, pValue.format(false));
			}	

			/**
			 * Update the Credit account of the item
			 * @param pValue the new credit account
			 * @throws SQLException
			 */
			public void updateCredit(long pValue) {
				addUpdate(theCrtCol, pValue);
			}

			/**
			 * Update the Debit account of the item
			 * @param pValue the new debit account
			 * @throws SQLException
			 */
			public void updateDebit(long pValue) {
				addUpdate(theDbtCol, pValue);
			}

			/**
			 * Update the TransType of the item
			 * @param pValue the new transtype
			 * @throws SQLException
			 */
			public void updateTransType(long pValue) {
				addUpdate(theTrnTypCol, pValue);
			}

			/**
			 * Update the Units of the item
			 * @param pValue the new units
			 * @throws SQLException
			 */
			public void updateUnits(finObject.Units pValue) {
				addUpdate(theUnitCol, (pValue == null) ? null
						                               : pValue.format(false));
			}

			/**
			 * Update the Tax Credit of the item
			 * @param pValue the new tax credit
			 * @throws SQLException
			 */
			public void updateTaxCredit(finObject.Money pValue) {
				addUpdate(theTxCrdtCol, (pValue == null) ? null
						                                 : pValue.format(false));
			}

			/**
			 * Update the Years of the item
			 * @param pValue the new years
			 * @throws SQLException
			 */
			public void updateYears(Integer pValue) {
				addUpdate(theYearsCol, (pValue == null) ? null : pValue.intValue());
			}
		}
	}
	
	/**
	 * Provides batch classes for accessing the database
	 */
	public static class Batch {
		private finData      theData         = null;
		private finStatic    theStatic       = null;
		private Table    	 theTables       = null;
		private AccountTypes theAccountTypes = null;
		private TransTypes   theTransTypes   = null;
		private TaxRegimes   theTaxRegimes   = null;
		private TaxTypes     theTaxTypes     = null;
		private Frequencys   theFrequencys   = null;
		private TaxParams    theTaxParams    = null;
		private Accounts     theAccounts     = null;
		private Rates     	 theRates        = null;
		private Prices     	 thePrices       = null;
		private Patterns   	 thePatterns     = null;
		private Events       theEvents       = null;
		private finBuilder   theBuilder      = null;
		private int          theBatchSize    = 0;		
		private statusCtl    theThread  	 = null; 
		
		/**
		 * Access the DataSet
		 * @return the data
		 */
		public finData       getData()       { return theData; }
		
		/**
		 * Constructor for loading data from tables
		 * @param pThread the Thread control
		 * @throws finObject.Exception
		 */
		public Batch(statusCtl pThread) 
								throws finObject.Exception {
			theBuilder      = new finBuilder();
			theData         = theBuilder.getData();
			theTables       = new Table(pThread.getProperties());
			theAccountTypes = new AccountTypes();
			theTransTypes   = new TransTypes();
			theTaxTypes     = new TaxTypes();
			theTaxRegimes   = new TaxRegimes();
			theFrequencys   = new Frequencys();
			theTaxParams    = new TaxParams();
			theAccounts     = new Accounts();
			theRates        = new Rates();
			thePrices       = new Prices();
			thePatterns     = new Patterns();
			theEvents       = new Events();
			theThread       = pThread;
		}

		/**
		 * Constructor for updating tables with changes
		 * @param pData the data to update from
		 * @param pThread the Thread control
		 * @throws finObject.Exception
		 */
		public Batch(finData   pData, 
					 statusCtl pThread) 
								throws finObject.Exception {
			theData         = pData;
			theStatic       = pData.getStatic();
			theTables       = new Table(pThread.getProperties());
			theAccountTypes = new AccountTypes(pData.getActTypes());
			theTransTypes   = new TransTypes(pData.getTransTypes());
			theTaxTypes     = new TaxTypes(pData.getTaxTypes());
			theTaxRegimes   = new TaxRegimes(pData.getTaxRegimes());
			theFrequencys   = new Frequencys(pData.getFrequencys());
			theTaxParams    = new TaxParams(pData.getTaxYears());
			theAccounts     = new Accounts(pData.getAccounts());
			theRates    	= new Rates(pData.getRates());
			thePrices    	= new Prices(pData.getPrices());
			thePatterns   	= new Patterns(pData.getPatterns());
			theEvents       = new Events(pData.getEvents());
			theBatchSize    = theTables.getBatchSize();
			theThread       = pThread;
		}
		
		/**
		 * Load data from the tables
		 * @throws finObject.Exception
		 */
		public finData loadDatabase() throws finObject.Exception {
			boolean bContinue;
			
			/* Set the number of stages */
			if (!theThread.setNumStages(13)) return null;
			
			/* Load the tables */
			bContinue = theAccountTypes.load();
			if (bContinue) bContinue = theTransTypes.load();
			if (bContinue) bContinue = theTaxTypes.load();
			if (bContinue) bContinue = theTaxRegimes.load();
			if (bContinue) bContinue = theFrequencys.load();
			if (bContinue) bContinue = theTaxParams.load();
			if (bContinue) getData().calculateDateRange();
			if (bContinue) bContinue = theAccounts.load();
			if (bContinue) bContinue = theRates.load();
			if (bContinue) bContinue = thePrices.load();
			if (bContinue) bContinue = thePatterns.load();
			if (bContinue) theBuilder.validateAccounts();
			if (bContinue) bContinue = theEvents.load();
			
			/* analyse the data */
			if (bContinue) bContinue = theThread.setNewStage("Analysing data");
			if (bContinue) getData().analyseData();
			if (bContinue) bContinue = theThread.setNewStage("Refreshing data");
						
			/* Check for cancellation */
			if (!bContinue) 
				throw new finObject.Exception(ExceptionClass.SQLSERVER,
											  "Operation Cancelled");
			
			/* Return the data */
			return (bContinue) ? getData() : null;
		}
		
		/**
		 * Update the tables with changes in the data
		 * @throws finObject.Exception
		 */
		public void applyChanges() throws finObject.Exception {
			boolean bContinue;
			
			/* Set the number of stages */
			if (!theThread.setNumStages(23)) return;
			
			/* Perform all changes */
			bContinue = insert();
			if (bContinue) bContinue = update();
			if (bContinue) bContinue = delete();
			
			/* Check for cancellation */
			if (!bContinue) 
				throw new finObject.Exception(ExceptionClass.SQLSERVER,
											  "Operation Cancelled");
		}
		
		/**
		 * Insert new rows into the tables
		 * @throws finObject.Exception
		 */
		private boolean insert() throws finObject.Exception {
			boolean bContinue = true;
			
			/* Load the new entries */
			bContinue = theAccountTypes.insert();
			if (bContinue) bContinue = theTransTypes.insert();
			if (bContinue) bContinue = theTaxTypes.insert();
			if (bContinue) bContinue = theTaxRegimes.insert();
			if (bContinue) bContinue = theFrequencys.insert();
			if (bContinue) bContinue = theTaxParams.insert();
			if (bContinue) bContinue = theAccounts.insert();
			if (bContinue) bContinue = theRates.insert();
			if (bContinue) bContinue = thePrices.insert();
			if (bContinue) bContinue = thePatterns.insert();
			if (bContinue) bContinue = theEvents.insert();
			
			/* Return to caller */
			return bContinue;
		}
		
		/**
		 * Update changed rows in the tables
		 * @throws finObject.Exception
		 */
		private boolean update() throws finObject.Exception {
			boolean bContinue;
			
			/* Update the changed entries */
			bContinue = theTaxParams.update();
			if (bContinue) bContinue = theAccounts.update();
			if (bContinue) bContinue = theRates.update();
			if (bContinue) bContinue = thePrices.update();
			if (bContinue) bContinue = thePatterns.update();
			if (bContinue) bContinue = theEvents.update();
			
			/* Return to caller */
			return bContinue;
		}
		
		/**
		 * Delete rows from the tables
		 * @throws finObject.Exception
		 */
		private boolean delete() throws finObject.Exception {
			boolean bContinue;
			
			/* Delete the old entries */
			bContinue = theEvents.delete();
			if (bContinue) bContinue = thePatterns.delete();
			if (bContinue) bContinue = thePrices.delete();
			if (bContinue) bContinue = theRates.delete();
			if (bContinue) bContinue = theAccounts.delete();
			if (bContinue) bContinue = theTaxParams.delete();
			
			/* Return to caller */
			return bContinue;
		}

		/**
		 * Model class for Batches
		 */
		private abstract class BatchModel {
			/**
			 * The table to which this batch belongs
			 */
			private Table.TableModel	theTable = null;

			/**
			 * The list of items for this batch
			 */
			private finLink.itemCtl		theList  = null;
			
			/**
			 * Constructor for loading items
			 * @param pTable the table to load from
			 */
			private BatchModel(Table.TableModel pTable) {
				theTable = pTable;
			}
			
			/**
			 * Constructor for updating items
			 * @param pTable the table to update
			 * @param pList the list of source items
			 */
			private BatchModel(Table.TableModel pTable,
					           finLink.itemCtl  pList) {
				theTable = pTable;
				theList  = pList;
			}
			
			/**
			 * Determine the name of the items in the list
			 * @return the name
			 */
			public abstract String getItemsName();
			
			/**
			 * Load an individual item from the result set 
			 * @param pTable the table to load from
			 * @throws finObject.Exception
			 * @throws SQLException
			 */
			public abstract void   loadItem(Table.TableModel pTable)
										throws finObject.Exception, SQLException;
			
			/**
			 * Insert an individual item from the list
			 * @param pTable the table to insert into
			 * @param pItem the item to insert
			 * @throws finObject.Exception
			 * @throws SQLException
			 */
			public abstract void   insertItem(Table.TableModel pTable,
					                          finLink.itemElement     pItem)
										throws finObject.Exception, SQLException;
			
			/**
			 * Update an individual item from the list
			 * @param pTable the table to update into
			 * @param pItem the item to update
			 * @throws finObject.Exception
			 * @throws SQLException
			 */
			public void   updateItem(Table.TableModel pTable,
					                 finLink.itemElement     pItem)
										throws finObject.Exception, SQLException {};
			
			/**
			 * Load items from the list into the table
			 * @return Continue <code>true/false</code>
			 * @throws finObject.Exception
			 */
			public boolean load() throws finObject.Exception {
				boolean bContinue = true;
				int     myCount   = 0;
				
				/* Declare the new stage */
				if (!theThread.setNewStage(getItemsName())) return false;
				
				/* Protect the load */
				try {
					/* Count the Items to be loaded */
					if (!theThread.setNumSteps(theTable.countItems())) return false;
					
					/* Read the items from the table */
					theTable.loadItems();
				
					/* Loop through the results */
					while (theTables.next()) {
						/* Load the next item */
						loadItem(theTable);
						
						/* Report the progress */
						myCount++;
						if ((myCount % theThread.getReportingSteps()) == 0) 
							if (!theThread.setStepsDone(myCount)) return false;
					}
					
					/* Close the Statement */
					theTables.closeStmt();
				}
				
				catch (SQLException e) {
					throw new finObject.Exception(ExceptionClass.SQLSERVER,
							                      "Failed to load " + getItemsName(),
							                      e);
				}
				catch (finObject.Exception e) {
					throw new finObject.Exception(e.getExceptionClass(),
							                      "Failed to load " + getItemsName(),
							                      e);
				}
				
				/* Return to caller */
				return bContinue;
			}
			
			/**
			 * Mark an items as committed
			 */
			private void commitItem(finLink.itemElement pItem) {
				/* Handle Deletions */
				if (pItem.getState() == State.DELETED)
					pItem.getBase().unLink();
			
				/* else  */
				else { 
					/* Set the item to clean and clear history */
					pItem.getBase().setState(State.CLEAN);
					pItem.getBase().clearHistory();
				}
		
				/* Mark this item as clean */
				pItem.setState(State.CLEAN);
			}
			
			/**
			 * Mark a batch of updates as committed
			 * @param pState the state of the items to update
			 * @throws finObject.Exception
			 */
			public void commitBatch(State pState)	throws finObject.Exception {
				finLink.itemElement myCurr;
				int                 iBatch = 0;
				
				/* Protect the commit */
				try {
					/* Commit the update */
					theTables.commit();
						
					/* Loop through the list */
					for (myCurr = theList.getFirst();
					     myCurr != null;
					     myCurr = myCurr.getNext()) {
					
						/* Ignore items that are not this type */
						if ((pState != State.NOSTATE) && 
							(myCurr.getState() != pState)) continue;
						
						/* commit the Item */
						commitItem(myCurr);
						
						/* Handle end of batch */
						if ((theBatchSize > 0) &&
							(++iBatch >= theBatchSize)) break;
					}
				}	
				catch (SQLException e) {
					throw new finObject.Exception(ExceptionClass.SQLSERVER,
												  "Failed to commit " + getItemsName(),
												  e);
				}
			}
			
			/**
			 * Determine the count of items that are in a particular state
			 * @param pState the particular state
			 * @return the count of items
			 */
			private int countItems(State pState) {
				finLink.itemElement	myCurr;
				int                 iCount = 0;
				
				/* Loop through the list */
				for (myCurr = theList.getFirst();
				     myCurr != null;
				     myCurr = myCurr.getNext()) {
					
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
			 * @return Continue <code>true/false</code>
			 * @throws finObject.Exception
			 */
			public boolean insert() throws finObject.Exception {
				finLink.itemElement	myCurr;
				int                 iBatch    = 0;
				int     			myCount   = 0;
				boolean             bContinue = true;
				
				/* Declare the new stage */
				if (!theThread.setNewStage("Inserting " + getItemsName())) return false;
				
				/* Protect the insert */
				try {
					/* Declare the number of steps */
					if (!theThread.setNumSteps(countItems(State.NEW))) return false;
					
					/* Prepare the insert statement */
					theTable.insertItems();
				
					/* Loop through the list */
					for (myCurr = theList.getFirst();
					     myCurr != null;
					     myCurr = myCurr.getNext()) {
					
						/* Ignore non-new items */
						if (myCurr.getState() != State.NEW) continue;
						
						/* Set the fields */
						insertItem(theTable, myCurr);
					
						/* Execute the insert */
						theTables.execute();
						
						/* If we should commit the batch */
						if ((theBatchSize > 0) &&
							(++iBatch >= theBatchSize)) {
							/* Reset the batch count */
							iBatch = 0;
							
							/* Commit the batch */
							commitBatch(State.NEW);
						}
						
						/* Report the progress */
						myCount++;
						if ((myCount % theThread.getReportingSteps()) == 0) 
							if (!theThread.setStepsDone(myCount)) return false;
					}
										
					/* Handle outstanding commits */
					if ((theBatchSize > 0) && 
						(iBatch > 0))
						commitBatch(State.NEW);
											
					/* Close the Statement */
					theTables.closeStmt();
				}
				
				catch (SQLException e) {
					throw new finObject.Exception(ExceptionClass.SQLSERVER,
							                      "Failed to insert " + getItemsName(),
							                      e);
				}
				
				/* Return to caller */
				return bContinue;
			}
			
			/**
			 * Update items from the list
			 * @return Continue <code>true/false</code>
			 * @throws finObject.Exception
			 */
			public boolean update() throws finObject.Exception {
				finLink.itemElement myCurr;
				int              	iBatch    = 0;
				int     			myCount   = 0;
				boolean          	bContinue = true;
			
				/* Declare the new stage */
				if (!theThread.setNewStage("Updating " + getItemsName())) return false;
				
				/* Protect the update */
				try {
					/* Declare the number of steps */
					if (!theThread.setNumSteps(countItems(State.CHANGED))) return false;
					
					/* Loop through the list */
					for (myCurr = theList.getFirst();
						 myCurr != null;
				         myCurr = myCurr.getNext()) {

						/* Ignore non-changed items */
						if (myCurr.getState() != State.CHANGED) continue;
						
						/* Update the item */
						updateItem(theTable, myCurr);
											
						/* Set Id and execute update */
						theTable.updateId(myCurr.getId());
							
						/* Close the Statement */
						theTables.closeStmt();
						
						/* If we should commit the batch */
						if ((theBatchSize > 0) &&
							(++iBatch >= theBatchSize)) {
							/* Reset the batch count */
							iBatch = 0;
							
							/* Commit the batch */
							commitBatch(State.CHANGED);
						}
						
						/* Report the progress */
						myCount++;
						if ((myCount % theThread.getReportingSteps()) == 0) 
							if (!theThread.setStepsDone(myCount)) return false;
					}
										
					/* Handle outstanding commits */
					if ((theBatchSize > 0) && 
						(iBatch > 0))
						commitBatch(State.CHANGED);
				}
			
				catch (SQLException e) {
					throw new finObject.Exception(ExceptionClass.SQLSERVER,
												  "Failed to update " + getItemsName(),
												  e);
				}
				
				/* Return to caller */
				return bContinue;
			}
			
			/**
			 * Delete items from the list
			 * @return Continue <code>true/false</code>
			 * @throws finObject.Exception
			 */
			public boolean delete() throws finObject.Exception {
				finLink.itemElement	myCurr;
				int              	iBatch    = 0;
				int     			myCount   = 0;
				boolean          	bContinue = true;
			
				/* Declare the new stage */
				if (!theThread.setNewStage("Deleting " + getItemsName())) return false;
				
				/* Protect the delete */
				try {
					/* Declare the number of steps */
					if (!theThread.setNumSteps(countItems(State.DELETED))) return false;
					
					/* Prepare the delete statement */
					theTable.deleteItems();
			
					/* Loop through the list */
					for (myCurr = theList.getFirst();
						 myCurr != null;
				         myCurr = myCurr.getNext()) {
				
						/* Ignore non-deleted items */
						if (myCurr.getState() != State.DELETED) continue;
						
						/* DelNew items are just discarded */
						if (myCurr.getBase().getState() == State.DELNEW) {
							commitItem(myCurr);
							continue;
						}
						
						/* Set the fields */
						theTable.setID(myCurr.getId());
				
						/* Execute the delete */
						theTables.execute();
						
						/* If we should commit the batch */
						if ((theBatchSize > 0) &&
							(++iBatch >= theBatchSize)) {
							/* Reset the batch count */
							iBatch = 0;
							
							/* Commit the batch */
							commitBatch(State.DELETED);
						}
						
						/* Report the progress */
						myCount++;
						if ((myCount % theThread.getReportingSteps()) == 0) 
							if (!theThread.setStepsDone(myCount)) return false;
					}
										
					/* Handle outstanding commits */
					if ((theBatchSize > 0) && 
						(iBatch > 0))
						commitBatch(State.DELETED);
									
					/* Close the Statement */
					theTables.closeStmt();
				}
			
				catch (SQLException e) {
					throw new finObject.Exception(ExceptionClass.SQLSERVER,
												  "Failed to delete " + getItemsName(),
												  e);
				}
				
				/* Return to caller */
				return bContinue;
			}
		}
		
		/**
		 * Batch class for AccountTypes
		 */
		private class AccountTypes extends BatchModel {
			/**
			 * Constructor for loading AccountTypes
			 */
			private AccountTypes() {
				super(theTables.getAccountTypes());
			}
			
			/**
			 * Constructor for updating AccountTypes
			 * @param pList the source list for updates
			 */
			private AccountTypes(finStatic.ActTypeList pList) {
				super(theTables.getAccountTypes(),
				      theStatic.new ActTypeList(pList, ListStyle.UPDATE));
			}
			
			/**
			 * Determine the name of the items
			 * @return the name of the items
			 */
			public String getItemsName() { return "AccountTypes"; }

			/* Load the account type */
			public void loadItem(Table.TableModel pTable) 
							throws finObject.Exception, SQLException {
				Table.AccountTypes 	myTable = (Table.AccountTypes) pTable;
				long    			myId;
				String  			myType;
				
				/* Get the various fields */
				myId   = myTable.getID();
				myType = myTable.getAccountType();
					
				/* Add into the list */
				theBuilder.addAccountType(myId, myType);
										
				/* Return to caller */
				return;
			}
			
			/* Insert an Account Type */
			public void insertItem(Table.TableModel 	pTable,
								   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.AccountTypes 		myTable = (Table.AccountTypes) pTable;
				finStatic.AccountType   myCurr  = (finStatic.AccountType)  pItem;
				
				/* Set the fields */
				myTable.setID(myCurr.getId());
				myTable.setAccountType(myCurr.getName());
					
				/* Return to caller */
				return;
			}
		}
		
		/**
		 * Batch class for TransactionTypes
		 */
		private class TransTypes extends BatchModel {
			/**
			 * Constructor for loading TransTypes
			 */
			private TransTypes() {
				super(theTables.getTransTypes());
			}
			
			/**
			 * Constructor for updating TransTypes
			 * @param pList the source list for updates
			 */
			private TransTypes(finStatic.TransTypeList pList) {
				super(theTables.getTransTypes(),
				      theStatic.new TransTypeList(pList, ListStyle.UPDATE));
			}
			
			/**
			 * Determine the name of the items
			 * @return the name of the items
			 */
			public String getItemsName() { return "TransactionTypes"; }

			/* Load the transaction type */
			public void loadItem(Table.TableModel pTable) 
							throws finObject.Exception, SQLException {
				Table.TransTypes 	myTable = (Table.TransTypes) pTable;
				long    			myId;
				String  			myType;
				
				/* Get the various fields */
				myId   = myTable.getID();
				myType = myTable.getTransType();
					
				/* Add into the list */
				theBuilder.addTransType(myId, myType);
										
				/* Return to caller */
				return;
			}
			
			/* Insert a transaction Type */
			public void insertItem(Table.TableModel 	pTable,
								   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.TransTypes 	myTable = (Table.TransTypes) pTable;
				finStatic.TransType myCurr  = (finStatic.TransType)  pItem;
				
				/* Set the fields */
				myTable.setID(myCurr.getId());
				myTable.setTransType(myCurr.getName());
					
				/* Return to caller */
				return;
			}
		}		
		
		/**
		 * Batch class for TaxTypes
		 */
		private class TaxTypes extends BatchModel {
			/**
			 * Constructor for loading TaxTypes
			 */
			private TaxTypes() {
				super(theTables.getTaxTypes());
			}
			
			/**
			 * Constructor for updating TaxTypes
			 * @param pList the source list for updates
			 */
			private TaxTypes(finStatic.TaxTypeList pList) {
				super(theTables.getTaxTypes(),
				      theStatic.new TaxTypeList(pList, ListStyle.UPDATE));
			}
			
			/**
			 * Determine the name of the items
			 * @return the name of the items
			 */
			public String getItemsName() { return "TaxClasses"; }

			/* Load the tax type */
			public void loadItem(Table.TableModel pTable) 
							throws finObject.Exception, SQLException {
				Table.TaxTypes 	myTable = (Table.TaxTypes) pTable;
				long    		myId;
				String  		myType;
				
				/* Get the various fields */
				myId   = myTable.getID();
				myType = myTable.getTaxType();
					
				/* Add into the list */
				theBuilder.addTaxType(myId, myType);
										
				/* Return to caller */
				return;
			}
			
			/* Insert a tax Type */
			public void insertItem(Table.TableModel 	pTable,
								   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.TaxTypes 		myTable = (Table.TaxTypes) pTable;
				finStatic.TaxType 	myCurr  = (finStatic.TaxType)  pItem;
				
				/* Set the fields */
				myTable.setID(myCurr.getId());
				myTable.setTaxType(myCurr.getName());
					
				/* Return to caller */
				return;
			}
		}		
		
		/**
		 * Batch class for TaxRegimes
		 */
		private class TaxRegimes extends BatchModel {
			/**
			 * Constructor for loading TaxRegimes
			 */
			private TaxRegimes() {
				super(theTables.getTaxRegimes());
			}
			
			/**
			 * Constructor for updating TaxRegimes
			 * @param pList the source list for updates
			 */
			private TaxRegimes(finStatic.TaxRegimeList pList) {
				super(theTables.getTaxRegimes(),
				      theStatic.new TaxRegimeList(pList, ListStyle.UPDATE));
			}
			
			/**
			 * Determine the name of the items
			 * @return the name of the items
			 */
			public String getItemsName() { return "TaxRegimes"; }

			/* Load the tax regime */
			public void loadItem(Table.TableModel pTable) 
							throws finObject.Exception, SQLException {
				Table.TaxRegimes 	myTable = (Table.TaxRegimes) pTable;
				long    			myId;
				String  			myType;
				
				/* Get the various fields */
				myId   = myTable.getID();
				myType = myTable.getTaxRegime();
					
				/* Add into the list */
				theBuilder.addTaxRegime(myId, myType);
										
				/* Return to caller */
				return;
			}
			
			/* Insert a tax regime */
			public void insertItem(Table.TableModel 	pTable,
								   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.TaxRegimes 	myTable = (Table.TaxRegimes) pTable;
				finStatic.TaxRegime myCurr  = (finStatic.TaxRegime)  pItem;
				
				/* Set the fields */
				myTable.setID(myCurr.getId());
				myTable.setTaxRegime(myCurr.getName());
					
				/* Return to caller */
				return;
			}
		}		
		
		/**
		 * Batch class for Frequencies
		 */
		private class Frequencys extends BatchModel {
			/**
			 * Constructor for loading Frequencies
			 */
			private Frequencys() {
				super(theTables.getFrequencys());
			}
			
			/**
			 * Constructor for updating Frequencies
			 * @param pList the source list for updates
			 */
			private Frequencys(finStatic.FreqList pList) {
				super(theTables.getFrequencys(),
				      theStatic.new FreqList(pList, ListStyle.UPDATE));
			}
			
			/**
			 * Determine the name of the items
			 * @return the name of the items
			 */
			public String getItemsName() { return "Frequency"; }

			/* Load the frequency */
			public void loadItem(Table.TableModel pTable) 
							throws finObject.Exception, SQLException {
				Table.Frequencys 	myTable = (Table.Frequencys) pTable;
				long    			myId;
				String  			myType;
				
				/* Get the various fields */
				myId   = myTable.getID();
				myType = myTable.getFrequency();
					
				/* Add into the list */
				theBuilder.addFrequency(myId, myType);
										
				/* Return to caller */
				return;
			}
			
			/* Insert a frequency */
			public void insertItem(Table.TableModel 	pTable,
								   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.Frequencys 	myTable = (Table.Frequencys) pTable;
				finStatic.Frequency myCurr  = (finStatic.Frequency)  pItem;
				
				/* Set the fields */
				myTable.setID(myCurr.getId());
				myTable.setFrequency(myCurr.getName());
					
				/* Return to caller */
				return;
			}
		}		
		
		/**
		 * Batch class for TaxParams
		 */
		private class TaxParams extends BatchModel {
			/**
			 * Constructor for loading TaxParams
			 */
			private TaxParams() {
				super(theTables.getTaxParams());
			}
			
			/**
			 * Constructor for updating TaxParams
			 * @param pList the source list for updates
			 */
			private TaxParams(finData.TaxParmList pList) {
				super(theTables.getTaxParams(),
				      theData.new TaxParmList(pList, ListStyle.UPDATE));
			}
			
			/**
			 * Determine the name of the items
			 * @return the name of the items
			 */
			public String getItemsName() { return "TaxParams"; }

			/* Load the TaxParams */
			public void loadItem(Table.TableModel pTable) 
							throws finObject.Exception, SQLException {
				Table.TaxParams myTable = (Table.TaxParams) pTable;
				long    		myId;
				Date    		myYear;
				String  		myAllowance;
				String  		myRentalAllow;
				String  		myLoBand;
				String  		myBasicBand;
				String  		myLoAgeAllow;
				String  		myHiAgeAllow;
				String  		myCapitalAllow;
				String  		myAgeAllowLimit;
				String  		myAddAllowLimit;
				String  		myAddIncBound;
				String  		myLoTaxRate;
				String  		myBasicTaxRate;
				String  		myHiTaxRate;
				String  		myIntTaxRate;
				String  		myDivTaxRate;
				String  		myHiDivTaxRate;
				String  		myAddTaxRate;
				String  		myAddDivTaxRate;
				String  		myCapTaxRate;
				String  		myHiCapTaxRate;
				long	  		myRegime;
				
				/* Get the various fields */
				myId   = myTable.getID();
				
				/* Get the various fields */
				myId            = myTable.getID();
				myYear          = myTable.getYear();
				myAllowance     = myTable.getAllowance();
				myRentalAllow   = myTable.getRentalAllow();
				myLoBand        = myTable.getLoTaxBand();
				myBasicBand     = myTable.getBasicTaxBand();
				myLoAgeAllow    = myTable.getLoAgeAllow();
				myHiAgeAllow    = myTable.getHiAgeAllow();
				myCapitalAllow  = myTable.getCapitalAllow();
				myAgeAllowLimit = myTable.getAgeAllowLimit();
				myAddAllowLimit = myTable.getAddAllowLimit();
				myAddIncBound 	= myTable.getAddIncBoundary();
				myLoTaxRate     = myTable.getLoTaxRate();
				myBasicTaxRate  = myTable.getBasicTaxRate();
				myHiTaxRate     = myTable.getHiTaxRate();
				myIntTaxRate    = myTable.getIntTaxRate();
				myDivTaxRate    = myTable.getDivTaxRate();
				myHiDivTaxRate  = myTable.getHiDivTaxRate();
				myAddTaxRate    = myTable.getAddTaxRate();
				myAddDivTaxRate = myTable.getAddDivTaxRate();
				myCapTaxRate    = myTable.getCapTaxRate();
				myHiCapTaxRate  = myTable.getHiCapTaxRate();
				myRegime        = myTable.getTaxRegime();
			
				/* Add into the list */
				theBuilder.addTaxYear(myId, myRegime, myYear, 
						              myAllowance, myRentalAllow,
						              myLoAgeAllow, myHiAgeAllow,
						              myCapitalAllow,
						              myAgeAllowLimit, myAddAllowLimit,
						              myLoBand, myBasicBand,
						              myAddIncBound,
						              myLoTaxRate, myBasicTaxRate, 
						              myHiTaxRate, myIntTaxRate,
						              myDivTaxRate, myHiDivTaxRate,
						              myAddTaxRate, myAddDivTaxRate,
						              myCapTaxRate, myHiCapTaxRate);
				
				/* Return to caller */
				return;
			}
			
			/* Insert a TaxParams */
			public void insertItem(Table.TableModel 	pTable,
								   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.TaxParams 	myTable = (Table.TaxParams) pTable;
				finData.TaxParms	myCurr  = (finData.TaxParms)  pItem;
				
				/* Set the fields */
				myTable.setID(myCurr.getId());
				myTable.setYear(myCurr.getDate());
				myTable.setAllowance(myCurr.getAllowance());
				myTable.setRentalAllow(myCurr.getRentalAllowance());
				myTable.setLoTaxBand(myCurr.getLoBand());
				myTable.setBasicTaxBand(myCurr.getBasicBand());
				myTable.setLoAgeAllow(myCurr.getLoAgeAllow());
				myTable.setHiAgeAllow(myCurr.getHiAgeAllow());
				myTable.setCapitalAllow(myCurr.getCapitalAllow());
				myTable.setAgeAllowLimit(myCurr.getAgeAllowLimit());
				myTable.setAddAllowLimit(myCurr.getAddAllowLimit());
				myTable.setAddIncBoundary(myCurr.getAddIncBound());
				myTable.setLoTaxRate(myCurr.getLoTaxRate());
				myTable.setBasicTaxRate(myCurr.getBasicTaxRate());
				myTable.setHiTaxRate(myCurr.getHiTaxRate());
				myTable.setIntTaxRate(myCurr.getIntTaxRate());
				myTable.setDivTaxRate(myCurr.getDivTaxRate());
				myTable.setHiDivTaxRate(myCurr.getHiDivTaxRate());
				myTable.setAddTaxRate(myCurr.getAddTaxRate());
				myTable.setAddDivTaxRate(myCurr.getAddDivTaxRate());
				myTable.setCapTaxRate(myCurr.getCapTaxRate());
				myTable.setHiCapTaxRate(myCurr.getHiCapTaxRate());
				myTable.setTaxRegime(myCurr.getTaxRegime().getId());
			
				/* Return to caller */
				return;
			}
			
			/* Update a TaxParams */
			public void updateItem(Table.TableModel 	pTable,
								   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.TaxParams 		myTable = (Table.TaxParams) pTable;
				finData.TaxParms		myCurr  = (finData.TaxParms)  pItem;
				finData.TaxParmValues 	myBase;
				
				/* Access the base */
				myBase = (finData.TaxParmValues)myCurr.getBaseObj();
					
				/* Update the fields */
				if (finObject.differs(myCurr.getAllowance(),
			  			  			  myBase.getAllowance()))
					myTable.updateAllowance(myCurr.getAllowance());
				if (finObject.differs(myCurr.getRentalAllowance(),
			  			  			  myBase.getRentalAllow()))
					myTable.updateRentalAllow(myCurr.getRentalAllowance());
				if (finObject.differs(myCurr.getLoBand(),
			  			  			  myBase.getLoBand()))
					myTable.updateLoTaxBand(myCurr.getLoBand());
				if (finObject.differs(myCurr.getBasicBand(),
			  			  			  myBase.getBasicBand()))
					myTable.updateBasicTaxBand(myCurr.getBasicBand());
				if (finObject.differs(myCurr.getLoAgeAllow(),
			  			  			  myBase.getLoAgeAllow()))
					myTable.updateLoAgeAllow(myCurr.getLoAgeAllow());
				if (finObject.differs(myCurr.getHiAgeAllow(),
			  			  			  myBase.getHiAgeAllow()))
					myTable.updateHiAgeAllow(myCurr.getHiAgeAllow());
				if (finObject.differs(myCurr.getCapitalAllow(),
			  			  			  myBase.getCapitalAllow()))
					myTable.updateCapitalAllow(myCurr.getHiAgeAllow());
				if (finObject.differs(myCurr.getAgeAllowLimit(),
			  			  			  myBase.getAgeAllowLimit()))
					myTable.updateAgeAllowLimit(myCurr.getAgeAllowLimit());
				if (finObject.differs(myCurr.getAddAllowLimit(),
			  			  			  myBase.getAddAllowLimit()))
					myTable.updateAddAllowLimit(myCurr.getAddAllowLimit());
				if (finObject.differs(myCurr.getAddIncBound(),
			  			  			  myBase.getAddIncBound()))
					myTable.updateAddIncBoundary(myCurr.getAddIncBound());
				if (finObject.differs(myCurr.getLoTaxRate(),
			  			  			  myBase.getLoTaxRate()))
					myTable.updateLoTaxRate(myCurr.getLoTaxRate());
				if (finObject.differs(myCurr.getBasicTaxRate(),
			  			  			  myBase.getBasicTaxRate()))
					myTable.updateBasicTaxRate(myCurr.getBasicTaxRate());
				if (finObject.differs(myCurr.getHiTaxRate(),
			  			  			  myBase.getHiTaxRate()))
					myTable.updateHiTaxRate(myCurr.getHiTaxRate());
				if (finObject.differs(myCurr.getIntTaxRate(),
			  			  			  myBase.getIntTaxRate()))
					myTable.updateIntTaxRate(myCurr.getIntTaxRate());
				if (finObject.differs(myCurr.getDivTaxRate(),
			  			  			  myBase.getDivTaxRate()))
					myTable.updateDivTaxRate(myCurr.getDivTaxRate());
				if (finObject.differs(myCurr.getHiDivTaxRate(),
			  			  			  myBase.getHiDivTaxRate()))
					myTable.updateHiDivTaxRate(myCurr.getHiDivTaxRate());
				if (finObject.differs(myCurr.getAddTaxRate(),
			  			  			  myBase.getAddTaxRate()))
					myTable.updateAddTaxRate(myCurr.getAddTaxRate());
				if (finObject.differs(myCurr.getAddDivTaxRate(),
			  			  			  myBase.getAddDivTaxRate()))
					myTable.updateAddDivTaxRate(myCurr.getAddDivTaxRate());
				if (finObject.differs(myCurr.getCapTaxRate(),
			  			  			  myBase.getCapTaxRate()))
					myTable.updateCapTaxRate(myCurr.getCapTaxRate());
				if (finObject.differs(myCurr.getHiCapTaxRate(),
			  			  			  myBase.getHiCapTaxRate()))
					myTable.updateHiCapTaxRate(myCurr.getHiCapTaxRate());
				if (finObject.differs(myCurr.getTaxRegime(),
			  			  myBase.getTaxRegime()))
					myTable.updateTaxRegime(myCurr.getTaxRegime().getId());
				
				/* Return to caller */
				return;
			}
		}		
				
		/**
		 * Batch class for Accounts
		 */
		private class Accounts extends BatchModel {
			/**
			 * Constructor for loading Accounts
			 */
			private Accounts() {
				super(theTables.getAccounts());
			}
			
			/**
			 * Constructor for updating Accounts
			 * @param pList the source list for updates
			 */
			private Accounts(finData.AccountList pList) {
				super(theTables.getAccounts(),
				      theData.new AccountList(pList, ListStyle.UPDATE));
			}
			
			/**
			 * Determine the name of the items
			 * @return the name of the items
			 */
			public String getItemsName() { return "Accounts"; }

			/* Load the Account */
			public void loadItem(Table.TableModel pTable) 
							throws finObject.Exception, SQLException {
				Table.Accounts 	myTable = (Table.Accounts) pTable;
				long    		myId;
				String  		myAccount;
				long    		myActTypeId;
				long    		myParentId;
				String  		myDesc;
				Date    		myMaturity;
				Date    		myClosed;
				
				/* Get the various fields */
				myId        = myTable.getID();
				myAccount   = myTable.getName();
				myActTypeId = myTable.getAccountType();
				myDesc      = myTable.getDescription();
				myMaturity  = myTable.getMaturity();
				myClosed    = myTable.getClosed();
				myParentId	= myTable.getParent();
			
				/* Add into the list */
				theBuilder.addAccount(myId, 
						              myAccount, 
						              myActTypeId,
						              myDesc, 
						              myMaturity,
						              myClosed,
						              myParentId);
				
				/* Return to caller */
				return;
			}
			
			/* Insert an Account */
			public void insertItem(Table.TableModel 	pTable,
								   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.Accounts 	myTable = (Table.Accounts) 	pTable;
				finData.Account	myCurr  = (finData.Account) pItem;
				
				/* Set the fields */
				myTable.setID(myCurr.getId());
				myTable.setName(myCurr.getName());
				myTable.setAccountType(myCurr.getActType().getId());
				myTable.setDescription(myCurr.getDesc());
				myTable.setMaturity(myCurr.getMaturity());
				myTable.setClosed(myCurr.getClose());
				myTable.setParent((myCurr.getParent() != null)
										? myCurr.getParent().getId() : -1);
						
				/* Return to caller */
				return;
			}
			
			/* Update an Account */
			public void updateItem(Table.TableModel 	pTable,
								   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.Accounts 			myTable = (Table.Accounts)	 pTable;
				finData.Account			myCurr  = (finData.Account)  pItem;
				finData.AccountValues 	myBase;
				
				/* Access the base */
				myBase = (finData.AccountValues)myCurr.getBaseObj();
					
				/* Update the fields */
				if (finObject.differs(myCurr.getName(),
						  			  myBase.getName()))
					myTable.updateName(myCurr.getName());
				if (finObject.differs(myCurr.getDesc(),
									  myBase.getDesc())) 
					myTable.updateDescription(myCurr.getDesc());
				if (finObject.differs(myCurr.getMaturity(),
						              myBase.getMaturity())) 
					myTable.updateMaturity(myCurr.getMaturity());
				if (finObject.differs(myCurr.getClose(),
									  myBase.getClose()))
					myTable.updateClosed(myCurr.getClose());
				if (finObject.differs(myCurr.getParent(),
									  myBase.getParent()))
					myTable.updateParent((myCurr.getParent() != null)
												? myCurr.getParent().getId() : -1);
				
				/* Return to caller */
				return;
			}
		}		
				
		/**
		 * Batch class for Patterns
		 */
		private class Patterns extends BatchModel {
			/**
			 * Constructor for loading Patterns
			 */
			private Patterns() {
				super(theTables.getPatterns());
			}
			
			/**
			 * Constructor for updating Patterns
			 * @param pList the source list for updates
			 */
			private Patterns(finData.PatternList pList) {
				super(theTables.getPatterns(),
				      theData.new PatternList(pList, ListStyle.UPDATE));
			}
			
			/**
			 * Determine the name of the items
			 * @return the name of the items
			 */
			public String getItemsName() { return "Patterns"; }

			/* Load the Pattern */
			public void loadItem(Table.TableModel pTable) 
							throws finObject.Exception, SQLException {
				Table.Patterns 	myTable = (Table.Patterns) pTable;
				long    		myId;
				long    		myAcctId;
				long    		myPartId;
				long    		myTransId;
				long    		myFreqId;
				String  		myAmount;
				boolean 		isCredit;
				String  		myDesc;
				Date    		myDate;
				
				/* Get the various fields */
				myId        = myTable.getID();
				myAcctId    = myTable.getAccount();
				myPartId    = myTable.getPartner();
				myTransId   = myTable.getTransType();
				myDate      = myTable.getDate();
				myDesc      = myTable.getDesc();
				isCredit    = myTable.getIsCredit();
				myAmount    = myTable.getAmount();
				myFreqId    = myTable.getFrequency();
					
				/* Add into the list */
				theBuilder.addPattern(myId, 
						              myDate,
						              myDesc,
						              myAmount,
						              myAcctId,
						              myPartId, 
						              myTransId,
						              myFreqId,
						              isCredit);
			}
						
			/* Insert the Pattern */
			public void insertItem(Table.TableModel 	pTable,
					   			   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.Patterns 	myTable = (Table.Patterns) 	pTable;
				finData.Pattern	myCurr  = (finData.Pattern) pItem;
									
				/* Set the fields */
				myTable.setID(myCurr.getId());
				myTable.setDate(myCurr.getDate());
				myTable.setDesc(myCurr.getDesc());
				myTable.setAmount(myCurr.getAmount());
				myTable.setTransType(myCurr.getTransType().getId());
				myTable.setIsCredit(myCurr.isCredit());
				myTable.setFrequency(myCurr.getFrequency().getId());				
				myTable.setPartner(myCurr.getPartner().getId());
				myTable.setAccount(myCurr.getAccount().getId());							
			}
		
			/* Update the Pattern */
			public void updateItem(Table.TableModel 	pTable,
								   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.Patterns 			myTable = (Table.Patterns)	 pTable;
				finData.Pattern			myCurr  = (finData.Pattern)  pItem;
				finData.PatternValues 	myBase;
				
				/* Access the base */
				myBase = (finData.PatternValues)myCurr.getBaseObj();
					
				/* Update the fields */
				if (finObject.differs(myCurr.getDate(),
									  myBase.getDate()))
					myTable.updateDate(myCurr.getDate());
				if (finObject.differs(myCurr.getDesc(),
						              myBase.getDesc()))
					myTable.updateDescription(myCurr.getDesc());
				if (finObject.differs(myCurr.getAmount(),
									  myBase.getAmount()))
					myTable.updateAmount(myCurr.getAmount());
				if (finObject.differs(myCurr.getPartner(),
						  			  myBase.getPartner()))
					myTable.updatePartner(myCurr.getPartner().getId());
				if (finObject.differs(myCurr.getTransType(),
						  			  myBase.getTransType()))
					myTable.updateTransType(myCurr.getTransType().getId());
				if (finObject.differs(myCurr.getFrequency(),
						  			  myBase.getFrequency()))
					myTable.updateFrequency(myCurr.getFrequency().getId());
			}
		}
		
		/**
		 * Batch class for Rates
		 */
		private class Rates extends BatchModel {
			/**
			 * Constructor for loading Rates
			 */
			private Rates() {
				super(theTables.getRates());
			}
			
			/**
			 * Constructor for updating Rates
			 * @param pList the source list for updates
			 */
			private Rates(finData.RateList pList) {
				super(theTables.getRates(),
				      theData.new RateList(pList, ListStyle.UPDATE));
			}
			
			/**
			 * Determine the name of the items
			 * @return the name of the items
			 */
			public String getItemsName() { return "Rates"; }

			/* Load the Rate */
			public void loadItem(Table.TableModel pTable) 
							throws finObject.Exception, SQLException {
				Table.Rates	myTable = (Table.Rates) pTable;
				long    	myId;
				long    	myActId;
				String  	myRate;
				String  	myBonus;
				Date    	myEndDate;
				
				/* Get the various fields */
				myId        = myTable.getID();
				myActId     = myTable.getAccount();
				myRate      = myTable.getRate();
				myBonus     = myTable.getBonus();
				myEndDate   = myTable.getEndDate();
					
				/* Add into the list */
				theBuilder.addRate(myId, 
						           myActId, 
						           myRate,
						           myEndDate, 
						           myBonus);
			}
			
			/* Insert the Rate */
			public void insertItem(Table.TableModel 	pTable,
					   			   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.Rates 	myTable = (Table.Rates) 	pTable;
				finData.Rate	myCurr  = (finData.Rate)	pItem;
									
				/* Set the fields */
				myTable.setID(myCurr.getId());
				myTable.setAccount(myCurr.getAccount().getId());
				myTable.setRate(myCurr.getRate());
				myTable.setEndDate(myCurr.getEndDate());
				myTable.setBonus(myCurr.getBonus());
			}
			
			/* Update the Rate */
			public void updateItem(Table.TableModel 	pTable,
								   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.Rates			myTable = (Table.Rates)		pTable;
				finData.Rate		myCurr  = (finData.Rate)	pItem;
				finData.RateValues 	myBase;
				
				/* Access the base */
				myBase = (finData.RateValues)myCurr.getBaseObj();
					
				/* Update the fields */
				if (finObject.differs(myCurr.getRate(),
						              myBase.getRate()))
					myTable.updateRate(myCurr.getRate());
				if (finObject.differs(myCurr.getBonus(), 
					                 myBase.getBonus())) 
					myTable.updateBonus(myCurr.getBonus());
				if (finObject.differs(myCurr.getEndDate(), 
									  myBase.getEndDate()))
					myTable.updateEndDate(myCurr.getEndDate());
			}
		}
		
		/**
		 * Batch class for Prices
		 */		
		private class Prices extends BatchModel {
			/**
			 * Constructor for loading Prices
			 */
			private Prices() {
				super(theTables.getPrices());
			}
			
			/**
			 * Constructor for updating Prices
			 * @param pList the source list for updates
			 */
			private Prices(finData.PriceList pList) {
				super(theTables.getPrices(),
				      theData.new PriceList(pList, ListStyle.UPDATE));
			}
			
			/**
			 * Determine the name of the items
			 * @return the name of the items
			 */
			public String getItemsName() { return "Prices"; }

			/* Load the Price */
			public void loadItem(Table.TableModel pTable) 
							throws finObject.Exception, SQLException {
				Table.Prices	myTable = (Table.Prices) pTable;
				long    		myId;
				long    		myActId;
				String  		myPrice;
				Date    		myDate;
				
				/* Get the various fields */
				myId        = myTable.getID();
				myActId     = myTable.getAccount();
				myDate      = myTable.getDate();
				myPrice     = myTable.getPrice();
					
				/* Add into the list */
				theBuilder.addPrice(myId,
						            myDate,
						            myActId, 
						            myPrice);
			}
			
			/* Insert the Price */
			public void insertItem(Table.TableModel 	pTable,
					   			   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.Prices 	myTable = (Table.Prices) 	pTable;
				finData.Price	myCurr  = (finData.Price)	pItem;
									
				/* Set the fields */
				myTable.setID(myCurr.getId());
				myTable.setAccount(myCurr.getAccount().getId());
				myTable.setDate(myCurr.getDate());
				myTable.setPrice(myCurr.getPrice());
			}
		
			/* Update the Price */
			public void updateItem(Table.TableModel 	pTable,
								   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.Prices		myTable = (Table.Prices)	pTable;
				finData.Price		myCurr  = (finData.Price)	pItem;
				finData.PriceValues myBase;
				
				/* Access the base */
				myBase = (finData.PriceValues)myCurr.getBaseObj();
					
				/* Update the fields */
				if (finObject.differs(myCurr.getDate(),
									  myBase.getDate()))
					myTable.updateDate(myCurr.getDate());
				if (finObject.differs(myCurr.getPrice(),
									  myBase.getPrice()))
					myTable.updatePrice(myCurr.getPrice());
			}
		}
		
		/**
		 * Batch class for Events
		 */		
		private class Events extends BatchModel {
			/**
			 * Constructor for loading Events
			 */
			private Events() {
				super(theTables.getEvents());
			}
			
			/**
			 * Constructor for updating Events
			 * @param pList the source list for updates
			 */
			private Events(finData.EventList pList) {
				super(theTables.getEvents(),
				      theData.new EventList(pList, ListStyle.UPDATE));
			}
			
			/**
			 * Determine the name of the items
			 * @return the name of the items
			 */
			public String getItemsName() { return "Events"; }

			/* Load the Event */
			public void loadItem(Table.TableModel pTable) 
							throws finObject.Exception, SQLException {
				Table.Events	myTable = (Table.Events) pTable;
				long    		myId;
				long    		myDebit;
				long    		myCredit;
				long    		myTransType;
				String  		myDesc;
				String  		myAmount;
				String  		myUnits;
				String			myTaxCredit;
				Date    		myDate;
				int				myYears;
				
				/* Get the various fields */
				myId        = myTable.getID();
				myDate      = myTable.getDate();
				myDesc      = myTable.getDesc();
				myAmount    = myTable.getAmount();
				myDebit     = myTable.getDebit();
				myCredit    = myTable.getCredit();
				myTransType = myTable.getTransType();
				myUnits     = myTable.getUnits();
				myTaxCredit = myTable.getTaxCredit();
				myYears		= myTable.getYears();
					
				/* Add into the list */
				theBuilder.addEvent(myId, 
						            myDate,
						            myDesc,
						            myAmount, 
						            myDebit,
						            myCredit, 
						            myUnits,
						            myTransType,
						            myTaxCredit,
						            myYears);
			}
			
			/* Insert the Event */
			public void insertItem(Table.TableModel 	pTable,
					   			   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.Events 	myTable = (Table.Events) 	pTable;
				finData.Event	myCurr  = (finData.Event)	pItem;
									
				/* Set the fields */
				myTable.setID(myCurr.getId());
				myTable.setDate(myCurr.getDate());
				myTable.setDesc(myCurr.getDesc());
				myTable.setAmount(myCurr.getAmount());
				myTable.setDebit(myCurr.getDebit().getId());
				myTable.setCredit(myCurr.getCredit().getId());
				myTable.setUnits(myCurr.getUnits());
				myTable.setTransType(myCurr.getTransType().getId());					
				myTable.setTaxCredit(myCurr.getTaxCredit());
				myTable.setYears(myCurr.getYears());
			}
		
			/* Update the Event */
			public void updateItem(Table.TableModel 	pTable,
								   finLink.itemElement  pItem)
							throws finObject.Exception, SQLException  {
				Table.Events		myTable = (Table.Events)	pTable;
				finData.Event	    myCurr  = (finData.Event)	pItem;
				finData.EventValues myBase;
				
				/* Access the base */
				myBase = (finData.EventValues)myCurr.getBaseObj();
					
				/* Update the fields */
				if (finObject.differs(myCurr.getDate(),
						  			  myBase.getDate()))
					myTable.updateDate(myCurr.getDate());
				if (finObject.differs(myCurr.getDesc(),
						  			  myBase.getDesc()))
					myTable.updateDescription(myCurr.getDesc());
				if (finObject.differs(myCurr.getAmount(),
						  			  myBase.getAmount()))
					myTable.updateAmount(myCurr.getAmount());
				if (finObject.differs(myCurr.getDebit(),
			  			  			  myBase.getDebit()))
					myTable.updateDebit(myCurr.getDebit().getId());
				if (finObject.differs(myCurr.getCredit(),
									  myBase.getCredit()))
					myTable.updateCredit(myCurr.getCredit().getId());
				if (finObject.differs(myCurr.getUnits(),
									  myBase.getUnits()))
					myTable.updateUnits(myCurr.getUnits());
				if (finObject.differs(myCurr.getTransType(),
			  			  			  myBase.getTransType()))
					myTable.updateTransType(myCurr.getTransType().getId());
				if (finObject.differs(myCurr.getTaxCredit(),
			  			  			  myBase.getTaxCredit()))
					myTable.updateTaxCredit(myCurr.getTaxCredit());
				if (myCurr.getYears() != myBase.getYears())
					myTable.updateYears(myCurr.getYears());	
			}
		}
	}	
}