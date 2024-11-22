/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.coeus.ui.panels;

import net.sourceforge.joceanus.coeus.data.CoeusMarketSet;
import net.sourceforge.joceanus.coeus.data.CoeusResource;
import net.sourceforge.joceanus.coeus.ui.CoeusDataEvent;
import net.sourceforge.joceanus.coeus.ui.CoeusDataLoader;
import net.sourceforge.joceanus.coeus.ui.CoeusFilter;
import net.sourceforge.joceanus.coeus.ui.CoeusMarketCache;
import net.sourceforge.joceanus.coeus.ui.CoeusMenuItem;
import net.sourceforge.joceanus.coeus.ui.CoeusUIResource;
import net.sourceforge.joceanus.metis.ui.MetisPreferenceView;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.metis.toolkit.MetisToolkit;
import net.sourceforge.joceanus.metis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.metis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.metis.viewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.metis.viewer.MetisViewerWindow;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.api.dialog.TethysUIAboutBox;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUILogTextArea;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIMainPanel;
import net.sourceforge.joceanus.tethys.ui.api.menu.TethysUIMenuBarManager;
import net.sourceforge.joceanus.tethys.ui.api.menu.TethysUIMenuBarManager.TethysUIMenuSubMenu;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUITabPaneManager;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUITabPaneManager.TethysUITabItem;

/**
 * Main Panel.
 */
public class CoeusMainPanel
    implements TethysUIMainPanel {
    /**
     * The Totals Table.
     */
    private final TethysUIFactory<?> theGuiFactory;

    /**
     * The Totals Table.
     */
    private final CoeusStatementTable theTotalsTable;

    /**
     * The tabs.
     */
    private final TethysUITabPaneManager theTabs;

    /**
     * The Totals Tab.
     */
    private final TethysUITabItem theTotalsTab;

    /**
     * The Log Tab.
     */
    private final TethysUITabItem theLogTab;

    /**
     * The menuBar.
     */
    private final TethysUIMenuBarManager theMenuBar;

    /**
     * The data window.
     */
    private final MetisViewerWindow theDataWdw;

    /**
     * The about box.
     */
    private TethysUIAboutBox theAboutBox;

    /**
     * Constructor.
     * @param pFactory the factory
     * @throws OceanusException on error
     */
    public CoeusMainPanel(final TethysUIFactory<?> pFactory) throws OceanusException {
        /* Store Gui factory */
        theGuiFactory = pFactory;

        /* Create Toolkit and access Preference Manager */
        final MetisToolkit myToolkit = new MetisToolkit(theGuiFactory);
        final MetisPreferenceManager myPreferences = myToolkit.getPreferenceManager();

        /* Create the Cache */
        final CoeusMarketCache myMarketCache = new CoeusMarketCache(myToolkit);

        /* Create the viewer entry for the cache */
        final MetisViewerManager myDataMgr = myToolkit.getViewerManager();
        final MetisViewerEntry mySection = myDataMgr.getStandardEntry(MetisViewerStandardEntry.VIEW);
        final MetisViewerEntry myEntry = myDataMgr.newEntry(mySection, CoeusResource.DATA_MARKETCACHE.getValue());
        myEntry.setObject(myMarketCache);

        /* Create the Tabbed Pane */
        theTabs = theGuiFactory.paneFactory().newTabPane();

        /* Create the report panel */
        final CoeusReportPanel myReports = new CoeusReportPanel(myToolkit, myMarketCache);
        theTabs.addTabItem(CoeusUIResource.TAB_REPORTS.getValue(), myReports);

        /* Listen to filter requests */
        final OceanusEventRegistrar<CoeusDataEvent> myRegistrar = myReports.getEventRegistrar();
        myRegistrar.addEventListener(CoeusDataEvent.GOTOSTATEMENT, this::handleGoToEvent);

        /* Create the totals table */
        theTotalsTable = new CoeusStatementTable(myToolkit, myMarketCache);
        theTotalsTab = theTabs.addTabItem(CoeusUIResource.TAB_STATEMENTS.getValue(), theTotalsTable);

        /* Create the Preferences Tab */
        final MetisPreferenceView myPrefPanel = new MetisPreferenceView(theGuiFactory, myPreferences);
        theTabs.addTabItem(CoeusUIResource.TAB_PREFERENCES.getValue(), myPrefPanel);

        /* Create the log tab */
        final TethysUILogTextArea myLog = theGuiFactory.getLogSink();
        theLogTab = theTabs.addTabItem(CoeusUIResource.TAB_LOG.getValue(), myLog);
        myLog.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> theLogTab.setVisible(true));
        myLog.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> theLogTab.setVisible(false));
        theLogTab.setVisible(myLog.isActive());

        /* Create the menu bar */
        theMenuBar = pFactory.menuFactory().newMenuBar();

        /* Create the Help menu */
        final TethysUIMenuSubMenu myHelp = theMenuBar.newSubMenu(CoeusMenuItem.HELP);

        /* Create the Viewer menuItem */
        myHelp.newMenuItem(CoeusMenuItem.DATAVIEWER, e -> handleDataViewer());
        myHelp.newMenuItem(CoeusMenuItem.ABOUT, e -> handleAboutBox());

        /* Create the data window */
        theDataWdw = myToolkit.newViewerWindow();
        theDataWdw.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> theMenuBar.setEnabled(CoeusMenuItem.DATAVIEWER, true));

        /* Create the loader */
        final CoeusDataLoader myLoader = new CoeusDataLoader(myToolkit);
        final CoeusMarketSet myMarketSet = myLoader.loadData();
        myMarketCache.declareMarketSet(myMarketSet);
    }

    @Override
    public TethysUITabPaneManager getComponent() {
        return theTabs;
    }

    @Override
    public TethysUIMenuBarManager getMenuBar() {
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
        if (theAboutBox == null) {
            theAboutBox = theGuiFactory.dialogFactory().newAboutBox();
        }
        theAboutBox.showDialog();
    }

    /**
     * Handle a GoTo event.
     * @param pEvent the event
     */
    private void handleGoToEvent(final OceanusEvent<CoeusDataEvent> pEvent) {
        /* Obtain the filter and pass to the statement panel */
        final CoeusFilter myFilter = pEvent.getDetails(CoeusFilter.class);
        theTotalsTable.processFilter(myFilter);

        /* Display the Statements Tab */
        theTotalsTab.selectItem();
    }

    @Override
    public boolean handleAppClose() {
        theDataWdw.closeWindow();
        return true;
    }
}
