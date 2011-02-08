package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class Frequency extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "Frequency";

	/**
	 * Frequency Name length
	 */
	public final static int NAMELEN = 50;

	/**
	 * The class of the Frequency
	 */
	private FreqClass  theClass = null;

	/**
	 * The sort order of the Frequency
	 */
	private int        theOrder = -1;
	
	/**
	 * Return the name of the Frequency
	 * @return the name
	 */
	public String    	getName()              { return getObj().getName(); }

	/**
	 * Return the sort order of the Frequency
	 * @return the order
	 */
	protected int       getOrder()             { return theOrder; }

	/**
	 * Return the Frequency class of the Frequency
	 * @return the class
	 */
	protected FreqClass getFrequency()         { return theClass; }
	
	/* Linking methods */
	public Frequency getBase() { return (Frequency)super.getBase(); }
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
	 * Construct a copy of a Frequency.
	 * 
	 * @param pList	The list to associate the Frequency with
	 * @param pFrequency The frequency to copy 
	 */
	protected Frequency(List  		pList,
			            Frequency 	pFrequency) { 
		super(pList, pFrequency.getId());
		Values myObj = new Values(pFrequency.getObj());
		setObj(myObj);
		theClass = pFrequency.theClass;
		setBase(pFrequency);
		setState(pFrequency.getState());
		theOrder = pFrequency.getOrder();
	}
	
	/**
	 * Construct a standard Frequency on load
	 * 
	 * @param pList	The list to associate the Frequency with
	 * @param uId   ID of Frequency
	 * @param sName Name of Frequency
	 * 
	 * @throws {@link Exception} if type is not supported
	 */
	private Frequency(List 	 pList,
			          long   uId,
			          String sName) throws Exception {
		super(pList, uId);
		Values myObj = new Values();
		setObj(myObj);
		myObj.setName(sName);
		pList.setNewId(this);				
	
		/* Determine class of Frequency */
		if (sName.equals("Monthly")) {
			theClass = FreqClass.MONTHLY;
			theOrder = 0;
		}
		else if (sName.equals("EndOfMonth")) {
			theClass = FreqClass.ENDMONTH;
			theOrder = 1;
		}
		else if (sName.equals("Quarterly")) {
			theClass = FreqClass.QUARTERLY;
			theOrder = 2;
		}
		else if (sName.equals("HalfYearly")) {
			theClass = FreqClass.HALFYEARLY;
			theOrder = 3;
		}
		else if (sName.equals("Annually")) {
			theClass = FreqClass.ANNUALLY;
			theOrder = 4;
		}
		else if (sName.equals("Maturity")) {
			theClass = FreqClass.MATURITY;
			theOrder = 5;
		}
		else if (sName.equals("TenMonths")) {
			theClass = FreqClass.TENMONTHS;
			theOrder = 6;
		}
		else {
			throw new Exception(ExceptionClass.DATA,
  					  			this,
                                "Invalid Frequency");
		}
	}
	
	/**
	 * Compare this frequency to another to establish equality.
	 * 
	 * @param that The Frequency to compare to
	 * @return <code>true</code> if the frequency is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a Frequency */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target Frequency */
		Frequency myFreq = (Frequency)pThat;

		if (getId() != myFreq.getId()) return false;
		return (getName().compareTo(myFreq.getName()) == 0);
	}

	/**
	 * Compare this frequency to another to establish sort order.
	 * 
	 * @param pThat The Frequency to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		long result;

		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a Frequency */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the target frequency */
		Frequency myThat = (Frequency)pThat;
		
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
	 * Represents a list of {@link Frequency} objects. 
	 */
	public static class List  extends DataList<Frequency> {
	 	/** 
	 	 * Construct an empty CORE frequency list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(DataSet pData) { super(ListStyle.CORE, true); }

		/** 
	 	 * Construct a generic frequency list
	 	 * @param pList the source Frequency list 
	 	 * @param pStyle the style of the list 
	 	 */
		public List(List pList, ListStyle pStyle) { super(pList, pStyle); }

		/** 
	 	 * Construct a difference frequency list
	 	 * @param pNew the new Frequency list 
	 	 * @param pOld the old Frequency list 
	 	 */
		protected List(List pNew, List pOld) { super(pNew, pOld); }
		
		/** 
	 	 * Clone a Frequency list
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
			Frequency myFreq = new Frequency(this, (Frequency)pItem);
			myFreq.addToList();
			return myFreq;
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
		protected Frequency searchFor(FreqClass eClass) {
			ListIterator 	myIterator;
			Frequency 		myCurr;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				if (myCurr.theClass == eClass) break;
			}
			
			/* Return to caller */
			return myCurr;
		}
		
		/**
		 * Search for a particular item by Name
		 * 
		 * @param sName The name of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		public Frequency searchFor(String sName) {
			ListIterator 	myIterator;
			Frequency   	myCurr;
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
		 * Add a Frequency
		 * @param uId the Id of the frequency
		 * @param pFrequency the Name of the frequency
		 * @throws Exception on error
		 */ 
		public void addItem(long   uId,
				            String pFrequency) throws Exception {
			Frequency myFrequency;
			
			/* Create a new Frequency */
			myFrequency = new Frequency(this, uId, pFrequency);
				
			/* Check that this FrequencyId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myFrequency,
			  			            "Duplicate FrequencyId");
				 
			/* Check that this Frequency has not been previously added */
			if (searchFor(pFrequency) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myFrequency,
			                        "Duplicate Frequency");
				
			/* Add the Frequency to the list */
			myFrequency.addToList();		
		}		
	}
	
	/**
	 * Values for a frequency 
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
	 * Enumeration of Frequency Classes. 
	 */
	protected enum FreqClass {
		/**
		 * Monthly Frequency
		 */
		MONTHLY,

		/**
		 * Monthly Frequency (at end of month)
		 */
		ENDMONTH,

		/**
		 * Quarterly Frequency
		 */
		QUARTERLY,
		
		/**
		 * Half Yearly Frequency
		 */
		HALFYEARLY,

		/**
		 * Annual Frequency
		 */
		ANNUALLY,

		/**
		 * Only on Maturity
		 */
		MATURITY,

		/**
		 * Monthly for up to ten-months
		 */
		TENMONTHS;     
	}
}
