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
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JDilution;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JUnits;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice.AccountPriceList;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QActionType;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFType;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QPortfolioLineType;

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
                if ((!myEvent.getDebit().isTaxFree())) {
                    return true;
                }
                if (Difference.isEqual(myEvent.getDebit(), myEvent.getCredit())) {
                    return false;
                }
                return !((myType.canXferPortfolioLinked() || myType.canXferPortfolioDirect()));
            case StockTakeOver:
                if (myEvent.getThirdParty() == null) {
                    return false;
                }
                return !myType.canXferPortfolioDirect();
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
        addDateLine(QPortfolioLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortfolioLineType.Action, (useBuyX)
                ? QActionType.BuyX
                : QActionType.Buy);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.Security, mySecurity);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QPortfolioLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.Cleared, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        JDecimal myUnitValue = new JDecimal(myUnits);
        addDecimalLine(QPortfolioLineType.Quantity, myUnitValue);

        /* Add the price */
        AccountPriceList myPrices = getAnalysis().getDataSet().getPrices();
        AccountPrice myPrice = myPrices.getLatestPrice(mySecurity, myDate);
        JDecimal myPriceValue = new JDecimal(myPrice.getPrice());
        addDecimalLine(QPortfolioLineType.Price, myPriceValue);

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.Comment, myDesc);
        }

        /* If we are using BuyX */
        if (useBuyX) {
            /* Add Transfer Category */
            addCategoryLine(QPortfolioLineType.TransferAccount, myEvent.getCategory());

            /* Add Transfer Amount */
            addDecimalLine(QPortfolioLineType.TransferAmount, myValue);
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
        Account myDebit = myEvent.getDebit();
        Account mySecurity = myEvent.getCredit();
        boolean autoCorrectZeroUnits = false;
        JUnits myUnits = myEvent.getCreditUnits();
        if (myUnits == null) {
            myUnits = new JUnits();
            autoCorrectZeroUnits = !getQIFType().canInvestCapital();
        }
        if (mySecurity.getAlias() != null) {
            mySecurity = mySecurity.getAlias();
        }

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Determine additional features */
        boolean useBuyX4Event = ((myDebit.hasValue()) && (getQIFType().canXferPortfolioLinked()));

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortfolioLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortfolioLineType.Action, (useBuyX4Event)
                ? QActionType.BuyX
                : QActionType.Buy);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.Security, mySecurity);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QPortfolioLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.Cleared, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        JDecimal myUnitValue = (autoCorrectZeroUnits)
                ? new JDecimal(1)
                : new JDecimal(myUnits);
        addDecimalLine(QPortfolioLineType.Quantity, myUnitValue);

        /* Add the price */
        AccountPriceList myPrices = getAnalysis().getDataSet().getPrices();
        AccountPrice myPrice = myPrices.getLatestPrice(mySecurity, myDate);
        JDecimal myPriceValue = new JDecimal(myPrice.getPrice());
        addDecimalLine(QPortfolioLineType.Price, myPriceValue);

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.Comment, myDesc);
        }

        /* If we are using BuyX */
        if (useBuyX4Event) {
            /* Add the account */
            addXferAccountLine(QPortfolioLineType.TransferAccount, myDebit);

            /* Add the value */
            myValue = new JDecimal(myAmount);
            addDecimalLine(QPortfolioLineType.TransferAmount, myValue);
        }

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortfolioLineType.Date, myDate);

            /* Add the action */
            addEnumLine(QPortfolioLineType.Action, QActionType.ShrsOut);

            /* Add the Security */
            addAccountLine(QPortfolioLineType.Security, mySecurity);

            /* Add the Cleared status */
            addStringLine(QPortfolioLineType.Cleared, myReconciled);

            /* Add the quantity */
            addDecimalLine(QPortfolioLineType.Quantity, myUnitValue);

            /* If we have a description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortfolioLineType.Comment, myDesc);
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
        Account myCredit = myEvent.getCredit();
        JUnits myUnits = myEvent.getDebitUnits();
        boolean autoCorrectZeroUnits = false;
        boolean zeroUnits = false;
        if (myUnits == null) {
            autoCorrectZeroUnits = !getQIFType().canReturnCapital();
            zeroUnits = !autoCorrectZeroUnits;
        }

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Determine additional flags */
        boolean useSellX4Event = ((myCredit.hasValue()) && (getQIFType().canXferPortfolioLinked()));

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortfolioLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortfolioLineType.Action, (zeroUnits)
                ? ((useSellX4Event)
                        ? QActionType.RtrnCapX
                        : QActionType.RtrnCap)
                : ((useSellX4Event)
                        ? QActionType.SellX
                        : QActionType.Sell));

        /* Add the Security */
        addAccountLine(QPortfolioLineType.Security, mySecurity);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QPortfolioLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.Cleared, myReconciled);

        /* If we have units */
        if (!zeroUnits) {
            /* Add the Quantity (as a simple decimal) */
            JDecimal myUnitValue = (autoCorrectZeroUnits)
                    ? new JDecimal(1)
                    : new JDecimal(myUnits);
            addDecimalLine(QPortfolioLineType.Quantity, myUnitValue);
        }

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.Comment, myDesc);
        }

        /* If we are using SellX/RtrnCapX */
        if (useSellX4Event) {
            /* Add the account */
            addXferAccountLine(QPortfolioLineType.TransferAccount, myCredit);

            /* Add the value */
            myValue = new JDecimal(myAmount);
            addDecimalLine(QPortfolioLineType.TransferAmount, myValue);
        }

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortfolioLineType.Date, myDate);

            /* Add the action */
            addEnumLine(QPortfolioLineType.Action, QActionType.ShrsIn);

            /* Add the Security */
            addAccountLine(QPortfolioLineType.Security, mySecurity);

            /* Add the Cleared status */
            addStringLine(QPortfolioLineType.Cleared, myReconciled);

            /* Add the quantity */
            addDecimalLine(QPortfolioLineType.Quantity, new JDecimal(1));

            /* If we have a description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortfolioLineType.Comment, myDesc);
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
        addDateLine(QPortfolioLineType.Date, myEvent.getDate());

        /* Add the action */
        if (useSplits) {
            addEnumLine(QPortfolioLineType.Action, QActionType.StkSplit);
        } else if (myUnits != null) {
            addEnumLine(QPortfolioLineType.Action, QActionType.ShrsIn);
        } else {
            addEnumLine(QPortfolioLineType.Action, QActionType.ShrsOut);
            myUnits = myEvent.getDebitUnits();
        }

        /* Add the Security */
        addAccountLine(QPortfolioLineType.Security, myEvent.getDebit());

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.Cleared, myReconciled);

        /* Add quantity */
        if (useSplits) {
            /* Access dilution, invert it and increase factor */
            JDecimal myValue = myDilution.getInverseRatio();
            myValue.multiply(JDecimal.RADIX_TEN);
            addDecimalLine(QPortfolioLineType.Quantity, myValue);
        } else {
            addDecimalLine(QPortfolioLineType.Quantity, myUnits);
        }

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.Comment, myDesc);
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
        addDateLine(QPortfolioLineType.Date, myEvent.getDate());

        /* Add the action */
        addEnumLine(QPortfolioLineType.Action, (isDebit)
                ? QActionType.ShrsOut
                : QActionType.ShrsIn);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.Security, myEvent.getDebit());

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.Cleared, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        JDecimal myValue = new JDecimal(myUnits);
        addDecimalLine(QPortfolioLineType.Quantity, myValue);

        /* If we have a description */
        String myDesc = myEvent.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.Comment, myDesc);
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
        Account myCredit = myEvent.getCredit();
        JMoney myAmount = myEvent.getAmount();
        JMoney myTaxCredit = myEvent.getTaxCredit();
        String myDesc = myEvent.getComments();
        QIFType myQIFType = getQIFType();
        boolean isReinvested = Difference.isEqual(mySecurity, myCredit);

        /* Check for auto-correction of zero units */
        boolean autoCorrectZeroUnits = false;
        JUnits myUnits = myEvent.getCreditUnits();
        if (myUnits == null) {
            myUnits = new JUnits();
            autoCorrectZeroUnits = !myQIFType.canInvestCapital();
        }

        /* Determine additional features */
        boolean useMiscIncTax = ((isReinvested) && (myTaxCredit != null));
        boolean isTaxFreeDiv = ((!isReinvested) && (myTaxCredit == null));
        boolean useDivX4Event = ((isTaxFreeDiv) && (myQIFType.canXferPortfolioLinked()));
        boolean useXOut4Event = ((isTaxFreeDiv)
                                 && (!useDivX4Event) && (myQIFType.canXferPortfolioDirect()));

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortfolioLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortfolioLineType.Action, (isReinvested)
                ? QActionType.ReinvDiv
                : (useDivX4Event)
                        ? QActionType.DivX
                        : QActionType.Div);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.Security, mySecurity);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        if (myTaxCredit != null) {
            myValue.addValue(myTaxCredit);
        }
        addDecimalLine(QPortfolioLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.Cleared, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        JDecimal myUnitValue = (autoCorrectZeroUnits)
                ? new JDecimal(1)
                : new JDecimal(myUnits);
        addDecimalLine(QPortfolioLineType.Quantity, myUnitValue);

        /* If we have a description */
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.Comment, myDesc);
        }

        /* If we are using DivX */
        if (useDivX4Event) {
            /* Add the account */
            addXferAccountLine(QPortfolioLineType.TransferAccount, myCredit);

            /* Add the value */
            myValue = new JDecimal(myAmount);
            addDecimalLine(QPortfolioLineType.TransferAmount, myValue);
        }

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortfolioLineType.Date, myDate);

            /* Add the action */
            addEnumLine(QPortfolioLineType.Action, QActionType.ShrsOut);

            /* Add the Security */
            addAccountLine(QPortfolioLineType.Security, mySecurity);

            /* Add the Cleared status */
            addStringLine(QPortfolioLineType.Cleared, myReconciled);

            /* Add the quantity */
            addDecimalLine(QPortfolioLineType.Quantity, myUnitValue);

            /* If we have a description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortfolioLineType.Comment, myDesc);
            }
        }

        /* If we need to use a miscellaneous income for the tax credit */
        if (useMiscIncTax) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortfolioLineType.Date, myDate);

            /* Add the action */
            addEnumLine(QPortfolioLineType.Action, QActionType.MiscInc);

            /* Add the Security */
            addAccountLine(QPortfolioLineType.Security, mySecurity);

            /* Add the Amount (as a simple decimal) */
            myValue = new JDecimal(myTaxCredit);
            addDecimalLine(QPortfolioLineType.Amount, myValue);

            /* Add the Cleared status */
            addStringLine(QPortfolioLineType.Cleared, myReconciled);

            /* Add description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortfolioLineType.Comment, myDesc);
            }
        }

        /* If should use XOut for the tax free dividend */
        if (useXOut4Event) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortfolioLineType.Date, myDate);

            /* Add the action */
            addEnumLine(QPortfolioLineType.Action, QActionType.XOut);

            /* Add the Amount (as a simple decimal) */
            myValue = new JDecimal(myAmount);
            addDecimalLine(QPortfolioLineType.Amount, myValue);

            /* Add the Cleared status */
            addStringLine(QPortfolioLineType.Cleared, myReconciled);

            /* Add the transfer line */
            addXferAccountLine(QPortfolioLineType.TransferAccount, myCredit);

            /* Add description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortfolioLineType.Comment, myDesc);
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

        /* Access SecurityBucket for Debit */
        SecurityBucket myBucket = getSecurityBucket(myDebit);

        /* Obtain the delta cost */
        JMoney myDeltaCost = myBucket.getMoneyDeltaForEvent(myEvent, SecurityAttribute.COST);
        myDeltaCost = new JMoney(myDeltaCost);
        myDeltaCost.negate();

        /* Add the Date */
        addDateLine(QPortfolioLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortfolioLineType.Action, (!zeroUnits)
                ? QActionType.Sell
                : QActionType.RtrnCap);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.Security, myDebit);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myDeltaCost);
        addDecimalLine(QPortfolioLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.Cleared, myReconciled);

        /* If we have units */
        if (!zeroUnits) {
            /* Add the Quantity (as a simple decimal) */
            JDecimal myUnitValue = (autoCorrectZeroUnits)
                    ? new JDecimal(1)
                    : new JDecimal(myDebitUnits);
            addDecimalLine(QPortfolioLineType.Quantity, myUnitValue);
        }

        /* If we have a description */
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.Comment, myDesc);
        }

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortfolioLineType.Date, myDate);

            /* Add the action */
            addEnumLine(QPortfolioLineType.Action, QActionType.ShrsIn);

            /* Add the Security */
            addAccountLine(QPortfolioLineType.Security, myDebit);

            /* Add the Cleared status */
            addStringLine(QPortfolioLineType.Cleared, myReconciled);

            /* Add the quantity */
            addDecimalLine(QPortfolioLineType.Quantity, new JDecimal(1));

            /* If we have a description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortfolioLineType.Comment, myDesc);
            }
        }

        /* End the extra item */
        endItem();

        /* Add the Date */
        addDateLine(QPortfolioLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortfolioLineType.Action, QActionType.Buy);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.Security, myEvent.getCredit());

        /* Add the Amount (as a simple decimal) */
        addDecimalLine(QPortfolioLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.Cleared, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        myValue = new JDecimal(myCreditUnits);
        addDecimalLine(QPortfolioLineType.Quantity, myValue);

        /* If we have a description */
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.Comment, myDesc);
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
        addDateLine(QPortfolioLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortfolioLineType.Action, QActionType.Sell);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.Security, myDebit);

        /* Access Investment Analysis for Debit */
        SecurityBucket myDebitBucket = getSecurityBucket(myDebit);
        SecurityBucket myCreditBucket = getSecurityBucket(myCredit);

        /* Obtain total payment value for sale stock */
        JMoney myStockValue = myCreditBucket.getMoneyDeltaForEvent(myEvent, SecurityAttribute.COST);
        JMoney mySaleValue = new JMoney(myStockValue);
        mySaleValue.addAmount(myAmount);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(mySaleValue);
        addDecimalLine(QPortfolioLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.Cleared, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        myValue = new JDecimal(myDebitBucket.getUnitsDeltaForEvent(myEvent, SecurityAttribute.UNITS));
        myValue.negate();
        addDecimalLine(QPortfolioLineType.Quantity, myValue);

        /* If we have a description */
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.Comment, myDesc);
        }

        /* End the initial item */
        endItem();

        /* If we have a ThirdParty cash component that we can use XOut on */
        if ((myThirdParty != null)
            && (getQIFType().canXferPortfolioDirect())) {
            /* Add the Date */
            addDateLine(QPortfolioLineType.Date, myDate);

            /* Add the action */
            addEnumLine(QPortfolioLineType.Action, QActionType.XOut);

            /* Add the Amount (as a simple decimal) */
            myValue = new JDecimal(myAmount);
            addDecimalLine(QPortfolioLineType.Amount, myValue);

            /* Add the Cleared status */
            addStringLine(QPortfolioLineType.Cleared, myReconciled);

            /* Add the Transfer Account */
            addXferAccountLine(QPortfolioLineType.TransferAccount, myThirdParty);

            /* Add the Transfer Amount */
            addDecimalLine(QPortfolioLineType.TransferAmount, myValue);

            /* If we have a description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortfolioLineType.Comment, myDesc);
            }

            /* End the initial item */
            endItem();
        }

        /* Add the Date */
        addDateLine(QPortfolioLineType.Date, myDate);

        /* Add the action */
        addEnumLine(QPortfolioLineType.Action, QActionType.Buy);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.Security, myCredit);

        /* Add the Amount (as a simple decimal) */
        myValue = new JDecimal(myStockValue);
        addDecimalLine(QPortfolioLineType.Amount, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.Cleared, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        myValue = new JDecimal(myCreditUnits);
        addDecimalLine(QPortfolioLineType.Quantity, myValue);

        /* If we have a description */
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.Comment, myDesc);
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
}
