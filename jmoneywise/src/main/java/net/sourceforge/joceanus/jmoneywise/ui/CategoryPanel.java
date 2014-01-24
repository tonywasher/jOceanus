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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;

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
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(CategoryPanel.class.getName());

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
    private final JButton theSelectButton;

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
     * Account Categories Table.
     */
    private final AccountCategoryTable theAccountTable;

    /**
     * Event Categories Table.
     */
    private final EventCategoryTable theEventTable;

    /**
     * Constructor.
     * @param pView the data view
     */
    public CategoryPanel(final View pView) {
        /* Create the table panels */
        theAccountTable = new AccountCategoryTable(pView);
        theEventTable = new EventCategoryTable(pView);

        /* Create selection button and label */
        JLabel myLabel = new JLabel(NLS_DATA);
        theSelectButton = new JButton(ArrowIcon.DOWN);
        theSelectButton.setVerticalTextPosition(AbstractButton.CENTER);
        theSelectButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the card panel */
        theCardPanel = new JEnablePanel();
        theLayout = new CardLayout();
        theCardPanel.setLayout(theLayout);

        /* Add to the card panels */
        theCardPanel.add(theAccountTable.getPanel(), PanelName.ACCOUNTS.toString());
        theCardPanel.add(theEventTable.getPanel(), PanelName.EVENTS.toString());
        theActive = PanelName.ACCOUNTS;
        theSelectButton.setText(theActive.toString());

        /* Create the card panel */
        theFilterCardPanel = new JEnablePanel();
        theFilterLayout = new CardLayout();
        theFilterCardPanel.setLayout(theFilterLayout);

        /* Add to the card panels */
        theFilterCardPanel.add(theAccountTable.getFilterPanel(), PanelName.ACCOUNTS.toString());
        theFilterCardPanel.add(theEventTable.getFilterPanel(), PanelName.EVENTS.toString());

        /* Create the selection panel */
        JPanel mySelect = new JPanel();
        mySelect.setBorder(BorderFactory.createTitledBorder(NLS_SELECT));

        /* Create the layout for the selection panel */
        mySelect.setLayout(new BoxLayout(mySelect, BoxLayout.X_AXIS));
        mySelect.add(myLabel);
        mySelect.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        mySelect.add(theSelectButton);
        mySelect.add(Box.createHorizontalGlue());
        mySelect.add(theFilterCardPanel);

        /* Now define the panel */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(mySelect);
        add(theCardPanel);

        /* Create the listener */
        CategoryListener myListener = new CategoryListener();
        theSelectButton.addActionListener(myListener);
        theAccountTable.addChangeListener(myListener);
        theEventTable.addChangeListener(myListener);
    }

    /**
     * Refresh data.
     */
    protected void refreshData() {
        theAccountTable.refreshData();
        theEventTable.refreshData();
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Switch on active component */
        switch (theActive) {
            case ACCOUNTS:
                theAccountTable.determineFocus();
                break;
            case EVENTS:
                theEventTable.determineFocus();
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
        boolean hasUpdates = theAccountTable.hasUpdates();
        if (!hasUpdates) {
            hasUpdates = theEventTable.hasUpdates();
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
        boolean hasErrors = theAccountTable.hasErrors();
        if (!hasErrors) {
            hasErrors = theEventTable.hasErrors();
        }

        /* Return to caller */
        return hasErrors;
    }

    /**
     * Listener.
     */
    private final class CategoryListener
            implements ActionListener, ChangeListener {
        /**
         * Show Selection menu.
         */
        private void showSelectMenu() {
            /* Create a new popUp menu */
            JPopupMenu myPopUp = new JPopupMenu();

            /* Loop through the panel names */
            for (PanelName myName : PanelName.values()) {
                /* Add reference */
                CategoryAction myAction = new CategoryAction(myName);
                JMenuItem myItem = new JMenuItem(myAction);
                myPopUp.add(myItem);
            }

            /* Show the AnalysisType menu in the correct place */
            Rectangle myLoc = theSelectButton.getBounds();
            myPopUp.show(theSelectButton, 0, myLoc.height);
        }

        @Override
        public void actionPerformed(final ActionEvent pEvent) {
            Object o = pEvent.getSource();

            /* If this event relates to the SelectButton */
            if (theSelectButton.equals(o)) {
                /* Show the selection menu */
                showSelectMenu();
            }
        }

        @Override
        public void stateChanged(final ChangeEvent e) {
            /* Pass on notification */
            fireStateChanged();
        }
    }

    /**
     * Category action class.
     */
    private final class CategoryAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 9120967972698885733L;

        /**
         * Category name.
         */
        private final PanelName theName;

        /**
         * Constructor.
         * @param pName the panel name
         */
        private CategoryAction(final PanelName pName) {
            super(pName.toString());
            theName = pName;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Move correct card to front */
            theLayout.show(theCardPanel, theName.toString());
            theFilterLayout.show(theFilterCardPanel, theName.toString());

            /* Note the active panel */
            theActive = theName;
            theSelectButton.setText(theName.toString());
        }
    }

    /**
     * Panel names.
     */
    private enum PanelName {
        /**
         * Accounts.
         */
        ACCOUNTS,

        /**
         * Events.
         */
        EVENTS;

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
