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
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QEventLineType;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * Quicken Interest Event.
 */
public class QInterestEvent
        extends QEvent {
    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pTrans the transaction
     * @param pCredit is this the credit item?
     */
    protected QInterestEvent(final QAnalysis pAnalysis,
                             final Transaction pTrans,
                             final boolean pCredit) {
        /* Call super constructor */
        super(pAnalysis, pTrans, pCredit);

        /* Make sure that the additional categories are registered */
        getAnalysis().getInterestCategory(pTrans);
        if (pTrans.getTaxCredit() != null) {
            getAnalysis().getCategory(EventInfoClass.TAXCREDIT);
        }
        if (pTrans.getCharityDonation() != null) {
            getAnalysis().getCategory(EventInfoClass.CHARITYDONATION);
        }
    }

    @Override
    protected String buildQIF() {
        return isCredit()
                         ? buildCreditEvent()
                         : buildDebitEvent();
    }

    /**
     * Build interest debit event.
     * @return the QIF string
     */
    private String buildDebitEvent() {
        /* Access the transaction */
        Transaction myTrans = getTransaction();
        JMoney myAmount = myTrans.getAmount();
        JMoney myTaxCredit = myTrans.getTaxCredit();
        JMoney myDonation = myTrans.getCharityDonation();
        EventCategory myCategory = getAnalysis().getInterestCategory(myTrans);

        /* Determine type of event */
        boolean isTaxFree = myTaxCredit == null;
        boolean isDonation = myDonation != null;
        boolean isReinvested = Difference.isEqual(myTrans.getDebit(), myTrans.getCredit());

        /* Are we using splits */
        boolean useSplits = !isTaxFree
                            || isDonation;
        useSplits |= !isReinvested;

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEventLineType.DATE, myTrans.getDate());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        if (!isReinvested) {
            myValue.setZero();
        }
        addDecimalLine(QEventLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QEventLineType.CLEARED, myReconciled);

        /* If we have a reference */
        String myRef = myTrans.getReference();
        if (myRef != null) {
            /* Add the reference */
            addStringLine(QEventLineType.REFERENCE, myRef);
        }

        /* Payee is the parent of the account */
        AssetBase<?> myDebit = myTrans.getDebit();
        Payee myParent = (myDebit instanceof Deposit)
                                                     ? ((Deposit) myDebit).getParent()
                                                     : null;
        addAccountLine(QEventLineType.PAYEE, myParent);

        /* If we have a description */
        String myDesc = myTrans.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEventLineType.COMMENT, myDesc);
        }

        /* If we are not using splits */
        if (!useSplits) {
            /* Just add the standard category */
            addCategoryLine(QEventLineType.CATEGORY, myCategory);
        } else {
            /* Add a Split */
            myValue = new JDecimal(myAmount);
            if (!isTaxFree) {
                myValue.addValue(myTaxCredit);
            }
            if (isDonation) {
                myValue.addValue(myDonation);
            }
            addCategoryLine(QEventLineType.SPLITCATEGORY, myCategory);
            addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);
            if (!isTaxFree) {
                myValue = new JDecimal(myTaxCredit);
                myValue.negate();
                myCategory = getAnalysis().getCategory(EventInfoClass.TAXCREDIT);
                addCategoryLine(QEventLineType.SPLITCATEGORY, myCategory);
                addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);
            }
            if (isDonation) {
                myValue = new JDecimal(myDonation);
                myValue.negate();
                myCategory = getAnalysis().getCategory(EventInfoClass.CHARITYDONATION);
                addCategoryLine(QEventLineType.SPLITCATEGORY, myCategory);
                addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);
            }
            if (!isReinvested) {
                myValue = new JDecimal(myAmount);
                myValue.negate();
                addXferAccountLine(QEventLineType.SPLITCATEGORY, myTrans.getCredit());
                addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);
            }
        }

        /* Return the result */
        return completeItem();
    }

    /**
     * Build interest credit event.
     * @return the formatted QIF string
     */
    private String buildCreditEvent() {
        /* Access the transaction */
        Transaction myTrans = getTransaction();

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEventLineType.DATE, myTrans.getDate());

        /* Add the Amount (as a simple decimal) */
        addDecimalLine(QEventLineType.AMOUNT, new JDecimal(myTrans.getAmount()));

        /* Add the Cleared status */
        addStringLine(QEventLineType.CLEARED, myReconciled);

        /* If we have a reference */
        String myRef = myTrans.getReference();
        if (myRef != null) {
            /* Add the reference */
            addStringLine(QEventLineType.REFERENCE, myRef);
        }

        /* Payee is the debit account */
        addLineType(QEventLineType.PAYEE);
        append(QIF_XFER);
        if (!getQIFType().useSimpleTransfer()) {
            append(QIF_XFERFROM);
            addAccount(myTrans.getDebit());
        }
        endLine();

        /* If we have a description */
        String myDesc = myTrans.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEventLineType.COMMENT, myDesc);
        }

        /* Add the transfer details */
        addXferAccountLine(QEventLineType.CATEGORY, myTrans.getDebit());

        /* Return the result */
        return completeItem();
    }
}
