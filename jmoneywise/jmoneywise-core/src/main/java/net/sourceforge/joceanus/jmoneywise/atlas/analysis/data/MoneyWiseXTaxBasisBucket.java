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
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasis;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * TaxBasis Bucket.
 */
public class MoneyWiseXTaxBasisBucket
        extends MoneyWiseXAnalysisBucket<TaxBasis, MoneyWiseXTaxBasisAttr> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXTaxBasisBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXTaxBasisBucket.class);

    /**
     * Constructor.
     * @param pTaxBasis the taxBasis
     * @param pAnalysis the analysis
     */
    MoneyWiseXTaxBasisBucket(final TaxBasis pTaxBasis,
                             final MoneyWiseXAnalysis pAnalysis) {
        super(pTaxBasis, initialValues(pAnalysis));
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pDate the end date.
     */
    private MoneyWiseXTaxBasisBucket(final MoneyWiseXTaxBasisBucket pBase,
                                     final TethysDate pDate) {
        super(pBase, pDate);
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pRange the date range.
     */
    private MoneyWiseXTaxBasisBucket(final MoneyWiseXTaxBasisBucket pBase,
                                     final TethysDateRange pRange) {
        super(pBase, pRange);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean isActive() {
        final MoneyWiseXAnalysisValues<MoneyWiseXTaxBasisAttr> myValues = getValues();
        final TethysMoney myGross = myValues.getMoneyValue(MoneyWiseXTaxBasisAttr.GROSS);
        final TethysMoney myNett = myValues.getMoneyValue(MoneyWiseXTaxBasisAttr.NETT);
        final TethysMoney myTax = myValues.getMoneyValue(MoneyWiseXTaxBasisAttr.TAXCREDIT);
        return myGross.isNonZero() || myNett.isNonZero() || myTax.isNonZero();
    }

    @Override
    public MoneyWiseXTaxBasisBucket newBucket(final TethysDate pDate) {
        return new MoneyWiseXTaxBasisBucket(this, pDate);
    }

    @Override
    public MoneyWiseXTaxBasisBucket newBucket(final TethysDateRange pRange) {
        return new MoneyWiseXTaxBasisBucket(this, pRange);
    }

    /**
     * Create a new set of values for a payee.
     * @param pAnalysis the analysis
     * @return the initial values
     */
    private static MoneyWiseXAnalysisValues<MoneyWiseXTaxBasisAttr> initialValues(final MoneyWiseXAnalysis pAnalysis) {
        return new MoneyWiseXAnalysisValues<>(MoneyWiseXTaxBasisAttr.class, pAnalysis.getReportingCurrency());
    }
}
