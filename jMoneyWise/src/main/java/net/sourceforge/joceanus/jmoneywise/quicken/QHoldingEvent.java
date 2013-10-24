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
package net.sourceforge.joceanus.jmoneywise.quicken;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QEventLineType;

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
            case OtherIncome:
                return buildIncomeQIF();
            case Dividend:
                return buildDividendQIF();
            case StockTakeOver:
                return buildTakeOverQIF();
            default:
                break;
        }

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build income holding transaction.
     * @return the QIF entry
     */
    protected String buildIncomeQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JMoney myAmount = myEvent.getAmount();
        Account myPortfolio = myEvent.getCredit().getPortfolio();

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEventLineType.Date, myEvent.getDate());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        myValue.setZero();
        addDecimalLine(QEventLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEventLineType.Cleared, myReconciled);

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEventLineType.Comment, myDesc);
        }

        /* Add the Payee */
        addAccountLine(QEventLineType.Payee, myEvent.getDebit());

        /* Add the category */
        addCategoryLine(QEventLineType.SplitCategory, myEvent.getCategory());

        /* Access the Amount again */
        myValue = new JDecimal(myAmount);

        /* Add the Amount again */
        addDecimalLine(QEventLineType.SplitAmount, myValue);

        /* Add the portfolio account */
        addXferAccountLine(QEventLineType.SplitCategory, myPortfolio);

        /* Add the Amount again (negative) */
        myValue.negate();
        addDecimalLine(QEventLineType.SplitAmount, myValue);

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build dividend holding transaction.
     * @return the QIF entry
     */
    protected String buildDividendQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JMoney myAmount = myEvent.getAmount();
        Account mySecurity = myEvent.getDebit();
        Account myXferAccount = myEvent.getCredit();
        Account myPortfolio = mySecurity.getPortfolio();
        JMoney myTaxCredit = myEvent.getTaxCredit();
        EventCategory myCategory = getAnalysis().getCategory(EventInfoClass.TaxCredit);
        boolean isReinvested = Difference.isEqual(mySecurity, myXferAccount);
        boolean isHolding = Difference.isEqual(myPortfolio.getHolding(), myXferAccount);

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEventLineType.Date, myEvent.getDate());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        if (!isHolding) {
            myValue.setZero();
        }
        addDecimalLine(QEventLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEventLineType.Cleared, myReconciled);

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEventLineType.Comment, myDesc);
        }

        /* Add the Payee */
        addStringLine(QEventLineType.Payee, "Dividend from "
                                            + mySecurity.getName());

        /* Determine the gross amount */
        if (isReinvested) {
            myValue = new JDecimal(myTaxCredit);
        } else {
            myValue = new JDecimal(myAmount);
            if (myTaxCredit != null) {
                myValue.addValue(myTaxCredit);
            }
        }

        /* Add the gross amount */
        addXferAccountLine(QEventLineType.SplitCategory, myPortfolio);
        addDecimalLine(QEventLineType.SplitAmount, myValue);

        /* If this is not a re-invest and not a holding */
        if ((!isReinvested)
            && (!isHolding)) {
            /* Add the net amount */
            myValue = new JDecimal(myAmount);
            myValue.negate();
            addXferAccountLine(QEventLineType.SplitCategory, myXferAccount);
            addDecimalLine(QEventLineType.SplitAmount, myValue);
        }

        /* If we have a tax credit */
        if (myTaxCredit != null) {
            /* Add the tax credit */
            myValue = new JDecimal(myTaxCredit);
            myValue.negate();
            addCategoryLine(QEventLineType.SplitCategory, myCategory);
            addDecimalLine(QEventLineType.SplitAmount, myValue);
        }

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build takeOver holding transaction.
     * @return the QIF entry
     */
    protected String buildTakeOverQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JMoney myAmount = myEvent.getAmount();
        Account mySecurity = myEvent.getDebit();
        Account myXferAccount = myEvent.getThirdParty();
        Account myPortfolio = mySecurity.getPortfolio();
        boolean isHolding = Difference.isEqual(myPortfolio.getHolding(), myXferAccount);

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEventLineType.Date, myEvent.getDate());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        if (!isHolding) {
            myValue.setZero();
        }
        addDecimalLine(QEventLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEventLineType.Cleared, myReconciled);

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEventLineType.Comment, myDesc);
        }

        /* Add the Payee */
        addStringLine(QEventLineType.Payee, "TakeOver of "
                                            + mySecurity.getName());

        /* If we are not transferring to the holding account */
        if (!isHolding) {
            /* Determine the gross amount */
            myValue = new JDecimal(myAmount);

            /* Add the gross amount */
            addXferAccountLine(QEventLineType.SplitCategory, myPortfolio);
            addDecimalLine(QEventLineType.SplitAmount, myValue);

            /* Add the net amount */
            myValue.negate();
            addXferAccountLine(QEventLineType.SplitCategory, myXferAccount);
            addDecimalLine(QEventLineType.SplitAmount, myValue);

            /* else just transfer from the portfolio */
        } else {

            /* Add the portfolio Xfer */
            addXferAccountLine(QEventLineType.Category, myPortfolio);
        }

        /* Return the detail */
        return completeItem();
    }
}
