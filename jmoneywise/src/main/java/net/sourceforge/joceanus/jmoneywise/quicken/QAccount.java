/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.quicken;

import java.io.IOException;
import java.io.OutputStreamWriter;

import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.quicken.QEvent.QEventBaseList;
import net.sourceforge.joceanus.jmoneywise.quicken.QEvent.QEventList;
import net.sourceforge.joceanus.jmoneywise.quicken.QPortfolioEvent.QPortfolioEventList;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QAccountLineType;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * Quicken Account.
 */
public final class QAccount
        extends QElement {
    /**
     * Item type.
     */
    protected static final String QIF_ITEM = "!Account";

    /**
     * The analysis.
     */
    private final QAnalysis theAnalysis;

    /**
     * The account.
     */
    private final AssetBase<?> theAccount;

    /**
     * The opening balance.
     */
    private final JMoney theOpeningBalance;

    /**
     * The events.
     */
    private final QEventBaseList<? extends QEvent> theEvents;

    /**
     * Is this account an autoExpense?
     */
    private final boolean isAutoExpense;

    /**
     * Obtain the account name.
     * @return the account name
     */
    public String getName() {
        return theAccount.getName();
    }

    /**
     * Obtain the account description.
     * @return the account name
     */
    public String getDesc() {
        return theAccount.getDesc();
    }

    /**
     * Obtain the account.
     * @return the account
     */
    protected AssetBase<?> getAccount() {
        return theAccount;
    }

    /**
     * Obtain the account parent.
     * @return the parent
     */
    protected Payee getParent() {
        if (theAccount instanceof Deposit) {
            return ((Deposit) theAccount).getParent();
        }
        if (theAccount instanceof Loan) {
            return ((Loan) theAccount).getParent();
        }
        return null;
    }

    /**
     * Obtain the autoExpense category.
     * @return the parent
     */
    protected EventCategory getAutoExpense() {
        if (theAccount instanceof Cash) {
            return ((Cash) theAccount).getAutoExpense();
        }
        return null;
    }

    /**
     * Obtain the holding account.
     * @return the holding account
     */
    protected Deposit getHolding() {
        return (theAccount instanceof Portfolio)
                                                ? ((Portfolio) theAccount).getHolding()
                                                : null;
    }

    /**
     * Is the account using autoExpense?
     * @return true/false
     */
    protected boolean isAutoExpense() {
        return isAutoExpense;
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pAccount the account
     */
    protected QAccount(final QAnalysis pAnalysis,
                       final AssetBase<?> pAccount) {
        /* Call super constructor */
        super(pAnalysis.getFormatter(), pAnalysis.getQIFType());

        /* Store the parameters */
        theAnalysis = pAnalysis;
        theAccount = pAccount;

        /* Determine flags */
        isAutoExpense = (pAccount instanceof Cash)
                        && (((Cash) pAccount).getAutoExpense() != null);
        boolean isPortfolio = pAccount instanceof Portfolio;

        /* Determine opening balance */
        theOpeningBalance = (pAccount instanceof Deposit)
                                                         ? ((Deposit) pAccount).getOpeningBalance()
                                                         : null;

        /* Allocate event list */
        theEvents = (isPortfolio)
                                 ? new QPortfolioEventList(pAnalysis, this)
                                 : new QEventList(pAnalysis, this);
    }

    /**
     * build QIF format.
     * @return the QIF format
     */
    protected String buildQIF() {
        /* Reset the builder */
        reset();

        /* Add the Account name */
        addAccountLine(QAccountLineType.NAME, theAccount);

        /* Add the AccountType */
        addStringLine(QAccountLineType.TYPE, getAccountType());

        /* If we have a description */
        String myDesc = theAccount.getDesc();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QAccountLineType.DESCRIPTION, myDesc);
        }

        /* Return the result */
        return completeItem();
    }

    @Override
    public String toString() {
        return buildQIF();
    }

    /**
     * build QIF format header.
     * @param pStartDate the start date
     * @return the QIF format
     */
    protected String buildQIFHeader(final JDateDay pStartDate) {
        /* Obtain the account type */
        String myType = getAccountType();

        /* Reset the builder */
        reset();

        /* Add the Account indicator */
        append(QIF_ITEM);
        endLine();

        /* Add the Account name */
        addAccountLine(QAccountLineType.NAME, theAccount);

        /* If we have a description */
        String myDesc = theAccount.getDesc();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QAccountLineType.DESCRIPTION, myDesc);
        }

        /* Add the Account type */
        addStringLine(QAccountLineType.TYPE, myType);

        /* Add the End indicator */
        endItem();

        /* Add the Item type */
        append(QIF_ITEMTYPE);
        append(myType);
        endLine();

        /* If the account has an opening balance */
        if (theOpeningBalance != null) {
            /* Create the opening balance event */
            QEvent myEvent = new QEvent(theAnalysis);
            append(myEvent.buildOpeningQIF(theAccount, pStartDate, theOpeningBalance));
        }

        /* Return the builder */
        return getBufferedString();
    }

    /**
     * Determine account type.
     * @return the account type
     */
    protected String getAccountType() {
        if (theAccount instanceof Deposit) {
            return "Bank";
        }
        if (theAccount instanceof Cash) {
            return "Cash";
        }
        if (theAccount instanceof Portfolio) {
            return "Invst";
        }
        if (theAccount instanceof Loan) {
            Loan myLoan = (Loan) theAccount;
            switch (myLoan.getCategoryClass()) {
                case CREDITCARD:
                    return "CCard";
                case PRIVATELOAN:
                    return "Oth A";
                case LOAN:
                    return "Oth L";
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * Process the transaction.
     * @param pTrans the transaction
     * @param isCredit is this the credit item?
     */
    protected void processTransaction(final Transaction pTrans,
                                      final boolean isCredit) {
        /* register the transaction and add to the list */
        theEvents.registerTransaction(pTrans, isCredit);
    }

    /**
     * Process the holding transaction.
     * @param pTrans the transaction
     * @param isCredit is this the credit item?
     */
    protected void processHoldingTransaction(final Transaction pTrans,
                                             final boolean isCredit) {
        /* register the event and add to the list */
        theEvents.registerHoldingTransaction(pTrans, isCredit);
    }

    /**
     * is the account active.
     * @return true/false
     */
    protected boolean isActive() {
        /* Check size of list */
        return (!theEvents.isEmpty()) || (theOpeningBalance != null);
    }

    /**
     * Output events.
     * @param pStream the output stream
     * @param pStartDate the opening date
     * @throws IOException on error
     */
    protected void outputEvents(final OutputStreamWriter pStream,
                                final JDateDay pStartDate) throws IOException {
        /* If the account is active */
        if (isActive()) {
            /* Output the events */
            if (isAutoExpense) {
                theEvents.outputAutoEvents(pStream, pStartDate);
            } else {
                theEvents.outputEvents(pStream, pStartDate);
            }
        }
    }
}
