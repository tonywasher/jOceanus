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
package net.sourceforge.joceanus.jcoeus.data.ratesetter;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * RateSetter Transaction Totals.
 */
public final class CoeusRateSetterTotals
        extends CoeusTotals {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusRateSetterTotals.class, CoeusTotals.getBaseFieldSet());

    /**
     * Asset Value.
     */
    private final TethysMoney theAssetValue;

    /**
     * Holding.
     */
    private final TethysMoney theHolding;

    /**
     * LoanBook.
     */
    private final TethysMoney theLoanBook;

    /**
     * Source Value.
     */
    private final TethysMoney theSourceValue;

    /**
     * Invested.
     */
    private final TethysMoney theInvested;

    /**
     * Earnings.
     */
    private final TethysMoney theEarnings;

    /**
     * Interest.
     */
    private final TethysMoney theInterest;

    /**
     * Fees.
     */
    private final TethysMoney theFees;

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
        theAssetValue = new TethysMoney(getZero());
        theHolding = new TethysMoney(getZero());
        theLoanBook = new TethysMoney(getZero());
        theSourceValue = new TethysMoney(getZero());
        theInvested = new TethysMoney(getZero());
        theEarnings = new TethysMoney(getZero());
        theInterest = new TethysMoney(getZero());
        theFees = new TethysMoney(getZero());
    }

    /**
     * Constructor for running total.
     * @param pTotals the previous totals
     * @param pUnderlying the underlying transaction
     */
    protected CoeusRateSetterTotals(final CoeusRateSetterTotals pTotals,
                                    final CoeusRateSetterTransaction pUnderlying) {
        /* Initialise underlying class */
        super(pUnderlying, pTotals);

        /* Initialise values from previous totals */
        theAssetValue = new TethysMoney(pTotals.getAssetValue());
        theHolding = new TethysMoney(pTotals.getHolding());
        theLoanBook = new TethysMoney(pTotals.getLoanBook());
        theSourceValue = new TethysMoney(pTotals.getSourceValue());
        theInvested = new TethysMoney(pTotals.getInvested());
        theEarnings = new TethysMoney(pTotals.getEarnings());
        theInterest = new TethysMoney(pTotals.getInterest());
        theFees = new TethysMoney(pTotals.getFees());
    }

    @Override
    protected void addTotalsToTotals(final CoeusTotals pTotals) {
        /* Cast correctly */
        final CoeusRateSetterTotals myTotals = (CoeusRateSetterTotals) pTotals;

        /* Add values from totals */
        theAssetValue.addAmount(myTotals.getAssetValue());
        theHolding.addAmount(myTotals.getHolding());
        theLoanBook.addAmount(myTotals.getLoanBook());
        theSourceValue.addAmount(myTotals.getSourceValue());
        theInvested.addAmount(myTotals.getInvested());
        theEarnings.addAmount(myTotals.getEarnings());
        theInterest.addAmount(myTotals.getInterest());
        theFees.addAmount(myTotals.getFees());
    }

    @Override
    protected void addTransactionToTotals(final CoeusTransaction pTransaction) {
        /* Cast correctly */
        final CoeusRateSetterTransaction myTransaction = (CoeusRateSetterTransaction) pTransaction;

        /* Add values from transaction */
        theInvested.addAmount(myTransaction.getInvested());
        theHolding.addAmount(myTransaction.getHolding());
        theLoanBook.addAmount(myTransaction.getLoanBook());
        theInterest.addAmount(myTransaction.getInterest());
        theFees.addAmount(myTransaction.getFees());

        /* Adjust earnings */
        theEarnings.addAmount(myTransaction.getInterest());
        theEarnings.addAmount(myTransaction.getFees());

        /* Adjust asset values */
        theAssetValue.addAmount(myTransaction.getHolding());
        theAssetValue.addAmount(myTransaction.getLoanBook());

        /* Adjust source values */
        theSourceValue.addAmount(myTransaction.getInvested());
        theSourceValue.addAmount(myTransaction.getInterest());
        theSourceValue.addAmount(myTransaction.getFees());
    }

    @Override
    protected TethysMoney getZero() {
        return CoeusRateSetterTransaction.ZERO_MONEY;
    }

    @Override
    public TethysMoney getDeltaForField(final MetisDataField pField) {
        return (TethysMoney) super.getDeltaForField(pField);
    }

    @Override
    public TethysMoney getAssetValue() {
        return theAssetValue;
    }

    @Override
    public TethysMoney getHolding() {
        return theHolding;
    }

    @Override
    public TethysMoney getLoanBook() {
        return theLoanBook;
    }

    @Override
    public TethysMoney getSourceValue() {
        return theSourceValue;
    }

    @Override
    public TethysMoney getInvested() {
        return theInvested;
    }

    @Override
    public TethysMoney getEarnings() {
        return theEarnings;
    }

    @Override
    public TethysMoney getTaxableEarnings() {
        return getEarnings();
    }

    @Override
    public TethysMoney getInterest() {
        return theInterest;
    }

    @Override
    public TethysMoney getNettInterest() {
        return getInterest();
    }

    @Override
    public TethysMoney getFees() {
        return theFees;
    }

    @Override
    public TethysMoney getBadDebtInterest() {
        return getZero();
    }

    @Override
    public TethysMoney getBadDebtCapital() {
        return getZero();
    }

    @Override
    public TethysMoney getCashBack() {
        return getZero();
    }

    @Override
    public TethysMoney getLosses() {
        return getZero();
    }

    @Override
    public TethysMoney getBadDebt() {
        return getZero();
    }

    @Override
    public TethysMoney getRecovered() {
        return getZero();
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }
}
