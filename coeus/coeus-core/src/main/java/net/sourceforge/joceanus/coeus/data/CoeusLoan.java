/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.coeus.data;

import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;

/**
 * Coeus Loan.
 */
public abstract class CoeusLoan
        implements MetisFieldItem, Comparable<CoeusLoan> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusLoan> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusLoan.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_MARKET, CoeusLoan::getMarket);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOANID, CoeusLoan::getLoanId);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_HISTORY, CoeusLoan::getHistory);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_STARTDATE, CoeusLoan::getStartDate);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LENT, CoeusLoan::getInitialLoan);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LASTDATE, CoeusLoan::getLastDate);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_BADDEBTDATE, CoeusLoan::getBadDebtDate);
    }

    /**
     * Loan Market.
     */
    private final CoeusMarket theMarket;

    /**
     * Loan Id.
     */
    private final String theLoanId;

    /**
     * The TotalsHistory.
     */
    private final CoeusHistory theHistory;

    /**
     * The Status.
     */
    private CoeusLoanStatus theStatus;

    /**
     * The Start date.
     */
    private OceanusDate theStartDate;

    /**
     * The InitialLoan.
     */
    private OceanusDecimal theInitialLoan;

    /**
     * The Last date.
     */
    private OceanusDate theLastDate;

    /**
     * The BadDebt date.
     */
    private OceanusDate theBadDebtDate;

    /**
     * Constructor.
     * @param pMarket the loanMarket
     * @param pId the loan Id
     */
    protected CoeusLoan(final CoeusMarket pMarket,
                        final String pId) {
        /* Store parameters */
        theMarket = pMarket;
        theLoanId = pId;

        /* Create the histories */
        theHistory = newHistory();

        /* Initial status is Active */
        theStatus = CoeusLoanStatus.ACTIVE;
    }

    /**
     * Constructor for partial view.
     * @param pLoan the base loan
     * @param pRange the date range
     */
    protected CoeusLoan(final CoeusLoan pLoan,
                        final OceanusDateRange pRange) {
        /* Record details */
        theMarket = pLoan.getMarket();
        theLoanId = pLoan.getLoanId();

        /* Create the cut down history */
        theHistory = theMarket.viewHistory(pLoan.getHistory(), pRange);

        /* Copy details from loan */
        theStartDate = pLoan.getStartDate();
        theInitialLoan = pLoan.getInitialLoan();
        theLastDate = pLoan.getLastDate();

        /* Determine badDebtDate */
        final OceanusDate myBadDebtDate = pLoan.getBadDebtDate();
        final OceanusDate myEndDate = pRange.getEnd();
        theBadDebtDate = myBadDebtDate != null
                                 && myBadDebtDate.compareTo(myEndDate) <= 0
                         ? myBadDebtDate
                         : null;

        /* Determine lastDate */
        if (theLastDate != null
                && theLastDate.compareTo(myEndDate) > 0) {
            theLastDate = theHistory.getTotals().getDate();
        }

        /* Determine status */
        theStatus = CoeusLoanStatus.ACTIVE;
        adjustStatus();
    }

    /**
     * Obtain the market.
     * @return the market
     */
    public CoeusMarket getMarket() {
        return theMarket;
    }

    /**
     * Obtain the loan Id.
     * @return the id
     */
    public String getLoanId() {
        return theLoanId;
    }

    /**
     * Obtain the history.
     * @return the history
     */
    public CoeusHistory getHistory() {
        return theHistory;
    }

    /**
     * Obtain the totals.
     * @return the totals
     */
    public CoeusTotals getTotals() {
        return theHistory.getTotals();
    }

    /**
     * Obtain the status.
     * @return the status
     */
    public CoeusLoanStatus getStatus() {
        return theStatus;
    }

    /**
     * Is the history empty?
     * @return true/false
     */
    public boolean isEmpty() {
        return theHistory.isEmpty();
    }

    /**
     * Obtain the balance.
     * @return the balance
     */
    public abstract OceanusDecimal getBalance();

    /**
     * Clear the history.
     */
    void clearHistory() {
        theHistory.clear();
    }

    /**
     * Obtain the start date.
     * @return the date
     */
    public OceanusDate getStartDate() {
        return theStartDate;
    }

    /**
     * Obtain the initialLoan.
     * @return the loan
     */
    public OceanusDecimal getInitialLoan() {
        return theInitialLoan;
    }

    /**
     * Obtain the last date.
     * @return the date
     */
    public OceanusDate getLastDate() {
        return theLastDate;
    }

    /**
     * Set the badDebt date.
     * @param pDate the date
     */
    public void setBadDebtDate(final OceanusDate pDate) {
        theBadDebtDate = pDate;
    }

    /**
     * Obtain the badDebt date.
     * @return the date
     */
    public OceanusDate getBadDebtDate() {
        return theBadDebtDate;
    }

    /**
     * Is this a badDebtCapital loan.
     * @return true/false
     */
    public boolean isBadDebtCapital() {
        return theBadDebtDate != null
               && CoeusLoanStatus.isCapitalBadDebt(theBadDebtDate);
    }

    /**
     * Add the transaction to the history.
     * @param pTrans the transaction
     */
    void addTransactionToHistory(final CoeusTransaction pTrans) {
        /* Adjust the history */
        theHistory.addTransactionToHistory(pTrans);

        /* Record dates */
        theLastDate = pTrans.getDate();
        if (theStartDate == null) {
            /* Access the loanBook delta */
            final OceanusDecimal myInitLoan = pTrans.getLoanBook();

            /* If we have an initialLoan */
            if (myInitLoan.isNonZero()) {
                /* record date and initial loan */
                theStartDate = theLastDate;
                theInitialLoan = pTrans.getLoanBook();
            }
        }

        /* Adjust the status */
        adjustStatus();
    }

    /**
     * Adjust status.
     */
    void adjustStatus() {
        /* If the loan has zero Capital outstanding */
        final CoeusTotals myTotals = theHistory.getTotals();
        if (theStartDate != null
                && myTotals.getLoanBook().isZero()) {
            /* Determine outstanding badDebt */
            final OceanusDecimal myBadDebt = myTotals.getBadDebt();
            final OceanusDecimal myRecovered = myTotals.getRecovered();

            /* Check whether this is a rePaid badDebt */
            final boolean isBadDebt = myBadDebt.isNonZero();
            final boolean isRePaid = myBadDebt.equals(myRecovered);

            /* Must be either badDebt or rePaid */
            theStatus = isBadDebt && !isRePaid
                        ? CoeusLoanStatus.BADDEBT
                        : CoeusLoanStatus.REPAID;
        }
    }

    /**
     * Obtain the dateRange of this loan.
     * @return the dateRange.
     */
    public OceanusDateRange getDateRange() {
        final CoeusTotals myFirst = theHistory.getHistory().iterator().next();
        return new OceanusDateRange(myFirst.getDate(), theLastDate);
    }

    /**
     * New history.
     * @return the history
     */
    protected abstract CoeusHistory newHistory();

    /**
     * CheckLoan.
     * @throws OceanusException on error
     */
    protected abstract void checkLoan() throws OceanusException;

    @Override
    public int compareTo(final CoeusLoan pThat) {
        return theLoanId.compareTo(pThat.getLoanId());
    }

    @Override
    public boolean equals(final Object pThat) {
        if (!(pThat instanceof CoeusLoan)) {
            return false;
        }
        return theLoanId.equals(((CoeusLoan) pThat).getLoanId());
    }

    @Override
    public int hashCode() {
        return theLoanId.hashCode();
    }

    @Override
    public String toString() {
        return theLoanId;
    }
}
