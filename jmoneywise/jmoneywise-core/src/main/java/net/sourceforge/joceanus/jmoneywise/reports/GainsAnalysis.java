/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.reports;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Gains Analysis Report.
 */
public class GainsAnalysis {
    /**
     * The open bracket.
     */
    private static final String BRACKET_START = " (";

    /**
     * The close bracket.
     */
    private static final char BRACKET_CLOSE = ')';

    /**
     * The formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * The source SecurityBucket.
     */
    private final SecurityBucket theBucket;

    /**
     * The string builder.
     */
    private final StringBuilder theBuilder;

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param pSecurity the security bucket
     */
    public GainsAnalysis(final MetisDataFormatter pFormatter,
                         final SecurityBucket pSecurity) {
        theFormatter = pFormatter;
        theBucket = pSecurity;
        theBuilder = new StringBuilder();
    }

    /**
     * format the cost history.
     * @param pIterator the transaction iterator
     */
    public void formatGainsHistory(final Iterator<Transaction> pIterator) {
        /* Reset the builder */
        theBuilder.setLength(0);

        /* Loop through the transactions */
        while (pIterator.hasNext()) {
            Transaction myTrans = pIterator.next();

            /* If the transaction relates to the security */
            SecurityValues myValues = theBucket.getValuesForTransaction(myTrans);
            if (myValues != null) {
                /* Format the transaction */
                formatTransaction(myTrans, myValues);
            }
        }

        /* Output the details */
        System.out.println(theBuilder.toString());
    }

    /**
     * Format the details.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatTransaction(final Transaction pTrans,
                                   final SecurityValues pValues) {
        /* Switch on the class */
        switch (pTrans.getCategoryClass()) {
            case TRANSFER:
            case STOCKRIGHTSISSUE:
            case INHERITED:
                formatTransfer(pTrans, pValues);
                break;
            case SECURITYREPLACE:
            case STOCKTAKEOVER:
                formatStockTakeOver(pTrans, pValues);
                break;
            case STOCKDEMERGER:
                formatStockDeMerger(pTrans, pValues);
                break;
            case STOCKSPLIT:
            case UNITSADJUST:
                formatUnitsAdjust(pTrans, pValues);
                break;
            case DIVIDEND:
                formatDividend(pTrans, pValues);
                break;
            default:
                formatStandardTransaction(pTrans, pValues);
                break;
        }
    }

    /**
     * Format basic details of a transaction.
     * @param pTrans the transaction
     */
    private void formatBasicTransaction(final Transaction pTrans) {
        /* Format the transaction */
        theBuilder.append("\n--------\n");
        theBuilder.append(theFormatter.formatObject(pTrans.getDate()));
        theBuilder.append(": ");
        theBuilder.append(pTrans);
        formatNewLine();
    }

    /**
     * Format a Stock DeMerger.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatStandardTransaction(final Transaction pTrans,
                                           final SecurityValues pValues) {
        /* Format the basic transaction */
        formatBasicTransaction(pTrans);

        /* For each of the security attributes */
        for (SecurityAttribute myAttr : SecurityAttribute.values()) {
            /* If we are interested in this value */
            Object myValue = pValues.getValue(myAttr);
            if (showAttr(myAttr, myValue)) {
                /* Format the entry */
                theBuilder.append(myAttr);
                theBuilder.append('=');
                theBuilder.append(theFormatter.formatObject(myValue));
                formatNewLine();
            }
        }
    }

    /**
     * Are we interested in this attribute?
     * @param pAttribute the attribute
     * @param pValue the value
     * @return true/false
     */
    private boolean showAttr(final SecurityAttribute pAttr,
                             final Object pValue) {
        if (pValue == null) {
            return false;
        }
        if ((pValue instanceof TethysDecimal) &&
            ((TethysDecimal) pValue).isZero()) {
            return false;
        }
        switch (pAttr) {
            case REALISEDGAINS:
            case UNITS:
            case INVESTED:
            case RESIDUALCOST:
            case VALUATION:
            case PRICE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check whether this is a debit transaction for the security.
     * @param pTrans the transaction
     * @return true/false
     */
    private boolean isDebit(final Transaction pTrans) {
        TransactionAsset myDebit = pTrans.getDirection().isTo()
                                                                ? pTrans.getAccount()
                                                                : pTrans.getPartner();
        return myDebit.equals(theBucket.getSecurityHolding());
    }

    /**
     * Check whether this is a credit transaction for the security.
     * @param pTrans the transaction
     * @return true/false
     */
    private boolean isCredit(final Transaction pTrans) {
        TransactionAsset myCredit = pTrans.getDirection().isFrom()
                                                                   ? pTrans.getAccount()
                                                                   : pTrans.getPartner();
        return myCredit.equals(theBucket.getSecurityHolding());
    }

    /**
     * Format a value.
     * @param pAttr the attribute
     * @param pValue the value
     * @param pNewLine as newline (true/false)
     */
    private void formatValue(final SecurityAttribute pAttr,
                             final Object pValue,
                             final boolean pNewLine) {
        theBuilder.append(pAttr);
        theBuilder.append('=');
        theBuilder.append(theFormatter.formatObject(pValue));
        if (pNewLine) {
            formatNewLine();
        }
    }

    /**
     * Format a division.
     * @param pNumerator the numerator
     * @param pValue the divisor
     */
    private void formatDivision(final TethysDecimal pNumerator,
                                final TethysDecimal pDivisor) {
        formatCombination(pNumerator, pDivisor, '/');
    }

    /**
     * Format a valuation.
     * @param pUnits the units
     * @param pPrice the price
     * @param pXchangeRate the exchange rate
     */
    private void formatValuation(final TethysUnits pUnits,
                                 final TethysPrice pPrice,
                                 final TethysRatio pXchangeRate) {
        theBuilder.append(BRACKET_START);
        theBuilder.append(theFormatter.formatObject(pUnits));
        theBuilder.append('@');
        theBuilder.append(theFormatter.formatObject(pPrice));
        if (pXchangeRate != null) {
            theBuilder.append('/');
            theBuilder.append(theFormatter.formatObject(pXchangeRate));
        }
        theBuilder.append(BRACKET_CLOSE);
        formatNewLine();
    }

    /**
     * Format a multiplication.
     * @param pFirst the first item
     * @param pSecond the second item
     */
    private void formatMultiplication(final TethysDecimal pFirst,
                                      final TethysDecimal pSecond) {
        formatCombination(pFirst, pSecond, '*');
    }

    /**
     * Format an addition.
     * @param pFirst the first item
     * @param pSecond the second item
     */
    private void formatAddition(final TethysDecimal pFirst,
                                final TethysDecimal pSecond) {
        formatCombination(pFirst, pSecond, '+');
    }

    /**
     * Format a subtraction.
     * @param pFirst the first item
     * @param pSecond the second item
     */
    private void formatSubtraction(final TethysDecimal pFirst,
                                   final TethysDecimal pSecond) {
        formatCombination(pFirst, pSecond, '-');
    }

    /**
     * Format a combination.
     * @param pFirst the first item
     * @param pSecond the second item
     * @param pSymbol the symbol
     */
    private void formatCombination(final TethysDecimal pFirst,
                                   final TethysDecimal pSecond,
                                   final char pSymbol) {
        theBuilder.append(BRACKET_START);
        theBuilder.append(theFormatter.formatObject(pFirst));
        theBuilder.append(pSymbol);
        theBuilder.append(theFormatter.formatObject(pSecond));
        theBuilder.append(BRACKET_CLOSE);
        formatNewLine();
    }

    /**
     * Format a newLine.
     */
    private void formatNewLine() {
        theBuilder.append('\n');
    }

    /**
     * Format a Transfer.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatTransfer(final Transaction pTrans,
                                final SecurityValues pValues) {
        /* Format the basic transaction */
        formatBasicTransaction(pTrans);

        /* Split workings for transfer in/out */
        if (isDebit(pTrans)) {
            formatTransferOut(pTrans, pValues);
        } else {
            formatTransferIn(pTrans, pValues);
        }
    }

    /**
     * Format a Dividend.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatDividend(final Transaction pTrans,
                                final SecurityValues pValues) {
        /* If this is a dividend re-investment */
        if (isCredit(pTrans)) {
            /* Format the basic transaction */
            formatBasicTransaction(pTrans);

            /* Deal as investment */
            formatTransferIn(pTrans, pValues);
        }
    }

    /**
     * Format transfer money in.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatTransferIn(final Transaction pTrans,
                                  final SecurityValues pValues) {
        /* Access interesting values */
        TethysUnits myUnits = pValues.getUnitsValue(SecurityAttribute.UNITS);
        TethysUnits myDeltaUnits = pTrans.getCreditUnits();
        TethysMoney myCost = pValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);
        TethysMoney myAmount = theBucket.getMoneyDeltaForTransaction(pTrans, SecurityAttribute.RESIDUALCOST);
        TethysPrice myPrice = pValues.getPriceValue(SecurityAttribute.PRICE);
        TethysRatio myXchangeRate = pValues.getRatioValue(SecurityAttribute.EXCHANGERATE);

        /* Obtain the original units/cost */
        SecurityValues myPreviousValues = theBucket.getPreviousValuesForTransaction(pTrans);
        TethysUnits myOriginalUnits = myPreviousValues.getUnitsValue(SecurityAttribute.UNITS);
        TethysMoney myOriginalCost = myPreviousValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);

        /* If this is an inheritance */
        if (pTrans.isCategoryClass(TransactionCategoryClass.INHERITED)) {
            formatValue(SecurityAttribute.INVESTED, myAmount, false);
            formatValuation(myDeltaUnits, myPrice, myXchangeRate);
        } else {
            formatValue(SecurityAttribute.INVESTED, myAmount, true);
        }

        /* Record the details */
        if (myDeltaUnits != null) {
            formatValue(SecurityAttribute.UNITS, myUnits, false);
            formatAddition(myOriginalUnits, myDeltaUnits);
        }
        formatValue(SecurityAttribute.RESIDUALCOST, myCost, false);
        formatAddition(myOriginalCost, myAmount);
    }

    /**
     * Format transfer money out.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatTransferOut(final Transaction pTrans,
                                   final SecurityValues pValues) {
        /* Access interesting values */
        TethysMoney myGain = pValues.getMoneyValue(SecurityAttribute.CAPITALGAIN);
        TethysMoney myAllowedCost = pValues.getMoneyValue(SecurityAttribute.ALLOWEDCOST);
        TethysRatio myCostDilution = pValues.getRatioValue(SecurityAttribute.COSTDILUTION);
        TethysMoney myTotalGains = pValues.getMoneyValue(SecurityAttribute.REALISEDGAINS);
        TethysMoney myCost = pValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);
        TethysUnits myUnits = pValues.getUnitsValue(SecurityAttribute.UNITS);
        TethysMoney myCash = pValues.getMoneyValue(SecurityAttribute.RETURNEDCASH);
        TethysMoney myConsideration = pValues.getMoneyValue(SecurityAttribute.CONSIDERATION);

        /* Obtain the original values */
        SecurityValues myPreviousValues = theBucket.getPreviousValuesForTransaction(pTrans);
        TethysMoney myOriginalCost = myPreviousValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);
        TethysUnits myOriginalUnits = myPreviousValues.getUnitsValue(SecurityAttribute.UNITS);

        /* Obtain the delta in units/money */
        TethysUnits myDeltaUnits = theBucket.getUnitsDeltaForTransaction(pTrans, SecurityAttribute.UNITS);
        TethysMoney myAmount = new TethysMoney(myCash);
        myAmount.negate();

        /* If we have changed the number of units */
        if (myDeltaUnits.isNonZero()) {
            /* Obtain the various values */
            myDeltaUnits = new TethysUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Format the units */
            formatValue(SecurityAttribute.UNITS, myUnits, false);
            formatSubtraction(myOriginalUnits, myDeltaUnits);

            /* Format the dilution */
            formatValue(SecurityAttribute.COSTDILUTION, myCostDilution, false);
            formatDivision(myUnits, myOriginalUnits);

            /* Else we need to format the cost dilution */
        } else if (myConsideration != null) {
            /* Format the valuation */
            TethysMoney myValuation = pValues.getMoneyValue(SecurityAttribute.VALUATION);
            TethysPrice myPrice = pValues.getPriceValue(SecurityAttribute.PRICE);
            TethysRatio myXchangeRate = pValues.getRatioValue(SecurityAttribute.EXCHANGERATE);
            formatValue(SecurityAttribute.VALUATION, myValuation, false);
            formatValuation(myUnits, myPrice, myXchangeRate);
            formatValue(SecurityAttribute.CONSIDERATION, myConsideration, false);
            formatAddition(myCash, myValuation);

            /* Format the dilution */
            formatValue(SecurityAttribute.COSTDILUTION, myCostDilution, false);
            formatDivision(myValuation, myConsideration);
        }

        /* Record the details */
        if (myCostDilution != null) {
            formatValue(SecurityAttribute.RESIDUALCOST, myCost, false);
            formatMultiplication(myOriginalCost, myCostDilution);
            formatValue(SecurityAttribute.ALLOWEDCOST, myAllowedCost, false);
            formatSubtraction(myOriginalCost, myCost);
        } else {
            formatValue(SecurityAttribute.ALLOWEDCOST, myAllowedCost, true);
            formatValue(SecurityAttribute.RESIDUALCOST, myCost, false);
            formatSubtraction(myOriginalCost, myAllowedCost);
        }
        if (myDeltaUnits.isNonZero()) {
            formatValue(SecurityAttribute.UNITS, myUnits, false);
            formatSubtraction(myOriginalUnits, myDeltaUnits);
        }

        /* Record the gains allocation */
        if (myGain != null) {
            formatValue(SecurityAttribute.CAPITALGAIN, myGain, false);
            formatSubtraction(myCash, myAllowedCost);
            formatValue(SecurityAttribute.REALISEDGAINS, myTotalGains, true);
        }
    }

    /**
     * Format a Units Adjustment.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatUnitsAdjust(final Transaction pTrans,
                                   final SecurityValues pValues) {
        /* Format the basic transaction */
        formatBasicTransaction(pTrans);

        /* Split workings for adding/removing units */
        if (pTrans.getDebitUnits() != null) {
            formatRemoveUnits(pTrans, pValues);
        } else {
            formatAddUnits(pTrans, pValues);
        }
    }

    /**
     * Format remove units.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatRemoveUnits(final Transaction pTrans,
                                   final SecurityValues pValues) {
        /* Access interesting values */
        TethysUnits myUnits = pValues.getUnitsValue(SecurityAttribute.UNITS);
        TethysUnits myDeltaUnits = pTrans.getDebitUnits();

        /* Obtain the original units */
        SecurityValues myPreviousValues = theBucket.getPreviousValuesForTransaction(pTrans);
        TethysUnits myOriginalUnits = myPreviousValues.getUnitsValue(SecurityAttribute.UNITS);

        /* Record the details */
        formatValue(SecurityAttribute.UNITS, myUnits, false);
        formatSubtraction(myOriginalUnits, myDeltaUnits);
    }

    /**
     * Format add units.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatAddUnits(final Transaction pTrans,
                                final SecurityValues pValues) {
        /* Access interesting values */
        TethysUnits myUnits = pValues.getUnitsValue(SecurityAttribute.UNITS);
        TethysUnits myDeltaUnits = pTrans.getCreditUnits();

        /* Obtain the original units */
        SecurityValues myPreviousValues = theBucket.getPreviousValuesForTransaction(pTrans);
        TethysUnits myOriginalUnits = myPreviousValues.getUnitsValue(SecurityAttribute.UNITS);

        /* Record the details */
        formatValue(SecurityAttribute.UNITS, myUnits, false);
        formatAddition(myOriginalUnits, myDeltaUnits);
    }

    /**
     * Format a Stock DeMerger.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatStockDeMerger(final Transaction pTrans,
                                     final SecurityValues pValues) {
        /* Format the basic transaction */
        formatBasicTransaction(pTrans);

        /* Split workings for credit and debit */
        if (isDebit(pTrans)) {
            formatDebitStockDeMerger(pTrans, pValues);
        } else {
            formatCreditStockDeMerger(pTrans, pValues);
        }
    }

    /**
     * Format debit side of a Stock DeMerger.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatDebitStockDeMerger(final Transaction pTrans,
                                          final SecurityValues pValues) {
        /* Access interesting values */
        TethysRatio myCostDilution = pValues.getRatioValue(SecurityAttribute.COSTDILUTION);
        TethysMoney myResidualCost = pValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);
        TethysMoney myXferredCost = pValues.getMoneyValue(SecurityAttribute.XFERREDCOST);
        TethysUnits myDeltaUnits = theBucket.getUnitsDeltaForTransaction(pTrans, SecurityAttribute.UNITS);

        /* Obtain the original cost */
        SecurityValues myPreviousValues = theBucket.getPreviousValuesForTransaction(pTrans);
        TethysMoney myOriginalCost = myPreviousValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);

        /* Record the details */
        formatValue(SecurityAttribute.COSTDILUTION, myCostDilution, true);
        formatValue(SecurityAttribute.RESIDUALCOST, myResidualCost, false);
        formatMultiplication(myOriginalCost, myCostDilution);
        formatValue(SecurityAttribute.XFERREDCOST, myXferredCost, false);
        formatSubtraction(myOriginalCost, myResidualCost);

        /* If we have changed the number of units */
        if (myDeltaUnits.isNonZero()) {
            /* Obtain the various values */
            TethysUnits myOriginalUnits = myPreviousValues.getUnitsValue(SecurityAttribute.UNITS);
            TethysUnits myUnits = pValues.getUnitsValue(SecurityAttribute.UNITS);
            myDeltaUnits = new TethysUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Format the units */
            formatValue(SecurityAttribute.UNITS, myUnits, false);
            formatSubtraction(myOriginalUnits, myDeltaUnits);
        }
    }

    /**
     * Format credit side of a Stock DeMerger.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatCreditStockDeMerger(final Transaction pTrans,
                                           final SecurityValues pValues) {
        /* Access interesting values */
        TethysMoney myResidualCost = pValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);
        TethysMoney myXferredCost = pValues.getMoneyValue(SecurityAttribute.XFERREDCOST);
        TethysMoney myValueXfer = pValues.getMoneyValue(SecurityAttribute.XFERREDVALUE);
        TethysUnits myUnits = theBucket.getUnitsDeltaForTransaction(pTrans, SecurityAttribute.UNITS);
        TethysPrice myPrice = pValues.getPriceValue(SecurityAttribute.PRICE);
        TethysRatio myXchangeRate = pValues.getRatioValue(SecurityAttribute.EXCHANGERATE);

        /* Record the details */
        formatValue(SecurityAttribute.XFERREDVALUE, myValueXfer, false);
        formatValuation(myUnits, myPrice, myXchangeRate);
        formatValue(SecurityAttribute.XFERREDCOST, myXferredCost, true);
        formatValue(SecurityAttribute.RESIDUALCOST, myResidualCost, true);
    }

    /**
     * Format a Stock TakeOver.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatStockTakeOver(final Transaction pTrans,
                                     final SecurityValues pValues) {
        /* Format the basic transaction */
        formatBasicTransaction(pTrans);

        /* Split out Stock and Cash TakeOver */
        TethysMoney myCash = pValues.getMoneyValue(SecurityAttribute.RETURNEDCASH);
        if (myCash != null) {
            formatStockAndCashTakeOver(pTrans, pValues, myCash);
            return;
        }

        /* Split workings for credit and debit */
        if (isDebit(pTrans)) {
            /* Record the transfer of cost for simple replacement takeOver */
            TethysMoney myCostXfer = pValues.getMoneyValue(SecurityAttribute.XFERREDCOST);
            formatValue(SecurityAttribute.XFERREDCOST, myCostXfer, true);
        } else {
            formatCreditStockTakeOver(pTrans, pValues);
        }
    }

    /**
     * Format a StockAndCash TakeOver.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     * @param pCash the cash consideration
     */
    private void formatStockAndCashTakeOver(final Transaction pTrans,
                                            final SecurityValues pValues,
                                            final TethysMoney pCash) {
        /* Split workings for credit and debit */
        if (isDebit(pTrans)) {
            formatDebitStockAndCashTakeOver(pTrans, pValues, pCash);
        } else {
            formatCreditStockTakeOver(pTrans, pValues);
        }
    }

    /**
     * Format debit side of a StockAndCash TakeOver.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     * @param pCash the cash consideration
     */
    private void formatDebitStockAndCashTakeOver(final Transaction pTrans,
                                                 final SecurityValues pValues,
                                                 final TethysMoney pCash) {
        /* Access interesting values */
        TethysMoney myStock = pValues.getMoneyValue(SecurityAttribute.XFERREDVALUE);
        TethysMoney myConsideration = pValues.getMoneyValue(SecurityAttribute.CONSIDERATION);
        TethysMoney myCostXfer = pValues.getMoneyValue(SecurityAttribute.XFERREDCOST);
        TethysRatio myCostDilution = pValues.getRatioValue(SecurityAttribute.COSTDILUTION);
        TethysMoney myAllowedCost = pValues.getMoneyValue(SecurityAttribute.ALLOWEDCOST);
        TethysMoney myGain = pValues.getMoneyValue(SecurityAttribute.CAPITALGAIN);
        TethysMoney myTotalGains = pValues.getMoneyValue(SecurityAttribute.REALISEDGAINS);

        /* Record the calculation of total consideration */
        formatValue(SecurityAttribute.RETURNEDCASH, pCash, true);
        formatValue(SecurityAttribute.XFERREDVALUE, myStock, true);
        formatValue(SecurityAttribute.CONSIDERATION, myConsideration, false);
        formatAddition(pCash, myStock);

        /* Obtain the original cost */
        SecurityValues myPreviousValues = theBucket.getPreviousValuesForTransaction(pTrans);
        TethysMoney myOriginalCost = myPreviousValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);

        /* Format the cost dilution */
        if (myCostDilution != null) {
            formatValue(SecurityAttribute.COSTDILUTION, myCostDilution, false);
            formatDivision(myStock, myConsideration);
        }
        formatValue(SecurityAttribute.ALLOWEDCOST, myAllowedCost, false);
        formatMultiplication(myOriginalCost, myCostDilution);
        formatValue(SecurityAttribute.XFERREDCOST, myCostXfer, false);
        formatSubtraction(myOriginalCost, myAllowedCost);

        /* Record the gains allocation */
        if (myGain != null) {
            formatValue(SecurityAttribute.CAPITALGAIN, myGain, false);
            formatSubtraction(pCash, myAllowedCost);
            formatValue(SecurityAttribute.REALISEDGAINS, myTotalGains, true);
        }
    }

    /**
     * Format credit side of a StockAndCash TakeOver.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatCreditStockTakeOver(final Transaction pTrans,
                                           final SecurityValues pValues) {
        /* Access interesting values */
        TethysPrice myPrice = pValues.getPriceValue(SecurityAttribute.PRICE);
        TethysUnits myUnits = theBucket.getUnitsDeltaForTransaction(pTrans, SecurityAttribute.UNITS);
        TethysMoney myValueXfer = pValues.getMoneyValue(SecurityAttribute.XFERREDVALUE);
        TethysMoney myCostXfer = pValues.getMoneyValue(SecurityAttribute.XFERREDCOST);
        TethysMoney myResidualCost = pValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);
        TethysRatio myXchangeRate = pValues.getRatioValue(SecurityAttribute.EXCHANGERATE);

        /* Record the transfer of value and cost */
        formatValue(SecurityAttribute.XFERREDVALUE, myValueXfer, false);
        formatValuation(myUnits, myPrice, myXchangeRate);
        formatValue(SecurityAttribute.XFERREDCOST, myCostXfer, true);
        formatValue(SecurityAttribute.RESIDUALCOST, myResidualCost, true);
    }
}
