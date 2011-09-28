package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.data.StaticClass.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.HistoryValues;

public class Analysis {
	/* Members */
	private FinanceData				theData 			= null;
	private AnalysisState			theAnalysisState	= AnalysisState.RAW;
	private BucketList				theList				= null;
	private ChargeableEvent.List	theCharges			= null;
	private TaxYear 				theYear				= null;
	private Date					theDate				= null;
	private Account					theAccount			= null;
	private boolean					hasGainsSlices		= false;
	private boolean					hasReducedAllow		= false;
	private int						theAge				= 0;

	/* Access methods */
	public FinanceData			getData() 			{ return theData; }
	public AnalysisState		getState() 			{ return theAnalysisState; }
	public BucketList 			getList() 			{ return theList; }
	public TaxYear 				getTaxYear() 		{ return theYear; }
	public Date 				getDate() 			{ return theDate; }
	public Account				getAccount()		{ return theAccount; }
	public ChargeableEvent.List getCharges()   	  	{ return theCharges; }
	public boolean     			hasReducedAllow() 	{ return hasReducedAllow; }
	public boolean     			hasGainsSlices()  	{ return hasGainsSlices; }
	public int					getAge()		  	{ return theAge; }
	
	/*Set methods */
	protected void	setState(AnalysisState pState) 			{ theAnalysisState = pState; }
	protected void	setAge(int pAge) 						{ theAge = pAge; }
	protected void	setHasReducedAllow(boolean hasReduced)	{ hasReducedAllow = hasReduced; }
	protected void	setHasGainsSlices(boolean hasSlices)	{ hasGainsSlices = hasSlices; }

	/**
	 * Constructor for a dated analysis
	 * @param pData	the data to analyse events for
	 * @param pDate	the Date for the analysis
	 */
	public Analysis(FinanceData	pData,
					Date		pDate) {
		/* Store the data */
		theData = pData;
		theDate	= pDate;
		
		/* Create a new list */
		theList 	= new BucketList(this);
		theCharges	= new ChargeableEvent.List();
	}
	
	/**
	 * Constructor for a dated account analysis
	 * @param pData	the data to analyse events for
	 * @param pAccount the account to analyse
	 * @param pDate	the Date for the analysis
	 */
	public Analysis(FinanceData pData,
					Account		pAccount,
					Date		pDate) {
		/* Store the data */
		theData 	= pData;
		theDate		= pDate;
		theAccount	= pAccount;
		
		/* Create a new list */
		theList 	= new BucketList(this);
		theCharges	= new ChargeableEvent.List();
	}
	
	/**
	 * Constructor for a dated account analysis
	 * @param pData	the data to analyse events for
	 * @param pYear the year to analyse
	 * @param pAnalysis the previous year analysis (if present)
	 */
	public Analysis(FinanceData	pData,
					TaxYear 	pYear,
					Analysis	pAnalysis) {
		/* Local variables */
		BucketList.ListIterator myIterator;
		AnalysisBucket			myCurr;
		AssetAccount			myAsset;
		DebtAccount				myDebt;
		MoneyAccount			myMoney;
		ExternalAccount			myExternal;
		TransDetail				myTrans;
		
		/* Store the data */
		theData = pData;
		theYear = pYear;
		theDate	= pYear.getDate();
		
		/* Create a new list */
		theList		= new BucketList(this);
		theCharges	= new ChargeableEvent.List();
	
		/* Return if we are the first analysis */
		if (pAnalysis == null) return;
		
		/* Access the iterator */
		myIterator = pAnalysis.getList().listIterator();
		
		/* Loop through the buckets */
		while ((myCurr = myIterator.next()) != null) {
			/* Switch on the bucket type */
			switch (myCurr.getBucketType()) {
				case ASSETDETAIL:
					if (myCurr.isActive()) {
						myAsset = new AssetAccount(theList, (AssetAccount)myCurr);
						theList.add(myAsset);
					}
					break;
				case DEBTDETAIL:
					if (myCurr.isActive()) {
						myDebt = new DebtAccount(theList, (DebtAccount)myCurr);
						theList.add(myDebt);
					}
					break;
				case MONEYDETAIL:
					if (myCurr.isActive()) {
						myMoney = new MoneyAccount(theList, (MoneyAccount)myCurr);
						theList.add(myMoney);
					}
					break;
				case EXTERNALDETAIL:
					if (myCurr.isActive()) {
						myExternal = new ExternalAccount(theList, (ExternalAccount)myCurr);
						theList.add(myExternal);
					}
					break;
				case TRANSDETAIL:
					if (myCurr.isActive()) {
						myTrans = new TransDetail(theList, (TransDetail)myCurr);
						theList.add(myTrans);
					}
					break;
			}
		}
	}
	
	/* The core AnalysisBucket Class */
	protected static abstract class AnalysisBucket extends DataItem<AnalysisBucket> {
		/* Members */
		private BucketType	theBucketType = null;
		private FinanceData	theData		  = null;
		private Date		theDate		  = null;
	
		/* Access methods */
		public 		BucketType 	getBucketType() { return theBucketType; }
		protected 	FinanceData getData()		{ return theData; }
		protected 	Date 		getDate()		{ return theDate; }

		/* Constructor */
		public AnalysisBucket(BucketList   	pList,
							  BucketType	pType,
							  int 			uId) {
			/* Call super-constructor */
			super(pList, uId + pType.getIdShift());
			theData = pList.theAnalysis.theData;
			theDate = pList.theAnalysis.theDate;
		
			/* Store the bucket type */
			theBucketType = pType;
		}
	
		/* Field IDs */
		public static final int FIELD_TYPE  	= DataItem.NUMFIELDS;
		public static final int NUMFIELDS	    = DataItem.NUMFIELDS+1;
	
		/**
		 * Determine the field name for a particular field
		 * @return the field name
		 */
		public static String	fieldName(int iField) {
			switch (iField) {
				case FIELD_TYPE: 		return "Type";
				default:		  		return DataItem.fieldName(iField);
			}
		}
	
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_TYPE:		
					myString += theBucketType;
					break;
				default: 			
					myString += super.formatField(iField, pValues);
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
		
			/* Make sure that the object is the same class */
			if (pThat.getClass() != this.getClass()) return false;
		
			/* Access the object as a Bucket */
			AnalysisBucket myThat = (AnalysisBucket)pThat;
		
			/* Check for equality */
			if (getId() 		!= myThat.getId()) 			return false;
			if (getBucketType() != myThat.getBucketType())	return false;
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
		
			/* Make sure that the object is an Analysis Bucket */
			if (!(pThat instanceof AnalysisBucket)) return -1;
		
			/* Access the object as am Analysis Bucket */
			AnalysisBucket myThat = (AnalysisBucket)pThat;
		
			/* Compare the bucket order */
			result = getBucketType().compareTo(myThat.getBucketType());
			return result;
		}
		
		/**
		 * is the bucket active (i.e. should it be copied)
		 */
		protected abstract boolean isActive();
		
		/**
		 * is the bucket relevant (i.e. should it be reported)
		 */
		protected abstract boolean isRelevant();
	}
	
	/* The List class */
	public class BucketList extends DataList<BucketList, AnalysisBucket> {
		/* Members */
		private Analysis theAnalysis	= null;
		
		/**
		 * The name of the object
		 */
		private static final String objName = "AnalysisBucket";

		/**
		 * Construct a top-level List
		 */
		public BucketList(Analysis pAnalysis) { 
			super(BucketList.class, AnalysisBucket.class, ListStyle.VIEW, false);
			theAnalysis = pAnalysis;
		}

		/* Obtain extract lists. */
		public BucketList getUpdateList() { return null; }
		public BucketList getEditList() 	{ return null; }
		public BucketList getShallowCopy() { return null; }
		public BucketList getDeepCopy(DataSet<?,?> pData) { return null; }
		public BucketList getDifferences(BucketList pOld) { return null; }

		/**
		 * Add a new item to the list
		 * @param pItem the item to add
		 * @return the newly added item
		 */
		public AnalysisBucket addNewItem(DataItem<?> pItem) { return null; }
	
		/**
		 * Add a new item to the edit list
		 * @return the newly added item
		 */
		public AnalysisBucket addNewItem() { return null; }
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }
		
		/**
		 * Add additional fields to HTML String
		 * @param pBuffer the string buffer 
		 */
		public void addHTMLFields(StringBuilder pBuffer) {
			/* Start the Fields section */
			pBuffer.append("<tr><th rowspan=\"4\">Fields</th></tr>");
				
			/* Format the date and account */
			pBuffer.append("<tr><td>State</td><td>"); 
			pBuffer.append(theAnalysisState); 
			pBuffer.append("</td></tr>"); 
			pBuffer.append("<tr><td>Date</td><td>"); 
			pBuffer.append(Date.format(theDate)); 
			pBuffer.append("</td></tr>"); 
			pBuffer.append("<tr><td>Account</td><td>"); 
			pBuffer.append(Account.format(theAccount)); 
			pBuffer.append("</td></tr>"); 
		}
		
		/**
		 * Obtain the AccountDetail Bucket for a given account 
		 * @param pAccount the account
		 * @return the bucket
		 */
		protected ActDetail getAccountDetail(Account pAccount) {
			/* Calculate the id that we are looking for */
			BucketType 	myBucket 	= BucketType.MONEYDETAIL;
			int 		uId 		= pAccount.getId() + myBucket.getIdShift();
			
			/* Locate the bucket in the list */
			ActDetail myItem = (ActDetail)searchFor(uId);
			
			/* If the item does not yet exist */
			if (myItem == null) {
				/* Determine the bucket type */
				myBucket = BucketType.getActBucketType(pAccount);
				
				/* Switch on the bucket type */
				switch (myBucket) {
					case MONEYDETAIL: 		myItem = new MoneyAccount(this, pAccount); 		break;
					case ASSETDETAIL:		myItem = new AssetAccount(this, pAccount); 		break;
					case EXTERNALDETAIL:	myItem = new ExternalAccount(this, pAccount);	break; 
					case DEBTDETAIL:
					default:				myItem = new DebtAccount(this, pAccount); 		break;
				}
				
				/* Add to the list */
				add(myItem);
			}
			
			/* Return the bucket */
			return myItem;
		}
		
		/**
		 * Obtain the Asset Summary Bucket for a given account type 
		 * @param pActType the account type
		 * @return the bucket
		 */
		protected AssetSummary getAssetSummary(AccountType pActType) {
			/* Calculate the id that we are looking for */
			BucketType 	myBucket 	= BucketType.ASSETSUMMARY;
			int 		uId 		= pActType.getId() + myBucket.getIdShift();
			
			/* Locate the bucket in the list */
			AssetSummary myItem = (AssetSummary)searchFor(uId);
			
			/* If the item does not yet exist */
			if (myItem == null) {
				/* Allocate it and add to the list */
				myItem = new AssetSummary(this, pActType);
				add(myItem);
			}
			
			/* Return the bucket */
			return myItem;
		}
		
		/**
		 * Obtain the Transaction Detail Bucket for a given transaction class 
		 * @param pTransType the transaction type
		 * @return the bucket
		 */
		protected TransDetail getTransDetail(TransClass pTransClass) {
			/* Calculate the id that we are looking for */
			TransactionType	myTrans	= theData.getTransTypes().searchFor(pTransClass);

			/* Return the bucket */
			return getTransDetail(myTrans);
		}
		
		/**
		 * Obtain the Transaction Detail Bucket for a given transaction type 
		 * @param pTransType the transaction type
		 * @return the bucket
		 */
		protected TransDetail getTransDetail(TransactionType pTransType) {
			/* Calculate the id that we are looking for */
			BucketType 	myBucket 	= BucketType.TRANSDETAIL;
			int 		uId 		= pTransType.getId() + myBucket.getIdShift();
			
			/* Locate the bucket in the list */
			TransDetail myItem = (TransDetail)searchFor(uId);
			
			/* If the item does not yet exist */
			if (myItem == null) {
				/* Allocate it and add to the list */
				myItem = new TransDetail(this, pTransType);
				add(myItem);
			}
			
			/* Return the bucket */
			return myItem;
		}
		
		/**
		 * Obtain the Transaction Summary Bucket for a given tax type 
		 * @param pTaxClass the taxation class
		 * @return the bucket
		 */
		protected TransSummary getTransSummary(TaxClass pTaxClass) {
			/* Calculate the id that we are looking for */
			BucketType 	myBucket 	= BucketType.TRANSSUMMARY;
			TaxType		myTaxType	= theData.getTaxTypes().searchFor(pTaxClass);
			int 		uId 		= myTaxType.getId() + myBucket.getIdShift();
			
			/* Locate the bucket in the list */
			TransSummary myItem = (TransSummary)searchFor(uId);
			
			/* If the item does not yet exist */
			if (myItem == null) {
				/* Allocate it and add to the list */
				myItem = new TransSummary(this, myTaxType);
				add(myItem);
			}
			
			/* Return the bucket */
			return myItem;
		}
		
		/**
		 * Obtain the Taxation Detail Bucket for a given tax type 
		 * @param pTaxClass the taxation class
		 * @return the bucket
		 */
		protected TaxDetail getTaxDetail(TaxClass pTaxClass) {
			/* Calculate the id that we are looking for */
			BucketType 	myBucket 	= BucketType.TAXDETAIL;
			TaxType		myTaxType	= theData.getTaxTypes().searchFor(pTaxClass);
			int 		uId 		= myTaxType.getId() + myBucket.getIdShift();
			
			/* Locate the bucket in the list */
			TaxDetail myItem = (TaxDetail)searchFor(uId);
			
			/* If the item does not yet exist */
			if (myItem == null) {
				/* Allocate it and add to the list */
				myItem = new TaxDetail(this, myTaxType);
				add(myItem);
			}
			
			/* Return the bucket */
			return myItem;
		}
		
		/**
		 * Obtain the Asset Total Bucket 
		 * @return the bucket
		 */
		protected AssetTotal getAssetTotal() {
			/* Calculate the id that we are looking for */
			BucketType 	myBucket 	= BucketType.ASSETTOTAL;
			int 		uId 		= myBucket.getIdShift();
			
			/* Locate the bucket in the list */
			AssetTotal myItem = (AssetTotal)searchFor(uId);
			
			/* If the item does not yet exist */
			if (myItem == null) {
				/* Allocate it and add to the list */
				myItem = new AssetTotal(this);
				add(myItem);
			}
			
			/* Return the bucket */
			return myItem;
		}
		
		/**
		 * Obtain the External Total Bucket 
		 * @return the bucket
		 */
		protected ExternalTotal getExternalTotal() {
			/* Calculate the id that we are looking for */
			BucketType 	myBucket 	= BucketType.EXTERNALTOTAL;
			int 		uId 		= myBucket.getIdShift();
			
			/* Locate the bucket in the list */
			ExternalTotal myItem = (ExternalTotal)searchFor(uId);
			
			/* If the item does not yet exist */
			if (myItem == null) {
				/* Allocate it and add to the list */
				myItem = new ExternalTotal(this);
				add(myItem);
			}
			
			/* Return the bucket */
			return myItem;
		}
		
		/**
		 * Obtain the Market Total Bucket 
		 * @return the bucket
		 */
		protected MarketTotal getMarketTotal() {
			/* Calculate the id that we are looking for */
			BucketType 	myBucket 	= BucketType.MARKETTOTAL;
			int 		uId 		= myBucket.getIdShift();
			
			/* Locate the bucket in the list */
			MarketTotal myItem = (MarketTotal)searchFor(uId);
			
			/* If the item does not yet exist */
			if (myItem == null) {
				/* Allocate it and add to the list */
				myItem = new MarketTotal(this);
				add(myItem);
			}
			
			/* Return the bucket */
			return myItem;
		}		
		
		/**
		 * Obtain the Transaction Total Bucket 
		 * @param pTaxClass the taxation class
		 * @return the bucket
		 */
		protected TransTotal getTransTotal(TaxClass pTaxClass) {
			/* Calculate the id that we are looking for */
			BucketType 	myBucket 	= BucketType.TRANSTOTAL;
			TaxType		myTaxType	= theData.getTaxTypes().searchFor(pTaxClass);
			int 		uId 		= myTaxType.getId() + myBucket.getIdShift();
			
			/* Locate the bucket in the list */
			TransTotal myItem = (TransTotal)searchFor(uId);
			
			/* If the item does not yet exist */
			if (myItem == null) {
				/* Allocate it and add to the list */
				myItem = new TransTotal(this, myTaxType);
				add(myItem);
			}
			
			/* Return the bucket */
			return myItem;
		}		

		/**
		 * Prune the list to remove irrelevant items 
		 */
		protected void prune() {
			ListIterator 	myIterator;
			AnalysisBucket	myCurr;

			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the buckets */
			while ((myCurr = myIterator.next()) != null) {
				/* Switch on the bucket type */
				switch (myCurr.getBucketType()) {
					/* Always keep asset details */
					case ASSETDETAIL:
						break;
					/* Remove item if it is irrelevant */
					default:
						if (!myCurr.isRelevant())
							myIterator.remove();
						break;
				}
			}
		}		
	}
	
	/* The Account Bucket class */
	protected static abstract class ActDetail extends AnalysisBucket {
		/* Members */
		private Account theAccount = null;

		/* Access methods */
		public String 		getName() 			{ return theAccount.getName(); }
		public Account 		getAccount()		{ return theAccount; }
		public AccountType 	getAccountType() 	{ return theAccount.getActType(); }

		/* Constructor */
		private ActDetail(BucketList	pList,
						  BucketType	pType,
						  Account		pAccount) {
			/* Call super-constructor */
			super(pList, pType, pAccount.getId());
			
			/* Store the account */
			theAccount = pAccount;
		}
		
		/* Field IDs */
		public static final int FIELD_ACCOUNT 	= AnalysisBucket.NUMFIELDS;
		public static final int NUMFIELDS	    = 1 + AnalysisBucket.NUMFIELDS;
		
		/**
		 * Determine the field name for a particular field
		 * @return the field name
		 */
		public static String	fieldName(int iField) {
			switch (iField) {
				case FIELD_ACCOUNT: 	return "Account";
				default:		  		return AnalysisBucket.fieldName(iField);
			}
		}
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_ACCOUNT:		
					myString += Account.format(theAccount);
					break;
				default:
					myString += super.formatField(iField, pValues);
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
			
			/* Make sure that the object is the same class */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the object as an Act Bucket */
			ActDetail myThat = (ActDetail)pThat;
			
			/* Check for equality */
			if (Account.differs(getAccount(), myThat.getAccount()).isDifferent())	return false;
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
			
			/* Make sure that the object is an Analysis Bucket */
			if (!(pThat instanceof AnalysisBucket)) return -1;
			
			/* Access the object as an Analysis Bucket */
			AnalysisBucket myBucket = (AnalysisBucket)pThat;
			
			/* Compare the bucket types */
			result = super.compareTo(myBucket);
			if (result != 0) return result;
			
			/* Access the object as an Act Bucket */
			ActDetail myThat = (ActDetail)pThat;
			
			/* Compare the Accounts */
			result = getAccount().compareTo(myThat.getAccount());
			return result;
		}		
		
		/**
		 * Adjust account for debit
		 * @param pEvent the event causing the debit
		 */
		protected abstract void adjustForDebit(Event pEvent);
		
		/**
		 * Adjust account for credit
		 * @param pEvent the event causing the credit
		 */
		protected abstract void adjustForCredit(Event pEvent);

		/**
		 * Create a save point
		 */
		protected abstract void createSavePoint();

		/**
		 * Restore a save point
		 */
		protected abstract void restoreSavePoint();
	}
	
	/* The Account Type Bucket class */
	private static abstract class ActType extends AnalysisBucket {
		/* Members */
		private AccountType theAccountType = null;

		/* Access methods */
		public String 		getName() 			{ return theAccountType.getName(); }
		public AccountType 	getAccountType() 	{ return theAccountType; }

		/* Constructor */
		private ActType(BucketList	pList,
						AccountType	pAccountType) {
			/* Call super-constructor */
			super(pList, BucketType.ASSETSUMMARY, pAccountType.getId());
			
			/* Store the account type */
			theAccountType = pAccountType;
		}
		
		/* Field IDs */
		public static final int FIELD_ACCOUNTTYPE 	= AnalysisBucket.NUMFIELDS;
		public static final int NUMFIELDS	    	= 1 + AnalysisBucket.NUMFIELDS;
		
		/**
		 * Determine the field name for a particular field
		 * @return the field name
		 */
		public static String	fieldName(int iField) {
			switch (iField) {
				case FIELD_ACCOUNTTYPE:	return "AccountType";
				default:		  		return AnalysisBucket.fieldName(iField);
			}
		}
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_ACCOUNTTYPE:		
					myString += AccountType.format(theAccountType);
					break;
				default:
					myString += super.formatField(iField, pValues);
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
			
			/* Make sure that the object is the same class */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the object as an ActType Bucket */
			ActType myThat = (ActType)pThat;
			
			/* Check for equality */
			if (AccountType.differs(getAccountType(), myThat.getAccountType()).isDifferent())	return false;
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
			
			/* Make sure that the object is an Analysis Bucket */
			if (!(pThat instanceof AnalysisBucket)) return -1;
			
			/* Access the object as an Analysis Bucket */
			AnalysisBucket myBucket = (AnalysisBucket)pThat;
			
			/* Compare the bucket types */
			result = super.compareTo(myBucket);
			if (result != 0) return result;
			
			/* Access the object as an ActType Bucket */
			ActType myThat = (ActType)pThat;
			
			/* Compare the AccountTypes */
			result = getAccountType().compareTo(myThat.getAccountType());
			return result;
		}		
	}
	
	/* The TransType Bucket class */
	private static abstract class TransType extends AnalysisBucket {
		/* Members */
		private TransactionType theTransType = null;

		/* Access methods */
		public String 			getName() 		{ return theTransType.getName(); }
		public TransactionType 	getTransType() 	{ return theTransType; }

		/* Constructor */
		private TransType(BucketList		pList,
						  TransactionType	pTransType) {
			/* Call super-constructor */
			super(pList, BucketType.TRANSDETAIL, pTransType.getId());
			
			/* Store the transaction type */
			theTransType = pTransType;
		}
		
		/* Field IDs */
		public static final int FIELD_TRANSTYPE 	= AnalysisBucket.NUMFIELDS;
		public static final int NUMFIELDS	    	= 1 + AnalysisBucket.NUMFIELDS;
		
		/**
		 * Determine the field name for a particular field
		 * @return the field name
		 */
		public static String	fieldName(int iField) {
			switch (iField) {
				case FIELD_TRANSTYPE:	return "TransType";
				default:		  		return AnalysisBucket.fieldName(iField);
			}
		}
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_TRANSTYPE:		
					myString += TransactionType.format(theTransType);
					break;
				default:
					myString += super.formatField(iField, pValues);
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
			
			/* Make sure that the object is the same class */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the object as an TransType Bucket */
			TransType myThat = (TransType)pThat;
			
			/* Check for equality */
			if (TransactionType.differs(getTransType(), myThat.getTransType()).isDifferent())	return false;
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
			
			/* Make sure that the object is an Analysis Bucket */
			if (!(pThat instanceof AnalysisBucket)) return -1;
			
			/* Access the object as an Analysis Bucket */
			AnalysisBucket myBucket = (AnalysisBucket)pThat;
			
			/* Compare the bucket types */
			result = super.compareTo(myBucket);
			if (result != 0) return result;
			
			/* Access the object as an TransType Bucket */
			TransType myThat = (TransType)pThat;
			
			/* Compare the TransactionTypes */
			result = getTransType().compareTo(myThat.getTransType());
			return result;
		}		
	}
	
	/* The Tax Bucket class */
	private static abstract class Tax extends AnalysisBucket {
		/* Members */
		private TaxType 	theTaxType = null;

		/* Access methods */
		public String 	getName() 		{ return theTaxType.getName(); }
		public TaxType 	getTaxType() 	{ return theTaxType; }

		/* Constructor */
		private Tax(BucketList	pList,
					TaxType		pTaxType) {
			/* Call super-constructor */
			super(pList, BucketType.getTaxBucketType(pTaxType), pTaxType.getId());
			
			/* Store the tax type */
			theTaxType = pTaxType;
		}
		
		/* Field IDs */
		public static final int FIELD_TAXTYPE 	= AnalysisBucket.NUMFIELDS;
		public static final int NUMFIELDS	    = 1 + AnalysisBucket.NUMFIELDS;
		
		/**
		 * Determine the field name for a particular field
		 * @return the field name
		 */
		public static String	fieldName(int iField) {
			switch (iField) {
				case FIELD_TAXTYPE:	return "TaxType";
				default:			return AnalysisBucket.fieldName(iField);
			}
		}
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_TAXTYPE:		
					myString += TaxType.format(theTaxType);
					break;
				default:
					myString += super.formatField(iField, pValues);
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
			
			/* Make sure that the object is the same class */
			if (pThat.getClass() != this.getClass()) return false;
			
			/* Access the object as a Tax Bucket */
			Tax myThat = (Tax)pThat;
			
			/* Check for equality */
			if (TaxType.differs(getTaxType(), myThat.getTaxType()).isDifferent())	return false;
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
			
			/* Make sure that the object is an Analysis Bucket */
			if (!(pThat instanceof AnalysisBucket)) return -1;
			
			/* Access the object as an Analysis Bucket */
			AnalysisBucket myBucket = (AnalysisBucket)pThat;
			
			/* Compare the bucket types */
			result = super.compareTo(myBucket);
			if (result != 0) return result;
			
			/* Access the object as an Tax Bucket */
			Tax myThat = (Tax)pThat;
			
			/* Compare the TaxTypes */
			result = getTaxType().compareTo(myThat.getTaxType());
			return result;
		}		
	}
	
	/* The ValueAccount Bucket class */
	protected static abstract class ValueAccount extends ActDetail {
		/* Members */
		private Money 	theValue 	= null;

		/* Override of getBase method */
		public ValueAccount getBase() 	{ return (ValueAccount)super.getBase(); }
		
		/* Access methods */
		public Money	getValue() 		{ return theValue; }
		public Money	getPrevValue() 	{ return (getBase() != null)
											? getBase().getValue() : null; }

		protected void	setValue(Money pValue) { theValue = pValue; }
		
		/* Constructor */
		private ValueAccount(BucketList	pList,
							 BucketType pType,
							 Account	pAccount) {
			/* Call super-constructor */
			super(pList, pType, pAccount);
			
			/* Initialise the money values */
			theValue 	= new Money(0);
		}
		
		/* Field IDs */
		public static final int FIELD_VALUE 	= ActDetail.NUMFIELDS;
		public static final int NUMFIELDS	    = 1 + ActDetail.NUMFIELDS;
		
		/**
		 * Determine the field name for a particular field
		 * @return the field name
		 */
		public static String	fieldName(int iField) {
			switch (iField) {
				case FIELD_VALUE:		return "Value";
				default:		  		return ActDetail.fieldName(iField);
			}
		}
		
		/**
		 * Determine the field name in a non-static fashion 
		 */
		public String getFieldName(int iField) { return fieldName(iField); }
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_VALUE:		
					myString += Money.format(theValue);
					break;
				default:
					myString += super.formatField(iField, pValues);
					break;
			}
			return myString;
		}
		
		/**
		 * is the bucket active (i.e. should it be copied)
		 */
		protected boolean isActive() {
			/* Copy if the value is non-zero */
			return theValue.isNonZero();
		}

		/**
		 * is the bucket relevant (i.e. should it be reported)
		 */
		protected boolean isRelevant() {
			/* Relevant if this value or the previous value is non-zero */
			return (theValue.isNonZero() || 
					((getPrevValue() != null) &&
					 (getPrevValue().isNonZero())));
		}
		
		/**
		 * Adjust account for debit
		 * @param pEvent the event causing the debit
		 */
		protected void adjustForDebit(Event pEvent) {
			/* Adjust for debit */
			theValue.subtractAmount(pEvent.getAmount());
		}
		
		/**
		 * Adjust account for credit
		 * @param pEvent the event causing the credit
		 */
		protected void adjustForCredit(Event pEvent) {
			/* Adjust for credit */
			theValue.addAmount(pEvent.getAmount());
		}
	}
		
	/* The MoneyAccount Bucket class */
	public static class MoneyAccount extends ValueAccount {
		/**
		 * The name of the object
		 */
		private static final String objName = "MoneyAccountBucket";

		/* Members */
		private Rate 			theRate			= null;
		private Date 			theMaturity		= null;
		private MoneyAccount	theSavePoint	= null;

		/* Override of getBase method */
		public MoneyAccount getBase() 	{ return (MoneyAccount)super.getBase(); }
		
		/* Access methods */
		public Rate 	getRate() 		{ return theRate; }
		public Date 	getMaturity()	{ return theMaturity; }

		/* Constructor */
		private MoneyAccount(BucketList	pList,
							 Account	pAccount) {
			/* Call super-constructor */
			super(pList,
				  BucketType.MONEYDETAIL,
				  pAccount);
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private MoneyAccount(BucketList		pList,
						   	 MoneyAccount	pPrevious) {
			/* Call super-constructor */
			super(pList, 
				  BucketType.MONEYDETAIL,
				  pPrevious.getAccount());

			/* Initialise the Money values */
			setValue(new Money(pPrevious.getValue()));
			
			/* Add the link to the previous item */
			setBase(new MoneyAccount(pPrevious));
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private MoneyAccount(MoneyAccount	pPrevious) {
			/* Call super-constructor */
			super((BucketList)pPrevious.getList(),
				  BucketType.MONEYDETAIL,
				  pPrevious.getAccount());

			/* Initialise the Money values */
			setValue(new Money(pPrevious.getValue()));
			if (pPrevious.getRate() != null)
				theRate		= new Rate(pPrevious.getRate());
			if (pPrevious.getMaturity() != null)
				theMaturity	= new Date(pPrevious.getMaturity());
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Field IDs */
		public static final int FIELD_RATE 		= ValueAccount.NUMFIELDS;
		public static final int FIELD_MATURITY	= 1 + ValueAccount.NUMFIELDS;
		public static final int NUMFIELDS	    = 2 + ValueAccount.NUMFIELDS;
		
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
				case FIELD_RATE:		return "Rate";
				case FIELD_MATURITY:	return "Maturity";
				default:		  		return ValueAccount.fieldName(iField);
			}
		}
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_RATE:		
					myString += Rate.format(theRate);
					break;
				case FIELD_MATURITY:		
					myString += Date.format(theMaturity);
					break;
				default:
					myString += super.formatField(iField, pValues);
					break;
			}
			return myString;
		}
		
		/**
		 * Determine the field name in a non-static fashion 
		 */
		public String getFieldName(int iField) { return fieldName(iField); }
		
		/**
		 * record the rate of the account at a given date
		 * @param pDate the date of valuation
		 */
		protected void recordRate(Date pDate) {
			AcctRate.List	myRates = getData().getRates();
			AcctRate 		myRate;
			Date			myDate;

			/* Obtain the appropriate price record */
			myRate = myRates.getLatestRate(getAccount(), getDate());
			myDate = getAccount().getMaturity();
			
			/* If we have a rate */
			if (myRate != null) { 
				/* Use Rate date instead */
				if (myDate == null) myDate = myRate.getDate();

				/* Store the rate */
				theRate = myRate.getRate();
			}
				
			/* Store the maturity */
			theMaturity = myDate;
		}
		
		/**
		 * Create a Save Point 
		 */
		protected void createSavePoint() {
			/* Create a save of the values */
			theSavePoint = new MoneyAccount(this);
		}

		/**
		 * Restore a Save Point 
		 */
		protected void restoreSavePoint() {
			/* If we have a Save point */
			if (theSavePoint != null) {
				/* Restore original value */
				setValue(new Money(theSavePoint.getValue()));
			}
		}
	}
	
	/* The DebtAccount Bucket class */
	public static class DebtAccount extends ValueAccount {
		/**
		 * The name of the object
		 */
		private static final String objName = "DebtAccountBucket";

		/* Members */
		private Money 		theSpend		= null;
		private DebtAccount	theSavePoint	= null;

		/* Override of getBase method */
		public DebtAccount getBase() 	{ return (DebtAccount)super.getBase(); }
		
		/* Access methods */
		public Money 	getSpend() 		{ return theSpend; }

		/* Constructor */
		private DebtAccount(BucketList	pList,
							Account		pAccount) {
			/* Call super-constructor */
			super(pList, BucketType.DEBTDETAIL, pAccount);
			
			/* Initialise the money values */
			theSpend 	= new Money(0);
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private DebtAccount(BucketList	pList,
						   	DebtAccount	pPrevious) {
			/* Call super-constructor */
			super(pList, 
				  BucketType.DEBTDETAIL,
				  pPrevious.getAccount());

			/* Initialise the Money values */
			setValue(new Money(pPrevious.getValue()));
			theSpend 	= new Money(0);
			
			/* Add the link to the previous item */
			setBase(new DebtAccount(pPrevious));
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private DebtAccount(DebtAccount	pPrevious) {
			/* Call super-constructor */
			super((BucketList)pPrevious.getList(),
				  BucketType.DEBTDETAIL,
				  pPrevious.getAccount());

			/* Initialise the Money values */
			setValue(new Money(pPrevious.getValue()));
			theSpend	= new Money(pPrevious.getSpend());
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Field IDs */
		public static final int FIELD_SPEND		= ValueAccount.NUMFIELDS;
		public static final int NUMFIELDS	    = 1 + ValueAccount.NUMFIELDS;
		
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
				case FIELD_SPEND:		return "Spend";
				default:		  		return ValueAccount.fieldName(iField);
			}
		}
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_SPEND:		
					myString += Money.format(theSpend);
					break;
				default:
					myString += super.formatField(iField, pValues);
					break;
			}
			return myString;
		}
		
		/**
		 * Determine the field name in a non-static fashion 
		 */
		public String getFieldName(int iField) { return fieldName(iField); }
		
		/**
		 * Create a Save Point 
		 */
		protected void createSavePoint() {
			/* Create a save of the values */
			theSavePoint = new DebtAccount(this);
		}

		/**
		 * Restore a Save Point 
		 */
		protected void restoreSavePoint() {
			/* If we have a Save point */
			if (theSavePoint != null) {
				/* Restore original value */
				setValue(new Money(theSavePoint.getValue()));
				theSpend = new Money(theSavePoint.getSpend());
			}
		}
	}
		
	/* The AssetAccount Bucket class */
	public static class AssetAccount extends ValueAccount {
		/**
		 * The name of the object
		 */
		private static final String objName = "AssetAccountBucket";

		/* Members */
		private CapitalEvent.List	theEvents		= null;
		private Money 				theCost 		= null;
		private Units 				theUnits		= null;
		private Money 				theGained		= null;

		private Money 				theInvested		= null;
		private Money 				theDividend		= null;
		private Money 				theGains		= null;

		private Money 				theProfit		= null;
		private Price 				thePrice		= null;
		private AssetAccount		theSavePoint	= null;
		
		/* Override of getBase method */
		public AssetAccount getBase() 	{ return (AssetAccount)super.getBase(); }
		
		/* Access methods */
		public Money	getCost() 		{ return theCost; }
		public Units 	getUnits() 		{ return theUnits; }
		public Money	getGained() 	{ return theGained; }
		public Money	getInvested()	{ return theInvested; }
		public Money	getDividend() 	{ return theDividend; }
		public Money	getGains() 		{ return theGains; }
		public Money	getProfit() 	{ return theProfit; }
		public Price	getPrice() 		{ return thePrice; }
		
		public Money	getPrevCost() 	{ return (getBase() != null)
												? getBase().getCost() : null; }
		public Units	getPrevUnits() 	{ return (getBase() != null)
												? getBase().getUnits() : null; }
		public Money	getPrevGained() { return (getBase() != null)
												? getBase().getGained() : null; }

		public CapitalEvent.List	getCapitalEvents() { return theEvents; }
		
		/* Constructor */
		private AssetAccount(BucketList	pList,
							 Account	pAccount) {
			/* Call super-constructor */
			super(pList, 
				  BucketType.ASSETDETAIL,
				  pAccount);
			
			/* Initialise the values */
			theUnits 		= new Units(0);
			theCost 		= new Money(0);
			theGained		= new Money(0);

			theInvested		= new Money(0);
			theDividend		= new Money(0);
			theGains	 	= new Money(0);
		
			/* allocate the Capital events */
			theEvents 		= new CapitalEvent.List(getData(), pAccount);
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private AssetAccount(BucketList		pList,
						   	 AssetAccount	pPrevious) {
			/* Call super-constructor */
			super(pList,
				  BucketType.ASSETDETAIL,
				  pPrevious.getAccount());

			/* Initialise the values */
			theUnits		= new Units(pPrevious.getUnits());
			theCost			= new Money(pPrevious.getCost());
			theGained		= new Money(pPrevious.getGained());
			theInvested		= new Money(0);
			theGains		= new Money(0);			
			theDividend		= new Money(0);
			
			/* Copy the Capital Events */
			theEvents		= pPrevious.getCapitalEvents();
			
			/* Add the link to the previous item */
			setBase(new AssetAccount(pPrevious));
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private AssetAccount(AssetAccount	pPrevious) {
			/* Call super-constructor */
			super((BucketList)pPrevious.getList(),
				  BucketType.ASSETDETAIL,
				  pPrevious.getAccount());

			/* Initialise the Money values */
			theUnits		= new Units(pPrevious.getUnits());
			theCost			= new Money(pPrevious.getCost());
			theGained		= new Money(pPrevious.getGained());
			theInvested		= new Money(pPrevious.getInvested());
			theDividend		= new Money(pPrevious.getDividend());
			theGains		= new Money(pPrevious.getGains());
			
			/* Copy price if available */
			if (pPrevious.getPrice() != null)
				thePrice	= new Price(pPrevious.getPrice());
			
			/* Initialise the Money values */
			setValue(new Money(pPrevious.getValue()));
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Field IDs */
		public static final int FIELD_COST 		= ValueAccount.NUMFIELDS;
		public static final int FIELD_UNITS		= 1 + ValueAccount.NUMFIELDS;
		public static final int FIELD_GAINED	= 2 + ValueAccount.NUMFIELDS;
		public static final int FIELD_INVESTED	= 3 + ValueAccount.NUMFIELDS;
		public static final int FIELD_DIVIDEND	= 4 + ValueAccount.NUMFIELDS;
		public static final int FIELD_GAINS		= 5 + ValueAccount.NUMFIELDS;
		public static final int FIELD_PRICE		= 6 + ValueAccount.NUMFIELDS;
		public static final int FIELD_PROFIT	= 7 + ValueAccount.NUMFIELDS;
		public static final int NUMFIELDS	    = 8 + ValueAccount.NUMFIELDS;
		
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
				case FIELD_UNITS:		return "Units";
				case FIELD_PRICE:		return "Price";
				case FIELD_COST:		return "Cost";
				case FIELD_GAINED:		return "Gained";
				case FIELD_INVESTED:	return "Invested";
				case FIELD_DIVIDEND:	return "Dividend";
				case FIELD_GAINS:		return "Gains";
				case FIELD_PROFIT:		return "Profit";
				default:		  		return ValueAccount.fieldName(iField);
			}
		}
		
		/**
		 * Determine the field name in a non-static fashion 
		 */
		public String getFieldName(int iField) { return fieldName(iField); }
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_UNITS:		
					myString += Units.format(theUnits);
					break;
				case FIELD_COST:		
					myString += Money.format(theCost);
					break;
				case FIELD_GAINED:		
					myString += Money.format(theGained);
					break;
				case FIELD_INVESTED:		
					myString += Money.format(theInvested);
					break;
				case FIELD_GAINS:		
					myString += Money.format(theGains);
					break;
				case FIELD_DIVIDEND:		
					myString += Money.format(theDividend);
					break;
				case FIELD_PROFIT:		
					myString += Money.format(theProfit);
					break;
				case FIELD_PRICE:		
					myString += Price.format(thePrice);
					break;
				default:
					myString += super.formatField(iField, pValues);
					break;
			}
			return myString;
		}
		
		/**
		 * is the bucket active (i.e. should it be copied)
		 */
		protected boolean isActive() {
			/* Copy if the units is non-zero */
			return theUnits.isNonZero();
		}

		/**
		 * is the bucket relevant (i.e. should it be reported)
		 */
		protected boolean isRelevant() {
			/* Relevant if this value or the previous value is non-zero */
			return (theUnits.isNonZero() || 
					((getPrevUnits() != null) &&
					 (getPrevUnits().isNonZero())));
		}

		/**
		 * value the asset at a particular date
		 * @param pDate the date of valuation
		 */
		protected void valueAsset(Date pDate) {
			AcctPrice.List	myPrices = getData().getPrices();
			AcctPrice 		myActPrice;

			/* Obtain the appropriate price record */
			myActPrice = myPrices.getLatestPrice(getAccount(), pDate);
			
			/* If we found a price */
			if (myActPrice != null) {
				/* Store the price */
				thePrice = myActPrice.getPrice();
			}
			
			/* else (can happen at present until we lock it down TODO) */
			else thePrice = new Price(0);
			
			/* Calculate the value */
			setValue(theUnits.valueAtPrice(thePrice));
		}

		/**
		 * Calculate profit
		 */
		protected void calculateProfit() {
			/* Calculate the profit */
			theProfit = new Money(getValue());
			theProfit.subtractAmount(theCost);
			theProfit.addAmount(theGained);
		}

		/**
		 * Adjust account for debit
		 * @param pEvent the event causing the debit
		 */
		protected void adjustForDebit(Event pEvent) {
			/* Adjust for debit */
			if (pEvent.getUnits() != null)
				theUnits.subtractUnits(pEvent.getUnits());
		}
		
		/**
		 * Adjust account for credit
		 * @param pEvent the event causing the credit
		 */
		protected void adjustForCredit(Event pEvent) {
			/* Adjust for credit */
			if (pEvent.getUnits() != null)
				theUnits.addUnits(pEvent.getUnits());
		}
		/**
		 * Create a Save Point 
		 */
		protected void createSavePoint() {
			/* Create a save of the values */
			theSavePoint = new AssetAccount(this);
		}

		/**
		 * Restore a Save Point 
		 */
		protected void restoreSavePoint() {
			/* If we have a Save point */
			if (theSavePoint != null) {
				/* Restore original value */
				setValue(new Money(theSavePoint.getValue()));

				/* Initialise the Money values */
				theUnits		= new Units(theSavePoint.getUnits());
				theCost			= new Money(theSavePoint.getCost());
				theGained		= new Money(theSavePoint.getGained());
				theInvested		= new Money(theSavePoint.getInvested());
				theDividend		= new Money(theSavePoint.getDividend());
				theGains		= new Money(theSavePoint.getGains());
				
				/* Copy price if available */
				if (theSavePoint.getPrice() != null)
					thePrice	= new Price(theSavePoint.getPrice());
				
				/* Trim back the capital events */
				theEvents.purgeAfterDate(getDate());
			}
		}
	}
	
	/* The ExternalAccount Bucket class */
	public static class ExternalAccount extends ActDetail {
		/**
		 * The name of the object
		 */
		private static final String objName = "ExternalAccountBucket";

		/* Members */
		private Money 			theIncome 		= null;
		private Money 			theExpense		= null;
		private ExternalAccount	theSavePoint	= null;

		/* Override of getBase method */
		public ExternalAccount getBase() 	{ return (ExternalAccount)super.getBase(); }
		
		/* Access methods */
		public Money	getIncome() 		{ return theIncome; }
		public Money 	getExpense() 		{ return theExpense; }
		public Money	getPrevIncome() 	{ return (getBase() != null)
												? getBase().getIncome() : null; }
		public Money	getPrevExpense()	{ return (getBase() != null)
												? getBase().getExpense() : null; }

		/* Constructor */
		private ExternalAccount(BucketList	pList,
								Account		pAccount) {
			/* Call super-constructor */
			super(pList, 
				  BucketType.EXTERNALDETAIL,
				  pAccount);
			
			/* Initialise the money values */
			theIncome 	= new Money(0);
			theExpense 	= new Money(0);
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private ExternalAccount(BucketList		pList,
						   		ExternalAccount	pPrevious) {
			/* Call super-constructor */
			super(pList,
				  BucketType.EXTERNALDETAIL,
				  pPrevious.getAccount());

			/* Initialise the Money values */
			theIncome	= new Money(0);
			theExpense	= new Money(0);
			
			/* Add the link to the previous item */
			setBase(new ExternalAccount(pPrevious));
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private ExternalAccount(ExternalAccount	pPrevious) {
			/* Call super-constructor */
			super((BucketList)pPrevious.getList(),
				  BucketType.EXTERNALDETAIL,
				  pPrevious.getAccount());

			/* Initialise the Money values */
			theIncome	= new Money(pPrevious.getIncome());
			theExpense	= new Money(pPrevious.getExpense());
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Field IDs */
		public static final int FIELD_INCOME 	= ActDetail.NUMFIELDS;
		public static final int FIELD_EXPENSE 	= 1 + ActDetail.NUMFIELDS;
		public static final int NUMFIELDS	    = 2 + ActDetail.NUMFIELDS;
		
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
				case FIELD_INCOME:		return "Income";
				case FIELD_EXPENSE:		return "Expense";
				default:		  		return ActDetail.fieldName(iField);
			}
		}
		
		/**
		 * Determine the field name in a non-static fashion 
		 */
		public String getFieldName(int iField) { return fieldName(iField); }
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_INCOME:		
					myString += Money.format(theIncome);
					break;
				case FIELD_EXPENSE:		
					myString += Money.format(theExpense);
					break;
				default:
					myString += super.formatField(iField, pValues);
					break;
			}
			return myString;
		}
		
		/**
		 * is the bucket active (i.e. should it be copied)
		 */
		protected boolean isActive() {
			/* Copy if the income or expense is non-zero */
			return (theIncome.isNonZero() || theExpense.isNonZero());
		}
		
		/**
		 * is the bucket relevant (i.e. should it be reported)
		 */
		protected boolean isRelevant() {
			/* Relevant if this value or the previous value is non-zero */
			return (theIncome.isNonZero() ||
					theExpense.isNonZero() ||
					((getPrevIncome() != null) &&
					 (getPrevIncome().isNonZero())) ||
					((getPrevExpense() != null) &&
					 (getPrevExpense().isNonZero())));
		}
		
		/**
		 * Adjust account for debit
		 * @param pEvent the event causing the debit
		 */
		protected void adjustForDebit(Event pEvent) {
			TransactionType myTransType = pEvent.getTransType();
			Money			myAmount	= pEvent.getAmount();
			Money			myTaxCred	= pEvent.getTaxCredit();

			/* If this is a recovered transaction */
			if (myTransType.isRecovered()) {
				/* This is a negative expense */
				theExpense.subtractAmount(myAmount);
			}
			
			/* else this is a standard income */
			else {
				/* Adjust for income */
				theIncome.addAmount(myAmount);
				
				/* If there is a TaxCredit */
				if (myTaxCred != null) {
					/* Adjust for Tax Credit */
					theIncome.addAmount(myTaxCred);					
				}
			}
		}
		
		/**
		 * Adjust account for credit
		 * @param pEvent the event causing the credit
		 */
		protected void adjustForCredit(Event pEvent) {
			/* Adjust for expense */
			theExpense.addAmount(pEvent.getAmount());
		}
		
		/**
		 * Adjust account for tax credit
		 * @param pEvent the event causing the tax credit
		 */
		protected void adjustForTaxCredit(Event pEvent) {
			/* Adjust for expense */
			theExpense.addAmount(pEvent.getTaxCredit());
		}
		
		/**
		 * Adjust account for taxable gain tax credit
		 * @param pEvent the event causing the tax credit
		 */
		protected void adjustForTaxGainTaxCredit(Event pEvent) {
			/* Adjust for expense */
			theIncome.addAmount(pEvent.getTaxCredit());
		}
		
		/**
		 * Create a Save Point 
		 */
		protected void createSavePoint() {
			/* Create a save of the values */
			theSavePoint = new ExternalAccount(this);
		}

		/**
		 * Restore a Save Point 
		 */
		protected void restoreSavePoint() {
			/* If we have a Save point */
			if (theSavePoint != null) {
				/* Restore original value */
				theIncome  = new Money(theSavePoint.getIncome());
				theExpense = new Money(theSavePoint.getExpense());
			}
		}
	}
	
	/* The AssetSummary Bucket class */
	public static class AssetSummary extends ActType {
		/**
		 * The name of the object
		 */
		private static final String objName = "AssetSummaryBucket";

		/* Members */
		private Money 		theValue 		= null;

		/* Override of getBase method */
		public AssetSummary getBase() 	{ return (AssetSummary)super.getBase(); }
		
		/* Access methods */
		public Money	getValue() 		{ return theValue; }
		public Money	getPrevValue()	{ return getBase().getValue(); }

		/* Constructor */
		private AssetSummary(BucketList		pList,
						 	 AccountType	pAccountType) {
			/* Call super-constructor */
			super(pList, pAccountType);
			
			/* Initialise the Money values */
			theValue 		= new Money(0);
			
			/* Create a new base for this total */
			setBase(new AssetSummary(this));
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private AssetSummary(AssetSummary	pMaster) {
			/* Call super-constructor */
			super((BucketList)pMaster.getList(),
				  pMaster.getAccountType());

			/* Initialise the Money values */
			theValue 		= new Money(0);
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Field IDs */
		public static final int FIELD_VALUE 	= ActType.NUMFIELDS;
		public static final int NUMFIELDS	    = 1 + ActType.NUMFIELDS;
		
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
				case FIELD_VALUE:		return "Value";
				default:		  		return ActType.fieldName(iField);
			}
		}
		
		/**
		 * Determine the field name in a non-static fashion 
		 */
		public String getFieldName(int iField) { return fieldName(iField); }
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_VALUE:		
					myString += Money.format(theValue);
					break;
				default:
					myString += super.formatField(iField, pValues);
					break;
			}
			return myString;
		}

		/**
		 * is the bucket active (i.e. should it be copied)
		 */
		protected boolean isActive() {	return false; }

		/**
		 * is the bucket relevant (i.e. should it be reported)
		 */
		protected boolean isRelevant() {	return true; }

		/**
		 * Add values to the summary value
		 * @param pBucket the bucket 
		 */
		protected void addValues(ValueAccount pBucket) {
			ValueAccount myPrevious = pBucket.getBase();
			
			/* the total */
			theValue.addAmount(pBucket.getValue());
			
			/* If there are previous totals and we have previous totals */
			if ((myPrevious != null) && (getBase() != null)) {
				getBase().addValues(myPrevious);
			}
		}
	}
	
	/* The AssetTotal Bucket class */
	public static class AssetTotal extends AnalysisBucket {
		/**
		 * The name of the object
		 */
		private static final String objName = "AssetTotalBucket";

		/* Members */
		private Money 		theValue 		= null;
		private Money 		theProfit 		= null;

		/* Override of getBase method */
		public AssetTotal	getBase() 	{ return (AssetTotal)super.getBase(); }
		
		/* Access methods */
		public Money	getValue() 		{ return theValue; }
		public Money	getProfit() 	{ return theProfit; }
		public Money	getPrevValue()	{ return getBase().getValue(); }

		/* Constructor */
		private AssetTotal(BucketList	pList) {
			/* Call super-constructor */
			super(pList, BucketType.ASSETTOTAL, 0);
			
			/* Initialise the Money values */
			theValue 		= new Money(0);
			
			/* Create a new base for this total */
			setBase(new AssetTotal(this));
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private AssetTotal(AssetTotal	pMaster) {
			/* Call super-constructor */
			super((BucketList)pMaster.getList(), 
				  BucketType.ASSETTOTAL,
				  0);

			/* Initialise the Money values */
			theValue 		= new Money(0);
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Field IDs */
		public static final int FIELD_VALUE 	= AnalysisBucket.NUMFIELDS;
		public static final int FIELD_PROFIT 	= 1 + AnalysisBucket.NUMFIELDS;
		public static final int NUMFIELDS	    = 2 + AnalysisBucket.NUMFIELDS;
		
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
				case FIELD_VALUE:		return "Value";
				case FIELD_PROFIT:		return "Profit";
				default:		  		return AnalysisBucket.fieldName(iField);
			}
		}
		
		/**
		 * Determine the field name in a non-static fashion 
		 */
		public String getFieldName(int iField) { return fieldName(iField); }
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_VALUE:		
					myString += Money.format(theValue);
					break;
				case FIELD_PROFIT:		
					myString += Money.format(theProfit);
					break;
				default:
					myString += super.formatField(iField, pValues);
					break;
			}
			return myString;
		}

		/**
		 * is the bucket active (i.e. should it be copied)
		 */
		protected boolean isActive() {	return false; }

		/**
		 * is the bucket relevant (i.e. should it be reported)
		 */
		protected boolean isRelevant() { return true; }

		/**
		 * Add values to the total value
		 * @param pBucket the bucket 
		 */
		protected void addValues(AssetSummary pBucket) {
			AssetSummary myPrevious = pBucket.getBase();
			
			/* the total */
			theValue.addAmount(pBucket.getValue());
			
			/* If there are previous totals and we have previous totals */
			if ((myPrevious != null) && (getBase() != null)) {
				getBase().addValues(myPrevious);
			}
		}

		/**
		 * Calculate profit
		 */
		protected void calculateProfit() {
			theProfit = new Money(theValue);
			if (getBase() != null)
				theProfit.subtractAmount(getPrevValue());
		}
	}
	
	/* The ExternalTotal Bucket class */
	public static class ExternalTotal extends AnalysisBucket {
		/**
		 * The name of the object
		 */
		private static final String objName = "ExternalTotalBucket";

		/* Members */
		private Money 		theIncome 		= null;
		private Money 		theExpense 		= null;
		private Money 		theProfit 		= null;

		/* Override of getBase method */
		public ExternalTotal	getBase() 	{ return (ExternalTotal)super.getBase(); }
		
		/* Access methods */
		public Money	getIncome()		{ return theIncome; }
		public Money	getExpense()	{ return theExpense; }
		public Money	getProfit() 	{ return theProfit; }
		public Money	getPrevIncome()	{ return getBase().getIncome(); }
		public Money	getPrevExpense(){ return getBase().getExpense(); }
		public Money	getPrevProfit() { return getBase().getProfit(); }

		/* Constructor */
		private ExternalTotal(BucketList	pList) {
			/* Call super-constructor */
			super(pList, 
				  BucketType.EXTERNALTOTAL,
				  0);
			
			/* Initialise the Money values */
			theIncome 		= new Money(0);
			theExpense 		= new Money(0);
			
			/* Create a new base for this total */
			setBase(new ExternalTotal(this));
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private ExternalTotal(ExternalTotal	pMaster) {
			/* Call super-constructor */
			super((BucketList)pMaster.getList(), 
				  BucketType.EXTERNALTOTAL,
				  0);

			/* Initialise the Money values */
			theIncome 		= new Money(0);
			theExpense 		= new Money(0);
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Field IDs */
		public static final int FIELD_INCOME 	= AnalysisBucket.NUMFIELDS;
		public static final int FIELD_EXPENSE 	= 1 + AnalysisBucket.NUMFIELDS;
		public static final int FIELD_PROFIT 	= 2 + AnalysisBucket.NUMFIELDS;
		public static final int NUMFIELDS	    = 3 + AnalysisBucket.NUMFIELDS;
		
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
				case FIELD_INCOME:		return "Income";
				case FIELD_EXPENSE:		return "Expense";
				case FIELD_PROFIT:		return "Profit";
				default:		  		return AnalysisBucket.fieldName(iField);
			}
		}
		
		/**
		 * Determine the field name in a non-static fashion 
		 */
		public String getFieldName(int iField) { return fieldName(iField); }
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_INCOME:		
					myString += Money.format(theIncome);
					break;
				case FIELD_EXPENSE:		
					myString += Money.format(theExpense);
					break;
				case FIELD_PROFIT:		
					myString += Money.format(theProfit);
					break;
				default:
					myString += super.formatField(iField, pValues);
					break;
			}
			return myString;
		}

		/**
		 * is the bucket active (i.e. should it be copied)
		 */
		protected boolean isActive() {	return false; }

		/**
		 * is the bucket relevant (i.e. should it be reported)
		 */
		protected boolean isRelevant() {	return true; }

		/**
		 * Add values to the total value
		 * @param pBucket the bucket 
		 */
		protected void addValues(ExternalAccount pBucket) {
			ExternalAccount myPrevious = pBucket.getBase();
			
			/* Add the values */
			theIncome.addAmount(pBucket.getIncome());
			theExpense.addAmount(pBucket.getExpense());
			
			/* If there are previous totals and we have previous totals */
			if ((myPrevious != null) && (getBase() != null)) {
				/* Add previous values */
				getBase().addValues(myPrevious);
			}
		}

		/**
		 * Calculate profit
		 */
		protected void calculateProfit() {
			theProfit = new Money(theIncome);
			theProfit.subtractAmount(theExpense);
			if (getBase() != null)
				getBase().calculateProfit();
		}
	}
	
	/* The MarketTotal Bucket class */
	public static class MarketTotal extends AnalysisBucket {
		/**
		 * The name of the object
		 */
		private static final String objName = "MarketTotalBucket";

		/* Members */
		private Money 		theCost 		= null;
		private Money 		theValue 		= null;
		private Money 		theGained 		= null;
		private Money 		theProfit 		= null;

		/* Override of getBase method */
		public MarketTotal	getBase() 	{ return (MarketTotal)super.getBase(); }
		
		/* Access methods */
		public Money	getCost()		{ return theCost; }
		public Money	getGained()		{ return theGained; }
		public Money	getValue()		{ return theValue; }
		public Money	getProfit() 	{ return theProfit; }

		/* Constructor */
		private MarketTotal(BucketList	pList) {
			/* Call super-constructor */
			super(pList, BucketType.MARKETTOTAL, 0);
			
			/* Initialise the Money values */
			theCost 		= new Money(0);
			theValue 		= new Money(0);
			theGained 		= new Money(0);
			theProfit 		= new Money(0);
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Field IDs */
		public static final int FIELD_COST 		= AnalysisBucket.NUMFIELDS;
		public static final int FIELD_VALUE 	= 1 + AnalysisBucket.NUMFIELDS;
		public static final int FIELD_GAINED 	= 2 + AnalysisBucket.NUMFIELDS;
		public static final int FIELD_PROFIT 	= 3 + AnalysisBucket.NUMFIELDS;
		public static final int NUMFIELDS	    = 4 + AnalysisBucket.NUMFIELDS;
		
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
				case FIELD_COST:		return "Cost";
				case FIELD_VALUE:		return "Value";
				case FIELD_GAINED:		return "Gained";
				case FIELD_PROFIT:		return "Profit";
				default:		  		return AnalysisBucket.fieldName(iField);
			}
		}
		
		/**
		 * Determine the field name in a non-static fashion 
		 */
		public String getFieldName(int iField) { return fieldName(iField); }
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_COST:		
					myString += Money.format(theCost);
					break;
				case FIELD_VALUE:		
					myString += Money.format(theValue);
					break;
				case FIELD_GAINED:		
					myString += Money.format(theGained);
					break;
				case FIELD_PROFIT:		
					myString += Money.format(theProfit);
					break;
				default:
					myString += super.formatField(iField, pValues);
					break;
			}
			return myString;
		}

		/**
		 * is the bucket active (i.e. should it be copied)
		 */
		protected boolean isActive() {	return false; }

		/**
		 * is the bucket relevant (i.e. should it be reported)
		 */
		protected boolean isRelevant() {	return true; }

		/**
		 * Add values to the total value
		 * @param pBucket the source bucket to add 
		 */
		protected void addValues(AssetAccount pBucket) {
			theCost.addAmount(pBucket.getCost());
			theGained.addAmount(pBucket.getGained());
			theProfit.addAmount(pBucket.getProfit());
			theValue.addAmount(pBucket.getValue());
		}
	}
	
	/* The Transaction Detail Bucket class */
	public static class TransDetail extends TransType {
		/**
		 * The name of the object
		 */
		private static final String objName = "TransactionDetailBucket";

		/* Members */
		private Money 		theAmount 		= null;
		private Money 		theTaxCredit	= null;

		/* Override of getBase method */
		public TransDetail getBase() 	{ return (TransDetail)super.getBase(); }
		
		/* Access methods */
		public Money	getAmount() 	{ return theAmount; }
		public Money	getTaxCredit() 	{ return theTaxCredit; }
		public Money	getPrevAmount() { return (getBase() != null)
											? getBase().getAmount() : null; }
		public Money	getPrevTax() 	{ return (getBase() != null)
											? getBase().getTaxCredit() : null; }

		/* Constructor */
		private TransDetail(BucketList		pList,
						    TransactionType	pTransType) {
			/* Call super-constructor */
			super(pList, pTransType);
			
			/* Initialise the Money values */
			theAmount 		= new Money(0);
			theTaxCredit	= new Money(0);
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private TransDetail(BucketList	pList,
						    TransDetail	pPrevious) {
			/* Call super-constructor */
			super(pList, pPrevious.getTransType());
			
			/* Initialise the Money values */
			theAmount 		= new Money(0);
			theTaxCredit	= new Money(0);
			
			/* Add the link to the previous item */
			setBase(new TransDetail(pPrevious));
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private TransDetail(TransDetail	pPrevious) {
			/* Call super-constructor */
			super((BucketList)pPrevious.getList(),
				  pPrevious.getTransType());

			/* Initialise the Money values */
			theAmount		= new Money(pPrevious.getAmount());
			theTaxCredit	= new Money(pPrevious.getTaxCredit());
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Field IDs */
		public static final int FIELD_AMOUNT 	= TransType.NUMFIELDS;
		public static final int FIELD_TAXCREDIT	= 1 + TransType.NUMFIELDS;
		public static final int NUMFIELDS	    = 2 + TransType.NUMFIELDS;
		
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
				case FIELD_AMOUNT:		return "Amount";
				case FIELD_TAXCREDIT:	return "TaxCredit";
				default:		  		return TransType.fieldName(iField);
			}
		}
		
		/**
		 * Determine the field name in a non-static fashion 
		 */
		public String getFieldName(int iField) { return fieldName(iField); }
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_AMOUNT:		
					myString += Money.format(theAmount);
					break;
				case FIELD_TAXCREDIT:		
					myString += Money.format(theTaxCredit);
					break;
				default:
					myString += super.formatField(iField, pValues);
					break;
			}
			return myString;
		}
		
		/**
		 * is the bucket active (i.e. should it be copied)
		 */
		protected boolean isActive() {
			/* Copy if the amount is non-zero */
			return theAmount.isNonZero();
		}

		/**
		 * is the bucket relevant (i.e. should it be reported)
		 */
		protected boolean isRelevant() {
			/* Relevant if this value or the previous value is non-zero */
			return (theAmount.isNonZero() || 
					((getPrevAmount() != null) &&
					 (getPrevAmount().isNonZero())));
		}
		
		
		/**
		 * Adjust account for transaction
		 * @param pEvent the source event
		 */
		protected void adjustAmount(Event pEvent) {
			/* Adjust for transaction */
			theAmount.addAmount(pEvent.getAmount());
			
			/* Adjust for tax credit */
			if (pEvent.getTaxCredit() != null) 
				theTaxCredit.addAmount(pEvent.getTaxCredit());
		}
		
		/**
		 * Adjust account for tax credit
		 * @param pEvent the source event
		 */
		protected void adjustForTaxCredit(Event pEvent) {
			/* Adjust for tax credit */
			theAmount.addAmount(pEvent.getTaxCredit());
		}
	}
	
	/* The Transaction Summary Bucket class */
	public static class TransSummary extends Tax {
		/**
		 * The name of the object
		 */
		private static final String objName = "TransactionSummaryBucket";

		/* Members */
		private Money 		theAmount 		= null;

		/* Override of getBase method */
		public TransSummary getBase() 	{ return (TransSummary)super.getBase(); }
		
		/* Access methods */
		public Money	getAmount() 	{ return theAmount; }
		public Money	getPrevAmount() { return getBase().getAmount(); }

		/* Constructor */
		private TransSummary(BucketList	pList,
						     TaxType	pTaxType) {
			/* Call super-constructor */
			super(pList, pTaxType);
			
			/* Initialise the Money values */
			theAmount 		= new Money(0);
			
			/* Create a new base for this total */
			setBase(new TransSummary(this));
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private TransSummary(TransSummary	pPrevious) {
			/* Call super-constructor */
			super((BucketList)pPrevious.getList(),
				  pPrevious.getTaxType());
			
			/* Initialise the Money values */
			theAmount 		= new Money(0);
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Field IDs */
		public static final int FIELD_AMOUNT 	= Tax.NUMFIELDS;
		public static final int NUMFIELDS	    = 1 + Tax.NUMFIELDS;
		
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
				case FIELD_AMOUNT:		return "Amount";
				default:		  		return Tax.fieldName(iField);
			}
		}
		
		/**
		 * Determine the field name in a non-static fashion 
		 */
		public String getFieldName(int iField) { return fieldName(iField); }
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_AMOUNT:		
					myString += Money.format(theAmount);
					break;
				default:
					myString += super.formatField(iField, pValues);
					break;
			}
			return myString;
		}

		/**
		 * is the bucket active (i.e. should it be copied)
		 */
		protected boolean isActive() {	return false; }

		/**
		 * is the bucket relevant (i.e. should it be reported)
		 */
		protected boolean isRelevant() {	return true; }

		/**
		 * Add values to the total value
		 * @param pBucket the bucket 
		 */
		protected void addValues(TransDetail pBucket) {
			TransDetail myPrevious = pBucket.getBase();
			
			/* Add the values */
			theAmount.addAmount(pBucket.getAmount());
			theAmount.addAmount(pBucket.getTaxCredit());
			
			/* If there are previous totals and we have previous totals */
			if ((myPrevious != null) && (getBase() != null)) {
				/* Add previous values */
				getBase().addValues(myPrevious);
			}
		}

		/**
		 * Subtract values from the total value
		 * @param pBucket the bucket 
		 */
		protected void subtractValues(TransDetail pBucket) {
			TransDetail myPrevious = pBucket.getBase();

			/* Add the values */
			theAmount.subtractAmount(pBucket.getAmount());
			
			/* If there are previous totals and we have previous totals */
			if ((myPrevious != null) && (getBase() != null)) {
				/* Add previous values */
				getBase().subtractValues(myPrevious);
			}
		}
	}
	
	/* The Transaction Total Bucket class */
	public static class TransTotal extends Tax {
		/**
		 * The name of the object
		 */
		private static final String objName = "TransactionTotalBucket";

		/* Members */
		private Money 		theAmount 		= null;

		/* Override of getBase method */
		public TransTotal getBase() 	{ return (TransTotal)super.getBase(); }
		
		/* Access methods */
		public Money	getAmount() 	{ return theAmount; }
		public Money	getPrevAmount() { return getBase().getAmount(); }

		/* Constructor */
		private TransTotal(BucketList	pList,
						   TaxType		pTaxType) {
			/* Call super-constructor */
			super(pList, pTaxType);
			
			/* Initialise the Money values */
			theAmount 		= new Money(0);
			
			/* Create a new base for this total */
			setBase(new TransTotal(this));
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private TransTotal(TransTotal	pMaster) {
			/* Call super-constructor */
			super((BucketList)pMaster.getList(), 
				  pMaster.getTaxType());
			
			/* Initialise the Money values */
			theAmount 		= new Money(0);
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Field IDs */
		public static final int FIELD_AMOUNT 	= Tax.NUMFIELDS;
		public static final int NUMFIELDS	    = 1 + Tax.NUMFIELDS;
		
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
				case FIELD_AMOUNT:		return "Amount";
				default:		  		return Tax.fieldName(iField);
			}
		}
		
		/**
		 * Determine the field name in a non-static fashion 
		 */
		public String getFieldName(int iField) { return fieldName(iField); }
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_AMOUNT:		
					myString += Money.format(theAmount);
					break;
				default:
					myString += super.formatField(iField, pValues);
					break;
			}
			return myString;
		}

		/**
		 * is the bucket active (i.e. should it be copied)
		 */
		protected boolean isActive() {	return false; }

		/**
		 * is the bucket relevant (i.e. should it be reported)
		 */
		protected boolean isRelevant() { return true; }

		/**
		 * Add values to the total value
		 * @param pBucket the bucket 
		 */
		protected void addValues(TransSummary pBucket) {
			TransSummary myPrevious = pBucket.getBase();
			
			/* Add the values */
			theAmount.addAmount(pBucket.getAmount());
			
			/* If there are previous totals and we have previous totals */
			if ((myPrevious != null) && (getBase() != null)) {
				/* Add previous values */
				getBase().addValues(myPrevious);
			}
		}

		/**
		 * Subtract values from the total value
		 * @param pBucket the bucket 
		 */
		protected void subtractValues(TransSummary pBucket) {
			TransSummary myPrevious = pBucket.getBase();
			
			/* Add the values */
			theAmount.subtractAmount(pBucket.getAmount());
			
			/* If there are previous totals and we have previous totals */
			if ((myPrevious != null) && (getBase() != null)) {
				/* Add previous values */
				getBase().subtractValues(myPrevious);
			}
		}
	}
	
	/* The Taxation Detail Bucket class */
	public static class TaxDetail extends Tax {
		/**
		 * The name of the object
		 */
		private static final String objName = "TaxationDetailBucket";

		/* Members */
		private Money 		theAmount 	= null;
		private Money 		theTaxation	= null;
		private Rate 		theRate		= null;
		private TaxDetail	theParent	= null;

		/* Override of getBase method */
		public TaxDetail 	getBase()	{ return (TaxDetail)super.getBase(); }
		
		/* Access methods */
		public Money		getAmount() 	{ return theAmount; }
		public Money		getTaxation() 	{ return theTaxation; }
		public Rate			getRate() 		{ return theRate; }
		public TaxDetail	getParent() 	{ return theParent; }
		public Money		getPrevAmount() { return (getBase() != null)
												? getBase().getAmount() : null; }
		public Money		getPrevTax() 	{ return (getBase() != null)
												? getBase().getTaxation() : null; }
		public Rate			getPrevRate() 	{ return (getBase() != null)
												? getBase().getRate() : null; }

		/* Constructor */
		private TaxDetail(BucketList	pList,
					   	  TaxType		pTaxType) {
			/* Call super-constructor */
			super(pList, pTaxType);
			
			/* Add the link to the previous item */
			setBase(new TaxDetail(this));
			
			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Constructor */
		private TaxDetail(TaxDetail	pMaster) {
			/* Call super-constructor */
			super((BucketList)pMaster.getList(),
				  pMaster.getTaxType());

			/* Set status */
			setState(DataState.CLEAN);
		}
		
		/* Field IDs */
		public static final int FIELD_AMOUNT 	= Tax.NUMFIELDS;
		public static final int FIELD_TAXATION	= 1 + Tax.NUMFIELDS;
		public static final int FIELD_RATE		= 2 + Tax.NUMFIELDS;
		public static final int NUMFIELDS	    = 3 + Tax.NUMFIELDS;
		
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }

		/**
		 * Obtain the number of fields for an item
		 * @return the number of fields
		 */
		public int	numFields() { return NUMFIELDS; }
		
		/**
		 * Determine the field name for a particular field
		 * @return the field name
		 */
		public static String	fieldName(int iField) {
			switch (iField) {
				case FIELD_AMOUNT:		return "Amount";
				case FIELD_TAXATION:	return "Taxation";
				case FIELD_RATE:		return "Rate";
				default:		  		return Tax.fieldName(iField);
			}
		}
		
		/**
		 * Determine the field name in a non-static fashion 
		 */
		public String getFieldName(int iField) { return fieldName(iField); }
		
		/**
		 * Format the value of a particular field as a table row
		 * @param iField the field number
		 * @param pValues the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, HistoryValues<AnalysisBucket> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_AMOUNT:		
					myString += Money.format(theAmount);
					break;
				case FIELD_TAXATION:		
					myString += Money.format(theTaxation);
					break;
				case FIELD_RATE:		
					myString += Rate.format(theRate);
					break;
				default:
					myString += super.formatField(iField, pValues);
					break;
			}
			return myString;
		}

		/**
		 * is the bucket active (i.e. should it be copied)
		 */
		protected boolean isActive() {	return false; }

		/**
		 * is the bucket relevant (i.e. should it be reported)
		 */
		protected boolean isRelevant() {	
			/* Relevant if this value or the previous value is non-zero */
			return (theAmount.isNonZero() ||
					theTaxation.isNonZero() ||
					((getPrevAmount() != null) &&
					 (getPrevAmount().isNonZero())) ||
					((getPrevTax() != null) &&
					 (getPrevTax().isNonZero())));
		}
		
		/**
		 * Set a taxation amount and calculate the tax on it
		 * 
		 * @param  	pAmount 		Amount to set
		 * @return the taxation on this bucket
		 */
		protected Money setAmount(Money pAmount) {
			/* Set the value */
			theAmount    = new Money(pAmount);
            
            /* Calculate the tax if we have a rate*/
			theTaxation = (theRate != null) ? theAmount.valueAtRate(theRate)
											: new Money(0);
			
			/* Return the taxation amount */
			return theTaxation;
		}
		
		/**
		 * Set explicit taxation value 
		 * 
		 * @param  	pAmount 		Amount to set
		 */
		protected void setTaxation(Money pAmount) {
			/* Set the value */
			theTaxation = new Money(pAmount);
		}		
		/**
		 * Set parent bucket for reporting purposes
		 * @param  	pParent the parent bucket
		 */
		protected void setParent(TaxDetail pParent) {
			/* Set the value */
			theParent = pParent;
		}
		
		/**
		 * Set a tax rate
		 * 
		 * @param  pRate 	 Amount to set
		 */
		protected void setRate(Rate pRate) {
			/* Set the value */
			theRate    = pRate;
		}
	}
	
	/**
	 *  Bucket Types
	 */
	public static enum BucketType {
		/* Enum values */
		MONEYDETAIL,
		ASSETDETAIL,
		DEBTDETAIL,
		EXTERNALDETAIL,
		ASSETSUMMARY,
		ASSETTOTAL,
		MARKETTOTAL,
		EXTERNALTOTAL,
		TRANSDETAIL,
		TRANSSUMMARY,
		TRANSTOTAL,
		TAXDETAIL,
		TAXSUMMARY,
		TAXTOTAL;
		
		/**
		 * Get the Id shift for this BucketType
		 * @return the id shift
		 */
		private int getIdShift() {
			switch (this) {
				case MONEYDETAIL: 	
				case DEBTDETAIL: 	
				case EXTERNALDETAIL:
				case ASSETDETAIL: 	return 1000; /* Account IDs */
				case ASSETSUMMARY: 	return 100;	 /* AccountType IDs */
				case TRANSDETAIL: 	return 200;	 /* TransactionType IDs */
				case TRANSSUMMARY: 	
				case TRANSTOTAL: 	
				case TAXDETAIL: 	
				case TAXSUMMARY: 	
				case TAXTOTAL: 		return 300;	 /* TaxType IDs */
				case ASSETTOTAL: 	return 1;
				case MARKETTOTAL: 	return 2;
				case EXTERNALTOTAL:	return 3;
				default:			return 0;
			}
		}

		/**
		 * Get the BucketType for this Account
		 * @param pTaxType the tax type
		 * @return the id shift
		 */
		private static BucketType getActBucketType(Account pAccount) {
			/* If this is a external/benefit */
			if (pAccount.isExternal() || pAccount.isBenefit())
				return EXTERNALDETAIL;
			else if (pAccount.isMoney())
				return MONEYDETAIL;
			else if (pAccount.isPriced())
				return ASSETDETAIL;
			else
				return DEBTDETAIL;
		}

		/**
		 * Get the BucketType for this TaxType
		 * @param pTaxType the tax type
		 * @return the id shift
		 */
		private static BucketType getTaxBucketType(TaxType pTaxType) {
			TaxBucket	myBucket = pTaxType.getTaxClass().getClassBucket();
			switch (myBucket) {
				case TRANSTOTAL: 	return TRANSTOTAL;
				case TAXDETAIL: 	return TAXDETAIL;
				case TAXSUMM: 		return TAXSUMMARY;
				case TAXTOTAL: 		return TAXTOTAL;
				default: 			return TRANSSUMMARY;
			}
		}
	}
	
	/* Analysis state */
	protected enum AnalysisState {
		RAW,
		VALUED,
		TOTALLED,
		TAXED;
	}
}
