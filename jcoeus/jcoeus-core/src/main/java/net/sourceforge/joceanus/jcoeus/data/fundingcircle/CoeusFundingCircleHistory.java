/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data.fundingcircle;

import net.sourceforge.joceanus.jcoeus.data.CoeusHistory;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * FundingCircle Transaction Totals History.
 */
public class CoeusFundingCircleHistory
        extends CoeusHistory {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusFundingCircleHistory> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusFundingCircleHistory.class);

    /**
     * Constructor for zeroed market totals.
     * @param pMarket the market
     */
    CoeusFundingCircleHistory(final CoeusFundingCircleMarket pMarket) {
        super(new CoeusFundingCircleTotals(pMarket));
    }

    /**
     * Constructor for zeroed loan totals.
     * @param pLoan the loan
     */
    CoeusFundingCircleHistory(final CoeusFundingCircleLoan pLoan) {
        super(new CoeusFundingCircleTotals(pLoan));
    }

    @Override
    public CoeusFundingCircleMarket getMarket() {
        return (CoeusFundingCircleMarket) super.getMarket();
    }

    @Override
    protected CoeusFundingCircleTotals newTotals(final CoeusTotals pTotals,
                                                 final CoeusTransaction pTrans) {
        return new CoeusFundingCircleTotals((CoeusFundingCircleTotals) pTotals, (CoeusFundingCircleTransaction) pTrans);
    }

    @Override
    public MetisFieldSet<CoeusFundingCircleHistory> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
