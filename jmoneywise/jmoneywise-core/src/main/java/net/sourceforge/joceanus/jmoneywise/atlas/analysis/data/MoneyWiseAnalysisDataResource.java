/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
public enum MoneyWiseAnalysisDataResource
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
    private static final Map<MoneyWiseAccountAttr, TethysBundleId> ACCOUNT_MAP = buildAccountMap();

    /**
     * The IncomeAttr Map.
     */
    private static final Map<MoneyWiseIncomeAttr, TethysBundleId> INCOME_MAP = buildIncomeMap();

    /**
     * The SecurityAttr Map.
     */
    private static final Map<MoneyWiseSecurityAttr, TethysBundleId> SECURITY_MAP = buildSecurityMap();

    /**
     * The TaxAttr Map.
     */
    private static final Map<MoneyWiseTaxBasisAttr, TethysBundleId> TAX_MAP = buildTaxMap();

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getPackageLoader(MoneyWiseAnalysisDataResource.class.getCanonicalName(),
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
    private static Map<MoneyWiseAccountAttr, TethysBundleId> buildAccountMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAccountAttr, TethysBundleId> myMap = new EnumMap<>(MoneyWiseAccountAttr.class);
        myMap.put(MoneyWiseAccountAttr.VALUATION, ACCOUNTATTR_VALUATION);
        myMap.put(MoneyWiseAccountAttr.LOCALVALUE, ACCOUNTATTR_LOCALVALUE);
        myMap.put(MoneyWiseAccountAttr.DEPOSITRATE, ACCOUNTATTR_DEPOSITRATE);
        myMap.put(MoneyWiseAccountAttr.EXCHANGERATE, ACCOUNTATTR_EXCHANGERATE);
        myMap.put(MoneyWiseAccountAttr.MATURITY, ACCOUNTATTR_MATURITY);
        myMap.put(MoneyWiseAccountAttr.SPEND, ACCOUNTATTR_SPEND);
        myMap.put(MoneyWiseAccountAttr.BADDEBTCAPITAL, ACCOUNTATTR_BADDEBTCAPITAL);
        myMap.put(MoneyWiseAccountAttr.BADDEBTINTEREST, ACCOUNTATTR_BADDEBTINTEREST);
        return myMap;
    }

    /**
     * Obtain key for account attribute.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForAccountAttr(final MoneyWiseAccountAttr pValue) {
        return TethysBundleLoader.getKeyForEnum(ACCOUNT_MAP, pValue);
    }
    /**
     * Build payee map.
     * @return the map
     */
    private static Map<MoneyWiseIncomeAttr, TethysBundleId> buildIncomeMap() {
        /* Create the map and return it */
        final Map<MoneyWiseIncomeAttr, TethysBundleId> myMap = new EnumMap<>(MoneyWiseIncomeAttr.class);
        myMap.put(MoneyWiseIncomeAttr.INCOME, INCOMEATTR_INCOME);
        myMap.put(MoneyWiseIncomeAttr.EXPENSE, INCOMEATTR_EXPENSE);
        myMap.put(MoneyWiseIncomeAttr.PROFIT, ACCOUNTATTR_PROFIT);
        return myMap;
    }

    /**
     * Obtain key for Income attribute.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForIncomeAttr(final MoneyWiseIncomeAttr pValue) {
        return TethysBundleLoader.getKeyForEnum(INCOME_MAP, pValue);
    }
    /**
     * Build security map.
     * @return the map
     */
    private static Map<MoneyWiseSecurityAttr, TethysBundleId> buildSecurityMap() {
        /* Create the map and return it */
        final Map<MoneyWiseSecurityAttr, TethysBundleId> myMap = new EnumMap<>(MoneyWiseSecurityAttr.class);
        myMap.put(MoneyWiseSecurityAttr.VALUATION, ACCOUNTATTR_VALUATION);
        myMap.put(MoneyWiseSecurityAttr.EXCHANGERATE, ACCOUNTATTR_EXCHANGERATE);
        myMap.put(MoneyWiseSecurityAttr.UNITS, SECURITYATTR_UNITS);
        myMap.put(MoneyWiseSecurityAttr.RESIDUALCOST, SECURITYATTR_RESIDUALCOST);
        myMap.put(MoneyWiseSecurityAttr.REALISEDGAINS, SECURITYATTR_REALISEDGAINS);
        myMap.put(MoneyWiseSecurityAttr.INVESTED, SECURITYATTR_INVESTED);
        myMap.put(MoneyWiseSecurityAttr.DIVIDEND, SECURITYATTR_DIVIDEND);
        myMap.put(MoneyWiseSecurityAttr.PRICE, SECURITYATTR_PRICE);
        return myMap;
    }

    /**
     * Obtain key for security attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysBundleId getKeyForSecurityAttr(final MoneyWiseSecurityAttr pValue) {
        return TethysBundleLoader.getKeyForEnum(SECURITY_MAP, pValue);
    }

    /**
     * Build taxBasis map.
     * @return the map
     */
    private static Map<MoneyWiseTaxBasisAttr, TethysBundleId> buildTaxMap() {
        /* Create the map and return it */
        final Map<MoneyWiseTaxBasisAttr, TethysBundleId> myMap = new EnumMap<>(MoneyWiseTaxBasisAttr.class);
        myMap.put(MoneyWiseTaxBasisAttr.GROSS, TAXATTR_GROSS);
        myMap.put(MoneyWiseTaxBasisAttr.NETT, TAXATTR_NETT);
        myMap.put(MoneyWiseTaxBasisAttr.TAXCREDIT, TAXATTR_TAX);
        return myMap;
    }

    /**
     * Obtain key for tax attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysBundleId getKeyForTaxAttr(final MoneyWiseTaxBasisAttr pValue) {
        return TethysBundleLoader.getKeyForEnum(TAX_MAP, pValue);
    }
}
