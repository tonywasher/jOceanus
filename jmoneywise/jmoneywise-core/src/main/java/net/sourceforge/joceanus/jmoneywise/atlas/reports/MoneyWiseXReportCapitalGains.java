/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.reports;

import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;
import net.sourceforge.joceanus.jmetis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseXAnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jmoneywise.reports.MoneyWiseReportResource;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseCashType;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * CapitalGains report builder.
 */
public class MoneyWiseXReportCapitalGains
        extends MetisReportBase<MoneyWiseXAnalysis, MoneyWiseXAnalysisFilter<?, ?>> {
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
    private final TethysUIDataFormatter theFormatter;

    /**
     * The string builder.
     */
    private final StringBuilder theStringBuilder;

    /**
     * The source SecurityBucket.
     */
    private MoneyWiseXAnalysisSecurityBucket theSecurity;

    /**
     * The transactions.
     */
    private List<MoneyWiseXAnalysisEvent> theEvents;

    /**
     * The EndDate.
     */
    private TethysDate theEndDate;

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
    MoneyWiseXReportCapitalGains(final MetisReportManager<MoneyWiseXAnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
        theStringBuilder = new StringBuilder();
    }

    /**
     * Set the security bucket.
     * @param pSecurity the security bucket
     */
    protected void setSecurity(final MoneyWiseXAnalysisSecurityBucket pSecurity) {
        theSecurity = pSecurity;
    }

    @Override
    public Document createReport(final MoneyWiseXAnalysis pAnalysis) {
        /* Access the events and the date */
        //theEvents = pAnalysis.getEditSet().getDataList(MoneyWiseBasicDataType.TRANSACTION, MoneyWiseTransactionList.class);
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
        for (MoneyWiseXAnalysisEvent myEvent : theEvents) {
            /* Ignore non-transactions */
            if (myEvent.getEventType() != MoneyWiseXAnalysisEventType.TRANSACTION) {
                continue;
            }

            /* Check for End of report */
            if (theEndDate != null
                    && theEndDate.compareTo(myEvent.getDate()) < 0) {
                break;
            }

            /* If the transaction relates to the security */
            final MoneyWiseXAnalysisSecurityValues myValues = theSecurity.getValuesForEvent(myEvent);
            if (myValues != null) {
                /* Format the transaction */
                formatTransaction(myEvent, myValues);

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
     * @param pEvent the event
     * @param pValues the values for the transaction
     */
    private void formatTransaction(final MoneyWiseXAnalysisEvent pEvent,
                                   final MoneyWiseXAnalysisSecurityValues pValues) {
        /* Switch on the class */
        final MoneyWiseTransaction myTrans = pEvent.getTransaction();
        switch (myTrans.getCategoryClass()) {
            case TRANSFER:
            case STOCKRIGHTSISSUE:
            case INHERITED:
                formatTransfer(pEvent, pValues);
                break;
            case SECURITYREPLACE:
            case STOCKTAKEOVER:
                formatStockTakeOver(pEvent, pValues);
                break;
            case STOCKDEMERGER:
                formatStockDeMerger(pEvent, pValues);
                break;
            case STOCKSPLIT:
            case UNITSADJUST:
                formatUnitsAdjust(pEvent, pValues);
                break;
            case DIVIDEND:
                formatDividend(pEvent, pValues);
                break;
            case PORTFOLIOXFER:
                formatPortfolioXfer(pEvent, pValues);
                break;
            default:
                break;
        }
    }

    /**
     * Format basic details of a transaction.
     * @param pEvent the event
     */
    private void formatBasicTransaction(final MoneyWiseXAnalysisEvent pEvent) {
        /* Create the transaction row */
        final MoneyWiseTransaction myTrans = pEvent.getTransaction();
        theBuilder.startRow(theTable);
        theBuilder.makeValueCell(theTable, myTrans.getDate());
        theBuilder.makeValueCell(theTable, myTrans);
    }

    /**
     * Check whether this is a debit transaction for the security.
     * @param pEvent the event
     * @return true/false
     */
    private boolean isDebit(final MoneyWiseXAnalysisEvent pEvent) {
        final MoneyWiseTransaction myTrans = pEvent.getTransaction();
        final MoneyWiseTransAsset myDebit = myTrans.getDirection().isTo()
                ? myTrans.getAccount()
                : myTrans.getPartner();
        return myDebit.equals(theSecurity.getSecurityHolding());
    }

    /**
     * Check whether this is a credit transaction for the security.
     * @param pEvent the event
     * @return true/false
     */
    private boolean isCredit(final MoneyWiseXAnalysisEvent pEvent) {
        final MoneyWiseTransaction myTrans = pEvent.getTransaction();
        final MoneyWiseTransAsset myCredit = myTrans.getDirection().isFrom()
                ? myTrans.getAccount()
                : myTrans.getPartner();
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
    private void formatValue(final MoneyWiseXAnalysisSecurityAttr pAttr,
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
    private void formatDivision(final MoneyWiseXAnalysisSecurityAttr pAttr,
                                final Object pValue,
                                final TethysDecimal pNumerator,
                                final TethysDecimal pDivisor) {
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
    private void formatValuation(final MoneyWiseXAnalysisSecurityAttr pAttr,
                                 final Object pValue,
                                 final TethysUnits pUnits,
                                 final TethysPrice pPrice,
                                 final TethysRatio pXchangeRate) {
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
    private void formatMultiplication(final MoneyWiseXAnalysisSecurityAttr pAttr,
                                      final Object pValue,
                                      final TethysDecimal pFirst,
                                      final TethysDecimal pSecond) {
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
    private void formatAddition(final MoneyWiseXAnalysisSecurityAttr pAttr,
                                final Object pValue,
                                final TethysDecimal pFirst,
                                final TethysDecimal pSecond) {
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
    private void formatSubtraction(final MoneyWiseXAnalysisSecurityAttr pAttr,
                                   final Object pValue,
                                   final TethysDecimal pFirst,
                                   final TethysDecimal pSecond) {
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
     * @param pEvent the event
     * @param pValues the values for the transaction
     */
    private void formatTransfer(final MoneyWiseXAnalysisEvent pEvent,
                                final MoneyWiseXAnalysisSecurityValues pValues) {
        /* Format the basic transaction */
        formatBasicTransaction(pEvent);

        /* Split workings for transfer in/out */
        if (isDebit(pEvent)) {
            formatTransferOut(pEvent, pValues);
        } else {
            formatTransferIn(pEvent, pValues);
        }
    }

    /**
     * Format a Dividend.
     * @param pEvent the event
     * @param pValues the values for the transaction
     */
    private void formatDividend(final MoneyWiseXAnalysisEvent pEvent,
                                final MoneyWiseXAnalysisSecurityValues pValues) {
        /* If this is a dividend re-investment */
        if (isCredit(pEvent)) {
            /* Format the basic transaction */
            formatBasicTransaction(pEvent);

            /* Deal as investment */
            formatTransferIn(pEvent, pValues);
        }
    }

    /**
     * Format transfer money in.
     * @param pEvent the event
     * @param pValues the values for the transaction
     */
    private void formatTransferIn(final MoneyWiseXAnalysisEvent pEvent,
                                  final MoneyWiseXAnalysisSecurityValues pValues) {

        /* Access interesting values */
        final MoneyWiseTransaction myTrans = pEvent.getTransaction();
        final TethysUnits myUnits = pValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        TethysUnits myDeltaUnits = myTrans.getAccountDeltaUnits();
        if (myDeltaUnits == null) {
            myDeltaUnits = myTrans.getPartnerDeltaUnits();
        }
        final TethysMoney myCost = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        final TethysMoney myAmount = theSecurity.getMoneyDeltaForEvent(pEvent, MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        final TethysPrice myPrice = pValues.getPriceValue(MoneyWiseXAnalysisSecurityAttr.PRICE);
        final TethysRatio myXchangeRate = pValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE);

        /* Obtain the original units/cost */
        final MoneyWiseXAnalysisSecurityValues myPreviousValues = theSecurity.getPreviousValuesForEvent(pEvent);
        final TethysUnits myOriginalUnits = myPreviousValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        final TethysMoney myOriginalCost = myPreviousValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);

        /* If this is an inheritance */
        //if (myTrans.isCategoryClass(MoneyWiseTransCategoryClass.INHERITED)) {
        //    formatValuation(MoneyWiseXAnalysisSecurityAttr.INVESTED, myAmount, myDeltaUnits, myPrice, myXchangeRate);
        //} else {
        //    formatValue(MoneyWiseXAnalysisSecurityAttr.INVESTED, myAmount);
        //}

        /* Record the details */
        if (myDeltaUnits != null) {
            formatAddition(MoneyWiseXAnalysisSecurityAttr.UNITS, myUnits, myOriginalUnits, myDeltaUnits);
        }
        formatAddition(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST, myCost, myOriginalCost, myAmount);
    }

    /**
     * Format transfer money out.
     * @param pEvent the event
     * @param pValues the values for the transaction
     */
    private void formatTransferOut(final MoneyWiseXAnalysisEvent pEvent,
                                   final MoneyWiseXAnalysisSecurityValues pValues) {
        /* Access interesting values */
        final TethysMoney myGain = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.CAPITALGAIN);
        final TethysMoney myAllowedCost = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.ALLOWEDCOST);
        final TethysRatio myCostDilution = pValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.COSTDILUTION);
        final TethysMoney myTotalGains = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS);
        final TethysMoney myCost = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        final TethysUnits myUnits = pValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        final TethysMoney myCash = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RETURNEDCASH);
        final TethysMoney myConsideration = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.CONSIDERATION);
        final MoneyWiseCashType myCashType = pValues.getEnumValue(MoneyWiseXAnalysisSecurityAttr.CASHTYPE, MoneyWiseCashType.class);

        /* Obtain the original values */
        final MoneyWiseXAnalysisSecurityValues myPreviousValues = theSecurity.getPreviousValuesForEvent(pEvent);
        final TethysMoney myOriginalCost = myPreviousValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        final TethysUnits myOriginalUnits = myPreviousValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);

        /* Obtain the delta in units/money */
        TethysUnits myDeltaUnits = theSecurity.getUnitsDeltaForEvent(pEvent, MoneyWiseXAnalysisSecurityAttr.UNITS);
        final TethysMoney myAmount = new TethysMoney(myCash);
        myAmount.negate();

        /* Report the returned cash */
        formatValue(MoneyWiseXAnalysisSecurityAttr.RETURNEDCASH, myCash);
        if (myCashType != null) {
            formatValue(MoneyWiseXAnalysisSecurityAttr.CASHTYPE, myCashType);
        }

        /* If we have changed the number of units */
        if (myDeltaUnits.isNonZero()) {
            /* Obtain the various values */
            myDeltaUnits = new TethysUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Format the units */
            formatSubtraction(MoneyWiseXAnalysisSecurityAttr.UNITS, myUnits, myOriginalUnits, myDeltaUnits);

            /* Format the dilution */
            formatDivision(MoneyWiseXAnalysisSecurityAttr.COSTDILUTION, myCostDilution, myUnits, myOriginalUnits);

            /* Else we need to format the cost dilution */
        } else if (myConsideration != null) {
            /* Format the valuation */
            final TethysMoney myValuation = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
            final TethysPrice myPrice = pValues.getPriceValue(MoneyWiseXAnalysisSecurityAttr.PRICE);
            final TethysRatio myXchangeRate = pValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE);
            formatValuation(MoneyWiseXAnalysisSecurityAttr.VALUATION, myValuation, myUnits, myPrice, myXchangeRate);
            formatAddition(MoneyWiseXAnalysisSecurityAttr.CONSIDERATION, myConsideration, myCash, myValuation);

            /* Format the dilution */
            formatDivision(MoneyWiseXAnalysisSecurityAttr.COSTDILUTION, myCostDilution, myValuation, myConsideration);
        }

        /* Record the details */
        if (myCostDilution != null) {
            formatMultiplication(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST, myCost, myOriginalCost, myCostDilution);
            formatSubtraction(MoneyWiseXAnalysisSecurityAttr.ALLOWEDCOST, myAllowedCost, myOriginalCost, myCost);
        } else {
            formatValue(MoneyWiseXAnalysisSecurityAttr.ALLOWEDCOST, myAllowedCost);
            formatSubtraction(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST, myCost, myOriginalCost, myAllowedCost);
        }

        /* Record the gains allocation */
        if (myGain != null) {
            formatSubtraction(MoneyWiseXAnalysisSecurityAttr.CAPITALGAIN, myGain, myCash, myAllowedCost);
            formatValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS, myTotalGains);
        }
    }

    /**
     * Format a Units Adjustment.
     * @param pEvent the event
     * @param pValues the values for the transaction
     */
    private void formatUnitsAdjust(final MoneyWiseXAnalysisEvent pEvent,
                                   final MoneyWiseXAnalysisSecurityValues pValues) {
        /* Format the basic transaction */
        final MoneyWiseTransaction myTrans = pEvent.getTransaction();
        formatBasicTransaction(pEvent);

        /* Access interesting values */
        final TethysUnits myUnits = pValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        TethysUnits myDeltaUnits = myTrans.getAccountDeltaUnits();

        /* Obtain the original units */
        final MoneyWiseXAnalysisSecurityValues myPreviousValues = theSecurity.getPreviousValuesForEvent(pEvent);
        final TethysUnits myOriginalUnits = myPreviousValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);

        /* Record the details */
        if (myDeltaUnits.isPositive()) {
            formatAddition(MoneyWiseXAnalysisSecurityAttr.UNITS, myUnits, myOriginalUnits, myDeltaUnits);
        } else {
            myDeltaUnits = new TethysUnits(myDeltaUnits);
            myDeltaUnits.negate();
            formatSubtraction(MoneyWiseXAnalysisSecurityAttr.UNITS, myUnits, myOriginalUnits, myDeltaUnits);
        }
    }

    /**
     * Format a Stock DeMerger.
     * @param pEvent the event
     * @param pValues the values for the transaction
     */
    private void formatStockDeMerger(final MoneyWiseXAnalysisEvent pEvent,
                                     final MoneyWiseXAnalysisSecurityValues pValues) {
        /* Format the basic transaction */
        formatBasicTransaction(pEvent);

        /* Split workings for credit and debit */
        if (isDebit(pEvent)) {
            formatDebitStockDeMerger(pEvent, pValues);
        } else {
            formatCreditStockDeMerger(pEvent, pValues);
        }
    }

    /**
     * Format debit side of a Stock DeMerger.
     * @param pEvent the event
     * @param pValues the values for the transaction
     */
    private void formatDebitStockDeMerger(final MoneyWiseXAnalysisEvent pEvent,
                                          final MoneyWiseXAnalysisSecurityValues pValues) {
        /* Access interesting values */
        final TethysRatio myCostDilution = pValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.COSTDILUTION);
        final TethysMoney myResidualCost = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        final TethysMoney myXferredCost = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST);
        TethysUnits myDeltaUnits = theSecurity.getUnitsDeltaForEvent(pEvent, MoneyWiseXAnalysisSecurityAttr.UNITS);

        /* Check whether the units have changed */
        final boolean isDeltaUnits = myDeltaUnits.isNonZero();

        /* Obtain the original cost */
        final MoneyWiseXAnalysisSecurityValues myPreviousValues = theSecurity.getPreviousValuesForEvent(pEvent);
        final TethysMoney myOriginalCost = myPreviousValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);

        /* If we have changed the number of units */
        if (isDeltaUnits) {
            /* Obtain the various values */
            final TethysUnits myOriginalUnits = myPreviousValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
            final TethysUnits myUnits = pValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
            myDeltaUnits = new TethysUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Format the units/dilution */
            formatSubtraction(MoneyWiseXAnalysisSecurityAttr.UNITS, myUnits, myOriginalUnits, myDeltaUnits);
            formatDivision(MoneyWiseXAnalysisSecurityAttr.COSTDILUTION, myCostDilution, myUnits, myOriginalUnits);

            /* else just report the dilution */
        } else {
            formatValue(MoneyWiseXAnalysisSecurityAttr.COSTDILUTION, myCostDilution);
        }

        /* Record the details */
        formatMultiplication(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST, myResidualCost, myOriginalCost, myCostDilution);
        formatSubtraction(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST, myXferredCost, myOriginalCost, myResidualCost);
    }

    /**
     * Format credit side of a Stock DeMerger.
     * @param pEvent the event
     * @param pValues the values for the transaction
     */
    private void formatCreditStockDeMerger(final MoneyWiseXAnalysisEvent pEvent,
                                           final MoneyWiseXAnalysisSecurityValues pValues) {
        /* Access interesting values */
        final TethysMoney myResidualCost = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        final TethysMoney myXferredCost = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST);
        final TethysMoney myValueXfer = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE);
        final TethysUnits myUnits = theSecurity.getUnitsDeltaForEvent(pEvent, MoneyWiseXAnalysisSecurityAttr.UNITS);
        final TethysPrice myPrice = pValues.getPriceValue(MoneyWiseXAnalysisSecurityAttr.PRICE);
        final TethysRatio myXchangeRate = pValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE);

        /* Record the details */
        formatValuation(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE, myValueXfer, myUnits, myPrice, myXchangeRate);
        formatValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST, myXferredCost);
        formatValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST, myResidualCost);
    }

    /**
     * Format a Stock TakeOver.
     * @param pEvent the event
     * @param pValues the values for the transaction
     */
    private void formatStockTakeOver(final MoneyWiseXAnalysisEvent pEvent,
                                     final MoneyWiseXAnalysisSecurityValues pValues) {
        /* Format the basic transaction */
        formatBasicTransaction(pEvent);

        /* Split out Stock and Cash TakeOver */
        final TethysMoney myCash = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RETURNEDCASH);
        if (myCash != null) {
            formatStockAndCashTakeOver(pEvent, pValues, myCash);

            /* Split workings for credit and debit */
        } else if (isDebit(pEvent)) {
            /* Record the transfer of cost for simple replacement takeOver */
            final TethysMoney myCostXfer = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST);
            formatValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST, myCostXfer);
        } else {
            formatCreditStockTakeOver(pEvent, pValues);
        }
    }

    /**
     * Format a StockAndCash TakeOver.
     * @param pEvent the event
     * @param pValues the values for the transaction
     * @param pCash the cash consideration
     */
    private void formatStockAndCashTakeOver(final MoneyWiseXAnalysisEvent pEvent,
                                            final MoneyWiseXAnalysisSecurityValues pValues,
                                            final TethysMoney pCash) {
        /* Split workings for credit and debit */
        if (isDebit(pEvent)) {
            formatDebitStockAndCashTakeOver(pEvent, pValues, pCash);
        } else {
            formatCreditStockTakeOver(pEvent, pValues);
        }
    }

    /**
     * Format debit side of a StockAndCash TakeOver.
     * @param pEvent the event
     * @param pValues the values for the transaction
     * @param pCash the cash consideration
     */
    private void formatDebitStockAndCashTakeOver(final MoneyWiseXAnalysisEvent pEvent,
                                                 final MoneyWiseXAnalysisSecurityValues pValues,
                                                 final TethysMoney pCash) {
        /* Access interesting values */
        final TethysMoney myStock = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE);
        final TethysMoney myConsideration = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.CONSIDERATION);
        final TethysMoney myCostXfer = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST);
        final TethysRatio myCostDilution = pValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.COSTDILUTION);
        final TethysMoney myAllowedCost = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.ALLOWEDCOST);
        final TethysMoney myGain = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.CAPITALGAIN);
        final TethysMoney myTotalGains = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS);
        final MoneyWiseCashType myCashType = pValues.getEnumValue(MoneyWiseXAnalysisSecurityAttr.CASHTYPE, MoneyWiseCashType.class);

        /* Record the calculation of total consideration */
        formatValue(MoneyWiseXAnalysisSecurityAttr.RETURNEDCASH, pCash);
        formatValue(MoneyWiseXAnalysisSecurityAttr.CASHTYPE, myCashType);
        formatValue(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE, myStock);
        formatAddition(MoneyWiseXAnalysisSecurityAttr.CONSIDERATION, myConsideration, pCash, myStock);

        /* Obtain the original cost */
        final MoneyWiseXAnalysisSecurityValues myPreviousValues = theSecurity.getPreviousValuesForEvent(pEvent);
        final TethysMoney myOriginalCost = myPreviousValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);

        /* Format the cost dilution */
        if (myCostDilution != null) {
            formatDivision(MoneyWiseXAnalysisSecurityAttr.COSTDILUTION, myCostDilution, pCash, myConsideration);
            formatMultiplication(MoneyWiseXAnalysisSecurityAttr.ALLOWEDCOST, myAllowedCost, myOriginalCost, myCostDilution);
        } else {
            formatValue(MoneyWiseXAnalysisSecurityAttr.ALLOWEDCOST, myAllowedCost);
        }
        formatSubtraction(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST, myCostXfer, myOriginalCost, myAllowedCost);

        /* Record the gains allocation */
        if (myGain != null) {
            formatSubtraction(MoneyWiseXAnalysisSecurityAttr.CAPITALGAIN, myGain, pCash, myAllowedCost);
            formatValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS, myTotalGains);
        }
    }

    /**
     * Format credit side of a StockAndCash TakeOver.
     * @param pEvent the event
     * @param pValues the values for the transaction
     */
    private void formatCreditStockTakeOver(final MoneyWiseXAnalysisEvent pEvent,
                                           final MoneyWiseXAnalysisSecurityValues pValues) {
        /* Access interesting values */
        final TethysPrice myPrice = pValues.getPriceValue(MoneyWiseXAnalysisSecurityAttr.PRICE);
        final TethysUnits myUnits = theSecurity.getUnitsDeltaForEvent(pEvent, MoneyWiseXAnalysisSecurityAttr.UNITS);
        final TethysMoney myValueXfer = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE);
        final TethysMoney myCostXfer = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST);
        final TethysMoney myResidualCost = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        final TethysRatio myXchangeRate = pValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE);

        /* Detail the new units and cost */
        final MoneyWiseXAnalysisSecurityValues myPreviousValues = theSecurity.getPreviousValuesForEvent(pEvent);
        final TethysUnits myNewUnits = pValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        final TethysUnits myOriginalUnits = myPreviousValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        formatAddition(MoneyWiseXAnalysisSecurityAttr.UNITS, myNewUnits, myOriginalUnits, myUnits);

        /* Record the transfer of value and cost */
        formatValuation(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE, myValueXfer, myUnits, myPrice, myXchangeRate);
        formatValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST, myCostXfer);
        formatValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST, myResidualCost);
    }

    /**
     * Format a Portfolio Xfer.
     * @param pEvent the event
     * @param pValues the values for the transaction
     */
    private void formatPortfolioXfer(final MoneyWiseXAnalysisEvent pEvent,
                                     final MoneyWiseXAnalysisSecurityValues pValues) {
        /* Format the basic transaction */
        formatBasicTransaction(pEvent);

        /* Determine the direction of transfer */
        final TethysMoney myCostXfer = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST);
        formatValue(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST, myCostXfer);

        final TethysUnits myUnits = theSecurity.getUnitsDeltaForEvent(pEvent, MoneyWiseXAnalysisSecurityAttr.UNITS);
        if (myUnits.isPositive()) {
            /* Detail the new units and cost */
            final MoneyWiseXAnalysisSecurityValues myPreviousValues = theSecurity.getPreviousValuesForEvent(pEvent);
            final TethysUnits myNewUnits = pValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
            final TethysUnits myOriginalUnits = myPreviousValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
            formatAddition(MoneyWiseXAnalysisSecurityAttr.UNITS, myNewUnits, myOriginalUnits, myUnits);
            final TethysMoney myCost = pValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
            final TethysMoney myOriginalCost = myPreviousValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
            formatAddition(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST, myCost, myOriginalCost, myCostXfer);
        }
    }

    @Override
    public MoneyWiseXAnalysisFilter<?, ?> processFilter(final Object pSource) {
        return null;
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        return null;
    }
}
