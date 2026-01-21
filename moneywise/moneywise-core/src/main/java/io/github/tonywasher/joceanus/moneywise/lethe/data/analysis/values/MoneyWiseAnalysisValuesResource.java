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
package io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values;

import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleLoader;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseStaticResource;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Resource IDs for MoneyWise Analysis Fields.
 */
public enum MoneyWiseAnalysisValuesResource
        implements OceanusBundleId, MetisDataFieldId {
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
    TAXATTR_TAX("TaxAttr.Tax");

    /**
     * The AccountAttr Map.
     */
    private static final Map<MoneyWiseAnalysisAccountAttr, OceanusBundleId> ACCOUNT_MAP = buildAccountMap();

    /**
     * The TransactionAttr Map.
     */
    private static final Map<MoneyWiseAnalysisTransAttr, OceanusBundleId> TRANSACTION_MAP = buildTransMap();

    /**
     * The PayeeAttr Map.
     */
    private static final Map<MoneyWiseAnalysisPayeeAttr, OceanusBundleId> PAYEE_MAP = buildPayeeMap();

    /**
     * The SecurityAttr Map.
     */
    private static final Map<MoneyWiseAnalysisSecurityAttr, OceanusBundleId> SECURITY_MAP = buildSecurityMap();

    /**
     * The TaxAttr Map.
     */
    private static final Map<MoneyWiseAnalysisTaxBasisAttr, OceanusBundleId> TAX_MAP = buildTaxMap();

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(MoneyWiseAnalysisValuesResource.class.getCanonicalName(),
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
     *
     * @param pKeyName the key name
     */
    MoneyWiseAnalysisValuesResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     *
     * @param pResource the underlying resource
     */
    MoneyWiseAnalysisValuesResource(final OceanusBundleId pResource) {
        theKeyName = null;
        theValue = pResource.getValue();
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "MoneyWise.analysis";
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
     *
     * @return the map
     */
    private static Map<MoneyWiseAnalysisAccountAttr, OceanusBundleId> buildAccountMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAnalysisAccountAttr, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseAnalysisAccountAttr.class);
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
     *
     * @param pValue the Value
     * @return the resource key
     */
    static OceanusBundleId getKeyForAccountAttr(final MoneyWiseAnalysisAccountAttr pValue) {
        return OceanusBundleLoader.getKeyForEnum(ACCOUNT_MAP, pValue);
    }

    /**
     * Build transaction map.
     *
     * @return the map
     */
    private static Map<MoneyWiseAnalysisTransAttr, OceanusBundleId> buildTransMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAnalysisTransAttr, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseAnalysisTransAttr.class);
        myMap.put(MoneyWiseAnalysisTransAttr.INCOME, PAYEEATTR_INCOME);
        myMap.put(MoneyWiseAnalysisTransAttr.EXPENSE, PAYEEATTR_EXPENSE);
        myMap.put(MoneyWiseAnalysisTransAttr.PROFIT, ACCOUNTATTR_PROFIT);
        return myMap;
    }

    /**
     * Obtain key for transaction attribute.
     *
     * @param pValue the Value
     * @return the resource key
     */
    protected static OceanusBundleId getKeyForTransactionAttr(final MoneyWiseAnalysisTransAttr pValue) {
        return OceanusBundleLoader.getKeyForEnum(TRANSACTION_MAP, pValue);
    }

    /**
     * Build payee map.
     *
     * @return the map
     */
    private static Map<MoneyWiseAnalysisPayeeAttr, OceanusBundleId> buildPayeeMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAnalysisPayeeAttr, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseAnalysisPayeeAttr.class);
        myMap.put(MoneyWiseAnalysisPayeeAttr.INCOME, PAYEEATTR_INCOME);
        myMap.put(MoneyWiseAnalysisPayeeAttr.EXPENSE, PAYEEATTR_EXPENSE);
        myMap.put(MoneyWiseAnalysisPayeeAttr.PROFIT, ACCOUNTATTR_PROFIT);
        return myMap;
    }

    /**
     * Obtain key for Payee attribute.
     *
     * @param pValue the Value
     * @return the resource key
     */
    static OceanusBundleId getKeyForPayeeAttr(final MoneyWiseAnalysisPayeeAttr pValue) {
        return OceanusBundleLoader.getKeyForEnum(PAYEE_MAP, pValue);
    }

    /**
     * Build security map.
     *
     * @return the map
     */
    private static Map<MoneyWiseAnalysisSecurityAttr, OceanusBundleId> buildSecurityMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAnalysisSecurityAttr, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseAnalysisSecurityAttr.class);
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
     *
     * @param pValue the Value
     * @return the resource key
     */
    static OceanusBundleId getKeyForSecurityAttr(final MoneyWiseAnalysisSecurityAttr pValue) {
        return OceanusBundleLoader.getKeyForEnum(SECURITY_MAP, pValue);
    }

    /**
     * Build taxBasis map.
     *
     * @return the map
     */
    private static Map<MoneyWiseAnalysisTaxBasisAttr, OceanusBundleId> buildTaxMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAnalysisTaxBasisAttr, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseAnalysisTaxBasisAttr.class);
        myMap.put(MoneyWiseAnalysisTaxBasisAttr.GROSS, TAXATTR_GROSS);
        myMap.put(MoneyWiseAnalysisTaxBasisAttr.NETT, TAXATTR_NETT);
        myMap.put(MoneyWiseAnalysisTaxBasisAttr.TAXCREDIT, TAXATTR_TAX);
        return myMap;
    }

    /**
     * Obtain key for tax attribute.
     *
     * @param pValue the Value
     * @return the resource key
     */
    static OceanusBundleId getKeyForTaxAttr(final MoneyWiseAnalysisTaxBasisAttr pValue) {
        return OceanusBundleLoader.getKeyForEnum(TAX_MAP, pValue);
    }
}
