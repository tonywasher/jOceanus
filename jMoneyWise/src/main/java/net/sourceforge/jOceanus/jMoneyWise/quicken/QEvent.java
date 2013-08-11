/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.quicken;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.views.InvestmentAnalysis;

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
     * The event.
     */
    private final Event theEvent;

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
    protected Event getEvent() {
        return theEvent;
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
     * @param pEvent the event
     * @param pCredit is this the credit item?
     */
    protected QEvent(final QAnalysis pAnalysis,
                     final Event pEvent,
                     final boolean pCredit) {
        /* Call super constructor */
        super(pAnalysis.getFormatter(), pAnalysis.getQIFType());

        /* Store details */
        theAnalysis = pAnalysis;
        theEvent = pEvent;
        isCredit = pCredit;
        isTransfer = pEvent.getCategory().isTransfer();

        /* Access the account */
        Account myAccount = (isCredit)
                ? pEvent.getCredit()
                : pEvent.getDebit();
        isAutoExpense = (myAccount.getAutoExpense() != null);
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
        theEvent = null;
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
        addDateLine(QEvtLineType.Date, theEvent.getDate());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(theEvent.getAmount());
        if (!isCredit) {
            myValue.negate();
        }
        addDecimalLine(QEvtLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, myReconciled);

        /* If we have a reference */
        String myRef = theEvent.getReference();
        if (myRef != null) {
            /* Add the reference */
            addStringLine(QEvtLineType.Reference, myRef);
        }

        /* Determine partner account */
        Account myPayee = (isCredit)
                ? theEvent.getDebit()
                : theEvent.getCredit();

        /* Handle portfolio accounts */
        if (myPayee.hasUnits()) {
            myPayee = myPayee.getPortfolio();
        }

        /* If the payee is autoExpense */
        EventCategory myAutoExpense = myPayee.getAutoExpense();
        if (myAutoExpense != null) {
            /* Add the autoExpense payee */
            addAutoAccountLine(QEvtLineType.Payee, myPayee);
        } else {
            /* Add standard payee */
            addLineType(QEvtLineType.Payee);

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
        String myDesc = theEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEvtLineType.Comment, myDesc);
        }

        /* If the payee is autoExpense */
        if (myAutoExpense != null) {
            /* Add the autoExpense */
            addCategoryLine(QEvtLineType.Category, myAutoExpense);
            /* else if its a transfer */
        } else if (isTransfer) {
            addXferAccountLine(QEvtLineType.Category, myPayee);
            /* else standard category */
        } else {
            addCategoryLine(QEvtLineType.Category, theEvent.getCategory());
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
    protected String buildOpeningQIF(final Account pAccount,
                                     final JDateDay pStartDate,
                                     final JMoney pBalance) {
        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEvtLineType.Date, pStartDate);

        /* Add the Amount (as a simple decimal) */
        addDecimalLine(QEvtLineType.Amount, new JDecimal(pBalance));

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, QIF_RECONCILED);

        /* Add the payee */
        addStringLine(QEvtLineType.Payee, "Opening Balance");

        if (getQIFType().selfOpeningBalance()) {
            /* Add the category as self-Opening */
            addXferAccountLine(QEvtLineType.Category, pAccount);
        } else {
            /* Add the explicit category */
            EventCategory myCat = getAnalysis().getCategory(EventCategoryClass.OpeningBalance);
            addCategoryLine(QEvtLineType.Category, myCat);
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
        addDateLine(QEvtLineType.Date, theEvent.getDate());

        /* Add the Amount */
        JDecimal myValue = new JDecimal(theEvent.getAmount());
        if (!doCredit) {
            myValue.negate();
        }
        addDecimalLine(QEvtLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, QIF_RECONCILED);

        /* If we have a reference */
        String myRef = theEvent.getReference();
        if (myRef != null) {
            /* Add the reference */
            addStringLine(QEvtLineType.Reference, myRef);
        }

        /* Determine this account and partner account */
        Account myPayee = (isCredit)
                ? theEvent.getDebit()
                : theEvent.getCredit();
        Account myAccount = (isCredit)
                ? theEvent.getCredit()
                : theEvent.getDebit();

        /* Access autoExpense */
        EventCategory myAutoExpense = myAccount.getAutoExpense();

        /* Add the payee */
        if (isCredit == doCredit) {
            addAccountLine(QEvtLineType.Payee, myPayee);
        } else {
            addAutoAccountLine(QEvtLineType.Payee, myAccount);
        }

        /* If we have a description */
        String myDesc = theEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEvtLineType.Comment, myDesc);
        }

        /* Set the category */
        addCategoryLine(QEvtLineType.Category, (isCredit == doCredit)
                ? theEvent.getCategory()
                : myAutoExpense);

        /* Complete the item */
        endItem();
    }

    /**
     * Obtain Investment Analysis for Investment Event.
     * @param pEvent the event
     * @param pSecurity the security for the event
     * @return the analysis
     */
    protected InvestmentAnalysis getInvestmentAnalysis(final Event pEvent,
                                                       final Account pSecurity) {
        /* Locate the security bucket */
        return theAnalysis.getInvestmentAnalysis(pEvent, pSecurity);
    }

    /**
     * Obtain Reconciled indication.
     * @return the reconciled flag
     */
    protected String getReconciledFlag() {
        /* Return the correct flag */
        return theEvent.isReconciled()
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
         * Register event.
         * @param pEvent the event
         * @param isCredit is this the credit item?
         */
        protected abstract void registerEvent(final Event pEvent,
                                              final boolean isCredit);

        /**
         * Register holding event.
         * @param pEvent the event
         * @param isCredit is this the credit item?
         */
        protected void registerHoldingEvent(final Event pEvent,
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

        /**
         * Register event.
         * @param pEvent the event
         * @param isCredit is this the credit item?
         */
        protected void registerEvent(final Event pEvent,
                                     final boolean isCredit) {
            QEvent myEvent;
            switch (pEvent.getCategoryClass()) {
                case TaxedIncome:
                case BenefitIncome:
                    myEvent = new QSalaryEvent(getAnalysis(), pEvent, isCredit);
                    break;
                case Interest:
                    myEvent = new QInterestEvent(getAnalysis(), pEvent, isCredit);
                    break;
                default:
                    myEvent = new QEvent(getAnalysis(), pEvent, isCredit);
                    break;
            }
            addEvent(myEvent);
        }

        /**
         * Register holding event.
         * @param pEvent the event
         * @param isCredit is this the credit item?
         */
        protected void registerHoldingEvent(final Event pEvent,
                                            final boolean isCredit) {

            /* Allocate the event */
            QHoldingEvent myEvent = new QHoldingEvent(getAnalysis(), pEvent, isCredit);
            addEvent(myEvent);
        }
    }

    /**
     * Quicken Event Line Types.
     */
    public enum QEvtLineType implements QLineType {
        /**
         * Date.
         */
        Date("D"),

        /**
         * Amount.
         */
        Amount("T"),

        /**
         * Cleared Status.
         */
        Cleared("C"),

        /**
         * Comment.
         */
        Comment("M"),

        /**
         * Reference.
         */
        Reference("N"),

        /**
         * Payee.
         */
        Payee("P"),

        /**
         * Category.
         */
        Category("L"),

        /**
         * SplitCategory.
         */
        SplitCategory("S"),

        /**
         * SplitComment.
         */
        SplitComment("E"),

        /**
         * SplitAmount.
         */
        SplitAmount("$");

        /**
         * The symbol.
         */
        private final String theSymbol;

        @Override
        public String getSymbol() {
            return theSymbol;
        }

        /**
         * Constructor.
         * @param pSymbol the symbol
         */
        private QEvtLineType(final String pSymbol) {
            /* Store symbol */
            theSymbol = pSymbol;
        }
    }
}
