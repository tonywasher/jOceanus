package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number;

public class ViewPrice extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "ViewPrice";

	/* Local values */
	private Account	theAccount	= null;
	private boolean	hasDilution	= false;
	
	/* Access methods */
	public  Values  			getObj()    		{ return (Values)super.getObj(); }
	public  Number.Price 		getPrice()  		{ return getObj().getPrice(); }
	public  Number.Dilution		getDilution()  		{ return getObj().getDilution(); }
	public  Number.DilutedPrice getDilutedPrice()  	{ return getObj().getDilutedPrice(); }
	public  Date  				getDate()			{ return getObj().getDate(); }
	public  Account				getAccount()		{ return theAccount; }
	public  void	setAccount(Account pAccount)	{ theAccount = pAccount; }
	
	/* Linking methods */
	public Price     getBase() { return (Price)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_ID       		= 0;
	public static final int FIELD_ACCOUNT  		= 1;
	public static final int FIELD_DATE     		= 2;
	public static final int FIELD_PRICE    		= 3;
	public static final int FIELD_DILUTION 		= 4;
	public static final int FIELD_DILUTEDPRICE 	= 5;
	public static final int NUMFIELDS	   		= 6;

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
			case FIELD_ID:				return "ID";
			case FIELD_ACCOUNT:			return "Account";
			case FIELD_DATE:			return "Date";
			case FIELD_PRICE:			return "Price";
			case FIELD_DILUTION:		return "Dilution";
			case FIELD_DILUTEDPRICE:	return "DilutedPrice";
			default:		  			return super.fieldName(iField);
		}
	}
				
	/**
	 * Format the value of a particular field as a table row
	 * @param iField the field number
	 * @param pObj the values to use
	 * @return the formatted field
	 */
	public String formatField(int iField, histObject pObj) {
		String 	myString = "<tr><td>" + fieldName(iField) + "</td><td>";
		Values 	myObj 	 = (Values)pObj;
		switch (iField) {
			case FIELD_ID: 		
				myString += getId(); 
				break;
			case FIELD_ACCOUNT:
				myString += Utils.formatAccount(getAccount()); 
				break;
			case FIELD_DATE:	
				myString += Utils.formatDate(getDate()); 
				break;
			case FIELD_PRICE:	
				myString += Utils.formatPrice(myObj.getPrice()); 
				break;
			case FIELD_DILUTION:	
				myString += Utils.formatDilution(getDilution()); 
				break;
			case FIELD_DILUTEDPRICE:	
				myString += Utils.formatDilutedPrice(myObj.getDilutedPrice()); 
				break;
		}
		return myString + "</td></tr>";
	}
							
	/**
 	* Construct a copy of a Price
 	* 
 	* @param pPrice The Price 
 	*/
	protected ViewPrice(List pList, Price pPrice) {
		/* Set standard values */
		super(pList, pPrice.getId());
		
		/* Create the history object */
		Values myObj = new Values();
		setObj(myObj);
		
		/* Set standard values */
		setAccount(pPrice.getAccount());
		hasDilution = pList.hasDilutions();
		myObj.setDate(pPrice.getDate());
		myObj.setPrice(pPrice.getPrice());
		
		/* If we have dilutions */
		if (hasDilution) {
			/* Determine the dilution factor for the date */
			Number.Dilution myDilution = pList.getDilutions().getDilutionFactor(theAccount, getDate());
			
			/* If we have a dilution factor */
			if (myDilution != null) {
				/* Store dilution details */
				myObj.setDilution(myDilution);
				myObj.setDilutedPrice(getPrice().getDilutedPrice(myDilution));
			}
		}
		
		/* Finish off */
		setBase(pPrice);
		setState(DataState.CLEAN);
	}

	/* Standard constructor for a newly inserted price */
	private ViewPrice(List pList) {
		super(pList, 0);
		Values myObj = new Values();
		setObj(myObj);
		setAccount(pList.theAccount);
		hasDilution = pList.hasDilutions();
		setState(DataState.NEW);
	}

	/**
	 * Compare this price to another to establish equality.
	 * 
	 * @param pThat The Price to compare to
	 * @return <code>true</code> if the tax year is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a Price */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a Price */
		ViewPrice myPrice = (ViewPrice)pThat;
		
		/* Check for equality */
		if (getId() != myPrice.getId()) return false;
		if (Utils.differs(getDate(),    myPrice.getDate())) 	return false;
		if (Utils.differs(getPrice(),   myPrice.getPrice())) 	return false;
		return true;
	}

	/**
	 * Compare this price to another to establish sort order. 
	 * @param pThat The Price to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		int iDiff;
		
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is an Price */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as a Price */
		ViewPrice myThat = (ViewPrice)pThat;

		/* If the date differs */
		if (this.getDate() != myThat.getDate()) {
			/* Handle null dates */
			if (this.getDate()   == null) return 1;
			if (myThat.getDate() == null) return -1;
			
			/* Compare the dates */
			iDiff = getDate().compareTo(myThat.getDate());
			if (iDiff != 0) return iDiff;
		}
		
		/* Compare the IDs */
		iDiff =(int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}
	
	/**
	 * Validate the price
	 * 
	 */
	public void validate() {
		Date 	myDate = getDate();
		List 	myList = (List)getList();
		DataSet	mySet  = myList.theData;
			
		/* The date must be non-null */
		if ((myDate == null) || (myDate.isNull())) {
			addError("Null Date is not allowed", FIELD_DATE);
		}
			
		/* else date is non-null */
		else {
			/* Date must be unique for this account */
			if (myList.countInstances(myDate, getAccount()) > 1) {
				addError("Date must be unique", FIELD_DATE);
			} 
			
			/* The date must be in-range */
			if (mySet.getDateRange().compareTo(myDate) != 0) {
				addError("Date must be within range", FIELD_DATE);
			}
		}
			
		/* The Price must be non-zero */
		if ((getPrice() == null) ||
			(!getPrice().isNonZero()) ||
			(!getPrice().isPositive())) {
			addError("Price must be non-Zero and positive", FIELD_PRICE);
		}						
		
		/* Set validation flag */
		if (!hasErrors()) setValidEdit();
	}

	/**
	 * Set a new price 
	 * 
	 * @param pPrice the price 
	 */
	public void setPrice(Number.Price pPrice) {
		getObj().setPrice((pPrice == null) ? null : new Number.Price(pPrice));
		
		/* If we have dilutions and a valid date/price */
		if ((getDate() != null) && (pPrice != null) && (hasDilution)) {
			ViewPrice.List myList = (ViewPrice.List)getList();
			
			/* Determine the dilution factor for the date */
			Number.Dilution myDilution = myList.getDilutions().getDilutionFactor(theAccount, getDate());
			
			/* If we have a dilution factor */
			if (myDilution != null) {
				/* Store dilution details */
				getObj().setDilution(myDilution);
				getObj().setDilutedPrice(getPrice().getDilutedPrice(myDilution));
			}
			
			/* else set null dilution */
			else {
				/* Store dilution details */
				getObj().setDilution(null);
				getObj().setDilutedPrice(null);				
			}
		}
		
		/* else set null dilution */
		else {
			/* Store dilution details */
			getObj().setDilution(null);
			getObj().setDilutedPrice(null);				
		}
	}

	/**s
	 * Set a new date 
	 * 
	 * @param pDate the new date 
	 */
	public void setDate(Date pDate) {
		/* Store date */
		getObj().setDate((pDate == null) ? null : new Date(pDate));
		
		/* If we have dilutions and a valid date/price */
		if ((pDate != null) && (getPrice() != null) && (hasDilution)) {
			ViewPrice.List myList = (ViewPrice.List)getList();
			
			/* Determine the dilution factor for the date */
			Number.Dilution myDilution = myList.getDilutions().getDilutionFactor(theAccount, getDate());
			
			/* If we have a dilution factor */
			if (myDilution != null) {
				/* Store dilution details */
				getObj().setDilution(myDilution);
				getObj().setDilutedPrice(getPrice().getDilutedPrice(myDilution));
			}
			
			/* else set null dilution */
			else {
				/* Store dilution details */
				getObj().setDilution(null);
				getObj().setDilutedPrice(null);				
			}
		}
		
		/* else set null dilution */
		else {
			/* Store dilution details */
			getObj().setDilution(null);
			getObj().setDilutedPrice(null);				
		}
	}

	/**
	 * Price List
	 */
	public static class List  extends DataList<ViewPrice> {
		/* Members */
		private Account				theAccount		= null;
		private DataSet				theData			= null;
		private DilutionEvent.List	theDilutions 	= null;
		private boolean				hasDilutions	= false;
		
		/* Access methods */
		public 	DataSet 			getData()		{ return theData; }
		private	DilutionEvent.List	getDilutions()	{ return theDilutions; }
		public	boolean				hasDilutions()	{ return hasDilutions; }

		/**
		 * Construct an edit extract of a Price list
		 * 
		 * @param pView      The master view
		 * @param pAccount	 The account to extract rates for 
		 */
		public List(View				pView,
					Account 			pAccount) {
			/* Make this list the correct style */
			super(ListStyle.EDIT, false);
			theData = pView.getData();

			/* Local variables */
			Price.List						myPrices;
			Price 							myCurr;
			ViewPrice 						myItem;
			DataList<Price>.ListIterator 	myIterator;

			/* Skip to alias if required */
			if ((pAccount != null) && (pAccount.getAlias() != null))
				pAccount = pAccount.getAlias();
			
			/* Access the base prices */
			myPrices = theData.getPrices();
			
			/* Store the account */
			theAccount = pAccount;

			/* Store dilution list and record whether we have dilutions */
			theDilutions = pView.getDilutions();
			hasDilutions = theDilutions.hasDilution(pAccount);
			
			/* Access the list iterator */
			myIterator = myPrices.listIterator(true);
			
			/* Loop through the list */
			while ((myCurr = myIterator.next()) != null) {
				/* If this item belongs to the account */
				if (!Utils.differs(myCurr.getAccount(), pAccount)) {
					/* Copy the item */
					myItem = new ViewPrice(this, myCurr);
					myItem.addToList();
				}
			}
		}

		/** 
		 * 	Clone a Price list
		 * @return the cloned list
		 */
		protected List cloneIt() { return null; }

		/* Is this list locked */
		public boolean isLocked() { return ((theAccount != null) && (theAccount.isLocked())); }

		/**
		 * Add a new item (never used)
		 */
		public DataItem addNewItem(DataItem pElement) {
			return null;}
		
		/**
		 * Add a new item to the edit list
		 * 
		 * @param isCredit - ignored
		 */
		public void addNewItem(boolean        isCredit) {
			ViewPrice myPrice = new ViewPrice(this);
			myPrice.setAccount(theAccount);
			myPrice.addToList();
		}

		/**
		 * 	Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }

		/**
		 * Count the instances of a date
		 * 
		 * @param pDate the date
		 * @return The Item if present (or null)
		 */
		protected int countInstances(Date 		pDate,
									 Account	pAccount) {
			ListIterator 	myIterator;
			ViewPrice  		myCurr;
			int      		iDiff;
			int      		iCount = 0;

			/* Access the list iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				iDiff = pDate.compareTo(myCurr.getDate());
				if (iDiff == 0) iDiff = pAccount.compareTo(myCurr.getAccount());
				if (iDiff == 0) iCount++;
			}

			/* return to caller */
			return iCount;
		}

		/** 
		 * Apply changes in a Prices view back into the core data
		 */
		public void applyChanges() {
			Price.List myBase;
			
			/* Access base details */
			myBase     = theData.getPrices();
			
			/* Apply the changes */
			myBase.applyChanges(this);
			
			/*
			 * Analyse and refresh are performed in the statement view
			 */
		}
	}
	
	/**
	 * Values for a view price 
	 */
	public class Values implements histObject {
		private Date       			theDate      	= null;
		private Number.Price    	thePrice     	= null;
		private Number.Dilution		theDilution		= null;
		private Number.DilutedPrice	theDilutedPrice = null;
		
		/* Access methods */
		public Date       			getDate()      		{ return theDate; }
		public Number.Price			getPrice()     		{ return thePrice; }
		public Number.Dilution		getDilution()   	{ return theDilution; }
		public Number.DilutedPrice	getDilutedPrice()   { return theDilutedPrice; }
		
		public void setDate(Date pDate) {
			theDate      	= pDate; }
		public void setPrice(Number.Price pPrice) {
			thePrice     	= pPrice; }
		public void setDilution(Number.Dilution pDilution) {
			theDilution  	= pDilution; }
		public void setDilutedPrice(Number.DilutedPrice pDilutedPrice) {
			theDilutedPrice	= pDilutedPrice; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) {
			theDate      	= pValues.getDate();
			thePrice     	= pValues.getPrice();
			theDilution		= pValues.getDilution();
			theDilutedPrice	= pValues.getDilutedPrice();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (Utils.differs(theDate,    		pValues.theDate))    		return false;
			if (Utils.differs(thePrice,   		pValues.thePrice))   		return false;
			if (Utils.differs(theDilution, 		pValues.theDilution)) 		return false;
			if (Utils.differs(theDilutedPrice, 	pValues.theDilutedPrice))	return false;
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
			theDate         = pValues.getDate();
			thePrice        = pValues.getPrice();
			theDilution		= pValues.getDilution();
			theDilutedPrice	= pValues.getDilutedPrice();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case FIELD_DATE:
					bResult = (Utils.differs(theDate,      		pValues.theDate));
					break;
				case FIELD_PRICE:
					bResult = (Utils.differs(thePrice,    		pValues.thePrice));
					break;
				case FIELD_DILUTION:
					bResult = (Utils.differs(theDilution,   	pValues.theDilution));
					break;
				case FIELD_DILUTEDPRICE:
					bResult = (Utils.differs(theDilutedPrice,	pValues.theDilutedPrice));
					break;
			}
			return bResult;
		}
	}		
}
