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
package net.sourceforge.joceanus.jcoeus.data.lendingworks;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * LendingWorks Transaction Totals.
 */
public final class CoeusLendingWorksTotals
        extends CoeusTotals {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusLendingWorksTotals.class, CoeusTotals.getBaseFieldSet());

    /**
     * AssetValue.
     */
    private final TethysDecimal theAssetValue;

    /**
     * Holding.
     */
    private final TethysDecimal theHolding;

    /**
     * LoanBook.
     */
    private final TethysDecimal theLoanBook;

    /**
     * SourceValue.
     */
    private final TethysDecimal theSourceValue;

    /**
     * Invested.
     */
    private final TethysDecimal theInvested;

    /**
     * Earnings.
     */
    private final TethysDecimal theEarnings;

    /**
     * Interest.
     */
    private final TethysDecimal theInterest;

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
        theAssetValue = new TethysDecimal(getZero());
        theHolding = new TethysDecimal(getZero());
        theLoanBook = new TethysDecimal(getZero());
        theSourceValue = new TethysDecimal(getZero());
        theInvested = new TethysDecimal(getZero());
        theEarnings = new TethysDecimal(getZero());
        theInterest = new TethysDecimal(getZero());
    }

    /**
     * Constructor for running total.
     * @param pTotals the previous totals
     * @param pUnderlying the underlying transaction
     */
    protected CoeusLendingWorksTotals(final CoeusLendingWorksTotals pTotals,
                                      final CoeusLendingWorksTransaction pUnderlying) {
        /* Initialise underlying class */
        super(pUnderlying, pTotals);

        /* Initialise values from previous totals */
        theAssetValue = new TethysDecimal(pTotals.getAssetValue());
        theHolding = new TethysDecimal(pTotals.getHolding());
        theLoanBook = new TethysDecimal(pTotals.getLoanBook());
        theSourceValue = new TethysDecimal(pTotals.getSourceValue());
        theInvested = new TethysDecimal(pTotals.getInvested());
        theEarnings = new TethysDecimal(pTotals.getEarnings());
        theInterest = new TethysDecimal(pTotals.getInterest());
    }

    @Override
    protected void addTotalsToTotals(final CoeusTotals pTotals) {
        /* Add values from totals */
        theAssetValue.addValue(pTotals.getAssetValue());
        theHolding.addValue(pTotals.getHolding());
        theLoanBook.addValue(pTotals.getLoanBook());
        theSourceValue.addValue(pTotals.getSourceValue());
        theInvested.addValue(pTotals.getInvested());
        theEarnings.addValue(pTotals.getEarnings());
        theInterest.addValue(pTotals.getInterest());
    }

    @Override
    protected void addTransactionToTotals(final CoeusTransaction pTransaction) {
        /* Add values from transaction */
        theInvested.addValue(pTransaction.getInvested());
        theHolding.addValue(pTransaction.getHolding());
        theLoanBook.addValue(pTransaction.getLoanBook());
        theInterest.addValue(pTransaction.getInterest());

        /* Adjust earnings */
        theEarnings.addValue(pTransaction.getInterest());
        theEarnings.subtractValue(pTransaction.getFees());

        /* Adjust asset values */
        theAssetValue.addValue(pTransaction.getHolding());
        theAssetValue.addValue(pTransaction.getLoanBook());

        /* Adjust source values */
        theSourceValue.addValue(pTransaction.getInvested());
        theSourceValue.addValue(pTransaction.getInterest());
    }

    @Override
    protected TethysDecimal getZero() {
        return CoeusLendingWorksTransaction.ZERO_MONEY;
    }

    @Override
    public TethysDecimal getAssetValue() {
        return theAssetValue;
    }

    @Override
    public TethysDecimal getHolding() {
        return theHolding;
    }

    @Override
    public TethysDecimal getLoanBook() {
        return theLoanBook;
    }

    @Override
    public TethysDecimal getSourceValue() {
        return theSourceValue;
    }

    @Override
    public TethysDecimal getInvested() {
        return theInvested;
    }

    @Override
    public TethysDecimal getEarnings() {
        return theEarnings;
    }

    @Override
    public TethysDecimal getTaxableEarnings() {
        return theInterest;
    }

    @Override
    public TethysDecimal getInterest() {
        return theInterest;
    }

    @Override
    public TethysDecimal getNettInterest() {
        return theEarnings;
    }

    @Override
    public TethysDecimal getFees() {
        return getZero();
    }

    @Override
    public TethysDecimal getBadDebtInterest() {
        return getZero();
    }

    @Override
    public TethysDecimal getBadDebtCapital() {
        return getZero();
    }

    @Override
    public TethysDecimal getCashBack() {
        return getZero();
    }

    @Override
    public TethysDecimal getLosses() {
        return getZero();
    }

    @Override
    public TethysDecimal getBadDebt() {
        return getZero();
    }

    @Override
    public TethysDecimal getRecovered() {
        return getZero();
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }
}
