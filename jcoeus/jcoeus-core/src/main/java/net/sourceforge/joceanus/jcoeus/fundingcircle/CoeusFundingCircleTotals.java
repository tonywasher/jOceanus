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
package net.sourceforge.joceanus.jcoeus.fundingcircle;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * FundingCircle Transaction Totals.
 */
public class CoeusFundingCircleTotals
        extends CoeusTotals<CoeusFundingCircleLoan, CoeusFundingCircleTransaction, CoeusFundingCircleTotals, CoeusFundingCircleHistory> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusFundingCircleTotals.class.getSimpleName(), CoeusTotals.getBaseFields());

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
     * NettInterest.
     */
    private final TethysMoney theTotalNettInterest;

    /**
     * Interest.
     */
    private final TethysMoney theTotalInterest;

    /**
     * Fees.
     */
    private final TethysMoney theTotalFees;

    /**
     * CashBack.
     */
    private final TethysMoney theTotalCashBack;

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
        theTotalValue = new TethysMoney();
        theTotalInvested = new TethysMoney();
        theTotalHolding = new TethysMoney();
        theTotalCapital = new TethysMoney();
        theTotalNettInterest = new TethysMoney();
        theTotalInterest = new TethysMoney();
        theTotalFees = new TethysMoney();
        theTotalCashBack = new TethysMoney();
        theTotalBadDebt = new TethysMoney();
        theTotalRecovered = new TethysMoney();
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
        theTotalValue = new TethysMoney(pTotals.getTotalValue());
        theTotalInvested = new TethysMoney(pTotals.getTotalInvested());
        theTotalHolding = new TethysMoney(pTotals.getTotalHolding());
        theTotalCapital = new TethysMoney(pTotals.getTotalCapital());
        theTotalNettInterest = new TethysMoney(pTotals.getTotalInterest());
        theTotalInterest = new TethysMoney(pTotals.getTotalInterest());
        theTotalFees = new TethysMoney(pTotals.getTotalFees());
        theTotalCashBack = new TethysMoney(pTotals.getTotalCashBack());
        theTotalBadDebt = new TethysMoney(pTotals.getTotalBadDebt());
        theTotalRecovered = new TethysMoney(pTotals.getTotalRecovered());
    }

    /**
     * Constructor for monthly totals.
     * @param pDate the date
     * @param pTotals the totals
     */
    protected CoeusFundingCircleTotals(final TethysDate pDate,
                                       final CoeusFundingCircleTotals pTotals) {
        /* Initialise underlying class */
        super(pTotals.getMarket(), pTotals.getLoan(), pDate);

        /* Initialise values from previous totals */
        theTotalValue = new TethysMoney(pTotals.getTotalValue());
        theTotalInvested = new TethysMoney(pTotals.getTotalInvested());
        theTotalHolding = new TethysMoney(pTotals.getTotalHolding());
        theTotalCapital = new TethysMoney(pTotals.getTotalCapital());
        theTotalNettInterest = new TethysMoney(pTotals.getTotalNettInterest());
        theTotalInterest = new TethysMoney(pTotals.getTotalInterest());
        theTotalFees = new TethysMoney(pTotals.getTotalFees());
        theTotalCashBack = new TethysMoney(pTotals.getTotalCashBack());
        theTotalBadDebt = new TethysMoney(pTotals.getTotalBadDebt());
        theTotalRecovered = new TethysMoney(pTotals.getTotalRecovered());
    }

    @Override
    protected void addTotalsToTotals(final CoeusFundingCircleTotals pTotals) {
        /* Add values from totals */
        theTotalValue.addAmount(pTotals.getTotalValue());
        theTotalInvested.addAmount(pTotals.getTotalInvested());
        theTotalHolding.addAmount(pTotals.getTotalHolding());
        theTotalCapital.addAmount(pTotals.getTotalCapital());
        theTotalNettInterest.addAmount(pTotals.getTotalNettInterest());
        theTotalInterest.addAmount(pTotals.getTotalInterest());
        theTotalFees.addAmount(pTotals.getTotalFees());
        theTotalCashBack.addAmount(pTotals.getTotalCashBack());
        theTotalBadDebt.addAmount(pTotals.getTotalBadDebt());
        theTotalRecovered.addAmount(pTotals.getTotalRecovered());
    }

    @Override
    protected void addTransactionToTotals(final CoeusFundingCircleTransaction pTransaction) {
        /* Add values from transaction */
        theTotalValue.addAmount(pTransaction.getValue());
        theTotalInvested.addAmount(pTransaction.getInvested());
        theTotalHolding.addAmount(pTransaction.getHolding());
        theTotalCapital.addAmount(pTransaction.getCapital());
        theTotalNettInterest.addAmount(pTransaction.getNettInterest());
        theTotalInterest.addAmount(pTransaction.getInterest());
        theTotalFees.addAmount(pTransaction.getFees());
        theTotalCashBack.addAmount(pTransaction.getCashBack());
        theTotalBadDebt.addAmount(pTransaction.getBadDebt());
        theTotalRecovered.addAmount(pTransaction.getRecovered());
    }

    @Override
    public CoeusFundingCircleMarket getMarket() {
        return (CoeusFundingCircleMarket) super.getMarket();
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
    public TethysMoney getNettInterest() {
        return (TethysMoney) super.getNettInterest();
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
    public TethysMoney getRecovered() {
        return (TethysMoney) super.getRecovered();
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
    public TethysMoney getTotalNettInterest() {
        return theTotalNettInterest;
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
        return theTotalCashBack;
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
