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
package net.sourceforge.joceanus.jcoeus.data.fundingcircle;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * FundingCircle Transaction Totals.
 */
public final class CoeusFundingCircleTotals
        extends CoeusTotals {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusFundingCircleTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusFundingCircleTotals.class);

    /**
     * AssetValue.
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
     * SourceValue.
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
     * taxableEarnings.
     */
    private final TethysMoney theTaxableEarnings;

    /**
     * Interest.
     */
    private final TethysMoney theInterest;

    /**
     * NettInterest.
     */
    private final TethysMoney theNettInterest;

    /**
     * BadDebtInterest.
     */
    private final TethysMoney theBadDebtInterest;

    /**
     * BadDebtCapital.
     */
    private final TethysMoney theBadDebtCapital;

    /**
     * Fees.
     */
    private final TethysMoney theFees;

    /**
     * CashBack.
     */
    private final TethysMoney theCashBack;

    /**
     * Losses.
     */
    private final TethysMoney theLosses;

    /**
     * BadDebt.
     */
    private final TethysMoney theBadDebt;

    /**
     * Recovered.
     */
    private final TethysMoney theRecovered;

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
        theAssetValue = new TethysMoney(getZero());
        theHolding = new TethysMoney(getZero());
        theLoanBook = new TethysMoney(getZero());
        theSourceValue = new TethysMoney(getZero());
        theInvested = new TethysMoney(getZero());
        theEarnings = new TethysMoney(getZero());
        theTaxableEarnings = new TethysMoney(getZero());
        theInterest = new TethysMoney(getZero());
        theNettInterest = new TethysMoney(getZero());
        theBadDebtInterest = new TethysMoney(getZero());
        theBadDebtCapital = new TethysMoney(getZero());
        theFees = new TethysMoney(getZero());
        theCashBack = new TethysMoney(getZero());
        theLosses = new TethysMoney(getZero());
        theBadDebt = new TethysMoney(getZero());
        theRecovered = new TethysMoney(getZero());
    }

    /**
     * Constructor for running total.
     * @param pTotals the previous totals
     * @param pUnderlying the underlying transaction
     */
    protected CoeusFundingCircleTotals(final CoeusFundingCircleTotals pTotals,
                                       final CoeusFundingCircleTransaction pUnderlying) {
        /* Initialise underlying class */
        super(pUnderlying, pTotals);

        /* Initialise values from previous totals */
        theAssetValue = new TethysMoney(pTotals.getAssetValue());
        theHolding = new TethysMoney(pTotals.getHolding());
        theLoanBook = new TethysMoney(pTotals.getLoanBook());
        theSourceValue = new TethysMoney(pTotals.getSourceValue());
        theInvested = new TethysMoney(pTotals.getInvested());
        theEarnings = new TethysMoney(pTotals.getEarnings());
        theTaxableEarnings = new TethysMoney(pTotals.getTaxableEarnings());
        theInterest = new TethysMoney(pTotals.getInterest());
        theNettInterest = new TethysMoney(pTotals.getNettInterest());
        theBadDebtInterest = new TethysMoney(pTotals.getBadDebtInterest());
        theBadDebtCapital = new TethysMoney(pTotals.getBadDebtCapital());
        theFees = new TethysMoney(pTotals.getFees());
        theCashBack = new TethysMoney(pTotals.getCashBack());
        theLosses = new TethysMoney(pTotals.getLosses());
        theBadDebt = new TethysMoney(pTotals.getBadDebt());
        theRecovered = new TethysMoney(pTotals.getRecovered());
    }

    @Override
    protected void addTotalsToTotals(final CoeusTotals pTotals) {
        /* Cast correctly */
        final CoeusFundingCircleTotals myTotals = (CoeusFundingCircleTotals) pTotals;

        /* Add values from totals */
        theAssetValue.addAmount(myTotals.getAssetValue());
        theHolding.addAmount(myTotals.getHolding());
        theLoanBook.addAmount(myTotals.getLoanBook());
        theSourceValue.addAmount(myTotals.getSourceValue());
        theInvested.addAmount(myTotals.getInvested());
        theEarnings.addAmount(myTotals.getEarnings());
        theTaxableEarnings.addAmount(myTotals.getTaxableEarnings());
        theInterest.addAmount(myTotals.getInterest());
        theNettInterest.addAmount(myTotals.getNettInterest());
        theBadDebtInterest.addAmount(myTotals.getBadDebtInterest());
        theBadDebtCapital.addAmount(myTotals.getBadDebtCapital());
        theFees.addAmount(myTotals.getFees());
        theCashBack.addAmount(myTotals.getCashBack());
        theLosses.addAmount(myTotals.getLosses());
        theBadDebt.addAmount(myTotals.getBadDebt());
        theRecovered.addAmount(myTotals.getRecovered());
    }

    @Override
    protected void addTransactionToTotals(final CoeusTransaction pTransaction) {
        /* Cast correctly */
        final CoeusFundingCircleTransaction myTransaction = (CoeusFundingCircleTransaction) pTransaction;

        /* Add values from transaction */
        theInvested.addAmount(myTransaction.getInvested());
        theHolding.addAmount(myTransaction.getHolding());
        theLoanBook.addAmount(myTransaction.getLoanBook());
        theInterest.addAmount(myTransaction.getInterest());
        theBadDebtInterest.addAmount(myTransaction.getBadDebtInterest());
        theBadDebtCapital.addAmount(myTransaction.getBadDebtCapital());
        theFees.addAmount(myTransaction.getFees());
        theCashBack.addAmount(myTransaction.getCashBack());
        theBadDebt.addAmount(myTransaction.getBadDebt());
        theRecovered.addAmount(myTransaction.getRecovered());

        /* Adjust earnings */
        theEarnings.addAmount(myTransaction.getInterest());
        theEarnings.addAmount(myTransaction.getFees());
        theEarnings.addAmount(myTransaction.getCashBack());

        /* Adjust taxable earnings */
        theTaxableEarnings.addAmount(myTransaction.getInterest());
        theTaxableEarnings.addAmount(myTransaction.getBadDebtInterest());
        theTaxableEarnings.addAmount(myTransaction.getFees());

        /* Adjust nettInterest */
        theNettInterest.addAmount(myTransaction.getInterest());
        theNettInterest.addAmount(myTransaction.getFees());

        /* Adjust losses */
        theLosses.addAmount(myTransaction.getBadDebt());
        theLosses.addAmount(myTransaction.getRecovered());

        /* Adjust asset values */
        theAssetValue.addAmount(myTransaction.getHolding());
        theAssetValue.addAmount(myTransaction.getLoanBook());

        /* Adjust source values */
        theSourceValue.addAmount(myTransaction.getInvested());
        theSourceValue.addAmount(myTransaction.getInterest());
        theSourceValue.addAmount(myTransaction.getCashBack());
        theSourceValue.addAmount(myTransaction.getFees());
        theSourceValue.addAmount(myTransaction.getBadDebt());
        theSourceValue.addAmount(myTransaction.getRecovered());
    }

    @Override
    protected TethysMoney getZero() {
        return CoeusFundingCircleTransaction.ZERO_MONEY;
    }

    @Override
    public CoeusFundingCircleMarket getMarket() {
        return (CoeusFundingCircleMarket) super.getMarket();
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
        return theTaxableEarnings;
    }

    @Override
    public TethysMoney getInterest() {
        return theInterest;
    }

    @Override
    public TethysMoney getNettInterest() {
        return theNettInterest;
    }

    @Override
    public TethysMoney getBadDebtInterest() {
        return theBadDebtInterest;
    }

    @Override
    public TethysMoney getBadDebtCapital() {
        return theBadDebtCapital;
    }

    @Override
    public TethysMoney getFees() {
        return theFees;
    }

    @Override
    public TethysMoney getCashBack() {
        return theCashBack;
    }

    @Override
    public TethysMoney getLosses() {
        return theLosses;
    }

    @Override
    public TethysMoney getBadDebt() {
        return theBadDebt;
    }

    @Override
    public TethysMoney getRecovered() {
        return theRecovered;
    }

    @Override
    public MetisFieldSet<CoeusFundingCircleTotals> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
