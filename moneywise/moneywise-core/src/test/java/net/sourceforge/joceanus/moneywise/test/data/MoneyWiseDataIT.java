/* *****************************************************************************
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

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.validate.MoneyWiseValidatorFactory;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.analyse.MoneyWiseAnalysisTransAnalyser;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisCashCategoryBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDepositCategoryBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisLoanCategoryBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisManager;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPayeeBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisPayeeAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTaxBasisAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTransAttr;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.moneywise.test.data.storage.MoneyWiseDataTestArchiveFile;
import net.sourceforge.joceanus.moneywise.test.data.storage.MoneyWiseDataTestDatabase;
import net.sourceforge.joceanus.moneywise.test.data.storage.MoneyWiseDataTestEditSet;
import net.sourceforge.joceanus.moneywise.test.data.storage.MoneyWiseDataTestODSFile;
import net.sourceforge.joceanus.moneywise.test.data.storage.MoneyWiseDataTestXMLFile;
import net.sourceforge.joceanus.moneywise.test.data.storage.MoneyWiseNullThreadMgr;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.toolkit.PrometheusToolkit;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadManager;
import net.sourceforge.joceanus.tethys.helper.TethysUIHelperFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * Test security.
 */
public class MoneyWiseDataIT {
    /**
     * Create the keySet test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    Stream<DynamicNode> dataTests() throws OceanusException {
        /* Create the data */
        final TethysUIHelperFactory myFactory = new TethysUIHelperFactory();
        final PrometheusToolkit myToolkit = new PrometheusToolkit(myFactory);
        final MoneyWiseView myView = new MoneyWiseView(myToolkit, new MoneyWiseUKTaxYearCache());
        myView.setValidatorFactory(new MoneyWiseValidatorFactory(true));

        /* Create tests */
        //final Stream<DynamicNode> myStream = MoneyWiseDataTestRunner.createTests(myView);
        //return Stream.concat(myStream, archiveDataTests(myView));
        return MoneyWiseDataTestRunner.createTests(myView);
    }

    /**
     * Populate local data.
     * @param pData the dataSet to populate
     * @param pView the view
     * @throws OceanusException on error
     */
    public static void checkEditSet(final MoneyWiseDataSet pData,
                                    final MoneyWiseView pView) throws OceanusException {
        /* Initialise the data */
        new MoneyWiseDataTestEditSet(pData).checkSeparateEditSets(pView);
        new MoneyWiseDataTestEditSet(pData).checkCombinedEditSet(pView);
    }

    /**
     * Analyse the data.
     * @param pData the dataSet to analyse
     * @param pView the view
     * @throws OceanusException on error
     */
    public void analyseData(final MoneyWiseDataSet pData,
                            final MoneyWiseView pView) throws OceanusException {
        /* Update the maps */
        pData.updateMaps();

        /* Create the analysis */
        final OceanusProfile myTask = pView.getNewProfile("Dummy");
        pView.setData(pData);
        final PrometheusEditSet myEditSet = new PrometheusEditSet(pView);
        final MoneyWiseAnalysisTransAnalyser myAnalyser = new MoneyWiseAnalysisTransAnalyser(myTask, myEditSet, pView.getPreferenceManager());

        /* Post process the analysis */
        myAnalyser.postProcessAnalysis();

        /* Access the analysis */
        final MoneyWiseAnalysis myAnalysis = myAnalyser.getAnalysis();
        final MoneyWiseAnalysisManager myAnalysisMgr = new MoneyWiseAnalysisManager(myAnalysis);
        myAnalysisMgr.analyseBase();

        /* Obtain deposit totals */
        final MoneyWiseAnalysisDepositCategoryBucket myDepCat = myAnalysis.getDepositCategories().getTotals();
        OceanusMoney myDepTotal = myDepCat.getValues().getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA);
        myDepTotal = myDepTotal == null ? new OceanusMoney() : new OceanusMoney(myDepTotal);

        /* Add in cash totals */
        final MoneyWiseAnalysisCashCategoryBucket myCashCat = myAnalysis.getCashCategories().getTotals();
        final OceanusMoney myCashTotal = myCashCat.getValues().getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA);
        if (myCashTotal != null)  {
            myDepTotal.addAmount(myCashTotal);
        }

        /* Add in loan totals */
        final MoneyWiseAnalysisLoanCategoryBucket myLoanCat = myAnalysis.getLoanCategories().getTotals();
        final OceanusMoney myLoanTotal = myLoanCat.getValues().getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA);
        if (myLoanTotal != null) {
            myDepTotal.addAmount(myLoanTotal);
        }

        /* Add in portfolio totals */
        final MoneyWiseAnalysisPortfolioBucket myPort = myAnalysis.getPortfolios().getTotals();
        final OceanusMoney myPortTotal = myPort.getValues().getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUEDELTA);
        if (myPortTotal != null) {
            myDepTotal.addAmount(myPortTotal);
        }

        /* Validate Payee totals */
        final MoneyWiseAnalysisPayeeBucket myPayeeTotals = myAnalysis.getPayees().getTotals();
        OceanusMoney myPayTotal = myPayeeTotals.getValues().getMoneyValue(MoneyWiseAnalysisPayeeAttr.PROFIT);
        if (myPayTotal == null) {
            myPayTotal = new OceanusMoney();
        }
        Assertions.assertEquals(myDepTotal, myPayTotal, "Payee total mismatch");

        /* Validate transaction totals */
        final MoneyWiseAnalysisTransCategoryBucket myTransTotals = myAnalysis.getTransCategories().getTotals();
        OceanusMoney myEvtTotal = myTransTotals.getValues().getMoneyValue(MoneyWiseAnalysisTransAttr.PROFIT);
        if (myEvtTotal == null) {
            myEvtTotal = new OceanusMoney();
        }
        Assertions.assertEquals(myDepTotal, myEvtTotal, "Transaction total mismatch");

        /* Validate tax totals */
        final MoneyWiseAnalysisTaxBasisBucket myTaxTotals = myAnalysis.getTaxBasis().getTotals();
        final OceanusMoney myTaxTotal = myTaxTotals.getValues().getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS);
        Assertions.assertEquals(myDepTotal, myTaxTotal, "TaxBasis total mismatch");
    }

    /**
     * Create the archiveData tests.
     * @param pView the view
     * @return the testStream
     */
    private Stream<DynamicNode> archiveDataTests(final MoneyWiseView pView) {
        /* Create the stream */
        final MoneyWiseDataSet myData = pView.getNewData();
        final TethysUIThreadManager myThreadMgr = new MoneyWiseNullThreadMgr();
        Stream<DynamicNode> myStream = Stream.of(DynamicTest.dynamicTest("initData",
                () -> new MoneyWiseDataTestArchiveFile(myThreadMgr).performTest(myData, pView)));
        myStream = Stream.concat(myStream, storageTests(myData, pView));
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("analyseData", () -> analyseData(myData, pView))));
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("editSet", () -> checkEditSet(myData, pView))));

        /* Return the stream */
        return Stream.of(DynamicContainer.dynamicContainer("archiveData", myStream));
    }

    /**
     * Create the storage tests.
     * @param pData the dataSet to populate
     * @param pView the view
     * @return the testStream
     */
    static Stream<DynamicNode> storageTests(final MoneyWiseDataSet pData,
                                            final MoneyWiseView pView) {
        /* Return the stream */
        final TethysUIThreadManager myThreadMgr = new MoneyWiseNullThreadMgr();
        return Stream.of(DynamicContainer.dynamicContainer("Storage Tests", Stream.of(
                DynamicTest.dynamicTest("XML File", () -> new MoneyWiseDataTestXMLFile(myThreadMgr).performTest(pData, pView)),
                DynamicTest.dynamicTest("ODS File", () -> new MoneyWiseDataTestODSFile(myThreadMgr).performTest(pData, pView)),
                DynamicTest.dynamicTest("dataBase", () -> new MoneyWiseDataTestDatabase(myThreadMgr).performTest(pData, pView)))));
    }
}
