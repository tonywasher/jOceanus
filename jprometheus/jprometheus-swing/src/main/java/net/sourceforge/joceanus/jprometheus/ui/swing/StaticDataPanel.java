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
package net.sourceforge.joceanus.jprometheus.ui.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisFieldEnum;
import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jprometheus.data.StaticData.StaticList;
import net.sourceforge.joceanus.jprometheus.data.StaticInterface;
import net.sourceforge.joceanus.jprometheus.swing.JOceanusSwingUtilitySet;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;

/**
 * Top level panel for static data.
 * @author Tony Washer
 * @param <E> the data type enum class
 */
public class StaticDataPanel<E extends Enum<E> & MetisFieldEnum>
        implements TethysEventProvider<PrometheusDataEvent> {
    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 5;

    /**
     * Text for DataEntry Title.
     */
    private static final String NLS_DATAENTRY = PrometheusUIResource.STATIC_DATAENTRY.getValue();

    /**
     * Text for Selection Title.
     */
    private static final String NLS_SELECT = PrometheusUIResource.STATIC_TITLE_SELECT.getValue();

    /**
     * Text for Data.
     */
    private static final String NLS_DATA = PrometheusUIResource.STATIC_PROMPT_DATA.getValue();

    /**
     * Text for Show Disabled.
     */
    private static final String NLS_DISABLED = PrometheusUIResource.STATIC_PROMPT_DISABLED.getValue();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The UtilitySet.
     */
    private final JOceanusSwingUtilitySet theUtilitySet;

    /**
     * The data control.
     */
    private final DataControl<?, E> theControl;

    /**
     * The Panel.
     */
    private final JPanel thePanel;

    /**
     * The table card panel.
     */
    private final TethysSwingEnablePanel theTableCard;

    /**
     * The card layout for the table.
     */
    private final CardLayout theTableLayout;

    /**
     * The new card panel.
     */
    private final TethysSwingEnablePanel theNewCard;

    /**
     * The card layout for the new button.
     */
    private final CardLayout theNewLayout;

    /**
     * The selection button.
     */
    private final JScrollButton<StaticDataTable<?, ?, ?, E>> theSelectButton;

    /**
     * Data menu builder.
     */
    private final JScrollMenuBuilder<StaticDataTable<?, ?, ?, E>> theDataMenuBuilder;

    /**
     * The disabled check box.
     */
    private final JCheckBox theDisabledCheckBox;

    /**
     * The Panel map.
     */
    private final Map<String, StaticDataTable<?, ?, ?, E>> theMap;

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
    private final UpdateSet<E> theUpdateSet;

    /**
     * The data entry.
     */
    private final MetisViewerEntry theDataEntry;

    /**
     * The Active panel.
     */
    private StaticDataTable<?, ?, ?, E> theActive;

    /**
     * Constructor.
     * @param pControl the data control
     * @param pUtilitySet the utility set
     * @param pClass the dataType class
     */
    public StaticDataPanel(final DataControl<?, E> pControl,
                           final JOceanusSwingUtilitySet pUtilitySet,
                           final Class<E> pClass) {
        /* Store control */
        theControl = pControl;
        theUtilitySet = pUtilitySet;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the Panel */
        thePanel = new TethysSwingEnablePanel();

        /* Build the Update set */
        theUpdateSet = new UpdateSet<>(pControl, pClass);

        /* Create the top level debug entry for this view */
        MetisViewerManager myDataMgr = theControl.getViewerManager();
        MetisViewerEntry mySection = theControl.getDataEntry(DataControl.DATA_MAINT);
        theDataEntry = myDataMgr.newEntry(NLS_DATAENTRY);
        theDataEntry.addAsChildOf(mySection);
        theDataEntry.setObject(theUpdateSet);

        /* Create the error panel */
        theError = new ErrorPanel(myDataMgr, theDataEntry);

        /* Create the action buttons panel */
        theActionButtons = new ActionButtons(theUpdateSet);

        /* Create selection button and label */
        JLabel myLabel = new JLabel(NLS_DATA);
        theSelectButton = new JScrollButton<>();

        /* Create the CheckBox */
        theDisabledCheckBox = new JCheckBox(NLS_DISABLED);

        /* Create the selection panel */
        JPanel mySelect = new TethysSwingEnablePanel();
        mySelect.setBorder(BorderFactory.createTitledBorder(NLS_SELECT));

        /* Create the standard strut */
        Dimension myStrutSize = new Dimension(STRUT_WIDTH, 0);

        /* Create the new card panel */
        theNewCard = new TethysSwingEnablePanel();
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
        JPanel myHeader = new TethysSwingEnablePanel();
        myHeader.setLayout(new BorderLayout());
        myHeader.add(mySelect, BorderLayout.CENTER);
        myHeader.add(theError, BorderLayout.PAGE_START);
        myHeader.add(theActionButtons.getNode(), BorderLayout.LINE_END);

        /* Create the table card panel */
        theTableCard = new TethysSwingEnablePanel();
        theTableLayout = new CardLayout();
        theTableCard.setLayout(theTableLayout);

        /* Create the panel map */
        theMap = new LinkedHashMap<>();

        /* Now define the panel */
        thePanel.setLayout(new BorderLayout());
        thePanel.add(myHeader, BorderLayout.PAGE_START);
        thePanel.add(theTableCard, BorderLayout.CENTER);

        /* Set visibility of new button */
        showNewButton();

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);

        /* Add listeners */
        theDisabledCheckBox.addItemListener(e -> showDisabled(theDisabledCheckBox.isSelected()));
        theSelectButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, e -> handlePanelSelection());
        theActionButtons.getEventRegistrar().addEventListener(this::handleActionButtons);
        theError.getEventRegistrar().addEventListener(e -> handleErrorPanel());
        theDataMenuBuilder = theSelectButton.getMenuBuilder();
        theDataMenuBuilder.getEventRegistrar().addEventListener(e -> {
            cancelEditing();
            buildDataMenu();
        });
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the node.
     * @return the node
     */
    public JComponent getNode() {
        return thePanel;
    }

    /**
     * Handle action buttons.
     * @param pEvent the event
     */
    private void handleActionButtons(final TethysEvent<PrometheusUIEvent> pEvent) {
        /* Cancel Editing */
        cancelEditing();

        /* Process the command */
        theUpdateSet.processCommand(pEvent.getEventId(), theError);

        /* Adjust visibility */
        setVisibility();
    }

    /**
     * Handle panel selection.
     */
    private void handlePanelSelection() {
        /* If the panel has changed */
        if (!MetisDifference.isEqual(theActive, theSelectButton.getValue())) {
            /* Move correct card to front */
            setSelection(theSelectButton.getDisplayName());
            showNewButton();
        }
    }

    /**
     * Handle error panel.
     */
    private void handleErrorPanel() {
        /* Determine whether we have an error */
        boolean isError = theError.hasError();

        /* Hide selection panel on error */
        theSelectButton.setVisible(!isError);

        /* Lock scroll-able area */
        theTableCard.setEnabled(!isError);

        /* Lock Action Buttons */
        theActionButtons.setEnabled(!isError);
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

    /**
     * Obtain the updateList.
     * @return the viewSet
     */
    protected UpdateSet<E> getUpdateSet() {
        return theUpdateSet;
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
        StaticDataTable<L, T, S, E> myPanel = new StaticDataTable<>(theControl, theUpdateSet, theUtilitySet, theError, pItemType, pListClass);

        /* Add the listener for the panel */
        myPanel.getEventRegistrar().addEventListener(e -> setVisibility());

        /* Access list name */
        String myName = pItemType.getFieldName();

        /* Add to the card panels */
        theTableCard.add(myPanel.getNode(), myName);
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
     * @throws OceanusException on error
     */
    public void refreshData() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = theControl.getActiveTask();
        myTask = myTask.startTask("StaticData");

        /* Loop through the map */
        for (Entry<String, StaticDataTable<?, ?, ?, E>> myEntry : theMap.entrySet()) {
            /* Note the stage */
            myTask.startTask(myEntry.getKey());

            /* Refresh the panel */
            myEntry.getValue().refreshData();
        }

        /* Touch the updateSet */
        theDataEntry.setObject(theUpdateSet);

        /* Complete the task */
        myTask.end();
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
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }
}
