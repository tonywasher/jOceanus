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
package net.sourceforge.joceanus.jcoeus.data.lendingworks;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * LendingWorks Transaction Totals.
 */
public class CoeusLendingWorksTotals
        extends CoeusTotals {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusLendingWorksTotals.class.getSimpleName(), CoeusTotals.getBaseFields());

    /**
     * AssetValue.
     */
    private final TethysDecimal theTotalAssetValue;

    /**
     * Holding.
     */
    private final TethysDecimal theTotalHolding;

    /**
     * LoanBook.
     */
    private final TethysDecimal theTotalLoanBook;

    /**
     * SourceValue.
     */
    private final TethysDecimal theTotalSourceValue;

    /**
     * Invested.
     */
    private final TethysDecimal theTotalInvested;

    /**
     * Earnings.
     */
    private final TethysDecimal theTotalEarnings;

    /**
     * Interest.
     */
    private final TethysDecimal theTotalInterest;

    /**
     * Constructor for zeroed totals.
     * @param pMarket the market
     */
    protected CoeusLendingWorksTotals(final CoeusLendingWorksMarket pMarket) {
        this(pMarket, null, null);
    }

    /**
     * Constructor for zeroed period totals.
     * @param pMarket the market
     * @param pDate the end date for the totals
     */
    protected CoeusLendingWorksTotals(final CoeusLendingWorksMarket pMarket,
                                      final TethysDate pDate) {
        this(pMarket, null, pDate);
    }

    /**
     * Constructor for zeroed totals.
     * @param pLoan the loan
     */
    protected CoeusLendingWorksTotals(final CoeusLendingWorksLoan pLoan) {
        this(pLoan.getMarket(), pLoan, null);
    }

    /**
     * Constructor for zeroed totals.
     * @param pLoan the loan
     * @param pDate the end date for the totals
     */
    protected CoeusLendingWorksTotals(final CoeusLendingWorksLoan pLoan,
                                      final TethysDate pDate) {
        this(pLoan.getMarket(), pLoan, pDate);
    }

    /**
     * Constructor for zeroed totals.
     * @param pMarket the market
     * @param pLoan the loan
     * @param pDate the end date for the totals
     */
    protected CoeusLendingWorksTotals(final CoeusLendingWorksMarket pMarket,
                                      final CoeusLendingWorksLoan pLoan,
                                      final TethysDate pDate) {
        /* Initialise underlying class */
        super(pMarket, pLoan, pDate);

        /* Initialise values */
        theTotalAssetValue = new TethysDecimal(CoeusLendingWorksTransaction.ZERO_MONEY);
        theTotalHolding = new TethysDecimal(CoeusLendingWorksTransaction.ZERO_MONEY);
        theTotalLoanBook = new TethysDecimal(CoeusLendingWorksTransaction.ZERO_MONEY);
        theTotalSourceValue = new TethysDecimal(CoeusLendingWorksTransaction.ZERO_MONEY);
        theTotalInvested = new TethysDecimal(CoeusLendingWorksTransaction.ZERO_MONEY);
        theTotalEarnings = new TethysDecimal(CoeusLendingWorksTransaction.ZERO_MONEY);
        theTotalInterest = new TethysDecimal(CoeusLendingWorksTransaction.ZERO_MONEY);
    }

    /**
     * Constructor for running total.
     * @param pTotals the previous totals
     * @param pUnderlying the underlying transaction
     */
    protected CoeusLendingWorksTotals(final CoeusLendingWorksTotals pTotals,
                                      final CoeusLendingWorksTransaction pUnderlying) {
        /* Initialise underlying class */
        super(pUnderlying);

        /* Initialise values from previous totals */
        theTotalAssetValue = new TethysDecimal(pTotals.getTotalAssetValue());
        theTotalHolding = new TethysDecimal(pTotals.getTotalHolding());
        theTotalLoanBook = new TethysDecimal(pTotals.getTotalLoanBook());
        theTotalSourceValue = new TethysDecimal(pTotals.getTotalSourceValue());
        theTotalInvested = new TethysDecimal(pTotals.getTotalInvested());
        theTotalEarnings = new TethysDecimal(pTotals.getTotalEarnings());
        theTotalInterest = new TethysDecimal(pTotals.getTotalInterest());
    }

    /**
     * Constructor for monthly totals.
     * @param pDate the date
     * @param pTotals the totals
     */
    protected CoeusLendingWorksTotals(final TethysDate pDate,
                                      final CoeusLendingWorksTotals pTotals) {
        /* Initialise underlying class */
        super(pTotals.getMarket(), pTotals.getLoan(), pDate);

        /* Initialise values from previous totals */
        theTotalAssetValue = new TethysDecimal(pTotals.getTotalAssetValue());
        theTotalHolding = new TethysDecimal(pTotals.getTotalHolding());
        theTotalLoanBook = new TethysDecimal(pTotals.getTotalLoanBook());
        theTotalSourceValue = new TethysDecimal(pTotals.getTotalSourceValue());
        theTotalInvested = new TethysDecimal(pTotals.getTotalInvested());
        theTotalEarnings = new TethysDecimal(pTotals.getTotalEarnings());
        theTotalInterest = new TethysDecimal(pTotals.getTotalInterest());
    }

    @Override
    protected void addTotalsToTotals(final CoeusTotals pTotals) {
        /* Add values from totals */
        theTotalAssetValue.addValue(pTotals.getTotalAssetValue());
        theTotalHolding.addValue(pTotals.getTotalHolding());
        theTotalLoanBook.addValue(pTotals.getTotalLoanBook());
        theTotalSourceValue.addValue(pTotals.getTotalSourceValue());
        theTotalInvested.addValue(pTotals.getTotalInvested());
        theTotalEarnings.addValue(pTotals.getTotalEarnings());
        theTotalInterest.addValue(pTotals.getTotalInterest());
    }

    @Override
    protected void addTransactionToTotals(final CoeusTransaction pTransaction) {
        /* Add values from transaction */
        theTotalInvested.addValue(pTransaction.getInvested());
        theTotalHolding.addValue(pTransaction.getHolding());
        theTotalLoanBook.addValue(pTransaction.getLoanBook());
        theTotalInterest.addValue(pTransaction.getInterest());

        /* Adjust earnings */
        theTotalEarnings.addValue(pTransaction.getInterest());
        theTotalEarnings.subtractValue(pTransaction.getFees());

        /* Adjust asset values */
        theTotalAssetValue.addValue(pTransaction.getHolding());
        theTotalAssetValue.addValue(pTransaction.getLoanBook());

        /* Adjust source values */
        theTotalSourceValue.addValue(pTransaction.getInvested());
        theTotalSourceValue.addValue(pTransaction.getInterest());
    }

    @Override
    public TethysDecimal getTotalAssetValue() {
        return theTotalAssetValue;
    }

    @Override
    public TethysDecimal getTotalHolding() {
        return theTotalHolding;
    }

    @Override
    public TethysDecimal getTotalLoanBook() {
        return theTotalLoanBook;
    }

    @Override
    public TethysDecimal getTotalSourceValue() {
        return theTotalSourceValue;
    }

    @Override
    public TethysDecimal getTotalInvested() {
        return theTotalInvested;
    }

    @Override
    public TethysDecimal getTotalEarnings() {
        return theTotalEarnings;
    }

    @Override
    public TethysDecimal getTotalTaxableEarnings() {
        return theTotalInterest;
    }

    @Override
    public TethysDecimal getTotalInterest() {
        return theTotalInterest;
    }

    @Override
    public TethysDecimal getTotalFees() {
        return CoeusLendingWorksTransaction.ZERO_MONEY;
    }

    @Override
    public TethysDecimal getTotalBadDebtInterest() {
        return CoeusLendingWorksTransaction.ZERO_MONEY;
    }

    @Override
    public TethysDecimal getTotalBadDebtCapital() {
        return CoeusLendingWorksTransaction.ZERO_MONEY;
    }

    @Override
    public TethysDecimal getTotalCashBack() {
        return CoeusLendingWorksTransaction.ZERO_MONEY;
    }

    @Override
    public TethysDecimal getTotalLosses() {
        return CoeusLendingWorksTransaction.ZERO_MONEY;
    }

    @Override
    public TethysDecimal getTotalBadDebt() {
        return CoeusLendingWorksTransaction.ZERO_MONEY;
    }

    @Override
    public TethysDecimal getTotalRecovered() {
        return CoeusLendingWorksTransaction.ZERO_MONEY;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }
}
