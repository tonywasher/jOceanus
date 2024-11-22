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
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;

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
    private OceanusDecimal theAssetValue;

    /**
     * Holding.
     */
    private OceanusDecimal theHolding;

    /**
     * LoanBook.
     */
    private OceanusDecimal theLoanBook;

    /**
     * SourceValue.
     */
    private OceanusDecimal theSourceValue;

    /**
     * Invested.
     */
    private OceanusDecimal theInvested;

    /**
     * Earnings.
     */
    private OceanusDecimal theEarnings;

    /**
     * Interest.
     */
    private OceanusDecimal theInterest;

    /**
     * CashBack.
     */
    private OceanusDecimal theCashBack;

    /**
     * Shield.
     */
    private OceanusDecimal theShield;

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
        theAssetValue = new OceanusDecimal(getZero());
        theHolding = new OceanusDecimal(getZero());
        theLoanBook = new OceanusDecimal(getZero());
        theSourceValue = new OceanusDecimal(getZero());
        theInvested = new OceanusDecimal(getZero());
        theEarnings = new OceanusDecimal(getZero());
        theInterest = new OceanusDecimal(getZero());
        theCashBack = new OceanusDecimal(getZero());
        theShield = new OceanusDecimal(getZero());
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
        theAssetValue = new OceanusDecimal(pTotals.getAssetValue());
        theHolding = new OceanusDecimal(pTotals.getHolding());
        theLoanBook = new OceanusDecimal(pTotals.getLoanBook());
        theSourceValue = new OceanusDecimal(pTotals.getSourceValue());
        theInvested = new OceanusDecimal(pTotals.getInvested());
        theEarnings = new OceanusDecimal(pTotals.getEarnings());
        theInterest = new OceanusDecimal(pTotals.getInterest());
        theCashBack = new OceanusDecimal(pTotals.getCashBack());
        theShield = new OceanusDecimal(pTotals.getShield());
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
        final OceanusDecimal myIncome = new OceanusDecimal(pTransaction.getInterest());
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
    protected OceanusDecimal getZero() {
        return CoeusLendingWorksTransaction.ZERO_MONEY;
    }

    @Override
    public OceanusDecimal getAssetValue() {
        return theAssetValue;
    }

    @Override
    public OceanusDecimal getHolding() {
        return theHolding;
    }

    @Override
    public OceanusDecimal getLoanBook() {
        return theLoanBook;
    }

    @Override
    public OceanusDecimal getSourceValue() {
        return theSourceValue;
    }

    @Override
    public OceanusDecimal getInvested() {
        return theInvested;
    }

    @Override
    public OceanusDecimal getEarnings() {
        return theEarnings;
    }

    @Override
    public OceanusDecimal getTaxableEarnings() {
        return getEarnings();
    }

    @Override
    public OceanusDecimal getInterest() {
        return theInterest;
    }

    @Override
    public OceanusDecimal getNettInterest() {
        return getInterest();
    }

    @Override
    public OceanusDecimal getFees() {
        return getZero();
    }

    @Override
    public OceanusDecimal getShield() {
        return theShield;
    }

    @Override
    public OceanusDecimal getBadDebtInterest() {
        return getZero();
    }

    @Override
    public OceanusDecimal getBadDebtCapital() {
        return getZero();
    }

    @Override
    public OceanusDecimal getCashBack() {
        return theCashBack;
    }

    @Override
    public OceanusDecimal getXferPayment() {
        return getZero();
    }

    @Override
    public OceanusDecimal getLosses() {
        return getZero();
    }

    @Override
    public OceanusDecimal getBadDebt() {
        return getZero();
    }

    @Override
    public OceanusDecimal getRecovered() {
        return getZero();
    }

    @Override
    public MetisFieldSet<CoeusLendingWorksTotals> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
