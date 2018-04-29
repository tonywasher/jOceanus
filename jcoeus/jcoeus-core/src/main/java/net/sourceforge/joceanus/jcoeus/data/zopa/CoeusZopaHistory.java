/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data.zopa;

import net.sourceforge.joceanus.jcoeus.data.CoeusHistory;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Zopa Transaction Totals History.
 */
public class CoeusZopaHistory
        extends CoeusHistory {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusZopaHistory> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusZopaHistory.class);

    /**
     * Constructor for zeroed market totals.
     * @param pMarket the market
     */
    protected CoeusZopaHistory(final CoeusZopaMarket pMarket) {
        super(new CoeusZopaTotals(pMarket));
    }

    /**
     * Constructor for zeroed period totals.
     * @param pMarket the market
     * @param pDate the end date for the totals
     */
    protected CoeusZopaHistory(final CoeusZopaMarket pMarket,
                               final TethysDate pDate) {
        super(new CoeusZopaTotals(pMarket, pDate));
    }

    /**
     * Constructor for zeroed loan totals.
     * @param pLoan the loan
     */
    protected CoeusZopaHistory(final CoeusZopaLoan pLoan) {
        super(new CoeusZopaTotals(pLoan));
    }

    /**
     * Constructor for zeroed loan totals.
     * @param pLoan the loan
     * @param pDate the end date for the totals
     */
    protected CoeusZopaHistory(final CoeusZopaLoan pLoan,
                               final TethysDate pDate) {
        super(new CoeusZopaTotals(pLoan, pDate));
    }

    @Override
    public CoeusZopaMarket getMarket() {
        return (CoeusZopaMarket) super.getMarket();
    }

    @Override
    protected CoeusZopaTotals newTotals(final CoeusTotals pTotals,
                                        final CoeusTransaction pTrans) {
        return new CoeusZopaTotals((CoeusZopaTotals) pTotals, (CoeusZopaTransaction) pTrans);
    }

    @Override
    public MetisFieldSet<CoeusZopaHistory> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
