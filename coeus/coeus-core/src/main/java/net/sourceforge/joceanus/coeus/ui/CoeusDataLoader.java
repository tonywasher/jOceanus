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
package net.sourceforge.joceanus.coeus.ui;

import net.sourceforge.joceanus.coeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.coeus.data.CoeusMarketSet;
import net.sourceforge.joceanus.coeus.data.fundingcircle.CoeusFundingCircleLoader;
import net.sourceforge.joceanus.coeus.data.lendingworks.CoeusLendingWorksLoader;
import net.sourceforge.joceanus.coeus.data.ratesetter.CoeusRateSetterLoader;
import net.sourceforge.joceanus.coeus.data.zopa.CoeusZopaLoader;
import net.sourceforge.joceanus.coeus.ui.CoeusPreference.CoeusPreferenceKey;
import net.sourceforge.joceanus.coeus.ui.CoeusPreference.CoeusPreferences;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.metis.toolkit.MetisToolkit;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

/**
 * Loader.
 */
public class CoeusDataLoader {
    /**
     * Data formatter.
     */
    private final OceanusDataFormatter theFormatter;

    /**
     * Preferences.
     */
    private final CoeusPreferences thePreferences;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     */
    public CoeusDataLoader(final MetisToolkit pToolkit) {
        /* Access the formatter */
        theFormatter = pToolkit.getFormatter();

        /* Obtain preferences */
        final MetisPreferenceManager myPrefMgr = pToolkit.getPreferenceManager();
        thePreferences = myPrefMgr.getPreferenceSet(CoeusPreferences.class);
    }

    /**
     * Load data.
     * @return the marketSet
     * @throws OceanusException on error
     */
    public CoeusMarketSet loadData() throws OceanusException {
        /* Create the loan market set */
        final CoeusMarketSet myMarketSet = new CoeusMarketSet();

        /* Obtain the base directory path from preferences */
        final String myBase = thePreferences.getStringValue(CoeusPreferenceKey.BASE);

        /* Create the loaders */
        final CoeusFundingCircleLoader myFundingCircleLoader = new CoeusFundingCircleLoader(theFormatter, myBase);
        final CoeusLendingWorksLoader myLendingWorksLoader = new CoeusLendingWorksLoader(theFormatter, myBase);
        final CoeusRateSetterLoader myRateSetterLoader = new CoeusRateSetterLoader(theFormatter, myBase);
        final CoeusZopaLoader myZopaLoader = new CoeusZopaLoader(theFormatter, myBase);

        /* Load the markets */
        myMarketSet.declareMarket(CoeusMarketProvider.FUNDINGCIRCLE, myFundingCircleLoader.loadMarket());
        myMarketSet.declareMarket(CoeusMarketProvider.LENDINGWORKS, myLendingWorksLoader.loadMarket());
        myMarketSet.declareMarket(CoeusMarketProvider.RATESETTER, myRateSetterLoader.loadMarket());
        myMarketSet.declareMarket(CoeusMarketProvider.ZOPA, myZopaLoader.loadMarket());

        /* Analyse the markets */
        myMarketSet.analyseMarkets();

        /* Return the market Set */
        return myMarketSet;
    }
}
