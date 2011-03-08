package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.EncryptedPair.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.DataItem.*;
import uk.co.tolcroft.models.DataItem.validationCtl.*;
import uk.co.tolcroft.models.Number.*;

public class Statement implements htmlDumpable {
	/* Members */
	private View      		theView      	= null;
	private Account      	theAccount      = null;
	private Date.Range      theRange        = null;
	private Money    		theStartBalance = null;
	private Money    		theEndBalance   = null;
	private Units    		theStartUnits   = null;
	private Units    		theEndUnits     = null;
	private AssetAnalysis	theAnalysis		= null;
	private List            theLines        = null;

	/* Encrypted access */
	private static String getStringPairValue(StringPair pPair) {
		return (pPair == null) ? null : pPair.getValue(); }
	private static Money getMoneyPairValue(MoneyPair pPair) {
		return (pPair == null) ? null : pPair.getValue(); }
	private static Units getUnitsPairValue(UnitsPair pPair) {
		return (pPair == null) ? null : pPair.getValue(); }
	
	/* Access methods */
	public Account       	getAccount()      { return theAccount; }
	public Date.Range       getDateRange()    { return theRange; }
	public Money     		getStartBalance() { return theStartBalance; }
	public Money     		getEndBalance()   { return theEndBalance; }
	public Units     		getStartUnits()   { return theStartUnits; }
	public Units     		getEndUnits()     { return theEndUnits; }
	public AccountType 		getActType()      { return theAccount.getActType(); }
	public List             getLines()        { return theLines; }
	public Line extractItemAt(long uIndex) {
		return theLines.get((int)uIndex); }
 	
 	/* Constructor */
	public Statement(View		pView,
					 Account 	pAccount,
			         Date.Range pRange) {
		DataSet							myData;
		Event              				myCurr;
		Event.List         				myBase;
		Line               				myLine;
		int                				myResult;
		DataList<Event>.ListIterator	myIterator;
		AssetAnalysis.Bucket			myBucket;
		AssetAnalysis.AssetBucket		myAssetBucket;

		/* Create a copy of the account (plus surrounding list) */
		theView	   = pView;
		theAccount = pAccount;
		theRange   = pRange;
		theLines   = new List();
		
		/* Create the list of statement lines */
		theLines        = new List();
		if (hasBalance()) theStartBalance = new Money(0);
		if (hasUnits())	  theStartUnits   = new Units(0);
		
		/* Access the underlying data and iterator */
		myData 		= theView.getData();
		myBase 		= myData.getEvents();
		myIterator 	= myBase.listIterator(true);

		/* Create an asset analysis for this account */
		theAnalysis = new AssetAnalysis(myData, pAccount);
		
		/* Loop through the Events extracting relevant elements */
		while ((myCurr = myIterator.next()) != null) {
			/* Check the range */
			myResult = pRange.compareTo(myCurr.getDate());
			
			/* Handle past limit */
			if (myResult == -1) break;
			
			/* Ignore items that do not relate to this account */
			if (!myCurr.relatesTo(pAccount)) continue;
			
			/* If we are too early for the statement */
			if (myResult == 1) {
				/* Process the event and continue */
				theAnalysis.processEvent(myCurr);
				continue;
			}
				
			/* Add a statement line to the statement */
			myLine = new Line(theLines, myCurr, theAccount);
			myLine.addToList();
		}
			 
		/* Access the account bucket */
		myBucket = theAnalysis.getAccountBucket();
		
		/* Access the starting balance */
		if (hasBalance()) theStartBalance = myBucket.getAmount();
		
		/* If this has units */
		if (hasUnits()) {
			/* Access as an asset bucket */
			myAssetBucket = (AssetAnalysis.AssetBucket) myBucket;
			theStartUnits = myAssetBucket.getUnits();
		}
		
		/* reset the balance */
		resetBalance();
	}
	
 	/* recalculate balance */
	public void resetBalance() {
		Line            			myLine;
		Event.List					myList;
		Event						myEvent;
		DataSet						myData;
		Money    					myInitAmount = null;
		Units						myInitUnits  = null;
		Money    					myAmount 	 = null;
		Units						myUnits   	 = null;
		AssetAnalysis.Bucket		myBucket;
		AssetAnalysis.AssetBucket	myAssetBucket = null;
		DataList<Line>.ListIterator	myIterator;

		/* Access the iterator */
		myIterator = theLines.listIterator();
		
		/* If we don't have balances just return */
		if (!hasBalance() && !hasUnits()) return;
		
		/* Create a new Event list */
		myData = theView.getData();
		myList = new Event.List(myData, ListStyle.VIEW);
	
		/* Access the bucket */
		myBucket = theAnalysis.getAccountBucket();
		
		/* If we have a balance */
		if (hasBalance()) {
			/* Access the amount and save its initial value */
			myAmount		= myBucket.getAmount();
			myInitAmount 	= new Money(myAmount);
		}
		
		/* If we have units */
		if (hasUnits()) {
			/* Access as an asset bucket */
			myAssetBucket = (AssetAnalysis.AssetBucket) myBucket;

			/* Access the units and save its initial value */
			myUnits 	= myAssetBucket.getUnits();
			myInitUnits	= new Units(myUnits);
		}
		
		/* Loop through the lines adjusting the balance */
		while ((myLine = myIterator.next()) != null) {
			/* Skip deleted lines */
			if (myLine.isDeleted()) continue;
			
			/* Create an event from this line */
			myEvent = new Event(myList, myLine);

			/* Process the event */
			theAnalysis.processEvent(myEvent);
			
			/* Take a copy of the balance if required */
			if (hasBalance()) 
				myLine.theBalance = new Money(myAmount);
			
			/* Take a copy of the units balance if required */
			if (hasUnits()) 
				myLine.theBalUnits = new Units(myUnits);
		}
	
		/* If we have balance */
		if (hasBalance()) {
			/* Set the end balance and restore the starting balance */
			theEndBalance = new Money(myAmount);
			myAmount.setZero();
			myAmount.addAmount(myInitAmount);
		}
		
		/* If we have units */
		if (hasUnits()) {
			/* Set the end balance and restore the starting balance */
			theEndUnits = new Units(myUnits);
			myUnits.setZero();
			myUnits.addUnits(myInitUnits);
		}
	}
	
	/* Does the statement have a money balance */
	public boolean hasBalance()   { 
		return ((!theAccount.isExternal()) &&
				(!theAccount.isPriced()) &&
				(!theAccount.isBenefit()));		
	}
	
	/* Does the statement have units */
	public boolean hasUnits()   { 
		return (theAccount.isPriced());		
	}
	
	/** 
	 * Apply changes in a statement back into the underlying finance objects
	 */
	public void applyChanges() {
		Event.List  myBase;
		DataSet		myData;
		
		/* Access base details */
		myData	= theView.getData();
		myBase  = myData.getEvents();
		
		/* Apply the changes from this list */
		myBase.applyChanges(theLines);
					
		/* analyse the data */
		theView.analyseData();
		
		/* Refresh windows */
		theView.refreshWindow();
	}
	
	/**
	 * The toHTMLString method just maps to that of the lines 
	 */
	public StringBuilder toHTMLString() { return theLines.toHTMLString(); }		

	/* The List class */
	public class List extends DataList<Line> {
		
		/* Constructors */
		public List() { super(ListStyle.EDIT, false); }
		
		/** 
	 	 * Clone a StatementLine list (never used)
	 	 * @return <code>null</code>
	 	 */
		protected List cloneIt() { return null; }
		
		/* Is this list locked */
		public boolean isLocked() { return theAccount.isLocked(); }
		
		/**
		 * Add a new item (never used)
		 */
		public DataItem addNewItem(DataItem pElement) {
			return null;}
		
		/**
		 * Add a new item to the edit list
		 * @param isCredit - ignored
		 */
		public void addNewItem(boolean        isCredit) {
			Line myLine = new Line(this, isCredit);
			myLine.addToList();
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "StatementLine"; }
		
		/**
		 * Add additional fields to HTML String
		 * @param pBuffer the string buffer 
		 */
		public void addHTMLFields(StringBuilder pBuffer) {
			/* Start the Fields section */
			pBuffer.append("<tr><th rowspan=\"7\">Fields</th></tr>");
				
			/* Format the balances */
			pBuffer.append("<tr><td>Account</td><td>"); 
			pBuffer.append(Account.format(theAccount)); 
			pBuffer.append("</td></tr>");
			
			/* Format the range */
			pBuffer.append("<tr><td>Range</td><td>"); 
			pBuffer.append(Date.Range.format(theRange)); 
			pBuffer.append("</td></tr>");
			
			/* Format the balances */
			pBuffer.append("<tr><td>StartBalance</td><td>"); 
			pBuffer.append(Money.format(theStartBalance)); 
			pBuffer.append("</td></tr>"); 
			pBuffer.append("<tr><td>EndBalance</td><td>"); 
			pBuffer.append(Money.format(theEndBalance)); 
			pBuffer.append("</td></tr>"); 
			pBuffer.append("<tr><td>StartUnits</td><td>"); 
			pBuffer.append(Units.format(theStartUnits)); 
			pBuffer.append("</td></tr>"); 
			pBuffer.append("<tr><td>EndUnits</td><td>"); 
			pBuffer.append(Units.format(theEndUnits)); 
			pBuffer.append("</td></tr>"); 
		}
		
		/** 
		 * Validate a statement
		 */
		public void validate() {
			Line        	myCurr;
			Event.List  	myList;
			ListIterator	myIterator;
			DataSet			myData;
			
			/* Clear the errors */
			clearErrors();
			
			/* Create an event list */
			myData = theView.getData();
			myList = new Event.List(myData, ListStyle.VIEW);
			
			/* Create the iterator */
			myIterator = listIterator();
			
			/* Loop through the lines */
			while ((myCurr = myIterator.next()) != null) {
				/* Validate the line */
				myCurr.validate(myList);
			}
			
			/* Determine the Edit State */
			findEditState();
		}
	}
			
	public class Line extends DataItem  {
		private Money        		theBalance   = null;
		private Units				theBalUnits  = null;
		private boolean             isCredit     = false;

		/* Access methods */
		public Account       	getAccount()   		{ return theAccount; }
		public Values      		getObj()       		{ return (Values)super.getObj(); }
		public Date        		getDate()      		{ return getObj().getDate(); }
		public String           getDesc()      		{ return getStringPairValue(getObj().getDesc()); }
		public Units       		getUnits()     		{ return getUnitsPairValue(getObj().getUnits()); }
		public Money       		getAmount()    		{ return getMoneyPairValue(getObj().getAmount()); }
		public Account       	getPartner()   		{ return getObj().getPartner(); }
		public Dilution   		getDilution() 		{ return getObj().getDilution(); }
		public Money   			getTaxCredit() 		{ return getMoneyPairValue(getObj().getTaxCredit()); }
		public Integer   		getYears() 			{ return getObj().getYears(); }
		public TransactionType	getTransType() 		{ return getObj().getTransType(); }
		public Money       		getBalance()   		{ return theBalance; }
		public Units       		getBalanceUnits() 	{ return theBalUnits; }
		public boolean          isCredit()     		{ return isCredit; }
		
		/* Linking methods */
		public Event getBase() { return (Event)super.getBase(); }

		/* Field IDs */
		public static final int FIELD_ID       	= 0;
		public static final int FIELD_DATE     	= 1;
		public static final int FIELD_DESC     	= 2;
		public static final int FIELD_AMOUNT   	= 3;
		public static final int FIELD_TRNTYP   	= 4;
		public static final int FIELD_PARTNER  	= 5;
		public static final int FIELD_ACCOUNT  	= 6;
		public static final int FIELD_UNITS    	= 7;
		public static final int FIELD_CREDIT   	= 8;
		public static final int FIELD_DILUTION 	= 9;
		public static final int FIELD_TAXCREDIT	= 10;
		public static final int FIELD_YEARS   	= 11;
		public static final int NUMFIELDS	   	= 13;
		
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "StatementLine"; }
		
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
				case FIELD_ID: 	  		return "ID";
				case FIELD_DATE: 		return "Date";
				case FIELD_DESC: 		return "Description";
				case FIELD_TRNTYP: 		return "TransactionType";
				case FIELD_PARTNER: 	return "Partner";
				case FIELD_ACCOUNT: 	return "Account";
				case FIELD_AMOUNT: 		return "Amount";
				case FIELD_UNITS: 		return "Units";
				case FIELD_CREDIT:		return "IsCredit";
				case FIELD_DILUTION:	return "Dilution";
				case FIELD_TAXCREDIT:	return "TaxCredit";
				case FIELD_YEARS:		return "Years";
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
			String 		myString = "";
			Values 	myObj 	 = (Values)pObj;
			switch (iField) {
				case FIELD_ID: 		
					myString += getId(); 
					break;
				case FIELD_ACCOUNT:	
					myString += Account.format(theAccount); 
					break;
				case FIELD_DATE:	
					myString += Date.format(myObj.getDate()); 
					break;
				case FIELD_DESC:	
					myString += getStringPairValue(myObj.getDesc()); 
					break;
				case FIELD_TRNTYP: 	
					myString += TransactionType.format(myObj.getTransType());	
					break;
				case FIELD_PARTNER:	
					myString += Account.format(myObj.getPartner()); 
					break;
				case FIELD_AMOUNT: 	
					myString += Money.format(getMoneyPairValue(myObj.getAmount()));	
					break;
				case FIELD_UNITS: 	
					myString += Units.format(getUnitsPairValue(myObj.getUnits()));	
					break;
				case FIELD_CREDIT: 
					myString +=	(isCredit() ? "true" : "false");
					break;
				case FIELD_TAXCREDIT: 	
					myString += Money.format(getMoneyPairValue(myObj.getAmount()));	
					break;
				case FIELD_YEARS:	
					myString += myObj.getYears(); 
					break;
				case FIELD_DILUTION:	
					myString += Dilution.format(myObj.getDilution()); 
					break;
			}
			return myString;
		}
								
		/* Standard constructor for a newly inserted pattern */
		public Line(List           pList, 
				    boolean        isCredit) {
			super(pList, 0);
			Values myObj = new Values();
			setObj(myObj);
			this.isCredit = isCredit;
			setState(DataState.NEW);
		}

		/* Standard constructor */
		public Line(List        pList,
				    Event   	pEvent,
					Account 	pAccount) {
			/* Make this an element */
			super(pList, 0);
			Values 			myObj 	= new Values();
			Event.Values	myBase 	= pEvent.getObj();
			
			setObj(myObj);
			myObj.setDate(pEvent.getDate());
			myObj.setDesc(myBase.getDesc());
			myObj.setAmount(myBase.getAmount());
			myObj.setUnits(myBase.getUnits());
			myObj.setTransType(pEvent.getTransType());
			myObj.setDilution(pEvent.getDilution());
			myObj.setTaxCredit(myBase.getTaxCredit());
			myObj.setYears(pEvent.getYears());
			setBase(pEvent);
			setState(DataState.CLEAN);

			/* If the account is debited */
			if (pAccount.compareTo(pEvent.getDebit()) == 0) {
				myObj.setPartner(pEvent.getCredit());
				isCredit   = false;
			}
			
			/* If the Account is Credited */
			else if (pAccount.compareTo(pEvent.getCredit()) == 0) {
				myObj.setPartner(pEvent.getDebit());
				isCredit   = true;
			}
		}
					
		/**
		 * Validate the line
		 */
		public void validate() { validate(null); }
		public void validate(Event.List pList) {
			Event        myEvent;
			errorElement myError;
			int          iField;
			DataSet		 myData;
		
			/* Access DataSet */
			myData = theView.getData();
			
			/* Create a new Event list */
			if (pList == null)
				pList = new Event.List(myData, ListStyle.VIEW);
		
			/* Create a new event based on this line */
			myEvent = new Event(pList, this);

			/* Validate it */
			myEvent.validate();
				
			/* Loop through the errors */
			for (myError = myEvent.getFirstError();
			     myError != null;
			     myError = myError.getNext()) {
				switch (myError.getField()) {
					case Event.FIELD_DATE: 
						iField = Line.FIELD_DATE; break;
					case Event.FIELD_DESC: 
						iField = Line.FIELD_DESC; break;
					case Event.FIELD_AMOUNT: 
						iField = Line.FIELD_AMOUNT; break;
					case Event.FIELD_TRNTYP: 
						iField = Line.FIELD_TRNTYP; break;
					case Event.FIELD_UNITS: 
						iField = Line.FIELD_UNITS; break;
					case Event.FIELD_DILUTION: 
						iField = Line.FIELD_DILUTION; break;
					case Event.FIELD_TAXCREDIT: 
						iField = Line.FIELD_TAXCREDIT; break;
					case Event.FIELD_YEARS: 
						iField = Line.FIELD_YEARS; break;
					case Event.FIELD_DEBIT: 
						iField = (isCredit())
									?  Line.FIELD_PARTNER
								    :  Line.FIELD_ACCOUNT; 
						break;
					case Event.FIELD_CREDIT: 
						iField = (isCredit())
									?  Line.FIELD_ACCOUNT
								    :  Line.FIELD_PARTNER; 
						break;
					default: iField = Line.FIELD_ACCOUNT;
						break;
				}	
					
				/* Add an error event to this object */
				addError(myError.getError(), iField);
			}
			
			/* Set validation flag */
			if (!hasErrors()) setValidEdit();
		}
		
		/**
		 * Calculate the tax credit
		 * @return the tax credit
		 */
		public Money calculateTaxCredit() {
			Event        myEvent;
			Event.List   myList;
			DataSet		 myData;
		
			/* Access DataSet */
			myData = theView.getData();
			
			/* Create a new Event list */
			myList = new Event.List(myData, ListStyle.VIEW);
		
			/* Create a new event based on this line */
			myEvent = new Event(myList, this);

			/* calculate the tax credit */
			return myEvent.calculateTaxCredit();
		}
		
		/**
		 * Compare the line
		 */
		public boolean equals(Object that) { return (this == that); }
		
		/**
		 * Determines whether a line is locked to updates
		 * 
		 * @return true/false 
		 */
		public boolean isLocked() {
			Account myPartner = getPartner();
			
			/* Check credit and debit accounts */
			return ((myPartner != null) &&
					((getPartner().isClosed()) ||
					 (theAccount.isClosed())));
		}
			
		/**
		 * Compare this line to another to establish sort order. 
		 * @param pThat The Line to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		public int compareTo(Object pThat) {
			int iDiff;
			
			/* Handle the trivial cases */
			if (this == pThat) return 0;
			if (pThat == null) return -1;
			
			/* Make sure that the object is Statement Line */
			if (pThat.getClass() != this.getClass()) return -1;
			
			/* Access the object as a Line */
			Line myThat = (Line)pThat;

			/* Compare the account */
			iDiff = getAccount().compareTo(myThat.getAccount());
			if (iDiff != 0) return iDiff;
			
			/* Compare the date */
			if (this.getDate() == null) return 1;
			if (myThat.getDate() == null) return -1;
			iDiff = getDate().compareTo(myThat.getDate());
			if (iDiff < 0) return -1;
			if (iDiff > 0) return 1;
			
			/* Compare the transaction type */
			if (this.getTransType() == null) return 1;
			if (myThat.getTransType() == null) return -1;
			iDiff = getTransType().compareTo(myThat.getTransType());
			if (iDiff < 0) return -1;
			if (iDiff > 0) return 1;
			
			/* Compare the description */
			if (this.getDesc() == null) return 1;
			if (myThat.getDesc() == null) return -1;
			iDiff = getDesc().compareTo(myThat.getDesc());
			if (iDiff < 0) return -1;
			if (iDiff > 0) return 1;
			
			/* Return equality */
			return 0;
		}

		/**
		 * Set a new partner 
		 * 
		 * @param pPartner the new partner 
		 */
		public void setPartner(Account pPartner) {
			getObj().setPartner(pPartner);
		}
		
		/**
		 * Set a new transtype 
		 * 
		 * @param pTranType the transtype 
		 */
		public void setTransType(TransactionType pTranType) {
			getObj().setTransType(pTranType);
		}
		
		/**
		 * Set a new description 
		 * 
		 * @param pDesc the description 
		 */
		public void setDescription(String pDesc) throws Exception {
			/* If we are setting a non null value */
			if (pDesc != null) {
				/* Create the Encrypted pair for the values */
				DataSet 		myData 	= theView.getData();
				EncryptedPair	myPairs = myData.getEncryptedPairs();
				StringPair		myPair	= myPairs.new StringPair(pDesc);
			
				/* Record the value and encrypt it*/
				getObj().setDesc(myPair);
				myPair.ensureEncryption();
			}
			
			/* Else we are setting a null value */
			else getObj().setDesc(null);
		}
		
		/**
		 * Set a new amount 
		 * 
		 * @param pAmount the amount 
		 */
		public void setAmount(Money pAmount) throws Exception {
			/* If we are setting a non null value */
			if (pAmount != null) {
				/* Create the Encrypted pair for the values */
				DataSet 		myData 	= theView.getData();
				EncryptedPair	myPairs = myData.getEncryptedPairs();
				MoneyPair		myPair	= myPairs.new MoneyPair(pAmount);
			
				/* Record the value and encrypt it*/
				getObj().setAmount(myPair);
				myPair.ensureEncryption();
			}
			
			/* Else we are setting a null value */
			else getObj().setAmount(null);
		}
		
		/**
		 * Set a new units 
		 * 
		 * @param pUnits the units 
		 */
		public void setUnits(Units pUnits) throws Exception {
			/* If we are setting a non null value */
			if (pUnits != null) {
				/* Create the Encrypted pair for the values */
				DataSet 		myData 	= theView.getData();
				EncryptedPair	myPairs = myData.getEncryptedPairs();
				UnitsPair		myPair	= myPairs.new UnitsPair(pUnits);
			
				/* Record the value and encrypt it*/
				getObj().setUnits(myPair);
				myPair.ensureEncryption();
			}
			
			/* Else we are setting a null value */
			else getObj().setUnits(null);
		}
		
		/**
		 * Set a new dilution
		 * 
		 * @param pDilution the dilution 
		 */
		public void setDilution(Dilution pDilution) {
			getObj().setDilution((pDilution == null) ? null : new Dilution(pDilution));
		}
		
		/**
		 * Set a new tax credit
		 * 
		 * @param pTaxCredit the tax credit 
		 */
		public void setTaxCredit(Money pTaxCredit) throws Exception {
			/* If we are setting a non null value */
			if (pTaxCredit != null) {
				/* Create the Encrypted pair for the values */
				DataSet 		myData 	= theView.getData();
				EncryptedPair	myPairs = myData.getEncryptedPairs();
				MoneyPair		myPair	= myPairs.new MoneyPair(pTaxCredit);
			
				/* Record the value and encrypt it*/
				getObj().setTaxCredit(myPair);
				myPair.ensureEncryption();
			}
			
			/* Else we are setting a null value */
			else getObj().setTaxCredit(null);
		}
		
		/**
		 * Set a new years 
		 * 
		 * @param pYears the years 
		 */
		public void setYears(Integer pYears) {
			getObj().setYears((pYears == null) ? null : new Integer(pYears));
		}
		
		/**
		 * Set a new date 
		 * 
		 * @param pDate the new date 
		 */
		public void setDate(Date pDate) {
			getObj().setDate((pDate == null) ? null : new Date(pDate));
		}
	}
	
	/**
	 *  Values for a line 
	 */
	public class Values implements histObject {
		private Date       		theDate      = null;
		private StringPair      theDesc      = null;
		private MoneyPair  		theAmount    = null;
		private Account         thePartner   = null;
		private UnitsPair  		theUnits     = null;
		private Dilution		theDilution  = null;
		private MoneyPair		theTaxCredit = null;
		private Integer			theYears  	 = null;
		private TransactionType	theTransType = null;
		
		/* Access methods */
		public Date       		getDate()      { return theDate; }
		public StringPair       getDesc()      { return theDesc; }
		public MoneyPair   		getAmount()    { return theAmount; }
		public Account          getPartner()   { return thePartner; }
		public UnitsPair  		getUnits()     { return theUnits; }
		public Dilution  		getDilution()  { return theDilution; }
		public MoneyPair   		getTaxCredit() { return theTaxCredit; }
		public Integer     		getYears()     { return theYears; }
		public TransactionType	getTransType() { return theTransType; }
		
		public void setDate(Date pDate) {
			theDate      = pDate; }
		public void setDesc(StringPair pDesc) {
			theDesc      = pDesc; }
		public void setAmount(MoneyPair pAmount) {
			theAmount    = pAmount; }
		public void setPartner(Account pPartner) {
			thePartner   = pPartner; }
		public void setUnits(UnitsPair pUnits) {
			theUnits     = pUnits; }
		public void setDilution(Dilution pDilution) {
			theDilution  = pDilution; }
		public void setTaxCredit(MoneyPair pTaxCredit) {
			theTaxCredit = pTaxCredit; }
		public void setYears(Integer pYears) {
			theYears     = pYears; }
		public void setTransType(TransactionType pTransType) {
			theTransType = pTransType; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) {
			theDate      = pValues.getDate();
			theDesc      = pValues.getDesc();
			theAmount    = pValues.getAmount();
			thePartner   = pValues.getPartner();
			theUnits     = pValues.getUnits();
			theDilution  = pValues.getDilution();
			theTaxCredit = pValues.getTaxCredit();
			theYears     = pValues.getYears();
			theTransType = pValues.getTransType();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (Date.differs(theDate,      				pValues.theDate))      return false;
			if (EncryptedPair.differs(theDesc,      	pValues.theDesc))      return false;
			if (EncryptedPair.differs(theAmount,    	pValues.theAmount))    return false;
			if (EncryptedPair.differs(theUnits,     	pValues.theUnits))     return false;
			if (Account.differs(thePartner,   			pValues.thePartner))   return false;
			if (Dilution.differs(theDilution,			pValues.theDilution))  return false;
			if (EncryptedPair.differs(theTaxCredit, 	pValues.theTaxCredit)) return false;
			if (Utils.differs(theYears,     			pValues.theYears))     return false;
			if (TransactionType.differs(theTransType,	pValues.theTransType)) return false;
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
			theDesc      = pValues.getDesc();
			theAmount    = pValues.getAmount();
			thePartner   = pValues.getPartner();
			theUnits     = pValues.getUnits();
			theDilution  = pValues.getDilution();
			theTaxCredit = pValues.getTaxCredit();
			theYears     = pValues.getYears();
			theTransType = pValues.getTransType();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values	pValues = (Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case Statement.Line.FIELD_DATE:
					bResult = (Date.differs(theDate,       			pValues.theDate));
					break;
				case Statement.Line.FIELD_DESC:
					bResult = (EncryptedPair.differs(theDesc,      	pValues.theDesc));
					break;
				case Statement.Line.FIELD_AMOUNT:
					bResult = (EncryptedPair.differs(theAmount,    	pValues.theAmount));
					break;
				case Statement.Line.FIELD_PARTNER:
					bResult = (Account.differs(thePartner,   		pValues.thePartner));
					break;
				case Statement.Line.FIELD_UNITS:
					bResult = (EncryptedPair.differs(theUnits,     	pValues.theUnits));
					break;
				case Statement.Line.FIELD_TRNTYP:
					bResult = (TransactionType.differs(theTransType, pValues.theTransType));
					break;
				case Statement.Line.FIELD_TAXCREDIT:
					bResult = (EncryptedPair.differs(theTaxCredit, 	pValues.theTaxCredit));
					break;
				case Statement.Line.FIELD_YEARS:
					bResult = (Utils.differs(theYears,     			pValues.theYears));
					break;
				case Statement.Line.FIELD_DILUTION:
					bResult = (Dilution.differs(theDilution,  		pValues.theDilution));
					break;
			}
			return bResult;
		}
	}
}
