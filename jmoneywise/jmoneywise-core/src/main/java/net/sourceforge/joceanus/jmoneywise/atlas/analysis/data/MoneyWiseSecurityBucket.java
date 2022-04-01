/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Security Holding Bucket.
 */
public class MoneyWiseSecurityBucket
        extends MoneyWiseAnalysisBucket<SecurityHolding, MoneyWiseSecurityAttr> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseSecurityBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSecurityBucket.class);

    /**
     * Constructor.
     * @param pHolding the security holding
     * @param pAnalysis the analysis
     */
    MoneyWiseSecurityBucket(final SecurityHolding pHolding,
                            final MoneyWiseAnalysis pAnalysis) {
        super(pHolding, initialValues(pHolding, pAnalysis));
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pDate the end date.
     */
    private MoneyWiseSecurityBucket(final MoneyWiseSecurityBucket pBase,
                                    final TethysDate pDate) {
        super(pBase, pDate);
    }

    /**
     * Constructor.
     * @param pBase the base bucket
     * @param pRange the date range.
     */
    private MoneyWiseSecurityBucket(final MoneyWiseSecurityBucket pBase,
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
        return getValues().getUnitsValue(MoneyWiseSecurityAttr.UNITS).isNonZero();
    }

    @Override
    public MoneyWiseSecurityBucket newBucket(final TethysDate pDate) {
        return new MoneyWiseSecurityBucket(this, pDate);
    }

    @Override
    public MoneyWiseSecurityBucket newBucket(final TethysDateRange pRange) {
        return new MoneyWiseSecurityBucket(this, pRange);
    }

    /**
     * Create a new set of values for a payee.
     * @param pHolding the security holding
     * @param pAnalysis the analysis
     * @return the initial values
     */
    private static MoneyWiseAnalysisValues<MoneyWiseSecurityAttr> initialValues(final SecurityHolding pHolding,
                                                                                final MoneyWiseAnalysis pAnalysis) {
        final Security mySecurity = pHolding.getSecurity();
        return new MoneyWiseAnalysisValues<>(MoneyWiseSecurityAttr.class, mySecurity.getAssetCurrency(), pAnalysis.getReportingCurrency());
    }
}
