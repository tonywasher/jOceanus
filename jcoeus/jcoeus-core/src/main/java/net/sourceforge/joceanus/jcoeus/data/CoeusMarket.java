/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2021 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jcoeus.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Loan Market.
 */
public abstract class CoeusMarket
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusMarket> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusMarket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_PROVIDER, CoeusMarket::getProvider);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOANS, CoeusMarket::getLoans);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_TRANSACTIONS, CoeusMarket::getTransactions);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_HISTORY, CoeusMarket::getFullHistory);
    }

    /**
     * Loan Market Provider.
     */
    private final CoeusMarketProvider theProvider;

    /**
     * Data Formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * Loan Map.
     */
    private final Map<String, CoeusLoan> theLoanMap;

    /**
     * The list of loans.
     */
    private final List<CoeusLoan> theLoans;

    /**
     * The List of Transactions.
     */
    private final List<CoeusTransaction> theTransactions;

    /**
     * The TotalsHistory.
     */
    private final CoeusHistory theHistory;

    /**
     * The next transactionId.
     */
    private Integer theNextId = 1;

    /**
     * Constructor.
     * @param pFormatter the data formatter
     * @param pProvider the loanMarket provider
     */
    protected CoeusMarket(final MetisDataFormatter pFormatter,
                          final CoeusMarketProvider pProvider) {
        /* Store parameters */
        theFormatter = pFormatter;
        theProvider = pProvider;

        /* Create maps */
        theLoanMap = new LinkedHashMap<>();

        /* Create lists */
        theLoans = new ArrayList<>();
        theTransactions = new ArrayList<>();

        /* Create the history */
        theHistory = newHistory();
    }

    /**
     * Obtain the market.
     * @return the market
     */
    public CoeusMarketProvider getProvider() {
        return theProvider;
    }

    /**
     * Obtain the formatter.
     * @return the formatter
     */
    public MetisDataFormatter getFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the loan iterator.
     * @return the iterator
     */
    public Iterator<CoeusLoan> loanIterator() {
        return theLoans.iterator();
    }

    /**
     * Obtain the loans.
     * @return the loans
     */
    private List<CoeusLoan> getLoans() {
        return theLoans;
    }

    /**
     * Obtain the transaction iterator.
     * @return the iterator
     */
    Iterator<CoeusTransaction> transactionIterator() {
        return theTransactions.iterator();
    }

    /**
     * Obtain the transactions.
     * @return the transactions
     */
    protected List<CoeusTransaction> getTransactions() {
        return theTransactions;
    }

    /**
     * Check loans.
     * @throws OceanusException on error
     */
    protected void checkLoans() throws OceanusException {
        /* Loop through the loans */
        final Iterator<CoeusLoan> myIterator = loanIterator();
        while (myIterator.hasNext()) {
            final CoeusLoan myLoan = myIterator.next();

            /* Check the loan */
            myLoan.checkLoan();
        }
    }

    /**
     * LookUp Loan by loanId.
     * @param pId the id of the loan
     * @return the loan
     * @throws OceanusException on error
     */
    public CoeusLoan findLoanById(final String pId) throws OceanusException {
        final CoeusLoan myLoan = getLoanById(pId);
        if (myLoan == null) {
            throw new CoeusDataException(pId, "Unrecognised LoanId");
        }
        return myLoan;
    }

    /**
     * Obtain preExisting loan.
     * @param pId the id of the loan
     * @return the loan
     */
    public CoeusLoan getLoanById(final String pId) {
        return theLoanMap.get(pId);
    }

    /**
     * Record loan.
     * @param pLoan the loan
     * @throws OceanusException on error
     */
    public void recordLoan(final CoeusLoan pLoan) throws OceanusException {
        recordLoanIdMapping(pLoan.getLoanId(), pLoan);
    }

    /**
     * Record loanId mapping.
     * @param pLoanId the loanId
     * @param pLoan the loan
     * @throws OceanusException on error
     */
    protected void recordLoanIdMapping(final String pLoanId,
                                       final CoeusLoan pLoan) throws OceanusException {
        /* Ensure that the id is unique */
        if (theLoanMap.get(pLoanId) != null) {
            throw new CoeusDataException(pLoanId, "Duplicate LoanId");
        }

        /* Record the loan in the map */
        theLoanMap.put(pLoanId, pLoan);

        /* If the loan is a primary */
        if (pLoanId.equals(pLoan.getLoanId())) {
            theLoans.add(pLoan);
        }
    }

    /**
     * Obtain next transaction id.
     * @return the next transactionId
     */
    Integer getNextTransactionId() {
        final Integer myNext = theNextId;
        theNextId = theNextId + 1;
        return myNext;
    }

    /**
     * Add transaction to list.
     * @param pTrans the transaction
     */
    protected void addTransaction(final CoeusTransaction pTrans) {
        theTransactions.add(pTrans);
    }

    /**
     * Reset the loans.
     */
    private void resetLoans() {
        /* Loop through the loans */
        for (CoeusLoan myLoan : theLoans) {
            myLoan.clearHistory();
        }
        theLoanMap.clear();
        theHistory.clear();
    }

    /**
     * Analyse the market.
     * @throws OceanusException on error
     */
    public abstract void analyseMarket() throws OceanusException;

    /**
     * Create market analysis.
     */
    protected void createAnalysis() {
        /* Sort the transactions and loans */
        theTransactions.sort(Comparator.comparing(CoeusTransaction::getDate));
        theLoans.sort(CoeusLoan::compareTo);

        /* Reset the loans */
        resetLoans();

        /* Loop through the transactions */
        final Iterator<CoeusTransaction> myIterator = transactionIterator();
        while (myIterator.hasNext()) {
            final CoeusTransaction myTransaction = myIterator.next();

            /* Adjust the history */
            theHistory.addTransactionToHistory(myTransaction);

            /* If the item has a loan */
            final CoeusLoan myLoan = myTransaction.getLoan();
            if (myLoan != null) {
                /* Add to the loans history */
                myLoan.addTransactionToHistory(myTransaction);
            }
        }
    }

    /**
     * Obtain full history.
     * @return the full history
     */
    protected CoeusHistory getFullHistory() {
        return theHistory;
    }

    /**
     * Obtain market snapshot.
     * @param pDate the date
     * @return the snapshot
     */
    CoeusMarketSnapShot getSnapshot(final TethysDate pDate) {
        return new CoeusMarketSnapShot(this, pDate);
    }

    /**
     * Obtain market annual.
     * @param pCalendar the calendar
     * @param pDate the date
     * @return the annual
     */
    CoeusMarketAnnual getAnnual(final CoeusCalendar pCalendar,
                                final TethysDate pDate) {
        return new CoeusMarketAnnual(this, pCalendar, pDate);
    }

    /**
     * New totals.
     * @return the totals
     */
    protected abstract CoeusTotals newTotals();

    /**
     * New history.
     * @return the history
     */
    protected abstract CoeusHistory newHistory();

    /**
     * New history view.
     * @param pRange the date range
     * @return the history
     */
    protected CoeusHistory viewHistory(final TethysDateRange pRange) {
        return viewHistory(getFullHistory(), pRange);
    }

    /**
     * New history view.
     * @param pHistory the history
     * @param pRange the date range
     * @return the history
     */
    protected abstract CoeusHistory viewHistory(CoeusHistory pHistory,
                                                TethysDateRange pRange);

    /**
     * New loan.
     * @param pLoanId the loan id
     * @return the loan
     */
    protected abstract CoeusLoan newLoan(String pLoanId);

    /**
     * New loan view.
     * @param pLoan the loan
     * @param pRange the date range
     * @return the loan
     */
    protected abstract CoeusLoan viewLoan(CoeusLoan pLoan,
                                          TethysDateRange pRange);

    /**
     * Does the market use decimal totals rather than money?
     * @return true/false
     */
    public abstract boolean usesDecimalTotals();

    /**
     * Does the market have any badDebt?
     * @return true/false
     */
    public abstract boolean hasBadDebt();
}
