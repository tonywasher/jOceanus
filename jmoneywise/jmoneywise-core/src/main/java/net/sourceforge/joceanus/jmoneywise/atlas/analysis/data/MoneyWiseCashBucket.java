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

import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseAnalysisBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseAnalysisValues;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Cash Bucket.
 */
public class MoneyWiseCashBucket
        extends MoneyWiseAnalysisBucket<Cash, MoneyWiseAccountAttr> {
    /**
     * Constructor.
     * @param pCash the cash account
     * @param pAnalysis the analysis
     */
    MoneyWiseCashBucket(final Cash pCash,
                        final MoneyWiseAnalysis pAnalysis) {
        super(pCash, initialValues(pCash, pAnalysis));
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pDate the end date.
     */
    private MoneyWiseCashBucket(final MoneyWiseCashBucket pBase,
                                final TethysDate pDate) {
        super(pBase, pDate);
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pRange the date range.
     */
    private MoneyWiseCashBucket(final MoneyWiseCashBucket pBase,
                                final TethysDateRange pRange) {
        super(pBase, pRange);
    }

    @Override
    public boolean isActive() {
        return getValues().getMoneyValue(MoneyWiseAccountAttr.VALUATION).isNonZero();
    }

    @Override
    public MoneyWiseCashBucket newBucket(final TethysDate pDate) {
        return new MoneyWiseCashBucket(this, pDate);
    }

    @Override
    public MoneyWiseCashBucket newBucket(final TethysDateRange pRange) {
        return new MoneyWiseCashBucket(this, pRange);
    }

    /**
     * Create a new set of values for a cash account.
     * @param pCash the cash account
     * @param pAnalysis the analysis
     * @return the initial values
     */
    private static MoneyWiseAnalysisValues<MoneyWiseAccountAttr> initialValues(final AssetBase<?> pCash,
                                                                               final MoneyWiseAnalysis pAnalysis) {
        return new MoneyWiseAnalysisValues<>(MoneyWiseAccountAttr.class, pCash.getAssetCurrency(), pAnalysis.getReportingCurrency());
    }
}
