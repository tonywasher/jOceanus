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
package net.sourceforge.joceanus.jcoeus;

import java.io.File;

import net.sourceforge.joceanus.jcoeus.zopa.CoeusZopaMarket;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Zopa Tester.
 */
public class CoeusZopaTester {
    /**
     * Base Path.
     */
    private static final String BASE_PATH = "c:\\Users\\Tony\\Documents\\Peer2Peer\\Archive\\Zopa";

    /**
     * Main program
     * @param args the arguments
     */
    public static void main(final String[] args) {
        /* Create the parser */
        MetisDataFormatter myFormatter = new MetisDataFormatter();
        CoeusZopaMarket myMarket = new CoeusZopaMarket(myFormatter);

        /* Protect against exceptions */
        try {
            /* Parse the loanBook file */
            myMarket.parseLoanBook(new File(BASE_PATH, "my_all_time_loan_book.csv"));

            /* Parse the statement files */
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement April 2013.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement May 2013.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement June 2013.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement July 2013.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement August 2013.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement September 2013.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement October 2013.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement November 2013.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement December 2013.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement January 2014.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement February 2014.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement March 2014.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement April 2014.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement May 2014.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement June 2014.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement July 2014.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement August 2014.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement September 2014.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement October 2014.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement November 2014.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement December 2014.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement January 2015.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement February 2015.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement March 2015.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement April 2015.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement May 2015.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement June 2015.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement July 2015.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement August 2015.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement September 2015.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement October 2015.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement November 2015.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement December 2015.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement January 2016.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement February 2016.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement March 2016.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement April 2016.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "ZopaStatement May 2016.csv"));

            /* Catch Exceptions */
        } catch (OceanusException e) {
            e.printStackTrace(System.out);
        }
    }
}
