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
package net.sourceforge.joceanus.moneywise.ui;

import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleLoader;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Resource IDs for MoneyWise UI Fields.
 */
public enum MoneyWiseUIResource
        implements OceanusBundleId, MetisDataFieldId {
    /**
     * Frozen ToolTip.
     */
    ICON_FROZEN("icons.Tip.Frozen"),

    /**
     * Reconcile ToolTip.
     */
    ICON_RECONCILE("icons.Tip.Reconcile"),

    /**
     * Release ToolTip.
     */
    ICON_RELEASE("icons.Tip.Release"),

    /**
     * Locked ToolTip.
     */
    ICON_LOCKED("icons.Tip.Locked"),

    /**
     * Lock ToolTip.
     */
    ICON_LOCK("icons.Tip.Lock"),

    /**
     * UnLock ToolTip.
     */
    ICON_UNLOCK("icons.Tip.UnLock"),

    /**
     * DirectionTo ToolTip.
     */
    ICON_DIRTO("icons.Tip.DirectionTo"),

    /**
     * DirectionFrom ToolTip.
     */
    ICON_DIRFROM("icons.Tip.DirectionFrom"),

    /**
     * Report Prompt.
     */
    REPORT_PROMPT("report.Prompt.Select"),

    /**
     * Report Prompt.
     */
    REPORT_TITLE("report.Title.Select"),

    /**
     * ShowCLosed Prompt.
     */
    UI_PROMPT_SHOWCLOSED("Prompt.ShowClosed"),

    /**
     * SpotPrice Date Prompt.
     */
    SPOTEVENT_DATE("spotEvent.Prompt.Date"),

    /**
     * SpotPrice Next Button.
     */
    SPOTPRICE_NEXT("spotPrice.ToolTip.Next"),

    /**
     * SpotPrice Previous Button.
     */
    SPOTPRICE_PREV("spotPrice.ToolTip.Prev"),

    /**
     * SpotPrice Title.
     */
    SPOTPRICE_TITLE("spotPrice.Title.Select"),

    /**
     * SpotRate Currency Prompt.
     */
    SPOTRATE_PROMPT_CURR("spotRate.Prompt.Currency"),

    /**
     * SpotRate Next Button.
     */
    SPOTRATE_NEXT("spotRate.ToolTip.Next"),

    /**
     * SpotRate Previous Button.
     */
    SPOTRATE_PREV("spotRate.ToolTip.Prev"),

    /**
     * SpotPrice Title.
     */
    SPOTRATE_TITLE("spotRate.Title.Select"),

    /**
     * AnalysisSelect Title.
     */
    ANALYSIS_TITLE("analysisSelect.Title.Analysis"),

    /**
     * AnalysisSelect Filter Title.
     */
    ANALYSIS_FILTER_TITLE("analysisSelect.Title.Filter"),

    /**
     * AnalysisSelect DateRange Prompt.
     */
    ANALYSIS_PROMPT_RANGE("analysisSelect.Prompt.DateRange"),

    /**
     * AnalysisSelect Filter Prompt.
     */
    ANALYSIS_PROMPT_FILTER("analysisSelect.Prompt.Filter"),

    /**
     * AnalysisSelect FilterType Prompt.
     */
    ANALYSIS_PROMPT_FILTERTYPE("analysisSelect.Prompt.FilterType"),

    /**
     * AnalysisSelect Bucket Prompt.
     */
    ANALYSIS_PROMPT_BUCKET("analysisSelect.Prompt.Bucket"),

    /**
     * AnalysisSelect ColumnSet Prompt.
     */
    ANALYSIS_PROMPT_COLUMNSET("analysisSelect.Prompt.ColumnSet"),

    /**
     * AnalysisSelect Bucket Prompt.
     */
    ANALYSIS_BUCKET_NONE("analysisSelect.Bucket.None"),

    /**
     * Action Column.
     */
    COLUMN_ACTION("Column.Action"),

    /**
     * DepositRates Tab.
     */
    DEPOSITPANEL_TAB_RATES("DepositPanel.Tab.Rates"),

    /**
     * OptionVests Tab.
     */
    OPTIONPANEL_TAB_VESTS("OptionPanel.Tab.Vests"),

    /**
     * SecurityPrices Tab.
     */
    SECURITYPANEL_TAB_PRICES("SecurityPanel.Tab.Prices"),

    /**
     * Transaction Info Tab.
     */
    TRANSPANEL_TAB_INFO("TransPanel.Tab.Info"),

    /**
     * Transaction Taxes Tab.
     */
    TRANSPANEL_TAB_TAXES("TransPanel.Tab.Taxes"),

    /**
     * Transaction Securities Tab.
     */
    TRANSPANEL_TAB_SECURITIES("TransPanel.Tab.Securities"),

    /**
     * Transaction Returned Tab.
     */
    TRANSPANEL_TAB_RETURNED("TransPanel.Tab.Returned"),

    /**
     * Balance ColumnSet.
     */
    COLUMNSET_BALANCE("analysisColumnSet.Balance"),

    /**
     * Standard ColumnSet.
     */
    COLUMNSET_STANDARD("analysisColumnSet.Standard"),

    /**
     * Salary ColumnSet.
     */
    COLUMNSET_SALARY("analysisColumnSet.Salary"),

    /**
     * Interest ColumnSet.
     */
    COLUMNSET_INTEREST("analysisColumnSet.Interest"),

    /**
     * Dividend ColumnSet.
     */
    COLUMNSET_DIVIDEND("analysisColumnSet.Dividend"),

    /**
     * Security ColumnSet.
     */
    COLUMNSET_SECURITY("analysisColumnSet.Security"),

    /**
     * All ColumnSet.
     */
    COLUMNSET_ALL("analysisColumnSet.All"),

    /**
     * Latest Activity Column Title.
     */
    ASSET_COLUMN_LATEST("Asset.Column.LastTran"),

    /**
     * Asset DataEntry.
     */
    ASSET_DATAENTRY("Asset.DataEntry"),

    /**
     * Asset Selection Title.
     */
    ASSET_TITLE_SELECT("Asset.Title.Select"),

    /**
     * Asset Selection Prompt.
     */
    ASSET_PROMPT_SELECT("Asset.Prompt.Select"),

    /**
     * Category DataEntry.
     */
    CATEGORY_DATAENTRY("Category.DataEntry"),

    /**
     * Category Selection Title.
     */
    CATEGORY_TITLE_SELECT("Category.Title.Select"),

    /**
     * Category Selection Prompt.
     */
    CATEGORY_PROMPT_SELECT("Category.Prompt.Select"),

    /**
     * Category Full Name Column Title.
     */
    CATEGORY_COLUMN_FULLNAME("Category.Column.FullName"),

    /**
     * Category Filter Prompt.
     */
    CATEGORY_PROMPT_FILTER("Category.Prompt.Filter"),

    /**
     * Category Filter Parents Text.
     */
    CATEGORY_FILTER_PARENT("Category.Filter.Parent"),

    /**
     * Category Filter ShowAll Text.
     */
    CATEGORY_FILTER_SHOWALL("Category.Filter.All"),

    /**
     * Report DataEntry.
     */
    REPORT_DATAENTRY("Report.DataEntry"),

    /**
     * SpotPrices DataEntry.
     */
    PRICES_DATAENTRY("SpotPrices.DataEntry"),

    /**
     * SpotRates DataEntry.
     */
    RATES_DATAENTRY("SpotRates.DataEntry"),

    /**
     * Register DataEntry.
     */
    REGISTER_DATAENTRY("Register.DataEntry"),

    /**
     * Filter DataEntry.
     */
    FILTER_DATAENTRY("Filter.DataEntry"),

    /**
     * Transaction DataEntry.
     */
    TRANSACTION_DATAENTRY("Transactions.DataEntry"),

    /**
     * Statement Column Direction.
     */
    STATEMENT_COLUMN_DIRECTION("Statement.Column.Direction"),

    /**
     * Statement Column Debit.
     */
    STATEMENT_COLUMN_DEBIT("Statement.Column.Debit"),

    /**
     * Statement Column Credit.
     */
    STATEMENT_COLUMN_CREDIT("Statement.Column.Credit"),

    /**
     * Statement Column Balance.
     */
    STATEMENT_COLUMN_BALANCE("Statement.Column.Balance"),

    /**
     * Statement Column Reconciled.
     */
    STATEMENT_COLUMN_RECONCILED("Statement.Column.Reconciled"),

    /**
     * Statement Column Years.
     */
    STATEMENT_COLUMN_YEARS("Statement.Column.Years"),

    /**
     * Statement Opening Balance Text.
     */
    STATEMENT_OPENINGBALANCE("Statement.Text.OpeningBalance"),

    /**
     * SpotRate Column Symbol.
     */
    SPOTRATE_COLUMN_SYMBOL("spotRate.Column.Symbol"),

    /**
     * Maintenance Account Tab.
     */
    MAINTENANCE_ACCOUNT("Maintenance.Tab.Account"),

    /**
     * Maintenance Category Tab.
     */
    MAINTENANCE_CATEGORY("Maintenance.Tab.Category"),

    /**
     * Maintenance Static Tab.
     */
    MAINTENANCE_STATIC("Maintenance.Tab.Static"),

    /**
     * Maintenance Settings Tab.
     */
    MAINTENANCE_SETTINGS("Maintenance.Tab.Preference"),

    /**
     * Main Register Tab.
     */
    MAIN_REGISTER("Main.Tab.Register"),

    /**
     * Main Report Tab.
     */
    MAIN_REPORT("Main.Tab.Report"),

    /**
     * Main Market Tab.
     */
    MAIN_MARKET("Main.Tab.Market"),

    /**
     * Main Maintenance Tab.
     */
    MAIN_MAINTENANCE("Main.Tab.Maint"),

    /**
     * Main Log Tab.
     */
    MAIN_LOG("Main.Tab.Log"),

    /**
     * Market Prices Tab.
     */
    MARKET_PRICES("Market.Tab.Prices"),

    /**
     * Market Rates Tab.
     */
    MARKET_RATES("Market.Tab.Rates"),

    /**
     * Main Menu Backup SVN.
     */
    MAIN_MENU_BACKUPSVN("Main.Menu.BackupSVN"),

    /**
     * Main Menu Restore SVN.
     */
    MAIN_MENU_RESTORESVN("Main.Menu.RestoreSVN"),

    /**
     * Main Menu Load Archive.
     */
    MAIN_MENU_LOADARCHIVE("Main.Menu.Archive"),

    /**
     * Main Menu CreateQIF.
     */
    MAIN_MENU_CREATEQIF("Main.Menu.CreateQIF"),

    /**
     * StaticData ShowDisabled.
     */
    STATICDATA_DISABLED("StaticData.Disabled"),

    /**
     * StaticData Selection.
     */
    STATICDATA_SELECTION("StaticData.Selection"),

    /**
     * StaticData Select.
     */
    STATICDATA_SELECT("StaticData.Select"),

    /**
     * StaticData Active.
     */
    STATICDATA_ACTIVE("StaticData.Active");

    /**
     * The ColumnSet Map.
     */
    private static final Map<MoneyWiseAnalysisColumnSet, OceanusBundleId> COLUMN_MAP = buildColumnMap();

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(MoneyWiseUIResource.class.getCanonicalName(),
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
    MoneyWiseUIResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "MoneyWise.ui";
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
     * Build column map.
     *
     * @return the map
     */
    private static Map<MoneyWiseAnalysisColumnSet, OceanusBundleId> buildColumnMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAnalysisColumnSet, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseAnalysisColumnSet.class);
        myMap.put(MoneyWiseAnalysisColumnSet.BALANCE, COLUMNSET_BALANCE);
        myMap.put(MoneyWiseAnalysisColumnSet.STANDARD, COLUMNSET_STANDARD);
        myMap.put(MoneyWiseAnalysisColumnSet.SALARY, COLUMNSET_SALARY);
        myMap.put(MoneyWiseAnalysisColumnSet.INTEREST, COLUMNSET_INTEREST);
        myMap.put(MoneyWiseAnalysisColumnSet.DIVIDEND, COLUMNSET_DIVIDEND);
        myMap.put(MoneyWiseAnalysisColumnSet.SECURITY, COLUMNSET_SECURITY);
        myMap.put(MoneyWiseAnalysisColumnSet.ALL, COLUMNSET_ALL);
        return myMap;
    }

    /**
     * Obtain key for columnSet.
     *
     * @param pValue the Value
     * @return the resource key
     */
    static OceanusBundleId getKeyForColumnSet(final MoneyWiseAnalysisColumnSet pValue) {
        return OceanusBundleLoader.getKeyForEnum(COLUMN_MAP, pValue);
    }
}
