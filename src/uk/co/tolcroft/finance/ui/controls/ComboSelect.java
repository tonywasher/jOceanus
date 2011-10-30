package uk.co.tolcroft.finance.ui.controls;

import javax.swing.JComboBox;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.*;

public class ComboSelect {
	/**
	 *  The JComboBox for the whole set of transaction types
	 */
	private JComboBox 	theTranTypeBox = null;
	
	/**
	 * The DataSet
	 */
	private FinanceData	theData			= null;
	
	/**
	 * Constructor
	 * @param pView
	 */
	public ComboSelect(View pView) {
		/* Store the data */
		theData = pView.getData();
		
		/* Create the TransType box */
		theTranTypeBox = new JComboBox();

		/* Access the transaction types */
		TransactionType.List				myList = theData.getTransTypes();
		TransactionType						myTrans;
		TransactionType.List.ListIterator  	myIterator;
		
		/* Create the iterator */
		myIterator = myList.listIterator();
		
		/* Loop through the Transaction types */
		while ((myTrans = myIterator.next()) != null) {
			/* Skip hidden values */
			if (myTrans.isHiddenType()) continue;
			
			/* Skip disabled values */
			if (!myTrans.getEnabled()) continue;
			
			/* Add the item to the list */
			theTranTypeBox.addItem(myTrans.getName());
		}
	}
	
	/**
	 * Obtain the pure transaction type ComboBox
	 * @return a ComboBox with all the transaction types 
	 */
	public JComboBox getAllTransTypes() {
		/* return to caller */
		return theTranTypeBox;
	}
	
	/**
	 * Obtain the ComboBox of transaction types for a Credit to an AccountType
	 * @param pType the account type
	 * @return the ComboBox
	 */
	public JComboBox getCreditTranTypes(AccountType pType) {
		TransactionType.List 	myList	= theData.getTransTypes();
		TransactionType			myTrans;
		JComboBox				myCombo;
		
		TransactionType.List.ListIterator myIterator;
		
		/* Create the iterator */
		myIterator = myList.listIterator();
		
		/* Create the ComboBox */
		myCombo = new JComboBox();
		
		/* Loop through the Transaction types */
		while ((myTrans = myIterator.next()) != null) {
			/* Skip hidden values */
			if (myTrans.isHiddenType()) continue;
			
			/* Skip disabled values */
			if (!myTrans.getEnabled()) continue;
			
			/* If this is OK for a credit to this account type */
			if (Event.isValidEvent(myTrans, pType, true)) {
				/* Add the item to the list */
				myCombo.addItem(myTrans.getName());
			}
		}
		
		/* return to caller */
		return myCombo;		
	}

	/**
	 * Obtain the ComboBox of transaction types for a Debit from an AccountType
	 * @param pType the transaction type
	 * @return the ComboBox
	 */
	public JComboBox getDebitTranTypes(AccountType pType) {
		TransactionType.List 	myList	= theData.getTransTypes();
		TransactionType			myTrans;
		JComboBox				myCombo;
		
		TransactionType.List.ListIterator myIterator;
		
		/* Create the iterator */
		myIterator = myList.listIterator();
		
		/* Create the ComboBox */
		myCombo = new JComboBox();
		
		/* Loop through the Transaction types */
		while ((myTrans = myIterator.next()) != null) {
			/* Skip hidden values */
			if (myTrans.isHiddenType()) continue;
			
			/* Skip disabled values */
			if (!myTrans.getEnabled()) continue;
			
			/* If this is OK for a debit from this account type */
			if (Event.isValidEvent(myTrans, pType, false)) {
				/* Add the item to the list */
				myCombo.addItem(myTrans.getName());
			}
		}
		
		/* return to caller */
		return myCombo;		
	}

	/**
	 * Obtain the ComboBox of accounts for a Debit for a Transaction Type
	 * @param pType the transaction type
	 * @return the ComboBox
	 */
	public JComboBox getDebitAccounts(TransactionType pType) {
		Account.List 	myList		= theData.getAccounts();
		Account			myAccount;
		AccountType		myType		= null;
		boolean			isValid		= false;
		JComboBox		myCombo;
		
		Account.List.ListIterator myIterator;
		
		/* Access the iterator */
		myIterator = myList.listIterator();
		
		/* Create the ComboBox */
		myCombo = new JComboBox();
		
		/* Loop through the accounts */
		while ((myAccount = myIterator.next()) != null) {
			/* If the type of this account is new */
			if (AccountType.differs(myType, myAccount.getActType()).isDifferent()) {
				/* Note the type */
				myType = myAccount.getActType();

				/* Determine whether we are a valid type */
				isValid = Event.isValidEvent(pType, myType, false);
			}
			
			/* Skip invalid types */
			if (!isValid) continue;
			
			/* Skip closed items if required */
			if (myAccount.isClosed()) continue;

			/* Add the item to the list */
			myCombo.addItem(myAccount.getName());
		}
		
		/* return to caller */
		return myCombo;		
	}

	/**
	 * Obtain the ComboBox of accounts for a Credit for a Transaction Type and Account
	 * @param pType the transaction type
	 * @param pDebit the debit account
	 * @return the ComboBox
	 */
	public JComboBox getCreditAccounts(TransactionType pType, Account pDebit) {
		Account.List 	myList		= theData.getAccounts();
		Account			myAccount;
		AccountType		myType		= null;
		boolean			isValid		= false;
		JComboBox		myCombo;
		
		Account.List.ListIterator myIterator;
		
		/* Access the iterator */
		myIterator = myList.listIterator();
		
		/* Create the ComboBox */
		myCombo = new JComboBox();
		
		/* Loop through the accounts */
		while ((myAccount = myIterator.next()) != null) {
			/* If the type of this account is new */
			if (AccountType.differs(myType, myAccount.getActType()).isDifferent()) {
				/* Note the type */
				myType = myAccount.getActType();

				/* Determine whether we are a valid type */
				isValid = Event.isValidEvent(pType, myType, true);
			}
			
			/* Skip invalid types */
			if (!isValid) continue;
			
			/* Skip closed items if required */
			if (myAccount.isClosed()) continue;

			/* If the account is identical to the selected account */
			if (!Account.differs(myAccount, pDebit).isDifferent()) {
				/* If this combination is allowed */
				if (Event.isValidEvent(pType, pDebit, myAccount)) {
					/* Add to beginning of list */
					myCombo.insertItemAt(myAccount.getName(), 0);
				}
			}
			
			/* else it is a different account */
			else {
				/* If this combination is allowed */
				if (Event.isValidEvent(pType, pDebit, myAccount)) {
					/* Add the item to the list */
					myCombo.addItem(myAccount.getName());
				}
			}
		}
		
		/* return to caller */
		return myCombo;		
	}

	/**
	 * Obtain the ComboBox of accounts for a Debit for a Transaction Type and Account
	 * @param pType the transaction type
	 * @param pDebit the debit account
	 * @return the ComboBox
	 */
	public JComboBox getDebitAccounts(TransactionType pType, Account pCredit) {
		Account.List 	myList		= theData.getAccounts();
		Account			myAccount;
		AccountType		myType		= null;
		boolean			isValid		= false;
		JComboBox		myCombo;
		
		Account.List.ListIterator myIterator;
		
		/* Access the iterator */
		myIterator = myList.listIterator();
		
		/* Create the ComboBox */
		myCombo = new JComboBox();
		
		/* Loop through the accounts */
		while ((myAccount = myIterator.next()) != null) {
			/* If the type of this account is new */
			if (AccountType.differs(myType, myAccount.getActType()).isDifferent()) {
				/* Note the type */
				myType = myAccount.getActType();

				/* Determine whether we are a valid type */
				isValid = Event.isValidEvent(pType, myType, false);
			}
			
			/* Skip invalid types */
			if (!isValid) continue;
			
			/* Skip closed items if required */
			if (myAccount.isClosed()) continue;

			/* If the account is identical to the selected account */
			if (!Account.differs(myAccount, pCredit).isDifferent()) {
				/* If this combination is allowed */
				if (Event.isValidEvent(pType, myAccount, pCredit)) {
					/* Add to beginning of list */
					myCombo.insertItemAt(myAccount.getName(), 0);
				}
			}
			
			/* else it is a different account */
			else {
				/* If this combination is allowed */
				if (Event.isValidEvent(pType, myAccount, pCredit)) {
					/* Add the item to the list */
					myCombo.addItem(myAccount.getName());
				}
			}
		}
		
		/* return to caller */
		return myCombo;		
	}
}
