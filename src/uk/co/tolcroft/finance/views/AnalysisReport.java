package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.views.Analysis.*;
import uk.co.tolcroft.finance.views.EventAnalysis.AnalysisYear;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.data.StaticClass.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number.*;

public class AnalysisReport {
	/* Properties */
	private Analysis		theAnalysis		= null;
	private AnalysisYear	theAnalysisYear	= null;
	private Date  			theDate 		= null;
	private TaxYear			theYear			= null;
	
	/* Constructor */
	public AnalysisReport(EventAnalysis pAnalysis) {
		/* Record the details */
		theAnalysis = pAnalysis.getAnalysis();
		theDate  	= theAnalysis.getDate();

		/* Produce totals */
		pAnalysis.getMetaAnalysis().produceTotals();
	}
	
	/* Constructor */
	public AnalysisReport(AnalysisYear pAnalysisYear) {
		/* Record the details */
		theAnalysisYear = pAnalysisYear;
		theAnalysis 	= pAnalysisYear.getAnalysis();
		theDate  		= theAnalysis.getDate();
		theYear			= pAnalysisYear.getTaxYear();

		/* Produce totals for the analysis year */
		pAnalysisYear.produceTotals();
	}
	
	/**
	 * Build a web output of the Year report
	 * @return Web output
	 */
	public String getYearReport() {
		DataList<AnalysisBucket>.ListIterator	myIterator;
		AnalysisBucket							myBucket;
		StringBuilder		   					myOutput = new StringBuilder(10000);
		StringBuilder          					myDetail = new StringBuilder(10000);	
		BucketList 								myList;
		AssetSummary							mySummary;
		AssetTotal								myTotal;

		/* Access the bucket lists */
		myList = theAnalysis.getList();
		
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
			if (myBucket.getBucketType() != BucketType.ASSETSUMMARY) continue;
			
			/* Access the summary bucket */
			mySummary = (AssetSummary)myBucket;
			
			/* Format the Summary */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append("<a href=\"#Detail");
			myOutput.append(mySummary.getName());
			myOutput.append("\">");
			myOutput.append(mySummary.getName());
			myOutput.append("</a></th>");
			myOutput.append(Report.makeMoneyItem(mySummary.getValue()));
			myOutput.append(Report.makeMoneyItem(mySummary.getPrevValue()));
			myOutput.append("</tr>");
				
			/* Format the detail */
			myDetail.append(makeStandardReport(mySummary));
		}
		
		/* Access the totals */
		myTotal = myList.getAssetTotal();
		
		/* Format the totals */
		myOutput.append("<tr><th>Totals</th>");
		myOutput.append(Report.makeMoneyTotal(myTotal.getValue()));
		myOutput.append(Report.makeMoneyTotal(myTotal.getPrevValue()));
		myOutput.append("</tr>");
		
		/* Format the profit */
		myOutput.append("<tr><th>Profit</th>");
		myOutput.append(Report.makeMoneyProfit(myTotal.getProfit()));
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
		DataList<AnalysisBucket>.ListIterator	myIterator;
		AnalysisBucket							myBucket;
		StringBuilder		   					myOutput = new StringBuilder(10000);
		StringBuilder          					myDetail = new StringBuilder(10000);	
		AccountType       						myType;
		AssetSummary							mySummary;
		AssetTotal								myTotal;
		BucketList 								myList;

		/* Access the bucket list */
		myList = theAnalysis.getList();
		
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
		
		/* Loop through the Summary Buckets */
		while ((myBucket = myIterator.next()) != null) {
			/* Only process summary items */
			if (myBucket.getBucketType() != BucketType.ASSETSUMMARY) continue;
			
			/* Access the summary bucket */
			mySummary = (AssetSummary)myBucket;
			
			/* Format the Summary */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append("<a href=\"#Detail");
			myOutput.append(mySummary.getName());
			myOutput.append("\">");
			myOutput.append(mySummary.getName());
			myOutput.append("</a></th>");
			myOutput.append(Report.makeMoneyItem(mySummary.getValue()));
			myOutput.append("</tr>");
			
			/* Access the type */
			myType = mySummary.getAccountType();
				
			/* Format the detail */
			if (myType.isMoney())
				myDetail.append(makeRatedReport(mySummary));
			else if (myType.isPriced())
				myDetail.append(makePricedReport(mySummary));
			else 
				myDetail.append(makeDebtReport(mySummary));
		}
		
		/* Access the totals */
		myTotal = myList.getAssetTotal();
		
		/* Format the totals */
		myOutput.append("<tr><th>Totals</th>");
		myOutput.append(Report.makeMoneyTotal(myTotal.getValue()));
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
		DataList<AnalysisBucket>.ListIterator	myIterator;
		AnalysisBucket							myBucket;
		StringBuilder				   			myOutput = new StringBuilder(10000);
		StringBuilder          					myDetail = new StringBuilder(10000);	
		AccountType       						myType; 
		BucketList				 				myList;
		AssetAccount							myAsset;
		MarketTotal								myTotal;

		/* Access the bucket lists */
		myList = theAnalysis.getList();
		
		/* Format the header */
		myOutput.append("<html><body><a name=\"Top\">");
		myOutput.append("<h1 align=\"center\">Market Report for ");
		myOutput.append(theDate.formatDate(false));
		myOutput.append("</h1></a>");
		myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">"); 
		myOutput.append("<thead><th>Name</th><th>Cost</th>");
		myOutput.append("<th>Value</th><th>Gains</th>");
		myOutput.append("<th>Profit</th></thead>");
		myOutput.append("<tbody>");
		
		/* Access the iterator */
		myIterator = myList.listIterator();
		
		/* Loop through the Detail Buckets */
		while ((myBucket = myIterator.next()) != null) {
			/* Only process detail items */
			if (myBucket.getBucketType() != BucketType.ASSETDETAIL) continue;
			
			/* Access the summary bucket */
			myAsset = (AssetAccount)myBucket;
			
			/* Access the type */
			myType = myAsset.getAccountType();
			
			/* Ignore non-priced items */
			if (!myType.isPriced()) continue;
			
			/* Format the Asset */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append("<a href=\"#Detail");
			myOutput.append(myAsset.getName());
			myOutput.append("\">");
			myOutput.append(myAsset.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeMoneyItem(myAsset.getCost()));
			myOutput.append(Report.makeMoneyItem(myAsset.getValue()));
			myOutput.append(Report.makeMoneyItem(myAsset.getGained()));
			myOutput.append(Report.makeMoneyItem(myAsset.getProfit()));
			myOutput.append("</tr>");

			/* If this is not an Endowment */
			if (!myType.isEndowment()) {
				/* Format the detail */
				myDetail.append(makeCapitalEventReport(myAsset));
			}
		}
		
		/* Access the totals */
		myTotal = myList.getMarketTotal();
		
		/* Format the totals */
		myOutput.append("<tr><th>Totals</th>");
		myOutput.append(Report.makeMoneyTotal(myTotal.getCost()));
		myOutput.append(Report.makeMoneyTotal(myTotal.getValue()));
		myOutput.append(Report.makeMoneyTotal(myTotal.getGained()));
		myOutput.append(Report.makeMoneyTotal(myTotal.getProfit()));
		myOutput.append("</tr></tbody></table>");
		
		/* Add the detail */
		myOutput.append(myDetail);
		
		/* Terminate the html */
		myOutput.append("</body></html>");
		
		/* Return the output */
		return myOutput.toString();
	}		
	
	/**
	 * Build a web output of the incoem/expense report
	 * @return Web output
	 */
	public String getIncomeReport() {
		AnalysisBucket 							myBucket;
		StringBuilder							myOutput = new StringBuilder(10000);
		BucketList					 			myList;
		DataList<AnalysisBucket>.ListIterator	myIterator;
		ExternalAccount							myExternal;
		ExternalTotal							myTotal;

		/* Access the bucket lists */
		myList = theAnalysis.getList();
		
		/* Format the header */
		myOutput.append("<html><body><a name=\"Top\">");
		myOutput.append("<h1 align=\"center\">Income/Expense Report for ");
		myOutput.append(theDate.getYear());
		myOutput.append("</h1></a>");
		myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">"); 
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
			/* Skip bucket if this is not an external account */
			if (myBucket.getBucketType() != BucketType.EXTERNALDETAIL) continue;
							
			/* Access the account */
			myExternal = (ExternalAccount)myBucket;
			
			/* Format the detail */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myExternal.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeMoneyItem(myExternal.getIncome()));
			myOutput.append(Report.makeMoneyItem(myExternal.getExpense()));
			myOutput.append(Report.makeMoneyItem(myExternal.getPrevIncome()));
			myOutput.append(Report.makeMoneyItem(myExternal.getPrevExpense()));
			myOutput.append("</tr>");
		}			 
		
		/* Access the totals */
		myTotal = myList.getExternalTotal();
		
		/* Format the totals */
		myOutput.append("<tr><th>Totals</th>");
		myOutput.append(Report.makeMoneyTotal(myTotal.getIncome()));
		myOutput.append(Report.makeMoneyTotal(myTotal.getExpense()));
		myOutput.append(Report.makeMoneyTotal(myTotal.getPrevIncome()));
		myOutput.append(Report.makeMoneyTotal(myTotal.getPrevExpense()));
		myOutput.append("</tr>");
		
		/* Format the profit */
		myOutput.append("<tr><th>Profit</th>");
		myOutput.append(Report.makeMoneyProfit(myTotal.getProfit()));
		myOutput.append(Report.makeMoneyProfit(myTotal.getPrevProfit()));
		myOutput.append("</tr></tbody></table></body></html>");
		
		/* Return the output */
		return myOutput.toString();
	}
	
	/**
	 * Build a standard yearly report element
	 * @param pSummary the class of the element
	 * @return Web output
	 */
	public StringBuilder makeStandardReport(AssetSummary pSummary) {
		DataList<AnalysisBucket>.ListIterator	myIterator;
		StringBuilder				   			myOutput = new StringBuilder(10000);
		AccountType								myType;
		AnalysisBucket							myBucket;
		BucketList 								myList;
		ValueAccount							myValue;

		/* Access the bucket lists */
		myList = theAnalysis.getList();
		
		/* Access the type */
		myType = pSummary.getAccountType();
			
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
			/* Skip record if not a value account */
			if (!(myBucket instanceof ValueAccount)) continue;
			
			/* Access the bucket */
			myValue = (ValueAccount) myBucket;

			/* Skip record if incorrect type */
			if (AccountType.differs(myValue.getAccountType(), myType)) continue;

			/* Format the detail */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myValue.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeMoneyItem(myValue.getValue()));
			myOutput.append(Report.makeMoneyItem(myValue.getPrevValue()));
			myOutput.append("</tr>");
		}			 
		
		myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
		myOutput.append(Report.makeMoneyTotal(pSummary.getValue()));
		myOutput.append(Report.makeMoneyTotal(pSummary.getPrevValue()));
		myOutput.append("</tr></tbody></table>");
		
		/* Return the output */
		return myOutput;
	}
	
	/**
	 * Build a rated instant report element
	 * @param pSummary the class of the element
	 * @return Web output
	 */
	private StringBuilder makeRatedReport(AssetSummary pSummary) {
		DataList<AnalysisBucket>.ListIterator	myIterator;
		StringBuilder		   					myOutput = new StringBuilder(10000);
		AccountType       						myType;
		AnalysisBucket							myBucket;
		BucketList				 				myList;
		MoneyAccount							myMoney;

		/* Access the bucket lists */
		myList = theAnalysis.getList();
		
		/* Access the type */
		myType = pSummary.getAccountType();
			
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
			/* Skip record if this is not a money detail */
			if (myBucket.getBucketType() != BucketType.MONEYDETAIL) continue;
			
			/* Access the bucket */
			myMoney = (MoneyAccount) myBucket;

			/* Skip record if incorrect type */
			if (AccountType.differs(myMoney.getAccountType(), myType)) continue;

			/* Format the detail */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myMoney.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeMoneyItem(myMoney.getValue()));
			myOutput.append(Report.makeRateItem(myMoney.getRate()));
			myOutput.append(Report.makeDateItem(myMoney.getMaturity()));
			myOutput.append("</tr>");
		}			 
		
		myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
		myOutput.append(Report.makeMoneyTotal(pSummary.getValue()));
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
	public StringBuilder makeDebtReport(AssetSummary pSummary) {
		DataList<AnalysisBucket>.ListIterator	myIterator;
		StringBuilder		   					myOutput = new StringBuilder(10000);
		AccountType 							myType;
		AnalysisBucket							myBucket;
		BucketList 								myList;
		DebtAccount								myDebt;

		/* Access the bucket lists */
		myList = theAnalysis.getList();
		
		/* Access the type */
		myType = pSummary.getAccountType();
			
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
			/* Skip record if this is not debt detail */
			if (myBucket.getBucketType() != BucketType.DEBTDETAIL) continue;
			
			/* Access the bucket */
			myDebt = (DebtAccount) myBucket;

			/* Skip record if incorrect type */
			if (AccountType.differs(myDebt.getAccountType(), myType)) continue;

			/* Format the detail */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myDebt.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeMoneyItem(myDebt.getValue()));
			myOutput.append("</tr>");
		}			 
		
		myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
		myOutput.append(Report.makeMoneyTotal(pSummary.getValue()));
		myOutput.append("</tr></tbody></table>");
		
		/* Return the output */
		return myOutput;
	}
	
	/**
	 * Build a priced instant report element
	 * @param pSummary the class of element
	 * @return Web output
	 */
	public StringBuilder makePricedReport(AssetSummary pSummary) {
		DataList<AnalysisBucket>.ListIterator	myIterator;
		StringBuilder		   					myOutput = new StringBuilder(10000);
		AccountType								myType;
		AnalysisBucket							myBucket;
		BucketList 								myList;
		AssetAccount							myAsset;

		/* Access the bucket lists */
		myList = theAnalysis.getList();
		
		/* Access the type */
		myType = pSummary.getAccountType();
			
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
			/* Skip record if this is not asset detail */
			if (myBucket.getBucketType() != BucketType.ASSETDETAIL) continue;
			
			/* Access the bucket */
			myAsset = (AssetAccount) myBucket;

			/* Skip record if incorrect type */
			if (AccountType.differs(myAsset.getAccountType(), myType)) continue;

			/* Skip irrelevant records */
			if (!myAsset.isRelevant()) continue;

			/* Format the detail */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myAsset.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeUnitsItem(myAsset.getUnits()));
			myOutput.append(Report.makePriceItem(myAsset.getPrice()));
			myOutput.append(Report.makeMoneyItem(myAsset.getValue()));
			myOutput.append("</tr>");
		}			 
		
		myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
		myOutput.append("<td/><td/>");
		myOutput.append(Report.makeMoneyTotal(pSummary.getValue()));
		myOutput.append("</tr></tbody></table>");
		
		/* Return the output */
		return myOutput;
	}		

	/**
	 * Build a capital event report element
	 * @param pAsset the asset to report on
	 * @return Web output
	 */
	public StringBuilder makeCapitalEventReport(AssetAccount pAsset) {
		DataList<CapitalEvent>.ListIterator	myIterator;
		StringBuilder		 				myOutput = new StringBuilder(10000);
		CapitalEvent						myEvent;
		CapitalEvent.List					myList;

		/* Access the event lists */
		myList = pAsset.getCapitalEvents();
		
		/* Format the detail */
		myOutput.append("<a name=\"Detail");
		myOutput.append(pAsset.getName());
		myOutput.append("\">");
		myOutput.append("<h2 align=\"center\">");
		myOutput.append(pAsset.getName());
		myOutput.append("</h2></a>");
		myOutput.append("<table border=\"1\" width=\"95%\" align=\"center\">"); 
		myOutput.append("<thead><th>Date</th><th>DeltaUnits</th>");
		myOutput.append("<th>DeltaCost</th><th>DeltaGains</th><th>Dividend</th></thead>");
		myOutput.append("<tbody>");
		
		/* Access the iterator */
		myIterator = myList.listIterator();
		
		/* Loop through the Events */
		while ((myEvent = myIterator.next()) != null) {
			/* Skip record if this is not based on an event (at present) */
			if (myEvent.getBase() == null) continue;
			
			/* Format the detail */
			myOutput.append("<tr><th>");
			myOutput.append(Date.format(myEvent.getDate()));
			myOutput.append("</th>");
			myOutput.append(Report.makeUnitsItem((Units)myEvent.findAttribute(CapitalEvent.capitalDeltaUnits)));
			myOutput.append(Report.makeMoneyItem((Money)myEvent.findAttribute(CapitalEvent.capitalDeltaCost)));
			myOutput.append(Report.makeMoneyItem((Money)myEvent.findAttribute(CapitalEvent.capitalDeltaGains)));
			myOutput.append(Report.makeMoneyItem((Money)myEvent.findAttribute(CapitalEvent.capitalDeltaDiv)));
			myOutput.append("</tr>");
		}			 
		
		myOutput.append("<tr><th><a href=\"#Top\">Totals</a></th>");
		myOutput.append(Report.makeUnitsItem(pAsset.getUnits()));
		myOutput.append(Report.makeMoneyItem(pAsset.getCost()));
		myOutput.append(Report.makeMoneyItem(pAsset.getGained()));
		myOutput.append("<td/></tr></tbody></table>");
		
		/* Return the output */
		return myOutput;
	}		

	/**
	 * Build a web output of the transaction report
	 * @return Web output
	 */
	public String getTransReport() {
		AnalysisBucket  						myBucket;
		StringBuilder		        			myOutput = new StringBuilder(10000);
		BucketList 								myList;
		DataList<AnalysisBucket>.ListIterator	myIterator;
		TransSummary							mySummary;
		TransTotal								myTotal;
		TransDetail								myDetail;

		/* Access the bucket lists */
		myList = theAnalysis.getList();
		
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
			/* Switch on bucket type */
			switch (myBucket.getBucketType()) {
				/* Summary */
				case TRANSSUMMARY:			
					mySummary = (TransSummary)myBucket;
			
					/* Format the detail */
					myOutput.append("<tr><th align=\"center\">" + mySummary.getName() + "</th>");
					myOutput.append(Report.makeMoneyItem(mySummary.getAmount()));
					myOutput.append(Report.makeMoneyItem(mySummary.getPrevAmount()));
					myOutput.append("</tr>");
					break;
				/* Total */
				case TRANSTOTAL:			
					myTotal = (TransTotal)myBucket;
			
					/* Format the detail */
					myOutput.append("<tr><th align=\"center\">" + myTotal.getName() + "</th>");
					myOutput.append(Report.makeMoneyItem(myTotal.getAmount()));
					myOutput.append(Report.makeMoneyItem(myTotal.getPrevAmount()));
					myOutput.append("</tr>");
					break;
			}
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
			/* Skip entries that are not TransDetail */
			if (myBucket.getBucketType() != BucketType.TRANSDETAIL) continue;
							
			/* Access the detail */
			myDetail = (TransDetail)myBucket;
			
			/* Format the detail */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myDetail.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeMoneyItem(myDetail.getAmount()));
			myOutput.append(Report.makeMoneyItem(myDetail.getTaxCredit()));
			myOutput.append(Report.makeMoneyItem(myDetail.getPrevAmount()));
			myOutput.append(Report.makeMoneyItem(myDetail.getPrevTax()));
			myOutput.append("</tr>");
		}			 
		
		/* Close the table */
		myOutput.append("</tbody></table></body></html>");
		
		/* Return the output */
		return myOutput.toString();
	}	

	/**
	 * Build a web output of the taxation report 
	 * @param pProperties the Properties
	 * @return Web output
	 */
	public String getTaxReport(Properties pProperties) {
		AnalysisBucket  						myBucket;
		StringBuilder		       				myOutput = new StringBuilder(10000);
		StringBuilder			  				myDetail = new StringBuilder(10000);
		BucketList		    					myList;
		DataList<AnalysisBucket>.ListIterator	myIterator;
		TaxDetail								myTax;
		TransSummary							myTrans;

		/* Ensure that tax has been calculated */
		theAnalysisYear.calculateTax(pProperties);
		
		/* Access the bucket lists */
		myList  = theAnalysis.getList();
		
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
		myIterator = myList.listIterator();
		
		/* Loop through the Transaction Summary Buckets */
		while ((myBucket = myIterator.next()) != null) {
			/* Skip the non-summary elements */
			if (myBucket.getBucketType() != BucketType.TAXSUMMARY) continue;
							
			/* Access the tax detail */
			myTax = (TaxDetail) myBucket;
			
			/* Format the line */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append("<a href=\"#Detail");
			myOutput.append(myTax.getName());
			myOutput.append("\">");
			myOutput.append(myTax.getName());
			myOutput.append("</a></th>");
			myOutput.append(Report.makeMoneyItem(myTax.getAmount()));
			myOutput.append(Report.makeMoneyItem(myTax.getTaxation()));
			myOutput.append("</tr>");
			
			/* Format the detail */
			myDetail.append(makeTaxReport(myTax));
		}

		/* Access the Total taxation bucket */
		myTax = myList.getTaxDetail(TaxClass.TOTALTAXATION);
		myOutput.append("<tr><th align=\"center\">");
		myOutput.append(myTax.getName());
		myOutput.append("</th>");
		myOutput.append(Report.makeMoneyTotal(myTax.getAmount()));
		myOutput.append(Report.makeMoneyTotal(myTax.getTaxation()));
		myOutput.append("</tr>");

		/* Access the Tax Paid bucket */
		myTrans = myList.getTransSummary(TaxClass.TAXPAID);
		myOutput.append("<tr><th align=\"center\">");
		myOutput.append(myTrans.getName());
		myOutput.append("</th>");
		myOutput.append(Report.makeMoneyTotal(new Money(0)));
		myOutput.append(Report.makeMoneyTotal(myTrans.getAmount()));
		myOutput.append("</tr>");

		/* Access the Tax Profit bucket */
		myTax = myList.getTaxDetail(TaxClass.TAXPROFITLOSS);
		myOutput.append("<tr><th align=\"center\">");
		myOutput.append(myTax.getName());
		myOutput.append("</th>");
		myOutput.append(Report.makeMoneyTotal(myTax.getAmount()));
		myOutput.append(Report.makeMoneyTotal(myTax.getTaxation()));
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
		myOutput.append(Report.makeMoneyItem(theYear.getAllowance()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>Age 65-74 PersonalAllowance</th>");
		myOutput.append(Report.makeMoneyItem(theYear.getLoAgeAllow()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>Age 75+ PersonalAllowance</th>");
		myOutput.append(Report.makeMoneyItem(theYear.getHiAgeAllow()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>RentalAllowance</th>");
		myOutput.append(Report.makeMoneyItem(theYear.getRentalAllowance()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>CapitalAllowance</th>");
		myOutput.append(Report.makeMoneyItem(theYear.getCapitalAllow()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>Income Limit for AgeAllowance</th>");
		myOutput.append(Report.makeMoneyItem(theYear.getAgeAllowLimit()));
		myOutput.append("</tr>");
		if (theYear.hasAdditionalTaxBand()) {
			myOutput.append("<tr><th>Income Limit for PersonalAllowance</th>");
			myOutput.append(Report.makeMoneyItem(theYear.getAddAllowLimit()));
			myOutput.append("</tr>");
		}
		myOutput.append("</tbody></table>");
		
		/* Format the Rates */
		myOutput.append("<a name=\"Allowances\"><h2 align=\"center\">TaxRates</h2></a>");
		myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">");
		myOutput.append("<thead><th>IncomeType</th><th>LoRate</th>");
		myOutput.append("<th>BasicRate</th><th>HiRate</th>");
		if (theYear.hasAdditionalTaxBand()) 
			myOutput.append("<th>AdditionalRate</th>");
		myOutput.append("</thead><tbody>");
		myOutput.append("<tr><th>Salary/Rental</th>");
		myOutput.append(Report.makeRateItem(theYear.hasLoSalaryBand()  
												? theYear.getLoTaxRate()
												: null));
		myOutput.append(Report.makeRateItem(theYear.getBasicTaxRate()));
		myOutput.append(Report.makeRateItem(theYear.getHiTaxRate()));
		if (theYear.hasAdditionalTaxBand()) 
			myOutput.append(Report.makeRateItem(theYear.getAddTaxRate()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>Interest</th>");
		myOutput.append(Report.makeRateItem(theYear.getLoTaxRate()));
		myOutput.append(Report.makeRateItem(theYear.getIntTaxRate()));
		myOutput.append(Report.makeRateItem(theYear.getHiTaxRate()));
		if (theYear.hasAdditionalTaxBand()) 
			myOutput.append(Report.makeRateItem(theYear.getAddTaxRate()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>Dividends</th>");
		myOutput.append(Report.makeRateItem(null));
		myOutput.append(Report.makeRateItem(theYear.getDivTaxRate()));
		myOutput.append(Report.makeRateItem(theYear.getHiDivTaxRate()));
		if (theYear.hasAdditionalTaxBand()) 
			myOutput.append(Report.makeRateItem(theYear.getAddDivTaxRate()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>TaxableGains</th>");
		myOutput.append(Report.makeRateItem(null));
		myOutput.append(Report.makeRateItem(theYear.getBasicTaxRate()));
		myOutput.append(Report.makeRateItem(theYear.getHiTaxRate()));
		if (theYear.hasAdditionalTaxBand()) 
			myOutput.append(Report.makeRateItem(theYear.getAddTaxRate()));
		myOutput.append("</tr>");
		myOutput.append("<tr><th>CapitalGains</th>");
		myOutput.append(Report.makeRateItem(null));
		myOutput.append(Report.makeRateItem(theYear.getCapTaxRate()));
		myOutput.append(Report.makeRateItem(theYear.getHiCapTaxRate()));
		if (theYear.hasAdditionalTaxBand()) 
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
		myOutput.append(theAnalysis.getAge());
		myOutput.append("</td></tr>");

		/* Access the original allowance */
		myTax = myList.getTaxDetail(TaxClass.ORIGALLOW);
		myOutput.append("<tr><th>Personal Allowance</th>");
		myOutput.append(Report.makeMoneyItem(myTax.getAmount()));
		myOutput.append("</tr>");

		/* if we have adjusted the allowance */
		if (theAnalysis.hasReducedAllow()) {
			/* Access the gross income */
			myTax = myList.getTaxDetail(TaxClass.GROSSINCOME);
			myOutput.append("<tr><th>Gross Taxable Income</th>");
			myOutput.append(Report.makeMoneyItem(myTax.getAmount()));
			myOutput.append("</tr>");

			/* Access the gross income */
			myTax = myList.getTaxDetail(TaxClass.ADJALLOW);
			myOutput.append("<tr><th>Adjusted Allowance</th>");
			myOutput.append(Report.makeMoneyItem(myTax.getAmount()));
			myOutput.append("</tr>");
		}
		
		/* Access the Low Tax Band */
		if (theYear.getLoBand() != null) {
			myOutput.append("<tr><th>Low Tax Band</th>");
			myOutput.append(Report.makeMoneyItem(theYear.getLoBand()));
			myOutput.append("</tr>");
		}

		/* Access the Basic Tax Band */
		myOutput.append("<tr><th>Basic Tax Band</th>");
		myOutput.append(Report.makeMoneyItem(theYear.getBasicBand()));
		myOutput.append("</tr>");

		/* If we have a high tax band */
		if (theYear.hasAdditionalTaxBand()) {
			/* Access the gross income */
			myTax = myList.getTaxDetail(TaxClass.HITAXBAND);
			myOutput.append("<tr><th>High Tax Band</th>");
			myOutput.append(Report.makeMoneyItem(myTax.getAmount()));
			myOutput.append("</tr>");				
		}
		myOutput.append("</tbody></table>");
		
			/* Add the detail */
		myOutput.append(myDetail);
		
		/* If we need a tax slice report */
		if (theAnalysis.hasGainsSlices())
			myOutput.append(makeTaxSliceReport());
		
		/* Close the document */
		myOutput.append("</body></html>");
		
		/* Return the output */
		return myOutput.toString();
	}
	
	/**
	 * Build a standard tax report element
	 * @return Web output
	 */
	public StringBuilder makeTaxReport(TaxDetail pSummary) {
		StringBuilder							myOutput = new StringBuilder(1000);
		AnalysisBucket							myBucket;
		BucketList				 				myList;
		TaxDetail								myTax;
		DataList<AnalysisBucket>.ListIterator	myIterator;

		/* Access the bucket lists */
		myList = theAnalysis.getList();
		
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
			/* Skip non-detail buckets */
			if (myBucket.getBucketType() != BucketType.TAXDETAIL) continue;
			
			/* Access the bucket */
			myTax = (TaxDetail)myBucket;
			
			/* Skip record if incorrect parent */
			if (myTax.getParent() != pSummary) continue;

			/* Format the detail */
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myTax.getName());
			myOutput.append("</th>");
			myOutput.append(Report.makeMoneyItem(myTax.getAmount()));
			myOutput.append(Report.makeRateItem(myTax.getRate()));
			myOutput.append(Report.makeMoneyItem(myTax.getTaxation()));
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
		StringBuilder							myOutput = new StringBuilder(1000);
		TaxDetail								myTax;
		BucketList								myList;
		ChargeableEvent							myCharge;
		ChargeableEvent.List					myCharges;
		DataList<ChargeableEvent>.ListIterator 	myIterator;

		/* Access the bucket lists */
		myList		= theAnalysis.getList();
		myCharges	= theAnalysis.getCharges();

		/* Format the detail */
		myOutput.append("<a name=\"DetailChargeableEvents>");
		myOutput.append("<h2 align=\"center\">Chargeable Events</h2></a>");
		myOutput.append("<table border=\"1\" width=\"90%\" align=\"center\">"); 
		myOutput.append("<thead><th>Date</th><th>Description</th>");
		myOutput.append("<th>Amount</th><th>TaxCredit</th><th>Years</th>");
		myOutput.append("<th>Slice</th><th>Taxation</th></thead>");
		myOutput.append("<tbody>");
		
		/* Create the list iterator */
		myIterator = theAnalysis.getCharges().listIterator();
		
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
		myOutput.append(Report.makeMoneyTotal(myCharges.getGainsTotal()));
		myOutput.append("<td/>");
		myOutput.append(Report.makeMoneyTotal(myCharges.getSliceTotal()));
		myOutput.append(Report.makeMoneyTotal(myCharges.getTaxTotal()));
		myOutput.append("</tr></tbody></table>");
			
		/* Access the Summary Tax Due Slice */
		myTax = myList.getTaxDetail(TaxClass.TAXDUESLICE);
		
		/* Add the Slice taxation details */
		myOutput.append(makeTaxReport(myTax));
		
		/* Return the output */
		return myOutput;
	}		
}
