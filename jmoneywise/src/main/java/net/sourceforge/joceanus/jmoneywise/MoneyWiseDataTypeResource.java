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

import net.sourceforge.joceanus.jtethys.resource.ResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.ResourceId;

/**
 * Resource IDs for jMoneyWise DataType Fields.
 */
public enum MoneyWiseDataTypeResource implements ResourceId {
    /**
     * DepositType Name.
     */
    DEPOSITTYPE_NAME("DepositType.Name"),

    /**
     * DepositType List.
     */
    DEPOSITTYPE_LIST("DepositType.List"),

    /**
     * CashType Name.
     */
    CASHTYPE_NAME("CashType.Name"),

    /**
     * CashType List.
     */
    CASHTYPE_LIST("CashType.List"),

    /**
     * LoanType Name.
     */
    LOANTYPE_NAME("LoanType.Name"),

    /**
     * LoanType List.
     */
    LOANTYPE_LIST("LoanType.List"),

    /**
     * SecurityType Name.
     */
    SECURITYTYPE_NAME("SecurityType.Name"),

    /**
     * SecurityType List.
     */
    SECURITYTYPE_LIST("SecurityType.List"),

    /**
     * PayeeType Name.
     */
    PAYEETYPE_NAME("PayeeType.Name"),

    /**
     * PayeeType List.
     */
    PAYEETYPE_LIST("PayeeType.List"),

    /**
     * TransType Name.
     */
    TRANSTYPE_NAME("TransType.Name"),

    /**
     * TransType List.
     */
    TRANSTYPE_LIST("TransType.List"),

    /**
     * TaxBasis Name.
     */
    TAXBASIS_NAME("TaxBasis.Name"),

    /**
     * TaxBasis List.
     */
    TAXBASIS_LIST("TaxBasis.List"),

    /**
     * TaxType Name.
     */
    TAXTYPE_NAME("TaxType.Name"),

    /**
     * TaxType List.
     */
    TAXTYPE_LIST("TaxType.List"),

    /**
     * TaxRegime Name.
     */
    TAXREGIME_NAME("TaxRegime.Name"),

    /**
     * TaxRegime List.
     */
    TAXREGIME_LIST("TaxRegime.List"),

    /**
     * Frequency Name.
     */
    FREQUENCY_NAME("Frequency.Name"),

    /**
     * Frequency List.
     */
    FREQUENCY_LIST("Frequency.List"),

    /**
     * Currency Name.
     */
    CURRENCY_NAME("Currency.Name"),

    /**
     * Currency List.
     */
    CURRENCY_LIST("Currency.List"),

    /**
     * TaxInfoType Name.
     */
    TAXINFOTYPE_NAME("TaxInfoType.Name"),

    /**
     * TaxInfoType List.
     */
    TAXINFOTYPE_LIST("TaxInfoType.List"),

    /**
     * AccountInfoType Name.
     */
    ACCOUNTINFOTYPE_NAME("AccountInfoType.Name"),

    /**
     * AccountInfoType List.
     */
    ACCOUNTINFOTYPE_LIST("AccountInfoType.List"),

    /**
     * TransInfoType Name.
     */
    TRANSINFOTYPE_NAME("TransInfoType.Name"),

    /**
     * TransInfoType List.
     */
    TRANSINFOTYPE_LIST("TransInfoType.List"),

    /**
     * DepositCategory Name.
     */
    DEPOSITCAT_NAME("DepositCategory.Name"),

    /**
     * DepositCategory List.
     */
    DEPOSITCAT_LIST("DepositCategory.List"),

    /**
     * CashCategory Name.
     */
    CASHCAT_NAME("CashCategory.Name"),

    /**
     * CashCategory List.
     */
    CASHCAT_LIST("CashCategory.List"),

    /**
     * LoanCategory Name.
     */
    LOANCAT_NAME("LoanCategory.Name"),

    /**
     * LoanCategory List.
     */
    LOANCAT_LIST("LoanCategory.List"),

    /**
     * TransCategory Name.
     */
    TRANSCAT_NAME("TransCategory.Name"),

    /**
     * TransCategory List.
     */
    TRANSCAT_LIST("TransCategory.List"),

    /**
     * ExchangeRate Name.
     */
    XCHGRATE_NAME("ExchangeRate.Name"),

    /**
     * ExchangeRate List.
     */
    XCHGRATE_LIST("ExchangeRate.List"),

    /**
     * TransTag Name.
     */
    TRANSTAG_NAME("TransTag.Name"),

    /**
     * TransTag List.
     */
    TRANSTAG_LIST("TransTag.List"),

    /**
     * TaxInfo Name.
     */
    TAXINFO_NAME("TaxInfo.Name"),

    /**
     * TaxInfo List.
     */
    TAXINFO_LIST("TaxInfo.List"),

    /**
     * TaxYear Name.
     */
    TAXYEAR_NAME("TaxYear.Name"),

    /**
     * TaxYear List.
     */
    TAXYEAR_LIST("TaxYear.List"),

    /**
     * Payee Name.
     */
    PAYEE_NAME("Payee.Name"),

    /**
     * Payee List.
     */
    PAYEE_LIST("Payee.List"),

    /**
     * PayeeInfo Name.
     */
    PAYEEINFO_NAME("PayeeInfo.Name"),

    /**
     * PayeeInfo List.
     */
    PAYEEINFO_LIST("PayeeInfo.List"),

    /**
     * Security Name.
     */
    SECURITY_NAME("Security.Name"),

    /**
     * Security List.
     */
    SECURITY_LIST("Security.List"),

    /**
     * SecurityPrice Name.
     */
    SECURITYPRICE_NAME("SecurityPrice.Name"),

    /**
     * SecurityPrice List.
     */
    SECURITYPRICE_LIST("SecurityPrice.List"),

    /**
     * SecurityInfo Name.
     */
    SECURITYINFO_NAME("SecurityInfo.Name"),

    /**
     * SecurityInfo List.
     */
    SECURITYINFO_LIST("SecurityInfo.List"),

    /**
     * Deposit Name.
     */
    DEPOSIT_NAME("Deposit.Name"),

    /**
     * Deposit List.
     */
    DEPOSIT_LIST("Deposit.List"),

    /**
     * DepositRate Name.
     */
    DEPOSITRATE_NAME("DepositRate.Name"),

    /**
     * DepositRate List.
     */
    DEPOSITRATE_LIST("DepositRate.List"),

    /**
     * DepositInfo Name.
     */
    DEPOSITINFO_NAME("DepositInfo.Name"),

    /**
     * PayeeInfo List.
     */
    DEPOSITINFO_LIST("DepositInfo.List"),

    /**
     * Cash Name.
     */
    CASH_NAME("Cash.Name"),

    /**
     * Cash List.
     */
    CASH_LIST("Cash.List"),

    /**
     * CashInfo Name.
     */
    CASHINFO_NAME("CashInfo.Name"),

    /**
     * CashInfo List.
     */
    CASHINFO_LIST("CashInfo.List"),

    /**
     * Loan Name.
     */
    LOAN_NAME("Loan.Name"),

    /**
     * Loan List.
     */
    LOAN_LIST("Loan.List"),

    /**
     * LoanInfo Name.
     */
    LOANINFO_NAME("LoanInfo.Name"),

    /**
     * LoanInfo List.
     */
    LOANINFO_LIST("LoanInfo.List"),

    /**
     * Portfolio Name.
     */
    PORTFOLIO_NAME("Portfolio.Name"),

    /**
     * Portfolio List.
     */
    PORTFOLIO_LIST("Portfolio.List"),

    /**
     * PortfolioInfo Name.
     */
    PORTFOLIOINFO_NAME("PortfolioInfo.Name"),

    /**
     * PortfolioInfo List.
     */
    PORTFOLIOINFO_LIST("PortfolioInfo.List"),

    /**
     * StockOption Name.
     */
    STOCKOPTION_NAME("StockOption.Name"),

    /**
     * StockOption List.
     */
    STOCKOPTION_LIST("StockOption.List"),

    /**
     * StockOptionVest Name.
     */
    STOCKOPTIONVEST_NAME("StockOptionVest.Name"),

    /**
     * StockOptionVest List.
     */
    STOCKOPTIONVEST_LIST("StockOptionVest.List"),

    /**
     * StockOptionInfo Name.
     */
    STOCKOPTIONINFO_NAME("StockOptionInfo.Name"),

    /**
     * StockOptionInfo List.
     */
    STOCKOPTIONINFO_LIST("StockOptionInfo.List"),

    /**
     * Transaction Name.
     */
    TRANSACTION_NAME("Transaction.Name"),

    /**
     * Transaction List.
     */
    TRANSACTION_LIST("Transaction.List"),

    /**
     * TransInfo Name.
     */
    TRANSINFO_NAME("TransactionInfo.Name"),

    /**
     * TransInfo List.
     */
    TRANSINFO_LIST("TransactionInfo.List"),

    /**
     * Schedule Name.
     */
    SCHEDULE_NAME("Schedule.Name"),

    /**
     * Schedule List.
     */
    SCHEDULE_LIST("Schedule.List");

    /**
     * The Resource Builder.
     */
    private static final ResourceBuilder BUILDER = ResourceBuilder.getResourceBuilder(MoneyWiseDataType.class.getCanonicalName());

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    private MoneyWiseDataTypeResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMoneyWise.data";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    /**
     * Obtain key for data item.
     * @param pValue the Value
     * @return the resource key
     */
    protected static MoneyWiseDataTypeResource getKeyForDataType(final MoneyWiseDataType pValue) {
        switch (pValue) {
            case DEPOSITTYPE:
                return DEPOSITTYPE_NAME;
            case CASHTYPE:
                return CASHTYPE_NAME;
            case LOANTYPE:
                return LOANTYPE_NAME;
            case SECURITYTYPE:
                return SECURITYTYPE_NAME;
            case PAYEETYPE:
                return PAYEETYPE_NAME;
            case TRANSTYPE:
                return TRANSTYPE_NAME;
            case TAXBASIS:
                return TAXBASIS_NAME;
            case TAXTYPE:
                return TAXTYPE_NAME;
            case TAXREGIME:
                return TAXREGIME_NAME;
            case FREQUENCY:
                return FREQUENCY_NAME;
            case CURRENCY:
                return CURRENCY_NAME;
            case TAXINFOTYPE:
                return TAXINFOTYPE_NAME;
            case ACCOUNTINFOTYPE:
                return ACCOUNTINFOTYPE_NAME;
            case TRANSINFOTYPE:
                return TRANSINFOTYPE_NAME;
            case DEPOSITCATEGORY:
                return DEPOSITCAT_NAME;
            case CASHCATEGORY:
                return CASHCAT_NAME;
            case LOANCATEGORY:
                return LOANCAT_NAME;
            case TRANSCATEGORY:
                return TRANSCAT_NAME;
            case EXCHANGERATE:
                return XCHGRATE_NAME;
            case TRANSTAG:
                return TRANSTAG_NAME;
            case TAXYEAR:
                return TAXYEAR_NAME;
            case TAXYEARINFO:
                return TAXINFO_NAME;
            case PAYEE:
                return PAYEE_NAME;
            case PAYEEINFO:
                return PAYEEINFO_NAME;
            case SECURITY:
                return SECURITY_NAME;
            case SECURITYPRICE:
                return SECURITYPRICE_NAME;
            case SECURITYINFO:
                return SECURITYINFO_NAME;
            case DEPOSIT:
                return DEPOSIT_NAME;
            case DEPOSITRATE:
                return DEPOSITRATE_NAME;
            case DEPOSITINFO:
                return DEPOSITINFO_NAME;
            case CASH:
                return CASH_NAME;
            case CASHINFO:
                return CASHINFO_NAME;
            case LOAN:
                return LOAN_NAME;
            case LOANINFO:
                return LOANINFO_NAME;
            case PORTFOLIO:
                return PORTFOLIO_NAME;
            case PORTFOLIOINFO:
                return PORTFOLIOINFO_NAME;
            case STOCKOPTION:
                return STOCKOPTION_NAME;
            case STOCKOPTIONVEST:
                return STOCKOPTIONVEST_NAME;
            case STOCKOPTIONINFO:
                return STOCKOPTIONINFO_NAME;
            case TRANSACTION:
                return TRANSACTION_NAME;
            case TRANSACTIONINFO:
                return TRANSINFO_NAME;
            case SCHEDULE:
                return SCHEDULE_NAME;
            default:
                return null;
        }
    }

    /**
     * Obtain key for data list.
     * @param pValue the Value
     * @return the resource key
     */
    protected static MoneyWiseDataTypeResource getKeyForDataList(final MoneyWiseDataType pValue) {
        switch (pValue) {
            case DEPOSITTYPE:
                return DEPOSITTYPE_LIST;
            case CASHTYPE:
                return CASHTYPE_LIST;
            case LOANTYPE:
                return LOANTYPE_LIST;
            case SECURITYTYPE:
                return SECURITYTYPE_LIST;
            case PAYEETYPE:
                return PAYEETYPE_LIST;
            case TRANSTYPE:
                return TRANSTYPE_LIST;
            case TAXBASIS:
                return TAXBASIS_LIST;
            case TAXTYPE:
                return TAXTYPE_LIST;
            case TAXREGIME:
                return TAXREGIME_LIST;
            case FREQUENCY:
                return FREQUENCY_LIST;
            case CURRENCY:
                return CURRENCY_LIST;
            case TAXINFOTYPE:
                return TAXINFOTYPE_LIST;
            case ACCOUNTINFOTYPE:
                return ACCOUNTINFOTYPE_LIST;
            case TRANSINFOTYPE:
                return TRANSINFOTYPE_LIST;
            case DEPOSITCATEGORY:
                return DEPOSITCAT_LIST;
            case CASHCATEGORY:
                return CASHCAT_LIST;
            case LOANCATEGORY:
                return LOANCAT_LIST;
            case TRANSCATEGORY:
                return TRANSCAT_LIST;
            case EXCHANGERATE:
                return XCHGRATE_LIST;
            case TRANSTAG:
                return TRANSTAG_LIST;
            case TAXYEAR:
                return TAXYEAR_LIST;
            case TAXYEARINFO:
                return TAXINFO_LIST;
            case PAYEE:
                return PAYEE_LIST;
            case PAYEEINFO:
                return PAYEEINFO_LIST;
            case SECURITY:
                return SECURITY_LIST;
            case SECURITYPRICE:
                return SECURITYPRICE_LIST;
            case SECURITYINFO:
                return SECURITYINFO_LIST;
            case DEPOSIT:
                return DEPOSIT_LIST;
            case DEPOSITRATE:
                return DEPOSITRATE_LIST;
            case DEPOSITINFO:
                return DEPOSITINFO_LIST;
            case CASH:
                return CASH_LIST;
            case CASHINFO:
                return CASHINFO_LIST;
            case LOAN:
                return LOAN_LIST;
            case LOANINFO:
                return LOANINFO_LIST;
            case PORTFOLIO:
                return PORTFOLIO_LIST;
            case PORTFOLIOINFO:
                return PORTFOLIOINFO_LIST;
            case STOCKOPTION:
                return STOCKOPTION_LIST;
            case STOCKOPTIONVEST:
                return STOCKOPTIONVEST_LIST;
            case STOCKOPTIONINFO:
                return STOCKOPTIONINFO_LIST;
            case TRANSACTION:
                return TRANSACTION_LIST;
            case TRANSACTIONINFO:
                return TRANSINFO_LIST;
            case SCHEDULE:
                return SCHEDULE_LIST;
            default:
                return null;
        }
    }
}
