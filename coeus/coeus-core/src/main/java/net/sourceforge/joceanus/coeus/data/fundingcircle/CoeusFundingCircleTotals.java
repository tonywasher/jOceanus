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
package net.sourceforge.joceanus.coeus.data.fundingcircle;

import java.util.Objects;

import net.sourceforge.joceanus.coeus.data.CoeusTotals;
import net.sourceforge.joceanus.coeus.data.CoeusTransaction;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

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
     * SourceValue.
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
     * taxableEarnings.
     */
    private OceanusMoney theTaxableEarnings;

    /**
     * Interest.
     */
    private OceanusMoney theInterest;

    /**
     * NettInterest.
     */
    private OceanusMoney theNettInterest;

    /**
     * BadDebtInterest.
     */
    private OceanusMoney theBadDebtInterest;

    /**
     * BadDebtCapital.
     */
    private OceanusMoney theBadDebtCapital;

    /**
     * Fees.
     */
    private OceanusMoney theFees;

    /**
     * CashBack.
     */
    private OceanusMoney theCashBack;

    /**
     * XferPayment.
     */
    private OceanusMoney theXferPayment;

    /**
     * Losses.
     */
    private OceanusMoney theLosses;

    /**
     * BadDebt.
     */
    private OceanusMoney theBadDebt;

    /**
     * Recovered.
     */
    private OceanusMoney theRecovered;

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
        theAssetValue = new OceanusMoney(getZero());
        theHolding = new OceanusMoney(getZero());
        theLoanBook = new OceanusMoney(getZero());
        theSourceValue = new OceanusMoney(getZero());
        theInvested = new OceanusMoney(getZero());
        theEarnings = new OceanusMoney(getZero());
        theTaxableEarnings = new OceanusMoney(getZero());
        theInterest = new OceanusMoney(getZero());
        theNettInterest = new OceanusMoney(getZero());
        theBadDebtInterest = new OceanusMoney(getZero());
        theBadDebtCapital = new OceanusMoney(getZero());
        theFees = new OceanusMoney(getZero());
        theCashBack = new OceanusMoney(getZero());
        theXferPayment = new OceanusMoney(getZero());
        theLosses = new OceanusMoney(getZero());
        theBadDebt = new OceanusMoney(getZero());
        theRecovered = new OceanusMoney(getZero());
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
        theAssetValue = new OceanusMoney(pTotals.getAssetValue());
        theHolding = new OceanusMoney(pTotals.getHolding());
        theLoanBook = new OceanusMoney(pTotals.getLoanBook());
        theSourceValue = new OceanusMoney(pTotals.getSourceValue());
        theInvested = new OceanusMoney(pTotals.getInvested());
        theEarnings = new OceanusMoney(pTotals.getEarnings());
        theTaxableEarnings = new OceanusMoney(pTotals.getTaxableEarnings());
        theInterest = new OceanusMoney(pTotals.getInterest());
        theNettInterest = new OceanusMoney(pTotals.getNettInterest());
        theBadDebtInterest = new OceanusMoney(pTotals.getBadDebtInterest());
        theBadDebtCapital = new OceanusMoney(pTotals.getBadDebtCapital());
        theFees = new OceanusMoney(pTotals.getFees());
        theCashBack = new OceanusMoney(pTotals.getCashBack());
        theXferPayment = new OceanusMoney(pTotals.getXferPayment());
        theLosses = new OceanusMoney(pTotals.getLosses());
        theBadDebt = new OceanusMoney(pTotals.getBadDebt());
        theRecovered = new OceanusMoney(pTotals.getRecovered());
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
        final OceanusMoney myIncome = new OceanusMoney(myTransaction.getInterest());
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
        final OceanusMoney myLosses = new OceanusMoney(myTransaction.getBadDebt());
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
        removeDuplicates();
    }

    @Override
    protected void removeDuplicates() {
        /* remove underlying duplicates */
        super.removeDuplicates();

        /* Resolve duplicates */
        final CoeusFundingCircleTotals myPrevious = (CoeusFundingCircleTotals) getPrevious();
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
        if (Objects.equals(theXferPayment, myPrevious.getXferPayment())) {
            theXferPayment = myPrevious.getXferPayment();
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
    protected OceanusMoney getZero() {
        return CoeusFundingCircleTransaction.ZERO_MONEY;
    }

    @Override
    public CoeusFundingCircleMarket getMarket() {
        return (CoeusFundingCircleMarket) super.getMarket();
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
        return theTaxableEarnings;
    }

    @Override
    public OceanusMoney getInterest() {
        return theInterest;
    }

    @Override
    public OceanusMoney getNettInterest() {
        return theNettInterest;
    }

    @Override
    public OceanusMoney getBadDebtInterest() {
        return theBadDebtInterest;
    }

    @Override
    public OceanusMoney getBadDebtCapital() {
        return theBadDebtCapital;
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
    public OceanusMoney getCashBack() {
        return theCashBack;
    }

    @Override
    public OceanusMoney getXferPayment() {
        return theXferPayment;
    }

    @Override
    public OceanusMoney getLosses() {
        return theLosses;
    }

    @Override
    public OceanusMoney getBadDebt() {
        return theBadDebt;
    }

    @Override
    public OceanusMoney getRecovered() {
        return theRecovered;
    }

    @Override
    public MetisFieldSet<CoeusFundingCircleTotals> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
