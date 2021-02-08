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
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasis;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * TaxBasis Bucket.
 */
public class MoneyWiseTaxBasisBucket
        extends MoneyWiseAnalysisBucket<TaxBasis, MoneyWiseTaxBasisAttr> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseTaxBasisBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTaxBasisBucket.class);

    /**
     * Constructor.
     * @param pTaxBasis the taxBasis
     * @param pAnalysis the analysis
     */
    MoneyWiseTaxBasisBucket(final TaxBasis pTaxBasis,
                            final MoneyWiseAnalysis pAnalysis) {
        super(pTaxBasis, initialValues(pAnalysis));
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pDate the end date.
     */
    private MoneyWiseTaxBasisBucket(final MoneyWiseTaxBasisBucket pBase,
                                    final TethysDate pDate) {
        super(pBase, pDate);
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pRange the date range.
     */
    private MoneyWiseTaxBasisBucket(final MoneyWiseTaxBasisBucket pBase,
                                    final TethysDateRange pRange) {
        super(pBase, pRange);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean isActive() {
        final MoneyWiseAnalysisValues<MoneyWiseTaxBasisAttr> myValues = getValues();
        final TethysMoney myGross = myValues.getMoneyValue(MoneyWiseTaxBasisAttr.GROSS);
        final TethysMoney myNett = myValues.getMoneyValue(MoneyWiseTaxBasisAttr.NETT);
        final TethysMoney myTax = myValues.getMoneyValue(MoneyWiseTaxBasisAttr.TAXCREDIT);
        return myGross.isNonZero() || myNett.isNonZero() || myTax.isNonZero();
    }

    @Override
    public MoneyWiseTaxBasisBucket newBucket(final TethysDate pDate) {
        return new MoneyWiseTaxBasisBucket(this, pDate);
    }

    @Override
    public MoneyWiseTaxBasisBucket newBucket(final TethysDateRange pRange) {
        return new MoneyWiseTaxBasisBucket(this, pRange);
    }

    /**
     * Create a new set of values for a payee.
     * @param pAnalysis the analysis
     * @return the initial values
     */
    private static MoneyWiseAnalysisValues<MoneyWiseTaxBasisAttr> initialValues(final MoneyWiseAnalysis pAnalysis) {
        return new MoneyWiseAnalysisValues<>(MoneyWiseTaxBasisAttr.class, pAnalysis.getReportingCurrency());
    }
}
