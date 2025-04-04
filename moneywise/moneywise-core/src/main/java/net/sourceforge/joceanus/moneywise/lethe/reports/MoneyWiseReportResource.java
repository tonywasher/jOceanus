/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.lethe.reports;

import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDataResource;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleId;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleLoader;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Resource IDs for MoneyWise Report Fields.
 */
public enum MoneyWiseReportResource implements OceanusBundleId {
    /**
     * NetWorth ReportType.
     */
    TYPE_NETWORTH("Type.NetWorth"),

    /**
     * BalanceSheet ReportType.
     */
    TYPE_BALANCESHEET("Type.BalanceSheet"),

    /**
     * CashFlow ReportType.
     */
    TYPE_CASHFLOW("Type.CashFlow"),

    /**
     * IncomeExpense ReportType.
     */
    TYPE_INCEXP("Type.IncExp"),

    /**
     * Portfolio ReportType.
     */
    TYPE_PORTFOLIO("Type.Portfolio"),

    /**
     * MarketGrowth ReportType.
     */
    TYPE_MARKET("Type.Market"),

    /**
     * TaxBasis ReportType.
     */
    TYPE_TAXBASIS("Type.TaxBasis"),

    /**
     * TaxCalc ReportType.
     */
    TYPE_TAXCALC("Type.TaxCalc"),

    /**
     * AssetGains ReportType.
     */
    TYPE_ASSETGAINS("Type.AssetGains"),

    /**
     * CapitalGains ReportType.
     */
    TYPE_CAPITALGAINS("Type.CapitalGains"),

    /**
     * NetWorth Title.
     */
    NETWORTH_TITLE("NetWorth.Title"),

    /**
     * NetWorth Asset.
     */
    NETWORTH_ASSET("NetWorth.Asset"),

    /**
     * BalanceSheet Title.
     */
    BALANCESHEET_TITLE("BalanceSheet.Title"),

    /**
     * CashFlow Title.
     */
    CASHFLOW_TITLE("CashFlow.Title"),

    /**
     * Income/Expense Title.
     */
    INCEXP_TITLE("IncExp.Title"),

    /**
     * Portfolio Title.
     */
    PORTFOLIO_TITLE("Portfolio.Title"),

    /**
     * MarketGrowth Title.
     */
    MARKETGROWTH_TITLE("MarketGrowth.Title"),

    /**
     * MarketGrowth BaseValue.
     */
    MARKETGROWTH_BASE("MarketGrowth.Base"),

    /**
     * AssetGains Title.
     */
    ASSETGAINS_TITLE("AssetGains.Title"),

    /**
     * CapitalGains Title.
     */
    CAPITALGAINS_TITLE("CapitalGains.Title"),

    /**
     * TaxBasis Title.
     */
    TAXBASIS_TITLE("TaxBasis.Title"),

    /**
     * TaxCalc Title.
     */
    TAXCALC_TITLE("TaxCalc.Title");

    /**
     * The Report Map.
     */
    private static final Map<MoneyWiseReportType, OceanusBundleId> REPORT_MAP = buildReportMap();

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(MoneyWiseAnalysisDataResource.class.getCanonicalName(),
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
    MoneyWiseReportResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "MoneyWise.report";
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

    /**
     * Build report map.
     * @return the map
     */
    private static Map<MoneyWiseReportType, OceanusBundleId> buildReportMap() {
        /* Create the map and return it */
        final Map<MoneyWiseReportType, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseReportType.class);
        myMap.put(MoneyWiseReportType.NETWORTH, TYPE_NETWORTH);
        myMap.put(MoneyWiseReportType.BALANCESHEET, TYPE_BALANCESHEET);
        myMap.put(MoneyWiseReportType.CASHFLOW, TYPE_CASHFLOW);
        myMap.put(MoneyWiseReportType.INCOMEEXPENSE, TYPE_INCEXP);
        myMap.put(MoneyWiseReportType.PORTFOLIO, TYPE_PORTFOLIO);
        myMap.put(MoneyWiseReportType.MARKETGROWTH, TYPE_MARKET);
        myMap.put(MoneyWiseReportType.TAXBASIS, TYPE_TAXBASIS);
        myMap.put(MoneyWiseReportType.TAXCALC, TYPE_TAXCALC);
        myMap.put(MoneyWiseReportType.ASSETGAINS, TYPE_ASSETGAINS);
        myMap.put(MoneyWiseReportType.CAPITALGAINS, TYPE_CAPITALGAINS);
        return myMap;
    }

    /**
     * Obtain key for report type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static OceanusBundleId getKeyForReportType(final MoneyWiseReportType pValue) {
        return OceanusBundleLoader.getKeyForEnum(REPORT_MAP, pValue);
    }
}
