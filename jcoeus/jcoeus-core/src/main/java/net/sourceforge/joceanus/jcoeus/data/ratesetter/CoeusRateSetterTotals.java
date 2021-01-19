/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data.ratesetter;

import java.util.Objects;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * RateSetter Transaction Totals.
 */
public final class CoeusRateSetterTotals
        extends CoeusTotals {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusRateSetterTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusRateSetterTotals.class);

    /**
     * Asset Value.
     */
    private TethysMoney theAssetValue;

    /**
     * Holding.
     */
    private TethysMoney theHolding;

    /**
     * LoanBook.
     */
    private TethysMoney theLoanBook;

    /**
     * Source Value.
     */
    private TethysMoney theSourceValue;

    /**
     * Invested.
     */
    private TethysMoney theInvested;

    /**
     * Earnings.
     */
    private TethysMoney theEarnings;

    /**
     * Interest.
     */
    private TethysMoney theInterest;

    /**
     * Fees.
     */
    private TethysMoney theFees;

    /**
     * Constructor for zeroed totals.
     * @param pMarket the market
     */
    CoeusRateSetterTotals(final CoeusRateSetterMarket pMarket) {
        this(pMarket, null);
    }


    /**
     * Constructor for zeroed totals.
     * @param pLoan the loan
     */
    CoeusRateSetterTotals(final CoeusRateSetterLoan pLoan) {
        this(pLoan.getMarket(), pLoan);
    }

    /**
     * Constructor for zeroed totals.
     * @param pMarket the market
     * @param pLoan the loan
     */
    CoeusRateSetterTotals(final CoeusRateSetterMarket pMarket,
                          final CoeusRateSetterLoan pLoan) {
        /* Initialise underlying class */
        super(pMarket, pLoan);

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
    CoeusRateSetterTotals(final CoeusRateSetterTotals pTotals,
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
    protected void calculateDelta(final CoeusTotals pBase) {
        /* Calculate delta rateOfReturns */
        super.calculateDelta(pBase);

        /* Cast correctly */
        final CoeusRateSetterTotals myBase = (CoeusRateSetterTotals) pBase;

        /* Calculate the deltas */
        theAssetValue.subtractAmount(myBase.getAssetValue());
        theHolding.subtractAmount(myBase.getHolding());
        theLoanBook.subtractAmount(myBase.getLoanBook());
        theSourceValue.subtractAmount(myBase.getSourceValue());
        theInvested.subtractAmount(myBase.getInvested());
        theEarnings.subtractAmount(myBase.getEarnings());
        theInterest.subtractAmount(myBase.getInterest());
        theFees.subtractAmount(myBase.getFees());
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
        final TethysMoney myIncome = new TethysMoney(myTransaction.getInterest());
        myIncome.addAmount(myTransaction.getFees());
        theEarnings.addAmount(myIncome);

        /* Adjust asset values */
        theAssetValue.addAmount(myTransaction.getHolding());
        theAssetValue.addAmount(myTransaction.getLoanBook());

        /* Adjust source values */
        theSourceValue.addAmount(myTransaction.getInvested());
        theSourceValue.addAmount(myTransaction.getInterest());
        theSourceValue.addAmount(myTransaction.getFees());

        /* Calculate the RateOfReturn */
        calculateRateOfReturn(myIncome);
        removeDuplicates();
    }

    @Override
    protected void removeDuplicates() {
        /* remove underlying duplicates */
        super.removeDuplicates();

        /* Resolve duplicates */
        final CoeusRateSetterTotals myPrevious = (CoeusRateSetterTotals) getPrevious();
        if (Objects.equals(theAssetValue, myPrevious.getAssetValue())) {
            theAssetValue = myPrevious.getAssetValue();
        }
        if (Objects.equals(theHolding, myPrevious.getHolding())) {
            theHolding = myPrevious.getHolding();
        }
        if (Objects.equals(theLoanBook, myPrevious.getLoanBook())) {
            theLoanBook = myPrevious.getLoanBook();
        }
        if (Objects.equals(theSourceValue, myPrevious.getSourceValue())) {
            theSourceValue = myPrevious.getSourceValue();
        }
        if (Objects.equals(theInvested, myPrevious.getInvested())) {
            theInvested = myPrevious.getInvested();
        }
        if (Objects.equals(theEarnings, myPrevious.getEarnings())) {
            theEarnings = myPrevious.getEarnings();
        }
        if (Objects.equals(theInterest, myPrevious.getInterest())) {
            theInterest = myPrevious.getInterest();
        }
        if (Objects.equals(theFees, myPrevious.getFees())) {
            theFees = myPrevious.getFees();
        }
    }

    @Override
    protected TethysMoney getZero() {
        return CoeusRateSetterTransaction.ZERO_MONEY;
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
    public TethysMoney getXferPayment() {
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
    public MetisFieldSet<CoeusRateSetterTotals> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
