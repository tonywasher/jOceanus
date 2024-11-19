/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.ui.panel;

import net.sourceforge.joceanus.metis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.quicken.definitions.MoneyWiseQIFPreference.MoneyWiseQIFPreferences;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseGoToId;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.prometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.prometheus.database.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.prometheus.preference.PrometheusPreferenceView;
import net.sourceforge.joceanus.prometheus.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUITabPaneManager;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUITabPaneManager.TethysUITabItem;

/**
 * Maintenance Tab panel.
 * @author Tony Washer
 */
public class MoneyWiseMaintenance
        implements TethysEventProvider<PrometheusDataEvent>, TethysUIComponent {
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
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The Data View.
     */
    private final MoneyWiseView theView;

    /**
     * The Tabs.
     */
    private final TethysUITabPaneManager theTabs;

    /**
     * The Account Panel.
     */
    private final MoneyWiseAccountPanel theAccountTab;

    /**
     * The Category Panel.
     */
    private final MoneyWiseCategoryPanel theCategoryTab;

    /**
     * The Static Panel.
     */
    private final MoneyWiseStaticPanel theStatic;

    /**
     * The Preferences Panel.
     */
    private final PrometheusPreferenceView thePreferences;

    /**
     * Refreshing flag.
     */
    private boolean isRefreshing;

    /**
     * Constructor.
     * @param pView the view
     */
    MoneyWiseMaintenance(final MoneyWiseView pView) {
        /* Store details */
        theView = pView;

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the Tabbed Pane */
        theTabs = theView.getGuiFactory().paneFactory().newTabPane();

        /* Create the account Tab and add it */
        theAccountTab = new MoneyWiseAccountPanel(theView);
        theTabs.addTabItem(TITLE_ACCOUNT, theAccountTab);

        /* Create the category Tab and add it */
        theCategoryTab = new MoneyWiseCategoryPanel(theView);
        theTabs.addTabItem(TITLE_CATEGORY, theCategoryTab);

        /* Create the Static Tab */
        theStatic = new MoneyWiseStaticPanel(theView);
        theTabs.addTabItem(TITLE_STATIC, theStatic);

        /* Create the Preferences Tab */
        final MetisPreferenceManager myPrefs = theView.getPreferenceManager();
        thePreferences = theView.getToolkit().newPreferenceView();
        theTabs.addTabItem(TITLE_PREFERENCES, thePreferences);

        /* Add interesting preferences */
        myPrefs.getPreferenceSet(PrometheusDatabasePreferences.class);
        myPrefs.getPreferenceSet(PrometheusBackupPreferences.class);
        myPrefs.getPreferenceSet(MoneyWiseQIFPreferences.class);

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
    public TethysUIComponent getUnderlying() {
        return theTabs;
    }

    @Override
    public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * setChildListeners.
     * @param pRegistrar the registrar
     */
    private void setChildListeners(final OceanusEventRegistrar<PrometheusDataEvent> pRegistrar) {
        pRegistrar.addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> {
            if (!isRefreshing) {
                setVisibility();
            }
        });
        pRegistrar.addEventListener(PrometheusDataEvent.GOTOWINDOW, this::handleGoToEvent);
    }

    /**
     * Obtain the view.
     * @return the view
     */
    protected MoneyWiseView getView() {
        return theView;
    }

    /**
     * Refresh data.
     */
    private void refreshData() {
        /* Obtain the active profile */
        OceanusProfile myTask = theView.getActiveTask();
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
    public void selectMaintenance(final PrometheusGoToEvent<MoneyWiseGoToId> pEvent) {
        /* Switch on the subId */
        switch (pEvent.getId()) {
            /* View the requested account */
            case ACCOUNT:
                /* Select the requested account */
                final MoneyWiseAssetBase myAccount = pEvent.getDetails(MoneyWiseAssetBase.class);
                theAccountTab.selectAccount(myAccount);

                /* Goto the Account tab */
                gotoNamedTab(TITLE_ACCOUNT);
                break;

            /* View the requested category */
            case CATEGORY:
                /* Select the requested category */
                final Object myCategory = pEvent.getDetails();
                theCategoryTab.selectCategory(myCategory);

                /* Goto the Category tab */
                gotoNamedTab(TITLE_CATEGORY);
                break;

            /* View the requested tag */
            case TAG:
                /* Select the requested tag */
                final Object myTag = pEvent.getDetails();
                theCategoryTab.selectTag(myTag);

                /* Goto the Category tab */
                gotoNamedTab(TITLE_CATEGORY);
                break;

            /* View the requested region */
            case REGION:
                /* Select the requested tag */
                final Object myRegion = pEvent.getDetails();
                theCategoryTab.selectRegion(myRegion);

                /* Goto the Category tab */
                gotoNamedTab(TITLE_CATEGORY);
                break;

            /* View the requested static */
            case STATIC:
                /* Select the requested tag */
                final PrometheusStaticDataItem myData = pEvent.getDetails(PrometheusStaticDataItem.class);
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
        final TethysUITabItem myItem = theTabs.findItemByName(pTabName);
        if (myItem != null) {
            myItem.selectItem();
        }
    }

    /**
     * Set visibility.
     */
    protected void setVisibility() {
        /* Determine whether we have any locked session */
        final boolean hasSession = hasSession();

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
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * Determine Focus.
     */
    public void determineFocus() {
        /* Access the selected component */
        final TethysUITabItem myItem = theTabs.getSelectedTab();
        final Integer myId = myItem.getId();

        /* If the selected component is Account */
        if (myId.equals(theAccountTab.getId())) {
            /* Set the debug focus */
            theAccountTab.determineFocus();

            /* If the selected component is Category */
        } else if (myId.equals(theCategoryTab.getId())) {
            /* Set the debug focus */
            theCategoryTab.determineFocus();

            /* If the selected component is Static */
        } else if (myId.equals(theStatic.getId())) {
            /* Set the debug focus */
            theStatic.determineFocus();

            /* If the selected component is Preferences */
        } else if (myId.equals(thePreferences.getId())) {
            /* Set the debug focus */
            thePreferences.determineFocus();
        }
    }

    /**
     * handle GoTo Event.
     * @param pEvent the event
     */
    @SuppressWarnings("unchecked")
    private void handleGoToEvent(final OceanusEvent<PrometheusDataEvent> pEvent) {
        /* Access details */
        final PrometheusGoToEvent<MoneyWiseGoToId> myEvent = pEvent.getDetails(PrometheusGoToEvent.class);

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
