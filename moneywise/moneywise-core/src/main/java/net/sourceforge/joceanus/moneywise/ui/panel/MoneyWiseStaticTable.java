/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.ui.panel;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.ui.MetisAction;
import net.sourceforge.joceanus.metis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.metis.ui.MetisIcon;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataList.PrometheusListStyle;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataItem.PrometheusStaticList;
import net.sourceforge.joceanus.prometheus.ui.PrometheusIcon;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIGenericWrapper;
import net.sourceforge.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableManager;

/**
 * MoneyWise Static Table.
 *
 * @param <L> the list type
 * @param <T> the data type
 */
public class MoneyWiseStaticTable<L extends PrometheusStaticList<T>, T extends PrometheusStaticDataItem>
        extends MoneyWiseBaseTable<T> {
    /**
     * Class column width.
     */
    private static final int WIDTH_CLASS = 90;

    /**
     * The list class.
     */
    private final Class<L> theClass;

    /**
     * The enabled column.
     */
    private final TethysUITableColumn<Boolean, MetisDataFieldId, T> theEnabledColumn;

    /**
     * The new button.
     */
    private final TethysUIScrollButtonManager<TethysUIGenericWrapper> theNewButton;

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
     *
     * @param pView      the view
     * @param pEditSet   the editSet
     * @param pError     the error panel
     * @param pDataType  the dataType
     * @param pListClass the listClass
     */
    MoneyWiseStaticTable(final MoneyWiseView pView,
                         final PrometheusEditSet pEditSet,
                         final MetisErrorPanel pError,
                         final MoneyWiseStaticDataType pDataType,
                         final Class<L> pListClass) {
        /* Store parameters */
        super(pView, pEditSet, pError, pDataType);
        theClass = pListClass;

        /* Access the gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        final TethysUITableManager<MetisDataFieldId, T> myTable = getTable();

        /* Create new button */
        theNewButton = myGuiFactory.buttonFactory().newScrollButton(TethysUIGenericWrapper.class);
        MetisIcon.configureNewScrollButton(theNewButton);

        /* Set table configuration */
        myTable.setDisabled(PrometheusStaticDataItem::isDisabled)
                .setComparator(PrometheusStaticDataItem::compareTo);

        /* Create the class column */
        myTable.declareStringColumn(PrometheusDataResource.STATICDATA_CLASS)
                .setCellValueFactory(r -> r.getStaticClass().toString())
                .setEditable(false)
                .setColumnWidth(WIDTH_CLASS);

        /* Create the name column */
        myTable.declareStringColumn(PrometheusDataResource.DATAITEM_FIELD_NAME)
                .setValidator(this::isValidName)
                .setCellValueFactory(PrometheusStaticDataItem::getName)
                .setEditable(true)
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(PrometheusStaticDataItem::setName, r, v));

        /* Create the description column */
        myTable.declareStringColumn(PrometheusDataResource.DATAITEM_FIELD_DESC)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(PrometheusStaticDataItem::getDesc)
                .setEditable(true)
                .setColumnWidth(WIDTH_DESC)
                .setOnCommit((r, v) -> updateField(PrometheusStaticDataItem::setDescription, r, v));

        /* Create the enabled column */
        final TethysUIIconMapSet<Boolean> myEnabledMapSet = PrometheusIcon.configureEnabledIconButton(myGuiFactory);
        theEnabledColumn = myTable.declareIconColumn(PrometheusDataResource.STATICDATA_ENABLED, Boolean.class)
                .setIconMapSet(r -> myEnabledMapSet)
                .setCellValueFactory(PrometheusStaticDataItem::getEnabled)
                .setVisible(false)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setColumnWidth(WIDTH_ICON)
                .setOnCommit((r, v) -> updateField(PrometheusStaticDataItem::setEnabled, r, v));

        /* Create the Active column */
        final TethysUIIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton(myGuiFactory);
        myTable.declareIconColumn(PrometheusDataResource.DATAITEM_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setColumnWidth(WIDTH_ICON)
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Add listeners */
        final OceanusEventRegistrar<TethysUIEvent> myRegistrar = theNewButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewClass());
        theNewButton.setMenuConfigurator(e -> buildNewMenu());
        setShowAll(false);
    }

    /**
     * Obtain the new button.
     *
     * @return the new Button
     */
    TethysUIScrollButtonManager<TethysUIGenericWrapper> getNewButton() {
        return theNewButton;
    }

    @Override
    protected void refreshData() throws OceanusException {
        final MoneyWiseDataSet myData = getView().getData();
        final PrometheusStaticList<T> myStatic = myData.getDataList(theClass);
        theStatic = theClass.cast(myStatic.deriveList(PrometheusListStyle.EDIT));
        theStatic.mapData();
        getTable().setItems(theStatic.getUnderlyingList());
        getEditEntry().setDataList(theStatic);
        restoreSelected();
    }

    /**
     * handle new static class.
     */
    private void handleNewClass() {
        /* Create a new profile */
        final OceanusProfile myTask = getView().getNewProfile("addNewClass");

        /* Access the new class */
        cancelEditing();
        final PrometheusStaticDataClass myClass = (PrometheusStaticDataClass) theNewButton.getValue().getData();

        /* Protect the action */
        try {
            /* Look to find a deleted value */
            myTask.startTask("addToList");
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
                myValue.adjustMapForItem();
            }

            /* Update the table */
            myTask.startTask("incrementVersion");
            getEditSet().incrementVersion();
            updateTableData();
            selectItem(myValue);
            notifyChanges();

            /* Handle exceptions */
        } catch (OceanusException e) {
            setError(e);
        }

        /* End the task */
        myTask.end();
    }

    /**
     * Build the menu of available new items.
     */
    private void buildNewMenu() {
        /* Reset the menu popUp */
        final TethysUIScrollMenu<TethysUIGenericWrapper> myMenu = theNewButton.getMenu();
        myMenu.removeAllItems();

        /* Loop through the missing classes */
        for (PrometheusStaticDataClass myValue : theStatic.getMissingClasses()) {
            /* Create a new MenuItem and add it to the popUp */
            myMenu.addItem(new TethysUIGenericWrapper(myValue));
        }
    }

    /**
     * Select static data.
     *
     * @param pStatic the static data
     */
    @SuppressWarnings("unchecked")
    void selectStatic(final PrometheusStaticDataItem pStatic) {
        getTable().selectRow((T) pStatic);
    }

    /**
     * Is the static table full?
     *
     * @return true/false
     */
    boolean isFull() {
        return theStatic == null
                || theStatic.isFull();
    }

    /**
     * adjust showALL.
     *
     * @param pShow show disabled entries
     */
    void setShowAll(final boolean pShow) {
        showAll = pShow;
        cancelEditing();
        getTable().setFilter(this::isFiltered);
        theEnabledColumn.setVisible(showAll);
        restoreSelected();
    }

    @Override
    protected boolean isFiltered(final T pRow) {
        return super.isFiltered(pRow) && (pRow.getEnabled() || showAll);
    }
}
