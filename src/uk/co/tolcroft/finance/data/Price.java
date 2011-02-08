package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.finance.views.SpotPrices;
import uk.co.tolcroft.finance.views.SpotPrices.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.Number;

public class Price extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "Price";

	/* Local values */
	private long 	theAccountId	= -1;
	
	/* Access methods */
	public  Values  		getObj()    	{ return (Values)super.getObj(); }
	public  Number.Price 	getPrice()  	{ return getObj().getPrice(); }
	public  Date  			getDate()		{ return getObj().getDate(); }
	public  Account			getAccount()	{ return getObj().getAccount(); }
	private void    		setAccount(Account pAccount)   {
		getObj().setAccount(pAccount); }

	/* Linking methods */
	public Price     getBase() { return (Price)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_ID       = 0;
	public static final int FIELD_ACCOUNT  = 1;
	public static final int FIELD_DATE     = 2;
	public static final int FIELD_PRICE    = 3;
	public static final int NUMFIELDS	   = 4;

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
			case FIELD_ID:			return "ID";
			case FIELD_ACCOUNT:		return "Account";
			case FIELD_DATE:		return "Date";
			case FIELD_PRICE:		return "Price";
			default:		  		return super.fieldName(iField);
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
				if ((getAccount() == null) &&
					(theAccountId != -1))
					myString += "Id=" + theAccountId;
				else
					myString += Utils.formatAccount(getAccount()); 
				break;
			case FIELD_DATE:	
				myString += Utils.formatDate(getDate()); 
				break;
			case FIELD_PRICE:	
				myString += Utils.formatPrice(myObj.getPrice()); 
				break;
		}
		return myString + "</td></tr>";
	}
							
	/**
 	* Construct a copy of a Price
 	* 
 	* @param pPrice The Price 
 	*/
	protected Price(List pList, Price pPrice) {
		/* Set standard values */
		super(pList, pPrice.getId());
		Values myObj = new Values(pPrice.getObj());
		setObj(myObj);

		/* Switch on the LinkStyle */
		switch (pList.getStyle()) {
			case CORE:
				pList.setNewId(this);				
				break;
			case EDIT:
				setBase(pPrice);
				setState(DataState.CLEAN);
				break;
			case UPDATE:
				setBase(pPrice);
				setState(pPrice.getState());
				break;
		}
	}

	/* Standard constructor for a newly inserted price */
	private Price(List pList) {
		super(pList, 0);
		Values myObj = new Values();
		setObj(myObj);
		setState(DataState.NEW);
	}

	/* Standard constructor */
	private Price(List       		pList,
			      long           	uId, 
				  long 		    	uAccountId,
			      java.util.Date 	pDate, 
			      String 			pPrice) throws Exception {
		/* Initialise the item */
		super(pList, uId);
		
		/* Initialise the values */
		Values myObj = new Values();
		setObj(myObj);
		
		/* Record the Id */
		theAccountId = uAccountId;
		
		/* Look up the Account */
		myObj.setAccount(pList.theData.getAccounts().searchFor(uAccountId));
		if (getAccount() == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Account Id");
					
		/* Record the date */
		myObj.setDate(new Date(pDate));
		
		/* Record the price */
		Number.Price myPrice = Number.Price.Parse(pPrice);
		if (myPrice == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Price: " + pPrice);
		myObj.setPrice(myPrice);
		
		/* Allocate the id */
		pList.setNewId(this);				
	}

	/* Special price constructor for diluted prices */
	private Price(List       	pList,
				  Account	    pAccount,
			      Date 			pDate, 
			      Number.Price	pPrice) {
		/* Initialise the item */
		super(pList, 0);
		
		/* Initialise the values */
		Values myObj = new Values();
		setObj(myObj);
		
		/* Record the Id */
		theAccountId = pAccount.getId();
		
		/* Set the passed details */
		myObj.setAccount(pAccount);
		myObj.setDate(pDate);
		myObj.setPrice(pPrice);
		
		/* Allocate the id */
		pList.setNewId(this);				
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
		Price myPrice = (Price)pThat;
		
		/* Check for equality */
		if (getId() != myPrice.getId()) return false;
		if (Utils.differs(getAccount(),	myPrice.getAccount())) 	return false;
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
		Price myThat = (Price)pThat;

		/* Compare the accounts */
		iDiff = getAccount().compareTo(myThat.getAccount());
		if (iDiff != 0) return iDiff;

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
	}

	/**
	 * Set a new date 
	 * 
	 * @param pDate the new date 
	 */
	public void setDate(Date pDate) {
		getObj().setDate((pDate == null) ? null : new Date(pDate));
	}

	/**
	 * Update Price from an item Element 
	 * 
	 * @param pItem the price extract 
	 */
	public void applyChanges(DataItem pItem) {
		if (pItem instanceof Price) {
			Price myPrice = (Price)pItem;
			applyChanges(myPrice);
		}
		else if (pItem instanceof SpotPrice) {
			SpotPrice mySpot = (SpotPrice)pItem;
			applyChanges(mySpot);
		}
	}
	
	/**
	 * Update Price from a Price extract 
	 * 
	 * @param pPrice the price extract 
	 */
	public void applyChanges(Price pPrice) {
		/* Store the current detail into history */
		pushHistory();
		
		/* Update the price if required */
		if (Utils.differs(getPrice(), pPrice.getPrice())) 
			setPrice(pPrice.getPrice());
	
		/* Update the date if required */
		if (Utils.differs(getDate(), pPrice.getDate())) 
			setDate(pPrice.getDate());
		
		/* Check for changes */
		if (checkForHistory()) setState(DataState.CHANGED);
	}
	
	/**
	 * Update Price from a Price extract 
	 * 
	 * @param pPrice the price extract 
	 */
	public void applyChanges(SpotPrice pPrice) {			
		/* If we are setting a null price */
		if (pPrice == null) {
			/* We are actually deleting the price */
			setState(DataState.DELETED);
		}
		
		/* else we have a price to set */
		else {
			/* Store the current detail into history */
			pushHistory();
		
			/* Update the price if required */
			if (Utils.differs(getPrice(), pPrice.getPrice())) 
				setPrice(pPrice.getPrice());
	
			/* Check for changes */
			if (checkForHistory()) setState(DataState.CHANGED);
		}
	}

	/**
	 * Price List
	 */
	public static class List  extends DataList<Price> {
		/* Members */
		private Account	  	theAccount	= null;
		private DataSet		theData		= null;

		/** 
		 * Construct an empty CORE price list
	 	 * @param pData the DataSet for the list
		 */
		protected List(DataSet pData) { 
			super(ListStyle.CORE, false);
			theData = pData;
		}

		/** 
		 * Construct an empty generic price list
	 	 * @param pData the DataSet for the list
		 * @param pStyle the style of the list 
		 */
		protected List(DataSet pData, ListStyle pStyle) {
			super(pStyle, false);
			theData = pData;
		}

		/** 
		 * Construct a generic Price list
		 * @param pList the source price list 
		 * @param pStyle the style of the list 
		 */
		public List(List pList, ListStyle pStyle) { 
			super(pList, pStyle);
			theData = pList.theData;
		}

		/** 
		 * Construct a difference price list
		 * @param pNew the new Price list 
		 * @param pOld the old Price list 
		 */
		protected List(List pNew, List pOld) { 
			super(pNew, pOld);
			theData = pNew.theData;
		}

		/**
		 * Construct an edit extract of a Price list
		 * 
		 * @param pList      The list to extract from
		 * @param pAccount	 The account to extract rates for 
		 */
		public List(List 	pList,
					Account pAccount) {
			/* Make this list the correct style */
			super(ListStyle.EDIT, false);
			theData = pList.theData;

			/* Local variables */
			Price 			myCurr;
			Price 			myItem;
			ListIterator 	myIterator;

			/* Skip to alias if required */
			if ((pAccount != null) && (pAccount.getAlias() != null))
				pAccount = pAccount.getAlias();
			
			/* Store the account */
			theAccount = pAccount;

			/* Access the list iterator */
			myIterator = pList.listIterator(true);
			
			/* Loop through the list */
			while ((myCurr = myIterator.next()) != null) {
				/* If this item belongs to the account */
				if (!Utils.differs(myCurr.getAccount(), pAccount)) {
					/* Copy the item */
					myItem = new Price(this, myCurr);
					myItem.addToList();
				}
			}
		}

		/** 
		 * 	Clone a Price list
		 * @return the cloned list
		 */
		protected List cloneIt() {return new List(this, ListStyle.CORE); }

		/* Is this list locked */
		public boolean isLocked() { return ((theAccount != null) && (theAccount.isLocked())); }

		/**
		 * Add a new item to the core list
		 * 
		 * @param pPrice item
		 * @return the newly added item
		 */
		public DataItem addNewItem(DataItem pPrice) {
			Price myPrice = new Price(this, (Price)pPrice);
			myPrice.addToList();
			return myPrice;
		}

		/**
		 * Add a new item to the edit list
		 * 
		 * @param isCredit - ignored
		 */
		public void addNewItem(boolean        isCredit) {
			Price myPrice = new Price(this);
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
			Price    		myCurr;
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
		 *  Obtain the most relevant price for a Date
		 *  
		 *   @param pDate the date from which a price is required
		 *   @return The relevant Price record 
		 */
		public Price getLatestPrice(Account pAccount,
									Date 	pDate) {
			ListIterator 	myIterator;
			Price 			myPrice = null;
			Price 			myCurr;

			/* Skip to alias if required */
			if (pAccount.getAlias() != null)
				pAccount = pAccount.getAlias();
			
			/* Access the list iterator */
			myIterator = listIterator();
			
			/* Loop through the Prices */
			while ((myCurr = myIterator.next()) != null) {
				/* Skip records that do not belong to this account */
				if (Utils.differs(myCurr.getAccount(), pAccount))
					continue;

				/* break loop if we have passed the date */
				if (myCurr.getDate().compareTo(pDate) > 0) break;

				/* Record the best case so far */
				myPrice = myCurr;
			}

			/* Return the price */
			return myPrice;
		}

		/**
		 *  Mark active prices
		 */
		protected void markActivePrices () {
			ListIterator 	myIterator;
			Price 			myCurr;

			/* Access the list iterator */
			myIterator = listIterator(true);
			
			/* Loop through the Prices */
			while ((myCurr = myIterator.next()) != null) {
				/* mark the account referred to */
				myCurr.getAccount().touchPrice(); 
			}
		}

		/**
		 * Apply changes from a Spot Price list
		 */
		public void applyChanges(SpotPrices pPrices) {
			DataList<SpotPrice>.ListIterator	myIterator;
			SpotPrices.List 	 				myList;
			SpotPrice 							mySpot;
			Date								myDate;
			Number.Price						myPoint;
			Price								myPrice;

			/* Access details */
			myDate = pPrices.getDate();
			myList = pPrices.getPrices();
			
			/* Access the iterator */
			myIterator = myList.listIterator();
			
			/* Loop through the spot prices */
			while ((mySpot  = myIterator.next()) != null) {
				/* Access the price for this date if it exists */
				myPrice 	= mySpot.getBase();
				myPoint 	= mySpot.getPrice();

				/* If the state is not clean */
				if (mySpot.getState() != DataState.CLEAN) {
					/* If we have an underlying price */
					if (myPrice != null) {
						/* Apply changes to the underlying entry */
						myPrice.applyChanges(mySpot);
					}

					/* else if we have a new price with no underlying */
					else if (myPoint != null) {
						/* Create the new Price */
						myPrice = new Price(this);

						/* Set the date and price */
						myPrice.setDate(new Date(myDate));
						myPrice.setPrice(new Number.Price(myPoint));
						myPrice.setAccount(mySpot.getAccount());

						/* Add to the list and link backwards */
						mySpot.setBase(myPrice);
						myPrice.addToList();
					}

					/* Clear history and set as a clean item */
					mySpot.clearHistory();
					mySpot.setState(DataState.CLEAN);					
				}
			}
		}
		
		/**
		 *  Add a Price
		 */
		public void addItem(long     		uId,
							java.util.Date  pDate,
	            			String   		pAccount,
				            String   		pPrice) throws Exception {
			Account     	myAccount;
			Account.List	myAccounts;
			
			/* Access the Accounts */
			myAccounts = theData.getAccounts();
			
			/* Look up the Account */
			myAccount = myAccounts.searchFor(pAccount);
			if (myAccount == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Price on [" + 
			                        Utils.formatDate(new Date(pDate)) +
			                        "] has invalid Account [" +
			                        pAccount + "]");
									
			/* Add the price */
			addItem(uId,
					pDate,
					myAccount.getId(),
					pPrice);
		}
			
		/**
		 *  Allow a price to be added
		 */
		public void addItem(long     		uId,
				            java.util.Date  pDate,
				            long     		uAccountId,
				            String   		pPrice) throws Exception {
			Price     	myPrice;
			
			/* Create the price and PricePoint */
			myPrice = new Price(this, uId, uAccountId, pDate, pPrice);
			
			/* Check that this PriceId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
									myPrice,
									"Duplicate PriceId <" + uId + ">");
			 
			/* Validate the price */
			myPrice.validate();

			/* Handle validation failure */
			if (myPrice.hasErrors()) 
				throw new Exception(ExceptionClass.VALIDATE,
									myPrice,
									"Failed validation");
				
			/* Add to the list */
			myPrice.addToList();
		}			

		/**
		 *  Allow a price to be added
		 */
		public void addItem(Account 		pAccount,
							Date  			pDate,
				            Number.Price	pPrice) throws Exception {
			Price     	myPrice;
			
			/* Create the price and PricePoint */
			myPrice = new Price(this, pAccount, pDate, pPrice);
			
			/* Validate the price */
			myPrice.validate();

			/* Handle validation failure */
			if (myPrice.hasErrors()) 
				throw new Exception(ExceptionClass.VALIDATE,
									myPrice,
									"Failed validation");
				
			/* Add to the list */
			myPrice.addToList();
		}			
	}
	
	/**
	 * Values for a price 
	 */
	public class Values implements histObject {
		private Date       		theDate      = null;
		private Number.Price    thePrice     = null;
		private Account    		theAccount   = null;
		
		/* Access methods */
		public Date       	getDate()      { return theDate; }
		public Number.Price	getPrice()     { return thePrice; }
		public Account		getAccount()   { return theAccount; }
		
		public void setDate(Date pDate) {
			theDate      = pDate; }
		public void setPrice(Number.Price pPrice) {
			thePrice     = pPrice; }
		public void setAccount(Account pAccount) {
			theAccount   = pAccount; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) {
			theDate      = pValues.getDate();
			thePrice     = pValues.getPrice();
			theAccount   = pValues.getAccount();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (Utils.differs(theDate,    pValues.theDate))    return false;
			if (Utils.differs(thePrice,   pValues.thePrice))   return false;
			if (Utils.differs(theAccount, pValues.theAccount)) return false;
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
			theDate      = pValues.getDate();
			thePrice     = pValues.getPrice();
			theAccount   = pValues.getAccount();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case FIELD_DATE:
					bResult = (Utils.differs(theDate,      pValues.theDate));
					break;
				case FIELD_PRICE:
					bResult = (Utils.differs(thePrice,     pValues.thePrice));
					break;
				case FIELD_ACCOUNT:
					bResult = (Utils.differs(theAccount,   pValues.theAccount));
					break;
			}
			return bResult;
		}
	}		
}
