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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Payee Bucket.
 */
public class MoneyWiseXPayeeBucket
        extends MoneyWiseXAnalysisBucket<Payee, MoneyWiseXIncomeAttr> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXPayeeBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXPayeeBucket.class);

    /**
     * Constructor.
     * @param pPayee the payee
     * @param pAnalysis the analysis
     */
    MoneyWiseXPayeeBucket(final Payee pPayee,
                          final MoneyWiseXAnalysis pAnalysis) {
        super(pPayee, initialValues(pAnalysis));
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pDate the end date.
     */
    private MoneyWiseXPayeeBucket(final MoneyWiseXPayeeBucket pBase,
                                  final TethysDate pDate) {
        super(pBase, pDate);
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pRange the date range.
     */
    private MoneyWiseXPayeeBucket(final MoneyWiseXPayeeBucket pBase,
                                  final TethysDateRange pRange) {
        super(pBase, pRange);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean isActive() {
        final MoneyWiseXAnalysisValues<MoneyWiseXIncomeAttr> myValues = getValues();
        final TethysMoney myIncome = myValues.getMoneyValue(MoneyWiseXIncomeAttr.INCOME);
        final TethysMoney myExpense = myValues.getMoneyValue(MoneyWiseXIncomeAttr.EXPENSE);
        return myIncome.isNonZero() || myExpense.isNonZero();
    }

    @Override
    public MoneyWiseXPayeeBucket newBucket(final TethysDate pDate) {
        return new MoneyWiseXPayeeBucket(this, pDate);
    }

    @Override
    public MoneyWiseXPayeeBucket newBucket(final TethysDateRange pRange) {
        return new MoneyWiseXPayeeBucket(this, pRange);
    }

    /**
     * Create a new set of values for a payee.
     * @param pAnalysis the analysis
     * @return the initial values
     */
    private static MoneyWiseXAnalysisValues<MoneyWiseXIncomeAttr> initialValues(final MoneyWiseXAnalysis pAnalysis) {
        return new MoneyWiseXAnalysisValues<>(MoneyWiseXIncomeAttr.class, pAnalysis.getReportingCurrency());
    }
}
