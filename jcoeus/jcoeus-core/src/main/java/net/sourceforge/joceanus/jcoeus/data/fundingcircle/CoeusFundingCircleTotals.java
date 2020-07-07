/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2020 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jcoeus.data.fundingcircle;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
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
     * XferPayment.
     */
    private final TethysMoney theXferPayment;

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
    CoeusFundingCircleTotals(final CoeusFundingCircleMarket pMarket) {
        this(pMarket, null);
    }

    /**
     * Constructor for zeroed totals.
     * @param pLoan the loan
     */
    CoeusFundingCircleTotals(final CoeusFundingCircleLoan pLoan) {
        this(pLoan.getMarket(), pLoan);
    }

    /**
     * Constructor for zeroed totals.
     * @param pMarket the market
     * @param pLoan the loan
     */
    private CoeusFundingCircleTotals(final CoeusFundingCircleMarket pMarket,
                                     final CoeusFundingCircleLoan pLoan) {
        /* Initialise underlying class */
        super(pMarket, pLoan);

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
        theXferPayment = new TethysMoney(getZero());
        theLosses = new TethysMoney(getZero());
        theBadDebt = new TethysMoney(getZero());
        theRecovered = new TethysMoney(getZero());
    }

    /**
     * Constructor for running total.
     * @param pTotals the previous totals
     * @param pUnderlying the underlying transaction
     */
    CoeusFundingCircleTotals(final CoeusFundingCircleTotals pTotals,
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
        theXferPayment = new TethysMoney(pTotals.getXferPayment());
        theLosses = new TethysMoney(pTotals.getLosses());
        theBadDebt = new TethysMoney(pTotals.getBadDebt());
        theRecovered = new TethysMoney(pTotals.getRecovered());
    }

    @Override
    protected void calculateDelta(final CoeusTotals pBase) {
        /* Calculate delta rateOfReturns */
        super.calculateDelta(pBase);

        /* Cast correctly */
        final CoeusFundingCircleTotals myBase = (CoeusFundingCircleTotals) pBase;

        /* Calculate the deltas */
        theAssetValue.subtractAmount(myBase.getAssetValue());
        theHolding.subtractAmount(myBase.getHolding());
        theLoanBook.subtractAmount(myBase.getLoanBook());
        theSourceValue.subtractAmount(myBase.getSourceValue());
        theInvested.subtractAmount(myBase.getInvested());
        theEarnings.subtractAmount(myBase.getEarnings());
        theTaxableEarnings.subtractAmount(myBase.getTaxableEarnings());
        theInterest.subtractAmount(myBase.getInterest());
        theNettInterest.subtractAmount(myBase.getNettInterest());
        theBadDebtInterest.subtractAmount(myBase.getBadDebtInterest());
        theBadDebtCapital.subtractAmount(myBase.getBadDebtCapital());
        theFees.subtractAmount(myBase.getFees());
        theCashBack.subtractAmount(myBase.getCashBack());
        theXferPayment.subtractAmount(myBase.getXferPayment());
        theLosses.subtractAmount(myBase.getLosses());
        theBadDebt.subtractAmount(myBase.getBadDebt());
        theRecovered.subtractAmount(myBase.getRecovered());
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
        theXferPayment.addAmount(myTransaction.getXferPayment());
        theBadDebt.addAmount(myTransaction.getBadDebt());
        theRecovered.addAmount(myTransaction.getRecovered());

        /* Adjust earnings */
        final TethysMoney myIncome = new TethysMoney(myTransaction.getInterest());
        myIncome.addAmount(myTransaction.getFees());
        myIncome.addAmount(myTransaction.getCashBack());
        myIncome.addAmount(myTransaction.getXferPayment());
        theEarnings.addAmount(myIncome);

        /* Adjust taxable earnings */
        theTaxableEarnings.addAmount(myTransaction.getInterest());
        theTaxableEarnings.addAmount(myTransaction.getBadDebtInterest());
        theTaxableEarnings.addAmount(myTransaction.getFees());

        /* Adjust nettInterest */
        theNettInterest.addAmount(myTransaction.getInterest());
        theNettInterest.addAmount(myTransaction.getFees());

        /* Adjust losses */
        final TethysMoney myLosses = new TethysMoney(myTransaction.getBadDebt());
        myLosses.addAmount(myTransaction.getRecovered());
        theLosses.addAmount(myLosses);
        myIncome.addAmount(myLosses);

        /* Adjust asset values */
        theAssetValue.addAmount(myTransaction.getHolding());
        theAssetValue.addAmount(myTransaction.getLoanBook());

        /* Adjust source values */
        theSourceValue.addAmount(myTransaction.getInvested());
        theSourceValue.addAmount(myTransaction.getInterest());
        theSourceValue.addAmount(myTransaction.getCashBack());
        theSourceValue.addAmount(myTransaction.getXferPayment());
        theSourceValue.addAmount(myTransaction.getFees());
        theSourceValue.addAmount(myTransaction.getBadDebt());
        theSourceValue.addAmount(myTransaction.getRecovered());

        /* Calculate the RateOfReturn */
        calculateRateOfReturn(myIncome);
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
    public TethysMoney getXferPayment() {
        return theXferPayment;
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
