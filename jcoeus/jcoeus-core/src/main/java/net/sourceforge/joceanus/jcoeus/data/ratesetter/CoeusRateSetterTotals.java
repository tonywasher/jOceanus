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
package net.sourceforge.joceanus.jcoeus.data.ratesetter;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * RateSetter Transaction Totals.
 */
public class CoeusRateSetterTotals
        extends CoeusTotals {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusRateSetterTotals.class.getSimpleName(), CoeusTotals.getBaseFields());

    /**
     * Asset Value.
     */
    private final TethysMoney theTotalAssetValue;

    /**
     * Holding.
     */
    private final TethysMoney theTotalHolding;

    /**
     * LoanBook.
     */
    private final TethysMoney theTotalLoanBook;

    /**
     * Source Value.
     */
    private final TethysMoney theTotalSourceValue;

    /**
     * Invested.
     */
    private final TethysMoney theTotalInvested;

    /**
     * Earnings.
     */
    private final TethysMoney theTotalEarnings;

    /**
     * Interest.
     */
    private final TethysMoney theTotalInterest;

    /**
     * Fees.
     */
    private final TethysMoney theTotalFees;

    /**
     * Constructor for zeroed totals.
     * @param pMarket the market
     */
    protected CoeusRateSetterTotals(final CoeusRateSetterMarket pMarket) {
        this(pMarket, null, null);
    }

    /**
     * Constructor for zeroed period totals.
     * @param pMarket the market
     * @param pDate the end date for the totals
     */
    protected CoeusRateSetterTotals(final CoeusRateSetterMarket pMarket,
                                    final TethysDate pDate) {
        this(pMarket, null, pDate);
    }

    /**
     * Constructor for zeroed totals.
     * @param pLoan the loan
     */
    protected CoeusRateSetterTotals(final CoeusRateSetterLoan pLoan) {
        this(pLoan.getMarket(), pLoan, null);
    }

    /**
     * Constructor for zeroed totals.
     * @param pLoan the loan
     * @param pDate the end date for the totals
     */
    protected CoeusRateSetterTotals(final CoeusRateSetterLoan pLoan,
                                    final TethysDate pDate) {
        this(pLoan.getMarket(), pLoan, pDate);
    }

    /**
     * Constructor for zeroed totals.
     * @param pMarket the market
     * @param pLoan the loan
     * @param pDate the end date for the totals
     */
    protected CoeusRateSetterTotals(final CoeusRateSetterMarket pMarket,
                                    final CoeusRateSetterLoan pLoan,
                                    final TethysDate pDate) {
        /* Initialise underlying class */
        super(pMarket, pLoan, pDate);

        /* Initialise values */
        theTotalAssetValue = new TethysMoney(CoeusRateSetterTransaction.ZERO_MONEY);
        theTotalHolding = new TethysMoney(CoeusRateSetterTransaction.ZERO_MONEY);
        theTotalLoanBook = new TethysMoney(CoeusRateSetterTransaction.ZERO_MONEY);
        theTotalSourceValue = new TethysMoney(CoeusRateSetterTransaction.ZERO_MONEY);
        theTotalInvested = new TethysMoney(CoeusRateSetterTransaction.ZERO_MONEY);
        theTotalEarnings = new TethysMoney(CoeusRateSetterTransaction.ZERO_MONEY);
        theTotalInterest = new TethysMoney(CoeusRateSetterTransaction.ZERO_MONEY);
        theTotalFees = new TethysMoney(CoeusRateSetterTransaction.ZERO_MONEY);
    }

    /**
     * Constructor for running total.
     * @param pTotals the previous totals
     * @param pUnderlying the underlying transaction
     */
    protected CoeusRateSetterTotals(final CoeusRateSetterTotals pTotals,
                                    final CoeusRateSetterTransaction pUnderlying) {
        /* Initialise underlying class */
        super(pUnderlying);

        /* Initialise values from previous totals */
        theTotalAssetValue = new TethysMoney(pTotals.getTotalAssetValue());
        theTotalHolding = new TethysMoney(pTotals.getTotalHolding());
        theTotalLoanBook = new TethysMoney(pTotals.getTotalLoanBook());
        theTotalSourceValue = new TethysMoney(pTotals.getTotalSourceValue());
        theTotalInvested = new TethysMoney(pTotals.getTotalInvested());
        theTotalEarnings = new TethysMoney(pTotals.getTotalEarnings());
        theTotalInterest = new TethysMoney(pTotals.getTotalInterest());
        theTotalFees = new TethysMoney(pTotals.getTotalFees());
    }

    @Override
    protected void addTotalsToTotals(final CoeusTotals pTotals) {
        /* Cast correctly */
        CoeusRateSetterTotals myTotals = (CoeusRateSetterTotals) pTotals;

        /* Add values from totals */
        theTotalAssetValue.addAmount(myTotals.getTotalAssetValue());
        theTotalHolding.addAmount(myTotals.getTotalHolding());
        theTotalLoanBook.addAmount(myTotals.getTotalLoanBook());
        theTotalSourceValue.addAmount(myTotals.getTotalSourceValue());
        theTotalInvested.addAmount(myTotals.getTotalInvested());
        theTotalEarnings.addAmount(myTotals.getTotalEarnings());
        theTotalInterest.addAmount(myTotals.getTotalInterest());
        theTotalFees.addAmount(myTotals.getTotalFees());
    }

    @Override
    protected void addTransactionToTotals(final CoeusTransaction pTransaction) {
        /* Cast correctly */
        CoeusRateSetterTransaction myTransaction = (CoeusRateSetterTransaction) pTransaction;

        /* Add values from transaction */
        theTotalInvested.addAmount(myTransaction.getInvested());
        theTotalHolding.addAmount(myTransaction.getHolding());
        theTotalLoanBook.addAmount(myTransaction.getLoanBook());
        theTotalInterest.addAmount(myTransaction.getInterest());
        theTotalFees.addAmount(myTransaction.getFees());

        /* Adjust earnings */
        theTotalEarnings.addAmount(myTransaction.getInterest());
        theTotalEarnings.addAmount(myTransaction.getFees());

        /* Adjust asset values */
        theTotalAssetValue.addAmount(myTransaction.getHolding());
        theTotalAssetValue.addAmount(myTransaction.getLoanBook());

        /* Adjust source values */
        theTotalSourceValue.addAmount(myTransaction.getInvested());
        theTotalSourceValue.addAmount(myTransaction.getInterest());
        theTotalSourceValue.addAmount(myTransaction.getFees());
    }

    @Override
    public TethysMoney getInvested() {
        return (TethysMoney) super.getInvested();
    }

    @Override
    public TethysMoney getHolding() {
        return (TethysMoney) super.getHolding();
    }

    @Override
    public TethysMoney getLoanBook() {
        return (TethysMoney) super.getLoanBook();
    }

    @Override
    public TethysMoney getInterest() {
        return (TethysMoney) super.getInterest();
    }

    @Override
    public TethysMoney getBadDebtInterest() {
        return (TethysMoney) super.getBadDebtInterest();
    }

    @Override
    public TethysMoney getFees() {
        return (TethysMoney) super.getFees();
    }

    @Override
    public TethysMoney getCashBack() {
        return (TethysMoney) super.getCashBack();
    }

    @Override
    public TethysMoney getBadDebt() {
        return (TethysMoney) super.getBadDebt();
    }

    @Override
    public TethysMoney getRecovered() {
        return (TethysMoney) super.getRecovered();
    }

    @Override
    public TethysMoney getTotalAssetValue() {
        return theTotalAssetValue;
    }

    @Override
    public TethysMoney getTotalHolding() {
        return theTotalHolding;
    }

    @Override
    public TethysMoney getTotalLoanBook() {
        return theTotalLoanBook;
    }

    @Override
    public TethysMoney getTotalSourceValue() {
        return theTotalSourceValue;
    }

    @Override
    public TethysMoney getTotalInvested() {
        return theTotalInvested;
    }

    @Override
    public TethysMoney getTotalEarnings() {
        return theTotalEarnings;
    }

    @Override
    public TethysMoney getTotalTaxableEarnings() {
        return theTotalInterest;
    }

    @Override
    public TethysMoney getTotalInterest() {
        return theTotalInterest;
    }

    @Override
    public TethysMoney getTotalFees() {
        return theTotalFees;
    }

    @Override
    public TethysMoney getTotalBadDebtInterest() {
        return CoeusRateSetterTransaction.ZERO_MONEY;
    }

    @Override
    public TethysMoney getTotalBadDebtCapital() {
        return CoeusRateSetterTransaction.ZERO_MONEY;
    }

    @Override
    public TethysMoney getTotalCashBack() {
        return CoeusRateSetterTransaction.ZERO_MONEY;
    }

    @Override
    public TethysMoney getTotalLosses() {
        return CoeusRateSetterTransaction.ZERO_MONEY;
    }

    @Override
    public TethysMoney getTotalBadDebt() {
        return CoeusRateSetterTransaction.ZERO_MONEY;
    }

    @Override
    public TethysMoney getTotalRecovered() {
        return CoeusRateSetterTransaction.ZERO_MONEY;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }
}
