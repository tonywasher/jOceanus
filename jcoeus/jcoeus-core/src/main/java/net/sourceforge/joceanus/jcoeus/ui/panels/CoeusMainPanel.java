/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.ui.panels;

import net.sourceforge.joceanus.jcoeus.data.CoeusMarketSet;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jcoeus.ui.CoeusDataEvent;
import net.sourceforge.joceanus.jcoeus.ui.CoeusDataLoader;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter;
import net.sourceforge.joceanus.jcoeus.ui.CoeusMarketCache;
import net.sourceforge.joceanus.jcoeus.ui.CoeusMenuItem;
import net.sourceforge.joceanus.jcoeus.ui.CoeusUIResource;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisPreferenceView;
import net.sourceforge.joceanus.jmetis.launch.MetisMainPanel;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerWindow;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysAbout;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLogTextArea;
import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager;
import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager.TethysMenuSubMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager.TethysTabItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Main Panel.
 */
public class CoeusMainPanel
    implements MetisMainPanel {
    /**
     * The Totals Table.
     */
    private final CoeusStatementTable theTotalsTable;

    /**
     * The tabs.
     */
    private final TethysTabPaneManager theTabs;

    /**
     * The Totals Tab.
     */
    private final TethysTabItem theTotalsTab;

    /**
     * The Log Tab.
     */
    private final TethysTabItem theLogTab;

    /**
     * The menuBar.
     */
    private final TethysMenuBarManager theMenuBar;

    /**
     * The data window.
     */
    private final MetisViewerWindow theDataWdw;

    /**
     * The about box.
     */
    private final TethysAbout theAboutBox;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    public CoeusMainPanel(final MetisToolkit pToolkit) throws OceanusException {
        /* Access Gui Factory and Preference Manager */
        final TethysGuiFactory myFactory = pToolkit.getGuiFactory();
        final MetisPreferenceManager myPreferences = pToolkit.getPreferenceManager();

        /* Create the Cache */
        final CoeusMarketCache myMarketCache = new CoeusMarketCache(pToolkit);

        /* Create the viewer entry for the cache */
        final MetisViewerManager myDataMgr = pToolkit.getViewerManager();
        final MetisViewerEntry mySection = myDataMgr.getStandardEntry(MetisViewerStandardEntry.VIEW);
        final MetisViewerEntry myEntry = myDataMgr.newEntry(mySection, CoeusResource.DATA_MARKETCACHE.getValue());
        myEntry.setObject(myMarketCache);

        /* Create the Tabbed Pane */
        theTabs = myFactory.newTabPane();

        /* Create the report panel */
        final CoeusReportPanel myReports = new CoeusReportPanel(pToolkit, myMarketCache);
        theTabs.addTabItem(CoeusUIResource.TAB_REPORTS.getValue(), myReports);

        /* Listen to filter requests */
        final TethysEventRegistrar<CoeusDataEvent> myRegistrar = myReports.getEventRegistrar();
        myRegistrar.addEventListener(CoeusDataEvent.GOTOSTATEMENT, this::handleGoToEvent);

        /* Create the totals table */
        theTotalsTable = new CoeusStatementTable(pToolkit, myMarketCache);
        theTotalsTab = theTabs.addTabItem(CoeusUIResource.TAB_STATEMENTS.getValue(), theTotalsTable);

        /* Create the Preferences Tab */
        final MetisPreferenceView myPrefPanel = new MetisPreferenceView(myFactory, myPreferences);
        theTabs.addTabItem(CoeusUIResource.TAB_PREFERENCES.getValue(), myPrefPanel);

        /* Create the log tab */
        final TethysLogTextArea myLog = myFactory.getLogSink();
        theLogTab = theTabs.addTabItem(CoeusUIResource.TAB_LOG.getValue(), myLog);
        myLog.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> theLogTab.setVisible(true));
        myLog.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> theLogTab.setVisible(false));
        theLogTab.setVisible(myLog.isActive());

        /* Create the menu bar */
        theMenuBar = myFactory.newMenuBar();

        /* Create the Help menu */
        final TethysMenuSubMenu<CoeusMenuItem> myHelp = theMenuBar.newSubMenu(CoeusMenuItem.HELP);

        /* Create the Viewer menuItem */
        myHelp.newMenuItem(CoeusMenuItem.DATAVIEWER, e -> handleDataViewer());
        myHelp.newMenuItem(CoeusMenuItem.ABOUT, e -> handleAboutBox());

        /* Create the data window */
        theDataWdw = pToolkit.newViewerWindow();
        theDataWdw.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> theMenuBar.setEnabled(CoeusMenuItem.DATAVIEWER, true));

        /* Create the aboutBox */
        theAboutBox = myFactory.newAboutBox();

        /* Create the loader */
        final CoeusDataLoader myLoader = new CoeusDataLoader(pToolkit);
        final CoeusMarketSet myMarketSet = myLoader.loadData();
        myMarketCache.declareMarketSet(myMarketSet);
    }

    @Override
    public TethysTabPaneManager getComponent() {
        return theTabs;
    }

    @Override
    public TethysMenuBarManager getMenuBar() {
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
