/*******************************************************************************
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
import java.awt.Font;

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

public class MaintenanceTab implements ChangeListener {
    /* properties */
    private View theView = null;
    private MainTab theParent = null;
    private JPanel thePanel = null;
    private JTabbedPane theTabs = null;
    private MaintAccount theAccountTab = null;
    private MaintTaxYear theTaxYearTab = null;
    private MaintProperties theProperties = null;
    private MaintNewYear thePatternYear = null;
    private MaintStatic theStatic = null;
    private JDataEntry theDataEntry = null;

    /* Access methods */
    protected JPanel getPanel() {
        return thePanel;
    }

    protected View getView() {
        return theView;
    }

    protected Font getStdFont(boolean isFixed) {
        return null;
    }

    protected Font getChgFont(boolean isFixed) {
        return null;
    }

    protected MainTab getTopWindow() {
        return theParent;
    }

    public JDataEntry getDataEntry() {
        return theDataEntry;
    }

    public JDataManager getDataManager() {
        return theParent.getDataMgr();
    }

    /* Tab titles */
    private static final String titleAccounts = "Accounts";
    private static final String titleTaxYear = "TaxYears";
    private static final String titleProperties = "Properties";
    private static final String titlePattern = "PatternYear";
    private static final String titleStatic = "Static";

    /* Constructor */
    public MaintenanceTab(MainTab pTop) {
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
        theTabs.addTab(titleAccounts, theAccountTab.getPanel());

        /* Create the TaxYears Tab */
        theTaxYearTab = new MaintTaxYear(this);
        theTabs.addTab(titleTaxYear, theTaxYearTab.getPanel());

        /* Create the Properties Tab */
        theProperties = new MaintProperties(this);
        theTabs.addTab(titleProperties, theProperties.getPanel());

        /* Create the PatternYear Tab */
        thePatternYear = new MaintNewYear(this);
        theTabs.addTab(titlePattern, thePatternYear.getPanel());

        /* Create the Static Tab */
        theStatic = new MaintStatic(this);
        theTabs.addTab(titleStatic, theStatic.getPanel());

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

    /* refresh data */
    public void refreshData() throws JDataException {
        /* Refresh sub-panels */
        theAccountTab.refreshData();
        theTaxYearTab.refreshData();
        thePatternYear.refreshData();
        theStatic.refreshData();
    }

    /* Has this set of tables got updates */
    public boolean hasUpdates() {
        boolean hasUpdates = false;

        /* Determine whether we have updates */
        hasUpdates = theAccountTab.hasUpdates();
        if (!hasUpdates)
            hasUpdates = theTaxYearTab.hasUpdates();
        if (!hasUpdates)
            hasUpdates = theProperties.hasUpdates();
        if (!hasUpdates)
            hasUpdates = theStatic.hasUpdates();

        /* Return to caller */
        return hasUpdates;
    }

    /* Select an explicit account for maintenance */
    public void selectAccount(Account pAccount) {
        /* Pass through to the Account control */
        theAccountTab.selectAccount(pAccount);

        /* Goto the Accounts tab */
        gotoNamedTab(titleAccounts);
    }

    /* Goto the specific tab */
    private void gotoNamedTab(String pTabName) {
        /* Access the Named index */
        int iIndex = theTabs.indexOfTab(pTabName);

        /* Select the required tab */
        theTabs.setSelectedIndex(iIndex);
    }

    /* Set visibility */
    protected void setVisibility() {
        int iIndex;
        boolean hasUpdates;

        /* Determine whether we have any updates */
        hasUpdates = hasUpdates();

        /* Access the Accounts index */
        iIndex = theTabs.indexOfTab(titleAccounts);

        /* Enable/Disable the Accounts tab */
        if (iIndex != -1)
            theTabs.setEnabledAt(iIndex, !hasUpdates || theAccountTab.hasUpdates());

        /* Access the TaxYear index */
        iIndex = theTabs.indexOfTab(titleTaxYear);

        /* Enable/Disable the TaxYear tab */
        if (iIndex != -1)
            theTabs.setEnabledAt(iIndex, !hasUpdates || theTaxYearTab.hasUpdates());

        /* Access the Properties panel */
        iIndex = theTabs.indexOfTab(titleProperties);

        /* Enable/Disable the Properties tab */
        if (iIndex != -1)
            theTabs.setEnabledAt(iIndex, !hasUpdates || theProperties.hasUpdates());

        /* Access the PatternYear panel */
        iIndex = theTabs.indexOfTab(titlePattern);

        /* Enable/Disable the patternYear tab */
        if (iIndex != -1)
            theTabs.setEnabledAt(iIndex, !hasUpdates);

        /* Access the Static panel */
        iIndex = theTabs.indexOfTab(titleStatic);

        /* Enable/Disable the static tab */
        if (iIndex != -1)
            theTabs.setEnabledAt(iIndex, !hasUpdates || theStatic.hasUpdates());

        /* Update the top level tabs */
        theParent.setVisibility();
    }

    /* Change listener */
    @Override
    public void stateChanged(ChangeEvent e) {
        /* Ignore if it is not the tabs */
        if (e.getSource() != theTabs)
            return;

        /* Determine the focus */
        determineFocus();
    }

    /* Determine Focus */
    protected void determineFocus() {
        /* Access the selected component */
        Component myComponent = theTabs.getSelectedComponent();

        /* If the selected component is Accounts */
        if (myComponent == (Component) theAccountTab.getPanel()) {
            /* Set the debug focus */
            theAccountTab.getDataEntry().setFocus();
        }

        /* If the selected component is TaxYear */
        else if (myComponent == (Component) theTaxYearTab.getPanel()) {
            /* Set the debug focus */
            theTaxYearTab.getDataEntry().setFocus();
        }

        /* If the selected component is NewYear */
        else if (myComponent == (Component) thePatternYear.getPanel()) {
            /* Set the debug focus */
            thePatternYear.getDataEntry().setFocus();
            thePatternYear.requestFocusInWindow();
        }

        /* If the selected component is Static */
        else if (myComponent == (Component) theStatic.getPanel()) {
            /* Set the debug focus */
            theStatic.determineFocus();
        }
    }
}
