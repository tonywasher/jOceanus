/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.atlas.ui;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.Frequency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.Frequency.FrequencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.FrequencyClass;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusIcon;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysCheckBox;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysOnColumnCommit;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableIconColumn;

/**
 * Frequency Table.
 */
public class MoneyWiseFrequencyTable
        implements TethysEventProvider<PrometheusDataEvent>, TethysComponent {
    /**
     * The view.
     */
    private final MoneyWiseView theView;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The UpdateSet associated with the table.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The UpdateEntry.
     */
    private final UpdateEntry<Frequency, MoneyWiseDataType> theUpdateEntry;

    /**
     * The ViewerEntry.
     */
    private final MetisViewerEntry theViewerEntry;

    /**
     * The Panel.
     */
    private final TethysBorderPaneManager thePanel;

    /**
     * The underlying table.
     */
    private final TethysSwingTableManager<MetisLetheField, Frequency> theTable;

    /**
     * The enabled column.
     */
    private final TethysSwingTableIconColumn<Boolean, MetisLetheField, Frequency> theEnabledColumn;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theError;

    /**
     * The action buttons panel.
     */
    private final PrometheusActionButtons theActionButtons;

    /**
     * The disabled check box.
     */
    private final TethysCheckBox theDisabledCheckBox;

    /**
     * The select button.
     */
    private final TethysButton theSelect;

    /**
     * The new button.
     */
    private final TethysScrollButtonManager<FrequencyClass> theNewButton;

    /**
     * The edit list.
     */
    private FrequencyList theStatic;

    /**
     * show disabled.
     */
    private boolean showAll;

    /**
     * Constructor.
     * @param pView the view
     */
    public MoneyWiseFrequencyTable(final MoneyWiseView pView) {
        /* Store parameters */
        theView = pView;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Build the Update set */
        theUpdateSet = new UpdateSet<>(theView, MoneyWiseDataType.class);
        theUpdateEntry = theUpdateSet.registerType(MoneyWiseDataType.FREQUENCY);

        /* Create the table */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        theTable = myGuiFactory.newTable();

        /* Create the top level viewer entry for this view */
        theViewerEntry = pView.getViewerEntry(PrometheusViewerEntryId.STATIC);
        theViewerEntry.setTreeObject(theUpdateSet);

        /* Create the error panel */
        theError = pView.getToolkit().getToolkit().newErrorPanel(theViewerEntry);
        theError.getEventRegistrar().addEventListener(e -> handleErrorPanel());

        /* Create the select button */
        theSelect = myGuiFactory.newButton();
        theSelect.setTextOnly();
        theSelect.setText("Temporary");

        /* Create the action buttons panel */
        theActionButtons = new PrometheusActionButtons(myGuiFactory, theUpdateSet);
        theActionButtons.getEventRegistrar().addEventListener(this::handleActionButtons);

        /* Create the CheckBox */
        theDisabledCheckBox = myGuiFactory.newCheckBox("Show Disabled");
        theDisabledCheckBox.getEventRegistrar().addEventListener(e -> setShowAll(theDisabledCheckBox.isSelected()));

        /* Create new button */
        theNewButton = myGuiFactory.newScrollButton();
        MetisIcon.configureNewScrollButton(theNewButton);

        /* Create the layout for the selection panel */
        final TethysBoxPaneManager mySubPanel = myGuiFactory.newHBoxPane();
        mySubPanel.addNode(theSelect);
        mySubPanel.addSpacer();
        mySubPanel.addNode(theDisabledCheckBox);
        mySubPanel.addSpacer();
        mySubPanel.addNode(theNewButton);
        mySubPanel.setBorderTitle("Selection");

        /* Create the header panel */
        final TethysBorderPaneManager myHeader = myGuiFactory.newBorderPane();
        myHeader.setCentre(mySubPanel);
        myHeader.setNorth(theError);
        myHeader.setEast(theActionButtons);

        /* Create the Panel */
        thePanel = myGuiFactory.newBorderPane();

        /* Now define the panel */
        thePanel.setNorth(myHeader);
        thePanel.setCentre(theTable);

        /* Set visibility of new button */
        showNewButton();

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);

        /* Set disabled indication and filter */
        theTable.setOnCommitError(this::setError);
        theTable.setDisabled(StaticData::isDisabled);
        theTable.setChanged(MoneyWiseFrequencyTable::isFieldChanged);
        theTable.setError(MoneyWiseFrequencyTable::isFieldInError);
        theTable.setComparator(StaticData::compareTo);
        theTable.setEditable(true);

        /* Create the class column */
        theTable.declareStringColumn(Frequency.FIELD_CLASS)
                .setCellValueFactory(r -> r.getFrequency().toString())
                .setEditable(false);

        /* Create the name column */
        theTable.declareStringColumn(Frequency.FIELD_NAME)
                .setValidator(this::isValidName)
                .setCellValueFactory(StaticData::getName)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(StaticData::setName, r, v));

        /* Create the description column */
        theTable.declareStringColumn(Frequency.FIELD_DESC)
                .setCellValueFactory(StaticData::getDesc)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(StaticData::setDescription, r, v));

        /* Create the enabled column */
        final TethysIconMapSet<Boolean> myEnabledMapSet = PrometheusIcon.configureEnabledIconButton();
        theEnabledColumn = theTable.declareIconColumn(Frequency.FIELD_ENABLED, Boolean.class)
                                          .setIconMapSet(r -> myEnabledMapSet);
        theEnabledColumn.setCellValueFactory(StaticData::getEnabled)
                .setVisible(false)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(StaticData::setEnabled, r, v));

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        final TethysSwingTableIconColumn<MetisAction, MetisLetheField, Frequency> myStatusColumn
                = theTable.declareIconColumn(Frequency.FIELD_TOUCH, MetisAction.class)
                            .setIconMapSet(r -> myActionMapSet);
        myStatusColumn.setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName("Active")
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Add listeners */
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = theNewButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewClass());
        theNewButton.setMenuConfigurator(e -> buildNewMenu());
        theUpdateSet.getEventRegistrar().addEventListener(e -> theTable.fireTableDataChanged());
        setShowAll(false);
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TethysNode getNode() {
        return thePanel.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Handle action buttons.
     * @param pEvent the event
     */
    private void handleActionButtons(final TethysEvent<PrometheusUIEvent> pEvent) {
        /* Cancel Editing */
        theTable.cancelEditing();

        /* Process the command */
        theUpdateSet.processCommand(pEvent.getEventId(), theError);

        /* Adjust visibility */
        setVisibility();
    }

    /**
     * Set Visibility.
     */
    protected void setVisibility() {
        /* Determine whether we have updates */
        final boolean hasUpdates = theUpdateSet.hasUpdates();

        /* Update the action buttons */
        theActionButtons.setEnabled(true);
        theActionButtons.setVisible(hasUpdates);

        /* Set visibility of New Button */
        showNewButton();

        /* Alert listeners that there has been a change */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * Handle error panel.
     */
    private void handleErrorPanel() {
        /* Determine whether we have an error */
        final boolean isError = theError.hasError();

        /* Hide selection panel on error */
        //theSelectionPanel.setVisible(!isError);

        /* Lock scroll-able area */
        theTable.setEnabled(!isError);

        /* Lock Action Buttons */
        theActionButtons.setEnabled(!isError);
    }

    /**
     * Refresh data.
     */
    public void refreshData() throws OceanusException {
        final MoneyWiseData myData = theView.getData();
        theStatic = (FrequencyList) myData.getFrequencys().deriveList(ListStyle.EDIT);
        theStatic.mapData();
        theTable.setItems(theStatic.getUnderlyingList());
        theUpdateEntry.setDataList(theStatic);
    }

    /**
     * handle new static class.
     */
    private void handleNewClass() {
        /* Access the new class */
        theTable.cancelEditing();
        final FrequencyClass myClass = theNewButton.getValue();

        /* Protect the action */
        try {
            /* Look to find a deleted value */
            Frequency myValue = theStatic.findItemByClass(myClass);

            /* If we found a deleted value */
            if (myValue != null) {
                /* reinstate it */
                myValue.setDeleted(false);

                /* else we have no existing value */
            } else {
                /* Create the new value */
                myValue = theStatic.addNewItem(myClass);
                myValue.setNewVersion();
            }

            /* Update the table */
            theUpdateSet.incrementVersion();
            theTable.fireTableDataChanged();
            setVisibility();
            //notifyChanges();

            /* Handle exceptions */
        } catch (OceanusException e) {
            setError(e);
        }
    }

    /**
     * Build the menu of available new items.
     */
    private void buildNewMenu() {
        /* Reset the menu popUp */
        final TethysScrollMenu<FrequencyClass> myMenu = theNewButton.getMenu();
        myMenu.removeAllItems();

        /* Loop through the missing classes */
        for (FrequencyClass myValue : theStatic.getMissingClasses()) {
            /* Create a new MenuItem and add it to the popUp */
            myMenu.addItem(myValue);
        }
    }

    /**
     * Show New button.
     */
    private void showNewButton() {
        /* Set visibility of New Button */
        final boolean showNew = !isFull();
        theNewButton.setVisible(showNew);
    }

    /**
     * Is the frequency table full?
     * @return true/false
     */
    protected boolean isFull() {
        return theStatic == null
                || theStatic.isFull();
    }

    /**
     * Delete row.
     * @param pRow the row
     * @param pValue the value (ignored)
     */
    private void deleteRow(final Frequency pRow,
                           final Object pValue) {
        pRow.setDeleted(true);
    }

    /**
     * Update value.
     * @param <T> the value type
     * @param pOnCommit the update function
     * @param pRow the row to update
     * @param pValue the value
     * @throws OceanusException on error
     */
    private <T> void updateField(final TethysOnColumnCommit<Frequency, T> pOnCommit,
                                 final Frequency pRow,
                                 final T pValue) throws OceanusException {
        /* Push history */
        pRow.pushHistory();

        /* Protect against Exceptions */
        try {
            /* Set the item value */
            pOnCommit.commitColumn(pRow, pValue);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Reset values */
            pRow.popHistory();

            /* Throw the error */
            throw new PrometheusDataException("Failed to update field", e);
        }

        /* Check for changes */
        if (pRow.checkForHistory()) {
            /* Increment data version */
            theUpdateSet.incrementVersion();

            /* Update components to reflect changes */
            theTable.fireTableDataChanged();
            setVisibility();
            //notifyChanges();
        }
    }

    /**
     * Set the error.
     * @param pError the error
     */
    protected void setError(final OceanusException pError) {
        theError.addError(pError);
    }

    /**
     * is field in error?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private static boolean isFieldInError(final MetisLetheField pField,
                                          final Frequency pItem) {
        return pItem.getFieldErrors(pField) != null;
    }

    /**
     * is field changed?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private static boolean isFieldChanged(final MetisLetheField pField,
                                          final Frequency pItem) {
        return pItem.fieldChanged(pField).isDifferent();
    }

    /**
     * adjust showALL.
     * @param pShow show disabled entries
     */
    public void setShowAll(final boolean pShow) {
        showAll = pShow;
        theTable.cancelEditing();
        theTable.setFilter(this::isFiltered);
        theEnabledColumn.setVisible(showAll);
    }

    /**
     * isFiltered?
     * @param pRow the row
     * @return true/false
     */
    private boolean isFiltered(final Frequency pRow) {
        return pRow.getEnabled() || showAll;
    }

    /**
     * is Valid name?
     * @param pNewName the new name
     * @param pRow the row
     * @return error message or null
     */
    private String isValidName(final String pNewName,
                               final Frequency pRow) {
        /* Reject null name */
        if (pNewName == null) {
            return "Null Name not allowed";
        }

        /* Loop through the existing values */
        for (Frequency myFreq : theStatic.getUnderlyingList()) {
            /* Ignore self and deleted */
            if (myFreq.isDeleted() || myFreq.equals(pRow)) {
                continue;
            }

            /* Check for duplicate */
            if (pNewName.equals(myFreq.getName())) {
                return "Duplicate name";
            }
        }

        /* Valid name */
        return null;
    }
}
