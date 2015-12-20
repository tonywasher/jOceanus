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
package net.sourceforge.joceanus.jmoneywise.ui.swing;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.swing.MetisPreferencesPanel;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType.CashCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryType.DepositCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.Frequency.FrequencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType.LoanCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasis.TaxBasisList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategory.TaxCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType.TaxYearInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryType.TransactionCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoType.TransactionInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFPreference;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jprometheus.preference.BackupPreferences;
import net.sourceforge.joceanus.jprometheus.preference.DatabasePreferences;
import net.sourceforge.joceanus.jprometheus.ui.swing.StaticDataPanel;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistration.TethysChangeRegistration;
import net.sourceforge.joceanus.jtethys.ui.TethysTabManager.TethysTabItem;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTabManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTabManager.TethysSwingTabItem;

/**
 * Maintenance Tab panel.
 * @author Tony Washer
 */
public class MaintenanceTab
        extends JPanel
        implements TethysEventProvider {
    /**
     * The serial Id.
     */
    private static final long serialVersionUID = 4291381331160920L;

    /**
     * TaxYears tab title.
     */
    private static final String TITLE_TAXYEARS = MoneyWiseUIResource.MAINTENANCE_TAXYEAR.getValue();

    /**
     * Preferences tab title.
     */
    private static final String TITLE_PREFERENCES = MoneyWiseUIResource.MAINTENANCE_SETTINGS.getValue();

    /**
     * Account tab title.
     */
    private static final String TITLE_ACCOUNT = MoneyWiseUIResource.MAINTENANCE_ACCOUNT.getValue();

    /**
     * Category tab title.
     */
    private static final String TITLE_CATEGORY = MoneyWiseUIResource.MAINTENANCE_CATEGORY.getValue();

    /**
     * Static tab title.
     */
    private static final String TITLE_STATIC = MoneyWiseUIResource.MAINTENANCE_STATIC.getValue();

    /**
     * The Event Manager.
     */
    private final transient TethysEventManager theEventManager;

    /**
     * The Data View.
     */
    private final transient SwingView theView;

    /**
     * The Parent.
     */
    private final transient MainTab theParent;

    /**
     * The Tabs.
     */
    private final transient TethysSwingTabManager theTabs;

    /**
     * The TaxYear Panel.
     */
    private final TaxYearTable theTaxYearTab;

    /**
     * The Account Panel.
     */
    private final AccountPanel theAccountTab;

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
    private final MetisPreferencesPanel thePreferences;

    /**
     * Constructor.
     * @param pTop top window
     */
    public MaintenanceTab(final MainTab pTop) {
        /* Store details */
        theView = pTop.getView();
        theParent = pTop;

        /* Create the event manager */
        theEventManager = new TethysEventManager();

        /* Create the Tabbed Pane */
        theTabs = new TethysSwingTabManager();

        /* Create the account Tab and add it */
        theAccountTab = new AccountPanel(theView);
        theTabs.addTabItem(TITLE_ACCOUNT, theAccountTab);

        /* Create the category Tab and add it */
        theCategoryTab = new CategoryPanel(theView);
        theTabs.addTabItem(TITLE_CATEGORY, theCategoryTab);

        /* Create the TaxYears Tab */
        theTaxYearTab = new TaxYearTable(theView);
        theTabs.addTabItem(TITLE_TAXYEARS, theTaxYearTab.getPanel());

        /* Create the Static Tab */
        theStatic = new StaticDataPanel<MoneyWiseDataType>(theView, theView.getUtilitySet(), MoneyWiseDataType.class);
        theTabs.addTabItem(TITLE_STATIC, theStatic);

        /* Add the static elements */
        theStatic.addStatic(MoneyWiseDataType.DEPOSITTYPE, DepositCategoryTypeList.class);
        theStatic.addStatic(MoneyWiseDataType.CASHTYPE, CashCategoryTypeList.class);
        theStatic.addStatic(MoneyWiseDataType.LOANTYPE, LoanCategoryTypeList.class);
        theStatic.addStatic(MoneyWiseDataType.PAYEETYPE, PayeeTypeList.class);
        theStatic.addStatic(MoneyWiseDataType.SECURITYTYPE, SecurityTypeList.class);
        theStatic.addStatic(MoneyWiseDataType.TRANSTYPE, TransactionCategoryTypeList.class);
        theStatic.addStatic(MoneyWiseDataType.CURRENCY, AssetCurrencyList.class);
        theStatic.addStatic(MoneyWiseDataType.TAXBASIS, TaxBasisList.class);
        theStatic.addStatic(MoneyWiseDataType.TAXTYPE, TaxCategoryList.class);
        theStatic.addStatic(MoneyWiseDataType.TAXREGIME, TaxRegimeList.class);
        theStatic.addStatic(MoneyWiseDataType.FREQUENCY, FrequencyList.class);
        theStatic.addStatic(MoneyWiseDataType.TAXINFOTYPE, TaxYearInfoTypeList.class);
        theStatic.addStatic(MoneyWiseDataType.ACCOUNTINFOTYPE, AccountInfoTypeList.class);
        theStatic.addStatic(MoneyWiseDataType.TRANSINFOTYPE, TransactionInfoTypeList.class);

        /* Create the Preferences Tab */
        MetisPreferenceManager myPrefs = theView.getPreferenceManager();
        thePreferences = new MetisPreferencesPanel(myPrefs, theView.getFieldManager(), theView.getViewerManager(), theView.getDataEntry(DataControl.DATA_MAINT));
        theTabs.addTabItem(TITLE_PREFERENCES, thePreferences);

        /* Add interesting preferences */
        myPrefs.getPreferenceSet(DatabasePreferences.class);
        myPrefs.getPreferenceSet(BackupPreferences.class);
        myPrefs.getPreferenceSet(QIFPreference.class);

        /* Create the layout for the panel */
        BorderLayout myLayout = new BorderLayout();
        setLayout(myLayout);

        /* Set the layout */
        add(theTabs.getNode());

        /* Create a listener */
        new MaintenanceListener();
    }

    @Override
    public TethysEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

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
     * Obtain the viewer manager.
     * @return the manager
     */
    public MetisViewerManager getViewerManager() {
        return theView.getViewerManager();
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
        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("MaintenanceTab");

        /* Protect against exceptions */
        try {
            /* Refresh sub-panels */
            theAccountTab.refreshData();
            theCategoryTab.refreshData();
            theTaxYearTab.refreshData();
            theStatic.refreshData();

        } catch (OceanusException e) {
            /* Show the error */
            theView.addError(e);
        }

        /* Complete the task */
        myTask.end();
    }

    /**
     * Has this set of tables got updates.
     * @return true/false
     */
    public boolean hasUpdates() {
        /* Determine whether we have updates */
        boolean hasUpdates = theAccountTab.hasUpdates();
        if (!hasUpdates) {
            hasUpdates = theCategoryTab.hasUpdates();
        }
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
     * Has this set of panels got the session focus?
     * @return true/false
     */
    public boolean hasSession() {
        /* Determine whether we have focus */
        boolean hasUpdates = theAccountTab.hasSession();
        if (!hasUpdates) {
            hasUpdates = theCategoryTab.hasSession();
        }
        if (!hasUpdates) {
            hasUpdates = theTaxYearTab.hasSession();
        }
        if (!hasUpdates) {
            hasUpdates = theStatic.hasSession();
        }
        if (!hasUpdates) {
            hasUpdates = thePreferences.hasSession();
        }

        /* Return to caller */
        return hasUpdates;
    }

    /**
     * Select maintenance.
     * @param pEvent the action request
     */
    protected void selectMaintenance(final TethysActionEvent pEvent) {
        /* Switch on the subId */
        switch (pEvent.getActionId()) {
            /* View the requested account */
            case MainTab.ACTION_VIEWACCOUNT:
                /* Select the requested account */
                AssetBase<?> myAccount = pEvent.getDetails(AssetBase.class);
                theAccountTab.selectAccount(myAccount);

                /* Goto the Account tab */
                gotoNamedTab(TITLE_ACCOUNT);
                break;

            /* View the requested category */
            case MainTab.ACTION_VIEWCATEGORY:
                /* Select the requested category */
                Object myCategory = pEvent.getDetails();
                theCategoryTab.selectCategory(myCategory);

                /* Goto the Category tab */
                gotoNamedTab(TITLE_CATEGORY);
                break;

            /* View the requested tag */
            case MainTab.ACTION_VIEWTAG:
                /* Select the requested tag */
                Object myTag = pEvent.getDetails();
                theCategoryTab.selectTag(myTag);

                /* Goto the Category tab */
                gotoNamedTab(TITLE_CATEGORY);
                break;

            /* View the requested taxYear */
            case MainTab.ACTION_VIEWTAXYEAR:
                /* Select the requested tag */
                TaxYear myYear = pEvent.getDetails(TaxYear.class);
                theTaxYearTab.selectTaxYear(myYear);

                /* Goto the TaxYears tab */
                gotoNamedTab(TITLE_TAXYEARS);
                break;

            /* View the requested static */
            case MainTab.ACTION_VIEWSTATIC:
                /* Select the requested tag */
                @SuppressWarnings("unchecked")
                StaticData<?, ?, MoneyWiseDataType> myData = (StaticData<?, ?, MoneyWiseDataType>) pEvent.getDetails(StaticData.class);
                theStatic.selectStatic(myData);

                /* Goto the Static tab */
                gotoNamedTab(TITLE_STATIC);
                break;

            /* Unsupported request */
            default:
                break;
        }
    }

    /**
     * Goto the specific tab.
     * @param pTabName the tab name
     */
    private void gotoNamedTab(final String pTabName) {
        /* Look up item and select it */
        TethysTabItem<?> myItem = theTabs.findItemByName(pTabName);
        if (myItem != null) {
            myItem.selectItem();
        }
    }

    /**
     * Set visibility.
     */
    protected void setVisibility() {
        /* Determine whether we have any locked session */
        boolean hasSession = hasSession();

        /* Enable/Disable the Account tab */
        boolean doEnabled = !hasSession || theAccountTab.hasSession();
        theTabs.enableItemByName(TITLE_ACCOUNT, doEnabled);

        /* Enable/Disable the Category tab */
        doEnabled = !hasSession || theCategoryTab.hasSession();
        theTabs.enableItemByName(TITLE_CATEGORY, doEnabled);

        /* Enable/Disable the TaxYear tab */
        doEnabled = !hasSession || theTaxYearTab.hasSession();
        theTabs.enableItemByName(TITLE_TAXYEARS, doEnabled);

        /* Enable/Disable the static tab */
        doEnabled = !hasSession || theStatic.hasSession();
        theTabs.enableItemByName(TITLE_STATIC, doEnabled);

        /* Enable/Disable the Properties tab */
        doEnabled = !hasSession || thePreferences.hasSession();
        theTabs.enableItemByName(TITLE_PREFERENCES, doEnabled);

        /* Update the top level tabs */
        theParent.setVisibility();
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Access the selected component */
        TethysSwingTabItem myItem = theTabs.getSelectedTab();
        JComponent myComponent = myItem.getNode();

        /* If the selected component is Account */
        if (myComponent.equals(theAccountTab)) {
            /* Set the debug focus */
            theAccountTab.determineFocus();

            /* If the selected component is Category */
        } else if (myComponent.equals(theCategoryTab)) {
            /* Set the debug focus */
            theCategoryTab.determineFocus();

            /* If the selected component is TaxYear */
        } else if (myComponent.equals(theTaxYearTab.getPanel())) {
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
            implements TethysActionEventListener, TethysChangeEventListener {
        /**
         * View Registration.
         */
        private final TethysChangeRegistration theViewReg;

        /**
         * Refreshing flag.
         */
        private boolean refreshing = false;

        /**
         * Constructor.
         */
        private MaintenanceListener() {
            /* Register listeners */
            theViewReg = theView.getEventRegistrar().addChangeListener(this);

            /* Handle sub-panels */
            TethysEventRegistrar myRegistrar = theAccountTab.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
            myRegistrar = theCategoryTab.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
            myRegistrar = theTaxYearTab.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
            myRegistrar = theStatic.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
            thePreferences.getEventRegistrar().addChangeListener(this);

            /* Listen to tab selection events */
            theTabs.getEventRegistrar().addActionListener(new TethysActionEventListener() {
                @Override
                public void processAction(final TethysActionEvent pEvent) {
                    determineFocus();
                }
            });
        }

        @Override
        public void processChange(final TethysChangeEvent pEvent) {
            /* If this is the data view */
            if (theViewReg.isRelevant(pEvent)) {
                /* Refresh the data locking visibility setting for the duration */
                refreshing = true;
                refreshData();
                refreshing = false;

                /* Update visibility */
                setVisibility();

                /* Don't set visibility if called from refresh */
            } else if (!refreshing) {
                /* Set the visibility */
                setVisibility();
            }
        }

        @Override
        public void processAction(final TethysActionEvent pEvent) {
            /* else handle or cascade */
            switch (pEvent.getActionId()) {
                /* Pass through the event */
                case MainTab.ACTION_VIEWSTATEMENT:
                    theEventManager.cascadeActionEvent(pEvent);
                    break;

                /* Access maintenance */
                case MainTab.ACTION_VIEWACCOUNT:
                case MainTab.ACTION_VIEWTAXYEAR:
                case MainTab.ACTION_VIEWCATEGORY:
                case MainTab.ACTION_VIEWTAG:
                case MainTab.ACTION_VIEWSTATIC:
                    selectMaintenance(pEvent);
                    break;
                default:
                    break;
            }
        }
    }
}
