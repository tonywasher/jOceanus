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
package net.sourceforge.joceanus.jcoeus.zopa;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Zopa Transaction Totals.
 */
public class CoeusZopaTotals
        extends CoeusTotals<CoeusZopaLoan, CoeusZopaTransaction, CoeusZopaTotals, CoeusZopaHistory> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusZopaTotals.class.getSimpleName(), CoeusTotals.getBaseFields());

    /**
     * Value.
     */
    private final TethysDecimal theTotalValue;

    /**
     * Invested.
     */
    private final TethysDecimal theTotalInvested;

    /**
     * Holding.
     */
    private final TethysDecimal theTotalHolding;

    /**
     * Capital.
     */
    private final TethysDecimal theTotalCapital;

    /**
     * Interest.
     */
    private final TethysDecimal theTotalInterest;

    /**
     * Fees.
     */
    private final TethysDecimal theTotalFees;

    /**
     * CashBack.
     */
    private final TethysDecimal theTotalCashBack;

    /**
     * BadDebt.
     */
    private final TethysDecimal theTotalBadDebt;

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
        theTotalValue = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
        theTotalInvested = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
        theTotalHolding = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
        theTotalCapital = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
        theTotalInterest = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
        theTotalFees = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
        theTotalCashBack = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
        theTotalBadDebt = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
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
        theTotalValue = new TethysDecimal(pTotals.getTotalValue());
        theTotalInvested = new TethysDecimal(pTotals.getTotalInvested());
        theTotalHolding = new TethysDecimal(pTotals.getTotalHolding());
        theTotalCapital = new TethysDecimal(pTotals.getTotalCapital());
        theTotalInterest = new TethysDecimal(pTotals.getTotalInterest());
        theTotalFees = new TethysDecimal(pTotals.getTotalFees());
        theTotalCashBack = new TethysDecimal(pTotals.getTotalCashBack());
        theTotalBadDebt = new TethysDecimal(pTotals.getTotalBadDebt());

        /* Add the transaction to the totals */
        addTransactionToTotals(pUnderlying);
    }

    /**
     * Constructor for monthly totals.
     * @param pDate the date
     * @param pTotals the totals
     */
    protected CoeusZopaTotals(final TethysDate pDate,
                              final CoeusZopaTotals pTotals) {
        /* Initialise underlying class */
        super(pTotals.getMarket(), pTotals.getLoan(), pDate);

        /* Initialise values from previous totals */
        theTotalValue = new TethysDecimal(pTotals.getTotalValue());
        theTotalInvested = new TethysDecimal(pTotals.getTotalInvested());
        theTotalHolding = new TethysDecimal(pTotals.getTotalHolding());
        theTotalCapital = new TethysDecimal(pTotals.getTotalCapital());
        theTotalInterest = new TethysDecimal(pTotals.getTotalInterest());
        theTotalFees = new TethysDecimal(pTotals.getTotalFees());
        theTotalCashBack = new TethysDecimal(pTotals.getTotalCashBack());
        theTotalBadDebt = new TethysDecimal(pTotals.getTotalBadDebt());
    }

    @Override
    protected void addTotalsToTotals(final CoeusZopaTotals pTotals) {
        /* Add values from totals */
        theTotalValue.addValue(pTotals.getTotalValue());
        theTotalInvested.addValue(pTotals.getTotalInvested());
        theTotalHolding.addValue(pTotals.getTotalHolding());
        theTotalCapital.addValue(pTotals.getTotalCapital());
        theTotalInterest.addValue(pTotals.getTotalInterest());
        theTotalFees.addValue(pTotals.getTotalFees());
        theTotalCashBack.addValue(pTotals.getTotalCashBack());
        theTotalBadDebt.addValue(pTotals.getTotalBadDebt());
    }

    @Override
    protected void addTransactionToTotals(final CoeusZopaTransaction pTransaction) {
        /* Add values from transaction */
        theTotalValue.addValue(pTransaction.getValue());
        theTotalInvested.addValue(pTransaction.getInvested());
        theTotalHolding.addValue(pTransaction.getHolding());
        theTotalCapital.addValue(pTransaction.getCapital());
        theTotalInterest.addValue(pTransaction.getInterest());
        theTotalFees.addValue(pTransaction.getFees());
        theTotalCashBack.addValue(pTransaction.getCashBack());
        theTotalBadDebt.addValue(pTransaction.getBadDebt());
    }

    @Override
    public CoeusZopaMarket getMarket() {
        return (CoeusZopaMarket) super.getMarket();
    }

    @Override
    public TethysDecimal getTotalValue() {
        return theTotalValue;
    }

    @Override
    public TethysDecimal getTotalInvested() {
        return theTotalInvested;
    }

    @Override
    public TethysDecimal getTotalHolding() {
        return theTotalHolding;
    }

    @Override
    public TethysDecimal getTotalCapital() {
        return theTotalCapital;
    }

    @Override
    public TethysDecimal getTotalInterest() {
        return theTotalInterest;
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
    public TethysDecimal getTotalBadDebt() {
        return theTotalBadDebt;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }
}
