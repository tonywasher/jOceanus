/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.ui.swing;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JTable;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldIconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldStringCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldIconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldStringCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData.StaticList;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticInterface;
import net.sourceforge.joceanus.jprometheus.lethe.swing.PrometheusSwingToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusIcon;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableColumn.PrometheusDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.lethe.views.DataControl;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingNode;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;

/**
 * Static Data Table.
 * @param <L> the list type
 * @param <T> the data type
 * @param <S> the static class
 * @param <E> the data type enum class
 */
public class PrometheusStaticDataTable<L extends StaticList<T, S, E>, T extends StaticData<T, S, E>, S extends Enum<S> & StaticInterface, E extends Enum<E>>
        extends PrometheusDataTable<T, E> {
    /**
     * Class column title.
     */
    private static final String TITLE_CLASS = StaticData.FIELD_CLASS.getName();

    /**
     * Name column title.
     */
    private static final String TITLE_NAME = StaticData.FIELD_NAME.getName();

    /**
     * Description column title.
     */
    private static final String TITLE_DESC = StaticData.FIELD_DESC.getName();

    /**
     * Enabled column title.
     */
    private static final String TITLE_ENABLED = StaticData.FIELD_ENABLED.getName();

    /**
     * Active column title.
     */
    private static final String TITLE_ACTIVE = PrometheusUIResource.STATIC_TITLE_ACTIVE.getValue();

    /**
     * The Data view.
     */
    private final DataControl<?, E> theControl;

    /**
     * The field manager.
     */
    private final MetisSwingFieldManager theFieldMgr;

    /**
     * The Panel.
     */
    private final TethysSwingEnablePanel thePanel;

    /**
     * The new button.
     */
    private final TethysSwingScrollButtonManager<S> theNewButton;

    /**
     * MenuBuilder.
     */
    private final TethysScrollMenu<S> theMenu;

    /**
     * The ItemType.
     */
    private final E theItemType;

    /**
     * The Data class.
     */
    private final Class<L> theClass;

    /**
     * The List.
     */
    private L theStatic;

    /**
     * The Table Model.
     */
    private final StaticModel theModel;

    /**
     * The Columns model.
     */
    private final StaticColumnModel theColumns;

    /**
     * The UpdateSet.
     */
    private final UpdateSet<E> theUpdateSet;

    /**
     * The UpdateEntry.
     */
    private final UpdateEntry<T, E> theUpdateEntry;

    /**
     * The Error panel.
     */
    private final MetisErrorPanel theError;

    /**
     * Constructor.
     * @param pControl the data control
     * @param pUpdateSet the update set
     * @param pUtilitySet the utility set
     * @param pError the error panel
     * @param pItemType the item type
     * @param pListClass the list class
     */
    public PrometheusStaticDataTable(final DataControl<?, E> pControl,
                                     final UpdateSet<E> pUpdateSet,
                                     final PrometheusSwingToolkit pUtilitySet,
                                     final MetisErrorPanel pError,
                                     final E pItemType,
                                     final Class<L> pListClass) {
        /* initialise the underlying class */
        super(pUtilitySet.getGuiFactory());

        /* Record the passed details */
        theError = pError;
        theItemType = pItemType;
        theClass = pListClass;
        theControl = pControl;
        theFieldMgr = pUtilitySet.getFieldManager();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and List */
        theUpdateSet = pUpdateSet;
        theUpdateEntry = theUpdateSet.registerType(pItemType);
        setUpdateSet(theUpdateSet);

        /* Set the table model */
        theModel = new StaticModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        final JTable myTable = getTable();
        theColumns = new StaticColumnModel();
        myTable.setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        myTable.getTableHeader().setReorderingAllowed(false);
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        myTable.setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create new button */
        theNewButton = pUtilitySet.getGuiFactory().newScrollButton();
        MetisIcon.configureNewScrollButton(theNewButton);

        /* Create the panel */
        thePanel = new TethysSwingEnablePanel();

        /* Create the layout for the panel */
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(super.getNode().getNode());

        /* Add listeners */
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = theNewButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewClass());
        theNewButton.setMenuConfigurator(e -> buildNewMenu());
        theUpdateSet.getEventRegistrar().addEventListener(e -> theModel.fireNewDataEvents());
        theMenu = theNewButton.getMenu();
    }

    @Override
    public TethysSwingNode getNode() {
        return thePanel.getNode();
    }

    /**
     * Obtain the item type.
     * @return the item type
     */
    protected E getItemType() {
        return theItemType;
    }

    /**
     * Obtain the new button.
     * @return the new Button
     */
    public TethysSwingScrollButtonManager<S> getNewButton() {
        return theNewButton;
    }

    @Override
    protected void setError(final OceanusException pError) {
        theError.addError(pError);
    }

    /**
     * handle new static class.
     */
    private void handleNewClass() {
        /* Access the new class */
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
            incrementVersion();
            theModel.fireNewDataEvents();
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
        theMenu.removeAllItems();

        /* Loop through the missing classes */
        for (S myValue : theStatic.getMissingClasses()) {
            /* Create a new MenuItem and add it to the popUp */
            theMenu.addItem(myValue);
        }
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    protected void determineFocus(final MetisViewerEntry pEntry) {
        /* Request the focus */
        getTable().requestFocusInWindow();

        /* Set the required focus */
        pEntry.setFocus(theUpdateEntry.getName());
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     * @throws OceanusException on error
     */
    protected void refreshData() throws OceanusException {
        /* Access data */
        final DataSet<?, E> myData = theControl.getData();

        /* Access edit list and map it */
        final StaticList<T, S, E> myStatic = myData.getDataList(theClass);
        theStatic = theClass.cast(myStatic.deriveList(ListStyle.EDIT));
        theStatic.mapData();

        /* Update the Data View */
        setList(theStatic);
        theUpdateEntry.setDataList(theStatic);
    }

    /**
     * Select static data.
     * @param pStatic the static data
     */
    protected void selectStatic(final StaticData<?, ?, E> pStatic) {
        /* Find the item in the list */
        int myIndex = theStatic.indexOf(pStatic);
        myIndex = getTable().convertRowIndexToView(myIndex);
        if (myIndex != -1) {
            /* Select the row and ensure that it is visible */
            selectRowWithScroll(myIndex);
        }
    }

    @Override
    public void setShowAll(final boolean pShow) {
        super.setShowAll(pShow);
        theColumns.setColumns();
    }

    /**
     * Is the static table full?
     * @return true/false
     */
    protected boolean isFull() {
        return theStatic == null
               || theStatic.isFull();
    }

    /**
     * Static table model.
     */
    public final class StaticModel
            extends PrometheusDataTableModel<T, E> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6428052539280821038L;

        /**
         * Constructor.
         */
        private StaticModel() {
            /* call constructor */
            super(PrometheusStaticDataTable.this);
        }

        @Override
        public T getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theStatic.get(pRowIndex);
        }

        /**
         * Get the number of display columns.
         * @return the columns
         */
        @Override
        public int getColumnCount() {
            return (theColumns == null)
                                        ? 0
                                        : theColumns.getDeclaredCount();
        }

        /**
         * Get the number of rows in the current table.
         * @return the number of rows
         */
        @Override
        public int getRowCount() {
            return (theStatic == null)
                                       ? 0
                                       : theStatic.size();
        }

        @Override
        public String getColumnName(final int pColIndex) {
            /* Obtain the column name */
            return theColumns.getColumnName(pColIndex);
        }

        @Override
        public MetisField getFieldForCell(final T pItem,
                                          final int pColIndex) {
            /* Obtain the column field */
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final T pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public Object getItemValue(final T pItem,
                                   final int pColIndex) {
            /* Obtain the item value for the column */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final T pItem,
                                 final int pColIndex,
                                 final Object pValue) throws OceanusException {
            /* Set the item value for the column */
            theColumns.setItemValue(pItem, pColIndex, pValue);
        }

        @Override
        public boolean includeRow(final T pRow) {
            /* Ignore deleted rows */
            if (pRow.isDeleted()) {
                return false;
            }

            /* Handle filter */
            return showAll() || !pRow.isDisabled();
        }
    }

    /**
     * Column Model class.
     */
    private final class StaticColumnModel
            extends PrometheusDataTableColumnModel<E> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 676363206266447113L;

        /**
         * Class column id.
         */
        private static final int COLUMN_CLASS = 0;

        /**
         * Name column id.
         */
        private static final int COLUMN_NAME = 1;

        /**
         * Description column id.
         */
        private static final int COLUMN_DESC = 2;

        /**
         * Enabled column id.
         */
        private static final int COLUMN_ENABLED = 3;

        /**
         * Active column id.
         */
        private static final int COLUMN_ACTIVE = 4;

        /**
         * Class column width.
         */
        private static final int WIDTH_CLASS = 90;

        /**
         * Enabled column.
         */
        private final PrometheusDataTableColumn theEnabledColumn;

        /**
         * Constructor.
         */
        private StaticColumnModel() {
            /* call constructor */
            super(PrometheusStaticDataTable.this);

            /* Create the relevant renderers/editors */
            final MetisFieldStringCellEditor myStringEditor = theFieldMgr.allocateStringCellEditor();
            final MetisFieldStringCellRenderer myStringRenderer = theFieldMgr.allocateStringCellRenderer();
            final MetisFieldIconButtonCellEditor<MetisAction> myStatusIconEditor = theFieldMgr.allocateIconButtonCellEditor(MetisAction.class);
            final MetisFieldIconButtonCellRenderer<MetisAction> myStatusIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(MetisAction.class);
            final MetisFieldIconButtonCellEditor<Boolean> myEnabledIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class);
            final MetisFieldIconButtonCellRenderer<Boolean> myEnabledIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(Boolean.class);

            /* Configure the iconButtons */
            final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
            myStatusIconRenderer.setIconMapSet(r -> myActionMapSet);
            myStatusIconEditor.setIconMapSet(r -> myActionMapSet);

            /* Configure the enabled iconButtons */
            final TethysIconMapSet<Boolean> myEnabledMapSet = PrometheusIcon.configureEnabledIconButton();
            myEnabledIconRenderer.setIconMapSet(r -> myEnabledMapSet);
            myEnabledIconEditor.setIconMapSet(r -> myEnabledMapSet);

            /* Create and declare the columns */
            declareColumn(new PrometheusDataTableColumn(COLUMN_CLASS, WIDTH_CLASS, myStringRenderer, myStringEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_NAME, WIDTH_NAME, myStringRenderer, myStringEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_DESC, WIDTH_DESC, myStringRenderer, myStringEditor));
            theEnabledColumn = new PrometheusDataTableColumn(COLUMN_ENABLED, WIDTH_ICON, myEnabledIconRenderer, myEnabledIconEditor);
            declareColumn(theEnabledColumn);
            declareColumn(new PrometheusDataTableColumn(COLUMN_ACTIVE, WIDTH_ICON, myStatusIconRenderer, myStatusIconEditor));

            /* Initialise the columns display */
            setColumns();
        }

        /**
         * Set visible columns according to the mode.
         */
        private void setColumns() {
            /* Switch on mode */
            if (showAll()) {
                revealColumn(theEnabledColumn);
            } else {
                hideColumn(theEnabledColumn);
            }
        }

        /**
         * Obtain column name.
         * @param pColIndex the column index
         * @return the column name
         */
        private String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_CLASS:
                    return TITLE_CLASS;
                case COLUMN_NAME:
                    return TITLE_NAME;
                case COLUMN_DESC:
                    return TITLE_DESC;
                case COLUMN_ENABLED:
                    return TITLE_ENABLED;
                case COLUMN_ACTIVE:
                    return TITLE_ACTIVE;
                default:
                    return null;
            }
        }

        /**
         * Obtain the value for the item column.
         * @param pItem the item
         * @param pColIndex column index
         * @return the value
         */
        private Object getItemValue(final T pItem,
                                    final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_CLASS:
                    return pItem.getStaticClass().toString();
                case COLUMN_NAME:
                    return pItem.getName();
                case COLUMN_DESC:
                    return pItem.getDesc();
                case COLUMN_ENABLED:
                    return pItem.getEnabled();
                case COLUMN_ACTIVE:
                    return pItem.isActive()
                                            ? MetisAction.ACTIVE
                                            : MetisAction.DELETE;
                default:
                    return null;
            }
        }

        /**
         * Set the value for the item column.
         * @param pItem the item
         * @param pColIndex column index
         * @param pValue the value to set
         * @throws OceanusException on error
         */
        private void setItemValue(final T pItem,
                                  final int pColIndex,
                                  final Object pValue) throws OceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    pItem.setName((String) pValue);
                    break;
                case COLUMN_DESC:
                    pItem.setDescription((String) pValue);
                    break;
                case COLUMN_ENABLED:
                    pItem.setEnabled((Boolean) pValue);
                    break;
                case COLUMN_ACTIVE:
                    pItem.setDeleted(true);
                    break;
                default:
                    break;
            }
        }

        /**
         * Is the cell editable?
         * @param pItem the item
         * @param pColIndex the column index
         * @return true/false
         */
        private boolean isCellEditable(final T pItem,
                                       final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                case COLUMN_DESC:
                    return true;
                case COLUMN_ACTIVE:
                case COLUMN_ENABLED:
                    return !pItem.isActive();
                default:
                    return false;
            }
        }

        /**
         * Obtain the field for the column index.
         * @param pColIndex column index
         * @return the field
         */
        private MetisField getFieldForCell(final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_CLASS:
                    return StaticData.FIELD_CLASS;
                case COLUMN_NAME:
                    return StaticData.FIELD_NAME;
                case COLUMN_DESC:
                    return StaticData.FIELD_DESC;
                case COLUMN_ENABLED:
                    return StaticData.FIELD_ENABLED;
                case COLUMN_ACTIVE:
                    return StaticData.FIELD_TOUCH;
                default:
                    return null;
            }
        }
    }
}
