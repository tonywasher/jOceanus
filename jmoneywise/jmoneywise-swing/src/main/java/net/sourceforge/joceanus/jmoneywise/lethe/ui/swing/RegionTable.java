/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldIconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldStringCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldIconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldStringCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region.RegionList;
import net.sourceforge.joceanus.jmoneywise.lethe.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.RegionPanel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTable;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableColumn;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableColumn.PrometheusDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableSelection;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingButton;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Region Table.
 */
public class RegionTable
        extends PrometheusDataTable<Region, MoneyWiseDataType> {
    /**
     * Name Column Title.
     */
    private static final String TITLE_NAME = Region.FIELD_NAME.getName();

    /**
     * Description Column Title.
     */
    private static final String TITLE_DESC = Region.FIELD_DESC.getName();

    /**
     * Active Column Title.
     */
    private static final String TITLE_ACTIVE = PrometheusUIResource.STATIC_TITLE_ACTIVE.getValue();

    /**
     * The data view.
     */
    private final SwingView theView;

    /**
     * The field manager.
     */
    private final MetisSwingFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The data entry.
     */
    private final UpdateEntry<Region, MoneyWiseDataType> theRegionEntry;

    /**
     * The error panel.
     */
    private final MetisErrorPanel<JComponent, Icon> theError;

    /**
     * The Table Model.
     */
    private final RegionTableModel theModel;

    /**
     * The Column Model.
     */
    private final RegionColumnModel theColumns;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * The filter panel.
     */
    private final TethysSwingBoxPaneManager theFilterPanel;

    /**
     * The new button.
     */
    private final TethysSwingButton theNewButton;

    /**
     * The Region dialog.
     */
    private final RegionPanel theActiveRegion;

    /**
     * The List Selection Model.
     */
    private final PrometheusDataTableSelection<Region, MoneyWiseDataType> theSelectionModel;

    /**
     * Regions.
     */
    private RegionList theRegions;

    /**
     * Constructor.
     * @param pView the data view
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public RegionTable(final SwingView pView,
                       final UpdateSet<MoneyWiseDataType> pUpdateSet,
                       final MetisErrorPanel<JComponent, Icon> pError) {
        /* initialise the underlying class */
        super(pView.getGuiFactory());

        /* Record the passed details */
        theView = pView;
        theError = pError;
        theFieldMgr = theView.getFieldManager();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entries */
        theUpdateSet = pUpdateSet;
        theRegionEntry = theUpdateSet.registerType(MoneyWiseDataType.REGION);
        setUpdateSet(theUpdateSet);

        /* Create the table model */
        theModel = new RegionTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        final JTable myTable = getTable();
        theColumns = new RegionColumnModel(this);
        myTable.setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        myTable.getTableHeader().setReorderingAllowed(false);
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        myTable.setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the layout for the panel */
        thePanel = new TethysSwingEnablePanel();
        thePanel.setLayout(new BorderLayout());
        thePanel.add(super.getNode(), BorderLayout.CENTER);

        /* Create new button */
        final TethysSwingGuiFactory myFactory = pView.getGuiFactory();
        theNewButton = myFactory.newButton();
        MetisIcon.configureNewIconButton(theNewButton);

        /* Create a region panel */
        theActiveRegion = new RegionPanel(myFactory, theFieldMgr, theUpdateSet, theError);
        thePanel.add(theActiveRegion.getNode(), BorderLayout.PAGE_END);

        /* Create a dummy filter panel */
        theFilterPanel = myFactory.newHBoxPane();
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(theNewButton);

        /* Create the selection model */
        theSelectionModel = new PrometheusDataTableSelection<>(this, theActiveRegion);

        /* Create listener */
        theUpdateSet.getEventRegistrar().addEventListener(e -> handleRewind());
        theActiveRegion.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
        theActiveRegion.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, this::cascadeEvent);

        /* Listen to swing events */
        theNewButton.getEventRegistrar().addEventListener(e -> theModel.addNewItem());
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    /**
     * Obtain the filter panel.
     * @return the filter panel
     */
    protected TethysSwingBoxPaneManager getFilterPanel() {
        return theFilterPanel;
    }

    /**
     * Are we in the middle of an item edit?
     * @return true/false
     */
    protected boolean isItemEditing() {
        return theActiveRegion.isEditing();
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    protected void determineFocus(final MetisViewerEntry pEntry) {
        /* Request the focus */
        getTable().requestFocusInWindow();

        /* Set the required focus */
        pEntry.setFocus(theRegionEntry.getName());
    }

    /**
     * Refresh data.
     */
    protected void refreshData() {
        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Regions");

        /* Get the Events edit list */
        final MoneyWiseData myData = theView.getData();
        final RegionList myRegions = myData.getRegions();
        theRegions = myRegions.deriveEditList();
        theRegionEntry.setDataList(theRegions);

        /* Notify panel of refresh */
        theActiveRegion.refreshData();

        /* Notify of the change */
        setList(theRegions);

        /* Complete the task */
        myTask.end();
    }

    @Override
    protected void setError(final OceanusException pError) {
        theError.addError(pError);
    }

    @Override
    public boolean hasUpdates() {
        return theUpdateSet.hasUpdates();
    }

    @Override
    public boolean hasSession() {
        return hasUpdates() || isItemEditing();
    }

    @Override
    public boolean hasErrors() {
        return theUpdateSet.hasErrors();
    }

    @Override
    public void cancelEditing() {
        /* Cancel editing on table */
        super.cancelEditing();

        /* Stop editing any item */
        theActiveRegion.setEditable(false);
    }

    /**
     * Select region.
     * @param pRegion the region to select
     */
    protected void selectRegion(final Region pRegion) {
        /* Find the item in the list */
        final int myIndex = theRegions.indexOf(pRegion);
        if (myIndex != -1) {
            /* Select the row and ensure that it is visible */
            selectRowWithScroll(myIndex);
        }
    }

    @Override
    protected void notifyChanges() {
        /* Adjust enable of the table */
        setEnabled(!theActiveRegion.isEditing());

        /* Pass call on */
        super.notifyChanges();
    }

    /**
     * Handle updateSet rewind.
     */
    private void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveRegion.isEditing()) {
            /* Handle the reWind */
            theSelectionModel.handleReWind();
        }

        /* Adjust for changes */
        notifyChanges();
    }

    /**
     * Handle panel state.
     */
    private void handlePanelState() {
        /* Only action if we are not editing */
        if (!theActiveRegion.isEditing()) {
            /* handle the edit transition */
            theSelectionModel.handleEditTransition();
        }

        /* Note changes */
        notifyChanges();
    }

    /**
     * JTable Data Model.
     */
    private final class RegionTableModel
            extends PrometheusDataTableModel<Region, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = 7467173113883256744L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private RegionTableModel(final RegionTable pTable) {
            /* call constructor */
            super(pTable);
        }

        @Override
        public int getColumnCount() {
            return (theColumns == null)
                                        ? 0
                                        : theColumns.getDeclaredCount();
        }

        @Override
        public int getRowCount() {
            return (theRegions == null)
                                        ? 0
                                        : theRegions.size();
        }

        @Override
        public MetisField getFieldForCell(final Region pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final Region pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public Region getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theRegions.get(pRowIndex);
        }

        @Override
        public Object getItemValue(final Region pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final Region pItem,
                                 final int pColIndex,
                                 final Object pValue) throws OceanusException {
            /* Set the item value for the column */
            theColumns.setItemValue(pItem, pColIndex, pValue);
        }

        @Override
        public String getColumnName(final int pColIndex) {
            /* Obtain the column name */
            return theColumns.getColumnName(pColIndex);
        }

        @Override
        public boolean includeRow(final Region pRow) {
            /* Ignore deleted rows */
            return !pRow.isDeleted();
        }

        /**
         * New item.
         */
        private void addNewItem() {
            /* Protect against Exceptions */
            try {
                /* Create the new region */
                final Region myRegion = new Region(theRegions);
                myRegion.setDefaults();

                /* Add the new item */
                myRegion.setNewVersion();
                theRegions.add(myRegion);

                /* Validate the new item and notify of the changes */
                myRegion.validate();
                incrementVersion();

                /* Lock the table */
                setEnabled(false);
                theActiveRegion.setNewItem(myRegion);

                /* Handle Exceptions */
            } catch (OceanusException e) {
                /* Build the error */
                final OceanusException myError = new MoneyWiseDataException("Failed to create new region", e);

                /* Show the error */
                setError(myError);
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class RegionColumnModel
            extends PrometheusDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -3705743928957038842L;

        /**
         * Name column id.
         */
        private static final int COLUMN_NAME = 0;

        /**
         * Description column id.
         */
        private static final int COLUMN_DESC = 1;

        /**
         * Active column id.
         */
        private static final int COLUMN_ACTIVE = 2;

        /**
         * Constructor.
         * @param pTable the table
         */
        private RegionColumnModel(final RegionTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            final MetisFieldIconButtonCellEditor<MetisAction> myIconEditor = theFieldMgr.allocateIconButtonCellEditor(MetisAction.class);
            final MetisFieldStringCellEditor myStringEditor = theFieldMgr.allocateStringCellEditor();
            final MetisFieldIconButtonCellRenderer<MetisAction> myIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(MetisAction.class);
            final MetisFieldStringCellRenderer myStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Configure the iconButton */
            final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
            myIconRenderer.setIconMapSet(r -> myActionMapSet);
            myIconEditor.setIconMapSet(r -> myActionMapSet);

            /* Create the columns */
            declareColumn(new PrometheusDataTableColumn(COLUMN_NAME, WIDTH_NAME, myStringRenderer, myStringEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_DESC, WIDTH_DESC, myStringRenderer, myStringEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_ACTIVE, WIDTH_ICON, myIconRenderer, myIconEditor));
        }

        /**
         * Obtain column name.
         * @param pColIndex the column index
         * @return the column name
         */
        private String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                    return TITLE_NAME;
                case COLUMN_DESC:
                    return TITLE_DESC;
                case COLUMN_ACTIVE:
                    return TITLE_ACTIVE;
                default:
                    return null;
            }
        }

        /**
         * Obtain the value for the region column.
         * @param pRegion region
         * @param pColIndex column index
         * @return the value
         */
        protected Object getItemValue(final Region pRegion,
                                      final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    return pRegion.getName();
                case COLUMN_DESC:
                    return pRegion.getDesc();
                case COLUMN_ACTIVE:
                    return pRegion.isActive()
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
        private void setItemValue(final Region pItem,
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
        private boolean isCellEditable(final Region pItem,
                                       final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                case COLUMN_DESC:
                    return true;
                case COLUMN_ACTIVE:
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
        protected MetisField getFieldForCell(final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_NAME:
                    return Region.FIELD_NAME;
                case COLUMN_DESC:
                    return Region.FIELD_DESC;
                case COLUMN_ACTIVE:
                    return Region.FIELD_TOUCH;
                default:
                    return null;
            }
        }
    }
}
