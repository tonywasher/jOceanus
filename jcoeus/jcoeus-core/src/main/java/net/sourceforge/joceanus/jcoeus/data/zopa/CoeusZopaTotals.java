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
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
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
     * Source Value.
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
     * Taxable Earnings.
     */
    private final TethysDecimal theTaxableEarnings;

    /**
     * Interest.
     */
    private final TethysDecimal theInterest;

    /**
     * NettInterest.
     */
    private final TethysDecimal theNettInterest;

    /**
     * BadDebtInterest.
     */
    private final TethysDecimal theBadDebtInterest;

    /**
     * BadDebtCapital.
     */
    private final TethysDecimal theBadDebtCapital;

    /**
     * Fees.
     */
    private final TethysDecimal theFees;

    /**
     * CashBack.
     */
    private final TethysDecimal theCashBack;

    /**
     * Losses.
     */
    private final TethysDecimal theLosses;

    /**
     * BadDebt.
     */
    private final TethysDecimal theBadDebt;

    /**
     * Recovered.
     */
    private final TethysDecimal theRecovered;

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
        theAssetValue = new TethysDecimal(getZero());
        theHolding = new TethysDecimal(getZero());
        theLoanBook = new TethysDecimal(getZero());
        theSourceValue = new TethysDecimal(getZero());
        theInvested = new TethysDecimal(getZero());
        theEarnings = new TethysDecimal(getZero());
        theTaxableEarnings = new TethysDecimal(getZero());
        theInterest = new TethysDecimal(getZero());
        theNettInterest = new TethysDecimal(getZero());
        theBadDebtInterest = new TethysDecimal(getZero());
        theBadDebtCapital = new TethysDecimal(getZero());
        theFees = new TethysDecimal(getZero());
        theCashBack = new TethysDecimal(getZero());
        theLosses = new TethysDecimal(getZero());
        theBadDebt = new TethysDecimal(getZero());
        theRecovered = new TethysDecimal(getZero());
    }

    /**
     * Constructor for running total.
     * @param pTotals the previous totals
     * @param pUnderlying the underlying transaction
     */
    protected CoeusZopaTotals(final CoeusZopaTotals pTotals,
                              final CoeusZopaTransaction pUnderlying) {
        /* Initialise underlying class */
        super(pUnderlying, pTotals);

        /* Initialise values from previous totals */
        theAssetValue = new TethysDecimal(pTotals.getAssetValue());
        theHolding = new TethysDecimal(pTotals.getHolding());
        theLoanBook = new TethysDecimal(pTotals.getLoanBook());
        theSourceValue = new TethysDecimal(pTotals.getSourceValue());
        theInvested = new TethysDecimal(pTotals.getInvested());
        theEarnings = new TethysDecimal(pTotals.getEarnings());
        theTaxableEarnings = new TethysDecimal(pTotals.getTaxableEarnings());
        theInterest = new TethysDecimal(pTotals.getInterest());
        theNettInterest = new TethysDecimal(pTotals.getNettInterest());
        theBadDebtInterest = new TethysDecimal(pTotals.getBadDebtInterest());
        theBadDebtCapital = new TethysDecimal(pTotals.getBadDebtCapital());
        theFees = new TethysDecimal(pTotals.getFees());
        theCashBack = new TethysDecimal(pTotals.getCashBack());
        theLosses = new TethysDecimal(pTotals.getLosses());
        theBadDebt = new TethysDecimal(pTotals.getBadDebt());
        theRecovered = new TethysDecimal(pTotals.getRecovered());
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
        theTaxableEarnings.addValue(pTotals.getTaxableEarnings());
        theInterest.addValue(pTotals.getInterest());
        theNettInterest.addValue(pTotals.getNettInterest());
        theBadDebtInterest.addValue(pTotals.getBadDebtInterest());
        theBadDebtCapital.addValue(pTotals.getBadDebtCapital());
        theFees.addValue(pTotals.getFees());
        theCashBack.addValue(pTotals.getCashBack());
        theLosses.addValue(pTotals.getLosses());
        theBadDebt.addValue(pTotals.getBadDebt());
        theRecovered.addValue(pTotals.getRecovered());
    }

    @Override
    protected void addTransactionToTotals(final CoeusTransaction pTransaction) {
        /* Add values from transaction */
        theInvested.addValue(pTransaction.getInvested());
        theHolding.addValue(pTransaction.getHolding());
        theLoanBook.addValue(pTransaction.getLoanBook());
        theInterest.addValue(pTransaction.getInterest());
        theBadDebtInterest.addValue(pTransaction.getBadDebtInterest());
        theBadDebtCapital.addValue(pTransaction.getBadDebtCapital());
        theFees.addValue(pTransaction.getFees());
        theCashBack.addValue(pTransaction.getCashBack());
        theBadDebt.addValue(pTransaction.getBadDebt());
        theRecovered.addValue(pTransaction.getRecovered());

        /* Adjust earnings */
        theEarnings.addValue(pTransaction.getInterest());
        theEarnings.addValue(pTransaction.getFees());
        theEarnings.addValue(pTransaction.getCashBack());

        /* Adjust taxable earnings */
        theTaxableEarnings.addValue(pTransaction.getInterest());
        theTaxableEarnings.addValue(pTransaction.getBadDebtInterest());
        theTaxableEarnings.addValue(pTransaction.getFees());

        /* Adjust nett interest */
        theNettInterest.addValue(pTransaction.getInterest());
        theNettInterest.addValue(pTransaction.getFees());

        /* Adjust losses */
        theLosses.addValue(pTransaction.getBadDebt());
        theLosses.addValue(pTransaction.getRecovered());

        /* Adjust asset values */
        theAssetValue.addValue(pTransaction.getHolding());
        theAssetValue.addValue(pTransaction.getLoanBook());

        /* Adjust source values */
        theSourceValue.addValue(pTransaction.getInvested());
        theSourceValue.addValue(pTransaction.getInterest());
        theSourceValue.addValue(pTransaction.getCashBack());
        theSourceValue.addValue(pTransaction.getFees());
        theSourceValue.addValue(pTransaction.getBadDebt());
        theSourceValue.addValue(pTransaction.getRecovered());
    }

    @Override
    protected TethysDecimal getZero() {
        return CoeusZopaTransaction.ZERO_MONEY;
    }

    @Override
    public CoeusZopaMarket getMarket() {
        return (CoeusZopaMarket) super.getMarket();
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
        return theTaxableEarnings;
    }

    @Override
    public TethysDecimal getInterest() {
        return theInterest;
    }

    @Override
    public TethysDecimal getNettInterest() {
        return theNettInterest;
    }

    @Override
    public TethysDecimal getBadDebtInterest() {
        return theBadDebtInterest;
    }

    @Override
    public TethysDecimal getBadDebtCapital() {
        return theBadDebtCapital;
    }

    @Override
    public TethysDecimal getFees() {
        return theFees;
    }

    @Override
    public TethysDecimal getCashBack() {
        return theCashBack;
    }

    @Override
    public TethysDecimal getLosses() {
        return theLosses;
    }

    @Override
    public TethysDecimal getBadDebt() {
        return theBadDebt;
    }

    @Override
    public TethysDecimal getRecovered() {
        return theRecovered;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }
}
