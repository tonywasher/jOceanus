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

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ResourceBundle;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.PreferencesPanel;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType.CashCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryType.DepositCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryType.EventCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoType.EventInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.Frequency;
import net.sourceforge.joceanus.jmoneywise.data.statics.Frequency.FrequencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType.LoanCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasis;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasis.TaxBasisList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategory.TaxCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType.TaxYearInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFPreference;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.preferences.BackupPreferences;
import net.sourceforge.joceanus.jprometheus.preferences.DatabasePreferences;
import net.sourceforge.joceanus.jprometheus.ui.StaticDataPanel;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnableTabbed;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jthemis.git.data.GitPreferences;
import net.sourceforge.joceanus.jthemis.jira.data.JiraPreferences;
import net.sourceforge.joceanus.jthemis.svn.data.SubVersionPreferences;

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
     * TaxYears tab title.
     */
    private static final String TITLE_TAXYEARS = NLS_BUNDLE.getString("TabTaxYear");

    /**
     * Preferences tab title.
     */
    private static final String TITLE_PREFERENCES = NLS_BUNDLE.getString("TabPreference");

    /**
     * Category tab title.
     */
    private static final String TITLE_CATEGORY = NLS_BUNDLE.getString("TabCategory");

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
     * The TaxYear Panel.
     */
    private final MaintTaxYear theTaxYearTab;

    /**
     * The Category Panel.
     */
    private final CategoryPanel theCategoryTab;

    /**
     * The Static Panel.
     */
    private final StaticDataPanel<MoneyWiseDataType> theStatic;

    /**
     * The Preferences Panel.
     */
    private final PreferencesPanel thePreferences;

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

        /* Create the category Tab and add it */
        theCategoryTab = new CategoryPanel(theView);
        theTabs.addTab(TITLE_CATEGORY, theCategoryTab);
        theCategoryTab.addChangeListener(myListener);

        /* Create the TaxYears Tab */
        theTaxYearTab = new MaintTaxYear(theView);
        theTabs.addTab(TITLE_TAXYEARS, theTaxYearTab);
        theTaxYearTab.addChangeListener(myListener);

        /* Create the Static Tab */
        theStatic = new StaticDataPanel<MoneyWiseDataType>(theView);
        theTabs.addTab(TITLE_STATIC, theStatic);
        theStatic.addChangeListener(myListener);
        theView.addChangeListener(myListener);

        /* Add the static elements */
        theStatic.addStatic(DepositCategoryType.LIST_NAME, DepositCategoryTypeList.class, DepositCategoryType.class);
        theStatic.addStatic(CashCategoryType.LIST_NAME, CashCategoryTypeList.class, CashCategoryType.class);
        theStatic.addStatic(LoanCategoryType.LIST_NAME, LoanCategoryTypeList.class, LoanCategoryType.class);
        theStatic.addStatic(PayeeType.LIST_NAME, PayeeTypeList.class, PayeeType.class);
        theStatic.addStatic(SecurityType.LIST_NAME, SecurityTypeList.class, SecurityType.class);
        theStatic.addStatic(EventCategoryType.LIST_NAME, EventCategoryTypeList.class, EventCategoryType.class);
        theStatic.addStatic(AccountCurrency.LIST_NAME, AccountCurrencyList.class, AccountCurrency.class);
        theStatic.addStatic(TaxBasis.LIST_NAME, TaxBasisList.class, TaxBasis.class);
        theStatic.addStatic(TaxCategory.LIST_NAME, TaxCategoryList.class, TaxCategory.class);
        theStatic.addStatic(TaxRegime.LIST_NAME, TaxRegimeList.class, TaxRegime.class);
        theStatic.addStatic(Frequency.LIST_NAME, FrequencyList.class, Frequency.class);
        theStatic.addStatic(TaxYearInfoType.LIST_NAME, TaxYearInfoTypeList.class, TaxYearInfoType.class);
        theStatic.addStatic(AccountInfoType.LIST_NAME, AccountInfoTypeList.class, AccountInfoType.class);
        theStatic.addStatic(EventInfoType.LIST_NAME, EventInfoTypeList.class, EventInfoType.class);

        /* Create the Preferences Tab */
        PreferenceManager myPrefs = theView.getPreferenceMgr();
        thePreferences = new PreferencesPanel(myPrefs, theView.getFieldMgr(), theView.getDataMgr(), theView.getDataEntry(DataControl.DATA_MAINT));
        theTabs.addTab(TITLE_PREFERENCES, thePreferences);
        thePreferences.addChangeListener(myListener);

        /* Add interesting preferences */
        myPrefs.getPreferenceSet(DatabasePreferences.class);
        myPrefs.getPreferenceSet(BackupPreferences.class);
        myPrefs.getPreferenceSet(JiraPreferences.class);
        myPrefs.getPreferenceSet(SubVersionPreferences.class);
        myPrefs.getPreferenceSet(GitPreferences.class);
        myPrefs.getPreferenceSet(QIFPreference.class);

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
            theCategoryTab.refreshData();
            theTaxYearTab.refreshData();
            theStatic.refreshData();

            /* Determine visibility */
            setVisibility();
        } catch (JOceanusException e) {
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
        boolean hasUpdates = theCategoryTab.hasUpdates();
        if (!hasUpdates) {
            hasUpdates = theTaxYearTab.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = theStatic.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = thePreferences.hasUpdates();
        }

        /* Return to caller */
        return hasUpdates;
    }

    /**
     * Select an explicit account for maintenance.
     * @param pAccount the account
     */
    // public void selectAccount(final AssetBase<?> pAccount) {
    /* Pass through to the Account control */
    // theAccountTab.selectAccount(pAccount);

    /* Goto the Accounts tab */
    // gotoNamedTab(TITLE_ACCOUNTS);
    // }

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

        /* Access the Category index */
        int iIndex = theTabs.indexOfTab(TITLE_CATEGORY);

        /* Enable/Disable the Category tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, !hasUpdates || theCategoryTab.hasUpdates());
        }

        /* Access the TaxYear index */
        iIndex = theTabs.indexOfTab(TITLE_TAXYEARS);

        /* Enable/Disable the TaxYear tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, !hasUpdates || theTaxYearTab.hasUpdates());
        }

        /* Access the Static panel */
        iIndex = theTabs.indexOfTab(TITLE_STATIC);

        /* Enable/Disable the static tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, !hasUpdates || theStatic.hasUpdates());
        }

        /* Access the Properties panel */
        iIndex = theTabs.indexOfTab(TITLE_PREFERENCES);

        /* Enable/Disable the Properties tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, !hasUpdates || thePreferences.hasUpdates());
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

        /* If the selected component is Category */
        if (myComponent.equals(theCategoryTab)) {
            /* Set the debug focus */
            theCategoryTab.determineFocus();

            /* If the selected component is TaxYear */
        } else if (myComponent.equals(theTaxYearTab)) {
            /* Set the debug focus */
            theTaxYearTab.determineFocus();

            /* If the selected component is Static */
        } else if (myComponent.equals(theStatic)) {
            /* Set the debug focus */
            theStatic.determineFocus();

            /* If the selected component is Preferences */
        } else if (myComponent.equals(thePreferences)) {
            /* Set the debug focus */
            thePreferences.determineFocus();
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
