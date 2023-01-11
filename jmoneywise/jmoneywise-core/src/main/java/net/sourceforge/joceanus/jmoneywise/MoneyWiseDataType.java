/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSimpleId;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheFieldEnum;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusListKey;

/**
 * MoneyWise Item Types.
 */
public enum MoneyWiseDataType
        implements MetisLetheFieldEnum, MetisDataFieldId, PrometheusListKey {
    /**
     * DepositType.
     */
    DEPOSITTYPE(CryptographyDataType.MAXKEYID + 1),

    /**
     * CashType.
     */
    CASHTYPE(CryptographyDataType.MAXKEYID + 2),

    /**
     * LoanType.
     */
    LOANTYPE(CryptographyDataType.MAXKEYID + 3),

    /**
     * PortfolioType.
     */
    PORTFOLIOTYPE(CryptographyDataType.MAXKEYID + 4),

    /**
     * SecurityType.
     */
    SECURITYTYPE(CryptographyDataType.MAXKEYID + 5),

    /**
     * PayeeType.
     */
    PAYEETYPE(CryptographyDataType.MAXKEYID + 6),

    /**
     * TransactionType.
     */
    TRANSTYPE(CryptographyDataType.MAXKEYID + 7),

    /**
     * TaxBasis.
     */
    TAXBASIS(CryptographyDataType.MAXKEYID + 8),

    /**
     * Currency.
     */
    CURRENCY(CryptographyDataType.MAXKEYID + 9),

    /**
     * Frequency.
     */
    FREQUENCY(CryptographyDataType.MAXKEYID + 10),

    /**
     * AccountInfoType.
     */
    ACCOUNTINFOTYPE(CryptographyDataType.MAXKEYID + 11),

    /**
     * TransactionInfoType.
     */
    TRANSINFOTYPE(CryptographyDataType.MAXKEYID + 12),

    /**
     * TransactionTag.
     */
    TRANSTAG(CryptographyDataType.MAXKEYID + 13),

    /**
     * Region.
     */
    REGION(CryptographyDataType.MAXKEYID + 14),

    /**
     * DepositCategory.
     */
    DEPOSITCATEGORY(CryptographyDataType.MAXKEYID + 15),

    /**
     * CashCategory.
     */
    CASHCATEGORY(CryptographyDataType.MAXKEYID + 16),

    /**
     * LoanCategory.
     */
    LOANCATEGORY(CryptographyDataType.MAXKEYID + 17),

    /**
     * TransactionCategory.
     */
    TRANSCATEGORY(CryptographyDataType.MAXKEYID + 18),

    /**
     * ExchangeRate.
     */
    EXCHANGERATE(CryptographyDataType.MAXKEYID + 19),

    /**
     * Payee.
     */
    PAYEE(CryptographyDataType.MAXKEYID + 20),

    /**
     * PayeeInfo.
     */
    PAYEEINFO(CryptographyDataType.MAXKEYID + 21),

    /**
     * Securities.
     */
    SECURITY(CryptographyDataType.MAXKEYID + 22),

    /**
     * SecurityInfo.
     */
    SECURITYINFO(CryptographyDataType.MAXKEYID + 23),

    /**
     * SecurityPrice.
     */
    SECURITYPRICE(CryptographyDataType.MAXKEYID + 24),

    /**
     * Deposit.
     */
    DEPOSIT(CryptographyDataType.MAXKEYID + 25),

    /**
     * DepositInfo.
     */
    DEPOSITINFO(CryptographyDataType.MAXKEYID + 26),

    /**
     * DepositRate.
     */
    DEPOSITRATE(CryptographyDataType.MAXKEYID + 27),

    /**
     * Cash.
     */
    CASH(CryptographyDataType.MAXKEYID + 28),

    /**
     * CashInfo.
     */
    CASHINFO(CryptographyDataType.MAXKEYID + 29),

    /**
     * Loan.
     */
    LOAN(CryptographyDataType.MAXKEYID + 30),

    /**
     * LoanInfo.
     */
    LOANINFO(CryptographyDataType.MAXKEYID + 31),

    /**
     * Portfolio.
     */
    PORTFOLIO(CryptographyDataType.MAXKEYID + 32),

    /**
     * PortfolioInfo.
     */
    PORTFOLIOINFO(CryptographyDataType.MAXKEYID + 33),

    /**
     * Transaction.
     */
    TRANSACTION(CryptographyDataType.MAXKEYID + 34),

    /**
     * TransactionInfo.
     */
    TRANSACTIONINFO(CryptographyDataType.MAXKEYID + 35),

    /**
     * Schedule.
     */
    SCHEDULE(CryptographyDataType.MAXKEYID + 36);

    /**
     * The list key.
     */
    private final Integer theKey;

    /**
     * The String id.
     */
    private MetisDataFieldId theNameId;

    /**
     * The List id.
     */
    private MetisDataFieldId theListId;

    /**
     * Constructor.
     * @param pKey the keyId
     */
    MoneyWiseDataType(final Integer pKey) {
        theKey = pKey;
    }

    @Override
    public String toString() {
        /* return the name */
        return getItemId().getId();
    }

    /**
     * Obtain Id of item.
     * @return the item name
     */
    public MetisDataFieldId getItemId() {
        /* If we have not yet loaded the id */
        if (theNameId == null) {
            /* Load the id */
            theNameId = MetisFieldSimpleId.convertResource(MoneyWiseDataTypeResource.getKeyForDataType(this));
        }

        /* Return the name id */
        return theNameId;
    }

    /**
     * Obtain Name of item.
     * @return the item name
     */
    @Override
    public String getItemName() {
        return toString();
    }

    /**
     * Obtain Id of associated list.
     * @return the list name
     */
    public MetisDataFieldId getListId() {
        /* If we have not yet loaded the id */
        if (theListId == null) {
            /* Load the id */
            theListId = MetisFieldSimpleId.convertResource(MoneyWiseDataTypeResource.getKeyForDataList(this));
        }

        /* return the list id */
        return theListId;
    }

    @Override
    public String getListName() {
        return getFieldName();
    }

    @Override
    public String getFieldName() {
        return getListId().getId();
    }

    @Override
    public String getId() {
        return getItemName();
    }

    @Override
    public Integer getItemKey() {
        return theKey;
    }
}
