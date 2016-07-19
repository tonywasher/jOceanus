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
import net.sourceforge.joceanus.jcoeus.CoeusResource;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysFiscalYear;

/**
 * Loan Market.
 * @param <L> the loan type
 * @param <T> the transaction type
 * @param <S> the totals type
 * @param <H> the history type
 */
public abstract class CoeusLoanMarket<L extends CoeusLoan<L, T, S, H>, T extends CoeusTransaction<L, T, S, H>, S extends CoeusTotals<L, T, S, H>, H extends CoeusHistory<L, T, S, H>>
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusLoanMarket.class.getSimpleName());

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
     * Totals Field Id.
     */
    private static final MetisField FIELD_TOTALS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALS.getValue());

    /**
     * MonthlyDeltas Field Id.
     */
    private static final MetisField FIELD_MONTHLYDELTAS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_MONTHLYDELTAS.getValue());

    /**
     * AnnualDeltas Field Id.
     */
    private static final MetisField FIELD_ANNUALDELTAS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_ANNUALDELTAS.getValue());

    /**
     * MonthlyTotals Field Id.
     */
    private static final MetisField FIELD_MONTHLYTOTALS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_MONTHLYTOTALS.getValue());

    /**
     * Loan Market Provider.
     */
    private final CoeusLoanMarketProvider theProvider;

    /**
     * Data Formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * FiscalYear.
     */
    private final TethysFiscalYear theFiscalYear;

    /**
     * Loan Map.
     */
    private final Map<String, L> theLoanMap;

    /**
     * The List of Transactions.
     */
    private final List<T> theTransactions;

    /**
     * The TotalsHistory.
     */
    private final H theHistory;

    /**
     * The Map of MonthlyHistories.
     */
    private final Map<TethysDate, H> theMonthlyHistories;

    /**
     * The Map of AnnualHistories.
     */
    private final Map<TethysDate, H> theAnnualHistories;

    /**
     * The List of MonthlyTotals.
     */
    private final List<S> theMonthlyTotals;

    /**
     * Use fiscal monthly totals.
     */
    private boolean makeFiscalTotals;

    /**
     * Constructor.
     * @param pFormatter the data formatter
     * @param pProvider the loanMarket provider
     */
    protected CoeusLoanMarket(final MetisDataFormatter pFormatter,
                              final CoeusLoanMarketProvider pProvider) {
        /* Store parameters */
        theFormatter = pFormatter;
        theProvider = pProvider;

        /* Determine the fiscal year */
        theFiscalYear = TethysFiscalYear.determineFiscalYear(theFormatter.getLocale());

        /* Create maps */
        theLoanMap = new LinkedHashMap<>();

        /* Create lists */
        theTransactions = new ArrayList<>();
        theMonthlyTotals = new ArrayList<>();

        /* Create the histories */
        theHistory = newHistory();
        theMonthlyHistories = new LinkedHashMap<>();
        theAnnualHistories = new LinkedHashMap<>();

        /* Use fiscal monthly totals by default */
        makeFiscalTotals = true;
    }

    /**
     * Obtain the market.
     * @return the market
     */
    public CoeusLoanMarketProvider getProvider() {
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
    public Iterator<L> loanIterator() {
        return theLoanMap.values().iterator();
    }

    /**
     * Obtain the transaction iterator.
     * @return the iterator
     */
    public Iterator<T> transactionIterator() {
        return theTransactions.iterator();
    }

    /**
     * Obtain the transactions.
     * @return the transactions
     */
    protected List<T> getTransactions() {
        return theTransactions;
    }

    /**
     * Make monthly totals.
     */
    public void makeMonthlyTotals() {
        makeFiscalTotals = false;
    }

    /**
     * Check loans.
     * @throws OceanusException on error
     */
    public void checkLoans() throws OceanusException {
        /* Loop through the loans */
        Iterator<L> myIterator = loanIterator();
        while (myIterator.hasNext()) {
            L myLoan = myIterator.next();

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
    public L findLoanById(final String pId) throws OceanusException {
        L myLoan = getLoanById(pId);
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
    protected L getLoanById(final String pId) throws OceanusException {
        return theLoanMap.get(pId);
    }

    /**
     * Record loan.
     * @param pLoan the loan
     * @throws OceanusException on error
     */
    protected void recordLoan(final L pLoan) throws OceanusException {
        /* Ensure that the id is unique */
        String myId = pLoan.getLoanId();
        if (theLoanMap.get(myId) != null) {
            throw new CoeusDataException(myId, "Duplicate LoanId");
        }

        /* Record the loan */
        theLoanMap.put(myId, pLoan);
    }

    /**
     * Add transaction to list.
     * @param pTrans the transaction
     */
    protected void addTransaction(final T pTrans) {
        theTransactions.add(pTrans);
    }

    /**
     * Reset the loans.
     */
    private void resetLoans() {
        /* Loop through the loans */
        Iterator<L> myIterator = theLoanMap.values().iterator();
        while (myIterator.hasNext()) {
            L myLoan = myIterator.next();
            myLoan.clearHistory();
        }
    }

    /**
     * Analyse the market.
     */
    public void analyseMarket() {
        /* Clear the history */
        theHistory.clear();
        theMonthlyHistories.clear();
        theAnnualHistories.clear();
        theMonthlyTotals.clear();
        resetLoans();

        /* Sort the transactions */
        theTransactions.sort((l, r) -> l.getDate().compareTo(r.getDate()));

        /* Loop through the transactions */
        Iterator<T> myIterator = transactionIterator();
        while (myIterator.hasNext()) {
            T myTransaction = myIterator.next();
            TethysDate myDate = myTransaction.getDate();

            /* Obtain the monthly history and adjust */
            H myHistory = getMonthlyHistory(myDate);
            myHistory.addTransactionToHistory(myTransaction);

            /* Obtain the annual history and adjust */
            myHistory = getAnnualHistory(myDate);
            myHistory.addTransactionToHistory(myTransaction);

            /* Adjust the history */
            theHistory.addTransactionToHistory(myTransaction);

            /* If the item has a loan */
            L myLoan = myTransaction.getLoan();
            if (myLoan != null) {
                /* Add to the loans history */
                myLoan.addTransactionToHistory(myTransaction);
            }
        }

        /* Build monthly totals */
        buildMonthlyTotals();
    }

    /**
     * Obtain monthly history.
     * @param pDate the date
     * @return the history
     */
    private H getMonthlyHistory(final TethysDate pDate) {
        /* Determine the date of the month */
        TethysDate myDate = makeFiscalTotals
                                             ? theFiscalYear.endOfMonth(pDate)
                                             : new TethysDate(pDate);
        myDate.endCalendarMonth();

        /* Look up an existing history */
        H myHistory = theMonthlyHistories.get(myDate);
        if (myHistory == null) {
            /* Create new history and record it */
            myHistory = newHistory(myDate);
            theMonthlyHistories.put(myDate, myHistory);
        }

        /* Return the history */
        return myHistory;
    }

    /**
     * Obtain annual history.
     * @param pDate the date
     * @return the history
     */
    private H getAnnualHistory(final TethysDate pDate) {
        /* Determine the date of the month */
        TethysDate myDate = theFiscalYear.endOfYear(pDate);

        /* Look up an existing history */
        H myHistory = theAnnualHistories.get(myDate);
        if (myHistory == null) {
            /* Create new totals and record them */
            myHistory = newHistory(myDate);
            theAnnualHistories.put(myDate, myHistory);
        }

        /* Return the history */
        return myHistory;
    }

    /**
     * Build monthly totals.
     */
    private void buildMonthlyTotals() {
        /* Create a base Totals */
        S myBase = newTotals();

        /* Loop through the monthly deltas */
        Iterator<H> myIterator = theMonthlyHistories.values().iterator();
        while (myIterator.hasNext()) {
            H myHistory = myIterator.next();
            S myDelta = myHistory.getTotals();
            TethysDate myDate = myDelta.getDate();

            /* Adjust totals */
            myBase.addTotalsToTotals(myDelta);
            S myTotals = newTotals(myDate, myBase);

            /* Add to the list */
            theMonthlyTotals.add(myTotals);
        }
    }

    /**
     * New totals.
     * @return the totals
     */
    protected abstract S newTotals();

    /**
     * New totals.
     * @param pDate the date
     * @param pTotals the totals
     * @return the totals
     */
    protected abstract S newTotals(final TethysDate pDate,
                                   final S pTotals);

    /**
     * New history.
     * @return the history
     */
    protected abstract H newHistory();

    /**
     * New dated history.
     * @param pDate the date
     * @return the history
     */
    protected abstract H newHistory(final TethysDate pDate);

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
        if (FIELD_TOTALS.equals(pField)) {
            return theHistory;
        }
        if (FIELD_MONTHLYDELTAS.equals(pField)) {
            return theMonthlyHistories;
        }
        if (FIELD_ANNUALDELTAS.equals(pField)) {
            return theAnnualHistories;
        }
        if (FIELD_MONTHLYTOTALS.equals(pField)) {
            return theMonthlyTotals;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }
}
