package finance;

import finance.finData.Event;
import finance.finData.TaxParms;
import finance.finData.Account;
import finance.finLink.linkCtl;
import finance.finLink.linkObject;
import finance.finStatic.TaxClass;
import finance.finStatic.TransClass;

public class finAnalysis {
	/* Members */
	private finData         theData    = null;

	/* Access methods */
	public finData         getData()      { return theData; }
 	
 	/* Constructor */
	public finAnalysis(finData pData) {
		theData    = pData;
	}
	
	/* Report Set class */
	public class List {
		/* Linking fields */
		private Set theFirst = null;
		private Set theLast  = null;
		
		/* Linking methods */
		public Set getFirst() { return theFirst; }
		public Set getLast()  { return theLast; }
		
		/* Constructor */
		public List() {
			Event            	myCurr;
			int              	myResult	= -1;
			TaxParms         	myTax  		= null;
			finObject.Date   	myDate 		= null;
			Set           	 	mySet  		= null;
			finData.TaxParmList	myList;

			/* Access the tax years list */
			myList = theData.getTaxYears();
			
			/* Loop through the Events extracting relevant elements */
			for (myCurr = theData.getEvents().getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				
				/* Ignore deleted events */
				if (myCurr.isDeleted()) continue;

				/* If we have a current tax year */
				if (myTax != null) {
					/* Check that this event is still in the tax year */
					myResult = myDate.compareTo(myCurr.getDate());
				}
				
				/* If we have exhausted the tax year or else this is the first tax year */
				if (myResult == -1) { 
					/* Access the relevant tax year */
					myTax  = myList.searchFor(myCurr.getDate());
					myDate = myTax.getDate();
			
					/* If we have an existing set */
					if (mySet != null) {
						/* Value priced assets */
						mySet.theAssets.valuePricedAssets();
					}
					
					/* Create the new report set */
					mySet = new Set(myTax);
					
					/* If this is not the first set */
					if (theLast != null) {
						/* Add it to the list */
						theLast.theNext = mySet;
						mySet.thePrev   = theLast;
						theLast         = mySet;
					
						/* Seed the reports from the previous set */
						mySet.seedReports();
					}
					
					/* else this is the first set */
					else {
						/* Add it to the list */
						theFirst = mySet;
						theLast  = mySet;						
					}
				}
							
				/* Touch credit and debit accounts */
				myCurr.getCredit().touchAccount(myCurr);
				myCurr.getDebit().touchAccount(myCurr);			
				
				/* Process the event in the report set */
				mySet.processEvent(myCurr);
				myTax.setActive();
			}
			
			/* Value priced assets of the most recent set */
			if (mySet != null) mySet.theAssets.valuePricedAssets();
		}
		
		/**
		 * Add a new item (no-op)
		 */
		public finLink.itemElement addNewItem(finLink.itemElement pElement) {
			return null;}
		
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "Set"; }
				
		/**
		 * Search for tax year 
		 * 
		 */
		public Set searchFor(finData.TaxParms pYear) {
			Set myCurr;
			
			/* Loop through the tax parameters */
			for (myCurr  = getFirst();
			     myCurr != null;
			     myCurr  = myCurr.getNext()) {
				/* Break on match */
				if (myCurr.theYear.compareTo(pYear) == 0)
					break;
			}
			
			/* Return to caller */
			return myCurr;
		}		
	}
				
	public class Set {
		/* Members */
		private Set				 theNext	= null;
		private Set 			 thePrev	= null;
		private finData.TaxParms theYear    = null;
		private finObject.Date   theDate    = null;
		private Asset        	 theAssets  = null;
		private Income       	 theIncome  = null;
		private Tax          	 theTax     = null;
		
		/* Linking methods */
		public Set getNext() { return theNext; }
		public Set getPrev() { return thePrev; }
		
		/* Access methods */
		public Asset               getAssetReport()  { return theAssets; }
		public Income              getIncomeReport() { return theIncome; }	
		public Tax                 getTaxReport()    { return theTax; }
		
		/* Constructor */
		public Set(finData.TaxParms pYear) {
			theYear   = pYear;
			theDate   = pYear.getDate();
			theAssets = new Asset(theDate, this);
			theIncome = new Income(theDate);
			theTax    = new Tax(theYear);
		}	
		
		/**
		 * Process an event
		 * 
		 * @param pEvent the event to process
		 */
		public void processEvent(finData.Event pEvent) {
			/* If the event is asset related */
			if (pEvent.isAssetRelated()) {
				/* Process in the asset report */
				theAssets.processEvent(pEvent);
			}
			
			/* Process in the income report */
			theIncome.processEvent(pEvent);
			
			/* Process in the tax report */
			theTax.processEvent(pEvent);		
		}
		
		/**
		 * Seed a Set from the preceding set
		 */
		public void seedReports() {
			Set mySet = getPrev();
			
			/* If there is a previous report */
			if (mySet != null ){
				/* Seed the Underlying reports */
				theAssets.seedReport(mySet.getAssetReport());
				theIncome.seedReport(mySet.getIncomeReport());
				theTax.seedReport(mySet.getTaxReport());
			}
		}
	}
	
	/* Bucket Types */
	public enum BucketType {
		STATIC,
		DETAIL,
		SUMMARY,
		TOTAL;
	}
	
	/* Bucket order */
	private int getBuckOrder(BucketType pBucket) {
		switch (pBucket) {
			case DETAIL: 	return 1;
			case SUMMARY: 	return 2;
			case TOTAL:  	return 3;
			default: 		return 0;
		}
	}
	
	/* Asset report class */
	public class Asset {
		/* Members */
		private Set            theSet	  = null;
		private finObject.Date theDate    = null;
		private List           theBuckets = null;
		private boolean		   hasTotals  = false;
		
		/* Access methods */
		public List           getBuckets() { return theBuckets; }
		public finObject.Date getDate()    { return theDate; }
		
		/* Constructor */
		public Asset() {
			theBuckets = new List();
		}
		
		/**
		 * Obtain an instant valuation report
		 * 
		 * @param  pDate The Date of the valuation
		 */
		public Asset(finObject.Date pDate) {
			Event myCurr;
			int   myResult;

			/* Initialise the buckets */
			theDate    = pDate;
			theBuckets = new List();
			
			/* Loop through the Events extracting relevant elements */
			for (myCurr = theData.getEvents().getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				
				/* Ignore deleted events */
				if (myCurr.isDeleted()) continue;
				
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
		 * @param  pDate The Date of the valuation
		 * @param  pSet  The Set of reports
		 */
		public Asset(finObject.Date pDate, Set pSet) {
			/* Store date and create new list */
			theDate    = pDate;
			theSet     = pSet;
			theBuckets = new List();
		}
			
		/**
		 * Seed from previous end-of-year report
		 * 
		 * @param  pReport The Previous report
		 */
		public void seedReport(Asset pReport) {
			Bucket myCurr;
			Bucket myBucket;
			
			/* If we have a report to copy */
			if (pReport != null) {
				
				/* Loop through the Buckets */
				for (myCurr = pReport.getBuckets().getFirst();
					 myCurr != null;
					 myCurr = myCurr.getNext()) {

					/* If we have a non-empty bucket */
					if ((myCurr.isPriced()) 
							? (myCurr.getUnits().isNonZero())
							: (myCurr.getAmount().isNonZero())) {
						/* Copy and add the bucket */
						myBucket  = new Bucket(theBuckets.theList, myCurr);
						myBucket.addToList();
					}
			    }
			}			
		}
		
		/**
		 * Process an event
		 * 
		 * @param pEvent the event to process
		 */
		public void processEvent(finData.Event pEvent) {
			finData.Account myAccount;
			Bucket     		myBucket;
			
			/* If the credit account is an asset */
			myAccount = pEvent.getCredit();
			if ((!myAccount.isExternal()) && 
				(!myAccount.isBenefit())) {
				/* Locate its bucket */
				myBucket = theBuckets.getAccountBucket(myAccount);
				
				/* Add the Event to the bucket */
				myBucket.addEvent(pEvent);
			}
			
			/* If the debit account is an asset */
			myAccount = pEvent.getDebit();
			if (!myAccount.isExternal()) {
				/* Locate its bucket */
				myBucket = theBuckets.getAccountBucket(myAccount);
				
				/* Subtract the Event from the bucket */
				myBucket.subtractEvent(pEvent);	
			}
		}
		
		/**
		 * Produce the totals
		 */
		public void produceTotals() {
			finStatic.AccountType myType;
			Bucket           	  myCurr;
			Bucket           	  myBucket;
			Bucket           	  myTotals;
			
			/* If we have not previously totalled */
			if (!hasTotals) {
				/* Access the totals bucket */
				myTotals = theBuckets.getTotalsBucket();
			
				/* Loop through the detail buckets */
				for (myCurr = theBuckets.getFirst();
					 (myCurr != null) && (myCurr.isDetail());
					 myCurr = myCurr.getNext()) {
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
			finStatic.AccountType myType;
			Bucket           	  myCurr;
			Bucket           	  myTotals;
			
			/* If we have not previously totalled */
			if (!hasTotals) {
				/* Access the totals bucket */
				myTotals = theBuckets.getTotalsBucket();
			
				/* Loop through the detail buckets */
				for (myCurr = theBuckets.getFirst();
					 (myCurr != null) && (myCurr.isDetail());
					 myCurr = myCurr.getNext()) {
					/* Access the Type */
					myType    = myCurr.getType();

					/* Ignore non-priced assets */
					if (!myType.isPriced()) continue;
					
					/* Add the amount to totals */
					myTotals.addMarketBucket(myCurr);			
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
			finData.Account       myAccount;
			finStatic.AccountType myType;
			Bucket           	  myCurr;
			finData.Price 		  myPrice;
			finData.PriceList	  myPrices;
			finData.Rate  		  myRate;
			finData.RateList	  myRates;
			finObject.Units       myUnits;
			finObject.Money       myValue;
			finObject.Date        myDate;
	
			/* Access the Rates/Prices */
			myRates  = theData.getRates();
			myPrices = theData.getPrices();
			
			/* Loop through the detail buckets */
			for (myCurr = theBuckets.getFirst();
				 (myCurr != null) && (myCurr.isDetail());
				 myCurr = myCurr.getNext()) {
				/* Access the Type and Account */
				myAccount = myCurr.getAccount();
				myType    = myCurr.getType();
				
				/* If this is a priced asset */
				if (myType.isPriced()) {
					/* Get the appropriate price */
					myPrice = myPrices.getLatestPrice(myAccount, getDate());
					
					/* Determine the number of units */
					myUnits = myCurr.getUnits();
					
					/* Calculate the new value */
					myValue = myUnits.valueAtPrice(myPrice.getPrice());
					
					/* Record the current amount as adjustment and the calculated value as amount */
					myCurr.theAdjust = myCurr.theAmount;
					myCurr.theAmount = new finObject.Money(myValue);
					myCurr.thePrice  = new finObject.Price(myPrice.getPrice());
					
					/* Calculate the market movement */
					myCurr.calculateMarketMovement();
					
					/* If we have a set to update */
					if (theSet != null) {
						/* Adjust the IncomeReport appropriately */
						theSet.getIncomeReport().addMarketMovement(myCurr);
					
						/* Adjust the TaxReport appropriately */ 
						theSet.getTaxReport().addMarketMovement(myCurr);
					}
				}
				
				/* else this has rates */
				else {
					/* Get the appropriate Rate */
					myRate = myRates.getLatestRate(myAccount, getDate());
					myDate = myAccount.getMaturity();
					
					/* If we have a rate */
					if (myRate != null) {
						/* Record the rate */
						myCurr.theRate  = new finObject.Rate(myRate.getRate());
						if (myDate == null)
							myDate          = myRate.getDate();
					}
					
					/* Record the date */
					myCurr.theDate = myDate;
				}
			}
		}
		
		public class List {
			/**
			 * The underlying list class
			 */
			private finLink.linkCtl   theList     = null;

			/**
			 * Get the first element of the list
			 * @return the first element (or <code>null</code>)
			 */
			protected Bucket getFirst()    { return (Bucket)theList.getFirst(); }

			/**
			 * Get the last element of the list
			 * @return the last element (or <code>null</code>)
			 */
			protected Bucket getLast()     { return (Bucket)theList.getLast(); }
			
			/**
			 * Construct a top-level List
			 */
			public List() { theList  = new finLink.linkCtl(true); }
	
			/**
			 * Search for a particular bucket
			 * 
			 * @param pName  Name of Item
			 * @param pOrder Order of Item
			 * @param pType  Type of bucket
			 * @return The Item if present (or null)
			 */
			protected Bucket searchFor(String pName, int pOrder, BucketType pType) {
				Bucket     myCurr;
				BucketType myType;
				String     myName;
				int        iDiff;
			
				/* Loop through the items to find the match */
				for (myCurr = getFirst();
			         myCurr != null;
			         myCurr = myCurr.getNext()) {
					myType = myCurr.getBucket();
					if (getBuckOrder(myType) < getBuckOrder(pType)) continue;
					if (getBuckOrder(myType) > getBuckOrder(pType)) 
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
				Bucket     myCurr;
				BucketType myType;
			
				/* Loop through the items to find the match */
				for (myCurr = getFirst();
			         myCurr != null;
			         myCurr = myCurr.getNext()) {
					myType = myCurr.getBucket();
					if (getBuckOrder(myType) < getBuckOrder(pType)) continue;
					if (getBuckOrder(myType) > getBuckOrder(pType)) 
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
			protected Bucket getAccountBucket(finData.Account pAccount) {
				Bucket   myBucket;
				
				/* Find an existing asset bucket */
				myBucket = searchFor(pAccount.getName(), pAccount.getOrder(), BucketType.DETAIL);
						
				/* If we did not find a bucket */
				if (myBucket == null) {
					/* Allocate a new bucket and add to the list */
					myBucket = new Bucket(theList, pAccount);
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
			protected Bucket getTypeBucket(finStatic.AccountType pType) {
				Bucket   myBucket;
				
				/* Find an existing asset bucket */
				myBucket = searchFor(pType.getName(), pType.getOrder(), BucketType.SUMMARY);
						
				/* If we did not find a bucket */
				if (myBucket == null) {
					/* Allocate a new bucket and add to the list */
					myBucket = new Bucket(theList, pType);
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
					myBucket = new Bucket(theList, BucketType.TOTAL);
					myBucket.addToList();
				}
						
				/* Return the bucket */
				return myBucket;
			}
			
			/**
			 * Resolve an asset list to remove null entries
			 */
			protected void prune() {
				Bucket myCurr;
				Bucket myNext;
				
				/* Loop through the Buckets */
				for (myCurr = getFirst();
				     myCurr != null;
				     myCurr = myNext) {
				
					/* Determine the next element */
					myNext = myCurr.getNext();
					
					/* If we have an empty bucket */
				    if ((myCurr.isPriced()) 
				    		? ((!myCurr.getUnits().isNonZero()) &&
				    		   (!myCurr.getOldUnits().isNonZero()))	
				    		: ((!myCurr.getAmount().isNonZero()) &&
				    		   (!myCurr.getOldAmount().isNonZero()))){
				   
						/* Unlink the entry */
				    	myCurr.unLink();
				    }
				}
				
				/* Return */
				return;
			}
			
			/**
			 * Resolve an asset list to remove null entries
			 */
			protected void pruneMarket() {
				Bucket myCurr;
				Bucket myNext;
				
				/* Loop through the Buckets */
				for (myCurr = getFirst();
				     myCurr != null;
				     myCurr = myNext) {
				
					/* Determine the next element */
					myNext = myCurr.getNext();
					
					/* If we have an non-priced bucket */
				    if ((!myCurr.isPriced()) &&
				    	(myCurr.isDetail())) { 
						/* Unlink the entry */
				    	myCurr.unLink();
				    }
				}
				
				/* Return */
				return;
			}
			
			/**
			 * Mark active accounts
			 */
			public void markActiveAccounts() {
				Account                myAccount;
				finStatic.AccountType  myType;
				Bucket                 myCurr;
			
				/* Loop through the detail buckets */
				for (myCurr = getFirst();
					(myCurr != null) && (myCurr.isDetail());
					myCurr = myCurr.getNext()) {
					/* Access the Amount and Account */
					myAccount = myCurr.getAccount();
					myType    = myCurr.getType();
				
					/* If this is a priced asset */
					if (myType.isPriced()){
						/* If the units are non-zero */
						if (myCurr.getUnits().isNonZero()) {
							/* Set the account as non-closeable */
							myAccount.setNonCloseable();
						}
					}
					
					/* else if this is just an asset */
					else if (!myType.isExternal()) {
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
		public class Bucket  implements finLink.linkObject{
			/**
			 * The linking element for this item
			 */
			private linkCtl.linkElement theLink    = null;
			
			/* Members */
			private		String				  theName      = null;
			private 	int             	  theOrder 	   = -1;
			private 	BucketType			  theBucket	   = null;
			private 	finData.Account       theAccount   = null;
			private		finStatic.AccountType theType      = null;
			private		finObject.Units	      theUnits     = null;
			private		finObject.Price	      thePrice     = null;
			private		finObject.Rate	      theRate	   = null;
			private		finObject.Date	      theDate	   = null;
			private		finObject.Money       theAmount    = null;
			private		finObject.Money       theAdjust    = null;
			private		finObject.Money       theMarket    = null;
			private		finObject.Units       theOldUnits  = null;
			private		finObject.Money       theOldAmount = null;
			private		finObject.Money       theOldMarket = null;
			
			/**
			 * Get the next item in the list 
			 * @return the next item or <code>null</code>
			 */
			public Bucket getNext()    { return (Bucket)theLink.getNext(); }

			/**
			 * Get the previous item in the list 
			 * @return the previous item or <code>null</code>
			 */
			public Bucket getPrev()    { return (Bucket)theLink.getPrev();  }

			/**
			 * Add the item to the list 
			 */
			public void        addToList()  { theLink.addToList(); }
			
			/**
			 * Unlink the item from the list
			 */
			public void        unLink()     { theLink.unLink(); }
			
			/* Access methods */
			public 	String				  getName()      { return theName; }
			private int    				  getOrder()     { return theOrder; }
			public 	BucketType			  getBucket()    { return theBucket; }
			public 	finData.Account       getAccount()   { return theAccount; }
			public 	finStatic.AccountType getType()      { return theType; }
			public 	finObject.Units       getUnits()     { return theUnits; }
			public 	finObject.Price       getPrice()     { return thePrice; }
			public 	finObject.Rate        getRate()      { return theRate; }
			public 	finObject.Date        getDate()      { return theDate; }
			public 	finObject.Money       getAmount()    { return theAmount; }
			public 	finObject.Money       getAdjust()    { return theAdjust; }
			public 	finObject.Money       getMarket()    { return theMarket; }
			public 	finObject.Units       getOldUnits()  { return theOldUnits; }
			public 	finObject.Money       getOldAmount() { return theOldAmount; }
			public 	finObject.Money       getOldMarket() { return theOldMarket; }
			private boolean     		  isDetail()   { 
				return (theBucket == BucketType.DETAIL); }
			public 	boolean               isPriced()   { 
				return ((theAccount != null) && (theAccount.isPriced())); }

			/* Constructors */
			private Bucket(finLink.linkCtl pList, finData.Account pAccount) { 
				theLink      = pList.new linkElement(this);
				theName      = pAccount.getName();
				theType      = pAccount.getActType();
				theBucket    = BucketType.DETAIL;
				theOrder     = theType.getOrder();
				theAccount   = pAccount;
				theAmount    = new finObject.Money(0);
				theOldAmount = new finObject.Money(0);
				if (pAccount.isPriced()) {
					theUnits     = new finObject.Units(0);
					theOldUnits  = new finObject.Units(0);
					theOldMarket = new finObject.Money(0);
				}
			}
			private Bucket(finLink.linkCtl pList, finStatic.AccountType pType) { 
				theLink      = pList.new linkElement(this);
				theName      = pType.getName();
				theType      = pType;
				theBucket    = BucketType.SUMMARY;
				theOrder     = pType.getOrder();
				theAmount    = new finObject.Money(0);
				theOldAmount = new finObject.Money(0);
			}
			private Bucket(finLink.linkCtl pList, BucketType pType) { 
				theLink      = pList.new linkElement(this);
				theBucket    = pType;
				theOrder     = 0;
				theAmount    = new finObject.Money(0);
				theOldAmount = new finObject.Money(0);
			}
			private Bucket(finLink.linkCtl pList, Bucket pBucket) {
				theLink      = pList.new linkElement(this);
				theName      = pBucket.getName();
				theOrder     = pBucket.getOrder();
				theBucket    = pBucket.getBucket();
				theAccount   = pBucket.getAccount();
				theType      = pBucket.getType();
				theOldAmount = new finObject.Money(pBucket.getAmount());
				if (isPriced()) {
					theOldUnits  = new finObject.Units(pBucket.getUnits());
					theUnits     = new finObject.Units(pBucket.getUnits());
					theAmount    = new finObject.Money(0);
					theOldMarket = new finObject.Money(pBucket.getMarket());
				}
				else if (getBucket() == BucketType.DETAIL)
					theAmount   = new finObject.Money(pBucket.getAmount());
				else
					theAmount   = new finObject.Money(0);
			}
			
			/**
			 * Override for standard method
			 */
			protected int compareTo(Bucket that) {
				int result;
				if (this == that) return 0;
				if (that == null) return -1;
				if (getBuckOrder(theBucket) < getBuckOrder(that.theBucket)) return -1;
				if (getBuckOrder(theBucket) > getBuckOrder(that.theBucket)) return  1;
				if (theOrder < that.theOrder) return -1;
				if (theOrder > that.theOrder) return  1;
				result = theName.compareTo(that.theName);
				if (result == 0) return 0;
				else if (result < 0) return -1;
				else return 1;
			}

			public int linkCompareTo(linkObject that) {
				Bucket myItem = (Bucket)that;
				return this.compareTo(myItem);
			}
			
			/**
			 * Add the event to the bucket
			 * 
			 * @param  pEvent Event to add
			 */
			protected void addEvent(finData.Event pEvent) {
				finObject.Money myAmount = pEvent.getAmount();
				finObject.Units myUnits  = pEvent.getUnits();

				/* Adjust the money total */
				theAmount.addAmount(myAmount);
				
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
			protected void subtractEvent(finData.Event pEvent) {
				finObject.Money myAmount = pEvent.getAmount();
				finObject.Units myUnits  = pEvent.getUnits();

				/* Adjust the money total */
				theAmount.subtractAmount(myAmount);
				
				/* If we need to adjust units */
				if ((myUnits != null) && (theUnits != null)) {
					/* Adjust the units total */
					theUnits.subtractUnits(myUnits);
				}
			}

			/**
			 * Add a bucket to a totalling bucket
			 * 
			 * @param  pBucket Bucket to add
			 */
			protected void addBucket(Bucket pBucket) {
				finObject.Money myAmount = pBucket.getAmount();

				/* Adjust the money total */
				theAmount.addAmount(myAmount);
				theOldAmount.addAmount(pBucket.getOldAmount());
			}
			
			/**
			 * Add a bucket to a totalling market bucket
			 * 
			 * @param  pBucket Bucket to add
			 */
			protected void addMarketBucket(Bucket pBucket) {
				finObject.Money myAmount = pBucket.getAmount();

				/* Adjust the money total */
				theAmount.addAmount(myAmount);
				if (theAdjust == null) theAdjust = new finObject.Money(0);
				theAdjust.addAmount(pBucket.getAdjust());
				if (theMarket == null) theMarket = new finObject.Money(0);
				theMarket.addAmount(pBucket.getMarket());
			}
			
			/**
		 	* Set an explicit valuation
		 	* 
		 	* @param  pAmount amount for bucket
		 	*/
			protected void setAmount(finObject.Money pAmount) {
				theAmount = new finObject.Money(pAmount);
			}

			/**
			 * Calculate market movement for an asset 
			 */
			public void calculateMarketMovement() {
				/** Calculate the market movement 
				 * This is defined as is NewValue - OldValue - Adjust
				 */
				finObject.Money myMovement = new finObject.Money(theAmount);
				myMovement.subtractAmount(theOldAmount);
				myMovement.subtractAmount(theAdjust);
			
				/* Store it and return */
				theMarket = myMovement;
				return;
			}
		}
	}
	
	/* Income Report class */
	public class Income {
		/* Members */
		private finObject.Date      theDate     = null;
		private finData.AccountList theAccounts = null;
		private List                theBuckets  = null;
		private boolean             hasTotals   = false;
		
		/* Access methods */
		public List            getBuckets()  { return theBuckets; }
		public finObject.Date  getDate()     { return theDate; }

		/* Constructor */
		public Income(finObject.Date sDate) {
			theDate     = sDate;
			theAccounts = theData.getAccounts();
			theBuckets  = new List();
		}

		/**
		 * Seed from previous end-of-year report
		 *
		 * @param  pReport The Previous report
		 */
		public void seedReport(Income pReport) {
			Bucket myCurr;
			Bucket myBucket;
			
			/* If we have a report to copy */
			if (pReport != null) {
				
				/* Loop through the Buckets */
				for (myCurr = pReport.getBuckets().getFirst();
					 myCurr != null;
					 myCurr = myCurr.getNext()) {

					/* If we have a non-empty bucket */
					if ((myCurr.getIncome().isNonZero()) ||
						(myCurr.getExpense().isNonZero())) {
						/* Copy and add the bucket */
						myBucket  = new Bucket(theBuckets.theList, myCurr);
						myBucket.addToList();
					}
			    }
			}			
		}
		
		/**
		 * Process an event
		 * 
		 * @param pEvent the event to process
		 */
		public void processEvent(finData.Event pEvent) {
			finData.Account      myAccount;
			finStatic.TransType  myTransType;
			Bucket          	 myBucket;
			
			/* If the credit account is external */
			myAccount = pEvent.getCredit();
			if (myAccount.isExternal()) {
				/* Locate its bucket */
				myBucket = theBuckets.getAccountBucket(myAccount);
				
				/* Add the Expense Event to the bucket */
				myBucket.addExpenseEvent(pEvent);
			}
			
			/* If the debit account is external */
			myAccount   = pEvent.getDebit();
			myTransType = pEvent.getTransType();
			if (myAccount.isExternal()) {
				/* Locate its bucket */
				myBucket = theBuckets.getAccountBucket(myAccount);
				
				/* If the event is recovered */
				if (myTransType.isRecovered()) {
					/* Subtract from expense */
					myBucket.subtractExpenseEvent(pEvent);					
				}
				else	
					/* Add the Income Event to the bucket */
					myBucket.addIncomeEvent(pEvent);	
			}
		}
		
		/**
		 * Add market movement
		 * 
		 * @param pMovement the Market movement
		 */
		public void addMarketMovement(Asset.Bucket pMovement) {
			Bucket     		myBucket;
			finObject.Money myMovement;
			
			/* Access the Market Bucket */
			myBucket = theBuckets.getAccountBucket(theAccounts.getMarket());
			
			/* Access the current market movement */
			myMovement = pMovement.getMarket();
			
			/* If the movement is positive */
			if (myMovement.isPositive())
			{
				/* Add the Market Growth */
			    myBucket.addIncomeAmount(myMovement);
			}
			
			/* else this is a market shrink */
			else {
				/* Negate the value */
				myMovement = new finObject.Money(myMovement);
				myMovement.negate();
				
				/* Add the market shrink */
			    myBucket.addExpenseAmount(myMovement);
			}			
		}
		
		/**
		 * Produce the totals
		 */
		public void produceTotals() {
			Bucket          myCurr;
			Bucket          myTotals;
			finObject.Money myMoney;

			/* If we have not produced totals before */
			if (!hasTotals) {
				/* Access the totals bucket */
				myTotals = theBuckets.getTotalsBucket();
			
				/* Loop through the detail buckets */
				for (myCurr = theBuckets.getFirst();
					 (myCurr != null) && (myCurr.isDetail());
					 myCurr = myCurr.getNext()) {
					/* If the expense is negative */
					myMoney = myCurr.getExpense();
					if (!myMoney.isPositive()) {
						/* Swap it to the income side */
						myCurr.getIncome().subtractAmount(myMoney);
						myMoney.setZero();
					}
					
					/* If the old expense is negative */
					myMoney = myCurr.getOldExpense();
					if (!myMoney.isPositive()) {
						/* Swap it to the income side */
						myCurr.getOldIncome().subtractAmount(myMoney);
						myMoney.setZero();
					}
					
					/* Add the amount to totals */
					myTotals.addBucket(myCurr);
				}
				
				/* Prune the list */
				theBuckets.prune();
				
				/* Set totals flag */
				hasTotals = true;
			}
		}
		
		public class List {
			/**
			 * The underlying list class
			 */
			private finLink.linkCtl   theList     = null;

			/**
			 * Get the first element of the list
			 * @return the first element (or <code>null</code>)
			 */
			protected Bucket getFirst()    { return (Bucket)theList.getFirst(); }

			/**
			 * Get the last element of the list
			 * @return the last element (or <code>null</code>)
			 */
			protected Bucket getLast()     { return (Bucket)theList.getLast(); }
			
			/**
			 * Construct a top-level List
			 */
			public List() { theList  = new finLink.linkCtl(true); }
	
			/**
			 * Search for a particular bucket
			 * 
			 * @param pName  Name of Item
			 * @param pOrder Order of Item
			 * @param pType  Type of bucket
			 * @return The Item if present (or null)
			 */
			protected Bucket searchFor(String pName, int pOrder, BucketType pType) {
				Bucket     myCurr;
				BucketType myType;
				String     myName;
				int        iDiff;
			
				/* Loop through the items to find the match */
				for (myCurr = getFirst();
			         myCurr != null;
			         myCurr = myCurr.getNext()) {
					myType = myCurr.getBucket();
					if (getBuckOrder(myType) < getBuckOrder(pType)) continue;
					if (getBuckOrder(myType) > getBuckOrder(pType)) 
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
				Bucket     myCurr;
				BucketType myType;
			
				/* Loop through the items to find the match */
				for (myCurr = getFirst();
			         myCurr != null;
			         myCurr = myCurr.getNext()) {
					myType = myCurr.getBucket();
					if (getBuckOrder(myType) < getBuckOrder(pType)) continue;
					if (getBuckOrder(myType) > getBuckOrder(pType)) 
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
			 * @return The Income Bucket
			 */
			protected Bucket getAccountBucket(finData.Account pAccount) {
				Bucket   myBucket;
				
				/* Find an existing income bucket */
				myBucket = searchFor(pAccount.getName(), pAccount.getOrder(), BucketType.DETAIL);
						
				/* If we did not find a bucket */
				if (myBucket == null) {
					/* Allocate a new bucket and add to the list */
					myBucket = new Bucket(theList, pAccount);
					myBucket.addToList();
				}
						
				/* Return the bucket */
				return myBucket;
			}
			
			/**
			 * Obtain or allocate a bucket for a totals
			 * 
			 * @return The Income Bucket
			 */
			protected Bucket getTotalsBucket() {
				Bucket   myBucket;
				
				/* Find an existing asset bucket */
				myBucket = searchFor(BucketType.TOTAL);
						
				/* If we did not find a bucket */
				if (myBucket == null) {
					/* Allocate a new bucket and add to the list */
					myBucket = new Bucket(theList, BucketType.TOTAL);
					myBucket.addToList();
				}
						
				/* Return the bucket */
				return myBucket;
			}
			
			/**
			 * Resolve an income list to remove null entries
			 */
			protected void prune() {
				Bucket myCurr;
				Bucket myNext;
				
				/* Loop through the Buckets */
				for (myCurr = getFirst();
				     myCurr != null;
				     myCurr = myNext) {
				
					/* Determine the next element */
					myNext = myCurr.getNext();
					
					/* If we have an empty bucket */
				    if ((!myCurr.getIncome().isNonZero())     &&
				    	(!myCurr.getExpense().isNonZero())    &&
					    (!myCurr.getOldIncome().isNonZero())  &&
						(!myCurr.getOldExpense().isNonZero())) {
				   
						/* Unlink the entry */
				    	myCurr.unLink();
				    }
				}
				
				/* Return */
				return;
			}			
		}
			
		/* The item class */
		public class Bucket implements finLink.linkObject {
			/**
			 * The linking element for this item
			 */
			private linkCtl.linkElement theLink    = null;
			
			/* Members */
			private		String			theName        = null;
			private 	int             theOrder 	   = -1;
			private 	BucketType		theBucket	   = null;
			private 	finData.Account theAccount     = null;
			private		finObject.Money theIncome      = null;
			private		finObject.Money theExpense     = null;
			private		finObject.Money theOldIncome   = null;
			private		finObject.Money theOldExpense  = null;
			
			/**
			 * Get the next item in the list 
			 * @return the next item or <code>null</code>
			 */
			public Bucket getNext()    { return (Bucket)theLink.getNext(); }

			/**
			 * Get the previous item in the list 
			 * @return the previous item or <code>null</code>
			 */
			public Bucket getPrev()    { return (Bucket)theLink.getPrev();  }

			/**
			 * Add the item to the list 
			 */
			public void        addToList()  { theLink.addToList(); }
			
			/**
			 * Unlink the item from the list
			 */
			public void        unLink()     { theLink.unLink(); }
			
			/* Access methods */
			public 	String				 getName()       { return theName; }
			private int    				 getOrder()      { return theOrder; }
			public 	BucketType			 getBucket()     { return theBucket; }
			public  finData.Account      getAccount()    { return theAccount; }
			public  finObject.Money      getIncome()     { return theIncome; }
			public  finObject.Money      getExpense()    { return theExpense; }
			public  finObject.Money      getOldIncome()  { return theOldIncome; }
			public  finObject.Money      getOldExpense() { return theOldExpense; }
			private boolean     		 isDetail()   { 
				return (theBucket == BucketType.DETAIL); }

			/* Constructors */
			private Bucket(finLink.linkCtl pList, finData.Account pAccount) { 
				theLink      = pList.new linkElement(this);
				theName       = pAccount.getName();
				theBucket     = BucketType.DETAIL;
				theOrder      = pAccount.getActType().getOrder();
				theAccount    = pAccount;
				theIncome     = new finObject.Money(0);
				theExpense    = new finObject.Money(0);
				theOldIncome  = new finObject.Money(0);
				theOldExpense = new finObject.Money(0);
			}
			private Bucket(finLink.linkCtl pList, BucketType pType) { 
				theLink      = pList.new linkElement(this);
				theName       = null;
				theBucket     = pType;
				theOrder      = 0;
				theIncome     = new finObject.Money(0);
				theExpense    = new finObject.Money(0);
				theOldIncome  = new finObject.Money(0);
				theOldExpense = new finObject.Money(0);
			}
			private Bucket(finLink.linkCtl pList, Bucket pBucket) { 
				theLink      = pList.new linkElement(this);
				theName       = pBucket.getName();
				theBucket     = pBucket.getBucket();
				theOrder      = pBucket.getOrder();
				theAccount    = pBucket.getAccount();
				theIncome     = new finObject.Money(0);
				theExpense    = new finObject.Money(0);
				theOldIncome  = new finObject.Money(pBucket.getIncome());
				theOldExpense = new finObject.Money(pBucket.getExpense());
			}
			
			/**
			 * Override for standard method
			 */
			protected int compareTo(Bucket that) {
				int result;
				if (this == that) return 0;
				if (that == null) return -1;
				if (getBuckOrder(theBucket) < getBuckOrder(that.theBucket)) return -1;
				if (getBuckOrder(theBucket) > getBuckOrder(that.theBucket)) return  1;
				if (theOrder < that.theOrder) return -1;
				if (theOrder > that.theOrder) return  1;
				result = theName.compareTo(that.theName);
				if (result == 0) return 0;
				else if (result < 0) return -1;
				else return 1;
			}

			public int linkCompareTo(linkObject that) {
				Bucket myItem = (Bucket)that;
				return this.compareTo(myItem);
			}
			
			/**
			 * Add the income event to the bucket
			 * 
			 * @param  pEvent Event to add
			 */
			protected void addIncomeEvent(finData.Event pEvent) {
				finObject.Money   myAmount = pEvent.getAmount();

				/* Adjust the income total */
				theIncome.addAmount(myAmount);
			}
			
			/**
			 * Add the expense event to the bucket
			 * 
			 * @param  pEvent Event to add
			 */
			protected void addExpenseEvent(finData.Event pEvent) {
				finObject.Money   myAmount = pEvent.getAmount();

				/* Adjust the expense total */
				theExpense.addAmount(myAmount);
			}
			
			/**
			 * Subtract the expense event from the bucket
			 * 
			 * @param  pEvent Event to subtract
			 */
			protected void subtractExpenseEvent(finData.Event pEvent) {
				finObject.Money   myAmount = pEvent.getAmount();

				/* Adjust the money total */
				theExpense.subtractAmount(myAmount);		
			}
			
			/**
			 * Add a bucket to a totalling bucket
			 * 
			 * @param  pBucket Bucket to add
			 */
			protected void addBucket(Bucket pBucket) {
				finObject.Money myIncome  = pBucket.getIncome();
				finObject.Money myExpense = pBucket.getExpense();

				/* Adjust the income/expense totals */
				theIncome.addAmount(myIncome);
				theExpense.addAmount(myExpense);
				theOldIncome.addAmount(pBucket.getOldIncome());
				theOldExpense.addAmount(pBucket.getOldExpense());
			}
			
			/**
			 * Add an amount to an income bucket
			 * 
			 * @param  pAmount Amount to add
			 */
			protected void addIncomeAmount(finObject.Money pAmount) {
				theIncome.addAmount(pAmount);
			}
			
			/**
			 * Add an amount to an expense bucket
			 * 
			 * @param  pAmount Amount to add
			 */
			protected void addExpenseAmount(finObject.Money pAmount) {
				theExpense.addAmount(pAmount);
			}
		}
	}
	
	/* Tax Report */
	public class Tax {
		/* Members */
		private finData.TaxParms		theYear			= null;
		private finObject.Date          theDate         = null;
		private TranList                theTransBuckets = null;
		private TaxList                 theTaxBuckets   = null;
		private finStatic.TransTypeList theTransTypes   = null;
		private finStatic.TaxTypeList   theTaxTypes     = null;
		private chargeableEvent			theCharges		= null;
		private boolean                 hasTotals       = false;
		private boolean					hasAgeAllowance = false;
		private boolean					hasGainsSlices	= false;
		private boolean					hasReducedAllow	= false;
		private int						theAge			= 0;

		/* Access methods */
		public finData.TaxParms    		getYear()         { return theYear; }
		public finObject.Date      		getDate()         { return theDate; }
		public finStatic.TaxTypeList 	getTaxTypes() 	  { return theTaxTypes; }
		public TranList            		getTransBuckets() { return theTransBuckets; }
		public TaxList             		getTaxBuckets()   { return theTaxBuckets; }
		public chargeableEvent     		getCharges()   	  { return theCharges; }
		public boolean     		   		hasReducedAllow() { return hasReducedAllow; }
		public boolean     		   		hasGainsSlices()  { return hasGainsSlices; }
		public int				   		getAge()		  { return theAge; }
		
		/* Constructor */
		public Tax(finData.TaxParms pYear) {
			theYear         = pYear;
			theDate         = pYear.getDate();
			theTransTypes   = theData.getTransTypes();
			theTaxTypes     = theData.getTaxTypes();
			theTransBuckets = new TranList();
			theTaxBuckets   = new TaxList();
		}
		
		/**
		 * Seed from previous end-of-year report
		 *
		 * @param  pReport The Previous report
		 */
		public void seedReport(Tax pReport) {
			TranBucket myCurr;
			TranBucket myBucket;
			
			/* If we have a report to copy */
			if (pReport != null) {				
				/* Loop through the TransType Buckets */
				for (myCurr = pReport.getTransBuckets().getFirst();
					 myCurr != null;
					 myCurr = myCurr.getNext()) {

					/* If we have a non-empty bucket */
					if (myCurr.getAmount().isNonZero()) {
						/* Copy and add the bucket */
						myBucket  = new TranBucket(theTransBuckets.theList, myCurr);
						myBucket.addToList();
					}
			    }
			}			
		}
		
		/**
		 * Produce totals for the tax year 
		 * @param pProperties the properties
		 */
		public void produceTotals(finProperties pProperties) {
			/* If we have not previously totalled */
			if (!hasTotals) {
				/* Calculate the totals */
				analyseTransactions();
				calculateTax(pProperties);
				
				/* Prune the lists */
				theTransBuckets.prune();
				theTaxBuckets.prune();
				
				/* Set flag */
				hasTotals = true;
			}
		}
		
		/**
		 * Process an event
		 * 
		 * @param pEvent the event to process
		 */
		public void processEvent(finData.Event pEvent) {
			finStatic.TransType myTransType;
			TranBucket         	myBucket;
			chargeableEvent		myCharge;
			
			/* Access the transaction type */
			myTransType = pEvent.getTransType();

			/* If the transaction is not a transfer */
			if ((!myTransType.isTransfer()) && 
				(!myTransType.isCashPayment()) &&
				(!myTransType.isCashRecovery())) {
				/* Locate its bucket */
				myBucket = theTransBuckets.getTransBucket(myTransType);
				
				/* Add the Event to the bucket */
				myBucket.addEvent(pEvent);
				
				/* If this is a taxable gain event */
				if (myTransType.isTaxableGain()) {
					/* Create and store the new taxable event */
					myCharge = new chargeableEvent(pEvent, theCharges);
					if (theCharges == null) theCharges = myCharge;
				}
			}
		}
		
		/**
		 * Add market movement
		 * 
		 * @param pBucket the market movement
		 */
		public void addMarketMovement(Asset.Bucket pBucket) {
			TranBucket     	myBucket;
			finObject.Money myMovement;
			
			/* Access the current movement */
			myMovement = pBucket.getMarket();
			
			/* If the movement is positive */
			if (myMovement.isPositive())
			{
				/* Access the Market Growth bucket */
				myBucket = theTransBuckets.getTransBucket(theTransTypes
			                 	.searchFor(TransClass.MKTGROWTH));
			    myBucket.addAmount(myMovement);
			}
			
			/* else this is a market shrink */
			else {
				/* Negate the value */
				myMovement = new finObject.Money(myMovement);
				myMovement.negate();
				
				/* Access the Market Growth bucket */
				myBucket = theTransBuckets.getTransBucket(theTransTypes
			                 	.searchFor(TransClass.MKTSHRINK));
			    myBucket.addAmount(myMovement);
			}
		}
		
		/**
		 * Analyse transactions for tax
		 */
		public void analyseTransactions() {
			TranBucket myBucket;
			
			/* Build the Salary bucket */
			myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					 .searchFor(TaxClass.GROSSSALARY));
			myBucket.addBucket(theTransBuckets
					 .getTransBucket(theTransTypes
					   		 .searchFor(TransClass.TAXEDINCOME)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.NATINSURANCE)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.BENEFIT)));
			
			/* Build the Interest bucket */
			myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					 .searchFor(TaxClass.GROSSINTEREST));
			myBucket.addBucket(theTransBuckets
					 .getTransBucket(theTransTypes
					   		 .searchFor(TransClass.INTEREST)));
			
			/* Build the Dividend bucket */
			myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					 .searchFor(TaxClass.GROSSDIVIDEND));
			myBucket.addBucket(theTransBuckets
					 .getTransBucket(theTransTypes
					   		 .searchFor(TransClass.DIVIDEND)));
			
			/* Build the Unit Trust Dividend bucket */
			myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					 .searchFor(TaxClass.GROSSUTDIVS));
			myBucket.addBucket(theTransBuckets
					 .getTransBucket(theTransTypes
					   		 .searchFor(TransClass.UNITTRUSTDIV)));
			
			/* Build the Taxable Gains bucket */
			myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					 .searchFor(TaxClass.GROSSTAXGAINS));
			myBucket.addBucket(theTransBuckets
					 .getTransBucket(theTransTypes
					   		 .searchFor(TransClass.TAXABLEGAIN)));
			
			/* Build the Capital Gains bucket */
			myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					 .searchFor(TaxClass.GROSSCAPGAINS));
			myBucket.addBucket(theTransBuckets
					 .getTransBucket(theTransTypes
					   		 .searchFor(TransClass.CAPITALGAIN)));
			
			/* Build the Rental bucket */
			myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					 .searchFor(TaxClass.GROSSRENTAL));
			myBucket.addBucket(theTransBuckets
					 .getTransBucket(theTransTypes
					   		 .searchFor(TransClass.RENTALINCOME)));
			
			/* Build the TaxPaid bucket */
			myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					 .searchFor(TaxClass.TAXPAID));
			myBucket.addBucket(theTransBuckets
					 .getTransBucket(theTransTypes
					   		 .searchFor(TransClass.TAXEDINCOME)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.INTEREST)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.DIVIDEND)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.UNITTRUSTDIV)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.TAXABLEGAIN)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.TAXOWED)));
			myBucket.subtractBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.TAXREFUND)));
					
			/* Build the TaxFree bucket */
			myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					 .searchFor(TaxClass.TAXFREE));
			myBucket.addBucket(theTransBuckets
					 .getTransBucket(theTransTypes
					   		 .searchFor(TransClass.TAXFREEINCOME)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.INHERITED)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.DIRLOAN)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.DEBTINTEREST)));
			
			/* Build the Market bucket */
			myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					 .searchFor(TaxClass.MARKET));
			myBucket.addBucket(theTransBuckets
					 .getTransBucket(theTransTypes
					   		 .searchFor(TransClass.MKTINCOME)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.MKTGROWTH)));
			myBucket.subtractBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.MKTSHRINK)));
			
			/* Build the Expense bucket */
			myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					 .searchFor(TaxClass.EXPENSE));
			myBucket.addBucket(theTransBuckets
					 .getTransBucket(theTransTypes
					   		 .searchFor(TransClass.EXPENSE)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.MORTGAGE)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.INSURANCE)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.ENDOWMENT)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.EXTRATAX)));
			myBucket.addBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.WRITEOFF)));
			myBucket.subtractBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.RECOVERED)));
			myBucket.subtractBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.TAXRELIEF)));

			/* Build the Profit bucket */
			myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					 .searchFor(TaxClass.PROFIT));
			myBucket.addBucket(theTransBuckets
					 .getSummaryBucket(theTaxTypes
					   		 .searchFor(TaxClass.GROSSSALARY)));
			myBucket.addBucket(theTransBuckets
					 .getSummaryBucket(theTaxTypes
					   		 .searchFor(TaxClass.GROSSRENTAL)));
			myBucket.addBucket(theTransBuckets
					 .getSummaryBucket(theTaxTypes
					   		 .searchFor(TaxClass.GROSSINTEREST)));
			myBucket.addBucket(theTransBuckets
					 .getSummaryBucket(theTaxTypes
					   		 .searchFor(TaxClass.GROSSDIVIDEND)));
			myBucket.addBucket(theTransBuckets
					 .getSummaryBucket(theTaxTypes
					   		 .searchFor(TaxClass.GROSSUTDIVS)));
			myBucket.addBucket(theTransBuckets
					 .getSummaryBucket(theTaxTypes
					   		 .searchFor(TaxClass.GROSSTAXGAINS)));
			myBucket.addBucket(theTransBuckets
					 .getSummaryBucket(theTaxTypes
					   		 .searchFor(TaxClass.GROSSCAPGAINS)));
			myBucket.addBucket(theTransBuckets
					 .getSummaryBucket(theTaxTypes
					   		 .searchFor(TaxClass.MARKET)));
			myBucket.addBucket(theTransBuckets
					 .getSummaryBucket(theTaxTypes
					   		 .searchFor(TaxClass.TAXFREE)));
			myBucket.subtractBucket(theTransBuckets
		             .getSummaryBucket(theTaxTypes
		            		 .searchFor(TaxClass.TAXPAID)));
			myBucket.subtractBucket(theTransBuckets
		             .getSummaryBucket(theTaxTypes
		            		 .searchFor(TaxClass.EXPENSE)));		
			myBucket.subtractBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.BENEFIT)));
			myBucket.subtractBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.NATINSURANCE)));
			
			/* Build the CoreProfit bucket */
			myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					 .searchFor(TaxClass.COREPROFIT));
			myBucket.addBucket(theTransBuckets
					 .getSummaryBucket(theTaxTypes
					   		 .searchFor(TaxClass.PROFIT)));
			myBucket.subtractBucket(theTransBuckets
		             .getSummaryBucket(theTaxTypes
		            		 .searchFor(TaxClass.MARKET)));			
			myBucket.subtractBucket(theTransBuckets
		             .getTransBucket(theTransTypes
		            		 .searchFor(TransClass.INHERITED)));			
		}
		
		/**
		 * Calculate tax
		 * @param pProperties the properties
		 */
		public void calculateTax(finProperties pProperties) {
			taxBands		myBands;
			finObject.Money myIncome	= new finObject.Money(0);
			finObject.Money myTax		= new finObject.Money(0);
			TaxBucket		myBucket;
			TranBucket		mySrcBucket;
			
			/* Calculate the gross income */
			calculateGrossIncome();
			
			/* Calculate the allowances and tax bands */
			myBands = calculateAllowances(pProperties);
			
			/* Calculate the salary taxation */
			myBucket = calculateSalaryTax(myBands);
			myIncome.addAmount(myBucket.getAmount());
			myTax.addAmount(myBucket.getTaxation());
			
			/* Calculate the rental taxation */
			myBucket = calculateRentalTax(myBands);
			myIncome.addAmount(myBucket.getAmount());
			myTax.addAmount(myBucket.getTaxation());
			
			/* Calculate the interest taxation */
			myBucket = calculateInterestTax(myBands);
			myIncome.addAmount(myBucket.getAmount());
			myTax.addAmount(myBucket.getTaxation());
			
			/* Calculate the dividends taxation */
			myBucket = calculateDividendsTax(myBands);
			myIncome.addAmount(myBucket.getAmount());
			myTax.addAmount(myBucket.getTaxation());
			
			/* Calculate the taxable gains taxation */
			myBucket = calculateTaxableGainsTax(myBands);
			myIncome.addAmount(myBucket.getAmount());
			myTax.addAmount(myBucket.getTaxation());
			
			/* Calculate the capital gains taxation */
			myBucket = calculateCapitalGainsTax(myBands);
			myIncome.addAmount(myBucket.getAmount());
			myTax.addAmount(myBucket.getTaxation());
						
			/* Build the TotalTaxBucket */
			myBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     .searchFor(TaxClass.TOTALTAX));
			myBucket.setAmount(myIncome);
			myBucket.setTaxation(myTax);
			
			/* Access the tax paid bucket */
			mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
		            		 					.searchFor(TaxClass.TAXPAID));
			
			/* Calculate the tax profit */
			myTax.subtractAmount(mySrcBucket.getAmount());

			/* Build the TaxProfitBucket */
			myBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     .searchFor(TaxClass.TAXPROFIT));
			myBucket.setAmount(new finObject.Money(0));
			myBucket.setTaxation(myTax);
		}

		/**
		 * Calculate the gross income for tax purposes
		 */
		public void calculateGrossIncome() {
			TaxBucket     	myBucket;
			TranBucket     	mySrcBucket;
			finObject.Money myIncome = new finObject.Money(0);
			finObject.Money myChargeable;
			
			/* Access the salary bucket and add to income */
			mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					   		 				.searchFor(TaxClass.GROSSSALARY));
			myIncome.addAmount(mySrcBucket.getAmount());
			
			/* Access the rental bucket */
			mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					   		 				.searchFor(TaxClass.GROSSRENTAL));
			myChargeable = new finObject.Money(mySrcBucket.getAmount());
			
			/* If we have a chargeable element */
			if (myChargeable.compareTo(theYear.getRentalAllowance()) > 0) {
				/* Add the chargeable element to income */
				myChargeable.subtractAmount(theYear.getRentalAllowance());
				myIncome.addAmount(myChargeable);
			}
			
			/* Access the interest bucket and add to income */
			mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					   		 				.searchFor(TaxClass.GROSSINTEREST));
			myIncome.addAmount(mySrcBucket.getAmount());
			
			/* Access the dividends bucket and add to income */
			mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					   		 				.searchFor(TaxClass.GROSSDIVIDEND));
			myIncome.addAmount(mySrcBucket.getAmount());
			
			/* Access the unit trust dividends bucket and add to income */
			mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					   		 				.searchFor(TaxClass.GROSSUTDIVS));
			myIncome.addAmount(mySrcBucket.getAmount());
			
			/* Access the taxable gains bucket and add to income */
			mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					   		 				.searchFor(TaxClass.GROSSTAXGAINS));
			myIncome.addAmount(mySrcBucket.getAmount());
			
			/* Access the taxable gains bucket and subtract the tax credit */
			mySrcBucket = theTransBuckets.getTransBucket(theTransTypes
					   		 				.searchFor(TransClass.TAXABLEGAIN));
			myIncome.subtractAmount(mySrcBucket.getTaxCredit());
			
			/* Access the capital gains bucket */
			mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					   		 				.searchFor(TaxClass.GROSSCAPGAINS));
			myChargeable = new finObject.Money(mySrcBucket.getAmount());
			
			/* If we have a chargeable element */
			if (myChargeable.compareTo(theYear.getCapitalAllow()) > 0) {
				/* Add the chargeable element to income */
				myChargeable.subtractAmount(theYear.getCapitalAllow());
				myIncome.addAmount(myChargeable);
			}
			
			/* Access the Gross Income bucket and set the amount */
			myBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					 					.searchFor(TaxClass.GROSSINCOME));
			myBucket.setAmount(myIncome);			
		}
		
		/**
		 * Calculate the allowances and tax bands
		 * @param pProperties the properties
		 */
		public taxBands calculateAllowances(finProperties pProperties) {
			taxBands     	myBands;
			TaxBucket		myBucket;
			TaxBucket		myParentBucket;
			finObject.Money myGrossIncome;
			finObject.Money myAdjust;
			finObject.Money myAllowance;
			long			myValue;
			
			/* Allocate the tax bands class */
			myBands = new taxBands();
			
			/* Determine the relevant age for this tax year */
			theAge = pProperties.getBirthDate().ageOn(theYear.getDate());
			
			/* Determine the relevant allowance */
			if (theAge >= 75) {
				myAllowance 	= theYear.getHiAgeAllow();
				hasAgeAllowance = true;
			}
			else if (theAge >= 65) {
				myAllowance 	= theYear.getLoAgeAllow();
				hasAgeAllowance = true;
			}
			else
				myAllowance 		= theYear.getAllowance();
				
			/* Set Allowance and Tax Bands */
			myBands.theAllowance = new finObject.Money(myAllowance);
			myBands.theLoBand    = new finObject.Money(theYear.getLoBand());
			myBands.theBasicBand = new finObject.Money(theYear.getBasicBand());
			
			/* Record the Original allowance */
			myParentBucket = theTaxBuckets.getTaxBucket(theTaxTypes
 									.searchFor(TaxClass.ORIGALLOW));
			myParentBucket.setAmount(myBands.theAllowance); 
				
			/* Access the gross income */
			myBucket = theTaxBuckets.getTaxBucket(theTaxTypes
 					.searchFor(TaxClass.GROSSINCOME));
			myGrossIncome = myBucket.getAmount(); 
			myBucket.setParent(myParentBucket);
				
			/* If we are using age allowance and the gross income is above the Age Allowance Limit */
			if ((hasAgeAllowance) &&
				(myGrossIncome.compareTo(theYear.getAgeAllowLimit()) > 0)) {
				/* Calculate the limit at which age allowance will disappear */
				myValue  = myBands.theAllowance.getValue();
				myValue *= 2; /* £1 reduction for every £2 increase */
				myAdjust = new finObject.Money(myValue);
				myAdjust.addAmount(theYear.getAgeAllowLimit());
				
				/* If the gross income is above this limit */
				if (myGrossIncome.compareTo(myAdjust) > 0) {
					/* Personal allowance is reduced to standard allowance */
					myBands.theAllowance = new finObject.Money(theYear.getAllowance());
					hasAgeAllowance = false;
				}
				
				/* else we need to reduce the personal allowance */
				else {
					/* Calculate the margin */
					myAdjust = new finObject.Money(myGrossIncome);
					myAdjust.subtractAmount(theYear.getAgeAllowLimit());
					myValue  = myAdjust.getValue();
					
					/* Divide by £2 and then multiply up to £1 */
					myValue /= 200;
					myValue *= 100;
					myAdjust = new finObject.Money(myValue);
					
					/* Adjust the allowance by this value */
					myBands.theAllowance = new finObject.Money(myBands.theAllowance);
					myBands.theAllowance.subtractAmount(myAdjust);
				}
				
				/* Record the adjusted allowance */
				myBucket = theTaxBuckets.getTaxBucket(theTaxTypes
	 					.searchFor(TaxClass.ADJALLOW));
				myBucket.setAmount(myBands.theAllowance);
				myBucket.setParent(myParentBucket);
				hasReducedAllow 	= true;
			}
			
			/* If we have an additional tax band */
			if (theYear.hasAdditionalTaxBand()) {
				/* Set the High tax band */
				myBands.theHiBand = new finObject.Money(theYear.getAddIncBound());
				
				/* Remove the basic band from this one */
				myBands.theHiBand.subtractAmount(myBands.theBasicBand);
				
				/* Record the High tax band */
				myBucket = theTaxBuckets.getTaxBucket(theTaxTypes
	 					.searchFor(TaxClass.HITAXBAND));
				myBucket.setAmount(myBands.theHiBand);
				myBucket.setParent(myParentBucket);
				
				/* If the gross income is above the Additional Allowance Limit */
				if (myGrossIncome.compareTo(theYear.getAddAllowLimit()) > 0) {
					/* Calculate the limit at which personal allowance will disappear */
					myValue  = myBands.theAllowance.getValue();
					myValue *= 2; /* £1 reduction for every £2 increase */
					myAdjust = new finObject.Money(myValue);
					myAdjust.addAmount(theYear.getAddAllowLimit());
					
					/* If the gross income is above this limit */
					if (myGrossIncome.compareTo(myAdjust) > 0) {
						/* Personal allowance is reduced to zero */
						myBands.theAllowance = new finObject.Money(0);
					}
					
					/* else we need to reduce the personal allowance */
					else {
						/* Calculate the margin */
						myAdjust = new finObject.Money(myGrossIncome);
						myAdjust.subtractAmount(theYear.getAddAllowLimit());
						myValue  = myAdjust.getValue();
						
						/* Divide by £2 and then multiply up to £1 */
						myValue /= 200;
						myValue *= 100;
						myAdjust = new finObject.Money(myValue);
						
						/* Adjust the allowance by this value */
						myBands.theAllowance = new finObject.Money(myBands.theAllowance);
						myBands.theAllowance.subtractAmount(myAdjust);
					}
					
					/* Record the adjusted allowance */
					myBucket = theTaxBuckets.getTaxBucket(theTaxTypes
		 					.searchFor(TaxClass.ADJALLOW));
					myBucket.setAmount(myBands.theAllowance);
					myBucket.setParent(myParentBucket);
					hasReducedAllow 	= true;
				}
			}
			
			/* Return to caller */
			return myBands;
		}
		
		/**
		 * Calculate the tax due on salary
		 * @param pBands the remaining allowances and tax bands
		 * @return the salary taxation bucket
		 */
		public TaxBucket calculateSalaryTax(taxBands pBands) {
			TranBucket     	mySrcBucket;
			TaxBucket     	myTaxBucket;
			TaxBucket     	myTopBucket;
			finObject.Money mySalary;
			finObject.Money myTax		= new finObject.Money(0);
			boolean			isFinished  = false;
			
			/* Access Salary */
			mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					           		.searchFor(TaxClass.GROSSSALARY));
			mySalary    = new finObject.Money(mySrcBucket.getAmount());
		
			/* Store the total into the TaxDueSalary Bucket */
			myTopBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.TAXDUESALARY));
			myTopBucket.setAmount(mySalary);
			
			/* Access the FreeSalaryBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.SALARYFREE));
			myTaxBucket.setParent(myTopBucket);
			
			/* If the salary is greater than the remaining allowance */
			if (mySalary.compareTo(pBands.theAllowance) > 0) {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(pBands.theAllowance));
				
				/* Adjust the salary to remove allowance */
				mySalary.subtractAmount(pBands.theAllowance);
				pBands.theAllowance.setZero();
			}
			
			/* else still have allowance left after salary */
			else {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(mySalary));
				
				/* Adjust the allowance to remove salary and note that we have finished */
				pBands.theAllowance.subtractAmount(mySalary);
				isFinished = true;
			}
				
			/* If we have salary left  */
			if (!isFinished) {
				/* If we have a low salary band */
				if (theYear.hasLoSalaryBand()) {
					/* Access the LowSalaryBucket */
					myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     		.searchFor(TaxClass.SALARYLO));
					myTaxBucket.setRate(theYear.getLoTaxRate());
					myTaxBucket.setParent(myTopBucket);
				
					/* If the salary is greater than the Low Tax Band */
					if (mySalary.compareTo(pBands.theLoBand) > 0) {
						/* Set the tax bucket and add the tax */
						myTax.addAmount(myTaxBucket.setAmount(pBands.theLoBand));
					
						/* Adjust the salary to remove LoBand */
						mySalary.subtractAmount(pBands.theLoBand);
						pBands.theLoBand.setZero();
					}
					
					/* else we still have band left after salary */
					else {
						/* Set the tax bucket and add the tax */
						myTax.addAmount(myTaxBucket.setAmount(mySalary));
					
						/* Adjust the loBand to remove salary and note that we have finished */
						pBands.theLoBand.subtractAmount(mySalary);
						isFinished = true;
					}
				}
				
				/* Else use up the Low Tax band */
				else {
					/* If the salary is greater than the Low Tax Band */
					if (mySalary.compareTo(pBands.theLoBand) > 0) {
						/* We have used up the band */
						pBands.theLoBand.setZero();
					}
					else {
						/* Adjust the band to remove salary */
						pBands.theLoBand.subtractAmount(mySalary);
					}
				}
			}
							
			/* If we have salary left */
			if (!isFinished) {
				/* Access the BasicSalaryBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     	.searchFor(TaxClass.SALARYBASIC));
				myTaxBucket.setRate(theYear.getBasicTaxRate());
				myTaxBucket.setParent(myTopBucket);
					
				/* If the salary is greater than the Basic Tax Band */
				if (mySalary.compareTo(pBands.theBasicBand) > 0) {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));
					
					/* Adjust the salary to remove BasicBand */
					mySalary.subtractAmount(pBands.theBasicBand);
					pBands.theBasicBand.setZero();
				}
					
				/* else we still have band left after salary */
				else {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(mySalary));
					
					/* Adjust the basicBand to remove salary and note that we have finished */
					pBands.theBasicBand.subtractAmount(mySalary);
					isFinished = true;
				}
			}
			
			/* If we have salary left */
			if (!isFinished) {
				/* Access the HiSalaryBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     	.searchFor(TaxClass.SALARYHI));
				myTaxBucket.setRate(theYear.getHiTaxRate());
				myTaxBucket.setParent(myTopBucket);
					
				/* If the salary is greater than the High Tax Band */
				if ((theYear.hasAdditionalTaxBand()) && 
					(mySalary.compareTo(pBands.theHiBand) > 0)) {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));
					
					/* Adjust the salary to remove HiBand */
					mySalary.subtractAmount(pBands.theHiBand);
					pBands.theHiBand.setZero();
				}
					
				/* else we still have band left after salary */
				else {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(mySalary));
					
					/* Adjust the hiBand to remove salary and note that we have finished */
					if (theYear.hasAdditionalTaxBand())
						pBands.theHiBand.subtractAmount(mySalary);
					isFinished = true;
				}
			}
			
			/* If we have salary left */
			if (!isFinished) {
				/* Access the AdditionalSalaryBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     	.searchFor(TaxClass.SALARYADD));
				myTaxBucket.setRate(theYear.getAddTaxRate());
				myTaxBucket.setParent(myTopBucket);
					
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(mySalary));
			}
			
			/* Store the taxation value into the top bucket */
			myTopBucket.setTaxation(myTax);
			
			/* Return the tax bucket */
			return myTopBucket;
		}
		
		/**
		 * Calculate the tax due on rental
		 * @param pBands the remaining allowances and tax bands
		 * @return the rental tax bucket
		 */
		public TaxBucket calculateRentalTax(taxBands pBands) {
			TranBucket     	mySrcBucket;
			TaxBucket     	myTaxBucket;
			TaxBucket     	myTopBucket;
			finObject.Money myRental;
			finObject.Money myAllowance;
			finObject.Money myTax		= new finObject.Money(0);
			boolean			isFinished  = false;
			
			/* Access Rental */
			mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					           		.searchFor(TaxClass.GROSSRENTAL));
			myRental    = new finObject.Money(mySrcBucket.getAmount());
		
			/* Store the total into the TaxDueRental Bucket */
			myTopBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.TAXDUERENTAL));
			myTopBucket.setAmount(myRental);
			
			/* Access the FreeRentalBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.RENTALFREE));
			myTaxBucket.setParent(myTopBucket);
			
			/* Pick up the rental allowance */
			myAllowance = theYear.getRentalAllowance();
			
			/* If the rental is less than the rental allowance */
			if (myRental.compareTo(myAllowance) < 0) {
				/* All of the rental is free so record it and note that we have finished */
				myTax.addAmount(myTaxBucket.setAmount(myRental));
				isFinished = true;
			}
			
			/* If we have not finished */
			if (!isFinished) {
				/* Remove allowance from rental figure */
				myRental.subtractAmount(myAllowance);

				/* If the rental is greater than the remaining allowance */
				if (myRental.compareTo(pBands.theAllowance) > 0) {
					/* Determine the remaining allowance */
					myAllowance.addAmount(pBands.theAllowance);
					
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(myAllowance));
				
					/* Adjust the rental to remove allowance */
					myRental.subtractAmount(pBands.theAllowance);
					pBands.theAllowance.setZero();
				}
			
				/* else still have allowance left after rental */
				else {
					/* Determine the remaining allowance */
					myAllowance.addAmount(myRental);
					
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(myAllowance));
				
					/* Adjust the allowance to remove rental and note that we have finished */
					pBands.theAllowance.subtractAmount(myRental);
					isFinished = true;
				}
			}
				
			/* If we have salary left  */
			if (!isFinished) {
				/* If we have a low salary band */
				if (theYear.hasLoSalaryBand()) {
					/* Access the LowSalaryBucket */
					myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     		.searchFor(TaxClass.RENTALLO));
					myTaxBucket.setRate(theYear.getLoTaxRate());
					myTaxBucket.setParent(myTopBucket);
				
					/* If the rental is greater than the Low Tax Band */
					if (myRental.compareTo(pBands.theLoBand) > 0) {
						/* Set the tax bucket and add the tax */
						myTax.addAmount(myTaxBucket.setAmount(pBands.theLoBand));
					
						/* Adjust the rental to remove LoBand */
						myRental.subtractAmount(pBands.theLoBand);
						pBands.theLoBand.setZero();
					}
					
					/* else we still have band left after salary */
					else {
						/* Set the tax bucket and add the tax */
						myTax.addAmount(myTaxBucket.setAmount(myRental));
					
						/* Adjust the loBand to remove rental and note that we have finished */
						pBands.theLoBand.subtractAmount(myRental);
						isFinished = true;
					}
				}
				
				/* Else use up the Low Tax band */
				else {
					/* If the rental is greater than the Low Tax Band */
					if (myRental.compareTo(pBands.theLoBand) > 0) {
						/* We have used up the band */
						pBands.theLoBand.setZero();
					}
					else {
						/* Adjust the band to remove rental */
						pBands.theLoBand.subtractAmount(myRental);
					}					
				}
			}
					
			/* If we have Rental left */
			if (!isFinished) {
				/* Access the BasicRentalBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     	.searchFor(TaxClass.RENTALBASIC));
				myTaxBucket.setRate(theYear.getBasicTaxRate());
				myTaxBucket.setParent(myTopBucket);
					
				/* If the rental is greater than the Basic Tax Band */
				if (myRental.compareTo(pBands.theBasicBand) > 0) {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));
					
					/* Adjust the rental to remove BasicBand */
					myRental.subtractAmount(pBands.theBasicBand);
					pBands.theBasicBand.setZero();
				}
					
				/* else we still have band left after rental */
				else {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(myRental));
					
					/* Adjust the basicBand to remove salary and note that we have finished */
					pBands.theBasicBand.subtractAmount(myRental);
					isFinished = true;
				}
			}
			
			/* If we have rental left */
			if (!isFinished) {
				/* Access the HiRentalBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     	.searchFor(TaxClass.RENTALHI));
				myTaxBucket.setRate(theYear.getHiTaxRate());
				myTaxBucket.setParent(myTopBucket);
					
				/* If the rental is greater than the High Tax Band */
				if ((theYear.hasAdditionalTaxBand()) && 
					(myRental.compareTo(pBands.theHiBand) > 0)) {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));
					
					/* Adjust the rental to remove HiBand */
					myRental.subtractAmount(pBands.theHiBand);
					pBands.theHiBand.setZero();
				}
					
				/* else we still have band left after rental */
				else {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(myRental));
					
					/* Adjust the hiBand to remove rental and note that we have finished */
					if (theYear.hasAdditionalTaxBand())
						pBands.theHiBand.subtractAmount(myRental);
					isFinished = true;
				}
			}
			
			/* If we have rental left */
			if (!isFinished) {
				/* Access the AdditionalRentalBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     	.searchFor(TaxClass.RENTALADD));
				myTaxBucket.setRate(theYear.getAddTaxRate());
				myTaxBucket.setParent(myTopBucket);
					
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myRental));
			}
			
			/* Store the taxation total */
			myTopBucket.setTaxation(myTax);
			
			/* Return the tax bucket */
			return myTopBucket;
		}
		
		/**
		 * Calculate the tax due on Interest
		 * @param pBands the remaining allowances and tax bands
		 * @return the interest tax bucket
		 */
		public TaxBucket calculateInterestTax(taxBands pBands) {
			TranBucket     	mySrcBucket;
			TaxBucket     	myTaxBucket;
			TaxBucket     	myTopBucket;
			finObject.Money myInterest;
			finObject.Money myTax		= new finObject.Money(0);
			boolean			isFinished  = false;
			
			/* If we do not have a Low salary band */
			if (!theYear.hasLoSalaryBand()) {
				/* Remove LoTaxBand from BasicTaxBand */
				pBands.theBasicBand.subtractAmount(pBands.theLoBand);
			}				
			
			/* Access Interest */
			mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					           		.searchFor(TaxClass.GROSSINTEREST));
 			myInterest  = new finObject.Money(mySrcBucket.getAmount());
		
			/* Store the total into the TaxDueInterest Bucket */
			myTopBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.TAXDUEINTEREST));
			myTopBucket.setAmount(myInterest);
			
			/* Access the FreeInterestBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.INTERESTFREE));
			myTaxBucket.setParent(myTopBucket);
			
			/* If the interest is greater than the remaining allowance */
			if (myInterest.compareTo(pBands.theAllowance) > 0) {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(pBands.theAllowance));
				
				/* Adjust the interest to remove allowance */
				myInterest.subtractAmount(pBands.theAllowance);
				pBands.theAllowance.setZero();
			}
			
			/* else still have allowance left after interest */
			else {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myInterest));
				
				/* Adjust the allowance to remove interest and note that we have finished */
				pBands.theAllowance.subtractAmount(myInterest);
				isFinished = true;
			}
				
			/* If we have interest left */
			if (!isFinished) {
				/* Access the LowInterestBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     	.searchFor(TaxClass.INTERESTLO));
				myTaxBucket.setRate(theYear.getLoTaxRate());
				myTaxBucket.setParent(myTopBucket);
				
				/* If the interest is greater than the Low Tax Band */
				if (myInterest.compareTo(pBands.theLoBand) > 0) {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(pBands.theLoBand));
					
					/* Adjust the interest to remove LoBand */
					myInterest.subtractAmount(pBands.theLoBand);
					pBands.theLoBand.setZero();
				}
					
				/* else we still have band left after interest */
				else {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(myInterest));
					
					/* Adjust the loBand to remove interest and note that we have finished */
					pBands.theLoBand.subtractAmount(myInterest);
					isFinished = true;
				}
			}
					
			/* If we have interest left */
			if (!isFinished) {
				/* Access the BasicInterestBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     	.searchFor(TaxClass.INTERESTBASIC));
				myTaxBucket.setRate(theYear.getIntTaxRate());
				myTaxBucket.setParent(myTopBucket);
					
				/* If the interest is greater than the Basic Tax Band */
				if (myInterest.compareTo(pBands.theBasicBand) > 0) {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));
					
					/* Adjust the interest to remove BasicBand */
					myInterest.subtractAmount(pBands.theBasicBand);
					pBands.theBasicBand.setZero();
				}
					
				/* else we still have band left after interest */
				else {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(myInterest));
					
					/* Adjust the basicBand to remove interest and note that we have finished */
					pBands.theBasicBand.subtractAmount(myInterest);
					isFinished = true;
				}
			}
			
			/* If we have interest left */
			if (!isFinished) {
				/* Access the HiInterestBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     	.searchFor(TaxClass.INTERESTHI));
				myTaxBucket.setRate(theYear.getHiTaxRate());
				myTaxBucket.setParent(myTopBucket);
					
				/* If the interest is greater than the High Tax Band */
				if ((theYear.hasAdditionalTaxBand()) && 
					(myInterest.compareTo(pBands.theHiBand) > 0)) {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));
					
					/* Adjust the interest to remove HiBand */
					myInterest.subtractAmount(pBands.theHiBand);
					pBands.theHiBand.setZero();
				}
					
				/* else we still have band left after interest */
				else {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(myInterest));
					
					/* Adjust the hiBand to remove interest and note that we have finished */
					if (theYear.hasAdditionalTaxBand())
						pBands.theHiBand.subtractAmount(myInterest);
					isFinished = true;
				}
			}
			
			/* If we have interest left */
			if (!isFinished) {
				/* Access the AdditionalInterestBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     	.searchFor(TaxClass.INTERESTADD));
				myTaxBucket.setRate(theYear.getAddTaxRate());
				myTaxBucket.setParent(myTopBucket);
					
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myInterest));
			}
			
			/* Remaining tax credits are not reclaimable */
			/* so add any remaining allowance/LoTaxBand into BasicTaxBand */
			pBands.theBasicBand.addAmount(pBands.theAllowance);
			pBands.theBasicBand.addAmount(pBands.theLoBand);
			pBands.theAllowance.setZero();
			pBands.theLoBand.setZero();
			
			/* Store the taxation total */
			myTopBucket.setTaxation(myTax);
			
			/* Return the tax bucket */
			return myTopBucket;
		}
		
		/**
		 * calculate the tax due on dividends
		 * @param pBands the remaining allowances and tax bands
		 * @return the dividends tax bucket
		 */
		public TaxBucket calculateDividendsTax(taxBands pBands) {
			TranBucket     	mySrcBucket;
			TaxBucket     	myTaxBucket;
			TaxBucket     	myTopBucket;
			finObject.Money myDividends;
			finObject.Money myTax		= new finObject.Money(0);
			boolean			isFinished  = false;
			
			/* Access Dividends */
			mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					           		.searchFor(TaxClass.GROSSDIVIDEND));
			myDividends = new finObject.Money(mySrcBucket.getAmount());
		
			/* Access Unit Trust Dividends */
			mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					           		.searchFor(TaxClass.GROSSUTDIVS));
 			myDividends.addAmount(mySrcBucket.getAmount());
			
			/* Store the total into the TaxDueDividends Bucket */
			myTopBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.TAXDUEDIVIDEND));
			myTopBucket.setAmount(myDividends);
			
			/* Access the BasicDividendBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.DIVIDENDBASIC));
			myTaxBucket.setRate(theYear.getDivTaxRate());
			myTaxBucket.setParent(myTopBucket);
					
			/* If the dividends are greater than the Basic Tax Band */
			if (myDividends.compareTo(pBands.theBasicBand) > 0) {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));
					
				/* Adjust the dividends to remove BasicBand */
				myDividends.subtractAmount(pBands.theBasicBand);
				pBands.theBasicBand.setZero();
			}
					
			/* else we still have band left after dividends */
			else {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myDividends));
					
				/* Adjust the basicBand to remove dividends and note that we have finished */
				pBands.theBasicBand.subtractAmount(myDividends);
				isFinished = true;
			}
			
			/* If we have dividends left */
			if (!isFinished) {
				/* Access the HiDividendsBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     	.searchFor(TaxClass.DIVIDENDHI));
				myTaxBucket.setRate(theYear.getHiDivTaxRate());
				myTaxBucket.setParent(myTopBucket);
					
				/* If the dividends are greater than the High Tax Band */
				if ((theYear.hasAdditionalTaxBand()) && 
					(myDividends.compareTo(pBands.theHiBand) > 0)) {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));
					
					/* Adjust the dividends to remove HiBand */
					myDividends.subtractAmount(pBands.theHiBand);
					pBands.theHiBand.setZero();
				}
					
				/* else we still have band left after dividends */
				else {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(myDividends));
					
					/* Adjust the hiBand to remove dividends and note that we have finished */
					if (theYear.hasAdditionalTaxBand())
						pBands.theHiBand.subtractAmount(myDividends);
					isFinished = true;
				}
			}
			
			/* If we have dividends left */
			if (!isFinished) {
				/* Access the AdditionalDividendsBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     	.searchFor(TaxClass.DIVIDENDADD));
				myTaxBucket.setRate(theYear.getAddDivTaxRate());
				myTaxBucket.setParent(myTopBucket);
					
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myDividends));
			}
			
			/* Store the taxation total */
			myTopBucket.setTaxation(myTax);
			
			/* Return the tax bucket */
			return myTopBucket;
		}
		
		/**
		 * calculate the tax due on taxable gains
		 * @param pBands the remaining allowances and tax bands
		 * @return the taxable gains bucket
		 */
		public TaxBucket calculateTaxableGainsTax(taxBands pBands) {
			TranBucket     		mySrcBucket;
			TaxBucket     		myTaxBucket;
			TaxBucket     		myTopBucket;
			TaxBucket     		mySliceBucket;
			finObject.Money 	myGains;
			finObject.Money		mySlice;
			finObject.Money		myHiTax;
			finObject.Money 	myTax		= new finObject.Money(0);
			boolean				isFinished  = false;
			
			/* Access Gains */
			myGains = (theCharges != null) 	? theCharges.getGainsTotal()
											: new finObject.Money(0);

			/* Store the total into the TaxDueTaxGains Bucket */
			myTopBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.TAXDUETAXGAINS));
			myTopBucket.setAmount(myGains);
			
			/* If the gains are less than the available basic tax band */
			if (myGains.compareTo(pBands.theBasicBand) <= 0) {
				/* Access the BasicGainsBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     		.searchFor(TaxClass.GAINSBASIC));
				myTaxBucket.setRate(theYear.getBasicTaxRate());
				myTaxBucket.setParent(myTopBucket);
						
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myGains));
					
				/* Adjust the basic band to remove taxable gains */
				pBands.theBasicBand.subtractAmount(myGains);
				isFinished = true;
			}
					
			/* If we are not finished but either have no basic band left
			 *  or are prevented from top-slicing due to using age allowances */
			if ((!isFinished) && 
				((!pBands.theBasicBand.isNonZero()) ||
				 (hasAgeAllowance))) {
				/* Access the BasicGainsBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     	.searchFor(TaxClass.GAINSBASIC));
				myTaxBucket.setRate(theYear.getBasicTaxRate());
				myTaxBucket.setParent(myTopBucket);
					
				/* If the gains is greater than the Basic Tax Band */
				if (myGains.compareTo(pBands.theBasicBand) > 0) {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));
					
					/* Adjust the interest to remove BasicBand */
					myGains.subtractAmount(pBands.theBasicBand);
					pBands.theBasicBand.setZero();
				}

				/* else case already handled */
				
				/* Access the HiGainsBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     		.searchFor(TaxClass.GAINSHI));
				myTaxBucket.setRate(theYear.getHiTaxRate());
				myTaxBucket.setParent(myTopBucket);
					
				/* If the gains are greater than the High Tax Band */
				if ((theYear.hasAdditionalTaxBand()) && 
					(myGains.compareTo(pBands.theHiBand) > 0)) {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));
					
					/* Adjust the gains to remove HiBand */
					myGains.subtractAmount(pBands.theHiBand);
					pBands.theHiBand.setZero();
				}
					
				/* else we still have band left after gains */
				else {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(myGains));
					
					/* Adjust the hiBand to remove dividends and note that we have finished */
					if (theYear.hasAdditionalTaxBand())
						pBands.theHiBand.subtractAmount(myGains);
					isFinished = true;
				}
				
				/* If we have gains left */
				if (!isFinished) {
					/* Access the AdditionalGainsBucket */
					myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
								     	.searchFor(TaxClass.GAINSADD));
					myTaxBucket.setRate(theYear.getAddDivTaxRate());
					myTaxBucket.setParent(myTopBucket);
						
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(myGains));
					isFinished = true;
				}
			}
			
			/* If we are not finished then we need top-slicing relief */
			if (!isFinished) { 
				/* Access to taxable slice */
				mySlice 		= theCharges.getSliceTotal();
				hasGainsSlices 	= true;

				/* Access the TaxDueSlice Bucket */
				mySliceBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     		.searchFor(TaxClass.TAXDUESLICE));
				mySliceBucket.setAmount(mySlice);
						
				/* Access the BasicSliceBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     		.searchFor(TaxClass.SLICEBASIC));
				myTaxBucket.setRate(theYear.getBasicTaxRate());
				myTaxBucket.setParent(mySliceBucket);
						
				/* If the slice is less than the available basic tax band */
				if (mySlice.compareTo(pBands.theBasicBand) < 0) {
					/* Set the slice details */
					myTax.addAmount(myTaxBucket.setAmount(mySlice));

					/* Distribute the Tax back to the chargeable events */
					theCharges.applyTax(myTax, theCharges.getSliceTotal());
					
					/* Access the BasicGainsBucket */
					myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     		.searchFor(TaxClass.GAINSBASIC));
					myTaxBucket.setRate(theYear.getBasicTaxRate());
					
					/* Only basic rate tax is payable */
					myTaxBucket.setAmount(myGains);
					mySliceBucket.setTaxation(myTax);
				}
				
				/* else we are using up the basic rate tax band */
				else {
					/* Set the slice details */
					myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));
					
					/* Subtract the basic band from the slice */
					mySlice.subtractAmount(pBands.theBasicBand);
					
					/* Access the BasicGainsBucket */
					myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     		.searchFor(TaxClass.GAINSBASIC));
					myTaxBucket.setRate(theYear.getBasicTaxRate());
					
					/* Basic Rate tax is payable on the remainder of the basic band */
					myTaxBucket.setAmount(pBands.theBasicBand);
					
					/* Remember this taxation amount to remove from HiTax bucket */
					myHiTax = new finObject.Money(myTaxBucket.getTaxation());
					myHiTax.negate();
					
					/* Access the HiSliceBucket */
					myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     		.searchFor(TaxClass.SLICEHI));
					myTaxBucket.setRate(theYear.getHiTaxRate());
					myTaxBucket.setParent(mySliceBucket);
							
					/* If the slice is greater than the High Tax Band */
					if ((theYear.hasAdditionalTaxBand()) && 
						(mySlice.compareTo(pBands.theHiBand) > 0)) {
						/* Set the tax bucket and add the tax */
						myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));
						
						/* Adjust the slice to remove HiBand */
						mySlice.subtractAmount(pBands.theHiBand);
						
						/* Access the AdditionalSliceBucket */
						myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
									     	.searchFor(TaxClass.SLICEADD));
						myTaxBucket.setRate(theYear.getAddTaxRate());
						myTaxBucket.setParent(mySliceBucket);
							
						/* Set the tax bucket and add the tax */
						myTax.addAmount(myTaxBucket.setAmount(mySlice));
					}
						
					/* else we still have band left after slice */
					else {
						/* Set the tax bucket and add the tax */
						myTax.addAmount(myTaxBucket.setAmount(mySlice));
					}
					
					/* Set the total tax into the slice bucket */
					mySliceBucket.setTaxation(myTax);
					
					/* Distribute the Slice back to the chargeable events */
					theCharges.applyTax(myTax, theCharges.getSliceTotal());
					
					/* Calculate the total tax payable */
					myTax = theCharges.getTaxTotal();
					
					/* HiRate tax is the calculated tax minus the tax payable in the basic band */
					myHiTax.addAmount(myTax);
					
					/* Access the HiGainsBucket */
					myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     		.searchFor(TaxClass.GAINSHI));
					myTaxBucket.setParent(myTopBucket);
					
					/* Subtract the basic band from the gains */
					myGains.subtractAmount(pBands.theBasicBand);
					
					/* Set the amount and tax explicitly */
					myTaxBucket.setAmount(myGains);
					myTaxBucket.setTaxation(myHiTax);
				}
				
				/* Re-access the gains */
				mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
						           		.searchFor(TaxClass.GROSSTAXGAINS));
				myGains     = new finObject.Money(mySrcBucket.getAmount());
			
				/* Subtract the gains from the tax bands */
				myGains.subtractAmount(pBands.theBasicBand);
				pBands.theBasicBand.setZero();
				if (theYear.hasAdditionalTaxBand())
					pBands.theHiBand.subtractAmount(myGains);
			}
			
			/* Access the TaxDueTaxableGains Bucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.TAXDUETAXGAINS));
			myTaxBucket.setTaxation(myTax);
			
			/* Return the tax bucket */
			return myTaxBucket;
		}
		
		/**
		 * calculate the tax due on capital gains
		 * @param pBands the remaining allowances and tax bands
		 * @return the capital gains tax bucket
		 */
		public TaxBucket calculateCapitalGainsTax(taxBands pBands) {
			TranBucket     		mySrcBucket;
			TaxBucket     		myTaxBucket;
			TaxBucket     		myTopBucket;
			finObject.Money 	myCapital;
			finObject.Money 	myAllowance;
			finObject.Money 	myTax		= new finObject.Money(0);
			finStatic.TaxRegime	myRegime	= theYear.getTaxRegime();
			boolean				isFinished  = false;
			
			/* Access Capital */
			mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					           		.searchFor(TaxClass.GROSSCAPGAINS));
			myCapital   = new finObject.Money(mySrcBucket.getAmount());
		
			/* Store the total into the TaxDueCapital Bucket */
			myTopBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.TAXDUECAPGAINS));
			myTopBucket.setAmount(myCapital);
			
			/* Access the FreeGainsBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.CAPITALFREE));
			myTaxBucket.setParent(myTopBucket);
			
			/* Pick up the capital allowance */
			myAllowance = theYear.getCapitalAllow();
			
			/* If the gains is greater than the capital allowance */
			if (myCapital.compareTo(myAllowance) > 0) {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myAllowance));
				
				/* Adjust the gains to remove allowance */
				myCapital.subtractAmount(myAllowance);
			}
			
			/* else allowance is sufficient */
			else {
				/* Set the correct value for the tax bucket and note that we have finished */
				myTax.addAmount(myTaxBucket.setAmount(myCapital));
				isFinished = true;
			}

			/* If we have gains left */
			if (!isFinished) {
				/* Access the BasicGainsBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     			.searchFor(TaxClass.CAPITALBASIC));
				myTaxBucket.setRate((myRegime.hasCapitalGainsAsIncome()
										? theYear.getBasicTaxRate()
										: theYear.getCapTaxRate()));
				myTaxBucket.setParent(myTopBucket);
					
				/* Determine whether we need to use basic tax band */
				boolean bUseBasicBand = ((myRegime.hasCapitalGainsAsIncome()) ||
					         		 	 (theYear.getHiCapTaxRate() != null));
			
				/* If the gains is greater than the Basic Tax Band and we have no higher rate */
				if ((myCapital.compareTo(pBands.theBasicBand) > 0) ||
					(!bUseBasicBand)){
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));
					
					/* Adjust the gains to remove BasicBand */
					myCapital.subtractAmount(pBands.theBasicBand);
					pBands.theBasicBand.setZero();
				}
					
				/* else we still have band left after gains */
				else {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(myCapital));
					
					/* Adjust the basicBand to remove capital and note that we have finished */
					if (bUseBasicBand)
						pBands.theBasicBand.subtractAmount(myCapital);
					isFinished = true;
				}
			}
			
			/* If we have gains left */
			if (!isFinished) {
				/* Access the HiGainsBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     		.searchFor(TaxClass.CAPITALHI));
				myTaxBucket.setRate((myRegime.hasCapitalGainsAsIncome()
						? theYear.getHiTaxRate()
						: theYear.getHiCapTaxRate()));
				myTaxBucket.setParent(myTopBucket);
					
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myCapital));
			}
			
			/* Store the taxation total */
			myTopBucket.setTaxation(myTax);
			
			/* Return the tax bucket */
			return myTopBucket;
		}
		
		/**
		 * Transaction Bucket list
		 */
		public class TranList {
			/**
			 * The underlying list class
			 */
			private finLink.linkCtl   theList     = null;

			/**
			 * Get the first element of the list
			 * @return the first element (or <code>null</code>)
			 */
			protected TranBucket getFirst()    { return (TranBucket)theList.getFirst(); }

			/**
			 * Get the last element of the list
			 * @return the last element (or <code>null</code>)
			 */
			protected TranBucket getLast()     { return (TranBucket)theList.getLast(); }
			
			/**
			 * Construct a top-level List
			 */
			public TranList() { theList  = new finLink.linkCtl(true); }
	
			/**
			 * Search for a particular bucket
			 * 
			 * @param pName  Name of Item
			 * @param pOrder Order of Item
			 * @param pType  Type of bucket
			 * @return The Item if present (or null)
			 */
			protected TranBucket searchFor(String pName, int pOrder, BucketType pType) {
				TranBucket myCurr;
				BucketType myType;
				String     myName;
				int        iDiff;
			
				/* Loop through the items to find the match */
				for (myCurr = getFirst();
			         myCurr != null;
			         myCurr = myCurr.getNext()) {
					myType = myCurr.getBucket();
					if (getBuckOrder(myType) < getBuckOrder(pType)) continue;
					if (getBuckOrder(myType) > getBuckOrder(pType)) 
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
			 * Obtain or allocate a bucket for a transact type
			 * 
			 * @param  pTransact Transaction Type
			 * @return The Transact Bucket
			 */
			protected TranBucket getTransBucket(finStatic.TransType pTransact) {
				TranBucket   myBucket;
				
				/* Find an existing transact bucket */
				myBucket = searchFor(pTransact.getName(), pTransact.getOrder(), BucketType.DETAIL);
						
				/* If we did not find a bucket */
				if (myBucket == null) {
					/* Allocate a new bucket and add to the list */
					myBucket = new TranBucket(theList, pTransact);
					myBucket.addToList();
				}
						
				/* Return the bucket */
				return myBucket;
			}
				
			/**
			 * Obtain or allocate a bucket for a tax type
			 * 
			 * @param  pTaxType Tax Type
			 * @return The Transact Bucket
			 */
			protected TranBucket getSummaryBucket(finStatic.TaxType pTaxType) {
				TranBucket   myBucket;
				
				/* Find an existing tax bucket */
				myBucket = searchFor(pTaxType.getName(), pTaxType.getOrder(), BucketType.SUMMARY);
						
				/* If we did not find a bucket */
				if (myBucket == null) {
					/* Allocate a new bucket and add to the list */
					myBucket = new TranBucket(theList, pTaxType);
					myBucket.addToList();
				}
						
				/* Return the bucket */
				return myBucket;
			}
			
			/**
			 * Resolve a transaction list to remove null entries
			 */
			protected void prune() {
				TranBucket myCurr;
				TranBucket myNext;
				
				/* Loop through the Buckets */
				for (myCurr = getFirst();
				     myCurr != null;
				     myCurr = myNext) {
					
					/* Determine the next element */
					myNext = myCurr.getNext();
					
					/* If we have an empty bucket */
				    if ((!myCurr.getAmount().isNonZero()) &&
					    (!myCurr.getOldAmount().isNonZero())) {
				   
						/* Unlink the entry */
				    	myCurr.unLink();
				    }
				}
				
				/* Return */
				return;
			}
		}
			
		/**
		 * Tax Bucket list
		 */
		public class TaxList {
			/**
			 * The underlying list class
			 */
			private finLink.linkCtl   theList     = null;

			/**
			 * Get the first element of the list
			 * @return the first element (or <code>null</code>)
			 */
			protected TaxBucket getFirst()    { return (TaxBucket)theList.getFirst(); }

			/**
			 * Get the last element of the list
			 * @return the last element (or <code>null</code>)
			 */
			protected TaxBucket getLast()     { return (TaxBucket)theList.getLast(); }
			
			/**
			 * Construct a top-level List
			 */
			public TaxList() { theList  = new finLink.linkCtl(true); }
	
			/**
			 * Search for a particular bucket
			 * 
			 * @param pName  Name of Item
			 * @param pOrder Order of Item
			 * @param pType  Type of bucket
			 * @return The Item if present (or null)
			 */
			protected TaxBucket searchFor(String pName, int pOrder, BucketType pType) {
				TaxBucket  myCurr;
				BucketType myType;
				String     myName;
				int        iDiff;
			
				/* Loop through the items to find the match */
				for (myCurr = getFirst();
			         myCurr != null;
			         myCurr = myCurr.getNext()) {
					myType = myCurr.getBucket();
					if (getBuckOrder(myType) < getBuckOrder(pType)) continue;
					if (getBuckOrder(myType) > getBuckOrder(pType)) 
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
			 * Obtain or allocate a bucket for a tax type
			 * 
			 * @param  pTaxType Tax Type
			 * @return The Transact Bucket
			 */
			protected TaxBucket getTaxBucket(finStatic.TaxType pTaxType) {
				TaxBucket   myBucket;
				
				/* Find an existing tax bucket */
				myBucket = searchFor(pTaxType.getName(), pTaxType.getOrder(), getTaxBucketType(pTaxType));
						
				/* If we did not find a bucket */
				if (myBucket == null) {
					/* Allocate a new bucket and add to the list */
					myBucket = new TaxBucket(theList, pTaxType);
					myBucket.addToList();
				}
						
				/* Return the bucket */
				return myBucket;
			}
			
			/**
			 * Resolve a transaction list to remove null entries
			 */
			protected void prune() {
				TaxBucket myCurr;
				TaxBucket myNext;
				
				/* Loop through the Buckets */
				for (myCurr = getFirst();
				     myCurr != null;
				     myCurr = myNext) {
					
					/* Determine the next element */
					myNext = myCurr.getNext();
					
					/* If we have an empty bucket */
			    	if (((myCurr.getAmount() == null) ||
				         (!myCurr.getAmount().isNonZero())) &&
				    	((myCurr.getTaxation() == null) ||
				    	 (!myCurr.getTaxation().isNonZero()))) {
						/* Unlink the entry */
				    	myCurr.unLink();
				    }
				}
				
				/* Return */
				return;
			}
		}
			
		/**
		 * The transaction bucket class
		 */
		public class TranBucket  implements finLink.linkObject {
			/**
			 * The linking element for this item
			 */
			private linkCtl.linkElement 	theLink    = null;
			
			/* Members */
			private		String				theName      	= null;
			private 	int             	theOrder 	 	= -1;
			private 	BucketType			theBucket	 	= null;
			private 	finStatic.TransType theTransact  	= null;
			private 	finStatic.TaxType   theTaxType   	= null;
			private		finObject.Money     theAmount    	= null;
			private     finObject.Money     theOldAmount 	= null;
			private     finObject.Money     theTaxCredit 	= null;
			private     finObject.Money     theOldTaxCred	= null;
			
			/**
			 * Get the next item in the list 
			 * @return the next item or <code>null</code>
			 */
			public TranBucket getNext()    { return (TranBucket)theLink.getNext(); }

			/**
			 * Get the previous item in the list 
			 * @return the previous item or <code>null</code>
			 */
			public TranBucket getPrev()    { return (TranBucket)theLink.getPrev();  }

			/**
			 * Add the item to the list 
			 */
			public void        addToList()  { theLink.addToList(); }
			
			/**
			 * Unlink the item from the list
			 */
			public void        unLink()     { theLink.unLink(); }
			
			/* Access methods */
			public 	String				 getName()      	{ return theName; }
			private int			 		 getOrder()     	{ return theOrder; }
			public 	BucketType			 getBucket()   	 	{ return theBucket; }
			public  finStatic.TransType  getTransType() 	{ return theTransact; }
			public  finStatic.TaxType    getTaxType()   	{ return theTaxType; }
			public  finObject.Money      getAmount()    	{ return theAmount; }
			public  finObject.Money      getOldAmount() 	{ return theOldAmount; }
			public  finObject.Money      getTaxCredit()  	{ return theTaxCredit; }
			public  finObject.Money      getOldTaxCred()	{ return theOldTaxCred; }

			/* Constructors */
			private TranBucket(finLink.linkCtl pList, finStatic.TransType pTransact) { 
				theLink      	= pList.new linkElement(this);
				theName      	= pTransact.getName();
				theOrder     	= pTransact.getOrder();
				theBucket    	= BucketType.DETAIL;
				theTransact  	= pTransact;
				theAmount    	= new finObject.Money(0);
				theOldAmount 	= new finObject.Money(0);
				theTaxCredit	= new finObject.Money(0);
				theOldTaxCred	= new finObject.Money(0);
			}
			private TranBucket(finLink.linkCtl pList, finStatic.TaxType pTaxType) {
				theLink      = pList.new linkElement(this);
				theName      = pTaxType.getName();
				theOrder     = pTaxType.getOrder();
				theBucket    = BucketType.SUMMARY;
				theTaxType   = pTaxType;
				theAmount    = new finObject.Money(0);
				theOldAmount = new finObject.Money(0);
			}
			private TranBucket(finLink.linkCtl pList, TranBucket pBucket) { 
				theLink      = pList.new linkElement(this);
				theName      = pBucket.getName();
				theOrder     = pBucket.getOrder();
				theBucket    = pBucket.getBucket();
				theTaxType   = pBucket.getTaxType();
				theTransact  = pBucket.getTransType();
				theAmount    = new finObject.Money(0);
				theOldAmount = new finObject.Money(pBucket.getAmount());
				if (theBucket == BucketType.DETAIL) {
					theTaxCredit 	= new finObject.Money(0);
					theOldTaxCred 	= new finObject.Money(pBucket.getTaxCredit());
				}
			}
			
			/**
			 * Override for standard method
			 */
			protected int compareTo(TranBucket that) {
				int result;
				if (this == that) return 0;
				if (that == null) return -1;
				if (getBuckOrder(theBucket) < getBuckOrder(that.theBucket)) return -1;
				if (getBuckOrder(theBucket) > getBuckOrder(that.theBucket)) return  1;
				if (theOrder < that.theOrder) return -1;
				if (theOrder > that.theOrder) return  1;
				result = theName.compareTo(that.theName);
				if (result == 0) return 0;
				else if (result < 0) return -1;
				else return 1;
			}

			public int linkCompareTo(linkObject that) {
				TranBucket myItem = (TranBucket)that;
				return this.compareTo(myItem);
			}
			
			/**
			 * Add the event to the bucket
			 * 
			 * @param  pEvent Event to add
			 */
			protected void addEvent(finData.Event pEvent) {
				finObject.Money   myAmount = pEvent.getAmount();
				finObject.Money   myTax	   = pEvent.getTaxCredit();

				/* Adjust the total amount */
				theAmount.addAmount(myAmount);
				
				/* Adjust the taxation if present */
				if (myTax != null) theTaxCredit.addAmount(myTax);
			}
			
			/**
			 * Add an amount
			 * 
			 * @param  pAmount Amount to set
			 */
			protected void addAmount(finObject.Money pAmount) {
				/* Adjust the income total */
				theAmount.addAmount(pAmount);
			}
			
			/**
			 * Add a bucket to a totalling bucket
			 * 
			 * @param  pBucket Bucket to add
			 */
			protected void addBucket(TranBucket pBucket) {
				finObject.Money myAmount = pBucket.getAmount();

				/* If this is the tax paid bucket and we are adding a tax creditable bucket */
				if ((theTaxType.isTaxPaid()) &&
					((pBucket.getTransType() != null) &&
					 (pBucket.getTransType().needsTaxCredit()))) {
					/* Just add the tax credits into the amount */
					theAmount.addAmount(pBucket.getTaxCredit());					
					theOldAmount.addAmount(pBucket.getOldTaxCred());					
				}
				
				/* else handle normally */
				else {
					/* Adjust the money total */
					theAmount.addAmount(myAmount);
					theOldAmount.addAmount(pBucket.getOldAmount());
				
					/* If this is a tax creditable bucket */
					if (theTaxType.hasTaxCredits()) {
						/* Add the tax credits into the amount */
						theAmount.addAmount(pBucket.getTaxCredit());
						theOldAmount.addAmount(pBucket.getOldTaxCred());					
					}
				}
			}
			
			/**
			 * Subtract a bucket from a totalling bucket
			 * 
			 * @param  pBucket Bucket to subtract
			 */
			protected void subtractBucket(TranBucket pBucket) {
				finObject.Money myAmount = pBucket.getAmount();

				/* Adjust the money total */
				theAmount.subtractAmount(myAmount);
				theOldAmount.subtractAmount(pBucket.getOldAmount());
			}
		}
		
		/* The Tax bucket class */
		public class TaxBucket  implements finLink.linkObject {
			/**
			 * The linking element for this item
			 */
			private linkCtl.linkElement theLink    = null;
			
			/* Members */
			private		String				theName      = null;
			private 	int             	theOrder 	 = -1;
			private 	BucketType			theBucket	 = null;
			private 	finStatic.TaxType   theTaxType   = null;
			private		finObject.Money     theAmount    = null;
			private     finObject.Money     theTaxation  = null;
			private		finObject.Rate      theRate      = null;
			private 	TaxBucket			theParent1	 = null;
			
			/**
			 * Get the next item in the list 
			 * @return the next item or <code>null</code>
			 */
			public TaxBucket getNext()    { return (TaxBucket)theLink.getNext(); }

			/**
			 * Get the previous item in the list 
			 * @return the previous item or <code>null</code>
			 */
			public TaxBucket getPrev()    { return (TaxBucket)theLink.getPrev();  }

			/**
			 * Add the item to the list 
			 */
			public void        addToList()  { theLink.addToList(); }
			
			/**
			 * Unlink the item from the list
			 */
			public void        unLink()     { theLink.unLink(); }
			
			/* Access methods */
			public 	String				 getName()      { return theName; }
			public 	BucketType			 getBucket()    { return theBucket; }
			public  finStatic.TaxType    getTaxType()   { return theTaxType; }
			public  finObject.Money      getAmount()    { return theAmount; }
			public  finObject.Money      getTaxation()  { return theTaxation; }
			public  finObject.Rate       getRate()      { return theRate; }
			public  TaxBucket			 getParent()	{ return theParent1; }

			private TaxBucket(finLink.linkCtl pList, finStatic.TaxType pTaxType) {
				theLink      = pList.new linkElement(this);
				theName      = pTaxType.getName();
				theOrder     = pTaxType.getOrder();
				theBucket    = getTaxBucketType(pTaxType);
				theTaxType   = pTaxType;
			}
			
			/**
			 * Override for standard method
			 */
			protected int compareTo(TaxBucket that) {
				int result;
				if (this == that) return 0;
				if (that == null) return -1;
				if (getBuckOrder(theBucket) < getBuckOrder(that.theBucket)) return -1;
				if (getBuckOrder(theBucket) > getBuckOrder(that.theBucket)) return  1;
				if (theOrder < that.theOrder) return -1;
				if (theOrder > that.theOrder) return  1;
				result = theName.compareTo(that.theName);
				if (result == 0) return 0;
				else if (result < 0) return -1;
				else return 1;
			}

			public int linkCompareTo(linkObject that) {
				TaxBucket myItem = (TaxBucket)that;
				return this.compareTo(myItem);
			}
			
			/**
			 * Set a taxation amount and calculate the tax on it
			 * 
			 * @param  	pAmount 		Amount to set
			 * @return the taxation on this bucket
			 */
			protected finObject.Money setAmount(finObject.Money pAmount) {
				/* Set the value */
				theAmount    = new finObject.Money(pAmount);
	            
	            /* Calculate the tax if we have a rate*/
				theTaxation = (theRate != null) ? theAmount.valueAtRate(theRate)
												: new finObject.Money(0);
				
				/* Return the taxation amount */
				return theTaxation;
			}
			
			/**
			 * Set explicit taxation value 
			 * 
			 * @param  	pAmount 		Amount to set
			 */
			protected void setTaxation(finObject.Money pAmount) {
				/* Set the value */
				theTaxation = new finObject.Money(pAmount);
			}
			
			/**
			 * Set parent bucket for reporting purposes
			 * @param  	pParent the parent bucket
			 */
			protected void setParent(TaxBucket pParent) {
				/* Set the value */
				theParent1 = pParent;
			}
			
			/**
			 * Set a tax rate
			 * 
			 * @param  pRate 	 Amount to set
			 */
			protected void setRate(finObject.Rate pRate) {
				/* Set the value */
				theRate    = pRate;
			}
		}
		
		/* The Chargeable event class */
		public class chargeableEvent {
			/* Members */
			private		finData.Event		theEvent    = null;
			private		finObject.Money		theSlice    = null;
			private		finObject.Money		theTaxation = null;
			private 	chargeableEvent 	theNext 	= null;
			
			/**
			 * Get the next item in the list 
			 * @return the next item or <code>null</code>
			 */
			public chargeableEvent 	getNext()		{ return theNext; }

			/**
			 * Get the Date of the chargeable event 
			 * @return the date of the chargeable event
			 */
			public finObject.Date 	getDate()		{ return theEvent.getDate(); }

			/**
			 * Get the Description of the chargeable event 
			 * @return the description of the chargeable event
			 */
			public String 			getDesc()		{ return theEvent.getDesc(); }

			/**
			 * Get the Amount of the chargeable event 
			 * @return the amount of the chargeable event
			 */
			public finObject.Money	getAmount()		{ return theEvent.getAmount(); }

			/**
			 * Get the TaxCredit of the chargeable event 
			 * @return the tax credit of the chargeable event
			 */
			public finObject.Money	getTaxCredit()	{ return theEvent.getTaxCredit(); }

			/**
			 * Get the Taxation of the chargeable event 
			 * @return the taxation of the chargeable event
			 */
			public finObject.Money	getTaxation()	{ return theTaxation; }

			/**
			 * Get the Slice of the chargeable event 
			 * @return the slice of the chargeable event
			 */
			public finObject.Money	getSlice()		{ return theSlice; }

			/**
			 * Get the Years of the chargeable event 
			 * @return the years of the chargeable event
			 */
			public Integer			getYears()		{ return theEvent.getYears(); }

			/**
			 * Constructor
			 * @param pEvent
			 * @param pFirst
			 */
			private chargeableEvent(finData.Event pEvent, chargeableEvent pFirst) {
				chargeableEvent myPrev = null;
				long			myValue;
				
				/* Store the event */
				theEvent = pEvent;
				
				/* Access the slice value of the event */
				myValue 	 = pEvent.getAmount().getAmount();
				myValue 	/= pEvent.getYears();
				theSlice	 = new finObject.Money(myValue);
				
				/* If there are elements in the list */
				if (pFirst != null) {
					/* Determine the last element in the list */
					myPrev = pFirst;
					while (myPrev.theNext != null) 
						myPrev = myPrev.theNext;
					
					/* Link us in */
					myPrev.theNext = this;
				}
			}
			
			/**
			 * Get the SliceTotal of the chargeable event list. Each slice is the TaxCredit of the event
			 * divided by the number of years that the charge is to be sliced over
			 * @return the slice total of the chargeable event list 
			 */
			public finObject.Money	getSliceTotal()		{
				finObject.Money myTotal;
				
				/* If we are the last slice */
				if (theNext == null) {
					/* Total is just us */
					myTotal = new finObject.Money(theSlice);
				}
				
				/* else we have further elements */
				else {
					/* Get the total of the further elements */
					myTotal = theNext.getSliceTotal();
					
					/* Add in our slice */
					myTotal.addAmount(theSlice);
				}
				
				/* Return the total */
				return myTotal;
			}

			/**
			 * Get the TaxTotal of the chargeable event list 
			 * @return the tax total of the chargeable event list 
			 */
			public finObject.Money	getTaxTotal()		{
				finObject.Money myTotal;
				
				/* If we are the last slice */
				if (theNext == null) {
					/* Total is just us */
					myTotal = new finObject.Money(theTaxation);
				}
				
				/* else we have further elements */
				else {
					/* Get the total of the further elements */
					myTotal = theNext.getTaxTotal();
					
					/* Add in our slice */
					myTotal.addAmount(theTaxation);
				}
				
				/* Return the total */
				return myTotal;
			}
			
			/**
			 * Get the GainsTotal of the chargeable event list 
			 * @return the gains total of the chargeable event list 
			 */
			public finObject.Money	getGainsTotal()		{
				finObject.Money myTotal;
				
				/* If we are the last slice */
				if (theNext == null) {
					/* Total is just us */
					myTotal = new finObject.Money(getAmount());
				}
				
				/* else we have further elements */
				else {
					/* Get the total of the further elements */
					myTotal = theNext.getGainsTotal();
					
					/* Add in our slice */
					myTotal.addAmount(getAmount());
				}
				
				/* Return the total */
				return myTotal;
			}
			
			/**
			 * Apply taxation of total slice to the individual events. This tax is first split 
			 * proportionally among the slices and then multiplied by the years of each individual event   
			 * @param pTax	the calculated taxation for the slice
			 * @param pTotal the slice total of the event list 
			 */
			public void	applyTax(finObject.Money pTax,
					             finObject.Money pTotal)	{
				finObject.Money myPortion;
				long			myValue;
				
				/* Calculate the portion of tax that applies to this slice */
				myPortion = pTax.valueAtWeight(theSlice, pTotal);
				
				/* Multiply by the number of years */
				myValue = myPortion.getValue() * getYears();
				theTaxation = new finObject.Money(myValue);
									
				/* If we have further slices */
				if (theNext != null) {
					/* Apply tax further down */
					theNext.applyTax(pTax, pTotal);
				}
			}
		}
		
		/**
		 * Class to hold active allowances and tax bands
		 */
		private class taxBands {
			/* properties */
			private finObject.Money 	theAllowance = null;
			private finObject.Money		theLoBand	 = null;
			private finObject.Money		theBasicBand = null;
			private finObject.Money		theHiBand	 = null;
		}
	}
	
	private static BucketType getTaxBucketType(finStatic.TaxType pTaxType) {
		BucketType myBucket = BucketType.STATIC;
		int        myOrder  = pTaxType.getOrder();
		switch (myOrder / finStatic.TaxType.CLASSDIVIDE) {
			case 1: myBucket = BucketType.DETAIL; break;
			case 2: myBucket = BucketType.SUMMARY; break;
			case 3: myBucket = BucketType.TOTAL; break;
		}
		return myBucket;
	}
}
