package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.finance.views.SpotPrices;
import uk.co.tolcroft.finance.views.SpotPrices.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.data.ControlKey;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.EncryptedItem;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.help.DebugDetail;

public class AcctPrice extends EncryptedItem<AcctPrice> {
	/**
	 * The name of the object
	 */
	public static final String objName = "Price";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";

	/* Access methods */
	public  Values  		getValues()    	{ return (Values)super.getValues(); }
	public  Price 			getPrice()  	{ return getPairValue(getValues().getPrice()); }
	public  Date  			getDate()		{ return getValues().getDate(); }
	public  Account			getAccount()	{ return getValues().getAccount(); }
	private void    		setAccount(Account pAccount)   {
		getValues().setAccount(pAccount); }

	public  byte[] 		getPriceBytes() 	{ return getValues().getPriceBytes(); }
	public  PricePair	getPricePair()   	{ return getValues().getPrice(); }

	/* Linking methods */
	public AcctPrice     getBase() { return (AcctPrice)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_ACCOUNT  = EncryptedItem.NUMFIELDS;
	public static final int FIELD_DATE     = EncryptedItem.NUMFIELDS+1;
	public static final int FIELD_PRICE    = EncryptedItem.NUMFIELDS+2;
	public static final int NUMFIELDS	   = EncryptedItem.NUMFIELDS+3;

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
			case FIELD_ACCOUNT:		return "Account";
			case FIELD_DATE:		return "Date";
			case FIELD_PRICE:		return "Price";
			default:		  		return EncryptedItem.fieldName(iField);
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
			case FIELD_ACCOUNT:
				if ((myValues.getAccount() == null) &&
					(myValues.getAccountId() != -1))
					myString += "Id=" + myValues.getAccountId();
				else
					myString += Account.format(myValues.getAccount()); 
				myString = pDetail.addDebugLink(myValues.getAccount(), myString);
				break;
			case FIELD_DATE:	
				myString += Date.format(getDate()); 
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
	protected HistoryValues<AcctPrice> getNewValues() { return new Values(); }
	
	/**
 	* Construct a copy of a Price
 	* 
 	* @param pPrice The Price 
 	*/
	protected AcctPrice(List pList, AcctPrice pPrice) {
		/* Set standard values */
		super(pList, pPrice.getId());
		Values myValues = getValues();
		myValues.copyFrom(pPrice.getValues());
		ListStyle myOldStyle = pPrice.getList().getStyle();

		/* Switch on the ListStyle */
		switch (pList.getStyle()) {
			case EDIT:
				/* If this is a view creation */
				if (myOldStyle == ListStyle.CORE) {
					/* Price is based on the original element */
					setBase(pPrice);
					pList.setNewId(this);				
					break;
				}
				
				/* Else this is a duplication so treat as new item */
				setId(0);
				pList.setNewId(this);				
				break;
			case CLONE:
				reBuildLinks(pList.getData());
			case COPY:
			case CORE:
				/* If this is an insert from a view */
				if (myOldStyle == ListStyle.EDIT) {
					/* Generate new id */
					setId(0);
					
					/* Rebuild links */
					reBuildLinks(pList.getData());
				}
				pList.setNewId(this);				
				break;
			case UPDATE:
				setBase(pPrice);
				setState(pPrice.getState());
				break;
		}
	}

	/**
	 * Construct a new price from a SpotPrice
	 * @param pPrice the price to copy 
	 */
	private AcctPrice(List   	pList,
		         	  SpotPrice	pPrice) {
	
		/* Set standard values */
		super(pList, 0);
		Values myValues = getValues();
		myValues.copyFrom(pPrice.getValues());
			
		/* Allocate the id */
		pList.setNewId(this);				
	}
	
	/* Standard constructor for a newly inserted price */
	protected AcctPrice(List pList) {
		super(pList, 0);
		setControlKey(pList.getControlKey());
		pList.setNewId(this);
	}

	/* Standard constructor for a newly inserted price */
	protected AcctPrice(List pList, Account pAccount) {
		super(pList, pAccount.getId());
		setControlKey(pList.getControlKey());
		pList.setNewId(this);
	}

	/* Extract constructor */
	private AcctPrice(List       		pList,
					  int				uId,
			      	  int 		    	uAccountId,
			      	  java.util.Date 	pDate, 
			      	  String 			pPrice) throws Exception {
		/* Initialise the item */
		super(pList, uId);
		
		/* Initialise the values */
		Values myValues = getValues();
		
		/* Record the Id */
		myValues.setAccountId(uAccountId);
		
		/* Access the DataSet */
		FinanceData myData 	= pList.getData();

		/* Look up the Account */
		Account myAccount = myData.getAccounts().searchFor(uAccountId);
		if (myAccount == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Account Id");
		myValues.setAccount(myAccount);
					
		/* Record the date and price */
		myValues.setDate(new Date(pDate));
		myValues.setPrice(new PricePair(pPrice));
		
		/* Allocate the id */
		pList.setNewId(this);				
	}

	/* Encryption constructor */
	private AcctPrice(List       		pList,
			      	  int           	uId, 
			      	  int 				uControlId,
			      	  int 		    	uAccountId,
			      	  java.util.Date 	pDate, 
			      	  byte[] 			pPrice) throws Exception {
		/* Initialise the item */
		super(pList, uId);
		
		/* Initialise the values */
		Values myValues = getValues();
		
		/* Record the Id */
		myValues.setAccountId(uAccountId);
		
		/* Store the controlId */
		setControlKey(uControlId);
		
		/* Access the DataSet */
		FinanceData myData 	= pList.getData();

		/* Look up the Account */
		Account myAccount = myData.getAccounts().searchFor(uAccountId);
		if (myAccount == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Account Id");
		myValues.setAccount(myAccount);
					
		/* Record the date and price */
		myValues.setDate(new Date(pDate));
		myValues.setPrice(new PricePair(pPrice));
		
		/* Allocate the id */
		pList.setNewId(this);				
	}

	/* Special price constructor for diluted prices */
	private AcctPrice(List       	pList,
				  	  Account	    pAccount,
				  	  Date 			pDate, 
				  	  Price			pPrice) throws Exception {
		/* Initialise the item */
		super(pList, 0);
		
		/* Initialise the values */
		Values myValues = getValues();
		
		/* Set the passed details */
		myValues.setAccount(pAccount);
		myValues.setDate(pDate);

		/* Create the pair for the values */
		myValues.setPrice(new PricePair(pPrice));
		
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
		AcctPrice myThat = (AcctPrice)pThat;
		
		/* Check for equality on id */
		if (getId() != myThat.getId()) return false;
		
		/* Compare the changeable values */
		return getValues().histEquals(myThat.getValues()).isIdentical();
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
		AcctPrice myThat = (AcctPrice)pThat;

		/* Compare the accounts */
		//iDiff = getAccount().compareTo(myThat.getAccount());
		//if (iDiff != 0) return iDiff;

		/* If the date differs */
		if (this.getDate() != myThat.getDate()) {
			/* Handle null dates */
			if (this.getDate()   == null) return 1;
			if (myThat.getDate() == null) return -1;
			
			/* Compare the dates */
			iDiff = getDate().compareTo(myThat.getDate());
			if (iDiff != 0) return iDiff;
		}
		
		/* Compare the accounts */
		iDiff = getAccount().compareTo(myThat.getAccount());
		if (iDiff != 0) return iDiff;

		/* Compare the IDs */
		iDiff =(int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}
	
	/**
	 * Rebuild Links to partner data
	 * @param pData the DataSet
	 */
	protected void reBuildLinks(FinanceData pData) {
		/* Update the Encryption details */
		super.reBuildLinks(pData);
		
		/* Access Accounts */
		Account.List myAccounts = pData.getAccounts();
		
		/* Update to use the local copy of the Accounts (use name rather than id) */
		Values 	myValues   	= getValues();
		Account	myAct		= myValues.getAccount();
		Account	myNewAct 	= myAccounts.searchFor(myAct.getName());
		myValues.setAccount(myNewAct);
	}

	/**
	 * Validate the price
	 */
	public void validate() {
		Date 		myDate = getDate();
		List 		myList = (List)getList();
		FinanceData	mySet  = myList.getData();
			
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
	public void setPrice(Price pPrice) throws Exception {
		if (pPrice != null) getValues().setPrice(new PricePair(pPrice));
		else 				getValues().setPrice(null);
	}

	/**
	 * Set a new date 
	 * 
	 * @param pDate the new date 
	 */
	public void setDate(Date pDate) {
		getValues().setDate((pDate == null) ? null : new Date(pDate));
	}

	/**
	 * Update Price from an item Element  
	 * @param pItem the price extract 
	 * @return whether changes have been made
	 */
	public boolean applyChanges(DataItem<?> pItem) {
		boolean bChanged = false;
		if (pItem instanceof SpotPrice) {
			SpotPrice mySpot = (SpotPrice)pItem;
			bChanged = applyChanges(mySpot);
		}
		else if (pItem instanceof AcctPrice) {
			AcctPrice myPrice = (AcctPrice)pItem;
			bChanged = applyChanges(myPrice);
		}
		return bChanged;
	}
	
	/**
	 * Update Price from a Price extract 
	 * @param pPrice the price extract 
	 * @return whether changes have been made
	 */
	private boolean applyChanges(AcctPrice pPrice) {
		Values	  myValues	= getValues();
		Values	  myNew		= pPrice.getValues();
		boolean   bChanged 	= false;
		
		/* Store the current detail into history */
		pushHistory();
		
		/* Update the price if required */
		if (differs(myValues.getPrice(), myNew.getPrice()).isDifferent()) 
			myValues.setPrice(myNew.getPrice());
	
		/* Update the date if required */
		if (Date.differs(getDate(), pPrice.getDate()).isDifferent()) 
			setDate(pPrice.getDate());
		
		/* Check for changes */
		if (checkForHistory()) {
			/* Mark as changed */
			setState(DataState.CHANGED);
			bChanged = true;
		}
		
		/* Return to caller */
		return bChanged;
	}
	
	/**
	 * Update Price from a Price extract 
	 * @param pPrice the price extract 
	 * @return whether changes have been made
	 */
	private boolean applyChanges(SpotPrice pPrice) {			
		boolean bChanged = false;
		Values	  			myValues	= getValues();
		SpotPrice.Values	myNew		= pPrice.getValues();
		
		/* If we are setting a null price */
		if (myNew.getPrice() == null) {
			/* We are actually deleting the price */
			setState(DataState.DELETED);
		}
		
		/* else we have a price to set */
		else {
			/* Store the current detail into history */
			pushHistory();
		
			/* Update the price if required */
			if (differs(myValues.getPrice(), myNew.getPrice()).isDifferent()) 
				myValues.setPrice(new PricePair(myNew.getPrice()));
	
			/* Check for changes */
			if (checkForHistory()) {
				/* Mark as changed */
				setState(DataState.CHANGED);
				bChanged = false;
			}
		}
		
		/* Return to caller */
		return bChanged;
	}

	/**
	 * Price List
	 */
	public static class List  extends EncryptedList<List, AcctPrice> {
		/* Members */
		private Account	theAccount	= null;

		/* Access Extra Variables correctly */
		public FinanceData 	getData() 		{ return (FinanceData) super.getData(); }
		public Account 		getAccount() 	{ return theAccount; }
		
		/** 
		 * Construct an empty CORE price list
	 	 * @param pData the DataSet for the list
		 */
		protected List(FinanceData pData) { 
			super(List.class, AcctPrice.class, pData);
		}

		/**
		 * Constructor for a cloned List
		 * @param pSource the source List
		 */
		private List(List pSource) { 
			super(pSource);
		}
		
		/**
		 * Construct an update extract for the List.
		 * @return the update Extract
		 */
		private List getExtractList(ListStyle pStyle) {
			/* Build an empty Extract List */
			List myList = new List(this);
			
			/* Obtain underlying updates */
			myList.populateList(pStyle);
			
			/* Return the list */
			return myList;
		}

		/* Obtain extract lists. */
		public List getUpdateList() { return getExtractList(ListStyle.UPDATE); }
		public List getEditList() 	{ return null; }
		public List getShallowCopy() 	{ return getExtractList(ListStyle.COPY); }
		public List getDeepCopy(DataSet<?> pDataSet)	{ 
			/* Build an empty Extract List */
			List myList = new List(this);
			myList.setData(pDataSet);
			
			/* Obtain underlying clones */
			myList.populateList(ListStyle.CLONE);
			myList.setStyle(ListStyle.CORE);
			
			/* Return the list */
			return myList;
		}

		/** 
		 * Construct a difference Price list
		 * @param pNew the new Price list 
		 * @param pOld the old Price list 
		 */
		protected List getDifferences(List pOld) { 
			/* Build an empty Difference List */
			List myList = new List(this);
			
			/* Calculate the differences */
			myList.getDifferenceList(this, pOld);
			
			/* Return the list */
			return myList;
		}

		/**
		 * Construct an edit extract of a Pattern list
		 * @param pAccount	 The account to extract patterns for 
		 */
		public List getEditList(Account pAccount) {
			/* Build an empty Update */
			List myList = new List(this);
			
			/* Make this list the correct style */
			myList.setStyle(ListStyle.EDIT);

			/* Local variables */
			AcctPrice 		myCurr;
			AcctPrice 		myItem;
			ListIterator 	myIterator;
			
			/* Store the account */
			myList.theAccount = pAccount;
			
			/* Access the list iterator */
			myIterator = listIterator(true);
			
			/* Loop through the Prices */
			while ((myCurr = myIterator.next()) != null) {
				/* Check the account */
				int myResult = pAccount.compareTo(myCurr.getAccount());
				
				/* Skip different accounts */
				if (myResult != 0) continue;
								
				/* Copy the item */
				myItem = new AcctPrice(myList, myCurr);
				myList.add(myItem);
			}
			
			/* Return the List */
			return myList;
		}
	
		/**
		 * Add a new item to the core list
		 * 
		 * @param pPrice item
		 * @return the newly added item
		 */
		public AcctPrice addNewItem(DataItem<?> pPrice) {
			if (pPrice instanceof SpotPrice) {
				AcctPrice myPrice = new AcctPrice(this, (SpotPrice)pPrice);
				add(myPrice);
				return myPrice;
			}
			else if (pPrice instanceof AcctPrice) {
				AcctPrice myPrice = new AcctPrice(this, (AcctPrice)pPrice);
				add(myPrice);
				return myPrice;
			}
			else return null;
		}

		/**
		 * Add a new item to the edit list
		 * @return the newly added item
		 */
		public AcctPrice addNewItem() {
			AcctPrice myPrice = new AcctPrice(this);
			myPrice.setAccount(theAccount);
			add(myPrice);
			return myPrice;
		}

		/**
		 * 	Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }

		/**
		 * Count the instances of a date
		 * 
		 * @param pDate the date
		 * @return The Item if present (or null)
		 */
		public int countInstances(Date 		pDate,
								  Account	pAccount) {
			ListIterator 	myIterator;
			AcctPrice    	myCurr;
			int      		iDiff;
			int      		iCount = 0;

			/* Access the list iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				iDiff = pDate.compareTo(myCurr.getDate());
				if (iDiff != 0) continue;
				iDiff = pAccount.compareTo(myCurr.getAccount());
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
		public AcctPrice getLatestPrice(Account pAccount,
										Date 	pDate) {
			ListIterator 	myIterator;
			AcctPrice 			myPrice = null;
			AcctPrice 			myCurr;

			/* Skip to alias if required */
			if (pAccount.getAlias() != null)
				pAccount = pAccount.getAlias();
			
			/* Access the list iterator */
			myIterator = listIterator();
			
			/* Loop through the Prices */
			while ((myCurr = myIterator.next()) != null) {
				/* Skip records that do not belong to this account */
				if (Account.differs(myCurr.getAccount(), pAccount).isDifferent())
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
		protected void markActiveItems () {
			ListIterator 	myIterator;
			AcctPrice 		myCurr;

			/* Access the list iterator */
			myIterator = listIterator();
			
			/* Loop through the Prices */
			while ((myCurr = myIterator.next()) != null) {
				/* mark the account referred to */
				myCurr.getAccount().touchItem(myCurr); 
			}
		}

		/**
		 * Apply changes from a Spot Price list
		 */
		public void applyChanges(SpotPrices pPrices) {
			ListIterator	myIterator;
			List 	 		myList;
			AcctPrice 		mySpot;
			Date			myDate;
			PricePair		myPoint;
			AcctPrice		myPrice;

			/* Access details */
			myDate = pPrices.getDate();
			myList = pPrices.getPrices();
			
			/* Access the iterator */
			myIterator = myList.listIterator();
			
			/* Loop through the spot prices */
			while ((mySpot  = myIterator.next()) != null) {
				/* Access the price for this date if it exists */
				myPrice 	= mySpot.getBase();
				myPoint 	= mySpot.getPricePair();

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
						myPrice = new AcctPrice(this);

						/* Set the date and price */
						myPrice.setDate(new Date(myDate));
						myPrice.getValues().setPrice(myPrice.new PricePair(myPoint));
						myPrice.setAccount(mySpot.getAccount());

						/* Add to the list and link backwards */
						mySpot.setBase(myPrice);
						add(myPrice);
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
		public void addItem(int				uId,
							java.util.Date  pDate,
	            			String   		pAccount,
				            String   		pPrice) throws Exception {
			Account     	myAccount;
			Account.List	myAccounts;
			
			/* Access the Accounts */
			myAccounts = getData().getAccounts();
			
			/* Look up the Account */
			myAccount = myAccounts.searchFor(pAccount);
			if (myAccount == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "Price on [" + 
			                        Date.format(new Date(pDate)) +
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
		public void addItem(int				uId,
							java.util.Date  pDate,
				            int     		uAccountId,
				            String   		pPrice) throws Exception {
			AcctPrice     	myPrice;
			
			/* Create the price and PricePoint */
			myPrice = new AcctPrice(this, uId, uAccountId, pDate, pPrice);
			
			/* Check that this PriceId has not been previously added */
			if (!isIdUnique(myPrice.getId())) 
				throw new Exception(ExceptionClass.DATA,
									myPrice,
									"Duplicate PriceId <" + myPrice.getId() + ">");
			 
			/* Validate the price */
			myPrice.validate();

			/* Handle validation failure */
			if (myPrice.hasErrors()) 
				throw new Exception(ExceptionClass.VALIDATE,
									myPrice,
									"Failed validation");
				
			/* Add to the list */
			add(myPrice);
		}			

		/**
		 *  Load an Encrypted price
		 */
		public void addItem(int     		uId,
			  	 			int 			uControlId,
				            java.util.Date  pDate,
				            int     		uAccountId,
				            byte[]   		pPrice) throws Exception {
			AcctPrice     	myPrice;
			
			/* Create the price and PricePoint */
			myPrice = new AcctPrice(this, uId, uControlId, uAccountId, pDate, pPrice);
			
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
			add(myPrice);
		}			

		/**
		 *  Allow a price to be added
		 */
		public AcctPrice addItem(Account 	pAccount,
							     Date  		pDate,
							     Price		pPrice) throws Exception {
			AcctPrice     	myPrice;
			
			/* Create the price and PricePoint */
			myPrice = new AcctPrice(this, pAccount, pDate, pPrice);
			
			/* Validate the price */
			myPrice.validate();

			/* Handle validation failure */
			if (myPrice.hasErrors()) 
				throw new Exception(ExceptionClass.VALIDATE,
									myPrice,
									"Failed validation");
				
			/* Add to the list */
			add(myPrice);
			
			/* Return the caller */
			return myPrice;
		}			
	}
	
	/**
	 * Values for a price 
	 */
	public class Values extends EncryptedValues {
		private Date    	theDate      = null;
		private PricePair   thePrice     = null;
		private Account 	theAccount   = null;
		private Integer	 	theAccountId = null;	
		
		/* Access methods */
		public Date     	getDate()      { return theDate; }
		public PricePair	getPrice()     { return thePrice; }
		public Account		getAccount()   { return theAccount; }
		private Integer		getAccountId() { return theAccountId; }
		
		public Price  		getPriceValue() { return getPairValue(thePrice); }
		public byte[]  		getPriceBytes() { return getPairBytes(thePrice); }

		public void setDate(Date pDate) {
			theDate      = pDate; }
		public void setPrice(PricePair pPrice) {
			thePrice     = pPrice; }
		public void setAccount(Account pAccount) {
			theAccount   = pAccount; 
			theAccountId = (pAccount == null) ? null : pAccount.getId(); }
		private void setAccountId(Integer pAccountId) {
			theAccountId   = pAccountId; } 

		/* Constructor */
		public Values() {}
		public Values(Values 			pValues) { copyFrom(pValues); }
		public Values(SpotPrice.Values 	pValues) { copyFrom(pValues); }
		
		/* Check whether this object is equal to that passed */
		public Difference histEquals(HistoryValues<AcctPrice> pCompare) {
			/* Make sure that the object is the same class */
			if (pCompare.getClass() != this.getClass()) return Difference.Different;
			
			/* Cast correctly */
			Values myValues = (Values)pCompare;

			/* Determine underlying differences */
			Difference myDifference = super.histEquals(pCompare);
			
			/* Compare underlying values */
			myDifference = myDifference.combine(Date.differs(theDate,     	myValues.theDate));
			myDifference = myDifference.combine(differs(thePrice, 			myValues.thePrice));
			myDifference = myDifference.combine(Account.differs(theAccount, myValues.theAccount));
			myDifference = myDifference.combine(Utils.differs(theAccountId, myValues.theAccountId));
			
			/* Return differences */
			return myDifference;
		}
		
		/* Copy values */
		public HistoryValues<AcctPrice> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {			
			/* Handle a SpotPrice Values */
			if (pSource instanceof SpotPrice.Values) {
				SpotPrice.Values 	myValues = (SpotPrice.Values)pSource;
				super.copyFrom(myValues);
				theDate      = myValues.getDate();
				thePrice     = (myValues.getPrice() == null) ? null : new PricePair(myValues.getPrice());
				theAccount   = myValues.getAccount();
				theAccountId = theAccount.getId();
			}
			
			/* Handle a Price Values */
			else if (pSource instanceof Values) {
				Values myValues = (Values)pSource;
				super.copyFrom(myValues);
				theDate      = myValues.getDate();
				thePrice     = myValues.getPrice();
				theAccount   = myValues.getAccount();
				theAccountId = myValues.getAccountId();
			}
		}
		public Difference	fieldChanged(int fieldNo, HistoryValues<AcctPrice> pOriginal) {
			Values 		pValues = (Values)pOriginal;
			Difference	bResult = Difference.Identical;
			switch (fieldNo) {
				case FIELD_DATE:
					bResult = (Date.differs(theDate,     	pValues.theDate));
					break;
				case FIELD_PRICE:
					bResult = (differs(thePrice,     		pValues.thePrice));
					break;
				case FIELD_ACCOUNT:
					bResult = (Account.differs(theAccount,  pValues.theAccount));
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
		protected void updateSecurity() throws Exception {
			/* Update the encryption */
			thePrice	= new PricePair(thePrice.getValue());
		}		
		
		/**
		 * Ensure encryption after security change
		 */
		protected void applySecurity() throws Exception {
			/* Apply the encryption */
			thePrice.encryptPair(null);
		}		
		
		/**
		 * Adopt encryption from base
		 * @param pBase the Base values
		 */
		protected void adoptSecurity(ControlKey pControl, EncryptedValues pBase) throws Exception {
			Values myBase = (Values)pBase;

			/* Apply the encryption */
			thePrice.encryptPair(myBase.getPrice());
		}
	}		
}
