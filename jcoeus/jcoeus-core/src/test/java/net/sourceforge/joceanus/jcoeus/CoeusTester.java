/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jcoeus;

import net.sourceforge.joceanus.jcoeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketSet;
import net.sourceforge.joceanus.jcoeus.data.fundingcircle.CoeusFundingCircleLoader;
import net.sourceforge.joceanus.jcoeus.data.lendingworks.CoeusLendingWorksLoader;
import net.sourceforge.joceanus.jcoeus.data.ratesetter.CoeusRateSetterLoader;
import net.sourceforge.joceanus.jcoeus.data.zopa.CoeusZopaLoader;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Zopa Tester.
 */
public class CoeusTester {
    /**
     * Base Path.
     */
    private static final String BASE_PATH = "c:\\Users\\Tony\\Documents\\Peer2Peer\\";

    /**
     * Main program.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        /* Protect against exceptions */
        try {
            /* Create the parser */
            MetisDataFormatter myFormatter = new MetisDataFormatter();

            /* Create the loaders */
            CoeusFundingCircleLoader myFundingCircleLoader = new CoeusFundingCircleLoader(myFormatter, BASE_PATH);
            CoeusRateSetterLoader myRateSetterLoader = new CoeusRateSetterLoader(myFormatter, BASE_PATH);
            CoeusZopaLoader myZopaLoader = new CoeusZopaLoader(myFormatter, BASE_PATH);
            CoeusLendingWorksLoader myLendingWorksLoader = new CoeusLendingWorksLoader(myFormatter, BASE_PATH);

            /* Create the loan market set */
            CoeusMarketSet myMarketSet = new CoeusMarketSet();

            /* Load the markets */
            myMarketSet.declareMarket(CoeusMarketProvider.FUNDINGCIRCLE, myFundingCircleLoader.loadMarket());
            myMarketSet.declareMarket(CoeusMarketProvider.RATESETTER, myRateSetterLoader.loadMarket());
            myMarketSet.declareMarket(CoeusMarketProvider.ZOPA, myZopaLoader.loadMarket());
            myMarketSet.declareMarket(CoeusMarketProvider.LENDINGWORKS, myLendingWorksLoader.loadMarket());

            /* Analyse the markets */
            myMarketSet.analyseMarkets();

            /* Place holder */
            myFormatter = null;

            /* Catch Exceptions */
        } catch (OceanusException e) {
            e.printStackTrace(System.out);
        }
    }
}
