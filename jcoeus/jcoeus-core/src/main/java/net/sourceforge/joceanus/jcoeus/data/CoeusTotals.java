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
package net.sourceforge.joceanus.jcoeus.data;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;

/**
 * Transaction Totals.
 */
public abstract class CoeusTotals
        implements MetisFieldTableItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusTotals.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(CoeusTotalsField.ID, CoeusTotals::getId);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.MARKET, CoeusTotals::getMarket);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.LOAN, CoeusTotals::getLoan);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.DATE, CoeusTotals::getDate);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.DESC, CoeusTotals::getDescription);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.TRANSTYPE, CoeusTotals::getTransType);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.TRANSACTION, CoeusTotals::getTransaction);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.SOURCEVALUE, CoeusTotals::getSourceValue);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.ASSETVALUE, CoeusTotals::getAssetValue);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.INVESTED, CoeusTotals::getInvested);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.HOLDING, CoeusTotals::getHolding);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.LOANBOOK, CoeusTotals::getLoanBook);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.EARNINGS, CoeusTotals::getEarnings);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.TAXABLEEARNINGS, CoeusTotals::getTaxableEarnings);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.INTEREST, CoeusTotals::getInterest);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.NETTINTEREST, CoeusTotals::getNettInterest);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.BADDEBTINTEREST, CoeusTotals::getBadDebtInterest);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.BADDEBTCAPITAL, CoeusTotals::getBadDebtCapital);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.FEES, CoeusTotals::getFees);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.CASHBACK, CoeusTotals::getCashBack);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.XFERPAYMENT, CoeusTotals::getXferPayment);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.LOSSES, CoeusTotals::getLosses);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.BADDEBT, CoeusTotals::getBadDebt);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.RECOVERED, CoeusTotals::getRecovered);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.LOANROR, CoeusTotals::getLoanRoR);
        FIELD_DEFS.declareLocalField(CoeusTotalsField.ASSETROR, CoeusTotals::getAssetRoR);
        FIELD_DEFS.declareCalculatedField(CoeusTotalsField.DELTA);
        FIELD_DEFS.declareCalculatedField(CoeusTotalsField.BALANCE);
    }

    /**
     * The market.
     */
    private final CoeusMarket theMarket;

    /**
     * The loan.
     */
    private final CoeusLoan theLoan;

    /**
     * The underlying transaction.
     */
    private final CoeusTransaction theTransaction;

    /**
     * The previous Totals.
     */
    private final CoeusTotals thePrevious;

    /**
     * The loan RateOfReturn.
     */
    private TethysRatio theLoanRor;

    /**
     * The asset RateOfReturn.
     */
    private TethysRatio theAssetRor;

    /**
     * The delta.
     */
    private TethysDecimal theDelta;

    /**
     * The balance.
     */
    private TethysDecimal theBalance;

    /**
     * Constructor.
     * @param pMarket the market.
     * @param pLoan the loan.
     */
    protected CoeusTotals(final CoeusMarket pMarket,
                          final CoeusLoan pLoan) {
        theMarket = pMarket;
        theLoan = pLoan;
        theTransaction = null;
        thePrevious = null;
        resetRatios();
    }

    /**
     * Constructor.
     * @param pUnderlying the underlying transaction.
     * @param pPrevious the previous totals (or null)
     */
    protected CoeusTotals(final CoeusTransaction pUnderlying,
                          final CoeusTotals pPrevious) {
        theMarket = pUnderlying.getMarket();
        theLoan = pUnderlying.getLoan();
        theTransaction = pUnderlying;
        thePrevious = pPrevious.theTransaction == null
                                                       ? null
                                                       : pPrevious;
    }

    /**
     * Constructor for cloning totals.
     * @param pTotals the totals to clone
     */
    protected CoeusTotals(final CoeusTotals pTotals) {
        theMarket = pTotals.getMarket();
        theLoan = pTotals.getLoan();
        theTransaction = pTotals.getTransaction();
        thePrevious = null;
    }

    /**
     * Obtain the fieldSet.
     * @return the fieldSet.
     */
    public static MetisFieldSet<CoeusTotals> getTheFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Integer getIndexedId() {
        return theTransaction != null
                                      ? theTransaction.getIndexedId()
                                      : -1;
    }

    /**
     * Obtain the id.
     * @return the id
     */
    private Integer getId() {
        return theTransaction != null
                                      ? theTransaction.getIndexedId()
                                      : null;
    }

    /**
     * Obtain the market.
     * @return the market
     */
    public CoeusMarket getMarket() {
        return theMarket;
    }

    /**
     * Obtain the loan.
     * @return the loan
     */
    public CoeusLoan getLoan() {
        return theLoan;
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public TethysDate getDate() {
        return theTransaction != null
               ? theTransaction.getDate()
               : null;
    }

    /**
     * Obtain the Description.
     * @return the description
     */
    public String getDescription() {
        return theTransaction == null
                                      ? CoeusTransactionType.TOTALS.toString()
                                      : theTransaction.getDescription();
    }

    /**
     * Obtain the TransactionType.
     * @return the transType
     */
    public CoeusTransactionType getTransType() {
        return theTransaction == null
                                      ? CoeusTransactionType.TOTALS
                                      : theTransaction.getTransType();
    }

    /**
     * Obtain the Transaction.
     * @return the transaction
     */
    private CoeusTransaction getTransaction() {
        return theTransaction;
    }

    /**
     * Obtain the total source value.
     * @return the value
     */
    public abstract TethysDecimal getSourceValue();

    /**
     * Obtain the total asset value.
     * @return the value
     */
    public abstract TethysDecimal getAssetValue();

    /**
     * Obtain the total invested.
     * @return the invested
     */
    public abstract TethysDecimal getInvested();

    /**
     * Obtain the total holding.
     * @return the holding
     */
    public abstract TethysDecimal getHolding();

    /**
     * Obtain the total loanBook.
     * @return the loanBook
     */
    public abstract TethysDecimal getLoanBook();

    /**
     * Obtain the total earnings.
     * @return the earnings
     */
    public abstract TethysDecimal getEarnings();

    /**
     * Obtain the total taxable interest.
     * @return the interest
     */
    public abstract TethysDecimal getTaxableEarnings();

    /**
     * Obtain the total interest.
     * @return the interest
     */
    public abstract TethysDecimal getInterest();

    /**
     * Obtain the total nettInterest.
     * @return the interest
     */
    public abstract TethysDecimal getNettInterest();

    /**
     * Obtain the total badDebt interest.
     * @return the interest
     */
    public abstract TethysDecimal getBadDebtInterest();

    /**
     * Obtain the total badDebt capital.
     * @return the capital
     */
    public abstract TethysDecimal getBadDebtCapital();

    /**
     * Obtain the total fees.
     * @return the fees
     */
    public abstract TethysDecimal getFees();

    /**
     * Obtain the total cashBack.
     * @return the cashBack
     */
    public abstract TethysDecimal getCashBack();

    /**
     * Obtain the total xferPayment.
     * @return the xferPayment
     */
    public abstract TethysDecimal getXferPayment();

    /**
     * Obtain the total losses.
     * @return the losses
     */
    public abstract TethysDecimal getLosses();

    /**
     * Obtain the total badDebt.
     * @return the badDebt
     */
    public abstract TethysDecimal getBadDebt();

    /**
     * Obtain the total recovered.
     * @return the recovered
     */
    public abstract TethysDecimal getRecovered();

    /**
     * Obtain the Transaction.
     * @return the transaction
     */
    public TethysDecimal getDelta() {
        return theDelta;
    }

    /**
     * Obtain the Transaction.
     * @return the transaction
     */
    public TethysDecimal getBalance() {
        return theBalance;
    }

    /**
     * Obtain the LoanRateOfReturn.
     * @return the rateOfReturn
     */
    public TethysRatio getLoanRoR() {
        return theLoanRor;
    }

    /**
     * Obtain the AssetRateOfReturn.
     * @return the rateOfReturn
     */
    public TethysRatio getAssetRoR() {
        return theAssetRor;
    }

    /**
     * Set the LoanRateOfReturn.
     * @param pRoR the rateOfReturn
     */
    protected void setLoanRoR(final TethysRatio pRoR) {
        theLoanRor = pRoR;
    }

    /**
     * Set the AssetRateOfReturn.
     * @param pRoR the rateOfReturn
     */
    protected void setAssetRoR(final TethysRatio pRoR) {
        theAssetRor = pRoR;
    }

    /**
     * Add transaction to totals.
     * @param pTransaction the transaction to add
     */
    protected abstract void addTransactionToTotals(CoeusTransaction pTransaction);

    /**
     * Reset the totals.
     */
    void resetTotals() {
        getSourceValue().setZero();
        getAssetValue().setZero();
        getInvested().setZero();
        getHolding().setZero();
        getLoanBook().setZero();
        getEarnings().setZero();
        getTaxableEarnings().setZero();
        getInterest().setZero();
        getNettInterest().setZero();
        getBadDebtInterest().setZero();
        getBadDebtCapital().setZero();
        getFees().setZero();
        getCashBack().setZero();
        getLosses().setZero();
        getBadDebt().setZero();
        getRecovered().setZero();
        resetRatios();
    }

    /**
     * Reset the ratios.
     */
    protected void resetRatios() {
        theAssetRor = TethysRatio.ONE;
        theLoanRor = TethysRatio.ONE;
    }

    /**
     * Calculate the fields.
     * @param pField the field
     */
    public void calculateFields(final MetisFieldDef pField) {
        /* Obtain the balance field value */
        final Object myBalance = pField.getFieldValue(this);

        /* Make sure that balance is Decimal */
        if (!(myBalance instanceof TethysDecimal)) {
            /* Null values */
            theBalance = null;
            theDelta = null;
        } else {
            /* Store new values */
            theBalance = (TethysDecimal) myBalance;
            theDelta = calculateDelta(pField);
        }
    }

    /**
     * Calculate the delta.
     * @param pField the field
     * @return the delta
     */
    private TethysDecimal calculateDelta(final MetisFieldDef pField) {
        /* Obtain the previous field value */
        final Object myPrevious = thePrevious == null
                                                      ? null
                                                      : pField.getFieldValue(thePrevious);

        /* If we do not have a preceding total */
        if (!(myPrevious instanceof TethysDecimal)) {
            /* Set delta as balance or null */
            return theBalance.isNonZero()
                                          ? theBalance
                                          : null;
        }

        /* Delta is null if there is no change */
        if (theBalance.equals(myPrevious)) {
            return null;
        }

        /* If this is a money value */
        if (theBalance instanceof TethysMoney
            && myPrevious instanceof TethysMoney) {
            final TethysMoney myResult = new TethysMoney((TethysMoney) theBalance);
            myResult.subtractAmount((TethysMoney) myPrevious);
            return myResult;
        }

        /* Handle standard result */
        final TethysDecimal myResult = new TethysDecimal(theBalance);
        myResult.subtractValue((TethysDecimal) myPrevious);
        return myResult;
    }

    /**
     * Obtain balance field for TotalSet.
     * @param pTotalSet the totalSet
     * @return the balance field
     */
    static CoeusTotalsField getBalanceField(final CoeusTotalSet pTotalSet) {
        switch (pTotalSet) {
            case INVESTED:
                return CoeusTotalsField.INVESTED;
            case EARNINGS:
                return CoeusTotalsField.EARNINGS;
            case TAXABLEEARNINGS:
                return CoeusTotalsField.TAXABLEEARNINGS;
            case INTEREST:
                return CoeusTotalsField.INTEREST;
            case NETTINTEREST:
                return CoeusTotalsField.NETTINTEREST;
            case FEES:
                return CoeusTotalsField.FEES;
            case CASHBACK:
                return CoeusTotalsField.CASHBACK;
            case XFERPAYMENT:
                return CoeusTotalsField.XFERPAYMENT;
            case BADDEBTCAPITAL:
                return CoeusTotalsField.BADDEBTCAPITAL;
            case BADDEBTINTEREST:
                return CoeusTotalsField.BADDEBTINTEREST;
            case LOSSES:
                return CoeusTotalsField.LOSSES;
            case BADDEBT:
                return CoeusTotalsField.BADDEBT;
            case RECOVERED:
                return CoeusTotalsField.RECOVERED;
            case HOLDING:
                return CoeusTotalsField.HOLDING;
            case LOANBOOK:
            default:
                return CoeusTotalsField.LOANBOOK;
        }
    }

    /**
     * Obtain zero total.
     * @return zero total
     */
    protected abstract TethysDecimal getZero();

    @Override
    public String toString() {
        /* Create builder and access formatter */
        final StringBuilder myBuilder = new StringBuilder();

        /* Add the values */
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_ASSETVALUE, getAssetValue());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_HOLDING, getHolding());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_LOANBOOK, getLoanBook());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_SOURCEVALUE, getSourceValue());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_INVESTED, getInvested());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_EARNINGS, getEarnings());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_INTEREST, getInterest());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_NETTINTEREST, getNettInterest());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_TAXEARNINGS, getTaxableEarnings());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_BDINTEREST, getBadDebtInterest());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_BDCAPITAL, getBadDebtCapital());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_FEES, getFees());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_CASHBACK, getCashBack());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_XFERPAYMENT, getXferPayment());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_LOSSES, getLosses());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_BADDEBT, getBadDebt());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_RECOVERED, getRecovered());

        /* Add brackets around the values */
        myBuilder.insert(0, CoeusTransaction.CHAR_OPEN);
        myBuilder.append(CoeusTransaction.CHAR_CLOSE);

        /* Format the transaction type and date */
        if (theTransaction != null) {
            myBuilder.insert(0, getDate().toString());
            myBuilder.insert(0, CoeusTransaction.CHAR_BLANK);
        }
        myBuilder.insert(0, CoeusTransactionType.TOTALS.toString());

        /* Return the formatted string */
        return myBuilder.toString();
    }
}
