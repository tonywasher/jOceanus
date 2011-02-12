package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number;

public class SpotPrices {
	/**
	 * The name of the object
	 */
	private static final String objName = "SpotPrice";

	/* Members */
	private View	theView		= null;
	private Date    theDate     = null;
	private Date    theNext     = null;
	private Date    thePrev     = null;
	private List	thePrices	= null;

	/* Access methods */
	public Date     getDate()    	{ return theDate; }
	public Date     getNext()    	{ return theNext; }
	public Date     getPrev()    	{ return thePrev; }
	public List 	getPrices()     { return thePrices; }
	public SpotPrice get(long uIndex) {
		return thePrices.get((int)uIndex); }
 	
 	/* Constructor */
	public SpotPrices(View pView, Date pDate) {
		/* Create a copy of the date and initiate the list */
		theView		= pView;
		theDate    	= pDate;
		thePrices  	= new List();
	}
	
	/** 
	 * Apply changes in a statement back into the underlying finance objects
	 */
	public void applyChanges() {
		Price.List  myBase;
		DataSet		myData;
		
		/* Access base details */
		myData	= theView.getData();
		myBase  = myData.getPrices();
		
		/* Apply the changes from this list */
		myBase.applyChanges(this);
		
		/* Analyse the data */
		theView.analyseData();
		
		/* Refresh windows */
		theView.refreshWindow();
	}
	
	/* The List class */
	public class List extends DataList<SpotPrice> {		
		/* Constructors */
		public List() { 
			super(ListStyle.SPOT, false);
			
			/* Declare variables */
			DataSet 	myData;
			Price 		myCurr;
			Price 		myLast;
			int			iDiff;
			boolean		isNew;
			boolean		isSet;
			Account 	myAcct;
			SpotPrice 	myPrice;
			
			DataList<Price>.ListIterator myIterator;
			
			/* Access the iterator */
			myData 		= theView.getData();
			myIterator 	= myData.getPrices().listIterator(true);
			myLast		= null;
			myAcct		= null;
			isSet		= false;
			
			/* Loop through the prices looking for this price */
			while ((myCurr = myIterator.next()) != null) {
				/* Test the date and account */
				iDiff 	= theDate.compareTo(myCurr.getDate()); 
				isNew	= Utils.differs(myAcct, myCurr.getAccount());
				
				/* If this is a new account */
				if (isNew) {
					/* If we have not set a price for this account */
					if ((myAcct != null) &&(!isSet)) {
						/* Create the new spot price and add it to the list */
						myPrice = new SpotPrice(this, myAcct, myLast);
						myPrice.addToList();
					}
					
					/* Record the account and note that we have no last value */
					myAcct	= myCurr.getAccount();
					myLast	= null;
					isSet	= false;
				}
				
				/* If this is prior to the required date */
				if (iDiff > 0) {
					/* Record nearest previous price point */
					if ((thePrev == null) ||
						(thePrev.compareTo(myCurr.getDate()) < 0))
						thePrev = myCurr.getDate();

					/* Record the last value for this account */
					if (!myCurr.isDeleted()) myLast = myCurr;
				}

				/* If we have found an exact match */
				if (iDiff == 0) {
					/* Create the new spot price and add it to the list */
					myPrice = new SpotPrice(this, myCurr, myLast);
					myPrice.addToList();
					isSet 	= true;
				}
				
				/* If this is past the required date */
				if (iDiff < 0) {
					/* If we have not set a price for this account */
					if (!isSet) {
						/* Create the new spot price and add it to the list */
						myPrice = new SpotPrice(this, myAcct, myLast);
						myPrice.addToList();
						isSet = true;
					}
					
					/* Record nearest subsequent price point */
					if ((theNext == null) ||
						(theNext.compareTo(myCurr.getDate()) > 0))
						theNext = myCurr.getDate();
				}
			}	

			/* If we have not set a price for this account */
			if ((myAcct != null) &&(!isSet)) {
				/* Create the new spot price and add it to the list */
				myPrice = new SpotPrice(this, myAcct, myLast);
				myPrice.addToList();
			}
		}
		
		/** 
	 	 * Clone a SpotView list (never used)
	 	 * @return <code>null</code>
	 	 */
		protected List cloneIt() { return null; }
		
		/* Is this list locked */
		public boolean isLocked() { return false; }
		
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
		public void addNewItem(boolean        isCredit) {}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }
		
		/** 
		 * Validate a spot price list
		 */
		public void validate() {
			DataList<SpotPrice>.ListIterator	myIterator;
			SpotPrice 							myCurr;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the list */
			while ((myCurr = myIterator.next()) != null) {
				/* Item is always valid */
				myCurr.setValidEdit();
			}
			
			/* Set the valid indication */
			setEditState(EditState.VALID);	
		}
	}
			
	public class SpotPrice 	extends DataItem  {
		/* Properties */
		private Account       	theAccount  	= null;
		private Number.Price	thePrevPrice	= null;
		private Date			thePrevDate		= null;
		
		/* Access methods */
		public Date        	getDate()      { return theDate; }
		public Account		getAccount()   { return theAccount; }
		public Values      	getObj()       { return (Values)super.getObj(); }
		public Number.Price getPrice()     { return getObj().getPrice(); }
		public Number.Price getPrevPrice() { return thePrevPrice; }
		public Date			getPrevDate()  { return thePrevDate; }
		
		/* Linking methods */
		public Price	 getBase() { return (Price)super.getBase(); }

		/* Field IDs */
		public static final int FIELD_ID       = 0;
		public static final int FIELD_ACCOUNT  = 1;
		public static final int FIELD_PRICE    = 2;
		public static final int NUMFIELDS	   = 3;
		
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
				case FIELD_ID: 	  	return "ID";
				case FIELD_ACCOUNT: return "Account";
				case FIELD_PRICE: 	return "Price";
				default:		  	return super.fieldName(iField);
			}
		}
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pObj the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, histObject pObj) {
			String 		myString = "";
			Values 	myObj 	 = (Values)pObj;
			switch (iField) {
				case FIELD_ID: 		
					myString += getId(); 
					break;
				case FIELD_ACCOUNT:	
					myString += theAccount.getName(); 
					break;
				case FIELD_PRICE: 	
					myString += Utils.formatPrice(myObj.getPrice());	
					break;
			}
			return myString;
		}
					
		/**
		 *  Constructor for a new SpotPrice 
		 *  @param pList the Spot Price List
		 *  @param pPrice the price for the date
		 *  @param pLast the last price for the account
		 */
		public SpotPrice(List pList, Price pPrice, Price pLast) {
			super(pList, 0);
	
			/* Variables */
			Values 							myObj = new Values();
			
			/* Store base values */
			setObj(myObj);
			theAccount 		= pPrice.getAccount();
			if (pLast != null) {
				thePrevPrice 	= pLast.getPrice();
				thePrevDate 	= pLast.getDate();
			}
				
			/* Set the price if it is not deleted */
			if (!pPrice.isDeleted()) setPrice(pPrice.getPrice());
			
			/* Link to base */
			setBase(pPrice);
			
			/* Set the state */
			setState(DataState.CLEAN);
			
			/* Note if the account is closed */
			if (theAccount.isClosed()) setHidden();
		}

		/**
		 *  Constructor for a new SpotPrice where no price data exists
		 *  @param pList the Spot Price List
		 *  @param pAccount the price for the date
		 *  @param pLast the last price for the account
		 */
		public SpotPrice(List pList, Account pAccount, Price pLast) {
			super(pList, 0);
	
			/* Variables */
			Values 							myObj = new Values();
			
			/* Store base values */
			setObj(myObj);
			theAccount 		= pAccount;
			if (pLast != null) {
				thePrevPrice 	= pLast.getPrice();
				thePrevDate 	= pLast.getDate();
			}
			
			/* Set the state */
			setState(DataState.CLEAN);
			
			/* Note if the account is closed */
			if (theAccount.isClosed()) setHidden();
		}
					
		/**
		 * Validate the line
		 */
		public void validate() { }

		/**
		 * Compare the price
		 */
		public boolean equals(Object that) { return (this == that); }
		
		/**
		 * Compare this price to another to establish sort order. 
		 * @param pThat The Price to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		public int compareTo(Object pThat) {
		
			/* Handle the trivial cases */
			if (this == pThat) return 0;
			if (pThat == null) return -1;
			
			/* Make sure that the object is a SpotPrice */
			if (pThat.getClass() != this.getClass()) return -1;
			
			/* Access the object as a SpotPrice */
			SpotPrice myThat = (SpotPrice)pThat;

			/* Compare the account */
			if (this.getAccount() == myThat.getAccount()) return 0;
			if (this.getAccount() == null) return 1;
			if (myThat.getAccount() == null) return -1;
			return getAccount().compareTo(myThat.getAccount());
		}
		
		/**
		 * Set a new price 
		 * 
		 * @param pPrice the new price 
		 */
		public void setPrice(Number.Price pPrice) {
			getObj().setPrice(pPrice);
		}
		
		/* SpotValues */
		public class Values implements histObject {
			private Number.Price		thePrice	= null;
			
			/* Access methods */
			public Number.Price		getPrice()     { return thePrice; }
			
			public void setPrice(Number.Price pPrice) {
				thePrice  = pPrice; }

			/* Constructor */
			public Values() {}
			public Values(Values pValues) {
				thePrice     = pValues.getPrice();
			}
			
			/* Check whether this object is equal to that passed */
			public boolean histEquals(histObject pCompare) {
				Values myValues = (Values)pCompare;
				return histEquals(myValues);
			}
			public boolean histEquals(Values pValues) {
				if (Utils.differs(thePrice,     pValues.thePrice))      return false;
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
				thePrice     = pValues.getPrice();
			}
			public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
				Values	pValues = (Values)pOriginal;
				boolean		bResult = false;
				switch (fieldNo) {
					case SpotPrices.SpotPrice.FIELD_PRICE:
						bResult = (Utils.differs(thePrice,  pValues.thePrice));
						break;
				}
				return bResult;
			}
		}		
	}
}
