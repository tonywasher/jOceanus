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
package net.sourceforge.joceanus.jcoeus.lendingworks;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * LendingWorks Transaction Totals.
 */
public class CoeusLendingWorksTotals
        extends CoeusTotals<CoeusLendingWorksLoan, CoeusLendingWorksTransaction, CoeusLendingWorksTotals, CoeusLendingWorksHistory> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusLendingWorksTotals.class.getSimpleName(), CoeusTotals.getBaseFields());

    /**
     * ZERO for BadDebt/CashBack.
     */
    private static final TethysMoney ZERO_MONEY = new TethysMoney();

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
     * NettInterest.
     */
    private final TethysDecimal theTotalNettInterest;

    /**
     * Interest.
     */
    private final TethysDecimal theTotalInterest;

    /**
     * Fees.
     */
    private final TethysDecimal theTotalFees;

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
        theTotalValue = new TethysDecimal(0, CoeusLendingWorksMarket.DECIMAL_SIZE);
        theTotalInvested = new TethysDecimal(0, CoeusLendingWorksMarket.DECIMAL_SIZE);
        theTotalHolding = new TethysDecimal(0, CoeusLendingWorksMarket.DECIMAL_SIZE);
        theTotalCapital = new TethysDecimal(0, CoeusLendingWorksMarket.DECIMAL_SIZE);
        theTotalNettInterest = new TethysDecimal(0, CoeusLendingWorksMarket.DECIMAL_SIZE);
        theTotalInterest = new TethysDecimal(0, CoeusLendingWorksMarket.DECIMAL_SIZE);
        theTotalFees = new TethysDecimal(0, CoeusLendingWorksMarket.DECIMAL_SIZE);
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
        theTotalValue = new TethysDecimal(pTotals.getTotalValue());
        theTotalInvested = new TethysDecimal(pTotals.getTotalInvested());
        theTotalHolding = new TethysDecimal(pTotals.getTotalHolding());
        theTotalCapital = new TethysDecimal(pTotals.getTotalCapital());
        theTotalNettInterest = new TethysDecimal(pTotals.getTotalNettInterest());
        theTotalInterest = new TethysDecimal(pTotals.getTotalInterest());
        theTotalFees = new TethysDecimal(pTotals.getTotalFees());
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
        theTotalValue = new TethysDecimal(pTotals.getTotalValue());
        theTotalInvested = new TethysDecimal(pTotals.getTotalInvested());
        theTotalHolding = new TethysDecimal(pTotals.getTotalHolding());
        theTotalCapital = new TethysDecimal(pTotals.getTotalCapital());
        theTotalNettInterest = new TethysDecimal(pTotals.getTotalNettInterest());
        theTotalInterest = new TethysDecimal(pTotals.getTotalInterest());
        theTotalFees = new TethysDecimal(pTotals.getTotalFees());
    }

    @Override
    protected void addTotalsToTotals(final CoeusLendingWorksTotals pTotals) {
        /* Add values from totals */
        theTotalValue.addValue(pTotals.getTotalValue());
        theTotalInvested.addValue(pTotals.getTotalInvested());
        theTotalHolding.addValue(pTotals.getTotalHolding());
        theTotalCapital.addValue(pTotals.getTotalCapital());
        theTotalNettInterest.addValue(pTotals.getTotalNettInterest());
        theTotalInterest.addValue(pTotals.getTotalInterest());
        theTotalFees.addValue(pTotals.getTotalFees());
    }

    @Override
    protected void addTransactionToTotals(final CoeusLendingWorksTransaction pTransaction) {
        /* Add values from transaction */
        theTotalValue.addValue(pTransaction.getValue());
        theTotalInvested.addValue(pTransaction.getInvested());
        theTotalHolding.addValue(pTransaction.getHolding());
        theTotalCapital.addValue(pTransaction.getCapital());
        theTotalNettInterest.addValue(pTransaction.getNettInterest());
        theTotalInterest.addValue(pTransaction.getInterest());
        theTotalFees.addValue(pTransaction.getFees());
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
    public TethysDecimal getTotalNettInterest() {
        return theTotalNettInterest;
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
        return ZERO_MONEY;
    }

    @Override
    public TethysDecimal getTotalBadDebt() {
        return ZERO_MONEY;
    }

    @Override
    public TethysDecimal getTotalRecovered() {
        return ZERO_MONEY;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }
}
