/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.lethe.reports;

import net.sourceforge.joceanus.metis.report.MetisReportBase;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.metis.report.MetisReportManager;
import net.sourceforge.joceanus.metis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseCashType;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Iterator;

/**
 * CapitalGains report builder.
 */
public class MoneyWiseReportCapitalGains
        extends MetisReportBase<MoneyWiseAnalysis, MoneyWiseAnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseReportResource.CAPITALGAINS_TITLE.getValue();

    /**
     * HTML builder.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final OceanusDataFormatter theFormatter;

    /**
     * The string builder.
     */
    private final StringBuilder theStringBuilder;

    /**
     * The source SecurityBucket.
     */
    private MoneyWiseAnalysisSecurityBucket theSecurity;

    /**
     * The transactions.
     */
    private MoneyWiseTransactionList theTransactions;

    /**
     * The EndDate.
     */
    private OceanusDate theEndDate;

    /**
     * The table.
     */
    private MetisHTMLTable theTable;

    /**
     * The attribute table.
     */
    private MetisHTMLTable theAttrTable;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected MoneyWiseReportCapitalGains(final MetisReportManager<MoneyWiseAnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
        theStringBuilder = new StringBuilder();
    }

    /**
     * Set the security bucket.
     * @param pSecurity the security bucket
     */
    protected void setSecurity(final MoneyWiseAnalysisSecurityBucket pSecurity) {
        theSecurity = pSecurity;
    }

    @Override
    public Document createReport(final MoneyWiseAnalysis pAnalysis) {
        /* Access the securities and the date */
        theTransactions = pAnalysis.getEditSet().getDataList(MoneyWiseBasicDataType.TRANSACTION, MoneyWiseTransactionList.class);
        theEndDate = pAnalysis.getDateRange().getEnd();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(theEndDate));
        theBuilder.makeSubTitle(myBody, theSecurity.getDecoratedName());

        /* Initialise the table */
        theTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(theTable);
        theBuilder.makeTitleCell(theTable, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE.getValue());
        theBuilder.makeTitleCell(theTable, MoneyWiseBasicDataType.TRANSACTION.getItemName());

        /* Format the history */
        formatHistory();

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * format the cost history.
     */
    private void formatHistory() {
        /* Loop through the transactions */
        final Iterator<MoneyWiseTransaction> myIterator = theTransactions.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransaction myTrans = myIterator.next();

            /* Check for End of report */
            if (theEndDate != null
                    && theEndDate.compareTo(myTrans.getDate()) < 0) {
                break;
            }

            /* If the transaction relates to the security */
            final MoneyWiseAnalysisSecurityValues myValues = theSecurity.getValuesForTransaction(myTrans);
            if (myValues != null) {
                /* Format the transaction */
                formatTransaction(myTrans, myValues);

                /* If we have an attribute table */
                if (theAttrTable != null) {
                    /* Embed the table correctly and reset the indicator */
                    theBuilder.embedTable(theAttrTable);
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
    private void formatTransaction(final MoneyWiseTransaction pTrans,
                                   final MoneyWiseAnalysisSecurityValues pValues) {
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
    private void formatBasicTransaction(final MoneyWiseTransaction pTrans) {
        /* Create the transaction row */
        theBuilder.startRow(theTable);
        theBuilder.makeValueCell(theTable, pTrans.getDate());
        theBuilder.makeValueCell(theTable, pTrans);
    }

    /**
     * Check whether this is a debit transaction for the security.
     * @param pTrans the transaction
     * @return true/false
     */
    private boolean isDebit(final MoneyWiseTransaction pTrans) {
        final MoneyWiseTransAsset myDebit = pTrans.getDirection().isTo()
                ? pTrans.getAccount()
                : pTrans.getPartner();
        return myDebit.equals(theSecurity.getSecurityHolding());
    }

    /**
     * Check whether this is a credit transaction for the security.
     * @param pTrans the transaction
     * @return true/false
     */
    private boolean isCredit(final MoneyWiseTransaction pTrans) {
        final MoneyWiseTransAsset myCredit = pTrans.getDirection().isFrom()
                ? pTrans.getAccount()
                : pTrans.getPartner();
        return myCredit.equals(theSecurity.getSecurityHolding());
    }

    /**
     * Ensure the attribute table.
     */
    private void ensureAttrTable() {
        /* If we do not have a current attribute table */
        if (theAttrTable == null) {
            /* Create a new table */
            theAttrTable = theBuilder.createEmbeddedTable(theTable);
        }
    }

    /**
     * Format a value.
     * @param pAttr the attribute
     * @param pValue the value
     */
    private void formatValue(final MoneyWiseAnalysisSecurityAttr pAttr,
                             final Object pValue) {
        /* Ensure that we have an attribute table */
        ensureAttrTable();

        /* Format the attribute */
        theBuilder.startRow(theAttrTable);
        theBuilder.makeValueCell(theAttrTable, pAttr);
        theBuilder.makeStretchedValueCell(theAttrTable, pValue);
    }

    /**
     * Format a division.
     * @param pAttr the attribute
     * @param pValue the value
     * @param pNumerator the numerator
     * @param pDivisor the divisor
     */
    private void formatDivision(final MoneyWiseAnalysisSecurityAttr pAttr,
                                final Object pValue,
                                final OceanusDecimal pNumerator,
                                final OceanusDecimal pDivisor) {
        /* Ensure that we have an attribute table */
        ensureAttrTable();

        /* Format the attribute */
        theBuilder.startRow(theAttrTable);
        theBuilder.makeValueCell(theAttrTable, pAttr);
        theBuilder.makeValueCell(theAttrTable, formatDivision(pNumerator, pDivisor));
        theBuilder.makeValueCell(theAttrTable, pValue);
    }

    /**
     * Format a division.
     * @param pNumerator the numerator
     * @param pDivisor the divisor
     * @return the formatted division
     */
    private String formatDivision(final OceanusDecimal pNumerator,
                                  final OceanusDecimal pDivisor) {
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
    private void formatValuation(final MoneyWiseAnalysisSecurityAttr pAttr,
                                 final Object pValue,
                                 final OceanusUnits pUnits,
                                 final OceanusPrice pPrice,
                                 final OceanusRatio pXchangeRate) {
        /* Ensure that we have an attribute table */
        ensureAttrTable();

        /* Format the attribute */
        theBuilder.startRow(theAttrTable);
        theBuilder.makeValueCell(theAttrTable, pAttr);
        theBuilder.makeValueCell(theAttrTable, formatValuation(pUnits, pPrice, pXchangeRate));
        theBuilder.makeValueCell(theAttrTable, pValue);
    }

    /**
     * Format a valuation.
     * @param pUnits the units
     * @param pPrice the price
     * @param pXchangeRate the exchange rate
     * @return the formatted valuation
     */
    private String formatValuation(final OceanusUnits pUnits,
                                   final OceanusPrice pPrice,
                                   final OceanusRatio pXchangeRate) {
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
    private void formatMultiplication(final MoneyWiseAnalysisSecurityAttr pAttr,
                                      final Object pValue,
                                      final OceanusDecimal pFirst,
                                      final OceanusDecimal pSecond) {
        /* Ensure that we have an attribute table */
        ensureAttrTable();

        /* Format the attribute */
        theBuilder.startRow(theAttrTable);
        theBuilder.makeValueCell(theAttrTable, pAttr);
        theBuilder.makeValueCell(theAttrTable, formatMultiplication(pFirst, pSecond));
        theBuilder.makeValueCell(theAttrTable, pValue);
    }

    /**
     * Format a multiplication.
     * @param pFirst the first item
     * @param pSecond the second item
     * @return the formatted multiplication
     */
    private String formatMultiplication(final OceanusDecimal pFirst,
                                        final OceanusDecimal pSecond) {
        return formatCombination(pFirst, pSecond, '*');
    }

    /**
     * Format an addition.
     * @param pAttr the attribute
     * @param pValue the value
     * @param pFirst the first item
     * @param pSecond the second item
     */
    private void formatAddition(final MoneyWiseAnalysisSecurityAttr pAttr,
                                final Object pValue,
                                final OceanusDecimal pFirst,
                                final OceanusDecimal pSecond) {
        /* Ensure that we have an attribute table */
        ensureAttrTable();

        /* Format the attribute */
        theBuilder.startRow(theAttrTable);
        theBuilder.makeValueCell(theAttrTable, pAttr);
        theBuilder.makeValueCell(theAttrTable, formatAddition(pFirst, pSecond));
        theBuilder.makeValueCell(theAttrTable, pValue);
    }

    /**
     * Format an addition.
     * @param pFirst the first item
     * @param pSecond the second item
     * @return the formatted addition
     */
    private String formatAddition(final OceanusDecimal pFirst,
                                  final OceanusDecimal pSecond) {
        return formatCombination(pFirst, pSecond, '+');
    }

    /**
     * Format a subtraction.
     * @param pAttr the attribute
     * @param pValue the value
     * @param pFirst the first item
     * @param pSecond the second item
     */
    private void formatSubtraction(final MoneyWiseAnalysisSecurityAttr pAttr,
                                   final Object pValue,
                                   final OceanusDecimal pFirst,
                                   final OceanusDecimal pSecond) {
        /* Ensure that we have an attribute table */
        ensureAttrTable();

        /* Format the attribute */
        theBuilder.startRow(theAttrTable);
        theBuilder.makeValueCell(theAttrTable, pAttr);
        theBuilder.makeValueCell(theAttrTable, formatSubtraction(pFirst, pSecond));
        theBuilder.makeValueCell(theAttrTable, pValue);
    }

    /**
     * Format a subtraction.
     * @param pFirst the first item
     * @param pSecond the second item
     * @return the formatted subtraction
     */
    private String formatSubtraction(final OceanusDecimal pFirst,
                                     final OceanusDecimal pSecond) {
        return formatCombination(pFirst, pSecond, '-');
    }

    /**
     * Format a combination.
     * @param pFirst the first item
     * @param pSecond the second item
     * @param pSymbol the symbol
     * @return the formatted combination
     */
    private String formatCombination(final OceanusDecimal pFirst,
                                     final OceanusDecimal pSecond,
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
    private void formatTransfer(final MoneyWiseTransaction pTrans,
                                final MoneyWiseAnalysisSecurityValues pValues) {
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
    private void formatDividend(final MoneyWiseTransaction pTrans,
                                final MoneyWiseAnalysisSecurityValues pValues) {
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
    private void formatTransferIn(final MoneyWiseTransaction pTrans,
                                  final MoneyWiseAnalysisSecurityValues pValues) {

        /* Access interesting values */
        final OceanusUnits myUnits = pValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        OceanusUnits myDeltaUnits = pTrans.getAccountDeltaUnits();
        if (myDeltaUnits == null) {
            myDeltaUnits = pTrans.getPartnerDeltaUnits();
        }
        final OceanusMoney myCost = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
        final OceanusMoney myAmount = theSecurity.getMoneyDeltaForTransaction(pTrans, MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
        final OceanusPrice myPrice = pValues.getPriceValue(MoneyWiseAnalysisSecurityAttr.PRICE);
        final OceanusRatio myXchangeRate = pValues.getRatioValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE);

        /* Obtain the original units/cost */
        final MoneyWiseAnalysisSecurityValues myPreviousValues = theSecurity.getPreviousValuesForTransaction(pTrans);
        final OceanusUnits myOriginalUnits = myPreviousValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        final OceanusMoney myOriginalCost = myPreviousValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);

        /* If this is an inheritance */
        if (pTrans.isCategoryClass(MoneyWiseTransCategoryClass.INHERITED)) {
            formatValuation(MoneyWiseAnalysisSecurityAttr.INVESTED, myAmount, myDeltaUnits, myPrice, myXchangeRate);
        } else {
            formatValue(MoneyWiseAnalysisSecurityAttr.INVESTED, myAmount);
        }

        /* Record the details */
        if (myDeltaUnits != null) {
            formatAddition(MoneyWiseAnalysisSecurityAttr.UNITS, myUnits, myOriginalUnits, myDeltaUnits);
        }
        formatAddition(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myCost, myOriginalCost, myAmount);
    }

    /**
     * Format transfer money out.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatTransferOut(final MoneyWiseTransaction pTrans,
                                   final MoneyWiseAnalysisSecurityValues pValues) {
        /* Access interesting values */
        final OceanusMoney myGain = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.CAPITALGAIN);
        final OceanusMoney myAllowedCost = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.ALLOWEDCOST);
        final OceanusRatio myCostDilution = pValues.getRatioValue(MoneyWiseAnalysisSecurityAttr.COSTDILUTION);
        final OceanusMoney myTotalGains = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS);
        final OceanusMoney myCost = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
        final OceanusUnits myUnits = pValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        final OceanusMoney myCash = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RETURNEDCASH);
        final OceanusMoney myConsideration = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.CONSIDERATION);
        final MoneyWiseCashType myCashType = pValues.getEnumValue(MoneyWiseAnalysisSecurityAttr.CASHTYPE, MoneyWiseCashType.class);

        /* Obtain the original values */
        final MoneyWiseAnalysisSecurityValues myPreviousValues = theSecurity.getPreviousValuesForTransaction(pTrans);
        final OceanusMoney myOriginalCost = myPreviousValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
        final OceanusUnits myOriginalUnits = myPreviousValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);

        /* Obtain the delta in units/money */
        OceanusUnits myDeltaUnits = theSecurity.getUnitsDeltaForTransaction(pTrans, MoneyWiseAnalysisSecurityAttr.UNITS);
        final OceanusMoney myAmount = new OceanusMoney(myCash);
        myAmount.negate();

        /* Report the returned cash */
        formatValue(MoneyWiseAnalysisSecurityAttr.RETURNEDCASH, myCash);
        if (myCashType != null) {
            formatValue(MoneyWiseAnalysisSecurityAttr.CASHTYPE, myCashType);
        }

        /* If we have changed the number of units */
        if (myDeltaUnits.isNonZero()) {
            /* Obtain the various values */
            myDeltaUnits = new OceanusUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Format the units */
            formatSubtraction(MoneyWiseAnalysisSecurityAttr.UNITS, myUnits, myOriginalUnits, myDeltaUnits);

            /* Format the dilution */
            formatDivision(MoneyWiseAnalysisSecurityAttr.COSTDILUTION, myCostDilution, myUnits, myOriginalUnits);

            /* Else we need to format the cost dilution */
        } else if (myConsideration != null) {
            /* Format the valuation */
            final OceanusMoney myValuation = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION);
            final OceanusPrice myPrice = pValues.getPriceValue(MoneyWiseAnalysisSecurityAttr.PRICE);
            final OceanusRatio myXchangeRate = pValues.getRatioValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE);
            formatValuation(MoneyWiseAnalysisSecurityAttr.VALUATION, myValuation, myUnits, myPrice, myXchangeRate);
            formatAddition(MoneyWiseAnalysisSecurityAttr.CONSIDERATION, myConsideration, myCash, myValuation);

            /* Format the dilution */
            formatDivision(MoneyWiseAnalysisSecurityAttr.COSTDILUTION, myCostDilution, myValuation, myConsideration);
        }

        /* Record the details */
        if (myCostDilution != null) {
            formatMultiplication(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myCost, myOriginalCost, myCostDilution);
            formatSubtraction(MoneyWiseAnalysisSecurityAttr.ALLOWEDCOST, myAllowedCost, myOriginalCost, myCost);
        } else {
            formatValue(MoneyWiseAnalysisSecurityAttr.ALLOWEDCOST, myAllowedCost);
            formatSubtraction(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myCost, myOriginalCost, myAllowedCost);
        }

        /* Record the gains allocation */
        if (myGain != null) {
            formatSubtraction(MoneyWiseAnalysisSecurityAttr.CAPITALGAIN, myGain, myCash, myAllowedCost);
            formatValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS, myTotalGains);
        }
    }

    /**
     * Format a Units Adjustment.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatUnitsAdjust(final MoneyWiseTransaction pTrans,
                                   final MoneyWiseAnalysisSecurityValues pValues) {
        /* Format the basic transaction */
        formatBasicTransaction(pTrans);

        /* Access interesting values */
        final OceanusUnits myUnits = pValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        OceanusUnits myDeltaUnits = pTrans.getAccountDeltaUnits();

        /* Obtain the original units */
        final MoneyWiseAnalysisSecurityValues myPreviousValues = theSecurity.getPreviousValuesForTransaction(pTrans);
        final OceanusUnits myOriginalUnits = myPreviousValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);

        /* Record the details */
        if (myDeltaUnits.isPositive()) {
            formatAddition(MoneyWiseAnalysisSecurityAttr.UNITS, myUnits, myOriginalUnits, myDeltaUnits);
        } else {
            myDeltaUnits = new OceanusUnits(myDeltaUnits);
            myDeltaUnits.negate();
            formatSubtraction(MoneyWiseAnalysisSecurityAttr.UNITS, myUnits, myOriginalUnits, myDeltaUnits);
        }
    }

    /**
     * Format a Stock DeMerger.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatStockDeMerger(final MoneyWiseTransaction pTrans,
                                     final MoneyWiseAnalysisSecurityValues pValues) {
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
    private void formatDebitStockDeMerger(final MoneyWiseTransaction pTrans,
                                          final MoneyWiseAnalysisSecurityValues pValues) {
        /* Access interesting values */
        final OceanusRatio myCostDilution = pValues.getRatioValue(MoneyWiseAnalysisSecurityAttr.COSTDILUTION);
        final OceanusMoney myResidualCost = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
        final OceanusMoney myXferredCost = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST);
        OceanusUnits myDeltaUnits = theSecurity.getUnitsDeltaForTransaction(pTrans, MoneyWiseAnalysisSecurityAttr.UNITS);

        /* Check whether the units have changed */
        final boolean isDeltaUnits = myDeltaUnits.isNonZero();

        /* Obtain the original cost */
        final MoneyWiseAnalysisSecurityValues myPreviousValues = theSecurity.getPreviousValuesForTransaction(pTrans);
        final OceanusMoney myOriginalCost = myPreviousValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);

        /* If we have changed the number of units */
        if (isDeltaUnits) {
            /* Obtain the various values */
            final OceanusUnits myOriginalUnits = myPreviousValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
            final OceanusUnits myUnits = pValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
            myDeltaUnits = new OceanusUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Format the units/dilution */
            formatSubtraction(MoneyWiseAnalysisSecurityAttr.UNITS, myUnits, myOriginalUnits, myDeltaUnits);
            formatDivision(MoneyWiseAnalysisSecurityAttr.COSTDILUTION, myCostDilution, myUnits, myOriginalUnits);

            /* else just report the dilution */
        } else {
            formatValue(MoneyWiseAnalysisSecurityAttr.COSTDILUTION, myCostDilution);
        }

        /* Record the details */
        formatMultiplication(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myResidualCost, myOriginalCost, myCostDilution);
        formatSubtraction(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myXferredCost, myOriginalCost, myResidualCost);
    }

    /**
     * Format credit side of a Stock DeMerger.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatCreditStockDeMerger(final MoneyWiseTransaction pTrans,
                                           final MoneyWiseAnalysisSecurityValues pValues) {
        /* Access interesting values */
        final OceanusMoney myResidualCost = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
        final OceanusMoney myXferredCost = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST);
        final OceanusMoney myValueXfer = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE);
        final OceanusUnits myUnits = theSecurity.getUnitsDeltaForTransaction(pTrans, MoneyWiseAnalysisSecurityAttr.UNITS);
        final OceanusPrice myPrice = pValues.getPriceValue(MoneyWiseAnalysisSecurityAttr.PRICE);
        final OceanusRatio myXchangeRate = pValues.getRatioValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE);

        /* Record the details */
        formatValuation(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE, myValueXfer, myUnits, myPrice, myXchangeRate);
        formatValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myXferredCost);
        formatValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myResidualCost);
    }

    /**
     * Format a Stock TakeOver.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatStockTakeOver(final MoneyWiseTransaction pTrans,
                                     final MoneyWiseAnalysisSecurityValues pValues) {
        /* Format the basic transaction */
        formatBasicTransaction(pTrans);

        /* Split out Stock and Cash TakeOver */
        final OceanusMoney myCash = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RETURNEDCASH);
        if (myCash != null) {
            formatStockAndCashTakeOver(pTrans, pValues, myCash);

            /* Split workings for credit and debit */
        } else if (isDebit(pTrans)) {
            /* Record the transfer of cost for simple replacement takeOver */
            final OceanusMoney myCostXfer = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST);
            formatValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myCostXfer);
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
    private void formatStockAndCashTakeOver(final MoneyWiseTransaction pTrans,
                                            final MoneyWiseAnalysisSecurityValues pValues,
                                            final OceanusMoney pCash) {
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
    private void formatDebitStockAndCashTakeOver(final MoneyWiseTransaction pTrans,
                                                 final MoneyWiseAnalysisSecurityValues pValues,
                                                 final OceanusMoney pCash) {
        /* Access interesting values */
        final OceanusMoney myStock = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE);
        final OceanusMoney myConsideration = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.CONSIDERATION);
        final OceanusMoney myCostXfer = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST);
        final OceanusRatio myCostDilution = pValues.getRatioValue(MoneyWiseAnalysisSecurityAttr.COSTDILUTION);
        final OceanusMoney myAllowedCost = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.ALLOWEDCOST);
        final OceanusMoney myGain = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.CAPITALGAIN);
        final OceanusMoney myTotalGains = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS);
        final MoneyWiseCashType myCashType = pValues.getEnumValue(MoneyWiseAnalysisSecurityAttr.CASHTYPE, MoneyWiseCashType.class);

        /* Record the calculation of total consideration */
        formatValue(MoneyWiseAnalysisSecurityAttr.RETURNEDCASH, pCash);
        formatValue(MoneyWiseAnalysisSecurityAttr.CASHTYPE, myCashType);
        formatValue(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE, myStock);
        formatAddition(MoneyWiseAnalysisSecurityAttr.CONSIDERATION, myConsideration, pCash, myStock);

        /* Obtain the original cost */
        final MoneyWiseAnalysisSecurityValues myPreviousValues = theSecurity.getPreviousValuesForTransaction(pTrans);
        final OceanusMoney myOriginalCost = myPreviousValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);

        /* Format the cost dilution */
        if (myCostDilution != null) {
            formatDivision(MoneyWiseAnalysisSecurityAttr.COSTDILUTION, myCostDilution, pCash, myConsideration);
            formatMultiplication(MoneyWiseAnalysisSecurityAttr.ALLOWEDCOST, myAllowedCost, myOriginalCost, myCostDilution);
        } else {
            formatValue(MoneyWiseAnalysisSecurityAttr.ALLOWEDCOST, myAllowedCost);
        }
        formatSubtraction(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myCostXfer, myOriginalCost, myAllowedCost);

        /* Record the gains allocation */
        if (myGain != null) {
            formatSubtraction(MoneyWiseAnalysisSecurityAttr.CAPITALGAIN, myGain, pCash, myAllowedCost);
            formatValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS, myTotalGains);
        }
    }

    /**
     * Format credit side of a StockAndCash TakeOver.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatCreditStockTakeOver(final MoneyWiseTransaction pTrans,
                                           final MoneyWiseAnalysisSecurityValues pValues) {
        /* Access interesting values */
        final OceanusPrice myPrice = pValues.getPriceValue(MoneyWiseAnalysisSecurityAttr.PRICE);
        final OceanusUnits myUnits = theSecurity.getUnitsDeltaForTransaction(pTrans, MoneyWiseAnalysisSecurityAttr.UNITS);
        final OceanusMoney myValueXfer = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE);
        final OceanusMoney myCostXfer = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST);
        final OceanusMoney myResidualCost = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
        final OceanusRatio myXchangeRate = pValues.getRatioValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE);

        /* Detail the new units and cost */
        final MoneyWiseAnalysisSecurityValues myPreviousValues = theSecurity.getPreviousValuesForTransaction(pTrans);
        final OceanusUnits myNewUnits = pValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        final OceanusUnits myOriginalUnits = myPreviousValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        formatAddition(MoneyWiseAnalysisSecurityAttr.UNITS, myNewUnits, myOriginalUnits, myUnits);

        /* Record the transfer of value and cost */
        formatValuation(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE, myValueXfer, myUnits, myPrice, myXchangeRate);
        formatValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myCostXfer);
        formatValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myResidualCost);
    }

    /**
     * Format a Stock DeMerger.
     * @param pTrans the transaction
     * @param pValues the values for the transaction
     */
    private void formatPortfolioXfer(final MoneyWiseTransaction pTrans,
                                     final MoneyWiseAnalysisSecurityValues pValues) {
        /* Format the basic transaction */
        formatBasicTransaction(pTrans);

        /* Determine the direction of transfer */
        final OceanusMoney myCostXfer = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST);
        formatValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myCostXfer);

        final OceanusUnits myUnits = theSecurity.getUnitsDeltaForTransaction(pTrans, MoneyWiseAnalysisSecurityAttr.UNITS);
        if (myUnits.isPositive()) {
            /* Detail the new units and cost */
            final MoneyWiseAnalysisSecurityValues myPreviousValues = theSecurity.getPreviousValuesForTransaction(pTrans);
            final OceanusUnits myNewUnits = pValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
            final OceanusUnits myOriginalUnits = myPreviousValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
            formatAddition(MoneyWiseAnalysisSecurityAttr.UNITS, myNewUnits, myOriginalUnits, myUnits);
            final OceanusMoney myCost = pValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
            final OceanusMoney myOriginalCost = myPreviousValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
            formatAddition(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myCost, myOriginalCost, myCostXfer);
        }
    }

    @Override
    public MoneyWiseAnalysisFilter<?, ?> processFilter(final Object pSource) {
        return null;
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        return null;
    }
}
