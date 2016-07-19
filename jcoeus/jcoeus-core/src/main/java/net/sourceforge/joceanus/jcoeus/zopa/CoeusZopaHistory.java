/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2016 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jcoeus.zopa;

import net.sourceforge.joceanus.jcoeus.data.CoeusHistory;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Zopa Transaction Totals History.
 */
public class CoeusZopaHistory
        extends CoeusHistory<CoeusZopaLoan, CoeusZopaTransaction, CoeusZopaTotals, CoeusZopaHistory> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusZopaHistory.class.getSimpleName(), CoeusHistory.getBaseFields());

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

    @Override
    public CoeusZopaMarket getMarket() {
        return (CoeusZopaMarket) super.getMarket();
    }

    @Override
    protected CoeusZopaTotals newTotals(final CoeusZopaTotals pTotals,
                                        final CoeusZopaTransaction pTrans) {
        return new CoeusZopaTotals(pTotals, pTrans);
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }
}
