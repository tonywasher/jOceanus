/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.analysis;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * The Portfolio Cash Bucket class.
 */
public class PortfolioCashBucket
        extends AccountBucket<Portfolio> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<PortfolioCashBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(PortfolioCashBucket.class);

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pPortfolio the portfolio
     */
    protected PortfolioCashBucket(final Analysis pAnalysis,
                                  final Portfolio pPortfolio) {
        /* Call super-constructor */
        super(pAnalysis, pPortfolio);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     */
    protected PortfolioCashBucket(final Analysis pAnalysis,
                                  final PortfolioCashBucket pBase) {
        /* Call super-constructor */
        super(pAnalysis, pBase);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    protected PortfolioCashBucket(final Analysis pAnalysis,
                                  final PortfolioCashBucket pBase,
                                  final TethysDate pDate) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pDate);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    protected PortfolioCashBucket(final Analysis pAnalysis,
                                  final PortfolioCashBucket pBase,
                                  final TethysDateRange pRange) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pRange);
    }

    @Override
    public MetisFieldSet<PortfolioCashBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Adjust account for transfer.
     * @param pSource the source portfolio
     * @param pTrans the transaction helper
     */
    protected void adjustForXfer(final PortfolioCashBucket pSource,
                                 final TransactionHelper pTrans) {
        /* Access transfer amount */
        final AccountValues myValues = pSource.getValues();
        TethysMoney myAmount = myValues.getMoneyValue(AccountAttribute.VALUATION);

        /* Adjust this valuation */
        adjustCounter(AccountAttribute.VALUATION, myAmount);
        registerTransaction(pTrans);

        /* Adjust source valuation */
        myAmount = new TethysMoney(myAmount);
        myAmount.negate();
        pSource.adjustCounter(AccountAttribute.VALUATION, myAmount);
        pSource.registerTransaction(pTrans);
    }

    /**
     * Add Values.
     * @param pBucket the portfolio cash bucket
     */
    protected void addValues(final PortfolioCashBucket pBucket) {
        /* Add values */
        addValues(getValues(), pBucket.getValues());

        /* Add base values */
        addValues(getBaseValues(), pBucket.getBaseValues());
    }

    /**
     * Add bucket to totals.
     * @param pTotals the totals
     * @param pSource the values to add
     */
    private static void addValues(final AccountValues pTotals,
                                  final AccountValues pSource) {
        /* Add valuation values */
        TethysMoney myValue = pTotals.getMoneyValue(AccountAttribute.VALUATION);
        myValue = new TethysMoney(myValue);
        final TethysMoney mySrcValue = pSource.getMoneyValue(AccountAttribute.VALUATION);
        myValue.addAmount(mySrcValue);
        pTotals.setValue(AccountAttribute.VALUATION, myValue);
    }
}
