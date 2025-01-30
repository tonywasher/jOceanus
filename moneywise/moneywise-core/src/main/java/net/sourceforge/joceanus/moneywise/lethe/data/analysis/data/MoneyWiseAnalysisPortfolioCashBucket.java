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
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.data;

import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

/**
 * The Portfolio Cash Bucket class.
 */
public final class MoneyWiseAnalysisPortfolioCashBucket
        extends MoneyWiseAnalysisAccountBucket<MoneyWisePortfolio> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisPortfolioCashBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisPortfolioCashBucket.class);

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pPortfolio the portfolio
     */
    MoneyWiseAnalysisPortfolioCashBucket(final MoneyWiseAnalysis pAnalysis,
                                         final MoneyWisePortfolio pPortfolio) {
        /* Call super-constructor */
        super(pAnalysis, pPortfolio);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     */
    MoneyWiseAnalysisPortfolioCashBucket(final MoneyWiseAnalysis pAnalysis,
                                         final MoneyWiseAnalysisPortfolioCashBucket pBase) {
        /* Call super-constructor */
        super(pAnalysis, pBase);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    MoneyWiseAnalysisPortfolioCashBucket(final MoneyWiseAnalysis pAnalysis,
                                         final MoneyWiseAnalysisPortfolioCashBucket pBase,
                                         final OceanusDate pDate) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pDate);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    MoneyWiseAnalysisPortfolioCashBucket(final MoneyWiseAnalysis pAnalysis,
                                         final MoneyWiseAnalysisPortfolioCashBucket pBase,
                                         final OceanusDateRange pRange) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pRange);
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisPortfolioCashBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Adjust account for transfer.
     * @param pSource the source portfolio
     * @param pTrans the transaction helper
     */
    public void adjustForXfer(final MoneyWiseAnalysisPortfolioCashBucket pSource,
                              final MoneyWiseAnalysisTransactionHelper pTrans) {
        /* Access transfer amount */
        final MoneyWiseAnalysisAccountValues myValues = pSource.getValues();
        OceanusMoney myAmount = myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION);

        /* Adjust this valuation */
        adjustCounter(MoneyWiseAnalysisAccountAttr.VALUATION, myAmount);
        registerTransaction(pTrans);

        /* Adjust source valuation */
        myAmount = new OceanusMoney(myAmount);
        myAmount.negate();
        pSource.adjustCounter(MoneyWiseAnalysisAccountAttr.VALUATION, myAmount);
        pSource.registerTransaction(pTrans);
    }

    /**
     * Add Values.
     * @param pBucket the portfolio cash bucket
     */
    void addValues(final MoneyWiseAnalysisPortfolioCashBucket pBucket) {
        /* Add values */
        addValues(getValues(), pBucket.getValues());

        /* Add base values */
        addValues(getBaseValues(), pBucket.getBaseValues());
    }

    /**
     * Add bucket to totals.
     * @param pTotals the totals
     * @param pSource the values to add
     */
    private static void addValues(final MoneyWiseAnalysisAccountValues pTotals,
                                  final MoneyWiseAnalysisAccountValues pSource) {
        /* Add valuation values */
        OceanusMoney myValue = pTotals.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION);
        myValue = new OceanusMoney(myValue);
        final OceanusMoney mySrcValue = pSource.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION);
        myValue.addAmount(mySrcValue);
        pTotals.setValue(MoneyWiseAnalysisAccountAttr.VALUATION, myValue);
    }
}
