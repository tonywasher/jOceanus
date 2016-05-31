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

import net.sourceforge.joceanus.jcoeus.ratesetter.CoeusRateSetterMarket;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * RateSetter Tester.
 */
public class CoeusRateSetterTester {
    /**
     * Base Path.
     */
    private static final String BASE_PATH = "c:\\Users\\Tony\\Documents\\Peer2Peer\\Archive\\RateSetter";

    /**
     * Main program.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        /* Protect against exceptions */
        try {
            /* Create the parser */
            MetisDataFormatter myFormatter = new MetisDataFormatter();
            CoeusRateSetterMarket myMarket = new CoeusRateSetterMarket(myFormatter);

            /* Parse the loanBook files */
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetterActive.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2016-05.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2016-04.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2016-03.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2016-02.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2016-01.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2015-12.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2015-11.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2015-10.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2015-09.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2015-08.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2015-07.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2015-06.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2015-05.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2015-04.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2015-03.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2015-02.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2015-01.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2014-12.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2014-11.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2014-10.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2014-09.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2014-07.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2014-06.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2014-05.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2014-04.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2014-03.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2014-01.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2013-12.html"));
            myMarket.parseLoanBook(new File(BASE_PATH, "RateSetter2013-11.html"));

            /* Parse the statement files */
            myMarket.parseStatement(new File(BASE_PATH, "LenderTransactions1.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "LenderTransactions2.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "LenderTransactions3.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "LenderTransactions4.csv"));

            /* Analyse the data */
            myMarket.analyseMarket();
            myMarket.repairLoans();
            myMarket.analyseMarket();
            myFormatter = null;

            /* Catch Exceptions */
        } catch (OceanusException e) {
            e.printStackTrace(System.out);
        }
    }
}
