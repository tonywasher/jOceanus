/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.analysis.data;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataTypeResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.StaticDataResource;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for MoneyWise Analysis Data Fields.
 */
public enum MoneyWiseXAnalysisDataResource
        implements TethysBundleId, MetisDataFieldId {
    /**
     * AccountAttr Valuation.
     */
    ACCOUNTATTR_VALUATION("AccountAttr.Valuation"),

    /**
     * AccountAttr DepositRate.
     */
    ACCOUNTATTR_DEPOSITRATE(MoneyWiseDataResource.MONEYWISEDATA_FIELD_RATE),

    /**
     * AccountAttr Profit.
     */
    ACCOUNTATTR_PROFIT("AccountAttr.Profit"),

    /**
     * AccountAttr LocalValue.
     */
    ACCOUNTATTR_LOCALVALUE("AccountAttr.LocalValue"),

    /**
     * AccountAttr ExchangeRate.
     */
    ACCOUNTATTR_EXCHANGERATE(MoneyWiseDataTypeResource.XCHGRATE_NAME),

    /**
     * AccountAttr Maturity.
     */
    ACCOUNTATTR_MATURITY("AccountAttr.Maturity"),

    /**
     * AccountAttr Valuation.
     */
    ACCOUNTATTR_SPEND("AccountAttr.Spend"),

    /**
     * AccountAttr BadDebtCapital.
     */
    ACCOUNTATTR_BADDEBTCAPITAL(StaticDataResource.TRANSTYPE_BADDEBTCAPITAL),

    /**
     * AccountAttr BadDebtInterest.
     */
    ACCOUNTATTR_BADDEBTINTEREST(StaticDataResource.TRANSTYPE_BADDEBTINTEREST),

    /**
     * IncomeAttr Valuation.
     */
    INCOMEATTR_INCOME("IncomeAttr.Income"),

    /**
     * IncomeAttr Valuation.
     */
    INCOMEATTR_EXPENSE("IncomeAttr.Expense"),

    /**
     * SecurityAttr Units.
     */
    SECURITYATTR_UNITS(MoneyWiseDataResource.MONEYWISEDATA_FIELD_UNITS),

    /**
     * SecurityAttr ResidualCost.
     */
    SECURITYATTR_RESIDUALCOST("SecurityAttr.ResidualCost"),

    /**
     * SecurityAttr RealisedGains.
     */
    SECURITYATTR_REALISEDGAINS("SecurityAttr.RealisedGains"),

    /**
     * SecurityAttr Invested.
     */
    SECURITYATTR_INVESTED("SecurityAttr.Invested"),

    /**
     * SecurityAttr Dividend.
     */
    SECURITYATTR_DIVIDEND("SecurityAttr.Dividend"),

    /**
     * SecurityAttr Price.
     */
    SECURITYATTR_PRICE(MoneyWiseDataResource.MONEYWISEDATA_FIELD_PRICE),

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
     * Analysis Events.
     */
    ANALYSIS_EVENTS("Analysis.Events"),

    /**
     * Analysis Deposits.
     */
    ANALYSIS_DEPOSITS("Analysis.Deposits"),

    /**
     * Analysis Cash.
     */
    ANALYSIS_CASH("Analysis.Cash"),

    /**
     * Analysis Loans.
     */
    ANALYSIS_LOANS("Analysis.Loans"),

    /**
     * Analysis Portfolios.
     */
    ANALYSIS_PORTFOLIOS("Analysis.Portfolios"),

    /**
     * Analysis Holdings.
     */
    ANALYSIS_HOLDINGS("Analysis.Holdings"),

    /**
     * Analysis Payees.
     */
    ANALYSIS_PAYEES("Analysis.Payees"),

    /**
     * Analysis Transactions.
     */
    ANALYSIS_TRANS("Analysis.Transactions"),

    /**
     * Analysis TaxBases.
     */
    ANALYSIS_TAXBASES("Analysis.TaxBases"),

    /**
     * Analysis TaxBases.
     */
    ANALYSIS_CURRENCY("Analysis.Currency");

    /**
     * The AccountAttr Map.
     */
    private static final Map<MoneyWiseXAccountAttr, TethysBundleId> ACCOUNT_MAP = buildAccountMap();

    /**
     * The IncomeAttr Map.
     */
    private static final Map<MoneyWiseXIncomeAttr, TethysBundleId> INCOME_MAP = buildIncomeMap();

    /**
     * The SecurityAttr Map.
     */
    private static final Map<MoneyWiseXSecurityAttr, TethysBundleId> SECURITY_MAP = buildSecurityMap();

    /**
     * The TaxAttr Map.
     */
    private static final Map<MoneyWiseXTaxBasisAttr, TethysBundleId> TAX_MAP = buildTaxMap();

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getPackageLoader(MoneyWiseXAnalysisDataResource.class.getCanonicalName(),
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
    MoneyWiseXAnalysisDataResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     * @param pResource the underlying resource
     */
    MoneyWiseXAnalysisDataResource(final TethysBundleId pResource) {
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
     * @return the map
     */
    private static Map<MoneyWiseXAccountAttr, TethysBundleId> buildAccountMap() {
        /* Create the map and return it */
        final Map<MoneyWiseXAccountAttr, TethysBundleId> myMap = new EnumMap<>(MoneyWiseXAccountAttr.class);
        myMap.put(MoneyWiseXAccountAttr.VALUATION, ACCOUNTATTR_VALUATION);
        myMap.put(MoneyWiseXAccountAttr.LOCALVALUE, ACCOUNTATTR_LOCALVALUE);
        myMap.put(MoneyWiseXAccountAttr.DEPOSITRATE, ACCOUNTATTR_DEPOSITRATE);
        myMap.put(MoneyWiseXAccountAttr.EXCHANGERATE, ACCOUNTATTR_EXCHANGERATE);
        myMap.put(MoneyWiseXAccountAttr.MATURITY, ACCOUNTATTR_MATURITY);
        myMap.put(MoneyWiseXAccountAttr.SPEND, ACCOUNTATTR_SPEND);
        myMap.put(MoneyWiseXAccountAttr.BADDEBTCAPITAL, ACCOUNTATTR_BADDEBTCAPITAL);
        myMap.put(MoneyWiseXAccountAttr.BADDEBTINTEREST, ACCOUNTATTR_BADDEBTINTEREST);
        return myMap;
    }

    /**
     * Obtain key for account attribute.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForAccountAttr(final MoneyWiseXAccountAttr pValue) {
        return TethysBundleLoader.getKeyForEnum(ACCOUNT_MAP, pValue);
    }
    /**
     * Build payee map.
     * @return the map
     */
    private static Map<MoneyWiseXIncomeAttr, TethysBundleId> buildIncomeMap() {
        /* Create the map and return it */
        final Map<MoneyWiseXIncomeAttr, TethysBundleId> myMap = new EnumMap<>(MoneyWiseXIncomeAttr.class);
        myMap.put(MoneyWiseXIncomeAttr.INCOME, INCOMEATTR_INCOME);
        myMap.put(MoneyWiseXIncomeAttr.EXPENSE, INCOMEATTR_EXPENSE);
        myMap.put(MoneyWiseXIncomeAttr.PROFIT, ACCOUNTATTR_PROFIT);
        return myMap;
    }

    /**
     * Obtain key for Income attribute.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForIncomeAttr(final MoneyWiseXIncomeAttr pValue) {
        return TethysBundleLoader.getKeyForEnum(INCOME_MAP, pValue);
    }
    /**
     * Build security map.
     * @return the map
     */
    private static Map<MoneyWiseXSecurityAttr, TethysBundleId> buildSecurityMap() {
        /* Create the map and return it */
        final Map<MoneyWiseXSecurityAttr, TethysBundleId> myMap = new EnumMap<>(MoneyWiseXSecurityAttr.class);
        myMap.put(MoneyWiseXSecurityAttr.VALUATION, ACCOUNTATTR_VALUATION);
        myMap.put(MoneyWiseXSecurityAttr.EXCHANGERATE, ACCOUNTATTR_EXCHANGERATE);
        myMap.put(MoneyWiseXSecurityAttr.UNITS, SECURITYATTR_UNITS);
        myMap.put(MoneyWiseXSecurityAttr.RESIDUALCOST, SECURITYATTR_RESIDUALCOST);
        myMap.put(MoneyWiseXSecurityAttr.REALISEDGAINS, SECURITYATTR_REALISEDGAINS);
        myMap.put(MoneyWiseXSecurityAttr.INVESTED, SECURITYATTR_INVESTED);
        myMap.put(MoneyWiseXSecurityAttr.DIVIDEND, SECURITYATTR_DIVIDEND);
        myMap.put(MoneyWiseXSecurityAttr.PRICE, SECURITYATTR_PRICE);
        return myMap;
    }

    /**
     * Obtain key for security attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysBundleId getKeyForSecurityAttr(final MoneyWiseXSecurityAttr pValue) {
        return TethysBundleLoader.getKeyForEnum(SECURITY_MAP, pValue);
    }

    /**
     * Build taxBasis map.
     * @return the map
     */
    private static Map<MoneyWiseXTaxBasisAttr, TethysBundleId> buildTaxMap() {
        /* Create the map and return it */
        final Map<MoneyWiseXTaxBasisAttr, TethysBundleId> myMap = new EnumMap<>(MoneyWiseXTaxBasisAttr.class);
        myMap.put(MoneyWiseXTaxBasisAttr.GROSS, TAXATTR_GROSS);
        myMap.put(MoneyWiseXTaxBasisAttr.NETT, TAXATTR_NETT);
        myMap.put(MoneyWiseXTaxBasisAttr.TAXCREDIT, TAXATTR_TAX);
        return myMap;
    }

    /**
     * Obtain key for tax attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysBundleId getKeyForTaxAttr(final MoneyWiseXTaxBasisAttr pValue) {
        return TethysBundleLoader.getKeyForEnum(TAX_MAP, pValue);
    }
}
