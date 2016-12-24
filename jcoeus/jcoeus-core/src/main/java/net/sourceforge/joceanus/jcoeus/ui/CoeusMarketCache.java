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
package net.sourceforge.joceanus.jcoeus.ui;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jcoeus.data.CoeusMarketAnnual;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketSet;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketSnapShot;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Loan MarketCache.
 */
public class CoeusMarketCache
        implements TethysEventProvider<CoeusDataEvent> {
    /**
     * The MarketSet.
     */
    private CoeusMarketSet theMarketSet;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<CoeusDataEvent> theEventManager;

    /**
     * The Map of MarketSnapShots.
     */
    private Map<CoeusMarketProvider, Map<TethysDate, CoeusMarketSnapShot>> theSnapShotMap;

    /**
     * The Map of MarketAnnuals.
     */
    private Map<CoeusMarketProvider, Map<TethysDate, CoeusMarketAnnual>> theAnnualMap;

    /**
     * Constructor.
     */
    public CoeusMarketCache() {
        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the maps */
        theSnapShotMap = new EnumMap<>(CoeusMarketProvider.class);
        theAnnualMap = new EnumMap<>(CoeusMarketProvider.class);
    }

    @Override
    public TethysEventRegistrar<CoeusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain SnapShot.
     * @param pProvider the market provider
     * @param pDate the snapshot date
     * @return the snapShot
     */
    public CoeusMarketSnapShot getSnapShot(final CoeusMarketProvider pProvider,
                                           final TethysDate pDate) {
        /* Obtain the map for the snapShot */
        Map<TethysDate, CoeusMarketSnapShot> myMap = theSnapShotMap.get(pProvider);
        if (myMap == null) {
            myMap = new HashMap<>();
            theSnapShotMap.put(pProvider, myMap);
        }

        /* Obtain the snapShot */
        CoeusMarketSnapShot mySnapShot = myMap.get(pDate);
        if ((mySnapShot == null)
            && (theMarketSet != null)) {
            mySnapShot = theMarketSet.getSnapshot(pProvider, pDate);
            myMap.put(pDate, mySnapShot);
        }

        /* Return the snapShot */
        return mySnapShot;
    }

    /**
     * Obtain Year.
     * @param pProvider the market provider
     * @param pDate the year date
     * @return the year
     */
    public CoeusMarketAnnual getAnnual(final CoeusMarketProvider pProvider,
                                       final TethysDate pDate) {
        /* Obtain the map for the annual */
        Map<TethysDate, CoeusMarketAnnual> myMap = theAnnualMap.get(pProvider);
        if (myMap == null) {
            myMap = new HashMap<>();
            theAnnualMap.put(pProvider, myMap);
        }

        /* Obtain the annual */
        CoeusMarketAnnual myAnnual = myMap.get(pDate);
        if ((myAnnual == null)
            && (theMarketSet != null)) {
            myAnnual = theMarketSet.getAnnual(pProvider, pDate);
            myMap.put(pDate, myAnnual);
        }

        /* Return the annual */
        return myAnnual;
    }

    /**
     * Is the cache idle?
     * @return true/false
     */
    public boolean isIdle() {
        return theMarketSet == null;
    }

    /**
     * Declare marketSet.
     * @param pMarketSet the market set
     */
    public void declareMarketSet(final CoeusMarketSet pMarketSet) {
        theMarketSet = pMarketSet;
        resetMaps();
    }

    /**
     * Reset maps.
     */
    private void resetMaps() {
        theSnapShotMap.clear();
        theAnnualMap.clear();
        theEventManager.fireEvent(CoeusDataEvent.REFRESHVIEW);
    }
}
