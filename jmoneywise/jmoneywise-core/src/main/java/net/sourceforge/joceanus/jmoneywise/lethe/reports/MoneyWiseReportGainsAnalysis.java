/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.reports;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseCashType;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Gains Analysis Report.
 */
public class MoneyWiseReportGainsAnalysis {
    /**
     * The formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * The source SecurityBucket.
     */
    private final SecurityBucket theBucket;

    /**
     * The HTML builder.
     */
    private final MetisReportHTMLBuilder theHTMLBuilder;

    /**
     * The string builder.
     */
    private final StringBuilder theStringBuilder;

    /**
     * The table.
     */
    private final MetisHTMLTable theTable;

    /**
     * The attribute table.
     */
    private MetisHTMLTable theAttrTable;

    /**
     * Constructor.
     * @param pReport the parent report
     * @param pParent the parent table
     * @param pSecurity the security bucket
     */
    public MoneyWiseReportGainsAnalysis(final MoneyWiseReportCapitalGains pReport,
                                        final MetisHTMLTable pParent,
                                        final SecurityBucket pSecurity) {
        /* Store parameters */
        theFormatter = pReport.getFormatter();
        theHTMLBuilder = pReport.getBuilder();
        theBucket = pSecurity;
        theStringBuilder = new StringBuilder();

        /* Create a new table */
        theTable = theHTMLBuilder.createEmbeddedTable(pParent);

        /* Build the headers */
        theHTMLBuilder.startRow(theTable);
        theHTMLBuilder.makeTitleCell(theTable, MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue());
        theHTMLBuilder.makeTitleCell(theTable, MoneyWiseDataType.TRANSACTION.getItemName());

        /* Format the history */
        formatHistory(pReport.transactionIterator(), pReport.getEndDate());
    }

    /**
     * Obtain the table.
     * @return the table
     */
    protected MetisHTMLTable getTable() {
        return theTable;
    }

    /**
     * format the cost history.
     * @param pIterator the transaction iterator
     * @param pEndDate the endDate
     */
    private void formatHistory(final Iterator<Transaction> pIterator,
                               final TethysDate pEndDate) {
        /* Loop through the transactions */
        while (pIterator.hasNext()) {
            Transaction myTrans = pIterator.next();

            /* Check for End of report */
            if ((pEndDate != null)
                && (pEndDate.compareTo(myTrans.getDate()) < 0)) {
                break;
            }

            /* If the transaction relates to the security */
            SecurityValues myValues = theBucket.getValuesForTransaction(myTrans);
            if (myValues != null) {
                /* Format the transaction */
                formatTransaction(myTrans, myValues);

                /* If we have an attribute table */
                if (theAttrTable != null) {
                    /* Embed the table correctly and reset the indicator */
                    theHTMLBuilder.embedTable(theAttrTable);
                    theAttrTable = null;
                }
            }
        }
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
            case PORTFOLIOXFER:
                formatPortfolioXfer(pTrans, pValues);
                break;
            default:
                break;
        }
    }

    /**
     * Format basic details of a transaction.
     * @param pTrans the transaction
     */
    private void formatBasicTransaction(final Transaction pTrans) {
        /* Create the transaction row */
        theHTMLBuilder.startRow(theTable);
        theHTMLBuilder.makeValueCell(theTable, pTrans.getDate());
        theHTMLBuilder.makeValueCell(theTable, pTrans);
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
     * Ensure the attribute table.
     */
    private void ensureAttrTable() {
        /* If we do not have a current attribute table */
        if (theAttrTable == null) {
            /* Create a new table */
            theAttrTable = theHTMLBuilder.createEmbeddedTable(theTable);
        }
    }

    /**
     * Format a value.
     * @param pAttr the attribute
     * @param pValue the value
     */
    private void formatValue(final SecurityAttribute pAttr,
                             final Object pValue) {
        /* Ensure that we have an attribute table */
        ensureAttrTable();

        /* Format the attribute */
        theHTMLBuilder.startRow(theAttrTable);
        theHTMLBuilder.makeValueCell(theAttrTable, pAttr);
        theHTMLBuilder.makeStretchedValueCell(theAttrTable, pValue);
    }

    /**
     * Format a division.
     * @param pAttr the attribute
     * @param pValue the value
     * @param pNumerator the numerator
     * @param pDivisor the divisor
     */
    private void formatDivision(final SecurityAttribute pAttr,
                                final Object pValue,
                                final TethysDecimal pNumerator,
                                final TethysDecimal pDivisor) {
        /* Ensure that we have an attribute table */
        ensureAttrTable();

        /* Format the attribute */
        theHTMLBuilder.startRow(theAttrTable);
        theHTMLBuilder.makeValueCell(theAttrTable, pAttr);
        theHTMLBuilder.makeValueCell(theAttrTable, formatDivision(pNumerator, pDivisor));
        theHTMLBuilder.makeValueCell(theAttrTable, pValue);
    }

    /**
     * Format a division.
     * @param pNumerator the numerator
     * @param pDivisor the divisor
     * @return the formatted division
     */
    private String formatDivision(final TethysDecimal pNumerator,
                                  final TethysDecimal pDivisor) {
        return formatCombination(pNumerator, pDivisor, '/');
    }

    /**
     * Format a valuation.
     * @param pAttr the attribute
     * @param pValue the value
     * @param pUnits the units
     * @param pPrice the price
     * @param pXchangeRate the exchange rate
     */
    private void formatValuation(final SecurityAttribute pAttr,
                                 final Object pValue,
                                 final TethysUnits pUnits,
                                 final TethysPrice pPrice,
                                 final TethysRatio pXchangeRate) {
        /* Ensure that we have an attribute table */
        ensureAttrTable();

        /* Format the attribute */
        theHTMLBuilder.startRow(theAttrTable);
        theHTMLBuilder.makeValueCell(theAttrTable, pAttr);
        theHTMLBuilder.makeValueCell(theAttrTable, formatValuation(pUnits, pPrice, pXchangeRate));
        theHTMLBuilder.makeValueCell(theAttrTable, pValue);
    }

    /**
     * Format a valuation.
     * @param pUnits the units
     * @param pPrice the price
     * @param pXchangeRate the exchange rate
     * @return the formatted valuation
     */
    private String formatValuation(final TethysUnits pUnits,
                                   final TethysPrice pPrice,
                                   final TethysRatio pXchangeRate) {
        theStringBuilder.setLength(0);
        theStringBuilder.append(theFormatter.formatObject(pUnits));
        theStringBuilder.append('@');
        theStringBuilder.append(theFormatter.formatObject(pPrice));
        if (pXchangeRate != null) {
            theStringBuilder.append('/');
            theStringBuilder.append(theFormatter.formatObject(pXchangeRate));
        }
        return theStringBuilder.toString();
    }

    /**
     * Format a multiplication.
     * @param pAttr the attribute
     * @param pValue the value
     * @param pFirst the first item
     * @param pSecond the second item
     */
    private void formatMultiplication(final SecurityAttribute pAttr,
                                      final Object pValue,
                                      final TethysDecimal pFirst,
                                      final TethysDecimal pSecond) {
        /* Ensure that we have an attribute table */
        ensureAttrTable();

        /* Format the attribute */
        theHTMLBuilder.startRow(theAttrTable);
        theHTMLBuilder.makeValueCell(theAttrTable, pAttr);
        theHTMLBuilder.makeValueCell(theAttrTable, formatMultiplication(pFirst, pSecond));
        theHTMLBuilder.makeValueCell(theAttrTable, pValue);
    }

    /**
     * Format a multiplication.
     * @param pFirst the first item
     * @param pSecond the second item
     * @return the formatted multiplication
     */
    private String formatMultiplication(final TethysDecimal pFirst,
                                        final TethysDecimal pSecond) {
        return formatCombination(pFirst, pSecond, '*');
    }

    /**
     * Format an addition.
     * @param pAttr the attribute
     * @param pValue the value
     * @param pFirst the first item
     * @param pSecond the second item
     */
    private void formatAddition(final SecurityAttribute pAttr,
                                final Object pValue,
                                final TethysDecimal pFirst,
                                final TethysDecimal pSecond) {
        /* Ensure that we have an attribute table */
        ensureAttrTable();

        /* Format the attribute */
        theHTMLBuilder.startRow(theAttrTable);
        theHTMLBuilder.makeValueCell(theAttrTable, pAttr);
        theHTMLBuilder.makeValueCell(theAttrTable, formatAddition(pFirst, pSecond));
        theHTMLBuilder.makeValueCell(theAttrTable, pValue);
    }

    /**
     * Format an addition.
     * @param pFirst the first item
     * @param pSecond the second item
     * @return the formatted addition
     */
    private String formatAddition(final TethysDecimal pFirst,
                                  final TethysDecimal pSecond) {
        return formatCombination(pFirst, pSecond, '+');
    }

    /**
     * Format a subtraction.
     * @param pAttr the attribute
     * @param pValue the value
     * @param pFirst the first item
     * @param pSecond the second item
     */
    private void formatSubtraction(final SecurityAttribute pAttr,
                                   final Object pValue,
                                   final TethysDecimal pFirst,
                                   final TethysDecimal pSecond) {
        /* Ensure that we have an attribute table */
        ensureAttrTable();

        /* Format the attribute */
        theHTMLBuilder.startRow(theAttrTable);
        theHTMLBuilder.makeValueCell(theAttrTable, pAttr);
        theHTMLBuilder.makeValueCell(theAttrTable, formatSubtraction(pFirst, pSecond));
        theHTMLBuilder.makeValueCell(theAttrTable, pValue);
    }

    /**
     * Format a subtraction.
     * @param pFirst the first item
     * @param pSecond the second item
     * @return the formatted subtraction
     */
    private String formatSubtraction(final TethysDecimal pFirst,
                                     final TethysDecimal pSecond) {
        return formatCombination(pFirst, pSecond, '-');
    }

    /**
     * Format a combination.
     * @param pFirst the first item
     * @param pSecond the second item
     * @param pSymbol the symbol
     * @return the formatted combination
     */
    private String formatCombination(final TethysDecimal pFirst,
                                     final TethysDecimal pSecond,
                                     final char pSymbol) {
        theStringBuilder.setLength(0);
        theStringBuilder.append(theFormatter.formatObject(pFirst));
        theStringBuilder.append(pSymbol);
        theStringBuilder.append(theFormatter.formatObject(pSecond));
        return theStringBuilder.toString();
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
            formatValuation(SecurityAttribute.INVESTED, myAmount, myDeltaUnits, myPrice, myXchangeRate);
        } else {
            formatValue(SecurityAttribute.INVESTED, myAmount);
        }

        /* Record the details */
        if (myDeltaUnits != null) {
            formatAddition(SecurityAttribute.UNITS, myUnits, myOriginalUnits, myDeltaUnits);
        }
        formatAddition(SecurityAttribute.RESIDUALCOST, myCost, myOriginalCost, myAmount);
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
        MoneyWiseCashType myCashType = pValues.getEnumValue(SecurityAttribute.CASHTYPE, MoneyWiseCashType.class);

        /* Obtain the original values */
        SecurityValues myPreviousValues = theBucket.getPreviousValuesForTransaction(pTrans);
        TethysMoney myOriginalCost = myPreviousValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);
        TethysUnits myOriginalUnits = myPreviousValues.getUnitsValue(SecurityAttribute.UNITS);

        /* Obtain the delta in units/money */
        TethysUnits myDeltaUnits = theBucket.getUnitsDeltaForTransaction(pTrans, SecurityAttribute.UNITS);
        TethysMoney myAmount = new TethysMoney(myCash);
        myAmount.negate();

        /* Report the returned cash */
        formatValue(SecurityAttribute.RETURNEDCASH, myCash);
        if (myCashType != null) {
            formatValue(SecurityAttribute.CASHTYPE, myCashType);
        }

        /* If we have changed the number of units */
        if (myDeltaUnits.isNonZero()) {
            /* Obtain the various values */
            myDeltaUnits = new TethysUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Format the units */
            formatSubtraction(SecurityAttribute.UNITS, myUnits, myOriginalUnits, myDeltaUnits);

            /* Format the dilution */
            formatDivision(SecurityAttribute.COSTDILUTION, myCostDilution, myUnits, myOriginalUnits);

            /* Else we need to format the cost dilution */
        } else if (myConsideration != null) {
            /* Format the valuation */
            TethysMoney myValuation = pValues.getMoneyValue(SecurityAttribute.VALUATION);
            TethysPrice myPrice = pValues.getPriceValue(SecurityAttribute.PRICE);
            TethysRatio myXchangeRate = pValues.getRatioValue(SecurityAttribute.EXCHANGERATE);
            formatValuation(SecurityAttribute.VALUATION, myValuation, myUnits, myPrice, myXchangeRate);
            formatAddition(SecurityAttribute.CONSIDERATION, myConsideration, myCash, myValuation);

            /* Format the dilution */
            formatDivision(SecurityAttribute.COSTDILUTION, myCostDilution, myValuation, myConsideration);
        }

        /* Record the details */
        if (myCostDilution != null) {
            formatMultiplication(SecurityAttribute.RESIDUALCOST, myCost, myOriginalCost, myCostDilution);
            formatSubtraction(SecurityAttribute.ALLOWEDCOST, myAllowedCost, myOriginalCost, myCost);
        } else {
            formatValue(SecurityAttribute.ALLOWEDCOST, myAllowedCost);
            formatSubtraction(SecurityAttribute.RESIDUALCOST, myCost, myOriginalCost, myAllowedCost);
        }

        /* Record the gains allocation */
        if (myGain != null) {
            formatSubtraction(SecurityAttribute.CAPITALGAIN, myGain, myCash, myAllowedCost);
            formatValue(SecurityAttribute.REALISEDGAINS, myTotalGains);
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
        formatSubtraction(SecurityAttribute.UNITS, myUnits, myOriginalUnits, myDeltaUnits);
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
        formatAddition(SecurityAttribute.UNITS, myUnits, myOriginalUnits, myDeltaUnits);
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

        /* Check whether the units have changed */
        boolean isDeltaUnits = myDeltaUnits.isNonZero();

        /* Obtain the original cost */
        SecurityValues myPreviousValues = theBucket.getPreviousValuesForTransaction(pTrans);
        TethysMoney myOriginalCost = myPreviousValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);

        /* If we have changed the number of units */
        if (isDeltaUnits) {
            /* Obtain the various values */
            TethysUnits myOriginalUnits = myPreviousValues.getUnitsValue(SecurityAttribute.UNITS);
            TethysUnits myUnits = pValues.getUnitsValue(SecurityAttribute.UNITS);
            myDeltaUnits = new TethysUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Format the units/dilution */
            formatSubtraction(SecurityAttribute.UNITS, myUnits, myOriginalUnits, myDeltaUnits);
            formatDivision(SecurityAttribute.COSTDILUTION, myCostDilution, myUnits, myOriginalUnits);

            /* else just report the dilution */
        } else {
            formatValue(SecurityAttribute.COSTDILUTION, myCostDilution);
        }

        /* Record the details */
        formatMultiplication(SecurityAttribute.RESIDUALCOST, myResidualCost, myOriginalCost, myCostDilution);
        formatSubtraction(SecurityAttribute.XFERREDCOST, myXferredCost, myOriginalCost, myResidualCost);
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
        formatValuation(SecurityAttribute.XFERREDVALUE, myValueXfer, myUnits, myPrice, myXchangeRate);
        formatValue(SecurityAttribute.XFERREDCOST, myXferredCost);
        formatValue(SecurityAttribute.RESIDUALCOST, myResidualCost);
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

            /* Split workings for credit and debit */
        } else if (isDebit(pTrans)) {
            /* Record the transfer of cost for simple replacement takeOver */
            TethysMoney myCostXfer = pValues.getMoneyValue(SecurityAttribute.XFERREDCOST);
            formatValue(SecurityAttribute.XFERREDCOST, myCostXfer);
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
        MoneyWiseCashType myCashType = pValues.getEnumValue(SecurityAttribute.CASHTYPE, MoneyWiseCashType.class);

        /* Record the calculation of total consideration */
        formatValue(SecurityAttribute.RETURNEDCASH, pCash);
        formatValue(SecurityAttribute.CASHTYPE, myCashType);
        formatValue(SecurityAttribute.XFERREDVALUE, myStock);
        formatAddition(SecurityAttribute.CONSIDERATION, myConsideration, pCash, myStock);

        /* Obtain the original cost */
        SecurityValues myPreviousValues = theBucket.getPreviousValuesForTransaction(pTrans);
        TethysMoney myOriginalCost = myPreviousValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);

        /* Format the cost dilution */
        if (myCostDilution != null) {
            formatDivision(SecurityAttribute.COSTDILUTION, myCostDilution, pCash, myConsideration);
            formatMultiplication(SecurityAttribute.ALLOWEDCOST, myAllowedCost, myOriginalCost, myCostDilution);
        } else {
            formatValue(SecurityAttribute.ALLOWEDCOST, myAllowedCost);
        }
        formatSubtraction(SecurityAttribute.XFERREDCOST, myCostXfer, myOriginalCost, myAllowedCost);

        /* Record the gains allocation */
        if (myGain != null) {
            formatSubtraction(SecurityAttribute.CAPITALGAIN, myGain, pCash, myAllowedCost);
            formatValue(SecurityAttribute.REALISEDGAINS, myTotalGains);
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

        /* Detail the new units and cost */
        SecurityValues myPreviousValues = theBucket.getPreviousValuesForTransaction(pTrans);
        TethysUnits myNewUnits = pValues.getUnitsValue(SecurityAttribute.UNITS);
        TethysUnits myOriginalUnits = myPreviousValues.getUnitsValue(SecurityAttribute.UNITS);
        formatAddition(SecurityAttribute.UNITS, myNewUnits, myOriginalUnits, myUnits);

        /* Record the transfer of value and cost */
        formatValuation(SecurityAttribute.XFERREDVALUE, myValueXfer, myUnits, myPrice, myXchangeRate);
        formatValue(SecurityAttribute.XFERREDCOST, myCostXfer);
        formatValue(SecurityAttribute.RESIDUALCOST, myResidualCost);
    }

    /**
     * Format a Stock DeMerger.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatPortfolioXfer(final Transaction pTrans,
                                     final SecurityValues pValues) {
        /* Format the basic transaction */
        formatBasicTransaction(pTrans);

        /* Determine the direction of transfer */
        TethysMoney myCostXfer = pValues.getMoneyValue(SecurityAttribute.XFERREDCOST);
        formatValue(SecurityAttribute.XFERREDCOST, myCostXfer);

        TethysUnits myUnits = theBucket.getUnitsDeltaForTransaction(pTrans, SecurityAttribute.UNITS);
        if (myUnits.isPositive()) {
            /* Detail the new units and cost */
            SecurityValues myPreviousValues = theBucket.getPreviousValuesForTransaction(pTrans);
            TethysUnits myNewUnits = pValues.getUnitsValue(SecurityAttribute.UNITS);
            TethysUnits myOriginalUnits = myPreviousValues.getUnitsValue(SecurityAttribute.UNITS);
            formatAddition(SecurityAttribute.UNITS, myNewUnits, myOriginalUnits, myUnits);
            TethysMoney myCost = pValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);
            TethysMoney myOriginalCost = myPreviousValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);
            formatAddition(SecurityAttribute.RESIDUALCOST, myCost, myOriginalCost, myCostXfer);
        }
    }
}