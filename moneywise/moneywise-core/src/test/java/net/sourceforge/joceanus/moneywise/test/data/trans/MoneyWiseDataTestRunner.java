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
package net.sourceforge.joceanus.moneywise.test.data.trans;

import net.sourceforge.joceanus.moneywise.atlas.data.analysis.analyse.MoneyWiseXAnalysisBuilder;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.toolkit.PrometheusToolkit;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Transaction Test provider.
 */
public class MoneyWiseDataTestRunner {
    /**
     * The view.
     */
    private final MoneyWiseView theView;

    /**
     * The dataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * Accounts Builder.
     */
    private final MoneyWiseDataTestAccounts theAccountBuilder;

    /**
     * Analysis Builder.
     */
    private final MoneyWiseXAnalysisBuilder theAnalysisBuilder;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    MoneyWiseDataTestRunner(final PrometheusToolkit pToolkit) throws OceanusException {
        /* Create the analyser */
        theView = new MoneyWiseView(pToolkit, new MoneyWiseUKTaxYearCache());
        theAnalysisBuilder = new MoneyWiseXAnalysisBuilder(theView);

        /* Create the dataSet */
        theDataSet = theView.getNewData();

        /* Create the account builder */
        theAccountBuilder = new MoneyWiseDataTestAccounts(theDataSet);
    }

    /**
     * Create the test suite.
     * @param pToolkit the toolkit
     * @return the test stream
     * @throws OceanusException on error
     */
    public static Stream<DynamicNode> createTests(final PrometheusToolkit pToolkit) throws OceanusException {
        /* Create the testRunner */
        final MoneyWiseDataTestRunner myRunner = new MoneyWiseDataTestRunner(pToolkit);

        /* Create the testCases */
        final List<MoneyWiseDataTestCase> myList = myRunner.createTestCases();
        Stream<DynamicNode> myTests = Stream.empty();
        for (MoneyWiseDataTestCase myTestCase : myList) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest(myTestCase.getName(),
                                                                               () -> myRunner.runTestCase(myTestCase))));
        }

        /* Return container of tests */
        return Stream.of(DynamicContainer.dynamicContainer("transactionTests", myTests));
    }

    /**
     * Create the testCases.
     * @return the testCases
     */
    private List<MoneyWiseDataTestCase> createTestCases() {
        final List<MoneyWiseDataTestCase> myList = new ArrayList<>();
        myList.add(new MoneyWiseDataTestTransfers(theAccountBuilder));
        myList.add(new MoneyWiseDataTestExpense(theAccountBuilder));
        myList.add(new MoneyWiseDataTestCash(theAccountBuilder));
        return myList;
    }

    /**
     * Run the testCase.
     * @param pTest the testCase
     * @throws OceanusException on error
     */
    public void runTestCase(final MoneyWiseDataTestCase pTest) throws OceanusException {
        /* Run the test */
        final OceanusProfile myTask = theView.getNewProfile("TestCase");
        prepareDataForTest(pTest);
        pTest.checkErrors();
        final MoneyWiseXAnalysis myAnalysis = theAnalysisBuilder.analyseNewData(theDataSet);
        pTest.setAnalysis(myAnalysis);
        pTest.checkAnalysis();
        myTask.end();
    }

    /**
     * Prepare dataSet.
     * @param pTest the testCase
     * @throws OceanusException on error
     */
    private void prepareDataForTest(final MoneyWiseDataTestCase pTest) throws OceanusException {
        /* Reset all data */
        theAccountBuilder.resetData();

        /* Create data */
        pTest.setUpAccounts();
        pTest.defineRates();
        pTest.definePrices();
        pTest.defineTransactions();

        /* Resolve the transactions */
        theDataSet.getTransactions().resolveDataSetLinks();
    }
}
