/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.ui.panels;

import net.sourceforge.joceanus.jcoeus.data.CoeusMarketSet;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jcoeus.ui.CoeusDataEvent;
import net.sourceforge.joceanus.jcoeus.ui.CoeusDataLoader;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter;
import net.sourceforge.joceanus.jcoeus.ui.CoeusMarketCache;
import net.sourceforge.joceanus.jcoeus.ui.CoeusMenuItem;
import net.sourceforge.joceanus.jcoeus.ui.CoeusUIResource;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.ui.MetisPreferenceView;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerWindow;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysAbout;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager;
import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager.TethysMenuSubMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager.TethysTabItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Main Panel.
 * @param <N> Node type
 * @param <I> Icon type
 */
public abstract class CoeusMainPanel<N, I> {
    /**
     * The Market Cache.
     */
    private final CoeusMarketCache theMarketCache;

    /**
     * The Totals Table.
     */
    private final CoeusStatementTable<N, I> theTotalsTable;

    /**
     * The tabs.
     */
    private final TethysTabPaneManager<N, I> theTabs;

    /**
     * The Totals Tab.
     */
    private final TethysTabItem<N, I> theTotalsTab;

    /**
     * The menuBar.
     */
    private final TethysMenuBarManager theMenuBar;

    /**
     * The data window.
     */
    private final MetisViewerWindow<N, I> theDataWdw;

    /**
     * The about box.
     */
    private final TethysAbout<N, I> theAboutBox;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    protected CoeusMainPanel(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Access Gui Factory and Preference Manager */
        TethysGuiFactory<N, I> myFactory = pToolkit.getGuiFactory();
        MetisPreferenceManager myPreferences = pToolkit.getPreferenceManager();

        /* Create the Cache */
        theMarketCache = new CoeusMarketCache(pToolkit);

        /* Create the viewer entry for the cache */
        MetisViewerManager myDataMgr = pToolkit.getViewerManager();
        MetisViewerEntry mySection = myDataMgr.getStandardEntry(MetisViewerStandardEntry.VIEW);
        MetisViewerEntry myEntry = myDataMgr.newEntry(mySection, CoeusResource.DATA_MARKETCACHE.getValue());
        myEntry.setObject(theMarketCache);

        /* Create the Tabbed Pane */
        theTabs = myFactory.newTabPane();

        /* Create the report panel */
        CoeusReportPanel<N, I> myReports = new CoeusReportPanel<>(pToolkit, theMarketCache);
        theTabs.addTabItem(CoeusUIResource.TAB_REPORTS.getValue(), myReports);

        /* Listen to filter requests */
        TethysEventRegistrar<CoeusDataEvent> myRegistrar = myReports.getEventRegistrar();
        myRegistrar.addEventListener(CoeusDataEvent.GOTOSTATEMENT, this::handleGoToEvent);

        /* Create the totals table */
        theTotalsTable = new CoeusStatementTable<>(pToolkit, theMarketCache);
        theTotalsTab = theTabs.addTabItem(CoeusUIResource.TAB_STATEMENTS.getValue(), theTotalsTable);

        /* Create the Preferences Tab */
        MetisPreferenceView<N, I> myPrefPanel = new MetisPreferenceView<>(myFactory, myPreferences);
        theTabs.addTabItem(CoeusUIResource.TAB_PREFERENCES.getValue(), myPrefPanel);

        /* Add interesting preferences */
        myPreferences.getPreferenceSet(PrometheusBackupPreferences.class);

        /* Create the menu bar */
        theMenuBar = myFactory.newMenuBar();

        /* Create the Help menu */
        TethysMenuSubMenu<CoeusMenuItem> myHelp = theMenuBar.newSubMenu(CoeusMenuItem.HELP);

        /* Create the Viewer menuItem */
        myHelp.newMenuItem(CoeusMenuItem.DATAVIEWER, e -> handleDataViewer());
        myHelp.newMenuItem(CoeusMenuItem.ABOUT, e -> handleAboutBox());

        /* Create the data window */
        theDataWdw = pToolkit.newViewerWindow();
        theDataWdw.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> theMenuBar.setEnabled(CoeusMenuItem.DATAVIEWER, true));

        /* Create the aboutBox */
        theAboutBox = myFactory.newAboutBox();

        /* Create the loader */
        CoeusDataLoader myLoader = new CoeusDataLoader(pToolkit);
        CoeusMarketSet myMarketSet = myLoader.loadData();
        theMarketCache.declareMarketSet(myMarketSet);
    }

    /**
     * Obtain the tabs.
     * @return the Tabs
     */
    protected TethysTabPaneManager<N, I> getTabs() {
        return theTabs;
    }

    /**
     * Obtain menuBar.
     * @return the menuBar
     */
    protected TethysMenuBarManager getMenuBar() {
        return theMenuBar;
    }

    /**
     * Handle ViewerClosed.
     */
    private void handleDataViewer() {
        theMenuBar.setEnabled(CoeusMenuItem.DATAVIEWER, false);
        theDataWdw.showDialog();
    }

    /**
     * Handle AboutBox.
     */
    private void handleAboutBox() {
        theAboutBox.showDialog();
    }

    /**
     * Handle a GoTo event.
     * @param pEvent the event
     */
    private void handleGoToEvent(final TethysEvent<CoeusDataEvent> pEvent) {
        /* Obtain the filter and pass to the statement panel */
        CoeusFilter myFilter = pEvent.getDetails(CoeusFilter.class);
        theTotalsTable.processFilter(myFilter);

        /* Display the Statements Tab */
        theTotalsTab.selectItem();
    }
}
