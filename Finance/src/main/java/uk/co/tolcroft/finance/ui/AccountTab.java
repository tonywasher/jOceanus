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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.JDataManager.EventManager.ActionDetailEvent;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDataManager.JPanelWithEvents;
import net.sourceforge.JDateDay.DateDayRangeSelect;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Account.AccountList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.ui.controls.AccountSelect;
import uk.co.tolcroft.finance.ui.controls.ComboSelect;
import uk.co.tolcroft.finance.views.Statement.StatementLine;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.data.EditState;
import uk.co.tolcroft.models.ui.ErrorPanel;
import uk.co.tolcroft.models.ui.SaveButtons;
import uk.co.tolcroft.models.views.DataControl;
import uk.co.tolcroft.models.views.UpdateSet;

/**
 * Account Tab panel.
 * @author Tony Washer
 */
public class AccountTab extends JPanelWithEvents {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 9200876063232169306L;

    /**
     * Data View.
     */
    private final transient View theView;

    /**
     * The Tabs.
     */
    private final JTabbedPane theTabs;

    /**
     * The Account Selection panel.
     */
    private final AccountSelect theSelect;

    /**
     * The Statement panel.
     */
    private final AccountStatement theStatement;

    /**
     * The Patterns panel.
     */
    private final AccountPatterns thePatterns;

    /**
     * The Prices panel.
     */
    private final AccountPrices thePrices;

    /**
     * The Rates panel.
     */
    private final AccountRates theRates;

    /**
     * The Update Set.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * The Account.
     */
    private transient Account theAccount = null;

    /**
     * The Account list.
     */
    private transient AccountList theAcList = null;

    /**
     * The Save buttons.
     */
    private final SaveButtons theSaveButtons;

    /**
     * The data Entry.
     */
    private final transient JDataEntry theDataEntry;

    /**
     * The Error panel.
     */
    private final ErrorPanel theError;

    /**
     * Obtain view.
     * @return the view
     */
    public View getView() {
        return theView;
    }

    /**
     * Obtain the updateSet.
     * @return the updateSet
     */
    public UpdateSet getUpdateSet() {
        return theUpdateSet;
    }

    /**
     * Statement panel title.
     */
    private static final String TITLE_STATEMENT = "Statement";

    /**
     * Patterns panel title.
     */
    private static final String TITLE_PATTERNS = "Patterns";

    /**
     * Prices panel title.
     */
    private static final String TITLE_PRICES = "Prices";

    /**
     * Rates panel title.
     */
    private static final String TITLE_RATES = "Rates";

    /**
     * Constructor for Account Window.
     * @param pView the data view
     */
    public AccountTab(final View pView) {
        /* Record passed details */
        theView = pView;

        /* Build the Update set */
        theUpdateSet = new UpdateSet(theView);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_VIEWS);
        theDataEntry = myDataMgr.new JDataEntry("Account");
        theDataEntry.addAsChildOf(mySection);
        theDataEntry.setObject(theUpdateSet);

        /* Create the Tabbed Pane */
        theTabs = new JTabbedPane();

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, theDataEntry);

        /* Create the optional tables */
        thePatterns = new AccountPatterns(theView, theUpdateSet, theError);
        theRates = new AccountRates(theView, theUpdateSet, theError);
        thePrices = new AccountPrices(theView, theUpdateSet, theError);

        /* Create the Statement table and add to tabbed pane */
        theStatement = new AccountStatement(theView, theUpdateSet, theError);
        theTabs.addTab(TITLE_STATEMENT, theStatement.getPanel());

        /* Create the listener */
        AccountListener myListener = new AccountListener();

        /* Add change listeners for the sub-panels */
        thePatterns.addChangeListener(myListener);
        theRates.addChangeListener(myListener);
        thePrices.addChangeListener(myListener);
        theStatement.addChangeListener(myListener);
        theTabs.addChangeListener(myListener);
        theStatement.addActionListener(myListener);

        /* Create the Account selection panel */
        theSelect = new AccountSelect(theView, false);
        theSelect.addChangeListener(myListener);
        theError.addChangeListener(myListener);

        /* Create the table buttons */
        theSaveButtons = new SaveButtons(theUpdateSet);
        theSaveButtons.addActionListener(myListener);

        /* Now define the panel */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(theSelect);
        add(theError);
        add(theTabs);
        add(theSaveButtons);

        /* Set the layout */
        // myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        // .addGroup(myLayout.createSequentialGroup()
        // .addContainerGap()
        // .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING,
        // false)
        // .addComponent(theError, GroupLayout.Alignment.LEADING,
        // GroupLayout.DEFAULT_SIZE,
        // GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        // .addComponent(theSelect, GroupLayout.Alignment.LEADING,
        // GroupLayout.DEFAULT_SIZE,
        // GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        // .addComponent(theTabs, GroupLayout.Alignment.LEADING,
        // GroupLayout.DEFAULT_SIZE,
        // GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        // .addComponent(theSaveButtons,
        // GroupLayout.Alignment.LEADING,
        // GroupLayout.DEFAULT_SIZE,
        // GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        // .addContainerGap()));
        // myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        // .addGroup(GroupLayout.Alignment.TRAILING,
        // myLayout.createSequentialGroup()
        // .addComponent(theError)
        // .addComponent(theSelect)
        // .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
        // GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        // .addComponent(theTabs)
        // .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        // .addComponent(theSaveButtons).addContainerGap()));
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     * @param pCombo the combo list
     * @throws JDataException on error
     */
    public void refreshData(final ComboSelect pCombo) throws JDataException {
        /* Refresh the account selection */
        theSelect.refreshData();

        /* Refresh the child tables */
        theRates.refreshData();
        thePrices.refreshData();
        thePatterns.refreshData(pCombo);
        theStatement.refreshData(pCombo);

        /* Redraw selection */
        setSelection(theSelect.getSelected());

        /* Create SavePoint */
        theSelect.createSavePoint();
    }

    /**
     * Has this set of tables got updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        /* Return to caller */
        return theUpdateSet.hasUpdates();
    }

    /**
     * Has this set of tables got errors.
     * @return true/false
     */
    public boolean hasErrors() {
        /* Return to caller */
        return theUpdateSet.hasErrors();
    }

    /**
     * Get the edit state of this set of tables.
     * @return the edit state
     */
    public EditState getEditState() {
        /* Return to caller */
        return theUpdateSet.getEditState();
    }

    /**
     * Perform a command.
     * @param pCmd the command to perform
     */
    public void performCommand(final String pCmd) {
        /* Cancel any editing */
        cancelEditing();

        /* Process the command */
        theUpdateSet.processCommand(pCmd);

        /* Access any error */
        JDataException myError = theView.getError();

        /* Show the error */
        if (myError != null) {
            theError.setError(myError);
        }

        /* Notify listeners of changes */
        notifyChanges();
    }

    /**
     * Is this table locked?
     * @return true/false
     */
    // @Override
    // public boolean isLocked() {
    /* State whether account is locked */
    // return ((theAccount == null) || theAccount.isClosed());
    // }

    /**
     * Cancel all editing.
     */
    public void cancelEditing() {
        /* cancel editing */
        theStatement.cancelEditing();
        thePatterns.cancelEditing();
        theRates.cancelEditing();
        thePrices.cancelEditing();
    }

    /**
     * Notify table that there has been a change in selection by an underlying control.
     * @param obj the underlying control that has changed selection
     */
    // @Override
    // public void notifySelection(final Object obj) {
    /* If this is a change from the account selection */
    // if (theSelect.equals(obj)) {
    /* Protect against exceptions */
    // try {
    /* Select the account */
    // setSelection(theSelect.getSelected());

    /* Create SavePoint */
    // theSelect.createSavePoint();
    // } catch (Exception e) {
    /* Build the error */
    // JDataException myError = new JDataException(ExceptionClass.DATA,
    // "Failed to change selection", e);

    /* Show the error */
    // theError.setError(myError);

    /* Restore SavePoint */
    // theSelect.restoreSavePoint();
    // }
    // }
    // }

    /**
     * Call underlying controls to take notice of changes in view/selection.
     */
    // @Override
    public void notifyChanges() {
        /* Lock down the table buttons and the selection */
        theSaveButtons.setEnabled(true);
        theSelect.setEnabled(!hasUpdates());

        /* Adjust the visibility of the tabs */
        setVisibleTabs();
    }

    /**
     * Select an explicit account.
     * @param pAccount the account to select
     * @throws JDataException on error
     */
    public void setSelection(final Account pAccount) throws JDataException {
        FinanceData myData = theView.getData();

        /* Release old list */
        if (theAcList != null) {
            theAcList.clear();
        }

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
     * Set tabs to be visible or not depending on the type of account.
     */
    private void setVisibleTabs() {
        int iIndex;
        boolean isPatternsSelected = false;
        boolean isPricesSelected = false;

        /* Access the Rates index */
        iIndex = theTabs.indexOfTab(TITLE_RATES);

        /* If the account has rates */
        if ((theAccount != null) && (theAccount.isMoney())) {

            /* Add the Rates if not present */
            if (iIndex == -1) {
                theTabs.addTab(TITLE_RATES, theRates.getPanel());

                /* Remove the patterns tab if present */
                iIndex = theTabs.indexOfTab(TITLE_PATTERNS);
                if (iIndex != -1) {
                    /* Remember if Patterns are selected since we need to restore this */
                    if ((iIndex == theTabs.getSelectedIndex()) && (!isPricesSelected)) {
                        isPatternsSelected = true;
                    }

                    /* Remove the patterns tab */
                    theTabs.removeTabAt(iIndex);
                }
            }

            /* else if not rates but tab is present */
        } else if (iIndex != -1) {
            /* If the tab is selected then set statement as selected */
            if (iIndex == theTabs.getSelectedIndex()) {
                theTabs.setSelectedIndex(0);
            }

            /* Remove the rates tab */
            theTabs.removeTabAt(iIndex);
        }

        /* Access the Prices index */
        iIndex = theTabs.indexOfTab(TITLE_PRICES);

        /* If the account has prices */
        if ((theAccount != null) && (theAccount.isPriced())) {

            /* Add the Prices if not present */
            if (iIndex == -1) {
                theTabs.addTab(TITLE_PRICES, thePrices.getPanel());

                /* If the prices were selected */
                if (isPricesSelected) {
                    /* Re-select the prices */
                    iIndex = theTabs.indexOfTab(TITLE_PRICES);
                    theTabs.setSelectedIndex(iIndex);
                }

                /* Remove the patterns tab if present */
                iIndex = theTabs.indexOfTab(TITLE_PATTERNS);
                if (iIndex != -1) {
                    /* Remember if Patterns are selected since we need to restore this */
                    if (iIndex == theTabs.getSelectedIndex()) {
                        isPatternsSelected = true;
                    }

                    /* Remove the patterns tab */
                    theTabs.removeTabAt(iIndex);
                }
            }

            /* else if not prices but tab is present */
        } else if (iIndex != -1) {
            /* If the tab is selected then set statement as selected */
            if (iIndex == theTabs.getSelectedIndex()) {
                theTabs.setSelectedIndex(0);
            }

            /* Remove the prices tab */
            theTabs.removeTabAt(iIndex);
        }

        /* Access the Patterns index */
        iIndex = theTabs.indexOfTab(TITLE_PATTERNS);

        /* If the account is not closed */
        if ((theAccount != null) && (!theAccount.isClosed())) {

            /* Add the Patterns if not present */
            if (iIndex == -1) {
                theTabs.addTab(TITLE_PATTERNS, thePatterns.getPanel());

                /* If the patterns were selected */
                if (isPatternsSelected) {
                    /* Re-select the patterns */
                    iIndex = theTabs.indexOfTab(TITLE_PATTERNS);
                    theTabs.setSelectedIndex(iIndex);
                }
            }

            /* else if not patterned but tab is present */
        } else if (iIndex != -1) {
            /* If the tab is selected then set statement as selected */
            if (iIndex == theTabs.getSelectedIndex()) {
                theTabs.setSelectedIndex(0);
            }

            /* Remove the units tab */
            theTabs.removeTabAt(iIndex);
        }

        /* Notify listeners */
        fireStateChanged();
    }

    /**
     * Select an explicit account and period.
     * @param pAccount the account to select
     * @param pSource the period source
     */
    public void selectAccount(final Account pAccount,
                              final DateDayRangeSelect pSource) {
        /* Protect against exceptions */
        try {
            /* Adjust the date selection for the statements appropriately */
            theStatement.selectPeriod(pSource);

            /* Adjust the account selection */
            theSelect.setSelection(pAccount);
            setSelection(theSelect.getSelected());

            /* Create SavePoint */
            theSelect.createSavePoint();
        } catch (Exception e) {
            /* Build the error */
            JDataException myError = new JDataException(ExceptionClass.DATA, "Failed to change selection", e);

            /* Show the error */
            theError.setError(myError);

            /* Restore SavePoint */
            theSelect.restoreSavePoint();
        }
    }

    /**
     * Add a pattern from a statement line.
     * @param pLine the line to add
     */
    public void addPattern(final StatementLine pLine) {
        /* Pass through to the Patterns table */
        thePatterns.addPattern(pLine);

        /* Change focus to the Patterns */
        gotoNamedTab(TITLE_PATTERNS);
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
     * Determine the current focus.
     */
    protected void determineFocus() {
        /* Access the selected component */
        Component myComponent = theTabs.getSelectedComponent();

        /* If the selected component is Statement */
        if (myComponent.equals(theStatement.getPanel())) {
            /* Set the debug focus */
            // theStatement.getDataEntry().setFocus();
            theStatement.requestFocusInWindow();

            /* If the selected component is Rates */
        } else if (myComponent.equals(theRates.getPanel())) {
            /* Set the debug focus */
            // theRates.getDataEntry().setFocus();
            theRates.requestFocusInWindow();

            /* If the selected component is Prices */
        } else if (myComponent.equals(thePrices.getPanel())) {
            /* Set the debug focus */
            // thePrices.getDataEntry().setFocus();
            thePrices.requestFocusInWindow();

            /* If the selected component is Patterns */
        } else if (myComponent.equals(thePatterns.getPanel())) {
            /* Set the debug focus */
            // thePatterns.getDataEntry().setFocus();
            thePatterns.requestFocusInWindow();
        }
    }

    /**
     * The listener class.
     */
    private final class AccountListener implements ActionListener, ChangeListener {
        @Override
        public void stateChanged(final ChangeEvent e) {
            Object o = e.getSource();

            /* If it is the tabs */
            if (theTabs.equals(o)) {
                /* Determine the focus */
                determineFocus();
                /* If this is the error panel reporting */
            } else if (theError.equals(o)) {
                /* Determine whether we have an error */
                boolean isError = theError.hasError();

                /* Hide selection panel on error */
                theSelect.setVisible(!isError);

                /* Lock tabs area */
                theTabs.setEnabled(!isError);

                /* Lock Save Buttons */
                theSaveButtons.setEnabled(!isError);
            } else if (theSelect.equals(o)) {

            } else if ((theRates.equals(o)) || (thePrices.equals(o)) || (thePatterns.equals(o))
                    || (theStatement.equals(o))) {
                notifyChanges();
            }
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the save buttons */
            if (theSaveButtons.equals(o)) {
                /* Perform the save command */
                performCommand(evt.getActionCommand());

            } else if ((theStatement.equals(o)) && (evt instanceof ActionDetailEvent)) {
                /* Cascade the command upwards */
                cascadeActionEvent((ActionDetailEvent) evt);
            }
        }
    }
}
