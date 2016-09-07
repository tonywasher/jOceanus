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
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Gains Analysis Report.
 */
public class GainsAnalysis {
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
            if ((myValues != null)
                && !myTrans.getCategoryClass().isDividend()) {
                /* Format the details */
                formatDetails(myTrans, myValues);
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
    private void formatDetails(final Transaction pTrans,
                               final SecurityValues pValues) {
        /* Format the transaction */
        theBuilder.append("\n--------\n");
        theBuilder.append(theFormatter.formatObject(pTrans.getDate()));
        theBuilder.append(": ");
        theBuilder.append(pTrans);
        theBuilder.append('\n');

        /* Switch on the class */
        switch (pTrans.getCategoryClass()) {
            case SECURITYREPLACE:
            case STOCKTAKEOVER:
                formatStockTakeOver(pValues);
                break;
            case STOCKDEMERGER:
                formatStockDeMerger(pValues);
                break;
            default:
                formatTransaction(pValues);
                break;
        }
    }

    /**
     * Format a Stock DeMerger.
     * @param pValues the values for the transaction
     */
    private void formatTransaction(final SecurityValues pValues) {
        /* For each of the security attributes */
        for (SecurityAttribute myAttr : SecurityAttribute.values()) {
            /* If we are interested in this value */
            Object myValue = pValues.getValue(myAttr);
            if (showAttr(myAttr, myValue)) {
                /* Format the entry */
                theBuilder.append(myAttr);
                theBuilder.append('=');
                theBuilder.append(theFormatter.formatObject(myValue));
                theBuilder.append('\n');
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
     * Format a value.
     * @param pAttr the attribute
     * @param pValue the value
     * @param pNewLine ass newline (true/false)
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
     * Format a ratio.
     * @param pNumerator the numerator
     * @param pValue the divisor
     * @param pNewLine ass newline (true/false)
     */
    private void formatRatio(final Object pNumerator,
                             final Object pDivisor,
                             final boolean pNewLine) {
        theBuilder.append(" (");
        theBuilder.append(theFormatter.formatObject(pNumerator));
        theBuilder.append('/');
        theBuilder.append(theFormatter.formatObject(pDivisor));
        theBuilder.append(')');
        if (pNewLine) {
            formatNewLine();
        }
    }

    /**
     * Format a newLine.
     */
    private void formatNewLine() {
        theBuilder.append('\n');
    }

    /**
     * Format a Stock DeMerger.
     * @param pValues the values for the transaction
     */
    private void formatStockDeMerger(final SecurityValues pValues) {
        /* Access interesting values */
        TethysRatio myCostDilution = pValues.getRatioValue(SecurityAttribute.COSTDILUTION);
        TethysMoney myResidualCost = pValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);
        TethysMoney myXferredCost = pValues.getMoneyValue(SecurityAttribute.XFERREDCOST);
        TethysUnits myUnits = pValues.getUnitsValue(SecurityAttribute.UNITS);

        /* Record the details */
        formatValue(SecurityAttribute.UNITS, myUnits, true);
        formatValue(SecurityAttribute.COSTDILUTION, myCostDilution, true);
        formatValue(SecurityAttribute.XFERREDCOST, myXferredCost, true);
        formatValue(SecurityAttribute.RESIDUALCOST, myResidualCost, true);
    }

    /**
     * Format a Stock TakeOver.
     * @param pValues the values for the transaction
     */
    private void formatStockTakeOver(final SecurityValues pValues) {
        /* Split out Stock and Cash TakeOver */
        TethysMoney myCash = pValues.getMoneyValue(SecurityAttribute.CASHCONSIDERATION);
        if (myCash != null) {
            formatStockAndCashTakeOver(pValues, myCash);
            return;
        }

        /* Record the transfer of cost */
        TethysMoney myCostXfer = pValues.getMoneyValue(SecurityAttribute.XFERREDCOST);
        formatValue(SecurityAttribute.XFERREDCOST, myCostXfer, true);
    }

    /**
     * Format a StockAndCash TakeOver.
     * @param pValues the values for the transaction
     * @param pCash the cash consideration
     */
    private void formatStockAndCashTakeOver(final SecurityValues pValues,
                                            final TethysMoney pCash) {
        /* Access interesting values */
        TethysMoney myCash = pValues.getMoneyValue(SecurityAttribute.CASHCONSIDERATION);
        TethysMoney myStock = pValues.getMoneyValue(SecurityAttribute.STOCKCONSIDERATION);
        TethysMoney myConsideration = pValues.getMoneyValue(SecurityAttribute.CONSIDERATION);
        TethysMoney myCostXfer = pValues.getMoneyValue(SecurityAttribute.XFERREDCOST);
        TethysRatio myCostDilution = pValues.getRatioValue(SecurityAttribute.COSTDILUTION);
        TethysMoney myAllowedCost = pValues.getMoneyValue(SecurityAttribute.ALLOWEDCOST);
        TethysMoney myGain = pValues.getMoneyValue(SecurityAttribute.CAPITALGAIN);
        TethysMoney myTotalGains = pValues.getMoneyValue(SecurityAttribute.REALISEDGAINS);

        /* Record the calculation of cost split */
        formatValue(SecurityAttribute.CONSIDERATION, myConsideration, true);
        formatValue(SecurityAttribute.STOCKCONSIDERATION, myStock, true);
        formatValue(SecurityAttribute.COSTDILUTION, myCostDilution, false);
        formatRatio(myStock, myConsideration, true);

        /* Record the cost allocation */
        formatValue(SecurityAttribute.XFERREDCOST, myCostXfer, true);
        formatValue(SecurityAttribute.ALLOWEDCOST, myAllowedCost, true);
        formatValue(SecurityAttribute.CAPITALGAIN, myGain, true);
        formatValue(SecurityAttribute.CASHCONSIDERATION, myCash, true);
        formatValue(SecurityAttribute.REALISEDGAINS, myTotalGains, true);
    }
}
