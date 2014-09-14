/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataFieldEnum;

/**
 * MoneyWise Item Types.
 */
public enum MoneyWiseDataType implements JDataFieldEnum {
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
     * TaxType.
     */
    TAXTYPE,

    /**
     * Currency.
     */
    CURRENCY,

    /**
     * TaxRegime.
     */
    TAXREGIME,

    /**
     * Frequency.
     */
    FREQUENCY,

    /**
     * TaxInfoType.
     */
    TAXINFOTYPE,

    /**
     * AccountInfoType.
     */
    ACCOUNTINFOTYPE,

    /**
     * TransactionInfoType.
     */
    TRANSINFOTYPE,

    /**
     * TaxYear.
     */
    TAXYEAR,

    /**
     * TaxYearInfo.
     */
    TAXYEARINFO,

    /**
     * TransactionTag.
     */
    TRANSTAG,

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
     * SecurityPrice.
     */
    SECURITYPRICE,

    /**
     * SecurityInfo.
     */
    SECURITYINFO,

    /**
     * Deposit.
     */
    DEPOSIT,

    /**
     * DepositRate.
     */
    DEPOSITRATE,

    /**
     * DepositInfo.
     */
    DEPOSITINFO,

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
     * The String name.
     */
    private String theName;

    /**
     * The List name.
     */
    private String theListName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = MoneyWiseDataTypeResource.getKeyForDataType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain name of item.
     * @return the item name
     */
    public String getItemName() {
        return toString();
    }

    /**
     * Obtain name of associated list.
     * @return the list name
     */
    public String getListName() {
        /* If we have not yet loaded the name */
        if (theListName == null) {
            /* Load the name */
            theListName = MoneyWiseDataTypeResource.getKeyForDataList(this).getValue();
        }

        /* return the list name */
        return theListName;
    }

    @Override
    public String getFieldName() {
        return getListName();
    }
}
