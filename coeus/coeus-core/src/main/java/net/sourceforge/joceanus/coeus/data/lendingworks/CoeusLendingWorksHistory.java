/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.coeus.data.lendingworks;

import net.sourceforge.joceanus.coeus.data.CoeusHistory;
import net.sourceforge.joceanus.coeus.data.CoeusTotals;
import net.sourceforge.joceanus.coeus.data.CoeusTransaction;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;

/**
 * LendingWorks Transaction Totals History.
 */
public class CoeusLendingWorksHistory
        extends CoeusHistory {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusLendingWorksHistory> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusLendingWorksHistory.class);

    /**
     * Constructor for zeroed market totals.
     * @param pMarket the market
     */
    CoeusLendingWorksHistory(final CoeusLendingWorksMarket pMarket) {
        super(new CoeusLendingWorksTotals(pMarket));
    }

    /**
     * Constructor for zeroed loan totals.
     * @param pLoan the loan
     */
    CoeusLendingWorksHistory(final CoeusLendingWorksLoan pLoan) {
        super(new CoeusLendingWorksTotals(pLoan));
    }

    /**
     * Constructor for history view.
     * @param pHistory the history
     * @param pRange the date Range
     */
    CoeusLendingWorksHistory(final CoeusHistory pHistory,
                             final OceanusDateRange pRange) {
        super(pHistory, pRange);
    }

    @Override
    public CoeusLendingWorksMarket getMarket() {
        return (CoeusLendingWorksMarket) super.getMarket();
    }

    @Override
    protected CoeusLendingWorksTotals newTotals(final CoeusTotals pTotals,
                                                final CoeusTransaction pTrans) {
        return new CoeusLendingWorksTotals((CoeusLendingWorksTotals) pTotals, (CoeusLendingWorksTransaction) pTrans);
    }

    @Override
    public MetisFieldSet<CoeusLendingWorksHistory> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
