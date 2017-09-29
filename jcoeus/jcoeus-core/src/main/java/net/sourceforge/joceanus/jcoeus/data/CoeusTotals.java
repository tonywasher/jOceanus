/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataTableItem;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Transaction Totals.
 */
public abstract class CoeusTotals
        implements MetisDataTableItem {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusTotals.class);

    /**
     * ID Field Id.
     */
    private static final MetisDataField FIELD_ID = FIELD_DEFS.declareLocalField(CoeusResource.DATA_ID.getValue());

    /**
     * Market Field Id.
     */
    private static final MetisDataField FIELD_MARKET = FIELD_DEFS.declareLocalField(CoeusResource.DATA_MARKET.getValue());

    /**
     * Loan Field Id.
     */
    public static final MetisDataField FIELD_LOAN = FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOAN.getValue());

    /**
     * Date Field Id.
     */
    public static final MetisDataField FIELD_DATE = FIELD_DEFS.declareLocalField(CoeusResource.DATA_DATE.getValue());

    /**
     * Description Field Id.
     */
    public static final MetisDataField FIELD_DESC = FIELD_DEFS.declareLocalField(CoeusResource.DATA_DESC.getValue());

    /**
     * TransactionType Field Id.
     */
    public static final MetisDataField FIELD_TYPE = FIELD_DEFS.declareLocalField(CoeusResource.DATA_TRANSTYPE.getValue());

    /**
     * Transaction Field Id.
     */
    private static final MetisDataField FIELD_TRANSACTION = FIELD_DEFS.declareLocalField(CoeusResource.DATA_TRANSACTION.getValue());

    /**
     * TotalSourceValue Field Id.
     */
    private static final MetisDataField FIELD_SOURCEVALUE = FIELD_DEFS.declareLocalField(CoeusResource.DATA_SOURCEVALUE.getValue());

    /**
     * TotalAssetValue Field Id.
     */
    private static final MetisDataField FIELD_ASSETVALUE = FIELD_DEFS.declareLocalField(CoeusResource.DATA_ASSETVALUE.getValue());

    /**
     * TotalInvested Field Id.
     */
    private static final MetisDataField FIELD_INVESTED = FIELD_DEFS.declareLocalField(CoeusResource.DATA_INVESTED.getValue());

    /**
     * TotalHolding Field Id.
     */
    private static final MetisDataField FIELD_HOLDING = FIELD_DEFS.declareLocalField(CoeusResource.DATA_HOLDING.getValue());

    /**
     * TotalLoanBook Field Id.
     */
    private static final MetisDataField FIELD_LOANBOOK = FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOANBOOK.getValue());

    /**
     * TotalEarnings Field Id.
     */
    private static final MetisDataField FIELD_EARNINGS = FIELD_DEFS.declareLocalField(CoeusResource.DATA_EARNINGS.getValue());

    /**
     * TotalTaxableEarnings Field Id.
     */
    private static final MetisDataField FIELD_TAXEARNINGS = FIELD_DEFS.declareLocalField(CoeusResource.DATA_TAXABLEEARNINGS.getValue());

    /**
     * TotalInterest Field Id.
     */
    private static final MetisDataField FIELD_INTEREST = FIELD_DEFS.declareLocalField(CoeusResource.DATA_INTEREST.getValue());

    /**
     * TotalNettInterest Field Id.
     */
    private static final MetisDataField FIELD_NETTINTEREST = FIELD_DEFS.declareLocalField(CoeusResource.DATA_NETTINTEREST.getValue());

    /**
     * TotalBadDebtInterest Field Id.
     */
    private static final MetisDataField FIELD_BADDEBTINTEREST = FIELD_DEFS.declareLocalField(CoeusResource.DATA_BADDEBTINTEREST.getValue());

    /**
     * TotalBadDebtCapital Field Id.
     */
    private static final MetisDataField FIELD_BADDEBTCAPITAL = FIELD_DEFS.declareLocalField(CoeusResource.DATA_BADDEBTCAPITAL.getValue());

    /**
     * TotalFees Field Id.
     */
    private static final MetisDataField FIELD_FEES = FIELD_DEFS.declareLocalField(CoeusResource.DATA_FEES.getValue());

    /**
     * TotalCashBack Field Id.
     */
    private static final MetisDataField FIELD_CASHBACK = FIELD_DEFS.declareLocalField(CoeusResource.DATA_CASHBACK.getValue());

    /**
     * TotalLosses Field Id.
     */
    private static final MetisDataField FIELD_LOSSES = FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOSSES.getValue());

    /**
     * TotalBadDebt Field Id.
     */
    private static final MetisDataField FIELD_BADDEBT = FIELD_DEFS.declareLocalField(CoeusResource.DATA_BADDEBT.getValue());

    /**
     * TotalRecovered Field Id.
     */
    private static final MetisDataField FIELD_RECOVERED = FIELD_DEFS.declareLocalField(CoeusResource.DATA_RECOVERED.getValue());

    /**
     * Delta Field Id.
     */
    public static final MetisDataField FIELD_DELTA = FIELD_DEFS.declareCalculatedField(CoeusResource.DATA_DELTA.getValue());

    /**
     * TotalRecovered Field Id.
     */
    public static final MetisDataField FIELD_BALANCE = FIELD_DEFS.declareCalculatedField(CoeusResource.DATA_BALANCE.getValue());

    /**
     * The market.
     */
    private final CoeusMarket theMarket;

    /**
     * The loan.
     */
    private final CoeusLoan theLoan;

    /**
     * The date for the totals.
     */
    private final TethysDate theDate;

    /**
     * The underlying transaction.
     */
    private final CoeusTransaction theTransaction;

    /**
     * The previous Totals.
     */
    private final CoeusTotals thePrevious;

    /**
     * Constructor.
     * @param pMarket the market.
     * @param pLoan the loan.
     * @param pDate the end date for the totals
     */
    protected CoeusTotals(final CoeusMarket pMarket,
                          final CoeusLoan pLoan,
                          final TethysDate pDate) {
        theMarket = pMarket;
        theLoan = pLoan;
        theTransaction = null;
        theDate = pDate;
        thePrevious = null;
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
        theDate = pUnderlying.getDate();
        theTransaction = pUnderlying;
        thePrevious = pPrevious.theTransaction == null
                                                       ? null
                                                       : pPrevious;
    }

    @Override
    public Integer getIndexedId() {
        return theTransaction != null
                                      ? theTransaction.getIndexedId()
                                      : -1;
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
        return theDate;
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
     * Add totals to totals.
     * @param pTotals the totals to add
     */
    protected abstract void addTotalsToTotals(CoeusTotals pTotals);

    /**
     * Add transaction to totals.
     * @param pTransaction the transaction to add
     */
    protected abstract void addTransactionToTotals(CoeusTransaction pTransaction);

    /**
     * Reset the totals.
     */
    protected void resetTotals() {
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
    }

    /**
     * Obtain delta for field.
     * @param pField the field
     * @return the delta (or null)
     */
    public TethysDecimal getDeltaForField(final MetisDataField pField) {
        /* Obtain the field value */
        Object myValue = getFieldValue(pField);
        if (MetisDataFieldValue.SKIP.equals(myValue)) {
            myValue = getZero();
        }

        /* If we do not have Decimal value, return null */
        if (!(myValue instanceof TethysDecimal)) {
            return null;
        }
        final TethysDecimal myDecimal = (TethysDecimal) myValue;

        /* Obtain the previous field value */
        myValue = thePrevious == null
                                      ? null
                                      : thePrevious.getFieldValue(pField);
        if (MetisDataFieldValue.SKIP.equals(myValue)) {
            myValue = null;
        }

        /* If we do not have a preceding total */
        if (!(myValue instanceof TethysDecimal)) {
            /* Return non-zero value or null */
            return myDecimal.isNonZero()
                                         ? myDecimal
                                         : null;
        }

        /* Return null if there is no change */
        final TethysDecimal myPrevious = (TethysDecimal) myValue;
        if (myPrevious.equals(myDecimal)) {
            return null;
        }

        /* If this is a money value */
        if (myPrevious instanceof TethysMoney
            && myDecimal instanceof TethysMoney) {
            final TethysMoney myResult = new TethysMoney((TethysMoney) myDecimal);
            myResult.subtractAmount((TethysMoney) myPrevious);
            return myResult;
        }

        /* Handle standard result */
        final TethysDecimal myResult = new TethysDecimal(myDecimal);
        myResult.subtractValue(myPrevious);
        return myResult;
    }

    /**
     * Obtain balance field for TotalSet.
     * @param pTotalSet the totalSet
     * @return the balance field
     */
    protected static MetisDataField getBalanceField(final CoeusTotalSet pTotalSet) {
        switch (pTotalSet) {
            case INVESTED:
                return FIELD_INVESTED;
            case EARNINGS:
                return FIELD_EARNINGS;
            case TAXABLEEARNINGS:
                return FIELD_TAXEARNINGS;
            case INTEREST:
                return FIELD_INTEREST;
            case NETTINTEREST:
                return FIELD_NETTINTEREST;
            case FEES:
                return FIELD_FEES;
            case CASHBACK:
                return FIELD_CASHBACK;
            case BADDEBTCAPITAL:
                return FIELD_BADDEBTCAPITAL;
            case BADDEBTINTEREST:
                return FIELD_BADDEBTINTEREST;
            case LOSSES:
                return FIELD_LOSSES;
            case BADDEBT:
                return FIELD_BADDEBT;
            case RECOVERED:
                return FIELD_RECOVERED;
            case HOLDING:
                return FIELD_HOLDING;
            case LOANBOOK:
            default:
                return FIELD_LOANBOOK;
        }
    }

    /**
     * Obtain zero total.
     * @return zero total
     */
    protected abstract TethysDecimal getZero();

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    public static MetisDataFieldSet getBaseFieldSet() {
        return FIELD_DEFS;
    }

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
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_LOSSES, getLosses());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_BADDEBT, getBadDebt());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_RECOVERED, getRecovered());

        /* Add brackets around the values */
        myBuilder.insert(0, CoeusTransaction.CHAR_OPEN);
        myBuilder.append(CoeusTransaction.CHAR_CLOSE);

        /* Format the transaction type and date */
        if (theDate != null) {
            myBuilder.insert(0, theDate.toString());
            myBuilder.insert(0, CoeusTransaction.CHAR_BLANK);
        }
        myBuilder.insert(0, CoeusTransactionType.TOTALS.toString());

        /* Return the formatted string */
        return myBuilder.toString();
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle standard fields */
        if (FIELD_ID.equals(pField)) {
            final Integer myId = getIndexedId();
            return myId == -1
                              ? MetisDataFieldValue.SKIP
                              : myId;
        }
        if (FIELD_MARKET.equals(pField)) {
            return theMarket;
        }
        if (FIELD_LOAN.equals(pField)) {
            return CoeusMarket.skipNull(getLoan());
        }
        if (FIELD_DATE.equals(pField)) {
            return CoeusMarket.skipNull(theDate);
        }
        if (FIELD_DESC.equals(pField)) {
            return getDescription();
        }
        if (FIELD_TYPE.equals(pField)) {
            return theTransaction == null
                                          ? CoeusTransactionType.TOTALS
                                          : theTransaction.getTransType();
        }
        if (FIELD_TRANSACTION.equals(pField)) {
            return CoeusMarket.skipNull(theTransaction);
        }
        if (FIELD_ASSETVALUE.equals(pField)) {
            return CoeusMarket.skipZero(getAssetValue());
        }
        if (FIELD_HOLDING.equals(pField)) {
            return CoeusMarket.skipZero(getHolding());
        }
        if (FIELD_LOANBOOK.equals(pField)) {
            return CoeusMarket.skipZero(getLoanBook());
        }
        if (FIELD_SOURCEVALUE.equals(pField)) {
            return CoeusMarket.skipZero(getSourceValue());
        }
        if (FIELD_INVESTED.equals(pField)) {
            return CoeusMarket.skipZero(getInvested());
        }
        if (FIELD_EARNINGS.equals(pField)) {
            return CoeusMarket.skipZero(getEarnings());
        }
        if (FIELD_TAXEARNINGS.equals(pField)) {
            return CoeusMarket.skipZero(getTaxableEarnings());
        }
        if (FIELD_INTEREST.equals(pField)) {
            return CoeusMarket.skipZero(getInterest());
        }
        if (FIELD_NETTINTEREST.equals(pField)) {
            return CoeusMarket.skipZero(getNettInterest());
        }
        if (FIELD_BADDEBTINTEREST.equals(pField)) {
            return CoeusMarket.skipZero(getBadDebtInterest());
        }
        if (FIELD_BADDEBTCAPITAL.equals(pField)) {
            return CoeusMarket.skipZero(getBadDebtCapital());
        }
        if (FIELD_FEES.equals(pField)) {
            return CoeusMarket.skipZero(getFees());
        }
        if (FIELD_CASHBACK.equals(pField)) {
            return CoeusMarket.skipZero(getCashBack());
        }
        if (FIELD_LOSSES.equals(pField)) {
            return CoeusMarket.skipZero(getLosses());
        }
        if (FIELD_BADDEBT.equals(pField)) {
            return CoeusMarket.skipZero(getBadDebt());
        }
        if (FIELD_RECOVERED.equals(pField)) {
            return CoeusMarket.skipZero(getRecovered());
        }

        /* Not recognised */
        return MetisDataFieldValue.UNKNOWN;
    }
}
