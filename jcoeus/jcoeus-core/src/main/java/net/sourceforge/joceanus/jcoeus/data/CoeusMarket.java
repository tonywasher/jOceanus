/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2017 Tony Washer
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
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataFieldItem;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Loan Market.
 */
public abstract class CoeusMarket
        implements MetisDataFieldItem {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusMarket.class);

    /**
     * Provider Field Id.
     */
    private static final MetisDataField FIELD_PROVIDER = FIELD_DEFS.declareLocalField(CoeusResource.DATA_PROVIDER.getValue());

    /**
     * LoanMap Field Id.
     */
    private static final MetisDataField FIELD_LOANS = FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOANMAP.getValue());

    /**
     * Transactions Field Id.
     */
    private static final MetisDataField FIELD_TRANS = FIELD_DEFS.declareLocalField(CoeusResource.DATA_TRANSACTIONS.getValue());

    /**
     * History Field Id.
     */
    private static final MetisDataField FIELD_HISTORY = FIELD_DEFS.declareLocalField(CoeusResource.DATA_HISTORY.getValue());

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
    public void recordLoanIdMapping(final String pLoanId,
                                    final CoeusLoan pLoan) throws OceanusException {
        /* Ensure that the id is unique */
        if (theLoanMap.get(pLoanId) != null) {
            throw new CoeusDataException(pLoanId, "Duplicate LoanId");
        }

        /* Record the loan */
        theLoanMap.put(pLoanId, pLoan);
    }

    /**
     * Obtain next transaction id.
     * @return the next transactionId
     */
    public Integer getNextTransactionId() {
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
        final Iterator<CoeusLoan> myIterator = theLoanMap.values().iterator();
        while (myIterator.hasNext()) {
            final CoeusLoan myLoan = myIterator.next();
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

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisDataFieldSet getBaseFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Skip zero value.
     * @param pValue the value
     * @return the value if non-zero, else SKIP
     */
    public static Object skipZero(final TethysDecimal pValue) {
        return pValue.isZero()
                               ? MetisDataFieldValue.SKIP
                               : pValue;
    }

    /**
     * Skip empty list.
     * @param pList the list
     * @return the list if non-empty, else SKIP
     */
    public static Object skipEmpty(final List<?> pList) {
        return pList.isEmpty()
                               ? MetisDataFieldValue.SKIP
                               : pList;
    }

    /**
     * Skip null value.
     * @param pValue the value
     * @return the list if non-empty, else SKIP
     */
    public static Object skipNull(final Object pValue) {
        return pValue == null
                              ? MetisDataFieldValue.SKIP
                              : pValue;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
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
        return MetisDataFieldValue.UNKNOWN;
    }
}
