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
package io.github.tonywasher.joceanus.moneywise.data.statics;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSimpleId;
import io.github.tonywasher.joceanus.metis.field.MetisFieldVersionedItem;
import io.github.tonywasher.joceanus.metis.list.MetisListKey;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet.PrometheusCryptographyDataType;

/**
 * MoneyWise Item Types.
 */
public enum MoneyWiseStaticDataType
        implements MetisListKey, MetisDataFieldId {
    /**
     * DepositType.
     */
    DEPOSITTYPE(PrometheusCryptographyDataType.MAXKEYID + 1),

    /**
     * CashType.
     */
    CASHTYPE(PrometheusCryptographyDataType.MAXKEYID + 2),

    /**
     * LoanType.
     */
    LOANTYPE(PrometheusCryptographyDataType.MAXKEYID + 3),

    /**
     * PortfolioType.
     */
    PORTFOLIOTYPE(PrometheusCryptographyDataType.MAXKEYID + 4),

    /**
     * SecurityType.
     */
    SECURITYTYPE(PrometheusCryptographyDataType.MAXKEYID + 5),

    /**
     * PayeeType.
     */
    PAYEETYPE(PrometheusCryptographyDataType.MAXKEYID + 6),

    /**
     * TransactionType.
     */
    TRANSTYPE(PrometheusCryptographyDataType.MAXKEYID + 7),

    /**
     * TaxBasis.
     */
    TAXBASIS(PrometheusCryptographyDataType.MAXKEYID + 8),

    /**
     * Currency.
     */
    CURRENCY(PrometheusCryptographyDataType.MAXKEYID + 9),

    /**
     * AccountInfoType.
     */
    ACCOUNTINFOTYPE(PrometheusCryptographyDataType.MAXKEYID + 10),

    /**
     * TransactionInfoType.
     */
    TRANSINFOTYPE(PrometheusCryptographyDataType.MAXKEYID + 11);

    /**
     * Maximum keyId.
     */
    public static final Integer MAXKEYID = TRANSINFOTYPE.getItemKey();

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
    MoneyWiseStaticDataType(final Integer pKey) {
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
            theNameId = MetisFieldSimpleId.convertResource(MoneyWiseStaticResource.getKeyForDataType(this));
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
            theListId = MetisFieldSimpleId.convertResource(MoneyWiseStaticResource.getKeyForDataList(this));
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
    public Class<? extends MetisFieldVersionedItem> getClazz() {
        switch (this) {
            case DEPOSITTYPE:
                return MoneyWiseDepositCategoryType.class;
            case CASHTYPE:
                return MoneyWiseCashCategoryType.class;
            case LOANTYPE:
                return MoneyWiseLoanCategoryType.class;
            case PAYEETYPE:
                return MoneyWisePayeeType.class;
            case PORTFOLIOTYPE:
                return MoneyWisePortfolioType.class;
            case SECURITYTYPE:
                return MoneyWiseSecurityType.class;
            case TRANSTYPE:
                return MoneyWiseTransCategoryType.class;
            case CURRENCY:
                return MoneyWiseCurrency.class;
            case TAXBASIS:
                return MoneyWiseTaxBasis.class;
            case ACCOUNTINFOTYPE:
                return MoneyWiseAccountInfoType.class;
            case TRANSINFOTYPE:
                return MoneyWiseTransInfoType.class;
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
