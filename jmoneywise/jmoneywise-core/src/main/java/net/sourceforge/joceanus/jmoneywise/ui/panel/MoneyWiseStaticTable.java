/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.panel;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataId;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataItem.StaticList;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataClass;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcon;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIGenericWrapper;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableColumn;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;

/**
 * MoneyWise Static Table.
 * @param <L> the list type
 * @param <T> the data type
 */
public class MoneyWiseStaticTable<L extends StaticList<T>, T extends StaticDataItem>
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
    private final TethysUITableColumn<Boolean, PrometheusDataFieldId, T> theEnabledColumn;

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
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     * @param pDataType the dataType
     * @param pListClass the listClass
     */
    MoneyWiseStaticTable(final MoneyWiseView pView,
                         final UpdateSet pUpdateSet,
                         final MetisErrorPanel pError,
                         final MoneyWiseDataType pDataType,
                         final Class<L> pListClass) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, pDataType);
        theClass = pListClass;

        /* Access the gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        final TethysUITableManager<PrometheusDataFieldId, T> myTable = getTable();

        /* Create new button */
        theNewButton = myGuiFactory.buttonFactory().newScrollButton(TethysUIGenericWrapper.class);
        MetisIcon.configureNewScrollButton(theNewButton);

        /* Set table configuration */
        myTable.setDisabled(StaticDataItem::isDisabled)
               .setComparator(StaticDataItem::compareTo);

        /* Create the class column */
        myTable.declareStringColumn(PrometheusDataId.CLASS)
               .setCellValueFactory(r -> r.getStaticClass().toString())
               .setEditable(false)
               .setColumnWidth(WIDTH_CLASS);

        /* Create the name column */
        myTable.declareStringColumn(PrometheusDataId.NAME)
               .setValidator(this::isValidName)
               .setCellValueFactory(StaticDataItem::getName)
               .setEditable(true)
               .setColumnWidth(WIDTH_NAME)
               .setOnCommit((r, v) -> updateField(StaticDataItem::setName, r, v));

        /* Create the description column */
        myTable.declareStringColumn(PrometheusDataId.DESC)
               .setValidator(this::isValidDesc)
               .setCellValueFactory(StaticDataItem::getDesc)
               .setEditable(true)
               .setColumnWidth(WIDTH_DESC)
               .setOnCommit((r, v) -> updateField(StaticDataItem::setDescription, r, v));

        /* Create the enabled column */
        final TethysUIIconMapSet<Boolean> myEnabledMapSet = PrometheusIcon.configureEnabledIconButton(myGuiFactory);
        theEnabledColumn = myTable.declareIconColumn(PrometheusDataId.ENABLED, Boolean.class)
               .setIconMapSet(r -> myEnabledMapSet)
               .setCellValueFactory(StaticDataItem::getEnabled)
               .setVisible(false)
               .setEditable(true)
               .setCellEditable(r -> !r.isActive())
               .setColumnWidth(WIDTH_ICON)
               .setOnCommit((r, v) -> updateField(StaticDataItem::setEnabled, r, v));

        /* Create the Active column */
        final TethysUIIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton(myGuiFactory);
        myTable.declareIconColumn(PrometheusDataId.TOUCH, MetisAction.class)
               .setIconMapSet(r -> myActionMapSet)
               .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
               .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
               .setEditable(true)
               .setCellEditable(r -> !r.isActive())
               .setColumnWidth(WIDTH_ICON)
               .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Add listeners */
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = theNewButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewClass());
        theNewButton.setMenuConfigurator(e -> buildNewMenu());
        setShowAll(false);
    }

    /**
     * Obtain the new button.
     * @return the new Button
     */
    TethysUIScrollButtonManager<TethysUIGenericWrapper> getNewButton() {
        return theNewButton;
    }

    @Override
    protected void refreshData() throws OceanusException {
        final MoneyWiseData myData = getView().getData();
        final StaticList<T> myStatic = myData.getDataList(theClass);
        theStatic = theClass.cast(myStatic.deriveList(ListStyle.EDIT));
        theStatic.mapData();
        getTable().setItems(theStatic.getUnderlyingList());
        getUpdateEntry().setDataList(theStatic);
        restoreSelected();
    }

    /**
     * handle new static class.
     */
    private void handleNewClass() {
        /* Access the new class */
        cancelEditing();
        final StaticDataClass myClass = (StaticDataClass) theNewButton.getValue().getData();

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
                myValue.adjustMapForItem();
            }

            /* Update the table */
            getUpdateSet().incrementVersion();
            updateTableData();
            selectItem(myValue);
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
        final TethysUIScrollMenu<TethysUIGenericWrapper> myMenu = theNewButton.getMenu();
        myMenu.removeAllItems();

        /* Loop through the missing classes */
        for (StaticDataClass myValue : theStatic.getMissingClasses()) {
            /* Create a new MenuItem and add it to the popUp */
            myMenu.addItem(new TethysUIGenericWrapper(myValue));
        }
    }

    /**
     * Select static data.
     * @param pStatic the static data
     */
    @SuppressWarnings("unchecked")
    void selectStatic(final StaticDataItem pStatic) {
        getTable().selectRow((T) pStatic);
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
     * adjust showALL.
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
