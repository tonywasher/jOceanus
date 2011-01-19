package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class TaxType extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "TaxClass";

	/**
	 * TaxType Name length
	 */
	public final static int NAMELEN = 50;

	/**
	 * The class of the TaxType
	 */
	private TaxClass   theClass = null;

	/**
	 * The sort order of the Tax Type
	 */
	private int        theOrder = -1;
	
	/**
	 * Constant to provide separation of classes in sort order
	 */		
	public final static short CLASSDIVIDE = 100;
	
	/**
	 * Return the name of the Tax Type
	 * @return the name
	 */
	public	String getName()              { return getObj().getName(); }

	/**
	 * Return the sort order of the Account Type
	 * @return the order
	 */
	public	int    getOrder()             { return theOrder; }
		
	/* Linking methods */
	public TaxType getBase() { return (TaxType)super.getBase(); }
	public Values  getObj()  { return (Values)super.getObj(); }	

	/* Field IDs */
	public static final int FIELD_ID     	= 0;
	public static final int FIELD_NAME     	= 1;
	public static final int FIELD_ORDER     = 2;
	public static final int FIELD_CLASS     = 3;
	public static final int NUMFIELDS	    = 4;
	
	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	public String itemType() { return objName; }
	
	/**
	 * Obtain the number of fields for an item
	 * @return the number of fields
	 */
	public int	numFields() {return NUMFIELDS; }
	
	/**
	 * Determine the field name for a particular field
	 * @return the field name
	 */
	public String	fieldName(int iField) {
		switch (iField) {
			case FIELD_ID: 	  return "ID";
			case FIELD_NAME:  return "Name";
			case FIELD_ORDER: return "Order";
			case FIELD_CLASS: return "Class";
			default:		  return super.fieldName(iField);
		}
	}
	
	/**
	 * Format the value of a particular field as a table row
	 * @param iField the field number
	 * @param pObj the values to use
	 * @return the formatted field
	 */
	public String formatField(int iField, histObject pObj) {
		String myString = "<tr><td>" + fieldName(iField) + "</td><td>"; 
		switch (iField) {
			case FIELD_ID: 		myString += getId();  	break;
			case FIELD_NAME:	myString += getName(); 	break;
			case FIELD_ORDER: 	myString += getOrder();	break;
			case FIELD_CLASS: 	myString += theClass;	break;
		}
		return myString + "</td></tr>";
	}
			
	/**
	 * Construct a copy of a Tax Type.
	 * 
	 * @param pList	The list to associate the Tax Type with
	 * @param pTaxType The Tax Type to copy 
	 */
	protected TaxType(List pList, TaxType pTaxType) { 
		super(pList, pTaxType.getId());
		Values myObj = new Values(pTaxType.getObj());
		setObj(myObj);
		theClass = pTaxType.theClass;
		setBase(pTaxType);
		setState(pTaxType.getState());
		theOrder = pTaxType.getOrder();
	}
	
	/**
	 * Construct a standard Tax type on load
	 * 
	 * @param pList	The list to associate the Tax Type with
	 * @param uId   ID of Tax Type
	 * @param sName Name of Tax Type
	 */
	public TaxType(List 	pList,
			       long     uId, 
			       String   sName) throws Exception {
		super(pList, uId);
		Values myObj = new Values();
		setObj(myObj);
		myObj.setName(sName);
		pList.setNewId(this);				
	
		if (sName.equals("GrossSalary")) {
			theClass = TaxClass.GROSSSALARY;
			theOrder = 0;
		}
		else if (sName.equals("GrossInterest")) {
			theClass = TaxClass.GROSSINTEREST;
			theOrder = 1;
		}
		else if (sName.equals("GrossDividends")) {
			theClass = TaxClass.GROSSDIVIDEND;
			theOrder = 2;
		}
		else if (sName.equals("GrossUnitTrustDividends")) {
			theClass = TaxClass.GROSSUTDIVS;
			theOrder = 3;
		}
		else if (sName.equals("GrossRental")) {
			theClass = TaxClass.GROSSRENTAL;
			theOrder = 4;
		}
		else if (sName.equals("GrossTaxableGains")) {
			theClass = TaxClass.GROSSTAXGAINS;
			theOrder = 5;
		}
		else if (sName.equals("GrossCapitalGains")) {
			theClass = TaxClass.GROSSCAPGAINS;
			theOrder = 6;
		}
		else if (sName.equals("TaxationPaid")) {
			theClass = TaxClass.TAXPAID;
			theOrder = 7;
		}
		else if (sName.equals("TaxFree")) {
			theClass = TaxClass.TAXFREE;
			theOrder = 8;
		}
		else if (sName.equals("Market")) {
			theClass = TaxClass.MARKET;
			theOrder = 9;
		}
		else if (sName.equals("Expense")) {
			theClass = TaxClass.EXPENSE;
			theOrder = 10;
		}
		else if (sName.equals("Profit/Loss")) {
			theClass = TaxClass.PROFIT;
			theOrder = 11;
		}
		else if (sName.equals("CoreProfit/Loss")) {
			theClass = TaxClass.COREPROFIT;
			theOrder = 12;
		}
		else if (sName.equals("GrossIncome")) {
			theClass = TaxClass.GROSSINCOME;
			theOrder = 0+CLASSDIVIDE;
		}
		else if (sName.equals("OriginalAllowance")) {
			theClass = TaxClass.ORIGALLOW;
			theOrder = 1+CLASSDIVIDE;
		}
		else if (sName.equals("AdjustedAllowance")) {
			theClass = TaxClass.ADJALLOW;
			theOrder = 2+CLASSDIVIDE;
		}
		else if (sName.equals("HighTaxBand")) {
			theClass = TaxClass.HITAXBAND;
			theOrder = 3+CLASSDIVIDE;
		}
		else if (sName.equals("SalaryNilRate")) {
			theClass = TaxClass.SALARYFREE;
			theOrder = 4+CLASSDIVIDE;
		}
		else if (sName.equals("RentalNilRate")) {
			theClass = TaxClass.RENTALFREE;
			theOrder = 5+CLASSDIVIDE;
		}
		else if (sName.equals("InterestNilRate")) {
			theClass = TaxClass.INTERESTFREE;
			theOrder = 6+CLASSDIVIDE;
		}
		else if (sName.equals("CapitalNilRate")) {
			theClass = TaxClass.CAPITALFREE;
			theOrder = 7+CLASSDIVIDE;
		}
		else if (sName.equals("SalaryLowRate")) {
			theClass = TaxClass.SALARYLO;
			theOrder = 8+CLASSDIVIDE;
		}
		else if (sName.equals("RentalLowRate")) {
			theClass = TaxClass.RENTALLO;
			theOrder = 9+CLASSDIVIDE;
		}
		else if (sName.equals("InterestLowRate")) {
			theClass = TaxClass.INTERESTLO;
			theOrder = 10+CLASSDIVIDE;
		}
		else if (sName.equals("SalaryBasicRate")) {
			theClass = TaxClass.SALARYBASIC;
			theOrder = 11+CLASSDIVIDE;
		}
		else if (sName.equals("RentalBasicRate")) {
			theClass = TaxClass.RENTALBASIC;
			theOrder = 12+CLASSDIVIDE;
		}
		else if (sName.equals("InterestBasicRate")) {
			theClass = TaxClass.INTERESTBASIC;
			theOrder = 13+CLASSDIVIDE;
		}
		else if (sName.equals("DividendBasicRate")) {
			theClass = TaxClass.DIVIDENDBASIC;
			theOrder = 14+CLASSDIVIDE;
		}
		else if (sName.equals("SliceBasicRate")) {
			theClass = TaxClass.SLICEBASIC;
			theOrder = 15+CLASSDIVIDE;
		}
		else if (sName.equals("GainsBasicRate")) {
			theClass = TaxClass.GAINSBASIC;
			theOrder = 16+CLASSDIVIDE;
		}
		else if (sName.equals("CapitalBasicRate")) {
			theClass = TaxClass.CAPITALBASIC;
			theOrder = 17+CLASSDIVIDE;
		}
		else if (sName.equals("SalaryHighRate")) {
			theClass = TaxClass.SALARYHI;
			theOrder = 18+CLASSDIVIDE;
		}
		else if (sName.equals("RentalHighRate")) {
			theClass = TaxClass.RENTALHI;
			theOrder = 19+CLASSDIVIDE;
		}
		else if (sName.equals("InterestHighRate")) {
			theClass = TaxClass.INTERESTHI;
			theOrder = 20+CLASSDIVIDE;
		}
		else if (sName.equals("DividendHighRate")) {
			theClass = TaxClass.DIVIDENDHI;
			theOrder = 21+CLASSDIVIDE;
		}
		else if (sName.equals("SliceHighRate")) {
			theClass = TaxClass.SLICEHI;
			theOrder = 22+CLASSDIVIDE;
		}
		else if (sName.equals("GainsHighRate")) {
			theClass = TaxClass.GAINSHI;
			theOrder = 23+CLASSDIVIDE;
		}
		else if (sName.equals("CapitalHighRate")) {
			theClass = TaxClass.CAPITALHI;
			theOrder = 24+CLASSDIVIDE;
		}
		else if (sName.equals("SalaryAdditionalRate")) {
			theClass = TaxClass.SALARYADD;
			theOrder = 25+CLASSDIVIDE;
		}
		else if (sName.equals("RentalAdditionalRate")) {
			theClass = TaxClass.RENTALADD;
			theOrder = 26+CLASSDIVIDE;
		}
		else if (sName.equals("InterestAdditionalRate")) {
			theClass = TaxClass.INTERESTADD;
			theOrder = 27+CLASSDIVIDE;
		}
		else if (sName.equals("DividendAdditionalRate")) {
			theClass = TaxClass.DIVIDENDADD;
			theOrder = 28+CLASSDIVIDE;
		}
		else if (sName.equals("SliceAdditionalRate")) {
			theClass = TaxClass.SLICEADD;
			theOrder = 29+CLASSDIVIDE;
		}
		else if (sName.equals("GainsAdditionalRate")) {
			theClass = TaxClass.GAINSADD;
			theOrder = 20+CLASSDIVIDE;
		}
		else if (sName.equals("TaxDueSlice")) {
			theClass = TaxClass.TAXDUESLICE;
			theOrder = 21+CLASSDIVIDE;
		}
		else if (sName.equals("TaxDueSalary")) {
			theClass = TaxClass.TAXDUESALARY;
			theOrder = 0+2*CLASSDIVIDE;
		}
		else if (sName.equals("TaxDueRental")) {
			theClass = TaxClass.TAXDUERENTAL;
			theOrder = 1+2*CLASSDIVIDE;
		}
		else if (sName.equals("TaxDueInterest")) {
			theClass = TaxClass.TAXDUEINTEREST;
			theOrder = 2+2*CLASSDIVIDE;
		}
		else if (sName.equals("TaxDueDividends")) {
			theClass = TaxClass.TAXDUEDIVIDEND;
			theOrder = 3+2*CLASSDIVIDE;
		}
		else if (sName.equals("TaxDueTaxableGains")) {
			theClass = TaxClass.TAXDUETAXGAINS;
			theOrder = 4+2*CLASSDIVIDE;
		}
		else if (sName.equals("TaxDueCapitalGains")) {
			theClass = TaxClass.TAXDUECAPGAINS;
			theOrder = 5+2*CLASSDIVIDE;
		}
		else if (sName.equals("TotalTaxation")) {
			theClass = TaxClass.TOTALTAX;
			theOrder = 0+3*CLASSDIVIDE;
		}
		else if (sName.equals("TaxProfit/Loss")) {
			theClass = TaxClass.TAXPROFIT;
			theOrder = 1+3*CLASSDIVIDE;
		}
		else {
			throw new Exception(ExceptionClass.DATA,
  					  			this,
                                "Invalid TaxType");
		}
	}
	
	/**
	 * Compare this tax type to another to establish equality.
	 * 
	 * @param pThat The Tax type to compare to
	 * @return <code>true</code> if the tax type is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a TransactionType */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target transactionType */
		TaxType myType = (TaxType)pThat;

		if (getId() != myType.getId()) return false;
		return (getName().compareTo(myType.getName()) == 0);
	}

	/**
	 * Compare this tax type to another to establish sort order.
	 * 
	 * @param pThat The Tax type to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		long result;

		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a TaxType */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the target taxType */
		TaxType myThat = (TaxType)pThat;
		
		/* Compare on order */
		if (theOrder < myThat.theOrder) return -1;
		if (theOrder > myThat.theOrder) return  1;
		
		/* Compare on name */
		result = getName().compareTo(myThat.getName());
		if (result < 0) return -1;
		if (result > 0) return 1;
		
		/* Compare on id */
		result = (int)(getId() - myThat.getId());
		if (result == 0) return 0;
		else if (result < 0) return -1;
		else return 1;
	}

	/**
	 * Determine whether we should add tax credits to the total
	 * 
	 * @return <code>true</code> if we should add tax credits to the total, <code>false</code> otherwise.
	 */
	public boolean hasTaxCredits() { 
		switch (theClass) {
			case GROSSSALARY:
			case GROSSINTEREST:
			case GROSSDIVIDEND:
			case GROSSUTDIVS:
			case GROSSTAXGAINS:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Determine whether we this is the tax paid bucket
	 * 
	 * @return <code>true</code> if we should add tax credits to the total, <code>false</code> otherwise.
	 */
	public boolean isTaxPaid() { 
		switch (theClass) {
			case TAXPAID:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Represents a list of {@link TaxType} objects. 
	 */
	public static class List extends DataList<TaxType> {
	 	/** 
	 	 * Construct an empty CORE tax type list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(DataSet pData) { super(ListStyle.CORE, true); }

		/** 
	 	 * Construct a generic tax type list
	 	 * @param pList the source tax type list 
	 	 * @param pStyle the style of the list 
	 	 */
		public List(List pList, ListStyle pStyle) { super(pList, pStyle); }

		/** 
	 	 * Construct a difference tax type list
	 	 * @param pNew the new TaxType list 
	 	 * @param pOld the old TaxType list 
	 	 */
		protected List(List pNew, List pOld) { super(pNew, pOld); }
		
		/** 
	 	 * Clone a TaxType list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return new List(this, ListStyle.CORE); }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pItem item to be added
		 * @return the newly added item
		 */
		public DataItem addNewItem(DataItem pItem) {
			TaxType myType = new TaxType(this, (TaxType)pItem);
			myType.addToList();
			return myType;
		}
	
		/**
		 * Create a new empty element in the edit list (null-operation)
		 * @param isCredit - is the item a credit or debit
		 */
		public void addNewItem(boolean isCredit) {};
			
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }
		
		/**
		 * Search for a particular item by class
		 *  
		 * @param eClass The class of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		public TaxType searchFor(TaxClass eClass) {
			ListIterator 	myIterator;
			TaxType 		myCurr;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				if (myCurr.theClass == eClass) break;
			}
			
			/* Return */
			return myCurr;
		}
		
		/**
		 * Search for a particular item by Name
		 * 
		 * @param sName The name of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		protected TaxType searchFor(String sName) {
			ListIterator 	myIterator;
			TaxType 		myCurr;
			int     		iDiff;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				iDiff = sName.compareTo(myCurr.getName());
				if (iDiff == 0) break;
			}
			
			/* Return to caller */
			return myCurr;
		}
		
		/**
		 * Add a TaxType
		 * @param uId the Id of the tax type
		 * @param pTaxType the Name of the tax type
		 */ 
		public void addItem(long   uId,
				            String pTaxType) throws Exception {
			TaxType      myTaxType;
			
			/* Create a new Tax Type */
			myTaxType = new TaxType(this, uId, pTaxType);
			
			/* Check that this TaxTypeId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxType,
			  			            "Duplicate TaxTypeId");
				 
			/* Check that this TaxType has not been previously added */
			if (searchFor(pTaxType) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxType,
			                        "Duplicate Tax Type");
				
			/* Add the Tax Type to the list */
			myTaxType.addToList();
		}			
	}
		
	/**
	 * Values for a tax type 
	 */
	public class Values implements histObject {
		private String     		theName      = null;
		
		/* Access methods */
		public String      	getName()      { return theName; }
		
		public void setName(String pName) {
			theName      = pName; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) {
			theName      = pValues.getName();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (Utils.differs(theName,    pValues.theName))    return false;
			return true;
		}
		
		/* Copy values */
		public void    copyFrom(histObject pSource) {
			Values myValues = (Values)pSource;
			copyFrom(myValues);
		}
		public histObject copySelf() {
			return new Values(this);
		}
		public void    copyFrom(Values pValues) {
			theName      = pValues.getName();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case FIELD_NAME:
					bResult = (Utils.differs(theName,      pValues.theName));
					break;
			}
			return bResult;
		}
	}
	
	/**
	 * Enumeration of Tax Type Classes. 
	 */
	public enum TaxClass {
		/**
		 * Gross Salary Income
		 */
		GROSSSALARY,

		/**
		 * Gross Interest Income
		 */
		GROSSINTEREST,

		/**
		 * Gross Dividend Income
		 */
		GROSSDIVIDEND,

		/**
		 * Gross Unit Trust Dividend Income
		 */
		GROSSUTDIVS,

		/**
		 * Gross Rental Income
		 */
		GROSSRENTAL,

		/**
		 * Gross Taxable gains
		 */
		GROSSTAXGAINS,

		/**
		 * Gross Capital gains
		 */
		GROSSCAPGAINS,

		/**
		 * Total Tax Paid
		 */
		TAXPAID,

		/**
		 * Market Growth/Shrinkage
		 */
		MARKET,     

		/**
		 * Tax Free Income
		 */
		TAXFREE,

		/**
		 * Gross Expense
		 */
		EXPENSE,

		/**
		 * Profit on Year
		 */
		PROFIT,
		
		/**
		 * Profit on year after ignoring market movements and inheritance
		 */
		COREPROFIT,
		
		/**
		 * Gross Income
		 */
		GROSSINCOME,

		/**
		 * Original Allowance
		 */
		ORIGALLOW,

		/**
		 * Adjusted Allowance
		 */
		ADJALLOW,

		/**
		 * High Tax Band
		 */
		HITAXBAND,

		/**
		 * Salary at nil-rate
		 */
		SALARYFREE,

		/**
		 * Salary at low-rate
		 */
		SALARYLO,

		/**
		 * Salary at basic-rate
		 */
		SALARYBASIC,

		/**
		 * Salary at high-rate
		 */
		SALARYHI,

		/**
		 * Salary at additional-rate
		 */
		SALARYADD,

		/**
		 * Rental at nil-rate
		 */
		RENTALFREE,

		/**
		 * Rental at low-rate
		 */
		RENTALLO,

		/**
		 * Rental at basic-rate
		 */
		RENTALBASIC,

		/**
		 * Rental at high-rate
		 */
		RENTALHI,

		/**
		 * Rental at additional-rate
		 */
		RENTALADD,

		/**
		 * Interest at nil-rate
		 */
		INTERESTFREE,

		/**
		 * Interest at low-rate
		 */
		INTERESTLO,

		/**
		 * Interest at basic-rate
		 */
		INTERESTBASIC,

		/**
		 * Interest at high-rate
		 */
		INTERESTHI,

		/**
		 * Interest at additional-rate
		 */
		INTERESTADD,

		/**
		 * Dividends at basic-rate
		 */
		DIVIDENDBASIC,

		/**
		 * Dividends at high-rate
		 */
		DIVIDENDHI,

		/**
		 * Dividends at additional-rate
		 */
		DIVIDENDADD,

		/**
		 * Slice at basic-rate
		 */
		SLICEBASIC,

		/**
		 * Slice at high-rate
		 */
		SLICEHI,

		/**
		 * Slice at additional-rate
		 */
		SLICEADD,

		/**
		 * Gains at basic-rate
		 */
		GAINSBASIC,

		/**
		 * Gains at high-rate
		 */
		GAINSHI,

		/**
		 * Gains at additional-rate
		 */
		GAINSADD,

		/**
		 * Capital at nil-rate
		 */
		CAPITALFREE,

		/**
		 * Capital at basic-rate
		 */
		CAPITALBASIC,

		/**
		 * Capital at high-rate
		 */
		CAPITALHI,

		/**
		 * Total Taxation Due on Salary
		 */
		TAXDUESALARY,

		/**
		 * Total Taxation Due on Rental
		 */
		TAXDUERENTAL,

		/**
		 * Total Taxation Due on Interest
		 */
		TAXDUEINTEREST,

		/**
		 * Total Taxation Due on Dividends
		 */
		TAXDUEDIVIDEND,

		/**
		 * Total Taxation Due on Taxable Gains
		 */
		TAXDUETAXGAINS,

		/**
		 * Total Taxation Due on Slice
		 */
		TAXDUESLICE,

		/**
		 * Total Taxation Due on Capital Gains
		 */
		TAXDUECAPGAINS,

		/**
		 * Total Taxation Due
		 */
		TOTALTAX,

		/**
		 * Taxation Profit (TaxDue-TaxPaid)
		 */
		TAXPROFIT;
	}
}
