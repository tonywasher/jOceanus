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

import net.sourceforge.joceanus.jcoeus.fundingcircle.CoeusFundingCircleMarket;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * FundingCircle Tester.
 */
public class CoeusFundingCircleTester {
    /**
     * Base Path.
     */
    private static final String BASE_PATH = "c:\\Users\\Tony\\Documents\\Peer2Peer\\Archive\\FundingCircle";

    /**
     * Main program.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        /* Create the market */
        MetisDataFormatter myFormatter = new MetisDataFormatter();
        CoeusFundingCircleMarket myMarket = new CoeusFundingCircleMarket(myFormatter);

        /* Protect against exceptions */
        try {
            /* Parse the loanBook file */
            myMarket.parseLoanBook(new File(BASE_PATH, "loan-parts-report.csv"));

            /* Parse the statement files */
            myMarket.parseStatement(new File(BASE_PATH, "statement_2013-07.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2013-08.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2013-09.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2013-10.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2013-11.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2013-12.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2014-01.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2014-02.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2014-03.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2014-04.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2014-05.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2014-06.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2014-07.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2014-08.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2014-09.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2014-10.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2014-11.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2014-12.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2015-01.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2015-02.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2015-03.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2015-04.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2015-05.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2015-06.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2015-07.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2015-08.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2015-09.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2015-10.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2015-11.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2015-12.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2016-01.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2016-02.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2016-03.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2016-04.csv"));
            myMarket.parseStatement(new File(BASE_PATH, "statement_2016-05.csv"));

            /* Catch Exceptions */
        } catch (OceanusException e) {
            e.printStackTrace(System.out);
        }
    }
}
