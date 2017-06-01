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

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Coeus Loan.
 */
public abstract class CoeusLoan
        implements MetisDataFieldItem, Comparable<CoeusLoan> {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusLoan.class);

    /**
     * Market Field Id.
     */
    private static final MetisDataField FIELD_MARKET = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_MARKET.getValue());

    /**
     * LoanId Field Id.
     */
    private static final MetisDataField FIELD_LOANID = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANID.getValue());

    /**
     * History Field Id.
     */
    private static final MetisDataField FIELD_HISTORY = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_HISTORY.getValue());

    /**
     * StartDate Field Id.
     */
    private static final MetisDataField FIELD_STARTDATE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_STARTDATE.getValue());

    /**
     * InitialLoan Field Id.
     */
    private static final MetisDataField FIELD_INITIALLOAN = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LENT.getValue());

    /**
     * LastDate Field Id.
     */
    private static final MetisDataField FIELD_LASTDATE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LASTDATE.getValue());

    /**
     * BadDebtDate Field Id.
     */
    private static final MetisDataField FIELD_BADDEBTDATE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_BADDEBTDATE.getValue());

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
    protected void clearHistory() {
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
     * @param pDate the date
     * @param pTrans the transaction
     */
    protected void addTransactionToHistory(final TethysDate pDate,
                                           final CoeusTransaction pTrans) {
        /* Adjust the history */
        theHistory.addTransactionToHistory(pTrans);

        /* Record dates */
        theLastDate = pTrans.getDate();
        if (theStartDate == null) {
            /* Access the loanBook delta */
            TethysDecimal myInitLoan = pTrans.getLoanBook();

            /* If we have an initialLoan */
            if (myInitLoan.isNonZero()) {
                /* record date and initial loan */
                theStartDate = theLastDate;
                theInitialLoan = pTrans.getLoanBook();
            }
        }

        /* If the loan has zero Capital outstanding */
        CoeusTotals myTotals = theHistory.getTotals();
        if (theStartDate != null
            && myTotals.getLoanBook().isZero()) {
            /* Determine outstanding badDebt */
            TethysDecimal myBadDebt = myTotals.getBadDebt();
            TethysDecimal myRecovered = myTotals.getRecovered();

            /* Check whether this is a rePaid badDebt */
            boolean isBadDebt = myBadDebt.isNonZero();
            boolean isRePaid = myBadDebt.equals(myRecovered);

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

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisDataFieldSet getBaseFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String toString() {
        return theLoanId;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle standard fields */
        if (FIELD_MARKET.equals(pField)) {
            return theMarket;
        }
        if (FIELD_LOANID.equals(pField)) {
            return theLoanId;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }
        if (FIELD_STARTDATE.equals(pField)) {
            return theStartDate;
        }
        if (FIELD_INITIALLOAN.equals(pField)) {
            return theInitialLoan;
        }
        if (FIELD_LASTDATE.equals(pField)) {
            return theLastDate;
        }
        if (FIELD_BADDEBTDATE.equals(pField)) {
            return theBadDebtDate == null
                                          ? MetisDataFieldValue.SKIP
                                          : theBadDebtDate;
        }

        /* Not recognised */
        return MetisDataFieldValue.UNKNOWN;
    }
}
