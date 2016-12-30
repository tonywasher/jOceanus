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
package net.sourceforge.joceanus.jcoeus.data.zopa;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Zopa Transaction Totals.
 */
public class CoeusZopaTotals
        extends CoeusTotals {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusZopaTotals.class.getSimpleName(), CoeusTotals.getBaseFields());

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
     * Source Value.
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
     * Taxable Earnings.
     */
    private final TethysDecimal theTotalTaxableEarnings;

    /**
     * Interest.
     */
    private final TethysDecimal theTotalInterest;

    /**
     * BadDebtInterest.
     */
    private final TethysDecimal theTotalBadDebtInterest;

    /**
     * BadDebtCapital.
     */
    private final TethysDecimal theTotalBadDebtCapital;

    /**
     * Fees.
     */
    private final TethysDecimal theTotalFees;

    /**
     * CashBack.
     */
    private final TethysDecimal theTotalCashBack;

    /**
     * Losses.
     */
    private final TethysDecimal theTotalLosses;

    /**
     * BadDebt.
     */
    private final TethysDecimal theTotalBadDebt;

    /**
     * Recovered.
     */
    private final TethysDecimal theTotalRecovered;

    /**
     * Constructor for zeroed totals.
     * @param pMarket the market
     */
    protected CoeusZopaTotals(final CoeusZopaMarket pMarket) {
        this(pMarket, null, null);
    }

    /**
     * Constructor for zeroed period totals.
     * @param pMarket the market
     * @param pDate the end date for the totals
     */
    protected CoeusZopaTotals(final CoeusZopaMarket pMarket,
                              final TethysDate pDate) {
        this(pMarket, null, pDate);
    }

    /**
     * Constructor for zeroed totals.
     * @param pLoan the loan
     */
    protected CoeusZopaTotals(final CoeusZopaLoan pLoan) {
        this(pLoan.getMarket(), pLoan, null);
    }

    /**
     * Constructor for zeroed totals.
     * @param pLoan the loan
     * @param pDate the end date for the totals
     */
    protected CoeusZopaTotals(final CoeusZopaLoan pLoan,
                              final TethysDate pDate) {
        this(pLoan.getMarket(), pLoan, pDate);
    }

    /**
     * Constructor for zeroed totals.
     * @param pMarket the market
     * @param pLoan the loan
     * @param pDate the end date for the totals
     */
    protected CoeusZopaTotals(final CoeusZopaMarket pMarket,
                              final CoeusZopaLoan pLoan,
                              final TethysDate pDate) {
        /* Initialise underlying class */
        super(pMarket, pLoan, pDate);

        /* Initialise values */
        theTotalAssetValue = new TethysDecimal(CoeusZopaTransaction.ZERO_MONEY);
        theTotalHolding = new TethysDecimal(CoeusZopaTransaction.ZERO_MONEY);
        theTotalLoanBook = new TethysDecimal(CoeusZopaTransaction.ZERO_MONEY);
        theTotalSourceValue = new TethysDecimal(CoeusZopaTransaction.ZERO_MONEY);
        theTotalInvested = new TethysDecimal(CoeusZopaTransaction.ZERO_MONEY);
        theTotalEarnings = new TethysDecimal(CoeusZopaTransaction.ZERO_MONEY);
        theTotalTaxableEarnings = new TethysDecimal(CoeusZopaTransaction.ZERO_MONEY);
        theTotalInterest = new TethysDecimal(CoeusZopaTransaction.ZERO_MONEY);
        theTotalBadDebtInterest = new TethysDecimal(CoeusZopaTransaction.ZERO_MONEY);
        theTotalBadDebtCapital = new TethysDecimal(CoeusZopaTransaction.ZERO_MONEY);
        theTotalFees = new TethysDecimal(CoeusZopaTransaction.ZERO_MONEY);
        theTotalCashBack = new TethysDecimal(CoeusZopaTransaction.ZERO_MONEY);
        theTotalLosses = new TethysDecimal(CoeusZopaTransaction.ZERO_MONEY);
        theTotalBadDebt = new TethysDecimal(CoeusZopaTransaction.ZERO_MONEY);
        theTotalRecovered = new TethysDecimal(CoeusZopaTransaction.ZERO_MONEY);
    }

    /**
     * Constructor for running total.
     * @param pTotals the previous totals
     * @param pUnderlying the underlying transaction
     */
    protected CoeusZopaTotals(final CoeusZopaTotals pTotals,
                              final CoeusZopaTransaction pUnderlying) {
        /* Initialise underlying class */
        super(pUnderlying);

        /* Initialise values from previous totals */
        theTotalAssetValue = new TethysDecimal(pTotals.getTotalAssetValue());
        theTotalHolding = new TethysDecimal(pTotals.getTotalHolding());
        theTotalLoanBook = new TethysDecimal(pTotals.getTotalLoanBook());
        theTotalSourceValue = new TethysDecimal(pTotals.getTotalSourceValue());
        theTotalInvested = new TethysDecimal(pTotals.getTotalInvested());
        theTotalEarnings = new TethysDecimal(pTotals.getTotalEarnings());
        theTotalTaxableEarnings = new TethysDecimal(pTotals.getTotalTaxableEarnings());
        theTotalInterest = new TethysDecimal(pTotals.getTotalInterest());
        theTotalBadDebtInterest = new TethysDecimal(pTotals.getTotalBadDebtInterest());
        theTotalBadDebtCapital = new TethysDecimal(pTotals.getTotalBadDebtCapital());
        theTotalFees = new TethysDecimal(pTotals.getTotalFees());
        theTotalCashBack = new TethysDecimal(pTotals.getTotalCashBack());
        theTotalLosses = new TethysDecimal(pTotals.getTotalLosses());
        theTotalBadDebt = new TethysDecimal(pTotals.getTotalBadDebt());
        theTotalRecovered = new TethysDecimal(pTotals.getTotalRecovered());
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
        theTotalTaxableEarnings.addValue(pTotals.getTotalTaxableEarnings());
        theTotalInterest.addValue(pTotals.getTotalInterest());
        theTotalBadDebtInterest.addValue(pTotals.getTotalBadDebtInterest());
        theTotalBadDebtCapital.addValue(pTotals.getTotalBadDebtCapital());
        theTotalFees.addValue(pTotals.getTotalFees());
        theTotalCashBack.addValue(pTotals.getTotalCashBack());
        theTotalLosses.addValue(pTotals.getTotalLosses());
        theTotalBadDebt.addValue(pTotals.getTotalBadDebt());
        theTotalRecovered.addValue(pTotals.getTotalRecovered());
    }

    @Override
    protected void addTransactionToTotals(final CoeusTransaction pTransaction) {
        /* Add values from transaction */
        theTotalInvested.addValue(pTransaction.getInvested());
        theTotalHolding.addValue(pTransaction.getHolding());
        theTotalLoanBook.addValue(pTransaction.getLoanBook());
        theTotalInterest.addValue(pTransaction.getInterest());
        theTotalBadDebtInterest.addValue(pTransaction.getBadDebtInterest());
        theTotalBadDebtCapital.addValue(pTransaction.getBadDebtCapital());
        theTotalFees.addValue(pTransaction.getFees());
        theTotalCashBack.addValue(pTransaction.getCashBack());
        theTotalBadDebt.addValue(pTransaction.getBadDebt());
        theTotalRecovered.addValue(pTransaction.getRecovered());

        /* Adjust earnings */
        theTotalEarnings.addValue(pTransaction.getInterest());
        theTotalEarnings.addValue(pTransaction.getFees());
        theTotalEarnings.addValue(pTransaction.getCashBack());

        /* Adjust taxable earnings */
        theTotalTaxableEarnings.addValue(pTransaction.getInterest());
        theTotalTaxableEarnings.addValue(pTransaction.getBadDebtInterest());
        theTotalTaxableEarnings.addValue(pTransaction.getFees());

        /* Adjust losses */
        theTotalLosses.addValue(pTransaction.getBadDebt());
        theTotalLosses.addValue(pTransaction.getRecovered());

        /* Adjust asset values */
        theTotalAssetValue.addValue(pTransaction.getHolding());
        theTotalAssetValue.addValue(pTransaction.getLoanBook());

        /* Adjust source values */
        theTotalSourceValue.addValue(pTransaction.getInvested());
        theTotalSourceValue.addValue(pTransaction.getInterest());
        theTotalSourceValue.addValue(pTransaction.getCashBack());
        theTotalSourceValue.addValue(pTransaction.getFees());
        theTotalSourceValue.addValue(pTransaction.getBadDebt());
        theTotalSourceValue.addValue(pTransaction.getRecovered());
    }

    @Override
    public CoeusZopaMarket getMarket() {
        return (CoeusZopaMarket) super.getMarket();
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
        return theTotalTaxableEarnings;
    }

    @Override
    public TethysDecimal getTotalInterest() {
        return theTotalInterest;
    }

    @Override
    public TethysDecimal getTotalBadDebtInterest() {
        return theTotalBadDebtInterest;
    }

    @Override
    public TethysDecimal getTotalBadDebtCapital() {
        return theTotalBadDebtCapital;
    }

    @Override
    public TethysDecimal getTotalFees() {
        return theTotalFees;
    }

    @Override
    public TethysDecimal getTotalCashBack() {
        return theTotalCashBack;
    }

    @Override
    public TethysDecimal getTotalLosses() {
        return theTotalLosses;
    }

    @Override
    public TethysDecimal getTotalBadDebt() {
        return theTotalBadDebt;
    }

    @Override
    public TethysDecimal getTotalRecovered() {
        return theTotalRecovered;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }
}
