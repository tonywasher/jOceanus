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
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.newanalysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QEventLineType;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * Quicken Standard event.
 */
public class QEvent
        extends QElement {
    /**
     * Reconciled flag.
     */
    protected static final String QIF_RECONCILED = "X";

    /**
     * Quicken Transfer.
     */
    protected static final String QIF_XFER = "Transfer";

    /**
     * Quicken Transfer from.
     */
    protected static final String QIF_XFERFROM = " from ";

    /**
     * Quicken Transfer to.
     */
    protected static final String QIF_XFERTO = " to ";

    /**
     * Quicken Blank.
     */
    protected static final String QIF_OPEN = " ";

    /**
     * The analysis.
     */
    private final QAnalysis theAnalysis;

    /**
     * The transaction.
     */
    private final Transaction theTransaction;

    /**
     * Is this a transfer?
     */
    private final boolean isTransfer;

    /**
     * Is this a credit?
     */
    private final boolean isCredit;

    /**
     * Is this an autoExpense event?
     */
    private final boolean isAutoExpense;

    /**
     * Obtain analysis.
     * @return the analysis
     */
    protected QAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain the event.
     * @return the event
     */
    protected Transaction getTransaction() {
        return theTransaction;
    }

    /**
     * Is the event a credit.
     * @return true/false
     */
    protected boolean isCredit() {
        return isCredit;
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pTrans the transaction
     * @param pCredit is this the credit item?
     */
    protected QEvent(final QAnalysis pAnalysis,
                     final Transaction pTrans,
                     final boolean pCredit) {
        /* Call super constructor */
        super(pAnalysis.getFormatter(), pAnalysis.getQIFType());

        /* Store details */
        theAnalysis = pAnalysis;
        theTransaction = pTrans;
        isCredit = pCredit;
        isTransfer = pTrans.getCategory().isTransfer();

        /* Access the account */
        AssetBase<?> myAccount = (isCredit)
                                           ? pTrans.getCredit()
                                           : pTrans.getDebit();
        isAutoExpense = ((myAccount instanceof Cash)
                && ((Cash) myAccount).isAutoExpense());
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     */
    protected QEvent(final QAnalysis pAnalysis) {
        /* Call super constructor */
        super(pAnalysis.getFormatter(), pAnalysis.getQIFType());

        /* Store details */
        theAnalysis = pAnalysis;
        theTransaction = null;
        isCredit = true;
        isTransfer = true;
        isAutoExpense = false;
    }

    /**
     * build QIF format.
     * @return the QIF format
     */
    protected String buildQIF() {
        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEventLineType.DATE, theTransaction.getDate());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(theTransaction.getAmount());
        if (!isCredit) {
            myValue.negate();
        }
        addDecimalLine(QEventLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QEventLineType.CLEARED, myReconciled);

        /* If we have a reference */
        String myRef = theTransaction.getReference();
        if (myRef != null) {
            /* Add the reference */
            addStringLine(QEventLineType.REFERENCE, myRef);
        }

        /* Determine partner account */
        AssetBase<?> myPayee = (isCredit)
                                         ? theTransaction.getDebit()
                                         : theTransaction.getCredit();

        /* Handle portfolio accounts */
        if (myPayee instanceof Security) {
            myPayee = theTransaction.getPortfolio();
        }

        /* If the payee is autoExpense */
        EventCategory myAutoExpense = (myPayee instanceof Cash)
                                                               ? ((Cash) myPayee).getAutoExpense()
                                                               : null;
        if (myAutoExpense != null) {
            /* Add the autoExpense payee */
            addAutoAccountLine(QEventLineType.PAYEE, myPayee);
        } else {
            /* Add standard payee */
            addLineType(QEventLineType.PAYEE);

            /* If this is a transfer */
            if (isTransfer) {
                /* Add transfer indication */
                append(QIF_XFER);

                /* If we are not using simple transfer */
                if (!getQIFType().useSimpleTransfer()) {
                    /* Add extra detail */
                    append((isCredit)
                                     ? QIF_XFERFROM
                                     : QIF_XFERTO);
                    addAccount(myPayee);
                }
                /* else just add payee */
            } else {
                addAccount(myPayee);
            }

            /* End the line */
            endLine();
        }

        /* If we have a description */
        String myDesc = theTransaction.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEventLineType.COMMENT, myDesc);
        }

        /* If the payee is autoExpense */
        if (myAutoExpense != null) {
            /* Add the autoExpense */
            addCategoryLine(QEventLineType.CATEGORY, myAutoExpense);
            /* else if its a transfer */
        } else if (isTransfer) {
            addXferAccountLine(QEventLineType.CATEGORY, myPayee);
            /* else standard category */
        } else {
            addCategoryLine(QEventLineType.CATEGORY, theTransaction.getCategory());
        }

        /* Return the result */
        return completeItem();
    }

    @Override
    public String toString() {
        if (isAutoExpense) {
            buildAutoExpenseCorrectionQIF(true);
            buildAutoExpenseCorrectionQIF(false);
            return getBufferedString();
        } else {
            return buildQIF();
        }
    }

    /**
     * build OpeningBalance QIF format.
     * @param pAccount the account
     * @param pStartDate the opening date
     * @param pBalance the opening balance
     * @return the QIF format
     */
    protected String buildOpeningQIF(final AssetBase<?> pAccount,
                                     final JDateDay pStartDate,
                                     final JMoney pBalance) {
        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEventLineType.DATE, pStartDate);

        /* Add the Amount (as a simple decimal) */
        addDecimalLine(QEventLineType.AMOUNT, new JDecimal(pBalance));

        /* Add the Cleared status */
        addStringLine(QEventLineType.CLEARED, QIF_RECONCILED);

        /* Add the payee */
        addStringLine(QEventLineType.PAYEE, "Opening Balance");

        if (getQIFType().selfOpeningBalance()) {
            /* Add the category as self-Opening */
            addXferAccountLine(QEventLineType.CATEGORY, pAccount);
        } else {
            /* Add the explicit category */
            addStringLine(QEventLineType.CATEGORY, "Income:OpeningBalance");
        }

        /* Return the result */
        return completeItem();
    }

    /**
     * build AutoExpense Correction QIF format.
     * @param doCredit perform credit correction
     */
    protected void buildAutoExpenseCorrectionQIF(final boolean doCredit) {
        /* Add the Date */
        addDateLine(QEventLineType.DATE, theTransaction.getDate());

        /* Add the Amount */
        JDecimal myValue = new JDecimal(theTransaction.getAmount());
        if (!doCredit) {
            myValue.negate();
        }
        addDecimalLine(QEventLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QEventLineType.CLEARED, QIF_RECONCILED);

        /* If we have a reference */
        String myRef = theTransaction.getReference();
        if (myRef != null) {
            /* Add the reference */
            addStringLine(QEventLineType.REFERENCE, myRef);
        }

        /* Determine this account and partner account */
        AssetBase<?> myPayee = (isCredit)
                                         ? theTransaction.getDebit()
                                         : theTransaction.getCredit();
        AssetBase<?> myAccount = (isCredit)
                                           ? theTransaction.getCredit()
                                           : theTransaction.getDebit();

        /* Access autoExpense */
        EventCategory myAutoExpense = (myAccount instanceof Cash)
                                                                 ? ((Cash) myAccount).getAutoExpense()
                                                                 : null;

        /* Add the payee */
        if (isCredit == doCredit) {
            addAccountLine(QEventLineType.PAYEE, myPayee);
        } else {
            addAutoAccountLine(QEventLineType.PAYEE, myAccount);
        }

        /* If we have a description */
        String myDesc = theTransaction.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEventLineType.COMMENT, myDesc);
        }

        /* Set the category */
        addCategoryLine(QEventLineType.CATEGORY, (isCredit == doCredit)
                                                                       ? theTransaction.getCategory()
                                                                       : myAutoExpense);

        /* Complete the item */
        endItem();
    }

    /**
     * Obtain SecurityBucket for security.
     * @param pSecurity the security
     * @return the bucket
     */
    protected SecurityBucket getSecurityBucket(final AssetBase<?> pSecurity) {
        /* Locate the security bucket */
        Portfolio myPortfolio = theTransaction.getPortfolio();
        return theAnalysis.getSecurityBucket(myPortfolio, pSecurity);
    }

    /**
     * Obtain Reconciled indication.
     * @return the reconciled flag
     */
    protected String getReconciledFlag() {
        /* Return the correct flag */
        return theTransaction.isReconciled()
                                            ? QIF_RECONCILED
                                            : QIF_OPEN;
    }

    /**
     * Event List class.
     */
    protected abstract static class QEventBaseList<T extends QEvent>
            extends QElement {
        /**
         * The analysis.
         */
        private final QAnalysis theAnalysis;

        /**
         * Event List.
         */
        private final List<T> theEvents;

        /**
         * The account.
         */
        private final QAccount theAccount;

        /**
         * Obtain analysis.
         * @return the analysis
         */
        protected QAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain account.
         * @return the account
         */
        protected QAccount getAccount() {
            return theAccount;
        }

        /**
         * Obtain event list size.
         * @return the size
         */
        protected int size() {
            return theEvents.size();
        }

        /**
         * is the list empty?
         * @return true/false
         */
        protected boolean isEmpty() {
            return theEvents.isEmpty();
        }

        /**
         * Constructor.
         * @param pAnalysis the analysis
         * @param pAccount the list owner
         */
        protected QEventBaseList(final QAnalysis pAnalysis,
                                 final QAccount pAccount) {
            /* Call super constructor */
            super(pAnalysis.getFormatter(), pAnalysis.getQIFType());

            /* Create the list */
            theAnalysis = pAnalysis;
            theEvents = new ArrayList<T>();
            theAccount = pAccount;
        }

        /**
         * Add event to list.
         * @param pEvent the event
         */
        protected void addEvent(final T pEvent) {
            theEvents.add(pEvent);
        }

        /**
         * Register transaction.
         * @param pTrans the transaction
         * @param isCredit is this the credit item?
         */
        protected abstract void registerTransaction(final Transaction pTrans,
                                                    final boolean isCredit);

        /**
         * Register holding transaction.
         * @param pTrans the transaction
         * @param isCredit is this the credit item?
         */
        protected void registerHoldingTransaction(final Transaction pTrans,
                                                  final boolean isCredit) {
        }

        /**
         * Output events.
         * @param pStream the output stream
         * @param pStartDate the opening date
         * @throws IOException on error
         */
        protected void outputEvents(final OutputStreamWriter pStream,
                                    final JDateDay pStartDate) throws IOException {
            /* Write the account header */
            pStream.write(theAccount.buildQIFHeader(pStartDate));

            /* Loop through the events */
            for (T myEvent : theEvents) {
                /* Write Event details */
                pStream.write(myEvent.buildQIF());
            }
        }

        /**
         * Output autoExpense.
         * @param pStream the output stream
         * @param pStartDate the opening date
         * @throws IOException on error
         */
        protected void outputAutoEvents(final OutputStreamWriter pStream,
                                        final JDateDay pStartDate) throws IOException {
            /* Write the account header */
            pStream.write(theAccount.buildQIFHeader(pStartDate));

            /* Loop through the events */
            for (T myEvent : theEvents) {
                /* Write Event details */
                myEvent.reset();
                myEvent.buildAutoExpenseCorrectionQIF(true);
                myEvent.buildAutoExpenseCorrectionQIF(false);
                pStream.write(myEvent.getBufferedString());
            }
        }
    }

    /**
     * Event List class.
     */
    protected static class QEventList
            extends QEventBaseList<QEvent> {
        /**
         * Constructor.
         * @param pAnalysis the analysis
         * @param pAccount the list owner
         */
        protected QEventList(final QAnalysis pAnalysis,
                             final QAccount pAccount) {
            /* Call super constructor */
            super(pAnalysis, pAccount);
        }

        @Override
        protected void registerTransaction(final Transaction pTrans,
                                           final boolean isCredit) {
            QEvent myEvent;
            switch (pTrans.getCategoryClass()) {
                case TAXEDINCOME:
                case BENEFITINCOME:
                    myEvent = new QSalaryEvent(getAnalysis(), pTrans, isCredit);
                    break;
                case INTEREST:
                    myEvent = new QInterestEvent(getAnalysis(), pTrans, isCredit);
                    break;
                default:
                    myEvent = new QEvent(getAnalysis(), pTrans, isCredit);
                    break;
            }
            addEvent(myEvent);
        }

        @Override
        protected void registerHoldingTransaction(final Transaction pTrans,
                                                  final boolean isCredit) {

            /* Allocate the event */
            QHoldingEvent myEvent = new QHoldingEvent(getAnalysis(), pTrans, isCredit);
            addEvent(myEvent);
        }
    }
}
