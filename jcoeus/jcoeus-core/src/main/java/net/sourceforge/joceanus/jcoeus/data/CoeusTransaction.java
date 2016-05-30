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
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Coeus Transaction.
 * @param <L> the loan type
 * @param <T> the transaction type
 * @param <S> the totals type
 * @param <H> the history type
 */
public abstract class CoeusTransaction<L extends CoeusLoan<L, T, S, H>, T extends CoeusTransaction<L, T, S, H>, S extends CoeusTotals<L, T, S, H>, H extends CoeusHistory<L, T, S, H>>
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusTransaction.class.getSimpleName());

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
     * Invested Field Id.
     */
    protected static final MetisField FIELD_INVESTED = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_INVESTED.getValue());

    /**
     * Holding Field Id.
     */
    protected static final MetisField FIELD_HOLDING = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_HOLDING.getValue());

    /**
     * Capital Field Id.
     */
    protected static final MetisField FIELD_CAPITAL = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_CAPITAL.getValue());

    /**
     * Interest Field Id.
     */
    protected static final MetisField FIELD_INTEREST = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_INTEREST.getValue());

    /**
     * Fees Field Id.
     */
    protected static final MetisField FIELD_FEES = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_FEES.getValue());

    /**
     * CashBack Field Id.
     */
    protected static final MetisField FIELD_CASHBACK = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_CASHBACK.getValue());

    /**
     * BadDebt Field Id.
     */
    protected static final MetisField FIELD_BADDEBT = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_BADDEBT.getValue());

    /**
     * Blank Character.
     */
    protected static final char CHAR_BLANK = ' ';

    /**
     * Open Character.
     */
    protected static final char CHAR_OPEN = '(';

    /**
     * Close Character.
     */
    protected static final char CHAR_CLOSE = ')';

    /**
     * Id For holding.
     */
    protected static final String ID_HOLDING = "H";

    /**
     * Id For capital.
     */
    protected static final String ID_CAPITAL = "C";

    /**
     * Id For interest.
     */
    protected static final String ID_INTEREST = "I";

    /**
     * Id For fees.
     */
    protected static final String ID_FEES = "F";

    /**
     * Id For invested.
     */
    protected static final String ID_INVESTED = "D";

    /**
     * Id For cashBack.
     */
    protected static final String ID_CASHBACK = "CB";

    /**
     * Id For basDebt.
     */
    protected static final String ID_BADDEBT = "BD";

    /**
     * The market.
     */
    private final CoeusLoanMarket<L, T, S, H> theMarket;

    /**
     * Constructor.
     * @param pMarket the loanMarket
     */
    protected CoeusTransaction(final CoeusLoanMarket<L, T, S, H> pMarket) {
        /* Store parameters */
        theMarket = pMarket;
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
    public abstract L getLoan();

    /**
     * Obtain the date.
     * @return the date
     */
    public abstract TethysDate getDate();

    /**
     * Obtain the Description.
     * @return the description
     */
    public abstract String getDescription();

    /**
     * Obtain the transactionType.
     * @return the transactionType
     */
    public abstract CoeusTransactionType getTransType();

    /**
     * Obtain the loanId.
     * @return the loanId
     */
    public abstract String getLoanId();

    /**
     * Obtain the invested.
     * @return the invested
     */
    public abstract TethysDecimal getInvested();

    /**
     * Obtain the holding.
     * @return the holding
     */
    public abstract TethysDecimal getHolding();

    /**
     * Obtain the capital.
     * @return the capital
     */
    public abstract TethysDecimal getCapital();

    /**
     * Obtain the interest.
     * @return the interest
     */
    public abstract TethysDecimal getInterest();

    /**
     * Obtain the fees.
     * @return the fees
     */
    public abstract TethysDecimal getFees();

    /**
     * Obtain the cashBack.
     * @return the cashBack
     */
    public abstract TethysDecimal getCashBack();

    /**
     * Obtain the badDebt.
     * @return the badDebt
     */
    public abstract TethysDecimal getBadDebt();

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
        MetisDataFormatter myFormatter = theMarket.getFormatter();

        /* Add the values */
        formatValue(myBuilder, ID_HOLDING, getHolding());
        formatValue(myBuilder, ID_CAPITAL, getCapital());
        formatValue(myBuilder, ID_INTEREST, getInterest());
        formatValue(myBuilder, ID_FEES, getFees());
        formatValue(myBuilder, ID_CASHBACK, getCashBack());
        formatValue(myBuilder, ID_BADDEBT, getBadDebt());
        formatValue(myBuilder, ID_INVESTED, getInvested());

        /* Add brackets around the values */
        myBuilder.insert(0, CHAR_OPEN);
        myBuilder.append(CHAR_CLOSE);

        /* Format the transaction type and date */
        myBuilder.insert(0, myFormatter.formatObject(getDate()));
        myBuilder.insert(0, CHAR_BLANK);
        myBuilder.insert(0, getTransType().toString());

        /* Return the formatted string */
        return myBuilder.toString();
    }

    /**
     * Format optional value.
     * @param pBuilder the builder
     * @param pPrefix the prefix
     * @param pValue the value
     */
    protected static void formatValue(final StringBuilder pBuilder,
                                      final String pPrefix,
                                      final TethysDecimal pValue) {
        /* If the value is non-zero */
        if (pValue.isNonZero()) {
            /* If we are not the first value */
            if (pBuilder.length() > 0) {
                /* Add a comma */
                pBuilder.append(',');
            }

            /* Add it */
            pBuilder.append(pPrefix);
            pBuilder.append('=');
            pBuilder.append(pValue.toString());
        }
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
            return getDate();
        }
        if (FIELD_DESC.equals(pField)) {
            return getDescription();
        }
        if (FIELD_TYPE.equals(pField)) {
            return getTransType();
        }
        if (FIELD_INVESTED.equals(pField)) {
            TethysDecimal myInvested = getInvested();
            return myInvested.isZero()
                                       ? MetisFieldValue.SKIP
                                       : myInvested;
        }
        if (FIELD_HOLDING.equals(pField)) {
            TethysDecimal myHolding = getHolding();
            return myHolding.isZero()
                                      ? MetisFieldValue.SKIP
                                      : myHolding;
        }
        if (FIELD_CAPITAL.equals(pField)) {
            TethysDecimal myCapital = getCapital();
            return myCapital.isZero()
                                      ? MetisFieldValue.SKIP
                                      : myCapital;
        }
        if (FIELD_INTEREST.equals(pField)) {
            TethysDecimal myInterest = getInterest();
            return myInterest.isZero()
                                       ? MetisFieldValue.SKIP
                                       : myInterest;
        }
        if (FIELD_FEES.equals(pField)) {
            TethysDecimal myFees = getFees();
            return myFees.isZero()
                                   ? MetisFieldValue.SKIP
                                   : myFees;
        }
        if (FIELD_CASHBACK.equals(pField)) {
            TethysDecimal myCashBack = getCashBack();
            return myCashBack.isZero()
                                       ? MetisFieldValue.SKIP
                                       : myCashBack;
        }
        if (FIELD_BADDEBT.equals(pField)) {
            TethysDecimal myBadDebt = getBadDebt();
            return myBadDebt.isZero()
                                      ? MetisFieldValue.SKIP
                                      : myBadDebt;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }
}
