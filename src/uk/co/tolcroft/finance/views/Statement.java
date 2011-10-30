package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.Analysis.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.data.ControlKey;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.EncryptedItem;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.data.ValidationControl;
import uk.co.tolcroft.models.data.EncryptedItem.EncryptedList;
import uk.co.tolcroft.models.help.DebugDetail;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugObject;
import uk.co.tolcroft.models.help.DebugManager.DebugEntry;

public class Statement implements DebugObject {
	/* Members */
	private View      		theView      	= null;
	private Account      	theAccount      = null;
	private ActDetail		theBucket		= null;
	private Date.Range      theRange        = null;
	private Money    		theStartBalance = null;
	private Money    		theEndBalance   = null;
	private Units    		theStartUnits   = null;
	private Units    		theEndUnits     = null;
	private EventAnalysis	theAnalysis		= null;
	private List            theLines        = null;

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
			         Date.Range pRange) throws Exception {
		/* Create a copy of the account (plus surrounding list) */
		theView	   = pView;
		theAccount = pAccount;
		theRange   = pRange;
		theLines   = new List(this);
		
		/* Create an analysis for this statement */
		theAnalysis = new EventAnalysis(theView.getData(), 
										this);
	}

	/**
	 *  Set the ending balances for the statement
	 *  @param pAccount the Account Bucket
	 */
	protected void setStartBalances(ActDetail pAccount) {
		/* Record the bucket and access bucket type */
		theBucket 	= pAccount;
		
		/* If the bucket has a balance */
		if (hasBalance()) {
			/* Set starting balance */
			theStartBalance = new Money(((ValueAccount)theBucket).getValue());
		}
		
		/* If the bucket has units */
		if (hasUnits()) {
			/* Set starting units */
			theStartUnits = new Units(((AssetAccount)theBucket).getUnits());
		}
	}
	
	/**
	 *  Set the ending balances for the statement
	 */
	protected void setEndBalances() {
		/* If the bucket has a balance */
		if (hasBalance()) {
			/* Set ending balance */
			theEndBalance = new Money(((ValueAccount)theBucket).getValue());
		}
		
		/* If the bucket has units */
		if (hasUnits()) {
			/* Set ending units */
			theEndUnits = new Units(((AssetAccount)theBucket).getUnits());
		}
	}
	
	/**
	 *  Reset the balances
	 */
	public void resetBalances() throws Exception {
		/* Reset the balances */
		theAnalysis.resetStatementBalance(this);
	}
	
	/**
	 *  Does the statement have a money balance
	 *  @return TRUE/FALSE
	 */
	public boolean hasBalance()   { 
		return (theBucket.getBucketType() != BucketType.EXTERNALDETAIL);		
	}
	
	/**
	 *  Does the statement have units
	 *  @return TRUE/FALSE
	 */
	public boolean hasUnits()   { 
		return (theBucket.getBucketType() == BucketType.ASSETDETAIL);		
	}
	
	/** 
	 * Prepare changes in a statement back into the underlying finance objects
	 */
	protected void prepareChanges() {
		/* Prepare the changes from this list */
		theLines.prepareChanges();
	}
	
	/** 
	 * Commit/RollBack changes in a statement back into the underlying finance objects
	 * @param bCommit <code>true/false</code>
	 */
	protected void commitChanges(boolean bCommit) {
		/* Commit/RollBack the changes */
		if (bCommit)	theLines.commitChanges();
		else			theLines.rollBackChanges();
	}
	
	/**
	 * Create a string form of the object suitable for inclusion in an HTML document
	 * @param pDetail the debug detail
	 * @return the formatted string
	 */
	public StringBuilder buildDebugDetail(DebugDetail pDetail) { 
		/* Local variables */
		StringBuilder	myString = new StringBuilder(10000);

		/* Format the table headers */
		myString.append("<table border=\"1\" width=\"90%\" align=\"center\">");
		myString.append("<thead><th>Statement</th>");
		myString.append("<th>Property</th><th>Value</th></thead><tbody>");
			
		/* Start the Fields section */
		myString.append("<tr><th rowspan=\"7\">Fields</th></tr>");
			
		/* Format the balances */
		myString.append("<tr><td>Account</td><td>"); 
		myString.append(Account.format(theAccount)); 
		myString.append("</td></tr>");
		
		/* Format the range */
		myString.append("<tr><td>Range</td><td>"); 
		myString.append(Date.Range.format(theRange)); 
		myString.append("</td></tr>");
		
		/* Format the balances */
		myString.append("<tr><td>StartBalance</td><td>"); 
		myString.append(Money.format(theStartBalance)); 
		myString.append("</td></tr>"); 
		myString.append("<tr><td>EndBalance</td><td>"); 
		myString.append(Money.format(theEndBalance)); 
		myString.append("</td></tr>"); 
		myString.append("<tr><td>StartUnits</td><td>"); 
		myString.append(Units.format(theStartUnits)); 
		myString.append("</td></tr>"); 
		myString.append("<tr><td>EndUnits</td><td>"); 
		myString.append(Units.format(theEndUnits)); 
		myString.append("</td></tr>"); 

		/* Return the Data */
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
		pManager.addChildEntry(pParent, "Lines", theLines);

		/* Add analysis child */
		pManager.addChildEntry(pParent, "Analysis", theAnalysis);
	}
	
	/* The List class */
	public class List extends EncryptedList<List, Line> {
		private Statement theStatement = null;
		
		/* Constructors */
		public List(Statement pStatement) { 
			super(List.class, Line.class, theView.getData(), ListStyle.EDIT);
			theStatement = pStatement;
			setBase(theView.getData().getEvents());
		}
		
		/* Obtain extract lists. */
		public List getUpdateList() { return null; }
		public List getEditList() 	{ return null; }
		public List getShallowCopy() { return null; }
		public List getDeepCopy(DataSet<?> pData) { return null; }
		public List getDifferences(List pOld) { return null; }

		/* Is this list locked */
		public boolean isLocked() { return theAccount.isLocked(); }
		
		/**
		 * Add a new item (never used)
		 */
		public Line addNewItem(DataItem<?> pElement) {
			Line myLine = new Line(this, (Line)pElement);
			add(myLine);
			return myLine;
		}
		
		/**
		 * Add a new item to the edit list
		 * @return the newly added item
		 */
		public Line addNewItem() {
			/* Create the new line */
			Line myLine = new Line(this);

			/* Set the Date as the start of the range */
			myLine.setDate(theRange.getStart());
			
			/* Add zero amount */
			try { myLine.setAmount(new Money(0)); } catch (Throwable e) {}
			
			/* Add line to list */
			add(myLine);
			return myLine;
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "StatementLine"; }
		
		/** 
		 * Validate a statement
		 */
		public void validate() {
			Line        	myCurr;
			Event.List  	myList;
			ListIterator	myIterator;
			FinanceData		myData;
			
			/* Clear the errors */
			clearErrors();
			
			/* Create an event list */
			myData = theView.getData();
			myList = myData.getEvents().getViewList();
			
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
			
	public static class Line extends EncryptedItem<Line>  {
		private Money        		theBalance   = null;
		private Units				theBalUnits  = null;
		private Statement			theStatement = null;

		/* Access methods */
		public Account       	getAccount()   		{ return theStatement.theAccount; }
		public Values      		getValues()       	{ return (Values)super.getValues(); }
		public Date        		getDate()      		{ return getValues().getDate(); }
		public String           getDesc()      		{ return getPairValue(getValues().getDesc()); }
		public Units       		getUnits()     		{ return getPairValue(getValues().getUnits()); }
		public Money       		getAmount()    		{ return getPairValue(getValues().getAmount()); }
		public Account       	getPartner()   		{ return getValues().getPartner(); }
		public Dilution   		getDilution() 		{ return getPairValue(getValues().getDilution()); }
		public Money   			getTaxCredit() 		{ return getPairValue(getValues().getTaxCredit()); }
		public Integer   		getYears() 			{ return getValues().getYears(); }
		public TransactionType	getTransType() 		{ return getValues().getTransType(); }
		public Money       		getBalance()   		{ return theBalance; }
		public Units       		getBalanceUnits() 	{ return theBalUnits; }
		public boolean          isCredit()     		{ return getValues().isCredit(); }
		
		private View       		getView()   		{ return theStatement.theView; }
		private ActDetail		getBucket()			{ return theStatement.theBucket; }
		
		/* Linking methods */
		public Event getBase() { return (Event)super.getBase(); }

		/* Field IDs */
		public static final int FIELD_DATE     	= EncryptedItem.NUMFIELDS;
		public static final int FIELD_DESC     	= EncryptedItem.NUMFIELDS+1;
		public static final int FIELD_AMOUNT   	= EncryptedItem.NUMFIELDS+2;
		public static final int FIELD_TRNTYP   	= EncryptedItem.NUMFIELDS+3;
		public static final int FIELD_PARTNER  	= EncryptedItem.NUMFIELDS+4;
		public static final int FIELD_ACCOUNT  	= EncryptedItem.NUMFIELDS+5;
		public static final int FIELD_UNITS    	= EncryptedItem.NUMFIELDS+6;
		public static final int FIELD_CREDIT   	= EncryptedItem.NUMFIELDS+7;
		public static final int FIELD_DILUTION 	= EncryptedItem.NUMFIELDS+8;
		public static final int FIELD_TAXCREDIT	= EncryptedItem.NUMFIELDS+9;
		public static final int FIELD_YEARS   	= EncryptedItem.NUMFIELDS+10;
		public static final int NUMFIELDS	   	= EncryptedItem.NUMFIELDS+11;
		
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
		public static String	fieldName(int iField) {
			switch (iField) {
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
		public String formatField(DebugDetail pDetail, int iField, HistoryValues<Line> pValues) {
			String 	myString = "";
			Values 	myValues = (Values)pValues;
			switch (iField) {
				case FIELD_ACCOUNT:	
					myString += Account.format(getAccount()); 
					myString = pDetail.addDebugLink(getAccount(), myString);
					break;
				case FIELD_DATE:	
					myString += Date.format(myValues.getDate()); 
					break;
				case FIELD_DESC:	
					myString += myValues.getDescValue(); 
					break;
				case FIELD_TRNTYP: 	
					myString += TransactionType.format(myValues.getTransType());	
					myString = pDetail.addDebugLink(getTransType(), myString);
					break;
				case FIELD_PARTNER:	
					myString += Account.format(myValues.getPartner()); 
					myString = pDetail.addDebugLink(getPartner(), myString);
					break;
				case FIELD_AMOUNT: 	
					myString += Money.format(myValues.getAmountValue());	
					break;
				case FIELD_UNITS: 	
					myString += Units.format(myValues.getUnitsValue());	
					break;
				case FIELD_CREDIT: 
					myString +=	(isCredit() ? "true" : "false");
					break;
				case FIELD_TAXCREDIT: 	
					myString += Money.format(myValues.getTaxCredValue());	
					break;
				case FIELD_YEARS:	
					myString += myValues.getYears(); 
					break;
				case FIELD_DILUTION:	
					myString += Dilution.format(myValues.getDilutionValue()); 
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
		protected HistoryValues<Line> getNewValues() { return new Values(); }
		
		/**
	 	* Construct a copy of a Line
	 	* @param pLine The Line
	 	*/
		protected Line(List pList, Line pLine) {
			/* Set standard values */
			super(pList, 0);
			theStatement = pList.theStatement;
			Values myValues = getValues();
			myValues.copyFrom(pLine.getValues());
			pList.setNewId(this);
		}

		/* Standard constructor for a newly inserted line */
		public Line(List           pList) {
			super(pList, 0);
			theStatement = pList.theStatement;
			setControlKey(pList.getControlKey());
			pList.setNewId(this);				
		}

		/* Standard constructor */
		public Line(List        pList,
				    Event   	pEvent) {
			/* Make this an element */
			super(pList, pEvent.getId());
			theStatement = pList.theStatement;
			Values myValues = getValues();
			myValues.copyFrom(pEvent.getValues());
			setBase(pEvent);
			pList.setNewId(this);				
		}
					
		/**
		 * Validate the line
		 */
		public void validate() { validate(null); }
		public void validate(Event.List pList) {
			Event        							myEvent;
			ValidationControl<Event>.errorElement 	myError;
			int          							iField;
			FinanceData	 							myData;
		
			/* Access DataSet */
			myData = getView().getData();
			
			/* Create a new Event list */
			if (pList == null)
				pList = myData.getEvents().getViewList();
		
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
		 *  Set Balances
		 */
		protected void setBalances() {
			/* If the bucket has a balance */
			if (theStatement.hasBalance()) {
				/* Set current balance */
				theBalance = new Money(((ValueAccount)getBucket()).getValue());
			}
			
			/* If the bucket has units */
			if (theStatement.hasUnits()) {
				/* Set current units */
				theBalUnits = new Units(((AssetAccount)getBucket()).getUnits());
			}
		}
		
		/**
		 * Calculate the tax credit
		 * @return the tax credit
		 */
		public Money calculateTaxCredit() {
			Event        myEvent;
			Event.List   myList;
			FinanceData	 myData;
		
			/* Access DataSet */
			myData = getView().getData();
			
			/* Create a new Event list */
			myList = myData.getEvents().getViewList();
		
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
					 (getAccount().isClosed())));
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
			getValues().setPartner(pPartner);
		}
		
		/**
		 * Set a new transtype 
		 * 
		 * @param pTranType the transtype 
		 */
		public void setTransType(TransactionType pTranType) {
			getValues().setTransType(pTranType);
		}
		
		/**
		 * Set a new description 
		 * 
		 * @param pDesc the description 
		 */
		public void setDescription(String pDesc) throws Exception {
			if (pDesc != null) getValues().setDesc(new StringPair(pDesc));
			else 			   getValues().setDesc(null);
		}
		
		/**
		 * Set a new amount 
		 * 
		 * @param pAmount the amount 
		 */
		public void setAmount(Money pAmount) throws Exception {
			if (pAmount != null) getValues().setAmount(new MoneyPair(pAmount));
			else 				 getValues().setAmount(null);
		}
		
		/**
		 * Set a new units 
		 * 
		 * @param pUnits the units 
		 */
		public void setUnits(Units pUnits) throws Exception {
			if (pUnits != null) getValues().setUnits(new UnitsPair(pUnits));
			else 				getValues().setUnits(null);
		}
		
		/**
		 * Set a new dilution
		 * 
		 * @param pDilution the dilution 
		 */
		public void setDilution(Dilution pDilution) throws Exception {
			if (pDilution != null) getValues().setDilution(new DilutionPair(pDilution));
			else 				   getValues().setDilution(null);
		}
		
		/**
		 * Set a new tax credit
		 * 
		 * @param pTaxCredit the tax credit 
		 */
		public void setTaxCredit(Money pTaxCredit) throws Exception {
			if (pTaxCredit != null) getValues().setTaxCredit(new MoneyPair(pTaxCredit));
			else 				    getValues().setTaxCredit(null);
		}
		
		/**
		 * Set a new years 
		 * 
		 * @param pYears the years 
		 */
		public void setYears(Integer pYears) {
			getValues().setYears((pYears == null) ? null : new Integer(pYears));
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
		 * Set a new isCredit indication 
		 * 
		 * @param isCredit
		 */
		public void setIsCredit(boolean isCredit) {
			getValues().setIsCredit(isCredit);
		}
		
		/**
		 *  Values for a line 
		 */
		public class Values extends EncryptedValues {
			private Date       		theDate      = null;
			private StringPair      theDesc      = null;
			private MoneyPair  		theAmount    = null;
			private Account         thePartner   = null;
			private UnitsPair  		theUnits     = null;
			private DilutionPair	theDilution  = null;
			private MoneyPair		theTaxCredit = null;
			private Integer			theYears  	 = null;
			private TransactionType	theTransType = null;
			private boolean         isCredit     = false;
			
			/* Access methods */
			public Date       		getDate()      { return theDate; }
			public StringPair       getDesc()      { return theDesc; }
			public MoneyPair   		getAmount()    { return theAmount; }
			public Account          getPartner()   { return thePartner; }
			public UnitsPair  		getUnits()     { return theUnits; }
			public DilutionPair		getDilution()  { return theDilution; }
			public MoneyPair   		getTaxCredit() { return theTaxCredit; }
			public Integer     		getYears()     { return theYears; }
			public TransactionType	getTransType() { return theTransType; }
			public boolean			isCredit() 	   { return isCredit; }
			public Account       	getAccount()   { return theStatement.theAccount; }
			
			/* Encrypted value access */
			public  Money		getAmountValue()    { return getPairValue(getAmount()); }
			public  String  	getDescValue()      { return getPairValue(getDesc()); }
			public  Money		getTaxCredValue()   { return getPairValue(getTaxCredit()); }
			public  Units		getUnitsValue()     { return getPairValue(getUnits()); }
			public  Dilution	getDilutionValue()  { return getPairValue(getDilution()); }

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
			public void setDilution(DilutionPair pDilution) {
				theDilution  = pDilution; }
			public void setTaxCredit(MoneyPair pTaxCredit) {
				theTaxCredit = pTaxCredit; }
			public void setYears(Integer pYears) {
				theYears     = pYears; }
			public void setTransType(TransactionType pTransType) {
				theTransType = pTransType; }
			public void setIsCredit(boolean isCredit) {
				this.isCredit = isCredit; }

			/* Constructor */
			public Values() {}
			public Values(Values 		pValues) { copyFrom(pValues); }
			public Values(Event.Values 	pValues) { copyFrom(pValues); }
			
			/* Check whether this object is equal to that passed */
			public boolean histEquals(HistoryValues<Line> pCompare) {
				Values myValues = (Values)pCompare;
				if (!super.histEquals(pCompare))					  							  return false;
				if (isCredit != myValues.isCredit)      										  return false;
				if (Date.differs(theDate,      				myValues.theDate).isDifferent())      return false;
				if (differs(theDesc,      					myValues.theDesc).isDifferent())      return false;
				if (differs(theAmount,    					myValues.theAmount).isDifferent())    return false;
				if (differs(theUnits,     					myValues.theUnits).isDifferent())     return false;
				if (Account.differs(thePartner,   			myValues.thePartner).isDifferent())   return false;
				if (differs(theDilution,					myValues.theDilution).isDifferent())  return false;
				if (differs(theTaxCredit, 					myValues.theTaxCredit).isDifferent()) return false;
				if (Utils.differs(theYears,     			myValues.theYears).isDifferent())     return false;
				if (TransactionType.differs(theTransType,	myValues.theTransType).isDifferent()) return false;
				return true;
			}
			
			/* Copy values */
			public HistoryValues<Line> copySelf() {
				return new Values(this);
			}
			public void    copyFrom(HistoryValues<?> pSource) {
				/* Handle a Line Values */
				if (pSource instanceof Values) {
					Values myValues = (Values)pSource;
					super.copyFrom(myValues);
					isCredit     = myValues.isCredit();
					theDate      = myValues.getDate();
					theDesc      = myValues.getDesc();
					theAmount    = myValues.getAmount();
					thePartner   = myValues.getPartner();
					theUnits     = myValues.getUnits();
					theDilution  = myValues.getDilution();
					theTaxCredit = myValues.getTaxCredit();
					theYears     = myValues.getYears();
					theTransType = myValues.getTransType();
				}
				
				/* Handle a Line Values */
				else if (pSource instanceof Event.Values) {
					Event.Values myValues = (Event.Values)pSource;
					super.copyFrom(myValues);
					theDate 	 = myValues.getDate();
					theDesc 	 = new StringPair(myValues.getDesc());
					theAmount 	 = new MoneyPair(myValues.getAmount());
					theTransType = myValues.getTransType();
					theYears	 = myValues.getYears();
					if (myValues.getUnits() != null)
						theUnits     = new UnitsPair(myValues.getUnits());
					if (myValues.getDilution() != null)
						theDilution  = new DilutionPair(myValues.getDilution());
					if (myValues.getTaxCredit() != null)
						theTaxCredit = new MoneyPair(myValues.getTaxCredit());

					/* If the account is credited */
					if (getAccount().compareTo(myValues.getCredit()) == 0) {
						thePartner = myValues.getDebit();
						isCredit   = true;
					}
					
					/* If the Account is debited */
					else {
						thePartner = myValues.getCredit();
						isCredit   = false;
					}
				}
			}
			
			public Difference	fieldChanged(int fieldNo, HistoryValues<Line> pOriginal) {
				Values		pValues = (Values)pOriginal;
				Difference	bResult = Difference.Identical;
				switch (fieldNo) {
					case Statement.Line.FIELD_CREDIT:
						bResult = (isCredit != pValues.isCredit) ? Difference.Different 
																 : Difference.Identical;
						break;
					case Statement.Line.FIELD_DATE:
						bResult = (Date.differs(theDate,       			pValues.theDate));
						break;
					case Statement.Line.FIELD_DESC:
						bResult = (differs(theDesc,      				pValues.theDesc));
						break;
					case Statement.Line.FIELD_AMOUNT:
						bResult = (differs(theAmount,    				pValues.theAmount));
						break;
					case Statement.Line.FIELD_PARTNER:
						bResult = (Account.differs(thePartner,   		pValues.thePartner));
						break;
					case Statement.Line.FIELD_UNITS:
						bResult = (differs(theUnits,     				pValues.theUnits));
						break;
					case Statement.Line.FIELD_TRNTYP:
						bResult = (TransactionType.differs(theTransType, pValues.theTransType));
						break;
					case Statement.Line.FIELD_TAXCREDIT:
						bResult = (differs(theTaxCredit, 				pValues.theTaxCredit));
						break;
					case Statement.Line.FIELD_YEARS:
						bResult = (Utils.differs(theYears,     			pValues.theYears));
						break;
					case Statement.Line.FIELD_DILUTION:
						bResult = (differs(theDilution,  				pValues.theDilution));
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
