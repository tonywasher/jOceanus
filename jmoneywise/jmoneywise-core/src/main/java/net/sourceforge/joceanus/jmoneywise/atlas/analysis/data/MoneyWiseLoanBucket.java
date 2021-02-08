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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Loan Bucket.
 */
public class MoneyWiseLoanBucket
        extends MoneyWiseAnalysisBucket<Loan, MoneyWiseAccountAttr> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseLoanBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseLoanBucket.class);

    /**
     * Constructor.
     * @param pLoan the loan account
     * @param pAnalysis the analysis
     */
    MoneyWiseLoanBucket(final Loan pLoan,
                        final MoneyWiseAnalysis pAnalysis) {
        super(pLoan, initialValues(pLoan, pAnalysis));
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pDate the end date.
     */
    private MoneyWiseLoanBucket(final MoneyWiseLoanBucket pBase,
                                final TethysDate pDate) {
        super(pBase, pDate);
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pRange the date range.
     */
    private MoneyWiseLoanBucket(final MoneyWiseLoanBucket pBase,
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
    public MoneyWiseLoanBucket newBucket(final TethysDate pDate) {
        return new MoneyWiseLoanBucket(this, pDate);
    }

    @Override
    public MoneyWiseLoanBucket newBucket(final TethysDateRange pRange) {
        return new MoneyWiseLoanBucket(this, pRange);
    }

    /**
     * Create a new set of values for a loan account.
     * @param pLoan the loan account
     * @param pAnalysis the analysis
     * @return the initial values
     */
    private static MoneyWiseAnalysisValues<MoneyWiseAccountAttr> initialValues(final Loan pLoan,
                                                                               final MoneyWiseAnalysis pAnalysis) {
        return new MoneyWiseAnalysisValues<>(MoneyWiseAccountAttr.class, pLoan.getAssetCurrency(), pAnalysis.getReportingCurrency());
    }
}

