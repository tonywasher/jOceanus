package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.views.AnalysisYear.*;
import uk.co.tolcroft.finance.views.IncomeAnalysis.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number;

public class IncomeReport {
	/* Properties */
	private IncomeAnalysis 	theIncome	= null;
	private Date  			theDate 	= null;
	
	/* Constructor */
	public IncomeReport(IncomeAnalysis pIncome) {
		theIncome  = pIncome;
		theDate    = pIncome.getDate();
	}
	
	/**
	 * Build a web output of the report
	 * @return Web output
	 */
	public String getReport() {
		Bucket 							myBucket;
		StringBuilder					myOutput = new StringBuilder(10000);
		Number.Money    				myProfit;
		IncomeAnalysis.List 			myList;
		SortedList<Bucket>.ListIterator	myIterator;

		/* Make sure that totals have been produced */
		theIncome.produceTotals();
		
		/* Access the bucket lists */
		myList = theIncome.getBuckets();
		
		/* Format the header */
		myOutput.append("<html><body><a name=\"Top\">");
		myOutput.append("<h1 align=\"center\">Income/Expense Report for ");
		myOutput.append(theDate.getYear());
		myOutput.append("</h1></a>");
		myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">"); 
		myOutput.append("<thead><th rowspan=\"2\">Name</th>");
		myOutput.append("<th colspan=\"2\">");
		myOutput.append(theDate.getYear());
		myOutput.append("</th>");
		myOutput.append("<th colspan=\"2\">");
		myOutput.append(theDate.getYear()-1);
		myOutput.append("</th></thead>");
		myOutput.append("<thead><th>Income</th><th>Expense</th>");
		myOutput.append("<th>Income</th><th>Expense</th></thead>");
		myOutput.append("<tbody>");
			
		/* Create the bucket iterator */
		myIterator = myList.listIterator();
		
		/* Loop through the Detail Buckets */
		while ((myBucket = myIterator.next()) != null) {
			
			/* Break loop if we have completed the details */
			if (myBucket.getBucket() != BucketType.DETAIL) break;
							
			/* Format the detail */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myBucket.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeMoneyItem(myBucket.getIncome()));
			myOutput.append(Report.makeMoneyItem(myBucket.getExpense()));
			myOutput.append(Report.makeMoneyItem(myBucket.getPrevIncome()));
			myOutput.append(Report.makeMoneyItem(myBucket.getPrevExpense()));
			myOutput.append("</tr>");
		}			 
		
		/* Access the totals */
		myBucket = myList.getTotalsBucket();
		
		/* Format the totals */
		myOutput.append("<tr><th>Totals</th>");
		myOutput.append(Report.makeMoneyTotal(myBucket.getIncome()));
		myOutput.append(Report.makeMoneyTotal(myBucket.getExpense()));
		myOutput.append(Report.makeMoneyTotal(myBucket.getPrevIncome()));
		myOutput.append(Report.makeMoneyTotal(myBucket.getPrevExpense()));
		myOutput.append("</tr>");
		
		/* Format the profit */
		myOutput.append("<tr><th>Profit</th>");
		myProfit = new Number.Money(myBucket.getIncome());
		myProfit.subtractAmount(myBucket.getExpense());
		myOutput.append(Report.makeMoneyProfit(myProfit));
		if (myBucket.getPrevious() != null) {
			myProfit = new Number.Money(myBucket.getPrevIncome());
			myProfit.subtractAmount(myBucket.getPrevExpense());
		}
		myOutput.append(Report.makeMoneyProfit(myProfit));
		myOutput.append("</tr></tbody></table></body></html>");
		
		/* Return the output */
		return myOutput.toString();
	}		
}
