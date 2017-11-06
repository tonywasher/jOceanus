/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField.MetisSimpleFieldId;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisFieldId;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisFieldEnum;

/**
 * MoneyWise Item Types.
 */
public enum MoneyWiseDataType implements MetisFieldEnum {
    /**
     * DepositType.
     */
    DEPOSITTYPE,

    /**
     * CashType.
     */
    CASHTYPE,

    /**
     * LoanType.
     */
    LOANTYPE,

    /**
     * PortfolioType.
     */
    PORTFOLIOTYPE,

    /**
     * SecurityType.
     */
    SECURITYTYPE,

    /**
     * PayeeType.
     */
    PAYEETYPE,

    /**
     * TransactionType.
     */
    TRANSTYPE,

    /**
     * TaxBasis.
     */
    TAXBASIS,

    /**
     * Currency.
     */
    CURRENCY,

    /**
     * Frequency.
     */
    FREQUENCY,

    /**
     * AccountInfoType.
     */
    ACCOUNTINFOTYPE,

    /**
     * TransactionInfoType.
     */
    TRANSINFOTYPE,

    /**
     * TransactionTag.
     */
    TRANSTAG,

    /**
     * Region.
     */
    REGION,

    /**
     * DepositCategory.
     */
    DEPOSITCATEGORY,

    /**
     * CashCategory.
     */
    CASHCATEGORY,

    /**
     * LoanCategory.
     */
    LOANCATEGORY,

    /**
     * TransactionCategory.
     */
    TRANSCATEGORY,

    /**
     * ExchangeRate.
     */
    EXCHANGERATE,

    /**
     * Payee.
     */
    PAYEE,

    /**
     * PayeeInfo.
     */
    PAYEEINFO,

    /**
     * Securities.
     */
    SECURITY,

    /**
     * SecurityInfo.
     */
    SECURITYINFO,

    /**
     * SecurityPrice.
     */
    SECURITYPRICE,

    /**
     * Deposit.
     */
    DEPOSIT,

    /**
     * DepositInfo.
     */
    DEPOSITINFO,

    /**
     * DepositRate.
     */
    DEPOSITRATE,

    /**
     * Cash.
     */
    CASH,

    /**
     * CashInfo.
     */
    CASHINFO,

    /**
     * Loan.
     */
    LOAN,

    /**
     * LoanInfo.
     */
    LOANINFO,

    /**
     * Portfolio.
     */
    PORTFOLIO,

    /**
     * PortfolioInfo.
     */
    PORTFOLIOINFO,

    /**
     * Transaction.
     */
    TRANSACTION,

    /**
     * TransactionInfo.
     */
    TRANSACTIONINFO,

    /**
     * Schedule.
     */
    SCHEDULE;

    /**
     * The String id.
     */
    private MetisFieldId theNameId;

    /**
     * The List id.
     */
    private MetisFieldId theListId;

    @Override
    public String toString() {
        /* return the name */
        return getItemId().getId();
    }

    /**
     * Obtain Id of item.
     * @return the item name
     */
    public MetisFieldId getItemId() {
        /* If we have not yet loaded the id */
        if (theNameId == null) {
            /* Load the id */
            theNameId = MetisSimpleFieldId.convertResource(MoneyWiseDataTypeResource.getKeyForDataType(this));
        }

        /* Return the name id */
        return theNameId;
    }

    /**
     * Obtain Name of item.
     * @return the item name
     */
    public String getItemName() {
        return toString();
    }

    /**
     * Obtain Id of associated list.
     * @return the list name
     */
    public MetisFieldId getListId() {
        /* If we have not yet loaded the id */
        if (theListId == null) {
            /* Load the id */
            theListId = MetisSimpleFieldId.convertResource(MoneyWiseDataTypeResource.getKeyForDataList(this));
        }

        /* return the list id */
        return theListId;
    }

    /**
     * Obtain Name of list for item.
     * @return the list name
     */
    public String getListName() {
        return getFieldName();
    }

    @Override
    public String getFieldName() {
        return getListId().getId();
    }
}
