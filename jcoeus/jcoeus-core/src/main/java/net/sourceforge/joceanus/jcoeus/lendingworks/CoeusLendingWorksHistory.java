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
package net.sourceforge.joceanus.jcoeus.lendingworks;

import net.sourceforge.joceanus.jcoeus.data.CoeusHistory;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * LendingWorks Transaction Totals History.
 */
public class CoeusLendingWorksHistory
        extends CoeusHistory<CoeusLendingWorksLoan, CoeusLendingWorksTransaction, CoeusLendingWorksTotals, CoeusLendingWorksHistory> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusLendingWorksHistory.class.getSimpleName(), CoeusHistory.getBaseFields());

    /**
     * Constructor for zeroed market totals.
     * @param pMarket the market
     */
    protected CoeusLendingWorksHistory(final CoeusLendingWorksMarket pMarket) {
        super(new CoeusLendingWorksTotals(pMarket));
    }

    /**
     * Constructor for zeroed period totals.
     * @param pMarket the market
     * @param pDate the end date for the totals
     */
    protected CoeusLendingWorksHistory(final CoeusLendingWorksMarket pMarket,
                                       final TethysDate pDate) {
        super(new CoeusLendingWorksTotals(pMarket, pDate));
    }

    /**
     * Constructor for zeroed loan totals.
     * @param pLoan the loan
     */
    protected CoeusLendingWorksHistory(final CoeusLendingWorksLoan pLoan) {
        super(new CoeusLendingWorksTotals(pLoan));
    }

    @Override
    public CoeusLendingWorksMarket getMarket() {
        return (CoeusLendingWorksMarket) super.getMarket();
    }

    @Override
    protected CoeusLendingWorksTotals newTotals(final CoeusLendingWorksTotals pTotals,
                                                final CoeusLendingWorksTransaction pTrans) {
        return new CoeusLendingWorksTotals(pTotals, pTrans);
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }
}