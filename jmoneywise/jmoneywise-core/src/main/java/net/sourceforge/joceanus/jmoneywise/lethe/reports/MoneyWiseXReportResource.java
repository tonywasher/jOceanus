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
package net.sourceforge.joceanus.jmoneywise.lethe.reports;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for jMoneyWise Report Fields.
 */
public enum MoneyWiseXReportResource implements TethysBundleId {
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
    private static final Map<MoneyWiseXReportType, TethysBundleId> REPORT_MAP = buildReportMap();

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getLoader(Analysis.class.getCanonicalName(),
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
    MoneyWiseXReportResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMoneyWise.report";
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
    private static Map<MoneyWiseXReportType, TethysBundleId> buildReportMap() {
        /* Create the map and return it */
        final Map<MoneyWiseXReportType, TethysBundleId> myMap = new EnumMap<>(MoneyWiseXReportType.class);
        myMap.put(MoneyWiseXReportType.NETWORTH, TYPE_NETWORTH);
        myMap.put(MoneyWiseXReportType.BALANCESHEET, TYPE_BALANCESHEET);
        myMap.put(MoneyWiseXReportType.CASHFLOW, TYPE_CASHFLOW);
        myMap.put(MoneyWiseXReportType.INCOMEEXPENSE, TYPE_INCEXP);
        myMap.put(MoneyWiseXReportType.PORTFOLIO, TYPE_PORTFOLIO);
        myMap.put(MoneyWiseXReportType.MARKETGROWTH, TYPE_MARKET);
        myMap.put(MoneyWiseXReportType.TAXBASIS, TYPE_TAXBASIS);
        myMap.put(MoneyWiseXReportType.TAXCALC, TYPE_TAXCALC);
        myMap.put(MoneyWiseXReportType.ASSETGAINS, TYPE_ASSETGAINS);
        myMap.put(MoneyWiseXReportType.CAPITALGAINS, TYPE_CAPITALGAINS);
        return myMap;
    }

    /**
     * Obtain key for report type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysBundleId getKeyForReportType(final MoneyWiseXReportType pValue) {
        return TethysBundleLoader.getKeyForEnum(REPORT_MAP, pValue);
    }
}
