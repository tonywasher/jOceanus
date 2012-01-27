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
package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.TransactionType;
import uk.co.tolcroft.finance.data.StaticClass.TransClass;
import uk.co.tolcroft.models.Decimal.Money;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.help.DebugDetail;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugManager.DebugEntry;
import uk.co.tolcroft.models.help.DebugObject;

public class IncomeBreakdown implements DebugObject {
	/**
	 * The Salary analysis
	 */
	private RecordList theSalary			= null;
	
	/**
	 * The Rental analysis
	 */
	private RecordList theRental			= null;
	
	/**
	 * The Taxable Interest analysis
	 */
	private RecordList theTaxedInterest		= null;
	
	/**
	 * The TaxFree Interest analysis
	 */
	private RecordList theTaxFreeInterest	= null;
	
	/**
	 * The Taxable Dividend analysis
	 */
	private RecordList theTaxedDividend		= null;
	
	/**
	 * The TaxFree Dividend analysis
	 */
	private RecordList theTaxFreeDividend	= null;
	
	/**
	 * The Taxable UnitTrust Dividend analysis
	 */
	private RecordList theUnitTrustDividend	= null;
	
	/* Access functions */
	public RecordList getSalary() 				{ return theSalary; }
	public RecordList getRental() 				{ return theRental; }
	public RecordList getTaxableInterest() 		{ return theTaxedInterest; }
	public RecordList getTaxFreeInterest() 		{ return theTaxFreeInterest; }
	public RecordList getTaxableDividend() 		{ return theTaxedDividend; }
	public RecordList getTaxFreeDividend() 		{ return theTaxFreeDividend; }
	public RecordList getUnitTrustDividend()	{ return theUnitTrustDividend; }
	
	/**
	 * Constructor
	 * @param pData the DataSet
	 */
	protected IncomeBreakdown(FinanceData pData) {
		/* Allocate lists */
		theSalary				= new RecordList(pData, "Salary");
		theRental				= new RecordList(pData, "Rental");
		theTaxedInterest		= new RecordList(pData, "TaxedInterest");
		theTaxFreeInterest		= new RecordList(pData, "TaxFreeInterest");
		theTaxedDividend		= new RecordList(pData, "TaxedDividend");
		theTaxFreeDividend		= new RecordList(pData, "TaxFreeDividend");
		theUnitTrustDividend	= new RecordList(pData, "UnitTrustDividend");
	}
	
	/**
	 * Process event
	 * @param pEvent the event to process
	 */
	protected void processEvent(Event pEvent) {
		AccountRecord 	myRecord;
		Account			myDebit	= pEvent.getDebit();
		TransactionType myTrans = pEvent.getTransType();
		
		/* Switch on Transaction Type */
		switch (myTrans.getTranClass()) {
			case INTEREST:
				if (myDebit.isTaxFree()) myRecord = theTaxFreeInterest.findAccountRecord(myDebit.getParent());
				else 					 myRecord = theTaxedInterest.findAccountRecord(myDebit.getParent());
				myRecord.processEvent(pEvent);
				break;
			case DIVIDEND:
				if (myDebit.isTaxFree()) 		myRecord = theTaxFreeDividend.findAccountRecord(myDebit.isChild() ? myDebit.getParent() : myDebit);
				else if (myDebit.isUnitTrust()) myRecord = theUnitTrustDividend.findAccountRecord(myDebit.isChild() ? myDebit.getParent() : myDebit);
				else 					 		myRecord = theTaxedDividend.findAccountRecord(myDebit.isChild() ? myDebit.getParent() : myDebit);
				myRecord.processEvent(pEvent);
				break;
			case TAXEDINCOME:
			case BENEFIT:
			case NATINSURANCE:
				myRecord = theSalary.findAccountRecord(myDebit);
				myRecord.processEvent(pEvent);
				break;
			case RENTALINCOME:
				myRecord = theRental.findAccountRecord(myDebit);
				myRecord.processEvent(pEvent);
				break;
		}
	}

	@Override
	public StringBuilder buildDebugDetail(DebugDetail pDetail) { return null; }	

	@Override
	public void addChildEntries(DebugManager 	pManager,
								DebugEntry		pParent) { 
		/* Add the analyses */
		pManager.addChildEntry(pParent, "Salary", 				theSalary);
		pManager.addChildEntry(pParent, "Rental", 				theRental);
		pManager.addChildEntry(pParent, "TaxedInterest", 		theTaxedInterest);
		pManager.addChildEntry(pParent, "TaxFreeInterest", 		theTaxFreeInterest);
		pManager.addChildEntry(pParent, "TaxedDividend", 		theTaxedDividend);
		pManager.addChildEntry(pParent, "TaxFreeDividend", 		theTaxFreeDividend);
		pManager.addChildEntry(pParent, "UnitTrustDividend", 	theUnitTrustDividend);
	}

	/**
	 * Totals
	 */
	protected static class IncomeTotals {
		/**
		 * The Gross income
		 */
		private Money theGrossIncome	= new Money(0);
	
		/**
		 * The Net income
		 */
		private Money theNetIncome		= new Money(0);
	
		/**
		 * The Tax Credit
		 */
		private Money theTaxCredit		= new Money(0);
	
		/**
		 * Obtain the Gross Income
		 */
		protected Money getGrossIncome() 	{ return theGrossIncome; }
		
		/**
		 * Obtain the Net Income
		 */
		protected Money getNetIncome() 		{ return theNetIncome; }
		
		/**
		 * Obtain the Tax Credit
		 */
		protected Money getTaxCredit() 		{ return theTaxCredit; }
	}	
	
	/**
	 * The Account record class
	 */
	protected static class AccountRecord extends DataItem<AccountRecord> {
		/**
		 * The name of the object
		 */
		private static final String objName 	= "AccountRecord";

		/**
		 * The account for the record 
		 */
		private Account			theAccount		= null;
		
		/**
		 * The Totals 
		 */
		private IncomeTotals 	theTotals		= null;
		
		/**
		 * The List Totals 
		 */
		private IncomeTotals 	theListTotals	= null;
		
		/**
		 * The Events relating to this account
		 */
		private Event.List		theEvents		= null;
		
		/**
		 * The Children relating to this account
		 */
		private RecordList		theChildren		= null;
		
		/**
		 * Obtain the Account
		 */
		protected Account 				getAccount() 	{ return theAccount; }
		
		/**
		 * Obtain the Totals
		 */
		protected IncomeTotals 			getTotals() 	{ return theTotals; }
		
		/**
		 * Obtain the List of events
		 */
		protected Event.List			getEvents() 	{ return theEvents; }
		
		/**
		 * Obtain the List of children
		 */
		protected RecordList			getChildren() 	{ return theChildren; }
		
		/**
		 * Build History (no history)
		 */
		protected void buildHistory() {}

		/**
		 * Constructor
		 * @param pList the list to which the record belongs
		 * @param pAccount the account
		 */
		private AccountRecord(RecordList 	pList,
							  Account 		pAccount) {
			/* Call super-constructor */
			super(pList, pAccount.getId());
			
			/* Access the Data for the account */
			FinanceData myData = pList.getData();
			
			/* Store parameter */
			theAccount 		= pAccount;
			
			/* Create the totals and access those of the list */
			theTotals 		= new IncomeTotals();
			theListTotals	= pList.theTotals;
			
			/* Build the name of the child list */
			StringBuilder myNameBuilder = new StringBuilder(100);
			myNameBuilder.append(pList.getName());
			myNameBuilder.append("-");
			myNameBuilder.append(pAccount.getName());
			String myName = myNameBuilder.toString();
			
			/* Create the list of children */
			theChildren = new RecordList(myData, myName);
			
			/* Create the event list */
			theEvents 	= myData.getEvents().getViewList();
		}
		
		/* Field IDs */
		public static final int FIELD_ACCOUNT  	= DataItem.NUMFIELDS;
		public static final int FIELD_GROSS  	= DataItem.NUMFIELDS+1;
		public static final int FIELD_NET  		= DataItem.NUMFIELDS+2;
		public static final int FIELD_TAX  		= DataItem.NUMFIELDS+3;
		public static final int FIELD_EVENTS	= DataItem.NUMFIELDS+4;
		public static final int FIELD_CHILDREN	= DataItem.NUMFIELDS+5;
		public static final int NUMFIELDS	    = DataItem.NUMFIELDS+6;
	
		@Override
		public String 	getFieldName(int iField) { return fieldName(iField); }

		@Override
		public int		numFields() { return NUMFIELDS; }
		
		/**
		 * Determine the field name for a particular field
		 * @return the field name
		 */
		public static String	fieldName(int iField) {
			switch (iField) {
				case FIELD_ACCOUNT: 	return Account.objName;
				case FIELD_GROSS: 		return "GrossIncome";
				case FIELD_NET: 		return "NetIncome";
				case FIELD_TAX: 		return "TaxCredit";
				case FIELD_EVENTS: 		return "Events";
				case FIELD_CHILDREN: 	return "Children";
				default:		  		return DataItem.fieldName(iField);
			}
		}
	
		@Override
		public String formatField(DebugDetail pDetail, int iField, HistoryValues<AccountRecord> pValues) {
			String myString = ""; 
			switch (iField) {
				case FIELD_ACCOUNT:		
					myString += Account.format(theAccount);
					myString = pDetail.addDebugLink(theAccount, myString);
					break;
				case FIELD_GROSS:		
					myString += Money.format(theTotals.getGrossIncome());
					break;
				case FIELD_NET:		
					myString += Money.format(theTotals.getNetIncome());
					break;
				case FIELD_TAX:		
					myString += Money.format(theTotals.getTaxCredit());
					break;
				default: 			
					myString += super.formatField(pDetail, iField, pValues);
					break;
			}
			return myString;
		}

		/**
		 * Process Event
		 * @param pEvent the event to process
		 */
		private void processEvent(Event pEvent) {
			/* Add the event to the list */
			//theEvents.addNewItem(pEvent);
			
			/* Access values */
			Money 			myAmount 	= pEvent.getAmount();
			Money 			myTax 		= pEvent.getTaxCredit();
			Account 		myDebit 	= pEvent.getDebit();
			TransactionType myTrans		= pEvent.getTransType();
			
			/* If we are NatInsurance/Benefit */
			if ((myTrans.getTranClass() == TransClass.NATINSURANCE) ||
			    (myTrans.getTranClass() == TransClass.BENEFIT)) {
				/* Just add to gross */
				theTotals.theGrossIncome.addAmount(myAmount);				
				theListTotals.theGrossIncome.addAmount(myAmount);				
			}
			
			else {
				/* Add to gross and net */
				theTotals.theGrossIncome.addAmount(myAmount);
				theTotals.theNetIncome.addAmount(myAmount);
				theListTotals.theGrossIncome.addAmount(myAmount);
				theListTotals.theNetIncome.addAmount(myAmount);
			
				/* If we have a tax credit */
				if (myTax != null) {
					/* Add to gross and tax */
					theTotals.theGrossIncome.addAmount(myTax);
					theTotals.theTaxCredit.addAmount(myTax);				
					theListTotals.theGrossIncome.addAmount(myTax);
					theListTotals.theTaxCredit.addAmount(myTax);				
				}
			}
			
			/* If the debit account is a child */
			if (Account.differs(theAccount, myDebit).isDifferent()) {
				/* Find the relevant account */
				AccountRecord 	myChild = theChildren.findAccountRecord(myDebit);
				
				/* Process the record for the child */
				myChild.processEvent(pEvent);
			}
			
			/* else we need to record the event */
			else {
				/* Add a copy of the event to the list */
				theEvents.addNewItem(pEvent);
			}
		}

		/**
		 * Add child entries for the debug object
		 * @param pManager the debug manager
		 * @param pParent the parent debug entry
		 */
		public void addChildEntries(DebugManager 	pManager,
									DebugEntry		pParent) { 
			/* Add Event list */
			if (theEvents != null)
				pManager.addChildEntry(pParent, "Events", theEvents);

			/* Add Children */
			if (theChildren != null)
				pManager.addChildEntry(pParent, "Children", theChildren);
		}
		
		@Override
		public String itemType() { return objName; }

		@Override
		public boolean equals(Object that) { return this == that; }

		@Override
		public int compareTo(Object pThat) {
			/* Handle the trivial cases */
			if (this == pThat) return 0;
			if (pThat == null) return -1;
		
			/* Make sure that the object is an AccountRecord */
			if (!(pThat instanceof AccountRecord)) return -1;
		
			/* Access as AccountRecord and compare Accounts */
			AccountRecord myThat = (AccountRecord)pThat;
			return theAccount.compareTo(myThat.theAccount);
		}
	}
	
	/**
	 *  The RecordList class
	 */
	public static class RecordList extends DataList<RecordList, AccountRecord> {
		/**
		 * The name of the object
		 */
		private static final String objName = "RecordList";

		/**
		 * The DataSet that this list is based on
		 */
		private FinanceData 	theData		= null;
		
		/**
		 * The Totals for the record list
		 */
		private IncomeTotals	theTotals	= new IncomeTotals();
		
		/**
		 * The Name for the record list
		 */
		private String			theName		= null;
		
		/**
		 * Access the DataSet for a RecordList
		 * @return the DataSet
		 */
		private FinanceData getData() 	{ return theData; }
		
		/**
		 * Access the Totals
		 * @return the Totals
		 */
		public IncomeTotals getTotals() { return theTotals; }
		
		/**
		 * Access the Name
		 * @return the Name
		 */
		public String getName() 		{ return theName; }
		
		/**
		 * Construct a top-level List
		 */
		public RecordList(FinanceData 	pData,
						  String 		pName) { 
			super(RecordList.class, AccountRecord.class, ListStyle.VIEW, false);
			theData = pData;
			theName = pName;
		}

		/* Obtain extract lists. */
		public RecordList getUpdateList() { return null; }
		public RecordList getEditList() 	{ return null; }
		public RecordList getShallowCopy() { return null; }
		public RecordList getDeepCopy(DataSet<?> pData) { return null; }
		public RecordList getDifferences(RecordList pOld) { return null; }

		@Override
		public String itemType() { return objName; }

		@Override
		public AccountRecord addNewItem(DataItem<?> pElement) { return null; }
 
		@Override
		public AccountRecord addNewItem() { return null; }
		
		/**
		 * Obtain the AccountRecord for a given account 
		 * @param pAccount the account
		 * @return the record
		 */
		protected AccountRecord findAccountRecord(Account pAccount) {
			/* Locate the record in the list */
			AccountRecord myRecord = (AccountRecord)searchFor(pAccount.getId());
			
			/* If the record does not yet exist */
			if (myRecord == null) {
				/* Allocate the record */
				myRecord = new AccountRecord(this, pAccount);
				
				/* Add to the list */
				add(myRecord);
			}
			
			/* Return the record */
			return myRecord;
		}		
		
		/**
		 * Add child entries for the debug object
		 * @param pManager the debug manager
		 * @param pParent the parent debug entry
		 */
		public void addChildEntries(DebugManager 	pManager,
									DebugEntry		pParent) { 
			DataListIterator<AccountRecord> 	myIterator = listIterator();
			AccountRecord						myRecord;
			
			/* Loop through the records */
			while ((myRecord = myIterator.previous()) != null) {
				/* Add child */
				pManager.addChildEntry(pParent, myRecord.getAccount().getName(), myRecord);
			}
		}
	}
}
