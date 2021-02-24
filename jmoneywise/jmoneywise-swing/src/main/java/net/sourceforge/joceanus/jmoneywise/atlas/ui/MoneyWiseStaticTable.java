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
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData.StaticList;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticInterface;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusIcon;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
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
 * MoneyWise Static Table.
 * @param <L> the list type
 * @param <T> the data type
 * @param <S> the static class
 */
public class MoneyWiseStaticTable<L extends StaticList<T, S, MoneyWiseDataType>, T extends StaticData<T, S, MoneyWiseDataType>, S extends Enum<S> & StaticInterface>
        implements TethysEventProvider<PrometheusDataEvent>, TethysComponent {
    /**
     * The view.
     */
    private final MoneyWiseView theView;

    /**
     * The ItemType.
     */
    private final MoneyWiseDataType theItemType;

    /**
     * The list class.
     */
    private final Class<L> theClass;

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
    private final UpdateEntry<T, MoneyWiseDataType> theUpdateEntry;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theError;

    /**
     * The underlying table.
     */
    private final TethysSwingTableManager<MetisLetheField, T> theTable;

    /**
     * The enabled column.
     */
    private final TethysSwingTableIconColumn<Boolean, MetisLetheField, T> theEnabledColumn;

    /**
     * The new button.
     */
    private final TethysScrollButtonManager<S> theNewButton;

    /**
     * The edit list.
     */
    private L theStatic;

    /**
     * show disabled.
     */
    private boolean showAll;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     * @param pDataType the dataType
     * @param pListClass the listClass
     */
    MoneyWiseStaticTable(final MoneyWiseView pView,
                         final UpdateSet<MoneyWiseDataType> pUpdateSet,
                         final MetisErrorPanel pError,
                         final MoneyWiseDataType pDataType,
                         final Class<L> pListClass) {
        /* Store parameters */
        theView = pView;
        theError = pError;
        theItemType = pDataType;
        theClass = pListClass;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Build the Update set */
        theUpdateSet = pUpdateSet;
        theUpdateEntry = theUpdateSet.registerType(pDataType);

        /* Create the table */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        theTable = myGuiFactory.newTable();

        /* Create new button */
        theNewButton = myGuiFactory.newScrollButton();
        MetisIcon.configureNewScrollButton(theNewButton);

        /* Set disabled indication and filter */
        theTable.setOnCommitError(this::setError);
        theTable.setDisabled(StaticData::isDisabled);
        theTable.setChanged(this::isFieldChanged);
        theTable.setError(this::isFieldInError);
        theTable.setComparator(StaticData::compareTo);
        theTable.setEditable(true);

        /* Create the class column */
        theTable.declareStringColumn(StaticData.FIELD_CLASS)
                .setCellValueFactory(r -> r.getStaticClass().toString())
                .setEditable(false);

        /* Create the name column */
        theTable.declareStringColumn(StaticData.FIELD_NAME)
                .setValidator(this::isValidName)
                .setCellValueFactory(StaticData::getName)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(StaticData::setName, r, v));

        /* Create the description column */
        theTable.declareStringColumn(StaticData.FIELD_DESC)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(StaticData::getDesc)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(StaticData::setDescription, r, v));

        /* Create the enabled column */
        final TethysIconMapSet<Boolean> myEnabledMapSet = PrometheusIcon.configureEnabledIconButton();
        theEnabledColumn = theTable.declareIconColumn(StaticData.FIELD_ENABLED, Boolean.class)
                .setIconMapSet(r -> myEnabledMapSet);
        theEnabledColumn.setCellValueFactory(StaticData::getEnabled)
                .setVisible(false)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(StaticData::setEnabled, r, v));

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        final TethysSwingTableIconColumn<MetisAction, MetisLetheField, T> myStatusColumn
                = theTable.declareIconColumn(StaticData.FIELD_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet);
        myStatusColumn.setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
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
     * Obtain the item type.
     * @return the item type
     */
    protected MoneyWiseDataType getItemType() {
        return theItemType;
    }

    /**
     * Obtain the new button.
     * @return the new Button
     */
    TethysScrollButtonManager<S> getNewButton() {
        return theNewButton;
    }

    /**
     * Refresh data.
     */
    void refreshData() throws OceanusException {
        final MoneyWiseData myData = theView.getData();
        final StaticList<T, S, MoneyWiseDataType> myStatic = myData.getDataList(theClass);
        theStatic = theClass.cast(myStatic.deriveList(ListStyle.EDIT));
        theStatic.mapData();
        theTable.setItems(theStatic.getUnderlyingList());
        theUpdateEntry.setDataList(theStatic);
    }

    /**
     * handle new static class.
     */
    private void handleNewClass() {
        /* Access the new class */
        cancelEditing();
        final S myClass = theNewButton.getValue();

        /* Protect the action */
        try {
            /* Look to find a deleted value */
            T myValue = theStatic.findItemByClass(myClass);

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
            notifyChanges();

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
        final TethysScrollMenu<S> myMenu = theNewButton.getMenu();
        myMenu.removeAllItems();

        /* Loop through the missing classes */
        for (S myValue : theStatic.getMissingClasses()) {
            /* Create a new MenuItem and add it to the popUp */
            myMenu.addItem(myValue);
        }
    }

    /**
     * Cancel editing.
     */
    void cancelEditing() {
        theTable.cancelEditing();
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
     * Select static data.
     * @param pStatic the static data
     */
    @SuppressWarnings("unchecked")
    void selectStatic(final StaticData<?, ?, MoneyWiseDataType> pStatic) {
        theTable.selectRowWithScroll((T) pStatic);
    }

    /**
     * Is the static table full?
     * @return true/false
     */
    boolean isFull() {
        return theStatic == null
                || theStatic.isFull();
    }

    /**
     * Delete row.
     * @param pRow the row
     * @param pValue the value (ignored)
     */
    private void deleteRow(final T pRow,
                           final Object pValue) {
        pRow.setDeleted(true);
    }

    /**
     * Update value.
     * @param <V> the value type
     * @param pOnCommit the update function
     * @param pRow the row to update
     * @param pValue the value
     * @throws OceanusException on error
     */
    private <V> void updateField(final TethysOnColumnCommit<T, V> pOnCommit,
                                 final T pRow,
                                 final V pValue) throws OceanusException {
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
                                   final T pItem) {
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
                                   final T pItem) {
        return pItem.fieldChanged(pField).isDifferent();
    }

    /**
     * adjust showALL.
     * @param pShow show disabled entries
     */
    void setShowAll(final boolean pShow) {
        showAll = pShow;
        cancelEditing();
        theTable.setFilter(this::isFiltered);
        theEnabledColumn.setVisible(showAll);
    }

    /**
     * isFiltered?
     * @param pRow the row
     * @return true/false
     */
    private boolean isFiltered(final T pRow) {
        return pRow.getEnabled() || showAll;
    }

    /**
     * is Valid name?
     * @param pNewName the new name
     * @param pRow the row
     * @return error message or null
     */
    private String isValidName(final String pNewName,
                               final T pRow) {
        /* Reject null name */
        if (pNewName == null) {
            return "Null Name not allowed";
        }

        /* Reject invalid name */
        if (!DataItem.validString(pNewName, null)) {
            return "Invalid characters in name";
        }

        /* Reject name that is too long */
        if (DataItem.byteLength(pNewName) > StaticData.NAMELEN) {
            return "Name too long";
        }

        /* Loop through the existing values */
        for (T myValue : theStatic.getUnderlyingList()) {
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
                               final T pRow) {
        /* Reject description that is too long */
        if (pNewDesc != null
                && DataItem.byteLength(pNewDesc) > StaticData.DESCLEN) {
            return "Description too long";
        }

        /* Valid description */
        return null;
    }
}
