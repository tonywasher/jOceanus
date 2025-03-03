/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.test.data.xdoc;

import net.sourceforge.joceanus.moneywise.exc.MoneyWiseIOException;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

import java.io.File;
import java.io.PrintWriter;

/**
 * XDoc Report Builder.
 */
public class MoneyWiseDataXDocBuilder {
    /**
     * Analysis Group.
     */
    static final String GRP_ANALYSIS = "analysis";

    /**
     * Accounts Group.
     */
    static final String GRP_ACCOUNTS = "accounts";

    /**
     * Data Group.
     */
    static final String GRP_DATA = "data";

    /**
     * Date Table Header.
     */
    static final String HDR_DATE = "Date";

    /**
     * Profit Table Header.
     */
    static final String HDR_PROFIT = "Profit";

    /**
     * Constructor.
     * @param pTest the test case
     * @throws OceanusException on error
     */
    public MoneyWiseDataXDocBuilder(final MoneyWiseDataTestCase pTest) throws OceanusException {
        /* Initialise the document */
        final MoneyWiseDataXDocReport myReport = new MoneyWiseDataXDocReport();
        myReport.startReport(pTest);

        /* Create definitions */
        myReport.newSection("Definitions");
        myReport.addParagraph("We start with the following accounts and transaction categories");
        final MoneyWiseDataXDocAssets myAssets = new MoneyWiseDataXDocAssets(myReport, pTest);
        myAssets.createAssetDefinitions();
        final MoneyWiseDataXDocPayee myPayee = new MoneyWiseDataXDocPayee(myReport, pTest);
        myPayee.declareParentPayees(myAssets.getParentPayees());
        myPayee.createPayeeDefinitions();
        final MoneyWiseDataXDocTransCat myTransCat = new MoneyWiseDataXDocTransCat(myReport, pTest);
        myTransCat.createTransDefinitions();

        /* Define transaction data */
        myReport.newSection("Transactions");
        myReport.addParagraph("We have the following events");
        final MoneyWiseDataXDocTrans myTrans = new MoneyWiseDataXDocTrans(myReport, pTest);
        myTrans.createTransactions();
        final MoneyWiseDataXDocRate myRates = new MoneyWiseDataXDocRate(myReport, pTest);
        myRates.createExchangeRates();
        final MoneyWiseDataXDocPrice myPrices = new MoneyWiseDataXDocPrice(myReport, pTest);
        myPrices.createSecurityPrices();

        /* Define analysis */
        myReport.newSection("Analysis");
        myReport.addParagraph("The analysis of these transactions is as follows");
        myAssets.createAssetAnalysis();
        myPayee.createPayeeAnalysis();
        myTransCat.createTransAnalysis();
        final MoneyWiseDataXDocTax myTax = new MoneyWiseDataXDocTax(myReport, pTest);
        myTax.createTaxAnalysis();

        /* Output the file */
        final String myHome = System.getProperty("user.home") + "/html";
        final File myFile = new File(myHome, pTest.getName() + ".xml");
        try (PrintWriter out = new PrintWriter(myFile)) {
            out.println(myReport.formatXML());
        } catch (Exception e) {
            throw new MoneyWiseIOException("Failed to write file", e);
        }
    }
}
