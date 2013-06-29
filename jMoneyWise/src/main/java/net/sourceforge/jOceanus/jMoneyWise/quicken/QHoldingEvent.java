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

import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;

/**
 * Quicken Interest Event.
 */
public class QHoldingEvent
        extends QEvent {
    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pEvent the event
     * @param pCredit is this the credit item?
     */
    protected QHoldingEvent(final QAnalysis pAnalysis,
                            final Event pEvent,
                            final boolean pCredit) {
        /* Call super constructor */
        super(pAnalysis, pEvent, pCredit);
    }

    @Override
    protected String buildQIF() {
        Event myEvent = getEvent();

        /* Switch on transaction type */
        switch (myEvent.getCategoryClass()) {
            case Inherited:
                return buildInheritedQIF();
            default:
                break;
        }

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build inherited holding transaction.
     * @return the QIF entry
     */
    protected String buildInheritedQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JMoney myAmount = myEvent.getAmount();
        Account myPortfolio = myEvent.getCredit().getParent();

        /* Determine whether we need to use a separate transfer */
        boolean xtraXfer = (!getQIFType().supportsSplitTransfer());

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEvtLineType.Date, myEvent.getDate());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        if (!xtraXfer) {
            myValue.setZero();
        }
        addDecimalLine(QEvtLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, (myEvent.getReconciled() == Boolean.TRUE)
                ? QIF_RECONCILED
                : QIF_OPEN);

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEvtLineType.Comment, myDesc);
        }

        /* Add the Payee */
        addAccountLine(QEvtLineType.Payee, myEvent.getDebit());

        if (!xtraXfer) {
            /* Add the category */
            addCategoryLine(QEvtLineType.SplitCategory, myEvent.getCategory());

            /* Access the Amount again */
            myValue = new JDecimal(myAmount);

            /* Add the Amount again */
            addDecimalLine(QEvtLineType.SplitAmount, myValue);

            /* Add the portfolio account */
            addXferAccountLine(QEvtLineType.SplitCategory, myPortfolio);

            /* Add the Amount again (negative) */
            myValue.negate();
            addDecimalLine(QEvtLineType.SplitAmount, myValue);

            /* else need to split out the record */
        } else {
            /* Just add the category */
            addCategoryLine(QEvtLineType.Category, myEvent.getCategory());

            /* End the item */
            endItem();

            /* Add the Date */
            addDateLine(QEvtLineType.Date, myEvent.getDate());

            /* Add the Amount (as a simple decimal) */
            myValue = new JDecimal(myAmount);
            myValue.negate();
            addDecimalLine(QEvtLineType.Amount, myValue);

            /* Add the Cleared status */
            addStringLine(QEvtLineType.Cleared, (myEvent.getReconciled() == Boolean.TRUE)
                    ? QIF_RECONCILED
                    : QIF_OPEN);

            /* Payee is the credit account */
            addLineType(QEvtLineType.Payee);
            append(QIF_XFER);
            if (!getQIFType().useSimpleTransfer()) {
                append(QIF_XFERTO);
                addAccount(myPortfolio);
            }
            endLine();

            /* If we have a description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QEvtLineType.Comment, myDesc);
            }

            /* Add the transfer details */
            addXferAccountLine(QEvtLineType.Category, myPortfolio);
        }

        /* Return the detail */
        return completeItem();
    }
}
