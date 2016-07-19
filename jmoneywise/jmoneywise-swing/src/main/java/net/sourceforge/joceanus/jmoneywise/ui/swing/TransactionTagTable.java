/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag.TransactionTagList;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseErrorPanel;
import net.sourceforge.joceanus.jmoneywise.ui.controls.swing.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.swing.TransactionTagPanel;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcon;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableSelection;
import net.sourceforge.joceanus.jprometheus.ui.swing.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingButton;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * TransactionTag Table.
 */
public class TransactionTagTable
        extends JDataTable<TransactionTag, MoneyWiseDataType> {
    /**
     * Name Column Title.
     */
    private static final String TITLE_NAME = TransactionTag.FIELD_NAME.getName();

    /**
     * Description Column Title.
     */
    private static final String TITLE_DESC = TransactionTag.FIELD_DESC.getName();

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
    private final MetisFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The data entry.
     */
    private final UpdateEntry<TransactionTag, MoneyWiseDataType> theTransactionTagEntry;

    /**
     * The error panel.
     */
    private final MoneyWiseErrorPanel<JComponent, Icon> theError;

    /**
     * The Table Model.
     */
    private final TransactionTagTableModel theModel;

    /**
     * The Column Model.
     */
    private final TransactionTagColumnModel theColumns;

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
     * The TransactionTag dialog.
     */
    private final TransactionTagPanel theActiveTag;

    /**
     * The List Selection Model.
     */
    private final JDataTableSelection<TransactionTag, MoneyWiseDataType> theSelectionModel;

    /**
     * TransactionTags.
     */
    private TransactionTagList theTransactionTags;

    /**
     * Constructor.
     * @param pView the data view
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public TransactionTagTable(final SwingView pView,
                               final UpdateSet<MoneyWiseDataType> pUpdateSet,
                               final MoneyWiseErrorPanel<JComponent, Icon> pError) {
        /* initialise the underlying class */
        super(pView.getGuiFactory());

        /* Record the passed details */
        theView = pView;
        theError = pError;
        theFieldMgr = theView.getFieldManager();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entries */
        theUpdateSet = pUpdateSet;
        theTransactionTagEntry = theUpdateSet.registerType(MoneyWiseDataType.TRANSTAG);
        setUpdateSet(theUpdateSet);

        /* Create the table model */
        theModel = new TransactionTagTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        JTable myTable = getTable();
        theColumns = new TransactionTagColumnModel(this);
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
        TethysSwingGuiFactory myFactory = pView.getGuiFactory();
        theNewButton = myFactory.newButton();
        PrometheusIcon.configureNewIconButton(theNewButton);

        /* Create a Tag panel */
        theActiveTag = new TransactionTagPanel(myFactory, theFieldMgr, theUpdateSet, theError);
        thePanel.add(theActiveTag.getNode(), BorderLayout.PAGE_END);

        /* Create a dummy filter panel */
        theFilterPanel = myFactory.newHBoxPane();
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(theNewButton);

        /* Create the selection model */
        theSelectionModel = new JDataTableSelection<>(this, theActiveTag);

        /* Create listener */
        theUpdateSet.getEventRegistrar().addEventListener(e -> handleRewind());
        theActiveTag.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
        theActiveTag.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, this::cascadeEvent);

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
        return theActiveTag.isEditing();
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    protected void determineFocus(final MetisViewerEntry pEntry) {
        /* Request the focus */
        getTable().requestFocusInWindow();

        /* Set the required focus */
        pEntry.setFocus(theTransactionTagEntry.getName());
    }

    /**
     * Refresh data.
     */
    protected void refreshData() {
        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Tags");

        /* Get the Events edit list */
        MoneyWiseData myData = theView.getData();
        TransactionTagList myTransactionTags = myData.getTransactionTags();
        theTransactionTags = myTransactionTags.deriveEditList();
        theTransactionTagEntry.setDataList(theTransactionTags);

        /* Notify panel of refresh */
        theActiveTag.refreshData();

        /* Notify of the change */
        setList(theTransactionTags);

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
        theActiveTag.setEditable(false);
    }

    /**
     * Select tag.
     * @param pTag the tag to select
     */
    protected void selectTag(final TransactionTag pTag) {
        /* Find the item in the list */
        int myIndex = theTransactionTags.indexOf(pTag);
        if (myIndex != -1) {
            /* Select the row and ensure that it is visible */
            selectRowWithScroll(myIndex);
        }
    }

    @Override
    protected void notifyChanges() {
        /* Adjust enable of the table */
        setEnabled(!theActiveTag.isEditing());

        /* Pass call on */
        super.notifyChanges();
    }

    /**
     * Handle updateSet rewind.
     */
    private void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveTag.isEditing()) {
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
        if (!theActiveTag.isEditing()) {
            /* handle the edit transition */
            theSelectionModel.handleEditTransition();
        }

        /* Note changes */
        notifyChanges();
    }

    /**
     * JTable Data Model.
     */
    private final class TransactionTagTableModel
            extends JDataTableModel<TransactionTag, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = -7851544627310851259L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private TransactionTagTableModel(final TransactionTagTable pTable) {
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
            return (theTransactionTags == null)
                                                ? 0
                                                : theTransactionTags.size();
        }

        @Override
        public MetisField getFieldForCell(final TransactionTag pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final TransactionTag pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public TransactionTag getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theTransactionTags.get(pRowIndex);
        }

        @Override
        public Object getItemValue(final TransactionTag pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final TransactionTag pItem,
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
        public boolean includeRow(final TransactionTag pRow) {
            /* Ignore deleted rows */
            if (pRow.isDeleted()) {
                return false;
            }

            /* Handle filter */
            return true;
        }

        /**
         * New item.
         */
        private void addNewItem() {
            /* Protect against Exceptions */
            try {
                /* Create the new tag */
                TransactionTag myTag = new TransactionTag(theTransactionTags);
                myTag.setDefaults();

                /* Add the new item */
                myTag.setNewVersion();
                theTransactionTags.append(myTag);

                /* Validate the new item and notify of the changes */
                myTag.validate();
                incrementVersion();

                /* Lock the table */
                setEnabled(false);
                theActiveTag.setNewItem(myTag);

                /* Handle Exceptions */
            } catch (OceanusException e) {
                /* Build the error */
                OceanusException myError = new MoneyWiseDataException("Failed to create new tag", e);

                /* Show the error */
                setError(myError);
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class TransactionTagColumnModel
            extends JDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2621702912812861337L;

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
         * Icon Renderer.
         */
        private final IconButtonCellRenderer<ActionType> theIconRenderer;

        /**
         * Icon editor.
         */
        private final IconButtonCellEditor<ActionType> theIconEditor;

        /**
         * String Renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * String Editor.
         */
        private final StringCellEditor theStringEditor;

        /**
         * Constructor.
         * @param pTable the table
         */
        private TransactionTagColumnModel(final TransactionTagTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theIconEditor = theFieldMgr.allocateIconButtonCellEditor(ActionType.class, false);
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theIconEditor);
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Configure the iconButton */
            MoneyWiseIcons.buildStatusButton(theIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_NAME, WIDTH_NAME, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_ACTIVE, WIDTH_ICON, theIconRenderer, theIconEditor));
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
         * Obtain the value for the TransactionTag column.
         * @param pTransactionTag TransactionTag
         * @param pColIndex column index
         * @return the value
         */
        protected Object getItemValue(final TransactionTag pTransactionTag,
                                      final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    return pTransactionTag.getName();
                case COLUMN_DESC:
                    return pTransactionTag.getDesc();
                case COLUMN_ACTIVE:
                    return pTransactionTag.isActive()
                                                      ? ActionType.ACTIVE
                                                      : ActionType.DELETE;
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
        private void setItemValue(final TransactionTag pItem,
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
        private boolean isCellEditable(final TransactionTag pItem,
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
                    return TransactionTag.FIELD_NAME;
                case COLUMN_DESC:
                    return TransactionTag.FIELD_DESC;
                case COLUMN_ACTIVE:
                    return TransactionTag.FIELD_TOUCH;
                default:
                    return null;
            }
        }
    }
}
