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
package net.sourceforge.joceanus.jcoeus.data.fundingcircle;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * FundingCircle Transaction Totals.
 */
public class CoeusFundingCircleTotals
        extends CoeusTotals {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusFundingCircleTotals.class.getSimpleName(), CoeusTotals.getBaseFields());

    /**
     * AssetValue.
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
     * SourceValue.
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
     * taxableEarnings.
     */
    private final TethysMoney theTotalTaxableEarnings;

    /**
     * Interest.
     */
    private final TethysMoney theTotalInterest;

    /**
     * BadDebtInterest.
     */
    private final TethysMoney theTotalBadDebtInterest;

    /**
     * BadDebtCapital.
     */
    private final TethysMoney theTotalBadDebtCapital;

    /**
     * Fees.
     */
    private final TethysMoney theTotalFees;

    /**
     * CashBack.
     */
    private final TethysMoney theTotalCashBack;

    /**
     * Losses.
     */
    private final TethysMoney theTotalLosses;

    /**
     * BadDebt.
     */
    private final TethysMoney theTotalBadDebt;

    /**
     * Recovered.
     */
    private final TethysMoney theTotalRecovered;

    /**
     * Constructor for zeroed totals.
     * @param pMarket the market
     */
    protected CoeusFundingCircleTotals(final CoeusFundingCircleMarket pMarket) {
        this(pMarket, null, null);
    }

    /**
     * Constructor for zeroed period totals.
     * @param pMarket the market
     * @param pDate the end date for the totals
     */
    protected CoeusFundingCircleTotals(final CoeusFundingCircleMarket pMarket,
                                       final TethysDate pDate) {
        this(pMarket, null, pDate);
    }

    /**
     * Constructor for zeroed totals.
     * @param pLoan the loan
     */
    protected CoeusFundingCircleTotals(final CoeusFundingCircleLoan pLoan) {
        this(pLoan.getMarket(), pLoan, null);
    }

    /**
     * Constructor for zeroed period totals.
     * @param pLoan the loan
     * @param pDate the end date for the totals
     */
    protected CoeusFundingCircleTotals(final CoeusFundingCircleLoan pLoan,
                                       final TethysDate pDate) {
        this(pLoan.getMarket(), pLoan, pDate);
    }

    /**
     * Constructor for zeroed totals.
     * @param pMarket the market
     * @param pLoan the loan
     * @param pDate the end date for the totals
     */
    private CoeusFundingCircleTotals(final CoeusFundingCircleMarket pMarket,
                                     final CoeusFundingCircleLoan pLoan,
                                     final TethysDate pDate) {
        /* Initialise underlying class */
        super(pMarket, pLoan, pDate);

        /* Initialise values */
        theTotalAssetValue = new TethysMoney(CoeusFundingCircleTransaction.ZERO_MONEY);
        theTotalHolding = new TethysMoney(CoeusFundingCircleTransaction.ZERO_MONEY);
        theTotalLoanBook = new TethysMoney(CoeusFundingCircleTransaction.ZERO_MONEY);
        theTotalSourceValue = new TethysMoney(CoeusFundingCircleTransaction.ZERO_MONEY);
        theTotalInvested = new TethysMoney(CoeusFundingCircleTransaction.ZERO_MONEY);
        theTotalEarnings = new TethysMoney(CoeusFundingCircleTransaction.ZERO_MONEY);
        theTotalTaxableEarnings = new TethysMoney(CoeusFundingCircleTransaction.ZERO_MONEY);
        theTotalInterest = new TethysMoney(CoeusFundingCircleTransaction.ZERO_MONEY);
        theTotalBadDebtInterest = new TethysMoney(CoeusFundingCircleTransaction.ZERO_MONEY);
        theTotalBadDebtCapital = new TethysMoney(CoeusFundingCircleTransaction.ZERO_MONEY);
        theTotalFees = new TethysMoney(CoeusFundingCircleTransaction.ZERO_MONEY);
        theTotalCashBack = new TethysMoney(CoeusFundingCircleTransaction.ZERO_MONEY);
        theTotalLosses = new TethysMoney(CoeusFundingCircleTransaction.ZERO_MONEY);
        theTotalBadDebt = new TethysMoney(CoeusFundingCircleTransaction.ZERO_MONEY);
        theTotalRecovered = new TethysMoney(CoeusFundingCircleTransaction.ZERO_MONEY);
    }

    /**
     * Constructor for running total.
     * @param pTotals the previous totals
     * @param pUnderlying the underlying transaction
     */
    protected CoeusFundingCircleTotals(final CoeusFundingCircleTotals pTotals,
                                       final CoeusFundingCircleTransaction pUnderlying) {
        /* Initialise underlying class */
        super(pUnderlying);

        /* Initialise values from previous totals */
        theTotalAssetValue = new TethysMoney(pTotals.getTotalAssetValue());
        theTotalHolding = new TethysMoney(pTotals.getTotalHolding());
        theTotalLoanBook = new TethysMoney(pTotals.getTotalLoanBook());
        theTotalSourceValue = new TethysMoney(pTotals.getTotalSourceValue());
        theTotalInvested = new TethysMoney(pTotals.getTotalInvested());
        theTotalEarnings = new TethysMoney(pTotals.getTotalEarnings());
        theTotalTaxableEarnings = new TethysMoney(pTotals.getTotalTaxableEarnings());
        theTotalInterest = new TethysMoney(pTotals.getTotalInterest());
        theTotalBadDebtInterest = new TethysMoney(pTotals.getTotalBadDebtInterest());
        theTotalBadDebtCapital = new TethysMoney(pTotals.getTotalBadDebtCapital());
        theTotalFees = new TethysMoney(pTotals.getTotalFees());
        theTotalCashBack = new TethysMoney(pTotals.getTotalCashBack());
        theTotalLosses = new TethysMoney(pTotals.getTotalLosses());
        theTotalBadDebt = new TethysMoney(pTotals.getTotalBadDebt());
        theTotalRecovered = new TethysMoney(pTotals.getTotalRecovered());
    }

    @Override
    protected void addTotalsToTotals(final CoeusTotals pTotals) {
        /* Cast correctly */
        CoeusFundingCircleTotals myTotals = (CoeusFundingCircleTotals) pTotals;

        /* Add values from totals */
        theTotalAssetValue.addAmount(myTotals.getTotalAssetValue());
        theTotalHolding.addAmount(myTotals.getTotalHolding());
        theTotalLoanBook.addAmount(myTotals.getTotalLoanBook());
        theTotalSourceValue.addAmount(myTotals.getTotalSourceValue());
        theTotalInvested.addAmount(myTotals.getTotalInvested());
        theTotalEarnings.addAmount(myTotals.getTotalEarnings());
        theTotalTaxableEarnings.addAmount(myTotals.getTotalTaxableEarnings());
        theTotalInterest.addAmount(myTotals.getTotalInterest());
        theTotalBadDebtInterest.addAmount(myTotals.getTotalBadDebtInterest());
        theTotalBadDebtCapital.addAmount(myTotals.getTotalBadDebtCapital());
        theTotalFees.addAmount(myTotals.getTotalFees());
        theTotalCashBack.addAmount(myTotals.getTotalCashBack());
        theTotalLosses.addAmount(myTotals.getTotalLosses());
        theTotalBadDebt.addAmount(myTotals.getTotalBadDebt());
        theTotalRecovered.addAmount(myTotals.getTotalRecovered());
    }

    @Override
    protected void addTransactionToTotals(final CoeusTransaction pTransaction) {
        /* Cast correctly */
        CoeusFundingCircleTransaction myTransaction = (CoeusFundingCircleTransaction) pTransaction;

        /* Add values from transaction */
        theTotalInvested.addAmount(myTransaction.getInvested());
        theTotalHolding.addAmount(myTransaction.getHolding());
        theTotalLoanBook.addAmount(myTransaction.getLoanBook());
        theTotalInterest.addAmount(myTransaction.getInterest());
        theTotalBadDebtInterest.addAmount(myTransaction.getBadDebtInterest());
        theTotalBadDebtCapital.addAmount(myTransaction.getBadDebtCapital());
        theTotalFees.addAmount(myTransaction.getFees());
        theTotalCashBack.addAmount(myTransaction.getCashBack());
        theTotalBadDebt.addAmount(myTransaction.getBadDebt());
        theTotalRecovered.addAmount(myTransaction.getRecovered());

        /* Adjust earnings */
        theTotalEarnings.addAmount(myTransaction.getInterest());
        theTotalEarnings.addAmount(myTransaction.getFees());
        theTotalEarnings.addAmount(myTransaction.getCashBack());

        /* Adjust taxable earnings */
        theTotalTaxableEarnings.addAmount(myTransaction.getInterest());
        theTotalTaxableEarnings.addAmount(myTransaction.getBadDebtInterest());
        theTotalTaxableEarnings.addAmount(myTransaction.getFees());

        /* Adjust losses */
        theTotalLosses.addAmount(myTransaction.getBadDebt());
        theTotalLosses.addAmount(myTransaction.getRecovered());

        /* Adjust asset values */
        theTotalAssetValue.addAmount(myTransaction.getHolding());
        theTotalAssetValue.addAmount(myTransaction.getLoanBook());

        /* Adjust source values */
        theTotalSourceValue.addAmount(myTransaction.getInvested());
        theTotalSourceValue.addAmount(myTransaction.getInterest());
        theTotalSourceValue.addAmount(myTransaction.getCashBack());
        theTotalSourceValue.addAmount(myTransaction.getFees());
        theTotalSourceValue.addAmount(myTransaction.getBadDebt());
        theTotalSourceValue.addAmount(myTransaction.getRecovered());
    }

    @Override
    public CoeusFundingCircleMarket getMarket() {
        return (CoeusFundingCircleMarket) super.getMarket();
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
    public TethysMoney getBadDebtCapital() {
        return (TethysMoney) super.getBadDebtCapital();
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
        return theTotalTaxableEarnings;
    }

    @Override
    public TethysMoney getTotalInterest() {
        return theTotalInterest;
    }

    @Override
    public TethysMoney getTotalBadDebtInterest() {
        return theTotalBadDebtInterest;
    }

    @Override
    public TethysMoney getTotalBadDebtCapital() {
        return theTotalBadDebtCapital;
    }

    @Override
    public TethysMoney getTotalFees() {
        return theTotalFees;
    }

    @Override
    public TethysMoney getTotalCashBack() {
        return theTotalCashBack;
    }

    @Override
    public TethysMoney getTotalLosses() {
        return theTotalLosses;
    }

    @Override
    public TethysMoney getTotalBadDebt() {
        return theTotalBadDebt;
    }

    @Override
    public TethysMoney getTotalRecovered() {
        return theTotalRecovered;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }
}
