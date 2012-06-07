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

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDateDay.DateDayRangeSelect;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Account.AccountList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.ui.controls.AccountSelect;
import uk.co.tolcroft.finance.ui.controls.ComboSelect;
import uk.co.tolcroft.finance.views.Statement;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.data.EditState;
import uk.co.tolcroft.models.ui.ErrorPanel;
import uk.co.tolcroft.models.ui.SaveButtons;
import uk.co.tolcroft.models.ui.StdInterfaces.StdPanel;
import uk.co.tolcroft.models.ui.StdInterfaces.stdCommand;
import uk.co.tolcroft.models.views.DataControl;
import uk.co.tolcroft.models.views.ViewList;

public class AccountTab implements StdPanel, ChangeListener {
    private View theView = null;
    private JPanel thePanel = null;
    private MainTab theParent = null;
    private JTabbedPane theTabs = null;
    private AccountSelect theSelect = null;
    private AccountStatement theStatement = null;
    private AccountPatterns thePatterns = null;
    private AccountPrices thePrices = null;
    private AccountRates theRates = null;
    private ViewList theViewSet = null;
    private Account theAccount = null;
    private AccountList theAcList = null;
    private SaveButtons theTabButs = null;
    private JDataEntry theDataEntry = null;
    private ErrorPanel theError = null;

    /* Access methods */
    public View getView() {
        return theView;
    }

    public MainTab getTopWindow() {
        return theParent;
    }

    public JPanel getPanel() {
        return thePanel;
    }

    public int getFieldForCol(int col) {
        return -1;
    }

    public ComboSelect getComboList() {
        return theParent.getComboList();
    }

    public ViewList getViewSet() {
        return theViewSet;
    }

    @Override
    public JDataEntry getDataEntry() {
        return theDataEntry;
    }

    @Override
    public JDataManager getDataManager() {
        return theParent.getDataMgr();
    }

    @Override
    public void printIt() {
    }

    /* Tab headers */
    private static final String titleStatement = "Statement";
    private static final String titlePatterns = "Patterns";
    private static final String titlePrices = "Prices";
    private static final String titleRates = "Rates";

    /**
     * Constructor for Account Window
     * @param pParent the parent window
     */
    public AccountTab(MainTab pParent) {
        GroupLayout myLayout;
        JDataEntry mySection;

        /* Record passed details */
        theParent = pParent;
        theView = pParent.getView();

        /* Build the View set */
        theViewSet = new ViewList(theView);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        mySection = theView.getDataEntry(DataControl.DATA_VIEWS);
        theDataEntry = myDataMgr.new JDataEntry("Account");
        theDataEntry.addAsChildOf(mySection);

        /* Create the Tabbed Pane */
        theTabs = new JTabbedPane();

        /* Create the Statement table and add to tabbed pane */
        theStatement = new AccountStatement(this);
        theTabs.addTab(titleStatement, theStatement.getPanel());
        theTabs.addChangeListener(this);

        /* Create the optional tables */
        thePatterns = new AccountPatterns(this);
        theRates = new AccountRates(this);
        thePrices = new AccountPrices(this);

        /* Create the Account selection panel */
        theSelect = new AccountSelect(theView, this, false);

        /* Create the table buttons */
        theTabButs = new SaveButtons(this);

        /* Create the error panel for this view */
        theError = new ErrorPanel(this);

        /* Create the panel */
        thePanel = new JPanel();

        /* Create the layout for the panel */
        myLayout = new GroupLayout(thePanel);
        thePanel.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING,
                                                                         false)
                                                    .addComponent(theError, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(theSelect, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(theTabs, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(theTabButs, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                          myLayout.createSequentialGroup()
                                  .addComponent(theError)
                                  .addComponent(theSelect)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                                                   GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                  .addComponent(theTabs)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(theTabButs).addContainerGap()));
    }

    /**
     * Lock on error
     * @param isError is there an error (True/False)
     */
    @Override
    public void lockOnError(boolean isError) {
        /* Hide selection panel */
        theSelect.setVisible(!isError);

        /* Lock tabs and buttons area */
        theTabs.setEnabled(!isError);
        theTabButs.setEnabled(!isError);
    }

    /**
     * Refresh views/controls after a load/update of underlying data
     * @throws JDataException on error
     */
    public void refreshData() throws JDataException {
        /* Refresh the account selection */
        theSelect.refreshData();

        /* Refresh the child tables */
        theRates.refreshData();
        thePrices.refreshData();
        thePatterns.refreshData();
        theStatement.refreshData();

        /* Redraw selection */
        setSelection(theSelect.getSelected());

        /* Create SavePoint */
        theSelect.createSavePoint();
    }

    /**
     * Has this set of tables got updates
     * @return true/false
     */
    @Override
    public boolean hasUpdates() {
        /* Return to caller */
        return theViewSet.hasUpdates();
    }

    /**
     * Has this set of tables got errors
     * @return true/false
     */
    public boolean hasErrors() {
        /* Return to caller */
        return theViewSet.hasErrors();
    }

    /**
     * Get the edit state of this set of tables
     * @return the edit state
     */
    @Override
    public EditState getEditState() {
        /* Return to caller */
        return theViewSet.getEditState();
    }

    /**
     * Perform a command
     * @param pCmd the command to perform
     */
    @Override
    public void performCommand(stdCommand pCmd) {
        /* Cancel any editing */
        cancelEditing();

        /* Switch on command */
        switch (pCmd) {
            case OK:
                saveData();
                break;
            case RESETALL:
                resetData();
                break;
        }
        notifyChanges();
    }

    /**
     * Is this table locked
     * @return true/false
     */
    @Override
    public boolean isLocked() {
        /* State whether account is locked */
        return ((theAccount == null) || theAccount.isClosed());
    }

    /**
     * Validate all tables
     */
    public void validateAll() {
        /* Validate the data */
        theStatement.validateAll();
        thePatterns.validateAll();
        theRates.validateAll();
        thePrices.validateAll();
        notifyChanges();
    }

    /**
     * Cancel all editing
     */
    public void cancelEditing() {
        /* cancel editing */
        theStatement.cancelEditing();
        thePatterns.cancelEditing();
        theRates.cancelEditing();
        thePrices.cancelEditing();
    }

    /**
     * Reset all table data
     */
    public void resetData() {
        /* reset the data */
        theStatement.resetData();
        thePatterns.resetData();
        theRates.resetData();
        thePrices.resetData();
    }

    /**
     * Save changes from the view into the underlying data
     */
    public void saveData() {
        /* Validate the changes */
        validateAll();

        /* Stop now if there are validation errors */
        if (hasErrors())
            return;

        /* Apply changes in the view set */
        theViewSet.applyChanges();

        /* Access any error */
        JDataException myError = theView.getError();

        /* Show the error */
        if (myError != null)
            theError.setError(myError);

        /* Update the debug of the underlying entries */
        theRates.saveData();
        thePatterns.saveData();
        thePrices.saveData();
        theStatement.saveData();
    }

    /**
     * Notify table that there has been a change in selection by an underlying control
     * @param obj the underlying control that has changed selection
     */
    @Override
    public void notifySelection(Object obj) {
        /* If this is a change from the account selection */
        if (obj == (Object) theSelect) {
            /* Protect against exceptions */
            try {
                /* Select the account */
                setSelection(theSelect.getSelected());

                /* Create SavePoint */
                theSelect.createSavePoint();
            }

            catch (Throwable e) {
                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA,
                        "Failed to change selection", e);

                /* Show the error */
                theError.setError(myError);

                /* Restore SavePoint */
                theSelect.restoreSavePoint();
            }
        }
    }

    /**
     * Call underlying controls to take notice of changes in view/selection
     */
    @Override
    public void notifyChanges() {
        /* Lock down the table buttons and the selection */
        theTabButs.setLockDown();
        theSelect.setLockDown();

        /* Adjust the visibility of the tabs */
        setVisibleTabs();
    }

    /**
     * Select an explicit account
     * @param pAccount the account to select
     * @throws JDataException on error
     */
    public void setSelection(Account pAccount) throws JDataException {
        FinanceData myData = theView.getData();

        /* Release old list */
        if (theAcList != null)
            theAcList.clear();

        /* Reset controls */
        theAcList = null;
        theAccount = null;

        /* If we have a selected account */
        if (pAccount != null) {
            /* Create the edit account list */
            theAcList = myData.getAccounts().getEditList(pAccount);

            /* Access the account */
            theAccount = theAcList.getAccount();
        }

        /* Alert the different tables to the change */
        theStatement.setSelection(theAccount);
        thePatterns.setSelection(theAccount);
        thePrices.setSelection(theAccount);
        theRates.setSelection(theAccount);

        /* Note the changes */
        notifyChanges();
    }

    /**
     * Set tabs to be visible or not depending on the type of account
     */
    private void setVisibleTabs() {
        int iIndex;
        boolean isPatternsSelected = false;
        boolean isPricesSelected = false;

        /* Access the Rates index */
        iIndex = theTabs.indexOfTab(titleRates);

        /* If the account has rates */
        if ((theAccount != null) && (theAccount.isMoney())) {

            /* Add the Rates if not present */
            if (iIndex == -1) {
                theTabs.addTab(titleRates, theRates.getPanel());
                theRates.getDataEntry().showEntry();

                /* Remove the patterns tab if present */
                iIndex = theTabs.indexOfTab(titlePatterns);
                if (iIndex != -1) {
                    /* Remember if Patterns are selected since we need to restore this */
                    if ((iIndex == theTabs.getSelectedIndex()) && (!isPricesSelected))
                        isPatternsSelected = true;

                    /* Remove the patterns tab */
                    theTabs.removeTabAt(iIndex);
                    thePatterns.getDataEntry().hideEntry();
                }
            }
        }

        /* else if not rates but tab is present */
        else if (iIndex != -1) {
            /* If the tab is selected then set statement as selected */
            if (iIndex == theTabs.getSelectedIndex())
                theTabs.setSelectedIndex(0);

            /* Remove the units tab */
            theTabs.removeTabAt(iIndex);
            theRates.getDataEntry().hideEntry();
        }

        /* Access the Prices index */
        iIndex = theTabs.indexOfTab(titlePrices);

        /* If the account has prices */
        if ((theAccount != null) && (theAccount.isPriced())) {

            /* Add the Prices if not present */
            if (iIndex == -1) {
                theTabs.addTab(titlePrices, thePrices.getPanel());
                thePrices.getDataEntry().showEntry();

                /* If the prices were selected */
                if (isPricesSelected) {
                    /* Re-select the prices */
                    iIndex = theTabs.indexOfTab(titlePrices);
                    theTabs.setSelectedIndex(iIndex);
                }

                /* Remove the patterns tab if present */
                iIndex = theTabs.indexOfTab(titlePatterns);
                if (iIndex != -1) {
                    /* Remember if Patterns are selected since we need to restore this */
                    if (iIndex == theTabs.getSelectedIndex())
                        isPatternsSelected = true;

                    /* Remove the patterns tab */
                    theTabs.removeTabAt(iIndex);
                    thePatterns.getDataEntry().hideEntry();
                }
            }
        }

        /* else if not rates but tab is present */
        else if (iIndex != -1) {
            /* If the tab is selected then set statement as selected */
            if (iIndex == theTabs.getSelectedIndex())
                theTabs.setSelectedIndex(0);

            /* Remove the units tab */
            theTabs.removeTabAt(iIndex);
            thePrices.getDataEntry().hideEntry();
        }

        /* Access the Patterns index */
        iIndex = theTabs.indexOfTab(titlePatterns);

        /* If the account is not closed */
        if ((theAccount != null) && (!theAccount.isClosed())) {

            /* Add the Patterns if not present */
            if (iIndex == -1) {
                theTabs.addTab(titlePatterns, thePatterns.getPanel());
                thePatterns.getDataEntry().showEntry();

                /* If the patterns were selected */
                if (isPatternsSelected) {
                    /* Re-select the patterns */
                    iIndex = theTabs.indexOfTab(titlePatterns);
                    theTabs.setSelectedIndex(iIndex);
                }
            }
        }

        /* else if not patterned but tab is present */
        else if (iIndex != -1) {
            /* If the tab is selected then set statement as selected */
            if (iIndex == theTabs.getSelectedIndex())
                theTabs.setSelectedIndex(0);

            /* Remove the units tab */
            theTabs.removeTabAt(iIndex);
            thePatterns.getDataEntry().hideEntry();
        }

        /* Update the top level tabs */
        theParent.setVisibility();
    }

    /**
     * Select an explicit account and period
     * @param pAccount the account to select
     * @param pSource the period source
     */
    public void selectAccount(Account pAccount,
                              DateDayRangeSelect pSource) {
        /* Protect against exceptions */
        try {
            /* Adjust the date selection for the statements appropriately */
            theStatement.selectPeriod(pSource);

            /* Adjust the account selection */
            theSelect.setSelection(pAccount);
            setSelection(theSelect.getSelected());

            /* Create SavePoint */
            theSelect.createSavePoint();
        }

        catch (Exception e) {
            /* Build the error */
            JDataException myError = new JDataException(ExceptionClass.DATA, "Failed to change selection", e);

            /* Show the error */
            theError.setError(myError);

            /* Restore SavePoint */
            theSelect.restoreSavePoint();
        }
    }

    /**
     * Add a pattern from a statement line
     * @param pLine the line to add
     */
    public void addPattern(final Statement.StatementLine pLine) {
        /* Pass through to the Patterns table */
        thePatterns.addPattern(pLine);

        /* Change focus to the Patterns */
        gotoNamedTab(titlePatterns);
    }

    /**
     * Select the explicitly named tab.
     * @param pTabName the tab to select
     */
    public void gotoNamedTab(final String pTabName) {
        /* Access the required index */
        int iIndex = theTabs.indexOfTab(pTabName);

        /* Select the required tab */
        theTabs.setSelectedIndex(iIndex);
    }

    /**
     * Handle state changed events (change of select tab).
     * @param e the change event
     */
    @Override
    public void stateChanged(final ChangeEvent e) {
        /* Ignore if it is not the tabs */
        if (e.getSource() != theTabs)
            return;

        /* Determine the focus */
        determineFocus();
    }

    /**
     * Determine the current focus.
     */
    protected void determineFocus() {
        /* Access the selected component */
        Component myComponent = theTabs.getSelectedComponent();

        /* If the selected component is Statement */
        if (myComponent == (Component) theStatement.getPanel()) {
            /* Set the debug focus */
            theStatement.getDataEntry().setFocus();
            theStatement.requestFocusInWindow();
        }

        /* If the selected component is Rates */
        else if (myComponent == (Component) theRates.getPanel()) {
            /* Set the debug focus */
            theRates.getDataEntry().setFocus();
            theRates.requestFocusInWindow();
        }

        /* If the selected component is Prices */
        else if (myComponent == (Component) thePrices.getPanel()) {
            /* Set the debug focus */
            thePrices.getDataEntry().setFocus();
            thePrices.requestFocusInWindow();
        }

        /* If the selected component is Patterns */
        else if (myComponent == (Component) thePatterns.getPanel()) {
            /* Set the debug focus */
            thePatterns.getDataEntry().setFocus();
            thePatterns.requestFocusInWindow();
        }
    }
}
