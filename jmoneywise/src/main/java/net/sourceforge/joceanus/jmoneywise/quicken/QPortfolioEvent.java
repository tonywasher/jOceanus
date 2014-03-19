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
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.newanalysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.newanalysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QActionType;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFType;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QPortfolioLineType;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * Quicken Portfolio Event Representation.
 */
public class QPortfolioEvent
        extends QEvent {

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pTrans the transaction
     * @param pCredit is this the credit item?
     */
    protected QPortfolioEvent(final QAnalysis pAnalysis,
                              final Transaction pTrans,
                              final boolean pCredit) {
        /* Call super-constructor */
        super(pAnalysis, pTrans, pCredit);
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

        /* Access the transaction */
        Transaction myTrans = getTransaction();
        AssetBase<?> myDebit = myTrans.getDebit();

        /* Switch on transaction type */
        switch (myTrans.getCategoryClass()) {
            case OTHERINCOME:
            case INHERITED:
                return true;
            case DIVIDEND:
                if ((!(myDebit instanceof Security))
                    || (!myTrans.getPortfolio().isTaxFree())) {
                    return true;
                }
                if (Difference.isEqual(myDebit, myTrans.getCredit())) {
                    return false;
                }
                return !(myType.canXferPortfolioLinked() || myType.canXferPortfolioDirect());
            case STOCKTAKEOVER:
                if (myTrans.getThirdParty() == null) {
                    return false;
                }
                return !myType.canXferPortfolioDirect();
            default:
                return false;
        }
    }

    @Override
    protected String buildQIF() {
        /* Access the transaction */
        Transaction myTrans = getTransaction();
        boolean isCredit = isCredit();

        /* Switch on transaction type */
        switch (myTrans.getCategoryClass()) {
            case OTHERINCOME:
            case INHERITED:
                return buildIncomeQIF();
            case TRANSFER:
                return (isCredit)
                                 ? buildXferInQIF()
                                 : buildXferOutQIF();
            case STOCKRIGHTSWAIVED:
                return buildXferOutQIF();
            case STOCKRIGHTSTAKEN:
                return buildXferInQIF();
            case STOCKSPLIT:
                return buildStockSplitQIF();
            case STOCKADJUST:
                return buildStockAdjustQIF();
            case DIVIDEND:
                return buildDividendQIF();
            case STOCKDEMERGER:
                return buildDeMergerQIF();
            case STOCKTAKEOVER:
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
        /* Access the transaction */
        Transaction myTrans = getTransaction();
        JDateDay myDate = myTrans.getDate();
        JMoney myAmount = myTrans.getAmount();
        AssetBase<?> mySecurity = myTrans.getCredit();
        JUnits myUnits = myTrans.getCreditUnits();
        boolean useBuyX = !getQIFType().useInvestmentHolding4Category();

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortfolioLineType.DATE, myDate);

        /* Add the action */
        addActionLine(QPortfolioLineType.ACTION, (useBuyX)
                                                          ? QActionType.BUYX
                                                          : QActionType.BUY);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.SECURITY, mySecurity);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QPortfolioLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.CLEARED, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        JDecimal myUnitValue = new JDecimal(myUnits);
        addDecimalLine(QPortfolioLineType.QUANTITY, myUnitValue);

        /* Add the price */
        SecurityPriceList myPrices = getAnalysis().getDataSet().getSecurityPrices();
        SecurityPrice myPrice = myPrices.getLatestPrice(mySecurity, myDate);
        JDecimal myPriceValue = new JDecimal(myPrice.getPrice());
        addDecimalLine(QPortfolioLineType.PRICE, myPriceValue);

        /* If we have a description */
        String myDesc = myTrans.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.COMMENT, myDesc);
        }

        /* If we are using BuyX */
        if (useBuyX) {
            /* Add Transfer Category */
            addCategoryLine(QPortfolioLineType.XFERACCOUNT, myTrans.getCategory());

            /* Add Transfer Amount */
            addDecimalLine(QPortfolioLineType.XFERAMOUNT, myValue);
        }

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build Transfer In transaction.
     * @return the QIF entry
     */
    private String buildXferInQIF() {
        /* Access the transaction */
        Transaction myTrans = getTransaction();
        JDateDay myDate = myTrans.getDate();
        JMoney myAmount = myTrans.getAmount();
        AssetBase<?> myDebit = myTrans.getDebit();
        AssetBase<?> mySecurity = myTrans.getCredit();
        boolean autoCorrectZeroUnits = false;
        JUnits myUnits = myTrans.getCreditUnits();
        if (myUnits == null) {
            myUnits = new JUnits();
            autoCorrectZeroUnits = !getQIFType().canInvestCapital();
        }

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Determine additional features */
        boolean useBuyX4Event = (!(myDebit instanceof Payee))
                                && getQIFType().canXferPortfolioLinked();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortfolioLineType.DATE, myDate);

        /* Add the action */
        addActionLine(QPortfolioLineType.ACTION, (useBuyX4Event)
                                                                ? QActionType.BUYX
                                                                : QActionType.BUY);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.SECURITY, mySecurity);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QPortfolioLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.CLEARED, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        JDecimal myUnitValue = (autoCorrectZeroUnits)
                                                     ? new JDecimal(1)
                                                     : new JDecimal(myUnits);
        addDecimalLine(QPortfolioLineType.QUANTITY, myUnitValue);

        /* Add the price */
        SecurityPriceList myPrices = getAnalysis().getDataSet().getSecurityPrices();
        SecurityPrice myPrice = myPrices.getLatestPrice(mySecurity, myDate);
        JDecimal myPriceValue = new JDecimal(myPrice.getPrice());
        addDecimalLine(QPortfolioLineType.PRICE, myPriceValue);

        /* If we have a description */
        String myDesc = myTrans.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.COMMENT, myDesc);
        }

        /* If we are using BuyX */
        if (useBuyX4Event) {
            /* Add the account */
            addXferAccountLine(QPortfolioLineType.XFERACCOUNT, myDebit);

            /* Add the value */
            myValue = new JDecimal(myAmount);
            addDecimalLine(QPortfolioLineType.XFERAMOUNT, myValue);
        }

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortfolioLineType.DATE, myDate);

            /* Add the action */
            addActionLine(QPortfolioLineType.ACTION, QActionType.SHRSOUT);

            /* Add the Security */
            addAccountLine(QPortfolioLineType.SECURITY, mySecurity);

            /* Add the Cleared status */
            addStringLine(QPortfolioLineType.CLEARED, myReconciled);

            /* Add the quantity */
            addDecimalLine(QPortfolioLineType.QUANTITY, myUnitValue);

            /* If we have a description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortfolioLineType.COMMENT, myDesc);
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
        /* Access the transaction */
        Transaction myTrans = getTransaction();
        JDateDay myDate = myTrans.getDate();
        JMoney myAmount = myTrans.getAmount();
        AssetBase<?> mySecurity = myTrans.getDebit();
        AssetBase<?> myCredit = myTrans.getCredit();
        JUnits myUnits = myTrans.getDebitUnits();
        boolean autoCorrectZeroUnits = false;
        boolean zeroUnits = false;
        if (myUnits == null) {
            autoCorrectZeroUnits = !getQIFType().canReturnCapital();
            zeroUnits = !autoCorrectZeroUnits;
        }

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Determine additional flags */
        boolean useSellX4Event = (!(myCredit instanceof Payee))
                                 && getQIFType().canXferPortfolioLinked();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortfolioLineType.DATE, myDate);

        /* Add the action */
        addActionLine(QPortfolioLineType.ACTION, zeroUnits
                                                          ? useSellX4Event
                                                                          ? QActionType.RTRNCAPX
                                                                          : QActionType.RTRNCAP
                                                          : useSellX4Event
                                                                          ? QActionType.SELLX
                                                                          : QActionType.SELL);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.SECURITY, mySecurity);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        addDecimalLine(QPortfolioLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.CLEARED, myReconciled);

        /* If we have units */
        if (!zeroUnits) {
            /* Add the Quantity (as a simple decimal) */
            JDecimal myUnitValue = (autoCorrectZeroUnits)
                                                         ? new JDecimal(1)
                                                         : new JDecimal(myUnits);
            addDecimalLine(QPortfolioLineType.QUANTITY, myUnitValue);
        }

        /* If we have a description */
        String myDesc = myTrans.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.COMMENT, myDesc);
        }

        /* If we are using SellX/RtrnCapX */
        if (useSellX4Event) {
            /* Add the account */
            addXferAccountLine(QPortfolioLineType.XFERACCOUNT, myCredit);

            /* Add the value */
            myValue = new JDecimal(myAmount);
            addDecimalLine(QPortfolioLineType.XFERAMOUNT, myValue);
        }

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortfolioLineType.DATE, myDate);

            /* Add the action */
            addActionLine(QPortfolioLineType.ACTION, QActionType.SHRSIN);

            /* Add the Security */
            addAccountLine(QPortfolioLineType.SECURITY, mySecurity);

            /* Add the Cleared status */
            addStringLine(QPortfolioLineType.CLEARED, myReconciled);

            /* Add the quantity */
            addDecimalLine(QPortfolioLineType.QUANTITY, new JDecimal(1));

            /* If we have a description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortfolioLineType.COMMENT, myDesc);
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
        /* Access the transaction */
        Transaction myTrans = getTransaction();
        JDilution myDilution = myTrans.getDilution();
        JUnits myUnits = myTrans.getCreditUnits();
        boolean useSplits = getQIFType().useStockSplit();

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortfolioLineType.DATE, myTrans.getDate());

        /* Add the action */
        if (useSplits) {
            addActionLine(QPortfolioLineType.ACTION, QActionType.STKSPLIT);
        } else if (myUnits != null) {
            addActionLine(QPortfolioLineType.ACTION, QActionType.SHRSIN);
        } else {
            addActionLine(QPortfolioLineType.ACTION, QActionType.SHRSOUT);
            myUnits = myTrans.getDebitUnits();
        }

        /* Add the Security */
        addAccountLine(QPortfolioLineType.SECURITY, myTrans.getDebit());

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.CLEARED, myReconciled);

        /* Add quantity */
        if (useSplits) {
            /* Access dilution, invert it and increase factor */
            JDecimal myValue = myDilution.getInverseRatio();
            myValue.multiply(JDecimal.RADIX_TEN);
            addDecimalLine(QPortfolioLineType.QUANTITY, myValue);
        } else {
            addDecimalLine(QPortfolioLineType.QUANTITY, myUnits);
        }

        /* If we have a description */
        String myDesc = myTrans.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.COMMENT, myDesc);
        }

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build StockAdjust transaction.
     * @return the QIF entry
     */
    private String buildStockAdjustQIF() {
        /* Access the transaction */
        Transaction myTrans = getTransaction();
        boolean isDebit = true;
        JUnits myUnits = myTrans.getDebitUnits();
        if (myUnits == null) {
            isDebit = false;
            myUnits = myTrans.getCreditUnits();
        }

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortfolioLineType.DATE, myTrans.getDate());

        /* Add the action */
        addActionLine(QPortfolioLineType.ACTION, (isDebit)
                                                          ? QActionType.SHRSOUT
                                                          : QActionType.SHRSIN);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.SECURITY, myTrans.getDebit());

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.CLEARED, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        JDecimal myValue = new JDecimal(myUnits);
        addDecimalLine(QPortfolioLineType.QUANTITY, myValue);

        /* If we have a description */
        String myDesc = myTrans.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.COMMENT, myDesc);
        }

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build Dividend transaction.
     * @return the QIF entry
     */
    private String buildDividendQIF() {
        /* Access the transaction */
        Transaction myTrans = getTransaction();
        JDateDay myDate = myTrans.getDate();
        AssetBase<?> mySecurity = myTrans.getDebit();
        AssetBase<?> myCredit = myTrans.getCredit();
        JMoney myAmount = myTrans.getAmount();
        JMoney myTaxCredit = myTrans.getTaxCredit();
        String myDesc = myTrans.getComments();
        QIFType myQIFType = getQIFType();
        boolean isReinvested = Difference.isEqual(mySecurity, myCredit);

        /* Check for auto-correction of zero units */
        boolean autoCorrectZeroUnits = false;
        JUnits myUnits = myTrans.getCreditUnits();
        if (myUnits == null) {
            myUnits = new JUnits();
            autoCorrectZeroUnits = !myQIFType.canInvestCapital();
        }

        /* Determine additional features */
        boolean useMiscIncTax = isReinvested
                                && (myTaxCredit != null);
        boolean isTaxFreeDiv = !isReinvested
                               && (myTaxCredit == null);
        boolean useDivX4Event = isTaxFreeDiv
                                && myQIFType.canXferPortfolioLinked();
        boolean useXOut4Event = isTaxFreeDiv
                                && !useDivX4Event
                                && myQIFType.canXferPortfolioDirect();

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortfolioLineType.DATE, myDate);

        /* Add the action */
        addActionLine(QPortfolioLineType.ACTION, (isReinvested)
                                                               ? QActionType.REINVDIV
                                                               : (useDivX4Event)
                                                                                ? QActionType.DIVX
                                                                                : QActionType.DIV);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.SECURITY, mySecurity);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myAmount);
        if (myTaxCredit != null) {
            myValue.addValue(myTaxCredit);
        }
        addDecimalLine(QPortfolioLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.CLEARED, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        JDecimal myUnitValue = (autoCorrectZeroUnits)
                                                     ? new JDecimal(1)
                                                     : new JDecimal(myUnits);
        addDecimalLine(QPortfolioLineType.QUANTITY, myUnitValue);

        /* If we have a description */
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.COMMENT, myDesc);
        }

        /* If we are using DivX */
        if (useDivX4Event) {
            /* Add the account */
            addXferAccountLine(QPortfolioLineType.XFERACCOUNT, myCredit);

            /* Add the value */
            myValue = new JDecimal(myAmount);
            addDecimalLine(QPortfolioLineType.XFERAMOUNT, myValue);
        }

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortfolioLineType.DATE, myDate);

            /* Add the action */
            addActionLine(QPortfolioLineType.ACTION, QActionType.SHRSOUT);

            /* Add the Security */
            addAccountLine(QPortfolioLineType.SECURITY, mySecurity);

            /* Add the Cleared status */
            addStringLine(QPortfolioLineType.CLEARED, myReconciled);

            /* Add the quantity */
            addDecimalLine(QPortfolioLineType.QUANTITY, myUnitValue);

            /* If we have a description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortfolioLineType.COMMENT, myDesc);
            }
        }

        /* If we need to use a miscellaneous income for the tax credit */
        if (useMiscIncTax) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortfolioLineType.DATE, myDate);

            /* Add the action */
            addActionLine(QPortfolioLineType.ACTION, QActionType.MISCINC);

            /* Add the Security */
            addAccountLine(QPortfolioLineType.SECURITY, mySecurity);

            /* Add the Amount (as a simple decimal) */
            myValue = new JDecimal(myTaxCredit);
            addDecimalLine(QPortfolioLineType.AMOUNT, myValue);

            /* Add the Cleared status */
            addStringLine(QPortfolioLineType.CLEARED, myReconciled);

            /* Add description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortfolioLineType.COMMENT, myDesc);
            }
        }

        /* If should use XOut for the tax free dividend */
        if (useXOut4Event) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortfolioLineType.DATE, myDate);

            /* Add the action */
            addActionLine(QPortfolioLineType.ACTION, QActionType.XOUT);

            /* Add the Amount (as a simple decimal) */
            myValue = new JDecimal(myAmount);
            addDecimalLine(QPortfolioLineType.AMOUNT, myValue);

            /* Add the Cleared status */
            addStringLine(QPortfolioLineType.CLEARED, myReconciled);

            /* Add the transfer line */
            addXferAccountLine(QPortfolioLineType.XFERACCOUNT, myCredit);

            /* Add description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortfolioLineType.COMMENT, myDesc);
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
        /* Access the transaction */
        Transaction myTrans = getTransaction();
        JDateDay myDate = myTrans.getDate();
        AssetBase<?> myDebit = myTrans.getDebit();
        JUnits myDebitUnits = myTrans.getDebitUnits();
        JUnits myCreditUnits = myTrans.getCreditUnits();
        String myDesc = myTrans.getComments();
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
        JMoney myDeltaCost = myBucket.getMoneyDeltaForTransaction(myTrans, SecurityAttribute.COST);
        myDeltaCost = new JMoney(myDeltaCost);
        myDeltaCost.negate();

        /* Add the Date */
        addDateLine(QPortfolioLineType.DATE, myDate);

        /* Add the action */
        addActionLine(QPortfolioLineType.ACTION, (!zeroUnits)
                                                             ? QActionType.SELL
                                                             : QActionType.RTRNCAP);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.SECURITY, myDebit);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(myDeltaCost);
        addDecimalLine(QPortfolioLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.CLEARED, myReconciled);

        /* If we have units */
        if (!zeroUnits) {
            /* Add the Quantity (as a simple decimal) */
            JDecimal myUnitValue = (autoCorrectZeroUnits)
                                                         ? new JDecimal(1)
                                                         : new JDecimal(myDebitUnits);
            addDecimalLine(QPortfolioLineType.QUANTITY, myUnitValue);
        }

        /* If we have a description */
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.COMMENT, myDesc);
        }

        /* If we need to autoCorrect */
        if (autoCorrectZeroUnits) {
            /* End the main item */
            endItem();

            /* Add the Date */
            addDateLine(QPortfolioLineType.DATE, myDate);

            /* Add the action */
            addActionLine(QPortfolioLineType.ACTION, QActionType.SHRSIN);

            /* Add the Security */
            addAccountLine(QPortfolioLineType.SECURITY, myDebit);

            /* Add the Cleared status */
            addStringLine(QPortfolioLineType.CLEARED, myReconciled);

            /* Add the quantity */
            addDecimalLine(QPortfolioLineType.QUANTITY, new JDecimal(1));

            /* If we have a description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortfolioLineType.COMMENT, myDesc);
            }
        }

        /* End the extra item */
        endItem();

        /* Add the Date */
        addDateLine(QPortfolioLineType.DATE, myDate);

        /* Add the action */
        addActionLine(QPortfolioLineType.ACTION, QActionType.BUY);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.SECURITY, myTrans.getCredit());

        /* Add the Amount (as a simple decimal) */
        addDecimalLine(QPortfolioLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.CLEARED, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        myValue = new JDecimal(myCreditUnits);
        addDecimalLine(QPortfolioLineType.QUANTITY, myValue);

        /* If we have a description */
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.COMMENT, myDesc);
        }

        /* Return the detail */
        return completeItem();
    }

    /**
     * Build TakeOver transaction.
     * @return the QIF entry
     */
    private String buildTakeOverQIF() {
        /* Access the transaction */
        Transaction myTrans = getTransaction();
        JDateDay myDate = myTrans.getDate();
        JMoney myAmount = myTrans.getAmount();
        AssetBase<?> myDebit = myTrans.getDebit();
        AssetBase<?> myCredit = myTrans.getCredit();
        String myDesc = myTrans.getComments();
        Deposit myThirdParty = myTrans.getThirdParty();
        JUnits myCreditUnits = myTrans.getCreditUnits();

        /* Determine reconciled flag */
        String myReconciled = getReconciledFlag();

        /* Reset the builder */
        reset();

        /* Add the Date */
        addDateLine(QPortfolioLineType.DATE, myDate);

        /* Add the action */
        addActionLine(QPortfolioLineType.ACTION, QActionType.SELL);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.SECURITY, myDebit);

        /* Access Investment Analysis for Debit */
        SecurityBucket myDebitBucket = getSecurityBucket(myDebit);
        SecurityBucket myCreditBucket = getSecurityBucket(myCredit);

        /* Obtain total payment value for sale stock */
        JMoney myStockValue = myCreditBucket.getMoneyDeltaForTransaction(myTrans, SecurityAttribute.COST);
        JMoney mySaleValue = new JMoney(myStockValue);
        mySaleValue.addAmount(myAmount);

        /* Add the Amount (as a simple decimal) */
        JDecimal myValue = new JDecimal(mySaleValue);
        addDecimalLine(QPortfolioLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.CLEARED, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        myValue = new JDecimal(myDebitBucket.getUnitsDeltaForTransaction(myTrans, SecurityAttribute.UNITS));
        myValue.negate();
        addDecimalLine(QPortfolioLineType.QUANTITY, myValue);

        /* If we have a description */
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.COMMENT, myDesc);
        }

        /* End the initial item */
        endItem();

        /* If we have a ThirdParty cash component that we can use XOut on */
        if ((myThirdParty != null)
            && (getQIFType().canXferPortfolioDirect())) {
            /* Add the Date */
            addDateLine(QPortfolioLineType.DATE, myDate);

            /* Add the action */
            addActionLine(QPortfolioLineType.ACTION, QActionType.XOUT);

            /* Add the Amount (as a simple decimal) */
            myValue = new JDecimal(myAmount);
            addDecimalLine(QPortfolioLineType.AMOUNT, myValue);

            /* Add the Cleared status */
            addStringLine(QPortfolioLineType.CLEARED, myReconciled);

            /* Add the Transfer Account */
            addXferAccountLine(QPortfolioLineType.XFERACCOUNT, myThirdParty);

            /* Add the Transfer Amount */
            addDecimalLine(QPortfolioLineType.XFERAMOUNT, myValue);

            /* If we have a description */
            if (myDesc != null) {
                /* Add the Description */
                addStringLine(QPortfolioLineType.COMMENT, myDesc);
            }

            /* End the initial item */
            endItem();
        }

        /* Add the Date */
        addDateLine(QPortfolioLineType.DATE, myDate);

        /* Add the action */
        addActionLine(QPortfolioLineType.ACTION, QActionType.BUY);

        /* Add the Security */
        addAccountLine(QPortfolioLineType.SECURITY, myCredit);

        /* Add the Amount (as a simple decimal) */
        myValue = new JDecimal(myStockValue);
        addDecimalLine(QPortfolioLineType.AMOUNT, myValue);

        /* Add the Cleared status */
        addStringLine(QPortfolioLineType.CLEARED, myReconciled);

        /* Add the Quantity (as a simple decimal) */
        myValue = new JDecimal(myCreditUnits);
        addDecimalLine(QPortfolioLineType.QUANTITY, myValue);

        /* If we have a description */
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QPortfolioLineType.COMMENT, myDesc);
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

        @Override
        protected void registerTransaction(final Transaction pTrans,
                                           final boolean isCredit) {
            /* Allocate the event */
            QPortfolioEvent myEvent = new QPortfolioEvent(getAnalysis(), pTrans, isCredit);
            addEvent(myEvent);

            /* If we need a holding event */
            if (myEvent.needsHoldingEvent()) {
                /* Access holding account */
                QAccount myHolding = getAnalysis().getAccount(getAccount().getHolding(), null);

                /* Allocate the transaction */
                myHolding.processHoldingTransaction(pTrans, isCredit);
            }
        }
    }
}
