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
package net.sourceforge.joceanus.moneywise.test.data;

import net.sourceforge.joceanus.moneywise.atlas.data.analysis.analyse.MoneyWiseXAnalysisBuilder;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.test.data.storage.MoneyWiseDataTestSecurity;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestAccounts;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCash;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestExpense;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestSalary;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestTransfers;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
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
     * The testCases.
     */
    private final List<MoneyWiseDataTestCase> theTestCases;

    /**
     * Constructor.
     *
     * @param pView the view
     * @throws OceanusException on error
     */
    MoneyWiseDataTestRunner(final MoneyWiseView pView) throws OceanusException {
        /* Create the analyser */
        theView = pView;
        theAnalysisBuilder = new MoneyWiseXAnalysisBuilder(theView);

        /* Create the dataSet */
        theDataSet = theView.getNewData();

        /* Create the account builder */
        theAccountBuilder = new MoneyWiseDataTestAccounts(theDataSet);

        theTestCases = createTestCases();
    }

    /**
     * Create the test suite.
     *
     * @param pView the view
     * @return the test stream
     * @throws OceanusException on error
     */
    public static Stream<DynamicNode> createTests(final MoneyWiseView pView) throws OceanusException {
        /* Create the testRunner */
        final MoneyWiseDataTestRunner myRunner = new MoneyWiseDataTestRunner(pView);

        /* Create the transaction testCases */
        Stream<DynamicNode> myTranTests = Stream.empty();
        for (MoneyWiseDataTestCase myTestCase : myRunner.getTestCases()) {
            myTranTests = Stream.concat(myTranTests, Stream.of(DynamicTest.dynamicTest(myTestCase.getName(),
                    () -> myRunner.runTestCase(myTestCase))));
        }

        /* Create container for tests */
        myTranTests = Stream.of(DynamicContainer.dynamicContainer("transactions", myTranTests));
        return Stream.of(DynamicContainer.dynamicContainer("localData", Stream.concat(
                myTranTests, myRunner.createStorageTests())));
    }

    /**
     * Obtain the testCase list.
     *
     * @return the testCase list
     */
    private List<MoneyWiseDataTestCase> getTestCases() {
        return theTestCases;
    }

    /**
     * Create the testCases.
     *
     * @return the testCases
     */
    private List<MoneyWiseDataTestCase> createTestCases() {
        final List<MoneyWiseDataTestCase> myList = new ArrayList<>();
        myList.add(new MoneyWiseDataTestTransfers(theAccountBuilder));
        myList.add(new MoneyWiseDataTestExpense(theAccountBuilder));
        myList.add(new MoneyWiseDataTestCash(theAccountBuilder));
        myList.add(new MoneyWiseDataTestSalary(theAccountBuilder));
        return myList;
    }

    /**
     * Run the testCase.
     *
     * @param pTest the testCase
     * @throws OceanusException on error
     */
    public void runTestCase(final MoneyWiseDataTestCase pTest) throws OceanusException {
        /* Run the test */
        final OceanusProfile myTask = theView.getNewProfile("Transaction TestCases");
        prepareDataForTest(pTest);
        pTest.checkErrors();
        final MoneyWiseXAnalysis myAnalysis = theAnalysisBuilder.analyseNewData(theDataSet);
        pTest.setAnalysis(myAnalysis);
        pTest.checkAnalysis();
        myTask.end();
    }

    /**
     * Prepare dataSet.
     *
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

    /**
     * Run the storage tests.
     *
     * @throws OceanusException on error
     */
    public Stream<DynamicNode> createStorageTests() throws OceanusException {
        Stream<DynamicNode> myStream = Stream.of(DynamicTest.dynamicTest("initData", () -> prepareFullData()));
        myStream = Stream.concat(myStream, MoneyWiseDataTest.storageTests(theDataSet, theView));
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("editSet",
                () -> MoneyWiseDataTest.checkEditSet(theDataSet, theView))));
        return Stream.of(DynamicContainer.dynamicContainer("allTrans", myStream));
    }

    /**
     * Prepare full data for test.
     * @throws OceanusException on error
     */
    private void prepareFullData() throws OceanusException {
        /* Reset all data */
        theAccountBuilder.resetData();

        /* Loop through the tests */
        for (MoneyWiseDataTestCase myTest : theTestCases) {
            /* Create data */
            myTest.setUpAccounts();
            myTest.defineRates();
            myTest.definePrices();
            myTest.defineTransactions();
        }

        /* Resolve the transactions */
        theDataSet.getTransactions().resolveDataSetLinks();

        /* Initialise the security */
        new MoneyWiseDataTestSecurity(theDataSet).initSecurity(theView);
    }
}
