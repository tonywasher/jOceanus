/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.analysis.data;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseAnalysisBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseAnalysisValues;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Account Bucket.
 */
public class MoneyWisePortfolioBucket
        extends MoneyWiseAnalysisBucket<Portfolio, MoneyWiseAccountAttr> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWisePortfolioBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePortfolioBucket.class);

    /**
     * Constructor.
     * @param pPortfolio the portfolio account
     * @param pAnalysis the analysis
     */
    MoneyWisePortfolioBucket(final Portfolio pPortfolio,
                             final MoneyWiseAnalysis pAnalysis) {
        super(pPortfolio, initialValues(pPortfolio, pAnalysis));
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pDate the end date.
     */
    private MoneyWisePortfolioBucket(final MoneyWisePortfolioBucket pBase,
                                     final TethysDate pDate) {
        super(pBase, pDate);
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pRange the date range.
     */
    private MoneyWisePortfolioBucket(final MoneyWisePortfolioBucket pBase,
                                     final TethysDateRange pRange) {
        super(pBase, pRange);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean isActive() {
        return getValues().getMoneyValue(MoneyWiseAccountAttr.VALUATION).isNonZero();
    }

    @Override
    public MoneyWisePortfolioBucket newBucket(final TethysDate pDate) {
        return new MoneyWisePortfolioBucket(this, pDate);
    }

    @Override
    public MoneyWisePortfolioBucket newBucket(final TethysDateRange pRange) {
        return new MoneyWisePortfolioBucket(this, pRange);
    }

    /**
     * Create a new set of values for a portfolio account.
     * @param pPortfolio the portfolio account
     * @param pAnalysis the analysis
     * @return the initial values
     */
    private static MoneyWiseAnalysisValues<MoneyWiseAccountAttr> initialValues(final Portfolio pPortfolio,
                                                                               final MoneyWiseAnalysis pAnalysis) {
        return new MoneyWiseAnalysisValues<>(MoneyWiseAccountAttr.class, pPortfolio.getAssetCurrency(), pAnalysis.getReportingCurrency());
    }
}
