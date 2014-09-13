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
package net.sourceforge.joceanus.jmoneywise.ui;

import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr.ResourceId;

/**
 * Resource IDs for jMoneyWise UI Fields.
 */
public enum MoneyWiseUIResource implements ResourceId {
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
     * Statement Opening Balance Text.
     */
    STATEMENT_OPENINGBALANCE("Statement.Text.OpeningBalance"),

    /**
     * Maintenance Account Tab.
     */
    MAINTENANCE_ACCOUNT("Maintenance.Tab.Account"),

    /**
     * Maintenance Category Tab.
     */
    MAINTENANCE_CATEGORY("Maintenance.Tab.Category"),

    /**
     * Maintenance TaxYear Tab.
     */
    MAINTENANCE_TAXYEAR("Maintenance.Tab.TaxYear"),

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
     * Main Statement Tab.
     */
    MAIN_STATEMENT("Main.Tab.Statement"),

    /**
     * Main Report Tab.
     */
    MAIN_REPORT("Main.Tab.Report"),

    /**
     * Main SpotPrice Tab.
     */
    MAIN_SPOTPRICE("Main.Tab.SpotPrice"),

    /**
     * Main Maintenance Tab.
     */
    MAIN_MAINTENANCE("Main.Tab.Maint"),

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
    MAIN_MENU_CREATEQIF("Main.Menu.CreateQIF");

    /**
     * The Bundle name.
     */
    private static final String BUNDLE_NAME = MoneyWiseIcons.class.getCanonicalName();

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    private MoneyWiseUIResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMoneyWise.ui";
    }

    @Override
    public String getBundleName() {
        return BUNDLE_NAME;
    }

}
