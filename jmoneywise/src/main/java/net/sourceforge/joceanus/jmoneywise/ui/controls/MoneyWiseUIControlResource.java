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
package net.sourceforge.joceanus.jmoneywise.ui.controls;

import net.sourceforge.joceanus.jtethys.resource.ResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.ResourceId;

/**
 * Resource IDs for jMoneyWise UI Control Fields.
 */
public enum MoneyWiseUIControlResource implements ResourceId {
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
     * Report Prompt.
     */
    REPORT_PROMPT("report.Prompt.Select"),

    /**
     * Report Print Button.
     */
    REPORT_PRINT("report.Button.Print"),

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
    SPOTPRICE_DATE("price.Prompt.Date"),

    /**
     * SpotPrice Next Button.
     */
    SPOTPRICE_NEXT("price.ToolTip.Next"),

    /**
     * SpotPrice Previous Button.
     */
    SPOTPRICE_PREV("price.ToolTip.Prev"),

    /**
     * SpotPrice Title.
     */
    SPOTPRICE_TITLE("price.Title.Select"),

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
    COLUMNSET_ALL("analysisColumnSet.All");

    /**
     * The Resource Builder.
     */
    private static final ResourceBuilder BUILDER = ResourceBuilder.getResourceBuilder(MoneyWiseIcons.class.getCanonicalName());

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
    private MoneyWiseUIControlResource(final String pKeyName) {
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
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    /**
     * Obtain key for columnSet.
     * @param pValue the Value
     * @return the resource key
     */
    protected static MoneyWiseUIControlResource getKeyForColumnSet(final AnalysisColumnSet pValue) {
        switch (pValue) {
            case BALANCE:
                return COLUMNSET_BALANCE;
            case STANDARD:
                return COLUMNSET_STANDARD;
            case SALARY:
                return COLUMNSET_SALARY;
            case INTEREST:
                return COLUMNSET_INTEREST;
            case DIVIDEND:
                return COLUMNSET_DIVIDEND;
            case SECURITY:
                return COLUMNSET_SECURITY;
            case ALL:
                return COLUMNSET_ALL;
            default:
                return null;
        }
    }
}
