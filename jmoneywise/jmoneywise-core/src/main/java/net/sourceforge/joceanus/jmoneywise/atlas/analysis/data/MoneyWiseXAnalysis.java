/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseXAnalysisBaseResource;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseXAnalysisBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseXAnalysisEvent;
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
public class MoneyWiseXAnalysis
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysis> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysis.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.HISTORY_RANGE, MoneyWiseXAnalysis::getRange);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisDataResource.ANALYSIS_EVENTS, MoneyWiseXAnalysis::getEvents);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisDataResource.ANALYSIS_DEPOSITS, MoneyWiseXAnalysis::getDeposits);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisDataResource.ANALYSIS_CASH, MoneyWiseXAnalysis::getCash);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisDataResource.ANALYSIS_LOANS, MoneyWiseXAnalysis::getLoans);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisDataResource.ANALYSIS_PORTFOLIOS, MoneyWiseXAnalysis::getPortfolios);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisDataResource.ANALYSIS_HOLDINGS, MoneyWiseXAnalysis::getHoldings);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisDataResource.ANALYSIS_PAYEES, MoneyWiseXAnalysis::getPayees);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisDataResource.ANALYSIS_TRANS, MoneyWiseXAnalysis::getTrans);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisDataResource.ANALYSIS_TAXBASES, MoneyWiseXAnalysis::getTaxBases);
    }

    /**
     * The dateRange.
     */
    private final TethysDateRange theRange;

    /**
     * The Events.
     */
    private final MoneyWiseXAnalysisEvents theEvents;

    /**
     * The Deposit Map.
     */
    private final Map<Integer, MoneyWiseXDepositBucket> theDeposits;

    /**
     * The Cash Map.
     */
    private final Map<Integer, MoneyWiseXCashBucket> theCash;

    /**
     * The Loan Map.
     */
    private final Map<Integer, MoneyWiseXLoanBucket> theLoans;

    /**
     * The Portfolio Map.
     */
    private final Map<Integer, MoneyWiseXPortfolioBucket> thePortfolios;

    /**
     * The Holdings Map.
     */
    private final Map<Integer, MoneyWiseXSecurityBucket> theHoldings;

    /**
     * The Payees Map.
     */
    private final Map<Integer, MoneyWiseXPayeeBucket> thePayees;

    /**
     * The Transactions Map.
     */
    private final Map<Integer, MoneyWiseXTransactionBucket> theTrans;

    /**
     * The TaxBasis Map.
     */
    private final Map<Integer, MoneyWiseXTaxBasisBucket> theTaxBases;

    /**
     * Constructor.
     * @param pDataSet the data
     */
    MoneyWiseXAnalysis(final MoneyWiseData pDataSet) {
        /* Create the events */
        theRange = new TethysDateRange(null, null);
        theEvents = new MoneyWiseXAnalysisEvents(pDataSet);

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
    MoneyWiseXAnalysis(final MoneyWiseXAnalysis pAnalysis,
                       final TethysDate pDate) {
        /* Create the events */
        theRange = new TethysDateRange(null, pDate);
        theEvents = new MoneyWiseXAnalysisEvents(pAnalysis.theEvents, pDate);

        /* Create the maps */
        theDeposits = MoneyWiseXAnalysisBucket.newMap(pAnalysis.theDeposits, b -> b.newBucket(pDate));
        theCash = MoneyWiseXAnalysisBucket.newMap(pAnalysis.theCash, b -> b.newBucket(pDate));
        theLoans = MoneyWiseXAnalysisBucket.newMap(pAnalysis.theLoans, b -> b.newBucket(pDate));
        thePortfolios = MoneyWiseXAnalysisBucket.newMap(pAnalysis.thePortfolios, b -> b.newBucket(pDate));
        theHoldings = MoneyWiseXAnalysisBucket.newMap(pAnalysis.theHoldings, b -> b.newBucket(pDate));
        thePayees = MoneyWiseXAnalysisBucket.newMap(pAnalysis.thePayees, b -> b.newBucket(pDate));
        theTrans = MoneyWiseXAnalysisBucket.newMap(pAnalysis.theTrans, b -> b.newBucket(pDate));
        theTaxBases = MoneyWiseXAnalysisBucket.newMap(pAnalysis.theTaxBases, b -> b.newBucket(pDate));
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pRange the dateRange
     */
    MoneyWiseXAnalysis(final MoneyWiseXAnalysis pAnalysis,
                       final TethysDateRange pRange) {
        /* Create the events */
        theRange = pRange;
        theEvents = new MoneyWiseXAnalysisEvents(pAnalysis.theEvents, pRange);

        /* Create the maps */
        theDeposits = MoneyWiseXAnalysisBucket.newMap(pAnalysis.theDeposits, b -> b.newBucket(pRange));
        theCash = MoneyWiseXAnalysisBucket.newMap(pAnalysis.theCash, b -> b.newBucket(pRange));
        theLoans = MoneyWiseXAnalysisBucket.newMap(pAnalysis.theLoans, b -> b.newBucket(pRange));
        thePortfolios = MoneyWiseXAnalysisBucket.newMap(pAnalysis.thePortfolios, b -> b.newBucket(pRange));
        theHoldings = MoneyWiseXAnalysisBucket.newMap(pAnalysis.theHoldings, b -> b.newBucket(pRange));
        thePayees = MoneyWiseXAnalysisBucket.newMap(pAnalysis.thePayees, b -> b.newBucket(pRange));
        theTrans = MoneyWiseXAnalysisBucket.newMap(pAnalysis.theTrans, b -> b.newBucket(pRange));
        theTaxBases = MoneyWiseXAnalysisBucket.newMap(pAnalysis.theTaxBases, b -> b.newBucket(pRange));
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
    public MoneyWiseXAnalysisEvents getEvents() {
        return theEvents;
    }

    /**
     * Get the deposit map.
     * @return the map
     */
    public Map<Integer, MoneyWiseXDepositBucket> getDeposits() {
        return theDeposits;
    }

    /**
     * Get the cash map.
     * @return the map
     */
    public Map<Integer, MoneyWiseXCashBucket> getCash() {
        return theCash;
    }

    /**
     * Get the loan map.
     * @return the map
     */
    public Map<Integer, MoneyWiseXLoanBucket> getLoans() {
        return theLoans;
    }

    /**
     * Get the portfolio map.
     * @return the map
     */
    public Map<Integer, MoneyWiseXPortfolioBucket> getPortfolios() {
        return thePortfolios;
    }

    /**
     * Get the holding map.
     * @return the map
     */
    public Map<Integer, MoneyWiseXSecurityBucket> getHoldings() {
        return theHoldings;
    }

    /**
     * Get the payee map.
     * @return the map
     */
    public Map<Integer, MoneyWiseXPayeeBucket> getPayees() {
        return thePayees;
    }

    /**
     * Get the transactions map.
     * @return the map
     */
    public Map<Integer, MoneyWiseXTransactionBucket> getTrans() {
        return theTrans;
    }

    /**
     * Get the taxBasis map.
     * @return the map
     */
    public Map<Integer, MoneyWiseXTaxBasisBucket> getTaxBases() {
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
    public Iterator<MoneyWiseXAnalysisEvent> eventIterator() {
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
    public MoneyWiseXDepositBucket getBucket(final Deposit pDeposit) {
        return theDeposits.computeIfAbsent(pDeposit.getId(), d -> new MoneyWiseXDepositBucket(pDeposit, this));
    }

    /**
     * Obtain/create the cash bucket.
     * @param pCash the cash
     * @return the bucket
     */
    public MoneyWiseXCashBucket getBucket(final Cash pCash) {
        return theCash.computeIfAbsent(pCash.getId(), d -> new MoneyWiseXCashBucket(pCash, this));
    }

    /**
     * Obtain/create the loan bucket.
     * @param pLoan the loan
     * @return the bucket
     */
    public MoneyWiseXLoanBucket getBucket(final Loan pLoan) {
        return theLoans.computeIfAbsent(pLoan.getId(), d -> new MoneyWiseXLoanBucket(pLoan, this));
    }

    /**
     * Obtain/create the portfolio bucket.
     * @param pPortfolio the portfolio
     * @return the bucket
     */
    public MoneyWiseXPortfolioBucket getBucket(final Portfolio pPortfolio) {
        return thePortfolios.computeIfAbsent(pPortfolio.getId(), d -> new MoneyWiseXPortfolioBucket(pPortfolio, this));
    }

    /**
     * Obtain/create the security holding bucket.
     * @param pHolding the security holding
     * @return the bucket
     */
    public MoneyWiseXSecurityBucket getBucket(final SecurityHolding pHolding) {
        return theHoldings.computeIfAbsent(pHolding.getId(), d -> new MoneyWiseXSecurityBucket(pHolding, this));
    }

    /**
     * Obtain/create the payee bucket.
     * @param pPayee the payee
     * @return the bucket
     */
    public MoneyWiseXPayeeBucket getBucket(final Payee pPayee) {
        return thePayees.computeIfAbsent(pPayee.getId(), d -> new MoneyWiseXPayeeBucket(pPayee, this));
    }

    /**
     * Obtain/create the transaction bucket.
     * @param pTrans the transaction category
     * @return the bucket
     */
    public MoneyWiseXTransactionBucket getBucket(final TransactionCategory pTrans) {
        return theTrans.computeIfAbsent(pTrans.getId(), d -> new MoneyWiseXTransactionBucket(pTrans, this));
    }

    /**
     * Obtain/create the taxBasis bucket.
     * @param pTaxBasis the taxBasis
     * @return the bucket
     */
    public MoneyWiseXTaxBasisBucket getBucket(final TaxBasis pTaxBasis) {
        return theTaxBases.computeIfAbsent(pTaxBasis.getId(), d -> new MoneyWiseXTaxBasisBucket(pTaxBasis, this));
    }
}
