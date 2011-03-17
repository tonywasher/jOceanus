package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class TaxRegime extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "TaxRegime";

	/**
	 * TaxRegime Name length
	 */
	public final static int NAMELEN = 50;

	/**
	 * The class of the TaxRegime
	 */
	private TaxRegClass  theClass = null;

	/**
	 * The sort order of the TaxRegime
	 */
	private int        theOrder = -1;
	
	/**
	 * Return the name of the TaxRegime
	 * @return the name
	 */
	public String    	getName()              { return getObj().getName(); }

	/**
	 * Return the sort order of the Frequency
	 * @return the order
	 */
	protected int       getOrder()             { return theOrder; }

	/**
	 * Return the TaxRegime class of the TaxRegime
	 * @return the class
	 */
	protected TaxRegClass getRegime()         { return theClass; }
	
	/* Linking methods */
	public TaxRegime getBase() { return (TaxRegime)super.getBase(); }
	public Values  	 getObj()  { return (Values)super.getObj(); }	

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
		String myString = ""; 
		switch (iField) {
			case FIELD_ID: 		myString += getId();  	break;
			case FIELD_NAME:	myString += getName(); 	break;
			case FIELD_ORDER: 	myString += getOrder();	break;
			case FIELD_CLASS: 	myString += theClass;	break;
		}
		return myString;
	}
	
	/**
	 * Construct a copy of a TaxRegime.
	 * 
	 * @param pList	The list to associate the TaxRegime with
	 * @param pTaxRegime The TaxRegime to copy 
	 */
	protected TaxRegime(List  		pList,
			            TaxRegime   pTaxRegime) { 
		super(pList, pTaxRegime.getId());
		Values myObj = new Values(pTaxRegime.getObj());
		setObj(myObj);
		theClass = pTaxRegime.theClass;
		setBase(pTaxRegime);
		setState(pTaxRegime.getState());
		theOrder = pTaxRegime.getOrder();
	}
	
	/**
	 * Construct a standard TaxRegime on load
	 * 
	 * @param pList	The list to associate the TaxRegime with
	 * @param uId   ID of TaxRegime
	 * @param sName Name of TaxRegime
	 */
	private TaxRegime(List 	 pList,
			          int	 uId,
			          String sName) throws Exception {
		super(pList, uId);
		Values myObj = new Values();
		setObj(myObj);
		myObj.setName(sName);
		pList.setNewId(this);				
	
		/* Determine class of TaxRegime */
		if (sName.equals("Archive")) {
			theClass = TaxRegClass.ARCHIVE;
			theOrder = 0;
		}
		else if (sName.equals("Standard")) {
			theClass = TaxRegClass.STANDARD;
			theOrder = 0;
		}
		else if (sName.equals("LoInterest")) {
			theClass = TaxRegClass.LOINTEREST;
			theOrder = 1;
		}
		else if (sName.equals("AdditionalBand")) {
			theClass = TaxRegClass.ADDITIONAL;
			theOrder = 2;
		}
		else {
			throw new Exception(ExceptionClass.DATA,
  					  			this,
                                "Invalid TaxRegime");
		}
	}
	
	/**
	 * Compare this regime to another to establish equality.
	 * 
	 * @param pThat The Regime to compare to
	 * @return <code>true</code> if the regime is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a TaxRegime */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target transactionType */
		TaxRegime myRegime = (TaxRegime)pThat;

		if (getId() != myRegime.getId()) return false;
		return (getName().compareTo(myRegime.getName()) == 0);
	}

	/**
	 * Compare this tax regime to another to establish sort order.
	 * 
	 * @param pThat The Tax Regime to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		long result;

		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a TaxRegime */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the target TaxRegime */
		TaxRegime myThat = (TaxRegime)pThat;
		
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
	 * Determine whether this tax regime supports a Lo Salary Band
	 * @return <code>true/false</code>
	 */
	public  boolean         hasLoSalaryBand() {
		switch (theClass) {
			case ARCHIVE: 	return true;
			case STANDARD: 	return true;
			default: 		return false;
		}
	}
	
	/**
	 * Determine whether this tax regime treats capital gains as standard income
	 * @return <code>true/false</code>
	 */
	public  boolean         hasCapitalGainsAsIncome() {
		switch (theClass) {
			case STANDARD: 	return true;
			default: 		return false;
		}
	}
	
	/**
	 * Determine whether this tax regime supports an additional taxation band
	 * @return <code>true/false</code>
	 */
	public  boolean         hasAdditionalTaxBand() {
		switch (theClass) {
			case ADDITIONAL: 	return true;
			default: 			return false;
		}
	}

	/**
	 * Format a TaxRegime 
	 * 
	 * @param pRegime the tax regime to format
	 * @return the formatted tax regime
	 */
	public static String format(TaxRegime pRegime) {
		String 	myFormat;
		myFormat = (pRegime != null) ? pRegime.getName()
							      	 : "null";
		return myFormat;
	}

	/**
	 * Determine whether two TaxRegime objects differ.
	 * 
	 * @param pCurr The current TaxRegime
	 * @param pNew The new TaxRegime
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(TaxRegime pCurr, TaxRegime pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}

	/**
	 * Represents a list of {@link TaxRegime} objects. 
	 */
	public static class List  extends DataList<TaxRegime> {			
	 	/** 
	 	 * Construct an empty CORE tax regime list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(DataSet pData) { super(ListStyle.CORE, true); }

		/** 
	 	 * Construct a generic tax regime list
	 	 * @param pList the source Tax Regime list 
	 	 * @param pStyle the style of the list 
	 	 */
		public List(List pList, ListStyle pStyle) { super(pList, pStyle); }

		/** 
	 	 * Construct a difference tax regime list
	 	 * @param pNew the new TaxRegime list 
	 	 * @param pOld the old TaxRegime list 
	 	 */
		protected List(List pNew, List pOld) { super(pNew, pOld); }
		
		/** 
	 	 * Clone a TaxRegime list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return new List(this, ListStyle.CORE); }
		
		/**
		 * Add a new item to the list
		 * @param pItem item to be added
		 * @return the newly added item
		 */
		public TaxRegime addNewItem(DataItem pItem) {
			TaxRegime myRegime = new TaxRegime(this, (TaxRegime)pItem);
			myRegime.addToList();
			return myRegime;
		}
	
		/**
		 * Create a new empty element in the edit list (null-operation)
		 * @param isCredit - is the item a credit or debit
		 * @return the newly added item
		 */
		public TaxRegime addNewItem(boolean isCredit) { return null; }
			
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }
				
		/**
		 * Search for a particular item by Name
		 * 
		 * @param sName The name of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		public TaxRegime searchFor(String sName) {
			ListIterator 	myIterator;
			TaxRegime   	myCurr;
			int         	iDiff;
			
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
		 * Add a TaxRegime
		 * @param uId the Id of the tax regime
		 * @param pTaxRegime the Name of the tax regime
		 */ 
		public void addItem(int    uId,
				            String pTaxRegime) throws Exception {
			TaxRegime     myTaxRegime;
			
			/* Create a new tax regime */
			myTaxRegime = new TaxRegime(this, uId, pTaxRegime);
				
			/* Check that this TaxRegimeId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxRegime,
			  			            "Duplicate TaxRegimeId");
				 
			/* Check that this TaxRegime has not been previously added */
			if (searchFor(pTaxRegime) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxRegime,
			                        "Duplicate TaxRegime");
				
			/* Add the TaxRegime to the list */
			myTaxRegime.addToList();		
		}			
	}
	
	/**
	 * Values for a tax regime 
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
	 * Enumeration of TaxRegime Classes. 
	 */
	protected enum TaxRegClass {
		/**
		 * Archive tax regime
		 */
		ARCHIVE,

		/**
		 * Standard tax regime
		 */
		STANDARD,

		/**
		 * Low Interest Tax Band
		 */
		LOINTEREST,

		/**
		 * Additional tax band
		 */
		ADDITIONAL,		
	}	
}
