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
package net.sourceforge.joceanus.jcoeus;

import net.sourceforge.joceanus.jcoeus.fundingcircle.CoeusFundingCircleLoader;
import net.sourceforge.joceanus.jcoeus.fundingcircle.CoeusFundingCircleMarket;
import net.sourceforge.joceanus.jcoeus.ratesetter.CoeusRateSetterLoader;
import net.sourceforge.joceanus.jcoeus.ratesetter.CoeusRateSetterMarket;
import net.sourceforge.joceanus.jcoeus.zopa.CoeusZopaLoader;
import net.sourceforge.joceanus.jcoeus.zopa.CoeusZopaMarket;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
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
            CoeusFundingCircleLoader myFundingCircleLoader = new CoeusFundingCircleLoader(myFormatter, BASE_PATH + "FundingCircle");
            CoeusRateSetterLoader myRateSetterLoader = new CoeusRateSetterLoader(myFormatter, BASE_PATH + "RateSetter");
            CoeusZopaLoader myZopaLoader = new CoeusZopaLoader(myFormatter, BASE_PATH + "Zopa");

            /* Load the markets */
            CoeusFundingCircleMarket myFundingCircleMarket = myFundingCircleLoader.loadMarket();
            CoeusRateSetterMarket myRateSetterMarket = myRateSetterLoader.loadMarket();
            CoeusZopaMarket myZopaMarket = myZopaLoader.loadMarket();

            /* Place holder */
            myFormatter = null;

            /* Catch Exceptions */
        } catch (OceanusException e) {
            e.printStackTrace(System.out);
        }
    }
}
