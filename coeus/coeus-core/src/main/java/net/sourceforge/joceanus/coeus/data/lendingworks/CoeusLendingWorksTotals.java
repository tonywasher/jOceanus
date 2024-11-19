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
package net.sourceforge.joceanus.coeus.data.lendingworks;

import java.util.Objects;

import net.sourceforge.joceanus.coeus.data.CoeusTotals;
import net.sourceforge.joceanus.coeus.data.CoeusTransaction;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.tethys.decimal.TethysDecimal;

/**
 * LendingWorks Transaction Totals.
 */
public final class CoeusLendingWorksTotals
        extends CoeusTotals {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusLendingWorksTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusLendingWorksTotals.class);

    /**
     * AssetValue.
     */
    private TethysDecimal theAssetValue;

    /**
     * Holding.
     */
    private TethysDecimal theHolding;

    /**
     * LoanBook.
     */
    private TethysDecimal theLoanBook;

    /**
     * SourceValue.
     */
    private TethysDecimal theSourceValue;

    /**
     * Invested.
     */
    private TethysDecimal theInvested;

    /**
     * Earnings.
     */
    private TethysDecimal theEarnings;

    /**
     * Interest.
     */
    private TethysDecimal theInterest;

    /**
     * CashBack.
     */
    private TethysDecimal theCashBack;

    /**
     * Shield.
     */
    private TethysDecimal theShield;

    /**
     * Constructor for zeroed totals.
     * @param pMarket the market
     */
    CoeusLendingWorksTotals(final CoeusLendingWorksMarket pMarket) {
        this(pMarket, null);
    }

    /**
     * Constructor for zeroed totals.
     * @param pLoan the loan
     */
    CoeusLendingWorksTotals(final CoeusLendingWorksLoan pLoan) {
        this(pLoan.getMarket(), pLoan);
    }

    /**
     * Constructor for zeroed totals.
     * @param pMarket the market
     * @param pLoan the loan
     */
    CoeusLendingWorksTotals(final CoeusLendingWorksMarket pMarket,
                            final CoeusLendingWorksLoan pLoan) {
        /* Initialise underlying class */
        super(pMarket, pLoan);

        /* Initialise values */
        theAssetValue = new TethysDecimal(getZero());
        theHolding = new TethysDecimal(getZero());
        theLoanBook = new TethysDecimal(getZero());
        theSourceValue = new TethysDecimal(getZero());
        theInvested = new TethysDecimal(getZero());
        theEarnings = new TethysDecimal(getZero());
        theInterest = new TethysDecimal(getZero());
        theCashBack = new TethysDecimal(getZero());
        theShield = new TethysDecimal(getZero());
    }

    /**
     * Constructor for running total.
     * @param pTotals the previous totals
     * @param pUnderlying the underlying transaction
     */
    CoeusLendingWorksTotals(final CoeusLendingWorksTotals pTotals,
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
        theCashBack = new TethysDecimal(pTotals.getCashBack());
        theShield = new TethysDecimal(pTotals.getShield());
    }

    @Override
    protected void calculateDelta(final CoeusTotals pBase) {
        /* Calculate delta rateOfReturns */
        super.calculateDelta(pBase);

        /* Calculate the deltas */
        theAssetValue.subtractValue(pBase.getAssetValue());
        theHolding.subtractValue(pBase.getHolding());
        theLoanBook.subtractValue(pBase.getLoanBook());
        theSourceValue.subtractValue(pBase.getSourceValue());
        theInvested.subtractValue(pBase.getInvested());
        theEarnings.subtractValue(pBase.getEarnings());
        theInterest.subtractValue(pBase.getInterest());
        theCashBack.subtractValue(pBase.getCashBack());
        theShield.subtractValue(pBase.getShield());
    }

    @Override
    protected void addTransactionToTotals(final CoeusTransaction pTransaction) {
        /* Add values from transaction */
        theInvested.addValue(pTransaction.getInvested());
        theHolding.addValue(pTransaction.getHolding());
        theLoanBook.addValue(pTransaction.getLoanBook());
        theInterest.addValue(pTransaction.getInterest());
        theCashBack.addValue(pTransaction.getCashBack());
        theShield.addValue(pTransaction.getShield());

        /* Adjust earnings */
        final TethysDecimal myIncome = new TethysDecimal(pTransaction.getInterest());
        myIncome.addValue(pTransaction.getFees());
        myIncome.addValue(pTransaction.getCashBack());
        myIncome.addValue(pTransaction.getShield());
        theEarnings.addValue(myIncome);

        /* Adjust asset values */
        theAssetValue.addValue(pTransaction.getHolding());
        theAssetValue.addValue(pTransaction.getLoanBook());

        /* Adjust source values */
        theSourceValue.addValue(pTransaction.getInvested());
        theSourceValue.addValue(pTransaction.getInterest());
        theSourceValue.addValue(pTransaction.getCashBack());
        theSourceValue.addValue(pTransaction.getShield());

        /* Calculate the RateOfReturn */
        calculateRateOfReturn(myIncome);
        removeDuplicates();
    }

    @Override
    protected void removeDuplicates() {
        /* remove underlying duplicates */
        super.removeDuplicates();

        /* Resolve duplicates */
        final CoeusLendingWorksTotals myPrevious = (CoeusLendingWorksTotals) getPrevious();
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
        if (Objects.equals(theCashBack, myPrevious.getCashBack())) {
            theCashBack = myPrevious.getCashBack();
        }
        if (Objects.equals(theShield, myPrevious.getShield())) {
            theShield = myPrevious.getShield();
        }
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
        return getEarnings();
    }

    @Override
    public TethysDecimal getInterest() {
        return theInterest;
    }

    @Override
    public TethysDecimal getNettInterest() {
        return getInterest();
    }

    @Override
    public TethysDecimal getFees() {
        return getZero();
    }

    @Override
    public TethysDecimal getShield() {
        return theShield;
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
        return theCashBack;
    }

    @Override
    public TethysDecimal getXferPayment() {
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
    public MetisFieldSet<CoeusLendingWorksTotals> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
