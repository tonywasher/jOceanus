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

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;

/**
 * Quicken Standard event.
 */
public class QEvent
        extends QElement {
    /**
     * Reconciled flag.
     */
    protected static final String QIF_RECONCILED = "R";

    /**
     * Quicken Blank.
     */
    protected static final String QIF_OPEN = " ";

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
     * Constructor.
     * @param pFormatter the data formatter
     * @param pEvent the event
     * @param pCredit is this the credit item?
     */
    protected QEvent(final JDataFormatter pFormatter,
                     final Event pEvent,
                     final boolean pCredit) {
        /* Call super constructor */
        super(pFormatter);

        /* Store details */
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
     * @param pFormatter the data formatter
     */
    protected QEvent(final JDataFormatter pFormatter) {
        /* Call super constructor */
        super(pFormatter);

        /* Store details */
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
        addStringLine(QEvtLineType.Cleared, (theEvent.getReconciled() == Boolean.TRUE)
                ? QIF_RECONCILED
                : QIF_OPEN);

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

        /* If the payee is autoExpense */
        EventCategory myAutoExpense = myPayee.getAutoExpense();
        if (myAutoExpense != null) {
            /* Add the autoExpense payee */
            addAutoAccountLine(QEvtLineType.Payee, myPayee);
        } else {
            /* Add standard payee */
            addLineType(QEvtLineType.Payee);

            /* Add the payee */
            if (isTransfer) {
                append("Transfer ");
                append((isCredit)
                        ? "from "
                        : "to ");
            }
            addAccount(myPayee);
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
     * @param pFormatter the formatter
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

        /* Add the category */
        addXferAccountLine(QEvtLineType.Category, pAccount);

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
     * Event List class.
     */
    protected static abstract class QEventBaseList<T extends QEvent>
            extends QElement {
        /**
         * Event List.
         */
        private final List<T> theEvents;

        /**
         * The account.
         */
        private final QAccount theAccount;

        /**
         * Obtain event list size.
         * @return the size
         */
        protected int size() {
            return theEvents.size();
        }

        /**
         * Constructor.
         * @param pAccount the list owner
         * @param pFormatter the data formatter
         */
        protected QEventBaseList(final QAccount pAccount,
                                 final JDataFormatter pFormatter) {
            /* Call super constructor */
            super(pFormatter);

            /* Create the list */
            theEvents = new ArrayList<T>();
            theAccount = pAccount;
        }

        /**
         * Register event.
         * @param pEvent the event
         * @param isCredit is this the credit item?
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
         * @param pAccount the list owner
         * @param pFormatter the data formatter
         */
        protected QEventList(final QAccount pAccount,
                             final JDataFormatter pFormatter) {
            /* Call super constructor */
            super(pAccount, pFormatter);
        }

        /**
         * Register event.
         * @param pEvent the event
         * @param isCredit is this the credit item?
         */
        protected void registerEvent(final Event pEvent,
                                     final boolean isCredit) {
            QEvent myEvent = new QEvent(getFormatter(), pEvent, isCredit);
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
