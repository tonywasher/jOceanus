/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.newlist.MetisListItem.MetisIndexedItem;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Transaction Totals.
 */
public abstract class CoeusTotals
        implements MetisIndexedItem {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusTotals.class.getSimpleName());

    /**
     * ID Field Id.
     */
    private static final MetisField FIELD_ID = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_ID.getValue());

    /**
     * Market Field Id.
     */
    private static final MetisField FIELD_MARKET = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_MARKET.getValue());

    /**
     * Loan Field Id.
     */
    public static final MetisField FIELD_LOAN = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOAN.getValue());

    /**
     * Date Field Id.
     */
    public static final MetisField FIELD_DATE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_DATE.getValue());

    /**
     * Description Field Id.
     */
    public static final MetisField FIELD_DESC = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_DESC.getValue());

    /**
     * TransactionType Field Id.
     */
    public static final MetisField FIELD_TYPE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TRANSTYPE.getValue());

    /**
     * Transaction Field Id.
     */
    private static final MetisField FIELD_TRANSACTION = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TRANSACTION.getValue());

    /**
     * TotalSourceValue Field Id.
     */
    private static final MetisField FIELD_TOTAL_SOURCEVALUE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALSOURCEVALUE.getValue());

    /**
     * TotalAssetValue Field Id.
     */
    private static final MetisField FIELD_TOTAL_ASSETVALUE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALASSETVALUE.getValue());

    /**
     * TotalInvested Field Id.
     */
    private static final MetisField FIELD_TOTAL_INVESTED = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALINVESTED.getValue());

    /**
     * TotalHolding Field Id.
     */
    private static final MetisField FIELD_TOTAL_HOLDING = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALHOLDING.getValue());

    /**
     * TotalLoanBook Field Id.
     */
    private static final MetisField FIELD_TOTAL_LOANBOOK = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALLOANBOOK.getValue());

    /**
     * TotalEarnings Field Id.
     */
    private static final MetisField FIELD_TOTAL_EARNINGS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALEARNINGS.getValue());

    /**
     * TotalTaxableEarnings Field Id.
     */
    private static final MetisField FIELD_TOTAL_TAXEARNINGS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALTAXABLEEARNINGS.getValue());

    /**
     * TotalInterest Field Id.
     */
    private static final MetisField FIELD_TOTAL_INTEREST = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALINTEREST.getValue());

    /**
     * TotalBadDebtInterest Field Id.
     */
    private static final MetisField FIELD_TOTAL_BADDEBTINTEREST = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALBADDEBTINTEREST.getValue());

    /**
     * TotalBadDebtCapital Field Id.
     */
    private static final MetisField FIELD_TOTAL_BADDEBTCAPITAL = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALBADDEBTCAPITAL.getValue());

    /**
     * TotalFees Field Id.
     */
    private static final MetisField FIELD_TOTAL_FEES = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALFEES.getValue());

    /**
     * TotalCashBack Field Id.
     */
    private static final MetisField FIELD_TOTAL_CASHBACK = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALCASHBACK.getValue());

    /**
     * TotalLosses Field Id.
     */
    private static final MetisField FIELD_TOTAL_LOSSES = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALLOSSES.getValue());

    /**
     * TotalBadDebt Field Id.
     */
    private static final MetisField FIELD_TOTAL_BADDEBT = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALBADDEBT.getValue());

    /**
     * TotalRecovered Field Id.
     */
    private static final MetisField FIELD_TOTAL_RECOVERED = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TOTALRECOVERED.getValue());

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
    }

    /**
     * Constructor.
     * @param pUnderlying the underlying transaction.
     */
    protected CoeusTotals(final CoeusTransaction pUnderlying) {
        theMarket = pUnderlying.getMarket();
        theLoan = pUnderlying.getLoan();
        theDate = pUnderlying.getDate();
        theTransaction = pUnderlying;
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
     * Obtain the transactionType.
     * @return the transactionType
     */
    public CoeusTransactionType getTransType() {
        return theTransaction == null
                                      ? CoeusTransactionType.TOTALS
                                      : theTransaction.getTransType();
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
     * Obtain the loanBook.
     * @return the loanBook
     */
    public TethysDecimal getLoanBook() {
        return theTransaction == null
                                      ? null
                                      : theTransaction.getLoanBook();
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
     * Obtain the badDebtInterest.
     * @return the interest
     */
    public TethysDecimal getBadDebtInterest() {
        return theTransaction == null
                                      ? null
                                      : theTransaction.getBadDebtInterest();
    }

    /**
     * Obtain the badDebtCapital.
     * @return the capital
     */
    public TethysDecimal getBadDebtCapital() {
        return theTransaction == null
                                      ? null
                                      : theTransaction.getBadDebtInterest();
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
     * Obtain the recovered.
     * @return the recovered
     */
    public TethysDecimal getRecovered() {
        return theTransaction == null
                                      ? null
                                      : theTransaction.getRecovered();
    }

    /**
     * Obtain the total source value.
     * @return the value
     */
    public abstract TethysDecimal getTotalSourceValue();

    /**
     * Obtain the total asset value.
     * @return the value
     */
    public abstract TethysDecimal getTotalAssetValue();

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
     * Obtain the total loanBook.
     * @return the loanBook
     */
    public abstract TethysDecimal getTotalLoanBook();

    /**
     * Obtain the total earnings.
     * @return the earnings
     */
    public abstract TethysDecimal getTotalEarnings();

    /**
     * Obtain the total taxable interest.
     * @return the interest
     */
    public abstract TethysDecimal getTotalTaxableEarnings();

    /**
     * Obtain the total interest.
     * @return the interest
     */
    public abstract TethysDecimal getTotalInterest();

    /**
     * Obtain the total badDebt interest.
     * @return the interest
     */
    public abstract TethysDecimal getTotalBadDebtInterest();

    /**
     * Obtain the total badDebt capital.
     * @return the capital
     */
    public abstract TethysDecimal getTotalBadDebtCapital();

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
     * Obtain the total losses.
     * @return the losses
     */
    public abstract TethysDecimal getTotalLosses();

    /**
     * Obtain the total badDebt.
     * @return the badDebt
     */
    public abstract TethysDecimal getTotalBadDebt();

    /**
     * Obtain the total recovered.
     * @return the recovered
     */
    public abstract TethysDecimal getTotalRecovered();

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
        getTotalSourceValue().setZero();
        getTotalAssetValue().setZero();
        getTotalInvested().setZero();
        getTotalHolding().setZero();
        getTotalLoanBook().setZero();
        getTotalEarnings().setZero();
        getTotalTaxableEarnings().setZero();
        getTotalInterest().setZero();
        getTotalBadDebtInterest().setZero();
        getTotalBadDebtCapital().setZero();
        getTotalFees().setZero();
        getTotalCashBack().setZero();
        getTotalLosses().setZero();
        getTotalBadDebt().setZero();
        getTotalRecovered().setZero();
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    public static MetisFields getBaseFields() {
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
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_ASSETVALUE, getTotalAssetValue());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_HOLDING, getTotalHolding());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_LOANBOOK, getTotalLoanBook());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_SOURCEVALUE, getTotalSourceValue());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_INVESTED, getTotalInvested());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_EARNINGS, getTotalEarnings());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_INTEREST, getTotalInterest());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_TAXEARNINGS, getTotalTaxableEarnings());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_BDINTEREST, getTotalBadDebtInterest());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_BDCAPITAL, getTotalBadDebtCapital());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_FEES, getTotalFees());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_CASHBACK, getTotalCashBack());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_LOSSES, getTotalLosses());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_BADDEBT, getTotalBadDebt());
        CoeusTransaction.formatValue(myBuilder, CoeusTransaction.ID_RECOVERED, getTotalRecovered());

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
        if (FIELD_ID.equals(pField)) {
            Integer myId = getIndexedId();
            return myId == -1
                              ? MetisFieldValue.SKIP
                              : myId;
        }
        if (FIELD_MARKET.equals(pField)) {
            return theMarket;
        }
        if (FIELD_LOAN.equals(pField)) {
            CoeusLoan myLoan = getLoan();
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
        if (FIELD_TRANSACTION.equals(pField)) {
            return theTransaction == null
                                          ? MetisFieldValue.SKIP
                                          : theTransaction;
        }
        if (FIELD_TOTAL_ASSETVALUE.equals(pField)) {
            TethysDecimal myValue = getTotalAssetValue();
            return myValue.isZero()
                                    ? MetisFieldValue.SKIP
                                    : myValue;
        }
        if (FIELD_TOTAL_HOLDING.equals(pField)) {
            TethysDecimal myHolding = getTotalHolding();
            return myHolding.isZero()
                                      ? MetisFieldValue.SKIP
                                      : myHolding;
        }
        if (FIELD_TOTAL_LOANBOOK.equals(pField)) {
            TethysDecimal myLoanBook = getTotalLoanBook();
            return myLoanBook.isZero()
                                       ? MetisFieldValue.SKIP
                                       : myLoanBook;
        }
        if (FIELD_TOTAL_SOURCEVALUE.equals(pField)) {
            TethysDecimal myValue = getTotalSourceValue();
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
        if (FIELD_TOTAL_EARNINGS.equals(pField)) {
            TethysDecimal myEarnings = getTotalEarnings();
            return myEarnings.isZero()
                                       ? MetisFieldValue.SKIP
                                       : myEarnings;
        }
        if (FIELD_TOTAL_TAXEARNINGS.equals(pField)) {
            TethysDecimal myEarnings = getTotalTaxableEarnings();
            return myEarnings.isZero()
                                       ? MetisFieldValue.SKIP
                                       : myEarnings;
        }
        if (FIELD_TOTAL_INTEREST.equals(pField)) {
            TethysDecimal myInterest = getTotalInterest();
            return myInterest.isZero()
                                       ? MetisFieldValue.SKIP
                                       : myInterest;
        }
        if (FIELD_TOTAL_BADDEBTINTEREST.equals(pField)) {
            TethysDecimal myInterest = getTotalBadDebtInterest();
            return myInterest.isZero()
                                       ? MetisFieldValue.SKIP
                                       : myInterest;
        }
        if (FIELD_TOTAL_BADDEBTCAPITAL.equals(pField)) {
            TethysDecimal myCapital = getTotalBadDebtCapital();
            return myCapital.isZero()
                                      ? MetisFieldValue.SKIP
                                      : myCapital;
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
        if (FIELD_TOTAL_LOSSES.equals(pField)) {
            TethysDecimal myLosses = getTotalEarnings();
            return myLosses.isZero()
                                     ? MetisFieldValue.SKIP
                                     : myLosses;
        }
        if (FIELD_TOTAL_BADDEBT.equals(pField)) {
            TethysDecimal myBadDebt = getTotalBadDebt();
            return myBadDebt.isZero()
                                      ? MetisFieldValue.SKIP
                                      : myBadDebt;
        }
        if (FIELD_TOTAL_RECOVERED.equals(pField)) {
            TethysDecimal myRecovered = getTotalRecovered();
            return myRecovered.isZero()
                                        ? MetisFieldValue.SKIP
                                        : myRecovered;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }
}
