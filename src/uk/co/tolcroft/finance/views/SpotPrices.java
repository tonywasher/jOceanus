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
import uk.co.tolcroft.models.data.HistoryValues;
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
	private View		theView		= null;
	private Date    	theDate     = null;
	private Date    	theNext     = null;
	private Date    	thePrev     = null;
	private SpotList	thePrices	= null;

	/* Access methods */
	public Date     getDate()    	{ return theDate; }
	public Date     getNext()    	{ return theNext; }
	public Date     getPrev()    	{ return thePrev; }
	public SpotList getPrices()     { return thePrices; }
	public SpotPrice get(long uIndex) {
		return (SpotPrice)thePrices.get((int)uIndex); }
 	
 	/* Constructor */
	public SpotPrices(View pView, Date pDate) {
		/* Create a copy of the date and initiate the list */
		theView		= pView;
		theDate    	= pDate;
		thePrices  	= new SpotList(this);
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
	public class SpotList extends AcctPrice.List {
		/* Members */
		private Date 		theDate 	= null;
		
		/* Constructors */
		public SpotList(SpotPrices pPrices) {
			/* Build initial list */
			super(theView.getData());
			setStyle(ListStyle.EDIT);
			theDate   = pPrices.theDate;

			/* Declare variables */
			FinanceData					myData;
			Account.List.ListIterator 	myActIterator;
			Account 					myAccount;
			SpotPrice					mySpot;
			ListIterator 				myIterator;
			AcctPrice					myPrice;
			int							iDiff;
			SpotPrice.Values			myValues;
			
			/* Loop through the Accounts */
			myData			= theView.getData();
			myActIterator	= myData.getAccounts().listIterator();
			while ((myAccount = myActIterator.next()) != null) {
				/* Ignore accounts that do not have prices */
				if (!myAccount.isPriced()) continue;
				
				/* Create a SpotPrice entry */
				mySpot = new SpotPrice(this, myAccount);
				add(mySpot);
				
				/* If the account is closed then hide the entry */
				if (myAccount.isClosed()) mySpot.setHidden();
			}
			
			/* Set the base for this list */
			AcctPrice.List myPrices = myData.getPrices();
			setBase(myPrices);
			
			/* Loop through the prices */
			myIterator 	= myPrices.listIterator(true);
			while ((myPrice = myIterator.next()) != null) {
				/* Test the Date */
				iDiff 	= theDate.compareTo(myPrice.getDate());
				
				/* If we are past the date */
				if (iDiff < 0) {
					/* Record the next date and break the loop */
					theNext = myPrice.getDate();
					break;
				}
				
				/* Access the Spot Price */
				myAccount 	= myPrice.getAccount();
				mySpot 		= (SpotPrice)searchFor(myAccount.getId());
				myValues	= mySpot.getValues();
				
				/* If we are exactly the date */
				if (iDiff == 0) {
					/* Set price */
					myValues.setPrice(myPrice.getPricePair());
					
					/* Link to base and re-establish state */
					mySpot.setBase(myPrice);
					mySpot.setState(DataState.CLEAN);
				}
				
				/* else we are a previous date */
				else {
					/* Set previous date and value */
					myValues.setPrevDate(myPrice.getDate());
					myValues.setPrevPrice(myPrice.getPrice());
					
					/* Record the latest previous date */
					thePrev = myPrice.getDate();
				}
			}
			
		}
		
		/* Disable extract lists. */
		public SpotList getUpdateList() { return null; }
		public SpotList getEditList() 	{ return null; }
		public SpotList getShallowCopy() { return null; }
		public SpotList getDeepCopy(DataSet<?> pData) { return null; }
		public SpotList getDifferences(SpotList pOld) { return null; }

		/* Is this list locked */
		public boolean isLocked() { return false; }
		
		/* Disable Add a new item */
		public SpotPrice addNewItem(DataItem<?> pElement) {	return null; }
		public SpotPrice addNewItem() { return null; }
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }
		
		/**
		 * Calculate the Edit State for the list
		 */
		public void findEditState() {
			ListIterator	myIterator;
			AcctPrice 		myCurr;
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
			AcctPrice 		myCurr;
			
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
			AcctPrice		myCurr;
				
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
			
	public static class SpotPrice extends AcctPrice {
		/* Access methods */
		public Values      	getValues()    { return (Values)super.getValues(); }
		public Price 		getPrevPrice() { return getValues().getPrevPrice(); }
		public Date			getPrevDate()  { return getValues().getPrevDate(); }

		/* Linking methods */
		public AcctPrice	 getBase() { return (AcctPrice)super.getBase(); }

		/* Field IDs */
		public static final int FIELD_PRVDATE  = AcctPrice.NUMFIELDS;
		public static final int FIELD_PRVPRICE = AcctPrice.NUMFIELDS+1;
		public static final int NUMFIELDS	   = AcctPrice.NUMFIELDS+2;
		
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
				case FIELD_PRVDATE: 	return "PreviousDate";
				case FIELD_PRVPRICE: 	return "PreviousPrice";
				default:		  		return AcctPrice.fieldName(iField);
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
		public String formatField(DebugDetail pDetail, int iField, HistoryValues<AcctPrice> pValues) {
			String 	myString = "";
			Values 	myValues = (Values)pValues;
			switch (iField) {
				case FIELD_PRVDATE: 	
					myString += Date.format(myValues.getPrevDate());	
					break;
				case FIELD_PRVPRICE: 	
					myString += Price.format(myValues.getPrevPrice());	
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
		protected HistoryValues<AcctPrice> getNewValues() { return new Values(); }
		
		/**
		 *  Constructor for a new SpotPrice where no price data exists
		 *  @param pList the Spot Price List
		 *  @param pAccount the price for the date
		 *  @param pLast the last price for the account
		 */
		private SpotPrice(SpotList pList, Account pAccount) {
			super(pList, pAccount);
	
			/* Variables */
			Values 	myValues = getValues();
			
			/* Store base values */
			setControlKey(pList.getControlKey());
			myValues.setDate(pList.theDate);
			myValues.setAccount(pAccount);
			
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

		/* Disable setDate */
		public void setDate(Date pDate) {}
		
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
		
		/* SpotValues */
		public class Values extends AcctPrice.Values {
			private Price		thePrevPrice	= null;
			private Date		thePrevDate		= null;
			
			/* Access methods */
			public Price	getPrevPrice()  	{ return thePrevPrice; }
			public Date		getPrevDate()   	{ return thePrevDate; }
			
			public void setPrevPrice(Price pPrice) {
				thePrevPrice  = pPrice; }
			public void setPrevDate(Date pDate) {
				thePrevDate   = pDate; }

			/* Constructor */
			public Values() {}
			public Values(Values 			pValues) { copyFrom(pValues); }
			public Values(AcctPrice.Values 	pValues) { copyFrom(pValues); }
			
			/* Check whether this object is equal to that passed */
			public boolean histEquals(HistoryValues<AcctPrice> pCompare) {
				Values myValues = (Values)pCompare;
				if (!super.histEquals(pCompare))										return false;
				if (Price.differs(thePrevPrice, myValues.thePrevPrice).isDifferent()) 	return false;
				if (Date.differs(thePrevDate,	myValues.thePrevDate).isDifferent()) 	return false;
				return true;
			}
			
			/* Copy values */
			public HistoryValues<AcctPrice> copySelf() {
				return new Values(this);
			}
			public void    copyFrom(HistoryValues<?> pSource) {
				/* Handle a SpotPrice Values */
				if (pSource instanceof Values) {
					Values myValues = (Values)pSource;
					super.copyFrom(myValues);
					thePrevPrice	= myValues.getPrevPrice();
					thePrevDate		= myValues.getPrevDate();
				}

				/* Handle an AcctPrice Values */
				else if (pSource instanceof AcctPrice.Values) {
					AcctPrice.Values myValues = (AcctPrice.Values)pSource;
					super.copyFrom(myValues);
				}
			}
			
			public Difference	fieldChanged(int fieldNo, HistoryValues<AcctPrice> pOriginal) {
				Values		pValues = (Values)pOriginal;
				Difference	bResult = Difference.Identical;
				switch (fieldNo) {
					case FIELD_PRVPRICE:
						bResult = (Price.differs(thePrevPrice,  pValues.thePrevPrice));
						break;
					case FIELD_PRVDATE:
						bResult = (Date.differs(thePrevDate,  pValues.thePrevDate));
						break;
					default:
						bResult = super.fieldChanged(fieldNo, pValues);
						break;
				}
				return bResult;
			}

			/**
			 * Disable encryption methods
			 */
			protected void updateSecurity() throws Exception {}
			protected void applySecurity() throws Exception {}
			protected void adoptSecurity(ControlKey pControl, EncryptedValues pBase) throws Exception {}
		}		
	}
}
