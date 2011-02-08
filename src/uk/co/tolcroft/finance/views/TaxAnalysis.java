package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.data.TaxType.*;
import uk.co.tolcroft.finance.data.TransactionType.*;
import uk.co.tolcroft.finance.views.AnalysisYear.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number;

public class TaxAnalysis {
	/* Members */
	private TaxYear					theYear			= null;
	private Date          			theDate         = null;
	private TranList                theTransBuckets = null;
	private TaxList                 theTaxBuckets   = null;
	private TransactionType.List 	theTransTypes   = null;
	private TaxType.List   			theTaxTypes     = null;
	private ChargeableEvent			theCharges		= null;
	private boolean                 hasTotals       = false;
	private boolean					hasAgeAllowance = false;
	private boolean					hasGainsSlices	= false;
	private boolean					hasReducedAllow	= false;
	private int						theAge			= 0;

	/* Access methods */
	public TaxYear    		getYear()         { return theYear; }
	public Date      		getDate()         { return theDate; }
	public TaxType.List 	getTaxTypes() 	  { return theTaxTypes; }
	public TranList         getTransBuckets() { return theTransBuckets; }
	public TaxList          getTaxBuckets()   { return theTaxBuckets; }
	public ChargeableEvent  getCharges()   	  { return theCharges; }
	public boolean     		hasReducedAllow() { return hasReducedAllow; }
	public boolean     		hasGainsSlices()  { return hasGainsSlices; }
	public int				getAge()		  { return theAge; }
	
	/* Constructor */
	public TaxAnalysis(DataSet pData, TaxYear pYear) {
		theYear         = pYear;
		theDate         = pYear.getDate();
		theTransTypes   = pData.getTransTypes();
		theTaxTypes     = pData.getTaxTypes();
		theTransBuckets = new TranList();
		theTaxBuckets   = new TaxList();
	}
	
	/**
	 * Seed from previous end-of-year analysis
	 *
	 * @param  pAnalysis The Previous analysis
	 */
	public void seedAnalysis(TaxAnalysis pAnalysis) {
		TranList.ListIterator	myIterator;
		TranBucket 				myCurr;
		TranBucket 				myBucket;
		
		/* If we have an analysis to copy */
		if (pAnalysis != null) {	
			/* Access the iterator */
			myIterator = pAnalysis.getTransBuckets().listIterator();
			
			/* Loop through the buckets */
			while ((myCurr = myIterator.next()) != null) {			
				/* If we have a non-empty bucket */
				if (myCurr.getAmount().isNonZero()) {
					/* Copy and add the bucket */
					myBucket  = new TranBucket(theTransBuckets, myCurr);
					myBucket.addToList();
				}
		    }
		}			
	}
	
	/**
	 * Produce totals for the tax year 
	 * @param pProperties the properties
	 */
	public void produceTotals(Properties pProperties) {
		/* If we have not previously totalled */
		if (!hasTotals) {
			/* Calculate the totals */
			analyseTransactions();
			calculateTax(pProperties);
			
			/* Prune the lists */
			theTransBuckets.prune();
			theTaxBuckets.prune();
			
			/* Set flag */
			hasTotals = true;
		}
	}
	
	/**
	 * Process an event
	 * 
	 * @param pEvent the event to process
	 */
	public void processEvent(Event pEvent) {
		TransactionType	myTransType;
		TranBucket      myBucket;
		ChargeableEvent	myCharge;
		
		/* Access the transaction type */
		myTransType = pEvent.getTransType();

		/* If the transaction is not a transfer */
		if ((!myTransType.isTransfer()) && 
			(!myTransType.isCashPayment()) &&
			(!myTransType.isCashRecovery())) {
			/* Locate its bucket */
			myBucket = theTransBuckets.getTransBucket(pEvent);
			
			/* Add the Event to the bucket */
			myBucket.addEvent(pEvent);
			
			/* If this is a taxable gain event */
			if (myTransType.isTaxableGain()) {
				/* Create and store the new taxable event */
				myCharge = new ChargeableEvent(pEvent, theCharges);
				if (theCharges == null) theCharges = myCharge;

				/* Remove this value from the market growth bucket */
				myBucket = theTransBuckets.getTransBucket(theTransTypes
			                 	.searchFor(TransClass.MKTGROWTH));
			    myBucket.subtractAmount(pEvent.getAmount());
			    myBucket.subtractAmount(pEvent.getTaxCredit());
			}

			/* If this is a capital gain event */
			if (myTransType.isCapitalGain()) {
				/* Access the Market Growth bucket */
				myBucket = theTransBuckets.getTransBucket(theTransTypes
			                 	.searchFor(TransClass.MKTGROWTH));
			    myBucket.subtractAmount(pEvent.getAmount());
			}

			/* If this is a capital loss event */
			if (myTransType.isCapitalLoss()) {
				/* Access the Market Growth bucket */
				myBucket = theTransBuckets.getTransBucket(theTransTypes
			                 	.searchFor(TransClass.MKTSHRINK));
			    myBucket.addAmount(pEvent.getAmount());
			}
		}
	}
	
	/**
	 * Add market movement
	 * 
	 * @param pBucket the market movement
	 */
	public void addMarketMovement(AssetAnalysis.AssetBucket pMovement) {
		TranBucket     	myBucket;
		Number.Money	myMovement;
		
		/* Access the current movement */
		myMovement = new Number.Money(pMovement.getMarket());
		
		/* If the movement is positive */
		if (myMovement.isPositive())
		{
			/* Access the Market Growth bucket */
			myBucket = theTransBuckets.getTransBucket(theTransTypes
		                 	.searchFor(TransClass.MKTGROWTH));
		    myBucket.addAmount(myMovement);
		}
		
		/* else this is a market shrink */
		else {
			/* Negate the value */
			myMovement.negate();
			
			/* Access the Market Growth bucket */
			myBucket = theTransBuckets.getTransBucket(theTransTypes
		                 	.searchFor(TransClass.MKTSHRINK));
		    myBucket.addAmount(myMovement);
		}
	}
	
	/**
	 * Analyse transactions for tax
	 */
	public void analyseTransactions() {
		TranBucket myBucket;
		
		/* Build the Salary bucket */
		myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				 .searchFor(TaxClass.GROSSSALARY));
		myBucket.addBucket(theTransBuckets
				 .getTransBucket(theTransTypes
				   		 .searchFor(TransClass.TAXEDINCOME)));
		myBucket.addBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.NATINSURANCE)));
		myBucket.addBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.BENEFIT)));
		
		/* Build the Interest bucket */
		myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				 .searchFor(TaxClass.GROSSINTEREST));
		myBucket.addBucket(theTransBuckets
				 .getTransBucket(theTransTypes
				   		 .searchFor(TransClass.INTEREST)));
		
		/* Build the Dividend bucket */
		myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				 .searchFor(TaxClass.GROSSDIVIDEND));
		myBucket.addBucket(theTransBuckets
				 .getTransBucket(theTransTypes
				   		 .searchFor(TransClass.DIVIDEND)));
		
		/* Build the Unit Trust Dividend bucket */
		myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				 .searchFor(TaxClass.GROSSUTDIVS));
		myBucket.addBucket(theTransBuckets
				 .getTransBucket(theTransTypes
				   		 .searchFor(TransClass.UNITTRUSTDIV)));
		
		/* Build the Taxable Gains bucket */
		myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				 .searchFor(TaxClass.GROSSTAXGAINS));
		myBucket.addBucket(theTransBuckets
				 .getTransBucket(theTransTypes
				   		 .searchFor(TransClass.TAXABLEGAIN)));
		
		/* Build the Capital Gains bucket */
		myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				 .searchFor(TaxClass.GROSSCAPGAINS));
		myBucket.addBucket(theTransBuckets
				 .getTransBucket(theTransTypes
				   		 .searchFor(TransClass.CAPITALGAIN)));
		myBucket.subtractBucket(theTransBuckets
				 .getTransBucket(theTransTypes
				   		 .searchFor(TransClass.CAPITALLOSS)));
		
		/* Build the Rental bucket */
		myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				 .searchFor(TaxClass.GROSSRENTAL));
		myBucket.addBucket(theTransBuckets
				 .getTransBucket(theTransTypes
				   		 .searchFor(TransClass.RENTALINCOME)));
		
		/* Build the TaxPaid bucket */
		myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				 .searchFor(TaxClass.TAXPAID));
		myBucket.addBucket(theTransBuckets
				 .getTransBucket(theTransTypes
				   		 .searchFor(TransClass.TAXEDINCOME)));
		myBucket.addBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.INTEREST)));
		myBucket.addBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.DIVIDEND)));
		myBucket.addBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.UNITTRUSTDIV)));
		myBucket.addBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.TAXABLEGAIN)));
		myBucket.addBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.TAXOWED)));
		myBucket.subtractBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.TAXREFUND)));
				
		/* Build the TaxFree bucket */
		myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				 .searchFor(TaxClass.TAXFREE));
		myBucket.addBucket(theTransBuckets
				 .getTransBucket(theTransTypes
				   		 .searchFor(TransClass.TAXFREEINTEREST)));
		myBucket.addBucket(theTransBuckets
				 .getTransBucket(theTransTypes
				   		 .searchFor(TransClass.TAXFREEDIVIDEND)));
		myBucket.addBucket(theTransBuckets
				 .getTransBucket(theTransTypes
				   		 .searchFor(TransClass.TAXFREEINCOME)));
		myBucket.addBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.INHERITED)));
		myBucket.addBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.DEBTINTEREST)));
		
		/* Build the Market bucket */
		myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				 .searchFor(TaxClass.MARKET));
		myBucket.addBucket(theTransBuckets
				 .getTransBucket(theTransTypes
				   		 .searchFor(TransClass.MKTINCOME)));
		myBucket.addBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.MKTGROWTH)));
		myBucket.subtractBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.MKTSHRINK)));
		
		/* Build the Expense bucket */
		myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				 .searchFor(TaxClass.EXPENSE));
		myBucket.addBucket(theTransBuckets
				 .getTransBucket(theTransTypes
				   		 .searchFor(TransClass.EXPENSE)));
		myBucket.addBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.MORTGAGE)));
		myBucket.addBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.INSURANCE)));
		myBucket.addBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.ENDOWMENT)));
		myBucket.addBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.EXTRATAX)));
		myBucket.addBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.WRITEOFF)));
		myBucket.subtractBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.RECOVERED)));
		myBucket.subtractBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.TAXRELIEF)));

		/* Build the Profit bucket */
		myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				 .searchFor(TaxClass.PROFIT));
		myBucket.addBucket(theTransBuckets
				 .getSummaryBucket(theTaxTypes
				   		 .searchFor(TaxClass.GROSSSALARY)));
		myBucket.addBucket(theTransBuckets
				 .getSummaryBucket(theTaxTypes
				   		 .searchFor(TaxClass.GROSSRENTAL)));
		myBucket.addBucket(theTransBuckets
				 .getSummaryBucket(theTaxTypes
				   		 .searchFor(TaxClass.GROSSINTEREST)));
		myBucket.addBucket(theTransBuckets
				 .getSummaryBucket(theTaxTypes
				   		 .searchFor(TaxClass.GROSSDIVIDEND)));
		myBucket.addBucket(theTransBuckets
				 .getSummaryBucket(theTaxTypes
				   		 .searchFor(TaxClass.GROSSUTDIVS)));
		myBucket.addBucket(theTransBuckets
				 .getSummaryBucket(theTaxTypes
				   		 .searchFor(TaxClass.GROSSTAXGAINS)));
		myBucket.addBucket(theTransBuckets
				 .getSummaryBucket(theTaxTypes
				   		 .searchFor(TaxClass.GROSSCAPGAINS)));
		myBucket.addBucket(theTransBuckets
				 .getSummaryBucket(theTaxTypes
				   		 .searchFor(TaxClass.MARKET)));
		myBucket.addBucket(theTransBuckets
				 .getSummaryBucket(theTaxTypes
				   		 .searchFor(TaxClass.TAXFREE)));
		myBucket.subtractBucket(theTransBuckets
	             .getSummaryBucket(theTaxTypes
	            		 .searchFor(TaxClass.TAXPAID)));
		myBucket.subtractBucket(theTransBuckets
	             .getSummaryBucket(theTaxTypes
	            		 .searchFor(TaxClass.EXPENSE)));		
		myBucket.subtractBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.BENEFIT)));
		myBucket.subtractBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.NATINSURANCE)));
		
		/* Build the CoreProfit bucket */
		myBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				 .searchFor(TaxClass.COREPROFIT));
		myBucket.addBucket(theTransBuckets
				 .getSummaryBucket(theTaxTypes
				   		 .searchFor(TaxClass.PROFIT)));
		myBucket.subtractBucket(theTransBuckets
	             .getSummaryBucket(theTaxTypes
	            		 .searchFor(TaxClass.MARKET)));			
		myBucket.subtractBucket(theTransBuckets
	             .getTransBucket(theTransTypes
	            		 .searchFor(TransClass.INHERITED)));			
	}
	
	/**
	 * Calculate tax
	 * @param pProperties the properties
	 */
	public void calculateTax(Properties pProperties) {
		taxBands		myBands;
		Number.Money 	myIncome	= new Number.Money(0);
		Number.Money 	myTax		= new Number.Money(0);
		TaxBucket		myBucket;
		TranBucket		mySrcBucket;
		
		/* Calculate the gross income */
		calculateGrossIncome();
		
		/* Calculate the allowances and tax bands */
		myBands = calculateAllowances(pProperties);
		
		/* Calculate the salary taxation */
		myBucket = calculateSalaryTax(myBands);
		myIncome.addAmount(myBucket.getAmount());
		myTax.addAmount(myBucket.getTaxation());
		
		/* Calculate the rental taxation */
		myBucket = calculateRentalTax(myBands);
		myIncome.addAmount(myBucket.getAmount());
		myTax.addAmount(myBucket.getTaxation());
		
		/* Calculate the interest taxation */
		myBucket = calculateInterestTax(myBands);
		myIncome.addAmount(myBucket.getAmount());
		myTax.addAmount(myBucket.getTaxation());
		
		/* Calculate the dividends taxation */
		myBucket = calculateDividendsTax(myBands);
		myIncome.addAmount(myBucket.getAmount());
		myTax.addAmount(myBucket.getTaxation());
		
		/* Calculate the taxable gains taxation */
		myBucket = calculateTaxableGainsTax(myBands);
		myIncome.addAmount(myBucket.getAmount());
		myTax.addAmount(myBucket.getTaxation());
		
		/* Calculate the capital gains taxation */
		myBucket = calculateCapitalGainsTax(myBands);
		myIncome.addAmount(myBucket.getAmount());
		myTax.addAmount(myBucket.getTaxation());
					
		/* Build the TotalTaxBucket */
		myBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     .searchFor(TaxClass.TOTALTAX));
		myBucket.setAmount(myIncome);
		myBucket.setTaxation(myTax);
		
		/* Access the tax paid bucket */
		mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
	            		 					.searchFor(TaxClass.TAXPAID));
		
		/* Calculate the tax profit */
		myTax.subtractAmount(mySrcBucket.getAmount());

		/* Build the TaxProfitBucket */
		myBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     .searchFor(TaxClass.TAXPROFIT));
		myBucket.setAmount(new Number.Money(0));
		myBucket.setTaxation(myTax);
	}

	/**
	 * Calculate the gross income for tax purposes
	 */
	public void calculateGrossIncome() {
		TaxBucket     	myBucket;
		TranBucket     	mySrcBucket;
		Number.Money 	myIncome = new Number.Money(0);
		Number.Money 	myChargeable;
		
		/* Access the salary bucket and add to income */
		mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				   		 				.searchFor(TaxClass.GROSSSALARY));
		myIncome.addAmount(mySrcBucket.getAmount());
		
		/* Access the rental bucket */
		mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				   		 				.searchFor(TaxClass.GROSSRENTAL));
		myChargeable = new Number.Money(mySrcBucket.getAmount());
		
		/* If we have a chargeable element */
		if (myChargeable.compareTo(theYear.getRentalAllowance()) > 0) {
			/* Add the chargeable element to income */
			myChargeable.subtractAmount(theYear.getRentalAllowance());
			myIncome.addAmount(myChargeable);
		}
		
		/* Access the interest bucket and add to income */
		mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				   		 				.searchFor(TaxClass.GROSSINTEREST));
		myIncome.addAmount(mySrcBucket.getAmount());
		
		/* Access the dividends bucket and add to income */
		mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				   		 				.searchFor(TaxClass.GROSSDIVIDEND));
		myIncome.addAmount(mySrcBucket.getAmount());
		
		/* Access the unit trust dividends bucket and add to income */
		mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				   		 				.searchFor(TaxClass.GROSSUTDIVS));
		myIncome.addAmount(mySrcBucket.getAmount());
		
		/* Access the taxable gains bucket and add to income */
		mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				   		 				.searchFor(TaxClass.GROSSTAXGAINS));
		myIncome.addAmount(mySrcBucket.getAmount());
		
		/* Access the taxable gains bucket and subtract the tax credit */
		mySrcBucket = theTransBuckets.getTransBucket(theTransTypes
				   		 				.searchFor(TransClass.TAXABLEGAIN));
		myIncome.subtractAmount(mySrcBucket.getTaxCredit());
		
		/* Access the capital gains bucket */
		mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				   		 				.searchFor(TaxClass.GROSSCAPGAINS));
		myChargeable = new Number.Money(mySrcBucket.getAmount());
		
		/* If we have a chargeable element */
		if (myChargeable.compareTo(theYear.getCapitalAllow()) > 0) {
			/* Add the chargeable element to income */
			myChargeable.subtractAmount(theYear.getCapitalAllow());
			myIncome.addAmount(myChargeable);
		}
		
		/* Access the Gross Income bucket and set the amount */
		myBucket = theTaxBuckets.getTaxBucket(theTaxTypes
				 					.searchFor(TaxClass.GROSSINCOME));
		myBucket.setAmount(myIncome);			
	}
	
	/**
	 * Calculate the allowances and tax bands
	 * @param pProperties the properties
	 */
	public taxBands calculateAllowances(Properties pProperties) {
		taxBands     	myBands;
		TaxBucket		myBucket;
		TaxBucket		myParentBucket;
		Number.Money 	myGrossIncome;
		Number.Money 	myAdjust;
		Number.Money 	myAllowance;
		long			myValue;
		
		/* Allocate the tax bands class */
		myBands = new taxBands();
		
		/* Determine the relevant age for this tax year */
		theAge = pProperties.getBirthDate().ageOn(theYear.getDate());
		
		/* Determine the relevant allowance */
		if (theAge >= 75) {
			myAllowance 	= theYear.getHiAgeAllow();
			hasAgeAllowance = true;
		}
		else if (theAge >= 65) {
			myAllowance 	= theYear.getLoAgeAllow();
			hasAgeAllowance = true;
		}
		else
			myAllowance 		= theYear.getAllowance();
			
		/* Set Allowance and Tax Bands */
		myBands.theAllowance = new Number.Money(myAllowance);
		myBands.theLoBand    = new Number.Money(theYear.getLoBand());
		myBands.theBasicBand = new Number.Money(theYear.getBasicBand());
		
		/* Record the Original allowance */
		myParentBucket = theTaxBuckets.getTaxBucket(theTaxTypes
									.searchFor(TaxClass.ORIGALLOW));
		myParentBucket.setAmount(myBands.theAllowance); 
			
		/* Access the gross income */
		myBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					.searchFor(TaxClass.GROSSINCOME));
		myGrossIncome = myBucket.getAmount(); 
		myBucket.setParent(myParentBucket);
			
		/* If we are using age allowance and the gross income is above the Age Allowance Limit */
		if ((hasAgeAllowance) &&
			(myGrossIncome.compareTo(theYear.getAgeAllowLimit()) > 0)) {
			/* Calculate the limit at which age allowance will disappear */
			myValue  = myBands.theAllowance.getValue();
			myValue *= 2; /* £1 reduction for every £2 increase */
			myAdjust = new Number.Money(myValue);
			myAdjust.addAmount(theYear.getAgeAllowLimit());
			
			/* If the gross income is above this limit */
			if (myGrossIncome.compareTo(myAdjust) > 0) {
				/* Personal allowance is reduced to standard allowance */
				myBands.theAllowance = new Number.Money(theYear.getAllowance());
				hasAgeAllowance = false;
			}
			
			/* else we need to reduce the personal allowance */
			else {
				/* Calculate the margin */
				myAdjust = new Number.Money(myGrossIncome);
				myAdjust.subtractAmount(theYear.getAgeAllowLimit());
				myValue  = myAdjust.getValue();
				
				/* Divide by £2 and then multiply up to £1 */
				myValue /= 200;
				myValue *= 100;
				myAdjust = new Number.Money(myValue);
				
				/* Adjust the allowance by this value */
				myBands.theAllowance = new Number.Money(myBands.theAllowance);
				myBands.theAllowance.subtractAmount(myAdjust);
			}
			
			/* Record the adjusted allowance */
			myBucket = theTaxBuckets.getTaxBucket(theTaxTypes
 					.searchFor(TaxClass.ADJALLOW));
			myBucket.setAmount(myBands.theAllowance);
			myBucket.setParent(myParentBucket);
			hasReducedAllow 	= true;
		}
		
		/* If we have an additional tax band */
		if (theYear.hasAdditionalTaxBand()) {
			/* Set the High tax band */
			myBands.theHiBand = new Number.Money(theYear.getAddIncBound());
			
			/* Remove the basic band from this one */
			myBands.theHiBand.subtractAmount(myBands.theBasicBand);
			
			/* Record the High tax band */
			myBucket = theTaxBuckets.getTaxBucket(theTaxTypes
 					.searchFor(TaxClass.HITAXBAND));
			myBucket.setAmount(myBands.theHiBand);
			myBucket.setParent(myParentBucket);
			
			/* If the gross income is above the Additional Allowance Limit */
			if (myGrossIncome.compareTo(theYear.getAddAllowLimit()) > 0) {
				/* Calculate the limit at which personal allowance will disappear */
				myValue  = myBands.theAllowance.getValue();
				myValue *= 2; /* £1 reduction for every £2 increase */
				myAdjust = new Number.Money(myValue);
				myAdjust.addAmount(theYear.getAddAllowLimit());
				
				/* If the gross income is above this limit */
				if (myGrossIncome.compareTo(myAdjust) > 0) {
					/* Personal allowance is reduced to zero */
					myBands.theAllowance = new Number.Money(0);
				}
				
				/* else we need to reduce the personal allowance */
				else {
					/* Calculate the margin */
					myAdjust = new Number.Money(myGrossIncome);
					myAdjust.subtractAmount(theYear.getAddAllowLimit());
					myValue  = myAdjust.getValue();
					
					/* Divide by £2 and then multiply up to £1 */
					myValue /= 200;
					myValue *= 100;
					myAdjust = new Number.Money(myValue);
					
					/* Adjust the allowance by this value */
					myBands.theAllowance = new Number.Money(myBands.theAllowance);
					myBands.theAllowance.subtractAmount(myAdjust);
				}
				
				/* Record the adjusted allowance */
				myBucket = theTaxBuckets.getTaxBucket(theTaxTypes
	 					.searchFor(TaxClass.ADJALLOW));
				myBucket.setAmount(myBands.theAllowance);
				myBucket.setParent(myParentBucket);
				hasReducedAllow 	= true;
			}
		}
		
		/* Return to caller */
		return myBands;
	}
	
	/**
	 * Calculate the tax due on salary
	 * @param pBands the remaining allowances and tax bands
	 * @return the salary taxation bucket
	 */
	public TaxBucket calculateSalaryTax(taxBands pBands) {
		TranBucket     	mySrcBucket;
		TaxBucket     	myTaxBucket;
		TaxBucket     	myTopBucket;
		Number.Money 	mySalary;
		Number.Money 	myTax		= new Number.Money(0);
		boolean			isFinished  = false;
		
		/* Access Salary */
		mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				           		.searchFor(TaxClass.GROSSSALARY));
		mySalary    = new Number.Money(mySrcBucket.getAmount());
	
		/* Store the total into the TaxDueSalary Bucket */
		myTopBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     	.searchFor(TaxClass.TAXDUESALARY));
		myTopBucket.setAmount(mySalary);
		
		/* Access the FreeSalaryBucket */
		myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     	.searchFor(TaxClass.SALARYFREE));
		myTaxBucket.setParent(myTopBucket);
		
		/* If the salary is greater than the remaining allowance */
		if (mySalary.compareTo(pBands.theAllowance) > 0) {
			/* Set the tax bucket and add the tax */
			myTax.addAmount(myTaxBucket.setAmount(pBands.theAllowance));
			
			/* Adjust the salary to remove allowance */
			mySalary.subtractAmount(pBands.theAllowance);
			pBands.theAllowance.setZero();
		}
		
		/* else still have allowance left after salary */
		else {
			/* Set the tax bucket and add the tax */
			myTax.addAmount(myTaxBucket.setAmount(mySalary));
			
			/* Adjust the allowance to remove salary and note that we have finished */
			pBands.theAllowance.subtractAmount(mySalary);
			isFinished = true;
		}
			
		/* If we have salary left  */
		if (!isFinished) {
			/* If we have a low salary band */
			if (theYear.hasLoSalaryBand()) {
				/* Access the LowSalaryBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     		.searchFor(TaxClass.SALARYLO));
				myTaxBucket.setRate(theYear.getLoTaxRate());
				myTaxBucket.setParent(myTopBucket);
			
				/* If the salary is greater than the Low Tax Band */
				if (mySalary.compareTo(pBands.theLoBand) > 0) {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(pBands.theLoBand));
				
					/* Adjust the salary to remove LoBand */
					mySalary.subtractAmount(pBands.theLoBand);
					pBands.theLoBand.setZero();
				}
				
				/* else we still have band left after salary */
				else {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(mySalary));
				
					/* Adjust the loBand to remove salary and note that we have finished */
					pBands.theLoBand.subtractAmount(mySalary);
					isFinished = true;
				}
			}
			
			/* Else use up the Low Tax band */
			else {
				/* If the salary is greater than the Low Tax Band */
				if (mySalary.compareTo(pBands.theLoBand) > 0) {
					/* We have used up the band */
					pBands.theLoBand.setZero();
				}
				else {
					/* Adjust the band to remove salary */
					pBands.theLoBand.subtractAmount(mySalary);
				}
			}
		}
						
		/* If we have salary left */
		if (!isFinished) {
			/* Access the BasicSalaryBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.SALARYBASIC));
			myTaxBucket.setRate(theYear.getBasicTaxRate());
			myTaxBucket.setParent(myTopBucket);
				
			/* If the salary is greater than the Basic Tax Band */
			if (mySalary.compareTo(pBands.theBasicBand) > 0) {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));
				
				/* Adjust the salary to remove BasicBand */
				mySalary.subtractAmount(pBands.theBasicBand);
				pBands.theBasicBand.setZero();
			}
				
			/* else we still have band left after salary */
			else {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(mySalary));
				
				/* Adjust the basicBand to remove salary and note that we have finished */
				pBands.theBasicBand.subtractAmount(mySalary);
				isFinished = true;
			}
		}
		
		/* If we have salary left */
		if (!isFinished) {
			/* Access the HiSalaryBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.SALARYHI));
			myTaxBucket.setRate(theYear.getHiTaxRate());
			myTaxBucket.setParent(myTopBucket);
				
			/* If the salary is greater than the High Tax Band */
			if ((theYear.hasAdditionalTaxBand()) && 
				(mySalary.compareTo(pBands.theHiBand) > 0)) {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));
				
				/* Adjust the salary to remove HiBand */
				mySalary.subtractAmount(pBands.theHiBand);
				pBands.theHiBand.setZero();
			}
				
			/* else we still have band left after salary */
			else {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(mySalary));
				
				/* Adjust the hiBand to remove salary and note that we have finished */
				if (theYear.hasAdditionalTaxBand())
					pBands.theHiBand.subtractAmount(mySalary);
				isFinished = true;
			}
		}
		
		/* If we have salary left */
		if (!isFinished) {
			/* Access the AdditionalSalaryBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.SALARYADD));
			myTaxBucket.setRate(theYear.getAddTaxRate());
			myTaxBucket.setParent(myTopBucket);
				
			/* Set the tax bucket and add the tax */
			myTax.addAmount(myTaxBucket.setAmount(mySalary));
		}
		
		/* Store the taxation value into the top bucket */
		myTopBucket.setTaxation(myTax);
		
		/* Return the tax bucket */
		return myTopBucket;
	}
	
	/**
	 * Calculate the tax due on rental
	 * @param pBands the remaining allowances and tax bands
	 * @return the rental tax bucket
	 */
	public TaxBucket calculateRentalTax(taxBands pBands) {
		TranBucket     	mySrcBucket;
		TaxBucket     	myTaxBucket;
		TaxBucket     	myTopBucket;
		Number.Money 	myRental;
		Number.Money 	myAllowance;
		Number.Money 	myTax		= new Number.Money(0);
		boolean			isFinished  = false;
		
		/* Access Rental */
		mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				           		.searchFor(TaxClass.GROSSRENTAL));
		myRental    = new Number.Money(mySrcBucket.getAmount());
	
		/* Store the total into the TaxDueRental Bucket */
		myTopBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     	.searchFor(TaxClass.TAXDUERENTAL));
		myTopBucket.setAmount(myRental);
		
		/* Access the FreeRentalBucket */
		myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     	.searchFor(TaxClass.RENTALFREE));
		myTaxBucket.setParent(myTopBucket);
		
		/* Pick up the rental allowance */
		myAllowance = theYear.getRentalAllowance();
		
		/* If the rental is less than the rental allowance */
		if (myRental.compareTo(myAllowance) < 0) {
			/* All of the rental is free so record it and note that we have finished */
			myTax.addAmount(myTaxBucket.setAmount(myRental));
			isFinished = true;
		}
		
		/* If we have not finished */
		if (!isFinished) {
			/* Remove allowance from rental figure */
			myRental.subtractAmount(myAllowance);

			/* If the rental is greater than the remaining allowance */
			if (myRental.compareTo(pBands.theAllowance) > 0) {
				/* Determine the remaining allowance */
				myAllowance.addAmount(pBands.theAllowance);
				
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myAllowance));
			
				/* Adjust the rental to remove allowance */
				myRental.subtractAmount(pBands.theAllowance);
				pBands.theAllowance.setZero();
			}
		
			/* else still have allowance left after rental */
			else {
				/* Determine the remaining allowance */
				myAllowance.addAmount(myRental);
				
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myAllowance));
			
				/* Adjust the allowance to remove rental and note that we have finished */
				pBands.theAllowance.subtractAmount(myRental);
				isFinished = true;
			}
		}
			
		/* If we have salary left  */
		if (!isFinished) {
			/* If we have a low salary band */
			if (theYear.hasLoSalaryBand()) {
				/* Access the LowSalaryBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     		.searchFor(TaxClass.RENTALLO));
				myTaxBucket.setRate(theYear.getLoTaxRate());
				myTaxBucket.setParent(myTopBucket);
			
				/* If the rental is greater than the Low Tax Band */
				if (myRental.compareTo(pBands.theLoBand) > 0) {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(pBands.theLoBand));
				
					/* Adjust the rental to remove LoBand */
					myRental.subtractAmount(pBands.theLoBand);
					pBands.theLoBand.setZero();
				}
				
				/* else we still have band left after salary */
				else {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(myRental));
				
					/* Adjust the loBand to remove rental and note that we have finished */
					pBands.theLoBand.subtractAmount(myRental);
					isFinished = true;
				}
			}
			
			/* Else use up the Low Tax band */
			else {
				/* If the rental is greater than the Low Tax Band */
				if (myRental.compareTo(pBands.theLoBand) > 0) {
					/* We have used up the band */
					pBands.theLoBand.setZero();
				}
				else {
					/* Adjust the band to remove rental */
					pBands.theLoBand.subtractAmount(myRental);
				}					
			}
		}
				
		/* If we have Rental left */
		if (!isFinished) {
			/* Access the BasicRentalBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.RENTALBASIC));
			myTaxBucket.setRate(theYear.getBasicTaxRate());
			myTaxBucket.setParent(myTopBucket);
				
			/* If the rental is greater than the Basic Tax Band */
			if (myRental.compareTo(pBands.theBasicBand) > 0) {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));
				
				/* Adjust the rental to remove BasicBand */
				myRental.subtractAmount(pBands.theBasicBand);
				pBands.theBasicBand.setZero();
			}
				
			/* else we still have band left after rental */
			else {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myRental));
				
				/* Adjust the basicBand to remove salary and note that we have finished */
				pBands.theBasicBand.subtractAmount(myRental);
				isFinished = true;
			}
		}
		
		/* If we have rental left */
		if (!isFinished) {
			/* Access the HiRentalBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.RENTALHI));
			myTaxBucket.setRate(theYear.getHiTaxRate());
			myTaxBucket.setParent(myTopBucket);
				
			/* If the rental is greater than the High Tax Band */
			if ((theYear.hasAdditionalTaxBand()) && 
				(myRental.compareTo(pBands.theHiBand) > 0)) {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));
				
				/* Adjust the rental to remove HiBand */
				myRental.subtractAmount(pBands.theHiBand);
				pBands.theHiBand.setZero();
			}
				
			/* else we still have band left after rental */
			else {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myRental));
				
				/* Adjust the hiBand to remove rental and note that we have finished */
				if (theYear.hasAdditionalTaxBand())
					pBands.theHiBand.subtractAmount(myRental);
				isFinished = true;
			}
		}
		
		/* If we have rental left */
		if (!isFinished) {
			/* Access the AdditionalRentalBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.RENTALADD));
			myTaxBucket.setRate(theYear.getAddTaxRate());
			myTaxBucket.setParent(myTopBucket);
				
			/* Set the tax bucket and add the tax */
			myTax.addAmount(myTaxBucket.setAmount(myRental));
		}
		
		/* Store the taxation total */
		myTopBucket.setTaxation(myTax);
		
		/* Return the tax bucket */
		return myTopBucket;
	}
	
	/**
	 * Calculate the tax due on Interest
	 * @param pBands the remaining allowances and tax bands
	 * @return the interest tax bucket
	 */
	public TaxBucket calculateInterestTax(taxBands pBands) {
		TranBucket     	mySrcBucket;
		TaxBucket     	myTaxBucket;
		TaxBucket     	myTopBucket;
		Number.Money 	myInterest;
		Number.Money 	myTax		= new Number.Money(0);
		boolean			isFinished  = false;
		
		/* If we do not have a Low salary band */
		if (!theYear.hasLoSalaryBand()) {
			/* Remove LoTaxBand from BasicTaxBand */
			pBands.theBasicBand.subtractAmount(pBands.theLoBand);
		}				
		
		/* Access Interest */
		mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				           		.searchFor(TaxClass.GROSSINTEREST));
			myInterest  = new Number.Money(mySrcBucket.getAmount());
	
		/* Store the total into the TaxDueInterest Bucket */
		myTopBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     	.searchFor(TaxClass.TAXDUEINTEREST));
		myTopBucket.setAmount(myInterest);
		
		/* Access the FreeInterestBucket */
		myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     	.searchFor(TaxClass.INTERESTFREE));
		myTaxBucket.setParent(myTopBucket);
		
		/* If the interest is greater than the remaining allowance */
		if (myInterest.compareTo(pBands.theAllowance) > 0) {
			/* Set the tax bucket and add the tax */
			myTax.addAmount(myTaxBucket.setAmount(pBands.theAllowance));
			
			/* Adjust the interest to remove allowance */
			myInterest.subtractAmount(pBands.theAllowance);
			pBands.theAllowance.setZero();
		}
		
		/* else still have allowance left after interest */
		else {
			/* Set the tax bucket and add the tax */
			myTax.addAmount(myTaxBucket.setAmount(myInterest));
			
			/* Adjust the allowance to remove interest and note that we have finished */
			pBands.theAllowance.subtractAmount(myInterest);
			isFinished = true;
		}
			
		/* If we have interest left */
		if (!isFinished) {
			/* Access the LowInterestBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.INTERESTLO));
			myTaxBucket.setRate(theYear.getLoTaxRate());
			myTaxBucket.setParent(myTopBucket);
			
			/* If the interest is greater than the Low Tax Band */
			if (myInterest.compareTo(pBands.theLoBand) > 0) {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(pBands.theLoBand));
				
				/* Adjust the interest to remove LoBand */
				myInterest.subtractAmount(pBands.theLoBand);
				pBands.theLoBand.setZero();
			}
				
			/* else we still have band left after interest */
			else {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myInterest));
				
				/* Adjust the loBand to remove interest and note that we have finished */
				pBands.theLoBand.subtractAmount(myInterest);
				isFinished = true;
			}
		}
				
		/* If we have interest left */
		if (!isFinished) {
			/* Access the BasicInterestBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.INTERESTBASIC));
			myTaxBucket.setRate(theYear.getIntTaxRate());
			myTaxBucket.setParent(myTopBucket);
				
			/* If the interest is greater than the Basic Tax Band */
			if (myInterest.compareTo(pBands.theBasicBand) > 0) {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));
				
				/* Adjust the interest to remove BasicBand */
				myInterest.subtractAmount(pBands.theBasicBand);
				pBands.theBasicBand.setZero();
			}
				
			/* else we still have band left after interest */
			else {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myInterest));
				
				/* Adjust the basicBand to remove interest and note that we have finished */
				pBands.theBasicBand.subtractAmount(myInterest);
				isFinished = true;
			}
		}
		
		/* If we have interest left */
		if (!isFinished) {
			/* Access the HiInterestBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.INTERESTHI));
			myTaxBucket.setRate(theYear.getHiTaxRate());
			myTaxBucket.setParent(myTopBucket);
				
			/* If the interest is greater than the High Tax Band */
			if ((theYear.hasAdditionalTaxBand()) && 
				(myInterest.compareTo(pBands.theHiBand) > 0)) {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));
				
				/* Adjust the interest to remove HiBand */
				myInterest.subtractAmount(pBands.theHiBand);
				pBands.theHiBand.setZero();
			}
				
			/* else we still have band left after interest */
			else {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myInterest));
				
				/* Adjust the hiBand to remove interest and note that we have finished */
				if (theYear.hasAdditionalTaxBand())
					pBands.theHiBand.subtractAmount(myInterest);
				isFinished = true;
			}
		}
		
		/* If we have interest left */
		if (!isFinished) {
			/* Access the AdditionalInterestBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.INTERESTADD));
			myTaxBucket.setRate(theYear.getAddTaxRate());
			myTaxBucket.setParent(myTopBucket);
				
			/* Set the tax bucket and add the tax */
			myTax.addAmount(myTaxBucket.setAmount(myInterest));
		}
		
		/* Remaining tax credits are not reclaimable */
		/* so add any remaining allowance/LoTaxBand into BasicTaxBand */
		pBands.theBasicBand.addAmount(pBands.theAllowance);
		pBands.theBasicBand.addAmount(pBands.theLoBand);
		pBands.theAllowance.setZero();
		pBands.theLoBand.setZero();
		
		/* Store the taxation total */
		myTopBucket.setTaxation(myTax);
		
		/* Return the tax bucket */
		return myTopBucket;
	}
	
	/**
	 * calculate the tax due on dividends
	 * @param pBands the remaining allowances and tax bands
	 * @return the dividends tax bucket
	 */
	public TaxBucket calculateDividendsTax(taxBands pBands) {
		TranBucket     	mySrcBucket;
		TaxBucket     	myTaxBucket;
		TaxBucket     	myTopBucket;
		Number.Money 	myDividends;
		Number.Money 	myTax		= new Number.Money(0);
		boolean			isFinished  = false;
		
		/* Access Dividends */
		mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				           		.searchFor(TaxClass.GROSSDIVIDEND));
		myDividends = new Number.Money(mySrcBucket.getAmount());
	
		/* Access Unit Trust Dividends */
		mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				           		.searchFor(TaxClass.GROSSUTDIVS));
			myDividends.addAmount(mySrcBucket.getAmount());
		
		/* Store the total into the TaxDueDividends Bucket */
		myTopBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     	.searchFor(TaxClass.TAXDUEDIVIDEND));
		myTopBucket.setAmount(myDividends);
		
		/* Access the BasicDividendBucket */
		myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     	.searchFor(TaxClass.DIVIDENDBASIC));
		myTaxBucket.setRate(theYear.getDivTaxRate());
		myTaxBucket.setParent(myTopBucket);
				
		/* If the dividends are greater than the Basic Tax Band */
		if (myDividends.compareTo(pBands.theBasicBand) > 0) {
			/* Set the tax bucket and add the tax */
			myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));
				
			/* Adjust the dividends to remove BasicBand */
			myDividends.subtractAmount(pBands.theBasicBand);
			pBands.theBasicBand.setZero();
		}
				
		/* else we still have band left after dividends */
		else {
			/* Set the tax bucket and add the tax */
			myTax.addAmount(myTaxBucket.setAmount(myDividends));
				
			/* Adjust the basicBand to remove dividends and note that we have finished */
			pBands.theBasicBand.subtractAmount(myDividends);
			isFinished = true;
		}
		
		/* If we have dividends left */
		if (!isFinished) {
			/* Access the HiDividendsBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.DIVIDENDHI));
			myTaxBucket.setRate(theYear.getHiDivTaxRate());
			myTaxBucket.setParent(myTopBucket);
				
			/* If the dividends are greater than the High Tax Band */
			if ((theYear.hasAdditionalTaxBand()) && 
				(myDividends.compareTo(pBands.theHiBand) > 0)) {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));
				
				/* Adjust the dividends to remove HiBand */
				myDividends.subtractAmount(pBands.theHiBand);
				pBands.theHiBand.setZero();
			}
				
			/* else we still have band left after dividends */
			else {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myDividends));
				
				/* Adjust the hiBand to remove dividends and note that we have finished */
				if (theYear.hasAdditionalTaxBand())
					pBands.theHiBand.subtractAmount(myDividends);
				isFinished = true;
			}
		}
		
		/* If we have dividends left */
		if (!isFinished) {
			/* Access the AdditionalDividendsBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.DIVIDENDADD));
			myTaxBucket.setRate(theYear.getAddDivTaxRate());
			myTaxBucket.setParent(myTopBucket);
				
			/* Set the tax bucket and add the tax */
			myTax.addAmount(myTaxBucket.setAmount(myDividends));
		}
		
		/* Store the taxation total */
		myTopBucket.setTaxation(myTax);
		
		/* Return the tax bucket */
		return myTopBucket;
	}
	
	/**
	 * calculate the tax due on taxable gains
	 * @param pBands the remaining allowances and tax bands
	 * @return the taxable gains bucket
	 */
	public TaxBucket calculateTaxableGainsTax(taxBands pBands) {
		TranBucket     	mySrcBucket;
		TaxBucket     	myTaxBucket;
		TaxBucket     	myTopBucket;
		TaxBucket     	mySliceBucket;
		Number.Money 	myGains;
		Number.Money	mySlice;
		Number.Money	myHiTax;
		Number.Money 	myTax		= new Number.Money(0);
		boolean			isFinished  = false;
		
		/* Access Gains */
		myGains = (theCharges != null) 	? theCharges.getGainsTotal()
										: new Number.Money(0);

		/* Store the total into the TaxDueTaxGains Bucket */
		myTopBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     	.searchFor(TaxClass.TAXDUETAXGAINS));
		myTopBucket.setAmount(myGains);
		
		/* If the gains are less than the available basic tax band */
		if (myGains.compareTo(pBands.theBasicBand) <= 0) {
			/* Access the BasicGainsBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     		.searchFor(TaxClass.GAINSBASIC));
			myTaxBucket.setRate(theYear.getBasicTaxRate());
			myTaxBucket.setParent(myTopBucket);
					
			/* Set the tax bucket and add the tax */
			myTax.addAmount(myTaxBucket.setAmount(myGains));
				
			/* Adjust the basic band to remove taxable gains */
			pBands.theBasicBand.subtractAmount(myGains);
			isFinished = true;
		}
				
		/* If we are not finished but either have no basic band left
		 *  or are prevented from top-slicing due to using age allowances */
		if ((!isFinished) && 
			((!pBands.theBasicBand.isNonZero()) ||
			 (hasAgeAllowance))) {
			/* Access the BasicGainsBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     	.searchFor(TaxClass.GAINSBASIC));
			myTaxBucket.setRate(theYear.getBasicTaxRate());
			myTaxBucket.setParent(myTopBucket);
				
			/* If the gains is greater than the Basic Tax Band */
			if (myGains.compareTo(pBands.theBasicBand) > 0) {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));
				
				/* Adjust the interest to remove BasicBand */
				myGains.subtractAmount(pBands.theBasicBand);
				pBands.theBasicBand.setZero();
			}

			/* else case already handled */
			
			/* Access the HiGainsBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     		.searchFor(TaxClass.GAINSHI));
			myTaxBucket.setRate(theYear.getHiTaxRate());
			myTaxBucket.setParent(myTopBucket);
				
			/* If the gains are greater than the High Tax Band */
			if ((theYear.hasAdditionalTaxBand()) && 
				(myGains.compareTo(pBands.theHiBand) > 0)) {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));
				
				/* Adjust the gains to remove HiBand */
				myGains.subtractAmount(pBands.theHiBand);
				pBands.theHiBand.setZero();
			}
				
			/* else we still have band left after gains */
			else {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myGains));
				
				/* Adjust the hiBand to remove dividends and note that we have finished */
				if (theYear.hasAdditionalTaxBand())
					pBands.theHiBand.subtractAmount(myGains);
				isFinished = true;
			}
			
			/* If we have gains left */
			if (!isFinished) {
				/* Access the AdditionalGainsBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
							     	.searchFor(TaxClass.GAINSADD));
				myTaxBucket.setRate(theYear.getAddDivTaxRate());
				myTaxBucket.setParent(myTopBucket);
					
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myGains));
				isFinished = true;
			}
		}
		
		/* If we are not finished then we need top-slicing relief */
		if (!isFinished) { 
			/* Access to taxable slice */
			mySlice 		= theCharges.getSliceTotal();
			hasGainsSlices 	= true;

			/* Access the TaxDueSlice Bucket */
			mySliceBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     		.searchFor(TaxClass.TAXDUESLICE));
			mySliceBucket.setAmount(mySlice);
					
			/* Access the BasicSliceBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     		.searchFor(TaxClass.SLICEBASIC));
			myTaxBucket.setRate(theYear.getBasicTaxRate());
			myTaxBucket.setParent(mySliceBucket);
					
			/* If the slice is less than the available basic tax band */
			if (mySlice.compareTo(pBands.theBasicBand) < 0) {
				/* Set the slice details */
				myTax.addAmount(myTaxBucket.setAmount(mySlice));

				/* Distribute the Tax back to the chargeable events */
				theCharges.applyTax(myTax, theCharges.getSliceTotal());
				
				/* Access the BasicGainsBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     		.searchFor(TaxClass.GAINSBASIC));
				myTaxBucket.setRate(theYear.getBasicTaxRate());
				
				/* Only basic rate tax is payable */
				myTaxBucket.setAmount(myGains);
				mySliceBucket.setTaxation(myTax);
			}
			
			/* else we are using up the basic rate tax band */
			else {
				/* Set the slice details */
				myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));
				
				/* Subtract the basic band from the slice */
				mySlice.subtractAmount(pBands.theBasicBand);
				
				/* Access the BasicGainsBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     		.searchFor(TaxClass.GAINSBASIC));
				myTaxBucket.setRate(theYear.getBasicTaxRate());
				
				/* Basic Rate tax is payable on the remainder of the basic band */
				myTaxBucket.setAmount(pBands.theBasicBand);
				
				/* Remember this taxation amount to remove from HiTax bucket */
				myHiTax = new Number.Money(myTaxBucket.getTaxation());
				myHiTax.negate();
				
				/* Access the HiSliceBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     		.searchFor(TaxClass.SLICEHI));
				myTaxBucket.setRate(theYear.getHiTaxRate());
				myTaxBucket.setParent(mySliceBucket);
						
				/* If the slice is greater than the High Tax Band */
				if ((theYear.hasAdditionalTaxBand()) && 
					(mySlice.compareTo(pBands.theHiBand) > 0)) {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));
					
					/* Adjust the slice to remove HiBand */
					mySlice.subtractAmount(pBands.theHiBand);
					
					/* Access the AdditionalSliceBucket */
					myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
								     	.searchFor(TaxClass.SLICEADD));
					myTaxBucket.setRate(theYear.getAddTaxRate());
					myTaxBucket.setParent(mySliceBucket);
						
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(mySlice));
				}
					
				/* else we still have band left after slice */
				else {
					/* Set the tax bucket and add the tax */
					myTax.addAmount(myTaxBucket.setAmount(mySlice));
				}
				
				/* Set the total tax into the slice bucket */
				mySliceBucket.setTaxation(myTax);
				
				/* Distribute the Slice back to the chargeable events */
				theCharges.applyTax(myTax, theCharges.getSliceTotal());
				
				/* Calculate the total tax payable */
				myTax = theCharges.getTaxTotal();
				
				/* HiRate tax is the calculated tax minus the tax payable in the basic band */
				myHiTax.addAmount(myTax);
				
				/* Access the HiGainsBucket */
				myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
						     		.searchFor(TaxClass.GAINSHI));
				myTaxBucket.setParent(myTopBucket);
				
				/* Subtract the basic band from the gains */
				myGains.subtractAmount(pBands.theBasicBand);
				
				/* Set the amount and tax explicitly */
				myTaxBucket.setAmount(myGains);
				myTaxBucket.setTaxation(myHiTax);
			}
			
			/* Re-access the gains */
			mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
					           		.searchFor(TaxClass.GROSSTAXGAINS));
			myGains     = new Number.Money(mySrcBucket.getAmount());
		
			/* Subtract the gains from the tax bands */
			myGains.subtractAmount(pBands.theBasicBand);
			pBands.theBasicBand.setZero();
			if (theYear.hasAdditionalTaxBand())
				pBands.theHiBand.subtractAmount(myGains);
		}
		
		/* Access the TaxDueTaxableGains Bucket */
		myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     	.searchFor(TaxClass.TAXDUETAXGAINS));
		myTaxBucket.setTaxation(myTax);
		
		/* Return the tax bucket */
		return myTaxBucket;
	}
	
	/**
	 * calculate the tax due on capital gains
	 * @param pBands the remaining allowances and tax bands
	 * @return the capital gains tax bucket
	 */
	public TaxBucket calculateCapitalGainsTax(taxBands pBands) {
		TranBucket     	mySrcBucket;
		TaxBucket     	myTaxBucket;
		TaxBucket     	myTopBucket;
		Number.Money 	myCapital;
		Number.Money 	myAllowance;
		Number.Money 	myTax		= new Number.Money(0);
		TaxRegime		myRegime	= theYear.getTaxRegime();
		boolean			isFinished  = false;
		
		/* Access Capital */
		mySrcBucket = theTransBuckets.getSummaryBucket(theTaxTypes
				           		.searchFor(TaxClass.GROSSCAPGAINS));
		myCapital   = new Number.Money(mySrcBucket.getAmount());
	
		/* Store the total into the TaxDueCapital Bucket */
		myTopBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     	.searchFor(TaxClass.TAXDUECAPGAINS));
		myTopBucket.setAmount(myCapital);
		
		/* Access the FreeGainsBucket */
		myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     	.searchFor(TaxClass.CAPITALFREE));
		myTaxBucket.setParent(myTopBucket);
		
		/* Pick up the capital allowance */
		myAllowance = theYear.getCapitalAllow();
		
		/* If the gains is greater than the capital allowance */
		if (myCapital.compareTo(myAllowance) > 0) {
			/* Set the tax bucket and add the tax */
			myTax.addAmount(myTaxBucket.setAmount(myAllowance));
			
			/* Adjust the gains to remove allowance */
			myCapital.subtractAmount(myAllowance);
		}
		
		/* else allowance is sufficient */
		else {
			/* Set the correct value for the tax bucket and note that we have finished */
			myTax.addAmount(myTaxBucket.setAmount(myCapital));
			isFinished = true;
		}

		/* If we have gains left */
		if (!isFinished) {
			/* Access the BasicGainsBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
				     			.searchFor(TaxClass.CAPITALBASIC));
			myTaxBucket.setRate((myRegime.hasCapitalGainsAsIncome()
									? theYear.getBasicTaxRate()
									: theYear.getCapTaxRate()));
			myTaxBucket.setParent(myTopBucket);
				
			/* Determine whether we need to use basic tax band */
			boolean bUseBasicBand = ((myRegime.hasCapitalGainsAsIncome()) ||
				         		 	 (theYear.getHiCapTaxRate() != null));
		
			/* If the gains is greater than the Basic Tax Band and we have no higher rate */
			if ((myCapital.compareTo(pBands.theBasicBand) > 0) ||
				(!bUseBasicBand)){
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));
				
				/* Adjust the gains to remove BasicBand */
				myCapital.subtractAmount(pBands.theBasicBand);
				pBands.theBasicBand.setZero();
			}
				
			/* else we still have band left after gains */
			else {
				/* Set the tax bucket and add the tax */
				myTax.addAmount(myTaxBucket.setAmount(myCapital));
				
				/* Adjust the basicBand to remove capital and note that we have finished */
				if (bUseBasicBand)
					pBands.theBasicBand.subtractAmount(myCapital);
				isFinished = true;
			}
		}
		
		/* If we have gains left */
		if (!isFinished) {
			/* Access the HiGainsBucket */
			myTaxBucket = theTaxBuckets.getTaxBucket(theTaxTypes
					     		.searchFor(TaxClass.CAPITALHI));
			myTaxBucket.setRate((myRegime.hasCapitalGainsAsIncome()
					? theYear.getHiTaxRate()
					: theYear.getHiCapTaxRate()));
			myTaxBucket.setParent(myTopBucket);
				
			/* Set the tax bucket and add the tax */
			myTax.addAmount(myTaxBucket.setAmount(myCapital));
		}
		
		/* Store the taxation total */
		myTopBucket.setTaxation(myTax);
		
		/* Return the tax bucket */
		return myTopBucket;
	}
	
	/**
	 * Transaction Bucket list
	 */
	public class TranList extends SortedList<TranBucket> {
		/**
		 * Construct a top-level List
		 */
		public TranList() { }

		/**
		 * Search for a particular bucket
		 * 
		 * @param pName  Name of Item
		 * @param pOrder Order of Item
		 * @param pType  Type of bucket
		 * @return The Item if present (or null)
		 */
		protected TranBucket searchFor(String pName, int pOrder, BucketType pType) {
			ListIterator	myIterator;
			TranBucket 		myCurr;
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
		 * Obtain or allocate a bucket for an Event
		 * 
		 * @param  pEvent Event
		 * @return The Transact Bucket
		 */
		private TranBucket getTransBucket(Event pEvent) {
			TransactionType myTransType;
			Account			myAccount;
			TranBucket   	myBucket;
			
			/* Access the TransType and account type */
			myTransType = pEvent.getTransType();
			myAccount 	= pEvent.getDebit();
			
			/* If this is Tax Free interest */
			if ((myTransType.isInterest()) && (myAccount.isTaxFree())) {
				/* Swap the transaction type */
				myTransType = theTransTypes.searchFor(TransClass.TAXFREEINTEREST);
			}
			
			/* If this is Dividend */
			else if (myTransType.isDividend()) {
				/* If this is Tax Free */
				if (myAccount.isTaxFree()) {
					/* Swap the transaction type */
					myTransType = theTransTypes.searchFor(TransClass.TAXFREEDIVIDEND);					
				}
				
				/* else if this is unit trust, switch the transaction */
				else if (myAccount.isUnitTrust()) {
					/* Swap the transaction type */
					myTransType = theTransTypes.searchFor(TransClass.UNITTRUSTDIV);
				}
			}
			
			/* Find the appropriate transaction bucket */
			myBucket = getTransBucket(myTransType);
					
			/* Return the bucket */
			return myBucket;
		}
			
		/**
		 * Obtain or allocate a bucket for a transact type
		 * 
		 * @param  pTransact Transaction Type
		 * @return The Transact Bucket
		 */
		protected TranBucket getTransBucket(TransactionType pTransact) {
			TranBucket   myBucket;
			
			/* Find an existing transact bucket */
			myBucket = searchFor(pTransact.getName(), pTransact.getOrder(), BucketType.DETAIL);
					
			/* If we did not find a bucket */
			if (myBucket == null) {
				/* Allocate a new bucket and add to the list */
				myBucket = new TranBucket(this, pTransact);
				myBucket.addToList();
			}
					
			/* Return the bucket */
			return myBucket;
		}
			
		/**
		 * Obtain or allocate a bucket for a tax type
		 * 
		 * @param  pTaxType Tax Type
		 * @return The Transact Bucket
		 */
		protected TranBucket getSummaryBucket(TaxType pTaxType) {
			TranBucket   myBucket;
			
			/* Find an existing tax bucket */
			myBucket = searchFor(pTaxType.getName(), pTaxType.getOrder(), BucketType.SUMMARY);
					
			/* If we did not find a bucket */
			if (myBucket == null) {
				/* Allocate a new bucket and add to the list */
				myBucket = new TranBucket(this, pTaxType);
				myBucket.addToList();
			}
					
			/* Return the bucket */
			return myBucket;
		}
		
		/**
		 * Resolve a transaction list to remove null entries
		 */
		protected void prune() {
			ListIterator	myIterator;
			TranBucket 		myCurr;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the items to find the match */
			while ((myCurr = myIterator.next()) != null) {
				/* If we have an empty bucket */
			    if ((!myCurr.getAmount().isNonZero()) &&
				    (!myCurr.getOldAmount().isNonZero())) {
			   
					/* Unlink the entry */
			    	myIterator.remove();
			    }
			}
			
			/* Return */
			return;
		}
	}
		
	/**
	 * Tax Bucket list
	 */
	public class TaxList extends SortedList<TaxBucket> {		
		/**
		 * Construct a top-level List
		 */
		public TaxList() { }

		/**
		 * Search for a particular bucket
		 * 
		 * @param pName  Name of Item
		 * @param pOrder Order of Item
		 * @param pType  Type of bucket
		 * @return The Item if present (or null)
		 */
		protected TaxBucket searchFor(String pName, int pOrder, BucketType pType) {
			ListIterator	myIterator;
			TaxBucket  		myCurr;
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
		 * Obtain or allocate a bucket for a tax type
		 * 
		 * @param  pTaxType Tax Type
		 * @return The Transact Bucket
		 */
		protected TaxBucket getTaxBucket(TaxType pTaxType) {
			TaxBucket   myBucket;
			
			/* Find an existing tax bucket */
			myBucket = searchFor(pTaxType.getName(), pTaxType.getOrder(), getTaxBucketType(pTaxType));
					
			/* If we did not find a bucket */
			if (myBucket == null) {
				/* Allocate a new bucket and add to the list */
				myBucket = new TaxBucket(this, pTaxType);
				myBucket.addToList();
			}
					
			/* Return the bucket */
			return myBucket;
		}
		
		/**
		 * Resolve a transaction list to remove null entries
		 */
		protected void prune() {
			ListIterator	myIterator;
			TaxBucket 		myCurr;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the items to find the match */
			while ((myCurr = myIterator.next()) != null) {				
				/* If we have an empty bucket */
		    	if (((myCurr.getAmount() == null) ||
			         (!myCurr.getAmount().isNonZero())) &&
			    	((myCurr.getTaxation() == null) ||
			    	 (!myCurr.getTaxation().isNonZero()))) {
					/* Unlink the entry */
			    	myIterator.remove();
			    }
			}
			
			/* Return */
			return;
		}
	}
		
	/**
	 * The transaction bucket class
	 */
	public class TranBucket  implements SortedList.linkObject {
		/**
		 * The List that this bucket belongs to
		 */
		private 	TranList		theList		   	= null;
		
	    /**
		 * Storage for the List Node
		 */
	    private 	Object			theLink			= null;

		/* Members */
		private		String			theName      	= null;
		private 	int             theOrder 	 	= -1;
		private 	BucketType		theBucket	 	= null;
		private 	TransactionType theTransact  	= null;
		private 	TaxType   		theTaxType   	= null;
		private		Number.Money    theAmount    	= null;
		private     Number.Money    theOldAmount 	= null;
		private     Number.Money    theTaxCredit 	= null;
		private     Number.Money    theOldTaxCred	= null;
		
		/**
		 * Add the item to the list 
		 */
		public void			addToList()  { theList.add(this); }
		
		/**
		 * Unlink the item from the list
		 */
		public void			unLink()     { theList.remove(this); }
		
		/* Access methods */
		public 	String			getName()      	{ return theName; }
		private int			 	getOrder()     	{ return theOrder; }
		public 	BucketType		getBucket()   	{ return theBucket; }
		public  TransactionType getTransType() 	{ return theTransact; }
		public  TaxType    		getTaxType()   	{ return theTaxType; }
		public  Number.Money    getAmount()    	{ return theAmount; }
		public  Number.Money    getOldAmount() 	{ return theOldAmount; }
		public  Number.Money    getTaxCredit()  { return theTaxCredit; }
		public  Number.Money    getOldTaxCred()	{ return theOldTaxCred; }

		/**
		 * Get the link node for this item
		 * @return the Link node or <code>null</code>
		 */
		public Object		getLinkNode(Object pList)	{ return theLink; }

		/**
		 * Get the link node for this item
		 * @return the Link node or <code>null</code>
		 */
		public void			setLinkNode(Object l, Object o)	{ theLink = o; }

		/**
		 * Determine whether the item is visible to standard searches
		 * @return <code>true/false</code>
		 */
		public boolean		isHidden()    	{ return false; }

		/* Constructors */
		private TranBucket(TranList pList, TransactionType pTransact) { 
			theList      	= pList;
			theName      	= pTransact.getName();
			theOrder     	= pTransact.getOrder();
			theBucket    	= BucketType.DETAIL;
			theTransact  	= pTransact;
			theAmount    	= new Number.Money(0);
			theOldAmount 	= new Number.Money(0);
			theTaxCredit	= new Number.Money(0);
			theOldTaxCred	= new Number.Money(0);
		}
		private TranBucket(TranList pList, TaxType pTaxType) {
			theList      = pList;
			theName      = pTaxType.getName();
			theOrder     = pTaxType.getOrder();
			theBucket    = BucketType.SUMMARY;
			theTaxType   = pTaxType;
			theAmount    = new Number.Money(0);
			theOldAmount = new Number.Money(0);
		}
		private TranBucket(TranList pList, TranBucket pBucket) { 
			theList      = pList;
			theName      = pBucket.getName();
			theOrder     = pBucket.getOrder();
			theBucket    = pBucket.getBucket();
			theTaxType   = pBucket.getTaxType();
			theTransact  = pBucket.getTransType();
			theAmount    = new Number.Money(0);
			theOldAmount = new Number.Money(pBucket.getAmount());
			if (theBucket == BucketType.DETAIL) {
				theTaxCredit 	= new Number.Money(0);
				theOldTaxCred 	= new Number.Money(pBucket.getTaxCredit());
			}
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
			
			/* Make sure that the object is a TranBucket */
			if (pThat.getClass() != this.getClass()) return -1;
			
			/* Access the object as a TaxBucket */
			TranBucket myThat = (TranBucket)pThat;
			
			/* Compare the bucket order */
			if (AnalysisYear.getBucketOrder(theBucket) < AnalysisYear.getBucketOrder(myThat.theBucket)) return -1;
			if (AnalysisYear.getBucketOrder(theBucket) > AnalysisYear.getBucketOrder(myThat.theBucket)) return  1;
			
			/* Compare the order */
			if (theOrder < myThat.theOrder) return -1;
			if (theOrder > myThat.theOrder) return  1;
			
			/* Compare the name */
			result = theName.compareTo(myThat.theName);
			if (result == 0) return 0;
			else if (result < 0) return -1;
			else return 1;
		}

		/**
		 * Add the event to the bucket
		 * 
		 * @param  pEvent Event to add
		 */
		protected void addEvent(Event pEvent) {
			Number.Money   myAmount = pEvent.getAmount();
			Number.Money   myTax	= pEvent.getTaxCredit();

			/* Adjust the total amount */
			theAmount.addAmount(myAmount);
			
			/* Adjust the taxation if present */
			if (myTax != null) theTaxCredit.addAmount(myTax);
		}
		
		/**
		 * Add an amount
		 * 
		 * @param  pAmount Amount to add
		 */
		protected void addAmount(Number.Money pAmount) {
			/* Adjust the income total */
			theAmount.addAmount(pAmount);
		}
		
		/**
		 * Subtract an amount
		 * 
		 * @param  pAmount Amount to subtract
		 */
		protected void subtractAmount(Number.Money pAmount) {
			/* Adjust the income total */
			theAmount.subtractAmount(pAmount);
		}
		
		/**
		 * Add a bucket to a totalling bucket
		 * 
		 * @param  pBucket Bucket to add
		 */
		protected void addBucket(TranBucket pBucket) {
			Number.Money myAmount = pBucket.getAmount();

			/* If this is the tax paid bucket and we are adding a tax creditable bucket */
			if ((theTaxType.isTaxPaid()) &&
				((pBucket.getTransType() != null) &&
				 (pBucket.getTransType().needsTaxCredit()))) {
				/* Just add the tax credits into the amount */
				theAmount.addAmount(pBucket.getTaxCredit());					
				theOldAmount.addAmount(pBucket.getOldTaxCred());					
			}
			
			/* else handle normally */
			else {
				/* Adjust the money total */
				theAmount.addAmount(myAmount);
				theOldAmount.addAmount(pBucket.getOldAmount());
			
				/* If this is a tax creditable bucket */
				if (theTaxType.hasTaxCredits()) {
					/* Add the tax credits into the amount */
					theAmount.addAmount(pBucket.getTaxCredit());
					theOldAmount.addAmount(pBucket.getOldTaxCred());					
				}
			}
		}
		
		/**
		 * Subtract a bucket from a totalling bucket
		 * 
		 * @param  pBucket Bucket to subtract
		 */
		protected void subtractBucket(TranBucket pBucket) {
			Number.Money myAmount = pBucket.getAmount();

			/* Adjust the money total */
			theAmount.subtractAmount(myAmount);
			theOldAmount.subtractAmount(pBucket.getOldAmount());
		}
	}
	
	/* The Tax bucket class */
	public class TaxBucket  implements SortedList.linkObject {
		/**
		 * The List that this bucket belongs to
		 */
		private 	TaxList			theList		   = null;
		
	    /**
		 * Storage for the List Node
		 */
	    private 	Object			theLink		= null;

		/* Members */
		private		String			theName      = null;
		private 	int             theOrder 	 = -1;
		private 	BucketType		theBucket	 = null;
		private 	TaxType   		theTaxType   = null;
		private		Number.Money    theAmount    = null;
		private     Number.Money    theTaxation  = null;
		private		Number.Rate		theRate      = null;
		private 	TaxBucket		theParent 	 = null;
		
		/**
		 * Add the item to the list 
		 */
		public void			addToList()  { theList.add(this); }
		
		/**
		 * Unlink the item from the list
		 */
		public void			unLink()     { theList.remove(this); }
		
		/* Access methods */
		public 	String			getName()      { return theName; }
		public 	BucketType		getBucket()    { return theBucket; }
		public  TaxType    		getTaxType()   { return theTaxType; }
		public  Number.Money    getAmount()    { return theAmount; }
		public  Number.Money    getTaxation()  { return theTaxation; }
		public  Number.Rate		getRate()      { return theRate; }
		public  TaxBucket		getParent()	   { return theParent; }

		/**
		 * Get the link node for this item
		 * @return the Link node or <code>null</code>
		 */
		public Object		getLinkNode(Object pList)	{ return theLink; }

		/**
		 * Get the link node for this item
		 * @return the Link node or <code>null</code>
		 */
		public void			setLinkNode(Object l, Object o)	{ theLink = o; }

		/**
		 * Determine whether the item is visible to standard searches
		 * @return <code>true/false</code>
		 */
		public boolean		isHidden()    	{ return false; }

		/* Constructor */
		private TaxBucket(TaxList pList, TaxType pTaxType) {
			theList      = pList;
			theName      = pTaxType.getName();
			theOrder     = pTaxType.getOrder();
			theBucket    = getTaxBucketType(pTaxType);
			theTaxType   = pTaxType;
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
			
			/* Make sure that the object is a TaxBucket */
			if (pThat.getClass() != this.getClass()) return -1;
			
			/* Access the object as a TaxBucket */
			TaxBucket myThat = (TaxBucket)pThat;
			
			/* Compare the bucket order */
			if (AnalysisYear.getBucketOrder(theBucket) < AnalysisYear.getBucketOrder(myThat.theBucket)) return -1;
			if (AnalysisYear.getBucketOrder(theBucket) > AnalysisYear.getBucketOrder(myThat.theBucket)) return  1;
			
			/* Compare the order */
			if (theOrder < myThat.theOrder) return -1;
			if (theOrder > myThat.theOrder) return  1;
			
			/* Compare the name */
			result = theName.compareTo(myThat.theName);
			if (result == 0) return 0;
			else if (result < 0) return -1;
			else return 1;
		}

		/**
		 * Set a taxation amount and calculate the tax on it
		 * 
		 * @param  	pAmount 		Amount to set
		 * @return the taxation on this bucket
		 */
		protected Number.Money setAmount(Number.Money pAmount) {
			/* Set the value */
			theAmount    = new Number.Money(pAmount);
            
            /* Calculate the tax if we have a rate*/
			theTaxation = (theRate != null) ? theAmount.valueAtRate(theRate)
											: new Number.Money(0);
			
			/* Return the taxation amount */
			return theTaxation;
		}
		
		/**
		 * Set explicit taxation value 
		 * 
		 * @param  	pAmount 		Amount to set
		 */
		protected void setTaxation(Number.Money pAmount) {
			/* Set the value */
			theTaxation = new Number.Money(pAmount);
		}
		
		/**
		 * Set parent bucket for reporting purposes
		 * @param  	pParent the parent bucket
		 */
		protected void setParent(TaxBucket pParent) {
			/* Set the value */
			theParent = pParent;
		}
		
		/**
		 * Set a tax rate
		 * 
		 * @param  pRate 	 Amount to set
		 */
		protected void setRate(Number.Rate pRate) {
			/* Set the value */
			theRate    = pRate;
		}
	}
	
	/**
	 * Class to hold active allowances and tax bands
	 */
	private class taxBands {
		/* properties */
		private Number.Money 	theAllowance = null;
		private Number.Money		theLoBand	 = null;
		private Number.Money		theBasicBand = null;
		private Number.Money		theHiBand	 = null;
	}

	
	private static BucketType getTaxBucketType(TaxType pTaxType) {
		BucketType myBucket = BucketType.STATIC;
		int        myOrder  = pTaxType.getOrder();
		switch (myOrder / TaxType.CLASSDIVIDE) {
			case 1: myBucket = BucketType.DETAIL; break;
			case 2: myBucket = BucketType.SUMMARY; break;
			case 3: myBucket = BucketType.TOTAL; break;
		}
		return myBucket;
	}
}
