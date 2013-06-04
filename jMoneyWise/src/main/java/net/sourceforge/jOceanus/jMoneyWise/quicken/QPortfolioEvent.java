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
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.quicken.QEvent.QEvtLineType;

/**
 * Quicken Portfolio Event Representation.
 */
public final class QPortfolioEvent {

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
     * Constructor.
     * @param pEvent the event
     * @param pCredit is this the credit item?
     */
    private QPortfolioEvent(final Event pEvent,
                            final boolean pCredit) {
        /* Store details */
        theEvent = pEvent;
        isCredit = pCredit;
        isTransfer = pEvent.getCategory().isTransfer();
    }

    /**
     * build QIF format.
     * @param pFormatter the formatter
     * @return the QIF format
     */
    protected String buildQIF(final JDataFormatter pFormatter) {
        StringBuilder myBuilder = new StringBuilder();

        /* Add the Date */
        myBuilder.append(QEvtLineType.Date.getSymbol());
        myBuilder.append(pFormatter.formatObject(theEvent.getDate()));
        myBuilder.append(QDataSet.QIF_EOL);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(theEvent.getAmount());
        if (!isCredit) {
            myValue.negate();
        }
        myBuilder.append(QEvtLineType.Amount.getSymbol());
        myBuilder.append(pFormatter.formatObject(myValue));
        myBuilder.append(QDataSet.QIF_EOL);

        /* Add the Cleared status */
        myBuilder.append(QEvtLineType.Cleared.getSymbol());
        myBuilder.append((theEvent.getReconciled() == Boolean.TRUE)
                ? QEvent.QIF_RECONCILED
                : QDataSet.QIF_BLANK);
        myBuilder.append(QDataSet.QIF_EOL);

        /* If we have a reference */
        String myRef = theEvent.getReference();
        if (myRef != null) {
            /* Add the reference */
            myBuilder.append(QEvtLineType.Reference.getSymbol());
            myBuilder.append(myRef);
            myBuilder.append(QDataSet.QIF_EOL);
        }

        /* Determine partner account */
        Account myPayee = (isCredit)
                ? theEvent.getDebit()
                : theEvent.getCredit();

        /* If the payee is autoExpense */
        EventCategory myAutoExpense = myPayee.getAutoExpense();
        myBuilder.append(QEvtLineType.Payee.getSymbol());
        if (myAutoExpense != null) {
            /* Add the payee */
            myBuilder.append(myPayee.getName());
            myBuilder.append("Expense");
        } else {
            /* Add the payee */
            if (isTransfer) {
                myBuilder.append("Transfer ");
                myBuilder.append((isCredit)
                        ? "from "
                        : "to ");
            }
            myBuilder.append(myPayee.getName());
        }
        myBuilder.append(QDataSet.QIF_EOL);

        /* If we have a description */
        String myDesc = theEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            myBuilder.append(QEvtLineType.Comment.getSymbol());
            myBuilder.append(myDesc);
            myBuilder.append(QDataSet.QIF_EOL);
        }

        /* If the payee is autoExpense */
        myBuilder.append(QEvtLineType.Category.getSymbol());
        if (myAutoExpense != null) {
            /* Add the autoExpense */
            myBuilder.append(myAutoExpense.getName());
        } else {
            /* Add the category */
            if (isTransfer) {
                myBuilder.append(QEvent.QIF_XFERSTART);
                myBuilder.append(myPayee.getName());
                myBuilder.append(QEvent.QIF_XFEREND);
            } else {
                myBuilder.append(theEvent.getCategoryName());
            }
        }
        myBuilder.append(QDataSet.QIF_EOL);

        /* Add the End indicator */
        myBuilder.append(QDataSet.QIF_EOI);
        myBuilder.append(QDataSet.QIF_EOL);

        /* Return the builder */
        return myBuilder.toString();
    }

    /**
     * Portfolio Event List class.
     */
    protected static class QPortfolioEventList {
        /**
         * Event List.
         */
        private final List<QPortfolioEvent> theEvents;

        /**
         * The account.
         */
        private final QAccount theAccount;

        /**
         * Data Formatter.
         */
        private final JDataFormatter theFormatter;

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
        protected QPortfolioEventList(final QAccount pAccount,
                                      final JDataFormatter pFormatter) {
            /* Create the list */
            theEvents = new ArrayList<QPortfolioEvent>();
            theAccount = pAccount;
            theFormatter = pFormatter;
        }

        /**
         * Register event.
         * @param pEvent the event
         * @param isCredit is this the credit item?
         */
        protected void registerEvent(final Event pEvent,
                                     final boolean isCredit) {
            QPortfolioEvent myEvent = new QPortfolioEvent(pEvent, isCredit);
            theEvents.add(myEvent);
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
            pStream.write(theAccount.buildQIFHeader(theFormatter, pStartDate));

            /* Loop through the events */
            for (QPortfolioEvent myEvent : theEvents) {
                /* Write Account details */
                pStream.write(myEvent.buildQIF(theFormatter));
            }
        }
    }

    /**
     * Quicken Portfolio Event Line Types.
     */
    public enum QPortLineType {
        /**
         * Date.
         */
        Date("D"),

        /**
         * Action.
         */
        Action("N"),

        /**
         * Security.
         */
        Security("Y"),

        /**
         * Price.
         */
        Price("I"),

        /**
         * Quantity.
         */
        Quantity("Q"),

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
         * Payee.
         */
        Payee("P"),

        /**
         * Commission.
         */
        Commission("O"),

        /**
         * TransferAccount.
         */
        TransferAccount("L"),

        /**
         * TransferAmount.
         */
        TransferAmount("$");

        /**
         * The symbol.
         */
        private final String theSymbol;

        /**
         * Obtain the symbol.
         * @return the symbol
         */
        public String getSymbol() {
            return theSymbol;
        }

        /**
         * Constructor.
         * @param pSymbol the symbol
         */
        private QPortLineType(final String pSymbol) {
            /* Store symbol */
            theSymbol = pSymbol;
        }
    }
}
