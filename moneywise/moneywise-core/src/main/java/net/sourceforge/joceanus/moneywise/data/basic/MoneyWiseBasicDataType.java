/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.basic;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.field.MetisFieldSimpleId;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.prometheus.data.PrometheusListKey;

/**
 * MoneyWise Item Types.
 */
public enum MoneyWiseBasicDataType
        implements PrometheusListKey, MetisDataFieldId {
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
     * @return the item name
     */
    public MetisDataFieldId getItemId() {
        /* If we have not yet loaded the id */
        if (theNameId == null) {
            /* Load the id */
            theNameId = MetisFieldSimpleId.convertResource(MoneyWiseBasicResource.getKeyForDataType(this));
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
            theListId = MetisFieldSimpleId.convertResource(MoneyWiseBasicResource.getKeyForDataList(this));
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
     * @return the field name
     */
    public String getFieldName() {
        return getListId().getId();
    }

    @Override
    public Class<? extends MetisFieldVersionedItem> getClazz() {
        switch (this) {
            case TRANSTAG:
                return MoneyWiseTransTag.class;
            case REGION:
                return MoneyWiseRegion.class;
            case DEPOSITCATEGORY:
                return MoneyWiseDepositCategory.class;
            case CASHCATEGORY:
                return MoneyWiseCashCategory.class;
            case LOANCATEGORY:
                return MoneyWiseLoanCategory.class;
            case TRANSCATEGORY:
                return MoneyWiseTransCategory.class;
            default:
                return null;
        }
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
