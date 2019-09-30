/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2019 Tony Washer
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

/**
 * Coeus Transaction.
 */
public abstract class CoeusTransaction
        implements MetisFieldTableItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusTransaction> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusTransaction.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_ID, CoeusTransaction::getIndexedId);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_MARKET, CoeusTransaction::getMarket);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOAN, CoeusTransaction::getLoan);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_DATE, CoeusTransaction::getDate);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_DESC, CoeusTransaction::getDescription);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_TRANSTYPE, CoeusTransaction::getTransType);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_INVESTED, CoeusTransaction::getInvested);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_HOLDING, CoeusTransaction::getHolding);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOANBOOK, CoeusTransaction::getLoanBook);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_INTEREST, CoeusTransaction::getInterest);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_BADDEBTINTEREST, CoeusTransaction::getBadDebtInterest);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_BADDEBTCAPITAL, CoeusTransaction::getBadDebtCapital);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_FEES, CoeusTransaction::getFees);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_CASHBACK, CoeusTransaction::getCashBack);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_BADDEBT, CoeusTransaction::getBadDebt);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_RECOVERED, CoeusTransaction::getRecovered);
    }

    /**
     * Blank Character.
     */
    static final char CHAR_BLANK = ' ';

    /**
     * Open Character.
     */
    static final char CHAR_OPEN = '(';

    /**
     * Close Character.
     */
    static final char CHAR_CLOSE = ')';

    /**
     * Id For source value.
     */
    static final String ID_SOURCEVALUE = "SV";

    /**
     * Id For asset value.
     */
    static final String ID_ASSETVALUE = "AV";

    /**
     * Id For holding.
     */
    static final String ID_HOLDING = "H";

    /**
     * Id For loanBook.
     */
    static final String ID_LOANBOOK = "LB";

    /**
     * Id For earnings.
     */
    static final String ID_EARNINGS = "E";

    /**
     * Id For taxInterest.
     */
    static final String ID_TAXEARNINGS = "TE";

    /**
     * Id For interest.
     */
    static final String ID_INTEREST = "I";

    /**
     * Id For nettInterest.
     */
    static final String ID_NETTINTEREST = "NI";

    /**
     * Id For badDebtInterest.
     */
    static final String ID_BDINTEREST = "DI";

    /**
     * Id For badDebtCapital.
     */
    static final String ID_BDCAPITAL = "DC";

    /**
     * Id For fees.
     */
    static final String ID_FEES = "F";

    /**
     * Id For invested.
     */
    static final String ID_INVESTED = "D";

    /**
     * Id For cashBack.
     */
    static final String ID_CASHBACK = "CB";

    /**
     * Id For losses.
     */
    static final String ID_LOSSES = "L";

    /**
     * Id For badDebt.
     */
    static final String ID_BADDEBT = "BD";

    /**
     * Id For recovered.
     */
    static final String ID_RECOVERED = "R";

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The market.
     */
    private final CoeusMarket theMarket;

    /**
     * Constructor.
     * @param pMarket the loanMarket
     */
    protected CoeusTransaction(final CoeusMarket pMarket) {
        /* Store parameters */
        theMarket = pMarket;
        theId = pMarket.getNextTransactionId();
    }

    @Override
    public Integer getIndexedId() {
        return theId;
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
    public abstract CoeusLoan getLoan();

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
     * Obtain the loanBook.
     * @return the loanBook
     */
    public abstract TethysDecimal getLoanBook();

    /**
     * Obtain the interest.
     * @return the interest
     */
    public abstract TethysDecimal getInterest();

    /**
     * Obtain the badDebtInterest.
     * @return the interest
     */
    public abstract TethysDecimal getBadDebtInterest();

    /**
     * Obtain the badDebtCapital.
     * @return the interest
     */
    public abstract TethysDecimal getBadDebtCapital();

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
     * Obtain the recovered.
     * @return the recovered
     */
    public abstract TethysDecimal getRecovered();

    @Override
    public String toString() {
        /* Create builder and access formatter */
        final StringBuilder myBuilder = new StringBuilder();

        /* Add the values */
        formatValue(myBuilder, ID_INVESTED, getInvested());
        formatValue(myBuilder, ID_HOLDING, getHolding());
        formatValue(myBuilder, ID_LOANBOOK, getLoanBook());
        formatValue(myBuilder, ID_INTEREST, getInterest());
        formatValue(myBuilder, ID_BDINTEREST, getBadDebtInterest());
        formatValue(myBuilder, ID_BDCAPITAL, getBadDebtCapital());
        formatValue(myBuilder, ID_FEES, getFees());
        formatValue(myBuilder, ID_CASHBACK, getCashBack());
        formatValue(myBuilder, ID_BADDEBT, getBadDebt());
        formatValue(myBuilder, ID_RECOVERED, getRecovered());

        /* Add brackets around the values */
        myBuilder.insert(0, CHAR_OPEN);
        myBuilder.append(CHAR_CLOSE);

        /* Format the transaction type and date */
        myBuilder.insert(0, getDate().toString());
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
    static void formatValue(final StringBuilder pBuilder,
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
}
