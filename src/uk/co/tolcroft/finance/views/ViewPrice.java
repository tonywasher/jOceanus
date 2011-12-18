package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.Decimal.*;
import uk.co.tolcroft.models.data.ControlKey;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.help.DebugDetail;

public class ViewPrice extends AcctPrice {
	/**
	 * The name of the object
	 */
	private static final String objName = "ViewPrice";

	/* Access methods */
	public  Values  			getValues()    		{ return (Values)super.getValues(); }
	public  Dilution			getDilution()  		{ return getValues().getDilution(); }
	public  DilutedPrice 		getDilutedPrice()  	{ return getValues().getDilutedPrice(); }
	
	/* Linking methods */
	public AcctPrice     		getBase() 			{ return (AcctPrice)super.getBase(); }
	
	/* Calculate the diluted price */
	private void 				calculateDiluted()  { getValues().calculateDiluted(); }
	
	/* Field IDs */
	public static final int FIELD_DILUTION 		= AcctPrice.NUMFIELDS;
	public static final int FIELD_DILUTEDPRICE 	= AcctPrice.NUMFIELDS+1;
	public static final int NUMFIELDS	   		= AcctPrice.NUMFIELDS+2;

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
			case FIELD_DILUTION:		return "Dilution";
			case FIELD_DILUTEDPRICE:	return "DilutedPrice";
			default:		  			return AcctPrice.fieldName(iField);
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
	protected HistoryValues<AcctPrice> getNewValues() { return new Values(); }
	
	/**
 	 * Construct a copy of a Price
 	 * @param pPrice The Price 
 	 */
	protected ViewPrice(List pList, AcctPrice pPrice) {
		/* Set standard values */
		super(pList, pPrice);
		
		/* Calculate diluted values */
		calculateDiluted();
	}

	/* Standard constructor for a newly inserted price */
	private ViewPrice(List pList) {
		super(pList);
		getValues().setAccount(pList.theAccount);
	}

	/**
	 * Set a new price 
	 * @param pPrice the price 
	 */
	public void setPrice(Price pPrice) throws ModelException {
		super.setPrice(pPrice);
		calculateDiluted();
	}

	/**
	 * Set a new date 
	 * @param pDate the new date 
	 */
	public void setDate(DateDay pDate) {
		/* Store date */
		super.setDate(pDate);
		calculateDiluted();
	}

	/**
	 * Price List
	 */
	public static class List  extends AcctPrice.List {
		/* Members */
		private Account				theAccount		= null;
		private DilutionEvent.List	theDilutions 	= null;
		private boolean				hasDilutions	= false;
		
		/* Access methods */
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
			/* Declare the data and set the style */
			super(pView.getData());
			setStyle(ListStyle.EDIT);
			
			/* Local variables */
			AcctPrice.List				myPrices;
			AcctPrice 					myCurr;
			ViewPrice 					myItem;
			AcctPrice.List.ListIterator myIterator;

			/* Skip to alias if required */
			if ((pAccount != null) && (pAccount.getAlias() != null))
				pAccount = pAccount.getAlias();
			
			/* Access the base prices */
			myPrices = getData().getPrices();
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
				
				/* Skip different accounts */
				if (myResult != 0) continue;
				
				/* Copy the item */
				myItem = new ViewPrice(this, myCurr);
				add(myItem);
			}
		}

		/* Disable extract lists. */
		public List getUpdateList() { return null; }
		public List getEditList() 	{ return null; }
		public List getShallowCopy() { return null; }
		public List getDeepCopy(DataSet<?> pData) { return null; }
		public List getDifferences(List pOld) { return null; }

		/* Is this list locked */
		public boolean isLocked() { return ((theAccount != null) && (theAccount.isLocked())); }

		/**
		 * Disable Add a new item
		 */
		public ViewPrice addNewItem(DataItem<?> pElement) {	return null; }
		
		/**
		 * Add a new item to the edit list
		 * @return the newly added item
		 */
		public ViewPrice addNewItem() {
			ViewPrice myPrice = new ViewPrice(this);
			add(myPrice);
			return myPrice;
		}

		/**
		 * 	Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }

		/**
		 * Add additional fields to HTML String
		 * @param pDetail the debug detail
		 * @param pBuffer the string buffer 
		 */
		public void addHTMLFields(DebugDetail pDetail, StringBuilder pBuffer) {
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
	public class Values extends AcctPrice.Values {
		private boolean				hasDilution		= false;
		private Dilution			theDilution		= null;
		private DilutedPrice		theDilutedPrice = null;
		
		/* Access methods */
		public boolean				hasDilution()   	{ return hasDilution; }
		public Dilution				getDilution()   	{ return theDilution; }
		public DilutedPrice			getDilutedPrice()   { return theDilutedPrice; }
		
		public void setDilution(Dilution pDilution) {
			theDilution  	= pDilution; }
		public void setDilutedPrice(DilutedPrice pDilutedPrice) {
			theDilutedPrice	= pDilutedPrice; }

		/* Constructor */
		public Values() { hasDilution = ((List)getList()).hasDilutions; }
		public Values(Values 			pValues) { copyFrom(pValues); }
		public Values(AcctPrice.Values 	pValues) { copyFrom(pValues); }
		
		/* Check whether this object is equal to that passed */
		public Difference histEquals(HistoryValues<AcctPrice> pCompare) {
			/* Make sure that the object is the same class */
			if (pCompare.getClass() != this.getClass()) return Difference.Different;
			
			/* Cast correctly */
			Values myValues = (Values)pCompare;

			/* Determine underlying differences */
			Difference myDifference = super.histEquals(pCompare);
			
			/* Compare underlying values */
			myDifference = myDifference.combine(Dilution.differs(theDilution, 			myValues.theDilution));
			myDifference = myDifference.combine(DilutedPrice.differs(theDilutedPrice, 	myValues.theDilutedPrice));
			
			/* Return differences */
			return myDifference;
		}
		
		/* Copy values */
		public HistoryValues<AcctPrice> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			/* Handle a ViewPrice Values */
			if (pSource instanceof Values) {
				Values myValues = (Values)pSource;
				super.copyFrom(myValues);
				hasDilution		= myValues.hasDilution();
				theDilution		= myValues.getDilution();
				theDilutedPrice	= myValues.getDilutedPrice();
			}
			
			/* Handle an AcctPrice Values */
			else if (pSource instanceof AcctPrice.Values) {
				AcctPrice.Values myValues = (AcctPrice.Values)pSource;
				super.copyFrom(myValues);
				calculateDiluted();
			}
		}
		
		public Difference	fieldChanged(int fieldNo, HistoryValues<AcctPrice> pOriginal) {
			Values 		pValues = (Values)pOriginal;
			Difference	bResult = Difference.Identical;
			switch (fieldNo) {
				case FIELD_DILUTION:
					bResult = (Dilution.differs(theDilution,   	pValues.theDilution));
					break;
				case FIELD_DILUTEDPRICE:
					bResult = (DilutedPrice.differs(theDilutedPrice,	pValues.theDilutedPrice));
					break;
				default:
					bResult = super.fieldChanged(fieldNo, pOriginal);
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

			/* Access Price and date */
			DateDay myDate 		= getDate();
			Price 	myPrice		= getPriceValue();
			Account myAccount 	= getAccount();
			
			/* If we have can look at dilutions */
			if ((hasDilution) && (myDate != null) && (myPrice != null)) {
				/* Determine the dilution factor for the date */
				Dilution myDilution = myList.getDilutions().getDilutionFactor(myAccount, myDate);
				
				/* If we have a dilution factor */
				if (myDilution != null) {
					/* Store dilution details */
					theDilution 	= myDilution;
					theDilutedPrice = myPrice.getDilutedPrice(myDilution);
				}
			}
		}

		/**
		 * Disable encryption methods
		 */
		protected void updateSecurity() throws ModelException {}
		protected void applySecurity() throws ModelException {}
		protected void adoptSecurity(ControlKey pControl, EncryptedValues pBase) throws ModelException {}
	}		
}
