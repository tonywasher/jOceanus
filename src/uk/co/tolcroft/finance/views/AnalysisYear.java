package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;

public class AnalysisYear implements SortedList.linkObject {
    /**
	 * Storage for the List Node
	 */
    private 	Object				theLink		= null;

	/* Members */
	private TaxYear 		theYear    = null;
	private Date   			theDate    = null;
	private AssetAnalysis 	theAssets  = null;
	private IncomeAnalysis	theIncome  = null;
	private TaxAnalysis		theTax     = null;
	
	/* Access methods */
	public AssetAnalysis	getAssetAnalysis()  { return theAssets; }
	public IncomeAnalysis	getIncomeAnalysis() { return theIncome; }	
	public TaxAnalysis		getTaxAnalysis()    { return theTax; }
	public TaxYear			getYear()    		{ return theYear; }
	public Date				getDate()    		{ return theDate; }
	
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
	public AnalysisYear(DataSet pData, TaxYear pYear) {
		theYear   = pYear;
		theDate   = pYear.getDate();
		theAssets = new AssetAnalysis(pData, theDate, this);
		theIncome = new IncomeAnalysis(pData, theDate);
		theTax    = new TaxAnalysis(pData, theYear);
	}	
	
	/**
	 * Compare this AnalysisYear to another to establish sort order.
	 * 
	 * @param pThat The Year to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is an AnalysisYear */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as an Analysis Year */
		AnalysisYear myThat = (AnalysisYear)pThat;
		
		/* Compare the year */
		return theDate.compareTo(myThat.theDate);
	}

	/**
	 * Process an event
	 * 
	 * @param pEvent the event to process
	 */
	public void processEvent(Event pEvent) {
		/* If the event is asset related */
		if (pEvent.isAssetRelated()) {
			/* Process in the asset report */
			theAssets.processEvent(pEvent);
		}
		
		/* Process in the income report */
		theIncome.processEvent(pEvent);
		
		/* Process in the tax report */
		theTax.processEvent(pEvent);		
	}
	
	/**
	 * Seed an analysis from the preceding analysis
	 */
	public void seedAnalyses(AnalysisYear pLast) {
		/* Seed the Underlying analyses */
		theAssets.seedAnalysis(pLast.getAssetAnalysis());
		theIncome.seedAnalysis(pLast.getIncomeAnalysis());
		theTax.seedAnalysis(pLast.getTaxAnalysis());
	}
	
	/**
	 * List of AnalysisYears
	 */
	public static class List extends SortedList<AnalysisYear> {
		
		/* Constructor */
		public List(DataSet pData) {
			Event           				myCurr;
			DataList<Event>.ListIterator	myIterator;
			int             				myResult	= -1;
			TaxYear         				myTax  		= null;
			Date   							myDate 		= null;
			AnalysisYear					myLast 		= null;
			AnalysisYear					myYear 		= null;
			TaxYear.List					myList;

			/* Access the tax years list */
			myList = pData.getTaxYears();
			
			/* Access the Event iterator */
			myIterator = pData.getEvents().listIterator();
			
			/* Loop through the Events extracting relevant elements */
			while ((myCurr = myIterator.next()) != null) {
				/* If we have a current tax year */
				if (myTax != null) {
					/* Check that this event is still in the tax year */
					myResult = myDate.compareTo(myCurr.getDate());
				}
				
				/* If we have exhausted the tax year or else this is the first tax year */
				if (myResult == -1) { 
					/* Access the relevant tax year */
					myTax  = myList.searchFor(myCurr.getDate());
					myDate = myTax.getDate();
			
					/* If we have an existing analysis year */
					if (myYear != null) {
						/* Value priced assets */
						myYear.theAssets.valuePricedAssets();
					}
					
					/* Create the new AnalysisYear */
					myLast = myYear;
					myYear = new AnalysisYear(pData, myTax);
					
					/* Add it to the list */
					add(myYear);
					
					/* Seed the analyses from the previous year */
					if (myLast != null) myYear.seedAnalyses(myLast);
				}
							
				/* Touch credit and debit accounts */
				myCurr.getCredit().touchAccount(myCurr);
				myCurr.getDebit().touchAccount(myCurr);			
				
				/* Process the event in the report set */
				myYear.processEvent(myCurr);
				myTax.setActive();
			}
			
			/* Value priced assets of the most recent set */
			if (myYear != null) myYear.theAssets.valuePricedAssets();
		}		
		
		/**
		 * Search for tax year 
		 * 
		 */
		public AnalysisYear searchFor(TaxYear pYear) {
			ListIterator myIterator;
			AnalysisYear myCurr;
			
			/* Access the list iterator */
			myIterator = listIterator();
			
			/* Loop through the tax parameters */
			while ((myCurr  = myIterator.next()) != null) {
				/* Break on match */
				if (myCurr.theYear.compareTo(pYear) == 0)
					break;
			}
			
			/* Return to caller */
			return myCurr;
		}		

		/**
		 * Get last asset report 
		 */
		public AssetAnalysis getLastAssets() {
			ListIterator myIterator;
			AnalysisYear myCurr;
			
			/* Access the list iterator and the last element */
			myIterator = listIterator();
			myCurr     = myIterator.peekLast();
						
			/* Return to caller */
			return (myCurr != null) ? myCurr.getAssetAnalysis() : null;
		}		
	}
	
	/**
	 *  Bucket Types
	 */
	public static enum BucketType {
		STATIC,
		DETAIL,
		SUMMARY,
		TOTAL;
	}
	
	/* Bucket order */
	public static int getBucketOrder(BucketType pBucket) {
		switch (pBucket) {
			case DETAIL: 	return 1;
			case SUMMARY: 	return 2;
			case TOTAL:  	return 3;
			default: 		return 0;
		}
	}
}
