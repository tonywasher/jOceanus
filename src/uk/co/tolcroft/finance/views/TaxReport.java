package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.data.TaxType.*;
import uk.co.tolcroft.finance.views.AnalysisYear.*;
import uk.co.tolcroft.finance.views.TaxAnalysis.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number.*;

public class TaxReport {
	/* Properties */
	private TaxAnalysis 	theTax 		  = null;
	private Date  			theDate 	  = null;
	private Properties		theProperties = null;
	
	/* Constructor */
	public TaxReport(Properties pProperties, TaxAnalysis pTax) {
		theTax  	  = pTax;
		theDate       = pTax.getDate();
		theProperties = pProperties;
	}
	
	/**
	 * Build a web output of the transaction report
	 * 
	 * @return Web output
	 */
	public String getTransReport() {
		TranBucket  						myBucket;
		StringBuilder		        		myOutput = new StringBuilder(10000);
		TaxAnalysis.TranList 				myList;
		SortedList<TranBucket>.ListIterator	myIterator;

		/* Ensure that totals have been produced */
		theTax.produceTotals(theProperties);
		
		/* Access the bucket lists */
		myList = theTax.getTransBuckets();
		
		/* Format the header */
		myOutput.append("<html><body><a name=\"Top\">");
		myOutput.append("<h1 align=\"center\">Transaction Report for ");
		myOutput.append(theDate.getYear());
		myOutput.append("</h1></a>");
		myOutput.append("<a name=\"TransactionTotals\"><h2 align=\"center\">Transaction Totals</h2></a>");
		myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">"); 
		myOutput.append("<thead><th rowspan=\"2\">Class</th><th colspan=\"2\">Value</th></thead>");
		myOutput.append("<thead><th>");
		myOutput.append(theDate.getYear());
		myOutput.append("</th><th>");
		myOutput.append(theDate.getYear()-1);
		myOutput.append("</th></thead>");
		myOutput.append("<tbody>");
	
		/* Access the bucket iterator */
		myIterator = myList.listIterator();
		
		/* Loop through the Transaction Summary Buckets */
		while ((myBucket = myIterator.next()) != null) {
			/* Skip the non-summary items */
			if (myBucket.getBucket() != BucketType.SUMMARY) continue;
							
			/* Format the detail */
			myOutput.append("<tr><th align=\"center\">" + myBucket.getName() + "</th>");
			myOutput.append(Report.makeMoneyItem(myBucket.getAmount()));
			myOutput.append(Report.makeMoneyItem(myBucket.getOldAmount()));
			myOutput.append("</tr>");
		}

		/* Format the next table */
		myOutput.append("</tbody></table>");
		myOutput.append("<a name=\"Trans\"><h2 align=\"center\">Transaction Breakdown</h2></a>");
		myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">"); 
		myOutput.append("<thead><th rowspan=\"2\">Class</th>");
		myOutput.append("<th colspan=\"2\">");
		myOutput.append(theDate.getYear());
		myOutput.append("</th><th  colspan=\"2\">");
		myOutput.append(theDate.getYear()-1);
		myOutput.append("</th></thead>");
		myOutput.append("<thead><th>Value</th><th>TaxCredit</th><th>Value</th><th>TaxCredit</th></thead>");
		myOutput.append("<tbody>");
			
		/* Access a new bucket iterator */
		myIterator = myList.listIterator();
		
		/* Loop through the Transaction Summary Buckets */
		while ((myBucket = myIterator.next()) != null) {
			/* Break loop if we have completed the details */
			if (myBucket.getBucket() != BucketType.DETAIL) break;
							
			/* Format the detail */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myBucket.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeMoneyItem(myBucket.getAmount()));
			myOutput.append(Report.makeMoneyItem(myBucket.getTaxCredit()));
			myOutput.append(Report.makeMoneyItem(myBucket.getOldAmount()));
			myOutput.append(Report.makeMoneyItem(myBucket.getOldTaxCred()));
			myOutput.append("</tr>");
		}			 
		
		/* Close the table */
		myOutput.append("</tbody></table></body></html>");
		
		/* Return the output */
		return myOutput.toString();
	}
	
	/**
	 * Build a web output of the taxation report
	 * 
	 * @return Web output
	 */
	public String getTaxReport() {
		TaxBucket  							myTaxBucket;
		TranBucket							myTranBucket;
		StringBuilder		       			myOutput = new StringBuilder(10000);
		StringBuilder			  			myDetail = new StringBuilder(10000);
		TaxAnalysis.TaxList    				myTaxList;
		TaxAnalysis.TranList    			myTranList;
		SortedList<TaxBucket>.ListIterator	myIterator;
		TaxYear					   			myYear;
		TaxType.List	   					myTaxTypes;

		/* Ensure that totals have been produced */
		theTax.produceTotals(theProperties);
		
		/* Access the bucket lists */
		myTaxList  = theTax.getTaxBuckets();
		myTranList = theTax.getTransBuckets();
		myYear	   = theTax.getYear();
		myTaxTypes = theTax.getTaxTypes();
		
		/* Initialise the detail */
		myDetail.append("<h1 align=\"center\">Taxation Breakdown</h1>");
		
		/* Format the header */
		myOutput.append("<html><body><a name=\"Top\">");
		myOutput.append("<h1 align=\"center\">Taxation Report for ");
		myOutput.append(theDate.getYear());
		myOutput.append("</h1></a>");
		myOutput.append("<a name=\"TaxSummary\"><h2 align=\"center\">Taxation Summary</h2></a>");
		myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">"); 
		myOutput.append("<thead><th>Class</th><th>Total Income</th><th>Taxation Due</th></thead>");
		myOutput.append("<tbody>");
	
		/* Access the tax bucket iterator */
		myIterator = myTaxList.listIterator();
		
		/* Loop through the Transaction Summary Buckets */
		while ((myTaxBucket = myIterator.next()) != null) {
			/* Skip the detail elements */
			if (myTaxBucket.getBucket() == BucketType.DETAIL) continue;
							
			/* Break loop if we are on to the totals */
			if (myTaxBucket.getBucket() != BucketType.SUMMARY) break;
							
			/* Format the line */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append("<a href=\"#Detail");
			myOutput.append(myTaxBucket.getName());
			myOutput.append("\">");
			myOutput.append(myTaxBucket.getName());
			myOutput.append("</a></th>");
			myOutput.append(Report.makeMoneyItem(myTaxBucket.getAmount()));
			myOutput.append(Report.makeMoneyItem(myTaxBucket.getTaxation()));
			myOutput.append("</tr>");
			
			/* Format the detail */
			myDetail.append(makeTaxReport(myTaxBucket));
		}

		/* Access the Total taxation bucket */
		myTaxBucket = myTaxList.getTaxBucket(myTaxTypes.searchFor(TaxClass.TOTALTAX));
		myOutput.append("<tr><th align=\"center\">");
		myOutput.append(myTaxBucket.getName());
		myOutput.append("</th>");
		myOutput.append(Report.makeMoneyTotal(myTaxBucket.getAmount()));
		myOutput.append(Report.makeMoneyTotal(myTaxBucket.getTaxation()));
		myOutput.append("</tr>");

		/* Access the Tax Paid bucket */
		myTranBucket = myTranList.getSummaryBucket(myTaxTypes.searchFor(TaxClass.TAXPAID));
		myOutput.append("<tr><th align=\"center\">");
		myOutput.append(myTranBucket.getName());
		myOutput.append("</th>");
		myOutput.append(Report.makeMoneyTotal(new Money(0)));
		myOutput.append(Report.makeMoneyTotal(myTranBucket.getAmount()));
		myOutput.append("</tr>");

		/* Access the Tax Profit bucket */
		myTaxBucket = myTaxList.getTaxBucket(myTaxTypes.searchFor(TaxClass.TAXPROFIT));
		myOutput.append("<tr><th align=\"center\">");
		myOutput.append(myTaxBucket.getName());
		myOutput.append("</th>");
		myOutput.append(Report.makeMoneyTotal(myTaxBucket.getAmount()));
		myOutput.append(Report.makeMoneyTotal(myTaxBucket.getTaxation()));
		myOutput.append("</tr>");

		/* Finish the table */
		myOutput.append("</tbody></table>");

		/* Format the tax parameters */
		myOutput.append("<a name=\"TaxParms\"><h1 align=\"center\">Taxation Parameters</h1></a>");

		/* Format the allowances */
		myOutput.append("<a name=\"Allowances\"><h2 align=\"center\">Allowances</h2></a>");
		myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">");
		myOutput.append("<thead><th>Name</th><th>Value</th></thead>");
		myOutput.append("<tbody>");
		myOutput.append("<tr><th>PersonalAllowance</th>");
		myOutput.append(Report.makeMoneyItem(myYear.getAllowance()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>Age 65-74 PersonalAllowance</th>");
		myOutput.append(Report.makeMoneyItem(myYear.getLoAgeAllow()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>Age 75+ PersonalAllowance</th>");
		myOutput.append(Report.makeMoneyItem(myYear.getHiAgeAllow()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>RentalAllowance</th>");
		myOutput.append(Report.makeMoneyItem(myYear.getRentalAllowance()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>CapitalAllowance</th>");
		myOutput.append(Report.makeMoneyItem(myYear.getCapitalAllow()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>Income Limit for AgeAllowance</th>");
		myOutput.append(Report.makeMoneyItem(myYear.getAgeAllowLimit()));
		myOutput.append("</tr>");
		if (myYear.hasAdditionalTaxBand()) {
			myOutput.append("<tr><th>Income Limit for PersonalAllowance</th>");
			myOutput.append(Report.makeMoneyItem(myYear.getAddAllowLimit()));
			myOutput.append("</tr>");
		}
		myOutput.append("</tbody></table>");
		
		/* Format the Rates */
		myOutput.append("<a name=\"Allowances\"><h2 align=\"center\">TaxRates</h2></a>");
		myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">");
		myOutput.append("<thead><th>IncomeType</th><th>LoRate</th>");
		myOutput.append("<th>BasicRate</th><th>HiRate</th>");
		if (myYear.hasAdditionalTaxBand()) 
			myOutput.append("<th>AdditionalRate</th>");
		myOutput.append("</thead><tbody>");
		myOutput.append("<tr><th>Salary/Rental</th>");
		myOutput.append(Report.makeRateItem(myYear.hasLoSalaryBand()  
												? myYear.getLoTaxRate()
												: null));
		myOutput.append(Report.makeRateItem(myYear.getBasicTaxRate()));
		myOutput.append(Report.makeRateItem(myYear.getHiTaxRate()));
		if (myYear.hasAdditionalTaxBand()) 
			myOutput.append(Report.makeRateItem(myYear.getAddTaxRate()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>Interest</th>");
		myOutput.append(Report.makeRateItem(myYear.getLoTaxRate()));
		myOutput.append(Report.makeRateItem(myYear.getIntTaxRate()));
		myOutput.append(Report.makeRateItem(myYear.getHiTaxRate()));
		if (myYear.hasAdditionalTaxBand()) 
			myOutput.append(Report.makeRateItem(myYear.getAddTaxRate()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>Dividends</th>");
		myOutput.append(Report.makeRateItem(null));
		myOutput.append(Report.makeRateItem(myYear.getDivTaxRate()));
		myOutput.append(Report.makeRateItem(myYear.getHiDivTaxRate()));
		if (myYear.hasAdditionalTaxBand()) 
			myOutput.append(Report.makeRateItem(myYear.getAddDivTaxRate()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>TaxableGains</th>");
		myOutput.append(Report.makeRateItem(null));
		myOutput.append(Report.makeRateItem(myYear.getBasicTaxRate()));
		myOutput.append(Report.makeRateItem(myYear.getHiTaxRate()));
		if (myYear.hasAdditionalTaxBand()) 
			myOutput.append(Report.makeRateItem(myYear.getAddTaxRate()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>CapitalGains</th>");
		myOutput.append(Report.makeRateItem(null));
		myOutput.append(Report.makeRateItem(myYear.getCapTaxRate()));
		myOutput.append(Report.makeRateItem(myYear.getHiCapTaxRate()));
		if (myYear.hasAdditionalTaxBand()) 
			myOutput.append(Report.makeRateItem(null));
		myOutput.append("</tr>");
		myOutput.append("</tbody></table>");
		
		/* Format the tax bands */
		myOutput.append("<a name=\"Allowances\"><h2 align=\"center\">TaxBands</h2></a>");
		myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">");
		myOutput.append("<thead><th>Name</th><th>Value</th></thead>");
		myOutput.append("<tbody>");
		myOutput.append("<tr><th>Age for Tax Year</th>");
		myOutput.append("<td align=\"right\" color=\"blue\">");
		myOutput.append(theTax.getAge());
		myOutput.append("</td></tr>");

		/* Access the original allowance */
		myTaxBucket = myTaxList.getTaxBucket(myTaxTypes
									.searchFor(TaxClass.ORIGALLOW));
		myOutput.append("<tr><th>Personal Allowance</th>");
		myOutput.append(Report.makeMoneyItem(myTaxBucket.getAmount()));
		myOutput.append("</tr>");

		/* if we have adjusted the allowance */
		if (theTax.hasReducedAllow()) {
			/* Access the gross income */
			myTaxBucket = myTaxList.getTaxBucket(myTaxTypes
										.searchFor(TaxClass.GROSSINCOME));
			myOutput.append("<tr><th>Gross Taxable Income</th>");
			myOutput.append(Report.makeMoneyItem(myTaxBucket.getAmount()));
			myOutput.append("</tr>");

			/* Access the gross income */
			myTaxBucket = myTaxList.getTaxBucket(myTaxTypes
										.searchFor(TaxClass.ADJALLOW));
			myOutput.append("<tr><th>Adjusted Allowance</th>");
			myOutput.append(Report.makeMoneyItem(myTaxBucket.getAmount()));
			myOutput.append("</tr>");
		}
		
		/* Access the Low Tax Band */
		if (myYear.getLoBand() != null) {
			myOutput.append("<tr><th>Low Tax Band</th>");
			myOutput.append(Report.makeMoneyItem(myYear.getLoBand()));
			myOutput.append("</tr>");
		}

		/* Access the Basic Tax Band */
		myOutput.append("<tr><th>Basic Tax Band</th>");
		myOutput.append(Report.makeMoneyItem(myYear.getBasicBand()));
		myOutput.append("</tr>");

		/* If we have a high tax band */
		if (myYear.hasAdditionalTaxBand()) {
			/* Access the gross income */
			myTaxBucket = myTaxList.getTaxBucket(myTaxTypes
										.searchFor(TaxClass.HITAXBAND));
			myOutput.append("<tr><th>High Tax Band</th>");
			myOutput.append(Report.makeMoneyItem(myTaxBucket.getAmount()));
			myOutput.append("</tr>");				
		}
		myOutput.append("</tbody></table>");
		
			/* Add the detail */
		myOutput.append(myDetail);
		
		/* If we need a tax slice report */
		if (theTax.hasGainsSlices())
			myOutput.append(makeTaxSliceReport());
		
		/* Close the document */
		myOutput.append("</body></html>");
		
		/* Return the output */
		return myOutput.toString();
	}
	
	/**
	 * Build a standard tax report element
	 * 
	 * @return Web output
	 */
	public StringBuilder makeTaxReport(TaxAnalysis.TaxBucket pSummary) {
		StringBuilder						myOutput = new StringBuilder(1000);
		TaxBucket							myBucket;
		TaxAnalysis.TaxList 				myList;
		SortedList<TaxBucket>.ListIterator	myIterator;

		/* Access the bucket lists */
		myList = theTax.getTaxBuckets();
		
		/* Format the detail */
		myOutput.append("<a name=\"Detail");
		myOutput.append(pSummary.getName());
		myOutput.append("\">");
		myOutput.append("<h2 align=\"center\">");
		myOutput.append(pSummary.getName());
		myOutput.append("</h2></a>");
		myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">"); 
		myOutput.append("<thead><th>Class</th><th>Income</th>");
		myOutput.append("<th>Rate</th><th>Taxation Due</th></thead>");
		myOutput.append("<tbody>");
		
		/* Access the tax bucket iterator */
		myIterator = myList.listIterator();
		
		/* Loop through the Transaction Summary Buckets */
		while ((myBucket = myIterator.next()) != null) {
			/* Break loop if we have completed the details */
			if (myBucket.getBucket() != BucketType.DETAIL) break;
			
			/* Skip record if incorrect parent */
			if (myBucket.getParent() != pSummary) continue;

			/* Format the detail */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myBucket.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeMoneyItem(myBucket.getAmount()));
			myOutput.append(Report.makeRateItem(myBucket.getRate()));
			myOutput.append(Report.makeMoneyItem(myBucket.getTaxation()));
			myOutput.append("</tr>");
		}			 
		
		myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
		myOutput.append(Report.makeMoneyTotal(pSummary.getAmount()));
		myOutput.append(Report.makeRateItem(null));
		myOutput.append(Report.makeMoneyTotal(pSummary.getTaxation()));
		myOutput.append("</tr></tbody></table>");
		
		/* Return the output */
		return myOutput;
	}
	
	/**
	 * Build a tax slice report
	 * 
	 * @return Web output
	 */
	public StringBuilder makeTaxSliceReport() {
		StringBuilder		myOutput = new StringBuilder(1000);
		ChargeableEvent		myCharge;
		TaxBucket	    	myBucket;
		TaxAnalysis.TaxList myList;
		TaxType.List	   	myTaxTypes;
		
		DataList<ChargeableEvent>.ListIterator myIterator;

		/* Access the bucket lists */
		myList 		= theTax.getTaxBuckets();
		myTaxTypes 	= theTax.getTaxTypes();

		/* Format the detail */
		myOutput.append("<a name=\"DetailChargeableEvents>");
		myOutput.append("<h2 align=\"center\">Chargeable Events</h2></a>");
		myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">"); 
		myOutput.append("<thead><th>Date</th><th>Description</th>");
		myOutput.append("<th>Amount</th><th>TaxCredit</th><th>Years</th>");
		myOutput.append("<th>Slice</th><th>Taxation</th></thead>");
		myOutput.append("<tbody>");
		
		/* Create the list iterator */
		myIterator = theTax.getCharges().listIterator();
		
		/* Loop through the Charges */
		while ((myCharge  = myIterator.next()) != null) {
			/* Format the detail */
			myOutput.append("<tr><td>");
			myOutput.append(myCharge.getDate().formatDate(false));
			myOutput.append("</td><td>");
			myOutput.append(myCharge.getDesc());
			myOutput.append("</td>");
			myOutput.append(Report.makeMoneyItem(myCharge.getAmount()));
			myOutput.append(Report.makeMoneyItem(myCharge.getTaxCredit()));
			myOutput.append("<td>");
			myOutput.append(myCharge.getYears());
			myOutput.append("</td>");
			myOutput.append(Report.makeMoneyItem(myCharge.getSlice()));
			myOutput.append(Report.makeMoneyItem(myCharge.getTaxation()));
			myOutput.append("</tr>");
		}			 
		
		/* Format the totals */
		myOutput.append("<tr><th>Totals</th><td/><td/>");
		myOutput.append(Report.makeMoneyTotal(theTax.getCharges().getGainsTotal()));
		myOutput.append("<td/>");
		myOutput.append(Report.makeMoneyTotal(theTax.getCharges().getSliceTotal()));
		myOutput.append(Report.makeMoneyTotal(theTax.getCharges().getTaxTotal()));
		myOutput.append("</tr></tbody></table>");
			
		/* Access the Summary Tax Due Slice */
		myBucket = myList.getTaxBucket(myTaxTypes
								.searchFor(TaxClass.TAXDUESLICE));
		
		/* Add the Slice taxation details */
		myOutput.append(makeTaxReport(myBucket));
		
		/* Return the output */
		return myOutput;
	}		
}
