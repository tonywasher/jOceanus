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
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData.StaticList;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticInterface;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusIcon;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;

/**
 * MoneyWise Static Table.
 * @param <L> the list type
 * @param <T> the data type
 * @param <S> the static class
 */
public class MoneyWiseStaticTable<L extends StaticList<T, S, MoneyWiseDataType>, T extends StaticData<T, S, MoneyWiseDataType>, S extends Enum<S> & StaticInterface>
        extends MoneyWiseBaseTable<T> {
    /**
     * The list class.
     */
    private final Class<L> theClass;

    /**
     * The enabled column.
     */
    private final TethysTableColumn<Boolean, MetisLetheField, T> theEnabledColumn;

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
        super(pView, pUpdateSet, pError, pDataType);
        theClass = pListClass;

        /* Access the gui factory */
        final TethysGuiFactory myGuiFactory = pView.getGuiFactory();
        final TethysSwingTableManager<MetisLetheField, T> myTable = getTable();

        /* Create new button */
        theNewButton = myGuiFactory.newScrollButton();
        MetisIcon.configureNewScrollButton(theNewButton);

        /* Set table configuration */
        myTable.setDisabled(StaticData::isDisabled)
               .setComparator(StaticData::compareTo);

        /* Create the class column */
        myTable.declareStringColumn(StaticData.FIELD_CLASS)
               .setCellValueFactory(r -> r.getStaticClass().toString())
               .setEditable(false);

        /* Create the name column */
        myTable.declareStringColumn(StaticData.FIELD_NAME)
               .setValidator(this::isValidName)
               .setCellValueFactory(StaticData::getName)
               .setEditable(true)
               .setOnCommit((r, v) -> updateField(StaticData::setName, r, v));

        /* Create the description column */
        myTable.declareStringColumn(StaticData.FIELD_DESC)
               .setValidator(this::isValidDesc)
               .setCellValueFactory(StaticData::getDesc)
               .setEditable(true)
               .setOnCommit((r, v) -> updateField(StaticData::setDescription, r, v));

        /* Create the enabled column */
        final TethysIconMapSet<Boolean> myEnabledMapSet = PrometheusIcon.configureEnabledIconButton();
        theEnabledColumn = myTable.declareIconColumn(StaticData.FIELD_ENABLED, Boolean.class)
               .setIconMapSet(r -> myEnabledMapSet)
               .setCellValueFactory(StaticData::getEnabled)
               .setVisible(false)
               .setEditable(true)
               .setCellEditable(r -> !r.isActive())
               .setOnCommit((r, v) -> updateField(StaticData::setEnabled, r, v));

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        myTable.declareIconColumn(StaticData.FIELD_TOUCH, MetisAction.class)
               .setIconMapSet(r -> myActionMapSet)
               .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
               .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
               .setEditable(true)
               .setCellEditable(r -> !r.isActive())
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
    TethysScrollButtonManager<S> getNewButton() {
        return theNewButton;
    }

    @Override
    protected void refreshData() throws OceanusException {
        final MoneyWiseData myData = getView().getData();
        final StaticList<T, S, MoneyWiseDataType> myStatic = myData.getDataList(theClass);
        theStatic = theClass.cast(myStatic.deriveList(ListStyle.EDIT));
        theStatic.mapData();
        getTable().setItems(theStatic.getUnderlyingList());
        getUpdateEntry().setDataList(theStatic);
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
                myValue.adjustMapForItem();
            }

            /* Update the table */
            getUpdateSet().incrementVersion();
            getTable().fireTableDataChanged();
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
     * Select static data.
     * @param pStatic the static data
     */
    @SuppressWarnings("unchecked")
    void selectStatic(final StaticData<?, ?, MoneyWiseDataType> pStatic) {
        getTable().selectRowWithScroll((T) pStatic);
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
    }

    @Override
    protected boolean isFiltered(final T pRow) {
        return super.isFiltered(pRow) && (pRow.getEnabled() || showAll);
    }
}
