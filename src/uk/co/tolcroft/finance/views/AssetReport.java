package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.AnalysisYear.*;
import uk.co.tolcroft.finance.views.AssetAnalysis.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number.*;

public class AssetReport {
	/* Properties */
	private AssetAnalysis	theAsset	= null;
	private Date  			theDate 	= null;
	
	/* Constructor */
	public AssetReport(AssetAnalysis pAsset) {
		theAsset = pAsset;
		theDate  = pAsset.getDate();
	}
	
	/**
	 * Build a web output of the Year report
	 * @return Web output
	 */
	public String getYearReport() {
		SortedList<Bucket>.ListIterator	myIterator;
		Bucket							myBucket;
		StringBuilder		   			myOutput = new StringBuilder(10000);
		StringBuilder          			myDetail = new StringBuilder(10000);	
		Money		        			myProfit;
		AssetAnalysis.List 				myList;

		/* Endure that totals have been produced */
		theAsset.produceTotals();
		
		/* Access the bucket lists */
		myList = theAsset.getBuckets();
		
		/* Format the header */
		myOutput.append("<html><body><a name=\"Top\">");
		myOutput.append("<h1 align=\"center\">Asset Report for ");
		myOutput.append(theDate.getYear());
		myOutput.append("</h1></a>");
		myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">"); 
		myOutput.append("<thead><th rowspan=\"2\">Class</th><th colspan=\"2\">Value</th></thead>");
		myOutput.append("<thead><th>");
		myOutput.append(theDate.getYear());
		myOutput.append("</th><th>");
		myOutput.append(theDate.getYear()-1);
		myOutput.append("</th></thead>");
		myOutput.append("<tbody>");
		
		/* Access the iterator */
		myIterator = myList.listIterator();
		
		/* Loop through the Summary Buckets */
		while ((myBucket = myIterator.next()) != null) {
			/* Only process summary items */
			if (myBucket.getBucket() != BucketType.SUMMARY) continue;
			
			/* Format the Summary */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append("<a href=\"#Detail");
			myOutput.append(myBucket.getName());
			myOutput.append("\">");
			myOutput.append(myBucket.getName());
			myOutput.append("</a></th>");
			myOutput.append(Report.makeMoneyItem(myBucket.getAmount()));
			myOutput.append(Report.makeMoneyItem(myBucket.getPrevAmount()));
			myOutput.append("</tr>");
				
			/* Format the detail */
			myDetail.append(makeStandardReport(myBucket));
		}
		
		/* Access the totals */
		myBucket = myList.getTotalsBucket();
		
		/* Format the totals */
		myOutput.append("<tr><th>Totals</th>");
		myOutput.append(Report.makeMoneyTotal(myBucket.getAmount()));
		myOutput.append(Report.makeMoneyTotal(myBucket.getPrevAmount()));
		myOutput.append("</tr>");
		
		/* Format the profit */
		myOutput.append("<tr><th>Profit</th>");
		myProfit = new Money(myBucket.getAmount());
		if (myBucket.getPrevAmount() != null)
			myProfit.subtractAmount(myBucket.getPrevAmount());
		myOutput.append(Report.makeMoneyProfit(myProfit));
		myOutput.append("</tr></tbody></table>");
		
		/* Add the detail */
		myOutput.append(myDetail);
		
		/* Terminate the html */
		myOutput.append("</body></html>");
		
		/* Return the output */
		return myOutput.toString();
	}		
	
	/**
	 * Build a web output of the instant report
	 * @return Web output
	 */
	public String getInstantReport() {
		SortedList<Bucket>.ListIterator	myIterator;
		Bucket							myBucket;
		StringBuilder		   			myOutput = new StringBuilder(10000);
		StringBuilder          			myDetail = new StringBuilder(10000);	
		AccountType       				myType;
		AssetAnalysis.List 				myList;

		/* Endure that totals have been produced */
		theAsset.produceTotals();
		
		/* Access the bucket lists */
		myList = theAsset.getBuckets();
		
		/* Format the header */
		myOutput.append("<html><body><a name=\"Top\">");
		myOutput.append("<h1 align=\"center\">Instant Asset Report for ");
		myOutput.append(theDate.formatDate(false));
		myOutput.append("</h1></a>");
		myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">"); 
		myOutput.append("<thead><th>Class</th><th>Value</th></thead>");
		myOutput.append("<tbody>");
		
		/* Access the iterator */
		myIterator = myList.listIterator();
		
		/* Loop through the Detail Buckets */
		while ((myBucket = myIterator.next()) != null) {
			/* Only process summary items */
			if (myBucket.getBucket() != BucketType.SUMMARY) continue;
			
			/* Format the Summary */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append("<a href=\"#Detail");
			myOutput.append(myBucket.getName());
			myOutput.append("\">");
			myOutput.append(myBucket.getName());
			myOutput.append("</a></th>");
			myOutput.append(Report.makeMoneyItem(myBucket.getAmount()));
			myOutput.append("</tr>");
			
			/* Access the type */
			myType = myBucket.getType();
				
			/* Format the detail */
			if (myType.isMoney())
				myDetail.append(makeRatedReport(myBucket));
			else if (myType.isPriced())
				myDetail.append(makePricedReport(myBucket));
			else 
				myDetail.append(makeDebtReport(myBucket));
		}
		
		/* Access the totals */
		myBucket = myList.getTotalsBucket();
		
		/* Format the totals */
		myOutput.append("<tr><th>Totals</th>");
		myOutput.append(Report.makeMoneyTotal(myBucket.getAmount()));
		myOutput.append("</tr></tbody></table>");
		
		/* Add the detail */
		myOutput.append(myDetail);
		
		/* Terminate the html */
		myOutput.append("</body></html>");
		
		/* Return the output */
		return myOutput.toString();
	}		
	
	/**
	 * Build a web output of the market report
	 * @return Web output
	 */
	public String getMarketReport() {
		SortedList<Bucket>.ListIterator	myIterator;
		Bucket							myBucket;
		StringBuilder		   			myOutput = new StringBuilder(10000);
		AccountType       				myType; 
		AssetAnalysis.List 				myList;
		AssetBucket						myAsset;

		/* Endure that totals have been produced */
		theAsset.produceMarketTotals();
		
		/* Access the bucket lists */
		myList = theAsset.getBuckets();
		
		/* Format the header */
		myOutput.append("<html><body><a name=\"Top\">");
		myOutput.append("<h1 align=\"center\">Market Report for ");
		myOutput.append(theDate.formatDate(false));
		myOutput.append("</h1></a>");
		myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">"); 
		myOutput.append("<thead><th>Name</th><th>Value</th>");
		myOutput.append("<th>Investment</th><th>Dividends</th>");
		myOutput.append("<th>MarketGrowth</th></thead>");
		myOutput.append("<tbody>");
		
		/* Access the iterator */
		myIterator = myList.listIterator();
		
		/* Loop through the Detail Buckets */
		while ((myBucket = myIterator.next()) != null) {
			/* Only process detail items */
			if (myBucket.getBucket() != BucketType.DETAIL) break;
			
			/* Access the type */
			myType = myBucket.getType();
			
			/* Ignore non-priced items */
			if (!myType.isPriced()) continue;
			
			/* Access the asset */
			myAsset = (AssetBucket)myBucket;
			
			/* Format the Asset */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myBucket.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeMoneyItem(myAsset.getAmount()));
			myOutput.append(Report.makeMoneyItem(myAsset.getInvestment()));
			myOutput.append(Report.makeMoneyItem(myAsset.getDividends()));
			myOutput.append(Report.makeMoneyItem(myAsset.getMarket()));
			myOutput.append("</tr>");
		}
		
		/* Access the totals */
		myAsset = myList.getMarketTotalsBucket();
		
		/* Format the totals */
		myOutput.append("<tr><th>Totals</th>");
		myOutput.append(Report.makeMoneyTotal(myAsset.getAmount()));
		myOutput.append(Report.makeMoneyTotal(myAsset.getInvestment()));
		myOutput.append(Report.makeMoneyTotal(myAsset.getDividends()));
		myOutput.append(Report.makeMoneyTotal(myAsset.getMarket()));
		myOutput.append("</tr></tbody></table>");
		
		/* Terminate the html */
		myOutput.append("</body></html>");
		
		/* Return the output */
		return myOutput.toString();
	}		
	
	/**
	 * Build a standard yearly report element
	 * @param pSummary the class of the element
	 * @return Web output
	 */
	public StringBuilder makeStandardReport(Bucket pSummary) {
		SortedList<Bucket>.ListIterator	myIterator;
		StringBuilder		   			myOutput = new StringBuilder(10000);
		AccountType						myType;
		Bucket							myBucket;
		AssetAnalysis.List 				myList;

		/* Access the bucket lists */
		myList = theAsset.getBuckets();
		
		/* Access the type */
		myType = pSummary.getType();
			
		/* Format the detail */
		myOutput.append("<a name=\"Detail");
		myOutput.append(pSummary.getName());
		myOutput.append("\">");
		myOutput.append("<h2 align=\"center\">");
		myOutput.append(pSummary.getName());
		myOutput.append("</h2></a>");
		myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">"); 
		myOutput.append("<thead><th rowspan=\"2\">Name</th><th colspan=\"2\">Value</th></thead>");
		myOutput.append("<thead><th>");
		myOutput.append(theDate.getYear());
		myOutput.append("</th><th>");
		myOutput.append(theDate.getYear()-1);
		myOutput.append("</th></thead>");
		myOutput.append("<tbody>");
		
		/* Access the iterator */
		myIterator = myList.listIterator();
		
		/* Loop through the Detail Buckets */
		while ((myBucket = myIterator.next()) != null) {
			/* Break loop if we have completed the details */
			if (myBucket.getBucket() != BucketType.DETAIL) break;
			
			/* Skip record if incorrect type */
			if (AccountType.differs(myBucket.getType(), myType)) continue;

			/* Format the detail */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myBucket.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeMoneyItem(myBucket.getAmount()));
			myOutput.append(Report.makeMoneyItem(myBucket.getPrevAmount()));
			myOutput.append("</tr>");
		}			 
		
		myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
		myOutput.append(Report.makeMoneyTotal(pSummary.getAmount()));
		myOutput.append(Report.makeMoneyTotal(pSummary.getPrevAmount()));
		myOutput.append("</tr></tbody></table>");
		
		/* Return the output */
		return myOutput;
	}
	
	/**
	 * Build a rated instant report element
	 * @param pSummary the class of the element
	 * @return Web output
	 */
	public StringBuilder makeRatedReport(Bucket pSummary) {
		SortedList<Bucket>.ListIterator	myIterator;
		StringBuilder		   			myOutput = new StringBuilder(10000);
		AccountType       				myType;
		Bucket							myBucket;
		AssetAnalysis.List 				myList;
		MoneyBucket						myMoney;

		/* Access the bucket lists */
		myList = theAsset.getBuckets();
		
		/* Access the type */
		myType = pSummary.getType();
			
		/* Format the detail */
		myOutput.append("<a name=\"Detail");
		myOutput.append(pSummary.getName());
		myOutput.append("\">");
		myOutput.append("<h2 align=\"center\">");
		myOutput.append(pSummary.getName());
		myOutput.append("</h2></a>");
		myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">"); 
		myOutput.append("<thead><th>Name</th><th>Value</th>");
		myOutput.append("<th>Rate</th><th>Maturity</th></thead>");
		myOutput.append("<tbody>");
		
		/* Access the iterator */
		myIterator = myList.listIterator();
		
		/* Loop through the Detail Buckets */
		while ((myBucket = myIterator.next()) != null) {
			/* Break loop if we have completed the details */
			if (myBucket.getBucket() != BucketType.DETAIL) break;
			
			/* Skip record if incorrect type */
			if (AccountType.differs(myBucket.getType(), myType)) continue;

			/* Access the bucket */
			myMoney = (MoneyBucket) myBucket;
			
			/* Format the detail */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myBucket.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeMoneyItem(myMoney.getAmount()));
			myOutput.append(Report.makeRateItem(myMoney.getRate()));
			myOutput.append(Report.makeDateItem(myMoney.getDate()));
			myOutput.append("</tr>");
		}			 
		
		myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
		myOutput.append(Report.makeMoneyTotal(pSummary.getAmount()));
		myOutput.append("<td/><td/>");
		myOutput.append("</tr></tbody></table>");
		
		/* Return the output */
		return myOutput;
	}
	
	/**
	 * Build a debt instant report element
	 * @param pSummary the class of element
	 * @return Web output
	 */
	public StringBuilder makeDebtReport(Bucket pSummary) {
		SortedList<Bucket>.ListIterator	myIterator;
		StringBuilder		   			myOutput = new StringBuilder(10000);
		AccountType 					myType;
		Bucket							myBucket;
		AssetAnalysis.List 				myList;

		/* Access the bucket lists */
		myList = theAsset.getBuckets();
		
		/* Access the type */
		myType = pSummary.getType();
			
		/* Format the detail */
		myOutput.append("<a name=\"Detail");
		myOutput.append(pSummary.getName());
		myOutput.append("\">");
		myOutput.append("<h2 align=\"center\">");
		myOutput.append(pSummary.getName());
		myOutput.append("</h2></a>");
		myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">"); 
		myOutput.append("<thead><th>Name</th><th>Value</th></thead>");
		myOutput.append("<tbody>");
		
		/* Access the iterator */
		myIterator = myList.listIterator();
		
		/* Loop through the Detail Buckets */
		while ((myBucket = myIterator.next()) != null) {
			/* Break loop if we have completed the details */
			if (myBucket.getBucket() != BucketType.DETAIL) break;
			
			/* Skip record if incorrect type */
			if (AccountType.differs(myBucket.getType(), myType)) continue;

			/* Format the detail */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myBucket.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeMoneyItem(myBucket.getAmount()));
			myOutput.append("</tr>");
		}			 
		
		myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
		myOutput.append(Report.makeMoneyTotal(pSummary.getAmount()));
		myOutput.append("</tr></tbody></table>");
		
		/* Return the output */
		return myOutput;
	}
	
	/**
	 * Build a priced instant report element
	 * @param pSummary the class of element
	 * @return Web output
	 */
	public StringBuilder makePricedReport(Bucket pSummary) {
		SortedList<Bucket>.ListIterator	myIterator;
		StringBuilder		   			myOutput = new StringBuilder(10000);
		AccountType						myType;
		Bucket							myBucket;
		AssetAnalysis.List 				myList;
		AssetBucket						myAsset;

		/* Access the bucket lists */
		myList = theAsset.getBuckets();
		
		/* Access the type */
		myType = pSummary.getType();
			
		/* Format the detail */
		myOutput.append("<a name=\"Detail");
		myOutput.append(pSummary.getName());
		myOutput.append("\">");
		myOutput.append("<h2 align=\"center\">");
		myOutput.append(pSummary.getName());
		myOutput.append("</h2></a>");
		myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">"); 
		myOutput.append("<thead><th>Name</th><th>Units</th>");
		myOutput.append("<th>Price</th><th>Value</th></thead>");
		myOutput.append("<tbody>");
		
		/* Access the iterator */
		myIterator = myList.listIterator();
		
		/* Loop through the Detail Buckets */
		while ((myBucket = myIterator.next()) != null) {
			/* Break loop if we have completed the details */
			if (myBucket.getBucket() != BucketType.DETAIL) break;
			
			/* Skip record if incorrect type */
			if (AccountType.differs(myBucket.getType(), myType)) continue;

			/* Access the asset */
			myAsset = (AssetBucket) myBucket;
			
			/* Format the detail */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myBucket.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeUnitsItem(myAsset.getUnits()));
			myOutput.append(Report.makePriceItem(myAsset.getPrice()));
			myOutput.append(Report.makeMoneyItem(myAsset.getAmount()));
			myOutput.append("</tr>");
		}			 
		
		myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
		myOutput.append("<td/><td/>");
		myOutput.append(Report.makeMoneyTotal(pSummary.getAmount()));
		myOutput.append("</tr></tbody></table>");
		
		/* Return the output */
		return myOutput;
	}		
}
