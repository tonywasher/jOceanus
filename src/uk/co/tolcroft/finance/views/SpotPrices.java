package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.data.ControlKey;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.EditState;
import uk.co.tolcroft.models.data.EncryptedItem;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.data.EncryptedItem.EncryptedList;
import uk.co.tolcroft.models.help.DebugDetail;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugObject;
import uk.co.tolcroft.models.help.DebugManager.DebugEntry;

public class SpotPrices implements DebugObject {
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
		thePrices  	= new List(this);
	}
	
	/**
	 * Create a string form of the object suitable for inclusion in an HTML document
	 * @param pDetail the debug detail
	 * @return the formatted string
	 */
	public StringBuilder buildDebugDetail(DebugDetail pDetail) {
		StringBuilder	myString = new StringBuilder(10000);
			
		/* Format the table headers */
		myString.append("<table border=\"1\" width=\"90%\" align=\"center\">");
		myString.append("<thead><th>SpotPrices</th>");
		myString.append("<th>Property</th><th>Value</th></thead><tbody>");
			
		/* Start the Fields section */
		myString.append("<tr><th rowspan=\"4\">Fields</th></tr>");
			
		/* Format the range */
		myString.append("<tr><td>Date</td><td>"); 
		myString.append(Date.format(theDate)); 
		myString.append("</td></tr>");
		myString.append("<tr><td>Next</td><td>"); 
		myString.append(Date.format(theNext)); 
		myString.append("</td></tr>");
		myString.append("<tr><td>Previous</td><td>"); 
		myString.append(Date.format(thePrev)); 
		myString.append("</td></tr>");
		myString.append("</tbody></table>"); 

		/* Return the data */
		return myString;
	}		
	
	/**
	 * Add child entries for the debug object
	 * @param pManager the debug manager
	 * @param pParent the parent debug entry
	 */
	public void addChildEntries(DebugManager 	pManager,
								DebugEntry		pParent) {
		/* Add lines child */
		pManager.addChildEntry(pParent, "Prices", thePrices);		
	}
	
	/* The List class */
	public class List extends EncryptedList<List, SpotPrice> {
		/* Members */
		private Date 		theDate 	= null;
		
		/* Constructors */
		public List(SpotPrices pPrices) { 
			super(List.class, SpotPrice.class, theView.getData(), ListStyle.EDIT);
			theDate   = pPrices.theDate;
			
			/* Declare variables */
			FinanceData		myData;
			AcctPrice 		myCurr;
			AcctPrice 		myLast;
			AcctPrice.List	myPrices;
			int				iDiff;
			boolean			isNew;
			boolean			isSet;
			Account 		myAcct;
			SpotPrice 		myPrice;
			
			AcctPrice.List.ListIterator myIterator;
			
			/* Access the iterator */
			myData 		= theView.getData();
			myPrices	= myData.getPrices();
			myIterator 	= myPrices.listIterator(true);
			myLast		= null;
			myAcct		= null;
			isSet		= false;
			setBase(myPrices);
			
			/* Loop through the prices looking for this price */
			while ((myCurr = myIterator.next()) != null) {
				/* Test the date and account */
				iDiff 	= theDate.compareTo(myCurr.getDate()); 
				isNew	= Account.differs(myAcct, myCurr.getAccount()).isDifferent();
				
				/* If this is a new account */
				if (isNew) {
					/* If we have not set a price for this account */
					if ((myAcct != null) && (!isSet)) {
						/* Create the new spot price and add it to the list */
						myPrice = new SpotPrice(this, myAcct, myLast);
						add(myPrice);

						/* Note if the account is closed */
						if (myAcct.isClosed()) myPrice.setHidden();
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
					add(myPrice);
					isSet 	= true;

					/* Note if the account is closed */
					if (myAcct.isClosed()) myPrice.setHidden();
				}
				
				/* If this is past the required date */
				if (iDiff < 0) {
					/* If we have not set a price for this account */
					if (!isSet) {
						/* Create the new spot price and add it to the list */
						myPrice = new SpotPrice(this, myAcct, myLast);
						add(myPrice);
						isSet = true;

						/* Note if the account is closed */
						if (myAcct.isClosed()) myPrice.setHidden();
					}
					
					/* Record nearest subsequent price point */
					if ((theNext == null) ||
						(theNext.compareTo(myCurr.getDate()) > 0))
						theNext = myCurr.getDate();
				}
			}	

			/* If we have not set a price for this account */
			if ((myAcct != null) && (!isSet)) {
				/* Create the new spot price and add it to the list */
				myPrice = new SpotPrice(this, myAcct, myLast);
				add(myPrice);
			}
		}
		
		/* Obtain extract lists. */
		public List getUpdateList() { return null; }
		public List getEditList() 	{ return null; }
		public List getShallowCopy() { return null; }
		public List getDeepCopy(DataSet<?> pData) { return null; }
		public List getDifferences(List pOld) { return null; }

		/* Is this list locked */
		public boolean isLocked() { return false; }
		
		/**
		 * Add a new item (never used)
		 */
		public SpotPrice addNewItem(DataItem<?> pElement) {
			return null;}
		
		/**
		 * Add a new item to the edit list
		 */
		public SpotPrice addNewItem() { return null; }
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }
		
		/** 
		 * Validate a spot price list
		 */
		public void validate() {
			ListIterator	myIterator;
			SpotPrice 		myCurr;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the list */
			while ((myCurr = myIterator.next()) != null) {
				/* Validate the item */
				myCurr.validate();
			}
		}	
		
		/**
		 * Calculate the Edit State for the list
		 */
		public void findEditState() {
			ListIterator	myIterator;
			SpotPrice 		myCurr;
			EditState		myEdit;
			
			/* Access the iterator */
			myIterator 	= listIterator();
			myEdit		= EditState.CLEAN;
			
			/* Loop through the list */
			while ((myCurr = myIterator.next()) != null) {
				/* Switch on new state */
				switch (myCurr.getState()) {
					case CLEAN:
					case DELNEW:
						break;
					case NEW:
					case DELETED:
					case DELCHG:
					case CHANGED:
					case RECOVERED:
						myEdit = EditState.VALID;
						break;
				}
			}
			
			/* Set the Edit State */
			setEditState(myEdit);
		}

		/**
		 * Does the list have updates
		 */
		public boolean hasUpdates() {
			ListIterator	myIterator;
			SpotPrice 		myCurr;
			
			/* Access the iterator */
			myIterator 	= listIterator();
			
			/* Loop through the list */
			while ((myCurr = myIterator.next()) != null) {
				/* Switch on state */
				switch (myCurr.getState()) {
					case CLEAN:
					case DELNEW:
						break;
					case DELETED:
					case DELCHG:
					case CHANGED:
					case RECOVERED:
						return true;
				}
			}
			
			/* Return no updates */
			return false;
		}

		/** 
		 * Reset changes in an edit view
		 */
		public void resetChanges() {
			ListIterator 	myIterator;
			SpotPrice		myCurr;
				
			/* Create an iterator for the list */
			myIterator = listIterator(true);
				
			/* Loop through the elements */
			while ((myCurr = myIterator.next()) != null) {		
				/* Switch on the state */
				switch (myCurr.getState()) {
					/* If this is a clean item, just ignore */
					case CLEAN:
					case DELNEW:
						break;
							
					/* If this is a changed or DELCHG item */
					case NEW:
					case CHANGED:
					case DELCHG:
						/* Clear changes and fall through */
						myCurr.resetHistory();

					/* If this is a deleted or recovered item */
					case DELETED:
					case RECOVERED:				
						/* Clear errors and mark the item as clean */
						myCurr.clearErrors();
						myCurr.setState(DataState.CLEAN);
						break;
				}
			}
		}
	}
			
	public static class SpotPrice 	extends EncryptedItem<SpotPrice>  {
		/* Properties */
		private Account       	theAccount  	= null;
		private Price			thePrevPrice	= null;
		private Date			thePrevDate		= null;
		private Date			theDate			= null;
		
		/* Access methods */
		public Date        	getDate()      { return theDate; }
		public Account		getAccount()   { return theAccount; }
		public Values      	getValues()    { return (Values)super.getValues(); }
		public Price 		getPrevPrice() { return thePrevPrice; }
		public Date			getPrevDate()  { return thePrevDate; }

		public PricePair	getPricePair() { return getValues().getPrice(); }
		
		/* Linking methods */
		public AcctPrice	 getBase() { return (AcctPrice)super.getBase(); }

		/* Field IDs */
		public static final int FIELD_ACCOUNT  = EncryptedItem.NUMFIELDS;
		public static final int FIELD_PRICE    = EncryptedItem.NUMFIELDS+1;
		public static final int NUMFIELDS	   = EncryptedItem.NUMFIELDS+2;
		
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
		public static String	fieldName(int iField) {
			switch (iField) {
				case FIELD_ACCOUNT: return "Account";
				case FIELD_PRICE: 	return "Price";
				default:		  	return EncryptedItem.fieldName(iField);
			}
		}
		
		/**
		 * Determine the field name in a non-static fashion 
		 */
		public String getFieldName(int iField) { return fieldName(iField); }
		
		/**
		 * Format the value of a particular field as a table row
		 * @param pDetail the debug detail
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(DebugDetail pDetail, int iField, HistoryValues<SpotPrice> pValues) {
			String 	myString = "";
			Values 	myValues = (Values)pValues;
			switch (iField) {
				case FIELD_ACCOUNT:	
					myString += theAccount.getName(); 
					myString = pDetail.addDebugLink(theAccount, myString);
					break;
				case FIELD_PRICE: 	
					myString += Price.format(myValues.getPriceValue());	
					break;
				default: 		
					myString += super.formatField(pDetail, iField, pValues); 
					break;
			}
			return myString;
		}
					
		/**
		 * Get an initial set of values 
		 * @return an initial set of values 
		 */
		protected HistoryValues<SpotPrice> getNewValues() { return new Values(); }
		
		/**
		 *  Constructor for a new SpotPrice 
		 *  @param pList the Spot Price List
		 *  @param pPrice the price for the date
		 *  @param pLast the last price for the account
		 */
		public SpotPrice(List pList, AcctPrice pPrice, AcctPrice pLast) {
			super(pList, pPrice.getId());
			theDate = pList.theDate;
	
			/* Variables */
			Values 	myValues = getValues();
			
			/* Store base values */
			setControlKey(pList.getControlKey());
			theAccount 		= pPrice.getAccount();
			if (pLast != null) {
				thePrevPrice 	= pLast.getPrice();
				thePrevDate 	= pLast.getDate();
			}
				
			/* Set the price */
			myValues.setPrice(new PricePair(pPrice.getPricePair()));
			
			/* Link to base */
			setBase(pPrice);
			
			/* Set the state */
			setState(DataState.CLEAN);			
		}

		/**
		 *  Constructor for a new SpotPrice where no price data exists
		 *  @param pList the Spot Price List
		 *  @param pAccount the price for the date
		 *  @param pLast the last price for the account
		 */
		public SpotPrice(List pList, Account pAccount, AcctPrice pLast) {
			super(pList, 0);
			theDate = pList.theDate;
	
			/* Store base values */
			setControlKey(pList.getControlKey());
			theAccount 		= pAccount;
			if (pLast != null) {
				thePrevPrice 	= pLast.getPrice();
				thePrevDate 	= pLast.getDate();
			}
			
			/* Set the state */
			setState(DataState.CLEAN);
		}
					
		/**
		 * Validate the line
		 */
		public void validate() {
			setValidEdit();
		}

		/* Is this row locked */
		public boolean isLocked() { return isHidden(); }
		
		/**
		 * Note that this item has been validated 
		 */
		public	  void					setValidEdit() {
			setEditState((hasHistory()) ? EditState.VALID : EditState.CLEAN);
		}

		/**
		 * Set the state of the item
		 * A Spot list has some minor changes to the algorithm in that there are 
		 * no NEW or DELETED states, leaving just CLEAN and CHANGED. The isDeleted
		 * flags is changed in usage to an isVisible flag
		 * @param newState the new state to set
		 */
		public Price getPrice() {
			/* Switch on state */
			switch (getState()) {
				case NEW:
				case CHANGED:
				case RECOVERED:
					return getPairValue(getValues().getPrice());
				case CLEAN:
					return (getBase().isDeleted()) ? null : getPairValue(getValues().getPrice());
				default:
					return null;
			}
		}
		
		/**
		 * Set the state of the item
		 * A Spot list has some minor changes to the algorithm in that there are 
		 * no NEW or DELETED states, leaving just CLEAN and CHANGED. The isDeleted
		 * flags is changed in usage to an isVisible flag
		 * @param newState the new state to set
		 */
		public void setState(DataState newState) {
			/* Switch on new state */
			switch (newState) {
				case CLEAN:
					setDataState((getBase()==null) ? DataState.DELNEW : newState);
					setEditState(EditState.CLEAN);
					break;
				case CHANGED:
					setDataState((getBase()==null) ? DataState.NEW : newState);
					setEditState(EditState.DIRTY);
					break;
				case DELETED:
					switch (getState()) {
						case NEW:
							setDataState(DataState.DELNEW);
							break;
						case CHANGED:
							setDataState(DataState.DELCHG);
							break;
						default:
							setDataState(DataState.DELETED);
							break;
					}
					setEditState(EditState.DIRTY);
					break;
				case RECOVERED:
					switch (getState()) {
						case DELNEW:
							setDataState(DataState.NEW);
							break;
						case DELCHG:
							setDataState(DataState.CHANGED);
							break;
						case DELETED:
							setDataState(DataState.CLEAN);
							break;
						default:
							setDataState(DataState.RECOVERED);
							break;
					}
					setEditState(EditState.DIRTY);
					break;
			}
		}
		
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
		public void setPrice(Price pPrice) throws Exception {
			if (pPrice != null) getValues().setPrice(new PricePair(pPrice));
			else 				getValues().setPrice(null);
		}
		
		/* SpotValues */
		public class Values extends EncryptedValues {
			private PricePair	thePrice	= null;
			
			/* Access methods */
			public Date			getDate()     	{ return theDate; }
			public Account		getAccount()    { return theAccount; }
			public PricePair	getPrice()     	{ return thePrice; }
			public Price  		getPriceValue() { return getPairValue(thePrice); }
			public byte[]  		getPriceBytes() { return getPairBytes(thePrice); }
			
			public void setPrice(PricePair pPrice) {
				thePrice  = pPrice; }

			/* Constructor */
			public Values() {}
			public Values(Values 			pValues) { copyFrom(pValues); }
			public Values(AcctPrice.Values 	pValues) { copyFrom(pValues); }
			
			/* Check whether this object is equal to that passed */
			public boolean histEquals(HistoryValues<SpotPrice> pCompare) {
				Values myValues = (Values)pCompare;
				if (!super.histEquals(pCompare))							return false;
				if (differs(thePrice,     myValues.thePrice).isDifferent()) return false;
				return true;
			}
			
			/* Copy values */
			public HistoryValues<SpotPrice> copySelf() {
				return new Values(this);
			}
			public void    copyFrom(HistoryValues<?> pSource) {
				/* Handle a SpotPrice Values */
				if (pSource instanceof Values) {
					Values myValues = (Values)pSource;
					super.copyFrom(myValues);
					thePrice     = myValues.getPrice();
				}

				/* Handle an AcctPrice Values */
				else if (pSource instanceof AcctPrice.Values) {
					AcctPrice.Values myValues = (AcctPrice.Values)pSource;
					super.copyFrom(myValues);
					thePrice     = new PricePair(myValues.getPrice());
				}
			}
			
			public Difference	fieldChanged(int fieldNo, HistoryValues<SpotPrice> pOriginal) {
				Values		pValues = (Values)pOriginal;
				Difference	bResult = Difference.Identical;
				switch (fieldNo) {
					case SpotPrices.SpotPrice.FIELD_PRICE:
						bResult = (differs(thePrice,  pValues.thePrice));
						break;
					default:
						bResult = super.fieldChanged(fieldNo, pValues);
						break;
				}
				return bResult;
			}

			/**
			 * Update encryption after security change
			 */
			protected void updateSecurity() throws Exception {}
			
			/**
			 * Apply encryption after non-encrypted load
			 */
			protected void applySecurity() throws Exception {}
			
			/**
			 * Adopt encryption from base
			 * @param pBase the Base values
			 */
			protected void adoptSecurity(ControlKey pControl, EncryptedValues pBase) throws Exception {}
		}		
	}
}
