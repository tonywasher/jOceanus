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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.values;

import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleLoader;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Resource IDs for MoneyWise Analysis Fields.
 */
public enum MoneyWiseXAnalysisValuesResource
        implements OceanusBundleId, MetisDataFieldId {
    /**
     * AccountAttr Balance.
     */
    ACCOUNTATTR_BALANCE("AccountAttr.Balance"),

    /**
     * AccountAttr ReportedBalance.
     */
    ACCOUNTATTR_VALUATION("AccountAttr.Valuation"),

    /**
     * AccountAttr Delta.
     */
    ACCOUNTATTR_VALUEDELTA("AccountAttr.ValueDelta"),

    /**
     * AccountAttr Profit.
     */
    ACCOUNTATTR_PROFIT("AccountAttr.Profit"),

    /**
     * AccountAttr DepositRate.
     */
    ACCOUNTATTR_DEPOSITRATE(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE),

    /**
     * AccountAttr ExchangeRate.
     */
    ACCOUNTATTR_EXCHANGERATE(MoneyWiseBasicResource.XCHGRATE_NAME),

    /**
     * AccountAttr Maturity.
     */
    ACCOUNTATTR_MATURITY("AccountAttr.Maturity"),

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
     * SecurityAttr Value.
     */
    SECURITYATTR_VALUE("SecurityAttr.Value"),

    /**
     * SecurityAttr ResidualCost.
     */
    SECURITYATTR_RESIDUALCOST("SecurityAttr.ResidualCost"),

    /**
     * SecurityAttr RealisedGains.
     */
    SECURITYATTR_REALISEDGAINS("SecurityAttr.RealisedGains"),

    /**
     * SecurityAttr unrealisedGains.
     */
    SECURITYATTR_UNREALISEDGAINS("SecurityAttr.UnRealisedGains"),

    /**
     * SecurityAttr unrealisedGainsAdjust.
     */
    SECURITYATTR_GAINSADJUST("SecurityAttr.GainsAdjust"),

    /**
     * SecurityAttr Dividend.
     */
    SECURITYATTR_DIVIDEND("SecurityAttr.Dividend"),

    /**
     * SecurityAttr MarketProfit.
     */
    SECURITYATTR_MARKETPROFIT("SecurityAttr.MarketProfit"),

    /**
     * SecurityAttr Profit.
     */
    SECURITYATTR_PROFIT("SecurityAttr.Profit"),

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
     * SecurityAttr StartDate.
     */
    SECURITYATTR_STARTDATE("SecurityAttr.StartDate"),

    /**
     * SecurityAttr Funded.
     */
    SECURITYATTR_FUNDED("SecurityAttr.Funded"),

    /**
     * SecurityAttr SliceGain.
     */
    SECURITYATTR_SLICEGAIN("SecurityAttr.SliceGain"),

    /**
     * SecurityAttr SliceYears.
     */
    SECURITYATTR_SLICEYEARS("SecurityAttr.SliceYears"),

    /**
     * SecurityAttr Consideration.
     */
    SECURITYATTR_CONSIDERATION("SecurityAttr.Consideration"),

    /**
     * SecurityAttr CostDilution.
     */
    SECURITYATTR_COSTDILUTION("SecurityAttr.CostDilution"),

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
    private static final Map<MoneyWiseXAnalysisAccountAttr, OceanusBundleId> ACCOUNT_MAP = buildAccountMap();

    /**
     * The TransactionAttr Map.
     */
    private static final Map<MoneyWiseXAnalysisTransAttr, OceanusBundleId> TRANSACTION_MAP = buildTransMap();

    /**
     * The PayeeAttr Map.
     */
    private static final Map<MoneyWiseXAnalysisPayeeAttr, OceanusBundleId> PAYEE_MAP = buildPayeeMap();

    /**
     * The SecurityAttr Map.
     */
    private static final Map<MoneyWiseXAnalysisSecurityAttr, OceanusBundleId> SECURITY_MAP = buildSecurityMap();

    /**
     * The TaxAttr Map.
     */
    private static final Map<MoneyWiseXAnalysisTaxBasisAttr, OceanusBundleId> TAX_MAP = buildTaxMap();

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(MoneyWiseXAnalysisValuesResource.class.getCanonicalName(),
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
    MoneyWiseXAnalysisValuesResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     *
     * @param pResource the underlying resource
     */
    MoneyWiseXAnalysisValuesResource(final OceanusBundleId pResource) {
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
    private static Map<MoneyWiseXAnalysisAccountAttr, OceanusBundleId> buildAccountMap() {
        /* Create the map and return it */
        final Map<MoneyWiseXAnalysisAccountAttr, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseXAnalysisAccountAttr.class);
        myMap.put(MoneyWiseXAnalysisAccountAttr.BALANCE, ACCOUNTATTR_BALANCE);
        myMap.put(MoneyWiseXAnalysisAccountAttr.VALUATION, ACCOUNTATTR_VALUATION);
        myMap.put(MoneyWiseXAnalysisAccountAttr.VALUEDELTA, ACCOUNTATTR_VALUEDELTA);
        myMap.put(MoneyWiseXAnalysisAccountAttr.DEPOSITRATE, ACCOUNTATTR_DEPOSITRATE);
        myMap.put(MoneyWiseXAnalysisAccountAttr.EXCHANGERATE, ACCOUNTATTR_EXCHANGERATE);
        myMap.put(MoneyWiseXAnalysisAccountAttr.MATURITY, ACCOUNTATTR_MATURITY);
        return myMap;
    }

    /**
     * Obtain key for account attribute.
     *
     * @param pValue the Value
     * @return the resource key
     */
    static OceanusBundleId getKeyForAccountAttr(final MoneyWiseXAnalysisAccountAttr pValue) {
        return OceanusBundleLoader.getKeyForEnum(ACCOUNT_MAP, pValue);
    }

    /**
     * Build transaction map.
     *
     * @return the map
     */
    private static Map<MoneyWiseXAnalysisTransAttr, OceanusBundleId> buildTransMap() {
        /* Create the map and return it */
        final Map<MoneyWiseXAnalysisTransAttr, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseXAnalysisTransAttr.class);
        myMap.put(MoneyWiseXAnalysisTransAttr.INCOME, PAYEEATTR_INCOME);
        myMap.put(MoneyWiseXAnalysisTransAttr.EXPENSE, PAYEEATTR_EXPENSE);
        myMap.put(MoneyWiseXAnalysisTransAttr.PROFIT, ACCOUNTATTR_PROFIT);
        return myMap;
    }

    /**
     * Obtain key for transaction attribute.
     *
     * @param pValue the Value
     * @return the resource key
     */
    protected static OceanusBundleId getKeyForTransactionAttr(final MoneyWiseXAnalysisTransAttr pValue) {
        return OceanusBundleLoader.getKeyForEnum(TRANSACTION_MAP, pValue);
    }

    /**
     * Build payee map.
     *
     * @return the map
     */
    private static Map<MoneyWiseXAnalysisPayeeAttr, OceanusBundleId> buildPayeeMap() {
        /* Create the map and return it */
        final Map<MoneyWiseXAnalysisPayeeAttr, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseXAnalysisPayeeAttr.class);
        myMap.put(MoneyWiseXAnalysisPayeeAttr.INCOME, PAYEEATTR_INCOME);
        myMap.put(MoneyWiseXAnalysisPayeeAttr.EXPENSE, PAYEEATTR_EXPENSE);
        myMap.put(MoneyWiseXAnalysisPayeeAttr.PROFIT, ACCOUNTATTR_PROFIT);
        return myMap;
    }

    /**
     * Obtain key for Payee attribute.
     *
     * @param pValue the Value
     * @return the resource key
     */
    static OceanusBundleId getKeyForPayeeAttr(final MoneyWiseXAnalysisPayeeAttr pValue) {
        return OceanusBundleLoader.getKeyForEnum(PAYEE_MAP, pValue);
    }

    /**
     * Build security map.
     *
     * @return the map
     */
    private static Map<MoneyWiseXAnalysisSecurityAttr, OceanusBundleId> buildSecurityMap() {
        /* Create the map and return it */
        final Map<MoneyWiseXAnalysisSecurityAttr, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseXAnalysisSecurityAttr.class);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.VALUE, SECURITYATTR_VALUE);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.VALUATION, ACCOUNTATTR_VALUATION);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.VALUEDELTA, ACCOUNTATTR_VALUEDELTA);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE, ACCOUNTATTR_EXCHANGERATE);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.UNITS, SECURITYATTR_UNITS);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST, SECURITYATTR_RESIDUALCOST);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.DIVIDEND, SECURITYATTR_DIVIDEND);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS, SECURITYATTR_REALISEDGAINS);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.UNREALISEDGAINS, SECURITYATTR_UNREALISEDGAINS);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.GAINSADJUST, SECURITYATTR_GAINSADJUST);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.MARKETPROFIT, SECURITYATTR_MARKETPROFIT);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.PROFIT, SECURITYATTR_PROFIT);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.RETURNEDCASH, SECURITYATTR_RETURNEDCASH);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.XFERREDVALUE, SECURITYATTR_XFERREDVALUE);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.XFERREDCOST, SECURITYATTR_XFERREDCOST);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.CASHINVESTED, SECURITYATTR_CASHINVESTED);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.CAPITALGAIN, SECURITYATTR_CAPITALGAIN);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.ALLOWEDCOST, SECURITYATTR_ALLOWEDCOST);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.PRICE, SECURITYATTR_PRICE);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.STARTDATE, SECURITYATTR_STARTDATE);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.FUNDED, SECURITYATTR_FUNDED);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.SLICEGAIN, SECURITYATTR_SLICEGAIN);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.SLICEYEARS, SECURITYATTR_SLICEYEARS);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.CONSIDERATION, SECURITYATTR_CONSIDERATION);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.COSTDILUTION, SECURITYATTR_COSTDILUTION);
        myMap.put(MoneyWiseXAnalysisSecurityAttr.CASHTYPE, SECURITYATTR_CASHTYPE);
        return myMap;
    }

    /**
     * Obtain key for security attribute.
     *
     * @param pValue the Value
     * @return the resource key
     */
    static OceanusBundleId getKeyForSecurityAttr(final MoneyWiseXAnalysisSecurityAttr pValue) {
        return OceanusBundleLoader.getKeyForEnum(SECURITY_MAP, pValue);
    }

    /**
     * Build taxBasis map.
     *
     * @return the map
     */
    private static Map<MoneyWiseXAnalysisTaxBasisAttr, OceanusBundleId> buildTaxMap() {
        /* Create the map and return it */
        final Map<MoneyWiseXAnalysisTaxBasisAttr, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseXAnalysisTaxBasisAttr.class);
        myMap.put(MoneyWiseXAnalysisTaxBasisAttr.GROSS, TAXATTR_GROSS);
        myMap.put(MoneyWiseXAnalysisTaxBasisAttr.NETT, TAXATTR_NETT);
        myMap.put(MoneyWiseXAnalysisTaxBasisAttr.TAXCREDIT, TAXATTR_TAX);
        return myMap;
    }

    /**
     * Obtain key for tax attribute.
     *
     * @param pValue the Value
     * @return the resource key
     */
    static OceanusBundleId getKeyForTaxAttr(final MoneyWiseXAnalysisTaxBasisAttr pValue) {
        return OceanusBundleLoader.getKeyForEnum(TAX_MAP, pValue);
    }
}
