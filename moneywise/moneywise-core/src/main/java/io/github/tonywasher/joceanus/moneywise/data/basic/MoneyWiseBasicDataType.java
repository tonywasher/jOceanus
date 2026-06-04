/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.data.basic;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSimpleId;
import io.github.tonywasher.joceanus.metis.list.MetisListKey;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;

/**
 * MoneyWise Item Types.
 */
public enum MoneyWiseBasicDataType
        implements MetisListKey, MetisDataFieldId {
    /**
     * TransactionTag.
     */
    TRANSTAG(MoneyWiseStaticDataType.MAXKEYID + 1),

    /**
     * Region.
     */
    REGION(MoneyWiseStaticDataType.MAXKEYID + 2),

    /**
     * DepositCategory.
     */
    DEPOSITCATEGORY(MoneyWiseStaticDataType.MAXKEYID + 3),

    /**
     * CashCategory.
     */
    CASHCATEGORY(MoneyWiseStaticDataType.MAXKEYID + 4),

    /**
     * LoanCategory.
     */
    LOANCATEGORY(MoneyWiseStaticDataType.MAXKEYID + 5),

    /**
     * TransactionCategory.
     */
    TRANSCATEGORY(MoneyWiseStaticDataType.MAXKEYID + 6),

    /**
     * ExchangeRate.
     */
    EXCHANGERATE(MoneyWiseStaticDataType.MAXKEYID + 7),

    /**
     * Payee.
     */
    PAYEE(MoneyWiseStaticDataType.MAXKEYID + 8),

    /**
     * PayeeInfo.
     */
    PAYEEINFO(MoneyWiseStaticDataType.MAXKEYID + 9),

    /**
     * Securities.
     */
    SECURITY(MoneyWiseStaticDataType.MAXKEYID + 10),

    /**
     * SecurityInfo.
     */
    SECURITYINFO(MoneyWiseStaticDataType.MAXKEYID + 11),

    /**
     * SecurityPrice.
     */
    SECURITYPRICE(MoneyWiseStaticDataType.MAXKEYID + 12),

    /**
     * Deposit.
     */
    DEPOSIT(MoneyWiseStaticDataType.MAXKEYID + 13),

    /**
     * DepositInfo.
     */
    DEPOSITINFO(MoneyWiseStaticDataType.MAXKEYID + 14),

    /**
     * DepositRate.
     */
    DEPOSITRATE(MoneyWiseStaticDataType.MAXKEYID + 15),

    /**
     * Cash.
     */
    CASH(MoneyWiseStaticDataType.MAXKEYID + 16),

    /**
     * CashInfo.
     */
    CASHINFO(MoneyWiseStaticDataType.MAXKEYID + 17),

    /**
     * Loan.
     */
    LOAN(MoneyWiseStaticDataType.MAXKEYID + 18),

    /**
     * LoanInfo.
     */
    LOANINFO(MoneyWiseStaticDataType.MAXKEYID + 19),

    /**
     * Portfolio.
     */
    PORTFOLIO(MoneyWiseStaticDataType.MAXKEYID + 20),

    /**
     * PortfolioInfo.
     */
    PORTFOLIOINFO(MoneyWiseStaticDataType.MAXKEYID + 21),

    /**
     * Transaction.
     */
    TRANSACTION(MoneyWiseStaticDataType.MAXKEYID + 22),

    /**
     * TransactionInfo.
     */
    TRANSACTIONINFO(MoneyWiseStaticDataType.MAXKEYID + 23);

    /**
     * Maximum keyId.
     */
    public static final Integer MAXKEYID = TRANSACTIONINFO.getItemKey();

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
     *
     * @param pKey the keyId
     */
    MoneyWiseBasicDataType(final Integer pKey) {
        theKey = pKey;
    }

    @Override
    public String toString() {
        /* return the name */
        return getItemId().getId();
    }

    /**
     * Obtain Id of item.
     *
     * @return the item name
     */
    public MetisDataFieldId getItemId() {
        /* If we have not yet loaded the id */
        if (theNameId == null) {
            /* Load the id */
            theNameId = MetisFieldSimpleId.convertResource(bundleIdForDataType(this));
        }

        /* Return the name id */
        return theNameId;
    }

    /**
     * Obtain Name of item.
     *
     * @return the item name
     */
    @Override
    public String getItemName() {
        return toString();
    }

    /**
     * Obtain Id of associated list.
     *
     * @return the list name
     */
    public MetisDataFieldId getListId() {
        /* If we have not yet loaded the id */
        if (theListId == null) {
            /* Load the id */
            theListId = MetisFieldSimpleId.convertResource(bundleIdForDataList(this));
        }

        /* return the list id */
        return theListId;
    }

    @Override
    public String getListName() {
        return getFieldName();
    }

    /**
     * Obtain field name.
     *
     * @return the field name
     */
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

    /**
     * Obtain the resource bundleId for the dataType.
     *
     * @param pType the dataType
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForDataType(final MoneyWiseBasicDataType pType) {
        /* Create the map and return it */
        return switch (pType) {
            case DEPOSITCATEGORY -> MoneyWiseBasicResource.DEPOSITCAT_NAME;
            case CASHCATEGORY -> MoneyWiseBasicResource.CASHCAT_NAME;
            case LOANCATEGORY -> MoneyWiseBasicResource.LOANCAT_NAME;
            case TRANSCATEGORY -> MoneyWiseBasicResource.TRANSCAT_NAME;
            case EXCHANGERATE -> MoneyWiseBasicResource.XCHGRATE_NAME;
            case TRANSTAG -> MoneyWiseBasicResource.TRANSTAG_NAME;
            case REGION -> MoneyWiseBasicResource.REGION_NAME;
            case PAYEE -> MoneyWiseBasicResource.PAYEE_NAME;
            case PAYEEINFO -> MoneyWiseBasicResource.PAYEEINFO_NAME;
            case SECURITY -> MoneyWiseBasicResource.SECURITY_NAME;
            case SECURITYPRICE -> MoneyWiseBasicResource.SECURITYPRICE_NAME;
            case SECURITYINFO -> MoneyWiseBasicResource.SECURITYINFO_NAME;
            case DEPOSIT -> MoneyWiseBasicResource.DEPOSIT_NAME;
            case DEPOSITRATE -> MoneyWiseBasicResource.DEPOSITRATE_NAME;
            case DEPOSITINFO -> MoneyWiseBasicResource.DEPOSITINFO_NAME;
            case CASH -> MoneyWiseBasicResource.CASH_NAME;
            case CASHINFO -> MoneyWiseBasicResource.CASHINFO_NAME;
            case LOAN -> MoneyWiseBasicResource.LOAN_NAME;
            case LOANINFO -> MoneyWiseBasicResource.LOANINFO_NAME;
            case PORTFOLIO -> MoneyWiseBasicResource.PORTFOLIO_NAME;
            case PORTFOLIOINFO -> MoneyWiseBasicResource.PORTFOLIOINFO_NAME;
            case TRANSACTION -> MoneyWiseBasicResource.TRANSACTION_NAME;
            case TRANSACTIONINFO -> MoneyWiseBasicResource.TRANSINFO_NAME;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain the resource bundleId for the dataType List.
     *
     * @param pType the dataType
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForDataList(final MoneyWiseBasicDataType pType) {
        /* Create the map and return it */
        return switch (pType) {
            case DEPOSITCATEGORY -> MoneyWiseBasicResource.DEPOSITCAT_LIST;
            case CASHCATEGORY -> MoneyWiseBasicResource.CASHCAT_LIST;
            case LOANCATEGORY -> MoneyWiseBasicResource.LOANCAT_LIST;
            case TRANSCATEGORY -> MoneyWiseBasicResource.TRANSCAT_LIST;
            case EXCHANGERATE -> MoneyWiseBasicResource.XCHGRATE_LIST;
            case TRANSTAG -> MoneyWiseBasicResource.TRANSTAG_LIST;
            case REGION -> MoneyWiseBasicResource.REGION_LIST;
            case PAYEE -> MoneyWiseBasicResource.PAYEE_LIST;
            case PAYEEINFO -> MoneyWiseBasicResource.PAYEEINFO_LIST;
            case SECURITY -> MoneyWiseBasicResource.SECURITY_LIST;
            case SECURITYPRICE -> MoneyWiseBasicResource.SECURITYPRICE_LIST;
            case SECURITYINFO -> MoneyWiseBasicResource.SECURITYINFO_LIST;
            case DEPOSIT -> MoneyWiseBasicResource.DEPOSIT_LIST;
            case DEPOSITRATE -> MoneyWiseBasicResource.DEPOSITRATE_LIST;
            case DEPOSITINFO -> MoneyWiseBasicResource.DEPOSITINFO_LIST;
            case CASH -> MoneyWiseBasicResource.CASH_LIST;
            case CASHINFO -> MoneyWiseBasicResource.CASHINFO_LIST;
            case LOAN -> MoneyWiseBasicResource.LOAN_LIST;
            case LOANINFO -> MoneyWiseBasicResource.LOANINFO_LIST;
            case PORTFOLIO -> MoneyWiseBasicResource.PORTFOLIO_LIST;
            case PORTFOLIOINFO -> MoneyWiseBasicResource.PORTFOLIOINFO_LIST;
            case TRANSACTION -> MoneyWiseBasicResource.TRANSACTION_LIST;
            case TRANSACTIONINFO -> MoneyWiseBasicResource.TRANSINFO_LIST;
            default -> throw new IllegalArgumentException();
        };
    }
}
