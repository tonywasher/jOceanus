/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.swing;

import java.awt.BorderLayout;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisPreferenceView;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.MoneyWiseCategoryPanel;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.MoneyWiseStaticPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions.QIFPreference.MoneyWiseQIFPreferences;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseGoToId;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager.TethysTabItem;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingNode;

/**
 * Maintenance Tab panel.
 * @author Tony Washer
 */
public class MaintenanceTab
        implements TethysEventProvider<PrometheusDataEvent>, TethysComponent {
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
    private final MoneyWiseView theView;

    /**
     * The Parent.
     */
    private final MainTab theParent;

    /**
     * The Panel.
     */
    private final TethysSwingEnablePanel thePanel;

    /**
     * The Tabs.
     */
    private final TethysTabPaneManager theTabs;

    /**
     * The Account Panel.
     */
    private final AccountPanel theAccountTab;

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
    private final MetisPreferenceView thePreferences;

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
        final TethysGuiFactory myFactory = theView.getGuiFactory();
        theId = myFactory.getNextId();

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the Panel */
        thePanel = new TethysSwingEnablePanel();

        /* Create the Tabbed Pane */
        theTabs = theView.getGuiFactory().newTabPane();

        /* Create the account Tab and add it */
        theAccountTab = new AccountPanel(theView);
        theTabs.addTabItem(TITLE_ACCOUNT, theAccountTab);

        /* Create the category Tab and add it */
        theCategoryTab = new MoneyWiseCategoryPanel(theView);
        theTabs.addTabItem(TITLE_CATEGORY, theCategoryTab);

        /* Create the Static Tab */
        theStatic = new MoneyWiseStaticPanel(theView);
        theTabs.addTabItem(TITLE_STATIC, theStatic);

        /* Create the Preferences Tab */
        final MetisPreferenceManager myPrefs = theView.getPreferenceManager();
        thePreferences = new MetisPreferenceView(myFactory, myPrefs);
        theTabs.addTabItem(TITLE_PREFERENCES, thePreferences);

        /* Add interesting preferences */
        myPrefs.getPreferenceSet(PrometheusDatabasePreferences.class);
        myPrefs.getPreferenceSet(PrometheusBackupPreferences.class);
        myPrefs.getPreferenceSet(MoneyWiseQIFPreferences.class);

        /* Create the layout for the panel */
        final BorderLayout myLayout = new BorderLayout();
        thePanel.setLayout(myLayout);
        thePanel.add(TethysSwingNode.getComponent(theTabs));

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
    public TethysSwingNode getNode() {
        return thePanel.getNode();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Obtain the view.
     * @return the view
     */
    protected MoneyWiseView getView() {
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
                final AssetBase<?> myAccount = pEvent.getDetails(AssetBase.class);
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
                @SuppressWarnings("unchecked")
                final StaticData<?, ?, MoneyWiseDataType> myData = (StaticData<?, ?, MoneyWiseDataType>) pEvent.getDetails(StaticData.class);
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
        final TethysTabItem myItem = theTabs.findItemByName(pTabName);
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
        theParent.setVisibility();
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Access the selected component */
        final TethysTabItem myItem = theTabs.getSelectedTab();
        final JComponent myComponent = TethysSwingNode.getComponent(myItem);

        /* If the selected component is Account */
        if (myComponent.equals(TethysSwingNode.getComponent(theAccountTab))) {
            /* Set the debug focus */
            theAccountTab.determineFocus();

            /* If the selected component is Category */
        } else if (myComponent.equals(TethysSwingNode.getComponent(theCategoryTab))) {
            /* Set the debug focus */
            theCategoryTab.determineFocus();

            /* If the selected component is Static */
        } else if (myComponent.equals(TethysSwingNode.getComponent(theStatic))) {
            /* Set the debug focus */
            theStatic.determineFocus();

            /* If the selected component is Preferences */
        } else if (myComponent.equals(TethysSwingNode.getComponent(thePreferences))) {
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
        final PrometheusGoToEvent<MoneyWiseGoToId> myEvent = (PrometheusGoToEvent<MoneyWiseGoToId>) pEvent.getDetails(PrometheusGoToEvent.class);

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
