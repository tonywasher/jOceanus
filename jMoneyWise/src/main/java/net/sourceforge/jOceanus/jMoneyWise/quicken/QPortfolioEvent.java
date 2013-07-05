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
public class QPortfolioEvent
        extends QEvent {

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pEvent the event
     * @param pCredit is this the credit item?
     */
    protected QPortfolioEvent(final QAnalysis pAnalysis,
                              final Event pEvent,
                              final boolean pCredit) {
        /* Call super-constructor */
        super(pAnalysis, pEvent, pCredit);
    }

    /**
     * Does this event require a holding event?
     * @return true/false
     */
    protected boolean needsHoldingEvent() {
        /* Access the event */
        Event myEvent = getEvent();

        /* Switch on transaction type */
        switch (myEvent.getCategoryClass()) {
            case Inherited:
                return true;
            default:
                return false;
        }
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
        addEnumLine(QPortLineType.Action, QActionType.Buy);

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myEvent.getCredit());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, (myEvent.isReconciled() == Boolean.TRUE)
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
        if (myUnits == null) {
            myUnits = new JUnits();
        }

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myEvent.getDate());

        /* Add the action */
        addEnumLine(QPortLineType.Action, QActionType.Buy);

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myEvent.getCredit());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, (myEvent.isReconciled() == Boolean.TRUE)
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
        addEnumLine(QPortLineType.Action, (hasUnits)
                ? QActionType.Sell
                : QActionType.RtrnCap);

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myEvent.getDebit());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        myValue.negate();
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, (myEvent.isReconciled() == Boolean.TRUE)
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
        addStringLine(QEvtLineType.Cleared, (myEvent.isReconciled() == Boolean.TRUE)
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
        boolean isDebit = true;
        JUnits myUnits = myEvent.getDebitUnits();
        if (myUnits == null) {
            isDebit = false;
            myUnits = myEvent.getCreditUnits();
        }

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myEvent.getDate());

        /* Add the action */
        addEnumLine(QPortLineType.Action, (isDebit)
                ? QActionType.ShrsOut
                : QActionType.ShrsIn);

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myEvent.getDebit());

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, (myEvent.isReconciled() == Boolean.TRUE)
                ? QIF_RECONCILED
                : QIF_OPEN);

        /* Add the Quantity (as a simple decimal) */
        JDecimal myValue = new JDecimal(myUnits);
        if (isDebit) {
            myValue.negate();
        }
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
        addEnumLine(QPortLineType.Action, (isReinvested)
                ? QActionType.ReinvDiv
                : QActionType.Div);

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myEvent.getDebit());

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, (myEvent.isReconciled() == Boolean.TRUE)
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
            /* Allocate the event */
            QPortfolioEvent myEvent = new QPortfolioEvent(getAnalysis(), pEvent, isCredit);
            addEvent(myEvent);

            /* If we need a holding event */
            if (myEvent.needsHoldingEvent()) {
                /* Access holding account */
                QAccount myHolding = getAnalysis().getAccount(getAccount().getHolding());

                /* Allocate the event */
                myHolding.processHoldingEvent(pEvent, isCredit);
            }
        }
    }

    /**
     * Quicken Action Types.
     */
    public enum QActionType {
        /**
         * Buy.
         */
        Buy,

        /**
         * Sell.
         */
        Sell,

        /**
         * SharesIn.
         */
        ShrsIn,

        /**
         * SharesOut.
         */
        ShrsOut,

        /**
         * Dividend.
         */
        Div,

        /**
         * Reinvested Dividend.
         */
        ReinvDiv,

        /**
         * Return of Capital.
         */
        RtrnCap;
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
