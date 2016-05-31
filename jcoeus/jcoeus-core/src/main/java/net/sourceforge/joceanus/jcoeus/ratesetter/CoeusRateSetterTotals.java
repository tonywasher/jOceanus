/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.ratesetter;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * RateSetter Transaction Totals.
 */
public class CoeusRateSetterTotals
        extends CoeusTotals<CoeusRateSetterLoan, CoeusRateSetterTransaction, CoeusRateSetterTotals, CoeusRateSetterHistory> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusRateSetterTotals.class.getSimpleName(), CoeusTotals.getBaseFields());

    /**
     * ZERO for BadDebt/CashBack.
     */
    private static final TethysMoney ZERO_MONEY = new TethysMoney();

    /**
     * Value.
     */
    private final TethysMoney theTotalValue;

    /**
     * Invested.
     */
    private final TethysMoney theTotalInvested;

    /**
     * Holding.
     */
    private final TethysMoney theTotalHolding;

    /**
     * Capital.
     */
    private final TethysMoney theTotalCapital;

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
        theTotalValue = new TethysMoney();
        theTotalInvested = new TethysMoney();
        theTotalHolding = new TethysMoney();
        theTotalCapital = new TethysMoney();
        theTotalInterest = new TethysMoney();
        theTotalFees = new TethysMoney();
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
        theTotalValue = new TethysMoney(pTotals.getTotalValue());
        theTotalInvested = new TethysMoney(pTotals.getTotalInvested());
        theTotalHolding = new TethysMoney(pTotals.getTotalHolding());
        theTotalCapital = new TethysMoney(pTotals.getTotalCapital());
        theTotalInterest = new TethysMoney(pTotals.getTotalInterest());
        theTotalFees = new TethysMoney(pTotals.getTotalFees());

        /* Add the transaction to the totals */
        addTransactionToTotals(pUnderlying);
    }

    /**
     * Constructor for monthly totals.
     * @param pDate the date
     * @param pTotals the totals
     */
    protected CoeusRateSetterTotals(final TethysDate pDate,
                                    final CoeusRateSetterTotals pTotals) {
        /* Initialise underlying class */
        super(pTotals.getMarket(), pTotals.getLoan(), pDate);

        /* Initialise values from previous totals */
        theTotalValue = new TethysMoney(pTotals.getTotalValue());
        theTotalInvested = new TethysMoney(pTotals.getTotalInvested());
        theTotalHolding = new TethysMoney(pTotals.getTotalHolding());
        theTotalCapital = new TethysMoney(pTotals.getTotalCapital());
        theTotalInterest = new TethysMoney(pTotals.getTotalInterest());
        theTotalFees = new TethysMoney(pTotals.getTotalFees());
    }

    @Override
    protected void addTotalsToTotals(final CoeusRateSetterTotals pTotals) {
        /* Add values from totals */
        theTotalValue.addAmount(pTotals.getTotalValue());
        theTotalInvested.addAmount(pTotals.getTotalInvested());
        theTotalHolding.addAmount(pTotals.getTotalHolding());
        theTotalCapital.addAmount(pTotals.getTotalCapital());
        theTotalInterest.addAmount(pTotals.getTotalInterest());
        theTotalFees.addAmount(pTotals.getTotalFees());
    }

    @Override
    protected void addTransactionToTotals(final CoeusRateSetterTransaction pTransaction) {
        /* Add values from transaction */
        theTotalValue.addAmount(pTransaction.getValue());
        theTotalInvested.addAmount(pTransaction.getInvested());
        theTotalHolding.addAmount(pTransaction.getHolding());
        theTotalCapital.addAmount(pTransaction.getCapital());
        theTotalInterest.addAmount(pTransaction.getInterest());
        theTotalFees.addAmount(pTransaction.getFees());
    }

    @Override
    public TethysMoney getValue() {
        return (TethysMoney) super.getValue();
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
    public TethysMoney getCapital() {
        return (TethysMoney) super.getCapital();
    }

    @Override
    public TethysMoney getInterest() {
        return (TethysMoney) super.getInterest();
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
    public TethysMoney getTotalValue() {
        return theTotalValue;
    }

    @Override
    public TethysMoney getTotalInvested() {
        return theTotalInvested;
    }

    @Override
    public TethysMoney getTotalHolding() {
        return theTotalHolding;
    }

    @Override
    public TethysMoney getTotalCapital() {
        return theTotalCapital;
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
    public TethysMoney getTotalCashBack() {
        return ZERO_MONEY;
    }

    @Override
    public TethysMoney getTotalBadDebt() {
        return ZERO_MONEY;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }
}
