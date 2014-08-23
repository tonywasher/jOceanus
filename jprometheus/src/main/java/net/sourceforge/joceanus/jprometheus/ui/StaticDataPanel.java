/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataFieldEnum;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jprometheus.data.StaticData.StaticList;
import net.sourceforge.joceanus.jprometheus.data.StaticInterface;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Top level panel for static data.
 * @author Tony Washer
 * @param <E> the data type enum class
 */
public class StaticDataPanel<E extends Enum<E> & JDataFieldEnum>
        extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -1089967527250331711L;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 5;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(StaticDataPanel.class.getName());

    /**
     * Text for Selection Title.
     */
    private static final String NLS_SELECT = NLS_BUNDLE.getString("SelectionTitle");

    /**
     * Text for Data.
     */
    private static final String NLS_DATA = NLS_BUNDLE.getString("StaticData");

    /**
     * Text for Show Disabled.
     */
    private static final String NLS_DISABLED = NLS_BUNDLE.getString("ShowDisabled");

    /**
     * The data control.
     */
    private final transient DataControl<?, E> theControl;

    /**
     * The table card panel.
     */
    private final JEnablePanel theTableCard;

    /**
     * The card layout for the table.
     */
    private final CardLayout theTableLayout;

    /**
     * The new card panel.
     */
    private final JEnablePanel theNewCard;

    /**
     * The card layout for the new button.
     */
    private final CardLayout theNewLayout;

    /**
     * The selection button.
     */
    private final JScrollButton<StaticDataTable<?, ?, ?, E>> theSelectButton;

    /**
     * The disabled check box.
     */
    private final JCheckBox theDisabledCheckBox;

    /**
     * The Panel map.
     */
    private final Map<String, StaticDataTable<?, ?, ?, E>> theMap;

    /**
     * The Active panel.
     */
    private StaticDataTable<?, ?, ?, E> theActive;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * The action buttons panel.
     */
    private final ActionButtons theActionButtons;

    /**
     * The UpdateSet.
     */
    private final transient UpdateSet<E> theUpdateSet;

    /**
     * The data entry.
     */
    private final transient JDataEntry theDataEntry;

    /**
     * The selection listener.
     */
    private final transient StaticListener theListener;

    /**
     * Obtain the updateList.
     * @return the viewSet
     */
    protected UpdateSet<E> getUpdateSet() {
        return theUpdateSet;
    }

    /**
     * Constructor.
     * @param pControl the data control
     * @param pClass the dataType class
     */
    public StaticDataPanel(final DataControl<?, E> pControl,
                           final Class<E> pClass) {
        /* Store control */
        theControl = pControl;

        /* Build the Update set */
        theUpdateSet = new UpdateSet<E>(pControl, pClass);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theControl.getDataMgr();
        JDataEntry mySection = theControl.getDataEntry(DataControl.DATA_MAINT);
        theDataEntry = myDataMgr.new JDataEntry(StaticData.class.getSimpleName());
        theDataEntry.addAsChildOf(mySection);
        theDataEntry.setObject(theUpdateSet);

        /* Create the error panel */
        theError = new ErrorPanel(myDataMgr, theDataEntry);

        /* Create the action buttons panel */
        theActionButtons = new ActionButtons(theUpdateSet);

        /* Create selection button and label */
        JLabel myLabel = new JLabel(NLS_DATA);
        theSelectButton = new JScrollButton<StaticDataTable<?, ?, ?, E>>();

        /* Create the CheckBox */
        theDisabledCheckBox = new JCheckBox(NLS_DISABLED);

        /* Add the listener for item changes */
        theListener = new StaticListener();
        theSelectButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, theListener);
        theError.addChangeListener(theListener);
        theActionButtons.addActionListener(theListener);
        theDisabledCheckBox.addItemListener(theListener);

        /* Create the selection panel */
        JPanel mySelect = new JPanel();
        mySelect.setBorder(BorderFactory.createTitledBorder(NLS_SELECT));

        /* Create the standard strut */
        Dimension myStrutSize = new Dimension(STRUT_WIDTH, 0);

        /* Create the new card panel */
        theNewCard = new JEnablePanel();
        theNewLayout = new CardLayout();
        theNewCard.setLayout(theNewLayout);

        /* Create the layout for the selection panel */
        mySelect.setLayout(new BoxLayout(mySelect, BoxLayout.X_AXIS));
        mySelect.add(Box.createRigidArea(myStrutSize));
        mySelect.add(myLabel);
        mySelect.add(Box.createRigidArea(myStrutSize));
        mySelect.add(theSelectButton);
        mySelect.add(Box.createRigidArea(myStrutSize));
        mySelect.add(Box.createHorizontalGlue());
        mySelect.add(theDisabledCheckBox);
        mySelect.add(Box.createHorizontalGlue());
        mySelect.add(theNewCard);
        mySelect.add(Box.createHorizontalGlue());

        /* Create the header panel */
        JPanel myHeader = new JPanel();
        myHeader.setLayout(new BoxLayout(myHeader, BoxLayout.X_AXIS));
        myHeader.add(mySelect);
        myHeader.add(theError);
        myHeader.add(theActionButtons);

        /* Create the table card panel */
        theTableCard = new JEnablePanel();
        theTableLayout = new CardLayout();
        theTableCard.setLayout(theTableLayout);

        /* Create the panel map */
        theMap = new LinkedHashMap<String, StaticDataTable<?, ?, ?, E>>();

        /* Now define the panel */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(myHeader);
        add(Box.createVerticalGlue());
        add(theTableCard);

        /* Set visibility of new button */
        showNewButton();

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);
    }

    /**
     * Add static panel.
     * @param pItemType the type of the list
     * @param pListClass the class of the list
     * @param <L> the list type
     * @param <S> the static class
     * @param <T> the data type
     */
    public <L extends StaticList<T, S, E>, T extends StaticData<T, S, E>, S extends Enum<S> & StaticInterface> void addStatic(final E pItemType,
                                                                                                                              final Class<L> pListClass) {
        /* Create the new panel */
        StaticDataTable<L, T, S, E> myPanel = new StaticDataTable<L, T, S, E>(theControl, theUpdateSet, theError, pItemType, pListClass);

        /* Add the listener for the panel */
        myPanel.addChangeListener(theListener);

        /* Access list name */
        String myName = pItemType.getFieldName();

        /* Add to the card panels */
        theTableCard.add(myPanel.getPanel(), myName);
        theNewCard.add(myPanel.getNewButton(), myName);

        /* Add to the Map */
        theMap.put(myName, myPanel);

        /* If we do not have an active panel */
        if (theActive == null) {
            /* select this panel */
            setSelection(myName);
        }
    }

    /**
     * Determine Focus.
     */
    public void determineFocus() {
        /* Request the focus */
        if (theActive != null) {
            theActive.determineFocus(theDataEntry);
        }
    }

    /**
     * Set Selection.
     * @param pName the name that is selected
     */
    private void setSelection(final String pName) {
        /* Select the correct static */
        theTableLayout.show(theTableCard, pName);
        theNewLayout.show(theNewCard, pName);
        theNewCard.setMaximumSize(new Dimension(theSelectButton.getWidth(), theSelectButton.getHeight()));
        theActive = theMap.get(pName);
        determineFocus();

        /* Show selection text */
        theSelectButton.setText(pName);
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
     * Has this set of panels got the session focus?
     * @return true/false
     */
    public boolean hasSession() {
        /* Return to caller */
        return hasUpdates();
    }

    /**
     * Has this set of tables got errors?
     * @return true/false
     */
    public boolean hasErrors() {
        /* Return to caller */
        return theUpdateSet.hasErrors();
    }

    /**
     * Select static data.
     * @param pStatic the static data to select
     */
    public void selectStatic(final StaticData<?, ?, E> pStatic) {
        /* Access the item type */
        E myType = pStatic.getItemType();
        String myName = myType.getFieldName();

        /* Access the panel */
        StaticDataTable<?, ?, ?, E> myPanel = theMap.get(myName);
        if (myPanel != null) {
            /* Update selection */
            myPanel.selectStatic(pStatic);
            setSelection(myName);
        }
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     * @throws JOceanusException on error
     */
    public void refreshData() throws JOceanusException {
        /* Loop through the map */
        for (StaticDataTable<?, ?, ?, ?> myPanel : theMap.values()) {
            /* Refresh the panel */
            myPanel.refreshData();
        }

        /* Touch the updateSet */
        theDataEntry.setObject(theUpdateSet);
    }

    /**
     * Show disabled.
     * @param pShow true/false
     */
    public void showDisabled(final boolean pShow) {
        /* Loop through the map */
        for (StaticDataTable<?, ?, ?, ?> myPanel : theMap.values()) {
            /* Update the panel */
            myPanel.setShowAll(pShow);
        }
    }

    /**
     * Cancel Editing of underlying tables.
     */
    private void cancelEditing() {
        /* Loop through the map */
        for (StaticDataTable<?, ?, ?, ?> myPanel : theMap.values()) {
            /* Refresh the underlying children */
            myPanel.cancelEditing();
        }
    }

    /**
     * Show New button.
     */
    private void showNewButton() {
        /* Set visibility of New Button */
        boolean showNew = theActive != null
                          && !theActive.isFull();
        theNewCard.setVisible(showNew);
    }

    /**
     * Set Visibility.
     */
    protected void setVisibility() {
        /* Determine whether we have updates */
        boolean hasUpdates = hasUpdates();

        /* Update the action buttons */
        theActionButtons.setEnabled(true);
        theActionButtons.setVisible(hasUpdates);

        /* Set visibility of New Button */
        showNewButton();

        /* Alert listeners that there has been a change */
        fireStateChanged();
    }

    /**
     * Listener class.
     */
    private final class StaticListener
            implements PropertyChangeListener, ChangeListener, ActionListener, ItemListener {
        /**
         * Data menu builder.
         */
        private final JScrollMenuBuilder<StaticDataTable<?, ?, ?, E>> theDataMenuBuilder;

        /**
         * Constructor.
         */
        private StaticListener() {
            /* Access builder */
            theDataMenuBuilder = theSelectButton.getMenuBuilder();
            theDataMenuBuilder.addChangeListener(this);
        }

        /**
         * Build StaticData menu.
         */
        private void buildDataMenu() {
            /* Reset the popUp menu */
            theDataMenuBuilder.clearMenu();

            /* Record active item */
            JMenuItem myActive = null;

            /* Loop through the panels */
            Iterator<Map.Entry<String, StaticDataTable<?, ?, ?, E>>> myIterator = theMap.entrySet().iterator();
            while (myIterator.hasNext()) {
                Map.Entry<String, StaticDataTable<?, ?, ?, E>> myEntry = myIterator.next();

                /* Create a new JMenuItem and add it to the popUp */
                StaticDataTable<?, ?, ?, E> myTable = myEntry.getValue();
                JMenuItem myItem = theDataMenuBuilder.addItem(myTable, myEntry.getKey());

                /* If this is the active panel */
                if (myTable.equals(theActive)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theDataMenuBuilder.showItem(myActive);
        }

        @Override
        public void stateChanged(final ChangeEvent evt) {
            /* Access reporting object */
            Object o = evt.getSource();

            /* If this is the error panel reporting */
            if (theError.equals(o)) {
                /* Determine whether we have an error */
                boolean isError = theError.hasError();

                /* Hide selection panel on error */
                theSelectButton.setVisible(!isError);

                /* Lock scroll-able area */
                theTableCard.setEnabled(!isError);

                /* Lock Action Buttons */
                theActionButtons.setEnabled(!isError);

                /* if this is the data menu builder reporting */
            } else if (theDataMenuBuilder.equals(o)) {
                /* Cancel Editing */
                cancelEditing();

                /* build data menu */
                buildDataMenu();

                /* if this is one of the static data panels */
            } else if (o instanceof StaticDataTable) {
                /* Adjust visibility */
                setVisibility();
            }
        }

        @Override
        public void actionPerformed(final ActionEvent pEvent) {
            /* Access reporting object and command */
            Object o = pEvent.getSource();
            String myCmd = pEvent.getActionCommand();

            /* if this is the action buttons reporting */
            if (theActionButtons.equals(o)) {
                /* Cancel Editing */
                cancelEditing();

                /* Process the command */
                theUpdateSet.processCommand(myCmd, theError);

                /* Adjust visibility */
                setVisibility();
            }
        }

        @Override
        public void itemStateChanged(final ItemEvent pEvent) {
            /* Access reporting object */
            Object o = pEvent.getSource();

            /* if this is the disabled check box reporting */
            if (theDisabledCheckBox.equals(o)) {
                /* Adjust the disabled settings */
                showDisabled(theDisabledCheckBox.isSelected());
            }
        }

        @Override
        public void propertyChange(final PropertyChangeEvent pEvent) {
            /* If the panel has changed */
            if (!Difference.isEqual(theActive, theSelectButton.getValue())) {
                /* Move correct card to front */
                setSelection(theSelectButton.getDisplayName());
                showNewButton();
            }
        }
    }
}
