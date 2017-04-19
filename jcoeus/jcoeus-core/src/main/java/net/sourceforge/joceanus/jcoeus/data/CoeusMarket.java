/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Loan Market.
 */
public abstract class CoeusMarket
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusMarket.class.getSimpleName());

    /**
     * Provider Field Id.
     */
    private static final MetisField FIELD_PROVIDER = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_PROVIDER.getValue());

    /**
     * LoanMap Field Id.
     */
    private static final MetisField FIELD_LOANS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANMAP.getValue());

    /**
     * Transactions Field Id.
     */
    private static final MetisField FIELD_TRANS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TRANSACTIONS.getValue());

    /**
     * History Field Id.
     */
    private static final MetisField FIELD_HISTORY = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_HISTORY.getValue());

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
    private Integer theNextId = Integer.valueOf(1);

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

        /* Create transaction list */
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
        return theLoanMap.values().iterator();
    }

    /**
     * Obtain the transaction iterator.
     * @return the iterator
     */
    public Iterator<CoeusTransaction> transactionIterator() {
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
    public void checkLoans() throws OceanusException {
        /* Loop through the loans */
        Iterator<CoeusLoan> myIterator = loanIterator();
        while (myIterator.hasNext()) {
            CoeusLoan myLoan = myIterator.next();

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
        CoeusLoan myLoan = getLoanById(pId);
        if (myLoan == null) {
            throw new CoeusDataException(pId, "Unrecognised LoanId");
        }
        return myLoan;
    }

    /**
     * Obtain preExisting loan.
     * @param pId the id of the loan
     * @return the loan
     * @throws OceanusException on error
     */
    public CoeusLoan getLoanById(final String pId) throws OceanusException {
        return theLoanMap.get(pId);
    }

    /**
     * Record loan.
     * @param pLoan the loan
     * @throws OceanusException on error
     */
    public void recordLoan(final CoeusLoan pLoan) throws OceanusException {
        /* Ensure that the id is unique */
        String myId = pLoan.getLoanId();
        if (theLoanMap.get(myId) != null) {
            throw new CoeusDataException(myId, "Duplicate LoanId");
        }

        /* Record the loan */
        theLoanMap.put(myId, pLoan);
    }

    /**
     * Obtain next transaction id.
     * @return the next transactionId
     */
    public Integer getNextTransactionId() {
        Integer myNext = theNextId;
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
        Iterator<CoeusLoan> myIterator = theLoanMap.values().iterator();
        while (myIterator.hasNext()) {
            CoeusLoan myLoan = myIterator.next();
            myLoan.clearHistory();
        }
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
        /* Clear the history */
        theHistory.clear();
        resetLoans();

        /* Sort the transactions */
        theTransactions.sort((l, r) -> l.getDate().compareTo(r.getDate()));

        /* Loop through the transactions */
        Iterator<CoeusTransaction> myIterator = transactionIterator();
        while (myIterator.hasNext()) {
            CoeusTransaction myTransaction = myIterator.next();
            TethysDate myDate = myTransaction.getDate();

            /* Adjust the history */
            theHistory.addTransactionToHistory(myTransaction);

            /* If the item has a loan */
            CoeusLoan myLoan = myTransaction.getLoan();
            if (myLoan != null) {
                /* Add to the loans history */
                myLoan.addTransactionToHistory(myDate, myTransaction);
            }
        }
    }

    /**
     * Obtain full history.
     * @return the full history
     */
    public CoeusHistory getFullHistory() {
        return theHistory;
    }

    /**
     * Obtain market snapshot.
     * @param pDate the date
     * @return the snapshot
     */
    public CoeusMarketSnapShot getSnapshot(final TethysDate pDate) {
        return new CoeusMarketSnapShot(this, pDate);
    }

    /**
     * Obtain market annual.
     * @param pCalendar the calendar
     * @param pDate the date
     * @return the annual
     */
    public CoeusMarketAnnual getAnnual(final CoeusCalendar pCalendar,
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
     * New loan.
     * @param pLoanId the loan id
     * @return the loan
     */
    protected abstract CoeusLoan newLoan(String pLoanId);

    /**
     * New dated history.
     * @param pDate the date
     * @return the history
     */
    protected abstract CoeusHistory newHistory(TethysDate pDate);

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

    @Override
    public String toString() {
        return formatObject();
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisFields getBaseFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_PROVIDER.equals(pField)) {
            return theProvider;
        }
        if (FIELD_LOANS.equals(pField)) {
            return theLoanMap;
        }
        if (FIELD_TRANS.equals(pField)) {
            return theTransactions;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }
}
