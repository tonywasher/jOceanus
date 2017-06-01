/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.lethe.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.lethe.ui.MetisPreferenceView;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType.CashCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryType.DepositCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.Frequency.FrequencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType.LoanCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasis.TaxBasisList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryType.TransactionCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoType.TransactionInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFPreference.MoneyWiseQIFPreferences;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseGoToId;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jprometheus.lethe.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jprometheus.lethe.preference.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.StaticDataPanel;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager.TethysTabItem;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTabPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTabPaneManager.TethysSwingTabItem;

/**
 * Maintenance Tab panel.
 * @author Tony Washer
 */
public class MaintenanceTab
        implements TethysEventProvider<PrometheusDataEvent>, TethysNode<JComponent> {
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
     * The Id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The Data View.
     */
    private final SwingView theView;

    /**
     * The Parent.
     */
    private final MainTab theParent;

    /**
     * The Panel.
     */
    private final JPanel thePanel;

    /**
     * The Tabs.
     */
    private final TethysSwingTabPaneManager theTabs;

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
    private final MetisPreferenceView<JComponent, Icon> thePreferences;

    /**
     * Refreshing flag.
     */
    private boolean isRefreshing;

    /**
     * Constructor.
     * @param pTop top window
     */
    public MaintenanceTab(final MainTab pTop) {
        /* Store details */
        theView = pTop.getView();
        theParent = pTop;

        /* Access GUI Factory */
        TethysSwingGuiFactory myFactory = theView.getUtilitySet().getGuiFactory();
        theId = myFactory.getNextId();

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the Panel */
        thePanel = new TethysSwingEnablePanel();

        /* Create the Tabbed Pane */
        theTabs = theView.getUtilitySet().getGuiFactory().newTabPane();

        /* Create the account Tab and add it */
        theAccountTab = new AccountPanel(theView);
        theTabs.addTabItem(TITLE_ACCOUNT, theAccountTab);

        /* Create the category Tab and add it */
        theCategoryTab = new CategoryPanel(theView);
        theTabs.addTabItem(TITLE_CATEGORY, theCategoryTab);

        /* Create the Static Tab */
        theStatic = new StaticDataPanel<>(theView, theView.getUtilitySet(), MoneyWiseDataType.class);
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
        theStatic.addStatic(MoneyWiseDataType.FREQUENCY, FrequencyList.class);
        theStatic.addStatic(MoneyWiseDataType.ACCOUNTINFOTYPE, AccountInfoTypeList.class);
        theStatic.addStatic(MoneyWiseDataType.TRANSINFOTYPE, TransactionInfoTypeList.class);

        /* Create the Preferences Tab */
        MetisPreferenceManager myPrefs = theView.getPreferenceManager();
        // thePreferences = new MetisPreferencesPanel(myFactory, myPrefs, theView.getFieldManager(),
        // theView.getViewerManager(), theView.getDataEntry(DataControl.DATA_MAINT));
        thePreferences = new MetisPreferenceView<>(myFactory, myPrefs);
        theTabs.addTabItem(TITLE_PREFERENCES, thePreferences);

        /* Add interesting preferences */
        myPrefs.getPreferenceSet(PrometheusDatabasePreferences.class);
        myPrefs.getPreferenceSet(PrometheusBackupPreferences.class);
        myPrefs.getPreferenceSet(MoneyWiseQIFPreferences.class);

        /* Create the layout for the panel */
        BorderLayout myLayout = new BorderLayout();
        thePanel.setLayout(myLayout);
        thePanel.add(theTabs.getNode());

        /* Create a listeners */
        theTabs.getEventRegistrar().addEventListener(e -> determineFocus());
        setChildListeners(theAccountTab.getEventRegistrar());
        setChildListeners(theCategoryTab.getEventRegistrar());
        setChildListeners(theStatic.getEventRegistrar());
        thePreferences.getEventRegistrar().addEventListener(e -> setVisibility());

        /* Handle Refresh */
        theView.getEventRegistrar().addEventListener(e -> {
            /* Refresh the data locking visibility setting for the duration */
            isRefreshing = true;
            refreshData();
            isRefreshing = false;

            /* Update visibility */
            setVisibility();
        });
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * setChildListeners.
     * @param pRegistrar the registrar
     */
    private void setChildListeners(final TethysEventRegistrar<PrometheusDataEvent> pRegistrar) {
        pRegistrar.addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> {
            if (!isRefreshing) {
                setVisibility();
            }
        });
        pRegistrar.addEventListener(PrometheusDataEvent.GOTOWINDOW, this::handleGoToEvent);
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Obtain the view.
     * @return the view
     */
    protected SwingView getView() {
        return theView;
    }

    /**
     * Obtain the top window.
     * @return the window
     */
    protected MainTab getTopWindow() {
        return theParent;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        /* Pass on to important elements */
        theTabs.setEnabled(pEnabled);
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
    protected void selectMaintenance(final PrometheusGoToEvent<MoneyWiseGoToId> pEvent) {
        /* Switch on the subId */
        switch (pEvent.getId()) {
            /* View the requested account */
            case ACCOUNT:
                /* Select the requested account */
                AssetBase<?> myAccount = pEvent.getDetails(AssetBase.class);
                theAccountTab.selectAccount(myAccount);

                /* Goto the Account tab */
                gotoNamedTab(TITLE_ACCOUNT);
                break;

            /* View the requested category */
            case CATEGORY:
                /* Select the requested category */
                Object myCategory = pEvent.getDetails();
                theCategoryTab.selectCategory(myCategory);

                /* Goto the Category tab */
                gotoNamedTab(TITLE_CATEGORY);
                break;

            /* View the requested tag */
            case TAG:
                /* Select the requested tag */
                Object myTag = pEvent.getDetails();
                theCategoryTab.selectTag(myTag);

                /* Goto the Category tab */
                gotoNamedTab(TITLE_CATEGORY);
                break;

            /* View the requested region */
            case REGION:
                /* Select the requested tag */
                Object myRegion = pEvent.getDetails();
                theCategoryTab.selectRegion(myRegion);

                /* Goto the Category tab */
                gotoNamedTab(TITLE_CATEGORY);
                break;

            /* View the requested static */
            case STATIC:
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
        TethysTabItem<?, ?> myItem = theTabs.findItemByName(pTabName);
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
        if (myComponent.equals(theAccountTab.getNode())) {
            /* Set the debug focus */
            theAccountTab.determineFocus();

            /* If the selected component is Category */
        } else if (myComponent.equals(theCategoryTab.getNode())) {
            /* Set the debug focus */
            theCategoryTab.determineFocus();

            /* If the selected component is Static */
        } else if (myComponent.equals(theStatic.getNode())) {
            /* Set the debug focus */
            theStatic.determineFocus();

            /* If the selected component is Preferences */
        } else if (myComponent.equals(thePreferences.getNode())) {
            /* Set the debug focus */
            thePreferences.determineFocus();
        }
    }

    /**
     * handle GoTo Event.
     * @param pEvent the event
     */
    private void handleGoToEvent(final TethysEvent<PrometheusDataEvent> pEvent) {
        /* Access details */
        @SuppressWarnings("unchecked")
        PrometheusGoToEvent<MoneyWiseGoToId> myEvent = (PrometheusGoToEvent<MoneyWiseGoToId>) pEvent.getDetails(PrometheusGoToEvent.class);

        /* Access event and obtain details */
        switch (myEvent.getId()) {
            /* Pass through the event */
            case STATEMENT:
                theEventManager.cascadeEvent(pEvent);
                break;

            /* Access maintenance */
            case ACCOUNT:
            case CATEGORY:
            case TAG:
            case REGION:
            case STATIC:
                selectMaintenance(myEvent);
                break;
            default:
                break;
        }
    }
}
