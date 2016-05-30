/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.ratesetter;

import net.sourceforge.joceanus.jcoeus.data.CoeusHistory;
import net.sourceforge.joceanus.jcoeus.fundingcircle.CoeusFundingCircleHistory;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * RateSetter Transaction Totals History.
 */
public class CoeusRateSetterHistory
        extends CoeusHistory<CoeusRateSetterLoan, CoeusRateSetterTransaction, CoeusRateSetterTotals, CoeusRateSetterHistory> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusFundingCircleHistory.class.getSimpleName(), CoeusHistory.getBaseFields());

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

    @Override
    public CoeusRateSetterMarket getMarket() {
        return (CoeusRateSetterMarket) super.getMarket();
    }

    @Override
    protected CoeusRateSetterTotals newTotals(final CoeusRateSetterTotals pTotals,
                                              final CoeusRateSetterTransaction pTrans) {
        return new CoeusRateSetterTotals(pTotals, pTrans);
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }
}
