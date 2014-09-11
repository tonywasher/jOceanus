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
package net.sourceforge.joceanus.jmoneywise.reports;

import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr.ResourceId;

/**
 * Resource IDs for jMoneyWise Report Fields.
 */
public enum ReportResource implements ResourceId {
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
     * TaxBasis Title.
     */
    TAXBASIS_TITLE("TaxBasis.Title"),

    /**
     * TaxCalc Title.
     */
    TAXCALC_TITLE("TaxCalc.Title");

    /**
     * The Bundle name.
     */
    private static final String BUNDLE_NAME = Analysis.class.getCanonicalName();

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    private ReportResource(final String pKeyName) {
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
    public String getBundleName() {
        return BUNDLE_NAME;
    }

    /**
     * Obtain key for report type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static ReportResource getKeyForReportType(final ReportType pValue) {
        switch (pValue) {
            case NETWORTH:
                return TYPE_NETWORTH;
            case BALANCESHEET:
                return TYPE_BALANCESHEET;
            case CASHFLOW:
                return TYPE_CASHFLOW;
            case INCOMEEXPENSE:
                return TYPE_INCEXP;
            case PORTFOLIO:
                return TYPE_PORTFOLIO;
            case MARKETGROWTH:
                return TYPE_MARKET;
            case TAXBASIS:
                return TYPE_TAXBASIS;
            case TAXCALC:
                return TYPE_TAXCALC;
            default:
                return null;
        }
    }
}
