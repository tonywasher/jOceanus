/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.SaveButtons;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Top-level panel for Account/EventCategories.
 */
public class CategoryPanel
        extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 418093805893095098L;

    /**
     * Strut width.
     */
    protected static final int STRUT_WIDTH = 5;

    /**
     * Panel padding.
     */
    protected static final int PANEL_PAD = 50;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(CategoryPanel.class.getName());

    /**
     * Text for DataEntry Title.
     */
    private static final String NLS_DATAENTRY = NLS_BUNDLE.getString("DataEntryTitle");

    /**
     * Text for Selection Title.
     */
    private static final String NLS_SELECT = NLS_BUNDLE.getString("SelectionTitle");

    /**
     * Text for Selection Prompt.
     */
    private static final String NLS_DATA = NLS_BUNDLE.getString("SelectionPrompt");

    /**
     * The select button.
     */
    private final JScrollButton<PanelName> theSelectButton;

    /**
     * The card panel.
     */
    private final JPanel theCardPanel;

    /**
     * The card layout.
     */
    private final CardLayout theLayout;

    /**
     * The filter card panel.
     */
    private final JPanel theFilterCardPanel;

    /**
     * The filter card layout.
     */
    private final CardLayout theFilterLayout;

    /**
     * The active panel.
     */
    private PanelName theActive;

    /**
     * Deposit Categories Table.
     */
    private final DepositCategoryTable theDepositTable;

    /**
     * Cash Categories Table.
     */
    private final CashCategoryTable theCashTable;

    /**
     * Loan Categories Table.
     */
    private final LoanCategoryTable theLoanTable;

    /**
     * Event Categories Table.
     */
    private final TransactionCategoryTable theEventTable;

    /**
     * Event Tags Table.
     */
    private final TransactionTagTable theTagTable;

    /**
     * The UpdateSet.
     */
    private final transient UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The data entry.
     */
    private final transient JDataEntry theDataEntry;

    /**
     * The save buttons panel.
     */
    private final SaveButtons theSaveButtons;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * Constructor.
     * @param pView the data view
     */
    public CategoryPanel(final View pView) {
        /* Build the Update set */
        theUpdateSet = new UpdateSet<MoneyWiseDataType>(pView, MoneyWiseDataType.class);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = pView.getDataMgr();
        JDataEntry mySection = pView.getDataEntry(DataControl.DATA_MAINT);
        theDataEntry = myDataMgr.new JDataEntry(NLS_DATAENTRY);
        theDataEntry.addAsChildOf(mySection);
        theDataEntry.setObject(theUpdateSet);

        /* Create the error panel */
        theError = new ErrorPanel(myDataMgr, theDataEntry);

        /* Create the save buttons panel */
        theSaveButtons = new SaveButtons(theUpdateSet);

        /* Create the table panels */
        theDepositTable = new DepositCategoryTable(pView, theUpdateSet, theError);
        theCashTable = new CashCategoryTable(pView, theUpdateSet, theError);
        theLoanTable = new LoanCategoryTable(pView, theUpdateSet, theError);
        theEventTable = new TransactionCategoryTable(pView, theUpdateSet, theError);
        theTagTable = new TransactionTagTable(pView, theUpdateSet, theError);

        /* Create selection button and label */
        JLabel myLabel = new JLabel(NLS_DATA);
        theSelectButton = new JScrollButton<PanelName>();
        buildSelectMenu();

        /* Create the card panel */
        theCardPanel = new JEnablePanel();
        theLayout = new CardLayout();
        theCardPanel.setLayout(theLayout);

        /* Add to the card panels */
        theCardPanel.add(theDepositTable.getPanel(), PanelName.DEPOSITS.toString());
        theCardPanel.add(theCashTable.getPanel(), PanelName.CASH.toString());
        theCardPanel.add(theLoanTable.getPanel(), PanelName.LOANS.toString());
        theCardPanel.add(theEventTable.getPanel(), PanelName.EVENTS.toString());
        theCardPanel.add(theTagTable.getPanel(), PanelName.EVENTTAGS.toString());
        theActive = PanelName.DEPOSITS;
        theSelectButton.setValue(theActive);

        /* Create the card panel */
        theFilterCardPanel = new JEnablePanel();
        theFilterLayout = new CardLayout();
        theFilterCardPanel.setLayout(theFilterLayout);

        /* Add to the card panels */
        theFilterCardPanel.add(theDepositTable.getFilterPanel(), PanelName.DEPOSITS.toString());
        theFilterCardPanel.add(theCashTable.getFilterPanel(), PanelName.CASH.toString());
        theFilterCardPanel.add(theLoanTable.getFilterPanel(), PanelName.LOANS.toString());
        theFilterCardPanel.add(theEventTable.getFilterPanel(), PanelName.EVENTS.toString());
        theFilterCardPanel.add(theTagTable.getFilterPanel(), PanelName.EVENTTAGS.toString());

        /* Create the selection panel */
        JPanel mySelect = new JPanel();
        mySelect.setBorder(BorderFactory.createTitledBorder(NLS_SELECT));

        /* Create the layout for the selection panel */
        mySelect.setLayout(new BoxLayout(mySelect, BoxLayout.X_AXIS));
        mySelect.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        mySelect.add(myLabel);
        mySelect.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        mySelect.add(theSelectButton);
        mySelect.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        mySelect.add(Box.createHorizontalGlue());
        mySelect.add(theFilterCardPanel);
        mySelect.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        mySelect.setMaximumSize(new Dimension(JDataTable.WIDTH_PANEL + PANEL_PAD, PANEL_PAD));

        /* Now define the panel */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(mySelect);
        add(theError);
        add(Box.createVerticalGlue());
        add(theCardPanel);
        add(theSaveButtons);

        /* Create the listener */
        CategoryListener myListener = new CategoryListener();
        theSelectButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
        theDepositTable.addChangeListener(myListener);
        theCashTable.addChangeListener(myListener);
        theLoanTable.addChangeListener(myListener);
        theEventTable.addChangeListener(myListener);
        theTagTable.addChangeListener(myListener);
        theSaveButtons.addActionListener(myListener);

        /* Hide the save buttons initially */
        theSaveButtons.setVisible(false);
    }

    /**
     * Build select menu.
     */
    private void buildSelectMenu() {
        /* Create builder */
        JScrollMenuBuilder<PanelName> myBuilder = theSelectButton.getMenuBuilder();

        /* Loop through the panels */
        for (PanelName myPanel : PanelName.values()) {
            /* Create a new JMenuItem for the panel */
            myBuilder.addItem(myPanel);
        }
    }

    /**
     * Refresh data.
     */
    protected void refreshData() throws JOceanusException {
        /* Refresh the tables */
        theDepositTable.refreshData();
        theCashTable.refreshData();
        theLoanTable.refreshData();
        theEventTable.refreshData();
        theTagTable.refreshData();

        /* Enable the save buttons */
        theSaveButtons.setEnabled(true);

        /* Touch the updateSet */
        theDataEntry.setObject(theUpdateSet);
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Switch on active component */
        switch (theActive) {
            case DEPOSITS:
                theDepositTable.determineFocus(theDataEntry);
                break;
            case CASH:
                theCashTable.determineFocus(theDataEntry);
                break;
            case LOANS:
                theLoanTable.determineFocus(theDataEntry);
                break;
            case EVENTS:
                theEventTable.determineFocus(theDataEntry);
                break;
            case EVENTTAGS:
                theTagTable.determineFocus(theDataEntry);
                break;
            default:
                break;
        }
    }

    /**
     * Does this panel have updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        /* Determine whether we have updates */
        boolean hasUpdates = theDepositTable.hasUpdates();
        if (!hasUpdates) {
            hasUpdates = theCashTable.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = theLoanTable.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = theEventTable.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = theTagTable.hasUpdates();
        }

        /* Return to caller */
        return hasUpdates;
    }

    /**
     * Does this panel have errors?
     * @return true/false
     */
    public boolean hasErrors() {
        /* Determine whether we have errors */
        boolean hasErrors = theDepositTable.hasErrors();
        if (!hasErrors) {
            hasErrors = theCashTable.hasErrors();
        }
        if (!hasErrors) {
            hasErrors = theLoanTable.hasErrors();
        }
        if (!hasErrors) {
            hasErrors = theEventTable.hasErrors();
        }
        if (!hasErrors) {
            hasErrors = theTagTable.hasErrors();
        }

        /* Return to caller */
        return hasErrors;
    }

    /**
     * Select category.
     * @param pCategory the category to select
     */
    protected void selectCategory(final Object pCategory) {
        /* Determine which panel to show */
        if (pCategory instanceof DepositCategory) {
            theDepositTable.selectCategory((DepositCategory) pCategory);
            showPanel(PanelName.DEPOSITS);
        } else if (pCategory instanceof CashCategory) {
            theCashTable.selectCategory((CashCategory) pCategory);
            showPanel(PanelName.CASH);
        } else if (pCategory instanceof LoanCategory) {
            theLoanTable.selectCategory((LoanCategory) pCategory);
            showPanel(PanelName.LOANS);
        } else if (pCategory instanceof TransactionCategory) {
            theEventTable.selectCategory((TransactionCategory) pCategory);
            showPanel(PanelName.EVENTS);
        }
    }

    /**
     * Select tag.
     * @param pTag the category to select
     */
    protected void selectTag(final Object pTag) {
        /* Determine which panel to show */
        if (pTag instanceof TransactionTag) {
            theTagTable.selectTag((TransactionTag) pTag);
            showPanel(PanelName.EVENTTAGS);
        }
    }

    /**
     * Show panel.
     * @param pName the panel name
     */
    private void showPanel(final PanelName pName) {
        /* Obtain name of panel */
        String myName = pName.toString();

        /* Move correct card to front */
        theLayout.show(theCardPanel, myName);
        theFilterLayout.show(theFilterCardPanel, myName);

        /* Note the active panel */
        theActive = pName;
        theSelectButton.setText(myName);

        /* Determine the focus */
        determineFocus();
    }

    /**
     * Set Visibility.
     */
    protected void setVisibility() {
        /* Determine whether we have updates */
        boolean hasUpdates = hasUpdates();

        /* Update the save buttons */
        theSaveButtons.setEnabled(true);
        theSaveButtons.setVisible(hasUpdates);

        /* Alert listeners that there has been a change */
        fireStateChanged();
    }

    /**
     * Cancel Editing of underlying tables.
     */
    private void cancelEditing() {
        /* Cancel editing on subPanels */
        theDepositTable.cancelEditing();
        theCashTable.cancelEditing();
        theLoanTable.cancelEditing();
        theEventTable.cancelEditing();
        theTagTable.cancelEditing();
    }

    /**
     * Listener.
     */
    private final class CategoryListener
            implements ActionListener, ChangeListener, PropertyChangeListener {
        @Override
        public void actionPerformed(final ActionEvent pEvent) {
            Object o = pEvent.getSource();
            String myCmd = pEvent.getActionCommand();

            /* if this is the save buttons reporting */
            if (theSaveButtons.equals(o)) {
                /* Cancel Editing */
                cancelEditing();

                /* Process the command */
                theUpdateSet.processCommand(myCmd, theError);

                /* Adjust visibility */
                setVisibility();
            }
        }

        @Override
        public void stateChanged(final ChangeEvent e) {
            /* Adjust visibility */
            setVisibility();
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            Object o = evt.getSource();

            /* if this event relates to the select button */
            if (theSelectButton.equals(o)) {
                /* Cancel Editing */
                cancelEditing();

                /* Show the correct panel */
                showPanel(theSelectButton.getValue());
            }
        }
    }

    /**
     * Panel names.
     */
    private enum PanelName {
        /**
         * Deposits.
         */
        DEPOSITS,

        /**
         * Cash.
         */
        CASH,

        /**
         * Loans.
         */
        LOANS,

        /**
         * Events.
         */
        EVENTS,

        /**
         * Tags.
         */
        EVENTTAGS;

        /**
         * The String name.
         */
        private String theName;

        @Override
        public String toString() {
            /* If we have not yet loaded the name */
            if (theName == null) {
                /* Load the name */
                theName = NLS_BUNDLE.getString(name());
            }

            /* return the name */
            return theName;
        }
    }
}
