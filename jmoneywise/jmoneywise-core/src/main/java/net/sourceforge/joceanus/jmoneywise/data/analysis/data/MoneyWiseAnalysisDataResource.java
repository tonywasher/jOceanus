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
package net.sourceforge.joceanus.jmoneywise.data.analysis.data;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseStaticResource;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for jMoneyWise Analysis Fields.
 */
public enum MoneyWiseAnalysisDataResource
        implements TethysBundleId, MetisDataFieldId {
    /**
     * Analysis Name.
     */
    ANALYSIS_NAME("Analysis.Name"),

    /**
     * Analysis Analyser Name.
     */
    ANALYSIS_ANALYSER("Analysis.Analyser"),

    /**
     * Analysis Manager Name.
     */
    ANALYSIS_MANAGER("Analysis.Manager"),

    /**
     * Analysis Chargeable Events.
     */
    ANALYSIS_CHARGES("Analysis.Charges"),

    /**
     * Analysis Dilution Events.
     */
    ANALYSIS_DILUTIONS("Analysis.Dilutions"),

    /**
     * Analysis Totals.
     */
    ANALYSIS_TOTALS("Analysis.Totals"),

    /**
     * Bucket Account.
     */
    BUCKET_ACCOUNT("Bucket.Account"),

    /**
     * Bucket BaseValues.
     */
    BUCKET_BASEVALUES("Bucket.BaseValues"),

    /**
     * Bucket History.
     */
    BUCKET_HISTORY("Bucket.History"),

    /**
     * Bucket SnapShot.
     */
    BUCKET_SNAPSHOT("Bucket.SnapShot"),

    /**
     * Bucket Values.
     */
    BUCKET_VALUES("Bucket.Values"),

    /**
     * Bucket Previous Values.
     */
    BUCKET_PREVIOUS("Bucket.Previous"),

    /**
     * Filter All.
     */
    FILTER_ALL("Filter.All"),

    /**
     * TransTag Name.
     */
    TRANSTAG_NAME("TransTag.Name"),

    /**
     * TransTag List.
     */
    TRANSTAG_LIST("TransTag.List"),

    /**
     * Cash Name.
     */
    CASH_NAME("Cash.Name"),

    /**
     * Cash List.
     */
    CASH_LIST("Cash.List"),

    /**
     * CashCategory Name.
     */
    CASHCATEGORY_NAME("CashCategory.Name"),

    /**
     * CashCategory List.
     */
    CASHCATEGORY_LIST("CashCategory.List"),

    /**
     * Deposit Name.
     */
    DEPOSIT_NAME("Deposit.Name"),

    /**
     * Deposit List.
     */
    DEPOSIT_LIST("Deposit.List"),

    /**
     * DepositCategory Name.
     */
    DEPOSITCATEGORY_NAME("DepositCategory.Name"),

    /**
     * DepositCategory List.
     */
    DEPOSITCATEGORY_LIST("DepositCategory.List"),

    /**
     * Loan Name.
     */
    LOAN_NAME("Loan.Name"),

    /**
     * Loan List.
     */
    LOAN_LIST("Loan.List"),

    /**
     * Loan isCreditCard.
     */
    LOAN_CREDITCARD("Loan.isCreditCard"),

    /**
     * LoanCategory Name.
     */
    LOANCATEGORY_NAME("LoanCategory.Name"),

    /**
     * LoanCategory List.
     */
    LOANCATEGORY_LIST("LoanCategory.List"),

    /**
     * TransactionCategory Name.
     */
    TRANSCATEGORY_NAME("TransCategory.Name"),

    /**
     * TransactionCategory List.
     */
    TRANSCATEGORY_LIST("TransCategory.List"),

    /**
     * Payee Name.
     */
    PAYEE_NAME("Payee.Name"),

    /**
     * Payee List.
     */
    PAYEE_LIST("Payee.List"),

    /**
     * Portfolio Name.
     */
    PORTFOLIO_NAME("Portfolio.Name"),

    /**
     * Portfolio List.
     */
    PORTFOLIO_LIST("Portfolio.List"),

    /**
     * Portfolio Cash Name.
     */
    PORTFOLIOCASH_NAME("Portfolio.Cash.Name"),

    /**
     * Security Name.
     */
    SECURITY_NAME("Security.Name"),

    /**
     * Security List.
     */
    SECURITY_LIST("Security.List"),

    /**
     * TaxBasis Name.
     */
    TAXBASIS_NAME("TaxBasis.Name"),

    /**
     * TaxBasis List.
     */
    TAXBASIS_LIST("TaxBasis.List"),

    /**
     * TaxBasisAccount Name.
     */
    TAXBASIS_ACCOUNTNAME("TaxBasis.AccountName"),

    /**
     * TaxBasisAccount List.
     */
    TAXBASIS_ACCOUNTLIST("TaxBasis.AccountList"),

    /**
     * Dilution Name.
     */
    DILUTION_NAME("Dilution.Name"),

    /**
     * Dilution List.
     */
    DILUTION_LIST("Dilution.List"),

    /**
     * Charge Name.
     */
    CHARGE_NAME("Charge.Name"),

    /**
     * Charge List.
     */
    CHARGE_LIST("Charge.List"),

    /**
     * Charge Slice.
     */
    CHARGE_SLICE("Charge.Slice"),

    /**
     * Charge Tax.
     */
    CHARGE_TAX("Charge.Tax"),

    /**
     * AccountAttr Valuation.
     */
    ACCOUNTATTR_VALUATION("AccountAttr.Valuation"),

    /**
     * AccountAttr DepositRate.
     */
    ACCOUNTATTR_DEPOSITRATE(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE),

    /**
     * AccountAttr Delta.
     */
    ACCOUNTATTR_VALUEDELTA("AccountAttr.ValueDelta"),

    /**
     * AccountAttr Profit.
     */
    ACCOUNTATTR_PROFIT("AccountAttr.Profit"),

    /**
     * AccountAttr LocalValue.
     */
    ACCOUNTATTR_LOCALVALUE("AccountAttr.LocalValue"),

    /**
     * AccountAttr Foreign Valuation.
     */
    ACCOUNTATTR_FOREIGNVALUE("AccountAttr.ForeignValue"),

    /**
     * AccountAttr CurrencyFluctuation.
     */
    ACCOUNTATTR_CURRENCYFLUCT("AccountAttr.CurrencyFluct"),

    /**
     * AccountAttr ExchangeRate.
     */
    ACCOUNTATTR_EXCHANGERATE(MoneyWiseBasicResource.XCHGRATE_NAME),

    /**
     * AccountAttr Maturity.
     */
    ACCOUNTATTR_MATURITY("AccountAttr.Maturity"),

    /**
     * AccountAttr Valuation.
     */
    ACCOUNTATTR_SPEND("AccountAttr.Spend"),

    /**
     * AccountAttr BadDebt.
     */
    ACCOUNTATTR_BADDEBTCAPITAL(MoneyWiseStaticResource.TRANSTYPE_BADDEBTCAPITAL),

    /**
     * AccountAttr BadDebt.
     */
    ACCOUNTATTR_BADDEBTINTEREST(MoneyWiseStaticResource.TRANSTYPE_BADDEBTINTEREST),

    /**
     * PayeeAttr Valuation.
     */
    PAYEEATTR_INCOME("PayeeAttr.Income"),

    /**
     * PayeeAttr Valuation.
     */
    PAYEEATTR_EXPENSE("PayeeAttr.Expense"),

    /**
     * SecurityAttr Units.
     */
    SECURITYATTR_UNITS(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_UNITS),

    /**
     * SecurityAttr ResidualCost.
     */
    SECURITYATTR_RESIDUALCOST("SecurityAttr.ResidualCost"),

    /**
     * SecurityAttr RealisedGains.
     */
    SECURITYATTR_REALISEDGAINS("SecurityAttr.RealisedGains"),

    /**
     * SecurityAttr GrowthAdjustment.
     */
    SECURITYATTR_GROWTHADJUST("SecurityAttr.GrowthAdjust"),

    /**
     * SecurityAttr ForeignValueDelta.
     */
    SECURITYATTR_FOREIGNVALUEDELTA("SecurityAttr.ForeignValueDelta"),

    /**
     * SecurityAttr Invested.
     */
    SECURITYATTR_INVESTED("SecurityAttr.Invested"),

    /**
     * SecurityAttr Foreign Invested.
     */
    SECURITYATTR_FOREIGNINVESTED("SecurityAttr.ForeignInvested"),

    /**
     * SecurityAttr Dividend.
     */
    SECURITYATTR_DIVIDEND("SecurityAttr.Dividend"),

    /**
     * SecurityAttr MarketGrowth.
     */
    SECURITYATTR_MARKETGROWTH("SecurityAttr.MarketGrowth"),

    /**
     * SecurityAttr ForeignMarketGrowth.
     */
    SECURITYATTR_FOREIGNMARKETGROWTH("SecurityAttr.ForeignMarketGrowth"),

    /**
     * SecurityAttr LocalMarketGrowth.
     */
    SECURITYATTR_LOCALMARKETGROWTH("SecurityAttr.LocalMarketGrowth"),

    /**
     * SecurityAttr MarketProfit.
     */
    SECURITYATTR_MARKETPROFIT("SecurityAttr.MarketProfit"),

    /**
     * SecurityAttr Profit.
     */
    SECURITYATTR_PROFIT("SecurityAttr.Profit"),

    /**
     * SecurityAttr Consideration.
     */
    SECURITYATTR_CONSIDER("SecurityAttr.Consider"),

    /**
     * SecurityAttr CashConsideration/ReturnedCash.
     */
    SECURITYATTR_RETURNEDCASH("SecurityAttr.ReturnedCash"),

    /**
     * SecurityAttr StockConsideration/XferredValue.
     */
    SECURITYATTR_XFERREDVALUE("SecurityAttr.XferredValue"),

    /**
     * SecurityAttr XferredCost.
     */
    SECURITYATTR_XFERREDCOST("SecurityAttr.XferredCost"),

    /**
     * SecurityAttr CostDilution.
     */
    SECURITYATTR_COSTDILUTION("SecurityAttr.CostDilution"),

    /**
     * SecurityAttr CashInvested.
     */
    SECURITYATTR_CASHINVESTED("SecurityAttr.CashInvested"),

    /**
     * SecurityAttr CapitalGain.
     */
    SECURITYATTR_CAPITALGAIN("SecurityAttr.CapitalGain"),

    /**
     * SecurityAttr AllowedCost.
     */
    SECURITYATTR_ALLOWEDCOST("SecurityAttr.AllowedCost"),

    /**
     * SecurityAttr Price.
     */
    SECURITYATTR_PRICE(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE),

    /**
     * SecurityAttr CashType.
     */
    SECURITYATTR_CASHTYPE("SecurityAttr.CashType"),

    /**
     * TaxAttr Gross.
     */
    TAXATTR_GROSS("TaxAttr.Gross"),

    /**
     * TaxAttr Nett.
     */
    TAXATTR_NETT("TaxAttr.Nett"),

    /**
     * TaxAttr Tax.
     */
    TAXATTR_TAX("TaxAttr.Tax"),

    /**
     * TaxCalculation.
     */
    TAX_CALCULATION("Tax.Calculation"),

    /**
     * TaxYears.
     */
    TAX_YEARS("Tax.Years"),

    /**
     * TaxPreference Display Name.
     */
    TAXPREF_PREFNAME("taxpref.display"),

    /**
     * TaxPreference Birth Date.
     */
    TAXPREF_BIRTH("taxpref.birth");

    /**
     * The AccountAttr Map.
     */
    private static final Map<MoneyWiseAnalysisAccountAttr, TethysBundleId> ACCOUNT_MAP = buildAccountMap();

    /**
     * The TransactionAttr Map.
     */
    private static final Map<MoneyWiseAnalysisTransAttr, TethysBundleId> TRANSACTION_MAP = buildTransMap();

    /**
     * The PayeeAttr Map.
     */
    private static final Map<MoneyWiseAnalysisPayeeAttr, TethysBundleId> PAYEE_MAP = buildPayeeMap();

    /**
     * The SecurityAttr Map.
     */
    private static final Map<MoneyWiseAnalysisSecurityAttr, TethysBundleId> SECURITY_MAP = buildSecurityMap();

    /**
     * The TaxAttr Map.
     */
    private static final Map<MoneyWiseAnalysisTaxBasisAttr, TethysBundleId> TAX_MAP = buildTaxMap();

    /**
     * The AnalysisType Map.
     */
    private static final Map<MoneyWiseAnalysisType, TethysBundleId> ANALYSIS_MAP = buildAnalysisMap();

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getLoader(MoneyWiseAnalysisDataResource.class.getCanonicalName(),
            ResourceBundle::getBundle);

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
    MoneyWiseAnalysisDataResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     * @param pResource the underlying resource
     */
    MoneyWiseAnalysisDataResource(final TethysBundleId pResource) {
        theKeyName = null;
        theValue = pResource.getValue();
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMoneyWise.analysis";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    @Override
    public String getId() {
        return getValue();
    }

    /**
     * Build account map.
     * @return the map
     */
    private static Map<MoneyWiseAnalysisAccountAttr, TethysBundleId> buildAccountMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAnalysisAccountAttr, TethysBundleId> myMap = new EnumMap<>(MoneyWiseAnalysisAccountAttr.class);
        myMap.put(MoneyWiseAnalysisAccountAttr.VALUATION, ACCOUNTATTR_VALUATION);
        myMap.put(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE, ACCOUNTATTR_FOREIGNVALUE);
        myMap.put(MoneyWiseAnalysisAccountAttr.LOCALVALUE, ACCOUNTATTR_LOCALVALUE);
        myMap.put(MoneyWiseAnalysisAccountAttr.CURRENCYFLUCT, ACCOUNTATTR_CURRENCYFLUCT);
        myMap.put(MoneyWiseAnalysisAccountAttr.DEPOSITRATE, ACCOUNTATTR_DEPOSITRATE);
        myMap.put(MoneyWiseAnalysisAccountAttr.EXCHANGERATE, ACCOUNTATTR_EXCHANGERATE);
        myMap.put(MoneyWiseAnalysisAccountAttr.VALUEDELTA, ACCOUNTATTR_VALUEDELTA);
        myMap.put(MoneyWiseAnalysisAccountAttr.MATURITY, ACCOUNTATTR_MATURITY);
        myMap.put(MoneyWiseAnalysisAccountAttr.SPEND, ACCOUNTATTR_SPEND);
        myMap.put(MoneyWiseAnalysisAccountAttr.BADDEBTCAPITAL, ACCOUNTATTR_BADDEBTCAPITAL);
        myMap.put(MoneyWiseAnalysisAccountAttr.BADDEBTINTEREST, ACCOUNTATTR_BADDEBTINTEREST);
        return myMap;
    }

    /**
     * Obtain key for account attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysBundleId getKeyForAccountAttr(final MoneyWiseAnalysisAccountAttr pValue) {
        return TethysBundleLoader.getKeyForEnum(ACCOUNT_MAP, pValue);
    }

    /**
     * Build transaction map.
     * @return the map
     */
    private static Map<MoneyWiseAnalysisTransAttr, TethysBundleId> buildTransMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAnalysisTransAttr, TethysBundleId> myMap = new EnumMap<>(MoneyWiseAnalysisTransAttr.class);
        myMap.put(MoneyWiseAnalysisTransAttr.INCOME, PAYEEATTR_INCOME);
        myMap.put(MoneyWiseAnalysisTransAttr.EXPENSE, PAYEEATTR_EXPENSE);
        myMap.put(MoneyWiseAnalysisTransAttr.PROFIT, ACCOUNTATTR_PROFIT);
        return myMap;
    }

    /**
     * Obtain key for transaction attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysBundleId getKeyForTransactionAttr(final MoneyWiseAnalysisTransAttr pValue) {
        return TethysBundleLoader.getKeyForEnum(TRANSACTION_MAP, pValue);
    }

    /**
     * Build payee map.
     * @return the map
     */
    private static Map<MoneyWiseAnalysisPayeeAttr, TethysBundleId> buildPayeeMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAnalysisPayeeAttr, TethysBundleId> myMap = new EnumMap<>(MoneyWiseAnalysisPayeeAttr.class);
        myMap.put(MoneyWiseAnalysisPayeeAttr.INCOME, PAYEEATTR_INCOME);
        myMap.put(MoneyWiseAnalysisPayeeAttr.EXPENSE, PAYEEATTR_EXPENSE);
        myMap.put(MoneyWiseAnalysisPayeeAttr.PROFIT, ACCOUNTATTR_PROFIT);
        return myMap;
    }

    /**
     * Obtain key for Payee attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysBundleId getKeyForPayeeAttr(final MoneyWiseAnalysisPayeeAttr pValue) {
        return TethysBundleLoader.getKeyForEnum(PAYEE_MAP, pValue);
    }

    /**
     * Build security map.
     * @return the map
     */
    private static Map<MoneyWiseAnalysisSecurityAttr, TethysBundleId> buildSecurityMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAnalysisSecurityAttr, TethysBundleId> myMap = new EnumMap<>(MoneyWiseAnalysisSecurityAttr.class);
        myMap.put(MoneyWiseAnalysisSecurityAttr.VALUATION, ACCOUNTATTR_VALUATION);
        myMap.put(MoneyWiseAnalysisSecurityAttr.FOREIGNVALUE, ACCOUNTATTR_FOREIGNVALUE);
        myMap.put(MoneyWiseAnalysisSecurityAttr.VALUEDELTA, ACCOUNTATTR_VALUEDELTA);
        myMap.put(MoneyWiseAnalysisSecurityAttr.FOREIGNVALUEDELTA, SECURITYATTR_FOREIGNVALUEDELTA);
        myMap.put(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, ACCOUNTATTR_EXCHANGERATE);
        myMap.put(MoneyWiseAnalysisSecurityAttr.UNITS, SECURITYATTR_UNITS);
        myMap.put(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, SECURITYATTR_RESIDUALCOST);
        myMap.put(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS, SECURITYATTR_REALISEDGAINS);
        myMap.put(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST, SECURITYATTR_GROWTHADJUST);
        myMap.put(MoneyWiseAnalysisSecurityAttr.INVESTED, SECURITYATTR_INVESTED);
        myMap.put(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, SECURITYATTR_FOREIGNINVESTED);
        myMap.put(MoneyWiseAnalysisSecurityAttr.DIVIDEND, SECURITYATTR_DIVIDEND);
        myMap.put(MoneyWiseAnalysisSecurityAttr.MARKETGROWTH, SECURITYATTR_MARKETGROWTH);
        myMap.put(MoneyWiseAnalysisSecurityAttr.FOREIGNMARKETGROWTH, SECURITYATTR_FOREIGNMARKETGROWTH);
        myMap.put(MoneyWiseAnalysisSecurityAttr.LOCALMARKETGROWTH, SECURITYATTR_LOCALMARKETGROWTH);
        myMap.put(MoneyWiseAnalysisSecurityAttr.CURRENCYFLUCT, ACCOUNTATTR_CURRENCYFLUCT);
        myMap.put(MoneyWiseAnalysisSecurityAttr.MARKETPROFIT, SECURITYATTR_MARKETPROFIT);
        myMap.put(MoneyWiseAnalysisSecurityAttr.PROFIT, SECURITYATTR_PROFIT);
        myMap.put(MoneyWiseAnalysisSecurityAttr.CONSIDERATION, SECURITYATTR_CONSIDER);
        myMap.put(MoneyWiseAnalysisSecurityAttr.RETURNEDCASH, SECURITYATTR_RETURNEDCASH);
        myMap.put(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE, SECURITYATTR_XFERREDVALUE);
        myMap.put(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, SECURITYATTR_XFERREDCOST);
        myMap.put(MoneyWiseAnalysisSecurityAttr.COSTDILUTION, SECURITYATTR_COSTDILUTION);
        myMap.put(MoneyWiseAnalysisSecurityAttr.CASHINVESTED, SECURITYATTR_CASHINVESTED);
        myMap.put(MoneyWiseAnalysisSecurityAttr.CAPITALGAIN, SECURITYATTR_CAPITALGAIN);
        myMap.put(MoneyWiseAnalysisSecurityAttr.ALLOWEDCOST, SECURITYATTR_ALLOWEDCOST);
        myMap.put(MoneyWiseAnalysisSecurityAttr.PRICE, SECURITYATTR_PRICE);
        myMap.put(MoneyWiseAnalysisSecurityAttr.CASHTYPE, SECURITYATTR_CASHTYPE);
        return myMap;
    }

    /**
     * Obtain key for security attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysBundleId getKeyForSecurityAttr(final MoneyWiseAnalysisSecurityAttr pValue) {
        return TethysBundleLoader.getKeyForEnum(SECURITY_MAP, pValue);
    }

    /**
     * Build taxBasis map.
     * @return the map
     */
    private static Map<MoneyWiseAnalysisTaxBasisAttr, TethysBundleId> buildTaxMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAnalysisTaxBasisAttr, TethysBundleId> myMap = new EnumMap<>(MoneyWiseAnalysisTaxBasisAttr.class);
        myMap.put(MoneyWiseAnalysisTaxBasisAttr.GROSS, TAXATTR_GROSS);
        myMap.put(MoneyWiseAnalysisTaxBasisAttr.NETT, TAXATTR_NETT);
        myMap.put(MoneyWiseAnalysisTaxBasisAttr.TAXCREDIT, TAXATTR_TAX);
        return myMap;
    }

    /**
     * Obtain key for tax attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysBundleId getKeyForTaxAttr(final MoneyWiseAnalysisTaxBasisAttr pValue) {
        return TethysBundleLoader.getKeyForEnum(TAX_MAP, pValue);
    }

    /**
     * Build analysis map.
     * @return the map
     */
    private static Map<MoneyWiseAnalysisType, TethysBundleId> buildAnalysisMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAnalysisType, TethysBundleId> myMap = new EnumMap<>(MoneyWiseAnalysisType.class);
        myMap.put(MoneyWiseAnalysisType.DEPOSIT, MoneyWiseBasicResource.DEPOSIT_NAME);
        myMap.put(MoneyWiseAnalysisType.CASH, MoneyWiseBasicResource.CASH_NAME);
        myMap.put(MoneyWiseAnalysisType.LOAN, MoneyWiseBasicResource.LOAN_NAME);
        myMap.put(MoneyWiseAnalysisType.PAYEE, MoneyWiseBasicResource.PAYEE_NAME);
        myMap.put(MoneyWiseAnalysisType.SECURITY, MoneyWiseBasicResource.SECURITY_NAME);
        myMap.put(MoneyWiseAnalysisType.PORTFOLIO, MoneyWiseBasicResource.PORTFOLIO_NAME);
        myMap.put(MoneyWiseAnalysisType.CATEGORY, MoneyWiseBasicResource.TRANSCAT_NAME);
        myMap.put(MoneyWiseAnalysisType.TAXBASIS, MoneyWiseStaticResource.TAXBASIS_NAME);
        myMap.put(MoneyWiseAnalysisType.TRANSTAG, MoneyWiseBasicResource.TRANSTAG_NAME);
        myMap.put(MoneyWiseAnalysisType.ALL, FILTER_ALL);
        return myMap;
    }

    /**
     * Obtain key for analysisType.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysBundleId getKeyForAnalysisType(final MoneyWiseAnalysisType pValue) {
        return TethysBundleLoader.getKeyForEnum(ANALYSIS_MAP, pValue);
    }
}
