/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2014 Tony Washer
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

import net.sourceforge.joceanus.jcoeus.CoeusResource;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Transaction Totals.
 * @param <L> the loan type
 * @param <T> the transaction type
 * @param <S> the totals type
 * @param <H> the history type
 */
public abstract class CoeusTotals<L extends CoeusLoan<L, T, S, H>, T extends CoeusTransaction<L, T, S, H>, S extends CoeusTotals<L, T, S, H>, H extends CoeusHistory<L, T, S, H>>
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusTotals.class.getSimpleName());

    /**
     * Market Field Id.
     */
    private static final MetisField FIELD_MARKET = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_MARKET.getValue());

    /**
     * Loan Field Id.
     */
    private static final MetisField FIELD_LOAN = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOAN.getValue());

    /**
     * Date Field Id.
     */
    private static final MetisField FIELD_DATE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_DATE.getValue());

    /**
     * Description Field Id.
     */
    private static final MetisField FIELD_DESC = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_DESC.getValue());

    /**
     * TransactionType Field Id.
     */
    private static final MetisField FIELD_TYPE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TRANSTYPE.getValue());

    /**
     * Value Field Id.
     */
    private static final MetisField FIELD_VALUE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_VALUE.getValue());

    /**
     * Invested Field Id.
     */
    private static final MetisField FIELD_INVESTED = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_INVESTED.getValue());

    /**
     * Holding Field Id.
     */
    private static final MetisField FIELD_HOLDING = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_HOLDING.getValue());

    /**
     * Capital Field Id.
     */
    private static final MetisField FIELD_CAPITAL = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_CAPITAL.getValue());

    /**
     * Interest Field Id.
     */
    private static final MetisField FIELD_INTEREST = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_INTEREST.getValue());

    /**
     * Fees Field Id.
     */
    private static final MetisField FIELD_FEES = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_FEES.getValue());

    /**
     * CashBack Field Id.
     */
    private static final MetisField FIELD_CASHBACK = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_CASHBACK.getValue());

    /**
     * BadDebt Field Id.
     */
    private static final MetisField FIELD_BADDEBT = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_BADDEBT.getValue());

    /**
     * TotalValue Field Id.
     */
    private static final MetisField FIELD_TOTAL_VALUE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALVALUE.getValue());

    /**
     * TotalInvested Field Id.
     */
    private static final MetisField FIELD_TOTAL_INVESTED = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALINVESTED.getValue());

    /**
     * TotalHolding Field Id.
     */
    private static final MetisField FIELD_TOTAL_HOLDING = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALHOLDING.getValue());

    /**
     * TotalCapital Field Id.
     */
    private static final MetisField FIELD_TOTAL_CAPITAL = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALCAPITAL.getValue());

    /**
     * TotalInterest Field Id.
     */
    private static final MetisField FIELD_TOTAL_INTEREST = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALINTEREST.getValue());

    /**
     * TotalFees Field Id.
     */
    private static final MetisField FIELD_TOTAL_FEES = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALFEES.getValue());

    /**
     * TotalCashBack Field Id.
     */
    private static final MetisField FIELD_TOTAL_CASHBACK = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALCASHBACK.getValue());

    /**
     * TotalBadDebt Field Id.
     */
    private static final MetisField FIELD_TOTAL_BADDEBT = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALBADDEBT.getValue());

    /**
     * The market.
     */
    private final CoeusLoanMarket<L, T, S, H> theMarket;

    /**
     * The loan.
     */
    private final L theLoan;

    /**
     * The date for the totals.
     */
    private final TethysDate theDate;

    /**
     * The underlying transaction.
     */
    private final T theTransaction;

    /**
     * Constructor.
     * @param pMarket the market.
     * @param pLoan the loan.
     * @param pDate the end date for the totals
     */
    protected CoeusTotals(final CoeusLoanMarket<L, T, S, H> pMarket,
                          final L pLoan,
                          final TethysDate pDate) {
        theMarket = pMarket;
        theLoan = pLoan;
        theTransaction = null;
        theDate = pDate;
    }

    /**
     * Constructor.
     * @param pUnderlying the underlying transaction.
     */
    protected CoeusTotals(final T pUnderlying) {
        theMarket = pUnderlying.getMarket();
        theLoan = pUnderlying.getLoan();
        theDate = pUnderlying.getDate();
        theTransaction = pUnderlying;
    }

    /**
     * Obtain the market.
     * @return the market
     */
    public CoeusLoanMarket<L, T, S, H> getMarket() {
        return theMarket;
    }

    /**
     * Obtain the loan.
     * @return the loan
     */
    public L getLoan() {
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
     * Obtain the transactionType.
     * @return the transactionType
     */
    public CoeusTransactionType getTransType() {
        return theTransaction == null
                                      ? CoeusTransactionType.TOTALS
                                      : theTransaction.getTransType();
    }

    /**
     * Obtain the value.
     * @return the value
     */
    public TethysDecimal getValue() {
        return theTransaction == null
                                      ? null
                                      : theTransaction.getValue();
    }

    /**
     * Obtain the invested.
     * @return the invested
     */
    public TethysDecimal getInvested() {
        return theTransaction == null
                                      ? null
                                      : theTransaction.getInvested();
    }

    /**
     * Obtain the holding.
     * @return the holding
     */
    public TethysDecimal getHolding() {
        return theTransaction == null
                                      ? null
                                      : theTransaction.getHolding();
    }

    /**
     * Obtain the capital.
     * @return the capital
     */
    public TethysDecimal getCapital() {
        return theTransaction == null
                                      ? null
                                      : theTransaction.getCapital();
    }

    /**
     * Obtain the interest.
     * @return the interest
     */
    public TethysDecimal getInterest() {
        return theTransaction == null
                                      ? null
                                      : theTransaction.getInterest();
    }

    /**
     * Obtain the fees.
     * @return the fees
     */
    public TethysDecimal getFees() {
        return theTransaction == null
                                      ? null
                                      : theTransaction.getFees();
    }

    /**
     * Obtain the cashBack.
     * @return the cashBack
     */
    public TethysDecimal getCashBack() {
        return theTransaction == null
                                      ? null
                                      : theTransaction.getCashBack();
    }

    /**
     * Obtain the badDebt.
     * @return the badDebt
     */
    public TethysDecimal getBadDebt() {
        return theTransaction == null
                                      ? null
                                      : theTransaction.getBadDebt();
    }

    /**
     * Obtain the total value.
     * @return the value
     */
    public abstract TethysDecimal getTotalValue();

    /**
     * Obtain the total invested.
     * @return the invested
     */
    public abstract TethysDecimal getTotalInvested();

    /**
     * Obtain the total holding.
     * @return the holding
     */
    public abstract TethysDecimal getTotalHolding();

    /**
     * Obtain the total capital.
     * @return the capital
     */
    public abstract TethysDecimal getTotalCapital();

    /**
     * Obtain the total interest.
     * @return the interest
     */
    public abstract TethysDecimal getTotalInterest();

    /**
     * Obtain the total fees.
     * @return the fees
     */
    public abstract TethysDecimal getTotalFees();

    /**
     * Obtain the total cashBack.
     * @return the cashBack
     */
    public abstract TethysDecimal getTotalCashBack();

    /**
     * Obtain the total badDebt.
     * @return the badDebt
     */
    public abstract TethysDecimal getTotalBadDebt();

    /**
     * Add totals to totals.
     * @param pTotals the totals to add
     */
    protected abstract void addTotalsToTotals(final S pTotals);

    /**
     * Add transaction to totals.
     * @param pTransaction the transaction to add
     */
    protected abstract void addTransactionToTotals(final T pTransaction);

    /**
     * Reset the totals.
     */
    protected void resetTotals() {
        getTotalValue().setZero();
        getTotalInvested().setZero();
        getTotalHolding().setZero();
        getTotalCapital().setZero();
        getTotalInterest().setZero();
        getTotalFees().setZero();
        getTotalCashBack().setZero();
        getTotalBadDebt().setZero();
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisFields getBaseFields() {
        return FIELD_DEFS;
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public String formatObject() {
        /* Create builder and access formatter */
        StringBuilder myBuilder = new StringBuilder();

        /* Add the values */
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_VALUE, getTotalValue());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_HOLDING, getTotalHolding());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_CAPITAL, getTotalCapital());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_INTEREST, getTotalInterest());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_FEES, getTotalFees());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_CASHBACK, getTotalCashBack());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_BADDEBT, getTotalBadDebt());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_INVESTED, getTotalInvested());

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
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_MARKET.equals(pField)) {
            return theMarket;
        }
        if (FIELD_LOAN.equals(pField)) {
            L myLoan = getLoan();
            return myLoan == null
                                  ? MetisFieldValue.SKIP
                                  : myLoan;
        }
        if (FIELD_DATE.equals(pField)) {
            return theDate == null
                                   ? MetisFieldValue.SKIP
                                   : theDate;
        }
        if (FIELD_DESC.equals(pField)) {
            return getDescription();
        }
        if (FIELD_TYPE.equals(pField)) {
            return getTransType();
        }
        if (FIELD_VALUE.equals(pField)) {
            return theTransaction == null
                                          ? MetisFieldValue.SKIP
                                          : theTransaction.getFieldValue(CoeusTransaction.FIELD_VALUE);
        }
        if (FIELD_INVESTED.equals(pField)) {
            return theTransaction == null
                                          ? MetisFieldValue.SKIP
                                          : theTransaction.getFieldValue(CoeusTransaction.FIELD_INVESTED);
        }
        if (FIELD_HOLDING.equals(pField)) {
            return theTransaction == null
                                          ? MetisFieldValue.SKIP
                                          : theTransaction.getFieldValue(CoeusTransaction.FIELD_HOLDING);
        }
        if (FIELD_CAPITAL.equals(pField)) {
            return theTransaction == null
                                          ? MetisFieldValue.SKIP
                                          : theTransaction.getFieldValue(CoeusTransaction.FIELD_CAPITAL);
        }
        if (FIELD_INTEREST.equals(pField)) {
            return theTransaction == null
                                          ? MetisFieldValue.SKIP
                                          : theTransaction.getFieldValue(CoeusTransaction.FIELD_INTEREST);
        }
        if (FIELD_FEES.equals(pField)) {
            return theTransaction == null
                                          ? MetisFieldValue.SKIP
                                          : theTransaction.getFieldValue(CoeusTransaction.FIELD_FEES);
        }
        if (FIELD_CASHBACK.equals(pField)) {
            return theTransaction == null
                                          ? MetisFieldValue.SKIP
                                          : theTransaction.getFieldValue(CoeusTransaction.FIELD_CASHBACK);
        }
        if (FIELD_BADDEBT.equals(pField)) {
            return theTransaction == null
                                          ? MetisFieldValue.SKIP
                                          : theTransaction.getFieldValue(CoeusTransaction.FIELD_BADDEBT);
        }
        if (FIELD_TOTAL_VALUE.equals(pField)) {
            TethysDecimal myValue = getTotalValue();
            return myValue.isZero()
                                    ? MetisFieldValue.SKIP
                                    : myValue;
        }
        if (FIELD_TOTAL_INVESTED.equals(pField)) {
            TethysDecimal myInvested = getTotalInvested();
            return myInvested.isZero()
                                       ? MetisFieldValue.SKIP
                                       : myInvested;
        }
        if (FIELD_TOTAL_HOLDING.equals(pField)) {
            TethysDecimal myHolding = getTotalHolding();
            return myHolding.isZero()
                                      ? MetisFieldValue.SKIP
                                      : myHolding;
        }
        if (FIELD_TOTAL_CAPITAL.equals(pField)) {
            TethysDecimal myCapital = getTotalCapital();
            return myCapital.isZero()
                                      ? MetisFieldValue.SKIP
                                      : myCapital;
        }
        if (FIELD_TOTAL_INTEREST.equals(pField)) {
            TethysDecimal myInterest = getTotalInterest();
            return myInterest.isZero()
                                       ? MetisFieldValue.SKIP
                                       : myInterest;
        }
        if (FIELD_TOTAL_FEES.equals(pField)) {
            TethysDecimal myFees = getTotalFees();
            return myFees.isZero()
                                   ? MetisFieldValue.SKIP
                                   : myFees;
        }
        if (FIELD_TOTAL_CASHBACK.equals(pField)) {
            TethysDecimal myCashBack = getTotalCashBack();
            return myCashBack.isZero()
                                       ? MetisFieldValue.SKIP
                                       : myCashBack;
        }
        if (FIELD_TOTAL_BADDEBT.equals(pField)) {
            TethysDecimal myBadDebt = getTotalBadDebt();
            return myBadDebt.isZero()
                                      ? MetisFieldValue.SKIP
                                      : myBadDebt;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }
}
