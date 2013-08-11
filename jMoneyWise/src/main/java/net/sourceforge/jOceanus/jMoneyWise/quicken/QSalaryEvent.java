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
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoClass;

/**
 * Quicken Salary Event.
 */
public class QSalaryEvent
        extends QEvent {
    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pEvent the event
     * @param pCredit is this the credit item?
     */
    protected QSalaryEvent(final QAnalysis pAnalysis,
                           final Event pEvent,
                           final boolean pCredit) {
        /* Call super constructor */
        super(pAnalysis, pEvent, pCredit);

        /* Make sure that the additional categories are registered */
        if (pEvent.getTaxCredit() != null) {
            getAnalysis().getCategory(EventInfoClass.TaxCredit);
        }
        if (pEvent.getNatInsurance() != null) {
            getAnalysis().getCategory(EventInfoClass.NatInsurance);
        }
        if (pEvent.getBenefit() != null) {
            getAnalysis().getCategory(EventInfoClass.Benefit);
        }
    }

    @Override
    protected String buildQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JMoney myAmount = myEvent.getAmount();
        JMoney myTaxCredit = myEvent.getTaxCredit();
        JMoney myNatIns = myEvent.getNatInsurance();
        JMoney myBenefit = myEvent.getBenefit();
        EventCategory myCategory = myEvent.getCategory();
        boolean isTaxCredit = (myTaxCredit != null);
        boolean isNatIns = (myNatIns != null);
        boolean isBenefit = (myBenefit != null);

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEvtLineType.Date, myEvent.getDate());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QEvtLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, myReconciled);

        /* If we have a reference */
        String myRef = myEvent.getReference();
        if (myRef != null) {
            /* Add the reference */
            addStringLine(QEvtLineType.Reference, myRef);
        }

        /* Payee is the debit account */
        addAccountLine(QEvtLineType.Payee, myEvent.getDebit());

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QEvtLineType.Comment, myDesc);
        }

        /* If no split is necessary */
        if ((!isTaxCredit)
            && (!isNatIns)
            && (!isBenefit)) {
            /* Add the standard category */
            addCategoryLine(QEvtLineType.Category, myCategory);
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
            addCategoryLine(QEvtLineType.SplitCategory, myCategory);
            addDecimalLine(QEvtLineType.SplitAmount, myValue);
            if (isTaxCredit) {
                myValue = new JDecimal(myTaxCredit);
                myValue.negate();
                myCategory = getAnalysis().getCategory(EventInfoClass.TaxCredit);
                addCategoryLine(QEvtLineType.SplitCategory, myCategory);
                addDecimalLine(QEvtLineType.SplitAmount, myValue);
            }
            if (isNatIns) {
                myValue = new JDecimal(myNatIns);
                myValue.negate();
                myCategory = getAnalysis().getCategory(EventInfoClass.NatInsurance);
                addCategoryLine(QEvtLineType.SplitCategory, myCategory);
                addDecimalLine(QEvtLineType.SplitAmount, myValue);
            }
            if (isBenefit) {
                myValue = new JDecimal(myBenefit);
                myValue.negate();
                myCategory = getAnalysis().getCategory(EventInfoClass.Benefit);
                addCategoryLine(QEvtLineType.SplitCategory, myCategory);
                addDecimalLine(QEvtLineType.SplitAmount, myValue);
            }
        }

        /* Return the result */
        return completeItem();
    }
}
