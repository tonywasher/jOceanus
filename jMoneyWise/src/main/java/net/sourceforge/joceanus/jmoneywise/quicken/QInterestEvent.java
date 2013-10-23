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

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoClass;
import net.sourceforge.jOceanus.jMoneyWise.quicken.definitions.QEventLineType;

/**
 * Quicken Interest Event.
 */
public class QInterestEvent
        extends QEvent {
    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pEvent the event
     * @param pCredit is this the credit item?
     */
    protected QInterestEvent(final QAnalysis pAnalysis,
                             final Event pEvent,
                             final boolean pCredit) {
        /* Call super constructor */
        super(pAnalysis, pEvent, pCredit);

        /* Make sure that the additional categories are registered */
        getAnalysis().getInterestCategory(pEvent);
        if (pEvent.getTaxCredit() != null) {
            getAnalysis().getCategory(EventInfoClass.TaxCredit);
        }
        if (pEvent.getCharityDonation() != null) {
            getAnalysis().getCategory(EventInfoClass.CharityDonation);
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
        /* Access the event */
        Event myEvent = getEvent();
        JMoney myAmount = myEvent.getAmount();
        JMoney myTaxCredit = myEvent.getTaxCredit();
        JMoney myDonation = myEvent.getCharityDonation();
        EventCategory myCategory = getAnalysis().getInterestCategory(myEvent);

        /* Determine type of event */
        boolean isTaxFree = (myTaxCredit == null);
        boolean isDonation = (myDonation != null);
        boolean isReinvested = Difference.isEqual(myEvent.getDebit(), myEvent.getCredit());

        /* Are we using splits */
        boolean useSplits = ((!isTaxFree) || (isDonation));
        useSplits |= (!isReinvested);

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEventLineType.Date, myEvent.getDate());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        if (!isReinvested) {
            myValue.setZero();
        }
        addDecimalLine(QEventLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEventLineType.Cleared, myReconciled);

        /* If we have a reference */
        String myRef = myEvent.getReference();
        if (myRef != null) {
            /* Add the reference */
            addStringLine(QEventLineType.Reference, myRef);
        }

        /* Payee is the parent of the account */
        Account myParent = myEvent.getDebit().getParent();
        addAccountLine(QEventLineType.Payee, myParent);

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEventLineType.Comment, myDesc);
        }

        /* If we are not using splits */
        if (!useSplits) {
            /* Just add the standard category */
            addCategoryLine(QEventLineType.Category, myCategory);
        } else {
            /* Add a Split */
            myValue = new JDecimal(myAmount);
            if (!isTaxFree) {
                myValue.addValue(myTaxCredit);
            }
            if (isDonation) {
                myValue.addValue(myDonation);
            }
            addCategoryLine(QEventLineType.SplitCategory, myCategory);
            addDecimalLine(QEventLineType.SplitAmount, myValue);
            if (!isTaxFree) {
                myValue = new JDecimal(myTaxCredit);
                myValue.negate();
                myCategory = getAnalysis().getCategory(EventInfoClass.TaxCredit);
                addCategoryLine(QEventLineType.SplitCategory, myCategory);
                addDecimalLine(QEventLineType.SplitAmount, myValue);
            }
            if (isDonation) {
                myValue = new JDecimal(myDonation);
                myValue.negate();
                myCategory = getAnalysis().getCategory(EventInfoClass.CharityDonation);
                addCategoryLine(QEventLineType.SplitCategory, myCategory);
                addDecimalLine(QEventLineType.SplitAmount, myValue);
            }
            if (!isReinvested) {
                myValue = new JDecimal(myAmount);
                myValue.negate();
                addXferAccountLine(QEventLineType.SplitCategory, myEvent.getCredit());
                addDecimalLine(QEventLineType.SplitAmount, myValue);
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
        /* Access the event */
        Event myEvent = getEvent();

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEventLineType.Date, myEvent.getDate());

        /* Add the Amount (as a simple decimal) */
        addDecimalLine(QEventLineType.Amount, new JDecimal(myEvent.getAmount()));

        /* Add the Cleared status */
        addStringLine(QEventLineType.Cleared, myReconciled);

        /* If we have a reference */
        String myRef = myEvent.getReference();
        if (myRef != null) {
            /* Add the reference */
            addStringLine(QEventLineType.Reference, myRef);
        }

        /* Payee is the debit account */
        addLineType(QEventLineType.Payee);
        append(QIF_XFER);
        if (!getQIFType().useSimpleTransfer()) {
            append(QIF_XFERFROM);
            addAccount(myEvent.getDebit());
        }
        endLine();

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEventLineType.Comment, myDesc);
        }

        /* Add the transfer details */
        addXferAccountLine(QEventLineType.Category, myEvent.getDebit());

        /* Return the result */
        return completeItem();
    }
}