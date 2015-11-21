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
package net.sourceforge.joceanus.jmoneywise.ui.swing;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.JDataProfile;
import net.sourceforge.joceanus.jmetis.viewer.ViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.ViewerManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jprometheus.ui.swing.ActionButtons;
import net.sourceforge.joceanus.jprometheus.ui.swing.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTable;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventManager;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusActionRegistration;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusChangeRegistration;
import net.sourceforge.joceanus.jtethys.ui.swing.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Top-level panel for Account/EventCategories.
 */
public class CategoryPanel
        extends JPanel
        implements JOceanusEventProvider {
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
     * Text for DataEntry Title.
     */
    private static final String NLS_DATAENTRY = MoneyWiseUIResource.CATEGORY_DATAENTRY.getValue();

    /**
     * Text for Selection Title.
     */
    private static final String NLS_SELECT = MoneyWiseUIResource.CATEGORY_TITLE_SELECT.getValue();

    /**
     * Text for Selection Prompt.
     */
    private static final String NLS_DATA = MoneyWiseUIResource.CATEGORY_PROMPT_SELECT.getValue();

    /**
     * The Event Manager.
     */
    private final transient JOceanusEventManager theEventManager;

    /**
     * The Data View.
     */
    private final transient SwingView theView;

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
    private final transient ViewerEntry theDataEntry;

    /**
     * The action buttons panel.
     */
    private final ActionButtons theActionButtons;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * Are we refreshing?
     */
    private boolean isRefreshing = false;

    /**
     * Constructor.
     * @param pView the data view
     */
    public CategoryPanel(final SwingView pView) {
        /* Store details */
        theView = pView;

        /* Create the event manager */
        theEventManager = new JOceanusEventManager();

        /* Build the Update set */
        theUpdateSet = new UpdateSet<MoneyWiseDataType>(pView, MoneyWiseDataType.class);

        /* Create the top level debug entry for this view */
        ViewerManager myDataMgr = pView.getViewerManager();
        ViewerEntry mySection = pView.getDataEntry(DataControl.DATA_MAINT);
        theDataEntry = myDataMgr.newEntry(NLS_DATAENTRY);
        theDataEntry.addAsChildOf(mySection);
        theDataEntry.setObject(theUpdateSet);

        /* Create the error panel */
        theError = new ErrorPanel(myDataMgr, theDataEntry);

        /* Create the action buttons panel */
        theActionButtons = new ActionButtons(theUpdateSet);

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
        JEnablePanel mySelect = new JEnablePanel();
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
        mySelect.setPreferredSize(new Dimension(JDataTable.WIDTH_PANEL, PANEL_PAD));
        mySelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, PANEL_PAD));

        /* Create the header panel */
        JPanel myHeader = new JPanel();
        myHeader.setLayout(new BoxLayout(myHeader, BoxLayout.X_AXIS));
        myHeader.add(mySelect);
        myHeader.add(theError);
        myHeader.add(theActionButtons);

        /* Now define the panel */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(myHeader);
        add(Box.createVerticalGlue());
        add(theCardPanel);

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);

        /* Create the listener */
        new CategoryListener();
    }

    @Override
    public JOceanusEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
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
     * @throws JOceanusException on error
     */
    protected void refreshData() throws JOceanusException {
        /* Obtain the active profile */
        JDataProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Categories");

        /* Note that we are refreshing */
        isRefreshing = true;

        /* Refresh the tables */
        theDepositTable.refreshData();
        theCashTable.refreshData();
        theLoanTable.refreshData();
        theEventTable.refreshData();
        theTagTable.refreshData();

        /* Clear refreshing flag */
        isRefreshing = false;
        setVisibility();

        /* Touch the updateSet */
        theDataEntry.setObject(theUpdateSet);

        /* Complete the task */
        myTask.end();
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
     * Has this set of panels got the session focus?
     * @return true/false
     */
    public boolean hasSession() {
        /* Determine whether we have session focus */
        boolean hasSession = theDepositTable.hasSession();
        if (!hasSession) {
            hasSession = theCashTable.hasSession();
        }
        if (!hasSession) {
            hasSession = theLoanTable.hasSession();
        }
        if (!hasSession) {
            hasSession = theEventTable.hasSession();
        }
        if (!hasSession) {
            hasSession = theTagTable.hasSession();
        }

        /* Return to caller */
        return hasSession;
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
     * Does this panel have item editing occurring?
     * @return true/false
     */
    public boolean isItemEditing() {
        /* Determine whether we have item editing */
        boolean isEditing = theDepositTable.isItemEditing();
        if (!isEditing) {
            isEditing = theCashTable.isItemEditing();
        }
        if (!isEditing) {
            isEditing = theLoanTable.isItemEditing();
        }
        if (!isEditing) {
            isEditing = theEventTable.isItemEditing();
        }
        if (!isEditing) {
            isEditing = theTagTable.isItemEditing();
        }

        /* Return to caller */
        return isEditing;
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
        boolean isItemEditing = isItemEditing();

        /* Update the action buttons */
        theActionButtons.setEnabled(true);
        theActionButtons.setVisible(hasUpdates && !isItemEditing);

        /* Update the selection */
        theSelectButton.setEnabled(!isItemEditing);
        theFilterCardPanel.setEnabled(!isItemEditing);

        /* Alert listeners that there has been a change */
        theEventManager.fireStateChanged();
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
            implements JOceanusActionEventListener, JOceanusChangeEventListener, PropertyChangeListener {
        /**
         * Error Registration.
         */
        private final JOceanusChangeRegistration theErrorReg;

        /**
         * Action Registration.
         */
        private final JOceanusActionRegistration theActionReg;

        /**
         * Constructor.
         */
        private CategoryListener() {
            /* Handle interesting items */
            theErrorReg = theError.getEventRegistrar().addChangeListener(this);
            theActionReg = theActionButtons.getEventRegistrar().addActionListener(this);
            theSelectButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, this);

            /* Handle sub-panels */
            JOceanusEventRegistrar myRegistrar = theDepositTable.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
            myRegistrar = theCashTable.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
            myRegistrar = theLoanTable.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
            myRegistrar = theEventTable.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
            myRegistrar = theTagTable.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
        }

        @Override
        public void processActionEvent(final JOceanusActionEvent pEvent) {
            /* if this is the action buttons reporting */
            if (theActionReg.isRelevant(pEvent)) {
                /* Cancel Editing */
                cancelEditing();

                /* Process the command */
                theUpdateSet.processCommand(pEvent.getActionId(), theError);

                /* else handle or cascade */
            } else {
                /* Access event and obtain details */
                switch (pEvent.getActionId()) {
                    /* Pass through the event */
                    case MainTab.ACTION_VIEWSTATEMENT:
                    case MainTab.ACTION_VIEWACCOUNT:
                    case MainTab.ACTION_VIEWTAXYEAR:
                    case MainTab.ACTION_VIEWSTATIC:
                        theEventManager.cascadeActionEvent(pEvent);
                        break;

                    /* Access subPanels */
                    case MainTab.ACTION_VIEWCATEGORY:
                        selectCategory(pEvent.getDetails());
                        break;
                    case MainTab.ACTION_VIEWTAG:
                        selectTag(pEvent.getDetails());
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public void processChangeEvent(final JOceanusChangeEvent pEvent) {
            /* If this is the error panel reporting */
            if (theErrorReg.isRelevant(pEvent)) {
                /* Determine whether we have an error */
                boolean isError = theError.hasError();

                /* Hide selection panel on error */
                theSelectButton.setVisible(!isError);

                /* Lock scroll-able area */
                theCardPanel.setEnabled(!isError);

                /* Lock Action Buttons */
                theActionButtons.setEnabled(!isError);

                /* if this is one of the sub-panels */
            } else if (!isRefreshing) {
                setVisibility();
            }
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
        DEPOSITS(MoneyWiseDataType.DEPOSITCATEGORY),

        /**
         * Cash.
         */
        CASH(MoneyWiseDataType.CASHCATEGORY),

        /**
         * Loans.
         */
        LOANS(MoneyWiseDataType.LOANCATEGORY),

        /**
         * Events.
         */
        EVENTS(MoneyWiseDataType.TRANSCATEGORY),

        /**
         * Tags.
         */
        EVENTTAGS(MoneyWiseDataType.TRANSTAG);

        /**
         * The String name.
         */
        private String theName;

        /**
         * Constructor.
         * @param pDataType the dataType
         */
        PanelName(final MoneyWiseDataType pDataType) {
            theName = pDataType.getListName();
        }

        @Override
        public String toString() {
            /* return the name */
            return theName;
        }
    }
}
