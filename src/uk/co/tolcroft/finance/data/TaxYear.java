package uk.co.tolcroft.finance.data;

import java.util.Calendar;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.Number.*;

/**
 * Tax Year Class representing taxation parameters for a tax year
 * @author Tony Washer
 */
public class TaxYear extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "TaxYear";

	/* Members */
	private int		theRegimeId		= -1;
	private boolean	isActive		= false;

	/* Access methods */
	public  Date  		getDate()         			{ return getObj().getYear(); }	
	public  Values   	getObj()          			{ return (Values)super.getObj(); }	
	public  TaxRegime 	getTaxRegime()				{ return getObj().getTaxRegime(); }
	public  Money 		getAllowance()    			{ return getObj().getAllowance(); }
	public  Money 		getRentalAllowance() 		{ return getObj().getRentalAllow(); }
	public  Money 		getLoBand()       			{ return getObj().getLoBand(); }
	public  Money 		getBasicBand()    			{ return getObj().getBasicBand(); }
	public  Money 		getCapitalAllow() 			{ return getObj().getCapitalAllow(); }
	public  Money 		getLoAgeAllow()   			{ return getObj().getLoAgeAllow(); }
	public  Money 		getHiAgeAllow()   			{ return getObj().getHiAgeAllow(); }
	public  Money 		getAgeAllowLimit()			{ return getObj().getAgeAllowLimit(); }
	public  Money 		getAddAllowLimit()			{ return getObj().getAddAllowLimit(); }
	public  Money 		getAddIncBound()  			{ return getObj().getAddIncBound(); }
	public  Rate 		getLoTaxRate()    			{ return getObj().getLoTaxRate(); }
	public  Rate 		getBasicTaxRate() 			{ return getObj().getBasicTaxRate(); }
	public  Rate 		getHiTaxRate()    			{ return getObj().getHiTaxRate(); }
	public  Rate 		getIntTaxRate()   			{ return getObj().getIntTaxRate(); }
	public  Rate 		getDivTaxRate()   			{ return getObj().getDivTaxRate(); }
	public  Rate 		getHiDivTaxRate() 			{ return getObj().getHiDivTaxRate(); }
	public  Rate 		getAddTaxRate()   			{ return getObj().getAddTaxRate(); }
	public  Rate 		getAddDivTaxRate()			{ return getObj().getAddDivTaxRate(); }
	public  Rate 		getCapTaxRate()   			{ return getObj().getCapTaxRate(); }
	public  Rate 		getHiCapTaxRate() 			{ return getObj().getHiCapTaxRate(); }
	public  boolean		isActive() 	  	  			{ return isActive; }
	public  boolean     hasLoSalaryBand() 			{ return getTaxRegime().hasLoSalaryBand(); }
	public  boolean     hasAdditionalTaxBand() 		{ return getTaxRegime().hasAdditionalTaxBand(); }
	public  boolean     hasCapitalGainsAsIncome() 	{ return getTaxRegime().hasCapitalGainsAsIncome(); }
	public  void        setDate(Date pDate) 		{ getObj().setYear(pDate); }

	/* Linking methods */
	public TaxYear    	getBase() { return (TaxYear)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_ID     		= 0;
	public static final int FIELD_REGIME  		= 1;
	public static final int FIELD_YEAR   		= 2;
	public static final int FIELD_RENTAL 		= 3;
	public static final int FIELD_ALLOW  		= 4;
	public static final int FIELD_LOAGAL 		= 5;
	public static final int FIELD_HIAGAL 		= 6;
	public static final int FIELD_LOBAND 		= 7;
	public static final int FIELD_BSBAND 		= 8;
	public static final int FIELD_CAPALW 		= 9;
	public static final int FIELD_AGELMT 		= 10;
	public static final int FIELD_ADDLMT 		= 11;
	public static final int FIELD_ADDBDY 		= 12;
	public static final int FIELD_LOTAX  		= 13;
	public static final int FIELD_BASTAX 		= 14;
	public static final int FIELD_HITAX  		= 15;
	public static final int FIELD_INTTAX 		= 16;
	public static final int FIELD_DIVTAX 		= 17;
	public static final int FIELD_HDVTAX 		= 18;
	public static final int FIELD_ADDTAX 		= 19;
	public static final int FIELD_ADVTAX 		= 20;
	public static final int FIELD_CAPTAX 		= 21;
	public static final int FIELD_HCPTAX 		= 22;
	public static final int NUMFIELDS	    	= 23;
	
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
			case FIELD_ID:		return "ID";
			case FIELD_REGIME:	return "Regime";
			case FIELD_YEAR:	return "Date";
			case FIELD_ALLOW:	return "Allowance";
			case FIELD_LOBAND:	return "LowTaxBand";
			case FIELD_BSBAND:	return "BasicTaxBand";
			case FIELD_RENTAL:	return "RentalAllowance";
			case FIELD_CAPALW:	return "CapitalAllowance";
			case FIELD_LOTAX:	return "LowTaxRate";
			case FIELD_BASTAX:	return "BasicTaxRate";
			case FIELD_HITAX:	return "HighTaxRate";
			case FIELD_INTTAX:	return "InterestTaxRate";
			case FIELD_DIVTAX:	return "DividendTaxRate";
			case FIELD_HDVTAX:	return "HighDividendTaxRate";
			case FIELD_ADDTAX:	return "AdditionalTaxRate";
			case FIELD_ADVTAX:	return "AdditionalDivTaxRate";
			case FIELD_CAPTAX:	return "CapitalTaxRate";
			case FIELD_HCPTAX:	return "HiCapitalTaxRate";
			case FIELD_LOAGAL:	return "LowAgeAllowance";
			case FIELD_HIAGAL:	return "HighAgeAllowance";
			case FIELD_AGELMT:	return "AgeAllowanceLimit";
			case FIELD_ADDLMT:	return "AdditionalAllowanceLimit";
			case FIELD_ADDBDY:	return "AdditionalIncomeBoundary";
			default:		  	return super.fieldName(iField);
		}
	}
	
	/**
	 * Format the value of a particular field as a table row
	 * @param iField the field number
	 * @param pObj the values to use
	 * @return the formatted field
	 */
	public String formatField(int iField, histObject pObj) {
		String 	myString = "";
		Values 	myObj 	 = (Values)pObj;
		switch (iField) {
			case FIELD_ID: 		
				myString += getId(); 
				break;
			case FIELD_REGIME:
				if ((myObj.getTaxRegime() == null) &&
					(theRegimeId != -1))
					myString += "Id=" + theRegimeId;
				else
					myString += TaxRegime.format(myObj.getTaxRegime()); 
				break;
			case FIELD_YEAR:	
				myString += Date.format(getDate()); 
				break;
			case FIELD_ALLOW:	
				myString += Money.format(myObj.getAllowance()); 
				break;
			case FIELD_LOBAND: 	
				myString += Money.format(myObj.getLoBand());	
				break;
			case FIELD_BSBAND:	
				myString += Money.format(myObj.getBasicBand()); 
				break;
			case FIELD_RENTAL:	
				myString += Money.format(myObj.getRentalAllow()); 
				break;
			case FIELD_LOTAX: 	
				myString += Rate.format(myObj.getLoTaxRate());	
				break;
			case FIELD_BASTAX: 	
				myString += Rate.format(myObj.getBasicTaxRate());	
				break;
			case FIELD_HITAX: 	
				myString += Rate.format(myObj.getHiTaxRate());	
				break;
			case FIELD_INTTAX: 	
				myString += Rate.format(myObj.getIntTaxRate());	
				break;
			case FIELD_DIVTAX: 	
				myString += Rate.format(myObj.getDivTaxRate());	
				break;
			case FIELD_HDVTAX: 	
				myString += Rate.format(myObj.getHiDivTaxRate());	
				break;
			case FIELD_ADDTAX: 	
				myString += Rate.format(myObj.getAddTaxRate());	
				break;
			case FIELD_ADVTAX: 	
				myString += Rate.format(myObj.getAddDivTaxRate());	
				break;
			case FIELD_LOAGAL: 	
				myString += Money.format(myObj.getLoAgeAllow());	
				break;
			case FIELD_HIAGAL: 	
				myString += Money.format(myObj.getHiAgeAllow());	
				break;
			case FIELD_AGELMT: 	
				myString += Money.format(myObj.getAgeAllowLimit());	
				break;
			case FIELD_ADDLMT: 	
				myString += Money.format(myObj.getAddAllowLimit());	
				break;
			case FIELD_ADDBDY: 	
				myString += Money.format(myObj.getAddIncBound());	
				break;
			case FIELD_CAPALW: 	
				myString += Money.format(myObj.getCapitalAllow());	
				break;
			case FIELD_CAPTAX: 	
				myString += Rate.format(myObj.getCapTaxRate());	
				break;
			case FIELD_HCPTAX: 	
				myString += Rate.format(myObj.getHiCapTaxRate());	
				break;
		}
		return myString;
	}
							
	/**
	 * Construct a copy of a TaxYear
	 * 
	 * @param pList The List to build into 
	 * @param pTaxYear The TaxYear to copy 
	 */
	public TaxYear(List pList, TaxYear pTaxYear) { 
		super(pList, pTaxYear.getId());
		isActive = pTaxYear.isActive();
		Values myObj  = new Values(pTaxYear.getObj());
		setObj(myObj);
		
		/* Switch on the ListStyle */
		switch (pList.getStyle()) {
			case CORE:
				pList.setNewId(this);				
				break;
			case EDIT:
				setBase(pTaxYear);
				setState(DataState.CLEAN);
				break;
			case UPDATE:
				setBase(pTaxYear);
				setState(pTaxYear.getState());
				break;
		}
	}
	
	/* Standard constructor */
	private TaxYear(List     		pList,
			        int            	uId,
			        int				uRegimeId,
			        java.util.Date  pDate,
			        String 			pAllowance,
			        String 			pRentalAllow,
			        String 			pLoAgeAllow,
			        String 			pHiAgeAllow,
			        String			pCapAllow,
			        String 			pAgeAllowLimit,
			        String 			pAddAllowLimit,
			        String 			pLoTaxBand,
			        String 			pBasicTaxBand,
			        String 			pAddIncBound,
			        String			pLoTaxRate,
			        String			pBasicTaxRate,
			        String			pHiTaxRate,
			        String			pIntTaxRate,
			        String			pDivTaxRate,
			        String			pHiDivTaxRate,
			        String			pAddTaxRate,
			        String			pAddDivTaxRate,
			        String			pCapTaxRate,
			        String			pHiCapTaxRate) throws Exception {
		/* Initialise item */
		super(pList, uId);
		
		/* Local variable */
		TaxRegime myRegime;
		
		/* Initialise values */
		Values myObj 	= new Values();
		setObj(myObj);

		/* Record the Id */
		theRegimeId = uRegimeId;
		myObj.setYear(new Date(pDate));
		
		/* Look up the Regime */
		myRegime = pList.theData.getTaxRegimes().searchFor(uRegimeId);
		if (myRegime == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Tax Regime Id");
		myObj.setTaxRegime(myRegime);
					
		/* Record the allowances */
		Money myMoney = Money.Parse(pAllowance);
		if (myMoney == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Allowance: " + pAllowance);
		myObj.setAllowance(myMoney);
		myMoney = Money.Parse(pLoTaxBand);
		if (myMoney == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Low Tax Band: " + pLoTaxBand);
		myObj.setLoBand(myMoney);
		myMoney = Money.Parse(pBasicTaxBand);
		if (myMoney == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Basic Tax Band: " + pBasicTaxBand);
		myObj.setBasicBand(myMoney);
		myMoney = Money.Parse(pRentalAllow);
		if (myMoney == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Rental Allowance: " + pRentalAllow);
		myObj.setRentalAllow(myMoney);
		myMoney = Money.Parse(pLoAgeAllow);
		if (myMoney == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Low Age Allowance: " + pLoAgeAllow);
		myObj.setLoAgeAllow(myMoney);	
		myMoney = Money.Parse(pHiAgeAllow);
		if (myMoney == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid High Age Allowance: " + pHiAgeAllow);
		myObj.setHiAgeAllow(myMoney);	
		myMoney = Money.Parse(pCapAllow);
		if (myMoney == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Capital Allowance: " + pHiAgeAllow);
		myObj.setCapitalAllow(myMoney);	
		myMoney = Money.Parse(pAgeAllowLimit);
		if (myMoney == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Age Allowance Limit: " + pAgeAllowLimit);
		myObj.setAgeAllowLimit(myMoney);	
		if (pAddAllowLimit != null) {
			myMoney = Money.Parse(pAddAllowLimit);
			if (myMoney == null) 
				throw new Exception(ExceptionClass.DATA,
									this,
									"Invalid Additional Allowance Limit: " + pAddAllowLimit);
			myObj.setAddAllowLimit(myMoney);	
		}
		if (pAddIncBound != null) {
			myMoney = Money.Parse(pAddIncBound);
			if (myMoney == null) 
				throw new Exception(ExceptionClass.DATA,
									this,
									"Invalid Additional Income Boundary: " + pAddIncBound);
			myObj.setAddIncBound(myMoney);	
		}

		/* Record the rates */
		Rate myRate = Rate.Parse(pLoTaxRate);
		if (myRate == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Low Tax Rate: " + pLoTaxRate);
		myObj.setLoTaxRate(myRate);
		myRate = Rate.Parse(pBasicTaxRate);
		if (myRate == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Basic Tax Rate: " + pBasicTaxRate);
		myObj.setBasicTaxRate(myRate);
		myRate = Rate.Parse(pHiTaxRate);
		if (myRate == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid High Tax Rate: " + pHiTaxRate);
		myObj.setHiTaxRate(myRate);
		myRate = Rate.Parse(pIntTaxRate);
		if (myRate == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Int Tax Rate: " + pIntTaxRate);
		myObj.setIntTaxRate(myRate);
		myRate = Rate.Parse(pDivTaxRate);
		if (myRate == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid Div Tax Rate: " + pDivTaxRate);
		myObj.setDivTaxRate(myRate);
		myRate = Rate.Parse(pHiDivTaxRate);
		if (myRate == null) 
			throw new Exception(ExceptionClass.DATA,
								this,
								"Invalid High Div Tax Rate: " + pHiDivTaxRate);
		myObj.setHiDivTaxRate(myRate);
		if (pAddTaxRate != null) {
			myRate = Rate.Parse(pAddTaxRate);
			if (myRate == null) 
				throw new Exception(ExceptionClass.DATA,
									this,
									"Invalid Additional Tax Rate: " + pAddTaxRate);
			myObj.setAddTaxRate(myRate);
		}
		if (pAddDivTaxRate != null) {
			myRate = Rate.Parse(pAddDivTaxRate);
			if (myRate == null) 
				throw new Exception(ExceptionClass.DATA,
									this,
									"Invalid Additional Div Tax Rate: " + pAddDivTaxRate);
			myObj.setAddDivTaxRate(myRate);
		}
		if (pCapTaxRate != null) {
			myRate = Rate.Parse(pCapTaxRate);
			if (myRate == null) 
				throw new Exception(ExceptionClass.DATA,
									this,
									"Invalid Capital Gains Tax Rate: " + pCapTaxRate);
			myObj.setCapTaxRate(myRate);
		}
		if (pHiCapTaxRate != null) {
			myRate = Rate.Parse(pHiCapTaxRate);
			if (myRate == null) 
				throw new Exception(ExceptionClass.DATA,
									this,
									"Invalid High Capital Gains Tax Rate: " + pHiCapTaxRate);
			myObj.setHiCapTaxRate(myRate);
		}
		
		/* Allocate the id */
		pList.setNewId(this);				
	}
		
	/* Standard constructor for a newly inserted account */
	public TaxYear(List pList) {
		super(pList, 0);
		Values theObj = new Values();
		setObj(theObj);
		setState(DataState.NEW);
	}

	/**
	 * Compare this tax year to another to establish equality.
	 * @param pThat The Tax Year to compare to
	 * @return <code>true</code> if the tax year is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a TaxYear */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a TaxYear */
		TaxYear myYear = (TaxYear)pThat;
		
		/* Check for equality */
		if (getId() != myYear.getId()) 											return false;
		if (Date.differs(getDate(),             myYear.getDate())) 				return false;
		if (TaxRegime.differs(getTaxRegime(),   myYear.getTaxRegime())) 		return false;
		if (Money.differs(getAllowance(),       myYear.getAllowance())) 		return false;
		if (Money.differs(getLoBand(),          myYear.getLoBand())) 			return false;
		if (Money.differs(getBasicBand(),       myYear.getBasicBand())) 		return false;
		if (Money.differs(getRentalAllowance(), myYear.getRentalAllowance())) 	return false;
		if (Money.differs(getCapitalAllow(),    myYear.getCapitalAllow())) 		return false;
		if (Money.differs(getLoAgeAllow(), 		myYear.getLoAgeAllow()))	 	return false;
		if (Money.differs(getHiAgeAllow(), 		myYear.getHiAgeAllow()))	 	return false;
		if (Money.differs(getAgeAllowLimit(), 	myYear.getAgeAllowLimit()))	 	return false;
		if (Money.differs(getAddAllowLimit(), 	myYear.getAddAllowLimit()))	 	return false;
		if (Money.differs(getAddIncBound(), 	myYear.getAddIncBound()))	 	return false;
		if (Rate.differs(getLoTaxRate(),     	myYear.getLoTaxRate())) 		return false;
		if (Rate.differs(getBasicTaxRate(),  	myYear.getBasicTaxRate())) 		return false;
		if (Rate.differs(getHiTaxRate(),     	myYear.getHiTaxRate())) 		return false;
		if (Rate.differs(getIntTaxRate(),    	myYear.getIntTaxRate())) 		return false;
		if (Rate.differs(getDivTaxRate(),    	myYear.getDivTaxRate())) 		return false;
		if (Rate.differs(getHiDivTaxRate(),  	myYear.getHiDivTaxRate()))    	return false;
		if (Rate.differs(getAddTaxRate(),    	myYear.getAddTaxRate()))    	return false;
		if (Rate.differs(getAddDivTaxRate(), 	myYear.getAddDivTaxRate()))    	return false;
		if (Rate.differs(getCapTaxRate(),    	myYear.getCapTaxRate()))    	return false;
		if (Rate.differs(getHiCapTaxRate(),  	myYear.getHiCapTaxRate()))    	return false;
		return true;
	}

	/**
	 * Compare this tax year to another to establish sort order. 
	 * @param pThat The TaxYear to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		int iDiff;

		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a TaxYear */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the target taxYear */
		TaxYear myThat = (TaxYear)pThat;
		
		/* If the dates differ */
		if (this.getDate() != myThat.getDate()) {
			/* Compare on date */
			if (this.getDate() == null) return 1;
			if (myThat.getDate() == null) return -1;
			iDiff = getDate().compareTo(myThat.getDate());
			if (iDiff != 0) return iDiff;
		}
		
		/* Compare on id */
		iDiff = (int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}

	/**
	 * Validate the taxYear
	 */
	public void validate() {
		Date 	myDate = getDate();
		List 	myList = (List)getList();
		TaxYear myPrev;
				
		/* The date must not be null */
		if ((myDate == null) || (myDate.isNull())) 
			addError("Null date is not allowed", FIELD_YEAR);
			
		/* else we have a date */
		else {
			/* The date must be unique */
			if (myList.countInstances(myDate) > 1) 
				addError("Date must be unique", FIELD_YEAR);
			
			/* The day and month must be 5th April */
			if ((myDate.getDay() != 5) || 
				(myDate.getMonth() != Calendar.APRIL)) 
				addError("Date must be 5th April", FIELD_YEAR);
			
			/* The year must be one greater than the preceding element */
			if (((myPrev = myList.peekPrevious(this)) != null) &&
			    (myDate.getYear() != myPrev.getDate().getYear()+1)) 
				addError("There can be no gaps in the list", FIELD_YEAR);
		}
			
		/* TaxRegime must be non-null */
		if (getTaxRegime() == null) 
			addError("TaxRegime must be non-null", FIELD_REGIME);
			
		/* The allowance must be non-null */
		if ((getAllowance() == null) || (!getAllowance().isPositive()))
			addError("Value must be positive", FIELD_ALLOW);
		
		/* The rental allowance must be non-null */
		if ((getRentalAllowance() == null) || (!getRentalAllowance().isPositive()))
			addError("Value must be positive", FIELD_RENTAL);
		
		/* The loAgeAllow must be non-null */
		if ((getLoAgeAllow() == null) || (!getLoAgeAllow().isPositive()))
			addError("Value must be positive", FIELD_LOAGAL);
		
		/* The loAgeAllow must be greater than Allowance */
		if ((getLoAgeAllow() != null) && (getAllowance() != null) &&
			(getLoAgeAllow().getValue() < getAllowance().getValue()))
			addError("Value must be greater than allowance", FIELD_LOAGAL);
		
		/* The hiAgeAllow must be non-null */
		if ((getHiAgeAllow() == null) || (!getHiAgeAllow().isPositive()))
			addError("Value must be positive", FIELD_HIAGAL);
		
		/* The hiAgeAllow must be greater than loAgeAllowance */
		if ((getHiAgeAllow() != null) && (getLoAgeAllow() != null) &&
			(getHiAgeAllow().getValue() < getLoAgeAllow().getValue()))
			addError("Value must be greater than low age allowance", FIELD_HIAGAL);
		
		/* The ageAllowLimit must be non-null */
		if ((getAgeAllowLimit() == null) || (!getAgeAllowLimit().isPositive()))
			addError("Value must be positive", FIELD_AGELMT);
		
		/* The capitalAllow must be non-null */
		if ((getCapitalAllow() == null) || (!getCapitalAllow().isPositive()))
			addError("Value must be positive", FIELD_CAPALW);
		
		/* The loBand must be non-null */
		if ((getLoBand() == null) || (!getLoBand().isPositive()))
			addError("Value must be positive", FIELD_LOBAND);
		
		/* The basicBand must be non-null */
		if ((getBasicBand() == null) || (!getBasicBand().isPositive()))
			addError("Value must be positive", FIELD_BSBAND);
		
		/* The loRate must be non-null */
		if ((getLoTaxRate() == null) || (!getLoTaxRate().isPositive()))
			addError("Value must be positive", FIELD_LOTAX);
		
		/* The basicRate must be non-null */
		if ((getBasicTaxRate() == null) || (!getBasicTaxRate().isPositive()))
			addError("Value must be positive", FIELD_BASTAX);

		/* The hiRate must be non-null */
		if ((getHiTaxRate() == null) || (!getHiTaxRate().isPositive()))
			addError("Value must be positive", FIELD_HITAX);

		/* The intRate must be non-null */
		if ((getIntTaxRate() == null) || (!getIntTaxRate().isPositive()))
			addError("Value must be positive", FIELD_INTTAX);
		
		/* The divRate must be non-null */
		if ((getDivTaxRate() == null) || (!getDivTaxRate().isPositive()))
			addError("Value must be positive", FIELD_DIVTAX);
		
		/* The hiDivRate must be non-null */
		if ((getHiDivTaxRate() == null) || (!getHiDivTaxRate().isPositive()))
			addError("Value must be positive", FIELD_HDVTAX);			
		
		/* If the tax regime is additional */
		if ((getTaxRegime() != null) && (getTaxRegime().hasAdditionalTaxBand())) {
			/* The addAllowLimit must be non-null */
			if ((getAddAllowLimit() == null) || (!getAddAllowLimit().isPositive()))
				addError("Value must be positive", FIELD_ADDLMT);
			
			/* The addIncBound must be non-null */
			if ((getAddIncBound() == null) || (!getAddIncBound().isPositive()))
				addError("Value must be positive", FIELD_ADDBDY);
			
			/* The addRate must be non-null */
			if ((getAddTaxRate() == null) || (!getAddTaxRate().isPositive()))
				addError("Value must be positive", FIELD_ADDTAX);
			
			/* The addDivRate must be non-null */
			if ((getAddDivTaxRate() == null) || (!getAddDivTaxRate().isPositive()))
				addError("Value must be positive", FIELD_ADVTAX);							
		}
		
		/* If the tax regime does not have capital gains as income */
		if ((getTaxRegime() != null) && (!getTaxRegime().hasCapitalGainsAsIncome())) {
			/* The capitalRate must be non-null */
			if ((getCapTaxRate() == null) || (!getCapTaxRate().isPositive()))
				addError("Value must be positive", FIELD_CAPTAX);
			
			/* The hiCapTaxRate must be positive */
			if ((getHiCapTaxRate() != null) && (!getHiCapTaxRate().isPositive()))
				addError("Value must be positive", FIELD_HCPTAX);
		}
		
		/* Set validation flag */
		if (!hasErrors()) setValidEdit();
	}			
	
	/**
	 * Extract the date range represented by the tax years
	 * 
	 * @return the range of tax years
	 */
	public Date.Range getRange() {
		Date  		myStart;
		Date  		myEnd;
		Date.Range 	myRange;
		
		/* Access start date */
		myStart =  new Date(getDate());
		
		/* Move back to start of year */
		myStart.adjustYear(-1);
		myStart.adjustDay(1);
		
		/* Access last date */
		myEnd  = getDate();
		
		/* Create the range */
		myRange = new Date.Range(myStart, myEnd);
		
		/* Return the range */
		return myRange;
	}
	
	/**
	 * Set a new tax regime 
	 * 
	 * @param pTaxRegime the TaxRegime 
	 */
	public void setTaxRegime(TaxRegime pTaxRegime) {
		getObj().setTaxRegime(pTaxRegime);
	}
	
	/**
	 * Set a new allowance 
	 * 
	 * @param pAllowance the allowance 
	 */
	public void setAllowance(Money pAllowance) {
		getObj().setAllowance((pAllowance == null) ? null 
												   : new Money(pAllowance));
	}
	
	/**
	 * Set a new rental allowance 
	 * 
	 * @param pAllowance the allowance 
	 */
	public void setRentalAllowance(Money pAllowance) {
		getObj().setRentalAllow((pAllowance == null) ? null 
								  				     : new Money(pAllowance));
	}
	
	/**
	 * Set a new capital allowance 
	 * 
	 * @param pAllowance the allowance 
	 */
	public void setCapitalAllow(Money pAllowance) {
		getObj().setCapitalAllow((pAllowance == null) ? null 
								  				      : new Money(pAllowance));
	}
	
	/**
	 * Set a new Low Tax Band 
	 * 
	 * @param pLoBand the Low Tax Band 
	 */
	public void setLoBand(Money pLoBand) {
		getObj().setLoBand((pLoBand == null) ? null : new Money(pLoBand));
	}
	
	/**
	 * Set a new Basic Tax Band 
	 * 
	 * @param pBasicBand the Basic Tax Band 
	 */
	public void setBasicBand(Money pBasicBand) {
		getObj().setBasicBand((pBasicBand == null) ? null : new Money(pBasicBand));
	}
	
	/**
	 * Set a new Low Age Allowance 
	 * 
	 * @param pLoAgeAllow the Low Age Allowance 
	 */
	public void setLoAgeAllow(Money pLoAgeAllow) {
		getObj().setLoAgeAllow((pLoAgeAllow == null) ? null : new Money(pLoAgeAllow));
	}
	
	/**
	 * Set a new High Age Allowance 
	 * 
	 * @param pHiAgeAllow the High Age Allowance 
	 */
	public void setHiAgeAllow(Money pHiAgeAllow) {
		getObj().setHiAgeAllow((pHiAgeAllow == null) ? null : new Money(pHiAgeAllow));
	}
	
	/**
	 * Set a new Age Allowance Limit
	 * 
	 * @param pAgeAllowLimit the Age Allowance Limit
	 */
	public void setAgeAllowLimit(Money pAgeAllowLimit) {
		getObj().setAgeAllowLimit((pAgeAllowLimit == null) ? null : new Money(pAgeAllowLimit));
	}
	
	/**
	 * Set a new Additional Allowance Limit
	 * 
	 * @param pAddAllowLimit the Additional Allowance Limit
	 */
	public void setAddAllowLimit(Money pAddAllowLimit) {
		getObj().setAddAllowLimit((pAddAllowLimit == null) ? null : new Money(pAddAllowLimit));
	}
	
	/**
	 * Set a new Additional Income Boundary
	 * 
	 * @param pAddIncBound the Additional Income Boundary
	 */
	public void setAddIncBound(Money pAddIncBound) {
		getObj().setAddIncBound((pAddIncBound == null) ? null : new Money(pAddIncBound));
	}
	
	/**
	 * Set a new Low Tax Rate 
	 * 
	 * @param pRate the Low Tax Rate 
	 */
	public void setLoTaxRate(Rate pRate) {
		getObj().setLoTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new Basic tax rate
	 * 
	 * @param pRate the Basic tax rate 
	 */
	public void setBasicTaxRate(Rate pRate) {
		getObj().setBasicTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new high tax rate 
	 * 
	 * @param pRate the high tax rate 
	 */
	public void setHiTaxRate(Rate pRate) {
		getObj().setHiTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new Interest Tax Rate 
	 * 
	 * @param pRate the Interest Tax Rate 
	 */
	public void setIntTaxRate(Rate pRate) {
		getObj().setIntTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new Dividend tax rate
	 * 
	 * @param pRate the Dividend tax rate 
	 */
	public void setDivTaxRate(Rate pRate) {
		getObj().setDivTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new high dividend tax rate 
	 * 
	 * @param pRate the high dividend tax rate 
	 */
	public void setHiDivTaxRate(Rate pRate) {
		getObj().setHiDivTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new additional tax rate 
	 * 
	 * @param pRate the additional tax rate 
	 */
	public void setAddTaxRate(Rate pRate) {
		getObj().setAddTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new additional dividend tax rate 
	 * 
	 * @param pRate the additional dividend tax rate 
	 */
	public void setAddDivTaxRate(Rate pRate) {
		getObj().setAddDivTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new capital tax rate 
	 * 
	 * @param pRate the capital tax rate 
	 */
	public void setCapTaxRate(Rate pRate) {
		getObj().setCapTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a high capital tax rate 
	 * 
	 * @param pRate the additional dividend tax rate 
	 */
	public void setHiCapTaxRate(Rate pRate) {
		getObj().setHiCapTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Mark the tax year as active 
	 */
	public void setActive() {
		isActive = true;
	}
	
	/**
	 * Update taxYear from a taxYear extract 
	 * 
	 * @param pTaxYear the changed taxYear 
	 */
	public void applyChanges(DataItem pTaxYear) {
		TaxYear myTaxYear = (TaxYear)pTaxYear;
		
		/* Store the current detail into history */
		pushHistory();
		
		/* Update the tax regime if required */
		if (TaxRegime.differs(getTaxRegime(), myTaxYear.getTaxRegime()))
			setTaxRegime(myTaxYear.getTaxRegime());
	
		/* Update the allowance if required */
		if (Money.differs(getAllowance(), myTaxYear.getAllowance()))
			setAllowance(myTaxYear.getAllowance());
	
		/* Update the rental allowance if required */
		if (Money.differs(getRentalAllowance(), myTaxYear.getRentalAllowance()))
			setRentalAllowance(myTaxYear.getRentalAllowance());
	
		/* Update the Low band if required */
		if (Money.differs(getLoBand(), myTaxYear.getLoBand()))
			setLoBand(myTaxYear.getLoBand());
			
		/* Update the basic band if required */
		if (Money.differs(getBasicBand(), myTaxYear.getBasicBand()))
			setBasicBand(myTaxYear.getBasicBand());
		
		/* Update the low age allowance if required */
		if (Money.differs(getLoAgeAllow(), myTaxYear.getLoAgeAllow()))
			setLoAgeAllow(myTaxYear.getLoAgeAllow());
		
		/* Update the high age allowance if required */
		if (Money.differs(getHiAgeAllow(), myTaxYear.getHiAgeAllow()))
			setHiAgeAllow(myTaxYear.getHiAgeAllow());
		
		/* Update the age allowance limit if required */
		if (Money.differs(getAgeAllowLimit(), myTaxYear.getAgeAllowLimit()))
			setAgeAllowLimit(myTaxYear.getAgeAllowLimit());
		
		/* Update the additional allowance limit if required */
		if (Money.differs(getAddAllowLimit(), myTaxYear.getAddAllowLimit()))
			setAddAllowLimit(myTaxYear.getAddAllowLimit());
		
		/* Update the additional income boundary if required */
		if (Money.differs(getAddIncBound(), myTaxYear.getAddIncBound()))
			setAddIncBound(myTaxYear.getAddIncBound());
		
		/* Update the Low tax rate if required */
		if (Rate.differs(getLoTaxRate(), myTaxYear.getLoTaxRate()))
			setLoTaxRate(myTaxYear.getLoTaxRate());
		
		/* Update the standard tax rate if required */
		if (Rate.differs(getBasicTaxRate(), myTaxYear.getBasicTaxRate()))
			setBasicTaxRate(myTaxYear.getBasicTaxRate());
						
		/* Update the high tax rate if required */
		if (Rate.differs(getHiTaxRate(), myTaxYear.getHiTaxRate()))
			setHiTaxRate(myTaxYear.getHiTaxRate());
						
		/* Update the interest tax rate if required */
		if (Rate.differs(getIntTaxRate(), myTaxYear.getIntTaxRate()))
			setIntTaxRate(myTaxYear.getIntTaxRate());
		
		/* Update the dividend tax rate if required */
		if (Rate.differs(getDivTaxRate(), myTaxYear.getDivTaxRate()))
			setDivTaxRate(myTaxYear.getDivTaxRate());
						
		/* Update the high dividend rate if required */
		if (Rate.differs(getHiDivTaxRate(), myTaxYear.getHiDivTaxRate()))
			setHiDivTaxRate(myTaxYear.getHiDivTaxRate());
		
		/* Update the additional rate if required */
		if (Rate.differs(getAddTaxRate(), myTaxYear.getAddTaxRate()))
			setAddTaxRate(myTaxYear.getAddTaxRate());
		
		/* Update the additional dividend rate if required */
		if (Rate.differs(getAddDivTaxRate(), myTaxYear.getAddDivTaxRate()))
			setAddDivTaxRate(myTaxYear.getAddDivTaxRate());
		
		/* Update the capital rate if required */
		if (Rate.differs(getCapTaxRate(), myTaxYear.getCapTaxRate()))
			setCapTaxRate(myTaxYear.getCapTaxRate());
		
		/* Update the high capital rate if required */
		if (Rate.differs(getHiCapTaxRate(), myTaxYear.getHiCapTaxRate()))
			setHiCapTaxRate(myTaxYear.getHiCapTaxRate());
		
		/* Check for changes */
		if (checkForHistory()) setState(DataState.CHANGED);
	}

	/* The Tax Year List class */
	public static class List extends DataList<TaxYear> {		
		private DataSet	theData			= null;
		public 	DataSet getData()		{ return theData; }

		/** 
	 	 * Construct an empty CORE TaxYear list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(DataSet pData) { 
			super(ListStyle.CORE, false);
			theData = pData;
		}

		/** 
	 	 * Construct an empty generic TaxYear list
	 	 * @param pData the DataSet for the list
	 	 * @param pStyle the style of the list 
	 	 */
		public List(DataSet pData, ListStyle pStyle) { 
			super(pStyle, false);
			theData = pData;
		}

		/** 
	 	 * Construct a generic TaxYear list
	 	 * @param pList the source TaxYear list 
	 	 * @param pStyle the style of the list 
	 	 */
		public List(List pList, ListStyle pStyle) { 
			super(pList, pStyle);
			theData = pList.getData();
		}

		/** 
	 	 * Construct a difference TaxYear list
	 	 * @param pNew the new TaxYear list 
	 	 * @param pOld the old TaxYear list 
	 	 */
		protected List(List pNew, List pOld) { 
			super(pNew, pOld);
			theData = pNew.getData();
		}
	
		/** 
	 	 * Clone a TaxYear list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return new List(this, ListStyle.CORE); }
		
		/**
		 * Add a new item to the core list
		 * @param pTaxYear item
		 * @return the newly added item
		 */
		public TaxYear addNewItem(DataItem pTaxYear) {
			TaxYear myYear = new TaxYear(this, (TaxYear)pTaxYear);
			myYear.addToList();
			return myYear;
		}
	
		/**
		 * Create a new empty element in the edit list (null-operation)
		 * @param isCredit - is the item a credit or debit
		 * @return the newly added item
		 */
		public TaxYear addNewItem(boolean isCredit) { return null; }
			
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }
				
		/**
		 * Search for the tax year that encompasses this date
		 * 
		 * @param pDate Date of item
		 * @return The TaxYear if present (or null)
		 */
		public TaxYear searchFor(Date pDate) {
			ListIterator 	myIterator;
			TaxYear   		myCurr;
			Date.Range		myRange;
			int        		iDiff;
			
			/* Access the iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				/* Access the range for this tax year */
				myRange = myCurr.getRange();
				
				/* Determine whether the date is owned by the tax year */
				iDiff = myRange.compareTo(pDate);
				if (iDiff == 0) break;
			}
			
			/* Return to caller */
			return myCurr;
		}
		
		/**
		 * Search for a particular tax year by year string
		 * 
		 * @param pYear Date of item
		 * @return The TaxYear if present (or null)
		 */
		public TaxYear searchFor(String pYear) {
			ListIterator 	myIterator;
			TaxYear   		myCurr;
			boolean    		isMatch;
			long	   		uYear;
			
			/* Access the search year */
			uYear = Long.parseLong(pYear);
			
			/* Access the iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				isMatch = (uYear == (long)myCurr.getDate().getYear());
				if (isMatch) break;
			}
			
			/* Return to caller */
			return myCurr;
		}
		
		/**
		 * Count the instances of a date
		 * 
		 * @param pDate the date
		 * @return The Item if present (or null)
		 */
		protected int countInstances(Date pDate) {
			ListIterator 	myIterator;
			TaxYear 		myCurr;
			int      		iDiff;
			int      		iCount = 0;
			
			/* Access the iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				iDiff = pDate.compareTo(myCurr.getDate());
				if (iDiff == 0) iCount++;
			}
			
			/* Return to caller */
			return iCount;
		}	
		
		/**
		 * Reset the active flags after changes to events
		 */
		public void reset() {
			ListIterator 	myIterator;
			TaxYear 		myCurr;
			
			/* Access the iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				/* Clear the flags */
				myCurr.isActive = false;
			}
		}
		
		/**
		 * Extract the date range represented by the tax years
		 * 
		 * @return the range of tax years
		 */
		public Date.Range getRange() {
			ListIterator 	myIterator;
			TaxYear     	myCurr;
			Date  			myStart;
			Date  			myEnd;
			Date.Range 		myRange;
			
			/* Access the iterator */
			myIterator = listIterator(true);
			
			/* Extract the first item */
			myCurr = myIterator.peekFirst();
			if (myCurr == null)	{
				/* Set null values */
				myStart = null;
				myEnd   = null;
			}
			
			/* else we have a tax year */
			else {
				/* Access start date */
				myStart =  new Date(myCurr.getDate());
			
				/* Move back to start of year */
				myStart.adjustYear(-1);
				myStart.adjustDay(1);
			
				/* Extract the last item */
				myCurr = myIterator.peekLast();
				myEnd  = myCurr.getDate();
			}
			
			/* Create the range */
			myRange = new Date.Range(myStart, myEnd);
			
			/* Return the range */
			return myRange;
		}
		
		/* Allow a tax parameter to be added */
		public void addItem(int	    		uId,
							String  		pRegime,
				            java.util.Date  pDate,
				            String  		pAllowance,
				            String  		pRentalAllow,
				            String  		pLoAgeAllow,
				            String  		pHiAgeAllow,
				            String			pCapAllow,
				            String  		pAgeAllowLimit,
				            String  		pAddAllowLimit,
				            String  		pLoTaxBand,
				            String  		pBasicTaxBand,
				            String  		pAddIncBound,
				            String  		pLoTaxRate,
				            String  		pBasicTaxRate,
				            String  		pHiTaxRate,
				            String  		pIntTaxRate,
				            String  		pDivTaxRate,
				            String  		pHiDivTaxRate,
				            String  		pAddTaxRate,
				            String  		pAddDivTaxRate,
				            String			pCapTaxRate,
				            String			pHiCapTaxRate) throws Exception {
			/* Local variables */
			TaxRegime    	myTaxRegime;		
			
			/* Look up the Tax Regime */
			myTaxRegime = theData.getTaxRegimes().searchFor(pRegime);
			if (myTaxRegime == null) 
				throw new Exception(ExceptionClass.DATA,
			                        "TaxYear on <" + 
			                        Date.format(new Date(pDate)) +
			                        "> has invalid TaxRegime <" + pRegime + ">");
			
			/* Create the tax year */
			addItem(uId,
					myTaxRegime.getId(),
					pDate,
					pAllowance,
					pRentalAllow,
					pLoAgeAllow,
					pHiAgeAllow,
					pCapAllow,
					pAgeAllowLimit,
					pAddAllowLimit,
					pLoTaxBand,
					pBasicTaxBand,
					pAddIncBound,
					pLoTaxRate,
					pBasicTaxRate,
					pHiTaxRate,
					pIntTaxRate,
					pDivTaxRate,
					pHiDivTaxRate,
					pAddTaxRate,
					pAddDivTaxRate,
					pCapTaxRate,
					pHiCapTaxRate);		
	    }
		
		/* Allow a tax parameter to be added */
		public void addItem(int    			uId,
							int    			uRegimeId,
				            java.util.Date  pDate,
				            String  		pAllowance,
				            String  		pRentalAllow,
				            String  		pLoAgeAllow,
				            String  		pHiAgeAllow,
				            String			pCapAllow,
				            String  		pAgeAllowLimit,
				            String  		pAddAllowLimit,
				            String  		pLoTaxBand,
				            String  		pBasicTaxBand,
				            String  		pAddIncBound,
				            String  		pLoTaxRate,
				            String  		pBasicTaxRate,
				            String  		pHiTaxRate,
				            String  		pIntTaxRate,
				            String  		pDivTaxRate,
				            String  		pHiDivTaxRate,
				            String  		pAddTaxRate,
				            String  		pAddDivTaxRate,
				            String			pCapTaxRate,
				            String			pHiCapTaxRate) throws Exception {
			/* Local variables */
			TaxYear       myTaxYear;		
			
			/* Create the tax year */
			myTaxYear = new TaxYear(this,
					                uId,
					                uRegimeId,
					                pDate,
					                pAllowance,
					                pRentalAllow,
					      			pLoAgeAllow,
					    			pHiAgeAllow,
					    			pCapAllow,
					    			pAgeAllowLimit,
					    			pAddAllowLimit,
					                pLoTaxBand,
					                pBasicTaxBand,
					    			pAddIncBound,
					                pLoTaxRate,
					                pBasicTaxRate,
					                pHiTaxRate,
					                pIntTaxRate,
					                pDivTaxRate,
					                pHiDivTaxRate,
					                pAddTaxRate,
					                pAddDivTaxRate,
					                pCapTaxRate,
					                pHiCapTaxRate);
			
			/* Check that this TaxYearId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
						  			myTaxYear,
			  			            "Duplicate TaxYearId");
			 
			/* Check that this TaxYear has not been previously added */
			if (searchFor(new Date(pDate)) != null) 
				throw new Exception(ExceptionClass.DATA,
						  			myTaxYear,
			                        "Duplicate TaxYear");
			
			/* Validate the tax year */
			myTaxYear.validate();
			
			/* Handle validation failure */
			if (myTaxYear.hasErrors()) 
				throw new Exception(ExceptionClass.VALIDATE,
									myTaxYear,
									"Failed validation");
			
			/* Add the TaxYear to the list */
			myTaxYear.addToList();				
	    }		
	}

	/**
	 *  Values for a tax year
	 */
	public class Values implements histObject {
		private Date 		theYear	 		 = null;
		private TaxRegime 	theTaxRegime	 = null;
		private Money 		theAllowance     = null;
		private Money 		theRentalAllow   = null;
		private Money 		theLoAgeAllow    = null;
		private Money 		theHiAgeAllow    = null;
		private Money 		theCapitalAllow  = null;
		private Money 		theLoBand		 = null;
		private Money 		theBasicBand     = null;
		private Money 		theAgeAllowLimit = null;
		private Money 		theAddAllowLimit = null;
		private Money 		theAddIncBound	 = null;
		private Rate 		theLoTaxRate     = null;
		private Rate 		theBasicTaxRate  = null;
		private Rate 		theHiTaxRate     = null;
		private Rate 		theIntTaxRate    = null;
		private Rate 		theDivTaxRate    = null;
		private Rate 		theHiDivTaxRate  = null;
		private Rate 		theAddTaxRate 	 = null;
		private Rate 		theAddDivTaxRate = null;
		private Rate 		theCapTaxRate 	 = null;
		private Rate 		theHiCapTaxRate  = null;
		
		/* Access methods */
		public Date 		getYear()			{ return theYear; }
		public TaxRegime 	getTaxRegime()		{ return theTaxRegime; }
		public Money 		getAllowance()		{ return theAllowance; }
		public Money		getRentalAllow()  	{ return theRentalAllow; }
		public Money		getLoBand()       	{ return theLoBand; }
		public Money 		getBasicBand()    	{ return theBasicBand; }
		public Money 		getCapitalAllow()   { return theCapitalAllow; }
		public Money 		getLoAgeAllow()     { return theLoAgeAllow; }
		public Money 		getHiAgeAllow()     { return theHiAgeAllow; }
		public Money		getAgeAllowLimit()  { return theAgeAllowLimit; }
		public Money		getAddAllowLimit()  { return theAddAllowLimit; }
		public Money		getAddIncBound()  	{ return theAddIncBound; }
		public Rate  		getLoTaxRate()    	{ return theLoTaxRate; }
		public Rate  		getBasicTaxRate()   { return theBasicTaxRate; }
		public Rate  		getHiTaxRate()    	{ return theHiTaxRate; }
		public Rate  		getIntTaxRate()   	{ return theIntTaxRate; }
		public Rate  		getDivTaxRate()   	{ return theDivTaxRate; }
		public Rate  		getHiDivTaxRate() 	{ return theHiDivTaxRate; }
		public Rate  		getAddTaxRate() 	{ return theAddTaxRate; }
		public Rate  		getAddDivTaxRate() 	{ return theAddDivTaxRate; }
		public Rate  		getCapTaxRate() 	{ return theCapTaxRate; }
		public Rate  		getHiCapTaxRate() 	{ return theHiCapTaxRate; }
		
		public void setYear(Date pYear) {
			theYear   		= pYear; }
		public void setTaxRegime(TaxRegime pTaxRegime) {
			theTaxRegime    = pTaxRegime; }
		public void setAllowance(Money pAllowance) {
			theAllowance    = pAllowance; }
		public void setRentalAllow(Money pAllowance) {
			theRentalAllow  = pAllowance; }
		public void setLoBand(Money pLoTaxBand) {
			theLoBand       = pLoTaxBand; }
		public void setBasicBand(Money pBasicTaxBand) {
			theBasicBand    = pBasicTaxBand; }
		public void setCapitalAllow(Money pAllowance) {
			theCapitalAllow = pAllowance; }
		public void setLoAgeAllow(Money pAllowance) {
			theLoAgeAllow   = pAllowance; }
		public void setHiAgeAllow(Money pAllowance) {
			theHiAgeAllow   = pAllowance; }
		public void setAgeAllowLimit(Money pLimit) {
			theAgeAllowLimit = pLimit; }
		public void setAddAllowLimit(Money pLimit) {
			theAddAllowLimit = pLimit; }
		public void setAddIncBound(Money pBound) {
			theAddIncBound	= pBound; }
		public void setLoTaxRate(Rate pLoTaxRate) {
			theLoTaxRate    = pLoTaxRate; }
		public void setBasicTaxRate(Rate pBasicTaxRate) {
			theBasicTaxRate   = pBasicTaxRate; }
		public void setHiTaxRate(Rate pHiTaxRate) {
			theHiTaxRate    = pHiTaxRate; }
		public void setIntTaxRate(Rate pIntTaxRate) {
			theIntTaxRate   = pIntTaxRate; }
		public void setDivTaxRate(Rate pDivTaxRate) {
			theDivTaxRate   = pDivTaxRate; }
		public void setHiDivTaxRate(Rate pHiDivTaxRate) {
			theHiDivTaxRate = pHiDivTaxRate; }
		public void setAddTaxRate(Rate pAddTaxRate) {
			theAddTaxRate = pAddTaxRate; }
		public void setAddDivTaxRate(Rate pAddDivTaxRate) {
			theAddDivTaxRate = pAddDivTaxRate; }
		public void setCapTaxRate(Rate pCapTaxRate) {
			theCapTaxRate = pCapTaxRate; }
		public void setHiCapTaxRate(Rate pHiCapTaxRate) {
			theHiCapTaxRate = pHiCapTaxRate; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) {
			theYear    		 = pValues.getYear();
			theTaxRegime     = pValues.getTaxRegime();
			theAllowance     = pValues.getAllowance();
			theRentalAllow   = pValues.getRentalAllow();
			theLoBand        = pValues.getLoBand();
			theBasicBand     = pValues.getBasicBand();
			theCapitalAllow  = pValues.getCapitalAllow();
			theLoAgeAllow    = pValues.getLoAgeAllow();
			theHiAgeAllow    = pValues.getHiAgeAllow();
			theAgeAllowLimit = pValues.getAgeAllowLimit();
			theAddAllowLimit = pValues.getAddAllowLimit();
			theAddIncBound	 = pValues.getAddIncBound();
			theLoTaxRate     = pValues.getLoTaxRate();
			theBasicTaxRate  = pValues.getBasicTaxRate();
			theHiTaxRate     = pValues.getHiTaxRate();
			theIntTaxRate    = pValues.getIntTaxRate();
			theDivTaxRate    = pValues.getDivTaxRate();
			theHiDivTaxRate  = pValues.getHiDivTaxRate();			
			theAddTaxRate    = pValues.getAddTaxRate();			
			theAddDivTaxRate = pValues.getAddDivTaxRate();			
			theCapTaxRate    = pValues.getCapTaxRate();			
			theHiCapTaxRate  = pValues.getHiCapTaxRate();			
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (Date.differs(theYear,     			pValues.theYear))     	   return false;
			if (TaxRegime.differs(theTaxRegime,     pValues.theTaxRegime))     return false;
			if (Money.differs(theAllowance,     	pValues.theAllowance))     return false;
			if (Money.differs(theRentalAllow,   	pValues.theRentalAllow))   return false;
			if (Money.differs(theLoBand,       		pValues.theLoBand))        return false;
			if (Money.differs(theBasicBand,     	pValues.theBasicBand))     return false;
			if (Money.differs(theCapitalAllow,  	pValues.theCapitalAllow))  return false;
			if (Money.differs(theLoAgeAllow,    	pValues.theLoAgeAllow))    return false;
			if (Money.differs(theHiAgeAllow,    	pValues.theHiAgeAllow))    return false;
			if (Money.differs(theAgeAllowLimit, 	pValues.theAgeAllowLimit)) return false;
			if (Money.differs(theAddAllowLimit, 	pValues.theAddAllowLimit)) return false;
			if (Money.differs(theAddIncBound,   	pValues.theAddIncBound))   return false;
			if (Rate.differs(theLoTaxRate,   		pValues.theLoTaxRate))     return false;
			if (Rate.differs(theBasicTaxRate,		pValues.theBasicTaxRate))  return false;
			if (Rate.differs(theHiTaxRate,   		pValues.theHiTaxRate))     return false;
			if (Rate.differs(theIntTaxRate,  		pValues.theIntTaxRate))    return false;
			if (Rate.differs(theDivTaxRate,  		pValues.theDivTaxRate))    return false;
			if (Rate.differs(theHiDivTaxRate,		pValues.theHiDivTaxRate))  return false;
			if (Rate.differs(theAddTaxRate,  		pValues.theAddTaxRate))    return false;
			if (Rate.differs(theAddDivTaxRate, 		pValues.theAddDivTaxRate)) return false;
			if (Rate.differs(theCapTaxRate,    		pValues.theCapTaxRate))    return false;
			if (Rate.differs(theHiCapTaxRate,  		pValues.theHiCapTaxRate))  return false;
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
			theYear    		 = pValues.getYear();
			theTaxRegime     = pValues.getTaxRegime();
			theAllowance     = pValues.getAllowance();
			theRentalAllow   = pValues.getRentalAllow();
			theLoBand        = pValues.getLoBand();
			theBasicBand     = pValues.getBasicBand();
			theCapitalAllow  = pValues.getCapitalAllow();
			theLoAgeAllow    = pValues.getLoAgeAllow();
			theHiAgeAllow    = pValues.getHiAgeAllow();
			theAgeAllowLimit = pValues.getAgeAllowLimit();
			theAddAllowLimit = pValues.getAddAllowLimit();
			theAddIncBound	 = pValues.getAddIncBound();
			theLoTaxRate     = pValues.getLoTaxRate();
			theBasicTaxRate  = pValues.getBasicTaxRate();
			theHiTaxRate     = pValues.getHiTaxRate();
			theIntTaxRate    = pValues.getIntTaxRate();
			theDivTaxRate    = pValues.getDivTaxRate();
			theHiDivTaxRate  = pValues.getHiDivTaxRate();						
			theAddTaxRate    = pValues.getAddTaxRate();						
			theAddDivTaxRate = pValues.getAddDivTaxRate();						
			theCapTaxRate    = pValues.getCapTaxRate();						
			theHiCapTaxRate  = pValues.getHiCapTaxRate();						
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case FIELD_YEAR:
					bResult = (Date.differs(theYear,  			pValues.theYear));
					break;
				case FIELD_REGIME:
					bResult = (TaxRegime.differs(theTaxRegime,  pValues.theTaxRegime));
					break;
				case FIELD_ALLOW:
					bResult = (Money.differs(theAllowance,  	pValues.theAllowance));
					break;
				case FIELD_RENTAL:
					bResult = (Money.differs(theRentalAllow,  	pValues.theRentalAllow));
					break;
				case FIELD_LOBAND:
					bResult = (Money.differs(theLoBand,       	pValues.theLoBand));
					break;
				case FIELD_BSBAND:
					bResult = (Money.differs(theBasicBand,    	pValues.theBasicBand));
					break;
				case FIELD_CAPALW:
					bResult = (Money.differs(theCapitalAllow, 	pValues.theCapitalAllow));
					break;
				case FIELD_LOAGAL:
					bResult = (Money.differs(theLoAgeAllow, 	pValues.theLoAgeAllow));
					break;
				case FIELD_HIAGAL:
					bResult = (Money.differs(theHiAgeAllow, 	pValues.theHiAgeAllow));
					break;
				case FIELD_AGELMT:
					bResult = (Money.differs(theAgeAllowLimit, 	pValues.theAgeAllowLimit));
					break;
				case FIELD_ADDLMT:
					bResult = (Money.differs(theAddAllowLimit, 	pValues.theAddAllowLimit));
					break;
				case FIELD_ADDBDY:
					bResult = (Money.differs(theAddIncBound, 	pValues.theAddIncBound));
					break;
				case FIELD_LOTAX:
					bResult = (Rate.differs(theLoTaxRate,  		pValues.theLoTaxRate));
					break;
				case FIELD_BASTAX:
					bResult = (Rate.differs(theBasicTaxRate, 	pValues.theBasicTaxRate));
					break;
				case FIELD_HITAX:
					bResult = (Rate.differs(theHiTaxRate,  		pValues.theHiTaxRate));
					break;
				case FIELD_INTTAX:
					bResult = (Rate.differs(theIntTaxRate, 		pValues.theIntTaxRate));
					break;
				case FIELD_DIVTAX:
					bResult = (Rate.differs(theDivTaxRate, 		pValues.theDivTaxRate));
					break;
				case FIELD_HDVTAX:
					bResult = (Rate.differs(theHiDivTaxRate, 	pValues.theHiDivTaxRate));
					break;
				case FIELD_ADDTAX:
					bResult = (Rate.differs(theAddTaxRate, 		pValues.theAddTaxRate));
					break;
				case FIELD_ADVTAX:
					bResult = (Rate.differs(theAddDivTaxRate, 	pValues.theAddDivTaxRate));
					break;
				case FIELD_CAPTAX:
					bResult = (Rate.differs(theCapTaxRate, 		pValues.theCapTaxRate));
					break;
				case FIELD_HCPTAX:
					bResult = (Rate.differs(theHiCapTaxRate, 	pValues.theHiCapTaxRate));
					break;
			}
			return bResult;
		}
	}
}
