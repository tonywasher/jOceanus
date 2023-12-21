/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseXAnalysisBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseXAnalysisValues;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Cash Bucket.
 */
public class MoneyWiseXCashBucket
        extends MoneyWiseXAnalysisBucket<Cash, MoneyWiseXAccountAttr> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXCashBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXCashBucket.class);

    /**
     * Constructor.
     * @param pCash the cash account
     * @param pAnalysis the analysis
     */
    MoneyWiseXCashBucket(final Cash pCash,
                         final MoneyWiseXAnalysis pAnalysis) {
        super(pCash, initialValues(pCash, pAnalysis));
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pDate the end date.
     */
    private MoneyWiseXCashBucket(final MoneyWiseXCashBucket pBase,
                                 final TethysDate pDate) {
        super(pBase, pDate);
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pRange the date range.
     */
    private MoneyWiseXCashBucket(final MoneyWiseXCashBucket pBase,
                                 final TethysDateRange pRange) {
        super(pBase, pRange);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean isActive() {
        return getValues().getMoneyValue(MoneyWiseXAccountAttr.VALUATION).isNonZero();
    }

    @Override
    public MoneyWiseXCashBucket newBucket(final TethysDate pDate) {
        return new MoneyWiseXCashBucket(this, pDate);
    }

    @Override
    public MoneyWiseXCashBucket newBucket(final TethysDateRange pRange) {
        return new MoneyWiseXCashBucket(this, pRange);
    }

    /**
     * Create a new set of values for a cash account.
     * @param pCash the cash account
     * @param pAnalysis the analysis
     * @return the initial values
     */
    private static MoneyWiseXAnalysisValues<MoneyWiseXAccountAttr> initialValues(final AssetBase pCash,
                                                                                 final MoneyWiseXAnalysis pAnalysis) {
        return new MoneyWiseXAnalysisValues<>(MoneyWiseXAccountAttr.class, pCash.getAssetCurrency(), pAnalysis.getReportingCurrency());
    }
}
