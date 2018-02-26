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

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Resource IDs for jMoneyWise DataType Fields.
 */
public enum MoneyWiseDataTypeResource
        implements TethysResourceId, MetisDataFieldId {
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
     * PortfolioType Name.
     */
    PORTFOLIOTYPE_NAME("PortfolioType.Name"),

    /**
     * PortfolioType List.
     */
    PORTFOLIOTYPE_LIST("PortfolioType.List"),

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
     * Region Name.
     */
    REGION_NAME("Region.Name"),

    /**
     * Region List.
     */
    REGION_LIST("Region.List"),

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
     * The MarketProvider Map.
     */
    private static final Map<MoneyWiseDataType, TethysResourceId> NAME_MAP = buildNameMap();

    /**
     * The MarketProvider Map.
     */
    private static final Map<MoneyWiseDataType, TethysResourceId> LIST_MAP = buildListMap();

    /**
     * The Resource Builder.
     */
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getResourceBuilder(MoneyWiseDataType.class.getCanonicalName());

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
    MoneyWiseDataTypeResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMoneyWise.datatype";
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

    @Override
    public String getId() {
        return getValue();
    }

    /**
     * Build name map.
     * @return the map
     */
    private static Map<MoneyWiseDataType, TethysResourceId> buildNameMap() {
        /* Create the map and return it */
        final Map<MoneyWiseDataType, TethysResourceId> myMap = new EnumMap<>(MoneyWiseDataType.class);
        myMap.put(MoneyWiseDataType.DEPOSITTYPE, DEPOSITTYPE_NAME);
        myMap.put(MoneyWiseDataType.CASHTYPE, CASHTYPE_NAME);
        myMap.put(MoneyWiseDataType.LOANTYPE, LOANTYPE_NAME);
        myMap.put(MoneyWiseDataType.PORTFOLIOTYPE, PORTFOLIOTYPE_NAME);
        myMap.put(MoneyWiseDataType.SECURITYTYPE, SECURITYTYPE_NAME);
        myMap.put(MoneyWiseDataType.PAYEETYPE, PAYEETYPE_NAME);
        myMap.put(MoneyWiseDataType.TRANSTYPE, TRANSTYPE_NAME);
        myMap.put(MoneyWiseDataType.TAXBASIS, TAXBASIS_NAME);
        myMap.put(MoneyWiseDataType.FREQUENCY, FREQUENCY_NAME);
        myMap.put(MoneyWiseDataType.CURRENCY, CURRENCY_NAME);
        myMap.put(MoneyWiseDataType.ACCOUNTINFOTYPE, ACCOUNTINFOTYPE_NAME);
        myMap.put(MoneyWiseDataType.TRANSINFOTYPE, TRANSINFOTYPE_NAME);
        myMap.put(MoneyWiseDataType.DEPOSITCATEGORY, DEPOSITCAT_NAME);
        myMap.put(MoneyWiseDataType.CASHCATEGORY, CASHCAT_NAME);
        myMap.put(MoneyWiseDataType.LOANCATEGORY, LOANCAT_NAME);
        myMap.put(MoneyWiseDataType.TRANSCATEGORY, TRANSCAT_NAME);
        myMap.put(MoneyWiseDataType.EXCHANGERATE, XCHGRATE_NAME);
        myMap.put(MoneyWiseDataType.TRANSTAG, TRANSTAG_NAME);
        myMap.put(MoneyWiseDataType.REGION, REGION_NAME);
        myMap.put(MoneyWiseDataType.PAYEE, PAYEE_NAME);
        myMap.put(MoneyWiseDataType.PAYEEINFO, PAYEEINFO_NAME);
        myMap.put(MoneyWiseDataType.SECURITY, SECURITY_NAME);
        myMap.put(MoneyWiseDataType.SECURITYPRICE, SECURITYPRICE_NAME);
        myMap.put(MoneyWiseDataType.SECURITYINFO, SECURITYINFO_NAME);
        myMap.put(MoneyWiseDataType.DEPOSIT, DEPOSIT_NAME);
        myMap.put(MoneyWiseDataType.DEPOSITRATE, DEPOSITRATE_NAME);
        myMap.put(MoneyWiseDataType.DEPOSITINFO, DEPOSITINFO_NAME);
        myMap.put(MoneyWiseDataType.CASH, CASH_NAME);
        myMap.put(MoneyWiseDataType.CASHINFO, CASHINFO_NAME);
        myMap.put(MoneyWiseDataType.LOAN, LOAN_NAME);
        myMap.put(MoneyWiseDataType.LOANINFO, LOANINFO_NAME);
        myMap.put(MoneyWiseDataType.PORTFOLIO, PORTFOLIO_NAME);
        myMap.put(MoneyWiseDataType.PORTFOLIOINFO, PORTFOLIOINFO_NAME);
        myMap.put(MoneyWiseDataType.TRANSACTION, TRANSACTION_NAME);
        myMap.put(MoneyWiseDataType.TRANSACTIONINFO, TRANSINFO_NAME);
        myMap.put(MoneyWiseDataType.SCHEDULE, SCHEDULE_NAME);
        return myMap;
    }

    /**
     * Obtain key for data item.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForDataType(final MoneyWiseDataType pValue) {
        return TethysResourceBuilder.getKeyForEnum(NAME_MAP, pValue);
    }

    /**
     * Build list map.
     * @return the map
     */
    private static Map<MoneyWiseDataType, TethysResourceId> buildListMap() {
        /* Create the map and return it */
        final Map<MoneyWiseDataType, TethysResourceId> myMap = new EnumMap<>(MoneyWiseDataType.class);
        myMap.put(MoneyWiseDataType.DEPOSITTYPE, DEPOSITTYPE_LIST);
        myMap.put(MoneyWiseDataType.CASHTYPE, CASHTYPE_LIST);
        myMap.put(MoneyWiseDataType.LOANTYPE, LOANTYPE_LIST);
        myMap.put(MoneyWiseDataType.PORTFOLIOTYPE, PORTFOLIOTYPE_LIST);
        myMap.put(MoneyWiseDataType.SECURITYTYPE, SECURITYTYPE_LIST);
        myMap.put(MoneyWiseDataType.PAYEETYPE, PAYEETYPE_LIST);
        myMap.put(MoneyWiseDataType.TRANSTYPE, TRANSTYPE_LIST);
        myMap.put(MoneyWiseDataType.TAXBASIS, TAXBASIS_LIST);
        myMap.put(MoneyWiseDataType.FREQUENCY, FREQUENCY_LIST);
        myMap.put(MoneyWiseDataType.CURRENCY, CURRENCY_LIST);
        myMap.put(MoneyWiseDataType.ACCOUNTINFOTYPE, ACCOUNTINFOTYPE_LIST);
        myMap.put(MoneyWiseDataType.TRANSINFOTYPE, TRANSINFOTYPE_LIST);
        myMap.put(MoneyWiseDataType.DEPOSITCATEGORY, DEPOSITCAT_LIST);
        myMap.put(MoneyWiseDataType.CASHCATEGORY, CASHCAT_LIST);
        myMap.put(MoneyWiseDataType.LOANCATEGORY, LOANCAT_LIST);
        myMap.put(MoneyWiseDataType.TRANSCATEGORY, TRANSCAT_LIST);
        myMap.put(MoneyWiseDataType.EXCHANGERATE, XCHGRATE_LIST);
        myMap.put(MoneyWiseDataType.TRANSTAG, TRANSTAG_LIST);
        myMap.put(MoneyWiseDataType.REGION, REGION_LIST);
        myMap.put(MoneyWiseDataType.PAYEE, PAYEE_LIST);
        myMap.put(MoneyWiseDataType.PAYEEINFO, PAYEEINFO_LIST);
        myMap.put(MoneyWiseDataType.SECURITY, SECURITY_LIST);
        myMap.put(MoneyWiseDataType.SECURITYPRICE, SECURITYPRICE_LIST);
        myMap.put(MoneyWiseDataType.SECURITYINFO, SECURITYINFO_LIST);
        myMap.put(MoneyWiseDataType.DEPOSIT, DEPOSIT_LIST);
        myMap.put(MoneyWiseDataType.DEPOSITRATE, DEPOSITRATE_LIST);
        myMap.put(MoneyWiseDataType.DEPOSITINFO, DEPOSITINFO_LIST);
        myMap.put(MoneyWiseDataType.CASH, CASH_LIST);
        myMap.put(MoneyWiseDataType.CASHINFO, CASHINFO_LIST);
        myMap.put(MoneyWiseDataType.LOAN, LOAN_LIST);
        myMap.put(MoneyWiseDataType.LOANINFO, LOANINFO_LIST);
        myMap.put(MoneyWiseDataType.PORTFOLIO, PORTFOLIO_LIST);
        myMap.put(MoneyWiseDataType.PORTFOLIOINFO, PORTFOLIOINFO_LIST);
        myMap.put(MoneyWiseDataType.TRANSACTION, TRANSACTION_LIST);
        myMap.put(MoneyWiseDataType.TRANSACTIONINFO, TRANSINFO_LIST);
        myMap.put(MoneyWiseDataType.SCHEDULE, SCHEDULE_LIST);
        return myMap;
    }

    /**
     * Obtain key for data list.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForDataList(final MoneyWiseDataType pValue) {
        return TethysResourceBuilder.getKeyForEnum(LIST_MAP, pValue);
    }
}
