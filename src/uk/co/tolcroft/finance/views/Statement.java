package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.Analysis.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.Decimal.*;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.help.DebugDetail;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugObject;
import uk.co.tolcroft.models.help.DebugManager.DebugEntry;

public class Statement implements DebugObject {
	/* Members */
	private View      		theView      	= null;
	private Account      	theAccount      = null;
	private ActDetail		theBucket		= null;
	private DateDay.Range   theRange        = null;
	private Money    		theStartBalance = null;
	private Money    		theEndBalance   = null;
	private Units    		theStartUnits   = null;
	private Units    		theEndUnits     = null;
	private EventAnalysis	theAnalysis		= null;
	private List            theLines        = null;

	/* Access methods */
	public Account       	getAccount()      { return theAccount; }
	public DateDay.Range    getDateRange()    { return theRange; }
	public Money     		getStartBalance() { return theStartBalance; }
	public Money     		getEndBalance()   { return theEndBalance; }
	public Units     		getStartUnits()   { return theStartUnits; }
	public Units     		getEndUnits()     { return theEndUnits; }
	public AccountType 		getActType()      { return theAccount.getActType(); }
	public List             getLines()        { return theLines; }
	public Line extractItemAt(long uIndex) {
		return (Line)theLines.get((int)uIndex); }
 	
 	/* Constructor */
	public Statement(View			pView,
					 Account 		pAccount,
			         DateDay.Range 	pRange) throws ModelException {
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
	public void resetBalances() throws ModelException {
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
		myString.append(DateDay.Range.format(theRange)); 
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
	public static class List extends Event.List {
		private Statement theStatement = null;

		/* Access functions */
		private Statement	getStatement()	{ return theStatement; }
		private Account 	getAccount() 	{ return theStatement.getAccount(); }
		
		/* Constructors */
		public List(Statement pStatement) { 
			/* Declare the data and set the style */
			super(pStatement.theView.getData());
			setStyle(ListStyle.EDIT);
			theStatement = pStatement;
			setBase(theStatement.theView.getData().getEvents());
		}
		
		/* Obtain extract lists. */
		public List getUpdateList() { return null; }
		public List getEditList() 	{ return null; }
		public List getShallowCopy() { return null; }
		public List getDeepCopy(DataSet<?> pData) { return null; }
		public List getDifferences(List pOld) { return null; }

		/* Is this list locked */
		public boolean isLocked() { return theStatement.theAccount.isLocked(); }
		
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
			myLine.setDate(theStatement.theRange.getStart());
			
			/* Add line to list */
			add(myLine);
			return myLine;
		}
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return "StatementLine"; }
	}
			
	public static class Line extends Event {
		private Money        		theBalance   = null;
		private Units				theBalUnits  = null;
		private Statement			theStatement = null;

		/* Access methods */
		public Values      		getValues()       	{ return (Values)super.getValues(); }
		public Account       	getPartner()   		{ return getValues().getPartner(); }
		public Account       	getAccount()   		{ return getValues().getAccount(); }
		public boolean          isCredit()     		{ return getValues().isCredit(); }
		public Money       		getBalance()   		{ return theBalance; }
		public Units       		getBalanceUnits() 	{ return theBalUnits; }
		
		private ActDetail		getBucket()			{ return theStatement.theBucket; }
		
		/* Linking methods */
		public Event getBase() { return (Event)super.getBase(); }

		/* Field IDs */
		public static final int FIELD_ISCREDIT = Event.NUMFIELDS;
		public static final int NUMFIELDS	   = Event.NUMFIELDS+1;
		
		/* Virtual Field IDs */
		public static final int VFIELD_ACCOUNT = NUMFIELDS;
		public static final int VFIELD_PARTNER = NUMFIELDS+1;

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
				case VFIELD_PARTNER: 	return "Partner";
				case VFIELD_ACCOUNT: 	return "Account";
				case FIELD_ISCREDIT:	return "IsCredit";
				default:		  		return Event.fieldName(iField);
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
		public String formatField(DebugDetail pDetail, int iField, HistoryValues<Event> pValues) {
			String 	myString = "";
			Values 	myValues = (Values)pValues;
			switch (iField) {
				case FIELD_ISCREDIT: 
					myString +=	(myValues.isCredit() ? "true" : "false");
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
		protected HistoryValues<Event> getNewValues() { return new Values(); }
		
		/**
	 	* Construct a copy of a Line
	 	* @param pLine The Line
	 	*/
		protected Line(Statement.List pList, Line pLine) {
			/* Set standard values */
			super(pList);
			theStatement = pList.getStatement();
			Values myValues = getValues();
			myValues.copyFrom(pLine.getValues());
			pList.setNewId(this);
		}

		/* Standard constructor for a newly inserted line */
		public Line(Statement.List  pList) {
			super(pList);
			theStatement = pList.getStatement();
			Values myValues = getValues();
			myValues.setAccount(pList.getAccount());
		}

		/* Standard constructor */
		public Line(Statement.List  pList,
				    Event   		pEvent) {
			/* Make this an element */
			super(pList, pEvent);
			theStatement = pList.getStatement();
			getValues().determineCredit(pList.getAccount());
			setBase(pEvent);
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
		 * Compare the line
		 */
		public boolean equals(Object that) { return (this == that); }
		
		/**
		 * Set a new partner 
		 * @param pPartner the new partner 
		 */
		public void setPartner(Account pPartner) {
			getValues().setPartner(pPartner);
		}
		
		/**
		 * Set a new isCredit indication 
		 * @param isCredit
		 */
		public void setIsCredit(boolean isCredit) {
			getValues().setIsCredit(isCredit);
		}
		
		/**
		 * Add an error for this item
		 * @param pError the error text
		 * @param iField the associated field
		 */
		protected void					addError(String pError, int iField) {
			/* Re-Map Credit/Debit field errors */
			switch (iField) {
				case FIELD_CREDIT: iField = isCredit() ? VFIELD_ACCOUNT : VFIELD_PARTNER; break;
				case FIELD_DEBIT:  iField = isCredit() ? VFIELD_PARTNER : VFIELD_ACCOUNT; break;
			}
			
			/* Call super class */
			super.addError(pError, iField);
		}
		
		/**
		 *  Values for a line 
		 */
		public class Values extends Event.Values {
			private boolean         isCredit     = false;
			
			/* Access methods */
			public Account          getPartner()   { return (isCredit) ? getDebit() : getCredit(); }
			public Account			getAccount()   { return (isCredit) ? getCredit() : getDebit(); }
			public boolean			isCredit() 	   { return isCredit; }
			
			public void setPartner(Account pPartner) {
				if (isCredit) 	setDebit(pPartner); 
				else 			setCredit(pPartner); }
			public void setAccount(Account pAccount) {
				if (isCredit) 	setCredit(pAccount); 
				else 			setDebit(pAccount); }
			public void setIsCredit(boolean isCredit) {
				/* If we are changing values */
				if (isCredit != this.isCredit) {
					/* Swap credit/debit values */
					Account myTemp = getCredit();
					setCredit(getDebit());
					setDebit(myTemp);
				}
				/* Store value */
				this.isCredit = isCredit; }
			
			/* Determine credit setting */
			private void determineCredit(Account pAccount) {
				if (Account.differs(getDebit(), pAccount).isDifferent())
					isCredit = true; }

			/* Constructor */
			public Values() {}
			public Values(Values 		pValues) { copyFrom(pValues); }
			public Values(Event.Values 	pValues) { copyFrom(pValues); }
			
			/* Check whether this object is equal to that passed */
			public Difference histEquals(HistoryValues<Event> pCompare) {
				/* Make sure that the object is the same class */
				if (pCompare.getClass() != this.getClass()) return Difference.Different;
				
				/* Cast correctly */
				Values myValues = (Values)pCompare;

				/* Handle boolean values */
				if (isCredit != myValues.isCredit)
					return Difference.Different;
				
				/* Determine underlying differences */
				Difference myDifference = super.histEquals(pCompare);
				
				/* Return Differences */
				return myDifference;
			}
			
			/* Copy values */
			public HistoryValues<Event> copySelf() {
				return new Values(this);
			}
			public void    copyFrom(HistoryValues<?> pSource) {
				/* Handle a Line Values */
				if (pSource instanceof Values) {
					Values myValues = (Values)pSource;
					super.copyFrom(myValues);
					isCredit     = myValues.isCredit();
				}
				
				/* Handle an Event Values */
				else if (pSource instanceof Event.Values) {
					Event.Values myValues = (Event.Values)pSource;
					super.copyFrom(myValues);
				}
			}
			
			public Difference	fieldChanged(int fieldNo, HistoryValues<Event> pOriginal) {
				Values		pValues = (Values)pOriginal;
				Difference	bResult = Difference.Identical;
				switch (fieldNo) {
					case FIELD_ISCREDIT:
						bResult = (isCredit != pValues.isCredit) ? Difference.Different 
																 : Difference.Identical;
						break;
					default:
						bResult = super.fieldChanged(fieldNo, pValues);
						break;
				}
				return bResult;
			}
		}
	}	
}
