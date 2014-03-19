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

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QEventLineType;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * Quicken Interest Event.
 */
public class QHoldingEvent
        extends QEvent {
    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pTrans the transaction
     * @param pCredit is this the credit item?
     */
    protected QHoldingEvent(final QAnalysis pAnalysis,
                            final Transaction pTrans,
                            final boolean pCredit) {
        /* Call super constructor */
        super(pAnalysis, pTrans, pCredit);
    }

    @Override
    protected String buildQIF() {
        Transaction myTrans = getTransaction();

        /* Switch on transaction type */
        switch (myTrans.getCategoryClass()) {
            case INHERITED:
            case OTHERINCOME:
                return buildIncomeQIF();
            case DIVIDEND:
                return buildDividendQIF();
            case STOCKTAKEOVER:
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
        /* Access the transaction */
        Transaction myTrans = getTransaction();
        JMoney myAmount = myTrans.getAmount();
        Portfolio myPortfolio = myTrans.getPortfolio();

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEventLineType.DATE, myTrans.getDate());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        myValue.setZero();
        addDecimalLine(QEventLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QEventLineType.CLEARED, myReconciled);

        /* If we have a description */
        String myDesc = myTrans.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEventLineType.COMMENT, myDesc);
        }

        /* Add the Payee */
        addAccountLine(QEventLineType.PAYEE, myTrans.getDebit());

        /* Add the category */
        addCategoryLine(QEventLineType.SPLITCATEGORY, myTrans.getCategory());

        /* Access the Amount again */
        myValue = new JDecimal(myAmount);

        /* Add the Amount again */
        addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);

        /* Add the portfolio account */
        addXferAccountLine(QEventLineType.SPLITCATEGORY, myPortfolio);

        /* Add the Amount again (negative) */
        myValue.negate();
        addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build dividend holding transaction.
     * @return the QIF entry
     */
    protected String buildDividendQIF() {
        /* Access the transaction */
        Transaction myTrans = getTransaction();
        JMoney myAmount = myTrans.getAmount();
        AssetBase<?> mySecurity = myTrans.getDebit();
        AssetBase<?> myXferAccount = myTrans.getCredit();
        Portfolio myPortfolio = myTrans.getPortfolio();
        JMoney myTaxCredit = myTrans.getTaxCredit();
        EventCategory myCategory = getAnalysis().getCategory(EventInfoClass.TAXCREDIT);
        boolean isReinvested = Difference.isEqual(mySecurity, myXferAccount);
        boolean isHolding = Difference.isEqual(myPortfolio.getHolding(), myXferAccount);

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEventLineType.DATE, myTrans.getDate());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        if (!isHolding) {
            myValue.setZero();
        }
        addDecimalLine(QEventLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QEventLineType.CLEARED, myReconciled);

        /* If we have a description */
        String myDesc = myTrans.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEventLineType.COMMENT, myDesc);
        }

        /* Add the Payee */
        addStringLine(QEventLineType.PAYEE, "Dividend from "
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
        addXferAccountLine(QEventLineType.SPLITCATEGORY, myPortfolio);
        addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);

        /* If this is not a re-invest and not a holding */
        if ((!isReinvested)
            && (!isHolding)) {
            /* Add the net amount */
            myValue = new JDecimal(myAmount);
            myValue.negate();
            addXferAccountLine(QEventLineType.SPLITCATEGORY, myXferAccount);
            addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);
        }

        /* If we have a tax credit */
        if (myTaxCredit != null) {
            /* Add the tax credit */
            myValue = new JDecimal(myTaxCredit);
            myValue.negate();
            addCategoryLine(QEventLineType.SPLITCATEGORY, myCategory);
            addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);
        }

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build takeOver holding transaction.
     * @return the QIF entry
     */
    protected String buildTakeOverQIF() {
        /* Access the transaction */
        Transaction myTrans = getTransaction();
        JMoney myAmount = myTrans.getAmount();
        AssetBase<?> mySecurity = myTrans.getDebit();
        Deposit myXferAccount = myTrans.getThirdParty();
        Portfolio myPortfolio = myTrans.getPortfolio();
        boolean isHolding = Difference.isEqual(myPortfolio.getHolding(), myXferAccount);

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEventLineType.DATE, myTrans.getDate());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        if (!isHolding) {
            myValue.setZero();
        }
        addDecimalLine(QEventLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QEventLineType.CLEARED, myReconciled);

        /* If we have a description */
        String myDesc = myTrans.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEventLineType.COMMENT, myDesc);
        }

        /* Add the Payee */
        addStringLine(QEventLineType.PAYEE, "TakeOver of "
                                            + mySecurity.getName());

        /* If we are not transferring to the holding account */
        if (!isHolding) {
            /* Determine the gross amount */
            myValue = new JDecimal(myAmount);

            /* Add the gross amount */
            addXferAccountLine(QEventLineType.SPLITCATEGORY, myPortfolio);
            addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);

            /* Add the net amount */
            myValue.negate();
            addXferAccountLine(QEventLineType.SPLITCATEGORY, myXferAccount);
            addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);

            /* else just transfer from the portfolio */
        } else {

            /* Add the portfolio Xfer */
            addXferAccountLine(QEventLineType.CATEGORY, myPortfolio);
        }

        /* Return the detail */
        return completeItem();
    }
}
