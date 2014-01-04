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

import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QEventLineType;

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
            getAnalysis().getCategory(EventInfoClass.TAXCREDIT);
        }
        if (pEvent.getNatInsurance() != null) {
            getAnalysis().getCategory(EventInfoClass.NATINSURANCE);
        }
        if (pEvent.getDeemedBenefit() != null) {
            getAnalysis().getCategory(EventInfoClass.DEEMEDBENEFIT);
        }
    }

    @Override
    protected String buildQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JMoney myAmount = myEvent.getAmount();
        JMoney myTaxCredit = myEvent.getTaxCredit();
        JMoney myNatIns = myEvent.getNatInsurance();
        JMoney myBenefit = myEvent.getDeemedBenefit();
        EventCategory myCategory = myEvent.getCategory();
        boolean isTaxCredit = myTaxCredit != null;
        boolean isNatIns = myNatIns != null;
        boolean isBenefit = myBenefit != null;

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QEventLineType.DATE, myEvent.getDate());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QEventLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QEventLineType.CLEARED, myReconciled);

        /* If we have a reference */
        String myRef = myEvent.getReference();
        if (myRef != null) {
            /* Add the reference */
            addStringLine(QEventLineType.REFERENCE, myRef);
        }

        /* Payee is the debit account */
        addAccountLine(QEventLineType.PAYEE, myEvent.getDebit());

        /* If we have a description */
        String myDesc = myEvent.getComments();
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
                myCategory = getAnalysis().getCategory(EventInfoClass.TAXCREDIT);
                addCategoryLine(QEventLineType.SPLITCATEGORY, myCategory);
                addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);
            }
            if (isNatIns) {
                myValue = new JDecimal(myNatIns);
                myValue.negate();
                myCategory = getAnalysis().getCategory(EventInfoClass.NATINSURANCE);
                addCategoryLine(QEventLineType.SPLITCATEGORY, myCategory);
                addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);
            }
            if (isBenefit) {
                myValue = new JDecimal(myBenefit);
                myValue.negate();
                myCategory = getAnalysis().getCategory(EventInfoClass.DEEMEDBENEFIT);
                addCategoryLine(QEventLineType.SPLITCATEGORY, myCategory);
                addDecimalLine(QEventLineType.SPLITAMOUNT, myValue);
            }
        }

        /* Return the result */
        return completeItem();
    }
}
