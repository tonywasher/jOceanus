/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ResourceBundle;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataManager;
import net.sourceforge.jOceanus.jDataModels.preferences.BackupPreferences;
import net.sourceforge.jOceanus.jDataModels.preferences.DatabasePreferences;
import net.sourceforge.jOceanus.jDataModels.ui.MaintStatic;
import net.sourceforge.jOceanus.jDataModels.views.DataControl;
import net.sourceforge.jOceanus.jEventManager.JEnableWrapper.JEnableTabbed;
import net.sourceforge.jOceanus.jEventManager.JEventPanel;
import net.sourceforge.jOceanus.jJira.data.JiraPreferences;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryType.AccountCategoryTypeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCurrency;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType.EventCategoryTypeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoType.EventInfoTypeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.Frequency;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.Frequency.FrequencyList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategory.TaxCategoryList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxRegime;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxYearInfoType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxYearInfoType.TaxYearInfoTypeList;
import net.sourceforge.jOceanus.jMoneyWise.quicken.definitions.QIFPreference;
import net.sourceforge.jOceanus.jMoneyWise.views.View;
import net.sourceforge.jOceanus.jPreferenceSet.MaintPreferences;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceManager;
import net.sourceforge.jOceanus.jSvnManager.data.SubVersionPreferences;

/**
 * Maintenance Tab panel.
 * @author Tony Washer
 */
public class MaintenanceTab
        extends JEventPanel {
    /**
     * The serial Id.
     */
    private static final long serialVersionUID = 4291381331160920L;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(MaintenanceTab.class.getName());

    /**
     * Accounts tab title.
     */
    private static final String TITLE_ACCOUNTS = NLS_BUNDLE.getString("TabAccount");

    /**
     * TaxYears tab title.
     */
    private static final String TITLE_TAXYEARS = NLS_BUNDLE.getString("TabTaxYear");

    /**
     * Preferences tab title.
     */
    private static final String TITLE_PREFERENCES = NLS_BUNDLE.getString("TabPreference");

    /**
     * NewYear tab title.
     */
    private static final String TITLE_NEWYEAR = NLS_BUNDLE.getString("TabNewYear");

    /**
     * Static tab title.
     */
    private static final String TITLE_STATIC = NLS_BUNDLE.getString("TabStatic");

    /**
     * The Data View.
     */
    private final transient View theView;

    /**
     * The Parent.
     */
    private final transient MainTab theParent;

    /**
     * The Tabs.
     */
    private final JEnableTabbed theTabs;

    /**
     * The Account Panel.
     */
    private final MaintAccount theAccountTab;

    /**
     * The TaxYear Panel.
     */
    private final MaintTaxYear theTaxYearTab;

    /**
     * The Preferences Panel.
     */
    private final MaintPreferences thePreferences;

    /**
     * The NewYear Panel.
     */
    private final MaintNewYear thePatternYear;

    /**
     * The Static Panel.
     */
    private final MaintStatic theStatic;

    /**
     * Obtain the view.
     * @return the view
     */
    protected View getView() {
        return theView;
    }

    /**
     * Obtain the top window.
     * @return the window
     */
    protected MainTab getTopWindow() {
        return theParent;
    }

    /**
     * Obtain the data manager.
     * @return the manager
     */
    public JDataManager getDataManager() {
        return theView.getDataMgr();
    }

    /**
     * Constructor.
     * @param pTop top window
     */
    public MaintenanceTab(final MainTab pTop) {
        /* Store details */
        theView = pTop.getView();
        theParent = pTop;

        /* Create a listener */
        MaintenanceListener myListener = new MaintenanceListener();

        /* Create the Tabbed Pane */
        theTabs = new JEnableTabbed();
        theTabs.addChangeListener(myListener);

        /* Create the account Tab and add it */
        theAccountTab = new MaintAccount(theView);
        theTabs.addTab(TITLE_ACCOUNTS, theAccountTab);
        theAccountTab.addChangeListener(myListener);

        /* Create the TaxYears Tab */
        theTaxYearTab = new MaintTaxYear(theView);
        theTabs.addTab(TITLE_TAXYEARS, theTaxYearTab);
        theTaxYearTab.addChangeListener(myListener);

        /* Create the Preferences Tab */
        PreferenceManager myPrefs = theView.getPreferenceMgr();
        thePreferences = new MaintPreferences(myPrefs, theView.getFieldMgr(), theView.getDataMgr(), theView.getDataEntry(DataControl.DATA_MAINT));
        theTabs.addTab(TITLE_PREFERENCES, thePreferences);
        thePreferences.addChangeListener(myListener);

        /* Add interesting preferences */
        myPrefs.getPreferenceSet(DatabasePreferences.class);
        myPrefs.getPreferenceSet(BackupPreferences.class);
        myPrefs.getPreferenceSet(JiraPreferences.class);
        myPrefs.getPreferenceSet(SubVersionPreferences.class);
        myPrefs.getPreferenceSet(QIFPreference.class);

        /* Create the PatternYear Tab */
        thePatternYear = new MaintNewYear(theView);
        theTabs.addTab(TITLE_NEWYEAR, thePatternYear.getPanel());

        /* Create the Static Tab */
        theStatic = new MaintStatic(theView);
        theTabs.addTab(TITLE_STATIC, theStatic);
        theStatic.addChangeListener(myListener);
        theView.addChangeListener(myListener);

        /* Add the static elements */
        theStatic.addStatic(AccountCategoryType.LIST_NAME, AccountCategoryTypeList.class, AccountCategoryType.class);
        theStatic.addStatic(EventCategoryType.LIST_NAME, EventCategoryTypeList.class, EventCategoryType.class);
        theStatic.addStatic(AccountCurrency.LIST_NAME, AccountCurrencyList.class, AccountCurrency.class);
        theStatic.addStatic(TaxCategory.LIST_NAME, TaxCategoryList.class, TaxCategory.class);
        theStatic.addStatic(TaxRegime.LIST_NAME, TaxRegimeList.class, TaxRegime.class);
        theStatic.addStatic(Frequency.LIST_NAME, FrequencyList.class, Frequency.class);
        theStatic.addStatic(TaxYearInfoType.LIST_NAME, TaxYearInfoTypeList.class, TaxYearInfoType.class);
        theStatic.addStatic(AccountInfoType.LIST_NAME, AccountInfoTypeList.class, AccountInfoType.class);
        theStatic.addStatic(EventInfoType.LIST_NAME, EventInfoTypeList.class, EventInfoType.class);

        /* Create the layout for the panel */
        FlowLayout myLayout = new FlowLayout();
        myLayout.setAlignment(FlowLayout.LEADING);
        setLayout(myLayout);

        /* Set the layout */
        add(theTabs);
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Pass on to important elements */
        theTabs.setEnabled(bEnabled);
    }

    /**
     * Refresh data.
     */
    private void refreshData() {
        try {
            /* Refresh sub-panels */
            theAccountTab.refreshData();
            theTaxYearTab.refreshData();
            thePatternYear.refreshData();
            theStatic.refreshData();

            /* Determine visibility */
            setVisibility();
        } catch (JDataException e) {
            /* Show the error */
            theView.addError(e);
        }
    }

    /**
     * Has this set of tables got updates.
     * @return true/false
     */
    public boolean hasUpdates() {
        /* Determine whether we have updates */
        boolean hasUpdates = theAccountTab.hasUpdates();
        if (!hasUpdates) {
            hasUpdates = theTaxYearTab.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = thePreferences.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = theStatic.hasUpdates();
        }

        /* Return to caller */
        return hasUpdates;
    }

    /**
     * Select an explicit account for maintenance.
     * @param pAccount the account
     */
    public void selectAccount(final Account pAccount) {
        /* Pass through to the Account control */
        theAccountTab.selectAccount(pAccount);

        /* Goto the Accounts tab */
        gotoNamedTab(TITLE_ACCOUNTS);
    }

    /**
     * Goto the specific tab.
     * @param pTabName the tab name
     */
    private void gotoNamedTab(final String pTabName) {
        /* Access the Named index */
        int iIndex = theTabs.indexOfTab(pTabName);

        /* Select the required tab */
        theTabs.setSelectedIndex(iIndex);
    }

    /**
     * Set visibility.
     */
    protected void setVisibility() {
        /* Determine whether we have any updates */
        boolean hasUpdates = hasUpdates();

        /* Access the Accounts index */
        int iIndex = theTabs.indexOfTab(TITLE_ACCOUNTS);

        /* Enable/Disable the Accounts tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, !hasUpdates
                                         || theAccountTab.hasUpdates());
        }

        /* Access the TaxYear index */
        iIndex = theTabs.indexOfTab(TITLE_TAXYEARS);

        /* Enable/Disable the TaxYear tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, !hasUpdates
                                         || theTaxYearTab.hasUpdates());
        }

        /* Access the Properties panel */
        iIndex = theTabs.indexOfTab(TITLE_PREFERENCES);

        /* Enable/Disable the Properties tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, !hasUpdates
                                         || thePreferences.hasUpdates());
        }

        /* Access the PatternYear panel */
        iIndex = theTabs.indexOfTab(TITLE_NEWYEAR);

        /* Enable/Disable the patternYear tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, !hasUpdates);
        }

        /* Access the Static panel */
        iIndex = theTabs.indexOfTab(TITLE_STATIC);

        /* Enable/Disable the static tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, !hasUpdates
                                         || theStatic.hasUpdates());
        }

        /* Update the top level tabs */
        theParent.setVisibility();
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Access the selected component */
        Component myComponent = theTabs.getSelectedComponent();

        /* If the selected component is Accounts */
        if (myComponent.equals(theAccountTab)) {
            /* Set the debug focus */
            theAccountTab.determineFocus();

            /* If the selected component is TaxYear */
        } else if (myComponent.equals(theTaxYearTab)) {
            /* Set the debug focus */
            theTaxYearTab.determineFocus();

            /* If the selected component is Preferences */
        } else if (myComponent.equals(thePreferences)) {
            /* Set the debug focus */
            thePreferences.determineFocus();

            /* If the selected component is NewYear */
        } else if (myComponent.equals(thePatternYear.getPanel())) {
            /* Set the debug focus */
            thePatternYear.determineFocus();

            /* If the selected component is Static */
        } else if (myComponent.equals(theStatic)) {
            /* Set the debug focus */
            theStatic.determineFocus();
        }
    }

    /**
     * The listener class.
     */
    private final class MaintenanceListener
            implements ChangeListener {
        @Override
        public void stateChanged(final ChangeEvent e) {
            Object o = e.getSource();

            /* if it is the tabs */
            if (theTabs.equals(o)) {
                /* Determine the focus */
                determineFocus();
            } else if (theView.equals(o)) {
                /* Refresh the data */
                refreshData();
            } else {
                /* Set the visibility */
                setVisibility();
            }
        }
    }
}
