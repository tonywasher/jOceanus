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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Security Holding Bucket.
 */
public class MoneyWiseXSecurityBucket
        extends MoneyWiseXAnalysisBucket<SecurityHolding, MoneyWiseXSecurityAttr> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXSecurityBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXSecurityBucket.class);

    /**
     * Constructor.
     * @param pHolding the security holding
     * @param pAnalysis the analysis
     */
    MoneyWiseXSecurityBucket(final SecurityHolding pHolding,
                             final MoneyWiseXAnalysis pAnalysis) {
        super(pHolding, initialValues(pHolding, pAnalysis));
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pDate the end date.
     */
    private MoneyWiseXSecurityBucket(final MoneyWiseXSecurityBucket pBase,
                                     final TethysDate pDate) {
        super(pBase, pDate);
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pRange the date range.
     */
    private MoneyWiseXSecurityBucket(final MoneyWiseXSecurityBucket pBase,
                                     final TethysDateRange pRange) {
        super(pBase, pRange);
    }

    /**
     * Obtain the portfolio.
     * @return the portfolio
     */
    public Portfolio getPortfolio() {
        return getOwner().getPortfolio();
    }

    /**
     * Obtain the security.
     * @return the security
     */
    public Security getSecurity() {
        return getOwner().getSecurity();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean isActive() {
        return getValues().getUnitsValue(MoneyWiseXSecurityAttr.UNITS).isNonZero();
    }

    @Override
    public MoneyWiseXSecurityBucket newBucket(final TethysDate pDate) {
        return new MoneyWiseXSecurityBucket(this, pDate);
    }

    @Override
    public MoneyWiseXSecurityBucket newBucket(final TethysDateRange pRange) {
        return new MoneyWiseXSecurityBucket(this, pRange);
    }

    /**
     * Create a new set of values for a payee.
     * @param pHolding the security holding
     * @param pAnalysis the analysis
     * @return the initial values
     */
    private static MoneyWiseXAnalysisValues<MoneyWiseXSecurityAttr> initialValues(final SecurityHolding pHolding,
                                                                                  final MoneyWiseXAnalysis pAnalysis) {
        final Security mySecurity = pHolding.getSecurity();
        return new MoneyWiseXAnalysisValues<>(MoneyWiseXSecurityAttr.class, mySecurity.getAssetCurrency(), pAnalysis.getReportingCurrency());
    }
}
