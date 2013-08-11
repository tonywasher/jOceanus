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
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JDilution;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice.AccountPriceList;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.views.InvestmentAnalysis;
import net.sourceforge.jOceanus.jMoneyWise.views.InvestmentAnalysis.InvestmentAttribute;

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
        /* Handle case when there is no need for a holding account */
        QIFType myType = getQIFType();
        if (!myType.useInvestmentHolding4Category()) {
            return false;
        }

        /* Access the event */
        Event myEvent = getEvent();

        /* Switch on transaction type */
        switch (myEvent.getCategoryClass()) {
            case OtherIncome:
            case Inherited:
                return true;
            case Dividend:
                return (!myEvent.getDebit().isTaxFree());
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
            case OtherIncome:
            case Inherited:
                return buildIncomeQIF();
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
            case StockDeMerger:
                return buildDeMergerQIF();
            case StockTakeOver:
                return buildTakeOverQIF();
            default:
                break;
        }

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build income transaction.
     * @return the QIF entry
     */
    private String buildIncomeQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JDateDay myDate = myEvent.getDate();
        JMoney myAmount = myEvent.getAmount();
        Account mySecurity = myEvent.getCredit();
        JUnits myUnits = myEvent.getCreditUnits();
        boolean useBuyX = !getQIFType().useInvestmentHolding4Category();

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortLineType.Action, (useBuyX)
                ? QActionType.BuyX
                : QActionType.Buy);

        /* Add the Security */
        addAccountLine(QPortLineType.Security, mySecurity);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        JDecimal myUnitValue = new JDecimal(myUnits);
        addDecimalLine(QPortLineType.Quantity, myUnitValue);

        /* Add the price */
        AccountPriceList myPrices = getAnalysis().getDataSet().getPrices();
        AccountPrice myPrice = myPrices.getLatestPrice(mySecurity, myDate);
        JDecimal myPriceValue = new JDecimal(myPrice.getPrice());
        addDecimalLine(QPortLineType.Price, myPriceValue);

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortLineType.Comment, myDesc);
        }

        /* If we are using BuyX */
        if (useBuyX) {
            /* Add Transfer Category */
            addCategoryLine(QPortLineType.TransferAccount, myEvent.getCategory());

            /* Add Transfer Amount */
            addDecimalLine(QPortLineType.TransferAmount, myValue);
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
        JDateDay myDate = myEvent.getDate();
        JMoney myAmount = myEvent.getAmount();
        Account mySecurity = myEvent.getCredit();
        boolean autoCorrectZeroUnits = false;
        JUnits myUnits = myEvent.getCreditUnits();
        if (myUnits == null) {
            myUnits = new JUnits();
            autoCorrectZeroUnits = !getQIFType().canInvestCapital();
        }

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortLineType.Action, QActionType.Buy);

        /* Add the Security */
        addAccountLine(QPortLineType.Security, mySecurity);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        JDecimal myUnitValue = (autoCorrectZeroUnits)
                ? new JDecimal(1)
                : new JDecimal(myUnits);
        addDecimalLine(QPortLineType.Quantity, myUnitValue);

        /* Add the price */
        AccountPriceList myPrices = getAnalysis().getDataSet().getPrices();
        AccountPrice myPrice = myPrices.getLatestPrice(mySecurity, myDate);
        JDecimal myPriceValue = new JDecimal(myPrice.getPrice());
        addDecimalLine(QPortLineType.Price, myPriceValue);

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortLineType.Comment, myDesc);
        }

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortLineType.Date, myDate);

            /* Add the action */
            addEnumLine(QPortLineType.Action, QActionType.ShrsOut);

            /* Add the Security */
            addAccountLine(QPortLineType.Security, mySecurity);

            /* Add the Cleared status */
            addStringLine(QEvtLineType.Cleared, myReconciled);

            /* Add the quantity */
            addDecimalLine(QPortLineType.Quantity, myUnitValue);

            /* If we have a description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortLineType.Comment, myDesc);
            }
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
        JDateDay myDate = myEvent.getDate();
        JMoney myAmount = myEvent.getAmount();
        Account mySecurity = myEvent.getDebit();
        JUnits myUnits = myEvent.getDebitUnits();
        boolean autoCorrectZeroUnits = false;
        boolean zeroUnits = false;
        if (myUnits == null) {
            autoCorrectZeroUnits = !getQIFType().canReturnCapital();
            zeroUnits = !autoCorrectZeroUnits;
        }

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortLineType.Action, (!zeroUnits)
                ? QActionType.Sell
                : QActionType.RtrnCap);

        /* Add the Security */
        addAccountLine(QPortLineType.Security, mySecurity);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, myReconciled);

        /* If we have units */
        if (!zeroUnits) {
            /* Add the Quantity (as a simple decimal) */
            JDecimal myUnitValue = (autoCorrectZeroUnits)
                    ? new JDecimal(1)
                    : new JDecimal(myUnits);
            addDecimalLine(QPortLineType.Quantity, myUnitValue);
        }

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortLineType.Comment, myDesc);
        }

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortLineType.Date, myDate);

            /* Add the action */
            addEnumLine(QPortLineType.Action, QActionType.ShrsIn);

            /* Add the Security */
            addAccountLine(QPortLineType.Security, mySecurity);

            /* Add the Cleared status */
            addStringLine(QEvtLineType.Cleared, myReconciled);

            /* Add the quantity */
            addDecimalLine(QPortLineType.Quantity, new JDecimal(1));

            /* If we have a description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortLineType.Comment, myDesc);
            }
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
        JDilution myDilution = myEvent.getDilution();
        JUnits myUnits = myEvent.getCreditUnits();
        boolean useSplits = getQIFType().useStockSplit();

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myEvent.getDate());

        /* Add the action */
        if (useSplits) {
            addEnumLine(QPortLineType.Action, QActionType.StkSplit);
        } else if (myUnits != null) {
            addEnumLine(QPortLineType.Action, QActionType.ShrsIn);
        } else {
            addEnumLine(QPortLineType.Action, QActionType.ShrsOut);
            myUnits = myEvent.getDebitUnits();
        }

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myEvent.getDebit());

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, myReconciled);

        /* Add quantity */
        if (useSplits) {
            /* Access dilution, invert it and increase factor */
            JDecimal myValue = myDilution.getInverseRatio();
            myValue.multiply(JDecimal.RADIX_TEN);
            addDecimalLine(QPortLineType.Quantity, myValue);
        } else {
            addDecimalLine(QPortLineType.Quantity, myUnits);
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

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

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
        addStringLine(QEvtLineType.Cleared, myReconciled);

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
     * Build Dividend transaction.
     * @return the QIF entry
     */
    private String buildDividendQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JDateDay myDate = myEvent.getDate();
        Account mySecurity = myEvent.getDebit();
        JMoney myAmount = myEvent.getAmount();
        JMoney myTaxCredit = myEvent.getTaxCredit();
        JUnits myUnits = myEvent.getCreditUnits();
        String myDesc = myEvent.getComments();
        boolean hasUnits = (myUnits != null);
        boolean isReinvested = Difference.isEqual(mySecurity, myEvent.getCredit());

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortLineType.Action, (isReinvested)
                ? QActionType.ReinvDiv
                : QActionType.Div);

        /* Add the Security */
        addAccountLine(QPortLineType.Security, mySecurity);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        if (myTaxCredit != null) {
            myValue.addValue(myTaxCredit);
        }
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, myReconciled);

        /* If we have units */
        if (hasUnits) {
            /* Add the Quantity (as a simple decimal) */
            myValue = new JDecimal(myUnits);
            addDecimalLine(QPortLineType.Quantity, myValue);
        }

        /* If we have a description */
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortLineType.Comment, myDesc);
        }

        /* If we are re-investing and have a TaxCredit */
        if ((isReinvested)
            && (myTaxCredit != null)) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortLineType.Date, myDate);

            /* Add the action */
            addEnumLine(QPortLineType.Action, QActionType.MiscInc);

            /* Add the Security */
            addAccountLine(QPortLineType.Security, mySecurity);

            /* Add the Amount (as a simple decimal) */
            myValue = new JDecimal(myTaxCredit);
            addDecimalLine(QPortLineType.Amount, myValue);

            /* Add the Cleared status */
            addStringLine(QEvtLineType.Cleared, myReconciled);

            /* Add description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortLineType.Comment, myDesc);
            }
        }

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build DeMerger transaction.
     * @return the QIF entry
     */
    private String buildDeMergerQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JDateDay myDate = myEvent.getDate();
        Account myDebit = myEvent.getDebit();
        JUnits myDebitUnits = myEvent.getDebitUnits();
        JUnits myCreditUnits = myEvent.getCreditUnits();
        String myDesc = myEvent.getComments();
        boolean autoCorrectZeroUnits = false;
        boolean zeroUnits = false;
        if (myDebitUnits == null) {
            autoCorrectZeroUnits = !getQIFType().canReturnCapital();
            zeroUnits = !autoCorrectZeroUnits;
        }

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Access Investment Analysis for Debit */
        InvestmentAnalysis myAnalysis = getInvestmentAnalysis(myEvent, myDebit);

        /* Obtain the delta cost */
        JMoney myDeltaCost = myAnalysis.getMoneyAttribute(InvestmentAttribute.DeltaCost);
        myDeltaCost = new JMoney(myDeltaCost);
        myDeltaCost.negate();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortLineType.Action, (!zeroUnits)
                ? QActionType.Sell
                : QActionType.RtrnCap);

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myDebit);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myDeltaCost);
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, myReconciled);

        /* If we have units */
        if (!zeroUnits) {
            /* Add the Quantity (as a simple decimal) */
            JDecimal myUnitValue = (autoCorrectZeroUnits)
                    ? new JDecimal(1)
                    : new JDecimal(myDebitUnits);
            addDecimalLine(QPortLineType.Quantity, myUnitValue);
        }

        /* If we have a description */
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortLineType.Comment, myDesc);
        }

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortLineType.Date, myDate);

            /* Add the action */
            addEnumLine(QPortLineType.Action, QActionType.ShrsIn);

            /* Add the Security */
            addAccountLine(QPortLineType.Security, myDebit);

            /* Add the Cleared status */
            addStringLine(QEvtLineType.Cleared, myReconciled);

            /* Add the quantity */
            addDecimalLine(QPortLineType.Quantity, new JDecimal(1));

            /* If we have a description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortLineType.Comment, myDesc);
            }
        }

        /* End the extra item */
        endItem();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortLineType.Action, QActionType.Buy);

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myEvent.getCredit());

        /* Add the Amount (as a simple decimal) */
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        myValue = new JDecimal(myCreditUnits);
        addDecimalLine(QPortLineType.Quantity, myValue);

        /* If we have a description */
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortLineType.Comment, myDesc);
        }

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build TakeOver transaction.
     * @return the QIF entry
     */
    private String buildTakeOverQIF() {
        /* Access the event */
        Event myEvent = getEvent();
        JDateDay myDate = myEvent.getDate();
        JMoney myAmount = myEvent.getAmount();
        Account myDebit = myEvent.getDebit();
        Account myCredit = myEvent.getCredit();
        String myDesc = myEvent.getComments();
        Account myThirdParty = myEvent.getThirdParty();
        JUnits myCreditUnits = myEvent.getCreditUnits();

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortLineType.Action, QActionType.Sell);

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myDebit);

        /* Access Investment Analysis for Debit */
        InvestmentAnalysis myAnalysis = getInvestmentAnalysis(myEvent, myDebit);

        /* Obtain total payment value for sale stock */
        JMoney myStockValue = myAnalysis.getMoneyAttribute(InvestmentAttribute.TakeOverStockCost);
        JMoney mySaleValue = new JMoney(myStockValue);
        mySaleValue.addAmount(myAmount);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(mySaleValue);
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        myValue = new JDecimal(myAnalysis.getUnitsAttribute(InvestmentAttribute.InitialUnits));
        addDecimalLine(QPortLineType.Quantity, myValue);

        /* If we have a description */
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortLineType.Comment, myDesc);
        }

        /* End the initial item */
        endItem();

        /* If we have a ThirdParty cash component */
        if (myThirdParty != null) {
            /* Add the Date */
            addDateLine(QPortLineType.Date, myDate);

            /* Add the action */
            addEnumLine(QPortLineType.Action, QActionType.XOut);

            /* Add the Amount (as a simple decimal) */
            myValue = new JDecimal(myAmount);
            addDecimalLine(QPortLineType.Amount, myValue);

            /* Add the Cleared status */
            addStringLine(QEvtLineType.Cleared, myReconciled);

            /* Add the Transfer Account */
            addXferAccountLine(QPortLineType.TransferAccount, myThirdParty);

            /* Add the Transfer Amount */
            addDecimalLine(QPortLineType.TransferAmount, myValue);

            /* If we have a description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortLineType.Comment, myDesc);
            }

            /* End the initial item */
            endItem();
        }

        /* Add the Date */
        addDateLine(QPortLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortLineType.Action, QActionType.Buy);

        /* Add the Security */
        addAccountLine(QPortLineType.Security, myCredit);

        /* Add the Amount (as a simple decimal) */
        myValue = new JDecimal(myStockValue);
        addDecimalLine(QPortLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QEvtLineType.Cleared, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        myValue = new JDecimal(myCreditUnits);
        addDecimalLine(QPortLineType.Quantity, myValue);

        /* If we have a description */
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
         * BuyX.
         */
        BuyX,

        /**
         * Sell.
         */
        Sell,

        /**
         * SellX.
         */
        SellX,

        /**
         * StockSplit.
         */
        StkSplit,

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
         * DividendX.
         */
        DivX,

        /**
         * Reinvested Dividend.
         */
        ReinvDiv,

        /**
         * Return of Capital.
         */
        RtrnCap,

        /**
         * Return of CapitalX.
         */
        RtrnCapX,

        /**
         * Transfer In.
         */
        XIn,

        /**
         * Transfer Out.
         */
        XOut,

        /**
         * Miscellaneous Income.
         */
        MiscInc,

        /**
         * Miscellaneous IncomeX.
         */
        MiscIncX,

        /**
         * Miscellaneous Expense.
         */
        MiscExp,

        /**
         * Miscellaneous ExpenseX.
         */
        MiscExpX,

        /**
         * Cash/Miscellaneous Expense.
         */
        Cash,

        /**
         * Options Grant.
         */
        Grant,

        /**
         * Options Vest.
         */
        Vest,

        /**
         * Options Exercise.
         */
        Exercise,

        /**
         * Options ExerciseX.
         */
        ExerciseX,

        /**
         * Options Expire.
         */
        Expire;
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

        @Override
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
