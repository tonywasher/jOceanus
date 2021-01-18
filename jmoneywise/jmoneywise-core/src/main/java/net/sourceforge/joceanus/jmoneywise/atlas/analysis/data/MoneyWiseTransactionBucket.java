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
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Transaction Bucket.
 */
public class MoneyWiseTransactionBucket
        extends MoneyWiseAnalysisBucket<TransactionCategory, MoneyWiseIncomeAttr> {
    /**
     * Constructor.
     * @param pCategory the transaction category
     * @param pAnalysis the analysis
     */
    MoneyWiseTransactionBucket(final TransactionCategory pCategory,
                               final MoneyWiseAnalysis pAnalysis) {
        super(pCategory, initialValues(pAnalysis));
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pDate the end date.
     */
    private MoneyWiseTransactionBucket(final MoneyWiseTransactionBucket pBase,
                                       final TethysDate pDate) {
        super(pBase, pDate);
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pRange the date range.
     */
    private MoneyWiseTransactionBucket(final MoneyWiseTransactionBucket pBase,
                                       final TethysDateRange pRange) {
        super(pBase, pRange);
    }

    @Override
    public boolean isActive() {
        final MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> myValues = getValues();
        final TethysMoney myIncome = myValues.getMoneyValue(MoneyWiseIncomeAttr.INCOME);
        final TethysMoney myExpense = myValues.getMoneyValue(MoneyWiseIncomeAttr.EXPENSE);
        return myIncome.isNonZero() || myExpense.isNonZero();
    }

    @Override
    public MoneyWiseTransactionBucket newBucket(final TethysDate pDate) {
        return new MoneyWiseTransactionBucket(this, pDate);
    }

    @Override
    public MoneyWiseTransactionBucket newBucket(final TethysDateRange pRange) {
        return new MoneyWiseTransactionBucket(this, pRange);
    }

    /**
     * Create a new set of values for a payee.
     * @param pAnalysis the analysis
     * @return the initial values
     */
    private static MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> initialValues(final MoneyWiseAnalysis pAnalysis) {
        return new MoneyWiseAnalysisValues<>(MoneyWiseIncomeAttr.class, pAnalysis.getReportingCurrency());
    }
}
