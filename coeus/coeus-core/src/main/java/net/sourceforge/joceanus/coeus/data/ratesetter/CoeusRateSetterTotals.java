/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.coeus.data.ratesetter;

import java.util.Objects;

import net.sourceforge.joceanus.coeus.data.CoeusTotals;
import net.sourceforge.joceanus.coeus.data.CoeusTransaction;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

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
    private OceanusMoney theAssetValue;

    /**
     * Holding.
     */
    private OceanusMoney theHolding;

    /**
     * LoanBook.
     */
    private OceanusMoney theLoanBook;

    /**
     * Source Value.
     */
    private OceanusMoney theSourceValue;

    /**
     * Invested.
     */
    private OceanusMoney theInvested;

    /**
     * Earnings.
     */
    private OceanusMoney theEarnings;

    /**
     * Interest.
     */
    private OceanusMoney theInterest;

    /**
     * Fees.
     */
    private OceanusMoney theFees;

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
        theAssetValue = new OceanusMoney(getZero());
        theHolding = new OceanusMoney(getZero());
        theLoanBook = new OceanusMoney(getZero());
        theSourceValue = new OceanusMoney(getZero());
        theInvested = new OceanusMoney(getZero());
        theEarnings = new OceanusMoney(getZero());
        theInterest = new OceanusMoney(getZero());
        theFees = new OceanusMoney(getZero());
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
        theAssetValue = new OceanusMoney(pTotals.getAssetValue());
        theHolding = new OceanusMoney(pTotals.getHolding());
        theLoanBook = new OceanusMoney(pTotals.getLoanBook());
        theSourceValue = new OceanusMoney(pTotals.getSourceValue());
        theInvested = new OceanusMoney(pTotals.getInvested());
        theEarnings = new OceanusMoney(pTotals.getEarnings());
        theInterest = new OceanusMoney(pTotals.getInterest());
        theFees = new OceanusMoney(pTotals.getFees());
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
        final OceanusMoney myIncome = new OceanusMoney(myTransaction.getInterest());
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
    protected OceanusMoney getZero() {
        return CoeusRateSetterTransaction.ZERO_MONEY;
    }

    @Override
    public OceanusMoney getAssetValue() {
        return theAssetValue;
    }

    @Override
    public OceanusMoney getHolding() {
        return theHolding;
    }

    @Override
    public OceanusMoney getLoanBook() {
        return theLoanBook;
    }

    @Override
    public OceanusMoney getSourceValue() {
        return theSourceValue;
    }

    @Override
    public OceanusMoney getInvested() {
        return theInvested;
    }

    @Override
    public OceanusMoney getEarnings() {
        return theEarnings;
    }

    @Override
    public OceanusMoney getTaxableEarnings() {
        return getEarnings();
    }

    @Override
    public OceanusMoney getInterest() {
        return theInterest;
    }

    @Override
    public OceanusMoney getNettInterest() {
        return getInterest();
    }

    @Override
    public OceanusMoney getFees() {
        return theFees;
    }

    @Override
    public OceanusMoney getShield() {
        return getZero();
    }

    @Override
    public OceanusMoney getBadDebtInterest() {
        return getZero();
    }

    @Override
    public OceanusMoney getBadDebtCapital() {
        return getZero();
    }

    @Override
    public OceanusMoney getCashBack() {
        return getZero();
    }

    @Override
    public OceanusMoney getXferPayment() {
        return getZero();
    }

    @Override
    public OceanusMoney getLosses() {
        return getZero();
    }

    @Override
    public OceanusMoney getBadDebt() {
        return getZero();
    }

    @Override
    public OceanusMoney getRecovered() {
        return getZero();
    }

    @Override
    public MetisFieldSet<CoeusRateSetterTotals> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
