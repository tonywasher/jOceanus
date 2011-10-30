package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.data.ControlKey;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.EncryptedItem;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.help.DebugDetail;

public class ViewPrice extends EncryptedItem<ViewPrice> {
	/**
	 * The name of the object
	 */
	private static final String objName = "ViewPrice";

	/* Local values */
	private Account	theAccount	= null;
	private boolean	hasDilution	= false;
	
	/* Access methods */
	public  Values  			getValues()    		{ return (Values)super.getValues(); }
	public  Price 				getPrice()  		{ return getValues().getPriceValue(); }
	public  Dilution			getDilution()  		{ return getValues().getDilution(); }
	public  DilutedPrice 		getDilutedPrice()  	{ return getValues().getDilutedPrice(); }
	public  Date  				getDate()			{ return getValues().getDate(); }
	public  Account				getAccount()		{ return theAccount; }
	public  void	setAccount(Account pAccount)	{ theAccount = pAccount; }
	
	/* Linking methods */
	public AcctPrice     getBase() { return (AcctPrice)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_ACCOUNT  		= EncryptedItem.NUMFIELDS;
	public static final int FIELD_DATE     		= EncryptedItem.NUMFIELDS+1;
	public static final int FIELD_PRICE    		= EncryptedItem.NUMFIELDS+2;
	public static final int FIELD_DILUTION 		= EncryptedItem.NUMFIELDS+3;
	public static final int FIELD_DILUTEDPRICE 	= EncryptedItem.NUMFIELDS+4;
	public static final int NUMFIELDS	   		= EncryptedItem.NUMFIELDS+5;

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
			case FIELD_ACCOUNT:			return "Account";
			case FIELD_DATE:			return "Date";
			case FIELD_PRICE:			return "Price";
			case FIELD_DILUTION:		return "Dilution";
			case FIELD_DILUTEDPRICE:	return "DilutedPrice";
			default:		  			return EncryptedItem.fieldName(iField);
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
	public String formatField(DebugDetail pDetail, int iField, HistoryValues<ViewPrice> pValues) {
		String 	myString = "";
		Values 	myValues = (Values)pValues;
		switch (iField) {
			case FIELD_ACCOUNT:
				myString += Account.format(getAccount()); 
				myString = pDetail.addDebugLink(getAccount(), myString);
				break;
			case FIELD_DATE:	
				myString += Date.format(getDate()); 
				break;
			case FIELD_PRICE:	
				myString += Price.format(myValues.getPriceValue()); 
				break;
			case FIELD_DILUTION:	
				myString += Dilution.format(getDilution()); 
				break;
			case FIELD_DILUTEDPRICE:	
				myString += DilutedPrice.format(myValues.getDilutedPrice()); 
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
	protected HistoryValues<ViewPrice> getNewValues() { return new Values(); }
	
	/**
 	* Construct a copy of a Price
 	* 
 	* @param pPrice The Price 
 	*/
	protected ViewPrice(List pList, AcctPrice pPrice) {
		/* Set standard values */
		super(pList, pPrice.getId());
		
		/* Set standard values */
		setAccount(pPrice.getAccount());
		hasDilution = pList.hasDilutions();

		/* Create the history object */
		Values myValues = getValues();
		myValues.copyFrom(pPrice.getValues());
		
		/* Finish off */
		setBase(pPrice);
		setState(DataState.CLEAN);
	}

	/* Standard constructor for a newly inserted price */
	private ViewPrice(List pList) {
		super(pList, 0);
		setControlKey(pList.getControlKey());
		setAccount(pList.theAccount);
		hasDilution = pList.hasDilutions();
		setState(DataState.NEW);
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
		ViewPrice myPrice = (ViewPrice)pThat;
		
		/* Check for equality */
		if (getId() != myPrice.getId()) return false;
		if (Date.differs(getDate(),     myPrice.getDate()).isDifferent()) 	return false;
		if (Price.differs(getPrice(),   myPrice.getPrice()).isDifferent()) 	return false;
		return true;
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
		ViewPrice myThat = (ViewPrice)pThat;

		/* If the date differs */
		if (this.getDate() != myThat.getDate()) {
			/* Handle null dates */
			if (this.getDate()   == null) return 1;
			if (myThat.getDate() == null) return -1;
			
			/* Compare the dates */
			iDiff = getDate().compareTo(myThat.getDate());
			if (iDiff != 0) return iDiff;
		}
		
		/* Compare the IDs */
		iDiff =(int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}
	
	/**
	 * Validate the price
	 * 
	 */
	public void validate() {
		Date 		myDate = getDate();
		List 		myList = (List)getList();
		FinanceData	mySet  = myList.theData;
			
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
		/* Store date */
		getValues().setDate((pDate == null) ? null : new Date(pDate));
		getValues().calculateDiluted();
	}

	/**
	 * Price List
	 */
	public static class List  extends EncryptedList<List, ViewPrice> {
		/* Members */
		private Account				theAccount		= null;
		private FinanceData			theData			= null;
		private DilutionEvent.List	theDilutions 	= null;
		private boolean				hasDilutions	= false;
		
		/* Access methods */
		public 	FinanceData			getData()		{ return theData; }
		private	DilutionEvent.List	getDilutions()	{ return theDilutions; }
		public	boolean				hasDilutions()	{ return hasDilutions; }

		/**
		 * Construct an edit extract of a Price list
		 * 
		 * @param pView      The master view
		 * @param pAccount	 The account to extract rates for 
		 */
		public List(View				pView,
					Account 			pAccount) {
			/* Make this list the correct style */
			super(List.class, ViewPrice.class, pView.getData(), ListStyle.EDIT);
			theData = pView.getData();

			/* Local variables */
			AcctPrice.List				myPrices;
			AcctPrice 					myCurr;
			ViewPrice 					myItem;
			AcctPrice.List.ListIterator myIterator;

			/* Skip to alias if required */
			if ((pAccount != null) && (pAccount.getAlias() != null))
				pAccount = pAccount.getAlias();
			
			/* Access the base prices */
			myPrices = theData.getPrices();
			setBase(myPrices);
			
			/* Store the account */
			theAccount = pAccount;

			/* Store dilution list and record whether we have dilutions */
			theDilutions = pView.getDilutions();
			hasDilutions = theDilutions.hasDilution(pAccount);
			
			/* Access the list iterator */
			myIterator = myPrices.listIterator(true);
			
			/* Loop through the list */
			while ((myCurr = myIterator.next()) != null) {
				/* Check the account */
				int myResult = pAccount.compareTo(myCurr.getAccount());
				
				/* Handle differing accounts */
				if (myResult ==  1) continue;
				if (myResult == -1) break;
				
				/* Copy the item */
				myItem = new ViewPrice(this, myCurr);
				add(myItem);
			}
		}

		/* Obtain extract lists. */
		public List getUpdateList() { return null; }
		public List getEditList() 	{ return null; }
		public List getShallowCopy() { return null; }
		public List getDeepCopy(DataSet<?> pData) { return null; }
		public List getDifferences(List pOld) { return null; }

		/* Is this list locked */
		public boolean isLocked() { return ((theAccount != null) && (theAccount.isLocked())); }

		/**
		 * Add a new item (never used)
		 */
		public ViewPrice addNewItem(DataItem<?> pElement) {
			return null;}
		
		/**
		 * Add a new item to the edit list
		 * @return the newly added item
		 */
		public ViewPrice addNewItem() {
			ViewPrice myPrice = new ViewPrice(this);
			myPrice.setAccount(theAccount);
			add(myPrice);
			return myPrice;
		}

		/**
		 * 	Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }

		/**
		 * Count the instances of a date
		 * 
		 * @param pDate the date
		 * @return The Item if present (or null)
		 */
		protected int countInstances(Date 		pDate,
									 Account	pAccount) {
			ListIterator 	myIterator;
			ViewPrice  		myCurr;
			int      		iDiff;
			int      		iCount = 0;

			/* Access the list iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				iDiff = pDate.compareTo(myCurr.getDate());
				if (iDiff == 0) iDiff = pAccount.compareTo(myCurr.getAccount());
				if (iDiff == 0) iCount++;
			}

			/* return to caller */
			return iCount;
		}

		/**
		 * Add additional fields to HTML String
		 * @param pBuffer the string buffer 
		 */
		public void addHTMLFields(StringBuilder pBuffer) {
			/* Start the Fields section */
			pBuffer.append("<tr><th rowspan=\"2\">Fields</th></tr>");

			/* Format the range */
			pBuffer.append("<tr><td>Account</td><td>"); 
			pBuffer.append(Account.format(theAccount)); 
			pBuffer.append("</td></tr>");
		}
	}
	
	/**
	 * Values for a view price 
	 */
	public class Values extends EncryptedValues {
		private Date       			theDate      	= null;
		private PricePair  			thePrice     	= null;
		private Dilution			theDilution		= null;
		private DilutedPrice		theDilutedPrice = null;
		
		/* Access methods */
		public Account				getAccount()    	{ return theAccount; }
		public Date       			getDate()      		{ return theDate; }
		public PricePair			getPrice()     		{ return thePrice; }
		public Dilution				getDilution()   	{ return theDilution; }
		public DilutedPrice			getDilutedPrice()   { return theDilutedPrice; }
		public Price  				getPriceValue() 	{ return getPairValue(thePrice); }
		public byte[]  				getPriceBytes() 	{ return getPairBytes(thePrice); }
		
		public void setDate(Date pDate) {
			theDate      	= pDate; }
		public void setPrice(PricePair pPrice) {
			thePrice     	= pPrice; }
		public void setDilution(Dilution pDilution) {
			theDilution  	= pDilution; }
		public void setDilutedPrice(DilutedPrice pDilutedPrice) {
			theDilutedPrice	= pDilutedPrice; }

		/* Constructor */
		public Values() {}
		public Values(Values 			pValues) { copyFrom(pValues); }
		public Values(AcctPrice.Values 	pValues) { copyFrom(pValues); }
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(HistoryValues<ViewPrice> pCompare) {
			Values myValues = (Values)pCompare;
			if (Date.differs(theDate,    				myValues.theDate).isDifferent())    		return false;
			if (differs(thePrice,   					myValues.thePrice).isDifferent())   		return false;
			if (Dilution.differs(theDilution, 			myValues.theDilution).isDifferent()) 		return false;
			if (DilutedPrice.differs(theDilutedPrice, 	myValues.theDilutedPrice).isDifferent())	return false;
			return true;
		}
		
		/* Copy values */
		public HistoryValues<ViewPrice> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			/* Handle a ViewPrice Values */
			if (pSource instanceof Values) {
				Values myValues = (Values)pSource;
				super.copyFrom(myValues);
				theDate         = myValues.getDate();
				thePrice        = myValues.getPrice();
				theDilution		= myValues.getDilution();
				theDilutedPrice	= myValues.getDilutedPrice();
			}
			
			/* Handle an AcctPrice Values */
			else if (pSource instanceof AcctPrice.Values) {
				AcctPrice.Values myValues = (AcctPrice.Values)pSource;
				super.copyFrom(myValues);
				theDate         = myValues.getDate();
				thePrice        = new PricePair(myValues.getPrice());
				calculateDiluted();
			}
		}
		
		public Difference	fieldChanged(int fieldNo, HistoryValues<ViewPrice> pOriginal) {
			Values 		pValues = (Values)pOriginal;
			Difference	bResult = Difference.Identical;
			switch (fieldNo) {
				case FIELD_DATE:
					bResult = (Date.differs(theDate,      		pValues.theDate));
					break;
				case FIELD_PRICE:
					bResult = (differs(thePrice,	    		pValues.thePrice));
					break;
				case FIELD_DILUTION:
					bResult = (Dilution.differs(theDilution,   	pValues.theDilution));
					break;
				case FIELD_DILUTEDPRICE:
					bResult = (DilutedPrice.differs(theDilutedPrice,	pValues.theDilutedPrice));
					break;
			}
			return bResult;
		}
		
		/**
		 * Calculate Diluted values 
		 */
		protected void calculateDiluted() {
			/* Access the list for the item */
			ViewPrice.List myList = (ViewPrice.List)getList();
				
			/* Set null default dilution */
			theDilution 	= null;
			theDilutedPrice = null;				

			/* If we have can look at dilutions */
			if ((hasDilution) && (theDate != null) && (thePrice != null)) {
				/* Determine the dilution factor for the date */
				Dilution myDilution = myList.getDilutions().getDilutionFactor(theAccount, theDate);
				
				/* If we have a dilution factor */
				if (myDilution != null) {
					/* Store dilution details */
					theDilution 	= myDilution;
					theDilutedPrice = thePrice.getValue().getDilutedPrice(myDilution);
				}
			}
		}

		/**
		 * Update encryption after security change
		 */
		protected void updateSecurity() throws Exception {}
		
		/**
		 * Appy encryption after non-encrypted load
		 */
		protected void applySecurity() throws Exception {}
		
		/**
		 * Adopt encryption from base
		 * @param pBase the Base values
		 */
		protected void adoptSecurity(ControlKey pControl, EncryptedValues pBase) throws Exception {}
	}		
}
