/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.finance.ui;

import java.awt.Component;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.views.DataControl;

/**
 * Maintenance Tab panel.
 * @author Tony Washer
 */
public class MaintenanceTab implements ChangeListener {
    /**
     * The Data View.
     */
    private final View theView;

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
    private final JTabbedPane theTabs;

    /**
     * The Account Panel.
     */
    private final MaintAccount theAccountTab;

    /**
     * The TaxYear Panel.
     */
    private final MaintTaxYear theTaxYearTab;

    /**
     * The Properties Panel.
     */
    private final MaintProperties theProperties;

    /**
     * The NewYear Panel.
     */
    private final MaintNewYear thePatternYear;

    /**
     * The Static Panel.
     */
    private final MaintStatic theStatic;

    /**
     * The DataEntry.
     */
    private final JDataEntry theDataEntry;

    /**
     * Obtain the panel.
     * @return the panel
     */
    protected JPanel getPanel() {
        return thePanel;
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
     * Obtain the DataEntry.
     * @return the entry
     */
    public JDataEntry getDataEntry() {
        return theDataEntry;
    }

    /**
     * Obtain the data manager.
     * @return the manager
     */
    public JDataManager getDataManager() {
        return theParent.getDataMgr();
    }

    /**
     * Accounts tab title.
     */
    private static final String TITLE_ACCOUNTS = "Accounts";

    /**
     * TaxYears tab title.
     */
    private static final String TITLE_TAXYEARS = "TaxYears";

    /**
     * Properties tab title.
     */
    private static final String TITLE_PROPERTIES = "Properties";

    /**
     * NewYear tab title.
     */
    private static final String TITLE_NEWYEAR = "PatternYear";

    /**
     * Static tab title.
     */
    private static final String TITLE_STATIC = "Static";

    /**
     * Constructor.
     * @param pTop top window
     */
    public MaintenanceTab(final MainTab pTop) {
        /* Store details */
        theView = pTop.getView();
        theParent = pTop;

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_VIEWS);
        theDataEntry = myDataMgr.new JDataEntry("Maintenance");
        theDataEntry.addAsChildOf(mySection);

        /* Create the Tabbed Pane */
        theTabs = new JTabbedPane();
        theTabs.addChangeListener(this);

        /* Create the account Tab and add it */
        theAccountTab = new MaintAccount(this);
        theTabs.addTab(TITLE_ACCOUNTS, theAccountTab.getPanel());

        /* Create the TaxYears Tab */
        theTaxYearTab = new MaintTaxYear(this);
        theTabs.addTab(TITLE_TAXYEARS, theTaxYearTab.getPanel());

        /* Create the Properties Tab */
        theProperties = new MaintProperties(this);
        theTabs.addTab(TITLE_PROPERTIES, theProperties.getPanel());

        /* Create the PatternYear Tab */
        thePatternYear = new MaintNewYear(this);
        theTabs.addTab(TITLE_NEWYEAR, thePatternYear.getPanel());

        /* Create the Static Tab */
        theStatic = new MaintStatic(this);
        theTabs.addTab(TITLE_STATIC, theStatic.getPanel());

        /* Create the panel */
        thePanel = new JPanel();

        /* Create the layout for the panel */
        GroupLayout myLayout = new GroupLayout(thePanel);
        thePanel.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING,
                                                                         false)
                                                    .addComponent(theTabs, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                          myLayout.createSequentialGroup().addComponent(theTabs)));
    }

    /**
     * Refresh data.
     * @throws JDataException on error
     */
    public void refreshData() throws JDataException {
        /* Refresh sub-panels */
        theAccountTab.refreshData();
        theTaxYearTab.refreshData();
        thePatternYear.refreshData();
        theStatic.refreshData();
    }

    /**
     * Has this set of tables got updates.
     * @return true/false
     */
    public boolean hasUpdates() {
        boolean hasUpdates = false;

        /* Determine whether we have updates */
        hasUpdates = theAccountTab.hasUpdates();
        if (!hasUpdates) {
            hasUpdates = theTaxYearTab.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = theProperties.hasUpdates();
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
        int iIndex;
        boolean hasUpdates;

        /* Determine whether we have any updates */
        hasUpdates = hasUpdates();

        /* Access the Accounts index */
        iIndex = theTabs.indexOfTab(TITLE_ACCOUNTS);

        /* Enable/Disable the Accounts tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, !hasUpdates || theAccountTab.hasUpdates());
        }

        /* Access the TaxYear index */
        iIndex = theTabs.indexOfTab(TITLE_TAXYEARS);

        /* Enable/Disable the TaxYear tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, !hasUpdates || theTaxYearTab.hasUpdates());
        }

        /* Access the Properties panel */
        iIndex = theTabs.indexOfTab(TITLE_PROPERTIES);

        /* Enable/Disable the Properties tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, !hasUpdates || theProperties.hasUpdates());
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
            theTabs.setEnabledAt(iIndex, !hasUpdates || theStatic.hasUpdates());
        }

        /* Update the top level tabs */
        theParent.setVisibility();
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        /* Ignore if it is not the tabs */
        if (e.getSource() != theTabs) {
            return;
        }

        /* Determine the focus */
        determineFocus();
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Access the selected component */
        Component myComponent = theTabs.getSelectedComponent();

        /* If the selected component is Accounts */
        if (myComponent.equals(theAccountTab.getPanel())) {
            /* Set the debug focus */
            theAccountTab.getDataEntry().setFocus();

            /* If the selected component is TaxYear */
        } else if (myComponent.equals(theTaxYearTab.getPanel())) {
            /* Set the debug focus */
            theTaxYearTab.getDataEntry().setFocus();

            /* If the selected component is NewYear */
        } else if (myComponent.equals(thePatternYear.getPanel())) {
            /* Set the debug focus */
            thePatternYear.getDataEntry().setFocus();
            thePatternYear.requestFocusInWindow();

            /* If the selected component is Static */
        } else if (myComponent.equals(theStatic.getPanel())) {
            /* Set the debug focus */
            theStatic.determineFocus();
        }
    }
}
