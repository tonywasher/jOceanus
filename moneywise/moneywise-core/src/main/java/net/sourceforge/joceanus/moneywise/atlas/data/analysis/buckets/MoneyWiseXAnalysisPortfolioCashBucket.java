/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;

/**
 * The Portfolio Cash Bucket class.
 */
public final class MoneyWiseXAnalysisPortfolioCashBucket
        extends MoneyWiseXAnalysisAccountBucket<MoneyWisePortfolio> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisPortfolioCashBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisPortfolioCashBucket.class);

    /**
     * Constructor.
     *
     * @param pAnalysis  the analysis
     * @param pPortfolio the portfolio
     */
    MoneyWiseXAnalysisPortfolioCashBucket(final MoneyWiseXAnalysis pAnalysis,
                                          final MoneyWisePortfolio pPortfolio) {
        /* Call super-constructor */
        super(pAnalysis, pPortfolio);
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     */
    MoneyWiseXAnalysisPortfolioCashBucket(final MoneyWiseXAnalysis pAnalysis,
                                          final MoneyWiseXAnalysisPortfolioCashBucket pBase) {
        /* Call super-constructor */
        super(pAnalysis, pBase);
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     * @param pDate     the date for the bucket
     */
    MoneyWiseXAnalysisPortfolioCashBucket(final MoneyWiseXAnalysis pAnalysis,
                                          final MoneyWiseXAnalysisPortfolioCashBucket pBase,
                                          final OceanusDate pDate) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pDate);
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     * @param pRange    the range for the bucket
     */
    MoneyWiseXAnalysisPortfolioCashBucket(final MoneyWiseXAnalysis pAnalysis,
                                          final MoneyWiseXAnalysisPortfolioCashBucket pBase,
                                          final OceanusDateRange pRange) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pRange);
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisPortfolioCashBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Long getBucketId() {
        return getAccount().getExternalId();
    }

    /**
     * Add Values.
     *
     * @param pBucket the portfolio cash bucket
     */
    void addValues(final MoneyWiseXAnalysisPortfolioCashBucket pBucket) {
        /* Add values */
        addValues(getValues(), pBucket.getValues());

        /* Add base values */
        addValues(getBaseValues(), pBucket.getBaseValues());
    }

    /**
     * Add bucket to totals.
     *
     * @param pTotals the totals
     * @param pSource the values to add
     */
    private static void addValues(final MoneyWiseXAnalysisAccountValues pTotals,
                                  final MoneyWiseXAnalysisAccountValues pSource) {
        /* Add valuation values */
        OceanusMoney myValue = pTotals.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
        myValue = new OceanusMoney(myValue);
        final OceanusMoney mySrcValue = pSource.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
        myValue.addAmount(mySrcValue);
        pTotals.setValue(MoneyWiseXAnalysisAccountAttr.VALUATION, myValue);
    }
}
