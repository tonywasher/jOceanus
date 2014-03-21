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

import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QEventLineType;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * Quicken Salary Event.
 */
public class QSalaryEvent
        extends QEvent {
    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pTrans the transaction
     * @param pCredit is this the credit item?
     */
    protected QSalaryEvent(final QAnalysis pAnalysis,
                           final Transaction pTrans,
                           final boolean pCredit) {
        /* Call super constructor */
        super(pAnalysis, pTrans, pCredit);

        /* Make sure that the additional categories are registered */
        if (pTrans.getTaxCredit() != null) {
            getAnalysis().getCategory(TransactionInfoClass.TAXCREDIT);
        }
        if (pTrans.getNatInsurance() != null) {
            getAnalysis().getCategory(TransactionInfoClass.NATINSURANCE);
        }
        if (pTrans.getDeemedBenefit() != null) {
            getAnalysis().getCategory(TransactionInfoClass.DEEMEDBENEFIT);
        }
    }

    @Override
    protected String buildQIF() {
        /* Access the transaction */
        Transaction myTrans = getTransaction();
        JMoney myAmount = myTrans.getAmount();
        JMoney myTaxCredit = myTrans.getTaxCredit();
        JMoney myNatIns = myTrans.getNatInsurance();
        JMoney myBenefit = myTrans.getDeemedBenefit();
        TransactionCategory myCategory = myTrans.getCategory();
        boolean isTaxCredit = myTaxCredit != null;
        boolean isNatIns = myNatIns != null;
        boolean isBenefit = myBenefit != null;

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEventLineType.DATE, myTrans.getDate());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QEventLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QEventLineType.CLEARED, myReconciled);

        /* If we have a reference */
        String myRef = myTrans.getReference();
        if (myRef != null) {
            /* Add the reference */
            addStringLine(QEventLineType.REFERENCE, myRef);
        }

        /* Payee is the debit account */
        addAccountLine(QEventLineType.PAYEE, myTrans.getDebit());

        /* If we have a description */
        String myDesc = myTrans.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEventLineType.COMMENT, myDesc);
        }

        /* If no split is necessary */
        if ((!isTaxCredit)
            && (!isNatIns)
            && (!isBenefit)) {
            /* Add the standard category */
            addCategoryLine(QEventLineType.CATEGORY, myCategory);
        } else {
            /* Add a Split */
            myValue = new JDecimal(myAmount);
            if (isTaxCredit) {
                myValue.addValue(myTaxCredit);
            }
            if (isNatIns) {
                myValue.addValue(myNatIns);
            }
            if (isBenefit) {
                myValue.addValue(myBenefit);
            }
            addCategoryLine(QEventLineType.SPLITCATEGORY, myCategory);
            addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);
            if (isTaxCredit) {
                myValue = new JDecimal(myTaxCredit);
                myValue.negate();
                myCategory = getAnalysis().getCategory(TransactionInfoClass.TAXCREDIT);
                addCategoryLine(QEventLineType.SPLITCATEGORY, myCategory);
                addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);
            }
            if (isNatIns) {
                myValue = new JDecimal(myNatIns);
                myValue.negate();
                myCategory = getAnalysis().getCategory(TransactionInfoClass.NATINSURANCE);
                addCategoryLine(QEventLineType.SPLITCATEGORY, myCategory);
                addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);
            }
            if (isBenefit) {
                myValue = new JDecimal(myBenefit);
                myValue.negate();
                myCategory = getAnalysis().getCategory(TransactionInfoClass.DEEMEDBENEFIT);
                addCategoryLine(QEventLineType.SPLITCATEGORY, myCategory);
                addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);
            }
        }

        /* Return the result */
        return completeItem();
    }
}
