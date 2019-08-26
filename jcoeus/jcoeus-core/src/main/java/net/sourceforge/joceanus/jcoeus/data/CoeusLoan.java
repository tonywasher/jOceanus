/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2019 Tony Washer
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

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Coeus Loan.
 */
public abstract class CoeusLoan
        implements MetisFieldItem, Comparable<CoeusLoan> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusLoan> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusLoan.class);

    /**
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
    private TethysDate theStartDate;

    /**
     * The InitialLoan.
     */
    private TethysDecimal theInitialLoan;

    /**
     * The Last date.
     */
    private TethysDate theLastDate;

    /**
     * The BadDebt date.
     */
    private TethysDate theBadDebtDate;

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
     * Obtain the balance.
     * @return the balance
     */
    public abstract TethysDecimal getBalance();

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
    public TethysDate getStartDate() {
        return theStartDate;
    }

    /**
     * Obtain the initialLoan.
     * @return the loan
     */
    public TethysDecimal getInitialLoan() {
        return theInitialLoan;
    }

    /**
     * Obtain the last date.
     * @return the date
     */
    public TethysDate getLastDate() {
        return theLastDate;
    }

    /**
     * Set the badDebt date.
     * @param pDate the date
     */
    public void setBadDebtDate(final TethysDate pDate) {
        theBadDebtDate = pDate;
    }

    /**
     * Obtain the badDebt date.
     * @return the date
     */
    public TethysDate getBadDebtDate() {
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
            final TethysDecimal myInitLoan = pTrans.getLoanBook();

            /* If we have an initialLoan */
            if (myInitLoan.isNonZero()) {
                /* record date and initial loan */
                theStartDate = theLastDate;
                theInitialLoan = pTrans.getLoanBook();
            }
        }

        /* If the loan has zero Capital outstanding */
        final CoeusTotals myTotals = theHistory.getTotals();
        if (theStartDate != null
            && myTotals.getLoanBook().isZero()) {
            /* Determine outstanding badDebt */
            final TethysDecimal myBadDebt = myTotals.getBadDebt();
            final TethysDecimal myRecovered = myTotals.getRecovered();

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
     * New history.
     * @return the history
     */
    protected abstract CoeusHistory newHistory();

    /**
     * New dated history.
     * @param pDate the date
     * @return the history
     */
    protected abstract CoeusHistory newHistory(TethysDate pDate);

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
