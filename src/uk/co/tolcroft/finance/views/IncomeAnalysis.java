package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.AnalysisYear.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number.*;

public class IncomeAnalysis {
	/* Members */
	private Date      		theDate     = null;
	private Account.List 	theAccounts = null;
	private List            theBuckets  = null;
	private boolean         hasTotals   = false;
	
	/* Access methods */
	public List  getBuckets()  { return theBuckets; }
	public Date  getDate()     { return theDate; }

	/* Constructor */
	public IncomeAnalysis(DataSet pData, Date sDate) {
		theDate     = sDate;
		theAccounts = pData.getAccounts();
		theBuckets  = new List();
	}

	/**
	 * Seed from previous end-of-year analysis
	 *
	 * @param  pAnalysis The Previous years analysis
	 */
	public void seedAnalysis(IncomeAnalysis pAnalysis) {
		List.ListIterator 	myIterator;
		Bucket 				myCurr;
		Bucket 				myBucket;
		
		/* If we have an analysis to copy */
		if (pAnalysis != null) {	
			/* Access the iterator */
			myIterator = pAnalysis.getBuckets().listIterator();
			
			/* Loop through the Buckets */
			while ((myCurr = myIterator.next()) != null) {
				/* If we have a non-empty bucket */
				if ((myCurr.getIncome().isNonZero()) ||
					(myCurr.getExpense().isNonZero())) {
					/* Copy and add the bucket */
					myBucket  = new Bucket(theBuckets, myCurr);
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
	public void processEvent(Event pEvent) {
		Account      	myAccount;
		TransactionType myTransType;
		Bucket          myBucket;
		
		/* If the credit account is external */
		myAccount = pEvent.getCredit();
		if ((myAccount.isExternal()) || (myAccount.isBenefit())) {
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
			else {	
				/* Add the Income Event to the bucket */
				myBucket.addIncomeEvent(pEvent);
			}
		}

		/* If this is a dividend from an asset */
		if ((myTransType.isDividend() &&
			(!myAccount.isExternal()))) {
			/* Access the parent account */
			myAccount = myAccount.getParent();
			
			/* Locate its bucket */
			myBucket = theBuckets.getAccountBucket(myAccount);
			
			/* Add the Income Event to the bucket */
			myBucket.addIncomeEvent(pEvent);	
		}

		/* If this is an interest payment */
		if (pEvent.isInterest()) {
			/* Access the parent account */
			myAccount = myAccount.getParent();
			
			/* Locate its bucket */
			myBucket = theBuckets.getAccountBucket(myAccount);
			
			/* Add the Income Event to the bucket */
			myBucket.addIncomeEvent(pEvent);	
		}

		/* If there is a tax credit associated with the item */
		if (pEvent.getTaxCredit() != null) {
			/* Access the TaxMan account */
			myAccount = theAccounts.getTaxMan();
			
			/* Locate its bucket */
			myBucket = theBuckets.getAccountBucket(myAccount);
			
			/* Add the Expense Event to the bucket */
			myBucket.addTaxExpenseEvent(pEvent);
		}
	}
	
	/**
	 * Add market movement
	 * 
	 * @param pMovement the Market movement
	 */
	public void addMarketMovement(AssetAnalysis.AssetBucket pMovement) {
		Bucket  myBucket;
		Money 	myMovement;
		
		/* Access the Market Bucket */
		myBucket = theBuckets.getAccountBucket(theAccounts.getMarket());
		
		/* Access the current market movement */
		myMovement = new Money(pMovement.getMarket());
		
		/* If the movement is positive */
		if (myMovement.isPositive())
		{
			/* Add the Market Growth */
		    myBucket.addIncomeAmount(myMovement);
		}
		
		/* else this is a market shrink */
		else {
			/* Negate the value */
			myMovement = new Money(myMovement);
			myMovement.negate();
			
			/* Add the market shrink */
		    myBucket.addExpenseAmount(myMovement);
		}			
	}
	
	/**
	 * Produce the totals
	 */
	public void produceTotals() {
		List.ListIterator	myIterator;
		Bucket  			myCurr;
		Bucket  			myTotals;
		Money 				myMoney;

		/* If we have not produced totals before */
		if (!hasTotals) {
			/* Access the totals bucket */
			myTotals = theBuckets.getTotalsBucket();
		
			/* Access the iterator */
			myIterator = theBuckets.listIterator();
			
			/* Loop through the items to find the match */
			while ((myCurr = myIterator.next()) != null) {
				/* Break loop if we are not a detail bucket */
				if (!myCurr.isDetail()) break;
				
				/* If the expense is negative */
				myMoney = myCurr.getExpense();
				if (!myMoney.isPositive()) {
					/* Swap it to the income side */
					myCurr.getIncome().subtractAmount(myMoney);
					myMoney.setZero();
				}
				
				/* If the old expense is negative */
				myMoney = myCurr.getPrevExpense();
				if (!myMoney.isPositive()) {
					/* Swap it to the income side */
					myCurr.getPrevIncome().subtractAmount(myMoney);
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
	
	public class List extends DataList<Bucket> {
		/**
		 * Construct a top-level List
		 */
		public List() {	super(ListStyle.VIEW, false); }

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
			pBuffer.append(Date.format(theDate)); 
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
			myIterator = listIterator();
			
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
			myIterator = listIterator();
			
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
		 * @return The Income Bucket
		 */
		protected Bucket getAccountBucket(Account pAccount) {
			Bucket   myBucket;
			
			/* Find an existing income bucket */
			myBucket = searchFor(pAccount.getName(), pAccount.getOrder(), BucketType.DETAIL);
					
			/* If we did not find a bucket */
			if (myBucket == null) {
				/* Allocate a new bucket and add to the list */
				myBucket = new Bucket(this, pAccount);
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
				myBucket = new Bucket(this, BucketType.TOTAL);
				myBucket.addToList();
			}
					
			/* Return the bucket */
			return myBucket;
		}
		
		/**
		 * Resolve an income list to remove null entries
		 */
		protected void prune() {
			ListIterator	myIterator;
			Bucket 			myCurr;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the items */
			while ((myCurr = myIterator.next()) != null) {
				/* If we have an empty bucket */
			    if ((!myCurr.getIncome().isNonZero())      &&
			    	(!myCurr.getExpense().isNonZero())     &&
			    	((myCurr.getPrevious() == null) ||
				     ((!myCurr.getPrevIncome().isNonZero())  &&
					  (!myCurr.getPrevExpense().isNonZero())))) {
			   
					/* Unlink the entry */
			    	myIterator.remove();
			    }
			}
			
			/* Return */
			return;
		}			
	}
		
	/* The bucket class */
	public class Bucket extends DataItem {
		/**
		 * The name of the object
		 */
		private static final String objName = "IncomeExpenseBucket";

		/* Members */
		private		String				theName        = null;
		private 	int             	theOrder 	   = -1;
		private 	BucketType			theBucket	   = null;
		private 	Account 			theAccount     = null;
		private		Money 				theIncome      = null;
		private		Money 				theExpense     = null;
		private		Bucket				thePrevious	   = null;

		/* Access methods */
		public 	String		getName()       { return theName; }
		private int    		getOrder()      { return theOrder; }
		public 	BucketType	getBucket()     { return theBucket; }
		public  Account     getAccount()    { return theAccount; }
		public  Money      	getIncome()     { return theIncome; }
		public  Money      	getExpense()    { return theExpense; }
		public 	Bucket    	getPrevious()  	{ return thePrevious; }
		public 	Money    	getPrevIncome()	{ return (thePrevious == null) ? null : thePrevious.theIncome; }
		public 	Money    	getPrevExpense(){ return (thePrevious == null) ? null : thePrevious.theExpense; }
		private boolean     		 isDetail()   { 
			return (theBucket == BucketType.DETAIL); }

		/* Constructors */
		private Bucket(List pList) {
			super(pList, 0);
			theIncome     = new Money(0);
			theExpense    = new Money(0);
		}
		private Bucket(List pList, Account pAccount) {
			super(pList, 0);
			theName       = pAccount.getName();
			theBucket     = BucketType.DETAIL;
			theOrder      = pAccount.getOrder();
			theAccount    = pAccount;
			theIncome     = new Money(0);
			theExpense    = new Money(0);
			thePrevious	  = new Bucket(pList);
		}
		private Bucket(List pList, BucketType pType) { 
			super(pList, 0);
			theName       = null;
			theBucket     = pType;
			theOrder      = 0;
			theIncome     = new Money(0);
			theExpense    = new Money(0);
			thePrevious   = new Bucket(pList);
		}
		private Bucket(List pList, Bucket pBucket) { 
			super(pList, 0);
			theName       = pBucket.getName();
			theBucket     = pBucket.getBucket();
			theOrder      = pBucket.getOrder();
			theAccount    = pBucket.getAccount();
			theIncome     = new Money(0);
			theExpense    = new Money(0);
			thePrevious   = pBucket;
		}
		
		/* Field IDs */
		public static final int FIELD_NAME  	= 0;
		public static final int FIELD_TYPE  	= 1;
		public static final int FIELD_ORDER  	= 2;
		public static final int FIELD_INCOME  	= 3;
		public static final int FIELD_EXPENSE   = 4;
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
				case FIELD_INCOME: 		return "Income";
				case FIELD_EXPENSE:		return "Expense";
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
				case FIELD_INCOME: 	
					myString += Money.format(theIncome);
					break;
				case FIELD_EXPENSE: 	
					myString += Money.format(theExpense);
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
			
			/* Make sure that the object is a IncomeBucket */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the object as a Bucket */
			Bucket myBucket = (Bucket)pThat;
			
			/* Check for equality */
			if (Utils.differs(getName(),      	myBucket.getName())) 		return false;
			if (getBucket() != myBucket.getBucket()) 						return false;
			if (getOrder() 	!= myBucket.getOrder()) 						return false;
			if (Money.differs(getIncome(),    	myBucket.getIncome())) 		return false;
			if (Money.differs(getExpense(),   	myBucket.getExpense()))		return false;
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
			if (pThat.getClass() != this.getClass()) return -1;
			
			/* Access the object as a Bucket */
			Bucket myThat = (Bucket)pThat;
			
			/* Compare the bucket order */
			if (AnalysisYear.getBucketOrder(theBucket) < AnalysisYear.getBucketOrder(myThat.theBucket)) return -1;
			if (AnalysisYear.getBucketOrder(theBucket) > AnalysisYear.getBucketOrder(myThat.theBucket)) return  1;

			/* Compare the bucket order */
			if (theOrder < myThat.theOrder) return -1;
			if (theOrder > myThat.theOrder) return  1;
			
			/* Compare the name */
			result = theName.compareTo(myThat.theName);
			if (result == 0) return 0;
			else if (result < 0) return -1;
			else return 1;
		}

		/**
		 * Add the income event to the bucket
		 * 
		 * @param  pEvent Event to add
		 */
		protected void addIncomeEvent(Event pEvent) {
			Money   myAmount 	= pEvent.getAmount();
			Money	myTaxCredit = pEvent.getTaxCredit();

			/* Adjust the income total */
			theIncome.addAmount(myAmount);
			if (myTaxCredit != null)
				theIncome.addAmount(myTaxCredit);
		}
		
		/**
		 * Add the expense event to the bucket
		 * 
		 * @param  pEvent Event to add
		 */
		protected void addExpenseEvent(Event pEvent) {
			Money   myAmount = pEvent.getAmount();

			/* Adjust the expense total */
			theExpense.addAmount(myAmount);
		}
		
		/**
		 * Add the tax expense event to the bucket
		 * 
		 * @param  pEvent Event to add
		 */
		protected void addTaxExpenseEvent(Event pEvent) {
			Money   myAmount = pEvent.getTaxCredit();

			/* Adjust the expense total */
			theExpense.addAmount(myAmount);
		}
		
		/**
		 * Subtract the expense event from the bucket
		 * 
		 * @param  pEvent Event to subtract
		 */
		protected void subtractExpenseEvent(Event pEvent) {
			Money   myAmount = pEvent.getAmount();

			/* Adjust the money total */
			theExpense.subtractAmount(myAmount);		
		}
		
		/**
		 * Add a bucket to a totalling bucket
		 * 
		 * @param  pBucket Bucket to add
		 */
		protected void addBucket(Bucket pBucket) {
			Money myIncome  = pBucket.getIncome();
			Money myExpense = pBucket.getExpense();

			/* Adjust the income/expense totals */
			theIncome.addAmount(myIncome);
			theExpense.addAmount(myExpense);
			if ((thePrevious != null) && (pBucket.getPrevious() != null)) 
				thePrevious.addBucket(pBucket.getPrevious());
		}
		
		/**
		 * Add an amount to an income bucket
		 * 
		 * @param  pAmount Amount to add
		 */
		protected void addIncomeAmount(Money pAmount) {
			theIncome.addAmount(pAmount);
		}
		
		/**
		 * Add an amount to an expense bucket
		 * 
		 * @param  pAmount Amount to add
		 */
		protected void addExpenseAmount(Money pAmount) {
			theExpense.addAmount(pAmount);
		}
	}
}
