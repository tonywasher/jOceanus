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
package net.sourceforge.joceanus.coeus.data;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;

import java.util.EnumMap;
import java.util.Map;

/**
 * Loan MarketSet.
 */
public class CoeusMarketSet
        implements MetisDataMap<CoeusMarketProvider, CoeusMarket> {
    /**
     * The map of markets.
     */
    private final Map<CoeusMarketProvider, CoeusMarket> theMarketMap;

    /**
     * Constructor.
     */
    public CoeusMarketSet() {
        theMarketMap = new EnumMap<>(CoeusMarketProvider.class);
    }

    @Override
    public Map<CoeusMarketProvider, CoeusMarket> getUnderlyingMap() {
        return theMarketMap;
    }

    /**
     * Declare the market.
     * @param pProvider the provider
     * @param pMarket the loan market
     */
    public void declareMarket(final CoeusMarketProvider pProvider,
                              final CoeusMarket pMarket) {
        theMarketMap.put(pProvider, pMarket);
    }

    /**
     * Analyse all markets.
     * @throws OceanusException on error
     */
    public void analyseMarkets() throws OceanusException {
        /* Loop through the markets performing analysis */
        for (CoeusMarket myMarket : theMarketMap.values()) {
            myMarket.analyseMarket();
        }
    }

    /**
     * Obtain Market.
     * @param pProvider the market provider
     * @return the history
     */
    public CoeusMarket getMarket(final CoeusMarketProvider pProvider) {
        return theMarketMap.get(pProvider);
    }

    /**
     * Obtain market snapshot.
     * @param pProvider the market provider
     * @param pDate the date
     * @return the snapshot
     */
    public CoeusMarketSnapShot getSnapshot(final CoeusMarketProvider pProvider,
                                           final OceanusDate pDate) {
        final CoeusMarket myMarket = theMarketMap.get(pProvider);
        return myMarket == null
                                ? null
                                : myMarket.getSnapshot(pDate);
    }

    /**
     * Obtain market annual.
     * @param pProvider the market provider
     * @param pCalendar the calendar
     * @param pDate the date
     * @return the annual
     */
    public CoeusMarketAnnual getAnnual(final CoeusMarketProvider pProvider,
                                       final CoeusCalendar pCalendar,
                                       final OceanusDate pDate) {
        final CoeusMarket myMarket = theMarketMap.get(pProvider);
        return myMarket == null
                                ? null
                                : myMarket.getAnnual(pCalendar, pDate);
    }
}
