package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.AnalysisYear.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number;

public class AssetAnalysis {
	/* Members */
	private DataSet			theData		= null;
	private AnalysisYear	theYear	  	= null;
	private Date 			theDate    	= null;
	private Account			theAccount	= null;
	private List    		theBuckets 	= null;
	private boolean			hasTotals  	= false;
	
	/* Access methods */
	public List 	getBuckets() { return theBuckets; }
	public Date 	getDate()    { return theDate; }
	
	/* Constructor */
	public AssetAnalysis(DataSet pData) {
		theData		= pData;
		theBuckets 	= new List();
	}
	
	/**
	 * Obtain an instant valuation report
	 * 
	 * @param  pData The DateSet for analysis
	 * @param  pDate The Date of the valuation
	 */
	public AssetAnalysis(DataSet pData, Date pDate) {
		DataList<Event>.ListIterator 	myIterator;
		Event.List					 	myEvents;
		Event 							myCurr;
		int   							myResult;

		/* Initialise the buckets */
		theData		= pData;
		theDate    	= pDate;
		theBuckets 	= new List();

		/* Access the events and the iterator */
		myEvents 	= pData.getEvents();
		myIterator 	= myEvents.listIterator();
		
		/* Loop through the Events extracting relevant elements */
		while ((myCurr = myIterator.next()) != null) {
			/* Check the range */
			myResult = pDate.compareTo(myCurr.getDate());
			
			/* Handle out of range */
			if (myResult == -1) break;
			
			/* Skip events that do not involve assets */
			if (!myCurr.isAssetRelated()) continue; 
			
			/* Process the event in the asset report */
			processEvent(myCurr);
		}
		
		/* Value priced assets */
		valuePricedAssets();
	}
	
	/**
	 * Obtain an end-of year valuation report
	 * 
	 * @param  pData The DateSet for analysis
	 * @param  pDate The Date of the valuation
	 * @param  pYear The Set of reports
	 */
	public AssetAnalysis(DataSet pData, Date pDate, AnalysisYear pYear) {
		/* Store date and create new list */
		theData		= pData;
		theDate    	= pDate;
		theYear    	= pYear;
		theBuckets 	= new List();
	}
		
	/**
	 * Obtain an asset valuation report
	 * 
	 * @param  pData The DateSet for analysis
	 * @param  pDate The Date of the valuation
	 * @param  pSet  The Set of reports
	 */
	protected AssetAnalysis(DataSet pData, Account pAccount) {
		/* Store date and create new list */
		theData		= pData;
		theAccount	= pAccount;
		theBuckets 	= new List();
	}
		
	/**
	 * Obtain the account bucket for an asset valuation report
	 * @return the account bucket 
	 */
	protected Bucket getAccountBucket() {
		Bucket myBucket = null;
		
		/* Access the Account bucket if present */
		if (theAccount != null)
			myBucket = theBuckets.getAccountBucket(theAccount);
		
		/* Return the bucket */
		return myBucket;
	}
	
	/**
	 * Seed from previous end-of-year analysis
	 * 
	 * @param  pAnalysis The Previous analysis
	 */
	public void seedAnalysis(AssetAnalysis pAnalysis) {
		List.ListIterator	myIterator;
		Bucket 				myCurr;
		Bucket 				myBucket;
		AssetBucket 		myAsset;
		
		/* If we have an analysis to copy */
		if (pAnalysis != null) {			
			/* Access the iterator */
			myIterator = pAnalysis.getBuckets().listIterator();
			
			/* Loop through the buckets */
			while ((myCurr = myIterator.next()) != null) {
				/* If this is an asset bucket */
				if (myCurr instanceof AssetBucket) {
					/* If we still have units */
					myAsset = (AssetBucket) myCurr;
					if (myAsset.getUnits().isNonZero()) {
						/* Copy and add the bucket */
						myAsset = new AssetBucket(theBuckets, myAsset);
						myAsset.addToList();
					}
				}
				
				/* If this is an money bucket */
				else if (myCurr instanceof MoneyBucket) {
					/* If we still have a value */
					if (myCurr.getAmount().isNonZero()) {
						/* Copy and add the bucket */
						myBucket = new MoneyBucket(theBuckets, (MoneyBucket)myCurr);
						myBucket.addToList();
					}
				}
				
				/* Else this is a standard bucket */
				else {
					/* If we still have a value */
					if (myCurr.getAmount().isNonZero()) {
						/* Copy and add the bucket */
						myBucket  = new Bucket(theBuckets, myCurr);
						myBucket.addToList();
					}
				}
		    }
		}			
	}
	
	/**
	 * Process an event
	 * 
	 * @param pEvent the event to process
	 */
	public void processEvent(Event pEvent) {
		Account myCredit;
		Account myDebit;
		Bucket  myBucket;

		/* Access the credit and debit accounts */
		myCredit = pEvent.getCredit();
		myDebit  = pEvent.getDebit();
		
		/* If we are only interested in a single account */
		if (theAccount != null) {
			/* Null values if they do not match the account */
			if (theAccount.compareTo(myCredit) != 0) myCredit = null;
			if (theAccount.compareTo(myDebit)  != 0) myDebit  = null;
		}
		
		/* If the credit account is an asset */
		if ((myCredit != null) &&
			(!myCredit.isExternal()) && 
			(!myCredit.isBenefit())) {
			/* Locate its bucket */
			myBucket = theBuckets.getAccountBucket(myCredit);
			
			/* Add the Event to the bucket */
			myBucket.addEvent(pEvent);
		}
		
		/* If the debit account is an asset */
		if ((myDebit != null) &&
		    (!myDebit.isExternal()) &&
			(!pEvent.isInterest())) {
			/* Locate its bucket */
			myBucket = theBuckets.getAccountBucket(myDebit);
			
			/* Subtract the Event from the bucket */
			myBucket.subtractEvent(pEvent);	
		}
	}
	
	/**
	 * Produce the totals
	 */
	public void produceTotals() {
		List.ListIterator	myIterator;
		AccountType			myType;
		Bucket      		myCurr;
		Bucket      		myBucket;
		Bucket      		myTotals;
		
		/* If we have not previously totalled */
		if (!hasTotals) {
			/* Access the totals bucket */
			myTotals = theBuckets.getTotalsBucket();
		
			/* Access the iterator */
			myIterator = theBuckets.listIterator();
			
			/* Loop through the items */
			while ((myCurr = myIterator.next()) != null) {
				/* Break loop if we are not a detail bucket */
				if (!myCurr.isDetail()) break;
				
				/* Access the Type */
				myType    = myCurr.getType();
								
				/* Access the relevant summary bucket */
				myBucket = theBuckets.getTypeBucket(myType);
			
				/* Add the amount to summary and totals */
				myBucket.addBucket(myCurr);
				myTotals.addBucket(myCurr);			
			}
		
			/* Prune the list */
			theBuckets.prune();
			
			/* Note that we have produced totals */
			hasTotals = true;
		}
	}
	
	/**
	 * Produce the market totals
	 */
	public void produceMarketTotals() {
		List.ListIterator	myIterator;
		Bucket      		myCurr;
		AssetBucket      	myTotals;
		
		/* If we have not previously totalled */
		if (!hasTotals) {
			/* Access the totals bucket */
			myTotals = theBuckets.getMarketTotalsBucket();
		
			/* Access the iterator */
			myIterator = theBuckets.listIterator();
			
			/* Loop through the items to find the match */
			while ((myCurr = myIterator.next()) != null) {
				/* Break loop if we are not a detail bucket */
				if (!myCurr.isDetail()) break;
				
				/* Ignore if this is not an Asset Bucket */
				if (!(myCurr instanceof AssetBucket)) continue;
				
				/* Add the amount to totals */
				myTotals.addMarketBucket((AssetBucket)myCurr);			
			}
		
			/* Prune the list */
			theBuckets.pruneMarket();
			
			/* Note that we have produced totals */
			hasTotals = true;
		}
	}
	
	/**
	 * Value Priced assets
	 */
	public void valuePricedAssets() {
		List.ListIterator	myIterator;
		Account     		myAccount;
		Bucket      		myCurr;
		AssetBucket			myAsset;
		MoneyBucket			myMoney;
		Price 				myPrice;
		Price.List			myPrices;
		Rate  				myRate;
		Rate.List			myRates;
		Date        		myDate;

		/* Access the Rates/Prices */
		myRates  = theData.getRates();
		myPrices = theData.getPrices();
		
		/* Access the iterator */
		myIterator = theBuckets.listIterator();
		
		/* Loop through the items to find the match */
		while ((myCurr = myIterator.next()) != null) {
			/* Break loop if we are not a detail bucket */
			if (!myCurr.isDetail()) break;
			
			/* Access the Account */
			myAccount 	= myCurr.getAccount();
		
			/* If this is not an Asset Bucket */
			if (myCurr instanceof AssetBucket) {
				/* Access the Asset */
				myAsset 	= (AssetBucket) myCurr;
				
				/* Get the appropriate price */
				myPrice = myPrices.getLatestPrice(myAccount, getDate());
				
				/* Determine the number of units */
				myAsset.setPrice(myPrice.getPrice());
								
				/* If we have a set to update */
				if (theYear != null) {
					/* Adjust the IncomeAnalysis appropriately */
					theYear.getIncomeAnalysis().addMarketMovement(myAsset);
				
					/* Adjust the TaxAnalysis appropriately */ 
					theYear.getTaxAnalysis().addMarketMovement(myAsset);
				}
			}
			
			/* else if this has rates */
			else if (myCurr instanceof MoneyBucket) {
				/* Access the Money */
				myMoney	= (MoneyBucket) myCurr;
				
				/* Get the appropriate Rate */
				myRate = myRates.getLatestRate(myAccount, getDate());
				myDate = myAccount.getMaturity();
				
				/* If we have a rate */
				if (myRate != null) {
					/* Use Rate date if no maturity */
					if (myDate == null)	myDate = myRate.getDate();
					
					/* Record the rate */
					myMoney.setRate(myRate.getRate(), myDate);
				}
			}
		}
	}
	
	/* List of buckets */
	public class List extends DataList<Bucket> {
		/**
		 * Construct a top-level List
		 */
		public List() { super(ListStyle.VIEW, false); }

		/** 
	 	 * Clone a Bucket list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return null; }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pItem the item to add
		 * @return the newly added item
		 */
		public DataItem addNewItem(DataItem pItem) { return null; }
	
		/**
		 * Add a new item to the edit list
		 * 
		 * @param isCredit - ignored
		 */
		public void addNewItem(boolean isCredit) { return; }
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return Bucket.objName; }		

		/**
		 * Add additional fields to HTML String
		 * @param pBuffer the string buffer 
		 */
		public void addHTMLFields(StringBuilder pBuffer) {
			/* Start the Fields section */
			pBuffer.append("<tr><th rowspan=\"2\">Fields</th></tr>");
				
			/* Format the date */
			pBuffer.append("<tr><td>Date</td><td>"); 
			pBuffer.append(Utils.formatDate(theDate)); 
			pBuffer.append("</td></tr>"); 
		}
		
		/**
		 * Search for a particular bucket
		 * 
		 * @param pName  Name of Item
		 * @param pOrder Order of Item
		 * @param pType  Type of bucket
		 * @return The Item if present (or null)
		 */
		protected Bucket searchFor(String pName, int pOrder, BucketType pType) {
			ListIterator	myIterator;
			Bucket     		myCurr;
			BucketType 		myType;
			String     		myName;
			int        		iDiff;
		
			/* Access the iterator */
			myIterator = theBuckets.listIterator();
			
			/* Loop through the items to find the match */
			while ((myCurr = myIterator.next()) != null) {
				myType = myCurr.getBucket();
				if (AnalysisYear.getBucketOrder(myType) < AnalysisYear.getBucketOrder(pType)) continue;
				if (AnalysisYear.getBucketOrder(myType) > AnalysisYear.getBucketOrder(pType)) 
					{ myCurr = null; break; };
				if (myCurr.theOrder < pOrder) continue;
				if (myCurr.theOrder > pOrder)
					{ myCurr = null; break; };
				myName = myCurr.getName();
				if (myName == null) break;
				iDiff = myName.compareTo(pName);
				if (iDiff == 0) break;
				if (iDiff > 0)
					{ myCurr = null; break; }
			}
		
			/* Return to caller */
			return myCurr;
		}
		
		/**
		 * Search for a particular bucket
		 * 
		 * @param pType Type of bucket
		 * @return The Item if present (or null)
		 */
		protected Bucket searchFor(BucketType pType) {
			ListIterator	myIterator;
			Bucket     		myCurr;
			BucketType 		myType;
		
			/* Access the iterator */
			myIterator = theBuckets.listIterator();
			
			/* Loop through the items to find the match */
			while ((myCurr = myIterator.next()) != null) {
				myType = myCurr.getBucket();
				if (AnalysisYear.getBucketOrder(myType) < AnalysisYear.getBucketOrder(pType)) continue;
				if (AnalysisYear.getBucketOrder(myType) > AnalysisYear.getBucketOrder(pType)) 
					{ myCurr = null; break; };
				break;
			}
		
			/* Return to caller */
			return myCurr;
		}
		
		/**
		 * Obtain or allocate a bucket for an account
		 * 
		 * @param  pAccount Financial details
		 * @return The Asset Bucket
		 */
		protected Bucket getAccountBucket(Account pAccount) {
			Bucket   myBucket;
			
			/* Find an existing asset bucket */
			myBucket = searchFor(pAccount.getName(), pAccount.getOrder(), BucketType.DETAIL);
					
			/* If we did not find a bucket */
			if (myBucket == null) {
				/* If this is a money account */
				if (pAccount.isMoney()) {
					/* Allocate a new AssetBucket and add to the list */
					myBucket = new MoneyBucket(this, pAccount);
				}
				
				/* else if this is a priced account */
				else if (pAccount.isPriced()) {
					/* Allocate a new AssetBucket and add to the list */
					myBucket = new AssetBucket(this, pAccount);
				}
				
				/* else allocate a standard bucket */
				else {
					/* Allocate a new bucket and add to the list */
					myBucket = new Bucket(this, pAccount);
				}
				
				/* Add it to the list */
				myBucket.addToList();
			}
					
			/* Return the bucket */
			return myBucket;
		}
		
		/**
		 * Obtain or allocate a bucket for an account type
		 * 
		 * @param  pType the account type
		 * @return The Asset Bucket
		 */
		protected Bucket getTypeBucket(AccountType pType) {
			Bucket   myBucket;
			
			/* Find an existing asset bucket */
			myBucket = searchFor(pType.getName(), pType.getOrder(), BucketType.SUMMARY);
					
			/* If we did not find a bucket */
			if (myBucket == null) {
				/* Allocate a new bucket and add to the list */
				myBucket = new Bucket(this, pType);
				myBucket.addToList();
			}
					
			/* Return the bucket */
			return myBucket;
		}
		
		/**
		 * Obtain or allocate a bucket for a totals
		 * 
		 * @return The Asset Bucket
		 */
		protected Bucket getTotalsBucket() {
			Bucket   myBucket;
			
			/* Find an existing totals bucket */
			myBucket = searchFor(BucketType.TOTAL);
					
			/* If we did not find a bucket */
			if (myBucket == null) {
				/* Allocate a new bucket and add to the list */
				myBucket = new Bucket(this, BucketType.TOTAL);
				myBucket.addToList();
			}
					
			/* Return the bucket */
			return myBucket;
		}
		
		/**
		 * Obtain or allocate a bucket for market totals
		 * 
		 * @return The Totals Bucket
		 */
		protected AssetBucket getMarketTotalsBucket() {
			AssetBucket   myBucket;
			
			/* Find an existing totals bucket */
			myBucket = (AssetBucket)searchFor(BucketType.TOTAL);
					
			/* If we did not find a bucket */
			if (myBucket == null) {
				/* Allocate a new bucket and add to the list */
				myBucket = new AssetBucket(this, BucketType.TOTAL);
				myBucket.addToList();
			}
					
			/* Return the bucket */
			return myBucket;
		}
		
		/**
		 * Resolve an asset list to remove null entries
		 */
		protected void prune() {
			ListIterator	myIterator;
			Bucket 			myCurr;
			AssetBucket		myAsset;
			
			/* Access the iterator */
			myIterator = theBuckets.listIterator();
			
			/* Loop through the items to find the match */
			while ((myCurr = myIterator.next()) != null) {
				/* If this is an asset bucket */
			    if (myCurr instanceof AssetBucket) {
			    	/* Access the Asset Bucket */
			    	myAsset = (AssetBucket) myCurr;
			    	
			    	/* If we have no units and no previous units */
			    	if ((!myAsset.getUnits().isNonZero()) &&
			    		((myAsset.getPrevious() == null) ||
			    		 (!myAsset.getPrevious().getUnits().isNonZero())))
			    		 /* Remove the item */
			    		 myIterator.remove();
			    }
			    
			    /* else this is a money bucket */
			    else { 
			    	/* If we have no amount and no previous amount */
			    	if ((!myCurr.getAmount().isNonZero()) &&
			    		((myCurr.getPrevious() == null) ||
			    		 (!myCurr.getPrevious().getAmount().isNonZero())))
			    		 /* Remove the item */
			    		 myIterator.remove();
			    }
			}
			
			/* Return */
			return;
		}
		
		/**
		 * Resolve an asset list to remove null entries
		 */
		protected void pruneMarket() {
			ListIterator	myIterator;
			Bucket 			myCurr;
			
			/* Access the iterator */
			myIterator = theBuckets.listIterator();
			
			/* Loop through the items to find the match */
			while ((myCurr = myIterator.next()) != null) {
				/* Break loop if we are not a detail bucket */
				if (!myCurr.isDetail()) break;
				
				/* If we have a Money Bucket */
			    if (myCurr instanceof MoneyBucket) { 
					/* Unlink the entry */
			    	myIterator.remove();
			    }
			}
			
			/* Return */
			return;
		}
		
		/**
		 * Mark active accounts
		 */
		public void markActiveAccounts() {
			ListIterator	myIterator;
			Account     	myAccount;
			Bucket     		myCurr;
			AssetBucket		myAsset;
		
			/* Access the iterator */
			myIterator = theBuckets.listIterator();
			
			/* Loop through the items to find the match */
			while ((myCurr = myIterator.next()) != null) {
				/* Break loop if we are not a detail bucket */
				if (!myCurr.isDetail()) break;
				
				/* Access the Amount and Account */
				myAccount = myCurr.getAccount();
			
				/* If this is a priced asset */
				if (myCurr instanceof AssetBucket){
					/* If the units are non-zero */
					myAsset = (AssetBucket) myCurr;
					if (myAsset.getUnits().isNonZero()) {
						/* Set the account as non-closeable */
						myAccount.setNonCloseable();
					}
				}
				
				/* else this is a money asset */
				else {
					/* If the money is non-zero */
					if (myCurr.getAmount().isNonZero()) {
						/* Set the account as non-closeable */
						myAccount.setNonCloseable();
					}
				}
			}
		}
	}
	
	/* The item class */
	public class Bucket extends DataItem {
		/**
		 * The name of the object
		 */
		private static final String objName = "AssetBucket";

		/* Members */
		private		String			theName      	= null;
		private 	int             theOrder 	 	= -1;
		private 	BucketType		theBucket	 	= null;
		private 	Account       	theAccount   	= null;
		private		AccountType 	theType      	= null;
		private		Number.Money    theAmount    	= null;
		private 	Bucket			thePrevious		= null;
		
		/* Access methods */
		public 	String			getName()      { return theName; }
		protected int  			getOrder()     { return theOrder; }
		public 	BucketType		getBucket()    { return theBucket; }
		public 	Account       	getAccount()   { return theAccount; }
		public 	AccountType 	getType()      { return theType; }
		public 	Date        	getDate()      { return theDate; }
		public 	Number.Money    getAmount()    { return theAmount; }
		public 	Bucket    		getPrevious()  { return thePrevious; }
		public 	Number.Money    getPrevAmount(){ return (thePrevious == null) ? null : thePrevious.theAmount; }
		private boolean     		  isDetail()   { 
			return (theBucket == BucketType.DETAIL); }

		/* Constructors */
		private Bucket(List pList) { 
			super(pList, 0);
			theAmount   = new Number.Money(0);
		}
		private Bucket(List pList, Account pAccount) { 
			super(pList, 0);
			theName     = pAccount.getName();
			theType     = pAccount.getActType();
			theBucket   = BucketType.DETAIL;
			theOrder    = pAccount.getOrder();
			theAccount  = pAccount;
			theAmount   = new Number.Money(0);
		}
		private Bucket(List pList, AccountType pType) { 
			super(pList, 0);
			theName     = pType.getName();
			theType     = pType;
			theBucket   = BucketType.SUMMARY;
			theOrder    = pType.getOrder();
			theAmount   = new Number.Money(0);
			thePrevious = new Bucket(pList);
		}
		private Bucket(List pList, BucketType pType) { 
			super(pList, 0);
			theBucket   = pType;
			theOrder    = 0;
			theAmount   = new Number.Money(0);
			thePrevious = new Bucket(pList);
		}
		private Bucket(List pList, Bucket pBucket) {
			super(pList, 0);
			theName     = pBucket.getName();
			theOrder    = pBucket.getOrder();
			theBucket   = pBucket.getBucket();
			theAccount  = pBucket.getAccount();
			theType     = pBucket.getType();
			theAmount   = new Number.Money(0);
			thePrevious = pBucket;
			theAmount   = new Number.Money(pBucket.getAmount());
		}
		
		/* Field IDs */
		public static final int FIELD_NAME  	= 0;
		public static final int FIELD_TYPE  	= 1;
		public static final int FIELD_ACTTYPE 	= 2;
		public static final int FIELD_ORDER  	= 3;
		public static final int FIELD_VALUE  	= 4;
		public static final int NUMFIELDS	    = 5;
		
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
				case FIELD_NAME: 		return "Name";
				case FIELD_TYPE: 		return "Type";
				case FIELD_ORDER: 		return "Order";
				case FIELD_ACTTYPE: 	return "AccountType";
				case FIELD_VALUE:		return "Value";
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
			String myString = ""; 
			switch (iField) {
				case FIELD_NAME: 			
					myString += theName;
					break;
				case FIELD_TYPE:		
					myString += theBucket;
					break;
				case FIELD_ORDER: 		
					myString += theOrder;
					break;
				case FIELD_ACTTYPE: 	
					myString += Utils.formatAccountType(theType);
					break;
				case FIELD_VALUE: 	
					myString += Utils.formatMoney(theAmount);
					break;
			}
			return myString;
		}

		/**
		 * Compare this Bucket to another to establish equality.
		 * 
		 * @param pThat The Bucket to compare to
		 * @return <code>true</code> if the bucket is identical, <code>false</code> otherwise
		 */
		public boolean equals(Object pThat) {
			/* Handle the trivial cases */
			if (this == pThat) return true;
			if (pThat == null) return false;
			
			/* Make sure that the object is an AssetBucket */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the object as a Bucket */
			Bucket myBucket = (Bucket)pThat;
			
			/* Check for equality */
			if (Utils.differs(getName(),      	myBucket.getName())) 		return false;
			if (getBucket() != myBucket.getBucket()) 						return false;
			if (getOrder() 	!= myBucket.getOrder()) 						return false;
			if (Utils.differs(getType(),    	myBucket.getType())) 		return false;
			if (Utils.differs(getAmount(),    	myBucket.getAmount())) 		return false;
			return true;
		}

		/**
		 * Compare this Bucket to another to establish sort order.
		 * 
		 * @param pThat The Bucket to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		public int compareTo(Object pThat) {
			int result;

			/* Handle the trivial cases */
			if (this == pThat) return 0;
			if (pThat == null) return -1;
			
			/* Make sure that the object is a Bucket */
			if (!(pThat instanceof Bucket)) return -1;
			
			/* Access the object as a Bucket */
			Bucket myThat = (Bucket)pThat;
			
			/* Compare the bucket order */
			if (AnalysisYear.getBucketOrder(theBucket) < AnalysisYear.getBucketOrder(myThat.theBucket)) return -1;
			if (AnalysisYear.getBucketOrder(theBucket) > AnalysisYear.getBucketOrder(myThat.theBucket)) return  1;
			
			/* Compare the order */
			if (theOrder < myThat.theOrder) return -1;
			if (theOrder > myThat.theOrder) return  1;
			
			/* Compare the name */
			result = theName.compareTo(myThat.theName);
			if (result == 0) return 0;
			else if (result < 0) return -1;
			else return 1;
		}

		/**
		 * Add the event to the bucket
		 * 
		 * @param  pEvent Event to add
		 */
		protected void addEvent(Event pEvent) {
			Number.Money myAmount = pEvent.getAmount();

			/* Adjust the money total */
			theAmount.addAmount(myAmount);
		}
		
		/**
		 * Subtract the event from the bucket
		 * 
		 * @param  pEvent Event to subtract
		 */
		protected void subtractEvent(Event pEvent) {
			Number.Money myAmount = pEvent.getAmount();

			/* Adjust the money total */
			theAmount.subtractAmount(myAmount);
		}

		/**
		 * Add a bucket to a totalling bucket
		 * 
		 * @param  pBucket Bucket to add
		 */
		protected void addBucket(Bucket pBucket) {
			Number.Money myAmount = pBucket.getAmount();

			/* Adjust the money total */
			theAmount.addAmount(myAmount);
			if ((thePrevious != null) && (pBucket.getPrevious() != null))
				thePrevious.addBucket(pBucket.getPrevious());
		}		
	}
	
	/* The item class for Assets */
	public class AssetBucket extends Bucket {
		/* Members */
		private		Number.Units	theUnits     		= null;
		private		Number.Price	thePrice     		= null;
		private		Number.Money    theInvestment  		= null;
		private		Number.Money    theDividends   		= null;
		private		Number.Money    theCost 	    	= null;
		private		Number.Money    theRealisedGains	= null;
		private		Number.Money    theProfit			= null;
		private		Number.Money    theMarket    		= null;
		
		/* Access methods */
		public 	Number.Units    getUnits()     		{ return theUnits; }
		public 	Number.Price    getPrice()     		{ return thePrice; }
		public 	Number.Money    getInvestment()		{ return theInvestment; }
		public 	Number.Money    getDividends() 		{ return theDividends; }
		public 	Number.Money    getMarket()    		{ return theMarket; }
		public 	Number.Money    getCost()			{ return theCost; }
		public 	Number.Money    getRealisedGains() 	{ return theRealisedGains; }
		public 	Number.Money    getProfit()			{ return theProfit; }
		public 	AssetBucket		getPrevious()  		{ return (AssetBucket)super.getPrevious(); }

		/* Constructors */
		private AssetBucket(List pList, Account pAccount) { 
			super(pList, pAccount);
			theUnits     		= new Number.Units(0);
			theInvestment		= new Number.Money(0);
			theDividends		= new Number.Money(0);
			theCost				= new Number.Money(0);
			theRealisedGains	= new Number.Money(0);
		}
		private AssetBucket(List pList, AssetBucket pBucket) {
			super(pList, pBucket);
			theUnits     		= new Number.Units(pBucket.getUnits());
			theCost     		= new Number.Money(pBucket.getCost());
			theRealisedGains 	= new Number.Money(pBucket.getRealisedGains());
			theInvestment		= new Number.Money(0);
			theDividends		= new Number.Money(0);
		}
		private AssetBucket(List pList, BucketType pType) {
			super(pList, pType);
			theInvestment		= new Number.Money(0);
			theMarket			= new Number.Money(0);
			theDividends		= new Number.Money(0);
			theCost				= new Number.Money(0);
			theRealisedGains	= new Number.Money(0);
			theProfit			= new Number.Money(0);
		}
		
		/* Field IDs */
		public static final int FIELD_UNITS  	= Bucket.NUMFIELDS;
		public static final int FIELD_PRICE	    = Bucket.NUMFIELDS + 1;
		public static final int FIELD_INVEST	= Bucket.NUMFIELDS + 2;
		public static final int FIELD_DIVIDEND  = Bucket.NUMFIELDS + 3;
		public static final int FIELD_COST	    = Bucket.NUMFIELDS + 4;
		public static final int FIELD_GAINS	    = Bucket.NUMFIELDS + 5;
		public static final int FIELD_PROFIT	= Bucket.NUMFIELDS + 6;
		public static final int FIELD_MARKET	= Bucket.NUMFIELDS + 7;
		public static final int NUMFIELDS	    = Bucket.NUMFIELDS + 8;
		
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
				case FIELD_UNITS:		return "Units";
				case FIELD_PRICE:		return "Price";
				case FIELD_INVEST:		return "Investment";
				case FIELD_DIVIDEND:	return "Dividends";
				case FIELD_COST:		return "Cost";
				case FIELD_GAINS:		return "Gains";
				case FIELD_PROFIT:		return "Profit";
				case FIELD_MARKET:		return "Market";
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
			String myString = ""; 
			switch (iField) {
				case FIELD_UNITS: 	
					myString += Utils.formatUnits(theUnits);
					break;
				case FIELD_PRICE: 	
					myString += Utils.formatPrice(thePrice);
					break;
				case FIELD_INVEST: 	
					myString += Utils.formatMoney(theInvestment);
					break;
				case FIELD_DIVIDEND: 	
					myString += Utils.formatMoney(theDividends);
					break;
				case FIELD_COST: 	
					myString += Utils.formatMoney(theCost);
					break;
				case FIELD_GAINS: 	
					myString += Utils.formatMoney(theRealisedGains);
					break;
				case FIELD_PROFIT: 	
					myString += Utils.formatMoney(theProfit);
					break;
				case FIELD_MARKET: 	
					myString += Utils.formatMoney(theMarket);
					break;
				default: 	
					myString += super.formatField(iField, pObj);
					break;
			}
			return myString;
		}

		/**
		 * Compare this Bucket to another to establish equality.
		 * 
		 * @param pThat The Bucket to compare to
		 * @return <code>true</code> if the bucket is identical, <code>false</code> otherwise
		 */
		public boolean equals(Object pThat) {
			/* Handle the trivial cases */
			if (this == pThat) return true;
			if (pThat == null) return false;
			
			/* Make sure that the object is an AssetBucket */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the object as an AssetBucket */
			AssetBucket myBucket = (AssetBucket)pThat;
			
			/* Check for equality */
			if (Utils.differs(getName(),      		myBucket.getName())) 		return false;
			if (getBucket() != myBucket.getBucket()) 							return false;
			if (getOrder() 	!= myBucket.getOrder()) 							return false;
			if (Utils.differs(getType(),    		myBucket.getType())) 		return false;
			if (Utils.differs(getAmount(),    		myBucket.getAmount())) 		return false;
			if (Utils.differs(getUnits(),    		myBucket.getUnits())) 		return false;
			if (Utils.differs(getPrice(),    		myBucket.getPrice())) 		return false;
			if (Utils.differs(getInvestment(),    	myBucket.getInvestment())) 	return false;
			if (Utils.differs(getDividends(),    	myBucket.getDividends())) 	return false;
			if (Utils.differs(getCost(),    		myBucket.getCost())) 		return false;
			if (Utils.differs(getRealisedGains(),   myBucket.getRealisedGains()))return false;
			if (Utils.differs(getProfit(),    		myBucket.getProfit())) 		return false;
			if (Utils.differs(getMarket(),    		myBucket.getMarket())) 		return false;
			return true;
		}

		/**
		 * Add the event to the bucket
		 * 
		 * @param  pEvent Event to add
		 */
		protected void addEvent(Event pEvent) {
			Number.Money myAmount = pEvent.getAmount();
			Number.Units myUnits  = pEvent.getUnits();

			/* Adjust the investment total */
			theInvestment.addAmount(myAmount);
			
			/* Adjust the cost base */
			theCost.addAmount(myAmount);
			
			/* If we need to adjust units */
			if ((myUnits != null) && (theUnits != null)) {
				/* Adjust the units total */
				theUnits.addUnits(myUnits);
			}
		}
		
		/**
		 * Subtract the event from the bucket
		 * 
		 * @param  pEvent Event to subtract
		 */
		protected void subtractEvent(Event pEvent) {
			Number.Money myAmount 		= pEvent.getAmount();
			Number.Money myTax	  		= pEvent.getTaxCredit();
			Number.Units myUnits  		= pEvent.getUnits();
			Number.Money myReduction;

			/* If this is a dividend */
			if (pEvent.getTransType().isDividend()) {
				/* Adjust the dividends total */
				theDividends.addAmount(myAmount);				
				if (myTax != null)
					theDividends.addAmount(myTax);				
			}
			
			/* else this a transfer of capital */
			else {
				/* Adjust the investment total */
				theInvestment.subtractAmount(myAmount);
				if (myTax != null)
					theInvestment.subtractAmount(myTax);				
				
				/* If we are reducing units */
				if ((myUnits != null) && (theUnits != null) &&
					(Utils.differs(pEvent.getDebit(), pEvent.getCredit()))) {
					/* Calculate the cost reduction */
					myReduction = theCost.valueAtWeight(myUnits, theUnits);
					
				/* else if the cost reduction is more than the cost */
				} else if (myAmount.getAmount() > theCost.getAmount())  {
					/* Reduce the cost to zero */
					myReduction = new Number.Money(theCost);
					
				/* else */	
				} else {
					/* Set the reduction as the amount */
					myReduction = new Number.Money(myAmount);
				}
				
				/* Reduce the cost */
				theCost.subtractAmount(myReduction);
			
				/* Adjust the realised gains */
				theRealisedGains.addAmount(myAmount);
				theRealisedGains.subtractAmount(myReduction);
			}
			
			/* If we need to adjust units */
			if ((myUnits != null) && (theUnits != null)) {
				/* Adjust the units total */
				if (pEvent.getTransType().isStockTakeover())
					theUnits.setZero();
				else if (!pEvent.getCredit().isPriced())
					theUnits.subtractUnits(myUnits);
			}
		}

		/**
		 * Add a bucket to a totalling market bucket
		 * 
		 * @param  pBucket Bucket to add
		 */
		protected void addMarketBucket(AssetBucket pBucket) {
			/* Adjust the value */
			super.addBucket(pBucket);
			
			/* Add Dividends back into investment */
			pBucket.getInvestment().addAmount(pBucket.getDividends());
			
			/* Adjust totals */
			theInvestment.addAmount(pBucket.getInvestment());
			theDividends.addAmount(pBucket.getDividends());
			theMarket.addAmount(pBucket.getMarket());
			theCost.addAmount(pBucket.getCost());
			theRealisedGains.addAmount(pBucket.getRealisedGains());
			theProfit.addAmount(pBucket.getProfit());
		}
		
		/**
	 	 * Set a price and valuation for asset
	 	 * @param  pPrice price for asset
	 	 */
		protected void setPrice(Number.Price pPrice) {
			/* Record the price of the asset */
			thePrice  = new Number.Price(pPrice);

			/* Record the value of the asset */
			super.theAmount = theUnits.valueAtPrice(pPrice);
			
			/* Calculate the market movement */
			calculateMarketMovement();
			
			/* Calculate the Profit */
			theProfit = new Number.Money(getAmount());
			theProfit.subtractAmount(theCost);
		}

		/**
		 * Calculate market movement for an asset 
		 */
		private void calculateMarketMovement() {
			/** Calculate the market movement 
			 * This is defined as is NewValue - OldValue - Investment
			 */
			Number.Money myMovement = new Number.Money(getAmount());
			
			/* Subtract any investment this year */
			myMovement.subtractAmount(theInvestment);
			
			/* If we have a previous value */
			if (getPrevious() != null) {
				/* Subtract it */
				myMovement.subtractAmount(getPrevious().getAmount());
			}
			
			/* Store it as the movement */
			theMarket = myMovement;
			return;
		}
	}
	
	/* The item class for Money */
	public class MoneyBucket extends Bucket {
		/* Members */
		private		Number.Rate		theRate     	= null;
		private		Date			theDate     	= null;
		
		/* Access methods */
		public 	Number.Rate    	getRate()     	{ return theRate; }
		public 	Date    		getDate()     	{ return theDate; }
		public 	MoneyBucket		getPrevious()  	{ return (MoneyBucket)super.getPrevious(); }

		/* Constructors */
		private MoneyBucket(List pList, Account pAccount) { 
			super(pList, pAccount);
		}
		private MoneyBucket(List pList, MoneyBucket pBucket) {
			super(pList, pBucket);
		}
		
		/* Field IDs */
		public static final int FIELD_RATE  	= Bucket.NUMFIELDS;
		public static final int FIELD_DATE	    = Bucket.NUMFIELDS + 1;
		public static final int NUMFIELDS	    = Bucket.NUMFIELDS + 2;
		
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
				case FIELD_RATE:		return "Rate";
				case FIELD_DATE:		return "Date";
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
			String myString = ""; 
			switch (iField) {
				case FIELD_RATE: 	
					myString += Utils.formatRate(theRate);
					break;
				case FIELD_DATE: 	
					myString += Utils.formatDate(theDate);
					break;
			}
			return myString;
		}

		/**
		 * Compare this Bucket to another to establish equality.
		 * 
		 * @param pThat The Bucket to compare to
		 * @return <code>true</code> if the bucket is identical, <code>false</code> otherwise
		 */
		public boolean equals(Object pThat) {
			/* Handle the trivial cases */
			if (this == pThat) return true;
			if (pThat == null) return false;
			
			/* Make sure that the object is a MoneyBucket */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the object as a MoneyBucket */
			MoneyBucket myBucket = (MoneyBucket)pThat;
			
			/* Check for equality */
			if (Utils.differs(getName(),    myBucket.getName()))	return false;
			if (getBucket() != myBucket.getBucket()) 				return false;
			if (getOrder() 	!= myBucket.getOrder()) 				return false;
			if (Utils.differs(getType(),    myBucket.getType())) 	return false;
			if (Utils.differs(getAmount(),  myBucket.getAmount())) 	return false;
			if (Utils.differs(getRate(),    myBucket.getRate())) 	return false;
			if (Utils.differs(getDate(),	myBucket.getDate())) 	return false;
			return true;
		}

		/**
	 	 * Set a Rate and date for the account
	 	 * @param  pPrice price for asset
	 	 */
		protected void setRate(Number.Rate pRate,
							   Date		   pDate) {
			/* Record the rate and date of the account */
			theRate  = new Number.Rate(pRate);
			if (pDate != null) theDate = new Date(pDate);
		}
	}
}
