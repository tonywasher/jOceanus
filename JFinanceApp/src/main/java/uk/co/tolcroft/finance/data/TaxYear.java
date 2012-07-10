/*******************************************************************************
 * Copyright 2012 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package uk.co.tolcroft.finance.data;

import java.util.Calendar;
import java.util.Date;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.*;
import uk.co.tolcroft.models.Decimal.*;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.help.DebugDetail;

/**
 * Tax Year Class representing taxation parameters for a tax year
 * @author Tony Washer
 */
public class TaxYear extends DataItem<TaxYear> {
	/**
	 * The name of the object
	 */
	public static final String objName = "TaxYear";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";

	/* Access methods */
	public  DateDay  	getDate()         			{ return getValues().getYear(); }	
	public  Values   	getValues()          		{ return (Values)super.getCurrentValues(); }	
	public  TaxRegime 	getTaxRegime()				{ return getValues().getTaxRegime(); }
	public  Money 		getAllowance()    			{ return getValues().getAllowance(); }
	public  Money 		getRentalAllowance() 		{ return getValues().getRentalAllow(); }
	public  Money 		getLoBand()       			{ return getValues().getLoBand(); }
	public  Money 		getBasicBand()    			{ return getValues().getBasicBand(); }
	public  Money 		getCapitalAllow() 			{ return getValues().getCapitalAllow(); }
	public  Money 		getLoAgeAllow()   			{ return getValues().getLoAgeAllow(); }
	public  Money 		getHiAgeAllow()   			{ return getValues().getHiAgeAllow(); }
	public  Money 		getAgeAllowLimit()			{ return getValues().getAgeAllowLimit(); }
	public  Money 		getAddAllowLimit()			{ return getValues().getAddAllowLimit(); }
	public  Money 		getAddIncBound()  			{ return getValues().getAddIncBound(); }
	public  Rate 		getLoTaxRate()    			{ return getValues().getLoTaxRate(); }
	public  Rate 		getBasicTaxRate() 			{ return getValues().getBasicTaxRate(); }
	public  Rate 		getHiTaxRate()    			{ return getValues().getHiTaxRate(); }
	public  Rate 		getIntTaxRate()   			{ return getValues().getIntTaxRate(); }
	public  Rate 		getDivTaxRate()   			{ return getValues().getDivTaxRate(); }
	public  Rate 		getHiDivTaxRate() 			{ return getValues().getHiDivTaxRate(); }
	public  Rate 		getAddTaxRate()   			{ return getValues().getAddTaxRate(); }
	public  Rate 		getAddDivTaxRate()			{ return getValues().getAddDivTaxRate(); }
	public  Rate 		getCapTaxRate()   			{ return getValues().getCapTaxRate(); }
	public  Rate 		getHiCapTaxRate() 			{ return getValues().getHiCapTaxRate(); }
	public  boolean     hasLoSalaryBand() 			{ return getTaxRegime().hasLoSalaryBand(); }
	public  boolean     hasAdditionalTaxBand() 		{ return getTaxRegime().hasAdditionalTaxBand(); }
	public  boolean     hasCapitalGainsAsIncome() 	{ return getTaxRegime().hasCapitalGainsAsIncome(); }
	public  void        setDate(DateDay pDate) 		{ getValues().setYear(pDate); }

	/* Linking methods */
	public TaxYear    	getBase() { return (TaxYear)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_REGIME  		= DataItem.NUMFIELDS;
	public static final int FIELD_YEAR   		= DataItem.NUMFIELDS+1;
	public static final int FIELD_RENTAL 		= DataItem.NUMFIELDS+2;
	public static final int FIELD_ALLOW  		= DataItem.NUMFIELDS+3;
	public static final int FIELD_LOAGAL 		= DataItem.NUMFIELDS+4;
	public static final int FIELD_HIAGAL 		= DataItem.NUMFIELDS+5;
	public static final int FIELD_LOBAND 		= DataItem.NUMFIELDS+6;
	public static final int FIELD_BSBAND 		= DataItem.NUMFIELDS+7;
	public static final int FIELD_CAPALW 		= DataItem.NUMFIELDS+8;
	public static final int FIELD_AGELMT 		= DataItem.NUMFIELDS+9;
	public static final int FIELD_ADDLMT 		= DataItem.NUMFIELDS+10;
	public static final int FIELD_ADDBDY 		= DataItem.NUMFIELDS+11;
	public static final int FIELD_LOTAX  		= DataItem.NUMFIELDS+12;
	public static final int FIELD_BASTAX 		= DataItem.NUMFIELDS+13;
	public static final int FIELD_HITAX  		= DataItem.NUMFIELDS+14;
	public static final int FIELD_INTTAX 		= DataItem.NUMFIELDS+15;
	public static final int FIELD_DIVTAX 		= DataItem.NUMFIELDS+16;
	public static final int FIELD_HDVTAX 		= DataItem.NUMFIELDS+17;
	public static final int FIELD_ADDTAX 		= DataItem.NUMFIELDS+18;
	public static final int FIELD_ADVTAX 		= DataItem.NUMFIELDS+19;
	public static final int FIELD_CAPTAX 		= DataItem.NUMFIELDS+20;
	public static final int FIELD_HCPTAX 		= DataItem.NUMFIELDS+21;
	public static final int NUMFIELDS	    	= DataItem.NUMFIELDS+22;
	
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
			default:		  	return DataItem.fieldName(iField);
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
	public String formatField(DebugDetail pDetail, int iField, HistoryValues<TaxYear> pValues) {
		String 	myString = "";
		Values 	myValues = (Values)pValues;
		switch (iField) {
			case FIELD_REGIME:
				if ((myValues.getTaxRegime() == null) &&
					(myValues.getTaxRegimeId() != null))
					myString += "Id=" + myValues.getTaxRegimeId();
				else
					myString += TaxRegime.format(myValues.getTaxRegime()); 
				myString = pDetail.addDebugLink(myValues.getTaxRegime(), myString);
				break;
			case FIELD_YEAR:	
				myString += DateDay.format(getDate()); 
				break;
			case FIELD_ALLOW:	
				myString += Money.format(myValues.getAllowance()); 
				break;
			case FIELD_LOBAND: 	
				myString += Money.format(myValues.getLoBand());	
				break;
			case FIELD_BSBAND:	
				myString += Money.format(myValues.getBasicBand()); 
				break;
			case FIELD_RENTAL:	
				myString += Money.format(myValues.getRentalAllow()); 
				break;
			case FIELD_LOTAX: 	
				myString += Rate.format(myValues.getLoTaxRate());	
				break;
			case FIELD_BASTAX: 	
				myString += Rate.format(myValues.getBasicTaxRate());	
				break;
			case FIELD_HITAX: 	
				myString += Rate.format(myValues.getHiTaxRate());	
				break;
			case FIELD_INTTAX: 	
				myString += Rate.format(myValues.getIntTaxRate());	
				break;
			case FIELD_DIVTAX: 	
				myString += Rate.format(myValues.getDivTaxRate());	
				break;
			case FIELD_HDVTAX: 	
				myString += Rate.format(myValues.getHiDivTaxRate());	
				break;
			case FIELD_ADDTAX: 	
				myString += Rate.format(myValues.getAddTaxRate());	
				break;
			case FIELD_ADVTAX: 	
				myString += Rate.format(myValues.getAddDivTaxRate());	
				break;
			case FIELD_LOAGAL: 	
				myString += Money.format(myValues.getLoAgeAllow());	
				break;
			case FIELD_HIAGAL: 	
				myString += Money.format(myValues.getHiAgeAllow());	
				break;
			case FIELD_AGELMT: 	
				myString += Money.format(myValues.getAgeAllowLimit());	
				break;
			case FIELD_ADDLMT: 	
				myString += Money.format(myValues.getAddAllowLimit());	
				break;
			case FIELD_ADDBDY: 	
				myString += Money.format(myValues.getAddIncBound());	
				break;
			case FIELD_CAPALW: 	
				myString += Money.format(myValues.getCapitalAllow());	
				break;
			case FIELD_CAPTAX: 	
				myString += Rate.format(myValues.getCapTaxRate());	
				break;
			case FIELD_HCPTAX: 	
				myString += Rate.format(myValues.getHiCapTaxRate());	
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
	protected HistoryValues<TaxYear> getNewValues() { return new Values(); }
	
	/**
	 * Construct a copy of a TaxYear
	 * 
	 * @param pList The List to build into 
	 * @param pTaxYear The TaxYear to copy 
	 */
	public TaxYear(List pList, TaxYear pTaxYear) { 
		super(pList, pTaxYear.getId());
		Values myValues  = getValues();
		myValues.copyFrom(pTaxYear.getValues());
		ListStyle myOldStyle = pTaxYear.getStyle();

		/* Switch on the ListStyle */
		switch (getStyle()) {
			case EDIT:
				/* If this is a view creation */
				if (myOldStyle == ListStyle.CORE) {
					/* TaxYear is based on the original element */
					setBase(pTaxYear);
					copyFlags(pTaxYear);
					pList.setNewId(this);				
					break;
				}
				
				/* Else this is a duplication so treat as new item */
				setId(0);
				pList.setNewId(this);				
				break;
			case CLONE:
				reBuildLinks(pList.getData());
			case COPY:
			case CORE:
				/* Reset Id if this is an insert from a view */
				if (myOldStyle == ListStyle.EDIT) setId(0);
				pList.setNewId(this);				
				break;
			case UPDATE:
				setBase(pTaxYear);
				setState(pTaxYear.getState());
				break;
		}
	}
	
	/* Standard constructor */
	private TaxYear(List    pList,
			        int     uId,
			        int		uRegimeId,
			        Date  	pDate,
			        String 	pAllowance,
			        String 	pRentalAllow,
			        String 	pLoAgeAllow,
			        String 	pHiAgeAllow,
			        String	pCapAllow,
			        String 	pAgeAllowLimit,
			        String 	pAddAllowLimit,
			        String 	pLoTaxBand,
			        String 	pBasicTaxBand,
			        String 	pAddIncBound,
			        String	pLoTaxRate,
			        String	pBasicTaxRate,
			        String	pHiTaxRate,
			        String	pIntTaxRate,
			        String	pDivTaxRate,
			        String	pHiDivTaxRate,
			        String	pAddTaxRate,
			        String	pAddDivTaxRate,
			        String	pCapTaxRate,
			        String	pHiCapTaxRate) throws ModelException {
		/* Initialise item */
		super(pList, uId);
		
		/* Local variable */
		TaxRegime myRegime;
		
		/* Initialise values */
		Values myValues  = getValues();

		/* Record the Id */
		myValues.setTaxRegimeId(uRegimeId);
		myValues.setYear(new DateDay(pDate));
		
		/* Look up the Regime */
		myRegime = pList.theData.getTaxRegimes().searchFor(uRegimeId);
		if (myRegime == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid Tax Regime Id");
		myValues.setTaxRegime(myRegime);
					
		/* Record the allowances */
		Money myMoney = Money.Parse(pAllowance);
		if (myMoney == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid Allowance: " + pAllowance);
		myValues.setAllowance(myMoney);
		myMoney = Money.Parse(pLoTaxBand);
		if (myMoney == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid Low Tax Band: " + pLoTaxBand);
		myValues.setLoBand(myMoney);
		myMoney = Money.Parse(pBasicTaxBand);
		if (myMoney == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid Basic Tax Band: " + pBasicTaxBand);
		myValues.setBasicBand(myMoney);
		myMoney = Money.Parse(pRentalAllow);
		if (myMoney == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid Rental Allowance: " + pRentalAllow);
		myValues.setRentalAllow(myMoney);
		myMoney = Money.Parse(pLoAgeAllow);
		if (myMoney == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid Low Age Allowance: " + pLoAgeAllow);
		myValues.setLoAgeAllow(myMoney);	
		myMoney = Money.Parse(pHiAgeAllow);
		if (myMoney == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid High Age Allowance: " + pHiAgeAllow);
		myValues.setHiAgeAllow(myMoney);	
		myMoney = Money.Parse(pCapAllow);
		if (myMoney == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid Capital Allowance: " + pHiAgeAllow);
		myValues.setCapitalAllow(myMoney);	
		myMoney = Money.Parse(pAgeAllowLimit);
		if (myMoney == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid Age Allowance Limit: " + pAgeAllowLimit);
		myValues.setAgeAllowLimit(myMoney);	
		if (pAddAllowLimit != null) {
			myMoney = Money.Parse(pAddAllowLimit);
			if (myMoney == null) 
				throw new ModelException(ExceptionClass.DATA,
									this,
									"Invalid Additional Allowance Limit: " + pAddAllowLimit);
			myValues.setAddAllowLimit(myMoney);	
		}
		if (pAddIncBound != null) {
			myMoney = Money.Parse(pAddIncBound);
			if (myMoney == null) 
				throw new ModelException(ExceptionClass.DATA,
									this,
									"Invalid Additional Income Boundary: " + pAddIncBound);
			myValues.setAddIncBound(myMoney);	
		}

		/* Record the rates */
		Rate myRate = Rate.Parse(pLoTaxRate);
		if (myRate == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid Low Tax Rate: " + pLoTaxRate);
		myValues.setLoTaxRate(myRate);
		myRate = Rate.Parse(pBasicTaxRate);
		if (myRate == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid Basic Tax Rate: " + pBasicTaxRate);
		myValues.setBasicTaxRate(myRate);
		myRate = Rate.Parse(pHiTaxRate);
		if (myRate == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid High Tax Rate: " + pHiTaxRate);
		myValues.setHiTaxRate(myRate);
		myRate = Rate.Parse(pIntTaxRate);
		if (myRate == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid Int Tax Rate: " + pIntTaxRate);
		myValues.setIntTaxRate(myRate);
		myRate = Rate.Parse(pDivTaxRate);
		if (myRate == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid Div Tax Rate: " + pDivTaxRate);
		myValues.setDivTaxRate(myRate);
		myRate = Rate.Parse(pHiDivTaxRate);
		if (myRate == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid High Div Tax Rate: " + pHiDivTaxRate);
		myValues.setHiDivTaxRate(myRate);
		if (pAddTaxRate != null) {
			myRate = Rate.Parse(pAddTaxRate);
			if (myRate == null) 
				throw new ModelException(ExceptionClass.DATA,
									this,
									"Invalid Additional Tax Rate: " + pAddTaxRate);
			myValues.setAddTaxRate(myRate);
		}
		if (pAddDivTaxRate != null) {
			myRate = Rate.Parse(pAddDivTaxRate);
			if (myRate == null) 
				throw new ModelException(ExceptionClass.DATA,
									this,
									"Invalid Additional Div Tax Rate: " + pAddDivTaxRate);
			myValues.setAddDivTaxRate(myRate);
		}
		if (pCapTaxRate != null) {
			myRate = Rate.Parse(pCapTaxRate);
			if (myRate == null) 
				throw new ModelException(ExceptionClass.DATA,
									this,
									"Invalid Capital Gains Tax Rate: " + pCapTaxRate);
			myValues.setCapTaxRate(myRate);
		}
		if (pHiCapTaxRate != null) {
			myRate = Rate.Parse(pHiCapTaxRate);
			if (myRate == null) 
				throw new ModelException(ExceptionClass.DATA,
									this,
									"Invalid High Capital Gains Tax Rate: " + pHiCapTaxRate);
			myValues.setHiCapTaxRate(myRate);
		}
		
		/* Allocate the id */
		pList.setNewId(this);				
	}
		
	/* Standard constructor for a newly inserted account */
	public TaxYear(List pList) {
		super(pList, 0);
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
		TaxYear myThat = (TaxYear)pThat;
		
		/* Check for equality on id */
		if (getId() != myThat.getId())	return false;
		
		/* Compare the changeable values */
		return getValues().histEquals(myThat.getValues()).isIdentical();
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
	 * Rebuild Links to partner data
	 * @param pData the DataSet
	 */
	private void reBuildLinks(FinanceData pData) {
		TaxRegime.List myRegimes = pData.getTaxRegimes();
		
		/* Update to use the local copy of the TaxRegimes */
		Values 		myValues   	= getValues();
		TaxRegime 	myRegime	= myValues.getTaxRegime();
		TaxRegime 	myNewReg 	= myRegimes.searchFor(myRegime.getId());
		myValues.setTaxRegime(myNewReg);
	}

	/**
	 * Validate the taxYear
	 */
	public void validate() {
		DateDay myDate = getDate();
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
		else if (!getTaxRegime().getEnabled()) 
			addError("TaxRegime must be enabled", FIELD_REGIME);
			
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
	public DateDay.Range getRange() {
		DateDay  		myStart;
		DateDay  		myEnd;
		DateDay.Range 	myRange;
		
		/* Access start date */
		myStart =  new DateDay(getDate());
		
		/* Move back to start of year */
		myStart.adjustYear(-1);
		myStart.adjustDay(1);
		
		/* Access last date */
		myEnd  = getDate();
		
		/* Create the range */
		myRange = new DateDay.Range(myStart, myEnd);
		
		/* Return the range */
		return myRange;
	}
	
	/**
	 * Set a new tax regime 
	 * 
	 * @param pTaxRegime the TaxRegime 
	 */
	public void setTaxRegime(TaxRegime pTaxRegime) {
		getValues().setTaxRegime(pTaxRegime);
	}
	
	/**
	 * Set a new allowance 
	 * 
	 * @param pAllowance the allowance 
	 */
	public void setAllowance(Money pAllowance) {
		getValues().setAllowance((pAllowance == null) ? null 
												   	  : new Money(pAllowance));
	}
	
	/**
	 * Set a new rental allowance 
	 * 
	 * @param pAllowance the allowance 
	 */
	public void setRentalAllowance(Money pAllowance) {
		getValues().setRentalAllow((pAllowance == null) ? null 
								  				        : new Money(pAllowance));
	}
	
	/**
	 * Set a new capital allowance 
	 * 
	 * @param pAllowance the allowance 
	 */
	public void setCapitalAllow(Money pAllowance) {
		getValues().setCapitalAllow((pAllowance == null) ? null 
								  				         : new Money(pAllowance));
	}
	
	/**
	 * Set a new Low Tax Band 
	 * 
	 * @param pLoBand the Low Tax Band 
	 */
	public void setLoBand(Money pLoBand) {
		getValues().setLoBand((pLoBand == null) ? null : new Money(pLoBand));
	}
	
	/**
	 * Set a new Basic Tax Band 
	 * 
	 * @param pBasicBand the Basic Tax Band 
	 */
	public void setBasicBand(Money pBasicBand) {
		getValues().setBasicBand((pBasicBand == null) ? null : new Money(pBasicBand));
	}
	
	/**
	 * Set a new Low Age Allowance 
	 * 
	 * @param pLoAgeAllow the Low Age Allowance 
	 */
	public void setLoAgeAllow(Money pLoAgeAllow) {
		getValues().setLoAgeAllow((pLoAgeAllow == null) ? null : new Money(pLoAgeAllow));
	}
	
	/**
	 * Set a new High Age Allowance 
	 * 
	 * @param pHiAgeAllow the High Age Allowance 
	 */
	public void setHiAgeAllow(Money pHiAgeAllow) {
		getValues().setHiAgeAllow((pHiAgeAllow == null) ? null : new Money(pHiAgeAllow));
	}
	
	/**
	 * Set a new Age Allowance Limit
	 * 
	 * @param pAgeAllowLimit the Age Allowance Limit
	 */
	public void setAgeAllowLimit(Money pAgeAllowLimit) {
		getValues().setAgeAllowLimit((pAgeAllowLimit == null) ? null : new Money(pAgeAllowLimit));
	}
	
	/**
	 * Set a new Additional Allowance Limit
	 * 
	 * @param pAddAllowLimit the Additional Allowance Limit
	 */
	public void setAddAllowLimit(Money pAddAllowLimit) {
		getValues().setAddAllowLimit((pAddAllowLimit == null) ? null : new Money(pAddAllowLimit));
	}
	
	/**
	 * Set a new Additional Income Boundary
	 * 
	 * @param pAddIncBound the Additional Income Boundary
	 */
	public void setAddIncBound(Money pAddIncBound) {
		getValues().setAddIncBound((pAddIncBound == null) ? null : new Money(pAddIncBound));
	}
	
	/**
	 * Set a new Low Tax Rate 
	 * 
	 * @param pRate the Low Tax Rate 
	 */
	public void setLoTaxRate(Rate pRate) {
		getValues().setLoTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new Basic tax rate
	 * 
	 * @param pRate the Basic tax rate 
	 */
	public void setBasicTaxRate(Rate pRate) {
		getValues().setBasicTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new high tax rate 
	 * 
	 * @param pRate the high tax rate 
	 */
	public void setHiTaxRate(Rate pRate) {
		getValues().setHiTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new Interest Tax Rate 
	 * 
	 * @param pRate the Interest Tax Rate 
	 */
	public void setIntTaxRate(Rate pRate) {
		getValues().setIntTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new Dividend tax rate
	 * 
	 * @param pRate the Dividend tax rate 
	 */
	public void setDivTaxRate(Rate pRate) {
		getValues().setDivTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new high dividend tax rate 
	 * 
	 * @param pRate the high dividend tax rate 
	 */
	public void setHiDivTaxRate(Rate pRate) {
		getValues().setHiDivTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new additional tax rate 
	 * 
	 * @param pRate the additional tax rate 
	 */
	public void setAddTaxRate(Rate pRate) {
		getValues().setAddTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new additional dividend tax rate 
	 * 
	 * @param pRate the additional dividend tax rate 
	 */
	public void setAddDivTaxRate(Rate pRate) {
		getValues().setAddDivTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a new capital tax rate 
	 * 
	 * @param pRate the capital tax rate 
	 */
	public void setCapTaxRate(Rate pRate) {
		getValues().setCapTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Set a high capital tax rate 
	 * 
	 * @param pRate the additional dividend tax rate 
	 */
	public void setHiCapTaxRate(Rate pRate) {
		getValues().setHiCapTaxRate((pRate == null) ? null : new Rate(pRate));
	}
	
	/**
	 * Update taxYear from a taxYear extract 
	 * @param pTaxYear the changed taxYear 
	 * @return whether changes have been made
	 */
	public boolean applyChanges(DataItem<?> pTaxYear) {
		TaxYear myTaxYear = (TaxYear)pTaxYear;
		boolean bChanged  = false;
		
		/* Store the current detail into history */
		pushHistory();
		
		/* Update the tax regime if required */
		if (TaxRegime.differs(getTaxRegime(), myTaxYear.getTaxRegime()).isDifferent())
			setTaxRegime(myTaxYear.getTaxRegime());
	
		/* Update the allowance if required */
		if (Money.differs(getAllowance(), myTaxYear.getAllowance()).isDifferent())
			setAllowance(myTaxYear.getAllowance());
	
		/* Update the rental allowance if required */
		if (Money.differs(getRentalAllowance(), myTaxYear.getRentalAllowance()).isDifferent())
			setRentalAllowance(myTaxYear.getRentalAllowance());
	
		/* Update the Low band if required */
		if (Money.differs(getLoBand(), myTaxYear.getLoBand()).isDifferent())
			setLoBand(myTaxYear.getLoBand());
			
		/* Update the basic band if required */
		if (Money.differs(getBasicBand(), myTaxYear.getBasicBand()).isDifferent())
			setBasicBand(myTaxYear.getBasicBand());
		
		/* Update the low age allowance if required */
		if (Money.differs(getLoAgeAllow(), myTaxYear.getLoAgeAllow()).isDifferent())
			setLoAgeAllow(myTaxYear.getLoAgeAllow());
		
		/* Update the high age allowance if required */
		if (Money.differs(getHiAgeAllow(), myTaxYear.getHiAgeAllow()).isDifferent())
			setHiAgeAllow(myTaxYear.getHiAgeAllow());
		
		/* Update the age allowance limit if required */
		if (Money.differs(getAgeAllowLimit(), myTaxYear.getAgeAllowLimit()).isDifferent())
			setAgeAllowLimit(myTaxYear.getAgeAllowLimit());
		
		/* Update the additional allowance limit if required */
		if (Money.differs(getAddAllowLimit(), myTaxYear.getAddAllowLimit()).isDifferent())
			setAddAllowLimit(myTaxYear.getAddAllowLimit());
		
		/* Update the additional income boundary if required */
		if (Money.differs(getAddIncBound(), myTaxYear.getAddIncBound()).isDifferent())
			setAddIncBound(myTaxYear.getAddIncBound());
		
		/* Update the Low tax rate if required */
		if (Rate.differs(getLoTaxRate(), myTaxYear.getLoTaxRate()).isDifferent())
			setLoTaxRate(myTaxYear.getLoTaxRate());
		
		/* Update the standard tax rate if required */
		if (Rate.differs(getBasicTaxRate(), myTaxYear.getBasicTaxRate()).isDifferent())
			setBasicTaxRate(myTaxYear.getBasicTaxRate());
						
		/* Update the high tax rate if required */
		if (Rate.differs(getHiTaxRate(), myTaxYear.getHiTaxRate()).isDifferent())
			setHiTaxRate(myTaxYear.getHiTaxRate());
						
		/* Update the interest tax rate if required */
		if (Rate.differs(getIntTaxRate(), myTaxYear.getIntTaxRate()).isDifferent())
			setIntTaxRate(myTaxYear.getIntTaxRate());
		
		/* Update the dividend tax rate if required */
		if (Rate.differs(getDivTaxRate(), myTaxYear.getDivTaxRate()).isDifferent())
			setDivTaxRate(myTaxYear.getDivTaxRate());
						
		/* Update the high dividend rate if required */
		if (Rate.differs(getHiDivTaxRate(), myTaxYear.getHiDivTaxRate()).isDifferent())
			setHiDivTaxRate(myTaxYear.getHiDivTaxRate());
		
		/* Update the additional rate if required */
		if (Rate.differs(getAddTaxRate(), myTaxYear.getAddTaxRate()).isDifferent())
			setAddTaxRate(myTaxYear.getAddTaxRate());
		
		/* Update the additional dividend rate if required */
		if (Rate.differs(getAddDivTaxRate(), myTaxYear.getAddDivTaxRate()).isDifferent())
			setAddDivTaxRate(myTaxYear.getAddDivTaxRate());
		
		/* Update the capital rate if required */
		if (Rate.differs(getCapTaxRate(), myTaxYear.getCapTaxRate()).isDifferent())
			setCapTaxRate(myTaxYear.getCapTaxRate());
		
		/* Update the high capital rate if required */
		if (Rate.differs(getHiCapTaxRate(), myTaxYear.getHiCapTaxRate()).isDifferent())
			setHiCapTaxRate(myTaxYear.getHiCapTaxRate());
		
		/* Check for changes */
		if (checkForHistory()) {
			/* Mark as changed */
			setState(DataState.CHANGED);
			bChanged = true;
		}
		
		/* Return to caller */
		return bChanged;
	}

	/* The Tax Year List class */
	public static class List extends DataList<List, TaxYear> {		
		private FinanceData	theData			= null;
		private TaxYear		theNewYear		= null;
		public 	FinanceData getData()		{ return theData; }
		public 	TaxYear		getNewYear()	{ return theNewYear; }

		/** 
	 	 * Construct an empty CORE TaxYear list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(FinanceData pData) { 
			super(List.class, TaxYear.class, ListStyle.CORE, false);
			theData = pData;
		}

		/**
		 * Constructor for a cloned List
		 * @param pSource the source List
		 */
		private List(List pSource) { 
			super(pSource);
			theData = pSource.theData;
		}
		
		/**
		 * Construct an update extract for the List.
		 * @return the update Extract
		 */
		private List getExtractList(ListStyle pStyle) {
			/* Build an empty Extract List */
			List myList = new List(this);
			
			/* Obtain underlying updates */
			myList.populateList(pStyle);
			
			/* Return the list */
			return myList;
		}

		/* Obtain extract lists. */
		public List getUpdateList() { return getExtractList(ListStyle.UPDATE); }
		public List getEditList() 	{ return null; }
		public List getShallowCopy() 	{ return getExtractList(ListStyle.COPY); }
		public List getDeepCopy(DataSet<?> pDataSet)	{ 
			/* Build an empty Extract List */
			List myList = new List(this);
			myList.theData = (FinanceData)pDataSet;
			
			/* Obtain underlying clones */
			myList.populateList(ListStyle.CLONE);
			myList.setStyle(ListStyle.CORE);
			
			/* Return the list */
			return myList;
		}

		/** 
		 * Construct a difference ControlData list
		 * @param pNew the new ControlData list 
		 * @param pOld the old ControlData list 
		 */
		protected List getDifferences(List pOld) { 
			/* Build an empty Difference List */
			List myList = new List(this);
			
			/* Calculate the differences */
			myList.getDifferenceList(this, pOld);
			
			/* Return the list */
			return myList;
		}

		/**
		 * Construct an edit extract for a TaxYear.
		 * @return the edit Extract
		 */
		public List getEditList(TaxYear pTaxYear) {
			/* Build an empty List */
			List myList = new List(this);
			
			/* Make this list the correct style */
			myList.setStyle(ListStyle.EDIT);
			
			/* Create a new tax year based on the passed tax year */
			TaxYear myYear = new TaxYear(myList, pTaxYear);
			myList.add(myYear);
			
			/* Return the List */
			return myList;
		}
		
		/* Constructor */
		public List getNewEditList() {
			/* Build an empty List */
			List myList = new List(this);
			
			/* Make this list the correct style */
			myList.setStyle(ListStyle.EDIT);
			
			/* Local Variables */
			TaxYear.List 				myTaxYears;
			TaxYear    					myBase;
			DataListIterator<TaxYear> 	myIterator;
			
			/* Access the existing tax years */
			myTaxYears = theData.getTaxYears();
			myIterator = myTaxYears.listIterator(true);
			
			/* Create a new tax year for the list */
			myBase = myIterator.peekLast();
			TaxYear myYear = new TaxYear(myList, myBase);
			myYear.setBase(null);
			myYear.setState(DataState.NEW);
			myYear.setId(0);
						
			/* Adjust the year and add to list */
			myYear.setDate(new DateDay(myBase.getDate()));
			myYear.getDate().adjustYear(1);
			myList.add(myYear);
			
			/* Record the new year */
			myList.theNewYear = myYear;
			
			/* Return the List */
			return myList;
		}
				
		/**
		 * Add a new item to the core list
		 * @param pTaxYear item
		 * @return the newly added item
		 */
		public TaxYear addNewItem(DataItem<?> pTaxYear) {
			TaxYear myYear = new TaxYear(this, (TaxYear)pTaxYear);
			add(myYear);
			return myYear;
		}
	
		/**
		 * Create a new empty element in the edit list (null-operation)
		 * @return the newly added item
		 */
		public TaxYear addNewItem() { return null; }
			
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }
				
		/**
		 * Search for the tax year that encompasses this date
		 * 
		 * @param pDate Date of item
		 * @return The TaxYear if present (or null)
		 */
		public TaxYear searchFor(DateDay pDate) {
			DataListIterator<TaxYear> 	myIterator;
			TaxYear   					myCurr;
			DateDay.Range				myRange;
			int        					iDiff;
			
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
			DataListIterator<TaxYear> 	myIterator;
			TaxYear   					myCurr;
			boolean    					isMatch;
			long	   					uYear;
			
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
		protected int countInstances(DateDay pDate) {
			DataListIterator<TaxYear> 	myIterator;
			TaxYear 					myCurr;
			int      					iDiff;
			int      					iCount = 0;
			
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
		 * Extract the date range represented by the tax years
		 * 
		 * @return the range of tax years
		 */
		public DateDay.Range getRange() {
			DataListIterator<TaxYear> 	myIterator;
			TaxYear     				myCurr;
			DateDay  					myStart;
			DateDay  					myEnd;
			DateDay.Range 				myRange;
			
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
				myStart =  new DateDay(myCurr.getDate());
			
				/* Move back to start of year */
				myStart.adjustYear(-1);
				myStart.adjustDay(1);
			
				/* Extract the last item */
				myCurr = myIterator.peekLast();
				myEnd  = myCurr.getDate();
			}
			
			/* Create the range */
			myRange = new DateDay.Range(myStart, myEnd);
			
			/* Return the range */
			return myRange;
		}
		
		/**
		 *  Mark active prices
		 */
		protected void markActiveItems () {
			DataListIterator<TaxYear> 	myIterator;
			TaxYear 					myCurr;

			/* Access the list iterator */
			myIterator = listIterator();
			
			/* Loop through the Prices */
			while ((myCurr = myIterator.next()) != null) {
				/* mark the tax regime referred to */
				myCurr.getTaxRegime().touchItem(myCurr); 
			}
		}

		/* Allow a tax parameter to be added */
		public void addItem(int		uId,
							String  pRegime,
				            Date  	pDate,
				            String  pAllowance,
				            String  pRentalAllow,
				            String  pLoAgeAllow,
				            String  pHiAgeAllow,
				            String	pCapAllow,
				            String  pAgeAllowLimit,
				            String  pAddAllowLimit,
				            String  pLoTaxBand,
				            String  pBasicTaxBand,
				            String  pAddIncBound,
				            String  pLoTaxRate,
				            String  pBasicTaxRate,
				            String  pHiTaxRate,
				            String  pIntTaxRate,
				            String  pDivTaxRate,
				            String  pHiDivTaxRate,
				            String  pAddTaxRate,
				            String  pAddDivTaxRate,
				            String	pCapTaxRate,
				            String	pHiCapTaxRate) throws ModelException {
			/* Local variables */
			TaxRegime    	myTaxRegime;		
			
			/* Look up the Tax Regime */
			myTaxRegime = theData.getTaxRegimes().searchFor(pRegime);
			if (myTaxRegime == null) 
				throw new ModelException(ExceptionClass.DATA,
			                        "TaxYear on <" + 
			                        DateDay.format(new DateDay(pDate)) +
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
		public void addItem(int    	uId,
							int    	uRegimeId,
				            Date  	pDate,
				            String  pAllowance,
				            String  pRentalAllow,
				            String  pLoAgeAllow,
				            String  pHiAgeAllow,
				            String	pCapAllow,
				            String  pAgeAllowLimit,
				            String  pAddAllowLimit,
				            String  pLoTaxBand,
				            String  pBasicTaxBand,
				            String  pAddIncBound,
				            String  pLoTaxRate,
				            String  pBasicTaxRate,
				            String  pHiTaxRate,
				            String  pIntTaxRate,
				            String  pDivTaxRate,
				            String  pHiDivTaxRate,
				            String  pAddTaxRate,
				            String  pAddDivTaxRate,
				            String	pCapTaxRate,
				            String	pHiCapTaxRate) throws ModelException {
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
				throw new ModelException(ExceptionClass.DATA,
						  			myTaxYear,
			  			            "Duplicate TaxYearId");
			 
			/* Check that this TaxYear has not been previously added */
			if (searchFor(new DateDay(pDate)) != null) 
				throw new ModelException(ExceptionClass.DATA,
						  			myTaxYear,
			                        "Duplicate TaxYear");
			
			/* Validate the tax year */
			myTaxYear.validate();
			
			/* Handle validation failure */
			if (myTaxYear.hasErrors()) 
				throw new ModelException(ExceptionClass.VALIDATE,
									myTaxYear,
									"Failed validation");
			
			/* Add the TaxYear to the list */
			add(myTaxYear);				
	    }		
	}

	/**
	 *  Values for a tax year
	 */
	public class Values extends HistoryValues<TaxYear> {
		private DateDay 	theYear	 		 = null;
		private Integer		theTaxRegimeId	 = null;
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
		public DateDay 		getYear()			{ return theYear; }
		private Integer		getTaxRegimeId()	{ return theTaxRegimeId; }
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
		
		public void setYear(DateDay pYear) {
			theYear   		= pYear; }
		private void setTaxRegimeId(int uTaxRegimeId) {
			theTaxRegimeId	= uTaxRegimeId; }
		public void setTaxRegime(TaxRegime pTaxRegime) {
			theTaxRegime    = pTaxRegime;
			theTaxRegimeId	= (pTaxRegime == null) ? null : pTaxRegime.getId(); }
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
		public Values(Values pValues) { copyFrom(pValues); }
		
		/* Check whether this object is equal to that passed */
		public Difference histEquals(HistoryValues<TaxYear> pCompare) {
			/* Make sure that the object is the same class */
			if (pCompare.getClass() != this.getClass()) return Difference.Different;
			
			/* Access as correct class */
			Values myValues = (Values)pCompare;
			
			/* Handle different date */
			if (DateDay.differs(theYear,	myValues.theYear).isDifferent())
				return Difference.Different;

			/* Handle different Tax RegimeId */
			if (Utils.differs(theTaxRegimeId,       myValues.theTaxRegimeId).isDifferent())
				return Difference.Different;

			/* Handle different Money items */
			if ((Money.differs(theAllowance,     	myValues.theAllowance).isDifferent())		||
				(Money.differs(theRentalAllow,   	myValues.theRentalAllow).isDifferent())		||
				(Money.differs(theLoBand,       	myValues.theLoBand).isDifferent())			||
				(Money.differs(theBasicBand,     	myValues.theBasicBand).isDifferent())		||
				(Money.differs(theCapitalAllow,  	myValues.theCapitalAllow).isDifferent())	||
				(Money.differs(theLoAgeAllow,    	myValues.theLoAgeAllow).isDifferent())		||
				(Money.differs(theHiAgeAllow,    	myValues.theHiAgeAllow).isDifferent())		||
				(Money.differs(theAgeAllowLimit, 	myValues.theAgeAllowLimit).isDifferent())	||
				(Money.differs(theAddAllowLimit, 	myValues.theAddAllowLimit).isDifferent())	||
				(Money.differs(theAddIncBound,   	myValues.theAddIncBound).isDifferent()))
				return Difference.Different;
				
			/* Handle different Rate items */
			if ((Rate.differs(theLoTaxRate,   		myValues.theLoTaxRate).isDifferent())		||
				(Rate.differs(theBasicTaxRate,		myValues.theBasicTaxRate).isDifferent())	||
				(Rate.differs(theHiTaxRate,   		myValues.theHiTaxRate).isDifferent())		||
				(Rate.differs(theIntTaxRate,  		myValues.theIntTaxRate).isDifferent())		||
				(Rate.differs(theDivTaxRate,  		myValues.theDivTaxRate).isDifferent())		||
				(Rate.differs(theHiDivTaxRate,		myValues.theHiDivTaxRate).isDifferent())	||
				(Rate.differs(theAddTaxRate,  		myValues.theAddTaxRate).isDifferent())		||
				(Rate.differs(theAddDivTaxRate, 	myValues.theAddDivTaxRate).isDifferent())	||
				(Rate.differs(theCapTaxRate,    	myValues.theCapTaxRate).isDifferent())		||
				(Rate.differs(theHiCapTaxRate,  	myValues.theHiCapTaxRate).isDifferent()))
				return Difference.Different;
				
			/* Return any TaxRegime differences */
			return TaxRegime.differs(theTaxRegime,	myValues.theTaxRegime);
		}
		
		/* Copy values */
		public HistoryValues<TaxYear> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			Values myValues = (Values)pSource;
			theYear    		 = myValues.getYear();
			theTaxRegimeId   = myValues.getTaxRegimeId();
			theTaxRegime     = myValues.getTaxRegime();
			theAllowance     = myValues.getAllowance();
			theRentalAllow   = myValues.getRentalAllow();
			theLoBand        = myValues.getLoBand();
			theBasicBand     = myValues.getBasicBand();
			theCapitalAllow  = myValues.getCapitalAllow();
			theLoAgeAllow    = myValues.getLoAgeAllow();
			theHiAgeAllow    = myValues.getHiAgeAllow();
			theAgeAllowLimit = myValues.getAgeAllowLimit();
			theAddAllowLimit = myValues.getAddAllowLimit();
			theAddIncBound	 = myValues.getAddIncBound();
			theLoTaxRate     = myValues.getLoTaxRate();
			theBasicTaxRate  = myValues.getBasicTaxRate();
			theHiTaxRate     = myValues.getHiTaxRate();
			theIntTaxRate    = myValues.getIntTaxRate();
			theDivTaxRate    = myValues.getDivTaxRate();
			theHiDivTaxRate  = myValues.getHiDivTaxRate();						
			theAddTaxRate    = myValues.getAddTaxRate();						
			theAddDivTaxRate = myValues.getAddDivTaxRate();						
			theCapTaxRate    = myValues.getCapTaxRate();						
			theHiCapTaxRate  = myValues.getHiCapTaxRate();						
		}
		public Difference	fieldChanged(int fieldNo, HistoryValues<TaxYear> pOriginal) {
			Values 	pValues = (Values)pOriginal;
			Difference	bResult = Difference.Identical;
			switch (fieldNo) {
				case FIELD_YEAR:
					bResult = (DateDay.differs(theYear,  		pValues.theYear));
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
