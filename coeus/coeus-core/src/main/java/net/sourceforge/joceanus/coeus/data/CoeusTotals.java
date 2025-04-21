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
package net.sourceforge.joceanus.coeus.data;

import java.util.Objects;

import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;

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
        FIELD_DEFS.declareLocalField(CoeusTotalsField.SHIELD, CoeusTotals::getShield);
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
    private OceanusRatio theLoanRor;

    /**
     * The asset RateOfReturn.
     */
    private OceanusRatio theAssetRor;

    /**
     * The delta.
     */
    private OceanusDecimal theDelta;

    /**
     * The balance.
     */
    private OceanusDecimal theBalance;

    /**
     * Constructor.
     * @param pMarket the market.
     * @param pLoan the loan.
     */
    protected CoeusTotals(final CoeusMarket pMarket,
                          final CoeusLoan pLoan) {
        /* Record details */
        theMarket = pMarket;
        theLoan = pLoan;
        theTransaction = null;
        thePrevious = null;

        /* Set rate of return ratios disabling Asset for Loan totals */
        theAssetRor = pLoan == null ? OceanusRatio.ONE : null;
        theLoanRor = OceanusRatio.ONE;
    }

    /**
     * Constructor.
     * @param pUnderlying the underlying transaction.
     * @param pPrevious the previous totals (or null)
     */
    protected CoeusTotals(final CoeusTransaction pUnderlying,
                          final CoeusTotals pPrevious) {
        theMarket = pPrevious.getMarket();
        theLoan = pUnderlying != null ? pUnderlying.getLoan() : null;
        theTransaction = pUnderlying;
        thePrevious = pPrevious;

        /* Clone the rate of returns */
        final OceanusRatio myAssetRor = thePrevious.getAssetRoR();
        theAssetRor = myAssetRor == null ? null : new OceanusRatio(myAssetRor);
        theLoanRor = new OceanusRatio(thePrevious.getLoanRoR());
    }

    /**
     * Calculate the delta from a totals.
     * @param pBase the base totals
     */
    protected void calculateDelta(final CoeusTotals pBase) {
        /* Calculate the delta rate of returns */
        if (theAssetRor != null) {
            theAssetRor = new OceanusRatio(theAssetRor, pBase.getAssetRoR());
        }
        theLoanRor = new OceanusRatio(theLoanRor, pBase.getLoanRoR());
    }

    /**
     * Remove duplicates.
     */
    protected void removeDuplicates() {
        /* Use previous values if equal */
        if (Objects.equals(theAssetRor, thePrevious.getAssetRoR())) {
            theAssetRor = thePrevious.getAssetRoR();
        }
        if (Objects.equals(theLoanRor, thePrevious.getLoanRoR())) {
            theLoanRor = thePrevious.getLoanRoR();
        }
    }

    /**
     * Calculate the rateOfReturn.
     * @param pDelta the delta
     */
    protected void calculateRateOfReturn(final OceanusDecimal pDelta) {
        /* No need to calculate if there is no delta */
        if (pDelta.isNonZero())  {
            /* Calculate the delta rate of returns */
            if (theAssetRor != null) {
                theAssetRor = theAssetRor.multiplyBy(calculateRateOfReturn(thePrevious.getAssetValue(), pDelta));
            }
            theLoanRor = theLoanRor.multiplyBy(calculateRateOfReturn(thePrevious.getLoanBook(), pDelta));
        }
        if (Objects.equals(theAssetRor, thePrevious.getAssetRoR())) {
            theAssetRor = thePrevious.getAssetRoR();
        }
        if (Objects.equals(theLoanRor, thePrevious.getLoanRoR())) {
            theLoanRor = thePrevious.getLoanRoR();
        }
    }

    /**
     * Calculate the rateOfReturn.
     * @param pBase the base value
     * @param pDelta the delta
     * @return the rate of return
     */
    private static OceanusRatio calculateRateOfReturn(final OceanusDecimal pBase,
                                                      final OceanusDecimal pDelta) {
        /* If base is zero, just return one */
        if (pBase.isZero()) {
            return OceanusRatio.ONE;
        }

        /* Calculate the delta rate of returns */
        final OceanusDecimal myNew = new OceanusDecimal(pBase);
        myNew.addValue(pDelta);
        return new OceanusRatio(myNew, pBase);
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
    public OceanusDate getDate() {
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
     * Obtain the Previous totals.
     * @return the transaction
     */
    protected CoeusTotals getPrevious() {
        return thePrevious;
    }

    /**
     * Obtain the Transaction.
     * @return the transaction
     */
    protected CoeusTransaction getTransaction() {
        return theTransaction;
    }

    /**
     * Obtain the total source value.
     * @return the value
     */
    public abstract OceanusDecimal getSourceValue();

    /**
     * Obtain the total asset value.
     * @return the value
     */
    public abstract OceanusDecimal getAssetValue();

    /**
     * Obtain the total invested.
     * @return the invested
     */
    public abstract OceanusDecimal getInvested();

    /**
     * Obtain the total holding.
     * @return the holding
     */
    public abstract OceanusDecimal getHolding();

    /**
     * Obtain the total loanBook.
     * @return the loanBook
     */
    public abstract OceanusDecimal getLoanBook();

    /**
     * Obtain the total earnings.
     * @return the earnings
     */
    public abstract OceanusDecimal getEarnings();

    /**
     * Obtain the total taxable interest.
     * @return the interest
     */
    public abstract OceanusDecimal getTaxableEarnings();

    /**
     * Obtain the total interest.
     * @return the interest
     */
    public abstract OceanusDecimal getInterest();

    /**
     * Obtain the total nettInterest.
     * @return the interest
     */
    public abstract OceanusDecimal getNettInterest();

    /**
     * Obtain the total badDebt interest.
     * @return the interest
     */
    public abstract OceanusDecimal getBadDebtInterest();

    /**
     * Obtain the total badDebt capital.
     * @return the capital
     */
    public abstract OceanusDecimal getBadDebtCapital();

    /**
     * Obtain the total fees.
     * @return the fees
     */
    public abstract OceanusDecimal getFees();

    /**
     * Obtain the total shield.
     * @return the shield
     */
    public abstract OceanusDecimal getShield();

    /**
     * Obtain the total cashBack.
     * @return the cashBack
     */
    public abstract OceanusDecimal getCashBack();

    /**
     * Obtain the total xferPayment.
     * @return the xferPayment
     */
    public abstract OceanusDecimal getXferPayment();

    /**
     * Obtain the total losses.
     * @return the losses
     */
    public abstract OceanusDecimal getLosses();

    /**
     * Obtain the total badDebt.
     * @return the badDebt
     */
    public abstract OceanusDecimal getBadDebt();

    /**
     * Obtain the total recovered.
     * @return the recovered
     */
    public abstract OceanusDecimal getRecovered();

    /**
     * Obtain the Delta.
     * @return the delta
     */
    public OceanusDecimal getDelta() {
        return theDelta;
    }

    /**
     * Obtain the Balance.
     * @return the balance
     */
    public OceanusDecimal getBalance() {
        return theBalance;
    }

    /**
     * Obtain the LoanRateOfReturn.
     * @return the rateOfReturn
     */
    public OceanusRatio getLoanRoR() {
        return theLoanRor;
    }

    /**
     * Obtain the AssetRateOfReturn.
     * @return the rateOfReturn
     */
    public OceanusRatio getAssetRoR() {
        return theAssetRor;
    }

    /**
     * Add transaction to totals.
     * @param pTransaction the transaction to add
     */
    protected abstract void addTransactionToTotals(CoeusTransaction pTransaction);

    /**
     * Calculate the fields.
     * @param pField the field
     * @param pInitial the initial totals
     */
    public void calculateFields(final MetisFieldDef pField,
                                final CoeusTotals pInitial) {
        /* Obtain the balance field value */
        final Object myBalance = pField.getFieldValue(this);
        final Object myBase = pField.getFieldValue(pInitial);

        /* Make sure that balance is Decimal */
        if (!(myBalance instanceof OceanusDecimal)) {
            /* Null values */
            theBalance = null;
            theDelta = null;
        } else {
            /* Store new values */
            theBalance = calculateDelta((OceanusDecimal) myBalance, myBase, false);
            theDelta = calculateDelta(pField, (OceanusDecimal) myBalance);
        }
    }

    /**
     * Calculate the delta.
     * @param pField the field
     * @param pBalance the balance
     * @return the delta
     */
    private OceanusDecimal calculateDelta(final MetisFieldDef pField,
                                          final OceanusDecimal pBalance) {
        /* Obtain the previous field value */
        final Object myPrevious = thePrevious == null
                                                      ? null
                                                      : pField.getFieldValue(thePrevious);

        /* return the delta */
        return calculateDelta(pBalance, myPrevious, true);
    }

    /**
     * Calculate the delta.
     * @param pBalance the balance
     * @param pPrevious the previous value
     * @param pNullIfEqual return null if the values are equal
     * @return the delta
     */
    private static OceanusDecimal calculateDelta(final OceanusDecimal pBalance,
                                                 final Object pPrevious,
                                                 final boolean pNullIfEqual) {
        /* If we do not have a preceding total */
        if (!(pPrevious instanceof OceanusDecimal)) {
            /* Set delta as balance or null */
            return pBalance.isNonZero() || !pNullIfEqual
                   ? pBalance
                   : null;
        }

        /* Delta is null if there is no change */
        if (pBalance.equals(pPrevious) && pNullIfEqual) {
            return null;
        }

        /* If this is a money value */
        if (pBalance instanceof OceanusMoney
                && pPrevious instanceof OceanusMoney) {
            final OceanusMoney myResult = new OceanusMoney((OceanusMoney) pBalance);
            myResult.subtractAmount((OceanusMoney) pPrevious);
            return myResult;
        }

        /* If this is a ratio value */
        if (pBalance instanceof OceanusRatio
                && pPrevious instanceof OceanusRatio) {
            return new OceanusRatio(pBalance, (OceanusDecimal) pPrevious);
        }

        /* Handle standard result */
        final OceanusDecimal myResult = new OceanusDecimal(pBalance);
        myResult.subtractValue((OceanusDecimal) pPrevious);
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
            case LOANROR:
                return CoeusTotalsField.LOANROR;
            case ASSETROR:
                return CoeusTotalsField.ASSETROR;
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
    protected abstract OceanusDecimal getZero();

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
        myBuilder.insert(0, CoeusTransactionType.TOTALS);

        /* Return the formatted string */
        return myBuilder.toString();
    }
}
