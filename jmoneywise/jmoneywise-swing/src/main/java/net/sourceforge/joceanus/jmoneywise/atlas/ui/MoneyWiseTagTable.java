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

import java.util.Objects;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag.TransactionTagList;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysOnCellCommit;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableIconColumn;

/**
 * MoneyWise Tag Table.
 */
public class MoneyWiseTagTable
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
    private final UpdateEntry<TransactionTag, MoneyWiseDataType> theUpdateEntry;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theError;

    /**
     * The underlying table.
     */
    private final TethysSwingTableManager<MetisLetheField, TransactionTag> theTable;

    /**
     * The new button.
     */
    private final TethysButton theNewButton;

    /**
     * The edit list.
     */
    private TransactionTagList theTags;

    /**
     * is the table editing?
     */
    private boolean isEditing;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    MoneyWiseTagTable(final MoneyWiseView pView,
                      final UpdateSet<MoneyWiseDataType> pUpdateSet,
                      final MetisErrorPanel pError) {
        /* Store parameters */
        theView = pView;
        theError = pError;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Build the Update set */
        theUpdateSet = pUpdateSet;
        theUpdateEntry = theUpdateSet.registerType(MoneyWiseDataType.TRANSTAG);

        /* Create the table */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        theTable = myGuiFactory.newTable();

        /* Create new button */
        theNewButton = myGuiFactory.newButton();
        MetisIcon.configureNewIconButton(theNewButton);

        /* Set disabled indication and filter */
        theTable.setOnCommitError(this::setError);
        theTable.setOnValidateError(this::showValidateError);
        theTable.setOnCellEditState(this::handleEditState);
        theTable.setDisabled(TransactionTag::isDisabled);
        theTable.setChanged(this::isFieldChanged);
        theTable.setError(this::isFieldInError);
        theTable.setComparator(TransactionTag::compareTo);
        theTable.setEditable(true);

        /* Create the name column */
        theTable.declareStringColumn(TransactionTag.FIELD_NAME)
                .setValidator(this::isValidName)
                .setCellValueFactory(TransactionTag::getName)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(TransactionTag::setName, r, v));

        /* Create the description column */
        theTable.declareStringColumn(TransactionTag.FIELD_DESC)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(TransactionTag::getDesc)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(TransactionTag::setDescription, r, v));

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        final TethysSwingTableIconColumn<MetisAction, MetisLetheField, TransactionTag> myStatusColumn
                = theTable.declareIconColumn(StaticData.FIELD_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet);
        myStatusColumn.setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Add listeners */
        theNewButton.getEventRegistrar().addEventListener(e -> addNewItem());
        theUpdateSet.getEventRegistrar().addEventListener(e -> theTable.fireTableDataChanged());
    }

    @Override
    public Integer getId() {
        return theTable.getId();
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TethysNode getNode() {
        return theTable.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theTable.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theTable.setVisible(pVisible);
    }

    /**
     * Obtain the new button.
     * @return the new Button
     */
    TethysButton getNewButton() {
        return theNewButton;
    }

    /**
     * Refresh data.
     */
    void refreshData() throws OceanusException {
        final MoneyWiseData myData = theView.getData();
        final TransactionTagList myBase = myData.getDataList(TransactionTagList.class);
        theTags = (TransactionTagList) myBase.deriveList(ListStyle.EDIT);
        theTags.mapData();
        theTable.setItems(theTags.getUnderlyingList());
        theUpdateEntry.setDataList(theTags);
    }

    /**
     * Cancel editing.
     */
    void cancelEditing() {
        theTable.cancelEditing();
    }

    /**
     * Is the table editing?
     * @return true/false
     */
    boolean isEditing() {
        return isEditing;
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    void determineFocus(final MetisViewerEntry pEntry) {
        /* Request the focus */
        theTable.requestFocus();

        /* Set the required focus */
        pEntry.setFocus(theUpdateEntry.getName());
    }

    /**
     * Select tag.
     * @param pTag the tag to select
     */
    void selectTag(final TransactionTag pTag) {
        /* Select the row and ensure that it is visible */
        theTable.selectRowWithScroll(pTag);
    }

    /**
     * Delete row.
     * @param pRow the row
     * @param pValue the value (ignored)
     */
    private void deleteRow(final TransactionTag pRow,
                           final Object pValue) {
        pRow.setDeleted(true);
    }

    /**
     * New item.
     */
    private void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Create the new tag */
            final TransactionTag myTag = theTags.addNewItem();
            myTag.setDefaults();

            /* Add the new item */
            myTag.setNewVersion();
            theTags.add(myTag);

            /* Validate the new item and notify of the changes */
            myTag.validate();
            theUpdateSet.incrementVersion();

            /* Lock the table */
            setEnabled(false);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new tag", e);

            /* Show the error */
            setError(myError);
        }
    }

    /**
     * Update value.
     * @param <V> the value type
     * @param pOnCommit the update function
     * @param pRow the row to update
     * @param pValue the value
     * @throws OceanusException on error
     */
    private <V> void updateField(final TethysOnCellCommit<TransactionTag, V> pOnCommit,
                                 final TransactionTag pRow,
                                 final V pValue) throws OceanusException {
        /* Push history */
        pRow.pushHistory();

        /* Protect against Exceptions */
        try {
            /* Set the item value */
            pOnCommit.commitCell(pRow, pValue);

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
            notifyChanges();
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
     * Notify that there have been changes to this list.
     */
    protected void notifyChanges() {
        /* Notify listeners */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * is field in error?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private boolean isFieldInError(final MetisLetheField pField,
                                   final TransactionTag pItem) {
        return pItem.getFieldErrors(pField) != null;
    }

    /**
     * is field changed?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private boolean isFieldChanged(final MetisLetheField pField,
                                   final TransactionTag pItem) {
        return pItem.fieldChanged(pField).isDifferent();
    }

    /**
     * isFiltered?
     * @param pRow the row
     * @return true/false
     */
    private boolean isFiltered(final TransactionTag pRow) {
        return true;
    }

    /**
     * is Valid name?
     * @param pNewName the new name
     * @param pRow the row
     * @return error message or null
     */
    private String isValidName(final String pNewName,
                               final TransactionTag pRow) {
        /* Reject null name */
        if (pNewName == null) {
            return "Null Name not allowed";
        }

        /* Reject invalid name */
        if (!DataItem.validString(pNewName, ",")) {
            return "Invalid characters in name";
        }

        /* Reject name that is too long */
        if (DataItem.byteLength(pNewName) > TransactionTag.NAMELEN) {
            return "Name too long";
        }

        /* Loop through the existing values */
        for (TransactionTag myValue : theTags.getUnderlyingList()) {
            /* Ignore self and deleted */
            if (myValue.isDeleted() || myValue.equals(pRow)) {
                continue;
            }

            /* Check for duplicate */
            if (pNewName.equals(myValue.getName())) {
                return "Duplicate name";
            }
        }

        /* Valid name */
        return null;
    }

    /**
     * is Valid description?
     * @param pNewDesc the new description
     * @param pRow the row
     * @return error message or null
     */
    private String isValidDesc(final String pNewDesc,
                               final TransactionTag pRow) {
        /* Reject description that is too long */
        if (pNewDesc != null
                && DataItem.byteLength(pNewDesc) > StaticData.DESCLEN) {
            return "Description too long";
        }

        /* Valid description */
        return null;
    }

    /**
     * Show validation error TODO use panel.
     * @param pError the error message
     */
    private void showValidateError(final String pError) {
        System.out.println(Objects.requireNonNullElse(pError, "Error cleared"));
    }

    /**
     * Check edit state.
     * @param pState the new state
     */
    private void handleEditState(final Boolean pState) {
        isEditing = pState;
        notifyChanges();
    }
}
