package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.data.EncryptedItem.EncryptedList;
import uk.co.tolcroft.finance.views.Analysis.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Number.*;

public class Statement implements htmlDumpable {
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
		theAnalysis = new EventAnalysis(theView.getDebugMgr(),
										theView.getData(), 
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
		Event.List  myBase;
		DataSet		myData;
		
		/* Access base details */
		myData	= theView.getData();
		myBase  = myData.getEvents();
		
		/* Apply the changes from this list */
		myBase.prepareChanges(theLines);
	}
	
	/** 
	 * Commit/RollBack changes in a statement back into the underlying finance objects
	 * @param bCommit <code>true/false</code>
	 */
	protected void commitChanges(boolean bCommit) {
		Event.List  myBase;
		DataSet		myData;
		
		/* Access base details */
		myData	= theView.getData();
		myBase  = myData.getEvents();
		
		/* Commit /RollBack the changes */
		if (bCommit)	myBase.commitChanges(theLines);
		else			myBase.rollBackChanges(theLines);
	}
	
	/**
	 * The toHTMLString method just maps to that of the lines 
	 */
	public StringBuilder toHTMLString() { return theLines.toHTMLString(); }		

	/* The List class */
	public class List extends EncryptedList<Line> {
		private Statement theStatement = null;
		
		/* Constructors */
		public List(Statement pStatement) { 
			super(Line.class, theView.getData(), ListStyle.EDIT);
			theStatement = pStatement;
		}
		
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
		public Line addNewItem(DataItem<?> pElement) {
			Line myLine = new Line(this, (Line)pElement);
			add(myLine);
			return myLine;
		}
		
		/**
		 * Add a new item to the edit list
		 * @param isCredit - is this a credit item
		 * @return the newly added item
		 */
		public Line addNewItem(boolean isCredit) {
			Line myLine = new Line(this, isCredit);
			add(myLine);
			return myLine;
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
			
	public static class Line extends EncryptedItem<Line>  {
		private Money        		theBalance   = null;
		private Units				theBalUnits  = null;
		private boolean             isCredit     = false;
		private Statement			theStatement = null;

		/* Access methods */
		public Account       	getAccount()   		{ return theStatement.theAccount; }
		public Values      		getObj()       		{ return (Values)super.getObj(); }
		public Date        		getDate()      		{ return getObj().getDate(); }
		public String           getDesc()      		{ return getPairValue(getObj().getDesc()); }
		public Units       		getUnits()     		{ return getPairValue(getObj().getUnits()); }
		public Money       		getAmount()    		{ return getPairValue(getObj().getAmount()); }
		public Account       	getPartner()   		{ return getObj().getPartner(); }
		public Dilution   		getDilution() 		{ return getPairValue(getObj().getDilution()); }
		public Money   			getTaxCredit() 		{ return getPairValue(getObj().getTaxCredit()); }
		public Integer   		getYears() 			{ return getObj().getYears(); }
		public TransactionType	getTransType() 		{ return getObj().getTransType(); }
		public Money       		getBalance()   		{ return theBalance; }
		public Units       		getBalanceUnits() 	{ return theBalUnits; }
		public boolean          isCredit()     		{ return isCredit; }
		
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
		 * @param iField the field number
		 * @param pObj the values to use
		 * @return the formatted field
		 */
		public String formatField(int iField, histObject pObj) {
			String 		myString = "";
			Values 	myObj 	 = (Values)pObj;
			switch (iField) {
				case FIELD_ACCOUNT:	
					myString += Account.format(getAccount()); 
					break;
				case FIELD_DATE:	
					myString += Date.format(myObj.getDate()); 
					break;
				case FIELD_DESC:	
					myString += myObj.getDescValue(); 
					break;
				case FIELD_TRNTYP: 	
					myString += TransactionType.format(myObj.getTransType());	
					break;
				case FIELD_PARTNER:	
					myString += Account.format(myObj.getPartner()); 
					break;
				case FIELD_AMOUNT: 	
					myString += Money.format(myObj.getAmountValue());	
					break;
				case FIELD_UNITS: 	
					myString += Units.format(myObj.getUnitsValue());	
					break;
				case FIELD_CREDIT: 
					myString +=	(isCredit() ? "true" : "false");
					break;
				case FIELD_TAXCREDIT: 	
					myString += Money.format(myObj.getTaxCredValue());	
					break;
				case FIELD_YEARS:	
					myString += myObj.getYears(); 
					break;
				case FIELD_DILUTION:	
					myString += Dilution.format(myObj.getDilutionValue()); 
					break;
				default: 		
					myString += super.formatField(iField, pObj); 
					break;
			}
			return myString;
		}
								
		/**
	 	* Construct a copy of a Line
	 	* @param pLine The Line
	 	*/
		protected Line(List pList, Line pLine) {
			/* Set standard values */
			super(pList, 0);
			theStatement = pList.theStatement;
			Values myObj = new Values(pLine.getObj());
			setObj(myObj);
			pList.setNewId(this);
		}

		/* Standard constructor for a newly inserted line */
		public Line(List           pList, 
				    boolean        isCredit) {
			super(pList, 0);
			theStatement = pList.theStatement;
			Values myObj = new Values();
			setObj(myObj);
			this.isCredit = isCredit;
			pList.setNewId(this);				
		}

		/* Standard constructor */
		public Line(List        pList,
				    Event   	pEvent,
					Account 	pAccount) {
			/* Make this an element */
			super(pList, pEvent.getId());
			theStatement = pList.theStatement;
			Values 			myObj 	= new Values();
			Event.Values	myBase 	= pEvent.getObj();
			
			setObj(myObj);
			myObj.setDate(pEvent.getDate());
			myObj.setDesc(new StringPair(myBase.getDesc()));
			myObj.setAmount(new MoneyPair(myBase.getAmount()));
			if (myBase.getUnits() != null)
				myObj.setUnits(new UnitsPair(myBase.getUnits()));
			myObj.setTransType(pEvent.getTransType());
			if (myBase.getDilution() != null)
				myObj.setDilution(new DilutionPair(myBase.getDilution()));
			if (myBase.getTaxCredit() != null)
				myObj.setTaxCredit(new MoneyPair(myBase.getTaxCredit()));
			myObj.setYears(pEvent.getYears());
			setBase(pEvent);
			pList.setNewId(this);				

			/* If the account is credited */
			if (pAccount.compareTo(pEvent.getCredit()) == 0) {
				myObj.setPartner(pEvent.getDebit());
				isCredit   = true;
			}
			
			/* If the Account is debited */
			else if (pAccount.compareTo(pEvent.getDebit()) == 0) {
				myObj.setPartner(pEvent.getCredit());
				isCredit   = false;
			}
		}
					
		/**
		 * Validate the line
		 */
		public void validate() { validate(null); }
		public void validate(Event.List pList) {
			Event        						myEvent;
			Event.validationCtl.errorElement 	myError;
			int          						iField;
			DataSet		 						myData;
		
			/* Access DataSet */
			myData = getView().getData();
			
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
			DataSet		 myData;
		
			/* Access DataSet */
			myData = getView().getData();
			
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
			if (pDesc != null) getObj().setDesc(new StringPair(pDesc));
			else 			   getObj().setDesc(null);
		}
		
		/**
		 * Set a new amount 
		 * 
		 * @param pAmount the amount 
		 */
		public void setAmount(Money pAmount) throws Exception {
			if (pAmount != null) getObj().setAmount(new MoneyPair(pAmount));
			else 				 getObj().setAmount(null);
		}
		
		/**
		 * Set a new units 
		 * 
		 * @param pUnits the units 
		 */
		public void setUnits(Units pUnits) throws Exception {
			if (pUnits != null) getObj().setUnits(new UnitsPair(pUnits));
			else 				getObj().setUnits(null);
		}
		
		/**
		 * Set a new dilution
		 * 
		 * @param pDilution the dilution 
		 */
		public void setDilution(Dilution pDilution) throws Exception {
			if (pDilution != null) getObj().setDilution(new DilutionPair(pDilution));
			else 				   getObj().setDilution(null);
		}
		
		/**
		 * Set a new tax credit
		 * 
		 * @param pTaxCredit the tax credit 
		 */
		public void setTaxCredit(Money pTaxCredit) throws Exception {
			if (pTaxCredit != null) getObj().setTaxCredit(new MoneyPair(pTaxCredit));
			else 				    getObj().setTaxCredit(null);
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
				if (differs(theDesc,      					pValues.theDesc))      return false;
				if (differs(theAmount,    					pValues.theAmount))    return false;
				if (differs(theUnits,     					pValues.theUnits))     return false;
				if (Account.differs(thePartner,   			pValues.thePartner))   return false;
				if (differs(theDilution,					pValues.theDilution))  return false;
				if (differs(theTaxCredit, 					pValues.theTaxCredit)) return false;
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
				}
				return bResult;
			}

			/**
			 * Ensure encryption after security change
			 */
			protected void applySecurity() throws Exception {
				/* Apply the encryption */
				/* Apply the encryption */
				theDesc.encryptPair();
				theAmount.encryptPair();
				if (theUnits     != null) theUnits.encryptPair();
				if (theTaxCredit != null) theTaxCredit.encryptPair();
				if (theDilution  != null) theDilution.encryptPair();
			}		
		}
	}	
}
