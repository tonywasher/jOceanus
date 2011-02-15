package uk.co.tolcroft.finance.ui.controls;

import javax.swing.JComboBox;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.*;

public class ComboSelect {
	/* Members */
	private Item    theFirst = null;
	
	/* Constructor */
	public ComboSelect(View pView) {
		DataSet					myData;
		Account       			myAcct;
		Account.List   			myAccounts;
		TransactionType   		myTran;
		TransactionType.List   	myTransTypes;
		AccountType 			myType;
		AccountType 			myCurr;
		ComboSelect.Item		myActList;
		ComboSelect.Item    	myTranList;
		boolean					isFirst;
		
		DataList<TransactionType>.ListIterator 	myTranIterator;
		DataList<Account>.ListIterator 			myActIterator;

		/* Access the data */
		myData     		= pView.getData();
		myAccounts 		= myData.getAccounts();
		myTransTypes	= myData.getTransTypes();
		myActList  		= null;
		myCurr     		= null;
		
		/* Create a transactionType Iterator */
		myTranIterator = myTransTypes.listIterator();
		
		/* Loop through the TransType values adding to the types box */
		while ((myTran = myTranIterator.next()) != null) {
			/* Ignore hidden values */
			if (myTran.isHidden()) continue;
			
			/* Access the list for this transaction type */
			myTranList = searchFor(myTran);
			
			/* Create an account Iterator */
			myActIterator = myAccounts.listIterator();
			
			/* Loop through the accounts */
			while ((myAcct = myActIterator.next()) != null) {					
				/* Ignore closed accounts */
				if (myAcct.isClosed()) continue;
				
				/* Access the account type */
				myType  = myAcct.getActType();
				isFirst = false;
				
				/* If this is a new account type */
				if (myType != myCurr) {
					/* Record it and get the correct list */
					myCurr     = myType;
					myActList  = searchFor(myType);
					isFirst    = true;
				}

				/* If this is OK for a credit account */
				if (Event.isValidEvent(myTran, myType, true)) {
					
					/* Add it to the list */
					if (isFirst) myActList.addCredit(myTran.getName());
					myTranList.addCredit(myAcct.getName());
				}
						
				/* If this is OK for a debit account */
				if (Event.isValidEvent(myTran, myType, false)) {
					/* Add it to the list */
					if (isFirst) myActList.addDebit(myTran.getName());
					myTranList.addDebit(myAcct.getName());
				}
			}	
		}
	}
	
	/**
	 * Search for the Item for this TransType
	 * @param pTransType - the TransType
	 * @return theItem
	 */
	public Item searchFor(TransactionType pTransType) {
		Item myCurr;
		for (myCurr = theFirst;
		     myCurr != null;
		     myCurr = myCurr.theNext) {
			if (!TransactionType.differs(pTransType, myCurr.theTransType))
				break;
		}
		if (myCurr == null) myCurr = new Item(pTransType);
		return myCurr;
	}
	
	/**
	 * Search for the item for this Account Type
	 * @param pActType - the Account type
	 * @return the item
	 */
	public Item searchFor(AccountType pActType) {
		Item myCurr;
		for (myCurr = theFirst;
		     myCurr != null;
		     myCurr = myCurr.theNext) {
			if (!AccountType.differs(pActType, myCurr.theActType))
				break;
		}
		if (myCurr == null) myCurr = new Item(pActType);
		return myCurr;
	}		
	
	/* The item class */
	public class Item {
		private TransactionType theTransType = null;
		private AccountType 	theActType   = null;
		private JComboBox       theCredit    = null;
		private JComboBox       theDebit     = null;
		private Item            theNext      = null;
		
		public JComboBox getCredit() { return theCredit; }
		public JComboBox getDebit()  { return theDebit; }
	
		public Item (TransactionType pTransType) {
			theCredit    = new JComboBox();
			theDebit     = new JComboBox();
			theTransType = pTransType;
			theNext      = theFirst;
			theFirst     = this;
		}
		public Item (AccountType pActType) {
			theCredit    = new JComboBox();
			theDebit     = new JComboBox();
			theActType   = pActType;
			theNext      = theFirst;
			theFirst     = this;
		}
		public void addCredit(java.lang.String pName) {
			theCredit.addItem(pName);
		}
		public void addDebit(java.lang.String pName) {
			theDebit.addItem(pName);
		}
	}
}
