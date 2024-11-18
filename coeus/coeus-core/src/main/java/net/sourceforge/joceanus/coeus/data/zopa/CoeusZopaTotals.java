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
package net.sourceforge.joceanus.coeus.data.zopa;

import java.util.Objects;

import net.sourceforge.joceanus.coeus.data.CoeusTotals;
import net.sourceforge.joceanus.coeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Zopa Transaction Totals.
 */
public final class CoeusZopaTotals
        extends CoeusTotals {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusZopaTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusZopaTotals.class);

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
     * Source Value.
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
     * Taxable Earnings.
     */
    private TethysDecimal theTaxableEarnings;

    /**
     * Interest.
     */
    private TethysDecimal theInterest;

    /**
     * NettInterest.
     */
    private TethysDecimal theNettInterest;

    /**
     * BadDebtInterest.
     */
    private TethysDecimal theBadDebtInterest;

    /**
     * BadDebtCapital.
     */
    private TethysDecimal theBadDebtCapital;

    /**
     * Fees.
     */
    private TethysDecimal theFees;

    /**
     * CashBack.
     */
    private TethysDecimal theCashBack;

    /**
     * Losses.
     */
    private TethysDecimal theLosses;

    /**
     * BadDebt.
     */
    private TethysDecimal theBadDebt;

    /**
     * Recovered.
     */
    private TethysDecimal theRecovered;

    /**
     * Constructor for zeroed totals.
     * @param pMarket the market
     */
    CoeusZopaTotals(final CoeusZopaMarket pMarket) {
        this(pMarket, null);
    }

    /**
     * Constructor for zeroed totals.
     * @param pLoan the loan
     */
    CoeusZopaTotals(final CoeusZopaLoan pLoan) {
        this(pLoan.getMarket(), pLoan);
    }

    /**
     * Constructor for zeroed totals.
     * @param pMarket the market
     * @param pLoan the loan
     */
    CoeusZopaTotals(final CoeusZopaMarket pMarket,
                    final CoeusZopaLoan pLoan) {
        /* Initialise underlying class */
        super(pMarket, pLoan);

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
    CoeusZopaTotals(final CoeusZopaTotals pTotals,
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
        theTaxableEarnings.subtractValue(pBase.getTaxableEarnings());
        theInterest.subtractValue(pBase.getInterest());
        theNettInterest.subtractValue(pBase.getNettInterest());
        theBadDebtInterest.subtractValue(pBase.getBadDebtInterest());
        theBadDebtCapital.subtractValue(pBase.getBadDebtCapital());
        theFees.subtractValue(pBase.getFees());
        theCashBack.subtractValue(pBase.getCashBack());
        theLosses.subtractValue(pBase.getLosses());
        theBadDebt.subtractValue(pBase.getBadDebt());
        theRecovered.subtractValue(pBase.getRecovered());
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
        final TethysDecimal myIncome = new TethysDecimal(pTransaction.getInterest());
        myIncome.addValue(pTransaction.getFees());
        myIncome.addValue(pTransaction.getCashBack());
        theEarnings.addValue(myIncome);

        /* Adjust taxable earnings */
        theTaxableEarnings.addValue(pTransaction.getInterest());
        theTaxableEarnings.addValue(pTransaction.getBadDebtInterest());
        theTaxableEarnings.addValue(pTransaction.getFees());

        /* Adjust nett interest */
        theNettInterest.addValue(pTransaction.getInterest());
        theNettInterest.addValue(pTransaction.getFees());

        /* Adjust losses */
        final TethysDecimal myLosses = new TethysDecimal(pTransaction.getBadDebt());
        myLosses.addValue(pTransaction.getRecovered());
        theLosses.addValue(myLosses);
        myIncome.addValue(myLosses);

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

        /* Calculate the RateOfReturn */
        calculateRateOfReturn(myIncome);
        removeDuplicates();
    }

    @Override
    protected void removeDuplicates() {
        /* remove underlying duplicates */
        super.removeDuplicates();

        /* Resolve duplicates */
        final CoeusZopaTotals myPrevious = (CoeusZopaTotals) getPrevious();
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
        if (Objects.equals(theTaxableEarnings, myPrevious.getTaxableEarnings())) {
            theTaxableEarnings = myPrevious.getTaxableEarnings();
        }
        if (Objects.equals(theInterest, myPrevious.getInterest())) {
            theInterest = myPrevious.getInterest();
        }
        if (Objects.equals(theNettInterest, myPrevious.getNettInterest())) {
            theNettInterest = myPrevious.getNettInterest();
        }
        if (Objects.equals(theBadDebtInterest, myPrevious.getBadDebtInterest())) {
            theBadDebtInterest = myPrevious.getBadDebtInterest();
        }
        if (Objects.equals(theBadDebtCapital, myPrevious.getBadDebtCapital())) {
            theBadDebtCapital = myPrevious.getBadDebtCapital();
        }
        if (Objects.equals(theFees, myPrevious.getFees())) {
            theFees = myPrevious.getFees();
        }
        if (Objects.equals(theCashBack, myPrevious.getCashBack())) {
            theCashBack = myPrevious.getCashBack();
        }
        if (Objects.equals(theLosses, myPrevious.getLosses())) {
            theLosses = myPrevious.getLosses();
        }
        if (Objects.equals(theBadDebt, myPrevious.getBadDebt())) {
            theBadDebt = myPrevious.getBadDebt();
        }
        if (Objects.equals(theRecovered, myPrevious.getRecovered())) {
            theRecovered = myPrevious.getRecovered();
        }
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
    public TethysDecimal getShield() {
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
    public MetisFieldSet<CoeusZopaTotals> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
