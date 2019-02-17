/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2019 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jcoeus.data.ratesetter;

import net.sourceforge.joceanus.jcoeus.data.CoeusHistory;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * RateSetter Transaction Totals History.
 */
public class CoeusRateSetterHistory
        extends CoeusHistory {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusRateSetterHistory> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusRateSetterHistory.class);

    /**
     * Constructor for zeroed market totals.
     * @param pMarket the market
     */
    protected CoeusRateSetterHistory(final CoeusRateSetterMarket pMarket) {
        super(new CoeusRateSetterTotals(pMarket));
    }

    /**
     * Constructor for zeroed period totals.
     * @param pMarket the market
     * @param pDate the end date for the totals
     */
    protected CoeusRateSetterHistory(final CoeusRateSetterMarket pMarket,
                                     final TethysDate pDate) {
        super(new CoeusRateSetterTotals(pMarket, pDate));
    }

    /**
     * Constructor for zeroed loan totals.
     * @param pLoan the loan
     */
    protected CoeusRateSetterHistory(final CoeusRateSetterLoan pLoan) {
        super(new CoeusRateSetterTotals(pLoan));
    }

    /**
     * Constructor for zeroed period loan totals.
     * @param pLoan the loan
     * @param pDate the end date for the totals
     */
    protected CoeusRateSetterHistory(final CoeusRateSetterLoan pLoan,
                                     final TethysDate pDate) {
        super(new CoeusRateSetterTotals(pLoan, pDate));
    }

    @Override
    public CoeusRateSetterMarket getMarket() {
        return (CoeusRateSetterMarket) super.getMarket();
    }

    @Override
    protected CoeusRateSetterTotals newTotals(final CoeusTotals pTotals,
                                              final CoeusTransaction pTrans) {
        return new CoeusRateSetterTotals((CoeusRateSetterTotals) pTotals, (CoeusRateSetterTransaction) pTrans);
    }

    @Override
    public MetisFieldSet<CoeusRateSetterHistory> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
