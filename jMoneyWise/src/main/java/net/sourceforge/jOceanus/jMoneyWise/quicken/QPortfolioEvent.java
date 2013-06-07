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
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;

/**
 * Quicken Portfolio Event Representation.
 */
public final class QPortfolioEvent
        extends QEvent {

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pEvent the event
     * @param pCredit is this the credit item?
     */
    private QPortfolioEvent(final QAnalysis pAnalysis,
                            final Event pEvent,
                            final boolean pCredit) {
        /* Call super-constructor */
        super(pAnalysis, pEvent, pCredit);
    }

    @Override
    protected String buildQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        boolean isCredit = isCredit();

        /* Switch on transaction type */
        switch (myEvent.getCategoryClass()) {
            case Inherited:
                return buildInheritedQIF();
            case Transfer:
                return (isCredit)
                        ? buildXferInQIF()
                        : buildXferOutQIF();
            case StockRightsWaived:
                return buildXferOutQIF();
            case StockRightsTaken:
                return buildXferInQIF();
            case StockSplit:
                return buildStockSplitQIF();
            case StockAdjust:
                return buildStockAdjustQIF();
            case Dividend:
                return buildDividendQIF();
            default:
                break;
        }

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build inherited transaction.
     * @return the QIF entry
     */
    private String buildInheritedQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JMoney myAmount = myEvent.getAmount();
        JUnits myUnits = myEvent.getCreditUnits();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myEvent.getDate());

        /* Add the action */
        addStringLine(QPortLineType.Action, "BuyX"); // TODO

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myEvent.getCredit());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, (myEvent.getReconciled() == Boolean.TRUE)
                ? QIF_RECONCILED
                : QIF_OPEN);

        /* Add the Quantity (as a simple decimal) */
        JDecimal myUnitValue = new JDecimal(myUnits);
        addDecimalLine(QPortLineType.Quantity, myUnitValue);

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortLineType.Comment, myDesc);
        }

        /* Add the Payee */
        addAccountLine(QPortLineType.Payee, myEvent.getDebit());

        /* Add the category */
        addCategoryLine(QPortLineType.TransferAccount, myEvent.getCategory());

        /* Add the Amount again */
        addDecimalLine(QPortLineType.TransferAmount, myValue);

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build Transfer In transaction.
     * @return the QIF entry
     */
    private String buildXferInQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JMoney myAmount = myEvent.getAmount();
        JUnits myUnits = myEvent.getCreditUnits();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myEvent.getDate());

        /* Add the action */
        addStringLine(QPortLineType.Action, "BuyX"); // TODO

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myEvent.getCredit());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, (myEvent.getReconciled() == Boolean.TRUE)
                ? QIF_RECONCILED
                : QIF_OPEN);

        /* Add the Quantity (as a simple decimal) */
        JDecimal myUnitValue = new JDecimal(myUnits);
        addDecimalLine(QPortLineType.Quantity, myUnitValue);

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortLineType.Comment, myDesc);
        }

        /* Add the category */
        addXferAccountLine(QPortLineType.TransferAccount, myEvent.getDebit());

        /* Add the Amount again */
        addDecimalLine(QPortLineType.TransferAmount, myValue);

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build Transfer In transaction.
     * @return the QIF entry
     */
    private String buildXferOutQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JMoney myAmount = myEvent.getAmount();
        JUnits myUnits = myEvent.getDebitUnits();
        boolean hasUnits = (myUnits != null);

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myEvent.getDate());

        /* Add the action */
        addStringLine(QPortLineType.Action, (hasUnits)
                ? "SellX"
                : "RtrnCapX"); // TODO

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myEvent.getDebit());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        myValue.negate();
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, (myEvent.getReconciled() == Boolean.TRUE)
                ? QIF_RECONCILED
                : QIF_OPEN);

        /* If we have units */
        if (hasUnits) {
            /* Add the Quantity (as a simple decimal) */
            JDecimal myUnitValue = new JDecimal(myUnits);
            myUnitValue.negate();
            addDecimalLine(QPortLineType.Quantity, myUnitValue);
        }

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortLineType.Comment, myDesc);
        }

        /* Add the category */
        addXferAccountLine(QPortLineType.TransferAccount, myEvent.getDebit());

        /* Add the Value again */
        addDecimalLine(QPortLineType.TransferAmount, myValue);

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build StockSplit transaction.
     * @return the QIF entry
     */
    private String buildStockSplitQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JUnits myUnits = myEvent.getCreditUnits();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myEvent.getDate());

        /* Add the action */
        addStringLine(QPortLineType.Action, "ShrsIn"); // TODO

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myEvent.getDebit());

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, (myEvent.getReconciled() == Boolean.TRUE)
                ? QIF_RECONCILED
                : QIF_OPEN);

        /* Add the Quantity (as a simple decimal) */
        JDecimal myValue = new JDecimal(myUnits);
        addDecimalLine(QPortLineType.Quantity, myValue);

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortLineType.Comment, myDesc);
        }

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build StockAdjust transaction.
     * @return the QIF entry
     */
    private String buildStockAdjustQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JUnits myUnits = myEvent.getDebitUnits();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myEvent.getDate());

        /* Add the action */
        addStringLine(QPortLineType.Action, "ShrsIn"); // TODO

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myEvent.getDebit());

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, (myEvent.getReconciled() == Boolean.TRUE)
                ? QIF_RECONCILED
                : QIF_OPEN);

        /* Add the Quantity (as a simple decimal) */
        JDecimal myValue = new JDecimal(myUnits);
        myValue.negate();
        addDecimalLine(QPortLineType.Quantity, myValue);

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortLineType.Comment, myDesc);
        }

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build Dividend transaction.
     * @return the QIF entry
     */
    private String buildDividendQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JMoney myAmount = myEvent.getAmount();
        JUnits myUnits = myEvent.getCreditUnits();
        boolean hasUnits = (myUnits != null);
        boolean isReinvested = Difference.isEqual(myEvent.getDebit(), myEvent.getCredit());

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myEvent.getDate());

        /* Add the action */
        addStringLine(QPortLineType.Action, (isReinvested)
                ? "ReinvDiv"
                : "DivX"); // TODO

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myEvent.getDebit());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, (myEvent.getReconciled() == Boolean.TRUE)
                ? QIF_RECONCILED
                : QIF_OPEN);

        /* If we have units */
        if (hasUnits) {
            /* Add the Quantity (as a simple decimal) */
            myValue = new JDecimal(myUnits);
            addDecimalLine(QPortLineType.Quantity, myValue);
        }

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortLineType.Comment, myDesc);
        }

        /* If this is not reinvested */
        if (!isReinvested) {
            /* Add the category */
            addXferAccountLine(QPortLineType.TransferAccount, myEvent.getCredit());

            /* Add the Amount again */
            addDecimalLine(QPortLineType.TransferAmount, myValue);
        }

        /* Return the detail */
        return completeItem();
    }

    /**
     * Event List class.
     */
    protected static class QPortfolioEventList
            extends QEventBaseList<QPortfolioEvent> {
        /**
         * Constructor.
         * @param pAnalysis the analysis
         * @param pAccount the list owner
         */
        protected QPortfolioEventList(final QAnalysis pAnalysis,
                                      final QAccount pAccount) {
            /* Call super constructor */
            super(pAnalysis, pAccount);
        }

        /**
         * Register event.
         * @param pEvent the event
         * @param isCredit is this the credit item?
         */
        protected void registerEvent(final Event pEvent,
                                     final boolean isCredit) {
            QPortfolioEvent myEvent = new QPortfolioEvent(getAnalysis(), pEvent, isCredit);
            addEvent(myEvent);
        }
    }

    /**
     * Quicken Portfolio Event Line Types.
     */
    public enum QPortLineType implements QLineType {
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