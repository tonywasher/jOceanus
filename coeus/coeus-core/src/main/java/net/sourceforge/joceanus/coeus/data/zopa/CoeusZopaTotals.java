/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2025 Tony Washer
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
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;

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
     * Source Value.
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
     * Taxable Earnings.
     */
    private OceanusDecimal theTaxableEarnings;

    /**
     * Interest.
     */
    private OceanusDecimal theInterest;

    /**
     * NettInterest.
     */
    private OceanusDecimal theNettInterest;

    /**
     * BadDebtInterest.
     */
    private OceanusDecimal theBadDebtInterest;

    /**
     * BadDebtCapital.
     */
    private OceanusDecimal theBadDebtCapital;

    /**
     * Fees.
     */
    private OceanusDecimal theFees;

    /**
     * CashBack.
     */
    private OceanusDecimal theCashBack;

    /**
     * Losses.
     */
    private OceanusDecimal theLosses;

    /**
     * BadDebt.
     */
    private OceanusDecimal theBadDebt;

    /**
     * Recovered.
     */
    private OceanusDecimal theRecovered;

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
        theAssetValue = new OceanusDecimal(getZero());
        theHolding = new OceanusDecimal(getZero());
        theLoanBook = new OceanusDecimal(getZero());
        theSourceValue = new OceanusDecimal(getZero());
        theInvested = new OceanusDecimal(getZero());
        theEarnings = new OceanusDecimal(getZero());
        theTaxableEarnings = new OceanusDecimal(getZero());
        theInterest = new OceanusDecimal(getZero());
        theNettInterest = new OceanusDecimal(getZero());
        theBadDebtInterest = new OceanusDecimal(getZero());
        theBadDebtCapital = new OceanusDecimal(getZero());
        theFees = new OceanusDecimal(getZero());
        theCashBack = new OceanusDecimal(getZero());
        theLosses = new OceanusDecimal(getZero());
        theBadDebt = new OceanusDecimal(getZero());
        theRecovered = new OceanusDecimal(getZero());
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
        theAssetValue = new OceanusDecimal(pTotals.getAssetValue());
        theHolding = new OceanusDecimal(pTotals.getHolding());
        theLoanBook = new OceanusDecimal(pTotals.getLoanBook());
        theSourceValue = new OceanusDecimal(pTotals.getSourceValue());
        theInvested = new OceanusDecimal(pTotals.getInvested());
        theEarnings = new OceanusDecimal(pTotals.getEarnings());
        theTaxableEarnings = new OceanusDecimal(pTotals.getTaxableEarnings());
        theInterest = new OceanusDecimal(pTotals.getInterest());
        theNettInterest = new OceanusDecimal(pTotals.getNettInterest());
        theBadDebtInterest = new OceanusDecimal(pTotals.getBadDebtInterest());
        theBadDebtCapital = new OceanusDecimal(pTotals.getBadDebtCapital());
        theFees = new OceanusDecimal(pTotals.getFees());
        theCashBack = new OceanusDecimal(pTotals.getCashBack());
        theLosses = new OceanusDecimal(pTotals.getLosses());
        theBadDebt = new OceanusDecimal(pTotals.getBadDebt());
        theRecovered = new OceanusDecimal(pTotals.getRecovered());
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
        final OceanusDecimal myIncome = new OceanusDecimal(pTransaction.getInterest());
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
        final OceanusDecimal myLosses = new OceanusDecimal(pTransaction.getBadDebt());
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
    protected OceanusDecimal getZero() {
        return CoeusZopaTransaction.ZERO_MONEY;
    }

    @Override
    public CoeusZopaMarket getMarket() {
        return (CoeusZopaMarket) super.getMarket();
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
        return theTaxableEarnings;
    }

    @Override
    public OceanusDecimal getInterest() {
        return theInterest;
    }

    @Override
    public OceanusDecimal getNettInterest() {
        return theNettInterest;
    }

    @Override
    public OceanusDecimal getBadDebtInterest() {
        return theBadDebtInterest;
    }

    @Override
    public OceanusDecimal getBadDebtCapital() {
        return theBadDebtCapital;
    }

    @Override
    public OceanusDecimal getFees() {
        return theFees;
    }

    @Override
    public OceanusDecimal getShield() {
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
        return theLosses;
    }

    @Override
    public OceanusDecimal getBadDebt() {
        return theBadDebt;
    }

    @Override
    public OceanusDecimal getRecovered() {
        return theRecovered;
    }

    @Override
    public MetisFieldSet<CoeusZopaTotals> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
