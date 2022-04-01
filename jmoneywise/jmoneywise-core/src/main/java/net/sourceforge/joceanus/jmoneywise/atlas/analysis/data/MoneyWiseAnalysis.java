/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.analysis.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseAnalysisBaseResource;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseAnalysisBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseAnalysisEvent;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasis;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Analysis.
 */
public class MoneyWiseAnalysis
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysis> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysis.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisBaseResource.HISTORY_RANGE, MoneyWiseAnalysis::getRange);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_EVENTS, MoneyWiseAnalysis::getEvents);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_DEPOSITS, MoneyWiseAnalysis::getDeposits);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_CASH, MoneyWiseAnalysis::getCash);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_LOANS, MoneyWiseAnalysis::getLoans);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_PORTFOLIOS, MoneyWiseAnalysis::getPortfolios);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_HOLDINGS, MoneyWiseAnalysis::getHoldings);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_PAYEES, MoneyWiseAnalysis::getPayees);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_TRANS, MoneyWiseAnalysis::getTrans);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_TAXBASES, MoneyWiseAnalysis::getTaxBases);
    }

    /**
     * The dateRange.
     */
    private final TethysDateRange theRange;

    /**
     * The Events.
     */
    private final MoneyWiseAnalysisEvents theEvents;

    /**
     * The Deposit Map.
     */
    private final Map<Integer, MoneyWiseDepositBucket> theDeposits;

    /**
     * The Cash Map.
     */
    private final Map<Integer, MoneyWiseCashBucket> theCash;

    /**
     * The Loan Map.
     */
    private final Map<Integer, MoneyWiseLoanBucket> theLoans;

    /**
     * The Portfolio Map.
     */
    private final Map<Integer, MoneyWisePortfolioBucket> thePortfolios;

    /**
     * The Holdings Map.
     */
    private final Map<Integer, MoneyWiseSecurityBucket> theHoldings;

    /**
     * The Payees Map.
     */
    private final Map<Integer, MoneyWisePayeeBucket> thePayees;

    /**
     * The Transactions Map.
     */
    private final Map<Integer, MoneyWiseTransactionBucket> theTrans;

    /**
     * The TaxBasis Map.
     */
    private final Map<Integer, MoneyWiseTaxBasisBucket> theTaxBases;

    /**
     * Constructor.
     * @param pDataSet the data
     */
    MoneyWiseAnalysis(final MoneyWiseData pDataSet) {
        /* Create the events */
        theRange = new TethysDateRange(null, null);
        theEvents = new MoneyWiseAnalysisEvents(pDataSet);

        /* Create the maps */
        theDeposits = new HashMap<>();
        theCash = new HashMap<>();
        theLoans = new HashMap<>();
        thePortfolios = new HashMap<>();
        theHoldings = new HashMap<>();
        thePayees = new HashMap<>();
        theTrans = new HashMap<>();
        theTaxBases = new HashMap<>();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pDate the date
     */
    MoneyWiseAnalysis(final MoneyWiseAnalysis pAnalysis,
                      final TethysDate pDate) {
        /* Create the events */
        theRange = new TethysDateRange(null, pDate);
        theEvents = new MoneyWiseAnalysisEvents(pAnalysis.theEvents, pDate);

        /* Create the maps */
        theDeposits = MoneyWiseAnalysisBucket.newMap(pAnalysis.theDeposits, b -> b.newBucket(pDate));
        theCash = MoneyWiseAnalysisBucket.newMap(pAnalysis.theCash, b -> b.newBucket(pDate));
        theLoans = MoneyWiseAnalysisBucket.newMap(pAnalysis.theLoans, b -> b.newBucket(pDate));
        thePortfolios = MoneyWiseAnalysisBucket.newMap(pAnalysis.thePortfolios, b -> b.newBucket(pDate));
        theHoldings = MoneyWiseAnalysisBucket.newMap(pAnalysis.theHoldings, b -> b.newBucket(pDate));
        thePayees = MoneyWiseAnalysisBucket.newMap(pAnalysis.thePayees, b -> b.newBucket(pDate));
        theTrans = MoneyWiseAnalysisBucket.newMap(pAnalysis.theTrans, b -> b.newBucket(pDate));
        theTaxBases = MoneyWiseAnalysisBucket.newMap(pAnalysis.theTaxBases, b -> b.newBucket(pDate));
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pRange the dateRange
     */
    MoneyWiseAnalysis(final MoneyWiseAnalysis pAnalysis,
                      final TethysDateRange pRange) {
        /* Create the events */
        theRange = pRange;
        theEvents = new MoneyWiseAnalysisEvents(pAnalysis.theEvents, pRange);

        /* Create the maps */
        theDeposits = MoneyWiseAnalysisBucket.newMap(pAnalysis.theDeposits, b -> b.newBucket(pRange));
        theCash = MoneyWiseAnalysisBucket.newMap(pAnalysis.theCash, b -> b.newBucket(pRange));
        theLoans = MoneyWiseAnalysisBucket.newMap(pAnalysis.theLoans, b -> b.newBucket(pRange));
        thePortfolios = MoneyWiseAnalysisBucket.newMap(pAnalysis.thePortfolios, b -> b.newBucket(pRange));
        theHoldings = MoneyWiseAnalysisBucket.newMap(pAnalysis.theHoldings, b -> b.newBucket(pRange));
        thePayees = MoneyWiseAnalysisBucket.newMap(pAnalysis.thePayees, b -> b.newBucket(pRange));
        theTrans = MoneyWiseAnalysisBucket.newMap(pAnalysis.theTrans, b -> b.newBucket(pRange));
        theTaxBases = MoneyWiseAnalysisBucket.newMap(pAnalysis.theTaxBases, b -> b.newBucket(pRange));
    }

    /**
     * Get the dateRange.
     * @return the dateRange
     */
    public TethysDateRange getRange() {
        return theRange;
    }

    /**
     * Get the events.
     * @return the events
     */
    public MoneyWiseAnalysisEvents getEvents() {
        return theEvents;
    }

    /**
     * Get the deposit map.
     * @return the map
     */
    public Map<Integer, MoneyWiseDepositBucket> getDeposits() {
        return theDeposits;
    }

    /**
     * Get the cash map.
     * @return the map
     */
    public Map<Integer, MoneyWiseCashBucket> getCash() {
        return theCash;
    }

    /**
     * Get the loan map.
     * @return the map
     */
    public Map<Integer, MoneyWiseLoanBucket> getLoans() {
        return theLoans;
    }

    /**
     * Get the portfolio map.
     * @return the map
     */
    public Map<Integer, MoneyWisePortfolioBucket> getPortfolios() {
        return thePortfolios;
    }

    /**
     * Get the holding map.
     * @return the map
     */
    public Map<Integer, MoneyWiseSecurityBucket> getHoldings() {
        return theHoldings;
    }

    /**
     * Get the payee map.
     * @return the map
     */
    public Map<Integer, MoneyWisePayeeBucket> getPayees() {
        return thePayees;
    }

    /**
     * Get the transactions map.
     * @return the map
     */
    public Map<Integer, MoneyWiseTransactionBucket> getTrans() {
        return theTrans;
    }

    /**
     * Get the taxBasis map.
     * @return the map
     */
    public Map<Integer, MoneyWiseTaxBasisBucket> getTaxBases() {
        return theTaxBases;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain an iterator for the events.
     * @return the iterator
     */
    public Iterator<MoneyWiseAnalysisEvent> eventIterator() {
        return theEvents.getEvents().iterator();
    }

    /**
     * Obtain the reporting currency.
     * @return the currency
     */
    public AssetCurrency getReportingCurrency() {
        return theEvents.getReportingCurrency();
    }

    /**
     * Obtain/create the deposit bucket.
     * @param pDeposit the deposit
     * @return the bucket
     */
    public MoneyWiseDepositBucket getBucket(final Deposit pDeposit) {
        return theDeposits.computeIfAbsent(pDeposit.getId(), d -> new MoneyWiseDepositBucket(pDeposit, this));
    }

    /**
     * Obtain/create the cash bucket.
     * @param pCash the cash
     * @return the bucket
     */
    public MoneyWiseCashBucket getBucket(final Cash pCash) {
        return theCash.computeIfAbsent(pCash.getId(), d -> new MoneyWiseCashBucket(pCash, this));
    }

    /**
     * Obtain/create the loan bucket.
     * @param pLoan the loan
     * @return the bucket
     */
    public MoneyWiseLoanBucket getBucket(final Loan pLoan) {
        return theLoans.computeIfAbsent(pLoan.getId(), d -> new MoneyWiseLoanBucket(pLoan, this));
    }

    /**
     * Obtain/create the portfolio bucket.
     * @param pPortfolio the portfolio
     * @return the bucket
     */
    public MoneyWisePortfolioBucket getBucket(final Portfolio pPortfolio) {
        return thePortfolios.computeIfAbsent(pPortfolio.getId(), d -> new MoneyWisePortfolioBucket(pPortfolio, this));
    }

    /**
     * Obtain/create the security holding bucket.
     * @param pHolding the security holding
     * @return the bucket
     */
    public MoneyWiseSecurityBucket getBucket(final SecurityHolding pHolding) {
        return theHoldings.computeIfAbsent(pHolding.getId(), d -> new MoneyWiseSecurityBucket(pHolding, this));
    }

    /**
     * Obtain/create the payee bucket.
     * @param pPayee the payee
     * @return the bucket
     */
    public MoneyWisePayeeBucket getBucket(final Payee pPayee) {
        return thePayees.computeIfAbsent(pPayee.getId(), d -> new MoneyWisePayeeBucket(pPayee, this));
    }

    /**
     * Obtain/create the transaction bucket.
     * @param pTrans the transaction category
     * @return the bucket
     */
    public MoneyWiseTransactionBucket getBucket(final TransactionCategory pTrans) {
        return theTrans.computeIfAbsent(pTrans.getId(), d -> new MoneyWiseTransactionBucket(pTrans, this));
    }

    /**
     * Obtain/create the taxBasis bucket.
     * @param pTaxBasis the taxBasis
     * @return the bucket
     */
    public MoneyWiseTaxBasisBucket getBucket(final TaxBasis pTaxBasis) {
        return theTaxBases.computeIfAbsent(pTaxBasis.getId(), d -> new MoneyWiseTaxBasisBucket(pTaxBasis, this));
    }
}
